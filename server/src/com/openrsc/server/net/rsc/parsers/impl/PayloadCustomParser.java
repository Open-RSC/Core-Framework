package com.openrsc.server.net.rsc.parsers.impl;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.constants.custom.*;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.parsers.PayloadParser;
import com.openrsc.server.net.rsc.struct.*;
import com.openrsc.server.net.rsc.struct.incoming.*;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Custom RSC Protocol Parser of Incoming Packets to respective Protocol Independent Structs
 * **/
public class PayloadCustomParser implements PayloadParser<OpcodeIn> {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public OpcodeIn toOpcodeEnum(Packet packet, Player player) {
		OpcodeIn opcode = null;
		// custom protocol with Security Settings
		// some packet opcodes conflict atm, so packet needs to be further examined
		boolean conflictFound = false;
		switch (packet.getID()) {
			case 67:
				opcode = OpcodeIn.HEARTBEAT;
				break;
			case 16:
				opcode = OpcodeIn.WALK_TO_ENTITY;
				break;
			case 187:
				opcode = OpcodeIn.WALK_TO_POINT;
				break;
			case 31:
				opcode = OpcodeIn.CONFIRM_LOGOUT;
				break;
			case 102:
				opcode = OpcodeIn.LOGOUT;
				break;
			case 59:
				opcode = OpcodeIn.BLINK;
				break;
			case 29:
				opcode = OpcodeIn.COMBAT_STYLE_CHANGED;
				break;
			case 116:
				opcode = OpcodeIn.QUESTION_DIALOG_ANSWER;
				break;
			case 235:
				opcode = OpcodeIn.PLAYER_APPEARANCE_CHANGE;
				break;
			case 132:
				opcode = OpcodeIn.SOCIAL_ADD_IGNORE;
				break;
			case 195:
				opcode = OpcodeIn.SOCIAL_ADD_FRIEND;
				break;
			case 218:
				opcode = OpcodeIn.SOCIAL_SEND_PRIVATE_MESSAGE;
				break;
			case 167:
				opcode = OpcodeIn.SOCIAL_REMOVE_FRIEND;
				break;
			case 241:
				opcode = OpcodeIn.SOCIAL_REMOVE_IGNORE;
				break;
			case 194:
				opcode = OpcodeIn.SOCIAL_ADD_DELAYED_IGNORE;
				break;
			case 176:
				opcode = OpcodeIn.DUEL_FIRST_ACCEPTED;
				break;
			case 33:
				opcode = OpcodeIn.DUEL_OFFER_ITEM;
				break;
			case 77:
				opcode = OpcodeIn.DUEL_SECOND_ACCEPTED;
				break;
			case 14:
				opcode = OpcodeIn.INTERACT_WITH_BOUNDARY;
				break;
			case 127:
				opcode = OpcodeIn.INTERACT_WITH_BOUNDARY2;
				break;
			case 180:
				opcode = OpcodeIn.CAST_ON_BOUNDARY;
				break;
			case 161:
				opcode = OpcodeIn.USE_WITH_BOUNDARY;
				break;
			case 153:
				opcode = OpcodeIn.NPC_TALK_TO;
				break;
			case 202:
				opcode = OpcodeIn.NPC_COMMAND;
				break;
			case 203:
				opcode = OpcodeIn.NPC_COMMAND2;
				break;
			case 190:
				opcode = OpcodeIn.NPC_ATTACK;
				break;
			case 50:
				opcode = OpcodeIn.CAST_ON_NPC;
				break;
			case 135:
				opcode = OpcodeIn.NPC_USE_ITEM;
				break;
			case 229:
				opcode = OpcodeIn.PLAYER_CAST_PVP;
				break;
			case 113:
				opcode = OpcodeIn.PLAYER_USE_ITEM;
				break;
			case 171:
				opcode = OpcodeIn.PLAYER_ATTACK;
				break;
			case 103:
				opcode = OpcodeIn.PLAYER_DUEL;
				break;
			case 142:
				opcode = OpcodeIn.PLAYER_INIT_TRADE_REQUEST;
				break;
			case 165:
				opcode = OpcodeIn.PLAYER_FOLLOW;
				break;
			case 249:
				opcode = OpcodeIn.CAST_ON_GROUND_ITEM;
				break;
			case 53:
				opcode = OpcodeIn.GROUND_ITEM_USE_ITEM;
				break;
			case 91:
				opcode = OpcodeIn.ITEM_USE_ITEM;
				break;
			case 170:
				opcode = OpcodeIn.ITEM_UNEQUIP_FROM_INVENTORY;
				break;
			case 169:
				opcode = OpcodeIn.ITEM_EQUIP_FROM_INVENTORY;
				break;
			case 168:
				opcode = OpcodeIn.ITEM_UNEQUIP_FROM_EQUIPMENT;
				break;
			case 172:
				opcode = OpcodeIn.ITEM_EQUIP_FROM_BANK;
				break;
			case 173:
				opcode = OpcodeIn.ITEM_REMOVE_TO_BANK;
				break;
			case 90:
				opcode = OpcodeIn.ITEM_COMMAND;
				break;
			case 246:
				opcode = OpcodeIn.ITEM_DROP;
				break;
			case 137:
				opcode = OpcodeIn.CAST_ON_SELF;
				break;
			case 158:
				opcode = OpcodeIn.CAST_ON_LAND;
				break;
			case 136:
				opcode = OpcodeIn.OBJECT_COMMAND;
				break;
			case 79:
				opcode = OpcodeIn.OBJECT_COMMAND2;
				break;
			case 99:
				opcode = OpcodeIn.CAST_ON_SCENERY;
				break;
			case 115:
				opcode = OpcodeIn.USE_ITEM_ON_SCENERY;
				break;
			case 166:
				opcode = OpcodeIn.SHOP_CLOSE;
				break;
			case 236:
				opcode = OpcodeIn.SHOP_BUY;
				break;
			case 221:
				opcode = OpcodeIn.SHOP_SELL;
				break;
			case 55:
				opcode = OpcodeIn.PLAYER_ACCEPTED_INIT_TRADE_REQUEST;
				break;
			case 230:
				opcode = OpcodeIn.PLAYER_DECLINED_TRADE;
				break;
			case 46:
				opcode = OpcodeIn.PLAYER_ADDED_ITEMS_TO_TRADE_OFFER;
				break;
			case 104:
				opcode = OpcodeIn.PLAYER_ACCEPTED_TRADE;
				break;
			case 60:
				opcode = OpcodeIn.PRAYER_ACTIVATED;
				break;
			case 254:
				opcode = OpcodeIn.PRAYER_DEACTIVATED;
				break;
			case 111:
				opcode = OpcodeIn.GAME_SETTINGS_CHANGED;
				break;
			case 216:
				opcode = OpcodeIn.CHAT_MESSAGE;
				break;
			case 38:
				opcode = OpcodeIn.COMMAND;
				break;
			case 64:
				opcode = OpcodeIn.PRIVACY_SETTINGS_CHANGED;
				break;
			case 206:
				opcode = OpcodeIn.REPORT_ABUSE;
				break;
			case 212:
				opcode = OpcodeIn.BANK_CLOSE;
				break;
			case 22:
				opcode = OpcodeIn.BANK_WITHDRAW;
				break;
			case 23:
				opcode = OpcodeIn.BANK_DEPOSIT;
				break;
			case 24:
				opcode = OpcodeIn.BANK_DEPOSIT_ALL_FROM_INVENTORY;
				break;
			case 26:
				opcode = OpcodeIn.BANK_DEPOSIT_ALL_FROM_EQUIPMENT;
				break;
			case 27:
				opcode = OpcodeIn.BANK_SAVE_PRESET;
				break;
			case 28:
				opcode = OpcodeIn.BANK_LOAD_PRESET;
				break;
			case 199:
				opcode = OpcodeIn.INTERFACE_OPTIONS;
				break;
			case 45:
				opcode = OpcodeIn.SLEEPWORD_ENTERED;
				break;
			case 84:
				opcode = OpcodeIn.SKIP_TUTORIAL;
				break;
			case 86:
				opcode = OpcodeIn.ON_BLACK_HOLE;
				break;
			case 89:
				opcode = OpcodeIn.NPC_DEFINITION_REQUEST;
				break;
			case 0:
				opcode = OpcodeIn.LOGIN;
				break;
			case 2:
				opcode = OpcodeIn.REGISTER_ACCOUNT;
				break;
			case 4:
				// originally OpcodeIn.CAST_ON_INVENTORY_ITEM
				// but with SecuritySettings may be OpcodeIn.FORGOT_PASSWORD
				conflictFound = true;
				break;
			case 8:
				// originally OpcodeIn.DUEL_FIRST_SETTINGS_CHANGED
				// but with SecuritySettings may be OpcodeIn.RECOVERY_ATTEMPT
				conflictFound = true;
				break;
			case 197:
				// originally OpcodeIn.DUEL_DECLINED
				// but with SecuritySettings may be OpcodeIn.CHANGE_RECOVERY_REQUEST
				conflictFound = true;
				break;
			case 247:
				// originally OpcodeIn.GROUND_ITEM_TAKE
				// but with SecuritySettings may be OpcodeIn.CHANGE_DETAILS_REQUEST
				conflictFound = true;
				break;
			case 25:
				opcode = OpcodeIn.CHANGE_PASS;
				break;
			case 208:
				opcode = OpcodeIn.SET_RECOVERY;
				break;
			case 253:
				opcode = OpcodeIn.SET_DETAILS;
				break;
			case 196:
				opcode = OpcodeIn.CANCEL_RECOVERY_REQUEST;
				break;
			default:
				break;
		}
		if (conflictFound) {
			opcode = resolveOpcode(packet, player);
		}

