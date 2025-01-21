//***********************************************************
// 
// Once an hour this timer will be fired to automatically
// run the "Stock Level" function.
//
// Functionality Tested:
//		EJB Programatic Calendar-based Non-persistent Timers
//		EJB Injection of a Singleton Session Bean
//
// Jim Krueger 7/27/2009
//***********************************************************

package startupandtimer;

import irwwbase.EJBNPTimerStatsBean;

import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.ScheduleExpression;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerConfig;
import jakarta.ejb.TimerService;
//import javax.sql.DataSource;

import stocklevelsession.ClientBean;

@Stateless
public class ProgrammaticCalendarNonPersistentTimerBean implements ProgrammaticCalendarNonPersistentTimerLocal {

	@Resource private SessionContext ctx;

	@EJB 
	ClientBean slClient;
	
	@EJB
	EJBNPTimerStatsBean theEJBNPTimerStatsBean;

	private String ivTimerName="ProgammaticCalendarBasedStockLevelTimer";

	public void initTimer() {
		
		debugOut("Executing ProgrammaticCalendarNonPersistentTimer31Bean.initTimer()...");
		
		TimerService ts = ctx.getTimerService();		
		ScheduleExpression se = new ScheduleExpression();
		se.timezone("America/Chicago");
		se.hour("*");

		// shupert - updated schedule so we do not conflict with the three EJB 3.x persistent
		//           timers that are also running in ERWW.
		se.minute("40");

		TimerConfig tc = new TimerConfig();
		tc.setPersistent(false);
		tc.setInfo(ivTimerName);
		Timer theTimer = ts.createCalendarTimer(se,tc);

		debugOut("EJB non-persistent, calendar-based, timer created with ScheduledExpression: "
				+ theTimer.getSchedule().toString() + " will first fire at : "
				+ theTimer.getNextTimeout());
	}

	@Timeout
	public void callStockLevel(Timer timer) {
		try {

			debugOut(" ERWW - Executing .callStockLevel() for EJB non-persistent, programmatic, calander-based timer (fires at 40 min after the hour)");

			// Run Stock level, but we really don't care what the output
			// is.  If it dies we do care, and will catch an exception for 
			// that.
			slClient.webAutoGenInput();
			
			
			// Now, tell the statistics bean that this timer just fired.  If we get here the stock level transaction
			// ran ok.   Otherwise, if an exception occurred, we will skip over this line of code, which is good because
			// we don't want to count the timer method as running yet.  The EJB container will be soon retrying the timer
			// method if an exception occurred.
			theEJBNPTimerStatsBean.calendarTimerFired();

		}

		catch (Throwable t) {
			System.out.println("ERWW: Something went wrong running Stock level transaction from EJB, programmatic, calendar-based timer.  Exception is: " + t.getMessage());
			
			// If this is the first exception that we caught (ie. FFDC), save it to print later if client runs the WebEJBChecker
			if ( !theEJBNPTimerStatsBean.getCalendarTimerFirstExceptionCaught() ){
			   theEJBNPTimerStatsBean.setCalendarTimerFirstExceptionCaught(true);
			   theEJBNPTimerStatsBean.setCalendarTimerExceptionData(t.getMessage());
			   theEJBNPTimerStatsBean.setCalendarTimerInfoMessage("Unexpected exception occurred while executing the Stock Level transaction");
			}  
			
			t.printStackTrace();
		}
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