package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.*;
import com.openrsc.server.content.SkillCapes;
import com.openrsc.server.database.impl.mysql.queries.logging.GenericLog;
import com.openrsc.server.event.MiniEvent;
import com.openrsc.server.event.rsc.impl.CustomProjectileEvent;
import com.openrsc.server.event.rsc.impl.ObjectRemover;
import com.openrsc.server.event.rsc.impl.ProjectileEvent;
import com.openrsc.server.event.rsc.impl.combat.CombatFormula;
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
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.util.rsc.CertUtil;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;

import static com.openrsc.server.net.rsc.OpcodeIn.CAST_ON_SELF;
import static com.openrsc.server.net.rsc.OpcodeIn.PLAYER_CAST_PVP;
import static com.openrsc.server.plugins.Functions.*;

public class SpellHandler implements PacketHandler {

	private static TreeMap<Integer, Item[]> staffs = new TreeMap<Integer, Item[]>();
	private static final String AMULET = "amulet";
	private static final String RING = "ring";
	private static final String NECKLACE = "necklace";
	private static final String DEFAULT = "";
	private static final int[] elementalRunes = new int[4];

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	static {
		staffs.put(ItemId.FIRE_RUNE.id(), new Item[]{new Item(ItemId.STAFF_OF_FIRE.id()), new Item(ItemId.BATTLESTAFF_OF_FIRE.id()), new Item(ItemId.ENCHANTED_BATTLESTAFF_OF_FIRE.id())}); // Fire-Rune
		staffs.put(ItemId.WATER_RUNE.id(), new Item[]{new Item(ItemId.STAFF_OF_WATER.id()), new Item(ItemId.BATTLESTAFF_OF_WATER.id()), new Item(ItemId.ENCHANTED_BATTLESTAFF_OF_WATER.id())}); // Water-Rune
		staffs.put(ItemId.AIR_RUNE.id(), new Item[]{new Item(ItemId.STAFF_OF_AIR.id()), new Item(ItemId.BATTLESTAFF_OF_AIR.id()), new Item(ItemId.ENCHANTED_BATTLESTAFF_OF_AIR.id())}); // Air-Rune
		staffs.put(ItemId.EARTH_RUNE.id(), new Item[]{new Item(ItemId.STAFF_OF_EARTH.id()), new Item(ItemId.BATTLESTAFF_OF_EARTH.id()), new Item(ItemId.ENCHANTED_BATTLESTAFF_OF_EARTH.id())}); // Earth-Rune
	}

	static {
		elementalRunes[0] = ItemId.AIR_RUNE.id();
		elementalRunes[1] = ItemId.WATER_RUNE.id();
		elementalRunes[2] = ItemId.EARTH_RUNE.id();
		elementalRunes[3] = ItemId.FIRE_RUNE.id();
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
		if (SkillCapes.shouldActivate(player, ItemId.MAGIC_CAPE)) {
			player.message("You manage to cast the spell without using any runes");
			return true;
		}
		Set<Entry<Integer, Integer>> runesToConsume = new HashSet<>();

		for (Entry<Integer, Integer> e : spell.getRunesRequired()) {
			boolean skipRune = false;
			if (player.getConfig().WANT_EQUIPMENT_TAB) {
				for (Item staff : getStaffs(e.getKey())) {
					if (player.getCarriedItems().getEquipment().searchEquipmentForItem(staff.getCatalogId()) != -1) {
						skipRune = true;
					}
				}
			} else {
				for (Item staff : getStaffs(e.getKey())) {
					synchronized (player.getCarriedItems().getInventory().getItems()) {
						if (player.getCarriedItems().getInventory().contains(staff)) {
							for (Item item : player.getCarriedItems().getInventory().getItems()) {
								if (item.equals(staff) && item.isWielded()) {
									skipRune = true;
									break;
								}
							}
						}
					}
				}
			}

			if (skipRune) {
				continue;
			}
			if (player.getCarriedItems().getInventory().countId(e.getKey()) < e.getValue()) {
				player.setSuspiciousPlayer(true, "player not all reagents for spell");
				player.message("You don't have all the reagents you need for this spell");
				return false;
			}
			runesToConsume.add(new AbstractMap.SimpleEntry<Integer, Integer>(e.getKey(), e.getValue()));
		}
		// remove now if player meets all rune requirements
		for (Entry<Integer, Integer> r : runesToConsume) {
			player.getCarriedItems().remove(new Item(r.getKey(), r.getValue()));
		}
		return true;
	}

	private static Item[] getStaffs(int runeID) {
		Item[] items = staffs.get(runeID);
		if (items == null) {
			return new Item[0];
		}
		return items;
	}

	// Check all prohibiting factors that would prevent spell from being cast
	// If all good, return correct spell
	private SpellDef spellSanityChecks(int spellIdx, Player player, OpcodeIn opcode) {
		if (spellIdx < 0 || spellIdx >= 49) {
			player.setSuspiciousPlayer(true, "idx < 0 or idx >= 49");
			return null;
		}

		SpellDef spell = player.getWorld().getServer().getEntityHandler().getSpellDef(spellIdx);
		if (spell.isMembers() && !player.getConfig().MEMBER_WORLD) {
			player.message("You need to login to a members world to use this spell");
			player.resetPath();
			return null;
		}

		// Services.lookup(DatabaseManager.class).addQuery(new
		// GenericLog(player.getUsername() + " tried to cast spell 49 at " +
		// player.getLocation()));

		// Check player's magic level prior to allowing cast.
		if (player.getSkills().getLevel(Skills.MAGIC) < spell.getReqLevel()) {
			player.setSuspiciousPlayer(true, "player magic ability not high enough");
			player.message("Your magic ability is not high enough for this spell.");
			player.resetPath();
			return null;
		}

		// Ensure player is allowed to teleport.
		if (opcode == CAST_ON_SELF && spell.getSpellType() == 0 && !canTeleport(player, spell, spellIdx)) {
			return null;
		}

		// You can't cast on things other than your opponent, while in a duel.
		if (opcode != PLAYER_CAST_PVP && player.getDuel().isDuelActive()) {
			player.message("You can't do that during a duel!");
			return null;
		}

		return spell;
	}

	private boolean spellSuccessCheck(Player player, SpellDef spell) {
		// Check for failed spell.
		if (!Formulae.castSpell(spell, player.getSkills().getLevel(Skills.MAGIC), player.getMagicPoints())) {
			player.message("The spell fails! You may try again in 20 seconds");
			player.playSound("spellfail");
			player.setSpellFail();
			player.resetPath();
			return false;
		}
		return true;
	}

