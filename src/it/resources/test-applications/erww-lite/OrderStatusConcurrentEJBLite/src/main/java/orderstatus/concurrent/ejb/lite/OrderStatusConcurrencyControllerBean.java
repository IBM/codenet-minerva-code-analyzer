package orderstatus.concurrent.ejb.lite;

import irwwbase.ExtendedRandom;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import jakarta.ejb.EJBException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.naming.InitialContext;


/**
 * Session Bean implementation class OrderStatusConcurrencyConcontrollerBean.
 * 
 * This bean is a singleton EJB "controller" that lets the ERWW application submit large
 * numbers of order status transactions as tasks to the JEE7 concurrency 1.0 service.
 * The work is submitted to a ManagedScheduledExecutor in the concurrency service that
 * lets the transactions be run over and over again with an initial delay time, and 
 * a second delay between each subsequent transaction.
 *  
 * The key functions the controller provides are startWork, stopWork, and getStatus.  
 * Only one set of tasks may be running at a time.  If a call to startWork 
 * happens while work is already active, it will be rejected.
 * 
 * The controller keeps a number of statistics for all the tasks running in the
 * server process which are then feed back to the user when status is requested
 * about the test run.
 * 
 * Note that the controller is a singleton and is shared by all the running tasks.
 * This means that we need to be careful that it is thread-safe.  In order to allow
 * the best performance we have chosen to make the methods only have READ locking.
 * This means we rely on the data structures to be thread-safe because the methods
 * themselves are not.  This is why AtomicIntegers are used for counters.    
 */
@Singleton
@LocalBean
@Lock(LockType.READ)
public class OrderStatusConcurrencyControllerBean {

	// The list of future objects for each order status client task, indexed by clientId
	private ArrayList<ScheduledFuture<?>> orderStatusTaskFutureList = null;

	// The future object for the test run duration timer task
	private ScheduledFuture<?> testDurationTimerTaskFuture = null;

	// The test run duration (in minutes) originally requested
	private int testRunDuration = 0;

	// The number of clients originally requested
	private int numberOfClients = 0;

	// The time (in seconds) that the work was stopped
	private int stopTime = 0;

	// The message to be displayed by status page
	private String message = "";

	// The exception to be displayed by the status page
	private String exception = "";
	
	// The stack from the exception
	private String stack = "";

	// The transaction counts
	private AtomicInteger attemptedTransactions = new AtomicInteger();
	private AtomicInteger passedTransactions = new AtomicInteger();
	private AtomicInteger failedTransactions = new AtomicInteger();
	private AtomicInteger expectedFailedTransactions = new AtomicInteger();
	private AtomicInteger cancelledTransactions = new AtomicInteger();
	
	// The flag used to decide whether new work can be started or not
	private boolean isWorkAlreadyRunning = false;

	/**
	 * Default constructor. 
	 */
	public OrderStatusConcurrencyControllerBean() {

	}

