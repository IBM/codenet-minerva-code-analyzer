package orderarchive.lite;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.Query;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;

import java.util.Random;

import irwwbase.IRWWBase;
import irwwbase.MaxValues;

import jakarta.annotation.security.DeclareRoles;

import orderjpa.OrderJPA;
import orderjpa.OrderKey;
import orderlinejpa.OrderlineJPA;


/**
 * 
 * OrderArchive Software Design:
 * 
 * 
 * 
 * 
 * * OrderArchiveServlet.java is invoked by doPost() through OrderArchiveInputForm.html 
 * * OrderArchiveSerlvet.java doPost() method then calls either:
 * 
 * 
 * 1) webAutoGenInput() * (Business Logic for "AutoGeneration")

 * 2) webManualInput() * (Business Logic for "Manual")
 *      
 *                                     
 *                                            
 * 3) ** webRemoveAllDelivered() **
 * -  * Currently not available for public use **
 * 
 * 
 * 
 * --------------------------------------------------------------------------------------------------------------------------
 *                                            
 * Note: webException() * Deploys (Web Page) that Handles Errors in Web Application WebOrderArchiveLite
 *   for both webAutoGenInput() and webManualInput() and is located towards the bottom of the source
 *   code under the comment block "Program Crash"
 * 
 * 
 *
 */



// Make sure the @WebServlet information matches your particular information
@WebServlet(name="OrderArchiveServlet", urlPatterns={"/OrderArchiveServlet"})

// These are by convention and security preferences
@DeclareRoles("AllAuthenticatedUsersRole")
@ServletSecurity(value=@HttpConstraint(rolesAllowed={"AllAuthenticatedUsersRole"}))
public class OrderArchiveServlet extends HttpServlet {

	/////////////////////////////////////////////// Fields ///////////////////////////////////////////////

	private static final long serialVersionUID = 1L;
	// Must match in your XML
	@PersistenceUnit(unitName = "WebOrderArchiveLite")
	// Need to create an entity manager later to make transactions and perform queries to DB2
	EntityManagerFactory emf;


	/////////////////////////////////////////////// Constructors ///////////////////////////////////////////////
	/**
	 * Calls super-constructor from HttpServlet#HttpServlet()
	 */
	public OrderArchiveServlet() {
		super();
	}

	/////////////////////////////////////////////// doGet() and doPost() ///////////////////////////////////////////////

