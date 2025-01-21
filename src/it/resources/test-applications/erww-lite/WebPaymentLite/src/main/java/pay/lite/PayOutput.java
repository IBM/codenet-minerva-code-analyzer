package pay.lite;

/**
 * Insert the type's description here.
 * Creation date: (8/29/00 7:49:44 AM)
 * @author: Administrator
 */
public class PayOutput implements  java.io.Serializable
 {
	private short customerId;
	private java.lang.String warehouseStreet1;
	private java.lang.String warehouseStreet2;
	private java.lang.String warehouseCity;
	private java.lang.String warehouseState;
	private java.lang.String warehouseZip;
	private java.lang.String districtStreet1;
	private java.lang.String districtStreet2;
	private java.lang.String districtCity;
	private java.lang.String districtState;
	private java.lang.String districtZip;
	private java.lang.String customerFirstName;
	private java.lang.String customerMiddleName;
	private java.lang.String customerLastName;
	private java.lang.String customerStreet1;
	private java.lang.String customerStreet2;
	private java.lang.String customerCity;
	private java.lang.String customerState;
	private java.lang.String customerZip;
	private java.lang.String customerPhone;
	private java.lang.String customerSince;
	private java.lang.String customerCredit;
	private java.math.BigDecimal customerCreditLimit;
	private java.math.BigDecimal customerDiscount;
	private java.math.BigDecimal customerBalance;
	private java.lang.String customerData;
	private java.lang.String historyDate;
	private java.lang.String msg;
	private short warehouseId;
	private short districtId;
	private short customerWarehouseId;
	private short customerDistrictId;
	private final static long serialVersionUID = -4993641693275374653L;
	private java.lang.String stcnum;
	private short entityType;
	private java.math.BigDecimal warehouseYtdBeforeUpdates;	
	private java.math.BigDecimal warehouseYtdAfterUpdates;
	private java.math.BigDecimal warehouseYtdDifference;
/**
 * PaymentOutput constructor comment.
 */
public PayOutput() {
	super();
}

/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:32:45 AM)
 * @return java.lang.String
 */
public java.lang.String getCustomerCity() {
	return customerCity;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:34:33 AM)
 * @return java.lang.String
 */
public java.lang.String getCustomerCredit() {
	return customerCredit;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:35:17 AM)
 * @return double
 */
public java.math.BigDecimal getCustomerCreditLimit() {
	return customerCreditLimit;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:36:42 AM)
 * @return java.lang.String
 */
public java.lang.String getCustomerData() {
	return customerData;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:35:40 AM)
 * @return double
 */
public java.math.BigDecimal getCustomerDiscount() {
	return customerDiscount;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:39:43 AM)
 * @return java.lang.String
 */
public short getCustomerDistrictId() {
	return customerDistrictId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:29:41 AM)
 * @return java.lang.String
 */
public java.lang.String getCustomerFirstName() {
	return customerFirstName;
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/00 8:34:48 AM)
 * @return java.lang.String
 */
public short getCustomerId() {
	return customerId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:31:35 AM)
 * @return java.lang.String
 */
public java.lang.String getCustomerLastName() {
	return customerLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:30:09 AM)
 * @return java.lang.String
 */
public java.lang.String getCustomerMiddleName() {
	return customerMiddleName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:33:51 AM)
 * @return java.lang.String
 */
public java.lang.String getCustomerPhone() {
	return customerPhone;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:34:15 AM)
 * @return java.lang.String
 */
public String getCustomerSince() {
	return customerSince;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:33:09 AM)
 * @return java.lang.String
 */
public java.lang.String getCustomerState() {
	return customerState;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:31:58 AM)
 * @return java.lang.String
 */
public java.lang.String getCustomerStreet1() {
	return customerStreet1;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:32:16 AM)
 * @return java.lang.String
 */
public java.lang.String getCustomerStreet2() {
	return customerStreet2;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:38:59 AM)
 * @return java.lang.String
 */
public short getCustomerWarehouseId() {
	return customerWarehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:33:30 AM)
 * @return java.lang.String
 */
public java.lang.String getCustomerZip() {
	return customerZip;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:28:11 AM)
 * @return java.lang.String
 */
public java.lang.String getDistrictCity() {
	return districtCity;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:38:27 AM)
 * @return java.lang.String
 */
public short getDistrictId() {
	return districtId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:28:32 AM)
 * @return java.lang.String
 */
public java.lang.String getDistrictState() {
	return districtState;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:27:13 AM)
 * @return java.lang.String
 */
public java.lang.String getDistrictStreet1() {
	return districtStreet1;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:27:33 AM)
 * @return java.lang.String
 */
public java.lang.String getDistrictStreet2() {
	return districtStreet2;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:28:55 AM)
 * @return java.lang.String
 */
