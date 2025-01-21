package websession.client1.lite;

import irwwbase.ExtendedRandom;
import irwwbase.IRWWBase;
import irwwbase.MaxValues;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
  
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jakarta.servlet.annotation.WebServlet;


/**
 * Servlet implementation class WebClient1Servlet
 */
@WebServlet("/WebClient1Servlet")
public class WebClient1Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String classname = "WebClient1Servlet";
	private IRWWBase irwwBase = new IRWWBase();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WebClient1Servlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		performTask(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		performTask(request, response);
	}
	
	public void performTask(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		
			irwwBase.debugOut("<<< Entering WebClient1Servlet");
			
			//***************************************************************
			//* Setup
			//***************************************************************
			
			HttpSession aHttpSession = null;
			String urlRewriteOrCookies = null;
			String currentItemIdString = null;
			String currentItemPriceString = null;
			String previousItemId = null;
			String previousItemPrice = null;
			ExtendedRandom rand = new ExtendedRandom();
			byte databaseSize = irwwBase.dbSize();
			
			int itemId = 0;
			double newPrice = 0; 
			//Comment out unused variables
			//int currentItemId = 0;
			//double currentItemPrice = 0;
					
			java.text.NumberFormat n = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US);		
			
			//***************************************************************
			//* Get Effective Session Tracking Mode
			//***************************************************************
			
			irwwBase.debugOut("<<< Before getEffectiveSessionTrackingModes()is called");
			try {
				if (request.getServletContext().getEffectiveSessionTrackingModes().contains(SessionTrackingMode.COOKIE)){
					urlRewriteOrCookies = "cookies";
				}		
				if (request.getServletContext().getEffectiveSessionTrackingModes().contains(SessionTrackingMode.URL)){
					urlRewriteOrCookies = "url";
				}		
			} catch (Throwable t) {
				printOut("<<< Error: getEffectiveSessionTrackingModes() failed");
				t.printStackTrace();
				throw new ServletException("<<< Error: getEffectiveSessionTrackingModes() failed", t);
			}
			irwwBase.debugOut("<<< After urlRewriteOrCookies is obtained from getEffectiveSessionTrackingModes() = "+ urlRewriteOrCookies);
					
			//***************************************************************
			//* Run PriceChange & set current variables
			//***************************************************************

			try {
				itemId = rand.nextInt(1, MaxValues.itemId(databaseSize));
				irwwBase.debugOut("generatePriceChangeInput - itemId = " + itemId + " databaseSize = "	+ databaseSize);
				newPrice = rand.nextDouble(MaxValues.itemPrice(databaseSize));
				if (newPrice >= 1000){
					newPrice = 999.99;
				}
				irwwBase.debugOut("generatePriceChangeInput - newPrice = " + newPrice);
			} catch (Throwable t) {
				printOut("<<< Error: generate Price Change Input failed");
				t.printStackTrace();
				throw new ServletException("Error: generate Price Change Input failed", t);
			}

			try {
				currentItemIdString = String.valueOf(itemId);
				currentItemPriceString = n.format(newPrice);
			} catch (Throwable t) {
				printOut("<<< Error:  getItemId() or getPrice() failed");
				t.printStackTrace();
				throw new ServletException("Error:  getItemId() or getPrice() failed", t);
			}

			irwwBase.debugOut("<<< WebClient1Servlet: current Item Id = " + currentItemIdString + " current Item Price = " + currentItemPriceString);

			//***************************************************************
			//* Check status of HTTP Session
			//***************************************************************
			
			irwwBase.debugOut("<<< Get a handle to the HTTP Session");
			try {
				aHttpSession = request.getSession(false);
			} catch (Throwable t) {
				printOut("<<< Error:  request.getSession(false) failed");
				t.printStackTrace();
				throw new ServletException("Error:  request.getSession(false) failed", t);
			}
			irwwBase.debugOut("<<< Got a handle to the session - aHttpSession");
			
			//if the HTTP session doesn't exist
			if (aHttpSession == null) {
				irwwBase.debugOut("<<< The http session session does not exist");
				aHttpSession = this.createHTTPSession(request, response, aHttpSession);
				if (urlRewriteOrCookies.startsWith("cookies")) {
					this.setCurrentItemAttributes(response, aHttpSession, currentItemIdString, currentItemPriceString);	
					this.setCurrentItemCookie(response, currentItemIdString);
					this.dispatchNextPage(request, response);
				}
				if (urlRewriteOrCookies.startsWith("url")) {
					this.setCurrentItemAttributes(response, aHttpSession, currentItemIdString, currentItemPriceString);	
					this.processUrlPath(request ,response, currentItemIdString, currentItemPriceString, previousItemId, previousItemPrice );					
				}
			} else {	
				// if HTTP Session does exist
				if (aHttpSession != null) {
					irwwBase.debugOut("<<< The http session session does exist");
					if (aHttpSession.getAttribute("itemId")==null){
						irwwBase.debugOut("<<< The http session does not contain session attributes");
						if (urlRewriteOrCookies.startsWith("cookies")) {
							this.setCurrentItemAttributes(response, aHttpSession, currentItemIdString, currentItemPriceString);	
							this.setCurrentItemCookie(response, currentItemIdString);
							this.dispatchNextPage(request, response);
						}
						if (urlRewriteOrCookies.startsWith("url")) {
							this.setCurrentItemAttributes(response, aHttpSession, currentItemIdString, currentItemPriceString);	
							this.processUrlPath(request, response, currentItemIdString, currentItemPriceString, previousItemId, previousItemPrice);					
						}
					} else {				
						try {				
							irwwBase.debugOut("<<< First get the previous itemItemId and the previous itemPrice attributes from an existing session");
							previousItemId = (String) aHttpSession.getAttribute("itemId");
							previousItemPrice =	(String) aHttpSession.getAttribute("itemPrice");
							irwwBase.debugOut("<<< Got the itemItemId " + previousItemId + " and itemPrice " + previousItemPrice	+ " attributes from an existing session");	
							
							irwwBase.debugOut("<<< Then set the Attributes for previous ItemId and previous ItemPrice");
							aHttpSession.setAttribute("previousItemId", previousItemId);
							aHttpSession.setAttribute("previousItemPrice", previousItemPrice);
							irwwBase.debugOut("<<< After setAttribute for previous ItemId and previous ItemPrice");						
							
							irwwBase.debugOut("<<< Then set the Attributes for current ItemId and current ItemPrice");
							aHttpSession.setAttribute("itemId", currentItemIdString);
							aHttpSession.setAttribute("itemPrice", currentItemPriceString);
							irwwBase.debugOut("<<< After setAttribute for current ItemId and current ItemPrice");
						} catch (Throwable t) {
							printOut("<<< Error:  get.Attributes failed");
							t.printStackTrace();
							throw new ServletException("Error:  get.Attributes failed", t);
						}
		
						irwwBase.debugOut("<<< urlRewriteOrCookies = " + urlRewriteOrCookies);
						if (urlRewriteOrCookies.startsWith("cookies")) {
							this.processCookiePath(request, response, aHttpSession, currentItemIdString, currentItemPriceString, previousItemId, previousItemPrice);	
							this.dispatchNextPage(request, response);
						} 
						
						if (urlRewriteOrCookies.startsWith("url")) {
							this.processUrlPath(request, response, currentItemIdString, currentItemPriceString, previousItemId, previousItemPrice);					
						} 					
					}
					/* Comment out dead code
					if (urlRewriteOrCookies==null){
						System.out.println("<<< Error: In " + classname + ": urlRewriteOrCookies is null ");
						throw new RuntimeException("<<< Error: In " + classname + ": urlRewriteOrCookies is null ");	
					}
					*/
				}
			}
		}
	
	public HttpSession createHTTPSession(HttpServletRequest request,HttpServletResponse response, HttpSession aHttpSession)
		       throws ServletException, IOException {
		    try {
				irwwBase.debugOut("<<< Getting a new session & setting the itemId and itemPrice attributes into the HTTP session");
				aHttpSession = request.getSession(true);			
			} catch (Throwable t) {
				printOut("<<< Error:  request.getSession(true) failed");
				t.printStackTrace();
				throw new ServletException("Error:  request.getSession(true) failed", t);
			} 
			return aHttpSession;
		}
		
		public void setCurrentItemAttributes(HttpServletResponse response, HttpSession aHttpSession, String currentItemIdString, String currentItemPriceString)
				throws ServletException, IOException {	
			
			try {
				aHttpSession.setAttribute("itemId", currentItemIdString);
				aHttpSession.setAttribute("itemPrice", currentItemPriceString);
				irwwBase.debugOut("<<< set itemId of " + currentItemIdString	+ " and itemPrice of " + currentItemPriceString	+ " in new session");
			} catch (Throwable t) {			
				printOut("<<< Error:  setAttributes failed");
				t.printStackTrace();
				throw new ServletException("Error:  setAttributes failed", t);
			}
		} 
		
		public void setCurrentItemCookie(HttpServletResponse response, String currentItemIdString)
				throws ServletException, IOException {	
			
			Cookie itemCookie = null;
				
			try {
				irwwBase.debugOut("<<< Create an itemCookie with "	+ currentItemIdString + " & set its path");
				itemCookie = new Cookie("itemCookie", currentItemIdString);
				itemCookie.setPath("/");

	     	    irwwBase.debugOut("<<< Send the itemCookie to the browser");
		    	response.addCookie(itemCookie);
			} catch (Throwable t) {
				printOut("<<< Error: creating itemCookie or sending the cookie to the browser failed");
			    t.printStackTrace();
			    throw new ServletException("Error: creating itemCookie or sending the cookie to the browser failed", t);
		    }
		}

		public void processCookiePath(HttpServletRequest request, HttpServletResponse response, HttpSession aHttpSession, String currentItemIdString, 
				String currentItemPriceString, String previousItemId, String previousItemPrice)
				throws ServletException, IOException {
			
			String previousCookieItemId = null;
			String cookieJSESSIONID = null;
			Cookie[] allCookies = null;
			Cookie itemCookie = null;
			Cookie JSESSIONIDcookie = null;
			
			try {
				irwwBase.debugOut("<<< Retrieve the itemCookie from the browser and get the itemId from the itemCookie");
				allCookies= request.getCookies();
			} catch (Throwable t) {
				printOut("<<< Error: request.getCookies()");
				t.printStackTrace();
				throw new ServletException("Error: request.getCookies()", t);
			}

			if (allCookies!=null){					
				for (int i = 0; i < allCookies.length; i++) {
					irwwBase.debugOut("<<< Before the if statement to see if itemCookie is in the list of cookies");
					if (allCookies[i].getName().compareTo("itemCookie") == 0) {
						try {
							itemCookie = allCookies[i];
							irwwBase.debugOut("<<< itemCookie = " + itemCookie);
						} catch (Throwable t) {
							printOut("<<< Error: itemCookie = allCookies[i]");
							t.printStackTrace();
							throw new ServletException("Error: itemCookie = allCookies[i]", t);
						}
					}
					irwwBase.debugOut("<<< Before the if statement to see if JSESSIONID is in the list of cookies");
					if (allCookies[i].getName().compareTo("JSESSIONID") == 0) {
						try {
							JSESSIONIDcookie = allCookies[i];
							irwwBase.debugOut("<<< JSESSIONIDcookie = " + JSESSIONIDcookie);
						} catch (Throwable t) {
							printOut("<<< Error: JESSIONIDcookie = allCookies[i]");
							t.printStackTrace();
							throw new ServletException("Error: JESSIONIDcookie = allCookies[i]", t);
						}
					}
				}
				
				try {
					irwwBase.debugOut("<<< Before itemCookie.getValue()");
					previousCookieItemId = itemCookie.getValue();
					aHttpSession.setAttribute("previousItemCookie",	previousCookieItemId);
				} catch (Throwable t) {
						printOut("<<< Error: itemCookie.getValue()");
						t.printStackTrace();
						throw new ServletException("Error: itemCookie.getValue()", t);
				}

				try {
					irwwBase.debugOut("<<< Before JSESSIONIDcookie.getValue()");
					cookieJSESSIONID = JSESSIONIDcookie.getValue();
					aHttpSession.setAttribute("JSESSIONIDvalue", cookieJSESSIONID);
				} catch (Throwable t) {
					printOut("<<< Error: JSESSIONIDcookie.getValue()");
					t.printStackTrace();
					throw new ServletException("Error: JSESSIONIDcookie.getValue()", t);
				}

				irwwBase.debugOut("<<< Previous itemId = " + previousItemId
					+ " Previous itemPrice = "	+ previousItemPrice
					+ " Previous cookieItemId = " + previousCookieItemId
					+ "cookieJSESSIONID = "	+ cookieJSESSIONID);

				if (previousItemId.compareTo(previousCookieItemId) == 0) {
					displayOut("<<< The itemId from the cookie and the session "	+ cookieJSESSIONID	+ " match from the previous run");
				} else {
					displayOut("<<< Error:  The itemId from the cookie and the session do not match");
				}
			}

			try {
				irwwBase.debugOut("<<< Reset the itemId and itemPrice attributes based upon the values generated for this run: "
						+ currentItemIdString + " "	+ currentItemPriceString);
				aHttpSession.setAttribute("itemId", currentItemIdString);
				aHttpSession.setAttribute("itemPrice", currentItemPriceString);
			} catch (Throwable t) {
				printOut("<<< Error:  set.Attributes failed");
				t.printStackTrace();
				throw new ServletException("Error:  set.Attributes failed", t);
			}

			try {
				irwwBase.debugOut("<<< Reset itemCookie with the value generated in this run: "	+ currentItemIdString + "set its path");
				itemCookie.setValue(currentItemIdString);
				itemCookie.setPath("/");
				response.addCookie(itemCookie);
			} catch (Throwable t) {
				printOut("<<< Error: itemCookie.setValue() or send cookie to browser");
				t.printStackTrace();
				throw new ServletException("Error: itemCookie.setValue() or send cookie to browser", t);
			}
		}
		
		public void processUrlPath(HttpServletRequest request, HttpServletResponse response, String currentItemId, 	String currentItemPrice, 
				String previousItemId, String previousItemPrice)
				throws ServletException, IOException {
					
			PrintWriter printWriter = null;		
			String reportTitle = "Web Client 1 - URL Rewriting Results";
			String Servlet1Name = "WebClient1Servlet";
			String Servlet2Name = "WebClient2Servlet";
			
			irwwBase.debugOut("<<< If urlRewrite == true, then use the print writer");
			response.setContentType("text/html");
			printWriter = response.getWriter();

			printWriter.println("<html>");
			printWriter.println("<head><title>" + reportTitle + "</title></head>");
			printWriter.println("<style type='text/css'>");
			printWriter.println("BODY { background-color: #f8f7cd; }");
			printWriter.println("</style>");
			printWriter.println("<BODY>");
			printWriter.println("<h1>Client1 - URL Rewriting Results page</font></h1>");

			if (previousItemId != null) {
				irwwBase.debugOut("<<< Before if(previousItemId.compareTo(request.getQueryString()) == 0)");
				irwwBase.debugOut("<<< request.getQueryString = " + request.getQueryString());
				irwwBase.debugOut("<<< itemId=" + previousItemId); 
				if ((request.getQueryString() != null)){ 
					if (("itemId=" + previousItemId).compareTo(request.getQueryString())== 0) {	
						printWriter.println("<h1>SUCCESS: The itemId from the url and the session match from the previous run </h1> ");
						displayOut("<<< SUCCESS: The itemId from the url and the session match from the previous run ");
					} else { 
						printWriter.println("<h1>ERROR: The itemId from the url and the session do not match from the previous run </h1> ");
						displayOut("<<< Failure:  The itemId from the url and the session do not match");
					}
				}
			}

			printWriter.println("<h2>Web Client 1:  You must use the following link for this test. Do NOT use the browser reload button.</h2>");
			irwwBase.debugOut("<<< request.getRequestURI()" + request.getRequestURI() + "?itemId=" + currentItemId);
			printWriter.println("<A href=\"" + response.encodeURL(request.getRequestURI() + "?itemId=" + currentItemId)
					+ "\">Reload " + Servlet1Name + "</A>");
			printWriter.println("<h2>Web Client 2:  You must use the following link for this test. Do NOT use the browser reload button.</h2>");
			printWriter.println("<A href=\"" + response.encodeURL("/WebSessionClient2Lite/WebClient2Servlet"	+ "?itemId=" + currentItemId)
					+ "\">Reload " + Servlet2Name + "</A>");
			printWriter.println("<h2>_________________________________________________________</h2>");
			printWriter.println("<h2>Information Generated from Previous Browser Invocation: </h2>");
			printWriter.println("<h2>itemId=" + previousItemId + " </h2>");
			printWriter.println("<h3>(from Session)</h3>");
			printWriter.println("<h2>" + request.getQueryString() + " </h2>");
			printWriter.println("<h3>(from URL request.getQueryString())</h3>");
			printWriter.println("<h2>itemPrice=" + previousItemPrice + " </h2>");
			printWriter.println("<h2>_________________________________________________________</h2>");
			printWriter.println("<h2>Information Generated from Current Browser Invocation: </h2>");
			printWriter.println("<h2>itemId=" + currentItemId + " </h2>");
			printWriter.println("<h2>itemPrice=" + currentItemPrice + " </h2>");	
			printWriter.println("<h2>_________________________________________________________</h2>");
		}
		
		public void dispatch(
				HttpServletRequest request,
				HttpServletResponse response,
				String nextPage)
				throws ServletException, IOException {
				RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
				dispatch.forward(request, response);
			}
		
		public void dispatchNextPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			String nextPage;
			try {
				irwwBase.debugOut("<<< Get the web page associated with the command in the request");
				nextPage = getInitParameter(request.getParameter("command"));
				nextPage = "WebClient1ResultsForm.jsp";
				irwwBase.debugOut("<<< nextPage = " + nextPage);
			} catch (Exception ex) {
				printOut("<<< Error: an exception is thrown serve the error page");
				nextPage = "/error.jsp";
			}
			irwwBase.debugOut("<<< Before dispatch");
			dispatch(request, response, nextPage);	
		}
	
	public void printOut(String message) {
		String ts = (new Timestamp(System.currentTimeMillis())).toString();
		String tn = Thread.currentThread().getName();
		System.out.println ("<<< " + classname + ": " + ts + " " + tn + " " + message);
	}
	
	public void displayOut(String message) {
		String ts = (new Timestamp(System.currentTimeMillis())).toString();
		String tn = Thread.currentThread().getName();
		irwwBase.displayOut ("<<< " + classname + ": " + ts + " " + tn + " " + message);
	}
	

}
