package cdi.fragment;

public class NewOrderEvent {

	private double purchasePrice = 0;
	
	public NewOrderEvent(double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
		
	public double getPurchasePrice() {
		return purchasePrice;
	}	
}
