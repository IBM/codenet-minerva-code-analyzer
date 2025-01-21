package newordersession;

import irwwbase.IRWWBase;
import irwwbase.UserException;
import itemjpa.ItemJPA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

import neworderjpa.NewOrderJPA;
/* Comment out unused imports
import newordersession.NewOrderInputInfo;
import newordersession.NewOrderOutputInfo;
import newordersession.NewOrderSingleItemOutInfo;
*/
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


/**
 *  Each MDB instance in the server creates an instance of this session bean to do
 *  the JPA work for the New Order transaction.  Each instance is used by one MDB at
 *  a time so the code does not have to be thread-safe.
 *
 */

@Stateless
@LocalBean
public class NewOrderSessionBean {


	@PersistenceContext(unitName = "NewOrderSessionEJBLite")
	EntityManager em;

	/**
	 * Business logic for processing a New Order transaction using JPA.  We
	 * are running the business logic in a new transaction in case there are errors and
	 * JPA marks the transaction for rollback.  We need the original transaction in onMessage
	 * to not roll back so that messages are sent back to the client properly.
	 * 
	 * @param New Order input
	 * @return New Order output
	 * @throws UserException
	 * 
	 **/
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public NewOrderOutputInfo processNewOrder(NewOrderInputInfo input) throws UserException {

		// Notes about exception handling.  This method calls a number of other methods
		// that may throw UserException.  At the point that we have any UserException
		// thrown, the strategy is to terminate processing (ie. since the New Order has failed 
		// at that point).  Therefore, this method makes no attempts to catch user Exceptions throw
		// by any of the methods it calls.  It just lets them percolate back up to the NewOrderMDB
		// that called this method.

		try {
			debugOut("<< Enter NewOrderSessionBean.processNewOrder() >>");

			IRWWBase ib = new IRWWBase();
			int numberOfItems = input.getNumberOfItems();

			int orderAllLocalFlag = 1;	

			// Build New Order Output object to fill in and return later
			NewOrderOutputInfo output = generateNewOrderOutput(ib.dbSize(), numberOfItems);
			output.setOutTotal(0);

			Date date = Calendar.getInstance().getTime();
			Timestamp timestamp = new java.sql.Timestamp(date.getTime());		

			// ***********************************************************
			// Copy non-repeating fields from input structure to output.
			// ***********************************************************

			output.setOutWarehouseId(input.getInWareHouseId());
			output.setOutDistrictId(input.getInDistrictId());
			output.setOutCustomerId(input.getInCustomerId());

			// ***********************************************************
			// Warehouse 2.4.2.2 bullet 3
			// - get WarehouseTax
			// ***********************************************************

			WarehouseJPA warehouse = null;

			// This call may throw UserException.  If so, let it percolate up.
			// We want processing in this method to end early if any exception
			// occurs.
			warehouse = getWarehouseInstance(input.getInWareHouseId(), em);

			output.setOutWarehouseTax(warehouse.getWarehouseTax().doubleValue());

			// ***********************************************************
			// District 2.4.2.2 bullet 4
			// - get district Tax
			// - get district next order id (use as this OrderId)
			// - increment district next order id
			// ( for future New Order Transactions)
			// ***********************************************************

			DistrictJPA district = null;

			// This call may throw UserException.  If so, let it percolate up.
			// We want processing in this method to end early if any exception
			// occurs.
			district = getDistrictInstance(input.getInDistrictId(),
					input.getInWareHouseId(), em);

			int d_next_o_id_int = district.getDistrictNextOrderId();
			
			// get next OrderId
			output.setOutOrderId(d_next_o_id_int);
			output.setOutDistrictTax(district.getDistrictTax().doubleValue());
			
			// increment the next order ID for the district for future
			// NewOrdersTrans
			// int nextOrderId = d_next_o_id_int+1);
			district.setDistrictNextOrderId(d_next_o_id_int + 1);

			// ***********************************************************
			// Customer 2.4.2.2 bullet 5
			// - get Customer Discount, Customer Last Name & Customer Credit
			// ***********************************************************

			CustomerJPA customer = null;


			// This call may throw UserException.  If so, let it percolate up.
			// We want processing in this method to end early if any exception
			// occurs.	
			customer = getCustomerInstance(input.getInCustomerId(),
					input.getInDistrictId(), input.getInWareHouseId(), em);


			output.setOutCustomerDiscount(customer.getCustomerDiscount()
					.doubleValue() * 100);
			output.setOutCustomerLastName(customer.getCustomerLast());
			output.setOutCustomerCredit(customer.getCustomerCredit());

			// *********************************************************
			// 2.4.2.2 bullet 7 o_ol_cnt (items_on_order) is not entered
			// *********************************************************

			short o_ol_cnt = (short) input.getInItemList().length;
			output.setOutOrderItemCount(o_ol_cnt);
			output.setOutOrderEntryDate(input.getInOrderEntryDate());


			// This call may throw UserException.  If so, let it percolate up.
			// We want processing in this method to end early if any exception
			// occurs.	
			createOrderInstance(output.getOutOrderId(),
					input.getInDistrictId(), input.getInWareHouseId(),
					input.getInCustomerId(), timestamp, (short) 0,
					new java.math.BigDecimal(numberOfItems),
					(short) orderAllLocalFlag, em);

			// ********************************************************
			// The "big loop" to process all order lines in the new order
			// ********************************************************
			int ol_number;
			for (ol_number = 1; ol_number <= numberOfItems; ol_number++) {

				debugOut("<< **************************************************************************************************************** >>");
				debugOut("<< NewOrderSessionBean.processNewOrder()NewOrderSession processing ol_number = " + ol_number + " out of "
						+ numberOfItems + " total order lines");

				// ****************************************************
				// Copy repeating fields (for this instance) from input
				// structure to output.
				// ****************************************************
	
				output.getOutItemList()[ol_number - 1]
						.setOutItemSupplyWarehouseId(input.getInItemList()[ol_number - 1]
								.getInItemSupplyWarehouseId());

				output.getOutItemList()[ol_number - 1].setOutItemId(input
						.getInItemList()[ol_number - 1].getInItemId());

				output.getOutItemList()[ol_number - 1]
						.setOutItemQuantity((int) input.getInItemList()[ol_number - 1]
								.getInItemQuantity());

				// ***********************************************************
				// Item 2.4.2.2 bullet 8 part 1
				// - get item name, item Price and
				// item Data (this is used for Brand/generic test)
				// ***********************************************************

				// Uncomment this and comment out next line for HP
				// ItemJPA item = getItemInstance(output.getOutItemList()[ol_number
				// - 1]
				// .getOutItemId());
				
				ItemJPA item = null;

				// This call may throw UserException.  If so, let it percolate up.
				// We want processing in this method to end early if any exception
				// occurs.	
				item = getItemInstance(
						output.getOutItemList()[ol_number - 1].getOutItemId(),
						em);

				output.getOutItemList()[ol_number - 1].setOutItemPrice(item
						.getItemPrice().doubleValue());
				output.getOutItemList()[ol_number - 1].setOutItemName(new String(
						item.getItemName()));
				String i_data = new String(item.getItemData());


				// **************************************
				// Stock 2.4.2.2. bullet 8.2
				// **************************************


				StockJPA stock = null;

				// This call may throw UserException.  If so, let it percolate up.
				// We want processing in this method to end early if any exception
				// occurs.					
				stock = getStockInstance(
						output.getOutItemList()[ol_number - 1].getOutItemId(),
						output.getOutItemList()[ol_number - 1]
								.getOutItemSupplyWarehouseId(), em);

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
				// if there is a remote warehouse incrementRemoteCount
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

				debugOut("<< NewOrderSessionBean.processNewOrder() - creating OrderLine Object with the following Key attributes: ");

				debugOut("<< - output.getOutOrderId() =  "
						+ output.getOutOrderId());
				debugOut("<< - output.getOutDistrictId() = "
						+ output.getOutDistrictId());
				debugOut("<< - output.getOutWarehouseId() =  "
						+ output.getOutWarehouseId());
				debugOut("<< - olNumber =  " + ol_number);
				debugOut("<< - output.getOutItemList()[ol_number - 1].getOutItemId() =  "
						+ output.getOutItemList()[ol_number - 1].getOutItemId());
				debugOut("<< - output.getOutItemList()[ol_number - 1].getOutItemSupplyWarehouseId() =  "
						+ output.getOutItemList()[ol_number - 1]
								.getOutItemSupplyWarehouseId());
				debugOut("<< - timestamp =  " + timestamp);
				debugOut("<< - new java.math.BigDecimal(output.getOutItemList()[ol_number - 1].getOutItemQuantity())  "
						+ new java.math.BigDecimal(
								output.getOutItemList()[ol_number - 1]
										.getOutItemQuantity()));
				debugOut("<< - output.getOutItemList()[ol_number - 1].getOutItemTotal()  "
						+ output.getOutItemList()[ol_number - 1].getOutItemTotal());
				debugOut("<< - s_dist_info =  " + s_dist_info);

				// This call may throw UserException.  If so, let it percolate up.
				// We want processing in this method to end early if any exception
				// occurs.					
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


			}	// end "big loop" (for number of order lines in new order)

			// **********************************************************
			// 2.4.2.2 bullet 6 - CREATE NEWORDER object & SET values in it &
			// output.
			// ************************************************************

			debugOut("<< NewOrderSessionBean.processNewOrder() - creating NewOrder Object with the following Key attributes: ");
			debugOut("<< - WarehouseId =  " + input.getInWareHouseId());
			debugOut("<< - DistrictId = " + input.getInDistrictId());
			debugOut("<< - OrderId =  " + output.getOutOrderId());
			// debugOut("<<<*NO " + input.inWareHouseId + " " + input.inDistrictId +
			// " "+output.outOrderId+" " +getCurrentTime() );

			// This call may throw UserException.  If so, let it percolate up.
			// We want processing in this method to end early if any exception
			// occurs.				
			createNewOrderInstance(output.getOutOrderId(),
					input.getInDistrictId(), input.getInWareHouseId(), em);

			
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

			output.setStcnum(irwwbase.Stcnum.getInstance().getStcnum());

			return output;

		} finally {
			debugOut("<< Exit NewOrderSessionBean.processNewOrder() >>");
		}
	}


