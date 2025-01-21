package monitor;

import jakarta.websocket.*;
import com.ibm.json.java.JSONObject;
import newordersession.NewOrderOutputInfo;

public class NewOrderOutputInfoEncoder implements Encoder.Text<NewOrderOutputInfo> {
	@Override
	public void init(EndpointConfig ec) {}
	
	@Override
	public void destroy() {}
	
	@Override
	public String encode(NewOrderOutputInfo output) throws EncodeException {
		String jsonString = "";
		JSONObject json = new JSONObject();
		
		json.put("type", "neworder");
		json.put("outWarehouseId", output.getOutWarehouseId());
		/*json.put("outDistrictId", output.getOutDistrictId());
		json.put("outCustomerId", output.getOutCustomerId());
		json.put("outOrderId", output.getOutOrderId());
		json.put("outOrderItemCount", output.getOutOrderItemCount());
		json.put("outCustomerLastName", output.getOutCustomerLastName());
		json.put("outCustomerCredit", output.getOutCustomerCredit());
		json.put("outWarehouseTax", output.getOutWarehouseTax());*/
		json.put("outTotal", output.getOutTotal());
		/*json.put("outMsg", output.getOutMsg());
		json.put("outOrderEntryDate", output.getOutOrderEntryDate());
		json.put("outItemList", output.getOutItemList());*/
		
		try {
			jsonString = json.serialize();
		} catch (Exception e) {
			throw new EncodeException(e, jsonString);
		}
		
		return jsonString;
	}
}
