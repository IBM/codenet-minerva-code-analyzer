package itemjpa;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Basic;
import jakarta.persistence.Version;
import jakarta.persistence.Transient;

@Entity
@Table(name="ITEM")
public class ItemJPA implements Serializable{
	
	private static final long serialVersionUID= 2716055610746597905L;
	
	@Version 
	protected Integer VERSION;

	@Id
	@Column(name="I_ID")
//	@GeneratedValue(strategy=GenerationType.AUTO)
	private int itemId;
	
	@Column(name="I_NAME")
	private String itemName;
	
	@Basic
	@Column(name="I_PRICE")
	private java.math.BigDecimal itemPrice;
	
	@Column(name="I_DATA")
	private String itemData;
		
	@Transient
	private String stcnum;
	
	public String getItemData() {
		return itemData;
	}
	public void setItemData(String itemData) {
		this.itemData = itemData;
	}
	
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public java.math.BigDecimal getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(java.math.BigDecimal itemPrice) {
		this.itemPrice = itemPrice;
	}	
	
	public void setStcnum(java.lang.String newStcnum) {
		this.stcnum = newStcnum;
	}
	public java.lang.String getStcnum() {
		return stcnum;
	}
}
