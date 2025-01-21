package pricechangesession;

import irwwbase.UserException;
import itemjpa.ItemJPA;

public interface PriceChangeSessionLocal {
	//public ItemJPA priceChangeSession(ItemJPA input) throws UserException;
	public PriceChangeOutput priceChangeSession(ItemJPA input) throws UserException;

}
