package erwwbase.cdi.events;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import erwwbase.cdi.input.output.pojos.OrderTrackingSingleInstance;
import erwwbase.cdi.qualifiers.OrderTrackingQualifier;

@OrderTrackingQualifier 
@Dependent
public class OrderTrackingPayload implements java.io.Serializable {
	
	private static final long serialVersionUID = -8876180044852814318L;
	
	@Inject @OrderTrackingQualifier RequestOrderTrackingPayload requestOrderTrackingPayload;	
	
	private OrderTrackingSingleInstance [] orderTrackingInstances = null;
	
	private short orderTrackingHopsCount = 0;
	
	@NotNull
	public RequestOrderTrackingPayload getRequestOrderTrackingPayload() {
		return requestOrderTrackingPayload;
	}
	
	public void setRequestOrderTrackingPayload(@NotNull @Valid RequestOrderTrackingPayload newRequestOrderTrackingPayload) {
		this.requestOrderTrackingPayload = newRequestOrderTrackingPayload;
	}

	@NotNull
	public OrderTrackingSingleInstance [] getOrderTrackingInstances() {
		return orderTrackingInstances;
	}
	
	public void setOrderTrackingInstances(@NotNull OrderTrackingSingleInstance [] newOrderTrackingInstances) {
		orderTrackingInstances = newOrderTrackingInstances;
	} 
	
	@NotNull
	@Min(1)
	public short getOrderTrackingHopsCount() {
		return orderTrackingHopsCount;
	}
	
	public void setOrderTrackingHopsCount(@NotNull @Min(1) short newOrderTrackingHopsCount) {
		orderTrackingHopsCount = newOrderTrackingHopsCount;
	}
	
}
