package orderstatus.concurrent.ejb.lite;

import irwwbase.ExtendedDatabase;
import irwwbase.ExtendedRandom;
import irwwbase.IRWWBase;
import irwwbase.MaxValues;
import irwwbase.UserException;

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import orderjpa.OrderJPA;
import orderlinejpa.OrderlineJPA;
import orderlinejpa.OrderlineKey;
import customerjpa.CustomerJPA;
import customerjpa.CustomerKey;

/**
 *  
 * The OrderStatusSessionBean EJB is a stateless session bean.  The key method of this
 * bean is "orderStatusSession" which provide an autogen version of the order status
 * transaction.  Each order status task running in the concurrency service holds an
 * ejb reference to one of these stateless session beans so that an order status
 * transaction can be performed.
 *
 * One thing that is unique about this bean is that it allows the order status
 * transaction to be interrupted.  In the orderStatusSession method a check is made
 * each time before the code goes off to do work in the database.  If the thread state
 * is found to be interrupted, then work stops immediately, and an InterruptedException
 * is thrown.
 * 
 * Business exceptions that occur during the course of processing an order status
 * transaction are returned as UserExceptions to the calling task.                                                                                                                    
 */

@Stateless
@DeclareRoles("CustomerRole")
@RolesAllowed("CustomerRole")
public class OrderStatusSessionBean extends ExtendedDatabase {

	private static final long serialVersionUID = -6050036297739923390L;
	
	private IRWWBase ib = new IRWWBase();
	
	private CustomerJPA customer = null;

	private OrderJPA order = null;

	private OrderlineJPA orderline = null;

	private java.lang.String _className;

	@PersistenceContext(unitName = "OrderStatusConcurrentLite")
	private EntityManager em;