	public void handlePacket(Packet packet, Player player) throws Exception {
		if ((player.isBusy() && !player.inCombat()) || player.isRanging()) {
			return;
		}
		if (!canCast(player)) {
			return;
		}

		OpcodeIn opcode = OpcodeIn.getFromList(packet.getID(),
			CAST_ON_SELF, OpcodeIn.PLAYER_CAST_PVP,
			OpcodeIn.CAST_ON_NPC, OpcodeIn.CAST_ON_INVENTORY_ITEM,
			OpcodeIn.CAST_ON_BOUNDARY, OpcodeIn.CAST_ON_SCENERY,
			OpcodeIn.CAST_ON_GROUND_ITEM, OpcodeIn.CAST_ON_LAND);

		if (opcode == null)
			return;

		player.resetAllExceptDueling();

		if (player.isUsingAuthenticClient()) {
			int idx;
			SpellDef spell;

			switch (opcode) {
				case CAST_ON_SELF:
					idx = packet.readShort();
					spell = spellSanityChecks(idx, player, opcode);
					if (spell == null) {
						return;
					}
					if (!spellSuccessCheck(player, spell)) {
						return;
					}

					if (spell.getSpellType() == 0) {
						handleTeleport(player, spell, idx);
						return;
					}
					handleGroundCast(player, spell, idx);
					break;
				case PLAYER_CAST_PVP:
					Player affectedPlayer = player.getWorld().getPlayer(packet.readShort());

					idx = packet.readShort();
					spell = spellSanityChecks(idx, player, opcode);
					if (spell == null) {
						return;
					}

					if (!spellSuccessCheck(player, spell)) {
						return;
					}

					if (spell.getSpellType() == 1 || spell.getSpellType() == 2) {

						if (affectedPlayer == null) {
							player.resetPath();
							return;
						}

						if (checkCastOnPlayer(player, affectedPlayer, idx)) return;

						handleMobCast(player, affectedPlayer, idx);
					}
					break;
				case CAST_ON_NPC:
					Npc affectedNpc = player.getWorld().getNpc(packet.readShort());

					idx = packet.readShort();
					spell = spellSanityChecks(idx, player, opcode);
					if (spell == null) {
						return;
					}
					if (!spellSuccessCheck(player, spell)) {
						return;
					}

					if (spell.getSpellType() == 2) {

						if (affectedNpc == null) {
							player.resetPath();
							return;
						}

						if (checkCastOnNpc(player, affectedNpc, spell)) return;

						handleMobCast(player, affectedNpc, idx);
					}
					break;
				case CAST_ON_INVENTORY_ITEM:
					// Have to throw in ugly exceptions for curse and enfeeble
					boolean runecraft = player.getConfig().WANT_RUNECRAFT;
					int curse = 9;
					int enfeeble = 44;

					int invIndex = packet.readShort();
					Item item = player.getCarriedItems().getInventory().get(invIndex);

					idx = packet.readShort();
					spell = spellSanityChecks(idx, player, opcode);
					if (spell == null) {
						return;
					}
					if (!spellSuccessCheck(player, spell)) {
						return;
					}

					if (spell.getSpellType() == 3
						|| (runecraft && (idx == curse || idx == enfeeble))) {

						if (item == null) {
							player.resetPath();
							return;
						}

						// Swap these lines to allow alchemy on notes.
						/*if ((idx == 10 || idx == 28) && item.getNoted()) {*/
						if (item.getNoted()) {
							player.message("Nothing interesting happens");
							return;
						}

						// Attempt to find a spell in a plugin, otherwise use this file.
						if (player.getWorld().getServer().getPluginHandler().handlePlugin(player, "SpellInv",
							new Object[]{player, invIndex, item.getCatalogId(), idx})) {
							return;
						}
						handleItemCast(player, spell, idx, item);
					}
					break;
				case CAST_ON_BOUNDARY:
					/* TODO:
					  -- 180c -- CLIENT_OPCODE_CAST_ON_BOUNDARY
					  elseif (clientOpcodeValue == 180) then
						-- standalone, doesn't require data from other opcodes
						opcodeField:add(clientCastOnBoundaryXCoord, buffer(1, 2))
						opcodeField:add(clientCastOnBoundaryYCoord, buffer(3, 2))
						opcodeField:add(clientCastOnBoundaryAlignment, buffer(5, 1))
						local spellField = opcodeField:add(clientCastOnGroundSpell, buffer(6, 2))
					 */
					player.message("@or1@This type of spell is not yet implemented.");
					break;
				case CAST_ON_SCENERY:
					int objectX = packet.readShort();
					int objectY = packet.readShort();

					idx = packet.readShort();
					spell = spellSanityChecks(idx, player, opcode);
					if (spell == null) {
						return;
					}
					if (!spellSuccessCheck(player, spell)) {
						return;
					}

					GameObject gameObject = player.getViewArea().getGameObject(Point.location(objectX, objectY));
					if (gameObject == null) {
						return;
					}

					if (player.getWorld().getServer().getPluginHandler().handlePlugin(player, "SpellLoc",
						new Object[]{player, gameObject, spell})) {
						return;
					}

					handleChargeOrb(player, gameObject, idx, spell);
					break;
				case CAST_ON_GROUND_ITEM:
					Point location = Point.location(packet.readShort(), packet.readShort());
					int itemId = packet.readShort();

					idx = packet.readShort();
					spell = spellSanityChecks(idx, player, opcode);
					if (spell == null) {
						return;
					}
					if (!spellSuccessCheck(player, spell)) {
						return;
					}

					GroundItem affectedItem = player.getViewArea().getGroundItem(itemId, location);
					if (affectedItem == null) {
						return;
					}
					handleItemCast(player, spell, idx, affectedItem);
					break;
				case CAST_ON_LAND:
					Point locationLand = Point.location(packet.readShort(), packet.readShort());
					idx = packet.readShort();
					spell = spellSanityChecks(idx, player, opcode);
					if (spell == null) {
						return;
					}
					if (!spellSuccessCheck(player, spell)) {
						return;
					}

					handleGroundCast(player, spell, idx);
					break;
				default:
					LOGGER.error("Wrong OPCODE passed to Spell Handler.");
					break;
			}

		} else {
			// Inauthentic client conveniently places Spell ID at the front for all Spell related packets.
			int idx = packet.readShort();

			SpellDef spell = spellSanityChecks(idx, player, opcode);
			if (spell == null) {
				return;
			}

			if (!spellSuccessCheck(player, spell)) {
				return;
			}

			switch (opcode) {
				case CAST_ON_SELF:
					if (spell.getSpellType() == 0) {
						handleTeleport(player, spell, idx);
						return;
					}
					handleGroundCast(player, spell, idx);
					break;
				case PLAYER_CAST_PVP:
					if (spell.getSpellType() == 1 || spell.getSpellType() == 2) {
						Player affectedPlayer = player.getWorld().getPlayer(packet.readShort());
						if (affectedPlayer == null) {
							player.resetPath();
							return;
						}

						if (checkCastOnPlayer(player, affectedPlayer, idx)) return;

						handleMobCast(player, affectedPlayer, idx);
					}
					break;
				case CAST_ON_NPC:
					if (spell.getSpellType() == 2) {
						Npc affectedNpc = player.getWorld().getNpc(packet.readShort());
						if (affectedNpc == null) {
							player.resetPath();
							return;
						}

						if (checkCastOnNpc(player, affectedNpc, spell)) return;

						handleMobCast(player, affectedNpc, idx);
					}
					break;
				case CAST_ON_INVENTORY_ITEM:
					// Have to throw in ugly exceptions for curse and enfeeble
					boolean runecraft = player.getConfig().WANT_RUNECRAFT;
					int curse = 9;
					int enfeeble = 44;

					if (spell.getSpellType() == 3
						|| (runecraft && (idx == curse || idx == enfeeble))) {

						int invIndex = packet.readShort();
						Item item = player.getCarriedItems().getInventory().get(invIndex);
						if (item == null) {
							player.resetPath();
							return;
						}

						// Swap these lines to allow alchemy on notes.
						/*if ((idx == 10 || idx == 28) && item.getNoted()) {*/
						if (item.getNoted()) {
							player.message("Nothing interesting happens");
							return;
						}

						// Attempt to find a spell in a plugin, otherwise use this file.
						if (player.getWorld().getServer().getPluginHandler().handlePlugin(player, "SpellInv",
							new Object[]{player, invIndex, item.getCatalogId(), idx})) {
							return;
						}
						handleItemCast(player, spell, idx, item);
					}
					break;
				case CAST_ON_BOUNDARY:
					player.message("@or1@This type of spell is not yet implemented.");
					break;
				case CAST_ON_SCENERY:
					int objectX = packet.readShort();
					int objectY = packet.readShort();
					GameObject gameObject = player.getViewArea().getGameObject(Point.location(objectX, objectY));
					if (gameObject == null) {
						return;
					}

					if (player.getWorld().getServer().getPluginHandler().handlePlugin(player, "SpellLoc",
						new Object[]{player, gameObject, spell})) {
						return;
					}

					handleChargeOrb(player, gameObject, idx, spell);
					break;
				case CAST_ON_GROUND_ITEM:
					Point location = Point.location(packet.readShort(), packet.readShort());
					int itemId = packet.readShort();
					GroundItem affectedItem = player.getViewArea().getGroundItem(itemId, location);
					if (affectedItem == null) {
						return;
					}
					handleItemCast(player, spell, idx, affectedItem);
					break;
				case CAST_ON_LAND:
					handleGroundCast(player, spell, idx);
					break;
				default:
					LOGGER.error("Wrong OPCODE passed to Spell Handler.");
					break;
			}
		}
	}

