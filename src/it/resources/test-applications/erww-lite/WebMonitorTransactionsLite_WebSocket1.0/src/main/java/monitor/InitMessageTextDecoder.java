package monitor;

import jakarta.websocket.*;

import java.util.ArrayList;

public class InitMessageTextDecoder implements Decoder.Text<InitMessage> {
	@Override
	public void init(EndpointConfig ec) {}
	
	@Override
	public void destroy() {}
	
	@Override
	public InitMessage decode(String msg) throws DecodeException {
		//JSONObject json = JSONObject.parse(msg);
		
		//for (int i=0; i < json.get("warehouse_sales"); i++) {
			
		//}
		
		return new InitMessage(new ArrayList<Double>(), new ArrayList<Double>(), 0);
	}

	@Override
	public boolean willDecode(String arg0) {
		return false;
	}
}
