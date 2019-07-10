package com.openrsc.server.plugins.quests.members.legendsquest.npcs;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.incQuestReward;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;

public class LegendsQuestSirRadimusErkle implements QuestInterface, TalkToNpcListener, TalkToNpcExecutiveListener, InvUseOnNpcListener, InvUseOnNpcExecutiveListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.LEGENDS_QUEST;
	}

	@Override
	public String getQuestName() {
		return "Legend's Quest (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.message("@gre@Well done - you have completed the Legends Guild Quest!");
		incQuestReward(p, Quests.questData.get(Quests.LEGENDS_QUEST), true);
		message(p, "@gre@You haved gained 4 quest points!");
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
			if (p.getCache().hasKey(s)) {
				p.getCache().remove(s);
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id() || n.getID() == NpcId.SIR_RADIMUS_ERKLE_GUILD.id();
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id()) {
			radimusInHouseDialogue(p, n, -1);
		}
		else if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_GUILD.id()) {
			radimusInGuildDialogue(p, n, -1);
		}
	}

	private void radimusInGuildDialogue(Player p, Npc n, int cID) {
		if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_GUILD.id()) {
			if (cID == -1) {
				switch (p.getQuestStage(Constants.Quests.LEGENDS_QUEST)) {
					case 11:
						if (!p.getCache().hasKey("legends_choose_reward")) {
							npcTalk(p, n, "Welcome to the Legends Guild Main Hall.",
								"We have placed your Totem Pole as pride of place.",
								"All members of the Legends Guild will see it as they walk in.",
								"They will know that you were the person to bring it back.",
								"Congratulations, you're now a fully fledged member.",
								"I would like to to offer you some training.",
								"Which will increase your experience and abilities ",
								"In four areas.",
								"Would you like to train now?");
							p.getCache().store("legends_choose_reward", true);
						} else {
							npcTalk(p, n, "Hello again...",
								"Would you like to continue with your training?");
						}
						int menu = showMenu(p, n, false, //do not send over
							"Yes, I'll train now.",
							"No, I've got something else to do at the moment.");
						if (menu == 0) {
							npcTalk(p, n, "You can choose " + getRewardClaimCount(p) + " areas to increase your abilities in.");
							radimusInGuildDialogue(p, n, RadimusInGuild.SKILL_MENU_ONE);
						} else if (menu == 1) {
							playerTalk(p, n, "No, I've got something else to do at the moment.");
							npcTalk(p, n, "Very well young " + (p.isMale() ? "man" : "lady") + ".",
								"Return when you are able, but don't leave it too long.",
								"You'll benefit alot from this training.",
								"Now, do excuse me while, I have other things to attend to.",
								"Do feel free to explore the rest of the building.");
						}
						break;
					case -1:
						npcTalk(p, n, "Hello there! How are you enjoying the Legends Guild?");
						message(p, n, 1300, "Radimus looks busy...");
						npcTalk(p, n, "Excuse me a moment won't you.",
							"Do feel free to explore the rest of the building.");
						break;
				}
			}
			switch (cID) {
				case RadimusInGuild.SKILL_MENU_ONE:
					int menu_one = showMenu(p,
						"* Attack *",
						"* Defense * ",
						"* Strength * ",
						"--- Go to Skill Menu 2 ----");
					if (menu_one == 0) {
						skillReward(p, n, SKILLS.ATTACK.id());
					} else if (menu_one == 1) {
						skillReward(p, n, SKILLS.DEFENSE.id());
					} else if (menu_one == 2) {
						skillReward(p, n, SKILLS.STRENGTH.id());
					} else if (menu_one == 3) {
						radimusInGuildDialogue(p, n, RadimusInGuild.SKILL_MENU_TWO);
					}
					break;
				case RadimusInGuild.SKILL_MENU_TWO:
					int menu_two = showMenu(p,
						"* Hits * ",
						"* Prayer * ",
						"* Magic *",
						"--- Go to Skill Menu 3  ----");
					if (menu_two == 0) {
						skillReward(p, n, SKILLS.HITS.id());
					} else if (menu_two == 1) {
						skillReward(p, n, SKILLS.PRAYER.id());
					} else if (menu_two == 2) {
						skillReward(p, n, SKILLS.MAGIC.id());
					} else if (menu_two == 3) {
						radimusInGuildDialogue(p, n, RadimusInGuild.SKILL_MENU_THREE);
					}
					break;
				case RadimusInGuild.SKILL_MENU_THREE:
					int menu_three = showMenu(p,
						"* Woodcutting * ",
						"* Crafting * ",
						"* Smithing * ",
						"--- Go to Skill Menu 4 ----");
					if (menu_three == 0) {
						skillReward(p, n, SKILLS.WOODCUT.id());
					} else if (menu_three == 1) {
						skillReward(p, n, SKILLS.CRAFTING.id());
					} else if (menu_three == 2) {
						skillReward(p, n, SKILLS.SMITHING.id());
					} else if (menu_three == 3) {
						radimusInGuildDialogue(p, n, RadimusInGuild.SKILL_MENU_FOUR);
					}
					break;
				case RadimusInGuild.SKILL_MENU_FOUR:
					int menu_four = showMenu(p,
						"* Herblaw *",
						"* Agility *",
						"* Thieving *",
						"--- Go to Skill Menu 1 ----");
					if (menu_four == 0) {
						skillReward(p, n, SKILLS.HERBLAW.id());
					} else if (menu_four == 1) {
						skillReward(p, n, SKILLS.AGILITY.id());
					} else if (menu_four == 2) {
						skillReward(p, n, SKILLS.THIEVING.id());
					} else if (menu_four == 3) {
						radimusInGuildDialogue(p, n, RadimusInGuild.SKILL_MENU_ONE);
					}
					break;
			}
		}
	}

	private int getRewardClaimCount(Player p) {
		int rewardCount = 4;
		if (p.getCache().hasKey("legends_reward_claimed")) {
			rewardCount = p.getCache().getInt("legends_reward_claimed");
		}
		return rewardCount;
	}

	private void updateRewardClaimCount(Player p) {
		if (!p.getCache().hasKey("legends_reward_claimed")) {
			p.getCache().set("legends_reward_claimed", 3);
		} else {
			int leftToClaim = p.getCache().getInt("legends_reward_claimed");
			p.getCache().set("legends_reward_claimed", leftToClaim - 1);
		}
	}

	private void skillReward(Player p, Npc n, int skill) {
		int[] questData = Quests.questData.get(Quests.LEGENDS_QUEST);
		questData[Quests.MAPIDX_SKILL] = skill;
		incQuestReward(p, questData, false);
		updateRewardClaimCount(p);
		p.message("You receive some training and increase experience to your " + Skills.getSkillName(skill) + ".");
		if (getRewardClaimCount(p) == 0) {
			npcTalk(p, n, "Right, that's all the training I can offer.! ",
				"Hope you're happy with your new skills.",
				"Excuse me now won't you ?",
				"Do feel free to explore the rest of the building.");
			p.sendQuestComplete(Constants.Quests.LEGENDS_QUEST);
		} else {
			npcTalk(p, n, "You can choose " + getRewardClaimCount(p) + " areas to increase your abilities in.");
			radimusInGuildDialogue(p, n, RadimusInGuild.SKILL_MENU_ONE);
		}
	}

	private void radimusInHouseDialogue(Player p, Npc n, int cID) {
		if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id()) {
			if (cID == -1) {
				switch (p.getQuestStage(Constants.Quests.LEGENDS_QUEST)) {
					case 0:
						npcTalk(p, n, "Good day to you " + (p.isMale() ? "Sir" : "my Lady") + " !",
							"No doubt you are keen to become a member of the Legends Guild ?");
						int menu = showMenu(p, n, false, //do not send over
							"Yes actually, what's involved?",
							"Maybe some other time.",
							"Who are you?");
						if (menu == 0) {
							/* START LEGENDS QUEST */
							playerTalk(p, n, "Yes actually, what's involved ?");
							radimusInHouseDialogue(p, n, RadimusInHouse.WHATS_INVOLVED);
						} else if (menu == 1) {
							playerTalk(p, n, "Maybe some other time.");
							radimusInHouseDialogue(p, n, RadimusInHouse.MAYBE_SOME_OTHER_TIME);
						} else if (menu == 2) {
							playerTalk(p, n, "Who are you?");
							radimusInHouseDialogue(p, n, RadimusInHouse.WHO_ARE_YOU);
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
						npcTalk(p, n, "Hello there, how is the quest going?");
						if (hasItem(p, ItemId.RADIMUS_SCROLLS.id()) || hasItem(p, ItemId.RADIMUS_SCROLLS_COMPLETE.id())) {
							radimusInHouseDialogue(p, n, RadimusInHouse.SAME_MENU_HAS_SCROLLS);
						} else {
							radimusInHouseDialogue(p, n, RadimusInHouse.SAME_MENU_NO_SCROLLS);
						}
						break;
					case 11:
						npcTalk(p, n, "Hello again, go through to the main Legends Guild Hall,",
							"I'll meet you in there. ",
							"And we can discuss your reward !");
						break;
					case -1:
						npcTalk(p, n, "Hello there! How are you enjoying the Legends Guild?");
						message(p, n, 1300, "Radimus looks busy...");
						npcTalk(p, n, "Excuse me a moment won't you.",
							"Do feel free to explore the rest of the building.");
						break;
				}
			}
			switch (cID) {
				case RadimusInHouse.WHATS_INVOLVED:
					npcTalk(p, n, "Well, you need to complete a quest for us.",
						"You need to map an area called the Kharazi Jungle",
						"It is the unexplored southern part of Karamja Island.",
						"You also need to befriend a native from the Kharazi tribe",
						"in order to get a gift or token of friendship.",
						"We want to display it in the Legends Guild Main hall.",
						"Are you interested in this quest?");
					int questMenu = showMenu(p, n,
						"Yes, it sounds great!",
						"Not just at the moment.");
					if (questMenu == 0) {
						/* START LEGENDS QUEST */
						npcTalk(p, n, "Excellent!",
							"Ok, you'll need this starting map of the Kharazi Jungle.");
						p.message("Grand Vizier Erkle gives you some notes and a map.");
						addItem(p, ItemId.RADIMUS_SCROLLS.id(), 1);
						npcTalk(p, n, "Complete this map when you get to the Kharazi Jungle.",
							"It's towards the southern most part of Karamja.",
							"You'll need additional papyrus and charcoal to complete the map.",
							"There are three different sectors of the Kharazi jungle to map.");
						message(p, 1200, "Radimus shuffles around the back of his desk.");
						npcTalk(p, n, "It is likely to be very tough going.",
							"You'll need an axe and a machette to cut through ",
							"the dense Kharazi jungle,collect a machette from the ",
							"cupboard before you leave. Bring back some sort of token ",
							"which we can display in the Guild.",
							"And very good luck to you !");
						p.updateQuestStage(Constants.Quests.LEGENDS_QUEST, 1);
					} else if (questMenu == 1) {
						npcTalk(p, n, "Very well, if you change your mind, please come back and see me.");
					}
					break;
				case RadimusInHouse.MAYBE_SOME_OTHER_TIME:
					npcTalk(p, n, "Ok, as you wish...");
					break;
				case RadimusInHouse.WHO_ARE_YOU:
					npcTalk(p, n, "My name is Radimus Erkle, I am the Grand Vizier of the Legends Guild.",
						"Are you interested in becoming a member?");
					int opt = showMenu(p, n, false, //do not send over
						"Yes actually, what's involved?",
						"Maybe some other time.");
					if (opt == 0) {
						playerTalk(p, n, "Yes actually, what's involved ?");
						radimusInHouseDialogue(p, n, RadimusInHouse.WHATS_INVOLVED);
					} else if (opt == 1) {
						playerTalk(p, n, "Maybe some other time.");
						radimusInHouseDialogue(p, n, RadimusInHouse.MAYBE_SOME_OTHER_TIME);
					}
					break;
				case RadimusInHouse.SAME_MENU_HAS_SCROLLS:
					int option = showMenu(p, n,
						"It's Ok, but I have forgotten what to do.",
						"I need another machete.",
						"I've run out of Charcoal.",
						"I've run out of Papyrus.",
						"I've completed the quest.");
					if (option == 0) {
						radimusInHouseDialogue(p, n, RadimusInHouse.FORGOTTEN_WHAT_TO_DO);
					} else if (option == 1) {
						radimusInHouseDialogue(p, n, RadimusInHouse.ANOTHER_MACHETE);
					} else if (option == 2) {
						radimusInHouseDialogue(p, n, RadimusInHouse.CHARCOAL);
					} else if (option == 3) {
						radimusInHouseDialogue(p, n, RadimusInHouse.PAPYRUS);
					} else if (option == 4) {
						radimusInHouseDialogue(p, n, RadimusInHouse.IVE_COMPLETED_QUEST);
					}
					break;
				case RadimusInHouse.SAME_MENU_NO_SCROLLS:
					int myMenu = showMenu(p, n, false, //do not send over
						"Terrible, I lost my map of the Kharazi Jungle.",
						"It's Ok, but I have forgotten what to do.",
						"Great, but I need another machete.",
						"I've run out of Charcoal.",
						"I've run out of Papyrus.");
					if (myMenu == 0) {
						playerTalk(p, n, "Terrible, I lost my map of the Kharazi Jungle.");
						radimusInHouseDialogue(p, n, RadimusInHouse.LOST_KHARAZI_JUNGLE_MAP);
					} else if (myMenu == 1) {
						playerTalk(p, n, "It's Ok, but I have forgotten what to do.");
						radimusInHouseDialogue(p, n, RadimusInHouse.FORGOTTEN_WHAT_TO_DO);
					} else if (myMenu == 2) {
						playerTalk(p, n, "I need another machete.");
						radimusInHouseDialogue(p, n, RadimusInHouse.ANOTHER_MACHETE);
					} else if (myMenu == 3) {
						playerTalk(p, n, "I've run out of Charcoal.");
						radimusInHouseDialogue(p, n, RadimusInHouse.CHARCOAL);
					} else if (myMenu == 4) {
						playerTalk(p, n, "I've run out of Papyrus.");
						radimusInHouseDialogue(p, n, RadimusInHouse.PAPYRUS);
					}
					break;
				case RadimusInHouse.FORGOTTEN_WHAT_TO_DO:
					npcTalk(p, n, "Tut! How forgetful!",
							"You need to find a way into the Kharazi jungle, ",
							"Then you need to explore and map that entire area.",
							"While you're there, you need to make contact with any jungle natives.",
							"Bring back a tribal gift from the natives",
							"so that we can display it in the Legends Guild.",
							"I hope that answers your question!");
					if (hasItem(p, ItemId.RADIMUS_SCROLLS.id()) || hasItem(p, ItemId.RADIMUS_SCROLLS_COMPLETE.id())) {
						radimusInHouseDialogue(p, n, RadimusInHouse.SAME_MENU_HAS_SCROLLS);
					} else {
						radimusInHouseDialogue(p, n, RadimusInHouse.SAME_MENU_NO_SCROLLS);
					}
					break;
				case RadimusInHouse.ANOTHER_MACHETE:
					npcTalk(p, n, "Well, just get another one from the cupboard.");
					if (hasItem(p, ItemId.RADIMUS_SCROLLS.id()) || hasItem(p, ItemId.RADIMUS_SCROLLS_COMPLETE.id())) {
						radimusInHouseDialogue(p, n, RadimusInHouse.SAME_MENU_HAS_SCROLLS);
					} else {
						radimusInHouseDialogue(p, n, RadimusInHouse.SAME_MENU_NO_SCROLLS);
					}
					break;
				case RadimusInHouse.CHARCOAL:
					npcTalk(p, n, "Well, get some more!",
						"Be proactive and get some more from somewhere.");
					message(p, 1200, "Sir Radimus mutters under his breath.");
					npcTalk(p, n, "It's hardly legendary if you fail a quest",
						"because you can't find some charcoal!");
					if (hasItem(p, ItemId.RADIMUS_SCROLLS.id()) || hasItem(p, ItemId.RADIMUS_SCROLLS_COMPLETE.id())) {
						radimusInHouseDialogue(p, n, RadimusInHouse.SAME_MENU_HAS_SCROLLS);
					} else {
						radimusInHouseDialogue(p, n, RadimusInHouse.SAME_MENU_NO_SCROLLS);
					}
					break;
				case RadimusInHouse.PAPYRUS:
					npcTalk(p, n, "Well, get some more!",
						"Be proactive and try to find some!");
					message(p, 1200, "Sir Radimus mutters under his breath.");
					npcTalk(p, n, "It's hardly legendary if you fail a quest",
						"because you can't find some papyrus!");
					if (hasItem(p, ItemId.RADIMUS_SCROLLS.id()) || hasItem(p, ItemId.RADIMUS_SCROLLS_COMPLETE.id())) {
						radimusInHouseDialogue(p, n, RadimusInHouse.SAME_MENU_HAS_SCROLLS);
					} else {
						radimusInHouseDialogue(p, n, RadimusInHouse.SAME_MENU_NO_SCROLLS);
					}
					break;
				case RadimusInHouse.IVE_COMPLETED_QUEST:
					npcTalk(p, n, "Well, if you have, show me the gift the Kharazi people gave you !",
						"Becoming a legend is more than just fighting you know.",
						"It also requires some carefull diplomacy and problem solving.",
						"Also complete the map of Kharazi jungle",
						"and we will admit you to the Guild.");
					radimusInHouseDialogue(p, n, RadimusInHouse.SAME_MENU_HAS_SCROLLS);
					break;
				case RadimusInHouse.LOST_KHARAZI_JUNGLE_MAP:
					npcTalk(p, n, "That is awful, well, luckily I have a copy here.",
						"But I need to charge you a copy fee of 30 gold pieces.");
					if (hasItem(p, ItemId.COINS.id(), 30)) {
						npcTalk(p, n, "Do you agree to pay?");
						int pay = showMenu(p, n,
							"Yes, I'll pay for it.",
							"No, I won't pay for it.");
						if (pay == 0) {
							p.message("You hand over 30 gold coins.");
							removeItem(p, ItemId.COINS.id(), 30);
							addItem(p, ItemId.RADIMUS_SCROLLS.id(), 1);
							npcTalk(p, n, "Ok, please don't lose this one..");
						} else if (pay == 1) {
							npcTalk(p, n, "Well, that's your decision, of course... ",
								"but you won't be able to complete the quest without it.",
								"Excuse, me now won't you, I have other business to attend to.");
							radimusInHouseDialogue(p, n, RadimusInHouse.SAME_MENU_NO_SCROLLS);
						}
					} else {
						npcTalk(p, n, "It looks as if you don't have the funds for it at the moment.",
							"How irritating...");
					}
					break;
			}
		}
	}

	@Override
	public boolean blockInvUseOnNpc(Player p, Npc n, Item item) {
		return n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id() && (item.getID() == ItemId.RADIMUS_SCROLLS_COMPLETE.id()
				|| item.getID() == ItemId.GILDED_TOTEM_POLE.id() || item.getID() == ItemId.TOTEM_POLE.id());
	}

	@Override
	public void onInvUseOnNpc(Player p, Npc n, Item item) {
		if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id() && item.getID() == ItemId.GILDED_TOTEM_POLE.id()) {
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 11) {
				npcTalk(p, n, "Go through to the main Legends Guild and I will join you.");
				return;
			}
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 10) {
				npcTalk(p, n, (p.isMale() ? "Sir" : "Madam") + ", this is truly amazing...");
				if (!hasItem(p, ItemId.RADIMUS_SCROLLS_COMPLETE.id())) {
					npcTalk(p, n, "However, I need you to complete the map of the ,",
						"Kharazi Jungle before your quest is complete.");
				} else {
					message(p, n, 1300, "Radimus Erkle orders some guards to take the totem pole,",
						"into the main Legends Hall.");
					removeItem(p, item.getID(), 1);
					npcTalk(p, n, "That will take pride of place in the Legends Guild ",
						"As a reminder of your quest to gain entry.",
						"And so that many other great adventurers can admire your bravery.",
						"Well, it seems that you have completed the tasks I set you.",
						"That map of the Kharazi jungle will be very helpful in future.",
						"Congratulations, welcome to the Legends Guild.",
						"Go through to the main Legends Guild building ",
						"and I will join you shortly.");
					p.updateQuestStage(Constants.Quests.LEGENDS_QUEST, 11);
				}
			} else {
				p.message("You have not completed this quest - submitting bug abuse.");
			}
		}
		else if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id() && item.getID() == ItemId.TOTEM_POLE.id()) {
			npcTalk(p, n, "Hmmm, well, it is very impressive.",
					"Especially since it looks very heavy...",
					"However, it lacks a certain authenticity,",
					"my guess is that you made it.",
					"But I'm not sure why.",
					"We would like to have a really nice display object",
					"to put on display in the Legends Guild main hall.",
					"Do you think you could get something more authentic ?");
		}
		else if (n.getID() == NpcId.SIR_RADIMUS_ERKLE_HOUSE.id() && item.getID() == ItemId.RADIMUS_SCROLLS_COMPLETE.id()) {
			npcTalk(p, n, "Well done " + (p.isMale() ? "Sir" : "Madam") + ", very well done...",
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
