package com.openrsc.server.plugins;

import com.openrsc.server.model.Shop;

public interface ShopInterface {
	
	public Shop[] getShops();
	
	public boolean isMembers();
}