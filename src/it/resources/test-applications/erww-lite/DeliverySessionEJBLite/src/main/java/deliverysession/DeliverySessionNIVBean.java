//***********************************************************
//
// Functionality Tested:
//		EJB 3.x - Aynchronous (fire and return)
//  	EJB 3.x - No Interface view on a Stateless Bean
//
//***********************************************************
package deliverysession;

import irwwbase.EJBAsyncMethodStatsBean;
import irwwbase.IRWWBase;
import irwwbase.UserException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Future;

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.ejb.Remote;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

import jakarta.persistence.Query;

import neworderjpa.NewOrderJPA;
import orderjpa.OrderJPA;
import orderjpa.OrderKey;
import orderlinejpa.OrderlineJPA;
import orderlinejpa.OrderlineKey;
import customerjpa.CustomerJPA;
import customerjpa.CustomerKey;



@Stateless
@LocalBean
@DeclareRoles("AdministratorRole")
@Remote(DeliverySessionNIVBeanRemote.class)
public class DeliverySessionNIVBean extends IRWWBase {

	public static int deathCount = 0;
	
	final static long serialVersionUID = 3206093459760846163L;

	private java.lang.String _className = "DeliverySessionNIVBean";

	@PersistenceContext(unitName = "DeliverySessionEJB")
	EntityManager em;
	
	@EJB
	EJBAsyncMethodStatsBean theEJBAsyncMethodStatsBean;

	private NewOrderJPA newOrder = null;

	private OrderJPA order = null;

	private OrderlineJPA orderline = null;

	private CustomerJPA customer = null;


	protected void getCustomerInstance(short customerId, short districtId,
			short warehouseId) throws UserException {

		try {

			CustomerKey key = new CustomerKey();
			key.setCustomerDId(districtId);
			key.setCustomerId(customerId);
			key.setCustomerWId(warehouseId);

			//kem.getFetchPlan().setReadLockMode(LockModeType.WRITE);
			customer = em.find(CustomerJPA.class, key, LockModeType.PESSIMISTIC_WRITE);
			//kem.getFetchPlan().setReadLockMode(null);
			if (customer==null)	throw new UserException("customer not found: district="+districtId+", customer="+customerId+" and warehouse="+warehouseId+", "+ getSTC(), 
					                                    "customer not found: district="+districtId+", customer="+customerId+" and warehouse="+warehouseId+", "+ getSTC());

		} catch (Exception t) {
			t.printStackTrace();
			if (is913(t)) {
				throw new UserException("<&!@"+checkFor913(t)+", finding Customer JPA, cause:"+ t.getCause()+ getSTC(),
						"<&!@"+checkFor913(t)+", finding Customer JPA, cause:"+ t.getCause()+ getSTC());
			} else {
				throw new UserException("<&!@"+"failure in getCustomerInstance: "
						+",exception message: "+t.getMessage()+", exception cause: "+ t.getCause()+ getSTC(), 
						"<&!@"+"failure in getCustomerInstance: "
								+",exception message: "+t.getMessage()+", exception cause: "+ t.getCause()+ getSTC());
			}
		} // end try

	}

	/**
	 * Insert the method's description here. Creation date: (9/20/00 4:23:25 PM)
	 * 
	 * @return java.lang.String
	 * @exception java.lang.Exception
	 *                The exception description.
	 */
	protected String getTimeStamp() {

		// Get Time Stamp in the form of yyyy-mm-dd-hh.mm.ss.nnnnnn
		// The toString methods returns the following format: dow mon dd
		// hh:mm:ss zzz yyyy

		Timestamp timestamp = new Timestamp(java.lang.System
				.currentTimeMillis());
		String timeStamp = timestamp.toString();

		return timeStamp; // method getTimeStamp

	}

