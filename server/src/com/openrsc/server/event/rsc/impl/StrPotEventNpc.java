package com.openrsc.server.event.rsc.impl;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;
import com.openrsc.server.event.rsc.impl.StatRestorationEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import static com.openrsc.server.plugins.Functions.sleep;
import static com.openrsc.server.plugins.Functions.showBubble2;

/**
 * @author n0m
 */
public class StrPotEventNpc extends GameTickEvent {
	
	private HashMap<Integer, Integer> restoringStats = new HashMap<Integer, Integer>();
	private long lastPot = System.currentTimeMillis();

	private long lastRestoration = System.currentTimeMillis();

	public StrPotEventNpc(Npc npc) {
		super(npc, 1);
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