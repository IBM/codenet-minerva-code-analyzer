package batch.artifacts;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import java.sql.Connection;
import javax.sql.DataSource;

import java.sql.PreparedStatement;
import java.sql.Timestamp;

import java.util.Date;
import java.util.Random;

import jakarta.batch.api.BatchProperty;
import jakarta.batch.api.Batchlet;
import jakarta.inject.Inject;
import javax.naming.InitialContext;

public class LoadOrderAndOrderlineTableBatchlet implements Batchlet {

	private InitialContext initCtx = null;

	private DataSource ds = null;
	
	// prepared statements used by this instance (partition) of the batchlet.
	private PreparedStatement orderLinePreparedStatement = null;
	private PreparedStatement orderPreparedStatement = null;

	/**
	 * The number of warehouses to create
	 */
	@Inject
	@BatchProperty(name = "numberWarehouses")
	private String numWarehousesStr;
	
	/**
	 * The customer warehouse to load
	 */
	@Inject
	@BatchProperty(name = "currentWarehouse")
	private String currentWarehouseStr;
	
	/**
	 * The customer district to load
	 */
	@Inject
	@BatchProperty(name = "currentDistrict")
	private String currentDistrictStr;

	/**
	 * Default constructor. 
	 */
	public LoadOrderAndOrderlineTableBatchlet() {
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

		int numWarehouses = Integer.parseInt(numWarehousesStr);
		
		try {
			getDataSource();
		} catch (Exception ex) {
			System.out.println("Problem gettting DataSource, exception is: " + ex);
		}
		
		try {
			loadOrdersTable(numWarehouses);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected void loadOrdersTable (int numberOfWarehouses) throws Exception {

      	int numWarehouses = Integer.parseInt(numWarehousesStr);
		int currentWarehouse = Integer.parseInt(currentWarehouseStr);
		int currentDistrict = Integer.parseInt(currentDistrictStr);
    	int maxWarehouseCnt = 0;
    	int maxDistrictCnt = 0;
    	
    	// If numWarehouses = 1, then we will have 10 partitions that will each load one
    	// District worth of orders and orderlines.  Otherwise we will have "n" partitions,
    	// that will each load one warehouse worth of orders and orderlines (ie. 10 districts
    	// worth for that warehouse).
    	if (numWarehouses == 1) {
    		maxWarehouseCnt = 1;
    		maxDistrictCnt = currentDistrict;
    	} else {
    		maxWarehouseCnt = currentWarehouse;
    		maxDistrictCnt = 10;
    	}

		// DB connection
		Connection con = null;

		// Outer try / finally block for resource closing
		try {

			try {	
				con = getConnection();

			} catch (Exception ex) {
				System.out.println("Problem getting connection: " + ex);
			}

			String insertOrdersSQL = "INSERT INTO ORDERS" +
					"(O_ID, O_D_ID, O_W_ID," + 
					"O_C_ID, O_ENTRY_D, O_CARRIER_ID," +
					"O_OL_CNT, O_ALL_LOCAL, O_TRACKING_NUMBER," +
					"O_TRACKING_HOPS_COUNT) VALUES" +
					"(?,?,?,?,?,?,?,?,?,?)";

			// Tracking number is always initialized to null string
			String initTrackingNumber = "";

			// Number of tracking hops count is always initialized to 0
			int initTrackingHopsCount = 0;

			//Create random number generator for use in many places below
			Random myGenerator = new Random();

			// For 1 or more warehouses, depending on input parameter
			for (int warehouseId = currentWarehouse; warehouseId <= maxWarehouseCnt; warehouseId++){
			
	 			System.out.println("LoadOrderAndOrderlineTableBatchlet starting work for warehouseId = " + warehouseId);
	 			long warehouseStartTime = System.currentTimeMillis();

				// For either 1 or 10 districts
				for (int districtId = currentDistrict; districtId <=maxDistrictCnt; districtId ++) {
					
					debugOut("Insert 3000 Orders for, warehouse Id: " + warehouseId + ", district Id: " + districtId);
					long districtStartTime = System.currentTimeMillis();

					// For 3000 orders in each warehouse / district combination
					for (int orderId = 1; orderId <= 3000; orderId ++) {

						try {						
							
    						// Generate Custonmer ID 1 - 3000 by counting backwards from 3000 (ie. subract order id
    						// which is counting upwards.  We may need to make this more random in the future.
    						int customerId = 3001 - orderId;

    						// Generate order entry date as today.
    						Date today = new Date();
    						Timestamp orderEntryDate = new Timestamp(today.getTime());

    						// Generate carrier ID
    						// Normally carrier ID is random in the range of 1 - 10.  However,
    						// orders with ID > 2100 are new orders.  So last 900 of the 3000
    						// orders are new orders.  New orders have no carrier ID yet so it
    						// is set to 0 for all of them.
    						int carrierId = 0; // assume a new order to start with (ie. no carrier Id)
    						if (orderId <= 2100) {
    							// its not a new order so set carrir Id to random number (1-10)
    							carrierId = myGenerator.nextInt(10) + 1;
    						}

    						// Generate orderline count as random number from 5-15.
    						// However, we need it stuffed into a 2 place decimal too, so convert it
    						int orderLineCountInt = myGenerator.nextInt(11) + 5;
    						BigDecimal orderLineCount = new BigDecimal(orderLineCountInt);
    						orderLineCount = orderLineCount.setScale(0);


    						// Note:  we cannot set allLocal on the Order, until we first generate
    						//        all the orderlines for this Order.  If any orderline is generated 
    						//        where the supplying warehouse is remote, then allLocal must be
    						//        set to false (ie. 0) for the entire order.


    						// Now that we know almost everything about the order, go insert the 
    						// orderlines for this order into the ORDERLINE table.   Track if any
    						// orderline in the set had a remote supplying warehouse.
    						
    						// debugOut("...Insert " + orderLineCountInt + " orderlines");
    						boolean remoteSupplyWarehouseFound = false;
    						boolean executeIt = false;  // don't execute the batch until the last orderline 
    						for (int orderLineNumber = 1; orderLineNumber <= orderLineCountInt; orderLineNumber++)	{
    							if (orderLineNumber == orderLineCountInt){
    								executeIt = true;
    							}
    							boolean resultAllLocal = loadOrderLineRow(con, orderId, districtId, warehouseId, orderLineNumber, orderEntryDate, numberOfWarehouses, executeIt);
    							if (!resultAllLocal) {
    								remoteSupplyWarehouseFound = true;
    							}
    						}

    						// Finally, set allLocal which says whether all order lines come from the current warehouse or
    						// at least one orderline is from a remote warehouse.  In the special case of only 1 warehouse
    						// allLocal is always true (ie. 1) since there can be no remote warehouse.  In all other cases
    						// allLocal will be true in about 10% orders since the average order has 10 orderlines, and
    						// orderlines are generated so that about 1% of them have a remote warehouse as their supplying
    						// warehouse.
    						int allLocal = 0;  // assume remote supply warehouse
    						if (numberOfWarehouses == 1){
    							allLocal = 1;
    						} else {
    							if (remoteSupplyWarehouseFound) {
    								allLocal = 0;					
    							} else {
    								allLocal = 1;
    							}
    						}

    						// Now load up all 11 columns of the order row to insert into the ORDERS table
    						if(orderId == 1) {
    							if (orderPreparedStatement != null) { 
    							   orderPreparedStatement.close();
    							   orderPreparedStatement=null;
    							}   							
    							orderPreparedStatement = con.prepareStatement(insertOrdersSQL);
    						}
    						orderPreparedStatement.setInt(1, orderId);
    						orderPreparedStatement.setInt(2, districtId);
    						orderPreparedStatement.setInt(3, warehouseId);
    						orderPreparedStatement.setInt(4, customerId);
    						orderPreparedStatement.setTimestamp(5, orderEntryDate);
    						orderPreparedStatement.setInt(6, carrierId);
    						orderPreparedStatement.setBigDecimal(7, orderLineCount);
    						orderPreparedStatement.setInt(8, allLocal);
    						orderPreparedStatement.setString(9, initTrackingNumber);
    						orderPreparedStatement.setInt(10, initTrackingHopsCount);
    						// VERSION is an integer column that JDBC uses as needed, we don't load it.

    						// batch up the new row
    						orderPreparedStatement.addBatch();
    											
    					} catch (Exception ex) {
    						System.out.println("Problem inserting Order into DB, exception is: " + ex);
    					}

    				} // End for orderId = 1-3000
    				
    			// For all orders in the each district commit connection
    			orderPreparedStatement.executeBatch();	
    			con.commit();	
    				
			    long elapsedTime = (System.currentTimeMillis() - districtStartTime) / 1000;
				debugOut("Finished inserting 3000 Orders for, warehouse Id: " + warehouseId + ", district Id: " + districtId + ", elapsed time (secs) = " + elapsedTime);
    				
    			} // End for districtId = 1-max
				
			    long elapsedTime = (System.currentTimeMillis() - warehouseStartTime) / 1000;
	 			System.out.println("LoadOrderAndOrderlineTableBatchlet finished work for warehouseId = " + warehouseId + ", elapsed time (secs) = " + elapsedTime);			
														
			} // End for warehouseId = 1 to max

		} finally {
			if (con != null){
				con.close();
			}
		}

	} // End loadOrdersTable	


	protected boolean loadOrderLineRow(Connection con, int orderId, int districtId, int warehouseId, int orderLineNumber, Timestamp orderEntryDate, int numberOfWarehouses, boolean executeIt) throws Exception {
		
		// This method returns a boolean that indicates whether the orderline loaded has a supplying warehouse
		// that is the current warehouse (ie local) or a remote warehouse (ie. not local).  We start out here
		// assuming the warehouse is remote so allLocalFlag is false.
		boolean resultAllLocalFlag = false;  
				
		try {

			//Create random number generator for use in many places below
			Random myGenerator = new Random();

			String insertOrderLineSQL = "INSERT INTO ORDERLINE" +
					"(OL_O_ID, OL_D_ID, OL_W_ID, OL_NUMBER, OL_I_ID, OL_SUPPLY_W_ID," +
					"OL_DELIVERY_D, OL_QUANTITY, OL_AMOUNT, OL_DIST_INFO) VALUES" +
					"(?,?,?,?,?,?,?,?,?,?)";

			// Generate itemId by randomly picking one of 100,000 items available.
			int itemId = myGenerator.nextInt(100000) + 1;

			// Generate the supplying warehouse Id for this item.  In the special case of only
			// one warehouse all orderlines are supplied from the home warehouse (ie. allLocal = 1).
			// Otherwise, make 99% of the remaining orderlines be supplied from the local warehouse and %1
			// of the remaining orderlines be supplied from a randomly picked remote warehouse.		
			int supplyWarehouseId = 0;

			if (numberOfWarehouses == 1) {
				supplyWarehouseId = warehouseId;
				resultAllLocalFlag = true;
			} else {
				// 99% local and 1% remote
				int random = myGenerator.nextInt(100) +1;
				if (random < 100) {
					// the 99% case
					supplyWarehouseId = warehouseId;
					resultAllLocalFlag = true;
				} else {
					// the 1% case
					boolean remoteWarehouseFound = false;
					while(remoteWarehouseFound == false) {
						int tempWarehouseId = myGenerator.nextInt(25) + 1;

						// Make sure random warehouse picked is not our current (local) warehouse.
						// Otherwise, try again.
						if (tempWarehouseId != warehouseId) {
							remoteWarehouseFound = true;
							supplyWarehouseId = tempWarehouseId;
						}				
					} // end while
					resultAllLocalFlag = false;			
				}  // end 1% case

			} // end if 

			// Generate orderline delivery date.  If this order has already been delivered (ie. its
			// not a new order), then set the orderline's delivery date the same as the order's entry
			// date.  Otherwise, for new orders the orderline's date is not set at all (ie. until it is 
			// delivered later).
			Timestamp orderLineDeliveryDate = null;
			// Each warehouse / district combination has 3000 orders.  Of those 3000 orders, the last
			// 900 start out as new orders.
			if (orderId <= 2100) {
				// It's an old order (ie. already delivered)
				orderLineDeliveryDate = orderEntryDate;
			}

			// Generate the quantity of the item that customer wanted in this orderline (eg, 1 shovel, or 5 shovels?).
			// For 99% of the orderlines just hardcode the quantity to 5, but for the remaining 1% pick a random quantity
			// between 1 and 10.   Make sure the quantity fits in 2 decimal places.
			BigDecimal itemQuantity = null;
			int random = myGenerator.nextInt(100) + 1;
			if (random < 100) {
				// the 99% case
				itemQuantity = new BigDecimal(5);
			} else {
				// the 1% case
				itemQuantity = new BigDecimal(myGenerator.nextInt(10) + 1);
			}
			itemQuantity = itemQuantity.setScale(0);

			// Generate a price from $.01 to $9,999.99 for this orderline.
			// Old Orders (ie already delivered) are paid for an have an amount of 0.
			// New Orders have a randomly generated amount.   Make sure price fits in
			// 6 decimal places.
			BigDecimal orderLinePrice = null;
			if (orderId <= 2100) {
				// It's an old (ie. delivered order)
				orderLinePrice = new BigDecimal(0);
			} else {
				orderLinePrice = new BigDecimal((myGenerator.nextDouble() * 9999.99) + .01);
			}			
			orderLinePrice = orderLinePrice.round(new MathContext(6)); 
			orderLinePrice = orderLinePrice.setScale(2, RoundingMode.HALF_UP);        

			// Generate orderline district info for this item.  This comes from the stock table
			// column for the district, but we will generate it here so we do not have to query
			// the stock table.   The general format is "item x for district y", where x may be 
			// in the range 1-100,000 and district may be in the range 1-10)
			String orderLineDistInfo = "item " + itemId + " for dist " + districtId;

			// Now load up all 12 columns of the orderline row to insert into the ORDERLINE table	
			if (orderLineNumber == 1) {
				if (orderLinePreparedStatement != null) {
				   orderLinePreparedStatement.close();
				   orderLinePreparedStatement = null;
				}
			    orderLinePreparedStatement = con.prepareStatement(insertOrderLineSQL);
			}
			orderLinePreparedStatement.setInt(1, orderId);
			orderLinePreparedStatement.setInt(2, districtId);
			orderLinePreparedStatement.setInt(3, warehouseId);
			orderLinePreparedStatement.setInt(4, orderLineNumber);
			orderLinePreparedStatement.setInt(5, itemId);
			orderLinePreparedStatement.setInt(6, supplyWarehouseId);
			orderLinePreparedStatement.setTimestamp(7, orderLineDeliveryDate);   
			orderLinePreparedStatement.setBigDecimal(8, itemQuantity);
			orderLinePreparedStatement.setBigDecimal(9, orderLinePrice);
			orderLinePreparedStatement.setString(10, orderLineDistInfo);
			// I_ORDERLINE_INFO is an XML column that we currently are not loading for Liberty
			// VERSION is an integer column that JDBC uses as needed, we don't load it.

			orderLinePreparedStatement.addBatch();
			if (executeIt) {
				orderLinePreparedStatement.executeBatch();
			}
			
		} catch (Exception ex) {System.out
			.println("Problem inserting orderline into DB, exception is: " + ex);
		}
		return(resultAllLocalFlag);
	}
	


	

	/**
	 * dropDatasource method comment
	 * 
	 * @exception java.rmi.RemoteException
	 *                The exception description.
	 */
	public void dropDataSource() throws java.lang.Exception {

		try {
			initCtx.close();
			initCtx = null;
			ds = null;
		} catch (Exception ex) {
			System.out.println("ERROR: dropDatasource() failed with Exception: " + ex);
			ex.printStackTrace();
			throw ex;
		}
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

	private void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null)
			if (System.getProperty("DEBUG_OUT").equals("ON"))
				System.out.println(message);
	}

}
