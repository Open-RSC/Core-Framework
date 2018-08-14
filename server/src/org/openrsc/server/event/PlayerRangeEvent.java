package org.openrsc.server.event;

import java.util.ArrayList;

import org.openrsc.server.Config;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Item;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Projectile;
import org.openrsc.server.model.TrajectoryHandler;
import org.openrsc.server.model.World;
import org.openrsc.server.util.Formulae;

public class PlayerRangeEvent extends DelayedEvent {
	private ArrayList<Player> playersToInform;
	private boolean firstRun = true;
	private Player target;
	
	public PlayerRangeEvent(Player ranger, Player target) {
		super(ranger, 1000);
		this.target = target;
		playersToInform = new ArrayList<Player>();
	}

	public void run() {
		owner.resetPath();
		clearTargetWindows();
		boolean withinRange = withinRange();
		
		if (canShoot() && withinRange) {
			if (!TrajectoryHandler.isRangedBlocked(owner.getX(), owner.getY(), target.getX(), target.getY())) {
				if (!target.isPrayerActivated(13)) {
					int arrowId = owner.checkForUsableArrows();
					if (arrowId != -1) {
						
						/*
						 * One vs One
						 * Implementation
						 */
						
						if (owner.getLocation().varrockWilderness() || target.getLocation().varrockWilderness() || Config.isPkMode())
						{
							if (target.inCombat())
							{
								owner.sendMessage(Config.getPrefix() + "This player cannot be ranged whilst in combat with another player.");
								owner.resetRange();
								owner.resetFollowing();
								return;
							}
						}
                        
						if (!target.getLocation().isInWarZone() || !World.pvpEnabled) 
						{
                            owner.sendMessage(Config.getPrefix() + "PVP is currently disabled.");
							owner.resetFollowing();
							owner.resetPath();															
							return;															
						}
                        
                        if ( target.isInvulnerable() /*affectedPlayer.isSuperMod() || affectedPlayer.isDev() || affectedPlayer.isEvent()*/)
                        {
                        	owner.sendMessage(Config.getPrefix() + target.getUsername() + " is currently invulnerable!");
                        	owner.resetFollowing();
                        	owner.resetPath();
                        	return;
						}
						
						int damage = Formulae.calcRangeHit(owner.getCurStat(4), owner.getRangePoints(), target.getArmourPoints(), arrowId);
						owner.setSprite(Math.abs(target.getLocation().getX() - owner.getX()));
						if (!owner.getLocation().isInDMArena()) {
							if (!Formulae.loseArrow(arrowId)) {
								Item arrows = getArrows(arrowId);
								if (arrows == null)
									World.registerEntity(new Item(arrowId, target.getX(), target.getY(), 1, owner));						
								else
									arrows.setAmount(arrows.getAmount() + 1);					
							}
						}
						removeArrow(arrowId);
						owner.resetRangeTimer();
						if (firstRun) {
							firstRun = false;
							target.sendMessage("Warning! " + owner.getUsername() + " is shooting at you!");
						}
						Projectile projectile = new Projectile(owner, target, 2);
						target.setLastDamage(damage);
                        target.updateKillStealing(owner, damage, 1);
						int newHp = target.getHits() - damage;
						target.setHits(newHp);
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
						
						target.sendStat(3);
						owner.sendSound("shoot", false);
						owner.setArrowFired();	
						if (newHp <= 0) {
							target.killedBy(owner, false);
							owner.resetRange();
						}	
					} else {
						getOwner().sendMessage("You don't have any usable arrows!");
						owner.resetRange();			
					}
				} else {
					getOwner().sendMessage("This player is invulnerable to missiles!");
					owner.resetRange();
				}
			} else {
				owner.sendMessage("I can't get a clear shot from here!");
				owner.resetRange();	
			}
		} else if (!withinRange) {
			owner.resetRange();
		}
	}
	
	public void clearTargetWindows() {
		target.resetTrade();
		if (target.getMenuHandler() != null)
		{
			target.getMenuHandler().onMenuCancelled();
			target.resetMenuHandler();
		}
		if (target.accessingBank())
			target.resetBank();
		if (target.accessingShop())
			target.resetShop();
		if (target.getNpc() != null) {
			target.getNpc().unblock();
			target.setNpc(null);
		}
	}
	
	public boolean canShoot() {
		double[] bowStats = owner.getBowStats();
		boolean canShoot = false;
		if (bowStats != null && owner.canRange() && owner.checkAttack(target, true))
			canShoot = true;
		return canShoot;
	}
	
	private boolean withinRange() {		
		return owner.withinRange(target, (int) owner.getBowStats()[2]);
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