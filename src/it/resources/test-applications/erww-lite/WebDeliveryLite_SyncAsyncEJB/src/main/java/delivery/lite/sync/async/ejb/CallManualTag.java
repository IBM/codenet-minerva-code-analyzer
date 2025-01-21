/*
 * Created on Nov 10, 2004
 *
 */
package delivery.lite.sync.async.ejb;

import java.io.IOException;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import deliverysession.ejb3.Client;

/**
 * @author mkerr
 *
 */
public class CallManualTag extends SimpleTagSupport {
	private Client dEBean;
	
	public CallManualTag() {
		super();
	}
	
    public void doTag() throws JspException, IOException {
    	
    	try {
    		dEBean.webWithoutGen();
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
     }
    
     public void setObject(Client dEBean) throws JspException {
         if (dEBean == null)
             throw new JspException("Null dEBean attribute in CallManualTag");
         this.dEBean = dEBean;
     }
}
