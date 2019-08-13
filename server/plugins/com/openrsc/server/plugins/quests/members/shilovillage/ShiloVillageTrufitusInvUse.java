package com.openrsc.server.plugins.quests.members.shilovillage;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class ShiloVillageTrufitusInvUse implements InvUseOnNpcListener, InvUseOnNpcExecutiveListener {

	@Override
	public boolean blockInvUseOnNpc(Player p, Npc n, Item item) {
		return n.getID() == NpcId.TRUFITUS.id() && (inArray(item.getID(), ItemId.STONE_PLAQUE.id(), ItemId.CRUMPLED_SCROLL.id(), ItemId.TATTERED_SCROLL.id(), ItemId.ZADIMUS_CORPSE.id(),
				ItemId.BONE_SHARD.id(), ItemId.LOCATING_CRYSTAL.id(), ItemId.BERVIRIUS_TOMB_NOTES.id(), ItemId.SWORD_POMMEL.id(), ItemId.RASHILIYA_CORPSE.id()));
	}

	private void corpseBuriedChat(Player p, Npc n) {
		npcTalk(p, n, "Ah, interesting, so you think that Zadimus gave you the bone?",
			"What makes you say that?");
		int alt = showMenu(p, n,
			"He said something after he gave it to me.",
			"I'm not sure.");
		if (alt == 0) {
			npcTalk(p, n, "What did he say?");
			int opt = showMenu(p, n,
				"The spirit said something about keys and kin?",
				"The spirit rambled on about some nonsense.");
			if (opt == 0) {
				npcTalk(p, n, "Hmmm, maybe it's a clue of some kind?",
					"Well, Rashiliyias only kin, Bervirius, is entombed",
					"on a small island which lies to the South West.",
					"I will do some research into this as well.",
					"But I think we must take this clue literally",
					"and get some item that belonged to Bervirius",
					"as it may be the only way to approach Rashiliyia.");
				if (p.getQuestStage(Quests.SHILO_VILLAGE) == 4) {
					p.updateQuestStage(Quests.SHILO_VILLAGE, 5);
				}
			} else if (opt == 1) {
				npcTalk(p, n, "Oh, so it most likely was not very important then?");
			}
		} else if (alt == 1) {
			npcTalk(p, n, "Oh, right.",
				"Come back and talk with me if you get an idea.");
		}
	}

	private void bronzeNecklaceChat(Player p, Npc n) {
		npcTalk(p, n, "Well, Bwana, I would guess that you would need",
			"to get some bronze metal and work it into something",
			"that could be turned into a necklace?");
		int option3 = showMenu(p, n,
			"What should I put on the necklace?",
			"Thanks!");
		if (option3 == 0) {
			putOnNecklaceChat(p, n);
		} else if (option3 == 1) {
			npcTalk(p, n, "You're more than welcome Bwana!",
				"Good luck for the rest of your quest.");
		}
	}

	private void putOnNecklaceChat(Player p, Npc n) {
		npcTalk(p, n, "Perhaps Zadimus's clue has the answer?",
			"Now, what was it that he said again?",
			"Something about kin and keys?");
		int option2 = showMenu(p, n,
			"How do I make a bronze necklace?",
			"Thanks!");
		if (option2 == 0) {
			bronzeNecklaceChat(p, n);
		} else if (option2 == 1) {
			npcTalk(p, n, "You're more than welcome Bwana!",
				"Good luck for the rest of your quest.");
		}
	}

	private void offMyHandsChat(Player p, Npc n) {
		npcTalk(p, n, "I dare not take them, I may be taken",
			"over by the evil spirit of Rashiliyia!");
		int opt = showMenu(p, n,
			"What should I do with them?",
			"Thanks!");
		if (opt == 0) {
			doWithThemChat(p, n);
		} else if (opt == 1) {
			npcTalk(p, n, "You're more than welcome Bwana!",
				"Good luck for the rest of your quest.");
		}
	}

	private void doWithThemChat(Player p, Npc n) {
		npcTalk(p, n, "Hmm, I'm not exactly sure...",
			"perhaps there is a clue in one ",
			"of the artifacts you have found?");
		int opt2 = showMenu(p, n,
			"Can you take them off my hands?",
			"Thanks!");
		if (opt2 == 0) {
			offMyHandsChat(p, n);
		} else if (opt2 == 1) {
			npcTalk(p, n, "You're more than welcome Bwana!",
				"Good luck for the rest of your quest.");
		}
	}

	@Override
	public void onInvUseOnNpc(Player p, Npc n, Item item) {
		if (n.getID() == NpcId.TRUFITUS.id() && item.getID() == ItemId.RASHILIYA_CORPSE.id()) {
			if (p.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				playerTalk(p, n, "Have a look at this.");
				npcTalk(p, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			p.message("You show Trufitus the remains...");
			playerTalk(p, n, "Could you have a look at this..");
			npcTalk(p, n, "This is truly incredible bwana...",
				"so these are the remains of the dread queen Rashiliyia?");
			playerTalk(p, n, "Yes, I think so.");
			int menu = showMenu(p, n,
				"What should I do with them?",
				"Can you take them off my hands?");
			if (menu == 0) {
				doWithThemChat(p, n);
			} else if (menu == 1) {
				offMyHandsChat(p, n);
			}
		}
		else if (n.getID() == NpcId.TRUFITUS.id() && item.getID() == ItemId.SWORD_POMMEL.id()) {
			if (p.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				playerTalk(p, n, "Have a look at this.");
				npcTalk(p, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			p.message("You show Trufitus the sword pommel.");
			npcTalk(p, n, "It is a very nice item Bwana.",
				"It may be just what we need to gain access to Rashiliyias tomb.",
				"While you were away, I did some research",
				"Rashiliyia would spare the lives of those who wore bronze necklaces.",
				"This item may have some significance to Bervirius.",
				"Perhaps you can craft something from it that can help?",
				"My guess is that you will need some protection to enter her tomb!");
			int option = showMenu(p, n,
				"How do I make a bronze necklace?",
				"What should I put on the necklace?");
			if (option == 0) {
				bronzeNecklaceChat(p, n);
			} else if (option == 1) {
				putOnNecklaceChat(p, n);
			}
		}
		else if (n.getID() == NpcId.TRUFITUS.id() && item.getID() == ItemId.BERVIRIUS_TOMB_NOTES.id()) {
			if (p.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				playerTalk(p, n, "Have a look at this.");
				npcTalk(p, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			p.message("You hand the notes over to Trufitus.");
			npcTalk(p, n, "Hmm, these notes are quite extraordinary Bwana.",
				"They give location details of Rashiliyias tomb, ",
				"and some information on how to use the crystal.",
				"The information is quite specific, North of Ah Za Rhoon!",
				"That's a great place to start looking!");
			if (p.getQuestStage(Quests.SHILO_VILLAGE) == 6) {
				p.updateQuestStage(Quests.SHILO_VILLAGE, 7);
			}
		}
		else if (n.getID() == NpcId.TRUFITUS.id() && item.getID() == ItemId.LOCATING_CRYSTAL.id()) {
			if (p.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				playerTalk(p, n, "Have a look at this.");
				npcTalk(p, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			p.message("You show Trufitus the Locating Crystal");
			npcTalk(p, n, "This is incredible Bwana,");
			playerTalk(p, n, "It is?");
			npcTalk(p, n, "Absolutely!",
				"This will help you to locate the entrance to Rashiliyia's tomb.",
				"Simply activate it when you think you are near, and it should ",
				"glow different colours to show how near you are.");
		}
		else if (n.getID() == NpcId.TRUFITUS.id() && item.getID() == ItemId.BONE_SHARD.id()) {
			if (p.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				playerTalk(p, n, "Have a look at this.");
				npcTalk(p, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			p.message("You show Trufitus the Bone Shard.");
			playerTalk(p, n, "Could you have a look at this please ?");
			message(p, "Trufitus looks at the object for a moment.");
			npcTalk(p, n, "It looks like a simple shard of bone.",
				"Why do you think it is significant ?");
			int menu = showMenu(p, n,
				"It appeared when I buried Zadimus's Corpse.",
				"No reason really.");
			if (menu == 0) {
				corpseBuriedChat(p, n);
			} else if (menu == 1) {
				npcTalk(p, n, "Well why are you showing it to me then?");
				int sub_menu = showMenu(p, n, "It appeared when I buried Zadimus's Corpse.",
					"I'm not sure.");
				if (sub_menu == 0) {
					corpseBuriedChat(p, n);
				} else if (sub_menu == 1) {
					npcTalk(p, n, "Oh, right.",
						"Come back and talk with me if you get an idea.");
				}
			}
		}
		else if (n.getID() == NpcId.TRUFITUS.id() && item.getID() == ItemId.CRUMPLED_SCROLL.id()) {
			if (p.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				playerTalk(p, n, "Have a look at this.");
				npcTalk(p, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			p.message("You hand the crumpled scroll to Trufitus.");
			playerTalk(p, n, "Have a look at this, tell me what you think.");
			npcTalk(p, n, "I am speechless Bwana, this is truly ancient.",
				"Where did you find it?");
			playerTalk(p, n, "In an underground building of some sort.");
			npcTalk(p, n, "You must truly have found the temple of Ah Za Rhoon!",
				"The scroll gives some interesting details about ",
				"Rashiliyia, some things I didn't know before.");
			p.message("Trufitus gives back the scroll.");
			int menu = showMenu(p, n,
				"Anything that can help?",
				"Ok, thanks!");
			if (menu == 0) {
				npcTalk(p, n, "Hmmm, well just that part about the wards..");
				message(p, "Trufitus seems to drift off in thought.");
				npcTalk(p, n, "It may be possible to make a ward like that?",
					"But what is the best thing to make it from?");
				// having zadimus corpse prolly
				if(hasItem(p, ItemId.ZADIMUS_CORPSE.id())) {
					npcTalk(p, n, "Now...what was it that Zadimus said...");
				} else {
					npcTalk(p, n, "Perhaps you'll get some clues from other items?");
				}
			} else if (menu == 1) {
				npcTalk(p, n, "You're quite welcome Bwana.");
			}
		}
		else if (n.getID() == NpcId.TRUFITUS.id() && item.getID() == ItemId.TATTERED_SCROLL.id()) {
			if (p.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				playerTalk(p, n, "Have a look at this.");
				npcTalk(p, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			p.message("You hand the Tattered Scroll to Trufitus");
			playerTalk(p, n, "What do you make of this?");
			npcTalk(p, n, "Truly amazing Bwana, this scroll must be ancient.",
				"I am unsure if I get any more meaning from it than you though.",
				"Perhaps Bervirius' tomb is still accessible?");
			p.message("Trufitus hands the Tattered scroll back to you.");
		}
		else if (n.getID() == NpcId.TRUFITUS.id() && item.getID() == ItemId.ZADIMUS_CORPSE.id()) {
			if (p.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				playerTalk(p, n, "Have a look at this.");
				npcTalk(p, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			p.message("You show Trufitus the corpse.");
			playerTalk(p, n, "What do you make of this?");
			npcTalk(p, n, "! GASP !",
				"That's incredible, where did you find it?");
			playerTalk(p, n, "I found the corpse in a decomposing gallows",
				"I get a very strange feeling every time I try to bury the body");
			npcTalk(p, n, "Hmmm, that sounds very strange",
				"I sense a spirit in torment, you should try to bury the remains.");
			int menu = showMenu(p, n,
				"Is there any sacred ground around here?",
				"Can you dispose of this for me?");
			if (menu == 0) {
				npcTalk(p, n, "The ground in the centre of the village is very sacred to us",
					"Maybe you could try there ?");
			} else if (menu == 1) {
				p.message("Trufitus pulls away");
				npcTalk(p, n, "I dare not touch it. I am a spiritual man and",
					"the spirit of this being may possess me and ",
					"turn me into a minion of Rashiliyia.");
			}
		}
		else if (n.getID() == NpcId.TRUFITUS.id() && item.getID() == ItemId.STONE_PLAQUE.id()) {
			if (p.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				playerTalk(p, n, "Have a look at this.");
				npcTalk(p, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			p.message("You hand over the Stone Plaque to Trufitus.");
			playerTalk(p, n, "Can you decipher this please?");
			npcTalk(p, n, "This is an ancient artifact!");
			p.message("Trufitus looks at the item in awe.");
			npcTalk(p, n, "I can certainly try!",
				"Hmm, incredible, it seems very ancient,",
				"and mentions something about Zadimus and Ah Za Rhoon.",
				"It says,'Here lies the traitor Zadimus, let his spirit",
				"be forever tormented'");
			p.message("Trufitus hands the Stone Plaque back");
			npcTalk(p, n, "If you have found anything else that you need help with",
					"please just let me know.");
		}
	}
}
