package consistency.check.lite;

import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.IdClass;
import jakarta.persistence.Version;

import neworderjpa.NewOrderKey;



@Entity
@IdClass(NewOrderKey.class)
public class ConCheck3Entity implements Serializable {

	private static final long serialVersionUID = 8565789092340913255L;
	
	@Version 
	protected Integer VERSION;

	@Id
	@Column(name="NO_O_ID")
	private int orderId;
	
	
	@Id
	@Column(name="NO_D_ID")
	private short districtId;
	
	
	@Column(name="NO_W_ID")
	@Id
	private short warehouseId;
	
	private short COMPUTED_ROWS;
	
	private short ACTUAL_ROWS;
	
	
	public short getActualRows() {
		return ACTUAL_ROWS;
	}
	public void setActualRows(short ActualRows) {
		this.ACTUAL_ROWS = ActualRows;
	}
	
	public short getComputedRows() {
		return COMPUTED_ROWS;
	}
	public void setComputedRows(short ComputedRows) {
		this.COMPUTED_ROWS = ComputedRows;
	}
	
	public short getDistrictId() {
		return districtId;
	}
	public void setDistrictId(short districtId) {
		this.districtId = districtId;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public short getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(short warehouseId) {
		this.warehouseId = warehouseId;
	}
	
	
}
