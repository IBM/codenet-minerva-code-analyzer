
package pay.lite;

/**
 * Insert the type's description here.
 * Creation date: (8/28/00 6:47:59 PM)
 * @author: Administrator
 */
public class PayInput implements  java.io.Serializable
 {
	private short warehouseId;
	private short districtId;
	private short customerId;
	private short customerDistrictId;
	private short customerWarehouseId;
	private java.lang.String customerLastName;
	private java.math.BigDecimal historyAmount;
	private java.lang.String historyDate;
	private boolean byLastName;
	private java.lang.String clientIdentifier;
	private boolean requiredRollback; // MD11544 Rollback
	private final static long serialVersionUID = 8306303136386303491L;
	private short entityType;
	private boolean useCmp;
	private boolean distributed;	
	private int db2LockSharing;
/**
 * PayInput constructor comment.
 */
public PayInput() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:44:42 AM)
 * @return java.lang.Boolean
 */
public boolean getByLastName() {
	return byLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:46:30 AM)
 * @return java.lang.String
 */
public java.lang.String getClientIdentifier() {
	return clientIdentifier;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:36:31 AM)
 * @return java.lang.String
 */
public short getCustomerDistrictId() {
	return customerDistrictId;
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/00 8:31:47 AM)
 * @return java.lang.String
 */
public short getCustomerId() {
	return customerId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:40:54 AM)
 * @return java.lang.String
 */
public java.lang.String getCustomerLastName() {
	return customerLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:37:22 AM)
 * @return java.lang.String
 */
public short getCustomerWarehouseId() {
	return customerWarehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:35:07 AM)
 * @return java.lang.String
 */
public short getDistrictId() {
	return districtId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:43:17 AM)
 * @return double
 */
public java.math.BigDecimal getHistoryAmount() {
	return historyAmount;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:43:59 AM)
 * @return java.lang.String
 */
public java.lang.String getHistoryDate() {
	return historyDate;
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2001 2:58:49 PM)
 * @return boolean
 */
public boolean getRequiredRollback() {
	return requiredRollback;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:34:28 AM)
 * @return java.lang.String
 */
public short getWarehouseId() {
	return warehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (11/28/2001 3:32:30 PM)
 * @return boolean
 */
public boolean isUseCmp() {
	return useCmp;
}

public boolean isByLastName() {
	return byLastName;
}

public boolean isRequiredRollback() {
	return requiredRollback;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:44:42 AM)
 * @param newByLastName java.lang.Boolean
 */
public void setByLastName(boolean newByLastName) {
	byLastName = newByLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:46:30 AM)
 * @param newClientIdentifier java.lang.String
 */
public void setClientIdentifier(java.lang.String newClientIdentifier) {
	clientIdentifier = newClientIdentifier;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:36:31 AM)
 * @param newCustomerDistrictId java.lang.String
 */
public void setCustomerDistrictId(short newCustomerDistrictId) {
	customerDistrictId = newCustomerDistrictId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:35:46 AM)
 * @param newCustomerId java.lang.String
 */
public void setCustomerId(short newCustomerId) {
	customerId = newCustomerId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:40:54 AM)
 * @param newCustomerLastName java.lang.String
 */
public void setCustomerLastName(java.lang.String newCustomerLastName) {
	customerLastName = newCustomerLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:37:22 AM)
 * @param newCustomerWarehouseId java.lang.String
 */
public void setCustomerWarehouseId(short newCustomerWarehouseId) {
	customerWarehouseId = newCustomerWarehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:35:07 AM)
 * @param newDistrictId java.lang.String
 */
public void setDistrictId(short newDistrictId) {
	districtId = newDistrictId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:43:17 AM)
 * @param newHistoryAmount double
 */
public void setHistoryAmount(java.math.BigDecimal newHistoryAmount) {
	historyAmount = newHistoryAmount;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:43:59 AM)
 * @param newHistoryDate java.lang.String
 */
public void setHistoryDate(java.lang.String newHistoryDate) {
	historyDate = newHistoryDate;
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2001 2:57:42 PM)
 * @param newRequiredRollback boolean
 */
public void setRequiredRollback(boolean newRequiredRollback) {
	requiredRollback = newRequiredRollback ;
	}
/**
 * Insert the method's description here.
 * Creation date: (11/28/2001 3:32:30 PM)
 * @param newUseCmp boolean
 */
public void setUseCmp(boolean newUseCmp) {
	useCmp = newUseCmp;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 7:34:28 AM)
 * @param newWarehouseId java.lang.String
 */
public void setWarehouseId(short newWarehouseId) {
	warehouseId = newWarehouseId;
}
	/**
	 * @return
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**
	 * @param l
	 */
	//public static void setSerialVersionUID(long l) {
	//	serialVersionUID = l;
	//}

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
	 * @return
	 */
	public boolean isDistributed() {
		return distributed;
	}

	/**
	 * @param b
	 */
	public void setDistributed(boolean b) {
		distributed = b;
	}
	
	/**
	 * @return the db2LockSharing
	 */
	public int getDb2LockSharing() {
		return db2LockSharing;
	}
	
	/**
	 * @param db2LockSharing the db2LockSharing to set
	 */
	public void setDb2LockSharing(int db2LockSharing) {
		this.db2LockSharing = db2LockSharing;
	}	
}
