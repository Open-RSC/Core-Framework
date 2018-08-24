package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;

import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;

import com.openrsc.server.Constants;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;

public class UndergroundPassKalrag implements PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener {

	public static int KALRAG = 641;
	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		if(n.getID() == KALRAG) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if(n.getID() == KALRAG) {
			n.killedBy(p);
			message(p, "kalrag slumps to the floor",
					"poison flows from the corpse over the soil");
			if(!p.getCache().hasKey("poison_on_doll") && p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == 6) {
				if(hasItem(p, 1004)) {
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
