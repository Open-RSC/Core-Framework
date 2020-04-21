package com.openrsc.server.plugins.skills.fishing;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.ObjectFishDef;
import com.openrsc.server.external.ObjectFishingDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.openrsc.server.plugins.Functions.*;

public class Fishing implements OpLocTrigger {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(Fishing.class);

	private ObjectFishDef getFish(ObjectFishingDef objectFishDef, int fishingLevel, int click) {
		ArrayList<ObjectFishDef> fish = new ArrayList<ObjectFishDef>();

		for (ObjectFishDef def : objectFishDef.getFishDefs()) {
			if (fishingLevel >= def.getReqLevel() && Formulae.calcGatheringSuccessful(def.getReqLevel(), fishingLevel)) {
				fish.add(def);
			}
		}
		if (fish.size() <= 0) {
			return null;
		}
		return fish.get(DataConversions.random(0, fish.size() - 1));
	}

	@Override
	public void onOpLoc(final GameObject object, String command, Player player) {
		if (command.equals("lure") || command.equals("bait") || command.equals("net") || command.equals("harpoon")
			|| command.equals("cage")) {
			handleFishing(object, player, player.click, command);
		}
	}

	private void handleFishing(final GameObject object, Player player, final int click, final String command) {

		final ObjectFishingDef def = player.getWorld().getServer().getEntityHandler().getObjectFishingDef(object.getID(), click);

		if (player.isBusy()) {
			return;
		}
		if (!player.withinRange(object, 1)) {
			return;
		}
		if (def == null) { // This shouldn't happen
			return;
		}
		if (player.getWorld().getServer().getConfig().WANT_FATIGUE) {
			if (player.getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 1
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.playerServerMessage(MessageType.QUEST,"You are too tired to catch this fish");
				return;
			}
		}
		if (object.getID() == 493 && player.getSkills().getExperience(Skills.FISHING) >= 200) {
			mes(player, "that's enough fishing for now",
					"go through the next door to continue the tutorial");
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
		if (player.getCarriedItems().getInventory().countId(netId) <= 0) {
			player.playerServerMessage(MessageType.QUEST,
				"You need a "
					+ player.getWorld().getServer().getEntityHandler()
					.getItemDef(
						netId)
					.getName().toLowerCase()
					+ " to " + (command.equals("lure") || command.equals("bait") ? command : def.getBaitId() > 0 ? "bait" : "catch") + " "
					+ (!command.contains("cage") ? "these fish"
					: player.getWorld().getServer().getEntityHandler().getItemDef(def.getFishDefs()[0].getId()).getName().toLowerCase()
					.substring(4) + "s"));
			return;
		}
		final int baitId = def.getBaitId();
		if (baitId >= 0) {
			if (player.getCarriedItems().getInventory().countId(baitId) <= 0) {
				player.playerServerMessage(MessageType.QUEST,
					"You don't have any " + player.getWorld().getServer().getEntityHandler().getItemDef(baitId).getName().toLowerCase() + " left");
				return;
			}
		}
		player.playSound("fish");
		player.playerServerMessage(MessageType.QUEST, "You attempt to catch " + tryToCatchFishString(def));
		thinkbubble(player, new Item(netId));
		player.setBatchEvent(new BatchEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK * 3, "Fishing", Formulae.getRepeatTimes(player, Skills.FISHING), true) {
			@Override
			public void action() {
				try {
					if (getOwner().getSkills().getLevel(Skills.FISHING) < def.getReqLevel(getWorld())) {
						getOwner().playerServerMessage(MessageType.QUEST, "You need at least level " + def.getReqLevel(getWorld()) + " "
							+ fishingRequirementString(object, command) + " "
							+ (!command.contains("cage") ? "these fish"
							: getWorld().getServer().getEntityHandler().getItemDef(def.getFishDefs()[0].getId()).getName().toLowerCase()
							.substring(4) + "s"));
						interrupt();
						return;
					}
					final int baitId = def.getBaitId();
					if (baitId >= 0) {
						if (getOwner().getCarriedItems().getInventory().countId(baitId) <= 0) {
							getOwner().playerServerMessage(MessageType.QUEST, "You don't have any " + getWorld().getServer().getEntityHandler().getItemDef(baitId).getName().toLowerCase()
								+ " left");
							interrupt();
							return;
						}
					}
					if (getWorld().getServer().getConfig().WANT_FATIGUE) {
						if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 1
							&& getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
							getOwner().playerServerMessage(MessageType.QUEST, "You are too tired to catch this fish");
							interrupt();
							return;
						}
					}
					List<ObjectFishDef> fishLst = new ArrayList<ObjectFishDef>();
					ObjectFishDef aFishDef = getFish(def, getOwner().getSkills().getLevel(Skills.FISHING), click);
					if (aFishDef != null) fishLst.add(aFishDef);
					if (fishLst.size() > 0) {
						//check if the spot is still active
						GameObject obj = getOwner().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
						if (obj == null) {
							getOwner().playerServerMessage(MessageType.QUEST, "You fail to catch anything");
							interrupt();
						} else {
							if (baitId >= 0) {
								int idx = getOwner().getCarriedItems().getInventory().getLastIndexById(baitId);
								Item bait = getOwner().getCarriedItems().getInventory().get(idx);
								int newCount = bait.getAmount() - 1;
								if (newCount <= 0) {
									getOwner().getCarriedItems().remove(new Item(idx));
								} else {
									bait.changeAmount(getOwner().getWorld().getServer().getDatabase(),-1);
								}
								ActionSender.sendInventory(getOwner());
							}
							if (netId == ItemId.BIG_NET.id()) {
								//big net spot may get 4 items but 1 already gotten
								int max = bigNetRand() - 1;
								for (int i = 0; i < max; i++) {
									aFishDef = getFish(def, getOwner().getSkills().getLevel(Skills.FISHING), click);
									if (aFishDef != null) fishLst.add(aFishDef);
								}
								if (DataConversions.random(0, 200) == 100) {
									getOwner().playerServerMessage(MessageType.QUEST, "You catch a casket");
									getOwner().incExp(Skills.FISHING, 40, true);
									give(getOwner(), ItemId.CASKET.id(), 1);
								}
								for (Iterator<ObjectFishDef> iter = fishLst.iterator(); iter.hasNext();) {
									ObjectFishDef fishDef = iter.next();
									Item fish = new Item(fishDef.getId());
									getOwner().getCarriedItems().getInventory().add(fish);
									getOwner().playerServerMessage(MessageType.QUEST, "You catch " + (fish.getCatalogId() == ItemId.BOOTS.id() || fish.getCatalogId() == ItemId.SEAWEED.id() || fish.getCatalogId() == ItemId.LEATHER_GLOVES.id() ? "some" : fish.getCatalogId() == ItemId.OYSTER.id() ? "an" : "a") + " "
										+ fish.getDef(getWorld()).getName().toLowerCase().replace("raw ", "").replace("leather ", "") + (fish.getCatalogId() == ItemId.OYSTER.id() ? " shell" : ""));
									getOwner().incExp(Skills.FISHING, fishDef.getExp(), true);
								}
							} else {
								Item fish = new Item(fishLst.get(0).getId());
								getOwner().getCarriedItems().getInventory().add(fish);
								getOwner().playerServerMessage(MessageType.QUEST, "You catch " + (netId == ItemId.NET.id() ? "some" : "a") + " "
									+ fish.getDef(getWorld()).getName().toLowerCase().replace("raw ", "") + (fish.getCatalogId() == ItemId.RAW_SHRIMP.id() ? "s" : "")
									+ (fish.getCatalogId() == ItemId.RAW_SHARK.id() ? "!" : ""));
								getOwner().incExp(Skills.FISHING, fishLst.get(0).getExp(), true);
								if (object.getID() == 493 && getOwner().getCache().hasKey("tutorial") && getOwner().getCache().getInt("tutorial") == 41)
									getOwner().getCache().set("tutorial", 42);
							}
						}
						if (getWorld().getServer().getConfig().FISHING_SPOTS_DEPLETABLE && DataConversions.random(1, 1000) <= def.getDepletion()) {
							obj = getOwner().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
							interrupt();
							if (obj != null && obj.getID() == object.getID() && def.getRespawnTime() > 0) {
								GameObject newObject = new GameObject(getWorld(), object.getLocation(), 668, object.getDirection(), object.getType());
								getWorld().replaceGameObject(object, newObject);
								getWorld().delayedSpawnObject(obj.getLoc(), def.getRespawnTime() * 1000, true);
							}
						}
					} else {
						getOwner().playerServerMessage(MessageType.QUEST, "You fail to catch anything");
						if (object.getID() == 493 && getOwner().getCache().hasKey("tutorial") && getOwner().getCache().getInt("tutorial") == 41) {
							getOwner().message("keep trying, you'll catch something soon");
						}
						if (object.getID() != 493 && getRepeatFor() > 1) {
							GameObject checkObj = getOwner().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
							if (checkObj == null) {
								interrupt();
							}
						}
					}
					if (!isCompleted()) {
						thinkbubble(getOwner(), new Item(netId));
						getOwner().playerServerMessage(MessageType.QUEST, "You attempt to catch " + tryToCatchFishString(def));
					}
				} catch (GameDatabaseException ex) {
					LOGGER.error(ex.getMessage());
				}
			}
		});
	}

	private int bigNetRand() {
		int roll = DataConversions.random(0, 30);
		if (roll <= 23) {
			return 1;
		} else if (roll <= 27) {
			return 2;
		} else if (roll <= 29) {
			return 3;
		} else {
			return 4;
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player player) {
		//special hemenster fishing spots
		if (obj.getID() == 351 || obj.getID() == 352 || obj.getID() == 353 || obj.getID() == 354)
			return false;
		if (command.equals("lure") || command.equals("bait") || command.equals("net") || command.equals("harpoon")
			|| command.equals("cage")) {
			return true;
		}
		return false;
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
}
