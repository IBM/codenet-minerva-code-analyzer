package consistency.check.jdbc.lite;

import java.io.IOException;
import java.sql.Connection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import jakarta.ejb.EJBException;
import jakarta.ejb.FinderException;
import javax.naming.InitialContext;
//Comment out unused import
//import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import jakarta.transaction.UserTransaction;
import jakarta.persistence.OptimisticLockException;

import irwwbase.IRWWBase;

/**
 * Servlet implementation class ConCheckServlet
 */
public class ConCheckServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	EntityManagerFactory emf = null;

	private String className = this.getClass().toString();
	private IRWWBase irwwbase = new IRWWBase();

	private short minWarehouseId = 0;
	private short maxWarehouseId = 0;
	private short minDistrictId = 0;
	private short maxDistrictId = 0;

	private Short numberOfRetries = null;
	private Vector<String> results = null;

	// //////////////////////xniu new jdbc variables
	private InitialContext initCtx = null;

	private DataSource ds = null;

	private java.sql.PreparedStatement aPS = null;

	private java.sql.ResultSet rs = null;

	private Connection con = null;

	private boolean isConnected = false;

	private String _className = "CheckIntegrityBean";

	// Vector globalResVec = null;
	Vector<String> globalResVec = new Vector<String>();

	int globalHowMany = 0;

	// /////////////////xniu end jdbc variables

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ConCheckServlet() {
		super();
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Servlet#getServletConfig()
	 */
	public ServletConfig getServletConfig() {
		return null;
	}

	/**
	 * @see Servlet#getServletInfo()
	 */
	public String getServletInfo() {
		return null;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		performTask(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		performTask(request, response);
	}

	protected void performTask(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//Comment out unused variable
		//EntityManager em = null;

		// xniu - use to be null
		numberOfRetries = 0;

		short retryCommit = 0;
		short commitErrorCode = 0;
		boolean retryAgain = false;

		// String lockMode = null;
		String consistencyCheck = null;
		String nextPage = "ConsistencyCheckResults.jsp";

		irwwbase.debugOut("<<< Entering " + className + " performTask ");

		minWarehouseId = Short.valueOf(request.getParameter("minWarehouseId"));
		irwwbase.debugOut("<<< In " + className
				+ " performTask: minWarehouseId = " + minWarehouseId);

		maxWarehouseId = Short.valueOf(request.getParameter("maxWarehouseId"));
		irwwbase.debugOut("<<< In " + className
				+ " performTask: maxWarehouseId = " + maxWarehouseId);

		minDistrictId = Short.valueOf(request.getParameter("minDistrictId"));
		irwwbase.debugOut("<<< In " + className
				+ " performTask: minDistrictId = " + minDistrictId);

		maxDistrictId = Short.valueOf(request.getParameter("maxDistrictId"));
		irwwbase.debugOut("<<< In " + className
				+ " performTask: maxDistrictId = " + maxDistrictId);

		consistencyCheck = request.getParameter("consistencyCheckGroup");
		irwwbase.debugOut("<<< In " + className
				+ " performTask: consistencyCheck = " + consistencyCheck);

		irwwbase.debugOut("<<< In "
				+ className
				+ " performTask: before while loop for first time through or"
				+ " for retry in OFI path due to OptimisticLockException during commit");
		while ((retryCommit == 0)
				|| ((retryCommit <= numberOfRetries) && (commitErrorCode == 1))) {

			retryAgain = ((retryCommit <= numberOfRetries) && (commitErrorCode == 1));

			retryCommit++;
			results = new Vector<String>();
			InitialContext initialContext = null;
			UserTransaction userTran = null;

			irwwbase.debugOut("<<< In " + className
					+ " performTask: retryCommit = " + retryCommit);
			irwwbase.debugOut("<<< In " + className
					+ " performTask: commitErrorCode = " + commitErrorCode);
			irwwbase.debugOut("<<< In " + className
					+ " performTask: retryAgain = " + retryAgain);

			try {

				irwwbase.debugOut("Before beginning a transaction in performTask method");
				initialContext = new InitialContext();
				userTran = (UserTransaction) initialContext
						.lookup("java:comp/UserTransaction");
				userTran.begin();

			} catch (Exception e) {
				System.out
						.println("<<< "
								+ className
								+ " ConCheckServlet.performTask() - Begin Transaction - Exception: "
								+ e);
				results.addElement("Exception: Begin Transaction.");
				e.printStackTrace();
			}

			try {
				if ((Short.valueOf(consistencyCheck)) == 0) {
					this.consistencyCheckAll();
				}
				if ((Short.valueOf(consistencyCheck)) == 1) {
					this.consistencyCheck1();
				}
				if ((Short.valueOf(consistencyCheck)) == 2) {
					this.consistencyCheck2();
				}
				if ((Short.valueOf(consistencyCheck)) == 3) {
					this.consistencyCheck3();
				}
				if ((Short.valueOf(consistencyCheck)) == 4) {
					this.consistencyCheck4();
				}
				if ((Short.valueOf(consistencyCheck)) == 8) {
					this.consistencyCheck8();
				}
				if ((Short.valueOf(consistencyCheck)) == 9) {
					this.consistencyCheck9();
				}
				if ((Short.valueOf(consistencyCheck)) == 13) {
					this.consistencyCheck13();
				}
			} catch (Exception e) {
				System.out.println("<<< " + className
						+ " ConCheckServlet.performTask() - Exception: " + e);
				results.addElement("Exception when running the consistency check");
				e.printStackTrace();
			}

			try {
				irwwbase.debugOut("Before commit of a transaction in performTask method");

				userTran.commit();

			} catch (OptimisticLockException t) {
				System.out.println("OptimisticLockException: retryCommit = "
						+ retryCommit + " commit()");
				commitErrorCode = 1;
			} catch (Exception e) {
				commitErrorCode = 2;
				System.out
						.println("<<< "
								+ className
								+ " ConCheckServlet.performTask() - Commit Transaction - Exception: "
								+ e);
				results.addElement("Exception when commiting the transaction");
				e.printStackTrace();
			}
		} // end while loop

		request.setAttribute("results", results);

		// Forward the request to the next page
		dispatch(request, response, nextPage);
	}

	public void consistencyCheckAll() throws FinderException {

		irwwbase.debugOut("<<< Entering " + className + " consistencyCheckAll ");
		this.consistencyCheck1();
		this.consistencyCheck2();
		this.consistencyCheck3();
		this.consistencyCheck4();
		this.consistencyCheck8();
		this.consistencyCheck9();
		this.consistencyCheck13();
	}

	// xniu new jdbc
	Vector<String> cc1Results = new Vector<String>();

	protected void consistencyCheck1() throws FinderException {

		// New JDBC Code
		int failureCheck1 = -1;
		String[] conCheck1Output = null;

		Vector<String> resVec = new Vector<String>();
		try {
			getDatasource();

			checkConnection();

		} catch (Exception ex) {
			throw new EJBException("<<< " + className + "checkConnection() failed" + ex.getMessage());
		}
		int howMany = 0;
		try {

			String sqlString = "SELECT * FROM (SELECT W_ID, W_YTD, SUM(D_YTD) AS SUM_D_YTD FROM WAREHOUSE, DISTRICT WHERE W_ID = D_W_ID GROUP BY W_ID, W_YTD) AS CC1 WHERE W_YTD!= SUM_D_YTD";

			aPS = con.prepareStatement(sqlString);

			// Execute query
			rs = aPS.executeQuery();

			while (rs.next()) {

				String result = "wid=" + rs.getShort(1) + " w_ytd="
						+ rs.getDouble(2) + " sum_d_ytd=" + rs.getDouble(3);

				System.out.println("result=" + result);
				resVec.add(result);
				globalResVec.add(result);
				howMany++;
			}

			Enumeration<String> enum1 = resVec.elements();
			globalHowMany = howMany;
			if (howMany == 0) {
				failureCheck1 = 0;
				conCheck1Output = new String[1];
				conCheck1Output[0] = "CCConCheck.Success1";
			} else {

				conCheck1Output = new String[howMany + 1];
				conCheck1Output[0] = "CCConCheck.Failed1";
				for (int i = 1; enum1.hasMoreElements(); i++) {
					conCheck1Output[i] = (String) enum1.nextElement();
				}
			}

		} catch (java.sql.SQLException ex) {

			if (ex.getNextException() != null) {
				System.out.println("getNextException = "
						+ ex.getNextException().getErrorCode());
			}

			throw new jakarta.ejb.FinderException(
					"CheckIntegrityBean.conCheck1 - JDBC query failed"
							+ ex.getMessage() + "SQLCode = "
							+ ex.getErrorCode() + "get Next Excep = ");

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new jakarta.ejb.FinderException(
					"CheckIntegrityBean.conCheck1 - JDBC query failed"
							+ ex.getMessage());
		} finally {
			try {

				if (rs != null) {
					debugOut("%%%%% Before rs.close()");
					rs.close();
				}

				if (aPS != null) {
					debugOut("%%%%% Before aPS.close()");
					aPS.close();
				}

				dropConnection();

			} catch (Exception ex) {
				throw new jakarta.ejb.FinderException(
						"CheckIntegrityBean.conCheck1 - dropConnection()"
								+ ex.getMessage());
			}
		}

		if (failureCheck1 == 0) {
			irwwbase.debugOut("<<< CC1 Succeeded!");
			results.addElement("CC1 Succeeded");
		}

		else {

			irwwbase.debugOut("<<< CC1 Failed!");
			Collections.addAll(results, conCheck1Output);
			// xniu results.addElement("CC1 Failed: " + conCheck1Output);
		}

		results.addElement("--------------------------------------------------------------------------------"
				+ "--------------------------------------------------------------------------------");

	}

	protected void consistencyCheck2() throws FinderException {
		int failureCheck2 = -1;
		int noNewOrdersCheck = -1;
		String[] conCheck2Output = null;

		Vector<String> resVec = new Vector<String>();
		try {
			getDatasource();
			checkConnection();
		} catch (Exception ex) {
			throw new EJBException("conCheck2 checkConnection() failed"
					+ ex.getMessage());
		}
		int howMany = 0;
		try {
			//Begin Defect 109432 fix.
			String sqlString = "SELECT Count(*) FROM WAREHOUSE";
			
			aPS = con.prepareStatement(sqlString);
			
			rs = aPS.executeQuery();
			
			rs.next();
			int numberWarehouses = rs.getInt(1);
			
			sqlString = "SELECT NO_W_ID, NO_D_ID, Count(*) FROM NEWORDERS GROUP BY NO_W_ID, NO_D_ID";
			
			aPS = con.prepareStatement(sqlString, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
			
			rs = aPS.executeQuery();
			
			int warehouseId = 0;
			int districtId = 0;
			//int numberNewOrders;
			
			//Find the number of rows to see if all the warehouse and district combinations are present. If they are, we don't need to search for the missing rows.
			int rowcount = 0;
			rs.last();
			rowcount = rs.getRow();
			rs.first();
			
			if (rowcount != (numberWarehouses * 10)) {
				//Search for the missing rows because the number of rows is not equal to the expected number of rows.
				for (int warehouseCount = 1; warehouseCount <= numberWarehouses; warehouseCount++ ) {
					for (int districtCount = 1; districtCount <= 10; districtCount++) {
						warehouseId = rs.getInt(1);
						districtId = rs.getInt(2);
						//numberNewOrders = rs.getInt(3);
						if (!((warehouseCount == warehouseId) && (districtCount == districtId))) {
							noNewOrdersCheck = 0;
							System.out.println("WARNING: There are no NewOrders for WarehouseId "
									+ warehouseCount + " and DistrictId " + districtCount + ". Reload the ERWW tables and ensure the workload script is executing 10 NewOrder transactions per 1 Delivery transaction.");
						} else if (!((warehouseCount == numberWarehouses) && (districtCount == 10))) {
							//Warehouse and district match, so move onto the next result.
							if (rowcount > rs.getRow()) {
								rs.next();
							}
						}
					
					}
				}
			} //End Defect 109432 fix.
			
			// String sqlString =
			// "SELECT D_W_ID, D_ID, D_O_ID, O_ID, NO_O_ID FROM ((SELECT D_W_ID, D_ID, D_NEXT_O_ID - 1 AS D_O_ID FROM DISTRICT GROUP BY D_W_ID, D_ID, D_NEXT_O_ID) X FULL OUTER JOIN (SELECT O_W_ID, O_D_ID, MAX(O_ID) AS O_ID FROM ORDERS GROUP BY O_W_ID, O_D_ID) Y ON D_W_ID = O_W_ID AND D_ID = O_D_ID FULL OUTER JOIN (SELECT NO_W_ID, NO_D_ID, MAX(NO_O_ID) AS NO_O_ID FROM NEWORDERS GROUP BY NO_W_ID, NO_D_ID) Z ON D_W_ID = NO_W_ID AND D_ID = NO_D_ID) WHERE O_ID != NO_O_ID WITH UR";
			sqlString = "SELECT D_W_ID, D_ID, D_O_ID, O_ID, NO_O_ID FROM ((SELECT D_W_ID, D_ID, D_NEXT_O_ID - 1 AS D_O_ID FROM DISTRICT GROUP BY D_W_ID, D_ID, D_NEXT_O_ID) X FULL OUTER JOIN (SELECT O_W_ID, O_D_ID, MAX(O_ID) AS O_ID FROM ORDERS GROUP BY O_W_ID, O_D_ID) Y ON D_W_ID = O_W_ID AND D_ID = O_D_ID FULL OUTER JOIN (SELECT NO_W_ID, NO_D_ID, MAX(NO_O_ID) AS NO_O_ID FROM NEWORDERS GROUP BY NO_W_ID, NO_D_ID) Z ON D_W_ID = NO_W_ID AND D_ID = NO_D_ID) WHERE ((D_O_ID != O_ID) OR (O_ID != NO_O_ID)) WITH UR";
			// The above sql has been updated as per Dan Kulik

			aPS = con.prepareStatement(sqlString);

			// Execute query
			rs = aPS.executeQuery();

			while (rs.next()) {

				String result = "D_W_ID=" + rs.getShort(1) + " D_ID="
						+ rs.getShort(2) + " D_O_ID=" + rs.getInt(3) + " O_ID="
						+ rs.getInt(4) + " NO_O_ID=" + rs.getInt(5);

				System.out.println("result=" + result);
				resVec.add(result);
				globalResVec.add(result);
				howMany++;
			}
			globalHowMany = howMany;
			Enumeration<String> enum1 = resVec.elements();
			if (howMany == 0) {
				failureCheck2 = 0;
				conCheck2Output = new String[1];
				conCheck2Output[0] = "CCConCheck.Success2";
			} else {
				conCheck2Output = new String[howMany + 1];
				conCheck2Output[0] = "CCConCheck.Failed2";
				for (int i = 1; enum1.hasMoreElements(); i++) {
					conCheck2Output[i] = (String) enum1.nextElement();
				}
			}
			
			//Defect 109432 fix. Fail the consistency check if there are no new orders.
			if (noNewOrdersCheck == 0) {
				failureCheck2 = -1;
				String[] conCheck2OutputNew = new String[conCheck2Output.length + 1];
				for (int i = 0; i < conCheck2Output.length; i++) {
					conCheck2OutputNew[i] = conCheck2Output[i];
				}
				conCheck2Output = conCheck2OutputNew;
				conCheck2Output[0] = "CCConCheck.Failed2";
				conCheck2Output[conCheck2Output.length-1] = "WARNING: There are no new orders. Reload the ERWW tables and ensure the workload script is executing 10 NewOrder transactions per 1 Delivery transaction.";
			}

		} catch (java.sql.SQLException ex) {

			if (ex.getNextException() != null) {
				System.out.println("getNextException = "
						+ ex.getNextException().getErrorCode());
			}

			throw new jakarta.ejb.FinderException(
					"CheckIntegrityBean.conCheck2 - JDBC query failed"
							+ ex.getMessage() + "SQLCode = "
							+ ex.getErrorCode() + "get Next Excep = ");

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new jakarta.ejb.FinderException(
					"CheckIntegrityBean.conCheck2 - JDBC query failed"
							+ ex.getMessage());
		} finally {
			try {

				if (rs != null) {
					debugOut("%%%%% Before rs.close()");
					rs.close();
				}

				if (aPS != null) {
					debugOut("%%%%% Before aPS.close()");
					aPS.close();
				}

				dropConnection();

			} catch (Exception ex) {
				throw new jakarta.ejb.FinderException(
						"CheckIntegrityBean.conCheck2 - dropConnection()"
								+ ex.getMessage());
			}
		}

		if (failureCheck2 == 0) {
			irwwbase.debugOut("<<< CC2 Succeeded!");
			results.addElement("CC2 Succeeded");
		}

		else {
			irwwbase.debugOut("<<< CC2 Failed!");
			Collections.addAll(results, conCheck2Output);
			// results.addElement("CC2 Failed: " + conCheck2Output);
		}

		results.addElement("--------------------------------------------------------------------------------"
				+ "--------------------------------------------------------------------------------");

	}

	protected void consistencyCheck3() throws FinderException {
		int failureCheck3 = -1;
		String[] conCheck3Output = null;

		Vector<String> resVec = new Vector<String>();
		try {
			getDatasource();
			checkConnection();
		} catch (Exception ex) {
			throw new EJBException(" <<< " + className + ": checkConnection() failed" + ex.getMessage());
		}
		int howMany = 0;
		try {
			String sqlString = "SELECT * FROM (SELECT NO_W_ID, NO_D_ID, MAX(NO_O_ID) - MIN(NO_O_ID) AS COMPUTED_ROWS, COUNT(*)-1 AS ACTUAL_ROWS FROM NEWORDERS GROUP BY NO_W_ID, NO_D_ID) AS CC3 WHERE COMPUTED_ROWS != ACTUAL_ROWS WITH UR";

			aPS = con.prepareStatement(sqlString);
			// PreparedStatement.
			// Execute query
			rs = aPS.executeQuery();

			while (rs.next()) {

				String result = "NO_W_ID=" + rs.getShort(1) + " NO_D_ID="
						+ rs.getShort(2) + " COMPUTED_ROWS=" + rs.getInt(3)
						+ " ACTUAL_ROWS=" + rs.getInt(4);

				System.out.println("result= " + result);
				resVec.add(result);
				globalResVec.add(result);
				howMany++;

			}
			globalHowMany = howMany;
			Enumeration<String> enum1 = resVec.elements();
			if (howMany == 0) {
				failureCheck3 = 0;
				conCheck3Output = new String[1];
				conCheck3Output[0] = "CCConCheck.Success3";
			} else {

				conCheck3Output = new String[howMany + 1];
				conCheck3Output[0] = "CCConCheck.Failed3";
				for (int i = 1; enum1.hasMoreElements(); i++) {
					conCheck3Output[i] = (String) enum1.nextElement();

				}
			}

		} catch (java.sql.SQLException ex) {

			if (ex.getNextException() != null) {
				System.out.println("getNextException = "
						+ ex.getNextException().getErrorCode());
			}

			throw new jakarta.ejb.FinderException(
					"CheckIntegrityBean.conCheck3 - JDBC query failed"
							+ ex.getMessage() + "SQLCode = "
							+ ex.getErrorCode() + "get Next Excep = ");

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new jakarta.ejb.FinderException(
					"CheckIntegrityBean.conCheck3 - JDBC query failed"
							+ ex.getMessage());
		} finally {
			try {

				if (rs != null) {
					debugOut("%%%%% Before rs.close()");
					rs.close();
				}

				if (aPS != null) {
					debugOut("%%%%% Before aPS.close()");
					aPS.close();
				}

				dropConnection();

			} catch (Exception ex) {
				throw new jakarta.ejb.FinderException(
						"CheckIntegrityBean.conCheck3 - dropConnection()"
								+ ex.getMessage());
			}
		}

		if (failureCheck3 == 0) {
			irwwbase.debugOut("<<< CC3 Succeeded!");
			results.addElement("CC3 Succeeded");
		}

		else {
			irwwbase.debugOut("<<< CC3 Failed!");
			Collections.addAll(results, conCheck3Output);
			// results.addElement("CC3 Failed: " + conCheck3Output);
		}

		results.addElement("--------------------------------------------------------------------------------"
				+ "--------------------------------------------------------------------------------");

	}

	protected void consistencyCheck4() throws FinderException {
		int failureCheck4 = -1;
		String[] conCheck4Output = null;

		Vector<String> resVec = new Vector<String>();
		try {
			getDatasource();
			checkConnection();
		} catch (Exception ex) {
			throw new EJBException("<<< " + className + ": checkConnection() failed" + ex.getMessage());
		}
		int howMany = 0;
		try {
			String sqlString = "SELECT O_W_ID, O_D_ID, OL_W_ID, OL_D_ID, O_ORDERLINES, OL_ORDERLINES FROM ((SELECT O_W_ID, O_D_ID, SUM(O_OL_CNT) AS O_ORDERLINES FROM ORDERS GROUP BY O_W_ID, O_D_ID) X FULL OUTER JOIN (SELECT OL_W_ID, OL_D_ID, COUNT(*) AS OL_ORDERLINES FROM ORDERLINE GROUP BY OL_W_ID, OL_D_ID) Y ON O_W_ID = OL_W_ID AND O_D_ID = OL_D_ID) WHERE O_ORDERLINES != OL_ORDERLINES WITH UR";

			aPS = con.prepareStatement(sqlString);

			// Execute query
			rs = aPS.executeQuery();

			while (rs.next()) {

				String result = "O_W_ID=" + rs.getShort(1) + " O_D_ID="
						+ rs.getShort(2) + " OL_W_ID=" + rs.getShort(3)
						+ " OL_D_ID=" + rs.getShort(4) + " O_ORDERLINES="
						+ rs.getInt(5) + " OL_ORDERLINES=" + rs.getInt(6);

				System.out.println("result=" + result);
				resVec.add(result);
				globalResVec.add(result);
				howMany++;
			}
			globalHowMany = howMany;
			Enumeration<String> enum1 = resVec.elements();
			if (howMany == 0) {
				failureCheck4 = 0;
				conCheck4Output = new String[1];
				conCheck4Output[0] = "CCConCheck.Success4";
			} else {
				conCheck4Output = new String[howMany + 1];
				conCheck4Output[0] = "CCConCheck.Failed4";
				for (int i = 1; enum1.hasMoreElements(); i++) {
					conCheck4Output[i] = (String) enum1.nextElement();
				}
			}

		} catch (java.sql.SQLException ex) {

			if (ex.getNextException() != null) {
				System.out.println("getNextException = "
						+ ex.getNextException().getErrorCode());
			}

			throw new jakarta.ejb.FinderException(
					"CheckIntegrityBean.conCheck4 - JDBC query failed"
							+ ex.getMessage() + "SQLCode = "
							+ ex.getErrorCode() + "get Next Excep = ");

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new jakarta.ejb.FinderException(
					"CheckIntegrityBean.conCheck4 - JDBC query failed"
							+ ex.getMessage());
		} finally {
			try {

				if (rs != null) {
					debugOut("%%%%% Before rs.close()");
					rs.close();
				}

				if (aPS != null) {
					debugOut("%%%%% Before aPS.close()");
					aPS.close();
				}

				dropConnection();

			} catch (Exception ex) {
				throw new jakarta.ejb.FinderException(
						"CheckIntegrityBean.conCheck4 - dropConnection()"
								+ ex.getMessage());
			}
		}

		if (failureCheck4 == 0) {
			irwwbase.debugOut("<<< CC4 Succeeded!");
			results.addElement("CC4 Succeeded");
		}

		else {
			irwwbase.debugOut("<<< CC4 Failed!");
			Collections.addAll(results, conCheck4Output);
			// results.addElement("CC4 Failed: " + conCheck4Output);
		}

		results.addElement("--------------------------------------------------------------------------------"
				+ "--------------------------------------------------------------------------------");

	}

	protected void consistencyCheck8() throws FinderException {
		int failureCheck8 = -1;
		String[] conCheck8Output = null;

		Vector<String> resVec = new Vector<String>();
		try {
			getDatasource();
			checkConnection();
		} catch (Exception ex) {
			throw new EJBException("conCheck8 checkConnection() failed"
					+ ex.getMessage());
		}
		int howMany = 0;
		try {
			String sqlString = "SELECT * FROM (SELECT W_ID, W_YTD, SUM(H_AMOUNT) AS SUM_H_AMOUNT FROM WAREHOUSE, HISTORY WHERE W_ID = H_W_ID GROUP BY W_ID, W_YTD) AS CC8 WHERE W_YTD != SUM_H_AMOUNT WITH UR";

			aPS = con.prepareStatement(sqlString);

			// Execute query
			rs = aPS.executeQuery();

			while (rs.next()) {

				String result = "W_ID=" + rs.getShort(1) + " W_YTD="
						+ rs.getDouble(2) + " SUM_H_AMOUNT=" + rs.getDouble(3);

				System.out.println("result=" + result);
				resVec.add(result);
				globalResVec.add(result);
				howMany++;
			}
			globalHowMany = howMany;
			Enumeration<String> enum1 = resVec.elements();
			if (howMany == 0) {
				failureCheck8 = 0;
				conCheck8Output = new String[1];
				conCheck8Output[0] = "CCConCheck.Success8";
			} else {
				conCheck8Output = new String[howMany + 1];
				conCheck8Output[0] = "CCConCheck.Failed8";
				for (int i = 1; enum1.hasMoreElements(); i++) {
					conCheck8Output[i] = (String) enum1.nextElement();
				}
			}

		} catch (java.sql.SQLException ex) {

			if (ex.getNextException() != null) {
				System.out.println("getNextException = "
						+ ex.getNextException().getErrorCode());
			}

			throw new jakarta.ejb.FinderException(
					"CheckIntegrityBean.conCheck8 - JDBC query failed"
							+ ex.getMessage() + "SQLCode = "
							+ ex.getErrorCode() + "get Next Excep = ");

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new jakarta.ejb.FinderException(
					"CheckIntegrityBean.conCheck8 - JDBC query failed"
							+ ex.getMessage());
		} finally {
			try {

				if (rs != null) {
					debugOut("%%%%% Before rs.close()");
					rs.close();
				}

				if (aPS != null) {
					debugOut("%%%%% Before aPS.close()");
					aPS.close();
				}

				dropConnection();

			} catch (Exception ex) {
				throw new jakarta.ejb.FinderException(
						"CheckIntegrityBean.conCheck8 - dropConnection()"
								+ ex.getMessage());
			}
		}

		if (failureCheck8 == 0) {
			irwwbase.debugOut("<<< CC8 Succeeded!");
			results.addElement("CC8 Succeeded");
		}

		else {
			irwwbase.debugOut("<<< CC8 Failed!");
			Collections.addAll(results, conCheck8Output);
			// results.addElement("CC8 Failed: " + conCheck8Output);
		}

		results.addElement("--------------------------------------------------------------------------------"
				+ "--------------------------------------------------------------------------------");

	}

	protected void consistencyCheck9() throws FinderException {
		int failureCheck9 = -1;
		String[] conCheck9Output = null;

		Vector<String> resVec = new Vector<String>();
		try {
			getDatasource();
			checkConnection();
		} catch (Exception ex) {
			throw new EJBException("conCheck9 checkConnection() failed"
					+ ex.getMessage());
		}
		int howMany = 0;
		try {
			String sqlString = "SELECT * FROM (SELECT D_W_ID, D_ID, D_YTD, SUM(H_AMOUNT) AS SUM_H_AMOUNT FROM DISTRICT, HISTORY WHERE D_W_ID = H_W_ID AND D_ID = H_D_ID GROUP BY D_W_ID, D_ID, D_YTD) AS CC9 WHERE D_YTD != SUM_H_AMOUNT WITH UR";

			aPS = con.prepareStatement(sqlString);

			rs = aPS.executeQuery();

			while (rs.next()) {

				String result = "D_W_ID=" + rs.getShort(1) + " D_ID="
						+ rs.getShort(2) + " D_YTD=" + rs.getDouble(3)
						+ " SUM_H_AMOUNT=" + rs.getDouble(4);

				System.out.println("result=" + result);
				resVec.add(result);
				globalResVec.add(result);
				howMany++;
			}
			globalHowMany = howMany;
			Enumeration<String> enum1 = resVec.elements();
			if (howMany == 0) {
				failureCheck9 = 0;
				conCheck9Output = new String[1];
				conCheck9Output[0] = "CCConCheck.Success9";
			} else {
				conCheck9Output = new String[howMany + 1];
				conCheck9Output[0] = "CCConCheck.Failed9";
				for (int i = 1; enum1.hasMoreElements(); i++) {
					conCheck9Output[i] = (String) enum1.nextElement();
				}
			}

		} catch (java.sql.SQLException ex) {

			if (ex.getNextException() != null) {
				System.out.println("getNextException = "
						+ ex.getNextException().getErrorCode());
			}

			throw new jakarta.ejb.FinderException(
					"CheckIntegrityBean.conCheck9 - JDBC query failed"
							+ ex.getMessage() + "SQLCode = "
							+ ex.getErrorCode() + "get Next Excep = ");

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new jakarta.ejb.FinderException(
					"CheckIntegrityBean.conCheck9 - JDBC query failed"
							+ ex.getMessage());
		} finally {
			try {

				if (rs != null) {
					debugOut("%%%%% Before rs.close()");
					rs.close();
				}

				if (aPS != null) {
					debugOut("%%%%% Before aPS.close()");
					aPS.close();
				}

				dropConnection();

			} catch (Exception ex) {
				throw new jakarta.ejb.FinderException(
						"CheckIntegrityBean.conCheck9 - dropConnection()"
								+ ex.getMessage());
			}
		}

		if (failureCheck9 == 0) {
			irwwbase.debugOut("<<< CC9 Succeeded!");
			results.addElement("CC9 Succeeded");
		}

		else {
			irwwbase.debugOut("<<< CC9 Failed!");
			Collections.addAll(results, conCheck9Output);
			// results.addElement("CC9 Failed: " + conCheck9Output);
		}

		results.addElement("--------------------------------------------------------------------------------"
				+ "--------------------------------------------------------------------------------");

	}
	
	protected void consistencyCheck13() throws FinderException {
		int failureCheck13 = -1;
		String[] conCheck13Output = null;
		String sqlString = null;

		Vector<String> resVec = new Vector<String>();
		try {
			getDatasource();
			checkConnection();
		} catch (Exception ex) {
			throw new EJBException("checkConnection() failed" + ex.getMessage());
		}
		int howMany = 0;
		
		try {			
			sqlString = "SELECT O_TRACKING_NUMBER, OT_TRACKING_NUMBER, O_ORDER_TRACKING_HOPS_COUNT, OT_ORDERTRACKING_ROWS FROM "
					+ "((SELECT O_TRACKING_NUMBER, O_TRACKING_HOPS_COUNT AS O_ORDER_TRACKING_HOPS_COUNT FROM ORDERS WHERE O_TRACKING_NUMBER IS NOT NULL AND "
						+ " O_W_ID >= " + minWarehouseId + " AND O_W_ID <= " + maxWarehouseId + " AND "
						+ " O_D_ID >= " + minDistrictId + " AND O_D_ID <= " + maxDistrictId + " ORDER BY O_TRACKING_NUMBER) "
					+ " X FULL OUTER JOIN "
					+ "(SELECT OT_TRACKING_NUMBER, COUNT(*) AS OT_ORDERTRACKING_ROWS FROM ORDER_TRACKING GROUP BY OT_TRACKING_NUMBER) "
					+ " Y ON O_TRACKING_NUMBER = OT_TRACKING_NUMBER) WHERE O_ORDER_TRACKING_HOPS_COUNT != OT_ORDERTRACKING_ROWS WITH UR";
			
			aPS = con.prepareStatement(sqlString);

			// Execute query
			rs = aPS.executeQuery();
			
			while (rs.next()) {
				String result = "O_TRACKING_NUMBER = " + rs.getString(1) + 
						" OT_TRACKING_NUMBER = " + rs.getString(2) + 
						" O_ORDER_TRACKING_HOPS_COUNT = " + rs.getInt(3) + 
						" OT_ORDERTRACKING_ROWS = " + rs.getInt(4);
				System.out.println("result=" + result);
				resVec.add(result);
				globalResVec.add(result);
				debugOut("%%%%% CC13: O_TRACKING_NUMBER = " + rs.getString(1));
				debugOut("%%%%% CC13: OT_TRACKING_NUMBER = " + rs.getString(2));
				debugOut("%%%%% CC13: O_ORDER_TRACKING_HOPS_COUNT  = " + rs.getInt(3));
				debugOut("%%%%% CC13: OT_ORDERTRACKING_ROWS  = " + rs.getInt(4));
				debugOut("%%%%% CC13: howMany  = " + howMany);
				howMany++;
			}
			globalHowMany = howMany;
			Enumeration<String> enum1 = resVec.elements();
			if (howMany == 0) {
				failureCheck13 = 0;
				conCheck13Output = new String[1];
				conCheck13Output[0] = "CCConCheck.Success13";
			} else {
				conCheck13Output = new String[howMany + 1];
				conCheck13Output[0] = "CCConCheck.Failed13";
				for (int i = 1; enum1.hasMoreElements(); i++) {
					conCheck13Output[i] = (String) enum1.nextElement();
				}
			}

		} catch (java.sql.SQLException ex) {

			if (ex.getNextException() != null) {
				System.out.println("getNextException = "
						+ ex.getNextException().getErrorCode());
			}

			throw new jakarta.ejb.FinderException(
					"CheckIntegrityBean.conCheck13 - JDBC query failed"
							+ ex.getMessage() + "SQLCode = "
							+ ex.getErrorCode() + "get Next Excep = ");

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new jakarta.ejb.FinderException(
					"CheckIntegrityBean.conCheck13 - JDBC query failed"
							+ ex.getMessage());
		} finally {
			try {

				if (rs != null) {
					debugOut("%%%%% Before rs.close()");
					rs.close();
				}

				if (aPS != null) {
					debugOut("%%%%% Before aPS.close()");
					aPS.close();
				}

				dropConnection();

			} catch (Exception ex) {
				throw new jakarta.ejb.FinderException(
						"CheckIntegrityBean.conCheck13 - dropConnection()"
								+ ex.getMessage());
			}
		}

		if (failureCheck13 == 0) {
			irwwbase.debugOut("<<< CC13 Succeeded!");
			results.addElement("CC13 Succeeded");
		}

		else {
			irwwbase.debugOut("<<< CC13 Failed!");
			Collections.addAll(results, conCheck13Output);
		}

		results.addElement("--------------------------------------------------------------------------------"
				+ "--------------------------------------------------------------------------------");

	}
	
	/****************************************************************
	 * Dispatches to the next page
	 * 
	 * @param request
	 *            The incoming request information
	 * @param response
	 *            The outgoing response information
	 * @param nextPage
	 *            The page to dispatch to
	 */
	public void dispatch(HttpServletRequest request,
			HttpServletResponse response, String nextPage)
			throws ServletException, IOException {
		RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
		dispatch.forward(request, response);
	}

	// //////////////////////// xniu, new jdbc business logic

	/**
	 * checkConnection method comment
	 * 
	 * @exception java.rmi.RemoteException
	 *                The exception description.
	 */
	private void checkConnection() throws java.rmi.RemoteException,
			java.lang.Exception {
		if (isConnected == false) {
			makeConnection();
		}
		return;
	}

	/**
	 * dropConnection method comment
	 * 
	 * @exception java.rmi.RemoteException
	 *                The exception description.
	 */
	private void dropConnection() throws java.rmi.RemoteException,
			java.lang.Exception {

		if (isConnected == true) {
			try {
				debugOut("%%%%% In dropConnection - con.getTransactionIsolation() = "
						+ con.getTransactionIsolation());

				con.close();
				con = null;

			} catch (Exception ex) {
				System.out
						.println("%%%%% ERROR: dropConnection() failed"
								+ _className);
				ex.printStackTrace();
				throw ex;
			}
		}

		isConnected = false;
	}

	/**
	 * dropDatasource method comment
	 * 
	 * @exception java.rmi.RemoteException
	 *                The exception description.
	 */
	public void dropDatasource() throws java.rmi.RemoteException,
			java.lang.Exception {
		try {
			initCtx.close();
			initCtx = null;
			ds = null;
		} catch (Exception ex) {
			System.out
					.println("%%%%% ERROR: dropDatasource() failed"
							+ _className);
			ex.printStackTrace();
			throw ex;
		}
	}

	/**
	 * getDatasource method comment
	 * 
	 * @exception java.rmi.RemoteException
	 *                The exception description.
	 */
	private synchronized DataSource getDatasource()
			throws java.rmi.RemoteException, Exception {
		if (ds == null) {
			String datasourceName = "java:comp/env/jdbc/ERWWDataSourceWithPwd";
			try {
				initCtx = new InitialContext();
				ds = (javax.sql.DataSource) initCtx.lookup(datasourceName);
			} catch (Exception ex) {
				System.out
						.println("%%%%% ERROR: getDatasource() failed"
								+ _className);
				ex.printStackTrace();
				throw ex;
			}
		}
		return ds;
	}

	/**
	 * makeConnection method comment
	 * 
	 * @exception java.rmi.RemoteException
	 *                The exception description.
	 */
	private void makeConnection() throws java.rmi.RemoteException,
			java.lang.Exception {
		debugOut("%%%%% - Entering makeConnection");
		try {
			con = ds.getConnection();

		} catch (Exception ex) {
			System.out
					.println("%%%%% ERROR: makeConnection() failed"
							+ _className);
			ex.printStackTrace();
			throw ex;
		}

		debugOut("%%%%% makeConnection() - isConnected = true");
		isConnected = true;
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
			if (System.getProperty("DEBUG_OUT").equals("ON"))
				System.out.println(message);
	}

	// end business logic

}
