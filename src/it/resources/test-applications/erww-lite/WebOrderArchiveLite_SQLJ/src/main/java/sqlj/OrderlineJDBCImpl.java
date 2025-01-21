package sqlj;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

/**
 * The purpose of this class (OrderlineJDBCImpl) is to perform the equivalent functions
 * its SQLJ counterpart (OrderlineSQLJImpl) and can be used as a "control" for comparison to
 * the SQLJ implementation for development or debugging, but should not be
 * used normally, as the purpose of this module is to test SQLJ, not JDBC.
 */
@Deprecated
public class OrderlineJDBCImpl extends OrderlineSQLJ
{
    private OrderSQLJ order;

    private short olNumber;

    public OrderlineJDBCImpl(DataSource ds) {
        super(ds);
    }

    @Override
    public List<OrderlineSQLJ> findOrderLinesWithSpecificOrderId(int orderId, short districtId, short warehouseId) throws SQLException {
        try (Connection con = ds.getConnection()) {
            // SQL query: SELECT * FROM ORDERLINE WHERE OL_D_ID=1 AND OL_W_ID=1 AND OL_O_ID=1
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM ORDERLINE" +
                                                              " WHERE OL_O_ID=" + orderId +
                                                              " AND OL_D_ID=" + districtId +
                                                              " AND OL_W_ID=" + warehouseId);
            OrderSQLJ order = OrderSQLJ.newInstance(ds).find(districtId, orderId, warehouseId);
            List<OrderlineSQLJ> results = new ArrayList<>();
            while (rs.next()) {
                OrderlineJDBCImpl oLine = new OrderlineJDBCImpl(ds);
                oLine.olNumber = rs.getShort("OL_NUMBER");
                oLine.order = order;
                results.add(oLine);
            }
            return results;
        }
    }

    @Override
    public void remove(OrderlineSQLJ oline) throws SQLException {
        // SQL query: DELETE FROM ORDERLINE WHERE OL_D_ID=1 AND OL_W_ID=1 AND OL_O_ID=1 AND OL_NUMBER=1
        OrderlineJDBCImpl ol = (OrderlineJDBCImpl) oline;
        try (Connection con = ds.getConnection()) {
            int updateCount = con.createStatement().executeUpdate("DELETE FROM ORDERLINE" +
                                                                  " WHERE OL_O_ID=" + ol.order.OrderId +
                                                                  " AND OL_D_ID=" + ol.order.DistrictId +
                                                                  " AND OL_W_ID=" + ol.order.WarehouseId +
                                                                  " AND OL_NUMBER=" + ol.getOlNumber());
            if (updateCount != 1)
                throw new SQLException("Expected 1 row to be deleted but instead " + updateCount + " were deleted.");
        }
    }

    @Override
    public int getOlNumber() {
        return olNumber;
    }

}