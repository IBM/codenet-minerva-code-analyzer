package irwwbase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * shupert:
 * 
 * This Bean keeps statistics about the EJB 3.x Persistent Timers that are running in
 * ERWW.  Validation methods are provided to check that the timers have been firing
 * as expected over a period of time. 
 */

@Singleton
@Startup
public class EJBPersistentTimerStatsBean {
	
    // timer interval for each of the three persistent timers = 20 minutes
    long timerInterval = 20;
	
    // system time in seconds when this singleton was created
	private long persistentTimerStatsBeanCreateTime = 0;
	
	// the timestamp when this singleton was created
	private long thisBeanCreationTimestamp = 0;
                                                                                                                                     
	
	private boolean initTimer1DBTableRow = false;
	private boolean timer1FirstExceptionCaught = false;         // Did timer1 get at least one unexpected exception?
	private String  timer1ExceptionData = "None";               // Data for timer1's exception, if we caught one
	private String  timer1InfoMessage = "None";                 // Timer1's Info Message for end user, if any
	
	private boolean initTimer2DBTableRow = false;
	private boolean timer2FirstExceptionCaught = false;         // Did timer2 get at least one unexpected exception?
	private String  timer2ExceptionData = "None";               // Data for timer2's exception, if we caught one
	private String  timer2InfoMessage = "None";                 // Timer2's Info Message for end user, if any
	
	private boolean initTimer3DBTableRow = false;
	private boolean timer3FirstExceptionCaught = false;         // Did timer3 get at least one unexpected exception?
	private String  timer3ExceptionData = "None";               // Data for timer3's exception, if we caught one
	private String  timer3InfoMessage = "None";                 // Timer3's Info Message for end user, if any
	
	// Flag to stop the calculated tick counting when timers are cancelled.
	private boolean calculateTickCounts = true;
	
	@PersistenceContext(unitName = "PersistentTimerStats")
	EntityManager em;
	
	
	/**
	 * This is a startup bean so the ctor will run
	 * at application start time.
	 */
	public EJBPersistentTimerStatsBean() {
		
		// record bean creation time 
	    persistentTimerStatsBeanCreateTime = System.currentTimeMillis()/60000;   // system time in minutes
		debugOut("Executing EJBPersistentTimerStatsBean ctor, Bean create time = " + persistentTimerStatsBeanCreateTime);
	}
		