	/**
	 * Insert the method's description here. Creation date: (9/20/00 2:53:41 PM)
	 * 
	 * @param logline
	 *            java.lang.String
	 * @param resultFile
	 *            java.io.FileWriter
	 * @throws UserException 
	 * @exception java.io.FileNotFoundException
	 *                The exception description.
	 */
	private void log(String logline, java.io.FileWriter resultFile) throws UserException {

		String timeStamp = new String("");
		timeStamp = getTimeStamp();

		try {
			resultFile.write(timeStamp + " - " + logline);
		} catch (Exception ex) {
			System.out
			.println("%%%%% ERROR: resultFile.write failed in log method - "
					+ _className + " " + getSTC());
			ex.printStackTrace();
			throw new UserException("%%%%% ERROR: resultFile.write failed in log method - "
					+ _className + " " + getSTC(),
					"%%%%% ERROR: resultFile.write failed in log method - "
							+ _className + " " + getSTC());
		}
		return; // method log

	}


	/**
	 * Insert the method's description here. Creation date: (9/20/00 4:23:25 PM)
	 * 
	 * @return java.lang.String
	 * @exception java.lang.Exception
	 *                The exception description.
	 */

	private java.math.BigDecimal processOrderlineInstance(int orderId,
			short districtId, short warehouseId, int count) throws UserException {

		debugOut("<<< in processOrderlineInstance - orderId: " + orderId
				+ " districtId: " + districtId + " warehouseId: " + warehouseId
				+ " count: " + count);

		Timestamp timeStamp = new Timestamp(java.lang.System
				.currentTimeMillis());
		BigDecimal amount = new BigDecimal(0);
		short olNumber = 0;
		OrderlineKey key = null;

		for (short i = 1; i <= count; i++) {

			olNumber = i;
			try {
				key = new OrderlineKey(orderId, districtId, warehouseId,
						olNumber);
				//kem.getFetchPlan().setReadLockMode(LockModeType.WRITE);
				orderline = em.find(OrderlineJPA.class, key, LockModeType.PESSIMISTIC_WRITE);
				//kem.getFetchPlan().setReadLockMode(null);
				if (orderline==null) {
					throw new UserException("orderline not found: order="+orderId+", districtId="+districtId+", warehouse="+warehouseId+" and orderline number="+olNumber+" " +getSTC(),
							"orderline not found: order="+orderId+", districtId="+districtId+", warehouse="+warehouseId+" and orderline number="+olNumber+" " +getSTC());
				}

			} catch (Exception ex) {
				System.out.println("em.find failed in processOrderlineInstance method, exception is: " + ex.getMessage());
				ex.printStackTrace();
				if (is913(ex))
					throw new UserException(checkFor913(ex)+", em.find failed in processOrderlineInstance method, exception is: "+ ex.getMessage(),
							                checkFor913(ex)+", em.find failed in processOrderlineInstance method, exception is: "+ ex.getMessage());
				else
					throw new UserException("em.find failed in processOrderlineInstance method, exception is: " +ex.getMessage(),
							                "em.find failed in processOrderlineInstance method, exception is: " +ex.getMessage());
			}
			if (orderline != null) {
				amount = orderline.getOlAmount().add(amount);
				debugOut("Orderline Delivery Date for olNumber = " + olNumber
						+ " is set to " + timeStamp);
				orderline.setOlDeliveryDate(timeStamp);
			} // if
			else {
				debugOut("Failure with Orderline number " + i + "!!!");
			} // else
		} // for

		return amount; // method processOrderlineInstances
	}

