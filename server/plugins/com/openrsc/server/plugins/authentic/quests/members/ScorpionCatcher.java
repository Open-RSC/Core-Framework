package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.content.minigame.combatodyssey.CombatOdysseyData;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.custom.minigames.CombatOdyssey;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.plugins.triggers.UseBoundTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class ScorpionCatcher implements QuestInterface, TalkNpcTrigger,
	UseNpcTrigger,
	UseBoundTrigger,
	OpBoundTrigger {

	// items 679 (scorpion1 taverly), 686 (scorpion2 barbarian), 687 (scorpion3 monastery)

	@Override
	public int getQuestId() {
		return Quests.SCORPION_CATCHER;
	}

	@Override
	public String getQuestName() {
		return "Scorpion catcher (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.SCORPION_CATCHER.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the scorpion catcher quest");
		final QuestReward reward = Quest.SCORPION_CATCHER.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.THORMAC_THE_SORCEROR.id() || n.getID() == NpcId.SEER.id() || n.getID() == NpcId.VELRAK_THE_EXPLORER.id();
	}

	private void seerDialogue(Player player, Npc n, int cID) {
		if (cID == -1) {
			switch (player.getQuestStage(this)) {
				case 0:
				case -1:
					seerDialogue(player, n, SEER_NPC.PRIMARY_DIALOGUE);
					break;
				case 1:
					npcsay(player, n, "Many greetings");
					int first = multi(player, n,
						"I need to locate some scorpions",
						"Your friend Thormac sent me to speak to you",
						"I seek knowledge and power");
					if (first == 0) {
						seerDialogue(player, n, SEER_NPC.LOCATE_SCORPIONS);
					} else if (first == 1) {
						npcsay(player, n, "What does the old fellow want");
						say(player, n, "He's lost his valuable lesser kharid scorpions");
						seerDialogue(player, n, SEER_NPC.LOCATE_SCORPIONS);
					} else if (first == 2) {
						npcsay(player, n, "Knowledge comes from experience, power comes from battleaxes");
					}
					break;
				case 2:

					// Still needs first scorpion
					if (!player.getCarriedItems().hasCatalogID(ItemId.SCORPION_CAGE_ONE.id(), Optional.of(false)) &&
						!player.getCarriedItems().hasCatalogID(ItemId.SCORPION_CAGE_ONE_TWO.id(), Optional.of(false)) &&
						!player.getCarriedItems().hasCatalogID(ItemId.SCORPION_CAGE_ONE_TWO_THREE.id(), Optional.of(false)) &&
						!player.getCarriedItems().hasCatalogID(ItemId.SCORPION_CAGE_ONE_THREE.id(), Optional.of(false))) {
						if (!player.getCarriedItems().hasCatalogID(ItemId.SCORPION_CAGE_NONE.id(), Optional.of(false))) {
							say(player, n, "I need to locate some scorpions");
							seerDialogue(player, n, SEER_NPC.LOCATE_SCORPIONS);
						} else {
							npcsay(player, n, "Many greetings");
							say(player, n, "Where did you say that scorpion was again?");
							npcsay(player, n, "Let me look into my looking glass");
							mes("The seer produces a small mirror");
							delay(3);
							mes("The seer gazes into the mirror");
							delay(3);
							mes("The seer smoothes his hair with his hand");
							delay(3);
							npcsay(player, n,
								"I can see a scorpion that you seek",
								"It would appear to be near some  nasty looking spiders",
								"I can see two coffins there as well",
								"The scorpion seems to be going through some crack in the wall",
								"He's gone into some sort of secret room",
								"Well see if you can find that scorpion then",
								"And I'll try and get you some information on the others");
						}
					}

					// Still needs second scorpion
					else if (!player.getCarriedItems().hasCatalogID(ItemId.SCORPION_CAGE_TWO.id(), Optional.of(false)) &&
						!player.getCarriedItems().hasCatalogID(ItemId.SCORPION_CAGE_ONE_TWO.id(), Optional.of(false)) &&
						!player.getCarriedItems().hasCatalogID(ItemId.SCORPION_CAGE_TWO_THREE.id(), Optional.of(false)) &&
						!player.getCarriedItems().hasCatalogID(ItemId.SCORPION_CAGE_ONE_TWO_THREE.id(), Optional.of(false))) {
						say(player, n, "Hi I have retrieved the scorpion from near the spiders");
						npcsay(player, n, "Well I've checked my looking glass",
							"There seems to be a kharid scorpion in a village full of  axe wielding warriors",
							"One of the warriors there, dressed mainly in black has picked it up",
							"That's all I can tell you about that scorpion");
					}

					// Still needs third scorpion
					else if (!player.getCarriedItems().hasCatalogID(ItemId.SCORPION_CAGE_THREE.id(), Optional.of(false)) &&
						!player.getCarriedItems().hasCatalogID(ItemId.SCORPION_CAGE_ONE_THREE.id(), Optional.of(false)) &&
						!player.getCarriedItems().hasCatalogID(ItemId.SCORPION_CAGE_TWO_THREE.id(), Optional.of(false)) &&
						!player.getCarriedItems().hasCatalogID(ItemId.SCORPION_CAGE_ONE_TWO_THREE.id(), Optional.of(false))) {
						npcsay(player, n, "Many greetings");
						say(player, n, "I have retrieved a second scoprion");
						npcsay(player, n,
							"That's lucky because I've got some information on the last scorpion for you",
							"It seems to be in some sort of upstairs room",
							"There seems to be some sort of brown clothing lying on the floor");
					} else seerDialogue(player, n, SEER_NPC.PRIMARY_DIALOGUE);
					break;
			}

		}
		switch (cID) {
			case SEER_NPC.LOCATE_SCORPIONS:
				npcsay(player, n, "Well you have come to the right place",
					"I am a master of animal detection",
					"Do you need to locate any particular scorpion",
					"Scorpions are a creature somewhat in abundance");
				say(player, n, "I'm looking for some lesser kharid scorpions",
					"They belong to Thormac the sorceror");
				npcsay(player, n, "Let me look into my looking glass");
				mes("The seer produces a small mirror");
				delay(3);
				mes("The seer gazes into the mirror");
				delay(3);
				mes("The seer smoothes his hair with his hand");
				delay(3);
				npcsay(player, n,
					"I can see a scorpion that you seek",
					"It would appear to be near some  nasty looking spiders",
					"I can see two coffins there as well",
					"The scorpion seems to be going through some crack in the wall",
					"He's gone into some sort of secret room",
					"Well see if you can find that scorpion then",
					"And I'll try and get you some information on the others");
				if (player.getQuestStage(this) == 1) {
					player.updateQuestStage(getQuestId(), 2);
				}
				break;

			case SEER_NPC.PRIMARY_DIALOGUE:
				// OG post-quest doesn't say this and jumps directly to menu
				if (player.getQuestStage(this) != -1) {
					npcsay(player, n, "Many greetings");
				}
				int menu = multi(player, n, "Many greetings", "I seek knowledge and power");
				if (menu == 1) {
					npcsay(player, n, "Knowledge comes from experience, power comes from battleaxes");
				}
				break;
		}
	}

	public void velrakDialogue(Player player, Npc n, int choice) {
		if (choice == -1) {
			if (player.getCarriedItems().hasCatalogID(ItemId.DUSTY_KEY.id(), Optional.of(false))) {
				say(player, n, "Are you still here?");
				npcsay(player, n, "Yes, I'm still plucking up courage",
					"To run out past those black knights");
				return;
			}
			npcsay(player, n, "Thankyou for rescuing me",
				"It isn't comfy in this cell");
			choice = multi(player, n,
				"So do you know anywhere good to explore?",
				"Do I get a reward?");

			if (choice > -1)
				velrakDialogue(player, n, choice);

		} else if (choice == 0) {
			npcsay(player, n, "Well this dungeon was quite good to explore",
				"Till I got captured",
				"I got given a key to an inner part of this dungeon",
				"By a mysterious cloaked stranger",
				"It's rather to tough for me to get that far though",
				"I keep getting captured",
				"Would you like to give it a go");
			choice = multi(player, n, "Yes please",
				"No it's to dangerous for me too");
			if (choice == 0) {
				mes("Velrak reaches inside his boot and passes you a key");
				delay(3);
				give(player, ItemId.DUSTY_KEY.id(), 1);
			}

		} else if (choice == 1) {
			npcsay(player, n,
				"Well not really the black knights took all my stuff before throwing me in here");
		}
	}

	public void thormacDialogue(Player player, Npc n, int choice) {
		if (choice == -1) {
			switch (player.getQuestStage(this)) {
				case 0:
					npcsay(player, n, "Hello I am Thormac the sorceror",
						"I don't suppose you could be of assistance to me?");
					int first = multi(player, n,
						"What do you need assistance with?",
						"I'm a little busy");
					if (first == 0) {
						thormacDialogue(player, n, 0);
					}
					break;
				case 1:
				case 2:
					npcsay(player, n, "How goes your quest?");
					if (!player.getCarriedItems().hasCatalogID(ItemId.SCORPION_CAGE_NONE.id(), Optional.of(false))
						&& !player.getCarriedItems().hasCatalogID(ItemId.SCORPION_CAGE_ONE_TWO_THREE.id(), Optional.of(false))) { // No empty cage, no full cage
						int menu = multi(player, n,
							"I've lost my cage",
							"I've not caught all the scorpions yet");
						if (menu == 0) {
							npcsay(player, n, "Ok here is another cage",
								"You're almost as bad at loosing things as me");
							give(player, ItemId.SCORPION_CAGE_NONE.id(), 1);
						} else if (menu == 1) {
							npcsay(player, n, "Well remember, go speak to the seers north of here if you need any help");
						}
					} else if (player.getCarriedItems().hasCatalogID(ItemId.SCORPION_CAGE_ONE_TWO_THREE.id(), Optional.of(false))) { // full cage
						say(player, n, "I have retrieved all your scorpions");
						npcsay(player, n, "aha my little scorpions home at last");
						player.getCarriedItems().remove(new Item(ItemId.SCORPION_CAGE_ONE_TWO_THREE.id()));
						player.sendQuestComplete(Quests.SCORPION_CATCHER);
					} else {
						say(player, n, "I've not caught all the scorpions yet");
						npcsay(player, n, "Well remember, go speak to the seers north of here if you need any help");
					}
					break;
				case -1:
					if (config().WANT_COMBAT_ODYSSEY
						&& CombatOdyssey.getCurrentTier(player) == 1
						&& CombatOdyssey.isTierCompleted(player)) {
						if (CombatOdyssey.biggumMissing()) return;
						int newTier = 2;
						CombatOdyssey.assignNewTier(player, newTier);
						npcsay(player, n, "Hello adventurer",
							"I suppose you're here on Radimus' mission?",
							"I have not forgotten your help in the past, and I wish you luck",
							"Radimus has asked me to send you to kill the following");
						npcsay(player, n, player.getWorld().getCombatOdyssey().getTier(newTier).getTasksAndCounts());
						CombatOdyssey.biggumSay(player, "Biggum keep track! Biggum help human!");
						npcsay(player, n, "...",
							"A most peculiar friend you have there",
							"Once done, you may seek out the ogre, Grew",
							"He will send you on the next part of this bizarre quest");
						return;
					}
					npcsay(player, n, "Thankyou for rescuing my scorpions");
					int four = multi(player, n, "That's ok",
						"You said you'd enchant my battlestaff for me");
					if (four == 1) {
						npcsay(player, n,
							"Yes it'll cost you 40000 coins for the materials needed mind you",
							"Which sort of staff did you want enchanting?");
						int five = multi(player, n, false, //do not send over
							"Battlestaff of fire", "battlestaff of water",
							"battlestaff of air", "battlestaff of earth",
							"I won't bother yet actually");
						if (five == 0) {
							say(player, n, "battlestaff of fire please");
							if (!player.getCarriedItems().hasCatalogID(ItemId.BATTLESTAFF_OF_FIRE.id(), Optional.of(false))) {
								say(player, n, "I don't have a battlestaff of fire yet though");
								return;
							}
							if (!ifheld(player, ItemId.COINS.id(), 40000)) {
								say(player, n, "I'll just get the money for you");
								return;
							}
							if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 40000)) != -1
								&& player.getCarriedItems().remove(new Item(ItemId.BATTLESTAFF_OF_FIRE.id())) != -1) {
								give(player, ItemId.ENCHANTED_BATTLESTAFF_OF_FIRE.id(), 1);
								player.message("Thormac enchants your staff");
							}
						} else if (five == 1) {
							say(player, n, "battlestaff of water please");
							if (!player.getCarriedItems().hasCatalogID(ItemId.BATTLESTAFF_OF_WATER.id(), Optional.of(false))) {
								say(player, n, "I don't have a battlestaff of water yet though");
								return;
							}
							if (!ifheld(player, ItemId.COINS.id(), 40000)) {
								say(player, n, "I'll just get the money for you");
								return;
							}
							if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 40000)) != -1
								&& player.getCarriedItems().remove(new Item(ItemId.BATTLESTAFF_OF_WATER.id())) != -1) {
								give(player, ItemId.ENCHANTED_BATTLESTAFF_OF_WATER.id(), 1);
								player.message("Thormac enchants your staff");
							}
						} else if (five == 2) {
							say(player, n, "battlestaff of air please");
							if (!player.getCarriedItems().hasCatalogID(ItemId.BATTLESTAFF_OF_AIR.id(), Optional.of(false))) {
								say(player, n, "I don't have a battlestaff of air yet though");
								return;
							}
							if (!ifheld(player, ItemId.COINS.id(), 40000)) {
								say(player, n, "I'll just get the money for you");
								return;
							}
							if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 40000)) != -1
								&& player.getCarriedItems().remove(new Item(ItemId.BATTLESTAFF_OF_AIR.id())) != -1) {
								give(player, ItemId.ENCHANTED_BATTLESTAFF_OF_AIR.id(), 1);
								player.message("Thormac enchants your staff");
							}
						} else if (five == 3) {
							say(player, n, "battlestaff of earth please");
							if (!player.getCarriedItems().hasCatalogID(ItemId.BATTLESTAFF_OF_EARTH.id(), Optional.of(false))) {
								say(player, n, "I don't have a battlestaff of earth yet though");
								return;
							}
							if (!ifheld(player, ItemId.COINS.id(), 40000)) {
								say(player, n, "I'll just get the money for you");
								return;
							}
							if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 40000)) != -1
								&& player.getCarriedItems().remove(new Item(ItemId.BATTLESTAFF_OF_EARTH.id())) != -1) {
								give(player, ItemId.ENCHANTED_BATTLESTAFF_OF_EARTH.id(), 1);
								player.message("Thormac enchants your staff");
							}
						} else if (five == 4) {
							say(player, n, "I won't bother yet actually");
						}
					}
					break;
			}
		}

		// What do you need assistance with?
		else if (choice == 0) {
			npcsay(player, n,
				"I've lost my pet scorpions",
				"They're lesser kharid scorpions, a very rare breed",
				"I left there cage door open",
				"now I don't know where they have gone",
				"There's 3 of them and they're quick little beasties",
				"They're all over runescape");
			choice = multi(player, n,
				"So how would I go about catching them then?",
				"What's in it for me?", "I'm not interested then");
			if (choice == 0) thormacDialogue(player, n, 1);
			else if (choice == 1) thormacDialogue(player, n, 2);
			else if (choice == 2) {
				npcsay(player, n, "Blast, I suppose I will have to have find someone else then");
			}
		}

		// So how would I go about catching them then?
		else if (choice == 1) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.SCORPION_CAGE_NONE.id(), Optional.of(false))) {
				npcsay(player, n, "Well I have a scorpion cage here",
					"Which you can use to catch them in");
				give(player, ItemId.SCORPION_CAGE_NONE.id(), 1);
				mes("Thormac gives you a cage");
				delay(3);
			} else {
				npcsay(player, n, "Well you have that scorpion cage I gave you",
					"Which you can use to catch them in");
			}
			npcsay(player, n,
				"If you go up to the village of seers to the north of here",
				"One of them will be able to tell you where the scorpions are now");

			if (player.getQuestStage(this) == 0) {
				player.updateQuestStage(getQuestId(), 1); // STARTED QUEST
			}

			choice = multi(player, n,
				"What's in it for me?",
				"Ok I will do it then");

			if (choice == 0) thormacDialogue(player, n, 2);

		}

		// What's in it for me?
		else if (choice == 2) {
			npcsay(player, n,
				"Well I suppose I can aid you with my skills as a staff sorcerer",
				"Most the battlestaffs around here are pretty puny",
				"I can beef them up for you a bit");

			choice = multi(player, n,
				"So how would I go about catching them then?",
				"Ok I will do it then");

			if (choice == 0) thormacDialogue(player, n, 1);
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.SEER.id()) {
			seerDialogue(player, n, -1);
		}
		else if (n.getID() == NpcId.VELRAK_THE_EXPLORER.id()) {
			velrakDialogue(player, n, -1);
		}
		else if (n.getID() == NpcId.THORMAC_THE_SORCEROR.id()) {
			thormacDialogue(player, n, -1);
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc n, Item i) {
		int cageId = i.getCatalogId();
		if (player.getCarriedItems().getInventory().countId(cageId) <= 0) return false;

		if (n.getID() == NpcId.KHARID_SCORPION_TAVERLEY.id() && // First Scorpion (Taverly)
			cageId != ItemId.SCORPION_CAGE_ONE.id() && cageId != ItemId.SCORPION_CAGE_ONE_TWO.id() &&
			cageId != ItemId.SCORPION_CAGE_ONE_TWO_THREE.id() && cageId != ItemId.SCORPION_CAGE_ONE_TWO_THREE.id()
		) {
			return true;
		} else if (n.getID() == NpcId.KHARID_SCORPION_BARBARIAN.id() && // Second Scorpion (Barbarian)
			cageId != ItemId.SCORPION_CAGE_TWO.id() && cageId != ItemId.SCORPION_CAGE_ONE_TWO.id() &&
			cageId != ItemId.SCORPION_CAGE_TWO_THREE.id() && cageId != ItemId.SCORPION_CAGE_ONE_TWO_THREE.id()
		) {
			return true;
		} else if (n.getID() == NpcId.KHARID_SCORPION_MONASTERY.id() && // Third Scorpion (Monastery)
			cageId != ItemId.SCORPION_CAGE_THREE.id() && cageId != ItemId.SCORPION_CAGE_ONE_TWO_THREE.id() &&
			cageId != ItemId.SCORPION_CAGE_TWO_THREE.id() && cageId != ItemId.SCORPION_CAGE_ONE_TWO_THREE.id()
		) {
			return true;
		}
		return false;
	}

	@Override
	public void onUseNpc(Player player, Npc n, Item i) {

		if (player.getQuestStage(this) == 2) {

			List<Integer> cages = new ArrayList<Integer>(Arrays.asList(
				ItemId.SCORPION_CAGE_NONE.id(), ItemId.SCORPION_CAGE_ONE.id(), ItemId.SCORPION_CAGE_TWO.id(), ItemId.SCORPION_CAGE_THREE.id(),
				ItemId.SCORPION_CAGE_ONE_TWO.id(), ItemId.SCORPION_CAGE_ONE_THREE.id(), ItemId.SCORPION_CAGE_TWO_THREE.id()
			));

			int itemId = i.getCatalogId();

			if (!cages.contains(itemId)) {
				player.message("Nothing interesting happens");
				return;
			}

			int toRemove = ItemId.NOTHING.id();
			int toAdd = ItemId.NOTHING.id();

			// Taverly scorpion
			if (n.getID() == NpcId.KHARID_SCORPION_TAVERLEY.id()) {
				switch (ItemId.getById(itemId)) {
					case SCORPION_CAGE_NONE:
						toRemove = itemId;
						toAdd = ItemId.SCORPION_CAGE_ONE.id();
						break;
					case SCORPION_CAGE_TWO:
						toRemove = itemId;
						toAdd = ItemId.SCORPION_CAGE_ONE_TWO.id();
						break;
					case SCORPION_CAGE_THREE:
						toRemove = itemId;
						toAdd = ItemId.SCORPION_CAGE_ONE_THREE.id();
						break;
					case SCORPION_CAGE_TWO_THREE:
						toRemove = itemId;
						toAdd = ItemId.SCORPION_CAGE_ONE_TWO_THREE.id();
						break;
					default:
						break;
				}
			}

			// Barbarian scorpion
			else if (n.getID() == NpcId.KHARID_SCORPION_BARBARIAN.id()) {
				switch (ItemId.getById(itemId)) {
					case SCORPION_CAGE_NONE:
						toRemove = itemId;
						toAdd = ItemId.SCORPION_CAGE_TWO.id();
						break;
					case SCORPION_CAGE_ONE:
						toRemove = itemId;
						toAdd = ItemId.SCORPION_CAGE_ONE_TWO.id();
						break;
					case SCORPION_CAGE_THREE:
						toRemove = itemId;
						toAdd = ItemId.SCORPION_CAGE_TWO_THREE.id();
						break;
					case SCORPION_CAGE_ONE_THREE:
						toRemove = itemId;
						toAdd = ItemId.SCORPION_CAGE_ONE_TWO_THREE.id();
						break;
					default:
						break;
				}
			}

			// Monastery scorpion
			else if (n.getID() == NpcId.KHARID_SCORPION_MONASTERY.id()) {
				switch (ItemId.getById(itemId)) {
					case SCORPION_CAGE_NONE:
						toRemove = itemId;
						toAdd = ItemId.SCORPION_CAGE_THREE.id();
						break;
					case SCORPION_CAGE_ONE:
						toRemove = itemId;
						toAdd = ItemId.SCORPION_CAGE_ONE_THREE.id();
						break;
					case SCORPION_CAGE_TWO:
						toRemove = itemId;
						toAdd = ItemId.SCORPION_CAGE_TWO_THREE.id();
						break;
					case SCORPION_CAGE_ONE_TWO:
						toRemove = itemId;
						toAdd = ItemId.SCORPION_CAGE_ONE_TWO_THREE.id();
						break;
					default:
						break;
				}
			}

			player.message("You catch a scorpion");

			if (toRemove > -1) player.getCarriedItems().remove(new Item(toRemove));
			if (toAdd > -1) give(player, toAdd, 1);
			delnpc(n, true);
		} else
			player.message("Talk to Seer before you attempt catching this scorpion");
	}

	@Override
	public boolean blockUseBound(Player player, GameObject obj, Item item) {
		return (obj.getID() == 83 && obj.getY() == 3428 && item.getCatalogId() == ItemId.JAIL_KEYS.id())
				|| (obj.getID() == 83 && obj.getY() == 3425 && item.getCatalogId() == ItemId.JAIL_KEYS.id())
				|| (obj.getID() == 84 && obj.getY() == 3353 && item.getCatalogId() == ItemId.DUSTY_KEY.id());
	}

	@Override
	public void onUseBound(Player player, GameObject obj, Item item) {
		/*
		 * Velrak cell door
		 */
		if (obj.getID() == 83 && obj.getY() == 3428 && item.getCatalogId() == ItemId.JAIL_KEYS.id()) {
			thinkbubble(item);
			doDoor(obj, player);
		}
		/*
		 * Below door infront of Velrak cell has nothing todo with quest or
		 * anything important at all - replicated it anyway.
		 */
		if (obj.getID() == 83 && obj.getY() == 3425 && item.getCatalogId() == ItemId.JAIL_KEYS.id()) {
			thinkbubble(item);
			doDoor(obj, player);
		}
		/*
		 * Dusty key door into blue dragons lair in Taverly dungeon
		 */
		if (obj.getID() == 84 && obj.getY() == 3353 && item.getCatalogId() == ItemId.DUSTY_KEY.id()) {
			thinkbubble(item);
			doDoor(obj, player);
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return obj.getID() == 87 && obj.getY() == 3353;
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == 87 && obj.getY() == 3353 && player.getQuestStage(this) == 2) {
			doDoor(obj, player);
			player.message("You just went through a secret door");
		}
	}

	class SEER_NPC {
		private static final int LOCATE_SCORPIONS = 0;
		private static final int PRIMARY_DIALOGUE = 1;
	}
}
