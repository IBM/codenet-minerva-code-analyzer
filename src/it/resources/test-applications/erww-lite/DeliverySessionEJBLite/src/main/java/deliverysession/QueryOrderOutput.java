package deliverysession;

import orderjpa.OrderJPA;

/**
 * Insert the type's description here.
 * Creation date: (9/21/00 4:25:13 PM)
 * @author: Administrator
 */
public class QueryOrderOutput implements java.io.Serializable {
	public OrderJPA order;
	public short customerId;
	private final static long serialVersionUID = -7050459068950404214L;
/**
 * QueryOrderOutput constructor comment.
 */
public QueryOrderOutput() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/00 4:27:13 PM)
 * @return java.lang.String
 */
public short getCustomerId() {
	return customerId;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/00 4:26:09 PM)
 * @return orderEntityPackage.OrderEntity
 */
public OrderJPA getOrderEntity() {
	return order;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/00 4:27:13 PM)
 * @param newCustomerId java.lang.String
 */
public void setCustomerId(short newCustomerId) {
	customerId = newCustomerId;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/00 4:26:09 PM)
 * @param newOrderEntity orderEntityPackage.OrderEntity
 */
public void setOrderEntity(OrderJPA order1) {
	order = order1;
}
}
