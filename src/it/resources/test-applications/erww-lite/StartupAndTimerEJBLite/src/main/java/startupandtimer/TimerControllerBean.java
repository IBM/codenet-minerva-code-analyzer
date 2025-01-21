package startupandtimer;

import irwwbase.UserException;

import java.util.Collection;

import jakarta.annotation.Resource;
import jakarta.ejb.Local;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerService;

/**
 * Session Bean implementation class TimerControllerBean
 */
@Stateless
@Local(TimerControllerLocal.class)
public class TimerControllerBean implements TimerControllerLocal {

	@Resource private SessionContext ctx;


	public int cancelAllTimers() throws UserException{
		
		String timerInfo = "";
		
		TimerService ts = ctx.getTimerService();

		Collection<Timer> timers = ts.getAllTimers();
				
		try{
			for(Timer timer : timers){
					timerInfo = (String) timer.getInfo();
					timer.cancel();
			}
		}catch(IllegalStateException e){
			throw new UserException(e.getMessage(),e,"%%%%% ERROR: timer.cancel() failed in TimerControllerBean.cancelAllTimers() "
					+ "with IllegalStateException for timer: " + timerInfo + " - "
					+ "Wait a little bit and retry the cancel.");
		}catch (Throwable t){
			throw new UserException(t.getMessage(),t,"%%%%% ERROR: timer.cancel() failed in TimerControllerBean.cancelAllTimers() "
					+ "for timer: " + timerInfo + " - ");
		}
		
		return timers.size();
	}

}
