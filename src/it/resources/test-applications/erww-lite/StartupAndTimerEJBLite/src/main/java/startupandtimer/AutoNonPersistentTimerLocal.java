//***********************************************************
// 
// Once and hour this timer will be fired to automatically
// run the "Stock Level EJB" function.
//
// Functionality Tested:
//		EJB 3.x - Automatic Non-persistent Timers
//  	EJB 3.x - No Interface view on a Stateless Bean
//		EJB 3.x - Injection of a Singleton Session Bean
//      EJB 3.x - new @Schedule annotation
//
//***********************************************************

package startupandtimer;

import jakarta.ejb.Timer;

public interface AutoNonPersistentTimerLocal  {
	
	public void callStockLevel(Timer timer);
	
}
