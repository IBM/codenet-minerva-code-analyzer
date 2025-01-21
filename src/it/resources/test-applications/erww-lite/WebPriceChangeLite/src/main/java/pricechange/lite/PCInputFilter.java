package pricechange.lite;

import irwwbase.POJOQualifier;

import java.io.IOException;
import java.sql.Timestamp;

import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * @version 	1.0
 * @author
 */
public class PCInputFilter implements Filter {
	
	@Inject @POJOQualifier irwwbase.IRWWBase ib;
	
	/**************************************************************
	* Allows you to turn debug statements on/off in jvm.properties
	*/
	public void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT_FILTER") != null) {
			if (System.getProperty("DEBUG_OUT_FILTER").equals("ON")) {
				String ts =
					(new Timestamp(System.currentTimeMillis())).toString();
				String tn = Thread.currentThread().getName();
				System.out.println(
					"Trace JustPC - PCVerifyInputFilter: "
						+ ts
						+ " "
						+ tn
						+ " "
						+ message);
			}
		}
	}

	private FilterConfig filterConfig;

	/**
	* @see jakarta.servlet.Filter#void ()
	*/
	public void destroy() {
		debugOut("PCVerifyInputFilter Destroyed: ");

	}

	/**
	* @see jakarta.servlet.Filter#void (jakarta.servlet.ServletRequest, jakarta.servlet.ServletResponse, jakarta.servlet.FilterChain)
	*/
	public void doFilter(
		ServletRequest req,
		ServletResponse resp,
		FilterChain chain)
		throws ServletException, IOException {

		RequestDispatcher rd = null;
		//Comment out unused variable
		//boolean emptyform = false;

		int itemIdValue = 0;
		String itemIdValueString = null;
		double priceValue = 0;
		String priceValueString = null;
		String buttonSelection = null;

		buttonSelection = req.getParameter("command");
		
		ib.displayOut("In PCVerifyInput filter (using injected reference)");

		if (!(buttonSelection.compareTo("AutoGeneration") == 0) && !(buttonSelection.compareTo("CMM")==0)) {
			try {
				ib.debugOut("PCVerifyInputFilter - Testing itemID datatype");
				itemIdValueString = req.getParameter("itemId");
				itemIdValue = Integer.valueOf(itemIdValueString).intValue();
			} catch (Exception e) {
				ib.debugOut("PCVerifyInputFilter - Failed itemID datatype test");
				req.setAttribute("invalidInput", "ItemId must be Integer");
				rd = req.getRequestDispatcher("/index.html");
				ib.debugOut("PCVerifyInputFilter - Returned from itemId Catch Request Dispatch");
			}
			try {
				ib.debugOut("PCVerifyInputFilter - Testing itemID range");
				if (!((0 < itemIdValue) && (itemIdValue < 100000))) {
					ib.debugOut("PCVerifyInputFilter - Failed itemID range test");
					throw new Exception("PCVerifyInputFilter - Failed itemID range test");
				}
			} catch (Exception e) {
				req.setAttribute(
					"invalidInput",
					"ItemId must an integer and be greater than 0, and less than 100000");
				rd = req.getRequestDispatcher("/index.html");
			}
			try {
				ib.debugOut("PCVerifyInputFilter - Testing price datatype");
				priceValueString = req.getParameter("price");
				priceValue = Double.valueOf(priceValueString).doubleValue();
			} catch (Exception e) {
				ib.debugOut("PCVerifyInputFilter - Failed price datatype test");
				req.setAttribute("invalidInput", "Price must be Decimal Value");
				rd = req.getRequestDispatcher("/index.html");
				ib.debugOut("PCVerifyInputFilter - Returned from price Catch Request Dispatch");
			}
			try {
				ib.debugOut("PCVerifyInputFilter - Testing price range");
				if (!((0 < priceValue) && (priceValue < 1000))) {
					ib.debugOut("PCVerifyInputFilter - Failed price range test");
					throw new Exception("PCVerifyInputFilter - Failed price range test");
				}
			} catch (Exception e) {
				req.setAttribute(
					"invalidInput",
					"Price must be a decimal greater than 0, and less than $1000");
				rd = req.getRequestDispatcher("/index.html");
			}
		}

		if (rd == null) {
			// The data fields were entered correctly
			chain.doFilter(req, resp);
		} else {
			// error in the data fields were entered correctly
			rd.forward(req, resp);
		}

	}

	public FilterConfig getFilterConfig() {
		return this.filterConfig;
	}

	public void setFilterConfig(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

	//      =======================================================

	/**
	* Method init.
	* @param config
	* @throws jakarta.servlet.ServletException
	*/
	public void init(FilterConfig config) throws ServletException {

	}

}