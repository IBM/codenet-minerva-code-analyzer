/*
 * Created on Nov 1, 2004
 *
 */
package delivery.lite.sync.async.ejb;

import java.io.IOException;

import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.JspFragment;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import deliverysession.ejb3.Client;


/**
 * @author mkerr
 *
 */
public class DeliveryOutputDetailTag extends SimpleTagSupport {
    private Client dEBean;
    private JspFragment jspFragment1;
    private JspFragment jspFragment2;

	/**
	 * 
	 */
	public DeliveryOutputDetailTag() {
		super();
	}

    public void doTag() throws JspException, IOException {
	    
		// Set the value of the deliveryRows in the JSP context
		// so that they are available for the JSP Fragments as scoped variables
		JspContext ctx = getJspContext();
		
		if (dEBean.getOput()!= null) {
			for (int i0=0; i0<dEBean.getOput().length; i0++) {
				ctx.setAttribute( "deliveryRows", dEBean.getOput()[i0] );
				jspFragment1.invoke( null );
			}
		}
		ctx.setAttribute( "status", dEBean.getStatus());
		ctx.setAttribute( "stack", dEBean.getStack());
		jspFragment2.invoke( null );
      }
     

     public void setObject(Client dEBean) throws JspException {
         if (dEBean == null)
             throw new JspException("Null dEBean attribute in DeliveryOutputDetailTag");
         this.dEBean = dEBean;
     }
     
//   Setter method for fragment type attribute - Fragement1
     public void setJspFragment1( JspFragment jspFragment1 ) {
       this.jspFragment1 = jspFragment1;
     }
     public void setJspFragment2( JspFragment jspFragment2 ) {
         this.jspFragment2 = jspFragment2;
       }
	
}
