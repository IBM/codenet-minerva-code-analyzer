package erwwbase.cdi.events;

import jakarta.enterprise.context.Dependent;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import erwwbase.cdi.qualifiers.OrderTrackingQualifier;

@OrderTrackingQualifier 
@Dependent
public class RequestOrderTrackingPayload implements java.io.Serializable {
	
	private static final long serialVersionUID = -8876180044855514318L;
	
	public short warehouseId = 0;
	public short districtId = 0;
	public short customerId = 0;
	public int orderId = 0;
	public EntityManager entityManager = null;
	
	public RequestOrderTrackingPayload () {
		super();
	}
	
	@NotNull
	@Min(1)
	public short getWarehouseId() {
		return warehouseId;
	}

	@NotNull
	@Min(1)
	public short getDistrictId() {
		return districtId;
	}

	@NotNull
	@Min(0)
	public short getCustomerId() {
		return customerId;
	}

	@NotNull
	@Min(1)
	public int getOrderId() {
		return orderId;
	}
	
	@NotNull
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setWarehouseId(@NotNull @Min(1) short inWarehouseId) {
		warehouseId = inWarehouseId;
	}
	
	public void setDistrictId(@NotNull @Min(1) short inDistrictId) {
		districtId = inDistrictId;
	}
	
	public void setCustomerId(@NotNull @Min(0) short inCustomerId) {
		customerId = inCustomerId;
	}

	public void setOrderId(@NotNull @Min(1) int inOrderId) {
		orderId = inOrderId;
	}
	
	public void setEntityManager(@NotNull EntityManager inEntityManager) {
		entityManager = inEntityManager;
	}
}
