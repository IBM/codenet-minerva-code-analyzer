package neworder.lite.ws;

/**
 * Insert the type's description here.
 * Creation date: (09/11/2000 6:10:10 PM)
 * @author: Administrator
 */
public class NewOrderInputInfo implements java.io.Serializable {
	private short inWareHouseId;
	private short inDistrictId;
	private short inCustomerId;
	private short entityType;
	private java.util.Calendar inOrderEntryDate;
	private neworder.lite.ws.NewOrderSingleItemInInfo[] inItemList;
	private final static long serialVersionUID = -1256687585198556728L;
	private boolean distributedEJBs;
	private boolean originatorIsJavaClient = false;
	private int numberOfItems;
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:10:53 PM)
 * @return java.lang.String
 */
public short getInCustomerId() {
	return inCustomerId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:10:41 PM)
 * @return java.lang.String
 */
public short getInDistrictId() {
	return inDistrictId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/12/2000 12:20:18 PM)
 * @return newordersessionpackage.NewOrderSingleItemInInfo[]
 */
public NewOrderSingleItemInInfo[] getInItemList() {
	return inItemList;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:11:18 PM)
 * @return java.lang.String
 */
public java.util.Calendar getInOrderEntryDate() {
	return inOrderEntryDate;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:10:27 PM)
 * @return java.lang.String
 */
public short getInWareHouseId() {
	return inWareHouseId;
}

/**
 * Insert the method's description here.
 * Creation date: (11/29/2001 12:17:50 PM)
 * @return boolean
 */
public boolean isOriginatorIsJavaClient() {
	return originatorIsJavaClient;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:10:53 PM)
 * @param newInCustomerId java.lang.String
 */
public void setInCustomerId(short newInCustomerId) {
	inCustomerId = newInCustomerId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:10:41 PM)
 * @param newInDistrictId java.lang.String
 */
public void setInDistrictId(short newInDistrictId) {
	inDistrictId = newInDistrictId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/12/2000 12:20:18 PM)
 * @param newInItemList newordersessionpackage.NewOrderSingleItemInInfo[]
 */
public void setInItemList(NewOrderSingleItemInInfo[] newInItemList) {
	inItemList = newInItemList;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:11:18 PM)
 * @param newInOrderEntryDate java.lang.String
 */
public void setInOrderEntryDate(java.util.Calendar newInOrderEntryDate) {
	inOrderEntryDate = newInOrderEntryDate;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:10:27 PM)
 * @param newInWareHouseId java.lang.String
 */
public void setInWareHouseId(short newInWareHouseId) {
	inWareHouseId = newInWareHouseId;
}

	/**
	 * Returns the numberOfItems.
	 * @return int
	 */
	public int getNumberOfItems() {int len=numberOfItems;
		if (numberOfItems==0) len=inItemList.length;
		return len;
	}

	/**
	 * Sets the numberOfItems.
	 * @param numberOfItems The numberOfItems to set
	 */
	public void setNumberOfItems(int numberOfItems) {
		this.numberOfItems = numberOfItems;
	}

	/**
	 * Returns the entityType.
	 * @return short
	 */
	public short getEntityType() {
		return entityType;
	}

	/**
	 * Sets the entityType.
	 * @param entityType The entityType to set
	 */
	public void setEntityType(short entityType) {
		this.entityType = entityType;
	}

	/**
	 * @return
	 */
	public boolean originatorIsJavaClient() {
		return originatorIsJavaClient;
	}

	/**
	 * @param b
	 */
	public void setOriginatorIsJavaClient(boolean b) {
		originatorIsJavaClient = b;
	}

	/**
	 * @return
	 */
	public boolean isDistributedEJBs() {
		return distributedEJBs;
	}

	/**
	 * @param b
	 */
	public void setDistributedEJBs(boolean b) {
		distributedEJBs = b;
	}

}
