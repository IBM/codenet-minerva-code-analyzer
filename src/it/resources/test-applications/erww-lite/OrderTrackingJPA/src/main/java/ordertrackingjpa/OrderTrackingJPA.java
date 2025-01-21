package ordertrackingjpa;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the ORDER_TRACKING database table.
 * 
 */
@Entity
@IdClass(OrderTrackingKey.class)
@Table(name="ORDER_TRACKING")

@NamedQueries({
	
	@NamedQuery(name="OrderTrackingJPA.findAll", query="SELECT o FROM OrderTrackingJPA o"),
	
	@NamedQuery(
			name="findMaxOrderTrackingHopforOrderTrackingNumber",
			query="select max(o.orderTrackingHop) from OrderTrackingJPA o where "
					+ "(o.orderTrackingNumber = :orderTrackingNumber) "
	),
	
	@NamedQuery(
			name="numberOfOrderTrackingRowsByOrderTrackingNumber",
			query="select count(o) from OrderTrackingJPA o where "
					+ "(o.orderTrackingNumber = :orderTrackingNumber) "
	)
	
})	

public class OrderTrackingJPA implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Version 
	protected Integer VERSION;
	
	@Id
	@Column(name="OT_TRACKING_NUMBER")
	private String orderTrackingNumber;
	
	@Id
	@Column(name="OT_TRACKING_HOP")
	private int orderTrackingHop;

	@Column(name="OT_ACTIVITY")
	private short orderTrackingActivity;

	@Column(name="OT_CITY")
	private String orderTrackingCity;

	@Column(name="OT_COUNTRY")
	private String orderTrackingCountry;

	@Column(name="OT_DATE")
	private Timestamp orderTrackingDate;

	@Column(name="OT_STATE")
	private String orderTrackingState;

	public OrderTrackingJPA() {
	}

	public String getOrderTrackingNumber() {
		return this.orderTrackingNumber;
	}

	public void setOrderTrackingNumber(String otNumber) {
		this.orderTrackingNumber = otNumber;
	}
	
	public int getOrderTrackingHop() {
		return this.orderTrackingHop;
	}

	public void setOrderTrackingHop(int otHop) {
		this.orderTrackingHop = otHop;
	}

	public short getOrderTrackingActivity() {
		return this.orderTrackingActivity;
	}

	public void setOrderTrackingActivity(short otActivity) {
		this.orderTrackingActivity = otActivity;
	}

	public String getOrderTrackingCity() {
		return this.orderTrackingCity;
	}

	public void setOrderTrackingCity(String otCity) {
		this.orderTrackingCity = otCity;
	}

	public String getOrderTrackingCountry() {
		return this.orderTrackingCountry;
	}

	public void setOrderTrackingCountry(String otCountry) {
		this.orderTrackingCountry = otCountry;
	}

	public Timestamp getOrderTrackingDate() {
		return this.orderTrackingDate;
	}

	public void setOrderTrackingDate(Timestamp otDate) {
		this.orderTrackingDate = otDate;
	}

	public String getOrderTrackingState() {
		return this.orderTrackingState;
	}

	public void setOrderTrackingState(String otState) {
		this.orderTrackingState = otState;
	}

}