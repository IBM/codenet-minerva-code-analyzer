package irwwbase;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

/**
 * This Bean tracks the number of EJB "fire and forget" asynchronous method calls
 * that were made by the application vs. the number of methods that were actually
 * executed by the WAS runtime.  After a small time delay to allow time for the method
 * to be scheduled by the EJBContainer, and then executed on a Work Manager, these counts
 * should be equal.
 */

@Singleton
@Startup
public class EJBAsyncMethodStatsBean {
	private AtomicInteger numberOfMethodsCalledCounter = new AtomicInteger(0);
	private AtomicInteger numberOfMethodsExecutedCounter = new AtomicInteger(0);
	
	/**
	 * This is a startup bean so the ctor will run
	 * at application start time.
	 */
	public EJBAsyncMethodStatsBean() {
		debugOut("Executing EJBAsyncMethodStatsBean ctor");
	}
	

	/**
	 * Bump up the number of fire and forget
	 * methods that have been called by one.
	 */
	public void fireAndForgetMethodCalled() {
		numberOfMethodsCalledCounter.getAndIncrement();
		debugOut("EJBAsyncMethodStatsBean.fireAndForgetMethodCalled() called count = " + numberOfMethodsCalledCounter.get());
	}
	
	/**
	 * Bump up the number of fire and forget
	 * methods that have been executed by one.
	 */
	public void fireAndForgetMethodExecuted() {
		numberOfMethodsExecutedCounter.getAndIncrement();
		debugOut("EJBAsyncMethodStatsBean.fireAndForgetMethodExcuted() executed count = " + numberOfMethodsExecutedCounter.get());
	}
	
	/**
	 * Validate if the runtime is executing EJB  
	 * asynchronous fire and forget methods properly.
	 * 
	 * @return true = good results
	 * @return false = failure 
	 */
	public boolean validateFireAndForgetMethods() {
		
		debugOut("ERWW - Executing EJBAsyncMethodStatsBean.validateFireAndForgetMethods calledCount = " + numberOfMethodsCalledCounter.get() + " executedCount = " + numberOfMethodsExecutedCounter.get());
		
		if (numberOfMethodsCalledCounter.get() == numberOfMethodsExecutedCounter.get()) {
			return (true);
		} else {
			return (false);
		}
	}

	/**
	 * Return the number of asynchronous fire-and-forget methods
	 * that ERWW attempted to call.
     *
     * @return methodsCalledCounter
	 */	
	public long getMethodsCalledCounter() {
		return (numberOfMethodsCalledCounter.get());
	}
	
	/**
	 * Return the number of asynchronous fire-and-forget methods
	 * that were actually executed by the WAS runtime for ERWW.
     *
     * @return methodsExecutedCounter
	 */		
	public long getMethodsExecutedCounter() {
		return (numberOfMethodsExecutedCounter.get());
	}
	
	
	private void debugOut(String message) 
	{
		if (System.getProperty("DEBUG_OUT") != null)
		{
			if (System.getProperty("DEBUG_OUT").equals("ON"))
			{
				System.out.println(message);
			}
		}
	}


}
