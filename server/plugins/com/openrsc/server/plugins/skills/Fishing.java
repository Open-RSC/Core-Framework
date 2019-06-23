package com.openrsc.server.plugins.skills;

import com.openrsc.server.Constants;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.ObjectFishDef;
import com.openrsc.server.external.ObjectFishingDef;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.showBubble;

public class Fishing implements ObjectActionListener, ObjectActionExecutiveListener {

	private static ObjectFishDef getFish(ObjectFishingDef objectFishDef, int fishingLevel, int click) {
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
	public void onObjectAction(final GameObject object, String command, Player owner) {
		if (command.equals("lure") || command.equals("bait") || command.equals("net") || command.equals("harpoon")
			|| command.equals("cage")) {
			handleFishing(object, owner, owner.click, command);
		}
	}

	private void handleFishing(final GameObject object, Player owner, final int click, final String command) {

		final ObjectFishingDef def = EntityHandler.getObjectFishingDef(object.getID(), click);

		if (owner.isBusy()) {
			return;
		}
		if (!owner.withinRange(object, 1)) {
			return;
		}
		if (def == null) { // This shouldn't happen
			return;
		}
		if (Constants.GameServer.WANT_FATIGUE) {
			if (owner.getFatigue() >= owner.MAX_FATIGUE) {
				owner.message("You are too tired to catch this fish");
				return;
			}
		}
		if (object.getID() == 493 && owner.getSkills().getExperience(Skills.FISHING) >= 200) {
			message(owner, "that's enough fishing for now",
					"go through the next door to continue the tutorial");
			return;
		}
		if (owner.getSkills().getLevel(Skills.FISHING) < def.getReqLevel()) {
			owner.playerServerMessage(MessageType.QUEST, "You need at least level " + def.getReqLevel() + " "
				+ fishingRequirementString(object, command) + " "
				+ (!command.contains("cage") ? "these fish"
				: EntityHandler.getItemDef(def.getFishDefs()[0].getId()).getName().toLowerCase()
				.substring(4) + "s"));
			return;
		}
		final int netId = def.getNetId();
		if (owner.getInventory().countId(netId) <= 0) {
			owner.playerServerMessage(MessageType.QUEST,
				"You need a "
					+ EntityHandler
					.getItemDef(
						netId)
					.getName().toLowerCase()
					+ " to " + (command.equals("lure") || command.equals("bait") ? command : def.getBaitId() > 0 ? "bait" : "catch") + " "
					+ (!command.contains("cage") ? "these fish"
					: EntityHandler.getItemDef(def.getFishDefs()[0].getId()).getName().toLowerCase()
					.substring(4) + "s"));
			return;
		}
		final int baitId = def.getBaitId();
		if (baitId >= 0) {
			if (owner.getInventory().countId(baitId) <= 0) {
				owner.playerServerMessage(MessageType.QUEST,
					"You don't have any " + EntityHandler.getItemDef(baitId).getName().toLowerCase() + " left");
				return;
			}
		}
		owner.playSound("fish");
		owner.playerServerMessage(MessageType.QUEST, "You attempt to catch " + tryToCatchFishString(def));
		showBubble(owner, new Item(netId));
		owner.setBatchEvent(new BatchEvent(owner, 1800, 1000, true) {
			@Override
			public void action() {
				final int baitId = def.getBaitId();
				if (baitId >= 0) {
					if (owner.getInventory().countId(baitId) <= 0) {
						owner.playerServerMessage(MessageType.QUEST, "You don't have any " + EntityHandler.getItemDef(baitId).getName().toLowerCase()
							+ " left");
						interrupt();
						return;
					}
				}
				if (Constants.GameServer.WANT_FATIGUE) {
					if (owner.getFatigue() >= owner.MAX_FATIGUE) {
						owner.playerServerMessage(MessageType.QUEST, "You are too tired to catch this fish");
						interrupt();
						return;
					}
				}
				List<ObjectFishDef> fishLst = new ArrayList<ObjectFishDef>();
				ObjectFishDef aFishDef = getFish(def, owner.getSkills().getLevel(Skills.FISHING), click);
				if (aFishDef != null) fishLst.add(aFishDef);
				if (fishLst.size() > 0) {
					//check if the spot is still active
					GameObject obj = owner.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
					if (obj == null) {
						owner.playerServerMessage(MessageType.QUEST, "You fail to catch anything");
						interrupt();
					} else {
						if (baitId >= 0) {
							int idx = owner.getInventory().getLastIndexById(baitId);
							Item bait = owner.getInventory().get(idx);
							int newCount = bait.getAmount() - 1;
							if (newCount <= 0) {
								owner.getInventory().remove(idx);
							} else {
								bait.setAmount(newCount);
							}
							ActionSender.sendInventory(owner);
						}
						if (netId == ItemId.BIG_NET.id()) {
							//big net spot may get 4 items but 1 already gotten
							int max = bigNetRand() - 1;
							for (int i = 0; i < max; i++) {
								aFishDef = getFish(def, owner.getSkills().getLevel(Skills.FISHING), click);
								if (aFishDef != null) fishLst.add(aFishDef);
							}
							if (DataConversions.random(0, 200) == 100) {
								owner.playerServerMessage(MessageType.QUEST, "You catch a casket");
								owner.incExp(Skills.FISHING, 40, true);
								addItem(owner, ItemId.CASKET.id(), 1);
							}
							for (Iterator<ObjectFishDef> iter = fishLst.iterator(); iter.hasNext();) {
								ObjectFishDef fishDef = iter.next();
								Item fish = new Item(fishDef.getId());
								owner.getInventory().add(fish);
								owner.playerServerMessage(MessageType.QUEST, "You catch " + (fish.getID() == ItemId.BOOTS.id() || fish.getID() == ItemId.SEAWEED.id() || fish.getID() == ItemId.LEATHER_GLOVES.id() ? "some" : fish.getID() == ItemId.OYSTER.id() ? "an" : "a") + " "
									+ fish.getDef().getName().toLowerCase().replace("raw ", "").replace("leather ", "") + (fish.getID() == ItemId.OYSTER.id() ? " shell" : ""));
								owner.incExp(Skills.FISHING, fishDef.getExp(), true);
							}
						} else {
							Item fish = new Item(fishLst.get(0).getId());
							owner.getInventory().add(fish);
							owner.playerServerMessage(MessageType.QUEST, "You catch " + (netId == ItemId.NET.id() ? "some" : "a") + " "
								+ fish.getDef().getName().toLowerCase().replace("raw ", "") + (fish.getID() == ItemId.RAW_SHRIMP.id() ? "s" : "")
								+ (fish.getID() == ItemId.RAW_SHARK.id() ? "!" : ""));
							owner.incExp(Skills.FISHING, fishLst.get(0).getExp(), true);
							if (object.getID() == 493 && owner.getCache().hasKey("tutorial") && owner.getCache().getInt("tutorial") == 41)
								owner.getCache().set("tutorial", 42);
						}
					}
					if (Constants.GameServer.FISHING_SPOTS_DEPLETABLE && DataConversions.random(1, 100) <= def.getDepletion()) {
						obj = owner.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
						interrupt();
						if (obj != null && obj.getID() == object.getID() && def.getRespawnTime() > 0) {
							GameObject newObject = new GameObject(object.getLocation(), 668, object.getDirection(), object.getType());
							World.getWorld().replaceGameObject(object, newObject);
							World.getWorld().delayedSpawnObject(obj.getLoc(), def.getRespawnTime() * 1000);
						}
					}					
				} else {
					owner.playerServerMessage(MessageType.QUEST, "You fail to catch anything");
					if (object.getID() == 493 && owner.getCache().hasKey("tutorial") && owner.getCache().getInt("tutorial") == 41) {
						owner.message("keep trying, you'll catch something soon");
					}
					if (object.getID() != 493 && getRepeatFor() > 1) {
						GameObject checkObj = owner.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
						if (checkObj == null) {
							interrupt();
						}
					}
				}
				if (!isCompleted()) {
					showBubble(owner, new Item(netId));
					owner.playerServerMessage(MessageType.QUEST, "You attempt to catch " + tryToCatchFishString(def));
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
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		if (obj.getID() == 352)
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
