package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.openrsc.server.plugins.Functions.*;

public class MerlinsCrystal implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger,
	OpBoundTrigger,
	KillNpcTrigger,
	UseLocTrigger,
	DropObjTrigger {

	private static final Logger LOGGER = LogManager.getLogger(MerlinsCrystal.class);

	@Override
	public int getQuestId() {
		return Quests.MERLINS_CRYSTAL;
	}

	@Override
	public String getQuestName() {
		return "Merlin's crystal (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.MERLINS_CRYSTAL.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.getCache().remove("magic_words");
		mez("WellDoneYouHaveCompletedMerlinsCrystal");
		final QuestReward reward = Quest.MERLINS_CRYSTAL.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return (obj.getID() == 292 || obj.getID() == 293)
			|| obj.getID() == 291
			|| (obj.getID() == 296 && obj.getY() == 366 && command
			.equalsIgnoreCase("search")) || obj.getID() == 295;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 292 || obj.getID() == 293) {
			Npc arhein = ifnearvisnpc(player, NpcId.ARHEIN.id(), 10);
			if (player.getQuestStage(this) >= 0 && player.getQuestStage(this) < 2) {
				mez("MerlinsCrystalIHaveNoReasonToDoThat");
			} else if (arhein != null) {
				npcsay(player, arhein, player.getText("MerlinsCrystalArheinOiGetAwayFromThere"));
			} else {
				player.teleport(456, 3352, false);
				mez("MerlinsCrystalYouHideAwayInTheShip");
				delay(2);
				mez("MerlinsCrystalTheShipStartsToMove");
				delay(5);
				mez("MerlinsCrystalYouAreOutAtSea");
				delay(5);
				mez("MerlinsCrystalTheShipComesToAStop");
				player.teleport(456, 520, false);
				mez("MerlinsCrystalYouSneakOutOfTheShip");
				delay(3);
			}
		} else if (obj.getID() == 291) {
			mez("MerlinsCrystalThereAreBucketsInCrate");
			delay(2);
			mez("MerlinsCrystalWouldYouLikeABucket");
			int opt = multi(player, player.getText("CapitalYes0"), player.getText("CapitalNo0"));
			if (opt == 0) {
				mez("MerlinsCrystalYouTakeABucket");
				give(player, ItemId.BUCKET.id(), 1);
			}
		} else if (obj.getID() == 296) {
			mez("MerlinsCrystalYouFindASmallInscription");
			delay(3);
			mez("MerlinsCrystalSnarthonCandtrickTermanto");
			delay(3);
			if (!player.getCache().hasKey("magic_words")) {
				player.getCache().store("magic_words", true);
			}
		} else if (obj.getID() == 295) {
			player.teleport(player.getX(), player.getY() + 944);
			mez("YouClimbUpTheLadder");
			if ((player.getQuestStage(this) >= 0 && player.getQuestStage(this) < 3) || !player.getCache().hasKey("lady_test")) {
				return;
			}
			delay();
			Npc lady = ifnearvisnpc(player, NpcId.LADY_UPSTAIRS.id(), 5);
			if (lady == null) {
				lady = addnpc(player.getWorld(), NpcId.LADY_UPSTAIRS.id(), 279, 1576, (int)TimeUnit.SECONDS.toMillis(74));
			}
			delay();
			if (lady != null) {
				say(player, lady, "Hello I am here, can I have Excalibur yet?");
				npcsay(player, lady, "I don't think you are worthy enough",
					"Come back when you are a better person");
			}
		}
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.SIR_MORDRED.id();
	}

	@Override
	public void onKillNpc(Player player, Npc npc) {
		if (player.getQuestStage(this) > 0) {
			if (npc.getCombatEvent() != null) {
				npc.getCombatEvent().resetCombat();
			}
			// from replay should do full heal
			npc.getSkills().setLevel(Skill.HITS.id(), npc.getDef().hits);
			Npc leFaye = ifnearvisnpc(player, NpcId.MORGAN_LE_FAYE.id(), 8);
			if (leFaye == null) {
				leFaye = addnpc(player.getWorld(), NpcId.MORGAN_LE_FAYE.id(), 461, 2407, (int)TimeUnit.SECONDS.toMillis(63));
			}
			delay();
			npcsay(player, leFaye, "Please spare my son");
			int option = multi(player, leFaye, "Tell me how to untrap Merlin and I might",
				"No he deserves to die", "OK then");
			if (option == 0) {
				if (player.getQuestStage(this) == 2) {
					player.updateQuestStage(this, 3);
				}
				npcsay(player, leFaye,
					"You have guessed correctly that I'm responsible for that");
				npcsay(player, leFaye,
					"I suppose I can live with that fool Merlin being loose");
				npcsay(player, leFaye, "for the sake of my son");
				npcsay(player, leFaye, "Setting him free won't be easy though");
				npcsay(player, leFaye,
					"You will need to find a pentagram as close to the crystal as you can find");
				npcsay(player, leFaye,
					"You will need to drop some bats bones in the pentagram");
				npcsay(player, leFaye, "while holding a black candle");
				npcsay(player, leFaye, "This will summon the demon Thrantax");
				npcsay(player, leFaye, "You will need to bind him with magic words");
				npcsay(player, leFaye,
					"Then you will need the sword Excalibur with which the spell was bound");
				npcsay(player, leFaye, "Shatter the crystal with Excalibur");
				int sub_opt = multi(player, leFaye, "So where can I find Excalibur?",
					"OK I will do all that", "What are the magic words?");
				if (sub_opt == 0) {
					npcsay(player, leFaye, "The lady of the lake has it");
					npcsay(player, leFaye, "I don't know if she will give it you though");
					npcsay(player, leFaye, "She can be rather temperamental");
					int sub_opt2 = multi(player, leFaye, false, //do not send over
						"OK I will go do all that",
						"What are the magic words?");
					if (sub_opt2 == 0) {
						say(player, leFaye, "OK I will do all that");
						player.message("Morgan Le Faye vanishes");
					} else if (sub_opt2 == 1) {
						say(player, leFaye, "What are the magic words?");
						npcsay(player, leFaye,
							"You will find the magic words at the base of one of the chaos altars");
						npcsay(player, leFaye, "Which chaos altar I cannot remember");
					}
				} else if (sub_opt == 1) {
					player.message("Morgan Le Faye vanishes");
				} else if (sub_opt == 2) {
					npcsay(player, leFaye,
						"You will find the magic words at the base of one of the chaos altars");
					npcsay(player, leFaye, "Which chaos altar I cannot remember");
				}
				npc.killed = false;
			} else if (option == 1) {
				player.message("You kill Mordred");
				npc.remove();
				return;
			} else if (option == 2) {
				player.message("Morgan Le Faye vanishes");
				npc.killed = false;
			} else if (option == -1) {
				npc.killed = false;
			}
			npc.killed = false;
		} else {
			player.getWorld().registerItem(
				new GroundItem(player.getWorld(), ItemId.BONES.id(), npc.getX(), npc.getY(), 1, player));
			npc.remove();
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == 294 || obj.getID() == 287 && item.getCatalogId() == ItemId.EXCALIBUR.id();
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == 294) {
			if (item.getCatalogId() == ItemId.INSECT_REPELLANT.id()) {
				mes("you squirt insect repellant on the beehive");
				delay(3);
				mes("You see bees leaving the hive");
				delay(3);
				if (!player.getCache().hasKey("squirt")) {
					player.getCache().store("squirt", true);
				}
			} else if (item.getCatalogId() == ItemId.BUCKET.id()) {
				mes("You try to get some wax from the beehive");
				delay(3);
				if (player.getCache().hasKey("squirt")) {
					mes("You get some wax from the hive");
					delay(3);
					mes("The bees fly back to the hive as the repellant wears off");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.BUCKET.id()));
					give(player, ItemId.WAX_BUCKET.id(), 1);
					player.getCache().remove("squirt");
				} else {
					player.message("Suddenly bees fly out of the hive and sting you");
					player.damage(2);
				}
			}
		} else if (obj.getID() == 287 && item.getCatalogId() == ItemId.EXCALIBUR.id()) {
			if (player.getQuestStage(this) == 4) {
				mes("The crystal shatters");
				delay(3);
				player.getWorld().unregisterGameObject(obj);
				player.getWorld().delayedSpawnObject(obj.getLoc(), (int)TimeUnit.SECONDS.toMillis(32));
				Npc merlin = ifnearvisnpc(player, NpcId.MERLIN_CRYSTAL.id(), 10);
				if (merlin != null) {
					npcsay(player, merlin, "Thankyou thankyou",
						"It's not fun being trapped in a giant crystal",
						"Go speak to King Arthur, I'm sure he'll reward you");

					player.message("You have set Merlin free now talk to king arthur");
					player.updateQuestStage(this, 5);
				}
			} else {
				player.message("Nothing interesting happens");
			}
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return obj.getX() == 277 && obj.getY() == 632;
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getX() == 277 && obj.getY() == 632) {
			if ((player.getQuestStage(this) >= 0 && player.getQuestStage(this) < 3) || !player.getCache().hasKey("lady_test")) {
				doDoor(obj, player);
				return;
			} else {
				Npc beggar = ifnearvisnpc(player, NpcId.BEGGAR.id(), 5);
				if (beggar == null) {
					beggar = addnpc(player.getWorld(), NpcId.BEGGAR.id(), 276, 631, (int)TimeUnit.SECONDS.toMillis(74));
				}
				delay();
				if (beggar != null) {
					npcsay(player, beggar, "Please sir, me and my family are starving",
						"Could you possibly give me a loaf of bread?");
					int opt = multi(player, beggar, "Yes certainly",
						"No I don't have any bread with me");
					if (opt == 0) {
						if (!player.getCarriedItems().hasCatalogID(ItemId.BREAD.id())) {
							say(player, beggar,
								"Except that I don't have any bread at the moment");
							npcsay(player, beggar,
								"Well if you get some you know where to come");
							doDoor(obj, player);
							beggar.remove();
						} else {
							mes("You give the bread to the beggar");
							delay(3);
							player.getCarriedItems().remove(new Item(ItemId.BREAD.id()));
							npcsay(player, beggar, "Thankyou very much");
							if (player.getCache().hasKey("lady_test")) {
								player.message("The beggar has turned into the lady of the lake!");
								Npc lady = changenpc(beggar, NpcId.LADY_GROUND.id(), true);
								delayedRemoveLady(player, lady);
								npcsay(player, lady, "Well done you have passed my test",
									"Here is Excalibur, guard it well");
								give(player, ItemId.EXCALIBUR.id(), 1);
								player.getCache().remove("lady_test");
							} else {
								doDoor(obj, player);
								beggar.remove();
							}
						}
					} else if (opt == 1) {
						npcsay(player, beggar,
							"Well if you get some you know where to come");
						doDoor(obj, player);
						beggar.remove();
					}
				} else {
					doDoor(obj, player);
				}
			}
		}

	}

	private void delayedRemoveLady(Player player, Npc n) {
		try {
			player.getWorld().getServer().getGameEventHandler().add(
				new SingleEvent(player.getWorld(), null,
					config().GAME_TICK * 116,
					"Lady Lakes Bread Delayed Remove", DuplicationStrategy.ALLOW_MULTIPLE) {
					@Override
					public void action() {
						n.remove();
					}
				});
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	@Override
	public boolean blockDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		return player.getX() == 448 && player.getY() == 435 && item.getCatalogId() == ItemId.BAT_BONES.id()
			&& player.getCache().hasKey("magic_words") && player.getCarriedItems().hasCatalogID(ItemId.LIT_BLACK_CANDLE.id(), Optional.of(false));
	}

	@Override
	public void onDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		Npc n = addnpc(player.getWorld(), NpcId.THRANTAX.id(), 448, 435, (int)TimeUnit.SECONDS.toMillis(63));
		n.displayNpcTeleportBubble(n.getX(), n.getY());
		player.message("Suddenly a demon appears");
		say(player, null, "Now what were those magic words?");
		int opt = multi(player, n, false, //do not send over
			"Snarthtrick Candanto Termon",
			"Snarthon Candtrick Termanto", "Snarthanto Candon Termtrick");
		if (opt == 1) {
			say(player, n, "Snarthon Candtrick Termanto");
			npcsay(player, n, "rarrrrgh", "You have me in your control",
				"What do you wish of me?",
				"So that I may return to the nether regions");
			say(player, n, "I wish to free Merlin from his giant crystal");
			npcsay(player, n, "rarrrrgh",
				"It is done, you can now shatter Merlins crystal with Excalibur");
			n.remove();
			player.updateQuestStage(this, 4);
			return;
		}
		if (opt == 0) {
			say(player, n, "Snarthtrick Candato Termon");
		} else if (opt == 2) {
			say(player, n, "Snarthanto Candon Termtrick");
		}
		n.getUpdateFlags().setChatMessage(new ChatMessage(n, "rarrrrgh", player));
		if (player.getCarriedItems().hasCatalogID(ItemId.LIT_BLACK_CANDLE.id(), Optional.of(false))) {
			player.getCarriedItems().remove(new Item(ItemId.LIT_BLACK_CANDLE.id()));
		}
		n.startCombat(player);
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return (n.getID() == NpcId.KING_ARTHUR.id() && !player.getLocation().inVarrock())
			|| n.getID() == NpcId.SIR_GAWAIN.id() || n.getID() == NpcId.SIR_LANCELOT.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.KING_ARTHUR.id() && !player.getLocation().inVarrock()) {
			switch (player.getQuestStage(Quests.THE_HOLY_GRAIL)) {
				case 1:
				case 2:
				case 3:
				case 5:
					npcsay(player, n, "How goes thy quest?");
					if (player.getCarriedItems().hasCatalogID(ItemId.HOLY_GRAIL.id(), Optional.of(false))) {
						say(player, n, "I have retrieved the grail");
						npcsay(player, n, "wow incredible you truly are a splendid knight");
						player.getCarriedItems().remove(new Item(ItemId.HOLY_GRAIL.id()));
						player.sendQuestComplete(Quests.THE_HOLY_GRAIL);
					} else {
						say(player, n, "I am making progress",
							"But I have not recovered the grail yet");
						npcsay(player, n, "Well the grail is very elusive",
							"It may take some perserverance");
						if (player.getQuestStage(Quests.THE_HOLY_GRAIL) == 1) {
							npcsay(player, n, "As I said before speak to Merlin",
								"in the workshop by the library");
						}
					}
					return;
				case 4:
					say(player, n, "Hello, do you have a knight named Sir Percival?");
					npcsay(player, n, "Ah yes I remember, young percival",
						"He rode off on a quest a couple of months ago",
						"We are getting a bit worried, he's not back yet",
						"He was going to try and recover the golden boots of Arkaneeses");
					say(player, n, "Any idea which way that would be?");
					npcsay(player, n, "Not exactly",
						"We discovered, some magic golden feathers",
						"They are said to point the way to the boots",
						"they certainly point somewhere",
						"just blowing gently on them",
						"Will make them show the way to go");
					if (!player.getCarriedItems().hasCatalogID(ItemId.MAGIC_GOLDEN_FEATHER.id(), Optional.of(false))) {
						player.message("King arthur gives you a feather");
						give(player, ItemId.MAGIC_GOLDEN_FEATHER.id(), 1);
					}
					return;
				case -1:
					npcsay(player, n, "Thankyou for retrieving the grail",
						"You shall be long remembered",
						"As one of the greatest heros",
						"Amongst the knights of the round table");
					return;
			}
			/** KING ARTHUR MERLINS CRYSTAL**/
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
					npcsay(player, n, "Welcome to the court of King Arthur");
					npcsay(player, n, "I am King Arthur");
					int option = multi(player, n, false, //do not send over
						"I want to become a knight of the round table",
						"So what are you doing in Runescape?",
						"Thankyou very much");
					if (option == 0) {
						say(player, n, "I want to become a knight of the round table");
						npcsay(player,
							n,
							"Well I think you need to go on a quest to prove yourself worthy",
							"My knights like a good quest",
							"Unfortunately our current quest is to rescue Merlin",
							"Back in England he got himself trapped in some sort of magical Crystal",
							"We've moved him from the cave we found him in",
							"He's upstairs in his tower");
						say(player, n, "I will see what I can do then");
						npcsay(player, n, "Talk to my knights if you need any help");
						if (player.getQuestStage(this) == 0) {
							player.updateQuestStage(Quests.MERLINS_CRYSTAL, 1);
						}
					} else if (option == 1) {
						say(player, n, "So what are you doing in Runescape");
						npcsay(player, n,
							"Well legend says we will return to Britain in it's time of greatest need");
						npcsay(player, n, "But that's not for quite a while");
						npcsay(player, n,
							"So we've moved the whole outfit here for now");
						npcsay(player, n, "We're passing the time in Runescape");
					} else if (option == 2) {
						say(player, n, "thankyou very much");
					}
					break;
				case 5:
					say(player, n, "I have freed Merlin from his crystal");
					npcsay(player, n, "Ah a good job well done", "I knight thee",
						"You are now a knight of the round table");
					player.sendQuestComplete(Quests.MERLINS_CRYSTAL);
					break;
				case -1:
					say(player, n, "Now i am a knight of the round table",
						"Do you have anymore quests for me?");
					npcsay(player,
						n,
						"Aha, I'm glad you are here",
						"I am sending out various knights on an important quest",
						"I was wondering if you too would like to take up this quest?");
					int q = multi(player, n, "Tell me of this quest",
						"I am weary of questing for the time being");
					if (q == 0) {
						/******************************/
						/**START OF THE HOLY GRAIL QUEST**/
						/******************************/
						npcsay(player, n, "Well we recently found out",
							"The holy grail has passed into the runescape world",
							"This is most fortuitous",
							"None of my knights ever did return with it last time",
							"Now we have the opportunity to give it another go",
							"Maybe this time we will have more luck");
						int startHoly = multi(player, n,
							"I'd enjoy trying that",
							"I may come back and try that later");
						if (startHoly == 0) {
							npcsay(player, n, "Go speak to Merlin",
								"He may be able to give a better clue as to where it is",
								"Now you have freed him from the crystal",
								"He has set up his workshop in the room next to the library");
							player.updateQuestStage(Quests.THE_HOLY_GRAIL, 1);
						} else if (startHoly == 1) {
							npcsay(player, n, "Be sure that you come speak to me soon then");
						}
					} else if (q == 1) {
						npcsay(player, n, "Maybe later then");
						say(player, n, "Maybe so");
					}
					break;
			}
		} else if (n.getID() == NpcId.SIR_GAWAIN.id()) {
			if (player.getCache().hasKey("talked_to_gawain")) {
				npcsay(player, n, "Good day to you sir");
				int option = multi(
					player,
					n,
					"Any idea how to get into Morgan Le Faye's stronghold?",
					"Hello again");
				if (option == 0) {
					npcsay(player, n, "No you've got me stumped there");
				}
				return;
			}
			switch (player.getQuestStage(this)) {
				case 0:
				case 2:
				case 3:
				case 4:
				case 5:
					npcsay(player, n, "Good day to you sir");
					int opt = multi(player, n, false, //do not send over
						"Good day",
						"Know you of any quests Sir knight?");
					if (opt == 0) {
						say(player, n, "good day");
					} else if (opt == 1) {
						say(player, n, "Know you of any quests sir knight?");
						npcsay(player, n,
							"The king is the man to talk to if you want a quest");
					}
					break;
				case 1:
					npcsay(player, n, "Good day to you sir");
					int option = multi(player, n, false, //do not send over
						"Good day",
						"Any ideas on how to get Merlin out that crystal?",
						"Do you know how Merlin got trapped");
					if (option == 0) {
						say(player, n, "good day");
					} else if (option == 1) {
						say(player, n, "Any ideas on how to get Merlin out that crystal?");
						npcsay(player, n, "I'm a little stumped myself",
							"We've tried opening it with anything and everything");
					} else if (option == 2) {
						say(player, n, "Do you know how Merlin got trapped?");
						npcsay(player, n,
							"I would guess this is the work of the evil Morgan Le Faye");
						say(player, n, "And where can I find her?");
						npcsay(player, n,
							"She lives in her stronghold to the south of here");
						npcsay(player, n,
							"Guarded by some renegade knights led by Sir Mordred");
						player.getCache().store("talked_to_gawain", true);
						int sub_option = multi(
							player,
							n,
							"Any idea how to get into Morgan Le Faye's stronghold?",
							"Thankyou for the information");
						if (sub_option == 0) {
							npcsay(player, n, "No you've got me stumped there");
						}
					}
					break;
				case -1:
					npcsay(player, n, "Good day to you sir");
					int ope = multi(player, n, false, //do not send over
						"Good day",
						"Know you of any quests Sir knight?");
					if (ope == 0) {
						say(player, n, "good day");
					} else if (ope == 1) {
						say(player, n, "Know you of any quests sir knight?");
						npcsay(player, n, "I think you've done the main quest we were on right now");
					}
					break;
			}
		} else if (n.getID() == NpcId.SIR_LANCELOT.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
					npcsay(player,
						n,
						"Greetings I am Sir Lancelot the greatest knight in the land",
						"What do you want?");
					if (player.getCache().hasKey("talked_to_gawain")) {
						int opt = multi(player, n, false, //do not send over
							"I want to get Merlin out of the crystal",
							"You're a little full of yourself aren't you?",
							"Any ideas on how to get into Morgan Le Faye's stronghold?");
						if (opt == 0) {
							say(player, n, "I want to get Merlin out of the crystal");
							npcsay(player, n,
								"Well the knights of the round table can't manage it",
								"I can't see how a commoner like you could succeed where we have failed");
						} else if (opt == 1) {
							say(player, n, "You're a little full of yourself aren't you?");
							npcsay(player, n,
								"I have every right to be proud of myself",
								"My prowess in battle is world renowned");
						} else if (opt == 2) {
							say(player, n, "Any ideas on how to get into Morgan Le Fayes's stronghold");
							npcsay(player,
								n,
								"That stronghold is built in a strong defensive position",
								"It's on a big rock sticking out into the sea",
								"There are two ways in that I know of, the large heavy front doors",
								"And the sea entrance, only penetrable by boat",
								"They take all their deliveries by boat");
							player.updateQuestStage(Quests.MERLINS_CRYSTAL, 2);
							player.getCache().remove("talked_to_gawain");
						}
					} else {
						int opt = multi(player, n,
							"I want to get Merlin out of the crystal",
							"You're a little full of yourself aren't you?");
						if (opt == 0) {
							npcsay(player,
								n,
								"Well the knights of the round table can't manage it",
								"I can't see how a commoner like you could succeed where we have failed");
						} else if (opt == 1) {
							npcsay(player, n,
								"I have every right to be proud of myself",
								"My prowess in battle is world renowned");
						}
					}
					break;
				case 2:
				case 3:
				case 4:
				case 5:
				case -1:
					npcsay(player,
						n,
						"Greetings I am Sir Lancelot the greatest knight in the land",
						"What do you want?");
					int opt = multi(player, n,
						"You're a little full of yourself aren't you?",
						"I seek a quest");
					if (opt == 0) {
						npcsay(player, n, "I have every right to be proud of myself",
							"My prowess in battle is world renowned");
					} else if (opt == 1) {
						npcsay(player, n, "Leave questing to the profesionals",
							"Such as myself");
					}
					break;
			}

		}
	}

}
