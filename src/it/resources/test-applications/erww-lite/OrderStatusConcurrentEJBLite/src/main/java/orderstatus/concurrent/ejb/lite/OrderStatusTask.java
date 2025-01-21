package orderstatus.concurrent.ejb.lite;

import java.util.concurrent.Callable;
import java.util.Map;

import jakarta.enterprise.concurrent.ManagedTask;
import jakarta.enterprise.concurrent.ManagedTaskListener;
import javax.naming.InitialContext;

public class OrderStatusTask implements Callable<Object>, ManagedTask {
	
	int clientId = 0;
	ManagedTaskListener theListener = null;
	OrderStatusSessionBean orderStatusBean = null;
	Boolean result = Boolean.FALSE;
	
	/**
	 * 
	 * This task runs a single order status transaction each time its call method is
	 * invoked.   The output if each transaction is a boolean (ie. pass/fail).
	 *
	 * This task is a "managed" task which means that it provides a listener 
	 * object which can be used to track its state as it executes in the concurrency
	 * service.
	 * 
	 * Each task instance is uniquely identified from other task instances by a
	 * clientId.
	 * 
	 * Each task instance holds a reference to a stateless session bean which is
	 * used to run order status transactions.
	 * 
	 * The call method returns a boolean (pass/fail) for each transaction it runs.
	 * 
	 */
	
	
	public OrderStatusTask(int inClientId, ManagedTaskListener inListener) throws Exception {
		super();

		if (inClientId > 0) {
			clientId = inClientId;
		} else {
			throw new Exception ("The order status task ctor requires a non-zero, positive integer to be passed in for the client Id!");
		}

		if (inListener != null) {
			theListener = inListener;
		} else {
			throw new Exception ("The order status task ctor requires a ManagedTaskListener to be passed in, but it was null!");
		}

		// Lookup an order status bean reference.  We can hold on to the
		// reference as long at the task exists the EJB container will
		// take care of hooking up a bean to the reference for us as needed.
		try {

			InitialContext ic = new InitialContext();
			orderStatusBean = (OrderStatusSessionBean) ic.lookup("java:app/OrderStatusConcurrentEJBLite/OrderStatusSessionBean");

		} catch (Throwable t) {
			System.out.println("OrderStatusTask ctor caught exception looking up Order Status Session Bean: " + t);
			t.printStackTrace();
			throw t;
		}
		
	}
	
	public Boolean call() throws Exception {
		
		// Assume the order status transaction failed, until proven otherwise.
		result = Boolean.FALSE;
		
		long startTime = System.currentTimeMillis();
		
		try {
			debugOut("Enter OrderStatusTask.call() for client #" + clientId);
			
			// Run the Order status transaction.  Any exception is a failure and
			// should be re-thrown so that the order status listener can handle it.
			if (orderStatusBean != null) {
				
				result = orderStatusBean.orderStatusSession();
				
			} else {
				throw new Exception ("The order status bean reference is null in the order status task!");
			}

		} catch (Throwable t) {
			System.out.println("OrderStatusTask.call() method caught exception: " + t.toString() + " in client #" + clientId + ".  The order status transaction failed.");
			t.printStackTrace();
            throw t;
            
		} finally {

			debugOut("Exiting OrderStatusTask.call() method for client #" + clientId + ". Elapsed time = " + (System.currentTimeMillis()-startTime) + " (milliseconds)");

		}
		
	    return (result);
	}


	// No special execution properties for my Task, so return null
	public Map<String, String> getExecutionProperties() {
		
		return null;
	}


	public ManagedTaskListener getManagedTaskListener() {
		
		return theListener;
	}	

	public void setResult (Boolean inResult){
		result = inResult;
	}
	
	public Boolean getResult() {
		return (result);
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
