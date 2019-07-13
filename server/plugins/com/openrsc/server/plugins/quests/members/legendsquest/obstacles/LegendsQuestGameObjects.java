package com.openrsc.server.plugins.quests.members.legendsquest.obstacles;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.RegionManager;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.quests.members.legendsquest.npcs.LegendsQuestNezikchened;
import com.openrsc.server.plugins.skills.Mining;
import com.openrsc.server.plugins.skills.Thieving;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.atQuestStages;
import static com.openrsc.server.plugins.Functions.createGroundItemDelayedRemove;
import static com.openrsc.server.plugins.Functions.delayedSpawnObject;
import static com.openrsc.server.plugins.Functions.displayTeleportBubble;
import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcWalkFromPlayer;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.replaceObject;
import static com.openrsc.server.plugins.Functions.replaceObjectDelayed;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;
import static com.openrsc.server.plugins.Functions.spawnNpc;
import static com.openrsc.server.plugins.Functions.spawnNpcWithRadius;

public class LegendsQuestGameObjects implements ObjectActionListener, ObjectActionExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener {

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
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return inArray(obj.getID(), GRAND_VIZIERS_DESK, LEGENDS_CUPBOARD, TOTEM_POLE, ROCK, TALL_REEDS,
				SHALLOW_WATER, CAVE_ENTRANCE_LEAVE_DUNGEON, CRATE, TABLE, BOOKCASE, CAVE_ENTRANCE_FROM_BOULDERS, CRUDE_DESK,
				CAVE_ANCIENT_WOODEN_DOORS, HEAVY_METAL_GATE, HALF_BURIED_REMAINS, CARVED_ROCK, WOODEN_BEAM, WOODEN_BEAM + 1, ROPE_UP,
				RED_EYE_ROCK, ANCIENT_LAVA_FURNACE, CAVERNOUS_OPENING, ECHNED_ZEKIN_ROCK, CRAFTED_TOTEM_POLE, TOTEM_POLE + 1)
				|| inArray(obj.getID(), SMASH_BOULDERS) || (obj.getID() == CRUDE_BED && command.equalsIgnoreCase("search"));
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == ECHNED_ZEKIN_ROCK) {
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 8) {
				message(p, 1300, "The rock moves quite easily.");
				p.message("And the spirit of Echned Zekin seems to have disapeared.");
				replaceObjectDelayed(obj, 10000, SHALLOW_WATER);
				return;
			}
			p.setBusy(true);
			Npc echned = getNearestNpc(p, NpcId.ECHNED_ZEKIN.id(), 2);
			if (echned == null) {
				message(p, 1300, "A thick, green mist seems to emanate from the water...",
					"It slowly congeals into the shape of a body...");
				echned = spawnNpcWithRadius(p, NpcId.ECHNED_ZEKIN.id(), p.getX(), p.getY(), 0, 60000 * 3);
				if (echned != null) {
					p.setBusyTimer(3000);
					sleep(1300);
					message(p, echned, 1300, "Which slowly floats towards you.");
					echned.initializeTalkScript(p);
				}
				return;
			}
			if (echned != null) {
				echned.initializeTalkScript(p);
			}
			p.setBusy(false);
		}
		else if (obj.getID() == CAVERNOUS_OPENING) {
			if (command.equalsIgnoreCase("enter")) {
				if (p.getY() >= 3733) {
					p.message("You enter the dark cave...");
					p.teleport(395, 3725);
				} else {
					if (p.getCache().hasKey("cavernous_opening")) {
						message(p, 1300, "You walk carefully into the darkness of the cavern..");
						p.teleport(395, 3733);
					} else {
						message(p, 1300, "You walk into an invisible barrier...");
						message(p, 600, "Somekind of magical force will not allow you to pass into the cavern.");
					}
				}
			} else if (command.equalsIgnoreCase("search")) {
				if (p.getCache().hasKey("cavernous_opening")) {
					message(p, 1300, "You can see a glowing crystal shape in the wall.",
						"It looks like the Crystal is magical, ",
						"it allows access to the cavern.");
				} else {
					message(p, 1300, "You see a heart shaped depression in the wall next to the cavern.",
						"And a message reads...",
						"@gre@All ye who stand 'ere the dragons teeth,");
					message(p, 600, "@gre@Place your full true heart and proceed...");
				}
			}
		}
		else if (obj.getID() == ANCIENT_LAVA_FURNACE) {
			if (command.equalsIgnoreCase("look")) {
				message(p, 600, "This is an ancient looking furnace.");
			} else if (command.equalsIgnoreCase("search")) {
				message(p, 1300, "You search the lava furnace.",
					"You find a small compartment that you may be able to use.",
					"Strangely, it looks as if it is designed for a specific purpose...");
				message(p, 600, "to fuse things together at very high temperatures...");
			}
		}
		else if (obj.getID() == RED_EYE_ROCK) {
			message(p, 600, "These rocks look somehow manufactured..");
		}
		else if (obj.getID() == ROPE_UP) {
			p.message("You climb the rope back out again.");
			p.teleport(471, 3707);
		}
		else if (obj.getID() == WOODEN_BEAM + 1) {
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) >= 9 || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == -1) {
				message(p, 1300, "The rope snaps as you're about to climb down it.",
					"Perhaps you need a new rope.");
				return;
			}
			message(p, 1300, "This rope climb looks pretty dangerous,",
				"Are you sure you want to go down?");
			int menu = showMenu(p,
				"Yes,I'll go down the rope...",
				"No way do I want to go down there.");
			if (menu == 0) {
				message(p, 1300, "You prepare to climb down the rope...");
				playerTalk(p, null, "! Gulp !");
				sleep(1100);
				if (!p.getCache().hasKey("gujuo_potion")) {
					message(p, 1300, "...but a terrible fear grips you...");
					p.message("And you can go no further.");
				} else {
					int rnd = DataConversions.random(0, 4);
					if (rnd == 0) {
						message(p, 1300, "but fear stabs at your heart...",
								"and you lose concentration,");
						p.damage(DataConversions.random(10, 15));
					}
					else {
						message(p, 1300, "And although fear stabs at your heart...",
								"You shimmey down the rope...");
					}
					p.teleport(426, 3707);
				}
			} else if (menu == 1) {
				p.message("You decide not to go down the rope.");
			}
		}
		else if (obj.getID() == WOODEN_BEAM) {
			p.message("You search the wooden beam...");
			if (p.getCache().hasKey("legends_wooden_beam")) {
				p.message("You search the wooden beam and find the rope you attached.");
				replaceObjectDelayed(obj, 5000, WOODEN_BEAM + 1);
			} else {
				message(p, 1300, "You see nothing special about this...");
				p.message("Perhaps if you had a rope, it might be more functional.");
			}
		}
		else if (obj.getID() == CARVED_ROCK) {
			message(p, 1300, "You see a delicate inscription on the rock, it says,");
			message(p, 1900, "@gre@'Once there were crystals to make the pool shine,'");
			message(p, 0, "@gre@'Ordered in stature to retrieve what's mine.'");
			String gem = "";
			boolean attached = false;
			// opal
			if (obj.getX() == 471 && obj.getY() == 3722) {
				gem = "Opal";
				attached = p.getCache().hasKey("legends_attach_1");
			}
			// emerald
			else if (obj.getX() == 474 && obj.getY() == 3730) {
				gem = "Emerald";
				attached = p.getCache().hasKey("legends_attach_2");
			}
			// ruby
			else if (obj.getX() == 471 && obj.getY() == 3734) {
				gem = "Ruby";
				attached = p.getCache().hasKey("legends_attach_3");
			}
			// diamond
			else if (obj.getX() == 466 && obj.getY() == 3739) {
				gem = "Diamond";
				attached = p.getCache().hasKey("legends_attach_4");
			}
			// sapphire
			else if (obj.getX() == 460 && obj.getY() == 3737) {
				gem = "Sapphire";
				attached = p.getCache().hasKey("legends_attach_5");
			}
			// red topaz
			else if (obj.getX() == 464 && obj.getY() == 3730) {
				gem = "Topaz";
				attached = p.getCache().hasKey("legends_attach_6");
			}
			// jade
			else if (obj.getX() == 469 && obj.getY() == 3728) {
				gem = "Jade";
				attached = p.getCache().hasKey("legends_attach_7");
			}
			
			if (!gem.equals("") && attached) {
				message(p, 1300, "A barely visible " + gem + " becomes clear again, spinning above the rock.",
						"And then fades again...");
			}
		}
		else if (obj.getID() == HALF_BURIED_REMAINS) {
			message(p, "It looks as if some poor unfortunate soul died here.");
		}
		else if (obj.getID() == HEAVY_METAL_GATE) {
			if (command.equalsIgnoreCase("look")) {
				message(p, 1300, "This huge metal gate bars the way further...",
					"There is an intense and unpleasant feeling from this place.");
				p.message("And you can see why, shadowy flying creatures seem to hover in the still dark air.");
			} else if (command.equalsIgnoreCase("push")) {
				message(p, 1300, "You push the gates...they're very stiff...",
					"They won't budge with a normal push.",
					"Do you want to try to force them open with brute strength?");
				int menu = showMenu(p,
					"Yes, I'm very strong, I'll force them open.",
					"No, I'm having second thoughts.");
				if (menu == 0) {
					if (getCurrentLevel(p, Skills.STRENGTH) < 50) {
						p.message("You need a Strength of at least 50 to affect these gates.");
						return;
					}
					message(p, 1300, "You ripple your muscles...preparing too exert yourself...");
					playerTalk(p, null, "Hup!");
					message(p, 1300, "You brace yourself against the doors...");
					playerTalk(p, null, "Urghhhhh!");
					message(p, 1300, "You start to force against the gate..");
					playerTalk(p, null, "Arghhhhhhh!");
					message(p, 1300, "You push and push,");
					playerTalk(p, null, "Shhhhhhhshshehshsh");
					if (Formulae.failCalculation(p, Skills.STRENGTH, 50)) {
						message(p, 1300, "You just manage to force the gates open slightly, ",
							"just enough to force yourself through.");
						replaceObjectDelayed(obj, 2000, 181);
						if (p.getY() <= 3717) {
							p.teleport(441, 3719);
						} else {
							p.teleport(441, 3717);
						}
					} else {
						message(p, 1300, "but run out of steam before you're able to force the gates open.");
						p.message("The effort of trying to force the gates reduces your strength temporarily");
						p.getSkills().decrementLevel(Skills.STRENGTH);
					}
				} else if (menu == 1) {
					p.message("You decide against forcing the gates.");
				}
			}
		}
		else if (inArray(obj.getID(), SMASH_BOULDERS)) {
			if (hasItem(p, Mining.getAxe(p))) {
				if (getCurrentLevel(p, Skills.MINING) < 52) {
					p.message("You need a mining ability of at least 52 to affect these boulders.");
					return;
				}
				if (Formulae.failCalculation(p, Skills.MINING, 50)) {
					message(p, 1300, "You take a good swing at the rock with your pick...");
					replaceObjectDelayed(obj, 2000, 1143);
					if (obj.getID() == SMASH_BOULDERS[0] && p.getY() <= 3704) {
						p.teleport(441, 3707);
					} else if (obj.getID() == SMASH_BOULDERS[0] && p.getY() >= 3707) {
						p.teleport(442, 3704);
					} else if (obj.getID() == SMASH_BOULDERS[1] && p.getY() <= 3708) {
						p.teleport(441, 3711);
					} else if (obj.getID() == SMASH_BOULDERS[1] && p.getY() >= 3711) {
						p.teleport(441, 3708);
					} else if (obj.getID() == SMASH_BOULDERS[2] && p.getY() <= 3712) {
						p.teleport(441, 3715);
					} else if (obj.getID() == SMASH_BOULDERS[2] && p.getY() >= 3715) {
						p.teleport(441, 3712);
					}
					message(p, 1900, "...and smash it into smaller pieces.");
					p.message("Another large rock falls down replacing the one that you smashed.");
				} else {
					p.message("You fail to make a mark on the rocks.");
					p.message("You miss hit the rock and the vibration shakes your bones.");
					p.message("Your mining ability suffers...");
					p.getSkills().decrementLevel(Skills.MINING);
				}
			} else {
				message(p, "You'll need a pickaxe to smash your way through these boulders.");
			}
		}
		else if (obj.getID() == CAVE_ANCIENT_WOODEN_DOORS) {
			if (command.equalsIgnoreCase("open")) {
				if (p.getY() >= 3703) {
					message(p, 1300, "You push the doors open and walk through.");
					replaceObjectDelayed(obj, 2000, 497);
					p.teleport(442, 3701);
					sleep(2000);
					p.message("The doors make a satisfying 'CLICK' sound as they close.");
				} else {
					message(p, 1300, "You push on the doors...they're really shut..",
						"It looks as if they have a huge lock on it...");
					p.message("Although ancient, it looks very sophisticated...");
				}
			} else if (command.equalsIgnoreCase("pick lock")) {
				if (p.getY() >= 3703) {
					message(p, 1300, "You see a lever which you pull on to open the door.");
					replaceObjectDelayed(obj, 2000, 497);
					p.teleport(442, 3701);
					message(p, 1300, "You walk through the door.");
					p.message("The doors make a satisfying 'CLICK' sound as they close.");
				} else {
					if (getCurrentLevel(p, Skills.THIEVING) < 50) {
						p.message("You need a thieving level of at least 50 to attempt this.");
						return;
					}
					if (hasItem(p, 714)) {
						message(p, 1300, "You attempt to pick the lock..");
						p.message("It looks very sophisticated ...");
						playerTalk(p, null, "Hmmm, interesting...");
						sleep(1300);
						p.message("You carefully insert your lockpick into the lock.");
						playerTalk(p, null, "This will be a challenge...");
						sleep(1300);
						p.message("You feel for the pins and levers in the mechanism.");
						playerTalk(p, null, "Easy does it....");
						sleep(1300);
						if (Thieving.succeedPickLockThieving(p, 50)) {
							message(p, 1300, "@gre@'CLICK'");
							playerTalk(p, null, "Easy as pie...");
							sleep(1300);
							message(p, 1300, "You tumble the lock mechanism and the door opens easily.");
							p.incExp(Skills.THIEVING, 100, true);
							replaceObjectDelayed(obj, 2000, 497);
							p.teleport(441, 3703);
						} else {
							p.message("...but you don't manage to pick the lock.");
						}
					} else {
						message(p, 1300, "The mechanism for this lock looks very sophisticated...");
						p.message("you're unable to affect the lock without the proper tool..");
					}
				}
			}
		}
		else if (obj.getID() == CRUDE_DESK) {
			if (hasItem(p, ItemId.SHAMANS_TOME.id())) {
				message(p, 1300, "You search the desk ...");
				p.message("...but find nothing.");
			} else {
				message(p, 2500, "You search the desk ...");
				addItem(p, ItemId.SHAMANS_TOME.id(), 1);
				p.message("You find a book...it looks like an ancient tome...");
			}
		}
		else if (obj.getID() == BOOKCASE) {
			message(p, 1300, "You search the bookcase...",
				"And find a large gaping hole at the back.");
			p.message("Would you like to climb through the hole?");
			int menu = showMenu(p,
				"Yes, I'll climb through the hole.",
				"No, I'll stay here.");
			if (menu == 0) {
				message(p, 1300, "You climb through the hole in the wall..",
					"It's very narrow and you have to contort your body a lot.",
					"After some time, you  manage to wriggle out of a small cavern...");
				p.teleport(444, 3699);
			} else if (menu == 1) {
				p.message("You decide to stay where you are.");
			}
		}
		else if (obj.getID() == TABLE) {
			p.message("You start searching the table...");
			if (hasItem(p, ItemId.SCRAWLED_NOTES.id())) {
				p.message("You cannot find anything else in here.");
			} else {
				sleep(1300);
				addItem(p, ItemId.SCRAWLED_NOTES.id(), 1);
				message(p, 1300, "You find a scrap of paper with nonesense written on it.");
			}
		}
		else if (obj.getID() == CRUDE_BED && command.equalsIgnoreCase("search")) {
			p.message("You search the flea infested rags..");
			if (hasItem(p, ItemId.SCATCHED_NOTES.id())) {
				p.message("You cannot find anything else in here.");
			} else {
				sleep(1300);
				addItem(p, ItemId.SCATCHED_NOTES.id(), 1);
				message(p, 1300, "You find a scrap of paper with spidery writing on it.");
			}
		}
		else if (obj.getID() == CRATE) {
			p.message("You search the crate.");
			if (hasItem(p, ItemId.SCRIBBLED_NOTES.id())) {
				p.message("You cannot find anything else in here.");
			} else {
				sleep(1300);
				addItem(p, ItemId.SCRIBBLED_NOTES.id(), 1);
				message(p, 1300, "After some time you find a scrumpled up piece of paper.");
				p.message("It looks like rubbish...");
			}
		}
		else if (obj.getID() == CAVE_ENTRANCE_FROM_BOULDERS) {
			message(p, 1300, "You see a small cave entrance.",
				"Would you like to climb into it?");
			int menu = showMenu(p,
				"Yes, I'll climb into it.",
				"No, I'll stay where I am.");
			if (menu == 0) {
				p.message("You clamber into the small cave...");
				p.teleport(452, 3702);
			} else if (menu == 1) {
				p.message("You decide against climbing into the small, uncomfortable looking tunnel.");
			}
		}
		else if (obj.getID() == CAVE_ENTRANCE_LEAVE_DUNGEON) {
			message(p, 1300, "You crawl back out from the cavern...");
			p.teleport(452, 874);
		}
		else if (obj.getID() == SHALLOW_WATER) {
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 8 && p.getY() >= 3723 && p.getY() <= 3740) {
				p.message("A magical looking pool.");
				return;
			}
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) >= 5 || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == -1) {
				p.message("A disgusting sess pit of filth and stench...");
				return;
			}
			message(p, 0, "A bubbling brook with effervescent water...");
		}
		else if (obj.getID() == TALL_REEDS) {
			message(p, 1300, "These tall reeds look nice and long, ");
			message(p, 1300, "with a long tube for a stem.");
			message(p, 0, "They reach all the way down to the water.");
		}
		else if (obj.getID() == ROCK) {
			if (p.getCache().hasKey("legends_cavern") || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) >= 2 || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == -1) {
				if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 1) {
					message(p, 1200, "You see nothing significant...",
						"At first....");
				}
				message(p, 1200, "You see that there is a small crevice that you may be able to crawl though.?",
					"Would you like to try to crawl through, it looks quite an enclosed area.");
				int menu = showMenu(p,
					"Yes, I'll crawl through, I'm very athletic.",
					"No, I'm pretty scared of enclosed areas.");
				if (menu == 0) {
					if (getCurrentLevel(p, Skills.AGILITY) < 50) {
						p.message("You need an agility of 50 to even attempt this.");
						p.setBusy(false);
						return;
					}
					message(p, 1300, "You try to crawl through...",
						"You contort your body to fit the crevice.");
					if (Formulae.failCalculation(p, Skills.AGILITY, 50)) {
						message(p, 1300, "You adroitely squeeze serpent like into the crevice.",
							"You find a small narrow tunnel that goes for some distance.",
							"After some time, you find a small cave opening...and walk through.");
						p.teleport(461, 3700);
						if (p.getCache().hasKey("legends_cavern")) {
							p.getCache().remove("legends_cavern");
							p.updateQuestStage(Constants.Quests.LEGENDS_QUEST, 2);
						}
					} else {
						message(p, 3200, "You get cramped into a tiny space and start to suffocate.",
							"You wriggle and wriggle but you cannot get out..");
						message(p, 1300, "Eventually you manage to break free.",
							"But you scrape yourself very badly as your force your way out.",
							"And you're totally exhausted from the experience.");
						p.damage(5);
					}
				} else if (menu == 1) {
					message(p, 1200, "You decide against forcing yourself into the tiny crevice..",
						"And realise that you have much better things to do..",
						"Like visit Inn's and mine ore...");
				}
			} else {
				p.message("You see nothing significant.");
			}
		}
		else if (obj.getID() == TOTEM_POLE) { // BLACK
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) >= 10 || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == -1) {
				replaceObjectDelayed(obj, 10000, 1170);
				message(p, 1300, "This totem pole is truly awe inspiring.",
					"It depicts powerful Karamja jungle animals.",
					"It is very well carved and brings a sense of power ",
					"and spiritual fullfilment to anyone who looks at it.");
				return;
			}
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 9) {
				replaceTotemPole(p, obj, false);
				return;
			}
			message(p, 1300, "This totem pole looks very corrupted,",
				"there is a darkness about it that seems quite unnatural.",
				"You don't like to look at it for too long.");

		}
		else if (obj.getID() == TOTEM_POLE + 1) { // RED
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) >= 10 || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == -1) {
				message(p, 1300, "This totem pole is truly awe inspiring.",
					"It depicts powerful Karamja jungle animals.",
					"It is very well carved and brings a sense of power ",
					"and spiritual fullfilment to anyone who looks at it.");
				return;
			}
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 9) {
				replaceTotemPole(p, obj, false);
				return;
			}
			replaceObject(obj, new GameObject(obj.getLocation(), TOTEM_POLE, obj.getDirection(), obj.getType()));
			message(p, 1300, "This totem pole looks very corrupted,",
				"there is a darkness about it that seems quite unnatural.",
				"You don't like to look at it for too long.");

		}
		else if (obj.getID() == GRAND_VIZIERS_DESK) {
			p.message("You rap loudly on the desk.");
			Npc radimus = getNearestNpc(p, NpcId.SIR_RADIMUS_ERKLE_HOUSE.id(), 6);
			if (radimus != null) {
				radimus.teleport(517, 545);
				npcWalkFromPlayer(p, radimus);
				radimus.initializeTalkScript(p);
			} else {
				p.message("Sir Radimus Erkle is currently busy at the moment.");
			}
		}
		else if (obj.getID() == LEGENDS_CUPBOARD) {
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) >= 1 || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == -1) {
				if (hasItem(p, ItemId.MACHETTE.id())) {
					p.message("The cupboard is empty.");
				} else {
					message(p, 1200, "You open the cupboard and find a machette.",
						"You take it out and add it to your inventory.");
					addItem(p, ItemId.MACHETTE.id(), 1);
				}
			} else {
				p.message("@gre@Sir Radimus Erkle: You're not authorised to open that cupboard.");
			}
		}
		else if (obj.getID() == CRAFTED_TOTEM_POLE) {
			if (obj.getOwner().equals(p.getUsername())) {
				message(p, 1300, "This totem pole looks very heavy...");
				replaceObject(obj, new GameObject(obj.getLocation(), FERTILE_EARTH, obj.getDirection(), obj.getType()));
				addItem(p, ItemId.TOTEM_POLE.id(), 1);
				if (!p.getCache().hasKey("crafted_totem_pole")) {
					p.getCache().store("crafted_totem_pole", true);
				}
				p.message("Carrying this totem pole saps your strength...");
				p.getSkills().setLevel(Skills.STRENGTH, (int) (p.getSkills().getLevel(Skills.STRENGTH) * 0.9));
			} else {
				p.message("This is not your totem pole to carry.");
			}
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		return (item.getID() == ItemId.MACHETTE.id() && obj.getID() == TALL_REEDS)
				|| (item.getID() == ItemId.CUT_REED_PLANT.id() && obj.getID() == SHALLOW_WATER)
				|| (item.getID() == ItemId.BLESSED_GOLDEN_BOWL.id() && obj.getID() == SHALLOW_WATER)
				|| obj.getID() == CARVED_ROCK || (obj.getID() == WOODEN_BEAM && item.getID() == ItemId.ROPE.id())
				|| obj.getID() == ANCIENT_LAVA_FURNACE || (obj.getID() == RED_EYE_ROCK && item.getID() == ItemId.A_RED_CRYSTAL.id())
				|| (obj.getID() == CAVERNOUS_OPENING && item.getID() == ItemId.A_GLOWING_RED_CRYSTAL.id())
				|| (obj.getID() == FERTILE_EARTH && item.getID() == ItemId.YOMMI_TREE_SEED.id())
				|| (obj.getID() == FERTILE_EARTH && item.getID() == ItemId.GERMINATED_YOMMI_TREE_SEED.id())
				|| (obj.getID() == YOMMI_TREE && item.getID() == ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id())
				|| (inArray(obj.getID(), DEAD_YOMMI_TREE, ROTTEN_YOMMI_TREE, GROWN_YOMMI_TREE, CHOPPED_YOMMI_TREE, TRIMMED_YOMMI_TREE) && item.getID() == ItemId.RUNE_AXE.id())
				|| (obj.getID() == TOTEM_POLE && item.getID() == ItemId.TOTEM_POLE.id());
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == TOTEM_POLE && item.getID() == ItemId.TOTEM_POLE.id()) {
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) >= 10 || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == -1) {
				message(p, "You have already replaced the evil totem pole with your own.",
						"You feel a great sense of accomplishment");
				return;
			}
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 9) {
				replaceTotemPole(p, obj, true);
				return;
			}
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 8) {
				if (p.getCache().hasKey("killed_viyeldi") && !p.getCache().hasKey("viyeldi_companions")) {
					p.getCache().set("viyeldi_companions", 1);
				}
				message(p, "You attempt to replace the evil totem pole.",
					"A black cloud emanates from the evil totem pole.");
				p.message("It slowly forms into the dread demon Nezikchened...");
				LegendsQuestNezikchened.demonFight(p);
			}
		}
		else if (obj.getID() == TRIMMED_YOMMI_TREE && item.getID() == ItemId.RUNE_AXE.id()) {
			if (obj.getOwner().equals(p.getUsername())) {
				int objectX = obj.getX();
				int objectY = obj.getY();
				message(p, 1300, "You craft a totem pole out of the Yommi tree.");
				replaceObject(obj, new GameObject(obj.getLocation(), CRAFTED_TOTEM_POLE, obj.getDirection(), obj.getType(), p.getUsername()));
				Server.getServer().getEventHandler().add(new SingleEvent(null, 60000, "Legends Quest Craft Totem Pole") {
					public void action() {
						GameObject whatObject = RegionManager.getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY));
						if (whatObject != null && whatObject.getID() == CRAFTED_TOTEM_POLE) {
							World.getWorld().registerGameObject(new GameObject(obj.getLocation(), FERTILE_EARTH, obj.getDirection(), obj.getType()));
						}
					}
				});
			} else {
				p.message("This is not your Yommi Tree.");
			}
		}
		else if (obj.getID() == CHOPPED_YOMMI_TREE && item.getID() == ItemId.RUNE_AXE.id()) {
			if (obj.getOwner().equals(p.getUsername())) {
				int objectX = obj.getX();
				int objectY = obj.getY();
				message(p, 1300, "You professionally wield your Rune Axe...",
					"As you trim the branches from the Yommi tree.");
				replaceObject(obj, new GameObject(obj.getLocation(), TRIMMED_YOMMI_TREE, obj.getDirection(), obj.getType(), p.getUsername()));
				Server.getServer().getEventHandler().add(new SingleEvent(null, 60000, "Legend Quest Trim Yommi Tree") {
					public void action() {
						GameObject whatObject = RegionManager.getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY));
						if (whatObject != null && whatObject.getID() == TRIMMED_YOMMI_TREE) {
							World.getWorld().registerGameObject(new GameObject(obj.getLocation(), FERTILE_EARTH, obj.getDirection(), obj.getType()));
						}
					}
				});
			} else {
				p.message("This is not your Yommi Tree.");
			}
		}
		else if (obj.getID() == GROWN_YOMMI_TREE && item.getID() == ItemId.RUNE_AXE.id()) {
			if (obj.getOwner().equals(p.getUsername())) {
				int objectX = obj.getX();
				int objectY = obj.getY();
				message(p, 1300, "You wield the Rune Axe and prepare to chop the Yommi tree.");
				replaceObject(obj, new GameObject(obj.getLocation(), CHOPPED_YOMMI_TREE, obj.getDirection(), obj.getType(), p.getUsername()));
				Server.getServer().getEventHandler().add(new SingleEvent(null, 60000, "Legend Quest Chop Yommi Tree") {
					public void action() {
						GameObject whatObject = RegionManager.getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY));
						if (whatObject != null && whatObject.getID() == CHOPPED_YOMMI_TREE) {
							World.getWorld().registerGameObject(new GameObject(obj.getLocation(), FERTILE_EARTH, obj.getDirection(), obj.getType()));
						}
					}
				});
				message(p, 1300, "You chop the Yommi tree down.",
					"Perhaps you should trim those branches ?");
			} else {
				p.message("This is not your Yommi Tree.");
			}
		}
		else if ((obj.getID() == DEAD_YOMMI_TREE || obj.getID() == ROTTEN_YOMMI_TREE) && item.getID() == ItemId.RUNE_AXE.id()) {
			message(p, 0, "You chop the dead Yommi Tree down.");
			message(p, 1300, "You gain some logs..");
			replaceObject(obj, new GameObject(obj.getLocation(), FERTILE_EARTH, obj.getDirection(), obj.getType()));
			addItem(p, ItemId.LOGS.id(), 1);
		}
		else if (obj.getID() == YOMMI_TREE && item.getID() == ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id()) {
			int objectX = obj.getX();
			int objectY = obj.getY();
			p.getInventory().replace(ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id(), ItemId.BLESSED_GOLDEN_BOWL.id());
			displayTeleportBubble(p, obj.getX(), obj.getY(), true);
			message(p, 1300, "You water the Yommi tree from the golden bowl...",
				"It grows at a remarkable rate.");
			replaceObject(obj, new GameObject(obj.getLocation(), GROWN_YOMMI_TREE, obj.getDirection(), obj.getType(), p.getUsername()));
			Server.getServer().getEventHandler().add(new SingleEvent(null, 15000, "Legend Quest Water Yommi Tree") {
				public void action() {
					GameObject whatObject = RegionManager.getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY));
					if (whatObject != null && whatObject.getID() == GROWN_YOMMI_TREE) {
						World.getWorld().registerGameObject(new GameObject(obj.getLocation(), ROTTEN_YOMMI_TREE, obj.getDirection(), obj.getType()));
						if (p.isLoggedIn()) {
							p.message("The Yommi tree is past it's prime and dies .");
						}
						delayedSpawnObject(obj.getLoc(), 60000);
					}
				}
			});
			message(p, 1300, "Soon the tree stops growing...",
				"It looks tall enough now to make a good totem pole.");
		}
		else if (obj.getID() == FERTILE_EARTH && item.getID() == ItemId.YOMMI_TREE_SEED.id()) {
			p.message("These seeds need to be germinated in pure water before they");
			p.message("can be planted in the fertile soil.");
		}
		else if (obj.getID() == FERTILE_EARTH && item.getID() == ItemId.GERMINATED_YOMMI_TREE_SEED.id()) {
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) != 8 || !hasItem(p, ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id())) {
				p.message("You'll need some sacred water to feed ");
				p.message("the tree when it starts growing.");
				return;
			}
			if (!hasItem(p, ItemId.RUNE_AXE.id())) {
				p.message("You'll need a very tough, very sharp axe to");
				p.message("fell the tree once it is grown.");
				return;
			}
			if (getCurrentLevel(p, Skills.WOODCUT) < 50) {
				p.message("You need an woodcut level of 50 to");
				p.message("fell the tree once it is grown.");
				return;
			}
			if (getCurrentLevel(p, Skills.HERBLAW) < 45) {
				p.message("You need a herblaw skill of at least 45 to complete this task.");
				return;
			}
			// 1112, 1107
			// 1172
			removeItem(p, ItemId.GERMINATED_YOMMI_TREE_SEED.id(), 1);
			if (DataConversions.random(0, 1) != 1) {
				int objectX = obj.getX();
				int objectY = obj.getY();
				replaceObject(obj, new GameObject(obj.getLocation(), BABY_YOMMI_TREE, obj.getDirection(), obj.getType()));
				message(p, 1300, "You bury the Germinated Yommi tree seed in the fertile earth...",
					"You start to see something growing.");
				replaceObject(obj, new GameObject(obj.getLocation(), YOMMI_TREE, obj.getDirection(), obj.getType()));
				Server.getServer().getEventHandler().add(new SingleEvent(null, 15000, "Legends Quest Grow Yommi Tree") {
					public void action() {
						GameObject whatObject = RegionManager.getRegion(Point.location(objectX, objectY)).getGameObject(Point.location(objectX, objectY));
						if (whatObject != null && whatObject.getID() == YOMMI_TREE) {
							World.getWorld().registerGameObject(new GameObject(obj.getLocation(), DEAD_YOMMI_TREE, obj.getDirection(), obj.getType(), p.getUsername()));
							if (p.isLoggedIn()) {
								p.message("The Sapling dies.");
							}
							delayedSpawnObject(obj.getLoc(), 60000);
						}
					}
				});
				p.message("The plant grows at a remarkable rate.");
				p.message("It looks as if the tree needs to be watered...");
			} else {
				p.message("You planted the seed incorrectly, it withers and dies.");
			}
		}
		else if (obj.getID() == CAVERNOUS_OPENING && item.getID() == ItemId.A_GLOWING_RED_CRYSTAL.id()) {
			message(p, 1300, "You carefully place the glowing heart shaped crystal into ",
				"the depression, it slots in perfectly and glows even brighter.",
				"You hear a snapping sound coming from in front of the cave.");
			removeItem(p, item.getID(), 1);
			if (!p.getCache().hasKey("cavernous_opening")) {
				p.getCache().store("cavernous_opening", true);
			}
		}
		else if (obj.getID() == RED_EYE_ROCK && item.getID() == ItemId.A_RED_CRYSTAL.id()) {
			message(p, 1300, "You carefully place the Dragon Crystal on the rock.",
				"The rocks seem to vibrate and hum and the crystal starts to glow.");
			p.message("The vibration in the area diminishes, but the crystal continues to glow.");
			p.getInventory().replace(ItemId.A_RED_CRYSTAL.id(), ItemId.A_GLOWING_RED_CRYSTAL.id());
		}
		else if (obj.getID() == ANCIENT_LAVA_FURNACE) {
			switch (ItemId.getById(item.getID())) {
				case A_CHUNK_OF_CRYSTAL:
				case A_LUMP_OF_CRYSTAL:
				case A_HUNK_OF_CRYSTAL:
					if (getCurrentLevel(p, Skills.CRAFTING) < 50) {
						//message possibly non kosher
						p.message("You need a crafting ability of at least 50 to perform this task.");
						return;
					}
					if (!p.getCache().hasKey(item.getDef().getName().toLowerCase().replace(" ", "_"))) {
						p.getCache().store(item.getDef().getName().toLowerCase().replace(" ", "_"), true);
						removeItem(p, item.getID(), 1);
						message(p, 1300, "You carefully place the piece of crystal into ",
							"a specially shaped compartment in the furnace.");
					}
					if (p.getCache().hasKey("a_chunk_of_crystal") && p.getCache().hasKey("a_lump_of_crystal") && p.getCache().hasKey("a_hunk_of_crystal")) {
						message(p, 1300, "You place the final segment of the crystal together into the ",
							"strangely shaped compartment, all the pieces seem to fit...",
							"You use your crafting skill to control the furnace.",
							"The heat in the furnace slowly rises and soon fuses the parts together...",
							"As soon as the item cools, you pick it up...",
							"As the crystal touches your hands a voice inside of your head says..",
							"@gre@Voice in head: Bring life to the dragons eye.");
						p.getCache().remove("a_chunk_of_crystal");
						p.getCache().remove("a_lump_of_crystal");
						p.getCache().remove("a_hunk_of_crystal");
						addItem(p, ItemId.A_RED_CRYSTAL.id(), 1);
					} else {
						message(p, 1300, "The compartment in the furnace isn't full yet.");
						message(p, 600, "It looks like you need more pieces of crystal.");
					}
					break;
				default:
					p.message("Nothing interesting happens");
					break;
			}
		}
		else if (obj.getID() == WOODEN_BEAM && item.getID() == ItemId.ROPE.id()) {
			p.message("You throw one end of the rope around the beam.");
			removeItem(p, ItemId.ROPE.id(), 1);
			replaceObjectDelayed(obj, 5000, WOODEN_BEAM + 1);
			if (!p.getCache().hasKey("legends_wooden_beam")) {
				p.getCache().store("legends_wooden_beam", true);
			}
		}
		else if (obj.getID() == CARVED_ROCK) {
			switch (ItemId.getById(item.getID())) {
				case SAPPHIRE:
				case EMERALD:
				case RUBY:
				case DIAMOND:
				case OPAL:
				case JADE:
				case RED_TOPAZ:
					int attachmentMode = -1;
					boolean alreadyAttached = false;
					if (item.getID() == ItemId.OPAL.id() && obj.getX() == 471 && obj.getY() == 3722) { // OPAL ROCK
						attachmentMode = 1;
					} else if (item.getID() == ItemId.EMERALD.id() && obj.getX() == 474 && obj.getY() == 3730) { // EMERALD ROCK
						attachmentMode = 2;
					} else if (item.getID() == ItemId.RUBY.id() && obj.getX() == 471 && obj.getY() == 3734) { // RUBY ROCK
						attachmentMode = 3;
					} else if (item.getID() == ItemId.DIAMOND.id() && obj.getX() == 466 && obj.getY() == 3739) { // DIAMOND ROCK
						attachmentMode = 4;
					} else if (item.getID() == ItemId.SAPPHIRE.id() && obj.getX() == 460 && obj.getY() == 3737) { // SAPPHIRE ROCK
						attachmentMode = 5;
					} else if (item.getID() == ItemId.RED_TOPAZ.id() && obj.getX() == 464 && obj.getY() == 3730) { // RED TOPAZ ROCK
						attachmentMode = 6;
					} else if (item.getID() == ItemId.JADE.id() && obj.getX() == 469 && obj.getY() == 3728) { // JADE ROCK
						attachmentMode = 7;
					}
					if (p.getCache().hasKey("legends_attach_" + attachmentMode)) {
						alreadyAttached = true;
						attachmentMode = -1;
					}
					if (alreadyAttached) {
						p.message("You have already placed an " + item.getDef().getName() + " above this rock.");
						createGroundItemDelayedRemove(new GroundItem(item.getID(), obj.getX(), obj.getY(), 1, p), 5000);
						message(p, 1300, "A barely visible " + item.getDef().getName() + " becomes clear again, spinning above the rock.");
						p.message("And then fades again...");
					} else {
						if (attachmentMode != -1 && !hasItem(p, ItemId.BOOKING_OF_BINDING.id())) {
							removeItem(p, item.getID(), 1);
							p.message("You carefully move the gem closer to the rock.");
							p.message("The " + item.getDef().getName() + " glows and starts spinning as it hovers above the rock.");
							createGroundItemDelayedRemove(new GroundItem(item.getID(), obj.getX(), obj.getY(), 1, p), 5000);
							if (!p.getCache().hasKey("legends_attach_" + attachmentMode)) {
								p.getCache().store("legends_attach_" + attachmentMode, true);
							}
							if (p.getCache().hasKey("legends_attach_1")
								&& p.getCache().hasKey("legends_attach_2")
								&& p.getCache().hasKey("legends_attach_3")
								&& p.getCache().hasKey("legends_attach_4")
								&& p.getCache().hasKey("legends_attach_5")
								&& p.getCache().hasKey("legends_attach_6")
								&& p.getCache().hasKey("legends_attach_7")) {
								message(p, 1300, "Suddenly all the crystals begin to glow very brightly.",
									"The room is lit up with the bright light...",
									"Soon, the light from all the crystals converges into a point.",
									"And you see a strange book appear where the light is focused.",
									"You pick the book up and place it in your inventory.",
									"All the crystals disapear...and the light fades...");
								addItem(p, ItemId.BOOKING_OF_BINDING.id(), 1);
								for (int i = 0; i < 8; i++) {
									if (p.getCache().hasKey("legends_attach_" + i)) {
										p.getCache().remove("legends_attach_" + i);
									}
								}
							}
						} else {
							p.message("You carefully move the gem closer to the rock.");
							p.message("but nothing happens...");
						}
					}
					break;
				default:
					p.message("Nothing interesting happens");
					break;
			}
		}
		else if (item.getID() == ItemId.MACHETTE.id() && obj.getID() == TALL_REEDS) {
			addItem(p, ItemId.CUT_REED_PLANT.id(), 1);
			message(p, 1300, "You use your machette to cut down a tall reed.",
				"You cut it into a length of pipe.");
		}
		else if (item.getID() == ItemId.BLESSED_GOLDEN_BOWL.id()) {
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 8 && p.getY() >= 3723 && p.getY() <= 3740) {
				p.message("You fill the bowl up with water..");
				p.getInventory().replace(ItemId.BLESSED_GOLDEN_BOWL.id(), ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id());
				return;
			}
			message(p, 1300, "The water is awkward to get to...",
				"The gap to the water is too narrow.");
		}
		else if (item.getID() == ItemId.CUT_REED_PLANT.id() && obj.getID() == SHALLOW_WATER) {
			if (atQuestStages(p, Constants.Quests.LEGENDS_QUEST, 5, 6, 7)) {
				message(p, 1300, "It looks as if this pool has dried up...",
					"A thick black sludge has replaced the sparkling pure water...",
					"There is a disgusting stench of death that emanates from this area...",
					"Maybe Gujuo knows what's happened...");
				if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 5) {
					p.updateQuestStage(Constants.Quests.LEGENDS_QUEST, 6);
				}
				return;
			}
			if((p.getQuestStage(Constants.Quests.LEGENDS_QUEST) >= 9 || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == -1) 
					&& !Constants.GameServer.LOOSE_SHALLOW_WATER_CHECK) {
				message(p, 1300, "You use the cut reed plant to syphon some water from the pool.",
						"You take a refreshing drink from the pool.",
						"The cut reed is soaked through with water and is now all soggy.");
				return;
			}
			
			int emptyID = -1;
			int refilledID = -1;
			for (int i = 0; i < REFILLABLE.length; i++) {
				if (hasItem(p, REFILLABLE[i])) {
					emptyID = REFILLABLE[i];
					refilledID = REFILLED[i];
					break;
				}
			}
			if (emptyID != ItemId.NOTHING.id()) {
				message(p, 1300, "You use the cut reed plant to syphon some water from the pool.");
				if (emptyID == ItemId.GOLDEN_BOWL.id()) {
					message(p, 1300, "into your gold bowl.");
					p.getInventory().replace(ItemId.GOLDEN_BOWL.id(), ItemId.GOLDEN_BOWL_WITH_PURE_WATER.id());
					message(p, 1300, "The water doesn't seem to sparkle as much as it did in the pool.");
				} else if (emptyID == ItemId.BLESSED_GOLDEN_BOWL.id()) {
					message(p, 1300, "into your blessed gold bowl.");
					p.getInventory().replace(ItemId.BLESSED_GOLDEN_BOWL.id(), ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id());
					message(p, 1300, "The water seems to bubble and sparkle as if alive.");
				} else {
					message(p, 1300, "You put some water in the " + EntityHandler.getItemDef(emptyID).getName().toLowerCase() + ".");
					p.getInventory().replace(emptyID, refilledID);
				}
				removeItem(p, ItemId.CUT_REED_PLANT.id(), 1);
				message(p, 0, "The cut reed is soaked through with water and is now all soggy.");
			} else {
				message(p, 1300, "You start to syphon some water up the tube...");
				message(p, 0, "But you have nothing to put the water in.");
			}
		}
	}

	private void replaceTotemPole(Player p, GameObject obj, boolean calledGujuo) {
		if (hasItem(p, ItemId.TOTEM_POLE.id())) {
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 9) {
				p.updateQuestStage(Constants.Quests.LEGENDS_QUEST, 10);
			}
			replaceObjectDelayed(obj, 10000, 1170);
			removeItem(p, ItemId.TOTEM_POLE.id(), 1);
			message(p, "You remove the evil totem pole.",
				"And replace it with the one you carved yourself.",
				"As you do so, you feel a lightness in the air,");
			p.message("almost as if the Kharazi jungle were sighing.");
			p.message("Perhaps Gujuo would like to see the totem pole.");
			if (calledGujuo) {
				Npc gujuo = spawnNpc(NpcId.GUJUO.id(), p.getX(), p.getY(), 60000 * 3);
				if (gujuo != null) {
					gujuo.initializeTalkScript(p);
				}
			}
		} else {
			p.message("I shall replace it with the Totem pole");
		}
	}
}