		return opcode;
	}

	private OpcodeIn resolveOpcode(Packet packet, Player player) {
		int pID = packet.getID();
		int length = packet.getLength();
		Player affectedPlayer;

		switch (pID) {
			case 4:
				if (player.isLoggedIn()) {
					// only can be cast on inventory item here
					return OpcodeIn.CAST_ON_INVENTORY_ITEM;
				} else {
					return OpcodeIn.FORGOT_PASSWORD;
				}
			case 8:
				if (player.isLoggedIn()) {
					// only can be duel settings changed
					return OpcodeIn.DUEL_FIRST_SETTINGS_CHANGED;
				} else {
					return OpcodeIn.RECOVERY_ATTEMPT;
				}
			case 197:
				// both are same length, check first if player can decline duel
				affectedPlayer = player.getDuel().getDuelRecipient();
				if (affectedPlayer != null && affectedPlayer != player && player.getDuel().isDuelActive()) {
					// likely to be a duel decline request
					return OpcodeIn.DUEL_DECLINED;
				} else {
					return OpcodeIn.CHANGE_RECOVERY_REQUEST;
				}
			case 247:
				if (length > 1) {
					// ground item take request
					return OpcodeIn.GROUND_ITEM_TAKE;
				} else {
					return OpcodeIn.CHANGE_DETAILS_REQUEST;
				}
		}
		return null;
	}

