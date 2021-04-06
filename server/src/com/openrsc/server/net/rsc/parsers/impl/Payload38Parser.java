package com.openrsc.server.net.rsc.parsers.impl;

import com.openrsc.server.constants.Spells;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.parsers.PayloadParser;
import com.openrsc.server.net.rsc.struct.*;
import com.openrsc.server.util.rsc.DataConversions;

/**
 * RSC Protocol-38 (Recreated) Parser of Incoming Packets to respective Protocol Independent Structs
 * **/
public class Payload38Parser implements PayloadParser<OpcodeIn> {
	@Override
	public OpcodeIn toOpcodeEnum(Packet packet, Player player) {
		OpcodeIn opcode = null;
		switch (packet.getID()) {
			case 5:
				opcode = OpcodeIn.HEARTBEAT;
				break;
			case 215:
				opcode = OpcodeIn.WALK_TO_ENTITY;
				break;
			case 255:
				opcode = OpcodeIn.WALK_TO_POINT;
				break;
			case 1:
				opcode = OpcodeIn.CONFIRM_LOGOUT;
				break;
			case 231:
				opcode = OpcodeIn.COMBAT_STYLE_CHANGED;
				break;
			case 237:
				opcode = OpcodeIn.QUESTION_DIALOG_ANSWER;
				break;
			case 236:
				opcode = OpcodeIn.PLAYER_APPEARANCE_CHANGE;
				break;
			case 29:
				opcode = OpcodeIn.SOCIAL_ADD_IGNORE;
				break;
			case 26:
				opcode = OpcodeIn.SOCIAL_ADD_FRIEND;
				break;
			case 28:
				opcode = OpcodeIn.SOCIAL_SEND_PRIVATE_MESSAGE;
				break;
			case 27:
				opcode = OpcodeIn.SOCIAL_REMOVE_FRIEND;
				break;
			case 30:
				opcode = OpcodeIn.SOCIAL_REMOVE_IGNORE;
				break;
			case 238:
				opcode = OpcodeIn.INTERACT_WITH_BOUNDARY;
				break;
			case 229:
				opcode = OpcodeIn.INTERACT_WITH_BOUNDARY2;
				break;
			case 223:
				opcode = OpcodeIn.CAST_ON_BOUNDARY;
				break;
			case 239:
				opcode = OpcodeIn.USE_WITH_BOUNDARY;
				break;
			case 245:
				opcode = OpcodeIn.NPC_TALK_TO;
				break;
			case 244:
				opcode = OpcodeIn.NPC_ATTACK;
				break;
			case 225:
				opcode = OpcodeIn.CAST_ON_NPC;
				break;
			case 243:
				opcode = OpcodeIn.NPC_USE_ITEM;
				break;
			case 226:
				opcode = OpcodeIn.PLAYER_CAST_PVP;
				break;
			case 219:
				opcode = OpcodeIn.PLAYER_USE_ITEM;
				break;
			case 228:
				opcode = OpcodeIn.PLAYER_ATTACK;
				break;
			case 235:
				opcode = OpcodeIn.PLAYER_INIT_TRADE_REQUEST;
				break;
			case 214:
				opcode = OpcodeIn.PLAYER_FOLLOW;
				break;
			case 224:
				opcode = OpcodeIn.CAST_ON_GROUND_ITEM;
				break;
			case 250:
				opcode = OpcodeIn.GROUND_ITEM_USE_ITEM;
				break;
			case 240:
				opcode = OpcodeIn.ITEM_USE_ITEM;
				break;
			case 248:
				opcode = OpcodeIn.ITEM_UNEQUIP_FROM_INVENTORY;
				break;
			case 249:
				opcode = OpcodeIn.ITEM_EQUIP_FROM_INVENTORY;
				break;
			case 246:
				opcode = OpcodeIn.ITEM_COMMAND;
				break;
			case 251:
				opcode = OpcodeIn.ITEM_DROP;
				break;
			case 227:
				opcode = OpcodeIn.CAST_ON_SELF;
				break;
			case 221:
				opcode = OpcodeIn.CAST_ON_LAND;
				break;
			case 242:
				opcode = OpcodeIn.OBJECT_COMMAND;
				break;
			case 230:
				opcode = OpcodeIn.OBJECT_COMMAND2;
				break;
			case 222:
				opcode = OpcodeIn.CAST_ON_SCENERY;
				break;
			case 241:
				opcode = OpcodeIn.USE_ITEM_ON_SCENERY;
				break;
			case 218:
				opcode = OpcodeIn.SHOP_CLOSE;
				break;
			case 217:
				opcode = OpcodeIn.SHOP_BUY;
				break;
			case 216:
				opcode = OpcodeIn.SHOP_SELL;
				break;
			case 233:
				opcode = OpcodeIn.PLAYER_DECLINED_TRADE;
				break;
			case 234:
				opcode = OpcodeIn.PLAYER_ADDED_ITEMS_TO_TRADE_OFFER;
				break;
			case 232:
				opcode = OpcodeIn.PLAYER_ACCEPTED_TRADE;
				break;
			case 3:
				opcode = OpcodeIn.CHAT_MESSAGE;
				break;
			case 31:
				opcode = OpcodeIn.PRIVACY_SETTINGS_CHANGED;
				break;
			case 0: //19 is relogin
			case 19:
				opcode = OpcodeIn.LOGIN;
				break;
			case 2:
				opcode = OpcodeIn.REGISTER_ACCOUNT;
				break;
			case 220:
				opcode = OpcodeIn.CAST_ON_INVENTORY_ITEM;
				break;
			case 252:
				opcode = OpcodeIn.GROUND_ITEM_TAKE;
				break;
			case 25:
				opcode = OpcodeIn.CHANGE_PASS;
				break;
			case 213:
				opcode = OpcodeIn.GAME_SETTINGS_CHANGED;
				break;
			case 17:
				opcode = OpcodeIn.SEND_DEBUG_INFO;
				break;
			case 254:
				opcode = OpcodeIn.KNOWN_PLAYERS;
				break;
			default:
				break;
		}

		return opcode;
	}

