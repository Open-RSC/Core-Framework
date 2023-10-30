package com.openrsc.server.plugins.authentic.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.model.Cache;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.custom.minigames.ABoneToPick;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class ErnestTheChicken implements QuestInterface,
	UseBoundTrigger,
	UseInvTrigger,
	OpBoundTrigger,
	TalkNpcTrigger, OpLocTrigger,
	UseLocTrigger {

	private static final Logger LOGGER = LogManager.getLogger(ErnestTheChicken.class);

	@Override
	public int getQuestId() {
		return Quests.ERNEST_THE_CHICKEN;
	}

	@Override
	public String getQuestName() {
		return "Ernest the chicken";
	}

	@Override
	public int getQuestPoints() {
		return Quest.ERNEST_THE_CHICKEN.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.getCarriedItems().getInventory().add(new Item(ItemId.COINS.id(), 300));
		if (player.getConfig().INFLUENCE_INSTEAD_QP) {
			player.message("Well done. You have completed the machine quest");
		} else {
			player.message("Well done. You have completed the Ernest the chicken quest");
		}
		final QuestReward reward = Quest.ERNEST_THE_CHICKEN.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return (obj.getID() == QuestObjects.FOUNTAIN && item.getCatalogId() == ItemId.POISONED_FISH_FOOD.id())
			|| (obj.getID() == QuestObjects.FOUNTAIN && item.getCatalogId() == ItemId.FISH_FOOD.id())
				|| (obj.getID() == QuestObjects.COMPOST && item.getCatalogId() == ItemId.SPADE.id());
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == QuestObjects.FOUNTAIN && item.getCatalogId() == ItemId.POISONED_FISH_FOOD.id()) {
			mes("You pour the poisoned fish food into the fountain");
			delay(3);
			mes("You see the pirhanas eating the food");
			delay(3);
			mes("The pirhanas drop dead and float to the surface");
			delay(3);
			player.getCarriedItems().remove(new Item(ItemId.POISONED_FISH_FOOD.id()));
			if (!player.getCache().hasKey("poisoned_fountain")) {
				player.getCache().store("poisoned_fountain", true);
			}
		} else if (obj.getID() == QuestObjects.FOUNTAIN
			&& item.getCatalogId() == ItemId.FISH_FOOD.id()) {
			mes("You pour the fish food into the fountain");
			delay(3);
			mes("You see the pirhanas eating the food");
			delay(3);
			mes("The pirhanas seem hungrier than ever");
			delay(3);
			player.getCarriedItems().remove(new Item(ItemId.FISH_FOOD.id()));
		}
		//nothing happens every other item
		else if (obj.getID() == QuestObjects.FOUNTAIN) {
			mes("Nothing interesting happens");
			delay(3);
		}
		if (obj.getID() == QuestObjects.COMPOST
			&& item.getCatalogId() == ItemId.SPADE.id()) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.CLOSET_KEY.id(), Optional.empty()) && player.getQuestStage(this) > 0) {
				mes("You dig through the compost heap");
				delay(3);
				mes("You find a small key");
				delay(3);
				give(player, ItemId.CLOSET_KEY.id(), 1);
			} else {
				mes("You dig through the compost heap");
				delay(3);
				mes("You find nothing of interest");
				delay(3);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.VERONICA.id() || n.getID() == NpcId.PROFESSOR_ODDENSTEIN.id();
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
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
	public void onOpLoc(Player player, GameObject obj, String command) {
		switch (obj.getID()) {
			case QuestObjects.LADDER:
				if (player.getCache().hasKey("LeverA") || player.getCache().hasKey("LeverB")
					|| player.getCache().hasKey("LeverC")
					|| player.getCache().hasKey("LeverD")
					|| player.getCache().hasKey("LeverE")
					|| player.getCache().hasKey("LeverF")) {
					player.getCache().remove("LeverA");
					player.getCache().remove("LeverB");
					player.getCache().remove("LeverC");
					player.getCache().remove("LeverD");
					player.getCache().remove("LeverE");
					player.getCache().remove("LeverF");
				}
				player.message("You climb up the ladder");
				player.teleport(223, 554, false);
				break;
			case QuestObjects.FOUNTAIN:
				if (player.getCache().hasKey("poisoned_fountain")) {
					if (!player.getCarriedItems().hasCatalogID(ItemId.PRESSURE_GAUGE.id(), Optional.empty())) {
						say(player, null,
							"There seems to be a pressure gauge in here",
							"There are also some dead fish");
						player.message("you get the pressure gauge from the fountain");
						give(player, ItemId.PRESSURE_GAUGE.id(), 1);
					} else {
						player.message("It's full of dead fish");
					}
				} else {
					say(player, null,
						"There seems to be a pressure gauge in here",
						"There are a lot of Pirhanas in there though",
						"I can't get the gauge out");
				}
				break;
			case QuestObjects.COMPOST:
				player.message("I'm not looking through that with my hands");
				break;
			case QuestObjects.LEVERA:
			case QuestObjects.LEVERB:
			case QuestObjects.LEVERC:
			case QuestObjects.LEVERD:
			case QuestObjects.LEVERE:
			case QuestObjects.LEVERF:
				if (command.equalsIgnoreCase("pull"))
					doLever(player, obj.getID());
				else if (command.equalsIgnoreCase("inspect"))
					inspectLever(player, obj.getID());
				break;
		}
	}

	public void inspectLever(Player player, int objectID) {
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
		player.message("The lever is "
			+ (player.getCache().getBoolean(leverName) ? "down" : "up"));
	}

	public void doLever(Player player, int objectID) {
		if (!player.getCache().hasKey("LeverA")) {
			player.getCache().store("LeverA", false);
			player.getCache().store("LeverB", false);
			player.getCache().store("LeverC", false);
			player.getCache().store("LeverD", false);
			player.getCache().store("LeverE", false);
			player.getCache().store("LeverF", false);
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
		player.getCache().store(leverName, !player.getCache().getBoolean(leverName));
		player.message("You pull " + nameToMsg(leverName) + " "
			+ (player.getCache().getBoolean(leverName) ? "down" : "up"));
		player.message("you hear a clunk");
	}

	public String nameToMsg(String leverName) {
		int length = leverName.length();
		return leverName.substring(0, length - 1).toLowerCase() + " " + leverName.substring(length - 1);
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		switch (NpcId.getById(n.getID())) {
			case VERONICA:
				veronicaDialogue(player, n, -1);
				break;
			case PROFESSOR_ODDENSTEIN:
				oddensteinDialogue(player, n, -1);
				break;
			default:
				break;
		}
	}

	public void oddensteinDialogue(Player player, Npc n, int cID) {
		if (cID == -1) {
			switch (player.getQuestStage(this)) {
				case -1:
				case 0:
					ArrayList<String> choices = new ArrayList<String>();
					choices.add("What does this machine do?");
					choices.add("Is this your house?");
					if (config().A_BONE_TO_PICK) {
						int stage = ABoneToPick.getStage(player);
						if (stage == ABoneToPick.HEARD_AMAZING_SONG) {
							choices.add("Can you help me get rid of some annoying skeletons?");
						} else if (stage == ABoneToPick.TALKED_TO_ODDENSTEIN) {
							choices.add("About the Bonecrusher");
						} else if (stage == ABoneToPick.FINISHED_BONECRUSHER
							&& !ifheld(player, ItemId.BONECRUSHER.id())) {
							choices.add("I've lost the Bonecrusher!");
						}
					}

					// Retrieve bonecrusher post-quest
					// Don't need to check for config because this will always be false if the player hasn't done the event
					if (ABoneToPick.getStage(player) == ABoneToPick.COMPLETED
						&& !ifheld(player, ItemId.BONECRUSHER.id())) {
						choices.add("I've lost the Bonecrusher!");
					}

					npcsay(player, n, "Be careful in here",
						"Lots of dangerous equipment in here");
					int choice = multi(player, n, choices.toArray(new String[0]));
					if (choice == 0) {
						oddensteinDialogue(player, n, Oddenstein.MACHINE);
					} else if (choice == 1) {
						oddensteinDialogue(player, n, Oddenstein.HOUSE);
					} else if (choice == 2) {
						ABoneToPick.oddensteinDialogue(player, n);
					}
					break;
				case 1:
					int s1Menu = multi(player, n, false, //do not send over
						"I'm looking for a guy called Ernest",
						"What do this machine do?", "Is this your house?");
					if (s1Menu == 0) {
						say(player, n, "I'm looking for a guy called Ernest");
						oddensteinDialogue(player, n, Oddenstein.LOOKING_FOR_ERNEST);
					} else if (s1Menu == 1) {
						say(player, n, "What does this machine do?");
						oddensteinDialogue(player, n, Oddenstein.MACHINE);
					} else if (s1Menu == 2) {
						say(player, n, "Is this your house?");
						oddensteinDialogue(player, n, Oddenstein.HOUSE);
					}
					break;
				case 2:
					npcsay(player, n, "Have you found anything yet?");

					// no items
					if (!player.getCarriedItems().hasCatalogID(ItemId.RUBBER_TUBE.id(), Optional.of(false))
						&& !player.getCarriedItems().hasCatalogID(ItemId.PRESSURE_GAUGE.id(), Optional.of(false))
						&& !player.getCarriedItems().hasCatalogID(ItemId.OIL_CAN.id(), Optional.of(false))) {
						say(player, n, "I'm afraid I don't have any yet!");
						npcsay(player, n,
							"I need a rubber tube, a pressure gauge and a can of oil",
							"Then your friend can stop being a chicken");
					}
					// all items
					else if (player.getCarriedItems().hasCatalogID(ItemId.RUBBER_TUBE.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.PRESSURE_GAUGE.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.OIL_CAN.id(), Optional.of(false))) {
						say(player, n, "I have everything");
						npcsay(player, n, "Give em here then");
						mes("You give a rubber tube, a pressure gauge and a can of oil to the Professer");
						delay(3);
						mes("Oddenstein starts up the machine");
						delay(3);
						mes("The machine hums and shakes");
						delay(3);
						mes("Suddenly a ray shoots out of the machine at the chicken");
						delay(3);
						Npc chicken = ifnearvisnpc(player, NpcId.ERNEST_CHICKEN.id(), 20);
						if (chicken != null) {
							player.getCarriedItems().remove(new Item(ItemId.RUBBER_TUBE.id()));
							player.getCarriedItems().remove(new Item(ItemId.PRESSURE_GAUGE.id()));
							player.getCarriedItems().remove(new Item(ItemId.OIL_CAN.id()));
							Npc ernest = changenpc(chicken, NpcId.ERNEST.id(), true);
							delayedChangeErnest(player, ernest);
							npcsay(player, ernest, "Thank you sir",
								"It was dreadfully irritating being a chicken",
								"How can I ever thank you?");
							say(player, ernest,
								"Well a cash reward is always nice");
							npcsay(player, ernest, "Of course, of course");

							mes("Ernest hands you 300 coins");
							delay(3);
							player.sendQuestComplete(getQuestId());
						}
					}
					// some items
					else {
						say(player, n, "I have found some of the things you need:");
						if (player.getCarriedItems().hasCatalogID(ItemId.OIL_CAN.id()))
							say(player, n, "I have a can of oil");
						if (player.getCarriedItems().hasCatalogID(ItemId.PRESSURE_GAUGE.id()))
							say(player, n, "I have a pressure gauge");
						if (player.getCarriedItems().hasCatalogID(ItemId.RUBBER_TUBE.id()))
							say(player, n, "I have a rubber tube");

						npcsay(player, n, "Well that's a start", "You still need to find");
						if (!player.getCarriedItems().hasCatalogID(ItemId.OIL_CAN.id()))
							npcsay(player, n, "A can of oil");
						if (!player.getCarriedItems().hasCatalogID(ItemId.RUBBER_TUBE.id()))
							npcsay(player, n, "A rubber tube");
						if (!player.getCarriedItems().hasCatalogID(ItemId.PRESSURE_GAUGE.id()))
							npcsay(player, n, "A Pressure Gauge");
						say(player, n, "Ok I'll try and find them");
					}
					break;
			}
			return;
		}
		switch (cID) {
			case Oddenstein.HOUSE:
				npcsay(player, n, "No, I'm just one of the tenants",
					"It belongs to the count", "Who lives in the basement");
				break;
			case Oddenstein.MACHINE:
				npcsay(player, n, "Nothing at the moment", "As it's broken",
					"It's meant to be a transmutation machine",
					"It has also spent time as a time travel machine",
					"And a dramatic lightning generator",
					"And a thing for generating monsters");
				break;
			case Oddenstein.LOOKING_FOR_ERNEST:
				npcsay(player, n, "Ah Ernest, top notch bloke",
					"He's helping me with my experiments");
				say(player, n, "So you know where he is then?");
				npcsay(player, n, "He's that chicken over there");
				say(player, n, "Ernest is a chicken?", "Are you sure?");
				npcsay(player,
					n,
					"Oh he isn't normally a chicken",
					"Or at least he wasn't",
					"Until he helped me test my pouletmorph machine",
					"It was originally going to be called a transmutation machine",
					"But after testing Pouletmorph seems more appropriate");
				int choices = multi(
					player,
					n,
					"I'm glad Veronica didn't actually get engaged to a chicken",
					"Change him back this instant");
				if (choices == 0) {
					npcsay(player, n, "Who's Veronica?");
					say(player, n, "Ernest's fiancee",
						"She probably doesn't want to marry a chicken");
					npcsay(player, n, "Ooh I dunno",
						"She could have free eggs for breakfast");
					say(player, n, "I think you'd better change him back");
					oddensteinDialogue(player, n, Oddenstein.CHANGEBACK);
				} else if (choices == 1) {
					oddensteinDialogue(player, n, Oddenstein.CHANGEBACK);
				}
				break;
			case Oddenstein.CHANGEBACK:
				npcsay(player, n, "Um it's not so easy", "My machine is broken",
					"And the house gremlins",
					"Have run off with some vital bits");
				say(player, n, "Well I can look out for them");
				npcsay(player, n,
					"That would be a help",
					"They'll be somewhere in the manor house or its grounds",
					"The gremlins never go further than the entrance gate",
					"I'm missing the pressure gauge and a rubber tube",
					"They've also taken my oil can",
					"Which I'm going to need to get this thing started again");
				player.updateQuestStage(this, 2);
				break;
		}
	}

	private void delayedChangeErnest(Player player, Npc n) {
		try {
			player.getWorld().getServer().getGameEventHandler().add(
				new SingleEvent(player.getWorld(), null,
					config().GAME_TICK * 98,
					"Ernest Chicken Delayed Change Ernest", DuplicationStrategy.ALLOW_MULTIPLE) {
					@Override
					public void action() {
						changenpc(n, NpcId.ERNEST_CHICKEN.id(), true);
					}
				});
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	private void veronicaDialogue(Player player, Npc n, int i) {
		if (i == -1) {
			switch (player.getQuestStage(this)) {
				case 0:
					npcsay(player, n, "Can you please help me?",
						"I'm in a terrible spot of trouble");
					int choice = multi(player, n, false, //do not send over
						"Aha, sounds like a quest. I'll help",
						"No, I'm looking for something to kill");
					if (choice == 0) {
						say(player, n, "Aha, sounds like a quest", "I'll help");
						npcsay(player, n, "Yes yes I suppose it is a quest",
							"My fiance Ernest and I came upon this house here",
							"Seeing as we were a little lost",
							"Ernest decided to go in and ask for directions",
							"That was an hour ago",
							"That house looks very spooky",
							"Can you go and see if you can find him for me?");
						say(player, n, "Ok, I'll see what I can do");
						npcsay(player, n, "Thank you, thank you", "I'm very grateful");
						player.updateQuestStage(this, 1);
					} else if (choice == 1) {
						say(player, n, "No, I'm looking for something to kill");
						npcsay(player, n, "Oooh you violent person you");
					}
					break;
				case 1:
					npcsay(player, n, "Have you found my sweetheart yet?");
					say(player, n, "No, not yet");
					break;
				case 2:
					npcsay(player, n, "Have you found my sweetheart yet?");
					say(player, n, "Yes, he's a chicken");
					npcsay(player, n, "I know he's not exactly brave",
						"But I think you're being a little harsh");
					say(player, n,
						"No no he's been turned into an actual chicken",
						"By a mad scientist");
					mes("Veronica lets out an ear piecing shreek");
					delay(3);
					npcsay(player, n, "Eeeeek", "My poor darling",
						"Why must these things happen to us?");
					say(player, n, "Well I'm doing my best to turn him back");
					npcsay(player, n, "Well be quick",
						"I'm sure being a chicken can't be good for him");
					break;
				case -1:
					npcsay(player, n, "Thank you for rescuing Ernest");
					say(player, n, "Where is he now?");
					npcsay(player, n, "Oh he went off to talk to some green warty guy",
						"I'm sure he'll be back soon");
					break;
			}
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
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
	public void onOpBound(Player player, GameObject obj, Integer click) {
		Cache c = player.getCache();
		switch (obj.getID()) {
			case 35:
				//only allow is player is stuck, otherwise promote using key
				if (player.getX() == 211 && player.getY() == 545 && !player.getCarriedItems().hasCatalogID(ItemId.CLOSET_KEY.id(), Optional.of(false))) {
					doDoor(obj, player);
					player.message("You go through the door");
				} else {
					player.message("The door is locked");
				}
				break;
			case 36:
				if (player.getY() >= 553) {
					doDoor(obj, player);
					delay(5);
					player.message("The door slams behind you!");
				} else {
					player.message("The door won't open");
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
					doDoor(obj, player);
				} else
					player.message("The door is locked");
				break;
			case 28:
				if (c.hasKey("LeverD") && c.getBoolean("LeverD"))
					doDoor(obj, player);
				else
					player.message("The door is locked");
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
					doDoor(obj, player);
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
					doDoor(obj, player);
				} else
					player.message("The door is locked");
				break;
			case 26:
				if (c.hasKey("LeverF")
					&& c.hasKey("LeverD")
					&& c.hasKey("LeverB")
					&& c.getBoolean("LeverF")
					&& c.getBoolean("LeverD")
					&& !c.getBoolean("LeverB"))
					doDoor(obj, player);
				else
					player.message("The door is locked");
				break;
			case 27:
				if (c.hasKey("LeverA")
					&& c.hasKey("LeverB")
					&& c.hasKey("LeverD")
					&& c.getBoolean("LeverA")
					&& c.getBoolean("LeverB")
					&& c.getBoolean("LeverD"))
					doDoor(obj, player);
				else
					player.message("The door is locked");
				break;
			case 31:// Need to make Lever work xD haha brb
				if (c.hasKey("LeverD")
					&& c.hasKey("LeverB")
					&& c.hasKey("LeverF")
					&& c.getBoolean("LeverD")
					&& !c.getBoolean("LeverB")
					&& !c.getBoolean("LeverF")) // easiet
					doDoor(obj, player);
				else
					player.message("The door is locked");

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
					doDoor(obj, player);
				} else
					player.message("The door is locked");
				break;
			case 33:// second door from the right
				if (c.hasKey("LeverC")
					&& c.hasKey("LeverD")
					&& !c.getBoolean("LeverC")
					&& c.getBoolean("LeverD")) {
					doDoor(obj, player);
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
					doDoor(obj, player);
				} else
					player.message("The door is locked");
				break;
		}
		if (obj.getID() == 30 && obj.getX() == 226 && obj.getY() == 3378) {
			if (c.hasKey("LeverF")
				&& c.hasKey("LeverE")
				&& c.getBoolean("LeverF")
				&& !c.getBoolean("LeverE")) {
				doDoor(obj, player);
			} else {
				player.message("The door is locked");
			}
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return compareItemsIds(item1, item2, ItemId.FISH_FOOD.id(), ItemId.POISON.id());
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (compareItemsIds(item1, item2, ItemId.FISH_FOOD.id(), ItemId.POISON.id())) {
			player.getCarriedItems().remove(new Item(ItemId.FISH_FOOD.id()));
			player.getCarriedItems().remove(new Item(ItemId.POISON.id()));
			give(player, ItemId.POISONED_FISH_FOOD.id(), 1);
			player.message("You poison the fish food");
		}
	}

	@Override
	public boolean blockUseBound(Player player, GameObject obj, Item item) {
		return item.getCatalogId() == ItemId.CLOSET_KEY.id() && obj.getID() == 35;
	}

	@Override
	public void onUseBound(Player player, GameObject obj, Item item) {
		if (item.getCatalogId() == ItemId.CLOSET_KEY.id() && obj.getID() == 35) {
			doDoor(obj, player);
			player.message("You unlock the door");
			player.message("You go through the door");
			thinkbubble(item);
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