public java.lang.String getDistrictZip() {
	return districtZip;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:37:45 AM)
 * @return java.lang.String
 */
public java.lang.String getMsg() {
	return msg;
}

/**
 * Insert the method's description here.
 * Creation date: (4/29/2002 4:54:06 PM)
 * @return java.lang.String
 */
public java.lang.String getStcnum() {
	return stcnum;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:23:52 AM)
 * @return java.lang.String
 */
public java.lang.String getWarehouseCity() {
	return warehouseCity;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:38:06 AM)
 * @return java.lang.String
 */
public short getWarehouseId() {
	return warehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:24:18 AM)
 * @return java.lang.String
 */
public java.lang.String getWarehouseState() {
	return warehouseState;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:52:01 AM)
 * @return java.lang.String
 */
public java.lang.String getWarehouseStreet1() {
	return warehouseStreet1;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:52:18 AM)
 * @return java.lang.String
 */
public java.lang.String getWarehouseStreet2() {
	return warehouseStreet2;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:26:48 AM)
 * @return java.lang.String
 */
public java.lang.String getWarehouseZip() {
	return warehouseZip;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:36:05 AM)
 * @param newCustomerBalance double
 */
public void setCustomerBalance(java.math.BigDecimal newCustomerBalance) {
	customerBalance = newCustomerBalance;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:32:45 AM)
 * @param newCustomerCity java.lang.String
 */
public void setCustomerCity(java.lang.String newCustomerCity) {
	customerCity = newCustomerCity;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:34:33 AM)
 * @param newCustomerCredit java.lang.String
 */
public void setCustomerCredit(java.lang.String newCustomerCredit) {
	customerCredit = newCustomerCredit;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:35:17 AM)
 * @param newCustomerCreditLimit double
 */
public void setCustomerCreditLimit(java.math.BigDecimal newCustomerCreditLimit) {
	customerCreditLimit = newCustomerCreditLimit;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:36:42 AM)
 * @param newCustomerDate java.lang.String
 */
public void setCustomerData(java.lang.String newCustomerData) {
	customerData = newCustomerData;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:35:40 AM)
 * @param newCustomerDiscount double
 */
public void setCustomerDiscount(java.math.BigDecimal newCustomerDiscount) {
	customerDiscount = newCustomerDiscount;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:39:43 AM)
 * @param newCustomerDistrictId java.lang.String
 */
public void setCustomerDistrictId(short newCustomerDistrictId) {
	customerDistrictId = newCustomerDistrictId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:29:41 AM)
 * @param newCustomerFirstName java.lang.String
 */
public void setCustomerFirstName(java.lang.String newCustomerFirstName) {
	customerFirstName = newCustomerFirstName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:51:33 AM)
 * @param newCustomerId java.lang.String
 */
public void setCustomerId(short newCustomerId) {
	customerId = newCustomerId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:31:35 AM)
 * @param newCustomerLastName java.lang.String
 */
public void setCustomerLastName(java.lang.String newCustomerLastName) {
	customerLastName = newCustomerLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:30:09 AM)
 * @param newCustomerMiddleName java.lang.String
 */
public void setCustomerMiddleName(java.lang.String newCustomerMiddleName) {
	customerMiddleName = newCustomerMiddleName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:33:51 AM)
 * @param newCustomerPhone java.lang.String
 */
public void setCustomerPhone(java.lang.String newCustomerPhone) {
	customerPhone = newCustomerPhone;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:34:15 AM)
 * @param newCustomerSince java.lang.String
 */
public void setCustomerSince(String newCustomerSince) {
	customerSince = newCustomerSince;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:33:09 AM)
 * @param newCustomerState java.lang.String
 */
public void setCustomerState(java.lang.String newCustomerState) {
	customerState = newCustomerState;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:31:58 AM)
 * @param newCustomerStreet1 java.lang.String
 */
public void setCustomerStreet1(java.lang.String newCustomerStreet1) {
	customerStreet1 = newCustomerStreet1;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:32:16 AM)
 * @param newCustomerStreet2 java.lang.String
 */
public void setCustomerStreet2(java.lang.String newCustomerStreet2) {
	customerStreet2 = newCustomerStreet2;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:38:59 AM)
 * @param newCustomerWarehouseId java.lang.String
 */
public void setCustomerWarehouseId(short newCustomerWarehouseId) {
	customerWarehouseId = newCustomerWarehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:33:30 AM)
 * @param newCustomerZip java.lang.String
 */
public void setCustomerZip(java.lang.String newCustomerZip) {
	customerZip = newCustomerZip;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:28:11 AM)
 * @param newDistrictCity java.lang.String
 */
