package stockjpa;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Basic;
import jakarta.persistence.Version;



@Entity
@IdClass(StockKey.class)
@Table(name="STOCK")
@NamedQuery(name="findStockThreshold",
			query="select DISTINCT s from StockJPA s, OrderlineJPA o where ((o.order.WarehouseId = :warehouseId) and (o.order.DistrictId = :districtId) and (o.order.OrderId >= :lowOrderId) " +
					"and (o.order.OrderId < :highOrderId) and (s.S_W_ID = :stockWareHouseId) and (s.S_I_ID = o.olItemId)  " +
					"and (s.S_QUANTITY < :stockQuantity) ) "	)
public class StockJPA implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 486983241006807061L;

	@Version 
	protected Integer VERSION;
	
	@Id
	@Column(name="S_I_ID")
	private int S_I_ID;
	
	@Id
	@Column(name="S_W_ID")
	private short S_W_ID;
	
	@Basic
	private java.math.BigDecimal S_QUANTITY;
	private java.lang.String S_DIST_01;
	private java.lang.String S_DIST_02;
	private java.lang.String S_DIST_03;
	private java.lang.String S_DIST_04;
	private java.lang.String S_DIST_05;
	private java.lang.String S_DIST_06;
	private java.lang.String S_DIST_07;
	private java.lang.String S_DIST_08;
	private java.lang.String S_DIST_09;
	private java.lang.String S_DIST_10;
	@Basic
	private java.math.BigDecimal S_YTD;
	@Basic
	private java.math.BigDecimal S_ORDER_CNT;
	@Basic
	private java.math.BigDecimal S_REMOTE_CNT;
	private java.lang.String S_DATA;
	
	public java.lang.String getS_DATA() {
		return S_DATA;
	}
	public void setS_DATA(java.lang.String s_data) {
		S_DATA = s_data;
	}
	public java.lang.String getS_DIST_01() {
		return S_DIST_01;
	}
	public void setS_DIST_01(java.lang.String s_dist_01) {
		S_DIST_01 = s_dist_01;
	}
	public java.lang.String getS_DIST_02() {
		return S_DIST_02;
	}
	public void setS_DIST_02(java.lang.String s_dist_02) {
		S_DIST_02 = s_dist_02;
	}
	public java.lang.String getS_DIST_03() {
		return S_DIST_03;
	}
	public void setS_DIST_03(java.lang.String s_dist_03) {
		S_DIST_03 = s_dist_03;
	}
	public java.lang.String getS_DIST_04() {
		return S_DIST_04;
	}
	public void setS_DIST_04(java.lang.String s_dist_04) {
		S_DIST_04 = s_dist_04;
	}
	public java.lang.String getS_DIST_05() {
		return S_DIST_05;
	}
	public void setS_DIST_05(java.lang.String s_dist_05) {
		S_DIST_05 = s_dist_05;
	}
	public java.lang.String getS_DIST_06() {
		return S_DIST_06;
	}
	public void setS_DIST_06(java.lang.String s_dist_06) {
		S_DIST_06 = s_dist_06;
	}
	public java.lang.String getS_DIST_07() {
		return S_DIST_07;
	}
	public void setS_DIST_07(java.lang.String s_dist_07) {
		S_DIST_07 = s_dist_07;
	}
	public java.lang.String getS_DIST_08() {
		return S_DIST_08;
	}
	public void setS_DIST_08(java.lang.String s_dist_08) {
		S_DIST_08 = s_dist_08;
	}
	public java.lang.String getS_DIST_09() {
		return S_DIST_09;
	}
	public void setS_DIST_09(java.lang.String s_dist_09) {
		S_DIST_09 = s_dist_09;
	}
	public java.lang.String getS_DIST_10() {
		return S_DIST_10;
	}
	public void setS_DIST_10(java.lang.String s_dist_10) {
		S_DIST_10 = s_dist_10;
	}
	public int getS_I_ID() {
		return S_I_ID;
	}
	public void setS_I_ID(int s_i_id) {
		S_I_ID = s_i_id;
	}
	public java.math.BigDecimal getS_ORDER_CNT() {
		return S_ORDER_CNT;
	}
	public void setS_ORDER_CNT(java.math.BigDecimal s_order_cnt) {
		S_ORDER_CNT = s_order_cnt;
	}
	public java.math.BigDecimal getS_QUANTITY() {
		return S_QUANTITY;
	}
	public void setS_QUANTITY(java.math.BigDecimal s_quantity) {
		S_QUANTITY = s_quantity;
	}
	public java.math.BigDecimal getS_REMOTE_CNT() {
		return S_REMOTE_CNT;
	}
	public void setS_REMOTE_CNT(java.math.BigDecimal s_remote_cnt) {
		S_REMOTE_CNT = s_remote_cnt;
	}
	public short getS_W_ID() {
		return S_W_ID;
	}
	public void setS_W_ID(short s_w_id) {
		S_W_ID = s_w_id;
	}
	public java.math.BigDecimal getS_YTD() {
		return S_YTD;
	}
	public void setS_YTD(java.math.BigDecimal s_ytd) {
		S_YTD = s_ytd;
	}


}