	/**
	 * doGet() does nothing because the HTML invokes the servlet through doPost()
	 * 
	 * doGet() is simply here for ERWW Lite software design consistency
	 * 
	 * servlet begins at next method, doPost()
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}



	/**
	 * OrderArchiveInputForm.html invokes this method doPost() in OrderArchiveInputForm.html
	 * 
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		IRWWBase ib = new IRWWBase();
		boolean throwable = false;
		String message = "";
		Exception passedException = null;
		Throwable passedThrowable = null;

		String theButton = request.getParameter("command");

		ib.debugOut("In doGet() method. Directly before AutoGeneration/Manual/RemoveAllDelivered decision.");

		if (theButton.trim().equalsIgnoreCase("AutoGeneration")) {

			try {
				this.webAutoGenInput(request,response);	
			}
			// if this catch block is entered, something very unexpected went wrong
			// this catch block is the last-ditch effort to prevent program crash
			catch (Throwable e) {
				throwable = true;
				message = "A WebOrderArchiveLite throwable exception occurred in doPost() method tring to call webAutoGenInput.";
				passedThrowable = e;
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				e.printStackTrace();
				System.out.println(message);
			}

		}

		else if (theButton.trim().equalsIgnoreCase("Manual")) {

			try {
				this.webManualInput(request,response);
			}
			// if this catch block is entered, something very unexpected went wrong
			// this catch block is the last-ditch effort to prevent program crash
			catch (Throwable e) {
				throwable = true;
				message = "A WebOrderArchiveLite throwable exception occurred in doPost() method trying to call webManualInput().";
				passedThrowable = e;
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				e.printStackTrace();
				System.out.println(message);
			}

		}

		else {

			try {
				this.webRemoveAllDelivered(request,response);
			}
			// if this catch block is entered, something very unexpected went wrong
			// this catch block is the last-ditch effort to prevent program crash
			catch (Throwable e) {
				throwable = true;
				message = "A WebOrderArchiveLite throwable exception occurred in doPost() method trying to call webRemoveAllDelivered().";
				passedThrowable = e;
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				e.printStackTrace();
				System.out.println(message);
			}

		}

	}


	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////// AUTOGENERATION CODE ////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////



	/**
	 * webAutoGenInput() is called by doPost() and contains 
	 * the business logic for the AutoGeneration button
	 * 
	 * webAutoGenInput() * (Business Logic)     // first method of the class not counting doPost() and doGet()
	 * 
	 */
	public void webAutoGenInput(HttpServletRequest request,
			HttpServletResponse response) {

		UserTransaction ut = null;
		IRWWBase ib = new IRWWBase();
		boolean throwable = false;
		String message = "";
		Exception passedException = null;
		Throwable passedThrowable = null;

		// outer try block to cover entire AutoGenInput() code 
		// allows for rollback() if anything goes wrong in transaction 
		try {

			short randomWarehouseId = 0;
			short randomDistrictId = 0;

			// - use (byte)0 for small tables
			// - use (byte)1 for medium tables
			// - use (byte)2 for large tables
			byte database = ib.dbSize();

			// For debugging loose ends with Orderline Table
			int numberOrderLines;
			ib.debugOut("Reached entry-point of BUSINESS LOGIC in webAutoGenInput().");

			EntityManager em = null;

			try {
				InitialContext ctx = new InitialContext();
				ib.debugOut("Before lookup of UserTransaction");
				ut = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
				ib.debugOut("After lookup of UserTransaction");
			} 
			catch (NamingException e) {
				message = "A WebOrderArchiveLite Naming exception occurred in webAutoGenInput().";
				passedException = e;
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				e.printStackTrace();
				System.out.println(message);
				return;
			}

			try {
				em = emf.createEntityManager(); 
			}
			catch (Exception e) {
				message = "A WebOrderArchiveLite general exception occurred trying to create entity manager in webAutoGenInput().";
				passedException = e;
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				e.printStackTrace();
				System.out.println(message);
				return;

			}
			try {
				ut.begin();
			}
			catch (Exception e) {
				message = "A WebOrderArchiveLite general exception trying to begin transaction in webAutoGenInput().";
				passedException = e;
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				e.printStackTrace();
				System.out.println(message);
				return;
			}
			try {
				em.joinTransaction(); 
			}
			catch (Exception e) {
				message = "A WebOrderArchiveLite general exception trying to join transaction in webAutoGenInput().";
				passedException = e;
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				e.printStackTrace();
				System.out.println(message);
				return;
			}
			Query queryMinOrderId = null;


			// According to Java API, nextInt(param) goes from 0 ---> param - 1 therefore need to increment by 1
			// or else random number generator will start 0
			Random rand = new Random();
			randomWarehouseId = (short) rand.nextInt(MaxValues.warehouseId(database));
			randomWarehouseId++;
			randomDistrictId = (short) rand.nextInt(MaxValues.districtId(database));
			randomDistrictId++;
			ib.debugOut("The randomly-generated Warehouse ID = " + randomWarehouseId);
			ib.debugOut("The randomly-generated District ID = " + randomDistrictId);


			// "findMinOrderId" is a query found in OrderJPA
			try {
				queryMinOrderId = em.createNamedQuery("findMinOrderId"); //
				queryMinOrderId.setParameter("warehouseId", randomWarehouseId);//
				queryMinOrderId.setParameter("districtId", randomDistrictId);//
			}
			catch (Exception e) {
				message = "A WebOrderArchiveLite general exception occurred trying to query database in webAutoGenInput().";
				passedException = e;
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				e.printStackTrace();
				System.out.println(message);
				return;
			}

			int orderId = 0;
			OrderJPA oldestOrder = null;

			try {
				orderId = (int)queryMinOrderId.getSingleResult(); 
				ib.debugOut("OrderId = " + orderId); 
				OrderKey oldestOrderKey = new OrderKey(randomDistrictId,orderId,randomWarehouseId);
				oldestOrder = em.find(OrderJPA.class, oldestOrderKey); 
			}
			catch (Exception e) {
				message = "A WebOrderArchiveLite general exception occurred trying to get result from query in webAutoGenInput().";
				passedException = e;
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				e.printStackTrace();
				System.out.println(message);
				return;	
			}

			if (oldestOrder == null) {			
				PrintWriter out = null;	
				response.setContentType("text/html");  

				try {
					out = response.getWriter();
				}
				catch (IOException e) {
					message = "A WebOrderArchiveLite IO Exception occurred trying to getWriter() in webAutoGenInput().";
					passedException = e;
					this.webException(message,passedException,passedThrowable,throwable,request,response);
					e.printStackTrace();
					System.out.println(message);
					return;
				}

				out.println("<html>");
				out.println("<center><H1>Order Archive Lite Results Form</H1></center>");
				out.println("<body bgcolor=\"#f8f7cd\">");
				out.println("<h3 center>Status: FAILED</h3> ");
				out.println("<h3 center>Message:<h3/><h4>An empty warehouse/district combination was randomly-generated and currently contains no orders.  Below is the information for the empty warehouse/district combination:</h4> ");

				out.println("<TABLE border=\"8\" bgcolor=\"#cccccc\" bordercolor=\"#FFCC99\" >");
				out.println("<tr><th></th>");
				out.println("<tr><th>Warehouse ID:</th>");

				out.println("<td>" + randomWarehouseId + "</td></tr>");

				out.println("<tr><th>District ID:</th>");
				out.println("<td>" + randomDistrictId + "</td>");	

				out.println("<tr><th>Order ID:</th>");
				out.println("<td>" + "-" + "</td>");	

				out.println("<br></br>");
				out.println("<br></br>");

				out.println( "</table>");    

				out.println("<br></br>");
				out.println("<br></br>");
				out.println("<h3 center>Exception:</h3>");
				out.println("<h3 center>Stack:</h3>");
				out.println( "</body>");
				out.println( "</html>"); 
				out.flush();	
				return;

			}

			else {

				ib.debugOut("The order to be removed from the archive was found successfully. Here is its information:");
				ib.debugOut("Warehouse ID: " + oldestOrder.getWarehouseId());
				ib.debugOut("District ID: " + oldestOrder.getDistrictId());
				ib.debugOut("OrderID: " + oldestOrder.getOrderId());
				
				numberOrderLines = oldestOrder.getOrderLineCount().intValue();

				orderId = oldestOrder.getOrderId();              
				short districtId = oldestOrder.getDistrictId();                
				short warehouseId = oldestOrder.getWarehouseId();

				try {
					em.remove(oldestOrder); 
				}
				catch (Exception e) {
					message = "A WebOrderArchiveLite general exception occurred trying to remove oldest order in webAutoGenInput().";
					passedException = e;
					this.webException(message,passedException,passedThrowable,throwable,request,response);
					e.printStackTrace();
					System.out.println(message);
					return;

				}


				ib.debugOut("*The above order has been removed from the order archive.*");
				            

				ib.debugOut("* There are " + numberOrderLines + " order lines associated with this order to be deleted.");
				ib.debugOut("The associated Order Lines have the following information...");
				Query orderlineQuery = null;
				orderlineQuery = em.createNamedQuery("findOrderLinesWithSpecificOrderId"); //

				orderlineQuery.setParameter("orderId", orderId);
				orderlineQuery.setParameter("districtId", districtId);
				orderlineQuery.setParameter("warehouseId", warehouseId);
				Object var = orderlineQuery.getResultList();
				List<OrderlineJPA> resultSet = new ArrayList<OrderlineJPA>();
				if (var instanceof List) {
					for (int i=0;i<((List<?>)var).size();i++) {
						Object item = ((List<?>)var).get(i);
						if (item instanceof OrderlineJPA) {
							resultSet.add((OrderlineJPA)item);
						}
					}
				}
				//Added above code to remove the type safety warning
				//List<OrderlineJPA> resultSet = orderlineQuery.getResultList();         
				
				// Delete all associated order lines
				for (int i = 0; i < resultSet.size(); i++) {
					ib.debugOut("WarehouseID = " + warehouseId);
					ib.debugOut("DistrictID = " + districtId); 
					ib.debugOut("OrderID = " + orderId); 
					ib.debugOut("Number = " + resultSet.get(i).getOlNumber());
					em.remove(resultSet.get(i) ); //
				}


				try {
					ut.commit(); 
				}
				catch (Exception e) {
					message = "A WebOrderArchiveLite general exception trying to commit the transaction in webAutoGenInput().";
					passedException = e;
					this.webException(message,passedException,passedThrowable,throwable,request,response);
					e.printStackTrace();
					System.out.println(message);
					return;
				}

				// Dispatch AutoGeneration Successful Results 

				this.webAutoGenResults(oldestOrder,request,response); 


				try {
					em.close(); 
				}
				catch (Exception e) {
					message = "Exception occurred in WebOrderArchive trying to close entity manager in webAutoGenResults().";
					passedException = e;
					this.webException(message,passedException,passedThrowable,throwable,request,response);
					e.printStackTrace();
					System.out.println(message);
					return;
				}

			}

			// end long try block that began at beginning of method
		}

		// rollback protection to be executed no matter what after end try block
		finally {
			try {
				if(ut != null) {
					if (ut.getStatus() != Status.STATUS_NO_TRANSACTION ) {
						ut.rollback();
					}
				}
			}
			catch (Exception e) {
				message = "A WebOrderArchiveLite general exception occurred trying to rollback in webAutoGenInput().";
				passedException = e;
				e.printStackTrace();
				System.out.println(message);
				return;
			}


		}

	}



