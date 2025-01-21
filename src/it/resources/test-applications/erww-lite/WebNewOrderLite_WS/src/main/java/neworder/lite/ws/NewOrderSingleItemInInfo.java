package neworder.lite.ws;

/**
 * Insert the type's description here.
 * Creation date: (09/11/2000 5:16:20 PM)
 * @author: Administrator
 */
public class NewOrderSingleItemInInfo implements java.io.Serializable {
	private int inItemId;
	private short inItemSupplyWarehouseId;
	private int inItemQuantity;
	private final static long serialVersionUID = 1777711544618328126L;
/**
 * NewOrderSingleItemInfo constructor comment.
 */
public NewOrderSingleItemInInfo() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:16:47 PM)
 * @return java.lang.String
 */
public int getInItemId() {
	return inItemId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:17:51 PM)
 * @return double
 */
public int getInItemQuantity() {
	return inItemQuantity;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:17:27 PM)
 * @return java.lang.String
 */
public short getInItemSupplyWarehouseId() {
	return inItemSupplyWarehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:16:47 PM)
 * @param newInItemId java.lang.String
 */
public void setInItemId(int newInItemId) {
	inItemId = newInItemId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:17:51 PM)
 * @param newInItemQuantity double
 */
public void setInItemQuantity(int newInItemQuantity) {
	inItemQuantity = newInItemQuantity;
}
/**
 * Insert the method's description here.
 * Creation date: (09/11/2000 5:17:27 PM)
 * @param newInItemSupplyWarehouseId java.lang.String
 */
public void setInItemSupplyWarehouseId(short newInItemSupplyWarehouseId) {
	inItemSupplyWarehouseId = newInItemSupplyWarehouseId;
}
}