	/**
	 * Insert the method's description here. Creation date: (9/20/00 4:41:24 PM)
	 * 
	 * @return java.lang.Boolean
	 * @param warehouseId
	 *            java.lang.String
	 * @param districtId
	 *            java.lang.String
	 * @throws UserException 
	 * @exception java.lang.Throwable
	 *                The exception description.
	 * @exception java.lang.Exception
	 *                The exception description.
	 */
	private QueryNewOrderOutput queryNewOrderInstances(short warehouseId,
			short districtId) throws UserException {

		int lowerOrderId = 0;
		
		// shupert - fix from Dave Cohen this variable should be at class scope.  Comment it out here.
	    //NewOrderJPA newOrder = null;

		QueryNewOrderOutput queryNewOrderOutput = new QueryNewOrderOutput();
		Boolean skipped = Boolean.valueOf(true);
		Boolean TRUE = Boolean.valueOf(true);
		Boolean FALSE = Boolean.valueOf(false);
		newOrder=null;
		int cnt = 0;

		try {
			Query q = em.createNamedQuery("findMinNewOrder");
			q.setParameter("warehouseId", warehouseId);
			q.setParameter("districtId", districtId);

			debugOut("warehouseId: " + warehouseId + " districtId: "
					+ districtId);
			
			q.setLockMode(LockModeType.PESSIMISTIC_WRITE);
			
			//newOrder = (NewOrderJPA) q.getSingleResult();
			
			Collection<?> coll = null;
			coll = q.getResultList();
			if (coll != null) {
				Iterator<?> it = coll.iterator();

				while (it.hasNext()) {
					cnt++;
					newOrder = (NewOrderJPA) it.next();
					debugOut("FindMinNewOrder rows::" + cnt + ": OrderID =  "
							+ newOrder.getOrderId() + " WarehouseId = "
							+ newOrder.getWarehouseId() + " DistrictId = "
							+ newOrder.getDistrictId());
					// v.addElement(cja);
				}

				//q.getFetchPlan().setReadLockMode(null);
				if (cnt == 0)
					debugOut("FindMinOrder returned no rows.");
			}
			
			if (newOrder == null) {
				lowerOrderId = 0;
				skipped = TRUE;
				debugOut("<<< No NewOrders found");
			} else {
				lowerOrderId = newOrder.getOrderId();
				skipped = FALSE;
				debugOut("<<< Minimum NewOrder: OrderID = " + newOrder.getOrderId() + 
						" WarehouseId = " + newOrder.getWarehouseId() + 
						" DistrictId =  " + newOrder.getDistrictId() + 
						" : ThreadId = " + Thread.currentThread().getId());
			}				
			debugOut("Skipped = " + skipped);

		} catch (Exception e) {
			System.out.println("Problem finding mininum NewOrder JPA, exception is: " + e.getMessage());
			e.printStackTrace();
			if (is913(e)) {
				throw new UserException("Problem: " + checkFor913(e) + ", finding mininum NewOrder JPA, exception is: " + e.getMessage(),
						                "Problem: " + checkFor913(e) + ", finding mininum NewOrder JPA, exception is: " + e.getMessage());

			} else if (e instanceof NullPointerException) {
				System.out.println("Query found no matching rows, districtId="+districtId+", warehouseId="+warehouseId);
				
			} else {
				throw new UserException("Problem finding mininum NewOrder JPA, exception is: " + e.getMessage(),
						                "Problem finding mininum NewOrder JPA, exception is: " + e.getMessage());		
			}	

	} finally { //andymc
		queryNewOrderOutput.setSkipped(skipped);
		queryNewOrderOutput.setOrderId(lowerOrderId);
	}

		return queryNewOrderOutput; // method queryNewOrderInstances
	}

