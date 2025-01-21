package orderstatus.concurrent.ejb.lite;

import java.util.Date;

import jakarta.enterprise.concurrent.LastExecution;
import jakarta.enterprise.concurrent.Trigger;

public class OrderStatusTaskTrigger implements Trigger {
	
	
	public OrderStatusTaskTrigger(int inInitialDelay, int inTransactionDelay) {
		super();
		initialStartDelayTime = inInitialDelay;
		transactionDelayTime = inTransactionDelay;
	}

	// initial delay time, and subsequent transaction delay times, both in milliseconds.
	int initialStartDelayTime = 0;
	int transactionDelayTime = 0;		

	public Date getNextRunTime(LastExecution inLastExecution, Date inTaskScheduledDate) {
						
		Date newDate = null;
		
 		debugOut("OrderStatusTrigger.getNextRunTime().");
		
		// The very first time only schedule the initial delay, after that always schedule
		// the transaction delay.
		if (inLastExecution == null) {
			
			newDate = new Date(inTaskScheduledDate.getTime() + initialStartDelayTime);			
			
		} else {

			// Add transaction delay to time when the last run ended to get the next time to
			// we want the task to run.
			newDate = inLastExecution.getRunEnd();
			long nextRunTime = newDate.getTime() + transactionDelayTime;
			newDate.setTime(nextRunTime);
		}
		
		return newDate;
	}

	
	public boolean skipRun(LastExecution arg0, Date arg1) {
		
		// Never skip next run interval
		return false;
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
