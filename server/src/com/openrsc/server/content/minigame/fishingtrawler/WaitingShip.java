package com.openrsc.server.content.minigame.fishingtrawler;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.Area;

import java.util.LinkedList;
import java.util.Queue;

public class WaitingShip {

	private static final int MINIMUM = 1;
	private Area waitingShip = new Area(318, 330, 740, 744, "FishingTrawler WaitingShip");
	private Point waitingRoomSpawn = new Point(320, 742);

	private Queue<Player> players = new LinkedList<Player>();
	private FishingTrawler trawler;
	private int timeTillNextMessage;
	private int gameStarts;
	private boolean startingGame = false;

	public WaitingShip(FishingTrawler fishingTrawler) {
		this.trawler = fishingTrawler;
	}

	public void update() {
		if (trawler.getStage() == FishingTrawler.WAITING) {
			if (players.size() >= FishingTrawler.MAX_PLAYERS && !startingGame) {
				// start instantly.
				gameStarts = 5;
				startingGame = true;
			} else if (players.size() >= MINIMUM && !startingGame) {
				gameStarts = 100;
				startingGame = true;
			}
		
			if (gameStarts > 0 && startingGame) {
				gameStarts--;
				if (gameStarts % 2 == 1) {// around 1.2 secs per msg.
					for (Player p : players) {
						p.message("Trawler will leave in " + (gameStarts * 600 / 1000) + " seconds");
					}
				}
			} else if (gameStarts == 0 && startingGame) {
				Player waitingPlayer = null;
				trawler.start();
				while ((waitingPlayer = players.poll()) != null) {
					trawler.addPlayer(waitingPlayer);
					if (trawler.getPlayers().size() >= FishingTrawler.MAX_PLAYERS) {
						break;
					}
				}
				startingGame = false;
			}

		} else {
			if (timeTillNextMessage-- <= 0) {
				for (Player p : players) {
					p.message("Trawler ship will return in approximately " + (trawler.getTimeTillReturn() * 600 / 60000)
							+ " minutes");
				}
				timeTillNextMessage = 15;
			}
		}
	}

	public void addPlayer(Player player) {
		if (!players.contains(player)) {
			players.add(player);
			player.teleport(320, 741, true);
		}
	}

	public void removePlayer(Player p) {
		p.setLocation(FishingTrawler.SPAWN_LAND, true);
		if(players.contains(p))
			players.remove(p);
		
		
	}
}
