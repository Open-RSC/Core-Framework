package com.openrsc.server.plugins.authentic.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class DragonSlayer implements QuestInterface, UseLocTrigger,
	UseInvTrigger,
	OpBoundTrigger,
	OpLocTrigger,
	TalkNpcTrigger,
	KillNpcTrigger {
	/*
	 * Ship: -Arrived: 281, 3472
	 *
	 * Crandor: -409, 638
	 */

	public static final int PORT_SARIM = 0;
	public static final int CRANDOR = 1;

	private static final int DWARVEN_CHEST_OPEN = 230;
	private static final int DWARVEN_CHEST_CLOSED = 231;
	private static final int MELZAR_CHEST_OPEN = 228;
	private static final int MELZAR_CHEST_CLOSED = 229;
	private static final int LUMBRIDGE_LADY_SARIM1 = 224;
	private static final int LUMBRIDGE_LADY_SARIM2 = 225;
	private static final int LUMBRIDGE_LADY_CRANDOR1 = 233;
	private static final int LUMBRIDGE_LADY_CRANDOR2 = 234;
	private static final int BOATS_LADDER = 227;

	@Override
	public int getQuestId() {
		return Quests.DRAGON_SLAYER;
	}

	@Override
	public String getQuestName() {
		return "Dragon slayer";
	}

	@Override
	public int getQuestPoints() {
		return Quest.DRAGON_SLAYER.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the dragon slayer quest");
		final QuestReward reward = Quest.DRAGON_SLAYER.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		player.teleport(410, 3481, false);
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.OZIACH.id() && player.getQuestStage(this) != -1;
	}

	public void oziachDialogue(Player player, Npc n, int cID) {
		if (cID == -1) {
			switch (player.getQuestStage(this)) {
				case 2:
					npcsay(player, n, "So how is thy quest going?");
					int map = multi(player, n,
						"So where can I find this dragon?",
						"Where can I get an antidragon shield?");
					if (map == 0) {
						oziachDialogue(player, n, Oziach.FIND_DRAGON);
					} else if (map == 1) {
						oziachDialogue(player, n, Oziach.ANTIDRAGON_SHIELD);
					}
					break;
				case 1:
					npcsay(player, n, "Aye tiz a fair day my friend");
					int menu = multi(player, n,
						"Can you sell me some rune plate mail?",
						"I'm not your friend", "Yes it's a very nice day");
					if (menu == 0) {
						npcsay(player, n, "Soo how does thee know I 'ave some?");
						int sub_menu = multi(player, n,
							"The guildmaster of the champion guild told me",
							"I am a master detective");
						if (sub_menu == 0) {
							npcsay(player,
								n,
								"Well if you're worthy of his advise",
								"You must have something going for you",
								"He has been known to let some weeklin's into his guild though",
								"I don't want just any old pumpkinmush to have this armour",
								"Jus cos they have a large amount of cash");
							oziachDialogue(player, n, Oziach.HERO);
						} else if (sub_menu == 1) {
							npcsay(player, n, "well however you found out about it");
							oziachDialogue(player, n, Oziach.HERO);
						}
					} else if (menu == 1) {
						npcsay(player, n,
							"I'd be suprised if your anyone's friend with that sort of manners");
					} else if (menu == 2) {
						npcsay(player, n, "Aye may the Gods walk by your side");
					}
					break;
				case 0:
					npcsay(player, n, "Aye tiz a fair day my friend");
					int menu2 = multi(player, n,
						"I'm not your friend", "Yes it's a very nice day");
					if (menu2 == 0) {
						npcsay(player, n,
							"I'd be suprised if your anyone's friend with that sort of manners");
					} else if (menu2 == 1) {
						npcsay(player, n, "Aye may the Gods walk by your side");
					}
					break;
			}
			return;
		}
		switch (cID) {
			case Oziach.ANTIDRAGON_SHIELD:
				npcsay(player, n,
					"I believe the Duke of Lumbridge Castle may have one in his armoury");
				int sub_menu4 = multi(player, n, "So where can I find this dragon?",
					"Ok I'll try and get everything together");
				if (sub_menu4 == 0) {
					oziachDialogue(player, n, Oziach.FIND_DRAGON);
				}
				if (sub_menu4 == 1) {
					npcsay(player, n, "Fare ye well");
				}
				break;
			case Oziach.DRAGON_FUN:
				npcsay(player, n, "Elvarg really is one of the most powerful dragons");
				npcsay(player, n,
					"I really wouldn't recommend charging in without special equipment");
				npcsay(player, n, "Her breath is the main thing to watch out for");
				npcsay(player, n, "You can get fried very fast");
				npcsay(player, n,
					"Unless you have a special flameproof antidragon shield");
				npcsay(player, n, "It won't totally protect you");
				npcsay(player, n, "but it should prevent some of the damage to you");
				player.updateQuestStage(this, 2);
				int funmenu = multi(player, n, "So where can I find this dragon?",
					"Where can I get an antidragon shield?");
				if (funmenu == 0) {
					oziachDialogue(player, n, Oziach.FIND_DRAGON);
				}
				if (funmenu == 1) {
					oziachDialogue(player, n, Oziach.ANTIDRAGON_SHIELD);
				}
				break;
			case Oziach.HERO:
				npcsay(player, n, "This is armour fit for a hero to be sure",
					"So you'll need to prove to me that you're a hero before you can buy some");
				int sub_menu2 = multi(player, n, "So how am I meant to prove that?",
					"That's a pity, I'm not a hero");
				if (sub_menu2 == 0)
					oziachDialogue(player, n, Oziach.PROVE_THAT);
				break;
			case Oziach.PROVE_THAT:
				npcsay(player, n, "Well if you want to prove yourself",
					"You could try and defeat Elvarg the dragon of the Isle of Crandor");
				int sub_menu3 = multi(player, n, false, "A dragon, that sounds like fun",
					"And will i need anything to defeat this dragon",
					"I may be a champion, but I don't think I'm up to dragon killing yet");
				if (sub_menu3 == 0) {
					say(player, n, "A dragon that sounds like fun");
					oziachDialogue(player, n, Oziach.DRAGON_FUN);
				} else if (sub_menu3 == 1) {
					say(player, n, "And will I need anything to defeat this dragon?");
					npcsay(player, n, "It's funny you shoud say that");
					oziachDialogue(player, n, Oziach.DRAGON_FUN);
				} else if (sub_menu3 == 2) {
					say(player, n, "I may be a champion, but I don't think I'm up to dragon killing yet");
					npcsay(player, n, "Yes I can understand that");
				}
				break;
			case Oziach.FIND_DRAGON:
				npcsay(player,
					n,
					"That is a problem too yes",
					"No one knows where the Isle of Crandor is located",
					"There was a map",
					"But it was torn up into three pieces",
					"Which are now scattered across Asgarnia",
					"You'll also struggle to find someone bold enough to take a ship to Crandor Island");
				int map = multi(player, n, "Where is the first piece of map?",
					"Where is the second piece of map?",
					"Where is the third piece of map?",
					"Where can I get an antidragon shield?");
				if (map == 0) {
					oziachDialogue(player, n, Oziach.FIRST_PIECE);
				} else if (map == 1) {
					oziachDialogue(player, n, Oziach.SECOND_PIECE);
				} else if (map == 2) {
					oziachDialogue(player, n, Oziach.THIRD_PIECE);
				} else if (map == 3) {
					oziachDialogue(player, n, Oziach.ANTIDRAGON_SHIELD);
				}

				break;
			case Oziach.FIRST_PIECE:
				npcsay(player, n, "deep in a strange building known as Melzar's maze");
				npcsay(player, n, "Located north west of Rimmington");
				if (!player.getCarriedItems().hasCatalogID(ItemId.MAZE_KEY.id(), Optional.of(false))) {
					npcsay(player, n, "You will need this to get in");
					npcsay(player, n,
						"This is the key to the front entrance to the maze");
					mes("Oziach hands you a key");
					delay(3);
					give(player, ItemId.MAZE_KEY.id(), 1);
				}
				int menu = multi(player, n, "Where can I get an antidragon shield?",
					"Where is the second piece of map?",
					"Where is the third piece of map?",
					"Ok I'll try and get everything together");
				if (menu == 0) {
					oziachDialogue(player, n, Oziach.ANTIDRAGON_SHIELD);
				} else if (menu == 1) {
					oziachDialogue(player, n, Oziach.SECOND_PIECE);
				} else if (menu == 2) {
					oziachDialogue(player, n, Oziach.THIRD_PIECE);
				} else if (menu == 3) {
					npcsay(player, n, "Fare ye well");
				}
				break;
			case Oziach.SECOND_PIECE:
				npcsay(player, n,
					"You will need to talk to the oracle on the ice mountain");
				int menu2 = multi(player, n, "Where can I get an antidragon shield?",
					"Where is the first piece of map?",
					"Where is the third piece of map?",
					"Ok I'll try and get everything together");
				if (menu2 == 0) {
					oziachDialogue(player, n, Oziach.ANTIDRAGON_SHIELD);
				} else if (menu2 == 1) {
					oziachDialogue(player, n, Oziach.FIRST_PIECE);
				} else if (menu2 == 2) {
					oziachDialogue(player, n, Oziach.THIRD_PIECE);
				} else if (menu2 == 3) {
					npcsay(player, n, "Fare ye well");
				}
				break;
			case Oziach.THIRD_PIECE:
				npcsay(player, n,
					"That was stolen by one of the goblins from the goblin village");
				int menu3 = multi(player, n, "Where can I get an antidragon shield?",
					"Where is the first piece of map?",
					"Where is the second piece of map?",
					"Ok I'll try and get everything together");
				if (menu3 == 0) {
					oziachDialogue(player, n, Oziach.ANTIDRAGON_SHIELD);
				} else if (menu3 == 1) {
					oziachDialogue(player, n, Oziach.FIRST_PIECE);
				} else if (menu3 == 2) {
					oziachDialogue(player, n, Oziach.SECOND_PIECE);
				} else if (menu3 == 3) {
					npcsay(player, n, "Fare ye well");
				}
				break;

		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return (obj.getY() == 643 && (obj.getID() == LUMBRIDGE_LADY_SARIM1 || obj.getID() == LUMBRIDGE_LADY_SARIM2))
			|| (obj.getY() == 641 && (obj.getID() == LUMBRIDGE_LADY_CRANDOR1 || obj.getID() == LUMBRIDGE_LADY_CRANDOR2))
			|| obj.getID() == BOATS_LADDER
			|| ((obj.getY() == 3458 || obj.getY() == 3331)
			&& (obj.getID() == MELZAR_CHEST_OPEN || obj.getID() == MELZAR_CHEST_CLOSED) || (obj.getID() == DWARVEN_CHEST_OPEN || obj.getID() == DWARVEN_CHEST_CLOSED));
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		switch (obj.getID()) {
			case DWARVEN_CHEST_OPEN:
			case DWARVEN_CHEST_CLOSED:
				if (command.equalsIgnoreCase("open")) {
					openGenericObject(obj, player, DWARVEN_CHEST_OPEN, "You open the chest");
				} else if (command.equalsIgnoreCase("close")) {
					closeGenericObject(obj, player, DWARVEN_CHEST_CLOSED, "You close the chest");
				} else {
					//kosher: could not "drop trick" easy, had to re-enter the door for another piece
					if (!player.getCarriedItems().hasCatalogID(ItemId.MAP_PIECE_3.id(), Optional.empty()) && player.getQuestStage(Quests.DRAGON_SLAYER) == 2
						&& player.getCache().hasKey("dwarven_unlocked")) {
						give(player, ItemId.MAP_PIECE_3.id(), 1);
						player.message("You find a piece of map in the chest");
						player.getCache().remove("dwarven_unlocked");
					} else {
						player.message("You find nothing in the chest");
					}
				}
				break;
			case MELZAR_CHEST_OPEN:
			case MELZAR_CHEST_CLOSED:
				if (command.equalsIgnoreCase("open")) {
					openGenericObject(obj, player, MELZAR_CHEST_OPEN, "You open the chest");
				} else if (command.equalsIgnoreCase("close")) {
					closeGenericObject(obj, player, MELZAR_CHEST_CLOSED, "You close the chest");
				} else {
					//kosher: could not "drop trick" easy, had to re-enter the door for another piece
					if (!player.getCarriedItems().hasCatalogID(ItemId.MAP_PIECE_2.id(), Optional.empty()) && player.getQuestStage(Quests.DRAGON_SLAYER) == 2
						&& player.getCache().hasKey("melzar_unlocked")) {
						give(player, ItemId.MAP_PIECE_2.id(), 1);
						player.message("You find a piece of map in the chest");
						player.getCache().remove("melzar_unlocked");
					} else {
						player.message("You find nothing in the chest");
					}
				}
				break;
			//clicking boat triggers klarense if available
			case LUMBRIDGE_LADY_SARIM1:
			case LUMBRIDGE_LADY_SARIM2:
				if (player.getCache().hasKey("owns_ship")) {
					//cases: a) ship not repaired and ned not hired -> teleport 259,3472 (first case when bought)
					//or player has enabled the crandor shortcut
					//b)ship repaired and ned not hired -> teleport 259,3493
					//c)ned hired and ship not repaired -> teleport 281,3472 (case when player is getting back)
					//location in c shared by location of arrival to crandor
					//d)ned hired and ship repaired -> teleport 281,3493
					player.getCache().set("lumb_lady", PORT_SARIM);
					if ((!player.getCache().hasKey("ship_fixed") && !player.getCache().hasKey("ned_hired")) || player.getCache().hasKey("crandor_shortcut")) {
						player.teleport(259, 3472, false);
					} else if (player.getCache().hasKey("ship_fixed") && !player.getCache().hasKey("ned_hired")) {
						player.teleport(259, 3493, false);
					} else if (!player.getCache().hasKey("ship_fixed") && player.getCache().hasKey("ned_hired")) {
						player.teleport(281, 3472, false);
					} else if (player.getCache().hasKey("ship_fixed") && player.getCache().hasKey("ned_hired")) {
						player.teleport(281, 3493, false);
					}
				} else {
					Npc klarense = ifnearvisnpc(player, NpcId.KLARENSE.id(), 15);
					if (klarense != null) {
						klarense.initializeTalkScript(player);
					} else {
						player.message("You must talk to the owner about this.");
					}
				}
				break;
			case BOATS_LADDER:
				if (player.getCache().hasKey("lumb_lady") && player.getCache().getInt("lumb_lady") == CRANDOR) {
					player.teleport(409, 638, false);
				} else {
					player.teleport(259, 641, false);
				}
				player.message("You leave the ship");
				break;
			case LUMBRIDGE_LADY_CRANDOR1:
			case LUMBRIDGE_LADY_CRANDOR2:
				player.getCache().set("lumb_lady", CRANDOR);
				if (player.getCache().hasKey("crandor_shortcut")) {
					player.teleport(259, 3472, false);
				} else {
					player.teleport(281, 3472, false);
				}
				break;
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.OZIACH.id()) {
			oziachDialogue(player, n, -1);
		}
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (n.getID() == NpcId.WORMBRAIN.id()) {
			player.getWorld().registerItem(
				new GroundItem(player.getWorld(), ItemId.MAP_PIECE_1.id(), n.getX(), n.getY(), 1, player));
		}
		if (n.getID() == NpcId.DRAGON.id() && player.getQuestStage(this) == 3) {
			player.sendQuestComplete(getQuestId());
		}
	}

	@Override
	public boolean blockKillNpc(Player player, Npc npc) {
		return DataConversions.inArray(new int[]{NpcId.WORMBRAIN.id(), NpcId.RAT_WMAZEKEY.id(),
			NpcId.GHOST_WMAZEKEY.id(), NpcId.SKELETON_WMAZEKEY.id(), NpcId.ZOMBIE_WMAZEKEY.id(),
			NpcId.MELZAR_THE_MAD.id(), NpcId.LESSER_DEMON_WMAZEKEY.id(), NpcId.DRAGON.id()}, npc.getID());
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return obj.getID() == 57 || obj.getID() == 58 || obj.getID() == 59 || obj.getID() == 60;
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == 57) {
			//special door dwarven mine
			if (player.getX() >= 259 && player.getCarriedItems().hasCatalogID(ItemId.WIZARDS_MIND_BOMB.id(), Optional.of(false))
				&& player.getCarriedItems().hasCatalogID(ItemId.SILK.id(), Optional.of(false))
				&& player.getCarriedItems().hasCatalogID(ItemId.LOBSTER_POT.id(), Optional.of(false))
				&& player.getCarriedItems().hasCatalogID(ItemId.UNFIRED_BOWL.id(), Optional.of(false))) {
				Point location = Point.location(player.getX(), player.getY());
				doDoor(obj, player);
				if (!player.getLocation().equals(location)) {
					player.getCarriedItems().remove(new Item(ItemId.WIZARDS_MIND_BOMB.id()));
					player.getCarriedItems().remove(new Item(ItemId.SILK.id()));
					player.getCarriedItems().remove(new Item(ItemId.LOBSTER_POT.id()));
					player.getCarriedItems().remove(new Item(ItemId.UNFIRED_BOWL.id()));
					player.getCache().store("dwarven_unlocked", true);
				}
			} else if (player.getX() <= 258) {
				doDoor(obj, player);
			} else {
				player.message("the door is locked");
			}
		} else if (obj.getID() == 58) {
			//from side of crandor
			if (player.getY() <= 3517) {
				player.message("You just went through a secret door");
				if (!player.getCache().hasKey("crandor_shortcut")) {
					player.message("You remember where the door is for future use");
					player.getCache().store("crandor_shortcut", true);
				}
				doDoor(obj, player, 11);
			} else {
				if (player.getCache().hasKey("crandor_shortcut")) {
					player.message("You just went through a secret door");
					doDoor(obj, player, 11);
				} else {
					player.message("nothing interesting happens");
				}
			}
		}
		//Door of Elvarg chamber
		else if (obj.getID() == 59) {
			if (player.getQuestStage(this) == 3 || player.getX() >= 414) {
				doDoor(obj, player);
			} else {
				player.playerServerMessage(MessageType.QUEST, "the door is locked");
			}
		} else if (obj.getID() == 60) {
			player.message("Nothing interesting happens");
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return DataConversions.inArray(new int[] {ItemId.MAP_PIECE_1.id(), ItemId.MAP_PIECE_2.id(), ItemId.MAP_PIECE_3.id()}, item1.getCatalogId())
				&& DataConversions.inArray(new int[] {ItemId.MAP_PIECE_1.id(), ItemId.MAP_PIECE_2.id(), ItemId.MAP_PIECE_3.id()}, item2.getCatalogId());
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (DataConversions.inArray(new int[] {ItemId.MAP_PIECE_1.id(), ItemId.MAP_PIECE_2.id(), ItemId.MAP_PIECE_3.id()}, item1.getCatalogId())
				&& DataConversions.inArray(new int[] {ItemId.MAP_PIECE_1.id(), ItemId.MAP_PIECE_2.id(), ItemId.MAP_PIECE_3.id()}, item2.getCatalogId())) {
			if (player.getCarriedItems().hasCatalogID(ItemId.MAP_PIECE_1.id(), Optional.of(false))
				&& player.getCarriedItems().hasCatalogID(ItemId.MAP_PIECE_2.id(), Optional.of(false))
				&& player.getCarriedItems().hasCatalogID(ItemId.MAP_PIECE_3.id(), Optional.of(false))) {
				player.getCarriedItems().remove(new Item(ItemId.MAP_PIECE_1.id()));
				player.getCarriedItems().remove(new Item(ItemId.MAP_PIECE_2.id()));
				player.getCarriedItems().remove(new Item(ItemId.MAP_PIECE_3.id()));
				give(player, ItemId.MAP.id(), 1);
			}
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == 226 || obj.getID() == 232;
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if ((obj.getID() == 226 || obj.getID() == 232) && item.getCatalogId() == ItemId.PLANK.id()) {
			// 226 hole of port sarim ship, 232 hole of crandor ship
			// however there are some odd teleports authentically and hence only viable check is with cache key
			if (player.getCache().hasKey("lumb_lady") && player.getCache().getInt("lumb_lady") == CRANDOR) {
				player.message("The ship doesn't seem easily repairable at the moment");
			} else {
				if (player.getCache().hasKey("crandor_shortcut")) {
					player.message("You don't need to mess about with broken ships");
					player.message("Now you have found that secret passage from Karamja");
				} else if (!player.getCache().hasKey("ship_repair") && ifheld(player, ItemId.NAILS.id(), 4)
					&& player.getCarriedItems().hasCatalogID(ItemId.PLANK.id(), Optional.of(false))) {
					player.message("You hammer the plank over the hole");
					player.message("You still need more planks to close the hole completely");
					player.getCarriedItems().remove(new Item(ItemId.NAILS.id(), 4));
					player.getCarriedItems().remove(new Item(ItemId.PLANK.id()));
					player.getCache().set("ship_repair", 1);
				} else if (ifheld(player, ItemId.NAILS.id(), 4) && player.getCarriedItems().hasCatalogID(ItemId.PLANK.id(), Optional.of(false))) {
					int planks_added = player.getCache().getInt("ship_repair");
					player.message("You hammer the plank over the hole");
					player.getCarriedItems().remove(new Item(ItemId.NAILS.id(), 4));
					player.getCarriedItems().remove(new Item(ItemId.PLANK.id()));
					if (planks_added + 1 == 3) {
						player.getCache().remove("ship_repair");
						player.getCache().store("ship_fixed", true);
						player.message("You board up the hole in the ship");
						player.teleport(281, 3493, false);
					} else {
						player.getCache().set("ship_repair", planks_added + 1);
						player.message("You still need more planks to close the hole completely");
					}
				} else if (!ifheld(player, ItemId.NAILS.id(), 4)) {
					player.message("You need 4 steel nails to attach the plank with");
				} else if (!player.getCarriedItems().hasCatalogID(ItemId.HAMMER.id(), Optional.of(false))) {
					player.message("You need a hammer to hammer the nails in with");
				} else {
					player.message("Nothing interesting happens");
				}
			}
		}
		//only accept planks used
		else if (obj.getID() == 226 || obj.getID() == 232) {
			player.message("Nothing interesting happens");
		}
	}

	class Oziach {
		public static final int THIRD_PIECE = 9;
		public static final int SECOND_PIECE = 8;
		public static final int FIRST_PIECE = 7;
		public static final int DRAGON_FUN = 6;
		public static final int ANTIDRAGON_SHIELD = 5;
		public static final int FIND_DRAGON = 4;
		public static final int WHAT_NEED = 3;
		public static final int NOT_HERO = 2;
		public static final int PROVE_THAT = 1;
		public static final int HERO = 0;
	}
}
