package sqlj;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * The purpose of this class (OrderJDBCImpl) is to perform the equivalent functions
 * its SQLJ counterpart (OrderSQLJImpl) and can be used as a "control" for comparison to
 * the SQLJ implementation for development or debugging, but should not be
 * used normally, as the purpose of this module is to test SQLJ, not JDBC.
 */
@Deprecated
public class OrderJDBCImpl extends OrderSQLJ {

    public OrderJDBCImpl(DataSource ds) {
        super(ds);
    }

    @Override
    public int findMinOrderId(short warehouseID, short districtID) throws SQLException {
        // JPQL query: select min(o.OrderId) from OrderJPA o where o.CarrierId <> 0 and o.WarehouseId = :warehouseId and o.DistrictId = :districtId
        // SQL query:  select MIN(O_ID) from ORDERS where O_D_ID=1 AND O_W_ID=1
        try (Connection con = ds.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT MIN(O_ID) FROM ORDERS" +
                                                              " WHERE O_D_ID=" + districtID +
                                                              " AND O_W_ID=" + warehouseID);
            if (!rs.next())
                throw new SQLException("Query did not return any results for district=" + districtID + " AND warehouse=" + warehouseID);
            return rs.getInt(1);
        }
    }

    @Override
    public OrderSQLJ find(short districtID, int orderId, short warehouseID) throws SQLException {
        try (Connection con = ds.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM ORDERS" +
                                                              " WHERE O_D_ID=" + districtID +
                                                              " AND O_W_ID=" + warehouseID +
                                                              " AND O_ID=" + orderId);
            OrderJDBCImpl obj = new OrderJDBCImpl(ds);
            obj.DistrictId = districtID;
            obj.OrderId = orderId;
            obj.WarehouseId = warehouseID;
            if (!rs.next())
                throw new SQLException("Did not find any order with query: " + "SELECT * FROM ORDERS" +
                                       " WHERE O_D_ID=" + districtID +
                                       " AND O_W_ID=" + warehouseID +
                                       " AND O_ID=" + orderId);
            obj.OrderLineCount = new BigDecimal(rs.getInt("O_OL_CNT"));
            return obj;
        }
    }

    @Override
    public void remove(OrderSQLJ order) throws SQLException {
        try (Connection con = ds.getConnection()) {
            int updateCount = con.createStatement().executeUpdate("DELETE FROM ORDERS" +
                                                                  " WHERE O_D_ID=" + order.DistrictId +
                                                                  " AND O_W_ID=" + order.WarehouseId +
                                                                  " AND O_ID=" + order.OrderId);
            if (updateCount != 1)
                throw new SQLException("Expected 1 row to be deleted but instead " + updateCount + " were deleted.");
        }
    }
}
