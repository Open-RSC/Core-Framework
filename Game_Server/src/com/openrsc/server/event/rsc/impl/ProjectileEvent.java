package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.SingleTickEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.Damage;
import com.openrsc.server.model.entity.update.Projectile;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;

/**
 * @author n0m
 */
public class ProjectileEvent extends SingleTickEvent {

	Mob caster, opponent;
	protected int damage;
	protected int type;
	boolean canceled;

	public ProjectileEvent(World world, Mob caster, Mob opponent, int damage, int type) {
		super(world, caster, 1, "Projectile Event");
		this.caster = caster;
		this.opponent = opponent;
		this.damage = damage;
		this.type = type;
		if (caster.isPlayer() && opponent.isPlayer()) {
			caster.setAttribute("projectile", this);
			opponent.setAttribute("projectile", this);
		}

		// opponent.setCombatTimer();
		opponent.setAttribute("last_shot", System.currentTimeMillis());

		sendProjectile(caster, opponent);
		if (caster.isPlayer() && opponent.isPlayer()) {
			Player oppPlayer = (Player) opponent;
			Player casterPlayer = (Player) caster;
			if (!casterPlayer.getDuel().isDuelActive())
				casterPlayer.setSkulledOn(oppPlayer);
			String casterName = caster.isPlayer() ? ((Player) caster).getUsername() : ((Npc) caster).getDef().getName();

			oppPlayer.message("Warning! " + casterName + " is shooting at you!");
		}
	}

	private void sendProjectile(Mob caster, Mob opponent) {
		Projectile projectile = new Projectile(caster, opponent, type);
		caster.getUpdateFlags().setProjectile(projectile);
	}

	@Override
	public void action() {
		if (!canceled && caster.withinRange(opponent, 15)) {// maybe this will
			// cancel the damage
			// out on death.
			projectileDamage();
			if (opponent.isPlayer()) {
				if (Functions.isWielding((Player) opponent, ItemId.RING_OF_RECOIL.id())) {
					recoilDamage((Player) opponent, caster, damage);
				} else if (opponent.getSkills().getLevel(3) > 0) {
					if (((Player) opponent).checkRingOfLife(caster))
						return;
				}
			}
		}
		if (caster.isPlayer() && opponent.isPlayer()) {
			caster.removeAttribute("projectile");
			opponent.removeAttribute("projectile");
		}
	}

	private void recoilDamage(Player opponent, Mob caster, int damage) {
		int reflectedDamage = damage / 10 + ((damage > 0) ? 1 : 0);
		if (reflectedDamage == 0)
			return;

		if (opponent.getCache().hasKey("ringofrecoil")) {
			int ringCheck = opponent.getCache().getInt("ringofrecoil");
			if (getWorld().getServer().getConfig().RING_OF_RECOIL_LIMIT - ringCheck <= reflectedDamage) {
				reflectedDamage = getWorld().getServer().getConfig().RING_OF_RECOIL_LIMIT - ringCheck;
				opponent.getCache().remove("ringofrecoil");
				opponent.getInventory().shatter(ItemId.RING_OF_RECOIL.id());
			} else {
				opponent.getCache().set("ringofrecoil", ringCheck + reflectedDamage);
			}
		} else {
			opponent.getCache().put("ringofrecoil", reflectedDamage);
			opponent.message("You start a new ring of recoil");
		}

		caster.getSkills().subtractLevel(3, reflectedDamage, false);
		caster.getUpdateFlags().setDamage(new Damage(caster, reflectedDamage));

		if (caster.getSkills().getLevel(Skills.HITS) <= 0) {
			if (opponent.isPlayer()) {
				Player player = (Player) opponent;
				if (type == 2 || type == 5) {
					player.resetRange();
				}
			}
			if (caster.isNpc()) {
				if (caster.getWorld().getServer().getPluginHandler().blockDefaultAction("PlayerKilledNpc",
					new Object[]{(Player) opponent, (Npc) caster})) {
					return;
				}
			}
			caster.killedBy(opponent);
		} else {
			if (caster.isPlayer()) {
				((Player) caster).checkRingOfLife(opponent);
			}
		}
	}

	private void projectileDamage() {
		if (caster.isPlayer()) {
			if (opponent.isRemoved() && type == 2) {
				((Player) caster).resetRange();
			}
		}

		opponent.getSkills().subtractLevel(3, damage, false);
		opponent.getUpdateFlags().setDamage(new Damage(opponent, damage));


		if (caster.isPlayer()) {
			Player casterPlayer = (Player) caster;
			if (opponent.isNpc()) {
				Npc npcc = (Npc) opponent;
				if (type == 1 || type == 4)
					npcc.addMageDamage(casterPlayer, damage);
				else if (type == 2 || type == 5)
					npcc.addRangeDamage(casterPlayer, damage);
			}
		} else if (opponent.isPlayer()) {
			Player affectedPlayer = (Player) opponent;
			ActionSender.sendStat(affectedPlayer, 3);
			if (affectedPlayer.getWorld().getServer().getConfig().WANT_PARTIES) {
				if (affectedPlayer.getParty() != null) {
					affectedPlayer.getParty().sendParty();
				}
			}
		}
		if (opponent.getSkills().getLevel(Skills.HITS) <= 0) {
			if (caster.isPlayer()) {
				Player player = (Player) caster;
				if (type == 2 || type == 5) {
					player.resetRange();
				}
			}
			if (opponent.isNpc() && caster.isPlayer()) {
				Player playerCaster = (Player) caster;
				Npc npcOpponent = (Npc) opponent;
				if (caster.getWorld().getServer().getPluginHandler().blockDefaultAction("PlayerKilledNpc",
					new Object[]{playerCaster, npcOpponent})) {
					return;
				}
				npcOpponent.killedBy(playerCaster);
			} else {
				opponent.killedBy(caster);
			}
		} else {
			if (opponent.isNpc() && caster.isPlayer()) {
				Npc npc = (Npc) opponent;
				Player player = (Player) caster;
				npc.setChasing(player);
			}
		}
	}

	public void setCanceled(boolean b) {
		canceled = b;
	}

}
