package com.openrsc.server.plugins.npcs.grandtree;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Trainers implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return inArray(n.getID(), NpcId.GNOME_TRAINER_ENTRANCE.id(), NpcId.GNOME_TRAINER_STARTINGNET.id(), NpcId.GNOME_TRAINER_PLATFORM.id(), NpcId.GNOME_TRAINER_ENDINGNET.id());
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GNOME_TRAINER_ENTRANCE.id()) {
			say(p, n, "hello, what is this place?");
			npcsay(p, n, "this my friend, is where we train",
					"it improves our agility, an essential skill");
			say(p, n, "looks easy enough");
			npcsay(p, n, "if you complete the course...",
					"from the slippery log to the end",
					"your agilty will increase much faster..",
					".. than repeating one obstical");
		} else if (n.getID() == NpcId.GNOME_TRAINER_STARTINGNET.id()) {
			say(p, n, "hello");
			npcsay(p, n, "this isn't a granny's tea party",
					"let's see some sweat human",
					"go, go ,go ,go");
		} else if (n.getID() == NpcId.GNOME_TRAINER_PLATFORM.id()) {
			say(p, n, "this is fun");
			npcsay(p, n, "this is training soldier",
					"if you want fun, go make some cocktails");
		} else if (n.getID() == NpcId.GNOME_TRAINER_ENDINGNET.id()) {
			say(p, n, "hello");
			npcsay(p, n, "hi");
			say(p, n, "how are you?");
			npcsay(p, n, "im amazed by how much you humans chat",
					"the sign say's training area...",
					"..not pointless conversation area",
					"now move it soldier");
		}
	}

}
