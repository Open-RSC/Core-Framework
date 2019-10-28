package com.openrsc.server.plugins.skills.agility;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.Formulae;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.openrsc.server.plugins.Functions.*;

public class WildernessAgilityCourse implements ObjectActionListener,
	ObjectActionExecutiveListener {

	private static final int GATE = 703;
	private static final int SECOND_GATE = 704;
	private static final int WILD_PIPE = 705;
	private static final int WILD_ROPESWING = 706;
	private static final int STONE = 707;
	private static final int LEDGE = 708;
	private static final int VINE = 709;

	private static Set<Integer> obstacles = new HashSet<Integer>(Arrays.asList(WILD_PIPE, WILD_ROPESWING, STONE, LEDGE));
	private static Integer lastObstacle = VINE;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return inArray(obj.getID(), GATE, SECOND_GATE, WILD_PIPE, WILD_ROPESWING, STONE, LEDGE, VINE);
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		final int failRate = failRate();
		if (obj.getID() == GATE) {
			if (getCurrentLevel(p, Skills.AGILITY) < 52) {
				p.message("You need an agility level of 52 to attempt balancing along the ridge");
				return;
			}
			p.setBusy(true);
			p.message("You go through the gate and try to edge over the ridge");
			p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Wilderness Agility Gate") {
				public void init() {
					addState(0, () -> {
						movePlayer(getPlayerOwner(), 298, 130);
						return nextState(2);
					});
					addState(1, () -> {
						if (failRate == 1) {
							message(getPlayerOwner(), "you lose your footing and fall into the wolf pit");
							movePlayer(getPlayerOwner(), 300, 129);
						} else if (failRate == 2) {
							message(getPlayerOwner(), "you lose your footing and fall into the wolf pit");
							movePlayer(getPlayerOwner(), 296, 129);
						} else {
							message(getPlayerOwner(), "You skillfully balance across the ridge");
							movePlayer(getPlayerOwner(), 298, 125);
							getPlayerOwner().incExp(Skills.AGILITY, 50, true);
						}
						getPlayerOwner().setBusy(false);
						return null;
					});
				}
			});
			return;
		} else if (obj.getID() == SECOND_GATE) {
			p.message("You go through the gate and try to edge over the ridge");
			p.setBusy(true);
			p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Wilderness Agility Gate") {
				public void init() {
					addState(0, () -> {
						movePlayer(getPlayerOwner(), 298, 130);
						return nextState(2);
					});
					addState(1, () -> {
						if (failRate == 1) {
							message(getPlayerOwner(), "you lose your footing and fall into the wolf pit");
							movePlayer(getPlayerOwner(), 300, 129);

						} else if (failRate == 2) {
							message(getPlayerOwner(), "you lose your footing and fall into the wolf pit");
							movePlayer(getPlayerOwner(), 296, 129);
						} else {
							message(getPlayerOwner(), "You skillfully balance across the ridge");
							movePlayer(getPlayerOwner(), 298, 134);
							getPlayerOwner().incExp(Skills.AGILITY, 50, true);
						}
						getPlayerOwner().setBusy(false);
						return null;
					});
				}
			});
			return;
		}
		if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
			if (p.getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 1
				&& p.getFatigue() >= p.MAX_FATIGUE && !inArray(obj.getID(), VINE)) {
				p.message("you are too tired to train");
				return;
			}
		}
		p.setBusy(true);
		boolean passObstacle = succeed(p);
		switch (obj.getID()) {
			case WILD_PIPE:
				p.message("You squeeze through the pipe");
				p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Wilderness Agility Gate") {
					public void init() {
						addState(0, () -> {
							movePlayer(getPlayerOwner(), 294, 112);
							getPlayerOwner().incExp(Skills.AGILITY, 50, true);
							AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), obstacles, lastObstacle, 1500);
							getPlayerOwner().setBusy(false);
							return null;
						});
					}
				});
				return;
			case WILD_ROPESWING:
				p.message("You grab the rope and try and swing across");
				p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Wilderness Agility Rope") {
					public void init() {
						addState(0, () -> {
							if (passObstacle) {
								message(getPlayerOwner(), "You skillfully swing across the hole");
								movePlayer(getPlayerOwner(), 292, 108);
								getPlayerOwner().incExp(Skills.AGILITY, 100, true);
								AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), obstacles, lastObstacle, 1500);
								getPlayerOwner().setBusy(false);
								return null;
							} else { // 13 damage on 85hp.
								// 11 damage on 73hp.
								//
								getPlayerOwner().message("Your hands slip and you fall to the level below");
								return nextState(2);
							}
						});
						addState(1, () -> {
							int damage = (int) Math.round((getPlayerOwner().getSkills().getLevel(Skills.HITS)) * 0.15D);
							movePlayer(getPlayerOwner(), 293, 2942);
							getPlayerOwner().message("You land painfully on the spikes");
							playerTalk(getPlayerOwner(), null, "ouch");
							getPlayerOwner().damage(damage);
							getPlayerOwner().setBusy(false);
							return null;
						});
					}
				});
				return;
			case STONE:
				p.message("you stand on the stepping stones");
				p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Wilderness Agility Stones") {
					public void init() {
						addState(0, () -> {
							if (passObstacle) {
								movePlayer(getPlayerOwner(), 293, 105);
								return nextState(1);
							} else {
								getPlayerOwner().message("Your lose your footing and land in the lava");
								movePlayer(getPlayerOwner(), 292, 104);
								int lavaDamage = (int) Math.round((p.getSkills().getLevel(Skills.HITS)) * 0.21D);
								getPlayerOwner().damage(lavaDamage);
								getPlayerOwner().setBusy(false);
								return null;
							}
						});
						addState(1, () -> {
							movePlayer(getPlayerOwner(), 294, 104);
							return nextState(1);
						});
						addState(2, () -> {
							movePlayer(getPlayerOwner(), 295, 104);
							getPlayerOwner().message("and walk across");
							return nextState(1);
						});
						addState(3, () -> {
							movePlayer(getPlayerOwner(), 296, 105);
							return nextState(1);
						});
						addState(4, () -> {
							movePlayer(getPlayerOwner(), 297, 106);
							getPlayerOwner().incExp(Skills.AGILITY, 80, true);
							AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), obstacles, lastObstacle, 1500);
							getPlayerOwner().setBusy(false);
							return null;
						});
					}
				});
				return;
			case LEDGE:
				p.message("you stand on the ledge");
				p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Wilderness Agility Log") {
					public void init() {
						addState(0, () -> {
							if (passObstacle) {
								movePlayer(getPlayerOwner(), 296, 112);
								return invoke(1, 1);
							} else {
								getPlayerOwner().message("you lose your footing and fall to the level below");
								return invoke(6, 2);
							}
						});
						addState(1, () -> {
							getPlayerOwner().message("and walk across");
							movePlayer(getPlayerOwner(), 297, 112);
							return invoke(2, 1);
						});
						addState(2, () -> {
							movePlayer(getPlayerOwner(), 298, 112);
							return invoke(3, 1);
						});
						addState(3, () -> {
							movePlayer(getPlayerOwner(), 299, 111);
							return invoke(4, 1);
						});
						addState(4, () -> {
							movePlayer(getPlayerOwner(), 300, 111);
							return invoke(5, 1);
						});
						addState(5, () -> {
							movePlayer(getPlayerOwner(), 301, 111);
							getPlayerOwner().incExp(Skills.AGILITY, 80, true);
							AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), obstacles, lastObstacle, 1500);
							getPlayerOwner().setBusy(false);
							return null;
						});
						addState(6, () -> {
							int ledgeDamage = (int) Math.round((getPlayerOwner().getSkills().getLevel(Skills.HITS)) * 0.25D);
							movePlayer(getPlayerOwner(), 298, 2945);
							getPlayerOwner().message("You land painfully on the spikes");
							playerTalk(getPlayerOwner(), null, "ouch");
							getPlayerOwner().damage(ledgeDamage);
							getPlayerOwner().setBusy(false);
							return null;
						});
					}
				});
				return;
			case VINE:
				p.message("You climb up the cliff");
				p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 2, "Wilderness Agility Log") {
					public void init() {
						addState(0, () -> {
							movePlayer(getPlayerOwner(), 305, 118);
							return nextState(1);
						});
						addState(1, () -> {
							movePlayer(getPlayerOwner(), 304, 119);
							return nextState(1);
						});
						addState(2, () -> {
							movePlayer(getPlayerOwner(), 304, 120);
							getPlayerOwner().incExp(Skills.AGILITY, 80, true); // COMPLETION OF THE COURSE.
							AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), obstacles, lastObstacle, 1500);
							getPlayerOwner().setBusy(false);
							return null;
						});
					}
				});
				return;
		}
	}

	private boolean succeed(Player player) {
		return Formulae.calcProductionSuccessful(52, getCurrentLevel(player, Skills.AGILITY), true, 102);
	}

	private int failRate() {
		return random(1, 5);
	}
}
