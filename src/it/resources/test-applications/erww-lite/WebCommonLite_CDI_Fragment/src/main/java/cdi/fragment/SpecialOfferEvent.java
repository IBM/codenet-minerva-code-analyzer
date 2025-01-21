package cdi.fragment;

public class SpecialOfferEvent {
	
	private SpecialOfferPayload specialOfferPayload = null;
	
	public SpecialOfferEvent(SpecialOfferPayload specialOfferPayload) {
		this.specialOfferPayload = specialOfferPayload;
	}
		
	public SpecialOfferPayload getSpecialOfferPayload() {
		return specialOfferPayload;
	}
}
