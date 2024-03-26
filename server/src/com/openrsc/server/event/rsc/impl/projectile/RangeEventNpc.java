package com.openrsc.server.event.rsc.impl.projectile;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.KillType;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.world.World;
import com.openrsc.server.util.rsc.Formulae;

public class RangeEventNpc extends GameTickEvent {

    private final Mob victim;

    public RangeEventNpc(World world, Npc owner, Mob victim) {
        super(world, owner, 1, "Range Event NPC", DuplicationStrategy.ALLOW_MULTIPLE);
        this.victim = victim;
    }

    public boolean equals(Object o) {
        if (o instanceof RangeEventNpc) {
            RangeEventNpc e = (RangeEventNpc) o;
            return e.belongsTo(getOwner());
        }
        return false;
    }

    public void run() {
        final Mob owner = getOwner();
        if ((victim.isPlayer() && !((Player) victim).loggedIn())
                || victim.getSkills().getLevel(Skill.HITS.id()) <= 0
                || !owner.withinRange(victim)) {
            owner.resetRange();
            return;
        }
        if (owner.inCombat()) {
            owner.resetRange();
            return;
        }
        if (!victim.getLocation().inBounds(((Npc) owner).getLoc().minX - 9, ((Npc) owner).getLoc().minY - 9,
                ((Npc) owner).getLoc().maxX + 9, ((Npc) owner).getLoc().maxY + 9) && owner.isNpc()) {
            owner.resetRange();
            return;
        }
        if (owner.getLocation().inWilderness() && victim.getLocation().inWilderness() && isUnreachable(victim)) {
            owner.walkToEntity(victim.getX(), victim.getY());
            if (owner.nextStep(owner.getX(), owner.getY(), victim) == null) {
                Player playerTarget = (Player) victim;
                playerTarget.message("You got away");
                owner.resetRange();
            }
        } else if (!owner.getLocation().inWilderness() && !victim.getLocation().inWilderness() && isUnreachable(victim)) {
            owner.walkToEntity(victim.getX(), victim.getY());
            if (owner.nextStep(owner.getX(), owner.getY(), victim) == null) {
                Player playerTarget = (Player) victim;
                playerTarget.message("You got away");
                owner.resetRange();
            }
        } else if (!owner.getLocation().inWilderness() && !victim.getLocation().inWilderness() && isUnreachable(victim)) {
            Player playerTarget = (Player) victim;
            playerTarget.message("You got away");
            owner.resetRange();
        } else {
            owner.resetPath();
                if (!PathValidation.checkPath(getWorld(), owner.getLocation(), victim.getLocation())) {
                    owner.resetRange();
                    stop();
                    return;
                }
                owner.face(victim);
            setDelayTicks(3);


                if (victim.isPlayer()) {
                    Player playerTarget = (Player) victim;
                    if (playerTarget.getPrayers().isPrayerActivated(Prayers.PROTECT_FROM_MISSILES)) {
                        playerTarget.message(owner + " is trying to shoot you!");
                        stop();
                        return;
                    }
                }
                int damage = RangeUtils.doRangedDamage(getPlayerOwner(), ItemId.LONGBOW.id(), ItemId.BRONZE_ARROWS.id(), victim, false);

                if (Formulae.loseArrow(damage)) {
                    GroundItem arrows = getArrows(getPlayerOwner());
                    if (arrows == null) {
                        for (Player p : getWorld().getPlayers()) {
                            getWorld().registerItem(new GroundItem(
                                    p.getWorld(),
                                    ItemId.BRONZE_ARROWS.id(),
                                    victim.getX(),
                                    victim.getY(),
                                    1,
                                    p
                            ));
                        }
                    } else {
                        arrows.setAmount(arrows.getAmount() + 1);
                    }
                }
                if (victim.isPlayer() && owner.isNpc()) {
                    ((Player) victim).message(owner + " is shooting at you!");
                }
                getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(getWorld(), owner, victim, damage, 2));
                owner.setKillType(KillType.RANGED);
            }
    }

    private GroundItem getArrows(Player player) {
        return victim.getViewArea().getVisibleGroundItem(ItemId.BRONZE_ARROWS.id(), victim.getLocation(), player);
    }

    private boolean isUnreachable(Mob mob) {
        int radius = 5;
        return !getOwner().withinRange(mob, radius);
    }
}
