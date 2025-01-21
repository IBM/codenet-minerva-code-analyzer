package neworderjpa;

import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Version;



@Entity
@IdClass(NewOrderKey.class)
@Table(name="NEWORDERS")

@NamedQueries({
  @NamedQuery(
		name="findMinNewOrder",
		query="select n from NewOrderJPA n where ((n.orderId in (select min(n1.orderId) from NewOrderJPA n1 where ((n1.warehouseId = :warehouseId) and (n1.districtId = :districtId)) ) ) and (n.warehouseId = :warehouseId) and (n.districtId = :districtId)) "
  ),
  
  @NamedQuery(
		name="findMaxNewOrder",
		query="select n from NewOrderJPA n where ((n.orderId in (select max(n1.orderId) from NewOrderJPA n1 where ((n1.warehouseId = :warehouseId) and (n1.districtId = :districtId)) ) ) and (n.warehouseId = :warehouseId) and (n.districtId = :districtId)) "
  ), 
  
  @NamedQuery(
		name="findNumberOfNewOrderRows",
		query="select Count(n) from NewOrderJPA n where n.warehouseId = :warehouseId and n.districtId = :districtId "
  )  
})	
		
public class NewOrderJPA implements Serializable {

	private static final long serialVersionUID = 8565789092340913255L;
	
	@Version 
	protected Integer VERSION;

	@Id
	@Column(name="NO_O_ID")
	private int orderId;
	
	
	@Id
	@Column(name="NO_D_ID")
	private short districtId;
	
	
	@Column(name="NO_W_ID")
	@Id
	private short warehouseId;
	
	public short getDistrictId() {
		return districtId;
	}
	public void setDistrictId(short districtId) {
		this.districtId = districtId;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public short getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(short warehouseId) {
		this.warehouseId = warehouseId;
	}
	
	
}
