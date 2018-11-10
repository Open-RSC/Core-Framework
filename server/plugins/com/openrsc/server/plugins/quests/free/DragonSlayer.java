package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.*;
import com.openrsc.server.plugins.listeners.executive.*;

import static com.openrsc.server.plugins.Functions.*;

/**
 * 
 * @author n0m
 * 
 */
public class DragonSlayer implements QuestInterface,InvUseOnObjectListener,
		InvUseOnObjectExecutiveListener, InvUseOnItemListener,
		InvUseOnItemExecutiveListener, WallObjectActionListener,
		WallObjectActionExecutiveListener, ObjectActionListener,
		ObjectActionExecutiveListener, TalkToNpcExecutiveListener,
		TalkToNpcListener, PlayerKilledNpcExecutiveListener {
	/*
	 * Ship: -Arrived: 281, 3472
	 * 
	 * Crandor: -409, 638
	 */

	private class QuestItems {
		public static final int RED_KEY = 390;
		public static final int ORANGE_KEY = 391;
		public static final int YELLOW_KEY = 392;
		public static final int BLUE_KEY = 393;
		public static final int MAGENTA_KEY = 394;
		public static final int BLACK_KEY = 395;
	}

	@Override
	public int getQuestId() {
		return Constants.Quests.DRAGON_SLAYER;
	}

	@Override
	public String getQuestName() {
		return "Dragon slayer";
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 187 && p.getQuestStage(this) != -1;
	}

	public void oziachDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			switch (p.getQuestStage(this)) {
			case 2:
				npcTalk(p, n, "So how is thy quest going?");
				int map = showMenu(p, n, new String[] {
						"So where can I find this dragon?",
						"Where can I get an antidragon shield?" });
				if (map == 0) {
					oziachDialogue(p, n, Oziach.FIND_DRAGON);
				} else if (map == 1) {
					oziachDialogue(p, n, Oziach.ANTIDRAGON_SHIELD);
				}
				break;
			case 1:
				npcTalk(p, n, "Aye tiz a fair day my friend");
				int menu = showMenu(p, n,
						"Can you sell me some rune plate mail?",
						"I'm not your friend", "Yes it's a very nice day");
				if (menu == 0) {
					npcTalk(p, n, "Soo how does thee know I 'ave some?");
					int sub_menu = showMenu(p, n,
							"The guildmaster of the champion guild told me",
							"I am a master detective");
					if (sub_menu == 0) {
						npcTalk(p,
								n,
								"Well if you're worthy of his advise",
								"You must have something going for you",
								"He has been known to let some weeklin's into his guild though",
								"I don't want just any old pumpkinmush to have this armour",
								"Jus cos they have a large amount of cash");
						oziachDialogue(p, n, Oziach.HERO);
					} else if (sub_menu == 1) {
						npcTalk(p, n, "well however you found out about it");
						oziachDialogue(p, n, Oziach.HERO);
					}
				} else if (menu == 1) {
					npcTalk(p, n,
							"I'd be suprised if your anyone's friend with that sort of manners");
				} else if (menu == 2) {
					npcTalk(p, n, "Aye may the Gods walk by your side");
				}
				break;
			case 0:
				npcTalk(p, n, "Aye tiz a fair day my friend");
				int menu2 = showMenu(p, n,
						"I'm not your friend", "Yes it's a very nice day");
				if (menu2 == 0) {
					npcTalk(p, n,
							"I'd be suprised if your anyone's friend with that sort of manners");
				} else if (menu2 == 1) {
					npcTalk(p, n, "Aye may the Gods walk by your side");
				} 
				break;
			}
			return;
		}
		switch (cID) {
		case Oziach.ANTIDRAGON_SHIELD:
			npcTalk(p, n,
					"I believe the Duke of Lumbridge Castle may have one in his armoury");
			int sub_menu4 = showMenu(p, n, "So where can I find this dragon?",
					"Ok I'll try and get everything together");
			if (sub_menu4 == 0) {
				oziachDialogue(p, n, Oziach.FIND_DRAGON);
			}
			if (sub_menu4 == 1) {
				npcTalk(p, n, "Fare ye well");
			}
			break;
		case Oziach.DRAGON_FUN:
			npcTalk(p, n, "Elvarg really is one of the most powerful dragons");
			npcTalk(p, n,
					"I really wouldn't recommend charging in without special equipment");
			npcTalk(p, n, "Her breath is the main thing to watch out for");
			npcTalk(p, n, "You can get fried very fast");
			npcTalk(p, n,
					"Unless you have a special flameproof antidragon shield");
			npcTalk(p, n, "It won't totally protect you");
			npcTalk(p, n, "but it should prevent some of the damage to you");
			p.updateQuestStage(this, 2);
			int funmenu = showMenu(p, n, "So where can I find this dragon?",
					"Where can I get an antidragon shield?");
			if (funmenu == 0) {
				oziachDialogue(p, n, Oziach.FIND_DRAGON);
			}
			if (funmenu == 1) {
				oziachDialogue(p, n, Oziach.ANTIDRAGON_SHIELD);
			}
			break;
		case Oziach.HERO:
			npcTalk(p, n, "This is armour fit for a hero to be sure",
					"So you'll need to prove to me that you're a hero before you can buy some");
			int sub_menu2 = showMenu(p, n, "So how am I meant to prove that?",
					"That's a pity, I'm not a hero");
			if (sub_menu2 == 0)
				oziachDialogue(p, n, Oziach.PROVE_THAT);
			break;
		case Oziach.PROVE_THAT:
			npcTalk(p, n, "Well if you want to prove yourself",
					"You could try and defeat Elvarg the dragon of the Isle of Crandor");
			int sub_menu3 = showMenu(p, n, false, "A dragon, that sounds like fun",
					"And will i need anything to defeat this dragon",
					"I may be a champion, but I don't think I'm up to dragon killing yet");
			if (sub_menu3 == 0) {
				playerTalk(p, n, "A dragon, that sounds like fun");
				oziachDialogue(p, n, Oziach.DRAGON_FUN);
			} else if (sub_menu3 == 1) {
				playerTalk(p, n, "And will I need anything to defeat this dragon?");
				npcTalk(p, n, "It's funny you shoud say that");
				oziachDialogue(p, n, Oziach.DRAGON_FUN);
			} else if (sub_menu3 == 2) {
				playerTalk(p, n, "I may be a champion, but I don't think I'm up to dragon killing yet");
				npcTalk(p, n, "Yes I can understand that");
			}
			break;
		case Oziach.FIND_DRAGON:
			npcTalk(p,
					n,
					"That is a problem too yes",
					"No one knows where the Isle of Crandor is located",
					"There was a map",
					"But it was torn up into three pieces",
					"Which are now scattered across Asgarnia",
					"You'll also struggle to find someone bold enough to take a ship to Crandor Island");
			int map = showMenu(p, n, "Where is the first piece of map?",
					"Where is the second piece of map?",
					"Where is the third piece of map?",
					"Where can I get an antidragon shield?");
			if (map == 0) {
				oziachDialogue(p, n, Oziach.FIRST_PIECE);
			} else if (map == 1) {
				oziachDialogue(p, n, Oziach.SECOND_PIECE);
			} else if (map == 2) {
				oziachDialogue(p, n, Oziach.THIRD_PIECE);
			} else if (map == 3) {
				oziachDialogue(p, n, Oziach.ANTIDRAGON_SHIELD);
			}

			break;
		case Oziach.FIRST_PIECE:
			npcTalk(p, n, "deep in a strange building known as Melzar's maze");
			npcTalk(p, n, "Located north west of Rimmington");
			if (!hasItem(p, 421, 1)) {
				npcTalk(p, n, "You will need this to get in");
				npcTalk(p, n,
						"This is the key to the front entrance to the maze");
				message(p, "Oziach hands you a key");
				addItem(p, 421, 1);
			}
			int menu = showMenu(p, n, "Where can I get an antidragon shield?",
					"Where is the second piece of map?",
					"Where is the third piece of map?",
					"Ok I'll try and get everything together");
			if (menu == 0) {
				oziachDialogue(p, n, Oziach.ANTIDRAGON_SHIELD);
			} else if (menu == 1) {
				oziachDialogue(p, n, Oziach.SECOND_PIECE);
			} else if (menu == 2) {
				oziachDialogue(p, n, Oziach.THIRD_PIECE);
			} else if (menu == 3) {
				npcTalk(p, n, "Fare ye well");
			}
			break;
		case Oziach.SECOND_PIECE:
			npcTalk(p, n,
					"You will need to talk to the oracle on the ice mountain");
			int menu2 = showMenu(p, n, "Where can I get an antidragon shield?",
					"Where is the first piece of map?",
					"Where is the third piece of map?",
					"Ok I'll try and get everything together");
			if (menu2 == 0) {
				oziachDialogue(p, n, Oziach.ANTIDRAGON_SHIELD);
			} else if (menu2 == 1) {
				oziachDialogue(p, n, Oziach.FIRST_PIECE);
			} else if (menu2 == 1) {
				oziachDialogue(p, n, Oziach.THIRD_PIECE);
			} else if (menu2 == 1) {
				npcTalk(p, n, "Fare ye well");
			}
			break;
		case Oziach.THIRD_PIECE:
			npcTalk(p, n,
					"That was stolen by one of the goblins from the goblin village");
			int menu3 = showMenu(p, n, "Where can I get an antidragon shield?",
					"Where is the first piece of map?",
					"Where is the second piece of map?",
					"Ok I'll try and get everything together");
			if (menu3 == 0) {
				oziachDialogue(p, n, Oziach.ANTIDRAGON_SHIELD);
			} else if (menu3 == 1) {
				oziachDialogue(p, n, Oziach.FIRST_PIECE);
			} else if (menu3 == 1) {
				oziachDialogue(p, n, Oziach.SECOND_PIECE);
			} else if (menu3 == 1) {
				npcTalk(p, n, "Fare ye well");
			}
			break;

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

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		return (obj.getY() == 643 && (obj.getID() == 224 || obj.getID() == 225))
				|| obj.getID() == 227
				|| ((obj.getY() == 3458 || obj.getY() == 3331)
						&& obj.getID() == 228 || obj.getID() == 230);
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		switch (obj.getID()) {
		case 230:
			if (!hasItem(p, 418, 1) && p.getQuestStage(Quests.DRAGON_SLAYER) == 2) {
				addItem(p, 418, 1);
				p.message("You find a piece of map in the chest");
			} else {
				p.message("You find nothing in the chest");
			}
			break;
		case 228:
			if (!hasItem(p, 417, 1) && p.getQuestStage(Quests.DRAGON_SLAYER) == 2) {
				addItem(p, 417, 1);
				p.message("You find a piece of map in the chest");
			} else {
				p.message("You find nothing in the chest");
			}
			break;
		//clicking boat triggers klarense if available
		case 224:
		case 225:
			if (p.getCache().hasKey("owns_ship")) {
				if (p.getCache().hasKey("ship_fixed") || p.getQuestStage(this) == -1) {
					p.teleport(258, 3493, false);
				} else if (p.getQuestStage(this) == 2
						|| p.getQuestStage(this) == 3
						&& !p.getCache().hasKey("ship_fixed")) {
					p.teleport(259, 3472, false);
				}
			} else {
				Npc klarense = getNearestNpc(p, 193, 15);
				if(klarense != null) {
					klarense.initializeTalkScript(p);
				} else {
					p.message("You must talk to the owner about this.");
				}
			}
			break;
		case 227:
			if (obj.getX() == 281 && obj.getY() == 3473) {
				p.teleport(409, 638, false);
				p.message("You leave the ship");
				return;
			}
			p.teleport(259, 641, false);
			p.message("You leave the ship");
			break;
		}
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 187) {
			oziachDialogue(p, n, -1);
		}
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == 192 && p.getQuestStage(this) >= 2) {
			World.getWorld().registerItem(
					new GroundItem(416, n.getX(), n.getY(), 1, p));
		}
		if (n.getID() == 177) {
			World.getWorld().registerItem(
					new GroundItem(QuestItems.RED_KEY, n.getX(), n.getY(), 1, p));
		} else if (n.getID() == 178) {
			World.getWorld().registerItem(
					new GroundItem(QuestItems.ORANGE_KEY, n.getX(), n.getY(), 1, p));
		} else if (n.getID() == 179) {
			World.getWorld().registerItem(
					new GroundItem(QuestItems.YELLOW_KEY, n.getX(), n.getY(), 1, p));
		} else if (n.getID() == 180) {
			World.getWorld().registerItem(
					new GroundItem(QuestItems.BLUE_KEY, n.getX(), n.getY(), 1, p));
		} else if (n.getID() == 182) {
			World.getWorld().registerItem(
					new GroundItem(QuestItems.MAGENTA_KEY, n.getX(), n.getY(), 1, p));
		} else if (n.getID() == 181) {
			World.getWorld().registerItem(
					new GroundItem(QuestItems.BLACK_KEY, n.getX(), n.getY(), 1, p));
		} else if (n.getID() == 196 && p.getQuestStage(this) == 3) {
			p.sendQuestComplete(getQuestId());
		}
		return false;
	}

	@Override
	public void handleReward(Player p) {
		p.teleport(411, 3480, false);
		p.message("Well done you have completed the dragon slayer quest!");
		p.message("@gre@You haved gained 2 quest points!");
		p.incQuestExp(2, p.getSkills().getMaxStat(2) * 1200 + 2600);
		p.incQuestExp(1, p.getSkills().getMaxStat(1) * 1200 + 2600);
		p.incQuestPoints(2);
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		return obj.getID() == 57 || obj.getID() == 58 || obj.getID() == 59 || obj.getID() == 60;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 57) {
			if (p.getX() >= 259 && hasItem(p, 268, 1) && hasItem(p, 200, 1) && hasItem(p, 375, 1) && hasItem(p, 340)) {
				Point location = Point.location(p.getX(), p.getY());
				doDoor(obj, p);
				if(!p.getLocation().equals(location)) {
					removeItem(p, 268, 1);
					removeItem(p, 200, 1);
					removeItem(p, 375, 1);
					removeItem(p, 340, 1);
				} 
			} 
			else if(p.getX() <= 258) {
				doDoor(obj, p);
			}
			else {
				p.message("the door is locked");
			}
		}
		else if(obj.getID() == 58) {
			//from side of crandor
			if(p.getY() <= 3517) {
				p.message("You just went through a secret door");
				if(!p.getCache().hasKey("crandor_shortcut")) {
					p.message("You remember where the door is for future use");
					p.getCache().store("crandor_shortcut", true);
				}
				doDoor(obj, p, 11);
			}
			else {
				if(p.getCache().hasKey("crandor_shortcut")) {
					p.message("You just went through a secret door");
					doDoor(obj, p, 11);
				}
				else {
					p.message("nothing interesting happens");
				}
			}
		}
		//??
		else if (obj.getID() == 59) {
			doDoor(obj, p);
		}
		else if (obj.getID() == 60) {
			p.message("Nothing interesting happens");
		}
	}

	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		if ((item1.getID() >= 416 && item1.getID() <= 418)
				&& (item2.getID() >= 416 && item2.getID() <= 418)) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnItem(Player p, Item item1, Item item2) {
		if ((item1.getID() >= 416 && item1.getID() <= 418)
				&& (item2.getID() >= 416 && item2.getID() <= 418)) {
			if (hasItem(p, 416, 1) && hasItem(p, 417, 1) && hasItem(p, 418, 1)) {
				removeItem(p, 416, 1);
				removeItem(p, 417, 1);
				removeItem(p, 418, 1);
				addItem(p, 415, 1);
			}
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
			Player player) {
		if (obj.getID() == 226) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == 226 && item.getID() == 410) {
			if(p.getCache().hasKey("crandor_shortcut")) {
				p.message("You don't need to mess about with broken ships");
				p.message("Now you have found that secret passage from Karamja");
			}
			else if (!p.getCache().hasKey("ship_repair") && hasItem(p, 419, 4)
					&& hasItem(p, 410, 1)) {
				p.message("You hammer the plank over the hole");
				p.message("You still need more planks to close the hole completely");
				p.getInventory().remove(419, 4);
				p.getInventory().remove(410, 1);
				p.getCache().set("ship_repair", 1);
			} else if (hasItem(p, 419, 4) && hasItem(p, 410, 1)) {
				int planks_added = p.getCache().getInt("ship_repair");
				p.message("You hammer the plank over the hole");
				p.getInventory().remove(419, 4);
				p.getInventory().remove(410, 1);
				if (planks_added + 1 == 3) {
					p.getCache().remove("ship_repair");
					p.getCache().store("ship_fixed", true);
					p.message("You board up the hole in the ship");
					p.teleport(258, 3493, false);
				} else {
					p.getCache().set("ship_repair", planks_added + 1);
					p.message("You still need more planks to close the hole completely");
				}
			} else if(!hasItem(p, 419, 4)) {
				p.message("You need 4 steel nails to attach the plank with");
			} else if(!hasItem(p, 168, 1)) {
				p.message("You need a hammer to hammer the nails in with");
			} else {
				p.message("Nothing interesting happens");
			}
		} 
		//only accept planks used
		else if(obj.getID() == 226) {
			p.message("Nothing interesting happens");
		}
	}
}
