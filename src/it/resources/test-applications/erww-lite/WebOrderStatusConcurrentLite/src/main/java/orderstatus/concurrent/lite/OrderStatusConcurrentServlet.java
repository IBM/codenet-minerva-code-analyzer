package orderstatus.concurrent.lite;

import irwwbase.IRWWBase;

import java.io.IOException;

import jakarta.annotation.security.DeclareRoles;
import javax.naming.InitialContext;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import orderstatus.concurrent.ejb.lite.OrderStatusConcurrencyControllerBean;
import orderstatus.concurrent.ejb.lite.OrderStatusConcurrentInput;
import orderstatus.concurrent.ejb.lite.OrderStatusConcurrentOutput;


/**
 * Servlet implementation class OrderStatusConcurrentServlet
 */
@WebServlet(name="OrderStatusConcurrentServlet", urlPatterns={"/OrderStatusConcurrentServlet"})
@DeclareRoles("CustomerRole")
@ServletSecurity(value=@HttpConstraint(rolesAllowed={"CustomerRole"}))
public class OrderStatusConcurrentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	IRWWBase ib = new IRWWBase();
	OrderStatusConcurrencyControllerBean controllerBean = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrderStatusConcurrentServlet() {
		super();

		// lookup the order status concurrency controller singleton bean in the app name space.
		try {
			InitialContext ic = new InitialContext();
			controllerBean = (OrderStatusConcurrencyControllerBean) ic.lookup("java:app/OrderStatusConcurrentEJBLite/OrderStatusConcurrencyControllerBean");
		} catch (Throwable t) {
			System.out.println("OrderStatusConcurrentServlet ctor caught throwable: " + t);
			t.printStackTrace();
		}		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		OrderStatusConcurrentOutput output = null;			

		try {
			ib.debugOut("Enter OrderStatusConcurrentServlet.doGet()");
			
			OrderStatusConcurrentInput input = new OrderStatusConcurrentInput();
			output = new OrderStatusConcurrentOutput();
			Boolean inputValid = true;
			String inputInvalidMessage = "Error: Invalid Input. <br />";

			// load input object with values from input web page	
			String button = request.getParameter("command");	
			input.setNumberOfClients(Integer.valueOf(request.getParameter("numberOfClients")));
			input.setClientStartupInterval(Integer.valueOf(request.getParameter("clientStartupInterval")));
			input.setMaximumClientTransactionWaitTime(Integer.valueOf(request.getParameter("maximumClientTransactionWaitTime")));
			input.setMinimumClientTransactionWaitTime(Integer.valueOf(request.getParameter("minimumClientTransactionWaitTime")));
			input.setTestRunDuration(Integer.valueOf(request.getParameter("testRunDuration")));

			// First, if the controller already has work running, we should not be trying to start new work
			if (controllerBean.isWorkAlreadyRunning()) {
				output.setStatus("FAILED");
				output.setMessage("You may not start a new work if work is already running.");
				ib.debugOut("Attempt was made to start work when work was already running.  This is not allowed.");
				
			} else {


				// Validate the input from the web page
				if(Integer.valueOf(request.getParameter("numberOfClients")) > 500 || Integer.valueOf(request.getParameter("numberOfClients"))<1){
					inputValid = false;
					inputInvalidMessage = inputInvalidMessage + "Number of clients (" + Integer.valueOf(request.getParameter("numberOfClients")) 
							+ ") must be between 1 and 500 clients inclusive. <br />";
				}

				if(Integer.valueOf(request.getParameter("clientStartupInterval")) > 100 || Integer.valueOf(request.getParameter("clientStartupInterval"))<1){
					inputValid = false;
					inputInvalidMessage = inputInvalidMessage + "Startup interval (" + Integer.valueOf(request.getParameter("clientStartupInterval")) 
							+ ") must be between 1 and 100 milliseconds inclusive. <br />";
				}

				if(Integer.valueOf(request.getParameter("minimumClientTransactionWaitTime")) > 5000 || 
						Integer.valueOf(request.getParameter("minimumClientTransactionWaitTime"))<100){
					inputValid = false;
					inputInvalidMessage = inputInvalidMessage + "Minimum wait time (" + Integer.valueOf(request.getParameter("minimumClientTransactionWaitTime")) 
							+ ") must be between 100 and 5000 milliseconds inclusive. <br />";
				}

				if(Integer.valueOf(request.getParameter("maximumClientTransactionWaitTime")) > 60000 || 
						Integer.valueOf(request.getParameter("maximumClientTransactionWaitTime"))<1000){
					inputValid = false;
					inputInvalidMessage = inputInvalidMessage + "Maximum wait time (" + Integer.valueOf(request.getParameter("maximumClientTransactionWaitTime")) 
							+ ") must be between 1000 and 60000 milliseconds inclusive. <br />";
				}			

				if(Integer.valueOf(request.getParameter("testRunDuration")) > 10080 || 
						Integer.valueOf(request.getParameter("testRunDuration"))<1){
					inputValid = false;
					inputInvalidMessage = inputInvalidMessage + "Test run duration (" + Integer.valueOf(request.getParameter("testRunDuration")) 
							+ ") must be between 1 and 10080 minutes inclusive.";
				}	

				// Also, Max Transaction wait time should never be less than Min Transaction wait time
				if(Integer.valueOf(request.getParameter("maximumClientTransactionWaitTime")) < Integer.valueOf(request.getParameter("minimumClientTransactionWaitTime"))) {
					inputValid = false;
					inputInvalidMessage = inputInvalidMessage + "Minimum wait time between transactions must be equal to, or less than, maximum wait time between transactions."; 
				}


				if (button.trim().equalsIgnoreCase("StartTest") && inputValid)	{

					// This is the normal, good path

					ib.debugOut("numberOfClients: " + input.getNumberOfClients());
					ib.debugOut("clientStartupInterval: " + input.getClientStartupInterval());
					ib.debugOut("minimumClientTransactionWaitTime: " + input.getMinimumClientTransactionWaitTime());
					ib.debugOut("maximumClientTransactionWaitTime: " + input.getMaximumClientTransactionWaitTime());
					ib.debugOut("testRunDuration: " + input.getTestRunDuration());

					// Go schedule all the requested work
					output = controllerBean.startWork(input);			

				} else if (!inputValid){
					output.setStatus("FAILED");
					output.setMessage(inputInvalidMessage);
					ib.debugOut(inputInvalidMessage.replace("<br />", "\n"));
				} else {
					// unexpected error - command button pressed was not "StartTest".
					output.setStatus("FAILED");
					output.setMessage("Button selected was not 'StartTest'");
					System.out.println("Button selected was not 'StartTest'");
				}

			}

		} catch(Throwable t) {
			System.out.println ("OrderStatusConcurrentServlet caught exeption: " + t);
			t.printStackTrace();
			output.setStatus("FAILED");
			output.setMessage("OrderStatusConcurrentServlet caught unexpected exeption");
			output.setException(t.getMessage());
			output.setStack(ib.getStackTrace(t));
			
		} finally {
			ib.debugOut("Exiting OrderStatusConcurrentServlet.doGet()");
		}
		

		// Display the results page, passing the output on the session object
		try {
			HttpSession sess = request.getSession();
			sess.setAttribute("outputBean", output);
			String nextPage = "OSConcurrentResultsForm.jsp";
			dispatch(request, response, nextPage);
			
		} catch (Throwable t) {
			ib.debugOut("OrderStatusConcurrentServlet.doGet() failed trying to dispatch results page.  Throwable caught is: " + t);
			t.printStackTrace();
		}
	}



	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);

	}


	public void dispatch(HttpServletRequest request,
			HttpServletResponse response, String nextPage)
					throws ServletException, IOException {

		RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
		dispatch.forward(request, response);
	}


}