	//
	// Method to initialize the output structure of NewOrderTransaction
	//
	private NewOrderOutputInfo generateNewOrderOutput(byte db, int numberOfItems) {

		try {
			debugOut("<<< Enter NewOrderSessionBean.generateNewOrderOutput() >>>");
			
			NewOrderOutputInfo output = new NewOrderOutputInfo();
			output.setOutWarehouseId((short) 0);
			output.setOutDistrictId((short) 0);
			output.setOutCustomerId((short) 0);
			output.setOutOrderId((short) 0);
			// double outOrderItemCount
			output.setOutCustomerLastName(" ");
			output.setOutCustomerCredit(" ");
			// double outCustomerDiscount
			// double outWarehouseTax
			// double outDistrictTax
			// output.setOutOrderEntryDate(java.util.Calendar.getInstance());
			// double outTotal
			output.setOutMsg(" ");

			debugOut("<<< NewOrderSessionBean.generateNewOrderOutput(), numberOfItems = " + numberOfItems);

			NewOrderSingleItemOutInfo item[] = new NewOrderSingleItemOutInfo[numberOfItems];

			for (int i = 0; i < numberOfItems; i++) {

				item[i] = new NewOrderSingleItemOutInfo();
				item[i].setOutItemId((short) 0);
				item[i].setOutItemSupplyWarehouseId((short) 0);
				item[i].setOutItemName(" ");
				// double outItemQuantity
				// double outStockQuantity
				// char outBrandGeneric
				// double outItemPrice
				// double outItemTotal
			}

			output.setOutItemList(item);

			return output;

		} finally {
			debugOut("<<< Exit NewOrderSessionBean.generateNewOrderOutput() >>>");
		}
	}


