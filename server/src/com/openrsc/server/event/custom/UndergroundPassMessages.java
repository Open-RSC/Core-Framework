package com.openrsc.server.event.custom;

import com.openrsc.server.Constants;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;

/**
 * @author Davve
 * Random appearing messages from the underground cave appearing at random time stamps triggers
 * when you cross an obstacle (Rock) & (Agility obstacles in the black area(Map3)).
 */

public class UndergroundPassMessages extends DelayedEvent {

	private Player p;

	public UndergroundPassMessages(Player p, int delay) {
		super(null, delay, "Underground Pass Messages");
		this.p = p;
	}

	@Override
	public void run() {
		int random = DataConversions.getRandom().nextInt(6);
		if (random == 0) {
			p.message("@red@iban will save you....he'll save us all");
		} else if (random == 1) {
			p.message("@red@join us...join us...embrace the mysery");
		} else if (random == 2 && p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) >= 4) {
			p.message("@red@I see you adventurer...you can't hide");
		} else if (random == 3 && p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) >= 4) {
			p.message("@red@Come taste the pleasure of evil");
		} else if (random == 4 && p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) >= 4) {
			p.message("@red@Death is only the beginning");
		} else if (random == 5) {
			stop();
		}
		stop();
	}
}
