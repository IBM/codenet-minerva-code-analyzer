package batch.artifacts;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

import jakarta.batch.api.BatchProperty;
import jakarta.batch.api.Batchlet;
import jakarta.batch.runtime.context.StepContext;
import jakarta.inject.Inject;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import irwwbase.ExtendedRandom;
import irwwbase.IRWWBase;

public class LoadCustomerTableBatchlet implements Batchlet {

	@Inject
	private StepContext stepCtx;
	
	/**
	 * The number of warehouses to create
	 */
	@Inject
	@BatchProperty(name = "numberWarehouses")
	private String numWarehousesStr;
	
	/**
	 * The customer warehouse to load
	 */
	@Inject
	@BatchProperty(name = "currentWarehouse")
	private String currentWarehouseStr;
	
	/**
	 * The customer district to load
	 */
	@Inject
	@BatchProperty(name = "currentDistrict")
	private String currentDistrictStr;
	
	private DataSource ds = null;
	
	private InitialContext initCtx = null;
	
	
    /**
     * Default constructor. 
     */
    public LoadCustomerTableBatchlet() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see Batchlet#stop()
     */
    public void stop() {
        // TODO Auto-generated method stub
    }

	/**
     * @see Batchlet#process()
     */
    public String process() {   
    	
    	// Each partition that loads part of the CUSTOMER table will call this batchlet.

    	try {
			getDataSource();
		} catch (Exception ex) {
			System.out.println("Problem gettting DataSource, exception is: " + ex);
			ex.printStackTrace();
		}
    	
    	try {
			loadCustomerTable();
		} catch (Exception e) {
			System.out.println("Problem loading CustomerTable, exception is: " + e);
			e.printStackTrace();
		}
		return null;
    }

    protected void loadCustomerTable () throws Exception {
 
       	int numWarehouses = Integer.parseInt(numWarehousesStr);
    	int currentWarehouse = Integer.parseInt(currentWarehouseStr);
    	int currentDistrict = Integer.parseInt(currentDistrictStr);
    	int maxWarehouseCnt = 0;
    	int maxDistrictCnt = 0 ;
    			
	
    	// If numWarehouses = 1, then we will have 10 partitions that will each load one
    	// District worth of customers.  Otherwise we will have "n" partitions, that will each
    	// load one warehouse of customers (ie. 10 districts worth for that warehouse).
    	if (numWarehouses == 1) {
    		maxWarehouseCnt = 1;
    		maxDistrictCnt = currentDistrict;
    	} else {
    		maxWarehouseCnt = currentWarehouse;
    		maxDistrictCnt = 10;
    	}

		// DB connection
		Connection con = null;

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
				con = getConnection();

			} catch (Exception ex) {
				System.out.println("Problem getting connection: " + ex);
				ex.printStackTrace();
			}

			String insertCustomerSQL = "INSERT INTO CUSTOMER"
					+ "(C_ID, C_D_ID, C_W_ID, C_FIRST, C_MIDDLE," +
					"C_LAST, C_STREET_1, C_STREET_2," +
					"C_CITY, C_STATE, C_ZIP," +
					"C_PHONE, C_SINCE, C_CREDIT," +
					"C_CREDIT_LIM, C_DISCOUNT, C_BALANCE," +
					"C_YTD_PAYMENT, C_PAYMENT_CNT, C_DELIVERY_CNT," +
					"C_DATA) VALUES" +
					"(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			// Set the columns that have hard-coded inital values
			BigDecimal initCreditLimit = new BigDecimal(50000);
			BigDecimal initBalance = new BigDecimal(-10);
			BigDecimal initYTDPayment = new BigDecimal(10);
			BigDecimal initPaymentCount = new BigDecimal(1);
			BigDecimal initDeliveryCount = new BigDecimal(0);

			//Create random number generator for use in many places below
			Random myGenerator = new Random();

			// For 1 or more warehouses, depending on input parameter
	 		for (int warehouseId = currentWarehouse; warehouseId <= maxWarehouseCnt; warehouseId++){ //Integer.parseInt(numWarehousesStr); warehouseId ++) {
                
	 			System.out.println("LoadCustomerTableBatchlet starting work for warehouseId = " + warehouseId);
	 			long warehouseStartTime = System.currentTimeMillis();
	 			
	 			// For either 1 district, or all 10 districts in one warehouse
	 			for (int districtId = currentDistrict; districtId <=maxDistrictCnt; districtId ++) {

					debugOut("Insert 3000 Customers for, warehouse Id: " + warehouseId + ", and district Id: " + districtId);
					long districtStartTime = System.currentTimeMillis();
	 				
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
							String streetAddress = Integer.valueOf(myGenerator.nextInt(4900) + 100).toString();
							streetAddress = streetAddress + " " + streetNamesArray[myGenerator.nextInt(10)];
							streetAddress = streetAddress + " " + streetSuffixArray[myGenerator.nextInt(5)];

							// Generate city name and state name abbreviations using same random number.
							// This yields one of the 50 USA capital cities.
							int x = myGenerator.nextInt(50);
							String cityName = cityNamesArray[x];
							String stateName = stateAbbreviationsArray[x];

							// Generate zip code (range 10000 - 99000)
							String zipCode = Integer.valueOf(myGenerator.nextInt(89000) + 10000).toString();

							// Generate phone number
							// Area code 100-999
							String areaCode = Integer.valueOf(myGenerator.nextInt(899) + 100).toString();
							// Next 3 digits 100-999
							String next3 = Integer.valueOf(myGenerator.nextInt(899) + 100).toString();
							// Last 4 digits 1000 - 9999
							String last4 = Integer.valueOf(myGenerator.nextInt(8999) + 1000).toString();
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

						} catch (Exception ex) {
							System.out.println("Problem inserting Customer into DB, exception is: " + ex);
							ex.printStackTrace();
						}

					} // End for customerId = 1-3000
					
					long elapsedTime = (System.currentTimeMillis() - districtStartTime) / 1000;
					debugOut("Finished inserting 3000 Customers for, warehouse Id: " + warehouseId + ", and district Id: " + districtId + ", time (secs) = " + elapsedTime);
					
				} // End for districtId = 1-max
	 			
	 			long elapsedTime = (System.currentTimeMillis() - warehouseStartTime) / 1000;
	 			System.out.println("LoadCustomerTableBatchlet finished work for warehouseId = " + warehouseId + ", elapsed time (secs) = " + elapsedTime);
	 			
			} // End for warehouseId = 1-max

		} finally {

			// Close connection used to load customer table
			if (con != null){ 
				con.close();
			}
		}  // end finally

	} // End loadCustomerTable
   
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
	
	/**
	 * getDatasource method comment
	 * 
	 */
	private synchronized DataSource getDataSource()	throws Exception {
				
		if (ds == null) {
			String datasourceName = "jdbc/ERWWDataSourceWithPwd";
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
	

	private void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null)
			if (System.getProperty("DEBUG_OUT").equals("ON"))
				System.out.println(message);
	}
}


