package com.openrsc.server.plugins.authentic.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;


public class PiratesTreasure implements QuestInterface,
	TalkNpcTrigger, OpLocTrigger, UseLocTrigger {

	private static final int HECTORS_CHEST_OPEN = 186;
	private static final int HECTORS_CHEST_CLOSED = 187;

	@Override
	public int getQuestId() {
		return Quests.PIRATES_TREASURE;
	}

	@Override
	public String getQuestName() {
		return "Pirate's treasure";
	}

	@Override
	public int getQuestPoints() {
		return Quest.PIRATES_TREASURE.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		// 2 Quest Points
		// 450 coins
		// Gold ring
		// Emerald
		give(player, ItemId.GOLD_RING.id(), 1);
		give(player, ItemId.EMERALD.id(), 1);
		give(player, ItemId.COINS.id(), 450);
		player.message("Well done you have completed the pirate treasure quest");
		final QuestReward reward = Quest.PIRATES_TREASURE.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		player.updateQuestStage(this, -1);
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return item.getCatalogId() == ItemId.BANANA.id() && obj.getID() == 182
				|| item.getCatalogId() == ItemId.KARAMJA_RUM.id() && obj.getID() == 182
				|| item.getCatalogId() == ItemId.CHEST_KEY.id() && obj.getID() == HECTORS_CHEST_CLOSED
				|| item.getCatalogId() == ItemId.SPADE.id() && obj.getID() == 188;
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (item.getCatalogId() == ItemId.BANANA.id() && obj.getID() == 182 && obj.getY() == 711) {
			if (player.getCache().hasKey("bananas")) {
				if (player.getCache().getInt("bananas") >= 10) {
					player.message(
						"the crate is already full");
					return;
				}
				if (player.getCarriedItems().remove(item) > -1) {
					player.message(
						"you put a banana in the crate");

					player.getCache().set("bananas",
						player.getCache().getInt("bananas") + 1);
				}
			} else {
				player.message("I have no reason to do that");
			}
		} else if (item.getCatalogId() == ItemId.KARAMJA_RUM.id() && obj.getID() == 182
			&& player.getQuestStage(this) > 0) {
			if (player.getCache().hasKey("bananas")) {
				if (player.getCarriedItems().remove(item) > -1) {
					player.message(
						"You stash the rum in the crate");
					if (!player.getCache().hasKey("rum_in_crate")) {
						player.getCache().store("rum_in_crate", true);
					}
				}
			}
		} else if (item.getCatalogId() == ItemId.CHEST_KEY.id() && obj.getID() == HECTORS_CHEST_CLOSED) {
			player.message("You unlock the chest");
			player.getWorld().replaceGameObject(obj,
				new GameObject(obj.getWorld(), obj.getLocation(), HECTORS_CHEST_OPEN, obj.getDirection(),
					obj.getType()));
			player.getWorld().delayedSpawnObject(obj.getLoc(), 3000);
			player.getCarriedItems().remove(new Item(ItemId.CHEST_KEY.id()));
			mes("All that is in the chest is a message");
			delay(3);
			mes("You take the message from the chest");
			delay(3);
			mes("It says dig just behind the south bench in the park");
			delay(3);
			player.updateQuestStage(this, 3);
		} else if (item.getCatalogId() == ItemId.SPADE.id() && obj.getID() == 188) { // It is authentic to not be able to activate spade to dig here. activate spade also produces no message.
			if (player.getQuestStage(this) != 3) {
				mes("It seems a shame to dig up these nice flowers for no reason");
				delay(3);
				return;
			}

			Npc wyson = ifnearvisnpc(player, NpcId.WYSON_THE_GARDENER.id(), 20);
			boolean dig = false;
			if (wyson != null) {
				wyson.getUpdateFlags().setChatMessage(new ChatMessage(wyson, "Hey leave off my flowers", player));
				delay(2);
				wyson.setChasing(player);
				long start = System.currentTimeMillis();
				while (!player.inCombat()) {
					if (System.currentTimeMillis() - start > 2000) {
						dig = true;
						break;
					}
					delay();
				}
			} else {
				dig = true;
			}
			if (dig) {
				mes("You dig a hole in the ground");
				delay(3);
				mes("You find a little bag of treasure");
				delay(3);
				player.sendQuestComplete(this.getQuestId());
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.REDBEARD_FRANK.id() || n.getID() == NpcId.LUTHAS.id();
	}

	public void frankDialogue(Player player, Npc n, int cID) {
		switch (player.getQuestStage(this)) {
			case 0:
				npcsay(player, n, "Arrrh Matey");
				int choice = multi(player, n,
					"I'm in search of treasure", "Arrrh",
					"Do you want to trade?");
				if (choice == 0) {
					npcsay(player, n, "Arrrh treasure you be after eh?",
						"Well I might be able to tell you where to find some.",
						"For a price");
					say(player, n, "What sort of price?");
					npcsay(player, n,
						"Well for example if you can get me a bottle of rum",
						"Not just any rum mind",
						"I'd like some rum brewed on Karamja island",
						"There's no rum like Karamja rum");
					player.updateQuestStage(this, 1);
				} else if (choice == 1) {
					npcsay(player, n, "Arrrh");
				} else if (choice == 2) {
					npcsay(player, n, "No, I've got nothing to trade");
				}
				break;
			case 1:
				npcsay(player, n, "Arrrh Matey",
					"Have Ye brought some rum for yer old mate Frank");
				if (!player.getCarriedItems().hasCatalogID(ItemId.KARAMJA_RUM.id())) {
					say(player, n, "No not yet");
					return;
				}
				say(player, n, "Yes I've got some");
				player.getCarriedItems().remove(new Item(ItemId.KARAMJA_RUM.id()));

				mes("Frank happily takes the rum");
				delay(3);
				npcsay(player,
					n,
					"Now a deals a deal, I'll tell ye about the treasure",
					"I used to serve under a pirate captain called One Eyed Hector",
					"Hector was a very succesful pirate and became very rich",
					"but about a year ago we were boarded by the Royal Asgarnian Navy",
					"Hector was killed along with many of the crew",
					"I was one of the few to escape", "And I escaped with this");
				mes("Frank hands you a key");
				delay(3);
				give(player, ItemId.CHEST_KEY.id(), 1);
				player.updateQuestStage(this, 2);
				npcsay(player, n, "This is Hector's key",
					"I believe it opens his chest",
					"In his old room in the blue moon inn in Varrock",
					"With any luck his treasure will be in there");
				int menu = multi(player, n,
					"Ok thanks, I'll go and get it",
					"So why didn't you ever get it?");
				if (menu == 1) {
					npcsay(player, n, "I'm not allowed in the blue moon inn",
						"Apparently I'm a drunken trouble maker");
				}
				break;
			case 2:
				npcsay(player, n, "Arrrh Matey");
				if (player.getCarriedItems().hasCatalogID(ItemId.CHEST_KEY.id(), Optional.empty()) || player.getBank().hasItemId(ItemId.CHEST_KEY.id())) {
					npcsay(player, n, "Arrrh Matey");
					int menu1 = multi(player, n, "Arrrh",
						"Do you want to trade?");
					if (menu1 == 0) {
						npcsay(player, n, "Arrrh");
					} else if (menu1 == 1) {
						npcsay(player, n, "No I've got nothing to trade");
					}
				} else {
					say(player, n, "I seem to have lost my chest key");
					npcsay(player, n, "Arrr silly you", "Fortunatly I took the precaution to have another one made");
					mes("Frank hands you a chest key");
					delay(3);
					give(player, ItemId.CHEST_KEY.id(), 1);
				}
				break;
			case 3:
			case -1:
				npcsay(player, n, "Arrrh Matey");
				int menu2 = multi(player, n, "Arrrh",
					"Do you want to trade?");
				if (menu2 == 0) {
					npcsay(player, n, "Arrrh");
				} else if (menu2 == 1) {
					npcsay(player, n, "No I've got nothing to trade");
				}
				break;
		}
	}

	public void luthasDialogue(Player player, Npc n, int cID) {
		if (cID == -1) {
			if (!player.getCache().hasKey("bananas")) {
				npcsay(player, n,
					"Hello I'm Luthas, I run the banana plantation here");
				int choice = multi(player, n,
					"Could you offer me employment on your plantation?",
					"That customs officer is annoying isn't she?");
				if (choice == 0) {
					npcsay(player,
						n,
						"Yes, I can sort something out",
						"Yes there's a crate outside ready for loading up on the ship",
						"If you could fill it up with bananas",
						"I'll pay you 30 gold");
					player.getCache().set("bananas", 0);
				} else if (choice == 1) {
					luthasDialogue(player, n, Luthas.ANNOYING);
				}
			} else {
				if (player.getCache().getInt("bananas") >= 10) {
					say(player, n, "I've filled a crate with bananas");
					npcsay(player, n, "Well done here is your payment");
					player.message("Luthas hands you 30 coins");
					player.getCarriedItems().getInventory().add(new Item(ItemId.COINS.id(), 30));
					if (player.getCache().hasKey("bananas")) {
						player.getCache().remove("bananas");
					}
					if (player.getCache().hasKey("rum_in_crate")) {
						player.getCache().remove("rum_in_crate");
					}
					if (!player.getCache().hasKey("rum_delivered")) {
						player.getCache().store("rum_delivered", true);
					}
					int choice = multi(
						player,
						n,
						"Will you pay me for another crate full?",
						"Thankyou, I'll be on my way",
						"So where are these bananas going to be delivered to?",
						"That customs officer is annoying isn't she?");
					if (choice == 0) {
						player.getCache().set("bananas", 0);
						npcsay(player,
							n,
							"Yes certainly",
							"If you go outside you should see the old crate has been loaded on to the ship",
							"and there is another empty crate in it's place");
					} else if (choice == 2) {
						npcsay(player, n,
							"I sell them to Wydin who runs a grocery store in Port Sarim");
					} else if (choice == 3) {
						luthasDialogue(player, n, Luthas.ANNOYING);
					}
					return;
				}
				npcsay(player, n, "Have you completed your task yet?");
				int choice = multi(player, n,
					"What did I have to do again?",
					"No, the crate isn't full yet");
				if (choice == 0) {
					npcsay(player,
						n,
						"There's a crate outside ready for loading up on the ship",
						"If you could fill it up with bananas",
						"I'll pay you 30 gold");
				} else if (choice == 1) {
					npcsay(player, n, "Well come back when it is");
				}
			}

		}
		switch (cID) {
			case Luthas.ANNOYING:
				npcsay(player, n, "Well I know her pretty well",
					"She doesn't cause me any trouble any more",
					"She doesn't even search my export crates any more",
					"She knows they only contain bananas");
				break;
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.LUTHAS.id()) {
			luthasDialogue(player, n, -1);
		} else if (n.getID() == NpcId.REDBEARD_FRANK.id()) {
			frankDialogue(player, n, -1);
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 182 || obj.getID() == 185;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		switch (obj.getID()) {
			case 182:
				String s = "";
				if (player.getCache().hasKey("bananas")) {
					int b = player.getCache().getInt("bananas");
					if (b == 0)
						s = "The crate is completely empty";
					else if (b < 10)
						s = "the crate is partially full of bananas";
					else
						s = "The crate is full of bananas";

				} else {
					s = "The crate is completely empty";
				}
				player.message(s);
				break;
			case 185:
				if (player.getCache().hasKey("rum_delivered") && player.getCache().getBoolean("rum_delivered")) {
					mes("There are a lot of bananas in the crate");
					delay(3);
					mes("You find your bottle of rum in amoungst the bananas");
					delay(3);
					player.getCarriedItems().getInventory().add(new Item(ItemId.KARAMJA_RUM.id()));
					player.getCache().remove("rum_delivered");
				}
				mes("Do you want to take a banana?");
				delay(3);
				int wantabanana = multi(player, "Yes", "No");
				if (wantabanana == 0) {
					player.getCarriedItems().getInventory().add(new Item(ItemId.BANANA.id()));
					player.playerServerMessage(MessageType.QUEST, "you take a banana");
				}
				break;
		}

	}

	class Frank {
		public static final int TREASURE = 0;
	}

	class Luthas {
		public static final int ANNOYING = 0;
	}
}
