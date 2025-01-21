package sqlj;

import java.math.BigDecimal;
import java.sql.SQLException;

import javax.sql.DataSource;

public abstract class OrderSQLJ
{
    @SuppressWarnings("unused")
    private static final String JDBC_IMPL = "sqlj.OrderJDBCImpl";
    private static final String SQLJ_IMPL = "sqlj.OrderSQLJImpl";

    final DataSource ds;

    public int OrderId;

    public short DistrictId;

    public short WarehouseId;

    public BigDecimal OrderLineCount;

    public static OrderSQLJ newInstance(DataSource ds) {
        try {
            Class<?> OrderSQLJImpl = Class.forName(SQLJ_IMPL);
            return (OrderSQLJ) OrderSQLJImpl.getConstructor(DataSource.class).newInstance(ds);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create an instance of class " + SQLJ_IMPL +
                                       " be sure that you have run the necessary SQLJ setup before packaging" +
                                       " this application.  See the SQLJ setup script at WebOrderArchiveLite_SQLJ/sqljcustomize/setup-sqlj.xml",
                            e);
        }
    }

    protected OrderSQLJ(DataSource ds) {
        this.ds = ds;
    }

    public abstract int findMinOrderId(short warehouseID, short districtID) throws SQLException;

    public abstract OrderSQLJ find(short districtID, int orderId, short warehouseID) throws SQLException;

    public abstract void remove(OrderSQLJ order) throws SQLException;
}
