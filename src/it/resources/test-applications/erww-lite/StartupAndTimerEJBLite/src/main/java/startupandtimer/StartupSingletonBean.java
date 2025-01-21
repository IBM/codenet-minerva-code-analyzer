package startupandtimer;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

// Simple startup, singleton bean that uses programming APIs to create a
// calendar based, non-persistent, EJB timer when the ERWW application starts.
//
@Singleton
@Startup
public class StartupSingletonBean  {
	
	@EJB
	ProgrammaticCalendarNonPersistentTimerLocal calendarTimer;
	
	@EJB
	PersistentTimerBeanLocal persistentTimerBean;

		
	@PostConstruct
	public void CreateTimer() {

		debugOut("ERWW Creating programatic calendar-base non-persistent timer ");
		calendarTimer.initTimer();
		debugOut("ERWW Programatic calendar-base non-persistent timer created.");
		
		debugOut("ERWW Creating programtic calendar-base persistent timer for delivery session");
		persistentTimerBean.initTimer();
		debugOut("ERWW Programatic calendar-base persistent timer for delivery session created.");

	}
	

	private void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null)
			if (System.getProperty("DEBUG_OUT").equals("ON"))
				System.out.println(message);
	}	
	
}
