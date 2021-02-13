package com.openrsc.server.plugins.authentic.skills.fishing;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.content.SkillCapes;
import com.openrsc.server.external.ObjectFishDef;
import com.openrsc.server.external.ObjectFishingDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Fishing implements OpLocTrigger {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(Fishing.class);

	public ObjectFishDef getFish(ObjectFishingDef objectFishingDef, int fishingLevel) {
		return objectFishingDef.fishingAttemptResult(fishingLevel);
	}

	@Override
	public void onOpLoc(Player player, final GameObject object, String command) {
		if (command.equals("lure") || command.equals("bait") || command.equals("net") || command.equals("harpoon")
			|| command.equals("cage")) {
			handleFishing(object, player, player.click, command);
		}
	}

	private void handleFishing(final GameObject object, Player player, final int click, final String command) {
		final ObjectFishingDef def = player.getWorld().getServer().getEntityHandler().getObjectFishingDef(object.getID(), click);

		if (!player.withinRange(object, 1)) {
			return;
		}
		if (def == null) { // This shouldn't happen
			return;
		}
		if (checkFatigue(player)) return;

		if (object.getID() == 493 && player.getSkills().getExperience(Skills.FISHING) >= 200) {
			mes("that's enough fishing for now");
			delay(3);
			mes("go through the next door to continue the tutorial");
			delay(3);
			return;
		}
		if (player.getSkills().getLevel(Skills.FISHING) < def.getReqLevel(player.getWorld())) {
			player.playerServerMessage(MessageType.QUEST, "You need at least level " + def.getReqLevel(player.getWorld()) + " "
				+ fishingRequirementString(object, command) + " "
				+ (!command.contains("cage") ? "these fish"
				: player.getWorld().getServer().getEntityHandler().getItemDef(def.getFishDefs()[0].getId()).getName().toLowerCase()
				.substring(4) + "s"));
			return;
		}
		final int netId = def.getNetId();
		if (player.getCarriedItems().getInventory().countId(netId, Optional.of(false)) <= 0) {
			player.playerServerMessage(MessageType.QUEST,
				"You need a "
					+ player.getWorld().getServer().getEntityHandler()
					.getItemDef(netId)
					.getName().toLowerCase()
					+ " to " + (command.equals("lure") || command.equals("bait") ? command : def.getBaitId() > 0 ? "bait" : "catch") + " "
					+ (!command.contains("cage") ? "these fish"
					: player.getWorld().getServer().getEntityHandler().getItemDef(def.getFishDefs()[0].getId()).getName().toLowerCase()
					.substring(4) + "s"));
			return;
		}
		final int baitId = def.getBaitId();
		if (baitId >= 0) {
			if (player.getCarriedItems().getInventory().countId(baitId, Optional.of(false)) <= 0) {
				player.playerServerMessage(MessageType.QUEST, outOfBait(baitId));
				return;
			}
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = Formulae.getRepeatTimes(player, Skills.FISHING);
		}

		startbatch(repeat);
		batchFishing(player, netId, def, object, command);
	}

	public void testBigNetFishing(int level, int trials, Player player) {
		ObjectFishingDef bigNet = player.getWorld().getServer().getEntityHandler().getObjectFishingDef(261, 0);
		if (bigNet == null) {
			LOGGER.error("Somehow bigNet fishing spot isn't defined. Check your cache files.");
			return;
		}

		List<ObjectFishDef> fishLst = new ArrayList<ObjectFishDef>();

		for (int i = 0; i < trials; i++) {
			doBigNetFishingRoll(fishLst, bigNet, level);
		}

		// tally results
		int[] results = new int[1290];
		for (ObjectFishDef fish : fishLst) {
			results[fish.getId()]++;
		}

		mes("@whi@At level @gre@" + level + "@whi@ in @gre@" + trials + "@whi@ attempts:");
		for (int i = 0; i < 1290; i++) {
			if (results[i] > 0) {
				mes( "@whi@We got @gre@" + results[i] + "@whi@ of id @mag@" + i);
			}
		}
	}

	private void batchFishing(Player player, int netId, ObjectFishingDef def, GameObject object, String command) {
		player.playerServerMessage(MessageType.QUEST, "You attempt to catch " + tryToCatchFishString(def));
		player.playSound("fish");
		thinkbubble(new Item(netId));
		delay(4);

		if (player.getSkills().getLevel(Skills.FISHING) < def.getReqLevel(player.getWorld())) {
			player.playerServerMessage(MessageType.QUEST, "You need at least level " + def.getReqLevel(player.getWorld()) + " "
				+ fishingRequirementString(object, command) + " "
				+ (!command.contains("cage") ? "these fish"
				: player.getWorld().getServer().getEntityHandler().getItemDef(def.getFishDefs()[0].getId()).getName().toLowerCase()
				.substring(4) + "s"));
			return;
		}
		final int baitId = def.getBaitId();
		if (baitId >= 0) {
			if (player.getCarriedItems().getInventory().countId(baitId, Optional.of(false)) <= 0) {
				player.playerServerMessage(MessageType.QUEST, outOfBait(baitId));
				return;
			}
		}

		if (checkFatigue(player)) return;

		List<ObjectFishDef> fishLst = new ArrayList<ObjectFishDef>();
		ObjectFishDef aFishDef;
		GameObject obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());

		if (object.getID() == 493) { // Tutorial Island Shrimp
			aFishDef = getFish(def, player.getSkills().getLevel(Skills.FISHING));
			if (aFishDef != null) fishLst.add(aFishDef);

			if (fishLst.size() > 0) {
				player.playerServerMessage(MessageType.QUEST, "You catch some shrimps");
				player.getCarriedItems().getInventory().add(new Item(fishLst.get(0).getId()));
				player.incExp(Skills.FISHING, fishLst.get(0).getExp(), true);
				if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 41) {
					player.getCache().set("tutorial", 42);
				}
			} else {
				// it is authentic that this message only appears until you have caught your first shrimp.
				if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 41) {
					player.message("keep trying, you'll catch something soon");
				} else {
					player.playerServerMessage(MessageType.QUEST, "You fail to catch anything");
				}
			}
		} else if (netId == ItemId.BIG_NET.id()) {

			ObjectFishingDef bigNet = player.getWorld().getServer().getEntityHandler().getObjectFishingDef(261, 0);
			if (bigNet == null) {
				LOGGER.error("Somehow bigNet fishing spot isn't defined. Check your cache files.");
				return;
			}

			// add the fish gained to fishLst, report how many rolls were able to be done
			int fishRolls = doBigNetFishingRoll(fishLst, bigNet, player.getSkills().getLevel(Skills.FISHING));

			//check if the spot is still active
			if (obj == null) {
				player.playerServerMessage(MessageType.QUEST, "You fail to catch anything");
				return;
			}
			// award the fish
			for (ObjectFishDef fishDef : fishLst) {
				Item fish = new Item(fishDef.getId());
				switch (ItemId.getById(fishDef.getId())) {
					// NOTICE: Don't obfuscate this by making it a one liner.
					// Needed to be on separate lines for Language Translations.
					case RAW_BASS:
						player.playerServerMessage(MessageType.QUEST, "You catch a bass");
						break;
					case RAW_COD:
						player.playerServerMessage(MessageType.QUEST, "You catch a cod");
						break;
					case RAW_MACKEREL:
						player.playerServerMessage(MessageType.QUEST, "You catch a mackerel");
						break;
					case OYSTER:
						player.playerServerMessage(MessageType.QUEST, "You catch an oyster shell");
						break;
					case CASKET:
						player.playerServerMessage(MessageType.QUEST, "You catch a casket");
						break;
					case BOOTS:
						player.playerServerMessage(MessageType.QUEST, "You catch some boots");
						break;
					case LEATHER_GLOVES:
						player.playerServerMessage(MessageType.QUEST, "You catch some gloves");
						break;
					case SEAWEED:
						player.playerServerMessage(MessageType.QUEST, "You catch some seaweed");
						break;
					default:
						player.playerServerMessage(MessageType.QUEST, "You catch something really surprising: a bug! Please report this bug!");
						break;
				}
				player.getCarriedItems().getInventory().add(fish);
				player.incExp(Skills.FISHING, fishDef.getExp(), true);
			}
			if (fishLst.size() == 0 && fishRolls == 9) {
				// An erroneous (mostly authentic) additional check on fishRolls here,
				// so that this message doesn't appear unless all rolls were possible at the player's fishing level.
				//
				// It was likely a NPE or array index out of bounds in the original source, like checking the result
				// of the fish in some array while awarding the fish, or just referencing some null value...
				// Because there's only 7 / 9  of the fish expected, the fishing script crashes & doesn't make it to this message.
				//
				// It would be rather ugly to emulate that at this time, so instead I'm checking fishRolls
				// Which is functionally the same except our script doesn't crash.
				player.playerServerMessage(MessageType.QUEST, "You fail to catch anything");
			}

			// check if fishing spot should inauthentically deplete
			if (fishLst.size() > 0) {
				if (config().FISHING_SPOTS_DEPLETABLE && DataConversions.random(1, 250) <= def.getDepletion()) {
					obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
					if (obj != null && obj.getID() == object.getID() && def.getRespawnTime() > 0) {
						GameObject newObject = new GameObject(player.getWorld(), object.getLocation(), 668, object.getDirection(), object.getType());
						player.getWorld().replaceGameObject(object, newObject);
						player.getWorld().delayedSpawnObject(obj.getLoc(), def.getRespawnTime() * config().GAME_TICK, true);
					}
				}
			}
		} else { // NOT big net fishing & NOT tutorial island shrimp; normal fishing
			// Roll for fish to be given to user
			aFishDef = getFish(def, player.getSkills().getLevel(Skills.FISHING));
			if (aFishDef != null) fishLst.add(aFishDef);

			//check if the spot is still active
			if (obj == null) {
				player.playerServerMessage(MessageType.QUEST, "You fail to catch anything");
				return;
			}

			// check & remove bait
			if (baitId >= 0) {
				int idx = player.getCarriedItems().getInventory().getLastIndexById(baitId, Optional.of(false));
				Item bait = player.getCarriedItems().getInventory().get(idx);
				if (bait == null) {
					// should not be reachable unless threading bug; this was already checked
					if (player.getCarriedItems().getInventory().countId(baitId, Optional.of(false)) <= 0) {
						player.playerServerMessage(MessageType.QUEST, outOfBait(baitId));
						return;
					}
				}
				player.getCarriedItems().remove(new Item(bait.getCatalogId(), 1, false, bait.getItemId()));
			}
			if (fishLst.size() == 0) {
				player.playerServerMessage(MessageType.QUEST, "You fail to catch anything");
				if (!ifbatchcompleted()) {
					GameObject checkObj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
					if (checkObj == null) {
						return;
					}
				}
			} else {
				// Award the fish
				Item fish = new Item(fishLst.get(0).getId());

				// Skill cape perk. Will convert a shark to either a manta ray or a turtle.
				String capeColor = "";
				if (fish.getCatalogId() == ItemId.RAW_SHARK.id()) {
					Item newFish = new Item(SkillCapes.shouldActivateInt(player, ItemId.FISHING_CAPE));
					if (newFish.getCatalogId() != -1) {
						fish = newFish;
						capeColor = "@dcy@";
						player.playerServerMessage(MessageType.QUEST, capeColor + "Because of your prowess in fishing");
					}
				}

				switch (ItemId.getById(fish.getCatalogId())) {
					// NOTICE: Don't obfuscate this by making it a one liner.
					// Needed to be on separate lines for Language Translations.
					case RAW_SHARK:
						player.playerServerMessage(MessageType.QUEST, "You catch a shark!");
						break;
					case RAW_SHRIMP:
						player.playerServerMessage(MessageType.QUEST, "You catch some shrimps");
						break;
					case RAW_ANCHOVIES:
						player.playerServerMessage(MessageType.QUEST, "You catch some anchovies");
						break;
					case RAW_MANTA_RAY:
						// inauthentic, from Fishing Skill Cape perk. These are usually just from trawler.
						player.playerServerMessage(MessageType.QUEST, capeColor + "You catch a manta ray!");
						break;
					case RAW_SEA_TURTLE:
						// inauthentic, from Fishing Skill Cape perk. These are usually just from trawler.
						player.playerServerMessage(MessageType.QUEST, capeColor + "You catch a sea turtle!");
						break;
					default:
						// TODO: may need to separate all these out for Language Translations
						String fishName = fish.getDef(player.getWorld()).getName().toLowerCase().replace("raw ", "");
						player.playerServerMessage(MessageType.QUEST, "You catch a " + fishName);
						break;
				}

				player.getCarriedItems().getInventory().add(fish);
				player.incExp(Skills.FISHING, fishLst.get(0).getExp(), true);

				// inauthentically check if the fishing spot should deplete
				if (config().FISHING_SPOTS_DEPLETABLE && DataConversions.random(1, 250) <= def.getDepletion()) {
					obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
					if (obj != null && obj.getID() == object.getID() && def.getRespawnTime() > 0) {
						GameObject newObject = new GameObject(player.getWorld(), object.getLocation(), 668, object.getDirection(), object.getType());
						player.getWorld().replaceGameObject(object, newObject);
						player.getWorld().delayedSpawnObject(obj.getLoc(), def.getRespawnTime() * config().GAME_TICK, true);
					}
				}
			}
		}

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !ifbatchcompleted()) {
			delay();
			batchFishing(player, netId, def, object, command);
		}
	}

	private int doBigNetFishingRoll(List<ObjectFishDef> fishLst, ObjectFishingDef bigNet, int playerLevel) {
		// Roll for fish. Each of the 8 fish get 1 roll each except mackerel, those get 2 rolls.
		// Based on jmod tweet & consistent with the data we have on this. The double mackerel roll can be seen in replays.

		int fishRolls = 0;
		for (ObjectFishDef fish : bigNet.getFishDefs()) {
			if (playerLevel >= fish.getReqLevel()) {
				int rolls = (fish.getId() == ItemId.RAW_MACKEREL.id() ? 2 : 1); // mackerel get 2 rolls, all others get 1 roll
				for (int roll = 0; roll < rolls; roll++) {
					fishRolls++;
					if (fish.rate[playerLevel] > Math.random()) {
						fishLst.add(fish);
					}
				}
			}
		}
		return fishRolls; // mmmm, delicious fish rolls...
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		//special hemenster fishing spots
		if (obj.getID() == 351 || obj.getID() == 352 || obj.getID() == 353 || obj.getID() == 354)
			return false;
		if (command.equals("lure") || command.equals("bait") || command.equals("net") || command.equals("harpoon")
			|| command.equals("cage")) {
			return true;
		}
		return false;
	}

	private String outOfBait(int baitId) {
		if (baitId == ItemId.FISHING_BAIT.id()) {
			return "You don't have any fishing bait left"; // /1e_Luis/Quests/Heroes Quest/Heroes Quest Pt1
		}
		if (baitId == ItemId.FEATHER.id()) {
			return "You don't have any feathers left to lure the fish"; // /flying sno/flying sno (redacted chat) replays/fsnom2@aol.com/07-30-2018 09.06.45
		}
		return "You are out of an unknown bait. Please report this.";
	}

	private String fishingRequirementString(GameObject obj, String command) {
		String name = "";
		if (command.equals("bait")) {
			name = "fishing to bait";
		} else if (command.equals("lure")) {
			name = "fishing to lure";
		} else if (command.equals("net")) {
			name = "fishing to net";
		} else if (command.equals("harpoon")) {
			name = "fishing to harpoon";
		} else if (command.equals("cage")) {
			name = "fishing to catch";
		}
		return name;
	}

	private String tryToCatchFishString(ObjectFishingDef def) {
		String name = "";
		if (def.getNetId() == ItemId.NET.id()) {
			name = "some fish";
		} else if (def.getNetId() == ItemId.LOBSTER_POT.id()) {
			name = "a lobster";
		} else {
			name = "a fish";
		}
		return name;
	}

	private boolean checkFatigue(Player player) {
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 1
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.playerServerMessage(MessageType.QUEST,"You are too tired to catch this fish");
				return true;
			}
		}
		return false;
	}
}
