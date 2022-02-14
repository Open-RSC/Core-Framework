package com.openrsc.server.plugins.shared;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.plugins.triggers.AttackPlayerTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class AttackPlayer implements AttackPlayerTrigger {
	@Override
	public void onAttackPlayer(Player player, Player affectedMob) {
		if (affectedMob.getLocation().inBounds(220, 107, 224, 111)) { // mage arena block real rsc.
			player.message("Here kolodion protects all from your attack");
			player.face(affectedMob); // TODO: not necessary to do this if the walk handler would do it for us.
			return;
		}

		if (attackPrevented(player, affectedMob)) {
			return;
		}

		player.startCombat(affectedMob);
		if (config().WANT_PARTIES) {
			if (player.getParty() != null) {
				player.getParty().sendParty();
			}
		}
	}

	@Override
	public boolean blockAttackPlayer(Player player, Player affectedMob) {
		return true;
	}

	public static boolean attackPrevented(Player player, Player affectedMob) {
		boolean prevented = false;
		if (affectedMob.getLocation().isInBank(player.getConfig().BASED_MAP_DATA)) {
			player.message("You cannot attack other players inside the bank");
			prevented = true;
		}

		Npc guard = ifnearvisnpc(player, NpcId.GUARD.id(), 3);
		if (guard != null) {
			guard.getUpdateFlags().setChatMessage(new ChatMessage(guard, "Hey! No fighting!", player));
			delay(2);
			guard.startCombat(player);
			prevented = true;
		}
		return prevented;
	}
}
