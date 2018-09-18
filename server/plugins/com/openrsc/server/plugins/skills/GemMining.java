package com.openrsc.server.plugins.skills;

import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.showBubble;

import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

public class GemMining implements ObjectActionListener,
ObjectActionExecutiveListener {

	public static final int GEM_ROCK = 588;
	
	public final int UNCUT_OPAL = 891;
	public final int UNCUT_JADE = 890;
	public final int UNCUT_RED_TOPAZ = 889;
	public final int UNCUT_SAPPHIRE = 160;
	public final int UNCUT_EMERALD = 159;
	public final int UNCUT_RUBY = 158;
	public final int UNCUT_DIAMOND = 157;

	private void handleGemRockMining(final GameObject obj, Player p, int click) {
		if (p.isBusy()) {
			return;
		}
		if (!p.withinRange(obj, 1)) {
			return;
		}
		final int axeId = getAxe(p);
		int retrytimes = -1;
		final int mineLvl = p.getSkills().getLevel(14);
		int reqlvl = 1;
		switch (axeId) {
		case 156:
			retrytimes = 1;
			break;
		case 1258:
			retrytimes = 2;
			break;
		case 1259:
			retrytimes = 3;
			reqlvl = 6;
			break;
		case 1260:
			retrytimes = 5;
			reqlvl = 21;
			break;
		case 1261:
			retrytimes = 8;
			reqlvl = 31;
			break;
		case 1262:
			retrytimes = 12;
			reqlvl = 41;
			break;
		}

		if (axeId < 0 || reqlvl > mineLvl) {
			message(p, "You need a pickaxe to mine this rock",
					"You do not have a pickaxe which you have the mining level to use");
			return;
		}

		if (p.getFatigue() >= 7500) {
			p.message("You are too tired to mine this rock");
			return;
		}

		p.playSound("mine");
		showBubble(p, new Item(1258));
		p.message("You have a swing at the rock!");
		p.setBatchEvent(new BatchEvent(p, 2000, retrytimes) {
			@Override
			public void action() {
				if (getGem(p, 40, owner.getSkills().getLevel(14), axeId) && mineLvl >= 40) { // always 40 required mining.
					Item gem = new Item(getGemFormula(), 1);
					owner.message(minedString(gem.getID()));
					owner.incExp(14, 200, true); // always 50XP
					owner.getInventory().add(gem);
					interrupt();
					GameObject object = owner.getViewArea().getGameObject(obj.getID(), obj.getX(), obj.getY());
					if(object != null && object.getID() == obj.getID()) {
						GameObject newObject = new GameObject(obj.getLocation(), 98, obj.getDirection(), obj.getType());
						World.getWorld().replaceGameObject(obj, newObject);
						World.getWorld().delayedSpawnObject(object.getLoc(), 120 * 1000); // 2 minutes respawn time
					}
				} else {
					owner.message("You only succeed in scratching the rock");
					if(getRepeatFor() > 1) {
						GameObject checkObj = owner.getViewArea().getGameObject(obj.getID(), obj.getX(), obj.getY());
						if(checkObj == null) {
							interrupt();
						}
					}
				}
				if(!isCompleted()) {
					showBubble(owner, new Item(1258));
					owner.message("You have a swing at the rock!");
				}
			}
		});
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == GEM_ROCK && command.equals("mine")) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == GEM_ROCK && command.equals("mine")) {
			handleGemRockMining(obj, p, p.click);
		}
	}

	private int getAxe(Player p) {
		int lvl = p.getSkills().getLevel(14);
		for (int i = 0; i < Formulae.miningAxeIDs.length; i++) {
			if (p.getInventory().countId(Formulae.miningAxeIDs[i]) > 0) {
				if (lvl >= Formulae.miningAxeLvls[i]) {
					return Formulae.miningAxeIDs[i];
				} 
			}
		}
		return -1;
	}
	
	private boolean getGem(Player p, int req, int miningLevel, int axeId) {
		int levelDiff = miningLevel - req;
		if (levelDiff > 50)
			return DataConversions.random(0, 9) != 1;
		if (levelDiff < 0) {
			return false;
		}
		int bonus = 0;
		switch (axeId) {
		case 156:
			bonus = 0;
			break;
		case 1258:
			bonus = 2;
			break;
		case 1259:
			bonus = 6;
			break;
		case 1260:
			bonus = 8;
			break;
		case 1261:
			bonus = 10;
			break;
		case 1262:
			bonus = 12;
			break;
		}
		if(p.getInventory().wielding(597)) { // charged dragonstone amulet bonus
			bonus = 5; // 5%
		}
		return DataConversions.percentChance(offsetToPercent(levelDiff + bonus));
	}
	private int offsetToPercent(int levelDiff) {
		return levelDiff > 40 ? 60 : 20 + levelDiff;
	}
	/**
	 * Returns a gem ID
	 */
	private int getGemFormula() {
		int rand = DataConversions.random(0, 100);
		if (rand >= 0 && rand <= 46.86) {
			return UNCUT_OPAL;
		} else if (rand >= 46.86 && rand <= 70.12) {
			return UNCUT_JADE;
		} else if (rand >= 70.12 && rand <= 81.88) {
			return UNCUT_RED_TOPAZ;
		} else if (rand >= 81.88 && rand <= 89.02) {
			return UNCUT_SAPPHIRE;
		} else if (rand >= 89.02 && rand <= 92.96) {
			return UNCUT_EMERALD;
		} else if (rand >= 92.96 && rand <= 96.85) {
			return UNCUT_RUBY;
		} else if (rand >= 96.85 && rand <= 100) {
			return UNCUT_DIAMOND;
		} 
		return -1;
	}
	
	private String minedString(int gemID) {
		if(gemID == UNCUT_OPAL) {
			return "You just mined an Opal!";
		} else if(gemID == UNCUT_JADE) {
			return "You just mined a piece of Jade!";
		} else if(gemID == UNCUT_RED_TOPAZ) {
			return "You just mined a Red Topaz!";
		} else if(gemID == UNCUT_SAPPHIRE) {
			return "You just found a sapphire!";
		} else if(gemID == UNCUT_EMERALD) {
			return "You just found an emerald!";
		} else if(gemID == UNCUT_RUBY) {
			return "You just found a ruby!";
		} else if(gemID == UNCUT_DIAMOND) {
			return "You just found a diamond!";
		}
		return null;
	}
}
