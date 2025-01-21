package consistency.check.lite;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity public class Check1 {
	
	int W_YTD;

	@Id
	@Column(name = "W_ID")
	private short warehouseId;
	
	public Check1() {
	}

	public void setW_YTD(int value) {
		this.W_YTD = value;
	}

	public int getW_YTD() {
		return W_YTD;
	}
	
	public void setID(short value) {
		this.warehouseId = value;
	}

	public long getID() {
		return warehouseId;
	}
}
