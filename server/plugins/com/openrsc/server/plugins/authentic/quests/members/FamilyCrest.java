package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.external.Gauntlets;
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

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class FamilyCrest implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger,
	OpBoundTrigger,
	UseNpcTrigger,
	KillNpcTrigger {

	@Override
	public int getQuestId() {
		return Quests.FAMILY_CREST;
	}

	@Override
	public String getQuestName() {
		return "Family crest (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.FAMILY_CREST.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		final QuestReward reward = Quest.FAMILY_CREST.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		player.message("Well done you have completed the family crest quest");
	}

	/**
	 * NPCS: #309 - Dimintheis - quest starter #310 - Chef - 1st son in catherby
	 * #307 - man in alkharid #314 - wizard 3rd son.
	 */
	private static void dimintheisDialogue(Player player, Npc n, int cID) {
		if (cID == -1) {
			switch (player.getQuestStage(Quests.FAMILY_CREST)) {
				case 0:
					npcsay(player, n, "Hello, my name is Dimintheis",
						"Of the noble family of Fitzharmon");
					//do not send over
					int menu = multi(player, n, false,
						"Why would a nobleman live in a little hut like this?",
						"You're rich then?, can I have some money?",
						"Hi, I am bold adventurer");
					if (menu == 0) {
						say(player, n, "Why would a nobleman live in a little hut like this?");
						npcsay(player, n, "The king has taken my estate from me",
							"Until I can show him my family crest");
						int first = multi(player, n, "Why would he do that?",
							"So where is this crest?");
						if (first == 0) {
							dimintheisDialogue(player, n, Dimintheis.TRADITION);
						} else if (first == 1) {
							dimintheisDialogue(player, n, Dimintheis.THREE_SONS);
						}
					} else if (menu == 1) {
						say(player, n, "You're rich then?", "Can I have some money?");
						npcsay(player, n, "Lousy beggar",
							"There's to many of your sort about these days",
							"If I gave money to each of you who asked",
							"I'd be living on the streets myself");
					} else if (menu == 2) {
						say(player, n, "Hi, I am a bold adventurer");
						npcsay(player, n, "An adventurer hmm?",
							"I may have an adventure for you",
							"I desperatly need my family crest returning to me");
						int menu2 = multi(player, n, false, //do not send over
							"Why are you so desperate for it?",
							"So where is this crest?",
							"I'm not interested in that adventure right now");
						if (menu2 == 0) {
							say(player, n, "Why are you desperate for it?");
							dimintheisDialogue(player, n, Dimintheis.TRADITION);
						} else if (menu2 == 1) {
							say(player, n, "so where is this crest?");
							dimintheisDialogue(player, n, Dimintheis.THREE_SONS);
						} else if (menu2 == 2) {
							say(player, n, "I'm not interested in that adventure right now");
						}
					}
					break;
				case 1:
					say(player, n, "Where did you say I could find Caleb?");
					npcsay(player,
						n,
						"I heard word that my son Caleb is alive trying to earn his fortune",
						"As a great chef, far away in the lands beyond white wolf mountain");
					break;
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
					boolean gave_crest = false;
					if (player.getCarriedItems().hasCatalogID(ItemId.FAMILY_CREST.id(), Optional.of(false))) {
						say(player, n, "I have retrieved your crest");
						player.message("You give the crest to Dimintheis");
						player.getCarriedItems().remove(new Item(ItemId.FAMILY_CREST.id()));
						gave_crest = true;
					} else if (player.getCarriedItems().hasCatalogID(ItemId.CREST_FRAGMENT_ONE.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.CREST_FRAGMENT_TWO.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.CREST_FRAGMENT_THREE.id(), Optional.of(false))) {
						say(player, n, "I have retrieved your crest");
						player.message("You give the parts of the crest to Dimintheis");
						player.getCarriedItems().remove(new Item(ItemId.CREST_FRAGMENT_ONE.id()));
						player.getCarriedItems().remove(new Item(ItemId.CREST_FRAGMENT_TWO.id()));
						player.getCarriedItems().remove(new Item(ItemId.CREST_FRAGMENT_THREE.id()));
						gave_crest = true;
					}
					if (gave_crest) {
						player.getCache().remove("north_leverA");
						player.getCache().remove("south_lever");
						player.getCache().remove("north_leverB");
						npcsay(player, n, "Thankyou for your kindness",
							"I cannot express my gratitude enough",
							"You truly are a great hero");
						player.sendQuestComplete(Quests.FAMILY_CREST);
						npcsay(player,
							n,
							"How can I reward you I wonder?",
							"I suppose these gauntlets would make a good reward",
							"If you die you will always retain these gauntlets");
						player.message("Dimintheis gives you a pair of gauntlets");
						give(player, ItemId.STEEL_GAUNTLETS.id(), 1);
						player.getCache().set("famcrest_gauntlets", Gauntlets.STEEL.id());
						npcsay(player,
							n,
							"These gautlets can be granted extra powers",
							"Take them to one of my boys, they can each do something to them",
							"Though they can only receive one of the three powers");

						return;
					}
					npcsay(player, n, "How are you doing finding the crest");
					say(player, n, "I don't have it yet");
					break;
				case -1:
					npcsay(player, n, "Thankyou for saving our family honour",
						"We will never forget you");
					if (config().CAN_RETRIEVE_POST_QUEST_ITEMS) {
						disenchantGauntlets(player, n);
					}
					break;
			}
		}
		switch (cID) {
			case Dimintheis.THREE_SONS:
				npcsay(player,
					n,
					"Well my 3 sons took it with them many years ago",
					"When they rode out to fight in the war",
					"Against the undead necromancer and his army",
					"I didn't hear from them for many years and mourned them dead",
					"However recently I heard word that my son Caleb is alive",
					"trying to earn his fortune",
					"As a great chef, far away in the lands beyond white wolf mountain");
				int menu3 = multi(player, n, false, //do not send over
					"Ok I will help you",
					"I'm not interested in that adventure right now");
				if (menu3 == 0) {
					say(player, n, "Ok, I will help you");
					npcsay(player, n, "I thank you greatly",
						"If you find Caleb send him my love");
					player.updateQuestStage(Quests.FAMILY_CREST, 1); // QUEST STARTED.
				} else if (menu3 == 1) {
					say(player, n, "I'm not interested in that adventure right now");
				}
				break;
			case Dimintheis.TRADITION:
				npcsay(player,
					n,
					"We have this tradition in the Varrocian arostocracy",
					"Each noble family has an ancient crest",
					"This represents the honour and lineage of the family",
					"If you are to lose this crest, the family's estate is given to the crown",
					"until the crest is returned",
					"In times past when there was much infighting between the various families",
					"Capturing a family's crest meant you captured their land");
				say(player, n, "so where is this crest?");
				dimintheisDialogue(player, n, Dimintheis.THREE_SONS);
				break;
		}
	}

	public static void disenchantGauntlets(Player player, Npc npc) {
		int goldCost = 200000;
		int drunkDragons = 3;
		if (player.getCarriedItems().getInventory().hasCatalogID(Gauntlets.getById(getGauntletEnchantment(player)).catalogId())
		&& getGauntletEnchantment(player) != Gauntlets.STEEL.id()) {

			boolean hasBoughtDrinks = player.getCarriedItems().getInventory().countId(ItemId.BLURBERRY_BARMAN_DRUNK_DRAGON.id(), Optional.of(false)) >= drunkDragons;
			boolean hasMadeDrinks = player.getCarriedItems().getInventory().countId(ItemId.DRUNK_DRAGON.id(), Optional.of(false)) >= drunkDragons;

			if ((hasBoughtDrinks || hasMadeDrinks)
				&& player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= goldCost) {

				if (multi(player, npc, "You're welcome", "I've got your stuff, let's do this") == 1) {
					for (int i = 0; i < drunkDragons; i++) {
						mes("You give a Drunk dragon to Dimintheis");
						delay(3);
						if (hasBoughtDrinks) {
							player.getCarriedItems().remove(new Item(ItemId.BLURBERRY_BARMAN_DRUNK_DRAGON.id()));
						} else if (hasMadeDrinks) {
							player.getCarriedItems().remove(new Item(ItemId.DRUNK_DRAGON.id()));
						}
					}
					mes("You give " + goldCost + " coins to Dimintheis");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), goldCost));
					mes("You give your gauntlets to Dimintheis");
					delay(3);
					Item itemToRemove = player.getCarriedItems().getEquipment().get(
						player.getCarriedItems().getEquipment().searchEquipmentForItem(
							Gauntlets.getById(getGauntletEnchantment(player)).catalogId()));
					if (itemToRemove == null) {
						itemToRemove = player.getCarriedItems().getInventory().get(
							player.getCarriedItems().getInventory().getLastIndexById(
								Gauntlets.getById(getGauntletEnchantment(player)).catalogId(), Optional.of(false)));
					}
					if (itemToRemove == null) return;
					player.getCarriedItems().remove(itemToRemove);
					mes("Dimintheis takes your gauntlets");
					delay(3);
					mes("He mutters some words that you don't understand");
					delay(3);
					mes("He hands you back a pair of steel gauntlets");
					delay(3);
					give(player, Gauntlets.STEEL.catalogId(), 1);
					player.getCache().set("famcrest_gauntlets", Gauntlets.STEEL.id());
					npcsay(player, npc, "It's done",
						"Just don't tell my kids about this");
				}

			} else {
				if (multi(player, npc, "You're welcome", "I may have made an error in judgement...") == 1) {
					npcsay(player, npc, "How so?");
					int choice = multi(player, npc, "I should have sided with the demon",
						"While I love my enchanted gauntlets, I would like a different enchantment");
					if (choice == 0) {
						npcsay(player, npc, "Bit late for that now...");
					} else if (choice == 1) {
						npcsay(player, npc, "Alright I can disenchant your gauntlets",
							"If you do me a favor");
						say(player, npc, "I'm all ears");
						npcsay(player, npc, "I'm going to need " + drunkDragons + " Drunk dragons and " + goldCost + " coins");
						choice = multi(player, npc, "Sure no problem",
							"No way",
							"How am I supposed to get a dragon drunk?");
						if (choice == 2) {
							npcsay(player, npc, "You don't",
								"It's a fancy-schmancy cocktail made by the little folk that live in trees");
							multi(player, npc, "Sure I can do that",
								"That seems like too much effort, I'll pass");
						}
					}
				}
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.DIMINTHEIS.id() || n.getID() == NpcId.AVAN.id() || n.getID() == NpcId.JOHNATHON.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			freePlayerDialogue(player, n);
			return;
		}
		if (n.getID() == NpcId.DIMINTHEIS.id()) {
			dimintheisDialogue(player, n, -1);
		}
		else if (n.getID() == NpcId.AVAN.id()) {
			switch (player.getQuestStage(this)) {
				case -1:
					npcsay(player, n, "I have heard word from my father",
						"Thankyou for helping to restore our family honour");
					if (player.getCarriedItems().hasCatalogID(ItemId.STEEL_GAUNTLETS.id(), Optional.of(false))
						&& getGauntletEnchantment(player) == Gauntlets.STEEL.id()) {
						say(player, n,
							"Your father said that you could improve these Gauntlets in some way for me");
						npcsay(player,
							n,
							"Indeed I can",
							"In my quest to find the perfect gold I learned a lot",
							"I can make it so when you're wearing these");
						npcsay(player, n, "You gain more experience when smithing gold");
						int menu = multi(player, n, false, //do not send over
							"That sounds good, improve them for me",
							"I think I'll check my other options with your brothers");
						if (menu == 0) {
							say(player, n, "That sounds good, enchant them for me");
							mes("Avan takes out a little hammer");
							delay(3);
							mes("He starts pounding on the gauntlets");
							delay(3);
							mes("Avan hands the gauntlets to you");
							delay(3);
							Item itemToRemove = player.getCarriedItems().getEquipment().get(
								player.getCarriedItems().getEquipment().searchEquipmentForItem(
									ItemId.STEEL_GAUNTLETS.id()));
							if (itemToRemove == null) {
								itemToRemove = player.getCarriedItems().getInventory().get(
									player.getCarriedItems().getInventory().getLastIndexById(
										ItemId.STEEL_GAUNTLETS.id(), Optional.of(false)));
							}
							if (itemToRemove == null) return;
							player.getCarriedItems().remove(itemToRemove);
							player.getCarriedItems().getInventory().add(new Item(ItemId.GAUNTLETS_OF_GOLDSMITHING.id()));
							player.getCache().set("famcrest_gauntlets", Gauntlets.GOLDSMITHING.id());
						} else if (menu == 1) {
							say(player, n, "I think I'll check my other options with your brothers");
							npcsay(player, n,
								"Ok if you insist on getting help from the likes of them");
						}
					}
					break;
				case 0:
				case 1:
				case 2:
				case 3:
					npcsay(player, n, "Can't you see I'm busy?");
					break;
				case 4:
					int menu = multi(player, n, false, //do not send over
						"Why are you hanging around in a scorpion pit?",
						"I'm looking for a man named Avan");
					if (menu == 0) {
						say(player, n, "Why are you hanging about in a scorpion pit?");
						npcsay(player, n, "It's a good place to find gold");
					} else if (menu == 1) {
						say(player, n, "I'm looking for a man named Avan");
						npcsay(player, n, "I'm called Avan yes");
						say(player, n, "You have part of a crest",
							"I have been sent to fetch it");
						npcsay(player, n,
							"Is one of my good for nothing brothers after it again?");
						say(player, n, "no your father would like it back");
						npcsay(player,
							n,
							"Oh Dad wants it this time",
							"Well I'll tell you what I'll do",
							"I'm trying to obtain the perfect jewellry",
							"There is a lady I am trying to impress",
							"What I want is a gold ring with a red stone in",
							"And a necklace to match",
							"Not just any gold mind you",
							"The gold in these rocks doesn't seem to be of the best quality",
							"I want as good a quality as you can get");
						say(player, n, "Any ideas where I can find that?");
						npcsay(player,
							n,
							"Well I have been looking for such gold for a while",
							"My latest lead was a dwarf named Boot",
							"Though he has gone back to his home in the mountain now");
						say(player, n, "Ok I will try to get what you are after");
						player.updateQuestStage(this, 5);
					}
					break;
				case 5:
					npcsay(player, n, "So how are you doing getting the jewellry?");
					say(player, n, "I'm still after that perfect gold");
					npcsay(player, n,
						"Well I have been looking for such gold for a while",
						"My latest lead was a dwarf named Boot",
						"Though he has gone back to his home in the mountain now");
					break;
				case 6:
					npcsay(player, n, "So how are you doing getting the jewellry?");
					if (player.getCarriedItems().hasCatalogID(ItemId.RUBY_RING_FAMILYCREST.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.RUBY_NECKLACE_FAMILYCREST.id(), Optional.of(false))) {
						say(player, n, "I have it");
						npcsay(player, n, "These are brilliant");
						player.message("You exchange the jewellry for a piece of crest");
						player.getCarriedItems().remove(new Item(ItemId.RUBY_RING_FAMILYCREST.id()));
						player.getCarriedItems().remove(new Item(ItemId.RUBY_NECKLACE_FAMILYCREST.id()));
						give(player, ItemId.CREST_FRAGMENT_TWO.id(), 1);
						npcsay(player,
							n,
							"These are a fine piece of work",
							"Such marvelous gold to",
							"I suppose you will be after the last piece of crest now",
							"I heard my brother Johnathon is now a young mage",
							"He is hunting some demon in the wilderness",
							"But he's not doing a very good job of it",
							"He spends most his time recovering in an inn",
							"on the edge of the wilderness");
						player.updateQuestStage(this, 7);
					} else {
						say(player, n,
							"I have spoken to boot about the perfect gold",
							"I haven't bought you your jewellry yet though");
						npcsay(player, n,
							"Remember I want a gold ring with a red stone in",
							"And a necklace to match");
					}
					break;
				case 7:
					say(player, n,
						"Where did you say I could find Johnathon again?");
					npcsay(player, n,
						"I heard my brother Johnathon is now a young mage",
						"He is hunting some demon in the wilderness",
						"But he's not doing a very good job of it",
						"He spends most his time recovering in an inn",
						"on the edge of the wilderness");
					break;
				case 8:
					npcsay(player, n, "How are you doing getting the rest of the crest?");
					if (player.getCarriedItems().hasCatalogID(ItemId.FAMILY_CREST.id(), Optional.of(false))) {
						say(player, n, "I have found it");
						npcsay(player, n, "Well done, take it to my father");
					} else if (!player.getCarriedItems().hasCatalogID(ItemId.CREST_FRAGMENT_TWO.id(), Optional.of(false))) {
						int menu2 = multi(player, n,
							"I am still working on it",
							"I have lost the piece you gave me");
						if (menu2 == 0) {
							npcsay(player, n, "Well good luck in your quest");
						} else if (menu2 == 1) {
							npcsay(player, n, "Ah well here is another one");
							give(player, ItemId.CREST_FRAGMENT_TWO.id(), 1);
						}
					} else {
						say(player, n, "I am still working on it");
						npcsay(player, n, "Well good luck in your quest");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.JOHNATHON.id()) {
			if (player.getQuestStage(this) >= 0 && player.getQuestStage(this) < 7) {
				npcsay(player, n, "I am so very tired, leave me to rest");
			} else if (player.getQuestStage(this) == 7) {
				if (player.getCache().hasKey("johnathon_ill")) {
					npcsay(player, n, "Arrgh what has that spider done to me",
						"I feel so ill, I can hardly think");
					return;
				}
				say(player, n, "Greetings, are you Johnathon Fitzharmon?");
				npcsay(player, n, "That is I");
				say(player, n,
					"I seek your fragment of the Fitzharmon family quest");
				npcsay(player, n, "The poison it is too much",
					"arrgh my head is all of a spin");
				player.message("Sweat is pouring down Johnathon's face");
				player.getCache().store("johnathon_ill", true);
			} else if (player.getQuestStage(this) == 8) {
				if (player.getCarriedItems().hasCatalogID(ItemId.CREST_FRAGMENT_THREE.id(), Optional.of(false))) {
					say(player, n, "I have your part of the crest now");
					npcsay(player, n,
						"Well done take it to my father");
					return;
				}
				npcsay(player, n,
					"I'm trying to kill the demon chronozon  that you mentioned");
				int menu = multi(player, n,
					"So is this Chronozon hard to defeat?",
					"Where can I find Chronozon?", "Wish me luck");
				if (menu == 0) {
					DEFEAT(player, n);
				} else if (menu == 1) {
					FIND(player, n);
				} else if (menu == 2) {
					npcsay(player, n, "Good luck");
				}
			} else if (player.getQuestStage(this) == -1) {
				npcsay(player, n, "Hello again");
				if (player.getCarriedItems().hasCatalogID(ItemId.STEEL_GAUNTLETS.id(), Optional.of(false)) && getGauntletEnchantment(player) == Gauntlets.STEEL.id()) {
					say(player, n,
						"Your father tells me, you can improve these gauntlets a bit");
					npcsay(player,
						n,
						"He would be right",
						"Though I didn't get good enough at the death spells to defeat chronozon",
						"I am pretty good at the chaos spells",
						"I can enchant your gauntlets so that your bolt spells are more effective");
					int menu = multi(player, n, "That sounds good to me",
						"I shall see what options your brothers can offer me first");
					if (menu == 0) {
						mes("Johnathon waves his staff");
						delay(3);
						mes("The gauntlets sparkle and shimmer");
						delay(3);
						Item itemToRemove = player.getCarriedItems().getEquipment().get(
							player.getCarriedItems().getEquipment().searchEquipmentForItem(
								ItemId.STEEL_GAUNTLETS.id()));
						if (itemToRemove == null) {
							itemToRemove = player.getCarriedItems().getInventory().get(
								player.getCarriedItems().getInventory().getLastIndexById(
									ItemId.STEEL_GAUNTLETS.id(), Optional.of(false)));
						}
						if (itemToRemove == null) return;
						player.getCarriedItems().remove(itemToRemove);
						player.getCarriedItems().getInventory().add(new Item(ItemId.GAUNTLETS_OF_CHAOS.id()));
						player.getCache().set("famcrest_gauntlets", Gauntlets.CHAOS.id());
					} else if (menu == 0) {
						npcsay(player, n,
							"Boring crafting and cooking enhacements knowing them");
					}
				} else {
					npcsay(player, n, "My family now considers you a hero");
				}
			}
		}
	}

	// All recreated/reconstructed
	private void freePlayerDialogue(Player player, Npc n) {
		if (n.getID() == NpcId.DIMINTHEIS.id()) {
			npcsay(player, n, "Hello traveller, can't talk now",
				"maybe you can come back later");
		} else if (n.getID() == NpcId.JOHNATHON.id()) {
			npcsay(player, n, "I am so very tired, leave me to rest");
		} else if (n.getID() == NpcId.AVAN.id()) {
			npcsay(player, n, "Can't you see I'm busy?");
		}
	}

	public static int getGauntletEnchantment(Player player) {
		try {
			return player.getCache().getInt("famcrest_gauntlets");
		} catch (Exception e) {
			return Gauntlets.STEEL.id();
		}
	}

	/**
	 * DOORS: #91 - front door #88 - south left door #90 - south right door #92
	 * - north door
	 * <p>
	 * p.message("The door is locked"); p.message("The door swings open");
	 * p.message("You go through the door");
	 **/

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return (obj.getID() == 88 && obj.getX() == 509 && obj.getY() == 3441) || (obj.getID() == 90 && obj.getX() == 512 && obj.getY() == 3441)
				|| obj.getID() == 91 || obj.getID() == 92;
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		switch (obj.getID()) {
			case 88:
				if (player.getCache().hasKey("north_leverA")
					&& player.getCache().hasKey("south_lever")
					&& player.getCache().getBoolean("north_leverA")
					&& player.getCache().getBoolean("south_lever")) {
					player.message("The door swings open");
					player.message("You go through the door");
					doDoor(obj, player);
				} else if (player.getCache().hasKey("north_leverA")
					&& player.getCache().hasKey("north_leverB")
					&& player.getCache().hasKey("south_lever")
					&& player.getCache().getBoolean("north_leverA")
					&& player.getCache().getBoolean("north_leverB")
					&& player.getCache().getBoolean("south_lever")) {
					player.message("The door swings open");
					player.message("You go through the door");
					doDoor(obj, player);

				} else {
					player.message("The door is locked");
				}
				break;
			case 90:
				if (player.getCache().hasKey("north_leverA")
					&& player.getCache().hasKey("south_lever")
					&& player.getCache().getBoolean("north_leverA")
					&& !player.getCache().getBoolean("south_lever")) {
					player.message("The door swings open");
					player.message("You go through the door");
					doDoor(obj, player);
				} else if (
					player.getCache().hasKey("north_leverA")
						&& player.getCache().hasKey("north_leverB")
						&& player.getCache().hasKey("south_lever")
						&& player.getCache().getBoolean("north_leverA")
						&& player.getCache().getBoolean("north_leverB")
						&& !player.getCache().getBoolean("south_lever")) {
					player.message("The door swings open");
					player.message("You go through the door");
					doDoor(obj, player);
				} else {
					player.message("The door is locked");
				}
				break;
			case 91:
				if (player.getCache().hasKey("north_leverA")
					&& player.getCache().hasKey("north_leverB")
					&& player.getCache().hasKey("south_lever")
					&& player.getCache().getBoolean("north_leverA")
					&& player.getCache().getBoolean("north_leverB")
					&& !player.getCache().getBoolean("south_lever")) {
					player.message("The door swings open");
					player.message("You go through the door");
					doDoor(obj, player);
				} else if (player.getQuestStage(this) == -1) { // FREE ACCESS TO THE
					// HELLHOUND ROOM AFTER
					// COMPLETING QUEST
					player.message("The door swings open");
					player.message("You go through the door");
					doDoor(obj, player);
				} else {
					player.message("The door is locked");
				}
				break;
			case 92:
				if (player.getCache().hasKey("north_leverA")
					&& (player.getCache().hasKey("north_leverB")
					|| player.getCache().hasKey("south_lever"))
					&& !player.getCache().getBoolean("north_leverA")
					&& (player.getCache().getBoolean("south_lever") || player.getCache()
					.getBoolean("north_leverB"))) {
					player.message("The door swings open");
					player.message("You go through the door");
					doDoor(obj, player);
				} else {
					player.message("The door is locked");
				}
				break;
		}
	}

	/**
	 * LEVERS: #316 - north lever infront of door #318 - north lever inside door
	 * #317 - south lever inside door
	 * <p>
	 * p.message("You pull the lever down"); p.message("you hear a clunk");
	 **/

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 316 || obj.getID() == 317 || obj.getID() == 318;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (command.equalsIgnoreCase("pull") && (obj.getID() == 316 || obj.getID() == 317 || obj.getID() == 318))
			doLever(player, obj.getID());
		else if (command.equalsIgnoreCase("inspect") && (obj.getID() == 316 || obj.getID() == 317 || obj.getID() == 318))
			inspectLever(player, obj.getID());
	}

	public void inspectLever(Player player, int objectID) {
		if (player.getQuestStage(Quests.FAMILY_CREST) == -1) {
			player.message("nothing interesting happens"); // SAID SO ON WIKI.
			return;
		}
		String leverName = null;
		if (objectID == 316) {
			leverName = "north_leverA";
		} else if (objectID == 317) {
			leverName = "south_lever";
		} else if (objectID == 318) {
			leverName = "north_leverB";
		}
		player.message("The lever is "
			+ (player.getCache().hasKey(leverName) && player.getCache().getBoolean(leverName) ? "down" : "up"));
	}

	public void doLever(Player player, int objectID) {
		if (player.getQuestStage(Quests.FAMILY_CREST) == -1) {
			player.message("nothing interesting happens"); // SAID SO ON WIKI.
			return;
		}
		if (!player.getCache().hasKey("north_leverA")) {
			player.getCache().store("north_leverA", false);
			player.getCache().store("south_lever", false);
			player.getCache().store("north_leverB", false);
		}
		String leverName = null;
		if (objectID == 316) {
			leverName = "north_leverA";
		} else if (objectID == 317) {
			leverName = "south_lever";
		} else if (objectID == 318) {
			leverName = "north_leverB";
		}
		player.getCache().store(leverName, !player.getCache().getBoolean(leverName));
		player.message("You pull the lever "
			+ (player.getCache().getBoolean(leverName) ? "down" : "up"));
		player.message("you hear a clunk");
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.JOHNATHON.id() && !item.getNoted() &&
			DataConversions.inArray(new int[]{ItemId.FULL_CURE_POISON_POTION.id(), ItemId.TWO_CURE_POISON_POTION.id(),
				ItemId.ONE_CURE_POISON_POTION.id()}, item.getCatalogId());
	}

	@Override
	public void onUseNpc(Player player, Npc n, Item item) {
		if (n.getID() == NpcId.JOHNATHON.id() && !item.getNoted() &&
			DataConversions.inArray(new int[]{ItemId.FULL_CURE_POISON_POTION.id(), ItemId.TWO_CURE_POISON_POTION.id(),
				ItemId.ONE_CURE_POISON_POTION.id()}, item.getCatalogId())) {
			if (player.getQuestStage(this) == 7) {
				mes("You feed your potion to Johnathon");
				delay(3);
				player.getCarriedItems().remove(new Item(item.getCatalogId()));
				player.updateQuestStage(this, 8);
				if (player.getCache().hasKey("johnathon_ill")) {
					player.getCache().remove("johnathon_ill");
				}
				npcsay(player, n, "Wow I'm feeling a lot better now",
					"Thankyou, what can I do for you?");
				say(player, n,
					"I'm after your part of the fitzharmon family crest");
				npcsay(player,
					n,
					"Ooh I don't think I have that anymore",
					"I have been trying to slay chronozon the blood demon",
					"and I think I dropped a lot of my things near him when he drove me away",
					"He will have it now");
				int menu = multi(player, n,
					"So is this Chronozon hard to defeat?",
					"Where can I find Chronozon?",
					"So how did you end up getting poisoned");
				if (menu == 0) {
					DEFEAT(player, n);
				} else if (menu == 1) {
					FIND(player, n);
				} else if (menu == 2) {
					POISONED(player, n);
				}
			} else {
				player.message("nothing interesting happens");
			}
		}
	}

	private void DEFEAT(Player player, Npc n) {
		npcsay(player, n, "Well you will need to be a good mage",
			"And I don't seem to be able to manage it",
			"He will need to be hit by the 4 elemental spells of death",
			"Before he can be defeated");
		int menu = multi(player, n, "Where can I find Chronozon?",
			"So how did you end up getting poisoned",
			"I will be on my way now");
		if (menu == 0) {
			FIND(player, n);
		} else if (menu == 1) {
			POISONED(player, n);
		}
	}

	private void POISONED(Player player, Npc n) {
		npcsay(player, n,
			"There are spiders towards the entrance to Chronozon's cave",
			"I must have taken a nip from one of them");
		int menu2 = multi(player, n, "So is this Chronozon hard to defeat?",
			"Where can I find Chronozon?", "I will be on my way now");
		if (menu2 == 0) {
			DEFEAT(player, n);
		} else if (menu2 == 1) {
			FIND(player, n);
		}
	}

	private void FIND(Player player, Npc n) {
		npcsay(player, n,
			"He is in the wilderness, somewhere below the obelisk of air");
		int menu = multi(player, n, "So is this Chronozon hard to defeat?",
			"So how did you end up getting poisoned",
			"I will be on my way now");
		if (menu == 0) {
			DEFEAT(player, n);
		} else if (menu == 1) {
			POISONED(player, n);
		}
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.CHRONOZON.id();
	}

	/**
	 * LAST FIGHT MESSAGES p.message("chronozon weakens");
	 * p.message("Chronozon regenerates"); FINALLY IF ALL SPELLS CASTED HE DIES
	 * AND DROP LAST CREST PIECE: ID: 697 (ONLY DROPPING IF QUEST STAGE 8)
	 */

	@Override
	public void onKillNpc(Player player, Npc npc) {
		if (npc.getID() == NpcId.CHRONOZON.id()) {
			String[] elementals = new String[]{"wind", "water", "earth",
				"fire"};
			boolean regenerate = false;
			for (String s : elementals) {
				if (!player.getAttribute("chronoz_" + s, false)) {
					regenerate = true;
					break;
				}
			}
			if (regenerate) {
				npc.getSkills().setLevel(Skill.HITS.id(), npc.getDef().hits);
				player.message("Chronozon regenerates");
				npc.killed = false;
			} else {
				player.getWorld().registerItem(
					new GroundItem(player.getWorld(), ItemId.ASHES.id(), npc.getX(), npc.getY(), 1, player));
				if (player.getQuestStage(this) == 8) {
					player.getWorld().registerItem(
						new GroundItem(player.getWorld(), ItemId.CREST_FRAGMENT_THREE.id(), npc.getX(), npc.getY(), 1, player));
				}
				npc.remove();
			}
		}

	}

	class Dimintheis {
		public static final int THREE_SONS = 0;
		public static final int TRADITION = 1;
	}

}
