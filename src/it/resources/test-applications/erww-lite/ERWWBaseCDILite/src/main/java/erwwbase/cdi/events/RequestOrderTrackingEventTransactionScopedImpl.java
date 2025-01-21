package erwwbase.cdi.events;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import erwwbase.cdi.qualifiers.OrderTrackingTransactionScopedQualifier;
import jakarta.validation.Valid;

@Priority(1)
@OrderTrackingTransactionScopedQualifier
@Dependent
public class RequestOrderTrackingEventTransactionScopedImpl implements RequestOrderTrackingEvent {
	
	@Valid
	private RequestOrderTrackingPayload requestOrderTrackingPayload = null;
	
	@Valid
	public RequestOrderTrackingEventTransactionScopedImpl(RequestOrderTrackingPayload requestOrderTrackingPayload) {
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
