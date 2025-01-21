/**
 * 
 */
package pagecode;

import jakarta.faces.component.html.HtmlCommandButton;
import jakarta.faces.component.html.HtmlForm;
import jakarta.faces.component.html.HtmlPanelGroup;

/**
 * @author ibmadmin
 *
 */
public class SpecialOfferInput extends PageCodeBase {

	protected HtmlForm SpecialOfferForm;
	protected HtmlPanelGroup TopGroup;
	protected HtmlCommandButton FireSpecialOfferEvent;

	protected HtmlForm getSpecialOfferForm() {
		if (SpecialOfferForm == null) {
			SpecialOfferForm = (HtmlForm) findComponentInRoot("SpecialOfferForm");
		}
		return SpecialOfferForm;
	}

	protected HtmlPanelGroup getTopGroup() {
		if (TopGroup == null) {
			TopGroup = (HtmlPanelGroup) findComponentInRoot("TopGroup");
		}
		return TopGroup;
	}

	protected HtmlCommandButton getFireSpecialOfferEvent() {
		if (FireSpecialOfferEvent == null) {
			FireSpecialOfferEvent = (HtmlCommandButton) findComponentInRoot("FireSpecialOfferEvent");
		}
		return FireSpecialOfferEvent;
	}

}