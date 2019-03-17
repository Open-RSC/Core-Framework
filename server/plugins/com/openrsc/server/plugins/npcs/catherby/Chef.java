package com.openrsc.server.plugins.npcs.catherby;

import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.Gauntlets;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.quests.members.FamilyCrest.getGauntletEnchantment;

public class Chef implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		switch (p.getQuestStage(Quests.FAMILY_CREST)) {
			case -1:
				npcTalk(p, n, "I hear you have brought the completed crest to my father",
					"Impressive work I must say");
				if (hasItem(p, ItemId.STEEL_GAUNTLETS.id()) && getGauntletEnchantment(p) == Gauntlets.STEEL.id()) {
					playerTalk(p, n, "My Father says you can improve these gauntlets for me");
					npcTalk(p, n, "Yes that is true",
						"I can change them to gauntlets of cooking",
						"Wearing them means you will burn your lobsters, swordish and shark less");
					int menu = showMenu(p, n,
						"Yes please do that for me",
						"I'll see what your brothers have to offer first");
					if (menu == 0) {
						message(p, "Caleb holds the gauntlets and closes his eyes",
							"Caleb concentrates",
							"Caleb hands the gauntlets to you");
						p.getInventory().replace(ItemId.STEEL_GAUNTLETS.id(), ItemId.GAUNTLETS_OF_COOKING.id());
						p.getCache().set("famcrest_gauntlets", Gauntlets.COOKING.id());
					} else if (menu == 1) {
						npcTalk(p, n, "Ok suit yourself");
					}
				}
				return;
			case 0:
			case 1:
				npcTalk(p, n, "Who are you? What are you after?");
				String[] menuOps = new String[]{
					"Are you Caleb Fitzharmon?",
					"Nothing, I will be on my way", "I see you are a chef, will you cook me anything?"
				};
				if (p.getQuestStage(Quests.FAMILY_CREST) == 0) {
					menuOps = new String[]{
						"Nothing, I will be on my way", "I see you are a chef, will you cook me anything?"
					};
					int choice = showMenu(p, n, false, menuOps);
					if (choice >= 0) {
						initialDialogue(p, n, choice + 1);
					}
				} else {
					int choice = showMenu(p, n, false, menuOps);
					initialDialogue(p, n, choice);
				}
				break;
			case 2:
				npcTalk(p, n, "How is the fish collecting going?");
				if (!hasItem(p, ItemId.SWORDFISH.id()) && !hasItem(p, ItemId.BASS.id()) && !hasItem(p, ItemId.TUNA.id())
						&& !hasItem(p, ItemId.SALMON.id()) && !hasItem(p, ItemId.SHRIMP.id())) {
					playerTalk(p, n, "I haven't got all the fish yet");
					npcTalk(p, n, "Remember I want cooked swordfish, bass, tuna, salmon and shrimp");
				} else {
					playerTalk(p, n, "Yes i have all of that now");
					message(p, "You give all of the fish to Caleb");
					removeItem(p, ItemId.SWORDFISH.id(), 1);
					removeItem(p, ItemId.BASS.id(), 1);
					removeItem(p, ItemId.TUNA.id(), 1);
					removeItem(p, ItemId.SALMON.id(), 1);
					removeItem(p, ItemId.SHRIMP.id(), 1);
					p.message("Caleb gives you his piece of the crest");
					addItem(p, ItemId.CREST_FRAGMENT_ONE.id(), 1);
					p.getCache().store("skipped_menu", true);
					p.updateQuestStage(Quests.FAMILY_CREST, 3);
					int m = showMenu(p, n,
						"Err what happened to the rest of it?",
						"Thankyou very much");
					if (m == 0) {
						npcTalk(p, n, "Well we had a bit of a fight over it",
							"We all wanted to be the heir of our fathers lands",
							"we each ended up with a piece of the crest",
							"none of us wanted to give their piece of the crest up to any of the others",
							"And none of us wanted to face our father",
							"coming home without a complete crest");
						playerTalk(p, n, "So do you know where I could find any of your brothers?");
						npcTalk(p, n, "Well we haven't really kept in touch",
							"What with all falling out over the crest",
							"I did hear from my brother Avan about a year ago though",
							"He said he was a living in a town in the desert",
							"Ask around the desert and you may find him",
							"My brother has very expensive tastes",
							"He may not give up the crest easily");
						p.getCache().remove("skipped_menu");
					}
				}
				return;
			case 3:
				if (p.getCache().hasKey("skipped_menu")) {
					npcTalk(p, n, "Hello again, I'm just putting the finishing touches to my salad");
					int menu = showMenu(p, n, "Err what happened to the rest of the crest?", "Good luck with that then");
					if (menu == 0) {
						npcTalk(p, n, "Well we had a bit of a fight over it",
							"We all wanted to be the heir of our fathers lands",
							"we each ended up with a piece of the crest",
							"none of us wanted to give their piece of the crest up to any of the others",
							"And none of us wanted to face our father",
							"coming home without a complete crest");
						playerTalk(p, n, "So do you know where I could find any of your brothers?");
						npcTalk(p, n, "Well we haven't really kept in touch",
							"What with all falling out over the crest",
							"I did hear from my brother Avan about a year ago though",
							"He said he was a living in a town in the desert",
							"Ask around the desert and you may find him",
							"My brother has very expensive tastes",
							"He may not give up the crest easily");
						p.getCache().remove("skipped_menu");
					}
					return;
				}
				playerTalk(p, n, "Where did you say I could find Avan?");
				npcTalk(p, n, "He said he was a living in a town in the desert",
					"Ask around the desert and you may find him");
				return;
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				npcTalk(p, n, "How are you doing getting the rest of the crest?");
				if (!hasItem(p, ItemId.CREST_FRAGMENT_ONE.id())) {
					int menu = showMenu(p, n,
						"I am still working on it",
						"I have lost the piece you gave me");
					if (menu == 0) {
						npcTalk(p, n, "Well good luck in your quest");
					} else if (menu == 1) {
						npcTalk(p, n, "Ah well here is another one");
						addItem(p, ItemId.CREST_FRAGMENT_ONE.id(), 1);
					}
				} else {
					playerTalk(p, n, "I am still working on it");
					npcTalk(p, n, "Well good luck in your quest");
				}
				return;
		}
	}

	public void initialDialogue(final Player p, final Npc n, int option) {
		if (option == 0) {
			playerTalk(p, n, "Are you Caleb Fitzharmon?");
			npcTalk(p, n, "I am he, and who might you be?");
			playerTalk(p, n, "I have been sent by your father",
				"He wants me to retrieve the Fitzharmon family crest");
			npcTalk(p, n, "Ah, yes hmm well I do have a bit of it yes");
			new Menu().addOptions(
				new Option("Err what happened to the rest of crest?") {
					public void action() {
						npcTalk(p, n, "Well we had a bit of a fight over it",
							"We all wanted to be the heir of our fathers lands",
							"we each ended up with a piece of the crest",
							"none of us wanted to give their piece of the crest up to any of the others",
							"And none of us wanted to face our father",
							"coming home without a complete crest");
						playerTalk(p, n, "So can I have your bit?");
						HAVE_YOUR_BIT(p, n);
					}
				},
				new Option("So can I have your bit?") {
					public void action() {
						HAVE_YOUR_BIT(p, n);
					}
				}).showMenu(p);
		} else if (option == 1) {
			playerTalk(p, n, "Nothing I will be on my way");
		} else if (option == 2) {
			playerTalk(p, n, "I see you are a chef", "Will you cook me anything?");
			npcTalk(p, n, "I would, but I am very busy",
				"Trying to prepare my special fish salad",
				"Which I hope will significantly increase my renown as a master chef");
		}
	}

	private void HAVE_YOUR_BIT(Player p, Npc n) {
		npcTalk(p, n, "Well I am the oldest son, by rights it is mine");
		playerTalk(p, n, "It's not a lot of use to you without the rest of it though");
		npcTalk(p, n, "Well true",
			"So I'll tell you what I'll do",
			"I am struggling to complete my seafood salad",
			"I don't seem to be able to get hold of the ingredients I need",
			"Help me and I'll help you");
		playerTalk(p, n, "What are you missing exactly?");
		npcTalk(p, n, "I need cooked swordfish,bass,tuna,salmon and shrimp");
		int menu = showMenu(p, n,
			"Ok I will get those",
			"Why don't you just give me the crest?");
		if (menu == 0) {
			p.updateQuestStage(Quests.FAMILY_CREST, 2);
		} else if (menu == 1) {
			npcTalk(p, n, "No I don't want to just give it away");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.CHEF.id();
	}

}
