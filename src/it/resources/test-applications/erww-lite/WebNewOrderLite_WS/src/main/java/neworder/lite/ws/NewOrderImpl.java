package neworder.lite.ws;

import irwwbase.IRWWBase;
import itemjpa.ItemJPA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

import neworderjpa.NewOrderJPA;
import orderjpa.OrderJPA;
import orderjpa.OrderKey;
import orderlinejpa.OrderlineJPA;
import stockjpa.StockJPA;
import stockjpa.StockKey;
import warehousejpa.WarehouseJPA;
import customerjpa.CustomerJPA;
import customerjpa.CustomerKey;
import districtjpa.DistrictJPA;
import districtjpa.DistrictKey;

//@Resource(name = "ut", type = jakarta.transaction.UserTransaction.class)
public class NewOrderImpl implements NewOrder {
	
	IRWWBase ib = new IRWWBase();

	public NewOrderOutputInfo createNewOrder(NewOrderInputInfo input, NewOrderOutputInfo output, EntityManagerFactory emf) throws Exception,
			IllegalStateException, SecurityException, SystemException {
		
		ib.debugOut("<<< Entering NewOrderImpl.createNewOrder()");
		
		//EntityManagerFactory emf = jakarta.persistence.Persistence.createEntityManagerFactory("NewOrderLite_WS");
		
		java.math.BigDecimal customerYTDPayment = null;
		//Comment out unused variable
		//int size = 0;

		EntityManager em = null;

		em = emf.createEntityManager();

		int orderAllLocalFlag = 1;

		// Set times
		//Comment out unused variables
		//java.lang.String s = new String();
		//java.lang.String beforeDistrictTime = null;
		java.lang.String afterDistrictTime = null;
		java.lang.String beforeNewOrderTime = null;
		java.lang.String afterNewOrderTime = null;
		java.util.Calendar calendar = null;
		
		java.util.Date date = null;
		java.sql.Timestamp timestamp = null;
		ib.startOfTran("WebNewOrderLite_WS: NewOrderImpl");

		// openjpa pessimistic update locking
		// kem = OpenJPAPersistence.cast (em);

		calendar = input.getInOrderEntryDate();

		date = java.util.Calendar.getInstance().getTime();
		timestamp = new java.sql.Timestamp(date.getTime());

		output.setOutTotal(0);

		// ***********************************************************
		// Copy non-repeating fields from input structure to output.
		// ***********************************************************

		output.setOutWarehouseId(input.getInWareHouseId());
		output.setOutDistrictId(input.getInDistrictId());
		output.setOutCustomerId(input.getInCustomerId());

		//Comment out unused variables
		//int ORDER_ID_LEN = 8;
		//int nextOrderId;

		// ***********************************************************
		// Warehouse 2.4.2.2 bullet 3
		// - get WarehouseTax
		// ***********************************************************
		ib.debugOut("<<< NewOrderImpl - getting Warehouse Object " + ib.getCurrentTime());
		
		UserTransaction ut = null;
		
		try {
			ib.debugOut("<<< NewOrderImpl - Before getUserTrans()");
			ut = getUserTrans();
			//ut = em.getTransaction();
			ib.debugOut("<<< NewOrderImpl - After getUserTrans()");
		} catch (NamingException e) {
			e.printStackTrace();
			throw new Exception("Exception getting user transaction", e);
		} 

		try {
			ib.debugOut("<<< NewOrderImpl - Before ut.begin()");
			ut.begin();
			//em.getTransaction().begin();
			ib.debugOut("<<< NewOrderImpl - After ut.begin()");
	   // } catch (NotSupportedException | SystemException e) {
		} catch (Throwable e) {
			e.printStackTrace();
			throw new Exception("Exception ut.begin()", e);
		} 

		try {
			ib.debugOut("<<< NewOrderImpl - Before em.joinTransaction()");
		    em.joinTransaction();
			ib.debugOut("<<< NewOrderImpl - After em.joinTransaction()");
	    } catch (Throwable e) {
			e.printStackTrace();
			throw new Exception("Exception em.joinTransaction()", e);
		} 
		
		WarehouseJPA warehouse = null;

		try {
			warehouse = getWarehouseInstance(input.getInWareHouseId(), em);
		} catch (Exception e) {
			throw new Exception("Exception getting warhouseInstance", e);
		}

		output.setOutWarehouseTax(warehouse.getWarehouseTax().doubleValue());

		// ***********************************************************
		// District 2.4.2.2 bullet 4
		// - get district Tax
		// - get district next order id (use as this OrderId)
		// - increment district next order id
		// ( for future New Order Transactions)
		// ***********************************************************
		ib.debugOut("<<< NewOrderImpl - getting District Object..."
				+ ib.getCurrentTime());
		DistrictJPA district = null;
		try {
			district = getDistrictInstance(input.getInDistrictId(),
					input.getInWareHouseId(), em);
		} catch (Exception e) {
			throw new Exception("Exception getting districtInstance", e);
		}
		ib.debugOut("<<< after - getting District Object..."
				+ ib.getCurrentTime());

		ib.debugOut("<<< Before get the next orderId" + ib.getCurrentTime());
		int d_next_o_id_int = district.getDistrictNextOrderId();
		// get next OrderId
		ib.debugOut("<<< After getting the next orderId" + ib.getCurrentTime());

		output.setOutOrderId(d_next_o_id_int);
		ib.debugOut("<<<output.outOrderId: " + output.getOutOrderId() + " "
				+ ib.getCurrentTime());
		ib.debugOut("<<<* " + input.getInWareHouseId() + " "
				+ input.getInDistrictId() + " " + output.getOutOrderId() + " "
				+ ib.getCurrentTime());

		output.setOutDistrictTax(district.getDistrictTax().doubleValue());
		// increment the next order ID for the district for future
		// NewOrdersTrans
		// int nextOrderId = d_next_o_id_int+1);
		ib.debugOut("<<<nextOrderId being put back into district: "
				+ (d_next_o_id_int + 1) + " " + ib.getCurrentTime());
		district.setDistrictNextOrderId(d_next_o_id_int + 1);
		afterDistrictTime = new java.util.Date().toString() + " "
				+ String.valueOf(System.currentTimeMillis());

		// ***********************************************************
		// Customer 2.4.2.2 bullet 5
		// - get Customer Discount, Customer Last Name & Customer Credit
		// ***********************************************************

		ib.debugOut("<<< NewOrderImpl - getting Customer Object..."
				+ ib.getCurrentTime());

		CustomerJPA customer = null;
		try {
			customer = getCustomerInstance(input.getInCustomerId(),
					input.getInDistrictId(), input.getInWareHouseId(), em);
		} catch (Exception e) {
			throw new Exception("Exception getting customerInstance", e);
		}

		output.setOutCustomerDiscount(customer.getCustomerDiscount()
				.doubleValue() * 100);
		output.setOutCustomerLastName(customer.getCustomerLast());
		output.setOutCustomerCredit(customer.getCustomerCredit());
		customerYTDPayment = customer.getCustomerYtdPayment();

		// *********************************************************
		// 2.4.2.2 bullet 7 o_ol_cnt (items_on_order) is not entered
		// *********************************************************

		short o_ol_cnt = (short) input.getInItemList().length;
		output.setOutOrderItemCount(o_ol_cnt);
		ib.debugOut("<<< NewOrderImpl - Number of Items in the List = o_ol_cnt = "
				+ o_ol_cnt + " " + ib.getCurrentTime());

		ib.debugOut("<<< input.getNumberOfItems() = "
				+ input.getNumberOfItems());

		output.setOutOrderEntryDate(input.getInOrderEntryDate());

		try {
			createOrderInstance(output.getOutOrderId(),
					input.getInDistrictId(), input.getInWareHouseId(),
					input.getInCustomerId(), timestamp, (short) 0,
					new java.math.BigDecimal(input.getNumberOfItems()),
					(short) orderAllLocalFlag, em);
		} catch (Exception e) {
			throw new Exception("Exception creating orderInstance", e);
		}

		int ol_number;
		for (ol_number = 1; ol_number <= input.getNumberOfItems(); ol_number++) {

			ib.debugOut("<<< NewOrderImpl - ol_number = " + ol_number + " "
					+ ib.getCurrentTime());

			// ****************************************************
			// Copy repeating fields (for this instance) from input
			// structure to output.
			// ****************************************************
			ib.debugOut("<<< setOutItemSupplyWarehouseId ");
			ib.debugOut("<<< server - # objects in array - input.getInItemList().length = "
					+ input.getInItemList().length);
			ib.debugOut("<<< server - # objects in array - output.getOutItemList().length = "
					+ output.getOutItemList().length);
			ib.debugOut("<<< ol_number = " + ol_number);

			output.getOutItemList()[ol_number - 1]
					.setOutItemSupplyWarehouseId(input.getInItemList()[ol_number - 1]
							.getInItemSupplyWarehouseId());

			ib.debugOut("<<< setOutItemId ");
			output.getOutItemList()[ol_number - 1].setOutItemId(input
					.getInItemList()[ol_number - 1].getInItemId());


			ib.debugOut("<<< setOutItemQuantity");
			output.getOutItemList()[ol_number - 1]
					.setOutItemQuantity((int) input.getInItemList()[ol_number - 1]
							.getInItemQuantity());

			// ***********************************************************
			// Item 2.4.2.2 bullet 8 part 1
			// - get item name, item Price and
			// item Data (this is used for Brand/generic test)
			// ***********************************************************

			ib.debugOut("<<< NewOrderImpl - getting Item Object..."
					+ ib.getCurrentTime());

			// Uncomment this and comment out next line for HP
			// ItemJPA item = getItemInstance(output.getOutItemList()[ol_number
			// - 1]
			// .getOutItemId());
			ItemJPA item = null;
			try {
				item = getItemInstance(
						output.getOutItemList()[ol_number - 1].getOutItemId(),
						em);
			} catch (Exception e) {
				throw new Exception("Exception getting itemInstance", e);
			}

			output.getOutItemList()[ol_number - 1].setOutItemPrice(item
					.getItemPrice().doubleValue());
			output.getOutItemList()[ol_number - 1].setOutItemName(new String(
					item.getItemName()));
			String i_data = new String(item.getItemData());

			ib.debugOut("<<< NewOrderImpl - ItemId = "
					+ output.getOutItemList()[ol_number - 1].getOutItemId()
					+ " " + ib.getCurrentTime());

			// **************************************
			// Stock 2.4.2.2. bullet 8.2
			// **************************************

			ib.debugOut("<<< NewOrderImpl - getting Stock Object..."
					+ ib.getCurrentTime());
			StockJPA stock = null;
			try {
				stock = getStockInstance(
						output.getOutItemList()[ol_number - 1].getOutItemId(),
						output.getOutItemList()[ol_number - 1]
								.getOutItemSupplyWarehouseId(), em);
			} catch (Exception e) {
				throw new Exception("Exception getting stock Instance",
						e);
			}

			int stockQuantity = stock.getS_QUANTITY().intValue();

			if (stockQuantity > (output.getOutItemList()[ol_number - 1]
					.getOutItemQuantity() + 10)) {
				stockQuantity -= output.getOutItemList()[ol_number - 1]
						.getOutItemQuantity();
				if (stockQuantity < 0)
					stockQuantity = 50;
			} else {
				stockQuantity = (stockQuantity - output.getOutItemList()[ol_number - 1]
						.getOutItemQuantity()) + 91;
				if (stockQuantity < 0)
					stockQuantity = 50;
			}

			output.getOutItemList()[ol_number - 1]
					.setOutStockQuantity(stockQuantity);
			stock.setS_QUANTITY(new BigDecimal(stockQuantity));
			// set based on calculations above

			// 2.4.2.2 bullet 8.2

			stock.setS_YTD(new BigDecimal(stock.getS_YTD().intValue()
					+ output.getOutItemList()[ol_number - 1]
							.getOutItemQuantity()));

			stock.setS_ORDER_CNT(new BigDecimal(checkForOverflow(stock
					.getS_ORDER_CNT().intValue()) + 1));
			
			String s_dist_info = getDistrictInfo(Short.valueOf(output.getOutDistrictId()).toString(), stock);
			String s_data = stock.getS_DATA();

			// ****************************************************
			// if there if there is a remote warehouse .
			// incrementRemoteCount
			// ****************************************************

			if (input.getInWareHouseId() != output.getOutItemList()[ol_number - 1]
					.getOutItemSupplyWarehouseId()) {

				stock.setS_REMOTE_CNT(new BigDecimal(checkForOverflow(stock
						.getS_REMOTE_CNT().intValue()) + 1));
				orderAllLocalFlag = 0;
			}

			// ****************************************
			// 2.4.2.2 bullet 8.3
			// - calculate Item "subtotal" and add to cumulative Total
			// ****************************************
			output.getOutItemList()[ol_number - 1].setOutItemTotal(output
					.getOutItemList()[ol_number - 1].getOutItemQuantity()
					* output.getOutItemList()[ol_number - 1].getOutItemPrice());

			output.setOutTotal(output.getOutTotal()
					+ output.getOutItemList()[ol_number - 1].getOutItemTotal());

			// ****************************************
			// Brand/Generic test 2.4.2.2 bullet 8.4
			// - if "ORIGINAL" is not in item data and stock data => generic
			// ****************************************

			// "ORIGINAL" in i_data or s_data?
			if ((i_data.indexOf("ORIGINAL") < 0)
					&& (s_data.indexOf("ORIGINAL") < 0))
				output.getOutItemList()[ol_number - 1].setOutBrandGeneric("G");
			else
				output.getOutItemList()[ol_number - 1].setOutBrandGeneric("B");

			// **************************************
			// createOrderLineInstanceFromCopy
			// **************************************

			ib.debugOut("<<< NewOrderImpl - creating OrderLine Object with the following Key attributes: "
					+ ib.getCurrentTime());

			ib.debugOut("<<< 	    	 - output.getOutOrderId() =  "
					+ output.getOutOrderId());
			ib.debugOut("<<<  		     - output.getOutDistrictId() = "
					+ output.getOutDistrictId());
			ib.debugOut("<<< 			 - output.getOutWarehouseId() =  "
					+ output.getOutWarehouseId());
			ib.debugOut("<<< 			 - olNumber =  " + ol_number);
			ib.debugOut("<<< 			 - output.getOutItemList()[ol_number - 1].getOutItemId() =  "
					+ output.getOutItemList()[ol_number - 1].getOutItemId());
			ib.debugOut("<<< 		     - output.getOutItemList()[ol_number - 1].getOutItemSupplyWarehouseId() =  "
					+ output.getOutItemList()[ol_number - 1]
							.getOutItemSupplyWarehouseId());
			ib.debugOut("<<< 			 - timestamp =  " + timestamp);
			ib.debugOut("<<< 			 - new java.math.BigDecimal(output.getOutItemList()[ol_number - 1].getOutItemQuantity())  "
					+ new java.math.BigDecimal(
							output.getOutItemList()[ol_number - 1]
									.getOutItemQuantity()));
			ib.debugOut("<<< 			 - output.getOutItemList()[ol_number - 1].getOutItemTotal()  "
					+ output.getOutItemList()[ol_number - 1].getOutItemTotal());
			ib.debugOut("<<< 		     - s_dist_info =  " + s_dist_info);

			try {
				createOrderLineInstance(
						output.getOutOrderId(),
						output.getOutDistrictId(),
						output.getOutWarehouseId(),
						ol_number,
						output.getOutItemList()[ol_number - 1].getOutItemId(),
						output.getOutItemList()[ol_number - 1]
								.getOutItemSupplyWarehouseId(),
						timestamp,
						new java.math.BigDecimal(
								output.getOutItemList()[ol_number - 1]
										.getOutItemQuantity()),
						output.getOutItemList()[ol_number - 1]
								.getOutItemTotal(), s_dist_info, em);
			} catch (Exception e) {
				throw new Exception(
						"Exception creating orderline Instance", e);
			}

		}

		// end loop

		// **********************************************************
		// 2.4.2.2 bullet 6 - CREATE NEWORDER object & SET values in it &
		// output.
		// ************************************************************

		ib.debugOut("<<< NewOrderImpl - creating NewOrder Object with the following Key attributes: "
				+ ib.getCurrentTime());
		ib.debugOut("<<< 				  - WarehouseId =  " + input.getInWareHouseId());
		ib.debugOut("<<<  			      - DistrictId = " + input.getInDistrictId());
		ib.debugOut("<<< 				  - OrderId =  " + output.getOutOrderId());
		// debugOut("<<<*NO " + input.inWareHouseId + " " + input.inDistrictId +
		// " "+output.outOrderId+" " +getCurrentTime() );

		beforeNewOrderTime = new java.util.Date().toString() + " "
				+ String.valueOf(System.currentTimeMillis());
		try {
			createNewOrderInstance(output.getOutOrderId(),
					input.getInDistrictId(), input.getInWareHouseId(), em);
		} catch (Exception e) {
			throw new Exception("Exception creating newOrder Instance",
					e);
		}

		afterNewOrderTime = new java.util.Date().toString() + " "
				+ String.valueOf(System.currentTimeMillis());

		// ************************************************************
		// 2.4.2.2 bullet 9 Calculate outTotal including taxes & discounts
		// **********************************************************

		/*
		 * if (this.businessRuleBeansEnabled) { //Reset customerDiscount
		 * (overriding the value from the DB2 tables) by invoking Business Rule
		 * Beans double customerDiscount = getCustomerStatusDiscount(
		 * output.getOutCustomerCredit(), customerYTDPayment);
		 * output.setOutCustomerDiscount(customerDiscount);
		 * 
		 * debugOut( " < < < Total Price before taxes & discount = " +
		 * output.getOutTotal()); debugOut(" < < < Warehouse Tax = " +
		 * output.getOutWarehouseTax()); debugOut(" < < < District Tax = " +
		 * output.getOutDistrictTax()); debugOut( " < < < Customer Discount = "
		 * + output.getOutCustomerDiscount()); }
		 */

		output.setOutTotal(output.getOutTotal()
				* (1 + output.getOutWarehouseTax() + output.getOutDistrictTax())
				* (1 - (output.getOutCustomerDiscount() / 100)));

		ib.endOfTran("WebNewOrderLite_WS: NewOrderImpl");

		output.setStcnum(irwwbase.Stcnum.getInstance().getStcnum());

		// If the request originator is a java client then the work area is not
		// going to be available
		// so just return and exit.
		// if (input.originatorIsJavaClient())
		// return output;

		// If WorkArea is Enabled Verify The Tax Rates using the WorkArea Before
		// Returning
		// if (checkEnableTaxWorkArea())
		// this.verifyTaxRateFromWorkArea(output);
		try {
			ut.commit();
			//em.getTransaction().commit();
		} catch (IllegalStateException | SecurityException e) {
			e.printStackTrace();
			em.getTransaction().rollback();
			throw new Exception("Exception committing user transaction",
					e);
		} 
		
		try {
			if (em!=null && em.isOpen()){
				em.close();
			}

		 }catch(Exception e){
				System.out.println("<<< NewOrderServlet - em close() - Exception: " + e);
				e.printStackTrace();				
		 }
		
		output.setOutMsg("Successful");
;
		return output;

	}

