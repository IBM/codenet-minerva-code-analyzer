package orderstatus.lite;

/**
 * Insert the type's description here.
 * Creation date: (09/10/00 14:29:54)
 * @author: Administrator
 */
public class OrderStatusInput implements java.io.Serializable {
	public short inCustomerWarehouseId = 0;
	public short inCustomerDistrictId = 0;
	public short inCustomerId = 0;
	public java.lang.String inCustomerLastName = null;
	public boolean inByLastName;
	private final static long serialVersionUID = -702265289437737156L;
	private boolean useCmp;
	private boolean distributedEJBs;
/**
 * OrderStatusInput constructor comment.
 */
public OrderStatusInput() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 14:31:57)
 * @return java.lang.String
 */
public short getInCustomerDistrictId() {
	return inCustomerDistrictId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 14:32:35)
 * @return java.lang.String
 */
public short getInCustomerId() {
	return inCustomerId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 14:32:58)
 * @return java.lang.String
 */
public java.lang.String getInCustomerLastName() {
	return inCustomerLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 14:31:14)
 * @return java.lang.String
 */
public short getInCustomerWarehouseId() {
	return inCustomerWarehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 14:33:38)
 * @return boolean
 */
public boolean isInByLastName() {
	return inByLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/29/2001 10:54:50 AM)
 * @return boolean
 */
public boolean isUseCmp() {
	return useCmp;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 14:33:38)
 * @param newInByLastName boolean
 */
public void setInByLastName(boolean newInByLastName) {
	inByLastName = newInByLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 14:31:57)
 * @param newInCustomerDistrictId java.lang.String
 */
public void setInCustomerDistrictId(short newInCustomerDistrictId) {
	inCustomerDistrictId = newInCustomerDistrictId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 14:32:35)
 * @param newInCustomerId java.lang.String
 */
public void setInCustomerId(short newInCustomerId) {
	inCustomerId = newInCustomerId;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 14:32:58)
 * @param newInCustomerLastName java.lang.String
 */
public void setInCustomerLastName(java.lang.String newInCustomerLastName) {
	inCustomerLastName = newInCustomerLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (09/10/00 14:31:14)
 * @param newInCustomerWarehouseId java.lang.String
 */
public void setInCustomerWarehouseId(short newInCustomerWarehouseId) {
	inCustomerWarehouseId = newInCustomerWarehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (11/29/2001 10:54:50 AM)
 * @param newUseCmp boolean
 */
public void setUseCmp(boolean newUseCmp) {
	useCmp = newUseCmp;
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