	/**
	 * The getCustomerInstance method sets the JPA Customer object variable in this bean based on the input warehouse, district, and customer IDs.
	 * 
	 * @param inCustomerId
	 *            short
	 * @param inCustomerWarehouseId
	 *            short
	 * @param inCustomerDistrictId
	 *            short
	 * @param output
	 *            OrderStatusOutput
	 * @throws UserException 
     *	
	 * @exception irwwbase.UserException
	 *                User exceptions are business logic exceptions.
	 */	
	private void getCustomerInstance(short customerId, short districtId,
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
	 * The getOrder method returns the newest (highest number) order for this warehouse, district, and customer combination.
	 * 
	 * @param inCustomerId
	 *            short
	 * @param inCustomerWarehouseId
	 *            short
	 * @param inCustomerDistrictId
	 *            short
	 * @param output
	 *            OrderStatusOutput
	 * @throws UserException 
     *	
	 * @exception irwwbase.UserException
	 *                User exceptions are business logic exceptions.
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

	private void getOrderlineInstance(int orderId, short districtId,
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
	 * The getOrderLines method fills the output object with information from the orderlines
	 * for the input order.
	 *
	 * @param inOrderLinecount
	 *            short
	 * @param inOrderId
	 *            int
	 * @param inCustomerWarehouseId
	 *            short
	 * @param inCustomerDistrictId
	 *            short
	 * @parm output
	 *            OrderStatusOutput object which is filled in with orderlines
	 *                       
	 * @throws UserException 
	 *
	 * @exception irwwbase.UserException
	 *                User exceptions are business logic exceptions.
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
	 * The orderStatusSession method completes an "autogen" version of the order status transaction
	 * each time it is invoked.  The transaction has been written to be interruptible.  The code
	 * that the code checks to see if the thread has been asked to interrupt each time before it
	 * goes to do activity in the DB.  If so, the method ends immediately and throws InterruptedException.
	 * 
	 * @return Boolean indicates if the transaction completed
	 *         successfully (ie. true) or failed (ie. false)
	 * 
	 * @exception UserExecption is thrown for errors caught by
	 *            the application code.  Also, InterruptedException
	 *            is thrown if the application code's thread is
	 *            interrupted during the transaction. 
	 *            
	 */
	public Boolean orderStatusSession()
			throws Exception {
		
		// This method behaves like the "autogen" version of order
		// status.  There is no "manual" input path.  Also, the
		// caller will not particularly care what the output is.
		// The order status transaction is considered to be successful
		// if some data is fetched from the DB and no Exception is raised.
		
		short warehouseId = 0;
		short districtId = 0;
		short customerId = 0;
		OrderStatusOutput output = null;
		
		// Assume failure, until proven otherwise
		Boolean returnStatus = Boolean.FALSE;
		
		// Outer try block to provide finally section for the entire
		// method.
		try {

			
			try {				
				debugOut("Enter OrderStatusSessionBean.orderSatusSession()");
				startOfTran("OrderStatusConcurrentLite");	

				output = new OrderStatusOutput();

				byte db = ib.dbSize();

				// Set class name of class
				_className = (OrderStatusSessionBean.class).getName();

				// Generate random Ids
				ExtendedRandom rand = new ExtendedRandom();
				warehouseId = ((short) rand.nextInt(1, MaxValues.warehouseId(db)));
				districtId = ((short) rand.nextInt(1, MaxValues.districtId(db)));
				customerId = ((short) rand.nextInt(1, MaxValues.customerId(db)));

				// Set warehouseId and districtId on output
				output.setOutCustomerWarehouseId(warehouseId);
				output.setOutCustomerDistrictId(districtId);

			} catch (Throwable t) {
				System.out.println("OrderStatusSessionBean.orderSatusSession() caught throwable during initial section: " + t.toString());
				t.printStackTrace();
				throw new UserException("OrderStatusSessionBean.orderSatusSession() caught throwable during initial section: "+ t.toString(), t,"");				
			}

			// Check if our thread has been requested to interrupt.  We might be asked to
			// cancel at any time during this transaction.
			if (Thread.currentThread().isInterrupted()) {
				debugOut("OrderStatusSession trasaction was interrupted (ie. asked to cancel)");
				throw new InterruptedException("OrderStatusSession trasaction was interrupted (ie. asked to cancel)");
			}

			// If we made it this far, go search for customer in the DB
			try {
				
				debugOut("Before calling getCustomerInstance(), customerId is: " + customerId);

				// Search for customer by ID
				// 2.6.2.2 bullet 3 - Case 2
				getCustomerInstance(customerId, districtId, warehouseId);

			} catch (Throwable t) {

				System.out.println("OrderStatusSessionBean.orderSatusSession() caught throwable finding Customer in DB: " + t.toString());
				t.printStackTrace();
				throw new UserException("OrderStatusSessionBean.orderSatusSession() caught throwable finding Customer in DB: "+t.toString(), t, "");
			}

			// Set customer data in output structure.
			debugOut("CustomerId: " + customerId + " was found");

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
				
				// Check if our thread has been requested to interrupt.  We might be asked to
				// cancel at any time during this transaction.
				if (Thread.currentThread().isInterrupted()) {
					debugOut("OrderStatusSession trasaction was interrupted (ie. asked to cancel)");
					throw new InterruptedException("OrderStatusSession trasaction was interrupted (ie. asked to cancel)");
				}
				
				
				// If we made it this far, search DB for Order
				try {

					debugOut("Before calling getorder()");

					getOrder(output.getOutCustomerId(), output
							.getOutCustomerWarehouseId(), output
							.getOutCustomerDistrictId(), output);

				} catch (Throwable t) {
					System.out.println("OrderStatusSessionBean.orderSatusSession() caught throwable finding Order in DB: " + t.toString());
					t.printStackTrace();
					throw new UserException("OrderStatusSessionBean.orderSatusSession() caught throwable finding Order in DB: "+t.toString(), t, "");				
				}

				// **********************************************************************************************
				// 2.6.2.2 bullet 5
				// all rows in the orderline table with matching OL_W_ID (equals
				// O_W_ID), OL_D_ID (equals O_D_ID),
				// and OL_O_ID (equals O_ID) are selected and the corresponding set
				// of
				// OL_I_ID, OL_SUPPLY_W_ID,
				// OL_QUANTITY, OL_AMOUNT, and OL_DELIVERY_D are retrieved.
				// ***********************************************************************************************

				// Check if our thread has been requested to interrupt.  We might be asked to
				// cancel at any time during this transaction.
				if (Thread.currentThread().isInterrupted()) {
					debugOut("OrderStatusSession trasaction was interrupted (ie. asked to cancel)");
					throw new InterruptedException("OrderStatusSession trasaction was interrupted (ie. asked to cancel)");
				}
				
				
				// If we made it this far, search DB for Orderlines
				try {			
				
				getOrderlines(output.getOutOrderLineCount(),
						output.getOutOrderId(), output.getOutCustomerWarehouseId(),
						output.getOutCustomerDistrictId(), output);

				debugOut("After calling getorder() and getorderLines() ");				
				debugOut("CustomerId: " + output.getOutCustomerId());
				debugOut("OrderId: " + output.getOutOrderId());
				
				} catch (Throwable t) {

					System.out.println("OrderStatusSessionBean.orderSatusSession() caught throwable finding Orderlines in DB: " + t.toString());
					t.printStackTrace();
					throw new UserException("OrderStatusSessionBean.orderSatusSession() caught throwable finding Orderlines in DB: "+t.toString(), t, "");				
				}

			} else {
				output.setOutErrorMsg("Customer with last name: " + output.getOutCustomerLastName()
						+ " was not found");
				throw new UserException("Customer with last name: " + output.getOutCustomerLastName()
						+ " was not found");
			}

			output.setStcnum(irwwbase.Stcnum.getInstance().getStcnum());

			debugOut("Output from order status transaction is: " + output);

			// If we made it here with no exceptions, we have success.
			returnStatus = Boolean.TRUE;

		// finally block for the entire method	
		} finally {

			debugOut("Exiting OrderStatusSessionBean.orderSatusSession()");
			endOfTran("OrderStatusConcurrentLite");
		}

		return returnStatus;
	}

	public void debugOut(String message) {
		
		if (System.getProperty("DEBUG_OUT") != null) {
			if (System.getProperty("DEBUG_OUT").equals("ON")) {
				long tid = Thread.currentThread().getId();
				System.out.println("concurrentOS_thread_" + tid + ": " + message);
			}
		}
	}

}
