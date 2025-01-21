package deliverysession;

/**
 * Insert the type's description here.
 * Creation date: (10/11/00 7:20:11 PM)
 * @author: Administrator
 */
public class DeliveryInputInfo {
	public short inWarehouseId;
	public short inCarrierId;
/**
 * DeliveryInputInfo constructor comment.
 */
public DeliveryInputInfo() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (10/11/00 7:21:47 PM)
 * @return java.lang.String
 */
public short getInCarrierId() {
	return inCarrierId;
}
/**
 * Insert the method's description here.
 * Creation date: (10/11/00 7:21:02 PM)
 * @return java.lang.String
 */
public short getInWarehouseId() {
	return inWarehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (10/11/00 7:21:47 PM)
 * @param newInCarrierId java.lang.String
 */
public void setInCarrierId(short newInCarrierId) {
	inCarrierId = newInCarrierId;
}
/**
 * Insert the method's description here.
 * Creation date: (10/11/00 7:21:02 PM)
 * @param newInWarehouseId java.lang.String
 */
public void setInWarehouseId(short newInWarehouseId) {
	inWarehouseId = newInWarehouseId;
}
}
