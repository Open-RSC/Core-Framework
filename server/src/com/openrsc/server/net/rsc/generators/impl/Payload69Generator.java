package com.openrsc.server.net.rsc.generators.impl;

import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.external.ItemLoc;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.PacketBuilder;
import com.openrsc.server.net.rsc.PayloadValidator;
import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.generators.PayloadGenerator;
import com.openrsc.server.net.rsc.struct.AbstractStruct;
import com.openrsc.server.net.rsc.struct.outgoing.*;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MathUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * RSC Protocol-69 Generator for Outgoing Packets from respective Protocol Independent Structs
 * **/
public class Payload69Generator implements PayloadGenerator<OpcodeOut> {
	private static final Map<OpcodeOut, Integer> opcodeMap = new HashMap<OpcodeOut, Integer>() {{
		put(OpcodeOut.SEND_SERVER_MESSAGE, 8);
		put(OpcodeOut.SEND_FRIEND_LIST, 23);
		put(OpcodeOut.SEND_FRIEND_UPDATE, 24);
		put(OpcodeOut.SEND_IGNORE_LIST, 26);
		put(OpcodeOut.SEND_PRIVACY_SETTINGS, 27);
		put(OpcodeOut.SEND_PRIVATE_MESSAGE, 28);
		put(OpcodeOut.SEND_INVENTORY_SIZE, 223);
		put(OpcodeOut.SEND_YOPTIN, 225);
		put(OpcodeOut.SEND_GAME_SETTINGS, 228);
		put(OpcodeOut.SEND_TRADE_ACCEPTED, 229);
		put(OpcodeOut.RUNESCAPE_UPDATED, 230); // won't be necessary to be used
		put(OpcodeOut.SEND_REMOVE_WORLD_NPC, 231);
		put(OpcodeOut.SEND_REMOVE_WORLD_PLAYER, 232);
		put(OpcodeOut.SEND_SHOP_CLOSE, 233);
		put(OpcodeOut.SEND_SHOP_OPEN, 234);
		put(OpcodeOut.SEND_TRADE_OTHER_ACCEPTED, 235);
		put(OpcodeOut.SEND_TRADE_OTHER_ITEMS, 236);
		put(OpcodeOut.SEND_TRADE_CLOSE, 237);
		put(OpcodeOut.SEND_TRADE_WINDOW, 238);
		put(OpcodeOut.SEND_APPEARANCE_SCREEN, 239);
		put(OpcodeOut.SEND_REMOVE_WORLD_ENTITY, 240);
		put(OpcodeOut.SEND_DEATH, 241);
		put(OpcodeOut.SEND_EQUIPMENT_STATS, 242);
		put(OpcodeOut.SEND_STATS, 243);
		put(OpcodeOut.SEND_WORLD_INFO, 244);
		put(OpcodeOut.SEND_OPTIONS_MENU_CLOSE, 245);
		put(OpcodeOut.SEND_OPTIONS_MENU_OPEN, 246);
		put(OpcodeOut.SEND_UPDATE_NPC, 247);
		put(OpcodeOut.SEND_NPC_COORDS, 248);
		put(OpcodeOut.SEND_BOUNDARY_HANDLER, 249);
		put(OpcodeOut.SEND_UPDATE_PLAYERS, 250);
		put(OpcodeOut.SEND_UPDATE_PLAYERS_RETRO, 251);
		put(OpcodeOut.SEND_INVENTORY, 252);
		put(OpcodeOut.SEND_SCENERY_HANDLER, 253);
		put(OpcodeOut.SEND_GROUND_ITEM_HANDLER, 254);
		put(OpcodeOut.SEND_PLAYER_COORDS, 255);
	}};

	@Override
	public PacketBuilder fromOpcodeEnum(OpcodeOut opcode, Player player) {
		PacketBuilder builder = null;
		Integer opcodeNum = opcodeMap.getOrDefault(opcode, null);
		if (opcodeNum != null) {
			builder = new PacketBuilder().setID(opcodeNum);
		}
		return builder;
	}

