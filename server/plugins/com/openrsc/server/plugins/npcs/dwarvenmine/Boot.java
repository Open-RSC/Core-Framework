package com.openrsc.server.plugins.npcs.dwarvenmine;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import static com.openrsc.server.plugins.Functions.npcTalk;

public class Boot implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Hello tall person");
		Menu defaultMenu = new Menu();
		if (p.getQuestStage(Quests.FAMILY_CREST) == 5) {
			defaultMenu.addOption(new Option("Hello I'm in search of very high quality gold") {
				@Override
				public void action() {
					npcTalk(p, n, "Hmm well the best gold I know of",
						"is east of the great city of Ardougne",
						"In some certain rocks underground there",
						"Its not the easiest of rocks to get to though I've heard");
					p.updateQuestStage(Quests.FAMILY_CREST, 6);
					// THEY MUST TALK TO THIS DWARF AND GET STAGE 6 OTHERWISE THEY WON'T BE ABLE TO MINE THE GOLD IN THE DUNGEON.
				}
			});
		}
		defaultMenu.addOption(new Option("Hello short person") {
			@Override
			public void action() {
				//NOTHING
			}
		});
		defaultMenu.addOption(new Option("Why are you called boot?") {
			@Override
			public void action() {
				npcTalk(p, n, "Because when I was a very young dwarf",
					"I used to sleep in a large boot");
			}
		});
		defaultMenu.showMenu(p);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.BOOT_THE_DWARF.id();
	}

}
