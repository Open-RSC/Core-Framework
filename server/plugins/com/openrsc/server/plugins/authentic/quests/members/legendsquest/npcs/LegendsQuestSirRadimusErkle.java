package com.openrsc.server.plugins.authentic.quests.members.legendsquest.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.util.rsc.StringUtil;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestSirRadimusErkle implements QuestInterface, TalkNpcTrigger, UseNpcTrigger {

	@Override
	public int getQuestId() {
		return Quests.LEGENDS_QUEST;
	}

	@Override
	public String getQuestName() {
		return "Legend's Quest (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.LEGENDS_QUEST.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.message("@gre@Well done - you have completed the Legends Guild Quest!");
		final QuestReward reward = Quest.LEGENDS_QUEST.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		delay(3);
		/** REMOVE QUEST CACHES **/
		String[] caches =
			{
				"gujuo_potion", "JUNGLE_EAST", "JUNGLE_MIDDLE", "JUNGLE_WEST",
				"already_cast_holy_spell", "ran_from_2nd_nezi", "legends_choose_reward",
				"legends_reward_claimed", "ancient_wall_runes", "gave_glowing_dagger",
				"met_spirit", "cavernous_opening", "viyeldi_companions", "killed_viyeldi",
				"legends_wooden_beam", "rewarded_totem", "holy_water_neiz", "crafted_totem_pole",
			};
		for (String s : caches) {
			if (player.getCache().hasKey(s)) {
				player.getCache().remove(s);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id() || n.getID() == NpcId.SIR_RADIMUS_ERKLE_GUILD.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id()) {
			radimusInHouseDialogue(player, n, -1);
		}
		else if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_GUILD.id()) {
			radimusInGuildDialogue(player, n, -1);
		}
	}

	private void radimusInGuildDialogue(Player player, Npc n, int cID) {
		if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_GUILD.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(Quests.LEGENDS_QUEST)) {
					case 11:
						if (!player.getCache().hasKey("legends_choose_reward")) {
							npcsay(player, n, "Welcome to the Legends Guild Main Hall.",
								"We have placed your Totem Pole as pride of place.",
								"All members of the Legends Guild will see it as they walk in.",
								"They will know that you were the person to bring it back.",
								"Congratulations, you're now a fully fledged member.",
								"I would like to to offer you some training.",
								"Which will increase your experience and abilities ",
								"In four areas.",
								"Would you like to train now?");
							player.getCache().store("legends_choose_reward", true);
						} else {
							npcsay(player, n, "Hello again...",
								"Would you like to continue with your training?");
						}
						int menu = multi(player, n, false, //do not send over
							"Yes, I'll train now.",
							"No, I've got something else to do at the moment.");
						if (menu == 0) {
							npcsay(player, n, "You can choose " + getRewardClaimCount(player) + " area" +
								(getRewardClaimCount(player) > 1 ? "s" : "") + " to increase your abilities in.");
							radimusInGuildDialogue(player, n, RadimusInGuild.SKILL_MENU_ONE);
						} else if (menu == 1) {
							say(player, n, "No, I've got something else to do at the moment.");
							npcsay(player, n, "Very well young " + (player.isMale() ? "man" : "lady") + ".",
								"Return when you are able, but don't leave it too long.",
								"You'll benefit alot from this training.",
								"Now, do excuse me while, I have other things to attend to.",
								"Do feel free to explore the rest of the building.");
						}
						break;
					case -1:
						npcsay(player, n, "Hello there! How are you enjoying the Legends Guild?");
						mes(n, "Radimus looks busy...");
						delay(2);
						npcsay(player, n, "Excuse me a moment won't you.",
							"Do feel free to explore the rest of the building.");
						break;
				}
			}
			switch (cID) {
				case RadimusInGuild.SKILL_MENU_ONE:
					int menu_one = multi(player,
						"* Attack *",
						"* Defense * ",
						"* Strength * ",
						"--- Go to Skill Menu 2 ----");
					if (menu_one == 0) {
						skillReward(player, n, Skill.ATTACK);
					} else if (menu_one == 1) {
						skillReward(player, n, Skill.DEFENSE);
					} else if (menu_one == 2) {
						skillReward(player, n, Skill.STRENGTH);
					} else if (menu_one == 3) {
						radimusInGuildDialogue(player, n, RadimusInGuild.SKILL_MENU_TWO);
					}
					break;
				case RadimusInGuild.SKILL_MENU_TWO:
					int menu_two = multi(player,
						"* Hits * ",
						"* Prayer * ",
						"* Magic *",
						"--- Go to Skill Menu 3  ----");
					if (menu_two == 0) {
						skillReward(player, n, Skill.HITS);
					} else if (menu_two == 1) {
						skillReward(player, n, Skill.PRAYER);
					} else if (menu_two == 2) {
						skillReward(player, n, Skill.MAGIC);
					} else if (menu_two == 3) {
						radimusInGuildDialogue(player, n, RadimusInGuild.SKILL_MENU_THREE);
					}
					break;
				case RadimusInGuild.SKILL_MENU_THREE:
					int menu_three = multi(player,
						"* Woodcutting * ",
						"* Crafting * ",
						"* Smithing * ",
						"--- Go to Skill Menu 4 ----");
					if (menu_three == 0) {
						skillReward(player, n, Skill.WOODCUTTING);
					} else if (menu_three == 1) {
						skillReward(player, n, Skill.CRAFTING);
					} else if (menu_three == 2) {
						skillReward(player, n, Skill.SMITHING);
					} else if (menu_three == 3) {
						radimusInGuildDialogue(player, n, RadimusInGuild.SKILL_MENU_FOUR);
					}
					break;
				case RadimusInGuild.SKILL_MENU_FOUR:
					int menu_four = multi(player,
						"* Herblaw *",
						"* Agility *",
						"* Thieving *",
						"--- Go to Skill Menu 1 ----");
					if (menu_four == 0) {
						skillReward(player, n, Skill.HERBLAW);
					} else if (menu_four == 1) {
						skillReward(player, n, Skill.AGILITY);
					} else if (menu_four == 2) {
						skillReward(player, n, Skill.THIEVING);
					} else if (menu_four == 3) {
						radimusInGuildDialogue(player, n, RadimusInGuild.SKILL_MENU_ONE);
					}
					break;
			}
		}
	}

	private int getRewardClaimCount(Player player) {
		int rewardCount = 4;
		if (player.getCache().hasKey("legends_reward_claimed")) {
			rewardCount = player.getCache().getInt("legends_reward_claimed");
		}
		return rewardCount;
	}

	private void updateRewardClaimCount(Player player) {
		if (!player.getCache().hasKey("legends_reward_claimed")) {
			player.getCache().set("legends_reward_claimed", 3);
		} else {
			int leftToClaim = player.getCache().getInt("legends_reward_claimed");
			player.getCache().set("legends_reward_claimed", leftToClaim - 1);
		}
	}

	private void skillReward(Player player, Npc n, Skill skill) {
		final XPReward origXpReward = Quest.LEGENDS_QUEST.reward().getXpRewards()[0];
		XPReward xpReward = origXpReward.copyTo(skill);
		incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		updateRewardClaimCount(player);
		player.message("You receive some training and increase experience to your " + StringUtil.convertToTitleCase(skill.name()) + ".");
		if (getRewardClaimCount(player) == 0) {
			npcsay(player, n, "Right, that's all the training I can offer.! ",
				"Hope you're happy with your new skills.",
				"Excuse me now won't you ?",
				"Do feel free to explore the rest of the building.");
			player.sendQuestComplete(Quests.LEGENDS_QUEST);
		} else {
			npcsay(player, n, "You can choose " + getRewardClaimCount(player) + " area" +
				(getRewardClaimCount(player) > 1 ? "s" : "") + " to increase your abilities in.");
			radimusInGuildDialogue(player, n, RadimusInGuild.SKILL_MENU_ONE);
		}
	}

	private void radimusInHouseDialogue(Player player, Npc n, int cID) {
		if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(Quests.LEGENDS_QUEST)) {
					case 0:
						npcsay(player, n, "Good day to you " + (player.isMale() ? "Sir" : "my Lady") + " !",
							"No doubt you are keen to become a member of the Legends Guild ?");
						int menu = multi(player, n, false, //do not send over
							"Yes actually, what's involved?",
							"Maybe some other time.",
							"Who are you?");
						if (menu == 0) {
							/* START LEGENDS QUEST */
							say(player, n, "Yes actually, what's involved ?");
							radimusInHouseDialogue(player, n, RadimusInHouse.WHATS_INVOLVED);
						} else if (menu == 1) {
							say(player, n, "Maybe some other time.");
							radimusInHouseDialogue(player, n, RadimusInHouse.MAYBE_SOME_OTHER_TIME);
						} else if (menu == 2) {
							say(player, n, "Who are you?");
							radimusInHouseDialogue(player, n, RadimusInHouse.WHO_ARE_YOU);
						}
						break;
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
					case 9:
					case 10:
						npcsay(player, n, "Hello there, how is the quest going?");
						if (player.getCarriedItems().hasCatalogID(ItemId.RADIMUS_SCROLLS.id(), Optional.of(false))
							|| player.getCarriedItems().hasCatalogID(ItemId.RADIMUS_SCROLLS_COMPLETE.id(), Optional.of(false))) {
							radimusInHouseDialogue(player, n, RadimusInHouse.SAME_MENU_HAS_SCROLLS);
						} else {
							radimusInHouseDialogue(player, n, RadimusInHouse.SAME_MENU_NO_SCROLLS);
						}
						break;
					case 11:
						npcsay(player, n, "Hello again, go through to the main Legends Guild Hall,",
							"I'll meet you in there. ",
							"And we can discuss your reward !");
						break;
					case -1:
						npcsay(player, n, "Hello there! How are you enjoying the Legends Guild?");
						mes(n, "Radimus looks busy...");
						delay(2);
						npcsay(player, n, "Excuse me a moment won't you.",
							"Do feel free to explore the rest of the building.");
						break;
				}
			}
			switch (cID) {
				case RadimusInHouse.WHATS_INVOLVED:
					npcsay(player, n, "Well, you need to complete a quest for us.",
						"You need to map an area called the Kharazi Jungle",
						"It is the unexplored southern part of Karamja Island.",
						"You also need to befriend a native from the Kharazi tribe",
						"in order to get a gift or token of friendship.",
						"We want to display it in the Legends Guild Main hall.",
						"Are you interested in this quest?");
					int questMenu = multi(player, n,
						"Yes, it sounds great!",
						"Not just at the moment.");
					if (questMenu == 0) {
						/* START LEGENDS QUEST */
						npcsay(player, n, "Excellent!",
							"Ok, you'll need this starting map of the Kharazi Jungle.");
						player.message("Grand Vizier Erkle gives you some notes and a map.");
						give(player, ItemId.RADIMUS_SCROLLS.id(), 1);
						npcsay(player, n, "Complete this map when you get to the Kharazi Jungle.",
							"It's towards the southern most part of Karamja.",
							"You'll need additional papyrus and charcoal to complete the map.",
							"There are three different sectors of the Kharazi jungle to map.");
						mes("Radimus shuffles around the back of his desk.");
						delay(2);
						npcsay(player, n, "It is likely to be very tough going.",
							"You'll need an axe and a machette to cut through ",
							"the dense Kharazi jungle,collect a machette from the ",
							"cupboard before you leave. Bring back some sort of token ",
							"which we can display in the Guild.",
							"And very good luck to you !");
						player.updateQuestStage(Quests.LEGENDS_QUEST, 1);
					} else if (questMenu == 1) {
						npcsay(player, n, "Very well, if you change your mind, please come back and see me.");
					}
					break;
				case RadimusInHouse.MAYBE_SOME_OTHER_TIME:
					npcsay(player, n, "Ok, as you wish...");
					break;
				case RadimusInHouse.WHO_ARE_YOU:
					npcsay(player, n, "My name is Radimus Erkle, I am the Grand Vizier of the Legends Guild.",
						"Are you interested in becoming a member?");
					int opt = multi(player, n, false, //do not send over
						"Yes actually, what's involved?",
						"Maybe some other time.");
					if (opt == 0) {
						say(player, n, "Yes actually, what's involved ?");
						radimusInHouseDialogue(player, n, RadimusInHouse.WHATS_INVOLVED);
					} else if (opt == 1) {
						say(player, n, "Maybe some other time.");
						radimusInHouseDialogue(player, n, RadimusInHouse.MAYBE_SOME_OTHER_TIME);
					}
					break;
				case RadimusInHouse.SAME_MENU_HAS_SCROLLS:
					int option = multi(player, n,
						"It's Ok, but I have forgotten what to do.",
						"I need another machete.",
						"I've run out of Charcoal.",
						"I've run out of Papyrus.",
						"I've completed the quest.");
					if (option == 0) {
						radimusInHouseDialogue(player, n, RadimusInHouse.FORGOTTEN_WHAT_TO_DO);
					} else if (option == 1) {
						radimusInHouseDialogue(player, n, RadimusInHouse.ANOTHER_MACHETE);
					} else if (option == 2) {
						radimusInHouseDialogue(player, n, RadimusInHouse.CHARCOAL);
					} else if (option == 3) {
						radimusInHouseDialogue(player, n, RadimusInHouse.PAPYRUS);
					} else if (option == 4) {
						radimusInHouseDialogue(player, n, RadimusInHouse.IVE_COMPLETED_QUEST);
					}
					break;
				case RadimusInHouse.SAME_MENU_NO_SCROLLS:
					int myMenu = multi(player, n, false, //do not send over
						"Terrible, I lost my map of the Kharazi Jungle.",
						"It's Ok, but I have forgotten what to do.",
						"Great, but I need another machete.",
						"I've run out of Charcoal.",
						"I've run out of Papyrus.");
					if (myMenu == 0) {
						say(player, n, "Terrible, I lost my map of the Kharazi Jungle.");
						radimusInHouseDialogue(player, n, RadimusInHouse.LOST_KHARAZI_JUNGLE_MAP);
					} else if (myMenu == 1) {
						say(player, n, "It's Ok, but I have forgotten what to do.");
						radimusInHouseDialogue(player, n, RadimusInHouse.FORGOTTEN_WHAT_TO_DO);
					} else if (myMenu == 2) {
						say(player, n, "I need another machete.");
						radimusInHouseDialogue(player, n, RadimusInHouse.ANOTHER_MACHETE);
					} else if (myMenu == 3) {
						say(player, n, "I've run out of Charcoal.");
						radimusInHouseDialogue(player, n, RadimusInHouse.CHARCOAL);
					} else if (myMenu == 4) {
						say(player, n, "I've run out of Papyrus.");
						radimusInHouseDialogue(player, n, RadimusInHouse.PAPYRUS);
					}
					break;
				case RadimusInHouse.FORGOTTEN_WHAT_TO_DO:
					npcsay(player, n, "Tut! How forgetful!",
							"You need to find a way into the Kharazi jungle, ",
							"Then you need to explore and map that entire area.",
							"While you're there, you need to make contact with any jungle natives.",
							"Bring back a tribal gift from the natives",
							"so that we can display it in the Legends Guild.",
							"I hope that answers your question!");
					if (player.getCarriedItems().hasCatalogID(ItemId.RADIMUS_SCROLLS.id(), Optional.of(false))
						|| player.getCarriedItems().hasCatalogID(ItemId.RADIMUS_SCROLLS_COMPLETE.id(), Optional.of(false))) {
						radimusInHouseDialogue(player, n, RadimusInHouse.SAME_MENU_HAS_SCROLLS);
					} else {
						radimusInHouseDialogue(player, n, RadimusInHouse.SAME_MENU_NO_SCROLLS);
					}
					break;
				case RadimusInHouse.ANOTHER_MACHETE:
					npcsay(player, n, "Well, just get another one from the cupboard.");
					if (player.getCarriedItems().hasCatalogID(ItemId.RADIMUS_SCROLLS.id(), Optional.of(false))
						|| player.getCarriedItems().hasCatalogID(ItemId.RADIMUS_SCROLLS_COMPLETE.id(), Optional.of(false))) {
						radimusInHouseDialogue(player, n, RadimusInHouse.SAME_MENU_HAS_SCROLLS);
					} else {
						radimusInHouseDialogue(player, n, RadimusInHouse.SAME_MENU_NO_SCROLLS);
					}
					break;
				case RadimusInHouse.CHARCOAL:
					npcsay(player, n, "Well, get some more!",
						"Be proactive and get some more from somewhere.");
					mes("Sir Radimus mutters under his breath.");
					delay(2);
					npcsay(player, n, "It's hardly legendary if you fail a quest",
						"because you can't find some charcoal!");
					if (player.getCarriedItems().hasCatalogID(ItemId.RADIMUS_SCROLLS.id(), Optional.of(false))
						|| player.getCarriedItems().hasCatalogID(ItemId.RADIMUS_SCROLLS_COMPLETE.id(), Optional.of(false))) {
						radimusInHouseDialogue(player, n, RadimusInHouse.SAME_MENU_HAS_SCROLLS);
					} else {
						radimusInHouseDialogue(player, n, RadimusInHouse.SAME_MENU_NO_SCROLLS);
					}
					break;
				case RadimusInHouse.PAPYRUS:
					npcsay(player, n, "Well, get some more!",
						"Be proactive and try to find some!");
					mes("Sir Radimus mutters under his breath.");
					delay(2);
					npcsay(player, n, "It's hardly legendary if you fail a quest",
						"because you can't find some papyrus!");
					if (player.getCarriedItems().hasCatalogID(ItemId.RADIMUS_SCROLLS.id(), Optional.of(false))
						|| player.getCarriedItems().hasCatalogID(ItemId.RADIMUS_SCROLLS_COMPLETE.id(), Optional.of(false))) {
						radimusInHouseDialogue(player, n, RadimusInHouse.SAME_MENU_HAS_SCROLLS);
					} else {
						radimusInHouseDialogue(player, n, RadimusInHouse.SAME_MENU_NO_SCROLLS);
					}
					break;
				case RadimusInHouse.IVE_COMPLETED_QUEST:
					npcsay(player, n, "Well, if you have, show me the gift the Kharazi people gave you !",
						"Becoming a legend is more than just fighting you know.",
						"It also requires some carefull diplomacy and problem solving.",
						"Also complete the map of Kharazi jungle",
						"and we will admit you to the Guild.");
					radimusInHouseDialogue(player, n, RadimusInHouse.SAME_MENU_HAS_SCROLLS);
					break;
				case RadimusInHouse.LOST_KHARAZI_JUNGLE_MAP:
					npcsay(player, n, "That is awful, well, luckily I have a copy here.",
						"But I need to charge you a copy fee of 30 gold pieces.");
					if (ifheld(player, ItemId.COINS.id(), 30)) {
						npcsay(player, n, "Do you agree to pay?");
						int pay = multi(player, n,
							"Yes, I'll pay for it.",
							"No, I won't pay for it.");
						if (pay == 0) {
							player.message("You hand over 30 gold coins.");
							player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 30));
							give(player, ItemId.RADIMUS_SCROLLS.id(), 1);
							npcsay(player, n, "Ok, please don't lose this one..");
						} else if (pay == 1) {
							npcsay(player, n, "Well, that's your decision, of course... ",
								"but you won't be able to complete the quest without it.",
								"Excuse, me now won't you, I have other business to attend to.");
							radimusInHouseDialogue(player, n, RadimusInHouse.SAME_MENU_NO_SCROLLS);
						}
					} else {
						npcsay(player, n, "It looks as if you don't have the funds for it at the moment.",
							"How irritating...");
					}
					break;
			}
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc n, Item item) {
		return n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id() && (item.getCatalogId() == ItemId.RADIMUS_SCROLLS_COMPLETE.id()
				|| item.getCatalogId() == ItemId.GILDED_TOTEM_POLE.id() || item.getCatalogId() == ItemId.TOTEM_POLE.id());
	}

	@Override
	public void onUseNpc(Player player, Npc n, Item item) {
		if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id() && item.getCatalogId() == ItemId.GILDED_TOTEM_POLE.id()) {
			if (player.getQuestStage(Quests.LEGENDS_QUEST) == 11) {
				npcsay(player, n, "Go through to the main Legends Guild and I will join you.");
				return;
			}
			if (player.getQuestStage(Quests.LEGENDS_QUEST) == 10) {
				npcsay(player, n, (player.isMale() ? "Sir" : "Madam") + ", this is truly amazing...");
				if (!player.getCarriedItems().hasCatalogID(ItemId.RADIMUS_SCROLLS_COMPLETE.id(), Optional.of(false))) {
					npcsay(player, n, "However, I need you to complete the map of the ,",
						"Kharazi Jungle before your quest is complete.");
				} else {
					mes(n, "Radimus Erkle orders some guards to take the totem pole,");
					delay(2);
					mes(n, "into the main Legends Hall.");
					delay(2);
					player.getCarriedItems().remove(new Item(item.getCatalogId()));
					npcsay(player, n, "That will take pride of place in the Legends Guild ",
						"As a reminder of your quest to gain entry.",
						"And so that many other great adventurers can admire your bravery.",
						"Well, it seems that you have completed the tasks I set you.",
						"That map of the Kharazi jungle will be very helpful in future.",
						"Congratulations, welcome to the Legends Guild.",
						"Go through to the main Legends Guild building ",
						"and I will join you shortly.");
					player.updateQuestStage(Quests.LEGENDS_QUEST, 11);
				}
			} else {
				player.message("You have not completed this quest - submitting bug abuse.");
			}
		}
		else if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id() && item.getCatalogId() == ItemId.TOTEM_POLE.id()) {
			npcsay(player, n, "Hmmm, well, it is very impressive.",
					"Especially since it looks very heavy...",
					"However, it lacks a certain authenticity,",
					"my guess is that you made it.",
					"But I'm not sure why.",
					"We would like to have a really nice display object",
					"to put on display in the Legends Guild main hall.",
					"Do you think you could get something more authentic ?");
		}
		else if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id() && item.getCatalogId() == ItemId.RADIMUS_SCROLLS_COMPLETE.id()) {
			npcsay(player, n, "Well done " + (player.isMale() ? "Sir" : "Madam") + ", very well done...",
				"However, you'll probably need it while you search",
				"for natives of the Kharazi tribe in the Kharazi jungle.",
				"Remember, we want a very special token of friendship from them.",
				"To place in the Legends Guild.",
				"I'll take the map off your hands once we get the ",
				"proof that you have met the natives.");
		}
	}

	class RadimusInHouse {
		static final int WHATS_INVOLVED = 0;
		static final int MAYBE_SOME_OTHER_TIME = 1;
		static final int WHO_ARE_YOU = 2;

		static final int SAME_MENU_HAS_SCROLLS = 3;

		static final int FORGOTTEN_WHAT_TO_DO = 4;
		static final int ANOTHER_MACHETE = 5;
		static final int CHARCOAL = 6;
		static final int PAPYRUS = 7;
		static final int IVE_COMPLETED_QUEST = 8;

		static final int SAME_MENU_NO_SCROLLS = 9;
		static final int LOST_KHARAZI_JUNGLE_MAP = 10;
	}

	class RadimusInGuild {
		static final int SKILL_MENU_ONE = 0;
		static final int SKILL_MENU_TWO = 1;
		static final int SKILL_MENU_THREE = 2;
		static final int SKILL_MENU_FOUR = 3;
	}
}
