package erwwbase.cdi.events;

public interface OrderTrackingEvent {
	
	public void OrderTrackingEvent(OrderTrackingPayload orderTrackingPayload);
	
	public OrderTrackingPayload getOrderTrackingPayload();
}
