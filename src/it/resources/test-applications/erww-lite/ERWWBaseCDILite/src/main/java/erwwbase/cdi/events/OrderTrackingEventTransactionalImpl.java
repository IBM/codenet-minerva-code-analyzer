package erwwbase.cdi.events;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.validation.Valid;
import erwwbase.cdi.qualifiers.OrderTrackingTransactionalQualifier;
// Comment out unused import
//import erwwbase.cdi.events.OrderTrackingPayload;

@Priority(1)
@OrderTrackingTransactionalQualifier
@Dependent
public class OrderTrackingEventTransactionalImpl implements OrderTrackingEvent {
	
	@Valid
	private OrderTrackingPayload orderTrackingPayload = null;
	
	@Valid
	public OrderTrackingEventTransactionalImpl(OrderTrackingPayload orderTrackingPayload) {
		this.orderTrackingPayload = orderTrackingPayload;
	}

	// @Valid removed to prevent HV000132 error JM 7/28/22
	public void OrderTrackingEvent(OrderTrackingPayload orderTrackingPayload) {
		this.orderTrackingPayload = orderTrackingPayload;
	}
	
	@Valid
	public OrderTrackingPayload getOrderTrackingPayload() {
		return orderTrackingPayload;
	}

}
