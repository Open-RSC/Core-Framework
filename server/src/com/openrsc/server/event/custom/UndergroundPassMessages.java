package com.openrsc.server.event.custom;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.util.rsc.DataConversions;

/**
 * Random appearing messages from the underground cave appearing at random time stamps triggers
 * when you cross an obstacle (Rock) & (Agility obstacles in the black area(Map3)).
 */

public class UndergroundPassMessages extends DelayedEvent {

	private Player player;

	public UndergroundPassMessages(World world, Player player, int delay) {
		super(world, null, delay, "Underground Pass Messages");
		this.player = player;
	}

	@Override
	public void run() {
		int random = DataConversions.getRandom().nextInt(6);
		if (random == 0) {
			player.message("@red@iban will save you....he'll save us all");
		} else if (random == 1) {
			player.message("@red@join us...join us...embrace the mysery");
		} else if (random == 2 && player.getQuestStage(Quests.UNDERGROUND_PASS) >= 4) {
			player.message("@red@I see you adventurer...you can't hide");
		} else if (random == 3 && player.getQuestStage(Quests.UNDERGROUND_PASS) >= 4) {
			player.message("@red@Come taste the pleasure of evil");
		} else if (random == 4 && player.getQuestStage(Quests.UNDERGROUND_PASS) >= 4) {
			player.message("@red@Death is only the beginning");
		} else if (random == 5) {
			stop();
		}
		stop();
	}
}
