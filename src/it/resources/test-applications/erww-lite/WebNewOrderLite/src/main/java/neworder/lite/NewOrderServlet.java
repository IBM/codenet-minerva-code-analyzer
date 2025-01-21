package neworder.lite;

import irwwbase.IRWWBase;
import itemjpa.ItemJPA;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.QueueConnection;
import jakarta.jms.QueueConnectionFactory;
import jakarta.jms.QueueSession;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceUnit;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

import neworderjpa.NewOrderJPA;
import orderjpa.OrderJPA;
import orderjpa.OrderKey;
import orderlinejpa.OrderlineJPA;
import stockjpa.StockJPA;
import stockjpa.StockKey;
import customerjpa.CustomerJPA;
import customerjpa.CustomerKey;
import districtjpa.DistrictJPA;
import districtjpa.DistrictKey;
import warehousejpa.WarehouseJPA;
import newordersession.NewOrderInputInfo;
import newordersession.NewOrderOutputInfo;
import newordersession.NewOrderSingleItemInInfo;
import newordersession.NewOrderSingleItemOutInfo;


/**
 * Servlet implementation class NewOrderServlet
 */
@WebServlet("/NewOrderServlet")
public class NewOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	IRWWBase ib = new IRWWBase();
	@PersistenceUnit(unitName = "NewOrderLite")
	EntityManagerFactory emf;
	
	public static Boolean WEBSOCKET_EXISTS = null; // Flag that indicates whether jakarta.websocket.WebSocketContainer exists
	public static Boolean WEBSOCKET_1_0 = null; // Flag that indicates whether monitor.SimpleMonitorEndpoint exists
	public static Boolean WEBSOCKET_1_1 = null; // Flag that indicates whether monitor.SimpleMonitorEndpoint exists
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public NewOrderServlet() {
		super();
		
		// Check whether WebSockets will be used with this transaction
		if (WEBSOCKET_EXISTS == null) {
			ib.debugOut("<<< Checking if WebSocket API exists. This transaction will send JMS messages if WebSocket does exist.");
			ib.debugOut("<<< Checking for class \"jakarta.websocket.WebSocketContainer\"");
			try {
				Class.forName("jakarta.websocket.WebSocketContainer");
				WEBSOCKET_EXISTS = true;
				ib.debugOut("<<< WEBSOCKET_EXISTS = true");
			} catch (ClassNotFoundException e) {
				WEBSOCKET_EXISTS = false;
				ib.debugOut("<<< WEBSOCKET_EXISTS = false");
			} catch (Throwable t) {
				System.out.println("Error occurred while searching for jakarta.websocket.WebSocketContainer");
				t.printStackTrace();
				WEBSOCKET_EXISTS = false;
			}
		}
		
		if (WEBSOCKET_1_0 == null) {
			ib.debugOut("<<< Checking for WebSocket 1.0 resources");
			try {
				InitialContext ic = new InitialContext();
				ic.lookup("jms/MonitorQueueCF1.0");
				ic.lookup("jms/MonitorQ1.0");
				WEBSOCKET_1_0 = true;
				ib.debugOut("<<< WEBSOCKET_1_0 = true");
			} catch (NameNotFoundException e) {
				WEBSOCKET_1_0 = false;
				ib.debugOut("<<< WEBSOCKET_1_0 = false");
			} catch (Throwable t) {
				System.out.println("Error occurred while searching for WebSocket 1.0 resources");
				t.printStackTrace();
				WEBSOCKET_1_0 = false;
				ib.debugOut("<<< WEBSOCKET_1_0 = false");
			}
		}
		
		if (WEBSOCKET_1_1 == null) {
			ib.debugOut("<<< Checking for WebSocket 1.1 resources");
			try {
				InitialContext ic = new InitialContext();
				ic.lookup("jms/MonitorQueueCF1.1");
				ic.lookup("jms/MonitorQ1.1");
				WEBSOCKET_1_1 = true;
				ib.debugOut("<<< WEBSOCKET_1_1 = true");
			} catch (NameNotFoundException e) {
				WEBSOCKET_1_1 = false;
				ib.debugOut("<<< WEBSOCKET_1_1 = false");
			} catch (Throwable t) {
				System.out.println("Error occurred while searching for WebSocket 1.1 resources");
				t.printStackTrace();
				WEBSOCKET_1_1 = false;
				ib.debugOut("<<< WEBSOCKET_1_1 = false");
			}
		}
	}

	public void performTask(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String submitButton = request.getParameter("command");

		String nextPage;

		nextPage = "NOResultsForm.jsp";

		ib.debugOut("<<< nextPage = " + nextPage);

		Client c = new Client();
		NewOrderInputInfo input = null;
		NewOrderOutputInfo output = null;
		if (submitButton.trim().equals("Manual")) {
			input = getManualInput(request);
			output = c.generateNewOrderOutput((byte) 1);
			NewOrderSingleItemOutInfo item[] = new NewOrderSingleItemOutInfo[input
					.getNumberOfItems()];

			for (int i = 0; i < input.getNumberOfItems(); i++) {

				item[i] = new NewOrderSingleItemOutInfo();
				item[i].setOutItemId((short) 0);
				item[i].setOutItemSupplyWarehouseId((short) 0);
				item[i].setOutItemName(" ");
			}
			output.setOutItemList(item);

		} else {
			c.web();
			input = c.getInput();
			output = c.getOutput();
		}
		output.setOutMsg("FAILED");
		try {
			output = newOrderServlet(input, output);
		} catch (IllegalStateException | SecurityException | SystemException e) {
			e.printStackTrace();
			throw new ServletException(
					"Exception calling output=newOrderSession(input, output); ",
					e);
		} 
		output.setOutMsg("Successful");

		String[] out = new String[input.getNumberOfItems() * 8 + 23];

		if (submitButton.trim().equals("Manual")) {
			out = c.genOutput(output, out);
			c.setOput(out);
		} else
			out = c.genOutput(output);

		HttpSession sess = request.getSession();

		sess.setAttribute("nOBean", c);

		// Forward the request to the next page
		dispatch(request, response, nextPage);
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

	public NewOrderOutputInfo newOrderServlet(NewOrderInputInfo input,
			NewOrderOutputInfo output) throws ServletException,
			IllegalStateException, SecurityException, SystemException {
		java.math.BigDecimal customerYTDPayment = null;
		//Comment out unused variable
		//int size = 0;

		EntityManager em = null;

		em = emf.createEntityManager();

		int orderAllLocalFlag = 1;

		// Set times
		//Comment out unused variables
		//java.lang.String s = new String();
		//java.lang.String beforeDistrictTime = null;
		java.lang.String afterDistrictTime = null;
		java.lang.String beforeNewOrderTime = null;
		java.lang.String afterNewOrderTime = null;
		java.util.Calendar calendar = null;
		java.util.Date date = null;
		java.sql.Timestamp timestamp = null;
		ib.startOfTran("NewOrderServlet");

		// openjpa pessimistic update locking
		// kem = OpenJPAPersistence.cast (em);

		calendar = input.getInOrderEntryDate();

		date = java.util.Calendar.getInstance().getTime();
		timestamp = new java.sql.Timestamp(date.getTime());
		// i18nService - Get Resource Bundles

		// NewOrderOutputInfo output = new NewOrderOutputInfo();

		output.setOutTotal(0);

		// ***********************************************************
		// Copy non-repeating fields from input structure to output.
		// ***********************************************************

		output.setOutWarehouseId(input.getInWareHouseId());
		output.setOutDistrictId(input.getInDistrictId());
		output.setOutCustomerId(input.getInCustomerId());

		//Comment out unused variables
		//int ORDER_ID_LEN = 8;
		//int nextOrderId;

		// ***********************************************************
		// Warehouse 2.4.2.2 bullet 3
		// - get WarehouseTax
		// ***********************************************************
		ib.debugOut("<<< NewOrderSession - getting Warehouse Object "
				+ ib.getCurrentTime());
		UserTransaction ut = null;
		try {
			ut = getUserTrans();
		} catch (NamingException e) {
			e.printStackTrace();
			throw new ServletException("Exception getting user transaction", e);
		}
		try {
			ut.begin();
		} catch (NotSupportedException | SystemException e) {
			e.printStackTrace();
			throw new ServletException("Exception starting user transaction", e);
		} 
		em.joinTransaction();
		WarehouseJPA warehouse = null;

		try {
			warehouse = getWarehouseInstance(input.getInWareHouseId(), em, ut);
		} catch (Exception e) {
			throw new ServletException("Exception getting warhouseInstance", e);
		}

		output.setOutWarehouseTax(warehouse.getWarehouseTax().doubleValue());

		// ***********************************************************
		// District 2.4.2.2 bullet 4
		// - get district Tax
		// - get district next order id (use as this OrderId)
		// - increment district next order id
		// ( for future New Order Transactions)
		// ***********************************************************
		ib.debugOut("<<< NewOrderSession - getting District Object..."
				+ ib.getCurrentTime());
		DistrictJPA district = null;
		try {
			district = getDistrictInstance(input.getInDistrictId(),
					input.getInWareHouseId(), em, ut);
		} catch (Exception e) {
			throw new ServletException("Exception getting districtInstance", e);
		}
		ib.debugOut("<<< after - getting District Object..."
				+ ib.getCurrentTime());

		ib.debugOut("<<< Before get the next orderId" + ib.getCurrentTime());
		int d_next_o_id_int = district.getDistrictNextOrderId();
		// get next OrderId
		ib.debugOut("<<< After getting the next orderId" + ib.getCurrentTime());

		output.setOutOrderId(d_next_o_id_int);
		ib.debugOut("<<<output.outOrderId: " + output.getOutOrderId() + " "
				+ ib.getCurrentTime());
		ib.debugOut("<<<* " + input.getInWareHouseId() + " "
				+ input.getInDistrictId() + " " + output.getOutOrderId() + " "
				+ ib.getCurrentTime());

		output.setOutDistrictTax(district.getDistrictTax().doubleValue());
		// increment the next order ID for the district for future
		// NewOrdersTrans
		// int nextOrderId = d_next_o_id_int+1);
		ib.debugOut("<<<nextOrderId being put back into district: "
				+ (d_next_o_id_int + 1) + " " + ib.getCurrentTime());
		district.setDistrictNextOrderId(d_next_o_id_int + 1);
		afterDistrictTime = new java.util.Date().toString() + " "
				+ String.valueOf(System.currentTimeMillis());

		// ***********************************************************
		// Customer 2.4.2.2 bullet 5
		// - get Customer Discount, Customer Last Name & Customer Credit
		// ***********************************************************

		ib.debugOut("<<< NewOrderSession - getting Customer Object..."
				+ ib.getCurrentTime());

		CustomerJPA customer = null;
		try {
			customer = getCustomerInstance(input.getInCustomerId(),
					input.getInDistrictId(), input.getInWareHouseId(), em, ut);
		} catch (Exception e) {
			throw new ServletException("Exception getting customerInstance", e);
		}

		output.setOutCustomerDiscount(customer.getCustomerDiscount()
				.doubleValue() * 100);
		output.setOutCustomerLastName(customer.getCustomerLast());
		output.setOutCustomerCredit(customer.getCustomerCredit());
		customerYTDPayment = customer.getCustomerYtdPayment();

		// *********************************************************
		// 2.4.2.2 bullet 7 o_ol_cnt (items_on_order) is not entered
		// *********************************************************

		short o_ol_cnt = (short) input.getInItemList().length;
		output.setOutOrderItemCount(o_ol_cnt);
		ib.debugOut("<<< NewOrderSession - Number of Items in the List = o_ol_cnt = "
				+ o_ol_cnt + " " + ib.getCurrentTime());

		ib.debugOut("<<< input.getNumberOfItems() = "
				+ input.getNumberOfItems());

		output.setOutOrderEntryDate(input.getInOrderEntryDate());

		try {
			createOrderInstance(output.getOutOrderId(),
					input.getInDistrictId(), input.getInWareHouseId(),
					input.getInCustomerId(), timestamp, (short) 0,
					new java.math.BigDecimal(input.getNumberOfItems()),
					(short) orderAllLocalFlag, em, ut);
		} catch (Exception e) {
			throw new ServletException("Exception creating orderInstance", e);
		}

		int ol_number;
		for (ol_number = 1; ol_number <= input.getNumberOfItems(); ol_number++) {

			ib.debugOut("<<< NewOrderSession - ol_number = " + ol_number + " "
					+ ib.getCurrentTime());

			// ****************************************************
			// Copy repeating fields (for this instance) from input
			// structure to output.
			// ****************************************************
			ib.debugOut("<<< CMPjdbc - setOutItemSupplyWarehouseId ");
			ib.debugOut("<<< server - # objects in array - input.getInItemList().length = "
					+ input.getInItemList().length);
			ib.debugOut("<<< server - # objects in array - output.getOutItemList().length = "
					+ output.getOutItemList().length);
			ib.debugOut("<<< ol_number = " + ol_number);
			output.getOutItemList()[ol_number - 1]
					.setOutItemSupplyWarehouseId(input.getInItemList()[ol_number - 1]
							.getInItemSupplyWarehouseId());

			ib.debugOut("<<< CMPjdbc - setOutItemId ");
			output.getOutItemList()[ol_number - 1].setOutItemId(input
					.getInItemList()[ol_number - 1].getInItemId());

			ib.debugOut("<<< setOutItemQuantity - CMPjdbc case");
			output.getOutItemList()[ol_number - 1]
					.setOutItemQuantity((int) input.getInItemList()[ol_number - 1]
							.getInItemQuantity());

			// ***********************************************************
			// Item 2.4.2.2 bullet 8 part 1
			// - get item name, item Price and
			// item Data (this is used for Brand/generic test)
			// ***********************************************************

			ib.debugOut("<<< NewOrderSession - getting Item Object..."
					+ ib.getCurrentTime());

			// Uncomment this and comment out next line for HP
			// ItemJPA item = getItemInstance(output.getOutItemList()[ol_number
			// - 1]
			// .getOutItemId());
			ItemJPA item = null;
			try {
				item = getItemInstance(
						output.getOutItemList()[ol_number - 1].getOutItemId(),
						em, ut);
			} catch (Exception e) {
				throw new ServletException("Exception getting itemInstance", e);
			}

			output.getOutItemList()[ol_number - 1].setOutItemPrice(item
					.getItemPrice().doubleValue());
			output.getOutItemList()[ol_number - 1].setOutItemName(new String(
					item.getItemName()));
			String i_data = new String(item.getItemData());

			ib.debugOut("<<< NewOrderSession - ItemId = "
					+ output.getOutItemList()[ol_number - 1].getOutItemId()
					+ " " + ib.getCurrentTime());

			// **************************************
			// Stock 2.4.2.2. bullet 8.2
			// **************************************

			ib.debugOut("<<< NewOrderSession - getting Stock Object..."
					+ ib.getCurrentTime());
			StockJPA stock = null;
			try {
				stock = getStockInstance(
						output.getOutItemList()[ol_number - 1].getOutItemId(),
						output.getOutItemList()[ol_number - 1]
								.getOutItemSupplyWarehouseId(), em);
			} catch (Exception e) {
				throw new ServletException("Exception getting stock Instance",
						e);
			}

			int stockQuantity = stock.getS_QUANTITY().intValue();

			if (stockQuantity > (output.getOutItemList()[ol_number - 1]
					.getOutItemQuantity() + 10)) {
				stockQuantity -= output.getOutItemList()[ol_number - 1]
						.getOutItemQuantity();
				if (stockQuantity < 0)
					stockQuantity = 50;
			} else {
				stockQuantity = (stockQuantity - output.getOutItemList()[ol_number - 1]
						.getOutItemQuantity()) + 91;
				if (stockQuantity < 0)
					stockQuantity = 50;
			}

			output.getOutItemList()[ol_number - 1]
					.setOutStockQuantity(stockQuantity);
			stock.setS_QUANTITY(new BigDecimal(stockQuantity));
			// set based on calculations above

			// 2.4.2.2 bullet 8.2

			stock.setS_YTD(new BigDecimal(stock.getS_YTD().intValue()
					+ output.getOutItemList()[ol_number - 1]
							.getOutItemQuantity()));

			stock.setS_ORDER_CNT(new BigDecimal(checkForOverflow(stock
					.getS_ORDER_CNT().intValue()) + 1));
			
			String s_dist_info = getDistrictInfo(Short.valueOf(output.getOutDistrictId()).toString(), stock);
			String s_data = stock.getS_DATA();

			// ****************************************************
			// if there if there is a remote warehouse .
			// incrementRemoteCount
			// ****************************************************

			if (input.getInWareHouseId() != output.getOutItemList()[ol_number - 1]
					.getOutItemSupplyWarehouseId()) {

				stock.setS_REMOTE_CNT(new BigDecimal(checkForOverflow(stock
						.getS_REMOTE_CNT().intValue()) + 1));
				orderAllLocalFlag = 0;
			}

			// ****************************************
			// 2.4.2.2 bullet 8.3
			// - calculate Item "subtotal" and add to cumulative Total
			// ****************************************
			output.getOutItemList()[ol_number - 1].setOutItemTotal(output
					.getOutItemList()[ol_number - 1].getOutItemQuantity()
					* output.getOutItemList()[ol_number - 1].getOutItemPrice());

			output.setOutTotal(output.getOutTotal()
					+ output.getOutItemList()[ol_number - 1].getOutItemTotal());

			// ****************************************
			// Brand/Generic test 2.4.2.2 bullet 8.4
			// - if "ORIGINAL" is not in item data and stock data => generic
			// ****************************************

			// "ORIGINAL" in i_data or s_data?
			if ((i_data.indexOf("ORIGINAL") < 0)
					&& (s_data.indexOf("ORIGINAL") < 0))
				output.getOutItemList()[ol_number - 1].setOutBrandGeneric("G");
			else
				output.getOutItemList()[ol_number - 1].setOutBrandGeneric("B");

			// **************************************
			// createOrderLineInstanceFromCopy
			// **************************************

			ib.debugOut("<<< NewOrderSession - creating OrderLine Object with the following Key attributes: "
					+ ib.getCurrentTime());

			ib.debugOut("<<< 	    	 - output.getOutOrderId() =  "
					+ output.getOutOrderId());
			ib.debugOut("<<<  		     - output.getOutDistrictId() = "
					+ output.getOutDistrictId());
			ib.debugOut("<<< 			 - output.getOutWarehouseId() =  "
					+ output.getOutWarehouseId());
			ib.debugOut("<<< 			 - olNumber =  " + ol_number);
			ib.debugOut("<<< 			 - output.getOutItemList()[ol_number - 1].getOutItemId() =  "
					+ output.getOutItemList()[ol_number - 1].getOutItemId());
			ib.debugOut("<<< 		     - output.getOutItemList()[ol_number - 1].getOutItemSupplyWarehouseId() =  "
					+ output.getOutItemList()[ol_number - 1]
							.getOutItemSupplyWarehouseId());
			ib.debugOut("<<< 			 - timestamp =  " + timestamp);
			ib.debugOut("<<< 			 - new java.math.BigDecimal(output.getOutItemList()[ol_number - 1].getOutItemQuantity())  "
					+ new java.math.BigDecimal(
							output.getOutItemList()[ol_number - 1]
									.getOutItemQuantity()));
			ib.debugOut("<<< 			 - output.getOutItemList()[ol_number - 1].getOutItemTotal()  "
					+ output.getOutItemList()[ol_number - 1].getOutItemTotal());
			ib.debugOut("<<< 		     - s_dist_info =  " + s_dist_info);

			try {
				createOrderLineInstance(
						output.getOutOrderId(),
						output.getOutDistrictId(),
						output.getOutWarehouseId(),
						ol_number,
						output.getOutItemList()[ol_number - 1].getOutItemId(),
						output.getOutItemList()[ol_number - 1]
								.getOutItemSupplyWarehouseId(),
						timestamp,
						new java.math.BigDecimal(
								output.getOutItemList()[ol_number - 1]
										.getOutItemQuantity()),
						output.getOutItemList()[ol_number - 1]
								.getOutItemTotal(), s_dist_info, em, ut);
			} catch (Exception e) {
				throw new ServletException(
						"Exception creating orderline Instance", e);
			}

		}

		// end loop

		// **********************************************************
		// 2.4.2.2 bullet 6 - CREATE NEWORDER object & SET values in it &
		// output.
		// ************************************************************

		ib.debugOut("<<< NewOrderSession - creating NewOrder Object with the following Key attributes: "
				+ ib.getCurrentTime());
		ib.debugOut("<<< 				  - WarehouseId =  " + input.getInWareHouseId());
		ib.debugOut("<<<  			      - DistrictId = " + input.getInDistrictId());
		ib.debugOut("<<< 				  - OrderId =  " + output.getOutOrderId());
		// debugOut("<<<*NO " + input.inWareHouseId + " " + input.inDistrictId +
		// " "+output.outOrderId+" " +getCurrentTime() );

		beforeNewOrderTime = new java.util.Date().toString() + " "
				+ String.valueOf(System.currentTimeMillis());
		try {
			createNewOrderInstance(output.getOutOrderId(),
					input.getInDistrictId(), input.getInWareHouseId(), em, ut);
		} catch (Exception e) {
			throw new ServletException("Exception creating newOrder Instance",
					e);
		}

		afterNewOrderTime = new java.util.Date().toString() + " "
				+ String.valueOf(System.currentTimeMillis());

		// ************************************************************
		// 2.4.2.2 bullet 9 Calculate outTotal including taxes & discounts
		// **********************************************************

		/*
		 * if (this.businessRuleBeansEnabled) { //Reset customerDiscount
		 * (overriding the value from the DB2 tables) by invoking Business Rule
		 * Beans double customerDiscount = getCustomerStatusDiscount(
		 * output.getOutCustomerCredit(), customerYTDPayment);
		 * output.setOutCustomerDiscount(customerDiscount);
		 * 
		 * debugOut( " < < < Total Price before taxes & discount = " +
		 * output.getOutTotal()); debugOut(" < < < Warehouse Tax = " +
		 * output.getOutWarehouseTax()); debugOut(" < < < District Tax = " +
		 * output.getOutDistrictTax()); debugOut( " < < < Customer Discount = "
		 * + output.getOutCustomerDiscount()); }
		 */

		output.setOutTotal(output.getOutTotal()
				* (1 + output.getOutWarehouseTax() + output.getOutDistrictTax())
				* (1 - (output.getOutCustomerDiscount() / 100)));

		ib.endOfTran("NewOrderServlet");

		output.setStcnum(irwwbase.Stcnum.getInstance().getStcnum());

		// If the request originator is a java client then the work area is not
		// going to be available
		// so just return and exit.
		// if (input.originatorIsJavaClient())
		// return output;
		// If WorkArea is Enabled Verify The Tax Rates using the WorkArea Before
		// Returning
		// if (checkEnableTaxWorkArea())
		// this.verifyTaxRateFromWorkArea(output);
		try {
			ut.commit();
		} catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | RollbackException | SystemException e) {
			e.printStackTrace();
			
			//ut.rollback();
			//xniu - cannot do a rollback after a commit
			throw new ServletException("Exception committing user transaction",
					e);
		} 
		
		try {
			if (em!=null && em.isOpen()){
				em.close();
			}

		 }catch(Exception e){
				System.out.println("<<< NewOrderServlet - em close() - Exception: " + e);
				e.printStackTrace();				
		 }
		
		try {
			if (WEBSOCKET_EXISTS == true) {
				if (WEBSOCKET_1_0 == true) {
					ib.debugOut("<<< Sending JMS message to WebSocket1.0 MessageDrivenBean");
					this.sendJmsMessageToMonitorTransactionsLite_WebSocket1_0(output);
				}
				if (WEBSOCKET_1_1 == true) {
					ib.debugOut("<<< Sending JMS message to WebSocket1.1 MessageDrivenBean");
					this.sendJmsMessageToMonitorTransactionsLite_WebSocket1_1(output);
				}
			}
		} catch (Exception e) {
			ib.debugOut("<<< sendJmsMessageToMonitorTransactionsLite failed: " + e.getMessage());
			e.printStackTrace();
		}
		
		return output;

	}
	
	private void sendJmsMessageToMonitorTransactionsLite_WebSocket1_0(NewOrderOutputInfo output) throws Exception {
	
		QueueConnectionFactory queueConnectionFactory = null;
		QueueConnection queueConnection = null;
		QueueSession queueSession = null;
		MessageProducer producer = null;
		MapMessage message = null;
		try {
	        // create the connection to JMS
			queueConnectionFactory = (QueueConnectionFactory) new InitialContext().lookup("java:comp/env/jms/MonitorQueueCF1.0");
			queueConnection = queueConnectionFactory.createQueueConnection();
			
			// start the connection and session
			queueConnection.start();
			queueSession = queueConnection.createQueueSession(false, jakarta.jms.Session.AUTO_ACKNOWLEDGE);	
			
			// create the queue and producer
			Queue queue = (Queue) new InitialContext().lookup("java:comp/env/jms/MonitorQ1.0");
			producer = queueSession.createProducer(queue);
			
			// build the message and send it
			message = queueSession.createMapMessage();
			message.setString("type", "neworder");
			int id = output.getOutWarehouseId();
			message.setInt("id", id);
			message.setDouble("value", output.getOutTotal());
			producer.send(message);
			ib.debugOut("<<< Send JMS message to WebSocket1.0 completed");
			
		} catch (JMSException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			
			// No matter what happens, always cleanup resources used
			cleanup(queueConnection, queueSession, producer);
		}
	}
	
	
	private void sendJmsMessageToMonitorTransactionsLite_WebSocket1_1(NewOrderOutputInfo output) throws Exception {
		
		QueueConnectionFactory queueConnectionFactory = null;
		QueueConnection queueConnection = null;
		QueueSession queueSession = null;
		MessageProducer producer = null;
		MapMessage message = null;
		try {
	        // create the connection to JMS
			queueConnectionFactory = (QueueConnectionFactory) new InitialContext().lookup("java:comp/env/jms/MonitorQueueCF1.1");
			queueConnection = queueConnectionFactory.createQueueConnection();
			
			// start the connection and session
			queueConnection.start();
			queueSession = queueConnection.createQueueSession(false, jakarta.jms.Session.AUTO_ACKNOWLEDGE);	
			
			// create the queue and producer
			Queue queue = (Queue) new InitialContext().lookup("java:comp/env/jms/MonitorQ1.1");
			producer = queueSession.createProducer(queue);
			
			// build the message and send it
			message = queueSession.createMapMessage();
			message.setString("type", "neworder");
			int id = output.getOutWarehouseId();
			message.setInt("id", id);
			message.setDouble("value", output.getOutTotal());
			producer.send(message);
			ib.debugOut("<<< Send JMS message to WebSocket1.1 completed");
			
		} catch (JMSException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			
			// No matter what happens, always cleanup resources used
			cleanup(queueConnection, queueSession, producer);
		}
	}
	
			
	private void cleanup(QueueConnection queueConnection,
			QueueSession queueSession, MessageProducer producer) throws Exception {
		
		// cleanup may be called on resources that have not been initialize
		// so be sure to check null first!
		
		try {	
			ib.debugOut("<<< Enter cleanup() to close down JMS resources");
			
			if (producer!=null) producer.close();
			if (queueSession!=null) queueSession.close();
			if (queueConnection!=null) queueConnection.close();
			
		} catch (JMSException j) {
			ib.debugOut("<<< Caught JMSException in cleanup(): " + j.getMessage());
			j.printStackTrace();
			Exception e = j.getLinkedException();
			if (e != null) {
				throw new Exception("Caught JMSException in cleanup()", e);
			} else {
				throw new Exception("Caught JMSException in cleanup()", j);
			}
			
		} catch (Exception e) {
			ib.debugOut("<<< Caught Exception in cleanup(): " + e.getMessage());
			e.printStackTrace();
			throw new Exception("Caught Exception in cleanup()", e); 

		} finally {
			ib.debugOut("<<< Exit cleanup() after closing down JMS resources");
		}
	}

	
	protected WarehouseJPA getWarehouseInstance(short warehouseId,
			EntityManager em, UserTransaction ut) throws ServletException,
			IllegalStateException, SecurityException, SystemException {

		ib.debugOut("warehouseId in getWarehouseInstance " + warehouseId);
		WarehouseJPA warehouse = null;

		try {

			warehouse = em.find(WarehouseJPA.class, warehouseId,
					LockModeType.PESSIMISTIC_READ);
			if (warehouse == null)
				throw new ServletException("warehouse not found, warehouse="
						+ warehouseId + " " + ib.getSTC());

		} catch (Exception t) {
			t.printStackTrace();
			ut.rollback();
			if (ib.is913(t))
				throw new ServletException("<&!@" + ib.checkFor913(t)
						+ ", finding Warehouse JPA, " + t.getCause()
						+ ib.getSTC() + ib.getStackTrace(t));
			throw new ServletException("<&!@"
					+ "failure in getWarehouseInstance: "
					+ " exception message: " + t.getMessage()
					+ ", exception cause: " + t.getCause() + ib.getSTC()
					+ ib.getStackTrace(t));
		} // end try
		return warehouse;

	}

	protected DistrictJPA getDistrictInstance(short districtId,
			short warehouseId, EntityManager em, UserTransaction ut)
			throws ServletException, IllegalStateException, SecurityException,
			SystemException {

		DistrictJPA district = null;
		try {
			DistrictKey key = new DistrictKey();
			key.setDistrictId(districtId);
			key.setDistrictWareId(warehouseId);

			// kem.getFetchPlan().setReadLockMode(LockModeType.WRITE);
			district = em.find(DistrictJPA.class, key,
					LockModeType.PESSIMISTIC_WRITE);
			// kem.getFetchPlan().setReadLockMode(null);
			if (district == null)
				throw new ServletException("district not found, districtId="
						+ districtId + " and warehouseId=" + warehouseId + " "
						+ ib.getSTC());

		} catch (Exception t) {
			t.printStackTrace();
			ut.rollback();
			if (ib.is913(t))
				throw new ServletException("<&!@" + ib.checkFor913(t)
						+ ", finding District JPA, " + t.getCause()
						+ ib.getSTC() + ib.getStackTrace(t));
			else
				throw new ServletException("<&!@"
						+ "failure in getDistrictInstance: "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause() + ib.getSTC()
						+ ib.getStackTrace(t));
		} // end try
		return district;

	}

	protected ItemJPA getItemInstance(int itemNum, EntityManager em,
			UserTransaction ut) throws ServletException, IllegalStateException,
			SecurityException, SystemException {

		ItemJPA item = null;
		try {
			// Uncomment next two lines and remove em from the method arg list
			// for HP
			// EntityManager em = null;

			// em = emf2.createEntityManager();

			item = em.find(ItemJPA.class, itemNum);
			if (item == null)
				throw new ServletException("item not found, item=" + item + " "
						+ ib.getSTC());
			ib.debugOut("inside getitemInstance" + item.getItemId()
					+ item.getItemPrice());

		} catch (Exception t) {
			t.printStackTrace();
			ut.rollback();
			if (ib.is913(t))
				throw new ServletException("<&!@" + ib.checkFor913(t)
						+ ", finding Item JPA, " + t.getCause() + ib.getSTC()
						+ ib.getStackTrace(t));
			throw new ServletException("<&!@" + "failure in getItemInstance: "
					+ " exception message: " + t.getMessage()
					+ ", exception cause: " + t.getCause() + ib.getSTC()
					+ ib.getStackTrace(t));
		} // end try
		return item;

	}

	private CustomerJPA getCustomerInstance(short customerId, short districtId,
			short warehouseId, EntityManager em, UserTransaction ut)
			throws ServletException, IllegalStateException, SecurityException,
			SystemException {
		CustomerJPA customer = null;
		try {

			CustomerKey key = new CustomerKey();
			key.setCustomerDId(districtId);
			key.setCustomerId(customerId);
			key.setCustomerWId(warehouseId);

			customer = em.find(CustomerJPA.class, key,
					LockModeType.PESSIMISTIC_READ);
			if (customer == null)
				throw new ServletException("customer not found, districtId="
						+ districtId + ", customerId=" + customerId
						+ " and warehouseId=" + warehouseId + " " + ib.getSTC());

		} catch (Exception t) {
			t.printStackTrace();
			ut.rollback();
			if (ib.is913(t))
				throw new ServletException("<&!@" + ib.checkFor913(t)
						+ ", finding Customer JPA, " + t.getCause()
						+ ib.getSTC() + ib.getStackTrace(t));
			throw new ServletException("<&!@"
					+ "failure in getCustomerInstance: "
					+ " exception message: " + t.getMessage()
					+ ", exception cause: " + t.getCause() + ib.getSTC()
					+ ib.getStackTrace(t));
		} // end try
		return customer;

	}

	private void createOrderInstance(int outOrderId, short inDistrictId,
			short inWareHouseId, short inCustomerId, Timestamp orderEntryDat,
			short orderCarrierId, BigDecimal orderLineCount,
			short orderAllLocal, EntityManager em, UserTransaction ut)
			throws ServletException, IllegalStateException, SecurityException,
			SystemException {
		try {
			OrderJPA order = new OrderJPA();
			order.setCustomerId(inCustomerId);
			order.setDistrictId(inDistrictId);
			order.setOrderId(outOrderId);
			order.setWarehouseId(inWareHouseId);
			order.setEntryDate(orderEntryDat);
			order.setCarrierId(orderCarrierId);
			order.setOrderLineCount(orderLineCount);
			order.setAllLocal(orderAllLocal);

			em.persist(order);

		} catch (Exception ex) {
			System.out.println("Error creating order instance");
			ex.printStackTrace();
			ut.rollback();
			if (ib.is913(ex))
				throw new ServletException("<&!@" + ib.checkFor913(ex)
						+ ", creating Order JPA, " + ex.getCause()
						+ ib.getSTC() + ib.getStackTrace(ex));
			else
				throw new ServletException("<&!@"
						+ "Error creating order instance"
						+ " exception message: " + ex.getMessage()
						+ ", exception cause: " + ex.getCause() + ib.getSTC()
						+ ib.getStackTrace(ex));

		} // end try

		ib.debugOut("<<< In NewOrderServlet, OrderJPA instance created where WarehouseId = "
				+ inWareHouseId
				+ " District Id = "
				+ inDistrictId
				+ " OrderId = " + outOrderId);

		return;

	}

	public java.math.BigDecimal convertToMoney(double doubleVal) {

		java.math.BigDecimal outMoney = new java.math.BigDecimal(doubleVal)
				.setScale(2, RoundingMode.HALF_EVEN);

		return outMoney;

	}

	private void createOrderLineInstance(int outOrderId, short outDistrictId,
			short outWarehouseId, int ol_number, int outItemId,
			short outItemSupplyWarehouseId, Timestamp orderDeliveryDate,
			BigDecimal quantity, double amount, String districtInfo,
			EntityManager em, UserTransaction ut) throws ServletException,
			IllegalStateException, SecurityException, SystemException {
		

		java.math.BigDecimal amt = this.convertToMoney(amount);

		ib.debugOut("oiginal value of amt is: " + amt);
		if (amt.doubleValue() > 9999.99)
			amt = new BigDecimal(9999.00);
		amt.setScale(2, RoundingMode.DOWN);
		ib.debugOut("amt=" + amt);

		ib.debugOut("oiginal value of quantity is: " + quantity);
		if (quantity.intValue() > 99)
			quantity = new BigDecimal(99.00);
		quantity.setScale(0, RoundingMode.DOWN);
		ib.debugOut("quantity=" + quantity);

		try {
			OrderKey orderKey = new OrderKey(outDistrictId, outOrderId, outWarehouseId);
			OrderJPA order = em.getReference(OrderJPA.class, orderKey);
			OrderlineJPA orderline = new OrderlineJPA();
			orderline.setOrder(order);
			orderline.setOlNumber((short) ol_number);
			orderline.setOlItemId(outItemId);
			orderline.setOlSupplyWarehouseId(outItemSupplyWarehouseId);
			orderline.setOlDeliveryDate(orderDeliveryDate);
			orderline.setOlQuantity(quantity);
			orderline.setOlAmount(amt);
			orderline.setOlDistrictInfo(districtInfo);

			em.persist(orderline);

		} catch (Exception ex) {
			System.out.println("Error creating orderline instance");
			ex.printStackTrace();
			ut.rollback();
			if (ib.is913(ex))
				throw new ServletException("<&!@" + ib.checkFor913(ex)
						+ ", creating Orderline JPA, " + ex.getCause()
						+ ib.getSTC() + ib.getStackTrace(ex));
			else
				throw new ServletException("<&!@"
						+ "Error creating orderline instance"
						+ " exception message: " + ex.getMessage()
						+ ", exception cause: " + ex.getCause() + ib.getSTC()
						+ ib.getStackTrace(ex));
		}

		return;

	}

	private void createNewOrderInstance(int outOrderId, short inDistrictId,
			short inWareHouseId, EntityManager em, UserTransaction ut)
			throws ServletException, IllegalStateException, SecurityException,
			SystemException {
		try {
			NewOrderJPA neworder = new NewOrderJPA();
			neworder.setOrderId(outOrderId);
			neworder.setDistrictId(inDistrictId);
			neworder.setWarehouseId(inWareHouseId);

			em.persist(neworder);

		} catch (Exception ex) {
			System.out.println("Error creating neworder instance");
			ex.printStackTrace();
			ut.rollback();
			if (ib.is913(ex))
				throw new ServletException("<&!@" + ib.checkFor913(ex)
						+ ", creating NewOrder JPA, " + ex.getCause()
						+ ib.getSTC() + ib.getStackTrace(ex));
			throw new ServletException("<&!@"
					+ "Error creating neworder instance" + ib.getSTC()
					+ ib.getStackTrace(ex));
		} // end try

		ib.debugOut("<<< In NewOrderServlet, NewOrderJPA instance created where WarehouseId = "
				+ inWareHouseId
				+ " District Id = "
				+ inDistrictId
				+ " OrderId = " + outOrderId);

		return;
	}

	private StockJPA getStockInstance(int outItemId,
			short outItemSupplyWarehouseId, EntityManager em)
			throws ServletException {
		
		StockJPA stock = null;
		try {

			StockKey key = new StockKey();
			key.setS_I_ID(outItemId);
			key.setS_W_ID(outItemSupplyWarehouseId);

			// kem.getFetchPlan().setReadLockMode(LockModeType.WRITE);
			stock = em
					.find(StockJPA.class, key, LockModeType.PESSIMISTIC_WRITE);
			// kem.getFetchPlan().setReadLockMode(null);
			if (stock == null)
				throw new ServletException("stock not found, item=" + outItemId
						+ " and warehouse=" + outItemSupplyWarehouseId + " "
						+ ib.getSTC());

		} catch (Exception t) {
			t.printStackTrace();
			if (ib.is913(t))
				throw new ServletException("<&!@" + ib.checkFor913(t)
						+ ", finding Stock JPA, " + t.getCause() + ib.getSTC()
						+ ib.getStackTrace(t));
			else
				throw new ServletException("<&!@"
						+ "failure in getStockInstance: "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause() + ib.getSTC()
						+ ib.getStackTrace(t));
		} // end try
		return stock;

	}

	private String getDistrictInfo(String districtId, StockJPA stock)
			throws ServletException {

		try {
			switch (districtId) {
			case ("1"):
				return stock.getS_DIST_01();
			case ("2"):
				return stock.getS_DIST_02();
			case ("3"):
				return stock.getS_DIST_03();
			case ("4"):
				return stock.getS_DIST_04();
			case ("5"):
				return stock.getS_DIST_05();
			case ("6"):
				return stock.getS_DIST_06();
			case ("7"):
				return stock.getS_DIST_07();
			case ("8"):
				return stock.getS_DIST_08();
			case ("9"):
				return stock.getS_DIST_09();
			case ("10"):
				return stock.getS_DIST_10();

			default:
				System.out.println("Unknown district entered");
				return "";
			}

		} catch (Exception ex) {
			System.out
					.println("Error getting district info from district JPA entity ");
			ex.printStackTrace();
			throw new ServletException(
					"Error getting district info from district JPA entity"
							+ " exception message: " + ex.getMessage()
							+ ", exception cause: " + ex.getCause()
							+ ib.getSTC() + ib.getStackTrace(ex));
		} // end try

	}

	private int checkForOverflow(int val) {
		if (val >= 9998)
			val = 0;
		return val;
	}

	private NewOrderInputInfo getManualInput(HttpServletRequest request) {

		//Comment out unused variable
		//byte database = (byte) 1;
		// database = dbSize();

		//Comment out unused variable
		//int iRc = 0;

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
		//Comment out unused variable
		//int x = actualLength * 8 + 23;

		input.setNumberOfItems(actualLength);
		input.setInItemList(itemElement);
		return input;
	}

	private UserTransaction getUserTrans() throws NamingException {
		InitialContext ctx = new InitialContext();
		UserTransaction tx = (UserTransaction) ctx
				.lookup("java:comp/UserTransaction");
		return tx;
	}

}
