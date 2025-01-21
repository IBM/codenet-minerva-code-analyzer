package orderstatussession.ejb;

import irwwbase.ExtendedDatabase;
import irwwbase.UserException;
import irwwbase.POJOQualifier;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.inject.Inject;

import orderjpa.OrderJPA;
import orderlinejpa.OrderlineJPA;
import orderlinejpa.OrderlineKey;
import customerjpa.CustomerJPA;
import customerjpa.CustomerKey;

@Stateless
@Local(OrderStatusSessionFacade.class)
public class OrderStatusSessionFacadeBean extends ExtendedDatabase implements
		OrderStatusSessionFacade {

	private static final long serialVersionUID = -6050036297739923390L;
	
	@Inject @POJOQualifier irwwbase.IRWWBase ib;
	
	CustomerJPA customer = null;

	OrderJPA order = null;

	OrderlineJPA orderline = null;

	private java.lang.String _className;

	private boolean flag = false;

	@PersistenceContext(unitName = "OrderStatusSession")
	EntityManager em;

	public void getCustomerByLastName(String lastName, short districtId,
			short warehouseId) throws UserException {
		// MD11455 changed signature to include SessionCtx so CMT can set
		// Rollback for DataIntegrity

		// changes by Surya
		int noCustomers = 0;
		int noCustDiv2 = 0;
		int remainder = 0;

		java.util.Vector<CustomerJPA> v = new java.util.Vector<CustomerJPA>();

		CustomerJPA cja;

		debugOut("Searching Customer with LastName " + lastName);

		try {

			Query q = em.createNamedQuery("findCustomerByLastName");
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
			throw new UserException(
					"Error on query in getCustomerByLastName(....), lastName="
							+ lastName + ", district=" + districtId
							+ " and warehouse=" + warehouseId + ", "
							+ " exception message: " + e.getMessage()
							+ ", exception cause: " + e.getCause() + getSTC(),
					getStackTrace(e));
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
			// added the IF loop - Surya
			if (noCustDiv2 > 0) {
				cja = v.elementAt(noCustDiv2 - 1);
				getCustomerInstance(cja.getCustomerId(), cja.getCustomerDId(),
						cja.getCustomerWId());

				flag = true;
			}
		} catch (Exception t) {
			System.out
					.println("%%%%% ERROR: getting customerInstance in PaymentSession failed in getCustomerByLastName method  ");
			t.printStackTrace();
			if (is913msg(t.getMessage()))
				throw new UserException("<&!@" + t.getMessage()
						+ ", finding Customer JPA, " + t.getCause() + getSTC(),
						getStackTrace(t));
			throw new UserException(
					"<&!@"
							+ "%%%%% ERROR: getting customerInstance in PaymentSession failed in getCustomerByLastName method  "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause() + getSTC(),
							getStackTrace(t));
		} // end try

		// end changes

	}

	protected void getCustomerInstance(short customerId, short districtId,
			short warehouseId) throws UserException {

		// Changes by Surya
		try {

			CustomerKey key = new CustomerKey();
			key.setCustomerDId(districtId);
			key.setCustomerId(customerId);
			key.setCustomerWId(warehouseId);

			customer = em.find(CustomerJPA.class, key);
			if (customer == null)
				throw new UserException("customer not found: district="
						+ districtId + ", customer=" + customerId
						+ " and warehouse=" + warehouseId + ", " + getSTC(),"");

			flag = true;

		} catch (Exception t) {
			t.printStackTrace();
			if (is913(t))
				throw new UserException("<&!@" + checkFor913(t)
						+ ", finding Customer JPA, " + t.getCause() + getSTC(),
						getStackTrace(t));
			throw new UserException("<&!@"
					+ "failure in getCustomerInstance: "
					+ " exception message: " + t.getMessage()
					+ ", exception cause: " + t.getCause() + getSTC(), getStackTrace(t));
		} // end try
	}

	/**
	 * Insert the method's description here. Creation date: (28/09/00 10:22:36)
	 * 
	 * @param inCustomerId
	 *            java.lang.String
	 * @param inCustomerWarehouseId
	 *            java.lang.String
	 * @param inCustomerDistrictId
	 *            java.lang.String
	 * @param output
	 *            irwwbase.OrderStatusOutput
	 * @throws UserException 
	 * @exception java.rmi.RemoteException
	 *                The exception description.
	 * @exception irwwbase.UserException
	 *                The exception description.
	 */
	private void getOrder(short inCustomerId, short inCustomerWarehouseId,
			short inCustomerDistrictId, OrderStatusOutput output) throws UserException {

		int orderId = 0;
		// Changes by Surya
		try {

			Query q = em.createNamedQuery("findMaxOrderId");
			q.setParameter("customerId", inCustomerId);
			q.setParameter("districtId", inCustomerDistrictId);
			q.setParameter("warehouseId", inCustomerWarehouseId);

			order = (OrderJPA) q.getSingleResult();
			if (order == null)
				throw new RuntimeException("order not found " + getSTC());
			debugOut("Inside getorder()");

			if (order != null) {
				orderId = order.getOrderId();
				debugOut("OrderId" + order.getOrderId());
				debugOut("OrderEntryDate" + order.getEntryDate());
				debugOut("OrderCarrierId" + order.getCarrierId());

				output.setOutOrderEntryDate(order.getEntryDate().toString());
				output.setOutOrderCarrierId(order.getCarrierId());
				output.setOutOrderLineCount((short) order.getOrderLineCount()
						.intValue());
			}

			output.setOutOrderId(orderId);

			if (orderId == 0) {
				output.setOutErrorMsg("There are no Orders for CustomerID "
						+ inCustomerId);
			}

		} catch (Exception t) {
			t.printStackTrace();
			System.out.println("orderid: " + orderId + "customerid: "
					+ inCustomerId + "districtid: " + inCustomerDistrictId
					+ "warehouseid: " + inCustomerWarehouseId);
			if (is913(t))
				throw new UserException("<&!@" + checkFor913(t)
						+ ", finding max Order JPA, " + t.getCause(), getStackTrace(t));
			else
				throw new UserException("<&!@"
						+ "failure in getOrder: orderid: " + orderId
						+ "customerid: " + inCustomerId + "districtid: "
						+ inCustomerDistrictId + "warehouseid: "
						+ inCustomerWarehouseId + ", " + " exception message: "
						+ t.getMessage() + ", exception cause: " + t.getCause()
						+ getSTC(), getStackTrace(t));
		}
	}

	protected void getOrderlineInstance(int orderId, short districtId,
			short warehouseId, short orderlineNumber) throws UserException {

		// changes by Surya

		try {

			OrderlineKey key = new OrderlineKey(orderId, districtId, warehouseId, orderlineNumber);

			orderline = em.find(OrderlineJPA.class, key);
			if (orderline == null)
				throw new UserException("orderline not found orderId="
						+ orderId + ", districtId=" + districtId
						+ ", warehouseId=" + warehouseId
						+ " and orderlineNumber=" + orderlineNumber + ", "
						+ getSTC(),"no stack");

		} catch (Exception t) {
			t.printStackTrace();
			if (is913(t))
				throw new UserException(
						"<&!@" + checkFor913(t) + ", finding Orderline JPA, "
								+ t.getCause() + getSTC(), getStackTrace(t));
			throw new UserException("<&!@" + "failure in getOrderInstance: "
					+ " exception message: " + t.getMessage()
					+ ", exception cause: " + t.getCause() + getSTC(), getStackTrace(t));
		}

		// end changes by Surya
	}

	/**
	 * Insert the method's description here. Creation date: (28/09/00 13:19:52)
	 * 
	 * @param inOrderlineCount
	 *            double
	 * @param inOrderId
	 *            java.lang.String
	 * @param inCustomerWarehouseId
	 *            java.lang.String
	 * @param inCustomerDistrictId
	 *            java.lang.String
	 * @throws UserException 
	 * @exception java.rmi.RemoteException
	 *                The exception description.
	 * @exception irwwbase.UserException
	 *                The exception description.
	 */
	private void getOrderlines(short inOrderlineCount, int inOrderId,
			short inCustomerWarehouseId, short inCustomerDistrictId,
			OrderStatusOutput output) throws UserException {
		String deliveryDate = null;
		int count = (Double.valueOf(inOrderlineCount)).intValue();
		output.outOrderLines = new OrderLineSingleItem[count];
		short k;

		try {

			for (short i = 0; i < count; i++) {

				k = i;
				k++;
				getOrderlineInstance(inOrderId, inCustomerDistrictId,
						inCustomerWarehouseId, k);

				output.outOrderLines[i] = new OrderLineSingleItem();

				output.outOrderLines[i].outOlSupplyWarehouseId = orderline
						.getOlSupplyWarehouseId();
				output.outOrderLines[i].outOlItemId = orderline.getOlItemId();
				output.outOrderLines[i].outOlQuantity = orderline
						.getOlQuantity().doubleValue();
				output.outOrderLines[i].outOlAmount = orderline.getOlAmount()
						.doubleValue();

				if (orderline.getOlDeliveryDate() == null)
					output.outOrderLines[i].outOlDeliveryDate = "";
				else {
					deliveryDate = orderline.getOlDeliveryDate().toString();
					output.outOrderLines[i].outOlDeliveryDate = deliveryDate;
				}
			}
		} catch (Exception ex) {
			System.out
					.println("********** S T A C K  T R A C E **************");
			ex.printStackTrace();
			if (ex.getMessage().indexOf("Failed due to -913") != -1)
				throw new 	UserException(
						"<&!@"
								+ "Failed due to -913, deadlock or timeout finding Orderline JPA"
								+ ex.getCause() + getSTC(), getStackTrace(ex));
			else
				throw new UserException("<&!@"
						+ "Method getOrderlines of class " + _className
						+ " failed!!! " + " exception message: "
						+ ex.getMessage() + ", exception cause: "
						+ ex.getCause() + getSTC(), getStackTrace(ex));
		}
	}

	/**
	 * Insert the method's description here. Creation date: (26/09/00 13:30:52)
	 * 
	 * @return stockLevelSessionPackage.StockLevelOutput
	 * @param input
	 *            stockLevelSessionPackage.StockLevelInput
	 * @exception java.rmi.RemoteException
	 *                The exception description.
	 * @exception jakarta.ejb.FinderException
	 *                The exception description.
	 */
	public OrderStatusOutput orderStatusSession(OrderStatusInput input)
			throws UserException {
		OrderStatusOutput output = new OrderStatusOutput();
		// Set class name of class
		_className = (OrderStatusSessionFacadeBean.class).getName();
		startOfTran("OrderStatusSessionEJBLite");

		flag = false;

		// UserTransaction ut=mySessionCtx.getUserTransaction();

		// Set warehouseId and districtId to that specified in the passed-in
		// input structure.
		output.setOutCustomerWarehouseId(input.getInCustomerWarehouseId());
		output.setOutCustomerDistrictId(input.getInCustomerDistrictId());

		// **********************************************************************
		// 2.6.2.2 bullet 3
		// **********************************************************************

		// Search for customer (either by ID or by last name)

		if (input.isInByLastName()) {
			// 2.6.2.2 bullet 3 - Case 1
			getCustomerByLastName(input.getInCustomerLastName(), input
					.getInCustomerDistrictId(), input
					.getInCustomerWarehouseId());

		} else {
			// 2.6.2.2 bullet 3 - Case 2
			getCustomerInstance(input.getInCustomerId(), input
					.getInCustomerDistrictId(), input
					.getInCustomerWarehouseId());

		}
		// Set customer data in output structure.

		debugOut(" inside OrderStatusSessionFacadeBean - orderstatusSession(input)");

		if (!flag) {
			CustomerKey key = new CustomerKey();
			key.setCustomerDId(input.getInCustomerDistrictId());
			key.setCustomerId(input.getInCustomerId());
			key.setCustomerWId(input.getInCustomerWarehouseId());
			customer = em.find(CustomerJPA.class, key);
		}
		if (customer != null) {
			output.setOutCustomerId(customer.getCustomerId());
			output.setOutCustomerFirstName(customer.getCustomerFirst());
			output.setOutCustomerMiddleName(customer.getCustomerMiddle());
			output.setOutCustomerLastName(customer.getCustomerLast());
			output.setOutCustomerBalance((customer.getCustomerBalance())
					.doubleValue());

			// ****************************************************************************************
			// 2.6.2.2 bullet 4
			// the row in the order table with matching O_W_ID (equals C_W_ID),
			// O_D_ID (equals C_D_ID),
			// O_C_ID (equals C_ID), and with the largest existing O_ID, is
			// selected. This is the most
			// recent order placed by the customer. O_ID, O_ENTRY_DATE, and
			// O_CARRIER_ID are retrieved.
			// ****************************************************************************************
			debugOut("Before calling getorder()");
			debugOut("OrderId" + output.getOutOrderId());
			debugOut("CustomerId" + output.getOutCustomerId());

			getOrder(output.getOutCustomerId(), output
					.getOutCustomerWarehouseId(), output
					.getOutCustomerDistrictId(), output);

			// **********************************************************************************************
			// 2.6.2.2 bullet 5
			// all rows in the orderline table with matching OL_W_ID (equals
			// O_W_ID), OL_D_ID (equals O_D_ID),
			// and OL_O_ID (equals O_ID) are selected and the corresponding set
			// of
			// OL_I_ID, OL_SUPPLY_W_ID,
			// OL_QUANTITY, OL_AMOUNT, and OL_DELIVERY_D are retrieved.
			// ***********************************************************************************************

			getOrderlines(output.getOutOrderLineCount(),
					output.getOutOrderId(), output.getOutCustomerWarehouseId(),
					output.getOutCustomerDistrictId(), output);

			debugOut(" after calling getorder() and getorderLines() ");
			debugOut("OrderId" + output.getOutOrderId());
			debugOut("CustomerId" + output.getOutCustomerId());
		} else {
			output.setOutErrorMsg("Customer " + input.getInCustomerLastName()
					+ " not Found");
		}
		endOfTran("OrderStatusSessionEJBLite");
		output.setStcnum(irwwbase.Stcnum.getInstance().getStcnum());

		return output;
	}

	public void debugOut(String message) {
		/*
		 * if (System.getProperty("DEBUG_OUT_HTTP") != null) { if
		 * (System.getProperty("DEBUG_OUT_HTTP").equals("ON")) { String ts =
		 * (new Timestamp(System.currentTimeMillis())) .toString(); String tn =
		 * Thread.currentThread().getName(); System.out.println("Trace: " + ts + " " +
		 * tn + " " + message); } }
		 */
		if (System.getProperty("DEBUG_OUT") != null) {
			if (System.getProperty("DEBUG_OUT").equals("ON")) {
				String ts = (new Timestamp(System.currentTimeMillis()))
						.toString();
				String tn = Thread.currentThread().getName();
				System.out.println("Trace: " + ts + " " + tn + " " + message);
			}
		}
	}

}
