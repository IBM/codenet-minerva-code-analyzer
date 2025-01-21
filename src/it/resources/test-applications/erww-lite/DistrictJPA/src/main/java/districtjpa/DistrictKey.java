package districtjpa;
/**
 * Key class for Entity Bean: DistrictCMPEntity
 */
public class DistrictKey implements java.io.Serializable {
	static final long serialVersionUID = 3206093459760846163L;
	/**
	 * Implementation field for persistent attribute: districtId
	 */
	public short districtId;
	/**
	 * Implementation field for persistent attribute: districtWareId
	 */
	public short districtWareId;
	/**
	 * Creates an empty key for Entity Bean: DistrictCMPEntity
	 */
	public DistrictKey() {
	}
	/**
	 * Creates a key for Entity Bean: DistrictCMPEntity
	 */
	public DistrictKey(short districtId, short districtWareId) {
		this.districtId = districtId;
		this.districtWareId = districtWareId;
	}
	/**
	 * Returns true if both keys are equal.
	 */
	public boolean equals(java.lang.Object otherKey) {
		if (otherKey instanceof districtjpa.DistrictKey) {
			districtjpa.DistrictKey o =
				(districtjpa.DistrictKey) otherKey;
			return (
				(this.districtId == o.districtId)
					&& (this.districtWareId == o.districtWareId));
		}
		return false;
	}
	/**
	 * Returns the hash code for the key.
	 */
	public int hashCode() {
		return (
			(java.lang.Short.valueOf(districtId).hashCode())
				+ (java.lang.Short.valueOf(districtWareId).hashCode()));
	}
	/**
	 * Get accessor for persistent attribute: districtId
	 */
	public short getDistrictId() {
		return districtId;
	}
	/**
	 * Set accessor for persistent attribute: districtId
	 */
	public void setDistrictId(short newDistrictId) {
		districtId = newDistrictId;
	}
	/**
	 * Get accessor for persistent attribute: districtWareId
	 */
	public short getDistrictWareId() {
		return districtWareId;
	}
	/**
	 * Set accessor for persistent attribute: districtWareId
	 */
	public void setDistrictWareId(short newDistrictWareId) {
		districtWareId = newDistrictWareId;
	}
}
