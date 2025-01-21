package monitor;

import java.util.List;

public class VerificationMessage {
	private List<Double> warehouseSales;
	private List<Double> neworders;
	private int clientCount;
	
	public VerificationMessage(List<Double> warehouseSalesArg, List<Double> newordersArg, int clientCountArg) {
		warehouseSales = warehouseSalesArg;
		neworders = newordersArg;
		clientCount = clientCountArg;
	}
	
	public List<Double> getWarehouseSales() {
		return warehouseSales;
	}
	
	public List<Double> getNewOrders() {
		return neworders;
	}
	
	public int getClientCount() {
		return clientCount;
	}
	
	boolean compareTo(VerificationMessage msg) {
		List<Double> otherWarehouseSales = msg.getWarehouseSales();
		List<Double> otherNeworders = msg.getNewOrders();
		
		if ((warehouseSales.size() != otherWarehouseSales.size()) ||
			(neworders.size() != otherNeworders.size())) {
			return false;
		}
		
		for (int i=0; i<warehouseSales.size(); i++) {
			if (!warehouseSales.get(i).equals(otherWarehouseSales.get(i))) {
				return false;
			}
		}
		
		for (int i=0; i<neworders.size(); i++) {
			if (!neworders.get(i).equals(otherNeworders.get(i))) {
				return false;
			}
		}
		
		return true;
	}
}
