package monitor;

import java.io.IOException;

import jakarta.websocket.*;

import com.ibm.json.java.JSONObject;

import newordersession.NewOrderOutputInfo;

public class NewOrderOutputInfoDecoder implements Decoder.Text<NewOrderOutputInfo> {
	@Override
	public void init(EndpointConfig ec) {}
	
	@Override
	public void destroy() {}
	
	@Override
	public NewOrderOutputInfo decode(String jsonString) throws DecodeException {
		NewOrderOutputInfo output = new NewOrderOutputInfo();
		JSONObject json = null;
		try {
			json = JSONObject.parse(jsonString);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		output.setOutWarehouseId((short) json.get("outWarehouseId"));
		output.setOutTotal((double) json.get("outTotal"));
		
		return output;
	}
	
	@Override
	public boolean willDecode(String arg0) {
		return false;
		/*JSONObject json;
		
		try {
			json = JSONObject.parse(arg0);
		} catch (IOException e) {
			return false;
		}
		
		if (json.get("type") == "neworder") {
			return true;
		}
		
		return false;*/
	}
}
