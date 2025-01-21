package orderstatussession.ejb;

import irwwbase.UserException;

public interface OrderStatusSessionFacade {
	public OrderStatusOutput orderStatusSession(OrderStatusInput input) throws UserException;

}
