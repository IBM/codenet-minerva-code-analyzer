/**
 * 
 */
package pagecode;

import jakarta.faces.component.html.HtmlInputText;
import jakarta.faces.component.html.HtmlPanelGroup;
import jakarta.faces.component.html.HtmlPanelGrid;
import jakarta.faces.component.html.HtmlInputSecret;

/**
 * @author mkerr
 *
 */
public class NewOrderLogin extends PageCodeBase {

	protected HtmlPanelGroup UseridGroup;
	protected HtmlPanelGrid NewOrderLoginGrid;
	protected HtmlInputText inCustomerId;
	protected HtmlPanelGroup PasswordGroup;
	protected HtmlInputText inDistrictId;
	protected HtmlInputText j_username;
	protected HtmlInputSecret j_password;
	protected HtmlInputText getInCustomerId() {
		if (inCustomerId == null) {
			inCustomerId = (HtmlInputText) findComponentInRoot("inCustomerId");
		}
		return inCustomerId;
	}

	protected HtmlInputText getInDistrictId() {
		if (inDistrictId == null) {
			inDistrictId = (HtmlInputText) findComponentInRoot("inDistrictId");
		}
		return inDistrictId;
	}

	protected HtmlInputText getJ_username() {
		if (j_username == null) {
			j_username = (HtmlInputText) findComponentInRoot("j_username");
		}
		return j_username;
	}

	protected HtmlInputSecret getJ_password() {
		if (j_password == null) {
			j_password = (HtmlInputSecret) findComponentInRoot("j_password");
		}
		return j_password;
	}

}