	private WarehouseJPA getWarehouseInstance(short warehouseId,
			EntityManager em) throws UserException {

		try {
			debugOut("<<< Enter NewOrderSessionBean.getWarehouseInstance(), warehouseId: " + warehouseId + " >>>");
			
			WarehouseJPA warehouse = null;
			IRWWBase ib = new IRWWBase();

			try {

				warehouse = em.find(WarehouseJPA.class, warehouseId,
						LockModeType.PESSIMISTIC_READ);
				if (warehouse == null)
					throw new UserException("NewOrderSessionBean.getWarehouseInstance() failed, warehouse not found, warehouse="
							+ warehouseId, "NewOrderSessionBean.getWarehouseInstance() find warehouse failed");

			} catch (Exception t) {

				System.out.println ("<<< NewOrderSessionBean.getWarehouseInstance() caught exception: " + t.getMessage());
				if (ib.is913(t)) {
					throw new UserException("NewOrderSessionBean.getWarehouseInstance() failed, check for 913: " + ib.checkFor913(t) + ", cause: " + t.getMessage(), t.getMessage());
				} else {
					throw new UserException("NewOrderSessionBean.getWarehouseInstance() failed, cause: " + t.getMessage(), t.getMessage());
				}
			} // end try

			return warehouse;

		} finally {
			debugOut("<<< Exit NewOrderSessionBean.getWarehouseInstance() >>>");			
		}

	}


