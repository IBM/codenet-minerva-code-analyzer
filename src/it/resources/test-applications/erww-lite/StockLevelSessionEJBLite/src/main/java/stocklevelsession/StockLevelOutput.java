package stocklevelsession;

/**
 * Insert the type's description here.
 * Creation date: (21/08/00 10:53:35)
 * @author: Administrator
 */
public class StockLevelOutput implements java.io.Serializable {
	private long lowStock;
	private final static long serialVersionUID = 1155361310772091476L;
	private java.lang.String stcnum;
/**
 * StockLevelOutput constructor comment.
 */
public StockLevelOutput() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (21/08/00 11:12:19)
 * @return long
 */
public long getLowStock() {
	return lowStock;
}
/**
 * Insert the method's description here.
 * Creation date: (4/29/2002 4:56:55 PM)
 * @return java.lang.String
 */
public java.lang.String getStcnum() {
	return stcnum;
}
/**
 * Insert the method's description here.
 * Creation date: (21/08/00 11:12:19)
 * @param newLowStock long
 */
public void setLowStock(long newLowStock) {
	lowStock = newLowStock;
}
/**
 * Insert the method's description here.
 * Creation date: (4/29/2002 4:56:55 PM)
 * @param newStcnum java.lang.String
 */
public void setStcnum(java.lang.String newStcnum) {
	stcnum = newStcnum;
}
}
