package com.openrsc.server.plugins.npcs.falador;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public class MakeOverMage implements TalkToNpcListener,
	TalkToNpcExecutiveListener {
	/**
	 * World instance
	 */
	public World world = World.getWorld();

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		npcTalk(p, n, "Are you happy with your looks?",
			"If not I can change them for the cheap cheap price",
			"Of 3000 coins");
		int opt = showMenu(p, n, "I'm happy with how I look thank you",
			"Yes change my looks please");
		if (opt == 1) {
			if (!hasItem(p, ItemId.COINS.id(), 3000)) {
				playerTalk(p, n, "I'll just go get the cash");
			} else {
				removeItem(p, ItemId.COINS.id(), 3000);
				p.setChangingAppearance(true);
				ActionSender.sendAppearanceScreen(p);
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.MAKE_OVER_MAGE.id();
	}

}
