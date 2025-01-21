package cdi.fragment;

import irwwbase.IRWWBase;
import irwwbase.POJOQualifier;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
//Commenting out this import because it was never used
//import cdi.fragment.SpecialOfferEvent;

@Named("specialOfferBean")
@RequestScoped
public class SpecialOfferBean {
	
	@Inject Event<SpecialOfferEvent> specialOfferEvent;
	
	@Inject @POJOQualifier IRWWBase irwwbase;	
	
	public SpecialOfferBean(){
	}
		
	public void setCouponDiscount(@Observes NewOrderEvent newOrderEvent){
		irwwbase.debugOut("<<< CDI Event 2: In SpecialOfferBean: Entered setCouponDiscount(@Observes NewOrderEvent newOrderEvent): object identity = " + this);
		
		double purchasePrice = 0;
		double[] purchasePriceValues = {0, 20000, 40000};
		String[] couponValues = {"SUCCESSFUL: Use this coupon to get 5% off on your next purchase", 
	             "SUCCESSFUL: Use this coupon to get 10% off on your next purchase", 
	             "SUCCESSFUL: Use this coupon to get 15% off on your next purchase"};

		SpecialOfferPayload specialOfferPayload = new SpecialOfferPayload();
		
		purchasePrice = newOrderEvent.getPurchasePrice();
		
		if (purchasePrice > 0 ){
		
			specialOfferPayload.setPurchasePriceValues(purchasePriceValues);
			specialOfferPayload.setCouponValues(couponValues);
		
			this.fireSpecialOfferEvent(specialOfferPayload);
		}
	} 
		
	public void fireSpecialOfferEvent(SpecialOfferPayload specialOfferPayload) {
		
		try{
			specialOfferEvent.fire(new SpecialOfferEvent(specialOfferPayload));
			irwwbase.debugOut("<<< CDI Event 3: In SpecialOfferBean: specialOfferBean.fireSpecialOfferEvent(): object identity = " + this);
		}catch (Exception e){			
			System.out.println("Error in specialOfferBean.fireSpecialOfferEvent()" + e);
			e.printStackTrace();
		} 
	}
}
 