	private boolean checkCastOnPlayer(Player player, Player affectedPlayer, int idx) {

		// Duel with "No Magic" selected.
		if (player.getDuel().isDuelActive() && player.getDuel().getDuelSetting(1)) {
			player.message("Magic cannot be used during this duel!");
			return true;
		}

		// Note: Blocking magic casts near mage arena is inauthentic
		// see [Logg/Tylerbeg/08-05-2018 13.53.26 more pvp mechanics slash bugs with zephyr]
		// Only ranged & melee are authentically blocked in that safespot.

		// Stop the player if they are close enough to their opponent.
		if (player.withinRange(affectedPlayer, 4)) {
			player.resetPath();
		}

		return player.getWorld().getServer().getPluginHandler()
				.handlePlugin(player, "SpellPlayer", new Object[]{player, affectedPlayer, idx});
	}

	private boolean checkCastOnNpc(Player player, Npc affectedNpc, SpellDef spell) {

		// Demon Slayer
		if (affectedNpc.getID() == NpcId.DELRITH.id()) {
			player.message("Delrith can not be attacked without the Silverlight sword");
			return true;
		}

		// Temple of Ikov
		if (affectedNpc.getID() == NpcId.LUCIEN_EDGE.id() && (player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -1
				|| player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -2)) {
			player.message("You have already completed this quest");
			return true;
		}
		if (affectedNpc.getID() == NpcId.LUCIEN_EDGE.id() && !player.getCarriedItems().getEquipment().hasEquipped(ItemId.PENDANT_OF_ARMADYL.id())) {
			npcsay(player, affectedNpc, "I'm sure you don't want to attack me really",
					"I am your friend");
			mes("You decide you don't want to attack Lucien really");
			delay(3);
			mes("He is your friend");
			delay(3);
			return true;
		}

		// Stop movement if the player is within range.
		if (player.withinRange(affectedNpc, 4)) {
			player.resetPath();
		}

		// Family Crest
		if (affectedNpc.getID() == NpcId.CHRONOZON.id()) {
			if (spell.getName().contains("blast")) {
				String elementalType = spell.getName().split(" ")[0].toLowerCase();
				player.message("chronozon weakens");
				if (!player.getAttribute("chronoz_" + elementalType, false)) {
					player.setAttribute("chronoz_" + elementalType, true);
				}
			}
		}

		return player.getWorld().getServer().getPluginHandler()
				.handlePlugin(player, "SpellNpc", new Object[]{player, affectedNpc});
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
		player.incExp(Skills.MAGIC, spell.getExp(), true);
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

				int boneCount = player.getCarriedItems().getInventory().countId(ItemId.BONES.id(), Optional.of(false));
				if (boneCount == 0) {
					player.message("You aren't holding any bones!");
					return;
				}
				for (int i = 0; i < boneCount; i++) {
					player.getCarriedItems().remove(
						player.getCarriedItems().getInventory().get(
							player.getCarriedItems().getInventory().getLastIndexById(ItemId.BONES.id(), Optional.of(false))
						)
					);
					player.getCarriedItems().getInventory().add(new Item(ItemId.BANANA.id()));
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
				player.getCache().store("charge_remaining", 6 * 60000);
				// charge is on self
				godSpellObject(player, player, 47);
				finalizeSpell(player, spell, DEFAULT);
				return;
		}
	}