	@Override
	public Packet generate(AbstractStruct<OpcodeOut> payload, Player player) {

		PacketBuilder builder = fromOpcodeEnum(payload.getOpcode(), player);
		boolean possiblyValid = PayloadValidator.isPayloadCorrectInstance(payload, payload.getOpcode());

		if (builder != null && possiblyValid) {
			switch (payload.getOpcode()) {
				// no payload opcodes
				case SEND_APPEARANCE_SCREEN:
				case SEND_DEATH:
				case SEND_TRADE_CLOSE:
				case SEND_SHOP_CLOSE:
				case SEND_OPTIONS_MENU_CLOSE:
				case SEND_YOPTIN:
					break;

				case RUNESCAPE_UPDATED:
					builder.writeByte((byte) 28); // verConfig
					builder.writeByte((byte) 20); // verMaps
					builder.writeByte((byte) 18); // verMedia
					builder.writeByte((byte) 7); // verModels
					builder.writeByte((byte) 6); // verTextures
					builder.writeByte((byte) 5); // verEntity
					break;

				case SEND_OPTIONS_MENU_OPEN:
					MenuOptionStruct mo = (MenuOptionStruct) payload;
					int numOptions = Math.min(mo.numOptions, 5);
					builder.writeByte((byte) numOptions);
					for (int i = 0; i < 5 && i < numOptions; i++){
						int optionLength = mo.optionTexts[i].length();
						builder.writeByte((byte) optionLength);
						builder.writeNonTerminatedString(mo.optionTexts[i]);
					}
					break;

				case SEND_STATS:
					StatInfoStruct si = (StatInfoStruct) payload;
					// 16 skills - current level
					builder.writeByte((byte) si.currentAttack);
					builder.writeByte((byte) si.currentDefense);
					builder.writeByte((byte) si.currentStrength);
					builder.writeByte((byte) si.currentHits);
					builder.writeByte((byte) si.currentRanged);
					builder.writeByte((byte) si.currentPrayer);
					builder.writeByte((byte) si.currentMagic);
					builder.writeByte((byte) si.currentCooking);
					builder.writeByte((byte) si.currentWoodcutting);
					builder.writeByte((byte) si.currentFletching);
					builder.writeByte((byte) si.currentFishing);
					builder.writeByte((byte) si.currentFiremaking);
					builder.writeByte((byte) si.currentCrafting);
					builder.writeByte((byte) si.currentSmithing);
					builder.writeByte((byte) si.currentMining);
					builder.writeByte((byte) si.currentHerblaw);

					// 16 skills - max level
					builder.writeByte((byte) si.maxAttack);
					builder.writeByte((byte) si.maxDefense);
					builder.writeByte((byte) si.maxStrength);
					builder.writeByte((byte) si.maxHits);
					builder.writeByte((byte) si.maxRanged);
					builder.writeByte((byte) si.maxPrayer);
					builder.writeByte((byte) si.maxMagic);
					builder.writeByte((byte) si.maxCooking);
					builder.writeByte((byte) si.maxWoodcutting);
					builder.writeByte((byte) si.maxFletching);
					builder.writeByte((byte) si.maxFishing);
					builder.writeByte((byte) si.maxFiremaking);
					builder.writeByte((byte) si.maxCrafting);
					builder.writeByte((byte) si.maxSmithing);
					builder.writeByte((byte) si.maxMining);
					builder.writeByte((byte) si.maxHerblaw);

					builder.writeByte((byte) si.questPoints);
					break;

				case SEND_EQUIPMENT_STATS:
					EquipmentStatsStruct es = (EquipmentStatsStruct) payload;
					builder.writeByte((byte) es.armourPoints);
					builder.writeByte((byte) es.weaponAimPoints);
					builder.writeByte((byte) es.weaponPowerPoints);
					builder.writeByte((byte) es.magicPoints);
					builder.writeByte((byte) es.prayerPoints);
					break;

				case SEND_GAME_SETTINGS:
					GameSettingsStruct gs = (GameSettingsStruct) payload;
					builder.writeByte((byte) gs.playerKiller);
					builder.writeByte((byte) gs.cameraModeAuto);
					builder.writeByte((byte) gs.pkChangesLeft);
					builder.writeByte((byte) gs.mouseButtonOne);
					break;

				case SEND_PRIVACY_SETTINGS:
					PrivacySettingsStruct prs = (PrivacySettingsStruct) payload;
					builder.writeByte((byte) prs.hideStatus);
					builder.writeByte((byte) prs.blockChat);
					builder.writeByte((byte) prs.blockPrivate);
					builder.writeByte((byte) prs.blockTrade);
					builder.writeByte((byte) 0); // not implemented for mc38
					break;

				case SEND_TRADE_WINDOW:
					TradeShowWindowStruct tsw = (TradeShowWindowStruct) payload;
					builder.writeShort(tsw.serverIndex);
					break;

				case SEND_TRADE_ACCEPTED:
				case SEND_TRADE_OTHER_ACCEPTED:
					TradeAcceptStruct ta = (TradeAcceptStruct) payload;
					builder.writeByte((byte) ta.accepted);
					break;

				case SEND_TRADE_OTHER_ITEMS:
					TradeTransactionStruct tt = (TradeTransactionStruct) payload;
					int tradeCount = tt.opponentTradeCount;
					builder.writeByte((byte) tradeCount);
					for (int i = 0; i < tradeCount; i++) {
						builder.writeShort(tt.opponentCatalogIDs[i]);
						builder.writeShort(tt.opponentAmounts[i] & 0xffff);
					}
					break;

				case SEND_FRIEND_LIST:
					FriendListStruct fl = (FriendListStruct) payload;
					int friendSize = fl.listSize;
					builder.writeByte((byte) friendSize);
					for (int i = 0; i < friendSize; i++) {
						builder.writeLong(DataConversions.usernameToHash(fl.name[i]));
						builder.writeByte((byte) onlineStatusConverter(fl.onlineStatus[i]));
					}
					break;

				case SEND_FRIEND_UPDATE:
					FriendUpdateStruct fr = (FriendUpdateStruct) payload;
					builder.writeLong(DataConversions.usernameToHash(fr.name));
					builder.writeByte((byte) onlineStatusConverter(fr.onlineStatus));
					break;

				case SEND_IGNORE_LIST:
					IgnoreListStruct il = (IgnoreListStruct) payload;
					int ignoreSize = il.listSize;
					builder.writeByte((byte) ignoreSize);
					for (int i = 0; i < ignoreSize; i++) {
						builder.writeLong(DataConversions.usernameToHash(il.name[i]));
					}
					break;

				case SEND_INVENTORY: {
					InventoryStruct is = (InventoryStruct) payload;

					for (int i = 0; i < 35; i++) {
						ItemDefinition curItem = (new Item(i)).getDef(player.getWorld());
						builder.writeBits(i, 10);
						if (curItem.isWieldable()) {
							builder.writeBits(0, 1);
						}
						if (curItem.isStackable()) {
							builder.writeBits(100, 16);
						}
					}
					/*
					for (int i = 0; i < is.inventorySize; i++) {
						ItemDefinition curItem = (new Item(is.catalogIDs[i])).getDef(player.getWorld());
						builder.writeBits(is.catalogIDs[i], 10);
						if (curItem.isWieldable()) {
							builder.writeBits(is.wielded[i], 1);
						}
						if (curItem.isStackable()) {
							builder.writeBits(is.amount[i], 16);
						}
					}

					 */
					break;
				}

				case SEND_INVENTORY_SIZE: {
					InventoryStruct is = (InventoryStruct) payload;
					int inventorySize = is.inventorySize;
					builder.writeByte((byte)inventorySize);
					break;
				}

				case SEND_SHOP_OPEN:
					ShopStruct s = (ShopStruct) payload;
					int shopSize = s.itemsStockSize;
					builder.writeByte((byte) shopSize);
					builder.writeByte((byte) s.isGeneralStore);
					builder.writeByte((byte) s.sellModifier);
					builder.writeByte((byte) s.buyModifier);
					for (int i = 0; i < shopSize; i++) {
						builder.writeShort(s.catalogIDs[i]);
						builder.writeShort(s.amount[i]);
						builder.writeByte(MathUtil.boundedNumber(s.baseAmount[i] - s.amount[i], -127, 127));
					}
					break;

				case SEND_SERVER_MESSAGE:
					MessageStruct m = (MessageStruct) payload;
					builder.writeNonTerminatedString(m.message);
					break;

				case SEND_PRIVATE_MESSAGE:
					PrivateMessageStruct pm = (PrivateMessageStruct) payload;
					builder.writeLong(DataConversions.usernameToHash(pm.playerName));
					builder.writeNonTerminatedString(pm.message);
					break;

				case SEND_WORLD_INFO:
					WorldInfoStruct wi = (WorldInfoStruct) payload;
					builder.writeShort(wi.serverIndex);
					builder.writeShort(wi.planeWidth);
					builder.writeShort(wi.planeHeight);
					builder.writeShort(wi.planeFloor);
					builder.writeShort(wi.distanceBetweenFloors);
					break;

				case SEND_NPC_COORDS:
				case SEND_PLAYER_COORDS:
					// TODO: CHECK IMPL
					MobsUpdateStruct mu = (MobsUpdateStruct) payload;
					builder.startBitAccess();
					for (Map.Entry<Integer, Integer> entry : mu.mobs) {
						builder.writeBits(entry.getKey(), entry.getValue());
					}
					builder.finishBitAccess();
					break;

				case SEND_UPDATE_NPC: //VERIFIED
				case SEND_UPDATE_PLAYERS: //TYPE 1 changes!
				case SEND_UPDATE_PLAYERS_RETRO: //TYPE 5
					AppearanceUpdateStruct au = (AppearanceUpdateStruct) payload;
					for (Object entry : au.info) {
						if (entry instanceof Byte) {
							builder.writeByte((Byte) entry);
						} else if (entry instanceof Short) {
							builder.writeShort((Short) entry);
						} else if (entry instanceof Character) { // wrapper class for appearance byte
							int value = (Character) entry;
							builder.writeAppearanceByte((byte) value, 69);
						} else if (entry instanceof String) {
							builder.writeNonTerminatedString((String) entry);
						} else if (entry instanceof Long) {
							builder.writeLong((long) entry);
						}
					}
					break;

				case SEND_SCENERY_HANDLER:
					GameObjectsUpdateStruct go = (GameObjectsUpdateStruct) payload;
					for (GameObjectLoc objectLoc : go.objects) {
						if (objectLoc.getId() > 179 && objectLoc.getId() != 60000) {
							// TODO: may want to define some pattern
							continue;
						}
						builder.writeShort(objectLoc.getId());
						builder.writeByte((byte) objectLoc.getX());
						builder.writeByte((byte) objectLoc.getY());
					}
					break;

				case SEND_BOUNDARY_HANDLER:
					GameObjectsUpdateStruct go1 = (GameObjectsUpdateStruct) payload;
					for (GameObjectLoc objectLoc : go1.objects) {
						if (objectLoc.getId() > 46 && objectLoc.getId() != 60000) {
							// TODO: may want to define some pattern
							continue;
						}
						builder.writeShort(objectLoc.getId());
						builder.writeByte((byte) objectLoc.getX());
						builder.writeByte((byte) objectLoc.getY());
						builder.writeByte((byte) objectLoc.getDirection());
					}
					break;

				case SEND_GROUND_ITEM_HANDLER:
					GroundItemsUpdateStruct gri = (GroundItemsUpdateStruct) payload;
					for (ItemLoc it : gri.objects) {
						if ((it.getId() & 0x7FFF) > 306) {
							// TODO: may want to define some pattern
							continue;
						}
						if (it.respawnTime == -1) {
							builder.writeByte((byte) 255);
						} else {
							builder.writeShort(it.getId());
						}
						builder.writeByte((byte) it.getX());
						builder.writeByte((byte) it.getY());
					}
					break;

				case SEND_REMOVE_WORLD_ENTITY:
					ClearLocationsStruct cl = (ClearLocationsStruct) payload;
					for (Point point : cl.points) {
						builder.writeShort(point.getX());
						builder.writeShort(point.getY());
					}
					break;

				case SEND_REMOVE_WORLD_NPC:
				case SEND_REMOVE_WORLD_PLAYER:
					ClearMobsStruct cm = (ClearMobsStruct) payload;
					for (Integer index : cm.indices) {
						builder.writeShort(index);
					}
					break;
			}
		}

		return builder != null ? builder.toPacket() : null;
	}