	public OrderStatusConcurrentOutput startWork(OrderStatusConcurrentInput input) throws EJBException {

		OrderStatusConcurrentOutput output = null;
		
		// One listener instance is shared by all tasks
		OrderStatusTaskListener orderStatusTaskListener = new OrderStatusTaskListener();


		try {

			if(!isWorkAlreadyRunning){
				
				debugOut("Enter OrderStatusConcurrencyControllerBean.startWork(), Starting " + numberOfClients + " clients for a test run of " + testRunDuration + " minutes.");

				isWorkAlreadyRunning = true;
				
				attemptedTransactions.getAndSet(0);
				passedTransactions.getAndSet(0);
				failedTransactions.getAndSet(0);
				expectedFailedTransactions.getAndSet(0);
				cancelledTransactions.getAndSet(0);				
				
				message = "";
				exception = "";
				stack = "";

				//Save away the test run duration and number of clients
				testRunDuration = input.getTestRunDuration();
				numberOfClients = input.getNumberOfClients();

				// List to store the future object returned for each client task
				// Note that arraylist position 0 has the future object for clientId #1
				// The array list is indexed from 0 to 1 less than the number of clients.
				orderStatusTaskFutureList = new ArrayList<ScheduledFuture<?>>();   		

				// Lookup the ERWW's managed scheduled executor
				InitialContext ic = new InitialContext();
				ManagedScheduledExecutorService mses = (ManagedScheduledExecutorService) ic.lookup("concurrent/ERWWManagedScheduledExecutor");

				// Schedule the order status work task for each client, as needed.
				for (int clientId = 1; clientId <= numberOfClients; clientId++) {

					// Create new managed task, and plug in my task listener	
					OrderStatusTask orderStatusTask = new OrderStatusTask(clientId, orderStatusTaskListener);   		   

					// Space out each client from the next, by the start delay time interval
					int initialStartDelayTime = clientId * input.getClientStartupInterval();   		 

					// Random transaction wait time, picked between min and max input values, in milliseconds
					// Note that maximum time input is in seconds so we convert to milliseconds below.

					ExtendedRandom rand = new ExtendedRandom();
					int transactionDelayTime = rand.nextInt(input.getMinimumClientTransactionWaitTime(), input.getMaximumClientTransactionWaitTime());
					debugOut("Randomized transaction delay time in milliseconds is: " + transactionDelayTime);

					OrderStatusTaskTrigger orderStatusTrigger = new OrderStatusTaskTrigger(initialStartDelayTime, transactionDelayTime);

					ScheduledFuture<?> taskFuture = mses.schedule(orderStatusTask, orderStatusTrigger);
					orderStatusTaskFutureList.add(taskFuture);
				}

				// Schedule a single task to wake us up when the test run time has expired
				TestDurationTimerTask testRunTimerTask = new TestDurationTimerTask();
				testDurationTimerTaskFuture = mses.schedule(testRunTimerTask, input.getTestRunDuration(), TimeUnit.MINUTES);

				// If we get this far without exceptions, we are successful.
				output = new OrderStatusConcurrentOutput();
				output.setStatus("SUCCESSFUL");
				output.setMessage("Your concurrent work has been scheduled and will start soon.");

			}
			else{
				output = new OrderStatusConcurrentOutput();
				output.setStatus("FAILED");
				output.setMessage("There is already work running. Please quiesce the current work or wait for it to finish.");
			} 
		}
		catch (Throwable t) {

			debugOut("OrderStatusConcurrencyControllerBean.startWork() caught exception: " + t + ".  The work may not have been scheduled properly.");
			output = new OrderStatusConcurrentOutput();
			output.setStatus("FAILED");
			output.setMessage("There was a problem scheduling your concurrent work.  Please check the log files.");
			t.printStackTrace();

		} finally {  

			debugOut("Exiting OrderStatusConcurrencyControllerBean.startWork()");

		}

		return output;
	}

	public void stopWork () throws EJBException {


		debugOut("Enter OrderStatusConcurrencyControllerBean.stopWork().  All active work will be cancelled.");

		try {

			// Check if stopWork called before any work started or threads already stopped
			if (numberOfClients > 0  && !testDurationTimerTaskFuture.isDone()) { 

				debugOut("stopWork(): Cancelling future list");
				
				isWorkAlreadyRunning = false;

				for (int taskFuture = 0; taskFuture < numberOfClients; taskFuture++) {
					((ScheduledFuture<?>)orderStatusTaskFutureList.get(taskFuture)).cancel(true);				
				}

				// Check to see why stop was called
				if(testDurationTimerTaskFuture.getDelay(TimeUnit.SECONDS) > 0){ 
					stopTime = testRunDuration*60 - (int) testDurationTimerTaskFuture.getDelay(TimeUnit.SECONDS);
					debugOut("stopWork(): Quiesce was pressed at: " + stopTime + " seconds");

					testDurationTimerTaskFuture.cancel(true);
				}
				else if(!testDurationTimerTaskFuture.isCancelled()){
					stopTime = testRunDuration*60;
					debugOut("stopWork(): Timer task completed normally, stopTime is: " + stopTime + " seconds");
				}
			}

			else{
				debugOut("stopWork(): Either the work was cancelled already or stopWork was called before work started");
				message = "There is no remaining work to be quiesced.";
			}

		} catch (Throwable t) {
			
			System.out.println("OrderStatusConcurrencyControllerBean.stopWork() caught throwable: " + t);
			message = "There was a problem stopping your concurrent work.  Please check the log files.";
			exception = t.toString();
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			if (t!=null) t.printStackTrace(printWriter);
			stack = result.toString();
			
			t.printStackTrace();

		} finally {

			debugOut ("Exiting  OrderStatusConcurrencyControllerBean.stopWork()"); 
		}

	}

