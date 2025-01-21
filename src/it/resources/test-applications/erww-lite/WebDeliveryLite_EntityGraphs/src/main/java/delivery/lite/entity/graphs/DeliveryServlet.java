package delivery.lite.entity.graphs;

import irwwbase.ExtendedRandom;
import irwwbase.IRWWBase;
import irwwbase.MaxValues;
import irwwbase.Stcnum;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.Query;
import jakarta.persistence.Subgraph;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.UserTransaction;

import neworderjpa.NewOrderJPA;
import orderjpa.OrderJPA;
import orderjpa.OrderKey;
import orderlinejpa.OrderlineJPA;
import orderlinejpa.OrderlineKey;

import com.ibm.db2.jcc.DB2Diagnosable;
import com.ibm.db2.jcc.DB2Sqlca;

import customerjpa.CustomerJPA;
import customerjpa.CustomerKey;
import deliverysession.DeliveryOutput;
import deliverysession.DistrictDeliveryStatistics;
import deliverysession.SingleDelivery;
import deliverysession.SingleDeliveryOutput;

/**
 * 
 * DeliveryServlet will handle incoming http requests and perform business logic in order to
 * deliver N orders, where N is the number of districts in your database. Specifically, this 
 * class was written to test JPA-2.1 functionality through the use of an Entity Graph when 
 * loading an Order from the database.  
 * <P>
 * Half of the deliveries will use a fetch graph that will eagerly load all the orderlines that 
 * are associated with an order.  The other half will use a load graph, that will lazily load the 
 * orderlines (i.e. they wont be loaded when the order is loaded). In addition to the orderlines, 
 * other attributes are specified to further test the Entity Graph is working properly.  
 * <P>
 * After loading, the order object is verified to have loaded the correct data from the database.  
 * If the graph did not work as designed, an error is thrown and the transaction is forced to fail.  
 * Finally, the average time it takes to load what is needed for the order and it's orderlines is 
 * displayed on the result page for both fetch and load graph. The expectation is that the fetch graph 
 * takes a shorter amount of time, though due to the small amount of data being loaded there will be 
 * times when the load graph is quicker than fetch.  This is only seen when the server and database
 * are on the same machine.  When the database is remote, fetch graph will always be quicker.
 * <P>
 * @author Greg Ecock
 */
