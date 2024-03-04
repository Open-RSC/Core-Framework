package com.openrsc.server.net.rsc.parsers.impl;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.Crypto;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.parsers.PayloadParser;
import com.openrsc.server.net.rsc.struct.*;
import com.openrsc.server.net.rsc.struct.incoming.*;
import com.openrsc.server.util.rsc.DataConversions;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * RSC Protocol-235 Parser of Incoming Packets to respective Protocol Independent Structs
 * **/
public class Payload235Parser implements PayloadParser<OpcodeIn> {
	@Override
	public OpcodeIn toOpcodeEnum(Packet packet, Player player) {
		OpcodeIn opcode = null;
		// since we have 235 w/RSC175 Security Settings
		// some packet opcodes conflict, so packet needs to be further examined
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
			case 45:
				opcode = OpcodeIn.SLEEPWORD_ENTERED;
				break;
			case 84:
				opcode = OpcodeIn.SKIP_TUTORIAL;
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
				co.command = packet.readZeroPaddedString();
				result = co;
				break;
			case SOCIAL_ADD_FRIEND:
			case SOCIAL_REMOVE_FRIEND:
			case SOCIAL_ADD_IGNORE:
			case SOCIAL_REMOVE_IGNORE:
			case SOCIAL_SEND_PRIVATE_MESSAGE:
				FriendStruct fs = new FriendStruct();
				fs.player = packet.readZeroPaddedString();
				if (opcode == OpcodeIn.SOCIAL_SEND_PRIVATE_MESSAGE) {
					fs.message = DataConversions.getEncryptedString(packet);
				}
				result = fs;
				break;

			case BANK_CLOSE:
				BankStruct b = new BankStruct();
				result = b;
				break;
			case BANK_WITHDRAW:
			case BANK_DEPOSIT:
				BankStruct b1 = new BankStruct();
				b1.catalogID = packet.readShort();
				b1.amount = packet.readInt();
				b1.magicNumber = packet.readInt();
				result = b1;
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

			case BLINK:
			case GROUND_ITEM_TAKE:
				TargetPositionStruct tp = new TargetPositionStruct();
				tp.coordinate = new Point(packet.readShort(), packet.readShort());
				if (opcode == OpcodeIn.GROUND_ITEM_TAKE) {
					tp.itemId = packet.readShort();
				}
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
			case NPC_COMMAND:
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
				sp.spell = Constants.spellToEnum(packet.readShort());
				result = sp;
				break;

			case PLAYER_DUEL:
			case DUEL_FIRST_SETTINGS_CHANGED:
			case DUEL_FIRST_ACCEPTED:
			case DUEL_DECLINED:
			case DUEL_OFFER_ITEM:
			case DUEL_SECOND_ACCEPTED:
				PlayerDuelStruct pd = new PlayerDuelStruct();
				if (opcode == OpcodeIn.PLAYER_DUEL) {
					pd.targetPlayerID = packet.readShort();
				} else if (opcode == OpcodeIn.DUEL_OFFER_ITEM) {
					pd.duelCount = packet.readByte();
					pd.duelCatalogIDs = new int[pd.duelCount];
					pd.duelAmounts = new int[pd.duelCount];
					pd.duelNoted = new boolean[pd.duelCount];
					for (int slot = 0; slot < pd.duelCount; slot++) {
						pd.duelCatalogIDs[slot] = packet.readShort();
						pd.duelAmounts[slot] = packet.readInt();
						pd.duelNoted[slot] = false;
					}
				} else if (opcode == OpcodeIn.DUEL_FIRST_SETTINGS_CHANGED) {
					pd.disallowRetreat = packet.readByte();
					pd.disallowMagic = packet.readByte();
					pd.disallowPrayer = packet.readByte();
					pd.disallowWeapons = packet.readByte();
				}
				result = pd;
				break;

			case PLAYER_INIT_TRADE_REQUEST:
			case PLAYER_ACCEPTED_INIT_TRADE_REQUEST:
			case PLAYER_ACCEPTED_TRADE:
			case PLAYER_DECLINED_TRADE:
			case PLAYER_ADDED_ITEMS_TO_TRADE_OFFER:
				PlayerTradeStruct pt = new PlayerTradeStruct();
				if (opcode == OpcodeIn.PLAYER_INIT_TRADE_REQUEST) {
					pt.targetPlayerID = packet.readShort();
				} else if (opcode == OpcodeIn.PLAYER_ADDED_ITEMS_TO_TRADE_OFFER) {
					pt.tradeCount = packet.readByte();
					pt.tradeCatalogIDs = new int[pt.tradeCount];
					pt.tradeAmounts = new int[pt.tradeCount];
					pt.tradeNoted = new boolean[pt.tradeCount];
					for (int slot = 0; slot < pt.tradeCount; slot++) {
						pt.tradeCatalogIDs[slot] = packet.readShort();
						pt.tradeAmounts[slot] = packet.readInt();
						pt.tradeNoted[slot] = false;
					}
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
					// Get encrypted block
					// old + new password is always 40 characters long, with spaces at the end.
					// each blocks having encrypted 7 chars of password
					int blockLen;
					byte[] decBlock; // current decrypted block
					int session = -1; // TODO: should be players stored TCP session to check if request should be processed
					int receivedSession;
					boolean errored = false;
					byte[] concatPassData = new byte[42];
					for (int i = 0; i < 6; i++) {
						blockLen = packet.readUnsignedByte();
						decBlock = Crypto.decryptRSA(packet.readBytes(blockLen), 0, blockLen);
						// TODO: there are ignored nonces at the beginning of the decrypted block
						receivedSession = ByteBuffer.wrap(Arrays.copyOfRange(decBlock, 4, 8)).getInt();
						// decrypted packet must be of length 15
						if (session == -1 && decBlock.length == 15) {
							session = receivedSession;
						} else if (session != receivedSession || decBlock.length != 15) {
							errored = true; // decryption error occurred
						}

						if (!errored) {
							System.arraycopy(decBlock, 8, concatPassData, i * 7, 7);
						}
					}

					String oldPassword = "";
					String newPassword = "";
					try {
						oldPassword = new String(Arrays.copyOfRange(concatPassData, 0, 20), "UTF8").trim();
						newPassword = new String(Arrays.copyOfRange(concatPassData, 20, 42), "UTF8").trim();
					} catch (Exception ex1) {
						//LOGGER.info("error parsing passwords in change password block");
						errored = true;
						ex1.printStackTrace();
					}

					if (!errored) {
						sec.passwords = new String[]{ oldPassword, newPassword };
					}
				} else if (opcode == OpcodeIn.SET_RECOVERY) {
					// Get the 5 recovery answers
					int blockLen;
					byte[] decBlock; // current decrypted block
					int session = -1; // TODO: should be players stored TCP session to check if request should be processed
					int receivedSession;
					boolean errored = false;
					int questLen = 0;
					int answerLen = 0;
					int expBlocks = 0;
					byte[] answerData;
					String questions[] = new String[5];
					String answers[] = new String[5];
					for (int i = 0; i < 5; i++) {
						questLen = packet.readUnsignedByte();
						questions[i] = new String(packet.readBytes(questLen));
						answerLen = packet.readUnsignedByte();
						// Get encrypted block for answers
						expBlocks = (int)Math.ceil(answerLen / 7.0);
						answerData = new byte[expBlocks * 7];
						for (int j = 0; j < expBlocks; j++) {
							blockLen = packet.readUnsignedByte();
							decBlock = Crypto.decryptRSA(packet.readBytes(blockLen), 0, blockLen);
							// TODO: there are ignored nonces at the beginning of the decrypted block
							receivedSession = ByteBuffer.wrap(Arrays.copyOfRange(decBlock, 4, 8)).getInt();
							// decrypted packet must be of length 15
							if (session == -1 && decBlock.length == 15) {
								session = receivedSession;
							} else if (session != receivedSession || decBlock.length != 15) {
								errored = true; // decryption error occurred
							}

							if (!errored) {
								System.arraycopy(decBlock, 8, answerData, j * 7, 7);
							}
						}

						try {
							answers[i] = new String(answerData, "UTF8").trim();
						} catch (Exception ex) {
							//LOGGER.info("error parsing answer " + i + " in change recovery block");
							errored = true;
							ex.printStackTrace();
						}
					}

					if (!errored) {
						sec.questions = questions.clone();
						sec.answers = answers.clone();
					}
				} else if (opcode == OpcodeIn.SET_DETAILS) {
					boolean errored = false;
					int expLen = 0;
					String details[] = new String[4];
					for (int i = 0; i < 4; i++) {
						expLen = packet.readUnsignedByte();
						details[i] = new String(packet.readBytes(expLen));
						if (details[i].length() != expLen) errored = true;
					}

					if (!errored) {
						sec.details = details.clone();
					}
				}
				result = sec;
				break;

			case REPORT_ABUSE:
				ReportStruct r = new ReportStruct();
				r.targetPlayerName = packet.readZeroPaddedString();
				r.reason = packet.readByte();
				r.suggestsOrMutes = packet.readByte();
				result = r;
				break;

			case SLEEPWORD_ENTERED:
				SleepStruct sl = new SleepStruct();
				sl.sleepDelay = packet.readUnsignedByte();
				sl.sleepWord = packet.readZeroPaddedString();
				result = sl;
				break;

			case HEARTBEAT:
			case SKIP_TUTORIAL:
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

	// a basic check is done on authentic opcodes against their possible lengths
	public static boolean isPossiblyValid(int opcode, int length, int protocolVer) {
		// TODO: remove this if checking valid for other protocol vers is implemented e.g. 127
		if (protocolVer < 127 || (protocolVer > 175 && protocolVer != 235)) {
			return true;
		}
		int payloadLength = length - 1; // subtract off opcode length.

		if (protocolVer <= 175) {
			switch (opcode) {
				// CHANGE_RECOVERY_REQUEST
				case 197:
					return payloadLength == 0;
				// CHANGE_DETAILS_REQUEST
				case 247:
					return payloadLength == 0;
				// CHANGE_PASS
				case 25:
					return payloadLength > 0;
				// SET_RECOVERY
				case 208:
					return payloadLength >= 15; // 5 sets of at least 3 for question-answer
				// SET_DETAILS
				case 253:
					return payloadLength >= 8; // 4 sets of at least 2 per each
				// CANCEL_RECOVERY_REQUEST
				case 196:
					return payloadLength == 0;

				// Unknown OPCODE
				default:
					System.out.println(String.format("Received inauthentic opcode %d from authentic claiming client", opcode));
					return false;
			}
		}
		if (protocolVer == 235) {
			switch (opcode) {
				// HEARTBEAT
				case 67:
					return payloadLength == 0;
				// WALK_TO_ENTITY
				case 16:
					return payloadLength >= 4;
				// WALK_TO_POINT
				case 187:
					return payloadLength >= 4;
				// CONFIRM_LOGOUT
				case 31:
					return payloadLength == 0;
				// LOGOUT
				case 102:
					return payloadLength == 0;
				// ADMIN_TELEPORT
				case 59:
					return payloadLength == 4;
				// COMBAT_STYLE_CHANGE
				case 29:
					return payloadLength == 1;
				// QUESTION_DIALOG_ANSWER
				case 116:
					return payloadLength == 1;

				// PLAYER-APPEARANCE_CHANGE
				case 235:
					return payloadLength == 8;
				// SOCIAL_ADD_IGNORE
				case 132:
					return payloadLength >= 3 && payloadLength <=22;
				// SOCIAL_ADD_FRIEND
				case 195:
					return payloadLength >= 3 && payloadLength <=22;
				// SOCIAL_SEND_PRIVATE_MESSAGE
				case 218:
					return payloadLength >= 6;
				// SOCIAL_REMOVE_FRIEND
				case 167:
					return payloadLength >= 3 && payloadLength <=22;
				// SOCIAL_REMOVE_IGNORE
				case 241:
					return payloadLength >= 3 && payloadLength <=22;

				// DUEL_FIRST_SETTINGS_CHANGED
				case 8:
					return payloadLength == 4;
				// DUEL_FIRST_ACCEPTED
				case 176:
					return payloadLength == 0;
				// DUEL_DECLINED
				case 197:
					return payloadLength == 0;
				// DUEL_OFFER_ITEM
				case 33:
					return payloadLength >= 1;
				// DUEL_SECOND_ACCEPTED
				case 77:
					return payloadLength == 0;

				// INTERACT_WITH_BOUNDARY
				case 14:
					return payloadLength == 5;
				// INTERACT_WITH_BOUNDARY2
				case 127:
					return payloadLength == 5;
				// CAST_ON_BOUNDARY
				case 180:
					return payloadLength == 7;
				// USE_WITH_BOUNDARY
				case 161:
					return payloadLength == 7;

				// NPC_TALK_TO
				case 153:
					return payloadLength == 2;
				// NPC_COMMAND1
				case 202:
					return payloadLength == 2;
				// NPC_ATTACK1
				case 190:
					return payloadLength == 2;
				// CAST_ON_NPC
				case 50:
					return payloadLength == 4;
				// NPC_USE_ITEM
				case 135:
					return payloadLength == 4;

				// PLAYER_CAST_PVP
				case 229:
					return payloadLength == 4;
				// PLAYER_USE_ITEM
				case 113:
					return payloadLength == 4;
				// PLAYER_ATTACK
				case 171:
					return payloadLength == 2;
				// PLAYER_DUEL
				case 103:
					return payloadLength == 2;
				// PLAYER_INIT_TRADE_REQUEST
				case 142:
					return payloadLength == 2;
				// PLAYER_FOLLOW
				case 165:
					return payloadLength == 2;

				// CAST_ON_GROUND_ITEM
				case 249:
					return payloadLength == 8;
				// GROUND_ITEM_USE_ITEM
				case 53:
					return payloadLength == 8;
				// GROUND_ITEM_TAKE
				case 247:
					return payloadLength == 6;

				// CAST_ON_INVENTORY_ITEM
				case 4:
					return payloadLength == 4;
				// ITEM_USE_ITEM
				case 91:
					return payloadLength == 4;
				// ITEM_UNEQUIP_FROM_INVENTORY
				case 170:
					return payloadLength == 2;
				// ITEM_EQUIP_FROM_INVENTORY
				case 169:
					return payloadLength == 2;
				// ITEM_COMMAND
				case 90:
					return payloadLength == 2;
				// ITEM_DROP
				case 246:
					return payloadLength == 2;

				// CAST_ON_SELF
				case 137:
					return payloadLength == 2;
				// CAST_ON_LAND
				case 158:
					return payloadLength == 6;

				// OBJECT_COMMAND1
				case 136:
					return payloadLength == 4;
				// OBJECT_COMMAND2
				case 79:
					return payloadLength == 4;
				// CAST_ON_SCENERY
				case 99:
					return payloadLength == 6;
				// USE_ITEM_ON_SCENERY
				case 115:
					return payloadLength == 6;

				// SHOP_CLOSE
				case 166:
					return payloadLength == 0;
				// SHOP_BUY
				case 236:
					return payloadLength == 6;
				// SHOP_SELL
				case 221:
					return payloadLength == 6;

				// PLAYER_ACCEPTED_INIT_TRADE_REQUEST
				case 55:
					return payloadLength == 0;
				// PLAYER_DECLINED_TRADE
				case 230:
					return payloadLength == 0;
				// PLAYER_ADDED_ITEMS_TO_TRADE_OFFER
				case 46:
					return payloadLength >= 1;
				// PLAYER_ACCEPTED_TRADE
				case 104:
					return payloadLength == 0;

				// PRAYER_ACTIVATED
				case 60:
					return payloadLength == 1;
				// PRAYER_DEACTIVATED
				case 254:
					return payloadLength == 1;

				// GAME_SETTINGS_CHANGED
				case 111:
					return payloadLength == 2;
				// CHAT_MESSAGE
				case 216:
					return payloadLength >= 2;
				// COMMAND
				case 38:
					return payloadLength >= 2;
				// PRIVACY_SETTINGS_CHANGED
				case 64:
					return payloadLength == 4;
				// REPORT_ABUSE
				case 206:
					return payloadLength >= 5 && payloadLength <= 24;
				// BANK_CLOSE
				case 212:
					return payloadLength == 0;
				// BANK_WITHDRAW
				case 22:
					return payloadLength == 10;
				// BANK_DEPOSIT
				case 23:
					return payloadLength == 10;

				// SLEEPWORD_ENTERED
				case 45:
					return payloadLength >= 3;

				// SKIP_TUTORIAL
				case 84:
					return payloadLength == 0;

				// Unknown OPCODE
				default:
					System.out.println(String.format("Received inauthentic opcode %d from authentic claiming client", opcode));
					return false;
			}
		}
		return false;
	}
}
