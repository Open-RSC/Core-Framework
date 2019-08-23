package com.openrsc.server.plugins.skills.agility;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.Formulae;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.openrsc.server.plugins.Functions.*;

public class BarbarianAgilityCourse implements WallObjectActionListener,
	WallObjectActionExecutiveListener, ObjectActionListener,
	ObjectActionExecutiveListener {

	private static final int LOW_WALL = 163;
	private static final int LOW_WALL2 = 164;
	private static final int LEDGE = 678;
	private static final int NET = 677;
	private static final int LOG = 676;
	private static final int PIPE = 671;
	private static final int BACK_PIPE = 672;
	private static final int SWING = 675;
	private static final int HANDHOLDS = 679;

	//private static final int[] obstacleOrder = {SWING, LOG, NET, LEDGE, LOW_WALL, LOW_WALL2};
	private static Set<Integer> obstacles = new HashSet<Integer>(Arrays.asList(SWING, LOG, NET, LEDGE, LOW_WALL));
	private static Integer lastObstacle = LOW_WALL2;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return inArray(obj.getID(), PIPE, BACK_PIPE, SWING, LOG, LEDGE, NET, HANDHOLDS);
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == BACK_PIPE || obj.getID() == PIPE) {
			if (getCurrentLevel(p, Skills.AGILITY) < 35) {
				p.message("You need an agility level of 35 to attempt to squeeze through the pipe");
				p.setBusy(false);
				return;
			}
			if (obj.getWorld().getServer().getConfig().WANT_FATIGUE) {
				if (p.getFatigue() >= p.MAX_FATIGUE) {
					p.message("You are too tired to squeeze through the pipe");
					p.setBusy(false);
					return;
				}
			}
			p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 0, "BarAGIPipe") {
				public void init() {
					addState(0, () -> {
						message(getPlayerOwner(), "You squeeze through the pipe");
						getPlayerOwner().setBusy(true);
						return invoke(1, 4);
					});
					addState(1, () -> {
						getPlayerOwner().setBusy(false);
						if (getPlayerOwner().getY() <= 551) {
							movePlayer(getPlayerOwner(), 487, 554);
						} else {
							movePlayer(getPlayerOwner(), 487, 551);
						}
						getPlayerOwner().incExp(Skills.AGILITY, 20, true);
						return null;
					});
				}
			});
			return;
		}
		if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
			if (p.getFatigue() >= p.MAX_FATIGUE && !inArray(obj.getID(), SWING, LOG, NET)) {
				p.message("you are too tired to train");
				return;
			}
		}
		p.setBusy(true);
		final boolean passObstacle = succeed(p);
		switch (obj.getID()) {
			case SWING:
				p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 0, "BarAGISwing") {
					public void init() {
						addState(0, () -> {
							getPlayerOwner().setBusy(true);
							message(getPlayerOwner(), "You grab the rope and try and swing across");
							return invoke(1, 2);
						});

						addState(1, () -> {
							if (passObstacle) {
								message(getPlayerOwner(), "You skillfully swing across the hole");
								movePlayer(getPlayerOwner(), 486, 559);
								getPlayerOwner().incExp(Skills.AGILITY, 80, true);
								AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), obstacles, lastObstacle, 300);
								p.setBusy(false);
								return null;
							} else {
								message(getPlayerOwner(), "Your hands slip and you fall to the level below");
								return invoke(2, 2);
							}
						});

						addState(2, () -> {
							int swingDamage = (int) Math.round((getPlayerOwner().getSkills().getLevel(Skills.HITS)) * 0.15D);
							movePlayer(getPlayerOwner(), 486, 3389);
							message(getPlayerOwner(), "You land painfully on the spikes");
							getPlayerOwner().damage(swingDamage);
							playerTalk(getPlayerOwner(), null, "ouch");
							p.setBusy(false);
							return null;
						});
					}
				});
				break;
			case LOG:
				p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 0, "BarAGILog") {
					public void init() {
						addState(0, () -> {
							getPlayerOwner().setBusy(true);
							getPlayerOwner().message("you stand on the slippery log");
							return invoke(1, 3);
						});
						addState(1, () -> {
							if (passObstacle) {
								movePlayer(getPlayerOwner(), 489, 563);
								return invoke(2, 1);
							} else {
								int slipDamage = (int) Math.round((p.getSkills().getLevel(Skills.HITS)) * 0.1D);
								getPlayerOwner().message("Your lose your footing and land in the water");
								movePlayer(getPlayerOwner(), 490, 561);
								getPlayerOwner().message("Something in the water bites you");
								getPlayerOwner().damage(slipDamage);
								getPlayerOwner().setBusy(false);
								return null;
							}
						});
						addState(2, () -> {
							movePlayer(getPlayerOwner(), 490, 563);
							return invoke(3,1);
						});
						addState(3, () -> {
							getPlayerOwner().message("and walk across");
							movePlayer(getPlayerOwner(), 492, 563);
							getPlayerOwner().incExp(Skills.AGILITY, 50, true);
							AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), obstacles, lastObstacle, 300);
							getPlayerOwner().face(495,563);
							getPlayerOwner().setBusy(false);
							return null;
						});

					}
				});
				break;
			case NET:
				p.message("You climb up the netting");
				movePlayer(p, 496, 1507);
				p.incExp(Skills.AGILITY, 50, true);
				AgilityUtils.completedObstacle(p, obj.getID(), obstacles, lastObstacle, 300);
				break;
			case LEDGE:
				p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 0, "BarAGILedge") {
					public void init() {
						addState(0, () -> {
							if (obj.getX() != 498) {
								return null;
							}
							getPlayerOwner().setBusy(true);
							getPlayerOwner().message("You put your foot on the ledge and try to edge across");
							return invoke(1, 2);
						});

						addState(1, () -> {
							if (passObstacle) {
								movePlayer(getPlayerOwner(), 501, 1506);
								getPlayerOwner().message("You skillfully balance across the hole");
								getPlayerOwner().incExp(Skills.AGILITY, 80, true);
								AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), obstacles, lastObstacle, 300);
							} else {
								int ledgeDamage = (int) Math.round((getPlayerOwner().getSkills().getLevel(Skills.HITS)) * 0.15D);
								getPlayerOwner().message("you lose your footing and fall to the level below");
								movePlayer(getPlayerOwner(), 499, 563);
								getPlayerOwner().message("You land painfully on the spikes");
								getPlayerOwner().damage(ledgeDamage);
								playerTalk(getPlayerOwner(), null, "ouch");
							}
							getPlayerOwner().setBusy(false);
							return null;
						});
					}
				});
				break;
			case HANDHOLDS:
				p.message("You climb up the wall");
				movePlayer(p, 497, 555);
				p.incExp(Skills.AGILITY
					, 20, true);
				break;
		}

		p.setBusy(false);
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		return inArray(obj.getID(), LOW_WALL, LOW_WALL2);
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 0, "BarAGILowWalls") {
			public void init() {
				addState(0, () -> {
					if (getPlayerOwner().getWorld().getServer().getConfig().WANT_FATIGUE) {
						if (getPlayerOwner().getFatigue() >= getPlayerOwner().MAX_FATIGUE && !inArray(obj.getID(), LOW_WALL, LOW_WALL2)) {
							getPlayerOwner().message("you are too tired to train");
							return null;
						}
					}
					getPlayerOwner().setBusy(true);
					getPlayerOwner().message("You jump over the wall");
					return invoke(1,2);
				});
				addState(1, () -> {
					movePlayer(getPlayerOwner(),
						getPlayerOwner().getX() == obj.getX() ? getPlayerOwner().getX() - 1 : getPlayerOwner().getX() + 1, getPlayerOwner().getY());
					getPlayerOwner().incExp(Skills.AGILITY, 20, true);
					AgilityUtils.completedObstacle(getPlayerOwner(), obj.getID(), obstacles, lastObstacle, 300);
					getPlayerOwner().setBusy(false);
					return null;
				});
			}
		});
	}

	private boolean succeed(Player player) {
		return Formulae.calcProductionSuccessful(35, getCurrentLevel(player, Skills.AGILITY), true, 65);
	}

}
