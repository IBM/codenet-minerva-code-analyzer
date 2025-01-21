package orderstatus.concurrent.ejb.lite;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;

import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.concurrent.ManagedTaskListener;
import javax.naming.InitialContext;

import irwwbase.IRWWBase;

/**
 *
 * The OrderStatusTaskListener is POJO object that listens for task events that
 * the JEE7 concurrency 1.0 service fires, based on the state changes of our
 * OrderStatusTask objects running in the server.  We only use one Listener instance
 * and share it with all the running task instances so the Listener must be thread-safe.
 * 
 * The Listener is informed by the concurrency service about taskSubmitted,
 * taskStarting, taskAborted, and taskDone state transitions for each task
 * instance we have running in the server.  Our task objects each have a 
 * unique client Id assigned so that allows the listener to understand which
 * particular task an event is for. 
 *  
 * The job of the listener is to report statistics and exception information
 * to the controller as the order status tasks run, over and over again, in the
 * concurrency service.  
 */


public class OrderStatusTaskListener implements ManagedTaskListener {
	
	private OrderStatusConcurrencyControllerBean controllerBean = null;
	private IRWWBase ib = null;

	public OrderStatusTaskListener() {
		super();
		
		try {

			// lookup the order status concurrency controller singleton bean in the app name space.
			InitialContext ic;
			ic = new InitialContext();
			controllerBean = (OrderStatusConcurrencyControllerBean) ic.lookup("java:app/OrderStatusConcurrentEJBLite/OrderStatusConcurrencyControllerBean");
			
			ib = new IRWWBase();

		} catch (Throwable t) {
			System.out.println("OrderStatusTaskListener ctor() caught throwable: " + t);
			t.printStackTrace();
		}

	}

	public void taskAborted(Future<?> taskFuture, ManagedExecutorService mes,
			Object myTask, Throwable exception) {

			
		debugOut("Client #" + ((OrderStatusTask)myTask).clientId + " was aborted");

		if (exception != null) {
			debugOut("Exception passed to abort is: " + exception);
		}
	
		// Note: If we get a CancellationException in abort, that is "normal" for
		//       the case where the user asked us to quiesce the currently running
		//       work.  We don't need to do anything in this case.  Other exceptions,
		//       however, are not expected so we do tell the controller about them.
		if (exception instanceof CancellationException) {
			
			// normal operation - stopping work in progress
			
		} else {
			
			controllerBean.setException(exception.getMessage());
			controllerBean.setStack(ib.getStackTrace(exception));
			
		}
		
	}

	public void taskDone(Future<?> taskFuture, ManagedExecutorService mes,
			Object myTask, Throwable exception) {
		
		try{
			debugOut ("Enter taskDone() for clientId: " + ((OrderStatusTask)myTask).clientId); 

			if(taskFuture.isCancelled()){
				
				// Cancel was called, but the task may have finished successfully if it was nearly
				// done at the time of the cancel attempt.  If so we have a successful transaction.
				if (((OrderStatusTask)myTask).getResult() == Boolean.TRUE) {
					debugOut("Client #" + ((OrderStatusTask)myTask).clientId + " was asked to cancel, but completed the current transaction successfully");	
					controllerBean.addPassedTransactions();					
				} else {
					debugOut("Client #" + ((OrderStatusTask)myTask).clientId + " was cancelled");	
					controllerBean.addCancelledTransactions();
				}			
			}
			else if (exception != null) {

				// Need to decipher between expected and not expected exceptions
				// SQL exception -913 such as optimistic lock errors can be
				// tolerated and should not fail the test run.
				debugOut("Exception passed on done is: " + exception.toString());	
				controllerBean.setException(exception.toString());
				if(exception.getCause() != null)
					controllerBean.setStack(ib.getStackTrace(exception.getCause()));
				else
					controllerBean.setStack(ib.getStackTrace(exception));
					

				if (ib.is913(exception)) {
					controllerBean.addExpectedFailedTransactions();
				} else {
					controllerBean.addFailedTransactions();
				}
				
				// If all the clients have failed, the run is really over at this point so call stopWork.
				// Note that we check ">=" below just in case some race condition might let us count
				// an extra error.  Normally, we expect the count to be equal when all clients have failed.
				if((controllerBean.getExpectedFailed() + controllerBean.getUnexpectedFailed()) >= controllerBean.getNumberOfClients())
					controllerBean.stopWork();
			}		
			else{
				debugOut("Client #" + ((OrderStatusTask)myTask).clientId + " is done");
				controllerBean.addPassedTransactions();
			}

		} catch (Throwable t) {
			// We really should never cause a new exception in taskDone processing, but if we do
			// go ahead and tell the controller about it since it is unexpected.
			System.out.println("OrderStatusTaskListener.taskDone() caught throwable: " + t);
			t.printStackTrace();
			controllerBean.setException(t.getMessage());
			controllerBean.setStack(ib.getStackTrace(t));
			controllerBean.addFailedTransactions();

		} finally {
			debugOut ("Exit taskDone()"); 
		}
	}

	public void taskStarting(Future<?> taskFuture, ManagedExecutorService mes,
			Object myTask) {

		debugOut("Client #" + ((OrderStatusTask)myTask).clientId + " is starting");		

	}

	public void taskSubmitted(Future<?> taskFuture, ManagedExecutorService mes,
			Object myTask) {
		try{

			debugOut("Client #" + ((OrderStatusTask)myTask).clientId + " is submitted");
			
			// re-initialize the task result to failed
			((OrderStatusTask)myTask).setResult(Boolean.FALSE);
			
			// increment attempted transaction count
			controllerBean.addAttemptedTransactions();

		} catch (Throwable t) {
			
			// We really should never get an exception in taskSubmitted, but if we do
			// go ahead and tell the controller about it since it is unexpected.
			System.out.println("OrderStatusTaskListener.taskSubmitted() caught throwable: " + t);
			t.printStackTrace();
			controllerBean.setException(t.getMessage());
			controllerBean.setStack(ib.getStackTrace(t));
		}	
	}

	public void debugOut(String message) {

		if (System.getProperty("DEBUG_OUT") != null) {
			if (System.getProperty("DEBUG_OUT").equals("ON")) {
				long tid = Thread.currentThread().getId();
				System.out.println("concurrentOS_thread_" + tid + ": " + message);
			}
		}
	}



}
