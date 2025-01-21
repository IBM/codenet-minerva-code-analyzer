package pricechangesession;

import itemjpa.ItemJPA;

import java.io.Serializable;

public class PriceChangeOutput implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2705167383607383975L;
	ItemJPA item = null;
	String stack = null;
	String status = null;
	String message = null;
	public ItemJPA getItem() {
		return item;
	}
	public void setItem(ItemJPA item) {
		this.item = item;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStack() {
		return stack;
	}
	public void setStack(String stack) {
		this.stack = stack;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
