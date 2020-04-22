package com.openrsc.server.plugins.minigames.mage_arena;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Minigames;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.rsc.impl.ObjectRemover;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.triggers.*;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class MageArena implements MiniGameInterface, TalkNpcTrigger, KillNpcTrigger, OpLocTrigger, TakeObjTrigger, SpellNpcTrigger, AttackNpcTrigger, PlayerDeathTrigger {

	public static final int SARADOMIN_STONE = 1152;
	public static final int GUTHIX_STONE = 1153;
	public static final int ZAMORAK_STONE = 1154;

	@Override
	public int getMiniGameId() {
		return Minigames.MAGE_ARENA;
	}

	@Override
	public String getMiniGameName() {
		return "Mage Arena (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		//mini-quest complete handled already
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (getMaxLevel(player, Skills.MAGIC) < 60) { // TODO: Enter the arena game.
			say(player, n, "hello there", "what is this place?");
			npcsay(player, n, "do not waste my time with trivial questions!",
				"i am the great kolodion, master of battle magic", "i have an arena to run");
			say(player, n, "can i enter?");
			npcsay(player, n, "hah, a wizard of your level..don't be absurd");
		} else if (player.getCache().hasKey("mage_arena")) {
			int stage = player.getCache().getInt("mage_arena");
			/* Started but failed. */
			if (stage == 1) {
				say(player, n, "hi");
				npcsay(player, n, "you return young conjurer..", "..you obviously have a taste for the darkside of magic",
					"let us continue with the battle...now");
				if (cantGo(player)) {
					mes(player, "You cannot enter the arena...", "...while carrying weapons or armour");
					return;
				}
				teleport(player, 229, 130);
				setCurrentLevel(player, 0, 0);
				setCurrentLevel(player, 2, 0);
				spawnKolodion(player, player.getCache().getInt("kolodion_stage"));
				startKolodionEvent(player);

			} else if (stage >= 2) {
				say(player, n, "hello kolodion");
				npcsay(player, n, "hey there, how are you?, enjoying the bloodshed?");
				say(player, n, "it's not bad, i've seen worse");
				int menu = multi(player, n,
					"i think i've had enough for now",
					"how can i use my new spells outside of the arena?");
				if (menu == 0) {
					npcsay(player, n, "shame , you're a good battle mage",
						"hope to see you soon");
				} else if (menu == 1) {
					npcsay(player, n, "experience my friend, experience",
						"once you've used the spell enough times in the arena...",
						"...you'll be able to use them in the rest of runescape");
					say(player, n, "good stuff");
					npcsay(player, n, "not so good for the citizens, they won't stand a chance");
					say(player, n, "how am i doing so far?");
					if (player.getCache().hasKey("Saradomin strike_casts") && player.getCache().getInt("Saradomin strike_casts") >= 100) {
						npcsay(player, n, "you're fully trained to use the strike spell anywhere");
					} else {
						npcsay(player, n, "you still need to train with the strike spell...",
							"...inside the arena before you can use it outside");
					}
					if (player.getCache().hasKey("Claws of Guthix_casts") && player.getCache().getInt("Claws of Guthix_casts") >= 100) {
						npcsay(player, n, "you're fully trained to use the claw spell anywhere");
					} else {
						npcsay(player, n, "you still need to train with the claw spell...",
							"...inside the arena before you can use it outside");
					}
					if (player.getCache().hasKey("Flames of Zamorak_casts") && player.getCache().getInt("Flames of Zamorak_casts") >= 100) {
						npcsay(player, n, "you're fully trained to use the flame spell anywhere");
					} else {
						npcsay(player, n, "you still need to train with the flame spell...",
							"...inside the arena before you can use it outside");
					}
				}
			}
		} else {
			say(player, n, "hello there",
				"what is this place?");
			npcsay(player, n, "i am the great kolodion, master of battle magic ...",
				"... and this is my battle arena",
				"top wizards travel from all over to fight here");
			int choice = multi(player, n, "can i fight here?", "what's the point of that?", "that's barbaric");
			if (choice == 0) {
				canifight(player, n);
			} else if (choice == 1) {
				whatsthepoint(player, n);
			} else if (choice == 2) {
				barbaric(player, n);
			}
		}
	}

	public void canifight(Player player, Npc n) {
		npcsay(player, n, "my arena is open to any high level wizard",
				"but this is no game traveller, wizards fall in this arena..",
				"..never to rise again, the strongest of mage's have been destroyed",
				"but if you're sure you want in?");
		int choice = multi(player, n, "yes indeedy", "no, i don't");
		if (choice == 0) {
			joinfight(player, n);
		} else if (choice == 1) {
			npcsay(player, n, "your loss");
		}
	}

	public void whatsthepoint(Player player, Npc n) {
		npcsay(player, n, "we learn how to use our magic to it fullest...",
			"..,how to channel forces of the cosmos into our world..",
			"..,but mainly I just like blasting people into dust");
		int choice = multi(player, n, "can i fight here?", "that's barbaric");
		if (choice == 0) {
			canifight(player, n);
		} else if (choice == 1) {
			barbaric(player, n);
		}
	}

	public void barbaric(Player player, Npc n) {
		npcsay(player, n, "nope, it's magic, but I know what you mean",
				"so do you want to join us?");
		int choice = multi(player, n, "yes indeedy", "no, i don't");
		if (choice == 0) {
			joinfight(player, n);
		} else if (choice == 1) {
			npcsay(player, n, "your loss");
		}
	}

	public void joinfight(Player player, Npc n) {
		npcsay(player, n, "good..good, you have a healthy sense of competition",
			"remember traveller in my arena hand to hand combat is useless",
			"your strength will diminish as you enter the arena",
			"but the spells you can learn are amongst the most powerful in runescape",
			"before i can accept you in, we must duel",
			"you may not take armour or weapons into the arena");
		if (cantGo(player)) {
			mes(player, "You cannot enter the arena...", "...while carrying weapons or armour");
		}
		else {
			int choice = multi(player, n, "ok let's fight", "no thanks");
			if (choice == 0) {
				npcsay(player, n, "I must check that you're up to scratch");
				say(player, n, "you don't need to worry about that");
				npcsay(player, n, "not just any magician can enter traveller",
						"only the most powerful, the most feared",
						"before you use the power of this arena",
						"you must prove yourself against me",
						"now!");
				if (!player.getCache().hasKey("mage_arena")) {
					player.getCache().set("mage_arena", 1);
				}
				teleport(player, 229, 130);
				setCurrentLevel(player, Skills.ATTACK, 0);
				setCurrentLevel(player, Skills.STRENGTH, 0);

				startKolodionEvent(player);
				spawnKolodion(player, NpcId.KOLODION_HUMAN.id());
				delay(player.getWorld().getServer().getConfig().GAME_TICK);
			} else if (choice == 1) {
				npcsay(player, n, "your loss");
			}
		}
	}

	public void learnSpellEvent(Player player) {
		DelayedEvent mageArena = player.getAttribute("mageArenaEvent", null);
		DelayedEvent mageArenaEvent = new DelayedEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK * 3, "Mage Arena Learn Spell Event") {
			@Override
			public void run() {
				/* Player logged out. */
				if (!getOwner().isLoggedIn() || getOwner().isRemoved()) {
					stop();
					return;
				}
				if (!getOwner().getLocation().inMageArena()) {
					stop();
					return;
				}
				if (getOwner().inCombat()) {
					return;
				}
				if (getOwner().getSkills().getLevel(Skills.ATTACK) > 0 || getOwner().getSkills().getLevel(Skills.STRENGTH) > 0) {
					getOwner().getSkills().setLevel(Skills.ATTACK, 0);
					getOwner().getSkills().setLevel(Skills.STRENGTH, 0);
				}
				Npc Guthix = ifnearvisnpc(player, NpcId.BATTLE_MAGE_GUTHIX.id(), 2);
				Npc Zamorak = ifnearvisnpc(player, NpcId.BATTLE_MAGE_ZAMAROK.id(), 2);
				Npc Saradomin = ifnearvisnpc(player, NpcId.BATTLE_MAGE_SARADOMIN.id(), 2);
				String[] randomMessage = {"@yel@zamorak mage: feel the wrath of zamarok", "@yel@Saradomin mage: feel the wrath of Saradomin", "@yel@guthix mage: feel the wrath of guthix"};
				if (Guthix != null && Guthix.withinRange(getOwner(), 1)) {
					godSpellObject(getOwner(), 33);
					player.message(randomMessage[2]);
					if (getCurrentLevel(getOwner(), Skills.HITS) < 20) {
						getOwner().damage(2);
					} else {
						getOwner().damage(getCurrentLevel(getOwner(), Skills.HITS) / 10);
					}
				} else if (Zamorak != null && Zamorak.withinRange(getOwner(), 1)) {
					godSpellObject(getOwner(), 35);
					player.message(randomMessage[0]);
					if (getCurrentLevel(getOwner(), Skills.HITS) < 20) {
						getOwner().damage(2);
					} else {
						getOwner().damage(getCurrentLevel(getOwner(), Skills.HITS) / 10);
					}
				} else if (Saradomin != null && Saradomin.withinRange(getOwner(), 1)) {
					godSpellObject(getOwner(), 34);
					player.message(randomMessage[1]);
					if (getCurrentLevel(getOwner(), Skills.HITS) < 20) {
						getOwner().damage(2);
					} else {
						getOwner().damage(getCurrentLevel(getOwner(), Skills.HITS) / 10);
					}
				}
			}
		};
		if (mageArena != null) {
			if (mageArena.shouldRemove()) {
				player.setAttribute("mageArenaEvent", mageArenaEvent);
				player.getWorld().getServer().getGameEventHandler().add(mageArenaEvent);
			}
		} else {
			player.setAttribute("mageArenaEvent", mageArenaEvent);
			player.getWorld().getServer().getGameEventHandler().add(mageArenaEvent);
		}
	}

	private void startKolodionEvent(Player player) {
		DelayedEvent kolE = player.getAttribute("kolodionEvent", null);
		DelayedEvent kolodionEvent = new DelayedEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK, "Mage Arena Kolodion Event") {
			@Override
			public void run() {
				Npc npc = getOwner().getAttribute("spawned_kolodion");
				if (npc == null) {
					return;
				}
				/* Player logged out. */
				if (!getOwner().isLoggedIn() || getOwner().isRemoved()) {
					npc.remove();
					stop();
					return;
				}
				/* Npc has been removed from the world. */
				if (!player.getWorld().hasNpc(npc)) {
					stop();
					return;
				}
				/* Player has left the area */
				if (!npc.withinRange(getOwner())) {
					npc.remove();
					stop();
					return;
				}
				if (getOwner().inCombat()) {
					return;
				}
				if (!npc.withinRange(getOwner(), 8)) {
					return;
				}
				if (random(0, 5) != 2) {
					return;
				}
				int spell_type = random(0, 2);
				switch (spell_type) {
					case 0:
						godSpellObject(getOwner(), 33);
						break;
					case 1:
						godSpellObject(getOwner(), 34);
						break;
					case 2:
						godSpellObject(getOwner(), 35);
						break;
				}
				String[] randomMessage = {"roooaar", "die you foolish mortal", "feel the power of the elements", "the bigger the better", "aaarrgghhh"};
				npcYell(getOwner(), npc, randomMessage[random(0, randomMessage.length - 1)]);
				getOwner().damage(random(3, 13));

			}
		};
		if (kolE != null) {
			if (kolE.shouldRemove()) {
				player.setAttribute("kolodionEvent", kolodionEvent);
				player.getWorld().getServer().getGameEventHandler().add(kolodionEvent);
			}
		} else {
			player.setAttribute("kolodionEvent", kolodionEvent);
			player.getWorld().getServer().getGameEventHandler().add(kolodionEvent);
		}
	}

	public void spawnKolodion(Player player, int id) {
		player.setAttribute("spawned_kolodion", addnpc(id, 227, 130, 300000, player));
		player.getCache().set("kolodion_stage", id);
		player.message("kolodion blasts you " + (id == NpcId.KOLODION_HUMAN.id() ? "with his staff" : "again"));
		player.damage(random(7, 15));
		startKolodionEvent(player);
		ActionSender.sendTeleBubble(player, player.getX(), player.getY(), true);
	}

	private boolean cantGo(Player player) {
		synchronized(player.getCarriedItems().getInventory().getItems()) {
			for (Item item : player.getCarriedItems().getInventory().getItems()) {
				String name = item.getDef(player.getWorld()).getName().toLowerCase();
				if (name.contains("dagger") || name.contains("scimitar") || name.contains("bow") || name.contains("mail")
					|| (name.contains("sword") && !name.equalsIgnoreCase("Swordfish")
					&& !name.equalsIgnoreCase("Burnt Swordfish") && !name.equalsIgnoreCase("Raw Swordfish"))
					|| name.contains("mace") || name.contains("helmet") || name.contains("axe")
					|| name.contains("arrow") || name.contains("bow") || name.contains("spear")
					|| name.contains("battlestaff")) {

					return true;
				}
			}
			return false;
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.KOLODION.id();
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return inArray(n.getID(), NpcId.KOLODION_HUMAN.id(), NpcId.KOLODION_OGRE.id(), NpcId.KOLODION_SPIDER.id(),
				NpcId.KOLODION_SOULESS.id(), NpcId.KOLODION_DEMON.id());
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (inArray(n.getID(), NpcId.KOLODION_HUMAN.id(), NpcId.KOLODION_OGRE.id(), NpcId.KOLODION_SPIDER.id(),
				NpcId.KOLODION_SOULESS.id(), NpcId.KOLODION_DEMON.id())) {
			n.remove();

			if (n.getID() == NpcId.KOLODION_HUMAN.id()) {
				mes(player, "kolodion slumps to the floor..");
				mes(player, "..his body begins to grow and he changes form", "He becomes an intimidating ogre");
				spawnKolodion(player, NpcId.KOLODION_OGRE.id());
			} else if (n.getID() == NpcId.KOLODION_OGRE.id()) {
				mes(player, "kolodion slumps to the floor once more..",
					"..but again his body begins to grow and he changes form", "He becomes an enormous spider");
				spawnKolodion(player, NpcId.KOLODION_SPIDER.id());
			} else if (n.getID() == NpcId.KOLODION_SPIDER.id()) {
				mes(player, "kolodion again slumps to the floor..",
					"..but again his body begins to grow as he changes form", "He becomes an ethereal being");
				spawnKolodion(player, NpcId.KOLODION_SOULESS.id());
			} else if (n.getID() == NpcId.KOLODION_SOULESS.id()) {
				mes(player, "kolodion again slumps to the floor..motionless",
					"..but again his body begins to grow as he changes form", "...larger this time",
					"He becomes a vicious demon");
				spawnKolodion(player, NpcId.KOLODION_DEMON.id());
			} else if (n.getID() == NpcId.KOLODION_DEMON.id()) {
				mes(player, "kolodion again slumps to the floor..motionless", "..he slowly rises to his feet in his true form");
				mes(player, "@yel@Kolodion: \"well done young adventurer\"",
					"@yel@Kolodion: \"you truly are a worthy battle mage\"");
				player.message("kolodion teleports you to his cave");
				player.teleport(446, 3370);
				Npc kolodion = ifnearvisnpc(player, NpcId.KOLODION.id(), 5);
				if (kolodion == null) {
					player.message("kolodion is currently busy");
					return;
				}
				say(player, kolodion, "what now kolodion? how can i learn some of those spells?");
				npcsay(player, kolodion, "these spells are gifts from the gods", "first you must choose which god...",
					"...you will represent in the mage arena");
				say(player, kolodion, "cool");
				npcsay(player, kolodion, "step into the magic pool, it will carry you to the chamber");
				say(player, kolodion, "the chamber?");

				npcsay(player, kolodion, "there you must decide your loyalty");
				say(player, kolodion, "ok kolodion , thanks for the battle");
				npcsay(player, kolodion, "remember young mage, you must use the spells...",
					"...many times in the arena before you can use them outside");
				say(player, kolodion, "no problem");
				player.getCache().set("mage_arena", 2);
				player.getCache().remove("kolodion_stage");
			}
		}
	}

	@Override
	public void onSpellNpc(Player player, Npc n) {
		if (inArray(n.getID(), NpcId.KOLODION_HUMAN.id(), NpcId.KOLODION_OGRE.id(), NpcId.KOLODION_SPIDER.id(),
			NpcId.KOLODION_SOULESS.id(), NpcId.KOLODION_DEMON.id())) {
			if (!n.getAttribute("spawnedFor", null).equals(player)) {
				player.message("that mage is busy.");
			}
		}
	}

	@Override
	public boolean blockSpellNpc(final Player player, final Npc n) {
		if (inArray(n.getID(), NpcId.KOLODION_HUMAN.id(), NpcId.KOLODION_OGRE.id(), NpcId.KOLODION_SPIDER.id(),
				NpcId.KOLODION_SOULESS.id(), NpcId.KOLODION_DEMON.id())) {
			if (!n.getAttribute("spawnedFor", null).equals(player)) {
				return true;
			}
		}
		return false;
	}

	public void godSpellObject(Mob affectedMob, int spell) {
		switch (spell) {
			case 33:
				GameObject guthix = new GameObject(affectedMob.getWorld(), affectedMob.getLocation(), 1142, 0, 0);
				affectedMob.getWorld().registerGameObject(guthix);
				affectedMob.getWorld().getServer().getGameEventHandler().add(new ObjectRemover(affectedMob.getWorld(), guthix, 2));
				break;
			case 34:
				GameObject sara = new GameObject(affectedMob.getWorld(), affectedMob.getLocation(), 1031, 0, 0);
				affectedMob.getWorld().registerGameObject(sara);
				affectedMob.getWorld().getServer().getGameEventHandler().add(new ObjectRemover(affectedMob.getWorld(), sara, 2));
				break;
			case 35:
				GameObject zammy = new GameObject(affectedMob.getWorld(), affectedMob.getLocation(), 1036, 0, 0);
				affectedMob.getWorld().registerGameObject(zammy);
				affectedMob.getWorld().getServer().getGameEventHandler().add(new ObjectRemover(affectedMob.getWorld(), zammy, 2));
				break;
			case 47:
				GameObject charge = new GameObject(affectedMob.getWorld(), affectedMob.getLocation(), 1147, 0, 0);
				affectedMob.getWorld().registerGameObject(charge);
				affectedMob.getWorld().getServer().getGameEventHandler().add(new ObjectRemover(affectedMob.getWorld(), charge, 2));
				break;
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player player) {
		return obj.getID() == 1019 || obj.getID() == 1020 || obj.getID() == 1027
			|| obj.getID() == SARADOMIN_STONE || obj.getID() == GUTHIX_STONE || obj.getID() == ZAMORAK_STONE;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player player) {
		boolean firstTimeEnchant = false;
		if (obj.getID() == 1019 || obj.getID() == 1020) {
			player.message("you open the gate ...");
			player.message("... and walk through");
			doGate(player, obj);
			if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") == 4) {
				learnSpellEvent(player);
			}
		} else if (obj.getID() == 1027) {
			if (player.getY() >= 120) {
				player.message("you pass through the mystical barrier");
				teleport(player, 228, 118);
				Npc kolodion = player.getAttribute("spawned_kolodion", null);
				if (kolodion != null) {
					kolodion.remove();
				}
			} else {
				if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") >= 4) {
					mes(player, "the barrier is checking your person for weapons");
					if (!cantGo(player)) {
						teleport(player, 228, 120);
					} else {
						mes(player, "You cannot enter the arena...", "...while carrying weapons or armour");
					}
				} else {
					player.message("you cannot enter without the permission of kolodion");
				}
			}
		} else if (obj.getID() == SARADOMIN_STONE) {
			if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") >= 3) {
				mes(player, "you kneel and chant to saradomin");
				if (!alreadyHasCape(player)) {
					mes(player, "you feel a rush of energy charge through your veins",
						"...and a cape appears before you");
					give(player, ItemId.SARADOMIN_CAPE.id(), 1);
				} else {
					mes(player, "but there is no response");
				}
			}
			// first time
			else if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") == 2) {
				mes(player, "you kneel and begin to chant to saradomin",
						"you feel a rush of energy charge through your veins");
				ActionSender.sendTeleBubble(player, player.getX(), player.getY(), true);
				give(player, ItemId.SARADOMIN_CAPE.id(), 1);
				player.getCache().set("mage_arena", 3);
				firstTimeEnchant = true;
			}
		} else if (obj.getID() == GUTHIX_STONE) {
			if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") >= 3) {
				mes(player, "you kneel and chant to guthix");
				if (!alreadyHasCape(player)) {
					mes(player, "you feel a rush of energy charge through your veins",
						"...and a cape appears before you");
					give(player, ItemId.GUTHIX_CAPE.id(), 1);
				} else {
					mes(player, "but there is no response");
				}
			}
			// first time
			else if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") == 2) {
				mes(player, "you kneel and begin to chant to guthix",
						"you feel a rush of energy charge through your veins");
				ActionSender.sendTeleBubble(player, player.getX(), player.getY(), true);
				give(player, ItemId.GUTHIX_CAPE.id(), 1);
				player.getCache().set("mage_arena", 3);
				firstTimeEnchant = true;
			}
		} else if (obj.getID() == ZAMORAK_STONE) {
			if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") >= 3) {
				mes(player, "you kneel and chant to zamorak");
				if (!alreadyHasCape(player)) {
					mes(player, "you feel a rush of energy charge through your veins",
						"...and a cape appears before you");
					give(player, ItemId.ZAMORAK_CAPE.id(), 1);
				} else {
					mes(player, "but there is no response");
				}
			}
			// first time
			else if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") == 2) {
				mes(player, "you kneel and begin to chant to zamorak",
						"you feel a rush of energy charge through your veins");
				ActionSender.sendTeleBubble(player, player.getX(), player.getY(), true);
				give(player, ItemId.ZAMORAK_CAPE.id(), 1);
				player.getCache().set("mage_arena", 3);
				firstTimeEnchant = true;
			}
		}

		if (firstTimeEnchant) {
			player.sendMiniGameComplete(this.getMiniGameId(), Optional.empty());
		}
	}

	private boolean alreadyHasCape(Player player) {
		synchronized(player.getCarriedItems().getInventory().getItems()) {
			for (Item item : player.getCarriedItems().getInventory().getItems()) {
				if (item.getCatalogId() == ItemId.ZAMORAK_CAPE.id() || item.getCatalogId() == ItemId.SARADOMIN_CAPE.id()
					|| item.getCatalogId() == ItemId.GUTHIX_CAPE.id()) {
					return true;
				}
			}
		}
		synchronized(player.getBank().getItems()) {
			for (Item item : player.getBank().getItems()) {
				if (item.getCatalogId() == ItemId.ZAMORAK_CAPE.id() || item.getCatalogId() == ItemId.SARADOMIN_CAPE.id()
					|| item.getCatalogId() == ItemId.GUTHIX_CAPE.id()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onAttackNpc(Player player, Npc affectedmob) {
		if (!affectedmob.getAttribute("spawnedFor", null).equals(player)) {
			player.message("that mage is busy.");
		}
	}

	@Override
	public boolean blockAttackNpc(Player player, Npc n) {
		if (inArray(n.getID(), NpcId.KOLODION_HUMAN.id(), NpcId.KOLODION_OGRE.id(), NpcId.KOLODION_SPIDER.id(),
				NpcId.KOLODION_SOULESS.id(), NpcId.KOLODION_DEMON.id())) {
			if(!n.getAttribute("spawnedFor", null).equals(player)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void onPlayerDeath(Player player) {
		if (player.getAttribute("spawned_kolodion", null) != null) {
			player.setAttribute("spawned_kolodion", null);
		}
	}

	@Override
	public boolean blockPlayerDeath(Player player) {
		return false;
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		return i.getID() == ItemId.ZAMORAK_CAPE.id() || i.getID() == ItemId.SARADOMIN_CAPE.id() || i.getID() == ItemId.GUTHIX_CAPE.id();
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.ZAMORAK_CAPE.id() || i.getID() == ItemId.SARADOMIN_CAPE.id() || i.getID() == ItemId.GUTHIX_CAPE.id()) {
			if (alreadyHasCape(player)) {
				player.message("you may only possess one sacred cape at a time");
			} else {
				Item Item = new Item(i.getID(), i.getAmount());
				player.getWorld().unregisterItem(i);
				player.playSound("takeobject");
				player.getCarriedItems().getInventory().add(Item);
			}
		}

	}
}
