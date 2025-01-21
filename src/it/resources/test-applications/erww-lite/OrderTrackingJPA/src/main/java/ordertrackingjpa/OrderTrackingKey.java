package ordertrackingjpa;

import java.io.Serializable;

/**
 * The primary key class for the ORDER_TRACKING database table.
 * 
 */
public class OrderTrackingKey implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String orderTrackingNumber;
	
	public int orderTrackingHop;
	
	public OrderTrackingKey() {
	}
	
	public OrderTrackingKey(
		String orderTrackingNumber,
		int orderTrackingHop) {
		this.orderTrackingNumber = orderTrackingNumber;
		this.orderTrackingHop = orderTrackingHop;
	}
	
	public String getOrderTrackingNumber() {
		return this.orderTrackingNumber;
	}
	public void setOrderTrackingNumber(String otTrackingNumber) {
		this.orderTrackingNumber = otTrackingNumber;
	}
	public int getOrderTrackingHop() {
		return this.orderTrackingHop;
	}
	public void setOrderTrackingHop(int otTrackingHop) {
		this.orderTrackingHop = otTrackingHop;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof OrderTrackingKey)) {
			return false;
		}
		OrderTrackingKey castOther = (OrderTrackingKey)other;
		return 
			this.orderTrackingNumber.equals(castOther.orderTrackingNumber)
			&& (this.orderTrackingHop == castOther.orderTrackingHop);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.orderTrackingNumber.hashCode();
		hash = hash * prime + this.orderTrackingHop;
		
		return hash;
	}
}