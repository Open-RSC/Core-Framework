package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.Cache;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.*;
import com.openrsc.server.plugins.listeners.executive.*;

import static com.openrsc.server.plugins.Functions.*;

/***
 * @author n0m
 */
public class ErnestTheChicken implements QuestInterface,
	InvUseOnWallObjectListener, InvUseOnWallObjectExecutiveListener,
	InvUseOnItemListener, InvUseOnItemExecutiveListener,
	WallObjectActionListener, WallObjectActionExecutiveListener,
	TalkToNpcListener, ObjectActionListener, ObjectActionExecutiveListener,
	TalkToNpcExecutiveListener, InvUseOnObjectListener,
	InvUseOnObjectExecutiveListener {

	@Override
	public int getQuestId() {
		return Quests.ERNEST_THE_CHICKEN;
	}

	@Override
	public String getQuestName() {
		return "Ernest the chicken";
	}
	
	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player p) {
		p.getInventory().add(new Item(ItemId.COINS.id(), 300));
		p.message("Well done. You have completed the Ernest the chicken quest");
		incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.ERNEST_THE_CHICKEN), true);
		p.message("@gre@You haved gained 4 quest points!");
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
									   Player player) {
		return (obj.getID() == QuestObjects.FOUNTAIN && item.getID() == ItemId.POISONED_FISH_FOOD.id())
				|| (obj.getID() == QuestObjects.COMPOST && item.getID() == ItemId.SPADE.id());
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == QuestObjects.FOUNTAIN && item.getID() == ItemId.POISONED_FISH_FOOD.id()) {
			message(p, "You pour the poisoned fish food into the fountain",
				"You see the pirhanas eating the food",
				"The pirhanas drop dead and float to the surface");
			removeItem(p, ItemId.POISONED_FISH_FOOD.id(), 1);
			if (!p.getCache().hasKey("poisoned_fountain")) {
				p.getCache().store("poisoned_fountain", true);
			}
		} else if (obj.getID() == QuestObjects.FOUNTAIN
			&& item.getID() == ItemId.FISH_FOOD.id()) {
			message(p, "You pour the fish food into the fountain",
				"You see the pirhanas eating the food",
				"The pirhanas seem hungrier than ever");
			removeItem(p, ItemId.FISH_FOOD.id(), 1);
		}
		//nothing happens every other item
		else if (obj.getID() == QuestObjects.FOUNTAIN) {
			message(p, "Nothing interesting happens");
		}
		if (obj.getID() == QuestObjects.COMPOST
			&& item.getID() == ItemId.SPADE.id()) {
			if (!hasItem(p, ItemId.CLOSET_KEY.id()) && p.getQuestStage(this) > 0) {
				message(p, "You dig through the compost heap",
					"You find a small key");
				addItem(p, ItemId.CLOSET_KEY.id(), 1);
			} else {
				message(p, "You dig through the compost heap",
					"You find nothing of interest");
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.VERONICA.id() || n.getID() == NpcId.PROFESSOR_ODDENSTEIN.id();
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
									 Player player) {
		switch (obj.getID()) {
			case 36:
				return true;
			case QuestObjects.LEVERA:
			case QuestObjects.LEVERB:
			case QuestObjects.LEVERC:
			case QuestObjects.LEVERD:
			case QuestObjects.LEVERE:
			case QuestObjects.LEVERF:
				return true;
		}
		return obj.getID() == QuestObjects.FOUNTAIN
			|| obj.getID() == QuestObjects.COMPOST
			|| obj.getID() == QuestObjects.LADDER;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		switch (obj.getID()) {
			case QuestObjects.LADDER:
				if (p.getCache().hasKey("LeverA") || p.getCache().hasKey("LeverB")
					|| p.getCache().hasKey("LeverC")
					|| p.getCache().hasKey("LeverD")
					|| p.getCache().hasKey("LeverE")
					|| p.getCache().hasKey("LeverF")) {
					p.getCache().remove("LeverA");
					p.getCache().remove("LeverB");
					p.getCache().remove("LeverC");
					p.getCache().remove("LeverD");
					p.getCache().remove("LeverE");
					p.getCache().remove("LeverF");
				}
				p.message("You climb up the ladder");
				p.teleport(223, 554, false);
				break;
			case QuestObjects.FOUNTAIN:
				if (p.getCache().hasKey("poisoned_fountain")) {
					if (!hasItem(p, ItemId.PRESSURE_GAUGE.id(), 1)) {
						playerTalk(p, null,
							"There seems to be a pressure gauge in here",
							"There are also some dead fish");
						p.message("you get the pressure gauge from the fountain");
						addItem(p, ItemId.PRESSURE_GAUGE.id(), 1);
					} else {
						p.message("It's full of dead fish");
					}
				} else {
					playerTalk(p, null,
						"There seems to be a pressure gauge in here",
						"There are a lot of Pirhanas in there though",
						"I can't get the gauge out");
				}
				break;
			case QuestObjects.COMPOST:
				p.message("I'm not looking through that with my hands");
				break;
			case QuestObjects.LEVERA:
			case QuestObjects.LEVERB:
			case QuestObjects.LEVERC:
			case QuestObjects.LEVERD:
			case QuestObjects.LEVERE:
			case QuestObjects.LEVERF:
				if (command.equalsIgnoreCase("pull"))
					doLever(p, obj.getID());
				else if (command.equalsIgnoreCase("inspect"))
					inspectLever(p, obj.getID());
				break;
		}
	}

	public void inspectLever(Player p, int objectID) {
		String leverName = null;
		if (objectID == QuestObjects.LEVERA) {
			leverName = "LeverA";
		} else if (objectID == QuestObjects.LEVERB) {
			leverName = "LeverB";
		} else if (objectID == QuestObjects.LEVERC) {
			leverName = "LeverC";
		} else if (objectID == QuestObjects.LEVERD) {
			leverName = "LeverD";
		} else if (objectID == QuestObjects.LEVERE) {
			leverName = "LeverE";
		} else if (objectID == QuestObjects.LEVERF) {
			leverName = "LeverF";
		}
		p.message("The lever is "
			+ (p.getCache().getBoolean(leverName) ? "down" : "up"));
	}

	public void doLever(Player p, int objectID) {
		if (!p.getCache().hasKey("LeverA")) {
			p.getCache().store("LeverA", false);
			p.getCache().store("LeverB", false);
			p.getCache().store("LeverC", false);
			p.getCache().store("LeverD", false);
			p.getCache().store("LeverE", false);
			p.getCache().store("LeverF", false);
		}
		String leverName = null;
		if (objectID == QuestObjects.LEVERA) {
			leverName = "LeverA";
		} else if (objectID == QuestObjects.LEVERB) {
			leverName = "LeverB";
		} else if (objectID == QuestObjects.LEVERC) {
			leverName = "LeverC";
		} else if (objectID == QuestObjects.LEVERD) {
			leverName = "LeverD";
		} else if (objectID == QuestObjects.LEVERE) {
			leverName = "LeverE";
		} else if (objectID == QuestObjects.LEVERF) {
			leverName = "LeverF";
		}
		p.getCache().store(leverName, !p.getCache().getBoolean(leverName));
		p.message("You pull " + nameToMsg(leverName) + " "
			+ (p.getCache().getBoolean(leverName) ? "down" : "up"));
		p.message("you hear a clunk");
	}

	public String nameToMsg(String leverName) {
		int length = leverName.length();
		return leverName.substring(0, length - 2).toLowerCase() + " " + leverName.substring(length - 1);
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		switch (NpcId.getById(n.getID())) {
			case VERONICA:
				veronicaDialogue(p, n, -1);
				break;
			case PROFESSOR_ODDENSTEIN:
				oddensteinDialogue(p, n, -1);
				break;
			default:
				break;
		}
	}

	public void oddensteinDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			switch (p.getQuestStage(this)) {
				case -1:
				case 0:
					npcTalk(p, n, "Be careful in here",
						"Lots of dangerous equipment in here");
					int choice = showMenu(p, n, false, //do not send over
						"What does this machine do?", "Is this your house?");
					if (choice == 0) {
						playerTalk(p, n, "What does this machine do?");
						oddensteinDialogue(p, n, Oddenstein.MACHINE);
					} else if (choice == 1) {
						playerTalk(p, n, "Is this your house?");
						oddensteinDialogue(p, n, Oddenstein.HOUSE);
					}
					break;
				case 1:
					int s1Menu = showMenu(p, n, false, //do not send over
						"I'm looking for a guy called Ernest",
						"What do this machine do?", "Is this your house?");
					if (s1Menu == 0) {
						playerTalk(p, n, "I'm looking for a guy called Ernest");
						oddensteinDialogue(p, n, Oddenstein.LOOKING_FOR_ERNEST);
					} else if (s1Menu == 1) {
						playerTalk(p, n, "What does this machine do?");
						oddensteinDialogue(p, n, Oddenstein.MACHINE);
					} else if (s1Menu == 2) {
						playerTalk(p, n, "Is this your house?");
						oddensteinDialogue(p, n, Oddenstein.HOUSE);
					}
					break;
				case 2:
					npcTalk(p, n, "Have you found anything yet?");

					// no items
					if (!hasItem(p, ItemId.RUBBER_TUBE.id())
						&& !hasItem(p, ItemId.PRESSURE_GAUGE.id())
						&& !hasItem(p, ItemId.OIL_CAN.id())) {
						playerTalk(p, n, "I'm afraid I don't have any yet!");
						npcTalk(p, n,
							"I need a rubber tube, a pressure gauge and a can of oil",
							"Then your friend can stop being a chicken");
					}
					// all items
					else if (hasItem(p, ItemId.RUBBER_TUBE.id())
						&& hasItem(p, ItemId.PRESSURE_GAUGE.id())
						&& hasItem(p, ItemId.OIL_CAN.id())) {
						playerTalk(p, n, "I have everything");
						npcTalk(p, n, "Give em here then");
						message(p,
							"You give a rubber tube, a pressure gauge and a can of oil to the Professer",
							"Oddenstein starts up the machine",
							"The machine hums and shakes",
							"Suddenly a ray shoots out of the machine at the chicken");
						Npc chicken = getNearestNpc(p, NpcId.ERNEST_CHICKEN.id(), 20);
						if (chicken != null) {
							removeItem(p, ItemId.RUBBER_TUBE.id(), 1);
							removeItem(p, ItemId.PRESSURE_GAUGE.id(), 1);
							removeItem(p, ItemId.OIL_CAN.id(), 1);
							Npc ernest = transform(chicken, NpcId.ERNEST.id(), false);
							npcTalk(p, ernest, "Thank you sir",
								"It was dreadfully irritating being a chicken",
								"How can I ever thank you?");
							playerTalk(p, ernest,
								"Well a cash reward is always nice");
							npcTalk(p, ernest, "Of course, of course");

							message(p, "Ernest hands you 300 coins");
							ernest.remove();
							p.sendQuestComplete(getQuestId());
						}
					}
					// some items
					else {
						playerTalk(p, n, "I have found some of the things you need:");
						if (p.getInventory().hasItemId(ItemId.OIL_CAN.id()))
							playerTalk(p, n, "I have a can of oil");
						if (p.getInventory().hasItemId(ItemId.PRESSURE_GAUGE.id()))
							playerTalk(p, n, "I have a pressure gauge");
						if (p.getInventory().hasItemId(ItemId.RUBBER_TUBE.id()))
							playerTalk(p, n, "I have a rubber tube");

						npcTalk(p, n, "Well that's a start", "You still need to find");
						if (!p.getInventory().hasItemId(ItemId.OIL_CAN.id()))
							npcTalk(p, n, "A can of oil");
						if (!p.getInventory().hasItemId(ItemId.RUBBER_TUBE.id()))
							npcTalk(p, n, "A rubber tube");
						if (!p.getInventory().hasItemId(ItemId.PRESSURE_GAUGE.id()))
							npcTalk(p, n, "A Pressure Gauge");
					}
					break;
			}
			return;
		}
		switch (cID) {
			case Oddenstein.HOUSE:
				npcTalk(p, n, "No, I'm just one of the tenants",
					"It belongs to the count", "Who lives in the basement");
				break;
			case Oddenstein.MACHINE:
				npcTalk(p, n, "Nothing at the moment", "As it's broken",
					"It's meant to be a transmutation machine",
					"It has also spent time as a time travel machine",
					"And a dramatic lightning generator",
					"And a thing for generating monsters");
				break;
			case Oddenstein.LOOKING_FOR_ERNEST:
				npcTalk(p, n, "Ah Ernest, top notch bloke",
					"He's helping me with my experiments");
				playerTalk(p, n, "So you know where he is then?");
				npcTalk(p, n, "He's that chicken over there");
				playerTalk(p, n, "Ernest is a chicken?", "Are you sure?");
				npcTalk(p,
					n,
					"Oh he isn't normally a chicken",
					"Or at least he wasn't",
					"Until he helped me test my pouletmorph machine",
					"It was originally going to be called a transmutation machine",
					"But after testing Pouletmorph seems more appropriate");
				int choices = showMenu(
					p,
					n,
					"I'm glad Veronica didn't actually get engaged to a chicken",
					"Change him back this instant");
				if (choices == 0) {
					npcTalk(p, n, "Who's Veronica?");
					playerTalk(p, n, "Ernest's fiancee",
						"She probably doesn't want to marry a chicken");
					npcTalk(p, n, "Ooh I dunno",
						"She could have free eggs for breakfast");
					playerTalk(p, n, "I think you'd better change him back");
					oddensteinDialogue(p, n, Oddenstein.CHANGEBACK);
				} else if (choices == 1) {
					oddensteinDialogue(p, n, Oddenstein.CHANGEBACK);
				}
				break;
			case Oddenstein.CHANGEBACK:
				npcTalk(p, n, "Um it's not so easy", "My machine is broken",
					"And the house gremlins",
					"Have run off with some vital bits");
				playerTalk(p, n, "Well I can look out for them");
				npcTalk(p, n,
					"That would be a help",
					"They'll be somewhere in the manor house or its grounds",
					"The gremlins never go further than the entrance gate",
					"I'm missing the pressure gauge and a rubber tube",
					"They've also taken my oil can",
					"Which I'm going to need to get this thing started again");
				p.updateQuestStage(this, 2);
				break;
		}
	}

	private void veronicaDialogue(Player p, Npc n, int i) {
		if (i == -1) {
			switch (p.getQuestStage(this)) {
				case 0:
					npcTalk(p, n, "Can you please help me?",
						"I'm in a terrible spot of trouble");
					int choice = showMenu(p, n, false, //do not send over
						"Aha, sounds like a quest. I'll help",
						"No, I'm looking for something to kill");
					if (choice == 0) {
						playerTalk(p, n, "Aha, sounds like a quest", "I'll help");
						npcTalk(p, n, "Yes yes I suppose it is a quest",
							"My fiance Ernest and I came upon this house here",
							"Seeing as we were a little lost",
							"Ernest decided to go in and ask for directions",
							"That was an hour ago",
							"That house looks very spooky",
							"Can you go and see if you can find him for me?");
						playerTalk(p, n, "Ok, I'll see what I can do");
						npcTalk(p, n, "Thank you, thank you", "I'm very grateful");
						p.updateQuestStage(this, 1);
					} else if (choice == 1) {
						playerTalk(p, n, "No, I'm looking for something to kill");
						npcTalk(p, n, "Oooh you violent person you");
					}
					break;
				case 1:
					npcTalk(p, n, "Have you found my sweetheart yet?");
					playerTalk(p, n, "No, not yet");
					break;
				case 2:
					npcTalk(p, n, "Have you found my sweetheart yet?");
					playerTalk(p, n, "Yes, he's a chicken");
					npcTalk(p, n, "I know he's not exactly brave",
						"But I think you're being a little harsh");
					playerTalk(p, n,
						"No no he's been turned into an actual chicken",
						"By a mad scientist");
					message(p, "Veronica lets out an ear piecing shreek");
					npcTalk(p, n, "Eeeeek", "My poor darling",
						"Why must these things happen to us?");
					playerTalk(p, n, "Well I'm doing my best to turn him back");
					npcTalk(p, n, "Well be quick",
						"I'm sure being a chicken can't be good for him");
					break;
				case -1:
					npcTalk(p, n, "Thank you for rescuing Ernest");
					playerTalk(p, n, "Where is he now?");
					npcTalk(p, n, "Oh he went off to talk to some green warty guy",
						"I'm sure he'll be back soon");
					break;
			}
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click,
										 Player player) {
		if (obj.getID() >= 25 && obj.getID() <= 29) {
			return true;
		}
		if (obj.getID() >= 31 && obj.getID() <= 36) {
			return true;
		}
		if (obj.getID() == 30 && obj.getX() == 226 && obj.getY() == 3378) {
			return true;
		}
		return false;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		Cache c = p.getCache();
		switch (obj.getID()) {
			case 35:
				//only allow is player is stuck, otherwise promote using key
				if (p.getX() == 211 && p.getY() == 545 && !hasItem(p, ItemId.CLOSET_KEY.id())) {
					doDoor(obj, p);
					p.message("You go through the door");
				} else {
					p.message("The door is locked");
				}
				break;
			case 36:
				if (p.getY() >= 553) {
					doDoor(obj, p);
					sleep(3000);
					p.message("The door slams behind you!");
				} else {
					p.message("The door won't open");
				}
				break;
			case 29:
				if (c.hasKey("LeverA")
					&& c.hasKey("LeverB")
					&& c.hasKey("LeverC")
					&& c.hasKey("LeverD")
					&& c.hasKey("LeverE")
					&& c.hasKey("LeverF")
					&& !c.getBoolean("LeverA")
					&& !c.getBoolean("LeverB")
					&& c.getBoolean("LeverC")
					&& c.getBoolean("LeverD")
					&& !c.getBoolean("LeverE")
					&& c.getBoolean("LeverF")) {
					doDoor(obj, p);
				} else
					p.message("The door is locked");
				break;
			case 28:
				if (c.hasKey("LeverD") && c.getBoolean("LeverD"))
					doDoor(obj, p);
				else
					p.message("The door is locked");
				break;
			case 25:
				if (c.hasKey("LeverA")
					&& c.hasKey("LeverB")
					&& c.hasKey("LeverC")
					&& c.hasKey("LeverD")
					&& c.hasKey("LeverE")
					&& c.hasKey("LeverF")
					&& !c.getBoolean("LeverA")
					&& !c.getBoolean("LeverB")
					&& !c.getBoolean("LeverC")
					&& c.getBoolean("LeverD")
					&& c.getBoolean("LeverE")
					&& c.getBoolean("LeverF")) {
					doDoor(obj, p);
				} else if (
					c.hasKey("LeverA")
						&& c.hasKey("LeverB")
						&& c.hasKey("LeverC")
						&& c.hasKey("LeverD")
						&& c.hasKey("LeverE")
						&& c.hasKey("LeverF")
						&& !c.getBoolean("LeverA")
						&& !c.getBoolean("LeverB")
						&& c.getBoolean("LeverC")
						&& c.getBoolean("LeverD")
						&& c.getBoolean("LeverE")
						&& c.getBoolean("LeverF")) {
					doDoor(obj, p);
				} else
					p.message("The door is locked");
				break;
			case 26:
				if (c.hasKey("LeverF")
					&& c.hasKey("LeverD")
					&& c.hasKey("LeverB")
					&& c.getBoolean("LeverF")
					&& c.getBoolean("LeverD")
					&& !c.getBoolean("LeverB"))
					doDoor(obj, p);
				else
					p.message("The door is locked");
				break;
			case 27:
				if (c.hasKey("LeverA")
					&& c.hasKey("LeverB")
					&& c.hasKey("LeverD")
					&& c.getBoolean("LeverA")
					&& c.getBoolean("LeverB")
					&& c.getBoolean("LeverD"))
					doDoor(obj, p);
				else
					p.message("The door is locked");
				break;
			case 31:// Need to make Lever work xD haha brb
				if (c.hasKey("LeverD")
					&& c.hasKey("LeverB")
					&& c.hasKey("LeverF")
					&& c.getBoolean("LeverD")
					&& !c.getBoolean("LeverB")
					&& !c.getBoolean("LeverF")) // easiet
					doDoor(obj, p);
				else
					p.message("The door is locked");

				break;
			case 32:// first door on the right
				if (c.hasKey("LeverA")
					&& c.hasKey("LeverB")
					&& c.hasKey("LeverC")
					&& c.hasKey("LeverD")
					&& c.hasKey("LeverE")
					&& c.hasKey("LeverF")
					&& c.getBoolean("LeverA")
					&& c.getBoolean("LeverB")
					&& !c.getBoolean("LeverC")
					&& !c.getBoolean("LeverD")
					&& !c.getBoolean("LeverE")
					&& !c.getBoolean("LeverF")) {
					doDoor(obj, p);
				} else
					p.message("The door is locked");
				break;
			case 33:// second door from the right
				if (c.hasKey("LeverC")
					&& c.hasKey("LeverD")
					&& !c.getBoolean("LeverC")
					&& c.getBoolean("LeverD")) {
					doDoor(obj, p);
				} else if (
					c.hasKey("LeverA")
						&& c.hasKey("LeverB")
						&& c.hasKey("LeverC")
						&& c.hasKey("LeverD")
						&& c.hasKey("LeverE")
						&& c.hasKey("LeverF")
						&& !c.getBoolean("LeverA")
						&& !c.getBoolean("LeverB")
						&& c.getBoolean("LeverC")
						&& c.getBoolean("LeverD")
						&& !c.getBoolean("LeverE")
						&& c.getBoolean("LeverF")) {
					doDoor(obj, p);
				} else
					p.message("The door is locked");
				break;
		}
		if (obj.getID() == 30 && obj.getX() == 226 && obj.getY() == 3378) {
			if (c.hasKey("LeverF")
				&& c.hasKey("LeverE")
				&& c.getBoolean("LeverF")
				&& !c.getBoolean("LeverE")) {
				doDoor(obj, p);
			} else {
				p.message("The door is locked");
			}
		}
	}

	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		return Functions.compareItemsIds(item1, item2, ItemId.FISH_FOOD.id(), ItemId.POISON.id());
	}

	@Override
	public void onInvUseOnItem(Player player, Item item1, Item item2) {
		if (Functions.compareItemsIds(item1, item2, ItemId.FISH_FOOD.id(), ItemId.POISON.id())) {
			removeItem(player, ItemId.FISH_FOOD.id(), 1);
			removeItem(player, ItemId.POISON.id(), 1);
			addItem(player, ItemId.POISONED_FISH_FOOD.id(), 1);
			player.message("You poison the fish food");
		}
	}

	@Override
	public boolean blockInvUseOnWallObject(GameObject obj, Item item,
										   Player player) {
		return item.getID() == ItemId.CLOSET_KEY.id() && obj.getID() == 35;
	}

	@Override
	public void onInvUseOnWallObject(GameObject obj, Item item, Player player) {
		if (item.getID() == ItemId.CLOSET_KEY.id() && obj.getID() == 35) {
			doDoor(obj, player);
			player.message("You unlock the door");
			player.message("You go through the door");
			showBubble(player, item);
		}
	}

	final class QuestObjects {
		public static final int LADDER = 130; // ID: 86 fountain - coords: 223,
		// 3385
		public static final int FOUNTAIN = 86; // ID: 86 fountain - coords: 226,
		// 565
		public static final int COMPOST = 134; // ID: 134 compost heap - coords:
		// 230, 552
		public static final int LEVERA = 124; // ID: 124 LeverA - coords: 225,
		// 3386
		public static final int LEVERB = 125; // ID: 125 LeverB - coords: 222,
		// 3382
		public static final int LEVERC = 126; // ID: 126 LeverC - coords: 222,
		// 3378
		public static final int LEVERD = 127; // ID: 127 LeverD - coords: 223,
		// 3375
		public static final int LEVERE = 128; // ID: 128 Lever E - coords: 229,
		// 3375
		public static final int LEVERF = 129; // ID: 129 Lever F - coords: 230,
		// 3376
		/*
		 * ID: 32 door - coords: 223, 3381 ID: 27 door - coords: 225, 3379 ID:
		 * 25 door - coords: 225, 3376 ID: 33 door - coords: 226, 3381 ID: 30
		 * door - coords: 226, 3378 ID: 28 door - coords: 228, 3379 ID: 26 door
		 * - coords: 228, 3376 ID: 31 door - coords: 229, 3378 ID: 29 door -
		 * coords: 228, 3382
		 */
	}

	final class Oddenstein {
		public static final int HOUSE = 0;
		public static final int MACHINE = 1;
		public static final int LOOKING_FOR_ERNEST = 2;
		public static final int ENGAGED = 3;
		public static final int CHANGEBACK = 4;

	}
}
