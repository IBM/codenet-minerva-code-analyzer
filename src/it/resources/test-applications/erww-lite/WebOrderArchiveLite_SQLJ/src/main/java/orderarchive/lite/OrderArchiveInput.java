package orderarchive.lite;

public class OrderArchiveInput implements java.io.Serializable
{
    private static final long serialVersionUID = -5054053397790942621L;
    private short warehouseId;
    private short districtId;
    private int numOrders;

    public short getWarehouseId() {
        return warehouseId;
    }

    public short getDistrictId() {
        return districtId;
    }

    public int getNumOrders() {
        return numOrders;
    }

    public void setWarehouseId(short newWarehouseId) {
        warehouseId = newWarehouseId;
    }

    public void setDistrictId(short newDistrictId) {
        districtId = newDistrictId;
    }

    public void setNumOrders(short newNumOrders) {
        numOrders = newNumOrders;
    }
}