	private DistrictJPA getDistrictInstance(short districtId,
			short warehouseId, EntityManager em)
					throws UserException {

		try {
			debugOut("<<< Enter NewOrderSessionBean.getDistrictInstance(), districtId: " + districtId + " >>>");
	
			DistrictJPA district = null;
			IRWWBase ib = new IRWWBase();

			try {
				DistrictKey key = new DistrictKey();
				key.setDistrictId(districtId);
				key.setDistrictWareId(warehouseId);

				district = em.find(DistrictJPA.class, key,
						LockModeType.PESSIMISTIC_WRITE);

				if (district == null)
					throw new UserException("NewOrderSessionBean.getDistrictInstance(), district not found, districtId="
							+ districtId + " and warehouseId=" + warehouseId, "NewOrderSessionBean.getDistrictInstance() find district failed" );

			} catch (Exception t) {
				
				System.out.println("<<< NewOrderSessionBean.getDistrictInstance() caught exception: " + t.getMessage());
				if (ib.is913(t)) {
					throw new UserException("NewOrderSessionBean.getDistrictInstance() failed, check for 913: " + ib.checkFor913(t) + ", cause: " + t.getMessage(), t.getMessage());
				} else {
					throw new UserException("NewOrderSessionBean.getDistrictInstance() failed, cause: " + t.getMessage(), t.getMessage());
				}
			} // end try

			return district;

		} finally {
			debugOut("<<< Exit NewOrderSessionBean.getDistrictInstance() >>>");	
		}

	}


	private CustomerJPA getCustomerInstance(short customerId, short districtId,
			short warehouseId, EntityManager em) throws UserException {

		try {
			debugOut("<<< Enter NewOrderSessionBean.getCustomerInstance(), customerId: " + customerId + " >>>");

			CustomerJPA customer = null;
			IRWWBase ib = new IRWWBase();

			try {

				CustomerKey key = new CustomerKey();
				key.setCustomerDId(districtId);
				key.setCustomerId(customerId);
				key.setCustomerWId(warehouseId);

				customer = em.find(CustomerJPA.class, key,
						LockModeType.PESSIMISTIC_READ);
				
				if (customer == null) {
					throw new UserException("NewOrderSessionBean.getCustomerInstance(), customer not found, districtId="
							+ districtId + ", customerId=" + customerId + " and warehouseId=" + warehouseId,
							"NewOrderSessionBean.getCustomerInstance() find customer failed" );
				}

			} catch (Exception t) {
				System.out.println("<<< NewOrderSessionBean.getCustomerInstance() caught exception: " + t.getMessage());
				if (ib.is913(t)) {
					throw new UserException("NewOrderSessionBean.getCustomerInstance() failed, check for 913: " + ib.checkFor913(t) + ", cause: " + t.getMessage(), t.getMessage());
				} else {
					throw new UserException("NewOrderSessionBean.getCustomerInstance() failed, cause: " + t.getMessage(), t.getMessage());
				}
				
			} // end try
			return customer;

		} finally {
			debugOut("<<< Exit NewOrderSessionBean.getCustomerInstance() >>>");
		}

	}


