package sqlj;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

public abstract class OrderlineSQLJ
{
    @SuppressWarnings("unused")
    private static final String JDBC_IMPL = "sqlj.OrderlineJDBCImpl";
    private static final String SQLJ_IMPL = "sqlj.OrderlineSQLJImpl";

    final DataSource ds;

    public static OrderlineSQLJ newInstance(DataSource ds) {
        try {
            Class<?> impl = Class.forName(SQLJ_IMPL);
            return (OrderlineSQLJ) impl.getConstructor(DataSource.class).newInstance(ds);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create an instance of class " + SQLJ_IMPL +
                                       " be sure that you have run the necessary SQLJ setup before packaging" +
                                       " this application.  See the SQLJ setup script at WebOrderArchiveLite_SQLJ/sqljcustomize/setup-sqlj.xml",
                            e);
        }
    }

    public OrderlineSQLJ(DataSource ds) {
        this.ds = ds;
    }

    public abstract List<OrderlineSQLJ> findOrderLinesWithSpecificOrderId(int orderId, short districtId, short warehouseId) throws SQLException;

    public abstract void remove(OrderlineSQLJ oline) throws SQLException;

    public abstract int getOlNumber();
}
