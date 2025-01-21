//***********************************************************
//
// Functionality Tested:
//		EJB 3.x - Aynchronous (fire and return) - see 
//                comment block in deliverySession().
//      EJB 3.x - Injection of Stateless bean with 
//				  only a No Interface View.
//
//***********************************************************

package deliverysession;

import irwwbase.EJBAsyncMethodStatsBean;
import irwwbase.IRWWBase;
import irwwbase.UserException;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RunAs;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;

// MLS - The security role "InvalidRole" is set below to test that this
//       annotation is properly overridden by a good role in ejb-jar.xml.
//       If that does not occur, this role will cause an error.

@Stateless
@Remote(DeliverySessionFacadeRemote.class)
@Local(DeliverySessionFacade.class)
@DeclareRoles({"InvalidRole", "RunAsAdministratorRole"})
@RolesAllowed("InvalidRole")
@RunAs("RunAsAdministratorRole")
public class DeliverySessionFacadeBean extends IRWWBase implements
		DeliverySessionFacadeRemote, DeliverySessionFacade {

	final static long serialVersionUID = 3206093459760846163L;
	
	@EJB
	DeliverySessionNIVBean nivBean;
	

	@EJB
	DeliverySessionNIVBeanRemote nivBeanRemote;
	
	@EJB
	EJBAsyncMethodStatsBean theEJBAsyncMethodStatsBean;

	private java.lang.String _className;

	private int rc = 0;

	private DeliveryOutput output;

	private SingleDelivery[] singleDeliveries = null;

	//@PersistenceContext(unitName = "DeliverySessionEJB")
	//EntityManager em;

	//OrderJPA order = null;

	//OrderlineJPA orderline = null;

	//CustomerJPA customer = null;

	/**
	 * Insert the method's description here. Creation date: (9/15/00 3:40:16 PM)
	 * 
	 * @return deliverySessionPackage.DeliveryOutput
	 * @param input deliverySessionPackage.DeliveryInput
	 * @exception UserException
	 *                Any exception handled by the application is returned as a user exception.
	 */
	public DeliveryOutput deliverySession(DeliveryInput input) throws UserException {

		_className = (DeliverySessionFacadeBean.class).getName();

		// outer try block so we can have a finally block
		try {

			startOfTran("DeliverySessionEJB");

			debugOut(" ERWW - Entering EJB deliverySession method of Delivery Transaction");

			debugOut(" ERWW - leakDeliveryResults(Bad App) = " + input.isLeakDeliveryResults());

			// shupert -  say whether this delivery will be run asynchronously or synchronously
			if (input.isUse31Async()){
				debugOut(" ERWW - delivery will be executed asynchronously");
			} else {
				debugOut(" ERWW - delivery will be executed synchronously");		
			}

			// Set class name of class
			if (System.getProperty("DEBUG_OUT") != null)
				if (System.getProperty("DEBUG_OUT").equals("ON")) {
					startStamp(_className);
				}

			debugOut(" ERWW - Retrieve the Warehouse Id and Carrier Id from the input structure");

			short warehouseId = input.getWarehouseId();
			short carrierId = input.getCarrierId();
			int maxdistrictId = input.getMaxdistrictId();
			boolean useRemoteBeans = input.isDistributedEJBs();

			debugOut("After input.getMaxdistrictId() - maxdistrictId = "
					+ maxdistrictId);

			singleDeliveries = new SingleDelivery[maxdistrictId];

			for (int x = 0; x < maxdistrictId; x++) {
				singleDeliveries[x] = new SingleDelivery();
			}

			debugOut("Beginning delivery for Warehouse " + warehouseId
					+ ", Carrier " + carrierId);
			debugOut(" ERWW - Loop through districts ");
			debugOut("Before BIG LOOP - maxdistrictId = " + maxdistrictId
					+ getCurrentTime());

			DistrictDeliveryStatistics dds = new DistrictDeliveryStatistics();

			if (input.isUse31Async()) {
				//************************************************************************
				// The following code was added to test the EJB 3.x Asynchronous method
				// function.   The deliverOrderforDistrict methods will be run 
				// asynchronously.  
				// The returned Future objects will be stored in
				// and ArrayList.  We will loop through these future objects 
				// and ensure that they have all finished before closing the FileWriter.			
				//*************************************************************************

				Object[] futureArray = new Object [maxdistrictId];
				Future<SingleDeliveryOutput> future = null;

				for (int x = 0; x < maxdistrictId; x++) {
					singleDeliveries[x] = new SingleDelivery();
				}

				for (int dist = 1; dist <= maxdistrictId; dist++) {
					//invoke the async fire-and-return and fire-and-forget methods on the correct exposure (local or remote) of the bean
					if (useRemoteBeans) {
						debugOut(" ERWW - Doing asynchronous method call on remote bean exposure.");


						nivBeanRemote.asyncNotifyDistrictWeAreSendingOrder(dist);									
						future = nivBeanRemote.secureAsyncDeliverOrderforDistrict(dist, input);
					}
					else {
						debugOut(" ERWW - Doing asynchronous method on no-interface bean exposure.");
						nivBean.asyncNotifyDistrictWeAreSendingOrder(dist);
						future = nivBean.secureAsyncDeliverOrderforDistrict(dist, input);
					}
					// shupert
					// Bump up the EJB 3.x async methods fire-and-forget "called" counter (ie. for application self validation)
					theEJBAsyncMethodStatsBean.fireAndForgetMethodCalled();

					// Save future away to retrieve results later
					futureArray[dist - 1] = future;
				}


				// get results from each asynchronous delivery
				for (int i=0; i < maxdistrictId; i++){
					
					try {

						// Skip future.get() calls for Bad App testing scenario?
						if (!input.isLeakDeliveryResults()){

							// No, don't skip future.get() calls - this is the normal path

							// shupert - Default the async methods Future.get() timeout to 30 seconds, but make
							//           a system property that can override it.
							Integer futureGetTimeout = 30;  // default = 30 sec.
							String futureGetTimeoutPropertyString = System.getProperty("ERWW_AsyncMethodsFutureDotGetTimeout");
							if (futureGetTimeoutPropertyString != null) {
								futureGetTimeout = Integer.valueOf(futureGetTimeoutPropertyString);
							}

							SingleDeliveryOutput singleDeliveryOutput = ((Future<SingleDeliveryOutput>)futureArray[i]).get(futureGetTimeout, TimeUnit.SECONDS);
							debugOut(" ERWW - Asynch method delivery skipped = " + singleDeliveryOutput.getDeliverySkipped());

							// aggregate results from each delivery
							singleDeliveries[i] = singleDeliveryOutput.getSingleDelivery();
							if (singleDeliveryOutput.getDeliverySkipped()){
								dds.addSkippedDistrict();
							} else {
								dds.addProcessedDistrict();
							}
							
						} else {

							// yes - do skip future.get() calls.  Leak the async delivery method call results on purpose

							dds.addProcessedDistrict();  // we did process the district, we are just leaking the results

							debugOut(" ERWW - Leaking async delivery result (on purpose) for Bad App testing");
						}

					} catch (ExecutionException e1) {					
						if (e1.getCause()!=null){
							Throwable t = e1.getCause();
							System.out.println("ExecutionException in " + _className + ": calling DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict(), exception: " + t.getMessage());		
							t.printStackTrace();
							throw new UserException("ExecutionException in " + _className + ": calling DeliverySessionNIVBean..secureAsyncDeliverOrderforDistrict(), exception: " + t.getMessage(),
									                "ExecutionException in " + _className + ": calling DeliverySessionNIVBean..secureAsyncDeliverOrderforDistrict(), exception: " + t.getMessage());
						}else{
							System.out.println("ExecutionException in " + _className + ": calling DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict(), exception: " + e1.getMessage());		
							e1.printStackTrace();
							throw new UserException("ExecutionException in " + _className + ": calling DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict(), exception: " + e1.getMessage(),
									                "ExecutionException in " + _className + ": calling DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict(), exception: " + e1.getMessage());
						}	
						
					} catch (CancellationException e2) {
						System.out.println("CancellationException in " + _className + ": calling DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict(), exception: " + e2.getMessage());		
						e2.printStackTrace();
						throw new UserException("CancellationException in " + _className + ": calling DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict(), exception: " + e2.getMessage(),
								                "CancellationException in " + _className + ": calling DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict(), exception: " + e2.getMessage());
						
					} catch (InterruptedException e3) {
						System.out.println("InterruptedException in " + _className + ": calling DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict(), exception: " + e3.getMessage());		
						e3.printStackTrace();
						throw new UserException("InterruptedException in " + _className + ": calling DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict(), exception: " + e3.getMessage(),
								                "InterruptedException in " + _className + ": calling DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict(), exception: " + e3.getMessage());
						
					} catch (TimeoutException e4) {
						System.out.println("TimeoutException in " + _className + ": calling DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict(), exception: " + e4.getMessage());		
						e4.printStackTrace();
						throw new UserException("TimeoutException in " + _className + ": calling DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict(), exception: " + e4.getMessage(),
								                "TimeoutException in " + _className + ": calling DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict(), exception: " + e4.getMessage());
	
					} catch (Throwable e5) {
						System.out.println("Throwable caught in " + _className + ": calling DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict(), exception: " + e5.getMessage());		
						e5.printStackTrace();
						throw new UserException("Throwable caught in " + _className + ": calling DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict(), exception: " + e5.getMessage(),
								                "Throwable caught in " + _className + ": calling DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict(), exception: " + e5.getMessage());
					}
					
				}

			} else {
				//**************************************************************************//
				// This code path will perform the delivery synchronously.                      
				//**************************************************************************//
				try {

					for (int i = 1; i <= maxdistrictId; i++) {

						SingleDeliveryOutput singleDeliveryOutput = null;

						if (useRemoteBeans) {
							debugOut(" ERWW - Doing synchronous method call on remote bean exposure.");

							singleDeliveryOutput = nivBeanRemote.secureDeliverOrderforDistrict(i, input);

						} else {
							debugOut(" ERWW - Doing synchronous method call on no-interface bean exposure.");
							singleDeliveryOutput = nivBean.secureDeliverOrderforDistrict(i, input);	
						}

						// aggregate results from each delivery
						singleDeliveries[i - 1] = singleDeliveryOutput.getSingleDelivery();
						if (singleDeliveryOutput.getDeliverySkipped()){
							dds.addSkippedDistrict();
						} else {
							dds.addProcessedDistrict();
						}
					}

				} catch (UserException ux)	{
					// If we catch UserException then the error is already logged, just rethrow it.
					throw ux;

				} catch (Exception ex) {
					System.out.println("Exception caught calling DeliverySessionNIVBean.secureDeliverOrderforDistrict()");
					ex.printStackTrace();
					throw new UserException("Exception caught calling DeliverySessionNIVBean.secureDeliverOrderforDistrict(), exception is: " + ex.getMessage(),
						                 	"Exception caught calling DeliverySessionNIVBean.secureDeliverOrderforDistrict(), exception is: " + ex.getMessage());
				}			

			}  // end else synchronous delivery

			debugOut("After BIG LOOP " + getCurrentTime());
			debugOut(" ERWW - Log a result message ");

			int districtsSkipped = dds.getDistrictsSkipped();
			if (districtsSkipped >= 1) {
				debugOut(" ERWW - Delivered orders for "
						+ (maxdistrictId - districtsSkipped) + " of "
						+ maxdistrictId + " districts");
			}

			output = new DeliveryOutput(rc, irwwbase.Stcnum.getInstance()
					.getStcnum());
			output.setUpSingleDeliveries(maxdistrictId);
			output.setSingleDeliveries(singleDeliveries);
			output.setDistrictDeliveryStatistics(dds);

			// Indicate in output if we purposely skipped getting the async delivery results for
			// Bad App testing
			if (input.isLeakDeliveryResults()){
				output.setDeliveryResultsLeaked(true);			
			}

			return output; // method deliverySession

		} finally {
			
			debugOut(" ERWW - Returning from EJB deliverySession method of Delivery Transaction");
			
			endOfTran("DeliverySessionEJB");
		}

	}

/*	private void getCustomerInstance(short customerId, short districtId,
			short warehouseId) throws UserException {

		try {

			CustomerKey key = new CustomerKey();
			key.setCustomerDId(districtId);
			key.setCustomerId(customerId);
			key.setCustomerWId(warehouseId);

			//kem.getFetchPlan().setReadLockMode(LockModeType.WRITE);
			customer = em.find(CustomerJPA.class, key);
			//kem.getFetchPlan().setReadLockMode(null);
			if (customer==null)	throw new UserException("customer not found: district="+districtId+", customer="+customerId+" and warehouse="+warehouseId+", "+getSTC(),"");

		} catch (Exception t) {
			t.printStackTrace();
			if (is913(t))
				throw new UserException("<&!@"+checkFor913(t)+", finding Customer JPA, "+ t.getCause()+getSTC(), getStackTrace(t));
			else
				throw new UserException("<&!@"+"failure in getCustomerInstance: "
						+",exception message: "+t.getMessage()+", exception cause: "+ t.getCause()+getSTC(), getStackTrace(t));
		} // end try

	}

*/	

	/**
	 * Insert the method's description here. Creation date: (9/20/00 4:23:25 PM)
	 * 
	 * @return java.lang.String
	 * @exception java.lang.Exception
	 *                The exception description.
	 */
/**	
	private String getTimeStamp() {

		// Get Time Stamp in the form of yyyy-mm-dd-hh.mm.ss.nnnnnn
		// The toString methods returns the following format: dow mon dd
		// hh:mm:ss zzz yyyy

		Timestamp timestamp = new Timestamp(java.lang.System
				.currentTimeMillis());
		String timeStamp = timestamp.toString();

		return timeStamp; // method getTimeStamp

	}
	*/

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
	
/***
	private void log(String logline, java.io.FileWriter resultFile) throws UserException {

		debugOut(" ERWW - get timeStamp ");

		String timeStamp = new String("");
		timeStamp = getTimeStamp();

		try {
			resultFile.write(timeStamp + " - " + logline);
		} catch (Exception ex) {
			System.out
					.println("%%%%% ERROR: resultFile.write failed in log method - "
							+ _className + " " + getSTC());
			ex.printStackTrace();
			throw new UserException(
					"%%%%% ERROR: resultFile.write failed in log method - "
							+ _className + " " + getSTC(), getStackTrace(ex));
		}
		return; // method log

	}
	*/

	
/*
	public java.math.BigDecimal processOrderlineInstance(int orderId,
			short districtId, short warehouseId, int count) throws UserException {

		debugOut("<<< in processOrderlineInstance - orderId: " + orderId
				+ " districtId: " + districtId + " warehouseId: " + warehouseId
				+ " count: " + count);

		boolean forUpdate = true;

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
				orderline = em.find(OrderlineJPA.class, key);
				//kem.getFetchPlan().setReadLockMode(null);
				if (orderline==null)	throw new UserException("orderline not found: order="+orderId+", districtId="+districtId+", warehouse="+warehouseId+" and orderline number="+olNumber+" " +getSTC(),"");

			} catch (Exception ex) {
				System.out
						.println("%%%%% ERROR: orderlineCMPEntityHome.findByPrimaryKey failed in processOrderlineInstance method - "
								+ _className + " " + getSTC());
				ex.printStackTrace();
				if (is913(ex))
					throw new UserException("<&!@"+checkFor913(ex)+", finding Orderline JPA, "+ ex.getCause()+getSTC(), getStackTrace(ex));
				else
					throw new UserException("<&!@"+
							"%%%%% ERROR: orderlineCMPEntityHome.findByPrimaryKey failed in processOrderlineInstance method - "
									+ _className + " " +",exception message: "+ex.getMessage()+", exception cause: "+ ex.getCause()+getSTC(), getStackTrace(ex));
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
*/
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
/*	public QueryNewOrderOutput queryNewOrderInstances(short warehouseId,
			short districtId) throws UserException {

		boolean forUpdate = true;
		String errorCode = "FirstTimeThrough";
		int retry = 0;
		short orderId = 0;
		short lowerOrderId = 0;
		NewOrderJPA newOrder = null;

		QueryNewOrderOutput queryNewOrderOutput = new QueryNewOrderOutput();
		Boolean skipped = new Boolean(false);
		Boolean TRUE = new Boolean(true);
		java.util.Collection newOrderCollection = null;
		java.util.Iterator iterator = null;
		NewOrderKey key = null;
		int lowestOrderIdRow = 0;
		int cnt = 0;

		try {

			OpenJPAQuery q = OpenJPAPersistence.cast(em
					.createNamedQuery("findMinNewOrder"));
			// Query q = em.createNamedQuery("findMinNewOrder");
			q.setParameter("warehouseId", warehouseId);
			q.setParameter("districtId", districtId);

			debugOut("warehouseId: " + warehouseId + " districtId: "
					+ districtId);
			q.getFetchPlan().setReadLockMode(LockModeType.WRITE);
			// newOrder = (NewOrderJPA) q.getSingleResult();
			// q.getFetchPlan().setReadLockMode(null);

			Collection coll = null;
			coll = q.getResultList();
			if (coll != null) {
				Iterator it = coll.iterator();

				while (it.hasNext()) {
					cnt++;
					newOrder = (NewOrderJPA) it.next();
					debugOut("FindMinNewOrder rows::" + cnt + ": orderID-> "
							+ newOrder.getOrderId() + "WarehouseId-> "
							+ newOrder.getWarehouseId() + "districtId-> "
							+ newOrder.getDistrictId());
					// v.addElement(cja);
				}

				q.getFetchPlan().setReadLockMode(null);
				if (cnt == 0)
					debugOut("FindMinOrder returned no rows.");
			}

			if (newOrder == null) {
				lowerOrderId = 0;
				skipped = TRUE;
			} else {
				lowerOrderId = (short) newOrder.getOrderId();
				debugOut("Found     O " + lowerOrderId + " D " + districtId
						+ " W " + warehouseId);
			}

			debugOut("%%%%% After lowest orderId is obtained " + lowerOrderId);
			debugOut("Skipped = " + skipped);

			queryNewOrderOutput.setSkipped(skipped);
			queryNewOrderOutput.setOrderId(lowerOrderId);

		} catch (Exception e) {
			e.printStackTrace();
			if (is913(e))
				throw new UserException("<&!@"+checkFor913(e)+", finding mininum NewOrder JPA, "+ e.getCause()+getSTC(), getStackTrace(e));
			else {
				if (e instanceof NullPointerException)
					System.out.println("Query found no matching rows, districtId="+districtId+", warehouseId="+warehouseId);
			}
			
		}

		return queryNewOrderOutput; // method queryNewOrderInstances
	}
*/
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
/*	public short queryOrderInstances(int orderId, short districtId,
			short warehouseId) throws UserException {

		boolean forUpdate = true;

		OrderKey key = new OrderKey(districtId, orderId, warehouseId);

		debugOut("In queryOrderInstances - wid=" + warehouseId);
		debugOut("In queryOrderInstances - oid=" + orderId);
		debugOut("In queryOrderInstances - did=" + districtId);

		short customerId = 0;
		try {
			// java.util.Enumeration enum = null;
			//kem.getFetchPlan().setReadLockMode(LockModeType.WRITE);
			order = em.find(OrderJPA.class, key);
			//kem.getFetchPlan().setReadLockMode(null);
			if (order==null)	throw new UserException("order not found: warehouseId="+warehouseId+", orderId="+orderId+" and districtId="+districtId+", "+getSTC(),"");
		} catch (Exception t) {
			t.printStackTrace();
			if (is913(t))
				throw new UserException("<&!@"+checkFor913(t)+", finding Order JPA, "+ t.getCause()+getSTC(), getStackTrace(t));
			else
				throw new UserException("<&!@"+"Exception while finding OrderJPA"+",exception message: "+t.getMessage()+", exception cause: "+ t.getCause()+getSTC(),
						getStackTrace(t));
		}
		debugOut("Before orderCMPEntityHome.getCustomerId()");
		customerId = order.getCustomerId();

		return customerId; // method queryOrderInstances

	}
*/
/*	
	private void deliverOrderforDistrict(int dist, DeliveryInput input,
			java.io.FileWriter resultFile, DistrictDeliveryStatistics dds) throws UserException {
		// Laa begin delivery for a district

		QueryNewOrderOutput queryNewOrderOutput = new QueryNewOrderOutput();
		QueryOrderOutput queryOrderOutput = new QueryOrderOutput();
		short warehouseId = input.getWarehouseId();
		short carrierId = input.getCarrierId();
		int maxdistrictId = input.getMaxdistrictId();
		short districtId = (short) dist;
		Boolean skipped;
		int processed = 0;
		int orderId = 0;
		short customerId = 0;
		BigDecimal amount = new BigDecimal(0);

		debugOut(" ERWW - Before Query New Order objects and find the oldest, undelivered Order "
				+ getCurrentTime());

		debugOut("In deliverOrderforDistrict:: warehouseId: " + warehouseId
				+ " districtId: " + districtId);

		queryNewOrderOutput = queryNewOrderInstances(warehouseId, districtId);
		debugOut("<After queryNewOrderInstances " + getCurrentTime());

		skipped = queryNewOrderOutput.getSkipped();
		orderId = queryNewOrderOutput.getOrderId();
		debugOut("skipped-> " + skipped + "orderid-> " + orderId);

		if (skipped.equals(Boolean.FALSE)) {

			try {
				em.remove(newOrder);
			} catch (Exception t) {
				t.printStackTrace();
				if (is913(t))
					throw new UserException("<&!@"+checkFor913(t)+", removing NewOrder JPA, "+ t.getCause()+getSTC(), getStackTrace(t));
				else
					throw new UserException("<&!@"+
							"Error during remove of NewOrder "+",exception message: "+t.getMessage()+", exception cause: "+ t.getCause()+getSTC(), getStackTrace(t));
			}
			debugOut("<After removing NewOrder " + getCurrentTime());

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
			debugOut("<Before customerInstance " + getCurrentTime());

			debugOut(" ERWW - Orderline amount = " + amount + " Customer ID = "
					+ customerId);
			customer.setCustomerBalance(amount.add(customer
					.getCustomerBalance()));
			customer.setCustomerDeliveryCnt(new BigDecimal(customer
					.getCustomerDeliveryCnt().intValue() + 1));
			dds.addProcessedDistrict();

			debugOut("Delivered O " + orderId + " D " + districtId + " W "
					+ warehouseId);

			log("Delivered Order " + orderId + " for District " + districtId
					+ " in Warehouse " + warehouseId + " for Amount " + amount
					+ '\n', resultFile);
			debugOut(" ERWW - Updating Customer Object, updated customer balance & delivery amount");

			debugOut("<Before add log for delivery " + getCurrentTime());
			log("Delivered Order " + orderId + " for District " + districtId
					+ " in Warehouse " + warehouseId + " for Amount " + amount
					+ '\n', resultFile);
			debugOut("<After add log for delivery " + getCurrentTime());

			singleDeliveries[dist - 1].setWarehouseId(warehouseId);
			singleDeliveries[dist - 1].setDistrictId(districtId);
			singleDeliveries[dist - 1].setOrderId(orderId);
			singleDeliveries[dist - 1].setCustomerId(customerId);
			singleDeliveries[dist - 1].setAmount(amount);

		} // end if
		else {
			dds.addSkippedDistrict();

			debugOut("Skipping a delivery");

			debugOut("<Before skip delivery log entry " + getCurrentTime());
			log("Skipping delivery for Warehouse " + warehouseId
					+ ", District " + districtId + '\n', resultFile);
			debugOut("<After skip delivery log entry " + getCurrentTime());
		} // end else

		return;
	}
*/
}
