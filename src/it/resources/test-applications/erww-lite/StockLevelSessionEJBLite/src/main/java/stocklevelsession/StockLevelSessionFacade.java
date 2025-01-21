package stocklevelsession;

import irwwbase.UserException;

public interface StockLevelSessionFacade {
	public StockLevelOutput stockLevelSession(StockLevelInput input) throws UserException;

}
