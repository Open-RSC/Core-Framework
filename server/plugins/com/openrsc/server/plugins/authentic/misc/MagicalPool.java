package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class MagicalPool implements OpLocTrigger {

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return !player.getConfig().WANT_OPENPK_POINTS && (obj.getID() == 1166 || obj.getID() == 1155);
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 1155) {
			if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") >= 2) {
				teleport(player, 471, 3385);
				player.message("you are teleported further under ground");
			} else {
				mes("you step into the pool");
				delay(2);
				mes("you wet your boots");
				delay(2);
			}
		}
		if (obj.getID() == 1166) {
			mes("you step into the sparkling water");
			delay(2);
			mes("you feel energy rush through your veins");
			delay(2);
			teleport(player, 447, 3373);
			player.message("you are teleported to kolodions cave");
		}
	}
}
