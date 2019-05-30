package com.openrsc.server.plugins.skills;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.ObjectMiningDef;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.showBubble;
import static com.openrsc.server.plugins.Functions.sleep;

public final class Mining implements ObjectActionListener,
	ObjectActionExecutiveListener {

	/*static int[] ids;

	static {
		ids = new int[]{176, 100, 101, 102, 103, 104, 105, 106, 107, 108,
			109, 110, 111, 112, 113, 114, 115, 195, 196, 210, 211};
		Arrays.sort(ids);
	}*/

	public static int getAxe(Player p) {
		int lvl = p.getSkills().getLevel(Skills.MINING);
		for (int i = 0; i < Formulae.miningAxeIDs.length; i++) {
			if (p.getInventory().countId(Formulae.miningAxeIDs[i]) > 0) {
				if (lvl >= Formulae.miningAxeLvls[i]) {
					return Formulae.miningAxeIDs[i];
				}
			}
		}
		return -1;
	}

	@Override
	public void onObjectAction(final GameObject object, String command,
							   Player owner) {
		if (object.getID() == 269) {
			if (command.equalsIgnoreCase("mine")) {
				if (hasItem(owner, getAxe(owner))) {
					if (getCurrentLevel(owner, Skills.MINING) >= 50) {
						owner.message("you manage to dig a way through the rockslide");
						if (owner.getX() <= 425) {
							owner.teleport(428, 438);
						} else {
							owner.teleport(425, 438);
						}
					} else {
						owner.message("You need a mining level of 50 to clear the rockslide");
					}
				} else {
					owner.message("you need a pickaxe to clear the rockslide");
				}
			} else if (command.equalsIgnoreCase("prospect")) {
				owner.message("these rocks contain nothing intersting");
				owner.message("they are just in the way");
			}
		} else if (object.getID() == 770) {
			if (hasItem(owner, getAxe(owner))) {
				owner.setBusyTimer(1600);
				message(owner, "you mine the rock", "and break of several large chunks");
				addItem(owner, ItemId.ROCKS.id(), 1);
			} else {
				owner.message("you need a pickaxe to mine this rock");
			}
		} else if (object.getID() == 1026) { // watchtower - rock of dalgroth
			if (command.equalsIgnoreCase("mine")) {
				if (owner.getQuestStage(Constants.Quests.WATCHTOWER) == 9) {
					if (!hasItem(owner, getAxe(owner))) {
						owner.message("You need a pickaxe to mine the rock");
						return;
					}
					if (getCurrentLevel(owner, Skills.MINING) < 40) {
						owner.message("You need a mining level of 40 to mine this crystal out");
						return;
					}
					if (hasItem(owner, ItemId.POWERING_CRYSTAL4.id())) {
						playerTalk(owner, null, "I already have this crystal",
							"There is no benefit to getting another");
						return;
					}
					owner.playSound("mine");
					// special bronze pick bubble for rock of dalgroth - see wiki
					showBubble(owner, new Item(ItemId.BRONZE_PICKAXE.id()));
					owner.message("You have a swing at the rock!");
					message(owner, "You swing your pick at the rock...");
					owner.message("A crack appears in the rock and you prize a crystal out");
					addItem(owner, ItemId.POWERING_CRYSTAL4.id(), 1);
				} else {
					playerTalk(owner, null, "I can't touch it...",
						"Perhaps it is linked with the shaman some way ?");
				}
			} else if (command.equalsIgnoreCase("prospect")) {
				owner.playSound("prospect");
				message(owner, "You examine the rock for ores...");
				owner.message("This rock contains a crystal!");
			}
		} else {
			handleMining(object, owner, owner.click);
		}
	}

	private void handleMining(final GameObject object, Player owner, int click) {
		if (owner.isBusy()) {
			return;
		}
		if (!owner.withinRange(object, 1)) {
			return;
		}

		final ObjectMiningDef def = EntityHandler.getObjectMiningDef(object.getID());
		final int axeId = getAxe(owner);
		int retrytimes = -1;
		final int mineLvl = owner.getSkills().getLevel(Skills.MINING);
		final int mineXP = owner.getSkills().getExperience(Skills.MINING);
		int reqlvl = 1;
		switch (ItemId.getById(axeId)) {
			case BRONZE_PICKAXE:
				retrytimes = 1;
				break;
			case IRON_PICKAXE:
				retrytimes = 2;
				break;
			case STEEL_PICKAXE:
				retrytimes = 3;
				reqlvl = 6;
				break;
			case MITHRIL_PICKAXE:
				retrytimes = 5;
				reqlvl = 21;
				break;
			case ADAMANTITE_PICKAXE:
				retrytimes = 8;
				reqlvl = 31;
				break;
			case RUNE_PICKAXE:
				retrytimes = 12;
				reqlvl = 41;
				break;
			default:
				retrytimes = 1;
				break;
		}

		if (owner.click == 0 && (def == null || (def.getRespawnTime() < 1 && object.getID() != 496) || (def.getOreId() == 315 && owner.getQuestStage(Quests.FAMILY_CREST) < 6))) {
			if (axeId < 0 || reqlvl > mineLvl) {
				message(owner, "You need a pickaxe to mine this rock",
					"You do not have a pickaxe which you have the mining level to use");
				return;
			}
			owner.setBusyTimer(1800);
			owner.message("You swing your pick at the rock...");
			sleep(1800);
			owner.message("There is currently no ore available in this rock");
			return;
		}
		if (owner.click == 1) {
			owner.playSound("prospect");
			owner.setBusyTimer(1800);
			owner.message("You examine the rock for ores...");
			sleep(1800);
			if (object.getID() == 496) {
				// Tutorial Island rock handler
				message(owner, "This rock contains " + new Item(def.getOreId()).getDef().getName(),
						"Sometimes you won't find the ore but trying again may find it",
						"If a rock contains a high level ore",
						"You will not find it until you increase your mining level");
				if (owner.getCache().hasKey("tutorial") && owner.getCache().getInt("tutorial") == 49)
					owner.getCache().set("tutorial", 50);
			} else {
				if (def == null || def.getRespawnTime() < 1) {
					owner.message("There is currently no ore available in this rock");
				} else {
					owner.message("This rock contains " + new Item(def.getOreId()).getDef().getName());
				}
			}
			return;
		}
		if (axeId < 0 || reqlvl > mineLvl) {
			message(owner, "You need a pickaxe to mine this rock",
				"You do not have a pickaxe which you have the mining level to use");
			return;
		}
		if (owner.getFatigue() >= owner.MAX_FATIGUE) {
			owner.message("You are too tired to mine this rock");
			return;
		}
		if (object.getID() == 496 && mineXP >= 210) {
			owner.message("Thats enough mining for now");
			return;
		}
		owner.playSound("mine");
		showBubble(owner, new Item(ItemId.IRON_PICKAXE.id()));
		owner.message("You swing your pick at the rock...");
		owner.setBatchEvent(new BatchEvent(owner, 1800, 1000 + retrytimes, true) {
			@Override
			public void action() {
				final Item ore = new Item(def.getOreId());
				if (getOre(def, owner.getSkills().getLevel(Skills.MINING), axeId) && mineLvl >= def.getReqLevel()) {
					if (DataConversions.random(1, 200) <= (owner.getInventory().wielding(ItemId.CHARGED_DRAGONSTONE_AMULET.id()) ? 2 : 1)) {
						owner.playSound("foundgem");
						Item gem = new Item(getGem(), 1);
						owner.getInventory().add(gem);
						owner.message("You just found a" + gem.getDef().getName().toLowerCase().replaceAll("uncut", "") + "!");
						interrupt();
					} else {
						//check if there is still ore at the rock
						GameObject obj = owner.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
						if (obj == null) {
							owner.message("You only succeed in scratching the rock");
						} else {
							owner.getInventory().add(ore);
							owner.message("You manage to obtain some " + ore.getDef().getName().toLowerCase());
							owner.incExp(Skills.MINING, def.getExp(), true);
						}
						interrupt();
						if (obj != null && obj.getID() == object.getID() && def.getRespawnTime() > 0) {
							GameObject newObject = new GameObject(object.getLocation(), 98, object.getDirection(), object.getType());
							World.getWorld().replaceGameObject(object, newObject);
							World.getWorld().delayedSpawnObject(obj.getLoc(), def.getRespawnTime() * 1000);
						}
						if (object.getID() == 496 && owner.getCache().hasKey("tutorial") && owner.getCache().getInt("tutorial") == 51)
							owner.getCache().set("tutorial", 52);
					}
				} else {
					if (object.getID() == 496) {
						owner.message("You fail to make any real impact on the rock");
					} else {
						owner.message("You only succeed in scratching the rock");
						if (getRepeatFor() > 1) {
							GameObject checkObj = owner.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
							if (checkObj == null) {
								interrupt();
							}
						}
					}
				}
				if (!isCompleted()) {
					showBubble(owner, new Item(ItemId.IRON_PICKAXE.id()));
					owner.message("You swing your pick at the rock...");
				}

			}
		});
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return (command.equals("mine") || command.equals("prospect")) && obj.getID() != 588;
	}

	/**
	 * Returns a gem ID
	 */
	public static int getGem() {
		int rand = DataConversions.random(0, 100);
		if (rand < 10) {
			return ItemId.UNCUT_DIAMOND.id();
		} else if (rand < 30) {
			return ItemId.UNCUT_RUBY.id();
		} else if (rand < 60) {
			return ItemId.UNCUT_EMERALD.id();
		} else {
			return ItemId.UNCUT_SAPPHIRE.id();
		}
	}

	private static int calcAxeBonus(int axeId) { // No evidence wielding different pickaxes gives a bonus, only more swings
		/*switch (axeId) {
			case BRONZE_PICKAXE:
				bonus = 0;
				break;
			case IRON_PICKAXE:
				bonus = 1;
				break;
			case STEEL_PICKAXE:
				bonus = 2;
				break;
			case MITHRIL_PICKAXE:
				bonus = 4;
				break;
			case ADAMANTITE_PICKAXE:
				bonus = 8;
				break;
			case RUNE_PICKAXE:
				bonus = 16;
				break;
		}*/
		return 0;
	}

	/**
	 * Should we can get an ore from the rock?
	 */
	private static boolean getOre(ObjectMiningDef def, int miningLevel, int axeId) {
		return Formulae.calcGatheringSuccessful(def.getReqLevel(), miningLevel, calcAxeBonus(axeId));
	}
}
