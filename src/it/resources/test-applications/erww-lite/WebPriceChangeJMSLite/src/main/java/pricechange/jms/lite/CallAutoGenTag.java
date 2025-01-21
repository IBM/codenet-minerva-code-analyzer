/*
 * Created on Oct 29, 2004
 *
 */
package pricechange.jms.lite;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
//comment out import that's never used
//import pricechange.jms.lite.PriceChangeJMSClient;


/**
 * @author mkerr
 *
 */
public class CallAutoGenTag extends SimpleTagSupport {
	
    private PriceChangeJMSClient JSBean;
  
	public CallAutoGenTag() {
		super();
	}
	
	public void doTag() throws JspException, IOException {

		try {

			JSBean.webAutoGenInput();

		} catch (Throwable t) {
			JSBean.setException(t);	
			t.printStackTrace();
		}
	}

    public void setObject(PriceChangeJMSClient JSBean) throws JspException {
        if (JSBean == null)
            throw new JspException("Null JSBean attribute in CallAutoGenTag");
        this.JSBean = JSBean;
    }

}
