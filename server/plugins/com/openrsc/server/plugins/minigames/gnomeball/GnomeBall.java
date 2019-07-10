package com.openrsc.server.plugins.minigames.gnomeball;

import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.impl.BallProjectileEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnPlayerListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnPlayerExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.minigames.gnomeball.GnomeField.Zone;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.doGate;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showBubble;

import com.openrsc.server.Constants;

public class GnomeBall implements MiniGameInterface, InvUseOnPlayerListener, InvUseOnPlayerExecutiveListener, PickupListener, PickupExecutiveListener,
InvActionListener, InvActionExecutiveListener, ObjectActionListener, ObjectActionExecutiveListener {
		
	private static final int[][] SCORES_XP = {{20, 30, 35, 40, 220} , {40, 50, 60, 70, 220}};

	@Override
	public int getMiniGameId() {
		return Constants.Minigames.GNOME_BALL;
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
	public void handleReward(Player p) {
		//mini-game complete handled already
	}
	
	@Override
	public void onInvUseOnPlayer(Player player, Player otherPlayer, Item item) {
		if (item.getID() == ItemId.GNOME_BALL.id()) {
			if (otherPlayer.isIronMan(1) || otherPlayer.isIronMan(2) || otherPlayer.isIronMan(3)) {
				player.message(otherPlayer.getUsername() + " is an Iron Man. He stands alone.");
			} else {
				// does not matter where the players are at, neither in the field or wild,
				// nor if they have free inventory space
				Server.getServer().getGameEventHandler().add(new BallProjectileEvent(player, otherPlayer, 3) {
					@Override
					public void doSpell() {
						if (otherPlayer.isPlayer()) {
							player.getInventory().remove(item);
							player.message("you throw the ball");
							
							// only the shops interface is reset is closed if they are accessing it
							if (otherPlayer.accessingShop()) {
								otherPlayer.resetShop();
							}
							
							otherPlayer.getInventory().add(item);
							otherPlayer.message("Warning! " + player.getUsername() + " is shooting at you!");
							otherPlayer.message("you catch the ball");
							playerTalk(player, null, "Good catch");
						}
					}
				});
			}
		}
	}
	
	@Override
	public void onInvAction(Item item, Player player) {
		Zone playerZone = GnomeField.getInstance().resolvePositionToZone(player);
		if (playerZone == Zone.ZONE_NO_PASS) {
			player.message("you can't make the pass from here");
		} else if (playerZone == Zone.ZONE_PASS) {
			Npc gnome_team; 
			if (player.getY() <= 449) {
				gnome_team = getNearestNpc(player, GnomeNpcs.GNOME_BALLER_NORTH, 10);
			}
			else {
				gnome_team = getNearestNpc(player, GnomeNpcs.GNOME_BALLER_SOUTH, 10);
			}
			if (gnome_team != null) {
				gnome_team.initializeIndirectTalkScript(player);
			}
		} else if (playerZone == Zone.ZONE_1XP_OUTER || playerZone == Zone.ZONE_1XP_INNER) {
			player.setSyncAttribute("throwing_ball_game", true);
			Npc goalie = getNearestNpc(player, GnomeNpcs.GOALIE, 15);
			player.setBusyTimer(600);
			Server.getServer().getGameEventHandler().add(new BallProjectileEvent(player, goalie, 3) {
				@Override
				public void doSpell() {
					//logic to try to score from 1xp
					showBubble(player, new Item(ItemId.GNOME_BALL.id()));
					message(player, "you throw the ball at the goal");
					removeItem(player, ItemId.GNOME_BALL.id(), 1);
					int random = DataConversions.random(0, 4);
					if (random < 2 + (playerZone == Zone.ZONE_1XP_INNER ? 2 : 0)) {
						message(player, "it flys through the net...",
								"into the hands of the goal catcher");
						Npc cheerleader = getNearestNpc(player, GnomeNpcs.CHEERLEADER, 10);
						if (cheerleader != null) {
							cheerLeaderCelebrate(player, cheerleader);
						}
						handleScore(player, 0);
					} else {
						if (DataConversions.random(0, 2) < 2 || playerZone == Zone.ZONE_1XP_OUTER) {
							message(player, "the ball flys way over the net");
						} else {
							message(player, "the ball just misses the net");
						}
					}
				}
			});
		} else if (playerZone == Zone.ZONE_2XP_OUTER || playerZone == Zone.ZONE_2XP_INNER) {
			player.setSyncAttribute("throwing_ball_game", true);
			Npc goalie = getNearestNpc(player, GnomeNpcs.GOALIE, 15);
			player.setBusyTimer(600);
			Server.getServer().getGameEventHandler().add(new BallProjectileEvent(player, goalie, 3) {
				@Override
				public void doSpell() {
					//logic to try to score from 2xp
					showBubble(player, new Item(ItemId.GNOME_BALL.id()));
					message(player, "you throw the ball at the goal");
					removeItem(player, ItemId.GNOME_BALL.id(), 1);
					int random = DataConversions.random(0, 9);
					if (random < 4 + (playerZone == Zone.ZONE_2XP_INNER ? 2 : 0)) {
						message(player, "it flys through the net...",
								"into the hands of the goal catcher");
						Npc cheerleader = getNearestNpc(player, GnomeNpcs.CHEERLEADER, 10);
						if (cheerleader != null) {
							cheerLeaderCelebrate(player, cheerleader);
						}
						handleScore(player, 1);
					} else {
						if (DataConversions.random(0, 2) < 2 || playerZone == Zone.ZONE_2XP_OUTER) {
							message(player, "you miss by a mile!");
						} else {
							message(player, "the ball flys way over the net");
						}
					}
				}
			});
		} else if (playerZone == Zone.ZONE_NOT_VISIBLE || playerZone == Zone.ZONE_OUTSIDE_THROWABLE) {
			showBubble(player, new Item(ItemId.GNOME_BALL.id()));
			message(player, "you throw the ball at the goal",
					"you miss by a mile!",
					"maybe you should try playing on the pitch!");
		}
	}
	
	private void cheerLeaderCelebrate(Player p, Npc n) {
		
		switch(DataConversions.random(0, 2)) {
		case 0:
			npcTalk(p, n, "yeah", "good goal");
			break;
		case 1:
			npcTalk(p, n, "yahoo", "go go traveller");
			break;
		case 2:
			npcTalk(p, n, "yeah baby", "gimme a g, gimme an o, gimme an a, gimme an l");
			break;
		}
	}
	
	private void loadIfNotMemory(Player p, String cacheName) {
		//load from player cache if not present in memory
		if((p.getSyncAttribute(cacheName, -1) == -1) && p.getCache().hasKey(cacheName)) {
			p.setSyncAttribute(cacheName, p.getCache().getInt(cacheName));
		} else if (p.getSyncAttribute(cacheName, -1) == -1) {
			p.setSyncAttribute(cacheName, 0);
		}
	}
	
	private void handleScore(Player p, int score_zone) {
		loadIfNotMemory(p, "gnomeball_goals");
		int prev_goalCount = p.getAttribute("gnomeball_goals", 0);
		p.incExp(SKILLS.RANGED.id(), SCORES_XP[score_zone][prev_goalCount], true);
		p.incExp(SKILLS.AGILITY.id(), SCORES_XP[score_zone][prev_goalCount], true);
		showScoreWindow(p, prev_goalCount+1);
		if (prev_goalCount+1 == 5) {
			ActionSender.sendTeleBubble(p, p.getX(), p.getY(), true);
		}
		p.setAttribute("gnomeball_goals", (prev_goalCount+1)%5);
	}
	
	private void showScoreWindow(Player p, int goalNum) {
		String text = "@yel@goal";
		if (goalNum > 1) {
			text += (" " + goalNum);
		}
		if (goalNum == 5) {
			text += ("% %Well Done% %@red@Agility Bonus");
		}
		ActionSender.sendBox(p, text, false);
	}

	@Override
	public void onPickup(Player p, GroundItem item) {
		if (item.getID() == ItemId.GNOME_BALL.id()) {
			if (hasItem(p, ItemId.GNOME_BALL.id())) {
				message(p, 1200, "you can only carry one ball at a time", "otherwise it would be too easy");
			} else {
				World.getWorld().unregisterItem(item);
				addItem(p, ItemId.GNOME_BALL.id(), 1);
			}
		}
	}
	
	@Override
	public boolean blockInvUseOnPlayer(Player player, Player otherPlayer, Item item) {
		return item.getID() == ItemId.GNOME_BALL.id();
	}

	@Override
	public boolean blockPickup(Player player, GroundItem item) {
		return item.getID() == ItemId.GNOME_BALL.id();
	}

	@Override
	public boolean blockInvAction(Item item, Player player) {
		return item.getID() == ItemId.GNOME_BALL.id();
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 702;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		if (obj.getID() == 702) {
			if (player.getY() > 456 || !hasItem(player, ItemId.GNOME_BALL.id())) {
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