	/*
	 * A method that will dispatch a results page for the AutoGeneration piece of Order Archive
	 * 
	 * webAutoGenResults() * Deploys (Web Page) 
	 */
	public void webAutoGenResults(OrderJPA theOb, HttpServletRequest request,
			HttpServletResponse response) {
		
		boolean throwable = false;
		String message = "";
		Exception passedException = null;
		Throwable passedThrowable = null;

		PrintWriter out = null;	
		response.setContentType("text/html");  

		try {
			out = response.getWriter(); 
		}
		catch (IOException e) {
			message = "A WebOrderArchiveLite IO exception occurred trying to getWriter() in webAutoGenResults().";
			passedException = e;
			this.webException(message,passedException,passedThrowable,throwable,request,response);
			e.printStackTrace();
			System.out.println(message);
			return;
		}


		out.println("<html>");
		out.println("<center><H1>Order Archive Lite Results Form</H1></center>");    
		out.println("<body bgcolor=\"#f8f7cd\">");
		out.println("<h3 center>Status: SUCCESSFUL</h3> ");
		out.println("<h3 center>Message:</h3>");
		out.println("<h2 center>The following order has been removed:</h2> ");
		out.println("</head> ");
		out.println("<TABLE border=\"8\" bgcolor=\"#cccccc\" bordercolor=\"#FFCC99\" >");  	

		out.println("<tr><th></th>");
		out.println("<tr><th>Warehouse ID:</th>");
		out.println("<td>" + theOb.getWarehouseId()+ "</td></tr>");	
		out.println("<tr><th>District ID:</th>");
		out.println("<td>" + theOb.getDistrictId()+ "</td>");
		out.println("<tr><th>Order ID:</th>");
		out.println("<td>" + theOb.getOrderId()+ "</td>");
		out.println( "</table>");   
		out.println("<h3 center>Exception:</h3>");
		out.println("<h3 center>Stack:</h3>");
		out.println( "</body>");
		out.println( "</html>"); 
		out.flush();	
	}



	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////// MANUAL CODE ////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////