	public static int onlineStatusConverter(int modernStatus) {
		int onlineStatus = 0;
		switch ((modernStatus & 0x6) >> 1) {
			case 3:
				// online and same world
				onlineStatus = 2;
				break;
			case 2:
				// online but different world;
				onlineStatus = 1;
				break;
			case 1:
			case 0:
				onlineStatus = 0;
				break;
		}
		return onlineStatus;
	}

	private byte[] make177ChatMessage(String messageToSend) {
		byte[] pmMessage = new byte[100];
		char[] characterDictionary = new char[] { ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '£', '$', '%', '\"', '[', ']' };
		if (messageToSend.length() > 80) {
			messageToSend = messageToSend.substring(0, 80);
		}

		messageToSend = messageToSend.toLowerCase();
		int messageLength = 0;
		int aFlag = -1;
		for (int messageIdx = 0; messageIdx < messageToSend.length(); messageIdx++) {
			char curChar = messageToSend.charAt(messageIdx);
			int charIdx = 0;
			for (int i = 0; i < characterDictionary.length; i++) {
				if (curChar == characterDictionary[i]) {
					charIdx = i;
					break;
				}
			}

			if (charIdx > 12) {
				charIdx += 195;
			}

			if (aFlag == -1) {
				if (charIdx < 13) {
					aFlag = charIdx;
				} else {
					pmMessage[messageLength++] = (byte) charIdx;
				}
			} else {
				if (charIdx < 13) {
					pmMessage[messageLength++] = (byte) ((aFlag << 4) + charIdx);
					aFlag = -1;
				} else {
					pmMessage[messageLength++] = (byte) ((aFlag << 4) + (charIdx >> 4));
					aFlag = charIdx & 15;
				}
			}
		}

		if (aFlag != -1) {
			pmMessage[messageLength++] = (byte)(aFlag << 4);
		}

		return Arrays.copyOf(pmMessage, messageLength);
	}
}
