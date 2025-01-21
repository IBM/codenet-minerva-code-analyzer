/*
 * Created on Oct 29, 2004
 *
 */
package webstateful;

import jakarta.ejb.EJB;
import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.JspFragment;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import java.io.IOException;

import stateful.*;
//import stateful.lite.view.AdditupLocal;


import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;


/**
 * @author mkerr
 *
 */
public class CallStatefulTag extends SimpleTagSupport {
    private Client sTBean;
    private ServletResponse response;
    private HttpServletResponse httpResponse;
    private JspFragment jspFragment1;
	private PageContext pageContext = null;
	@EJB AdditupLocal aab;
  
	public CallStatefulTag() {
		super();
	}
	
	public void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null)
			if (System.getProperty("DEBUG_OUT").equals("ON"))
				System.out.println(message);
	}
	
    public void doTag() throws JspException, IOException {
		int httpRc=200;
		
		debugOut("<SFSB> 0");
		JspContext ctx = this.getJspContext();
		debugOut("<SFSB> 1");
		if (getJspContext() instanceof PageContext)
		      pageContext = (PageContext) getJspContext();
		debugOut("<SFSB> 2");
		response = pageContext.getResponse();
		debugOut("<SFSB> 3");
		httpResponse = (HttpServletResponse) response; 
		debugOut("<SFSB> 4");		
		try {
			debugOut("<SFSB> 5");
			sTBean.web(aab);
			debugOut("<SFSB> 6");
			ctx.setAttribute( "status", sTBean.getStatus());
			debugOut("<SFSB> 9");
			ctx.setAttribute( "results", sTBean.getResults());
			jspFragment1.invoke( null );
			debugOut("<SFSB> 11");
		}
		catch (Throwable e) {
			e.printStackTrace();
			System.out.println("<SFSB> A");
			System.out.println("<SFSB> B");
			httpRc=700;	
			System.out.println("<SFSB> C");
			ctx.setAttribute( "httpRc", Integer.toString(httpRc));
			ctx.setAttribute( "status", sTBean.getStatus());
			ctx.setAttribute( "stack", sTBean.getStack());
			System.out.println("<SFSB> D");
			jspFragment1.invoke( null );
		}
    }

    public void setObject(Client sTBean) throws JspException {
    	debugOut("<SFSB> X1");
        if (sTBean == null)
            throw new JspException("Null sTBean attribute in CallStatefulTag");
        debugOut("<SFSB> X2");
        this.sTBean = sTBean;
    }
    
//  Setter method for fragment type attribute - Fragement1
    public void setJspFragment1( JspFragment jspFragment1 ) {
    	debugOut("<SFSB> Y1");
        this.jspFragment1 = jspFragment1;
    }
    
}
