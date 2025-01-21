package orderlinejpa;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Basic;
import jakarta.persistence.Version;

//JPA-2.1 Migration
//These OpenJPA specific functionalities were used for olInfo which
//isn't used by ERWW-Lite and don't work with JPA-2.1
//-import org.apache.openjpa.persistence.Persistent;
//-import org.apache.openjpa.persistence.jdbc.Strategy;

import orderjpa.OrderJPA;


@Entity
@IdClass(OrderlineKey.class)
@Table(name="Orderline")

@NamedQueries({
	
	  @NamedQuery(
	    name="findNumberOfOrderlineRows",
		query="select Count(o) from OrderlineJPA o where o.order.WarehouseId = :warehouseId and o.order.DistrictId = :districtId "
	    ), 

	  @NamedQuery(
		name="findOrderLinesWithSpecificOrderId",
		query="select o from OrderlineJPA o where o.order.OrderId = :orderId and o.order.DistrictId = :districtId and o.order.WarehouseId = :warehouseId"
	    )

	})

public class OrderlineJPA implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6850145591790844181L;

	@Version 
	protected Integer VERSION;

	@Id
	@JoinColumns({
	    @JoinColumn(name="OL_O_ID", referencedColumnName="O_ID", nullable=false),
	    @JoinColumn(name="OL_D_ID", referencedColumnName="O_D_ID", nullable=false),
	    @JoinColumn(name="OL_W_ID", referencedColumnName="O_W_ID", nullable=false)
	})
	@ManyToOne(fetch=FetchType.LAZY)
	private OrderJPA order;
	
	@Id
	@Column(name="OL_NUMBER")
	private short olNumber;
	
	@Column(name="OL_I_ID")
	private int olItemId;
	
	@Column(name="OL_SUPPLY_W_ID")
	private short olSupplyWarehouseId;
	
	@Basic
	@Column(name="OL_DELIVERY_D")
	private java.sql.Timestamp  olDeliveryDate;
	
	@Basic
	@Column(name="OL_QUANTITY")
	private java.math.BigDecimal olQuantity;
	
	@Basic
	@Column(name="OL_AMOUNT")
	private java.math.BigDecimal olAmount;
	
	@Column(name="OL_DIST_INFO")
	private java.lang.String olDistrictInfo;
	
	//JPA-2.1 Migration
	//EclipseLink takes exception with using xml attributes and Mary Ellen stated this is not used for ERWW_Lite anyway
	/*
	@Persistent
	@Strategy("org.apache.openjpa.jdbc.meta.strats.XMLValueHandler")
	@Column(name="I_ORDERLINE_INFO")
	private OrderlineInfo olInfo;
	*/

	public java.math.BigDecimal getOlAmount() {
		return olAmount;
	}
	public void setOlAmount(java.math.BigDecimal olAmount) {
		this.olAmount = olAmount;
	}

	public java.sql.Timestamp getOlDeliveryDate() {
		return olDeliveryDate;
	}
	public void setOlDeliveryDate(java.sql.Timestamp olDeliveryDate) {
		this.olDeliveryDate = olDeliveryDate;
	}

	public java.lang.String getOlDistrictInfo() {
		return olDistrictInfo;
	}
	public void setOlDistrictInfo(java.lang.String olDistrictInfo) {
		this.olDistrictInfo = olDistrictInfo;
	}

	public int getOlItemId() {
		return olItemId;
	}
	public void setOlItemId(int olItemId) {
		this.olItemId = olItemId;
	}

	public short getOlNumber() {
		return olNumber;
	}
	public void setOlNumber(short olNumber) {
		this.olNumber = olNumber;
	}

	public java.math.BigDecimal getOlQuantity() {
		return olQuantity;
	}
	public void setOlQuantity(java.math.BigDecimal olQuantity) {
		this.olQuantity = olQuantity;
	}

	public short getOlSupplyWarehouseId() {
		return olSupplyWarehouseId;
	}
	public void setOlSupplyWarehouseId(short olSupplyWarehouseId) {
		this.olSupplyWarehouseId = olSupplyWarehouseId;
	}
	
    public void setOrder(OrderJPA order) {
        this.order = order;
    }
    public OrderJPA getOrder() {
        return order;
    }
}
