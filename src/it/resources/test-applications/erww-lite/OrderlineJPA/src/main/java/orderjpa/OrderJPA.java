package orderjpa;
import java.io.Serializable;import java.util.Collection;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Basic;
import jakarta.persistence.Version;

import orderlinejpa.OrderlineJPA;

@Entity
@IdClass(OrderKey.class)
@Table(name="ORDERS")

@NamedQueries({
	
  @NamedQuery(
	name="findMaxOrderId",
	query="select o from OrderJPA o where o.OrderId in (select max(o1.OrderId) from OrderJPA o1 where ((o1.CustomerId = :customerId) and (o1.DistrictId = :districtId) and (o1.WarehouseId = :warehouseId))) and (o.CustomerId = :customerId) and (o.DistrictId = :districtId) and (o.WarehouseId = :warehouseId) "
	),
	
  @NamedQuery( 
	name="findMinOrderId",
	query="select min(o.OrderId) from OrderJPA o where o.CarrierId <> 0 and o.WarehouseId = :warehouseId and o.DistrictId = :districtId "
	),
	
  @NamedQuery( 
	name="findOrdersForUniqueWarehouseDistrictCombination",
	query="select o from OrderJPA o where o.CarrierId <> 0 and o.WarehouseId = :warehouseId and o.DistrictId = :districtId order by o.OrderId "
	),
	
  @NamedQuery( 
	name="findAllDeliveredOrders",
	query="select o from OrderJPA o where o.CarrierId <> 0 "
	),
			
  @NamedQuery(
	name="findOrderlineCountByWarehouseDistrict",
	query="select SUM(o.OrderLineCount) from OrderJPA o where o.DistrictId = :districtId and o.WarehouseId = :warehouseId "
	),
	
  @NamedQuery(
	name="findMaxOrderIdByWarehouseDistrict",
	query="select o from OrderJPA o where o.OrderId in (select max(o1.OrderId) from OrderJPA o1 where ((o1.DistrictId = :districtId) and (o1.WarehouseId = :warehouseId))) and (o.DistrictId = :districtId) and (o.WarehouseId = :warehouseId) "	
	),
	
  @NamedQuery(
	name="findOrdersWithAnOrderTrackingNumberByWarehouseIdAndDistrictId",
	query="select o from OrderJPA o where o.OrderTrackingNumber is not null and o.WarehouseId >= :minWarehouseId and o.WarehouseId <= :maxWarehouseId and o.DistrictId >= :minDistrictId and o.DistrictId <= :maxDistrictId"
	)
  
})	
			
public class OrderJPA implements Serializable {

	private static final long serialVersionUID = -7624807731830116495L;
	
	@Version 
	protected Integer VERSION;

	@Id
	@Column(name="O_ID")
	private  int OrderId;
	
	@Id
	@Column(name="O_D_ID")
	private  short DistrictId;
	
	@Id
	@Column(name="O_W_ID")
	private  short WarehouseId;
	
	@Column(name="O_C_ID")
	private  short CustomerId;
	
	@Column(name="O_ENTRY_D")
	private java.sql.Timestamp EntryDate;
	
	@Column(name="O_CARRIER_ID")
	private  short CarrierId;
	
	@Basic
	@Column(name="O_OL_CNT")
	private java.math.BigDecimal OrderLineCount;
	
	@Column(name="O_ALL_LOCAL")
	private  short AllLocal;
	
	@Column(name="O_TRACKING_NUMBER")
	private String OrderTrackingNumber;
	
	@Column(name="O_TRACKING_HOPS_COUNT")
	private int OrderTrackingHopsCount;
	
	@OneToMany(mappedBy="order", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	private Collection<OrderlineJPA> orderlines;
	
	public short getAllLocal() {
		return AllLocal;
	}
	public void setAllLocal(short allLocal) {
		AllLocal = allLocal;
	}
	public short getCarrierId() {
		return CarrierId;
	}
	public void setCarrierId(short carrierId) {
		CarrierId = carrierId;
	}
	public short getCustomerId() {
		return CustomerId;
	}
	public void setCustomerId(short customerId) {
		CustomerId = customerId;
	}
	public short getDistrictId() {
		return DistrictId;
	}
	public void setDistrictId(short districtId) {
		DistrictId = districtId;
	}
	public java.sql.Timestamp getEntryDate() {
		return EntryDate;
	}
	public void setEntryDate(java.sql.Timestamp entryDate) {
		EntryDate = entryDate;
	}
	public int getOrderId() {
		return OrderId;
	}
	public void setOrderId(int orderId) {
		OrderId = orderId;
	}
	public java.math.BigDecimal getOrderLineCount() {
		return OrderLineCount;
	}
	public void setOrderLineCount(java.math.BigDecimal orderLineCount) {
		OrderLineCount = orderLineCount;
	}
	public short getWarehouseId() {
		return WarehouseId;
	}
	public void setWarehouseId(short warehouseId) {
		WarehouseId = warehouseId;
	}
	
	public String getOrderTrackingNumber() {
		return OrderTrackingNumber;
	}
	public void setOrderTrackingNumber(String orderTrackingNumber) {
		OrderTrackingNumber = orderTrackingNumber;
	}	
	
	public int getOrderTrackingHopsCount() {
		return OrderTrackingHopsCount;
	}
	public void setOrderTrackingHopsCount(int orderTrackingHopsCount) {
		OrderTrackingHopsCount = orderTrackingHopsCount;
	}
	
	public Collection<OrderlineJPA> getOrderlines() {
        return orderlines;
    }
    public void setOrderlines(Collection<OrderlineJPA> orderlines) {
        this.orderlines = orderlines;
    }
    public void addOrderline(OrderlineJPA orderline){
    	this.orderlines.add(orderline);
    }
    public void removeOrderline(OrderlineJPA orderline){
    	this.orderlines.remove(orderline);
    }


}

