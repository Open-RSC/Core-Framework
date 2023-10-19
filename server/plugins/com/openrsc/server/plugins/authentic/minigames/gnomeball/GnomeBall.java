package com.openrsc.server.plugins.authentic.minigames.gnomeball;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Minigames;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.rsc.impl.projectile.BallProjectileEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.authentic.minigames.gnomeball.GnomeField.Zone;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.plugins.triggers.UsePlayerTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class GnomeBall implements MiniGameInterface, UsePlayerTrigger, TakeObjTrigger,
	OpInvTrigger, OpLocTrigger {

	private static final int[][] SCORES_XP = {{20, 30, 35, 40, 220} , {40, 50, 60, 70, 220}};

	@Override
	public int getMiniGameId() {
		return Minigames.GNOME_BALL;
	}

	@Override
	public String getMiniGameName() {
		return "Gnome Ball (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		//mini-game complete handled already
	}

	@Override
	public void onUsePlayer(Player player, Player otherPlayer, Item item) {
		if (item.getCatalogId() == ItemId.GNOME_BALL.id()) {
			if (otherPlayer.isIronMan(IronmanMode.Ironman.id()) || otherPlayer.isIronMan(IronmanMode.Ultimate.id())
				|| otherPlayer.isIronMan(IronmanMode.Hardcore.id()) || otherPlayer.isIronMan(IronmanMode.Transfer.id())) {
				player.message(otherPlayer.getUsername() + " is an Ironman. " + (otherPlayer.isMale() ? "He" : "She") + " stands alone.");
			} else {
				// does not matter where the players are at, neither in the field or wild,
				// nor if they have free inventory space
				player.getWorld().getServer().getGameEventHandler().add(new BallProjectileEvent(player.getWorld(), player, otherPlayer, 3) {
					@Override
					public void doSpell() {
						if (otherPlayer.isPlayer()) {
							player.getCarriedItems().remove(item);
							player.message("you throw the ball");

							// only the shops interface is reset is closed if they are accessing it
							if (otherPlayer.accessingShop()) {
								otherPlayer.resetShop();
							}

							otherPlayer.getCarriedItems().getInventory().add(item);
							otherPlayer.message("Warning! " + player.getUsername() + " is shooting at you!");
							otherPlayer.message("you catch the ball");
							say(player, "good catch");
						}
					}
				});
			}
		}
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		Zone playerZone = GnomeField.getInstance().resolvePositionToZone(player);
		if (playerZone == Zone.ZONE_NO_PASS) {
			player.message("you can't make the pass from here");
		} else if (playerZone == Zone.ZONE_PASS) {
			Npc gnome_team;
			if (player.getY() <= 449) {
				gnome_team = ifnearvisnpc(player, GnomeNpcs.GNOME_BALLER_NORTH, 10);
			}
			else {
				gnome_team = ifnearvisnpc(player, GnomeNpcs.GNOME_BALLER_SOUTH, 10);
			}
			if (gnome_team != null) {
				GnomeNpcs.passToTeam(player, gnome_team);
			}
		} else if (playerZone == Zone.ZONE_1XP_OUTER || playerZone == Zone.ZONE_1XP_INNER) {
			player.setAttribute("throwing_ball_game", true);
			Npc goalie = ifnearvisnpc(player, GnomeNpcs.GOALIE, 15);
			player.getWorld().getServer().getGameEventHandler().add(new BallProjectileEvent(player.getWorld(), player, goalie, 3) {
				@Override
				public void doSpell() {

				}
			});

			//logic to try to score from 1xp
			thinkbubble(new Item(ItemId.GNOME_BALL.id()));
			mes("you throw the ball at the goal");
			delay(3);
			player.getCarriedItems().remove(new Item(ItemId.GNOME_BALL.id()));
			int random = DataConversions.random(0, 4);
			if (random < 2 + (playerZone == Zone.ZONE_1XP_INNER ? 2 : 0)) {
				mes("it flys through the net...");
				delay(3);
				mes("into the hands of the goal catcher");
				delay(3);
				Npc cheerleader = ifnearvisnpc(player, GnomeNpcs.CHEERLEADER, 10);
				if (cheerleader != null) {
					cheerLeaderCelebrate(player, cheerleader);
				}
				handleScore(player, 0);
			} else {
				if (DataConversions.random(0, 2) < 2 || playerZone == Zone.ZONE_1XP_OUTER) {
					mes("the ball flys way over the net");
					delay(3);
				} else {
					mes("the ball just misses the net");
					delay(3);
				}
			}
		} else if (playerZone == Zone.ZONE_2XP_OUTER || playerZone == Zone.ZONE_2XP_INNER) {
			player.setAttribute("throwing_ball_game", true);
			Npc goalie = ifnearvisnpc(player, GnomeNpcs.GOALIE, 15);
			player.getWorld().getServer().getGameEventHandler().add(new BallProjectileEvent(player.getWorld(), player, goalie, 3) {
				@Override
				public void doSpell() {

				}
			});

			//logic to try to score from 2xp
			thinkbubble(new Item(ItemId.GNOME_BALL.id()));
			mes("you throw the ball at the goal");
			delay(3);
			player.getCarriedItems().remove(new Item(ItemId.GNOME_BALL.id()));
			int random = DataConversions.random(0, 9);
			if (random < 4 + (playerZone == Zone.ZONE_2XP_INNER ? 2 : 0)) {
				mes("it flys through the net...");
				delay(3);
				mes("into the hands of the goal catcher");
				delay(3);
				Npc cheerleader = ifnearvisnpc(player, GnomeNpcs.CHEERLEADER, 10);
				if (cheerleader != null) {
					cheerLeaderCelebrate(player, cheerleader);
				}
				handleScore(player, 1);
			} else {
				if (DataConversions.random(0, 2) < 2 || playerZone == Zone.ZONE_2XP_OUTER) {
					mes("you miss by a mile!");
					delay(3);
				} else {
					mes("the ball flys way over the net");
					delay(3);
				}
			}
		} else if (playerZone == Zone.ZONE_NOT_VISIBLE || playerZone == Zone.ZONE_OUTSIDE_THROWABLE) {
			thinkbubble(new Item(ItemId.GNOME_BALL.id()));
			mes("you throw the ball at the goal");
			delay(3);
			mes("you miss by a mile!");
			delay(3);
			mes("maybe you should try playing on the pitch!");
			delay(3);
		}
	}

	private void cheerLeaderCelebrate(Player player, Npc n) {

		switch(DataConversions.random(0, 2)) {
		case 0:
			npcsay(player, n, "yeah", "good goal");
			break;
		case 1:
			npcsay(player, n, "yahoo", "go go traveller");
			break;
		case 2:
			npcsay(player, n, "yeah baby", "gimme a g, gimme an o, gimme an a, gimme an l");
			break;
		}
	}

	private void loadIfNotMemory(Player player, String cacheName) {
		//load from player cache if not present in memory
		if((player.getAttribute(cacheName, -1) == -1) && player.getCache().hasKey(cacheName)) {
			player.setAttribute(cacheName, player.getCache().getInt(cacheName));
		} else if (player.getAttribute(cacheName, -1) == -1) {
			player.setAttribute(cacheName, 0);
		}
	}

	private void handleScore(Player player, int score_zone) {
		int totalXp = 0, totalGoals = 1;

		if (player.getCache().hasKey("gnomeball_total_goals")) {
			totalGoals += player.getCache().getInt("gnomeball_total_goals");
		}

		if (player.getCache().hasKey("gnomeball_xp")) {
			totalXp += player.getCache().getInt("gnomeball_xp");
		}

		loadIfNotMemory(player, "gnomeball_goals");
		int prev_goalCount = player.getAttribute("gnomeball_goals", 0);
		player.incExp(Skill.RANGED.id(), SCORES_XP[score_zone][prev_goalCount], true);
		player.incExp(Skill.AGILITY.id(), SCORES_XP[score_zone][prev_goalCount], true);
		totalXp += SCORES_XP[score_zone][prev_goalCount];
		showScoreWindow(player, prev_goalCount+1);
		if (prev_goalCount+1 == 5) {
			ActionSender.sendTeleBubble(player, player.getX(), player.getY(), true);
		}
		player.setAttribute("gnomeball_goals", (prev_goalCount+1)%5);
		player.getCache().set("gnomeball_xp", totalXp);
		player.getCache().set("gnomeball_total_goals", totalGoals);
	}

	private void showScoreWindow(Player player, int goalNum) {
		String text = "@yel@goal";
		if (goalNum > 1) {
			text += (" " + goalNum);
		}
		if (goalNum == 5) {
			text += ("% %Well Done% %@red@Agility Bonus");
		}
		ActionSender.sendBox(player, text, false);
	}

	@Override
	public void onTakeObj(Player player, GroundItem item) {
		if (item.getID() == ItemId.GNOME_BALL.id()) {
			if (player.getCarriedItems().hasCatalogID(ItemId.GNOME_BALL.id(), Optional.of(false))) {
				mes("you can only carry one ball at a time");
				delay(2);
				mes("otherwise it would be too easy");
				delay(2);
			} else {
				player.getWorld().unregisterItem(item);
				give(player, ItemId.GNOME_BALL.id(), 1);
			}
		}
	}

	@Override
	public boolean blockUsePlayer(Player player, Player otherPlayer, Item item) {
		return item.getCatalogId() == ItemId.GNOME_BALL.id();
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem item) {
		return item.getID() == ItemId.GNOME_BALL.id();
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.GNOME_BALL.id();
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 702;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 702) {
			if (player.getY() > 456 || !player.getCarriedItems().hasCatalogID(ItemId.GNOME_BALL.id(), Optional.of(false))) {
				player.message("you open the gate");
				player.message("and walk through");
				doGate(player, obj, 357);
			}
			else {
				player.message("you have to leave the ball here");
			}
		}
	}
}