	public ConcurrencyStatusOutput getStatus () throws EJBException {

		ConcurrencyStatusOutput outputPage = new ConcurrencyStatusOutput();
		String[] msgs = new String[2];

		try{
			if (numberOfClients > 0){
				long timeElapsed = 0;

				debugOut("Entering OrderStatusConcurrencyControllerBean.getStatus()");

				outputPage.setNumberOfClients(numberOfClients);
				outputPage.setAttemptedTrans(attemptedTransactions.get());
				outputPage.setPassedTrans(passedTransactions.get());
				outputPage.setUnexpectedFailedTrans(failedTransactions.get());
				outputPage.setExpectedFailedTrans(expectedFailedTransactions.get());
				outputPage.setCancelledTrans(cancelledTransactions.get());

				if(!testDurationTimerTaskFuture.isDone()){
					timeElapsed = testRunDuration*60 - testDurationTimerTaskFuture.getDelay(TimeUnit.SECONDS);
					debugOut("getStatus(): timer task did not complete, timeElapsed is: " + timeElapsed + " seconds");
				}
				else{
					debugOut("getStatus(): timer task finished or quiesce was pressed, the stopTime was " + stopTime + " seconds");
					timeElapsed = stopTime;
				}

				outputPage.setTime((int) timeElapsed);

				if(exception == "")
					outputPage.setException("No exceptions occurred");
				else{
					outputPage.setException(exception);
				}
				
				if(stack == "")
					outputPage.setStack("");
				else
					outputPage.setStack(stack);
				
				msgs = getStatusAndMessage();
				outputPage.setStatusMsg(msgs[0]);
				outputPage.setMessage(msgs[1]);
			}
			else{
				outputPage.setStatusMsg("FAILED");
				outputPage.setMessage("There is currently no submitted or running work.");
			}

		} catch (Throwable t) {
			System.out.println("OrderStatusConcurrencyControllerBean.getStatus() caught throwable: " + t);
			t.printStackTrace();
		} finally {
			debugOut ("Exiting OrderStatusConcurrencyControllerBean.getStatus()"); 
		}

		return outputPage;

	}

	public int sumTransaction() {
		int sum = passedTransactions.intValue() + failedTransactions.intValue() 
				+ expectedFailedTransactions.intValue() + cancelledTransactions.intValue();
		return sum;
	}

	public void addAttemptedTransactions(){
		attemptedTransactions.incrementAndGet();
	}

	public void addPassedTransactions(){
		passedTransactions.incrementAndGet();
	}

	public void addFailedTransactions(){
		failedTransactions.incrementAndGet();
	}

	public void addCancelledTransactions(){
		cancelledTransactions.incrementAndGet();
	}

	public void addExpectedFailedTransactions(){
		expectedFailedTransactions.incrementAndGet();
	}

	public void setException(String excpt){
		if(exception=="")
			exception = excpt;
	}
	
	public void setStack(String stck){
		if(stack == "")
			stack = stck;
	}
	
	public boolean isWorkAlreadyRunning() {
		return this.isWorkAlreadyRunning;
	}
	
	public int getNumberOfClients (){
		return this.numberOfClients;
	}
	
	public int getExpectedFailed (){
		return this.expectedFailedTransactions.intValue();
	}
	
	public int getUnexpectedFailed (){
		return this.failedTransactions.intValue();
	}
	
	public void debugOut(String message) {

		if (System.getProperty("DEBUG_OUT") != null) {
			if (System.getProperty("DEBUG_OUT").equals("ON")) {
				long tid = Thread.currentThread().getId();
				System.out.println("concurrentOS_thread_" + tid + ": " + message);
			}
		}
	}
	
	public String[] getStatusAndMessage(){
		String[] msgs = new String[2];
		
		msgs[0] = "SUCCESS";
		msgs[1] = "";
		
		if(message != ""){ //Quiesce was not completed
			msgs[0] = "FAILED";
			msgs[1] = message + " ";
			message = "";
		}
		// We should only count a single failure for each client, but just in case some race condition
		// would make us count an extra error or two, check if ">=" below just to be safe.
		if((failedTransactions.intValue()+expectedFailedTransactions.intValue()) >= numberOfClients){ 
			msgs[1] += "All clients have failed.";
			msgs[0] = "FAILED";
		}
		else if(!isWorkAlreadyRunning){ //Work has stopped
			if(testDurationTimerTaskFuture.isCancelled()){
				if(sumTransaction() == attemptedTransactions.intValue())
					msgs[1] += "Your work has been quiesced.";
				else
					msgs[1] += "Your work is being quiesced.";
			}
			else
				msgs[1] += "Your test has completed.";
				
		}
		else{ //Work is still running
			msgs[1] += "Your work is still running.";
		}
		
		//Append any exception messages needed
		if(failedTransactions.intValue() >0 && expectedFailedTransactions.intValue()>0){
			msgs[1] += " There were both expected and unexpected exceptions during the run.";
			msgs[0] = "FAILED";
		}
		else if(failedTransactions.intValue() > 0){
			msgs[0] = "FAILED";
			msgs[1] += " There were unexpected exceptions during the run.";
		}
		else if(expectedFailedTransactions.intValue() > 0){
			msgs[1] += " There were expected exceptions during the run.";			
		}
		else
			msgs[1] += " There were no exceptions during the run.";
		
		return msgs;
	}
}
