package orderstatus.lite;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import irwwbase.*;


/**
 * Servlet implementation class OSController
 */
@WebServlet("/OSController")
public class OSController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private java.lang.String className = this.getClass().toString();
	IRWWBase ib = new IRWWBase();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OSController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			ib.debugOut("*** Entering doPost method of OSController ***");
			
			ib.startOfTran("WebOrderStatusLite");
		    //Comment out unused variable
			//short ol_number = 0; // counter for"current"item
		    String stcNum = "none";
		    
		    String inputType = "AutoGen";
		    short warehouseId = 0;
		    short districtId = 0;
		    short customerId = 0;
		    String customerLastName = null;
		    boolean byLastName = false;
		    
		    byte db = ib.dbSize();
			
			String dataSourceJNDIName = "java:comp/env/jdbc/ERWWDataSourceWithPwd";
			Connection con = null; 

			// Set times
			/* Comment out unused variables
			java.sql.Timestamp timestamp = null; 
			java.util.Date date = java.util.Calendar.getInstance().getTime();
			timestamp = new java.sql.Timestamp(date.getTime());
			*/
			OrderStatusOutput output = new OrderStatusOutput();
			
			//Assume failure until the web app succeeds
			output.setRetc("Failed");
						
			inputType = request.getParameter("inputType");
			
			if (inputType.contains("Manual")){
				warehouseId = Short.valueOf(request.getParameter("warehouseId"));
				districtId = Short.valueOf(request.getParameter("districtId"));
				customerId = Short.valueOf(request.getParameter("customerId"));
				customerLastName = request.getParameter("customerLastName");
				if (customerId <= 0){
					byLastName = true;
				}
			} else {
				ExtendedRandom rand = new ExtendedRandom();
				warehouseId = ((short) rand.nextInt(1, MaxValues.warehouseId(db)));
				districtId = ((short) rand.nextInt(1, MaxValues.districtId(db)));
				customerId = ((short) rand.nextInt(1, MaxValues.customerId(db)));
			}
	   
			try{
				con = ib.getConnection(ib.getDatasource(dataSourceJNDIName));
			} catch(java.sql.SQLException sqle){
				System.out.println("<<< " + className + " - SQLException: " + sqle);
				sqle.printStackTrace();	
				throw new ServletException (new java.sql.SQLException("<<< " + className + " - SQLException: " + sqle));
			} catch(ServletException sre){
				System.out.println("<<< " + className + " - ServletException: " + sre);
				sre.printStackTrace();	
				throw new ServletException("<<< " + className + " - ServletException: " + sre);
			} catch(Exception e){
				System.out.println("<<< " + className + " - Exception: " + e);
				e.printStackTrace();
				throw new RuntimeException("<<< " + className + " - Exception: " + e);
			} 

			// Set warehouseId and districtId in output
			output.setOutCustomerWarehouseId(warehouseId);
			output.setOutCustomerDistrictId(districtId);
			output.setOutCustomerId(customerId);
			
	    	ib.debugOut("<<< " + className + " customerWarehouseId = " + warehouseId);
	    	ib.debugOut("<<< " + className + " customerDistrictId = " + districtId);
	    	ib.debugOut("<<< " + className + " customerId = " + customerId);
	    	ib.debugOut("<<< " + className + " customerLastName = " + customerLastName);
	    	ib.debugOut("<<< " + className + " byLastName = " + byLastName);

		// **********************************************************************
			// 2.6.2.2 bullet 3
			// **********************************************************************
	    	
			// Search for customer (either by ID or by last name)
	    	if (byLastName){
				//2.6.2.2 bullet 3 - Case 1
				ib.debugOut("<<<in by Customer Last Name");
				customerId = this.getCustomerByLastName(districtId, warehouseId, customerLastName, con, output);
			} else {
				//	2.6.2.2 bullet 3 - Case 2
				ib.debugOut("<<<in by Customer Id");
				this.getCustomerById(customerId, districtId, warehouseId, con, output);
			}
	    	
			// ****************************************************************************************
			// 2.6.2.2 bullet 4
			// the row in the order table with matching O_W_ID (equals C_W_ID),
			// O_D_ID (equals C_D_ID),
			// O_C_ID (equals C_ID), and with the largest existing O_ID, is
			// selected. This is the most
			// recent order placed by the customer. O_ID, O_ENTRY_DATE, and
			// O_CARRIER_ID are retrieved.
			// ****************************************************************************************
			
			this.getOrder(customerId, districtId, warehouseId, con, output);
			
			// **********************************************************************************************
			// 2.6.2.2 bullet 5
			// all rows in the orderline table with matching OL_W_ID (equals
			// O_W_ID), OL_D_ID (equals O_D_ID),
			// and OL_O_ID (equals O_ID) are selected and the corresponding set of
			// OL_I_ID, OL_SUPPLY_W_ID,
			// OL_QUANTITY, OL_AMOUNT, and OL_DELIVERY_D are retrieved.
			// ***********************************************************************************************
			this.getOrderlines(customerId, districtId, warehouseId, con, output);

			output.setRetc("Successful");
			
			request.setAttribute("output", output);
			
			ib.endOfTran("WebOrderStatusLite");
			
			dispatch(request, response, "OSResultsForm.jsp");

			try {				
				ib.closeConnection(con);
		    } catch (SQLException | IOException ex) {
				System.out.println("<<< " + className
						+ " - Cleanup - Exception: " + ex);
				ex.printStackTrace();
				throw new ServletException("<<< "
						+ className + " - Cleanup - Exception: " + ex);
			} 
			catch (Exception ex) {
				System.out.println("<<< " + className
						+ " - Cleanup - Exception: " + ex);
				ex.printStackTrace();
				throw new ServletException("<<< "
						+ className + " - Cleanup - Exception: " + ex);
			}  finally {
				try {
					if (output != null){
						this.printOrderStatusOutput(output);
					}
					if (Stcnum.getInstance().getStcnum() != null){
						stcNum = Stcnum.getInstance().getStcnum();
						ib.debugOut("<<< " + className + " stcNum = " + stcNum);
					}
					output.setStcnum(stcNum);
					ib.debugOut("<<< Exiting: " + className + ib.getCurrentTime());
				} catch(Exception e){
					System.out.println("<<< " + className + " - Exception: " + e);
					e.printStackTrace();	
					throw new RuntimeException("<<< " + className + " - Exception: " + e);
				}
			}
	}
			
	private short getCustomerByLastName(short districtId, short warehouseId,
			String customerLastName, Connection con, OrderStatusOutput output)
			throws ServletException, IOException {

		java.sql.PreparedStatement aPS = null;
		java.sql.ResultSet rs = null;
		String aSqlString = null;
		/* Comment out unused variables
		String c_first_name = null;
		String c_middle_name = null;
		String c_last_name = null;
		BigDecimal c_balance = new BigDecimal(0);
		*/
		short customerId = 0;

		int noCustomers = 0;
		int noCustDiv2 = 0;
		int remainder = 0;
		java.util.Vector v = new java.util.Vector();
		boolean moreRecords = false;
		ib.debugOut("<<< " + className + " - getting Customer by 'Last Name'");
		ib.debugOut("<<< " + className + " - getting Customer by 'Last Name'");

		try {
			aSqlString = "SELECT C_ID FROM CUSTOMER WHERE C_D_ID = ? and C_W_ID = ? and C_LAST = ? WITH CS";
			aPS = con.prepareStatement(aSqlString);
			aPS.setShort(1, districtId);
			aPS.setShort(2, warehouseId);
			aPS.setString(3, customerLastName);
			rs = aPS.executeQuery();
			
			while (moreRecords = rs.next()) {
				v.addElement(rs.getShort(1));
				noCustomers++;
				ib.debugOut("<<< "
						+ className
						+ " after v.addElement(rs.getShort(1)) where Customer ID = "
						+ rs.getShort(1));
			}

			if ((moreRecords == false) && (noCustomers == 0)) {
				ib.debugOut("<<< "
						+ className
						+ " There are no more records and number of customers equal 0");
				throw new ServletException(" Number of Customers =  "
						+ noCustomers, new Exception(" not found"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ServletException(
					"SQLException " + e.getMessage(),e);
		} finally {
	        try {
	        	ib.closeResultSet(rs);
	        	ib.closePreparedStatement(aPS);
	        	ib.debugOut("<<< Exiting: " + className + ".getCustomerByLastName: at " + ib.getCurrentTime());						
			} catch(java.sql.SQLException sqle) {
				System.out.println("<<< " + className + " - getCustomerByLastName failed - SQLException: " + sqle);
				sqle.printStackTrace();	
				throw new ServletException("<<< " + className + " - getCustomerByLastName failed - SQLException: " + sqle);
			} catch(java.io.IOException ioe) {
				System.out.println("<<< " + className + " - getCustomerByLastName failed - IOException: " + ioe);
				ioe.printStackTrace();	
				throw new java.io.IOException("<<< " + className + " - getCustomerByLastName failed - IOException: " + ioe);
			} catch (Exception ex) {
				System.out.println("<<< ERROR: " + className + " - getCustomerByLastName failed with Exception: " + ex);
				ex.printStackTrace();
				throw new RuntimeException("<<< " + className + " - getCustomerByLastName failed - Exception: " + ex);		
			}
	    }

		if (noCustomers == 1) {
			noCustDiv2 = 1;
		} else {
			// Divide number of selected rows by 2, rounding up if you have a
			// remainder.
			// (i.e., 5/3=1)
			noCustDiv2 = noCustomers / 2;
			remainder = noCustomers % 2;

			if (remainder > 0) {
				noCustDiv2 = noCustDiv2 + 1;
			}
		}

		ib.debugOut("<<< " + className + "Number of Customers = " + noCustomers);
		ib.debugOut("<<< " + className + "Vector Elements = " + v.elements());

		try {
			customerId = (Short) v.elementAt(noCustDiv2 - 1);
			output.setOutCustomerId(customerId);
		} catch (Exception ex) {
			System.out.println("<<< ERROR: " + className
					+ " - getCustomerByLastName - Exception: " + ex);
			ex.printStackTrace();
			throw new RuntimeException("<<< " + className
					+ " - getCustomerByLastName - Exception: " + ex);
		}

		ib.debugOut("<<< " + className + "Customer ID = " + customerId);
		ib.debugOut("<<< " + className + "District ID = " + districtId);
		ib.debugOut("<<< " + className + "Warehouse ID = " + warehouseId);

		try {
			aSqlString = "SELECT C_FIRST, C_MIDDLE, C_LAST, C_BALANCE "
					+ " FROM CUSTOMER WHERE C_ID = ? and C_D_ID = ? and C_W_ID = ? WITH CS";
			aPS = con.prepareStatement(aSqlString);
			aPS.setShort(1, customerId);
			aPS.setShort(2, districtId);
			aPS.setShort(3, warehouseId);
		    rs = aPS.executeQuery();

			if (rs.next() == true) {
				ib.debugOut("<<< "
						+ className
						+ " getCustomerByLastName - record is found where customer ID = "
						+ customerId);
			} else {
				throw new ServletException(
						"Customer ID " + customerId,
						new Exception(" not found"));
			}

			output.setOutCustomerFirstName(rs.getString(1));
			output.setOutCustomerMiddleName(rs.getString(2));
			output.setOutCustomerLastName(rs.getString(3));
			output.setOutCustomerBalance(rs.getBigDecimal(4).doubleValue());

		} catch (SQLException e) {
			
			e.printStackTrace();
			throw new ServletException(
					"SQLException " + e.getMessage(),e);
		} finally {
	        try {
	        	ib.closeResultSet(rs);
	        	ib.closePreparedStatement(aPS);
	        	ib.debugOut("<<< Exiting: " + className + ".getCustomerByLastName: at " + ib.getCurrentTime());						
			} catch(java.sql.SQLException sqle) {
				System.out.println("<<< " + className + " - getCustomerByLastName failed - SQLException: " + sqle);
				sqle.printStackTrace();	
				throw new ServletException("<<< " + className + " - getCustomerByLastName failed - SQLException: " + sqle);
			} catch(java.io.IOException ioe) {
				System.out.println("<<< " + className + " - getCustomerByLastName failed - IOException: " + ioe);
				ioe.printStackTrace();	
				throw new java.io.IOException("<<< " + className + " - getCustomerByLastName failed - IOException: " + ioe);
			} catch (Exception ex) {
				System.out.println("<<< ERROR: " + className + " - getCustomerByLastName failed with Exception: " + ex);
				ex.printStackTrace();
				throw new RuntimeException("<<< " + className + " - getCustomerByLastName failed - Exception: " + ex);		
			}
	    }

		return customerId;

	}

	private void getCustomerById(short customerId, short districtId, short warehouseId, Connection con, OrderStatusOutput output)throws ServletException, IOException {

		java.sql.PreparedStatement aPS = null;
		java.sql.ResultSet rs = null;
		String aSqlString = null;
		/*Comment out unused variables
		String c_first_name = null;
		String c_middle_name = null;
		String c_last_name = null;
		BigDecimal c_balance = new BigDecimal(0);
		*/
		try {
			ib.debugOut("<<< " + className + " - getting Customer by 'ID'");		
			
			ib.debugOut("<<< " + className + " getCustomerById - customerWarehouseId = " + warehouseId);
			ib.debugOut("<<< " + className + " getCustomerById -  customerDistrictId = " + districtId);
			ib.debugOut("<<< " + className + " getCustomerById -  customerId = " + customerId);

			aSqlString = "SELECT C_FIRST, C_MIDDLE, C_LAST, C_BALANCE " +
						 " FROM CUSTOMER WHERE C_ID = ? and C_D_ID = ? and C_W_ID = ?" +
						 " WITH CS";
			aPS = con.prepareStatement(aSqlString);
			aPS.setShort(1, customerId);
			aPS.setShort(2, districtId);
			aPS.setShort(3, warehouseId);
			rs = aPS.executeQuery();
			
			if (rs.next() == true) {
				ib.debugOut("<<< " + className + " getCustomerById - record is found where customer ID = " + customerId);
			} else {
				throw new ERWWDataNotFoundException("Customer ID "+ customerId,  new Exception(" not found"));
			}
			
			output.setOutCustomerFirstName(rs.getString(1) );
			output.setOutCustomerMiddleName(rs.getString(2) );
			output.setOutCustomerLastName(rs.getString(3) );
			output.setOutCustomerBalance(rs.getBigDecimal(4).doubleValue() );  

		} catch(java.sql.SQLException sqle){
			System.out.println("<<< " + className + " - getCustomerById - SQLException: " + sqle);
			sqle.printStackTrace();	
			throw new ServletException("<<< " + className + " - getCustomerById failed - SQLException: " + sqle);
		} catch(Exception e){
			System.out.println("<<< " + className + " - getCustomerById failed - Exception: " + e);
			e.printStackTrace();
			throw new RuntimeException("<<< " + className + " - getCustomerById failed - Exception: " + e);
		} finally {
			try {
				ib.closeResultSet(rs);
				ib.closePreparedStatement(aPS);				
				ib.debugOut("<<< Exiting: " + className + ".getCustomerById: at " + ib.getCurrentTime());						
			} catch(java.sql.SQLException sqle){
				System.out.println("<<< " + className + " - getCustomerById failed - SQLException: " + sqle);
				sqle.printStackTrace();	
				throw new ServletException("<<< " + className + " - getCustomerById failed - SQLException: " + sqle);
			} catch(java.io.IOException ioe){
				System.out.println("<<< " + className + " - getCustomerById failed - IOException: " + ioe);
				ioe.printStackTrace();	
				throw new java.io.IOException("<<< " + className + " - getCustomerById failed - IOException: " + ioe);
			} catch (Exception ex) {
				System.out.println("<<< ERROR: " + className + " - getCustomerById failed with Exception: " + ex);
				ex.printStackTrace();
				throw new RuntimeException("<<< " + className + " - getCustomerById failed - Exception: " + ex);		
			}
		}
	}
			
	private void getOrder(short customerId, short districtId, short warehouseId, Connection con, OrderStatusOutput output )throws ServletException, IOException {
		
		java.sql.PreparedStatement aPS = null;
		java.sql.ResultSet rs = null;
		String aSqlString = null;
	    /*Comment out unused variables
		int orderId = 0;
		int currentOrderId = 0;
		*/
		int largestOrderId = 0;
		
		ib.debugOut("<<< " + className + " getOrder - customerId = " + customerId);
		ib.debugOut("<<< " + className + " getOrder - customerWarehouseId = " + warehouseId);
		ib.debugOut("<<< " + className + " getOrder - customerDistrictId = " + districtId);
		
		try {
			ib.debugOut("<<< " + className + " - getting Order");	
			aSqlString = "SELECT MAX(O_ID) AS largestO_ID FROM ORDERS WHERE O_C_ID = ? and O_W_ID = ? and O_D_ID = ? WITH CS";	
			aPS = con.prepareStatement(aSqlString);
			aPS.setShort(1, customerId);
			aPS.setShort(2, warehouseId);
			aPS.setShort(3, districtId);
			rs = aPS.executeQuery();
			
			if (rs.next()) {
				largestOrderId = rs.getInt("largestO_ID");
			}
		} catch(java.sql.SQLException sqle){
			System.out.println("<<< " + className + " - getOrder - SQLException: " + sqle);
			sqle.printStackTrace();	
			throw new ServletException("<<< " + className + " - getOrder - SQLException: " + sqle);
		} catch (Exception ex) {
			System.out.println("<<< ERROR: " + className + " - getOrder - Exception: " + ex);
			ex.printStackTrace();
			throw new RuntimeException("<<< " + className + " - getOrder failed - Exception: " + ex);				
		} finally {
	        try {
	        	ib.closeResultSet(rs);
	        	ib.closePreparedStatement(aPS);
	        	ib.debugOut("<<< Exiting: " + className + ".getOrder: at " + ib.getCurrentTime());						
			} catch(java.sql.SQLException sqle) {
				System.out.println("<<< " + className + " - getOrder failed - SQLException: " + sqle);
				sqle.printStackTrace();	
				throw new ServletException("<<< " + className + " - getOrder failed - SQLException: " + sqle);
			} catch(java.io.IOException ioe) {
				System.out.println("<<< " + className + " - getOrder failed - IOException: " + ioe);
				ioe.printStackTrace();	
				throw new java.io.IOException("<<< " + className + " - getOrder failed - IOException: " + ioe);
			} catch (Exception ex) {
				System.out.println("<<< ERROR: " + className + " - getOrder failed with Exception: " + ex);
				ex.printStackTrace();
				throw new RuntimeException("<<< " + className + " - getOrder failed - Exception: " + ex);		
			}
	    }
		
		ib.debugOut("<<< " + className + " - largestOrderId = " + largestOrderId);
		
		try {
			aSqlString = "SELECT O_ENTRY_D, O_CARRIER_ID, O_OL_CNT FROM ORDERS WHERE O_ID = ? and O_D_ID = ? and O_W_ID = ? WITH CS";	
			aPS = con.prepareStatement(aSqlString);
			aPS.setInt(1, largestOrderId);
			aPS.setShort(2, districtId);
			aPS.setShort(3, warehouseId);
			rs = aPS.executeQuery();
			
			if (rs.next()==true) {
				output.setOutOrderId(largestOrderId);
				output.setOutOrderEntryDate(rs.getString(1));
				output.setOutOrderCarrierId(rs.getShort(2));
				output.setOutOrderLineCount(rs.getShort(3));
			} else {
				System.out.println("<<< ERROR: " + className + " - getOrder failed since the Order is not found:  " +
						"Warehouse Id: " + warehouseId +  " District Id: " + districtId + " Order Id: " + largestOrderId);
				throw new RuntimeException("<<< " + className + " - getOrder failed since the Order is not found:  " +
						"Warehouse Id: " + warehouseId +  " District Id: " + districtId + " Order Id: " + largestOrderId);
			}
		} catch(java.sql.SQLException sqle){
			System.out.println("<<< " + className + " - getOrder - SQLException: " + sqle);
			sqle.printStackTrace();	
			throw new ServletException("<<< " + className + " - getOrder - SQLException: " + sqle);
		} catch (Exception ex) {
			System.out.println("<<< ERROR: " + className + " - getOrder - Exception: " + ex);
			ex.printStackTrace();
			throw new RuntimeException("<<< " + className + " - getOrder failed - Exception: " + ex);
		} finally {
	        try {
	        	ib.closeResultSet(rs);
	        	ib.closePreparedStatement(aPS);
	        	ib.debugOut("<<< Exiting: " + className + ".getOrder: at " + ib.getCurrentTime());						
			} catch(java.sql.SQLException sqle) {
				System.out.println("<<< " + className + " - getOrder failed - SQLException: " + sqle);
				sqle.printStackTrace();	
				throw new ServletException("<<< " + className + " - getOrder failed - SQLException: " + sqle);
			} catch(java.io.IOException ioe) {
				System.out.println("<<< " + className + " - getOrder failed - IOException: " + ioe);
				ioe.printStackTrace();	
				throw new java.io.IOException("<<< " + className + " - getOrder failed - IOException: " + ioe);
			} catch (Exception ex) {
				System.out.println("<<< ERROR: " + className + " - getOrder failed with Exception: " + ex);
				ex.printStackTrace();
				throw new RuntimeException("<<< " + className + " - getOrder failed - Exception: " + ex);		
			}
	    }
		
	 }

	private void getOrderlines(short customerId, short districtId, short warehouseId, Connection con, OrderStatusOutput output)throws ServletException, IOException {

		java.sql.PreparedStatement aPS = null;
		java.sql.ResultSet rs = null;
		String aSqlString = null;
		String deliveryDate = null;
	    output.outOrderLines = new OrderLineSingleItem[output.getOutOrderLineCount()];
		int ol_number = 0;
    	short olSupplyWarehouseId = 0;
    	int olItemId = 0;
    	BigDecimal olQuantity = new BigDecimal(0);
    	BigDecimal olAmount = new BigDecimal(0);
    	String olDeliveryDate = null;
		
    	ib.debugOut("<<< " + className + " - olOrderId = " + output.getOutOrderId());
    	ib.debugOut("<<< " + className + " - olDistrictId = " + districtId);
    	ib.debugOut("<<< " + className + " - olWarehouse = " + warehouseId);
		
		try {
			aSqlString = "SELECT OL_SUPPLY_W_ID, OL_I_ID, OL_QUANTITY, OL_AMOUNT, OL_DELIVERY_D FROM ORDERLINE " + 
						 " WHERE OL_O_ID = ? AND OL_D_ID = ? AND OL_W_ID = ? WITH CS";	
			aPS = con.prepareStatement(aSqlString);
			aPS.setInt(1, output.getOutOrderId());
			aPS.setShort(2, districtId);
			aPS.setShort(3, warehouseId);
			rs = aPS.executeQuery();
			
			while (rs.next()) {				    		
		    	olSupplyWarehouseId = rs.getShort(1);
		    	olItemId = rs.getInt(2);
		    	olQuantity = rs.getBigDecimal(3);
		    	olAmount = rs.getBigDecimal(4);
		    	olDeliveryDate = rs.getString(5);

		    	ib.debugOut("<<< " + className + " - olSupplyWarehouseId = " + olSupplyWarehouseId);
		    	ib.debugOut("<<< " + className + " - olItemId = " + olItemId);
		    	ib.debugOut("<<< " + className + " - olQuantity = " + olQuantity);
		    	ib.debugOut("<<< " + className + " - olAmount = " + olAmount);
		    	ib.debugOut("<<< " + className + " - olDeliveryDate = " + olDeliveryDate);
				
				output.outOrderLines[ol_number] = new OrderLineSingleItem();

				output.outOrderLines[ol_number].outOlSupplyWarehouseId = olSupplyWarehouseId;
				output.outOrderLines[ol_number].outOlItemId = olItemId;
				output.outOrderLines[ol_number].outOlQuantity = olQuantity.doubleValue();
				output.outOrderLines[ol_number].outOlAmount = olAmount.doubleValue();

				if (olDeliveryDate == null){
					output.outOrderLines[ol_number].outOlDeliveryDate = "99-99-9999";
				} else {
					deliveryDate = olDeliveryDate;
					output.outOrderLines[ol_number].outOlDeliveryDate = deliveryDate;
			    }
				ol_number++;
		    }
			
		} catch(java.sql.SQLException sqle){
			System.out.println("<<< " + className + " - OrderLine Object - SQLException: " + sqle);
			sqle.printStackTrace();	
			throw new ServletException("<<< " + className + " - OrderLine Object - SQLException: " + sqle);
		} catch(Exception e){
			System.out.println("<<< " + className + " - OrderLine Object - Exception: " + e);
			e.printStackTrace();
			throw new RuntimeException("<<< " + className + " - OrderLine Object - Exception: " + e);
		} finally {
	        try {
	        	ib.closeResultSet(rs);
	        	ib.closePreparedStatement(aPS);
	        	ib.debugOut("<<< Exiting: " + className + ".getOrder: at " + ib.getCurrentTime());						
			} catch(java.sql.SQLException sqle) {
				System.out.println("<<< " + className + " - getOrder failed - SQLException: " + sqle);
				sqle.printStackTrace();	
				throw new ServletException("<<< " + className + " - getOrder failed - SQLException: " + sqle);
			} catch(java.io.IOException ioe) {
				System.out.println("<<< " + className + " - getOrder failed - IOException: " + ioe);
				ioe.printStackTrace();	
				throw new java.io.IOException("<<< " + className + " - getOrder failed - IOException: " + ioe);
			} catch (Exception ex) {
				System.out.println("<<< ERROR: " + className + " - getOrder failed with Exception: " + ex);
				ex.printStackTrace();
				throw new RuntimeException("<<< " + className + " - getOrder failed - Exception: " + ex);		
			}
	    }
			
	}
			
	public void printOrderStatusOutput(OrderStatusOutput output) throws ServletException, IOException {
		ib.debugOut("<< " + this.getClass().toString() + ".printOrderStatusOutput - Entered");
	    int length = 0;		        
	    length = output.getOutOrderLines().length;
	
	    try {
	    	ib.debugOut("<<< Output Warehouse ID: " + output.getOutCustomerWarehouseId());
	    	ib.debugOut("<<< Output District ID:  " + output.getOutCustomerDistrictId());
	    	ib.debugOut("<<< Output Customer ID: " + output.getOutCustomerId());
	    	ib.debugOut("<<< Output Customer First Name:  " + output.getOutCustomerFirstName());
	    	ib.debugOut("<<< Output Customer Middle Name: " + output.getOutCustomerMiddleName());
	    	ib.debugOut("<<< Output Customer Last Name:  " + output.getOutCustomerLastName());
	    	ib.debugOut("<<< Output Customer Customer Balance : " + output.getOutCustomerBalance());
	    	ib.debugOut("<<< Output Order ID:  " + output.getOutOrderId());
	    	ib.debugOut("<<< Output Order Entry Date: " + output.getOutOrderEntryDate());
	    	ib.debugOut("<<< Output Order Carrier ID:  " + output.getOutOrderCarrierId());
	    	ib.debugOut("<<< Output Orderline Count: " + output.getOutOrderLineCount());
	    	ib.debugOut("<<< Output stcNum: " + output.getStcnum());
	
	        if (output.getOutOrderLines() != null) {
	        	ib.debugOut("<<< outputOrderLines is not null");
	            for (int i = 0; i < length; i++) {
	            	ib.debugOut("<<< After for loop to print the outputOrderLine info");
	
	            	ib.debugOut("<<< _____________________________");
	            	ib.debugOut("<<< Output Supply Warehouse ID: " + output.getOutOrderLines()[i].getOutOlSupplyWarehouseId());
	            	ib.debugOut("<<< Output Item ID: " + output.getOutOrderLines()[i].getOutOlItemId());
	            	ib.debugOut("<<< Output Item Quantity: " + output.getOutOrderLines()[i].getOutOlQuantity());
	            	ib.debugOut("<<< Output Item Amount: " + output.getOutOrderLines()[i].getOutOlAmount());
	            	ib.debugOut("<<< Output Item Delivery Date: " + output.getOutOrderLines()[i].getOutOlDeliveryDate());
	            	ib.debugOut("<<< _____________________________");
	            }
	        }
	    } catch (Exception e) {
	        System.out.println("<<< printOrderStatusOutput() failed - Exception: " + e);
	        e.printStackTrace();
	        throw new ServletException("<<< printOrderStatusOutput() failed - Exception: " + e);
	    }
	}

	public void dispatch(HttpServletRequest request, HttpServletResponse response, String nextPage)
		throws ServletException, IOException {
		
		RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
		dispatch.forward(request, response);
	}
}
