package com.openrsc.server.plugins;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.world.World;

public interface ShopInterface {

	public Shop[] getShops(World world);

	public boolean isMembers();
}
