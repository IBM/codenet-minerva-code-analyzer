package orderstatussession.ejb;

/**
 * Insert the type's description here.
 * Creation date: (09/10/00 15:08:58)
 * @author: Administrator
 */
public class OrderLineSingleItem implements java.io.Serializable {
	public short outOlSupplyWarehouseId = 0;
	public int outOlItemId = 0;
	public double outOlQuantity = 0;
	public double outOlAmount = 0;
	public java.lang.String outOlDeliveryDate = null;
	private final static long serialVersionUID = 7529256653789513313L;
/**
 * OrderLineSingleItem constructor comment.
 */
public OrderLineSingleItem() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:10:55)
 * @return double
 */
public double getOutOlAmount() {
	return outOlAmount;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:11:18)
 * @return java.lang.String
 */
public java.lang.String getOutOlDeliveryDate() {
	return outOlDeliveryDate;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:10:10)
 * @return java.lang.String
 */
public int getOutOlItemId() {
	return outOlItemId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:10:33)
 * @return double
 */
public double getOutOlQuantity() {
	return outOlQuantity;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:09:44)
 * @return java.lang.String
 */
public short getOutOlSupplyWarehouseId() {
	return outOlSupplyWarehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:10:55)
 * @param newOutOlAmount double
 */
public void setOutOlAmount(double newOutOlAmount) {
	outOlAmount = newOutOlAmount;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:11:18)
 * @param newOutOlDeliveryDate java.lang.String
 */
public void setOutOlDeliveryDate(java.lang.String newOutOlDeliveryDate) {
	outOlDeliveryDate = newOutOlDeliveryDate;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:10:10)
 * @param newOutOlItemId java.lang.String
 */
public void setOutOlItemId(int newOutOlItemId) {
	outOlItemId = newOutOlItemId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:10:33)
 * @param newOutOlQuantity double
 */
public void setOutOlQuantity(double newOutOlQuantity) {
	outOlQuantity = newOutOlQuantity;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 15:09:44)
 * @param newOutOlSupplyWarehouseId java.lang.String
 */
public void setOutOlSupplyWarehouseId(short newOutOlSupplyWarehouseId) {
	outOlSupplyWarehouseId = newOutOlSupplyWarehouseId;
}
}
