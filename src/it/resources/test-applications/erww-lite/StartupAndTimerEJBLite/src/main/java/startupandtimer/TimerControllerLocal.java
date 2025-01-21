package startupandtimer;

import irwwbase.UserException;

import jakarta.ejb.Local;

@Local
public interface TimerControllerLocal {

	public int cancelAllTimers() throws UserException;
}
