package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.MiniEvent;
import com.openrsc.server.event.rsc.impl.CustomProjectileEvent;
import com.openrsc.server.event.rsc.impl.ObjectRemover;
import com.openrsc.server.event.rsc.impl.ProjectileEvent;
import com.openrsc.server.external.ItemSmeltingDef;
import com.openrsc.server.external.ReqOreDef;
import com.openrsc.server.external.SpellDef;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.action.WalkToPointAction;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.entity.update.Damage;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.sql.query.logs.GenericLog;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import static com.openrsc.server.plugins.Functions.*;

public class SpellHandler implements PacketHandler {

	private static TreeMap<Integer, Item[]> staffs = new TreeMap<Integer, Item[]>();
	private static final String AMULET = "amulet";
	private static final String RING = "ring";
	private static final String NECKLACE = "necklace";
	private static final String DEFAULT = "";

	static {
		staffs.put(com.openrsc.server.constants.ItemId.FIRE_RUNE.id(), new Item[]{new Item(com.openrsc.server.constants.ItemId.STAFF_OF_FIRE.id()), new Item(com.openrsc.server.constants.ItemId.BATTLESTAFF_OF_FIRE.id()), new Item(com.openrsc.server.constants.ItemId.ENCHANTED_BATTLESTAFF_OF_FIRE.id())}); // Fire-Rune
		staffs.put(com.openrsc.server.constants.ItemId.WATER_RUNE.id(), new Item[]{new Item(com.openrsc.server.constants.ItemId.STAFF_OF_WATER.id()), new Item(com.openrsc.server.constants.ItemId.BATTLESTAFF_OF_WATER.id()), new Item(com.openrsc.server.constants.ItemId.ENCHANTED_BATTLESTAFF_OF_WATER.id())}); // Water-Rune
		staffs.put(com.openrsc.server.constants.ItemId.AIR_RUNE.id(), new Item[]{new Item(com.openrsc.server.constants.ItemId.STAFF_OF_AIR.id()), new Item(com.openrsc.server.constants.ItemId.BATTLESTAFF_OF_AIR.id()), new Item(com.openrsc.server.constants.ItemId.ENCHANTED_BATTLESTAFF_OF_AIR.id())}); // Air-Rune
		staffs.put(com.openrsc.server.constants.ItemId.EARTH_RUNE.id(), new Item[]{new Item(com.openrsc.server.constants.ItemId.STAFF_OF_EARTH.id()), new Item(com.openrsc.server.constants.ItemId.BATTLESTAFF_OF_EARTH.id()), new Item(com.openrsc.server.constants.ItemId.ENCHANTED_BATTLESTAFF_OF_EARTH.id())}); // Earth-Rune
	}

	private static boolean canCast(Player player) {
		if (!player.castTimer()) {
			player.message("You need to wait " + player.getSpellWait() + " seconds before you can cast another spell");
			player.resetPath();
			return false;
		}
		return true;
	}

	public static boolean checkAndRemoveRunes(Player player, SpellDef spell) {
		for (Entry<Integer, Integer> e : spell.getRunesRequired()) {
			boolean skipRune = false;
			if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
				for (Item staff : getStaffs(e.getKey())) {
					if (player.getEquipment().hasEquipped(staff.getID()) != -1) {
						skipRune = true;
					}
				}
			} else {
				for (Item staff : getStaffs(e.getKey())) {
					if (player.getInventory().contains(staff)) {
						for (Item item : player.getInventory().getItems()) {
							if (item.equals(staff) && item.isWielded()) {
								skipRune = true;
								break;
							}
						}
					}
				}
			}

			if (skipRune) {
				continue;
			}
			if (player.getInventory().countId(e.getKey()) < e.getValue()) {
				player.setSuspiciousPlayer(true, "player not all reagents for spell");
				player.message("You don't have all the reagents you need for this spell");
				return false;
			}
			player.getInventory().remove(e.getKey(), e.getValue());
		}
		/*
		for (Entry<Integer, Integer> e : spell.getRunesRequired()) {
			boolean skipRune = false;
			for (Item staff : getStaffs(e.getKey())) {
				if (player.getInventory().contains(staff)) {
					for (Item item : player.getInventory().getItems()) {
						if (item.equals(staff) && item.isWielded()) {
							skipRune = true;
							break;
						}
					}
				}
			}
			if (skipRune) {
				continue;
			}
			player.getInventory().remove(e.getKey(), e.getValue());
		}

		 */