	// Use post construct to initially create the timer statistic DB table rows.
	@PostConstruct
	public void startup() {
		
		debugOut("Executing EJBPersistentTimerStatsBean @PostConstruct method '.startup', setting up timer statistics DB table rows");
		
		
		// Setup DB query values for the current time plus or minus 5 min (ie. 300000 millisec)
	    long currentTime = Calendar.getInstance().getTimeInMillis();
	    long currentMinTime = currentTime - 300000;
	    long currentMaxTime = currentTime + 300000;
	    
		PersistentTimerStatsJPA timerStats = null;
		
		// Do the fuzzy query, and set locking mode in case we update the rows
		
		Query theQuery = em.createQuery("select t from PersistentTimerStatsJPA t where t.beanCreationTimestamp between :minTime and :maxTime")
		                              .setParameter("minTime", currentMinTime)
		                              .setParameter("maxTime", currentMaxTime);
		theQuery.setLockMode(LockModeType.PESSIMISTIC_READ);
		
		// Get the resultSet 
		/* Replace this code with the below code to remove type safety warning
		List<PersistentTimerStatsJPA> theResults = theQuery.getResultList();
		*/
		List<PersistentTimerStatsJPA> theResults = new ArrayList<PersistentTimerStatsJPA>();
		Object var = theQuery.getResultList();
		if (var instanceof List) {
			for (int i=0;i<((List<?>)var).size();i++) {
				Object item = ((List<?>)var).get(i);
				if (item instanceof PersistentTimerStatsJPA) {
					theResults.add((PersistentTimerStatsJPA)item);
				}
			}
		}
				
		// Did we find a matching DB row for TIMER1?
		if (!theResults.isEmpty()) {
						
			// Look for matching TIMER1 row and use the existing timestamp value
			for (PersistentTimerStatsJPA theRow: theResults) {
				if (theRow.getName().equals("TIMER1")) {
					thisBeanCreationTimestamp = theRow.getBeanCreationTimestamp();				
					debugOut("Found persistent timer statistics rows in DB with timestamp +/- 5 min. from current time, using timestamp in primary key: " + thisBeanCreationTimestamp);
				}
			}			
		}
				
		// If no matching row found for TIMER1, assume that all the persistent timer stats are
		// not created yet, and do it now.
		if (thisBeanCreationTimestamp == 0) {
		
			debugOut("No existing persistent timer statistics rows in DB with timestamp +/- 5 min. from current time so creating them now");
			thisBeanCreationTimestamp = currentTime;  
			
			timerStats = new PersistentTimerStatsJPA();
			timerStats.setName("TIMER1");
			timerStats.setBeanCreationTimestamp(thisBeanCreationTimestamp);
			timerStats.setTimerInterval(timerInterval);
			timerStats.setInitialStartTime(0);
			timerStats.setActualTickCount(0);
			timerStats.setCalculatedTickCount(0);
			em.persist(timerStats);
			
			timerStats = new PersistentTimerStatsJPA();
			timerStats.setName("TIMER2");
			timerStats.setBeanCreationTimestamp(thisBeanCreationTimestamp);
			timerStats.setTimerInterval(timerInterval);
			timerStats.setInitialStartTime(0);
			timerStats.setActualTickCount(0);
			timerStats.setCalculatedTickCount(0);
			em.persist(timerStats);
			
			timerStats = new PersistentTimerStatsJPA();
			timerStats.setName("TIMER3");
			timerStats.setBeanCreationTimestamp(thisBeanCreationTimestamp);
			timerStats.setTimerInterval(timerInterval);
			timerStats.setInitialStartTime(0);
			timerStats.setActualTickCount(0);
			timerStats.setCalculatedTickCount(0);
			em.persist(timerStats);									
		}							
	}
	
	/**
	 * Update tick count for timer #1 
	 */
	public void timer1Fired() {

		long currentTime = System.currentTimeMillis()/60000;  // system time in minutes		
		PersistentTimerStatsPK timer1PK = new PersistentTimerStatsPK("TIMER1", thisBeanCreationTimestamp);
		PersistentTimerStatsJPA timerStats = em.find(PersistentTimerStatsJPA.class, timer1PK, LockModeType.PESSIMISTIC_WRITE);		
		
		// Because persistent timers are "really" persistent, we may receive timer events
		// when the application first starts that are simply "catch up" events (ie. missed timer
		// ticks from when the application had temporarily been stopped).  To allow for this
		// we are going to ignore all timer ticks for the first 5 minutes after the application
		// starts.  By then we should be caught up with any old events and can start keeping 
		// statistics for the current run of the application.
		if (currentTime - persistentTimerStatsBeanCreateTime > 5){

			// On first timer tick, finish initialization of the DB record for this timer.
			// On subsequent timer ticks, update the tick counter.
			if (!initTimer1DBTableRow) {

				// pretend like timer just started now.	
				timerStats.setInitialStartTime(currentTime);   
				initTimer1DBTableRow = true;
				debugOut("EJBPersistentTimerStatsBean.timer1Fired... on tick 0, initialTime = " + timerStats.getInitialStartTime());

			} else {

				// update tick counter
				timerStats.setActualTickCount(timerStats.getActualTickCount() + 1);
				debugOut("EJBPersistentTimerStatsBean.timer1Fired tickCount = " + timerStats.getActualTickCount() + " currentTime = " + currentTime + " initialTime = " + timerStats.getInitialStartTime());
			}
			em.persist(timerStats);
			
		} else {
			
			debugOut("EJBPersistentTimerStatsBean.timer1Fired... in 5 minute warmup period.  All timer ticks are ignored.");
		}
	}	
	
	
	/**
	 * Update tick count for timer #2 
	 */
	public void timer2Fired() {

		long currentTime = System.currentTimeMillis()/60000;  // system time in minutes
		PersistentTimerStatsPK timer2PK = new PersistentTimerStatsPK("TIMER2", thisBeanCreationTimestamp);
		PersistentTimerStatsJPA timerStats = em.find(PersistentTimerStatsJPA.class, timer2PK, LockModeType.PESSIMISTIC_WRITE);

		// Because persistent timers are "really" persistent, we may receive timer events
		// when the application first starts that are simply "catch up" events (ie. missed timer
		// ticks from when the application had temporarily been stopped).  To allow for this
		// we are going to ignore all timer ticks for the first 5 minutes after the application
		// starts.  By then we should be caught up with any old events and can start keeping 
		// statistics for the current run of the application.
		if (currentTime - persistentTimerStatsBeanCreateTime > 5){		

			// On first timer tick, finish initialization of the DB record for this timer.
			// On subsequent timer ticks, update the tick counter.
			if (!initTimer2DBTableRow) {

				// pretend like timer just started now.
				timerStats.setInitialStartTime(currentTime);  
				initTimer2DBTableRow = true;
				debugOut("EJBPersistentTimerStatsBean.timer2Fired... on tick 0, initialTime = " + timerStats.getInitialStartTime());

			} else {

				// update tick counter

				timerStats.setActualTickCount(timerStats.getActualTickCount() + 1);
				debugOut("EJBPersistentTimerStatsBean.timer2Fired tickCount = " + timerStats.getActualTickCount() + " currentTime = " + currentTime + " initialTime = " + timerStats.getInitialStartTime());
			}
			em.persist(timerStats);

		} else {
			
			debugOut("EJBPersistentTimerStatsBean.timer2Fired... in 5 minute warmup period.  All timer ticks are ignored.");
		}
	}	
	
