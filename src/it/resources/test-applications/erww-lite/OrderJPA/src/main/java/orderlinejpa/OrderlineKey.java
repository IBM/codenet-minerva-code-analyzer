package orderlinejpa;

import orderjpa.OrderKey;

/**
 * Key class for Entity Bean: OrderlineCMPEntity
 */
public class OrderlineKey implements java.io.Serializable {
	static final long serialVersionUID = 3206093459760846163L;
	
	/**
	 * Implementation field for reference to Order entity
	 */
	public OrderKey order;

	/**
	 * Implementation field for persistent attribute: olNumber
	 */
	public short olNumber;
	/**
	 * Creates an empty key for Entity Bean: OrderlineCMPEntity
	 */
	public OrderlineKey() {
	}
	/**
	 * Creates a key for Entity Bean: OrderlineCMPEntity
	 */
	public OrderlineKey(
		int olOrderId,
		short olDistrictId,
		short olWarehouseId,
		short olNumber) {
		this.order = new OrderKey(olDistrictId, olOrderId, olWarehouseId);
		this.olNumber = olNumber;
	}
	/**
	 * Returns true if both keys are equal.
	 */
	public boolean equals(java.lang.Object otherKey) {
		if (otherKey instanceof orderlinejpa.OrderlineKey) {
			orderlinejpa.OrderlineKey o =
				(orderlinejpa.OrderlineKey) otherKey;
			return (
					(this.order == o.order)
					&& (this.olNumber == o.olNumber));
		}
		return false;
	}
	/**
	 * Returns the hash code for the key.
	 */
	public int hashCode() {
		return (
				(order.hashCode())
				+ (java.lang.Short.valueOf(olNumber).hashCode()));
	}
	
	/**
	 * Get accessor for persistent attribute: olNumber
	 */
	public short getOlNumber() {
		return olNumber;
	}
	/**
	 * Set accessor for persistent attribute: olNumber
	 */
	public void setOlNumber(short newOlNumber) {
		olNumber = newOlNumber;
	}
}
