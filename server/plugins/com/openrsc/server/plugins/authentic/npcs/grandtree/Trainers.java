package com.openrsc.server.plugins.authentic.npcs.grandtree;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Trainers implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return inArray(n.getID(), NpcId.GNOME_TRAINER_ENTRANCE.id(), NpcId.GNOME_TRAINER_STARTINGNET.id(), NpcId.GNOME_TRAINER_PLATFORM.id(), NpcId.GNOME_TRAINER_ENDINGNET.id());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.GNOME_TRAINER_ENTRANCE.id()) {
			say(player, n, "hello, what is this place?");
			npcsay(player, n, "this my friend, is where we train",
					"it improves our agility, an essential skill");
			say(player, n, "looks easy enough");
			npcsay(player, n, "if you complete the course...",
					"from the slippery log to the end",
					"your agilty will increase much faster..",
					".. than repeating one obstical");
		} else if (n.getID() == NpcId.GNOME_TRAINER_STARTINGNET.id()) {
			say(player, n, "hello");
			npcsay(player, n, "this isn't a granny's tea party",
					"let's see some sweat human",
					"go, go ,go ,go");
		} else if (n.getID() == NpcId.GNOME_TRAINER_PLATFORM.id()) {
			say(player, n, "this is fun");
			npcsay(player, n, "this is training soldier",
					"if you want fun, go make some cocktails");
		} else if (n.getID() == NpcId.GNOME_TRAINER_ENDINGNET.id()) {
			say(player, n, "hello");
			npcsay(player, n, "hi");
			say(player, n, "how are you?");
			npcsay(player, n, "im amazed by how much you humans chat",
					"the sign say's training area...",
					"..not pointless conversation area",
					"now move it soldier");
		}
	}

}
