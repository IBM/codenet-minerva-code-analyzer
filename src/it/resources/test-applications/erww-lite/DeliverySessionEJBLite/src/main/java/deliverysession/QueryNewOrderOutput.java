package deliverysession;

/**
 * Insert the type's description here.
 * Creation date: (9/20/00 5:16:35 PM)
 * @author: Administrator
 */
public class QueryNewOrderOutput implements java.io.Serializable {
	public java.lang.Boolean skipped;
	public int orderId;
	private final static long serialVersionUID = -425680728437614883L;
/**
 * QueryNewOrderOutput constructor comment.
 */
public QueryNewOrderOutput() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/00 5:20:03 PM)
 * @return java.lang.String
 */
public int getOrderId() {
	return orderId;
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/00 5:19:25 PM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getSkipped() {
	return skipped;
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/00 5:20:03 PM)
 * @param newOrderId java.lang.String
 */
public void setOrderId(int newOrderId) {
	orderId = newOrderId;
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/00 5:19:25 PM)
 * @param newSkipped java.lang.Boolean
 */
public void setSkipped(java.lang.Boolean newSkipped) {
	skipped = newSkipped;
}
}