		return true;
	}

	private static Item[] getStaffs(int runeID) {
		Item[] items = staffs.get(runeID);
		if (items == null) {
			return new Item[0];
		}
		return items;
	}

	public void handlePacket(Packet p, Player player) throws Exception {

		if ((player.isBusy() && !player.inCombat()) || player.isRanging()) {
			return;
		}
		if (!canCast(player)) {
			return;
		}
		int pID = p.getID();
		int CAST_ON_SELF = OpcodeIn.CAST_ON_SELF.getOpcode();
		int CAST_ON_PLAYER = OpcodeIn.PLAYER_CAST_SPELL.getOpcode();
		int CAST_ON_NPC = OpcodeIn.NPC_CAST_SPELL.getOpcode();
		int CAST_ON_INV_ITEM = OpcodeIn.ITEM_CAST_SPELL.getOpcode();
		int CAST_ON_DOOR = OpcodeIn.WALL_OBJECT_CAST.getOpcode();
		int CAST_ON_GAME_OBJECT = OpcodeIn.OBJECT_CAST.getOpcode();
		int CAST_ON_GROUNDITEM = OpcodeIn.GROUND_ITEM_CAST_SPELL.getOpcode();
		int CAST_ON_LAND = OpcodeIn.CAST_ON_LAND.getOpcode();

		player.resetAllExceptDueling();
		int idx = p.readShort();
		if (idx < 0 || idx >= 49) {
			player.setSuspiciousPlayer(true, "idx < 0 or idx >= 49");
			return;
		}
		SpellDef spell = player.getWorld().getServer().getEntityHandler().getSpellDef(idx);
		if (spell.isMembers() && !player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.message("You need to login to a members world to use this spell");
			player.resetPath();
			return;
		}

		// Services.lookup(DatabaseManager.class).addQuery(new
		// GenericLog(player.getUsername() + " tried to cast spell 49 at " +
		// player.getLocation()));

		if (player.getSkills().getLevel(com.openrsc.server.constants.Skills.MAGIC) < spell.getReqLevel()) {
			player.setSuspiciousPlayer(true, "player magic ability not high enough");
			player.message("Your magic ability is not high enough for this spell.");
			player.resetPath();
			return;
		}
		if (!Formulae.castSpell(spell, player.getSkills().getLevel(com.openrsc.server.constants.Skills.MAGIC), player.getMagicPoints())) {
			player.message("The spell fails! You may try again in 20 seconds");
			player.playSound("spellfail");
			player.setSpellFail();
			player.resetPath();
			return;
		}
		if (pID == CAST_ON_SELF) { // Cast on self!
			if (player.getDuel().isDuelActive()) {
				player.message("You can't do that during a duel!");
				return;
			}
			if (spell.getSpellType() == 0) {
				handleTeleport(player, spell, idx);
				return;
			}
			handleGroundCast(player, spell, idx);
		} else if (pID == CAST_ON_PLAYER) { // Cast on player
			if (spell.getSpellType() == 1 || spell.getSpellType() == 2) {
				Player affectedPlayer = player.getWorld().getPlayer(p.readShort());

				if (player.getDuel().isDuelActive() && player.getDuel().getDuelSetting(1)) {
					player.message("Magic cannot be used during this duel!");
					return;
				}
				if (affectedPlayer == null) { // This shouldn't happen
					player.resetPath();
					return;
				}

				if (affectedPlayer.getLocation().inBounds(220, 108, 225, 111)) { // mage arena block real rsc.
					player.message("Here kolodion protects all from your attack");
					player.resetPath();
					return;
				}

				if (player.withinRange(affectedPlayer, 4)) {
					player.resetPath();
				}
				if (player.getWorld().getServer().getPluginHandler().blockDefaultAction("PlayerMage",
					new Object[]{player, affectedPlayer, idx})) {
					return;
				}
				handleMobCast(player, affectedPlayer, idx);
			}
		} else if (pID == CAST_ON_NPC) { // Cast on npc
			if (player.getDuel().isDuelActive()) {
				player.message("You can't do that during a duel!");
				return;
			}
			if (spell.getSpellType() == 2) {
				Npc affectedNpc = player.getWorld().getNpc(p.readShort());
				if (affectedNpc == null) { // This shouldn't happen
					player.resetPath();
					return;
				}
				if (affectedNpc.getID() == com.openrsc.server.constants.NpcId.DELRITH.id()) {
					player.message("Delrith can not be attacked without the Silverlight sword");
					return;
				}

				if (affectedNpc.getID() == com.openrsc.server.constants.NpcId.LUCIEN_EDGE.id() && (player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -1
					|| player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -2)) {
					player.message("You have already completed this quest");
					return;
				}
				if (affectedNpc.getID() == com.openrsc.server.constants.NpcId.LUCIEN_EDGE.id() && !player.getInventory().wielding(com.openrsc.server.constants.ItemId.PENDANT_OF_ARMADYL.id())) {
					npcTalk(player, affectedNpc, "I'm sure you don't want to attack me really",
						"I am your friend");
					message(player, "You decide you don't want to attack Lucien really",
						"He is your friend");
					return;
				}

				if (player.withinRange(affectedNpc, 4)) {
					player.resetPath();
				}
				if (affectedNpc.getID() == com.openrsc.server.constants.NpcId.CHRONOZON.id()) {
					/** FAMILY CREST CHRONOZ **/
					if (spell.getName().contains("blast")) {
						String elementalType = spell.getName().split(" ")[0].toLowerCase();
						player.message("chronozon weakens");
						if (!player.getAttribute("chronoz_" + elementalType, false)) {
							player.setAttribute("chronoz_" + elementalType, true);
						}
					}
				}

				if (player.getWorld().getServer().getPluginHandler().blockDefaultAction("PlayerMageNpc",
					new Object[]{player, affectedNpc})) {
					return;
				}

				handleMobCast(player, affectedNpc, idx);
			}
		} else if (pID == CAST_ON_INV_ITEM) { // Cast on inventory item
			if (player.getDuel().isDuelActive()) {
				player.message("You can't do that during a duel!");
				return;
			}
			if (spell.getSpellType() == 3) {
				Item item = player.getInventory().get(p.readShort());
				if (item == null) { // This shoudln't happen
					player.resetPath();
					return;
				}
				if (player.getWorld().getServer().getPluginHandler().blockDefaultAction("PlayerMageItem",
					new Object[]{player, (Integer)item.getID(), (Integer)idx})) {
					return;
				}
				handleItemCast(player, spell, idx, item);
			}
		} else if (pID == CAST_ON_DOOR) { // Cast on door - type 4
			if (player.getDuel().isDuelActive()) {
				player.message("You can't do that during a duel!");
				return;
			}
			player.message("@or1@This type of spell is not yet implemented.");
		} else if (pID == CAST_ON_GAME_OBJECT) { // Cast on game object - type 5
			if (player.getDuel().isDuelActive()) {
				player.message("You can't do that during a duel!");
				return;
			}
			int objectX = p.readShort();
			int objectY = p.readShort();
			GameObject gameObject = player.getViewArea().getGameObject(Point.location(objectX, objectY));
			if (gameObject == null) {
				return;
			}

			if (player.getWorld().getServer().getPluginHandler().blockDefaultAction("PlayerMageObject",
				new Object[]{player, gameObject, spell})) {
				return;
			}

			int chargedOrb = com.openrsc.server.constants.ItemId.NOTHING.id();
			switch (idx) {
				case 40:
					if (gameObject.getID() == 303) {
						chargedOrb = com.openrsc.server.constants.ItemId.AIR_ORB.id();
					} else {
						player.playerServerMessage(MessageType.QUEST, "This spell can only be used on air obelisks");
					}
					break;
				case 29:
					if (gameObject.getID() == 300) {
						chargedOrb = com.openrsc.server.constants.ItemId.WATER_ORB.id();
					} else {
						player.playerServerMessage(MessageType.QUEST, "This spell can only be used on water obelisks");
					}
					break;
				case 36:
					if (gameObject.getID() == 304) {
						chargedOrb = com.openrsc.server.constants.ItemId.EARTH_ORB.id();
					} else {
						player.playerServerMessage(MessageType.QUEST, "This spell can only be used on earth obelisks");
					}
					break;
				case 38:
					if (gameObject.getID() == 301) {
						chargedOrb = com.openrsc.server.constants.ItemId.FIRE_ORB.id();
					} else {
						player.playerServerMessage(MessageType.QUEST, "This spell can only be used on fire obelisks");
					}
					break;
			}
			if (chargedOrb == com.openrsc.server.constants.ItemId.NOTHING.id()) {
				return;
			}

			if (!checkAndRemoveRunes(player, spell)) {
				return;
			}
			player.getInventory().add(new Item(chargedOrb));
			player.lastCast = System.currentTimeMillis();
			player.playSound("spellok");
			player.playerServerMessage(MessageType.QUEST, "You succesfully charge the orb");
			player.incExp(com.openrsc.server.constants.Skills.MAGIC, spell.getExp(), true);
			player.setCastTimer();
		} else if (pID == CAST_ON_GROUNDITEM) { // Cast on ground items
			if (player.getDuel().isDuelActive()) {
				player.message("You can't do that during a duel!");
				return;
			}
			Point location = Point.location(p.readShort(), p.readShort());
			int itemId = p.readShort();
			GroundItem affectedItem = player.getViewArea().getGroundItem(itemId, location);
			if (affectedItem == null) {
				return;
			}
			handleItemCast(player, spell, idx, affectedItem);
		} else if (pID == CAST_ON_LAND) {
			if (player.getDuel().isDuelActive()) {
				player.message("You can't do that during a duel!");
				return;
			}
			handleGroundCast(player, spell, idx);
		}
		return;
	}

	private void finalizeSpellNoMessage(Player player, SpellDef spell) {
		SpellHandler.finalizeSpell(player, spell, null);
	}

	public static void finalizeSpell(Player player, SpellDef spell, String message) {
		player.lastCast = System.currentTimeMillis();
		player.playSound("spellok");
		// don't display a message if message is null (example superheat)
		if (message != null) {
			player.playerServerMessage(MessageType.QUEST, message.trim().isEmpty() ? "Cast spell successfully" : message);
		}
		player.incExp(com.openrsc.server.constants.Skills.MAGIC, spell.getExp(), true);
		player.setCastTimer();
	}

	public void godSpellObject(Player player, Mob affectedMob, int spell) {
		switch (spell) {
			case 33:
				GameObject guthix = new GameObject(affectedMob.getWorld(), affectedMob.getLocation(), 1142, 0, 0);
				player.getWorld().registerGameObject(guthix);
				player.getWorld().getServer().getGameEventHandler().add(new ObjectRemover(player.getWorld(), guthix, 2));
				break;
			case 34:
				GameObject sara = new GameObject(affectedMob.getWorld(), affectedMob.getLocation(), 1031, 0, 0);
				player.getWorld().registerGameObject(sara);
				player.getWorld().getServer().getGameEventHandler().add(new ObjectRemover(player.getWorld(), sara, 2));
				break;
			case 35:
				GameObject zammy = new GameObject(affectedMob.getWorld(), affectedMob.getLocation(), 1036, 0, 0);
				player.getWorld().registerGameObject(zammy);
				player.getWorld().getServer().getGameEventHandler().add(new ObjectRemover(player.getWorld(), zammy, 2));
				break;
			case 47:
				GameObject charge = new GameObject(affectedMob.getWorld(), affectedMob.getLocation(), 1147, 0, 0);
				player.getWorld().registerGameObject(charge);
				player.getWorld().getServer().getGameEventHandler().add(new ObjectRemover(player.getWorld(), charge, 2));
				break;
		}
		if (spell != 47) {
			double lowersBy = -1;
			int affectsStat = -1;
			if (spell == 33) {
				lowersBy = 0.02;
				affectsStat = 1; // defense
			} else if (spell == 34) { // SARADOMIN
				lowersBy = 1;
				affectsStat = 5; // prayer
			} else if (spell == 35) {
				lowersBy = 0.02;
				affectsStat = 6; // magic
			}
			/* How much to lower the stat */
			int lowerBy = (spell != 34 ? (int) Math.ceil((affectedMob.getSkills().getLevel(affectsStat) * lowersBy))
				: (int) lowersBy);

			/* New current level */
			final int newStat = affectedMob.getSkills().getLevel(affectsStat) - lowerBy;
			/* Lowest stat you can weaken to with this spell */
			final int maxWeaken = affectedMob.getSkills().getMaxStat(affectsStat)
				- (int) Math.ceil((affectedMob.getSkills().getLevel(affectsStat) * lowersBy) * 4);

			if (newStat < maxWeaken && spell != 34) {
				player.playerServerMessage(MessageType.QUEST, "Your opponent already has weakened " + player.getWorld().getServer().getConstants().getSkills().getSkillName(affectsStat));
				return;
			}
			if (player.getDuel().isDuelActive() && affectedMob.isPlayer()) {
				Player aff = (Player) affectedMob;
				aff.message("Your " + aff.getWorld().getServer().getConstants().getSkills().getSkillName(affectsStat) + " has been reduced by the spell!");
			}
			affectedMob.getSkills().setLevel(affectsStat, newStat);
		}
	}

	private void handleGroundCast(Player player, SpellDef spell, int id) {
		switch (id) {
			case 7: // Bones to bananas
				if (!checkAndRemoveRunes(player, spell)) {
					return;
				}
				Iterator<Item> inventory = player.getInventory().iterator();
				int boneCount = 0;
				while (inventory.hasNext()) {
					Item i = inventory.next();
					if (i.getID() == com.openrsc.server.constants.ItemId.BONES.id()) {
						inventory.remove();
						boneCount++;
					}
				}
				if (boneCount == 0) {
					player.message("You aren't holding any bones!");
					return;
				}
				for (int i = 0; i < boneCount; i++) {
					player.getInventory().add(new Item(com.openrsc.server.constants.ItemId.BANANA.id()));
				}
				// needs verify if default message
				finalizeSpell(player, spell, DEFAULT);
				break;
			case 47: // Charge
			/*if (!player.getLocation().isMembersWild()) {
				player.message("Members content can only be used in wild levels: " + World.membersWildStart + " - "
						+ World.membersWildMax);
				return;
			}*/
				if (!player.getLocation().inMageArena()) {
					if ((!player.getCache().hasKey("Flames of Zamorak_casts") && !player.getCache().hasKey("Saradomin strike_casts") && !player.getCache().hasKey("Claws of Guthix_casts"))
						||
						((player.getCache().hasKey("Saradomin strike_casts") && player.getCache().getInt("Saradomin strike_casts") < 100))
						||
						((player.getCache().hasKey("Flames of Zamorak_casts") && player.getCache().getInt("Flames of Zamorak_casts") < 100))
						||
						((player.getCache().hasKey("Claws of Guthix_casts") && player.getCache().getInt("Claws of Guthix_casts") < 100))) {
						player.message("this spell can only be used in the mage arena");
						return;
					}
				}
				if (player.getViewArea().getGameObject(player.getLocation()) != null) {
					player.message("You can't charge power here, please move to a different area");
					return;
				}
				if (!checkAndRemoveRunes(player, spell)) {
					return;
				}
				player.message("@gre@You feel charged with magic power");
				player.addCharge(6 * 60000);
				// charge is on self
				godSpellObject(player, player, 47);
				finalizeSpell(player, spell, DEFAULT);
				return;
		}
	}

	private void handleItemCast(Player player, SpellDef spell, int id, Item affectedItem) {
		String jewelryType = "";
		switch (id) {
			case 3: // Enchant lvl-1 Sapphire amulet
				if (affectedItem.getID() == com.openrsc.server.constants.ItemId.SAPPHIRE_AMULET.id()
				|| (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB
					&& (affectedItem.getID() == com.openrsc.server.constants.ItemId.SAPPHIRE_RING.id() || affectedItem.getID() == com.openrsc.server.constants.ItemId.OPAL_RING.id()))) {
					if (!checkAndRemoveRunes(player, spell)) {
						return;
					}
					int itemID = 0;
					switch(com.openrsc.server.constants.ItemId.getById(affectedItem.getID())) {
						case SAPPHIRE_AMULET:
							itemID = com.openrsc.server.constants.ItemId.SAPPHIRE_AMULET_OF_MAGIC.id();
							jewelryType = AMULET;
							break;
						case SAPPHIRE_RING:
							itemID = com.openrsc.server.constants.ItemId.RING_OF_RECOIL.id();
							jewelryType = RING;
							break;
						case SAPPHIRE_NECKLACE:
							jewelryType = NECKLACE;
							break;
						case OPAL_RING:
							itemID = com.openrsc.server.constants.ItemId.DWARVEN_RING.id();
							jewelryType = RING;
							break;
					}
					player.getInventory().remove(affectedItem);
					player.getInventory().add(new Item(itemID));
					finalizeSpell(player, spell, "You succesfully enchant the " + jewelryType);
				} else
					player.playerServerMessage(MessageType.QUEST, "This spell can only be used on unenchanted sapphire " + (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB ? " rings/amulets or opal rings" : "amulets"));
				break;
			case 10: // Low level alchemy
				if (affectedItem.getID() == com.openrsc.server.constants.ItemId.COINS.id()) {
					player.playerServerMessage(MessageType.QUEST, "That's already made of gold!");
					return;
				}
				if (affectedItem.getDef(player.getWorld()).getOriginalItemID() != com.openrsc.server.constants.ItemId.NOTHING.id()) {
					player.message("You can't alch noted items");
					return;
				}
				if (!checkAndRemoveRunes(player, spell)) {
					return;
				}
				// ana in barrel kept but xp allowed
				if (affectedItem.getID() == com.openrsc.server.constants.ItemId.ANA_IN_A_BARREL.id()) {
					player.message("@gre@Ana: Don't you start casting spells on me!");
					finalizeSpellNoMessage(player, spell);
				} else {
					if (player.getInventory().remove(affectedItem.getID(), 1) > -1) {
						int value = (int) (affectedItem.getDef(player.getWorld()).getDefaultPrice() * 0.4D);
						player.getInventory().add(new Item(com.openrsc.server.constants.ItemId.COINS.id(), value)); // 40%
					}
					finalizeSpell(player, spell, "Alchemy spell successful");
				}

				break;
			case 13: // Enchant lvl-2 emerald amulet
			if (affectedItem.getID() == com.openrsc.server.constants.ItemId.EMERALD_AMULET.id()
				|| (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB && affectedItem.getID() == com.openrsc.server.constants.ItemId.EMERALD_RING.id())) {
				if (!checkAndRemoveRunes(player, spell)) {
					return;
				}
				int itemID = 0;
				switch(com.openrsc.server.constants.ItemId.getById(affectedItem.getID())) {
					case EMERALD_AMULET:
						itemID = com.openrsc.server.constants.ItemId.EMERALD_AMULET_OF_PROTECTION.id();
						jewelryType = AMULET;
						break;
					case EMERALD_RING:
						itemID = com.openrsc.server.constants.ItemId.RING_OF_SPLENDOR.id();
						jewelryType = RING;
						break;
					case EMERALD_NECKLACE:
						jewelryType = NECKLACE;
						break;
				}
				player.getInventory().remove(affectedItem);
				player.getInventory().add(new Item(itemID));
				finalizeSpell(player, spell, "You succesfully enchant the " + jewelryType);
			} else
				player.playerServerMessage(MessageType.QUEST, "This spell can only be used on unenchanted emerald " + (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB ? "rings and amulets" : "amulets"));
			break;
			case 21: // Superheat item
				ItemSmeltingDef smeltingDef = affectedItem.getSmeltingDef(player.getWorld());
				if (smeltingDef == null || affectedItem.getID() == com.openrsc.server.constants.ItemId.COAL.id()) {
					player.playerServerMessage(MessageType.QUEST, "This spell can only be used on ore");
					return;
				}
				for (ReqOreDef reqOre : smeltingDef.getReqOres()) {
					if (player.getInventory().countId(reqOre.getId()) < reqOre.getAmount()) {
						if (affectedItem.getID() == com.openrsc.server.constants.ItemId.IRON_ORE.id()) {
							smeltingDef = player.getWorld().getServer().getEntityHandler().getItemSmeltingDef(9999);
							break;
						}
						if (affectedItem.getID() == com.openrsc.server.constants.ItemId.TIN_ORE.id() || affectedItem.getID() == com.openrsc.server.constants.ItemId.COPPER_ORE.id()) {
							player.playerServerMessage(MessageType.QUEST, "You also need some " + (affectedItem.getID() == com.openrsc.server.constants.ItemId.TIN_ORE.id() ? "copper" : "tin")
								+ " to make bronze");
							return;
						}
						player.playerServerMessage(MessageType.QUEST, "You need " + reqOre.getAmount() + " heaps of "
							+ player.getWorld().getServer().getEntityHandler().getItemDef(reqOre.getId()).getName().toLowerCase() + " to smelt "
							+ affectedItem.getDef(player.getWorld()).getName().toLowerCase().replaceAll("ore", ""));
						return;
					}
				}

				if (player.getSkills().getLevel(com.openrsc.server.constants.Skills.SMITHING) < smeltingDef.getReqLevel()) {
					player.playerServerMessage(MessageType.QUEST, "You need to be at least level-" + smeltingDef.getReqLevel() + " smithing to smelt "
						+ player.getWorld().getServer().getEntityHandler().getItemDef(smeltingDef.barId).getName().toLowerCase().replaceAll("bar", ""));
					return;
				}
				if (!checkAndRemoveRunes(player, spell)) {
					return;
				}
				Item bar = new Item(smeltingDef.getBarId());
				if (player.getInventory().remove(affectedItem) > -1) {
					for (ReqOreDef reqOre : smeltingDef.getReqOres()) {
						for (int i = 0; i < reqOre.getAmount(); i++) {
							player.getInventory().remove(new Item(reqOre.getId()));
						}
					}
					player.playerServerMessage(MessageType.QUEST, "You make a bar of " + bar.getDef(player.getWorld()).getName().replace("bar", "").toLowerCase());
					player.getInventory().add(bar);
					player.incExp(com.openrsc.server.constants.Skills.SMITHING, smeltingDef.getExp(), true);
				}
				finalizeSpellNoMessage(player, spell);
				break;
			case 24: // Enchant lvl-3 ruby amulet
				if (affectedItem.getID() == com.openrsc.server.constants.ItemId.RUBY_AMULET.id()
					|| (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB && affectedItem.getID() == com.openrsc.server.constants.ItemId.RUBY_RING.id())) {
					if (!checkAndRemoveRunes(player, spell)) {
						return;
					}
					int itemID = 0;
					switch(com.openrsc.server.constants.ItemId.getById(affectedItem.getID())) {
						case RUBY_AMULET:
							itemID = com.openrsc.server.constants.ItemId.RUBY_AMULET_OF_STRENGTH.id();
							jewelryType = AMULET;
							break;
						case RUBY_RING:
							itemID = com.openrsc.server.constants.ItemId.RING_OF_FORGING.id();
							jewelryType = RING;
							break;
						case RUBY_NECKLACE:
							jewelryType = NECKLACE;
							break;
					}
					player.getInventory().remove(affectedItem);
					player.getInventory().add(new Item(itemID));
					finalizeSpell(player, spell, "You succesfully enchant the " + jewelryType);
				} else
					player.playerServerMessage(MessageType.QUEST, "This spell can only be used on unenchanted ruby " + (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB ? "rings and amulets" : "amulets"));
				break;
			case 28: // High level alchemy
				if (affectedItem.getID() == com.openrsc.server.constants.ItemId.COINS.id()) {
					player.playerServerMessage(MessageType.QUEST, "That's already made of gold!");
					return;
				}
				if (affectedItem.getDef(player.getWorld()).getOriginalItemID() != com.openrsc.server.constants.ItemId.NOTHING.id()) {
					player.message("You can't alch noted items");
					return;
				}
				if (!checkAndRemoveRunes(player, spell)) {
					return;
				}
				// ana in barrel kept but xp allowed
				if (affectedItem.getID() == com.openrsc.server.constants.ItemId.ANA_IN_A_BARREL.id()) {
					player.message("@gre@Ana: Don't you start casting spells on me!");
					finalizeSpellNoMessage(player, spell);
				} else {
					if (player.getInventory().remove(affectedItem.getID(), 1) > -1) {
						int value = (int) (affectedItem.getDef(player.getWorld()).getDefaultPrice() * 0.6D);
						player.getInventory().add(new Item(com.openrsc.server.constants.ItemId.COINS.id(), value)); // 60%
					}
					finalizeSpell(player, spell, "Alchemy spell successful");
				}
				break;
			case 30: // Enchant lvl-4 diamond amulet
				if (affectedItem.getID() == com.openrsc.server.constants.ItemId.DIAMOND_AMULET.id()
						|| (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB && affectedItem.getID() == com.openrsc.server.constants.ItemId.DIAMOND_RING.id())){
					if (!checkAndRemoveRunes(player, spell)) {
						return;
					}
					int itemID = 0;
					switch(com.openrsc.server.constants.ItemId.getById(affectedItem.getID())) {
						case DIAMOND_AMULET:
							itemID = com.openrsc.server.constants.ItemId.DIAMOND_AMULET_OF_POWER.id();
							jewelryType = AMULET;
							break;
						case DIAMOND_RING:
							itemID = com.openrsc.server.constants.ItemId.RING_OF_LIFE.id();
							jewelryType = RING;
							break;
						case DIAMOND_NECKLACE:
							jewelryType = NECKLACE;
							break;
					}
					player.getInventory().remove(affectedItem);
					player.getInventory().add(new Item(itemID));
					finalizeSpell(player, spell, "You succesfully enchant the " + jewelryType);
				} else
					player.playerServerMessage(MessageType.QUEST, "This spell can only be used on unenchanted diamond " + (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB ? "rings and amulets" : "amulets"));
				break;
			case 42: // Enchant lvl-5 dragonstone amulet
				if (affectedItem.getID() == com.openrsc.server.constants.ItemId.UNENCHANTED_DRAGONSTONE_AMULET.id()
					|| (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB && affectedItem.getID() == com.openrsc.server.constants.ItemId.DRAGONSTONE_RING.id())) {
					int itemID = 0;
					switch(com.openrsc.server.constants.ItemId.getById(affectedItem.getID())) {
						case UNENCHANTED_DRAGONSTONE_AMULET:
							itemID = com.openrsc.server.constants.ItemId.DRAGONSTONE_AMULET.id();
							jewelryType = AMULET;
							break;
						case DRAGONSTONE_NECKLACE:
							jewelryType = NECKLACE;
							break;
					}
					if (!checkAndRemoveRunes(player, spell)) {
						return;
					}
					player.getInventory().remove(affectedItem);
					player.getInventory().add(new Item(itemID));
					finalizeSpell(player, spell, "You succesfully enchant the " + jewelryType);
				} else
					player.playerServerMessage(MessageType.QUEST, "This spell can only be used on unenchanted dragonstone " + (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB ? "rings and amulets" : "amulets"));
				break;

		}
		if (affectedItem.isWielded()) {
			player.getInventory().unwieldItem(affectedItem, false);
		}

	}

	private void handleItemCast(Player player, final SpellDef spell, final int id, final GroundItem affectedItem) {
		player.setStatus(Action.CASTING_GITEM);
		player.setWalkToAction(new WalkToPointAction(player, affectedItem.getLocation(), 4) {
			public void execute() {
				player.resetPath();
				if (!canCast(player) || player.getViewArea().getGroundItem(affectedItem.getID(), location) == null
					|| player.getStatus() != Action.CASTING_GITEM || affectedItem.isRemoved()) {
					return;
				}
				if (!PathValidation.checkPath(player.getWorld(), player.getLocation(), affectedItem.getLocation())) {
					player.playerServerMessage(MessageType.QUEST, "I can't see the object from here");
					player.resetPath();
					return;
				}
				player.resetAllExceptDueling();
				switch (id) {
					case 16: // Telekinetic grab
						// fluffs gets its own message
						// same case with ana
						int[] ungrabbableArr = {
							//scythe
							com.openrsc.server.constants.ItemId.SCYTHE.id(),
							//bunny ears
							com.openrsc.server.constants.ItemId.BUNNY_EARS.id(),
							//orbs
							com.openrsc.server.constants.ItemId.ORB_OF_LIGHT_WHITE.id(), com.openrsc.server.constants.ItemId.ORB_OF_LIGHT_BLUE.id(), com.openrsc.server.constants.ItemId.ORB_OF_LIGHT_PINK.id(), com.openrsc.server.constants.ItemId.ORB_OF_LIGHT_YELLOW.id(),
							//cat (underground pass)
							com.openrsc.server.constants.ItemId.KARDIA_CAT.id(),
							//god capes
							com.openrsc.server.constants.ItemId.ZAMORAK_CAPE.id(), com.openrsc.server.constants.ItemId.SARADOMIN_CAPE.id(), com.openrsc.server.constants.ItemId.GUTHIX_CAPE.id(),
							//holy grail
							com.openrsc.server.constants.ItemId.HOLY_GRAIL.id(),
							//large cogs
							com.openrsc.server.constants.ItemId.LARGE_COG_BLUE.id(), com.openrsc.server.constants.ItemId.LARGE_COG_BLACK.id(), com.openrsc.server.constants.ItemId.LARGE_COG_RED.id(), com.openrsc.server.constants.ItemId.LARGE_COG_PURPLE.id(),
							//staff of armadyl,
							com.openrsc.server.constants.ItemId.STAFF_OF_ARMADYL.id(),
							//ice arrows
							com.openrsc.server.constants.ItemId.ICE_ARROWS.id(),
							//Firebird Feather
							com.openrsc.server.constants.ItemId.RED_FIREBIRD_FEATHER.id(),
							//Ball of Witch's House
							com.openrsc.server.constants.ItemId.BALL.id(),
							//skull of restless ghost
							com.openrsc.server.constants.ItemId.QUEST_SKULL.id()
						};
						List<Integer> ungrabbables = new ArrayList<Integer>();
						for (int item : ungrabbableArr) {
							ungrabbables.add(item);
						}
						if (affectedItem.getID() == com.openrsc.server.constants.ItemId.PRESENT.id()) {
							return;
						} else if (ungrabbables.contains(affectedItem.getID())) { // list of ungrabbable items sharing this message
							player.playerServerMessage(MessageType.QUEST, "I can't use telekinetic grab on this object");
							return;
						}

						if (!player.getWorld().isTelegrabEnabled()) {
							player.message("Telegrab has been disabled");
							return;
						}
						if (affectedItem.getLocation().isInSeersPartyHall()) {
							player.message("You can't cast this spell within the vicinity of the party hall");
							return;
						}
						if (affectedItem.getID() == com.openrsc.server.constants.ItemId.A_BLUE_WIZARDS_HAT.id()) {
							player.message("The spell fizzles as the magical hat resists your spell.");
							return;
						}
						if (affectedItem.getID() == com.openrsc.server.constants.ItemId.GERTRUDES_CAT.id()) {
							player.message("I can't use telekinetic grab on the cat");
							return;
						}
						if (affectedItem.getID() == com.openrsc.server.constants.ItemId.ANA_IN_A_BARREL.id()) {
							player.message("I can't use telekinetic grab on Ana");
							return;
						}
						//coin respawn in Rashiliyia's Tomb can't be telegrabbed
						if (affectedItem.getID() == com.openrsc.server.constants.ItemId.COINS.id() && affectedItem.getLocation().equals(new Point(358, 3626))) {
							player.message("The coins turn to dust in your inventory...");
							return;
						}
						if (affectedItem.getLocation().inBounds(97, 1428, 106, 1440)) {
							player.message("Telekinetic grab cannot be used in here");
							return;
						}

						if (affectedItem.getLocation().inWilderness() && affectedItem.belongsTo(player) && affectedItem.getAttribute("playerKill", false) && (player.isIronMan(2) || player.isIronMan(1) || player.isIronMan(3))) {
							player.message("You're an Iron Man, so you can't loot items from players.");
							return;
						}
						if (!affectedItem.belongsTo(player) && (player.isIronMan(1) || player.isIronMan(2) || player.isIronMan(3))) {
							player.message("You're an Iron Man, so you can't take items that other players have dropped.");
							return;
						}

						if (!checkAndRemoveRunes(player, spell)) {
							return;
						}
						ActionSender.sendTeleBubble(player, location.getX(), location.getY(), true);
						for (Player p : player.getViewArea().getPlayersInView()) {
							ActionSender.sendTeleBubble(p, location.getX(), location.getY(), true);
						}

						player.getWorld().unregisterItem(affectedItem);
						finalizeSpell(player, spell, "Spell successful");
						player.getWorld().getServer().getGameLogger().addQuery(
							new GenericLog(player.getWorld(), player.getUsername() + " telegrabbed " + affectedItem.getDef().getName()
								+ " x" + affectedItem.getAmount() + " from " + affectedItem.getLocation().toString()
								+ " while standing at " + player.getLocation().toString()));
						Item item = new Item(affectedItem.getID(), affectedItem.getAmount());

						if (affectedItem.getOwnerUsernameHash() == 0 || affectedItem.getAttribute("npcdrop", false)) {
							item.setAttribute("npcdrop", true);
						}
						player.getInventory().add(item);
						break;
				}
			}
		});
	}

	private void handleMobCast(final Player player, final Mob affectedMob, final int spellID) {
		if (player.getDuel().isDuelActive() && affectedMob.isPlayer()) {
			Player aff = (Player) affectedMob;
			if (!player.getDuel().getDuelRecipient().getUsername().toLowerCase()
				.equals(aff.getUsername().toLowerCase()))
				return;
		}
		if (!PathValidation.checkPath(player.getWorld(), player.getLocation(), affectedMob.getLocation())) {
			player.playerServerMessage(MessageType.QUEST, "I can't get a clear shot from here");
			player.resetPath();
			return;
		}
		if (affectedMob.isPlayer()) {
			Player other = (Player) affectedMob;
			if (player.getLocation().inWilderness() && System.currentTimeMillis() - other.getLastRun() < 1000) {
				player.resetPath();
				return;
			}
		}
		if (player.getLocation().inWilderness() && System.currentTimeMillis() - player.getLastRun() < 3000) {
			player.resetPath();
			return;
		}
		player.setFollowing(affectedMob);
		player.setStatus(Action.CASTING_MOB);
		player.setWalkToAction(new WalkToMobAction(player, affectedMob, 4) {
			public void execute() {
				if (!PathValidation.checkPath(player.getWorld(), player.getLocation(), affectedMob.getLocation())) {
					player.playerServerMessage(MessageType.QUEST, "I can't get a clear shot from here");
					player.resetPath();
					return;
				}
				player.resetFollowing();
				player.resetPath();
				SpellDef spell = player.getWorld().getServer().getEntityHandler().getSpellDef(spellID);
				if (!canCast(player) || affectedMob.getSkills().getLevel(com.openrsc.server.constants.Skills.HITS) <= 0 || player.getStatus() != Action.CASTING_MOB) {
					player.resetPath();
					return;
				}
				if (!player.checkAttack(affectedMob, true) && affectedMob.isPlayer()) {
					player.resetPath();
					return;
				}
				if (!player.checkAttack(affectedMob, true) && affectedMob.isNpc()) {
					player.message("I can't attack that");
					player.resetPath();
					return;
				}
				if (affectedMob.isNpc()) {
					Npc n = (Npc) affectedMob;
					if (n.getID() == com.openrsc.server.constants.NpcId.DRAGON.id() || n.getID() == com.openrsc.server.constants.NpcId.KING_BLACK_DRAGON.id()) {
						player.playerServerMessage(MessageType.QUEST, "The dragon breathes fire at you");
						int percentage = 20;
						int fireDamage;
						if (player.getInventory().wielding(com.openrsc.server.constants.ItemId.ANTI_DRAGON_BREATH_SHIELD.id())) {
							if (n.getID() == com.openrsc.server.constants.NpcId.DRAGON.id()) {
								percentage = 10;
							} else if (n.getID() == com.openrsc.server.constants.NpcId.KING_BLACK_DRAGON.id()) {
								percentage = 4;
							} else {
								percentage = 0;
							}
							player.playerServerMessage(MessageType.QUEST, "Your shield prevents some of the damage from the flames");
						}
						fireDamage = (int) Math.floor(getCurrentLevel(player, com.openrsc.server.constants.Skills.HITS) * percentage / 100.0);
						player.damage(fireDamage);

						//reduce ranged level (case for KBD)
						if (n.getID() == com.openrsc.server.constants.NpcId.KING_BLACK_DRAGON.id()) {
							int newLevel = getCurrentLevel(player, com.openrsc.server.constants.Skills.RANGED) - Formulae.getLevelsToReduceAttackKBD(player);
							player.getSkills().setLevel(com.openrsc.server.constants.Skills.RANGED, newLevel);
						}
					}

				}
				player.resetAllExceptDueling();
				switch (spellID) {
					/*
					 * Confuse, reduces attack by 5% Weaken, reduces strength by 5%
					 * Curse reduces defense by 5%
					 *
					 * Vulnerability, reduces defense by 10% Enfeeble, reduces
					 * strength by 10% Stun, reduces attack by 10%
					 */
					case 1: // Confuse
					case 5: // Weaken
					case 9: // Curse
					case 41: // vulnerability
					case 44: // Enfeeble
					case 46: // Stun
						double lowersBy = 0.0;
						int affectsStat = -1;
						if (spellID == 1) {
							lowersBy = 0.05;
							affectsStat = 0;
						} else if (spellID == 5) {
							lowersBy = 0.05;
							affectsStat = 2;
						} else if (spellID == 9) {
							lowersBy = 0.05;
							affectsStat = 1;
						} else if (spellID == 41) {
							lowersBy = 0.10;
							affectsStat = 1;
						} else if (spellID == 44) {
							lowersBy = 0.10;
							affectsStat = 2;
						} else if (spellID == 46) {
							lowersBy = 0.10;
							affectsStat = 0;
						}

						/* How much to lower the stat */
						int lowerBy = (int) Math.ceil((affectedMob.getSkills().getLevel(affectsStat) * lowersBy));
						/* New current level */
						final int newStat = affectedMob.getSkills().getLevel(affectsStat) - lowerBy;
						/* Lowest stat you can weaken to with this spell */
						final int maxWeaken = affectedMob.getSkills().getMaxStat(affectsStat)
							- (int) Math.ceil((affectedMob.getSkills().getLevel(affectsStat) * lowersBy));
						if (newStat < maxWeaken) {
							player.playerServerMessage(MessageType.QUEST, "Your opponent already has weakened " + player.getWorld().getServer().getConstants().getSkills().getSkillName(affectsStat));
							return;
						}
						if (!checkAndRemoveRunes(player, spell)) {
							return;
						}
						final int stat = affectsStat;
						player.getWorld().getServer().getGameEventHandler().add(new CustomProjectileEvent(player.getWorld(), player, affectedMob, 1) {
							@Override
							public void doSpell() {
								affectedMob.getSkills().setLevel(stat, newStat);
								if (affectedMob.isPlayer()) {
									((Player) affectedMob).message("You have been weakened");
								}
							}
						});
						finalizeSpell(player, spell, DEFAULT);
						return;
					case 19: // Crumble undead
						if (affectedMob.isPlayer()) {
							player.message("You can not use this spell on a Player");
							return;
						}
						Npc n = (Npc) affectedMob;
						if (!n.getAttribute("isUndead", false)) {
							player.playerServerMessage(MessageType.QUEST, "This spell can only be used on skeletons, zombies and ghosts");
							return;
						}
						int damaga = DataConversions.random(3, Constants.CRUMBLE_UNDEAD_MAX);
						if (!checkAndRemoveRunes(player, spell)) {
							return;
						}
						if (DataConversions.random(0, 8) == 2)
							damaga = 0;

						player.getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(player.getWorld(), player, affectedMob, damaga, 1));
						finalizeSpell(player, spell, DEFAULT);
						return;

					case 25: /* Iban Blast */
						if (player.getQuestStage(Quests.UNDERGROUND_PASS) != -1) {
							player.message("you need to complete underground pass quest to cast this spell");
							return;
						}
						if (!player.getInventory().wielding(com.openrsc.server.constants.ItemId.STAFF_OF_IBAN.id())) {
							player.message("you need the staff of iban to cast this spell");
							return;
						}
						if (player.getCache().hasKey(spell.getName() + "_casts")
							&& player.getCache().getInt(spell.getName() + "_casts") < 1) {
							player.message("you need to recharge the staff of iban");
							player.message("at iban's temple");
							return;
						}
						if (!checkAndRemoveRunes(player, spell)) {
							return;
						}
						if (player.getCache().hasKey(spell.getName() + "_casts")) {
							int casts = player.getCache().getInt(spell.getName() + "_casts");
							player.getCache().set(spell.getName() + "_casts", casts - 1);
						}
						player.getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(player.getWorld(), player, affectedMob, Formulae.calcGodSpells(player, affectedMob, true), 1));
						finalizeSpell(player, spell, DEFAULT);
						break;
					case 33: // Guthix cast
					case 34: // Saradomin cast
					case 35: // Zamorak cast
						if (!player.getInventory().wielding(com.openrsc.server.constants.ItemId.STAFF_OF_GUTHIX.id()) && spellID == 33) {
							player.message("you must weild the staff of guthix to cast this spell");
							return;
						}
						if (!player.getInventory().wielding(com.openrsc.server.constants.ItemId.STAFF_OF_SARADOMIN.id()) && spellID == 34) {
							player.message("you must weild the staff of saradomin to cast this spell");
							return;
						}
						if (!player.getInventory().wielding(com.openrsc.server.constants.ItemId.STAFF_OF_ZAMORAK.id()) && spellID == 35) {
							player.message("you must weild the staff of zamorak to cast this spell");
							return;
						}
					/*if (player.getLocation().inWilderness() && !player.getLocation().inMageArena()
							&& (player.getLocation().wildernessLevel() < World.godSpellsStart
									|| player.getLocation().wildernessLevel() > World.godSpellsMax)) {
						player.message("God spells can only be used in wild levels: " + World.godSpellsStart + " - "
								+ World.godSpellsMax);
						return;
					}*/

						if (!player.getLocation().inMageArena()) {
							if ((!player.getCache().hasKey(spell.getName() + "_casts"))
								|| (player.getCache().hasKey(spell.getName() + "_casts")
								&& player.getCache().getInt(spell.getName() + "_casts") < 100)) {
								player.message("this spell can only be used in the mage arena");
								player.message("You must learn this spell first, you need "
									+ (player.getCache().hasKey(spell.getName() + "_casts")
									? (100 - player.getCache().getInt(spell.getName() + "_casts")) : "100")
									+ " more casts in the mage arena");
								return;
							}
						}
						if (!checkAndRemoveRunes(player, spell)) {
							return;
						}
						if (player.getLocation().inMageArena()) {
							if (player.getCache().hasKey(spell.getName() + "_casts")) {
								int casts = player.getCache().getInt(spell.getName() + "_casts");
								player.getCache().set(spell.getName() + "_casts", casts + 1);
								if (casts == 99) {
									player.message("Well done .. you can now use the " + spell.getName() + " outside the arena");
								}
							} else {
								player.getCache().set(spell.getName() + "_casts", 1);
							}
						}
						if (affectedMob.getRegion().getGameObject(affectedMob.getX(), affectedMob.getY()) == null) {
							godSpellObject(player, affectedMob, spellID);
						}
						player.getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(player.getWorld(), player, affectedMob, Formulae.calcGodSpells(player, affectedMob, false), 1));

						finalizeSpell(player, spell, DEFAULT);
						break;
					default:
						if (spell.getReqLevel() == 62 || spell.getReqLevel() == 65 || spell.getReqLevel() == 70
							|| spell.getReqLevel() == 75) {
						/*if (!player.getLocation().isMembersWild()) {
							player.message("Members content can only be used in wild levels: " + World.membersWildStart
									+ " - " + World.membersWildMax);
							return;
						}*/
						}
						if (!checkAndRemoveRunes(player, spell)) {
							return;
						}
						/** SALARIN THE TWISTED - STRIKE SPELLS **/
						if (affectedMob.getID() == NpcId.SALARIN_THE_TWISTED.id() && (spell.getName().equals("Wind strike")
							|| spell.getName().equals("Water strike") || spell.getName().equals("Earth strike")
							|| spell.getName().equals("Fire strike"))) {
							int firstDamage = 0;
							final int secondAdditionalDamage;
							if (spell.getName().equals("Fire strike")) {
								firstDamage = 12;
								secondAdditionalDamage = DataConversions.getRandom().nextInt(5); // 4 // max.
							} else if (spell.getName().equals("Earth strike")) {
								firstDamage = 11;
								secondAdditionalDamage = DataConversions.getRandom().nextInt(4); // 3 // max.
							} else if (spell.getName().equals("Water strike")) {
								firstDamage = 10;
								secondAdditionalDamage = DataConversions.getRandom().nextInt(3); // 2 // max.
							} else {
								firstDamage = 9;
								secondAdditionalDamage = DataConversions.getRandom().nextInt(2); // 1 // max														// max.
							}
							// Shout message from NPC when being maged
							affectedMob.getUpdateFlags().setChatMessage(new ChatMessage(affectedMob, "Aaarrgh my head", player));
							// Deal first damage
							player.getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(player.getWorld(), player, affectedMob, firstDamage, 1));
							// Deal Second Damage
							player.getWorld().getServer().getGameEventHandler().add(new MiniEvent(player.getWorld(), player, 600, "Salarin the Twisted Strike") {
								@Override
								public void action() {
									affectedMob.getSkills().subtractLevel(3, secondAdditionalDamage, false);
									affectedMob.getUpdateFlags().setDamage(new Damage(affectedMob, secondAdditionalDamage));
									if (affectedMob.isPlayer()) {
										if (player.getWorld().getServer().getConfig().WANT_PARTIES) {
											if(player.getParty() != null){
												player.getParty().sendParty();
											}
										}
									}
									if (affectedMob.getSkills().getLevel(com.openrsc.server.constants.Skills.HITS) <= 0) {
										affectedMob.killedBy(player);
									}

								}
							});
							// Send finalize spell without giving XP
							player.lastCast = System.currentTimeMillis();
							player.playSound("spellok");
							player.playerServerMessage(MessageType.QUEST, "Cast spell successfully");
							player.setCastTimer();
							return;
						}

						int max = -1;
						for (int i = 0; i < player.getWorld().getServer().getConstants().SPELLS.length; i++) {
							if (spell.getReqLevel() == player.getWorld().getServer().getConstants().SPELLS[i][0])
								max = player.getWorld().getServer().getConstants().SPELLS[i][1];
						}

						if (player.getMagicPoints() > 30
							|| (player.getInventory().wielding(com.openrsc.server.constants.ItemId.GAUNTLETS_OF_CHAOS.id()) && spell.getName().contains("bolt")))
							max += 1;

						int damage = Formulae.calcSpellHit(max, player.getMagicPoints());

						player.getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(player.getWorld(), player, affectedMob, damage, 1));
						player.setKillType(1);
						finalizeSpell(player, spell, DEFAULT);
						break;
				}
			}
		});
	}

	private void handleTeleport(Player player, SpellDef spell, int id) {
		if (player.getLocation().wildernessLevel() >= 20 || player.getLocation().isInFisherKingRealm()
			|| player.getLocation().isInsideGrandTreeGround()
			|| (player.getLocation().inModRoom() && !player.isAdmin())) {
			player.message("A mysterious force blocks your teleport spell!");
			player.message("You can't use teleport after level 20 wilderness");
			return;
		}
		// if (player.getLocation().inWilderness() && System.currentTimeMillis() - player.getCombatTimer() < 10000) {
		//	player.message("You need to stay out of combat for 10 seconds before using a teleport.");
		//	return;
		//}
		if (player.getInventory().countId(com.openrsc.server.constants.ItemId.ANA_IN_A_BARREL.id()) > 0) {
			message(player, "You can't teleport while holding Ana,",
				"It's just too difficult to concentrate.");
			return;
		}
		if (!player.getCache().hasKey("ardougne_scroll") && id == 26) {
			player.message("You don't know how to cast this spell yet");
			player.message("You need to do the plague city quest");
			return;
		}
		if (!player.getCache().hasKey("watchtower_scroll") && id == 31) {
			player.message("You cannot cast this spell");
			player.message("You need to finish the watchtower quest first");
			return;
		}
		if (player.getLocation().inModRoom()) {
			return;
		}
		if (!checkAndRemoveRunes(player, spell)) {
			return;
		}
		if (player.getLocation().inKaramja() || player.getLocation().inBrimhaven()) {
			while (player.getInventory().countId(com.openrsc.server.constants.ItemId.KARAMJA_RUM.id()) > 0) {
				player.getInventory().remove(new Item(com.openrsc.server.constants.ItemId.KARAMJA_RUM.id()));
			}
		}
		if (player.getInventory().hasItemId(com.openrsc.server.constants.ItemId.PLAGUE_SAMPLE.id())) {
			player.message("the plague sample is too delicate...");
			player.message("it disintegrates in the crossing");
			while (player.getInventory().countId(com.openrsc.server.constants.ItemId.PLAGUE_SAMPLE.id()) > 0) {
				player.getInventory().remove(new Item(com.openrsc.server.constants.ItemId.PLAGUE_SAMPLE.id()));
			}
		}
		switch (id) {
			case 12: // Varrock
				player.teleport(120, 504, true);
				break;
			case 15: // Lumbridge
				player.teleport(120, 648, true);
				break;
			case 18: // Falador
				player.teleport(312, 552, true);
				break;
			case 22: // Camelot
				player.teleport(465, 456, true);
				break;
			case 26: // Ardougne
				player.teleport(588, 621, true);
				break;
			case 31: // Watchtower
				player.teleport(493, 3525, true);
				break;
			default:
				break;
		}
		finalizeSpellNoMessage(player, spell);
	}

}
