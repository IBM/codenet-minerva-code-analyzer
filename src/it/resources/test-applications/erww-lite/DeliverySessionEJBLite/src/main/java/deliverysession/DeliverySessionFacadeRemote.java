package deliverysession;

import irwwbase.UserException;

public interface DeliverySessionFacadeRemote {
	public DeliveryOutput deliverySession(DeliveryInput input) throws UserException;
}

