package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;


public class PiratesTreasure implements QuestInterface, OpInvTrigger,
	TalkNpcTrigger, OpLocTrigger,
	UseLocTrigger, TeleportTrigger {

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
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player p) {
		// 2 Quest Points
		// 450 coins
		// Gold ring
		// Emerald
		give(p, ItemId.GOLD_RING.id(), 1);
		give(p, ItemId.EMERALD.id(), 1);
		give(p, ItemId.COINS.id(), 450);
		p.message("Well done you have completed the pirate treasure quest");
		incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.PIRATES_TREASURE), true);
		p.message("@gre@You haved gained 2 quest points!");
		p.updateQuestStage(this, -1);
	}

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player player) {
		return item.getCatalogId() == ItemId.BANANA.id() && obj.getID() == 182
				|| item.getCatalogId() == ItemId.KARAMJA_RUM.id() && obj.getID() == 182
				|| item.getCatalogId() == ItemId.CHEST_KEY.id() && obj.getID() == HECTORS_CHEST_CLOSED;
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (item.getCatalogId() == ItemId.BANANA.id() && obj.getID() == 182 && obj.getY() == 711) {
			if (p.getCache().hasKey("bananas")) {
				if (p.getCache().getInt("bananas") >= 10) {
					p.message(
						"the crate is already full");
					return;
				}
				if (p.getCarriedItems().remove(item) > -1) {
					p.message(
						"you put a banana in the crate");

					p.getCache().set("bananas",
						p.getCache().getInt("bananas") + 1);
				}
			} else {
				p.message("I have no reason to do that");
			}
		} else if (item.getCatalogId() == ItemId.KARAMJA_RUM.id() && obj.getID() == 182
			&& p.getQuestStage(this) > 0) {
			if (p.getCache().hasKey("bananas")) {
				if (p.getCarriedItems().remove(item) > -1) {
					p.message(
						"You stash the rum in the crate");
					if (!p.getCache().hasKey("rum_in_crate")) {
						p.getCache().store("rum_in_crate", true);
					}
				}
			}
		} else if (item.getCatalogId() == ItemId.CHEST_KEY.id() && obj.getID() == HECTORS_CHEST_CLOSED) {
			p.message("You unlock the chest");
			p.getWorld().replaceGameObject(obj,
				new GameObject(obj.getWorld(), obj.getLocation(), HECTORS_CHEST_OPEN, obj.getDirection(),
					obj.getType()));
			p.getWorld().delayedSpawnObject(obj.getLoc(), 3000);
			p.getCarriedItems().remove(new Item(ItemId.CHEST_KEY.id()));
			Functions.mes(p, "All that is in the chest is a message");
			Functions.mes(p, "You take the message from the chest");
			Functions.mes(p, "It says dig just behind the south bench in the park");
			p.updateQuestStage(this, 3);
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.REDBEARD_FRANK.id() || n.getID() == NpcId.LUTHAS.id();
	}

	public void frankDialogue(Player p, Npc n, int cID) {
		switch (p.getQuestStage(this)) {
			case 0:
				npcsay(p, n, "Arrrh Matey");
				int choice = multi(p, n,
					"I'm in search of treasure", "Arrrh",
					"Do you want to trade?");
				if (choice == 0) {
					npcsay(p, n, "Arrrh treasure you be after eh?",
						"Well I might be able to tell you where to find some.",
						"For a price");
					say(p, n, "What sort of price?");
					npcsay(p, n,
						"Well for example if you can get me a bottle of rum",
						"Not just any rum mind",
						"I'd like some rum brewed on Karamja island",
						"There's no rum like Karamja rum");
					p.updateQuestStage(this, 1);
				} else if (choice == 1) {
					npcsay(p, n, "Arrrh");
				} else if (choice == 2) {
					npcsay(p, n, "No, I've got nothing to trade");
				}
				break;
			case 1:
				npcsay(p, n, "Arrrh Matey",
					"Have Ye brought some rum for yer old mate Frank");
				if (!p.getCarriedItems().hasCatalogID(ItemId.KARAMJA_RUM.id())) {
					say(p, n, "No not yet");
					return;
				}
				say(p, n, "Yes I've got some");
				p.getCarriedItems().remove(new Item(ItemId.KARAMJA_RUM.id()));

				Functions.mes(p, "Frank happily takes the rum");
				npcsay(p,
					n,
					"Now a deals a deal, I'll tell ye about the treasure",
					"I used to serve under a pirate captain called One Eyed Hector",
					"Hector was a very succesful pirate and became very rich",
					"but about a year ago we were boarded by the Royal Asgarnian Navy",
					"Hector was killed along with many of the crew",
					"I was one of the few to escape", "And I escaped with this");
				Functions.mes(p, "Frank hands you a key");
				give(p, ItemId.CHEST_KEY.id(), 1);
				p.updateQuestStage(this, 2);
				npcsay(p, n, "This is Hector's key",
					"I believe it opens his chest",
					"In his old room in the blue moon inn in Varrock",
					"With any luck his treasure will be in there");
				int menu = multi(p, n,
					"Ok thanks, I'll go and get it",
					"So why didn't you ever get it?");
				if (menu == 1) {
					npcsay(p, n, "I'm not allowed in the blue moon inn",
						"Apparently I'm a drunken trouble maker");
				}
				break;
			case 2:
				npcsay(p, n, "Arrrh Matey");
				if (p.getCarriedItems().hasCatalogID(ItemId.CHEST_KEY.id(), Optional.empty()) || p.getBank().hasItemId(ItemId.CHEST_KEY.id())) {
					npcsay(p, n, "Arrrh Matey");
					int menu1 = multi(p, n, "Arrrh",
						"Do you want to trade?");
					if (menu1 == 0) {
						npcsay(p, n, "Arrrh");
					} else if (menu1 == 1) {
						npcsay(p, n, "No I've got nothing to trade");
					}
				} else {
					say(p, n, "I seem to have lost my chest key");
					npcsay(p, n, "Arrr silly you", "Fortunatly I took the precaution to have another one made");
					Functions.mes(p, "Frank hands you a chest key");
					give(p, ItemId.CHEST_KEY.id(), 1);
				}
				break;
			case 3:
			case -1:
				npcsay(p, n, "Arrrh Matey");
				int menu2 = multi(p, n, "Arrrh",
					"Do you want to trade?");
				if (menu2 == 0) {
					npcsay(p, n, "Arrrh");
				} else if (menu2 == 1) {
					npcsay(p, n, "No I've got nothing to trade");
				}
				break;
		}
	}

	public void luthasDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			if (!p.getCache().hasKey("bananas")) {
				npcsay(p, n,
					"Hello I'm Luthas, I run the banana plantation here");
				int choice = multi(p, n,
					"Could you offer me employment on your plantation?",
					"That customs officer is annoying isn't she?");
				if (choice == 0) {
					npcsay(p,
						n,
						"Yes, I can sort something out",
						"Yes there's a crate outside ready for loading up on the ship",
						"If you could fill it up with bananas",
						"I'll pay you 30 gold");
					p.getCache().set("bananas", 0);
				} else if (choice == 1) {
					luthasDialogue(p, n, Luthas.ANNOYING);
				}
			} else {
				if (p.getCache().getInt("bananas") >= 10) {
					say(p, n, "I've filled a crate with bananas");
					npcsay(p, n, "Well done here is your payment");
					p.message("Luthas hands you 30 coins");
					p.getCarriedItems().getInventory().add(new Item(ItemId.COINS.id(), 30));
					if (p.getCache().hasKey("bananas")) {
						p.getCache().remove("bananas");
					}
					if (p.getCache().hasKey("rum_in_crate")) {
						p.getCache().remove("rum_in_crate");
					}
					if (!p.getCache().hasKey("rum_delivered")) {
						p.getCache().store("rum_delivered", true);
					}
					int choice = multi(
						p,
						n,
						"Will you pay me for another crate full?",
						"Thankyou, I'll be on my way",
						"So where are these bananas going to be delivered to?",
						"That customs officer is annoying isn't she?");
					if (choice == 0) {
						p.getCache().set("bananas", 0);
						npcsay(p,
							n,
							"Yes certainly",
							"If you go outside you should see the old crate has been loaded on to the ship",
							"and there is another empty crate in it's place");
					} else if (choice == 2) {
						npcsay(p, n,
							"I sell them to Wydin who runs a grocery store in Port Sarim");
					} else if (choice == 3) {
						luthasDialogue(p, n, Luthas.ANNOYING);
					}
					return;
				}
				npcsay(p, n, "Have you completed your task yet?");
				int choice = multi(p, n,
					"What did I have to do again?",
					"No, the crate isn't full yet");
				if (choice == 0) {
					npcsay(p,
						n,
						"There's a crate outside ready for loading up on the ship",
						"If you could fill it up with bananas",
						"I'll pay you 30 gold");
				} else if (choice == 1) {
					npcsay(p, n, "Well come back when it is");
				}
			}

		}
		switch (cID) {
			case Luthas.ANNOYING:
				npcsay(p, n, "Well I know her pretty well",
					"She doesn't cause me any trouble any more",
					"She doesn't even search my export crates any more",
					"She knows they only contain bananas");
				break;
		}
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.LUTHAS.id()) {
			luthasDialogue(p, n, -1);
		} else if (n.getID() == NpcId.REDBEARD_FRANK.id()) {
			frankDialogue(p, n, -1);
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command,
							  Player player) {
		return obj.getID() == 182 || obj.getID() == 185;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		switch (obj.getID()) {
			case 182:
				String s = "";
				if (p.getCache().hasKey("bananas")) {
					int b = p.getCache().getInt("bananas");
					if (b == 0)
						s = "The crate is completely empty";
					else if (b < 10)
						s = "the crate is partially full of bananas";
					else
						s = "The crate is full of bananas";

				} else {
					s = "The crate is completely empty";
				}
				p.message(s);
				break;
			case 185:
				if (p.getCache().hasKey("rum_delivered") && p.getCache().getBoolean("rum_delivered")) {
					Functions.mes(p, "There are a lot of bananas in the crate",
							"You find your bottle of rum in amoungst the bananas");
					p.getCarriedItems().getInventory().add(new Item(ItemId.KARAMJA_RUM.id()));
					p.getCache().remove("rum_delivered");
				}
				Functions.mes(p, "Do you want to take a banana?");
				int wantabanana = multi(p, "Yes", "No");
				if (wantabanana == 0) {
					p.getCarriedItems().getInventory().add(new Item(ItemId.BANANA.id()));
					p.playerServerMessage(MessageType.QUEST, "you take a banana");
				}
				break;
		}

	}

	@Override
	public boolean blockOpInv(Item item, Player p, String command) {
		return (p.getY() == 548 && p.getX() > 287 && p.getX() < 291)
			&& item.getCatalogId() == ItemId.SPADE.id();
	}

	@Override
	public void onTeleport(Player p) {
		if (p.getCarriedItems().hasCatalogID(ItemId.KARAMJA_RUM.id()) && (p.getLocation().inKaramja())) {
			p.getCarriedItems().remove(new Item(ItemId.KARAMJA_RUM.id()));
		}
	}

	@Override
	public boolean blockTeleport(Player p) {
		return false;
	}

	@Override
	public void onOpInv(Item item, Player p, String command) {
		if (p.getQuestStage(this) != 3)
			return;
		if (p.isBusy())
			return;
		if ((p.getY() == 548 && p.getX() >= 287 && p.getX() <= 291)
			&& item.getCatalogId() == ItemId.SPADE.id()) {
			if (p.getX() == 290 || p.getX() == 289) {
				Npc wyson = ifnearvisnpc(p, NpcId.WYSON_THE_GARDENER.id(), 20);
				boolean dig = false;
				if (wyson != null) {
					wyson.getUpdateFlags().setChatMessage(new ChatMessage(wyson, "Hey leave off my flowers", p));
					delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
					wyson.setChasing(p);
					long start = System.currentTimeMillis();
					while (!p.inCombat()) {
						if (System.currentTimeMillis() - start > 2000) {
							dig = true;
							break;
						}
						delay(50);
					}
				} else {
					dig = true;
				}
				if (dig) {
					Functions.mes(p, "You dig a hole in the ground",
						"You find a little bag of treasure");
					p.sendQuestComplete(this.getQuestId());
				}
			}
		}
	}

	class Frank {
		public static final int TREASURE = 0;
	}

	class Luthas {
		public static final int ANNOYING = 0;
	}
}
