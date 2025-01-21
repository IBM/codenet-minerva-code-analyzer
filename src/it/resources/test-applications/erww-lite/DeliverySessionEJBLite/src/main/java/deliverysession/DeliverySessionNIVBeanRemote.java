package deliverysession;

import irwwbase.UserException;

import java.util.concurrent.Future;

public interface DeliverySessionNIVBeanRemote 
{
	public SingleDeliveryOutput secureDeliverOrderforDistrict(int dist, 
			DeliveryInput input) throws UserException;

	public Future<SingleDeliveryOutput> secureAsyncDeliverOrderforDistrict(int dist, 
			DeliveryInput input) throws UserException;

	public void asyncNotifyDistrictWeAreSendingOrder(int dist);
}

