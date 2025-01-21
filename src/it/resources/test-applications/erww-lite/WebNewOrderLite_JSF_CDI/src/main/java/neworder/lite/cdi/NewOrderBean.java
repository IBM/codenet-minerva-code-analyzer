package neworder.lite.cdi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.TransactionRequiredException;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

import cdi.fragment.CustomerQualifier;
import cdi.fragment.DistrictQualifier;
import cdi.fragment.ItemQualifier;
import cdi.fragment.ItemSupplyWarehouseQualifier;
import cdi.fragment.NewOrderEvent;
import cdi.fragment.QuantityQualifier;
import cdi.fragment.SpecialOfferEvent;
import cdi.fragment.WarehouseQualifier;
import newordersession.NewOrderInputInfo;
import newordersession.NewOrderOutputInfo;
import newordersession.NewOrderSingleItemInInfo;
import newordersession.NewOrderSingleItemOutInfo;
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
import itemjpa.ItemJPA;
import irwwbase.ExtendedRandom;
import irwwbase.IRWWBase;
import irwwbase.MaxValues;

import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;

import static jakarta.enterprise.event.TransactionPhase.BEFORE_COMPLETION;

@Named("newOrderBean")
@RequestScoped
public class NewOrderBean {

	@Inject 
	@CustomerQualifier 
	@DistrictQualifier 
	@WarehouseQualifier 
	@ItemQualifier 
	@QuantityQualifier 
	@ItemSupplyWarehouseQualifier
	private NewOrderValidatorBean newOrderValidatorBean;
	
	@PersistenceUnit(unitName = "WebNewOrderLite_JSF_CDI")
	EntityManagerFactory emf;
		
	@Inject Event<NewOrderEvent> newOrderEvent;
	
	String classname = "NewOrderBean";
 
	private String inCustomerId = null;
	private String inDistrictId = null;
	private String inWarehouseId = null;
	private String itemId1 = null;
	private String quantity1 = null;
	private String itemSupplyWarehouseId1 = null;
	private String itemId2 = null;
	private String quantity2 = null;
	private String itemSupplyWarehouseId2 = null;
	private String itemId3 = null;
	private String quantity3 = null;
	private String itemSupplyWarehouseId3 = null;
	private String itemId4 = null;
	private String quantity4 = null;
	private String itemSupplyWarehouseId4 = null;
	private String itemId5 = null;
	private String quantity5 = null;
	private String itemSupplyWarehouseId5 = null;
	private String itemId6 = null;
	private String quantity6 = null;
	private String itemSupplyWarehouseId6 = null;
	private String itemId7 = null;
	private String quantity7 = null;
	private String itemSupplyWarehouseId7 = null;
	private String itemId8 = null;
	private String quantity8 = null;
	private String itemSupplyWarehouseId8 = null;
	private String itemId9 = null;
	private String quantity9 = null;
	private String itemSupplyWarehouseId9 = null;
	private String itemId10 = null;
	private String quantity10 = null;
	private String itemSupplyWarehouseId10 = null;
	private String itemId11 = null;
	private String quantity11 = null;
	private String itemSupplyWarehouseId11 = null;
	private String itemId12 = null;
	private String quantity12 = null;
	private String itemSupplyWarehouseId12 = null;
	private String itemId13 = null;
	private String quantity13 = null;
	private String itemSupplyWarehouseId13 = null;
	private String itemId14 = null;
	private String quantity14 = null;
	private String itemSupplyWarehouseId14 = null;
	private String itemId15 = null;
	private String quantity15 = null;
	private String itemSupplyWarehouseId15 = null;	
	private String coupon = "Failed NewOrder Coupon";
	
	private NewOrderInputInfo input = null;
	private NewOrderOutputInfo output = null;
		
	IRWWBase irwwbase = new IRWWBase();
	
	public NewOrderBean(){
	}
	
	public NewOrderValidatorBean getNewOrderValidatorBean() {
		return newOrderValidatorBean;
	}
	
	public void setCustomerValidatorBean(NewOrderValidatorBean newOrderValidatorBean) {
		this.newOrderValidatorBean = newOrderValidatorBean;
	}
				 
	public String getInCustomerId() {
		return inCustomerId;
	}
	
	public void setInCustomerId(String inCustomerId) {
		this.inCustomerId = inCustomerId;
	}
	 
	public String getInDistrictId() {
		return inDistrictId;
	}
	
	public void setInDistrictId(String inDistrictId) {
		this.inDistrictId = inDistrictId;
	}
	
	public String getInWarehouseId() {
		return inWarehouseId;
	}
	
	public void setInWarehouseId(String inWarehouseId) {
		this.inWarehouseId = inWarehouseId;
	}

	public String getItemId1() {
		return itemId1;
	}

	public void setItemId1(String itemId1) {
		this.itemId1 = itemId1;
	}

	public String getQuantity1() {
		return quantity1;
	}

	public void setQuantity1(String quantity1) {
		this.quantity1 = quantity1;
	}

	public String getItemSupplyWarehouseId1() {
		return itemSupplyWarehouseId1;
	}

	public void setItemSupplyWarehouseId1(String itemSupplyWarehouseId1) {
		this.itemSupplyWarehouseId1 = itemSupplyWarehouseId1;
	}
	
	public String getItemId2() {
		return itemId2;
	}

	public void setItemId2(String itemId2) {
		this.itemId2 = itemId2;
	}

	public String getQuantity2() {
		return quantity2;
	}

	public void setQuantity2(String quantity2) {
		this.quantity2 = quantity2;
	}

	public String getItemSupplyWarehouseId2() {
		return itemSupplyWarehouseId2;
	}

	public void setItemSupplyWarehouseId2(String itemSupplyWarehouseId2) {
		this.itemSupplyWarehouseId2 = itemSupplyWarehouseId2;
	}

	public String getItemId3() {
		return itemId3;
	}

	public void setItemId3(String itemId3) {
		this.itemId3 = itemId3;
	}

	public String getQuantity3() {
		return quantity3;
	}

	public void setQuantity3(String quantity3) {
		this.quantity3 = quantity3;
	}

	public String getItemSupplyWarehouseId3() {
		return itemSupplyWarehouseId3;
	}

	public void setItemSupplyWarehouseId3(String itemSupplyWarehouseId3) {
		this.itemSupplyWarehouseId3 = itemSupplyWarehouseId3;
	}

	public String getItemId4() {
		return itemId4;
	}

	public void setItemId4(String itemId4) {
		this.itemId4 = itemId4;
	}

	public String getQuantity4() {
		return quantity4;
	}

	public void setQuantity4(String quantity4) {
		this.quantity4 = quantity4;
	}

	public String getItemSupplyWarehouseId4() {
		return itemSupplyWarehouseId4;
	}

	public void setItemSupplyWarehouseId4(String itemSupplyWarehouseId4) {
		this.itemSupplyWarehouseId4 = itemSupplyWarehouseId4;
	}

