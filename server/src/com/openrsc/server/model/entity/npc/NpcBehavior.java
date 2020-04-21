package com.openrsc.server.model.entity.npc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.impl.combat.AggroEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.CombatState;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;


public class NpcBehavior {

	private long lastMovement;
	private long lastTackleAttempt;
	private static final int[] TACKLING_XP = {7, 10, 15, 20};

	protected Npc npc;

	protected Mob target;
	private State state = State.ROAM;

	private boolean draynorManorSkeleton;
	private boolean blackKnightsFortress;

	NpcBehavior(final Npc npc) {
		this.npc = npc;
		this.blackKnightsFortress = npc.getLoc().startX() > 274 && npc.getLoc().startX() < 283
			&& npc.getLoc().startY() > 432 && npc.getLoc().startY() < 441;
		this.draynorManorSkeleton = npc.getID() == NpcId.SKELETON_LVL21.id()
			&& npc.getLoc().startX() >= 208 && npc.getLoc().startX() <= 211
			&& npc.getLoc().startY() >= 545 && npc.getLoc().startY() <= 546;
	}

	public void tick() {
		if (state == State.ROAM) {
			handleRoam();
		} else if (state == State.AGGRO) {
			handleAggro();
		} else if (state == State.COMBAT) {
			handleCombat();
		} else if (state == State.TACKLE) {
			handleTackle();
		} else if (state == State.RETREAT || state == State.TACKLE_RETREAT) {
			if (npc.finishedPath()) setRoaming();
		}
	}

	private void handleRoam() {
		Mob lastTarget;

		// NPC is in combat or busy, do not set them to ROAM.
		if (npc.inCombat()) {
			state = State.COMBAT;
			return;
		} else if (npc.isBusy()) {
			return;
		}

		// If NPC has not moved in 3 seconds, and is out of combat 3 seconds
		// and are finished our previous path.
		target = null;
		if (System.currentTimeMillis() - lastMovement > 3000
			&& System.currentTimeMillis() - npc.getCombatTimer() > 3000
			&& npc.finishedPath()) {
			lastMovement = System.currentTimeMillis();
			lastTarget = null;
			int rand = DataConversions.random(0, 1);

			// NPC is not busy, and we rolled to move (50% chance)
			if (!npc.isBusy() && rand == 1 && !npc.isRemoved() && !npc.getLocation().equals(new Point(0, 0))) {
				//Plagued sheep shouldn't roam
				if (npc.getID() == NpcId.FIRST_PLAGUE_SHEEP.id() ||
					npc.getID() == NpcId.SECOND_PLAGUE_SHEEP.id() ||
					npc.getID() == NpcId.THIRD_PLAGUE_SHEEP.id() ||
					npc.getID() == NpcId.FOURTH_PLAGUE_SHEEP.id()) {
					return;
				}
				Point p = npc.walkablePoint(Point.location(npc.getLoc().minX(), npc.getLoc().minY()),
					Point.location(npc.getLoc().maxX(), npc.getLoc().maxY()));
				npc.walk(p.getX(), p.getY());
			}
		}

		// NPC can aggro a target
		else if (System.currentTimeMillis() - npc.getCombatTimer() > 3000) {
			if ((npc.getDef().isAggressive() && !draynorManorSkeleton)
				|| npc.getLocation().inWilderness()
				|| (blackKnightsFortress)) {

				// We loop through all players in view.
				for (Player p : npc.getViewArea().getPlayersInView()) {

					int range = npc.getWorld().getServer().getConfig().AGGRO_RANGE;
					switch (NpcId.getById(npc.getID())) {
						case BANDIT_AGGRESSIVE:
							range = 2;
							break;
						case BLACK_KNIGHT:
							range = 10;
					}

					if (!canAggro(p) || !p.withinRange(npc, range))
						continue; // Can't aggro or is not in range.

					state = State.AGGRO;
					target = p;

					// Remove the opponent if the player has not been engaged in > 10 seconds
					if (npc.getLastOpponent() == p && (p.getLastOpponent() != npc || expiredLastTargetCombatTimer())) {
						npc.setLastOpponent(null);
						setRoaming();

					// AggroEvent, as NPC should target this player.
					} else {
						new AggroEvent(npc.getWorld(), npc, p);
					}

					break;
				}
			}
		}

		else if (System.currentTimeMillis() - lastTackleAttempt > 3000 &&
			npc.getDef().getName().toLowerCase().equals("gnome baller")
			&& !(npc.getID() == NpcId.GNOME_BALLER_TEAMNORTH.id() || npc.getID() == NpcId.GNOME_BALLER_TEAMSOUTH.id())) {
			for (Player p : npc.getViewArea().getPlayersInView()) {
				int range = 1;
				if (!p.withinRange(npc, range) || !p.getCarriedItems().hasCatalogID(ItemId.GNOME_BALL.id(), Optional.of(false))
					|| !inArray(p.getAttribute("gnomeball_npc", -1), -1, 0))
					continue; // Not in range, does not have a gnome ball or a gnome baller already has ball.

				//set tackle
				state = State.TACKLE;
				target = p;
			}
		}
	}

