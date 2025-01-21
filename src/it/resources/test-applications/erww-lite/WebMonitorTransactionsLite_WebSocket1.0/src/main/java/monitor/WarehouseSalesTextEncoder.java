package monitor;

import jakarta.websocket.*;

import java.util.ArrayList;

import com.ibm.json.java.JSONObject;
import com.ibm.json.java.JSONArray;

public class WarehouseSalesTextEncoder implements Encoder.Text<ArrayList<Double>> {
	@Override
	public void init(EndpointConfig ec) {}
	
	@Override
	public void destroy() {}
	
	@Override
	public String encode(ArrayList<Double> pay_msg) throws EncodeException {
		String wSalesJsonString = "";
		JSONObject json = new JSONObject();
		JSONArray jarr = new JSONArray();
		
		jarr.addAll(pay_msg);
		
		json.put("type", "init");
		json.put("warehouse_sales", jarr);
		
		try {
			wSalesJsonString = json.serialize();
		} catch (Exception e) {
		}
		return wSalesJsonString;
	}
}
