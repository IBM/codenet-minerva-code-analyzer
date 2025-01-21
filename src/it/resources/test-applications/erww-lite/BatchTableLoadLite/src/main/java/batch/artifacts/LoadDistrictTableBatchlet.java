package batch.artifacts;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

import jakarta.batch.api.BatchProperty;
import jakarta.batch.api.Batchlet;
import jakarta.batch.runtime.context.StepContext;
import jakarta.inject.Inject;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class LoadDistrictTableBatchlet implements Batchlet {

	@Inject
	private StepContext stepCtx;
	
	/**
	 * The number of warehouses to create
	 */
	@Inject
	@BatchProperty(name = "numberWarehouses")
	private String numWarehousesStr;
	
	/**
	 * The District warehouse to load
	 */
	@Inject
	@BatchProperty(name = "currentWarehouse")
	private String currentWarehouseStr;
	
	/**
	 * The District to load
	 */
	@Inject
	@BatchProperty(name = "currentDistrict")
	private String currentDistrictStr;
	
	private DataSource ds = null;
	
	private InitialContext initCtx = null;
	
    /**
     * Default constructor. 
     */
    public LoadDistrictTableBatchlet() {
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
    	// Each partition that loads part of the DISTRICT table will call this batchlet.

    	try {
			getDataSource();
		} catch (Exception ex) {
			System.out.println("Problem gettting DataSource, exception is: " + ex);
			ex.printStackTrace();
		}
    	
    	try {
			loadDistrictTable();
		} catch (Exception e) {
			System.out.println("Problem loading DistrictTable, exception is: " + e);
			e.printStackTrace();
		}
		return null;
    }
    

    protected void loadDistrictTable () throws Exception {
 
       	int numWarehouses = Integer.parseInt(numWarehousesStr);
    	int currentWarehouse = Integer.parseInt(currentWarehouseStr);
    	int currentDistrict = Integer.parseInt(currentDistrictStr);
    	int maxWarehouseCnt = 0;
    	int maxDistrictCnt = 0 ;
    			
	
    	// If numWarehouses = 1, then we will have 10 partitions that will each load one
    	// District.  Otherwise we will have "n" partitions, that will each
    	// load one warehouse of districts (ie. 10 districts per warehouse).
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

			String insertDistrictSQL = "INSERT INTO DISTRICT"
					+ "(D_ID, D_W_ID, D_NAME," +
					"D_STREET_1, D_STREET_2," +
					"D_CITY, D_STATE, D_ZIP," +
					"D_TAX, D_YTD, D_NEXT_O_ID) VALUES" +
					"(?,?,?,?,?,?,?,?,?,?,?)";

			// Set the columns that have hard-coded inital values
			BigDecimal initYTDPayment = new BigDecimal(30000);
			int nextOrderId = 3001;
			

			//Create random number generator for use in many places below
			Random myGenerator = new Random();

			// For 1 or more warehouses, depending on input parameter
	 		for (int warehouseId = currentWarehouse; warehouseId <= maxWarehouseCnt; warehouseId++){ //Integer.parseInt(numWarehousesStr); warehouseId ++) {
                
	 			System.out.println("LoadDistrictTableBatchlet starting work for warehouseId = " + warehouseId);
	 			long warehouseStartTime = System.currentTimeMillis();
	 			
	 			// For either 1 district, or all 10 districts in one warehouse
	 			for (int districtId = currentDistrict; districtId <=maxDistrictCnt; districtId ++) {

						try {
							
							// Generate the warehouse name
							String districtName = "DIST" + String.format("%02d", Integer.parseInt(currentDistrictStr)) + getRandomString(0,4);

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
							BigDecimal districtTax =new BigDecimal(Math.random());
							districtTax = districtTax.round(new MathContext(4, RoundingMode.HALF_UP));
							
							// Load up all 11 columns of the District row to insert into District table
							PreparedStatement preparedStatement = con.prepareStatement(insertDistrictSQL);
							preparedStatement.setInt(1, districtId);
							preparedStatement.setInt(2, warehouseId);
							preparedStatement.setString(3, districtName);
							preparedStatement.setString(4, streetAddress);
							preparedStatement.setString(5, " ");
							preparedStatement.setString(6, cityName);
							preparedStatement.setString(7, stateName);
							preparedStatement.setString(8, zipCode);
							preparedStatement.setBigDecimal(9, districtTax);
							preparedStatement.setBigDecimal(10, initYTDPayment);
							preparedStatement.setInt(11, nextOrderId);
							// VERSION is an integer column that JDBC uses as needed, we don't load it.

							// insert one row
							preparedStatement.executeUpdate();
							preparedStatement.close();

						} catch (Exception ex) {
							System.out.println("Problem inserting District into DB, exception is: " + ex);
							ex.printStackTrace();
						}

					} // End for districtId = 1-max
					
					long elapsedTime = (System.currentTimeMillis() - warehouseStartTime) / 1000;
					debugOut("Finished inserting 10 districts for, warehouse Id: " + warehouseId + ", time (secs) = " + elapsedTime);
					
				} // End for warehouseId = 1-max
		} finally {

			// Close connection used to load District table
			if (con != null){ 
				con.close();
			}
		}  // end finally

	} // End loadTable
   
   

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


	private void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null)
			if (System.getProperty("DEBUG_OUT").equals("ON"))
				System.out.println(message);
	}
}

