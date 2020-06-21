package com.openrsc.server.plugins.quests.members.legendsquest.obstacles;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.quests.members.legendsquest.npcs.LegendsQuestNezikchened;
import com.openrsc.server.plugins.skills.mining.Mining;
import com.openrsc.server.plugins.skills.thieving.Thieving;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestGameObjects implements OpLocTrigger, UseLocTrigger {

	// Objects
	private static final int LEGENDS_CUPBOARD = 1149;
	private static final int GRAND_VIZIERS_DESK = 1177;
	private static final int TOTEM_POLE = 1169;
	private static final int ROCK = 1151;
	private static final int TALL_REEDS = 1163;
	private static final int SHALLOW_WATER = 582;
	private static final int CRATE = 1144;
	private static final int CRUDE_BED = 1162;
	private static final int CRUDE_DESK = 1032;
	private static final int TABLE = 1161;
	private static final int BOOKCASE = 931;
	private static final int CAVE_ENTRANCE_LEAVE_DUNGEON = 1158;
	private static final int CAVE_ENTRANCE_FROM_BOULDERS = 1159;
	private static final int CAVE_ANCIENT_WOODEN_DOORS = 1160;
	private static final int HEAVY_METAL_GATE = 1033;
	private static final int HALF_BURIED_REMAINS = 1168;
	private static final int CARVED_ROCK = 1037;
	private static final int WOODEN_BEAM = 1156;
	private static final int ROPE_UP = 1167;
	private static final int RED_EYE_ROCK = 1148;
	private static final int ANCIENT_LAVA_FURNACE = 1146;
	private static final int CAVERNOUS_OPENING = 1145;
	private static final int ECHNED_ZEKIN_ROCK = 1116;
	private static final int FERTILE_EARTH = 1113;

	private static final int[] SMASH_BOULDERS = {1117, 1184, 1185};
	private static final int BABY_YOMMI_TREE = 1112;
	private static final int YOMMI_TREE = 1107;
	private static final int DEAD_YOMMI_TREE = 1141;
	private static final int GROWN_YOMMI_TREE = 1108;
	private static final int ROTTEN_YOMMI_TREE = 1172;
	private static final int CHOPPED_YOMMI_TREE = 1109;
	private static final int TRIMMED_YOMMI_TREE = 1110;
	private static final int CRAFTED_TOTEM_POLE = 1111;
	private final int[] REFILLABLE = {1188, 1266, 21, 140, 341, 465};
	private final int[] REFILLED = {1189, 1267, 50, 141, 342, 464};

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return inArray(obj.getID(), GRAND_VIZIERS_DESK, LEGENDS_CUPBOARD, TOTEM_POLE, ROCK, TALL_REEDS,
				SHALLOW_WATER, CAVE_ENTRANCE_LEAVE_DUNGEON, CRATE, TABLE, BOOKCASE, CAVE_ENTRANCE_FROM_BOULDERS, CRUDE_DESK,
				CAVE_ANCIENT_WOODEN_DOORS, HEAVY_METAL_GATE, HALF_BURIED_REMAINS, CARVED_ROCK, WOODEN_BEAM, WOODEN_BEAM + 1, ROPE_UP,
				RED_EYE_ROCK, ANCIENT_LAVA_FURNACE, CAVERNOUS_OPENING, ECHNED_ZEKIN_ROCK, CRAFTED_TOTEM_POLE, TOTEM_POLE + 1)
				|| inArray(obj.getID(), SMASH_BOULDERS) || (obj.getID() == CRUDE_BED && command.equalsIgnoreCase("search"));
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == ECHNED_ZEKIN_ROCK) {
			if (player.getQuestStage(Quests.LEGENDS_QUEST) == 8) {
				mes(config().GAME_TICK * 2, "The rock moves quite easily.");
				player.message("And the spirit of Echned Zekin seems to have disapeared.");
				changeloc(obj, 10000, SHALLOW_WATER);
				return;
			}
			Npc echned = ifnearvisnpc(player, NpcId.ECHNED_ZEKIN.id(), 2);
			if (echned == null) {
				mes(config().GAME_TICK * 2, "A thick, green mist seems to emanate from the water...",
					"It slowly congeals into the shape of a body...");
				echned = addnpc(player, NpcId.ECHNED_ZEKIN.id(), player.getX(), player.getY(), 0, (int)TimeUnit.SECONDS.toMillis(180));
				if (echned != null) {
					delay(2);
					mes(echned, config().GAME_TICK * 2, "Which slowly floats towards you.");
					echned.initializeTalkScript(player);
				}
				return;
			}
			if (echned != null) {
				echned.initializeTalkScript(player);
			}
		}
		else if (obj.getID() == CAVERNOUS_OPENING) {
			if (command.equalsIgnoreCase("enter")) {
				if (player.getY() >= 3733) {
					player.message("You enter the dark cave...");
					player.teleport(395, 3725);
				} else {
					if (player.getCache().hasKey("cavernous_opening") || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
						mes(config().GAME_TICK * 2, "You walk carefully into the darkness of the cavern..");
						player.teleport(395, 3733);
					} else {
						mes(config().GAME_TICK * 2, "You walk into an invisible barrier...");
						mes(config().GAME_TICK, "Somekind of magical force will not allow you to pass into the cavern.");
					}
				}
			} else if (command.equalsIgnoreCase("search")) {
				if (player.getCache().hasKey("cavernous_opening")) {
					mes(config().GAME_TICK * 2, "You can see a glowing crystal shape in the wall.",
						"It looks like the Crystal is magical, ",
						"it allows access to the cavern.");
				} else {
					mes(config().GAME_TICK * 2, "You see a heart shaped depression in the wall next to the cavern.",
						"And a message reads...",
						"@gre@All ye who stand 'ere the dragons teeth,");
					mes(config().GAME_TICK, "@gre@Place your full true heart and proceed...");
				}
			}
		}
		else if (obj.getID() == ANCIENT_LAVA_FURNACE) {
			if (command.equalsIgnoreCase("look")) {
				mes(config().GAME_TICK, "This is an ancient looking furnace.");
			} else if (command.equalsIgnoreCase("search")) {
				mes(config().GAME_TICK * 2, "You search the lava furnace.",
					"You find a small compartment that you may be able to use.",
					"Strangely, it looks as if it is designed for a specific purpose...");
				mes(config().GAME_TICK, "to fuse things together at very high temperatures...");
			}
		}
		else if (obj.getID() == RED_EYE_ROCK) {
			mes(config().GAME_TICK, "These rocks look somehow manufactured..");
		}
		else if (obj.getID() == ROPE_UP) {
			player.message("You climb the rope back out again.");
			player.teleport(471, 3707);
		}
		else if (obj.getID() == WOODEN_BEAM + 1) {
			if (player.getQuestStage(Quests.LEGENDS_QUEST) >= 9 || blockDescendBeamPostQuest(player)) {
				mes(config().GAME_TICK * 2, "The rope snaps as you're about to climb down it.",
					"Perhaps you need a new rope.");
				return;
			}
			mes(config().GAME_TICK * 2, "This rope climb looks pretty dangerous,",
				"Are you sure you want to go down?");
			int menu = multi(player,
				"Yes,I'll go down the rope...",
				"No way do I want to go down there.");
			if (menu == 0) {
				mes(config().GAME_TICK * 2, "You prepare to climb down the rope...");
				say(player, null, "! Gulp !");
				delay(2);
				if ((player.getQuestStage(Quests.LEGENDS_QUEST) >= 0 && !player.getCache().hasKey("gujuo_potion")) || blockDescendBeamPostQuest(player)) {
					mes(config().GAME_TICK * 2, "...but a terrible fear grips you...");
					player.message("And you can go no further.");
				} else {
					int rnd = DataConversions.random(0, 4);
					if (rnd == 0) {
						mes(config().GAME_TICK * 2, "but fear stabs at your heart...",
								"and you lose concentration,",
							"you slip and fall....");
						player.damage(DataConversions.random(10, 15));
					}
					else {
						mes(config().GAME_TICK * 2, "And although fear stabs at your heart...",
								"You shimmey down the rope...");
					}
					player.teleport(426, 3707);
				}
			} else if (menu == 1) {
				player.message("You decide not to go down the rope.");
			}
		}
		else if (obj.getID() == WOODEN_BEAM) {
			player.message("You search the wooden beam...");
			if (player.getCache().hasKey("legends_wooden_beam")) {
				player.message("You search the wooden beam and find the rope you attached.");
				changeloc(obj, config().GAME_TICK * 8, WOODEN_BEAM + 1);
			} else {
				mes(config().GAME_TICK * 2, "You see nothing special about this...");
				player.message("Perhaps if you had a rope, it might be more functional.");
			}
		}
		else if (obj.getID() == CARVED_ROCK) {
			mes(config().GAME_TICK * 2, "You see a delicate inscription on the rock, it says,");
			mes(config().GAME_TICK * 3, "@gre@'Once there were crystals to make the pool shine,'");
			mes(0, "@gre@'Ordered in stature to retrieve what's mine.'");
			String gem = "";
			boolean attached = false;
			// opal
			if (obj.getX() == 471 && obj.getY() == 3722) {
				gem = "Opal";
				attached = player.getCache().hasKey("legends_attach_1");
			}
			// emerald
			else if (obj.getX() == 474 && obj.getY() == 3730) {
				gem = "Emerald";
				attached = player.getCache().hasKey("legends_attach_2");
			}
			// ruby
			else if (obj.getX() == 471 && obj.getY() == 3734) {
				gem = "Ruby";
				attached = player.getCache().hasKey("legends_attach_3");
			}
			// diamond
			else if (obj.getX() == 466 && obj.getY() == 3739) {
				gem = "Diamond";
				attached = player.getCache().hasKey("legends_attach_4");
			}
			// sapphire
			else if (obj.getX() == 460 && obj.getY() == 3737) {
				gem = "Sapphire";
				attached = player.getCache().hasKey("legends_attach_5");
			}
			// red topaz
			else if (obj.getX() == 464 && obj.getY() == 3730) {
				gem = "Topaz";
				attached = player.getCache().hasKey("legends_attach_6");
			}
			// jade
			else if (obj.getX() == 469 && obj.getY() == 3728) {
				gem = "Jade";
				attached = player.getCache().hasKey("legends_attach_7");
			}

			if (!gem.equals("") && attached) {
				mes(config().GAME_TICK * 2, "A barely visible " + gem + " becomes clear again, spinning above the rock.",
						"And then fades again...");
			}
		}
		else if (obj.getID() == HALF_BURIED_REMAINS) {
			mes("It looks as if some poor unfortunate soul died here.");
		}
		else if (obj.getID() == HEAVY_METAL_GATE) {
			if (command.equalsIgnoreCase("look")) {
				mes(config().GAME_TICK * 2, "This huge metal gate bars the way further...",
					"There is an intense and unpleasant feeling from this place.");
				player.message("And you can see why, shadowy flying creatures seem to hover in the still dark air.");
			} else if (command.equalsIgnoreCase("push")) {
				mes(config().GAME_TICK * 2, "You push the gates...they're very stiff...",
					"They won't budge with a normal push.",
					"Do you want to try to force them open with brute strength?");
				int menu = multi(player,
					"Yes, I'm very strong, I'll force them open.",
					"No, I'm having second thoughts.");
				if (menu == 0) {
					if (getCurrentLevel(player, Skills.STRENGTH) < 50) {
						player.message("You need a Strength of at least 50 to affect these gates.");
						return;
					}
					mes(config().GAME_TICK * 2, "You ripple your muscles...preparing too exert yourself...");
					say(player, null, "Hup!");
					mes(config().GAME_TICK * 2, "You brace yourself against the doors...");
					say(player, null, "Urghhhhh!");
					mes(config().GAME_TICK * 2, "You start to force against the gate..");
					say(player, null, "Arghhhhhhh!");
					mes(config().GAME_TICK * 2, "You push and push,");
					say(player, null, "Shhhhhhhshshehshsh");
					if (Formulae.failCalculation(player, Skills.STRENGTH, 50)) {
						mes(config().GAME_TICK * 2, "You just manage to force the gates open slightly, ",
							"just enough to force yourself through.");
						changeloc(obj, config().GAME_TICK * 3, 181);
						if (player.getY() <= 3717) {
							player.teleport(441, 3719);
						} else {
							player.teleport(441, 3717);
						}
					} else {
						mes(config().GAME_TICK * 2, "but run out of steam before you're able to force the gates open.");
						player.message("The effort of trying to force the gates reduces your strength temporarily");
						player.getSkills().decrementLevel(Skills.STRENGTH);
					}
				} else if (menu == 1) {
					player.message("You decide against forcing the gates.");
				}
			}
		}
		else if (inArray(obj.getID(), SMASH_BOULDERS)) {
			if (player.getCarriedItems().hasCatalogID(Mining.getAxe(player), Optional.of(false))) {
				if (getCurrentLevel(player, Skills.MINING) < 52) {
					if (player.getY() < 3707) {
						player.message("You need a mining ability of at least 52 to affect these boulders.");
						return;
					} else {
						mes("You could be stuck here for ages until your mining ability returns.",
							"Would you like to try to climb out?",
							"It looks rough going, but at least you won't be stuck here for ages.");
						int outmenu = multi(player, "Yes, I'll climb out.", "No, I'll stay here a while.");
						if (outmenu == 0) {
							while (player.getY() > 3707) {
								player.damage(2);
								player.teleport(player.getX(), player.getY() - 3);
								delay(3);
							}
							player.damage(1);
							player.teleport(442, 3703);
						} else if (outmenu == 1) {
							mes("You decide to stay where you are.");
						}
					}
				}
				if (Formulae.failCalculation(player, Skills.MINING, 50)) {
					mes(config().GAME_TICK * 2, "You take a good swing at the rock with your pick...");
					changeloc(obj, config().GAME_TICK * 3, 1143);
					if (obj.getID() == SMASH_BOULDERS[0] && player.getY() <= 3704) {
						player.teleport(441, 3707);
					} else if (obj.getID() == SMASH_BOULDERS[0] && player.getY() >= 3707) {
						player.teleport(442, 3704);
					} else if (obj.getID() == SMASH_BOULDERS[1] && player.getY() <= 3708) {
						player.teleport(441, 3711);
					} else if (obj.getID() == SMASH_BOULDERS[1] && player.getY() >= 3711) {
						player.teleport(441, 3708);
					} else if (obj.getID() == SMASH_BOULDERS[2] && player.getY() <= 3712) {
						player.teleport(441, 3715);
					} else if (obj.getID() == SMASH_BOULDERS[2] && player.getY() >= 3715) {
						player.teleport(441, 3712);
					}
					mes(config().GAME_TICK * 3, "...and smash it into smaller pieces.");
					player.message("Another large rock falls down replacing the one that you smashed.");
				} else {
					player.message("You fail to make a mark on the rocks.");
					player.message("You miss hit the rock and the vibration shakes your bones.");
					player.message("Your mining ability suffers...");
					player.getSkills().decrementLevel(Skills.MINING);
				}
			} else {
				mes("You'll need a pickaxe to smash your way through these boulders.");
			}
		}
		else if (obj.getID() == CAVE_ANCIENT_WOODEN_DOORS) {
			if (command.equalsIgnoreCase("open")) {
				if (player.getY() >= 3703) {
					mes(config().GAME_TICK * 2, "You push the doors open and walk through.");
					changeloc(obj, config().GAME_TICK * 3, 497);
					player.teleport(442, 3701);
					delay(3);
					player.message("The doors make a satisfying 'CLICK' sound as they close.");
				} else {
					mes(config().GAME_TICK * 2, "You push on the doors...they're really shut..",
						"It looks as if they have a huge lock on it...");
					player.message("Although ancient, it looks very sophisticated...");
				}
			} else if (command.equalsIgnoreCase("pick lock")) {
				if (player.getY() >= 3703) {
					mes(config().GAME_TICK * 2, "You see a lever which you pull on to open the door.");
					changeloc(obj, config().GAME_TICK * 3, 497);
					player.teleport(442, 3701);
					mes(config().GAME_TICK * 2, "You walk through the door.");
					player.message("The doors make a satisfying 'CLICK' sound as they close.");
				} else {
					if (getCurrentLevel(player, Skills.THIEVING) < 50) {
						player.message("You need a thieving level of at least 50 to attempt this.");
						return;
					}
					if (player.getCarriedItems().hasCatalogID(ItemId.LOCKPICK.id(), Optional.of(false))) {
						mes(config().GAME_TICK * 2, "You attempt to pick the lock..");
						player.message("It looks very sophisticated ...");
						say(player, null, "Hmmm, interesting...");
						delay(2);
						player.message("You carefully insert your lockpick into the lock.");
						say(player, null, "This will be a challenge...");
						delay(2);
						player.message("You feel for the pins and levers in the mechanism.");
						say(player, null, "Easy does it....");
						delay(2);
						if (Thieving.succeedPickLockThieving(player, 50)) {
							mes(config().GAME_TICK * 2, "@gre@'CLICK'");
							say(player, null, "Easy as pie...");
							delay(2);
							mes(config().GAME_TICK * 2, "You tumble the lock mechanism and the door opens easily.");
							player.incExp(Skills.THIEVING, 100, true);
							changeloc(obj, config().GAME_TICK * 3, 497);
							player.teleport(441, 3703);
						} else {
							player.message("...but you don't manage to pick the lock.");
						}
					} else {
						mes(config().GAME_TICK * 2, "The mechanism for this lock looks very sophisticated...");
						player.message("you're unable to affect the lock without the proper tool..");
					}
				}
			}
		}
		else if (obj.getID() == CRUDE_DESK) {
			if (player.getCarriedItems().hasCatalogID(ItemId.SHAMANS_TOME.id(), Optional.empty())) {
				mes(config().GAME_TICK * 2, "You search the desk ...");
				player.message("...but find nothing.");
			} else {
				mes(config().GAME_TICK * 4, "You search the desk ...");
				give(player, ItemId.SHAMANS_TOME.id(), 1);
				player.message("You find a book...it looks like an ancient tome...");
			}
		}
		else if (obj.getID() == BOOKCASE) {
			mes(config().GAME_TICK * 2, "You search the bookcase...",
				"And find a large gaping hole at the back.");
			player.message("Would you like to climb through the hole?");
			int menu = multi(player,
				"Yes, I'll climb through the hole.",
				"No, I'll stay here.");
			if (menu == 0) {
				mes(config().GAME_TICK * 2, "You climb through the hole in the wall..",
					"It's very narrow and you have to contort your body a lot.",
					"After some time, you  manage to wriggle out of a small cavern...");
				player.teleport(444, 3699);
			} else if (menu == 1) {
				player.message("You decide to stay where you are.");
			}
		}
		else if (obj.getID() == TABLE) {
			player.message("You start searching the table...");
			if (player.getCarriedItems().hasCatalogID(ItemId.SCRAWLED_NOTES.id(), Optional.empty())) {
				player.message("You cannot find anything else in here.");
			} else {
				delay(2);
				give(player, ItemId.SCRAWLED_NOTES.id(), 1);
				mes(config().GAME_TICK * 2, "You find a scrap of paper with nonesense written on it.");
			}
		}
		else if (obj.getID() == CRUDE_BED && command.equalsIgnoreCase("search")) {
			player.message("You search the flea infested rags..");
			if (player.getCarriedItems().hasCatalogID(ItemId.SCATCHED_NOTES.id(), Optional.empty())) {
				player.message("You cannot find anything else in here.");
			} else {
				delay(2);
				give(player, ItemId.SCATCHED_NOTES.id(), 1);
				mes(config().GAME_TICK * 2, "You find a scrap of paper with spidery writing on it.");
			}
		}
		else if (obj.getID() == CRATE) {
			player.message("You search the crate.");
			if (player.getCarriedItems().hasCatalogID(ItemId.SCRIBBLED_NOTES.id(), Optional.empty())) {
				player.message("You cannot find anything else in here.");
			} else {
				delay(2);
				give(player, ItemId.SCRIBBLED_NOTES.id(), 1);
				mes(config().GAME_TICK * 2, "After some time you find a scrumpled up piece of paper.");
				player.message("It looks like rubbish...");
			}
		}
		else if (obj.getID() == CAVE_ENTRANCE_FROM_BOULDERS) {
			mes(config().GAME_TICK * 2, "You see a small cave entrance.",
				"Would you like to climb into it?");
			int menu = multi(player,
				"Yes, I'll climb into it.",
				"No, I'll stay where I am.");
			if (menu == 0) {
				player.message("You clamber into the small cave...");
				player.teleport(452, 3702);
			} else if (menu == 1) {
				player.message("You decide against climbing into the small, uncomfortable looking tunnel.");
			}
		}
		else if (obj.getID() == CAVE_ENTRANCE_LEAVE_DUNGEON) {
			mes(config().GAME_TICK * 2, "You crawl back out from the cavern...");
			player.teleport(452, 874);
		}
		else if (obj.getID() == SHALLOW_WATER) {
			if (player.getQuestStage(Quests.LEGENDS_QUEST) == 8 && player.getY() >= 3723 && player.getY() <= 3740) {
				player.message("A magical looking pool.");
				return;
			}
			if (player.getQuestStage(Quests.LEGENDS_QUEST) >= 5 || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
				player.message("A disgusting sess pit of filth and stench...");
				return;
			}
			mes(0, "A bubbling brook with effervescent water...");
		}
		else if (obj.getID() == TALL_REEDS) {
			mes(config().GAME_TICK * 2, "These tall reeds look nice and long, ");
			mes(config().GAME_TICK * 2, "with a long tube for a stem.");
			mes(0, "They reach all the way down to the water.");
		}
		else if (obj.getID() == ROCK) {
			if (player.getCache().hasKey("legends_cavern") || player.getQuestStage(Quests.LEGENDS_QUEST) >= 2 || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
				if (player.getQuestStage(Quests.LEGENDS_QUEST) == 1) {
					mes(config().GAME_TICK * 2, "You see nothing significant...",
						"At first....");
				}
				mes(config().GAME_TICK * 2, "You see that there is a small crevice that you may be able to crawl though.?",
					"Would you like to try to crawl through, it looks quite an enclosed area.");
				int menu = multi(player,
					"Yes, I'll crawl through, I'm very athletic.",
					"No, I'm pretty scared of enclosed areas.");
				if (menu == 0) {
					if (getCurrentLevel(player, Skills.AGILITY) < 50) {
						player.message("You need an agility of 50 to even attempt this.");
						return;
					}
					mes(config().GAME_TICK * 2, "You try to crawl through...",
						"You contort your body to fit the crevice.");
					if (Formulae.failCalculation(player, Skills.AGILITY, 50)) {
						mes(config().GAME_TICK * 2, "You adroitely squeeze serpent like into the crevice.",
							"You find a small narrow tunnel that goes for some distance.",
							"After some time, you find a small cave opening...and walk through.");
						player.teleport(461, 3700);
						if (player.getCache().hasKey("legends_cavern")) {
							player.getCache().remove("legends_cavern");
							player.updateQuestStage(Quests.LEGENDS_QUEST, 2);
						}
					} else {
						mes(config().GAME_TICK * 5, "You get cramped into a tiny space and start to suffocate.",
							"You wriggle and wriggle but you cannot get out..");
						mes(config().GAME_TICK * 2, "Eventually you manage to break free.",
							"But you scrape yourself very badly as your force your way out.",
							"And you're totally exhausted from the experience.");
						player.damage(5);
					}
				} else if (menu == 1) {
					mes(config().GAME_TICK * 2, "You decide against forcing yourself into the tiny crevice..",
						"And realise that you have much better things to do..",
						"Like visit Inn's and mine ore...");
				}
			} else {
				player.message("You see nothing significant.");
			}
		}
		else if (obj.getID() == TOTEM_POLE) { // BLACK
			if (player.getQuestStage(Quests.LEGENDS_QUEST) >= 10 || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
				changeloc(obj, config().GAME_TICK * 16, 1170);
				mes(config().GAME_TICK * 2, "This totem pole is truly awe inspiring.",
					"It depicts powerful Karamja jungle animals.",
					"It is very well carved and brings a sense of power ",
					"and spiritual fullfilment to anyone who looks at it.");
				return;
			}
			if (player.getQuestStage(Quests.LEGENDS_QUEST) == 9) {
				replaceTotemPole(player, obj, false);
				return;
			}
			mes(config().GAME_TICK * 2, "This totem pole looks very corrupted,",
				"there is a darkness about it that seems quite unnatural.",
				"You don't like to look at it for too long.");

		}
		else if (obj.getID() == TOTEM_POLE + 1) { // RED
			if (player.getQuestStage(Quests.LEGENDS_QUEST) >= 10 || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
				mes(config().GAME_TICK * 2, "This totem pole is truly awe inspiring.",
					"It depicts powerful Karamja jungle animals.",
					"It is very well carved and brings a sense of power ",
					"and spiritual fullfilment to anyone who looks at it.");
				return;
			}
			if (player.getQuestStage(Quests.LEGENDS_QUEST) == 9) {
				replaceTotemPole(player, obj, false);
				return;
			}
			changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), TOTEM_POLE, obj.getDirection(), obj.getType()));
			mes(config().GAME_TICK * 2, "This totem pole looks very corrupted,",
				"there is a darkness about it that seems quite unnatural.",
				"You don't like to look at it for too long.");

		}
		else if (obj.getID() == GRAND_VIZIERS_DESK) {
			player.message("You rap loudly on the desk.");
			Npc radimus = ifnearvisnpc(player, NpcId.SIR_RADIMUS_ERKLE_HOUSE.id(), 6);
			if (radimus != null) {
				radimus.teleport(517, 545);
				npcWalkFromPlayer(player, radimus);
				radimus.initializeTalkScript(player);
			} else {
				player.message("Sir Radimus Erkle is currently busy at the moment.");
			}
		}
		else if (obj.getID() == LEGENDS_CUPBOARD) {
			if (player.getQuestStage(Quests.LEGENDS_QUEST) >= 1 || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
				if (player.getCarriedItems().hasCatalogID(ItemId.MACHETTE.id(), Optional.of(false))) {
					player.message("The cupboard is empty.");
				} else {
					mes(config().GAME_TICK * 2, "You open the cupboard and find a machette.",
						"You take it out and add it to your inventory.");
					give(player, ItemId.MACHETTE.id(), 1);
				}
			} else {
				player.message("@gre@Sir Radimus Erkle: You're not authorised to open that cupboard.");
			}
		}
		else if (obj.getID() == CRAFTED_TOTEM_POLE) {
			if (obj.getOwner().equals(player.getUsername())) {
				mes(config().GAME_TICK * 2, "This totem pole looks very heavy...");
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), FERTILE_EARTH, obj.getDirection(), obj.getType()));
				give(player, ItemId.TOTEM_POLE.id(), 1);
				if (!player.getCache().hasKey("crafted_totem_pole")) {
					player.getCache().store("crafted_totem_pole", true);
				}
				player.message("Carrying this totem pole saps your strength...");
				player.getSkills().setLevel(Skills.STRENGTH, (int) (player.getSkills().getLevel(Skills.STRENGTH) * 0.9));
			} else {
				player.message("This is not your totem pole to carry.");
			}
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return (item.getCatalogId() == ItemId.MACHETTE.id() && obj.getID() == TALL_REEDS)
				|| (item.getCatalogId() == ItemId.CUT_REED_PLANT.id() && obj.getID() == SHALLOW_WATER)
				|| (item.getCatalogId() == ItemId.BLESSED_GOLDEN_BOWL.id() && obj.getID() == SHALLOW_WATER)
				|| obj.getID() == CARVED_ROCK || (obj.getID() == WOODEN_BEAM && item.getCatalogId() == ItemId.ROPE.id())
				|| obj.getID() == ANCIENT_LAVA_FURNACE || (obj.getID() == RED_EYE_ROCK && item.getCatalogId() == ItemId.A_RED_CRYSTAL.id())
				|| (obj.getID() == CAVERNOUS_OPENING && item.getCatalogId() == ItemId.A_GLOWING_RED_CRYSTAL.id())
				|| (obj.getID() == FERTILE_EARTH && item.getCatalogId() == ItemId.YOMMI_TREE_SEED.id())
				|| (obj.getID() == FERTILE_EARTH && item.getCatalogId() == ItemId.GERMINATED_YOMMI_TREE_SEED.id())
				|| (obj.getID() == YOMMI_TREE && item.getCatalogId() == ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id())
				|| (inArray(obj.getID(), DEAD_YOMMI_TREE, ROTTEN_YOMMI_TREE, GROWN_YOMMI_TREE, CHOPPED_YOMMI_TREE, TRIMMED_YOMMI_TREE) && item.getCatalogId() == ItemId.RUNE_AXE.id())
				|| (obj.getID() == TOTEM_POLE && item.getCatalogId() == ItemId.TOTEM_POLE.id());
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == TOTEM_POLE && item.getCatalogId() == ItemId.TOTEM_POLE.id()) {
			if (player.getQuestStage(Quests.LEGENDS_QUEST) >= 10 || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
				mes("You have already replaced the evil totem pole with your own.",
						"You feel a great sense of accomplishment");
				return;
			}
			if (player.getQuestStage(Quests.LEGENDS_QUEST) == 9) {
				replaceTotemPole(player, obj, true);
				return;
			}
			if (player.getQuestStage(Quests.LEGENDS_QUEST) == 8) {
				if (player.getCache().hasKey("killed_viyeldi") && !player.getCache().hasKey("viyeldi_companions")) {
					player.getCache().set("viyeldi_companions", 1);
				}
				mes("You attempt to replace the evil totem pole.",
					"A black cloud emanates from the evil totem pole.");
				player.message("It slowly forms into the dread demon Nezikchened...");
				LegendsQuestNezikchened.demonFight(player);
			}
		}
		else if (obj.getID() == TRIMMED_YOMMI_TREE && item.getCatalogId() == ItemId.RUNE_AXE.id()) {
			if (obj.getOwner().equals(player.getUsername())) {
				int objectX = obj.getX();
				int objectY = obj.getY();
				mes(config().GAME_TICK * 2, "You craft a totem pole out of the Yommi tree.");
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), CRAFTED_TOTEM_POLE, obj.getDirection(), obj.getType(), player.getUsername()));
				obj.getWorld().getServer().getGameEventHandler().add(new SingleEvent(obj.getWorld(), null, 60000, "Legends Quest Craft Totem Pole") {
					public void action() {
						GameObject whatObject = player.getWorld().getRegionManager().getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY), player);
						if (whatObject != null && whatObject.getID() == CRAFTED_TOTEM_POLE) {
							obj.getWorld().registerGameObject(new GameObject(obj.getWorld(), obj.getLocation(), FERTILE_EARTH, obj.getDirection(), obj.getType()));
						}
					}
				});
			} else {
				player.message("This is not your Yommi Tree.");
			}
		}
		else if (obj.getID() == CHOPPED_YOMMI_TREE && item.getCatalogId() == ItemId.RUNE_AXE.id()) {
			if (obj.getOwner().equals(player.getUsername())) {
				int objectX = obj.getX();
				int objectY = obj.getY();
				mes(config().GAME_TICK * 2, "You professionally wield your Rune Axe...",
					"As you trim the branches from the Yommi tree.");
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), TRIMMED_YOMMI_TREE, obj.getDirection(), obj.getType(), player.getUsername()));
				obj.getWorld().getServer().getGameEventHandler().add(new SingleEvent(obj.getWorld(), null, 60000, "Legend Quest Trim Yommi Tree") {
					public void action() {
						GameObject whatObject = player.getWorld().getRegionManager().getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY), player);
						if (whatObject != null && whatObject.getID() == TRIMMED_YOMMI_TREE) {
							obj.getWorld().registerGameObject(new GameObject(obj.getWorld(), obj.getLocation(), FERTILE_EARTH, obj.getDirection(), obj.getType()));
						}
					}
				});
			} else {
				player.message("This is not your Yommi Tree.");
			}
		}
		else if (obj.getID() == GROWN_YOMMI_TREE && item.getCatalogId() == ItemId.RUNE_AXE.id()) {
			if (obj.getOwner().equals(player.getUsername())) {
				int objectX = obj.getX();
				int objectY = obj.getY();
				mes(config().GAME_TICK * 2, "You wield the Rune Axe and prepare to chop the Yommi tree.");
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), CHOPPED_YOMMI_TREE, obj.getDirection(), obj.getType(), player.getUsername()));
				obj.getWorld().getServer().getGameEventHandler().add(new SingleEvent(obj.getWorld(), null, 60000, "Legend Quest Chop Yommi Tree") {
					public void action() {
						GameObject whatObject = player.getWorld().getRegionManager().getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY), player);
						if (whatObject != null && whatObject.getID() == CHOPPED_YOMMI_TREE) {
							obj.getWorld().registerGameObject(new GameObject(obj.getWorld(), obj.getLocation(), FERTILE_EARTH, obj.getDirection(), obj.getType()));
						}
					}
				});
				mes(config().GAME_TICK * 2, "You chop the Yommi tree down.",
					"Perhaps you should trim those branches ?");
			} else {
				player.message("This is not your Yommi Tree.");
			}
		}
		else if ((obj.getID() == DEAD_YOMMI_TREE || obj.getID() == ROTTEN_YOMMI_TREE) && item.getCatalogId() == ItemId.RUNE_AXE.id()) {
			mes(0, "You chop the dead Yommi Tree down.");
			mes(config().GAME_TICK * 2, "You gain some logs..");
			changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), FERTILE_EARTH, obj.getDirection(), obj.getType()));
			give(player, ItemId.LOGS.id(), 1);
		}
		else if (obj.getID() == YOMMI_TREE && item.getCatalogId() == ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id()) {
			int objectX = obj.getX();
			int objectY = obj.getY();
			player.getCarriedItems().remove(new Item(ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id()));
			player.getCarriedItems().getInventory().add(new Item(ItemId.BLESSED_GOLDEN_BOWL.id()));
			displayTeleportBubble(player, obj.getX(), obj.getY(), true);
			mes(config().GAME_TICK * 2, "You water the Yommi tree from the golden bowl...",
				"It grows at a remarkable rate.");
			changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), GROWN_YOMMI_TREE, obj.getDirection(), obj.getType(), player.getUsername()));
			obj.getWorld().getServer().getGameEventHandler().add(new SingleEvent(obj.getWorld(), null, 15000, "Legend Quest Water Yommi Tree") {
				public void action() {
					GameObject whatObject = player.getWorld().getRegionManager().getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY), player);
					if (whatObject != null && whatObject.getID() == GROWN_YOMMI_TREE) {
						obj.getWorld().registerGameObject(new GameObject(obj.getWorld(), obj.getLocation(), ROTTEN_YOMMI_TREE, obj.getDirection(), obj.getType()));
						if (player.isLoggedIn()) {
							player.message("The Yommi tree is past it's prime and dies .");
						}
						addloc(obj.getWorld(), obj.getLoc(), 60000);
					}
				}
			});
			mes(config().GAME_TICK * 2, "Soon the tree stops growing...",
				"It looks tall enough now to make a good totem pole.");
		}
		else if (obj.getID() == FERTILE_EARTH && item.getCatalogId() == ItemId.YOMMI_TREE_SEED.id()) {
			player.message("These seeds need to be germinated in pure water before they");
			player.message("can be planted in the fertile soil.");
		}
		else if (obj.getID() == FERTILE_EARTH && item.getCatalogId() == ItemId.GERMINATED_YOMMI_TREE_SEED.id()) {
			if (player.getQuestStage(Quests.LEGENDS_QUEST) != 8 || !player.getCarriedItems().hasCatalogID(ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id(), Optional.of(false))) {
				player.message("You'll need some sacred water to feed ");
				player.message("the tree when it starts growing.");
				return;
			}
			if (!player.getCarriedItems().hasCatalogID(ItemId.RUNE_AXE.id(), Optional.of(false))) {
				player.message("You'll need a very tough, very sharp axe to");
				player.message("fell the tree once it is grown.");
				return;
			}
			if (getCurrentLevel(player, Skills.WOODCUT) < 50) {
				player.message("You need an woodcut level of 50 to");
				player.message("fell the tree once it is grown.");
				return;
			}
			if (getCurrentLevel(player, Skills.HERBLAW) < 45) {
				player.message("You need a herblaw skill of at least 45 to complete this task.");
				return;
			}
			// 1112, 1107
			// 1172
			player.getCarriedItems().remove(new Item(ItemId.GERMINATED_YOMMI_TREE_SEED.id()));
			if (DataConversions.random(0, 1) != 1) {
				int objectX = obj.getX();
				int objectY = obj.getY();
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), BABY_YOMMI_TREE, obj.getDirection(), obj.getType()));
				mes(config().GAME_TICK * 2, "You bury the Germinated Yommi tree seed in the fertile earth...",
					"You start to see something growing.");
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), YOMMI_TREE, obj.getDirection(), obj.getType()));
				obj.getWorld().getServer().getGameEventHandler().add(new SingleEvent(obj.getWorld(), null, 15000, "Legends Quest Grow Yommi Tree") {
					public void action() {
						GameObject whatObject = player.getWorld().getRegionManager().getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY), player);
						if (whatObject != null && whatObject.getID() == YOMMI_TREE) {
							obj.getWorld().registerGameObject(new GameObject(obj.getWorld(), obj.getLocation(), DEAD_YOMMI_TREE, obj.getDirection(), obj.getType(), player.getUsername()));
							if (player.isLoggedIn()) {
								player.message("The Sapling dies.");
							}
							addloc(obj.getWorld(), obj.getLoc(), 60000);
						}
					}
				});
				player.message("The plant grows at a remarkable rate.");
				player.message("It looks as if the tree needs to be watered...");
			} else {
				player.message("You planted the seed incorrectly, it withers and dies.");
			}
		}
		else if (obj.getID() == CAVERNOUS_OPENING && item.getCatalogId() == ItemId.A_GLOWING_RED_CRYSTAL.id()) {
			mes(config().GAME_TICK * 2, "You carefully place the glowing heart shaped crystal into ",
				"the depression, it slots in perfectly and glows even brighter.",
				"You hear a snapping sound coming from in front of the cave.");
			player.getCarriedItems().remove(new Item(item.getCatalogId()));
			if (!player.getCache().hasKey("cavernous_opening")) {
				player.getCache().store("cavernous_opening", true);
			}
		}
		else if (obj.getID() == RED_EYE_ROCK && item.getCatalogId() == ItemId.A_RED_CRYSTAL.id()) {
			mes(config().GAME_TICK * 2, "You carefully place the Dragon Crystal on the rock.",
				"The rocks seem to vibrate and hum and the crystal starts to glow.");
			player.message("The vibration in the area diminishes, but the crystal continues to glow.");
			player.getCarriedItems().remove(new Item(ItemId.A_RED_CRYSTAL.id()));
			player.getCarriedItems().getInventory().add(new Item(ItemId.A_GLOWING_RED_CRYSTAL.id()));
		}
		else if (obj.getID() == ANCIENT_LAVA_FURNACE) {
			switch (ItemId.getById(item.getCatalogId())) {
				case A_CHUNK_OF_CRYSTAL:
				case A_LUMP_OF_CRYSTAL:
				case A_HUNK_OF_CRYSTAL:
					if (getCurrentLevel(player, Skills.CRAFTING) < 50) {
						//message possibly non kosher
						player.message("You need a crafting ability of at least 50 to perform this task.");
						return;
					}
					if (!player.getCache().hasKey(item.getDef(player.getWorld()).getName().toLowerCase().replace(" ", "_"))) {
						player.getCache().store(item.getDef(player.getWorld()).getName().toLowerCase().replace(" ", "_"), true);
						player.getCarriedItems().remove(new Item(item.getCatalogId()));
						mes(config().GAME_TICK * 2, "You carefully place the piece of crystal into ",
							"a specially shaped compartment in the furnace.");
					}
					if (player.getCache().hasKey("a_chunk_of_crystal") && player.getCache().hasKey("a_lump_of_crystal") && player.getCache().hasKey("a_hunk_of_crystal")) {
						mes(config().GAME_TICK * 2, "You place the final segment of the crystal together into the ",
							"strangely shaped compartment, all the pieces seem to fit...",
							"You use your crafting skill to control the furnace.",
							"The heat in the furnace slowly rises and soon fuses the parts together...",
							"As soon as the item cools, you pick it up...",
							"As the crystal touches your hands a voice inside of your head says..",
							"@gre@Voice in head: Bring life to the dragons eye.");
						player.getCache().remove("a_chunk_of_crystal");
						player.getCache().remove("a_lump_of_crystal");
						player.getCache().remove("a_hunk_of_crystal");
						give(player, ItemId.A_RED_CRYSTAL.id(), 1);
					} else {
						mes(config().GAME_TICK * 2, "The compartment in the furnace isn't full yet.");
						mes(config().GAME_TICK, "It looks like you need more pieces of crystal.");
					}
					break;
				default:
					player.message("Nothing interesting happens");
					break;
			}
		}
		else if (obj.getID() == WOODEN_BEAM && item.getCatalogId() == ItemId.ROPE.id()) {
			player.message("You throw one end of the rope around the beam.");
			player.getCarriedItems().remove(new Item(ItemId.ROPE.id()));
			changeloc(obj, config().GAME_TICK * 8, WOODEN_BEAM + 1);
			if (!player.getCache().hasKey("legends_wooden_beam")) {
				player.getCache().store("legends_wooden_beam", true);
			}
		}
		else if (obj.getID() == CARVED_ROCK) {
			switch (ItemId.getById(item.getCatalogId())) {
				case SAPPHIRE:
				case EMERALD:
				case RUBY:
				case DIAMOND:
				case OPAL:
				case JADE:
				case RED_TOPAZ:
					int attachmentMode = -1;
					boolean alreadyAttached = false;
					if (item.getCatalogId() == ItemId.OPAL.id() && obj.getX() == 471 && obj.getY() == 3722) { // OPAL ROCK
						attachmentMode = 1;
					} else if (item.getCatalogId() == ItemId.EMERALD.id() && obj.getX() == 474 && obj.getY() == 3730) { // EMERALD ROCK
						attachmentMode = 2;
					} else if (item.getCatalogId() == ItemId.RUBY.id() && obj.getX() == 471 && obj.getY() == 3734) { // RUBY ROCK
						attachmentMode = 3;
					} else if (item.getCatalogId() == ItemId.DIAMOND.id() && obj.getX() == 466 && obj.getY() == 3739) { // DIAMOND ROCK
						attachmentMode = 4;
					} else if (item.getCatalogId() == ItemId.SAPPHIRE.id() && obj.getX() == 460 && obj.getY() == 3737) { // SAPPHIRE ROCK
						attachmentMode = 5;
					} else if (item.getCatalogId() == ItemId.RED_TOPAZ.id() && obj.getX() == 464 && obj.getY() == 3730) { // RED TOPAZ ROCK
						attachmentMode = 6;
					} else if (item.getCatalogId() == ItemId.JADE.id() && obj.getX() == 469 && obj.getY() == 3728) { // JADE ROCK
						attachmentMode = 7;
					}
					if (player.getCache().hasKey("legends_attach_" + attachmentMode)) {
						alreadyAttached = true;
						attachmentMode = -1;
					}
					if (alreadyAttached) {
						player.message("You have already placed an " + item.getDef(player.getWorld()).getName() + " above this rock.");
						createGroundItemDelayedRemove(new GroundItem(player.getWorld(), item.getCatalogId(), obj.getX(), obj.getY(), 1, player), config().GAME_TICK * 8);
						mes(config().GAME_TICK * 2, "A barely visible " + item.getDef(player.getWorld()).getName() + " becomes clear again, spinning above the rock.");
						player.message("And then fades again...");
					} else {
						if (attachmentMode != -1 && !player.getCarriedItems().hasCatalogID(ItemId.BOOKING_OF_BINDING.id(), Optional.empty())) {
							player.getCarriedItems().remove(new Item(item.getCatalogId()));
							player.message("You carefully move the gem closer to the rock.");
							player.message("The " + item.getDef(player.getWorld()).getName() + " glows and starts spinning as it hovers above the rock.");
							createGroundItemDelayedRemove(new GroundItem(player.getWorld(), item.getCatalogId(), obj.getX(), obj.getY(), 1, player), config().GAME_TICK * 8);
							if (!player.getCache().hasKey("legends_attach_" + attachmentMode)) {
								player.getCache().store("legends_attach_" + attachmentMode, true);
							}
							if (player.getCache().hasKey("legends_attach_1")
								&& player.getCache().hasKey("legends_attach_2")
								&& player.getCache().hasKey("legends_attach_3")
								&& player.getCache().hasKey("legends_attach_4")
								&& player.getCache().hasKey("legends_attach_5")
								&& player.getCache().hasKey("legends_attach_6")
								&& player.getCache().hasKey("legends_attach_7")) {
								mes(config().GAME_TICK * 2, "Suddenly all the crystals begin to glow very brightly.",
									"The room is lit up with the bright light...",
									"Soon, the light from all the crystals converges into a point.",
									"And you see a strange book appear where the light is focused.",
									"You pick the book up and place it in your inventory.",
									"All the crystals disapear...and the light fades...");
								give(player, ItemId.BOOKING_OF_BINDING.id(), 1);
								for (int i = 0; i < 8; i++) {
									if (player.getCache().hasKey("legends_attach_" + i)) {
										player.getCache().remove("legends_attach_" + i);
									}
								}
							}
						} else {
							player.message("You carefully move the gem closer to the rock.");
							player.message("but nothing happens...");
						}
					}
					break;
				default:
					player.message("Nothing interesting happens");
					break;
			}
		}
		else if (item.getCatalogId() == ItemId.MACHETTE.id() && obj.getID() == TALL_REEDS) {
			give(player, ItemId.CUT_REED_PLANT.id(), 1);
			mes(config().GAME_TICK * 2, "You use your machette to cut down a tall reed.",
				"You cut it into a length of pipe.");
		}
		else if (item.getCatalogId() == ItemId.BLESSED_GOLDEN_BOWL.id()) {
			if (player.getQuestStage(Quests.LEGENDS_QUEST) == 8 && player.getY() >= 3723 && player.getY() <= 3740) {
				player.message("You fill the bowl up with water..");
				player.getCarriedItems().remove(new Item(ItemId.BLESSED_GOLDEN_BOWL.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id()));
				return;
			}
			mes(config().GAME_TICK * 2, "The water is awkward to get to...",
				"The gap to the water is too narrow.");
		}
		else if (item.getCatalogId() == ItemId.CUT_REED_PLANT.id() && obj.getID() == SHALLOW_WATER) {
			if (atQuestStages(player, Quests.LEGENDS_QUEST, 5, 6, 7)) {
				mes(config().GAME_TICK * 2, "It looks as if this pool has dried up...",
					"A thick black sludge has replaced the sparkling pure water...",
					"There is a disgusting stench of death that emanates from this area...",
					"Maybe Gujuo knows what's happened...");
				if (player.getQuestStage(Quests.LEGENDS_QUEST) == 5) {
					player.updateQuestStage(Quests.LEGENDS_QUEST, 6);
				}
				return;
			}
			if((player.getQuestStage(Quests.LEGENDS_QUEST) >= 9 || player.getQuestStage(Quests.LEGENDS_QUEST) == -1)
					&& !config().LOOSE_SHALLOW_WATER_CHECK) {
				mes(config().GAME_TICK * 2, "You use the cut reed plant to syphon some water from the pool.",
						"You take a refreshing drink from the pool.",
						"The cut reed is soaked through with water and is now all soggy.");
				return;
			}

			int emptyID = -1;
			int refilledID = -1;
			for (int i = 0; i < REFILLABLE.length; i++) {
				if (player.getCarriedItems().hasCatalogID(REFILLABLE[i], Optional.of(false))) {
					emptyID = REFILLABLE[i];
					refilledID = REFILLED[i];
					break;
				}
			}
			if (emptyID != ItemId.NOTHING.id()) {
				mes(config().GAME_TICK * 2, "You use the cut reed plant to syphon some water from the pool.");
				if (emptyID == ItemId.GOLDEN_BOWL.id()) {
					mes(config().GAME_TICK * 2, "into your gold bowl.");
					player.getCarriedItems().remove(new Item(ItemId.GOLDEN_BOWL.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.GOLDEN_BOWL_WITH_PURE_WATER.id()));
					mes(config().GAME_TICK * 2, "The water doesn't seem to sparkle as much as it did in the pool.");
				} else if (emptyID == ItemId.BLESSED_GOLDEN_BOWL.id()) {
					mes(config().GAME_TICK * 2, "into your blessed gold bowl.");
					player.getCarriedItems().remove(new Item(ItemId.BLESSED_GOLDEN_BOWL.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id()));
					mes(config().GAME_TICK * 2, "The water seems to bubble and sparkle as if alive.");
				} else {
					mes(config().GAME_TICK * 2, "You put some water in the " + player.getWorld().getServer().getEntityHandler().getItemDef(emptyID).getName().toLowerCase() + ".");
					player.getCarriedItems().remove(new Item(emptyID));
					player.getCarriedItems().getInventory().add(new Item(refilledID));
				}
				player.getCarriedItems().remove(new Item(ItemId.CUT_REED_PLANT.id()));
				mes(0, "The cut reed is soaked through with water and is now all soggy.");
			} else {
				mes(config().GAME_TICK * 2, "You start to syphon some water up the tube...");
				mes(0, "But you have nothing to put the water in.");
			}
		}
	}

	private void replaceTotemPole(Player player, GameObject obj, boolean calledGujuo) {
		if (player.getCarriedItems().hasCatalogID(ItemId.TOTEM_POLE.id(), Optional.of(false))) {
			if (player.getQuestStage(Quests.LEGENDS_QUEST) == 9) {
				player.updateQuestStage(Quests.LEGENDS_QUEST, 10);
			}
			changeloc(obj, config().GAME_TICK * 16, 1170);
			player.getCarriedItems().remove(new Item(ItemId.TOTEM_POLE.id()));
			mes("You remove the evil totem pole.",
				"And replace it with the one you carved yourself.",
				"As you do so, you feel a lightness in the air,");
			player.message("almost as if the Kharazi jungle were sighing.");
			player.message("Perhaps Gujuo would like to see the totem pole.");
			if (calledGujuo) {
				Npc gujuo = addnpc(obj.getWorld(), NpcId.GUJUO.id(), player.getX(), player.getY(), (int)TimeUnit.SECONDS.toMillis(150));
				if (gujuo != null) {
					gujuo.initializeTalkScript(player);
				}
			}
		} else {
			player.message("I shall replace it with the Totem pole");
		}
	}

	private boolean blockDescendBeamPostQuest(Player player) {
		return player.getQuestStage(Quests.LEGENDS_QUEST) == -1 &&
			!config().LOCKED_POST_QUEST_REGIONS_ACCESSIBLE;
	}
}
