package monitor;

import java.util.List;

public class InitMessage {
	private List<Double> warehouse_sales;
	private List<Double> neworders;
	private int message_count;
	
	public InitMessage(List<Double> ws, List<Double> no, int mc) {
		warehouse_sales = ws;
		neworders = no;
		message_count = mc;
	}
	
	public List<Double> getWarehouseSales() {
		return this.warehouse_sales;
	}
	
	public List<Double> getNewOrders() {
		return this.neworders;
	}
	
	public int getMessageCount() {
		return this.message_count;
	}
}
