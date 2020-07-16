package com.openrsc.server.plugins.authentic.quests.members.shilovillage;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class ShiloVillageTrufitusInvUse implements UseNpcTrigger {

	@Override
	public boolean blockUseNpc(Player player, Npc n, Item item) {
		return n.getID() == NpcId.TRUFITUS.id() && (inArray(item.getCatalogId(), ItemId.STONE_PLAQUE.id(), ItemId.CRUMPLED_SCROLL.id(), ItemId.TATTERED_SCROLL.id(), ItemId.ZADIMUS_CORPSE.id(),
				ItemId.BONE_KEY.id(), ItemId.BONE_SHARD.id(), ItemId.LOCATING_CRYSTAL.id(), ItemId.BERVIRIUS_TOMB_NOTES.id(), ItemId.SWORD_POMMEL.id(), ItemId.RASHILIYA_CORPSE.id()));
	}

	private void boneKeyWork(Player player, Npc n) {
		npcsay(player, n, "Does the key work?");
		int menu = multi(player, n,
			"Yes and I explored inside some sort of cavern.",
			"I don't know, I haven't tried it yet.");
		if (menu == 0) {
			npcsay(player, n, "How interesting Bwana, did you find anything?");
			int submenu = multi(player, n,
				"Not really.",
				"Yes, I found lots of things.");
			if (submenu == 0) {
				// unsure if also other stages
				if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
					npcsay(player, n, "Maybe you should go back and try to find some more things.",
						"Maybe there are more items to be found at Ah Za Rhoon?");
					return;
				}
				npcsay(player, n, "Maybe you should go back and try to find some more things.",
					"Show me any other items that you may have.",
					"We need any clue to locate Rashiliyia's resting place.");
			} else if (submenu == 1) {
				npcsay(player, n, "If you let me see them Bwana,",
					"perhaps I can offer you some extra information.");
			}
		} else if (menu == 1) {
			npcsay(player, n, "It may be an idea to try it and then scout out the area.",
				"If it relates to Rashiliyia, it might help us to defeat her.");
		}
	}

	private void corpseBuriedChat(Player player, Npc n) {
		npcsay(player, n, "Ah, interesting, so you think that Zadimus gave you the bone?",
			"What makes you say that?");
		int alt = multi(player, n,
			"He said something after he gave it to me.",
			"I'm not sure.");
		if (alt == 0) {
			npcsay(player, n, "What did he say?");
			int opt = multi(player, n, false, //do not send over
				"The spirit said something about keys and kin?",
				"The spirit rambled on about some nonsense.");
			if (opt == 0) {
				say(player, n, " \"The spirit said something about keys and kin?\"");
				npcsay(player, n, "Hmmm, maybe it's a clue of some kind?",
					"Well, Rashiliyias only kin, Bervirius, is entombed",
					"on a small island which lies to the South West.",
					"I will do some research into this as well.",
					"But I think we must take this clue literally",
					"and get some item that belonged to Bervirius",
					"as it may be the only way to approach Rashiliyia.");
				if (player.getQuestStage(Quests.SHILO_VILLAGE) == 4) {
					player.updateQuestStage(Quests.SHILO_VILLAGE, 5);
				}
			} else if (opt == 1) {
				say(player, n, "The spirit rambled on about some nonsense.");
				npcsay(player, n, "Oh, so it most likely was not very important then?");
			}
		} else if (alt == 1) {
			npcsay(player, n, "Oh, right.",
				"Come back and talk with me if you get an idea.");
		}
	}

	private void bronzeNecklaceChat(Player player, Npc n) {
		npcsay(player, n, "Well, Bwana, I would guess that you would need",
			"to get some bronze metal and work it into something",
			"that could be turned into a necklace?");
		int option3 = multi(player, n,
			"What should I put on the necklace?",
			"Thanks!");
		if (option3 == 0) {
			putOnNecklaceChat(player, n);
		} else if (option3 == 1) {
			npcsay(player, n, "You're more than welcome Bwana!",
				"Good luck for the rest of your quest.");
		}
	}

	private void putOnNecklaceChat(Player player, Npc n) {
		npcsay(player, n, "Perhaps Zadimus's clue has the answer?",
			"Now, what was it that he said again?",
			"Something about kin and keys?");
		int option2 = multi(player, n,
			"How do I make a bronze necklace?",
			"Thanks!");
		if (option2 == 0) {
			bronzeNecklaceChat(player, n);
		} else if (option2 == 1) {
			npcsay(player, n, "You're more than welcome Bwana!",
				"Good luck for the rest of your quest.");
		}
	}

	private void offMyHandsChat(Player player, Npc n) {
		npcsay(player, n, "I dare not take them, I may be taken",
			"over by the evil spirit of Rashiliyia!");
		int opt = multi(player, n,
			"What should I do with them?",
			"Thanks!");
		if (opt == 0) {
			doWithThemChat(player, n);
		} else if (opt == 1) {
			npcsay(player, n, "You're more than welcome Bwana!",
				"Good luck for the rest of your quest.");
		}
	}

	private void doWithThemChat(Player player, Npc n) {
		npcsay(player, n, "Hmm, I'm not exactly sure...",
			"perhaps there is a clue in one ",
			"of the artifacts you have found?");
		int opt2 = multi(player, n,
			"Can you take them off my hands?",
			"Thanks!");
		if (opt2 == 0) {
			offMyHandsChat(player, n);
		} else if (opt2 == 1) {
			npcsay(player, n, "You're more than welcome Bwana!",
				"Good luck for the rest of your quest.");
		}
	}

	@Override
	public void onUseNpc(Player player, Npc n, Item item) {
		if (n.getID() == NpcId.TRUFITUS.id() && item.getCatalogId() == ItemId.RASHILIYA_CORPSE.id()) {
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				say(player, n, "Have a look at this.");
				npcsay(player, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			player.message("You show Trufitus the remains...");
			say(player, n, "Could you have a look at this..");
			npcsay(player, n, "This is truly incredible bwana...",
				"so these are the remains of the dread queen Rashiliyia?");
			say(player, n, "Yes, I think so.");
			int menu = multi(player, n,
				"What should I do with them?",
				"Can you take them off my hands?");
			if (menu == 0) {
				doWithThemChat(player, n);
			} else if (menu == 1) {
				offMyHandsChat(player, n);
			}
		}
		else if (n.getID() == NpcId.TRUFITUS.id() && item.getCatalogId() == ItemId.SWORD_POMMEL.id()) {
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				say(player, n, "Have a look at this.");
				npcsay(player, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			player.message("You show Trufitus the sword pommel.");
			npcsay(player, n, "It is a very nice item Bwana.",
				"It may be just what we need to gain access to Rashiliyias tomb.",
				"While you were away, I did some research",
				"Rashiliyia would spare the lives of those who wore bronze necklaces.",
				"This item may have some significance to Bervirius.",
				"Perhaps you can craft something from it that can help?",
				"My guess is that you will need some protection to enter her tomb!");
			int option = multi(player, n,
				"How do I make a bronze necklace?",
				"What should I put on the necklace?");
			if (option == 0) {
				bronzeNecklaceChat(player, n);
			} else if (option == 1) {
				putOnNecklaceChat(player, n);
			}
		}
		else if (n.getID() == NpcId.TRUFITUS.id() && item.getCatalogId() == ItemId.BERVIRIUS_TOMB_NOTES.id()) {
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				say(player, n, "Have a look at this.");
				npcsay(player, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			player.message("You hand the notes over to Trufitus.");
			npcsay(player, n, "Hmm, these notes are quite extraordinary Bwana.",
				"They give location details of Rashiliyias tomb, ",
				"and some information on how to use the crystal.",
				"The information is quite specific, North of Ah Za Rhoon!",
				"That's a great place to start looking!");
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == 6) {
				player.updateQuestStage(Quests.SHILO_VILLAGE, 7);
			}
		}
		else if (n.getID() == NpcId.TRUFITUS.id() && item.getCatalogId() == ItemId.LOCATING_CRYSTAL.id()) {
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				say(player, n, "Have a look at this.");
				npcsay(player, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			player.message("You show Trufitus the Locating Crystal");
			npcsay(player, n, "This is incredible Bwana,");
			say(player, n, "It is?");
			npcsay(player, n, "Absolutely!",
				"This will help you to locate the entrance to Rashiliyia's tomb.",
				"Simply activate it when you think you are near, and it should ",
				"glow different colours to show how near you are.");
		}
		else if (n.getID() == NpcId.TRUFITUS.id() && item.getCatalogId() == ItemId.BONE_KEY.id()) {
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				say(player, n, "Have a look at this.");
				npcsay(player, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			say(player, n, "Have a look at this!");
			npcsay(player, n, "This is amazing Bwana,the level of detail is incredible.",
				"Where did you find it?");
			int menu = multi(player, n,
				"I made it from the bone shard that Zadimus gave me.",
				"Do you know what it opens?");
			if (menu == 0) {
				npcsay(player, n, "How very inventive Bwana.",
					"You must have seen the lock to have crafted it so well.");
				boneKeyWork(player, n);
			} else if (menu == 1) {
				npcsay(player, n, "You must already know what it opens to have carved it",
					"so pefectly.",
					"Perhaps in your travels you have come ",
					"across some unique doors with a unique lock",
					"I hope this helps with your quest.");
			}
		}
		else if (n.getID() == NpcId.TRUFITUS.id() && item.getCatalogId() == ItemId.BONE_SHARD.id()) {
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				say(player, n, "Have a look at this.");
				npcsay(player, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			player.message("You show Trufitus the Bone Shard.");
			say(player, n, "Could you have a look at this please ?");
			mes("Trufitus looks at the object for a moment.");
			delay(3);
			npcsay(player, n, "It looks like a simple shard of bone.",
				"Why do you think it is significant ?");
			int menu = multi(player, n,
				"It appeared when I buried Zadimus's Corpse.",
				"No reason really.");
			if (menu == 0) {
				corpseBuriedChat(player, n);
			} else if (menu == 1) {
				npcsay(player, n, "Well why are you showing it to me then?");
				int sub_menu = multi(player, n, "It appeared when I buried Zadimus's Corpse.",
					"I'm not sure.");
				if (sub_menu == 0) {
					corpseBuriedChat(player, n);
				} else if (sub_menu == 1) {
					npcsay(player, n, "Oh, right.",
						"Come back and talk with me if you get an idea.");
				}
			}
		}
		else if (n.getID() == NpcId.TRUFITUS.id() && item.getCatalogId() == ItemId.CRUMPLED_SCROLL.id()) {
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				say(player, n, "Have a look at this.");
				npcsay(player, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			player.message("You hand the crumpled scroll to Trufitus.");
			say(player, n, "Have a look at this, tell me what you think.");
			npcsay(player, n, "I am speechless Bwana, this is truly ancient.",
				"Where did you find it?");
			say(player, n, "In an underground building of some sort.");
			npcsay(player, n, "You must truly have found the temple of Ah Za Rhoon!",
				"The scroll gives some interesting details about ",
				"Rashiliyia, some things I didn't know before.");
			player.message("Trufitus gives back the scroll.");
			int menu = multi(player, n,
				"Anything that can help?",
				"Ok, thanks!");
			if (menu == 0) {
				npcsay(player, n, "Hmmm, well just that part about the wards..");
				mes("Trufitus seems to drift off in thought.");
				delay(3);
				npcsay(player, n, "It may be possible to make a ward like that?",
					"But what is the best thing to make it from?");
				// having zadimus corpse prolly
				if(player.getCarriedItems().hasCatalogID(ItemId.ZADIMUS_CORPSE.id(), Optional.of(false))) {
					npcsay(player, n, "Now...what was it that Zadimus said...");
				} else {
					npcsay(player, n, "Perhaps you'll get some clues from other items?");
				}
			} else if (menu == 1) {
				npcsay(player, n, "You're quite welcome Bwana.");
			}
		}
		else if (n.getID() == NpcId.TRUFITUS.id() && item.getCatalogId() == ItemId.TATTERED_SCROLL.id()) {
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				say(player, n, "Have a look at this.");
				npcsay(player, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			player.message("You hand the Tattered Scroll to Trufitus");
			say(player, n, "What do you make of this?");
			npcsay(player, n, "Truly amazing Bwana, this scroll must be ancient.",
				"I am unsure if I get any more meaning from it than you though.",
				"Perhaps Bervirius' tomb is still accessible?");
			player.message("Trufitus hands the Tattered scroll back to you.");
		}
		else if (n.getID() == NpcId.TRUFITUS.id() && item.getCatalogId() == ItemId.ZADIMUS_CORPSE.id()) {
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				say(player, n, "Have a look at this.");
				npcsay(player, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			player.message("You show Trufitus the corpse.");
			say(player, n, "What do you make of this?");
			npcsay(player, n, "! GASP !",
				"That's incredible, where did you find it?");
			say(player, n, "I found the corpse in a decomposing gallows",
				"I get a very strange feeling every time I try to bury the body");
			npcsay(player, n, "Hmmm, that sounds very strange",
				"I sense a spirit in torment, you should try to bury the remains.");
			int menu = multi(player, n,
				"Is there any sacred ground around here?",
				"Can you dispose of this for me?");
			if (menu == 0) {
				npcsay(player, n, "The ground in the centre of the village is very sacred to us",
					"Maybe you could try there ?");
			} else if (menu == 1) {
				player.message("Trufitus pulls away");
				npcsay(player, n, "I dare not touch it. I am a spiritual man and",
					"the spirit of this being may possess me and ",
					"turn me into a minion of Rashiliyia.");
			}
		}
		else if (n.getID() == NpcId.TRUFITUS.id() && item.getCatalogId() == ItemId.STONE_PLAQUE.id()) {
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				say(player, n, "Have a look at this.");
				npcsay(player, n, "Hmmm, I'm not sure you will get much use out of this.",
					"Why not see if you can sell it in Shilo Village.");
				return;
			}
			player.message("You hand over the Stone Plaque to Trufitus.");
			say(player, n, "Can you decipher this please?");
			npcsay(player, n, "This is an ancient artifact!");
			player.message("Trufitus looks at the item in awe.");
			npcsay(player, n, "I can certainly try!",
				"Hmm, incredible, it seems very ancient,",
				"and mentions something about Zadimus and Ah Za Rhoon.",
				"It says,'Here lies the traitor Zadimus, let his spirit",
				"be forever tormented'");
			player.message("Trufitus hands the Stone Plaque back");
			npcsay(player, n, "If you have found anything else that you need help with",
					"please just let me know.");
		}
	}
}
