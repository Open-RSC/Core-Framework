package com.openrsc.server.plugins.minigames.gnomeball;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.impl.BallProjectileEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.plugins.minigames.gnomeball.GnomeField.Zone;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class GnomeNpcs implements AttackNpcTrigger, SpellNpcTrigger, PlayerRangeNpcTrigger,
	TalkNpcTrigger, OpNpcTrigger, IndirectTalkToNpcTrigger {

	private static final int[] GNOME_BALLERS_ZONE_PASS = {605, 606, 607, 608};
	private static final int[] GNOME_BALLERS_ZONE1XP_OUTER = {603, 604};
	private static final int[] GNOME_BALLERS_ZONE2XP_OUTER = {595, 600, 602};
	private static final int[] GNOME_BALLERS_ZONE1XP_INNER = {597, 598, 599};
	public static final int GNOME_BALLER_NORTH = 609;
	public static final int GNOME_BALLER_SOUTH = 610;
	public static final int GOALIE = 596;
	public static final int CHEERLEADER = 611;
	private static final int REFEREE = 601;
	private static final int OFFICIAL = 625;
	private static final int[] TACKLING_XP = {15, 20};

	@Override
	public boolean blockPlayerRangeNpc(Player p, Npc n) {
		return DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID());
	}

	@Override
	public boolean blockSpellNpc(Player p, Npc n) {
		return DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID());
	}

	@Override
	public boolean blockAttackNpc(Player p, Npc n) {
		return DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID());
	}

	@Override
	public void onPlayerRangeNpc(Player p, Npc n) {
		if(DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID())) {
			mes(p, 1200, "you can't attack this gnome", "that's cheating");
		}
	}

	@Override
	public void onSpellNpc(Player p, Npc n) {
		if(DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID())) {
			mes(p, 1200, "you can't attack this gnome", "that's cheating");
		}
	}

	@Override
	public void onAttackNpc(Player p, Npc n) {
		if(DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID())) {
			mes(p, 1200, "you can't attack this gnome", "that's cheating");
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == CHEERLEADER || n.getID() == OFFICIAL || n.getID() == REFEREE
				|| DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID());
	}

	@Override
	public boolean blockOpNpc(Npc n, String command, Player p) {
		return n.getID() == GNOME_BALLER_NORTH || n.getID() == GNOME_BALLER_SOUTH
				|| DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID());
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == CHEERLEADER) {
			say(p, n, "hello");
			npcsay(p, n, "hi there, how are you doing?");
			say(p, n, "not bad thanks");
			npcsay(p, n, "i just love the big games",
					"all those big muscle bound gnomes running around");
			say(p, n, "big?");
			npcsay(p, n, "do you play gnome ball?");
			int option = multi(p, n, false, //do not send over
					"what is it?", "play! i'm a gnome ball master");
			if (option == 0) {
				say(p, n, "what is it?");
				npcsay(p, n, "like, only the greatest gnome ball game ever made!");
				say(p, n, "are there many gnome ball games");
				npcsay(p, n, "no, there's just one",
						"and it's the best");
				say(p, n, "ok, so how do you play?");
				npcsay(p, n, "the attacker gets the ball and runs towards the goal net");
				say(p, n, "and...?");
				npcsay(p, n, "scores of course");
				say(p, n, "sounds easy enough");
				npcsay(p, n, "you'll be playing against the best defenders in the gnome ball league");
				say(p, n, "really, are there many teams in the league?");
				npcsay(p, n, "nope, just us!");
			} else if (option == 1) {
				say(p, n, "play! i'm a gnome ball master?");
				npcsay(p, n, "really, that's amazing, you're not even a gnome");
				say(p, n, "it does give me a height advantage");
				npcsay(p, n, "i look forward to cheering you on");
				say(p, n, "the first goal's for you");
				npcsay(p, n, "wow!, thanks");
			}
		}
		else if (n.getID() == OFFICIAL) {
			say(p, n, "hello there");
			npcsay(p, n, "well hello adventurer, are you playing?");
			int option = multi(p, n, "not at the moment", "yes, i'm just having a break");
			if (option == 0) {
				npcsay(p, n, "well really you shouldn't be on the pitch",
						"some of these games get really rough");
				int sub_option = multi(p, n, "how do you play?", "it looks like a silly game anyway");
				if (sub_option == 0) {
					npcsay(p, n, "the gnomes in orange are on your team",
							"you then charge at the gnome defense and try to throw the ball..",
							"..through the net to the goal catcher, it's a rough game but fun",
							"it's also great way to improve your agility");
				} else if (option == 1) {
					npcsay(p, n, "gnome ball silly!, this my friend is the backbone of our community",
							"it also happens to be a great way to stay fit and agile");
				}
			} else if (option == 1) {
				npcsay(p, n, "good stuff, there's nothing like chasing a pigs bladder..",
						"..to remind one that they're alive");
			}
		}
		else if (n.getID() == REFEREE) {
			if (!p.getCache().hasKey("gnomeball")) {
				npcsay(p, n, "hi, welcome to gnome ball");
				say(p, n, "gnome ball?, how do you play?");
				npcsay(p, n, "it's pretty simple really, you take the ball from me",
						"charge at the gnome defense and try to throw the ball..",
						"..through the net to the goal catcher, it's a rough game but great fun",
						"it's also a great way to improve your agility",
						"so do you fancy a game?");
				int option = multi(p, n, "looks too dangerous for me", "ok then i'll have a go");
				if (option == 0) {
					npcsay(p, n, "you may be right, we've seen humans die on this field");
				} else if (option == 1) {
					npcsay(p, n, "great stuff",
							"there are no rules to gnome ball, so it can get a bit rough",
							"you can pass to the winger gnomes if your behind the start line",
							"otherwise, if you're feeling brave you, can just charge and dodge");
					say(p, n, "sounds easy enough");
					npcsay(p, n, "the main aim is to leave with no broken limbs",
							"i think you should be fine");
					p.getCache().store("gnomeball", true);
					npcsay(p, n, "ready ...  go");
					mes(p, 1200, "the ref throws the ball into the air", "you jump up and catch it");
					give(p, ItemId.GNOME_BALL.id(), 1);
				}
			} else {
				// player does not have ball
				if (!p.getCarriedItems().hasCatalogID(ItemId.GNOME_BALL.id(), Optional.of(false))) {
					loadIfNotMemory(p, "gnomeball_npc");
					// and neither does a gnome baller
					if (inArray(p.getAttribute("gnomeball_npc", -1), 0)) {
						p.setAttribute("throwing_ball_game", false);
						npcsay(p, n, "ready ...  go");
						mes(p, 1200, "the ref throws the ball into the air", "you jump up and catch it");
						give(p, ItemId.GNOME_BALL.id(), 1);
					}
					else {
						npcsay(p, n, "the ball's still in play");
					}
				} else {
					npcsay(p, n, "the ball's still in play");
				}
			}
		}
		else if (DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID())) {
			tackleGnomeBaller(p, n);
		}
	}

	private void loadIfNotMemory(Player p, String cacheName) {
		//load from player cache if not present in memory
		if((p.getAttribute(cacheName, -1) == -1) && p.getCache().hasKey(cacheName)) {
			p.setAttribute(cacheName, p.getCache().getInt(cacheName));
		} else if (p.getAttribute(cacheName, -1) == -1) {
			p.setAttribute(cacheName, 0);
		}
	}

	@Override
	public void onOpNpc(Npc n, String command, Player p) {
		if (n.getID() == GNOME_BALLER_NORTH || n.getID() == GNOME_BALLER_SOUTH) {
			Zone currentZone = GnomeField.getInstance().resolvePositionToZone(p);
			if (currentZone == GnomeField.Zone.ZONE_NO_PASS) {
				p.message("you can't make the pass from here");
			}
			else if (currentZone == GnomeField.Zone.ZONE_PASS) {
				if (!p.getCarriedItems().hasCatalogID(ItemId.GNOME_BALL.id(), Optional.of(false))) {
					p.message("you need the ball first");
				}
				else {
					p.setAttribute("throwing_ball_game", true);
					p.getWorld().getServer().getGameEventHandler().add(new BallProjectileEvent(p.getWorld(), p, n, 3) {
						@Override
						public void doSpell() {
						}
					});
					p.message("you pass the ball to the gnome");
					remove(p, ItemId.GNOME_BALL.id(), 1);
					npcsay(p, n, 100, "run long..");
					delay(5000);
					p.message("the gnome throws you a long ball");
					give(p, ItemId.GNOME_BALL.id(), 1);
					p.setAttribute("throwing_ball_game", false);
				}
			}
			else if (command.equals("pass to")) {
				if (!p.getCarriedItems().hasCatalogID(ItemId.GNOME_BALL.id(), Optional.of(false))) {
					p.message("you need the ball first");
				} else {
					p.message("you can't make the pass from here");
				}
			}
		} else if (DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID())) {
			tackleGnomeBaller(p, n);
		}
	}

	private void tackleGnomeBaller(Player p, Npc n) {
		loadIfNotMemory(p, "gnomeball_npc");
		// and neither does a gnome baller
		if (p.getAttribute("gnomeball_npc", -1) == 0 || n.getID() != p.getAttribute("gnomeball_npc", -1)) {
			p.message("the gnome isn't carrying the ball");
		}
		else {
			thinkbubble(p, new Item(ItemId.GNOME_BALL.id()));
			Functions.mes(p, "you attempt to tackle the gnome");
			if (DataConversions.random(0, 1) == 0) {
				//successful tackles gives agility xp
				p.playerServerMessage(MessageType.QUEST, "You skillfully grab the ball");
				p.playerServerMessage(MessageType.QUEST, "and push the gnome to the floor");
				npcsay(p, n, "grrrr");
				give(p, ItemId.GNOME_BALL.id(), 1);
				p.incExp(Skills.AGILITY, TACKLING_XP[DataConversions.random(0,1)], true);
				p.setAttribute("gnomeball_npc", 0);
			} else {
				p.playerServerMessage(MessageType.QUEST, "You're pushed away by the gnome");
				say(p, n, "ouch");
				p.damage((int)(Math.ceil(p.getSkills().getLevel(Skills.HITS)*0.05)));
				npcsay(p, n, "hee hee");
			}
		}
	}

	//this should only happens when player passes ball to gnome baller (team)
	@Override
	public void onIndirectTalkToNpc(Player p, Npc n) {
		if (n.getID() == GNOME_BALLER_NORTH || n.getID() == GNOME_BALLER_SOUTH) {
			//pass to -> direct use of command
			//pass -> passing via gnome ball's shoot (requires player to be in correct position)
			this.onOpNpc(n, "pass", p);
		}
	}

	//work around, technically these should be on command but wasnt being triggered
	@Override
	public boolean blockIndirectTalkToNpc(Player p, Npc n) {
		return n.getID() == GNOME_BALLER_NORTH || n.getID() == GNOME_BALLER_SOUTH;
	}

}
