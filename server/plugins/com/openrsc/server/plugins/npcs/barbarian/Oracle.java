package com.openrsc.server.plugins.npcs.barbarian;

import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.npcTalk;

public final class Oracle implements TalkToNpcExecutiveListener,
	TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		Menu defaultMenu = new Menu();
		if (p.getQuestStage(Quests.DRAGON_SLAYER) == 2) {
			defaultMenu.addOption(new Option(
				"I seek a piece of the map of the isle of Crondor") {
				@Override
				public void action() {
					npcTalk(p, n, "The map's behind a door below",
						"But entering is rather tough",
						"And this is what you need to know",
						"You must hold the following stuff",
						"First a drink used by the mage",
						"Next some worm string, changed to sheet",
						"Then a small crustacean cage",
						"Last a bowl that's not seen heat");
				}
			});
		}
		defaultMenu.addOption(new Option(
			"Can you impart your wise knowledge to me oh oracle?") {
			public void action() {
				if (p.getQuestStage(Quests.DRAGON_SLAYER) == 2) {
					npcTalk(p, n, "You must search from within to find your true destiny");
				} else {
					int rand = DataConversions.random(0, 7);
					switch (rand) {
					case 0:
						npcTalk(p, n, "You must search from within to find your true destiny");
						break;
					case 1:
						npcTalk(p, n, "No crisps at the party");
						break;
					case 2:
						npcTalk(p, n, "It is cunning, almost foxlike");
						break;
					case 3:
						npcTalk(p, n, "Is it waking up time, I'm not quite sure");
						break;
					case 4:
						npcTalk(p, n, "When in Asgarnia do as the Asgarnians do");
						break;
					case 5:
						npcTalk(p, n, "The light at the end of the tunnel is the demon infested lava pit");
						break;
					case 6:
						npcTalk(p, n, "Watch out for cabbages they are green and leafy");
						break;
					case 7:
						npcTalk(p, n, "Too many cooks spoil the anchovie pizza");
						break;
					}
				}
			}
		});
		defaultMenu.showMenu(p);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.ORACLE.id();
	}
}
