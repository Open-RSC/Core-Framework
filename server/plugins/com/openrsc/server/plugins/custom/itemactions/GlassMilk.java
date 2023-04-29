package com.openrsc.server.plugins.custom.itemactions;
			
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.util.rsc.DataConversions; 
import com.openrsc.server.util.rsc.MessageType;
	
import java.util.Optional;
import java.util.stream.IntStream;

import static com.openrsc.server.plugins.Functions.*;
		
public class GlassMilk implements OpInvTrigger {

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		if (!command.equalsIgnoreCase("drink"))
			return false;

		int id = item.getCatalogId();

		if (id == ItemId.GLASS_MILK.id())
			return true;

		return false;
	}       
			
	@Override	       
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		handleGlassMilk(player, item);
	}

	private static void handleGlassMilk(Player player, Item item) {
		if (player.getCarriedItems().remove(item) == -1) return;
		thinkbubble(item);
		player.message("You drink the cold milk");
		give(player, ItemId.BEVERAGE_GLASS.id(), 1);
		// heal constant 4
		healstat(player, Skill.HITS.id(), 2, 0);
	}
}
