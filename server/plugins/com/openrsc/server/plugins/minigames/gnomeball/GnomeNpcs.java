package com.openrsc.server.plugins.minigames.gnomeball;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showBubble;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;

import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.impl.BallProjectileEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.IndirectTalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.NpcCommandListener;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerMageNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerRangeNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.IndirectTalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.NpcCommandExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerRangeNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.minigames.gnomeball.GnomeField.Zone;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

public class GnomeNpcs implements PlayerAttackNpcListener, PlayerAttackNpcExecutiveListener, PlayerMageNpcListener, PlayerMageNpcExecutiveListener, PlayerRangeNpcListener, PlayerRangeNpcExecutiveListener,
TalkToNpcListener, TalkToNpcExecutiveListener, NpcCommandListener, NpcCommandExecutiveListener, IndirectTalkToNpcListener, IndirectTalkToNpcExecutiveListener {
	
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
	public boolean blockPlayerMageNpc(Player p, Npc n) {
		return DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID());
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
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
			message(p, 1200, "you can't attack this gnome", "that's cheating");
		}
	}
	
	@Override
	public void onPlayerMageNpc(Player p, Npc n) {
		if(DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID())) {
			message(p, 1200, "you can't attack this gnome", "that's cheating");
		}
	}

	@Override
	public void onPlayerAttackNpc(Player p, Npc n) {
		if(DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID())) {
			message(p, 1200, "you can't attack this gnome", "that's cheating");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == CHEERLEADER || n.getID() == OFFICIAL || n.getID() == REFEREE
				|| DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID());
	}
	
	@Override
	public boolean blockNpcCommand(Npc n, String command, Player p) {
		return n.getID() == GNOME_BALLER_NORTH || n.getID() == GNOME_BALLER_SOUTH 
				|| DataConversions.inArray(GNOME_BALLERS_ZONE_PASS, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE2XP_OUTER, n.getID())
				|| DataConversions.inArray(GNOME_BALLERS_ZONE1XP_INNER, n.getID());
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == CHEERLEADER) {
			playerTalk(p, n, "hello");
			npcTalk(p, n, "hi there, how are you doing?");
			playerTalk(p, n, "not bad thanks");
			npcTalk(p, n, "i just love the big games",
					"all those big muscle bound gnomes running around");
			playerTalk(p, n, "big?");
			npcTalk(p, n, "do you play gnome ball?");
			int option = showMenu(p, n, false, //do not send over
					"what is it?", "play! i'm a gnome ball master");
			if (option == 0) {
				playerTalk(p, n, "what is it?");
				npcTalk(p, n, "like, only the greatest gnome ball game ever made!");
				playerTalk(p, n, "are there many gnome ball games");
				npcTalk(p, n, "no, there's just one",
						"and it's the best");
				playerTalk(p, n, "ok, so how do you play?");
				npcTalk(p, n, "the attacker gets the ball and runs towards the goal net");
				playerTalk(p, n, "and...?");
				npcTalk(p, n, "scores of course");
				playerTalk(p, n, "sounds easy enough");
				npcTalk(p, n, "you'll be playing against the best defenders in the gnome ball league");
				playerTalk(p, n, "really, are there many teams in the league?");
				npcTalk(p, n, "nope, just us!");
			} else if (option == 1) {
				playerTalk(p, n, "play! i'm a gnome ball master?");
				npcTalk(p, n, "really, that's amazing, you're not even a gnome");
				playerTalk(p, n, "it does give me a height advantage");
				npcTalk(p, n, "i look forward to cheering you on");
				playerTalk(p, n, "the first goal's for you");
				npcTalk(p, n, "wow!, thanks");
			}
		}
		else if (n.getID() == OFFICIAL) {
			playerTalk(p, n, "hello there");
			npcTalk(p, n, "well hello adventurer, are you playing?");
			int option = showMenu(p, n, "not at the moment", "yes, i'm just having a break");
			if (option == 0) {
				npcTalk(p, n, "well really you shouldn't be on the pitch",
						"some of these games get really rough");
				int sub_option = showMenu(p, n, "how do you play?", "it looks like a silly game anyway");
				if (sub_option == 0) {
					npcTalk(p, n, "the gnomes in orange are on your team",
							"you then charge at the gnome defense and try to throw the ball..",
							"..through the net to the goal catcher, it's a rough game but fun",
							"it's also great way to improve your agility");
				} else if (option == 1) {
					npcTalk(p, n, "gnome ball silly!, this my friend is the backbone of our community",
							"it also happens to be a great way to stay fit and agile");
				}
			} else if (option == 1) {
				npcTalk(p, n, "good stuff, there's nothing like chasing a pigs bladder..",
						"..to remind one that they're alive");
			}
		}
		else if (n.getID() == REFEREE) {
			if (!p.getCache().hasKey("gnomeball")) {
				npcTalk(p, n, "hi, welcome to gnome ball");
				playerTalk(p, n, "gnome ball?, how do you play?");
				npcTalk(p, n, "it's pretty simple really, you take the ball from me",
						"charge at the gnome defense and try to throw the ball..",
						"..through the net to the goal catcher, it's a rough game but great fun",
						"it's also a great way to improve your agility",
						"so do you fancy a game?");
				int option = showMenu(p, n, "looks too dangerous for me", "ok then i'll have a go");
				if (option == 0) {
					npcTalk(p, n, "you may be right, we've seen humans die on this field");
				} else if (option == 1) {
					npcTalk(p, n, "great stuff",
							"there are no rules to gnome ball, so it can get a bit rough",
							"you can pass to the winger gnomes if your behind the start line",
							"otherwise, if you're feeling brave you, can just charge and dodge");
					playerTalk(p, n, "sounds easy enough");
					npcTalk(p, n, "the main aim is to leave with no broken limbs",
							"i think you should be fine");
					p.getCache().store("gnomeball", true);
					npcTalk(p, n, "ready ...  go");
					message(p, 1200, "the ref throws the ball into the air", "you jump up and catch it");
					addItem(p, ItemId.GNOME_BALL.id(), 1);
				}
			} else {
				// player does not have ball
				if (!hasItem(p, ItemId.GNOME_BALL.id())) {
					loadIfNotMemory(p, "gnomeball_npc");
					// and neither does a gnome baller
					if (inArray(p.getSyncAttribute("gnomeball_npc", -1), 0)) {
						p.setSyncAttribute("throwing_ball_game", false);
						npcTalk(p, n, "ready ...  go");
						message(p, 1200, "the ref throws the ball into the air", "you jump up and catch it");
						addItem(p, ItemId.GNOME_BALL.id(), 1);
					}
					else {
						npcTalk(p, n, "the ball's still in play");
					}
				} else {
					npcTalk(p, n, "the ball's still in play");
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
		if((p.getSyncAttribute(cacheName, -1) == -1) && p.getCache().hasKey(cacheName)) {
			p.setSyncAttribute(cacheName, p.getCache().getInt(cacheName));
		} else if (p.getSyncAttribute(cacheName, -1) == -1) {
			p.setSyncAttribute(cacheName, 0);
		}
	}

	@Override
	public void onNpcCommand(Npc n, String command, Player p) {
		if (n.getID() == GNOME_BALLER_NORTH || n.getID() == GNOME_BALLER_SOUTH) {
			Zone currentZone = GnomeField.getInstance().resolvePositionToZone(p);
			if (currentZone == GnomeField.Zone.ZONE_NO_PASS) {
				p.message("you can't make the pass from here");
			}
			else if (currentZone == GnomeField.Zone.ZONE_PASS) {
				if (!hasItem(p, ItemId.GNOME_BALL.id())) {
					p.message("you need the ball first");
				}
				else {
					p.setSyncAttribute("throwing_ball_game", true);
					Server.getServer().getGameEventHandler().add(new BallProjectileEvent(p, n, 3) {
						@Override
						public void doSpell() {
						}
					});
					p.message("you pass the ball to the gnome");
					removeItem(p, ItemId.GNOME_BALL.id(), 1);
					npcTalk(p, n, 100, "run long..");
					sleep(5000);
					p.message("the gnome throws you a long ball");
					addItem(p, ItemId.GNOME_BALL.id(), 1);
					p.setSyncAttribute("throwing_ball_game", false);
				}
			}
			else if (command.equals("pass to")) {
				if (!hasItem(p, ItemId.GNOME_BALL.id())) {
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
		if (p.getSyncAttribute("gnomeball_npc", -1) == 0 || n.getID() != p.getSyncAttribute("gnomeball_npc", -1)) {
			p.message("the gnome isn't carrying the ball");
		}
		else {
			showBubble(p, new Item(ItemId.GNOME_BALL.id()));
			message(p, "you attempt to tackle the gnome");
			if (DataConversions.random(0, 1) == 0) {
				//successful tackles gives agility xp
				p.playerServerMessage(MessageType.QUEST, "You skillfully grab the ball");
				p.playerServerMessage(MessageType.QUEST, "and push the gnome to the floor");
				npcTalk(p, n, "grrrr");
				addItem(p, ItemId.GNOME_BALL.id(), 1);
				p.incExp(SKILLS.AGILITY.id(), TACKLING_XP[DataConversions.random(0,1)], true);
				p.setSyncAttribute("gnomeball_npc", 0);
			} else {
				p.playerServerMessage(MessageType.QUEST, "You're pushed away by the gnome");
				playerTalk(p, n, "ouch");
				p.damage((int)(Math.ceil(p.getSkills().getLevel(SKILLS.HITS.id())*0.05)));
				npcTalk(p, n, "hee hee");
			}
		}
	}

	//this should only happens when player passes ball to gnome baller (team)
	@Override
	public void onIndirectTalkToNpc(Player p, Npc n) {
		if (n.getID() == GNOME_BALLER_NORTH || n.getID() == GNOME_BALLER_SOUTH) {
			//pass to -> direct use of command
			//pass -> passing via gnome ball's shoot (requires player to be in correct position)
			this.onNpcCommand(n, "pass", p);
		}
	}

	//work around, technically these should be on command but wasnt being triggered
	@Override
	public boolean blockIndirectTalkToNpc(Player p, Npc n) {
		return n.getID() == GNOME_BALLER_NORTH || n.getID() == GNOME_BALLER_SOUTH;
	}

}
