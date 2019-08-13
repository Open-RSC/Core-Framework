package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;

public class UndergroundPassKalrag implements PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener {

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return n.getID() == NpcId.KALRAG.id();
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == NpcId.KALRAG.id()) {
			n.killedBy(p);
			message(p, "kalrag slumps to the floor",
				"poison flows from the corpse over the soil");
			if (!p.getCache().hasKey("poison_on_doll") && p.getQuestStage(Quests.UNDERGROUND_PASS) == 6) {
				if (hasItem(p, ItemId.A_DOLL_OF_IBAN.id())) {
					message(p, "you smear the doll of iban in the poisoned blood");
					p.message("it smells horrific");
					p.getCache().store("poison_on_doll", true);
				} else {
					message(p, "it quikly seeps away into the earth");
					p.message("you dare not collect any without ibans doll");
				}
			}
		}
	}
}
