package com.openrsc.server.plugins.authentic.minigames.gnomeball;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.rsc.impl.projectile.BallProjectileEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class GnomeNpcs implements AttackNpcTrigger, SpellNpcTrigger, PlayerRangeNpcTrigger, TalkNpcTrigger, OpNpcTrigger {

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
	public boolean blockPlayerRangeNpc(Player player, Npc n) {
		return DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID());
	}

	@Override
	public boolean blockSpellNpc(Player player, Npc n) {
		return DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID());
	}

	@Override
	public boolean blockAttackNpc(Player player, Npc n) {
		return DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID());
	}

	@Override
	public void onPlayerRangeNpc(Player player, Npc n) {
		if(DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID())) {
			mes("you can't attack this gnome");
			delay(2);
			mes("that's cheating");
			delay(2);
		}
	}

	@Override
	public void onSpellNpc(Player player, Npc n) {
		if(DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID())) {
			mes("you can't attack this gnome");
			delay(2);
			mes("that's cheating");
			delay(2);
		}
	}

	@Override
	public void onAttackNpc(Player player, Npc n) {
		if(DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID())) {
			mes("you can't attack this gnome");
			delay(2);
			mes("that's cheating");
			delay(2);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == CHEERLEADER || n.getID() == OFFICIAL || n.getID() == REFEREE
				|| DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID());
	}

	@Override
	public boolean blockOpNpc(Player player, Npc n, String command) {
		return n.getID() == GNOME_BALLER_NORTH || n.getID() == GNOME_BALLER_SOUTH
				|| DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == CHEERLEADER) {
			say(player, n, "hello");
			npcsay(player, n, "hi there, how are you doing?");
			say(player, n, "not bad thanks");
			npcsay(player, n, "i just love the big games",
					"all those big muscle bound gnomes running around");
			say(player, n, "big?");
			npcsay(player, n, "do you play gnome ball?");
			int option = multi(player, n, false, //do not send over
					"what is it?", "play! i'm a gnome ball master");
			if (option == 0) {
				say(player, n, "what is it?");
				npcsay(player, n, "like, only the greatest gnome ball game ever made!");
				say(player, n, "are there many gnome ball games");
				npcsay(player, n, "no, there's just one",
						"and it's the best");
				say(player, n, "ok, so how do you play?");
				npcsay(player, n, "the attacker gets the ball and runs towards the goal net");
				say(player, n, "and...?");
				npcsay(player, n, "scores of course");
				say(player, n, "sounds easy enough");
				npcsay(player, n, "you'll be playing against the best defenders in the gnome ball league");
				say(player, n, "really, are there many teams in the league?");
				npcsay(player, n, "nope, just us!");
			} else if (option == 1) {
				say(player, n, "play! i'm a gnome ball master?");
				npcsay(player, n, "really, that's amazing, you're not even a gnome");
				say(player, n, "it does give me a height advantage");
				npcsay(player, n, "i look forward to cheering you on");
				say(player, n, "the first goal's for you");
				npcsay(player, n, "wow!, thanks");
			}
		}
		else if (n.getID() == OFFICIAL) {
			say(player, n, "hello there");
			npcsay(player, n, "well hello adventurer, are you playing?");
			int option = multi(player, n, "not at the moment", "yes, i'm just having a break");
			if (option == 0) {
				npcsay(player, n, "well really you shouldn't be on the pitch",
						"some of these games get really rough");
				int sub_option = multi(player, n, "how do you play?", "it looks like a silly game anyway");
				if (sub_option == 0) {
					npcsay(player, n, "it's easy, you're given a ball from the ref",
							"the gnomes in orange are on your team",
							"you then charge at the gnome defense and try to throw the ball..",
							"..through the net to the goal catcher, it's a rough game but great fun",
							"it's also a great way to improve your agility");
				} else if (option == 1) {
					npcsay(player, n, "gnome ball silly!, this my friend is the backbone of our community",
							"it also happens to be a great way to stay fit and agile");
				}
			} else if (option == 1) {
				npcsay(player, n, "good stuff, there's nothing like chasing a pigs bladder..",
						"..to remind one that they're alive");
			}
		}
		else if (n.getID() == REFEREE) {
			if (!player.getCache().hasKey("gnomeball")) {
				npcsay(player, n, "hi, welcome to gnome ball");
				say(player, n, "gnome ball?, how do you play?");
				npcsay(player, n, "it's pretty simple really, you take the ball from me",
						"charge at the gnome defense and try to throw the ball..",
						"..through the net to the goal catcher, it's a rough game but great fun",
						"it's also a great way to improve your agility",
						"so do you fancy a game?");
				int option = multi(player, n, "looks too dangerous for me", "ok then i'll have a go");
				if (option == 0) {
					npcsay(player, n, "you may be right, we've seen humans die on this field");
				} else if (option == 1) {
					npcsay(player, n, "great stuff",
							"there are no rules to gnome ball, so it can get a bit rough",
							"you can pass to the winger gnomes if your behind the start line",
							"then you can make a run and they'll pass back",
							"otherwise, if you're feeling brave you, can just charge and dodge");
					say(player, n, "sounds easy enough");
					npcsay(player, n, "the main aim is to leave with no broken limbs",
							"i think you should be fine");
					player.getCache().store("gnomeball", true);
					npcsay(player, n, "ready ...  go");
					mes("the ref throws the ball into the air");
					delay(2);
					mes("you jump up and catch it");
					delay(2);
					give(player, ItemId.GNOME_BALL.id(), 1);
				}
			} else {
				// player does not have ball
				if (!player.getCarriedItems().hasCatalogID(ItemId.GNOME_BALL.id(), Optional.of(false))) {
					loadIfNotMemory(player, "gnomeball_npc");
					// and neither does a gnome baller
					if (inArray(player.getAttribute("gnomeball_npc", -1), 0)) {
						player.setAttribute("throwing_ball_game", false);
						npcsay(player, n, "ready ...  go");
						mes("the ref throws the ball into the air");
						delay(2);
						mes("you jump up and catch it");
						delay(2);
						give(player, ItemId.GNOME_BALL.id(), 1);
					}
					else {
						npcsay(player, n, "the ball's still in play");
					}
				} else {
					npcsay(player, n, "the ball's still in play");
				}
			}
		}
		else if (DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID())) {
			tackleGnomeBaller(player, n);
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

	@Override
	public void onOpNpc(Player player, Npc n, String command) {
		Npc baller = player.getWorld().getNpc(n.getID(),
			player.getX() - 2, player.getX() + 2,
			player.getY() - 2, player.getY() + 2);
		if (baller == null) return;
		if (n.getID() == GNOME_BALLER_NORTH || n.getID() == GNOME_BALLER_SOUTH) {
			if(command.equalsIgnoreCase("pass to")) {
				passToTeam(player, n);
			}
		} else if (DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID())) {
			tackleGnomeBaller(player, n);
		}
	}

	protected static void passToTeam(Player player, Npc n) {
		GnomeField.Zone currentZone = GnomeField.getInstance().resolvePositionToZone(player);
		if (currentZone == GnomeField.Zone.ZONE_NO_PASS) {
			player.message("you can't make the pass from here");
		}
		else if (currentZone == GnomeField.Zone.ZONE_PASS) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.GNOME_BALL.id(), Optional.of(false))) {
				player.message("you need the ball first");
			}
			else {
				player.setAttribute("throwing_ball_game", true);
				player.getWorld().getServer().getGameEventHandler().add(new BallProjectileEvent(player.getWorld(), player, n, 3) {
					@Override
					public void doSpell() {
					}
				});
				player.message("you pass the ball to the gnome");
				player.getCarriedItems().remove(new Item(ItemId.GNOME_BALL.id()));
				npcsay(player, n, "run long..");
				player.getWorld().getServer().getGameEventHandler().add(
					new SingleEvent(player.getWorld(), player, config().GAME_TICK * 8, "Gnome Ball Pass Event") {
						@Override
						public void action() {
							getOwner().message("the gnome throws you a long ball");
							give(getOwner(), ItemId.GNOME_BALL.id(), 1);
							getOwner().setAttribute("throwing_ball_game", false);
						}
					}
				);
			}
		}
		else {
			if (!player.getCarriedItems().hasCatalogID(ItemId.GNOME_BALL.id(), Optional.of(false))) {
				player.message("you need the ball first");
			} else {
				player.message("you can't make the pass from here");
			}
		}
	}

	private void tackleGnomeBaller(Player player, Npc n) {
		loadIfNotMemory(player, "gnomeball_npc");
		// and neither does a gnome baller
		if (player.getAttribute("gnomeball_npc", -1) == 0 || n.getID() != player.getAttribute("gnomeball_npc", -1)) {
			player.message("the gnome isn't carrying the ball");
		}
		else {
			thinkbubble(new Item(ItemId.GNOME_BALL.id()));
			mes("you attempt to tackle the gnome");
			delay(3);
			if (DataConversions.random(0, 1) == 0) {
				//successful tackles gives agility xp
				player.playerServerMessage(MessageType.QUEST, "You skillfully grab the ball");
				player.playerServerMessage(MessageType.QUEST, "and push the gnome to the floor");
				npcsay(player, n, "grrrr");
				give(player, ItemId.GNOME_BALL.id(), 1);
				player.incExp(Skill.AGILITY.id(), TACKLING_XP[DataConversions.random(0,1)], true);
				player.setAttribute("gnomeball_npc", 0);
				n.setCombatTimer(-player.getConfig().GAME_TICK * 4);
			} else {
				player.playerServerMessage(MessageType.QUEST, "You're pushed away by the gnome");
				say(player, n, "ouch");
				player.damage((int)(Math.ceil(player.getSkills().getLevel(Skill.HITS.id())*0.05)));
				npcsay(player, n, "hee hee");
			}
		}
	}
}
