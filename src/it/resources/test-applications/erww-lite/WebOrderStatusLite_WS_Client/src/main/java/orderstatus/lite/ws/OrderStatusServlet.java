package orderstatus.lite.ws;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceRef;
//Comment out unused import
//import orderstatus.lite.ws.OrderStatusOutput;
import irwwbase.*;

/**
 * Servlet implementation class OrderStatusServlet
 */
@WebServlet("/OrderStatusServlet")
public class OrderStatusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private java.lang.String className = this.getClass().toString();
       
    @WebServiceRef(name="OrderStatusImplService",value=OrderStatusImplService.class)
     private OrderStatusImpl orderStatusImpl;
          
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OrderStatusServlet() {
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
		
		IRWWBase irwwbase = new IRWWBase();
		
		irwwbase.debugOut("*** Entering doPost method of Web Services OrderStatusServlet Client ***");
	    
	    String inputType = "AutoGen";
	    short warehouseId = 0;
	    short districtId = 0;
	    short customerId = 0;
	    String customerLastName = null;
	    boolean byLastName = false;
	    
	    byte db = irwwbase.dbSize();

		// Set times - Commenting out set times section because it
	    // appears to be unused
		//java.sql.Timestamp timestamp = null; 
		//java.util.Date date = java.util.Calendar.getInstance().getTime();
		//timestamp = new java.sql.Timestamp(date.getTime());
		
		OrderStatusInput input = new OrderStatusInput();
		OrderStatusOutput output = new OrderStatusOutput();
		
		//Assume failure until the web app succeeds
		output.setRetc("Failed");
					
		inputType = request.getParameter("inputType");
		
		if (inputType.contains("Manual")){
			warehouseId = Short.valueOf(request.getParameter("warehouseId"));
			districtId = Short.valueOf(request.getParameter("districtId"));
			customerId = Short.valueOf(request.getParameter("customerId"));
			customerLastName = request.getParameter("customerLastName");
			if (customerId <= 0){
				byLastName = true;
			}
		} else {
			ExtendedRandom rand = new ExtendedRandom();
			warehouseId = ((short) rand.nextInt(1, MaxValues.warehouseId(db)));
			districtId = ((short) rand.nextInt(1, MaxValues.districtId(db)));
			customerId = ((short) rand.nextInt(1, MaxValues.customerId(db)));
		}
		input.setInCustomerWarehouseId(warehouseId);
		input.setInCustomerDistrictId(districtId);
		input.setInCustomerId(customerId);
		input.setInByLastName(byLastName);
		input.setInCustomerLastName(customerLastName);
					
		Map<String, Object> requestContext = ((BindingProvider)orderStatusImpl).getRequestContext();
		
		try{
			requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://"+ request.getParameter("ws_host")+ ":" + request.getParameter("ws_port")+"/WebOrderStatusLite_WS/OrderStatusImplService");
			irwwbase.debugOut("*** BindingProvider.ENDPOINT_ADDRESS_PROPERTY = " + requestContext.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
		} catch (Throwable ex) {
			System.out.println("<<< ERROR: " + className + "  - Exception: " + ex);
			throw new ServletException();	
		}
		
/*
		irwwbase.debugOut("*** Retrieving the properties such as ReceiveTimeout from ibm-ws-bnd.xml is only supported on Liberty, not tWAS");
		try{
			BindingProvider bindingProvider = (BindingProvider)orderStatusImpl;
		
			irwwbase.debugOut("*** Before getting the ConnectionTimeout");
			String connectionTimeout = bindingProvider.getRequestContext().get("http.conduit.client.ConnectionTimeout").toString();   
			irwwbase.debugOut("*** ConnectionTimeout = " + connectionTimeout);
			
			irwwbase.debugOut("*** Before getting the ReceiveTimeout");
			String receiveTimeout = bindingProvider.getRequestContext().get("http.conduit.client.ReceiveTimeout").toString(); 
			irwwbase.debugOut("*** ReceiveTimeout = " + receiveTimeout);
			
		} catch (Throwable ex) {
			System.out.println("<<< ERROR: " + className + "  - Exception: " + ex);
			throw new ServletException();	
		}
*/
			
		try{
			output = orderStatusImpl.getOrderStatus(input);
		} catch (Throwable ex) {
			System.out.println("<<< ERROR: " + className + "  - Exception: " + ex);
			throw new ServletException();	
		} 
		request.setAttribute("output", output);
				
		dispatch(request, response, "OSResultsForm.jsp");
	}
	
	public void dispatch(
			HttpServletRequest request,
			HttpServletResponse response,
			String nextPage)
			throws ServletException, IOException {
			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);
		}

}