	/*
	 * webManualInput() is called by doPost() if the user wants to do a manual
	 * order archive transaction
	 * 
	 * webManualInput() * (Business Logic)
	 */
	private void webManualInput(HttpServletRequest request,
			HttpServletResponse response) {

		UserTransaction ut = null;
		IRWWBase ib = new IRWWBase();
		boolean throwable = false;
		String message = "";
		Exception passedException = null;
		Throwable passedThrowable = null;


		// outer try block to cover entire webManualInput() code 
		// allows for rollback() if anything goes wrong in transaction 
		try {

			// - use (byte)0 for small tables
			// - use (byte)1 for medium tables
			// - use (byte)2 for large tables
			byte database = ib.dbSize();

			// counter purposes
			int i;

			ib.debugOut("Reached entry-point of BUSINESS LOGIC in webManualInput()");

			String manualErrorPage = "webManualError.html";

			OrderArchiveInput userInput = new OrderArchiveInput();


			// Handles if manual input for Warehouse ID was empty (Input 1 Check)
			try {
				userInput.setWarehouseId(Short.valueOf(request 
						.getParameter("warehouseId")));
				ib.debugOut("User WarehouseID Input = " + Short.valueOf(request.getParameter("warehouseId")));
			}
			catch(NumberFormatException e) {
				message = "A user entered null as a value for warehouse id to delete in webManualInput() for WebOrderArchiveLite.";
				this.webManualError(request,response,manualErrorPage);
				System.out.println(message);
				return;
			}

			// Handles if manual input for District ID was empty (Input 2 Check)
			try {
				userInput.setDistrictId(Short.valueOf(request
						.getParameter("districtId")));
				ib.debugOut("User DistrictID Input = " + Short.valueOf(request.getParameter("districtId")));
			}
			catch(NumberFormatException e) {
				message = "A user entered null as a value for district id to delete in webManualInput() for WebOrderArchiveLite.";
				this.webManualError(request,response,manualErrorPage);
				System.out.println(message);
				return;

			}

			// Handles if manual input for number of orders to delete was empty (Input 3 Check)
			try {
				userInput.setNumOrders(Short.valueOf(request
						.getParameter("numOrders")));
				ib.debugOut("User NumOrdersToDelete Input = " + Short.valueOf(request.getParameter("numOrders")));
			}
			catch(NumberFormatException e) {
				message = "A user entered null as a value for num orders to delete in webManualInput() for WebOrderArchiveLite.";
				this.webManualError(request,response,manualErrorPage);
				System.out.println(message);
				return;

			}

			// set user input values according to the manual values the user inputted
			short warehouseId = userInput.getWarehouseId();
			short districtId = userInput.getDistrictId();
			int numOrders = userInput.getNumOrders();

			// set max values according to the database to check the user values are valid
			// "numOrders" is not a value set by database, that max value is set by us 
			// rowToStart, MaxNumOrders will be used to query database for first 10 rows
			// "rowToStart" = 0 is row 1 as per the API

			short maxWarehouseId = (short)MaxValues.warehouseId(database);
			short maxDistrictId = (short)MaxValues.districtId(database);
			int rowToStart = 0;
			int maxNumOrders = 10;

			// This value is very important and is decided by the user. It is how many 
			// orders the user wants to delete
			int numReqOrdersToDelete = userInput.getNumOrders();

			// Validate the user input is all valid                 
			if (     ( (warehouseId  > 0) && (warehouseId <= maxWarehouseId) )     &&     ( (districtId > 0) && (districtId <= maxDistrictId) )    &&    ( (numOrders > 0) && (numOrders <= maxNumOrders) )      ) {
				ib.debugOut("All inputs are valid");
			}
			else {
				ib.debugOut("The user inputted invalid values for the manual path.");
				this.webManualError(request,response,manualErrorPage);
				return;
			}

			EntityManager em = null;
			try {
				InitialContext ctx = new InitialContext();
				ib.debugOut("Before lookup of UserTransaction");
				ut = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
				ib.debugOut("After lookup of UserTransaction");
			} catch (NamingException e) {
				message = "A WebOrderArchiveLite NamingException occurred looking up initial context in webManualInput().";
				passedException = e;
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				e.printStackTrace();
				System.out.println(message);
				return;
			}

			em = emf.createEntityManager();
			try {
				ut.begin();
			}
			catch (Exception e) {
				message = "A WebOrderArchiveLite general exception occurred trying to begin a transaction in webManualInput().";
				passedException = e;
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				e.printStackTrace();
				System.out.println(message);
				return;
			}

			try {
				em.joinTransaction();
			}
			catch (Exception e) {
				message = "A WebOrderArchiveLite general exception occurred trying to join transaction in webManualInput().";
				passedException = e;
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				e.printStackTrace();
				System.out.println(message);
				return;
			}
			
			ib.debugOut("After Begin Transaction in Manual Code");

			Query queryOrdersForUniqueWDComb = null;

			// query located in OrderJPA.java
			try {
				queryOrdersForUniqueWDComb = em.createNamedQuery("findOrdersForUniqueWarehouseDistrictCombination");
				queryOrdersForUniqueWDComb.setParameter("warehouseId", warehouseId);
				queryOrdersForUniqueWDComb.setParameter("districtId", districtId);
			}
			catch (Exception e) {
				message = "A WebOrderArchiveLite general exception occurred trying to query database in webManualInput().";
				passedException = e;
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				e.printStackTrace();
				System.out.println(message);
				return;
			}

			// return the first 10 rows from the query
			// (oldest 10 delivered orders for the specified warehouse/district combination)

			queryOrdersForUniqueWDComb.setFirstResult(rowToStart);
			queryOrdersForUniqueWDComb.setMaxResults(numReqOrdersToDelete);

			/* Replace this code with the below code to remove type safety warning
			List<OrderJPA> uniqueWarehouseDistrictComboList = null;
			try {
				uniqueWarehouseDistrictComboList = queryOrdersForUniqueWDComb.getResultList();
			}
			*/
			List<OrderJPA> uniqueWarehouseDistrictComboList = new ArrayList<OrderJPA>();
			try {
				Object var = queryOrdersForUniqueWDComb.getResultList();
				if (var instanceof List) {
					for (int x=0; x<((List<?>)var).size();x++) {
						Object item = ((List<?>)var).get(x);
						if (item instanceof OrderJPA) {
							uniqueWarehouseDistrictComboList.add((OrderJPA)item);
						}
					}
				}
			}
			catch (Exception e) {
				message = "A WebOrderArchiveLite general exception occurred trying query database in webManualInput()";
				passedException = e;
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				e.printStackTrace();
				System.out.println(message);
				return;
			}
			int sizeWareDistList = uniqueWarehouseDistrictComboList.size();

			ib.debugOut("The number of rows returned from DB2 for this manual request is " + sizeWareDistList);

			// if the user asks to delete more delivered orders than there are available,
			// we will not delete any and inform the user of the failure by passing through
			// boolean nearEmptyMessage as true to webManualResults()
			boolean nearEmptyMessage = false; 

			if (numReqOrdersToDelete > sizeWareDistList) {
				nearEmptyMessage = true;
				this.webManualResults(uniqueWarehouseDistrictComboList,numReqOrdersToDelete,nearEmptyMessage,request,response);
				return;
			}

			ib.debugOut("Size of manual list = " +  uniqueWarehouseDistrictComboList.size() );

			// We do not want null orders to delete and we do not want more than 10 orders to delete 
			ib.debugOut("There are " + uniqueWarehouseDistrictComboList.size() + " manual orders to delete.");

			// Loop "numReqOrdersToDelete" times a variable that was determined by the user
			for (i = 0; i < numReqOrdersToDelete; i++) {
				ib.debugOut("Manual Order " + (i + 1) + " Information");
				ib.debugOut("WarehouseID = " + uniqueWarehouseDistrictComboList.get(i).getWarehouseId() );
				ib.debugOut("DistrictID = " + uniqueWarehouseDistrictComboList.get(i).getDistrictId() );
				ib.debugOut("OrderID = " + uniqueWarehouseDistrictComboList.get(i).getOrderId() );
				try {
					em.remove(uniqueWarehouseDistrictComboList.get(i));
				}
				catch (Exception e) {
					message = "A WebOrderArchiveLite general exception occurred trying to remove orders in webManualInput().";
					passedException = e;
					this.webException(message,passedException,passedThrowable,throwable,request,response);
					e.printStackTrace();
					System.out.println(message);
					return;

				}
				ib.debugOut("*The above manual order have been removed from the archive.*");
			}

			// Remove All Associated Order Lines

			int orderId;

			ib.debugOut("The associated Order Lines have the following information...");

			for (i = 0; i < numReqOrdersToDelete; i++) {

				orderId = uniqueWarehouseDistrictComboList.get(i).getOrderId();
				districtId = uniqueWarehouseDistrictComboList.get(i).getDistrictId();
				warehouseId = uniqueWarehouseDistrictComboList.get(i).getWarehouseId();

				ib.debugOut("Deleted Manual Order " + (i + 1) + " has the following Orderline information: ");

				Query orderlineQuery = null;

				try {
					
					orderlineQuery = em.createNamedQuery("findOrderLinesWithSpecificOrderId");
					
					orderlineQuery.setParameter("orderId", orderId);
					orderlineQuery.setParameter("districtId", districtId);
					orderlineQuery.setParameter("warehouseId", warehouseId);
				}
				catch (Exception e) {
					message = "A WebOrderArchiveLite general exception occurred trying query database webManualInput().";
					passedException = e;
					this.webException(message,passedException,passedThrowable,throwable,request,response);
					e.printStackTrace();
					System.out.println(message);
					return;

				}
				/*Replace this code with the below code to remove type safety warning
				List<OrderlineJPA> resultSet = null;
				try {
					resultSet = orderlineQuery.getResultList();
				}
				*/
				List<OrderlineJPA> resultSet = new ArrayList<OrderlineJPA>();
				try {
					Object var = orderlineQuery.getResultList();
					if (var instanceof List) {
						for (int x=0;x<((List<?>)var).size();x++) {
							Object item = ((List<?>)var).get(x);
							if (item instanceof OrderlineJPA) {
								resultSet.add((OrderlineJPA)item);
							}
						}
					}
				}
				catch (Exception e) {
					message = "A WebOrderArchiveLite general exception occurred trying to get result list of a query in webManualInput().";
					passedException = e;
					this.webException(message,passedException,passedThrowable,throwable,request,response);
					e.printStackTrace();
					System.out.println(message);
					return;

				}

				// for one manual order remove all order lines
				for (int j = 0; j < resultSet.size(); j++) {
					ib.debugOut("WarehouseID = " + resultSet.get(i).getOrder().getWarehouseId());
					ib.debugOut("DistrictID = " + resultSet.get(i).getOrder().getDistrictId());
					ib.debugOut("OrderID = " + resultSet.get(i).getOrder().getOrderId());
					ib.debugOut("Number = " + resultSet.get(i).getOlNumber());
					try {
						em.remove(resultSet.get(j));
					}
					catch (Exception e) {
						message = "A WebOrderArchiveLite general exception occurred trying to remove order lines in webManualInput().";
						passedException = e;
						this.webException(message,passedException,passedThrowable,throwable,request,response);
						e.printStackTrace();
						System.out.println(message);
						return;
					}
				}

			}

			// Code to commit the manual transaction
			try {
				ut.commit();
			}
			catch(Exception e) {
				message = "A WebOrderArchiveLite general exception occurred trying to commit transaction in webManualInput().";
				passedException = e;
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				e.printStackTrace();
				System.out.println(message);
				return;
			}

			// Dispatch Manual Successful Results 

			this.webManualResults(uniqueWarehouseDistrictComboList,numReqOrdersToDelete,nearEmptyMessage,request,response);


			try {
				em.close();
			}
			catch (Exception e) {
				message = "A WebOrderArchiveLite general exception occurred trying to close entity manager in webManualInput().";
				passedException = e;
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				e.printStackTrace();
				System.out.println(message);
				return;
			}


			// end method-long try block for rollback() protection
		}

		// rollback protection to be executed no matter what
		finally {
			try {
				if (ut != null ) {
					if (ut.getStatus() != Status.STATUS_NO_TRANSACTION ) {
						ut.rollback();
					}
				}
			}
			catch (Exception e) {
				message = "A WebOrderArchiveLite general exception trying to rollback in webManualInput().";
				passedException = e;
				e.printStackTrace();
				System.out.println(message);
				return;
			}


		}

	}



