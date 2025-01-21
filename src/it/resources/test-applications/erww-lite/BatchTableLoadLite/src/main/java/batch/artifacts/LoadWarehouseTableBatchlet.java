package batch.artifacts;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

import jakarta.batch.api.BatchProperty;
import jakarta.batch.api.Batchlet;
//Comment out unused import
//import jakarta.batch.runtime.context.StepContext;
import jakarta.inject.Inject;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class LoadWarehouseTableBatchlet implements Batchlet {

	private InitialContext initCtx = null;

	private DataSource ds = null;

	//Comment out unused private variable
	//@Inject
	//private StepContext stepCtx;

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
	 * Default constructor. 
	 */
	public LoadWarehouseTableBatchlet() {
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
		System.out.println("Warehouse Process");

		try {
			getDataSource();
		} catch (Exception ex) {
			System.out.println("Problem gettting DataSource, exception is: " + ex);
		}

		try {
			loadWarehouseTable();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void loadWarehouseTable() throws Exception {
		System.out.println("Warehouse Load");
		int currentWarehouse = Integer.parseInt(currentWarehouseStr);
		System.out.println("currentWarehouse = " + currentWarehouse);

		// DB connection
		Connection con = null;

		// try block to make sure connection is closed
		try {

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

			try {	
				con = getConnection();

			} catch (Exception ex) {
				System.out.println("Problem getting connection: " + ex);
				ex.printStackTrace();
			}

			String insertWarehouseSQL = "INSERT INTO WAREHOUSE"
					+ "(W_ID, W_NAME, W_STREET_1, W_STREET_2," +
					" W_CITY, W_STATE, W_ZIP, W_TAX, W_YTD) VALUES" +
					"(?,?,?,?,?,?,?,?,?)";

			// Set the columns that have hard-coded initial values
			BigDecimal initYTDPayment = new BigDecimal(300000);

			//Create random number generator for use in many places below
			Random myGenerator = new Random();

			System.out.println("LoadCustomerTableBatchlet starting work for warehouseId = " + currentWarehouse);
			long warehouseStartTime = System.currentTimeMillis();

			try {

				// Generate the warehouse name
				String warehouseName = "WH" + String.format("%04d", Integer.parseInt(currentWarehouseStr)) + getRandomString(0,4);

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
				
				// Generate tax
				BigDecimal warehouseTax =new BigDecimal(Math.random());
				warehouseTax = warehouseTax.round(new MathContext(4, RoundingMode.HALF_UP));
				

				// Load up all 9 columns of the warehouse row to insert into WAREHOUSE table
				PreparedStatement preparedStatement = con.prepareStatement(insertWarehouseSQL);
				preparedStatement.setInt(1, currentWarehouse);
				preparedStatement.setString(2, warehouseName);
				preparedStatement.setString(3, streetAddress);
				preparedStatement.setString(4, " ");
				preparedStatement.setString(5, cityName);
				preparedStatement.setString(6, stateName);
				preparedStatement.setString(7, zipCode);
				preparedStatement.setBigDecimal(8, warehouseTax);	    
				preparedStatement.setBigDecimal(9, initYTDPayment);
				// VERSION is an integer column that JDBC uses as needed, we don't load it.

				// insert one row
				preparedStatement.executeUpdate();
				preparedStatement.close();

			} catch (Exception ex) {
				System.out.println("Problem inserting Warehouse into DB, exception is: " + ex);
				ex.printStackTrace();
			}

			long elapsedTime = (System.currentTimeMillis() - warehouseStartTime) / 1000;
			System.out.println("LoadWarehouseTableBatchlet finished work for warehouseId = " + currentWarehouse + ", elapsed time (secs) = " + elapsedTime);

		} finally {

			// Close connection used to load warehouse table
			if (con != null){ 
				con.close();
			}
		}  // end finally

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

	/* Comment out unused private method
	private void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null)
			if (System.getProperty("DEBUG_OUT").equals("ON"))
				System.out.println(message);
	}
	*/

	/**
	 * 
	 * @param min			The minimum length of the string
	 * @param max			The maximum length of the string
	 * @return	randomStr	The randomly generated string
	 * @throws Exception
	 */
	public String getRandomString(int min, int max) throws Exception{
		String randomStr = "";
		Random rdm = new Random();
		
		char[] ALPHABET = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
				'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
				'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

		for(int i=0;i<min;i++)
			randomStr += ALPHABET[rdm.nextInt(ALPHABET.length)];			
		for(int j=0;j<(rdm.nextInt((max+1)-min));j++)
			randomStr += ALPHABET[rdm.nextInt(ALPHABET.length)];

		return randomStr;
	}

}
