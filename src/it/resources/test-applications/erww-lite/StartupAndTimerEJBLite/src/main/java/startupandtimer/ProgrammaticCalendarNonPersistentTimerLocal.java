//***********************************************************
// 
// Once an hour this timer will be fired to automatically
// run the "Stock Level EJB 3.0" function.
//
// Functionality Tested:
//		EJB 3.x - Programatic Calendar-based Non-persistent Timers
//  	EJB 3.x - No Interface view on a Singleton Bean
//		EJB 3.x - Injection of a Singleton Session Bean
//
//***********************************************************

package startupandtimer;

import jakarta.ejb.Local;
import jakarta.ejb.Timer;

@Local
public interface ProgrammaticCalendarNonPersistentTimerLocal  {

	public void initTimer();
	public void callStockLevel(Timer timer);
}
