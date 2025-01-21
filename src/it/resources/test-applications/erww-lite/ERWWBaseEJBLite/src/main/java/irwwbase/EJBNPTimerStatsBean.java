package irwwbase;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

/**
 * shupert:
 * 
 * This Bean tracks the EJB non-persistent timers that are running in ERWW.
 * Validation methods are provided to check that the timers have been firing
 * as expected over a period of time.
 */

@Singleton
@Startup
public class EJBNPTimerStatsBean {
	
	
	// System time when bean started
	private long NPTimerStatsBeanCreateTime = 0;  // When bean was created in minutes
	
	// Data for the automatic timer (ie. configured via @Schedule annotation)
	//
	private AtomicInteger autoTimerTickCounter = new AtomicInteger(0);   // Number of times timer has fired 
	private long autoTimerInitialStartTime = 0;                          // Timer start time (ie. system time in minutes)
	private long autoTimerTimeInterval = 60;                             // Lag time between timer pops = 60 minutes
	private long autoTimerCalculatedTickCount = 0;                       // Calculated tick count based on system time difference
	private boolean autoTimerFirstExceptionCaught = false;               // Did auto timer get at least one unexpected exception?
	private String autoTimerExceptionData = "None";                      // Data for exception, if we caught one
	private String autoTimerInfoMessage = "None";                        // Info Message for end user, if any
		
	//Data for the calendar-based timer (ie. created programmatically)
	//
	private AtomicInteger calendarTimerTickCounter = new AtomicInteger(0);  // Number of times timer has fired
	private long calendarTimerInitialStartTime = 0;                         // Timer start time (ie. system time in minutes)
	private long calendarTimerTimeInterval = 60;                            // Lag time between timer pops = 60 minutes
	private long calendarTimerCalculatedTickCount = 0;                      // Calculated tick count based on system time difference
	private boolean calendarTimerFirstExceptionCaught = false;              // Did calendar timer get at least one unexpected exception?
	private String calendarTimerExceptionData = "None";                     // Data for exception, if we caught one
	private String calendarTimerInfoMessage = "None";                       // Info Message for end user, if any
	
	// Flag to stop the calculated tick counting when timers are cancelled.
	private boolean calculateTickCounts = true;
	
	/**
	 * This is a startup bean so the ctor will run
	 * at application start time.
	 */
	public EJBNPTimerStatsBean() {
		
		NPTimerStatsBeanCreateTime = System.currentTimeMillis()/60000;   // system time in minutes		
		debugOut("Executing EJBNPTimerStatsBean ctor, Bean create time = " + NPTimerStatsBeanCreateTime);
	}
		
	
	/**
	 * Update tick count for the EJB automatically created NP timer. 
	 */
	public void autoTimerFired() {

		long currentTime = System.currentTimeMillis()/60000;  // system time in minutes
		
		// On first timer tick, just record the initial start time.
		// On subsequent timer ticks, update the tick counter.
		if (autoTimerInitialStartTime == 0) {
			
			// record start time and wait for next timer pop to occur.
			autoTimerInitialStartTime = currentTime;
			debugOut("EJBNPTimerStatsBean.autoTimerFired... on tick 0, initialTime = " + autoTimerInitialStartTime);

		} else {
			
			// update tick counter
			autoTimerTickCounter.getAndIncrement();
			debugOut("EJBNPTimerStatsBean.autoTimerFired tickCount = " + autoTimerTickCounter.get() + " currentTime = " + currentTime + " initialTime = " + autoTimerInitialStartTime);
		}
	}	
	
	/**
	 * Update tick count for the EJB , progammatically created, NP calendar-based timer.  
	 */
	public void calendarTimerFired() {

		long currentTime = System.currentTimeMillis()/60000;   // system time in minutes
		
		// On first timer tick, just record the initial start time.
		// On subsequent timer ticks, update the tick counter.
		if (calendarTimerInitialStartTime == 0) {
			
			// record initial start time and wait for next timer pop to occur.
			calendarTimerInitialStartTime = currentTime;
			debugOut("EJBNPTimerStatsBean.calendarTimerFired... on tick 0, InitialTime = " + calendarTimerInitialStartTime);

		} else {
			
			calendarTimerTickCounter.getAndIncrement();
			debugOut("EJBNPTimerStatsBean.calendarTimerFired tickCount = " + calendarTimerTickCounter.get() + " currentTime = " + currentTime + " initialTime = " + calendarTimerInitialStartTime);
			
		}
	}	
	
