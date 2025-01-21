package warehousejpa;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Basic;
import jakarta.persistence.Version;

@Entity
@Table(name = "WAREHOUSE")

public class WarehouseJPA implements Serializable {

	private static final long serialVersionUID= 2716055690746597905L;
		
	@Version 
	protected Integer VERSION;
	
	@Id
	@Column(name = "W_ID")
	private short warehouseId;

	@Column(name = "W_NAME")
	private java.lang.String warehouseName;

	@Column(name = "W_STREET_1")
	private java.lang.String warehouseStreet1;

	@Column(name = "W_STREET_2")
	private java.lang.String warehouseStreet2;

	@Column(name = "W_CITY")
	private java.lang.String warehouseCity;

	@Column(name = "W_ZIP")
	private java.lang.String warehouseZip;

	@Column(name = "W_STATE")
	private java.lang.String warehouseState;

	@Basic
	@Column(name = "W_TAX")
	private java.math.BigDecimal warehouseTax;

	@Basic
	@Column(name = "W_YTD")
	private java.math.BigDecimal warehouseYtd;

	public java.lang.String getWarehouseCity() {
		return warehouseCity;
	}

	public void setWarehouseCity(java.lang.String warehouseCity) {
		this.warehouseCity = warehouseCity;
	}

	public short getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(short warehouseId) {
		this.warehouseId = warehouseId;
	}

	public java.lang.String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(java.lang.String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public java.lang.String getWarehouseState() {
		return warehouseState;
	}

	public void setWarehouseState(java.lang.String warehouseState) {
		this.warehouseState = warehouseState;
	}

	public java.lang.String getWarehouseStreet1() {
		return warehouseStreet1;
	}

	public void setWarehouseStreet1(java.lang.String warehouseStreet1) {
		this.warehouseStreet1 = warehouseStreet1;
	}

	public java.lang.String getWarehouseStreet2() {
		return warehouseStreet2;
	}

	public void setWarehouseStreet2(java.lang.String warehouseStreet2) {
		this.warehouseStreet2 = warehouseStreet2;
	}

	public java.math.BigDecimal getWarehouseTax() {
		return warehouseTax;
	}

	public void setWarehouseTax(java.math.BigDecimal warehouseTax) {
		this.warehouseTax = warehouseTax;
	}

	public java.math.BigDecimal getWarehouseYtd() {
		return warehouseYtd;
	}

	public void setWarehouseYtd(java.math.BigDecimal warehouseYtd) {
		this.warehouseYtd = warehouseYtd;
	}

	public java.lang.String getWarehouseZip() {
		return warehouseZip;
	}

	public void setWarehouseZip(java.lang.String warehouseZip) {
		this.warehouseZip = warehouseZip;
	}

	
}
