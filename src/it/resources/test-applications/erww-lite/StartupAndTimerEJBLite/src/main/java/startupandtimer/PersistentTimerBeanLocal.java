package startupandtimer;

import jakarta.ejb.Timer;

public interface PersistentTimerBeanLocal 
{
	
	public void initTimer();
	
	public void delivery1(Timer timer);
	public void delivery2(Timer timer);
	public void delivery3(Timer timer);

}
