package orderstatussession.ejb;

/**
 * Insert the type's description here.
 * Creation date: (09/10/00 14:30:25)
 * @author: Administrator
 */
public class OrderStatusOutput implements java.io.Serializable {
	public short outCustomerWarehouseId=0;
	public short outCustomerDistrictId = 0;
	public short outCustomerId = 0;
	public java.lang.String outCustomerFirstName = null;
	public java.lang.String outCustomerMiddleName = null;
	public java.lang.String outCustomerLastName = null;
	public double outCustomerBalance = 0;
	public int outOrderId = 0;
	public java.lang.String outOrderEntryDate = null;
	public short outOrderCarrierId = 0;
	public short outOrderLineCount = 0;
	public java.lang.String outErrorMsg = null;
	public OrderLineSingleItem[] outOrderLines = null;
	private final static long serialVersionUID = 3717603783007674663L;
	public java.lang.String retc = null;
	public java.lang.String stcnum;
/**
 * OrderStatusOutput constructor comment.
 */
public OrderStatusOutput() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:05:15)
 * @return double
 */
public double getOutCustomerBalance() {
	return outCustomerBalance;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:00:18)
 * @return java.lang.String
 */
public short getOutCustomerDistrictId() {
	return outCustomerDistrictId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:00:59)
 * @return java.lang.String
 */
public java.lang.String getOutCustomerFirstName() {
	return outCustomerFirstName;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:00:37)
 * @return java.lang.String
 */
public short getOutCustomerId() {
	return outCustomerId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:01:48)
 * @return java.lang.String
 */
public java.lang.String getOutCustomerLastName() {
	return outCustomerLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:01:24)
 * @return java.lang.String
 */
public java.lang.String getOutCustomerMiddleName() {
	return outCustomerMiddleName;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 14:34:22)
 * @return java.lang.String
 */
public short getOutCustomerWarehouseId() {
	return outCustomerWarehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:07:40)
 * @return java.lang.String
 */
public java.lang.String getOutErrorMsg() {
	return outErrorMsg;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:06:35)
 * @return java.lang.String
 */
public short getOutOrderCarrierId() {
	return outOrderCarrierId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:06:03)
 * @return java.lang.String
 */
public java.lang.String getOutOrderEntryDate() {
	return outOrderEntryDate;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:05:35)
 * @return java.lang.String
 */
public int getOutOrderId() {
	return outOrderId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:07:07)
 * @return double
 */
public short getOutOrderLineCount() {
	return outOrderLineCount;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:12:05)
 * @return orderStatusSessionPackage.OrderLineSingleItem[]
 */
public OrderLineSingleItem[] getOutOrderLines() {
	return outOrderLines;
}
/**
 * Insert the method's description here.
 * Creation date: (11/20/2013 1:53:00 PM)
 * @return java.lang.String
 */
public java.lang.String getRetc() {
	return retc;
}
/**
 * Insert the method's description here.
 * Creation date: (4/29/2002 4:53:12 PM)
 * @return java.lang.String
 */
public java.lang.String getStcnum() {
	return stcnum;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:05:15)
 * @param newOutCustomerBalance double
 */
public void setOutCustomerBalance(double newOutCustomerBalance) {
	outCustomerBalance = newOutCustomerBalance;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:00:18)
 * @param newOutCustomerDistrictId java.lang.String
 */
public void setOutCustomerDistrictId(short newOutCustomerDistrictId) {
	outCustomerDistrictId = newOutCustomerDistrictId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:00:59)
 * @param newOutCustomerFirstName java.lang.String
 */
public void setOutCustomerFirstName(java.lang.String newOutCustomerFirstName) {
	outCustomerFirstName = newOutCustomerFirstName;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:00:37)
 * @param newOutCustomerId java.lang.String
 */
public void setOutCustomerId(short newOutCustomerId) {
	outCustomerId = newOutCustomerId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:01:48)
 * @param newOutCustomerLastName java.lang.String
 */
public void setOutCustomerLastName(java.lang.String newOutCustomerLastName) {
	outCustomerLastName = newOutCustomerLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:01:24)
 * @param newOutCustomerMiddleName java.lang.String
 */
public void setOutCustomerMiddleName(java.lang.String newOutCustomerMiddleName) {
	outCustomerMiddleName = newOutCustomerMiddleName;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 14:34:22)
 * @param newOutCustomerWarehouseId java.lang.String
 */
public void setOutCustomerWarehouseId(short newOutCustomerWarehouseId) {
	outCustomerWarehouseId = newOutCustomerWarehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:07:40)
 * @param newOutErrorMsg java.lang.String
 */
public void setOutErrorMsg(java.lang.String newOutErrorMsg) {
	outErrorMsg = newOutErrorMsg;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:06:35)
 * @param newOutOrderCarrierId java.lang.String
 */
public void setOutOrderCarrierId(short newOutOrderCarrierId) {
	outOrderCarrierId = newOutOrderCarrierId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:06:03)
 * @param newOutOrderEntryDate java.lang.String
 */
public void setOutOrderEntryDate(java.lang.String newOutOrderEntryDate) {
	outOrderEntryDate = newOutOrderEntryDate;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:05:35)
 * @param newOutOrderId java.lang.String
 */
public void setOutOrderId(int newOutOrderId) {
	outOrderId = newOutOrderId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:07:07)
 * @param newOutOrderLineCount double
 */
public void setOutOrderLineCount(short newOutOrderLineCount) {
	outOrderLineCount = newOutOrderLineCount;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:12:05)
 * @param newOutOrderLines orderStatusSessionPackage.OrderLineSingleItem[]
 */
public void setOutOrderLines(OrderLineSingleItem[] newOutOrderLines) {
	outOrderLines = newOutOrderLines;
}
/**
 * Insert the method's description here.
 * Creation date: (11/20/2013 1:53:00 PM)
 * @return java.lang.String
 */
public void setRetc(java.lang.String newRetc) {
	retc = newRetc;
}
/**
 * Insert the method's description here.
 * Creation date: (4/29/2002 4:53:12 PM)
 * @param newStcnum java.lang.String
 */
public void setStcnum(java.lang.String newStcnum) {
	stcnum = newStcnum;
}
}
