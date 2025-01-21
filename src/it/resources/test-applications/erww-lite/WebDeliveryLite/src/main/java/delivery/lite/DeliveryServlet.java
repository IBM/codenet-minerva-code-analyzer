package delivery.lite;

import irwwbase.*;

import java.io.IOException;
//Comment out unused import
//import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;

import jakarta.ejb.EJB;
import jakarta.annotation.Resource;
import javax.naming.InitialContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Query;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.UserTransaction;


//import com.ibm.websphere.security.WSSecurityHelper;  //Not supported by Liberty yet



import orderjpa.OrderJPA;
import orderjpa.OrderKey;
import orderlinejpa.OrderlineJPA;
import orderlinejpa.OrderlineKey;
import neworderjpa.NewOrderJPA;

import com.ibm.db2.jcc.DB2Diagnosable;
import com.ibm.db2.jcc.DB2Sqlca;

import customerjpa.CustomerJPA;
import customerjpa.CustomerKey;
import deliverysession.DeliveryInput;
import deliverysession.DeliveryOutput;
import deliverysession.DeliverySessionFacade;
import deliverysession.DistrictDeliveryStatistics;
import deliverysession.SingleDelivery;
import deliverysession.SingleDeliveryOutput;
import irwwbase.UserException;


/**
 * Servlet implementation class DeliveryServlet
 */
@EJB(beanName = "DeliverySessionFacadeBean", name = "ejb/DeliverySessionFacadeBean", beanInterface = DeliverySessionFacade.class )
public class DeliveryServlet extends HttpServlet {
		
	//@EJB
	//public DeliverySessionFacade lejb;
	
	private static final long serialVersionUID = 1L;
    
	private java.lang.String _className = (DeliveryServlet.class).getName();
	//Comment out unused variable
	//private java.lang.String idStamp;
	
	IRWWBase ib = new IRWWBase(); 
	
	
	@Resource
	UserTransaction tx;  // Injected UserTransaction
	      
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeliveryServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      
		ib.debugOut(" ERWW - Entering doPost method of DeliveryServlet");

		String inputType = "AutoGeneration";
		short warehouseId = 0;
		short carrierId = 0;
		int maxdistrictId =  0;

		java.io.FileWriter resultFile = null;
		DistrictDeliveryStatistics dds = null;

		DeliveryInput deliveryInput = null;
		DeliveryOutput ejbDeliveryOutput = null;
		DeliveryOutput output = null;
		SingleDelivery[] singleDeliveries = null;

		byte db = ib.dbSize();

		InitialContext ctx = null;
		EntityManager em = null;
		DeliverySessionFacade localDeliverySessionEJB = null;

		ib.startOfTran("DeliveryServlet");