	/**
	 * Update tick count for timer #3 
	 */
	public void timer3Fired() {

		long currentTime = System.currentTimeMillis()/60000;  // system time in minutes
		PersistentTimerStatsPK timer3PK = new PersistentTimerStatsPK("TIMER3", thisBeanCreationTimestamp);
		PersistentTimerStatsJPA timerStats = em.find(PersistentTimerStatsJPA.class, timer3PK, LockModeType.PESSIMISTIC_WRITE);

		// Because persistent timers are "really" persistent, we may receive timer events
		// when the application first starts that are simply "catch up" events (ie. missed timer
		// ticks from when the application had temporarily been stopped).  To allow for this
		// we are going to ignore all timer ticks for the first 5 minutes after the application
		// starts.  By then we should be caught up with any old events and can start keeping 
		// statistics for the current run of the application.
		if (currentTime - persistentTimerStatsBeanCreateTime > 5){		

			// On first timer tick, finish initialization of the DB record for this timer.
			// On subsequent timer ticks, update the tick counter.
			if (!initTimer3DBTableRow) {

				// pretend like timer just started now.
				timerStats.setInitialStartTime(currentTime); 
				initTimer3DBTableRow = true;
				debugOut("EJBPersistentTimerStatsBean.timer3Fired... on tick 0, initialTime = " + timerStats.getInitialStartTime());

			} else {

				// update tick counter
				timerStats.setActualTickCount(timerStats.getActualTickCount() + 1);
				debugOut("EJBPersistentTimerStatsBean.timer3Fired tickCount = " + timerStats.getActualTickCount() + " currentTime = " + currentTime + " initialTime = " + timerStats.getInitialStartTime());
			}
			em.persist(timerStats);

		} else {
			
			debugOut("EJBPersistentTimerStatsBean.timer3Fired... in 5 minute warmup period.  All timer ticks are ignored.");
		}
	}	

	
	/**
	 * Validate the EJB persistent timer #1 (automatically created via annotations). 
	 * 
	 * @return true = good results
	 * @return false = failure 
	 */
	public boolean validatePersistentTimer1() {

		long currentTime = System.currentTimeMillis()/60000;   // system time in minutes
		PersistentTimerStatsPK timer1PK = new PersistentTimerStatsPK("TIMER1", thisBeanCreationTimestamp);
		PersistentTimerStatsJPA timerStats = em.find(PersistentTimerStatsJPA.class, timer1PK);
		
		debugOut("ERWW - Executing EJBPersistentTimerStatsBean.validatePersistentTimer1 tickCounter = " + timerStats.getActualTickCount() + " current time = " + currentTime + " start time = " + timerStats.getInitialStartTime());

		// See if correct number of ticks occurred and we have not had any exceptions:
		//
		// First, if the tick counter is still 0, we need to look a bit further.  If the bean was recently
		// created we may have just not reached the first tick count, otherwise the timer may not be running
		// at all for some unexpected reason.
		//
		// Otherwise, compare the actual timer tick count to the calculated tick count.  The calculated tick
		// count is the current system time (in minutes), minus the initial start time (in minutes), divided
		// by the timer interval.  The remainder is discarded so that we get an integer number of timer ticks.
		//
		// Finally, make sure there were no exceptions during the timer methods.  Otherwise, fail validation.

		if (timerStats.getActualTickCount() == 0) {

			// If more than 20 minutes has gone by since the bean was created, and the initial start time was
			// not recorded, this timer is not firing at all for some reason. Note that Tick 0 is not counted, but
			// is used to record the initial timer start time.  Tick 0 should have happened by now. 
			if ((currentTime - persistentTimerStatsBeanCreateTime > 60) && (timerStats.getInitialStartTime() == 0)) {
				System.out.println("ERWW - Error: The persistent timer1's initial start time is uninitialized.   The timer did not fire when expected!");
				return (false);	

				// Otherwise, just wait for more time to go by.	
			} else {
				
				em.persist(timerStats);
				
				// If we happened to have an exception during the very first timer tick the count may still be 0 and we need
				// to return a failure now.
				if (timer1FirstExceptionCaught) {
					return(false);				   
				} else {
				    return (true);
				}    
			}    

		} else {	
			
			// Determine value for calculated tick count and write it out to the DB table
			long elapsedTime = currentTime - timerStats.getInitialStartTime();
			
			// Stop updating the tick count once the timers are cancelled
            if(calculateTickCounts) {
			   timerStats.setCalculatedTickCount(elapsedTime / timerStats.getTimerInterval());
            }
            
			em.persist(timerStats);
		
			if (timerStats.getActualTickCount() == timerStats.getCalculatedTickCount()){
				
				// Great, the counts match.  Now have we had any exceptions?
				if (timer1FirstExceptionCaught) {
					return(false);				   
				} else {
					return(true);
				}	
		
			} else {
				// counts don't match
				return(false);
			}			
		}
	}
	
	
	/**
	 * Validate the EJB persistent timer #2 (automatically created via xml). 
	 * 
	 * @return true = good results
	 * @return false = failure 
	 */
	public boolean validatePersistentTimer2() {

		long currentTime = System.currentTimeMillis()/60000;   // system time in minutes
		PersistentTimerStatsPK timer2PK = new PersistentTimerStatsPK("TIMER2", thisBeanCreationTimestamp);
		PersistentTimerStatsJPA timerStats = em.find(PersistentTimerStatsJPA.class, timer2PK);
		
		debugOut("ERWW - Executing EJBPersistentTimerStatsBean.validatePersistentTimer2 tickCounter = " + timerStats.getActualTickCount() + " current time = " + currentTime + " start time = " + timerStats.getInitialStartTime());

		// See if correct number of ticks occurred and we have not had any exceptions.
		//
		// First, if the tick counter is still 0, we need to look a bit further.  If the bean was recently
		// created we may have just not reached the first tick count, otherwise the timer may not be running
		// at all for some unexpected reason.
		//
		// Otherwise, compare the actual timer tick count to the calculated tick count.  The calculated tick
		// count is the current system time (in minutes), minus the initial start time (in minutes), divided
		// by the timer interval.  The remainder is discarded so that we get an integer number of timer ticks.
		//
		// Finally, check if we have had any exceptions.   If so, fail validation.

		if (timerStats.getActualTickCount() == 0) {

			// If more than 20 minutes has gone by since the bean was created, and the initial start time was
			// not recorded, this timer is not firing at all for some reason. Note that Tick 0 is not counted, but
			// is used to record the initial timer start time.  Tick 0 should have happened by now. 
			if ((currentTime - persistentTimerStatsBeanCreateTime > 60) && (timerStats.getInitialStartTime() == 0)) {
				System.out.println("ERWW - Error: The persistent timer2's initial start time is uninitialized.   The timer did not fire when expected!");
				return (false);	

				// Otherwise, just wait for more time to go by.	
			} else {
				
				em.persist(timerStats);
				
				// If we happened to have an exception during the very first timer tick the count may still be 0 and we need
				// to return a failure now.
				if (timer2FirstExceptionCaught) {
					return(false);				   
				} else {
				    return (true);
				}    
			}    

		} else {	
			
			// Determine value for calculated tick count and write it out to the DB table
			long elapsedTime = currentTime - timerStats.getInitialStartTime();
			
			// Stop updating the tick count once the timers are cancelled
            if(calculateTickCounts) {
			   timerStats.setCalculatedTickCount(elapsedTime / timerStats.getTimerInterval());
            }
            
			em.persist(timerStats);
			
			if (timerStats.getActualTickCount() == timerStats.getCalculatedTickCount()){
				
				// Great, the counts match.  Now have we had any exceptions?
				if (timer2FirstExceptionCaught) {
					return(false);				   
				} else {
					return(true);
				}
				
			} else {
				// counts don't match
				return(false);
			}			
		}
	}
	
	
	/**
	 * Validate the EJB persistent timer #3 (programmatically created). 
	 * 
	 * @return true = good results
	 * @return false = failure 
	 */
	public boolean validatePersistentTimer3() {

		long currentTime = System.currentTimeMillis()/60000;   // system time in minutes
		PersistentTimerStatsPK timer3PK = new PersistentTimerStatsPK("TIMER3", thisBeanCreationTimestamp);
		PersistentTimerStatsJPA timerStats = em.find(PersistentTimerStatsJPA.class, timer3PK);
		
		debugOut("ERWW - Executing EJBPersistentTimerStatsBean.validatePersistentTimer3 tickCounter = " + timerStats.getActualTickCount() + " current time = " + currentTime + " start time = " + timerStats.getInitialStartTime());

		// See if correct number of ticks occurred, and we have not had any exceptions.
		//
		// First, if the tick counter is still 0, we need to look a bit further.  If the bean was recently
		// created we may have just not reached the first tick count, otherwise the timer may not be running
		// at all for some unexpected reason.
		//
		// Otherwise, compare the actual timer tick count to the calculated tick count.  The calculated tick
		// count is the current system time (in minutes), minus the initial start time (in minutes), divided
		// by the timer interval.  The remainder is discarded so that we get an integer number of timer ticks.
		//
		// Finally, check if any exceptions occurred.  If so, fail validation.

		if (timerStats.getActualTickCount() == 0) {

			// If more than 20 minutes has gone by since the bean was created, and the initial start time was
			// not recorded, this timer is not firing at all for some reason. Note that Tick 0 is not counted, but
			// is used to record the initial timer start time.  Tick 0 should have happened by now. 
			if ((currentTime - persistentTimerStatsBeanCreateTime > 60) && (timerStats.getInitialStartTime() == 0)) {
				System.out.println("ERWW - Error: The persistent timer3's initial start time is uninitialized.   The timer did not fire when expected!");
				return (false);	

				// Otherwise, just wait for more time to go by.	
			} else {
				
				em.persist(timerStats);
				
				// If we happened to have an exception during the very first timer tick the count may still be 0 and we need
				// to return a failure now.
				if (timer3FirstExceptionCaught) {
					return(false);				   
				} else {
				    return (true);
				}    
			}    

		} else {	
			
			// Determine value for calculated tick count and write it out to the DB table
			long elapsedTime = currentTime - timerStats.getInitialStartTime();
			
			// Stop updating the tick count once the timers are cancelled
            if(calculateTickCounts) {
			   timerStats.setCalculatedTickCount(elapsedTime / timerStats.getTimerInterval());
            }
            
			em.persist(timerStats);
			
			if (timerStats.getActualTickCount() == timerStats.getCalculatedTickCount()){

				// Great, the counts match.  Now have we had any exceptions?
				if (timer3FirstExceptionCaught) {
					return(false);				   
				} else {
					return(true);
				}
			} else {
				// counts don't match
				return(false);
			}
		}
	}
	
	
	// A few simple getter and setter methods
	
