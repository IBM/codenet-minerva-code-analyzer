package batch.artifacts;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.batch.api.Batchlet;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class CreateTableBatchlet implements Batchlet {

	DataSource ds;
	
	private InitialContext initCtx = null;
	
    /**
     * Default constructor. 
     */
    public CreateTableBatchlet() {
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

    	// DB connection
    	Connection con = null;

    	// Create customer DB table and load data for the requested number of warehouses
    	try {
    		getDataSource();
    	} catch (Exception ex) {
    		System.out.println("Problem getting DataSource, exception is: " + ex);
    		ex.printStackTrace();
    	}

    	try {	
    		con = getConnection();

    		Statement stmt = con.createStatement();

    		String sql = "CREATE TABLE  CBIVP.CUSTOMER (C_ID SMALLINT NOT NULL, C_D_ID SMALLINT NOT NULL," +
    				"C_W_ID SMALLINT NOT NULL, C_FIRST CHARACTER(16) NOT NULL, C_MIDDLE CHARACTER(2) NOT NULL," +
    				"C_LAST CHARACTER(16) NOT NULL, C_STREET_1 CHARACTER(20) NOT NULL, C_STREET_2 CHARACTER(20) NOT NULL," +
    				"C_CITY CHARACTER(20) NOT NULL, C_STATE CHARACTER(2) NOT NULL, C_ZIP CHARACTER(9) NOT NULL," +
    				"C_PHONE CHARACTER(16) NOT NULL, C_SINCE  TIMESTAMP  NOT NULL, C_CREDIT CHARACTER(2) NOT NULL," +
    				"C_CREDIT_LIM DECIMAL(12,2) NOT NULL, C_DISCOUNT DECIMAL(4,4) NOT NULL, C_BALANCE DECIMAL(12,2) NOT NULL,"+
    				"C_YTD_PAYMENT DECIMAL(12,2) NOT NULL, C_PAYMENT_CNT  DECIMAL(4,0) NOT NULL, C_DELIVERY_CNT DECIMAL(4,0) NOT NULL," +
    				"C_DATA VARCHAR(500) NOT NULL, VERSION INTEGER DEFAULT 1, PRIMARY KEY ( C_ID, C_D_ID, C_W_ID ) )";
    		
            debugOut("Creating CUSTOMER table...");
    		
    		stmt.executeUpdate(sql);

    	} catch (Exception ex) {
    		System.out.println("Problem creating Customer DB table, exception is: " + ex);
    		ex.printStackTrace();
    	}


    	try {	

    		Statement stmt = con.createStatement();

    		String sql = "CREATE TABLE CBIVP.ORDERS (O_ID INTEGER NOT NULL, O_D_ID SMALLINT NOT NULL, O_W_ID SMALLINT NOT NULL," + 
    				"O_C_ID SMALLINT NOT NULL, O_ENTRY_D TIMESTAMP NOT NULL, O_CARRIER_ID SMALLINT, O_OL_CNT DECIMAL(2,0) NOT NULL," +
    				"O_ALL_LOCAL SMALLINT NOT NULL, O_TRACKING_NUMBER CHARACTER(24), O_TRACKING_HOPS_COUNT INTEGER DEFAULT 0," +
    				"VERSION INTEGER DEFAULT 1,  PRIMARY KEY ( O_ID, O_D_ID, O_W_ID ) )";

    		debugOut("Creating ORDERS table...");
    		stmt.executeUpdate(sql);

    	} catch (Exception ex) {
    		System.out.println("Problem creating Orders DB table, exception is: " + ex);
    		ex.printStackTrace();
    	}

    	try {	

    		Statement stmt = con.createStatement();

    		String sql = "CREATE TABLE CBIVP.ORDERLINE (OL_O_ID INTEGER NOT NULL, OL_D_ID SMALLINT NOT NULL," +
    				"OL_W_ID SMALLINT NOT NULL, OL_NUMBER SMALLINT NOT NULL, OL_I_ID INTEGER NOT NULL, OL_SUPPLY_W_ID SMALLINT NOT NULL," +
    				"OL_DELIVERY_D TIMESTAMP, OL_QUANTITY DECIMAL(2,0) NOT NULL, OL_AMOUNT DECIMAL(6,2) NOT NULL, OL_DIST_INFO CHARACTER(24)NOT NULL," +
    				"PRIMARY KEY(OL_O_ID, OL_D_ID, OL_W_ID, OL_NUMBER) )";
    		
    		debugOut("Creating ORDERLINE table...");
    		stmt.executeUpdate(sql);

    	} catch (Exception ex) {
    		System.out.println("Problem creating Orderline DB table, exception is: " + ex);
    		ex.printStackTrace();
    	}
    	
    	try {	

    		Statement stmt = con.createStatement();

    		String sql = "CREATE TABLE  CBIVP.ORDER_TRACKING (OT_TRACKING_NUMBER CHARACTER(24) NOT NULL, OT_TRACKING_HOP INTEGER NOT NULL, " +
					 " OT_DATE TIMESTAMP NOT NULL, OT_City CHAR(20) NOT NULL, OT_State CHAR(2) NOT NULL, OT_Country CHAR(3) NOT NULL, " +
					 " OT_Activity SMALLINT NOT NULL DEFAULT 0, VERSION INTEGER DEFAULT 1, PRIMARY KEY ( OT_TRACKING_NUMBER, OT_TRACKING_HOP ) )";
    		
    		debugOut("Creating ORDERLINE table...");
    		stmt.executeUpdate(sql);

    	} catch (Exception ex) {
    		System.out.println("Problem creating Ordertracking DB table, exception is: " + ex);
    		ex.printStackTrace();
    	}
    	
    	try {	

    		Statement stmt = con.createStatement();

    		String sql = "CREATE TABLE  CBIVP.WAREHOUSE (W_ID SMALLINT NOT NULL, W_NAME CHARACTER(10) NOT NULL, " +
					 " W_STREET_1 CHARACTER(20) NOT NULL, W_STREET_2 CHARACTER(20) NOT NULL, W_CITY CHARACTER(20) NOT NULL, W_STATE CHAR(2) NOT NULL, " +
					 " W_ZIP CHAR(9) NOT NULL, W_TAX DECIMAL(4,4), W_YTD DECIMAL(12,2), " + 
					 " VERSION INTEGER DEFAULT 1, PRIMARY KEY ( W_ID ) )";
    		
    		debugOut("Creating WAREHOUSE table...");
    		stmt.executeUpdate(sql);

    	} catch (Exception ex) {
    		System.out.println("Problem creating Warehouse DB table, exception is: " + ex);
    		ex.printStackTrace();
    	}
    	
    	try {	

    		Statement stmt = con.createStatement();

    		String sql = "CREATE TABLE  CBIVP.DISTRICT (D_ID SMALLINT NOT NULL, D_W_ID SMALLINT NOT NULL, D_NAME CHARACTER(10) NOT NULL, " +
					 " D_STREET_1 CHARACTER(20) NOT NULL, D_STREET_2 CHARACTER(20) NOT NULL, D_CITY CHARACTER(20) NOT NULL, D_STATE CHAR(2) NOT NULL, " +
					 " D_ZIP CHAR(9) NOT NULL, D_TAX DECIMAL(4,4), D_YTD DECIMAL(12,2), D_NEXT_O_ID INTEGER NOT NULL, " + 
					 " VERSION INTEGER DEFAULT 1, PRIMARY KEY ( D_ID, D_W_ID ) )";
    		
    		debugOut("Creating DISTRICT table...");
    		stmt.executeUpdate(sql);

    	} catch (Exception ex) {
    		System.out.println("Problem creating Warehouse DB table, exception is: " + ex);
    		ex.printStackTrace();
    	}

    	try {
    		con.close();
    	} catch (SQLException e) {
    		System.out.println("Problem closing connection, exception is: " + e);
    		e.printStackTrace();
    	}

    	return null;
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
