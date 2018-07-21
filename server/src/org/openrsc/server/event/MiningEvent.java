package org.openrsc.server.event;

import org.openrsc.server.entityhandling.defs.extras.ObjectMiningDef;
import org.openrsc.server.model.GameObject;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;

public class MiningEvent extends DelayedEvent {
	private int maxSwings;
	private int swings;
	private InvItem ore;
	private int axeId;
	private ObjectMiningDef def;
	private GameObject object;
	
	public MiningEvent(final Player player, final ObjectMiningDef def, final GameObject object, final int axeId, final int maxSwings) {
		super(player, 1500);
		this.maxSwings = maxSwings;
		this.axeId = axeId;
		this.def = def;
		this.ore = new InvItem(def.getOreId());
		this.object = object;
		this.swings = 0;
	}
	
	public void run() {
		if (ore == null || def == null || object == null) {
			owner.setBusy(false);
			super.running = false;
		} else {
			if (swings < maxSwings) {
				if (swings != 0) {
					owner.sendSound("mine", false);
					//Bubble bubble = new Bubble(owner.getIndex(), axeId);
					for (Player p : owner.getViewArea().getPlayersInView())
					{
						p.watchItemBubble(owner.getIndex(), axeId);
						//p.informOfBubble(bubble);
					}
					owner.sendMessage("You swing your pick at the rock...");
				}
				if (Formulae.getOre(def, owner.getCurStat(14), axeId)) {
					if (DataConversions.random(0, owner.isWearing(597) ? 100 : 200) == 0) {
						InvItem gem = new InvItem(Formulae.getGem(), 1);
						owner.getInventory().add(gem);
						owner.sendMessage("You found a gem!");
					} else {
						owner.getInventory().add(ore);
						owner.sendMessage("You manage to obtain some " + ore.getDef().getName() + ".");
						owner.increaseXP(14, def.getExp());
						owner.sendStat(14);
						World.registerEntity(new GameObject(object.getLocation(), 98, object.getDirection(), object.getType()));
						World.delayedSpawnObject(object.getLoc(), def.getRespawnTime() * 1000);
					}
					owner.sendInventory();
					owner.setBusy(false);
					super.running = false;
				} else {
					owner.sendMessage("You only succeed in scratching the rock.");
					swings++;
				}
			} else {
				owner.setBusy(false);
				super.running = false;
			}
		}
	}
}