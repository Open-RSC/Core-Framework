package com.openrsc.server.plugins.authentic.minigames.mage_arena;

import com.openrsc.server.constants.*;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.rsc.impl.ObjectRemover;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
		if (getMaxLevel(player, Skill.MAGIC.id()) < 60) { // TODO: Enter the arena game.
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
					mes("You cannot enter the arena...");
					delay(3);
					mes("...while carrying weapons or armour");
					delay(3);
					return;
				}
				teleport(player, 229, 130);
				delay();
				setCurrentLevel(player, Skill.ATTACK.id(), 0);
				setCurrentLevel(player, Skill.STRENGTH.id(), 0);
				spawnKolodion(player, player.getCache().getInt("kolodion_stage"), true);

			} else if (stage == 2) {
				say(player, n, "hello kolodion");
				npcsay(player, n, "hello  young mage.. you're a tough one you");
				say(player, n, "what now?");
				npcsay(player, n, "step into the magic pool, it will take you to the chamber",
					"there you must decide which god you'll represent in the arena");
				say(player, n, "ok .. thanks kolodion");
				npcsay(player, n, "that's what i'm here for");
			} else if (stage >= 3) {
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
			mes("You cannot enter the arena...");
			delay(3);
			mes("...while carrying weapons or armour");
			delay(3);
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
				delay();
				setCurrentLevel(player, Skill.ATTACK.id(), 0);
				setCurrentLevel(player, Skill.STRENGTH.id(), 0);

				// first time
				spawnKolodion(player, NpcId.KOLODION_HUMAN.id(), false);
			} else if (choice == 1) {
				npcsay(player, n, "your loss");
			}
		}
	}

	public void learnSpellEvent(Player player) {
		DelayedEvent mageArena = player.getAttribute("mageArenaEvent", null);
		DelayedEvent mageArenaEvent = new DelayedEvent(player.getWorld(), player, config().GAME_TICK * 3, "Mage Arena Learn Spell Event") {
			@Override
			public void run() {
				boolean recentMaged = getOwner().getAttribute("maged_kolodion", false);
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
				if (!recentMaged) {
					return;
				}
				if (random(0, 1) == 1) {
					getOwner().setAttribute("maged_kolodion", false);
					return;
				}
				boolean sendUpdate = getOwner().getClientLimitations().supportsSkillUpdate;
				if (getOwner().getSkills().getLevel(Skill.ATTACK.id()) > 0 || getOwner().getSkills().getLevel(Skill.STRENGTH.id()) > 0) {
					getOwner().getSkills().setLevel(Skill.ATTACK.id(), 0, sendUpdate);
					getOwner().getSkills().setLevel(Skill.STRENGTH.id(), 0, sendUpdate);
					if (!sendUpdate) {
						getOwner().getSkills().sendUpdateAll();
					}
				}
				Npc Guthix = ifnearvisnpc(player, NpcId.BATTLE_MAGE_GUTHIX.id(), 2);
				Npc Zamorak = ifnearvisnpc(player, NpcId.BATTLE_MAGE_ZAMORAK.id(), 2);
				Npc Saradomin = ifnearvisnpc(player, NpcId.BATTLE_MAGE_SARADOMIN.id(), 2);
				String[] randomMessage = {"@yel@zamorak mage: feel the wrath of zamarok", "@yel@Saradomin mage: feel the wrath of Saradomin", "@yel@guthix mage: feel the wrath of guthix"};
				getOwner().setAttribute("maged_kolodion", false);
				if (Guthix != null && Guthix.withinRange(getOwner(), 1)) {
					godSpellObject(getOwner(), Spells.CLAWS_OF_GUTHIX);
					player.message(randomMessage[2]);
					if (getCurrentLevel(getOwner(), Skill.HITS.id()) < 20) {
						getOwner().damage(2);
					} else {
						getOwner().damage((int)Math.ceil(getCurrentLevel(getOwner(), Skill.HITS.id()) * 0.08));
					}
				} else if (Zamorak != null && Zamorak.withinRange(getOwner(), 1)) {
					godSpellObject(getOwner(), Spells.FLAMES_OF_ZAMORAK);
					player.message(randomMessage[0]);
					if (getCurrentLevel(getOwner(), Skill.HITS.id()) < 20) {
						getOwner().damage(2);
					} else {
						getOwner().damage((int)Math.ceil(getCurrentLevel(getOwner(), Skill.HITS.id()) * 0.08));
					}
				} else if (Saradomin != null && Saradomin.withinRange(getOwner(), 1)) {
					godSpellObject(getOwner(), Spells.SARADOMIN_STRIKE);
					player.message(randomMessage[1]);
					if (getCurrentLevel(getOwner(), Skill.HITS.id()) < 20) {
						getOwner().damage(2);
					} else {
						getOwner().damage((int)Math.ceil(getCurrentLevel(getOwner(), Skill.HITS.id()) * 0.08));
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
		DelayedEvent kolodionEvent = new DelayedEvent(player.getWorld(), player, config().GAME_TICK, "Mage Arena Kolodion Event") {
			@Override
			public void run() {
				Npc npc = getOwner().getAttribute("spawned_kolodion");
				boolean recentMaged = getOwner().getAttribute("maged_kolodion", false);
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
				if ((random(1, 100) != 1 && !recentMaged)) {
					return;
				}
				if (random(0, 1) == 1 && recentMaged) {
					// just message
					getOwner().message(DataConversions.getRandom().nextBoolean()
						? "@yel@kolodion: die you foolish mortal" : "@yel@kolodion: the bigger the better");

					getOwner().setAttribute("maged_kolodion", false);
					return;
				}
				int transformStage = 0;
				switch(NpcId.getById(npc.getID())) {
					case KOLODION:
					case KOLODION_OGRE:
						transformStage = 0;
						break;
					case KOLODION_SPIDER:
						transformStage = 1;
						break;
					case KOLODION_SOULESS:
						transformStage = 2;
						break;
					case KOLODION_DEMON:
						transformStage = 3;
						break;
				}
				boolean allElems = transformStage >= 2;
				int spell_type = random(0, 1 + (allElems ? 1 : 0));
				switch (spell_type) {
					case 0: //claws of guthix
						godSpellObject(getOwner(), Spells.CLAWS_OF_GUTHIX);
						break;
					case 1: //saradomin strike
						godSpellObject(getOwner(), Spells.SARADOMIN_STRIKE);
						break;
					case 2: //flames of zamorak
						godSpellObject(getOwner(), Spells.FLAMES_OF_ZAMORAK);
						break;
				}
				// how many lvls needed for +1 dmg (min 16, max 25)
				int reciprocalSlope = (int) Math.floor(1.0 / (0.06 - (0.01 / 48.0) * getMaxLevel(getOwner(), Skill.HITS.id())));
				// what is the lvl "shift" per new transformation to calculate dmg
				int shiftPerPhase = (int) Math.round((0.004 * getMaxLevel(getOwner(), Skill.HITS.id()) + 0.4) * reciprocalSlope);
				ArrayList<String[]> messages = new ArrayList<String[]>() {
					{
						add(new String[]{"@yel@kolodion: roooaar", "claws grab you from below"});
						add(new String[]{"@yel@kolodion: aaarrgghhh", "@yel@kolodion: feel the power of the elements", "you are hit by a lightning bolt"});
						add(new String[]{"@yel@kolodion: feel the power of the elements mortal", "you burst into flames"});
					}
				};
				if (transformStage == 3 && spell_type == 2 && DataConversions.getRandom().nextBoolean()) {
					// replace message of flames of zamorak
					messages.set(2, new String[]{"@yel@kolodion: burn fool ....burn", "you burst into flames"});
				}
				getOwner().setAttribute("maged_kolodion", false);
				// mes throwing currently error here
				//mes(messages.get(spell_type));
				// temporary below
				for(String message : messages.get(spell_type)) {
					getOwner().message(message);
					delay(2);
				}
				delay(3);
				getOwner().damage((int) Math.ceil(Math.max(getCurrentLevel(getOwner(), Skill.HITS.id()) + (transformStage - 1.0) * shiftPerPhase, 0) / reciprocalSlope) + 1);
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

	// new kolodion stage
	public void spawnKolodion(Player player, int id) {
		this.spawnKolodion(player, id, false);
	}

	// kolodion from new attempt
	public void spawnKolodion(Player player, int id, boolean isContinue) {
		player.setAttribute("spawned_kolodion", addnpc(id, 227, 130, (int)TimeUnit.SECONDS.toMillis(516), player));
		if (!isContinue) {
			player.getCache().set("kolodion_stage", id);
			player.message("kolodion blasts you " + ((id == NpcId.KOLODION_HUMAN.id() || id == NpcId.KOLODION_OGRE.id()) ? "with his staff" : "again"));
			player.damage(random(7, 15));
			ActionSender.sendTeleBubble(player, player.getX(), player.getY(), true);
		}
		startKolodionEvent(player);
	}

	private int[] staves = {
		ItemId.STAFF.id(),
		ItemId.MAGIC_STAFF.id(),
		ItemId.STAFF_OF_AIR.id(),
		ItemId.STAFF_OF_WATER.id(),
		ItemId.STAFF_OF_EARTH.id(),
		ItemId.STAFF_OF_FIRE.id(),
		ItemId.STAFF_OF_SARADOMIN.id(),
		ItemId.STAFF_OF_ZAMORAK.id(),
		ItemId.STAFF_OF_GUTHIX.id()
	};

	private boolean isNotAllowed(Player player, Item item) {
		ItemDefinition def = item.getDef(player.getWorld());
		if (def.isWieldable()) {
			// allow anything in necklace and cape slot
			if (def.getWieldPosition() == Equipment.EquipmentSlot.SLOT_NECK.getIndex()
				|| def.getWieldPosition() == Equipment.EquipmentSlot.SLOT_CAPE.getIndex()) return false;
			// if is one of the allowed staves, then it's good
			if (inArray(item.getCatalogId(), staves)) return false;
			// disallow any other weapon
			if (def.getWieldPosition() == Equipment.EquipmentSlot.SLOT_MAINHAND.getIndex()
				|| def.getWieldPosition() == Equipment.EquipmentSlot.SLOT_OFFHAND.getIndex()) return true;
			// allow "low-tier" magic / prayer related equipment, with low melee bonus
			// per rs2 guides seems higher tier would not have been permitted
			if (((def.getMagicBonus() > 0 && def.getMagicBonus() <= 10)
				|| (def.getPrayerBonus() > 0 && def.getPrayerBonus() <= 10)) && def.getMeleeBonus() <= 5) return false;
			// allow "very basic" armour, per rs2 guides seems leather boots and gloves
			// were ok in apr 2004
			if ((def.getArmourBonus() <= 2 && def.getWeaponPowerBonus() == 0 && def.getWeaponAimBonus() == 0)
				|| (item.getCatalogId() == ItemId.ICE_GLOVES.id())) return false;
			// disallow any other wearable
			return true;
		}
		// non wearables are ok
		return false;
	}

	private boolean cantGo(Player player) {
		synchronized(player.getCarriedItems().getInventory().getItems()) {
			for (Item item : player.getCarriedItems().getInventory().getItems()) {
				if (isNotAllowed(player, item)) return true;
			}

			if (config().WANT_EQUIPMENT_TAB) {
				Item item;
				for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
					item = player.getCarriedItems().getEquipment().get(i);
					if (item == null) continue;
					if (isNotAllowed(player, item)) return true;
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
				mes("kolodion slumps to the floor..");
				delay(3);
				mes("..his body begins to grow and he changes form");
				delay(3);
				mes("He becomes an intimidating ogre");
				delay(3);
				spawnKolodion(player, NpcId.KOLODION_OGRE.id());
			} else if (n.getID() == NpcId.KOLODION_OGRE.id()) {
				mes("kolodion slumps to the floor once more..");
				delay(3);
				mes("..but again his body begins to grow and he changes form");
				delay(3);
				mes("He becomes an enormous spider");
				delay(3);
				spawnKolodion(player, NpcId.KOLODION_SPIDER.id());
			} else if (n.getID() == NpcId.KOLODION_SPIDER.id()) {
				mes("kolodion again slumps to the floor..");
				delay(3);
				mes("..but again his body begins to grow as he changes form");
				delay(3);
				mes("He becomes an ethereal being");
				delay(3);
				spawnKolodion(player, NpcId.KOLODION_SOULESS.id());
			} else if (n.getID() == NpcId.KOLODION_SOULESS.id()) {
				mes("kolodion again slumps to the floor..motionless");
				delay(3);
				mes("..but again his body begins to grow as he changes form");
				delay(3);
				mes("...larger this time");
				delay(3);
				mes("He becomes a vicious demon");
				delay(3);
				spawnKolodion(player, NpcId.KOLODION_DEMON.id());
			} else if (n.getID() == NpcId.KOLODION_DEMON.id()) {
				mes("kolodion again slumps to the floor..motionless");
				delay(3);
				mes("..he slowly rises to his feet in his true form");
				delay(3);
				mes("@yel@Kolodion: \"well done young adventurer\"");
				delay(3);
				mes("@yel@Kolodion: \"you truly are a worthy battle mage\"");
				delay(3);
				player.message("kolodion teleports you to his cave");
				player.teleport(446, 3370);
				Npc kolodion = ifnearvisnpc(player, NpcId.KOLODION.id(), 8);
				if (kolodion == null) {
					player.message("kolodion is currently busy");
				} else {
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
				}
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
		} else if (inArray(n.getID(), NpcId.BATTLE_MAGE_GUTHIX.id(), NpcId.BATTLE_MAGE_ZAMORAK.id(), NpcId.BATTLE_MAGE_SARADOMIN.id())
			&& (!player.getCache().hasKey("mage_arena") || player.getCache().getInt("mage_arena") < 2)) {
			player.message("you are not yet ready to fight the battle mages");
		}
	}

	@Override
	public boolean blockSpellNpc(final Player player, final Npc n) {
		if (inArray(n.getID(), NpcId.KOLODION_HUMAN.id(), NpcId.KOLODION_OGRE.id(), NpcId.KOLODION_SPIDER.id(),
				NpcId.KOLODION_SOULESS.id(), NpcId.KOLODION_DEMON.id())) {
			if (!n.getAttribute("spawnedFor", null).equals(player)) {
				return true;
			}
		} else if (inArray(n.getID(), NpcId.BATTLE_MAGE_GUTHIX.id(), NpcId.BATTLE_MAGE_ZAMORAK.id(), NpcId.BATTLE_MAGE_SARADOMIN.id())
			&& (!player.getCache().hasKey("mage_arena") || player.getCache().getInt("mage_arena") < 2)) {
			return true;
		}
		return false;
	}

	public void godSpellObject(Mob affectedMob, Spells spellEnum) {
		switch (spellEnum) {
			case CLAWS_OF_GUTHIX:
				GameObject guthix = new GameObject(affectedMob.getWorld(), affectedMob.getLocation(), 1142, 0, 0);
				affectedMob.getWorld().registerGameObject(guthix);
				affectedMob.getWorld().getServer().getGameEventHandler().add(new ObjectRemover(affectedMob.getWorld(), guthix, 2));
				break;
			case SARADOMIN_STRIKE:
				GameObject sara = new GameObject(affectedMob.getWorld(), affectedMob.getLocation(), 1031, 0, 0);
				affectedMob.getWorld().registerGameObject(sara);
				affectedMob.getWorld().getServer().getGameEventHandler().add(new ObjectRemover(affectedMob.getWorld(), sara, 2));
				break;
			case FLAMES_OF_ZAMORAK:
				GameObject zammy = new GameObject(affectedMob.getWorld(), affectedMob.getLocation(), 1036, 0, 0);
				affectedMob.getWorld().registerGameObject(zammy);
				affectedMob.getWorld().getServer().getGameEventHandler().add(new ObjectRemover(affectedMob.getWorld(), zammy, 2));
				break;
			case CHARGE:
				GameObject charge = new GameObject(affectedMob.getWorld(), affectedMob.getLocation(), 1147, 0, 0);
				affectedMob.getWorld().registerGameObject(charge);
				affectedMob.getWorld().getServer().getGameEventHandler().add(new ObjectRemover(affectedMob.getWorld(), charge, 2));
				break;
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 1019 || obj.getID() == 1020 || obj.getID() == 1027
			|| obj.getID() == SARADOMIN_STONE || obj.getID() == GUTHIX_STONE || obj.getID() == ZAMORAK_STONE;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
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
					mes("the barrier is checking your person for weapons");
					delay(3);
					if (!cantGo(player)) {
						teleport(player, 228, 120);
					} else {
						mes("You cannot enter the arena...");
						delay(3);
						mes("...while carrying weapons or armour");
						delay(3);
					}
				} else {
					player.message("you cannot enter without the permission of kolodion");
				}
			}
		} else if (obj.getID() == SARADOMIN_STONE) {
			if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") >= 3) {
				mes("you kneel and chant to saradomin");
				delay(3);
				if (!alreadyHasCape(player)) {
					mes("you feel a rush of energy charge through your veins");
					delay(3);
					mes("...and a cape appears before you");
					delay(3);
					give(player, ItemId.SARADOMIN_CAPE.id(), 1);
				} else {
					mes("but there is no response");
					delay(3);
				}
			}
			// first time
			else if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") == 2) {
				mes("you kneel and begin to chant to saradomin");
				delay(3);
				mes("you feel a rush of energy charge through your veins");
				delay(3);
				ActionSender.sendTeleBubble(player, player.getX(), player.getY(), true);
				give(player, ItemId.SARADOMIN_CAPE.id(), 1);
				player.getCache().set("mage_arena", 3);
				firstTimeEnchant = true;
			}
		} else if (obj.getID() == GUTHIX_STONE) {
			if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") >= 3) {
				mes("you kneel and chant to guthix");
				delay(3);
				if (!alreadyHasCape(player)) {
					mes("you feel a rush of energy charge through your veins");
					delay(3);
					mes("...and a cape appears before you");
					delay(3);
					give(player, ItemId.GUTHIX_CAPE.id(), 1);
				} else {
					mes("but there is no response");
					delay(3);
				}
			}
			// first time
			else if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") == 2) {
				mes("you kneel and begin to chant to guthix");
				delay(3);
				mes("you feel a rush of energy charge through your veins");
				delay(3);
				ActionSender.sendTeleBubble(player, player.getX(), player.getY(), true);
				give(player, ItemId.GUTHIX_CAPE.id(), 1);
				player.getCache().set("mage_arena", 3);
				firstTimeEnchant = true;
			}
		} else if (obj.getID() == ZAMORAK_STONE) {
			if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") >= 3) {
				mes("you kneel and chant to zamorak");
				delay(3);
				if (!alreadyHasCape(player)) {
					mes("you feel a rush of energy charge through your veins");
					delay(3);
					mes("...and a cape appears before you");
					delay(3);
					give(player, ItemId.ZAMORAK_CAPE.id(), 1);
				} else {
					mes("but there is no response");
					delay(3);
				}
			}
			// first time
			else if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") == 2) {
				mes("you kneel and begin to chant to zamorak");
				delay(3);
				mes("you feel a rush of energy charge through your veins");
				delay(3);
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
		boolean isCarryingCape = player.getCarriedItems().hasCatalogID(ItemId.ZAMORAK_CAPE.id(), Optional.empty())
			|| player.getCarriedItems().hasCatalogID(ItemId.SARADOMIN_CAPE.id(), Optional.empty())
			|| player.getCarriedItems().hasCatalogID(ItemId.GUTHIX_CAPE.id(), Optional.empty());

		boolean hasBankedCape = player.getBank().hasItemId(ItemId.ZAMORAK_CAPE.id())
			|| player.getBank().hasItemId(ItemId.SARADOMIN_CAPE.id())
			|| player.getBank().hasItemId(ItemId.GUTHIX_CAPE.id());

		return isCarryingCape || hasBankedCape;
	}

	@Override
	public void onAttackNpc(Player player, Npc affectedmob) {
		if (inArray(affectedmob.getID(), NpcId.KOLODION_HUMAN.id(), NpcId.KOLODION_OGRE.id(), NpcId.KOLODION_SPIDER.id(),
			NpcId.KOLODION_SOULESS.id(), NpcId.KOLODION_DEMON.id())) {
			if (!affectedmob.getAttribute("spawnedFor", null).equals(player)) {
				player.message("that mage is busy.");
			}
		} else if (inArray(affectedmob.getID(), NpcId.BATTLE_MAGE_GUTHIX.id(), NpcId.BATTLE_MAGE_ZAMORAK.id(), NpcId.BATTLE_MAGE_SARADOMIN.id())
			&& (!player.getCache().hasKey("mage_arena") || player.getCache().getInt("mage_arena") <= 2)) {
			player.message("you are not yet ready to fight the battle mages");
		}
	}

	@Override
	public boolean blockAttackNpc(Player player, Npc n) {
		if (inArray(n.getID(), NpcId.KOLODION_HUMAN.id(), NpcId.KOLODION_OGRE.id(), NpcId.KOLODION_SPIDER.id(),
				NpcId.KOLODION_SOULESS.id(), NpcId.KOLODION_DEMON.id())) {
			if(!n.getAttribute("spawnedFor", null).equals(player)) {
				return true;
			}
		} else if (inArray(n.getID(), NpcId.BATTLE_MAGE_GUTHIX.id(), NpcId.BATTLE_MAGE_ZAMORAK.id(), NpcId.BATTLE_MAGE_SARADOMIN.id())
			&& (!player.getCache().hasKey("mage_arena") || player.getCache().getInt("mage_arena") <= 2)) {
			return true;
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
