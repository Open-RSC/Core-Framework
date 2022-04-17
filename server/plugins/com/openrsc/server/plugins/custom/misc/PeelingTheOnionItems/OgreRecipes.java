package com.openrsc.server.plugins.custom.misc.PeelingTheOnionItems;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpInvTrigger;


public class OgreRecipes implements OpInvTrigger {
	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		ActionSender.sendBox(player, "@lre@Classic Ogre Recipes% %" +
				"@yel@Weedrat stew:@whi@ First catch a weedrat. Boil entire rat in swamp water for 20 to 40 minutes with onions and swamp greens. Alternatively, rotisserie 40 to 60 minutes.%" +
				"@yel@Spider cotton-candy:@whi@ Take a stick to a spiderweb and rotate until the spiderweb completely wraps around the stick several times. Best if spiders and their eggs in the web.%" +
				"@yel@Swamplarva:@whi@ Turn over some rocks, you'll find delicious grubs. These should be eaten live.%" +
				"@yel@Onions:@whi@ Always a great snack. These can be boiled in a stew or eaten raw.%" +
				"@yel@Wormstuffed pumpkin:@whi@ Cut open the top of a pumpkin and stick some redvine worms inside. Enjoy.%" +
				"@yel@Eyeballs:@whi@ All varieties are delicious. However, fish heads can be gathered in mass quantities near human settlements, as they usually waste them. It's also easily possible to catch your own fish, e.g. by farting underwater.%"
			, true);
	}


	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.OGRE_RECIPES.id();
	}
}
