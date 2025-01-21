package customerjpa;
/**
 * Key class for Entity Bean: CustomerCMPEntity
 */
public class CustomerKey implements java.io.Serializable {
	static final long serialVersionUID = 3206093459760846163L;
	/**
	 * Implementation field for persistent attribute: customerId
	 */
	public short customerId;
	/**
	 * Implementation field for persistent attribute: customerDId
	 */
	public short customerDId;
	/**
	 * Implementation field for persistent attribute: customerWId
	 */
	public short customerWId;
	/**
	 * Creates an empty key for Entity Bean: CustomerCMPEntity
	 */
	public CustomerKey() {
	}
	/**
	 * Creates a key for Entity Bean: CustomerCMPEntity
	 */
	public CustomerKey(
		short customerId,
		short customerDId,
		short customerWId) {
		this.customerId = customerId;
		this.customerDId = customerDId;
		this.customerWId = customerWId;
	}
	/**
	 * Returns true if both keys are equal.
	 */
	public boolean equals(java.lang.Object otherKey) {
		if (otherKey instanceof customerjpa.CustomerKey) {
			customerjpa.CustomerKey o =
				(customerjpa.CustomerKey) otherKey;
			return (
				(this.customerId == o.customerId)
					&& (this.customerDId == o.customerDId)
					&& (this.customerWId == o.customerWId));
		}
		return false;
	}
	/**
	 * Returns the hash code for the key.
	 */
	public int hashCode() {
		return (
			(java.lang.Short.valueOf(customerId).hashCode())
				+ (java.lang.Short.valueOf(customerDId).hashCode())
				+ (java.lang.Short.valueOf(customerWId).hashCode()));
	}
	/**
	 * Get accessor for persistent attribute: customerId
	 */
	public short getCustomerId() {
		return customerId;
	}
	/**
	 * Set accessor for persistent attribute: customerId
	 */
	public void setCustomerId(short newCustomerId) {
		customerId = newCustomerId;
	}
	/**
	 * Get accessor for persistent attribute: customerDId
	 */
	public short getCustomerDId() {
		return customerDId;
	}
	/**
	 * Set accessor for persistent attribute: customerDId
	 */
	public void setCustomerDId(short newCustomerDId) {
		customerDId = newCustomerDId;
	}
	/**
	 * Get accessor for persistent attribute: customerWId
	 */
	public short getCustomerWId() {
		return customerWId;
	}
	/**
	 * Set accessor for persistent attribute: customerWId
	 */
	public void setCustomerWId(short newCustomerWId) {
		customerWId = newCustomerWId;
	}
}
