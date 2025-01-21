/*
 * Created on Nov 1, 2004
 *
 */
package pricechange.jms.lite;

import java.io.IOException;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import jakarta.servlet.jsp.tagext.JspFragment;
import jakarta.servlet.jsp.JspContext;
//Comment out unused import
//import pricechange.jms.lite.PriceChangeJMSClient;


public class PriceChangeOutputTag extends SimpleTagSupport {
	private PriceChangeJMSClient JSBean;
	private JspFragment jspFragment1;

	/**
	 * 
	 */
	public PriceChangeOutputTag() {
		super();
	}

	public void doTag() throws JspException, IOException {

		// Set the value of the price change output fields in the JSP context
		// so they are available for the JSP Fragments as scoped variables
		JspContext ctx = getJspContext();

		Throwable resultException = JSBean.getException();

		if (resultException != null) {
			
			// Price change resulted in exception
			try {
				ctx.setAttribute("priceChangeResult", "none");
				ctx.setAttribute("status", JSBean.getStatus());
				ctx.setAttribute("exception", resultException);
				jspFragment1.invoke(null);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			
			// Price change normal path, no exception
			try {
				ctx.setAttribute("priceChangeResult", JSBean.getOutput());
				ctx.setAttribute("status", JSBean.getStatus());
				ctx.setAttribute("exception", "none");
				jspFragment1.invoke(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}			
	}


	public void setObject(PriceChangeJMSClient JSBean) throws JspException {
		if (JSBean == null)
			throw new JspException("Null JSBean attribute in PriceChangeOutputTag");
		this.JSBean = JSBean;
	}

	public void setJspFragment1( JspFragment jspFragment1 ) {
		this.jspFragment1 = jspFragment1;
	}

}
