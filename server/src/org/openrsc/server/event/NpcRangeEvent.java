package org.openrsc.server.event;

import java.util.ArrayList;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Item;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Projectile;
import org.openrsc.server.model.TrajectoryHandler;
import org.openrsc.server.model.World;
import org.openrsc.server.util.Formulae;

import java.lang.Math;

public class NpcRangeEvent extends DelayedEvent {

	private ArrayList<Player> playersToInform;
	private Npc target;
	
	public NpcRangeEvent(Player player, Npc npc) {
		super(player, 2000);
		this.target = npc;
		playersToInform = new ArrayList<Player>();
	}
	
	public boolean canShoot() {
		double[] bowStats = owner.getBowStats();
		boolean canShoot = false;
		if (bowStats != null && owner.castTimer() && owner.canRange() && owner.withinRange(target, (int) bowStats[2]) && owner.checkAttack(target, true))
			canShoot = true;
		return canShoot;
	}
	
	public void run() {
		//if(target.getLocation().distanceTo(owner.getX(), owner.getY()) > 7) 
			//owner.setFollowing(target, 7);
		//} else {
			owner.resetPath();
		//}
		if (canShoot() && !target.isRemoved()) {
			if (!TrajectoryHandler.isRangedBlocked(owner.getX(), owner.getY(), target.getX(), target.getY())) {
				int arrowId = owner.checkForUsableArrows();
				if (arrowId != -1) {
					int damage = Formulae.calcRangeHit(owner.getCurStat(4), owner.getRangePoints(), target.getArmourPoints(), arrowId);
					if (damage > target.getHits())
						damage = target.getHits();
					owner.setSprite(Math.abs(target.getLocation().getX() - owner.getX()));
					if (!Formulae.loseArrow(arrowId)) {
						Item arrows = getArrows(arrowId);
						if (arrows == null)
							World.registerEntity(new Item(arrowId, target.getX(), target.getY(), 1, owner));						
						else
							arrows.setAmount(arrows.getAmount() + 1);					
					}
					removeArrow(arrowId);
					owner.resetRangeTimer();
					Projectile projectile = new Projectile(owner, target, 2);
					target.setLastDamage(damage);
					target.updateKillStealing(owner, damage, 1);
					int newHp = target.getHits() - damage;
					target.setHits(target.getHits() - damage);
					if(!target.inCombat()) {
						target.setAggressive(owner);
					}
					playersToInform.addAll(owner.getViewArea().getPlayersInView());
					playersToInform.addAll(target.getViewArea().getPlayersInView());
					final ArrayList<Player> delayedInformants = playersToInform;
					for (Player p : playersToInform)
						p.informOfProjectile(projectile);	
					
					World.getDelayedEventHandler().add(new SingleEvent(null, 500) {
						public void action() {
							for (Player p : delayedInformants)
								p.informOfModifiedHits(target);						
						}
					});
					owner.sendSound("shoot", false);
					owner.setArrowFired();
					owner.setLastRange();
					
					if (target.getID() == 196) {
						playersToInform.clear();
						owner.sendMessage("Your projectile's damage was reflected!");
						owner.setLastDamage(damage);
						owner.setHits(owner.getHits() - damage);
						owner.sendStat(3);
						if (owner.getHits() < 1)
							owner.killedBy(target, false);
						playersToInform.addAll(owner.getViewArea().getPlayersInView());
						for (Player p : playersToInform)
							p.informOfModifiedHits(owner);				
					}
					if (newHp <= 0) {
						target.killedBy(owner);
						owner.resetRange();
					} else if (!target.isBusy() && rangerWithinBounds() && !target.isAggressive())
						target.setAggressive(owner);
				} else {
					getOwner().sendMessage("You don't have any usable arrows!");
					owner.resetRange();
				}
			} else {
				owner.sendMessage("I can't get a clear shot from here!");
				owner.resetRange();				
			}
		}
	}

	private boolean rangerWithinBounds() {
		return (owner.getLocation().getX() < target.getLoc().maxX() && owner.getLocation().getX() > target.getLoc().minX() && owner.getLocation().getY() < target.getLoc().maxY() && owner.getLocation().getY() > target.getLoc().minY());
	}
	
	private Item getArrows(int id) {
		for (Item i : World.getZone(target.getX(), target.getY()).getItemsAt(target.getX(), target.getY())) {
			if (i.getID() == id && i.visibleTo(owner) && !i.isRemoved())
				return i;
		}
		return null;
	}

	public void removeArrow(int id) {
		InvItem arrow = new InvItem(id, 1);
		owner.getInventory().remove(arrow);
		owner.sendInventory();
	}
}
