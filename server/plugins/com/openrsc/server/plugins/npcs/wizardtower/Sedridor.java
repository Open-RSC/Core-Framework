package com.openrsc.server.plugins.npcs.wizardtower;

import com.openrsc.server.Constants;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.quests.members.RuneMysteries;
import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;



public class Sedridor implements  TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p,n,"Welcome, adventurer, to the world-renowned Wizards' Tower",
			"How many I help you?");

		if (p.getQuestStage(Constants.Quests.RUNE_MYSTERIES) >= 2)
		{
			RuneMysteries.sedridorDialog(p,n);
			return;
		}
		ArrayList<String> menu = new ArrayList<>();
		menu.add("Nothing, thanks. I'm just looking around");
		menu.add("What are you doing down here?");
		if (Constants.GameServer.WANT_RUNECRAFTING && p.getQuestStage(Constants.Quests.RUNE_MYSTERIES) == 1)
		{
			menu.add("I'm looking for the head wizard.");
		}
		int choice = showMenu(p,n, menu.toArray(new String[menu.size()]));
		if (choice == 1)
		{
			playerTalk(p,n,"What are you doing down here?");
			npcTalk(p,n,"That is, indeed, a good question.",
			"Here in the cellar of the Wizards' Tower",
			"you find the remains of the old Wizards' Tower,",
				"destroyed by fire many years past by the",
				"treachery of the Zamorakians.",
				"Many mysteries were lost, which we are",
				"trying to rediscover. By building this",
				"tower on the remains of the old, we seek",
				"to show the world our dedication to the",
				"mysteries of magic. I am here sifting",
				"through fragments for knowledge of artefacts of our past.");
			playerTalk(p,n,"Have you found anything useful?");
			npcTalk(p,n,"Ah, that would be telling, adventurer.",
				"Anything I have found I cannot speak freely of,",
				"for fear of the treachery we have",
				"already seen once in the past.");
			choice = showMenu(p,n,"Okay, well I'll leave you to it.", "What do you mean, 'treachery'?");
			if (choice == 0)
			{
				playerTalk(p,n,"Okay, well, I'll leave you to it");
				return;
			} else if (choice == 1)
			{
				playerTalk(p,n,"What do you mean, 'treachery'?");
				npcTalk(p,n,"It is a long story. Many years ago, this Wizards' Tower",
					"was a focus of great learning, where mages studied together",
					"to learn the secrets behind the runes that allow us",
					"to use magic. Who makes them? Where do they come from?",
					"How many types are there? What spells can they produce?",
					"All of these questions and more are unknown to us,",
					"but were once known by our ancestors. Legends tell us",
					"that in the past, mages could fashion runes almost at will.");
				playerTalk(p,n,"But they cannot anymore?");
				npcTalk(p,n,"No, unfortunately not. Many years past, the wizards",
					"of Zamorak, the god of chaos, burned this tower to the ground.",
					"and all who were inside. To this day, we do not know why",
					"they did this terrible thing, but all of our research",
					"and our greatest magical minds were destroyed in one",
					"fell swoop. This is why I spend my time searching through",
					"the few remains of the glorious old tower. I hope to",
					"find something that will tell us more of the mysteries of",
					"the runes that we use daily, dwindling in supply",
					"with each use. I hope we may once again create our own",
					"runes, and the Wizards' Tower will return to its",
					"former position of glory!");
				playerTalk(p,n,"Right, I'll leave you to it.");
				npcTalk(p,n,"Goodbye, " + p.getUsername());
				playerTalk(p,n,"How did you know my name?");
				npcTalk(p,n,"Well, I AM the head wizard.");
				return;
			}
		} else if (choice == 2)
		{
			RuneMysteries.sedridorDialog(p,n);
		}

		switch (p.getQuestStage(Constants.Quests.RUNE_MYSTERIES))
		{
			case 1:

		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.SEDRIDOR.id();
	}
}
