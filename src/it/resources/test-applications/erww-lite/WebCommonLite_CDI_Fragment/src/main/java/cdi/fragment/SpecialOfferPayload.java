package cdi.fragment;

public class SpecialOfferPayload implements java.io.Serializable {

	private static final long serialVersionUID = -8876180044852814317L;
	
	public double [] purchasePriceValues = null;
	public String [] couponValues = null;
	
	public double [] getPurchasePriceValues() {
		return purchasePriceValues;
	}
	
	public void setPurchasePriceValues(double [] purchasePriceValues) {
		this.purchasePriceValues = purchasePriceValues;
	}
				 
	public String [] getCouponValues() {
		return couponValues;
	}
	
	public void setCouponValues(String [] couponValues) {
		this.couponValues = couponValues;
	}
	
}