	public long getTimer1TickCount () {
		PersistentTimerStatsPK timer1PK = new PersistentTimerStatsPK("TIMER1", thisBeanCreationTimestamp);
		PersistentTimerStatsJPA timerStats = em.find(PersistentTimerStatsJPA.class, timer1PK);
		return (timerStats.getActualTickCount());		
	}
	
	public long getTimer1CalculatedTickCount () {
		PersistentTimerStatsPK timer1PK = new PersistentTimerStatsPK("TIMER1", thisBeanCreationTimestamp);
		PersistentTimerStatsJPA timerStats = em.find(PersistentTimerStatsJPA.class, timer1PK);
		return (timerStats.getCalculatedTickCount());		
	}
	
	public long getTimer2TickCount () {
		PersistentTimerStatsPK timer2PK = new PersistentTimerStatsPK("TIMER2", thisBeanCreationTimestamp);
		PersistentTimerStatsJPA timerStats = em.find(PersistentTimerStatsJPA.class, timer2PK);
        return (timerStats.getActualTickCount());
	}
	
	public long getTimer2CalculatedTickCount () {
		PersistentTimerStatsPK timer2PK = new PersistentTimerStatsPK("TIMER2", thisBeanCreationTimestamp);
		PersistentTimerStatsJPA timerStats = em.find(PersistentTimerStatsJPA.class, timer2PK);
        return (timerStats.getCalculatedTickCount());
	}
	
