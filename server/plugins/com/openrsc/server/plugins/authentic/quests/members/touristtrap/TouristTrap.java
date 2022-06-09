package com.openrsc.server.plugins.authentic.quests.members.touristtrap;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class TouristTrap implements QuestInterface, TalkNpcTrigger, UseNpcTrigger, OpLocTrigger, OpNpcTrigger,
	KillNpcTrigger, AttackNpcTrigger, SpellNpcTrigger, PlayerRangeNpcTrigger, OpBoundTrigger {

	private static final Logger LOGGER = LogManager.getLogger(TouristTrap.class);

	/**
	 * Player isWielding
	 **/
	private static final int[] restricted = {0, 1, 3, 4, 5, 6, 7, 9};
	public static final int[] allow = {ItemId.DESERT_ROBE.id(), ItemId.DESERT_SHIRT.id(), ItemId.METAL_KEY.id(), ItemId.SLAVES_ROBE_BOTTOM.id(), ItemId.SLAVES_ROBE_TOP.id()};
	/**
	 * Quest Objects
	 **/
	private static int STONE_GATE = 916;
	private static int IRON_GATE = 932;
	private static int JAIL_DOOR = 177;
	private static int WINDOW = 178;
	private static int ROCK_1 = 953;
	private static int WOODEN_DOORS = 958;
	private static int DESK = ItemId.SLAVES_ROBE_TOP.id();
	private static int BOOKCASE = 1004;
	private static int CAPTAINS_CHEST = 1005;
	/**
	 * Quest WallObjects
	 **/
	private static int TENT_DOOR_1 = 198;
	private static int TENT_DOOR_2 = 196;
	private static int CAVE_JAIL_DOOR = 180;
	private static int STURDY_IRON_GATE = 200;
	private ArrayList<Integer> wieldPos = new ArrayList<>();
	private ArrayList<Integer> allowed = new ArrayList<>();

	// points player may be left of in the desert
	private final Point[] desertTPPoints = new Point[]{new Point(121, 743), new Point(135, 775), new Point(121, 803), new Point(102, 775), new Point(93, 765) };

	@Override
	public int getQuestId() {
		return Quests.TOURIST_TRAP;
	}

	@Override
	public String getQuestName() {
		return "Tourist trap (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.TOURIST_TRAP.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(final Player player) {
		player.message("");
		delay();
		player.message("@yel@                          !!!  Well Done !!!   ");
		delay();
		player.message("");
		delay();
		player.message("@gre@***********************************************************");
		delay();
		player.message("@gre@*** You have completed the 'Tourist Trap' Quest ! ***");
		delay();
		player.message("@gre@***********************************************************");
		delay();
		final QuestReward reward = Quest.TOURIST_TRAP.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	@Override
	public boolean blockTalkNpc(final Player player, final Npc npc) {
		return inArray(npc.getID(), NpcId.IRENA.id(), NpcId.MERCENARY.id(), NpcId.MERCENARY_CAPTAIN.id(), NpcId.MERCENARY_ESCAPEGATES.id(),
				NpcId.CAPTAIN_SIAD.id(), NpcId.MINING_SLAVE.id(), NpcId.ESCAPING_MINING_SLAVE.id(), NpcId.BEDABIN_NOMAD.id(), NpcId.BEDABIN_NOMAD_GUARD.id(),
				NpcId.AL_SHABIM.id(), NpcId.MERCENARY_LIFTPLATFORM.id(), NpcId.MERCENARY_JAILDOOR.id(), NpcId.ANA.id());
	}

	private void irenaDialogue(final Player player, final Npc npc, final int cID) {
		if (npc.getID() == NpcId.IRENA.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(this)) {
					case 0:
						mes("Irena seems to be very upset and cries as you start to approach her.");
						delay(3);
						npcsay(player, npc, "Boo hoo, oh dear, my only daughter....");
						int menu = multi(player, npc,
							"What's the matter?",
							"Cheer up, it might never happen.");
						if (menu == 0) {
							npcsay(player, npc, "Oh dear...my daughter, Ana, has gone missing in the desert.",
								"I fear that she is lost, or perhaps...*sob* even worse.");
							int matterMenu = multi(player, npc,
								"When did she go into the desert?",
								"What did she go into the desert for?",
								"Is there a reward if I get her back?");
							if (matterMenu == 0) {
								irenaDialogue(player, npc, Irene.WHENDIDSHEGO);
							} else if (matterMenu == 1) {
								irenaDialogue(player, npc, Irene.WHATDIDSHEGO);
							} else if (matterMenu == 2) {
								irenaDialogue(player, npc, Irene.REWARD);
							}

						} else if (menu == 1) {
							npcsay(player, npc, "It may already have happened you thoughtless oaf!",
								"My daughter, Ana, could be dead or dying in the desert!!!");
							int newMenu = multi(player, npc,
								"When did she go into the desert?",
								"What did she go into the desert for?",
								"Is there a reward if I get her back?");
							if (newMenu == 0) {
								irenaDialogue(player, npc, Irene.WHENDIDSHEGO);
							} else if (newMenu == 1) {
								irenaDialogue(player, npc, Irene.WHATDIDSHEGO);
							} else if (newMenu == 2) {
								irenaDialogue(player, npc, Irene.REWARD);
							}

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
						npcsay(player, npc, "Please bring my daughter back to me.",
							"She is most likely lost in the Desert somewhere.",
							"I miss her so much....",
							"Wahhhhh!",
							"*Sob*");
						break;
					case 9:
						if (!player.getCarriedItems().hasCatalogID(ItemId.ANA_IN_A_BARREL.id(), Optional.of(false))) {
							npcsay(player, npc, "Please bring my daughter back to me.",
								"She is most likely lost in the Desert somewhere.",
								"I miss her so much....",
								"Wahhhhh!",
								"*Sob*");
						} else {
							npcsay(player, npc, "Hey, great you've found Ana!");
							mes("You show Irena the barrel with Ana in it.");
							delay(3);
							player.getCarriedItems().remove(new Item(ItemId.ANA_IN_A_BARREL.id()));
							player.updateQuestStage(this, 10);
							Npc Ana = addnpc(player.getWorld(), NpcId.ANA.id(), player.getX(), player.getY(), 60000);
							Ana.teleport(player.getX(), player.getY() + 1);
							if (Ana != null) {
								delay();
								player.message("@gre@Ana: Hey great, there's my Mum!");
								npcsay(player, Ana,
									"Great! Thanks for getting me out of that mine!",
									"And that barrel wasn't too bad anyway!",
									"Pop by again sometime, I'm sure we'll have a barrel of laughs!",
									"Oh! I nearly forgot, here's a key I found in the tunnels.",
									"It might be of some use to you, not sure what it opens.");
								give(player, ItemId.WROUGHT_IRON_KEY.id(), 1);
								mes("Ana spots Irena and waves...");
								delay(3);
								npcsay(player, Ana, "Hi Mum!",
									"Sorry, I have to go now!");
								Ana.remove();
							}
							npcsay(player, npc, "Hi Ana!");
							rewardMenu(player, npc, true);
							player.getCache().remove("tried_ana_barrel");
						}
						break;
					case 10:
						if (player.getCache().hasKey("advanced1")) {
							lastRewardMenu(player, npc, true);
						} else {
							rewardMenu(player, npc, true);
						}
						break;
					case -1:
						player.message("Irena seems happy now that her daugher has returned home.");
						npcsay(player, npc, "Thanks so much for returning my daughter to me.",
							"I expect that she will go on another trip soon though.",
							"She is the adventurous type...a bit like yourself really!",
							"Ok, see you around then!");
						player.message("Irena goes back to work.");
						break;
				}
			}
			switch (cID) {
				case Irene.WHENDIDSHEGO:
					npcsay(player, npc, "*Sob*",
						"She went in there just a few days ago, ",
						"She said she would be back yesterday.",
						"And she's not...");
					int menu = multi(player, npc,
						"What did she go into the desert for?",
						"Is there a reward if I get her back?",
						"I'll look for your daughter.");
					if (menu == 0) {
						irenaDialogue(player, npc, Irene.WHATDIDSHEGO);
					} else if (menu == 1) {
						irenaDialogue(player, npc, Irene.REWARD);
					} else if (menu == 2) {
						irenaDialogue(player, npc, Irene.LOOKFORDAUGHTER);
					}
					break;
				case Irene.WHATDIDSHEGO:
					npcsay(player, npc, "She was just travelling, a tourist you might say.",
						"*Sob* She said she would be safe and now she could be..");
					player.message("Irena's bottom lip trembles a little.");
					npcsay(player, npc, "*Whhhhhaaaaa*");
					player.message("Irena cries her heart out in front of you.");
					int menuWhat = multi(player, npc,
						"When did she go into the desert?",
						"Is there a reward if I get her back?",
						"I'll look for your daughter.");
					if (menuWhat == 0) {
						irenaDialogue(player, npc, Irene.WHENDIDSHEGO);
					} else if (menuWhat == 1) {
						irenaDialogue(player, npc, Irene.REWARD);
					} else if (menuWhat == 2) {
						irenaDialogue(player, npc, Irene.LOOKFORDAUGHTER);
					}
					break;
				case Irene.REWARD:
					npcsay(player, npc, player.getText("TouristTrapIreneWellYesYoullHaveMyGratitude"),
						"And I'm sure that Ana will also be very pleased!",
						"And I may see if I can get a small reward together...",
						"But I cannot promise anything.",
						"So does that mean that you'll look for her then?");
					int rewardMenu = multi(player, npc,
						"Oh, Ok, I'll get your daughter back for you.",
						"No, sorry, I'm just too busy!");
					if (rewardMenu == 0) {
						irenaDialogue(player, npc, Irene.GETBACKDAUGHTER);
					} else if (rewardMenu == 1) {
						npcsay(player, npc, "Oh really, can't I persuade you in anyway?");
					}
					break;
				case Irene.LOOKFORDAUGHTER:
					npcsay(player, npc, "That would be very good of you.",
						"You would have the gratitude of a very loving mother.",
						"Are you sure you want to take on that responsibility?");
					int lookMenu = multi(player, npc,
						"Oh, Ok, I'll get your daughter back for you.",
						"No, sorry, I'm just too busy!");
					if (lookMenu == 0) {
						irenaDialogue(player, npc, Irene.GETBACKDAUGHTER);
					} else if (lookMenu == 1) {
						npcsay(player, npc, "Oh really, can't I persuade you in anyway?");
					}
					break;
				case Irene.GETBACKDAUGHTER:
					npcsay(player, npc, "That would be great!",
						"That's really very nice of you!",
						"She was wearing a red silk scarf when she left.");
					player.updateQuestStage(this, 1);
					break;
			}
		}
	}

	private void lastRewardMenu(final Player player, final Npc npc, final boolean showIrenaDialogue) {
		if (showIrenaDialogue) {
			npcsay(player, npc, "Thank you very much for returning my daughter to me.",
				"I'm really very grateful...",
				"I would like to reward you for your bravery and daring.",
				"I can offer you increased knowledge in one of the following areas.");
		}
		final XPReward origXpReward = Quest.TOURIST_TRAP.reward().getXpRewards()[0];
		XPReward xpReward;
		int lastRewardMenu = multi(player, npc, false, //do not send over
			"Fletching.",
			"Agility.",
			"Smithing.",
			"Thieving");
		if (lastRewardMenu == 0) {
			skillReward(player, npc, Skill.FLETCHING, false);
		} else if (lastRewardMenu == 1) {
			skillReward(player, npc, Skill.AGILITY, false);
		} else if (lastRewardMenu == 2) {
			skillReward(player, npc, Skill.SMITHING, false);
		} else if (lastRewardMenu == 3) {
			skillReward(player, npc, Skill.THIEVING, false);
		}
	}

	private void rewardMenu(final Player player, final Npc npc, final boolean showIrenaDialogue) {
		npcsay(player, npc, "Thank you very much for returning my daughter to me.",
			"I'm really very grateful...",
			"I would like to reward you for your bravery and daring.",
			"I can offer you increased knowledge in two of the following areas.");
		int rewardMenu = multi(player, npc, false, //do not send over
			"Fletching.",
			"Agility.",
			"Smithing.",
			"Thieving");
		if (rewardMenu == 0) {
			skillReward(player, npc, Skill.FLETCHING, true);
		} else if (rewardMenu == 1) {
			skillReward(player, npc, Skill.AGILITY, true);
		} else if (rewardMenu == 2) {
			skillReward(player, npc, Skill.SMITHING, true);
		} else if (rewardMenu == 3) {
			skillReward(player, npc, Skill.THIEVING, true);
		}
	}

	private void skillReward(final Player player, final Npc npc, final Skill skill, final boolean isFirst) {
		final XPReward origXpReward = Quest.TOURIST_TRAP.reward().getXpRewards()[0];
		XPReward xpReward = origXpReward.copyTo(skill);
		incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		mes("You advance your stat in " + StringUtil.convertToTitleCase(skill.name()) + ".");
		delay(3);
		if (!isFirst) {
			player.sendQuestComplete(Quests.TOURIST_TRAP);
			if (player.getCache().hasKey("advanced1")) {
				player.getCache().remove("advanced1");
			}
		} else {
			mes("Ok, now choose your second skil.");
			delay(3);
			if (!player.getCache().hasKey("advanced1")) {
				player.getCache().store("advanced1", true);
			}
			lastRewardMenu(player, npc, false);
		}
	}

	private void mercenaryDialogue(final Player player, final Npc npc, final int cID) {
		if (npc.getID() == NpcId.MERCENARY.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(this)) {
					case 0:
						if (player.getCarriedItems().hasCatalogID(ItemId.METAL_KEY.id(), Optional.of(false))) {
							npcsay(player, npc, "Move along now..we've had enough of your sort!");
							return;
						}
						npcsay(player, npc, "Yeah, what do you want?");
						int menu = multi(player, npc,
							"What is this place?",
							"What are you guarding?");
						if (menu == 0) {
							mercenaryDialogue(player, npc, Mercenary.PLACE_START);
						} else if (menu == 1) {
							mercenaryDialogue(player, npc, Mercenary.GUARDING_FIRST);
						}
						break;
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
						if (player.getCarriedItems().hasCatalogID(ItemId.METAL_KEY.id(), Optional.of(false)) || player.getLocation().inTouristTrapCave()) {
							npcsay(player, npc, "Move along now..we've had enough of your sort!");
							return;
						}
						if (player.getQuestStage(this) == 1 && player.getCache().hasKey("first_kill_captn") && player.getCache().getBoolean("first_kill_captn")) {
							//dialogue only on stage 1
							//talking after captain is killed -> special dialogue bet and sets false flag
							boolean completed = false;
							if (!player.getCache().hasKey("mercenary_bet")) {
								npcsay(player, npc, "Well, you've killed our Captain.",
									"I guess you've proved yourself in combat.",
									"However, you've left a horrible mess now.",
									"And it's gonna cost you for us to clean it up.",
									"Let's say 20 gold and we won't have to get rough with you?");
								int opts = multi(player, npc, false, //do not send over
									"Yeah, ok, I'll give you 20 gold.",
									"I'll give you 15, that's all you're gettin'",
									"You can whistle for you money, I'll take you all on.");
								if (opts == 0) {
									say(player, npc, "Yeah, ok, I'll give you 20 gold.");
									if (ifheld(player, ItemId.COINS.id(), 20)) {
										player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 20));
										npcsay(player, npc, "Good! Seeya, we have some cleaning to do.");
										completed = true;
									} else {
										npcsay(player, npc, "You don't have the gold and now we're gonna teach you a lesson.");
										mes("The Guards search you!");
										delay(3);
										mercenaryDialogue(player, npc, Mercenary.LEAVE_DESERT);
										completed = true;
									}
								} else if (opts == 1) {
									say(player, npc, "I'll give you 15, that's all you're gettin'");
									if (ifheld(player, ItemId.COINS.id(), 15)) {
										player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 15));
										npcsay(player, npc, "Ok, we'll take fifteen, you push a hard bargain!");
										completed = true;
									} else {
										npcsay(player, npc, "You don't have the gold and now we're gonna teach you a lesson.");
										mes("The Guards search you!");
										delay(3);
										mercenaryDialogue(player, npc, Mercenary.LEAVE_DESERT);
										completed = true;
									}
								} else if (opts == 2) {
									say(player, npc, "You can whistle for your money, I'll take you all on.");
									npcsay(player, npc, "Ok, that's it, we're gonna teach you a lesson.");
									mes("The Guards search you!");
									delay(3);
									mercenaryDialogue(player, npc, Mercenary.LEAVE_DESERT);
									completed = true;
								}
							} else {
								say(player, npc, "Hey, I've come to collect my bet!");
								npcsay(player, npc, "Well, I guess congratulations are in order.");
								say(player, npc, "Thanks!");
								npcsay(player, npc, "And we'll only charge the paltry sum of..erm...");
								mes("The guards starts to do some mental calculations...");
								delay(3);
								mes("You can see his brow furrow and he starts to sweat profusely");
								delay(3);
								switch (player.getCache().getInt("mercenary_bet")) {
									case 5:
										npcsay(player, npc, "Five gold for cleaning up the mess.",
											"You have won 1 Gold piece!");
										give(player, ItemId.COINS.id(), 1);
										break;
									case 10:
										npcsay(player, npc, "10 gold for cleaning up the mess.",
											"You have won 2 Gold pieces!");
										give(player, ItemId.COINS.id(), 2);
										break;
									case 15:
										npcsay(player, npc, "15 gold for cleaning up the mess.",
											"You have won 4 Gold pieces!");
										give(player, ItemId.COINS.id(), 4);
										break;
									case 20:
										npcsay(player, npc, "20 gold for cleaning up the mess.",
											"You have won 10 Gold pieces!");
										give(player, ItemId.COINS.id(), 10);
										break;
								}
								npcsay(player, npc, "Well done..!", "Ha, ha, ha ha!");
								player.message("The guards walk off chuckling to themselves.");
								completed = true;
							}
							if (completed) {
								player.getCache().store("first_kill_captn", false);
							}
							return;
						}

						npcsay(player, npc, "Yeah, what do you want?");
						int option = multi(player, npc,
							"What is this place?",
							"What are you guarding?",
							"I'm looking for a woman called Ana, have you seen her?");
						if (option == 0) {
							mercenaryDialogue(player, npc, Mercenary.PLACE_START);
						} else if (option == 1) {
							mercenaryDialogue(player, npc, Mercenary.GUARDING_FIRST);
						} else if (option == 2) {
							mercenaryDialogue(player, npc, Mercenary.ANA_FIRST);
						}
						break;
					case 6:
					case 7:
					case 8:
					case 9:
					case 10:
						npcsay(player, npc, "Move along now..we've had enough of your sort!");
						break;
					case -1:
						npcsay(player, npc, "What're you looking at?");
						break;
				}
			}
			switch (cID) {
				case Mercenary.THROW_PLAYER:
					npcsay(player, npc, "Don't try to fool me, you don't have five gold coins!",
						"Before you try to bribe someone, make sure you have the money effendi!");
					mercenaryDialogue(player, npc, Mercenary.LEAVE_DESERT);
					break;
				case Mercenary.THROW_PRISON:
					mes("The Guards search you!");
					delay(3);
					int rand = DataConversions.random(0, 3);
					if (player.getCarriedItems().hasCatalogID(ItemId.CELL_DOOR_KEY.id(), Optional.of(false)) && rand == 0) {
						player.message("The guards find the cell door key and remove it!");
						player.getCarriedItems().remove(new Item(ItemId.CELL_DOOR_KEY.id()));
					}
					if (player.getCarriedItems().hasCatalogID(ItemId.METAL_KEY.id(), Optional.of(false)) && rand == 1) {
						player.message("The guards find the main gate key and remove it!");
						player.getCarriedItems().remove(new Item(ItemId.METAL_KEY.id()));
					}
					mes("More guards rush to catch you.");
					delay(3);
					mes("You are roughed up a bit by the guards as you're manhandlded to a cell.");
					delay(3);
					if (npc != null) {
						npcsay(player, npc, "Into the cell you go! I hope this teaches you a lesson.");
					}
					player.teleport(89, 801);
					break;
				case Mercenary.LEAVE_DESERT:
					npcsay(player, npc, "Guards, guards!");
					if (!npc.inCombat()) {
						npc.setChasing(player);
					}
					mes("Nearby guards quickly grab you and rough you up a bit.");
					delay(3);
					npcsay(player, npc, "Let's see how good you are with desert survival techniques!");
					mes("You're bundled into the back of a cart and blindfolded...");
					delay(3);
					mes("Sometime later you wake up in the desert.");
					delay(3);
					// TODO: can also take waterskins and replace them with single empty waterskin.
					if (player.getCarriedItems().hasCatalogID(ItemId.BOWL_OF_WATER.id(), Optional.of(false))) {
						npcsay(player, npc, "You won't be needing that water any more!");
						mes("The guards throw your water away...");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.BOWL_OF_WATER.id()));
					}
					Point desertLoc = desertTPPoints[DataConversions.getRandom().nextInt(desertTPPoints.length)];
					player.teleport(desertLoc.getX(), desertLoc.getY());
					break;
				case Mercenary.PLACE_START:
					npcsay(player, npc, "It's none of your business now get lost.");
					int menu = multi(player, npc,
						"Perhaps five gold coins will make it my business?",
						"Ok, thanks.");
					if (menu == 0) {
						npcsay(player, npc, "It certainly will!");
						if (ifheld(player, ItemId.COINS.id(), 5)) {
							player.message("The guard takes the five gold coins.");
							player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
							npcsay(player, npc, "Now then, what did you want to know?");
							int secondMenu = multi(player, npc,
								"What is this place?",
								"What are you guarding?");
							if (secondMenu == 0) {
								mercenaryDialogue(player, npc, Mercenary.PLACE_SECOND);
							} else if (secondMenu == 1) {
								mercenaryDialogue(player, npc, Mercenary.GUARDING_SECOND);
							}
						} else {
							mercenaryDialogue(player, npc, Mercenary.THROW_PLAYER);
						}
					} else if (menu == 1) {
						npcsay(player, npc, "Yeah, whatever!");
					}
					break;
				case Mercenary.PLACE_SECOND:
					npcsay(player, npc, "It's just a mining camp. Prisoners are sent here from Al Kharid.",
						"They serve out their sentence by mining.",
						"Most prisoners will end their days here, surrounded by desert.");
					say(player, npc, "So you could almost say that they got their... 'just desserts'");
					npcsay(player, npc, "You could say that...");
					mes("There is an awkward pause");
					delay(3);
					npcsay(player, npc, "But it wouldn't be very funny.");
					mes("There is another awkward pause.");
					delay(3);
					say(player, npc, "When they talk about the silence of the desert,",
						"this must be what they mean.");
					player.message("The guard starts losing interest in the conversation.");
					int options = multi(player, npc,
						"Can I take a look around the place?",
						"Ok thanks.");
					if (options == 0) {
						npcsay(player, npc, "Not really. The Captain won't let you in the compound.",
							"He's the only one who has the key to the gate.",
							"And if you talk to him, he'll probably just order us to kill you.",
							"Unless...");
						int newMenu = multi(player, npc,
							"Does the Captain order you to kill a lot of people?",
							"Unless what?");
						if (newMenu == 0) {
							mercenaryDialogue(player, npc, Mercenary.ORDER_KILL_PEOPLE);
						} else if (newMenu == 1) {
							npcsay(player, npc, "Unless he has a use for you.",
								"He's been trying to track down a someone called 'Al Zaba Bhasim'.",
								"You could offer to catch him and that might put you in his good books?");
							int tenthMenu = multi(player, npc,
								"Where would I find this Al Zaba Bhasim?",
								"Ok thanks.");
							if (tenthMenu == 0) {
								npcsay(player, npc, "Well, he could be anywhere, he's a nomadic desert dweller.",
									"However, he is frequently to be found to the west in the ",
									"hospitality of the tenti's.");
								int eleventhMenu = multi(player, npc, false, //do not send over
									"The Tenti's, who are they?",
									"Ok thanks.");
								if (eleventhMenu == 0) {
									say(player, npc, "The Tenti's, who are they?");
									npcsay(player, npc, "Well, we're not really sure what they're proper name is.",
										"But they live in tents so we call them the 'Tenti's'.");
									int twelftMenu = multi(player, npc, false, //do not send over
										"Ok thanks.",
										"Is Al Zaba Bhasim very tough?");
									if (twelftMenu == 0) {
										say(player, npc, "Ok, thanks.");
										npcsay(player, npc, "Yeah, whatever!");
									} else if (twelftMenu == 1) {
										say(player, npc, "Is Al Zaba Bhasim very tough?");
										npcsay(player, npc, "Well, I'm not sure, but by all accounts, he is a slippery fellow.",
											"The Captain has been trying to capture him for years.",
											"A bit of a waste of time if you ask me.",
											"Anyway, I have to get going, I do have work to do.");
										player.message("The guard walks off.");
									}
								} else if (eleventhMenu == 1) {
									say(player, npc, "Ok, thanks.");
									npcsay(player, npc, "Yeah, whatever!");
								}
							} else if (tenthMenu == 1) {
								npcsay(player, npc, "Yeah, whatever!");
							}
						}
					} else if (options == 1) {
						npcsay(player, npc, "Yeah, whatever!");
					}
					break;
				case Mercenary.ORDER_KILL_PEOPLE:
					player.message("The guard snorts.");
					npcsay(player, npc, "*Snort*",
						"Just about anyone who talks to him.",
						"Unless he has a use for you, he'll probably just order us to kill you.",
						"And it's such a horrible job cleaning up the mess afterwards.");
					int sixthMenu = multi(player, npc,
						"Not to mention the senseless waste of human life.",
						"Ok thanks.");
					if (sixthMenu == 0) {
						npcsay(player, npc, "Heh?");
						mes("The guard looks at you with a confused stare...");
						delay(3);
						int seventhMenu = multi(player, npc, false, //do not send over
							"It doesn't sound as if you respect your Captain much.",
							"Ok thanks.");
						if (seventhMenu == 0) {
							say(player, npc, "It doesn't sound is if you respect your Captain much.");
							npcsay(player, npc, "Well, to be honest.");
							mes("The guard looks around conspiratorially.");
							delay(3);
							npcsay(player, npc, "We think he's not exactly as brave as he makes out.",
								"But we have to follow his orders.",
								"If someone called him a coward, ",
								"or managed to trick him into a one-on-one duel.",
								"Many of us bet that he'll be slaughtered in double quick time.",
								"And all the men agreed that they wouldn't intervene.");
							int eightMenu = multi(player, npc, false, //do not send over
								"Can I have a bet on that?",
								"Ok Thanks.");
							if (eightMenu == 0) {
								say(player, npc, "Can I have a bet on that?");
								if (player.getCache().hasKey("mercenary_bet")) {
									npcsay(player, npc, "Sorry, we've already taken your bet, wouldn't want any cheating now.",
										"Anyway, I have to get back to work. See ya around...");
									return;
								}
								npcsay(player, npc, "Well, if you think you stand a chance, sure.",
									"But remember, if he gives us an order, we have to obey.");
								int ninthMenu = multi(player, npc,
									"I'll bet 5 gold that I win.",
									"I'll bet 10 gold that I win.",
									"I'll bet 15 gold that I win.",
									"I'll bet 20 gold that I win.",
									"Ok, thanks.");
								if (ninthMenu >= 0 && ninthMenu <= 3) {
									int betAmount = 5, recvAmount = 6;
									if (ninthMenu == 0) {
										betAmount = 5;
										recvAmount = 6;
									} else if (ninthMenu == 1) {
										betAmount = 10;
										recvAmount = 12;
									} else if (ninthMenu == 2) {
										betAmount = 15;
										recvAmount = 19;
									} else if (ninthMenu == 3) {
										betAmount = 20;
										recvAmount = 30;
									}
									if (ifheld(player, ItemId.COINS.id(), betAmount)) {
										npcsay(player, npc, "Great, I'll take that bet.");
										player.message("You hand over " + betAmount + " gold coins.");
										player.getCarriedItems().remove(new Item(ItemId.COINS.id(), betAmount));
										npcsay(player, npc, "Ok, if you win, you'll get " + recvAmount + "gold back.");
										player.getCache().set("mercenary_bet", betAmount);
									}
									npcsay(player, npc, "Anyway, I have to get going, I do have work to do.");
									player.message("The guard walks off.");
								} else if (ninthMenu == 4) {
									npcsay(player, npc, "Yeah, whatever!");
								}
							} else if (eightMenu == 1) {
								say(player, npc, "Ok, thanks.");
								npcsay(player, npc, "Yeah, whatever!");
							}
						} else if (seventhMenu == 1) {
							say(player, npc, "Ok, thanks.");
							npcsay(player, npc, "Yeah, whatever!");
						}
					} else if (sixthMenu == 1) {
						npcsay(player, npc, "Yeah, whatever!");
					}
					break;
				case Mercenary.GUARDING_FIRST:
					npcsay(player, npc, "Get lost before I chop off your head!");
					int chopMenu = multi(player, npc, false, //do not send over
						"Ok thanks.",
						"Perhaps these five gold coins will sweeten your mood?");
					if (chopMenu == 0) {
						say(player, npc, "Ok, thanks.");
						npcsay(player, npc, "Yeah, whatever!");
					} else if (chopMenu == 1) {
						say(player, npc, "Perhaps these five gold coins will sweeten your mood?");
						if (ifheld(player, ItemId.COINS.id(), 5)) {
							npcsay(player, npc, "Well, it certainly will help...");
							player.message("The guard takes the five gold coins.");
							player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
							npcsay(player, npc, "Now then, what did you want to know?");
							int knowMenu = multi(player, npc,
								"What is this place?",
								"What are you guarding?",
								"I'm looking for a woman called Ana, have you seen her?");
							if (knowMenu == 0) {
								mercenaryDialogue(player, npc, Mercenary.PLACE_SECOND);
							} else if (knowMenu == 1) {
								mercenaryDialogue(player, npc, Mercenary.GUARDING_SECOND);
							} else if (knowMenu == 2) {
								mercenaryDialogue(player, npc, Mercenary.ANA_SECOND);
							}
						} else {
							mercenaryDialogue(player, npc, Mercenary.THROW_PLAYER);
						}
					}
					break;
				case Mercenary.GUARDING_SECOND:
					npcsay(player, npc, "Well, if you have to know, we're making sure that no prisoners get out.");
					mes("The guard gives you a disaproving look.");
					delay(3);
					npcsay(player, npc, "And to make sure that unauthorised people don't get in.");
					mes("The guard looks around nervously.");
					delay(3);
					npcsay(player, npc, "You'd better go now before the Captain orders us to kill you.");
					int gmenu = multi(player, npc, false, //do not send over
						"Does the Captain order you to kill a lot of people?",
						"Ok Thanks.");
					if (gmenu == 0) {
						say(player, npc, "Does the Captain order you to kill a lot of people?");
						mercenaryDialogue(player, npc, Mercenary.ORDER_KILL_PEOPLE);
					} else if (gmenu == 2) {
						say(player, npc, "Ok, thanks.");
						npcsay(player, npc, "Yeah, whatever!");
					}
					break;
				case Mercenary.ANA_FIRST:
					npcsay(player, npc, "No, now get lost!");
					int altMenu = multi(player, npc,
						"Perhaps five gold coins will help you remember?",
						"Ok, thanks.");
					if (altMenu == 0) {
						npcsay(player, npc, "Hmm, it might help!");
						if (ifheld(player, ItemId.COINS.id(), 5)) {
							player.message("The guards takes the five gold coins.");
							player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
							npcsay(player, npc, "Now then, what did you want to know?");
							int anaMenu = multi(player, npc,
								"I'm looking for a woman called Ana, have you seen her?",
								"What is this place?",
								"What are you guarding?");
							if (anaMenu == 0) {
								mercenaryDialogue(player, npc, Mercenary.ANA_SECOND);
							} else if (anaMenu == 1) {
								mercenaryDialogue(player, npc, Mercenary.PLACE_SECOND);
							} else if (anaMenu == 2) {
								mercenaryDialogue(player, npc, Mercenary.GUARDING_SECOND);
							}
						} else {
							mercenaryDialogue(player, npc, Mercenary.THROW_PLAYER);
						}
					} else if (altMenu == 1) {
						npcsay(player, npc, "Yeah, whatever!");
					}
					break;
				case Mercenary.ANA_SECOND:
					npcsay(player, npc, "Hmm, well, we get a lot of people in here.",
						"But not many women though...",
						"Saw one come in last week....",
						"But I don't know if it's the woman you're looking for?");
					int lastMenu = multi(player, npc,
						"What is this place?",
						"What are you guarding?");
					if (lastMenu == 0) {
						mercenaryDialogue(player, npc, Mercenary.PLACE_SECOND);
					} else if (lastMenu == 1) {
						mercenaryDialogue(player, npc, Mercenary.GUARDING_SECOND);
					}
					break;
			}
		}
	}

	private void mercenaryCaptainDialogue(final Player player, final Npc npc, final int cID) {
		if (npc.getID() == NpcId.MERCENARY_CAPTAIN.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(this)) {
					case 0:
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
					case -1:
						if (player.getCarriedItems().hasCatalogID(ItemId.METAL_KEY.id(), Optional.of(false))) {
							npcsay(player, npc, "Move along now...we've had enough of your sort!");
							return;
						}
						player.message("You approach the Mercenary Captain.");
						int menu = multi(player, npc,
							"Hello.",
							"You there!",
							"Hey ugly!");
						if (menu == 0) {
							npcsay(player, npc, "Be off Effendi, you are not wanted around here.");
							int be = multi(player, npc,
								"That's rude, I ought to teach you some manners.",
								"I 'll offer you something in return for your time.");
							if (be == 0) {
								npcsay(player, npc, "Oh yes! How might you do that?",
									"You seem little more than a gutter dweller.",
									"How could you teach me manners?");
								int manners = multi(player, npc, false, //do not send over
									"With my right fist and a good deal of force.",
									"Err, sorry, I thought I was talking to someone else.");
								if (manners == 0) {
									say(player, npc, "With my good right arm and a good deal of force.");
									npcsay(player, npc, "Oh yes, ready your weapon then!",
										"I'm sure you won't mind if my men join in?",
										"Har, har, har!",
										"Guards, kill this gutter dwelling slime.");
									captainWantToThrowPlayer(player, npc);
								} else if (manners == 1) {
									say(player, npc, "Err, sorry, I thought I was talking to someone else.");
									npcsay(player, npc, "Well, Effendi, you do need to be carefull of what you say to people.",
										"Or they may take it the wrong way.",
										"Thankfully, I'm very understanding.",
										"I'll just let me guards deal with you.",
										"Guards, teach this desert weed some manners.");
									captainWantToThrowPlayer(player, npc);
								}

							} else if (be == 1) {
								npcsay(player, npc, "Hmmm, oh yes, what might that be?");
								int menus = multi(player, npc,
									"I have some gold.",
									"There must be something that I can do for you?");
								if (menus == 0) {
									npcsay(player, npc, "Ha, ha, ha! You come to a mining camp and offer us gold!",
										"Thanks effendi, but we have all the gold that we'll ever need.",
										"Now be off with you,",
										"before we reduce you to a bloody mess on the sand.");
									int option = multi(player, npc,
										"There must be something that I can do for you?",
										"You don't scare me!");
									if (option == 0) {
										mercenaryCaptainDialogue(player, npc, MercenaryCaptain.MUSTBESOMETHINGICANDO);
									} else if (option == 1) {
										mercenaryCaptainDialogue(player, npc, MercenaryCaptain.DONTSCAREME);
									}
								} else if (menus == 1) {
									mercenaryCaptainDialogue(player, npc, MercenaryCaptain.MUSTBESOMETHINGICANDO);
								}
							}
						} else if (menu == 1) {
							npcsay(player, npc, "How dare you talk to me like that!",
								"Explain your business quickly...",
								"or my guards will slay you where you stand.");
							player.message("Some guards close in around you.");
							int thirdMenu = multi(player, npc,
								"I'm lost, can you help me?",
								"What are you guarding?");
							if (thirdMenu == 0) {
								mes("The captain smiles broadly and with a sickening voice says.");
								delay(3);
								npcsay(player, npc, "We are not a charity effendi,",
									"Be off with you before I have your head removed from your body.");
								int lostMenu = multi(player, npc,
									"What are you guarding?",
									"You don't scare me!");
								if (lostMenu == 0) {
									mercenaryCaptainDialogue(player, npc, MercenaryCaptain.GUARDING);
								} else if (lostMenu == 1) {
									mercenaryCaptainDialogue(player, npc, MercenaryCaptain.DONTSCAREME);
								}

							} else if (thirdMenu == 1) {
								mercenaryCaptainDialogue(player, npc, MercenaryCaptain.GUARDING);
							}
						} else if (menu == 2) {
							npcsay(player, npc, "I will not tolerate such insults..",
								player.getText("TouristTrapMercenaryCaptainGuardsKillThem"));
							mes("The captain marches away in disgust leaving his guards to tackle you.");
							delay(3);
							captainWantToThrowPlayer(player, npc);
						}
						break;

				}
				// 102, 775
			}
			switch (cID) {
				case MercenaryCaptain.GUARDING:
					npcsay(player, npc, "Effendi...",
						"For just one second, imagine that it's none of your business!",
						"Also imagine having your limbs pulled from your body one at a time.",
						"Now, what was the question again?");
					int fourthMenu = multi(player, npc,
						"Do you have sand in your ears, I said, 'What are you guarding?'",
						"You don't scare me!");
					if (fourthMenu == 0) {
						npcsay(player, npc, "Why....you ignorant, rude and eternally damned infidel,");
						player.message("The captain seems very agitated with what you just said.");
						npcsay(player, npc, "Guards, kill this infidel!");
						captainWantToThrowPlayer(player, npc);
					} else if (fourthMenu == 1) {
						mercenaryCaptainDialogue(player, npc, MercenaryCaptain.DONTSCAREME);
					}
					break;
				case MercenaryCaptain.DONTSCAREME:
					npcsay(player, npc, "Well, perhaps I can try a little harder.",
						"Guards, kill this infidel.");
					captainWantToThrowPlayer(player, npc);
					break;
				case MercenaryCaptain.MUSTBESOMETHINGICANDO:
					player.message("The Captain ponders a moment and then looks at you critically.");
					npcsay(player, npc, "You could bring me the head of Al Zaba Bhasim.",
						"He is the leader of the notorius desert bandits, they plague us daily.",
						"You should find them west of here.",
						"You should have no problem in finishing them all off.",
						"Do this for me and maybe I will consider helping you.");
					if (!player.getCache().hasKey("find_al_bhasim")) {
						player.getCache().store("find_al_bhasim", true);
					}
					int doThis = multi(player, npc,
						"Consider it done.",
						"I don't think I can do that.");
					if (doThis == 0) {
						npcsay(player, npc, "Good...run along then.",
							"You stand around flapping your tongue chatting like an insane camel.");
					} else if (doThis == 1) {
						npcsay(player, npc, "Hmm, well yes, I did consider that you might not be right for the job.",
							"Be off with you then before I turn my men loose on you.");
						int no = multi(player, npc, false, //do not send over
							"I guess you can't fight your own battles then?",
							"Ok, I'll move on.");
						if (no == 0) {
							say(player, npc, "I guess you can't fight your own battles then?");
							player.message("The men around you fall silent and the Captain silently fumes.");
							delay(3);
							player.message("All eyes turn to the Captain...");
							npcsay(player, npc, "Very well, if you're challenging me, let's get on with it!");
							player.message("The guards gather around to watch the fight.");
							npc.setChasing(player);
						} else if (no == 1) {
							say(player, npc, "Ok, I'll be moving along then.");
							npcsay(player, npc, "Effendi, I think you'll find that is the ",
								"wisest decision you have made today.");
						}
					}
					break;
			}
		}
	}

	private void slaveDialogue(final Player player, final Npc npc, final int cID) {
		if (npc.getID() == NpcId.MINING_SLAVE.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(this)) {
					case 0:
					case 1:
						npcsay(player, npc, "You look like a new 'recruit'.",
							"How long have you been here?");
						int menu = multi(player, npc,
							"I've just arrived.",
							"Oh, I've been here ages.");
						if (menu == 0) {
							npcsay(player, npc, "Yeah, it looks like it as well.");
							slaveDialogue(player, npc, Slave.NEWRECRUIT);
						} else if (menu == 1) {
							npcsay(player, npc, "That's funny, I haven't seen you around here before.",
								"You're clothes look too clean for you to have been here ages.");
							int secondMenu = multi(player, npc,
								"Ok, you caught me out.",
								"The guards allow me to clean my clothes.");
							if (secondMenu == 0) {
								npcsay(player, npc, "Ah ha! I knew it! A new recruit then?");
								slaveDialogue(player, npc, Slave.NEWRECRUIT);
							} else if (secondMenu == 1) {
								npcsay(player, npc, "Oh, a special relationship with the guards heh?",
									"How very nice of them.",
									"Maybe you could persuade them to let me out of here?");
								mes("The slave swaggers of with a sarcastic smirk on his face.");
								delay(3);
							}
						}
						break;
					case 2:
						npcsay(player, npc, "Hello again, are you ready to unlock my chains?");
						int opt = multi(player, npc,
							"Yeah, Ok, let's give it a go.",
							"I need to do some other things first.");
						if (opt == 0) {
							slaveDialogue(player, npc, Slave.GIVEITAGO);
						} else if (opt == 1) {
							npcsay(player, npc, "Ok, fair enough, let me know when you want to give it another go.");
						}
						break;
					case 3:
						npcsay(player, npc, "Do you have the Desert Clothes yet?");
						necessaryStuffSlave(player, npc);
						break;
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
					case 9:
					case 10:
					case -1:
						if (player.getLocation().inTouristTrapCave()) {
							npcsay(player, npc, "Can't you see I'm busy?");
							if ((!player.getCarriedItems().getEquipment().hasEquipped(ItemId.SLAVES_ROBE_BOTTOM.id())
								|| !player.getCarriedItems().getEquipment().hasEquipped(ItemId.SLAVES_ROBE_TOP.id())) && player.getQuestStage(this) != -1) {
								player.message("A guard notices you and starts running after you.");
								Npc npcN = ifnearvisnpc(player, NpcId.MERCENARY.id(), 10);
								if (npcN == null) {
									npcN = addnpc(player.getWorld(), NpcId.MERCENARY.id(), player.getX(), player.getY(), 60000);
									delay(2);
								}
								npcsay(player, npcN, "Hey! You're no slave!");
								npcN.startCombat(player);
								mes("The Guards search you!");
								delay(3);
								if (player.getCarriedItems().hasCatalogID(ItemId.CELL_DOOR_KEY.id(), Optional.of(false))) {
									player.message("The guards find the cell door key and remove it!");
									player.getCarriedItems().remove(new Item(ItemId.CELL_DOOR_KEY.id()));
								}
								mes("Some guards rush to help their comrade.");
								delay(3);
								mes("You are roughed up a bit by the guards as you're manhandlded into a cell.");
								delay(3);
								npcsay(player, npcN, "Into the cell you go! I hope this teaches you a lesson.");
								if (player.getQuestStage(this) >= 9) {
									player.teleport(74, 3626);
								} else {
									player.teleport(89, 801);
								}
							}
						} else if (player.getCarriedItems().hasCatalogID(ItemId.SLAVES_ROBE_BOTTOM.id(), Optional.of(false))
							&& player.getCarriedItems().hasCatalogID(ItemId.SLAVES_ROBE_TOP.id(), Optional.of(false))) {
							npcsay(player, npc, "Not much to do here but mine all day long.");
						} else {
							npcsay(player, npc, "Oh bother, I was caught by the guards again...",
								"Listen, if you can get me some Desert Clothes,",
								" I'll trade you for my slaves clothes again..",
								"Do you want to trade?");
							int trade = multi(player, npc,
								"Yes, I'll trade.",
								"No thanks...");
							if (trade == 0) {
								necessaryStuffSlave(player, npc);
							} else if (trade == 1) {
								npcsay(player, npc, "Ok, fair enough, let me know if you change your mind though.");
							}
						}
						break;
				}
			}
			switch (cID) {
				case Slave.NEWRECRUIT:
					npcsay(player, npc, "It's a shame that I won't be around long enough to get to know you.",
						"I'm making a break for it today.",
						"I have a plan to get out of here!",
						"It's amazing in it's sophistication.");
					int thirdMenu = multi(player, npc,
						"What are those big wooden doors in the corner of the compound?",
						"Oh yes, that sounds interesting.");
					if (thirdMenu == 0) {
						npcsay(player, npc, "They lead to an underground mine,",
							"but you really don't want to go down there.",
							"I've only seen slaves and guards go down there,",
							"I never see the slaves come back up.",
							"At least up here you have a nice view and a bit of sun.");
						mes("The slave smiles at you happily and then goes back to his work.");
						delay(3);
					} else if (thirdMenu == 1) {
						npcsay(player, npc, "Yes, it is actually.",
							"I have all the details figured out except for one.");
						int four = multi(player, npc, false, //do not send over
							"What's that then?",
							"Oh, that's a shame.");
						if (four == 0) {
							say(player, npc, "What's that then?");
							mes("The slave shakes his arms and the chains rattle loudly.");
							delay(3);
							npcsay(player, npc, "These bracelets, I can't seem to get them off.",
								"If I could get them off, I'd be able to climb my way",
								"out of here.");
							int five = multi(player, npc,
								"I can try to undo them for you.",
								"That's ridiculous, you're talking rubbish.");
							if (five == 0) {
								slaveDialogue(player, npc, Slave.UNDOTHEM);
							} else if (five == 1) {
								npcsay(player, npc, "No, it's true, I can make a break for it",
									"If I can just get these bracelets off.");
								int six = multi(player, npc,
									"Good luck!",
									"I can try to undo them for you.");
								if (six == 0) {
									npcsay(player, npc, "Thanks...same to you.");
								} else if (six == 1) {
									slaveDialogue(player, npc, Slave.UNDOTHEM);
								}
							}
						} else if (four == 1) {
							say(player, npc, "Oh, that's a shame...",
								"Still, 'worse things happen at sea right?'");
							npcsay(player, npc, "You've obviously never worked as a slave",
								"...in a mining camp...",
								"...in the middle of the desert");
							say(player, npc, "Well I suppose I'd better be getting on my way now...");
							player.message("The slave nods in agreement and goes back to work.");
						}
					}
					break;
				case Slave.UNDOTHEM:
					npcsay(player, npc, "Really, that would be great...");
					mes("The slave looks at you strangely.");
					delay(3);
					npcsay(player, npc, "Hang on a minute...I suppose you want something for doing this?",
						"The last time I did a trade in this place,",
						"I nearly lost the shirt from my back!");
					int trade = multi(player, npc, false, //do not send over
						"It's funny you should say that...",
						"That sounds awful.");
					if (trade == 0) {
						say(player, npc, "It's funny you should say that actually.");
						mes("The slave looks at you blankly.");
						delay(3);
						npcsay(player, npc, "Yeah, go on!");
						say(player, npc, "If I can get the chains off, you have to give me something, ok?");
						npcsay(player, npc, "Sure, what do you want?");
						say(player, npc, "I want your clothes!",
							"I can dress like a slave and gain access to the mine area to scout it out.");
						npcsay(player, npc, "Blimey! You're either incredibly brave or incredibly stupid.",
							"But what would I wear if you take my clothes?",
							"Get me some nice desert clothes and I'll think about it?",
							"Do you still want to try and undo the locks for me?");
						player.updateQuestStage(this, 2);
						player.getCache().remove("first_kill_captn");
						player.getCache().remove("mercenary_bet");
						int go = multi(player, npc,
							"Yeah, Ok, let's give it a go.",
							"I need to do some other things first.");
						if (go == 0) {
							slaveDialogue(player, npc, Slave.GIVEITAGO);
						} else if (go == 1) {
							npcsay(player, npc, "Ok, fair enough, let me know when you want to give it another go.");
						}
					} else if (trade == 1) {
						say(player, npc, "That sounds awful.");
						npcsay(player, npc, "Yeah, bunch of no hopers, tried to rob me blind.",
							"But I guess that's what you get when you deal with convicts.");
					}
					break;
				case Slave.GIVEITAGO:
					npcsay(player, npc, "Great!");
					mes("You use some nearby bits of wood and wire to try and pick the lock.");
					delay(3);
					int attempt1 = DataConversions.random(0, 1);
					//failed attempt 1
					if (attempt1 == 0) {
						mes("You fail!");
						delay(3);
						mes("You didn't manage to pick the lock this time, would you like another go?");
						delay(3);
						int anotherGo = multi(player, "Yeah, I'll give it another go.", "I'll try something different instead.");
						if (anotherGo == 0) {
							mes("You use some nearby bits of wood and wire to try and pick the lock.");
							delay(3);
							int attempt2 = DataConversions.random(0, 1);
							//failed attempt 2
							if (attempt2 == 0) {
								mes("You fail!");
								delay(3);
								Npc mercenary = ifnearvisnpc(player, NpcId.MERCENARY.id(), 15);
								if (mercenary != null) {
									mes("A nearby guard spots you!");
									delay(3);
									npcsay(player, npc, "Oh oh!");
									npcsay(player, mercenary, "Oi, what are you two doing?");
									mercenary.setChasing(player);
									mes("The Guards search you!");
									delay(3);
									mes("More guards rush to catch you.");
									delay(3);
									mes("You are roughed up a bit by the guards as you're manhandlded to a cell.");
									delay(3);
									npcsay(player, mercenary, "Into the cell you go! I hope this teaches you a lesson.");
									player.teleport(89, 801);
								}
							} else {
								succeedFreeSlave(player, npc);
							}
						} else if (anotherGo == 1) {
							mes("You decide to try something else.");
							delay(3);
							npcsay(player, npc, "Are you givin in already?");
							say(player, npc, "I just want to try something else.");
							npcsay(player, npc, "Ok, if you want to try again, let me know.");
						}
					} else {
						succeedFreeSlave(player, npc);
					}

					break;
			}
		}
	}

	private void succeedFreeSlave(final Player player, final Npc npc) {
		mes("You hear a satisfying 'click' as you tumble the lock mechanism.");
		delay(3);
		npcsay(player, npc, "Great! You did it!");

		necessaryStuffSlave(player, npc);
	}

	private void necessaryStuffSlave(final Player player, final Npc npc) {
		if (player.getCarriedItems().hasCatalogID(ItemId.DESERT_SHIRT.id(), Optional.of(false))
			&& player.getCarriedItems().hasCatalogID(ItemId.DESERT_ROBE.id(), Optional.of(false))
			&& player.getCarriedItems().hasCatalogID(ItemId.DESERT_BOOTS.id(), Optional.of(false))) {
			npcsay(player, npc, "Great! You have the Desert Clothes!");
			mes("The slave starts getting undressed right in front of you.");
			delay(3);
			npcsay(player, npc, "Ok, here's the clothes, I won't need them anymore.");
			mes("The slave gives you his dirty, flea infested robe.");
			delay(3);
			mes("The slave gives you his muddy, sweat soaked shirt.");
			delay(3);
			player.getCarriedItems().remove(new Item(ItemId.DESERT_ROBE.id()));
			player.getCarriedItems().getInventory().add(new Item(ItemId.SLAVES_ROBE_BOTTOM.id()));
			player.getCarriedItems().remove(new Item(ItemId.DESERT_SHIRT.id()));
			player.getCarriedItems().getInventory().add(new Item(ItemId.SLAVES_ROBE_TOP.id()));
			player.getCarriedItems().remove(new Item(ItemId.DESERT_BOOTS.id()));
			Npc newSlave = changenpc(npc, NpcId.ESCAPING_MINING_SLAVE.id(), true);
			delay(2);
			delayedReturnSlave(player, newSlave);
			npcsay(player, newSlave, "Right, I'm off! Good luck!");
			say(player, newSlave, "Yeah, good luck to you too!");
			if (player.getQuestStage(this) == 2 || player.getQuestStage(this) == 3)
				player.updateQuestStage(this, 4);
			return;
		}

		if (!player.getCarriedItems().hasCatalogID(ItemId.DESERT_SHIRT.id(), Optional.of(false))
			&& !player.getCarriedItems().hasCatalogID(ItemId.DESERT_ROBE.id(), Optional.of(false))
			&& !player.getCarriedItems().hasCatalogID(ItemId.DESERT_BOOTS.id(), Optional.of(false))) {
			npcsay(player, npc, "I need a desert shirt, robe and boots if you want these clothes off me.");
		} else if (!player.getCarriedItems().hasCatalogID(ItemId.DESERT_SHIRT.id(), Optional.of(false))
			&& !player.getCarriedItems().hasCatalogID(ItemId.DESERT_ROBE.id(), Optional.of(false))
			&& player.getCarriedItems().hasCatalogID(ItemId.DESERT_BOOTS.id(), Optional.of(false))) {
			npcsay(player, npc, "I need desert robe and shirt if you want these clothes off me.");
		} else if (!player.getCarriedItems().hasCatalogID(ItemId.DESERT_SHIRT.id(), Optional.of(false))
			&& player.getCarriedItems().hasCatalogID(ItemId.DESERT_ROBE.id(), Optional.of(false))
			&& !player.getCarriedItems().hasCatalogID(ItemId.DESERT_BOOTS.id(), Optional.of(false))) {
			npcsay(player, npc, "I need a desert shirt and boots if you want these clothes off me.");
		} else if (player.getCarriedItems().hasCatalogID(ItemId.DESERT_SHIRT.id(), Optional.of(false))
			&& !player.getCarriedItems().hasCatalogID(ItemId.DESERT_ROBE.id(), Optional.of(false))
			&& !player.getCarriedItems().hasCatalogID(ItemId.DESERT_BOOTS.id(), Optional.of(false))) {
			npcsay(player, npc, "I need desert robe and boots if you want these clothes off me.");
		} else if (!player.getCarriedItems().hasCatalogID(ItemId.DESERT_SHIRT.id(), Optional.of(false))
			&& player.getCarriedItems().hasCatalogID(ItemId.DESERT_ROBE.id(), Optional.of(false))
			&& player.getCarriedItems().hasCatalogID(ItemId.DESERT_BOOTS.id(), Optional.of(false))) {
			npcsay(player, npc, "I need a desert shirt if you want these clothes off me.");
		} else if (player.getCarriedItems().hasCatalogID(ItemId.DESERT_SHIRT.id(), Optional.of(false))
			&& !player.getCarriedItems().hasCatalogID(ItemId.DESERT_ROBE.id(), Optional.of(false))
			&& player.getCarriedItems().hasCatalogID(ItemId.DESERT_BOOTS.id(), Optional.of(false))) {
			npcsay(player, npc, "I need desert robe if you want these clothes off me.");
		} else if (player.getCarriedItems().hasCatalogID(ItemId.DESERT_SHIRT.id(), Optional.of(false))
			&& player.getCarriedItems().hasCatalogID(ItemId.DESERT_ROBE.id(), Optional.of(false))
			&& !player.getCarriedItems().hasCatalogID(ItemId.DESERT_BOOTS.id(), Optional.of(false))) {
			npcsay(player, npc, "I need desert boots if you want these clothes off me.");
		}

		if (player.getQuestStage(this) == 2)
			player.updateQuestStage(this, 3);
	}

	private void escapingSlaveDialogue(Player player, Npc n) {
		npcsay(player, n, "Hey, I'm trying to escape!",
			"You're attracting too much attention to me!",
			"See ya!");
	}

	private void mercenaryInsideDialogue(final Player player, final Npc npc, final int cID) {
		if (npc.getID() == NpcId.MERCENARY_ESCAPEGATES.id()) {
			if (cID == -1) {
				if (player.getLocation().inTouristTrapCave()) {
					if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.SLAVES_ROBE_BOTTOM.id())
						|| !player.getCarriedItems().getEquipment().hasEquipped(ItemId.SLAVES_ROBE_TOP.id())) {
						player.message("This guard looks as if he's been down here a while.");
						npcsay(player, npc, "Hey, you're no slave!",
							"What are you doing down here?");
						npc.setChasing(player);
						if (player.getQuestStage(this) != -1) {
							mes("More guards rush to catch you.");
							delay(3);
							mes("You are roughed up a bit by the guards as you're manhandlded to a cell.");
							delay(3);
							npcsay(player, npc, "Into the cell you go! I hope this teaches you a lesson.");
							player.teleport(89, 801);
						}
						return;
					}
					if (player.getQuestStage(this) >= 9 || player.getQuestStage(this) == -1) {
						player.message("This guard looks as if he's been down here a while.");
						npcsay(player, npc, "That pineapple was just delicious, many thanks.",
							"I don't suppose you could get me another?");
						player.message("The guard looks at you pleadingly.");
						return;
					}
					player.message("This guard looks as if he's been down here a while.");
					npcsay(player, npc, "Yeah, what do you want?");
					int mama = multi(player, npc,
						"Er nothing really.",
						"I'd like to mine in a different area.");
					if (mama == 0) {
						npcsay(player, npc, "Ok...so move along and get on with your work.");
					} else if (mama == 1) {
						npcsay(player, npc, "Oh, so you want to work in another area of the mine heh?");
						mes("The guard seems quite pleased with his rhetorical question.");
						delay(3);
						npcsay(player, npc, "Well, I can understand that, a change is as good as a rest they say.");
						int menu = multi(player, npc,
							"Huh, fat chance of a rest for me.",
							"Yes sir, you're quite right sir.");
						if (menu == 0) {
							npcsay(player, npc, "You miserable whelp!",
								"Get back to work!");
							player.damage(2);
							player.message("The guard cuffs you around head.");
						} else if (menu == 1) {
							npcsay(player, npc, "Of course I'm right...",
								"And what goes around comes around as they say.",
								"And it's been absolutely ages since I've had anything different to eat.",
								"What I wouldn't give for some ripe and juicy pineapple for a change.",
								"And those Tenti's have the best pineapple in this entire area.");
							player.message("The guard winks at you.");
							npcsay(player, npc, "I'm sure you get my meaning...");
							int pus = multi(player, npc,
								"How am I going to get some pineapples around here?",
								"Yes sir, we understand each other perfectly.",
								"What are the 'Tenti's'?");
							if (pus == 0) {
								mercenaryInsideDialogue(player, npc, MercenaryInside.PINEAPPLES);
							} else if (pus == 1) {
								mercenaryInsideDialogue(player, npc, MercenaryInside.UNDERSTAND);
							} else if (pus == 2) {
								npcsay(player, npc, "Well, you really don't come from around here do you?",
									"The tenti's are what we call the nomadic people west of here.",
									"They live in tents, so we call them the tenti's",
									"They have great pineapples!",
									"I'm sure you get my meaning...");
								int pus2 = multi(player, npc,
									"How am I going to get some pineapples around here?",
									"Yes sir, we understand each other perfectly.");
								if (pus2 == 0) {
									mercenaryInsideDialogue(player, npc, MercenaryInside.PINEAPPLES);
								} else if (pus2 == 1) {
									mercenaryInsideDialogue(player, npc, MercenaryInside.UNDERSTAND);
								}
							}
						}
					}
					return;
				}
				player.message("This guard looks as if he's been in the sun for a while.");
				npcsay(player, npc, "Move along now...");
			}
			switch (cID) {
				case MercenaryInside.PINEAPPLES:
					if (player.getQuestStage(this) == 4) {
						player.updateQuestStage(this, 5);
					}
					npcsay(player, npc, "Well, that's not my problem is it?",
						"Also, I know that you slaves trade your items down here.",
						"I'm sure that if you're resourceful enough, you'll come up with the goods.",
						"Now, get along and do some work, before we're both in for it.");
					break;
				case MercenaryInside.UNDERSTAND:
					if (player.getQuestStage(this) == 4) {
						player.updateQuestStage(this, 5);
					}
					npcsay(player, npc, "Ok, good then.");
					player.message("The guard moves back to his post and winks at you knowingly.");
					break;

			}
		}
	}

	private void bedabinNomadDialogue(final Player player, final Npc npc, final int cID) {
		if (npc.getID() == NpcId.BEDABIN_NOMAD.id()) {
			if (cID == -1) {
				npcsay(player, npc, "Hello Effendi!",
					"How can I help you?");
				int menu = multi(player, npc, false, //do not send over
					"What is this place?",
					"Where is the Shantay Pass?",
					"Buy a jug of water - 5 Gold Pieces.",
					"Buy a full waterskin - 20 Gold Pieces.",
					"Buy a bucket of water - 20 Gold Pieces.");
				if (menu == 0) {
					say(player, npc, "What is this place?");
					bedabinNomadDialogue(player, npc, BedabinNomad.PLACE);
				} else if (menu == 1) {
					say(player, npc, "Where is the Shantay Pass.");
					bedabinNomadDialogue(player, npc, BedabinNomad.SHANTAYPASS);
				} else if (menu == 2) {
					say(player, npc, "Buy a jug of water - 5 Gold Pieces.");
					bedabinNomadDialogue(player, npc, BedabinNomad.JUGOFWATER);
				} else if (menu == 3) {
					say(player, npc, "Buy a full waterskin - 25 Gold Pieces.");
					bedabinNomadDialogue(player, npc, BedabinNomad.FULLWATERSKIN);
				} else if (menu == 4) {
					say(player, npc, "Buy a bucket of water - 20 Gold Pieces.");
					bedabinNomadDialogue(player, npc, BedabinNomad.BUCKETOFWATER);
				}
			}
			switch (cID) {
				case BedabinNomad.BUCKETOFWATER:
					if (ifheld(player, ItemId.COINS.id(), 20)) {
						mes("You hand over 20 gold pieces.");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 20));
						npcsay(player, npc, "Very well Effendi!");
						mes("You recieve a bucket of water.");
						delay(3);
						give(player, ItemId.BUCKET_OF_WATER.id(), 1);
					} else {
						mes("Sorry Effendi, you don't seem to have the money.");
						delay(3);
					}
					npcsay(player, npc, "How can I help you?");
					int newMenu = multi(player, npc,
						"What is this place?",
						"Where is the Shantay Pass.",
						"Buy a jug of water - 5 Gold Pieces.",
						"Buy a full waterskin - 25 Gold Pieces.",
						"Buy a bucket of water - 20 Gold Pieces.");
					if (newMenu == 0) {
						bedabinNomadDialogue(player, npc, BedabinNomad.PLACE);
					} else if (newMenu == 1) {
						bedabinNomadDialogue(player, npc, BedabinNomad.SHANTAYPASS);
					} else if (newMenu == 2) {
						bedabinNomadDialogue(player, npc, BedabinNomad.JUGOFWATER);
					} else if (newMenu == 3) {
						bedabinNomadDialogue(player, npc, BedabinNomad.FULLWATERSKIN);
					} else if (newMenu == 4) {
						bedabinNomadDialogue(player, npc, BedabinNomad.BUCKETOFWATER);
					}

					break;
				case BedabinNomad.FULLWATERSKIN:
					if (ifheld(player, ItemId.COINS.id(), 25)) {
						mes("You hand over 25 gold pieces.");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 25));
						npcsay(player, npc, "Very well Effendi!");
						mes("You recieve a full waterskin.");
						delay(3);
						give(player, ItemId.FULL_WATER_SKIN.id(), 1);
					} else {
						mes("Sorry Effendi, you don't seem to have the money.");
						delay(3);
					}
					npcsay(player, npc, "How can I help you?");
					int option = multi(player, npc,
						"What is this place?",
						"Where is the Shantay Pass.",
						"Buy a jug of water - 5 Gold Pieces.",
						"Buy a full waterskin - 25 Gold Pieces.",
						"Buy a bucket of water - 20 Gold Pieces.");
					if (option == 0) {
						bedabinNomadDialogue(player, npc, BedabinNomad.PLACE);
					} else if (option == 1) {
						bedabinNomadDialogue(player, npc, BedabinNomad.SHANTAYPASS);
					} else if (option == 2) {
						bedabinNomadDialogue(player, npc, BedabinNomad.JUGOFWATER);
					} else if (option == 3) {
						bedabinNomadDialogue(player, npc, BedabinNomad.FULLWATERSKIN);
					} else if (option == 4) {
						bedabinNomadDialogue(player, npc, BedabinNomad.BUCKETOFWATER);
					}
					break;
				case BedabinNomad.JUGOFWATER:
					if (ifheld(player, ItemId.COINS.id(), 5)) {
						mes("You hand over 5 gold pieces.");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
						npcsay(player, npc, "Very well Effendi!");
						mes("You recieve a jug full or water.");
						delay(3);
						give(player, ItemId.JUG_OF_WATER.id(), 1);
					} else {
						mes("Sorry Effendi, you don't seem to have the money.");
						delay(3);
					}
					npcsay(player, npc, "How can I help you?");
					int optiony = multi(player, npc,
						"What is this place?",
						"Where is the Shantay Pass.",
						"Buy a jug of water - 5 Gold Pieces.",
						"Buy a full waterskin - 25 Gold Pieces.",
						"Buy a bucket of water - 20 Gold Pieces.");
					if (optiony == 0) {
						bedabinNomadDialogue(player, npc, BedabinNomad.PLACE);
					} else if (optiony == 1) {
						bedabinNomadDialogue(player, npc, BedabinNomad.SHANTAYPASS);
					} else if (optiony == 2) {
						bedabinNomadDialogue(player, npc, BedabinNomad.JUGOFWATER);
					} else if (optiony == 3) {
						bedabinNomadDialogue(player, npc, BedabinNomad.FULLWATERSKIN);
					} else if (optiony == 4) {
						bedabinNomadDialogue(player, npc, BedabinNomad.BUCKETOFWATER);
					}
					break;
				case BedabinNomad.PLACE:
					npcsay(player, npc, "This is the camp of the Bedabin.",
						"Talk to our leader, Al Shabim, he'll be happy to chat.");
					player.message("We can sell you very reasonably priced water...");
					npcsay(player, npc, "How can I help you?");
					int opt = multi(player, npc,
						"Where is the Shantay Pass.",
						"Buy a jug of water - 5 Gold Pieces.",
						"Buy a full waterskin - 25 Gold Pieces.",
						"Buy a bucket of water - 20 Gold Pieces.");
					if (opt == 0) {
						bedabinNomadDialogue(player, npc, BedabinNomad.SHANTAYPASS);
					} else if (opt == 1) {
						bedabinNomadDialogue(player, npc, BedabinNomad.JUGOFWATER);
					} else if (opt == 2) {
						bedabinNomadDialogue(player, npc, BedabinNomad.FULLWATERSKIN);
					} else if (opt == 3) {
						bedabinNomadDialogue(player, npc, BedabinNomad.BUCKETOFWATER);
					}

					break;
				case BedabinNomad.SHANTAYPASS:
					npcsay(player, npc, "It is North East of here effendi, across the trackless desert.",
						"It will be a thirsty trip, can I interest you in a drink?",
						"How can I help you?");
					int options = multi(player, npc,
						"Buy a jug of water - 5 Gold Pieces.",
						"What is this place?",
						"Buy a full waterskin - 25 Gold Pieces.",
						"Buy a bucket of water - 20 Gold Pieces.");
					if (options == 0) {
						bedabinNomadDialogue(player, npc, BedabinNomad.JUGOFWATER);
					} else if (options == 1) {
						bedabinNomadDialogue(player, npc, BedabinNomad.PLACE);
					} else if (options == 2) {
						bedabinNomadDialogue(player, npc, BedabinNomad.FULLWATERSKIN);
					} else if (options == 3) {
						bedabinNomadDialogue(player, npc, BedabinNomad.BUCKETOFWATER);
					}
					break;
			}
		}
	}

	private static void alShabimDialogue(final Player player, final Npc npc, final int cID) {
		if (npc.getID() == NpcId.AL_SHABIM.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(Quests.TOURIST_TRAP)) {
					case 0:
						npcsay(player, npc, "Hello Effendi!",
							"I am Al Shabim, greetings on behalf of the Bedabin nomads.");
						int menu = multi(player, npc,
							"What is this place?",
							"Goodbye!");
						if (menu == 0) {
							alShabimDialogue(player, npc, AlShabim.WHATISTHISPLACE);
						} else if (menu == 1) {
							npcsay(player, npc, "Very well, good day Effendi!");
						}
						break;
					case 1:
					case 2:
					case 3:
					case 4:
						npcsay(player, npc, "Hello Effendi!",
							"I am Al Shabim, greetings on behalf of the Bedabin nomads.");
						int menuO;
						if (player.getCache().hasKey("find_al_bhasim") &&
							!player.getCarriedItems().hasCatalogID(ItemId.METAL_KEY.id(), Optional.of(false))) {
							menuO = multi(player, npc,
								"I am looking for Al Zaba Bhasim.",
								"What is this place?");
							if (menuO == 0) {
								npcsay(player, npc, "Huh! You have been talking to the guards at the mining camp.",
									"Or worse, that cowardly mercenary captain.",
									"Al Zaba Bhasim does not exist, he is a figment of their imagination!",
									"Go back and tell this captain that if he wants to find this man",
									"he should search for him personally.",
									"See how much of his own time he would like to waste.");
							} else if (menuO == 1) {
								alShabimDialogue(player, npc, AlShabim.WHATISTHISPLACE);
							}
						} else {
							menuO = multi(player, npc,
								"What is this place?",
								"Goodbye!");
							if (menuO == 0) {
								alShabimDialogue(player, npc, AlShabim.WHATISTHISPLACE);
							} else if (menuO == 1) {
								npcsay(player, npc, "Very well, good day Effendi!");
							}
						}
						break;
					case 5:
						npcsay(player, npc, "Hello Effendi!",
							"I am Al Shabim, greetings on behalf of the Bedabin nomads.");
						int option = multi(player, npc,
							"I am looking for a pineapple.",
							"What is this place?");
						if (option == 0) {
							npcsay(player, npc, "Oh yes, well that is interesting.",
								"Our sweet pineapples are renowned throughout the whole of Kharid !",
								"And I'll give you one if you do me a favour?");
							say(player, npc, "Yes ?");
							npcsay(player, npc, "Captain Siad at the mining camp is holding some secret information.",
								"It is very important to us and we would like you to get it for us.",
								"It gives details of an interesting, yet ancient weapon.",
								"We would gladly share this information with you.",
								"All you have to do is gain access to his private room upstairs.",
								"We have a key for the chest that contains this information.",
								"Are you interested in our deal?");
							int opt = multi(player, npc,
								"Yes, I'm interested.",
								"Not at the moment.");
							if (opt == 0) {
								npcsay(player, npc, "That's great Effendi!",
									"Here is a copy of the key that should give you access to the chest.",
									"Bring us back the plans inside the chest, they should be sealed.",
									"All haste to you Effendi!");
								give(player, ItemId.BEDOBIN_COPY_KEY.id(), 1);
								player.updateQuestStage(Quests.TOURIST_TRAP, 6);
							} else if (opt == 1) {
								npcsay(player, npc, "Very well Effendi!");
							}
						} else if (option == 1) {
							alShabimDialogue(player, npc, AlShabim.WHATISTHISPLACE);
						}
						break;
					case 6:
					case 7:
						npcsay(player, npc, "Hello Effendi!");
						if (player.getCarriedItems().hasCatalogID(ItemId.PROTOTYPE_THROWING_DART.id(), Optional.of(false))) {
							alShabimDialogue(player, npc, AlShabim.MADE_WEAPON);
						} else if (player.getCarriedItems().hasCatalogID(ItemId.TECHNICAL_PLANS.id(), Optional.of(false))
							&& !player.getCarriedItems().hasCatalogID(ItemId.PROTOTYPE_THROWING_DART.id(), Optional.of(false))) {
							alShabimDialogue(player, npc, AlShabim.HAVE_PLANS);
						} else if (player.getCarriedItems().hasCatalogID(ItemId.BEDOBIN_COPY_KEY.id(), Optional.of(false))
							&& !player.getCarriedItems().hasCatalogID(ItemId.TECHNICAL_PLANS.id(), Optional.of(false))) {
							npcsay(player, npc, "How are things going Effendi?");
							int dede = multi(player, npc,
								"Very well thanks!",
								"Not so good actually!",
								"What is this place?",
								"Goodbye!");
							if (dede == 0) {
								if (!player.getCache().hasKey("tech_plans")) {
									npcsay(player, npc, "Well, hurry along and get those plans for me.");
								} else {
									npcsay(player, npc, "I really need those plans!");
								}
							} else if (dede == 1) {
								if (!player.getCache().hasKey("tech_plans")) {
									npcsay(player, npc, "Well, first you need to get those plans from Captain Siad.");
								} else {
									npcsay(player, npc, "Bring me the plans from Captain Siad's office...they're in a chest.");
								}
							} else if (dede == 2) {
								alShabimDialogue(player, npc, AlShabim.WHATISTHISPLACE);
							} else if (dede == 3) {
								npcsay(player, npc, "Very well, good day Effendi!");
							}
						} else if (player.getCache().hasKey("tech_plans")) {
							int keke = multi(player, npc,
								"I've lost the key and the plans!",
								"What is this place?",
								"Goodbye!");
							if (keke == 0) {
								npcsay(player, npc, "How very careless of you!");
								player.message("Al Shabim thinks for a moment.");
								npcsay(player, npc, "The Captain may have some new plans drawn up.",
									"Go back and see if you can collect them.",
									"Here is the key you'll need for the chest!");
								player.message("Al Shabim gives you another key.");
								give(player, ItemId.BEDOBIN_COPY_KEY.id(), 1);
							} else if (keke == 1) {
								alShabimDialogue(player, npc, AlShabim.WHATISTHISPLACE);
							} else if (keke == 2) {
								npcsay(player, npc, "Very well, good day Effendi!");
							}
						} else {
							int kaka = multi(player, npc,
								"I've lost the key!",
								"What is this place?",
								"Goodbye!");
							if (kaka == 0) {
								npcsay(player, npc, "How very careless of you!",
									"Here is another key, don't lose it this time !");
								player.message("Al Shabim gives you another key.");
								give(player, ItemId.BEDOBIN_COPY_KEY.id(), 1);
							} else if (kaka == 1) {
								alShabimDialogue(player, npc, AlShabim.WHATISTHISPLACE);
							} else if (kaka == 2) {
								npcsay(player, npc, "Very well, good day Effendi!");
							}
						}
						break;
					case 8:
					case 9:
					case 10:
					case -1:
						if (player.getCarriedItems().hasCatalogID(ItemId.PROTOTYPE_THROWING_DART.id(), Optional.of(false))) {
							npcsay(player, npc, "Hello Effendi!",
								"Wonderful, I see you have made the new weapon!",
								"Where did you get this from Effendi!",
								"I'll have to confiscate this for your own safety!");
							player.getCarriedItems().remove(new Item(ItemId.PROTOTYPE_THROWING_DART.id()));
							return;
						}
						if (player.getCarriedItems().hasCatalogID(ItemId.TECHNICAL_PLANS.id(), Optional.of(false))) {
							npcsay(player, npc, "Hello Effendi!");
							alShabimDialogue(player, npc, AlShabim.HAVE_PLANS);
							return;
						}
						if (player.getQuestStage(Quests.TOURIST_TRAP) == 8) {
							npcsay(player, npc, "Hello Effendi!",
								"Many thanks with your help previously Effendi!");
							if (player.getCarriedItems().hasCatalogID(ItemId.TENTI_PINEAPPLE.id(), Optional.of(false))) {
								int mopt = multi(player, npc,
									"What is this place?",
									"Goodbye!");
								if (mopt == 0) {
									alShabimDialogue(player, npc, AlShabim.WHATISTHISPLACE);
								} else if (mopt == 1) {
									npcsay(player, npc, "Very well, good day Effendi!");
								}
							} else {
								int mopt = multi(player, npc,
									"I am looking for a pineapple.",
									"What is this place?");
								if (mopt == 0) {
									npcsay(player, npc, "Here is another pineapple, try not to lose this one.");
									player.message("Al Shabim gives you another pineapple.");
									give(player, ItemId.TENTI_PINEAPPLE.id(), 1);
								} else if (mopt == 1) {
									alShabimDialogue(player, npc, AlShabim.WHATISTHISPLACE);
								}
							}
						} else {
							npcsay(player, npc, "Hello Effendi!",
								"Many thanks with your help previously Effendi!",
								"I am Al Shabim, greetings on behalf of the Bedabin nomads.");
							int mopt = multi(player, npc,
								"What is this place?",
								"Goodbye!");
							if (mopt == 0) {
								alShabimDialogue(player, npc, AlShabim.WHATISTHISPLACE);
							} else if (mopt == 1) {
								npcsay(player, npc, "Very well, good day Effendi!");
							}
						}
						break;
				}
			}
			switch (cID) {
				case AlShabim.WHATISTHISPLACE:
					npcsay(player, npc, "This is the home of the Bedabin, ",
						"We're a peaceful tribe of desert dwellers.",
						"Some idiots call us 'Tenti's', a childish name borne of ignorance.",
						"We're renowned for surviving in the harshest desert climate.",
						"We also grow the 'Bedabin ambrosia.'...",
						"A pineapple of such delicious sumptiousness that it defies description.",
						"Take a look around our camp if you like!");
					int menu = multi(player, npc,
						"Ok Thanks!",
						"What is there to do around here?");
					if (menu == 0) {
						npcsay(player, npc, "Good day Effendi!");
					} else if (menu == 1) {
						npcsay(player, npc, "Well, we are all very busy most of the time tending to the pineapples.",
							"They are grown in a secret location.",
							"To stop thieves from raiding our most precious prize.");
					}
					break;
				case AlShabim.HAVE_PLANS:
					npcsay(player, npc, "Aha! I see you have the plans.",
						"This is great!",
						"However, these plans do indeed look very technical",
						"My people have further need of your skills.",
						"If you can help us to manufacture this item,",
						"we will share it's secret with you.",
						"Does this deal interest you effendi?");
					int tati = multi(player, npc,
						"Yes, I'm very interested.",
						"No, sorry.");
					if (tati == 0) {
						if (player.getCarriedItems().hasCatalogID(ItemId.BRONZE_BAR.id(), Optional.of(false))
							&& ifheld(player, ItemId.FEATHER.id(), 10)) {
							npcsay(player, npc, "Aha! I see you have the items we need!",
								"Are you still willing to help make the weapon?");
							int make = multi(player, npc,
								"Yes, I'm kind of curious.",
								"No,sorry.");
							if (make == 0) {
								npcsay(player, npc, "Ok Effendi, you need to follow the plans.",
									"You will need some special tools for this...",
									"There is a forge in the other tent.",
									"You have my permision to use it, but show the plans to the guard.",
									"You have the plans and the all the items needed, ",
									"You should be able to complete the item on your own.",
									"Please bring me the item when it is finished.");
								if (player.getQuestStage(Quests.TOURIST_TRAP) == 6) {
									player.updateQuestStage(Quests.TOURIST_TRAP, 7);
								}
							} else if (make == 1) {
								npcsay(player, npc, "As you wish effendi!",
									"Come back if you change your mind!");
							}
						} else {
							npcsay(player, npc, "Great, we need the following items.",
								"A bar of pure bronze and 10 feathers.",
								"Bring them to me and we'll continue to make the item.");
						}

					} else if (tati == 1) {
						npcsay(player, npc, "As you wish effendi!",
							"Come back if you change your mind!");
					}
					break;
				case AlShabim.MADE_WEAPON:
					npcsay(player, npc, "Wonderful, I see you have made the new weapon!");
					mes("You show Al Shabim the prototype dart.");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.PROTOTYPE_THROWING_DART.id()));
					npcsay(player, npc, "This is truly fantastic Effendi!");
					if (player.getCarriedItems().hasCatalogID(ItemId.TECHNICAL_PLANS.id(), Optional.of(false))) {
						npcsay(player, npc, "We will take the technical plans for the weapon as well.");
						player.getCarriedItems().remove(new Item(ItemId.TECHNICAL_PLANS.id()));
						mes("You hand over the technical plans for the weapon.");
						delay(3);
					}
					npcsay(player, npc, "We are forever grateful for this gift.",
						"My advisors have discovered some secrets which we will share with you.");
					mes("Al Shabim's advisors show you some advanced techniques for making the new weapon.");
					delay(3);
					npcsay(player, npc, "Oh, and here is your pineapple!");
					give(player, ItemId.TENTI_PINEAPPLE.id(), 1);
					npcsay(player, npc, "Please accept this selection of six bronze throwing darts",
						"as a token of our appreciation.");
					give(player, ItemId.BRONZE_THROWING_DART.id(), 6);
					if (player.getCarriedItems().hasCatalogID(ItemId.BEDOBIN_COPY_KEY.id(), Optional.of(false))) {
						npcsay(player, npc, "I'll take that key off your hands as well effendi!");
						player.getCarriedItems().remove(new Item(ItemId.BEDOBIN_COPY_KEY.id()));
						npcsay(player, npc, "Many thanks!");
					}
					player.message("");
					player.message("********************************************************************");
					player.message("*** You can now make a new weapon type: Throwing dart. ***");
					player.message("********************************************************************");
					player.updateQuestStage(Quests.TOURIST_TRAP, 8); //>= 8 or -1 for throwing darts.
					break;
			}
		}
	}

	private void captainSiadDialogue(final Player player, final Npc npc, final int cID, final GameObject obj) {
		// USED FOR CHEST AND TALK-TO
		if (npc.getID() == NpcId.CAPTAIN_SIAD.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(this)) {
					case 0:
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
					case -1:
						if (obj != null) {
							mes("The captains spots you before you manage to open the chest...");
							delay(3);
						} else {
							mes("The captain looks up from his work as you address him.");
							delay(3);
						}
						if (player.getCarriedItems().hasCatalogID(ItemId.TECHNICAL_PLANS.id(), Optional.of(false)) || player.getQuestStage(this) >= 8 || player.getQuestStage(this) == -1) {
							npcsay(player, npc, "I don't have time to talk to you.",
								"Move along please!");
							return;
						}
						npcsay(player, npc, "What are you doing in here?");
						int menu = multi(player, npc,
							"I wanted to have a chat?",
							"What's it got to do with you?",
							"Prepare to die!",
							"All the slaves have broken free!",
							"Fire!Fire!");
						if (menu == 0) {
							npcsay(player, npc, "You don't belong in here, get out!");
							int m = multi(player, npc,
								"But I just need two minutes of your time?",
								"Prepare to die!",
								"All the slaves have broken free!",
								"Fire!Fire!",
								"You seem to have a lot of books!");
							if (m == 0) {
								captainSiadDialogue(player, npc, Siad.TWOMINUTES, null);
							} else if (m == 1) {
								captainSiadDialogue(player, npc, Siad.PREPARETODIE, null);
							} else if (m == 2) {
								captainSiadDialogue(player, npc, Siad.SLAVESBROKENFREE, null);
							} else if (m == 3) {
								captainSiadDialogue(player, npc, Siad.FIREFIRE, null);
							} else if (m == 4) {
								captainSiadDialogue(player, npc, Siad.BOOKS, null);
							}
						} else if (menu == 1) {
							npcsay(player, npc, "This happens to be my office.",
								"Now explain yourself before I run you through!");
							int keke = multi(player, npc,
								"The guard downstairs said you were lonely.",
								"I need to service your chest.");
							if (keke == 0) {
								captainSiadDialogue(player, npc, Siad.LONELY, null);
							} else if (keke == 1) {
								captainSiadDialogue(player, npc, Siad.SERVICE, null);
							}

						} else if (menu == 2) {
							captainSiadDialogue(player, npc, Siad.PREPARETODIE, null);
						} else if (menu == 3) {
							captainSiadDialogue(player, npc, Siad.SLAVESBROKENFREE, null);
						} else if (menu == 4) {
							captainSiadDialogue(player, npc, Siad.FIREFIRE, null);
						}
						break;
				}
			}
			switch (cID) {
				case Siad.PREPARETODIE:
					npcsay(player, npc, "I'll teach you a lesson!",
						"Guards! Guards!");
					captainSiadDialogue(player, npc, Siad.PUNISHED, null);
					break;
				case Siad.PUNISHED:
					mes("The Guards search you!");
					delay(3);
					int rand = DataConversions.random(0, 3);
					if (player.getCarriedItems().hasCatalogID(ItemId.CELL_DOOR_KEY.id(), Optional.of(false)) && rand == 0) {
						player.message("The guards find the cell door key and remove it!");
						player.getCarriedItems().remove(new Item(ItemId.CELL_DOOR_KEY.id()));
					}
					if (player.getCarriedItems().hasCatalogID(ItemId.METAL_KEY.id(), Optional.of(false)) && rand == 1) {
						player.message("The guards find the main gate key and remove it!");
						player.getCarriedItems().remove(new Item(ItemId.METAL_KEY.id()));
					}
					mes("Some guards rush to help the captain.");
					delay(3);
					mes("You are roughed up a bit by the guards as you're manhandlded into a cell.");
					delay(3);
					player.damage(7);
					mes("@yel@Guards: Into the cell you go! I hope this teaches you a lesson.");
					delay(3);
					player.teleport(89, 801);
					break;
				case Siad.TWOMINUTES:
					npcsay(player, npc, "Well, ok, but very quickly.",
						"I am a very busy person you know!");
					int menu = multi(player, npc,
						"Well, er...erm, I err....",
						"Oh my, a dragon just flew straight past your window!");
					if (menu == 0) {
						captainSiadDialogue(player, npc, Siad.ERM, null);
					} else if (menu == 1) {
						captainSiadDialogue(player, npc, Siad.DRAGON, null);
					}
					break;
				case Siad.SLAVESBROKENFREE:
					if (!succeedRate(player)) {
						npcsay(player, npc, "Don't talk rubbish, the warning siren isn't sounding.",
							"Now state your business before I have you thrown out.");
						int gay = multi(player, npc,
							"The guard downstairs said you were lonely.",
							"I need to service your chest.");
						if (gay == 0) {
							captainSiadDialogue(player, npc, Siad.LONELY, null);
						} else if (gay == 1) {
							captainSiadDialogue(player, npc, Siad.SERVICE, null);
						}
					} else {
						mes("The captain seems distracted with what you just said.");
						delay(3);
						mes("The captain looks out of the window to see if there are any prisoners escaping.");
						delay(3);
						if (!player.getCache().hasKey("tourist_chest")) {
							player.getCache().store("tourist_chest", true); // if don't have the key, remove the cache.
						}
					}
					break;
				case Siad.ERM:
					npcsay(player, npc, "Come on, spit it out!",
						"Right that's it!",
						"Guards!");
					captainSiadDialogue(player, npc, Siad.PUNISHED, null);
					break;
				case Siad.SERVICE:
					npcsay(player, npc, "You need to what?");
					say(player, npc, "I need to service your chest?");
					npcsay(player, npc, "There's nothing wrong with the chest, it's fine, now get out!");
					int fire = multi(player, npc,
						"I'm here to take your plans, hand them over now or I'll kill you!",
						"Fire!Fire!");
					if (fire == 0) {
						captainSiadDialogue(player, npc, Siad.PLANS, null);
					} else if (fire == 1) {
						captainSiadDialogue(player, npc, Siad.FIREFIRE, null);
					}
					break;
				case Siad.DRAGON:
					if (!succeedRate(player)) {
						npcsay(player, npc, "Really! Where?",
							"I don't see any dragons young man?",
							"Now, please get out of my office, I have work to do.");
						player.message("The Captain goes back to his work.");
					} else {
						captainSiadDialogue(player, npc, Siad.SUCCEED, null);
					}
					break;
				case Siad.LONELY:
					mes("The captain gives you a puzzled look.");
					delay(3);
					npcsay(player, npc, "Well, I most certainly am not lonely!",
						"I'm an incredibly busy man you know!",
						"Now, get to the point, what do you want?");
					int opt = multi(player, npc,
						"Well, er...erm, I err....",
						"I need to service your chest.");
					if (opt == 0) {
						captainSiadDialogue(player, npc, Siad.ERM, null);
					} else if (opt == 1) {
						captainSiadDialogue(player, npc, Siad.SERVICE, null);
					}
					break;
				case Siad.PLANS:
					npcsay(player, npc, "Don't be silly!",
						"I'm going to teach you a lesson!",
						"Guards! Guards!");
					captainSiadDialogue(player, npc, Siad.PUNISHED, null);
					break;
				case Siad.SUCCEED:
					mes("The captain seems distracted with what you just said.");
					delay(3);
					mes("The captain looks out of the window for the dragon.");
					delay(3);
					if (!player.getCache().hasKey("tourist_chest")) {
						player.getCache().store("tourist_chest", true); // if don't have the key, remove the cache.
					}
					break;
				case Siad.FIREFIRE:
					if (!succeedRate(player)) {
						npcsay(player, npc, "Where's the fire?",
							"I don't see any fire?");
						int fireMenu = multi(player, npc,
							"It's down in the lower mines, sound the alarm!",
							"Oh yes,  you're right, they must have put it out!");
						if (fireMenu == 0) {
							npcsay(player, npc, "You go and sound the alarm, I can't see anything wrong with the mine.",
								"Have you seen the fire yourself?");
							int variableF = multi(player, npc,
								"Yes actually!",
								"Er, no, one of the slaves told me.");
							if (variableF == 0) {
								npcsay(player, npc, "Well, why didn't you raise the alarm?");
								int variableG = multi(player, npc,
									"I don't know where the alarm is.",
									"I was so concerned for your safety that I rushed to save you.");
								if (variableG == 0) {
									npcsay(player, npc, "That's the most ridiculous thing I've heard.",
										"Who are you? Where do you come from?",
										"It doesn't matter...");
									mes("The Captain shouts the guards...");
									delay(3);
									npcsay(player, npc, "Guards!",
										"Show this person out!");
									captainSiadDialogue(player, npc, Siad.PUNISHED, null);
								} else if (variableG == 1) {
									npcsay(player, npc, "Well, that's very good of you.",
										"But as you can see, I am very fine and well thanks!",
										"Now, please leave so that I can get back to my work.");
									player.message("The Captain goes back to his desk.");
								}
							} else if (variableF == 1) {
								npcsay(player, npc, "Well...you can't believe them, they're all a bunch of convicts.",
									"Anyway, it doesn't look as if there is a fire down there.",
									"So I'm going to get on with my work.",
									"Please remove yourself from my office.");
								player.message("The Captain goes back to his desk and starts studying.");
							}
						} else if (fireMenu == 1) {
							npcsay(player, npc, "Good, now perhaps you can leave me in peace?",
								"After all I do have some work to do.");
							int er = multi(player, npc,
								"Er, yes Ok then.",
								"Well, er...erm, I err....");
							if (er == 0) {
								npcsay(player, npc, "Good!",
									"Please remove yourself from my office.");
								player.message("The Captain goes back to his desk and starts studying.");
							} else if (er == 1) {
								captainSiadDialogue(player, npc, Siad.ERM, null);
							}
						}
					} else {
						mes("The captain seems distracted with what you just said.");
						delay(3);
						mes("The captain looks out of the window to see if is a fire.");
						delay(3);
						if (!player.getCache().hasKey("tourist_chest")) {
							player.getCache().store("tourist_chest", true); // if don't have the key, remove the cache.
						}
					}
					break;
				case Siad.BOOKS:
					npcsay(player, npc, "Yes, I do. Now please get to the point?");
					int books = 0;
					if (player.getCache().hasKey("sailing")) {
						books = multi(player, npc,
							"How long have you been interested in books?",
							"I could get you some books!",
							"So, you're interested in sailing?");
					} else {
						books = multi(player, npc,
							"How long have you been interested in books?",
							"I could get you some books!");
					}
					if (books == 0) {
						npcsay(player, npc, "Long enough to know when someone is stalling!",
							"Ok, that's it, get out!",
							"Guards!");
						captainSiadDialogue(player, npc, Siad.PUNISHED, null);
					} else if (books == 1) {
						npcsay(player, npc, "Oh, really!",
							"Sorry, not interested!",
							"GUARDS!");
						captainSiadDialogue(player, npc, Siad.PUNISHED, null);
					} else if (books == 2) {
						player.message("The captain's interest seems to perk up.");
						npcsay(player, npc, "Well, yes actually...",
							"It's been a passion of mine for some years...");
						int sail = multi(player, npc,
							"I could tell by the cut of your jib.",
							"Not much sailing to be done around here though?");
						if (sail == 0) {
							npcsay(player, npc, "Oh yes? Really?");
							player.message("The Captain looks flattered.");
							npcsay(player, npc, "Well, you know, I was quite the catch in my day you know!");
							mes("The captain starts rambling on about his days as a salty sea dog.");
							delay(3);
							mes("He looks quite distracted...");
							delay(3);
							if (!player.getCache().hasKey("tourist_chest")) {
								player.getCache().store("tourist_chest", true); // if don't have the key, remove the cache.
							}
						} else if (sail == 1) {
							player.message("The captain frowns slightly...");
							npcsay(player, npc, "Well of course there isn't, we're surrounded by desert.",
								"Now, why are you here exactly?");
							int again = multi(player, npc,
								"Oh my, a dragon just flew straight past your window!",
								"Well, er...erm, I err....");
							if (again == 0) {
								captainSiadDialogue(player, npc, Siad.DRAGON, null);
							} else if (again == 1) {
								captainSiadDialogue(player, npc, Siad.ERM, null);
							}
						}
					}
					break;
			}
		}
	}

	private void anaDialogue(final Player player, final Npc npc, final int cID) {
		if (cID == -1) {
			if (player.getQuestStage(this) == -1) {
				player.message("This slave does not appear interested in talking to you.");
				return;
			}
			if ((!player.getCarriedItems().getEquipment().hasEquipped(ItemId.SLAVES_ROBE_BOTTOM.id())
				|| !player.getCarriedItems().getEquipment().hasEquipped(ItemId.SLAVES_ROBE_TOP.id())) && player.getQuestStage(this) != -1) {
				player.message("A guard notices you and starts running after you.");
				Npc npcN = ifnearvisnpc(player, NpcId.MERCENARY.id(), 10);
				if (npcN == null) {
					npcN = addnpc(player.getWorld(), NpcId.MERCENARY.id(), player.getX(), player.getY(), 60000);
					delay(2);
				}
				npcsay(player, npcN, "Hey! You're no slave!");
				npcN.startCombat(player);
				mes("The Guards search you!");
				delay(3);
				if (player.getCarriedItems().hasCatalogID(ItemId.CELL_DOOR_KEY.id(), Optional.of(false))) {
					mes("The guards find the cell door key and remove it!");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.CELL_DOOR_KEY.id()));
				}
				mes("Some guards rush to help their comrade.");
				delay(3);
				mes("You are roughed up a bit by the guards as you're manhandlded into a cell.");
				delay(3);
				npcsay(player, npcN, "Into the cell you go! I hope this teaches you a lesson.");
				player.teleport(75, 3625);
				return;
			}
			say(player, npc, "Hello!");
			npcsay(player, npc, "Hello there, I don't think I've seen you before.");
			int menu = multi(player, npc,
				"No, I'm new here!",
				"What's your name.");
			if (menu == 0) {
				npcsay(player, npc, "I thought so you know!",
					"How do you like the hospitality down here?",
					"Not exactly Al Kharid Inn style is it?",
					"Well, I guess I'd better get back to work.",
					"Don't want to get into trouble with the guards again.");
				int ooo = multi(player, npc,
					"Do you get into trouble with guards often?",
					"I want to try and get you out of here.");
				if (ooo == 0) {
					npcsay(player, npc, "No, not really, because I'm usually working very hard.",
						"Come to think of it, I'd better get back to work.");
					int often = multi(player, npc,
						"Do you enjoy it down here?",
						"Ok, see ya!");
					if (often == 0) {
						npcsay(player, npc, "Of course not!",
							"I just don't have much choice about it a the moment.");
						int enjoy = multi(player, npc,
							"I want to try and get you out of here.",
							"Do you have any ideas about how we can get out of here?");
						if (enjoy == 0) {
							anaDialogue(player, npc, Ana.TRYGETYOUOUTOFHERE);
						} else if (enjoy == 1) {
							npcsay(player, npc, "Hmmm, not really, I would have tried them already if I did.",
								"The guards seem to live in the compound.",
								"How did you get in there anyway?");
							int mmm = multi(player, npc,
								"I managed to sneak past the guards.",
								"Huh, these guards are rubbish, it was easy to sneak past them!");
							if (mmm == 0) {
								anaDialogue(player, npc, Ana.SNEAKEDPAST);
							} else if (mmm == 1) {
								anaDialogue(player, npc, Ana.GUARDSRUBBISH);
							}
						}
					} else if (often == 1) {
						npcsay(player, npc, "Goodbye and good luck!");
					}
				} else if (ooo == 1) {
					anaDialogue(player, npc, Ana.TRYGETYOUOUTOFHERE);
				}
			} else if (menu == 1) {
				npcsay(player, npc, "My name? Oh, how sweet, my name is Ana,",
					"I come from Al Kharid, thought the desert might be interesting.",
					"What a surprise I got!");
				int opt = multi(player, npc, false, //do not send over
					"What kind of suprise did you get?",
					"Do you want to go back to Al Kharid?");
				if (opt == 0) {
					say(player, npc, "What kind of surpise did you get?");
					npcsay(player, npc, "Well, I was just touring the desert looking for the nomad tribe to west.",
						"And I was set upon by these armoured men.",
						"I think that the guards think I am an escaped prisoner.",
						"They didn't understand that I was exploring the desert as an adventurer.");
				} else if (opt == 1) {
					say(player, npc, "Do you want to go back to Al Kharid?");
					npcsay(player, npc, "Sure, I miss my Mum, her name is Irena and she is probably waiting for me.",
						"how do you propose we get out of here though?",
						"I'm sure you've noticed the many square jawed guards around here.",
						"You look like you can handle yourself, ",
						"but I have my doubts that you can take them all on!");
				}
			}
		}
		switch (cID) {
			case Ana.TRYGETYOUOUTOFHERE:
				npcsay(player, npc, "Wow! You're brave. How do you propose we do that?",
					"In case you hadn't noticed, this place is quite well guarded.");
				int menu = multi(player, npc,
					"We could try to sneak out.",
					"Have you got any suggestions?");
				if (menu == 0) {
					npcsay(player, npc, "That doesn't sound very likely. How did you get in here anway?",
						"Did you deliberately hand yourself over to the guards?",
						"Ha, ha ha ha! Sorry, just kidding.");
					int last = multi(player, npc,
						"I managed to sneak past the guards.",
						"Huh, these guards are rubbish, it was easy to sneak past them!");
					if (last == 0) {
						anaDialogue(player, npc, Ana.SNEAKEDPAST);
					} else if (last == 1) {
						anaDialogue(player, npc, Ana.GUARDSRUBBISH);
					}
				} else if (menu == 1) {
					npcsay(player, npc, "Hmmm, let me think...",
						"Hmmm.",
						"No, sorry...",
						"The only thing that gets out of here is the rock that we mine.",
						"Not even the dead get a decent funeral.",
						"Bodies are just thrown down dissused mine holes.",
						"It's very disrespectful...");
					int gah = multi(player, npc,
						"Ok, I'll check around for another way to try and get out.",
						"How does the rock get out?");
					if (gah == 0) {
						npcsay(player, npc, "Good luck!");
					} else if (gah == 1) {
						npcsay(player, npc, "Well, in this section we mine it, ",
							"Then someone else scoops it into a barrel. ",
							"The barrels are loaded onto a mine cart.",
							"Then they're desposited near the surface lift.",
							"I have no idea where they go from there.",
							"But that's not going to help us, is it?");
						int kaka = multi(player, npc,
							"Maybe? I'll come back to you when I have a plan.",
							"Where would I get one of those barrels from?");
						if (kaka == 0) {
							npcsay(player, npc, "Ok, well, I'm not going anywhere!");
							player.message("Ana nods at a nearby guard!");
							npcsay(player, npc, "Unless he feels generous enough to let me go!");
							player.message("The guard ignores the comment.");
							npcsay(player, npc, "Oh well, I'd better get back to work, you take care!");
						} else if (kaka == 1) {
							npcsay(player, npc, "Well, you would get one from around by the lift area.",
								"But why would you want one of those?");
							int tjatja = multi(player, npc,
								"Er no reason! Just wondering.",
								"You could hide in one of those barrels and I could try to sneak you out!");
							if (tjatja == 0) {
								npcsay(player, npc, "Hmmm, just don't get any funny ideas...",
									"I am not going to get into one of those barrels!",
									"Ok, have you got that?");
								anaDialogue(player, npc, Ana.GOTTHAT);
							} else if (tjatja == 1) {
								npcsay(player, npc, "There is no way that you are getting me into a barrel.",
									"No WAY! DO you understand?");
								anaDialogue(player, npc, Ana.GOTTHAT);
							}
						}
					}
				}
				break;
			case Ana.GOTTHAT:
				int gotit = multi(player, npc,
					"Ok, yep, I've got that.",
					"Well, we'll see, it might be the only way.");
				if (gotit == 0) {
					npcsay(player, npc, "Good, just make sure you keep it in mind.",
						"Anyway, I have to get back to work.",
						"The guards will come along soon and give us some trouble else.");
				} else if (gotit == 1) {
					npcsay(player, npc, "No, there has to be a better way!",
						"Anyway, I have to get back to work.",
						"The guards will come along soon and give us some trouble else.");
				}
				break;
			case Ana.SNEAKEDPAST:
				npcsay(player, npc, "Hmm, impressive, but can you so easily sneak out again?",
					"How did you manage to get through the gate?");
				int gosh = multi(player, npc, false, //do not send over
					"I have a key",
					"It's a trade secret!");
				if (gosh == 0) {
					say(player, npc, "I used a key.");
					Npc guard = addnpc(player.getWorld(), NpcId.MERCENARY.id(), player.getX(), player.getY(), 60000);
					if (guard != null) {
						npcsay(player, guard, "I heard that! So you used a key did you?! ");
						if (player.getCarriedItems().hasCatalogID(ItemId.METAL_KEY.id(), Optional.of(false))) {
							npcsay(player, guard, "Right, we'll have that key off you!");
							player.getCarriedItems().remove(new Item(ItemId.METAL_KEY.id()));
						}
						npcsay(player, guard, "Guards! Guards!");
						guard.startCombat(player);
						npcsay(player, npc, "Oopps! See ya!");
						mes("Some guards rush to help their comrade.");
						delay(3);
						mes("You are roughed up a bit by the guards as you're manhandlded into a cell.");
						delay(3);
						npcsay(player, guard, "Into the cell you go! I hope this teaches you a lesson.");
						player.teleport(75, 3625);
					}
				} else if (gosh == 1) {
					say(player, npc, "It's a trade secret!");
					npcsay(player, npc, "Oh, right, well, I guess you know what you're doing.",
						"Anyway, I have to get back to work.",
						"The guards will come along soon and give us some trouble else.");
				}
				break;
			case Ana.GUARDSRUBBISH:
				Npc guard = addnpc(player.getWorld(), NpcId.MERCENARY.id(), player.getX(), player.getY(), 60000);
				if (guard != null) {
					npcsay(player, guard, "I heard that! So you managed to sneak in did you!",
						"Guards! Guards!");
					guard.startCombat(player);
					npcsay(player, npc, "Oopps! See ya!");
					mes("The Guards search you!");
					delay(3);
					mes("Some guards rush to help their comrade.");
					delay(3);
					mes("You are roughed up a bit by the guards as you're manhandlded into a cell.");
					delay(3);
					npcsay(player, guard, "Into the cell you go! I hope this teaches you a lesson.");
					player.teleport(75, 3625);
				}
				break;
		}

	}

	@Override
	public void onTalkNpc(final Player player, final Npc npc) {
		if (npc.getID() == NpcId.IRENA.id()) {
			irenaDialogue(player, npc, -1);
		} else if (npc.getID() == NpcId.MERCENARY.id()) {
			mercenaryDialogue(player, npc, -1);
		} else if (npc.getID() == NpcId.MERCENARY_CAPTAIN.id()) {
			mercenaryCaptainDialogue(player, npc, -1);
		} else if (npc.getID() == NpcId.MERCENARY_ESCAPEGATES.id()) {
			mercenaryInsideDialogue(player, npc, -1);
		} else if (npc.getID() == NpcId.MINING_SLAVE.id()) {
			slaveDialogue(player, npc, -1);
		} else if (npc.getID() == NpcId.ESCAPING_MINING_SLAVE.id()) {
			escapingSlaveDialogue(player, npc);
		} else if (npc.getID() == NpcId.BEDABIN_NOMAD.id()) {
			bedabinNomadDialogue(player, npc, -1);
		} else if (npc.getID() == NpcId.BEDABIN_NOMAD_GUARD.id()) {
			switch (player.getQuestStage(this)) {
				case 8:
				case 9:
				case 10:
				case -1:
					npcsay(player, npc, "Sorry, but you can't use the tent without permission.",
						"But thanks for your help to the Bedabin people.");
					if (player.getCarriedItems().hasCatalogID(ItemId.TECHNICAL_PLANS.id(), Optional.of(false))) {
						npcsay(player, npc, "And we'll take those plans off your hands as well!");
						player.getCarriedItems().remove(new Item(ItemId.TECHNICAL_PLANS.id()));
					}
					break;
				default:
					npcsay(player, npc, "Sorry, this is a private tent, no one is allowed in.",
						"Orders of Al Shabim...");
					break;
			}
		} else if (npc.getID() == NpcId.AL_SHABIM.id()) {
			alShabimDialogue(player, npc, -1);
		} else if (npc.getID() == NpcId.CAPTAIN_SIAD.id()) {
			captainSiadDialogue(player, npc, -1, null);
		} else if (npc.getID() == NpcId.MERCENARY_LIFTPLATFORM.id()) {
			if (player.getQuestStage(this) == -1) {
				npcsay(player, npc, "Move along please, don't want any trouble today!");
				return;
			}
			npcsay(player, npc, "Yes, what do you want?");
			int menu = multi(player, npc,
				"Nothing thanks - sorry for disturbing you.",
				"Your head on a stick.");
			if (menu == 0) {
				npcsay(player, npc, "Well...I guess that's Ok, get on your way though.");
			} else if (menu == 1) {
				npcsay(player, npc, "Why you ungrateful whelp...I'll teach you some manners.");
				if (player.getQuestStage(this) == -1) {
					npc.startCombat(player);
				} else {
					mes("The guard shouts for help.");
					delay(3);
					npc.startCombat(player);
					mes("Other guards start arriving.");
					delay(3);
					npcsay(player, npc, player.getText("TouristTrapMercenaryLiftPlatformGetThemMen"));
					player.message("The guards rough you up a bit and then drag you to a cell.");
					player.teleport(76, 3625);
				}
			}
		} else if (npc.getID() == NpcId.MERCENARY_JAILDOOR.id()) {
			npcsay(player, npc, "Yeah, what do you want?");
			int menu = multi(player, npc,
				"What are you guarding?",
				"Oh, nothing sorry for disturbing you.",
				"Your head on a stick.");
			if (menu == 0) {
				npcsay(player, npc, "I'm guarding troublesome prisoners.",
					"They think they can get away with attacking the guards.",
					"Well, we taught them a thing or two.");
			} else if (menu == 1) {
				npcsay(player, npc, "I should think so to, now get back to work.");
			} else if (menu == 2) {
				npcsay(player, npc, "Why you ungrateful whelp...I'll teach you some manners.");
				if (player.getQuestStage(this) == -1) {
					npc.startCombat(player);
				} else {
					mes("The guard shouts for help.");
					delay(3);
					npc.startCombat(player);
					mes("Other guards start arriving.");
					delay(3);
					npcsay(player, npc, player.getText("TouristTrapMercenaryLiftPlatformGetThemMen"));
					player.message("The guards rough you up a bit and then drag you to a cell.");
					player.teleport(76, 3625);
				}
			}
		} else if (npc.getID() == NpcId.ANA.id()) {
			anaDialogue(player, npc, -1);
		}
	}

	public static void indirectTalktoAlShabim(final Player player, final Npc npc) {
		if (npc.getID() == NpcId.AL_SHABIM.id()) {
			if (player.getQuestStage(Quests.TOURIST_TRAP) == 6 || player.getQuestStage(Quests.TOURIST_TRAP) == 7) {
				alShabimDialogue(player, npc, AlShabim.HAVE_PLANS);
			} else if (player.getQuestStage(Quests.TOURIST_TRAP) > 7 || player.getQuestStage(Quests.TOURIST_TRAP) == -1) {
				mes("Al Shabim takes the technical plans off you.");
				delay(3);
				npcsay(player, npc, "Thanks for the technical plans Effendi!",
					"We've been lost without them!");
			}
		}
	}

	@Override
	public boolean blockOpLoc(final Player player, final GameObject obj, final String command) {
		return inArray(obj.getID(), IRON_GATE, ROCK_1, WOODEN_DOORS, DESK, BOOKCASE, CAPTAINS_CHEST) || (obj.getID() == STONE_GATE && player.getY() >= 735);
	}

	@Override
	public void onOpLoc(final Player player, final GameObject obj, final String command) {
		if (obj.getID() == STONE_GATE && player.getY() >= 735) {
			if (command.equals("go through")) {
				if (!player.getCarriedItems().hasCatalogID(ItemId.ANA_IN_A_BARREL.id(), Optional.of(false))) {
					player.message("you go through the gate");
					player.teleport(62, 732);
				} else {
					if (player.getQuestStage(this) == 9) {
						mes("Ana looks out of the barrel...");
						delay(3);
						mes("@gre@Ana: Hey great, we're at the Shantay Pass!");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.ANA_IN_A_BARREL.id()));
						player.updateQuestStage(this, 10);
						Npc Ana = addnpc(player.getWorld(), NpcId.ANA.id(), player.getX(), player.getY(), 60000);
						Ana.teleport(player.getX(), player.getY() + 1);
						if (Ana != null) {
							delay();
							npcsay(player, Ana, "Great! Thanks for getting me out of that mine!",
								"And that barrel wasn't too bad anyway!",
								"Pop by again sometime, I'm sure we'll have a barrel of laughs!",
								"Oh! I nearly forgot, here's a key I found in the tunnels.",
								"It might be of some use to you, not sure what it opens.");
							give(player, ItemId.WROUGHT_IRON_KEY.id(), 1);
							mes("Ana spots Irena and waves...");
							delay(3);
							npcsay(player, Ana, "Hi Mum!",
								"Sorry, I have to go now!");
							Ana.remove();
						}
						Npc Irena = ifnearvisnpc(player, NpcId.IRENA.id(), 15);
						if (Irena != null) {
							npcsay(player, Irena, "Hi Ana!");
							rewardMenu(player, Irena, true);
							player.getCache().remove("tried_ana_barrel");
						}
					}
					//should not have an ana in barrel in other stages
					else {
						player.getCarriedItems().remove(new Item(ItemId.ANA_IN_A_BARREL.id()));
					}
				}
			} else if (command.equals("look")) {
				mes("You look at the huge Stone Gate.");
				delay(3);
				mes("On the gate is a large poster, it reads.");
				delay(3);
				mes("@gre@The Desert is a VERY Dangerous place...do not enter if you are scared of dying.");
				delay(3);
				mes("@gre@Beware of high temperatures, sand storms, robbers, and slavers...");
				delay(3);
				mes("@gre@No responsibility is taken by Shantay ");
				delay(3);
				mes("@gre@If anything bad should happen to you in any circumstances whatsoever.");
				delay(3);
				mes("Despite this warning lots of people seem to pass through the gate.");
				delay(3);
			}
		} else if (obj.getID() == IRON_GATE) {
			if (command.equals("open")) {
				if (player.getCarriedItems().hasCatalogID(ItemId.ANA_IN_A_BARREL.id(), Optional.of(false))) {
					failEscapeAnaInBarrel(player, null);
					return;
				} else if (!player.getCarriedItems().hasCatalogID(ItemId.METAL_KEY.id(), Optional.of(false))) {
					player.message("This gate is locked, you'll need a key to open it.");
				} else {
					Armed armedVal = playerArmed(player);
					// armed player makes mercenary not follow slave dialogue but that of weapon/armour
					if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.SLAVES_ROBE_BOTTOM.id())
						&& player.getCarriedItems().getEquipment().hasEquipped(ItemId.SLAVES_ROBE_TOP.id())
						&& armedVal == Armed.NONE) {
						Npc guard = ifnearvisnpc(player, NpcId.MERCENARY.id(), 5);
						if (guard != null ){
							player.message("A guard notices you as you try to slip past...");
							npcsay(player, guard, "Hey! Where do you think you're going?");
							guard.setChasing(player);
							npcsay(player, guard, "Guards! Slave escaping!");
							if (player.getQuestStage(this) == -1) {
								player.message("No other guards come to the rescue.");
								return;
							}
							if (player.getX() <= 91) {
								// sent to jail
								mercenaryDialogue(player, guard, Mercenary.THROW_PRISON);
							} else {
								// sent to desert
								mercenaryDialogue(player, guard, Mercenary.LEAVE_DESERT);
							}
						}
						return;
					}

					mes("You use the metal key to unlock the gates.");
					delay(3);
					mes("You manage to sneak past the guards!.");
					delay(3);
					doGate(player, obj);
					player.message("The gate swings open.");
					delay(2);
					player.message("The gates close behind you.");
					Npc n = ifnearvisnpc(player, NpcId.MERCENARY_ESCAPEGATES.id(), 15);
					if (n != null) {
						if (player.getQuestStage(this) == -1) { // no dialogue after quest on just opening gates
							// todo change the coords going in and going out.
						} else {
							if (armedVal != Armed.NONE) {
								switch (armedVal) {
									case WEAPON:
										npcsay(player, n, "Oi You with the weapon, what are you doing?");
										break;
									case ARMOUR:
										npcsay(player, n, "Oi You with the armour on, what are you doing?");
										break;
									case BOTH:
									default:
										npcsay(player, n, "Oi You with the weapon and armour, what are you doing?");
										break;
								}
								npcsay(player, n, "You don't belong in here!");
								player.message("More guards come to arrest you.");
								n.startCombat(player);
								npcsay(player, n, "Right, you're going in the cell!");
								mes("You're outnumbered by all the guards.");
								delay(3);
								mes("They man-handle you into a cell.");
								delay(3);
								player.teleport(89, 801);
							}
						}
					}
				}
			} else if (command.equals("search")) {
				mes("You search the gate.");
				delay(3);
				mes("Inside the compound you can see that there are lots of slaves mining away.");
				delay(3);
				mes("They all seem to be dressed in dirty disgusting desert rags.");
				delay(3);
				mes("And equiped only with a mining pick.");
				delay(3);
				mes("Each slave is chained to a rock where they seemingly mine all day long.");
				delay(3);
				mes("Guards patrol the area extensively.");
				delay(3);
				mes("But you might be able to sneak past them if you try to blend in.");
				delay(3);
			}
		} else if (obj.getID() == ROCK_1) {
			player.message("You start climbing the rocky elevation.");
			if (!succeedRate(player)) {
				player.message("You slip a little and tumble the rest of the way down the slope.");
				player.damage(7);
			}
			player.teleport(93, 799);
		} else if (obj.getID() == WOODEN_DOORS) {
			if (command.equals("open")) {
				mes("You push the door.");
				delay(3);
				say(player, null, "Ugh!");
				if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.SLAVES_ROBE_BOTTOM.id()) && player.getCarriedItems().getEquipment().hasEquipped(ItemId.SLAVES_ROBE_TOP.id())) {
					mes("The door opens with some effort ");
					delay(3);
					if (obj.getX() == 81 && obj.getY() == 3633) {
						player.teleport(82, 802);
						return;
					}
					player.teleport(82, 3630);
					mes("The huge doors open into a dark, dank and smelly tunnel.");
					delay(3);
					mes("The associated smells of a hundred sweaty miners greets your nostrils.");
					delay(3);
					mes("And your ears ring with the 'CLANG CLANG CLANG' as metal hits rock.");
					delay(3);
				} else {
					final Npc n = addnpc(player.getWorld(), NpcId.DRAFT_MERCENARY_GUARD.id(), player.getX(), player.getY());
					player.getWorld().getServer().getGameEventHandler().add(
						new SingleEvent(player.getWorld(), null, config().GAME_TICK * 50, "Draft Mercenary Talk Delay") {
							public void action() {
								npcYell(player, n, "Is that the time, I ought to be going.");
								getWorld().getServer().getGameEventHandler().add(new SingleEvent(getWorld(), null, n.getConfig().GAME_TICK * 5, "Draft Mercenary Remove") {
									@Override
									public void action() {
										n.remove();
									}
								});
							}
						});
					delay(2);
					npcsay(player, n, "Oi You!");
					mes("A guard notices you and approaches...");
					delay(3);
					n.startCombat(player);
					npcsay(player, n, "Hey, you're no slave, where do you think you're going!");
					npcsay(player, n, "Guards, guards!");
					if (player.getQuestStage(this) == -1) {
						player.message("No other guards come to the rescue.");
						return;
					}
					mercenaryDialogue(player, n, Mercenary.THROW_PRISON);
				}
			} else if (command.equals("watch")) {
				if (obj.getX() == 81 && obj.getY() == 3633) {
					player.message("Nothing much seems to happen.");
				} else {
					mes("You watch the doors for some time.");
					delay(3);
					mes("You notice that only slaves seem to go down there.");
					delay(3);
					mes("You might be able to sneak down if you pass as a slave.");
					delay(3);
				}
			}
		} else if (obj.getID() == DESK) {
			mes("You search the captains desk while he's not looking.");
			delay(3);
			if (player.getCarriedItems().hasCatalogID(ItemId.CELL_DOOR_KEY.id(), Optional.of(false))
				&& player.getCarriedItems().hasCatalogID(ItemId.METAL_KEY.id(), Optional.of(false)) &&
				((player.getQuestStage(this) >= 0 && player.getQuestStage(this) <= 9) || player.getCarriedItems().hasCatalogID(ItemId.WROUGHT_IRON_KEY.id(), Optional.of(false)))) {
				mes("...but you find nothing of interest.");
				delay(3);
				return;
			}
			if (!player.getCarriedItems().hasCatalogID(ItemId.CELL_DOOR_KEY.id(), Optional.of(false))) {
				mes("You find a cell door key.");
				delay(3);
				give(player, ItemId.CELL_DOOR_KEY.id(), 1);
			}
			if (!player.getCarriedItems().hasCatalogID(ItemId.METAL_KEY.id(), Optional.of(false))) {
				mes("You find a large metalic key.");
				delay(3);
				give(player, ItemId.METAL_KEY.id(), 1);
			}
			//only after player has past to stage of wrought iron key
			if (!(player.getQuestStage(this) >= 0 && player.getQuestStage(this) <= 9) && !player.getCarriedItems().hasCatalogID(ItemId.WROUGHT_IRON_KEY.id(), Optional.of(false))) {
				mes("You find a large wrought iron key.");
				delay(3);
				give(player, ItemId.WROUGHT_IRON_KEY.id(), 1);
			}
		} else if (obj.getID() == BOOKCASE) {
			if (command.equals("search")) {
				player.message("You notice several books on the subject of Sailing.");
				if (!player.getCache().hasKey("sailing")) {
					player.getCache().store("sailing", true);
				}
			} else if (command.equals("look")) {
				player.message("The captain seems to collect lots of books!");
			}
		} else if (obj.getID() == CAPTAINS_CHEST) {
			if (player.getCache().hasKey("tourist_chest") || player.getQuestStage(this) == -1) {
				if (player.getCarriedItems().hasCatalogID(ItemId.BEDOBIN_COPY_KEY.id(), Optional.of(false))) {
					if (!player.getCarriedItems().hasCatalogID(ItemId.TECHNICAL_PLANS.id(), Optional.of(false))) {
						mes("While the Captain's distracted, you quickly unlock the chest.");
						delay(3);
						mes("You use the Bedobin Copy Key to open the chest.");
						delay(3);
						mes("You open the chest and take out the plans.");
						delay(3);
						give(player, ItemId.TECHNICAL_PLANS.id(), 1);
						if (!player.getCache().hasKey("tech_plans")) {
							player.getCache().store("tech_plans", true);
						}
					} else {
						player.message("The chest is empty.");
					}
					if (player.getCache().hasKey("sailing")) {
						player.getCache().remove("sailing");
					}
					if (player.getCache().hasKey("tourist_chest")) {
						player.getCache().remove("tourist_chest");
					}
				} else {
					if (player.getCache().hasKey("sailing")) {
						player.getCache().remove("sailing");
					}
					if (player.getCache().hasKey("tourist_chest")) {
						player.getCache().remove("tourist_chest");
					}
					player.message("This chest needs a key!");
				}
			} else {
				Npc n = ifnearvisnpc(player, NpcId.CAPTAIN_SIAD.id(), 5);
				if (n == null) {
					final Npc npc = addnpc(player.getWorld(), NpcId.CAPTAIN_SIAD.id(), 85, 1745);
					player.getWorld().getServer().getGameEventHandler().add(
						new SingleEvent(player.getWorld(), null, config().GAME_TICK * 490, "Captain Siad Despawn Delay") {
							public void action() {
								npcYell(player, npc, "Ah, time for my evening snooze!");
								getWorld().getServer().getGameEventHandler().add(new SingleEvent(getWorld(), null, npc.getConfig().GAME_TICK * 5, "Captain Siad Remove") {
									@Override
									public void action() {
										npc.remove();
									}
								});
							}
						});
					n = npc;
					delay(2);
				}
				captainSiadDialogue(player, n, -1, obj);
			}
		}
	}

	@Override
	public boolean blockOpNpc(final Player player, final Npc npc, final String command) {
		return npc.getID() == NpcId.MERCENARY_CAPTAIN.id() && command.equalsIgnoreCase("watch");
	}

	@Override
	public void onOpNpc(final Player player, final Npc npc, final String command) {
		if (npc.getID() == NpcId.MERCENARY_CAPTAIN.id() && command.equalsIgnoreCase("watch")) {
			mes("You watch the Mercenary Captain for some time.");
			delay(3);
			mes("He has a large metal key attached to his belt.");
			delay(3);
			mes("You notice that he usually gets his men to do his dirty work.");
			delay(3);
		}
	}

	@Override
	public boolean blockKillNpc(final Player player, final Npc npc) {
		return npc.getID() == NpcId.MERCENARY_CAPTAIN.id();
	}

	@Override
	public void onKillNpc(final Player player, final Npc npc) {
		if (npc.getID() == NpcId.MERCENARY_CAPTAIN.id()) {
			player.message("You kill the captain!");
			if (player.getQuestStage(this) == 1 && !player.getCache().hasKey("first_kill_captn")) {
				player.getCache().store("first_kill_captn", true);
			}
			if (!player.getCarriedItems().hasCatalogID(ItemId.METAL_KEY.id(), Optional.of(false))) {
				give(player, ItemId.METAL_KEY.id(), 1);
				mes("The mercenary captain drops a metal key on the floor.");
				delay(3);
				mes("You quickly grab the key and add it to your inventory.");
				delay(3);
			}
		}
	}

	@Override
	public boolean blockAttackNpc(final Player player, final Npc npc) {
		return player.getQuestStage(this) >= 0 &&
			(npc.getID() == NpcId.CAPTAIN_SIAD.id() || npc.getID() == NpcId.MERCENARY.id() || npc.getID() == NpcId.MERCENARY_ESCAPEGATES.id()
				|| npc.getID() == NpcId.MERCENARY_LIFTPLATFORM.id() || npc.getID() == NpcId.MERCENARY_JAILDOOR.id()
				|| (npc.getID() == NpcId.MERCENARY_CAPTAIN.id() && player.getCarriedItems().getInventory().countId(ItemId.METAL_KEY.id()) < 1));
	}

	@Override
	public void onAttackNpc(final Player player, final Npc affectedmob) {
		tryToAttackMercenarys(player, affectedmob);
	}

	@Override
	public boolean blockPlayerRangeNpc(final Player player, final Npc npc) {
		return player.getQuestStage(this) >= 0 &&
			(npc.getID() == NpcId.CAPTAIN_SIAD.id() || npc.getID() == NpcId.MERCENARY.id() || npc.getID() == NpcId.MERCENARY_ESCAPEGATES.id()
				|| npc.getID() == NpcId.MERCENARY_LIFTPLATFORM.id() || npc.getID() == NpcId.MERCENARY_JAILDOOR.id()
				|| (npc.getID() == NpcId.MERCENARY_CAPTAIN.id() && player.getCarriedItems().getInventory().countId(ItemId.METAL_KEY.id()) < 1));
	}

	@Override
	public void onPlayerRangeNpc(final Player player, final Npc npc) {
		tryToAttackMercenarys(player, npc);
	}

	@Override
	public boolean blockSpellNpc(final Player player, final Npc npc) {
		final boolean questStage = player.getQuestStage(this) >= 0;
		final boolean isMerc = npc.getID() == NpcId.CAPTAIN_SIAD.id() || npc.getID() == NpcId.MERCENARY.id() || npc.getID() == NpcId.MERCENARY_ESCAPEGATES.id()
			|| npc.getID() == NpcId.MERCENARY_LIFTPLATFORM.id() || npc.getID() == NpcId.MERCENARY_JAILDOOR.id();
		final boolean isMercCaptain = npc.getID() == NpcId.MERCENARY_CAPTAIN.id() && player.getCarriedItems().getInventory().countId(ItemId.METAL_KEY.id()) < 1;

		// Check if we're already in combat. If we are, then we can mage.
		if (player.inCombat()) {
			// Make sure that we're casting on the same NPC we're fighting
			Npc victim = null;
			if (player.getCombatEvent().getAttacker().isNpc()) {
				victim = (Npc)player.getCombatEvent().getAttacker();
			} else if (player.getCombatEvent().getVictim().isNpc()) {
				victim = (Npc)player.getCombatEvent().getVictim();
			}

			if (victim != null && victim.getUUID() == npc.getUUID()) {
				// Cast on them
				return false;
			}
		}

		return questStage && (isMerc || isMercCaptain);
	}

	@Override
	public void onSpellNpc(Player player, Npc n) {
		tryToAttackMercenarys(player, n);
	}

	private void tryToAttackMercenarys(Player player, Npc affectedmob) {
		// We should probably return if the player is already in combat.
		// This should only happen if the player is trying to mage another guard while in combat.
		// We don't need them fighting two enemies at once, and we don't want them to be able
		// to get teleported if they accidentally cast on another enemy.
		if (player.inCombat()) return;

		if (affectedmob.getID() == NpcId.CAPTAIN_SIAD.id()) {
			player.message("Captain Siad looks pretty aggressive.");
			player.message("Are you sure you want to attack him?");
			int menu = multi(player,
				"Yes, I want to attack him.",
				"Nope, I've changed my mind.");
			if (menu == 0) {
				npcsay(player, affectedmob, "Guards! Guards!");
				affectedmob.startCombat(player);
				captainSiadDialogue(player, affectedmob, Siad.PUNISHED, null);
			} else if (menu == 1) {
				player.message("You change your mind about attacking the Captain.");
			}
		} else if (affectedmob.getID() == NpcId.MERCENARY_CAPTAIN.id() || affectedmob.getID() == NpcId.MERCENARY.id() || affectedmob.getID() == NpcId.MERCENARY_ESCAPEGATES.id()) {
			player.message("This guard looks fearsome and very aggressive.");
			player.message("Are you sure you want to attack him?");
			int menu = multi(player,
				"Yes, I want to attack him.",
				"Nope, I've changed my mind.");
			if (menu == 0) {
				player.message("You decide to attack the guard.");
				npcsay(player, affectedmob, "Guards! Guards!");
				if (affectedmob.getID() == NpcId.MERCENARY_CAPTAIN.id()) {
					Npc helpermob = ifnearvisnpc(player, NpcId.MERCENARY.id(), 10);
					if (helpermob != null) affectedmob = helpermob;
				}
				affectedmob.startCombat(player);
				if (affectedmob.getID() == NpcId.MERCENARY_ESCAPEGATES.id()) {
					player.message("More guards rush to catch you.");
					mes("You are roughed up a bit by the guards as you're manhandlded to a cell.");
					delay(3);
					npcsay(player, affectedmob, "Into the cell you go! I hope this teaches you a lesson.");
					player.teleport(89, 801);
				} else {
					npcsay(player, affectedmob, "Guards, guards!");
					mes("Nearby guards quickly grab you and rough you up a bit.");
					delay(3);
					npcsay(player, affectedmob, "Let's see how good you are with desert survival techniques!");
					mes("You're bundled into the back of a cart and blindfolded...");
					delay(3);
					mes("Sometime later you wake up in the desert.");
					delay(3);
					if (player.getCarriedItems().hasCatalogID(ItemId.BOWL_OF_WATER.id(), Optional.of(false))) {
						npcsay(player, affectedmob, "You won't be needing that water any more!");
						mes("The guards throw your water away...");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.BOWL_OF_WATER.id()));
					}
					mes("The guards move off in the cart leaving you stranded in the desert.");
					delay(3);
					Point desertLoc = desertTPPoints[DataConversions.getRandom().nextInt(desertTPPoints.length)];
					player.teleport(desertLoc.getX(), desertLoc.getY());
				}
			} else if (menu == 1) {
				player.message("You decide not to attack the guard.");
			}
		} else if (affectedmob.getID() == NpcId.MERCENARY_LIFTPLATFORM.id() || affectedmob.getID() == NpcId.MERCENARY_JAILDOOR.id()) {
			player.message("This guard looks fearsome and very aggressive.");
			player.message("Are you sure you want to attack him?");
			int menu = multi(player,
				"Yes, I want to attack him.",
				"Nope, I've changed my mind.");
			if (menu == 0) {
				player.message("You decide to attack the guard.");
				npcsay(player, affectedmob, "Guards! Guards!");
				affectedmob.startCombat(player);
				if (player.getCarriedItems().hasCatalogID(ItemId.ANA_IN_A_BARREL.id(), Optional.of(false))) {
					npcsay(player, affectedmob, "Hey, what's in this barrel?",
						"Right...we'll take that off your hands!");
					mes("The guards drag Ana into the distance...");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.ANA_IN_A_BARREL.id()));
				}
				mes("Some guards rush to help their comrade.");
				delay(3);
				mes("You are roughed up a bit by the guards as you're manhandlded into a cell.");
				delay(3);
				npcsay(player, affectedmob, "Into the cell you go! I hope this teaches you a lesson.");
				player.teleport(74, 3626);
			} else if (menu == 1) {
				player.message("You decide not to attack the guard.");
			}
		}
	}

	private void captainWantToThrowPlayer(Player player, Npc n) {
		n = ifnearvisnpc(player, NpcId.MERCENARY.id(), 10);
		if (n != null) {
			int punishment = DataConversions.random(0, 3);
			if (punishment == 0) {
				player.message("A guard approaches you and pretends to start hiting you.");
				npcsay(player, n, "Take that you infidel!");
				player.message("The guard leans closer to you and says in a low voice.");
				npcsay(player, n, "We're sick of having to kill every lunatic that comes along",
					"and insults the captain, it makes such a mess.",
					"Thankfully, he's a bit decrepid so he doesn't notice",
					"so please, buzz off and don't come here again.");
			} else if (punishment == 1) {
				player.message("The guard approaches you again kicks you slightly.");
				say(player, n, "Ow!");
				npcsay(player, n, "Take that you mad child of a dog!");
				player.message("The guard leans closer to you and says in a low voice.");
				npcsay(player, n, "What are you doing here again?",
					"Didn't I tell you to get out of here!",
					"Now get lost, properly this time!",
					"Or we may be forced to see his orders through properly.");
			} else if (punishment == 2) {
				player.message("A guard approaches you and looks very angry, he slaps you across the face.");
				npcsay(player, n, "Prepare to die effendi!");
				player.message("The guard leans close and whispers");
				npcsay(player, n, "Are you mad effendi!",
					"This is your last chance.",
					"Leave now and never come back.",
					"Or I'll introduce you to my friend.");
				player.message("The guard half draws his fearsome looking scimitar.");
				npcsay(player, n, "And we'll be pleased to clean the mess up after you've been dispatched.");
			} else {
				player.message("An angry guard approaches you and whips out his sword.");
				npcsay(player, n, (DataConversions.getRandom().nextBoolean() ? "Guard: " : "") +"Ok, that does it!",
					"You're in serious trouble now!");
				if (DataConversions.getRandom().nextBoolean()) {
					npcsay(player, n,
						player.getText("TouristTrapCaptainWantsToThrowPlayerOkMenWeNeedToTeachThis"),
						"about desert survival techniques.");
					mes("The guards grab you and beat you up.");
					delay(3);
				} else {
					npcsay(player, n,
						"Ok men, we need to teach this person a thing or two",
						"about desert survival techniques.");
					mes("The guards grab you and rough you up a bit.");
					delay(3);
				}
				player.damage(DataConversions.random(4, 7));
				mes("You're grabed and manhandled onto a cart.");
				delay(3);
				mes("Sometime later you're dumped in the middle of the desert.");
				delay(3);
				mes("The guards move off in the cart leaving you stranded in the desert.");
				delay(3);
				Point desertLoc = desertTPPoints[DataConversions.getRandom().nextInt(desertTPPoints.length)];
				player.teleport(desertLoc.getX(), desertLoc.getY());
			}
		}
	}

	private void failEscapeAnaInBarrel(Player player, Npc n) {
		if (player.getCarriedItems().hasCatalogID(ItemId.ANA_IN_A_BARREL.id(), Optional.of(false))) {
			n = addnpc(player.getWorld(), NpcId.MERCENARY.id(), player.getX(), player.getY(), 60000);
			delay();
			npcsay(player, n, "Hey, where d'ya think you're going with that barrel?",
				"You should know that they go out on the cart!",
				"We'd better check this out!");
			player.message("The guards prize the lid off the barrel.");
			player.getCarriedItems().remove(new Item(ItemId.ANA_IN_A_BARREL.id()));
			npcsay(player, n, "Blimey! It's a jail break!",
				"They're making a break for it!");
			Npc ana = addnpc(player.getWorld(), NpcId.ANA.id(), player.getX(), player.getY(), 30000);
			delay();
			npcsay(player, ana, "I could have told you we wouldn't get away with it!",
				"Now look at the mess you've caused!");
			player.message("The guards grab Ana and drag her away.");
			if (ana != null) {
				ana.remove();
			}
			mes("@gre@Ana: Hey, watch it with the hands buster.");
			delay(3);
			mes("@gre@Ana: These are the upper market slaves clothes doncha know!");
			delay(3);
			npcsay(player, n, "Right, we'd better teach you a lesson as well!");
			mes("The guards rough you up a bit.");
			delay(3);
			npcsay(player, n, player.getText("TouristTrapFailEscapeAnaInBarrelRightLadsStuff"),
				"Specially for our most honoured guests.");
			player.message("The guards drag you away to a cell.");
			mes("@yel@Guards: There you go, we hope you 'dig' you're stay here.");
			delay(3);
			mes("@yel@Guards: Har! Har! Har!");
			delay(3);
			if (n != null) {
				n.remove();
			}
			player.teleport(75, 3626);
		}
	}

	private void failWindowAnaInBarrel(Player player, Npc n) {
		if (player.getCarriedItems().hasCatalogID(ItemId.ANA_IN_A_BARREL.id(), Optional.of(false))) {
			mes("You focus all of your strength on the bar. Your muscles ripple!");
			delay(3);
			mes("You manage to bend the bars on the window .");
			delay(3);
			mes("You'll never get Ana in the Barrel through the window.");
			delay(3);
			mes("The barrel is just too big.");
			delay(3);
			mes("@gre@Ana: Don't think for one minute ...");
			delay(3);
			mes("@gre@Ana: you're gonna get me through that window!");
			delay(3);
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return (obj.getID() == JAIL_DOOR && obj.getX() == 88 && obj.getY() == 801)
				|| (obj.getID() == WINDOW && (obj.getX() == 90 || obj.getX() == 89) && obj.getY() == 802)
				|| obj.getID() == TENT_DOOR_1 || obj.getID() == TENT_DOOR_2
				|| obj.getID() == CAVE_JAIL_DOOR || obj.getID() == STURDY_IRON_GATE;
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == WINDOW && (obj.getX() == 90 || obj.getX() == 89) && obj.getY() == 802) {
			mes("You search the window.");
			delay(3);
			mes("After some time you find that one of the bars looks weak,  ");
			delay(3);
			mes("you may be able to bend one of the bars. ");
			delay(3);
			mes("Would you like to try ?");
			delay(3);
			int menu = multi(player,
				"Yes, I'll bend the bar.",
				"No, I'd better stay here.");
			if (menu == 0) {
				attemptBendBar(player);
			} else if (menu == 1) {
				mes("You decide to stay in the cell.");
				delay(3);
				mes("Maybe they'll let you out soon?");
				delay(3);
			}
		} else if (obj.getID() == JAIL_DOOR && obj.getX() == 88 && obj.getY() == 801) {
			if (player.getCarriedItems().hasCatalogID(ItemId.CELL_DOOR_KEY.id(), Optional.of(false))) {
				player.message("You unlock the door and walk through.");
				doDoor(obj, player);
			} else {
				mes("You need a key to unlock this door,");
				delay(3);
				mes("And you don't seem to have one that fits.");
				delay(3);
			}
		} else if (obj.getID() == TENT_DOOR_1) {
			if (player.getY() <= 793) {
				player.teleport(171, 795);
			} else {
				Npc n = ifnearvisnpc(player, NpcId.BEDABIN_NOMAD_GUARD.id(), 5);
				if (n == null) {
					n = addnpc(player.getWorld(), NpcId.BEDABIN_NOMAD_GUARD.id(), player.getX(), player.getY(), 60000);
					delay();
				}
				n.teleport(170, 794);
				switch (player.getQuestStage(this)) {
					case 8:
					case 9:
					case 10:
					case -1:
						npcsay(player, n, "Sorry, but you can't use the tent without permission.",
							"But thanks for your help to the Bedabin people.");
						if (player.getCarriedItems().hasCatalogID(ItemId.TECHNICAL_PLANS.id(), Optional.of(false))) {
							npcsay(player, n, "And we'll take those plans off your hands as well!");
							player.getCarriedItems().remove(new Item(ItemId.TECHNICAL_PLANS.id()));
						}
						break;
					default:
						npcsay(player, n, "Sorry, this is a private tent, no one is allowed in.",
							"Orders of Al Shabim...");
						break;
				}
			}
		} else if (obj.getID() == TENT_DOOR_2) {
			/*if(p.getY() >= 805) {
				p.teleport(169, 804);
			} else {
				p.teleport(171, 806);
			}*/
			doTentDoor(obj, player);
		} else if (obj.getID() == CAVE_JAIL_DOOR) {
			Npc n = ifnearvisnpc(player, NpcId.MERCENARY_JAILDOOR.id(), 5);
			if (n != null) {
				if (player.getX() >= 72) {
					if (!ifheld(player, ItemId.ROCKS.id(), 15)) {
						npcsay(player, n, "Hey, move away from the gate.",
							"If you wanna get out, you're gonna have to mine for it.",
							"You're gonna have to bring me 15 loads of rocks - in one go!",
							"And then I'll let you out.",
							"You can go back and work with the other slaves then!");
					} else {
						say(player, n, "Hey, I have your rocks here, let me out.");
						for (int i = 0; i < 15; i++) {
							player.getCarriedItems().remove(new Item(ItemId.ROCKS.id()));
						}
						npcsay(player, n, "Ok, ok, come on out.");
						player.teleport(71, 3626);
						player.message("The guard unlocks the gate and lets you out.");
						player.teleport(69, 3625);
					}
				} else {
					npcsay(player, n, "Hey, move away from that gate!");
				}
			}
		} else if (obj.getID() == STURDY_IRON_GATE) {
			if (player.getY() >= 3617) {
				if (player.getCarriedItems().hasCatalogID(ItemId.WROUGHT_IRON_KEY.id(), Optional.of(false))) {
					player.message("You use the wrought iron key to unlock the gate.");
					player.teleport(player.getX(), player.getY() - 1);
				} else {
					mes("You need a key to unlock this door,");
					delay(3);
					mes("And you don't seem to have one that fits.");
					delay(3);
				}
			} else {
				player.message("You push the gate open and walk through.");
				player.teleport(player.getX(), player.getY() + 1);
			}
		}
	}

	private void attemptBendBar(Player player) {
		mes("You focus all of your strength on the bar. Your muscles ripple!");
		delay(3);
		if (player.getX() <= 89) {
			int attempt = DataConversions.random(0, 1);
			if (attempt == 0) { //fail-cell
				mes("You find it hard to bend the bar, perhaps you should try again?");
				delay(3);
				int stay = multi(player,
					"Yes, I'll try to bend the bar again.",
					"No, I'm going to give up.");
				if (stay == 0) {
					attemptBendBar(player);
				} else if (stay == 1) {
					mes("You decide to stay in the cell.");
					delay(3);
					mes("Maybe they'll let you out soon?");
					delay(3);
				}
			} else { //success-cell
				if (player.getCarriedItems().hasCatalogID(ItemId.ANA_IN_A_BARREL.id(), Optional.of(false))) {
					failWindowAnaInBarrel(player, null);
				} else {
					mes("You manage to bend the bar and climb out of the window.");
					delay(3);
					player.incExp(Skill.STRENGTH.id(), 40, true);
					player.teleport(90, 802);
					player.message("You land near some rough rocks, which you may be able to climb.");
				}
			}
		} else {
			int attempt = DataConversions.random(0, 1);
			if (attempt == 0) { //fail-hill
				mes("You find it hard to bend the bar, perhaps you should try again?");
				delay(3);
				int stay = multi(player,
					"Yes, I'll try to bend the bar again.",
					"No, I'm going to give up.");
				if (stay == 0) {
					attemptBendBar(player);
				} else if (stay == 1) {
					mes("You decide to stay in the cell.");
					delay(3);
					mes("Maybe they'll let you out soon?");
					delay(3);
				}
			} else { //success-hill
				if (player.getCarriedItems().hasCatalogID(ItemId.ANA_IN_A_BARREL.id(), Optional.of(false))) { //from the hill outside the window (fail-safe)
					failWindowAnaInBarrel(player, null);
				} else {
					mes("You manage to bend the bar !");
					delay(3);
					player.incExp(Skill.STRENGTH.id(), 40, true);
					player.teleport(89, 802);
					player.message("You climb back inside the cell.");
				}
			}
		}
	}

	private boolean succeedRate(Player player) {
		int random = DataConversions.getRandom().nextInt(5);
		if (random == 4 || random == 3) {
			return false;
		} else {
			return true;
		}
	}

	private Armed playerArmed(Player player) {
		for (int robes : allow) {
			allowed.add(robes);
		}
		for (int pos : restricted) {
			wieldPos.add(pos);
		}
		boolean hasArmour = false;
		boolean hasWeapon = false;
		int wieldpos;
		if (config().WANT_EQUIPMENT_TAB) {
			Item item;
			for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
				item = player.getCarriedItems().getEquipment().get(i);
				if (item == null)
					continue;
				if (item.getDef(player.getWorld()).getWieldPosition() > 5 && allowed.contains(item.getCatalogId()))
					continue;
				if (wieldPos.contains(item.getDef(player.getWorld()).getWieldPosition())) {
					if (item.getDef(player.getWorld()).getWieldPosition() == 3) {
						if (item.getDef(player.getWorld()).getName().toLowerCase().contains("shield"))
							hasArmour = true;
						else
							hasWeapon = true;
					} else if (item.getDef(player.getWorld()).getWieldPosition() == 4) {
						hasWeapon = true;
					} else {
						hasArmour = true;
					}
				}
			}
		} else {
			synchronized (player.getCarriedItems().getInventory().getItems()) {
				for (Item item : player.getCarriedItems().getInventory().getItems()) {
					if (item.isWielded() && item.getDef(player.getWorld()).getWieldPosition() > 5 && allowed.contains(item.getCatalogId())) {
						continue;
					}
					wieldpos = item.getDef(player.getWorld()).getWieldPosition();
					if (item.isWielded() && wieldPos.contains(wieldpos)) {
						if (wieldpos == 3) {
							if (item.getDef(player.getWorld()).getName().toLowerCase().contains("shield")) {
								hasArmour = true;
							} else {
								hasWeapon = true;
							}
						} else if (wieldpos == 4) {
							hasWeapon = true;
						} else {
							hasArmour = true;
						}
					}
				}
			}
		}

		if (hasWeapon && hasArmour) return Armed.BOTH;
		else if (hasWeapon) return Armed.WEAPON;
		else if (hasArmour) return Armed.ARMOUR;
		else return Armed.NONE;
	}

	private void delayedReturnSlave(Player player, Npc n) {
		try {
			player.getWorld().getServer().getGameEventHandler().add(
				new SingleEvent(player.getWorld(), null,
					config().GAME_TICK * 50,
					"Tourist Trap Delayed Return Slave", DuplicationStrategy.ALLOW_MULTIPLE) {
					@Override
					public void action() {
					changenpc(n, NpcId.MINING_SLAVE.id(), true);
				}
			});
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return item.getCatalogId() == ItemId.PROTOTYPE_THROWING_DART.id() && npc.getID() == NpcId.AL_SHABIM.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (npc.getID() == NpcId.AL_SHABIM.id() && player.getCarriedItems().hasCatalogID(ItemId.PROTOTYPE_THROWING_DART.id(), Optional.of(false))) {
			if (player.getQuestStage(this) == 7) {
				alShabimDialogue(player, npc, AlShabim.MADE_WEAPON);
			} else if (player.getQuestStage(this) > 7 || player.getQuestStage(this) == -1) {
				npcsay(player, npc, "Where did you get this from Effendi!",
					"I'll have to confiscate this for your own safety!");
				player.getCarriedItems().remove(new Item(ItemId.PROTOTYPE_THROWING_DART.id()));
			}
		}
	}

	enum Armed {
		NONE,
		ARMOUR,
		WEAPON,
		BOTH
	}

	class Irene {
		static final int WHENDIDSHEGO = 0;
		static final int WHATDIDSHEGO = 1;
		static final int REWARD = 2;
		static final int LOOKFORDAUGHTER = 3;
		static final int GETBACKDAUGHTER = 4;
	}

	class Mercenary {
		static final int THROW_PLAYER = 0;
		static final int PLACE_START = 1;
		static final int PLACE_SECOND = 2;
		static final int ORDER_KILL_PEOPLE = 3;
		static final int GUARDING_FIRST = 4;
		static final int GUARDING_SECOND = 5;
		static final int ANA_FIRST = 6;
		static final int ANA_SECOND = 7;
		static final int LEAVE_DESERT = 8;
		static final int THROW_PRISON = 9;
	}

	class MercenaryCaptain {
		static final int GUARDING = 0;
		static final int DONTSCAREME = 1;
		static final int MUSTBESOMETHINGICANDO = 2;
	}

	class Slave {
		static final int NEWRECRUIT = 0;
		static final int UNDOTHEM = 1;
		static final int GIVEITAGO = 2;
	}

	class MercenaryInside {
		static final int PINEAPPLES = 0;
		static final int UNDERSTAND = 1;
	}

	class BedabinNomad {
		static final int JUGOFWATER = 0;
		static final int FULLWATERSKIN = 1;
		static final int BUCKETOFWATER = 2;
		static final int SHANTAYPASS = 3;
		static final int PLACE = 4;
	}

	class AlShabim {
		static final int WHATISTHISPLACE = 0;
		static final int HAVE_PLANS = 1;
		static final int MADE_WEAPON = 2;
	}

	class Siad {
		static final int PREPARETODIE = 0;
		static final int SLAVESBROKENFREE = 1;
		static final int FIREFIRE = 2;
		static final int TWOMINUTES = 3;
		static final int ERM = 4;
		static final int SERVICE = 5;
		static final int DRAGON = 6;
		static final int LONELY = 7;
		static final int PLANS = 8;
		static final int SUCCEED = 9;
		static final int BOOKS = 10;
		static final int PUNISHED = 11;
	}

	class Ana {
		static final int TRYGETYOUOUTOFHERE = 0;
		static final int GOTTHAT = 1;
		static final int SNEAKEDPAST = 2;
		static final int GUARDSRUBBISH = 3;
	}
}