	protected WarehouseJPA getWarehouseInstance(short warehouseId,
			EntityManager em) throws Exception,
			IllegalStateException, SecurityException, SystemException {

		ib.debugOut("warehouseId in getWarehouseInstance " + warehouseId);
		WarehouseJPA warehouse = null;

		try {

			warehouse = em.find(WarehouseJPA.class, warehouseId,
					LockModeType.PESSIMISTIC_READ);
			if (warehouse == null)
				throw new Exception("warehouse not found, warehouse="
						+ warehouseId + " " + ib.getSTC());

		} catch (Exception t) {
			t.printStackTrace();
			//ut.rollback();
			em.getTransaction().rollback();
			if (ib.is913(t))
				throw new Exception("<&!@" + ib.checkFor913(t)
						+ ", finding Warehouse JPA, " + t.getCause()
						+ ib.getSTC() + ib.getStackTrace(t));
			throw new Exception("<&!@"
					+ "failure in getWarehouseInstance: "
					+ " exception message: " + t.getMessage()
					+ ", exception cause: " + t.getCause() + ib.getSTC()
					+ ib.getStackTrace(t));
		} // end try
		return warehouse;

	}

	protected DistrictJPA getDistrictInstance(short districtId,
			short warehouseId, EntityManager em)
			throws Exception, IllegalStateException, SecurityException,
			SystemException {

		DistrictJPA district = null;
		try {
			DistrictKey key = new DistrictKey();
			key.setDistrictId(districtId);
			key.setDistrictWareId(warehouseId);

			// kem.getFetchPlan().setReadLockMode(LockModeType.WRITE);
			district = em.find(DistrictJPA.class, key,
					LockModeType.PESSIMISTIC_WRITE);
			// kem.getFetchPlan().setReadLockMode(null);
			if (district == null)
				throw new Exception("district not found, districtId="
						+ districtId + " and warehouseId=" + warehouseId + " "
						+ ib.getSTC());

		} catch (Exception t) {
			t.printStackTrace();
			em.getTransaction().rollback();
			if (ib.is913(t))
				throw new Exception("<&!@" + ib.checkFor913(t)
						+ ", finding District JPA, " + t.getCause()
						+ ib.getSTC() + ib.getStackTrace(t));
			else
				throw new Exception("<&!@"
						+ "failure in getDistrictInstance: "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause() + ib.getSTC()
						+ ib.getStackTrace(t));
		} // end try
		return district;

	}

