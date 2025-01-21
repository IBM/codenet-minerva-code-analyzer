//***********************************************************
// 
// Once an hour this timer will be fired to automatically
// run the "Stock Level" function.
//
// Functionality Tested:
//		EJB 3.x - Automatic Non-persistent Timers
//		EJB 3.x - Injection of a Singleton Session Bean
//      EJB 3.x - @Schedule annotation
//
//***********************************************************

package startupandtimer;

import irwwbase.EJBNPTimerStatsBean;

//import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Schedule;
import jakarta.ejb.Stateless;
import jakarta.ejb.Timer;
//import javax.sql.DataSource;

import stocklevelsession.ClientBean;

@Stateless
@Local(AutoNonPersistentTimerLocal.class)
public class AutoNonPersistentTimerBean  {

	@EJB 
	ClientBean slClient;
	
	@EJB
	EJBNPTimerStatsBean theEJBNPTimerStatsBean;

	// shupert - update schedule so we do not conflict with the three persistent
	//           timers that are also running in ERWW.
	@Schedule(hour="*",minute="20",persistent=false)
	public void callStockLevel(Timer timer) {
		try {
			
			debugOut(" ERRW - Executing .callStockLevel() for EJB, non-persistent, auto timer (fires at 20 min after the hour)");

			// Run stock level, but we really don't care what the output is.
			// If it dies, we do care about that, and will catch an
			// exception below.
			slClient.webAutoGenInput();
			
			// Now, tell the statistics bean that this timer just fired.  If we get here the stock level transaction
			// ran ok.   Otherwise, if an exception occurred, we will skip over this line of code, which is good because
			// we don't want to count the timer method as running yet.  The EJB container will be soon retrying the timer
			// method if an exception occurred.
			theEJBNPTimerStatsBean.autoTimerFired();
			
		}
		catch (Throwable t) {
			System.out.println("ERWW: Something went wrong running Stock level transaction from EJB, non-persistent, auto timer.  Exception is: " + t.getMessage());
			
			// If this is the first exception that we caught (ie. FFDC), save it to print later if client runs the WebEJBChecker
			if ( !theEJBNPTimerStatsBean.getAutoTimerFirstExceptionCaught() ){
			   theEJBNPTimerStatsBean.setAutoTimerFirstExceptionCaught(true);
			   theEJBNPTimerStatsBean.setAutoTimerExceptionData(t.getMessage());
			   theEJBNPTimerStatsBean.setAutoTimerInfoMessage("Unexpected exception occurred while executing the Stock Level transaction");
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
