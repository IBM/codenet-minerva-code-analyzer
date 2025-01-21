package neworder.lite.ws;

import java.io.IOException;
//Comment out unused import
//import java.util.Date;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceRef;

import irwwbase.*;

/**
 * Servlet implementation class OrderStatusServlet
 */
@WebServlet("/NewOrderServlet_WS_Client")
public class NewOrderServlet_WS_Client extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private java.lang.String className = this.getClass().toString();
	private IRWWBase irwwbase = new IRWWBase();
		     
    @WebServiceRef(name="NewOrderImplService",value=NewOrderImplService.class)
    private NewOrderImplDelegate newOrderImpl;
          
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NewOrderServlet_WS_Client() {
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
			
		irwwbase.debugOut("*** Entering doPost method of Web Services NewOrder Client ***");
				
		//Comment out unused variable
		//String status="FAILED";
	
		byte database = irwwbase.dbSize();
		NewOrderInputInfo input = null;	
		NewOrderOutputInfo output = null;
		
		String submitButton = request.getParameter("command");
		String nextPage;		
		nextPage = "NOResultsForm.jsp";

		irwwbase.debugOut("<<< nextPage = " + nextPage);
		
		if (submitButton.trim().equals("Manual")) {
			input = this.getManualNewOrderInput(request);
		} else if (submitButton.trim().equals("AutoGeneration")){
			input = this.generateNewOrderInput(database);
		}
		
		output = this.generateNewOrderOutput(database, input);

		irwwbase.debugOut("*** Before getting request context ***");
		Map<String, Object> requestContext = ((BindingProvider)newOrderImpl).getRequestContext();
		irwwbase.debugOut("*** After getting request context ***");
		try{
			irwwbase.debugOut("*** Before requestContext.put ***");
			requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://"+ request.getParameter("ws_host")+ ":" + request.getParameter("ws_port")+"/WebNewOrderLite_WS/NewOrderImplService");
			irwwbase.debugOut("*** BindingProvider.ENDPOINT_ADDRESS_PROPERTY = " + requestContext.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
		} catch (Throwable ex) {
			System.out.println("<<< ERROR: " + className + "  - Exception: " + ex);
			throw new ServletException();	
		}
		
/*
		irwwbase.debugOut("*** Retrieving the properties such as ReceiveTimeout from ibm-ws-bnd.xml is only supported on Liberty, not tWAS");
		try{
			BindingProvider bindingProvider = (BindingProvider)newOrderStatusImpl;
		
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
			output = newOrderImpl.createNewOrder(input, output);
			
		} catch (Throwable ex) {
			System.out.println("<<< ERROR: " + className + "  - Exception: " + ex);
			throw new ServletException();	
		} 
		request.setAttribute("output", output);
		
		irwwbase.debugOut("<<< output.getOutItemList().size() = " + output.getOutItemList().size());
				
		dispatch(request, response, "NOResultsForm.jsp");
	}
	
	//
	// Method to create the input structure for NewOrderTransaction
	//

	private NewOrderInputInfo generateNewOrderInput(byte db) throws ServletException {
		
		int numberOfItems = 0;
		ExtendedRandom rand = new ExtendedRandom();

		NewOrderInputInfo input = new NewOrderInputInfo();

		try {
		     input.setInWareHouseId((short) rand.nextInt(1, MaxValues.warehouseId(db)));		
		     input.setInDistrictId((short) rand.nextInt(1, MaxValues.districtId(db)));
		     input.setInCustomerId((short) rand.nextInt(1, MaxValues.customerId(db)));

		     numberOfItems = rand.nextInt(MaxValues.minOrderlinesPerOrder(db),MaxValues.orderlinesPerOrder(db));

		     input.setNumberOfItems(numberOfItems);

		     NewOrderSingleItemInInfo item[] = new NewOrderSingleItemInInfo[numberOfItems];
		     
		     irwwbase.displayOut("<<< numberOfItems = "+ numberOfItems);

		     for (int i = 0; i < numberOfItems; i++) {

		    	 item[i] = new NewOrderSingleItemInInfo();
		    	 item[i].setInItemId(rand.nextInt(1, MaxValues.itemId(db)));

		    	 if (rand.nextBoolProb(1, 100) && MaxValues.warehouseId(db) != 1) { // remote warehouse 1% of the time
		    		 do {
		    			 item[i].setInItemSupplyWarehouseId((short) rand.nextInt(1,MaxValues.warehouseId(db)));
		    		 } while (item[i].getInItemSupplyWarehouseId() == input.getInWareHouseId());
		    	 } else { // supplying warehouse = home warehouse
		    		 item[i].setInItemSupplyWarehouseId(input.getInWareHouseId());
		    	 }
		    	 item[i].setInItemQuantity(rand.nextInt(1, 10));	
			
		    	 input.getInItemList().add(i, item[i]);

		     } // for i
		     //input.setInItemList(item);	

		     //Comment out unused variable
		     //String dateString = new String(new Date().toString());
		     // actual format is : Thu Dec 09 05:50:07 EST 1999
		     // required format is: 1999-12-09-11.03.22.000000

		     //input.setInOrderEntryDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(java.util.Calendar.getInstance().toString()));	
		
		} catch (Throwable ex) {
			System.out.println("<<< ERROR: " + className + "  - Exception: " + ex);
			throw new ServletException();	
		}

		return input;
	}
	
	private NewOrderInputInfo getManualNewOrderInput(HttpServletRequest request) throws ServletException {
		
		boolean skip = false;
		NewOrderInputInfo input = null;
		int itemListLength=15;
		int actualLength = 0;

		
		input = new NewOrderInputInfo();
		
		NewOrderSingleItemInInfo[] itemElement = new NewOrderSingleItemInInfo[itemListLength];

		//input.setDistributedEJBs(Boolean.valueOf(request.getParameter("distributed"))); //currently not used
		try {
			input.setInWareHouseId(Short.valueOf(request.getParameter("inWarehouseId")));
			input.setInDistrictId(Short.valueOf(request.getParameter("inDistrictId")));
			input.setInCustomerId(Short.valueOf(request.getParameter("inCustomerId")));

			//input.setInOrderEntryDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(java.util.Calendar.getInstance().toString()));
	
			int i = 0;
			if (Integer.valueOf(request.getParameter("itemId1")).intValue() != 0) {
				itemElement[i] = new NewOrderSingleItemInInfo();
				itemElement[i].setInItemId(Integer.valueOf(request.getParameter("itemId1")));
				itemElement[i].setInItemQuantity(Integer.valueOf(request.getParameter("quantity1")));
				itemElement[i].setInItemSupplyWarehouseId(Short.valueOf(request.getParameter("itemSupplyWarehouseId1")));
				input.getInItemList().add(i, itemElement[i]);
				skip = false;
			} else {
				skip = true;
			}

			++i;
			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId2")).intValue() != 0) {
					itemElement[i] = new NewOrderSingleItemInInfo();
					itemElement[i].setInItemId(Integer.valueOf(request.getParameter("itemId2")));
					itemElement[i].setInItemQuantity(Integer.valueOf(request.getParameter("quantity2")));
					itemElement[i].setInItemSupplyWarehouseId(Short.valueOf(request.getParameter("itemSupplyWarehouseId2")));
					input.getInItemList().add(i, itemElement[i]);
				} else {
					actualLength = 1;
					skip = true;
				}
			}

			++i;
			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId3")).intValue() != 0) {
					itemElement[i] = new NewOrderSingleItemInInfo();
					itemElement[i].setInItemId(Integer.valueOf(request.getParameter("itemId3")));
					itemElement[i].setInItemQuantity(Integer.valueOf(request.getParameter("quantity3")));
					itemElement[i].setInItemSupplyWarehouseId(Short.valueOf(request.getParameter("itemSupplyWarehouseId3")));
					input.getInItemList().add(i, itemElement[i]);
				} else {
					actualLength = 2;
					skip = true;
				}
			}

			++i;
			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId4")).intValue() != 0) {
					itemElement[i] = new NewOrderSingleItemInInfo();
					itemElement[i].setInItemId(Integer.valueOf(request.getParameter("itemId4")));
					itemElement[i].setInItemQuantity(Integer.valueOf(request.getParameter("quantity4")));
					itemElement[i].setInItemSupplyWarehouseId(Short.valueOf(request.getParameter("itemSupplyWarehouseId4")));
					input.getInItemList().add(i, itemElement[i]);
				} else {
					actualLength = 3;
					skip = true;
				}
			}

			++i;
			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId5")).intValue() != 0) {
					itemElement[i] = new NewOrderSingleItemInInfo();
					itemElement[i].setInItemId(Integer.valueOf(request.getParameter("itemId5")));
					itemElement[i].setInItemQuantity(Integer.valueOf(request.getParameter("quantity5")));
					itemElement[i].setInItemSupplyWarehouseId(Short.valueOf(request.getParameter("itemSupplyWarehouseId5")));
					input.getInItemList().add(i, itemElement[i]);
				} else {
					actualLength = 4;
					skip = true;
				}
			}

			++i;
			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId6")).intValue() != 0) {
					itemElement[i] = new NewOrderSingleItemInInfo();
					itemElement[i].setInItemId(Integer.valueOf(request.getParameter("itemId6")));
					itemElement[i].setInItemQuantity(Integer.valueOf(request.getParameter("quantity6")));
					itemElement[i].setInItemSupplyWarehouseId(Short.valueOf(request.getParameter("itemSupplyWarehouseId6")));
					input.getInItemList().add(i, itemElement[i]);
				} else {
					actualLength = 5;
					skip = true;
				}
			}

			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId7")).intValue() != 0) {
					itemElement[i] = new NewOrderSingleItemInInfo();
					itemElement[i].setInItemId(Integer.valueOf(request.getParameter("itemId7")));
					itemElement[i].setInItemQuantity(Integer.valueOf(request.getParameter("quantity7")));
					itemElement[i].setInItemSupplyWarehouseId(Short.valueOf(request.getParameter("itemSupplyWarehouseId7")));
					input.getInItemList().add(i, itemElement[i]);
				} else {
					actualLength = 6;
					skip = true;
				}
			}

			++i;
			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId8")).intValue() != 0) {
					itemElement[i] = new NewOrderSingleItemInInfo();
					itemElement[i].setInItemId(Integer.valueOf(request.getParameter("itemId8")));
					itemElement[i].setInItemQuantity(Integer.valueOf(request.getParameter("quantity8")));
					itemElement[i].setInItemSupplyWarehouseId(Short.valueOf(request.getParameter("itemSupplyWarehouseId8")));
					input.getInItemList().add(i, itemElement[i]);
				} else {
					actualLength = 7;
					skip = true;
				}
			}

			++i;
			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId9")).intValue() != 0) {
					itemElement[i] = new NewOrderSingleItemInInfo();
					itemElement[i].setInItemId(Integer.valueOf(request.getParameter("itemId9")));
					itemElement[i].setInItemQuantity(Integer.valueOf(request.getParameter("quantity9")));
					itemElement[i].setInItemSupplyWarehouseId(Short.valueOf(request.getParameter("itemSupplyWarehouseId9")));
					input.getInItemList().add(i, itemElement[i]);
				} else {
					actualLength = 8;
					skip = true;
				}
			}

			++i;
			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId10")).intValue() != 0) {
					itemElement[i] = new NewOrderSingleItemInInfo();
					itemElement[i].setInItemId(Integer.valueOf(request.getParameter("itemId10")));
					itemElement[i].setInItemQuantity(Integer.valueOf(request.getParameter("quantity10")));
					itemElement[i].setInItemSupplyWarehouseId(Short.valueOf(request.getParameter("itemSupplyWarehouseId10")));
					input.getInItemList().add(i, itemElement[i]);
				} else {
					actualLength = 9;
					skip = true;
				}
			}

			++i;
			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId11")).intValue() != 0) {
					itemElement[i] = new NewOrderSingleItemInInfo();
					itemElement[i].setInItemId(Integer.valueOf(request.getParameter("itemId11")));
					itemElement[i].setInItemQuantity(Integer.valueOf(request.getParameter("quantity11")));
					itemElement[i].setInItemSupplyWarehouseId(Short.valueOf(request.getParameter("itemSupplyWarehouseId11")));
					input.getInItemList().add(i, itemElement[i]);
				} else {
					actualLength = 10;
					skip = true;
				}
			}

			++i;
			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId12")).intValue() != 0) {
					itemElement[i] = new NewOrderSingleItemInInfo();
					itemElement[i].setInItemId(Integer.valueOf(request.getParameter("itemId12")));
					itemElement[i].setInItemQuantity(Integer.valueOf(request.getParameter("quantity12")));
					itemElement[i].setInItemSupplyWarehouseId(Short.valueOf(request.getParameter("itemSupplyWarehouseId12")));
					input.getInItemList().add(i, itemElement[i]);
				} else {
					actualLength = 11;
					skip = true;
				}
			}

			++i;
			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId13")).intValue() != 0) {
					itemElement[i] = new NewOrderSingleItemInInfo();
					itemElement[i].setInItemId(Integer.valueOf(request.getParameter("itemId13")));
					itemElement[i].setInItemQuantity(Integer.valueOf(request.getParameter("quantity13")));
					itemElement[i].setInItemSupplyWarehouseId(Short.valueOf(request.getParameter("itemSupplyWarehouseId13")));
					input.getInItemList().add(i, itemElement[i]);
				} else {
					actualLength = 12;
					skip = true;
				}
			}

			++i;
			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId14")).intValue() != 0) {
					itemElement[i] = new NewOrderSingleItemInInfo();
					itemElement[i].setInItemId(Integer.valueOf(request.getParameter("itemId14")));
					itemElement[i].setInItemQuantity(Integer.valueOf(request.getParameter("quantity14")));
					itemElement[i].setInItemSupplyWarehouseId(Short.valueOf(request.getParameter("itemSupplyWarehouseId14")));
					input.getInItemList().add(i, itemElement[i]);
				} else {
					actualLength = 13;
					skip = true;
				}
			}

			++i;
			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId15")).intValue() != 0) {
					itemElement[i] = new NewOrderSingleItemInInfo();
					itemElement[i].setInItemId(Integer.valueOf(request.getParameter("itemId15")));
					itemElement[i].setInItemQuantity(Integer.valueOf(request.getParameter("quantity15")));
					itemElement[i].setInItemSupplyWarehouseId(Short.valueOf(request.getParameter("itemSupplyWarehouseId15")));
					input.getInItemList().add(i, itemElement[i]);
					actualLength = 15;
				} else {
					actualLength = 14;
				}
			}
		
			input.setNumberOfItems(actualLength);
		//input.setInItemList(itemElement);
		} catch (Throwable ex) {
			System.out.println("<<< ERROR: " + className + "  - Exception: " + ex);
			throw new ServletException();	
		}
		
		return input;
	}
	
	NewOrderOutputInfo generateNewOrderOutput(byte db, NewOrderInputInfo input) {
		
		int numberOfItems = input.getNumberOfItems();

		NewOrderOutputInfo output = new NewOrderOutputInfo();
		output.setOutWarehouseId((short) 0);
		output.setOutDistrictId((short) 0);
		output.setOutCustomerId((short) 0);
		output.setOutOrderId((short) 0);
		output.setOutCustomerLastName(" ");
		output.setOutCustomerCredit(" ");
		output.setOutMsg(" ");

		irwwbase.debugOut("In generateNewOrderOutput, numberOfItems = " + numberOfItems);

		NewOrderSingleItemOutInfo item[] = new NewOrderSingleItemOutInfo[numberOfItems];

		for (int i = 0; i < numberOfItems; i++) {

			item[i] = new NewOrderSingleItemOutInfo();
			item[i].setOutItemId((short) 0);
			item[i].setOutItemSupplyWarehouseId((short) 0);
			item[i].setOutItemName(" ");
			
			output.getOutItemList().add(i, item[i]);
		}

		return output;
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