	public long getTimer3TickCount () {
		PersistentTimerStatsPK timer3PK = new PersistentTimerStatsPK("TIMER3", thisBeanCreationTimestamp);
		PersistentTimerStatsJPA timerStats = em.find(PersistentTimerStatsJPA.class, timer3PK);
        return (timerStats.getActualTickCount());
	}
	
	public long getTimer3CalculatedTickCount () {
		PersistentTimerStatsPK timer3PK = new PersistentTimerStatsPK("TIMER3", thisBeanCreationTimestamp);
		PersistentTimerStatsJPA timerStats = em.find(PersistentTimerStatsJPA.class, timer3PK);
        return (timerStats.getCalculatedTickCount());
	}
	
	public boolean getTimer1FirstExceptionCaught() {
		return (timer1FirstExceptionCaught);
	}
	
	public void setTimer1FirstExceptionCaught(boolean firstExceptionCaught) {
		timer1FirstExceptionCaught = firstExceptionCaught;
	}

	public boolean getTimer2FirstExceptionCaught() {
		return (timer2FirstExceptionCaught);
	}
	
	public void setTimer2FirstExceptionCaught(boolean firstExceptionCaught) {
		timer2FirstExceptionCaught = firstExceptionCaught;
	}
	
	public boolean getTimer3FirstExceptionCaught() {
		return (timer3FirstExceptionCaught);
	}
	