	protected ItemJPA getItemInstance(int itemNum, EntityManager em
			) throws Exception, IllegalStateException,
			SecurityException, SystemException {

		ItemJPA item = null;
		try {
			// Uncomment next two lines and remove em from the method arg list
			// for HP
			// EntityManager em = null;

			// em = emf2.createEntityManager();

			item = em.find(ItemJPA.class, itemNum);
			if (item == null)
				throw new Exception("item not found, item=" + item + " "
						+ ib.getSTC());
			ib.debugOut("inside getitemInstance" + item.getItemId()
					+ item.getItemPrice());

		} catch (Exception t) {
			t.printStackTrace();
			em.getTransaction().rollback();
			if (ib.is913(t))
				throw new Exception("<&!@" + ib.checkFor913(t)
						+ ", finding Item JPA, " + t.getCause() + ib.getSTC()
						+ ib.getStackTrace(t));
			throw new Exception("<&!@" + "failure in getItemInstance: "
					+ " exception message: " + t.getMessage()
					+ ", exception cause: " + t.getCause() + ib.getSTC()
					+ ib.getStackTrace(t));
		} // end try
		return item;

	}

	private CustomerJPA getCustomerInstance(short customerId, short districtId,
			short warehouseId, EntityManager em)
			throws Exception, IllegalStateException, SecurityException,
			SystemException {
		CustomerJPA customer = null;
		try {

			CustomerKey key = new CustomerKey();
			key.setCustomerDId(districtId);
			key.setCustomerId(customerId);
			key.setCustomerWId(warehouseId);

			customer = em.find(CustomerJPA.class, key,
					LockModeType.PESSIMISTIC_READ);
			if (customer == null)
				throw new Exception("customer not found, districtId="
						+ districtId + ", customerId=" + customerId
						+ " and warehouseId=" + warehouseId + " " + ib.getSTC());

		} catch (Exception t) {
			t.printStackTrace();
			em.getTransaction().rollback();
			if (ib.is913(t))
				throw new Exception("<&!@" + ib.checkFor913(t)
						+ ", finding Customer JPA, " + t.getCause()
						+ ib.getSTC() + ib.getStackTrace(t));
			throw new Exception("<&!@"
					+ "failure in getCustomerInstance: "
					+ " exception message: " + t.getMessage()
					+ ", exception cause: " + t.getCause() + ib.getSTC()
					+ ib.getStackTrace(t));
		} // end try
		return customer;

	}

