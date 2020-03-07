package com.openrsc.server.plugins.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.npcsay;
import static com.openrsc.server.plugins.Functions.say;

import com.openrsc.server.constants.NpcId;

public class FinancialAdvisor implements TalkNpcTrigger {
	/**
	 * @author Davve
	 * Tutorial island financial advisor
	 */

	@Override
	public void onTalkNpc(Player p, Npc n) {
		npcsay(p, n, "Hello there",
			"I'm your designated financial advisor");
		Functions.say(p, n, "That's good because I don't have any money at the moment",
			"How do I get rich?");
		npcsay(p, n, "There are many different ways to make money in runescape",
			"for example certain monsters will drop a bit of loot",
			"To start with killing men and goblins might be a good idea",
			"Some higher level monsters will drop quite a lot of treasure",
			"several of runescapes skills are good money making skills",
			"two of these skills are mining and fishing",
			"there are instructors on the island who will help you with this",
			"using skills and combat to make money is a good plan",
			"because using a skill also slowly increases your level in that skill",
			"A high level in a skill opens up many more oppurtunites",
			"Some other ways of making money include taking quests and tasks",
			"You can find these by talking to certain game controlled characters",
			"Our quest advisors will tell you about this",
			"Sometimes you will find items lying around",
			"Selling these to the shops makes some money too",
			"Now continue through the next door");
		if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") < 40)
			p.getCache().set("tutorial", 40);
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.FINANCIAL_ADVISOR.id();
	}

}
