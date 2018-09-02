package com.openrsc.server.plugins.skills.agility;

import static com.openrsc.server.plugins.Functions.AGILITY;
import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.movePlayer;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.random;
import static com.openrsc.server.plugins.Functions.sleep;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;

public class BarbarianAgilityCourse implements WallObjectActionListener,
		WallObjectActionExecutiveListener, ObjectActionListener,
		ObjectActionExecutiveListener {
	 
	public static final int LOW_WALL = 164;
	public static final int LOW_WALL2 = 163;
	public static final int LEDGE = 678;
	public static final int NET = 677;
	public static final int LOG = 676;
	public static final int PIPE = 671;
	public static final int BACK_PIPE = 672;
	public static final int SWING = 675;
	private static final int HANDHOLDS = 679;
	
	public static final int[] obstacleOrder = {675, 676, 677, 678, 163, 164};
	
	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return inArray(obj.getID(), PIPE, BACK_PIPE, SWING, LOG, LEDGE, NET, HANDHOLDS);
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		p.setBusy(true);
		boolean fail = succeed(p);
		switch(obj.getID()) {
			case BACK_PIPE:
			case PIPE:
				p.message("You squeeze through the pipe");
				sleep(1000);
				if(p.getY() <= 551) {
					movePlayer(p, 487, 554);
				} else {
					movePlayer(p, 487, 551);
				}
				p.incExp(AGILITY, 20, true);
				break;
			case SWING:
				p.message("you grab the rope and try and swing across");
				sleep(1000);
				int swingDamage = (int) Math.round((p.getSkills().getLevel(3)) * 0.15D);
				if(fail) {
					p.message("you skillfully swing across the hole");
					movePlayer(p, 486, 559);
					p.incExp(AGILITY, 80, true);
					AgilityUtils.setNextObstacle(p, obj.getID(), obstacleOrder, 300);
				} else {
					p.message("Your hands slip and you fall to the level below");
					sleep(1000);
					movePlayer(p, 486, 3389);
					p.message("you land painfully on the spikes");
					p.damage(swingDamage);
					playerTalk(p, null, "ouch");
				}
				break;
			case LOG:
				int slipDamage = (int) Math.round((p.getSkills().getLevel(3)) * 0.1D);
				p.message("You stand on the slippery log");
				sleep(2000);
				if(fail) {
					movePlayer(p,489, 563);
					sleep(650);
					movePlayer(p,490, 563);
					sleep(650);
					p.message("and walk across");
					movePlayer(p, 492, 563);
					p.incExp(AGILITY, 50, true);
					AgilityUtils.setNextObstacle(p, obj.getID(), obstacleOrder, 300);
				} else {
					p.message("You lose your footing and land in the water");
					movePlayer(p, 490, 561);
					p.message("Something in the water bites you");
					p.damage(slipDamage);
				}
				break;
			case NET:
				p.message("You climb up the netting");
				movePlayer(p, 496, 1507);
				p.incExp(AGILITY, 50, true);
				AgilityUtils.setNextObstacle(p, obj.getID(), obstacleOrder, 300);
				break;
			case LEDGE:
				if(obj.getX() != 498) {
					p.setBusy(false);
					return;
				}
				int ledgeDamage = (int) Math.round((p.getSkills().getLevel(3)) * 0.15D);
				if(fail) {
					movePlayer(p, 501, 1506);
					p.message("You skillfully balance across the hole");
					p.incExp(AGILITY, 80, true);
					AgilityUtils.setNextObstacle(p, obj.getID(), obstacleOrder, 300);
				} else {
					p.message("you lose your footing and fall to the level below");
					movePlayer(p, 499, 563);
					p.message("You land painfully on the spikes");
					p.damage(ledgeDamage);
					playerTalk(p, null, "ouch");
					
				}
				break;
			case HANDHOLDS:
				p.message("You climb up the wall");
				movePlayer(p, 497, 555);
				p.incExp(AGILITY, 20, true);
				AgilityUtils.setNextObstacle(p, obj.getID(), obstacleOrder, 300);
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
		p.setBusy(true);
		switch(obj.getID()) {
			case LOW_WALL:
				p.message("You jump over the wall");
				p.setBusyTimer(1000);
				sleep(650);
				movePlayer(p, p.getX() == obj.getX() ? p.getX() - 1 : p.getX() + 1, p.getY());
				break;
			case LOW_WALL2:
				p.message("You jump over the wall");
				p.setBusyTimer(1000);
				sleep(650);
				movePlayer(p, p.getX() == obj.getX() ? p.getX() - 1 : p.getX() + 1, p.getY());
				break;
		}
		p.incExp(AGILITY, 20, true);
		AgilityUtils.setNextObstacle(p, obj.getID(), obstacleOrder, 300);
		p.setBusy(false);
	}
	
	boolean succeed(Player player) {
		int level_difference = getCurrentLevel(player, AGILITY) - 35;
		int percent = random(1, 100);
		
		if(level_difference < 0)
			return true;
		if(level_difference >= 10)
		 	level_difference = 80;
		if(level_difference >= 15)
			level_difference = 90;
		else
		 	level_difference = 60 + level_difference;
		
		return percent <= level_difference;
	}

}
