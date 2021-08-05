package com.openrsc.server.plugins.authentic.quests.members.legendsquest.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestEchnedZekin implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.ECHNED_ZEKIN.id();
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (n.getID() == NpcId.ECHNED_ZEKIN.id()) {
			echnedDialogue(player, n, -1);
		}
	}

	private void holyForceSpell(Player player, Npc n) {
		//not sure if this line is correct
		//message(player, n, config().GAME_TICK * 2, "You quickly grab the Holy Force Spell and cast it at the Demon.");
		mes("You thrust the Holy Force spell in front of the spirit.");
		delay(3);
		mes(n, "A bright, holy light streams out from the paper spell.");
		delay(2);
		if (player.getCache().hasKey("already_cast_holy_spell")) {
			npcsay(player, n, "Argghhhhh...not again....!");
		} else {
			npcsay(player, n, "Argghhhhh...noooooo!");
			player.getCache().store("already_cast_holy_spell", true);
		}
	}

	private void neziAttack(Player player, Npc n, boolean useHolySpell) {
		if (player.getCache().hasKey("ran_from_2nd_nezi")) {
			npcsay(player, n, "You have returned and I am ready for you...");
		}
		npcsay(player, n, "I will now reveal myself and spell out your doom.");
		int formerNpcX = n.getX();
		int formerNpcY = n.getY();
		if (n != null)
			n.remove();
		Npc second_nezikchened = addnpc(NpcId.NEZIKCHENED.id(), formerNpcX, formerNpcY, (int)TimeUnit.SECONDS.toMillis(500), player);
		if (second_nezikchened != null) {
			if (useHolySpell) {
				holyForceSpell(player, second_nezikchened);
				mes(second_nezikchened, "The spirit lets out an unearthly, blood curdling scream...");
				delay(2);
				mes(second_nezikchened, "The spell seems to weaken the Demon.");
				delay();
				second_nezikchened.getSkills().setLevel(Skill.DEFENSE.id(), second_nezikchened.getSkills().getLevel(Skill.DEFENSE.id()) - 5);
			}
			second_nezikchened.startCombat(player);
			if (useHolySpell) {
				int newPray = (int) Math.ceil((double) player.getSkills().getLevel(Skill.PRAYER.id()) / 2);
				if (player.getSkills().getLevel(Skill.PRAYER.id()) - newPray < 30) {
					mes("A sense of fear comes over you ");
					delay(2);
					mes("You feel a sense of loss...");
					delay(2);
				} else {
					mes("An intense sense of fear comes over you ");
					delay(2);
					mes("You feel a great sense of loss...");
					delay(2);
				}
				player.getSkills().setLevel(Skill.PRAYER.id(), newPray);

				delay(11);
				mes("The Demon takes out a dark dagger and throws it at you...");
				delay(2);
				if (DataConversions.random(0, 1) == 1) {
					mes("The dagger hits you with an agonising blow...");
					delay(2);
					player.damage(14);
				} else {
					mes("But you neatly manage to dodge the attack.");
					delay();
				}
			} else {
				mes("A terrible fear comes over you. ");
				delay(2);
				mes("You feel a terrible sense of loss...");
				delay(2);
				player.getSkills().setLevel(Skill.PRAYER.id(), 0);
			}
		}
	}

	private void echnedDialogue(Player player, Npc n, int cID) {
		if (n.getID() == NpcId.ECHNED_ZEKIN.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(Quests.LEGENDS_QUEST)) {
					case 7:
						/**
						 * HAS HOLY FORCE SPELL.
						 */
						if (player.getCache().hasKey("gave_glowing_dagger")) {
							neziAttack(player, n, player.getCarriedItems().hasCatalogID(ItemId.HOLY_FORCE_SPELL.id(), Optional.of(false)));
							return;
						}
						if (player.getCarriedItems().hasCatalogID(ItemId.HOLY_FORCE_SPELL.id(), Optional.of(false))) {
							npcsay(player, n, "Something seems different about you...",
								"Your sense of purpose seems not bent to my will...",
								"Give me the dagger that you used to slay Viyeldi or taste my wrath!");
							int forceMenu = multi(player, n,
								"I don't have the dagger.",
								"I haven't slayed Viyeldi yet.",
								"I have something else in mind!",
								"I have to be going...");
							if (forceMenu == 0) {
								echnedDialogue(player, n, Echned.I_DONT_HAVE_THE_DAGGER);
							} else if (forceMenu == 1) {
								echnedDialogue(player, n, Echned.I_HAVE_NOT_SLAYED_VIYELDI_YET);
							} else if (forceMenu == 2) {
								echnedDialogue(player, n, Echned.I_HAVE_SOMETHING_ELSE_IN_MIND);
							} else if (forceMenu == 3) {
								echnedDialogue(player, n, Echned.I_HAVE_TO_BE_GOING);
							}
						}
						/**
						 * HAS DARK GLOWING DAGGER - KILLED VIYELDI
						 */
						else if (player.getCarriedItems().hasCatalogID(ItemId.GLOWING_DARK_DAGGER.id(), Optional.of(false))
						&& !player.getCarriedItems().hasCatalogID(ItemId.HOLY_FORCE_SPELL.id(), Optional.of(false))) {
							npcsay(player, n, "Aha, I see you have completed your task. ",
								"I'll take that dagger from you now.");
							player.getCarriedItems().remove(new Item(ItemId.GLOWING_DARK_DAGGER.id()));
							if (!player.getCache().hasKey("gave_glowing_dagger")) {
								player.getCache().store("gave_glowing_dagger", true);
							}
							mes(n, "The formless shape of Echned Zekin takes the dagger from you.");
							delay(2);
							mes(n, "As a ghostly hand envelopes the dagger, something seems to move");
							delay(2);
							mes(n, "from the black weapon into the floating figure...");
							delay(2);
							npcsay(player, n, "Aahhhhhhhhh! As I take the spirit of one departed,",
								"I will now reveal myself and spell out your doom.");
							mes(n, "A terrible fear comes over you. ");
							delay(2);
							int formerNpcX = n.getX();
							int formerNpcY = n.getY();
							if (n != null)
								n.remove();
							Npc second_nezikchened = addnpc(NpcId.NEZIKCHENED.id(), formerNpcX, formerNpcY, (int)TimeUnit.SECONDS.toMillis(500), player);
							if (second_nezikchened != null) {
								delay();
								second_nezikchened.startCombat(player);
								player.message("You feel a terrible sense of loss...");
								player.getSkills().setLevel(Skill.PRAYER.id(), 0);
							}
						}
						/**
						 * HAS THE DARK DAGGER
						 */
						else if (player.getCarriedItems().hasCatalogID(ItemId.DARK_DAGGER.id(), Optional.of(false))
						&& !player.getCarriedItems().hasCatalogID(ItemId.GLOWING_DARK_DAGGER.id(), Optional.of(false))
						&& !player.getCarriedItems().hasCatalogID(ItemId.HOLY_FORCE_SPELL.id(), Optional.of(false))) {
							mes("The shapeless entity of Echned Zekin appears in front of you.");
							delay(3);
							npcsay(player, n, "Why do you return when your task is still incomplete?");
							mes("There is an undercurrent of anger in his voice.");
							delay(3);
							int menu = multi(player, n,
								"Who am I supposed to kill again?",
								"Er I've had second thoughts.",
								"I have to be going...");
							if (menu == 0) {
								echnedDialogue(player, n, Echned.WHO_AM_I_SUPPOSED_TO_KILL_AGAIN);
							} else if (menu == 1) {
								echnedDialogue(player, n, Echned.ER_IVE_HAD_SECOND_THOUGHTS);
							} else if (menu == 2) {
								echnedDialogue(player, n, Echned.I_HAVE_TO_BE_GOING);
							}
						} else {
							/**
							 * NO DARK DAGGER - Default dialogue.
							 */
							mes(n, "In a rasping, barely audible voice you hear the entity speak.");
							delay(2);
							npcsay(player, n, "Who disturbs the rocks of Zekin?");
							mes(n, "There seems to be something slightly familiar about this presence.");
							delay(2);
							int menu = multi(player, n,
								"Er...me?",
								"Who's asking?");
							if (menu == 0) {
								npcsay(player, n, "So, you desire the water that flows here?");
								int opt1 = multi(player, n,
									"Yes, I need it for my quest.",
									"Not really, I just wondered if I could push that big rock.");
								if (opt1 == 0) {
									npcsay(player, n, "The water babbles so loudly and I am already so tortured.",
										"I cannot abide the sound so I have stoppered the streams...",
										"Care you not for my torment and pain?");
									int opt4 = multi(player, n,
										"Why are you tortured?",
										"What can I do about that?");
									if (opt4 == 0) {
										echnedDialogue(player, n, Echned.WHY_ARE_YOU_TORTURED);
									} else if (opt4 == 1) {
										echnedDialogue(player, n, Echned.WHAT_CAN_I_DO_ABOUT_THAT);
									}
								} else if (opt1 == 1) {
									npcsay(player, n, "The rock must remain, it stoppers the waters that babble.",
										"The noise troubles my soul and I seek some rest...",
										"rest from this terrible torture...");
									int opt2 = multi(player, n,
										"Why are you tortured?",
										"What can I do about that?");
									if (opt2 == 0) {
										echnedDialogue(player, n, Echned.WHY_ARE_YOU_TORTURED);
									} else if (opt2 == 1) {
										echnedDialogue(player, n, Echned.WHAT_CAN_I_DO_ABOUT_THAT);
									}
								}
							} else if (menu == 1) {
								mes(n, "The hooded, headless figure faces you...it's quite unnerving..");
								delay(2);
								npcsay(player, n, "I am Echned Zekin...and I seek peace from my eternal torture...");
								int opt3 = multi(player, n,
									"What can I do about that?",
									"Do I know you?",
									"Why are you tortured?");
								if (opt3 == 0) {
									echnedDialogue(player, n, Echned.WHAT_CAN_I_DO_ABOUT_THAT);
								} else if (opt3 == 1) {
									npcsay(player, n, "I am long since dead and buried, lost in the passages of time.",
										"Long since have my kin departed and have I been forgotten...",
										"It is unlikely that you know me...",
										"I am a poor tortured soul looking for rest and eternal peace...");
									int opt5 = multi(player, n,
										"Why are you tortured?",
										"What can I do about that?");
									if (opt5 == 0) {
										echnedDialogue(player, n, Echned.WHY_ARE_YOU_TORTURED);
									} else if (opt5 == 1) {
										echnedDialogue(player, n, Echned.WHAT_CAN_I_DO_ABOUT_THAT);
									}
								} else if (opt3 == 2) {
									echnedDialogue(player, n, Echned.WHY_ARE_YOU_TORTURED);
								}
							}
						}
						break;
				}
			}
			switch (cID) {
				case Echned.WHAT_CAN_I_DO_ABOUT_THAT:
					npcsay(player, n, "I was brutally murdered by a viscious man called Viyeldi",
						"I sense his presence near by, but I know that he is no longer living",
						"My spirit burns with the need for revenge, I shall not rest while",
						"I sense his spirit still.",
						"If you seek the pure water, you must ensure he meets his end.",
						"If not, you will never see the source and your journey back must ye start.",
						"What is your answer? Will ye put an end to Viyeldi for me?");
					int sub_menu2 = multi(player, n,
						"I'll do what I must to get the water.",
						"No, I won't take someone's life for you.");
					if (sub_menu2 == 0) {
						echnedDialogue(player, n, Echned.I_WILL_DO_WHAT_I_MUST_TO_GET_THE_WATER);
					} else if (sub_menu2 == 1) {
						echnedDialogue(player, n, Echned.I_WONT_TAKE_SOMEONES_LIFE_FOR_YOU);
					}
					break;
				case Echned.WHY_ARE_YOU_TORTURED:
					npcsay(player, n, "I was robbed of my life by a cruel man called Viyeldi",
						"And I hunger for revenge upon him....",
						"It is long since I have walked this world looking for him",
						"to haunt him and raise terror in his life...",
						"but tragedy of tragedies, his spirit is neither living or dead",
						"he serves the needs of the source.",
						"He died trying to collect the water from this stream,",
						"and now I hang in torment for eternity.");
					int sub_menu = multi(player, n,
						"What can I do about that?",
						"Can't I just get some water?");
					if (sub_menu == 0) {
						echnedDialogue(player, n, Echned.WHAT_CAN_I_DO_ABOUT_THAT);
					} else if (sub_menu == 1) {
						npcsay(player, n, "Yes, you may get some water, but first you must help me.",
							"Revenge is the only thing that keeps my spirit in this place",
							"help me take vengeance on Viyeldi and I will gladly remove",
							"the rocks and allow you access to the water",
							"What say you?");
						int sub_menu3 = multi(player, n,
							"I'll do what I must to get the water.",
							"No, I won't take someone's life for you.");
						if (sub_menu3 == 0) {
							echnedDialogue(player, n, Echned.I_WILL_DO_WHAT_I_MUST_TO_GET_THE_WATER);
						} else if (sub_menu3 == 1) {
							echnedDialogue(player, n, Echned.I_WONT_TAKE_SOMEONES_LIFE_FOR_YOU);
						}
					}
					break;
				case Echned.ILL_DO_IT:
					player.message("The formless shape shimmers brightly...");
					npcsay(player, n, "You will benefit from this decision, the source will be",
						"opened to you.",
						"Bring the dagger back to me when you have completed this task.");
					if (n != null) {
						n.remove();
					}
					break;
				case Echned.I_WONT_TAKE_SOMEONES_LIFE_FOR_YOU:
					npcsay(player, n, "Such noble thoughts, but Viyeldi is not alive.",
						"He is merely a vessel by which the power of the source ",
						"protects itself. ",
						"If that is your decision, so be it, but expect not to ",
						"gain the water from this stream.");
					if (n != null) {
						n.remove();
					}
					break;
				case Echned.I_WILL_DO_WHAT_I_MUST_TO_GET_THE_WATER:
					mes(n, "The shapeless spirit seems to crackle with energy.");
					delay(2);
					npcsay(player, n, "You would release me from my torment and the source would",
						"be available to you.",
						"However, you must realise that this will be no easy task.");
					if (!player.getCarriedItems().hasCatalogID(ItemId.DARK_DAGGER.id(), Optional.of(false))) {
						npcsay(player, n, "I will furnish you with a weapon which will help you",
							"to achieve your aims...",
							"Here, take this...");
						player.message("The spiritless body waves an arm and in front of you appears");
						player.message("a dark black dagger made of pure obsidian.");
						npcsay(player, n, "To complete this task you must use this weapon on Viyeldi.");
						give(player, ItemId.DARK_DAGGER.id(), 1);
						player.message("You take the dagger and place it in your inventory.");
						if (!player.getCache().hasKey("met_spirit")) {
							player.getCache().store("met_spirit", true);
						}
					}
					npcsay(player, n, "Use the dagger I have provided for you to complete this task.",
						"and then bring it to me when Viyeldi is dead.");
					int sub_menu4 = multi(player, n,
						"Ok, I'll do it.",
						"I've changed my mind, I can't do it.",
						"No, I won't take someone's life for you.");
					if (sub_menu4 == 0) {
						echnedDialogue(player, n, Echned.ILL_DO_IT);
					} else if (sub_menu4 == 1) {
						npcsay(player, n, "The pure water you seek will forever be out of your reach.");
						say(player, n, "I'll do what I must to get the water.");
						player.message("The shapeless spirit seems to crackle with energy.");
						npcsay(player, n, "You would release me from my torment and the source would",
								"be available to you.",
								"However, you must realise that this will be no easy task.",
								"Use the dagger I have provided for you to complete this task.",
								"and then bring it to me when Viyeldi is dead.");
						int sub_menu5 = multi(player, n, "Ok, I'll do it.",
								"I've changed my mind, I can't do it.");
						if (sub_menu5 == 0) {
							echnedDialogue(player, n, Echned.ILL_DO_IT);
						} else if (sub_menu5 == 1) {
							npcsay(player, n, "The decision is yours but you will have no other way to ",
									"get to the source.",
									"The pure water you seek will forever be out of your reach.");
								int sub_menu6 = multi(player, n,
									"I'll do what I must to get the water.",
									"No, I won't take someone's life for you.");
								if (sub_menu6 == 0) {
									echnedDialogue(player, n, Echned.I_WILL_DO_WHAT_I_MUST_TO_GET_THE_WATER);
								} else if (sub_menu6 == 1) {
									echnedDialogue(player, n, Echned.I_WONT_TAKE_SOMEONES_LIFE_FOR_YOU);
								}
						}
					} else if (sub_menu4 == 2) {
						echnedDialogue(player, n, Echned.I_WONT_TAKE_SOMEONES_LIFE_FOR_YOU);
					}
					break;
				case Echned.WHO_AM_I_SUPPOSED_TO_KILL_AGAIN:
					npcsay(player, n, "Avenge upon me the death of Viyeldi, the cruel.",
						"And I will give you access to source...");
					int new_menu = multi(player, n,
						"Er I've had second thoughts.",
						"I have to be going...");
					if (new_menu == 0) {
						echnedDialogue(player, n, Echned.ER_IVE_HAD_SECOND_THOUGHTS);
					} else if (new_menu == 1) {
						echnedDialogue(player, n, Echned.I_HAVE_TO_BE_GOING);
					}
					break;
				case Echned.ER_IVE_HAD_SECOND_THOUGHTS:
					npcsay(player, n, "It is too late for second thoughts...",
						"Do as you have agreed and return to me in all haste...",
						"His presence tortures me so...");
					int thoughts = multi(player, n,
						"Who am I supposed to kill again?",
						"I have to be going...");
					if (thoughts == 0) {
						echnedDialogue(player, n, Echned.WHO_AM_I_SUPPOSED_TO_KILL_AGAIN);
					} else if (thoughts == 1) {
						echnedDialogue(player, n, Echned.I_HAVE_TO_BE_GOING);
					}
					break;
				case Echned.I_HAVE_TO_BE_GOING:
					npcsay(player, n, "Return swiftly with the weapon as soon as your task is complete.");
					player.message("The spirit slowly fades and then disapears.");
					if (n != null) {
						n.remove();
					}
					break;
				case Echned.I_DONT_HAVE_THE_DAGGER:
					mes(n, "The spirit seems to shake with anger...");
					delay(2);
					npcsay(player, n, "Bring it to me with all haste.",
						"Or torment and pain will I bring to you...",
						"the spirit extends a wraithlike finger which touches you.",
						"You feel a searing pain jolt through your body...");
					player.damage(DataConversions.random(8, 15));
					int c_menu = multi(player, n,
						"I haven't slayed Viyeldi yet.",
						"I have something else in mind!",
						"I have to be going...");
					if (c_menu == 0) {
						echnedDialogue(player, n, Echned.I_HAVE_NOT_SLAYED_VIYELDI_YET);
					} else if (c_menu == 1) {
						echnedDialogue(player, n, Echned.I_HAVE_SOMETHING_ELSE_IN_MIND);
					} else if (c_menu == 2) {
						echnedDialogue(player, n, Echned.I_HAVE_TO_BE_GOING);
					}
					break;
				case Echned.I_HAVE_NOT_SLAYED_VIYELDI_YET:
					npcsay(player, n, "Go now and slay him, as you agreed.",
						"If you are forfeit on this.",
						"And I will take you as a replacement for Viyeldi !");
					int b_menu = multi(player, n,
						"I don't have the dagger.",
						"I have something else in mind!",
						"I have to be going...");
					if (b_menu == 0) {
						echnedDialogue(player, n, Echned.I_DONT_HAVE_THE_DAGGER);
					} else if (b_menu == 1) {
						echnedDialogue(player, n, Echned.I_HAVE_SOMETHING_ELSE_IN_MIND);
					} else if (b_menu == 2) {
						echnedDialogue(player, n, Echned.I_HAVE_TO_BE_GOING);
					}
					break;
				case Echned.I_HAVE_SOMETHING_ELSE_IN_MIND:
					npcsay(player, n, "You worthless Vacu, how dare you seek to trick me.",
						"Go and slay Viyeldi as you promised ",
						"or I will layer upon you all the pain and ",
						"torment I have endured all these long years!");
					int a_menu = multi(player, n,
						"I don't have the dagger.",
						"I haven't slayed Viyeldi yet.",
						"I have to be going...");
					if (a_menu == 0) {
						echnedDialogue(player, n, Echned.I_DONT_HAVE_THE_DAGGER);
					} else if (a_menu == 1) {
						echnedDialogue(player, n, Echned.I_HAVE_NOT_SLAYED_VIYELDI_YET);
					} else if (a_menu == 2) {
						echnedDialogue(player, n, Echned.I_HAVE_TO_BE_GOING);
					}
					break;
			}
		}
	}

	class Echned {
		static final int WHAT_CAN_I_DO_ABOUT_THAT = 0;
		static final int WHY_ARE_YOU_TORTURED = 1;
		static final int I_WONT_TAKE_SOMEONES_LIFE_FOR_YOU = 2;
		static final int I_WILL_DO_WHAT_I_MUST_TO_GET_THE_WATER = 3;
		static final int ER_IVE_HAD_SECOND_THOUGHTS = 4;
		static final int I_HAVE_TO_BE_GOING = 5;
		static final int WHO_AM_I_SUPPOSED_TO_KILL_AGAIN = 6;
		static final int I_HAVE_SOMETHING_ELSE_IN_MIND = 7;
		static final int I_HAVE_NOT_SLAYED_VIYELDI_YET = 8;
		static final int I_DONT_HAVE_THE_DAGGER = 9;
		static final int ILL_DO_IT = 10;
	}
}