	@Override
	public AbstractStruct<OpcodeIn> parse(Packet packet, Player player) {

		OpcodeIn opcode = toOpcodeEnum(packet, player);
		AbstractStruct<OpcodeIn> result = null;

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
				pl.chosenClass = packet.readByte();
				pl.pkMode = packet.readByte();
				result = pl;
				break;

			case QUESTION_DIALOG_ANSWER:
				MenuOptionStruct m = new MenuOptionStruct();
				m.option = packet.readByte();
				result = m;
				break;

			case CHAT_MESSAGE:
				ChatStruct cs = new ChatStruct();
				cs.message = packet.readString();
				result = cs;
				break;
			case SOCIAL_ADD_FRIEND:
			case SOCIAL_REMOVE_FRIEND:
			case SOCIAL_ADD_IGNORE:
			case SOCIAL_REMOVE_IGNORE:
			case SOCIAL_SEND_PRIVATE_MESSAGE:
				FriendStruct fs = new FriendStruct();
				fs.player = DataConversions.hashToUsername(packet.readLong());
				if (opcode == OpcodeIn.SOCIAL_SEND_PRIVATE_MESSAGE) {
					int len = packet.readByte();
					fs.message = packet.readString(len);
				}
				result = fs;
				break;

			case SHOP_CLOSE:
				ShopStruct s = new ShopStruct();
				result = s;
				break;
			case SHOP_BUY:
			case SHOP_SELL:
				ShopStruct s1 = new ShopStruct();
				s1.catalogID = packet.readShort();
				s1.price = packet.readUnsignedShort();
				s1.amount = 1;
				result = s1;
				break;

			case ITEM_UNEQUIP_FROM_INVENTORY:
			case ITEM_EQUIP_FROM_INVENTORY:
				EquipStruct e = new EquipStruct();
				e.slotIndex = packet.readShort();
				result = e;
				break;

			case GROUND_ITEM_USE_ITEM:
				ItemOnGroundItemStruct iog = new ItemOnGroundItemStruct();
				iog.groundItemCoord = new Point(packet.readShort(), packet.readShort());
				iog.groundItemId = packet.readShort();
				iog.slotIndex = packet.readShort();
				result = iog;
				break;

			case ITEM_USE_ITEM:
				ItemOnItemStruct ioi = new ItemOnItemStruct();
				ioi.slotIndex1 = packet.readShort();
				ioi.slotIndex2 = packet.readShort();
				result = ioi;
				break;

			case USE_WITH_BOUNDARY:
			case USE_ITEM_ON_SCENERY:
				ItemOnObjectStruct ioo = new ItemOnObjectStruct();
				ioo.coordObject = new Point(packet.readShort(), packet.readShort());
				if (opcode == OpcodeIn.USE_WITH_BOUNDARY) {
					ioo.direction = packet.readByte();
				}
				ioo.slotID = packet.readShort();
				result = ioo;
				break;

			case NPC_USE_ITEM:
			case PLAYER_USE_ITEM:
				ItemOnMobStruct it = new ItemOnMobStruct();
				it.serverIndex = packet.readShort();
				it.slotIndex = packet.readShort();
				result = it;
				break;

			case GROUND_ITEM_TAKE:
				TargetPositionStruct tp = new TargetPositionStruct();
				tp.coordinate = new Point(packet.readShort(), packet.readShort());
				tp.itemId = packet.readShort();
				result = tp;
				break;

			case ITEM_COMMAND:
			case ITEM_DROP:
				ItemCommandStruct ic = new ItemCommandStruct();
				ic.index = packet.readShort();
				result = ic;
				break;

			case OBJECT_COMMAND:
			case OBJECT_COMMAND2:
			case INTERACT_WITH_BOUNDARY:
			case INTERACT_WITH_BOUNDARY2:
				TargetObjectStruct to = new TargetObjectStruct();
				to.coordObject = new Point(packet.readShort(), packet.readShort());
				if (opcode == OpcodeIn.INTERACT_WITH_BOUNDARY || opcode == OpcodeIn.INTERACT_WITH_BOUNDARY2) {
					to.direction = packet.readByte();
				}
				result = to;
				break;

			case NPC_ATTACK:
			case NPC_TALK_TO:
			case PLAYER_ATTACK:
			case PLAYER_FOLLOW:
				TargetMobStruct t = new TargetMobStruct();
				t.serverIndex = packet.readShort();
				result = t;
				break;

			case CAST_ON_SELF:
			case PLAYER_CAST_PVP:
			case CAST_ON_NPC:
			case CAST_ON_INVENTORY_ITEM:
			case CAST_ON_BOUNDARY:
			case CAST_ON_SCENERY:
			case CAST_ON_GROUND_ITEM:
			case CAST_ON_LAND:
				SpellStruct sp = new SpellStruct();
				if (opcode == OpcodeIn.PLAYER_CAST_PVP || opcode == OpcodeIn.CAST_ON_NPC
					|| opcode == OpcodeIn.CAST_ON_INVENTORY_ITEM) {
					sp.targetIndex = packet.readShort();
				} else if (opcode == OpcodeIn.CAST_ON_BOUNDARY || opcode == OpcodeIn.CAST_ON_SCENERY
					|| opcode == OpcodeIn.CAST_ON_GROUND_ITEM || opcode == OpcodeIn.CAST_ON_LAND) {
					sp.targetCoord = new Point(packet.readShort(), packet.readShort());
					if (opcode == OpcodeIn.CAST_ON_BOUNDARY) {
						sp.direction = packet.readByte();
					} else if (opcode == OpcodeIn.CAST_ON_GROUND_ITEM) {
						sp.targetIndex = packet.readShort();
					}
				}
				// TODO: with good and evil magic could have been upper 1 byte if good and lower 1 byte if evil
				int val = packet.readShort();
				Spells spell = null;
				if ((val & 0xff00) == 0) {
					// good magic
					val = (val >> 8) & 0xff;
					switch (val) {
						case 0:
							spell = Spells.CHILL_BOLT;
							break;
						case 1:
							spell = Spells.BURST_OF_STRENGTH;
							break;
						case 2:
							spell = Spells.CAMOUFLAGE;
							break;
						case 3:
							spell = Spells.ROCK_SKIN;
							break;
						case 4:
							spell = Spells.WIND_BOLT_R;
							break;
					}
				} else {
					// evil magic
					switch (val) {
						case 0:
							spell = Spells.CONFUSE_R;
							break;
						case 1:
							spell = Spells.THICK_SKIN;
							break;
						case 2:
							spell = Spells.SHOCK_BOLT;
							break;
						case 3:
							spell = Spells.ELEMENTAL_BOLT;
							break;
						case 4:
							spell = Spells.FEAR;
							break;
					}
				}
				sp.spell = spell;
				result = sp;
				break;

			case PLAYER_INIT_TRADE_REQUEST:
			case PLAYER_ACCEPTED_TRADE:
			case PLAYER_DECLINED_TRADE:
			case PLAYER_ADDED_ITEMS_TO_TRADE_OFFER:
				PlayerTradeStruct pt = new PlayerTradeStruct();
				if (opcode == OpcodeIn.PLAYER_INIT_TRADE_REQUEST) {
					pt.targetPlayerID = packet.readShort();
				} else if (opcode == OpcodeIn.PLAYER_ACCEPTED_TRADE) {
					pt.tradeAccepted = packet.readByte();
				} else if (opcode == OpcodeIn.PLAYER_ADDED_ITEMS_TO_TRADE_OFFER) {
					pt.tradeCount = packet.readByte();
					pt.tradeCatalogIDs = new int[pt.tradeCount];
					pt.tradeAmounts = new int[pt.tradeCount];
					pt.tradeNoted = new boolean[pt.tradeCount];
					for (int slot = 0; slot < pt.tradeCount; slot++) {
						pt.tradeCatalogIDs[slot] = packet.readShort();
						pt.tradeAmounts[slot] = packet.readUnsignedShort();
						pt.tradeNoted[slot] = false;
					}
				}
				result = pt;
				break;

			case KNOWN_PLAYERS:
				KnownPlayersStruct kp = new KnownPlayersStruct();
				kp.playerCount = packet.readShort();
				kp.playerServerIndex = new int[kp.playerCount];
				kp.playerServerId = new int[kp.playerCount];
				for (int i = 0; i < kp.playerCount; i++) {
					kp.playerServerIndex[i] = packet.readShort();
					kp.playerServerId[i] = packet.readShort();
				}
				result = kp;
				break;

			case GAME_SETTINGS_CHANGED:
				GameSettingStruct gs = new GameSettingStruct();
				int setting = packet.readByte();
				int value = packet.readByte();
				if (setting == 0) {
					gs.cameraModeAuto = value;
				} else if (setting == 1) {
					gs.playerKiller = value;
				} else if (setting == 2) {
					gs.mouseButtonOne = value;
				}
				result = gs;
				break;

			case PRIVACY_SETTINGS_CHANGED:
				PrivacySettingsStruct pr = new PrivacySettingsStruct();
				pr.hideStatus = packet.readByte();
				pr.blockChat = packet.readByte();
				pr.blockPrivate = packet.readByte();
				pr.blockTrade = packet.readByte();
				packet.readByte(); // todo:? always sent 0 here
				result = pr;
				break;

			case CHANGE_PASS:
				SecuritySettingsStruct sec = new SecuritySettingsStruct();
				String newPassword = packet.readString(20); // only newPassword sent
				sec.passwords = new String[]{ "", newPassword };

				result = sec;
				break;

			case HEARTBEAT:
			case CONFIRM_LOGOUT:
				NoPayloadStruct n = new NoPayloadStruct();
				result = n;
				break;

			case WALK_TO_POINT:
			case WALK_TO_ENTITY:
				WalkStruct w = new WalkStruct();
				w.firstStep = new Point(packet.readAnotherShort(), packet.readAnotherShort());

				int numWaypoints = packet.getReadableBytes() / 2;
				for (int stepCount = 0; stepCount < numWaypoints; stepCount++) {
					w.steps.add(new Point(packet.readByte(), packet.readByte()));
				}
				result = w;
				break;

			case SEND_DEBUG_INFO:
				DebugInfoStruct ds = new DebugInfoStruct();
				ds.infoString = packet.readString();
				result = ds;
				break;
		}

		if (result != null) {
			result.setOpcode(opcode);
		}

		return result;

	}

	public static boolean isPossiblyValid(int opcode, int length, int protocolVer) {
		return true;
	}
}