	private void createOrderInstance(int outOrderId, short inDistrictId,
			short inWareHouseId, short inCustomerId, Timestamp orderEntryDat,
			short orderCarrierId, BigDecimal orderLineCount,
			short orderAllLocal, EntityManager em)
			throws Exception, IllegalStateException, SecurityException,
			SystemException {
		try {
			OrderJPA order = new OrderJPA();
			order.setCustomerId(inCustomerId);
			order.setDistrictId(inDistrictId);
			order.setOrderId(outOrderId);
			order.setWarehouseId(inWareHouseId);
			order.setEntryDate(orderEntryDat);
			order.setCarrierId(orderCarrierId);
			order.setOrderLineCount(orderLineCount);
			order.setAllLocal(orderAllLocal);

			em.persist(order);

		} catch (Exception ex) {
			System.out.println("Error creating order instance");
			ex.printStackTrace();
			em.getTransaction().rollback();
			if (ib.is913(ex))
				throw new Exception("<&!@" + ib.checkFor913(ex)
						+ ", creating Order JPA, " + ex.getCause()
						+ ib.getSTC() + ib.getStackTrace(ex));
			else
				throw new Exception("<&!@"
						+ "Error creating order instance"
						+ " exception message: " + ex.getMessage()
						+ ", exception cause: " + ex.getCause() + ib.getSTC()
						+ ib.getStackTrace(ex));

		} // end try

		ib.debugOut("<<< In NewOrderServlet, OrderJPA instance created where WarehouseId = "
				+ inWareHouseId
				+ " District Id = "
				+ inDistrictId
				+ " OrderId = " + outOrderId);

		return;

	}