		if (checkUserInRoleWhenAppSecurityEnabled(request, response, "WebDeliveryLite", "CompanyRole")){

			// Now that security checks are done, we need to decide if the business logic will be
			// executed right here in the servlet, or if it will be done in an EJB instead.		
			String businessLogic = null;
			if(request.getParameter("invocationModeRadios") != null) {
				if(request.getParameter("invocationModeRadios").equals("radio1")) {
					//Synchronous Servlet, business logic in servlet
					businessLogic = "synchronous_servlet";
				}         
				else if(request.getParameter("invocationModeRadios").equals("radio2")) {
					//Synchronous Servlet, calls synchronous EJB, business logic in EJB
					businessLogic = "synchronous_EJB";
				}
				else {
					// Unexpected error - bad value in invocationModeRadios button
					System.out.println("%%%%% ERROR: Unexpected value found in invocationModeRadios button:  " + request.getParameter("invocationModeRadios") );
					throw new ServletException("%%%%% ERROR:  Unexpected value found in invocationModeRadios button:  " + request.getParameter("invocationModeRadios") );	                	
				}
			} else {
				// Unexpected error - radio button input is completely missing in the servlet request
				System.out.println("%%%%% ERROR:  Radio button input for invocationModeRadios is completely missing in the servlet request" );
				throw new ServletException("%%%%% ERROR:  Radio button input for invocationModeRadios is completely missing in the servlet request" );	            	
			}

			ib.debugOut(" ERWW - InvocationMode input is: " + businessLogic);
		
			boolean useAsync = Boolean.valueOf(request.getParameter("useAsync"));

			warehouseId = Short.valueOf(request.getParameter("warehouseId"));		
			inputType = request.getParameter("inputType");

			if (inputType.contains("Manual")){
				warehouseId = Short.valueOf(request.getParameter("warehouseId"));
				carrierId = Short.valueOf(request.getParameter("carrierId"));
			} else {
				ExtendedRandom rand = new ExtendedRandom();
				warehouseId = ((short) rand.nextInt(1, MaxValues.warehouseId(db)));
				carrierId = ((short) rand.nextInt(1, MaxValues.carrierId(db)));
			}

			if (useAsync){
				ib.debugOut(" ERWW - delivery will be executed asynchronously");
			} else {
				ib.debugOut(" ERWW - delivery will be executed synchronously");		
			}

			maxdistrictId = MaxValues.districtId(db);

			ib.debugOut("After MaxValues.districtId(db) - maxdistrictId = " + maxdistrictId);

			singleDeliveries = new SingleDelivery[maxdistrictId];

			for (int x = 0; x < maxdistrictId; x++) {
				singleDeliveries[x] = new SingleDelivery();
			}

			ib.debugOut(" ERWW - Initialize the result file");
			try {
				resultFile = new java.io.FileWriter("results.out");
			} catch (Exception ex) {
				System.out.println("%%%%% ERROR: resultFile = new java.io.FileWriter(results.out) failed in doPost() - " + _className + " " + ib.getSTC());
				ex.printStackTrace();
				throw new ServletException("%%%%% ERROR: resultFile = new java.io.FileWriter(results.out) failed in doPost()- "+ib.getSTC() + " " +	ex.getMessage());
			}

			ib.debugOut(" ERWW - Write the initial log message ");
			ib.debugOut("Beginning delivery for Warehouse " + warehouseId + ", Carrier " + carrierId);

			this.log("Beginning delivery for Warehouse " + warehouseId + ", Carrier " + carrierId + '\n', resultFile);

			// temporarily close the log file so sub-method calls can append to it
			try {
				resultFile.close();
			} catch (Exception ex) {
				System.out.println("%%%%% ERROR: resultFile.close() failed in doPost() - " + _className + " " + ib.getSTC());
				ex.printStackTrace();
				throw new ServletException("%%%%% ERROR: resultFile.close() failed in doPost() - " + _className + " " + ib.getSTC()+ " " + ex.getMessage());
			}

			ib.debugOut(" ERWW - Loop through districts ");
			ib.debugOut("Before BIG LOOP - maxdistrictId = " + maxdistrictId + ib.getCurrentTime());

			//Assume failure (-1) until the web app succeeds
			output = new DeliveryOutput(-1, Stcnum.getInstance().getStcnum());
			
			dds = new DistrictDeliveryStatistics();
			

			if (useAsync) {
				//************************************************************************
				// The following code was added to test the Asynchronous method function.
				// The deliverOrderforDistrict methods will be run asynchronously.
				// The returned Future objects will be stored in an ArrayList.
				//  We will loop through these future objects and ensure that they have all finished before closing the FileWriter.			
				//*************************************************************************

				ib.debugOut("Asynchronous support is not available yet ");
				/*
				Object[] futureArray = new Object [maxdistrictId];
				Future<SingleDeliveryOutput> future = null;

				for (int x = 0; x < maxdistrictId; x++) {
					singleDeliveries[x] = new SingleDelivery();
				}

				for (int dist = 1; dist <= maxdistrictId; dist++) {
					//invoke the async fire-and-return and fire-and-forget methods on the correct exposure (local or remote) of the bean
					ib.debugOut(" ERWW - Doing asynchronous method ");
						this.asyncNotifyDistrictWeAreSendingOrder(dist);
						future = this.secureAsyncDeliverOrderforDistrict(dist, input);
					// Bump up the async methods fire-and-forget "called" counter (ie. for application self validation)
					theEJBAsyncMethodStatsBean.fireAndForgetMethodCalled();

					// Save future away to retrieve results later
					futureArray[dist - 1] = future;
				}

				// get results from each asynchronous delivery
				for (int i=0; i < maxdistrictId; i++){
					try{	
						Integer futureGetTimeout = 30;  // default = 30 sec.
						String futureGetTimeoutPropertyString = System.getProperty("ERWW_AsyncMethodsFutureDotGetTimeout");
						if (futureGetTimeoutPropertyString != null) {
							futureGetTimeout = Integer.valueOf(futureGetTimeoutPropertyString);
						}

						SingleDeliveryOutput singleDeliveryOutput = ((Future<SingleDeliveryOutput>)futureArray[i]).get(futureGetTimeout, TimeUnit.SECONDS);
						ib.debugOut(" ERWW - Asynch method delivery skipped = " + singleDeliveryOutput.getDeliverySkipped());

						// aggregate results from each delivery
						singleDeliveries[i] = singleDeliveryOutput.getSingleDelivery();
						if (singleDeliveryOutput.getDeliverySkipped()){
							dds.addSkippedDistrict();
						} else {
							dds.addProcessedDistrict();
						}

					} catch (ExecutionException e1) {					
						if (e1.getCause()!=null){
							Throwable t = e1.getCause();
							System.out.println("ExecutionException in " + _className + ": asynch calling of DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict()");		
							t.printStackTrace();

							throw new ServletException("<&!@"+"Exception in " + _className + ": asynch calling of DeliverySessionNIVBean..secureAsyncDeliverOrderforDistrict()"+ib.getSTC()+ " " + 
									t.getMessage());
						}else{
							System.out.println("ExecutionException in " + _className + ": asynch calling of DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict()");		
							e1.printStackTrace();
							throw new ServletException("<&!@"+"Exception in " + _className + ": asynch calling of DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict()"+ib.getSTC()+ " " + 
									e1.getMessage());
						}					
					} catch (CancellationException e2) {
						e2.printStackTrace();
						throw new ServletException(
								"<&!@"+"CancellationException calling asynch DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict()" + ib.getSTC()+ " " + 
								ib.getStackTrace(e2));
					} catch (InterruptedException e3) {
						e3.printStackTrace();
						throw new ServletException(
								"<&!@"+"InterruptedException calling asynch DeliverySessionNIVBean.secureAsynceliverOrderforDistrict()" + ib.getSTC()+ " " + 
								ib.getStackTrace(e3));
					} catch (TimeoutException e4) {
						e4.printStackTrace();
						throw new ServletException(
								"<&!@"+"TimeoutException calling asynch DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict()" + ib.getSTC()+ " " + 
								ib.getStackTrace(e4));
					} catch (Throwable e5) {
						e5.printStackTrace();
						throw new ServletException(
								"<&!@"+"Throwable caught calling asynch DeliverySessionNIVBean.secureAsyncDeliverOrderforDistrict()" + ib.getSTC()+ " " + 
								ib.getStackTrace(e5));
					}
				} */
			} else {
				//**************************************************************************//
				// This code path will perform the delivery synchronously.
				// Note that EJB deliveries are always local because  Liberty does not
				// support remote EJB calls yet.
				//**************************************************************************//

				if (businessLogic.equals("synchronous_servlet")){

					// Business logic is right here in the servlet.  Synchronous calls are used.

					// Setup entity manager because business logic in servlet uses JPA
					
					
					try {
						tx.begin();
						ctx = new InitialContext();
						em = (EntityManager)ctx.lookup("java:comp/env/deliverypu/entitymanager");
					} catch (Exception ex) {
						System.out.println("%%%%% ERROR: transaction begin or getting the entity manager in class: " + _className);
						ex.printStackTrace();
						throw new ServletException("%%%%% ERROR:  transaction begin or getting the entity manager, exception: "  +	ex.getMessage());
					}
					
				
					// Now do deliveries for each District here
					try {
						for (int i = 1; i <= maxdistrictId; i++) {

							SingleDeliveryOutput singleDeliveryOutput = null;

							ib.debugOut(" ERWW - Doing synchronous method call.");

							singleDeliveryOutput = this.deliverOrderforDistrict(em, i, warehouseId, carrierId);

							// aggregate results from each delivery
							singleDeliveries[i - 1] = singleDeliveryOutput.getSingleDelivery();

							if (singleDeliveryOutput.getDeliverySkipped()){
								dds.addSkippedDistrict();
							} else {
								dds.addProcessedDistrict();
							}
						}

					} catch (Throwable t) {
						System.out.println("Exception in this.deliverOrderforDistrict()");
						t.printStackTrace();
						if (t instanceof Exception) {
							Exception ux = (Exception)t;
							if (ux.getMessage().indexOf("Failed due to -913")!=-1) throw new ServletException("<&!@"+"Failed due to -913, deadlock or timeout on call to this.deliverOrderforDistrict()"
									+ ux.getCause()+ib.getSTC());
							else throw new ServletException("<&!@"+"Exception calling this.deliverOrderforDistrict() "+ib.getSTC());
						} else {
							throw new ServletException("<&!@"+"Exception calling this.deliverOrderforDistrict() "+ib.getSTC()+ " " + t.getCause().getMessage());
						}
					}	


					try {
						tx.commit();
					} catch (Exception ex) {
						System.out.println("%%%%% ERROR: transaction commit " + _className + " " + ib.getSTC());
						ex.printStackTrace();
						throw new ServletException("%%%%% ERROR:  transaction commit - "+ib.getSTC() + " " + ex.getMessage());
					}
					
					// If we made it this far we should have success.
					output.setRetc(0);

				} else {
				
					// Business logic will be in the EJB (businessLogic.equals("synchronous_EJB")).
					
					// setup EJB reference and Delivery input object	
					try {
						InitialContext ic = new InitialContext();
						localDeliverySessionEJB = (DeliverySessionFacade) ic.lookup("java:comp/env/ejb/DeliverySessionFacadeBean");

					} catch (Exception ex) {
						System.out.println("%%%%% ERROR: looking up EJB for DeliverySessionFacadeBean in class: " + _className);
						ex.printStackTrace();
						throw new ServletException("%%%%% ERROR:  looking up EJB for DeliverySessionFacadeBean, exception: " +	ex.getMessage());
					}


					deliveryInput = new DeliveryInput();
					deliveryInput.setWarehouseId(warehouseId);
					deliveryInput.setCarrierId(carrierId);
					deliveryInput.setMaxdistrictId(maxdistrictId);
					deliveryInput.setDistributedEJBs(false);
					deliveryInput.setUse31Async(false);
					deliveryInput.setLeakDeliveryResults(false);

					// For business logic in EJB, call the EJB to process all orders for all districts
					try {
						ejbDeliveryOutput = localDeliverySessionEJB.deliverySession(deliveryInput);
											
						// Pull output out of delivery that common code below needs.
						singleDeliveries = ejbDeliveryOutput.getSingleDeliveries();
						dds = ejbDeliveryOutput.getDistrictDeliveryStatistics();
						
						ib.debugOut("EJB Path: First Single Delivery where warehouseId = " + singleDeliveries[0].getWarehouseId());
						
					} catch(UserException ue) {
						System.out.println("UserException calling localDelivertySessionEJB.deliverySession(deliveryInput), stcnum: "+ib.getSTC()+ " UserException: " + ue.getMessage());
						throw new ServletException("<&!@"+"UserException calling localDelivertySessionEJB.deliverySession(deliveryInput), stcnum: "+ib.getSTC()+ " exception: " + ue.getMessage());
					} catch(Throwable t) {
						System.out.println("Exception calling localDelivertySessionEJB.deliverySession(deliveryInput), stcnum: "+ib.getSTC()+ " Exception: " + t.getMessage());
						throw new ServletException("<&!@"+"Exception calling localDelivertySessionEJB.deliverySession(deliveryInput), stcnum: "+ib.getSTC()+ " exception: " + t.getMessage());							
					}
					
					// If we made it this far we should have success.
					output.setRetc(0);

				}

			}  // end else synchronous delivery


			// Going back to common code (used for both business logic in servlet and business logic in EJB)
			// now to process the output and do logging 	

			ib.debugOut("After BIG LOOP " + ib.getCurrentTime());
			ib.debugOut(" ERWW - Log a result message ");
			
	        // load up the output object   
			output.setUpSingleDeliveries(maxdistrictId);
			output.setSingleDeliveries(singleDeliveries);
			if (output.getRetc() == 0){
			   output.setStatus("successful");
			}   

			// reopen log file writer to "append" to existing log file
			try {
				resultFile = new java.io.FileWriter("results.out", true);
			} catch (Exception ex) {
				System.out.println("%%%%% ERROR: resultFile = new java.io.FileWriter(results.out, true) failed in doPost() - "	+ _className + " " + ib.getSTC());
				ex.printStackTrace();
				throw new ServletException("%%%%% ERROR: resultFile = new java.io.FileWriter(results.out, true) failed in doPost() - "+ib.getSTC()+ " " + ex.getMessage());
			}		

			int districtsSkipped = dds.getDistrictsSkipped();
			int districtsProcessed = dds.getDistrictsProcessed();

			log("Districts skipped: " + districtsSkipped + ", Districts processed: " + districtsProcessed + '\n', resultFile);

			if (districtsSkipped >= 1) {
				ib.debugOut(" ERWW - Delivered orders for " + (maxdistrictId - districtsSkipped) + " of " + maxdistrictId + " districts");
				log("Delivered orders for " + (maxdistrictId - districtsSkipped)+ " of " + maxdistrictId + " districts" + '\n', resultFile);
			}

			ib.debugOut(" ERWW - Log a termination message ");
			log("Finished delivery transaction for Warehouse " + warehouseId + ", Carrier " + carrierId + '\n', resultFile);

			ib.debugOut(" ERWW - Close the result file ");

			// close log file for last time
			try {
				resultFile.close();
			} catch (Exception ex) {
				System.out.println("%%%%% ERROR: resultFile.close() failed in doPost() - " + _className + " " + ib.getSTC());
				ex.printStackTrace();
				throw new ServletException("%%%%% ERROR: resultFile.close() failed in doPost() - " + _className + " " + ib.getSTC()+ " " + ex.getMessage());
			}

			request.setAttribute("output", output);

			ib.endOfTran("DeliveryServlet");

			dispatch(request, response, "DEResultsForm.jsp");
			
		} else {
			dispatch(request, response, "DEUserNotInRoleError.jsp");
		}

	}
	
	public SingleDeliveryOutput deliverOrderforDistrict(EntityManager em, int dist, short warehouseId, short carrierId) throws ServletException {

		ib.debugOut(" ERWW - Entering deliverOrderforDistrict method of DeliveryServlet.");

		short districtId = (short)dist;
		QueryNewOrderOutput queryNewOrderOutput = new QueryNewOrderOutput();
		Boolean skipped = Boolean.valueOf(true);
		int orderId = 0;
		short customerId = 0;
		BigDecimal amount = new BigDecimal(0);
		
		NewOrderJPA newOrder = null;
	    OrderJPA order = null;
		CustomerJPA customer = null;
		
		// Prepare output object
		SingleDeliveryOutput singleDeliveryOutput = new SingleDeliveryOutput();

		// Setup log file writer to "append" to existing log file
		java.io.FileWriter resultFile = null;	
		try {
			resultFile = new java.io.FileWriter("results.out", true);
		} catch (Exception ex) {
			System.out.println("%%%%% ERROR: resultFile = new java.io.FileWriter(results.out, true) failed in deliverOrderforDistrict method - " + _className + " " + ib.getSTC());
			ex.printStackTrace();
			throw new ServletException(	"%%%%% ERROR: resultFile = new java.io.FileWriter(results.out, true) failed in deliverOrderforDistrict method - "+ib.getSTC()+ " " + ex.getMessage());
		}
		
		ib.debugOut("In deliverOrderforDistrict: warehouseId: " + warehouseId	+ " districtId: " + districtId);

		ib.debugOut("<Before Query New Order objects and find the oldest, undelivered Order "	+ ib.getCurrentTime());
		queryNewOrderOutput = queryNewOrderInstances(em, warehouseId, districtId);
		ib.debugOut("<After queryNewOrderInstances " + ib.getCurrentTime());

		skipped = queryNewOrderOutput.getSkipped();
		orderId = queryNewOrderOutput.getOrderId();
		newOrder = queryNewOrderOutput.getNewOrder();
		ib.debugOut("skipped-> " + skipped + "orderid-> " + orderId);

		if (skipped.equals(Boolean.FALSE)) {

			try {
				ib.debugOut("<Before removing NewOrder " + ib.getCurrentTime());
				em.remove(newOrder);
			} catch (Exception t) {
				t.printStackTrace();
				if (ib.is913(t)) {
					// Added this try/catch to remove the warning about a resource
					// leak for resultFile not being closed.
					try {
						resultFile.close();
					} catch (IOException e) {
						ib.debugOut("Failed to close the results file.");
						e.printStackTrace();
					}
					throw new ServletException("<&!@"+checkFor913(t)+", removing NewOrder JPA, "+ t.getCause()+ib.getSTC()+ " " + ib.getStackTrace(t));
				}
				else {
					// Added this try/catch to remove the warning about a resource
					// leak for resultFile not being closed.
					try {
						resultFile.close();
					} catch (IOException e) {
						ib.debugOut("Failed to close the results file.");
						e.printStackTrace();
					}
					throw new ServletException("<&!@"+"Error during remove of NewOrder "+",exception message: "+t.getMessage()+", exception cause: "+ t.getCause()+ib.getSTC()+ " "+ ib.getStackTrace(t));
				}
			}
			ib.debugOut("<After removing NewOrder " + ib.getCurrentTime());

			ib.debugOut(" ERWW - Select the appropriate Order object and update the Carrier ID = "	+ carrierId);

			ib.debugOut("<Before queryOrderInstances " + ib.getCurrentTime());
			order = queryOrderInstances(em, orderId, districtId, warehouseId);
			ib.debugOut("<After queryOrderInstances" + ib.getCurrentTime());

			order.setCarrierId(carrierId);
			customerId = order.getCustomerId();

			ib.debugOut(" ERWW - Query the Orderline objects, update the delivery data, retrieve orderline amount");
			java.math.BigDecimal count = null;
			count = order.getOrderLineCount();
			ib.debugOut("<Before processOrderlineInstance " + ib.getCurrentTime());
			amount = processOrderlineInstance(em, orderId, districtId, warehouseId,	count.intValue());
			ib.debugOut("<After processOrderlineInstance " + ib.getCurrentTime());

			ib.debugOut("<Before customerInstance " + ib.getCurrentTime());
			customer = getCustomerInstance(em, customerId, districtId, warehouseId);
			ib.debugOut("<After customerInstance " + ib.getCurrentTime());

			ib.debugOut(" ERWW - Orderline amount = " + amount + " Customer ID = "	+ customerId);
			
			ib.debugOut(" ERWW - Updating Customer Object, updated customer balance & delivery amount");

			// Avoid overflow of customer balance field.
			BigDecimal amt=amount.add(customer.getCustomerBalance());			
			if (amt.doubleValue()>9999999999.99) {
				// Replaced the commented out line with one below it to remove a static warning
				//amt=new BigDecimal(9999999999.99).setScale(2,amt.ROUND_HALF_DOWN);			
				amt=new BigDecimal(9999999999.99).setScale(2,RoundingMode.HALF_DOWN);			
			}
			customer.setCustomerBalance(amt);

			// Avoid overflow of customer delivery count field.
			int custDelCount = customer.getCustomerDeliveryCnt().intValue() + 1;
			if (custDelCount>=10000) {
				custDelCount=9999;
			}
			customer.setCustomerDeliveryCnt(new BigDecimal(custDelCount));	

			ib.debugOut("Delivered O " + orderId + " D " + districtId + " W " + warehouseId);

			log("Delivered Order " + orderId + " for District " + districtId + " in Warehouse " + warehouseId + " for Amount " + amount	+ '\n', resultFile);
			
			// write output object - delivery was processed
			singleDeliveryOutput.setDeliverySkipped(false);
			SingleDelivery singleDelivery = new SingleDelivery();
			singleDelivery.setWarehouseId(warehouseId);
			singleDelivery.setDistrictId(districtId);
			singleDelivery.setOrderId(orderId);
			singleDelivery.setCustomerId(customerId);
			singleDelivery.setAmount(amount);
			singleDeliveryOutput.setSingleDelivery(singleDelivery);
		
		} // end if
		else {

			ib.debugOut("Skipping a delivery");

			ib.debugOut("<Before skip delivery log entry " + ib.getCurrentTime());
			log("Skipping delivery for Warehouse " + warehouseId + ", District " + districtId + '\n', resultFile);
			ib.debugOut("<After skip delivery log entry " + ib.getCurrentTime());		

			// write output object - delivery was skipped
			singleDeliveryOutput.setDeliverySkipped(true);
			SingleDelivery singleDelivery = new SingleDelivery();
			singleDelivery.setWarehouseId(warehouseId);
			singleDelivery.setDistrictId(districtId);
			singleDeliveryOutput.setSingleDelivery(singleDelivery);	

		} // end else
		
		try {
			// close log file for now.
			resultFile.close();
		} catch (Exception ex) {
			System.out.println("%%%%% ERROR: resultFile.close() failed in deliverOrderforDistrict method - " + _className + " " + ib.getSTC());
			ex.printStackTrace();
			throw new ServletException("%%%%% ERROR: resultFile.close() failed in deliverOrderforDistrict method - " + _className + " " + ib.getSTC()+ " " + ex.getMessage());
		}
		
		return (singleDeliveryOutput);
	}
	
	protected CustomerJPA getCustomerInstance(EntityManager em, short customerId, short districtId,short warehouseId) throws ServletException {
		
		CustomerJPA customer = null;
		
		try {
			CustomerKey key = new CustomerKey();
			key.setCustomerDId(districtId);
			key.setCustomerId(customerId);
			key.setCustomerWId(warehouseId);

			customer = em.find(CustomerJPA.class, key, LockModeType.PESSIMISTIC_WRITE);
			if (customer==null)	throw new ServletException("customer not found: district="+districtId+", customer="+customerId+" and warehouse="+warehouseId+", "+ib.getSTC()+ "");

		} catch (Exception t) {
			t.printStackTrace();
			if (ib.is913(t))
				throw new ServletException("<&!@"+checkFor913(t)+", finding Customer JPA, "+ t.getCause()+ib.getSTC()+ " " + ib.getStackTrace(t));
			else
				throw new ServletException("<&!@"+"failure in getCustomerInstance: "
						+",exception message: "+t.getMessage()+", exception cause: "+ t.getCause()+ib.getSTC()+ " " + ib.getStackTrace(t));
		} // end try

		return customer;
	}
	
	private java.math.BigDecimal processOrderlineInstance(EntityManager em, int orderId,
			short districtId, short warehouseId, int count) throws ServletException {

		ib.debugOut("<<< in processOrderlineInstance - orderId: " + orderId
				+ " districtId: " + districtId + " warehouseId: " + warehouseId
				+ " count: " + count);

		Timestamp timeStamp = new Timestamp(java.lang.System
				.currentTimeMillis());
		BigDecimal amount = new BigDecimal(0);
		short olNumber = 0;
		
		OrderlineJPA orderline = null;
		OrderlineKey key = null;

		for (short i = 1; i <= count; i++) {

			olNumber = i;
			try {
				key = new OrderlineKey(orderId, districtId, warehouseId, olNumber);
				orderline = em.find(OrderlineJPA.class, key, LockModeType.PESSIMISTIC_WRITE);
				if (orderline==null) throw new ServletException("orderline not found: order="+orderId+", districtId="+districtId+", warehouse="+warehouseId+" and orderline number="+olNumber+" " +ib.getSTC()+ "");

			} catch (Exception ex) {
				System.out.println("%%%%% ERROR: em.find failed in processOrderlineInstance method - "	+ _className + " " + ib.getSTC());
				ex.printStackTrace();
				if (ib.is913(ex))
					throw new ServletException("<&!@"+checkFor913(ex)+", finding Orderline JPA, "+ ex.getCause()+ib.getSTC()+ " " + ib.getStackTrace(ex));
				else
					throw new ServletException("<&!@"+ "%%%%% ERROR: em.find failed in processOrderlineInstance method - "
									+ _className + " " +",exception message: "+ex.getMessage()+", exception cause: "+ ex.getCause()+ib.getSTC()+ " " + ib.getStackTrace(ex));
			}
			if (orderline != null) {
				amount = orderline.getOlAmount().add(amount);
				ib.debugOut("Orderline Delivery Date for olNumber = " + olNumber + " is set to " + timeStamp);
				orderline.setOlDeliveryDate(timeStamp);
			} // if
			/*Comment out dead code
			else {
				ib.debugOut("Failure with Orderline number " + i + "!!!");
			}// else
			*/
		} // for

		return amount; // method processOrderlineInstances
	}

	private QueryNewOrderOutput queryNewOrderInstances(EntityManager em, short warehouseId,
			short districtId) throws ServletException {
		
		int lowerOrderId = 0;

		QueryNewOrderOutput queryNewOrderOutput = new QueryNewOrderOutput();
		Boolean skipped = Boolean.valueOf(true);
		Boolean TRUE = Boolean.valueOf(true);
		Boolean FALSE = Boolean.valueOf(false);
		NewOrderJPA newOrder=null;
		int cnt = 0;

		try {
			Query q = em.createNamedQuery("findMinNewOrder");
			q.setParameter("warehouseId", warehouseId);
			q.setParameter("districtId", districtId);

			ib.debugOut("warehouseId: " + warehouseId + " districtId: " + districtId);
			
			q.setLockMode(LockModeType.PESSIMISTIC_WRITE);
			
			Collection<?> coll = null;
			coll = q.getResultList();
			if (coll != null) {
				Iterator<?> it = coll.iterator();

				while (it.hasNext()) {
					cnt++;
					newOrder = (NewOrderJPA) it.next();
					ib.debugOut("FindMinNewOrder rows::" + cnt + ": OrderID =  "
							+ newOrder.getOrderId() + " WarehouseId = "
							+ newOrder.getWarehouseId() + " DistrictId = "
							+ newOrder.getDistrictId());
				}

				if (cnt == 0)
					ib.debugOut("FindMinOrder returned no rows.");
			}
			
			if (newOrder == null) {
				lowerOrderId = 0;
				skipped = TRUE;
				ib.debugOut("<<< No NewOrders found");
			} else {
				lowerOrderId = newOrder.getOrderId();
				skipped = FALSE;
				ib.debugOut("<<< Minimum NewOrder: OrderID = " + newOrder.getOrderId() + 
						" WarehouseId = " + newOrder.getWarehouseId() + 
						" DistrictId =  " + newOrder.getDistrictId() + 
						" : ThreadId = " + Thread.currentThread().getId());
			}				
			ib.debugOut("Skipped = " + skipped);

		} catch (Exception e) {
			e.printStackTrace();
			if (ib.is913(e)) {
				throw new ServletException("<&!@"+checkFor913(e)+", finding mininum NewOrder JPA, "+ e.getCause()+ " " + ib.getSTC()+ " " + ib.getStackTrace(e));
			}
			else {			
				if (e instanceof NullPointerException) {
					System.out.println("Query found no matching rows, districtId="+districtId+", warehouseId="+warehouseId);
				}
			}
			
			throw new ServletException("Unexpected exception"+ " " + e + " " + "Exception in queryNewOrder");
			
		} finally {  
			queryNewOrderOutput.setSkipped(skipped);
			queryNewOrderOutput.setOrderId(lowerOrderId);
			queryNewOrderOutput.setNewOrder(newOrder);
		}

		return queryNewOrderOutput; // method queryNewOrderInstances
	}
	
	private OrderJPA queryOrderInstances(EntityManager em, int orderId, short districtId,
			short warehouseId) throws ServletException {
		
		OrderJPA order = null;
		OrderKey key = new OrderKey(districtId, orderId, warehouseId);

		ib.debugOut("In queryOrderInstances - wid=" + warehouseId);
		ib.debugOut("In queryOrderInstances - oid=" + orderId);
		ib.debugOut("In queryOrderInstances - did=" + districtId);

		//Comment out unused variable
		//short customerId = 0;
		try {
			order = em.find(OrderJPA.class, key, LockModeType.PESSIMISTIC_WRITE);
			
			ib.debugOut("In " + _className + ": OrderJPA: queryOrderInstances: After em.find(OrderJPA.class, key): " +
					" WarehouseId = " + warehouseId + 
					" DistrictId = " + districtId +
					" OrderId = " + orderId + 
					" : threadId = " + Thread.currentThread().getId());
			
			if (order==null)	throw new ServletException("order not found: warehouseId="+warehouseId+", orderId="+orderId+" and districtId= "+districtId+", "+ib.getSTC()+ "");
		} catch (Exception t) {
			t.printStackTrace();
			if (ib.is913(t))
				throw new ServletException("<&!@"+checkFor913(t)+", finding Order JPA, "+ t.getCause()+ib.getSTC()+ " " + ib.getStackTrace(t));
			
			else
				throw new ServletException("<&!@"+"Exception while finding OrderJPA"+",exception message: "+ t.getMessage()+ " exception cause: "+ t.getCause()+ib.getSTC()+ " "+
						ib.getStackTrace(t));
		}

		return order; // method queryOrderInstances

	}

	public String checkFor913(Throwable t) {
		String nine_thirteen = "false";

		if (t instanceof SQLException)
			;
		else
			t = t.getCause();

		if (t instanceof SQLException) {
			SQLException sqle = (SQLException) t;
			System.out.println("SQL CODE=" + sqle.getErrorCode()
					+ " SQL STATE=" + sqle.getSQLState());

			String holdTokens = " ";
			boolean keepGoing = true;

			while (sqle != null) {

				if ((sqle.getErrorCode() == -913)
						|| sqle.getErrorCode() == -911)
					nine_thirteen = "Failed due to -913 or -911, deadlock or timeout";
				DB2Diagnosable diagnosable = null;
				try {
					// Check if DB2-only information exists
					if (sqle instanceof DB2Diagnosable) {
						diagnosable = (DB2Diagnosable) sqle;
						keepGoing = true;
					}else {
						keepGoing = false;
					}
				}catch (Exception e) {
					e.printStackTrace();
			    }
				if (keepGoing) {
					DB2Sqlca sqlca = diagnosable.getSqlca();
					// Get DB2Sqlca object
					if (sqlca != null) { // Check that DB2Sqlca is not null
						//Comment out unused variables sqlCode, sqlErrmc, sqlErrp, sqlErrd, sqlWarn, and sqlState
						//int sqlCode = sqlca.getSqlCode(); // Get the SQL error
						// code 3b4
						//String sqlErrmc = sqlca.getSqlErrmc();
						// Get the entire SQLERRMC
						String[] sqlErrmcTokens = sqlca.getSqlErrmcTokens();
						// You can also retrieve the
						// individual SQLERRMC tokens
						//String sqlErrp = sqlca.getSqlErrp();
						// Get the SQLERRP
						//int[] sqlErrd = sqlca.getSqlErrd();
						// Get SQLERRD fields
						//char[] sqlWarn = sqlca.getSqlWarn();
						// Get SQLWARN fields
						//String sqlState = sqlca.getSqlState();
						// Get SQLSTATE
						holdTokens = ",tokens:  ";
 
						if (sqlErrmcTokens != null) {
						    for (int i = 0; i < sqlErrmcTokens.length; i++) {
							    System.out.println("<<token1oop[" + i + "]>>");
							    holdTokens = holdTokens + " , " + sqlErrmcTokens[i];
							}    
						}
					}

					if (nine_thirteen
							.indexOf("Failed due to -913 or -911, deadlock or timeout") != -1)
						nine_thirteen = nine_thirteen + ", " + holdTokens;
					if (nine_thirteen
							.indexOf("Failed due to -913 or -911, deadlock or timeout") != -1)
						break;
				}
				// SQLException
				sqle = sqle.getNextException(); // Retrieve next
			}
		}
		return nine_thirteen;
	}
	
	
	
	
	public boolean isUserInSecurityRole(HttpServletRequest request, HttpServletResponse response, String application, String role) throws IOException {

        Principal principal = null;
        String principalName = null;
        String [] usernames = null;
        String isUserInRoleResults = null;
        boolean isUserInRole = false;
        
        ib.debugOut("<<< Entering isUserInSecurityRole()");
       			
        principal = request.getUserPrincipal();
        ib.debugOut("<<< Security:  Principal = " + principal);

        if (principal != null) {
        	principalName = principal.getName();
        	ib.debugOut("<<< Security: principalName = " + principalName);
        	usernames = principalName.split("/");
        	ib.debugOut("<<< Security: username = " + usernames[0]);
        	            
           	isUserInRole = request.isUserInRole(role);
            ib.debugOut("<<< isUserInRole = " + isUserInRole);
        	
        	if (isUserInRole){
        		isUserInRoleResults = " User "+ usernames[0] + " is in the " + role;
        	}else{
        		isUserInRoleResults = " User "+ usernames[0] + " is NOT in the " + role;
         	}       	
        }else{
        	isUserInRoleResults = " principal is null";
        }
		ib.debugOut("<<<" + application + " " + isUserInRoleResults );
		
   		request.setAttribute("isUserInRoleResults", isUserInRoleResults);

		return isUserInRole;
	}
	
	public boolean checkUserInRoleWhenAppSecurityEnabled(HttpServletRequest request, HttpServletResponse response, String application, String role) throws IOException {

		boolean checkUserInRole = false;
        //boolean isServerSecurityEnabled = false;           
        boolean isSecurityCondtionMet = false;

        ib.debugOut("<<< Entering checkUserInRoleWhenAppSecurityEnabled()");
        //Comment out unused variable
        //PrintWriter out = response.getWriter();
        
	    checkUserInRole = Boolean.valueOf(request.getParameter("checkUserInRole"));

    	//isServerSecurityEnabled = WSSecurityHelper.isServerSecurityEnabled(); //temporarily commenting this out until Liberty supports the API
         
        //ib.debugOut("<<< Server security enabled = " + isServerSecurityEnabled);      

        if(checkUserInRole) { 
        	ib.debugOut("<<< Role Checking using isUserInRole(CompanyRole) will be performed.");
        	isSecurityCondtionMet = isUserInSecurityRole(request, response, application, role);
        } else { 
        	ib.debugOut("<<< Role Checking using isUserInRole(CompanyRole) will NOT be performed.");
      		isSecurityCondtionMet = true;
        }
        
        ib.debugOut("<<< isSecurityCondtionMet = " + isSecurityCondtionMet);   

		return isSecurityCondtionMet;
	}
		
	private void log(String logline, java.io.FileWriter resultFile) throws ServletException {

		String timeStamp = new String("");
		timeStamp = getTimeStamp();

		try {
			resultFile.write(timeStamp + " - " + logline);
		} catch (Exception ex) {
			System.out
					.println("%%%%% ERROR: resultFile.write failed in log method - "
							+ _className + " " + ib.getSTC());
			ex.printStackTrace();
			throw new ServletException(
					"%%%%% ERROR: resultFile.write failed in log method - "
							+ _className + " " + ib.getSTC()+ " " + ib.getStackTrace(ex));
		}
		return; // method log

	}
	
	private String getTimeStamp() {

		// Get Time Stamp in the form of yyyy-mm-dd-hh.mm.ss.nnnnnn
		// The toString methods returns the following format: dow mon dd
		// hh:mm:ss zzz yyyy

		Timestamp timestamp = new Timestamp(java.lang.System.currentTimeMillis());
		String timeStamp = timestamp.toString();

		return timeStamp; // method getTimeStamp
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
