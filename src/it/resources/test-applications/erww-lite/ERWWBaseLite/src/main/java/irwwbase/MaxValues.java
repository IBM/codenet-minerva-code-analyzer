package irwwbase;

/**
 * Insert the type's description here.
 * Creation date: (8/23/00 1:51:15 PM)
 * @author: Administrator
 */
public class MaxValues {

	//Original values for MaxValues
	
    public final static byte itemIdLength = 6;
	public final static byte warehouseIdLength = 4;
	public final static byte districtIdLength = 2;
	public final static byte customerIdLength = 4; 
	public final static byte carrierIdLength = 2;
	private final static int[] _itemId = { 100000, 100000, 100000 };
	private final static int[] _itemPrice = { 1000, 1000, 1000 };
	private final static int[] _itemQuantity = { 100, 100, 100 };
	private final static int[] _warehouseId = { 1, 25, 75 };
	private final static int[] _districtId = { 10, 10, 10 };
	private final static int[] _customerId = { 3000, 3000, 3000 };
	//Comment out unused variable
	//private final static int[] _customerName = { 999, 999, 999 };
	private final static int[] _stock = { 100, 100, 100 };
	private final static int[] _carrierId = { 99, 99, 99 };
	private final static int[] _minOlinesPerOrder = { 5, 5, 5 };
	private final static int[] _olinesPerOrder = { 15, 15, 15 };
	private final static int[] _stockLevelLowThreshold = { 10, 10, 10 };
	private final static int[] _stockLevelUpThreshold = { 20, 20, 20 };

//Modified values for use with GVT Testing.  Uncomment the block above and compile with code below to gain access to GVT data 
//values in the database
/**	public final static byte itemIdLength = 6;
	public final static byte warehouseIdLength = 4;
	public final static byte districtIdLength = 2;
	public final static byte customerIdLength = 4; 
	public final static byte carrierIdLength = 2;
	private final static int[] _itemId = { 47, 100000, 100000 };
	private final static int[] _itemPrice = { 1000, 1000, 1000 };
	private final static int[] _itemQuantity = { 100, 100, 100 };
	private final static int[] _warehouseId = { 46, 25, 75 };
	private final static int[] _districtId = { 3, 10, 10 };
	private final static int[] _customerId = { 38, 3000, 3000 };
	private final static int[] _customerName = { 9, 999, 999 };
	private final static int[] _stock = { 100, 100, 100 };
	private final static int[] _carrierId = { 99, 99, 99 };
	private final static int[] _minOlinesPerOrder = { 5, 5, 5 };
	private final static int[] _olinesPerOrder = { 15, 15, 15 };
	private final static int[] _stockLevelLowThreshold = { 10, 10, 10 };
	private final static int[] _stockLevelUpThreshold = { 20, 20, 20 };
	*
	
/**
 * MaxValues constructor comment.
 */
public MaxValues() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/00 4:28:29 PM)
 * @return int
 * @param db byte
 */
public static int carrierId( byte db ) {
   return _carrierId[ db ];
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/00 4:28:29 PM)
 * @return int
 * @param db byte
 */
public static int customerId( byte db ) {
   return _customerId[ db ];
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/00 4:28:29 PM)
 * @return int
 * @param db byte
 */
public static int districtId( byte db ) {
   return _districtId[ db ];
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/00 4:28:29 PM)
 * @return int
 * @param db byte
 */
public static int itemId(byte db) {
	
	return _itemId[ db ];
	
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/00 4:28:29 PM)
 * @return int
 * @param db byte
 */
public static int itemPrice( byte db ) {
   return _itemPrice[ db ];
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/00 4:28:29 PM)
 * @return int
 * @param db byte
 */
public static int itemQuantity( byte db ) {
   return _itemQuantity[ db ];
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/00 4:28:29 PM)
 * @return int
 * @param db byte
 */
public static int minOrderlinesPerOrder( byte db ) {
   return _minOlinesPerOrder[ db ];
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/00 4:28:29 PM)
 * @return int
 * @param db byte
 */
public static int orderlinesPerOrder( byte db ) {
   return _olinesPerOrder[ db ];
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/00 4:28:29 PM)
 * @return int
 * @param db byte
 */
public static int stock( byte db ) {
   return _stock[ db ];
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/00 4:28:29 PM)
 * @return int
 * @param db byte
 */
public static int stockLevelLowerThreshold( byte db ) {
   return _stockLevelLowThreshold[ db ];
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/00 4:28:29 PM)
 * @return int
 * @param db byte
 */
public static int stockLevelUpperThreshold( byte db ) {
   return _stockLevelUpThreshold[ db ];
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/00 4:28:29 PM)
 * @return int
 * @param db byte
 */
public static int warehouseId( byte db ) {
   return _warehouseId[ db ];
}
}