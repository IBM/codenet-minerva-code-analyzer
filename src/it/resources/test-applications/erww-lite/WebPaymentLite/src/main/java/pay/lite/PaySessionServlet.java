package pay.lite;

import irwwbase.IRWWBase;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

import customerjpa.CustomerJPA;
import customerjpa.CustomerKey;
import districtjpa.DistrictJPA;
import districtjpa.DistrictKey;
import historyjpa.HistoryJPA;
import warehousejpa.WarehouseJPA;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;

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
import jakarta.persistence.Query;

/**
 * Servlet implementation class PaySessionServlet
 */
@WebServlet("/PaySessionServlet")
public class PaySessionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	IRWWBase ib = new IRWWBase();

	@PersistenceUnit(unitName = "PaySession")
	EntityManagerFactory emf;
	
	public static Boolean WEBSOCKET_EXISTS = null; // Flag that indicates whether jakarta.websocket.WebSocketContainer exists
	public static Boolean WEBSOCKET_1_0 = null; // Flag that indicates whether monitor.SimpleMonitorEndpoint exists
	public static Boolean WEBSOCKET_1_1 = null; // Flag that indicates whether monitor.SimpleMonitorEndpoint exists
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PaySessionServlet() {
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
				ib.debugOut("<<< WEBSOCKET_EXISTS = false");
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

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Client c = new Client();
		// get input object from manual input, if its there
		String button = request.getParameter("command");
		String status = "SUCCESSFUL";
		PayInput input = null;
		if (button.trim().equalsIgnoreCase("manual")) {
			input = new PayInput();
			input.setWarehouseId(Short.valueOf(request
					.getParameter("warehouseId")));
			input.setDistrictId(Short.valueOf(request
					.getParameter("districtId")));
			input.setCustomerId(Short.valueOf(request
					.getParameter("customerId")));
			input.setCustomerWarehouseId(Short.valueOf(request
					.getParameter("customerWarehouseId")));
			input.setCustomerDistrictId(Short.valueOf(request
					.getParameter("customerDistrictId")));
			input.setHistoryAmount(BigDecimal.valueOf(Double.valueOf(request
					.getParameter("historyAmount"))));
			input.setHistoryDate(request.getParameter("historyDate"));
			input.setCustomerLastName(request.getParameter("customerLastName"));
			if (input.getCustomerId() == 0)
				input.setByLastName(true);
		}

		else {
			try {
				input = c.web();
			} catch (Exception e) {
				e.printStackTrace();
				status="FAILED";
				throw new ServletException("Problem autogenning input data", e);
			}
		}
		PayOutput output = null;
		// call the paySession method to perform the payment
		try {
		    output = paySession(input);
		}
		catch (Exception e) {
			e.printStackTrace();
			status="FAILED";
			throw new ServletException("Error in PaySessionServlet: ", e);
			
		}

		// call genOput to format the output
		c.genOput(output, input,status);

		// get the formatted output
		String[] outString = c.getOput();

		// get a print writer to print out the output
		PrintWriter out = response.getWriter();

		// format and print the HTML response
		printResultsStart(out);
		printResultsMiddle(out, outString);
		printResultsEnd(out);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public PayOutput paySession(PayInput input) throws ServletException {

		ib.displayOut("Entering paySessionServlet method - (using injected ref)");

		BigDecimal tempSum;

		String spaces = "    ";
		String h_data = null;
		String w_name = null;
		String d_name = null;

		ib.startStamp("PaySessionServlet");

		// kem = OpenJPAPersistence.cast (em);

		PayOutput output = new PayOutput();
		// Set warehouseId and some other pieces of information to that
		// specified in the passed-in input structure.
		output.setWarehouseId(input.getWarehouseId());
		output.setDistrictId(input.getDistrictId());
		output.setCustomerId(input.getCustomerId());
		output.setCustomerDistrictId(input.getCustomerDistrictId());
		output.setCustomerWarehouseId(input.getCustomerWarehouseId());
		output.setHistoryDate(input.getHistoryDate());
		output.setMsg("  ");

		// Get the timestamp from the input object, not from the system
		// java.sql.Timestamp historyDate = new
		// java.sql.Timestamp(System.currentTimeMillis());
		ib.debugOut("input.getHistoryDate() = " + input.getHistoryDate());
		ib.debugOut("input.getHistoryAmount() = " + input.getHistoryAmount());

		// **********************************************************************
		// 2.5.2.2 bullet 3
		// Retrieve the Warehouse name and address.
		// (Warehouse is always the same, so no need to match it to
		// customerWarehouseId.)
		// **********************************************************************
		UserTransaction userTran = null;
		try {
			InitialContext ctx = new InitialContext();
			userTran = (UserTransaction) ctx
					.lookup("java:comp/UserTransaction");
		} catch (NamingException e) {
			e.printStackTrace();
			throw new ServletException("problem getting userTransaction", e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("problem getting userTransaction", e);
		}

		try {
			userTran.begin();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("problem on userTran.begin()", e);
		}

		EntityManager em = null;

		em = emf.createEntityManager();
		em.joinTransaction();

		// Get an instance of the Warehouse object
		WarehouseJPA warehouse = getWarehouseInstance(input.getWarehouseId(),
				em, userTran);

		w_name = warehouse.getWarehouseName();
		output.setWarehouseStreet1(warehouse.getWarehouseStreet1());
		output.setWarehouseStreet2(warehouse.getWarehouseStreet2());
		output.setWarehouseCity(warehouse.getWarehouseCity());
		output.setWarehouseState(warehouse.getWarehouseState());
		output.setWarehouseZip(warehouse.getWarehouseZip());
		// Update the Warehouse object's Ytd attribute
		tempSum = warehouse.getWarehouseYtd();
		tempSum = tempSum.add(input.getHistoryAmount());
		tempSum = tempSum.setScale(2, RoundingMode.HALF_UP);
		// Changing to 9999.99 breaks consistency checking
		// if (tempSum.doubleValue() >= 10000) tempSum=(new
		// BigDecimal(9999.99));

		warehouse.setWarehouseYtd(tempSum);
		// Local variable for passing data to MonitorTransactionsLite_WebSockets using JMS
		// See private method sendJmsMessageToMonitorTransactionsLite()
		//Comment out unused variable
		//BigDecimal warehouseYtd = tempSum;
		
		// **********************************************************************
		// 2.5.2.2 bullet 4
		// Retrieve the District name and address.
		// **********************************************************************
		DistrictJPA district = getDistrictInstance(input.getDistrictId(),
				input.getWarehouseId(), em, userTran);

		// Get an instance of the District object
		d_name = district.getDistrictName();
		output.setDistrictStreet1(district.getDistrictStreet1());
		output.setDistrictStreet2(district.getDistrictStreet2());
		output.setDistrictCity(district.getDistrictCity());
		output.setDistrictState(district.getDistrictState());
		output.setDistrictZip(district.getDistrictZip());
		ib.debugOut(" After district getters ");

		// Update the District object districtYtd attribute
		tempSum = district.getDistrictYtd();
		tempSum = tempSum.add(input.getHistoryAmount());

		// Changing to 9999.99 breaks consistency checking
		// if (tempSum.doubleValue() >= 10000) tempSum=(new
		// BigDecimal(9999.99));

		district.setDistrictYtd(tempSum.setScale(2, RoundingMode.HALF_UP));

		// **********************************************************************
		// 2.5.2.2 bullet 5 - Customer - Case 1 or Case 2
		// **********************************************************************
		CustomerJPA customer = null;
		// Search for customer (either by ID or by last name)
		if (input.isByLastName() == true) {
			// If customer is selected based on customer last name, all
			// customers with the
			// specific last name, warehouse Id and district Id will be
			// determind.
			// The resulting list of customers is sorted and the (n/2)th
			// customer is chosen.

			ib.debugOut("<<< Customer by Last Name path");

			customer = getCustomerByLastName(input.getCustomerLastName(),
					input.getCustomerDistrictId(),
					input.getCustomerWarehouseId(), em, userTran);
		} else {
			// Get an instance of the Customer object

			ib.debugOut("<<< Customer by ID path");

			customer = getCustomerInstance(input.getCustomerId(),
					input.getCustomerDistrictId(),
					input.getCustomerWarehouseId(), em, userTran);
		}

		setUpCustomerData(input, output, customer);

		// 2.5.2.2 bullet 7
		// h_data is built by concatenating warehouseName and districtName
		// separated by 4 blanks.
		h_data = new String(w_name + spaces + d_name);
		if (h_data.length() > 24) {
			System.out.println("ERWW: H_DATA LENGTH EXCEEDED 24 CHARACTERS");
			String wrkString = h_data;
			h_data = wrkString.substring(0, 24);
		}

		ib.debugOut("Before creating a History Instance");
		HistoryJPA history = new HistoryJPA();
		// ib.debugOut("HISTORY DATE " + input.getHistoryDate());

		// TEMP FIX
		Timestamp time = new Timestamp(System.currentTimeMillis());
		// System.out.print("my time|" + input.getHistoryDate() + "|");
		// ib.debugOut("generated time:" + time);
		// Timestamp time = Timestamp.valueOf(input.getHistoryDate());
		history.setHistoryDate(time);
		history.setHistoryAmount(input.getHistoryAmount());
		history.setHistoryCDId(input.getCustomerDistrictId());
		history.setHistoryCId(input.getCustomerId());
		history.setHistoryCWId(input.getCustomerWarehouseId());
		history.setHistoryData(h_data);
		history.setHistoryDId(input.getDistrictId());
		history.setHistoryWId(input.getWarehouseId());
		try {
			em.persist(history);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				userTran.rollback();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran persisting history row",
						e);
			} catch (SecurityException e1) {
				e1.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran persisting history row",
						e);
			} catch (SystemException e1) {
				e1.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran persisting history row",
						e);
			}
			throw new ServletException("error persisting history row", e);
		}
		try {
			userTran.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				userTran.rollback();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran persisting history row",
						e);
			} catch (SecurityException e1) {
				e1.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran persisting history row",
						e);
			} catch (SystemException e1) {
				e1.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran persisting history row",
						e);
			}
			throw new ServletException("problem committing userTransaction", e);
		}
		
		//TODO: send message to monitor transactions anytime the transaction succeeds, fix entity manager close to catch any possible exceptions
		//try {
			em.close();
		
		ib.debugOut("After creating a History Instance");
		ib.endStamp("PaySessionServlet");

		output.setStcnum(irwwbase.Stcnum.getInstance().getStcnum());

		try {
			if (WEBSOCKET_EXISTS == true) {
				if (WEBSOCKET_1_0 == true) {
					ib.debugOut("<<< Sending JMS message to WebSocket1.0 MessageDrivenBean");
					this.sendJmsMessageToMonitorTransactionsLite_WebSocket1_0(output, input.getHistoryAmount());
				}
				if (WEBSOCKET_1_1 == true) {
					ib.debugOut("<<< Sending JMS message to WebSocket1.1 MessageDrivenBean");
					this.sendJmsMessageToMonitorTransactionsLite_WebSocket1_1(output, input.getHistoryAmount());
				}
			}
		} catch (Exception e) {
			ib.debugOut("<<< sendJmsMessageToMonitorTransactionsLite failed: " + e.getMessage());
			e.printStackTrace();
		}

		return output;

	}
	
	private void sendJmsMessageToMonitorTransactionsLite_WebSocket1_0(PayOutput output, BigDecimal warehouseYtd) throws Exception {
	
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
			message.setString("type", "payment");
			message.setInt("id", (int)output.getWarehouseId());
			message.setDouble("value", warehouseYtd.doubleValue());
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
	
	
	private void sendJmsMessageToMonitorTransactionsLite_WebSocket1_1(PayOutput output, BigDecimal warehouseYtd) throws Exception {
		
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
			message.setString("type", "payment");
			message.setInt("id", (int)output.getWarehouseId());
			message.setDouble("value", warehouseYtd.doubleValue());
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
	

	private void cleanup(QueueConnection queueConnection, QueueSession queueSession,
			MessageProducer producer) throws Exception {
		
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
	
	protected DistrictJPA getDistrictInstance(short districtId,
			short warehouseId, EntityManager em, UserTransaction userTran)
			throws ServletException {
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
						+ ib.getSTC() + "no stack");

		} catch (Exception t) {
			t.printStackTrace();
			try {
				userTran.rollback();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding district row",
						e);
			} catch (SecurityException e) {
				e.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding district row",
						e);
			} catch (SystemException e) {
				e.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding district row",
						e);
			}
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

	protected WarehouseJPA getWarehouseInstance(short warehouseId,
			EntityManager em, UserTransaction userTran) throws ServletException {

		ib.debugOut("warehouseId in getWarehouseInstance " + warehouseId);
		WarehouseJPA warehouse = null;
		try {

			// kem.getFetchPlan().setReadLockMode(LockModeType.WRITE);

			warehouse = em.find(WarehouseJPA.class, warehouseId,
					LockModeType.PESSIMISTIC_WRITE);
			ib.debugOut("<<< em.find(WarehouseJPA.class, warehouseId,LockModeType.PESSIMISTIC_WRITE) just completed.");

			if (warehouse == null)
				throw new ServletException("warehouse not found, warehouse="
						+ warehouseId + " " + ib.getSTC() + "no stack");

			// kem.getFetchPlan().setReadLockMode(null);

		} catch (Exception t) {
			t.printStackTrace();
			try {
				userTran.rollback();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding warehouse row",
						e);
			} catch (SecurityException e) {
				e.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding warehouse row",
						e);
			} catch (SystemException e) {
				e.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding warehouse row",
						e);
			}
			if (ib.is913(t))
				throw new ServletException("<&!@" + ib.checkFor913(t)
						+ ", finding Warehouse JPA, " + t.getCause()
						+ ib.getSTC() + ib.getStackTrace(t));
			else
				throw new ServletException("<&!@"
						+ "failure in getWarehouseInstance: "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause() + ib.getSTC()
						+ ib.getStackTrace(t));
		} // end try
		return warehouse;

	}

	private CustomerJPA getCustomerInstance(short customerId, short districtId,
			short warehouseId, EntityManager em, UserTransaction userTran)
			throws ServletException {
		CustomerJPA customer = null;
		try {

			CustomerKey key = new CustomerKey();
			key.setCustomerDId(districtId);
			key.setCustomerId(customerId);
			key.setCustomerWId(warehouseId);

			// kem.getFetchPlan().setReadLockMode(LockModeType.WRITE);

			customer = em.find(CustomerJPA.class, key,
					LockModeType.PESSIMISTIC_WRITE);

			if (customer == null)
				throw new ServletException("customer not found: district="
						+ districtId + ", customer=" + customerId
						+ " and warehouse=" + warehouseId + ", " + ib.getSTC()
						+ "no stack");

			// kem.getFetchPlan().setReadLockMode(null);

		} catch (Exception t) {
			t.printStackTrace();
			try {
				userTran.rollback();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding customer row",
						e);
			} catch (SecurityException e) {
				e.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding customer row",
						e);
			} catch (SystemException e) {
				e.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding customer row",
						e);
			}

			if (ib.is913(t))
				throw new ServletException("<&!@" + ib.checkFor913(t)
						+ ", finding Customer JPA, " + t.getCause()
						+ ib.getSTC() + ib.getStackTrace(t));
			else
				throw new ServletException("<&!@"
						+ "failure in getCustomerInstance: "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause() + ib.getSTC()
						+ ib.getStackTrace(t));
		} // end try
		return customer;

	}

	public CustomerJPA getCustomerByLastName(String lastName, short districtId,
			short warehouseId, EntityManager em, UserTransaction userTran)
			throws ServletException {

		// MD11455 changed signature to include SessionCtx so CMT can set
		// Rollback for DataIntegrity
		int noCustomers = 0;
		int noCustDiv2 = 0;
		int remainder = 0;

		java.util.Vector<CustomerJPA> v = new java.util.Vector<CustomerJPA>();

		CustomerJPA cja;

		ib.debugOut("Inside getCustomerByLastName ");
		ib.debugOut("lastname= " + lastName + " districtid= " + districtId
				+ " warehouseid= " + warehouseId);

		try {

			Query q = null;
			q = em.createNamedQuery("findCustomerByLastName");

			q.setParameter("lastName", lastName);
			q.setParameter("districtId", districtId);
			q.setParameter("warehouseId", warehouseId);
			Collection<?> coll = null;
			coll = q.getResultList();
			Iterator<?> it = coll.iterator();

			while (it.hasNext()) {
				noCustomers++;
				cja = (CustomerJPA) it.next();
				v.addElement(cja);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				userTran.rollback();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding CustomerByLastName row",
						e1);
			} catch (SecurityException e1) {
				e1.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding CustomerByLastName row",
						e1);
			} catch (SystemException e1) {
				e1.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding CustomerByLastName row",
						e1);
			}
			if (ib.is913(e))
				throw new ServletException("<&!@" + ib.checkFor913(e)
						+ ", in getCustomerByLastName(....), " + e.getCause()
						+ ib.getSTC() + ib.getStackTrace(e));
			else
				throw new ServletException(
						"<&!@"
								+ "Error on query in getCustomerByLastName(....), lastName="
								+ lastName + ", district=" + districtId
								+ " and warehouse=" + warehouseId + ", "
								+ " exception message: " + e.getMessage()
								+ ", exception cause: " + e.getCause()
								+ ib.getSTC() + ib.getStackTrace(e));
		}
		if (noCustomers == 1) {
			noCustDiv2 = 1;
		} else {
			// Divide number of selected rows by 2, rounding up if there is
			// a remainder.
			// (i.e., 5/3=1)
			noCustDiv2 = noCustomers / 2;
			remainder = noCustomers % 2;

			if (remainder > 0) {
				noCustDiv2 = noCustDiv2 + 1;
			}
		}

		try {
			// need to get the lock on the Customer row by excuting
			// findByPrimaryKey in getCustomerInstance method
			// added the if loop - surya
			if (noCustDiv2 > 0) {
				cja = v.elementAt(noCustDiv2 - 1);

				em.refresh(cja, LockModeType.PESSIMISTIC_WRITE);

				// cja = v.elementAt(noCustDiv2 - 1);
				// getCustomerInstance(cja.getCustomerId(),
				// cja.getCustomerDId(), cja
				// .getCustomerWId());
			} else {
				try {
					userTran.rollback();
				} catch (IllegalStateException e1) {
					e1.printStackTrace();
					throw new ServletException(
							"exception rolling back user tran finding CustomerByLastName row",
							e1);
				} catch (SecurityException e1) {
					e1.printStackTrace();
					throw new ServletException(
							"exception rolling back user tran finding CustomerByLastName row",
							e1);
				} catch (SystemException e1) {
					e1.printStackTrace();
					throw new ServletException(
							"exception rolling back user tran finding CustomerByLastName row",
							e1);
				}
				throw new ServletException("<&!@"
						+ "Error: CustomerByLastName not found " + "lastname= "
						+ lastName + " districtid= " + districtId
						+ " warehouseid= " + warehouseId + "no stack");
			}
		} catch (Exception t) {
			System.out
					.println("%%%%% ERROR: getting customerInstance in PaymentSession failed in getCustomerByLastName method  ");
			t.printStackTrace();
			try {
				userTran.rollback();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding CustomerByLastName row",
						e1);
			} catch (SecurityException e1) {
				e1.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding CustomerByLastName row",
						e1);
			} catch (SystemException e1) {
				e1.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding CustomerByLastName row",
						e1);
			}
			if (t.getMessage().indexOf("Failed due to -913") != -1)
				throw new ServletException(
						"<&!@"
								+ "Failed due to -913, deadlock or timeout finding Customer JPA "
								+ t.getCause() + ib.getSTC()
								+ ib.getStackTrace(t));
			else
				throw new ServletException(
						"<&!@"
								+ "%%%%% ERROR: getting customerInstance in PaymentSession failed in getCustomerByLastName method  "
								+ t.getCause() + ib.getSTC()
								+ ib.getStackTrace(t));
		} // end try
		return cja;

	}

	private void setUpCustomerData(PayInput input, PayOutput output,
			CustomerJPA customer) {

		BigDecimal tempSum;
		String temp_data = null;

		String c_data = null;
		String h_amt = null;

		// From XML FeP version of ERWW: commented out for now
		// output.setCustomerId(input.getCustomerId());
		output.setCustomerId(customer.getCustomerId());
		output.setCustomerFirstName(customer.getCustomerFirst());
		output.setCustomerMiddleName(customer.getCustomerMiddle());
		output.setCustomerLastName(customer.getCustomerLast());
		output.setCustomerStreet1(customer.getCustomerStreet1());
		output.setCustomerStreet2(customer.getCustomerStreet2());
		output.setCustomerCity(customer.getCustomerCity());
		output.setCustomerState(customer.getCustomerState());
		output.setCustomerZip(customer.getCustomerZip());
		output.setCustomerPhone(customer.getCustomerPhone());
		output.setCustomerSince(new String((customer.getCustomerSince())
				.toString()));
		output.setCustomerCredit(customer.getCustomerCredit());
		output.setCustomerCreditLimit(customer.getCustomerCreditLim().setScale(
				2, RoundingMode.HALF_UP));
		output.setCustomerDiscount(customer.getCustomerDiscount());

		// Update the Customer object's attributes
		// tempSum = new
		// BigDecimal(customerCMPEntity.getCustomerBalance().toString());
		tempSum = customer.getCustomerBalance().setScale(5, 2);
		tempSum = tempSum.subtract(input.getHistoryAmount()).setScale(2,
				RoundingMode.HALF_UP);

		// Changing to 9999.99 breaks consistency checking
		// if (tempSum.doubleValue() <= -10000) tempSum=(new
		// BigDecimal(9999.99));
		// tempStr = new String(tempSum.toString());
		customer.setCustomerBalance(tempSum);
		ib.debugOut("*** setCustomerBalance = " + tempSum);
		output.setCustomerBalance(tempSum.setScale(2, RoundingMode.HALF_UP));

		tempSum = customer.getCustomerYtdPayment();
		tempSum = tempSum.add(input.getHistoryAmount());

		// Changing to 9999.99 breaks consistency checking
		// if (tempSum.doubleValue() >= 10000) tempSum=(new
		// BigDecimal(9999.99));
		// tempStr = new String(tempSum.toString());
		customer.setCustomerYtdPayment(tempSum.setScale(2,
				RoundingMode.HALF_UP));
		ib.debugOut("*** setCustomerYtdPayment = "
				+ tempSum.setScale(2, RoundingMode.HALF_UP));

		customer.setCustomerPaymentCnt(customer.getCustomerPaymentCnt().add(
				new BigDecimal(1)));

		// XML FeP version below: using Matterhorn version above instead
		// tempSum = customer.getCustomerPaymentCnt().add(new BigDecimal(1));

		// Changing to 9999.99 breaks consistency checking
		// if (tempSum.doubleValue() >= 10000) tempSum=(new
		// BigDecimal(9999.99));

		// customer.setCustomerPaymentCnt(tempSum);
		ib.debugOut("*** setCustomerPaymentCnt = " + tempSum);

		c_data = new String(customer.getCustomerData());

		// *********************************************************************
		// Section 2.5.2.2 bullet 6
		// If C_Credit is equal to "BC", then update the customer's
		// C_DATA attribute.
		// *********************************************************************
		if (output.getCustomerCredit().trim().equals("BC")) {
			// ********************************************************************
			// customerData must be shifted to the right and new
			// customer data inserted at the
			// beginning of customerData.
			// ********************************************************************
			ib.debugOut(" IRWW - Before updating customer data when Customer Credit = BC ");

			// temp_data =
			// tempInt.toString((int)output.getCustomerId());
			temp_data = (Integer.valueOf((int) output.getCustomerId())).toString();
			temp_data = temp_data + input.getCustomerDistrictId();
			temp_data = temp_data + input.getCustomerWarehouseId();
			temp_data = temp_data + input.getDistrictId();
			temp_data = temp_data + input.getWarehouseId();
			h_amt = String.valueOf(input.getHistoryAmount());
			temp_data = temp_data + h_amt;
			temp_data = temp_data + c_data; // want total length to
			// remain 500
			if (temp_data.length() > 200) {
				output.setCustomerData(new String(temp_data.substring(0, 199)));
				// Only display first 200 chars
			} else {
				output.setCustomerData(temp_data);
			}
			if (temp_data.length() > 500) {
				customer.setCustomerData(new String(temp_data.substring(0, 499)));
				ib.debugOut("*** setCustomerData (if length > 500) = "
						+ temp_data.substring(0, 499));
			} else {
				customer.setCustomerData(new String(temp_data));
				ib.debugOut("*** setCustomerData (else) = " + temp_data);
			}
		} else {
			output.setCustomerData(new String(c_data));
		}

	}

	private void printResultsStart(PrintWriter out) {
		String title = "Results of Liberty Pay Lite";

		out.println("<HTML><HEAD><TITLE>");
		out.println(title);
		out.println("</TITLE></HEAD><body bgcolor='#f8f7cd'>");
		out.println("<H1 align=\"center\">" + title + "</H1>");
		out.println("<BR><BR><BR>");

		out.println("<TABLE align='left'	border='8' bgcolor='#cccccc' bordercolor='#FFCC99'>");
		out.println("<TBODY>");
	}

	private void printResultsMiddle(PrintWriter out, String[] os) {

		for (int i = 0; i < os.length; i++) {
			out.println("<TR ALIGN=left>");
			out.println("<TH>" + os[i] + "</TH>");
			out.println("</TR>");
		}

	}

	private void printResultsEnd(PrintWriter out) {
		out.println("</TBODY>");
		out.println("</TABLE>");
	}

}
