package com.openrsc.server.plugins.skills;

import com.openrsc.server.Server;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.ObjectMiningDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public final class Mining implements ObjectActionListener,
	ObjectActionExecutiveListener {

	/*static int[] ids;

	static {
		ids = new int[]{176, 100, 101, 102, 103, 104, 105, 106, 107, 108,
			109, 110, 111, 112, 113, 114, 115, 195, 196, 210, 211};
		Arrays.sort(ids);
	}*/

	public static int getAxe(Player p) {
		int lvl = p.getSkills().getLevel(com.openrsc.server.constants.Skills.MINING);
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
							   Player player) {
		if (object.getID() == 269) {
			if (command.equalsIgnoreCase("mine")) {
				if (hasItem(player, getAxe(player))) {
					if (getCurrentLevel(player, com.openrsc.server.constants.Skills.MINING) >= 50) {
						player.message("you manage to dig a way through the rockslide");
						if (player.getX() <= 425) {
							player.teleport(428, 438);
						} else {
							player.teleport(425, 438);
						}
					} else {
						player.playerServerMessage(MessageType.QUEST, "You need a mining level of 50 to clear the rockslide");
					}
				} else {
					player.playerServerMessage(MessageType.QUEST, "you need a pickaxe to clear the rockslide");
				}
			} else if (command.equalsIgnoreCase("prospect")) {
				player.playerServerMessage(MessageType.QUEST, "these rocks contain nothing interesting");
				player.playerServerMessage(MessageType.QUEST, "they are just in the way");
			}
		} else if (object.getID() == 770) {
			if (hasItem(player, getAxe(player))) {
				player.setBusyTimer(1600);
				message(player, "you mine the rock", "and break of several large chunks");
				addItem(player, ItemId.ROCKS.id(), 1);
			} else {
				player.message("you need a pickaxe to mine this rock");
			}
		} else if (object.getID() == 1026) { // watchtower - rock of dalgroth
			if (command.equalsIgnoreCase("mine")) {
				if (player.getQuestStage(Quests.WATCHTOWER) == 9) {
					if (!hasItem(player, getAxe(player))) {
						player.playerServerMessage(MessageType.QUEST, "You need a pickaxe to mine the rock");
						return;
					}
					if (getCurrentLevel(player, com.openrsc.server.constants.Skills.MINING) < 40) {
						player.playerServerMessage(MessageType.QUEST, "You need a mining level of 40 to mine this crystal out");
						return;
					}
					if (hasItem(player, ItemId.POWERING_CRYSTAL4.id())) {
						playerTalk(player, null, "I already have this crystal",
							"There is no benefit to getting another");
						return;
					}
					player.playSound("mine");
					// special bronze pick bubble for rock of dalgroth - see wiki
					showBubble(player, new Item(ItemId.BRONZE_PICKAXE.id()));
					player.message("You have a swing at the rock!");
					player.playerServerMessage(MessageType.QUEST, "You swing your pick at the rock...");
					player.message("A crack appears in the rock and you prize a crystal out");
					addItem(player, ItemId.POWERING_CRYSTAL4.id(), 1);
				} else {
					playerTalk(player, null, "I can't touch it...",
						"Perhaps it is linked with the shaman some way ?");
				}
			} else if (command.equalsIgnoreCase("prospect")) {
				player.playSound("prospect");
				player.playerServerMessage(MessageType.QUEST, "You examine the rock for ores...");
				player.playerServerMessage(MessageType.QUEST, "This rock contains a crystal!");
			}
		} else {
			handleMining(object, player, player.click);
		}
	}

	private void handleMining(final GameObject object, Player player, int click) {
		if (player.isBusy()) {
			return;
		}
		if (!player.withinRange(object, 1)) {
			return;
		}

		final ObjectMiningDef def = player.getWorld().getServer().getEntityHandler().getObjectMiningDef(object.getID());
		final int axeId = getAxe(player);
		int retrytimes = -1;
		final int mineLvl = player.getSkills().getLevel(com.openrsc.server.constants.Skills.MINING);
		final int mineXP = player.getSkills().getExperience(Skills.MINING);
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

		if (player.click == 0 && (def == null || (def.getRespawnTime() < 1 && object.getID() != 496) || (def.getOreId() == 315 && player.getQuestStage(Quests.FAMILY_CREST) < 6))) {
			if (axeId < 0 || reqlvl > mineLvl) {
				message(player, "You need a pickaxe to mine this rock",
					"You do not have a pickaxe which you have the mining level to use");
				return;
			}
			player.setBusyTimer(1800);
			player.playerServerMessage(MessageType.QUEST, "You swing your pick at the rock...");
			sleep(1800);
			player.playerServerMessage(MessageType.QUEST, "There is currently no ore available in this rock");
			return;
		}
		if (player.click == 1) {
			player.playSound("prospect");
			player.setBusyTimer(1800);
			player.playerServerMessage(MessageType.QUEST, "You examine the rock for ores...");
			sleep(1800);
			if (object.getID() == 496) {
				// Tutorial Island rock handler
				message(player, "This rock contains " + new Item(def.getOreId()).getDef(player.getWorld()).getName(),
						"Sometimes you won't find the ore but trying again may find it",
						"If a rock contains a high level ore",
						"You will not find it until you increase your mining level");
				if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 49)
					player.getCache().set("tutorial", 50);
			} else {
				if (def == null || def.getRespawnTime() < 1) {
					player.playerServerMessage(MessageType.QUEST, "You fail to find anything interesting");
				} else {
					player.playerServerMessage(MessageType.QUEST, "This rock contains " + new Item(def.getOreId()).getDef(player.getWorld()).getName());
				}
			}
			return;
		}
		if (axeId < 0 || reqlvl > mineLvl) {
			message(player, "You need a pickaxe to mine this rock",
				"You do not have a pickaxe which you have the mining level to use");
			return;
		}
		if (player.getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 1
			&& player.getFatigue() >= player.MAX_FATIGUE) {
			player.playerServerMessage(MessageType.QUEST, "You are too tired to mine this rock");
			return;
		}
		if (object.getID() == 496 && mineXP >= 210) {
			player.message("Thats enough mining for now");
			return;
		}
		player.playSound("mine");
		showBubble(player, new Item(ItemId.IRON_PICKAXE.id()));
		player.playerServerMessage(MessageType.QUEST, "You swing your pick at the rock...");
		retrytimes = player.getWorld().getServer().getConfig().BATCH_PROGRESSION ? Formulae.getRepeatTimes(player, com.openrsc.server.constants.Skills.MINING) : retrytimes + 1000;
		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 1800, "Mining", retrytimes, true) {
			@Override
			public void action() {
				final Item ore = new Item(def.getOreId());
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 1
						&& getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
						getOwner().playerServerMessage(MessageType.QUEST, "You are too tired to mine this rock");
						interrupt();
						return;
					}
				}
				if (getOre(getWorld().getServer(), def, getOwner().getSkills().getLevel(com.openrsc.server.constants.Skills.MINING), axeId) && mineLvl >= def.getReqLevel()) {
					if (DataConversions.random(1, 200) <= (getOwner().getInventory().wielding(ItemId.CHARGED_DRAGONSTONE_AMULET.id()) ? 2 : 1)) {
						getOwner().playSound("foundgem");
						Item gem = new Item(getGem(), 1);
						getOwner().getInventory().add(gem);
						getOwner().playerServerMessage(MessageType.QUEST, "You just found a" + gem.getDef(getWorld()).getName().toLowerCase().replaceAll("uncut", "") + "!");
						interrupt();
					} else {
						//check if there is still ore at the rock
						GameObject obj = getOwner().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
						if (obj == null) {
							getOwner().playerServerMessage(MessageType.QUEST, "You only succeed in scratching the rock");
						} else {
							getOwner().getInventory().add(ore);
							getOwner().playerServerMessage(MessageType.QUEST, "You manage to obtain some " + ore.getDef(getWorld()).getName().toLowerCase());
							getOwner().incExp(com.openrsc.server.constants.Skills.MINING, def.getExp(), true);
						}
						if (object.getID() == 496 && getOwner().getCache().hasKey("tutorial") && getOwner().getCache().getInt("tutorial") == 51)
							getOwner().getCache().set("tutorial", 52);
						if (!getWorld().getServer().getConfig().MINING_ROCKS_EXTENDED || DataConversions.random(1, 100) <= def.getDepletion()) {
							interrupt();
							if (obj != null && obj.getID() == object.getID() && def.getRespawnTime() > 0) {
								GameObject newObject = new GameObject(getWorld(), object.getLocation(), 98, object.getDirection(), object.getType());
								getWorld().replaceGameObject(object, newObject);
								getWorld().delayedSpawnObject(obj.getLoc(), def.getRespawnTime() * 1000);
							}
						}
					}
				} else {
					if (object.getID() == 496) {
						getOwner().playerServerMessage(MessageType.QUEST, "You fail to make any real impact on the rock");
					} else {
						getOwner().playerServerMessage(MessageType.QUEST, "You only succeed in scratching the rock");
						if (getRepeatFor() > 1) {
							GameObject checkObj = getOwner().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
							if (checkObj == null) {
								interrupt();
							}
						}
					}
				}
				if (!isCompleted()) {
					showBubble(getOwner(), new Item(ItemId.IRON_PICKAXE.id()));
					getOwner().playerServerMessage(MessageType.QUEST, "You swing your pick at the rock...");
				}

			}
		});
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return (command.equals("mine") || command.equals("prospect")) && obj.getID() != 588 && obj.getID() != 1227;
	}

	/**
	 * Returns a gem ID
	 */
	public int getGem() {
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

	private int calcAxeBonus(Server server, int axeId) {
			//If server doesn't use batching, pickaxe shouldn't improve gathering chance
			if (!server.getConfig().BATCH_PROGRESSION)
				return 0;
			int bonus = 0;
			switch (ItemId.getById(axeId)) {
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
			}
			return bonus;
	}

	/**
	 * Should we can get an ore from the rock?
	 */
	private boolean getOre(Server server, ObjectMiningDef def, int miningLevel, int axeId) {
		return Formulae.calcGatheringSuccessful(def.getReqLevel(), miningLevel, calcAxeBonus(server, axeId));
	}
}
