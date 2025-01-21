package pricequote.lite;

/**
 * Insert the type's description here.
 * Creation date: (8/18/00 2:07:35 PM)
 * @author: Administrator
 */
public class PriceQuoteInput implements java.io.Serializable {
	private short itemId;
	private final static long serialVersionUID = 6978357631616444064L;
	
/**
 * PriceQuoteInput constructor comment.
 */
public PriceQuoteInput() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (8/18/00 2:08:02 PM)
 * @return java.lang.String
 */
public short getItemId() {
	return itemId;
}
/**
 * Insert the method's description here.
 * Creation date: (8/18/00 2:08:02 PM)
 * @param newItemId java.lang.String
 */
public void setItemId(short newItemId) {
	itemId = newItemId;
}
	
}

