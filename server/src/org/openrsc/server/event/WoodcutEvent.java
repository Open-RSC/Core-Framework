package org.openrsc.server.event;

import org.openrsc.server.model.*;
import org.openrsc.server.util.Formulae;
import org.openrsc.server.util.DataConversions;

public class WoodcutEvent extends ShortEvent {
	private int axe;
	private int level;
	private int exp;
	private int fell;
	private int logID;
	private int respawnTime;
	private GameObject tree;
	
	public WoodcutEvent(Player owner, int axe, GameObject tree, int level, int exp, int fell, int respawnTime, int logID) {
		super(owner);
		this.axe = axe;
		this.exp = exp;
		this.level = level;
		this.tree = tree;
		this.fell = fell;
		this.logID = logID;
		this.respawnTime = respawnTime;
	}
	
	public void action() {
		if (tree != null) {
			if(Formulae.getLog(level, owner.getCurStat(8), axe)) {
				InvItem log = new InvItem(logID);
				owner.getInventory().add(log);
				owner.sendMessage("You get some wood.");
				owner.sendInventory();
				owner.increaseXP(Skills.WOODCUT, exp);
				owner.sendStat(8);
				if (DataConversions.random(1, 100) <= fell) {
					World.registerEntity(new GameObject(tree.getLocation(), 4, tree.getDirection(), tree.getType()));
					World.delayedSpawnObject(tree.getLoc(), respawnTime * 1000);
				}
			} else
				owner.sendMessage("You slip and fail to hit the tree.");
			owner.setBusy(false);	
		}
	}
}