	private java.math.BigDecimal convertToMoney(double doubleVal) {

		java.math.BigDecimal outMoney = new java.math.BigDecimal(doubleVal)
				.setScale(2, RoundingMode.HALF_EVEN);

		return outMoney;

	}

	private void createOrderLineInstance(int outOrderId, short outDistrictId,
			short outWarehouseId, int ol_number, int outItemId,
			short outItemSupplyWarehouseId, Timestamp orderDeliveryDate,
			BigDecimal quantity, double amount, String districtInfo,
			EntityManager em) throws Exception,
			IllegalStateException, SecurityException, SystemException {
		

		java.math.BigDecimal amt = this.convertToMoney(amount);

		ib.debugOut("oiginal value of amt is: " + amt);
		if (amt.doubleValue() > 9999.99)
			amt = new BigDecimal(9999.00);
		amt.setScale(2, RoundingMode.DOWN);
		ib.debugOut("amt=" + amt);

		ib.debugOut("oiginal value of quantity is: " + quantity);
		if (quantity.intValue() > 99)
			quantity = new BigDecimal(99.00);
		quantity.setScale(0, RoundingMode.DOWN);
		ib.debugOut("quantity=" + quantity);

		try {
			OrderKey orderKey = new OrderKey(outDistrictId, outOrderId, outWarehouseId);
			OrderJPA order = em.getReference(OrderJPA.class, orderKey);
			OrderlineJPA orderline = new OrderlineJPA();
			orderline.setOrder(order);
			orderline.setOlNumber((short) ol_number);
			orderline.setOlItemId(outItemId);
			orderline.setOlSupplyWarehouseId(outItemSupplyWarehouseId);
			orderline.setOlDeliveryDate(orderDeliveryDate);
			orderline.setOlQuantity(quantity);
			orderline.setOlAmount(amt);
			orderline.setOlDistrictInfo(districtInfo);

			em.persist(orderline);

		} catch (Exception ex) {
			System.out.println("Error creating orderline instance");
			ex.printStackTrace();
			em.getTransaction().rollback();
			if (ib.is913(ex))
				throw new Exception("<&!@" + ib.checkFor913(ex)
						+ ", creating Orderline JPA, " + ex.getCause()
						+ ib.getSTC() + ib.getStackTrace(ex));
			else
				throw new Exception("<&!@"
						+ "Error creating orderline instance"
						+ " exception message: " + ex.getMessage()
						+ ", exception cause: " + ex.getCause() + ib.getSTC()
						+ ib.getStackTrace(ex));
		}

		return;

	}