	private boolean isPossiblyValid(Packet packet, Player player) {
		OpcodeIn opcode = toOpcodeEnum(packet, player);
		switch (opcode) {
			case COMBAT_STYLE_CHANGED:
				return packet.getLength() == 1;
			case PLAYER_APPEARANCE_CHANGE:
				return packet.getLength() == 10;
			case QUESTION_DIALOG_ANSWER:
				return packet.getLength() == 1;
			case BANK_SAVE_PRESET:
			case BANK_LOAD_PRESET:
				return packet.getLength() == 2;
			case BANK_WITHDRAW:
				if (player.getConfig().WANT_BANK_NOTES)
					return packet.getLength() >= 7;
				else
					return packet.getLength() >= 6;
			case BANK_DEPOSIT:
				return packet.getLength() >= 6;
			case SHOP_BUY:
			case SHOP_SELL:
				return packet.getLength() == 6;
			case GROUND_ITEM_USE_ITEM:
				return packet.getLength() == 8;
			case ITEM_USE_ITEM:
				return packet.getLength() == 4;
			case ITEM_UNEQUIP_FROM_INVENTORY:
			case ITEM_EQUIP_FROM_INVENTORY:
				return packet.getLength() == 2;
			case ITEM_UNEQUIP_FROM_EQUIPMENT:
			case ITEM_REMOVE_TO_BANK:
				return packet.getLength() == 1;
			case ITEM_EQUIP_FROM_BANK:
				return packet.getLength() == 2;
			case USE_WITH_BOUNDARY:
				return packet.getLength() >= 7;
			case USE_ITEM_ON_SCENERY:
				return packet.getLength() == 6;
			case NPC_USE_ITEM:
			case PLAYER_USE_ITEM:
				return packet.getLength() == 4;
			case BLINK:
				return packet.getLength() == 4;
			case GROUND_ITEM_TAKE:
				return packet.getLength() == 6;
			case ITEM_COMMAND:
				return packet.getLength() >= 7;
			case ITEM_DROP:
				if (player.getWorld().getServer().getConfig().WANT_DROP_X) {
					return packet.getLength() >= 6;
				}
				return packet.getLength() >= 4;
			case OBJECT_COMMAND:
			case OBJECT_COMMAND2:
				return packet.getLength() == 4;
			case INTERACT_WITH_BOUNDARY:
			case INTERACT_WITH_BOUNDARY2:
				return packet.getLength() == 5;
			case NPC_ATTACK:
			case NPC_COMMAND:
			case NPC_COMMAND2:
			case NPC_TALK_TO:
			case PLAYER_ATTACK:
			case PLAYER_FOLLOW:
				return packet.getLength() == 2;
			case CAST_ON_SELF:
				return packet.getLength() == 2;
			case PLAYER_CAST_PVP:
			case CAST_ON_NPC:
				return packet.getLength() == 4;
			case CAST_ON_INVENTORY_ITEM:
				return packet.getLength() == 4;
			case CAST_ON_BOUNDARY:
				return packet.getLength() == 7;
			case CAST_ON_SCENERY:
				return packet.getLength() == 6;
			case CAST_ON_GROUND_ITEM:
				return packet.getLength() == 8;
			case CAST_ON_LAND:
				return packet.getLength() == 6;
			case PLAYER_DUEL:
				return packet.getLength() == 2;
			case DUEL_FIRST_SETTINGS_CHANGED:
				return packet.getLength() == 4;
			case DUEL_OFFER_ITEM:
				return packet.getLength() >= 1;
			case PLAYER_INIT_TRADE_REQUEST:
				return packet.getLength() == 2;
			case PLAYER_ADDED_ITEMS_TO_TRADE_OFFER:
				return packet.getLength() >= 1;
			case PRAYER_ACTIVATED:
			case PRAYER_DEACTIVATED:
				return packet.getLength() == 1;
			case GAME_SETTINGS_CHANGED:
				return packet.getLength() == 2;
			case PRIVACY_SETTINGS_CHANGED:
				return packet.getLength() == 4;
			case PLAYER_ACCEPTED_INIT_TRADE_REQUEST:
			case PLAYER_ACCEPTED_TRADE:
			case PLAYER_DECLINED_TRADE:
			case DUEL_FIRST_ACCEPTED:
			case DUEL_DECLINED:
			case DUEL_SECOND_ACCEPTED:
			case HEARTBEAT:
			case SKIP_TUTORIAL:
			case ON_BLACK_HOLE:
			case LOGOUT:
			case CONFIRM_LOGOUT:
				return packet.getLength() == 0;
			case WALK_TO_POINT:
			case WALK_TO_ENTITY:
				return packet.getLength() >= 4;
		}
		return true;
	}

