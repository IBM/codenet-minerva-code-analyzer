package historyjpa;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity
@Table(name="HISTORY")

@NamedQueries({
  @NamedQuery(
		name="sumHistoryByWarehouse",
		query="select SUM(h.historyAmount) from HistoryJPA h where h.historyWId = :warehouseId "
  ),
   
  @NamedQuery(
		name="sumHistoryByWarehouseDistrict",
		query="select SUM(h.historyAmount) from HistoryJPA h where h.historyWId = :warehouseId and h.historyDId = :districtId "
  )  
})


public class HistoryJPA implements Serializable {
	
	private static final long serialVersionUID= 2716055680746597905L;
	
	@Version 
	protected Integer VERSION;
	
	@Id
	@Column(name="H_DATE")	
	private java.sql.Timestamp historyDate;
	
	@Column(name="H_C_ID")
	private short historyCId;
	
	@Column(name="H_C_D_ID")
	private short historyCDId;
	
	@Column(name="H_C_W_ID")
	private short historyCWId;
	
	@Column(name="H_D_ID")
	private short historyDId;
	
	@Column(name="H_W_ID")
	private short historyWId;
	
	@Basic
	@Column(name="H_AMOUNT")
	private java.math.BigDecimal historyAmount;
	
	@Column(name="H_DATA")
	private java.lang.String historyData;
	
	

	
	
	public java.math.BigDecimal getHistoryAmount() {
		return historyAmount;
	}
	public void setHistoryAmount(java.math.BigDecimal historyAmount) {
		this.historyAmount = historyAmount;
	}
	
	public short getHistoryCDId() {
		return historyCDId;
	}
	public void setHistoryCDId(short historyCDId) {
		this.historyCDId = historyCDId;
	}
	
	public short getHistoryCId() {
		return historyCId;
	}
	public void setHistoryCId(short historyCId) {
		this.historyCId = historyCId;
	}
	
	public short getHistoryCWId() {
		return historyCWId;
	}
	public void setHistoryCWId(short historyCWId) {
		this.historyCWId = historyCWId;
	}
	
	public java.lang.String getHistoryData() {
		return historyData;
	}
	public void setHistoryData(java.lang.String historyData) {
		this.historyData = historyData;
	}
	
	public java.sql.Timestamp getHistoryDate() {
	  return historyDate;
	}
	public void setHistoryDate(java.sql.Timestamp historyDate) {
		this.historyDate = historyDate;
	}
	
	public short getHistoryDId() {
		return historyDId;
	}
	public void setHistoryDId(short historyDId) {
		this.historyDId = historyDId;
	}
	
	public short getHistoryWId() {
		return historyWId;
	}
	public void setHistoryWId(short historyWId) {
		this.historyWId = historyWId;
	}


}
