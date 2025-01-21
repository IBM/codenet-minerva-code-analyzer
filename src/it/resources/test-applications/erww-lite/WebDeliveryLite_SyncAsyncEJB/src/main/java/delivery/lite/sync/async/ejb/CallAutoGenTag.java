/*
k * Created on Oct 29, 2004
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
public class CallAutoGenTag extends SimpleTagSupport {
    private Client dEBean;
  
	public CallAutoGenTag() {
		super();
	}
	
    public void doTag() throws JspException, IOException {
       
		try {
			dEBean.web();
		}
			catch (Exception e) {
			 e.printStackTrace();
		}
    }

    public void setObject(Client dEBean) throws JspException {
        if (dEBean == null)
            throw new JspException("Null dEBean attribute in CallAutoGenTag");
        this.dEBean = dEBean;
    }

}
