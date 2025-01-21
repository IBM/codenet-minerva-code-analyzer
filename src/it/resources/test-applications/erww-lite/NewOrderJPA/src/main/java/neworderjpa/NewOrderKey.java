package neworderjpa;

/**
 * Key class for Entity Bean: NewOrderCMPEntity
 */
public class NewOrderKey implements java.io.Serializable {
	static final long serialVersionUID = 3206093459760846163L;
	/**
	 * Implementation field for persistent attribute: orderId
	 */
	public int orderId;
	/**
	 * Implementation field for persistent attribute: districtId
	 */
	public short districtId;
	/**
	 * Implementation field for persistent attribute: warehouseId
	 */
	public short warehouseId;
	/**
	 * Creates an empty key for Entity Bean: NewOrderCMPEntity
	 */
	public NewOrderKey() {
	}
	/**
	 * Creates a key for Entity Bean: NewOrderCMPEntity
	 */
	public NewOrderKey(
		int orderId,
		short districtId,
		short warehouseId) {
		this.orderId = orderId;
		this.districtId = districtId;
		this.warehouseId = warehouseId;
	}
	/**
	 * Returns true if both keys are equal.
	 */
	public boolean equals(java.lang.Object otherKey) {
		if (otherKey instanceof neworderjpa.NewOrderKey) {
			neworderjpa.NewOrderKey o =
				(neworderjpa.NewOrderKey) otherKey;
			return (
				(this.orderId == o.orderId)
					&& (this.districtId == o.districtId)
					&& (this.warehouseId == o.warehouseId));
		}
		return false;
	}
	/**
	 * Returns the hash code for the key.
	 */
	public int hashCode() {
		return (
			(java.lang.Integer.valueOf(orderId).hashCode())
				+ (java.lang.Short.valueOf(districtId).hashCode())
				+ (java.lang.Short.valueOf(warehouseId).hashCode()));
	}
	/**
	 * Get accessor for persistent attribute: orderId
	 */
	public int getOrderId() {
		return orderId;
	}
	/**
	 * Set accessor for persistent attribute: orderId
	 */
	public void setOrderId(int newOrderId) {
		orderId = newOrderId;
	}
	/**
	 * Get accessor for persistent attribute: districtId
	 */
	public short getDistrictId() {
		return districtId;
	}
	/**
	 * Set accessor for persistent attribute: districtId
	 */
	public void setDistrictId(short newDistrictId) {
		districtId = newDistrictId;
	}
	/**
	 * Get accessor for persistent attribute: warehouseId
	 */
	public short getWarehouseId() {
		return warehouseId;
	}
	/**
	 * Set accessor for persistent attribute: warehouseId
	 */
	public void setWarehouseId(short newWarehouseId) {
		warehouseId = newWarehouseId;
	}
}
