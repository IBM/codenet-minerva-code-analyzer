package concurrency.status.lite;

import irwwbase.IRWWBase;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import orderstatus.concurrent.ejb.lite.ConcurrencyStatusOutput;
import orderstatus.concurrent.ejb.lite.OrderStatusConcurrencyControllerBean;

/**
 * Servlet implementation class ConcurrencyStatusServlet
 */
@WebServlet("/ConcurrencyStatusServlet")
public class ConcurrencyStatusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
     
	IRWWBase ib = new IRWWBase();
	ConcurrencyStatusOutput outputPage = new ConcurrencyStatusOutput();
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConcurrencyStatusServlet() {
        super();
       
    }

	
    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			String buttonPressed = request.getParameter("command");
			String nextPage;

			// lookup the order status concurrency controller singleton bean in the global name space.
			InitialContext ic;
			ic = new InitialContext();
			OrderStatusConcurrencyControllerBean controllerBean = (OrderStatusConcurrencyControllerBean) ic.lookup("java:app/OrderStatusConcurrentEJBLite/OrderStatusConcurrencyControllerBean");


			nextPage = "ConcurrencyStatusResultsForm.jsp";
			outputPage.setButtonPressed(buttonPressed);
			
			ib.debugOut("<<< nextpage =" + nextPage);
			
			if (buttonPressed.trim().equalsIgnoreCase("Quiesce")) 
				controllerBean.stopWork();
				
			outputPage = controllerBean.getStatus();
				
			HttpSession sess = request.getSession();

			sess.setAttribute("outputBean", outputPage);
			// Forward the request to the next page
			dispatch(request, response, nextPage);

		} catch (NamingException e) {
            System.out.println("WebConcurrencyStatus.doPost() caught throwable: " + e);
			e.printStackTrace();
		}
	}
	
	
	public void dispatch(HttpServletRequest request,
			HttpServletResponse response, String nextPage)
			throws ServletException, IOException {

		RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
		dispatch.forward(request, response);
	}
}
