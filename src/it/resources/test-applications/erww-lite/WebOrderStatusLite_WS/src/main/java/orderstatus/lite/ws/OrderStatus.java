package orderstatus.lite.ws;

import irwwbase.ERWWDataNotFoundException;

import java.io.IOException;
import java.sql.SQLException;

public interface OrderStatus {
	
    public OrderStatusOutput getOrderStatus(OrderStatusInput input)
            throws IOException, SQLException, Exception, ERWWDataNotFoundException;

}
