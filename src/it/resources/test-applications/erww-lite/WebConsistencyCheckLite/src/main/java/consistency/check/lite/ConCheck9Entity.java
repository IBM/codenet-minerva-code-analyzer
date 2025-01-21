package consistency.check.lite;



import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Basic;

import districtjpa.DistrictKey;


@Entity
@IdClass(DistrictKey.class)
public class ConCheck9Entity  {
	

	
	//	 persistent attributes which maps to the columns of the district table
	
	@Id 
	@Column(name="D_ID")
	private short districtId; 

	@Id 
	@Column(name="D_W_ID")
	private short districtWareId; 
	
	@Column(name="D_NAME")
	private String districtName;

	@Column(name="D_STREET_1")
	private String districtStreet1;

	@Column(name="D_STREET_2")
	private String districtStreet2;

	@Column(name="D_CITY")
	private String districtCity;

	@Column(name="D_STATE")
	private String districtState;

	@Column(name="D_ZIP")
	private String districtZip;

	@Basic
	@Column(name="D_TAX")
	private java.math.BigDecimal districtTax;
	
	@Basic
	@Column(name="SUM_H_AMOUNT")
	private java.math.BigDecimal sumAmount;
	
	@Basic
	@Column(name="D_YTD")
	private java.math.BigDecimal districtYtd;

	@Column(name="D_NEXT_O_ID")
	private int districtNextOrderId;
	
	public java.math.BigDecimal getSumAmount() {
		return sumAmount;
	}

	public void setSumAmount(java.math.BigDecimal sumAmount) {
		this.sumAmount = sumAmount;
	}

	
	public String getDistrictCity() {
		return districtCity;
	}

	public void setDistrictCity(String districtCity) {
		this.districtCity = districtCity;
	}

	
	
	public short getDistrictId() {
		return districtId;
	}

	public void setDistrictId(short districtId) {
		this.districtId = districtId;
	}

	
	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	
	public int getDistrictNextOrderId() {
		return districtNextOrderId;
	}

	public void setDistrictNextOrderId(int districtNextOrderId) {
		this.districtNextOrderId = districtNextOrderId;
	}

	public String getDistrictState() {
		return districtState;
	}

	public void setDistrictState(String districtState) {
		this.districtState = districtState;
	}

	
	public String getDistrictStreet1() {
		return districtStreet1;
	}

	public void setDistrictStreet1(String districtStreet1) {
		this.districtStreet1 = districtStreet1;
	}

	
	public String getDistrictStreet2() {
		return districtStreet2;
	}

	public void setDistrictStreet2(String districtStreet2) {
		this.districtStreet2 = districtStreet2;
	}

	
	public java.math.BigDecimal getDistrictTax() {
		return districtTax;
	}

	public void setDistrictTax(java.math.BigDecimal districtTax) {
		this.districtTax = districtTax;
	}

	
	
	public short getDistrictWareId() {
		return districtWareId;
	}

	public void setDistrictWareId(short districtWareId) {
		this.districtWareId = districtWareId;
	}

	
	public java.math.BigDecimal getDistrictYtd() {
		return districtYtd;
	}

	public void setDistrictYtd(java.math.BigDecimal districtYtd) {
		this.districtYtd = districtYtd;
	}

	
	public String getDistrictZip() {
		return districtZip;
	}

	public void setDistrictZip(String districtZip) {
		this.districtZip = districtZip;
	}


}
