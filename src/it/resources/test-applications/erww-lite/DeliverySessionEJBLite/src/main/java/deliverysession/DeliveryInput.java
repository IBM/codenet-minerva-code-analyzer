package deliverysession;

/**
 * Insert the type's description here.
 * Creation date: (9/15/00 3:37:40 PM)
 * @author: Administrator
 */
public class DeliveryInput implements java.io.Serializable {
	private short warehouseId;
	private int runtimeMode; 
	private short carrierId;
	private final static long serialVersionUID = -7543365077731739375L;
	private boolean useCmp;
	private short entityType;
	private int maxdistrictId;
	private boolean distributedEJBs;
	
	// shupert - no longer used.  Asynch methods Future.get timeout is now an ERWW system property
	//private int asyncTimeout;
	
	private boolean use31Async;
	
	// shupert - For "Bad Application" testing.  Skip the call to get the results from
	//           asynchronous deliveries (on purpose).
	private boolean leakDeliveryResults;


	
	/**
	 * DeliveryInput constructor comment.
	 */
	public DeliveryInput() {
		super();
		//set default 
		this.use31Async = false;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/20/00 2:22:15 PM)
	 * @return java.lang.String
	 */
	public short getCarrierId() {
		return carrierId;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/20/00 2:20:24 PM)
	 * @return java.lang.String
	 */
	public short getWarehouseId() {
		return warehouseId;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/26/2001 3:56:35 PM)
	 * @return boolean
	 */
	public boolean isUseCmp() {
		return useCmp;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/20/00 2:22:15 PM)
	 * @param newCarrierId java.lang.String
	 */
	public void setCarrierId(short newCarrierId) {
		carrierId = newCarrierId;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/26/2001 3:56:35 PM)
	 * @param newUseCmp boolean
	 */
	public void setUseCmp(boolean newUseCmp) {
		useCmp = newUseCmp;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/20/00 2:20:24 PM)
	 * @param newWarehouseId java.lang.String
	 */
	public void setWarehouseId(short newWarehouseId) {
		warehouseId = newWarehouseId;
	}
	/**
	 * Returns the maxdistrictId.
	 * @return int
	 */
	public int getMaxdistrictId() {
		return maxdistrictId;
	}

	/**
	 * Sets the maxdistrictId.
	 * @param maxdistrictId The maxdistrictId to set
	 */
	public void setMaxdistrictId(int maxdistrictId) {
		this.maxdistrictId = maxdistrictId;
	}

	public void setRuntimeMode(int runtimeMode) {
		this.runtimeMode = runtimeMode;
	}

	public int getRuntimeMode() {
		return runtimeMode;
	}

	// shupert - no longer used.  Asynch methods Future.get timeout is now an ERWW system property
	/**
	 * @return
	 */
	//public int getAsyncTimeout() {
	//	return asyncTimeout;
	//}

	// shupert - no longer used.  Asynch methods Future.get timeout is now an ERWW system property
	/**
	 * @param timeout
	 */
	//public void setAsyncTimeout(int timeout) {
	//	asyncTimeout = timeout;
	//}

	/**
	 * @return
	 */
	public boolean isDistributedEJBs() {
		return distributedEJBs;
	}

	/**
	 * @return
	 */
	public boolean isUse31Async() {
		return use31Async;
	}
	
	/**
	 * @return
	 */
	public boolean isLeakDeliveryResults() {
		return leakDeliveryResults;
	}

	/**
	 * @param b
	 */
	public void setDistributedEJBs(boolean b) {
		distributedEJBs = b;
	}

	/**
	 * @param b
	 */
	public void setUse31Async(boolean b) {
		use31Async = b;
	}
	
	/**
	 * @param b
	 */
	public void setLeakDeliveryResults(boolean b) {
		leakDeliveryResults = b;
	}


	/**
	 * @return Returns the entityType.
	 */
	public short getEntityType() {
		return entityType;
	}
	/**
	 * @param entityType The entityType to set.
	 */
	public void setEntityType(short entityType) {
		this.entityType = entityType;
	}
}
