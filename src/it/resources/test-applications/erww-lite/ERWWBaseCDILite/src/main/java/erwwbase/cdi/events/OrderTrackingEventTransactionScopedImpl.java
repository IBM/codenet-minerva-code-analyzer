package erwwbase.cdi.events;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.validation.Valid;

import erwwbase.cdi.qualifiers.OrderTrackingTransactionScopedQualifier;

@Priority(1)
@OrderTrackingTransactionScopedQualifier
@Dependent
public class OrderTrackingEventTransactionScopedImpl implements OrderTrackingEvent {
		
	@Valid
	private OrderTrackingPayload orderTrackingPayload = null;
	
	@Valid
	public OrderTrackingEventTransactionScopedImpl(OrderTrackingPayload orderTrackingPayload) {
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
