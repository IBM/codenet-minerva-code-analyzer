package orderjpa;

/**
 * Key class for Entity Bean: OrderCMPEntity
 */
public class OrderKey implements java.io.Serializable {
	static final long serialVersionUID = 3206093459760846163L;
	/**
	 * Implementation field for persistent attribute: districtId
	 */
	public short DistrictId;
	/**
	 * Implementation field for persistent attribute: orderId
	 */
	public int OrderId;
	/**
	 * Implementation field for persistent attribute: warehouseId
	 */
	public short WarehouseId;
	/**
	 * Creates an empty key for Entity Bean: OrderCMPEntity
	 */
	public OrderKey() {
	}
	/**
	 * Creates a key for Entity Bean: OrderCMPEntity
	 */
	public OrderKey(
		short districtId,
		int orderId,
		short warehouseId) {
		this.DistrictId = districtId;
		this.OrderId = orderId;
		this.WarehouseId = warehouseId;
	}
	/**
	 * Returns true if both keys are equal.
	 */
	public boolean equals(java.lang.Object otherKey) {
		if (otherKey instanceof orderjpa.OrderKey) {
			orderjpa.OrderKey o =
				(orderjpa.OrderKey) otherKey;
			return (
				(this.DistrictId == o.DistrictId)
					&& (this.OrderId == o.OrderId)
					&& (this.WarehouseId == o.WarehouseId));
		}
		return false;
	}
	/**
	 * Returns the hash code for the key.
	 */
	public int hashCode() {
		return (
			(java.lang.Short.valueOf(DistrictId).hashCode())
				+ (java.lang.Integer.valueOf(OrderId).hashCode())
				+ (java.lang.Short.valueOf(WarehouseId).hashCode()));
	}
	/**
	 * Get accessor for persistent attribute: districtId
	 */
	public short getDistrictId() {
		return DistrictId;
	}
	/**
	 * Set accessor for persistent attribute: districtId
	 */
	public void setDistrictId(short newDistrictId) {
		DistrictId = newDistrictId;
	}
	/**
	 * Get accessor for persistent attribute: orderId
	 */
	public int getOrderId() {
		return OrderId;
	}
	/**
	 * Set accessor for persistent attribute: orderId
	 */
	public void setOrderId(int newOrderId) {
		OrderId = newOrderId;
	}
	/**
	 * Get accessor for persistent attribute: warehouseId
	 */
	public short getWarehouseId() {
		return WarehouseId;
	}
	/**
	 * Set accessor for persistent attribute: warehouseId
	 */
	public void setWarehouseId(short newWarehouseId) {
		WarehouseId = newWarehouseId;
	}
}
