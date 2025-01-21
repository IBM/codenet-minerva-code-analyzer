package stock.query;

import irwwbase.IRWWBase;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.Query;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import stockjpa.StockJPA;
import districtjpa.DistrictJPA;
import districtjpa.DistrictKey;

/**
 * Servlet implementation class StockQueryServlet
 */
// @WebServlet("/StockQueryServlet")
public class StockQueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit(unitName = "StockQuery")
	EntityManagerFactory emf;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public StockQueryServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		short districtId = Short.valueOf(request.getParameter("districtId"));
		short warehouseId = Short.valueOf(request.getParameter("warehouseId"));
		long threshold = Integer.valueOf(request.getParameter("threshold"));
		int nextOrderId = 0;

		startOfTran("StockLeveLite");
		String status = "SUCCESSFUL";
		String stack = "no stack";

		DistrictJPA district = null;
		IRWWBase ib = new IRWWBase();
		try {
			district = getDistrictInstance(districtId, warehouseId);
		} catch (Exception e) {
			status = "FAILED";
			stack = ib.getStackTrace(e);
		}
		nextOrderId = district.getDistrictNextOrderId();

		int foid = nextOrderId - 20;
		int noid = nextOrderId;
		int lowStock=0;
		try {
		lowStock=findStockThreshold(threshold, warehouseId, warehouseId,
				districtId, foid, noid);
		}
		catch (Exception e) {
			status = "FAILED";
			stack = ib.getStackTrace(e);			
		}

		String title = "Stock Query Results";

		out.println("<HTML><HEAD><TITLE>");
		out.println(title);
		out.println("</TITLE></HEAD><body bgcolor='#F8F7CD'>");
		out.println("<H3 align=\"left\">" + title + "</H3>");
		out.println("<p>Low stock is:"
				+ lowStock + "</p>");
		out.println("<p>Status: " + status + "</p>");
		out.println("<p>Stack: " + stack + "</p>");

		endOfTran("StockLevelLite");

	}

	public int findStockThreshold(long threshold, short stockWarehouseId,
			short olWarehouseId, short districtId, int firstOrderId,
			int nextOrderId) throws ServletException {

		EntityManager em = null;

		em = emf.createEntityManager();

		int lowStock = 0;
		
		//Comment out unused variable
		//java.util.Vector<StockJPA> v = new java.util.Vector<StockJPA>();

		debugOut("%%%%% In findStockThreshold - threshold = " + threshold);
		debugOut("%%%%% In findStockThreshold - stockWarehouseId = "
				+ stockWarehouseId);
		debugOut("%%%%% In findStockThreshold olWarehouseId = " + olWarehouseId);
		debugOut("%%%%% In findStockThreshold - CMP path - districtId = "
				+ districtId);
		debugOut("%%%%% In findStockThreshold - CMP path - firstOrderId = "
				+ firstOrderId);
		debugOut("%%%%% In findStockThreshold - CMP path - nextOrderId = "
				+ nextOrderId);

		try {
			Query q = (Query) em.createNamedQuery("findStockThreshold");
			q.setParameter("warehouseId", olWarehouseId);
			q.setParameter("districtId", districtId);
			q.setParameter("lowOrderId", firstOrderId);
			q.setParameter("highOrderId", nextOrderId);
			q.setParameter("stockWareHouseId", stockWarehouseId);
			q.setParameter("stockQuantity",
					java.math.BigDecimal.valueOf(threshold));
			Collection<?> coll = null;
			// q.setLockMode(LockModeType.OPTIMISTIC);
			coll = q.getResultList();

			// changes by surya
			Iterator<?> it = coll.iterator();
			StockJPA sja;
			while (it.hasNext()) {

				sja = (StockJPA) it.next();
				debugOut("StockItemID: " + sja.getS_I_ID() + " StockWareID: "
						+ sja.getS_W_ID() + " StockQuantity: "
						+ sja.getS_QUANTITY());
			}
			// end changes

			if (coll != null)
				lowStock = coll.size();
			//Comment out dead code
			//else;
			em.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(
					"<&!@"
							+ "Error doing complex query to determine lowStock in StockLevelSessionFacadeBean, olWid="
							+ olWarehouseId + ", did=" + districtId + ", foid="
							+ firstOrderId + ", nextOid=" + nextOrderId
							+ ", stkWid=" + stockWarehouseId
							+ " and threshold=" + threshold + ", "
							+ " exception message: " + e.getMessage()
							+ ", exception cause: " + e.getCause());
		}
		return lowStock;

	}

	protected DistrictJPA getDistrictInstance(short districtId,
			short warehouseId) throws ServletException {
		DistrictJPA district = null;

		try {
			EntityManager em = null;

			em = emf.createEntityManager();

			DistrictKey key = new DistrictKey();
			key.setDistrictId(districtId);
			key.setDistrictWareId(warehouseId);

			district = em.find(DistrictJPA.class, key);
			if (district == null)
				throw new ServletException("district not found, districtId="
						+ districtId + " and warehouseId=" + warehouseId);
			em.close();

		} catch (Exception t) {
			t.printStackTrace();
			throw new ServletException("<&!@"
					+ "failure in getDistrictInstance: "
					+ " exception message: " + t.getMessage()
					+ ", exception cause: " + t.getCause());
		} // end try

		return district;

	}

	public void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null)
			if (System.getProperty("DEBUG_OUT").equals("ON"))
				System.out.println(message);
	}

	public void startOfTran(String _className) {
		displayOut("<<<Running " + _className + "<<<Starting Time is: "
				+ new java.util.Date().toString() + ", ThreadId="
				+ Thread.currentThread().getId() + ", milliseconds: "
				+ String.valueOf(System.currentTimeMillis()));
	}

	public void endOfTran(String _className) {
		displayOut("<<< " + _className + " Completed, " + "<<<Ending Time is: "
				+ new java.util.Date().toString() + ", ThreadId="
				+ Thread.currentThread().getId() + ", milliseconds: "
				+ String.valueOf(System.currentTimeMillis()));
	}

	public void displayOut(String s) {
		if (System.getProperty("DISPLAY_OUT") == null);
			else if (System.getProperty("DISPLAY_OUT").equals("ON"))
			System.out.println(s);
		else
			;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}

}