public void setDistrictCity(java.lang.String newDistrictCity) {
	districtCity = newDistrictCity;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:38:27 AM)
 * @param newDistrictId java.lang.String
 */
public void setDistrictId(short newDistrictId) {
	districtId = newDistrictId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:28:32 AM)
 * @param newDistrictState java.lang.String
 */
public void setDistrictState(java.lang.String newDistrictState) {
	districtState = newDistrictState;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:27:13 AM)
 * @param newDistrictStreet1 java.lang.String
 */
public void setDistrictStreet1(java.lang.String newDistrictStreet1) {
	districtStreet1 = newDistrictStreet1;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:27:33 AM)
 * @param newDistrictStreet2 java.lang.String
 */
public void setDistrictStreet2(java.lang.String newDistrictStreet2) {
	districtStreet2 = newDistrictStreet2;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:28:55 AM)
 * @param newDistrictZip java.lang.String
 */
public void setDistrictZip(java.lang.String newDistrictZip) {
	districtZip = newDistrictZip;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:37:24 AM)
 * @param newHistoryDate java.lang.String
 */
public void setHistoryDate(java.lang.String newHistoryDate) {
	historyDate = newHistoryDate;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:37:45 AM)
 * @param newMsg java.lang.String
 */
public void setMsg(java.lang.String newMsg) {
	msg = newMsg;
}
/**
 * Insert the method's description here.
 * Creation date: (4/29/2002 4:54:06 PM)
 * @param newStcnum java.lang.String
 */
public void setStcnum(java.lang.String newStcnum) {
	stcnum = newStcnum;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:23:52 AM)
 * @param newWarehouseCity java.lang.String
 */
public void setWarehouseCity(java.lang.String newWarehouseCity) {
	warehouseCity = newWarehouseCity;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:38:06 AM)
 * @param newWarehouseId java.lang.String
 */
public void setWarehouseId(short newWarehouseId) {
	warehouseId = newWarehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:24:18 AM)
 * @param newWarehouseState java.lang.String
 */
public void setWarehouseState(java.lang.String newWarehouseState) {
	warehouseState = newWarehouseState;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:52:01 AM)
 * @param newWarehouseStreet1 java.lang.String
 */
public void setWarehouseStreet1(java.lang.String newWarehouseStreet1) {
	warehouseStreet1 = newWarehouseStreet1;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:52:18 AM)
 * @param newWarehouseStreet2 java.lang.String
 */
public void setWarehouseStreet2(java.lang.String newWarehouseStreet2) {
	warehouseStreet2 = newWarehouseStreet2;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 8:26:48 AM)
 * @param newWarehouseZip java.lang.String
 */
public void setWarehouseZip(java.lang.String newWarehouseZip) {
	warehouseZip = newWarehouseZip;
}
	/**
	 * @return
	 */
	public java.math.BigDecimal getCustomerBalance() {
		return customerBalance;
	}

	/**
	 * @return
	 */
	public java.lang.String getHistoryDate() {
		return historyDate;
	}

	/**
	 * @return
	 */
	public short getEntityType() {
		return entityType;
	}

	/**
	 * @param s
	 */
	public void setEntityType(short s) {
		entityType = s;
	}

	/**
	 * @return the warehouseYtdBeforeUpdates
	 */
	public java.math.BigDecimal getWarehouseYtdBeforeUpdates() {
		return warehouseYtdBeforeUpdates;
	}

	/**
	 * @param warehouseYtdBeforeUpdates the warehouseYtdBeforeUpdates to set
	 */
	public void setWarehouseYtdBeforeUpdates(
			java.math.BigDecimal warehouseYtdBeforeUpdates) {
		this.warehouseYtdBeforeUpdates = warehouseYtdBeforeUpdates;
	}

	/**
	 * @return the warehouseYtdAfterUpdates
	 */
	public java.math.BigDecimal getWarehouseYtdAfterUpdates() {
		return warehouseYtdAfterUpdates;
	}

	/**
	 * @param warehouseYtdAfterUpdates the warehouseYtdAfterUpdates to set
	 */
	public void setWarehouseYtdAfterUpdates(
			java.math.BigDecimal warehouseYtdAfterUpdates) {
		this.warehouseYtdAfterUpdates = warehouseYtdAfterUpdates;
	}

	/**
	 * @return the warehouseYtdDifference
	 */
	public java.math.BigDecimal getWarehouseYtdDifference() {
		return warehouseYtdDifference;
	}

	/**
	 * @param warehouseYtdDifference the warehouseYtdDifference to set
	 */
	public void setWarehouseYtdDifference(
			java.math.BigDecimal warehouseYtdDifference) {
		this.warehouseYtdDifference = warehouseYtdDifference;
	}


}
