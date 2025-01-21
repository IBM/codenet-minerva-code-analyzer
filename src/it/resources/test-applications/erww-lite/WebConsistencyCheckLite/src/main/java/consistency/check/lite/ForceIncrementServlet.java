package consistency.check.lite;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import jakarta.ejb.FinderException;
import javax.naming.InitialContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.Query;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.UserTransaction;
import jakarta.persistence.OptimisticLockException;

import neworderjpa.NewOrderJPA;
import neworderjpa.NewOrderKey;
import orderjpa.OrderJPA;
import orderjpa.OrderKey;
import orderlinejpa.OrderlineJPA;
import orderlinejpa.OrderlineKey;
import ordertrackingjpa.OrderTrackingJPA;
//Comment out unused imports
//import stockjpa.StockJPA;
//import stockjpa.StockKey;
import warehousejpa.WarehouseJPA;
import customerjpa.CustomerJPA;
import customerjpa.CustomerKey;
import districtjpa.DistrictJPA;
import districtjpa.DistrictKey;
import irwwbase.IRWWBase;
import irwwbase.UserException;
import itemjpa.ItemJPA;

/**
 * Servlet implementation class ForceIncrementServlet
 */
public class ForceIncrementServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit(unitName = "ConsistencyCheckJPA")
	EntityManagerFactory emf = null;

	private String className = this.getClass().toString();
	private IRWWBase irwwbase = new IRWWBase();

	private WarehouseJPA warehouse = null;
	private DistrictJPA district = null;
	private CustomerJPA customer = null;
	private OrderJPA order = null;
	private NewOrderJPA newOrder = null;
	private OrderlineJPA orderline = null;
	//Comment out unused variable
	//private StockJPA stock = null;
	private ItemJPA item = null;

	private short minWarehouseId = 0;
	private short maxWarehouseId = 0;
	private short minDistrictId = 0;
	private short maxDistrictId = 0;

	private Short numberOfRetries = 0;
	private Vector<String> results = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ForceIncrementServlet() {
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

		EntityManager em = null;

		numberOfRetries = null;
		short retryCommit = 0;
		short commitErrorCode = 0;
		boolean retryAgain = false;

		String lockMode = null;
		String consistencyCheck = null;
		String nextPage = "ConsistencyCheckResults.jsp";

		irwwbase.debugOut("<<< Entering " + className + " performTask ");

		lockMode = request.getParameter("lockMode");
		irwwbase.debugOut("<<< In " + className + " performTask: lockMode = "
				+ lockMode);

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

		numberOfRetries = Short
				.valueOf(request.getParameter("numberOfRetries"));
		irwwbase.debugOut("<<< In " + className
				+ " performTask: numberOfRetries = " + numberOfRetries);

		consistencyCheck = request.getParameter("consistencyCheckGroup");
		irwwbase.debugOut("<<< In " + className
				+ " performTask: consistencyCheck = " + consistencyCheck);

		irwwbase.debugOut("<<< In "
				+ className
				+ " performTask: before while loop for first time through or"
				+ " for retry in OFI path due to OptimisticLockException during commit");
		while ((retryCommit == 0)
				|| ((retryCommit <= numberOfRetries) && (commitErrorCode == 1) && (lockMode
						.contains("Optimistic_Force_Increment")))) {

			retryAgain = ((retryCommit <= numberOfRetries)
					&& (commitErrorCode == 1) && (lockMode
					.contains("Optimistic_Force_Increment")));

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
				em = emf.createEntityManager();

				irwwbase.debugOut("Before beginning a transaction in performTask method");
				initialContext = new InitialContext();
				userTran = (UserTransaction) initialContext
						.lookup("java:comp/UserTransaction");
				userTran.begin();
				em.joinTransaction();
			} catch (Exception e) {
				System.out
						.println("<<< "
								+ className
								+ " ForceIncrementServlet.performTask() - Begin Transaction - Exception: "
								+ e);
				results.addElement("Exception: Begin Transaction.");
				e.printStackTrace();
			}

			try {
				if ((Short.valueOf(consistencyCheck)) == 0) {
					this.consistencyCheckAll(em, lockMode);
				}
				if ((Short.valueOf(consistencyCheck)) == 1) {
					this.consistencyCheck1(em, lockMode);
				}
				if ((Short.valueOf(consistencyCheck)) == 2) {
					this.consistencyCheck2(em, lockMode);
				}
				if ((Short.valueOf(consistencyCheck)) == 3) {
					this.consistencyCheck3(em, lockMode);
				}
				if ((Short.valueOf(consistencyCheck)) == 4) {
					this.consistencyCheck4(em, lockMode);
				}
				if ((Short.valueOf(consistencyCheck)) == 8) {
					this.consistencyCheck8(em, lockMode);
				}
				if ((Short.valueOf(consistencyCheck)) == 9) {
					this.consistencyCheck9(em, lockMode);
				}
				if ((Short.valueOf(consistencyCheck)) == 13) {
					this.consistencyCheck13(em, lockMode);
				}
			} catch (Exception e) {
				System.out.println("<<< " + className
						+ " ForceIncrementServlet.performTask() - Exception: "
						+ e);
				results.addElement("Exception when running the consistency check");
				e.printStackTrace();
			}

			try {
				irwwbase.debugOut("Before commit of a transaction in performTask method");
				// em.getTransaction().commit();
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
								+ " ForceIncrementServlet.performTask() - Commit Transaction - Exception: "
								+ e);
				results.addElement("Exception when commiting the transaction");
				e.printStackTrace();
			}
		} // end while loop

		if (lockMode.contains("Optimistic_Force_Increment")) {
			results.addElement("Actual Number of Retries (due to OptimisticLockException during commit): "
					+ (retryCommit - 1));
		}

		request.setAttribute("results", results);

		try {
			if (em != null && em.isOpen()) {
				em.close();
			}

		} catch (Exception e) {
			System.out
					.println("<<< "
							+ className
							+ " ForceIncrementServlet.performTask() - em close() - Exception: "
							+ e);
			results.addElement("Exception when closing em");
			e.printStackTrace();
		}

		// Forward the request to the next page
		dispatch(request, response, nextPage);
	}

	protected void consistencyCheckAll(EntityManager em, String lockMode)
			throws ServletException, IOException, UserException,
			FinderException {
		irwwbase.debugOut("<<< Entering " + className + " consistencyCheckAll ");
		this.consistencyCheck1(em, lockMode);
		this.consistencyCheck2(em, lockMode);
		this.consistencyCheck3(em, lockMode);
		this.consistencyCheck4(em, lockMode);
		this.consistencyCheck8(em, lockMode);
		this.consistencyCheck9(em, lockMode);
		this.consistencyCheck13(em, lockMode);
	}

	protected void consistencyCheck1(EntityManager em, String lockMode)
			throws ServletException, IOException, UserException,
			FinderException {

		// xniu new code
		
		List<?> resultList1 = null;
		List<?> resultList2 = null;
		int failureCheck1 = -1;
		
		irwwbase.debugOut("<<< Entering ConCheck1 ");
		
		Query jpaQuery = null;
		Query jpaQuery1 = null;
		short retry = 0;
		int errorCode = 0;

		try {
			
			jpaQuery = em
					.createQuery("SELECT SUM(d.districtYtd) FROM WarehouseJPA w,DistrictJPA d WHERE w.warehouseId = d.districtWareId GROUP BY w.warehouseId, w.warehouseYtd");
			jpaQuery1 = em.createQuery("SELECT w from WarehouseJPA w",
					WarehouseJPA.class);

		} catch (Exception t) {
			t.printStackTrace();
			System.out.println("<<< In " + className
					+ " Concheck 1: em.createQuery failed");
			throw new UserException(
					"<&!@ failure in ConCheck1: em.createQuery failed "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		// xniu lockmode stuff

		if (lockMode.contains("Pessimistic_Force_Increment")) {

			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " consistencyCheck1: Before query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
				jpaQuery.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				jpaQuery1.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " consistencyCheck1: After query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", failure in consistencyCheck1, " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				} else {
					throw new UserException("<&!@"
							+ "failure in consistencyCheck1: "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				}
			}

		} else {

			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " consistencyCheck1: Before query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					jpaQuery.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					jpaQuery1
							.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " consistencyCheck1: After query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", failure in in consistencyCheck1, "
								+ t.getCause() + irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					} else {
						throw new UserException("<&!@"
								+ "failure in in consistencyCheck1: "
								+ " exception message: " + t.getMessage()
								+ ", exception cause: " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					}
				}
			}

		}

		// end lock mode stuff

		try {
			
			resultList1 = jpaQuery.getResultList();
			resultList2 = jpaQuery1.getResultList();
			WarehouseJPA ware = (WarehouseJPA) resultList2.get(0);

			BigDecimal res = ware.getWarehouseYtd();
			BigDecimal res1 = (BigDecimal) resultList1.get(0);
			if (res.equals(res1)) {
				failureCheck1 = 0;
			}

			else {
				failureCheck1 = -1;


				String result1 = "wid=" + ware.getWarehouseId() + " w_ytd="
						+ res + " sum_d_ytd=" + res1;
				System.out.println("JPA result=" + result1);

			}

		} catch (Exception t) {
			t.printStackTrace();

			if (irwwbase.is913(t))
				throw new UserException("<&!@" + irwwbase.checkFor913(t)
						+ ", failure in in consistencyCheck1, " + t.getCause(),
						irwwbase.getStackTrace(t));
			else
				throw new UserException("<&!@"
						+ "failure in in consistencyCheck1: "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause()
						+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		irwwbase.debugOut("<<< Exit " + className + " ConCheck1 ");

		if (failureCheck1 == 0) {
			irwwbase.debugOut("<<< CC1 Succeeded!");
			results.addElement("CC1 Succeeded");
		}

		else {

			irwwbase.debugOut("<<< CC1 Failed!");
			results.addElement("CC1 Failed");
			// Collections.addAll(results, conCheck1Output);
			// xniu results.addElement("CC1 Failed: " + conCheck1Output);
		}

		results.addElement("--------------------------------------------------------------------------------"
				+ "--------------------------------------------------------------------------------");
	}

	/*
	protected void consistencyCheck2(EntityManager em, String lockMode) throws ServletException, IOException, UserException {
	//
	//
	//
	// // xniu new code
	//
	// List resultList = null;
	// int failureCheck1 = -1;
	// String[] conCheck1Output = null;
	// irwwbase.debugOut("<<< Entering ConCheck2 ");
	//
	// Query query = null;
	// short retry = 0;
	// int errorCode = 0;
	//
	// try {
	// // query =
	// //
	// em.createQuery("SELECT * FROM (SELECT W_ID, W_YTD, SUM(D_YTD) AS SUM_D_YTD FROM WAREHOUSE, DISTRICT WHERE W_ID = D_W_ID GROUP BY W_ID, W_YTD) AS CC1 WHERE W_YTD!= SUM_D_YTD");
	// // query = em.createNativeQuery("SELECT * FROM WAREHOUSE");
	// query = em
	// .createNativeQuery("SELECT D_W_ID, D_ID, D_O_ID, O_ID, NO_O_ID FROM ((SELECT D_W_ID, D_ID, D_NEXT_O_ID - 1 AS D_O_ID FROM DISTRICT GROUP BY D_W_ID, D_ID, D_NEXT_O_ID) X FULL OUTER JOIN (SELECT O_W_ID, O_D_ID, MAX(O_ID) AS O_ID FROM ORDERS GROUP BY O_W_ID, O_D_ID) Y ON D_W_ID = O_W_ID AND D_ID = O_D_ID FULL OUTER JOIN (SELECT NO_W_ID, NO_D_ID, MAX(NO_O_ID) AS NO_O_ID FROM NEWORDERS GROUP BY NO_W_ID, NO_D_ID) Z ON D_W_ID = NO_W_ID AND D_ID = NO_D_ID) WHERE ((D_O_ID != O_ID) OR (O_ID != NO_O_ID)) WITH UR");
	// } catch (Exception t) {
	// t.printStackTrace();
	// System.out.println("<<< In " + className
	// + " Concheck 2: em.createQuery failed");
	// throw new UserException(
	// "<&!@ failure in ConCheck2: em.createQuery failed "
	// + " exception message: " + t.getMessage()
	// + ", exception cause: " + t.getCause()
	// + irwwbase.getSTC(), irwwbase.getStackTrace(t));
	// }
	//
	//xniu lockmode stuff
	if (lockMode.contains("Pessimistic_Force_Increment")){
	try { irwwbase.debugOut("<<< In " + className +
	
	" consistencyCheck2: Before query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)"
	); query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
	irwwbase.debugOut("<<< In " + className +
	
	" consistencyCheck2: After query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)"
	); } catch (Exception t) { t.printStackTrace(); if
	(irwwbase.is913(t)){ throw new
	UserException("<&!@"+irwwbase.checkFor913
	(t)+", failure in consistencyCheck2, " +
	t.getCause()+irwwbase.getSTC(), irwwbase.getStackTrace(t)); }else{
	throw new UserException("<&!@"+"failure in consistencyCheck2: "
	+" exception message: "+t.getMessage()+", exception cause: "+
	t.getCause() +irwwbase.getSTC(), irwwbase.getStackTrace(t)); } }
	} else{
	while ((retry == 0) || ((retry <= numberOfRetries) &&
	(errorCode==1))) { try { retry++; irwwbase.debugOut("<<< In " +
	className +
	
	" consistencyCheck2: Before query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)"
	); query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
	irwwbase.debugOut("<<< In " + className +
	
	" consistencyCheck2: After query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)"
	); } catch (OptimisticLockException t) {
	System.out.println("OptimisticLockException: retry = " + retry +
	" query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
	errorCode = 1; } catch (Exception t) { t.printStackTrace(); errorCode
	= 2; if (irwwbase.is913(t)){ throw new
	UserException("<&!@"+irwwbase.checkFor913
	(t)+", failure in in consistencyCheck2, " +
	t.getCause()+irwwbase.getSTC(), irwwbase.getStackTrace(t)); }else{
	throw new UserException("<&!@"+"failure in in consistencyCheck2: "
	+" exception message: "+t.getMessage()+", exception cause: "+
	t.getCause() +irwwbase.getSTC(), irwwbase.getStackTrace(t)); } } }
	}
	// end lock mode stuff
	//
	// try {
	// resultList = query.getResultList();
	// // System.out.println(Arrays.toString(resultList.toArray()));
	//
	// if (resultList.isEmpty()) {
	// failureCheck1 = 0;
	// }
	//
	// } catch (Exception t) {
	// t.printStackTrace();
	//
	// if (irwwbase.is913(t))
	// throw new UserException("<&!@" + irwwbase.checkFor913(t)
	// + ", failure in in consistencyCheck1, " + t.getCause(),
	// irwwbase.getStackTrace(t));
	// else
	// throw new UserException("<&!@"
	// + "failure in in consistencyCheck1: "
	// + " exception message: " + t.getMessage()
	// + ", exception cause: " + t.getCause()
	// + irwwbase.getSTC(), irwwbase.getStackTrace(t));
	// }
	//
	// irwwbase.debugOut("<<< Exit " + className + " ConCheck2 ");
	//
	// if (failureCheck1 == 0) {
	// irwwbase.debugOut("<<< CC2 Succeeded!");
	// results.addElement("CC2 Succeeded");
	// }
	//
	// else {
	//
	// irwwbase.debugOut("<<< CC2 Failed!");
	// results.addElement("CC2 Failed");
	// // Collections.addAll(results, conCheck1Output);
	// // xniu results.addElement("CC1 Failed: " + conCheck1Output);
	// }
	//
	// results.addElement("--------------------------------------------------------------------------------"
	// +
	// "--------------------------------------------------------------------------------");
	//
	// }

	// xniu- sql statement unable to be converted to jpql to increase efficiency
	// sql statement incorporates FULL OUTER JOIN, which is a combination of
	// right and left outer joins
	// JPA does not currently support RIGHT OUTER JOIN
	// Once JPA does support FULL OUTER JOIN, possible to change to jpql
	// statement to increase efficiency*/
	
	protected void consistencyCheck2(EntityManager em, String lockMode)
			throws ServletException, IOException, UserException {

		// D_NEXT_O_ID - 1 = max(O_ID) = max(NO_O_ID) - for each district
		irwwbase.debugOut("<<< Entering " + className + " consistencyCheck2 ");

		List<?> resultList = null;
		int districtNextOrderId = 0;
		int maxOrderIdOrder = 0;
		int maxOrderIdNewOrder = 0;
		Vector<Integer> cc2Results = new Vector<Integer>();
		Query jpaQuery = null;
		int newOrderCheck = -1;

		// handle case where there are no new orders
		try {

			jpaQuery = em
					.createQuery("SELECT Count(n) from NewOrderJPA n GROUP BY n.warehouseId, n.districtId");

		} catch (Exception t) {
			t.printStackTrace();
			System.out.println("<<< In " + className
					+ " Concheck 1: em.createQuery failed");
			throw new UserException(
					"<&!@ failure in ConCheck1: em.createQuery failed "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		try {

			resultList = jpaQuery.getResultList();

			if (resultList.size() == 0) {
				System.out
						.println("There are no new orders. Number of NewOrders = "
								+ resultList.size());

			}

			if (resultList.size() > 0) {
				newOrderCheck = 0;
			}

		} catch (Exception t) {
			t.printStackTrace();

			if (irwwbase.is913(t))
				throw new UserException("<&!@" + irwwbase.checkFor913(t)
						+ ", failure in in consistencyCheck3, " + t.getCause(),
						irwwbase.getStackTrace(t));
			else
				throw new UserException("<&!@"
						+ "failure in in consistencyCheck3: "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause()
						+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		// end case handling

		if (newOrderCheck == 0) {

			try {
				for (short w = minWarehouseId; w <= maxWarehouseId; w++) {
					for (short d = minDistrictId; d <= maxDistrictId; d++) {

						this.getDistrictInstance(em, d, w, lockMode);
						districtNextOrderId = district.getDistrictNextOrderId();
						irwwbase.debugOut("<<< " + className
								+ " CC2: districtNextOrderId =  "
								+ districtNextOrderId);

						maxOrderIdOrder = this
								.queryMaxOrderIdFromOrderInstances(em, d, w,
										lockMode);
						irwwbase.debugOut("<<< " + className
								+ " CC2: maxOrderIdOrder =  " + maxOrderIdOrder);

						maxOrderIdNewOrder = this
								.queryMaxOrderIdFromNewOrderInstances(em, d, w,
										lockMode);
						irwwbase.debugOut("<<< " + className
								+ " CC2: maxOrderIdNewOrder =  "
								+ maxOrderIdNewOrder);

						if ( (((districtNextOrderId - 1) == maxOrderIdOrder) && (maxOrderIdOrder == maxOrderIdNewOrder)) ) {
							cc2Results.addElement(1);
							irwwbase.debugOut("<<< CC2: warehouseId " + w
									+ " district " + d + " passed!");
						} else {
							if ((w == minWarehouseId) && (d == minDistrictId)) {
								results.addElement("CC2 Failed");
							}
							
							// No new orders displays an explanation for failure.
							if (((districtNextOrderId - 1) == maxOrderIdOrder) && (maxOrderIdNewOrder == 0)) {
								results.addElement("*** CC2: warehouseId " + w
										+ ": districtId " + d + " failed"
										+ ": districtNextOrderId = "
										+ districtNextOrderId
										+ ": maxOrderIdOrder= " + maxOrderIdOrder
										+ ": maxOrderIdNewOrder = "
										+ maxOrderIdNewOrder + "\n"
										+ "WARNING: There are no new orders. Reload the ERWW tables and ensure the workload script is executing 10 NewOrder transactions per 1 Delivery transaction.");
							} else {
								results.addElement("*** CC2: warehouseId " + w
										+ ": districtId " + d + " failed"
										+ ": districtNextOrderId = "
										+ districtNextOrderId
										+ ": maxOrderIdOrder= " + maxOrderIdOrder
										+ ": maxOrderIdNewOrder = "
										+ maxOrderIdNewOrder);
							}
							irwwbase.debugOut("<<< CC2: warehouseId " + w
									+ " district " + d + " failed!");
						}
					}
				}
				if (cc2Results.size() == ((maxWarehouseId - minWarehouseId + 1) * (maxDistrictId
						- minDistrictId + 1))) {
					irwwbase.debugOut("<<< CC2 Succeeded!");
					results.addElement("CC2 Succeeded");
				} else {
					irwwbase.debugOut("<<< CC2 Failed!");
				}
				results.addElement("--------------------------------------------------------------------------------"
						+ "--------------------------------------------------------------------------------");
			} catch (Exception e) {
				System.out
						.println("<<< "
								+ className
								+ " ForceIncrementServlet.consistencyCheck2() - Exception: "
								+ e);
				results.addElement("<<< "
						+ className
						+ " ForceIncrementServlet.consistencyCheck2() - Exception: "
						+ e);
				e.printStackTrace();
				throw new UserException(
						"<<< "
								+ className
								+ " ForceIncrementServlet.consistencyCheck2() - Exception: "
								+ e);
			}

		}

		else {
			irwwbase.debugOut("<<< CC2 Succeeded!");
			results.addElement("CC2 Succeeded");
		}
	}

	protected void consistencyCheck3(EntityManager em, String lockMode)
			throws ServletException, IOException, UserException {

		// xniu new code

		List<?> resultList = null;
		//Comment out unused variable
		//List<?> resultList1 = null;
		List<?> resultList2 = null;

		int failureCheck1 = -1;
		//Comment out unused variable
		//String[] conCheck1Output = null;
		irwwbase.debugOut("<<< Entering ConCheck3 ");

		//Comment out unused variable
		//Query query = null;

		Query jpaQuery = null;
		//Comment out unused variable
		//Query jpaQuery1 = null;
		Query jpaQuery2 = null;

		short retry = 0;
		int errorCode = 0;

		try {

			// jpaQuery = em
			// .createQuery("SELECT SUM(h.historyAmount) FROM HistoryJPA h, DistrictJPA d WHERE d.districtWareId = h.historyWId AND d.districtId = h.historyDId GROUP BY d.districtWareId, d.districtId, d.districtYtd");

			jpaQuery = em
					.createQuery("SELECT MAX(n.orderId) - MIN(n.orderId) FROM NewOrderJPA n GROUP BY n.warehouseId, n.districtId");
			// jpaQuery1 = em
			// .createQuery("SELECT MIN(n.orderId) FROM NewOrderJPA n GROUP BY n.warehouseId, n.districtId");
			jpaQuery2 = em
					.createQuery("SELECT Count(n) from NewOrderJPA n GROUP BY n.warehouseId, n.districtId");

			// query = em
			// .createNativeQuery("SELECT NO_W_ID, NO_D_ID, MAX(NO_O_ID) - MIN(NO_O_ID) AS COMPUTED_ROWS, COUNT(*)-1 AS ACTUAL_ROWS FROM NEWORDERS GROUP BY NO_W_ID, NO_D_ID");
		} catch (Exception t) {
			t.printStackTrace();
			System.out.println("<<< In " + className
					+ " Concheck 1: em.createQuery failed");
			throw new UserException(
					"<&!@ failure in ConCheck1: em.createQuery failed "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		// xniu lockmode stuff

		if (lockMode.contains("Pessimistic_Force_Increment")) {

			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " consistencyCheck3: Before query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
				jpaQuery.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				// jpaQuery1.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				jpaQuery2.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " consistencyCheck3: After query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", failure in consistencyCheck3, " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				} else {
					throw new UserException("<&!@"
							+ "failure in consistencyCheck3: "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				}
			}

		} else {

			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " consistencyCheck3: Before query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					jpaQuery.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					// jpaQuery1
					// .setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					jpaQuery2
							.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " consistencyCheck3: After query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", failure in in consistencyCheck3, "
								+ t.getCause() + irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					} else {
						throw new UserException("<&!@"
								+ "failure in in consistencyCheck3: "
								+ " exception message: " + t.getMessage()
								+ ", exception cause: " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					}
				}
			}

		}

		// end lock mode stuff

		try {
			// MAX-MIN
			resultList = jpaQuery.getResultList();
			irwwbase.debugOut("MAX-MIN=" + resultList.size());

			// MIN
			// resultList1 = jpaQuery1.getResultList();
			// System.out.println("MIN"+resultList1.size());

			// Count
			resultList2 = jpaQuery2.getResultList();
			irwwbase.debugOut("COUNT=" + resultList2.size());

			irwwbase.debugOut("JPA results: COMPUTED_ROWS="
					+ (resultList.size()) + " ACTUAL_ROWS="
					+ (resultList2.size()));

			if (resultList.size() == resultList2.size()) {
				failureCheck1 = 0;
			} else {
				System.out.println("JPA results: COMPUTED_ROWS="
						+ (resultList.size()) + " ACTUAL_ROWS="
						+ (resultList2.size()));

			}
			// if (ress==null) {
			// failureCheck1 = 0;
			// }
			// else{
			// ConCheck3Entity res = (ConCheck3Entity) ress;
			// System.out.println("SQL results: Computed Rows"+
			// res.getComputedRows()+"Actual Rows"+ res.getActualRows());
			//
			// }

		} catch (Exception t) {
			t.printStackTrace();

			if (irwwbase.is913(t))
				throw new UserException("<&!@" + irwwbase.checkFor913(t)
						+ ", failure in in consistencyCheck3, " + t.getCause(),
						irwwbase.getStackTrace(t));
			else
				throw new UserException("<&!@"
						+ "failure in in consistencyCheck3: "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause()
						+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		irwwbase.debugOut("<<< Exit " + className + " ConCheck3 ");

		if (failureCheck1 == 0) {
			irwwbase.debugOut("<<< CC3 Succeeded!");
			results.addElement("CC3 Succeeded");
		}

		else {

			irwwbase.debugOut("<<< CC3 Failed!");
			results.addElement("CC3 Failed");
			// Collections.addAll(results, conCheck1Output);
			// xniu results.addElement("CC1 Failed: " + conCheck1Output);
		}

		results.addElement("--------------------------------------------------------------------------------"
				+ "--------------------------------------------------------------------------------");

	}

	/*
	protected void consistencyCheck4(EntityManager em, String lockMode)
	throws ServletException, IOException, UserException {
	
	// xniu new code
	
	List resultList = null;
	int failureCheck1 = -1;
	String[] conCheck1Output = null;
	irwwbase.debugOut("<<< Entering ConCheck4 ");
	
	Query query = null;
	short retry = 0;
	int errorCode = 0;
	
	try {
	// query = em.createQuery("SELECT * FROM (SELECT W_ID, W_YTD, SUM(D_YTD) AS SUM_D_YTD FROM WAREHOUSE, DISTRICT WHERE W_ID = D_W_ID GROUP BY W_ID, W_YTD) AS CC1 WHERE W_YTD!= SUM_D_YTD");
	// query = em.createNativeQuery("SELECT * FROM WAREHOUSE");
	// query = em.createNativeQuery("SELECT O_W_ID, O_D_ID, OL_W_ID, OL_D_ID, O_ORDERLINES, OL_ORDERLINES FROM ((SELECT O_W_ID, O_D_ID, SUM(O_OL_CNT) AS O_ORDERLINES FROM ORDERS GROUP BY O_W_ID, O_D_ID) X FULL OUTER JOIN (SELECT OL_W_ID, OL_D_ID, COUNT(*) AS OL_ORDERLINES FROM ORDERLINE GROUP BY OL_W_ID, OL_D_ID) Y ON O_W_ID = OL_W_ID AND O_D_ID = OL_D_ID) WHERE O_ORDERLINES != OL_ORDERLINES WITH UR");
	} catch (Exception t) {
		t.printStackTrace();
		System.out.println("<<< In " + className
		+ " Concheck 4: em.createQuery failed");
		throw new UserException(
		"<&!@ failure in ConCheck4: em.createQuery failed "
		+ " exception message: " + t.getMessage()
		+ ", exception cause: " + t.getCause()
		+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
	}

	//xniu lockmode stuff
	if (lockMode.contains("Pessimistic_Force_Increment")){
		try {
			irwwbase.debugOut("<<< In " + className
			+ " consistencyCheck4: Before query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)"
			); query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
			irwwbase.debugOut("<<< In " + className
			+ " consistencyCheck4: After query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)"
			);
		} catch (Exception t) {
			t.printStackTrace();
			if (irwwbase.is913(t)){
				throw new UserException("<&!@"+irwwbase.checkFor913(t)+", failure in consistencyCheck4, " + t.getCause()+irwwbase.getSTC(), irwwbase.getStackTrace(t));
			} else {
			throw new UserException("<&!@"+"failure in consistencyCheck4: "
									+" exception message: "+t.getMessage()+", exception cause: "
									+t.getCause() +irwwbase.getSTC(), irwwbase.getStackTrace(t));
			}
		}
	} else {
		while ((retry == 0) || ((retry <= numberOfRetries) && (errorCode==1))) {
			try {
				retry++;
				irwwbase.debugOut("<<< In " + className
									+ " consistencyCheck4: Before query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
				query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In " + className + " consistencyCheck4: After query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
			} catch (OptimisticLockException t) {
				System.out.println("OptimisticLockException: retry = " + retry + " query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
				errorCode = 1;
			} catch (Exception t) {
				t.printStackTrace();
				errorCode = 2;
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@"+irwwbase.checkFor913(t)+", failure in in consistencyCheck4, " + t.getCause() + irwwbase.getSTC(), irwwbase.getStackTrace(t));
				} else {
					throw new UserException("<&!@"+"failure in in consistencyCheck4: "+" exception message: "+t.getMessage()+", exception cause: "+t.getCause() +irwwbase.getSTC(), irwwbase.getStackTrace(t));
				}
			}
		}
	} // end lock mode stuff

	// try {
	// resultList = query.getResultList();
	// // System.out.println(Arrays.toString(resultList.toArray()));
	//
	// if (resultList.isEmpty()) {
	// failureCheck1 = 0;
	// }
	//
	// } catch (Exception t) {
	// t.printStackTrace();
	//
	// if (irwwbase.is913(t))
	// throw new UserException("<&!@" + irwwbase.checkFor913(t)
	// + ", failure in in consistencyCheck4, " + t.getCause(),
	// irwwbase.getStackTrace(t));
	// else
	// throw new UserException("<&!@"
	// + "failure in in consistencyCheck4: "
	// + " exception message: " + t.getMessage()
	// + ", exception cause: " + t.getCause()
	// + irwwbase.getSTC(), irwwbase.getStackTrace(t));
	// }
	//
	// irwwbase.debugOut("<<< Exit " + className + " ConCheck4 ");
	//
	// if (failureCheck1 == 0) {
	// irwwbase.debugOut("<<< CC4 Succeeded!");
	// results.addElement("CC4 Succeeded");
	// }
	//
	// else {
	//
	// irwwbase.debugOut("<<< CC4 Failed!");
	// results.addElement("CC4 Failed");
	// // Collections.addAll(results, conCheck1Output);
	// // xniu results.addElement("CC1 Failed: " + conCheck1Output);
	// }
	//
	// results.addElement("--------------------------------------------------------------------------------"
	// +
	// "--------------------------------------------------------------------------------");
	//
	// }*/

	// xniu- sql statement unable to be converted to jpql to increase efficiency
	// sql statement incorporates FULL OUTER JOIN, which is a combination of
	// right and left outer joins
	// JPA does not currently support RIGHT OUTER JOIN
	// Once JPA does support FULL OUTER JOIN, possible to change to jpql
	// statement to increase efficiency
	protected void consistencyCheck4(EntityManager em, String lockMode)
			throws ServletException, IOException, UserException {

		// sum(O_OL_CNT) = [number of rows in the ORDER-LINE table for this
		// district] for each district
		irwwbase.debugOut("<<< Entering " + className + " consistencyCheck4 ");
		BigDecimal sumOrderlineCountOrder = new BigDecimal(0);
		BigDecimal numberRowsOrderline = new BigDecimal(0);
		Vector<Integer> cc4Results = new Vector<Integer>();

		try {
			for (short w = minWarehouseId; w <= maxWarehouseId; w++) {
				for (short d = minDistrictId; d <= maxDistrictId; d++) {

					sumOrderlineCountOrder = this
							.queryOrderlineCountFromOrderInstances(em, d, w,
									lockMode);
					irwwbase.debugOut("<<< " + className
							+ " CC4: sumOrderlineCountOrder =  "
							+ sumOrderlineCountOrder);

					numberRowsOrderline = this
							.queryNumberRowsFromOrderlineInstances(em, d, w,
									lockMode);
					irwwbase.debugOut("<<< " + className
							+ " CC4: numberRowsOrderline =  "
							+ numberRowsOrderline);

					if (sumOrderlineCountOrder.equals(numberRowsOrderline)) {
						cc4Results.addElement(1);
						irwwbase.debugOut("<<< CC4: warehouseId " + w
								+ " district " + d + " passed!");
					} else {
						if ((w == minWarehouseId) && (d == minDistrictId)) {
							results.addElement("CC4 Failed");
						}
						results.addElement("*** CC4: warehouseId " + w
								+ " and districtId " + d + " failed"
								+ ": sumOrderlineCountOrder = "
								+ sumOrderlineCountOrder
								+ ": numberRowsOrderline = "
								+ numberRowsOrderline);
						irwwbase.debugOut("<<< CC4: warehouseId " + w
								+ " district " + d + " failed!");
					}
				}
			}
			if (cc4Results.size() == ((maxWarehouseId - minWarehouseId + 1) * (maxDistrictId - minDistrictId + 1))) {
				irwwbase.debugOut("<<< CC4 Succeeded!");
				results.addElement("CC4 Succeeded");
			} else {
				irwwbase.debugOut("<<< CC4 Failed!");
			}
			results.addElement("--------------------------------------------------------------------------------"
					+ "--------------------------------------------------------------------------------");
		} catch (Exception e) {
			System.out
					.println("<<< "
							+ className
							+ " ForceIncrementServlet.consistencyCheck4() - Exception: "
							+ e);
			results.addElement("<<< "
					+ className
					+ " ForceIncrementServlet.consistencyCheck4() - Exception: "
					+ e);
			e.printStackTrace();
			throw new UserException(
					"<<< "
							+ className
							+ " ForceIncrementServlet.consistencyCheck4() - Exception: "
							+ e);
		}
	}

	protected void consistencyCheck8(EntityManager em, String lockMode)
			throws ServletException, IOException, UserException {

		// xniu new code

		// List resultList = null;

		List<?> resultList1 = null;
		List<?> resultList2 = null;

		int failureCheck1 = -1;
		//Comment out unused variable
		//String[] conCheck1Output = null;
		irwwbase.debugOut("<<< Entering ConCheck8 ");

		//Comment out unused variable
		//Query query = null;
		short retry = 0;
		int errorCode = 0;

		Query jpaQuery = null;
		Query jpaQuery1 = null;

		try {
			// query =
			// em.createQuery("SELECT * FROM (SELECT W_ID, W_YTD, SUM(D_YTD) AS SUM_D_YTD FROM WAREHOUSE, DISTRICT WHERE W_ID = D_W_ID GROUP BY W_ID, W_YTD) AS CC1 WHERE W_YTD!= SUM_D_YTD");
			// query = em.createNativeQuery("SELECT * FROM WAREHOUSE");

			jpaQuery = em
					.createQuery("SELECT SUM(h.historyAmount) FROM HistoryJPA h, WarehouseJPA w WHERE w.warehouseId = h.historyWId GROUP BY w.warehouseId, w.warehouseYtd");
			jpaQuery1 = em.createQuery("SELECT w from WarehouseJPA w",
					WarehouseJPA.class);

			// query = em
			// .createNativeQuery("SELECT * FROM (SELECT W_ID, W_YTD, SUM(H_AMOUNT) AS SUM_H_AMOUNT FROM WAREHOUSE, HISTORY WHERE W_ID = H_W_ID GROUP BY W_ID, W_YTD) AS CC8 WHERE W_YTD != SUM_H_AMOUNT WITH UR");
		} catch (Exception t) {
			t.printStackTrace();
			System.out.println("<<< In " + className
					+ " Concheck 8: em.createQuery failed");
			throw new UserException(
					"<&!@ failure in ConCheck8: em.createQuery failed "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		// xniu lockmode stuff

		if (lockMode.contains("Pessimistic_Force_Increment")) {

			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " consistencyCheck8: Before query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
				jpaQuery.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				jpaQuery1.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " consistencyCheck8: After query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", failure in consistencyCheck8, " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				} else {
					throw new UserException("<&!@"
							+ "failure in consistencyCheck8: "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				}
			}

		} else {

			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " consistencyCheck8: Before query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					jpaQuery.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					jpaQuery1
							.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " consistencyCheck8: After query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", failure in in consistencyCheck8, "
								+ t.getCause() + irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					} else {
						throw new UserException("<&!@"
								+ "failure in in consistencyCheck8: "
								+ " exception message: " + t.getMessage()
								+ ", exception cause: " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					}
				}
			}

		}

		// end lock mode stuff

		try {
			// resultList = query.getResultList();

			resultList1 = jpaQuery.getResultList();
			resultList2 = jpaQuery1.getResultList();
			WarehouseJPA ware = (WarehouseJPA) resultList2.get(0);

			BigDecimal res = ware.getWarehouseYtd();
			BigDecimal res1 = (BigDecimal) resultList1.get(0);

			if (res.equals(res1)) {
				failureCheck1 = 0;
			}

			else {
				failureCheck1 = -1;

				System.out.println("JPA result: W_ID=" + ware.getWarehouseId()
						+ " W_YTD=" + ware.getWarehouseYtd() + " SUM_H_AMOUNT="
						+ res1);

			}

			// System.out.println(Arrays.toString(resultList.toArray()));

		} catch (Exception t) {
			t.printStackTrace();

			if (irwwbase.is913(t))
				throw new UserException("<&!@" + irwwbase.checkFor913(t)
						+ ", failure in in consistencyCheck8, " + t.getCause(),
						irwwbase.getStackTrace(t));
			else
				throw new UserException("<&!@"
						+ "failure in in consistencyCheck8: "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause()
						+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		irwwbase.debugOut("<<< Exit " + className + " ConCheck8 ");

		if (failureCheck1 == 0) {
			irwwbase.debugOut("<<< CC8 Succeeded!");
			results.addElement("CC8 Succeeded");
		}

		else {

			irwwbase.debugOut("<<< CC8 Failed!");
			results.addElement("CC8 Failed");
			// Collections.addAll(results, conCheck1Output);
			// xniu results.addElement("CC1 Failed: " + conCheck1Output);
		}

		results.addElement("--------------------------------------------------------------------------------"
				+ "--------------------------------------------------------------------------------");

	}

	/*****
	 * xniu - notes***** JPA does not support WITH UR functionality yet OpenJPA
	 * has a query constant for UPDATE WITH UR, but it is not ever used. Update
	 * cc9 when this functionality has been implemented
	 * 
	 * Currently this test case is using a native query to obtain desired
	 * results.
	 * 
	 * *
	 */
	protected void consistencyCheck9(EntityManager em, String lockMode)
			throws ServletException, IOException, UserException {

		// xniu new code

		List<?> resultList = null;
		// List resultList1 = null;
		// List resultList2 = null;

		int failureCheck1 = -1;
		// String[] conCheck1Output = null;
		irwwbase.debugOut("<<< Entering ConCheck9 ");

		Query query = null;
		// short retry = 0;
		// int errorCode = 0;

		// Query jpaQuery = null;
		// Query jpaQuery1 = null;

		try {
			// query =
			// em.createQuery("SELECT * FROM (SELECT W_ID, W_YTD, SUM(D_YTD) AS SUM_D_YTD FROM WAREHOUSE, DISTRICT WHERE W_ID = D_W_ID GROUP BY W_ID, W_YTD) AS CC1 WHERE W_YTD!= SUM_D_YTD");
			// query = em.createNativeQuery("SELECT * FROM WAREHOUSE");

			/*
			 * jpaQuery = em .createQuery(
			 * "SELECT SUM(h.historyAmount) FROM HistoryJPA h, DistrictJPA d WHERE d.districtWareId = h.historyWId AND d.districtId = h.historyDId GROUP BY d.districtWareId, d.districtId, d.districtYtd"
			 * );
			 * 
			 * jpaQuery1 = em.createQuery("SELECT d from DistrictJPA d",
			 * DistrictJPA.class);
			 */

			// query =
			// em.createNativeQuery("SELECT SUM(H_AMOUNT) FROM DISTRICT, HISTORY WHERE D_W_ID = H_W_ID AND D_ID = H_D_ID GROUP BY D_W_ID, D_ID, D_YTD WITH UR");
			// returns an object, might need to make entity class
			// query =
			// em.createNativeQuery("SELECT D_W_ID, D_ID, D_YTD, SUM(H_AMOUNT) AS SUM_H_AMOUNT FROM DISTRICT, HISTORY WHERE D_W_ID = H_W_ID AND D_ID = H_D_ID GROUP BY D_W_ID, D_ID, D_YTD WITH UR",
			// ConCheck9Entity.class);
			query = em
					.createNativeQuery(
							"SELECT * FROM (SELECT D_W_ID, D_ID, D_YTD, SUM(H_AMOUNT) AS SUM_H_AMOUNT FROM DISTRICT, HISTORY WHERE D_W_ID = H_W_ID AND D_ID = H_D_ID GROUP BY D_W_ID, D_ID, D_YTD) AS CC9 WHERE D_YTD != SUM_H_AMOUNT WITH UR",
							ConCheck9Entity.class);

			/*
			 * query = em .createNativeQuery(
			 * "SELECT * FROM (SELECT D_W_ID, D_ID, D_YTD, SUM(H_AMOUNT) AS SUM_H_AMOUNT FROM DISTRICT, HISTORY WHERE D_W_ID = H_W_ID AND D_ID = H_D_ID GROUP BY D_W_ID, D_ID, D_YTD) AS CC9 WHERE D_YTD != SUM_H_AMOUNT WITH UR"
			 * );
			 */
		} catch (Exception t) {
			t.printStackTrace();
			System.out.println("<<< In " + className
					+ " Concheck 9: em.createQuery failed");
			throw new UserException(
					"<&!@ failure in ConCheck9: em.createQuery failed "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		/*
		 * // xniu lockmode stuff
		 * 
		 * if (lockMode.contains("Pessimistic_Force_Increment")) {
		 * 
		 * try { irwwbase.debugOut("<<< In " + className +
		 * " consistencyCheck9: Before query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)"
		 * ); jpaQuery.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
		 * jpaQuery1.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
		 * irwwbase.debugOut("<<< In " + className +
		 * " consistencyCheck9: After query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)"
		 * ); } catch (Exception t) { t.printStackTrace(); if
		 * (irwwbase.is913(t)) { throw new UserException("<&!@" +
		 * irwwbase.checkFor913(t) + ", failure in consistencyCheck9, " +
		 * t.getCause() + irwwbase.getSTC(), irwwbase.getStackTrace(t)); } else
		 * { throw new UserException("<&!@" + "failure in consistencyCheck9: " +
		 * " exception message: " + t.getMessage() + ", exception cause: " +
		 * t.getCause() + irwwbase.getSTC(), irwwbase.getStackTrace(t)); } }
		 * 
		 * } else {
		 * 
		 * while ((retry == 0) || ((retry <= numberOfRetries) && (errorCode ==
		 * 1))) { try { retry++; irwwbase.debugOut("<<< In " + className +
		 * " consistencyCheck9: Before query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)"
		 * ); jpaQuery.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
		 * jpaQuery1 .setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
		 * irwwbase.debugOut("<<< In " + className +
		 * " consistencyCheck9: After query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)"
		 * ); } catch (OptimisticLockException t) { System.out
		 * .println("OptimisticLockException: retry = " + retry +
		 * " query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
		 * errorCode = 1; } catch (Exception t) { t.printStackTrace(); errorCode
		 * = 2; if (irwwbase.is913(t)) { throw new UserException("<&!@" +
		 * irwwbase.checkFor913(t) + ", failure in in consistencyCheck9, " +
		 * t.getCause() + irwwbase.getSTC(), irwwbase.getStackTrace(t)); } else
		 * { throw new UserException("<&!@" +
		 * "failure in in consistencyCheck9: " + " exception message: " +
		 * t.getMessage() + ", exception cause: " + t.getCause() +
		 * irwwbase.getSTC(), irwwbase.getStackTrace(t)); } } }
		 * 
		 * }
		 * 
		 * // end lock mode stuff
		 */
		try {
			// resultList = query.getResultList();
			// System.out.println(Arrays.toString(resultList.toArray()));

			// native query
			resultList = query.getResultList();

			/*
			 * //sum of history resultList1 = jpaQuery.getResultList();
			 * 
			 * //district total resultList2 = jpaQuery1.getResultList();
			 * 
			 * 
			 * DistrictJPA dis = (DistrictJPA) resultList2.get(0);
			 */

			/*
			 * //district BigDecimal res = dis.getDistrictYtd();
			 * 
			 * //sum BigDecimal res1 = (BigDecimal) resultList1.get(0);
			 */

			if (resultList.size() == 0) {
				failureCheck1 = 0;
			}

			else {
				ConCheck9Entity resNative = (ConCheck9Entity) resultList.get(0);
				failureCheck1 = -1;

				System.out.println("NATIVE query result: Sum Amount="
						+ resNative.getSumAmount() + " D_YTD="
						+ resNative.getDistrictYtd() + " D_W_ID="
						+ resNative.getDistrictWareId() + " D_ID="
						+ resNative.getDistrictId());

				/*
				 * System.out.println("JPA result: D_W_ID=" +
				 * dis.getDistrictWareId() + " D_ID=" + dis.getDistrictId() +
				 * " D_YTD=" + dis.getDistrictYtd() + " SUM_H_AMOUNT=" + res1);
				 */

			}

			// else{
			// ConCheck9Entity res0 = (ConCheck9Entity) resultList.get(0);
			//
			// String result = "D_W_ID=" + res0.getDistrictWareId() + " D_ID="
			// + res0.getDistrictId() + " D_YTD=" + res0.getDistrictYtd()
			// + " SUM_H_AMOUNT=" + res0.getSumAmount();
			//
			// System.out.println("SQL results: "+result);
			//
			// }

		} catch (Exception t) {
			t.printStackTrace();

			if (irwwbase.is913(t))
				throw new UserException("<&!@" + irwwbase.checkFor913(t)
						+ ", failure in in consistencyCheck9, " + t.getCause(),
						irwwbase.getStackTrace(t));
			else
				throw new UserException("<&!@"
						+ "failure in in consistencyCheck9: "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause()
						+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		irwwbase.debugOut("<<< Exit " + className + " ConCheck9 ");

		if (failureCheck1 == 0) {
			irwwbase.debugOut("<<< CC9 Succeeded!");
			results.addElement("CC9 Succeeded");
		}

		else {

			irwwbase.debugOut("<<< CC9 Failed!");
			results.addElement("CC9 Failed");
			// Collections.addAll(results, conCheck1Output);
			// xniu results.addElement("CC1 Failed: " + conCheck1Output);
		}

		results.addElement("--------------------------------------------------------------------------------"
				+ "--------------------------------------------------------------------------------");

	}
	
	// sql statement unable to be converted to jpql to increase efficiency
	// sql statement incorporates FULL OUTER JOIN, which is a combination of
	// right and left outer joins
	// JPA does not currently support RIGHT OUTER JOIN
	// Once JPA does support FULL OUTER JOIN, possible to change to jpql
	// statement to increase efficiency
	protected void consistencyCheck13(EntityManager em, String lockMode)
			throws ServletException, IOException, UserException {

		irwwbase.debugOut("<<< Entering " + className + " consistencyCheck13 ");
		Query jpaQuery1 = null;
		Query jpaQuery2 = null;
		List<?> resultList1 = null;
		OrderJPA order = null;
		String orderTrackingNumber = null;
		int orderTrackingHopsCount = 0;
		Integer numberOrderTrackingRows = 0;
		Vector cc13Results = new Vector();
		short retry = 0;
		int errorCode = 0;

		while ((retry == 0)	|| ((retry <= numberOfRetries) && (errorCode == 1) && lockMode.contains("Optimistic_Force_Increment"))) {
			irwwbase.debugOut("<<< In "	+ className	+ " consistencyCheck13: Within while loop where lockMode = " + lockMode + "; retry =  " + retry);
			try {	
				jpaQuery1 = em.createNamedQuery("findOrdersWithAnOrderTrackingNumberByWarehouseIdAndDistrictId", OrderJPA.class);
				if (lockMode.contains("Pessimistic_Force_Increment")) {
					jpaQuery1.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				} else if (lockMode.contains("Optimistic_Force_Increment")) {
					jpaQuery1.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);	
				}
				jpaQuery1.setParameter("minWarehouseId", minWarehouseId);
				jpaQuery1.setParameter("maxWarehouseId", maxWarehouseId);
				jpaQuery1.setParameter("minDistrictId", minDistrictId);
				jpaQuery1.setParameter("maxDistrictId", maxDistrictId);
				resultList1 = jpaQuery1.getResultList();
			
				for (int i = 0; i < resultList1.size(); i++) {
					order = (OrderJPA)resultList1.get(i);
					orderTrackingNumber = order.getOrderTrackingNumber();
					orderTrackingHopsCount = order.getOrderTrackingHopsCount();
					irwwbase.debugOut("<<< " + className + " CC13: orderTrackingHopsCount =  "	+ orderTrackingHopsCount);

					jpaQuery2 = em.createNamedQuery("numberOfOrderTrackingRowsByOrderTrackingNumber", OrderTrackingJPA.class);
					if (lockMode.contains("Pessimistic_Force_Increment")) {
						jpaQuery2.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
					}
					else if (lockMode.contains("Optimistic_Force_Increment")) {
						jpaQuery2.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);	
					}				
					jpaQuery2.setParameter("orderTrackingNumber", orderTrackingNumber);
					numberOrderTrackingRows = Integer.valueOf(jpaQuery2.getSingleResult().toString());
					irwwbase.debugOut("<<< " + className + " CC13: numberRowsOrderTracking =  "	+ numberOrderTrackingRows);

					if (orderTrackingHopsCount == numberOrderTrackingRows) {
					    cc13Results.addElement(1);
						irwwbase.debugOut("<<< CC13: OrderTrackingNumber " + order.getOrderTrackingNumber() + " passed!");
					} else {
						if (orderTrackingHopsCount != numberOrderTrackingRows) {
							cc13Results.addElement("CC13 Failed");
						}
						results.addElement("*** CC13: warehouseId " + order.getWarehouseId()  
							+ "; districtId " + order.getDistrictId()
							+ "; customerId " + order.getCustomerId()
							+ "; orderId " + order.getOrderId() + " failed: where "
							+ " orderTrackingHopsCount = " + orderTrackingHopsCount
							+ " and numberRowsOrderTracking = " + numberOrderTrackingRows);
						irwwbase.debugOut("*** CC13: warehouseId " + order.getWarehouseId()  
							+ "; districtId " + order.getDistrictId()
							+ "; customerId " + order.getCustomerId()
							+ "; orderId " + order.getOrderId() + " failed: where "
							+ " orderTrackingHopsCount = " + orderTrackingHopsCount
							+ " and numberRowsOrderTracking = " + numberOrderTrackingRows);
					}
				}
			} catch (OptimisticLockException t) {
				System.out.println("<<< " + className + ": consistencyCheck13(): OptimisticLockException: retry = " + retry );
				errorCode = 1;		
			} catch (Exception e) {
				System.out.println("<<< " + className + ": consistencyCheck13() - Exception: " + e);
				results.addElement("<<< " + className + ": consistencyCheck13() - Exception: " + e);
				e.printStackTrace();
				throw new UserException("<<< " + className + " ForceIncrementServlet.consistencyCheck13() - Exception: " + e);
			}
			retry++;
		}
			
		try {
			if (cc13Results.contains("CC13 Failed")){
				irwwbase.debugOut("<<< CC13 Failed!");	
				results.addElement("CC13  Failed!");
			} else {
				irwwbase.debugOut("<<< CC13 Succeeded!");
				results.addElement("CC13 Succeeded");	
			}
			results.addElement("--------------------------------------------------------------------------------"
					+ "--------------------------------------------------------------------------------");
		} catch (Exception e) {
			System.out.println("<<< " + className + " ForceIncrementServlet.consistencyCheck13() - Exception: " + e);
			results.addElement("<<< " + className + " ForceIncrementServlet.consistencyCheck13() - Exception: " + e);
			e.printStackTrace();
			throw new UserException("<<< " + className + " ForceIncrementServlet.consistencyCheck13() - Exception: " + e);
		}
	}

	protected void getWarehouseInstance(EntityManager em, short warehouseId,
			String lockMode) throws UserException {

		irwwbase.debugOut("<<< Entering " + className
				+ " getWarehouseInstance ");
		irwwbase.debugOut("<<< In " + className
				+ " getWarehouseInstance: warehouseId = " + warehouseId);
		irwwbase.debugOut("<<< In " + className
				+ " getWarehouseInstance: lockMode = " + lockMode);

		short retry = 0;
		int errorCode = 0;

		if (lockMode.contains("Pessimistic_Force_Increment")) {
			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " getWarehouseInstance: Before em.find with LockModeType.PESSIMISTIC_FORCE_INCREMENT");
				warehouse = em.find(WarehouseJPA.class, warehouseId,
						LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " getWarehouseInstance: After em.find with LockModeType.PESSIMISTIC_FORCE_INCREMENT");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", getWarehouseInstance, " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				} else {
					throw new UserException("<&!@"
							+ "failure in getWarehouseInstance: "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				}
			}
		} else {
			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " getWarehouseInstance: Before em.find with LockModeType.OPTIMISTIC_FORCE_INCREMENT");
					warehouse = em.find(WarehouseJPA.class, warehouseId,
							LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " getWarehouseInstance: After em.find with LockModeType.OPTIMISTIC_FORCE_INCREMENT");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " em.find(WarehouseJPA.class, warehouseId,LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", getWarehouseInstance, " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					} else {
						throw new UserException("<&!@"
								+ "failure in getWarehouseInstance: "
								+ " exception message: " + t.getMessage()
								+ ", exception cause: " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					}
				}
			}
		}

		if (warehouse == null) {
			throw new UserException("warehouse not found, warehouse="
					+ warehouseId + " " + irwwbase.getSTC(), "");
		}

		irwwbase.debugOut("<<< Exit " + className + " getWarehouseInstance");
	}

	protected void getDistrictInstance(EntityManager em, short districtId,
			short warehouseId, String lockMode) throws UserException {

		irwwbase.debugOut("<<< Entering " + className + " getDistrictInstance ");
		irwwbase.debugOut("<<< In " + className
				+ " getDistrictInstance: warehouseId = " + warehouseId);
		irwwbase.debugOut("<<< In " + className
				+ " getDistrictInstance: districtId = " + districtId);
		irwwbase.debugOut("<<< In " + className
				+ " getDistrictInstance: lockMode = " + lockMode);

		DistrictKey key = null;
		short retry = 0;
		int errorCode = 0;

		key = new DistrictKey();
		key.setDistrictId(districtId);
		key.setDistrictWareId(warehouseId);

		if (lockMode.contains("Pessimistic_Force_Increment")) {
			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " getDistrictInstance: Before em.find with LockModeType.PESSIMISTIC_FORCE_INCREMENT");
				district = em.find(DistrictJPA.class, key,
						LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " getDistrictInstance: After em.find with LockModeType.PESSIMISTIC_FORCE_INCREMENT");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", getDistrictInstance, " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				} else {
					throw new UserException("<&!@"
							+ "failure in getDistrictInstance: "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				}
			}
		} else {
			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " getDistrictInstance: Before em.find with LockModeType.OPTIMISTIC_FORCE_INCREMENT");
					district = em.find(DistrictJPA.class, key,
							LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " getDistrictInstance: After em.find with LockModeType.OPTIMISTIC_FORCE_INCREMENT");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " em.find(DistrictJPA.class, key ,LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", getDistrictInstance, " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					} else {
						throw new UserException("<&!@"
								+ "failure in getDistrictInstance: "
								+ " exception message: " + t.getMessage()
								+ ", exception cause: " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					}
				}
			}
		}
		if (district == null) {
			throw new UserException("district not found, districtId = "
					+ districtId + " and warehouseId = " + warehouseId + " "
					+ irwwbase.getSTC(), "");
		}
		irwwbase.debugOut("<<< Exit " + className + " getDistrictInstance");
	}

	protected void getCustomerInstance(EntityManager em, short customerId,
			short districtId, short warehouseId, String lockMode)
			throws UserException {

		irwwbase.debugOut("<<< Entering " + className + " getCustomerInstance ");

		CustomerKey key = null;
		//Comment out unused variables
		//EntityManagerFactory emfCustomerJPA = null;
		//EntityManager emCustomerJPA = null;

		key = new CustomerKey();
		key.setCustomerDId(districtId);
		key.setCustomerId(customerId);
		key.setCustomerWId(warehouseId);

		short retry = 0;
		int errorCode = 0;

		if (lockMode.contains("Pessimistic_Force_Increment")) {
			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " getCustomerInstance: Before em.find with LockModeType.PESSIMISTIC_FORCE_INCREMENT");
				customer = em.find(CustomerJPA.class, key,
						LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " getCustomerInstance: After em.find with LockModeType.PESSIMISTIC_FORCE_INCREMENT");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", getCustomerInstance, " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				} else {
					throw new UserException("<&!@"
							+ "failure in getCustomerInstance: "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				}
			}
		} else {
			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " getCustomerInstance: Before em.find with LockModeType.OPTIMISTIC_FORCE_INCREMENT");
					customer = em.find(CustomerJPA.class, key,
							LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " getCustomerInstance: After em.find with LockModeType.OPTIMISTIC_FORCE_INCREMENT");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " em.find(CustomerJPA.class, key ,LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", getCustomerInstance, " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					} else {
						throw new UserException("<&!@"
								+ "failure in getCustomerInstance: "
								+ " exception message: " + t.getMessage()
								+ ", exception cause: " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					}
				}
			}
		}
		if (customer == null) {
			throw new UserException("customer not found, districtId = "
					+ districtId + ", customerId = " + customerId
					+ " and warehouseId = " + warehouseId + " "
					+ irwwbase.getSTC(), "");
		}
		irwwbase.debugOut("<<< Exit " + className + " getCustomerInstance");
	}

	protected OrderJPA getOrderInstance(EntityManager em, int orderId,
			short districtId, short warehouseId, String lockMode)
			throws UserException {

		irwwbase.debugOut("<<< Entering " + className + " getOrderInstance ");

		OrderKey key = null;
		key = new OrderKey();
		key.setOrderId(orderId);
		key.setDistrictId(districtId);
		key.setWarehouseId(warehouseId);

		short retry = 0;
		int errorCode = 0;

		if (lockMode.contains("Pessimistic_Force_Increment")) {
			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " getOrderInstance: Before em.find with LockModeType.PESSIMISTIC_FORCE_INCREMENT");
				order = em.find(OrderJPA.class, key,
						LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " getOrderInstance: After em.find with LockModeType.PESSIMISTIC_FORCE_INCREMENT");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", getOrderInstance, " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				} else {
					throw new UserException("<&!@"
							+ "failure in getOrderInstance: "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				}
			}
		} else {
			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " getOrderInstance: Before em.find with LockModeType.OPTIMISTIC_FORCE_INCREMENT");
					order = em.find(OrderJPA.class, key,
							LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " getOrderInstance: After em.find with LockModeType.OPTIMISTIC_FORCE_INCREMENT");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " em.find(OrderJPA.class,key,LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", getOrderInstance, " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					} else {
						throw new UserException("<&!@"
								+ "failure in getOrderInstance: "
								+ " exception message: " + t.getMessage()
								+ ", exception cause: " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					}
				}
			}
		}

		if (order == null) {
			throw new UserException("order not found orderId = " + orderId
					+ ", districtId = " + districtId + ", warehouseId = "
					+ warehouseId + ", " + irwwbase.getSTC(), "no stack");
		}
		irwwbase.debugOut("<<< Exit " + className + " getOrderInstance");
		
		return order; 
	}

	protected int queryMaxOrderIdFromOrderInstances(EntityManager em,
			short districtId, short warehouseId, String lockMode)
			throws UserException {

		irwwbase.debugOut("<<< Entering " + className
				+ " queryMaxOrderIdFromOrderInstances ");

		int orderId = 0;
		Query query = null;

		short retry = 0;
		int errorCode = 0;

		try {
			query = em.createNamedQuery("findMaxOrderIdByWarehouseDistrict",
					OrderJPA.class);
			// query = em.createNamedQuery("findMaxOrderIdByWarehouseDistrict");
		} catch (Exception t) {
			t.printStackTrace();
			System.out
					.println("<<< In "
							+ className
							+ " queryMaxOrderIdFromOrderInstances: em.createNamedQuery(findMaxOrderIdByWarehouseDistrict,OrderJPA.class) failed");
			throw new UserException(
					"<&!@ failure in queryMaxOrderIdFromOrderInstances: em.createNamedQuery(findMaxOrderIdByWarehouseDistrict,OrderJPA.class) failed "
							+ " exception message: "
							+ t.getMessage()
							+ ", exception cause: "
							+ t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		if (lockMode.contains("Pessimistic_Force_Increment")) {
			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " queryMaxOrderIdFromOrderInstances: Before query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
				query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " queryMaxOrderIdFromOrderInstances: After query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", queryMaxOrderIdFromOrderInstances, "
							+ t.getCause() + irwwbase.getSTC(),
							irwwbase.getStackTrace(t));
				} else {
					throw new UserException("<&!@"
							+ "failure in queryMaxOrderIdFromOrderInstances: "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				}
			}
		} else {
			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " queryMaxOrderIdFromOrderInstances: Before query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " queryMaxOrderIdFromOrderInstances: After query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", queryMaxOrderIdFromOrderInstances:, "
								+ t.getCause() + irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					} else {
						throw new UserException(
								"<&!@"
										+ "failure in queryMaxOrderIdFromOrderInstances: "
										+ " exception message: "
										+ t.getMessage()
										+ ", exception cause: " + t.getCause()
										+ irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					}
				}
			}
		}

		try {
			query.setParameter("districtId", districtId);
			query.setParameter("warehouseId", warehouseId);

			order = (OrderJPA) query.getSingleResult();

			if (order == null) {
				throw new RuntimeException(
						"<<< "
								+ className
								+ " queryMaxOrderIdFromOrderInstances: order not found "
								+ irwwbase.getSTC());
			}

			if (order != null) {
				orderId = order.getOrderId();
				irwwbase.debugOut("OrderId" + order.getOrderId());
			}

			if (orderId == 0) {
				System.out.println("There are no Orders for WarehouseId "
						+ warehouseId + " and DistrictId " + districtId);
			}
		} catch (Exception t) {
			t.printStackTrace();
			System.out.println("orderid: " + orderId + "districtid: "
					+ districtId + "warehouseid: " + warehouseId);
			if (irwwbase.is913(t))
				throw new UserException("<&!@" + irwwbase.checkFor913(t)
						+ ", queryMaxOrderIdFromOrderInstances, "
						+ t.getCause(), irwwbase.getStackTrace(t));
			else
				throw new UserException(
						"<&!@"
								+ "failure in queryMaxOrderIdFromOrderInstances: orderid: "
								+ orderId + " districtid: " + districtId
								+ " warehouseid: " + warehouseId + ", "
								+ " exception message: " + t.getMessage()
								+ ", exception cause: " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		irwwbase.debugOut("<<< Exit " + className
				+ " queryMaxOrderIdFromOrderInstances ");

		return orderId;
	}

	protected BigDecimal queryOrderlineCountFromOrderInstances(
			EntityManager em, short districtId, short warehouseId,
			String lockMode) throws UserException {

		irwwbase.debugOut("<<< Entering " + className
				+ " queryOrderlineCountFromOrderInstances ");

		BigDecimal orderlineCount = new BigDecimal(0);
		Query query = null;

		short retry = 0;
		int errorCode = 0;

		try {
			query = em.createNamedQuery(
					"findOrderlineCountByWarehouseDistrict", OrderJPA.class);
		} catch (Exception t) {
			t.printStackTrace();
			System.out
					.println("<<< In "
							+ className
							+ " queryOrderlineCountFromOrderInstances: em.createNamedQuery(findOrderlineCountByWarehouseDistrict, OrderJPA.class) failed");
			throw new UserException(
					"<&!@ failure in queryOrderlineCountFromOrderInstances: em.createNamedQuery(findOrderlineCountByWarehouseDistrict, OrderJPA.class) failed "
							+ " exception message: "
							+ t.getMessage()
							+ ", exception cause: "
							+ t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		if (lockMode.contains("Pessimistic_Force_Increment")) {
			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " queryOrderlineCountFromOrderInstances: Before query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
				query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " queryOrderlineCountFromOrderInstances: After query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", queryMaxOrderIdFromOrderInstances, "
							+ t.getCause() + irwwbase.getSTC(),
							irwwbase.getStackTrace(t));
				} else {
					throw new UserException("<&!@"
							+ "failure in queryMaxOrderIdFromOrderInstances: "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				}
			}
		} else {
			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " queryOrderlineCountFromOrderInstances: Before query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " queryOrderlineCountFromOrderInstances: After query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", queryOrderlineCountFromOrderInstances:, "
								+ t.getCause() + irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					} else {
						throw new UserException(
								"<&!@"
										+ "failure in queryOrderlineCountFromOrderInstances: "
										+ " exception message: "
										+ t.getMessage()
										+ ", exception cause: " + t.getCause()
										+ irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					}
				}
			}
		}
		try {
			query.setParameter("districtId", districtId);
			query.setParameter("warehouseId", warehouseId);

			orderlineCount = (BigDecimal) query.getSingleResult();

			irwwbase.debugOut("In queryOrderlineCountFromOrderInstances, orderlineCount = "
					+ orderlineCount);

		} catch (Exception t) {
			t.printStackTrace();
			if (irwwbase.is913(t)) {
				throw new UserException("<&!@" + irwwbase.checkFor913(t)
						+ ", queryOrderlineCountFromOrderInstances, "
						+ t.getCause() + irwwbase.getSTC(),
						irwwbase.getStackTrace(t));
			} else {
				throw new UserException("<&!@"
						+ "failure in queryOrderlineCountFromOrderInstances(: "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause()
						+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
			}
		}
		irwwbase.debugOut("<<< Exit " + className
				+ " queryOrderlineCountFromOrderInstances ");

		return orderlineCount;
	}

	protected BigDecimal queryHistoryAmount(EntityManager em,
			short warehouseId, String lockMode) throws UserException {

		irwwbase.debugOut("<<< Entering " + className
				+ " queryHistoryAmount(w, lockMode) ");

		BigDecimal sumHistoryAmount = new BigDecimal(0);
		Query query = null;

		short retry = 0;
		int errorCode = 0;

		try {
			query = em.createNamedQuery("sumHistoryByWarehouse");
		} catch (Exception t) {
			t.printStackTrace();
			System.out
					.println("<<< In "
							+ className
							+ " queryHistoryAmount(w, lockMode): em.createNamedQuery(sumHistoryByWarehouse) failed");
			throw new UserException(
					"<&!@ failure in queryHistoryAmount(w, lockMode): em.createNamedQuery(sumHistoryByWarehouse) failed "
							+ " exception message: "
							+ t.getMessage()
							+ ", exception cause: "
							+ t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		if (lockMode.contains("Pessimistic_Force_Increment")) {
			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " queryHistoryAmount(w, lockMode): Before query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
				query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " queryHistoryAmount(w, lockMode): After query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", queryHistoryAmount(w, lockMode), "
							+ t.getCause() + irwwbase.getSTC(),
							irwwbase.getStackTrace(t));
				} else {
					throw new UserException("<&!@"
							+ "failure in queryHistoryAmount(w, lockMode): "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				}
			}
		} else {
			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " queryHistoryAmount(w, lockMode): Before query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " queryHistoryAmount(w, lockMode): After query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", queryHistoryAmount(w, lockMode):, "
								+ t.getCause() + irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					} else {
						throw new UserException(
								"<&!@"
										+ "failure in queryHistoryAmount(w, lockMode): "
										+ " exception message: "
										+ t.getMessage()
										+ ", exception cause: " + t.getCause()
										+ irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					}
				}
			}
		}

		try {
			irwwbase.debugOut("In queryHistoryAmount(w, lockMode), warehouseId = "
					+ warehouseId);

			query.setParameter("warehouseId", warehouseId);

			sumHistoryAmount = sumHistoryAmount.add((BigDecimal) query
					.getSingleResult());

			irwwbase.debugOut("In queryHistoryAmount(w, lockMode), sumHistoryAmount = "
					+ sumHistoryAmount);

		} catch (Exception t) {
			t.printStackTrace();
			if (irwwbase.is913(t)) {
				throw new UserException("<&!@" + irwwbase.checkFor913(t)
						+ ", queryHistoryAmount(w, lockMode), " + t.getCause()
						+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
			} else {
				throw new UserException("<&!@"
						+ "failure in queryHistoryAmount(w, lockMode): "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause()
						+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
			}
		}

		irwwbase.debugOut("<<< Exit " + className
				+ " queryHistoryAmount(w, lockMode) ");

		return sumHistoryAmount;
	}

	protected BigDecimal queryHistoryAmount(EntityManager em, short districtId,
			short warehouseId, String lockMode) throws UserException {

		irwwbase.debugOut("<<< Entering " + className
				+ " queryHistoryAmount(d, w, lockMode) ");

		BigDecimal sumHistoryAmount = new BigDecimal(0);
		Query query = null;

		short retry = 0;
		int errorCode = 0;

		try {
			query = em.createNamedQuery("sumHistoryByWarehouseDistrict");
		} catch (Exception t) {
			t.printStackTrace();
			System.out
					.println("<<< In "
							+ className
							+ " queryHistoryAmount(d, w, lockMode): em.createNamedQuery(sumHistoryByWarehouse) failed");
			throw new UserException(
					"<&!@ failure in queryHistoryAmount(d, w, lockMode): em.createNamedQuery(sumHistoryByWarehouse) failed "
							+ " exception message: "
							+ t.getMessage()
							+ ", exception cause: "
							+ t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}
		if (lockMode.contains("Pessimistic_Force_Increment")) {
			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " queryHistoryAmount(d, w, lockMode): Before query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
				query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " queryHistoryAmount(d, w, lockMode): After query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", queryHistoryAmount(d, w, lockMode), "
							+ t.getCause() + irwwbase.getSTC(),
							irwwbase.getStackTrace(t));
				} else {
					throw new UserException("<&!@"
							+ "failure in queryHistoryAmount(d, w, lockMode): "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				}
			}
		} else {
			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " queryHistoryAmount(d, w, lockMode): Before query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " queryHistoryAmount(d, w, lockMode): After query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", queryHistoryAmount(d, w, lockMode):, "
								+ t.getCause() + irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					} else {
						throw new UserException(
								"<&!@"
										+ "failure in queryHistoryAmount(d, w, lockMode): "
										+ " exception message: "
										+ t.getMessage()
										+ ", exception cause: " + t.getCause()
										+ irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					}
				}
			}
		}

		try {
			irwwbase.debugOut("In queryHistoryAmount(d, w, lockMode): warehouseId: "
					+ warehouseId);
			irwwbase.debugOut("In queryHistoryAmount(d, w, lockMode): districtId: "
					+ districtId);

			query.setParameter("warehouseId", warehouseId);
			query.setParameter("districtId", districtId);

			sumHistoryAmount = (BigDecimal) query.getSingleResult();

			irwwbase.debugOut("In queryHistoryAmount(d, w, lockMode): sumHistoryAmount: "
					+ sumHistoryAmount);

		} catch (Exception t) {
			t.printStackTrace();
			if (irwwbase.is913(t)) {
				throw new UserException("<&!@" + irwwbase.checkFor913(t)
						+ ", queryHistoryAmount(d, w, lockMode), "
						+ t.getCause() + irwwbase.getSTC(),
						irwwbase.getStackTrace(t));
			} else {
				throw new UserException("<&!@"
						+ "failure in queryHistoryAmount(d, w, lockMode): "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause()
						+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
			}
		}
		irwwbase.debugOut("<<< Exit " + className
				+ " queryHistoryAmount(d, w, lockMode) ");

		return sumHistoryAmount;
	}

	protected void getNewOrderInstance(EntityManager em, int orderId,
			short districtId, short warehouseId, String lockMode)
			throws UserException {

		irwwbase.debugOut("<<< Entering " + className + " getNewOrderInstance ");

		NewOrderKey key = null;
		key = new NewOrderKey();
		key.setOrderId(orderId);
		key.setDistrictId(districtId);
		key.setWarehouseId(warehouseId);

		short retry = 0;
		int errorCode = 0;

		if (lockMode.contains("Pessimistic_Force_Increment")) {
			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " getNewOrderInstance: Before em.find with LockModeType.PESSIMISTIC_FORCE_INCREMENT");
				newOrder = em.find(NewOrderJPA.class, key,
						LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " getNewOrderInstance: After em.find with LockModeType.PESSIMISTIC_FORCE_INCREMENT");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", getNewOrderInstance, " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				} else {
					throw new UserException("<&!@"
							+ "failure in getNewOrderInstance: "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				}
			}
		} else {
			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " getNewOrderInstance: Before em.find with LockModeType.OPTIMISTIC_FORCE_INCREMENT");
					newOrder = em.find(NewOrderJPA.class, key,
							LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " getNewOrderInstance: After em.find with LockModeType.OPTIMISTIC_FORCE_INCREMENT");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " em.find(NewOrderJPA.class,key,LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", getNewOrderInstance, " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					} else {
						throw new UserException("<&!@"
								+ "failure in getNewOrderInstance: "
								+ " exception message: " + t.getMessage()
								+ ", exception cause: " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					}
				}
			}
		}

		if (newOrder == null) {
			throw new UserException("newOrder not found orderId = " + orderId
					+ ", districtId = " + districtId + ", warehouseId = "
					+ warehouseId + ", " + irwwbase.getSTC(), "no stack");
		}
		irwwbase.debugOut("<<< Exit " + className + " getNewOrderInstance");
	}

	protected int queryMaxOrderIdFromNewOrderInstances(EntityManager em,
			short districtId, short warehouseId, String lockMode)
			throws UserException, ServletException {

		irwwbase.debugOut("<<< Entering " + className
				+ " queryMaxOrderIdFromNewOrderInstances ");

		int orderId = 0;
		Query query = null;

		short retry = 0;
		int errorCode = 0;

		try {
			query = em.createNamedQuery("findMaxNewOrder", NewOrderJPA.class);
			// query = em.createNamedQuery("findMaxNewOrder");
		} catch (Exception t) {
			t.printStackTrace();
			System.out
					.println("<<< In "
							+ className
							+ " queryMaxOrderIdFromNewOrderInstances: em.createNamedQuery(findMaxNewOrder, NewOrderJPA.class) failed");
			throw new UserException(
					"<&!@ failure in queryMaxOrderIdFromNewOrderInstances: em.createNamedQuery(findMaxNewOrder, NewOrderJPA.class) failed "
							+ " exception message: "
							+ t.getMessage()
							+ ", exception cause: "
							+ t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		if (lockMode.contains("Pessimistic_Force_Increment")) {
			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " queryMaxOrderIdFromNewOrderInstances: Before query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
				query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " queryMaxOrderIdFromNewOrderInstances: After query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", queryMaxOrderIdFromNewOrderInstances, "
							+ t.getCause() + irwwbase.getSTC(),
							irwwbase.getStackTrace(t));
				} else {
					throw new UserException(
							"<&!@"
									+ "failure in queryMaxOrderIdFromNewOrderInstances: "
									+ " exception message: " + t.getMessage()
									+ ", exception cause: " + t.getCause()
									+ irwwbase.getSTC(),
							irwwbase.getStackTrace(t));
				}
			}
		} else {
			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " queryMaxOrderIdFromNewOrderInstances: Before query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " queryMaxOrderIdFromNewOrderInstances: After query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", queryMaxOrderIdFromNewOrderInstances:, "
								+ t.getCause() + irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					} else {
						throw new UserException(
								"<&!@"
										+ "failure in queryMaxOrderIdFromNewOrderInstances: "
										+ " exception message: "
										+ t.getMessage()
										+ ", exception cause: " + t.getCause()
										+ irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					}
				}
			}
		}

		try {
			query.setParameter("districtId", districtId);
			query.setParameter("warehouseId", warehouseId);

			newOrder = (NewOrderJPA) query.getSingleResult();

			if (newOrder == null) {
				// flag orderId=0 since there are no new orders to deliver
				orderId = 0;
				throw new ServletException(
						"<<< "
								+ className
								+ " queryMaxOrderIdFromNewOrderInstances: order not found "
								+ irwwbase.getSTC());
			}

			if (newOrder != null) {
				orderId = newOrder.getOrderId();
				irwwbase.debugOut("OrderId = " + newOrder.getOrderId());
			}

			if (orderId == 0) {
				System.out.println("There are no NewOrders for WarehouseId "
						+ warehouseId + " and DistrictId " + districtId);
			}

		} catch (NoResultException t) {
			// t.printStackTrace();
			// throw new
			// ServletException("There are no NewOrders for WarehouseId " +
			// warehouseId + " and DistrictId "
			// + districtId,t);
			orderId = 0;
			System.out.println("WARNING: There are no NewOrders for WarehouseId "
					+ warehouseId + " and DistrictId " + districtId + ". Reload the ERWW tables and ensure the workload script is executing 10 NewOrder transactions per 1 Delivery transaction.");
		}

		catch (Exception t) {
			t.printStackTrace();
			if (irwwbase.is913(t)) {
				throw new UserException("<&!@" + irwwbase.checkFor913(t)
						+ ", queryMaxOrderIdFromNewOrderInstances, "
						+ t.getCause() + irwwbase.getSTC(),
						irwwbase.getStackTrace(t));
			} else {
				throw new UserException("<&!@"
						+ "failure in queryMaxOrderIdFromNewOrderInstances: "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause()
						+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
			}
		}
		irwwbase.debugOut("<<< Exit " + className
				+ " queryMaxOrderIdFromNewOrderInstances ");

		return orderId;
	}

	protected int queryMinOrderIdFromNewOrderInstances(EntityManager em,
			short districtId, short warehouseId, String lockMode)
			throws UserException {

		irwwbase.debugOut("<<< Entering " + className
				+ " queryMinOrderIdFromNewOrderInstances ");

		int orderId = 0;
		Query query = null;

		short retry = 0;
		int errorCode = 0;

		try {
			// query = em.createNamedQuery("findMinNewOrder",
			// NewOrderJPA.class);
			query = em.createNamedQuery("findMinNewOrder");
		} catch (Exception t) {
			t.printStackTrace();
			System.out
					.println("<<< In "
							+ className
							+ " queryMinOrderIdFromNewOrderInstances: em.createNamedQuery(findMinNewOrder) failed");
			throw new UserException(
					"<&!@ failure in queryMinOrderIdFromNewOrderInstances: em.createNamedQuery(findMinNewOrder) failed "
							+ " exception message: "
							+ t.getMessage()
							+ ", exception cause: "
							+ t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		if (lockMode.contains("Pessimistic_Force_Increment")) {
			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " queryMinOrderIdFromNewOrderInstances: Before query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
				query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " queryMinOrderIdFromNewOrderInstances: After query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", queryMinOrderIdFromNewOrderInstances, "
							+ t.getCause() + irwwbase.getSTC(),
							irwwbase.getStackTrace(t));
				} else {
					throw new UserException(
							"<&!@"
									+ "failure in queryMinOrderIdFromNewOrderInstances: "
									+ " exception message: " + t.getMessage()
									+ ", exception cause: " + t.getCause()
									+ irwwbase.getSTC(),
							irwwbase.getStackTrace(t));
				}
			}
		} else {
			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " queryMinOrderIdFromNewOrderInstances: Before query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " queryMinOrderIdFromNewOrderInstances: After query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", queryMinOrderIdFromNewOrderInstances:, "
								+ t.getCause() + irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					} else {
						throw new UserException(
								"<&!@"
										+ "failure in queryMinOrderIdFromNewOrderInstances: "
										+ " exception message: "
										+ t.getMessage()
										+ ", exception cause: " + t.getCause()
										+ irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					}
				}
			}
		}

		try {
			query.setParameter("districtId", districtId);
			query.setParameter("warehouseId", warehouseId);

			newOrder = (NewOrderJPA) query.getSingleResult();

			if (newOrder == null) {
				throw new RuntimeException(
						"<<< "
								+ className
								+ " queryMinOrderIdFromNewOrderInstances: order not found "
								+ irwwbase.getSTC());
			}

			if (newOrder != null) {
				orderId = newOrder.getOrderId();
				irwwbase.debugOut("OrderId = " + newOrder.getOrderId());
			}

			if (orderId == 0) {
				System.out.println("There are no NewOrders for WarehouseId "
						+ warehouseId + " and DistrictId " + districtId);
			}

		} catch (NoResultException t) {
			//t.printStackTrace();
			orderId = 0;
			System.out.println("WARNING: There are no NewOrders for WarehouseId "
					+ warehouseId + " and DistrictId " + districtId + ". Reload the ERWW tables and ensure the workload script is executing 10 NewOrder transactions per 1 Delivery transaction.");
		} catch (Exception t) {
			t.printStackTrace();
			if (irwwbase.is913(t)) {
				throw new UserException("<&!@" + irwwbase.checkFor913(t)
						+ ", queryMinOrderIdFromNewOrderInstances, "
						+ t.getCause() + irwwbase.getSTC(),
						irwwbase.getStackTrace(t));
			} else {
				throw new UserException("<&!@"
						+ "failure in queryMinOrderIdFromNewOrderInstances: "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause()
						+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
			}
		}
		irwwbase.debugOut("<<< Exit " + className
				+ " queryMinOrderIdFromNewOrderInstances");

		return orderId;
	}

	protected int queryNumberRowsFromNewOrderInstances(EntityManager em,
			short districtId, short warehouseId, String lockMode)
			throws UserException {

		irwwbase.debugOut("<<< Entering " + className
				+ " queryNumberRowsFromNewOrderInstances ");

		Query query = null;
		int numberOfRows = 0;

		short retry = 0;
		int errorCode = 0;

		try {
			query = em.createNamedQuery("findNumberOfNewOrderRows");
		} catch (Exception t) {
			t.printStackTrace();
			System.out
					.println("<<< In "
							+ className
							+ " queryNumberRowsFromNewOrderInstances: em.createNamedQuery(findNumberOfNewOrderRows) failed");
			throw new UserException(
					"<&!@ failure in queryNumberRowsFromNewOrderInstances: em.createNamedQuery(findNumberOfNewOrderRows) failed "
							+ " exception message: "
							+ t.getMessage()
							+ ", exception cause: "
							+ t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		if (lockMode.contains("Pessimistic_Force_Increment")) {
			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " queryNumberRowsFromNewOrderInstances: Before query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
				query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " queryNumberRowsFromNewOrderInstances: After query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", queryNumberRowsFromNewOrderInstances, "
							+ t.getCause() + irwwbase.getSTC(),
							irwwbase.getStackTrace(t));
				} else {
					throw new UserException(
							"<&!@"
									+ "failure in queryNumberRowsFromNewOrderInstances: "
									+ " exception message: " + t.getMessage()
									+ ", exception cause: " + t.getCause()
									+ irwwbase.getSTC(),
							irwwbase.getStackTrace(t));
				}
			}
		} else {
			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " queryNumberRowsFromNewOrderInstances: Before query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " queryNumberRowsFromNewOrderInstances: After query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", queryNumberRowsFromNewOrderInstances:, "
								+ t.getCause() + irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					} else {
						throw new UserException(
								"<&!@"
										+ "failure in queryNumberRowsFromNewOrderInstances: "
										+ " exception message: "
										+ t.getMessage()
										+ ", exception cause: " + t.getCause()
										+ irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					}
				}
			}
		}

		try {
			query.setParameter("districtId", districtId);
			query.setParameter("warehouseId", warehouseId);

			numberOfRows = ((Long) query.getSingleResult()).intValue();

			if (numberOfRows == 0) {
				System.out
						.println("There are no NewOrder rows for WarehouseId "
								+ warehouseId + " and DistrictId " + districtId);
			}

		} catch (Exception t) {
			t.printStackTrace();
			if (irwwbase.is913(t)) {
				throw new UserException("<&!@" + irwwbase.checkFor913(t)
						+ ", queryNumberRowsFromNewOrderInstances, "
						+ t.getCause() + irwwbase.getSTC(),
						irwwbase.getStackTrace(t));
			} else {
				throw new UserException("<&!@"
						+ "failure in queryNumberRowsFromNewOrderInstances: "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause()
						+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
			}
		}
		irwwbase.debugOut("<<< Exit " + className
				+ " queryNumberRowsFromNewOrderInstances ");

		return numberOfRows;
	}

	protected void getOrderlineInstance(EntityManager em, int orderId,
			short districtId, short warehouseId, short orderlineNumber,
			String lockMode) throws UserException {

		irwwbase.debugOut("<<< Entering " + className
				+ " getOrderlineInstance ");

		OrderlineKey key = new OrderlineKey(orderId, districtId, warehouseId, orderlineNumber);

		short retry = 0;
		int errorCode = 0;

		if (lockMode.contains("Pessimistic_Force_Increment")) {
			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " getOrderlineInstance: Before em.find with LockModeType.PESSIMISTIC_FORCE_INCREMENT");
				orderline = em.find(OrderlineJPA.class, key,
						LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " getOrderlineInstance: After em.find with LockModeType.PESSIMISTIC_FORCE_INCREMENT");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", getOrderlineInstance, " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				} else {
					throw new UserException("<&!@"
							+ "failure in getOrderlineInstance: "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				}
			}
		} else {
			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " getOrderlineInstance: Before em.find with LockModeType.OPTIMISTIC_FORCE_INCREMENT");
					orderline = em.find(OrderlineJPA.class, key,
							LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " getOrderlineInstance: After em.find with LockModeType.OPTIMISTIC_FORCE_INCREMENT");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " em.find(OrderlineJPA.class,key,LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", getOrderlineInstance, " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					} else {
						throw new UserException("<&!@"
								+ "failure in getOrderlineInstance: "
								+ " exception message: " + t.getMessage()
								+ ", exception cause: " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					}
				}
			}
		}

		if (orderline == null) {
			throw new UserException("orderline not found orderId = " + orderId
					+ ", districtId = " + districtId + ", warehouseId = "
					+ warehouseId + " and orderlineNumber = " + orderlineNumber
					+ ", " + irwwbase.getSTC(), "no stack");
		}
		irwwbase.debugOut("<<< Exit " + className + " getOrderlineInstance");
	}

	protected BigDecimal queryNumberRowsFromOrderlineInstances(
			EntityManager em, short districtId, short warehouseId,
			String lockMode) throws UserException {

		irwwbase.debugOut("<<< Entering " + className
				+ " queryNumberRowsFromOrderlineInstances ");

		int rows = 0;
		BigDecimal numberOfRows = new BigDecimal(0);
		Query query = null;

		short retry = 0;
		int errorCode = 0;

		try {
			query = em.createNamedQuery("findNumberOfOrderlineRows");
		} catch (Exception t) {
			t.printStackTrace();
			System.out
					.println("<<< In "
							+ className
							+ " queryNumberRowsFromOrderlineInstances: em.createNamedQuery(findNumberOfOrderlineRowse) failed");
			throw new UserException(
					"<&!@ failure in queryNumberRowsFromOrderlineInstances: em.createNamedQuery(findNumberOfOrderlineRows) failed "
							+ " exception message: "
							+ t.getMessage()
							+ ", exception cause: "
							+ t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
		}

		if (lockMode.contains("Pessimistic_Force_Increment")) {
			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " queryNumberRowsFromOrderlineInstances: Before query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
				query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " queryNumberRowsFromOrderlineInstances: After query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", queryNumberRowsFromOrderlineInstances, "
							+ t.getCause() + irwwbase.getSTC(),
							irwwbase.getStackTrace(t));
				} else {
					throw new UserException(
							"<&!@"
									+ "failure in queryNumberRowsFromOrderlineInstances: "
									+ " exception message: " + t.getMessage()
									+ ", exception cause: " + t.getCause()
									+ irwwbase.getSTC(),
							irwwbase.getStackTrace(t));
				}
			}
		} else {
			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " queryNumberRowsFromOrderlineInstances: Before query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " queryNumberRowsFromOrderlineInstances: After query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", queryNumberRowsFromOrderlineInstances:, "
								+ t.getCause() + irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					} else {
						throw new UserException(
								"<&!@"
										+ "failure in queryNumberRowsFromOrderlineInstances: "
										+ " exception message: "
										+ t.getMessage()
										+ ", exception cause: " + t.getCause()
										+ irwwbase.getSTC(),
								irwwbase.getStackTrace(t));
					}
				}
			}
		}
		try {
			query.setParameter("districtId", districtId);
			query.setParameter("warehouseId", warehouseId);

			rows = ((Long) query.getSingleResult()).intValue();

			if (rows == 0) {
				System.out
						.println("There are no Orderline rows for WarehouseId "
								+ warehouseId + " and DistrictId " + districtId);
			}

			numberOfRows = new BigDecimal(rows);

		} catch (Exception t) {
			t.printStackTrace();
			if (irwwbase.is913(t)) {
				throw new UserException("<&!@" + irwwbase.checkFor913(t)
						+ ", queryNumberRowsFromOrderlineInstances, "
						+ t.getCause() + irwwbase.getSTC(),
						irwwbase.getStackTrace(t));
			} else {
				throw new UserException("<&!@"
						+ "failure in queryNumberRowsFromOrderlineInstances: "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause()
						+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
			}
		}
		irwwbase.debugOut("<<< Exit " + className
				+ " queryNumberRowsFromOrderlineInstances ");

		return numberOfRows;
	}

	/* Comment out unused private method
	private void getStockInstance(EntityManager em, int outItemId,
			short outItemSupplyWarehouseId, String lockMode)
			throws UserException {

		irwwbase.debugOut("<<< Entering " + className + " getStockInstance ");

		StockKey key = null;
		key = new StockKey();
		key.setS_I_ID(outItemId);
		key.setS_W_ID(outItemSupplyWarehouseId);

		short retry = 0;
		int errorCode = 0;

		if (lockMode.contains("Pessimistic_Force_Increment")) {
			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " getStockInstance: Before em.find with LockModeType.PESSIMISTIC_FORCE_INCREMENT");
				stock = em.find(StockJPA.class, key,
						LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " getStockInstance: After em.find with LockModeType.PESSIMISTIC_FORCE_INCREMENT");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", getStockInstance, " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				} else {
					throw new UserException("<&!@"
							+ "failure in getStockInstance: "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				}
			}
		} else {
			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " getStockInstance: Before em.find with LockModeType.OPTIMISTIC_FORCE_INCREMENT");
					stock = em.find(StockJPA.class, key,
							LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " getStockInstance: After em.find with LockModeType.OPTIMISTIC_FORCE_INCREMENT");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " em.find(StockJPA.class,key,LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", getStockInstance, " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					} else {
						throw new UserException("<&!@"
								+ "failure in getStockInstance: "
								+ " exception message: " + t.getMessage()
								+ ", exception cause: " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					}
				}
			}
		}
		if (stock == null) {
			throw new UserException("stock not found, item = " + outItemId
					+ " and stock = " + outItemSupplyWarehouseId + " "
					+ irwwbase.getSTC(), "");
		}
		irwwbase.debugOut("<<< Exit " + className + " getStockInstance");
	}
	*/

	protected void getItemInstance(EntityManager em, int itemId, String lockMode)
			throws UserException {

		irwwbase.debugOut("<<< Entering " + className + " getItemInstanc ");

		short retry = 0;
		int errorCode = 0;

		if (lockMode.contains("Pessimistic_Force_Increment")) {
			try {
				irwwbase.debugOut("<<< In "
						+ className
						+ " getItemInstance: Before em.find with LockModeType.PESSIMISTIC_FORCE_INCREMENT");
				item = em.find(ItemJPA.class, itemId,
						LockModeType.PESSIMISTIC_FORCE_INCREMENT);
				irwwbase.debugOut("<<< In "
						+ className
						+ " getItemInstance: After em.find with LockModeType.PESSIMISTIC_FORCE_INCREMENT");
			} catch (Exception t) {
				t.printStackTrace();
				if (irwwbase.is913(t)) {
					throw new UserException("<&!@" + irwwbase.checkFor913(t)
							+ ", getItemInstance, " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				} else {
					throw new UserException("<&!@"
							+ "failure in getItemInstance: "
							+ " exception message: " + t.getMessage()
							+ ", exception cause: " + t.getCause()
							+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
				}
			}
		} else {
			while ((retry == 0)
					|| ((retry <= numberOfRetries) && (errorCode == 1))) {
				try {
					retry++;
					irwwbase.debugOut("<<< In "
							+ className
							+ " getItemInstance: Before em.find with LockModeType.OPTIMISTIC_FORCE_INCREMENT");
					item = em.find(ItemJPA.class, itemId,
							LockModeType.OPTIMISTIC_FORCE_INCREMENT);
					irwwbase.debugOut("<<< In "
							+ className
							+ " getItemInstance: After em.find with LockModeType.OPTIMISTIC_FORCE_INCREMENT");
				} catch (OptimisticLockException t) {
					System.out
							.println("OptimisticLockException: retry = "
									+ retry
									+ " em.find(ItemJPA.class,itemId,LockModeType.OPTIMISTIC_FORCE_INCREMENT)");
					errorCode = 1;
				} catch (Exception t) {
					t.printStackTrace();
					errorCode = 2;
					if (irwwbase.is913(t)) {
						throw new UserException("<&!@"
								+ irwwbase.checkFor913(t)
								+ ", getItemInstance, " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					} else {
						throw new UserException("<&!@"
								+ "failure in getItemInstance: "
								+ " exception message: " + t.getMessage()
								+ ", exception cause: " + t.getCause()
								+ irwwbase.getSTC(), irwwbase.getStackTrace(t));
					}
				}
			}
		}
		if (item == null) {
			throw new UserException("item not found, item = " + item + " "
					+ irwwbase.getSTC(), "");
		}
		irwwbase.debugOut("<<< Exit " + className + " getItemInstance");
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

}
