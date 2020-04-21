package com.openrsc.server.plugins.npcs.grandtree;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Brimstail implements TalkNpcTrigger, OpLocTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.BRIMSTAIL.id();
	}

	@Override
	public void onTalkNpc(Player p, final Npc n) {
		p.setBusy(true);
		say(p, "Hello");
		delay(p.getWorld().getServer().getConfig().GAME_TICK * 3);
		p.message("The gnome is chanting");
		delay(p.getWorld().getServer().getConfig().GAME_TICK * 3);
		p.message("he does not respond");
		p.setBusy(false);
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player player) {
		return obj.getID() == 667;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		p.setBusy(true);
		p.message("you enter the cave");
		delay(p.getWorld().getServer().getConfig().GAME_TICK * 3);
		p.message("it leads to a ladder");
		delay(p.getWorld().getServer().getConfig().GAME_TICK * 3);
		p.message("you climb down");
		p.teleport(730, 3334, false);
		p.setBusy(false);
	}
}
