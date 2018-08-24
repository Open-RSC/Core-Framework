package com.openrsc.server.plugins.quests.members.legendsquest.mechanism;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.DropListener;
import com.openrsc.server.plugins.listeners.executive.DropExecutiveListener;
import com.openrsc.server.plugins.quests.members.legendsquest.npcs.LegendsQuestIrvigSenay;
import com.openrsc.server.plugins.quests.members.legendsquest.npcs.LegendsQuestRanalphDevere;
import com.openrsc.server.plugins.quests.members.legendsquest.npcs.LegendsQuestSanTojalon;

public class LegendsQuestOnDrop implements DropListener, DropExecutiveListener {
	
	@Override
	public boolean blockDrop(Player p, Item i) {
		if(i.getID() == LegendsQuestSanTojalon.A_CHUNK_OF_CRYSTAL || i.getID() == LegendsQuestIrvigSenay.A_LUMP_OF_CRYSTAL || i.getID() == LegendsQuestRanalphDevere.A_HUNK_OF_CRYSTAL || i.getID() == LegendsQuestInvAction.A_RED_CRYSTAL || i.getID() == LegendsQuestInvAction.A_RED_CRYSTAL + 9) {
			return true;
		}
		if(i.getID() == 1267) {
			return true;
		}
		return false;
	}

	@Override
	public void onDrop(Player p, Item i) {
		if(i.getID() == 1267) {
			removeItem(p, 1267, 1);
			p.message("You drop the bowl on the floor and the water spills out everywhere.");
			createGroundItem(1266, 1, p.getX(), p.getY());
		}
		if(i.getID() == LegendsQuestSanTojalon.A_CHUNK_OF_CRYSTAL || i.getID() == LegendsQuestIrvigSenay.A_LUMP_OF_CRYSTAL || i.getID() == LegendsQuestRanalphDevere.A_HUNK_OF_CRYSTAL || i.getID() == LegendsQuestInvAction.A_RED_CRYSTAL || i.getID() == LegendsQuestRanalphDevere.A_HUNK_OF_CRYSTAL || i.getID() == LegendsQuestInvAction.A_RED_CRYSTAL + 9) {
			removeItem(p, i.getID(), 1);
			message(p, 600, "The crystal starts fading..");
		}
	}
}
