package irwwbase;

/**
 * Insert the type's description here. Creation date: (08/21/2000 4:11:16 PM)
 * 
 * @author: Administrator
 */

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import jakarta.inject.Named;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

import com.ibm.db2.jcc.DB2Diagnosable;
import com.ibm.db2.jcc.DB2Sqlca;

@POJOQualifier
@Named("irwwBaseBean")
public class IRWWBase implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2441301208389636626L;
	
	private static java.lang.String _className;
	//Comment out unused variable
	//private java.lang.String s = new String();
	
	private java.lang.String idStamp;

	private long t1 = 0;

	private long t2 = 0;
	
	/**
	 * IRWWBase constructor comment.
	 */
	public IRWWBase() {
		super();
		_className = (IRWWBase.class).getName();
	}

	/**
	 * Insert the method's description here. Creation date: (1/28/2002 4:24:46
	 * PM)
	 */
	public void currentTime() {
		displayOut("TRANID Stamp is:" + idStamp + " <<<Current time is: "
				+ new java.util.Date().toString() + ", milliseconds: "
				+ String.valueOf(System.currentTimeMillis()));
	}

	/**
	 * Insert the method's description here. Creation date: (12/18/2001 12:58:15
	 * PM)
	 * 
	 * @return java.lang.Byte
	 */
	public byte dbSize() {
		byte dbsize = 1;
		if (System.getProperty("DB_SIZE") != null) {
			if (System.getProperty("DB_SIZE").equals("S"))
				dbsize = (byte) 0;
			if (System.getProperty("DB_SIZE").equals("M"))
				dbsize = (byte) 1;
			if (System.getProperty("DB_SIZE").equals("L"))
				dbsize = (byte) 2;
		}
		return dbsize;
	}
	
	

	/**
	 * Insert the method's description here. Creation date: (11/28/2001 10:52:30
	 * AM)
	 * 
	 * @param message
	 *            java.lang.String
	 */
	public void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null)
			if (System.getProperty("DEBUG_OUT").equals("ON")){
				//System.out.println(message);
				System.out.println("Transaction Id: " + TraceTransaction.getTransactionId() + ": " + message);
			}
	}

	/**
	 * Insert the method's description here. Creation date: (11/28/2001 10:52:30
	 * AM)
	 * 
	 * @param message
	 *            java.lang.String
	 */
	public void gvtOut(String message) {
		if (System.getProperty("GVT_OUT") != null)
			if (System.getProperty("GVT_OUT").equals("ON"))
				System.out.println(message);
	}

	/**
	 * Insert the method's description here. Creation date: (12/3/2001 9:17:26
	 * PM)
	 */
	public void displayOut(String s) {
		if (System.getProperty("DISPLAY_OUT") == null)
			;
		else if (System.getProperty("DISPLAY_OUT").equals("ON"))
			System.out.println(s);
		else
			;
	}

	/**
	 * Insert the method's description here. Creation date: (12/11/2001 1:04:47
	 * PM)
	 */
	public void endStamp(String _className) {
		displayOut("<<< " + _className + " Completed, " + "<<<Ending Time is: "
				+ new java.util.Date().toString() + ", TranId=" + idStamp
				+ ", milliseconds: "
				+ String.valueOf(System.currentTimeMillis()));
	}
	
	public void endOfTran(String _className) {
		displayOut("<<< " + _className + " Completed, " + "<<<Ending Time is: "
				+ new java.util.Date().toString() + ", ThreadId=" + Thread.currentThread().getId()
				+ ", milliseconds: "
				+ String.valueOf(System.currentTimeMillis()));
	}

	/**
	 * Insert the method's description here. Creation date: (1/28/2002 4:36:58
	 * PM)
	 * 
	 * @return java.lang.String
	 */
	public String getCurrentTime() {
		return " TRANID Stamp is:" + idStamp + " <<<Current time is: "
				+ new java.util.Date().toString() + ", milliseconds: "
				+ String.valueOf(System.currentTimeMillis());
	}

	/**
	 * Insert the method's description here. Creation date: (1/28/2002 4:28:39
	 * PM)
	 */
	public void getInitStamp() {
		idStamp = String.valueOf(System.currentTimeMillis());
	}

	private String getTimeStamp() {

		Timestamp timestamp = new Timestamp(java.lang.System.currentTimeMillis());
		String timeStamp = timestamp.toString();

		return timeStamp; // method getTimeStamp
	}
	
	protected void handleBMTGlobalTranException(Exception t, String methodName,
			String className, UserTransaction userTran) throws UserException,
			SystemException {

		System.out.println("Exception detected in " + methodName
				+ " method of " + this.getClass());
		System.out.println("********** S T A C K  T R A C E **************");

		checkSQLEx(t);

		t.printStackTrace();

		if ((userTran != null)
				&& (t.getMessage().indexOf(
						"jakarta.transaction.RollbackException") == -1)) {
			userTran.rollback();
		}

		throw new UserException("Method " + methodName + " of class "
				+ className + " failed!!! "
				+ irwwbase.Stcnum.getInstance().getStcnum(), t.getMessage());
	}

	protected void handleBMTLocalTranException(Exception t, String methodName,
			String className, Connection con) throws UserException,
			SQLException {

		System.out.println("Exception detected in " + methodName
				+ " method of " + this.getClass());
		System.out.println("********** S T A C K  T R A C E **************");

		checkSQLEx(t);

		t.printStackTrace();

		if ((con != null)) {
			con.rollback();
		}

		throw new UserException("Method " + methodName + " of class "
				+ className + " failed!!! "
				+ irwwbase.Stcnum.getInstance().getStcnum(), t.getMessage());
	}

	/**
	 * Insert the method's description here. Creation date: (12/11/2001 1:04:23
	 * PM)
	 */
	public void startStamp(String _className) {
		getInitStamp();
		displayOut("<<<Running " + _className + "<<<Starting Time is: "
				+ new java.util.Date().toString() + ", TranId=" + idStamp
				+ ", milliseconds: "
				+ String.valueOf(System.currentTimeMillis()));
	}
	
	public void startOfTran(String _className) {
		displayOut("<<<Running " + _className + "<<<Starting Time is: "
				+ new java.util.Date().toString() + ", ThreadId=" + Thread.currentThread().getId()
				+ ", milliseconds: "
				+ String.valueOf(System.currentTimeMillis()));
	}

	public double timeDiff() {
		t1 = t2;
		t2 = System.currentTimeMillis();
		double d = (t2 - t1) / 1000.00000;
		return d;
	}

	public void timeDiffInit() {
		t2 = System.currentTimeMillis();
	}

	public boolean debugEnabled() {
		// Method to determine if debug has been enabled.
		if (System.getProperty("DEBUG_OUT") != null)
			if (System.getProperty("DEBUG_OUT").equals("ON"))
				return true;

		return false;
	}

	/**
	 * Insert the method's description here. Creation date: (6/19/2002 4:03:47
	 * PM)
	 */
	public String getSTC() {
		return " STC" + irwwbase.Stcnum.getInstance().getStcnum();
	}

	public void checkSQLEx(Throwable t) {
		if (t instanceof SQLException) {
			SQLException s = (SQLException) t;
			System.out.println("SQL CODE=" + s.getErrorCode() + " SQL STATE="
					+ s.getSQLState());
		}
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
	public boolean is913(Throwable t) {
		boolean chk = false;
		if (checkFor913(t).indexOf("Failed due to -913") != -1)
			chk = true;
		return chk;
	}
	
	public void logResultFile(String logline, java.io.FileWriter resultFile) throws Exception {

		String timeStamp = new String("");
		timeStamp = this.getTimeStamp();

		try {
			resultFile.write(timeStamp + " - " + logline);
		} catch (Exception ex) {
			System.out
					.println("%%%%% ERROR: resultFile.write failed in log method - "
							+ _className + " " + this.getSTC());
			ex.printStackTrace();
			throw new Exception(
					"%%%%% ERROR: resultFile.write failed in log method - "
							+ _className + " " + this.getSTC()+ " " + this.getStackTrace(ex));
		}
		return; // method log

	}

	public Exception loopThruExesOld(Exception e) {
		if (e.getMessage().indexOf("<&!@") != -1) {
			for (int i = 0; i <= 4; i++) {
				e = (Exception) e.getCause();
				if (e == null)
					break;
				if (e.getMessage().indexOf("<&!@") != -1)
					break;
			}
		}
		return e;
	}
	
	public Throwable loopThruExes(Throwable e) {
		if (e.getCause()!=null) loopThruExes(e.getCause());				
		return e;
	}

	public boolean is913msg(String exMsg) {
		boolean chk = false;
		if (exMsg.indexOf("Failed due to -913") != -1)
			chk = true;
		return chk;
	}

	public String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		if (aThrowable==null) return "no exception stack";
		else aThrowable.printStackTrace(printWriter);
		return result.toString();
	}
	
	public void cutAFile(String fileName, String data) {
		
		java.io.FileWriter resultFile = null;
		try {
			resultFile = new java.io.FileWriter(fileName);
		} catch (Exception ex) {
			System.out
					.println("error opening "+fileName+" for writing ");
			ex.printStackTrace();			
		}	

		try {
			resultFile.write(data);
			resultFile.close();
		} catch (Exception ex) {
			System.out
					.println("error writing to "+fileName);
			ex.printStackTrace();			
		}

	}
	
	public boolean isDebugOn() {
		boolean isItOn=false;
		if (System.getProperty("DEBUG_OUT") != null)
			if (System.getProperty("DEBUG_OUT").equals("ON")) isItOn=true;		
		return isItOn;
	}
	    
    public DataSource getDatasource(String datasourceJNDIName) throws Exception {
        DataSource datasource = null;
        if (datasource == null) {
            InitialContext initCtx = null;
            
            debugOut("datasourceJNDIName = " + datasourceJNDIName);

            try {
                initCtx = new InitialContext();
                datasource = (javax.sql.DataSource) initCtx.lookup(datasourceJNDIName);
            } catch (Exception ex) {
                System.out.println("ERROR: " + _className + ".getDatasource() failed");
                ex.printStackTrace();
                throw ex;
            }
        }
        return datasource;
    }

    public Connection getConnection(DataSource datasource) throws Exception, InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        debugOut("<< Entering getConnection");
        
        Connection con = null;
        
        try {

            con = datasource.getConnection();
        } catch (Exception ex) {
            System.out.println("ERROR: " + _className + ".getConnection() failed where con = " + con);
            ex.printStackTrace();
            throw ex;
        }
        return con;
    }
	
    public void closeResultSet(java.sql.ResultSet rs) throws Exception {

        try {
            debugOut("<<< In " + _className + ".closeResultSet() - before rs.close() where rs = " + rs);
            if (rs != null)
                rs.close();
            rs = null;
        } catch (Exception ex) {
            System.out.println("<<< ERROR: " + _className + ".closeResultSet() failed where rs = " + rs);
            ex.printStackTrace();
            throw ex;
        }
    }

    public void closePreparedStatement(java.sql.PreparedStatement ps) throws Exception {

        try {
            debugOut("<<< In " + _className + ".closePreparedStatement() - before ps.close() where ps = " + ps);
            if (ps != null)
                ps.close();
            ps = null;
        } catch (Exception ex) {
            System.out.println("<<< ERROR: " + _className + ".closePreparedStatement() failed where ps = " + ps);
            ex.printStackTrace();
            throw ex;
        }
    }
    
    public void closeConnection(Connection con) throws Exception {
        try {
            debugOut("<< - " + _className + ".closeConnection() - before con.close() where con = " + con);
            if (con != null && !con.isClosed())
                con.close();
            con = null;
        } catch (Exception ex) {
            System.out.println("ERROR: " + _className + ".closeConnection() failed where con = " + con);
            ex.printStackTrace();
            throw ex;
        }
    }
    
	public String getHost() {		
		String host = null;
		if (System.getProperty("GET_HOST") == null);{
			host = System.getProperty("GET_HOST");
		}		
		return host;
	}
	
	public String getPort() {		
		String port = null;
		if (System.getProperty("GET_PORT") == null);{
			port = System.getProperty("GET_PORT");
		}		
		return port;
	}
		
}