	/*
	 * A method that will dispatch a results page for manual Order Archive
	 * 
	 * webManualResults() * Deploys (Web Page)
	 */
	public void webManualResults(List<OrderJPA> warehouseDistrictList, int numOrdersToDelete, 
			boolean nearEmpty, HttpServletRequest request,HttpServletResponse response) {

		boolean throwable = false;
		String message = "";
		Exception passedException = null;
		Throwable passedThrowable = null;
		
		int deliveredOrdersLeft = warehouseDistrictList.size();

		PrintWriter out = null;	
		response.setContentType("text/html");  

		try {
			out = response.getWriter();
		}
		catch (IOException e) {
			message = "A WebOrderArchiveLite IO exception occurred trying to getWriter() in webManualResults().";
			passedException = e;
			this.webException(message,passedException,passedThrowable,throwable,request,response);
			e.printStackTrace();
			System.out.println(message);
			return;	
		}   

		out.println("<html>");

		// logic passed from webManualInput() to handle if the amount of orders the user 
		// requested to delete was more than existed for a WH/Dist Combination

		if ( (nearEmpty == true) ) {
			out.println("<center><H1>Order Archive Lite Results Form</H1></center>"); 
			out.println("<body bgcolor=\"#f8f7cd\">");
			out.println("<h3 center>Status: FAILED</h3> ");
			out.println("<h3 center>Message: </32>" + "<h4>User not permitted to remove " + numOrdersToDelete + 
					" delivered orders.  Only " + deliveredOrdersLeft + " delivered orders are left"
					+ " for this particular warehouse/district combination.</h4> ");
			out.println("<h3 center>Exception:</h3>");
			out.println("<h3 center>Stack:</h3>");
		}

		else {
			out.println("<center><H1>Order Archive Lite Results Form</H1></center>");
			out.println("<body bgcolor=\"#f8f7cd\">");
			out.println("<h3 center>Status: SUCCESSFUL</h3> ");
			out.println("<h3 center>Message:</h3>");
			out.println("<h2 center>The following order(s) have been removed:</h2> ");


			out.println("</head> ");
			out.println("<tr><th></th>");

			for (int omega = 0; omega < numOrdersToDelete; omega++) {

				out.println("<TABLE border=\"8\" bgcolor=\"#cccccc\" bordercolor=\"#FFCC99\" >");  	
				out.println("<tr><th>Warehouse ID:</th>");
				out.println("<td>" + warehouseDistrictList.get(omega).getWarehouseId()+ "</td></tr>");

				out.println("<tr><th>District ID:</th>");
				out.println("<td>" + warehouseDistrictList.get(omega).getDistrictId()+ "</td>");	

				out.println("<tr><th>Order ID:</th>");
				out.println("<td>" + warehouseDistrictList.get(omega).getOrderId()+ "</td>");	

				out.println("<br></br>");
				out.println("<br></br>");

				out.println( "</table>");       

			}
			out.println("<br></br>");
			out.println("<h3 center>Exception:</h3>");
			out.println("<h3 center>Stack:</h3>");

		}


		out.println("<br></br>");
		out.println("<br></br>");
		out.println( "</body>");
		out.println( "</html>"); 
		out.flush();	
	}
	
	

