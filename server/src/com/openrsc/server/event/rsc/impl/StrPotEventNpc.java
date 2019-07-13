package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.HashMap;

import static com.openrsc.server.plugins.Functions.sleep;

/**
 * @author n0m
 */
public class StrPotEventNpc extends GameTickEvent {
	
	private HashMap<Integer, Integer> restoringStats = new HashMap<Integer, Integer>();
	private long lastPot = System.currentTimeMillis();

	private long lastRestoration = System.currentTimeMillis();

	public StrPotEventNpc(Npc npc) {
		super(npc, 1, "Str Pot Event NPC");
	}

	@Override
	public void run() {

		boolean restored = false;

			if (!owner.inCombat() && owner.getLocation().inWilderness())
			{
				int baseStat = owner.getSkills().getLevel(2) > owner.getSkills().getMaxStat(2) ? owner.getSkills().getMaxStat(2) : owner.getSkills().getLevel(2);
				int newStat = baseStat
				+ DataConversions.roundUp((owner.getSkills().getMaxStat(2) / 100D) * 10)
				+ 3;
			if (newStat > owner.getSkills().getLevel(2)) {
				for (Player p22 : World.getWorld().getPlayers()) {
					//p22.message("TEST 00000000");				
				}
				owner.getSkills().setLevel(2, newStat);
				sleep(1200);
				//break;
			} else {
				for (Player p22 : World.getWorld().getPlayers()) {
					//p22.message("TEST 111111");				
				}
				//break;
			return;
			}
				Server.getServer().getGameEventHandler().add(new StatRestorationEvent(owner));
			}
	}
}
