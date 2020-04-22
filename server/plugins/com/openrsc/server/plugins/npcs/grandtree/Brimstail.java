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
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BRIMSTAIL.id();
	}

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		player.setBusy(true);
		say(player, "Hello");
		delay(player.getWorld().getServer().getConfig().GAME_TICK * 3);
		player.message("The gnome is chanting");
		delay(player.getWorld().getServer().getConfig().GAME_TICK * 3);
		player.message("he does not respond");
		player.setBusy(false);
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player player) {
		return obj.getID() == 667;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player player) {
		player.setBusy(true);
		player.message("you enter the cave");
		delay(player.getWorld().getServer().getConfig().GAME_TICK * 3);
		player.message("it leads to a ladder");
		delay(player.getWorld().getServer().getConfig().GAME_TICK * 3);
		player.message("you climb down");
		player.teleport(730, 3334, false);
		player.setBusy(false);
	}
}
