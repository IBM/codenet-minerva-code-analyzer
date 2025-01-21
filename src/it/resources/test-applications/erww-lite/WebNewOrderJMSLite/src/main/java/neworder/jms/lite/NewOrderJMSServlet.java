package neworder.jms.lite;

import irwwbase.IRWWBase;
import irwwbase.UserException;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import newordersession.NewOrderInputInfo;
import newordersession.NewOrderOutputInfo;
import newordersession.NewOrderSingleItemInInfo;


/**
 * Servlet implementation class NewOrderJMSServlet
 */
@WebServlet("/NewOrderJMSServlet")
public class NewOrderJMSServlet extends HttpServlet {

	// Counter of active client transaction requests (ie. from all client threads currently running).
	// AtomicInteger is used for thread safety since the servlet is multi-threaded.
	static AtomicInteger JMSNewOrderClientTxInFlightCounter = new AtomicInteger(0);
	
	private static final long serialVersionUID = 123987L;
	
	IRWWBase ib = new IRWWBase();
	
	public NewOrderJMSServlet() {
		super();
	}

	
	public void performTask(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {
			JMS_MDB_debugOut(">> JMSNewOrderClientTxInFlightCounter = " + JMSNewOrderClientTxInFlightCounter.incrementAndGet());	  

			String submitButton = request.getParameter("command");		

			String nextPage = "NOResultsForm.jsp";

			ib.debugOut("Enter NewOrderJMSServlet.performTask(), command = " + submitButton + " nextPage = " + nextPage);

			NewOrderJMSClient clientBean = new NewOrderJMSClient();
			NewOrderInputInfo input = null;
			NewOrderOutputInfo output = null;

			// Create the results data object, and start out assuming we will have failure (ie. until proven otherwise)
			NewOrderJMSResults results = new NewOrderJMSResults();
			results.setStatus("FAILED");
			results.setException("None");

			try {

				// Manual input, or auto-generation of input data?
				if (submitButton.trim().equals("Manual")) {
					input = getManualInput(request);
					clientBean.webManualInput(input);

				} else {
					clientBean.webAutoGenInput();

				}
				
			    // If no exceptions were thrown, then we have success.  This is the one place where successful status is marked.
				results.setStatus("SUCCESSFUL");
                

			// If we catch Exceptions, the transaction failed, but we should continue on to display the 
			// output page.
			} catch (UserException ue) {
				System.out.println("UserException caught in NewOrderJMSServlet.performTask() method: " + ue);
				results.setStatus("FAILED");
				results.setException("UserException: " + ue.getMessage());
				ue.printStackTrace();

			} catch (Throwable t) {
				System.out.println("Unexpected exception caught in NewOrderJMSServlet.performTask() method: " + t.getMessage());
				results.setStatus("FAILED");
				results.setException("Throwable: " + t.getMessage());
				t.printStackTrace();
			}


			try {	

				// Process remaining results into the results data object to pass on to the results web page
				results.setMessage1(clientBean.getResultMessage1());
				results.setMessage2(clientBean.getResultMessage2());
				results.setMessage3(clientBean.getResultMessage3());


				// If we have success, then process the New Order result details into a string array.
				// Otherwise, return an array with one line that says "None".
				if ( (results.getStatus()).contains("SUCCESSFUL") ) {
					output = clientBean.getOutput();
					String[] finalOutput = new String[output.getOutOrderItemCount() * 8 + 22];
					finalOutput = clientBean.formatOutput(output);
					results.setOutputDetails(finalOutput);
				} else {
					String[] emptyArray = new String[1];
					emptyArray[0] = "None";
					results.setOutputDetails(emptyArray);
				}

				// Now pass the results object into the final JSP page as a session attribute
				HttpSession sess = request.getSession();
				sess.setAttribute("NewOrderJMSResults", results);

				// Forward the request to the next page
				dispatch(request, response, nextPage);

			// We don't expect to get exceptions while attempting to display the output page 
			} catch (Throwable t) {
				System.out.println("Unexpected exception caught attempting to display output page in NewOrderJMSServlet.performTask() method: " + t);
				t.printStackTrace();
			}	

		} finally {

			// One less client transaction request is now active
	 		JMS_MDB_debugOut("<< JMSNewOrderClientTxInFlightCounter = " + JMSNewOrderClientTxInFlightCounter.decrementAndGet() );
	 		
			ib.debugOut("Exit NewOrderJMSServlet.performTask()");
		}
	}



	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		performTask(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		performTask(request, response);
	}


	public void dispatch(HttpServletRequest request,
			HttpServletResponse response, String nextPage)
					throws ServletException, IOException {

		RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
		dispatch.forward(request, response);
	}

    /**
     * When manual input is entered on the input page, we use this method to pull
     * all the parameters into a input object.
     */