	/**
	 * Insert the method's description here. Creation date: (9/21/00 4:01:54 PM)
	 * 
	 * @return orderEntityPackage.OrderEntity
	 * @param orderEntityHome
	 *            orderEntityPackage.OrderEntityHome
	 * @param orderId
	 *            java.lang.String
	 * @param districtId
	 *            java.lang.String
	 * @param warehouseId
	 *            java.lang.String
	 * @throws UserException 
	 * @exception java.lang.Throwable
	 *                The exception description.
	 * @exception java.rmi.RemoteException
	 *                The exception description.
	 * @exception jakarta.ejb.FinderException
	 *                The exception description.
	 */
	private short queryOrderInstances(int orderId, short districtId,
			short warehouseId) throws UserException {

		OrderKey key = new OrderKey(districtId, orderId, warehouseId);

		debugOut("In queryOrderInstances - wid=" + warehouseId);
		debugOut("In queryOrderInstances - oid=" + orderId);
		debugOut("In queryOrderInstances - did=" + districtId);

		short customerId = 0;
		try {
			// java.util.Enumeration enum = null;
			//kem.getFetchPlan().setReadLockMode(LockModeType.WRITE);
			order = em.find(OrderJPA.class, key, LockModeType.PESSIMISTIC_WRITE);
			//kem.getFetchPlan().setReadLockMode(null);
			
			debugOut("In " + _className + ": OrderJPA: queryOrderInstances: After em.find(OrderJPA.class, key): " +
					" WarehouseId = " + warehouseId + 
					" DistrictId = " + districtId +
					" OrderId = " + orderId + 
					" : threadId = " + Thread.currentThread().getId());
			
			if (order==null) {
				throw new UserException("No Order found in queryOrderInstances method: warehouseId="+warehouseId+", orderId="+orderId+" and districtId="+districtId+", " +getSTC(),
						                "No Order found in queryOrderInstances method: warehouseId="+warehouseId+", orderId="+orderId+" and districtId="+districtId+", " +getSTC());
			}
			
		} catch (Exception ex) {
	
			System.out.println("Problem finding Order in queryOrderInstances method, exception is: " + ex.getMessage());
			ex.printStackTrace();
			if (is913(ex))
				throw new UserException(checkFor913(ex)+", problem finding Order in queryOrderInstances method, exception is: " + ex.getMessage(),
						                checkFor913(ex)+", problem finding Order in queryOrderInstances method, exception is: " + ex.getMessage());
			else
				throw new UserException("Problem finding Order in queryOrderInstances method, exception is: " +ex.getMessage(),
						                "Problem finding Order in queryOrderInstances method, exception is: " +ex.getMessage());			
		}
		
		debugOut("Before orderJPA.getCustomerId()");
		customerId = order.getCustomerId();

		return customerId; // method queryOrderInstances

	}
	
	@Asynchronous
	public void asyncNotifyDistrictWeAreSendingOrder(int dist) {
		debugOut(" ERWW - Notifying district **" + dist + "** that we are processing their order.");
		
		// shupert
		// Bump up the EJB 3.x asynch methods fire and forget "executed" counter (ie. for application self validation)
		theEJBAsyncMethodStatsBean.fireAndForgetMethodExecuted();
	}
     
