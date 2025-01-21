package orderstatus.lite.ws;

import irwwbase.ERWWDataNotFoundException;
import irwwbase.IRWWBase;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.lang.String;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.annotation.Resource;

import java.sql.SQLException;

@WebService
public class OrderStatusImpl implements OrderStatus {

	@Resource(name="jdbc/ERWWDataSourceWithPwd")
	private javax.sql.DataSource ERWWDataSourceWithPwd;
	
	private java.lang.String className = this.getClass().toString();

	@WebMethod
	@Override
	public OrderStatusOutput getOrderStatus(OrderStatusInput input)
		throws IOException, SQLException, Exception,ERWWDataNotFoundException {
		
		IRWWBase irwwbase = new IRWWBase();
		
		irwwbase.debugOut("*** Entering getOrderStatus method of OrderStatusImpl class ***");
		
		irwwbase.startOfTran("WebOrderStatusLite_WS");
		//Comment out unused variable
		//short ol_number = 0; // counter for"current"item
	    String stcNum = "none";
	    //Comment out unused variable
	    //String inputType = "AutoGen";
	    short warehouseId = 0;
	    short districtId = 0;
	    short customerId = 0;
	    String customerLastName = null;
	    boolean byLastName = false;
	    //Comment out unused variable
	    //byte db = irwwbase.dbSize();
		
		String dataSourceJNDIName = "java:comp/env/jdbc/ERWWDataSourceWithPwd";
		Connection con = null; 
		
		OrderStatusOutput output = new OrderStatusOutput();
	
		// Set times
		/*Comment out unused variables
		java.sql.Timestamp timestamp = null; 
		java.util.Date date = java.util.Calendar.getInstance().getTime();
		timestamp = new java.sql.Timestamp(date.getTime());
		*/		
		//Assume failure until the web app succeeds
		output.setRetc("Failed");

		warehouseId = input.getInCustomerWarehouseId();
		districtId = input.getInCustomerDistrictId();
		customerId = input.getInCustomerId();
		customerLastName =input.getInCustomerLastName();
		if (customerId <= 0){
			byLastName = true;
		}
	
		try{
			con = irwwbase.getConnection(irwwbase.getDatasource(dataSourceJNDIName));
		} catch(java.sql.SQLException sqle){
			System.out.println("<<< " + className + " - SQLException: " + sqle);
			sqle.printStackTrace();	
			throw new java.sql.SQLException("<<< " + className + " - SQLException: " + sqle);
		} catch(Exception e){
			System.out.println("<<< " + className + " - Exception: " + e);
			e.printStackTrace();
			throw new RuntimeException("<<< " + className + " - Exception: " + e);
		} 
	
		// Set warehouseId and districtId in output
		output.setOutCustomerWarehouseId(warehouseId);
		output.setOutCustomerDistrictId(districtId);
		output.setOutCustomerId(customerId);
		
		irwwbase.debugOut("<<< " + className + " customerWarehouseId = " + warehouseId);
		irwwbase.debugOut("<<< " + className + " customerDistrictId = " + districtId);
		irwwbase.debugOut("<<< " + className + " customerId = " + customerId);
		irwwbase.debugOut("<<< " + className + " customerLastName = " + customerLastName);
		irwwbase.debugOut("<<< " + className + " byLastName = " + byLastName);
	
		// **********************************************************************
		// 2.6.2.2 bullet 3
		// **********************************************************************
	
		// Search for customer (either by ID or by last name)
		if (byLastName){
			//2.6.2.2 bullet 3 - Case 1
			irwwbase.debugOut("<<<in by Customer Last Name");
			customerId = this.getCustomerByLastName(districtId, warehouseId, customerLastName, con, output);
		} else {
			//	2.6.2.2 bullet 3 - Case 2
			irwwbase.debugOut("<<<in by Customer Id");
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
		
		irwwbase.endOfTran("WebOrdereStatusLite_WS");
	
		try {				
			irwwbase.closeConnection(con);
		} catch(java.sql.SQLException sqle){
			System.out.println("<<< " + className + " - Cleanup - SQLException: " + sqle);
			sqle.printStackTrace();	
			throw new java.sql.SQLException("<<< " + className + " - Cleanup - SQLException: " + sqle);
		} catch(java.io.IOException ioe){
			System.out.println("<<< " + className + " - Cleanup - IOException: " + ioe);
			ioe.printStackTrace();	
			throw new RuntimeException("<<< " + className + " - Cleanup - IOException: " + ioe);
		} catch (Exception ex) {
			System.out.println("<<< ERROR: " + className + " - Cleanup - Exception: " + ex);
			ex.printStackTrace();
			throw new RuntimeException(ex);	
		} finally {
			try {
				if (output != null){
					this.printOrderStatusOutput(output);
				}	
				if (irwwbase.getSTC() != null){
					stcNum = irwwbase.getSTC();
					irwwbase.debugOut("<<< " + className + " stcNum = " + stcNum);
				}
				output.setStcnum(stcNum);
				irwwbase.debugOut("<<< Exiting: " + className + irwwbase.getCurrentTime());
			} catch(Throwable e){
				System.out.println("<<< " + className + " - Exception: " + e);
				e.printStackTrace();	
				throw new RuntimeException("<<< " + className + " - Exception: " + e);
			}
		}
		return output;
	}
	
	private short getCustomerByLastName(short districtId, short warehouseId, String customerLastName, Connection con, OrderStatusOutput output)throws Exception, IOException {
		
		IRWWBase irwwbase = new IRWWBase();
		
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
	
		try {
			irwwbase.debugOut("<<< " + className + " - getting Customer by 'Last Name'");		
															
			aSqlString = "SELECT C_ID FROM CUSTOMER WHERE C_D_ID = ? and C_W_ID = ? and C_LAST = ? WITH CS";	
			aPS = con.prepareStatement(aSqlString);
			aPS.setShort(1, districtId);
			aPS.setShort(2, warehouseId);
			aPS.setString(3, customerLastName);
			rs = aPS.executeQuery();
			
			while (moreRecords = rs.next()) {
				v.addElement(rs.getShort(1));
				noCustomers++;
				irwwbase.debugOut("<<< " + className + " after v.addElement(rs.getShort(1)) where Customer ID = " + rs.getShort(1));
			}
			
			if ((moreRecords == false) && (noCustomers == 0)) {
				irwwbase.debugOut("<<< " + className + " There are no more records and number of customers equal 0");
				throw new ERWWDataNotFoundException(" Number of Customers =  "+noCustomers, " Number of Customers =  "+noCustomers, new Exception(" not found"));
			}
		} catch(java.sql.SQLException sqle){
			System.out.println("<<< " + className + " - getCustomerByLastName - SQLException: " + sqle);
			sqle.printStackTrace();	
			throw new java.sql.SQLException("<<< " + className + " - getCustomerByLastName - SQLException: " + sqle);
		} catch (Exception ex) {
			System.out.println("<<< ERROR: " + className + " - getCustomerByLastName - Exception: " + ex);
			ex.printStackTrace();
			throw new RuntimeException("<<< " + className + " - getCustomerByLastName - SQLException: " + ex);		
		} finally {
	        try {
	        	irwwbase.closeResultSet(rs);
	        	irwwbase.closePreparedStatement(aPS);
	        	irwwbase.debugOut("<<< Exiting: " + className + ".getCustomerByLastName: at " + irwwbase.getCurrentTime());						
			} catch(java.sql.SQLException sqle) {
				System.out.println("<<< " + className + " - getCustomerByLastName failed - SQLException: " + sqle);
				sqle.printStackTrace();	
				throw new SQLException("<<< " + className + " - getCustomerByLastName failed - SQLException: " + sqle);
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
			// Divide number of selected rows by 2, rounding up if you have a remainder.
			// (i.e., 5/3=1)
			noCustDiv2 = noCustomers / 2;
			remainder = noCustomers % 2;
	
			if (remainder > 0) {
				noCustDiv2 = noCustDiv2 + 1;
			}
		}
		
		irwwbase.debugOut("<<< " + className + "Number of Customers = " + noCustomers);
		irwwbase.debugOut("<<< " + className + "Vector Elements = " + v.elements());
		
		try {
			customerId = (Short)v.elementAt(noCustDiv2 - 1);
			output.setOutCustomerId(customerId);
		} catch (Exception ex) {
			System.out.println("<<< ERROR: " + className + " - getCustomerByLastName - Exception: " + ex);
			ex.printStackTrace();
			throw new RuntimeException("<<< " + className + " - getCustomerByLastName - Exception: " + ex);			
		}
		
		irwwbase.debugOut("<<< " + className + "Customer ID = " + customerId);
		irwwbase.debugOut("<<< " + className + "District ID = " + districtId);
		irwwbase.debugOut("<<< " + className + "Warehouse ID = " + warehouseId);
		
		try {
			aSqlString = "SELECT C_FIRST, C_MIDDLE, C_LAST, C_BALANCE " +
						 " FROM CUSTOMER WHERE C_ID = ? and C_D_ID = ? and C_W_ID = ? WITH CS";	
			aPS = con.prepareStatement(aSqlString);
			aPS.setShort(1, customerId);
			aPS.setShort(2, districtId);
			aPS.setShort(3, warehouseId);
			rs = aPS.executeQuery();
			
			if (rs.next() == true) {
				irwwbase.debugOut("<<< " + className + " getCustomerByLastName - record is found where customer ID = " + customerId);
			} else {
				throw new ERWWDataNotFoundException("Customer ID "+customerId, new Exception(" not found"));
			}
		
			output.setOutCustomerFirstName(rs.getString(1) );
			output.setOutCustomerMiddleName(rs.getString(2) );
			output.setOutCustomerLastName(rs.getString(3) );
			output.setOutCustomerBalance(rs.getBigDecimal(4).doubleValue());
			
		} catch(java.sql.SQLException sqle){
			System.out.println("<<< " + className + " - getCustomerByLastName - SQLException: " + sqle);
			sqle.printStackTrace();	
			throw new SQLException("<<< " + className + " - getCustomerByLastName failed - SQLException: " + sqle);
		} catch(Exception e){
			System.out.println("<<< " + className + " - getCustomerByLastName failed - Exception: " + e);
			e.printStackTrace();
			throw new RuntimeException("<<< " + className + " - getCustomerByLastName failed - Exception: " + e);
		} finally {
			try {
				irwwbase.closeResultSet(rs);
				irwwbase.closePreparedStatement(aPS);				
				irwwbase.debugOut("<<< Exiting: " + className + ".getCustomerByLastName: at " + irwwbase.getCurrentTime());						
			} catch(java.sql.SQLException sqle){
				System.out.println("<<< " + className + " - getCustomerByLastName failed - SQLException: " + sqle);
				sqle.printStackTrace();	
				throw new SQLException("<<< " + className + " - getCustomerByLastName failed - SQLException: " + sqle);
			} catch(java.io.IOException ioe){
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
	
	private void getCustomerById(short customerId, short districtId, short warehouseId, Connection con, OrderStatusOutput output)throws SQLException, IOException {
	
		IRWWBase irwwbase = new IRWWBase();
		
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
			irwwbase.debugOut("<<< " + className + " - getting Customer by 'ID'");
			irwwbase.debugOut("<<< " + className + " getCustomerById - customerWarehouseId = " + warehouseId);
			irwwbase.debugOut("<<< " + className + " getCustomerById -  customerDistrictId = " + districtId);
			irwwbase.debugOut("<<< " + className + " getCustomerById -  customerId = " + customerId);
	
			aSqlString = "SELECT C_FIRST, C_MIDDLE, C_LAST, C_BALANCE " +
						 " FROM CUSTOMER WHERE C_ID = ? and C_D_ID = ? and C_W_ID = ?" +
						 " WITH CS";	
			aPS = con.prepareStatement(aSqlString);
			aPS.setShort(1, customerId);
			aPS.setShort(2, districtId);
			aPS.setShort(3, warehouseId);
			rs = aPS.executeQuery();
			
			if (rs.next() == true) {
				irwwbase.debugOut("<<< " + className + " getCustomerById - record is found where customer ID = " + customerId);
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
			throw new SQLException("<<< " + className + " - getCustomerById failed - SQLException: " + sqle);
		} catch(Exception e){
			System.out.println("<<< " + className + " - getCustomerById failed - Exception: " + e);
			e.printStackTrace();
			throw new RuntimeException("<<< " + className + " - getCustomerById failed - Exception: " + e);
		} finally {
			try {
				irwwbase.closeResultSet(rs);
				irwwbase.closePreparedStatement(aPS);				
				irwwbase.debugOut("<<< Exiting: " + className + ".getCustomerById: at " + irwwbase.getCurrentTime());						
			} catch(java.sql.SQLException sqle){
				System.out.println("<<< " + className + " - getCustomerById failed - SQLException: " + sqle);
				sqle.printStackTrace();	
				throw new SQLException("<<< " + className + " - getCustomerById failed - SQLException: " + sqle);
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
		
	private void getOrder(short customerId, short districtId, short warehouseId, Connection con, OrderStatusOutput output )throws SQLException, IOException {
		
		IRWWBase irwwbase = new IRWWBase();
		
		java.sql.PreparedStatement aPS = null;
		java.sql.ResultSet rs = null;
		String aSqlString = null;
		/*Comment out unused variables
		int orderId = 0;
		int currentOrderId = 0;
		*/
		int largestOrderId = 0;
		
		irwwbase.debugOut("<<< " + className + " getOrder - customerId = " + customerId);
		irwwbase.debugOut("<<< " + className + " getOrder - customerWarehouseId = " + warehouseId);
		irwwbase.debugOut("<<< " + className + " getOrder - customerDistrictId = " + districtId);
		 
		try {
			irwwbase.debugOut("<<< " + className + " - getting Order");				
	
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
			throw new SQLException("<<< " + className + " - getOrder - SQLException: " + sqle);
		} catch (Exception ex) {
			System.out.println("<<< ERROR: " + className + " - getOrder - Exception: " + ex);
			ex.printStackTrace();
			throw new RuntimeException("<<< " + className + " - getOrder failed - Exception: " + ex);				
		} finally {
	        try {
	        	irwwbase.closeResultSet(rs);
	        	irwwbase.closePreparedStatement(aPS);
	        	irwwbase.debugOut("<<< Exiting: " + className + ".getOrder: at " + irwwbase.getCurrentTime());						
			} catch(java.sql.SQLException sqle) {
				System.out.println("<<< " + className + " - getOrder failed - SQLException: " + sqle);
				sqle.printStackTrace();	
				throw new SQLException("<<< " + className + " - getOrder failed - SQLException: " + sqle);
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
		
		irwwbase.debugOut("<<< " + className + " - largestOrderId = " + largestOrderId);
		
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
			}else{
				System.out.println("<<< ERROR: " + className + " - getOrder failed since the Order is not found:  " +
						"Warehouse Id: " + warehouseId +  " District Id: " + districtId + " Order Id: " + largestOrderId);
				throw new RuntimeException("<<< " + className + " - getOrder failed since the Order is not found:  " +
						"Warehouse Id: " + warehouseId +  " District Id: " + districtId + " Order Id: " + largestOrderId);
			}
	
		} catch(java.sql.SQLException sqle){
			System.out.println("<<< " + className + " - getOrder - SQLException: " + sqle);
			sqle.printStackTrace();	
			throw new SQLException("<<< " + className + " - getOrder - SQLException: " + sqle);
		} catch (Exception ex) {
			System.out.println("<<< ERROR: " + className + " - getOrder - Exception: " + ex);
			ex.printStackTrace();
			throw new RuntimeException("<<< " + className + " - getOrder failed - Exception: " + ex);
		} finally {
	        try {
	        	irwwbase.closeResultSet(rs);
	        	irwwbase.closePreparedStatement(aPS);
	        	irwwbase.debugOut("<<< Exiting: " + className + ".getOrder: at " + irwwbase.getCurrentTime());						
			} catch(java.sql.SQLException sqle) {
				System.out.println("<<< " + className + " - getOrder failed - SQLException: " + sqle);
				sqle.printStackTrace();	
				throw new SQLException("<<< " + className + " - getOrder failed - SQLException: " + sqle);
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
	
	private void getOrderlines(short customerId, short districtId, short warehouseId, Connection con, OrderStatusOutput output)throws SQLException, IOException {
		
		IRWWBase irwwbase = new IRWWBase();
		
		java.sql.PreparedStatement aPS = null;
		java.sql.ResultSet rs = null;
		String aSqlString = null;
		String deliveryDate = null;
	    output.setOutOrderLines(new OrderLineSingleItem[output.getOutOrderLineCount()]);
		int ol_number = 0;
    	short olSupplyWarehouseId = 0;
    	int olItemId = 0;
    	BigDecimal olQuantity = new BigDecimal(0);
    	BigDecimal olAmount = new BigDecimal(0);
    	String olDeliveryDate = null;
    	OrderLineSingleItem orderLineSingleItem = null;
    	OrderLineSingleItem[] orderLineSingleItems = new OrderLineSingleItem[output.getOutOrderLineCount()];
    				
    	irwwbase.debugOut("<<< " + className + " - olOrderId = " + output.getOutOrderId());
    	irwwbase.debugOut("<<< " + className + " - olDistrictId = " + districtId);
    	irwwbase.debugOut("<<< " + className + " - olWarehouse = " + warehouseId);
		
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

				irwwbase.debugOut("<<< " + className + " - olSupplyWarehouseId = " + olSupplyWarehouseId);
				irwwbase.debugOut("<<< " + className + " - olItemId = " + olItemId);
				irwwbase.debugOut("<<< " + className + " - olQuantity = " + olQuantity);
				irwwbase.debugOut("<<< " + className + " - olAmount = " + olAmount);
				irwwbase.debugOut("<<< " + className + " - olDeliveryDate = " + olDeliveryDate);
					
				orderLineSingleItem = new OrderLineSingleItem();						
					
				orderLineSingleItem.setOutOlSupplyWarehouseId(olSupplyWarehouseId);
				orderLineSingleItem.setOutOlItemId(olItemId);
				orderLineSingleItem.setOutOlQuantity(olQuantity.doubleValue());	
				orderLineSingleItem.setOutOlAmount(olAmount.doubleValue());	

				if (olDeliveryDate == null){
					orderLineSingleItem.setOutOlDeliveryDate("99-99-9999");
				} else {
					deliveryDate = olDeliveryDate;
					orderLineSingleItem.setOutOlDeliveryDate(deliveryDate);
				}
				
				irwwbase.debugOut("<<< " + className + " - ol_number = " + ol_number);
				orderLineSingleItems[(ol_number)]=orderLineSingleItem;	
				ol_number++;								
			 }
			
		} catch(java.sql.SQLException sqle) {
			System.out.println("<<< " + className + " - OrderLine Object - SQLException: " + sqle);
			sqle.printStackTrace();	
			throw new SQLException("<<< " + className + " - OrderLine Object - SQLException: " + sqle);
		} catch(Exception e) {
			System.out.println("<<< " + className + " - OrderLine Object - Exception: " + e);
			e.printStackTrace();
			throw new RuntimeException("<<< " + className + " - OrderLine Object - Exception: " + e);
		} finally {
	        try {
	        	irwwbase.closeResultSet(rs);
	        	irwwbase.closePreparedStatement(aPS);
	        	irwwbase.debugOut("<<< Exiting: " + className + ".getOrderline: at " + irwwbase.getCurrentTime());						
			} catch(java.sql.SQLException sqle) {
				System.out.println("<<< " + className + " - getOrderline failed - SQLException: " + sqle);
				sqle.printStackTrace();	
				throw new SQLException("<<< " + className + " - getOrderline failed - SQLException: " + sqle);
			} catch(java.io.IOException ioe) {
				System.out.println("<<< " + className + " - getOrderline failed - IOException: " + ioe);
				ioe.printStackTrace();	
				throw new java.io.IOException("<<< " + className + " - getOrderline failed - IOException: " + ioe);
			} catch (Exception ex) {
				System.out.println("<<< ERROR: " + className + " - getOrderline failed with Exception: " + ex);
				ex.printStackTrace();
				throw new RuntimeException("<<< " + className + " - getOrderline failed - Exception: " + ex);		
			}
	    }
		
		try {				
			output.setOutOrderLines(orderLineSingleItems);
		} catch(Exception e){
			System.out.println("<<< " + className + " - OrderLine Object - Exception: " + e);
			e.printStackTrace();
			throw new RuntimeException("<<< " + className + " - OrderLine Object - Exception: " + e);
		}	
	}
	    		
	public void printOrderStatusOutput(OrderStatusOutput output) throws SQLException, IOException, Exception {
		IRWWBase irwwbase = new IRWWBase();
		
		irwwbase.debugOut("<< " + this.getClass().toString() + ".printOrderStatusOutput - Entered");
	    int length = 0;		        
	    length = output.getOutOrderLines().length;
	
	    try {
	    	irwwbase.debugOut("<<< Output Warehouse ID: " + output.getOutCustomerWarehouseId());
	    	irwwbase.debugOut("<<< Output District ID:  " + output.getOutCustomerDistrictId());
	    	irwwbase.debugOut("<<< Output Customer ID: " + output.getOutCustomerId());
	    	irwwbase.debugOut("<<< Output Customer First Name:  " + output.getOutCustomerFirstName());
	    	irwwbase.debugOut("<<< Output Customer Middle Name: " + output.getOutCustomerMiddleName());
	    	irwwbase.debugOut("<<< Output Customer Last Name:  " + output.getOutCustomerLastName());
	    	irwwbase.debugOut("<<< Output Customer Customer Balance : " + output.getOutCustomerBalance());
	    	irwwbase.debugOut("<<< Output Order ID:  " + output.getOutOrderId());
	    	irwwbase.debugOut("<<< Output Order Entry Date: " + output.getOutOrderEntryDate());
	    	irwwbase.debugOut("<<< Output Order Carrier ID:  " + output.getOutOrderCarrierId());
	    	irwwbase.debugOut("<<< Output Orderline Count: " + output.getOutOrderLineCount());
	    	irwwbase.debugOut("<<< Output stcNum: " + output.getStcnum());
	
	        if (output.getOutOrderLines() != null) {
	        	irwwbase.debugOut("<<< outputOrderLines is not null");
	            for (int i = 0; i < length; i++) {
	            	irwwbase.debugOut("<<< After for loop to print the outputOrderLine info");
	
	            	irwwbase.debugOut("<<< _____________________________");
	            	irwwbase.debugOut("<<< Output Supply Warehouse ID: " + output.getOutOrderLines()[i].getOutOlSupplyWarehouseId());
	            	irwwbase.debugOut("<<< Output Item ID: " + output.getOutOrderLines()[i].getOutOlItemId());
	            	irwwbase.debugOut("<<< Output Item Quantity: " + output.getOutOrderLines()[i].getOutOlQuantity());
	            	irwwbase.debugOut("<<< Output Item Amount: " + output.getOutOrderLines()[i].getOutOlAmount());
	            	irwwbase.debugOut("<<< Output Item Delivery Date: " + output.getOutOrderLines()[i].getOutOlDeliveryDate());
	            	irwwbase.debugOut("<<< _____________________________");
	            }
	        }
	    } catch (Exception e) {
	        System.out.println("<<< printOrderStatusOutput() failed - Exception: " + e);
	        e.printStackTrace();
	        throw new Exception("<<< printOrderStatusOutput() failed - Exception: " + e);
	    }
	}	  	
	  
}

