package orderstatus.concurrent.ejb.lite;

import javax.naming.InitialContext;

/**
 * 
 * This task provides the timer function to determine when the order status
 * concurrency test run has completed (ie. the overall test run execution time).
 * It is a single shot task.  The run method is called when the test run is 
 * over.  It is used to cancel all the running tasks.
 *
 */

public class TestDurationTimerTask implements java.lang.Runnable {
	
	
	public void run() {
		
		try {
			debugOut ("*************************************************************************************************");
			debugOut ("TestDurationTimerTask.run() invoked.  Test run has finished.  All active tasks will be cancelled.");
			debugOut ("*************************************************************************************************");

			// Lookup the order status controller singleton so that we can stop the work now
			InitialContext ic = new InitialContext();
			OrderStatusConcurrencyControllerBean controllerBean = (OrderStatusConcurrencyControllerBean) ic.lookup("java:app/OrderStatusConcurrentEJBLite/OrderStatusConcurrencyControllerBean");
	
			controllerBean.stopWork();

		} catch (Throwable t) {
			System.out.println ("TestDurationTimerTask.run() caught throwable: " + t);
			t.printStackTrace();
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