@WebServlet("/DeliveryServlet")
public class DeliveryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@PersistenceUnit (name="DeliveryEntityGraphs")
	EntityManagerFactory emf;
	
	@Resource
	UserTransaction ut;
	
	IRWWBase ib = new IRWWBase(); 
	private java.lang.String _className = (DeliveryServlet.class).getName();
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeliveryServlet() {
        super();
    }

	/**
	 * Creates an EntityManager to be used throughout the transaction, handles the parsing of web input parameters,
	 * calls for N deliveries (where N is the number of districts), and accumulates and formats the results form information.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String inputType = "AutoGeneration";
		short warehouseId = 0;
		short carrierId = 0;
		int maxdistrictId =  0;

		java.io.FileWriter resultFile = null;
		DistrictDeliveryStatistics dds = null;

		DeliveryOutput output = null;
		SingleDelivery[] singleDeliveries = null;

		byte db = ib.dbSize();
		
		EntityManager em = null;
		
		ib.startOfTran("DeliveryServlet");
		
		inputType = request.getParameter("inputType");

		if (inputType.contains("Manual")){
			warehouseId = Short.valueOf(request.getParameter("warehouseId"));
			carrierId = Short.valueOf(request.getParameter("carrierId"));
		} else {
			ExtendedRandom rand = new ExtendedRandom();
			warehouseId = ((short) rand.nextInt(1, MaxValues.warehouseId(db)));
			carrierId = ((short) rand.nextInt(1, MaxValues.carrierId(db)));
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
		try {
			ut.begin();
			em = emf.createEntityManager();
		} catch (Exception ex) {
			System.out.println("%%%%% ERROR: transaction begin or getting the entity manager in class: " + _className);
			ex.printStackTrace();
			throw new ServletException("%%%%% ERROR:  transaction begin or getting the entity manager, exception: "  +	ex.getMessage());
		}
		
		int numFetchGraphs = 0;
		long totalFetchGraphTime = 0;
		Long fetchGraphAverage = Long.valueOf(0);
		int numLoadGraphs = 0;
		long totalLoadGraphTime = 0;
		Long loadGraphAverage = Long.valueOf(0);
		boolean useFetchGraph = true;
		long startTime = 0;
		long stopTime = 0;

		// Now do deliveries for each District here
		try {
			for (int i = 1; i <= maxdistrictId; i++) {
				startTime = 0;
				stopTime = 0;
				
				if(i%2==0)
					useFetchGraph = true;
				else
					useFetchGraph = false;

				SingleDeliveryOutput singleDeliveryOutput = null;

				ib.debugOut(" ERWW - Doing synchronous method call.");
				startTime = System.currentTimeMillis();

				singleDeliveryOutput = this.deliverOrderforDistrict(em, i, warehouseId, carrierId, useFetchGraph);
				
				stopTime = System.currentTimeMillis();
				// aggregate results from each delivery
				singleDeliveries[i - 1] = singleDeliveryOutput.getSingleDelivery();

				if (singleDeliveryOutput.getDeliverySkipped()){
					dds.addSkippedDistrict();
				} else {
					dds.addProcessedDistrict();
				}
				
				if(useFetchGraph && i>=1 && i<=maxdistrictId){
					totalFetchGraphTime = totalFetchGraphTime + (stopTime - startTime);
					numFetchGraphs++;
					ib.debugOut("Fetch Graph took: " + (stopTime-startTime));
				}
				else if (i>=1 && i<=maxdistrictId){
					totalLoadGraphTime = totalLoadGraphTime + (stopTime - startTime);
					numLoadGraphs++;
					ib.debugOut("Load graph took: " + (stopTime-startTime));
				}
			}
			
			fetchGraphAverage = totalFetchGraphTime/numFetchGraphs;
			loadGraphAverage = totalLoadGraphTime/numLoadGraphs;

		} catch (Throwable t) {
			System.out.println("Exception in this.deliverOrderforDistrict()");
			t.printStackTrace();
			if (t instanceof Exception) {
				Exception ux = (Exception)t;
				if (ux.getMessage().indexOf("Failed due to -913")!=-1) throw new ServletException("<&!@"+"Failed due to -913, deadlock or timeout on call to this.deliverOrderforDistrict()"
						+ ux.getCause()+ib.getSTC());
				else throw new ServletException("<&!@"+"Exception calling this.deliverOrderforDistrict() "+ib.getSTC() + " " + t.getMessage());
			} else {
				throw new ServletException("<&!@"+"Exception calling this.deliverOrderforDistrict() "+ib.getSTC()+ " " + t.getCause().getMessage());
			}
		}	


		try {
			ut.commit();
		} catch (Exception ex) {
			System.out.println("%%%%% ERROR: transaction commit " + _className + " " + ib.getSTC());
			ex.printStackTrace();
			throw new ServletException("%%%%% ERROR:  transaction commit - "+ib.getSTC() + " " + ex.getMessage());
		}
		
		// If we made it this far we should have success.
		output.setRetc(0);
		
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
		request.setAttribute("fetchGraphTime", fetchGraphAverage);
		request.setAttribute("loadGraphTime", loadGraphAverage);

		ib.endOfTran("DeliveryServlet");

		dispatch(request, response, "DEResultsForm.jsp");
		
	}

	/* Commenting out this function because of the warning that it was
	 * never used.
	
	private void verifyLoadBidirectional(OrderJPA order, EntityManager em) throws ServletException{
		PersistenceUnitUtil unitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
		
		//Attributes that should not have been loaded
		if(unitUtil.isLoaded(order, "AllLocal")){
			ib.debugOut(">>>AllLocal was loaded by the EntityGraph when it wasn't supposed to be");
			throw new ServletException("%%%%% ERROR: The entity graph failed to load AllLocal from OrderJPA" +
					ib.getSTC());
		}
		else{
			ib.debugOut(">>>AllLoacl was successfully not loaded by the EntityGraph");
		}
		//Attributes that should not have been loaded
		if(unitUtil.isLoaded(order, "orderlines"))
			ib.debugOut(">>>orderlines were successfully loaded by the EntityGraph");
		else{
			ib.debugOut(">>>orderlines failed to be loaded by the EntityGraph");
			throw new ServletException("%%%%% ERROR: The entity graph failed to load the orderlines from OrderJPA" +
					ib.getSTC());
		}
		
	}
    */
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
	
	/**
	 * Contains the main business logic for the delivery transaction. Finds the oldest, undelivered NewOrder and removes it.  
	 * Then finds the associated Order object in order to update the carrier id and find it's orderlines.  The orderlines
	 * are then used to sum up the total amount of the order and each is given an updated delivery date. The associated 
	 * customer is then found and it's balance and delivery count are updated.
	 * 
	 * @param em The entity manager used for the transaction.
	 * @param dist The district id where the desired entities reside.
	 * @param warehouseId The warehouse id where the desired entities reside.
	 * @param carrierId The carrier id to be used to update the order.
	 * @return A SingleDeliveryOutput object which contains information regarding the delivery.
	 * @see deliverysession.SingleDeliveryOutput
	 * @throws ServletException If something goes wrong during removal of the NewOrder, opening/closing of the result file,
	 * 							or any of the methods that are called.
	 */
	public SingleDeliveryOutput deliverOrderforDistrict(EntityManager em, int dist, short warehouseId, short carrierId, boolean fetchGraph) 
			throws ServletException {

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
		ib.debugOut("skipped-> " + skipped + " orderid-> " + orderId);

		if (skipped.equals(Boolean.FALSE)) {

			try {
				ib.debugOut("<Before removing NewOrder " + ib.getCurrentTime());
				em.remove(newOrder);
			} catch (Exception t) {
				t.printStackTrace();
				if (ib.is913(t)) {
					//Added this try/catch to remove the warning about
					//a resource leak for resultFile not being closed
					try {
						resultFile.close();
					} catch (IOException e) {
						ib.debugOut("The resultFile failed to close.");
						e.printStackTrace();
					}
					throw new ServletException("<&!@"+checkFor913(t)+", removing NewOrder JPA, "+ t.getCause()+ib.getSTC()+ " " + ib.getStackTrace(t));
				}
				else {
					//Added this try/catch to remove the warning about
					//a resource leak for resultFile not being closed
					try {
						resultFile.close();
					} catch (IOException e) {
						ib.debugOut("The resultFile failed to close.");
						e.printStackTrace();
					}
					throw new ServletException("<&!@"+"Error during remove of NewOrder "+",exception message: "+t.getMessage()+", exception cause: "+ t.getCause()+ib.getSTC()+ " "+ ib.getStackTrace(t));
				}
			}
			ib.debugOut("<After removing NewOrder " + ib.getCurrentTime());

			ib.debugOut(" ERWW - Select the appropriate Order object and update the Carrier ID = "	+ carrierId);

			ib.debugOut("<Before queryOrderInstances " + ib.getCurrentTime());
			//startTime = System.currentTimeMillis();
			order = queryOrderInstances(em, orderId, districtId, warehouseId, fetchGraph);
			ib.debugOut("<After queryOrderInstances" + ib.getCurrentTime());

			order.setCarrierId(carrierId);
			customerId = order.getCustomerId();

			ib.debugOut(" ERWW - Query the Orderline objects, update the delivery data, retrieve orderline amount");
			java.math.BigDecimal count = null;
			count = order.getOrderLineCount();
			ib.debugOut("<Before processOrderlineInstance " + ib.getCurrentTime());
			amount = processOrderlineInstance(em, order, orderId, districtId, warehouseId,	count.intValue(), fetchGraph);
			//stopTime = System.currentTimeMillis();
			ib.debugOut("<After processOrderlineInstance " + ib.getCurrentTime());

			ib.debugOut("<Before customerInstance " + ib.getCurrentTime());
			customer = getCustomerInstance(em, customerId, districtId, warehouseId);
			ib.debugOut("<After customerInstance " + ib.getCurrentTime());

			ib.debugOut(" ERWW - Orderline amount = " + amount + " Customer ID = "	+ customerId);
			
			ib.debugOut(" ERWW - Updating Customer Object, updated customer balance & delivery amount");

			// Avoid overflow of customer balance field.
			BigDecimal amt=amount.add(customer.getCustomerBalance());			
			if (amt.doubleValue()>9999999999.99) {
				// Changed this line amt=new BigDecimal(9999999999.99).setScale(2,amt.ROUND_HALF_DOWN);			
				// to access ROUND_HALF_DOWN in a static way
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
	
	/**
	 * Finds a customer based on the customer, district, and warehouse ids given as params.
	 * 
	 * @param em The entity manager used by the transaction.
	 * @param customerId The customer id of the primary key used to find the customer.
	 * @param districtId The district id of the primary key used to find the customer.
	 * @param warehouseId The warehouse id of the primary key used to find the customer.
	 * @return The desired customer from the database.
	 * @throws ServletException If something goes wrong finding the customer from the database.
	 */
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
	
	/**
	 * Sums up the olAmounts of all the orderlines associated with the given order.
	 * 
	 * @param em The entity manager for the transaction.
	 * @param order The OrderJPA object corresponding to the orderId, distrcitId, and warehouseId params.
	 * @param orderId The orderId primary key for the OrderJPA object param.
	 * @param districtId The districtId primary key for the OrderJPA object param.
	 * @param warehouseId The warehouseId primary key for the OrderJPA object param.
	 * @param count The number of orderlines associated with the OrderJPA object param.
	 * @return The sum of all the orderline olAmounts.
	 * @throws ServletException 
	 */
	private java.math.BigDecimal processOrderlineInstance(EntityManager em, OrderJPA order, int orderId,
			short districtId, short warehouseId, int count, boolean fetchGraph) throws ServletException {

		ib.debugOut("<<< in processOrderlineInstance - orderId: " + orderId
				+ " districtId: " + districtId + " warehouseId: " + warehouseId
				+ " count: " + count);

		Timestamp timeStamp = new Timestamp(java.lang.System
				.currentTimeMillis());

		if(fetchGraph){
			BigDecimal amount2 = new BigDecimal(0);

			int index = 1;

			for(OrderlineJPA ol : order.getOrderlines()){
				if(ol != null){
					amount2 = ol.getOlAmount().add(amount2);
					ib.debugOut("****Orderline Delivery Date for olNumber = " + ol.getOlNumber() + " is set to " + timeStamp);
					ol.setOlDeliveryDate(timeStamp);
				}
				else{
					ib.debugOut("****Failure with Orderline number " + index + "!!");
				}
				index++;
			}
			return amount2;
		}
		else{
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
				amount = orderline.getOlAmount().add(amount);
				ib.debugOut("Orderline Delivery Date for olNumber = " + olNumber + " is set to " + timeStamp);
				orderline.setOlDeliveryDate(timeStamp);
				
			} // for
			return amount;
		}

		 // method processOrderlineInstances
	}

	/**
	 * Finds a NewOrder based in the warehouse and district ids passed as parameters.
	 * 
	 * @param em The entity manager used by the transaction.
	 * @param warehouseId The warehouseId used as part of the NewOrder primary key.
	 * @param districtId The districtId used as part of the NewOrder primary key.
	 * @return A QueryNewOrderOutput object containing the found NewOrder, whether an order 
	 * 			was skipped, and the orderId associated with the NewOrder.
	 * @throws ServletException If something goes wrong querying the database for the NewOrder.
	 */
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
	
	/**
	 * Finds an order based on the warehouse, district and order ids, while using an entity graph
	 * to specify which attributes to load eagerly.
	 * 
	 * @param em The entity manager for the transaction.
	 * @param orderId The orderId to be used for finding the correct order.
	 * @param districtId The districtId to be used for finding the correct order.
	 * @param warehouseId The warehouseId to be used for finding the correct order.
	 * @return The OrderJPA object corresponding the the primary key consisting of the params.
	 * @throws ServletException If something went wrong finding the order.
	 */
	private OrderJPA queryOrderInstances(EntityManager em, int orderId, short districtId,
			short warehouseId, boolean fetchGraph) throws ServletException {
		
		OrderJPA order = null;
		OrderKey key = new OrderKey(districtId, orderId, warehouseId);

		ib.debugOut("In queryOrderInstances - wid=" + warehouseId);
		ib.debugOut("In queryOrderInstances - oid=" + orderId);
		ib.debugOut("In queryOrderInstances - did=" + districtId);

		try {
			EntityGraph<OrderJPA> eg = em.createEntityGraph(OrderJPA.class);
			Map<String, Object> hints = new HashMap<>();
			
			if(fetchGraph){
				eg.addAttributeNodes("orderlines","OrderLineCount", "CarrierId");
				Subgraph<OrderlineJPA> orderlines = eg.addSubgraph("orderlines");
				orderlines.addAttributeNodes("olAmount", "olDeliveryDate");
				Subgraph<OrderJPA> orderlineOrder = orderlines.addSubgraph("order");
				orderlineOrder.addAttributeNodes("OrderLineCount", "CarrierId");
				hints.put("jakarta.persistence.fetchgraph", eg);
			}
			else{
				eg.addAttributeNodes("OrderLineCount", "CarrierId");
				hints.put("jakarta.persistence.loadgraph", eg);
			}
			
			order = em.find(OrderJPA.class, key, LockModeType.PESSIMISTIC_WRITE, hints);
			
			if(fetchGraph)
				verifyFetchGraph(order, em);
			else
				verifyLoadGraph(order,em);
		
			ib.debugOut("In " + _className + ": OrderJPA: queryOrderInstances: After em.find(OrderJPA.class, key): " +
					" WarehouseId = " + warehouseId + 
					" DistrictId = " + districtId +
					" OrderId = " + orderId + 
					" : threadId = " + Thread.currentThread().getId());
			
			if (order==null)	throw new ServletException("order not found: warehouseId="+warehouseId+", orderId="+orderId+" and districtId= "+districtId+", "+ib.getSTC()+ "");
		} catch (Exception t) {
			System.out.println("WarehouseId=" + warehouseId + " DistrictId=" + districtId +
					" OrderId=" + orderId);
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
						//Commented out sqlCode, sqlErrmc, sqlErrp, sqlErrd, sqlWarn, and sqlState
						//because the local variables were not used
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
	
	/**
	 * Verifies that the entity graph, which uses a fetch graph, worked as designed.
	 * 
	 * @param order The Order entity that was found using the entity graph.
	 * @param em The entity manager for the transaction, which was used to find the order.
	 * @throws ServletException If an attribute was loaded when it should not have been, or if 
	 * 							an attribute was not loaded when it should have been.
	 */
	private void verifyFetchGraph(OrderJPA order, EntityManager em) throws ServletException {
		PersistenceUnitUtil unitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
	
		verifyOrderPrimaryKeys(order, em);
		verifyOrderFetchGraph(order, em);
		
		if(unitUtil.isLoaded(order, "orderlines")){
			ib.debugOut(">>>orderlines were successfully loaded by the EntityGraph");
			
			verifyOrderlinePrimaryKeys(order, em);		
			verifyOrderlineFetchGraph(order, em);
		}
		else{
			ib.debugOut(">>>orderlines failed to be loaded by the EntityGraph");
			throw new ServletException("%%%%% ERROR: The entity graph failed to load the orderlines from OrderJPA" +
				ib.getSTC());
		}
	}
	
	/**
	 * Verifies the attributes belonging to the Order entity were loaded in accordance with the 
	 * entity graph defined to use a fetch graph.
	 * 
	 * @param order The Order entity that was found using the entity graph.
	 * @param em The entity manager for the transaction, which was used to find the order.
	 * @throws ServletException If an attribute was loaded when it should not have been, or if 
	 * 							an attribute was not loaded when it should have been.
	 */
	private void verifyOrderFetchGraph(OrderJPA order, EntityManager em) throws ServletException {
		PersistenceUnitUtil unitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
		
		//Attributes that should not have been loaded
		if(unitUtil.isLoaded(order, "CustomerId")){
			ib.debugOut(">>>CustomerId was loaded by the EntityGraph when it wasn't supposed to be");
			throw new ServletException("%%%%% ERROR: The entity graph failed to omit loading CustomerId from OrderJPA" +
				ib.getSTC());
		}
		else
			ib.debugOut(">>>CustomerId was successfully not loaded by the EntityGraph");
		
		if(unitUtil.isLoaded(order, "EntryDate")){
			ib.debugOut(">>>EntryDate was loaded by the EntityGraph when it wasn't supposed to be");
			throw new ServletException("%%%%% ERROR: The entity graph failed to omit loading EntryDate from OrderJPA" +
				ib.getSTC());
		}
		else
			ib.debugOut(">>>EntryDate was successfully not loaded by the EntityGraph");
		
		if(unitUtil.isLoaded(order, "AllLocal")){
			ib.debugOut(">>>AllLocal was loaded by the EntityGraph when it wasn't supposed to be");
			throw new ServletException("%%%%% ERROR: The entity graph failed to omit loading AllLocal from OrderJPA" +
				ib.getSTC());
		}
		else
			ib.debugOut(">>>AllLocal was successfully not loaded by the EntityGraph");
		
		//Attribute that should have been loaded
		if(unitUtil.isLoaded(order, "OrderLineCount"))
			ib.debugOut(">>>OrderLineCount was successfully loaded by the EntityGraph");
		else{
			ib.debugOut(">>>OrderLineCount failed to be loaded by the EntityGraph");
			throw new ServletException("%%%%% ERROR: The entity graph failed to load OrderLineCount from OrderJPA" +
			ib.getSTC());
		}
		
		if(unitUtil.isLoaded(order, "CarrierId"))
			ib.debugOut(">>>CarrierId was successfully loaded by the EntityGraph");
		else{
			ib.debugOut(">>>CarrierId failed to be loaded by the EntityGraph");
			throw new ServletException("%%%%% ERROR: The entity graph failed to load CarrierId from OrderJPA" +
			ib.getSTC());
		}
	}
	
	/**
	 * Verifies the attributes belonging to the Orderline entity were loaded in accordance with the 
	 * entity graph defined to use a fetch graph.
	 * 
	 * @param order The Order entity that was found using the entity graph.
	 * @param em The entity manager for the transaction, which was used to find the order.
	 * @throws ServletException If an attribute was loaded when it should not have been, or if 
	 * 							an attribute was not loaded when it should have been.
	 */
	private void verifyOrderlineFetchGraph(OrderJPA order, EntityManager em) throws ServletException {
		PersistenceUnitUtil unitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
		
		//Attributes that should not have been loaded
		if(unitUtil.isLoaded(order.getOrderlines().toArray()[0], "olItemId")){
			ib.debugOut(">>>olItemId was loaded by the EntityGraph when it wasn't supposed to be");
			throw new ServletException("%%%%% ERROR: The entity graph failed to omit loading olItemId from OrderlineJPA" +
					ib.getSTC());
		}
		else
			ib.debugOut(">>>olItemId was successfully not loaded by the EntityGraph");
		
		if(unitUtil.isLoaded(order.getOrderlines().toArray()[0], "olSupplyWarehouse")){
			ib.debugOut(">>>olSupplyWarehouse was loaded by the EntityGraph when it wasn't supposed to be");
			throw new ServletException("%%%%% ERROR: The entity graph failed to omit loading olSupplyWarehouse from OrderlineJPA" +
					ib.getSTC());
		}
		else
			ib.debugOut(">>>olSupplyWarehouse was successfully not loaded by the EntityGraph");
		
		if(unitUtil.isLoaded(order.getOrderlines().toArray()[0], "olQuantity")){
			ib.debugOut(">>>olQuantity was loaded by the EntityGraph when it wasn't supposed to be");
			throw new ServletException("%%%%% ERROR: The entity graph failed to omit loading olQuantity from OrderlineJPA" +
					ib.getSTC());
		}
		else
			ib.debugOut(">>>olQuantity was successfully not loaded by the EntityGraph");
		
		if(unitUtil.isLoaded(order.getOrderlines().toArray()[0], "olDistrictInfo")){
			ib.debugOut(">>>olDistrictInfo was loaded by the EntityGraph when it wasn't supposed to be");
			throw new ServletException("%%%%% ERROR: The entity graph failed to omit loading olDistrictInfo from OrderlineJPA" +
					ib.getSTC());
		}
		else
			ib.debugOut(">>>olDistrictInfo was successfully not loaded by the EntityGraph");
		
		//Attribute that should have been loaded
		if(unitUtil.isLoaded(order.getOrderlines().toArray()[0], "olAmount"))
			ib.debugOut(">>>olAmount was successfully loaded by the EntityGraph");
		else{
			ib.debugOut(">>>olAmount failed to be loaded by the EntityGraph");
			throw new ServletException("%%%%% ERROR: The entity graph failed to load olAmount from OrderlineJPA" +
			ib.getSTC());
		}
		
		if(unitUtil.isLoaded(order.getOrderlines().toArray()[0], "olDeliveryDate"))
			ib.debugOut(">>>olDeliveryDate was successfully loaded by the EntityGraph");
		else{
			ib.debugOut(">>>olDeliveryDate failed to be loaded by the EntityGraph");
			throw new ServletException("%%%%% ERROR: The entity graph failed to load olDeliveryDate from OrderlineJPA" +
			ib.getSTC());
		}	
	}
	
	/**
	 * Verifies that the entity graph, which uses a fetch graph, worked as designed.
	 * 
	 * @param order The Order entity that was found using the entity graph.
	 * @param em The entity manager for the transaction, which was used to find the order.
	 * @throws ServletException If an attribute was loaded when it should not have been, or if 
	 * 							an attribute was not loaded when it should have been.
	 */
	private void verifyLoadGraph(OrderJPA order, EntityManager em) throws ServletException{	
		verifyOrderPrimaryKeys(order, em);
		verifyOrderLoadGraph(order, em);
	}
	
	/**
	 * Verifies the attributes belonging to the Order entity were loaded in accordance with the 
	 * entity graph defined to use a load graph.
	 * 
	 * @param order The Order entity that was found using the entity graph.
	 * @param em The entity manager for the transaction, which was used to find the order.
	 * @throws ServletException If an attribute was loaded when it should not have been, or if 
	 * 							an attribute was not loaded when it should have been.
	 */
	private void verifyOrderLoadGraph(OrderJPA order, EntityManager em) throws ServletException {
		PersistenceUnitUtil unitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
		
		//Attributes that should not have been loaded
		if(unitUtil.isLoaded(order, "orderlines")){
			ib.debugOut(">>>orderlines were loaded by the EntityGraph when they weren't supposed to be");
			throw new ServletException("%%%%% ERROR: The entity graph failed to omit loading orderlines from OrderJPA" +
					ib.getSTC());
		}
		else{
			ib.debugOut(">>>orderlines were successfully not loaded by the EntityGraph");
		}
		
		//Attribute that should have been loaded
		if(unitUtil.isLoaded(order, "OrderLineCount"))
			ib.debugOut(">>>OrderLineCount was successfully loaded by the EntityGraph");
		else{
			ib.debugOut(">>>OrderLineCount failed to be loaded by the EntityGraph");
			throw new ServletException("%%%%% ERROR: The entity graph failed to load OrderLineCount from OrderJPA" +
			ib.getSTC());
		}
		
		if(unitUtil.isLoaded(order, "CarrierId"))
			ib.debugOut(">>>CarrierId was successfully loaded by the EntityGraph");
		else{
			ib.debugOut(">>>CarrierId failed to be loaded by the EntityGraph");
			throw new ServletException("%%%%% ERROR: The entity graph failed to load CarrierId from OrderJPA" +
			ib.getSTC());
		}
		
		if(unitUtil.isLoaded(order, "EntryDate"))
			ib.debugOut(">>>EntryDate was successfully loaded by the EntityGraph");
		else{
			ib.debugOut(">>>EntryDate failed to be loaded by the EntityGraph");
			throw new ServletException("%%%%% ERROR: The entity graph failed to load EntryDate from OrderJPA" +
				ib.getSTC());
		}
		
		if(unitUtil.isLoaded(order, "AllLocal"))
			ib.debugOut(">>>AllLocal was successfully loaded by the EntityGraph");
		else{
			ib.debugOut(">>>AllLocal failed to be loaded by the EntityGraph");
			throw new ServletException("%%%%% ERROR: The entity graph failed to load AllLocal from OrderJPA" +
				ib.getSTC());
		}

		if(unitUtil.isLoaded(order, "CustomerId"))
			ib.debugOut(">>>CustomerID was successfully loaded by the Entity Graph");
		else{
			ib.debugOut(">>>CustomerId failed to be loaded by the EntityGraph");
			throw new ServletException("%%%%% ERROR: The entity graph failed to load CustomerId from OrderJPA" +
				ib.getSTC());	
		}
	}

	/**
	 * Verifies the primary keys belonging to the Order entity were loaded.  Primary keys should 
	 * always be loaded when a table (entity) is accessed, regardless of the defined graph. For the Order entity
	 * the primary keys are the WarehouseId, DistrictId, and OrderId
	 * 
	 * @param order The Order entity that was found using the entity graph.
	 * @param em The entity manager for the transaction, which was used to find the order.
	 * @throws ServletException If an attribute was loaded when it should not have been, or if 
	 * 							an attribute was not loaded when it should have been.
	 */
 	private void verifyOrderPrimaryKeys(OrderJPA order, EntityManager em ) throws ServletException{
		PersistenceUnitUtil unitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
		
		if(unitUtil.isLoaded(order, "WarehouseId"))
			ib.debugOut(">>>WarehouseId was successfully loaded by the Entity Graph");
		else{
			ib.debugOut(">>>WarehouseId failed to be loaded by the EntityGraph");
			throw new ServletException("%%%%% ERROR: The entity graph failed to load WarehouseId from OrderJPA" +
				ib.getSTC());	
		}

		if(unitUtil.isLoaded(order, "DistrictId"))
			ib.debugOut(">>>DistrictId was successfully loaded by the Entity Graph");
		else{
			ib.debugOut(">>>DistrictId failed to be loaded by the EntityGraph");
			throw new ServletException("%%%%% ERROR: The entity graph failed to load DistrictId from OrderJPA" +
				ib.getSTC());	
		}

		if(unitUtil.isLoaded(order, "OrderId"))
			ib.debugOut(">>>OrderId was successfully loaded by the Entity Graph");
		else{
			ib.debugOut(">>>OrderId failed to be loaded by the EntityGraph");
			throw new ServletException("%%%%% ERROR: The entity graph failed to load OrderId from OrderJPA" +
				ib.getSTC());	
		}
	}

 	/**
	 * Verifies the primary keys belonging to the Orderline entity were loaded.  Primary keys should
	 * always be loaded when a table (entity) is accessed, regardless of the defined graph.  For the Orderline 
	 * entity the only primary key is olNumber. The other keys (WarehouseId, DistrictId, OrderId) are stored as 
	 * an Order object in the entity and thus not checked here.
	 * 
	 * @param order The Order entity that was found using the entity graph.
	 * @param em The entity manager for the transaction, which was used to find the order.
	 * @throws ServletException If an attribute was loaded when it should not have been, or if 
	 * 							an attribute was not loaded when it should have been.
	 */
	private void verifyOrderlinePrimaryKeys(OrderJPA order, EntityManager em) throws ServletException{
		PersistenceUnitUtil unitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
		
		if(unitUtil.isLoaded(order.getOrderlines().toArray()[0], "olNumber"))
			ib.debugOut(">>>olNuber was successfully loaded by the Entity Graph");
		else{
			ib.debugOut(">>>olNumber failed to be loaded by the EntityGraph");
			throw new ServletException("%%%%% ERROR: The entity graph failed to load olNumber from OrderJPA" +
				ib.getSTC());	
		}
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