	/**
	 * Validate the EJB  automatically created NP timer. 
	 * 
	 * @return true = good results
	 * @return false = failure 
	 */
	public boolean validateAutoTimer() {

		long currentTime = System.currentTimeMillis()/60000;   // system time in minutes		
		debugOut("ERWW - Executing EJBNPTimerStatsBean.validateAutoTimer tickCounter = " + autoTimerTickCounter.get() + " current time = " + currentTime + " start time = " + autoTimerInitialStartTime);

		// See if correct number of ticks occurred:
		//
		// First, if the tick counter is still 0, we need to look a bit further.  If the bean was recently
		// created we may have just not reached the first tick count, otherwise the timer may not be running
		// at all for some unexpected reason.
		//
		// Otherwise, compare the actual timer tick count to the calculated tick count.  The calculated tick
		// count is the current system time (in minutes), minus the initial start time (in minutes), divided
		// by the timer interval.  The remainder is discarded so that we get an integer number of timer ticks.

		if (autoTimerTickCounter.get() == 0) {

			// If more than 60 minutes has gone by since the bean was created, and the initial start time was
			// not recorded, this timer is not firing at all for some reason. Note that Tick 0 is not counted, but
			// is used to record the initial timer start time.  Tick 0 should have happened by now. 
			if ((currentTime - NPTimerStatsBeanCreateTime > 60) && (autoTimerInitialStartTime == 0)) {
				System.out.println("ERWW - Error: The auto timer's initial start time is uninitialized.   The auto timer did not fire when expected!");
				return (false);	

				// Otherwise, just wait for more time to go by.	
			} else {
				return (true);
			}    

		} else {	
			
			long elapsedTime = currentTime - autoTimerInitialStartTime;
			
			if(calculateTickCounts)
				autoTimerCalculatedTickCount = elapsedTime / autoTimerTimeInterval;
			
			if (autoTimerTickCounter.get() == autoTimerCalculatedTickCount){
				return(true); 
			} else {
				return(false);
			}
			
		}
	}
	
	/**
	 * Validate the EJB programmatically created NP calendar-based timer. 
	 * 
	 * @return true = good results
	 * @return false = failure 
	 */
	public boolean validateCalendarTimer() {

		long currentTime = System.currentTimeMillis()/60000;   // system time in minutes	
		debugOut("ERWW - Executing EJBNPTimerStatsBean.validateCalendarTimer tickCounter = " + calendarTimerTickCounter.get() + " current time = " + currentTime + " start time = " + calendarTimerInitialStartTime);

		// See if correct number of ticks occurred:
		//
		// First, if the tick counter is still 0, we need to look a bit further.  If the bean was recently
		// created we may have just not reached the first tick count, otherwise the timer may not be running
		// at all for some unexpected reason.		
		//
		// Otherwise, compare the actual timer tick count to the calculated tick count.  The calculated tick
		// count is the current system time (in minutes), minus the initial start time (in minutes), divided
		// by the timer interval.  The remainder is discarded so that we get an integer number of timer ticks.

		if (calendarTimerTickCounter.get() == 0) {
		
			// If more than 60 minutes has gone by since the bean was created, and the initial start time was
			// not recorded, this timer is not firing at all for some reason. Note that Tick 0 is not counted, but
			// is used to record the initial timer start time.  Tick 0 should have happened by now. 
			if ((currentTime - NPTimerStatsBeanCreateTime > 60) && (calendarTimerInitialStartTime == 0)) {
				System.out.println("ERWW - Error: The calendar timer's initial start time is uninitialized.   The calendar timer did not fire when expected!");
				return (false);
								
			// Otherwise, just wait for more time to go by.	
			} else {
                return (true);
			}    

		} else {	
			
            long elapsedTime = currentTime - calendarTimerInitialStartTime;
            
            if(calculateTickCounts)
            	calendarTimerCalculatedTickCount = elapsedTime / calendarTimerTimeInterval;
            
            if (calendarTimerTickCounter.get() == calendarTimerCalculatedTickCount) {
            	return(true);
            } else {
            	return(false);
            }
			
		}
	}
	
	// A few simple setter / getter  methods
	
	public long getAutoTimerTickCount () {
		return (autoTimerTickCounter.get());		
	}
	
	public long getAutoTimerCalculatedTickCount () {
		return (autoTimerCalculatedTickCount);		
	}
	
	public void setAutoTimerFirstExceptionCaught (boolean caughtFirstExceptionFlag) {
		autoTimerFirstExceptionCaught = caughtFirstExceptionFlag;
	}
	
	public boolean getAutoTimerFirstExceptionCaught () {
		return (autoTimerFirstExceptionCaught);
	}
	
	public void setAutoTimerExceptionData (String exceptionData) {
		autoTimerExceptionData = exceptionData;
	}
	
	public String getAutoTimerExceptionData () {
		return (autoTimerExceptionData);
	}
	
	public void setAutoTimerInfoMessage (String infoMsg) {
		autoTimerInfoMessage = infoMsg;
	}
	
	public String getAutoTimerInfoMessage () {
		return (autoTimerInfoMessage);
	}
	
	public long getCalendarTimerTickCount () {
        return (calendarTimerTickCounter.get());
	}
	
	public long getCalendarTimerCalculatedTickCount () {
        return (calendarTimerCalculatedTickCount);
	}
	
	public void setCalendarTimerFirstExceptionCaught (boolean caughtFirstExceptionFlag) {
		calendarTimerFirstExceptionCaught = caughtFirstExceptionFlag;
	}
	
	public boolean getCalendarTimerFirstExceptionCaught () {
		return (calendarTimerFirstExceptionCaught);
	}
	
	public void setCalendarTimerExceptionData (String exceptionData) {
		calendarTimerExceptionData = exceptionData;
	}
	
	public String getCalendarTimerExceptionData () {
		return (calendarTimerExceptionData);
	}
	
	public void setCalendarTimerInfoMessage (String infoMsg) {
		calendarTimerInfoMessage = infoMsg;
	}
	
	public String getCalendarTimerInfoMessage () {
		return (calendarTimerInfoMessage);
	}
	
	public void setCalculateTickCounts(boolean flag){
		calculateTickCounts = flag;
	}
	
	public boolean getCalculateTickCounts(){
		return calculateTickCounts;
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
