/**
 * PriceQuoteInputInfo.java
 *
 * This file was auto-generated from WSDL
 * by the IBM WebServices WSDL2Java emitter.
 * 3
 */

package pricequote.lite;

//import irwwbase.*;

public class PriceQuoteInputInfo implements java.io.Serializable {
	/**
	 * Autogenerated UID
	 */
	private static final long serialVersionUID = -3583343970165742312L;
	private pricequote.lite.PriceQuoteSingleItemInInfo[] inItemList;
	private short itemListLength;
	private short listLength;
	private boolean useCmp;
	private boolean distributed;
	private short entityType;

	public PriceQuoteInputInfo() {
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

	public PriceQuoteSingleItemInInfo[] getInItemList() {
		return inItemList;
	}

	public void setInItemList(PriceQuoteSingleItemInInfo[] inItemList) {
		this.inItemList = inItemList;
	}

	///////////////////////////////////////////////////////////
	// following method generate by WebServices Tooling.
	///////////////////////////////////////////////////////////
	//public short getItemListLength() {
	//    return itemListLength;
	//}

	//////////////////////////////////////////////////////////
	public short getItemListLength() {
		short len = listLength;
		if (listLength == 0) {
			if (inItemList != null) {
				len = (short) inItemList.length;
			} else {
				len = 15;
			}
		}
		return len;
	}
	//////////////////////////////////////////////////////////
	public void setItemListLength(short itemListLength) {
		this.itemListLength = itemListLength;
	}

	public short getListLength() {
		return listLength;
	}

	public void setListLength(short listLength) {
		this.listLength = listLength;
	}

	public boolean isUseCmp() {
		return useCmp;
	}

	public void setUseCmp(boolean useCmp) {
		this.useCmp = useCmp;
	}

	private java.lang.Object __equalsCalc = null;
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof PriceQuoteInputInfo))
			return false;
		PriceQuoteInputInfo other = (PriceQuoteInputInfo) obj;
		/*Comment out dead code
		if (obj == null)
			return false;
		*/
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals =
			true
				&& ((inItemList == null && other.getInItemList() == null)
					|| (inItemList != null && java.util.Arrays.equals(inItemList, other.getInItemList())))
				&& itemListLength == other.getItemListLength()
				&& listLength == other.getListLength()
				&& useCmp == other.isUseCmp();
		__equalsCalc = null;
		return _equals;
	}

	private boolean __hashCodeCalc = false;
	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = 1;
		if (getInItemList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getInItemList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getInItemList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		_hashCode += getItemListLength();
		_hashCode += getListLength();
		_hashCode += Boolean.valueOf(isUseCmp()).hashCode();
		__hashCodeCalc = false;
		return _hashCode;
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