	/*
	 * A method that will dispatch the Manual Error HTML page
	 * 
	 * webManualError() * Deploys (Web Page) that Handles Manual Invalid User Input
	 */
	public void webManualError(HttpServletRequest request,HttpServletResponse response, String manualErrorPage) {
		
		boolean throwable = false;
		String message = "";
		Exception passedException = null;
		Throwable passedThrowable = null;

		RequestDispatcher dispatch = request.getRequestDispatcher(manualErrorPage);

		try {
			dispatch.forward(request, response);
		}
		catch (Exception e) {
			message = "A WebOrderArchiveLite general exception occurred trying to dispatch manual invalid input web page.";
			passedException = e;
			this.webException(message,passedException,passedThrowable,throwable,request,response);
			e.printStackTrace();
			System.out.println(message);
			return;	
		}
	}



	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////// Remove All Delivered ////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////



	/*
	 * ********* DO NOT USE REMOVEALLDELIVERED *************
	 * ********* CURRENTLY UNDER DEVELOPMENT ***************
	 * 
	 * webRemoveAllDelivered() is called by doPost() if the user wants to remove
	 * all delivered orders in the order archive
	 * 
	 * webRemoveAllDelivered() * (Business Logic)
	 */
	private void webRemoveAllDelivered(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		
		
		boolean throwable = false;
		String message = "Sorry, RemoveAllDelivered button is not available for public use.";
		Exception passedException = null;
		Throwable passedThrowable = null;
		
		
		this.webException(message,passedException,passedThrowable,throwable,request,response);

		/*IRWWBase ib = new IRWWBase();

		// - use (byte)0 for small tables
		// - use (byte)1 for medium tables
		// - use (byte)2 for large tables
		// ********* byte database = ib.dbSize();



		// counter purposes
		int i;

		ib.debugOut("Reached entry-point of BUSINESS LOGIC in removeAllDelivered()");

		UserTransaction ut = null;
		EntityManager em = null;
		try {
			InitialContext ctx = new InitialContext();
			ib.debugOut("Before lookup of UserTransaction");
			ut = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
			ib.debugOut("After lookup of UserTransaction");
		} catch (NamingException e1) {
			e1.printStackTrace();
			throw new ServletException("Exception getting userTransaction", e1);
		}

		em = emf.createEntityManager();
		try {
			ut.begin();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("An exception occurred trying to begin the transaction in Remove All Delivered.");
			System.out.println("Status: FAILED");
			try {
				ut.rollback();
			}
			catch (Exception e2) {
				e2.printStackTrace();
				System.out.println("And an exception occurred rolling back.");
				System.out.println("Status: FAILED");
			}
			return;
		}
		em.joinTransaction();
		ib.debugOut("After Begin Transaction in Remove All Delivered");

		// ******** Query queryAllDelivered = null;

		///////////// query located in OrderJPA.java
		///////////queryAllDelivered = em.createNamedQuery("findAllDeliveredOrders");


		////////////////////make sure we grabbing only delivered orders///////////////////////////////////
		Query queryAllDelivered2 = null;

		// query located in OrderJPA.java
		queryAllDelivered2 = em.createNamedQuery("findAllDeliveredOrders");

		// return all delivered orders from the query

		List<OrderJPA> allDeliveredOrders2 = queryAllDelivered2.getResultList();
		int sizeDeliveredList2 = allDeliveredOrders2.size();
		System.out.println("Size of delivered orders list Begin of Run = " + sizeDeliveredList2);
		////////////////////make sure we grabbing only delivered orders///////////////////////////////////

		int a = 0;
		if (a ==0) {
			return;
		}



		ib.debugOut("The number of rows returned from DB2 for Remove All Delivered " + sizeDeliveredList2);

		ib.debugOut("Total number of delivered orders to remove = " +  sizeDeliveredList2 );

		// Remove All Delivered Orders
		for (i = 0; i < sizeDeliveredList2; i++) {
			ib.debugOut("Delivered Order " + (i + 1) + " Information");
			ib.debugOut("WarehouseID = " + allDeliveredOrders2.get(i).getWarehouseId() );
			ib.debugOut("DistrictID = " + allDeliveredOrders2.get(i).getDistrictId() );
			ib.debugOut("OrderID = " + allDeliveredOrders2.get(i).getOrderId() );
			try {
				em.remove(allDeliveredOrders2.get(i));
				ib.debugOut("*The above delivered order have been deleted from the archive.*");
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("Exception trying to remove deliverd orders in webRemoveAllDelivered().");
				System.out.println("Status: FAILED");
				try {
					ut.rollback();
				}
				catch (Exception e2) {
					e2.printStackTrace();
					System.out.println("Exception rolling back.");
					System.out.println("Status: FAILED");
				}
			}
		}

		int orderId;
		short districtId;
		short warehouseId;

		ib.debugOut("The associated Order Lines have the following information...");

		for (i = 0; i < sizeDeliveredList2; i++) {

			orderId = allDeliveredOrders2.get(i).getOrderId();
			districtId = allDeliveredOrders2.get(i).getDistrictId();
			warehouseId = allDeliveredOrders2.get(i).getWarehouseId();

			ib.debugOut("Deleted Manual Order " + (i + 1) + " has the following Orderline information: ");

			Query orderlineQuery = null;
			orderlineQuery = em.createNamedQuery("findOrderLinesWithSpecificOrderId");
			orderlineQuery.setParameter("olOrderId", orderId);
			orderlineQuery.setParameter("olDistrictId", districtId);
			orderlineQuery.setParameter("olWarehouseId", warehouseId);
			List<OrderlineJPA> resultSet = orderlineQuery.getResultList();

			// for one manual order remove all order lines
			for (int j = 0; j < resultSet.size(); j++) {
				ib.debugOut("WarehouseID = " + resultSet.get(j).getOlWarehouseId());
				ib.debugOut("DistrictID = " + resultSet.get(j).getOlDistrictId());
				ib.debugOut("OrderID = " + resultSet.get(j).getOlOrderId());
				ib.debugOut("Number = " + resultSet.get(j).getOlNumber());
				try {
					em.remove(resultSet.get(j));
				}
				catch (Exception e) {
					System.out.println("Status: FAILED");
					e.printStackTrace();
					System.out.println("Exception trying to remove order lines in webManualInput().");
					try {
						System.out.println("Status: FAILED");
						ut.rollback();
					}
					catch (Exception e2) {
						System.out.println("Status: FAILED");
						e2.printStackTrace();
						System.out.println("And there was an exception rolling back as well");
					}
					return;
				}
			}

		}





		// Code to commit the RemoveAllDelivered transaction
		try {
			//	ut.commit();
			System.out.println("Status: SUCCESSFUL");
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Status: FAILED");
			try {
				ut.rollback();
			}
			catch (Exception e2) {
				e2.printStackTrace();
				System.out.println("Status: FAILED");
			}
			return;
		}

		// Dispatch Manual Successful Results 
		try {
			this.webRemoveAllDeliveredResults(request, response);
			ib.debugOut("Successful Dispatch of Manual Results HTML Page.");
			System.out.println("Status: SUCCESSFUL");

		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception occurred in Manual Code trying to dispatch webManualResults().");
			return;
		}

		em.close(); */


	}


