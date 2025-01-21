package data.loader;

import java.io.IOException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import java.sql.Connection;
import javax.sql.DataSource;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import java.util.Date;
import java.util.Random;

import javax.naming.InitialContext;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import irwwbase.ExtendedRandom;
import irwwbase.IRWWBase;

/**
 * Servlet implementation class DBLoaderServlet
 */
@WebServlet("/TableLoaderServlet")
public class TableLoaderServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private InitialContext initCtx = null;

	private DataSource ds = null;
	
	private PreparedStatement orderLinePreparedStatement = null;
	private PreparedStatement orderPreparedStatement = null;
	

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TableLoaderServlet() {
        super();
     }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("TableLoaderServlet is starting work now.");
		long startTime = System.currentTimeMillis();
		
		int inputNumberOfWarehouses = Short.valueOf(request.getParameter("warehouseCount"));
		String schemaName = (request.getParameter("schemaName"));

		try {
			getDataSource();
		} catch (Exception ex) {
			System.out.println("Problem gettting DataSource, exception is: " + ex);
		}
	
	    try {
	    	createLoadCustomerTable(inputNumberOfWarehouses, schemaName);
	    } catch (Exception ex) {
	    	System.out.println ("Problem loading customer table: " + ex);
	    }
	    
	    try {
	    	createLoadOrdersTable_createOrderlineTable(inputNumberOfWarehouses, schemaName);
	    } catch (Exception ex) {
	    	System.out.println ("Problem loading orders table: " + ex);
	    }
	    
	    try {
	    	createOrderTrackingTable(schemaName);
	    } catch (Exception ex) {
	    	System.out.println ("Problem creating order_tracking table: " + ex);
	    }
	      
		long elapsedTime = (System.currentTimeMillis() - startTime) / 1000 ;
		System.out.println("TableLoaderServlet is finished, elapsed time (secs) = " + elapsedTime);

		// Display results page even if we had failures along the way.
	    String nextPage = "DBLoaderResultsForm.jsp";
	    
	    // Forward the request to the next page
		dispatch(request, response, nextPage);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void dispatch(HttpServletRequest request,
			HttpServletResponse response, String nextPage)
					throws ServletException, IOException {

		RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
		dispatch.forward(request, response);
	}
	
	
	protected void createLoadCustomerTable (int numberOfWarehouses, String schemaName) throws Exception {

		// Create customer DB table and load data for the requested number of warehouses

		// DB connection
		Connection con = null;	

		debugOut("*** Drop CUSTOMER DB table ***");

		try {	
			con = getConnection();						

			Statement stmt = con.createStatement();

			stmt.executeUpdate("DROP TABLE " + schemaName + ".CUSTOMER");

		} catch (Exception ex) {
			System.out.println("Problem dropping Customer DB table, exception is: " + ex);
		}

		// try block to make sure connection is closed
		try{

			// 10 first names
			String[] firstNamesArray = {"Brenda", "Phillip", "Carol", "Justin", "Mary",
					"Roy", "Virginia", "Adam", "Cathrine", "Kevin"};
			// 10 middle names
			String[] middleNamesArray = {"Jo", "Al", "El", "Me", "Ad", "Ni", "By", "Za", "Ma", "Pi"};

			// 10 street names
			String[] streetNamesArray = {"River", "Shady", "Beach", "Maple", "Mountain",
					"Perfect", "Happy", "Ocean", "High", "Sunny"};

			// 5 street suffix names
			String[] streetSuffixArray = {"Lane", "Road", "Street", "Drive", "Avenue"};

			// 50 city names
			String[] cityNamesArray = {"Juneau", "Montgomery", "Little Rock", "Phoenix", "Sacramento", "Denver", 
					"Hartford", "Washington", "Dover", "Tallahassee", "Atlanta", "Honolulu", "Des Moines", "Boise", "Springfield", "Indianapolis", "Topeka", "Frankfort", 
					"Baton Rouge", "Boston", "Annapolis", "Augusta", "Lansing", "Saint Paul", "Jefferson City", "Jackson", "Helena", "Raleigh", "Bismarck", "Lincoln", 
					"Concord", "Trenton", "Santa Fe", "Carson City", "Albany", "Columbus", "Oklahoma City", "Salem", "Harrisburg", "Providence", "Columbia", "Pierre", 
					"Nashville", "Austin", "Salt Lake City", "Richmond", "Montpelier", "Olympia", "Madison", "Charleston", "Cheyenne"};

			// 50 state abbreviations
			String[] stateAbbreviationsArray = {"AK", "AL", "AR", "AZ", "CA", "CO", 
					"CT", "DC", "DE", "FL", "GA", "HI", "IA", "ID", "IL", "IN", "KS", "KY", 
					"LA", "MA", "MD", "ME", "MI", "MN", "MO", "MS", "MT", "NC", "ND", "NE", 
					"NH", "NJ", "NM", "NV", "NY", "OH", "OK", "OR", "PA", "RI", "SC", "SD", 
					"TN", "TX", "UT", "VA", "VT", "WA", "WI", "WV", "WY"};	

			// 2 credit ratings
			String[] creditRatingArray = {"GC", "BC"};

			// Customer data is generated from 5 arrays of strings.   Each array has 3 different choices.
			String[] ageCategoryArray = {"AGE_CATEGORY: This customer is less than 21 years old.  ",
					"AGE_CATEGORY: This customer is between 21 and 55 years old.  ",
			"AGE_CATEGORY: This customer is greater than 55 years old.  "};
			String[] familyStatusArray = {"FAMILY_STATUS: This customer is single.  ",
					"FAMILY_STATUS: This customer is married, but has no children.  ",
			"FAMILY_STATUS: This customer is married, and has one or more children.  "};
			String[] favoriteSportArray = {"FAVORITE_SPORT: This customer's favorite sport is golf.  ",
					"FAVORITE_SPORT: This customer's favorite sport is tennis.  ",
			"FAVORITE_SPORT: This customer's favorite sport is hockey.  "};
			String[] shoppingPreferenceArray = {"SHOPPING_PREFERENCE: This customer only shops in the store.  ",
					"SHOPPING_PREFERENCE: This customer only shops online.  ",
			"SHOPPING_PREFERENCE: This customer shops both in the store and online.  "};
			String[] purchaseHistoryArray = {"PURCHASE_HISTORY:  This customer makes a purchase less than once per year.  ",
					"PURCHASE_HISTORY:  This customer makes 1 to 3 purchases per year.  ",
			"PURCHASE_HISTORY:  This cusomter make over 3 purchases per year.  "};		

			try {	
				
				Statement stmt = con.createStatement();

				String sql = "CREATE TABLE " + schemaName + ".CUSTOMER (C_ID SMALLINT NOT NULL, C_D_ID SMALLINT NOT NULL," +
						"C_W_ID SMALLINT NOT NULL, C_FIRST CHARACTER(16) NOT NULL, C_MIDDLE CHARACTER(2) NOT NULL," +
						"C_LAST CHARACTER(16) NOT NULL, C_STREET_1 CHARACTER(20) NOT NULL, C_STREET_2 CHARACTER(20) NOT NULL," +
						"C_CITY CHARACTER(20) NOT NULL, C_STATE CHARACTER(2) NOT NULL, C_ZIP CHARACTER(9) NOT NULL," +
						"C_PHONE CHARACTER(16) NOT NULL, C_SINCE  TIMESTAMP  NOT NULL, C_CREDIT CHARACTER(2) NOT NULL," +
						"C_CREDIT_LIM DECIMAL(12,2) NOT NULL, C_DISCOUNT DECIMAL(4,4) NOT NULL, C_BALANCE DECIMAL(12,2) NOT NULL,"+
						"C_YTD_PAYMENT DECIMAL(12,2) NOT NULL, C_PAYMENT_CNT  DECIMAL(4,0) NOT NULL, C_DELIVERY_CNT DECIMAL(4,0) NOT NULL," +
						"C_DATA VARCHAR(500) NOT NULL, VERSION INTEGER DEFAULT 1, PRIMARY KEY ( C_ID, C_D_ID, C_W_ID ) )";

				stmt.executeUpdate(sql);

			} catch (Exception ex) {
				System.out.println("Problem creating Customer DB table, exception is: " + ex);
			}

			String insertCustomerSQL = "INSERT INTO " + schemaName + ".CUSTOMER"
					+ "(C_ID, C_D_ID, C_W_ID, C_FIRST, C_MIDDLE," +
					"C_LAST, C_STREET_1, C_STREET_2," +
					"C_CITY, C_STATE, C_ZIP," +
					"C_PHONE, C_SINCE, C_CREDIT," +
					"C_CREDIT_LIM, C_DISCOUNT, C_BALANCE," +
					"C_YTD_PAYMENT, C_PAYMENT_CNT, C_DELIVERY_CNT," +
					"C_DATA) VALUES" +
					"(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			// Set the columns that have hard-coded initial values
			BigDecimal initCreditLimit = new BigDecimal(50000);
			BigDecimal initBalance = new BigDecimal(-10);
			BigDecimal initYTDPayment = new BigDecimal(10);
			BigDecimal initPaymentCount = new BigDecimal(1);
			BigDecimal initDeliveryCount = new BigDecimal(0);

			//Create random number generator for use in many places below
			Random myGenerator = new Random();

			// For 1 or more warehouses, depending on input parameter
			for (int warehouseId = 1; warehouseId <= numberOfWarehouses; warehouseId ++) {

				// For all 10 districts in each warehouse
				for (int districtId = 1; districtId <=10; districtId ++) {

					debugOut("Insert all Customers for, warehouse Id: " + warehouseId + ", district Id: " + districtId);
					
					// For 3000 customers in each warehouse / district combination
					for (int customerId = 1; customerId <= 3000; customerId ++) {

						try {	

							// Set customer "since" data to be current day / time
							Date today = new Date();
							Timestamp currentTime = new Timestamp(today.getTime());

							// Generate customer discount up to .5000 max value (ie. 50%)
							// Make the result fit into 4 digits.

							// Start with a random decimal from 0 to 1
							BigDecimal randomDecimal = new BigDecimal(Math.random());

							BigDecimal maxDiscount = new BigDecimal(.5000);

							// discount = random decimal fraction * .5000
							BigDecimal discount = maxDiscount.multiply(randomDecimal);

							// Round to 4 digits to fit column space
							discount = discount.round(new MathContext(4, RoundingMode.HALF_UP));


							// Generate customer's first and middle names from random array
							String firstName = firstNamesArray[myGenerator.nextInt(10)];
							String middleName = middleNamesArray[myGenerator.nextInt(10)];

							//Generate customer last name using extendedRandom from ERWWBaseLite (ie. same as autogen does)
							ExtendedRandom extRandom = new ExtendedRandom();
							String lastName = extRandom.nextName(new IRWWBase().dbSize());

							// Generate street address (number + street name + street suffix)
							// Number range from 100 - 5000
							String streetAddress = (Integer.valueOf(myGenerator.nextInt(4900) + 100)).toString();
							streetAddress = streetAddress + " " + streetNamesArray[myGenerator.nextInt(10)];
							streetAddress = streetAddress + " " + streetSuffixArray[myGenerator.nextInt(5)];

							// Generate city name and state name abbreviations using same random number.
							// This yields one of the 50 USA capital cities.
							int x = myGenerator.nextInt(50);
							String cityName = cityNamesArray[x];
							String stateName = stateAbbreviationsArray[x];

							// Generate zip code (range 10000 - 99000)
							String zipCode = (Integer.valueOf(myGenerator.nextInt(89000) + 10000)).toString();

							// Generate phone number
							// Area code 100-999
							String areaCode = (Integer.valueOf(myGenerator.nextInt(899) + 100)).toString();
							// Next 3 digits 100-999
							String next3 = (Integer.valueOf(myGenerator.nextInt(899) + 100)).toString();
							// Last 4 digits 1000 - 9999
							String last4 = (Integer.valueOf(myGenerator.nextInt(8999) + 1000)).toString();
							// Complete phone number format (123) 123-1234
							String phoneNumber = "(" + areaCode + ") " + next3 + "-" + last4; 

							// Credit Rating (either good credit "GC" or bad credit "BC")
							String creditRating = creditRatingArray[myGenerator.nextInt(2)];

							//Generate customer information data string
							String customerData = ageCategoryArray[myGenerator.nextInt(3)];
							customerData = customerData + familyStatusArray[myGenerator.nextInt(3)];
							customerData = customerData + favoriteSportArray[myGenerator.nextInt(3)];
							customerData = customerData + shoppingPreferenceArray[myGenerator.nextInt(3)];
							customerData = customerData + purchaseHistoryArray[myGenerator.nextInt(3)];


							// Load up all 22 columns of the customer row to insert into CUSTOMER table
							PreparedStatement preparedStatement = con.prepareStatement(insertCustomerSQL);
							preparedStatement.setInt(1, customerId);
							preparedStatement.setInt(2, districtId);
							preparedStatement.setInt(3, warehouseId);
							preparedStatement.setString(4, firstName);
							preparedStatement.setString(5, middleName);
							preparedStatement.setString(6, lastName);
							preparedStatement.setString(7, streetAddress);
							preparedStatement.setString(8, " ");
							preparedStatement.setString(9, cityName);
							preparedStatement.setString(10, stateName);
							preparedStatement.setString(11, zipCode);
							preparedStatement.setString(12, phoneNumber);	    
							preparedStatement.setTimestamp(13, currentTime);
							preparedStatement.setString(14, creditRating);
							preparedStatement.setBigDecimal(15, initCreditLimit);
							preparedStatement.setBigDecimal(16, discount);
							preparedStatement.setBigDecimal(17, initBalance);
							preparedStatement.setBigDecimal(18, initYTDPayment);
							preparedStatement.setBigDecimal(19, initPaymentCount);
							preparedStatement.setBigDecimal(20, initDeliveryCount);
							preparedStatement.setString(21, customerData);
							// VERSION is an integer column that JDBC uses as needed, we don't load it.

							// insert one row
							preparedStatement.executeUpdate();
							preparedStatement.close();
							con.commit();	

						} catch (Exception ex) {
							System.out.println("Problem inserting Customer into DB, exception is: " + ex);
						}

					} // End for customerId = 1-3000
				} // End for districtId = 1-10
			} // End for warehouseId = 1-max

		} finally {

			// Close connection used to load customer table
			if (con != null){ 
				con.close();
			}
		}  // end finally

	} // End loadCustomerTable


    
   protected void createLoadOrdersTable_createOrderlineTable (int numberOfWarehouses, String schemaName) throws Exception {
    	
    	// Drop then Create Orders DB table and load data for the requested number of warehouses
	       	
    	// DB connection
    	Connection con = null;
    	
		debugOut("*** Drop ORDERS DB table ***");
		try {	
			con = getConnection();						

			Statement stmt = con.createStatement();

			stmt.executeUpdate("DROP TABLE  " + schemaName + ".ORDERS");

		} catch (Exception ex) {
			System.out.println("Problem dropping ORDERS DB table, exception is: " + ex);
		}
    	
	    debugOut("*** Create ORDERS DB table for " + numberOfWarehouses + " warehouses ***");
    	// Outer try / finally block for resource closing
    	try {

    		try {	
    			//con = getConnection();
    			con.setAutoCommit(false);

    			Statement stmt = con.createStatement();

    			String sql = "CREATE TABLE " + schemaName + ".ORDERS (O_ID INTEGER NOT NULL, O_D_ID SMALLINT NOT NULL, O_W_ID SMALLINT NOT NULL," + 
    					"O_C_ID SMALLINT NOT NULL, O_ENTRY_D TIMESTAMP NOT NULL, O_CARRIER_ID SMALLINT, O_OL_CNT DECIMAL(2,0) NOT NULL," +
    					"O_ALL_LOCAL SMALLINT NOT NULL, O_TRACKING_NUMBER CHARACTER(24), O_TRACKING_HOPS_COUNT INTEGER DEFAULT 0," +
    					"VERSION INTEGER DEFAULT 1,  PRIMARY KEY ( O_ID, O_D_ID, O_W_ID ) )";

    			stmt.executeUpdate(sql);

    		} catch (Exception ex) {
    			System.out.println("Problem creating Orders DB table, exception is: " + ex);
    		}
    		
    		debugOut("*** Drop ORDERLINE DB table ***");
    		try {
    			Statement stmt = con.createStatement();

    			stmt.executeUpdate("DROP TABLE  " + schemaName + ".ORDERLINE");

    		} catch (Exception ex) {
    			System.out.println("Problem dropping ORDERLINE DB table, exception is: " + ex);
    		}

    		debugOut("*** Create ORDERLINE DB table for " + numberOfWarehouses + " warehouses ***");

    		try {	

    			Statement stmt = con.createStatement();

    			String sql = "CREATE TABLE " + schemaName + ".ORDERLINE (OL_O_ID INTEGER NOT NULL, OL_D_ID SMALLINT NOT NULL," +
    					"OL_W_ID SMALLINT NOT NULL, OL_NUMBER SMALLINT NOT NULL, OL_I_ID INTEGER NOT NULL, OL_SUPPLY_W_ID SMALLINT NOT NULL," +
    					"OL_DELIVERY_D TIMESTAMP, OL_QUANTITY DECIMAL(2,0) NOT NULL, OL_AMOUNT DECIMAL(6,2) NOT NULL, OL_DIST_INFO CHARACTER(24)NOT NULL," +
    					"PRIMARY KEY(OL_O_ID, OL_D_ID, OL_W_ID, OL_NUMBER) )";

    			stmt.executeUpdate(sql);

    		} catch (Exception ex) {
    			System.out.println("Problem creating Orderline DB table, exception is: " + ex);
    		}
    		
    		// now commit the table creates
    		con.commit();

    		String insertOrdersSQL = "INSERT INTO " + schemaName + ".ORDERS" +
    				"(O_ID, O_D_ID, O_W_ID," + 
    				"O_C_ID, O_ENTRY_D, O_CARRIER_ID," +
    				"O_OL_CNT, O_ALL_LOCAL, O_TRACKING_NUMBER," +
    				"O_TRACKING_HOPS_COUNT) VALUES" +
    				"(?,?,?,?,?,?,?,?,?,?)";

    		// Tracking number is always initialized to null string
    		String initTrackingNumber = "";

    		// Number of tracking hops count is always initialized to 0
    		int initTrackingHopsCount = 0;

    		//Create random number generator for use in many places below
    		Random myGenerator = new Random();
    		
    		// For 1 or more warehouses, depending on input parameter
    		for (int warehouseId = 1; warehouseId <= numberOfWarehouses; warehouseId ++) {

    			// For all 10 districts in each warehouse
    			for (int districtId = 1; districtId <=10; districtId ++) {
    				
    				long districtStartTime = System.currentTimeMillis();
					debugOut("Insert all Orders for, warehouse Id: " + warehouseId + ", district Id: " + districtId);

    				// For 3000 orders in each warehouse / district combination
    				for (int orderId = 1; orderId <= 3000; orderId ++) {

    					try {
    						
    						// Generate Custonmer ID 1 - 3000 by counting backwards from 3000 (ie. subract order id
    						// which is counting upwards.  We may need to make this more random in the future.
    						int customerId = 3001 - orderId;

    						// Generate order entry date as today.
    						Date today = new Date();
    						Timestamp orderEntryDate = new Timestamp(today.getTime());

    						// Generate carrier ID
    						// Normally carrier ID is random in the range of 1 - 10.  However,
    						// orders with ID > 2100 are new orders.  So last 900 of the 3000
    						// orders are new orders.  New orders have no carrier ID yet so it
    						// is set to 0 for all of them.
    						int carrierId = 0; // assume a new order to start with (ie. no carrier Id)
    						if (orderId <= 2100) {
    							// its not a new order so set carrir Id to random number (1-10)
    							carrierId = myGenerator.nextInt(10) + 1;
    						}

    						// Generate orderline count as random number from 5-15.
    						// However, we need it stuffed into a 2 place decimal too, so convert it
    						int orderLineCountInt = myGenerator.nextInt(11) + 5;
    						BigDecimal orderLineCount = new BigDecimal(orderLineCountInt);
    						orderLineCount = orderLineCount.setScale(0);


    						// Note:  we cannot set allLocal on the Order, until we first generate
    						//        all the orderlines for this Order.  If any orderline is generated 
    						//        where the supplying warehouse is remote, then allLocal must be
    						//        set to false (ie. 0) for the entire order.


    						// Now that we know almost everything about the order, go insert the 
    						// orderlines for this order into the ORDERLINE table.   Track if any
    						// orderline in the set had a remote supplying warehouse.
    						
    						// debugOut("...Insert " + orderLineCountInt + " orderlines");
    						boolean remoteSupplyWarehouseFound = false;
    						boolean executeIt = false;  // don't execute the batch until the last orderline 
    						for (int orderLineNumber = 1; orderLineNumber <= orderLineCountInt; orderLineNumber++)	{
    							if (orderLineNumber == orderLineCountInt){
    								executeIt = true;
    							}
    							boolean resultAllLocal = loadOrderLineRow(con, orderId, districtId, warehouseId, orderLineNumber, orderEntryDate, numberOfWarehouses, schemaName, executeIt);
    							if (!resultAllLocal) {
    								remoteSupplyWarehouseFound = true;
    							}
    						}

    						// Finally, set allLocal which says whether all order lines come from the current warehouse or
    						// at least one orderline is from a remote warehouse.  In the special case of only 1 warehouse
    						// allLocal is always true (ie. 1) since there can be no remote warehouse.  In all other cases
    						// allLocal will be true in about 10% orders since the average order has 10 orderlines, and
    						// orderlines are generated so that about 1% of them have a remote warehouse as their supplying
    						// warehouse.
    						int allLocal = 0;  // assume remote supply warehouse
    						if (numberOfWarehouses == 1){
    							allLocal = 1;
    						} else {
    							if (remoteSupplyWarehouseFound) {
    								allLocal = 0;					
    							} else {
    								allLocal = 1;
    							}
    						}

    						// Now load up all 11 columns of the order row to insert into the ORDERS table
    						if(orderId == 1) {
    							if (orderPreparedStatement != null) { 
    							   orderPreparedStatement.close();
    							   orderPreparedStatement=null;
    							}   							
    							orderPreparedStatement = con.prepareStatement(insertOrdersSQL);
    						}
    						orderPreparedStatement.setInt(1, orderId);
    						orderPreparedStatement.setInt(2, districtId);
    						orderPreparedStatement.setInt(3, warehouseId);
    						orderPreparedStatement.setInt(4, customerId);
    						orderPreparedStatement.setTimestamp(5, orderEntryDate);
    						orderPreparedStatement.setInt(6, carrierId);
    						orderPreparedStatement.setBigDecimal(7, orderLineCount);
    						orderPreparedStatement.setInt(8, allLocal);
    						orderPreparedStatement.setString(9, initTrackingNumber);
    						orderPreparedStatement.setInt(10, initTrackingHopsCount);
    						// VERSION is an integer column that JDBC uses as needed, we don't load it.

    						// batch up the new row
    						orderPreparedStatement.addBatch();
    						
    						
    					} catch (Exception ex) {
    						System.out.println("Problem inserting Order into DB, exception is: " + ex);
    					}

    				} // End for orderId = 1-3000
    				
    			// For all orders in the each district commit connection
    			orderPreparedStatement.executeBatch();	
    			con.commit();	
    				
			    long districtElapsedTime = (System.currentTimeMillis() - districtStartTime) / 1000;
			    debugOut ("Finished all orders, elapsed time (secs): " + districtElapsedTime);
    				
    			} // End for districtId = 1-10
    			
    			
    		} // End for warehouseId = 1 to max
    		

    	} finally {
    		if (con != null){
    			con.close();
    		}
    	}

   } // End loadOrdersTable	
    

	protected boolean loadOrderLineRow(Connection con, int orderId, int districtId, int warehouseId, int orderLineNumber, Timestamp orderEntryDate, int numberOfWarehouses, String schemaName, boolean executeIt) throws Exception {
		
		// This method returns a boolean that indicates whether the orderline loaded has a supplying warehouse
		// that is the current warehouse (ie local) or a remote warehouse (ie. not local).  We start out here
		// assuming the warehouse is remote so allLocalFlag is false.
		boolean resultAllLocalFlag = false;  
				
		try {

			//Create random number generator for use in many places below
			Random myGenerator = new Random();

			String insertOrderLineSQL = "INSERT INTO  " + schemaName + ".ORDERLINE" +
					"(OL_O_ID, OL_D_ID, OL_W_ID, OL_NUMBER, OL_I_ID, OL_SUPPLY_W_ID," +
					"OL_DELIVERY_D, OL_QUANTITY, OL_AMOUNT, OL_DIST_INFO) VALUES" +
					"(?,?,?,?,?,?,?,?,?,?)";

			// Generate itemId by randomly picking one of 100,000 items available.
			int itemId = myGenerator.nextInt(100000) + 1;

			// Generate the supplying warehouse Id for this item.  In the special case of only
			// one warehouse all orderlines are supplied from the home warehouse (ie. allLocal = 1).
			// Otherwise, make 99% of the remaining orderlines be supplied from the local warehouse and %1
			// of the remaining orderlines be supplied from a randomly picked remote warehouse.		
			int supplyWarehouseId = 0;

			if (numberOfWarehouses == 1) {
				supplyWarehouseId = warehouseId;
				resultAllLocalFlag = true;
			} else {
				// 99% local and 1% remote
				int random = myGenerator.nextInt(100) +1;
				if (random < 100) {
					// the 99% case
					supplyWarehouseId = warehouseId;
					resultAllLocalFlag = true;
				} else {
					// the 1% case
					boolean remoteWarehouseFound = false;
					while(remoteWarehouseFound == false) {
						int tempWarehouseId = myGenerator.nextInt(25) + 1;

						// Make sure random warehouse picked is not our current (local) warehouse.
						// Otherwise, try again.
						if (tempWarehouseId != warehouseId) {
							remoteWarehouseFound = true;
							supplyWarehouseId = tempWarehouseId;
						}				
					} // end while
					resultAllLocalFlag = false;			
				}  // end 1% case

			} // end if 

			// Generate orderline delivery date.  If this order has already been delivered (ie. its
			// not a new order), then set the orderline's delivery date the same as the order's entry
			// date.  Otherwise, for new orders the orderline's date is not set at all (ie. until it is 
			// delivered later).
			Timestamp orderLineDeliveryDate = null;
			// Each warehouse / district combination has 3000 orders.  Of those 3000 orders, the last
			// 900 start out as new orders.
			if (orderId <= 2100) {
				// It's an old order (ie. already delivered)
				orderLineDeliveryDate = orderEntryDate;
			}

			// Generate the quantity of the item that customer wanted in this orderline (eg, 1 shovel, or 5 shovels?).
			// For 99% of the orderlines just hardcode the quantity to 5, but for the remaining 1% pick a random quantity
			// between 1 and 10.   Make sure the quantity fits in 2 decimal places.
			BigDecimal itemQuantity = null;
			int random = myGenerator.nextInt(100) + 1;
			if (random < 100) {
				// the 99% case
				itemQuantity = new BigDecimal(5);
			} else {
				// the 1% case
				itemQuantity = new BigDecimal(myGenerator.nextInt(10) + 1);
			}
			itemQuantity = itemQuantity.setScale(0);

			// Generate a price from $.01 to $9,999.99 for this orderline.
			// Old Orders (ie already delivered) are paid for an have an amount of 0.
			// New Orders have a randomly generated amount.   Make sure price fits in
			// 6 decimal places.
			BigDecimal orderLinePrice = null;
			if (orderId <= 2100) {
				// It's an old (ie. delivered order)
				orderLinePrice = new BigDecimal(0);
			} else {
				orderLinePrice = new BigDecimal((myGenerator.nextDouble() * 9999.99) + .01);
			}			
			orderLinePrice = orderLinePrice.round(new MathContext(6)); 
			orderLinePrice = orderLinePrice.setScale(2, RoundingMode.HALF_UP);        

			// Generate orderline district info for this item.  This comes from the stock table
			// column for the district, but we will generate it here so we do not have to query
			// the stock table.   The general format is "item x for district y", where x may be 
			// in the range 1-100,000 and district may be in the range 1-10)
			String orderLineDistInfo = "item " + itemId + " for dist " + districtId;

			// Now load up all 12 columns of the orderline row to insert into the ORDERLINE table	
			if (orderLineNumber == 1) {
				if (orderLinePreparedStatement != null) {
				   orderLinePreparedStatement.close();
				   orderLinePreparedStatement = null;
				}
			    orderLinePreparedStatement = con.prepareStatement(insertOrderLineSQL);
			}
			orderLinePreparedStatement.setInt(1, orderId);
			orderLinePreparedStatement.setInt(2, districtId);
			orderLinePreparedStatement.setInt(3, warehouseId);
			orderLinePreparedStatement.setInt(4, orderLineNumber);
			orderLinePreparedStatement.setInt(5, itemId);
			orderLinePreparedStatement.setInt(6, supplyWarehouseId);
			orderLinePreparedStatement.setTimestamp(7, orderLineDeliveryDate);   
			orderLinePreparedStatement.setBigDecimal(8, itemQuantity);
			orderLinePreparedStatement.setBigDecimal(9, orderLinePrice);
			orderLinePreparedStatement.setString(10, orderLineDistInfo);
			// I_ORDERLINE_INFO is an XML column that we currently are not loading for Liberty
			// VERSION is an integer column that JDBC uses as needed, we don't load it.

			orderLinePreparedStatement.addBatch();
			if (executeIt) {
				orderLinePreparedStatement.executeBatch();
			}
			
		} catch (Exception ex) {System.out
			.println("Problem inserting orderline into DB, exception is: " + ex);
		}
		return(resultAllLocalFlag);
	}
	
	protected void createOrderTrackingTable (String schemaName) throws Exception {

		// Create Order_Tracking DB table; no data is loaded

		// DB connection
		Connection con = null;	

		debugOut("*** Drop ORDER_TRACKING DB table ***");

		try {	
			con = getConnection();						

			Statement stmt = con.createStatement();

			stmt.executeUpdate("DROP TABLE " + schemaName + ".ORDER_TRACKING");

		} catch (Exception ex) {
			System.out.println("Problem dropping Order_Tracking DB table, exception is: " + ex);
		}

		try {	
				
			Statement stmt = con.createStatement();

			String sql = "CREATE TABLE  " + schemaName + ".ORDER_TRACKING (OT_TRACKING_NUMBER CHARACTER(24) NOT NULL, OT_TRACKING_HOP INTEGER NOT NULL, " +
						 " OT_DATE TIMESTAMP NOT NULL, OT_City CHAR(20) NOT NULL, OT_State CHAR(2) NOT NULL, OT_Country CHAR(3) NOT NULL, " +
						 " OT_Activity SMALLINT NOT NULL DEFAULT 0, VERSION INTEGER DEFAULT 1, PRIMARY KEY ( OT_TRACKING_NUMBER, OT_TRACKING_HOP ) )";

			stmt.executeUpdate(sql);

		} catch (Exception ex) {
				System.out.println("Problem creating Order_Tracking DB table, exception is: " + ex);
		}
	}
   
	/**
	 * dropDatasource method comment
	 * 
	 * @exception java.rmi.RemoteException
	 *                The exception description.
	 */
	public void dropDataSource() throws java.lang.Exception {
		
		try {
			initCtx.close();
			initCtx = null;
			ds = null;
		} catch (Exception ex) {
			System.out.println("ERROR: dropDatasource() failed with Exception: " + ex);
			ex.printStackTrace();
			throw ex;
		}
	}

	/**
	 * getDatasource method comment
	 * 
	 */
	private synchronized DataSource getDataSource()	throws Exception {
				
		if (ds == null) {
			String datasourceName = "java:comp/env/jdbc/ERWWDataSourceWithPwd";
			try {
				initCtx = new InitialContext();
				ds = (javax.sql.DataSource) initCtx.lookup(datasourceName);
			} catch (Exception ex) {
				System.out.println("ERROR: getDatasource() failed with exception: " + ex);
				ex.printStackTrace();
				throw ex;
			}
		}
		return ds;
	}

	
	/**
	 * getConnection method comment
	 * 
	 */
	private Connection getConnection() throws java.lang.Exception {
		
		Connection theConnection = null;
		try {
			theConnection = ds.getConnection();

		} catch (Exception ex) {
			System.out.println("ERROR: getConnection() failed with Exception: " + ex);
			ex.printStackTrace();
			throw ex;
		}
		return theConnection;
	}
	
	private void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null)
			if (System.getProperty("DEBUG_OUT").equals("ON"))
				System.out.println(message);
	}


}