	private void createNewOrderInstance(int outOrderId, short inDistrictId,
			short inWareHouseId, EntityManager em)
			throws Exception, IllegalStateException, SecurityException,
			SystemException {
		try {
			NewOrderJPA neworder = new NewOrderJPA();
			neworder.setOrderId(outOrderId);
			neworder.setDistrictId(inDistrictId);
			neworder.setWarehouseId(inWareHouseId);

			em.persist(neworder);

		} catch (Exception ex) {
			System.out.println("Error creating neworder instance");
			ex.printStackTrace();
			em.getTransaction().rollback();
			if (ib.is913(ex))
				throw new Exception("<&!@" + ib.checkFor913(ex)
						+ ", creating NewOrder JPA, " + ex.getCause()
						+ ib.getSTC() + ib.getStackTrace(ex));
			throw new Exception("<&!@"
					+ "Error creating neworder instance" + ib.getSTC()
					+ ib.getStackTrace(ex));
		} // end try

		ib.debugOut("<<< In NewOrderServlet, NewOrderJPA instance created where WarehouseId = "
				+ inWareHouseId
				+ " District Id = "
				+ inDistrictId
				+ " OrderId = " + outOrderId);

		return;
	}

	private StockJPA getStockInstance(int outItemId,
			short outItemSupplyWarehouseId, EntityManager em)
			throws Exception {
		
		StockJPA stock = null;
		try {

			StockKey key = new StockKey();
			key.setS_I_ID(outItemId);
			key.setS_W_ID(outItemSupplyWarehouseId);

			// kem.getFetchPlan().setReadLockMode(LockModeType.WRITE);
			stock = em
					.find(StockJPA.class, key, LockModeType.PESSIMISTIC_WRITE);
			// kem.getFetchPlan().setReadLockMode(null);
			if (stock == null)
				throw new Exception("stock not found, item=" + outItemId
						+ " and warehouse=" + outItemSupplyWarehouseId + " "
						+ ib.getSTC());

		} catch (Exception t) {
			t.printStackTrace();
			if (ib.is913(t))
				throw new Exception("<&!@" + ib.checkFor913(t)
						+ ", finding Stock JPA, " + t.getCause() + ib.getSTC()
						+ ib.getStackTrace(t));
			else
				throw new Exception("<&!@"
						+ "failure in getStockInstance: "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause() + ib.getSTC()
						+ ib.getStackTrace(t));
		} // end try
		return stock;

	}