	private void handleItemCast(Player player, SpellDef spell, int id, Item affectedItem) {
		switch (id) {

			// Enchant lvl-1 Sapphire amulet
			case 3:
				enchantTierOneJewelry(player, affectedItem, spell);
				break;

			// Curse or Enfeeble on talisman
			case 9:
			case 44:
				buffTalisman(player, affectedItem, spell);
				break;

			// Low level alchemy
			case 10:
				lowLevelAlchemy(player, affectedItem, spell);
				break;

			// Enchant lvl-2 emerald amulet
			case 13:
				enchantTierTwoJewelry(player, affectedItem, spell);
				break;

			// Superheat item
			case 21:
				superheatItem(player, affectedItem, spell);
				break;

			// Enchant lvl-3 ruby amulet
			case 24:
				enchantTierThreeJewelry(player, affectedItem, spell);
				break;

			// High level alchemy
			case 28:
				highLevelAlchemy(player, affectedItem, spell);
				break;

			// Enchant lvl-4 diamond amulet
			case 30:
				enchantTierFourJewelry(player, affectedItem, spell);
				break;

			// Enchant lvl-5 dragonstone amulet
			case 42:
				enchantTierFiveJewelry(player, affectedItem, spell);
				break;

		}
		if (affectedItem.isWielded()) {
			player.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(player, affectedItem, UnequipRequest.RequestType.CHECK_IF_EQUIPMENT_TAB, false));
		}

	}

	private void enchantTierOneJewelry(Player player, Item affectedItem, SpellDef spell) {
		if (affectedItem.getCatalogId() == ItemId.SAPPHIRE_AMULET.id()
				|| (player.getConfig().WANT_EQUIPMENT_TAB
				&& (affectedItem.getCatalogId() == ItemId.SAPPHIRE_RING.id() || affectedItem.getCatalogId() == ItemId.OPAL_RING.id()))) {
			if (!checkAndRemoveRunes(player, spell)) {
				return;
			}
			int itemID = 0;
			String jewelryType = "";
			switch(ItemId.getById(affectedItem.getCatalogId())) {
				case SAPPHIRE_AMULET:
					itemID = ItemId.SAPPHIRE_AMULET_OF_MAGIC.id();
					jewelryType = AMULET;
					break;
				case SAPPHIRE_RING:
					itemID = ItemId.RING_OF_RECOIL.id();
					jewelryType = RING;
					break;
				case SAPPHIRE_NECKLACE:
					jewelryType = NECKLACE;
					break;
				case OPAL_RING:
					itemID = ItemId.DWARVEN_RING.id();
					jewelryType = RING;
					break;
			}
			player.getCarriedItems().remove(affectedItem);
			player.getCarriedItems().getInventory().add(new Item(itemID));
			finalizeSpell(player, spell, "You succesfully enchant the " + jewelryType);
		} else
			player.playerServerMessage(MessageType.QUEST, "This spell can only be used on unenchanted sapphire " + (player.getConfig().WANT_EQUIPMENT_TAB ? " rings/amulets or opal rings" : "amulets"));
	}

	private void buffTalisman(Player player, Item item, SpellDef spell) {
		int talismen[] = {
			ItemId.AIR_TALISMAN.id(),
			ItemId.MIND_TALISMAN.id(),
			ItemId.WATER_TALISMAN.id(),
			ItemId.EARTH_TALISMAN.id(),
			ItemId.FIRE_TALISMAN.id(),
			ItemId.BODY_TALISMAN.id(),
			ItemId.COSMIC_TALISMAN.id(),
			ItemId.CHAOS_TALISMAN.id(),
			ItemId.NATURE_TALISMAN.id(),
			ItemId.LAW_TALISMAN.id(),
			ItemId.DEATH_TALISMAN.id(),
			ItemId.BLOOD_TALISMAN.id()
		};
		int curse = 9;
		int enfeeble = 44;
		int talismanIndex = -1;

		for (int i = 0; i < talismen.length; ++i) {
			if (item.getCatalogId() == talismen[i]) {
				talismanIndex = i;
				break;
			}
		}

		if (talismanIndex == -1 || item.getItemStatus().getNoted()) {
			player.message("Nothing interesting happens");
			return;
		}

		if (!checkAndRemoveRunes(player, spell)) return;

		// Get last talisman in inventory.
		item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(
				item.getCatalogId(), Optional.of(false)));
		if (item == null) return;

		// Get the talisman to replace it with
		Item newTalisman = null;
		if (spell.getName().equalsIgnoreCase("curse")) {
			newTalisman = new Item(ItemId.CURSED_AIR_TALISMAN.id() + talismanIndex);
		}
		else if (spell.getName().equalsIgnoreCase("enfeeble")) {
			newTalisman = new Item(ItemId.ENFEEBLED_AIR_TALISMAN.id() + talismanIndex);
		}
		if (newTalisman == null) return;

		// Remove it
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(newTalisman);
		finalizeSpell(player, spell, "You successfully cast " + spell.getName()
			+ " on the " + item.getDef(player.getWorld()).getName());
	}

	private void enchantTierTwoJewelry(Player player, Item affectedItem, SpellDef spell) {
		if (affectedItem.getCatalogId() == ItemId.EMERALD_AMULET.id()
				|| (player.getConfig().WANT_EQUIPMENT_TAB && affectedItem.getCatalogId() == ItemId.EMERALD_RING.id())) {
			if (!checkAndRemoveRunes(player, spell)) {
				return;
			}
			int itemID = 0;
			String jewelryType = "";
			switch(ItemId.getById(affectedItem.getCatalogId())) {
				case EMERALD_AMULET:
					itemID = ItemId.EMERALD_AMULET_OF_PROTECTION.id();
					jewelryType = AMULET;
					break;
				case EMERALD_RING:
					itemID = ItemId.RING_OF_SPLENDOR.id();
					jewelryType = RING;
					break;
				case EMERALD_NECKLACE:
					jewelryType = NECKLACE;
					break;
			}
			player.getCarriedItems().remove(affectedItem);
			player.getCarriedItems().getInventory().add(new Item(itemID));
			finalizeSpell(player, spell, "You succesfully enchant the " + jewelryType);
		} else
			player.playerServerMessage(MessageType.QUEST, "This spell can only be used on unenchanted emerald " + (player.getConfig().WANT_EQUIPMENT_TAB ? "rings and amulets" : "amulets"));
	}

	private void enchantTierThreeJewelry(Player player, Item affectedItem, SpellDef spell) {
		if (affectedItem.getCatalogId() == ItemId.RUBY_AMULET.id()
				|| (player.getConfig().WANT_EQUIPMENT_TAB && affectedItem.getCatalogId() == ItemId.RUBY_RING.id())) {
			if (!checkAndRemoveRunes(player, spell)) {
				return;
			}
			int itemID = 0;
			String jewelryType = "";
			switch(ItemId.getById(affectedItem.getCatalogId())) {
				case RUBY_AMULET:
					itemID = ItemId.RUBY_AMULET_OF_STRENGTH.id();
					jewelryType = AMULET;
					break;
				case RUBY_RING:
					itemID = ItemId.RING_OF_FORGING.id();
					jewelryType = RING;
					break;
				case RUBY_NECKLACE:
					jewelryType = NECKLACE;
					break;
			}
			player.getCarriedItems().remove(affectedItem);
			player.getCarriedItems().getInventory().add(new Item(itemID));
			finalizeSpell(player, spell, "You succesfully enchant the " + jewelryType);
		} else
			player.playerServerMessage(MessageType.QUEST, "This spell can only be used on unenchanted ruby " + (player.getConfig().WANT_EQUIPMENT_TAB ? "rings and amulets" : "amulets"));
	}

	private void enchantTierFourJewelry(Player player, Item affectedItem, SpellDef spell) {
		if (affectedItem.getCatalogId() == ItemId.DIAMOND_AMULET.id()
				|| (player.getConfig().WANT_EQUIPMENT_TAB && affectedItem.getCatalogId() == ItemId.DIAMOND_RING.id())){
			if (!checkAndRemoveRunes(player, spell)) {
				return;
			}
			int itemID = 0;
			String jewelryType = "";
			switch(ItemId.getById(affectedItem.getCatalogId())) {
				case DIAMOND_AMULET:
					itemID = ItemId.DIAMOND_AMULET_OF_POWER.id();
					jewelryType = AMULET;
					break;
				case DIAMOND_RING:
					itemID = ItemId.RING_OF_LIFE.id();
					jewelryType = RING;
					break;
				case DIAMOND_NECKLACE:
					jewelryType = NECKLACE;
					break;
			}
			player.getCarriedItems().remove(affectedItem);
			player.getCarriedItems().getInventory().add(new Item(itemID));
			finalizeSpell(player, spell, "You succesfully enchant the " + jewelryType);
		} else
			player.playerServerMessage(MessageType.QUEST, "This spell can only be used on unenchanted diamond " + (player.getConfig().WANT_EQUIPMENT_TAB ? "rings and amulets" : "amulets"));
	}

	private void enchantTierFiveJewelry(Player player, Item affectedItem, SpellDef spell) {
		if (affectedItem.getCatalogId() == ItemId.UNENCHANTED_DRAGONSTONE_AMULET.id()
				|| (player.getConfig().WANT_EQUIPMENT_TAB && affectedItem.getCatalogId() == ItemId.DRAGONSTONE_RING.id())) {
			int itemID = 0;
			String jewelryType = "";
			switch(ItemId.getById(affectedItem.getCatalogId())) {
				case UNENCHANTED_DRAGONSTONE_AMULET:
					itemID = ItemId.DRAGONSTONE_AMULET.id();
					jewelryType = AMULET;
					break;
				case DRAGONSTONE_NECKLACE:
					jewelryType = NECKLACE;
					break;
			}
			if (!checkAndRemoveRunes(player, spell)) {
				return;
			}
			player.getCarriedItems().remove(affectedItem);
			player.getCarriedItems().getInventory().add(new Item(itemID));
			finalizeSpell(player, spell, "You succesfully enchant the " + jewelryType);
		} else
			player.playerServerMessage(MessageType.QUEST, "This spell can only be used on unenchanted dragonstone " + (player.getConfig().WANT_EQUIPMENT_TAB ? "rings and amulets" : "amulets"));
	}

	private void lowLevelAlchemy(Player player, Item affectedItem, SpellDef spell) {
		if (affectedItem.getCatalogId() == ItemId.COINS.id()) {
			player.playerServerMessage(MessageType.QUEST, "That's already made of gold!");
			return;
		}
		if (affectedItem.getNoted()) {
			player.message("You can't alch noted items");
			return;
		}
		if (!checkAndRemoveRunes(player, spell)) {
			return;
		}
		// ana in barrel kept but xp allowed
		if (affectedItem.getCatalogId() == ItemId.ANA_IN_A_BARREL.id()) {
			player.message("@gre@Ana: Don't you start casting spells on me!");
			finalizeSpellNoMessage(player, spell);
		} else {
			if (player.getCarriedItems().remove(new Item(affectedItem.getCatalogId(), affectedItem.getAmount())) > -1) {
				int value = (int) (affectedItem.getDef(player.getWorld()).getDefaultPrice() * 0.4D * affectedItem.getAmount());
				player.getCarriedItems().getInventory().add(new Item(ItemId.COINS.id(), value)); // 40%
			}
			finalizeSpell(player, spell, "Alchemy spell successful");
		}
	}

	private void highLevelAlchemy(Player player, Item affectedItem, SpellDef spell) {
		if (affectedItem.getCatalogId() == ItemId.COINS.id()) {
			player.playerServerMessage(MessageType.QUEST, "That's already made of gold!");
			return;
		}
		if (affectedItem.getNoted()) {
			player.message("You can't alch noted items");
			return;
		}
		if (!checkAndRemoveRunes(player, spell)) {
			return;
		}
		// ana in barrel kept but xp allowed
		if (affectedItem.getCatalogId() == ItemId.ANA_IN_A_BARREL.id()) {
			player.message("@gre@Ana: Don't you start casting spells on me!");
			finalizeSpellNoMessage(player, spell);
		} else {
			if (player.getCarriedItems().remove(new Item(affectedItem.getCatalogId(), affectedItem.getAmount())) > -1) {
				int value = (int) (affectedItem.getDef(player.getWorld()).getDefaultPrice() * 0.6D * affectedItem.getAmount());
				player.getCarriedItems().getInventory().add(new Item(ItemId.COINS.id(), value)); // 60%
			}
			finalizeSpell(player, spell, "Alchemy spell successful");
		}
	}

	private void superheatItem(Player player, Item affectedItem, SpellDef spell) {
		ItemSmeltingDef smeltingDef = affectedItem.getSmeltingDef(player.getWorld());
		if (smeltingDef == null || affectedItem.getCatalogId() == ItemId.COAL.id()) {
			player.playerServerMessage(MessageType.QUEST, "This spell can only be used on ore");
			return;
		}
		for (ReqOreDef reqOre : smeltingDef.getReqOres()) {
			if (player.getCarriedItems().getInventory().countId(reqOre.getId()) < reqOre.getAmount()) {
				if (affectedItem.getCatalogId() == ItemId.IRON_ORE.id()) {
					smeltingDef = player.getWorld().getServer().getEntityHandler().getItemSmeltingDef(9999);
					break;
				}
				if (affectedItem.getCatalogId() == ItemId.TIN_ORE.id() || affectedItem.getCatalogId() == ItemId.COPPER_ORE.id()) {
					player.playerServerMessage(MessageType.QUEST, "You also need some " + (affectedItem.getCatalogId() == ItemId.TIN_ORE.id() ? "copper" : "tin")
							+ " to make bronze");
					return;
				}
				player.playerServerMessage(MessageType.QUEST, "You need " + reqOre.getAmount() + " heaps of "
						+ player.getWorld().getServer().getEntityHandler().getItemDef(reqOre.getId()).getName().toLowerCase() + " to smelt "
						+ affectedItem.getDef(player.getWorld()).getName().toLowerCase().replaceAll("ore", ""));
				return;
			}
		}

		if (player.getSkills().getLevel(Skills.SMITHING) < smeltingDef.getReqLevel()) {
			player.playerServerMessage(MessageType.QUEST, "You need to be at least level-" + smeltingDef.getReqLevel() + " smithing to smelt "
					+ player.getWorld().getServer().getEntityHandler().getItemDef(smeltingDef.barId).getName().toLowerCase().replaceAll("bar", ""));
			return;
		}
		if (!checkAndRemoveRunes(player, spell)) {
			return;
		}
		Item bar = new Item(smeltingDef.getBarId());
		if (player.getCarriedItems().remove(affectedItem) > -1) {
			for (ReqOreDef reqOre : smeltingDef.getReqOres()) {
				int toUse = reqOre.getAmount();
				if (reqOre.getId() == ItemId.COAL.id()
					&& SkillCapes.shouldActivate(player, ItemId.SMITHING_CAPE)) {

					toUse = reqOre.getAmount()/2;
					player.message("You heat the ore using half the usual amount of coal");
				}
				for (int i = 0; i < toUse; i++) {
					player.getCarriedItems().remove(new Item(reqOre.getId()));
				}
			}
			player.playerServerMessage(MessageType.QUEST, "You make a bar of " + bar.getDef(player.getWorld()).getName().replace("bar", "").toLowerCase());
			player.getCarriedItems().getInventory().add(bar);
			player.incExp(Skills.SMITHING, smeltingDef.getExp(), true);
		}
		finalizeSpellNoMessage(player, spell);
	}

	private void handleItemCast(Player player, final SpellDef spell, final int id, final GroundItem affectedItem) {
		player.setWalkToAction(new WalkToPointAction(player, affectedItem.getLocation(), 4) {
			public void executeInternal() {
				getPlayer().resetPath();
				if (!canCast(getPlayer()) || getPlayer().getViewArea().getGroundItem(affectedItem.getID(), getLocation()) == null || affectedItem.isRemoved()) {
					return;
				}
				if (!PathValidation.checkPath(getPlayer().getWorld(), getPlayer().getLocation(), affectedItem.getLocation())) {
					getPlayer().playerServerMessage(MessageType.QUEST, "I can't see the object from here");
					getPlayer().resetPath();
					return;
				}
				getPlayer().resetAllExceptDueling();
				switch (id) {
					case 16: // Telekinetic grab
						// fluffs gets its own message
						// same case with ana
						int[] ungrabbableArr = {
							//scythe
							ItemId.SCYTHE.id(),
							//bunny ears
							ItemId.BUNNY_EARS.id(),
							//orbs
							ItemId.ORB_OF_LIGHT_WHITE.id(), ItemId.ORB_OF_LIGHT_BLUE.id(), ItemId.ORB_OF_LIGHT_PINK.id(), ItemId.ORB_OF_LIGHT_YELLOW.id(),
							//cat (underground pass)
							ItemId.KARDIA_CAT.id(),
							//god capes
							ItemId.ZAMORAK_CAPE.id(), ItemId.SARADOMIN_CAPE.id(), ItemId.GUTHIX_CAPE.id(),
							//holy grail
							ItemId.HOLY_GRAIL.id(),
							//large cogs
							ItemId.LARGE_COG_BLUE.id(), ItemId.LARGE_COG_BLACK.id(), ItemId.LARGE_COG_RED.id(), ItemId.LARGE_COG_PURPLE.id(),
							//staff of armadyl,
							ItemId.STAFF_OF_ARMADYL.id(),
							//ice arrows
							ItemId.ICE_ARROWS.id(),
							//Firebird Feather
							ItemId.RED_FIREBIRD_FEATHER.id(),
							//Ball of Witch's House
							ItemId.BALL.id(),
							//skull of restless ghost
							ItemId.QUEST_SKULL.id()
						};
						List<Integer> ungrabbables = new ArrayList<Integer>();
						for (int item : ungrabbableArr) {
							ungrabbables.add(item);
						}
						if (affectedItem.getID() == ItemId.PRESENT.id()) {
							return;
						} else if (ungrabbables.contains(affectedItem.getID())) { // list of ungrabbable items sharing this message
							getPlayer().playerServerMessage(MessageType.QUEST, "I can't use telekinetic grab on this object");
							return;
						}

						if (!getPlayer().getWorld().isTelegrabEnabled()) {
							getPlayer().message("Telegrab has been disabled");
							return;
						}
						if (affectedItem.getLocation().isInSeersPartyHall()) {
							getPlayer().message("You can't cast this spell within the vicinity of the party hall");
							return;
						}
						if (affectedItem.getLocation().isInWatchtowerPedestal()) {
							getPlayer().playerServerMessage(MessageType.QUEST, "I can't see the object from here");
							return;
						}
						if (affectedItem.getID() == ItemId.A_BLUE_WIZARDS_HAT.id()) {
							getPlayer().message("The spell fizzles as the magical hat resists your spell.");
							return;
						}
						if (affectedItem.getID() == ItemId.GERTRUDES_CAT.id()) {
							getPlayer().message("I can't use telekinetic grab on the cat");
							return;
						}
						if (affectedItem.getID() == ItemId.ANA_IN_A_BARREL.id()) {
							getPlayer().message("I can't use telekinetic grab on Ana");
							return;
						}
						//coin respawn in Rashiliyia's Tomb can't be telegrabbed
						if (affectedItem.getID() == ItemId.COINS.id() && affectedItem.getLocation().equals(new Point(358, 3626))) {
							getPlayer().message("The coins turn to dust in your inventory...");
							return;
						}
						if (affectedItem.getLocation().inBounds(97, 1428, 106, 1440)) {
							getPlayer().message("Telekinetic grab cannot be used in here");
							return;
						}

						if (affectedItem.getLocation().inWilderness() && !affectedItem.belongsTo(getPlayer())
							&& affectedItem.getAttribute("playerKill", false)
							&& (getPlayer().isIronMan(IronmanMode.Ironman.id()) || getPlayer().isIronMan(IronmanMode.Ultimate.id())
							|| getPlayer().isIronMan(IronmanMode.Hardcore.id()) || getPlayer().isIronMan(IronmanMode.Transfer.id()))) {
							getPlayer().message("You're an Iron Man, so you can't loot items from players.");
							return;
						}
						if (!affectedItem.belongsTo(getPlayer())
							&& (getPlayer().isIronMan(IronmanMode.Ironman.id()) || getPlayer().isIronMan(IronmanMode.Ultimate.id())
							|| getPlayer().isIronMan(IronmanMode.Hardcore.id()) || getPlayer().isIronMan(IronmanMode.Transfer.id()))) {
							getPlayer().message("You're an Iron Man, so you can't take items that other players have dropped.");
							return;
						}

						if (!affectedItem.belongsTo(getPlayer()) && affectedItem.getAttribute("isTransferIronmanItem", false)) {
							getPlayer().message("That belongs to a Transfer Ironman player.");
							return;
						}

						if (CertUtil.isCert(affectedItem.getID()) && getPlayer().getCertOptOut()
							&& affectedItem.getOwnerUsernameHash() != 0 && !affectedItem.belongsTo(getPlayer())) {
							getPlayer().message("You have opted out of taking certs that other players have dropped.");
							return;
						}

						if (!checkAndRemoveRunes(getPlayer(), spell)) {
							return;
						}
						ActionSender.sendTeleBubble(getPlayer(), getLocation().getX(), getLocation().getY(), true);
						for (Player player : getPlayer().getViewArea().getPlayersInView()) {
							ActionSender.sendTeleBubble(player, getLocation().getX(), getLocation().getY(), true);
						}

						getPlayer().getWorld().unregisterItem(affectedItem);
						finalizeSpell(getPlayer(), spell, "Spell successful");
						getPlayer().getWorld().getServer().getGameLogger().addQuery(
							new GenericLog(getPlayer().getWorld(), getPlayer().getUsername() + " telegrabbed " + affectedItem.getDef().getName()
								+ " x" + affectedItem.getAmount() + " from " + affectedItem.getLocation().toString()
								+ " while standing at " + getPlayer().getLocation().toString()));
						Item item = new Item(affectedItem.getID(), affectedItem.getAmount(), affectedItem.getNoted());

						if (affectedItem.getOwnerUsernameHash() == 0 || affectedItem.getAttribute("npcdrop", false)) {
							item.setAttribute("npcdrop", true);
						}
						getPlayer().getCarriedItems().getInventory().add(item);
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
		int timeToAllowAttacks = player.getConfig().GAME_TICK * 5;
		if (affectedMob.isPlayer()) {
			Player other = (Player) affectedMob;
			if (player.getLocation().inWilderness() && System.currentTimeMillis() - other.getCombatTimer() < timeToAllowAttacks) {
				player.resetPath();
				// Effectively remove the attack timer from the player casted on
				// Authentic: see ticket #2579
				player.setCombatTimer(-timeToAllowAttacks);
			}
			if (player.getLocation().inWilderness() && System.currentTimeMillis() - player.getCombatTimer() < timeToAllowAttacks) {
				player.resetPath();
				// TODO: ...? should probably display a message here instead of dying silently...?
				System.out.println("Killed pvp cast silently because they shot too fast");
				return;
			}
		}

		// Do not cast if the mob is too far away and we are already in a fight.
		if (!player.withinRange(affectedMob, 4) && player.inCombat()) return;

		player.setFollowing(affectedMob);
		player.setWalkToAction(new WalkToMobAction(player, affectedMob, 4, false) {
			public void executeInternal() {
				if (!PathValidation.checkPath(getPlayer().getWorld(), getPlayer().getLocation(), affectedMob.getLocation())) {
					getPlayer().playerServerMessage(MessageType.QUEST, "I can't get a clear shot from here");
					getPlayer().resetPath();
					return;
				}
				getPlayer().resetFollowing();
				getPlayer().resetPath();
				SpellDef spell = getPlayer().getWorld().getServer().getEntityHandler().getSpellDef(spellID);
				if (!canCast(getPlayer()) || affectedMob.getSkills().getLevel(Skills.HITS) <= 0) {
					getPlayer().resetPath();
					return;
				}
				if (!getPlayer().checkAttack(affectedMob, true) && affectedMob.isPlayer()) {
					getPlayer().resetPath();
					return;
				}
				if (!getPlayer().checkAttack(affectedMob, true) && affectedMob.isNpc()) {
					// Exception for certain non-attackable mobs that attack you
					// We want to make sure that player is in combat with the mob.
					boolean inCombat = getPlayer().inCombat();
					boolean isRightMob = inArray(affectedMob.getID(),
						new int[]{
							NpcId.SHAPESHIFTER_SPIDER.id(),
							NpcId.SHAPESHIFTER_BEAR.id(),
							NpcId.SHAPESHIFTER_WOLF.id(),
							NpcId.THRANTAX.id(),
							NpcId.GUARDIAN_OF_ARMADYL_FEMALE.id(),
							NpcId.GUARDIAN_OF_ARMADYL_MALE.id(),
							NpcId.OGRE_TRADER_FOOD.id(),
							NpcId.OGRE_TRADER_ROCKCAKE.id(),
							NpcId.OGRE_TRADER_FOOD.id(),
							NpcId.CITY_GUARD.id(),
							NpcId.TOBAN.id()});
					boolean shouldCastSpell = inCombat && isRightMob;
					if (!shouldCastSpell) {
						getPlayer().message("I can't attack that");
						getPlayer().resetPath();
						return;
					}
				}
				boolean setChasing = true;
				if (affectedMob.isNpc()) {
					Npc n = (Npc) affectedMob;
					if (n.getID() == NpcId.DRAGON.id() || n.getID() == NpcId.KING_BLACK_DRAGON.id()) {
						getPlayer().playerServerMessage(MessageType.QUEST, "The dragon breathes fire at you");
						int percentage = 20;
						int fireDamage;
						if (getPlayer().getCarriedItems().getEquipment().hasEquipped(ItemId.ANTI_DRAGON_BREATH_SHIELD.id())) {
							if (n.getID() == NpcId.DRAGON.id()) {
								percentage = 10;
							} else if (n.getID() == NpcId.KING_BLACK_DRAGON.id()) {
								percentage = 4;
							} else {
								percentage = 0;
							}
							getPlayer().playerServerMessage(MessageType.QUEST, "Your shield prevents some of the damage from the flames");
						}
						fireDamage = (int) Math.floor(getCurrentLevel(getPlayer(), Skills.HITS) * percentage / 100.0);
						getPlayer().damage(fireDamage);

						//reduce ranged level (case for KBD)
						if (n.getID() == NpcId.KING_BLACK_DRAGON.id()) {
							int newLevel = getCurrentLevel(getPlayer(), Skills.RANGED) - Formulae.getLevelsToReduceAttackKBD(getPlayer());
							getPlayer().getSkills().setLevel(Skills.RANGED, newLevel);
						}
					} else if (inArray(n.getID(), NpcId.KOLODION_HUMAN.id(), NpcId.KOLODION_OGRE.id(), NpcId.KOLODION_SPIDER.id(),
						NpcId.KOLODION_SOULESS.id(), NpcId.KOLODION_DEMON.id(), NpcId.BATTLE_MAGE_GUTHIX.id(),
						NpcId.BATTLE_MAGE_ZAMORAK.id(), NpcId.BATTLE_MAGE_SARADOMIN.id()) && getPlayer().getLocation().inMageArena()) {
						setChasing = false;
						getPlayer().setAttribute("maged_kolodion", true);
					}

				}
				getPlayer().resetAllExceptDueling();
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
							getPlayer().playerServerMessage(MessageType.QUEST, "Your opponent already has weakened " + getPlayer().getWorld().getServer().getConstants().getSkills().getSkillName(affectsStat));
							return;
						}
						if (!checkAndRemoveRunes(getPlayer(), spell)) {
							return;
						}
						final int stat = affectsStat;
						getPlayer().getWorld().getServer().getGameEventHandler().add(new CustomProjectileEvent(getPlayer().getWorld(), getPlayer(), affectedMob, 1, setChasing) {
							@Override
							public void doSpell() {
								affectedMob.getSkills().setLevel(stat, newStat);
								if (affectedMob.isPlayer()) {
									((Player) affectedMob).message("You have been weakened");
								}
							}
						});
						finalizeSpell(getPlayer(), spell, DEFAULT);
						return;
					case 19: // Crumble undead
						if (affectedMob.isPlayer()) {
							getPlayer().message("You can not use this spell on a Player");
							return;
						}
						Npc n = (Npc) affectedMob;
						if (!n.getAttribute("isUndead", false)) {
							getPlayer().playerServerMessage(MessageType.QUEST, "This spell can only be used on skeletons, zombies and ghosts");
							return;
						}
						int damaga = DataConversions.random(3, Constants.CRUMBLE_UNDEAD_MAX);
						if (!checkAndRemoveRunes(getPlayer(), spell)) {
							return;
						}
						if (DataConversions.random(0, 8) == 2)
							damaga = 0;

						getPlayer().getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(getPlayer().getWorld(), getPlayer(), affectedMob, damaga, 1, setChasing));
						finalizeSpell(getPlayer(), spell, DEFAULT);
						return;

					case 25: /* Iban Blast */
						if (getPlayer().getQuestStage(Quests.UNDERGROUND_PASS) != -1) {
							getPlayer().message("you need to complete underground pass quest to cast this spell");
							return;
						}
						if (!getPlayer().getCarriedItems().getEquipment().hasEquipped(ItemId.STAFF_OF_IBAN.id())) {
							getPlayer().message("you need the staff of iban to cast this spell");
							return;
						}
						if (getPlayer().getCache().hasKey(spell.getName() + "_casts")
							&& getPlayer().getCache().getInt(spell.getName() + "_casts") < 1) {
							getPlayer().message("you need to recharge the staff of iban");
							getPlayer().message("at iban's temple");
							return;
						}
						if (!checkAndRemoveRunes(getPlayer(), spell)) {
							return;
						}
						if (getPlayer().getCache().hasKey(spell.getName() + "_casts")) {
							int casts = getPlayer().getCache().getInt(spell.getName() + "_casts");
							getPlayer().getCache().set(spell.getName() + "_casts", casts - 1);
						}
						getPlayer().getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(getPlayer().getWorld(), getPlayer(), affectedMob, CombatFormula.calculateIbanSpellDamage(), 1, setChasing));
						finalizeSpell(getPlayer(), spell, DEFAULT);
						break;
					case 33: // Guthix cast
					case 34: // Saradomin cast
					case 35: // Zamorak cast
						if (!getPlayer().getCarriedItems().getEquipment().hasEquipped(ItemId.STAFF_OF_GUTHIX.id()) && spellID == 33) {
							getPlayer().message("you must weild the staff of guthix to cast this spell");
							return;
						}
						if (!getPlayer().getCarriedItems().getEquipment().hasEquipped(ItemId.STAFF_OF_SARADOMIN.id()) && spellID == 34) {
							getPlayer().message("you must weild the staff of saradomin to cast this spell");
							return;
						}
						if (!getPlayer().getCarriedItems().getEquipment().hasEquipped(ItemId.STAFF_OF_ZAMORAK.id()) && spellID == 35) {
							getPlayer().message("you must weild the staff of zamorak to cast this spell");
							return;
						}
					/*if (player.getLocation().inWilderness() && !player.getLocation().inMageArena()
							&& (player.getLocation().wildernessLevel() < World.godSpellsStart
									|| player.getLocation().wildernessLevel() > World.godSpellsMax)) {
						player.message("God spells can only be used in wild levels: " + World.godSpellsStart + " - "
								+ World.godSpellsMax);
						return;
					}*/

						if (!getPlayer().getLocation().inMageArena()) {
							if ((!getPlayer().getCache().hasKey(spell.getName() + "_casts"))
								|| (getPlayer().getCache().hasKey(spell.getName() + "_casts")
								&& getPlayer().getCache().getInt(spell.getName() + "_casts") < 100)) {
								getPlayer().message("this spell can only be used in the mage arena");
								getPlayer().message("You must learn this spell first, you need "
									+ (getPlayer().getCache().hasKey(spell.getName() + "_casts")
									? (100 - getPlayer().getCache().getInt(spell.getName() + "_casts")) : "100")
									+ " more casts in the mage arena");
								return;
							}
						}
						if (!checkAndRemoveRunes(getPlayer(), spell)) {
							return;
						}
						if (getPlayer().getLocation().inMageArena()) {
							if (getPlayer().getCache().hasKey(spell.getName() + "_casts")) {
								int casts = getPlayer().getCache().getInt(spell.getName() + "_casts");
								getPlayer().getCache().set(spell.getName() + "_casts", casts + 1);
								if (casts == 99) {
									getPlayer().message("Well done .. you can now use the " + spell.getName() + " outside the arena");
								}
							} else {
								getPlayer().getCache().set(spell.getName() + "_casts", 1);
							}
						}
						if (affectedMob.getRegion().getGameObject(affectedMob.getX(), affectedMob.getY(), getPlayer()) == null) {
							godSpellObject(getPlayer(), affectedMob, spellID);
						}
						getPlayer().getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(getPlayer().getWorld(), getPlayer(), affectedMob, CombatFormula.calculateGodSpellDamage(getPlayer()), 1, setChasing));

						finalizeSpell(getPlayer(), spell, DEFAULT);
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
						if (!checkAndRemoveRunes(getPlayer(), spell)) {
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
							affectedMob.getUpdateFlags().setChatMessage(new ChatMessage(affectedMob, "Aaarrgh my head", getPlayer()));
							// Deal first damage
							getPlayer().getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(getPlayer().getWorld(), getPlayer(), affectedMob, firstDamage, 1));
							// Deal Second Damage
							getPlayer().getWorld().getServer().getGameEventHandler().add(new MiniEvent(getPlayer().getWorld(), getPlayer(), getPlayer().getConfig().GAME_TICK, "Salarin the Twisted Strike") {
								@Override
								public void action() {
									affectedMob.getSkills().subtractLevel(Skills.HITS, secondAdditionalDamage, false);
									affectedMob.getUpdateFlags().setDamage(new Damage(affectedMob, secondAdditionalDamage));
									if (affectedMob.isPlayer()) {
										if (getPlayer().getConfig().WANT_PARTIES) {
											if(getPlayer().getParty() != null){
												getPlayer().getParty().sendParty();
											}
										}
									}
									if (affectedMob.getSkills().getLevel(Skills.HITS) <= 0) {
										affectedMob.killedBy(getPlayer());
									}

								}
							});
							// Send finalize spell without giving XP
							getPlayer().lastCast = System.currentTimeMillis();
							getPlayer().playSound("spellok");
							getPlayer().playerServerMessage(MessageType.QUEST, "Cast spell successfully");
							getPlayer().setCastTimer();
							return;
						}

						int max = -1;
						for (int i = 0; i < getPlayer().getWorld().getServer().getConstants().SPELLS.length; i++) {
							if (spell.getReqLevel() == getPlayer().getWorld().getServer().getConstants().SPELLS[i][0])
								max = getPlayer().getWorld().getServer().getConstants().SPELLS[i][1];
						}

						if (getPlayer().getMagicPoints() > 30
							|| (getPlayer().getCarriedItems().getEquipment().hasEquipped(ItemId.GAUNTLETS_OF_CHAOS.id()) && spell.getName().contains("bolt")))
							max += 1;

						int damage = CombatFormula.calculateMagicDamage(max + 1) - 1;

						getPlayer().getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(getPlayer().getWorld(), getPlayer(), affectedMob, damage, 1, setChasing));
						getPlayer().setKillType(1);
						finalizeSpell(getPlayer(), spell, DEFAULT);
						break;
				}
			}
		});
	}

	private boolean canTeleport(Player player, SpellDef spell, int id) {
		boolean canTeleport = true;
		if (player.getLocation().wildernessLevel() >= 20 || player.getLocation().isInFisherKingRealm()
			|| player.getLocation().isInsideGrandTreeGround()
			|| (player.getLocation().inModRoom() && !player.isAdmin())) {
			player.message("A mysterious force blocks your teleport spell!");
			player.message("You can't use teleport after level 20 wilderness");
			canTeleport = false;
		}
		// if (player.getLocation().inWilderness() && System.currentTimeMillis() - player.getCombatTimer() < 10000) {
		//	player.message("You need to stay out of combat for 10 seconds before using a teleport.");
		//	return;
		//}
		else if (player.getCarriedItems().getInventory().countId(ItemId.ANA_IN_A_BARREL.id()) > 0) {
			mes("You can't teleport while holding Ana,");
			delay(3);
			mes("It's just too difficult to concentrate.");
			delay(3);
			canTeleport = false;
		}
		else if (!player.getCache().hasKey("ardougne_scroll") && id == 26) {
			player.message("You don't know how to cast this spell yet");
			player.message("You need to do the plague city quest");
			canTeleport = false;
		}
		else if (!player.getCache().hasKey("watchtower_scroll") && id == 31) {
			player.message("You cannot cast this spell");
			player.message("You need to finish the watchtower quest first");
			canTeleport = false;
		}
		if (player.getLocation().inModRoom()) {
			canTeleport = false;
		}
		return canTeleport;
	}

	private void handleTeleport(Player player, SpellDef spell, int id) {
		if (!checkAndRemoveRunes(player, spell)) {
			return;
		}
		if (player.getLocation().inKaramja() || player.getLocation().inBrimhaven()) {
			while (player.getCarriedItems().getInventory().countId(ItemId.KARAMJA_RUM.id()) > 0) {
				player.getCarriedItems().remove(new Item(ItemId.KARAMJA_RUM.id()));
			}
		}
		if (player.getCarriedItems().hasCatalogID(ItemId.KARAMJA_RUM.id()) && (player.getLocation().inKaramja())) {
			player.getCarriedItems().remove(new Item(ItemId.KARAMJA_RUM.id()));
		}
		if (player.getCarriedItems().hasCatalogID(ItemId.PLAGUE_SAMPLE.id())) {
			player.message("the plague sample is too delicate...");
			player.message("it disintegrates in the crossing");
			while (player.getCarriedItems().getInventory().countId(ItemId.PLAGUE_SAMPLE.id()) > 0) {
				player.getCarriedItems().remove(new Item(ItemId.PLAGUE_SAMPLE.id()));
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
				player.teleport(456, 456, true);
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

	private void handleChargeOrb(Player player, GameObject gameObject, int idx, SpellDef spell) {
		int chargedOrb = ItemId.NOTHING.id();
		switch (idx) {
			case 40:
				if (gameObject.getID() == 303) {
					chargedOrb = ItemId.AIR_ORB.id();
				} else {
					player.playerServerMessage(MessageType.QUEST, "This spell can only be used on air obelisks");
				}
				break;
			case 29:
				if (gameObject.getID() == 300) {
					chargedOrb = ItemId.WATER_ORB.id();
				} else {
					player.playerServerMessage(MessageType.QUEST, "This spell can only be used on water obelisks");
				}
				break;
			case 36:
				if (gameObject.getID() == 304) {
					chargedOrb = ItemId.EARTH_ORB.id();
				} else {
					player.playerServerMessage(MessageType.QUEST, "This spell can only be used on earth obelisks");
				}
				break;
			case 38:
				if (gameObject.getID() == 301) {
					chargedOrb = ItemId.FIRE_ORB.id();
				} else {
					player.playerServerMessage(MessageType.QUEST, "This spell can only be used on fire obelisks");
				}
				break;
		}
		if (chargedOrb == ItemId.NOTHING.id()) {
			return;
		}

		if (!checkAndRemoveRunes(player, spell)) {
			return;
		}
		player.getCarriedItems().getInventory().add(new Item(chargedOrb));
		player.lastCast = System.currentTimeMillis();
		player.playSound("spellok");
		player.playerServerMessage(MessageType.QUEST, "You succesfully charge the orb");
		player.incExp(Skills.MAGIC, spell.getExp(), true);
		player.setCastTimer();
	}

}
