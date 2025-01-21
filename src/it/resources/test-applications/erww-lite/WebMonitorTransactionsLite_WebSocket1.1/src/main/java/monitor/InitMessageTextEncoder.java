package monitor;

import jakarta.websocket.*;
import com.ibm.json.java.JSONObject;
import com.ibm.json.java.JSONArray;

public class InitMessageTextEncoder implements Encoder.Text<InitMessage> {
	@Override
	public void init(EndpointConfig ec) {}
	
	@Override
	public void destroy() {}
	
	@Override
	public String encode(InitMessage init_msg) throws EncodeException {
		//TODO: talk to rumana haque to see if JSON-P is something we could use
		String wSalesJsonString = "";
		JSONObject json = new JSONObject();
		JSONArray jarr_ws = new JSONArray();
		JSONArray jarr_no = new JSONArray();
		int mc = init_msg.getMessageCount();
		jarr_ws.addAll(init_msg.getWarehouseSales());
		jarr_no.addAll(init_msg.getNewOrders());
		json.put("type", "init");
		json.put("warehouse_sales", jarr_ws);
		json.put("neworders", jarr_no);
		json.put("message_count", mc);
		
		try {
			wSalesJsonString = json.serialize();
		} catch (Exception e) {
		}
		return wSalesJsonString;
	}
}
