package com.openrsc.server.plugins.skills;

import java.util.ArrayList;
import java.util.Collections;

import com.openrsc.server.Constants.GameServer;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.NpcCommandListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.NpcCommandExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

public class Thieving extends Functions
implements ObjectActionListener, NpcCommandListener, NpcCommandExecutiveListener, ObjectActionExecutiveListener,
WallObjectActionExecutiveListener, WallObjectActionListener {

	enum Pickpocket {
		MAN(1, 32, "Oi what do you think you're doing",
				new LootItem(10, 3, 100)), 
		FARMER(10, 58, "What do you think you're doing",
				new LootItem(10, 9, 100)), 
		WARRIOR(25, 104, "Hey what do you think you're doing",
				new LootItem(10, 18, 100)), 
		WORKMAN(25, 0, "Hey what do you think you're doing",
				new LootItem(10, 10, 30), 
				new LootItem(211, 1, 20),
				new LootItem(21, 1, 18), 
				new LootItem(237, 1, 16), 
				new LootItem(1115, 1, 14),
				new LootItem(1117, 1, 12), 
				new LootItem(16, 1, 10), 
				new LootItem(-1, 0, 8)), 
		ROGUE(32, 142, "Hey what do you think you're doing",
				new LootItem(10, 25, 40),
				new LootItem(10, 40, 30), 
				new LootItem(142, 1, 10), 
				new LootItem(33, 8, 10),
				new LootItem(714, 1, 10), 
				new LootItem(559, 1, 3)), 
		GUARD(40, 186, "Err what do you think you're doing",
				new LootItem(10, 30, 100)), 
		KNIGHT(55, 338, "Err what do you think you're doing",
				new LootItem(10, 50, 100)),
		YANILLE_WATCHMAN(65, 500, "Oi you nasty little thief",
				new LootItem(10, 60, 100), 
				new LootItem(138, 1, 100)), 
		PALADIN(70, 608, "Get your hands off my valuables",
				new LootItem(10, 80, 100), 
				new LootItem(41, 1, 100)),
		GNOME_LOCAL(75, 792, "Get your hands off my valuables human",
				new LootItem(10, 200, 22), 
				new LootItem(10, 400, 18), 
				new LootItem(152, 1, 10),
				new LootItem(34, 1, 15), 
				new LootItem(895, 1, 15), 
				new LootItem(897, 1, 20)),
		GNOME_CHILD(75, 792, "Get your hands off my valuables human",
				new LootItem(10, 200, 22), 
				new LootItem(10, 400, 18), 
				new LootItem(152, 1, 10),
				new LootItem(34, 1, 15), 
				new LootItem(895, 1, 15), 
				new LootItem(897, 1, 20)),
		BLURBERRY_BARMAN(75, 792, "Get your hands off my valuables human",
				new LootItem(10, 200, 22), 
				new LootItem(10, 400, 18), 
				new LootItem(152, 1, 10),
				new LootItem(34, 1, 15), 
				new LootItem(895, 1, 15), 
				new LootItem(897, 1, 20)),
		HERO(80, 1096, "Get your hands off my valuables",
				new LootItem(10, 100, 25), 
				new LootItem(10, 200, 15), 
				new LootItem(10, 300, 10),
				new LootItem(612, 1, 10), 
				new LootItem(142, 1, 14), 
				new LootItem(152, 1, 5), 
				new LootItem(38, 2, 10), 
				new LootItem(619, 1, 5), 
				new LootItem(161, 1, 1));

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
		BAKERS_STALL(325, 5, 64, 325, 5000,
				new LootItem(330, 1, 100)),
		SILK_STALL(326, 20, 96, 326, 8000,
				new LootItem(200, 1, 100)),
		FUR_STALL(327, 35, 144, 327, 15000,
				new LootItem(541, 1, 10),
				new LootItem(146, 1, 100)),
		SILVER_STALL(328, 50, 216, 328, 30000,
				new LootItem(383, 1, 100)),
		SPICES_STALL(329, 65, 324, 329, 80000,
				new LootItem(707, 1, 100)),
		GEMS_STALL(330, 75, 640, 330, 180000,
				new LootItem(160, 1, 65),
				new LootItem(159, 1, 20),
				new LootItem(158, 1, 10),
				new LootItem(157, 1, 5));

		ArrayList<LootItem> lootTable;
		private int xp;
		private int requiredLevel;
		private int respawnTime;
		private int ownerID;

		Stall(int ownerID, int req, int xp, int ownerNpc, int respawnTime, LootItem... loot) {
			this.ownerID = ownerID;
			this.setXp(xp);
			this.setRespawnTime(respawnTime);
			this.setRequiredLevel(req);
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
	}

	public void stallThieving(Player player, GameObject object, Stall stall) {
		player.setBusyTimer(1200);
		String objectName = object.getGameObjectDef().getName().toLowerCase();

		if (stall.equals(Stall.BAKERS_STALL))
			player.message("You attempt to steal cake from the " + objectName);
		else
			player.message("You attempt to steal " + objectName.replaceAll("stall", "") + " from the " + objectName);
		sleep(800);
		if (player.getSkills().getLevel(17) < stall.getRequiredLevel()) {
			player.message("Your theiving ability is not high enough to thieve from stall.");
			return;
		}

		Npc shopkeeper = Functions.getNearestNpc(player, stall.getOwnerID(), 8);
		Npc guard = null;
		if (stall.equals(Stall.BAKERS_STALL)) {
			guard = getMultipleNpcsInArea(player, 5, 65);
		} else if (stall.equals(Stall.SILVER_STALL) || stall.equals(Stall.SPICES_STALL) || stall.equals(Stall.FUR_STALL)
				|| stall.equals(Stall.SILK_STALL)) {
			guard = getMultipleNpcsInArea(player, 5, 65, 322);
		} else if (stall.equals(Stall.GEMS_STALL)) {
			guard = getMultipleNpcsInArea(player, 5, 65, 322, 324);
		}

		if (shopkeeper != null) {
			if (canBeSeen(shopkeeper.getX(), shopkeeper.getY(), player.getX(), player.getY())) {
				Functions.npcYell(player, shopkeeper, "Hey thats mine");
				if (!player.getCache().hasKey("stolenFrom" + stall.getOwnerID())) {
					player.getCache().store("stolenFrom" + stall.getOwnerID(), true);
				}
				return;
			}
		}
		if (guard != null) {
			if (canBeSeen(guard.getX(), guard.getY(), player.getX(), player.getY())) {
				Functions.npcYell(player, guard, "Hey! Get your hands off there!");
				player.setAttribute("stolenFrom" + stall.getOwnerID(), true);
				guard.setChasing(player);
				return;
			}
		}

		int random = DataConversions.random(1, 100);
		Item selectedLoot = null;
		for (LootItem loot : stall.lootTable) {
			if (loot.getChance() >= random) {
				selectedLoot = new Item(loot.getId(), loot.getAmount());
				break;
			}
		}
		if (selectedLoot == null) {
			selectedLoot = new Item(stall.lootTable.get(0).getId(), stall.lootTable.get(0).getAmount());
			return;
		}
		if (player.getFatigue() >= 7500)
			player.message("@gre@You are too tired to gain experience, get some rest");

		player.getInventory().add(selectedLoot);
		player.message("You steal " + selectedLoot.getDef().getName().toLowerCase() + " from the stall");

		player.incExp(17, stall.getXp(), true);
		World.getWorld().replaceGameObject(object,
				new GameObject(object.getLocation(), 341, object.getDirection(), object.getType()));

		World.getWorld().delayedSpawnObject(object.getLoc(), stall.getRespawnTime());
	}

	public void handleChestThieving(Player player, GameObject obj) {
		player.setBusyTimer(3000);
		int req = 1;
		int respawnTime = 0;
		ArrayList<LootItem> loot = new ArrayList<LootItem>();
		Point teleLoc = null;
		int xp = 0;
		switch (obj.getID()) {
		case 334:
			// 10gp Chest
			req = 13;
			xp = 30;
			respawnTime = 10000;
			loot.add(new LootItem(10, 10, 100));
			break;
			// Nature-rune Chest
		case 335:
			req = 28;
			xp = 100;
			respawnTime = 25000;
			loot = getLootAsList(new LootItem(10, 3, 100), new LootItem(40, 1, 100));
			break;
		case 336:
			// 50gp Chest
			req = 43;
			xp = 500;
			respawnTime = 100000;
			loot.add(new LootItem(10, 50, 100));
			break;
		case 337:
			// blood Chest
			req = 59;
			xp = 600;
			respawnTime = 250000;
			loot = getLootAsList(new LootItem(10, 500, 100), new LootItem(619, 2, 100));
			teleLoc = Point.location(614, 568);
			break;
		case 338:
			// paladin Chest
			req = 72;
			xp = 2000;
			respawnTime = 500000;
			loot = getLootAsList(new LootItem(10, 1000, 100), new LootItem(545, 1, 100),
					new LootItem(154, 1, 100), new LootItem(160, 1, 100));
			teleLoc = Point.location(523, 606);
			break;
		}

		player.message("You search the chest for traps");
		sleep(1200);
		if (player.getSkills().getLevel(17) < req) {
			player.message("You find nothing");
			return;
		}
		if (player.getFatigue() >= 7500) {
			player.message("You are too tired to thieve here");
			return;
		}

		player.message("You find a trap on the chest");
		sleep(1200);
		player.message("You disable the trap");

		openChest(obj);
		replaceObject(obj, new GameObject(obj.getLocation(), 340, obj.getDirection(), obj.getType()));

		message(player, "You open the chest");
		int random = DataConversions.random(1, 100);
		Collections.sort(loot);
		for (LootItem l : loot) {
			if (l.getChance() >= random) {
				player.getInventory().add(new Item(l.getId(), l.getAmount()));
			}
		}
		player.incExp(17, xp, true);
		message(player, "You find treasure inside!");
		World.getWorld().delayedSpawnObject(obj.getLoc(), respawnTime);
		if (teleLoc != null) {
			message(player, "suddenly a second magical trap triggers");
			player.teleport(teleLoc.getX(), teleLoc.getY(), true);
		}
	}

	private ArrayList<LootItem> getLootAsList(LootItem... lootItem) {
		ArrayList<LootItem> l = new ArrayList<LootItem>();
		for (LootItem loot : lootItem) {
			l.add(loot);
		}
		return l;
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		String formattedName = obj.getGameObjectDef().getName().toUpperCase().replaceAll(" ", "_");

		if (formattedName.contains("STALL") && !formattedName.equals("TEA_STALL")) {
			if (obj.getGameObjectDef().getName().equalsIgnoreCase("empty stall")) {
				return false;
			}
			if (!GameServer.MEMBER_WORLD) {
				player.message(player.MEMBER_MESSAGE);
				return false;
			}
			Stall stall = Stall.valueOf(formattedName);
			if (stall != null) {
				return true;
			}
		} else if (obj.getID() >= 334 && obj.getID() <= 339) {
			if (!GameServer.MEMBER_WORLD) {
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
	public boolean blockNpcCommand(Npc n, String command, Player p) {
		if(command.equalsIgnoreCase("pickpocket")) {
			Pickpocket pickpocket = null;
			try {
				pickpocket = Pickpocket.valueOf(n.getDef().getName().toUpperCase().replace(" ", "_"));
			} catch(Exception e) {
				//Ignore..
			}

			if (pickpocket != null) {
				if (!GameServer.MEMBER_WORLD) {
					p.message(p.MEMBER_MESSAGE);
					return false;
				}
				return true;
			} else {
				p.message("You can't pickpocket that person, it has not been implemented yet");
			}

		}
		return false;
	}

	public void doPickpocket(final Player player, final Npc npc, final Pickpocket pickpocket) {
		player.face(npc);
		if (player.getSkills().getLevel(17) < pickpocket.getRequiredLevel()) {
			player.message("Your theiving ability is not high enough to thieve the " + npc.getDef().getName());
			return;
		}
		if (npc.inCombat()) {
			player.message("I can't get close enough");
			return;
		}
		final ArrayList<LootItem> lootTable = (ArrayList<LootItem>) pickpocket.getLootTable().clone();
		player.playerServerMessage(MessageType.QUEST, "You attempt to pick the " + npc.getDef().getName().toLowerCase() + "'s pocket");
		player.setBatchEvent(new BatchEvent(player, 1300, Formulae.getRepeatTimes(player, THIEVING)) {
			@Override
			public void action() {

				player.setBusyTimer(1300);
				npc.setBusyTimer(1300 * 2);
				if (npc.inCombat()) {
					interrupt();
					return;
				}
				boolean succeededPickpocket = succeedThieving(player, pickpocket.getRequiredLevel());
				if (succeededPickpocket) {
					if (player.getFatigue() >= 7500)
						player.message("@gre@You are too tired to gain experience, get some rest");

					player.incExp(17, pickpocket.getXp(), true);
					Item selectedLoot = null;
					int total = 0;
					for (LootItem loot : lootTable) {
						total += loot.getChance();
					}
					int hit = DataConversions.random(0, total);
					total = 0;
					for (LootItem loot : lootTable) {
						if (loot.getChance() >= 100) {
							player.getInventory().add(new Item(loot.getId(), loot.getAmount()));
							continue;
						}
						if (hit >= total && hit < (total + loot.getChance())) {
							if(loot.getId() == -1) {
								player.message("You find nothing to steal");
								return;
							}
							selectedLoot = (new Item(loot.getId(), loot.getAmount()));
							break;
						}
						total += loot.getChance();
					}
					player.message("You pick the " + npc.getDef().getName().toLowerCase() + "'s pocket");
					if (selectedLoot != null) {
						player.getInventory().add(selectedLoot);
					} 
				} else {
					player.face(npc);
					player.setBusyTimer(0);
					npc.setBusyTimer(0);
					setDelay(650);
					player.playerServerMessage(MessageType.QUEST, "You fail to pick the " + npc.getDef().getName().toLowerCase() + "'s pocket");
					npc.getUpdateFlags()
					.setChatMessage(new ChatMessage(npc, pickpocket.shoutMessage, player));
					interrupt();
					npc.startCombat(player);
				}
			}
		});
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		String formattedName = obj.getGameObjectDef().getName().toUpperCase().replaceAll(" ", "_");
		if (formattedName.contains("STALL")) {
			if (obj.getGameObjectDef().getName().equalsIgnoreCase("empty stall")) {
				return;
			}
			Stall stall = Stall.valueOf(formattedName);
			if (stall != null) {
				stallThieving(player, obj, stall);
			}
		} else if (obj.getID() >= 334 && obj.getID() <= 339) {
			if (command.contains("trap")) {
				handleChestThieving(player, obj);
			} else {
				player.message("You have activated a trap on the chest");
				player.damage(DataConversions.random(0, 8));

			}
		} else if (obj.getID() == 379) { // HEMENSTER CHEST HARDCODE
			if (command.equalsIgnoreCase("Open")) {
				player.message("This chest is locked");
			} else {
				player.setBusyTimer(3000);
				player.message("you attempt to pick the lock");
				if (player.getFatigue() >= 7500) {
					player.message("You are too tired to thieve here");
					player.setBusyTimer(0);
					return;
				}
				if (player.getSkills().getLevel(17) < 47) {
					player.message("You are not a high enough level to pick this lock");
					player.setBusyTimer(0);
					return;
				}
				if (!hasItem(player, 714)) {
					player.message("You need a lockpick for this lock");
					player.setBusyTimer(0);
					return;
				}
				message(player, "You manage to pick the lock");

				openChest(obj);
				message(player, "You open the chest");

				message(player, "You find a treasure inside!");

				player.incExp(17, 600, true);
				addItem(player, 10, 20);
				addItem(player, 671, 5);

				World.getWorld().replaceGameObject(obj,
						new GameObject(obj.getLocation(), 340, obj.getDirection(), obj.getType()));
				World.getWorld().delayedSpawnObject(obj.getLoc(), 150000);
			}
		}
	}

	private boolean canBeSeen(int fromX, int fromY, int targetX, int targetY) {
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
			if ((World.getWorld().getTile(fromX, fromY).traversalMask & 64) != 0) {
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

	public static boolean succeedPickLockThieving(Player player, int req_level) {
		int level_diff = player.getSkills().getLevel(17) - req_level;

		int percent = DataConversions.random(1, 100);
		if (level_diff < 0)
			level_diff = 0;

		if (level_diff > 40) {
			level_diff = 75;
		} else {
			level_diff = (int) (player.getSkills().getLevel(17) * (double) 0.2D) + 50;
		}
		if (hasItem(player, 714, 1)) {
			level_diff += 10;
		}
		return percent <= level_diff;
	}

	/*
	 * public static boolean succeedThieving(Player p, int req_level) { int
	 * levelDiff = p.getSkills().getLevel(17) - req_level;
	 * 
	 * if (levelDiff < 0) { return false; }
	 * 
	 * System.out.println("Thieving: " + DataConversions.random(0, (levelDiff +
	 * 2) * 2)); return DataConversions.random(0, (levelDiff + 2) * 2) != 0; }
	 */
	private static boolean succeedThieving(Player player, int req_level) {
		int level_diff = player.getSkills().getLevel(17) - req_level;

		int percent = DataConversions.random(1, 100);
		if (level_diff < 0)
			level_diff = 0;

		if (level_diff > 10) {
			level_diff = 70;
		}
		if (level_diff > 20) {
			level_diff = 80;
		}
		if (level_diff > 30) {
			level_diff = 90;
		} else {
			level_diff = 56 + level_diff;
		}
		return percent <= level_diff;
	}

	/**
	 * Nature rune chest door id (94) [3.11.2013 22:02:55] Kevin: 10gp chest
	 * door id (93) [3.11.2013 22:05:47] Kevin: house where u can find nature
	 * chest + 10gp chest door id (94) [3.11.2013 22:07:17] Kevin: Chaos druid
	 * tower door id (96) [3.11.2013 22:11:49] Kevin: yanille druid door id(162)
	 * [3.11.2013 22:15:08] Kevin: axe huts door id (100) [3.11.2013 22:17:28]
	 * Kevin: pirate hut door id (99) [3.11.2013 22:22:29] Kevin:paladin 2nd
	 * floor door id (97) [3.11.2013 22:23:46] Kevin: the paladin chest give all
	 * the items it says it gie all at once [3.11.2013 22:23:58] Kevin: when you
	 * loot the chest you get teleported at 523, 606 [3.11.2013
	 * 
	 * @param player
	 * @param obj
	 */
	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		if (obj.getID() >= 93 && obj.getID() <= 97 || obj.getID() >= 99 & obj.getID() <= 100 || obj.getID() == 162) {
			if (!GameServer.MEMBER_WORLD) {
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
		case 94: // Nature rune chest + 50 gp chest door id (94)
			if (obj.getX() == 586 && obj.getY() == 581 || obj.getX() == 539 && obj.getY() == 599
			|| obj.getX() == 581 && obj.getY() == 580) {
				req = 16;
				exp = 60;
				if (player.getX() == 539 && player.getY() >= 599) {
					goThrough = true;
				} else if (player.getX() <= 585 && player.getY() == 581) {
					goThrough = true;
				} else if (player.getX() >= 581 && player.getY() == 580) {
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
			message(player, 1200, "you attempt to pick the lock");

			if (getCurrentLevel(player, THIEVING) < req) {
				player.message("You are not a high enough level to pick this lock");
				return;
			}
			if (!hasItem(player, 714) && requiresLockpick) {
				player.message("You need a lockpick for this lock");
				return;
			}
			if (succeedPickLockThieving(player, req) && !goThrough) {
				player.message("You manage to pick the lock");
				doDoor(obj, player);
				player.message("You go through the door");
				if (player.getFatigue() >= 7500) {
					player.message("@gre@You are too tired to gain experience, get some rest");
					return;
				}
				player.incExp(17, (int) exp, true);
			} else {
				player.message("You fail to pick the lock");
			}
		}
	}

	@Override
	public void onNpcCommand(Npc n, String command, Player p) {
		if(command.equalsIgnoreCase("pickpocket")) {
			Pickpocket pickpocket = Pickpocket.valueOf(n.getDef().getName().toUpperCase().replace(" ", "_"));
			if (pickpocket != null) {
				doPickpocket(p, n, pickpocket);
			} else {
				p.message("ERROR: Pickpocket handler not found.");
			}
		}
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		handlePicklock(obj, p, click);
	}

	static class LootItem implements Comparable<LootItem> {
		private int chance;
		private final int id;
		private final int amount;

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
