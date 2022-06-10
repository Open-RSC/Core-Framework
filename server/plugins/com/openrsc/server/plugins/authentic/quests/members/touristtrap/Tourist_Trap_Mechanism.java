package com.openrsc.server.plugins.authentic.quests.members.touristtrap;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Tourist_Trap_Mechanism implements RemoveObjTrigger, UseNpcTrigger, OpLocTrigger, UseLocTrigger, UseInvTrigger, TakeObjTrigger,
	DropObjTrigger, TalkNpcTrigger {

	private static int MINING_CAVE = 963;
	private static int MINING_CART = 976;
	private static int MINING_CAVE_BACK = 964;
	private static int TRACK = 974;
	private static int MINING_BARREL = 967;
	private static int LIFT_PLATFORM = 977;
	private static int LIFT_UP = 966;
	private static int MINING_CART_ABOVE = 1025;
	private static int DISTURBED_SAND1 = 944;
	private static int DISTURBED_SAND2 = 945;

	public static double[] protoDartSmithRates;
	public static double[] protoDartFletchRates;

	@Override
	public boolean blockRemoveObj(Player player, Integer invIndex, UnequipRequest request) {
		return (request.item.getCatalogId() == ItemId.SLAVES_ROBE_BOTTOM.id() || request.item.getCatalogId() == ItemId.SLAVES_ROBE_TOP.id()) && (request.player.getLocation().inTouristTrapCave()) && request.player.getQuestStage(Quests.TOURIST_TRAP) != -1;
	}

	@Override
	public void onRemoveObj(Player player, Integer invIndex, UnequipRequest request) {
		Item item = request.item;
		Player requestPlayer = request.player;
		if ((item.getCatalogId() == ItemId.SLAVES_ROBE_BOTTOM.id() || item.getCatalogId() == ItemId.SLAVES_ROBE_TOP.id()) && (requestPlayer.getLocation().inTouristTrapCave()) && requestPlayer.getQuestStage(Quests.TOURIST_TRAP) != -1) {
			if (!requestPlayer.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(requestPlayer, item, UnequipRequest.RequestType.CHECK_IF_EQUIPMENT_TAB, true))) {
				return;
			}

			Npc n = ifnearvisnpc(requestPlayer, NpcId.MERCENARY.id(), 5);
			if (n != null) {
				n.teleport(requestPlayer.getX(), requestPlayer.getY());
				requestPlayer.teleport(requestPlayer.getX(), requestPlayer.getY());
				delay();
				npcsay(requestPlayer, n, "Oi! What are you doing down here?",
					"You're no slave!");
				n.startCombat(requestPlayer);
			} else {
				requestPlayer.teleport(requestPlayer.getX(), requestPlayer.getY());
				Npc newNpc = addnpc(requestPlayer.getWorld(), NpcId.MERCENARY.id(), requestPlayer.getX(), requestPlayer.getY(), 30000);
				delay();
				npcsay(requestPlayer, newNpc, "Oi! What are you doing down here?",
					"You're no slave!");
				newNpc.startCombat(requestPlayer);
			}
		}
	}

	public boolean isPineappleBased(Item item) {
		return !item.getNoted() && DataConversions.inArray(new int[]{ItemId.TENTI_PINEAPPLE.id(), ItemId.PINEAPPLE.id(), ItemId.FRESH_PINEAPPLE.id(),
			ItemId.PINEAPPLE_CHUNKS.id(), ItemId.PINEAPPLE_RING.id()}, item.getCatalogId());
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return (item.getCatalogId() == ItemId.TECHNICAL_PLANS.id() && npc.getID() == NpcId.BEDABIN_NOMAD_GUARD.id())
				|| (item.getCatalogId() == ItemId.TECHNICAL_PLANS.id() && npc.getID() == NpcId.AL_SHABIM.id())
				|| (isPineappleBased(item) && npc.getID() == NpcId.MERCENARY_ESCAPEGATES.id())
				|| (item.getCatalogId() == ItemId.MINING_BARREL.id() && npc.getID() == NpcId.ANA.id());
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (item.getCatalogId() == ItemId.TECHNICAL_PLANS.id() && npc.getID() == NpcId.BEDABIN_NOMAD_GUARD.id()) {
			if (player.getQuestStage(Quests.TOURIST_TRAP) > 7
				|| player.getQuestStage(Quests.TOURIST_TRAP) == -1) {
				npcsay(player, npc, "Sorry, but you can't use the tent without permission.",
					"But thanks for all your help with the Bedabin people.",
					"And we'll take those plans off your hands as well!");
			} else if (player.getQuestStage(Quests.TOURIST_TRAP) == 6 || player.getQuestStage(Quests.TOURIST_TRAP) == 7) {
				npcsay(player, npc, "Ok, you can go in, Al Shabim has told me about you.");
				player.teleport(171, 792);
			} else if (player.getQuestStage(Quests.TOURIST_TRAP) >= 0) {
				npcsay(player, npc, "Hmm, those plans look interesting.",
					"Go and show them to Al Shabim...",
					"I'm sure he'll be pleased to see them.");
			}
		}
		else if (item.getCatalogId() == ItemId.TECHNICAL_PLANS.id() && npc.getID() == NpcId.AL_SHABIM.id()) {
			TouristTrap.indirectTalktoAlShabim(player, npc);
		}
		else if (!item.getNoted() && isPineappleBased(item) && npc.getID() == NpcId.MERCENARY_ESCAPEGATES.id()) {
			if (item.getCatalogId() == ItemId.TENTI_PINEAPPLE.id()) {
				player.getCarriedItems().remove(new Item(ItemId.TENTI_PINEAPPLE.id()));
				npcsay(player, npc, "Great! Just what I've been looking for!",
					"Mmmmmmm, delicious!!",
					"Oh, this is soo nice!",
					"Mmmmm, *SLURP*",
					"Yummmm....Oh yes, this is great.");
				if (player.getQuestStage(Quests.TOURIST_TRAP) == 8) {
					player.updateQuestStage(Quests.TOURIST_TRAP, 9);
				}
			} else {
				npcsay(player, npc, "Oh great!");
				mes("The guard rolls his eyes in glee.");
				delay(2);
				mes("and takes a bite of the pineapple.");
				delay(2);
				mes("His face turns from pleasure to pain as he spits the mouthful of pineapple out.");
				delay(3);
				npcsay(player, npc, "Yeuch!",
					"That's awful! That's not Tenti pineapple,",
					"Get me some Tenti pineapple if you know what's good for you.");
			}
		}
		else if (item.getCatalogId() == ItemId.MINING_BARREL.id() && npc.getID() == NpcId.ANA.id()) {
			if (player.getQuestStage(Quests.TOURIST_TRAP) == -1) {
				player.message("You have already completed this quest.");
				npcsay(player, npc, "I think you might have me confused with someone else.");
				return;
			}
			if (!player.getCarriedItems().hasCatalogID(ItemId.ANA_IN_A_BARREL.id(), Optional.of(false))) {
				boolean isFirstTime = !player.getCache().hasKey("tried_ana_barrel");
				if (player.getCache().hasKey("ana_lift") || player.getCache().hasKey("ana_cart")
					|| player.getCache().hasKey("ana_in_cart")) {
					mes("Oh, here's Ana, the guards must have discovered her.");
					delay(3);
					mes("And sent her back to the mines...");
					delay(3);
				}
				if (isFirstTime) {
					npcsay(player, npc, "Hey, what do you think you're doing?",
						"Harumph!");
				} else {
					npcsay(player, npc, "Hey, what do you think you're doing?",
						"Leave me alone and let me get on with my work.",
						"Else we'll both be in trouble.",
						"Oh no, NOT AGAIN!",
						"Harumph!");
				}
				say(player, npc, "Shush...It's for your own good!");
				mes("You manage to squeeze Ana into the barrel,");
				delay(3);
				mes("despite her many complaints.");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.MINING_BARREL.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.ANA_IN_A_BARREL.id()));
				if (npc != null) {
					npc.remove();
				}
				if (isFirstTime) {
					player.getCache().store("tried_ana_barrel", true);
				}
			} else {
				player.message("You already have Ana in a barrel, you can't get two in there!");
			}
		}
	}

	private void makeDartTip(Player player, GameObject obj) {
		if (obj.getID() == 1006) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.TECHNICAL_PLANS.id(), Optional.of(false))) {
				mes("This anvil is experimental...");
				delay(2);
				mes("You need detailed plans of the item you want to make in order to use it.");
				return;
			}
			mes("Do you want to follow the technical plans ?");
			int menu = multi(player, "Yes. I'd like to try.", "No, not just yet.");
			if (menu == 0) {
				if (!player.getCarriedItems().hasCatalogID(ItemId.HAMMER.id(), Optional.of(false))) {
					player.message("You need a hammer to work anything on the anvil.");
					return;
				}
				if (getCurrentLevel(player, Skill.SMITHING.id()) < 20) {
					player.message("You need level 20 in smithing before you can attempt this.");
					return;
				}
				mes("You begin experimenting in forging the weapon...");
				delay(2);
				player.getCarriedItems().remove(new Item(ItemId.BRONZE_BAR.id()));
				mes("You follow the plans carefully.");
				delay(2);
				mes("And after a long time of careful work.");
				delay(2);
				if (protoDartSmithSuccessful(player.getSkills().getLevel(Skill.SMITHING.id()))) {
					mes("You finally manage to forge a sharp, pointed...");
					delay(2);
					mes("... dart tip...");
					delay(2);
					if (!player.getCarriedItems().hasCatalogID(ItemId.PROTOTYPE_DART_TIP.id(), Optional.of(false))) {
						give(player, ItemId.PROTOTYPE_DART_TIP.id(), 1);
					}
					mes("You study the technical plans even more...");
					mes("You need to attach feathers to the tip to complete the weapon.");
				} else {
					mes("You waste the bronze bar through an unlucky accident.");
				}
			} else if (menu == 1) {
				player.message("You decide not follow the technical plans.");
			}
		}
	}

	private void attachFeathersToPrototype(Player player, Item i, Item i2) {
		if (compareItemsIds(i, i2, ItemId.FEATHER.id(), ItemId.PROTOTYPE_DART_TIP.id())) {
			if (!ifheld(player, ItemId.FEATHER.id(), 10)) {
				player.message("You need at least ten feathers to make this item.");
				return;
			}
			if (getCurrentLevel(player, Skill.FLETCHING.id()) < 10) {
				player.message("You need a fletching level of at least 10 to complete this.");
				return;
			}
			mes("You try to attach feathers to the bronze dart tip.");
			delay(2);
			mes("Following the plans is tricky, but you persevere.");
			delay(2);
			if (player.getCarriedItems().remove(new Item(ItemId.FEATHER.id(), 10)) == -1) return;
			if (protoDartFletchSuccessful(player.getSkills().getLevel(Skill.FLETCHING.id()))) {
				mes("You succesfully attach the feathers to the dart tip.");
				delay(2);
				if (player.getCarriedItems().remove(new Item(ItemId.PROTOTYPE_DART_TIP.id())) == -1) return;
				player.getCarriedItems().getInventory().add(new Item(ItemId.PROTOTYPE_THROWING_DART.id()));
				//kosher: dependent on fletching level!
				player.incExp(Skill.FLETCHING.id(), getMaxLevel(player, Skill.FLETCHING.id()) * 50, true);
			} else {
				mes("An unlucky accident causes you to waste the feathers.");
				delay(2);
				mes("But you feel that you're close to making this item though.");
			}
		}
	}


	// TODO: this is entirely made up as far as I know. 60% successful cart.
	private boolean getIntoCartSuccessful(Player player) {
		int random = DataConversions.getRandom().nextInt(5);
		if (random == 4 || random == 3) {
			return false;
		} else {
			return true;
		}
	}

	// Note: there is only very limited information on these success rates in replays.
	// The rates implemented here are the ones that existed in OSRS in 2021, which we are using mostly blindly,
	// but with the very little data we do have, it seems possible these could have been the RSC rates as well.
	// See https://oldschool.runescape.wiki/w/Prototype_dart
	public static boolean protoDartSmithSuccessful(int smithingLevel) {
		if (protoDartSmithRates == null) {
			defineSuccessRates();
		}
		double successRate = protoDartSmithRates[smithingLevel];
		double roll = Math.random();
		return successRate > roll;
	}

	// Note: there is only very limited information on these success rates in replays.
	// The rates implemented here are the ones that existed in OSRS in 2021, which we are using mostly blindly,
	// but with the very little data we do have, it seems possible these could have been the RSC rates as well.
	// See https://oldschool.runescape.wiki/w/Prototype_dart_tip
	public static boolean protoDartFletchSuccessful(int fletchingLevel) {
		if (protoDartFletchRates == null) {
			defineSuccessRates();
		}
		double successRate = protoDartFletchRates[fletchingLevel];
		double roll = Math.random();
		return successRate > roll;
	}

	private static void defineSuccessRates() {
		int maxLevelToCalcFor = 138;

		protoDartSmithRates = new double[maxLevelToCalcFor];
		for (int level = 0; level < maxLevelToCalcFor; level++) {
			if (level >= 20) {
				protoDartSmithRates[level] = Formulae.interp(61, 245, level);
			}
		}

		protoDartFletchRates = new double[maxLevelToCalcFor];
		for (int level = 0; level < maxLevelToCalcFor; level++) {
			if (level >= 10) {
				protoDartFletchRates[level] = Formulae.interp(61, 254, level);
			}
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return inArray(obj.getID(), DISTURBED_SAND1, DISTURBED_SAND2, 1006, MINING_CAVE, MINING_CAVE_BACK, MINING_CART,
				MINING_BARREL, TRACK, LIFT_PLATFORM, LIFT_UP, MINING_CART_ABOVE);
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		//closest to irena
		if (obj.getID() == DISTURBED_SAND1) {
			if (command.equals("look")) {
				if (player.getQuestStage(Quests.TOURIST_TRAP) <= 0) {
					mes("You see some footsteps in the sand.");
					delay(3);
				} else {
					mes("This looks like some disturbed sand.");
					delay(3);
					mes("footsteps seem to be heading of towards the south west.");
					delay(3);
				}
			} else if (command.equals("search")) {
				if (player.getQuestStage(Quests.TOURIST_TRAP) <= 0) {
					mes("You just see some footsteps in the sand.");
					delay(3);
				} else {
					mes("You search the footsteps more closely.");
					delay(3);
					mes("You can see that there are five sets of footprints.");
					delay(3);
					mes("One set of footprints seems lighter than the others.");
					delay(3);
					mes("The four other footsteps were made by heavier people with boots.");
					delay(3);
				}
			}
		}
		//closest to camp
		else if (obj.getID() == DISTURBED_SAND2) {
			if (command.equals("look")) {
				if (player.getQuestStage(Quests.TOURIST_TRAP) <= 0) {
					mes("You just see some footsteps in the sand.");
					delay(3);
				} else {
					mes("You find footsteps heading south.");
					delay(3);
					mes("And this time evidence of a struggle...");
					delay(3);
					mes("The footsteps head off due south.");
					delay(3);
				}
			} else if (command.equals("search")) {
				if (player.getQuestStage(Quests.TOURIST_TRAP) <= 0) {
					mes("You just see some footsteps in the sand!");
					delay(3);
				} else {
					mes("You search the area thoroughly...");
					delay(3);
					mes("You notice something colourful in the sand.");
					delay(3);
					mes("You dig around and find a piece of red silk scarf.");
					delay(3);
					mes("It looks as if Ana has been this way!");
					delay(3);
				}
			}
		}
		else if (obj.getID() == 1006) {
			makeDartTip(player, obj);
		}
		else if (obj.getID() == MINING_CAVE_BACK) {
			if (player.getCarriedItems().hasCatalogID(ItemId.ANA_IN_A_BARREL.id(), Optional.of(false))) {
				failCaveAnaInBarrel(player, null);
				return;
			}
			mes("You walk into the dark of the cavern...");
			delay(3);
			player.message("And emerge in a different part of this huge underground complex.");
			player.teleport(84, 3640);
		}
		else if (obj.getID() == MINING_CAVE) {
			Npc n = ifnearvisnpc(player, NpcId.MERCENARY_ESCAPEGATES.id(), 10);
			if ((!player.getCarriedItems().getEquipment().hasEquipped(ItemId.SLAVES_ROBE_BOTTOM.id())
				|| !player.getCarriedItems().getEquipment().hasEquipped(ItemId.SLAVES_ROBE_TOP.id())) && player.getQuestStage(Quests.TOURIST_TRAP) != -1) {
				player.message("This guard looks as if he's been down here a while.");
				npcsay(player, n, "Hey, you're no slave!");
				npcsay(player, n, "What are you doing down here?");
				n.setChasing(player);
				mes("More guards rush to catch you.");
				delay(3);
				mes("You are roughed up a bit by the guards as you're manhandlded to a cell.");
				delay(3);
				npcsay(player, n, "Into the cell you go! I hope this teaches you a lesson.");
				player.teleport(89, 801);
				return;
			}
			if (player.getQuestStage(Quests.TOURIST_TRAP) >= 9 || player.getQuestStage(Quests.TOURIST_TRAP) == -1) {
				mes("You walk into the dark of the cavern...");
				delay(3);
				player.message("And emerge in a different part of this huge underground complex.");
				player.teleport(76, 3640);
				return;
			}
			player.message("Two guards block your way further into the caves");
			if (n != null) {
				npcsay(player, n, "Hey you, move away from there!");
			}
		}
		else if (obj.getID() == MINING_CART) {
			if (command.equals("look")) {
				if (obj.getX() == 62 && obj.getY() == 3639) {
					player.message("This cart is being unloaded into this section of the mine.");
					player.message("Before being sent back for another load.");
				} else {
					player.message("This mine cart is being loaded up with new rocks and stone.");
					player.message("It gets sent to a different section of the mine for unloading.");
				}
			} else if (command.equals("search")) {
				player.message("You search the mine cart.");
				if (player.getCarriedItems().hasCatalogID(ItemId.ANA_IN_A_BARREL.id(), Optional.of(false))) {
					player.message("There isn't enough space for both you and Ana in the cart.");
					return;
				}
				player.message("There may be just enough space to squeeze yourself into the cart.");
				player.message("Would you like to try?");
				int menu = multi(player, "Yes, of course.", "No Thanks, it looks pretty dangerous.");
				if (menu == 0) {
					if (getIntoCartSuccessful(player)) {
						player.message("You succeed!");
						if (obj.getX() == 56 && obj.getY() == 3631) {
							player.teleport(62, 3640);
						} else if (obj.getX() == 62 && obj.getY() == 3639) {
							player.teleport(55, 3632);
						}
					} else {
						player.message("You fail to fit yourself into the cart in time before it starts it's journey.");
						player.message("You fall and hurt yourself.");
						player.damage(2);
					}
				} else if (menu == 1) {
					player.message("You decide not to get into the dangerous looking mine cart.");
				}
			}
		}
		else if (obj.getID() == TRACK) {
			player.message("You see that this track is too dangerous to cross.");
			player.message("High speed carts are crossing the track most of the time.");
		}
		else if (obj.getID() == MINING_BARREL) {
			if (player.getCache().hasKey("ana_is_up")) {
				if (player.getCarriedItems().hasCatalogID(ItemId.MINING_BARREL.id(), Optional.of(false))) {
					player.message("You can only manage one of these at a time.");
					return;
				}
				mes("You find the barrel with ana in it.");
				delay(3);
				mes("@gre@Ana: Let me out of here, I feel sick!");
				delay(3);
				give(player, ItemId.ANA_IN_A_BARREL.id(), 1);
				player.getCache().remove("ana_is_up");
				return;
			}
			if (player.getCache().hasKey("ana_cart")) {
				if (player.getCarriedItems().hasCatalogID(ItemId.MINING_BARREL.id(), Optional.of(false))) {
					player.message("You can only manage one of these at a time.");
					return;
				}
				player.message("You search the barrels and find the one with Ana in it.");
				player.message("@gre@Ana: Let me out!");
				give(player, ItemId.ANA_IN_A_BARREL.id(), 1);
				player.getCache().remove("ana_cart");
				return;
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.ANA_IN_A_BARREL.id(), Optional.of(false))) {
				player.message("You cannot carry another barrel while you're carrying Ana.");
				return;
			}
			if (player.getCache().hasKey("ana_lift")) {
				player.message("You search for Ana, but cannot find her.");
			}
			player.message("This barrel is quite big, but you may be able to carry one. ");
			player.message("Would you like to take one?");
			int menu = multi(player, "Yeah, cool!", "No thanks.");
			if (menu == 0) {
				if (player.getCarriedItems().hasCatalogID(ItemId.MINING_BARREL.id(), Optional.of(false))) {
					player.message("You can only manage one of these at a time.");
				} else {
					player.message("You take the barrel, it's not that heavy, just awkward.");
					give(player, ItemId.MINING_BARREL.id(), 1);
				}
			} else if (menu == 1) {
				player.message("You decide not to take the barrel.");
			}
		}
		else if (obj.getID() == LIFT_PLATFORM) {
			Npc n = ifnearvisnpc(player, NpcId.MERCENARY_LIFTPLATFORM.id(), 5);
			if (n != null) {
				if (player.getCarriedItems().hasCatalogID(ItemId.ANA_IN_A_BARREL.id(), Optional.of(false))) {
					anaToLift(player, n);
					return;
				}
				npcsay(player, n, "Hey there, what do you want?");
				int menu = multi(player, n,
					"What is this thing?",
					"Can I use this?");
				if (menu == 0) {
					repeatLiftDialogue(player, n, RepeatLift.THING);
				} else if (menu == 1) {
					repeatLiftDialogue(player, n, RepeatLift.USETHIS);
				}
			}
		}
		else if (obj.getID() == LIFT_UP) {
			player.message("You pull on the winch");
			if (player.getCache().hasKey("ana_lift")) {
				mes("You see a barrel coming to the surface.");
				delay(3);
				mes("Before too long you haul it onto the side.");
				delay(3);
				mes("The barrel seems quite heavy and you hear a muffled sound coming from inside.");
				delay(3);
				player.message("@gre@Ana: Get me OUT OF HERE!");
				player.getCache().remove("ana_lift");
				if (!player.getCache().hasKey("ana_is_up")) {
					player.getCache().store("ana_is_up", true);
				}
			} else {
				player.message("You pull on the winch and a heavy barrel filled with stone comes to the surface.");
			}
		}
		else if (obj.getID() == MINING_CART_ABOVE) {
			player.message("You search the mine cart.");
			if (player.getCarriedItems().hasCatalogID(ItemId.ANA_IN_A_BARREL.id(), Optional.of(false))) {
				mes("There should be enough space for Ana (in the barrel) to go on here.");
				delay(3);
			}
			if (player.getCache().hasKey("ana_in_cart")) {
				mes("You can see the barrel with Ana in it on the cart already.");
				delay(3);
			}
			mes("There is space on the cart for you get on, would you like to try?");
			delay(3);
			int menu = multi(player,
				"Yes, I'll get on.",
				"No, I've got other plans.",
				"Attract mine cart drivers attention.");
			if (menu == 0) {
				player.message("You decide to climb onto the cart.");
				if (player.getCache().hasKey("ana_in_cart")) {
					mes("You hear Ana starting to bang on the barrel for her to be let out.");
					delay(3);
					mes("@gre@Ana: Get me out of here, I'm suffocating!");
					delay(3);
					mes("@gre@Ana: It smells like dwarven underwear in here!");
					delay(3);
				}
				player.teleport(86, 808);
				if (player.getCache().hasKey("rescue")) {
					mes("As soon as you get on the cart, it starts to move.");
					delay(3);
					mes("Before too long you are past the gates.");
					delay(3);
					mes("You jump off the cart taking Ana with you.");
					delay(3);
					player.teleport(106, 806);
					player.getCache().remove("rescue");
					give(player, ItemId.ANA_IN_A_BARREL.id(), 1);
				}
			} else if (menu == 1) {
				player.message("You decide not to get onto the cart.");
			} else if (menu == 2) {
				Npc cartDriver = ifnearvisnpc(player, NpcId.MINING_CART_DRIVER.id(), 10);
				if (cartDriver != null) {
					npcsay(player, cartDriver, "Ahem.");
					if (player.getCache().hasKey("rescue")) {
						npcsay(player, cartDriver, "Hurry up, get in the cart or I'll go without you!");
						return;
					}
					if (player.getCache().hasKey("ana_in_cart")) {
						getOutWithAnaInCart(player, cartDriver, -1);
						return;
					}
					if (player.getCarriedItems().hasCatalogID(ItemId.ANA_IN_A_BARREL.id(), Optional.of(false))) {
						npcsay(player, cartDriver, "What're you doing carrying that big barrel around?",
							"Put it in the back of the cart like all the others!");
						return;
					}
					player.message("The cart driver is busy loading the cart up ...");
				}
			}
		}
	}

	private void getOutWithAnaInCart(Player player, Npc n, int cID) {
		if (cID == -1) {
			mes("The cart driver seems to be festidiously cleaning his cart.");
			delay(3);
			mes("It doesn't look as if he wants to be disturbed.");
			delay(3);
			int menu = multi(player, n, false, //do not send over
				"Hello.",
				"Nice cart.",
				"Pssst...");
			if (menu == 0) {
				say(player, n, "Hello");
				npcsay(player, n, "Can't you see I'm busy?",
					"Now get out of here!");
				int getGo = multi(player, n,
					"Oh, ok, sorry.",
					"Nice cart.",
					"Pssst...");
				if (getGo == 0) {
					getOutWithAnaInCart(player, n, CartDriver.OKSORRY);
				} else if (getGo == 1) {
					getOutWithAnaInCart(player, n, CartDriver.NICECART);
				} else if (getGo == 2) {
					getOutWithAnaInCart(player, n, CartDriver.PSSST);
				}
			} else if (menu == 1) {
				say(player, n, "Nice cart.");
				getOutWithAnaInCart(player, n, CartDriver.NICECART);
			} else if (menu == 2) {
				say(player, n, "Pssst...");
				getOutWithAnaInCart(player, n, CartDriver.PSSST);
			}
		}
		switch (cID) {
			case CartDriver.PSSST:
				mes("The cart driver completely ignores you.");
				delay(3);
				int pst = multi(player, n,
					"Psssst...",
					"Psssssst...",
					"Pssssssssttt!!!");
				if (pst == 0) {
					getOutWithAnaInCart(player, n, CartDriver.PSSST2);
				} else if (pst == 1) {
					getOutWithAnaInCart(player, n, CartDriver.PSSST3);
				} else if (pst == 2) {
					getOutWithAnaInCart(player, n, CartDriver.PSSSTFINAL);
				}
				break;
			case CartDriver.PSSST2:
				mes("The driver completely ignores you.");
				delay(3);
				int m = multi(player, n,
					"Psssssst...",
					"Pssst...",
					"Pssssssssttt!!!");
				if (m == 0) {
					getOutWithAnaInCart(player, n, CartDriver.PSSST3);
				} else if (m == 1) {
					getOutWithAnaInCart(player, n, CartDriver.PSSST);
				} else if (m == 2) {
					getOutWithAnaInCart(player, n, CartDriver.PSSSTFINAL);
				}
				break;
			case CartDriver.PSSST3:
				mes("The driver completely ignores you.");
				delay(3);
				int me = multi(player, n,
					"Psssst...",
					"Pssst...",
					"Pssssssssttt!!!");
				if (me == 0) {
					getOutWithAnaInCart(player, n, CartDriver.PSSST2);
				} else if (me == 1) {
					getOutWithAnaInCart(player, n, CartDriver.PSSST);
				} else if (me == 2) {
					getOutWithAnaInCart(player, n, CartDriver.PSSSTFINAL);
				}
				break;
			case CartDriver.PSSSTFINAL:
				mes("The cart driver turns around quickly to face you.");
				delay(3);
				npcsay(player, n, "What!",
					"Can't you see I'm busy?");
				int shh = multi(player, n,
					"Oh, ok, sorry.",
					"Shhshhh!");
				if (shh == 0) {
					getOutWithAnaInCart(player, n, CartDriver.OKSORRY);
				} else if (shh == 1) {
					npcsay(player, n, "Shush yourself!");
					player.message("The cart driver goes back to his work.");
				}
				break;
			case CartDriver.OKSORRY:
				npcsay(player, n, "Look just leave me alone!");
				player.message("The cart driver goes back to his work.");
				break;
			case CartDriver.NICECART:
				mes("The cart driver looks around at you and tries to weigh you up.");
				delay(3);
				npcsay(player, n, "Hmmm.");
				mes("He tuts to himself and starts checking the wheels.");
				delay(3);
				npcsay(player, n, "Tut !");
				int tut = multi(player, n,
					"I wonder if you could help me?",
					"One wagon wheel says to the other,'I'll see you around'.",
					"Can I help you at all?");
				if (tut == 0) {
					getOutWithAnaInCart(player, n, CartDriver.WONDERIF);
				} else if (tut == 1) {
					getOutWithAnaInCart(player, n, CartDriver.WAGON);
				} else if (tut == 2) {
					getOutWithAnaInCart(player, n, CartDriver.HELPYOU);
				}
				break;
			case CartDriver.WAGON:
				mes("The cart driver smirks a little.");
				delay(3);
				mes("He starts checking the steering on the cart.");
				delay(3);
				int menu = multi(player, n, false, //do not send over
					"'One good turn deserves another'",
					"Can you get me the heck out of here please?");
				if (menu == 0) {
					say(player, n, "'One good turn deserves another.");
					mes("The cart driver smiles a bit and then turns to you.");
					delay(3);
					npcsay(player, n, "Are you trying to get me fired?");
					int menu2 = multi(player, n,
						"No",
						"Yes",
						"Fired...no, shot perhaps!");
					if (menu2 == 0) {
						npcsay(player, n, "It certainly sounds like it, now leave me alone.",
							"If you bug me again, I'm gonna call the guards.");
						player.message("The cart driver goes back to his work.");
					} else if (menu2 == 1) {
						npcsay(player, n, "And why would you want to do a crazy thing like that for?",
							"I ought to teach you a lesson!");
						driverCallGuards(player, n);
					} else if (menu2 == 2) {
						npcsay(player, n, "Ha ha ha! You're funny!");
						mes("The cart driver checks that the guards aren't watching him.");
						delay(3);
						npcsay(player, n, "What're you in fer?");
						int menu3 = multi(player, n,
							"Oh, I'm not supposed to be here at all actually.",
							"I'm in for murder, so you'd better get me out of here!",
							"In for a penny in for a pound.");
						if (menu3 == 0) {
							npcsay(player, n, "Hmmm, interesting...let me guess.",
								"You're completely innocent...",
								"like all the other inmates in here.",
								"Ha ha ha!");
							player.message("The Cart driver goes back to his work.");
						} else if (menu3 == 1) {
							npcsay(player, n, "Hmm, well, I wonder what the guards are gonna say about that!");
							driverCallGuards(player, n);
						} else if (menu3 == 2) {
							mes("The cart driver laughs at your pun...");
							delay(3);
							npcsay(player, n, "Ha ha ha, oh Stoppit!");
							mes("The cart driver seems much happier now.");
							delay(3);
							npcsay(player, n, "What can I do for you anyway?");
							int menu4 = multi(player, n, false, //do not send over
								"Can you smuggle me out on your cart?",
								"Can you smuggle my friend Ana out on your cart?",
								"Well, you see, it's like this...");
							if (menu4 == 0) {
								say(player, n, "Can you smuggle me out on your cart?");
								mes("The cart driver points at a nearby guard.");
								delay(3);
								npcsay(player, n, "Ask that man over there if it's OK and I'll consider it!",
									"Ha ha ha!");
								player.message("The cart driver goes back to his work, laughing to himself.");
							} else if (menu4 == 1) {
								say(player, n, "Can you smuggle my friend out on your cart?");
								npcsay(player, n, "As long as your friend is a barrel full of rocks.",
									"I don't think it would be a problem at all!",
									"Ha ha ha!");
								player.message("The cart driver goes back to his work, laughing to himself.");
							} else if (menu4 == 2) {
								say(player, n, "Well, you see, it's like this...");
								npcsay(player, n, "yeah!");
								int menu5 = multi(player, n,
									"Prison riot in ten minutes, get your cart out of here!",
									"There's ten gold in it for you if you leave now - no questions asked.");
								if (menu5 == 0) {
									player.message("The cart driver seems visibly shaken...");
									npcsay(player, n, "Oh, right..yes...yess, Ok...");
									mes("The cart driver quickly starts preparing the cart.");
									delay(3);
									int menu6 = multi(player, n,
										"Good luck!",
										"You can't leave me here, I'll get killed!");
									if (menu6 == 0) {
										npcsay(player, n, "Yeah, you too!");
										mes("The cart sets off at a hectic pace.");
										delay(3);
										mes("The guards at the gate get suspiscious and search the cart.");
										delay(3);
										mes("They find Ana in the Barrel and take her back into the mine.");
										delay(3);
										if (player.getCache().hasKey("ana_in_cart")) {
											player.getCache().remove("ana_in_cart");
										}
									} else if (menu6 == 1) {
										npcsay(player, n, "Oh, right...ok, you'd better jump in the cart then!",
											"Quickly!");
										if (player.getCache().hasKey("ana_in_cart")) {
											player.getCache().remove("ana_in_cart");
											player.getCache().store("rescue", true);
										}
									}
								} else if (menu5 == 1) {
									npcsay(player, n, "If you're going to bribe me, at least make it worth my while.",
										"Now, let's say 100 Gold pieces should we?",
										"Ha ha ha!");
									int menu6 = multi(player, n, false, //do not send over
										"A hundred it is!",
										"Forget it!");
									if (menu6 == 0) {
										say(player, n, "A hundred it is.");
										npcsay(player, n, "Great!");
										if (ifheld(player, ItemId.COINS.id(), 100)) {
											npcsay(player, n, "Ok, get in the back of the cart then!");
											player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 100));
											if (player.getCache().hasKey("ana_in_cart")) {
												player.getCache().remove("ana_in_cart");
												player.getCache().store("rescue", true);
											}
										} else {
											npcsay(player, n, "You little cheat, trying to trick me!",
												"I'll show you!");
											driverCallGuards(player, n);
										}
									} else if (menu6 == 1) {
										say(player, n, "Forget it!");
										npcsay(player, n, "Ok, fair enough!",
											"But don't bother me anymore.");
										player.message("The cart driver goes back to work.");
									}
								}
							}
						}
					}
				} else if (menu == 1) {
					say(player, n, "Can you get me the heck out of here please?");
					getOutWithAnaInCart(player, n, CartDriver.HECKOUT);
				}
				break;
			case CartDriver.HELPYOU:
				npcsay(player, n, "I'm quite capable thanks...",
					"Now get lost before I call the guards.");
				int help = multi(player, n,
					"Can you get me the heck out of here please?",
					"I could help, I know a lot about carts.");
				if (help == 0) {
					getOutWithAnaInCart(player, n, CartDriver.HECKOUT);
				} else if (help == 1) {
					npcsay(player, n, "Are you saying I don't know anything about carts?",
						"Why you cheeky little....");
					mes("The cart driver seems mortally offended...");
					delay(3);
					mes("his temper explodes as he shouts the guards.");
					delay(3);
					driverCallGuards(player, n);
				}
				break;
			case CartDriver.WONDERIF:
				npcsay(player, n, "Sorry friend, I'm busy, go bug the guards,",
					"I'm sure they'll give ya the time of day.");
				mes("The cart driver chuckles to himself.");
				delay(3);
				int ok = multi(player, n,
					"Can I help you at all?",
					"Can you get me the heck out of here please?");
				if (ok == 0) {
					getOutWithAnaInCart(player, n, CartDriver.HELPYOU);
				} else if (ok == 1) {
					getOutWithAnaInCart(player, n, CartDriver.HECKOUT);
				}
				break;
			case CartDriver.HECKOUT:
				npcsay(player, n, "No way, and if you bug me again, I'm gonna call the guards.");
				mes("The cart driver goes back to his work.");
				delay(3);
				break;
		}

	}

	private void driverCallGuards(Player player, Npc n) {
		int succeed = DataConversions.random(0, 1);
		npcsay(player, n, "Guards! Guards!");
		if (succeed == 0) {
			mes("Some guards notice you and come over.");
			delay(3);
			Npc mercenary = ifnearvisnpc(player, NpcId.MERCENARY.id(), 15);
			if (mercenary != null) {
				mercenary = addnpc(player.getWorld(), NpcId.MERCENARY.id(), player.getX(), player.getY(), 60000);
				delay(2);
			}
			npcsay(player, mercenary, "Oi, what are you two doing?");
			mercenary.startCombat(player);
			mes("The Guards search you!");
			delay(3);
			mes("More guards rush to catch you.");
			delay(3);
			mes("You are roughed up a bit by the guards as you're manhandlded to a cell.");
			delay(3);
			npcsay(player, mercenary, "Into the cell you go! I hope this teaches you a lesson.");
			player.teleport(89, 801);
		} else {
			mes("You quickly slope away and hide from the guards.");
			delay(3);
		}
	}

	private void repeatLiftDialogue(Player player, Npc n, int cID) {
		switch (cID) {
			case RepeatLift.THING:
				npcsay(player, n, "It is quite clearly a lift.",
					"Any fool can see that it's used to transport rock to the surface.");
				int opt = multi(player, n,
					"Can I use this?",
					"Ok, thanks.");
				if (opt == 0) {
					repeatLiftDialogue(player, n, RepeatLift.USETHIS);
				}
				break;
			case RepeatLift.USETHIS:
				npcsay(player, n, "Of course not, you'd be doing me out of a job.",
					"Anyway, you haven't got any barrels that need to go to the surface.",
					"Now, move along and get some work done before you get a good beating.");
				int options = multi(player, n, false, //do not send over
					"What is this thing?,",
					"Ok, thanks.");
				if (options == 0) {
					say(player, n, "What is this thing?");
					repeatLiftDialogue(player, n, RepeatLift.THING);
				} else if (options == 1) {
					say(player, n, "Ok, thanks.");
				}
				break;
		}
	}

	private void failCaveAnaInBarrel(Player player, Npc n) {
		if (player.getCarriedItems().hasCatalogID(ItemId.ANA_IN_A_BARREL.id(), Optional.of(false))) {
			n = addnpc(player.getWorld(), NpcId.MERCENARY.id(), player.getX(), player.getY(), 60000);
			delay();
			npcsay(player, n, "Hey, where d'ya think you're going with that Barrel?");
			player.message("A guard comes over and takes the barrel off you.");
			player.getCarriedItems().remove(new Item(ItemId.ANA_IN_A_BARREL.id()));
			npcsay(player, n, "'Cor! This barrel is really heavy!",
				"Have you been mining lead?",
				"Har, har har!");
			mes("@gre@Ana: How rude! Why I ought to teach you a lesson.");
			delay(3);
			npcsay(player, n, "What was that!");
			player.message("The guards kick the barrel open.!");
			Npc ana = addnpc(player.getWorld(), NpcId.ANA.id(), player.getX(), player.getY(), 30000);
			delay();
			npcsay(player, ana, "How dare you say that I'm as heavy as lead?");
			player.message("The guards drag Ana of and then throw you into a cell.");
			if (ana != null) {
				ana.remove();
			}
			mes("@yel@Guards: Into the cell you go!");
			delay(3);
			mes("@yel@I hope this teaches you a lesson.");
			delay(3);
			if (n != null) {
				n.remove();
			}
			player.teleport(75, 3626);
		}
	}

	private void anaToLift(Player player, Npc n) {
		player.message("The guard notices the barrel (with Ana in it) that you're carrying.");
		npcsay(player, n, "Hey, that Barrel looks heavy, do you need a hand?");
		int menu = multi(player, n, "Yes please.", "No thanks, I can manage.");
		if (menu == 0) {
			player.message("The guard comes over and helps you. He takes one end of the barrel.");
			npcsay(player, n, "Blimey! This is heavy!");
			mes("@gre@Ana in a barrel: Why you cheeky....!");
			delay(3);
			mes("The guard looks around suprised at Ana's outburst.");
			delay(3);
			npcsay(player, n, "What was that?");
			say(player, n, "Oh, it was nothing.");
			npcsay(player, n, "I could have sworn I heard something!");
			player.message("@gre@Ana in a barrel: Yes you did you ignaramus.");
			npcsay(player, n, "What was that you said?");
			int opt = multi(player, n,
				"I said you were very gregarious!",
				"Oh, nothing.");
			if (opt == 0) {
				mes("@gre@Ana in a barrel: You creep!");
				delay(3);
				npcsay(player, n, "Oh, right, how very nice of you to say so.");
				player.message("The guard seems flattered.");
				npcsay(player, n, "Anyway, let's get this barrel up to the surface, plenty more work to you to do!");
				player.message("The guard places the barrel carefully on the lift platform.");
				npcsay(player, n, "Oh, there's no one operating the lift up top, hope this barrel isn't urgent?",
					"You'd better get back to work!");
				player.getCarriedItems().remove(new Item(ItemId.ANA_IN_A_BARREL.id()));
				if (!player.getCache().hasKey("ana_lift")) {
					player.getCache().store("ana_lift", true);
				}
				// use cache again maybe?
			} else if (opt == 1) {
				npcsay(player, n, "I heard you say something, now spit it out!");
			}
		} else if (menu == 1) {
			npcsay(player, n, "Ok, fair enough, I was only offering.");
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return (obj.getID() == 1006 && item.getCatalogId() == ItemId.BRONZE_BAR.id())
				|| (obj.getID() == MINING_CART && item.getCatalogId() == ItemId.ANA_IN_A_BARREL.id())
				|| (obj.getID() == LIFT_PLATFORM && item.getCatalogId() == ItemId.ANA_IN_A_BARREL.id())
				|| (obj.getID() == MINING_CART_ABOVE && item.getCatalogId() == ItemId.ANA_IN_A_BARREL.id());
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == 1006 && item.getCatalogId() == ItemId.BRONZE_BAR.id()) {
			if (player.getCarriedItems().hasCatalogID(ItemId.PROTOTYPE_DART_TIP.id(), Optional.of(false))) {
				player.message("You have already made the prototype dart tip.");
				player.message("You don't need to make another one.");
			} else if (player.getCarriedItems().hasCatalogID(ItemId.PROTOTYPE_THROWING_DART.id(), Optional.of(false))) {
				player.message("You have already made the prototype dart.");
				player.message("You don't need to make another one.");
			} else {
				makeDartTip(player, obj);
			}
		}
		else if (obj.getID() == MINING_CART && item.getCatalogId() == ItemId.ANA_IN_A_BARREL.id()) {
			mes("You carefully place Ana in the barrel into the mine cart.");
			delay(3);
			mes("Soon the cart moves out of sight and then it returns.");
			delay(3);
			player.getCarriedItems().remove(new Item(ItemId.ANA_IN_A_BARREL.id()));
			if (!player.getCache().hasKey("ana_cart")) {
				player.getCache().store("ana_cart", true);
			}
		}
		else if (obj.getID() == LIFT_PLATFORM && item.getCatalogId() == ItemId.ANA_IN_A_BARREL.id()) {
			Npc n = ifnearvisnpc(player, NpcId.MERCENARY_LIFTPLATFORM.id(), 5);
			if (n != null) {
				anaToLift(player, n);
			}
		}
		else if (obj.getID() == MINING_CART_ABOVE && item.getCatalogId() == ItemId.ANA_IN_A_BARREL.id()) {
			mes("You place Ana (In the barrel) carefully on the cart.");
			delay(3);
			mes("This was the last barrel to go on the cart,");
			delay(3);
			mes("but the cart driver doesn't seem to be in any rush to get going.");
			delay(3);
			mes("And the desert heat will soon get to Ana.");
			delay(3);
			player.getCarriedItems().remove(new Item(ItemId.ANA_IN_A_BARREL.id()));
			if (!player.getCache().hasKey("ana_in_cart")) {
				player.getCache().store("ana_in_cart", true);
			}
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return compareItemsIds(item1, item2, ItemId.FEATHER.id(), ItemId.PROTOTYPE_DART_TIP.id());
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (compareItemsIds(item1, item2, ItemId.FEATHER.id(), ItemId.PROTOTYPE_DART_TIP.id())) {
			attachFeathersToPrototype(player, item1, item2);
		}
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem item) {
		return item.getID() == ItemId.ANA_IN_A_BARREL.id();
	}

	@Override
	public void onTakeObj(Player player, GroundItem item) {
		if (item.getID() == ItemId.ANA_IN_A_BARREL.id()) {
			//non-kosher, unsure if item despawned when killed or gave dialogue on this condition
			player.message("@gre@Ana: Don't think for one minute ...");
			player.message("@gre@Ana: You can just come back and pick me up");
			player.message("Ana goes out running away");
			player.getWorld().unregisterItem(item);
			return;
		}
	}

	@Override
	public boolean blockDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		return item.getCatalogId() == ItemId.ANA_IN_A_BARREL.id();
	}

	@Override
	public void onDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		if (item.getCatalogId() == ItemId.ANA_IN_A_BARREL.id()) {
			if (player.getQuestStage(Quests.TOURIST_TRAP) == -1) {
				player.getCarriedItems().remove(new Item(ItemId.ANA_IN_A_BARREL.id()));
				return;
			}
			player.message("Are you sure you want to drop this?");
			int menu = multi(player,
				"Yes, I'm sure.",
				"Erm, no I've had second thoughts.");
			if (menu == 0) {
				if (outsideCamp(player)) {
					mes("@gre@Ana: You can't drop me here!");
					delay(3);
					mes("@gre@Ana: I'll die in the desert on my own!");
					delay(3);
					mes("@gre@Ana: Take me back to the Shantay pass.");
					delay(3);
					return;
				}
				int diffX = 0;
				//inside mining prison cell
				if ((player.getX() >= 72 && player.getX() <= 77) && (player.getY() >= 3613 && player.getY() <= 3631)) {
					//mercenary does not get placed in jail if player is there
					diffX = -8;
				}
				mes("You drop the barrel to the floor and Ana gets out.");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.ANA_IN_A_BARREL.id()));
				Npc Ana = addnpc(player.getWorld(), NpcId.ANA.id(), player.getX(), player.getY(), 20000);
				delay();
				npcsay(player, Ana, "How dare you put me in that barrel you barbarian!");
				mes("Ana's outburst attracts the guards, they come running over.");
				delay(3);
				Npc guard = ifnearvisnpc(player, NpcId.MERCENARY.id(), 15);
				if (guard == null || guard.inCombat()) {
					guard = addnpc(player.getWorld(), NpcId.MERCENARY.id(), player.getX() + diffX, player.getY(), 30000);
				}
				delay();
				npcsay(player, guard, "Hey! What's going on here then?");
				if (diffX == 0)
					guard.startCombat(player);
				mes("The guards drag Ana away and then throw you into a cell.");
				delay(3);
				player.teleport(75, 3626);
			} else if (menu == 1) {
				mes("You think twice about dropping the barrel to the floor.");
				delay(3);
			}
		}
	}

	private boolean outsideCamp(Player player) {
		return (player.getY() < 795) || (player.getX() >= 92 && (player.getY() >= 795 && player.getY() <= 814))
			|| (player.getX() <= 78 && (player.getY() >= 795 && player.getY() <= 814));
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.MINING_CART_DRIVER.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.MINING_CART_DRIVER.id()) {
			if (n.getID() == NpcId.MINING_CART_DRIVER.id()) {
				if (player.getQuestStage(Quests.TOURIST_TRAP) == -1) {
					npcsay(player, n, "Don't trouble me, can't you see I'm busy?");
					return;
				}
				if (player.getCache().hasKey("rescue")) {
					npcsay(player, n, "Hurry up, get in the cart or I'll go without you!");
					return;
				}
				if (player.getCache().hasKey("ana_in_cart")) {
					getOutWithAnaInCart(player, n, -1);
					return;
				}
				if (player.getCarriedItems().hasCatalogID(ItemId.ANA_IN_A_BARREL.id(), Optional.of(false))) {
					npcsay(player, n, "What're you doing carrying that big barrel around?",
						"Put it in the back of the cart like all the others!");
					return;
				}
				player.message("The cart driver is busy loading the cart up ...");
			}
		}

	}

	class RepeatLift {
		static final int USETHIS = 0;
		static final int THING = 1;
	}

	class CartDriver {
		static final int PSSST = 0;
		static final int PSSST2 = 1;
		static final int PSSST3 = 2;
		static final int PSSSTFINAL = 3;
		static final int OKSORRY = 4;
		static final int NICECART = 5;
		static final int WAGON = 6;
		static final int HELPYOU = 7;
		static final int WONDERIF = 8;
		static final int HECKOUT = 9;
	}
}