	public String getItemId5() {
		return itemId5;
	}

	public void setItemId5(String itemId5) {
		this.itemId5 = itemId5;
	}

	public String getQuantity5() {
		return quantity5;
	}

	public void setQuantity5(String quantity5) {
		this.quantity5 = quantity5;
	}

	public String getItemSupplyWarehouseId5() {
		return itemSupplyWarehouseId5;
	}

	public void setItemSupplyWarehouseId5(String itemSupplyWarehouseId5) {
		this.itemSupplyWarehouseId5 = itemSupplyWarehouseId5;
	}

	public String getItemId6() {
		return itemId6;
	}

	public void setItemId6(String itemId6) {
		this.itemId6 = itemId6;
	}

	public String getQuantity6() {
		return quantity6;
	}

	public void setQuantity6(String quantity6) {
		this.quantity6 = quantity6;
	}

	public String getItemSupplyWarehouseId6() {
		return itemSupplyWarehouseId6;
	}

	public void setItemSupplyWarehouseId6(String itemSupplyWarehouseId6) {
		this.itemSupplyWarehouseId6 = itemSupplyWarehouseId6;
	}

	public String getItemId7() {
		return itemId7;
	}

	public void setItemId7(String itemId7) {
		this.itemId7 = itemId7;
	}

	public String getQuantity7() {
		return quantity7;
	}

	public void setQuantity7(String quantity7) {
		this.quantity7 = quantity7;
	}

	public String getItemSupplyWarehouseId7() {
		return itemSupplyWarehouseId7;
	}

	public void setItemSupplyWarehouseId7(String itemSupplyWarehouseId7) {
		this.itemSupplyWarehouseId7 = itemSupplyWarehouseId7;
	}

	public String getItemId8() {
		return itemId8;
	}

	public void setItemId8(String itemId8) {
		this.itemId8 = itemId8;
	}

	public String getQuantity8() {
		return quantity8;
	}

	public void setQuantity8(String quantity8) {
		this.quantity8 = quantity8;
	}

	public String getItemSupplyWarehouseId8() {
		return itemSupplyWarehouseId8;
	}

	public void setItemSupplyWarehouseId8(String itemSupplyWarehouseId8) {
		this.itemSupplyWarehouseId8 = itemSupplyWarehouseId8;
	}

	public String getItemId9() {
		return itemId9;
	}

	public void setItemId9(String itemId9) {
		this.itemId9 = itemId9;
	}

	public String getQuantity9() {
		return quantity9;
	}

	public void setQuantity9(String quantity9) {
		this.quantity9 = quantity9;
	}

	public String getItemSupplyWarehouseId9() {
		return itemSupplyWarehouseId9;
	}

	public void setItemSupplyWarehouseId9(String itemSupplyWarehouseId9) {
		this.itemSupplyWarehouseId9 = itemSupplyWarehouseId9;
	}

	public String getItemId10() {
		return itemId10;
	}

	public void setItemId10(String itemId10) {
		this.itemId10 = itemId10;
	}

	public String getQuantity10() {
		return quantity10;
	}

	public void setQuantity10(String quantity10) {
		this.quantity10 = quantity10;
	}

	public String getItemSupplyWarehouseId10() {
		return itemSupplyWarehouseId10;
	}

	public void setItemSupplyWarehouseId10(String itemSupplyWarehouseId10) {
		this.itemSupplyWarehouseId10 = itemSupplyWarehouseId10;
	}

	public String getItemId11() {
		return itemId11;
	}

	public void setItemId11(String itemId11) {
		this.itemId11 = itemId11;
	}

	public String getQuantity11() {
		return quantity11;
	}

	public void setQuantity11(String quantity11) {
		this.quantity11 = quantity11;
	}

	public String getItemSupplyWarehouseId11() {
		return itemSupplyWarehouseId11;
	}

	public void setItemSupplyWarehouseId11(String itemSupplyWarehouseId11) {
		this.itemSupplyWarehouseId11 = itemSupplyWarehouseId11;
	}

	public String getItemId12() {
		return itemId12;
	}

	public void setItemId12(String itemId12) {
		this.itemId12 = itemId12;
	}

	public String getQuantity12() {
		return quantity12;
	}

	public void setQuantity12(String quantity12) {
		this.quantity12 = quantity12;
	}

	public String getItemSupplyWarehouseId12() {
		return itemSupplyWarehouseId12;
	}

	public void setItemSupplyWarehouseId12(String itemSupplyWarehouseId12) {
		this.itemSupplyWarehouseId12 = itemSupplyWarehouseId12;
	}

	public String getItemId13() {
		return itemId13;
	}

	public void setItemId13(String itemId13) {
		this.itemId13 = itemId13;
	}

	public String getQuantity13() {
		return quantity13;
	}

	public void setQuantity13(String quantity13) {
		this.quantity13 = quantity13;
	}

	public String getItemSupplyWarehouseId13() {
		return itemSupplyWarehouseId13;
	}

	public void setItemSupplyWarehouseId13(String itemSupplyWarehouseId13) {
		this.itemSupplyWarehouseId13 = itemSupplyWarehouseId13;
	}

	public String getItemId14() {
		return itemId14;
	}

	public void setItemId14(String itemId14) {
		this.itemId14 = itemId14;
	}

	public String getQuantity14() {
		return quantity14;
	}

	public void setQuantity14(String quantity14) {
		this.quantity14 = quantity14;
	}

	public String getItemSupplyWarehouseId14() {
		return itemSupplyWarehouseId14;
	}

	public void setItemSupplyWarehouseId14(String itemSupplyWarehouseId14) {
		this.itemSupplyWarehouseId14 = itemSupplyWarehouseId14;
	}

	public String getItemId15() {
		return itemId15;
	}

	public void setItemId15(String itemId15) {
		this.itemId15 = itemId15;
	}

	public String getQuantity15() {
		return quantity15;
	}

	public void setQuantity15(String quantity15) {
		this.quantity15 = quantity15;
	}

	public String getItemSupplyWarehouseId15() {
		return itemSupplyWarehouseId15;
	}

	public void setItemSupplyWarehouseId15(String itemSupplyWarehouseId15) {
		this.itemSupplyWarehouseId15 = itemSupplyWarehouseId15;
	}
	
	public String getCoupon() {
		return coupon;
	}

	public void setCoupon(String coupon) {
		this.coupon = coupon;
	}

	public NewOrderOutputInfo getOutput() {
		return output;
	}

	public void setOutput(NewOrderOutputInfo output) {
		this.output = output;
	}