	private NewOrderInputInfo getManualInput(HttpServletRequest request) {

		boolean skip = false;

		NewOrderInputInfo input = new NewOrderInputInfo();

		NewOrderSingleItemInInfo[] itemElement = new NewOrderSingleItemInInfo[15];

		input.setInWareHouseId(Short.valueOf(request
				.getParameter("inWarehouseId")));

		input.setInDistrictId(Short.valueOf(request
				.getParameter("inDistrictId")));

		input.setInCustomerId(Short.valueOf(request
				.getParameter("inCustomerId")));

		input.setInOrderEntryDate(java.util.Calendar.getInstance());

		if (Integer.valueOf(request.getParameter("itemId1")) != 0) {
			itemElement[0] = new NewOrderSingleItemInInfo();
			itemElement[0].setInItemId(Integer.valueOf(request
					.getParameter("itemId1")));
			itemElement[0].setInItemQuantity(Integer.valueOf(request
					.getParameter("quantity1")));
			itemElement[0].setInItemSupplyWarehouseId(Short.valueOf(request
					.getParameter("itemSupplyWarehouseId1")));
			skip = false;
		} else {
			skip = true;
		}

		int actualLength = 0;
		if (skip == false) {
			if (Integer.valueOf(request.getParameter("itemId2")) != 0) {
				itemElement[1] = new NewOrderSingleItemInInfo();
				itemElement[1].setInItemId(Integer.valueOf(request
						.getParameter("itemId2")));
				itemElement[1].setInItemQuantity(Integer.valueOf(request
						.getParameter("quantity2")));
				itemElement[1].setInItemSupplyWarehouseId(Short.valueOf(request
						.getParameter("itemSupplyWarehouseId2")));
			} else {
				actualLength = 1;
				skip = true;
			}
		}

		if (skip == false) {
			if (Integer.valueOf(request.getParameter("itemId3")) != 0) {
				itemElement[2] = new NewOrderSingleItemInInfo();
				itemElement[2].setInItemId(Integer.valueOf(request
						.getParameter("itemId3")));
				itemElement[2].setInItemQuantity(Integer.valueOf(request
						.getParameter("quantity3")));
				itemElement[2].setInItemSupplyWarehouseId(Short.valueOf(request
						.getParameter("itemSupplyWarehouseId3")));
			} else {
				actualLength = 2;
				skip = true;
			}
		}

		if (skip == false) {
			if (Integer.valueOf(request.getParameter("itemId4")) != 0) {
				itemElement[3] = new NewOrderSingleItemInInfo();
				itemElement[3].setInItemId(Integer.valueOf(request
						.getParameter("itemId4")));
				itemElement[3].setInItemQuantity(Integer.valueOf(request
						.getParameter("quantity4")));
				itemElement[3].setInItemSupplyWarehouseId(Short.valueOf(request
						.getParameter("itemSupplyWarehouseId1")));
			} else {
				actualLength = 3;
				skip = true;
			}
		}

		if (skip == false) {
			if (Integer.valueOf(request.getParameter("itemId5")) != 0) {
				itemElement[4] = new NewOrderSingleItemInInfo();
				itemElement[4].setInItemId(Integer.valueOf(request
						.getParameter("itemId5")));
				itemElement[4].setInItemQuantity(Integer.valueOf(request
						.getParameter("quantity5")));
				itemElement[4].setInItemSupplyWarehouseId(Short.valueOf(request
						.getParameter("itemSupplyWarehouseId5")));
			} else {
				actualLength = 4;
				skip = true;
			}
		}

		if (skip == false) {
			if (Integer.valueOf(request.getParameter("itemId6")) != 0) {
				itemElement[5] = new NewOrderSingleItemInInfo();
				itemElement[5].setInItemId(Integer.valueOf(request
						.getParameter("itemId6")));
				itemElement[5].setInItemQuantity(Integer.valueOf(request
						.getParameter("quantity6")));
				itemElement[5].setInItemSupplyWarehouseId(Short.valueOf(request
						.getParameter("itemSupplyWarehouseId6")));
			} else {
				actualLength = 5;
				skip = true;
			}
		}

		if (skip == false) {
			if (Integer.valueOf(request.getParameter("itemId7")) != 0) {
				itemElement[6] = new NewOrderSingleItemInInfo();
				itemElement[6].setInItemId(Integer.valueOf(request
						.getParameter("itemId7")));
				itemElement[6].setInItemQuantity(Integer.valueOf(request
						.getParameter("quantity7")));
				itemElement[6].setInItemSupplyWarehouseId(Short.valueOf(request
						.getParameter("itemSupplyWarehouseId7")));
			} else {
				actualLength = 6;
				skip = true;
			}
		}

		if (skip == false) {
			if (Integer.valueOf(request.getParameter("itemId8")) != 0) {
				itemElement[7] = new NewOrderSingleItemInInfo();
				itemElement[7].setInItemId(Integer.valueOf(request
						.getParameter("itemId8")));
				itemElement[7].setInItemQuantity(Integer.valueOf(request
						.getParameter("quantity8")));
				itemElement[7].setInItemSupplyWarehouseId(Short.valueOf(request
						.getParameter("itemSupplyWarehouseId8")));
			} else {
				actualLength = 7;
				skip = true;
			}
		}

		if (skip == false) {
			if (Integer.valueOf(request.getParameter("itemId9")) != 0) {
				itemElement[8] = new NewOrderSingleItemInInfo();
				itemElement[8].setInItemId(Integer.valueOf(request
						.getParameter("itemId9")));
				itemElement[8].setInItemQuantity(Integer.valueOf(request
						.getParameter("quantity9")));
				itemElement[8].setInItemSupplyWarehouseId(Short.valueOf(request
						.getParameter("itemSupplyWarehouseId9")));
			} else {
				actualLength = 8;
				skip = true;
			}
		}

		if (skip == false) {
			if (Integer.valueOf(request.getParameter("itemId10")) != 0) {
				itemElement[9] = new NewOrderSingleItemInInfo();
				itemElement[9].setInItemId(Integer.valueOf(request
						.getParameter("itemId10")));
				itemElement[9].setInItemQuantity(Integer.valueOf(request
						.getParameter("quantity10")));
				itemElement[9].setInItemSupplyWarehouseId(Short.valueOf(request
						.getParameter("itemSupplyWarehouseId10")));
			} else {
				actualLength = 9;
				skip = true;
			}
		}

		if (skip == false) {
			if (Integer.valueOf(request.getParameter("itemId11")) != 0) {
				itemElement[10] = new NewOrderSingleItemInInfo();
				itemElement[10].setInItemId(Integer.valueOf(request
						.getParameter("itemId11")));
				itemElement[10].setInItemQuantity(Integer.valueOf(request
						.getParameter("quantity11")));
				itemElement[10].setInItemSupplyWarehouseId(Short
						.valueOf(request
								.getParameter("itemSupplyWarehouseId11")));
			} else {
				actualLength = 10;
				skip = true;
			}
		}

		if (skip == false) {
			if (Integer.valueOf(request.getParameter("itemId12")) != 0) {
				itemElement[11] = new NewOrderSingleItemInInfo();
				itemElement[11].setInItemId(Integer.valueOf(request
						.getParameter("itemId12")));
				itemElement[11].setInItemQuantity(Integer.valueOf(request
						.getParameter("quantity12")));
				itemElement[11].setInItemSupplyWarehouseId(Short
						.valueOf(request
								.getParameter("itemSupplyWarehouseId12")));
			} else {
				actualLength = 11;
				skip = true;
			}
		}

		if (skip == false) {
			if (Integer.valueOf(request.getParameter("itemId13")) != 0) {
				itemElement[12] = new NewOrderSingleItemInInfo();
				itemElement[12].setInItemId(Integer.valueOf(request
						.getParameter("itemId13")));
				itemElement[12].setInItemQuantity(Integer.valueOf(request
						.getParameter("quantity13")));
				itemElement[12].setInItemSupplyWarehouseId(Short
						.valueOf(request
								.getParameter("itemSupplyWarehouseId13")));
			} else {
				actualLength = 12;
				skip = true;
			}
		}

		if (skip == false) {
			if (Integer.valueOf(request.getParameter("itemId14")) != 0) {
				itemElement[13] = new NewOrderSingleItemInInfo();
				itemElement[13].setInItemId(Integer.valueOf(request
						.getParameter("itemId14")));
				itemElement[13].setInItemQuantity(Integer.valueOf(request
						.getParameter("quantity14")));
				itemElement[13].setInItemSupplyWarehouseId(Short
						.valueOf(request
								.getParameter("itemSupplyWarehouseId14")));
			} else {
				actualLength = 13;
				skip = true;
			}
		}

		if (skip == false) {
			if (Integer.valueOf(request.getParameter("itemId15")) != 0) {
				itemElement[14] = new NewOrderSingleItemInInfo();
				itemElement[14].setInItemId(Integer.valueOf(request
						.getParameter("itemId15")));
				itemElement[14].setInItemQuantity(Integer.valueOf(request
						.getParameter("quantity15")));
				itemElement[14].setInItemSupplyWarehouseId(Short
						.valueOf(request
								.getParameter("itemSupplyWarehouseId15")));
				actualLength = 15;
			} else {
				actualLength = 14;
			}
		}

		input.setNumberOfItems(actualLength);
		input.setInItemList(itemElement);
		return input;
	}

	
	private void JMS_MDB_debugOut(String message) {
		if (System.getProperty("JMS_MDB_DEBUG_OUT") != null)
			if (System.getProperty("JMS_MDB_DEBUG_OUT").equals("ON"))
				System.out.println(message);
    }
	
	
}
