package com.openrsc.server.plugins.skills;

import static com.openrsc.server.plugins.Functions.showBubble;
import static com.openrsc.server.plugins.Functions.addItem;

import java.util.ArrayList;

import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ObjectFishDef;
import com.openrsc.server.external.ObjectFishingDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

public class Fishing implements ObjectActionListener, ObjectActionExecutiveListener {

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
		if (owner.getFatigue() >= owner.MAX_FATIGUE) {
			owner.playerServerMessage(MessageType.QUEST, "@gre@You are too tired to catch this fish");
			return;
		}
		if (owner.getSkills().getLevel(10) < def.getReqLevel()) {
			owner.playerServerMessage(MessageType.QUEST, "You need at least level " + def.getReqLevel() + " "
					+ fishingRequirementString(object, command) + " "
					+ (def.getFishDefs().length > 1 ? "these fish"
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
							+ " to " + (def.getBaitId() > 0 ? "bait" : "catch") + " "
							+ (def.getFishDefs().length > 1 ? "these fish"
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
		owner.playerServerMessage(MessageType.QUEST, "You attempt to catch " + tryToCatchFishString(def));
		showBubble(owner, new Item(netId));
		owner.setBatchEvent(new BatchEvent(owner, 1800, Formulae.getRepeatTimes(owner, 10)) {
			@Override
			public void action() {
				showBubble(owner, new Item(netId));
				final int baitId = def.getBaitId();
				if (baitId >= 0) {
					if (owner.getInventory().countId(baitId) <= 0) {
						owner.playerServerMessage(MessageType.QUEST, "You don't have any " + EntityHandler.getItemDef(baitId).getName().toLowerCase()
								+ " left");
						return;
					}
				}
				if (owner.getFatigue() >= owner.MAX_FATIGUE) {
					owner.playerServerMessage(MessageType.QUEST, "You are too tired to catch this fish");
					interrupt();
					return;
				}
				owner.playSound("fish");
				ObjectFishDef fishDef = getFish(def, owner.getSkills().getLevel(10), click);
				if (fishDef != null) {
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
					if(netId == 548) {
						if(DataConversions.random(0, 200) == 100) {
							owner.playerServerMessage(MessageType.QUEST, "You catch a casket");
							owner.incExp(10, fishDef.getExp(), true);
							addItem(owner, 549, 1);
						}
						Item fish = new Item(fishDef.getId());
						owner.getInventory().add(fish);
						owner.playerServerMessage(MessageType.QUEST, "You catch " + (fish.getID() == 17 || fish.getID() == 622 || fish.getID() == 16 ? "some" : fish.getID() == 793 ? "an" : "a") + " "
								+ fish.getDef().getName().toLowerCase().replace("raw ", "").replace("leather ", "") + (fish.getID() == 793 ? " shell" : ""));
						owner.incExp(10, fishDef.getExp(), true);
					} else {
						Item fish = new Item(fishDef.getId());
						owner.getInventory().add(fish);
						owner.playerServerMessage(MessageType.QUEST, "You catch " + (netId == 376 ? "some" : "a") + " "
								+ fish.getDef().getName().toLowerCase().replace("raw ", "") + (fish.getID() == 349 ? "s" : "")
								+ (fish.getID() == 545 ? "!" : ""));
						owner.incExp(10, fishDef.getExp(), true);
					}
				} else {
					owner.playerServerMessage(MessageType.QUEST, "You fail to catch anything");
					if (!owner.getInventory().hasItemId(349) && owner.getLocation().onTutorialIsland()
							&& owner.getCache().hasKey("tutorial") && owner.getCache().getInt("tutorial") == 40) {
						owner.message("keep trying, you'll catch something soon");
					}
				}
			}
		});
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
			name = "to bait";
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

	public static ObjectFishDef getFish(ObjectFishingDef objectFishDef, int fishingLevel, int click) {
		ArrayList<ObjectFishDef> fish = new ArrayList<ObjectFishDef>();

		for (ObjectFishDef def : objectFishDef.getFishDefs()) {
			if (fishingLevel >= def.getReqLevel()) {
				fish.add(def);
			}
		}
		if (fish.size() <= 0) {
			return null;
		}
		ObjectFishDef thisFish = fish.get(DataConversions.random(0, fish.size() - 1));
		int levelDiff = fishingLevel - thisFish.getReqLevel();
		if (levelDiff < 0) {
			return null;
		}
		return DataConversions.percentChance(offsetToPercent(levelDiff)) ? thisFish : null;
	}

	private static int offsetToPercent(int levelDiff) {
		return levelDiff > 40 ? 60 : 20 + levelDiff;
	}

	private String tryToCatchFishString(ObjectFishingDef def) {
		String name = "";
		if (def.getNetId() == 376) {
			name = "some fish";
		} else if (def.getNetId() == 375) {
			name = "a lobster";
		} else {
			name = "a fish";
		}
		return name;
	}
}
