package com.openrsc.server.plugins.skills;

import com.openrsc.server.Constants;
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
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.*;

public class GemMining implements ObjectActionListener,
	ObjectActionExecutiveListener {

	private static final int GEM_ROCK = 588;

	private static final int[] gemWeightsWithoutDragonstone = {64, 32, 16, 8, 3, 3, 2};
	private static final int[] gemWeightsWithDragonstone = {60, 30, 15, 9, 5, 5, 4};
	private static final int[] gemIds = {
		ItemId.UNCUT_OPAL.id(),
		ItemId.UNCUT_JADE.id(),
		ItemId.UNCUT_RED_TOPAZ.id(),
		ItemId.UNCUT_SAPPHIRE.id(),
		ItemId.UNCUT_EMERALD.id(),
		ItemId.UNCUT_RUBY.id(),
		ItemId.UNCUT_DIAMOND.id()
	};

	private void handleGemRockMining(final GameObject obj, Player p, int click) {
		if (p.isBusy()) {
			return;
		}
		if (!p.withinRange(obj, 1)) {
			return;
		}
		final ObjectMiningDef def = EntityHandler.getObjectMiningDef(obj.getID());
		final int axeId = getAxe(p);
		int retrytimes;
		final int mineLvl = p.getSkills().getLevel(Skills.MINING);
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

		if (p.click == 1) {
			p.playSound("prospect");
			p.setBusyTimer(1800);
			p.message("You examine the rock for ores...");
			sleep(1800);
			if (obj.getID() == GEM_ROCK) {
				p.message("You fail to find anything interesting");
				return;
			}
			//should not get into the else, just a fail-safe
			else {
				p.message("There is currently no ore available in this rock");
				return;
			}
		}

		if (axeId < 0 || reqlvl > mineLvl) {
			message(p, "You need a pickaxe to mine this rock",
				"You do not have a pickaxe which you have the mining level to use");
			return;
		}

		if (Constants.GameServer.WANT_FATIGUE) {
			if (p.getFatigue() >= p.MAX_FATIGUE) {
				p.message("You are too tired to mine this rock");
				return;
			}
		}

		p.playSound("mine");
		showBubble(p, new Item(ItemId.IRON_PICKAXE.id()));
		p.message("You have a swing at the rock!");
		p.setBatchEvent(new BatchEvent(p, 1800, 1000 + retrytimes, true) {
			@Override
			public void action() {
				if (getGem(p, 40, owner.getSkills().getLevel(Skills.MINING), axeId) && mineLvl >= 40) { // always 40 required mining.
					Item gem = new Item(getGemFormula(p.getInventory().wielding(ItemId.CHARGED_DRAGONSTONE_AMULET.id())), 1);
					//check if there is still gem at the rock
					GameObject object = owner.getViewArea().getGameObject(obj.getID(), obj.getX(), obj.getY());
					if (object == null) {
						owner.message("You only succeed in scratching the rock");
					} else {
						owner.message(minedString(gem.getID()));
						owner.incExp(Skills.MINING, 200, true); // always 50XP
						owner.getInventory().add(gem);
					}
					interrupt();
					
					if (object != null && object.getID() == obj.getID()) {
						GameObject newObject = new GameObject(obj.getLocation(), 98, obj.getDirection(), obj.getType());
						World.getWorld().replaceGameObject(obj, newObject);
						World.getWorld().delayedSpawnObject(object.getLoc(), 120 * 1000); // 2 minute respawn time
					}
				} else {
					owner.message("You only succeed in scratching the rock");
					if (getRepeatFor() > 1) {
						GameObject checkObj = owner.getViewArea().getGameObject(obj.getID(), obj.getX(), obj.getY());
						if (checkObj == null) {
							interrupt();
						}
					}
				}
				if (!isCompleted()) {
					showBubble(owner, new Item(ItemId.IRON_PICKAXE.id()));
					owner.message("You have a swing at the rock!");
				}
			}
		});
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return obj.getID() == GEM_ROCK && (command.equals("mine") || command.equals("prospect"));
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == GEM_ROCK && (command.equals("mine") || command.equals("prospect"))) {
			handleGemRockMining(obj, p, p.click);
		}
	}

	private int getAxe(Player p) {
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

	private static boolean getGem(Player p, int req, int miningLevel, int axeId) {
		return Formulae.calcGatheringSuccessful(req, miningLevel, calcAxeBonus(axeId));
	}

	/**
	 * Returns a gem ID
	 */
	private int getGemFormula(boolean dragonstoneAmmy) {
		return dragonstoneAmmy ?
			Formulae.weightedRandomChoice(gemIds, gemWeightsWithDragonstone) :
			Formulae.weightedRandomChoice(gemIds, gemWeightsWithoutDragonstone);
	}

	private String minedString(int gemID) {
		if (gemID == ItemId.UNCUT_OPAL.id()) {
			return "You just mined an Opal!";
		} else if (gemID == ItemId.UNCUT_JADE.id()) {
			return "You just mined a piece of Jade!";
		} else if (gemID == ItemId.UNCUT_RED_TOPAZ.id()) {
			return "You just mined a Red Topaz!";
		} else if (gemID == ItemId.UNCUT_SAPPHIRE.id()) {
			return "You just found a sapphire!";
		} else if (gemID == ItemId.UNCUT_EMERALD.id()) {
			return "You just found an emerald!";
		} else if (gemID == ItemId.UNCUT_RUBY.id()) {
			return "You just found a ruby!";
		} else if (gemID == ItemId.UNCUT_DIAMOND.id()) {
			return "You just found a diamond!";
		}
		return null;
	}
}