	private String getDistrictInfo(String districtId, StockJPA stock)
			throws Exception {

		try {
			switch (districtId) {
			case ("1"):
				return stock.getS_DIST_01();
			case ("2"):
				return stock.getS_DIST_02();
			case ("3"):
				return stock.getS_DIST_03();
			case ("4"):
				return stock.getS_DIST_04();
			case ("5"):
				return stock.getS_DIST_05();
			case ("6"):
				return stock.getS_DIST_06();
			case ("7"):
				return stock.getS_DIST_07();
			case ("8"):
				return stock.getS_DIST_08();
			case ("9"):
				return stock.getS_DIST_09();
			case ("10"):
				return stock.getS_DIST_10();

			default:
				System.out.println("Unknown district entered");
				return "";
			}

		} catch (Exception ex) {
			System.out
					.println("Error getting district info from district JPA entity ");
			ex.printStackTrace();
			throw new Exception(
					"Error getting district info from district JPA entity"
							+ " exception message: " + ex.getMessage()
							+ ", exception cause: " + ex.getCause()
							+ ib.getSTC() + ib.getStackTrace(ex));
		} // end try

	}

	private int checkForOverflow(int val) {
		if (val >= 9998)
			val = 0;
		return val;
	}

	private UserTransaction getUserTrans() throws NamingException {
		InitialContext ctx = new InitialContext();
		UserTransaction tx = (UserTransaction) ctx
				.lookup("java:comp/UserTransaction");
		return tx;
	}
	
}