	private void handleAggro() {
		// There should not be combat or aggro. Let's resume roaming.
		if ((target == null || npc.isRespawning() || npc.isRemoved() || target.isRemoved()) && !npc.isFollowing()) {
			setRoaming();
		}

		// Target is not in range.
		else if (target.getX() < (npc.getLoc().minX() - 4) || target.getX() > (npc.getLoc().maxX() + 4)
			|| target.getY() < (npc.getLoc().minY() - 4) || target.getY() > (npc.getLoc().maxY() + 4)) {

			// Send the NPC back to its original spawn point.
			if (npc.getWorld().getServer().getConfig().WANT_IMPROVED_PATHFINDING) {
				Point origin = new Point(npc.getLoc().startX(), npc.getLoc().startY());
				npc.walkToEntityAStar(origin.getX(), origin.getY());
				npc.getSkills().normalize();
				npc.cure();
			}
			setRoaming();
		}

		// Combat with another target - set state.
		else {

			// Reset the target if the wrong one is focused
			if (npc.inCombat() && npc.getOpponent() != target) {
				npc.setLastOpponent(null);
				target = npc.getOpponent();
				state = State.COMBAT;
			}

			// If target is not waiting for "run away" timer, send them chasing
			lastMovement = System.currentTimeMillis();
			if (!checkTargetCombatTimer()) {
				if (npc.getWorld().getServer().getConfig().WANT_IMPROVED_PATHFINDING)
					npc.walkToEntityAStar(target.getX(), target.getY());
				else
					npc.walkToEntity(target.getX(), target.getY());

				// Fight the target when in range
				if (npc.withinRange(target, 1)
					&& npc.canReach(target)
					&& !target.inCombat()) {
					setFighting(target);
				}
			}
		}
	}

	private void handleCombat() {
		Mob lastTarget = target;
		target = npc.getOpponent();

		// No target, return to roaming.
		if (target == null || npc.isRemoved() || target.isRemoved()) {
			setRoaming();
		}

		// Current NPC is in combat
		else if (npc.inCombat()) {

			// Retreat if NPC hits remaining and > round 3
			if (shouldRetreat(npc) && npc.getSkills().getLevel(Skills.HITS) > 0
				&& npc.getOpponent().getHitsMade() >= 3) {
				retreat();
			}

		// NPC is not in combat
		} else if (!npc.inCombat()) {
			npc.setExecutedAggroScript(false);

			// If there is a valid target and NPC is aggressive, set AGGRO and target.
			if (npc.getDef().isAggressive() &&
				(lastTarget != null &&
					(lastTarget.getCombatLevel() < ((npc.getNPCCombatLevel() * 2) + 1) ||
						(lastTarget.getLocation().inWilderness() && npc.getLocation().inWilderness()))
				)) {
				state = State.AGGRO;
				if (lastTarget != null)
					target = lastTarget;

			// Otherwise, set roaming if NPC is not already following something
			} else {
				if (!npc.isFollowing())
					setRoaming();
			}
		}
	}

	private void handleTackle() {
		// There should not be tackle. Let's resume roaming.
		if (target == null || npc.isRespawning() || npc.isRemoved() || target.isRemoved() || target.inCombat() || target.isBusy()) {
			setRoaming();
		}
		// Target is not in range.
		else if (target.getX() < (npc.getLoc().minX() - 4) || target.getX() > (npc.getLoc().maxX() + 4)
			|| target.getY() < (npc.getLoc().minY() - 4) || target.getY() > (npc.getLoc().maxY() + 4)) {
			setRoaming();
		}
		if (target.isPlayer()) {
			attemptTackle(npc, (Player) target);
			tackle_retreat();
		}
	}

	private synchronized void attemptTackle(final Npc n, final Player p) {
		int otherNpcId = p.getAttribute("gnomeball_npc", -1);
		if ((!inArray(otherNpcId, -1, 0) && npc.getID() != otherNpcId) || p.getAttribute("throwing_ball_game", false)) {
			return;
		}
		lastTackleAttempt = System.currentTimeMillis();
		thinkbubble(p, new Item(ItemId.GNOME_BALL.id()));
		p.message("the gnome trys to tackle you");
		if (DataConversions.random(0, 1) == 0) {
			//successful avoiding tackles gives agility xp
			p.playerServerMessage(MessageType.QUEST, "You manage to push him away");
			npcYell(p, npc, "grrrrr");
			p.incExp(Skills.AGILITY, TACKLING_XP[DataConversions.random(0, 3)], true);
		} else {
			if (!inArray(p.getAttribute("gnomeball_npc", -1), -1, 0) || p.getAttribute("throwing_ball_game", false)) {
				// some other gnome beat here or player is shooting at goal
				return;
			}
			p.setAttribute("gnomeball_npc", npc.getID());
			p.getCarriedItems().remove(new Item(ItemId.GNOME_BALL.id()));
			p.playerServerMessage(MessageType.QUEST, "he takes the ball...");
			p.playerServerMessage(MessageType.QUEST, "and pushes you to the floor");
			p.damage((int) (Math.ceil(p.getSkills().getLevel(Skills.HITS) * 0.05)));
			say(p, null, "ouch");
			npcYell(p, npc, "yeah");
		}
	}

