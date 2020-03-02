package com.openrsc.server.model.entity.npc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.rsc.impl.HealEventNpc;
import com.openrsc.server.event.rsc.impl.RangeEventNpc;
import com.openrsc.server.event.rsc.impl.combat.AggroEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.CombatState;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

// TODO: Very dirty class. It doesn't reuse a lot of code from NpcBehaviour.
// TODO: The PKBot specific behaviour needs to be a logic function in NpcBehaviour
public class PkBotBehavior extends NpcBehavior {

	private long lastMovement;
	private long lastRetreat;
	private long lastTackleAttempt;
	private static final int[] TACKLING_XP = {7, 10, 15, 20};

	protected PkBot npc;

	protected Mob target;
	private State state = State.ROAM;

	PkBotBehavior(final PkBot npc) {
		super(npc);
		this.npc = npc;
	}

	public void tick() {
		Mob lastTarget;
		if (npc.isPkBot() && npc.getWorld().getServer().getConfig().WANT_PK_BOTS) {
			for (Player p5 : npc.getViewArea().getPlayersInView()) {
				int combDiff2 = Math.abs(npc.getCombatLevel() - p5.getCombatLevel());
				int targetWildLvl2 = p5.getLocation().wildernessLevel();
				int myWildLvl2 = npc.getLocation().wildernessLevel();
				final Point objectLocation = Point.location(102, 509);
				final Point byDoor = Point.location(103, 509);
				final Point byDoor2 = Point.location(102, 509);
				final Point byDoor3 = Point.location(103, 510);
				final Point byDoor4 = Point.location(102, 510);
				final GameObject object = npc.getViewArea().getGameObject(objectLocation);
				if (npc.getHeals() < 5 && npc.getLocation().equals(byDoor) || npc.getLocation().equals(byDoor2) && object.getID() == 64) {
					replaceObject(object, new GameObject(object.getWorld(), object.getLocation(), 63, object.getDirection(), object.getType(), null));
					npc.walkToEntityAStar(103, 510, 200);
				}
				if (npc.getHeals() > 17 && npc.getLocation().equals(byDoor3) || npc.getLocation().equals(byDoor4) && object.getID() == 64) {
					replaceObject(object, new GameObject(object.getWorld(), object.getLocation(), 63, object.getDirection(), object.getType(), null));
					npc.getWorld().getServer().getGameEventHandler().add(new DelayedEvent(npc.getWorld(), ((Player) null), 1500, "Npc walk back to wild2") {
						public void run() {
							npc.walkToEntityAStar(108, 425, 200);
							stop();
						}
					});
				}
				if (npc.inCombat()) {
					npc.setWield(51);
					npc.setWield2(80);
				}
				if (npc.getHeals() < 1 && !npc.inCombat() && npc.getLocation().inWilderness()) {
					retreatFromWild3();
				}
				if (npc.getHeals() > 1 && !npc.getLocation().inWilderness()) {
					npc.walkToEntityAStar(214, 424, 200);
				}
				if (npc.getHeals() < 1 && npc.inCombat() && npc.getHitsMade() >= 3 && npc.getOpponent().getHitsMade() >= 3 && npc.getLocation().inWilderness()) {
					retreatFromWild();
				}
				if (npc.getHeals() > 0 && p5.getLocation().inBounds(npc.getLoc().minX - 9, npc.getLoc().minY - 9,
					npc.getLoc().maxX + 9, npc.getLoc().maxY + 9) && System.currentTimeMillis() - p5.getCombatTimer() > 1000 && System.currentTimeMillis() - npc.getCombatTimer() > 1000 && !npc.inCombat() && p5.getLocation().inWilderness() && combDiff2 < targetWildLvl2 && combDiff2 < myWildLvl2 && npc.withinRange(p5, 10) && npc.getLocation().inWilderness() && npc.getID() == 804) {
					/*if (npc.nextStep(npc.getX(), npc.getY(), p5) == null) {
						npc.walkToEntityAStar2(p5.getX(), p5.getY());
						npc.resetRange();
					} else */if (!p5.inCombat()) {
						target = p5;
						npc.setWield(51);
						npc.setWield2(80);
						npc.resetRange();
					} else if (p5.inCombat()) {
						target = p5;
						npc.setRangeEventNpc(new RangeEventNpc(npc.getWorld(), npc, target));
						npc.setWield(252);
						npc.setWield2(80);
						npc.setSkulledOn(((Player) target));
					}
				} else if (!p5.getLocation().inBounds(npc.getLoc().minX - 9, npc.getLoc().minY - 9,
					npc.getLoc().maxX + 9, npc.getLoc().maxY + 9) || !p5.getLocation().inWilderness()) {
					target = null;
				}
				if (npc.inCombat() && npc.getOpponent().getHitsMade() >= 3 && npc.getSkills().getLevel(Skills.HITPOINTS) < npc.getOpponent().getSkills().getLevel(Skills.HITPOINTS)) {
					if (npc.getHeals() > 0) {
						retreat();
						lastRetreat = System.currentTimeMillis();
						npc.getWorld().getServer().getGameEventHandler().add(new HealEventNpc(npc.getWorld(), npc));
					} else if (npc.getLocation().inWilderness()) {
						retreatFromWild();
						lastRetreat = System.currentTimeMillis();
					}
				}
				if (npc.inCombat() && npc.getOpponent().getHitsMade() >= 3 && npc.getSkills().getLevel(Skills.HITPOINTS) < npc.getSkills().getMaxStat(Skills.HITPOINTS) * 0.33) {
					if (npc.getHeals() > 0) {
						retreat();
						//npc.setWield(252);
						//npc.setWield2(80);
						lastRetreat = System.currentTimeMillis();
						npc.getWorld().getServer().getGameEventHandler().add(new HealEventNpc(npc.getWorld(), npc));
					} else if (npc.getLocation().inWilderness()) {
						retreatFromWild();
						lastRetreat = System.currentTimeMillis();
					}
				}
				if (npc.getHeals() < 1 && !npc.inCombat() && npc.getSkills().getLevel(Skills.HITPOINTS) < npc.getSkills().getMaxStat(Skills.HITPOINTS) * 0.75) {
					if (npc.getLocation().inWilderness()) {
						retreatFromWild3();
					}
				}
				if (target == null && !npc.isRanging()) {
					if (!npc.inCombat() && System.currentTimeMillis() - lastMovement > 900 && System.currentTimeMillis() - npc.getCombatTimer() > 900 && npc.finishedPath()) {
						lastMovement = System.currentTimeMillis();
						lastTarget = null;
						int rand = DataConversions.random(0, 1);
						if (!npc.isBusy() && rand == 1 && !npc.isRemoved() && npc.getHeals() > 0) {
							int newX = DataConversions.random(npc.getLoc().minX(), npc.getLoc().maxX());
							int newY = DataConversions.random(npc.getLoc().minY(), npc.getLoc().maxY());
							if (npc.getLocation().equals(new Point(0, 0))) {
								npc.walkToEntityAStar(newX, newY, 200);
								npc.setWield(252);
								npc.setWield2(80);
							} else {
								Point p = npc.walkablePoint(Point.location(npc.getLoc().minX(), npc.getLoc().minY()),
									Point.location(npc.getLoc().maxX(), npc.getLoc().maxY()));
								npc.walk(p.getX(), p.getY());
							}
						}
					}
				} else
					for (Player p3 : npc.getViewArea().getPlayersInView()) {
						if (npc.inCombat() && npc.getID() == NpcId.PKBOT1.id()) {
							target = npc.getOpponent();
							if (npc.getHeals() > 0 && npc.getLocation().inWilderness() && target.getLocation().inWilderness()) {
								npc.walkToEntityAStar(target.getX(), target.getY(), 200);
							}
						}
						if (npc.getLocation().inWilderness() && npc.getID() == NpcId.PKBOT1.id() && npc.getSkills().getLevel(Skills.HITPOINTS) < npc.getSkills().getMaxStat(Skills.HITPOINTS) * 0.82) {
							if (npc.getHeals() > 0) {
								npc.getWorld().getServer().getGameEventHandler().add(new HealEventNpc(npc.getWorld(), npc));
							}
						}
						int combDiff = Math.abs(npc.getCombatLevel() - target.getCombatLevel());
						int targetWildLvl = target.getLocation().wildernessLevel();
						int myWildLvl = npc.getLocation().wildernessLevel();
						if (System.currentTimeMillis() - target.getCombatTimer() > 3250 && System.currentTimeMillis() - npc.getCombatTimer() > 3250 && !npc.inCombat() && !target.inCombat() && combDiff < targetWildLvl && combDiff < myWildLvl && npc.withinRange(target, 10) && npc.getLocation().inWilderness() && target.getLocation().inWilderness() && npc.getID() == 804) {
							if (npc.withinRange(target, 2) && System.currentTimeMillis() - target.getCombatTimer() > 3250 && System.currentTimeMillis() - npc.getCombatTimer() > 3250 && !npc.inCombat() && !target.inCombat() && combDiff < targetWildLvl && combDiff < myWildLvl && npc.getLocation().inWilderness() && target.getLocation().inWilderness() && target != null && !npc.isRespawning() && !npc.isRemoved() && !target.isRemoved()) {
								if (npc.nextStep(npc.getX(), npc.getY(), p5) == null) {
								} else {
									setFighting(target);
									npc.setWield(51);
									npc.setWield2(80);
									npc.setSkulledOn(((Player) target));
									//showBubbleNpc(npc, new Item(373));
								}
							}
						}
						if (System.currentTimeMillis() - npc.getCombatTimer() > 1000 && System.currentTimeMillis() - target.getCombatTimer() > 1000 && !target.inCombat() && combDiff < targetWildLvl && combDiff < myWildLvl && npc.withinRange(target, 10) && npc.getLocation().inWilderness() && target.getLocation().inWilderness() && npc.getID() == 804) {
							if (npc.getHeals() > 0 && System.currentTimeMillis() - npc.getCombatTimer() > 1000 && System.currentTimeMillis() - target.getCombatTimer() > 1000 && !npc.inCombat() && !target.inCombat() && System.currentTimeMillis() - lastRetreat > 3250 && combDiff < targetWildLvl && combDiff < myWildLvl && npc.getLocation().inWilderness() && target.getLocation().inWilderness() && target != null && !npc.isRespawning() && !npc.isRemoved() && !target.isRemoved()) {
								npc.walkToEntityAStar(target.getX(), target.getY(), 200);
								npc.resetRange();
							} else {
								setRoaming();
								return;
							}
						} else if (npc.inCombat() && (target == null || npc.isRespawning() || npc.isRemoved() || target.isRemoved() || combDiff > targetWildLvl || combDiff > myWildLvl || !npc.getLocation().inWilderness())) {
							setRoaming();
						} else
							// Target is not in range.
							if (System.currentTimeMillis() - target.getCombatTimer() > 3000 && System.currentTimeMillis() - npc.getCombatTimer() > 3000 && !npc.inCombat() && !target.inCombat() && npc.getID() == 804 && (combDiff > targetWildLvl || combDiff > myWildLvl || !npc.getLocation().inWilderness() || target.getX() < (npc.getLoc().minX() - 4) || target.getX() > (npc.getLoc().maxX() + 4) || target.getY() < (npc.getLoc().minY() - 4) || target.getY() > (npc.getLoc().maxY() + 4))) {
								setRoaming();
							} else if (!target.getLocation().inWilderness() && npc.getID() == 804) {
								for (Player p : npc.getViewArea().getPlayersInView()) {
									if (p.getLocation().inWilderness()) {
										target = p;
									} else
										target = null;
								}
							}
					}
			}
		} else if (state == State.ROAM) {

			if (npc.inCombat()) {
				state = State.COMBAT;
				return;
			} else if (npc.isBusy()) {
				return;
			}

			target = null;
			if (System.currentTimeMillis() - lastMovement > 3000
				&& System.currentTimeMillis() - npc.getCombatTimer() > 3000
				&& npc.finishedPath()) {
				lastMovement = System.currentTimeMillis();
				lastTarget = null;
				int rand = DataConversions.random(0, 1);
				if (!npc.isBusy() && rand == 1 && !npc.isRemoved()) {
					int newX = DataConversions.random(npc.getLoc().minX(), npc.getLoc().maxX());
					int newY = DataConversions.random(npc.getLoc().minY(), npc.getLoc().maxY());
					if (npc.getLocation().equals(new Point(0, 0))) {
						npc.walk(newX, newY);
					} else {
						Point p = npc.walkablePoint(Point.location(npc.getLoc().minX(), npc.getLoc().minY()),
							Point.location(npc.getLoc().maxX(), npc.getLoc().maxY()));
						npc.walk(p.getX(), p.getY());
					}
				}
			}
			if (System.currentTimeMillis() - npc.getCombatTimer() > 3000
				&& ((npc.getDef().isAggressive()
				&& !(npc.getID() == NpcId.SKELETON_LVL21.id() && npc.getX() >= 208 && npc.getX() <= 211 && npc.getY() >= 545 && npc.getY() <= 546)) // Skeleton in draynor manor
				|| (npc.getLocation().inWilderness()))
				|| (npc.getX() > 274 && npc.getX() < 283 && npc.getY() > 432 && npc.getY() < 441) // Black Knight's Fortress
			) {

				// We loop through all players in view.
				for (Player p : npc.getViewArea().getPlayersInView()) {

					int range = npc.getWorld().getServer().getConfig().AGGRO_RANGE;
					switch (NpcId.getById(npc.getID())) {
						case BANDIT_AGGRESSIVE:
							range = 5;
							break;
						case BLACK_KNIGHT:
							range = 10;
							break;
						default:
							break;
					}

					if (!canAggro(p) || !p.withinRange(npc, range))
						continue; // Can't aggro or is not in range.

					state = State.AGGRO;
					target = p;

					if (npc.getLastOpponent() == p && (p.getLastOpponent() != npc || expiredLastTargetCombatTimer())) {
						npc.setLastOpponent(null);
						setRoaming();
					} else {
						//aggro behavior if any
						new AggroEvent(npc.getWorld(), npc, p);
					}

					break;
				}
				for (Npc pkBot : npc.getViewArea().getNpcsInView()) {

					int range = npc.getWorld().getServer().getConfig().AGGRO_RANGE;
					switch (NpcId.getById(npc.getID())) {
						case BANDIT_AGGRESSIVE:
							range = 5;
							break;
						case BLACK_KNIGHT:
							range = 10;
							break;
						default:
							break;
					}

					if (!canAggro28(pkBot) || !pkBot.withinRange(npc, range) || !pkBot.isPkBot())
						continue; // Can't aggro or is not in range.

					state = State.AGGRO;
					target = pkBot;

					if (npc.getLastOpponent() == pkBot && (pkBot.getLastOpponent() != npc || expiredLastTargetCombatTimer())) {
						npc.setLastOpponent(null);
						setRoaming();
					} else {
						//aggro behavior if any
						new AggroEvent(npc.getWorld(), npc, pkBot);
					}

					break;
				}
			}
			if (System.currentTimeMillis() - lastTackleAttempt > 3000 &&
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
		} else if (state == State.AGGRO) {

			// There should not be combat or aggro. Let's resume roaming.
			if ((target == null || npc.isRespawning() || npc.isRemoved() || target.isRemoved() || (!npc.isPkBot() && target.inCombat())) && !npc.isFollowing()) {
				setRoaming();
			}

			// Target is not in range.
			else if (target.getX() < (npc.getLoc().minX() - 4) || target.getX() > (npc.getLoc().maxX() + 4)
				|| target.getY() < (npc.getLoc().minY() - 4) || target.getY() > (npc.getLoc().maxY() + 4)) {
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
				if (npc.inCombat() && npc.getOpponent() != target) {
					npc.setLastOpponent(null);
					target = npc.getOpponent();
					state = State.COMBAT;
				}

				lastMovement = System.currentTimeMillis();
				if (!checkTargetCombatTimer()) {
					if (npc.getWorld().getServer().getConfig().WANT_IMPROVED_PATHFINDING)
						npc.walkToEntityAStar(target.getX(), target.getY());
					else
						npc.walkToEntity(target.getX(), target.getY());

					if (npc.withinRange(target, 1)
						&& npc.canReach(target)
						&& !target.inCombat() && (!npc.isPkBot())) {
						setFighting(target);
					}
				}
			}

		} else if (state == State.COMBAT) {
			lastTarget = target;
			target = npc.getOpponent();
			if (target == null || npc.isRemoved() || target.isRemoved()) {
				setRoaming();
			}
			if (npc.inCombat()) {
				if (shouldRetreat(npc) && npc.getSkills().getLevel(Skills.HITS) > 0
					&& npc.getOpponent().getHitsMade() >= 3) {
					retreat();
					lastRetreat = System.currentTimeMillis();
				}
			} else if (!npc.inCombat()) {
				npc.setExecutedAggroScript(false);
				if (npc.getDef().isAggressive() &&
					((lastTarget != null &&
						lastTarget.getCombatLevel() < ((npc.getNPCCombatLevel() * 2) + 1)) ||
						npc.getLocation().inWilderness())
				) {
					state = State.AGGRO;
					if (lastTarget != null)
						target = lastTarget;
				} else {
					if (!npc.isFollowing())
						setRoaming();
				}
			}

		} else if (state == State.TACKLE) {
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
		} else if (state == State.RETREAT || state == State.TACKLE_RETREAT) {
			if (npc.finishedPath()) setRoaming();
		}
	}

	private synchronized void attemptTackle(Npc n, Player p) {
		int otherNpcId = p.getAttribute("gnomeball_npc", -1);
		if ((!inArray(otherNpcId, -1, 0) && npc.getID() != otherNpcId) || p.getAttribute("throwing_ball_game", false)) {
			return;
		}
		lastTackleAttempt = System.currentTimeMillis();
		showBubble(p, new Item(ItemId.GNOME_BALL.id()));
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
			removeItem(p, ItemId.GNOME_BALL.id(), 1);
			p.playerServerMessage(MessageType.QUEST, "he takes the ball...");
			p.playerServerMessage(MessageType.QUEST, "and pushes you to the floor");
			p.damage((int) (Math.ceil(p.getSkills().getLevel(Skills.HITS) * 0.05)));
			playerTalk(p, null, "ouch");
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
		if (!npc.isPkBotMelee()) {
			npc.setLastCombatState(CombatState.RUNNING);
		}
		npc.setLastCombatState(CombatState.RUNNING);
		npc.getOpponent().setLastCombatState(CombatState.WAITING);
		npc.resetCombatEvent();

		Point walkTo = Point.location(DataConversions.random(npc.getLoc().minX(), npc.getLoc().maxX()),
			DataConversions.random(npc.getLoc().minY(), npc.getLoc().maxY()));
		npc.walk(walkTo.getX(), walkTo.getY());
	}

	public void retreatFromWild() {
		if (npc.getLocation().inWilderness()) {
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
			if (!npc.isPkBotMelee()) {
				npc.setLastCombatState(CombatState.RUNNING);
			}
			npc.setLastCombatState(CombatState.RUNNING);
			npc.getOpponent().setLastCombatState(CombatState.WAITING);
			npc.resetCombatEvent();

			Point walkTo = Point.location(DataConversions.random(101, 114),
				DataConversions.random(427, 428));
			npc.walkToEntityAStar(walkTo.getX(), walkTo.getY(), 200);
		}
	}

	public void retreatFromWild2() {
		if (npc.getLocation().inWilderness()) {
			state = State.RETREAT;
			//npc.walk(walkTo.getX(), walkTo.getY());
			npc.walkToEntityAStar(103, 510, 200);//this is causing teleport
			npc.getWorld().getServer().getGameEventHandler().add(new DelayedEvent(npc.getWorld(), ((Player) null), 80000, "Npc walk back to wild") {
				public void run() {
					npc.walkToEntityAStar(108, 425, 200);
					npc.setHeals(25);
					stop();
				}
			});
			lastMovement = System.currentTimeMillis();
		}
	}

	public void retreatFromWild3() {
		if (npc.getLocation().inWilderness()) {
			state = State.RETREAT;
			npc.walkToEntityAStar(218, 447, 200);
			npc.getWorld().getServer().getGameEventHandler().add(new DelayedEvent(npc.getWorld(), ((Player) null), 50000, "Npc walk back to wild") {
				public void run() {
					npc.walkToEntityAStar(214, 424, 200);
					npc.setHeals(25);
					stop();
				}
			});
			lastMovement = System.currentTimeMillis();
		}
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

	private boolean canAggro(Mob p) {
		boolean outOfBounds = !p.getLocation().inBounds(npc.getLoc().minX - 4, npc.getLoc().minY - 4,
			npc.getLoc().maxX + 4, npc.getLoc().maxY + 4);

		boolean playerOccupied = p.inCombat();
		boolean playerCombatTimeout = System.currentTimeMillis()
			- p.getCombatTimer() < (p.getCombatState() == CombatState.RUNNING
			|| p.getCombatState() == CombatState.WAITING ? 3000 : 1500);

		boolean shouldAttack = (npc.getDef().isAggressive() && (p.getCombatLevel() < ((npc.getNPCCombatLevel() * 2) + 1)
			|| npc.getLocation().inWilderness())) || (npc.getLastOpponent() == p && !shouldRetreat(npc));

		boolean closeEnough = npc.canReach(p);

		return closeEnough && shouldAttack
			&& (p instanceof Player && (!((Player) p).isInvulnerableTo(npc) && !((Player) p).isInvisibleTo(npc)))
			&& !outOfBounds && !playerOccupied && !playerCombatTimeout;
	}

	private boolean canAggro28(final Mob p) {
		boolean outOfBounds = !p.getLocation().inBounds(npc.getLoc().minX - 4, npc.getLoc().minY - 4,
			npc.getLoc().maxX + 4, npc.getLoc().maxY + 4);

		boolean playerOccupied = p.inCombat();
		boolean playerCombatTimeout = System.currentTimeMillis()
			- p.getCombatTimer() < (p.getCombatState() == CombatState.RUNNING
			|| p.getCombatState() == CombatState.WAITING ? 3000 : 1500);

		boolean shouldAttack = (npc.getDef().isAggressive() && (p.getCombatLevel() < ((npc.getNPCCombatLevel() * 2) + 1)
			|| npc.getLocation().inWilderness())) || (npc.getLastOpponent() == p && !shouldRetreat(npc));

		boolean closeEnough = npc.canReach(p);

		return closeEnough && shouldAttack
			&& (p instanceof Npc) && !outOfBounds && !playerOccupied && !playerCombatTimeout;
	}

	private boolean shouldRetreat(final PkBot npc) {
		if (npc.isPkBotMelee()) {
			return npc.getSkills().getLevel(Skills.HITS) <= Math.ceil(npc.getSkills().getMaxStat(Skills.HITS) * 0.20);
		}
		return false;
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

	enum State {
		ROAM, AGGRO, COMBAT, RETREAT, TACKLE, TACKLE_RETREAT;
	}
}
