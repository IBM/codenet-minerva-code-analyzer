package newordersession;

/**
 * Insert the type's description here.
 * Creation date: (09/11/2000 6:04:45 PM)
 * @author: Administrator
 */
public class NewOrderSingleItemOutInfo implements java.io.Serializable {
	private int outItemId;
	private short outItemSupplyWarehouseId;
	private java.lang.String outItemName;
	private int outItemQuantity;
	private int outStockQuantity;
	private java.lang.String outBrandGeneric;
	private double outItemPrice;
	private double outItemTotal;
	private final static long serialVersionUID = 6925458998367787473L;
/**
 * NewOrderSingleItemOutInfo constructor comment.
 */
public NewOrderSingleItemOutInfo() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:07:46 PM)
 * @return java.lang.String
 */
public java.lang.String getOutBrandGeneric() {
	return outBrandGeneric;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:05:23 PM)
 * @return java.lang.String
 */
public int getOutItemId() {
	return outItemId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:06:25 PM)
 * @return java.lang.String
 */
public java.lang.String getOutItemName() {
	return outItemName;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:08:06 PM)
 * @return double
 */
public double getOutItemPrice() {
	return outItemPrice;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:07:05 PM)
 * @return double
 */
public int getOutItemQuantity() {
	return outItemQuantity;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:06:01 PM)
 * @return java.lang.String
 */
public short getOutItemSupplyWarehouseId() {
	return outItemSupplyWarehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:08:28 PM)
 * @return double
 */
public double getOutItemTotal() {
	return outItemTotal;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:07:26 PM)
 * @return double
 */
public int getOutStockQuantity() {
	return outStockQuantity;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:07:46 PM)
 * @param newOutBrandGeneric java.lang.String
 */
public void setOutBrandGeneric(java.lang.String newOutBrandGeneric) {
	outBrandGeneric = newOutBrandGeneric;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:05:23 PM)
 * @param newOutItemId java.lang.String
 */
public void setOutItemId(int newOutItemId) {
	outItemId = newOutItemId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:06:25 PM)
 * @param newOutItemName java.lang.String
 */
public void setOutItemName(java.lang.String newOutItemName) {
	outItemName = newOutItemName;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:08:06 PM)
 * @param newOutItemPrice double
 */
public void setOutItemPrice(double newOutItemPrice) {
	outItemPrice = newOutItemPrice;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:07:05 PM)
 * @param newOutItemQuantity double
 */
public void setOutItemQuantity(int newOutItemQuantity) {
	outItemQuantity = newOutItemQuantity;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:06:01 PM)
 * @param newOutItemSupplyWarehouseId java.lang.String
 */
public void setOutItemSupplyWarehouseId(short newOutItemSupplyWarehouseId) {
	outItemSupplyWarehouseId = newOutItemSupplyWarehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:08:28 PM)
 * @param newOutItemTotal double
 */
public void setOutItemTotal(double newOutItemTotal) {
	outItemTotal = newOutItemTotal;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 6:07:26 PM)
 * @param newOutStockQuantity double
 */
public void setOutStockQuantity(int newOutStockQuantity) {
	outStockQuantity = newOutStockQuantity;
}
}
