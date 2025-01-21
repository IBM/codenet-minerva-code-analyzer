package monitor;

import jakarta.websocket.*;

import java.util.ArrayList;

public class WarehouseSalesTextDecoder implements Decoder.Text<ArrayList<Double>> {
	@Override
	public void init(EndpointConfig ec) {}
	
	@Override
	public void destroy() {}
	
	@Override
	public ArrayList<Double> decode(String msg) throws DecodeException {

		return new ArrayList<Double>();
	}

	@Override
	public boolean willDecode(String arg0) {
		return false;
	}
}
