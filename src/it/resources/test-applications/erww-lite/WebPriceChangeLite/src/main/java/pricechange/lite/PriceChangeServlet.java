package pricechange.lite;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceUnit;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jakarta.transaction.UserTransaction;
import java.util.Arrays;

import irwwbase.ExtendedRandom;
import irwwbase.IRWWBase;
import irwwbase.MaxValues;
import irwwbase.POJOQualifier;
import itemjpa.ItemJPA;
import pricechangesession.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.annotation.security.DeclareRoles;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;

import com.ibm.websphere.cache.DistributedMap;
import com.ibm.websphere.cache.DynamicCacheAccessor;

/**
 * Servlet implementation class PriceChangeServlet
 */
@WebServlet(name="PriceChangeServlet", urlPatterns={"/PriceChangeServlet"})
@DeclareRoles("AllAuthenticatedUsersRole")
@ServletSecurity(value=@HttpConstraint(rolesAllowed={"AllAuthenticatedUsersRole"}))
public class PriceChangeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@PersistenceUnit(unitName = "PriceChangeLite")
	EntityManagerFactory emf;
	
	@EJB PriceChangeSessionFacadeBean pcNiv;
	
	@EJB PriceChangeSessionBMTBean pcBmtNiv;
	
	@Inject @POJOQualifier IRWWBase ib;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PriceChangeServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		InitialContext ic = null;
		
		DistributedMap map = null;	
		
		int maxNumberOfItemIds = 15;  //Need to invalidate itemId1 through itemId15
				
		String button = request.getParameter("command");

		ItemJPA input = null;
		
		boolean ejb = (Boolean.valueOf(request.getParameter("ejb"))).booleanValue();
		
		boolean bmt = (Boolean.valueOf(request.getParameter("useBmt"))).booleanValue();
		
		byte database = (byte) 1;

		if (button.trim().equalsIgnoreCase("manual"))
			input = getManualInput(request);
		else
			input = this.generatePriceChangeInput(database);

		//Comment out unused variables
		//String stack = null;
		//IRWWBase ib = new IRWWBase();
		PriceChangeOutput output = new PriceChangeOutput();
		//String status = "Status: FAILED";  //Assume failure unless the transaction is successful
		if (ejb) {
			if (!bmt) {
				try {
					output = pcNiv.priceChangeSession(input);
					ib.debugOut("After pcNiv.priceChangeSession(input) ");
					output.setStatus("Status: SUCCESSFUL");
				} catch (Throwable e) {
					e.printStackTrace();
					output.setMessage((e.getMessage()));
					output.setStack(Arrays.toString(e.getStackTrace()).replaceAll(",","\n"));
					output.setStatus("Status: FAILED");
				}
			} else {
				try {
					output = pcBmtNiv.priceChangeSession(input);
					ib.debugOut("After pcBmtNiv.priceChangeSession(input) ");
					output.setStatus("Status: SUCCESSFUL");
				} catch (Throwable e) {
					e.printStackTrace();
					output.setMessage((e.getMessage()));
					output.setStack(Arrays.toString(e.getStackTrace()).replaceAll(",","\n"));
					output.setStatus("Status: FAILED");
				}
			}
		} else {
			try {
				output = priceChangeServlet(input);
				output.setStatus("Status: SUCCESSFUL");
			} catch (Throwable e) {
				e.printStackTrace();
				output.setMessage((e.getMessage()));
				output.setStack(Arrays.toString(e.getStackTrace()).replaceAll(",","\n"));
				output.setStatus("Status: FAILED");
			}
		}
		HttpSession sess = request.getSession();

		sess.setAttribute("oBean", output);
		String nextPage = "PCResultsForm.jsp";

		try {
			if (DynamicCacheAccessor.isServletCachingEnabled()) {
				ic = new InitialContext();
				map = (DistributedMap) ic.lookup("services/cache/basecache");
				if (map != null){
					for (int i = 1; i < (maxNumberOfItemIds+1); i++) {
						map.invalidate("itemId"+i+"_ItemTable_Manual:"+input.getItemId());
						ib.debugOut("Dynacache Invalidatation in PriceChange - " + "itemId"+i+"_ItemTable_Manual:"+input.getItemId());
						
						map.invalidate("itemId"+i+"_ItemTable_AutoGen:"+input.getItemId());
						ib.debugOut("Dynacache Invalidatation in PriceChange - " + "itemId"+i+"_ItemTable_AutoGen:"+input.getItemId());
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			output.setMessage((e.getMessage()));
			output.setStack(Arrays.toString(e.getStackTrace()).replaceAll(",","\n"));
			output.setStatus("Status: FAILED");
		}

		// Forward the request to the next page
		dispatch(request, response, nextPage);
	}

	public PriceChangeOutput priceChangeServlet(ItemJPA input)
			throws ServletException {
		//IRWWBase ib = new IRWWBase();
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
 		ib.startOfTran("PriceChangeServlet Version");

		ib.debugOut("Running PriceChangeServlet priceChangeServlet()");

		ItemJPA itemJPA = null;
		PriceChangeOutput output = new PriceChangeOutput();

		try {
			em = emf.createEntityManager();
			ut.begin();
			em.joinTransaction();
			ib.debugOut("After Begin Transaction");

			itemJPA = em.find(ItemJPA.class, input.getItemId(),
					LockModeType.PESSIMISTIC_WRITE);

			if (itemJPA != null) {

				ib.debugOut("Item instance is not null");

				if (input.getItemPrice().doubleValue() >= 1000) {
					System.out
							.println("<<< Somehow itemPrice exceeded 999.99, itemPrice ="
									+ input.getItemPrice()
									+ " resetting to 999.99");
					input.setItemPrice(new BigDecimal(999.99));
				}

				itemJPA.setItemPrice(input.getItemPrice().setScale(2,
						RoundingMode.DOWN));
				ib.debugOut("Item Price was set");
				if (em.contains(itemJPA))
					ib.debugOut("mgd_");

				ib.debugOut(" item entity values, itemId: "
						+ itemJPA.getItemId() + " price: "
						+ itemJPA.getItemPrice());
			} else {
				ut.rollback();
				throw new ServletException("item not found, item="
						+ input.getItemId() + ", " + ib.getSTC() + "no stack");
			}
			
			output.setItem(itemJPA);
			
			ut.commit();

			itemJPA.setStcnum(irwwbase.Stcnum.getInstance().getStcnum());
			// end
		} catch (Exception e) {
			if (ib.is913(e))
				throw new ServletException("<&!@" + ib.checkFor913(e)
						+ ", finding Item JPA, " + e.getCause() + ib.getSTC()
						+ ib.getStackTrace(e));
			else
				throw new ServletException("<&!@"
						+ "Error while processing PriceChange, "
						+ " exception message: " + e.getMessage()
						+ ", exception cause: " + e.getCause() + ib.getSTC()
						+ ib.getStackTrace(e));
		}		
		ib.endOfTran("PriceChangeServlet");
		
		try {
			if (em!=null && em.isOpen()){
				em.close();
			}

		 }catch(Exception e){
				System.out.println("<<< PriceChangeServlet - em close() - Exception: " + e);
				e.printStackTrace();				
		 }

		return output;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);

	}

	private ItemJPA generatePriceChangeInput(byte db)
			throws ServletException {

		ExtendedRandom rand = new ExtendedRandom();
		ItemJPA input = new ItemJPA();
		IRWWBase ib = new IRWWBase();

		try {
			int itemId = rand.nextInt(1, MaxValues.itemId(db));
			ib.debugOut("generatePriceChangeInput - itemId = " + itemId
					+ " db = " + db);
			double newPrice = rand.nextDouble(MaxValues.itemPrice(db));
			if (newPrice >= 1000)
				newPrice = 999.99;
			ib.debugOut("generatePriceChangeInput - newPrice = " + newPrice);

			input.setItemId(itemId);
			input.setItemPrice(new BigDecimal(newPrice));
		} catch (java.lang.Exception ex) {
			throw new ServletException("error genning priceChange input data",
					ex);

		}
		return input;
	}

	public void dispatch(HttpServletRequest request,
			HttpServletResponse response, String nextPage)
			throws ServletException, IOException {

		RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
		dispatch.forward(request, response);
	}

	private ItemJPA getManualInput(HttpServletRequest request) {
		ItemJPA input = new ItemJPA();
		input.setItemId(Integer.valueOf(request.getParameter("itemId")));
		input.setItemPrice(new BigDecimal(request.getParameter("price")));

		return input;
	}

}
