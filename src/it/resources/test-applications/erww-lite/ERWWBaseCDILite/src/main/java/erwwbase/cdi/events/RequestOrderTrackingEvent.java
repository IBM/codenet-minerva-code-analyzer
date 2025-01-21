package erwwbase.cdi.events;

public interface RequestOrderTrackingEvent {
	
	public void RequestOrderTrackingEvent(RequestOrderTrackingPayload requestOrderTrackingPayload);
	
	public RequestOrderTrackingPayload getRequestOrderTrackingPayload();

}
