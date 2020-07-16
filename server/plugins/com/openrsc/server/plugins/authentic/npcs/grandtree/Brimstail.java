package com.openrsc.server.plugins.authentic.npcs.grandtree;

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
		say(player, "Hello");
		delay(3);
		player.message("The gnome is chanting");
		delay(3);
		player.message("he does not respond");
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 667;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		player.message("you enter the cave");
		delay(3);
		player.message("it leads to a ladder");
		delay(3);
		player.message("you climb down");
		player.teleport(730, 3334, false);
	}
}
