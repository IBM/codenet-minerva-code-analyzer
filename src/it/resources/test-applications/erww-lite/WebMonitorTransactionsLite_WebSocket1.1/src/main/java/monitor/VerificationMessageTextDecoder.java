package monitor;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.websocket.*;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class VerificationMessageTextDecoder implements Decoder.Text<VerificationMessage> {
	@Override
	public void init(EndpointConfig ec) {}
	
	@Override
	public void destroy() {}
	
	@Override
	public VerificationMessage decode(String jsonString) throws DecodeException {
		
		JSONObject json = null;
		try {
			json = JSONObject.parse(jsonString);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		int clientCount = (int) (long) json.get("message_count");
		ArrayList<Double> ws = new ArrayList<Double>();
		ArrayList<Double> no = new ArrayList<Double>(); 
		
		JSONArray jsonArray1 = (JSONArray)json.get("warehouse_sales"); 
		if (jsonArray1 != null) { 
		   int len = jsonArray1.size();
		   for (int i=0;i<len;i++){ 
		    ws.add(Double.valueOf(jsonArray1.get(i).toString()));
		   } 
		}
		
		JSONArray jsonArray2 = (JSONArray)json.get("neworders"); 
		if (jsonArray2 != null) { 
		   int len = jsonArray2.size();
		   for (int i=0;i<len;i++){ 
		    no.add(Double.valueOf(jsonArray2.get(i).toString()));
		   }
		}
		
		VerificationMessage verify_msg = new VerificationMessage(ws,no,clientCount);
		
		return verify_msg;
	}
	
	@Override
	public boolean willDecode(String arg0) {
		
		JSONObject json;
		
		try {
			json = JSONObject.parse(arg0);
		} catch (IOException e) {
			return false;
		}
		
		if ("verify_data".equals(json.get("type"))) {
			return true;
		}
		
		return false;
	}
}
