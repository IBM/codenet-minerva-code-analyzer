package stocklevelsession;

/**
 * Insert the type's description here.
 * Creation date: (21/08/00 10:47:40)
 * @author: Administrator
 */
public class StockLevelInput implements java.io.Serializable {
	private short warehouseId;
	private short districtId;
	private long threshold;
	private final static long serialVersionUID = 1176084359011720968L;
	private boolean useCmp;
	private boolean distributed;
/**
 * StockLevelInput constructor comment.
 */
public StockLevelInput() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (21/08/00 10:50:25)
 * @return java.lang.String
 */
public short getDistrictId() {
	return districtId;
}
/**
 * Insert the method's description here.
 * Creation date: (21/08/00 10:51:58)
 * @return long
 */
public long getThreshold() {
	return threshold;
}
/**
 * Insert the method's description here.
 * Creation date: (21/08/00 10:49:18)
 * @return java.lang.String
 */
public short getWarehouseId() {
	return warehouseId;
}
/**
 * Insert the method's description here.
 * Creation date: (11/28/2001 4:54:20 PM)
 * @return boolean
 */
public boolean isUseCmp() {
	return useCmp;
}
/**
 * Insert the method's description here.
 * Creation date: (21/08/00 10:50:25)
 * @param newDistrictId java.lang.String
 */
public void setDistrictId(short newDistrictId) {
	districtId = newDistrictId;
}
/**
 * Insert the method's description here.
 * Creation date: (21/08/00 10:51:58)
 * @param newThreshold long
 */
public void setThreshold(long newThreshold) {
	threshold = newThreshold;
}
/**
 * Insert the method's description here.
 * Creation date: (11/28/2001 4:54:20 PM)
 * @param newUseCmp boolean
 */
public void setUseCmp(boolean newUseCmp) {
	useCmp = newUseCmp;
}
/**
 * Insert the method's description here.
 * Creation date: (21/08/00 10:49:18)
 * @param newWarehouseId java.lang.String
 */
public void setWarehouseId(short newWarehouseId) {
	warehouseId = newWarehouseId;
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

}
