package consistency.check.lite;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Basic;

@Entity public class ConCheck2Entity {
	
	@Id
	@Column(name = "W_ID")
	private short warehouseId;
	
	@Basic
	@Column(name = "W_YTD")
	private java.math.BigDecimal warehouseYtd;
	
	private int SUM_D_YTD;

	
	public ConCheck2Entity() {
	}

	public short getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(short warehouseId) {
		this.warehouseId = warehouseId;
	}
	
	public java.math.BigDecimal getWarehouseYtd() {
		return warehouseYtd;
	}

	public void setWarehouseYtd(java.math.BigDecimal warehouseYtd) {
		this.warehouseYtd = warehouseYtd;
	}
	
	
	public void setSUM_D_YTD(int value) {
		this.SUM_D_YTD = value;
	}


	public int getSUM_D_YTD() {
		return SUM_D_YTD;
	}
	
	
}
