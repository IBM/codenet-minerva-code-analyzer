/*
 * Created on Nov 10, 2004
 *
 */
package pricechange.jms.lite;

import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import jakarta.servlet.jsp.JspException;
import java.io.IOException;
//Comment out import that is unused
//import pricechange.jms.lite.PriceChangeJMSClient;

/**
 * @author mkerr
 *
 */
public class CallManualTag extends SimpleTagSupport {
	
	private PriceChangeJMSClient JSBean;
	
	public CallManualTag() {
		super();
	}
	
	public void doTag() throws JspException, IOException {

		try {
			
			JSBean.webManualInput();

		} catch (Throwable t) {
			t.printStackTrace();
			JSBean.setException(t);	
		}
	}

     public void setObject(PriceChangeJMSClient JSBean) throws JspException {
         if (JSBean == null)
             throw new JspException("Null JSBean attribute in CallManualTag");
         this.JSBean = JSBean;
     }
}
