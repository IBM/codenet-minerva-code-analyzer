package webejbchecker;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import irwwbase.EJBAsyncMethodStatsBean;
import irwwbase.EJBNPTimerStatsBean;
import irwwbase.EJBPersistentTimerStatsBean;
import irwwbase.UserException;

import startupandtimer.TimerControllerLocal;


/**
 * Servlet implementation class Validator
 */
public class Validator extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	@EJB(name="EJBAsyncMethodStatsBean")
	EJBAsyncMethodStatsBean theEJBAsyncMethodStatsBean;
	
	@EJB(name="EJBNPTimerStatsBean")
	EJBNPTimerStatsBean theEJBNPTimerStatsBean;
	
	@EJB(name="EJBPersistentTimerStatsBean")	
	EJBPersistentTimerStatsBean theEJBPersistentTimerStatsBean;

	@EJB(name="TimerControllerBean")
	TimerControllerLocal theTimerControllerBean;
	     
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Validator() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String buttonSelection = request.getParameter("command");
		PrintWriter out = response.getWriter();
		
		if (buttonSelection.equalsIgnoreCase("Asynchronous Methods Validation")) {
			
			// check if the asynchronous fire-and-forget method counters are consistent
			boolean asyncMethodsValidated = theEJBAsyncMethodStatsBean.validateFireAndForgetMethods();
			
			// grab the the raw counter data
			long asyncMethodsCalledCounter = theEJBAsyncMethodStatsBean.getMethodsCalledCounter();
			long asyncMethodsExecutedCounter = theEJBAsyncMethodStatsBean.getMethodsExecutedCounter();			
			
			// Format the results to the web page
			String title = "EJB Asynchronous Fire-And-Forget Methods";
			out.println("<HTML><HEAD><TITLE>");
			out.println(title);
			out.println("</TITLE></HEAD><BODY bgcolor='#f8f7cd'>");
			out.println("<H3 align=\"left\">" + title + "</H3>");	
			out.println("<H4 align=\"left\">(application self-checking results)</H4>");	
			

			String asyncResult = "";
			if (asyncMethodsValidated){
				asyncResult = "SUCCESSFUL";
			} else {
				asyncResult = "FAILED";		 		
			}
			
			out.println("<HR size=\"3\"><TABLE border=\"8\" bordercolor=\"#ffcc99\" cellpadding=\"2\"><TBODY>");
			out.println("<TR>");
			out.println("<TH>Result</TH>");
			out.println("<TD>" + asyncResult + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Methods Called Counter</TH>");
			out.println("<TD>" + asyncMethodsCalledCounter + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Methods Executed Counter</TH>");
			out.println("<TD>" + asyncMethodsExecutedCounter + "</TD>");
			out.println("</TR>");
			out.println("</TBODY></TABLE></BODY></HTML>");

		
		} else if (buttonSelection.equalsIgnoreCase("Non-persistent Timers Validation") ) {
				
			// get auto timer results
			boolean autoTimerValidated = theEJBNPTimerStatsBean.validateAutoTimer();
			long autoTimerCalculatedTickCount = theEJBNPTimerStatsBean.getAutoTimerCalculatedTickCount();
			long autoTimerActualTickCount = theEJBNPTimerStatsBean.getAutoTimerTickCount();
			String autoTimerExceptionData = theEJBNPTimerStatsBean.getAutoTimerExceptionData();
			String autoTimerInfoMessage = theEJBNPTimerStatsBean.getAutoTimerInfoMessage();
			
			// get calendar timer results
			boolean calendarTimerValidated = theEJBNPTimerStatsBean.validateCalendarTimer();
			long calendarTimerCalculatedTickCount = theEJBNPTimerStatsBean.getCalendarTimerCalculatedTickCount();
			long calendarTimerActualTickCount = theEJBNPTimerStatsBean.getCalendarTimerTickCount();
			String calendarTimerExceptionData = theEJBNPTimerStatsBean.getCalendarTimerExceptionData();
			String calendarTimerInfoMessage = theEJBNPTimerStatsBean.getCalendarTimerInfoMessage();
			
			// get FAT timer results
			// int FATNPTimerCounter = theFATProgrammaticNPTimer31Bean.getTickCount();
			
			// Format the results to the web page
			String title = "EJB Non-persistent Timers Validation";
			out.println("<HTML><HEAD><TITLE>");
			out.println(title);
			out.println("</TITLE></HEAD><BODY bgcolor='#f8f7cd'>");
			out.println("<H3 align=\"left\">" + title + "</H3>");	
			out.println("<H4 align=\"left\">(application self-checking results)</H4>");

			// display auto timer results		
			String autoTimerResult = "";
			
			// To pass the timer must be validated, and we can't have had any exceptions during the stock level
			// transactions that it runs.
			if ((autoTimerValidated) && (!theEJBNPTimerStatsBean.getAutoTimerFirstExceptionCaught() )) {
				autoTimerResult = "SUCCESSFUL";
			} else {
				autoTimerResult = "FAILED";		 		
			}	
			out.println("<HR size=\"3\">");
			out.println("<TABLE border=\"8\" bordercolor=\"#ffcc99\" cellpadding=\"2\"> <TBODY>");
			out.println("<CAPTION>Automatic timer results</CAPTION>");
			out.println("<TR>");
			out.println("<TH>Result</TH>");
			out.println("<TD>" + autoTimerResult + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Calculated Tick Count</TH>");
			out.println("<TD>" + autoTimerCalculatedTickCount + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Actual Tick Count</TH>");
			out.println("<TD>" + autoTimerActualTickCount + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Exception</TH>");
			out.println("<TD>" + autoTimerExceptionData + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Additional Info</TH>");
			out.println("<TD>" + autoTimerInfoMessage + "</TD>");
			out.println("</TR>");
			out.println("</TBODY></TABLE>");
			
			
			// display calendar timer results	
			String calendarTimerResult = "";
			
			// To pass the timer must be validated, and we can't have had any exceptions during the stock level
			// transactions that it runs.			
			if ((calendarTimerValidated) && (!theEJBNPTimerStatsBean.getCalendarTimerFirstExceptionCaught() )) {
				calendarTimerResult = "SUCCESSFUL";
			} else {
				calendarTimerResult = "FAILED";		 		
			}
			
			out.println("<HR size=\"3\">"); 
			out.println("<TABLE border=\"8\" bordercolor=\"#ffcc99\" cellpadding=\"2\"> <TBODY>");
			out.println("<CAPTION>Programmatic timer results</CAPTION>");
			out.println("<TR>");
			out.println("<TH>Result</TH>");
			out.println("<TD>" + calendarTimerResult + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Calculated Tick Count</TH>");
			out.println("<TD>" + calendarTimerCalculatedTickCount + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Actual Tick Count</TH>");
			out.println("<TD>" + calendarTimerActualTickCount + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Exception</TH>");
			out.println("<TD>" + calendarTimerExceptionData + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Additional Info</TH>");
			out.println("<TD>" + calendarTimerInfoMessage + "</TD>");
			out.println("</TR>");
			out.println("</TBODY></TABLE>");
			
				
			// end formatting			
			out.println("</BODY></HTML>");
			
		} else if (buttonSelection.equalsIgnoreCase("Persistent Timers Validation") ) {
			
			// get results from the three persistent timers
			boolean persistentTimer1Validated = theEJBPersistentTimerStatsBean.validatePersistentTimer1();
			long persistentTimer1CalculatedTickCount = theEJBPersistentTimerStatsBean.getTimer1CalculatedTickCount();
			long persistentTimer1ActualTickCount = theEJBPersistentTimerStatsBean.getTimer1TickCount();
			String timer1ExceptionData = theEJBPersistentTimerStatsBean.getTimer1ExceptionData();
			String timer1InfoMessage = theEJBPersistentTimerStatsBean.getTimer1InfoMessage();
			boolean persistentTimer2Validated = theEJBPersistentTimerStatsBean.validatePersistentTimer2();
			long persistentTimer2CalculatedTickCount = theEJBPersistentTimerStatsBean.getTimer2CalculatedTickCount();
			long persistentTimer2ActualTickCount = theEJBPersistentTimerStatsBean.getTimer2TickCount();
			String timer2ExceptionData = theEJBPersistentTimerStatsBean.getTimer2ExceptionData();
			String timer2InfoMessage = theEJBPersistentTimerStatsBean.getTimer2InfoMessage();
			boolean persistentTimer3Validated = theEJBPersistentTimerStatsBean.validatePersistentTimer3();
			long persistentTimer3CalculatedTickCount = theEJBPersistentTimerStatsBean.getTimer3CalculatedTickCount();
			long persistentTimer3ActualTickCount = theEJBPersistentTimerStatsBean.getTimer3TickCount();
			String timer3ExceptionData = theEJBPersistentTimerStatsBean.getTimer3ExceptionData();
			String timer3InfoMessage = theEJBPersistentTimerStatsBean.getTimer3InfoMessage();
			
			// Format the results to the web page
			String title = "EJB Persistent Timers Validation";
			out.println("<HTML><HEAD><TITLE>");
			out.println(title);
			out.println("</TITLE></HEAD><BODY bgcolor='#f8f7cd'>");
			out.println("<H3 align=\"left\">" + title + "</H3>");	
			out.println("<H4 align=\"left\">(application self-checking results)</H4>");

			// display timer1 results		
			String persistentTimer1Result = "";
			if (persistentTimer1Validated){
				persistentTimer1Result = "SUCCESSFUL";
			} else {
				persistentTimer1Result = "FAILED";		 		
			}	
			out.println("<HR size=\"3\">");
			out.println("<TABLE border=\"8\" bordercolor=\"#ffcc99\" cellpadding=\"2\"> <TBODY>");
			out.println("<CAPTION>Persistent timer1 results</CAPTION>");
			out.println("<TR>");
			out.println("<TH>Result</TH>");
			out.println("<TD>" + persistentTimer1Result + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Calculated Tick Count</TH>");
			out.println("<TD>" + persistentTimer1CalculatedTickCount + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Actual Tick Count</TH>");
			out.println("<TD>" + persistentTimer1ActualTickCount + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Exception</TH>");
			out.println("<TD>" + timer1ExceptionData + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Additional Info</TH>");
			out.println("<TD>" + timer1InfoMessage + "</TD>");
			out.println("</TR>");
			out.println("</TBODY></TABLE>");	
			
			// display timer2 results		
			String persistentTimer2Result = "";
			if (persistentTimer2Validated){
				persistentTimer2Result = "SUCCESSFUL";
			} else {
				persistentTimer2Result = "FAILED";		 		
			}	
			out.println("<HR size=\"3\">");
			out.println("<TABLE border=\"8\" bordercolor=\"#ffcc99\" cellpadding=\"2\"> <TBODY>");
			out.println("<CAPTION>Persistent timer2 results</CAPTION>");
			out.println("<TR>");
			out.println("<TH>Result</TH>");
			out.println("<TD>" + persistentTimer2Result + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Calculated Tick Count</TH>");
			out.println("<TD>" + persistentTimer2CalculatedTickCount + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Actual Tick Count</TH>");
			out.println("<TD>" + persistentTimer2ActualTickCount + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Exception</TH>");
			out.println("<TD>" + timer2ExceptionData + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Additional Info</TH>");
			out.println("<TD>" + timer2InfoMessage + "</TD>");
			out.println("</TR>");
			out.println("</TBODY></TABLE>");
			
			// display timer3 results		
			String persistentTimer3Result = "";
			if (persistentTimer3Validated){
				persistentTimer3Result = "SUCCESSFUL";
			} else {
				persistentTimer3Result = "FAILED";		 		
			}	
			out.println("<HR size=\"3\">");
			out.println("<TABLE border=\"8\" bordercolor=\"#ffcc99\" cellpadding=\"2\"> <TBODY>");
			out.println("<CAPTION>Persistent timer3 results</CAPTION>");
			out.println("<TR>");
			out.println("<TH>Result</TH>");
			out.println("<TD>" + persistentTimer3Result + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Calculated Tick Count</TH>");
			out.println("<TD>" + persistentTimer3CalculatedTickCount + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Actual Tick Count</TH>");
			out.println("<TD>" + persistentTimer3ActualTickCount + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Exception</TH>");
			out.println("<TD>" + timer3ExceptionData + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Additional Info</TH>");
			out.println("<TD>" + timer3InfoMessage + "</TD>");
			out.println("</TR>");
			out.println("</TBODY></TABLE>");		
			
			// end formatting			
			out.println("</BODY></HTML>");
						
			
		} else if (buttonSelection.equalsIgnoreCase("Cancel All Timers") ) {
			
			String cancelResult = "FAILED";
			String statusMessage = "There were no active timers found";
			int numTimers = 0;
			
			try {
				numTimers = theTimerControllerBean.cancelAllTimers();
				
				// Tell the statistics counter beans to stop counting now.
				theEJBNPTimerStatsBean.setCalculateTickCounts(false);
				theEJBPersistentTimerStatsBean.setCalculateTickCounts(false);
				
			} catch (UserException e) {
				System.out.println(e.get_infostring());
				e.printStackTrace();
			}
			
			if(numTimers>0){
				cancelResult = "SUCCESSFUL";
				statusMessage = "All active timers were cancelled.";
			}
			
			// Format the results to the web page
			String title = "EJB Timers Cancel";
			out.println("<HTML><HEAD><TITLE>");
			out.println(title);
			out.println("</TITLE></HEAD><BODY bgcolor='#f8f7cd'>");
			out.println("<H3 align=\"left\">" + title + "</H3>");	

			out.println("<HR size=\"2\"><TABLE border=\"8\" bordercolor=\"#ffcc99\" cellpadding=\"2\"><TBODY>");
			out.println("<TR>");
			out.println("<TH>Result</TH>");
			out.println("<TD>" + cancelResult + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Number of timers</TH>");
			out.println("<TD>" + numTimers + "</TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TH>Status</TH>");
			out.println("<TD>" + statusMessage + "</TD>");
			out.println("</TR>");
			out.println("</TBODY></TABLE></BODY></HTML>");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}

}