	private void createOrderInstance(int outOrderId, short inDistrictId,
			short inWareHouseId, short inCustomerId, Timestamp orderEntryDat,
			short orderCarrierId, BigDecimal orderLineCount,
			short orderAllLocal, EntityManager em)
					throws UserException {

		try {
			debugOut("<<< Enter NewOrderSessionBean.createOrderInstance(), outOrderId: " + outOrderId + " >>>");
			
			IRWWBase ib = new IRWWBase();

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
				
				System.out.println("<<< NewOrderSessionBean.createOrderInstance() caught exception: " + ex.getMessage());
				if (ib.is913(ex)) {
					throw new UserException("NewOrderSessionBean.createOrderInstance() failed, check for 913: " + ib.checkFor913(ex) + ", cause: " + ex.getMessage(), ex.getMessage());
				} else {
					throw new UserException("NewOrderSessionBean.createOrderInstance() failed, cause: " + ex.getMessage(), ex.getMessage());
				}
			} // end try

			debugOut("<<< NewOrderSessionBean.createOrderInstance(), OrderJPA instance created where WarehouseId = "
					+ inWareHouseId	+ " District Id = "	+ inDistrictId	+ " OrderId = " + outOrderId);

		} finally {
			debugOut("<<< Exit NewOrderSessionBean.createOrderInstance() >>>");
		}

	}


	private ItemJPA getItemInstance(int itemNum, EntityManager em) throws UserException {

		try {
			debugOut("<<< Enter NewOrderSessionBean.getItemInstance(), itemNum: " + itemNum + " >>>");

			ItemJPA item = null;
			IRWWBase ib = new IRWWBase();

			try {
				// Uncomment next two lines and remove em from the method arg list
				// for HP
				// EntityManager em = null;

				// em = emf2.createEntityManager();

				item = em.find(ItemJPA.class, itemNum);
				
				if (item == null) {
					throw new UserException("NewOrderSessionBean.getItemInstance(), item not found, itemNum = "
							+ itemNum, 	"NewOrderSessionBean.getItemInstance() find item failed" );
				}
		
			} catch (Exception t) {

				System.out.println("<<< NewOrderSessionBean.getItemInstance() caught exception: " + t.getMessage());
				if (ib.is913(t)) {
					throw new UserException("NewOrderSessionBean.getItemInstance() failed, check for 913: " + ib.checkFor913(t) + ", cause: " + t.getMessage(), t.getMessage());
				} else {
					throw new UserException("NewOrderSessionBean.getItemInstance() failed, cause: " + t.getMessage(), t.getMessage());
				}
			} // end try
			return item;

		} finally {
			debugOut("<<< Exit NewOrderSessionBean.getItemInstance() >>>");			
		}

	}


	private StockJPA getStockInstance(int outItemId,
			short outItemSupplyWarehouseId, EntityManager em)
					throws UserException {

		try {
			debugOut("<<< Enter NewOrderSessionBean.getStockInstance(), outItemId: " + outItemId + " >>>");
			
			StockJPA stock = null;
			IRWWBase ib = new IRWWBase();

			try {

				StockKey key = new StockKey();
				key.setS_I_ID(outItemId);
				key.setS_W_ID(outItemSupplyWarehouseId);

				stock = em.find(StockJPA.class, key, LockModeType.PESSIMISTIC_WRITE);
				
				if (stock == null) {
					throw new UserException("NewOrderSessionBean.getStockInstance(), stock not found, outItemId = "	+ outItemId 
							+ " outItemSupplyWarehouseId = " +  outItemSupplyWarehouseId, "NewOrderSessionBean.getStockInstance() find stock failed" );
				}	

			} catch (Exception t) {
	
				System.out.println("<<< NewOrderSessionBean.getStockInstance() caught exception: " + t.getMessage());
				if (ib.is913(t))
					throw new UserException("NewOrderSessionBean.getStockInstance() failed, check for 913: " + ib.checkFor913(t) + ", cause: " + t.getMessage(), t.getMessage());
				else
					throw new UserException("NewOrderSessionBean.getStockInstance() failed, cause: " + t.getMessage(), t.getMessage());
			} // end try
			return stock;

		} finally {
			debugOut("<<< Exit NewOrderSessionBean.getStockInstance() >>>");
		}
	}


	private int checkForOverflow(int val) {
		if (val >= 9998)
			val = 0;
		return val;
	}


	private String getDistrictInfo(String districtId, StockJPA stock)
			throws UserException {

		try {
			debugOut("<<< Enter NewOrderSessionBean.getDistrictInfo(), districtId: " + districtId + " >>>");			

			IRWWBase ib = new IRWWBase();

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
					System.out.println("NewOrderSessionBean.getDistrictInfo() failed because districtId was out of valid range: " + districtId);
					throw new UserException("NewOrderSessionBean.getDistrictInfo(), districtId is out of valid range: " + districtId,
							"NewOrderSessionBean.getDistrictInfo() failed because districtId was out of valid range: " + districtId);
				}

			} catch (Exception ex) {
				
				System.out.println("<<< NewOrderSessionBean.getDistrictInfo() caught exception: " + ex.getMessage());
				if (ib.is913(ex))
					throw new UserException("NewOrderSessionBean.getDistrictInfo() failed, check for 913: " + ib.checkFor913(ex) + ", cause: " + ex.getMessage(), ex.getMessage());
				else
					throw new UserException("NewOrderSessionBean.getStockInstance() failed, cause: " + ex.getMessage(), ex.getMessage());
				
			} // end try

		} finally {
			debugOut("<<< Exit NewOrderSessionBean.getDistrictInfo() >>>");			
		}

	}


	private void createOrderLineInstance(int outOrderId, short outDistrictId,
			short outWarehouseId, int ol_number, int outItemId,
			short outItemSupplyWarehouseId, Timestamp orderDeliveryDate,
			BigDecimal quantity, double amount, String districtInfo,
			EntityManager em) throws UserException {


		try {
			debugOut("<<< Enter NewOrderSessionBean.createOrderLineInstance(), ol_number: " + ol_number + " >>>");			

			IRWWBase ib = new IRWWBase();
			java.math.BigDecimal amt = this.convertToMoney(amount);

			if (amt.doubleValue() > 9999.99)
				amt = new BigDecimal(9999.00);
			amt.setScale(2, RoundingMode.DOWN);

			if (quantity.intValue() > 99)
				quantity = new BigDecimal(99.00);
			quantity.setScale(0, RoundingMode.DOWN);

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
				
				System.out.println("<<< NewOrderSessionBean.createOrderLineInstance() caught exception: " + ex.getMessage());
				if (ib.is913(ex)) {
					throw new UserException("NewOrderSessionBean.createOrderLineInstance() failed, check for 913: " + ib.checkFor913(ex) + ", cause: " + ex.getMessage(), ex.getMessage());
				} else {
					throw new UserException("NewOrderSessionBean.createOrderLineInstance() failed, cause: " + ex.getMessage(), ex.getMessage());
				}	

			}
			return;

		} finally {
			debugOut("<<< Exit NewOrderSessionBean.createOrderLineInstance() >>>");			
		}

	}


	private void createNewOrderInstance(int outOrderId, short inDistrictId,
			short inWareHouseId, EntityManager em)
					throws UserException {

		try {
			debugOut("<<< Enter NewOrderSessionBean.createNewOrderInstance(), outOrderId: " + outOrderId + " >>>");			

			IRWWBase ib = new IRWWBase();

			try {

				NewOrderJPA neworder = new NewOrderJPA();
				neworder.setOrderId(outOrderId);
				neworder.setDistrictId(inDistrictId);
				neworder.setWarehouseId(inWareHouseId);

				em.persist(neworder);

			} catch (Exception ex) {
				
				System.out.println("<<< NewOrderSessionBean.createNewOrderInstance() caught exception: " + ex.getMessage());
				if (ib.is913(ex)) {
					throw new UserException("NewOrderSessionBean.createNewOrderInstance() failed, check for 913: " + ib.checkFor913(ex) + ", cause: " + ex.getMessage(), ex.getMessage());
				} else {
					throw new UserException("NewOrderSessionBean.createNewOrderInstance() failed, cause: " + ex.getMessage(), ex.getMessage());
				}	

			} // end try

			debugOut("<<< NewOrderSessionBean.createNewOrderInstance(), NewOrderJPA instance created where WarehouseId = "
					+ inWareHouseId	+ " District Id = "	+ inDistrictId	+ " OrderId = " + outOrderId);
			return;

		} finally {
			debugOut("<<< Exit NewOrderSessionBean.createNewOrderInstance() >>>");						
		}
	}


	private BigDecimal convertToMoney(double doubleVal) {

		java.math.BigDecimal outMoney = new java.math.BigDecimal(doubleVal)
		.setScale(2, RoundingMode.HALF_EVEN);

		return outMoney;
	}


	private void debugOut(String message) {
		/*
		 * if (System.getProperty("DEBUG_OUT_HTTP") != null) { if
		 * (System.getProperty("DEBUG_OUT_HTTP").equals("ON")) { String ts =
		 * (new Timestamp(System.currentTimeMillis())) .toString(); String tn =
		 * Thread.currentThread().getName(); System.out.println("Trace: " + ts + " " +
		 * tn + " " + message); } }
		 */
		if (System.getProperty("DEBUG_OUT") != null) {
			if (System.getProperty("DEBUG_OUT").equals("ON")) {
				System.out.println(message);
			}
		}
	}

}