	public String autoGenerationPath() throws Exception {
		irwwbase.debugOut("<<< NewOrderBean: AutoGen Path");
				
		String status="FAILED";
		
		UserTransaction ut = null;
		EntityManager em = null;

		em = emf.createEntityManager();
		
		try {
			ut = this.getUserTrans();
		} catch (NamingException e) {
			e.printStackTrace();
			throw new Exception(">>>> Error in " + classname +  ": autoGenerationPath(): Exception calling this.getUserTrans(): "	+ 
					" Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause());
		}
		
		try {
			ut.begin();
			em.joinTransaction();
		} catch (NotSupportedException | SystemException | TransactionRequiredException e) {
			e.printStackTrace();
			throw new Exception(">>>> Error in " + classname +  ": autoGenerationPath(): Exception calling ut.begin() or em.joinTransaction(): "	+ 
					" Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause());
		} 		

		byte database = irwwbase.dbSize();

		input = this.generateNewOrderInput(database);
		output = this.generateNewOrderOutput(database, input.getNumberOfItems());
		output.setOutMsg(status);

		try {
			output = this.createNewOrder(em, ut);
			irwwbase.debugOut(">>>>> SUCCESSFUL");
			status = "SUCCESSFUL";
			output.setOutMsg(status);
		} catch (IllegalStateException | SecurityException | SystemException e) {
			e.printStackTrace();
			throw new Exception(">>>> Error in " + classname +  ": autoGenerationPath(): Exception calling output=createNewOrder(input, output): " +
					" Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(">>>> Error in " + classname +  ": autoGenerationPath(): Exception calling output=createNewOrder(input, output): " +
					" Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause());
		} 
		
		irwwbase.debugOut(">>>>> Before ut.commit()");
		try {
			ut.commit();
		} catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | RollbackException | SystemException e) {
			e.printStackTrace();
			throw new Exception(">>>> Error in " + classname +  ": autoGenerationPath(): Exception calling ut.commit(): " +
					" Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause());
		} 
		irwwbase.debugOut(">>>>> After ut.commit()- output.getOutTotal() = " + output.getOutTotal());
		
		try {
			if (em!=null && em.isOpen()){
				em.close();
			}
		}catch(Throwable e){
				System.out.println("<<< NewOrderBean - em.close() - Exception: " + e);
				e.printStackTrace();
				throw new Exception(">>>> Error in " + classname +  ": autoGenerationPath(): Exception calling em.close(): " +
						" Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause());
	    }
				
		return status;
	}
	
	public String manualPath() throws Exception {
		irwwbase.debugOut("<<< NewOrderBean: Manual Path");
		
		String status="FAILED";
		
		UserTransaction ut = null;
		EntityManager em = null;

		em = emf.createEntityManager();		
		
		try {
			ut = this.getUserTrans();
		} catch (NamingException e) {
			e.printStackTrace();
			throw new Exception(">>>> Error in " + classname +  ": manualPath(): Exception calling this.getUserTrans(): "	+ 
					" Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause());
		}
		
		try {
			ut.begin();
			em.joinTransaction();
		} catch (NotSupportedException | SystemException | TransactionRequiredException e) {
			e.printStackTrace();
			throw new Exception(">>>> Error in " + classname +  ": manualPath(): Exception calling ut.begin() or em.joinTransaction(): "	+ 
					" Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause());
		} 	
	
		input = this.generateNewOrderInput(irwwbase.dbSize());			
		input = this.getManualInput();
		
		output = this.generateNewOrderOutput(irwwbase.dbSize(), input.getNumberOfItems());
		output.setOutMsg(status);

		try {
			output = this.createNewOrder(em, ut);
			irwwbase.debugOut(">>>>> SUCCESSFUL: 02/21/2014");
			status = "SUCCESSFUL";
			output.setOutMsg(status);
		} catch (IllegalStateException | SecurityException | SystemException e) {
			e.printStackTrace();
			throw new Exception(">>>> Error in " + classname +  ": manualPath(): Exception calling output=createNewOrder(input, output): " +
					" Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause());
		} catch (Throwable e) {
			e.printStackTrace();
			throw new Exception(">>>> Error in " + classname +  ": manualPath(): Exception calling output=createNewOrder(input, output): " +
					" Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause());
		} 
		
		irwwbase.debugOut(">>>>> Before ut.commit()");
		try {
			ut.commit();
		} catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | RollbackException | SystemException e) {
			e.printStackTrace();
			throw new Exception(">>>> Error in " + classname +  ": manualPath(): Exception calling ut.commit(): " +
					" Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause());
		} 
		irwwbase.debugOut(">>>>> After ut.commit()- output.getOutTotal() = " + output.getOutTotal());
		
		try {
			if (em!=null && em.isOpen()){
				em.close();
			}
		}catch(Throwable e){
			System.out.println("<<< NewOrderBean - em.close() - Exception: " + e);
			e.printStackTrace();
			throw new Exception(">>>> Error in " + classname +  ": manualPath(): Exception calling em.close(): " +
					" Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause());
		}
		
		return status;
	}
	
	public NewOrderOutputInfo createNewOrder(EntityManager em, UserTransaction ut) 
			throws IllegalStateException, SecurityException, SystemException, Exception {
		
		int orderAllLocalFlag = 1;
		java.util.Date date = null;
		java.sql.Timestamp timestamp = null;
	
		date = java.util.Calendar.getInstance().getTime();
		timestamp = new java.sql.Timestamp(date.getTime());
		output.setOutTotal(0);
		
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
		irwwbase.debugOut("<<< NewOrderBean - getting Warehouse Object "
				+ irwwbase.getCurrentTime());

		WarehouseJPA warehouse = null;

		try {
			warehouse = getWarehouseInstance(input.getInWareHouseId(), em, ut);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(">>>> Error in " + classname + "Exception getting warhouseInstance", e);
		}

		irwwbase.debugOut("<<< NewOrderBean - warehouse.getWarehouseTax() = "+warehouse.getWarehouseTax());
		output.setOutWarehouseTax(warehouse.getWarehouseTax().doubleValue());

		// ***********************************************************
		// District 2.4.2.2 bullet 4
		// - get district Tax
		// - get district next order id (use as this OrderId)
		// - increment district next order id
		// (for future New Order Transactions)
		// ***********************************************************
		irwwbase.debugOut("<<< NewOrderBean - getting District Object..."
				+ irwwbase.getCurrentTime());
		DistrictJPA district = null;
		try {
			irwwbase.debugOut("<<< input.getInDistrictId() = "+ input.getInDistrictId());
			irwwbase.debugOut("<<< input.getInWareHouseId() = "+ input.getInWareHouseId());
			
			district = getDistrictInstance(input.getInDistrictId(),input.getInWareHouseId(),em,ut);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(">>>> Error in " + classname + "Exception getting districtInstance", e);
		}
		irwwbase.debugOut("<<< after - getting District Object..."
				+ irwwbase.getCurrentTime());

		irwwbase.debugOut("<<< Before get the next orderId" + irwwbase.getCurrentTime());
		int d_next_o_id_int = district.getDistrictNextOrderId();

		irwwbase.debugOut("<<< After getting the next orderId" + irwwbase.getCurrentTime());

		output.setOutOrderId(d_next_o_id_int);
		irwwbase.debugOut("<<<output.outOrderId: " + output.getOutOrderId() + " "
				+ irwwbase.getCurrentTime());
		irwwbase.debugOut("<<<* " + input.getInWareHouseId() + " "
				+ input.getInDistrictId() + " " + output.getOutOrderId() + " "
				+ irwwbase.getCurrentTime());

		output.setOutDistrictTax(district.getDistrictTax().doubleValue());
		
		// increment the next order ID for the district for future NewOrdersTrans
		irwwbase.debugOut("<<<nextOrderId being put back into district: "
				+ (d_next_o_id_int + 1) + " " + irwwbase.getCurrentTime());
		district.setDistrictNextOrderId(d_next_o_id_int + 1);

		// ***********************************************************
		// Customer 2.4.2.2 bullet 5
		// - get Customer Discount, Customer Last Name & Customer Credit
		// ***********************************************************

		irwwbase.debugOut("<<< NewOrderBean - getting Customer Object..."
				+ irwwbase.getCurrentTime());

		CustomerJPA customer = null;
		try {
			customer = getCustomerInstance(input.getInCustomerId(),
					input.getInDistrictId(), input.getInWareHouseId(), em, ut);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(">>>> Error in " + classname + "Exception getting customerInstance", e);
		}

		output.setOutCustomerDiscount(customer.getCustomerDiscount()
				.doubleValue() * 100);
		output.setOutCustomerLastName(customer.getCustomerLast());
		output.setOutCustomerCredit(customer.getCustomerCredit());
		//customerYTDPayment = customer.getCustomerYtdPayment();

		// *********************************************************
		// 2.4.2.2 bullet 7 o_ol_cnt (items_on_order) is not entered
		// *********************************************************

		short o_ol_cnt = (short) input.getNumberOfItems();
		output.setOutOrderItemCount(o_ol_cnt);
		irwwbase.debugOut("<<< NewOrderBean - Number of Items in the List = o_ol_cnt = "
				+ o_ol_cnt + " " + irwwbase.getCurrentTime());

		irwwbase.debugOut("<<< input.getNumberOfItems() = "
				+ input.getNumberOfItems());

		output.setOutOrderEntryDate(input.getInOrderEntryDate());

		try {
			createOrderInstance(output.getOutOrderId(),
					input.getInDistrictId(), input.getInWareHouseId(),
					input.getInCustomerId(), timestamp, (short) 0,
					new java.math.BigDecimal(input.getNumberOfItems()),
					(short) orderAllLocalFlag, em, ut);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(">>>> Error in " + classname + "Exception creating orderInstance", e);
		}

		int ol_number;
		for (ol_number = 1; ol_number <= input.getNumberOfItems(); ol_number++) {

			irwwbase.debugOut("<<< NewOrderBean - ol_number = " + ol_number + " "
					+ irwwbase.getCurrentTime());

			// ****************************************************
			// Copy repeating fields (for this instance) from input
			// structure to output.
			// ****************************************************
			irwwbase.debugOut("<<< setOutItemSupplyWarehouseId ");
			irwwbase.debugOut("<<< server - # objects in array - input.getInItemList().length = "
					+ input.getInItemList().length);
			irwwbase.debugOut("<<< server - # objects in array - output.getOutItemList().length = "
					+ output.getOutItemList().length);
			irwwbase.debugOut("<<< ol_number = " + ol_number);
			output.getOutItemList()[ol_number - 1]
					.setOutItemSupplyWarehouseId(input.getInItemList()[ol_number - 1]
							.getInItemSupplyWarehouseId());

			irwwbase.debugOut("<<< setOutItemId ");
			output.getOutItemList()[ol_number - 1].setOutItemId(input
					.getInItemList()[ol_number - 1].getInItemId());

			irwwbase.debugOut("<<< setOutItemQuantity");
			output.getOutItemList()[ol_number - 1]
					.setOutItemQuantity((int) input.getInItemList()[ol_number - 1]
							.getInItemQuantity());

			// ***********************************************************
			// Item 2.4.2.2 bullet 8 part 1
			// - get item name, item Price and
			// item Data (this is used for Brand/generic test)
			// ***********************************************************

			irwwbase.debugOut("<<< NewOrderBean - getting Item Object..."
					+ irwwbase.getCurrentTime());

			ItemJPA item = null;
			try {
				item = getItemInstance(
						output.getOutItemList()[ol_number - 1].getOutItemId(),
						em, ut);
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(">>>> Error in " + classname + "Exception getting itemInstance", e);
			}

			output.getOutItemList()[ol_number - 1].setOutItemPrice(item
					.getItemPrice().doubleValue());
			output.getOutItemList()[ol_number - 1].setOutItemName(new String(
					item.getItemName()));
			String i_data = new String(item.getItemData());

			irwwbase.debugOut("<<< NewOrderBean - ItemId = "
					+ output.getOutItemList()[ol_number - 1].getOutItemId()
					+ " " + irwwbase.getCurrentTime());

			// **************************************
			// Stock 2.4.2.2. bullet 8.2
			// **************************************

			irwwbase.debugOut("<<< NewOrderBean - getting Stock Object..."
					+ irwwbase.getCurrentTime());
			StockJPA stock = null;
			try {
				stock = getStockInstance(
						output.getOutItemList()[ol_number - 1].getOutItemId(),
						output.getOutItemList()[ol_number - 1]
								.getOutItemSupplyWarehouseId(), em);
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(">>>> Error in " + classname + "Exception getting stock Instance", e);
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

			// 2.4.2.2 bullet 8.2
			stock.setS_YTD(new BigDecimal(stock.getS_YTD().intValue()
					+ output.getOutItemList()[ol_number - 1]
							.getOutItemQuantity()));

			stock.setS_ORDER_CNT(new BigDecimal(checkForOverflow(stock
					.getS_ORDER_CNT().intValue()) + 1));
			
			String s_dist_info = getDistrictInfo(Short.valueOf(output.getOutDistrictId()).toString(), stock);
			String s_data = stock.getS_DATA();

			// ****************************************************
			// if there if there is a remote warehouse.
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

			irwwbase.debugOut("<<< NewOrderBean - creating OrderLine Object with the following Key attributes: "
					+ irwwbase.getCurrentTime());

			irwwbase.debugOut("<<< 	    	 - output.getOutOrderId() =  "
					+ output.getOutOrderId());
			irwwbase.debugOut("<<<  		     - output.getOutDistrictId() = "
					+ output.getOutDistrictId());
			irwwbase.debugOut("<<< 			 - output.getOutWarehouseId() =  "
					+ output.getOutWarehouseId());
			irwwbase.debugOut("<<< 			 - olNumber =  " + ol_number);
			irwwbase.debugOut("<<< 			 - output.getOutItemList()[ol_number - 1].getOutItemId() =  "
					+ output.getOutItemList()[ol_number - 1].getOutItemId());
			irwwbase.debugOut("<<< 		     - output.getOutItemList()[ol_number - 1].getOutItemSupplyWarehouseId() =  "
					+ output.getOutItemList()[ol_number - 1]
							.getOutItemSupplyWarehouseId());
			irwwbase.debugOut("<<< 			 - timestamp =  " + timestamp);
			irwwbase.debugOut("<<< 			 - new java.math.BigDecimal(output.getOutItemList()[ol_number - 1].getOutItemQuantity())  "
					+ new java.math.BigDecimal(
							output.getOutItemList()[ol_number - 1]
									.getOutItemQuantity()));
			irwwbase.debugOut("<<< 			 - output.getOutItemList()[ol_number - 1].getOutItemTotal()  "
					+ output.getOutItemList()[ol_number - 1].getOutItemTotal());
			irwwbase.debugOut("<<< 		     - s_dist_info =  " + s_dist_info);

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
								.getOutItemTotal(), s_dist_info, em, ut);
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("Exception creating orderline Instance", e);
			}

		}
		// end loop

		// **********************************************************
		// 2.4.2.2 bullet 6 - CREATE NEWORDER object & SET values in it &
		// output.
		// ************************************************************

		irwwbase.debugOut("<<< NewOrderBean - creating NewOrder Object with the following Key attributes: "
				+ irwwbase.getCurrentTime());
		irwwbase.debugOut("<<< 				  - WarehouseId =  " + input.getInWareHouseId());
		irwwbase.debugOut("<<<  			      - DistrictId = " + input.getInDistrictId());
		irwwbase.debugOut("<<< 				  - OrderId =  " + output.getOutOrderId());

		try {
			createNewOrderInstance(output.getOutOrderId(),
					input.getInDistrictId(), input.getInWareHouseId(), em, ut);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(">>>> Error in " + classname + "Exception creating newOrder Instance", e);
		}

		// ************************************************************
		// 2.4.2.2 bullet 9 Calculate outTotal including taxes & discounts
		// **********************************************************

		output.setOutTotal(output.getOutTotal()
				* (1 + output.getOutWarehouseTax() + output.getOutDistrictTax())
				* (1 - (output.getOutCustomerDiscount() / 100)));
		
		try{
			newOrderEvent.fire(new NewOrderEvent(output.getOutTotal()));
			irwwbase.debugOut("<<< CDI Event 1: In NewOrderBean: newOrderEvent.fire(new NewOrderEvent(output.getOutTotal())): object identity = " + this);
		}catch (Exception e){			
			System.out.println("Error in newOrderEvent.fire" + e);
			e.printStackTrace();
			throw new Exception(">>>> Error in " + classname + "Error in newOrderEvent.fire", e);
		} 
		
		return output;

	}

	protected WarehouseJPA getWarehouseInstance(short warehouseId,
			EntityManager em, UserTransaction ut) throws Exception,
			IllegalStateException, SecurityException, SystemException {

		irwwbase.debugOut("warehouseId in getWarehouseInstance " + warehouseId);
		WarehouseJPA warehouse = null;

		try {

			warehouse = em.find(WarehouseJPA.class, warehouseId,
					LockModeType.PESSIMISTIC_READ);
			if (warehouse == null)
				throw new Exception(">>>> Error in " + classname + "warehouse not found, warehouse="
						+ warehouseId + " " + irwwbase.getSTC());

		} catch (Exception t) {
			t.printStackTrace();
			ut.rollback();
			if (irwwbase.is913(t))
				throw new Exception(">>>> Error in " + classname + "<&!@" + irwwbase.checkFor913(t)
						+ ", finding Warehouse JPA, " + t.getCause()
						+ irwwbase.getSTC() + irwwbase.getStackTrace(t));
			throw new Exception(">>>> Error in " + classname + "<&!@"
					+ "failure in getWarehouseInstance: "
					+ " exception message: " + t.getMessage()
					+ ", Exception Cause: " + t.getCause() + irwwbase.getSTC()
					+ irwwbase.getStackTrace(t));
		} // end try
		return warehouse;

	}

	protected DistrictJPA getDistrictInstance(short districtId,
			short warehouseId, EntityManager em, UserTransaction ut)
			throws Exception, IllegalStateException, SecurityException,
			SystemException {

		DistrictJPA district = null;
		try {
			DistrictKey key = new DistrictKey();
			key.setDistrictId(districtId);
			key.setDistrictWareId(warehouseId);

			district = em.find(DistrictJPA.class, key,
					LockModeType.PESSIMISTIC_WRITE);

			if (district == null)
				throw new Exception(">>>> Error in " + classname + "district not found, districtId="
						+ districtId + " and warehouseId=" + warehouseId + " "
						+ irwwbase.getSTC());

		} catch (Exception t) {
			t.printStackTrace();
			ut.rollback();
			if (irwwbase.is913(t))
				throw new Exception(">>>> Error in " + classname + "<&!@" + irwwbase.checkFor913(t)
						+ ", finding District JPA, " + t.getCause()
						+ irwwbase.getSTC() + irwwbase.getStackTrace(t));
			else
				throw new Exception(">>>> Error in " + classname + "<&!@"
						+ "failure in getDistrictInstance: "
						+ " exception message: " + t.getMessage()
						+ ", Exception Cause: " + t.getCause() + irwwbase.getSTC()
						+ irwwbase.getStackTrace(t));
		} // end try
		return district;

	}

	protected ItemJPA getItemInstance(int itemNum, EntityManager em,
			UserTransaction ut) throws Exception, IllegalStateException,
			SecurityException, SystemException {

		ItemJPA item = null;
		try {

			item = em.find(ItemJPA.class, itemNum);
			if (item == null)
				throw new Exception(">>>> Error in " + classname + "item not found, item=" + item + " "
						+ irwwbase.getSTC());
				irwwbase.debugOut("inside getitemInstance" + item.getItemId()
					+ item.getItemPrice());

		} catch (Exception t) {
			t.printStackTrace();
			ut.rollback();
			if (irwwbase.is913(t))
				throw new Exception(">>>> Error in " + classname + "<&!@" + irwwbase.checkFor913(t)
						+ ", finding Item JPA, " + t.getCause() + irwwbase.getSTC()
						+ irwwbase.getStackTrace(t));
			throw new Exception(">>>> Error in " + classname + "<&!@" + "failure in getItemInstance: "
					+ " exception message: " + t.getMessage()
					+ ", Exception Cause: " + t.getCause() + irwwbase.getSTC()
					+ irwwbase.getStackTrace(t));
		} // end try
		return item;

	}

	private CustomerJPA getCustomerInstance(short customerId, short districtId,
			short warehouseId, EntityManager em, UserTransaction ut)
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
				throw new Exception(">>>> Error in " + classname + "customer not found, districtId="
						+ districtId + ", customerId=" + customerId
						+ " and warehouseId=" + warehouseId + " " + irwwbase.getSTC());

		} catch (Exception t) {
			t.printStackTrace();
			ut.rollback();
			if (irwwbase.is913(t))
				throw new Exception(">>>> Error in " + classname + "<&!@" + irwwbase.checkFor913(t)
						+ ", finding Customer JPA, " + t.getCause()
						+ irwwbase.getSTC() + irwwbase.getStackTrace(t));
			throw new Exception(">>>> Error in " + classname + "<&!@"
					+ "failure in getCustomerInstance: "
					+ " exception message: " + t.getMessage()
					+ ", Exception Cause: " + t.getCause() + irwwbase.getSTC()
					+ irwwbase.getStackTrace(t));
		} // end try
		return customer;
	}

	private void createOrderInstance(int outOrderId, short inDistrictId,
			short inWareHouseId, short inCustomerId, Timestamp orderEntryDat,
			short orderCarrierId, BigDecimal orderLineCount,
			short orderAllLocal, EntityManager em, UserTransaction ut)
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
			ut.rollback();
			if (irwwbase.is913(ex))
				throw new Exception(">>>> Error in " + classname + "<&!@" + irwwbase.checkFor913(ex)
						+ ", creating Order JPA, " + ex.getCause()
						+ irwwbase.getSTC() + irwwbase.getStackTrace(ex));
			else
				throw new Exception(">>>> Error in " + classname + "<&!@"
						+ "Error creating order instance"
						+ " exception message: " + ex.getMessage()
						+ ", Exception Cause: " + ex.getCause() + irwwbase.getSTC()
						+ irwwbase.getStackTrace(ex));
		} // end try

		irwwbase.debugOut("<<< In NewOrderServlet, OrderJPA instance created where WarehouseId = "
				+ inWareHouseId
				+ " District Id = "
				+ inDistrictId
				+ " OrderId = " + outOrderId);

		return;
	}

	public java.math.BigDecimal convertToMoney(double doubleVal) {

		java.math.BigDecimal outMoney = new java.math.BigDecimal(doubleVal)
				.setScale(2, RoundingMode.HALF_EVEN);

		return outMoney;

	}

	private void createOrderLineInstance(int outOrderId, short outDistrictId,
			short outWarehouseId, int ol_number, int outItemId,
			short outItemSupplyWarehouseId, Timestamp orderDeliveryDate,
			BigDecimal quantity, double amount, String districtInfo,
			EntityManager em, UserTransaction ut) throws Exception,
			IllegalStateException, SecurityException, SystemException {


		java.math.BigDecimal amt = this.convertToMoney(amount);

		irwwbase.debugOut("oiginal value of amt is: " + amt);
		if (amt.doubleValue() > 9999.99)
			amt = new BigDecimal(9999.00);
		amt.setScale(2, RoundingMode.DOWN);
		irwwbase.debugOut("amt=" + amt);

		irwwbase.debugOut("oiginal value of quantity is: " + quantity);
		if (quantity.intValue() > 99)
			quantity = new BigDecimal(99.00);
		quantity.setScale(0, RoundingMode.DOWN);
		irwwbase.debugOut("quantity=" + quantity);

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
			ut.rollback();
			if (irwwbase.is913(ex))
				throw new Exception(">>>> Error in " + classname + "<&!@" + irwwbase.checkFor913(ex)
						+ ", creating Orderline JPA, " + ex.getCause()
						+ irwwbase.getSTC() + irwwbase.getStackTrace(ex));
			else
				throw new Exception(">>>> Error in " + classname + "<&!@"
						+ "Error creating orderline instance"
						+ " exception message: " + ex.getMessage()
						+ ", Exception Cause: " + ex.getCause() + irwwbase.getSTC()
						+ irwwbase.getStackTrace(ex));
		}

		return;

	}

	private void createNewOrderInstance(int outOrderId, short inDistrictId,
			short inWareHouseId, EntityManager em, UserTransaction ut)
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
			ut.rollback();
			if (irwwbase.is913(ex))
				throw new Exception(">>>> Error in " + classname + "<&!@" + irwwbase.checkFor913(ex)
						+ ", creating NewOrder JPA, " + ex.getCause()
						+ irwwbase.getSTC() + irwwbase.getStackTrace(ex));
			throw new Exception(">>>> Error in " + classname + "<&!@"
					+ "Error creating neworder instance" + irwwbase.getSTC()
					+ irwwbase.getStackTrace(ex));
		} // end try

		irwwbase.debugOut("<<< In NewOrderServlet, NewOrderJPA instance created where WarehouseId = "
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

			stock = em.find(StockJPA.class, key, LockModeType.PESSIMISTIC_WRITE);

			if (stock == null)
				throw new Exception(">>>> Error in " + classname + "stock not found, item=" + outItemId
						+ " and warehouse=" + outItemSupplyWarehouseId + " "
						+ irwwbase.getSTC());

		} catch (Exception t) {
			t.printStackTrace();
			if (irwwbase.is913(t))
				throw new Exception(">>>> Error in " + classname + "<&!@" + irwwbase.checkFor913(t)
						+ ", finding Stock JPA, " + t.getCause() + irwwbase.getSTC()
						+ irwwbase.getStackTrace(t));
			else
				throw new Exception(">>>> Error in " + classname + "<&!@"
						+ "failure in getStockInstance: "
						+ " exception message: " + t.getMessage()
						+ ", Exception Cause: " + t.getCause() + irwwbase.getSTC()
						+ irwwbase.getStackTrace(t));
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
			System.out.println("Error getting district info from district JPA entity ");
			ex.printStackTrace();
			throw new Exception(
					"Error getting district info from district JPA entity"
							+ " exception message: " + ex.getMessage()
							+ ", Exception Cause: " + ex.getCause()
							+ irwwbase.getSTC() + irwwbase.getStackTrace(ex));
		} // end try

	}

	private int checkForOverflow(int val) {
		if (val >= 9998)
			val = 0;
		return val;
	}

	
	private NewOrderInputInfo generateNewOrderInput(byte db) {
		
		int numberOfItems = 0;
		
		java.lang.String[] oput;

		ExtendedRandom rand = new ExtendedRandom();

		NewOrderInputInfo input = new NewOrderInputInfo();

		input.setInWareHouseId((short) rand.nextInt(1, MaxValues
				.warehouseId(db)));
		
		input.setInDistrictId((short) rand.nextInt(1, MaxValues
						.districtId(db)));

		input.setInCustomerId((short) rand.nextInt(1, MaxValues
						.customerId(db)));

		numberOfItems = rand.nextInt(MaxValues.minOrderlinesPerOrder(db),MaxValues.orderlinesPerOrder(db));
		irwwbase.debugOut("<<< generateNewOrderInput: numberOfItems = " + numberOfItems);

		input.setNumberOfItems(numberOfItems);

		NewOrderSingleItemInInfo item[] = new NewOrderSingleItemInInfo[numberOfItems];

		for (int i = 0; i < numberOfItems; i++) {

			item[i] = new NewOrderSingleItemInInfo();
			item[i].setInItemId(rand.nextInt(1, MaxValues.itemId(db)));

			if (rand.nextBoolProb(1, 100) && MaxValues.warehouseId(db) != 1) { // remote
				// warehouse 1% of the time
				do {
					item[i].setInItemSupplyWarehouseId((short) rand.nextInt(1,
							MaxValues.warehouseId(db)));

				} while (item[i].getInItemSupplyWarehouseId() == input
						.getInWareHouseId());
			} else { // supplying warehouse = home warehouse
				item[i].setInItemSupplyWarehouseId(input.getInWareHouseId());
			}
			item[i].setInItemQuantity(rand.nextInt(1, 10));
		} // for i
		input.setInItemList(item);

		input.setInOrderEntryDate(java.util.Calendar.getInstance());

		int x = numberOfItems * 8 + 23;
		oput = new String[x];
		irwwbase.debugOut("<<<<length: " + oput.length);

		return input;
	}

	// Method to initialize the output structure of NewOrderTransaction
	NewOrderOutputInfo generateNewOrderOutput(byte db, int numberOfItems) {

		NewOrderOutputInfo output = new NewOrderOutputInfo();
		output.setOutWarehouseId((short) 0);
		output.setOutDistrictId((short) 0);
		output.setOutCustomerId((short) 0);
		output.setOutOrderId((short) 0);
		output.setOutCustomerLastName(" ");
		output.setOutCustomerCredit(" ");
		output.setOutMsg(" ");

		irwwbase.debugOut("In generateNewOrderOutput, numberOfItems = " + numberOfItems);

		NewOrderSingleItemOutInfo item[] = new NewOrderSingleItemOutInfo[numberOfItems];

		for (int i = 0; i < numberOfItems; i++) {
			item[i] = new NewOrderSingleItemOutInfo();
			item[i].setOutItemId((short) 0);
			item[i].setOutItemSupplyWarehouseId((short) 0);
			item[i].setOutItemName(" ");
		}
		output.setOutItemList(item);

		return output;
	}
	
	private NewOrderInputInfo getManualInput() {

		boolean skip = false;
		
		int actualLength = 0;

		NewOrderInputInfo input = new NewOrderInputInfo();

		NewOrderSingleItemInInfo[] itemElement = new NewOrderSingleItemInInfo[15];

		input.setInWareHouseId(Short.valueOf(this.getInWarehouseId()));

		input.setInDistrictId(Short.valueOf(this.getInDistrictId()));

		input.setInCustomerId(Short.valueOf(this.getInCustomerId()));

		input.setInOrderEntryDate(java.util.Calendar.getInstance());

		if ((this.getItemId1().trim().length() > 0)){
			itemElement[0] = new NewOrderSingleItemInInfo();
			itemElement[0].setInItemId(Integer.valueOf(this.getItemId1()));
			itemElement[0].setInItemQuantity(Integer.valueOf(this.getQuantity1()));
			itemElement[0].setInItemSupplyWarehouseId(Short.valueOf(this.getItemSupplyWarehouseId1()));
			skip = false;
		} else {
			skip = true;
		}		
		actualLength = 1;

		if (skip == false) {
			if ((this.getItemId2().trim().length() > 0)){
				itemElement[1] = new NewOrderSingleItemInInfo();
				itemElement[1].setInItemId(Integer.valueOf(this.getItemId2()));
				itemElement[1].setInItemQuantity(Integer.valueOf(this.getQuantity2()));
				itemElement[1].setInItemSupplyWarehouseId(Short.valueOf(this.getItemSupplyWarehouseId2()));
			} else {
				actualLength = 1;
				skip = true;
			}
		}

		if (skip == false) {
			if ((this.getItemId3().trim().length() > 0)){
				itemElement[2] = new NewOrderSingleItemInInfo();
				itemElement[2].setInItemId(Integer.valueOf(this.getItemId3()));
				itemElement[2].setInItemQuantity(Integer.valueOf(this.getQuantity3()));
				itemElement[2].setInItemSupplyWarehouseId(Short.valueOf(this.getItemSupplyWarehouseId3()));
			} else {
				actualLength = 2;
				skip = true;
			}
		}

		if (skip == false) {
			if ((this.getItemId4().trim().length() > 0)){
				itemElement[3] = new NewOrderSingleItemInInfo();
				itemElement[3].setInItemId(Integer.valueOf(this.getItemId4()));
				itemElement[3].setInItemQuantity(Integer.valueOf(this.getQuantity4()));
				itemElement[3].setInItemSupplyWarehouseId(Short.valueOf(this.getItemSupplyWarehouseId4()));
			} else {
				actualLength = 3;
				skip = true;
			}
		}

		if (skip == false) {
			if ((this.getItemId5().trim().length() > 0)){
				itemElement[4] = new NewOrderSingleItemInInfo();
				itemElement[4].setInItemId(Integer.valueOf(this.getItemId5()));
				itemElement[4].setInItemQuantity(Integer.valueOf(this.getQuantity5()));
				itemElement[4].setInItemSupplyWarehouseId(Short.valueOf(this.getItemSupplyWarehouseId5()));
			} else {
				actualLength = 4;
				skip = true;
			}
		}

		if (skip == false) {
			if ((this.getItemId6().trim().length() > 0)){
				itemElement[5] = new NewOrderSingleItemInInfo();
				itemElement[5].setInItemId(Integer.valueOf(this.getItemId6()));
				itemElement[5].setInItemQuantity(Integer.valueOf(this.getQuantity6()));
				itemElement[5].setInItemSupplyWarehouseId(Short.valueOf(this.getItemSupplyWarehouseId6()));
			} else {
				actualLength = 5;
				skip = true;
			}
		}

		if (skip == false) {
			if ((this.getItemId7().trim().length() > 0)){
				itemElement[6] = new NewOrderSingleItemInInfo();
				itemElement[6].setInItemId(Integer.valueOf(this.getItemId7()));
				itemElement[6].setInItemQuantity(Integer.valueOf(this.getQuantity7()));
				itemElement[6].setInItemSupplyWarehouseId(Short.valueOf(this.getItemSupplyWarehouseId7()));
			} else {
				actualLength = 6;
				skip = true;
			}
		}

		if (skip == false) {
			if ((this.getItemId8().trim().length() > 0)){
				itemElement[7] = new NewOrderSingleItemInInfo();
				itemElement[7].setInItemId(Integer.valueOf(this.getItemId8()));
				itemElement[7].setInItemQuantity(Integer.valueOf(this.getQuantity8()));
				itemElement[7].setInItemSupplyWarehouseId(Short.valueOf(this.getItemSupplyWarehouseId8()));
			} else {
				actualLength = 7;
				skip = true;
			}
		}

		if (skip == false) {
			if ((this.getItemId9().trim().length() > 0)){
				itemElement[8] = new NewOrderSingleItemInInfo();
				itemElement[8].setInItemId(Integer.valueOf(this.getItemId9()));
				itemElement[8].setInItemQuantity(Integer.valueOf(this.getQuantity9()));
				itemElement[8].setInItemSupplyWarehouseId(Short.valueOf(this.getItemSupplyWarehouseId9()));;
			} else {
				actualLength = 8;
				skip = true;
			}
		}

		if (skip == false) {
			if ((this.getItemId10().trim().length() > 0)){
				itemElement[9] = new NewOrderSingleItemInInfo();
				itemElement[9].setInItemId(Integer.valueOf(this.getItemId10()));
				itemElement[9].setInItemQuantity(Integer.valueOf(this.getQuantity10()));
				itemElement[9].setInItemSupplyWarehouseId(Short.valueOf(this.getItemSupplyWarehouseId10()));
			} else {
				actualLength = 9;
				skip = true;
			}
		}

		if (skip == false) {
			if ((this.getItemId11().trim().length() > 0)){
				itemElement[10] = new NewOrderSingleItemInInfo();
				itemElement[10].setInItemId(Integer.valueOf(this.getItemId11()));
				itemElement[10].setInItemQuantity(Integer.valueOf(this.getQuantity11()));
				itemElement[10].setInItemSupplyWarehouseId(Short.valueOf(this.getItemSupplyWarehouseId11()));
			} else {
				actualLength = 10;
				skip = true;
			}
		}

		if (skip == false) {
			if ((this.getItemId12().trim().length() > 0)){
				itemElement[11] = new NewOrderSingleItemInInfo();
				itemElement[11].setInItemId(Integer.valueOf(this.getItemId12()));
				itemElement[11].setInItemQuantity(Integer.valueOf(this.getQuantity12()));
				itemElement[11].setInItemSupplyWarehouseId(Short.valueOf(this.getItemSupplyWarehouseId12()));
			} else {
				actualLength = 11;
				skip = true;
			}
		}

		if (skip == false) {
			if ((this.getItemId13().trim().length() > 0)){
				itemElement[12] = new NewOrderSingleItemInInfo();
				itemElement[12].setInItemId(Integer.valueOf(this.getItemId13()));
				itemElement[12].setInItemQuantity(Integer.valueOf(this.getQuantity13()));
				itemElement[12].setInItemSupplyWarehouseId(Short.valueOf(this.getItemSupplyWarehouseId13()));
			} else {
				actualLength = 12;
				skip = true;
			}
		}

		if (skip == false) {
			if ((this.getItemId14().trim().length() > 0)){
				itemElement[13] = new NewOrderSingleItemInInfo();
				itemElement[13].setInItemId(Integer.valueOf(this.getItemId14()));
				itemElement[13].setInItemQuantity(Integer.valueOf(this.getQuantity14()));
				itemElement[13].setInItemSupplyWarehouseId(Short.valueOf(this.getItemSupplyWarehouseId14()));
			} else {
				actualLength = 13;
				skip = true;
			}
		}

		if (skip == false) {
			if ((this.getItemId15().trim().length() > 0)){
				itemElement[14] = new NewOrderSingleItemInInfo();
				itemElement[14].setInItemId(Integer.valueOf(this.getItemId15()));
				itemElement[14].setInItemQuantity(Integer.valueOf(this.getQuantity15()));
				itemElement[14].setInItemSupplyWarehouseId(Short.valueOf(this.getItemSupplyWarehouseId15()));
				actualLength = 15;
			} else {
				actualLength = 14;
			}
		} 

		input.setNumberOfItems(actualLength);
		input.setInItemList(itemElement);
		return input;
	}
	
	private UserTransaction getUserTrans() throws NamingException {
		
		InitialContext ctx = null;
		UserTransaction tx = null;
		
   	 	try{
   	 		ctx = new InitialContext();
   	 		tx = (UserTransaction)ctx.lookup("java:comp/UserTransaction");
   	 	} catch (Exception e) {
   	 		System.out.println(">>> Error in getUserTrans() where exception = " + e);
			e.printStackTrace();
			throw new NamingException(">>>> Error in " + classname +  ": getUserTrans(): Exception: "	+ 
					" Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause());
		} 
		return tx;
	}
	
	public void addCoupon(@Observes(during=BEFORE_COMPLETION) SpecialOfferEvent specialOfferEvent) throws Exception {
		    
		irwwbase.debugOut("<<< CDI Event 4: In NewOrderBean: addCoupon(@Observes(during=BEFORE_COMPLETION) SpecialOfferEvent specialOfferEvent): object identity = " + this);	 
		
		double [] purchasePriceValues = null;
		String [] couponValues = null;	

	    if (output!= null && specialOfferEvent!= null){
	    	 try{	    		 
	    		 purchasePriceValues = specialOfferEvent.getSpecialOfferPayload().getPurchasePriceValues();
	    		 couponValues = specialOfferEvent.getSpecialOfferPayload().getCouponValues();
	    		 
	    		 irwwbase.debugOut("<<< In "+ classname + " addCoupon observer method where purchasePriceValues = " + purchasePriceValues[0] + " " 
	    				 			+ purchasePriceValues[1] +  " " + purchasePriceValues[2] );
	    		 irwwbase.debugOut("<<< In "+ classname + " addCoupon observer method where couponValues = " + couponValues[0] + " " 
				 			+ couponValues[1] +  " " + couponValues[2] );
	    		 
	    		 if (purchasePriceValues[0] < output.getOutTotal() && output.getOutTotal() <= purchasePriceValues[1]){	    		    		 
	    			 output.setCoupon(specialOfferEvent.getSpecialOfferPayload().couponValues[0]);   			 
	    		 } else if (purchasePriceValues[1] < output.getOutTotal() && output.getOutTotal() <= purchasePriceValues[2]){	
	    			 output.setCoupon(specialOfferEvent.getSpecialOfferPayload().couponValues[1]);  
	    		 } else if (purchasePriceValues[2] < output.getOutTotal()){
	    			 output.setCoupon(specialOfferEvent.getSpecialOfferPayload().couponValues[2]); 
	    		 }
	    		 
	    	 } catch (Exception e) {
	    		System.out.println(">>> Error in output.setCoupon(specialOfferEvent.getCoupon()) where exception = " + e);
	 			e.printStackTrace();
				throw new Exception(">>>> Error in " + classname +  ": addCoupon(): Exception calling output.setCoupon(specialOfferEvent.getCoupon()): "	+ 
						" Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause());
			} 
	    }else{
	    	 System.out.println(">>> Error in output.setCoupon(specialOfferEvent.getCoupon())");
	    	 throw new Exception(">>>> Error in " + classname +  ": addCoupon(): either output or specialOfferEvent is null");
	    }
	} 
	
}
