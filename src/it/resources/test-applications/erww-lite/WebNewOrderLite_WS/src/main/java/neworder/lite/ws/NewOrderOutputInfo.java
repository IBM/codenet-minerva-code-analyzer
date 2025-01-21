package neworder.lite.ws;

/**
 * Insert the type's description here.
 * Creation date: (09/11/2000 5:19:43 PM)
 * @author: Administrator
 */
public class NewOrderOutputInfo implements java.io.Serializable {
	private short outWarehouseId;
	private short outDistrictId;
	private short outCustomerId;
	private int outOrderId;
	private int outOrderItemCount;
	private java.lang.String outCustomerLastName;
	private java.lang.String outCustomerCredit;
	private double outCustomerDiscount;
	private double outWarehouseTax;
	private double outDistrictTax;
	private double outTotal;
	private java.lang.String outMsg;
	//private java.sql.Timestamp outOrderEntryDate;
	private java.util.Calendar outOrderEntryDate;
	private NewOrderSingleItemOutInfo[] outItemList;
	private final static long serialVersionUID = -2850033766875973445L;
	private java.lang.String stcnum;
	private short entityType;
/**
 * NewOrderSingleItemOutInfo constructor comment.
 */
public NewOrderOutputInfo() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:43:33 PM)
 * @return java.lang.String
 */
public java.lang.String getOutCustomerCredit() {
	return outCustomerCredit;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:44:52 PM)
 * @return double
 */
public double getOutCustomerDiscount() {
	return outCustomerDiscount;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:22:14 PM)
 * @return java.lang.String
 */
public short getOutCustomerId() {
	return outCustomerId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:42:30 PM)
 * @return java.lang.String
 */
public java.lang.String getOutCustomerLastName() {
	return outCustomerLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:21:38 PM)
 * @return java.lang.String
 */
public short getOutDistrictId() {
	return outDistrictId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:45:42 PM)
 * @return double
 */
public double getOutDistrictTax() {
	return outDistrictTax;
}
/**
 * Insert the method's description here.
 * Creation date: (09/15/2000 4:24:49 PM)
 * @return newordersessionpackage.NewOrderSingleItemOutInfo[]
 */
public NewOrderSingleItemOutInfo[] getOutItemList() {
	return outItemList;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:48:37 PM)
 * @return java.lang.String
 */
public java.lang.String getOutMsg() {
	return outMsg;
}
/**
 * Insert the method's description here.
 * Creation date: (09/13/2000 4:54:45 PM)
 * @return java.lang.String
 */
public java.util.Calendar getOutOrderEntryDate() {
	return outOrderEntryDate;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:41:30 PM)
 * @return java.lang.String
 */
public int getOutOrderId() {
	return outOrderId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:41:58 PM)
 * @return double
 */
public int getOutOrderItemCount() {
	return outOrderItemCount;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:46:48 PM)
 * @return double
 */
public double getOutTotal() {
	return outTotal;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:20:52 PM)
 * @return java.lang.String
 */
public short getOutWarehouseId() {
	return outWarehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:45:19 PM)
 * @return double
 */
public double getOutWarehouseTax() {
	return outWarehouseTax;
}
/**
 * Insert the method's description here.
 * Creation date: (4/29/2002 4:52:11 PM)
 * @return java.lang.String
 */
public java.lang.String getStcnum() {
	return stcnum;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:43:33 PM)
 * @param newOutCustomerCredit java.lang.String
 */
public void setOutCustomerCredit(java.lang.String newOutCustomerCredit) {
	outCustomerCredit = newOutCustomerCredit;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:44:52 PM)
 * @param newOutCustomerDiscount double
 */
public void setOutCustomerDiscount(double newOutCustomerDiscount) {
	outCustomerDiscount = newOutCustomerDiscount;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:22:14 PM)
 * @param newOutCustomerId java.lang.String
 */
public void setOutCustomerId(short newOutCustomerId) {
	outCustomerId = newOutCustomerId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:42:30 PM)
 * @param newOutCustomerLastName java.lang.String
 */
public void setOutCustomerLastName(java.lang.String newOutCustomerLastName) {
	outCustomerLastName = newOutCustomerLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:21:38 PM)
 * @param newOutDistrictId java.lang.String
 */
public void setOutDistrictId(short newOutDistrictId) {
	outDistrictId = newOutDistrictId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:45:42 PM)
 * @param newOutDistrictTax double
 */
public void setOutDistrictTax(double newOutDistrictTax) {
	outDistrictTax = newOutDistrictTax;
}
/**
 * Insert the method's description here.
 * Creation date: (09/15/2000 4:24:49 PM)
 * @param newOutItemList newordersessionpackage.NewOrderSingleItemOutInfo[]
 */
public void setOutItemList(NewOrderSingleItemOutInfo[] newOutItemList) {
	outItemList = newOutItemList;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:48:37 PM)
 * @param newOutMsg java.lang.String
 */
public void setOutMsg(java.lang.String newOutMsg) {
	outMsg = newOutMsg;
}
/**
 * Insert the method's description here.
 * Creation date: (09/13/2000 4:54:45 PM)
 * @param newOutOrderEntryDate java.lang.String
 */
public void setOutOrderEntryDate(java.util.Calendar newOutOrderEntryDate) {
	outOrderEntryDate = newOutOrderEntryDate;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:41:30 PM)
 * @param newOutOrderId java.lang.String
 */
public void setOutOrderId(int newOutOrderId) {
	outOrderId = newOutOrderId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:41:58 PM)
 * @param newOutOrderItemCount double
 */
public void setOutOrderItemCount(int newOutOrderItemCount) {
	outOrderItemCount = newOutOrderItemCount;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:46:48 PM)
 * @param newOutTotal double
 */
public void setOutTotal(double newOutTotal) {
	outTotal = newOutTotal;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:20:52 PM)
 * @param newOutWarehouseId java.lang.String
 */
public void setOutWarehouseId(short newOutWarehouseId) {
	outWarehouseId = newOutWarehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:45:19 PM)
 * @param newOutWarehouseTax double
 */
public void setOutWarehouseTax(double newOutWarehouseTax) {
	outWarehouseTax = newOutWarehouseTax;
}
/**
 * Insert the method's description here.
 * Creation date: (4/29/2002 4:52:11 PM)
 * @param newStcnum java.lang.String
 */
public void setStcnum(java.lang.String newStcnum) {
	stcnum = newStcnum;
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

}
