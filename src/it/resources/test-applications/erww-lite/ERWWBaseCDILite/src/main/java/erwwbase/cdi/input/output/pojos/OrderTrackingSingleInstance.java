package erwwbase.cdi.input.output.pojos;

import java.sql.Timestamp;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OrderTrackingSingleInstance implements java.io.Serializable {
	
	private static final long serialVersionUID = 7364169947796934077L;

	private String orderTrackingNumber = null;

	private int orderTrackingHop = 0;

	private short orderTrackingActivity = 0;

	private String orderTrackingCity = null;

	private String orderTrackingCountry = null;

	private Timestamp orderTrackingDate = null;

	private String orderTrackingState = null;
	
	@NotNull
	@Size(min=7, max=24) 
	public String getOrderTrackingNumber() {
		return orderTrackingNumber;
	}
	
	public void setOrderTrackingNumber(@NotNull @Size(min=7, max=24) String newOrderTrackingNumber) {
		this.orderTrackingNumber = newOrderTrackingNumber;
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
