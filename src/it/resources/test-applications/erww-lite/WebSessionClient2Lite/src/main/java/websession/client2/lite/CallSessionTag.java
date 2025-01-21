package websession.client2.lite;

import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.JspFragment;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
//Comment out unused import
//import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.PageContext;

/**
 * @author mkerr
 *
 */
public class CallSessionTag extends SimpleTagSupport {
	//Comment out unused variable
    //private ServletRequest request;
    private HttpServletRequest httpRequest;
    private HttpSession session;
    private JspFragment jspFragment1;
	private PageContext pageContext = null;
	private String message = null;
  
	public CallSessionTag() {
		super();
	}
	
    public void doTag() throws JspException, IOException {
    	//Comment out unused variable
		//int httpRc=200;
		
		JspContext ctx = this.getJspContext();

		if (getJspContext() instanceof PageContext)
		      pageContext = (PageContext) getJspContext();
		
		session = pageContext.getSession();
				
		if (session == null){
			session = httpRequest.getSession(true);
		}
			
		String itemId = (String) session.getAttribute("itemId");  
		String itemPrice = (String) session.getAttribute("itemPrice");  
		String previousItemId = (String) session.getAttribute("previousItemId");  
		String previousItemCookie = (String) session.getAttribute("previousItemCookie");  
		String previousItemPrice = (String) session.getAttribute("previousItemPrice"); 
		String JSESSIONIDvalue = (String) session.getAttribute("JSESSIONIDvalue");   
	
		if (previousItemId != null) {
		  	if (previousItemId.compareTo(previousItemCookie)==0)
		 	{
		     	 message = "Success:  The itemId from the cookie and the session match from the Previous Browser Invocation ";
		 	}
		 	else
		 	{
		     	message = "Failure:   The itemId from the cookie and the session bean do not match from the Previous Browser Invocation ";
		 	}   
		 }   

		ctx.setAttribute( "message", message);
		ctx.setAttribute( "JSESSIONIDvalue", JSESSIONIDvalue);
		ctx.setAttribute( "previousItemId", previousItemId);
		ctx.setAttribute( "previousItemPrice", previousItemPrice);
		ctx.setAttribute( "previousItemCookie", previousItemCookie);
		ctx.setAttribute( "itemId", itemId);
		ctx.setAttribute( "itemPrice", itemPrice);
		jspFragment1.invoke( null );

    }

  
//  Setter method for fragment type attribute - Fragement1
    public void setJspFragment1( JspFragment jspFragment1 ) {
      this.jspFragment1 = jspFragment1;
      
    }
}