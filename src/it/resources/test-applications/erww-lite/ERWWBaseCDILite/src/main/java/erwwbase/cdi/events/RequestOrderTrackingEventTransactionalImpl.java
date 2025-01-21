package erwwbase.cdi.events;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import erwwbase.cdi.qualifiers.OrderTrackingTransactionalQualifier;
import jakarta.validation.Valid;

@Priority(1)
@OrderTrackingTransactionalQualifier
@Dependent
public class RequestOrderTrackingEventTransactionalImpl implements RequestOrderTrackingEvent {
	
	@Valid
	private RequestOrderTrackingPayload requestOrderTrackingPayload = null;
	
	@Valid
	public RequestOrderTrackingEventTransactionalImpl(RequestOrderTrackingPayload requestOrderTrackingPayload) {
		this.requestOrderTrackingPayload = requestOrderTrackingPayload;
	}
	
	// @Valid removed to prevent HV000132 error JM 7/28/22
	public void RequestOrderTrackingEvent(RequestOrderTrackingPayload requestOrderTrackingPayload) {
		this.requestOrderTrackingPayload = requestOrderTrackingPayload;
	}
		
	@Valid
	public RequestOrderTrackingPayload getRequestOrderTrackingPayload() {
		return requestOrderTrackingPayload;
	}

}
