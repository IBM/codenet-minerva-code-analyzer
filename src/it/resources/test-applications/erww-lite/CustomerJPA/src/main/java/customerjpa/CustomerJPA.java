package customerjpa;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity
@IdClass(CustomerKey.class)
@Table(name="CUSTOMER")
@NamedQuery(
		name="findCustomerByLastName",
		query="select c from CustomerJPA c where ((c.customerLast = :lastName) and (c.customerDId = :districtId) and (c.customerWId = :warehouseId))"	)
public class CustomerJPA implements Serializable
{
	private static final long serialVersionUID= 2716055660746597905L;
	
	@Version 
	protected Integer VERSION;

	@Basic
	@Column(name="C_BALANCE")
	private java.math.BigDecimal customerBalance;

	@Column(name="C_CITY")
	private String customerCity;

	@Column(name="C_CREDIT")
	private String customerCredit;

	@Basic
	@Column(name="C_CREDIT_LIM")
	private java.math.BigDecimal customerCreditLim;

	@Column(name="C_DATA")
	private String customerData;

	@Basic
	@Column(name="C_DELIVERY_CNT")
	private java.math.BigDecimal customerDeliveryCnt;

	@Column(name="C_D_ID")
	@Id
	private short customerDId;

	@Basic
	@Column(name="C_DISCOUNT")
	private java.math.BigDecimal customerDiscount;

	@Column(name="C_FIRST")
	private String customerFirst;

	@Id
	@Column(name="C_ID")
	private short customerId;

	@Column(name="C_LAST")
	private String customerLast;

	@Column(name="C_MIDDLE")
	private String customerMiddle;

	@Basic
	@Column(name="C_PAYMENT_CNT")
	private java.math.BigDecimal customerPaymentCnt;

	@Column(name="C_PHONE")
	private String customerPhone;

	@Basic
	@Column(name="C_SINCE")
	private java.sql.Timestamp customerSince;

	@Column(name="C_STATE")
	private String customerState;
	
	
	@Column(name="C_STREET_1")
	private String customerStreet1;

	@Column(name="C_STREET_2")
	private String customerStreet2;

	@Column(name="C_W_ID")
	@Id
	private short customerWId;
	
	@Basic
	@Column(name="C_YTD_PAYMENT")
	private java.math.BigDecimal customerYtdPayment;

	@Column(name="C_ZIP")
	private String customerZip;
	
	@Column(name="C_TIME")
	private java.time.LocalTime customerTime;


	public java.time.LocalTime getCustomerTime() {
			return customerTime;
	}

	public void setCustomerTime(java.time.LocalTime customerTime) {
			this.customerTime = customerTime;
	}

	
	public java.math.BigDecimal getCustomerBalance() {
		return customerBalance;
	}

	public void setCustomerBalance(java.math.BigDecimal customerBalance) {
		this.customerBalance = customerBalance;
	}

	
	public String getCustomerCity() {
		return customerCity;
	}

	public void setCustomerCity(String customerCity) {
		this.customerCity = customerCity;
	}

	
	public String getCustomerCredit() {
		return customerCredit;
	}

	public void setCustomerCredit(String customerCredit) {
		this.customerCredit = customerCredit;
	}

	
	public java.math.BigDecimal getCustomerCreditLim() {
		return customerCreditLim;
	}

	public void setCustomerCreditLim(java.math.BigDecimal customerCreditLim) {
		this.customerCreditLim = customerCreditLim;
	}

	
	public String getCustomerData() {
		return customerData;
	}

	public void setCustomerData(String customerData) {
		this.customerData = customerData;
	}

	
	public java.math.BigDecimal getCustomerDeliveryCnt() {
		return customerDeliveryCnt;
	}

	public void setCustomerDeliveryCnt(java.math.BigDecimal customerDeliveryCnt) {
		this.customerDeliveryCnt = customerDeliveryCnt;
	}

	
	public short getCustomerDId() {
		return customerDId;
	}

	public void setCustomerDId(short customerDId) {
		this.customerDId = customerDId;
	}

	
	public java.math.BigDecimal getCustomerDiscount() {
		return customerDiscount;
	}

	public void setCustomerDiscount(java.math.BigDecimal customerDiscount) {
		this.customerDiscount = customerDiscount;
	}

	
	public String getCustomerFirst() {
		return customerFirst;
	}

	public void setCustomerFirst(String customerFirst) {
		this.customerFirst = customerFirst;
	}

	
	public short getCustomerId() {
		return customerId;
	}

	public void setCustomerId(short customerId) {
		this.customerId = customerId;
	}

	
	public String getCustomerLast() {
		return customerLast;
	}

	public void setCustomerLast(String customerLast) {
		this.customerLast = customerLast;
	}

	
	public String getCustomerMiddle() {
		return customerMiddle;
	}

	public void setCustomerMiddle(String customerMiddle) {
		this.customerMiddle = customerMiddle;
	}

	
	public java.math.BigDecimal getCustomerPaymentCnt() {
		return customerPaymentCnt;
	}

	public void setCustomerPaymentCnt(java.math.BigDecimal customerPaymentCnt) {
		this.customerPaymentCnt = customerPaymentCnt;
	}

	
	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	
	public java.sql.Timestamp getCustomerSince() {
		return customerSince;
	}

	public void setCustomerSince(java.sql.Timestamp customerSince) {
		this.customerSince = customerSince;
	}

	
	public String getCustomerState() {
		return customerState;
	}

	public void setCustomerState(String customerState) {
		this.customerState = customerState;
	}

	
	public String getCustomerStreet1() {
		return customerStreet1;
	}

	public void setCustomerStreet1(String customerStreet1) {
		this.customerStreet1 = customerStreet1;
	}

	
	public String getCustomerStreet2() {
		return customerStreet2;
	}

	public void setCustomerStreet2(String customerStreet2) {
		this.customerStreet2 = customerStreet2;
	}

	
	public short getCustomerWId() {
		return customerWId;
	}

	public void setCustomerWId(short customerWId) {
		this.customerWId = customerWId;
	}

	
	public java.math.BigDecimal getCustomerYtdPayment() {
		return customerYtdPayment;
	}

	public void setCustomerYtdPayment(java.math.BigDecimal customerYtdPayment) {
		this.customerYtdPayment = customerYtdPayment;
	}

	
	public String getCustomerZip() {
		return customerZip;
	}

	public void setCustomerZip(String customerZip) {
		this.customerZip = customerZip;
	}

	
}

