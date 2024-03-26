package com.openrsc.server.plugins.authentic.skills.thieving;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.content.SkillCapes;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.OpNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.openrsc.server.constants.ItemId.THIEVING_CAPE;
import static com.openrsc.server.plugins.Functions.*;

public class Thieving implements OpLocTrigger, OpNpcTrigger, OpBoundTrigger {

	private static final String piece_of = "piece of ";

	public static boolean succeedPickLockThieving(Player player, int req_level) {
		//lockpick said to make picking a bit easier
		int effectiveLevel = player.getSkills().getLevel(Skill.THIEVING.id()) + (player.getCarriedItems().hasCatalogID(ItemId.LOCKPICK.id(), Optional.of(false)) ? 10 : 0);
		return Formulae.calcGatheringSuccessfulLegacy(req_level, effectiveLevel);
	}

	private boolean succeedThieving(Player player, int req_level) {
		return Formulae.calcGatheringSuccessfulLegacy(req_level, player.getSkills().getLevel(Skill.THIEVING.id()), 40);
	}

	public void stallThieving(Player player, GameObject object, final Stall stall) {
		String objectName = object.getGameObjectDef().getName().toLowerCase();

		if (stall.equals(Stall.BAKERS_STALL))
			player.playerServerMessage(MessageType.QUEST, "You attempt to steal some cake from the " + objectName);
		else if (stall.equals(Stall.TEA_STALL)) {
			int chance_player_caught = 60;
			Npc teaseller = ifnearvisnpc(player, stall.getOwnerID(), 8);
			boolean caught = (chance_player_caught > DataConversions.random(0, 100));
			if (caught && teaseller != null) {
				npcsay(player, teaseller, "Oi what do you think you are doing ?", "I'm not like those stallholders in Al Kharid", "No one steals from my stall..");
				return;
			} else
				player.playerServerMessage(MessageType.QUEST, "You attempt to steal a cup of tea...");
		} else if (stall.equals(Stall.GEMS_STALL))
			player.playerServerMessage(MessageType.QUEST, "You attempt to steal gem from the " + objectName);
		else
			player.playerServerMessage(MessageType.QUEST, "You attempt to steal some " + objectName.replaceAll("stall", "").trim() + " from the " + objectName);

		delay(3);

		String failNoun = stall.equals(Stall.BAKERS_STALL) ? "cake" : objectName.replaceAll("stall", "").trim();
		if (!failNoun.endsWith("s")) {
			failNoun += "s";
		}
		if (player.getSkills().getLevel(Skill.THIEVING.id()) < stall.getRequiredLevel()) {
			player.message("You are not a high enough level to steal the " + failNoun);
			return;
		}

		Npc shopkeeper = ifnearvisnpc(player, stall.getOwnerID(), 8);
		Npc guard = null;
		if (stall.equals(Stall.BAKERS_STALL)) {
			guard = ifnearvisnpc(player, NpcId.GUARD_ARDOUGNE.id(), 5);
		} else if (stall.equals(Stall.SILK_STALL) || stall.equals(Stall.FUR_STALL)) {
			guard = ifnearvisnpc(player, 5, NpcId.KNIGHT.id(), NpcId.GUARD_ARDOUGNE.id());
		} else if (stall.equals(Stall.SILVER_STALL) || stall.equals(Stall.SPICES_STALL)) {
			guard = ifnearvisnpc(player, 5, NpcId.PALADIN.id(), NpcId.KNIGHT.id(), NpcId.GUARD_ARDOUGNE.id());
		} else if (stall.equals(Stall.GEMS_STALL)) {
			guard = ifnearvisnpc(player, 5, NpcId.HERO.id(), NpcId.PALADIN.id(), NpcId.KNIGHT.id(), NpcId.GUARD_ARDOUGNE.id());
		}

		if (shopkeeper != null) {
			if (canBeSeen(player.getWorld(), shopkeeper.getX(), shopkeeper.getY(), player.getX(), player.getY())) {
				npcYell(player, shopkeeper, "Hey thats mine");
				if (!player.getCache().hasKey("stolenFrom" + stall.getOwnerID())) {
					player.getCache().store("stolenFrom" + stall.getOwnerID(), true);
				}
				return;
			}
		}
		if (guard != null) {
			if (canBeSeen(player.getWorld(), guard.getX(), guard.getY(), player.getX(), player.getY())) {
				npcYell(player, guard, "Hey! Get your hands off there!");
				player.setAttribute("stolenFrom" + stall.getOwnerID(), true);
				guard.setChasing(player);
				return;
			}
		}

		int random = DataConversions.random(1, 100);
		Item selectedLoot = null;
		int cummChance = 0;
		for (LootItem loot : stall.lootTable) {
			if (cummChance + loot.getChance() >= random) {
				selectedLoot = new Item(loot.getId(), loot.getAmount());
				break;
			}
			cummChance += loot.getChance();
		}
		if (selectedLoot == null) {
			selectedLoot = new Item(stall.lootTable.get(0).getId(), stall.lootTable.get(0).getAmount());
		}
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 2
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("You are too tired to thieve here");
				return;
			}
		}

		player.getCarriedItems().getInventory().add(selectedLoot);
		String loot = stall.equals(Stall.GEMS_STALL) ? "gem" : selectedLoot.getDef(player.getWorld()).getName().toLowerCase();
		player.message("You steal a " + stall.getLootPrefix() + loot);
		int stallXp = player.getConfig().WANT_CORRECTED_SKILLING_XP && stall.equals(Stall.GEMS_STALL) ? 640 : stall.getXp();

		player.incExp(Skill.THIEVING.id(), stallXp, true);

		if (stall.equals(Stall.BAKERS_STALL)) { // Cake
			player.getCache().put("cakeStolen", Instant.now().getEpochSecond());
		} else if (stall.equals(Stall.SILK_STALL)) { // Silk
			player.getCache().put("silkStolen", Instant.now().getEpochSecond());
		} else if (stall.equals(Stall.FUR_STALL)) { // Fur
			player.getCache().put("furStolen", Instant.now().getEpochSecond());
		} else if (stall.equals(Stall.SILVER_STALL)) { // Silver
			player.getCache().put("silverStolen", Instant.now().getEpochSecond());
		} else if (stall.equals(Stall.SPICES_STALL)) { // Spice
			player.getCache().put("spiceStolen", Instant.now().getEpochSecond());
		} else if (stall.equals(Stall.GEMS_STALL)) { // Gem
			player.getCache().put("gemStolen", Instant.now().getEpochSecond());
		}

		// Replace stall with empty version
		object.getWorld().replaceGameObject(object,
			new GameObject(player.getWorld(), object.getLocation(), 341, object.getDirection(), object.getType()));
		object.getWorld().delayedSpawnObject(object.getLoc(), stall.getRespawnTime());
	}

	public void handleChestThieving(Player player, GameObject obj) {
		int reqtemp = 1;
		int respawnTimetmep = 0;
		ArrayList<LootItem> loottemp = new ArrayList<LootItem>();
		Point teleLoctemp = null;
		int xptemp = 0;
		switch (obj.getID()) {
			case 334:
				// 10gp Chest
				reqtemp = 13;
				xptemp = 30;
				respawnTimetmep = 10000;
				loottemp.add(new LootItem(ItemId.COINS.id(), 10, 100));
				break;
			case 335:
				// Nature-rune Chest
				reqtemp = 28;
				xptemp = 100;
				respawnTimetmep = 25000;
				loottemp = getLootAsList(new LootItem(ItemId.COINS.id(), 3, 100), new LootItem(ItemId.NATURE_RUNE.id(), 1, 100));
				break;
			case 336:
				// 50gp Chest
				reqtemp = 43;
				xptemp = 500;
				respawnTimetmep = 100000;
				loottemp.add(new LootItem(ItemId.COINS.id(), 50, 100));
				break;
			case 337:
				// blood Chest
				reqtemp = 59;
				xptemp = 1000;
				respawnTimetmep = 250000;
				loottemp = getLootAsList(new LootItem(ItemId.COINS.id(), 500, 100), new LootItem(ItemId.BLOOD_RUNE.id(), 2, 100));
				teleLoctemp = Point.location(614, 568);
				break;
			case 338:
				// paladin Chest
				reqtemp = 72;
				xptemp = 2000;
				respawnTimetmep = 500000;
				loottemp = getLootAsList(new LootItem(ItemId.COINS.id(), 1000, 100), new LootItem(ItemId.RAW_SHARK.id(), 1, 100),
					new LootItem(ItemId.ADAMANTITE_ORE.id(), 1, 100), new LootItem(ItemId.UNCUT_SAPPHIRE.id(), 1, 100));
				teleLoctemp = Point.location(523, 606);
				break;
		}
		final int req = reqtemp;
		final int respawnTime = respawnTimetmep;
		final ArrayList<LootItem> loot = loottemp;
		final Point teleLoc = teleLoctemp;
		final int xp = xptemp;
		player.message("You search the chest for traps");
		boolean makeChestStuck = config().LOOTED_CHESTS_STUCK;
		AtomicReference<GameObject> tempChest = new AtomicReference<GameObject>();
		if (player.getSkills().getLevel(Skill.THIEVING.id()) < req) {
			player.message("You find nothing");
			return;
		}
		if (config().WANT_FATIGUE) {
			// On OG thieving chests not thievable on 100% fatigue
			if (config().STOP_SKILLING_FATIGUED >= 1
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("You are too tired to thieve here");
				return;
			}
		}
		//check if the chest is still same
		GameObject checkObj = player.getViewArea().getGameObject(obj.getID(), obj.getX(), obj.getY());
		if (checkObj == null) {
			player.message("You find nothing");
			return;
		}

		player.message("You find a trap on the chest");
		SingleEvent chestValidationEvent = null;
		if (!makeChestStuck) {
			final int totalTime = (player.getWorld().getServer().getConfig().GAME_TICK * 8) + respawnTime;
			chestValidationEvent = scheduleChestValidation(player.getWorld(), totalTime, obj);
			tempChest.set(new GameObject(player.getWorld(), obj.getLocation(), 340, obj.getDirection(), obj.getType()));
			changeloc(obj, tempChest.get());
		}
		delay(2);
		player.message("You disable the trap");

		mes("You open the chest");
		delay(3);
		if (!makeChestStuck && tempChest.get() != null) {
			openChest(tempChest.get());
		} else {
			changeloc(obj, respawnTime, 339);
		}
		int random = DataConversions.random(1, 100);
		Collections.sort(loot);
		for (LootItem l : loot) {
			if (l.getChance() >= random) {
				player.getCarriedItems().getInventory().add(new Item(l.getId(), l.getAmount()));
			}
		}
		player.incExp(Skill.THIEVING.id(), xp, true);
		mes("You find treasure inside!");
		delay(3);
		if (!makeChestStuck) {
			if (chestValidationEvent != null) {
				chestValidationEvent.stop();
			}
			changeloc(obj, respawnTime, 340);
		}
		if (teleLoc != null) {
			mes("suddenly a second magical trap triggers");
			delay(3);
			player.teleport(teleLoc.getX(), teleLoc.getY(), true);
		}
	}

	private SingleEvent scheduleChestValidation(final World world, final int totalSpawnTime, final GameObject chest) {
		final GameObject newChest = new GameObject(world, new Point(chest.getLoc().getX(), chest.getLoc().getY()),
			chest.getID(), chest.getDirection(), chest.getType(), chest.getOwner());

		final SingleEvent chestValidationEvent = new SingleEvent(world, null, totalSpawnTime, "Delayed chest validation") {
			public void action() {
				world.registerGameObject(newChest);
			}
		};

		world.getServer().getGameEventHandler().submit(() ->
			world.getServer().getGameEventHandler().add(chestValidationEvent), "Register chest validation");
		return chestValidationEvent;
	}

	private ArrayList<LootItem> getLootAsList(LootItem... lootItem) {
		ArrayList<LootItem> l = new ArrayList<LootItem>();
		for (LootItem loot : lootItem) {
			l.add(loot);
		}
		return l;
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		String formattedName = obj.getGameObjectDef().getName().toUpperCase().replaceAll(" ", "_");

		if (formattedName.contains("STALL")) {
			if (obj.getGameObjectDef().getName().equalsIgnoreCase("empty stall")) {
				return false;
			}
			if (!player.getConfig().MEMBER_WORLD) {
				player.message(player.MEMBER_MESSAGE);
				return false;
			}
			Stall stall = Stall.valueOf(formattedName);
			if (stall != null) {
				return true;
			}
		} else if (obj.getID() >= 334 && obj.getID() <= 339) {
			if (!player.getConfig().MEMBER_WORLD) {
				player.message(player.MEMBER_MESSAGE);
				return false;
			}
			return true;
		} else if (obj.getID() == 340) {
			player.message("It looks like this chest has already been looted");
			return true;
		} else if (obj.getID() == 379) { // hemenster chest
			return true;
		}
		return false;
	}

	@Override
	public boolean blockOpNpc(Player player, Npc n, String command) {
		if (command.equalsIgnoreCase("pickpocket")) {
			Pickpocket pickpocket = null;
			try {
				pickpocket = Pickpocket.valueOf(n.getDef().getName().toUpperCase().replace(" ", "_"));
			} catch (Exception e) {
				//Ignore..
			}

			if (pickpocket != null) {
				if (!player.getConfig().MEMBER_WORLD) {
					player.message(player.MEMBER_MESSAGE);
					return false;
				}
				return true;
			} else if (n.getID() == NpcId.CIVILLIAN_PICKPOCKET.id()) {
				player.message("Nothing interesting happens");
			} else {
				player.message("You can't pickpocket that person, it has not been implemented yet");
			}

		}
		return false;
	}

	public void doPickpocket(final Player player, final Npc npc, final Pickpocket pickpocket) {
		if (npc.inCombat()) {
			player.message("I can't get close enough");
			return;
		}
		if (npc.getID() == NpcId.WORKMAN_UNDERGROUND.id()) {
			npcsay(player, npc, "Hey! trying to steal from me are you ?", "What do you think I am - stupid or something !?");
			say(player, npc, "Err...sorry");
			return;
		}
		final ArrayList<LootItem> lootTable = new ArrayList<LootItem>(pickpocket.getLootTable());
		String thievedMobName = npc.getDef().getName().toLowerCase();
		//gnome local, child, trainer, waiter and barman all known as gnome for the thiev messages
		//yanille watchman known simply as watchman
		final String thievedMobString = (thievedMobName.contains("gnome") || thievedMobName.contains("blurberry")) ? "gnome" :
			thievedMobName.contains("watchman") ? "watchman" : thievedMobName;

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = Formulae.getRepeatTimes(player, Skill.THIEVING.id());
			npc.setBusy(true);
		}

		startbatch(repeat);
		batchPickpocket(player, npc, pickpocket, lootTable, thievedMobString);
	}

	private void batchPickpocket(Player player, Npc npc, Pickpocket pickpocket, ArrayList<LootItem> lootTable, String thievedMobString) {
		if (npc.inCombat()) {
			npc.setBusy(false);
			return;
		}
		if (player.getSkills().getLevel(Skill.THIEVING.id()) < pickpocket.getRequiredLevel()) {
			player.playerServerMessage(MessageType.QUEST, "You need to be a level " + pickpocket.getRequiredLevel() + " thief to pick the " + thievedMobString + "'s pocket");
			npc.setBusy(false);
			return;
		}
		player.playerServerMessage(MessageType.QUEST, "You attempt to pick the " + thievedMobString + "'s pocket");
		delay();
		boolean succeededPickpocket = succeedThieving(player, pickpocket.getRequiredLevel());
		if (SkillCapes.shouldActivate(player, THIEVING_CAPE, succeededPickpocket)) {
			succeededPickpocket = true;
			thinkbubble(new Item(THIEVING_CAPE.id()));
			mes("@mag@Your Thieving cape activates, and you successfully pick the " + thievedMobString + "'s pocket");
		}
		if (succeededPickpocket) {
			if (config().WANT_FATIGUE) {
				if (config().STOP_SKILLING_FATIGUED >= 2
					&& player.getFatigue() >= player.MAX_FATIGUE) {
					player.message("You are too tired to pickpocket this mob");
					npc.setBusy(false);
					return;
				}
			}

			player.playerServerMessage(MessageType.QUEST, "You pick the " + thievedMobString + "'s pocket");

			Item selectedLoot = null;
			int total = 0;
			for (LootItem loot : lootTable) {
				total += loot.getChance();
			}
			int hit = DataConversions.random(0, total);
			total = 0;
			for (LootItem loot : lootTable) {
				if (loot.getChance() >= 100) {
					player.getCarriedItems().getInventory().add(new Item(loot.getId(), loot.getAmount()));
					continue;
				}
				if (hit >= total && hit < (total + loot.getChance())) {
					if (loot.getId() == -1) {
						player.playerServerMessage(MessageType.QUEST, "You find nothing to steal");
						npc.setBusy(false);
						return;
					}
					selectedLoot = (new Item(loot.getId(), loot.getAmount()));
					break;
				}
				total += loot.getChance();
			}

			if (selectedLoot != null) {
				player.getCarriedItems().getInventory().add(selectedLoot);
			}

			player.incExp(Skill.THIEVING.id(), pickpocket.getXp(), true);
		} else {
			player.playerServerMessage(MessageType.QUEST, "You fail to pick the " + thievedMobString + "'s pocket");
			npc.getUpdateFlags()
				.setChatMessage(new ChatMessage(npc, pickpocket.shoutMessage, player));
			delay();
			npc.startCombat(player);
			npc.setBusy(false);
			return;
		}

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			if (!player.withinRange(npc, 1)) {
				player.message("The " + thievedMobString + " has moved.");
				npc.setBusy(false);
				return;
			}
			delay(2);
			batchPickpocket(player, npc, pickpocket, lootTable, thievedMobString);
		}
		else {
			npc.setBusy(false);
		}
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		String formattedName = obj.getGameObjectDef().getName().toUpperCase().replaceAll(" ", "_");
		if (formattedName.contains("STALL")) {
			if (obj.getGameObjectDef().getName().equalsIgnoreCase("empty stall")) {
				return;
			}
			Stall stall = Stall.valueOf(formattedName);
			if (stall != null) {
				stallThieving(player, obj, stall);
			}
		} else if (obj.getID() >= 334 && obj.getID() < 339) {
			if (command.equalsIgnoreCase("Open")) {
				player.playerServerMessage(MessageType.QUEST, "You have activated a trap on the chest");
				player.damage(DataConversions.random(0, 8));
			} else {
				handleChestThieving(player, obj);
			}
		} else if (obj.getID() == 339) {
			player.message("You search the chest for traps");
			player.message("You find nothing");
		} else if (obj.getID() == 379) { // HEMENSTER CHEST HARDCODE
			if (command.equalsIgnoreCase("Open")) {
				player.playerServerMessage(MessageType.QUEST, "This chest is locked");
			} else {
				player.playerServerMessage(MessageType.QUEST, "you attempt to pick the lock");
				if (config().WANT_FATIGUE) {
					if (config().STOP_SKILLING_FATIGUED >= 2
						&& player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to pick the lock");
						return;
					}
				}
				if (player.getSkills().getLevel(Skill.THIEVING.id()) < 47) {
					player.playerServerMessage(MessageType.QUEST, "You are not a high enough level to pick this lock");
					return;
				}
				if (!player.getCarriedItems().hasCatalogID(ItemId.LOCKPICK.id(), Optional.of(false))) {
					player.playerServerMessage(MessageType.QUEST, "You need a lockpick for this lock");
					return;
				}
				player.playerServerMessage(MessageType.QUEST, "You manage to pick the lock");

				openChest(obj);
				mes("You open the chest");
				delay(3);

				mes("You find a treasure inside!");
				delay(3);

				player.incExp(Skill.THIEVING.id(), 600, true);
				give(player, ItemId.COINS.id(), 20);
				give(player, ItemId.STEEL_ARROW_HEADS.id(), 5);

				player.getWorld().replaceGameObject(obj,
					new GameObject(player.getWorld(), obj.getLocation(), 340, obj.getDirection(), obj.getType()));
				player.getWorld().delayedSpawnObject(obj.getLoc(), 150000);
			}
		}
	}

	private boolean canBeSeen(World world, int fromX, int fromY, int targetX, int targetY) {
		int count = 0;
		boolean stop = false;
		while (!stop) {
			if (count++ > 10)
				break;
			if (fromY < targetY) {// Target is at South
				fromY++;
			}
			if (fromY > targetY) {// Target is north
				fromY--;
			}
			if (fromX >= targetX) { // Target is at West
				fromX--;
			}
			if (fromX <= targetX) { // Target is East
				fromX++;
			}
			/* If there is no unwalkable object in the way */
			if ((world.getTile(fromX, fromY).traversalMask & 64) != 0) {
				stop = true;
				return false;
			}

			if (fromX == targetX && fromY == targetY) {
				stop = true;
				return true;
			}
		}
		return true;
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() >= 93 && obj.getID() <= 97 || obj.getID() >= 99 & obj.getID() <= 100 || obj.getID() == 162) {
			if (!player.getConfig().MEMBER_WORLD) {
				player.message(player.MEMBER_MESSAGE);
				return false;
			}
			return true;
		}
		return false;
	}

	private void handlePicklock(GameObject obj, Player player, Integer click) {
		int req = 1;
		int exp = 0;
		boolean goThrough = false; // no need for picklock again.
		boolean requiresLockpick = false;
		switch (obj.getID()) {
			case 93: // 10gp chest door id (93)
				req = 7;
				exp = 15;
				if (player.getY() <= 591) {
					goThrough = true;
				}
				break;
			case 94: // Nature rune chest + 50 gp chest door id and Yanille anvil hut (94)
				if ((obj.getX() == 586 && obj.getY() == 581)
					|| (obj.getX() == 539 && obj.getY() == 599)
					|| (obj.getX() == 581 && obj.getY() == 580)
					|| (obj.getX() == 581 && obj.getY() == 761)) {
					req = 16;
					exp = 60;
					if (player.getX() == 539 && player.getY() >= 599) {
						goThrough = true;
					} else if (player.getX() <= 585 && player.getY() == 581) {
						goThrough = true;
					} else if (player.getX() >= 581 && player.getY() == 580) {
						goThrough = true;
					} else if (player.getX() == 582 && player.getY() >= 761
						|| player.getX() == 581 && player.getY() >= 762) {
						goThrough = true;
					}
				}
				break;
			case 95: // Ardougne Sewer mine (95)
				req = 31;
				exp = 100;
				if (player.getX() <= 556) {
					goThrough = true;
				}
				break;
			case 96: // Chaos druid tower door id (96)
				req = 46;
				exp = 150;
				if (player.getY() <= 555) {
					goThrough = true;
				}
				break;
			case 162: // yanille druid door id(162)
				req = 82;
				exp = 200;
				requiresLockpick = true;
				break;
			case 100: // axe huts door id (100)
				req = 32;
				exp = 100;
				requiresLockpick = true;
				if (player.getY() >= 103 && player.getY() <= 107) {
					goThrough = true;
				}
				break;
			case 99: // pirate hut door id (99)
				req = 39;
				exp = 140;
				requiresLockpick = true;
				if ((player.getX() >= 263 && player.getX() <= 269 && player.getY() == 104)
					|| (player.getX() == 266 && player.getY() >= 100)) {
					goThrough = true;
				}
				break;
			case 97:// Ardougne Paladin 2nd floor door id (97)
				req = 61;
				exp = 200;
				if (player.getY() >= 1548 && player.getX() == 609) {
					goThrough = true;
				}
				break;
		}
		if (click == 0) {
			if (goThrough) {
				player.message("You go through the door");
				doDoor(obj, player);
			} else {
				player.message("The door is locked");
			}
		} else if (click == 1) {
			if (goThrough) {
				player.message("You have already unlocked the door");
				return;
			}

			int repeat = 1;
			if (config().BATCH_PROGRESSION) {
				repeat = Formulae.getRepeatTimes(player, Skill.THIEVING.id());
			}

			startbatch(repeat);
			batchPicklock(player, obj, req, exp, goThrough, requiresLockpick);
		}
	}

	public void batchPicklock(Player player, GameObject obj, int req,
							  int exp, boolean goThrough, boolean requiresLockpick) {
		player.playerServerMessage(MessageType.QUEST, "you attempt to pick the lock");

		if (getCurrentLevel(player, Skill.THIEVING.id()) < req) {
			player.playerServerMessage(MessageType.QUEST, "You are not a high enough level to pick this lock");
			return;
		}
		if (!player.getCarriedItems().hasCatalogID(ItemId.LOCKPICK.id(), Optional.of(false)) && requiresLockpick) {
			player.playerServerMessage(MessageType.QUEST, "You need a lockpick for this lock");
			return;
		}
//		// commented since may get people stuck behind door otherwise
//		if (config().WANT_FATIGUE) {
//			if (config().STOP_SKILLING_FATIGUED >= 2
//				&& player.getFatigue() >= player.MAX_FATIGUE) {
//				player.message("You are too tired to pick the lock");
//				return;
//			}
//		}
		if (succeedPickLockThieving(player, req) && !goThrough) {
			player.playerServerMessage(MessageType.QUEST, "You manage to pick the lock");
			doDoor(obj, player);
			player.message("You go through the door");
			player.incExp(Skill.THIEVING.id(), (int) exp, true);
		} else {
			player.playerServerMessage(MessageType.QUEST, "You fail to pick the lock");

			// Repeat on failure
			updatebatch();
			if (!ifinterrupted() && !isbatchcomplete()) {
				delay();
				batchPicklock(player, obj, req, exp, goThrough, requiresLockpick);
			}
		}
	}

	@Override
	public void onOpNpc(Player player, Npc n, String command) {
		if (command.equalsIgnoreCase("pickpocket")) {
			Npc npc = player.getWorld().getNpc(
					n.getID(),
					player.getX() - 2,
					player.getX() + 2,
					player.getY() - 2,
					player.getY() + 2
			);
			if (npc == null) {
				return;
			}
			Pickpocket pickpocket = Pickpocket.valueOf(n.getDef().getName().toUpperCase().replace(" ", "_"));
			doPickpocket(player, n, pickpocket);
		}
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		handlePicklock(obj, player, click);
	}

	enum Pickpocket {
		MAN(1, 32, "Oi what do you think you're doing",
			new LootItem(ItemId.COINS.id(), 3, 100)),
		FARMER(10, 58, "What do you think you're doing",
			new LootItem(ItemId.COINS.id(), 9, 100)),
		WARRIOR(25, 104, "Hey what do you think you're doing",
			new LootItem(ItemId.COINS.id(), 18, 100)),
		WORKMAN(25, 0, "Hey what do you think you're doing",
			new LootItem(ItemId.COINS.id(), 10, 30),
			new LootItem(ItemId.SPADE.id(), 1, 20),
			new LootItem(ItemId.BUCKET.id(), 1, 18),
			new LootItem(ItemId.ROPE.id(), 1, 16),
			new LootItem(ItemId.SPECIMEN_BRUSH.id(), 1, 14),
			new LootItem(ItemId.ROCK_SAMPLE_GREEN.id(), 1, 12),
			new LootItem(ItemId.LEATHER_GLOVES.id(), 1, 10),
			new LootItem(ItemId.NOTHING.id(), 0, 8)),
		ROGUE(32, 146, "Hey what do you think you're doing",
			new LootItem(ItemId.COINS.id(), 25, 40),
			new LootItem(ItemId.COINS.id(), 40, 30),
			new LootItem(ItemId.WINE.id(), 1, 10),
			new LootItem(ItemId.AIR_RUNE.id(), 8, 10),
			new LootItem(ItemId.LOCKPICK.id(), 1, 10),
			new LootItem(ItemId.POISONED_IRON_DAGGER.id(), 1, 3)),
		GUARD(40, 187, "Err what do you think you're doing",
			new LootItem(ItemId.COINS.id(), 30, 100)),
		KNIGHT(55, 337, "Err what do you think you're doing",
			new LootItem(ItemId.COINS.id(), 50, 100)),
		YANILLE_WATCHMAN(65, 550, "Oi you nasty little thief",
			new LootItem(ItemId.COINS.id(), 60, 100),
			new LootItem(ItemId.BREAD.id(), 1, 100)),
		PALADIN(70, 607, "Get your hands off my valuables",
			new LootItem(ItemId.COINS.id(), 80, 100),
			new LootItem(ItemId.CHAOS_RUNE.id(), 1, 100)),
		GNOME_LOCAL(75, 793, "Get your hands off my valuables human",
			new LootItem(ItemId.COINS.id(), 200, 22),
			new LootItem(ItemId.COINS.id(), 400, 18),
			new LootItem(ItemId.GOLD.id(), 1, 10),
			new LootItem(ItemId.EARTH_RUNE.id(), 1, 15),
			new LootItem(ItemId.SWAMP_TOAD.id(), 1, 15),
			new LootItem(ItemId.KING_WORM.id(), 1, 20)),
		GNOME_CHILD(75, 793, "Get your hands off my valuables human",
			new LootItem(ItemId.COINS.id(), 200, 22),
			new LootItem(ItemId.COINS.id(), 400, 18),
			new LootItem(ItemId.GOLD.id(), 1, 10),
			new LootItem(ItemId.EARTH_RUNE.id(), 1, 15),
			new LootItem(ItemId.SWAMP_TOAD.id(), 1, 15),
			new LootItem(ItemId.KING_WORM.id(), 1, 20)),
		GNOME_TRAINER(75, 793, "Get your hands off my valuables human",
			new LootItem(ItemId.COINS.id(), 200, 22),
			new LootItem(ItemId.COINS.id(), 400, 18),
			new LootItem(ItemId.GOLD.id(), 1, 10),
			new LootItem(ItemId.EARTH_RUNE.id(), 1, 15),
			new LootItem(ItemId.SWAMP_TOAD.id(), 1, 15),
			new LootItem(ItemId.KING_WORM.id(), 1, 20)),
		GNOME_WAITER(75, 793, "Get your hands off my valuables human",
			new LootItem(ItemId.COINS.id(), 200, 22),
			new LootItem(ItemId.COINS.id(), 400, 18),
			new LootItem(ItemId.GOLD.id(), 1, 10),
			new LootItem(ItemId.EARTH_RUNE.id(), 1, 15),
			new LootItem(ItemId.SWAMP_TOAD.id(), 1, 15),
			new LootItem(ItemId.KING_WORM.id(), 1, 20)),
		BLURBERRY_BARMAN(75, 793, "Get your hands off my valuables human",
			new LootItem(ItemId.COINS.id(), 200, 22),
			new LootItem(ItemId.COINS.id(), 400, 18),
			new LootItem(ItemId.GOLD.id(), 1, 10),
			new LootItem(ItemId.EARTH_RUNE.id(), 1, 15),
			new LootItem(ItemId.SWAMP_TOAD.id(), 1, 15),
			new LootItem(ItemId.KING_WORM.id(), 1, 20)),
		HERO(80, 1093, "Get your hands off my valuables",
			new LootItem(ItemId.COINS.id(), 100, 25),
			new LootItem(ItemId.COINS.id(), 200, 15),
			new LootItem(ItemId.COINS.id(), 300, 10),
			new LootItem(ItemId.FIRE_ORB.id(), 1, 10),
			new LootItem(ItemId.WINE.id(), 1, 14),
			new LootItem(ItemId.GOLD.id(), 1, 5),
			new LootItem(ItemId.DEATH_RUNE.id(), 2, 10),
			new LootItem(ItemId.BLOOD_RUNE.id(), 1, 5),
			new LootItem(ItemId.DIAMOND.id(), 1, 1));

		private final ArrayList<LootItem> lootTable;
		private final int xp;
		private final int requiredLevel;
		private final String shoutMessage;

		Pickpocket(int req, int xp, String shoutMessage, LootItem... possibleLoot) {
			this.xp = xp;
			this.requiredLevel = req;
			this.shoutMessage = shoutMessage;
			lootTable = new ArrayList<LootItem>();
			for (LootItem lootItem : possibleLoot) {
				lootTable.add(lootItem);
			}
			Collections.sort(lootTable);
		}

		public int getRequiredLevel() {
			return requiredLevel;
		}

		public ArrayList<LootItem> getLootTable() {
			return lootTable;
		}

		public int getXp() {
			return xp;
		}
	}

	enum Stall {
		TEA_STALL(5, 64, NpcId.TEA_SELLER.id(), 5000,
			"", new LootItem(ItemId.CUP_OF_TEA.id(), 1, 100)),
		BAKERS_STALL(5, 64, NpcId.BAKER.id(), 5000,
			"", new LootItem(ItemId.CAKE.id(), 1, 100)),
		SILK_STALL(20, 96, NpcId.SILK_MERCHANT.id(), 8000,
			piece_of, new LootItem(ItemId.SILK.id(), 1, 100)),
		FUR_STALL(35, 144, NpcId.FUR_TRADER.id(), 15000,
			piece_of, new LootItem(ItemId.GREY_WOLF_FUR.id(), 1, 100)),
		SILVER_STALL(50, 216, NpcId.SILVER_MERCHANT.id(), 30000,
			piece_of, new LootItem(ItemId.SILVER.id(), 1, 100)),
		SPICES_STALL(65, 324, NpcId.SPICE_MERCHANT.id(), 80000,
			"pot of ", new LootItem(ItemId.SPICE.id(), 1, 100)),
		GEMS_STALL(75, 64, NpcId.GEM_MERCHANT.id(), 180000,
			"", new LootItem(ItemId.UNCUT_SAPPHIRE.id(), 1, 65),
			new LootItem(ItemId.UNCUT_EMERALD.id(), 1, 20),
			new LootItem(ItemId.UNCUT_RUBY.id(), 1, 10),
			new LootItem(ItemId.UNCUT_DIAMOND.id(), 1, 5));

		ArrayList<LootItem> lootTable;
		private String lootPrefix;
		private int xp;
		private int requiredLevel;
		private int respawnTime;
		private int ownerID;

		Stall(int req, int xp, int ownerID, int respawnTime, String lootPrefix, LootItem... loot) {
			this.setXp(xp);
			this.setRespawnTime(respawnTime);
			this.setRequiredLevel(req);
			this.ownerID = ownerID;
			this.setLootPrefix(lootPrefix);
			lootTable = new ArrayList<LootItem>();
			for (LootItem lootItem : loot) {
				lootTable.add(lootItem);
			}
			Collections.sort(lootTable);
		}

		public int getRequiredLevel() {
			return requiredLevel;
		}

		public void setRequiredLevel(int requiredLevel) {
			this.requiredLevel = requiredLevel;
		}

		public int getRespawnTime() {
			return respawnTime;
		}

		public void setRespawnTime(int respawnTime) {
			this.respawnTime = respawnTime;
		}

		public int getXp() {
			return xp;
		}

		public void setXp(int xp) {
			this.xp = xp;
		}

		public int getOwnerID() {
			return ownerID;
		}

		public String getLootPrefix() {
			return lootPrefix;
		}

		public void setLootPrefix(String lootPrefix) {
			this.lootPrefix = lootPrefix;
		}
	}

	static class LootItem implements Comparable<LootItem> {
		private final int id;
		private final int amount;
		private int chance;

		public LootItem(int id, int amount, int chance) {
			this.id = id;
			this.amount = amount;
			this.chance = chance;
		}

		public int getChance() {
			return chance;
		}

		@Override
		public int compareTo(LootItem arg0) {
			if (getChance() > arg0.getChance())
				return 1;
			else
				return -1;
		}

		public int getAmount() {
			return amount;
		}

		public int getId() {
			return id;
		}
	}
}
