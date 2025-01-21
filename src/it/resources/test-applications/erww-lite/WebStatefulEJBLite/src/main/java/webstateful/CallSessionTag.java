/*
 * Created on Oct 29, 2004
 *
 */
package webstateful;

import irwwbase.ExtendedRandom;
import irwwbase.MaxValues;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

import jakarta.ejb.EJB;
import jakarta.ejb.NoSuchEJBException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.JspFragment;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import stateful.AdditupLocal;
import stateful.StatefulOutput;


public class CallSessionTag extends SimpleTagSupport {
	//Comment out unused variable
    //private Client sTBean;
    private ServletResponse response;
    private HttpServletResponse httpResponse;
    private ServletRequest request;
    private HttpServletRequest httpRequest;
    private HttpSession session;
    private JspFragment jspFragment1;
	private PageContext pageContext = null;
//I18nService declarations
	
	//protected I18nService i18n; 
	//protected Internationalization callerI18n;
	protected Locale locale;
	@EJB AdditupLocal aab;
	

  
	public CallSessionTag() {
		super();
	}
	
	public void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null)
			if (System.getProperty("DEBUG_OUT").equals("ON"))
				System.out.println(message);
	}
	
    public void doTag() throws JspException, IOException {
		//int httpRc=0;
//		i18nService - Get Resource Bundles
		
//		I18nService i18n = new I18nService();
//		i18n.setSessionContext();
//		i18n.setResourceBundle();
//		locale=i18n.getLocale();
//		java.text.DateFormat df = java.text.DateFormat.getDateInstance (java.text.DateFormat.DEFAULT, locale);
//		java.text.DateFormat tf = java.text.DateFormat.getTimeInstance (java.text.DateFormat.DEFAULT, locale);
//		java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance(locale);
		JspContext ctx = this.getJspContext();

		if (getJspContext() instanceof PageContext)
		      pageContext = (PageContext) getJspContext();
		
		response = pageContext.getResponse();
		httpResponse = (HttpServletResponse) response;
		
		request = pageContext.getRequest();
		httpRequest = (HttpServletRequest) request;
		
		session = pageContext.getSession();
				
		if (session == null){
			session = httpRequest.getSession(true);
		}

		int num, loopnum;
		long duration;
		String results = null;
		//Comment out unused variable
		//int httpRC = 670;
		//HttpSession session = null;
		

		loopnum = Integer.valueOf(httpRequest.getParameter("loopnum")).intValue();
		duration = Long.valueOf(httpRequest.getParameter("duration")).longValue();

		//Comment out unused variable
		//String s = new String();

//		System.out.print(".");

		String clientIdent = String.valueOf(System.currentTimeMillis());
		StatefulOutput output = null;

		String clientMatch;
		int sumFromBean = 0;
		int sum = 0, i;
		try {

			
			
			aab.setClientIdentifier(clientIdent);
			session.setAttribute("ejbref", aab);

			for (i = 1; i <= loopnum; i++) {
				ExtendedRandom rand = new ExtendedRandom();
				try {
					num = rand.nextInt(1, MaxValues.itemId((byte) 1));
				} catch (java.lang.Exception ex) {
					ex.printStackTrace();
					throw ex;
				}

				aab = (AdditupLocal) session.getAttribute("ejbref");
				clientMatch = aab.addIt(num);
				debugOut("<Client> Random number is: " + num);
				if (!(clientMatch.equalsIgnoreCase(clientIdent))) {
					results = "Mismatch on session bean ids, wrong bean retrieved after "
							+ i + " "
							+ "remote method calls, identifier on client side is: "
							+ clientIdent + " "
							+ "identifier on bean side is"
							+ "STCNUM:" + aab.getStcNum();
					break;
				}
				debugOut("Identifier on client side is: "
						+ clientIdent + " identifier on bean side is: "
						+ clientMatch);
				sum = sum + num;
				output = aab.total();
				sumFromBean = output.getTotal();
				debugOut("Sum at client is: " + sum
						+ " sum from bean is: " + sumFromBean);
				if (sum != sumFromBean) {
					results = "State data did not sum properly after"
							+ i + " "
							+ "remote method calls, identifier on client side is:"
							+ sum + " "
							+ "sum from bean is:" 
							+ sumFromBean + " " 
							+ "STCNUM:"
							+ aab.getStcNum();
					break;
				}
				debugOut("<Client> Current sum is: " + sum);
				if (i == loopnum)
					break;
				Thread.sleep(duration);
			}
			if (results == null) {
				output = aab.total();
				clientMatch = output.getClientIdentifier();
				if (!(clientMatch.equalsIgnoreCase(clientIdent)))
					results = "Mismatch on session bean ids, wrong bean retrieved";
				sumFromBean = output.getTotal();
				if (sum == sumFromBean && results == null) {
					results = "Test Succeeded!! After" 
							+ i + " "
							+ "remote method calls, sum at client is:" 
							+ sum + " "
							+ "sum from bean is:" 
							+ sumFromBean;					
				} else {
					results = "State data did not sum properly after"
							+ i + " "
							+ "remote method calls, sum at client is:"
							+ sum + " "
							+ "sum from bean is:"
							+ sumFromBean + " "
							+ results;
					
				}
		   }  
		   debugOut("CallSessionTag.doTag()  results string: " + results);

		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			ctx.setAttribute( "status", "failed: Exception occurred to processing of method call loop "+ex.getMessage());
			ctx.setAttribute( "stack", getStackTrace(ex));
			//throw new JspException("Exception occurred to processing of method call loop",ex);			
		}

		try {
			aab.remove(); 
		}
		catch (Exception e) {
			System.out.println("error on remove of ejb ref");
			e.printStackTrace();
			ctx.setAttribute( "status", "failed: something went wrong on remove of ejb "+e.getMessage());
			ctx.setAttribute( "stack", getStackTrace(e));
			throw new JspException("something went wrong on remove of ejb",e);
		} 
		Exception ex=null;
		try {
			aab.setClientIdentifier("end of test");
		} catch (NoSuchEJBException ne) {
			ex=ne;
			debugOut("<<< Got NoSuchObjectException, correct: "+ne.getMessage());
			ctx.setAttribute( "status", "SUCCESSFUL");
		}
		catch (Exception e) {
			ex=e;
			debugOut("<<< Did not get NoSuchObjectException, got: "+e.getMessage());
			ctx.setAttribute( "status", "failed: something else went wrong touching bean after remove: "+ex.getMessage());
			ctx.setAttribute( "stack", getStackTrace(ex));
			// throw new JspException("something else went wrong touching bean after remove",ex);
		}
		if (ex!=null) {
			if (ex instanceof NoSuchEJBException );
			else {
				ctx.setAttribute( "status", "failed: SFSB was removed, then something else went wrong: "+ex.getMessage());
				ctx.setAttribute( "stack", getStackTrace(ex));
				//throw new JspException("SFSB was removed, then something else went wrong: "+ex.getMessage());
				}			
	    }
		else throw new JspException("SFSB was removed, yet it still works ");

		debugOut(" Exiting StatefulTest");
		debugOut(" Timestamp is:" + new String(new java.util.Date().toString()));

		
		ctx.setAttribute( "results", results);
		
		debugOut("CallSessionTag.doTag()  jspFragment1: " + jspFragment1);
		jspFragment1.invoke( null );

    }
    
    public String getStackTrace(Throwable aThrowable) {
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    aThrowable.printStackTrace(printWriter);
	    return result.toString();
	  }

  
//  Setter method for fragment type attribute - Fragement1
    public void setJspFragment1( JspFragment jspFragment1 ) {
      this.jspFragment1 = jspFragment1;
      debugOut("CallSessionTag.setJspFragment1()  jspFragment1: " + jspFragment1);
      
    }
    
}