	public void retreat() {
		state = State.RETREAT;
		npc.getOpponent().setLastOpponent(npc);
		npc.setLastOpponent(npc.getOpponent());
		npc.setRanAwayTimer();
		if (npc.getOpponent().isPlayer()) {
			Player victimPlayer = ((Player) npc.getOpponent());
			victimPlayer.resetAll();
			victimPlayer.message("Your opponent is retreating");
			ActionSender.sendSound(victimPlayer, "retreat");
		}
		npc.setLastCombatState(CombatState.RUNNING);
		npc.setLastCombatState(CombatState.RUNNING);
		npc.getOpponent().setLastCombatState(CombatState.WAITING);
		npc.resetCombatEvent();

		Point walkTo = Point.location(DataConversions.random(npc.getLoc().minX(), npc.getLoc().maxX()),
			DataConversions.random(npc.getLoc().minY(), npc.getLoc().maxY()));
		npc.walk(walkTo.getX(), walkTo.getY());
	}

	private void tackle_retreat() {
		state = State.TACKLE_RETREAT;
		npc.setLastCombatState(CombatState.RUNNING);
		target.setLastCombatState(CombatState.WAITING);
		npc.resetCombatEvent();

		Point walkTo = Point.location(DataConversions.random(npc.getLoc().minX(), npc.getLoc().maxX()),
			DataConversions.random(npc.getLoc().minY(), npc.getLoc().maxY()));
		npc.walk(walkTo.getX(), walkTo.getY());
	}

	private boolean shouldContinueChase(final Npc n, final Mob p) {
		return p.getLocation().inWilderness()
			|| (!p.getLocation().inWilderness() && !npc.getLocation().inWilderness() &&
			p.getCombatLevel() < ((npc.getNPCCombatLevel() * 2) + 1));
	}

	private boolean canAggro(final Mob p) {
		boolean outOfBounds = !p.getLocation().inBounds(npc.getLoc().minX - 4, npc.getLoc().minY - 4,
			npc.getLoc().maxX + 4, npc.getLoc().maxY + 4);

		boolean playerOccupied = p.inCombat();
		boolean playerCombatTimeout = System.currentTimeMillis()
			- p.getCombatTimer() < (p.getCombatState() == CombatState.RUNNING
			|| p.getCombatState() == CombatState.WAITING ? 3000 : 1500);

		boolean shouldAttack = (npc.getDef().isAggressive() && (p.getCombatLevel() < ((npc.getNPCCombatLevel() * 2) + 1)
			|| (p.getLocation().inWilderness() && npc.getLocation().inWilderness())))
			|| (npc.getLastOpponent() == p && shouldContinueChase(npc, p) && !shouldRetreat(npc));

		boolean closeEnough = npc.canReach(p);

		return closeEnough && shouldAttack
			&& (p instanceof Player && (!((Player) p).isInvulnerableTo(npc) && !((Player) p).isInvisibleTo(npc)))
			&& !outOfBounds && !playerOccupied && !playerCombatTimeout;
	}

	private boolean grandTreeGnome(final Npc npc) {
		String npcName = npc.getDef().getName();
		return npcName.equalsIgnoreCase("gnome child") || npcName.equalsIgnoreCase("gnome local");
	}

	public State getBehaviorState() {
		return state;
	}

	boolean isChasing() {
		return state == State.AGGRO;
	}

	public void setChasing(final Player player) {
		state = State.AGGRO;
		target = player;
	}

	public void setChasing(final Npc npc) {
		state = State.AGGRO;
		target = npc;
	}

	Player getChasedPlayer() {
		if (target.isPlayer())
			return (Player) target;
		return null;
	}

	Npc getChasedNpc() {
		if (target.isNpc())
			return (Npc) target;
		return null;
	}

	private boolean checkTargetCombatTimer() {
		return (System.currentTimeMillis() - target.getCombatTimer()
			< (target.getCombatState() == CombatState.RUNNING
			|| target.getCombatState() == CombatState.WAITING ? 3000 : 1500)
		);
	}

	private boolean expiredLastTargetCombatTimer() {
		return (System.currentTimeMillis() - npc.getLastOpponent().getRanAwayTimer() > 10000);
	}

	public Mob getChaseTarget() {
		return target;
	}

	public void setRoaming() {
		npc.setExecutedAggroScript(false);
		state = State.ROAM;
	}

	private void setFighting(final Mob target) {
		npc.startCombat(target);
		state = State.COMBAT;
	}

	private boolean shouldRetreat(final Npc npc) {
		if (!npc.getWorld().getServer().getConfig().NPC_DONT_RETREAT) {
			if (npc.getWorld().getServer().getConstants().getRetreats().npcData.containsKey(npc.getID())) {
				return npc.getSkills().getLevel(Skills.HITS) <= npc.getWorld().getServer().getConstants().getRetreats().npcData.get(npc.getID());
			}
		}

		return false;
	}

	enum State {
		ROAM, AGGRO, COMBAT, RETREAT, TACKLE, TACKLE_RETREAT;
	}
}