	/*
	 * A method that will dispatch a results page for webRemoveAllDelivered()
	 * 
	 * webRemoveAllDeliveredResults() * Deploys (Web Page) that handles successful
	 * transaction of webRemoveAllDelivered
	 *
	public void webRemoveAllDeliveredResults(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {
		
		PrintWriter out = null;	
		response.setContentType("text/html");  

		try{
			out = response.getWriter();
		}
		catch (Throwable e) {
			e.printStackTrace();	
		}   

		out.println("<html>");
		out.println("<center><H1>Order Archive Lite Results Form</H1></center>");    
		out.println("<body bgcolor=\"#f8f7cd\">");
		out.println("<h2 center>Success:</h2> ");
		out.println("<h3 center>All delivered order(s) have been removed from the order archive successfully.</h3> ");
		out.println("</head> ");     
		out.println( "</body>");
		out.println( "</html>"); 
		out.flush();	
	} */



	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////// PROGRAM CRASH //////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////



	/* A method that will dispatch the HTML page describing errors in the web application for both 
	 * webAutoGenInput() and webManualInput()
	 * 
	 * webException() * Deploys (Web Page) that Handles Exceptions
	 */
	public void webException(String theMessage, Exception theException, Throwable theThrowable, boolean caughtThrowable,
			HttpServletRequest request, HttpServletResponse response) {
		
		IRWWBase ib = new IRWWBase();

		if (caughtThrowable == false) {
			PrintWriter out = null;	
			response.setContentType("text/html");  

			try {
				out = response.getWriter();
			}
			catch (IOException e) {
			}

			out.println("<html>");
			out.println("<center><H1>Order Archive Lite Results Form</H1></center>");
			out.println("<body bgcolor=\"#f8f7cd\">");
			out.println("<h3 center>Status: FAILED</h3> ");
			out.println("<h3 center>Message:</h3>");
			out.println("<td>" + theMessage + "</td>");
			out.println("<h3 center>Exception:</h3>");
			out.println("<td>" + theException + "</td>");
			out.print("<h3 center>Stack:</h3>");
			out.println("<td>" + ib.getStackTrace(theException) + "</td>");
			out.println( "</body>");
			out.println( "</html>"); 
			out.flush();
		}

		else  {
			PrintWriter out = null;	
			response.setContentType("text/html");  

			try {
				out = response.getWriter();
			}
			catch (IOException e) {

			}

			out.println("<html>");
			out.println("<center><H1>Order Archive Lite Results Form</H1></center>");
			out.println("<body bgcolor=\"#f8f7cd\">");
			out.println("<h3 center>Status: FAILED</h3> ");
			out.println("<h3 center>Message:</h3>");
			out.println("<td>" + theMessage + "</td>");
			out.println("<h3 center>Exception:</h3>");
			out.println("<td>" + theThrowable + "</td>");
			out.print("<h3 center>Stack:</h3>");
			out.println("<td>" + ib.getStackTrace(theThrowable) + "</td>");
			out.println( "</body>");
			out.println( "</html>"); 
			out.flush();
		}

	}



	// end of the entire class OrderArchiveServlet
}























