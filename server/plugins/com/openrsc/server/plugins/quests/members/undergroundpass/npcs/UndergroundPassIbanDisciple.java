package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassIbanDisciple implements PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener {

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return n.getID() == NpcId.IBAN_DISCIPLE.id();
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == NpcId.IBAN_DISCIPLE.id()) {
			n.killedBy(p);
			if (p.getQuestStage(Quests.UNDERGROUND_PASS) == -1) {
				message(p, "you search the diciples remains");
				if (!hasItem(p, ItemId.STAFF_OF_IBAN.id()) && !hasItem(p, ItemId.STAFF_OF_IBAN_BROKEN.id())) {
					p.message("and find a staff of iban");
					addItem(p, ItemId.STAFF_OF_IBAN_BROKEN.id(), 1);
				} else {
					p.message("but find nothing");
				}
			} else {
				createGroundItem(ItemId.ROBE_OF_ZAMORAK_TOP.id(), 1, p.getX(), p.getY(), p);
				createGroundItem(ItemId.ROBE_OF_ZAMORAK_BOTTOM.id(), 1, p.getX(), p.getY(), p);
			}
		}
	}
}