	public void setTimer3FirstExceptionCaught(boolean firstExceptionCaught) {
		timer3FirstExceptionCaught = firstExceptionCaught;
	}
	
	public String getTimer1ExceptionData() {
		return (timer1ExceptionData); 
	}
	
	public void setTimer1ExceptionData(String exceptionData) {
		timer1ExceptionData = exceptionData; 
	}
	
	public String getTimer2ExceptionData() {
		return (timer2ExceptionData); 
	}
	
	public void setTimer2ExceptionData(String exceptionData) {
		timer2ExceptionData = exceptionData; 
	}
	
	public String getTimer3ExceptionData() {
		return (timer3ExceptionData); 
	}
	
	public void setTimer3ExceptionData(String exceptionData) {
		timer3ExceptionData = exceptionData; 
	}
	
	public String getTimer1InfoMessage() {
		return (timer1InfoMessage);
	}
	
	public void setTimer1InfoMessage(String infoMessage) {
		timer1InfoMessage = infoMessage;
	}
	
	public String getTimer2InfoMessage() {
		return (timer2InfoMessage);
	}
	
	public void setTimer2InfoMessage(String infoMessage) {
		timer2InfoMessage = infoMessage;
	}
	
	public String getTimer3InfoMessage() {
		return (timer3InfoMessage);
	}
	
	public void setTimer3InfoMessage(String infoMessage) {
		timer3InfoMessage = infoMessage;
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
