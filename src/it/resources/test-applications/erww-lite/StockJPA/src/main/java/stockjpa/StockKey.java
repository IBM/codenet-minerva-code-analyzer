package stockjpa;

/**
 * Key class for Entity Bean: StockCMPEntity
 */
public class StockKey implements java.io.Serializable {
	static final long serialVersionUID = 3206093459760846163L;
	/**
	 * Implementation field for persistent attribute: S_W_ID
	 */
	public short S_W_ID;
	/**
	 * Implementation field for persistent attribute: S_I_ID
	 */
	public int S_I_ID;
	/**
	 * Creates an empty key for Entity Bean: StockCMPEntity
	 */
	public StockKey() {
	}
	/**
	 * Creates a key for Entity Bean: StockCMPEntity
	 */
	public StockKey(short S_W_ID, int S_I_ID) {
		this.S_W_ID = S_W_ID;
		this.S_I_ID = S_I_ID;
	}
	/**
	 * Returns true if both keys are equal.
	 */
	public boolean equals(java.lang.Object otherKey) {
		if (otherKey instanceof stockjpa.StockKey) {
			stockjpa.StockKey o =
				(stockjpa.StockKey) otherKey;
			return ((this.S_W_ID == o.S_W_ID) && (this.S_I_ID == o.S_I_ID));
		}
		return false;
	}
	/**
	 * Returns the hash code for the key.
	 */
	public int hashCode() {
		return (
			(java.lang.Short.valueOf(S_W_ID).hashCode())
				+ (java.lang.Integer.valueOf(S_I_ID).hashCode()));
	}
	/**
	 * Get accessor for persistent attribute: S_W_ID
	 */
	public short getS_W_ID() {
		return S_W_ID;
	}
	/**
	 * Set accessor for persistent attribute: S_W_ID
	 */
	public void setS_W_ID(short newS_W_ID) {
		S_W_ID = newS_W_ID;
	}
	/**
	 * Get accessor for persistent attribute: S_I_ID
	 */
	public int getS_I_ID() {
		return S_I_ID;
	}
	/**
	 * Set accessor for persistent attribute: S_I_ID
	 */
	public void setS_I_ID(int newS_I_ID) {
		S_I_ID = newS_I_ID;
	}
}
