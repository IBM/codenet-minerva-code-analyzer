package deliverysession;

import irwwbase.UserException;

public interface DeliverySessionFacade {
	public DeliveryOutput deliverySession(DeliveryInput input) throws UserException;
	
}