	@Override
	public AbstractStruct<OpcodeIn> parse(Packet packet, Player player) {

		OpcodeIn opcode = toOpcodeEnum(packet, player);
		AbstractStruct<OpcodeIn> result = null;

		final ItemOnObjectStruct iot;
		final PlayerTradeStruct pt;
		final PlayerDuelStruct pd;
		final TargetPositionStruct tp;
		final TargetObjectStruct to;
		final SpellStruct sp;

		if (!isPossiblyValid(packet, player)) {
			LOGGER.info(String.format("Caught invalid incoming opcode (custom protocol);; id: %d; len: %d\n", packet.getID(), packet.getLength()));
			return null;
		}

		switch (opcode) {
			case COMBAT_STYLE_CHANGED:
				CombatStyleStruct c = new CombatStyleStruct();
				c.style = packet.readByte();
				result = c;
				break;

			case PLAYER_APPEARANCE_CHANGE:
				PlayerAppearanceStruct pl = new PlayerAppearanceStruct();
				pl.headRestrictions = packet.readByte();
				pl.headType = packet.readByte();
				pl.bodyType = packet.readByte();
				pl.mustEqual2 = packet.readByte();
				pl.hairColour = packet.readByte();
				pl.topColour = packet.readByte();
				pl.trouserColour = packet.readByte();
				pl.skinColour = packet.readByte();
				pl.ironmanMode = packet.readByte();
				pl.isOneXp = packet.readByte();
				result = pl;
				break;

			case QUESTION_DIALOG_ANSWER:
				MenuOptionStruct m = new MenuOptionStruct();
				m.option = packet.readByte();
				result = m;
				break;

			case CHAT_MESSAGE:
				ChatStruct cs = new ChatStruct();
				cs.message = DataConversions.getEncryptedString(packet);
				result = cs;
				break;
			case COMMAND:
				CommandStruct co = new CommandStruct();
				co.command = packet.readString();
				result = co;
				break;
			case SOCIAL_ADD_FRIEND:
			case SOCIAL_REMOVE_FRIEND:
			case SOCIAL_ADD_IGNORE:
			case SOCIAL_REMOVE_IGNORE:
			case SOCIAL_SEND_PRIVATE_MESSAGE:
			case SOCIAL_ADD_DELAYED_IGNORE:
				FriendStruct fs = new FriendStruct();
				fs.player = packet.readString();
				if (opcode == OpcodeIn.SOCIAL_SEND_PRIVATE_MESSAGE) {
					fs.message = DataConversions.getEncryptedString(packet);
				}
				result = fs;
				break;

			case BANK_CLOSE:
			case BANK_WITHDRAW:
			case BANK_DEPOSIT:
			case BANK_DEPOSIT_ALL_FROM_INVENTORY:
			case BANK_DEPOSIT_ALL_FROM_EQUIPMENT:
			case BANK_SAVE_PRESET:
			case BANK_LOAD_PRESET:
				BankStruct b = new BankStruct();
				if (opcode == OpcodeIn.BANK_WITHDRAW || opcode == OpcodeIn.BANK_DEPOSIT) {
					b.catalogID = packet.readShort();
					b.amount = packet.readInt();
					if (opcode == OpcodeIn.BANK_WITHDRAW && player.getConfig().WANT_BANK_NOTES) {
						b.noted = packet.readByte() == 1;
					}
				} else if (opcode == OpcodeIn.BANK_LOAD_PRESET || opcode == OpcodeIn.BANK_SAVE_PRESET) {
					b.presetSlot = packet.readShort();
				}
				result = b;
				break;

			case SHOP_CLOSE:
				ShopStruct s = new ShopStruct();
				result = s;
				break;
			case SHOP_BUY:
			case SHOP_SELL:
				ShopStruct s1 = new ShopStruct();
				s1.catalogID = packet.readShort();
				s1.stockAmount = packet.readUnsignedShort();
				s1.amount = packet.readUnsignedShort();
				result = s1;
				break;

			case ITEM_UNEQUIP_FROM_INVENTORY:
			case ITEM_EQUIP_FROM_INVENTORY:
			case ITEM_UNEQUIP_FROM_EQUIPMENT:
			case ITEM_EQUIP_FROM_BANK:
			case ITEM_REMOVE_TO_BANK:
				EquipStruct e = new EquipStruct();
				if (opcode == OpcodeIn.ITEM_UNEQUIP_FROM_INVENTORY || opcode == OpcodeIn.ITEM_EQUIP_FROM_INVENTORY) {
					e.slotIndex = packet.readShort();
				} else if (opcode == OpcodeIn.ITEM_UNEQUIP_FROM_EQUIPMENT || opcode == OpcodeIn.ITEM_REMOVE_TO_BANK) {
					e.slotIndex = packet.readByte();
				} else if (opcode == OpcodeIn.ITEM_EQUIP_FROM_BANK) {
					e.slotIndex = packet.readShort();
				}
				result = e;
				break;

			case GROUND_ITEM_USE_ITEM:
				ItemOnGroundItemStruct iog = new ItemOnGroundItemStruct();
				iog.groundItemCoord = new Point(packet.readShort(), packet.readShort());
				iog.slotIndex = packet.readShort();
				iog.groundItemId = packet.readShort();
				result = iog;
				break;

			case ITEM_USE_ITEM:
				ItemOnItemStruct ioi = new ItemOnItemStruct();
				ioi.slotIndex1 = packet.readShort();
				ioi.slotIndex2 = packet.readShort();
				result = ioi;
				break;

			case USE_WITH_BOUNDARY:
				iot = new ItemOnObjectStruct();
				iot.coordObject = new Point(packet.readShort(), packet.readShort());
				iot.direction = packet.readByte();
				iot.slotID = packet.readShort();
				if (player.getConfig().WANT_EQUIPMENT_TAB &&
				    iot.slotID == -1 &&
				    packet.getReadableBytes() >= 2) {
					iot.itemID = packet.readShort();
				}
				result = iot;
				break;

			case USE_ITEM_ON_SCENERY:
				iot = new ItemOnObjectStruct();
				iot.coordObject = new Point(packet.readShort(), packet.readShort());
				iot.slotID = packet.readShort();
				result = iot;
				break;

			case NPC_USE_ITEM:
			case PLAYER_USE_ITEM:
				ItemOnMobStruct it = new ItemOnMobStruct();
				it.serverIndex = packet.readShort();
				it.slotIndex = packet.readShort();
				result = it;
				break;

			case BLINK:
				tp = new TargetPositionStruct();
				tp.coordinate = new Point(packet.readShort(), packet.readShort());
				result = tp;
				break;

			case GROUND_ITEM_TAKE:
				tp = new TargetPositionStruct();
				tp.coordinate = new Point(packet.readShort(), packet.readShort());
				tp.itemId = packet.readShort();
				result = tp;
				break;

			case ITEM_COMMAND:
			case ITEM_DROP:
				ItemCommandStruct ic = new ItemCommandStruct();
				ic.index = packet.readShort();
				if (opcode == OpcodeIn.ITEM_COMMAND) {
					ic.amount = packet.readInt();
					if (ic.index == -1 && packet.getReadableBytes() >= 2) {
						ic.realIndex = packet.readShort();
					}
					ic.commandIndex = packet.readByte();
				} else {
					if (player.getWorld().getServer().getConfig().WANT_DROP_X) {
						ic.amount = packet.readInt();
					}
					if (ic.index == -1 && packet.getReadableBytes() >= 2) {
						ic.realIndex = packet.readShort();
					}
				}
				result = ic;
				break;

			case OBJECT_COMMAND:
			case OBJECT_COMMAND2:
				to = new TargetObjectStruct();
				to.coordObject = new Point(packet.readShort(), packet.readShort());
				result = to;
				break;

			case INTERACT_WITH_BOUNDARY:
			case INTERACT_WITH_BOUNDARY2:
				to = new TargetObjectStruct();
				to.coordObject = new Point(packet.readShort(), packet.readShort());
				to.direction = packet.readByte();
				result = to;
				break;

			case NPC_ATTACK:
			case NPC_COMMAND:
			case NPC_COMMAND2:
			case NPC_TALK_TO:
			case PLAYER_ATTACK:
			case PLAYER_FOLLOW:
				TargetMobStruct t = new TargetMobStruct();
				t.serverIndex = packet.readShort();
				result = t;
				break;

			case CAST_ON_SELF:
				sp = new SpellStruct();
				sp.spell = Constants.spellToEnum(packet.readShort());
				result = sp;
				break;

			case PLAYER_CAST_PVP:
				sp = new SpellStruct();
				sp.spell = Constants.spellToEnum(packet.readShort());
				sp.targetIndex = packet.readShort();
				result = sp;
				break;

			case CAST_ON_NPC:
				sp = new SpellStruct();
				sp.spell = Constants.spellToEnum(packet.readShort());
				sp.targetIndex = packet.readShort();
				result = sp;
				break;

			case CAST_ON_INVENTORY_ITEM:
				sp = new SpellStruct();
				sp.spell = Constants.spellToEnum(packet.readShort());
				sp.targetIndex = packet.readShort();
				result = sp;
				break;

			case CAST_ON_BOUNDARY:
				sp = new SpellStruct();
				sp.spell = Constants.spellToEnum(packet.readShort());
				sp.targetCoord = new Point(packet.readShort(), packet.readShort());
				sp.direction = packet.readByte();
				result = sp;
				break;

			case CAST_ON_SCENERY:
				sp = new SpellStruct();
				sp.spell = Constants.spellToEnum(packet.readShort());
				sp.targetCoord = new Point(packet.readShort(), packet.readShort());
				result = sp;
				break;

			case CAST_ON_GROUND_ITEM:
				sp = new SpellStruct();
				sp.spell = Constants.spellToEnum(packet.readShort());
				sp.targetCoord = new Point(packet.readShort(), packet.readShort());
				sp.targetIndex = packet.readShort();
				result = sp;
				break;

			case CAST_ON_LAND:
				sp = new SpellStruct();
				sp.spell = Constants.spellToEnum(packet.readShort());
				sp.targetCoord = new Point(packet.readShort(), packet.readShort());
				result = sp;
				break;

			case PLAYER_DUEL:
				pd = new PlayerDuelStruct();
				pd.targetPlayerID = packet.readShort();
				result = pd;
				break;

			case DUEL_FIRST_SETTINGS_CHANGED:
				pd = new PlayerDuelStruct();
				pd.disallowRetreat = packet.readByte();
				pd.disallowMagic = packet.readByte();
				pd.disallowPrayer = packet.readByte();
				pd.disallowWeapons = packet.readByte();
				result = pd;
				break;

			case DUEL_OFFER_ITEM:
				pd = new PlayerDuelStruct();
				pd.duelCount = packet.readByte();
				pd.duelCatalogIDs = new int[pd.duelCount];
				pd.duelAmounts = new int[pd.duelCount];
				pd.duelNoted = new boolean[pd.duelCount];
				for (int slot = 0; slot < pd.duelCount; slot++) {
					if (packet.getReadableBytes() < 8) {
						break;
					}
					pd.duelCatalogIDs[slot] = packet.readShort();
					pd.duelAmounts[slot] = packet.readInt();
					pd.duelNoted[slot] = packet.readShort() == 1;
				}
				result = pd;
				break;

			case DUEL_FIRST_ACCEPTED:
			case DUEL_DECLINED:
			case DUEL_SECOND_ACCEPTED:
				pd = new PlayerDuelStruct();
				result = pd;
				break;

			case PLAYER_INIT_TRADE_REQUEST:
				pt = new PlayerTradeStruct();
				pt.targetPlayerID = packet.readShort();
				result = pt;
				break;

			case PLAYER_ACCEPTED_INIT_TRADE_REQUEST:
			case PLAYER_ACCEPTED_TRADE:
			case PLAYER_DECLINED_TRADE:
				pt = new PlayerTradeStruct();
				result = pt;
				break;

			case PLAYER_ADDED_ITEMS_TO_TRADE_OFFER:
				pt = new PlayerTradeStruct();
				pt.tradeCount = packet.readByte();
				pt.tradeCatalogIDs = new int[pt.tradeCount];
				pt.tradeAmounts = new int[pt.tradeCount];
				pt.tradeNoted = new boolean[pt.tradeCount];
				for (int slot = 0; slot < pt.tradeCount; slot++) {
					if (packet.getReadableBytes() < 8) {
						break;
					}
					pt.tradeCatalogIDs[slot] = packet.readShort();
					pt.tradeAmounts[slot] = packet.readInt();
					pt.tradeNoted[slot] = packet.readShort() == 1;
				}
				result = pt;
				break;

			case PRAYER_ACTIVATED:
			case PRAYER_DEACTIVATED:
				PrayerStruct p = new PrayerStruct();
				p.prayerID = packet.readByte();
				result = p;
				break;

			case GAME_SETTINGS_CHANGED:
				GameSettingStruct gs = new GameSettingStruct();
				int setting = gs.index = packet.readByte();
				int value = gs.value = packet.readByte();
				if (setting == 0) {
					gs.cameraModeAuto = value;
				} else if (setting == 2) {
					gs.mouseButtonOne = value;
				} else if (setting == 3) {
					gs.soundDisabled = value;
				} else {
					// custom settings, too many to name individually
				}
				result = gs;
				break;

			case PRIVACY_SETTINGS_CHANGED:
				PrivacySettingsStruct pr = new PrivacySettingsStruct();
				pr.blockChat = packet.readByte();
				pr.blockPrivate = packet.readByte();
				pr.blockTrade = packet.readByte();
				pr.blockDuel = packet.readByte();
				result = pr;
				break;

			case CHANGE_PASS:
			case CANCEL_RECOVERY_REQUEST:
			case CHANGE_RECOVERY_REQUEST:
			case CHANGE_DETAILS_REQUEST:
			case SET_RECOVERY:
			case SET_DETAILS:
				SecuritySettingsStruct sec = new SecuritySettingsStruct();
				if (opcode == OpcodeIn.CHANGE_PASS) {
					String oldPassword = packet.readString().trim();
					String newPassword = packet.readString().trim();

					sec.passwords = new String[]{ oldPassword, newPassword };
				} else if (opcode == OpcodeIn.SET_RECOVERY) {
					String[] questions = new String[5];
					String[] answers = new String[5];
					for (int i=0; i<5; i++) {
						questions[i] = packet.readString().trim();
						answers[i] = DataConversions.normalize(packet.readString(), 50);
					}

					sec.questions = questions.clone();
					sec.answers = answers.clone();
				} else if (opcode == OpcodeIn.SET_DETAILS) {
					String[] details = new String[4];
					for (int i = 0; i < 4; i++) {
						details[i] = packet.readString();
					}

					sec.details = details.clone();
				}
				result = sec;
				break;

			case INTERFACE_OPTIONS:
				OptionsStruct os = new OptionsStruct();
				os.index = packet.readByte();
				final InterfaceOptions option = InterfaceOptions.getById(os.index);
				switch (option) {
					case SWAP_CERT:
					case SWAP_NOTE:
						os.value = packet.readByte();
						break;
					case BANK_SWAP:
					case BANK_INSERT:
					case INVENTORY_INSERT:
					case INVENTORY_SWAP:
						os.slot = packet.readInt();
						os.to = packet.readInt();
						break;
					case CANCEL_BATCH:
						//nothing
						break;
					case IRONMAN_MODE:
						os.value = packet.readByte();
						if (os.value == 0 || os.value == 1) {
							os.value2 = packet.readByte();
						}
						break;
					case BANK_PIN:
						os.value = packet.readByte();
						if (os.value == 0) {
							os.pin = packet.readString();
						}
						break;
					case UNUSED:
						//inexistent
						break;
					case AUCTION:
						os.value = packet.readByte();
						final AuctionOptions auctionOption = AuctionOptions.getById((int)os.value);
						switch (auctionOption) {
							case BUY:
								os.id = packet.readInt();
								os.amount = packet.readInt();
								break;
							case CREATE:
								os.id = packet.readInt();
								os.amount = packet.readInt();
								os.price = packet.readInt();
								break;
							case ABORT:
							case DELETE:
								os.id = packet.readInt();
								break;
							case REFRESH:
							case CLOSE:
								// nothing
								break;
						}
						break;
					case CLAN:
						os.value = packet.readByte();
						final ClanOptions clanOption = ClanOptions.getById((int)os.value);
						switch (clanOption) {
							case CREATE:
								os.name = packet.readString();
								os.tag = packet.readString();
								break;
							case LEAVE:
							case ACCEPT_INVITE:
							case DECLINE_INVITE:
							case SEND_CLAN_INFO:
								//nothing
								break;
							case INVITE_PLAYER:
							case KICK_PLAYER:
								os.player = packet.readString();
								break;
							case RANK_PLAYER:
								os.player = packet.readString();
								os.value2 = packet.readByte();
								break;
							case CLAN_SETTINGS:
								os.value2 = packet.readByte();
								if (os.value2 >= 0 && os.value2 <= 3) {
									os.value3 = packet.readByte();
								}
								break;
						}
						break;
					case PARTY:
						os.value = packet.readByte();
						final PartyOptions partyOption = PartyOptions.getById((int)os.value);
						switch (partyOption) {
							case CREATE_OR_INVITE:
								os.id = packet.readShort();
								os.name = packet.readString();
								os.tag = packet.readString();
								break;
							case INIT:
							case LEAVE:
							case ACCEPT_INVITE:
							case DECLINE_INVITE:
							case SEND_PARTY_INFO:
								//nothing
								break;
							case KICK_PLAYER:
								os.player = packet.readString();
								break;
							case RANK_PLAYER:
								os.player = packet.readString();
								os.value2 = packet.readByte();
								break;
							case PARTY_SETTINGS:
								os.value2 = packet.readByte();
								if (os.value2 >= 0 && os.value2 <= 3) {
									os.value3 = packet.readByte();
								}
								break;
							case INVITE_PLAYER_OR_MAKE:
								os.player = packet.readString();
								os.name = packet.readString();
								os.tag = packet.readString();
								break;
						}
						break;
					case POINTS:
						os.value = packet.readByte();
						final PointsOptions pointsOption = PointsOptions.getById((int)os.value);
						switch (pointsOption) {
							case REDUCE_DEFENSE:
							case INCREASE_DEFENSE:
							case INCREASE_ATTACK:
							case INCREASE_STRENGTH:
							case INCREASE_RANGED:
							case INCREASE_PRAYER:
							case INCREASE_MAGIC:
							case REDUCE_ATTACK:
							case REDUCE_STRENGTH:
							case REDUCE_RANGED:
							case REDUCE_PRAYER:
							case REDUCE_MAGIC:
							case POINTS_TO_GP:
							case SAVE_PRESET:
								os.amount = packet.readInt();
								break;
						}
						break;
				}
				result = os;
				break;

			case REPORT_ABUSE:
				ReportStruct r = new ReportStruct();
				r.targetPlayerName = packet.readString();
				r.reason = packet.readByte();
				r.suggestsOrMutes = packet.readByte();
				result = r;
				break;

			case SLEEPWORD_ENTERED:
				SleepStruct sl = new SleepStruct();
				sl.sleepDelay = 100; // need to determine default
				sl.sleepWord = packet.readString();
				result = sl;
				break;

			case HEARTBEAT:
			case SKIP_TUTORIAL:
			case ON_BLACK_HOLE:
			case LOGOUT:
			case CONFIRM_LOGOUT:
				NoPayloadStruct n = new NoPayloadStruct();
				result = n;
				break;

			case WALK_TO_POINT:
			case WALK_TO_ENTITY:
				WalkStruct w = new WalkStruct();
				w.firstStep = new Point(packet.readShort(), packet.readShort());

				int numWaypoints = packet.getReadableBytes() / 2;
				for (int stepCount = 0; stepCount < numWaypoints; stepCount++) {
					if (packet.getReadableBytes() < 2) {
						break;
					}
					w.steps.add(new Point(packet.readByte(), packet.readByte()));
				}
				result = w;
				break;
		}

		if (result != null) {
			result.setOpcode(opcode);
		}

		return result;

	}
}