	@Asynchronous
	@RolesAllowed("AdministratorRole")
	public Future<SingleDeliveryOutput> secureAsyncDeliverOrderforDistrict (int dist, 
			DeliveryInput input) throws UserException {

		
		short warehouseId = 0;
		short carrierId = 0;
		short districtId = 0;
		short customerId = 0;
		int orderId = 0;
		Boolean skipped = Boolean.FALSE;
		BigDecimal amount = new BigDecimal(0);
		QueryNewOrderOutput queryNewOrderOutput = null;
		
		// Setup log file writer to "append" to existing log file
		java.io.FileWriter resultFile = null;

		SingleDeliveryOutput singleDeliveryOutput = null;
		
		// Outer try block is here to guarantee resources are closed
		try {

			// Begin delivery for a district
			debugOut(" ERWW - Entering secureAsyncDeliverOrderforDistrict method of DeliverySessionNIVBean.  Thread ID = " + Thread.currentThread().getId());		

			try {
				resultFile = new java.io.FileWriter("results.out", true);
			} catch (Exception ex) {
				System.out.println("Problem creating results file in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage());
				ex.printStackTrace();
				throw new UserException("Problem creating results file in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage(),
						"Problem creating results file in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage());
			}

			// begin the setup input section
			try {

				queryNewOrderOutput = new QueryNewOrderOutput();
				warehouseId = input.getWarehouseId();
				carrierId = input.getCarrierId();
				districtId = (short) dist;
				skipped = Boolean.valueOf(true);
				order = null;
				orderline = null;
				customer = null;
				newOrder = null;

				singleDeliveryOutput = new SingleDeliveryOutput();

				debugOut("In secureAsyncDeliverOrderforDistrict:: warehouseId: " + warehouseId
						+ " districtId: " + districtId);

			} catch (Exception ex) {
				// If the setup section failed, throw user exception and don't continue on.	
				System.out.println("Problem setting up input in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage());
				ex.printStackTrace();
				throw new UserException("Problem setting up input in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage(),
						"Problem setting up input in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage());
			}
			
			
			// Now query new orders and find delivery that needs to be done.
			try {
				debugOut("<Before Query New Order objects and find the oldest, undelivered Order " 	+ getCurrentTime());
				queryNewOrderOutput = queryNewOrderInstances(warehouseId, districtId);
				debugOut("<After queryNewOrderInstances " + getCurrentTime());

				skipped = queryNewOrderOutput.getSkipped();
				orderId = queryNewOrderOutput.getOrderId();
				debugOut("skipped-> " + skipped + "orderid-> " + orderId);

			} catch (UserException ux) {
				// If we catch user exception the error has already been logged, just rethrow it.
				throw ux;
				
			} catch (Exception ex) {
				// If query new orders failed, throw user exception and don't continue on.	
				System.out.println("Problem with query of new orders in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage());
				ex.printStackTrace();
				throw new UserException("Problem with query of new orders in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage(),
						"Problem with query of new orders in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage());
			}
			
					
			
			if (skipped.equals(Boolean.FALSE)) {

				try {
					debugOut("<Before removing NewOrder " + getCurrentTime());
					em.remove(newOrder);
				} catch (Exception ex) {
					System.out.println("Problem removing NewOrder JPA in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage());
					ex.printStackTrace();
					if (is913(ex))
						throw new UserException("Problem: " + checkFor913(ex)+ ", removing NewOrder JPA in secureAsyncDeliverOrderforDistrict method, exception is: "+ ex.getMessage(),
							                	"Problem: " + checkFor913(ex)+ ", removing NewOrder JPA in secureAsyncDeliverOrderforDistrict method, exception is: "+ ex.getMessage());
					else
						throw new UserException("Problem during remove of NewOrder JPA in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage(), 
								                "Problem during remove of NewOrder JPA in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage());
				}
				
				debugOut("<After removing NewOrder " + getCurrentTime());

				// Now finish the delivery steps
				try {

					debugOut(" ERWW - Select the appropriate Order object and update the Carrier ID = "
							+ carrierId);

					debugOut("<Before queryOrderInstances " + getCurrentTime());
					customerId = queryOrderInstances(orderId, districtId, warehouseId);
					debugOut("<After queryOrderInstances" + getCurrentTime());

					order.setCarrierId(carrierId);

					debugOut(" ERWW - Query the Orderline objects, update the delivery data, retrieve orderline amount");
					java.math.BigDecimal count = null;
					count = order.getOrderLineCount();
					debugOut("<Before processOrderlineInstance " + getCurrentTime());
					amount = processOrderlineInstance(orderId, districtId, warehouseId,
							count.intValue());
					debugOut("<After processOrderlineInstance " + getCurrentTime());

					debugOut("<Before customerInstance " + getCurrentTime());
					getCustomerInstance(customerId, districtId, warehouseId);
					debugOut("<After customerInstance " + getCurrentTime());

					debugOut(" ERWW - Orderline amount = " + amount + " Customer ID = "
							+ customerId);

					debugOut(" ERWW - Updating Customer Object, updated customer balance & delivery amount");

					//customer.setCustomerBalance(amount.add(customer
					//		.getCustomerBalance()));

					// shupert - Fix from Dave Cohen for above line.  Avoid overflow of customer balance field.
					BigDecimal amt=amount.add(customer.getCustomerBalance());	

					if (amt.doubleValue()>9999999999.99) {
						amt=new BigDecimal(9999999999.99).setScale(2,RoundingMode.HALF_DOWN);					
					}	
					customer.setCustomerBalance(amt);

					// 	customer.setCustomerDeliveryCnt(new BigDecimal(customer
					// .getCustomerDeliveryCnt().intValue() + 1));

					// shupert - Fix from Dave Cohen for above line .  Avoid overflow of customer delivery count field.
					int custDelCount = customer.getCustomerDeliveryCnt().intValue() + 1;
					if (custDelCount>=10000) {
						custDelCount=9999;
					}
					customer.setCustomerDeliveryCnt(new BigDecimal(custDelCount));	

					debugOut("Delivered O " + orderId + " D " + districtId + " W "
							+ warehouseId);

					log("Delivered async Order " + orderId + " for District " + districtId
							+ " in Warehouse " + warehouseId + " for Amount " + amount
							+ '\n', resultFile);

					// write output object - delivery was processed
					singleDeliveryOutput.setDeliverySkipped(false);
					SingleDelivery singleDelivery = new SingleDelivery();
					singleDelivery.setWarehouseId(warehouseId);
					singleDelivery.setDistrictId(districtId);
					singleDelivery.setOrderId(orderId);
					singleDelivery.setCustomerId(customerId);
					singleDelivery.setAmount(amount);
					singleDeliveryOutput.setSingleDelivery(singleDelivery);

				} catch (UserException ux) {
					// if we catch user exception then the problem is already looged, just rethrow it.
					throw ux;
					
				} catch (Exception ex) {
					System.out.println("Problem finishing delivery in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage());
					ex.printStackTrace();
					if (is913(ex))
						throw new UserException("Problem: " + checkFor913(ex)+ ", finishing delivery in secureAsyncDeliverOrderforDistrict method, exception is: "+ ex.getMessage(),
								"Problem: " + checkFor913(ex)+ ", finishing delivery in secureAsyncDeliverOrderforDistrict method, exception is: "+ ex.getMessage());
					else
						throw new UserException("Problem finishing delivery in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage(), 
								"Problem finishing devliery in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage());
				}

			} // end if !skipped

			else {

				// write output object - delivery was skipped
				singleDeliveryOutput.setDeliverySkipped(true);
				SingleDelivery singleDelivery = new SingleDelivery();
				singleDelivery.setWarehouseId(warehouseId);
				singleDelivery.setDistrictId(districtId);
				singleDeliveryOutput.setSingleDelivery(singleDelivery);

				debugOut("Skipping a delivery");

				debugOut("<Before skip delivery log entry " + getCurrentTime());
				log("Skipping async delivery for Warehouse " + warehouseId
						+ ", District " + districtId + '\n', resultFile);
				debugOut("<After skip delivery log entry " + getCurrentTime());

			} // end else

			
		} catch (UserException ex) {
			
			// If we catch user exception in the outer try block, then the exception
			// data was already logged so just rethrow it back to the caller here.
			// Ultimately, we want it to show up in the client's browser.
			throw ex;
		
		} catch (Exception ex) {
			// Other exceptions are unexpected in outer try block, so rethrow as a User Exception	
			System.out.println("Unexpected exception in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage());
			ex.printStackTrace();
			throw new UserException("Unexpected exception in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage(),
					"Unexpected exception in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage());	
						
		} finally {
            
			// close resoures used in this method
			
			if (resultFile != null) {
				try {
					// close log file for now.
					resultFile.close();
				} catch (Exception ex) {
					
					// Not much we can do in finally, log a message and print stack trace.
					System.out.println("Caught exception in finally block, resultFile.close() failed in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage());
					ex.printStackTrace();
				}
			}
		}		
		return new AsyncResult<SingleDeliveryOutput>(singleDeliveryOutput);
	}	
	
	@RolesAllowed("AdministratorRole")
	public SingleDeliveryOutput secureDeliverOrderforDistrict(int dist, 
			DeliveryInput input)
					throws UserException {

		
		// Setup log file writer to "append" to existing log file
		java.io.FileWriter resultFile = null;	

		SingleDeliveryOutput singleDeliveryOutput = null;

		try {

			// Begin delivery for a district
			debugOut(" ERWW - Entering deliverOrderforDistrict method of DeliverySessionNIVBean.");

			QueryNewOrderOutput queryNewOrderOutput = new QueryNewOrderOutput();
			short warehouseId = input.getWarehouseId();
			short carrierId = input.getCarrierId();
			short districtId = (short) dist;
			Boolean skipped = Boolean.valueOf(true);
			int orderId = 0;
			short customerId = 0;
			BigDecimal amount = new BigDecimal(0);
			order = null;
			orderline = null;
			customer = null;
			newOrder = null;

			// Prepare output object
			singleDeliveryOutput = new SingleDeliveryOutput();

			try {
				resultFile = new java.io.FileWriter("results.out", true);
				
			} catch (Exception ex) {
				System.out.println("Problem creating results file in secureDeliverOrderforDistrict method, exception is: " + ex.getMessage());
				ex.printStackTrace();
				throw new UserException("Problem creating results file in secureDeliverOrderforDistrict method, exception is: " + ex.getMessage(),
						                "Problem creating results file in secureDeliverOrderforDistrict method, exception is: " + ex.getMessage());
			}	
	

			debugOut("In deliverOrderforDistrict:: warehouseId: " + warehouseId
					+ " districtId: " + districtId);

			try {

				debugOut("<Before Query New Order objects and find the oldest, undelivered Order "
						+ getCurrentTime());
				queryNewOrderOutput = queryNewOrderInstances(warehouseId, districtId);
				debugOut("<After queryNewOrderInstances " + getCurrentTime());

				skipped = queryNewOrderOutput.getSkipped();
				orderId = queryNewOrderOutput.getOrderId();
				debugOut("skipped-> " + skipped + "orderid-> " + orderId);


			} catch (UserException ux) {
				// If we catch user exception the error has already been logged, just rethrow it.
				throw ux;

			} catch (Exception ex) {
				// If query new orders failed, throw user exception and don't continue on.	
				System.out.println("Problem with query of new orders in secureDeliverOrderforDistrict method, exception is: " + ex.getMessage());
				ex.printStackTrace();
				throw new UserException("Problem with query of new orders in secureDeliverOrderforDistrict method, exception is: " + ex.getMessage(),
						"Problem with query of new orders in secureDeliverOrderforDistrict method, exception is: " + ex.getMessage());
			}


			if (skipped.equals(Boolean.FALSE)) {

				try {
					debugOut("<Before removing NewOrder " + getCurrentTime());
					em.remove(newOrder);
					
				} catch (Exception ex) {
					System.out.println("Problem removing NewOrder JPA in secureAsyncDeliverOrderforDistrict method, exception is: " + ex.getMessage());
					ex.printStackTrace();
					if (is913(ex))
						throw new UserException("Problem: " + checkFor913(ex)+ ", removing NewOrder JPA in secureDeliverOrderforDistrict method, exception is: "+ ex.getMessage(),
							                	"Problem: " + checkFor913(ex)+ ", removing NewOrder JPA in secureDeliverOrderforDistrict method, exception is: "+ ex.getMessage());
					else
						throw new UserException("Problem during remove of NewOrder JPA in secureDeliverOrderforDistrict method, exception is: " + ex.getMessage(), 
								                "Problem during remove of NewOrder JPA in secureDeliverOrderforDistrict method, exception is: " + ex.getMessage());
				}			
		
				debugOut("<After removing NewOrder " + getCurrentTime());

				try {

					debugOut(" ERWW - Select the appropriate Order object and update the Carrier ID = "
							+ carrierId);

					debugOut("<Before queryOrderInstances " + getCurrentTime());
					customerId = queryOrderInstances(orderId, districtId, warehouseId);
					debugOut("<After queryOrderInstances" + getCurrentTime());

					order.setCarrierId(carrierId);

					debugOut(" ERWW - Query the Orderline objects, update the delivery data, retrieve orderline amount");
					java.math.BigDecimal count = null;
					count = order.getOrderLineCount();
					debugOut("<Before processOrderlineInstance " + getCurrentTime());
					amount = processOrderlineInstance(orderId, districtId, warehouseId,
							count.intValue());
					debugOut("<After processOrderlineInstance " + getCurrentTime());

					debugOut("<Before customerInstance " + getCurrentTime());
					getCustomerInstance(customerId, districtId, warehouseId);
					debugOut("<After customerInstance " + getCurrentTime());

					debugOut(" ERWW - Orderline amount = " + amount + " Customer ID = "
							+ customerId);

					debugOut(" ERWW - Updating Customer Object, updated customer balance & delivery amount");

					//customer.setCustomerBalance(amount.add(customer
					//		.getCustomerBalance()));

					// shupert - Fix from Dave Cohen for above line.  Avoid overflow of customer balance field.
					BigDecimal amt=amount.add(customer.getCustomerBalance());			
					if (amt.doubleValue()>9999999999.99) {
						amt=new BigDecimal(9999999999.99).setScale(2,RoundingMode.HALF_DOWN);			
					}
					customer.setCustomerBalance(amt);

					// customer.setCustomerDeliveryCnt(new BigDecimal(customer
					//		.getCustomerDeliveryCnt().intValue() + 1));

					// shupert - Fix from Dave Cohen for above line.  Avoid overflow of customer delivery count field.
					int custDelCount = customer.getCustomerDeliveryCnt().intValue() + 1;
					if (custDelCount>=10000) {
						custDelCount=9999;
					}
					customer.setCustomerDeliveryCnt(new BigDecimal(custDelCount));	

					debugOut("Delivered O " + orderId + " D " + districtId + " W "
							+ warehouseId);

					log("Delivered Order " + orderId + " for District " + districtId
							+ " in Warehouse " + warehouseId + " for Amount " + amount
							+ '\n', resultFile);

					// write output object - delivery was processed
					singleDeliveryOutput.setDeliverySkipped(false);
					SingleDelivery singleDelivery = new SingleDelivery();
					singleDelivery.setWarehouseId(warehouseId);
					singleDelivery.setDistrictId(districtId);
					singleDelivery.setOrderId(orderId);
					singleDelivery.setCustomerId(customerId);
					singleDelivery.setAmount(amount);
					singleDeliveryOutput.setSingleDelivery(singleDelivery);

				} catch (UserException ux) {
					// if we catch user exception then the problem is already looged, just rethrow it.
					throw ux;

				} catch (Exception ex) {
					System.out.println("Problem finishing delivery in secureDeliverOrderforDistrict method, exception is: " + ex.getMessage());
					ex.printStackTrace();
					if (is913(ex))
						throw new UserException("Problem: " + checkFor913(ex)+ ", finishing delivery in secureDeliverOrderforDistrict method, exception is: "+ ex.getMessage(),
								"Problem: " + checkFor913(ex)+ ", finishing delivery in secureDeliverOrderforDistrict method, exception is: "+ ex.getMessage());
					else
						throw new UserException("Problem finishing delivery in secureDeliverOrderforDistrict method, exception is: " + ex.getMessage(), 
								"Problem finishing devliery in secureDeliverOrderforDistrict method, exception is: " + ex.getMessage());
				}

			} // end if ! skipped
			else {

				debugOut("Skipping a delivery");

				debugOut("<Before skip delivery log entry " + getCurrentTime());
				log("Skipping delivery for Warehouse " + warehouseId
						+ ", District " + districtId + '\n', resultFile);
				debugOut("<After skip delivery log entry " + getCurrentTime());		
				// write output object - delivery was skipped
				singleDeliveryOutput.setDeliverySkipped(true);
				SingleDelivery singleDelivery = new SingleDelivery();
				singleDelivery.setWarehouseId(warehouseId);
				singleDelivery.setDistrictId(districtId);
				singleDeliveryOutput.setSingleDelivery(singleDelivery);	
			} // end else

		} finally {

			if (resultFile != null) {
				try {
					// close log file for now.
					resultFile.close();
				} catch (Exception ex) {
					System.out.println("ERROR: resultFile.close() failed in deliverOrderforDistrict method - "
							+ _className + " " + getSTC());
					ex.printStackTrace();
				}
			}

		}

		return (singleDeliveryOutput);
	}

	

}
