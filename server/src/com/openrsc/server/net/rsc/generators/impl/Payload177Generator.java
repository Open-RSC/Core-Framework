package com.openrsc.server.net.rsc.generators.impl;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.external.ItemLoc;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.RSCString;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.PacketBuilder;
import com.openrsc.server.net.rsc.GameNetworkException;
import com.openrsc.server.net.rsc.PayloadValidator;
import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.generators.PayloadGenerator;
import com.openrsc.server.net.rsc.struct.AbstractStruct;
import com.openrsc.server.net.rsc.struct.outgoing.*;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MathUtil;
import com.openrsc.server.util.rsc.StringUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * RSC Protocol-177 Generator for Outgoing Packets from respective Protocol Independent Structs
 * **/
public class Payload177Generator implements PayloadGenerator<OpcodeOut> {
	private static final Map<OpcodeOut, Integer> opcodeMap = new HashMap<OpcodeOut, Integer>() {{
		put(OpcodeOut.SEND_PLAYER_COORDS, 255);
		put(OpcodeOut.SEND_GROUND_ITEM_HANDLER, 254);
		put(OpcodeOut.SEND_SCENERY_HANDLER, 253);
		put(OpcodeOut.SEND_INVENTORY, 252);
		// SEND_UPDATE_PLAYERS_RETRO (251) would later be replaced by the In Tutorial opcode it seems...
		put(OpcodeOut.SEND_UPDATE_PLAYERS, 250);
		put(OpcodeOut.SEND_BOUNDARY_HANDLER, 249);
		put(OpcodeOut.SEND_NPC_COORDS, 248);
		put(OpcodeOut.SEND_UPDATE_NPC, 247);
		put(OpcodeOut.SEND_OPTIONS_MENU_OPEN, 246);
		put(OpcodeOut.SEND_OPTIONS_MENU_CLOSE, 245);
		put(OpcodeOut.SEND_WORLD_INFO, 244);
		put(OpcodeOut.SEND_STATS, 243);
		put(OpcodeOut.SEND_EQUIPMENT_STATS, 242);
		put(OpcodeOut.SEND_DEATH, 241);
		put(OpcodeOut.SEND_REMOVE_WORLD_ENTITY, 240);
		put(OpcodeOut.SEND_APPEARANCE_SCREEN, 239);
		put(OpcodeOut.SEND_TRADE_WINDOW, 238);
		put(OpcodeOut.SEND_TRADE_CLOSE, 237);
		put(OpcodeOut.SEND_TRADE_OTHER_ITEMS, 236);
		put(OpcodeOut.SEND_TRADE_OTHER_ACCEPTED, 235);
		put(OpcodeOut.SEND_SHOP_OPEN, 234);
		put(OpcodeOut.SEND_SHOP_CLOSE, 233);
		put(OpcodeOut.SEND_OPEN_DETAILS, 232); // replaced SEND_REMOVE_WORLD_PLAYER
		// 231 SEND_REMOVE_WORLD_NPC gone
		// 230 RUNESCAPE_UPDATED gone
		put(OpcodeOut.SEND_TRADE_ACCEPTED, 229);
		put(OpcodeOut.SEND_GAME_SETTINGS, 228);
		put(OpcodeOut.SEND_PRAYERS_ACTIVE, 227);
		put(OpcodeOut.SEND_QUESTS, 226);
		// 225 missing, dunno what it would have been
		put(OpcodeOut.SEND_OPEN_RECOVERY, 224);
		// 223 missing
		put(OpcodeOut.SEND_BANK_OPEN, 222);
		put(OpcodeOut.SEND_BANK_CLOSE, 221);
		put(OpcodeOut.SEND_EXPERIENCE, 220);
		put(OpcodeOut.SEND_DUEL_WINDOW, 219);
		put(OpcodeOut.SEND_DUEL_CLOSE, 218);
		put(OpcodeOut.SEND_TRADE_OPEN_CONFIRM, 217);
		put(OpcodeOut.SEND_DUEL_OPPONENTS_ITEMS, 216);
		put(OpcodeOut.SEND_DUEL_SETTINGS, 215);
		put(OpcodeOut.SEND_BANK_UPDATE, 214);
		put(OpcodeOut.SEND_INVENTORY_UPDATEITEM, 213);
		put(OpcodeOut.SEND_INVENTORY_REMOVE_ITEM, 212);
		put(OpcodeOut.SEND_STAT, 211);
		put(OpcodeOut.SEND_DUEL_OTHER_ACCEPTED, 210);
		put(OpcodeOut.SEND_DUEL_ACCEPTED, 209);
		put(OpcodeOut.SEND_DUEL_CONFIRMWINDOW, 208);
		put(OpcodeOut.SEND_PLAY_SOUND, 207);
		put(OpcodeOut.SEND_BUBBLE, 206); // used for teleport, telegrab, and iban's magic
		put(OpcodeOut.SEND_WELCOME_INFO, 205);
		put(OpcodeOut.SEND_BOX2, 204);
		put(OpcodeOut.SEND_BOX, 203);
		put(OpcodeOut.SEND_FATIGUE, 202);
		put(OpcodeOut.SEND_SLEEPSCREEN, 201);
		put(OpcodeOut.SEND_SLEEP_FATIGUE, 200);
		put(OpcodeOut.SEND_STOPSLEEP, 199);
		put(OpcodeOut.SEND_SLEEPWORD_INCORRECT, 198);
		// put(OpcodeOut.SEND_SYSTEM_UPDATE, 197); // Doesn't exist?
		// put(OpcodeOut.SEND_APPEARANCE_KEEPALIVE, 196); // also does not exist ofc
		/*
		put(OpcodeOut.SEND_PRIVATE_MESSAGE_SENT, 87);
		put(OpcodeOut.SEND_ON_TUTORIAL, 111);
		put(OpcodeOut.SEND_LOGOUT, 165);
		put(OpcodeOut.SEND_28_BYTES_UNUSED, 189);
		put(OpcodeOut.SEND_UPDATE_IGNORE_LIST_BECAUSE_NAME_CHANGE, 237);
		*/

		// found in a.a.b.a(int,int);
		put(OpcodeOut.SEND_SERVER_MESSAGE, 8);
		put(OpcodeOut.SEND_LOGOUT_REQUEST_CONFIRM, 9); //sends opcode 1, 325
		put(OpcodeOut.SEND_CANT_LOGOUT, 10);
		put(OpcodeOut.SEND_FRIEND_LIST, 23);
		put(OpcodeOut.SEND_FRIEND_UPDATE, 24);
		put(OpcodeOut.SEND_IGNORE_LIST, 26);
		put(OpcodeOut.SEND_PRIVACY_SETTINGS, 27);
		put(OpcodeOut.SEND_PRIVATE_MESSAGE, 28);
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
				case SEND_LOGOUT:
				case SEND_LOGOUT_REQUEST_CONFIRM:
				case SEND_CANT_LOGOUT:
				case SEND_APPEARANCE_SCREEN:
				case SEND_APPEARANCE_KEEPALIVE:
				case SEND_OPEN_RECOVERY:
				case SEND_OPEN_DETAILS:
				case SEND_DEATH:
				case SEND_SLEEPWORD_INCORRECT:
				case SEND_STOPSLEEP:
				case SEND_TRADE_CLOSE:
				case SEND_DUEL_CLOSE:
				case SEND_BANK_CLOSE:
				case SEND_SHOP_CLOSE:
				case SEND_OPTIONS_MENU_CLOSE:
					break;

				case SEND_BOX:
				case SEND_BOX2:
					MessageBoxStruct mb = (MessageBoxStruct) payload;
					String message = mb.message;
					if (message.length() > 4975) {
						message = message.substring(0, 4975);
					}
					builder.writeNonTerminatedString(message);
					break;

				case SEND_OPTIONS_MENU_OPEN:
					MenuOptionStruct mo = (MenuOptionStruct) payload;
					int numOptions = Math.min(mo.numOptions, 5);
					builder.writeByte((byte) numOptions);
					for (int i = 0; i < 5 && i < numOptions; i++){
						builder.writeByte(mo.optionTexts[i].length());
						builder.writeNonTerminatedString(mo.optionTexts[i]);
					}
					break;

				case SEND_STATS:
					StatInfoStruct si = (StatInfoStruct) payload;
					// 18 skills - current level
					builder.writeByte((byte) si.currentAttack);
					builder.writeByte((byte) si.currentDefense);
					builder.writeByte((byte) si.currentStrength);
					builder.writeByte((byte) si.currentHits);
					builder.writeByte((byte) si.currentRanged);
					builder.writeByte((byte) si.getCurrentPrayer());
					builder.writeByte((byte) si.getCurrentMagic());
					builder.writeByte((byte) si.currentCooking);
					builder.writeByte((byte) si.currentWoodcutting);
					builder.writeByte((byte) si.currentFletching);
					builder.writeByte((byte) si.currentFishing);
					builder.writeByte((byte) si.currentFiremaking);
					builder.writeByte((byte) si.currentCrafting);
					builder.writeByte((byte) si.currentSmithing);
					builder.writeByte((byte) si.currentMining);
					builder.writeByte((byte) si.currentHerblaw);
					builder.writeByte((byte) si.currentAgility);
					builder.writeByte((byte) si.currentThieving);

					// 18 skills - max level
					builder.writeByte((byte) si.maxAttack);
					builder.writeByte((byte) si.maxDefense);
					builder.writeByte((byte) si.maxStrength);
					builder.writeByte((byte) si.maxHits);
					builder.writeByte((byte) si.maxRanged);
					builder.writeByte((byte) si.getMaxPrayer());
					builder.writeByte((byte) si.getMaxMagic());
					builder.writeByte((byte) si.maxCooking);
					builder.writeByte((byte) si.maxWoodcutting);
					builder.writeByte((byte) si.maxFletching);
					builder.writeByte((byte) si.maxFishing);
					builder.writeByte((byte) si.maxFiremaking);
					builder.writeByte((byte) si.maxCrafting);
					builder.writeByte((byte) si.maxSmithing);
					builder.writeByte((byte) si.maxMining);
					builder.writeByte((byte) si.maxHerblaw);
					builder.writeByte((byte) si.maxAgility);
					builder.writeByte((byte) si.maxThieving);

					// 18 skills - experiences
					builder.writeInt(si.experienceAttack);
					builder.writeInt(si.experienceDefense);
					builder.writeInt(si.experienceStrength);
					builder.writeInt(si.experienceHits);
					builder.writeInt(si.experienceRanged);
					builder.writeInt(si.getExperiencePrayer());
					builder.writeInt(si.getExperienceMagic());
					builder.writeInt(si.experienceCooking);
					builder.writeInt(si.experienceWoodcutting);
					builder.writeInt(si.experienceFletching);
					builder.writeInt(si.experienceFishing);
					builder.writeInt(si.experienceFiremaking);
					builder.writeInt(si.experienceCrafting);
					builder.writeInt(si.experienceSmithing);
					builder.writeInt(si.experienceMining);
					builder.writeInt(si.experienceHerblaw);
					builder.writeInt(si.experienceAgility);
					builder.writeInt(si.experienceThieving);

					builder.writeByte((byte) si.questPoints);
					break;

				case SEND_STAT:
					StatUpdateStruct statup = (StatUpdateStruct) payload;
					builder.writeByte((byte) statup.statId);
					builder.writeByte((byte) statup.currentLevel);
					builder.writeByte((byte) statup.maxLevel);
					builder.writeInt(statup.experience);
					break;

				case SEND_EXPERIENCE:
					ExperienceStruct ex = (ExperienceStruct) payload;
					builder.writeByte((byte) ex.statId);
					builder.writeInt(ex.experience);
					break;

				case SEND_EQUIPMENT_STATS:
					EquipmentStatsStruct es = (EquipmentStatsStruct) payload;
					builder.writeByte((byte) es.armourPoints);
					builder.writeByte((byte) es.weaponAimPoints);
					builder.writeByte((byte) es.weaponPowerPoints);
					builder.writeByte((byte) es.magicPoints);
					builder.writeByte((byte) es.prayerPoints);
					break;

				case SEND_QUESTS:
					QuestInfoStruct qi = (QuestInfoStruct) payload;
					for (int i = 0; i < 50; i++) {
						builder.writeByte((byte) qi.questCompleted[i]);
					}
					break;

				case SEND_PRAYERS_ACTIVE:
					PrayersActiveStruct ps =  (PrayersActiveStruct) payload;
					for (int active : ps.prayerActive) {
						builder.writeByte((byte) active);
					}
					break;

				case SEND_FATIGUE:
				case SEND_SLEEP_FATIGUE:
					FatigueStruct fs = (FatigueStruct) payload;
					builder.writeShort(fs.serverFatigue / (player.MAX_FATIGUE / 750));
					break;

				case SEND_PLAY_SOUND:
					PlaySoundStruct pls = (PlaySoundStruct) payload;
					builder.writeNonTerminatedString(pls.soundName);
					break;

				case SEND_BUBBLE:
					TeleBubbleStruct tb = (TeleBubbleStruct) payload;
					builder.writeByte((byte) tb.isGrab);
					builder.writeByte((byte) tb.localPoint.getX());
					builder.writeByte((byte) tb.localPoint.getY());
					break;

				case SEND_GAME_SETTINGS:
					GameSettingsStruct gs = (GameSettingsStruct) payload;
					builder.writeByte((byte) gs.cameraModeAuto);
					builder.writeByte((byte) gs.mouseButtonOne);
					builder.writeByte((byte) gs.soundDisabled);
					break;

				case SEND_PRIVACY_SETTINGS:
					PrivacySettingsStruct prs = (PrivacySettingsStruct) payload;
					builder.writeByte((byte) prs.blockChat);
					builder.writeByte((byte) prs.blockPrivate);
					builder.writeByte((byte) prs.blockTrade);
					builder.writeByte((byte) prs.blockDuel);
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

				case SEND_TRADE_OPEN_CONFIRM:
					TradeConfirmStruct tc = (TradeConfirmStruct) payload;
					int tradedItemSize = tc.opponentTradeCount;

					for (int i = 0; i < tradedItemSize; i++) {
						// validate against any possible item id that are greater than allowed
						// if so fail out
						if (tc.opponentCatalogIDs[i] > ItemId.maxAuthentic) {
							// fail out transaction
							throw new GameNetworkException(tc, "Traded item id is greater than supported in generator", tc.opponentCatalogIDs[i] + "");
						}
					}

					builder.writeLong(DataConversions.usernameToHash(tc.targetPlayer));
					builder.writeByte((byte) tradedItemSize);
					for (int i = 0; i < tradedItemSize; i++) {
						builder.writeShort(tc.opponentCatalogIDs[i]);
						builder.writeInt(tc.opponentAmounts[i]);
					}
					tradedItemSize = tc.myCount;
					builder.writeByte((byte) tradedItemSize);
					for (int i = 0; i < tradedItemSize; i++) {
						builder.writeShort(tc.myCatalogIDs[i]);
						builder.writeInt(tc.myAmounts[i]);
					}
					break;

				case SEND_TRADE_OTHER_ITEMS:
					TradeTransactionStruct tt = (TradeTransactionStruct) payload;
					int tradeCount = tt.opponentTradeCount;
					builder.writeByte((byte) tradeCount);
					for (int i = 0; i < tradeCount; i++) {
						builder.writeShort(tt.opponentCatalogIDs[i]);
						builder.writeInt(tt.opponentAmounts[i]);
					}
					break;

				case SEND_DUEL_WINDOW:
					DuelShowWindowStruct dsw = (DuelShowWindowStruct) payload;
					builder.writeShort(dsw.serverIndex);
					break;

				case SEND_DUEL_OPPONENTS_ITEMS:
					DuelStakeStruct ds = (DuelStakeStruct) payload;
					int othersSize = ds.count;
					builder.writeByte((byte) othersSize);
					for (int i = 0; i < othersSize; i++) {
						builder.writeShort(ds.catalogIDs[i]);
						builder.writeInt(ds.amounts[i]);
					}
					break;

				case SEND_DUEL_SETTINGS:
					DuelSettingsStruct dss = (DuelSettingsStruct) payload;
					builder.writeByte((byte) dss.disallowRetreat);
					builder.writeByte((byte) dss.disallowMagic);
					builder.writeByte((byte) dss.disallowPrayer);
					builder.writeByte((byte) dss.disallowWeapons);
					break;

				case SEND_DUEL_ACCEPTED:
				case SEND_DUEL_OTHER_ACCEPTED:
					DuelAcceptStruct da = (DuelAcceptStruct) payload;
					builder.writeByte((byte) da.accepted);
					break;

				case SEND_DUEL_CONFIRMWINDOW:
					DuelConfirmStruct dc = (DuelConfirmStruct) payload;
					builder.writeLong(DataConversions.usernameToHash(dc.targetPlayer));
					int stakedItemSize = dc.opponentDuelCount;
					builder.writeByte((byte) stakedItemSize);
					for (int i = 0; i < stakedItemSize; i++) {
						builder.writeShort(dc.opponentCatalogIDs[i]);
						builder.writeInt(dc.opponentAmounts[i]);
					}
					stakedItemSize = dc.myCount;
					builder.writeByte((byte) stakedItemSize);
					for (int i = 0; i < stakedItemSize; i++) {
						builder.writeShort(dc.myCatalogIDs[i]);
						builder.writeInt(dc.myAmounts[i]);
					}
					builder.writeByte((byte) dc.disallowRetreat);
					builder.writeByte((byte) dc.disallowMagic);
					builder.writeByte((byte) dc.disallowPrayer);
					builder.writeByte((byte) dc.disallowWeapons);
					break;

				case SEND_SLEEPSCREEN:
					SleepScreenStruct ss = (SleepScreenStruct) payload;
					builder.writeBytes(ss.image);
					break;

				case SEND_FRIEND_LIST:
					FriendListStruct fl = (FriendListStruct) payload;
					int friendSize = fl.listSize;
					builder.writeByte((byte) friendSize);
					for (int i = 0; i < friendSize; i++) {
						builder.writeLong(DataConversions.usernameToHash(fl.name[i]));
						builder.writeByte((byte) fl.worldNumber[i]);
					}
					break;

				case SEND_FRIEND_UPDATE:
					FriendUpdateStruct fr = (FriendUpdateStruct) payload;
					builder.writeLong(DataConversions.usernameToHash(fr.name));
					builder.writeByte((byte) fr.worldNumber);
					break;

				case SEND_IGNORE_LIST:
					IgnoreListStruct il = (IgnoreListStruct) payload;
					int ignoreSize = il.listSize;
					builder.writeByte((byte) ignoreSize);
					for (int i = 0; i < ignoreSize; i++) {
						builder.writeLong(DataConversions.usernameToHash(il.name[i]));
					}
					break;

				case SEND_INVENTORY:
					InventoryStruct is = (InventoryStruct) payload;
					int inventorySize = is.inventorySize;
					builder.writeByte((byte) inventorySize);
					for (int i = 0; i < inventorySize; i++) {
						// First bit is if it is wielded or not
						builder.writeShort((is.wielded[i] << 15) | is.catalogIDs[i]);
						// amount[i] will only be > 0 if the item is stackable or noted.
						if (is.amount[i] > 0) {
							builder.writeUnsignedByteInt(is.amount[i]);
						}
					}
					break;

				case SEND_INVENTORY_REMOVE_ITEM:
					InventoryUpdateStruct iupr = (InventoryUpdateStruct) payload;
					builder.writeByte((byte) iupr.slot);
					break;

				case SEND_INVENTORY_UPDATEITEM:
					InventoryUpdateStruct iup = (InventoryUpdateStruct) payload;
					builder.writeByte((byte) iup.slot);
					boolean isItemNull = iup.catalogID == 0 && iup.amount == 0;
					if (!isItemNull) {
						builder.writeShort(iup.catalogID + (iup.wielded == 1 ? 32768 : 0));
						// amount will only be > 0 if the item is stackable or noted
						if (iup.amount > 0) {
							builder.writeUnsignedByteInt(iup.amount);
						}
					} else {
						builder.writeShort(0);
						builder.writeShort(0);
						builder.writeInt(0);
					}
					break;

				case SEND_BANK_OPEN:
					BankStruct b = (BankStruct) payload;
					int storedSize = b.itemsStoredSize;
					int maxBankSize = b.maxBankSize;
					builder.writeByte(storedSize > 255 ? (byte)255 : storedSize & 0xFF);
					builder.writeByte(maxBankSize > 255 ? (byte)255 : maxBankSize & 0xFF);
					for (int i = 0; i < storedSize; i++) {
						builder.writeShort(b.catalogIDs[i]);
						builder.writeUnsignedByteInt(b.amount[i]);
					}
					break;

				case SEND_BANK_UPDATE:
					BankUpdateStruct bu = (BankUpdateStruct) payload;
					builder.writeByte((byte) bu.slot);
					builder.writeShort(bu.catalogID);
					builder.writeUnsignedByteInt(bu.amount);
					break;

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
					builder.writeBytes(StringUtil.compressMessage(pm.message));
					break;

				case SEND_WELCOME_INFO:
					WelcomeInfoStruct sw = (WelcomeInfoStruct) payload;
					// Send days since login
					builder.writeInt(sw.daysSinceLogin);

					// Send days since recovery question changed (different format than RSC235)
					int daysSinceRecoveryChange = sw.daysSinceRecoveryChange;
					if (daysSinceRecoveryChange == -1) {
						builder.writeInt(-1);
					} else if (daysSinceRecoveryChange < 14) {
						builder.writeInt(14 - daysSinceRecoveryChange);
					} else {
						builder.writeInt(-1);
					}

					// Send 4 byte IP Address
					String ipString = sw.lastIp; // Open RSC stores IP address as a string which must be converted
					if (!ipString.contains(":")) {
						// IPv4
						String[] ipSplit = ipString.split("\\.");
						if (ipSplit.length == 4) {
							for (int i = 0; i < 4; i++) {
								builder.writeByte(Integer.parseInt(ipSplit[i]) & 0xFF);
							}
						} else {
							// Failed to parse IP address, just send 0.0.0.0, it doesn't matter that much that this is accurate.
							for (int i = 0; i < 4; i++) {
								builder.writeByte(0);
							}
						}
					} else {
						// IPv6
						// Authentic server sends IP address as an 32 bit integer, IPv6 addresses can not be fully represented.
						// Going to concat IPv6 address from 128 bits to just 24 bits, and use the first 8 bits of ipv4 to denote ipv6.
						System.out.println("ipv6 user address: " + ipString);

						// Mark this as an ipv6 address by writing "6" as the first octet of the ipv4 address.
						// Technically this is naughty, since the 6.0.0.0/8 ipv4 block could be one day be networked on the internet.
						// However, since 1990-03-26, the U.S. Army has reserved that IP block for their own internal use.
						// If an IP address begins with a 6, it is a valid ipv4 address, but it is not one that you will ever see
						// (assuming you are not running the Open RSC server inside a US Army network, or are borrowing their IP
						//  block the way I am here...!).
						//
						// Another sane alternative would have been to write a 0 here, but I like 6 better.
						builder.writeByte(6);

						String[] ipv6components = ipString.split(":");
						byte[] writeMe = new byte[3];
						int byteIdx = writeMe.length - 1;
						for (int i = ipv6components.length - 1; i > 0 && byteIdx > 0; i--) {
							try {
								writeMe[byteIdx--] = (byte) (Integer.parseInt(ipv6components[i], 16) & 0xFF);
							} catch (NumberFormatException nfe) {
								// will remain 0, or find some other ipv6 segment we understand
							}
						}
						for (int i = 0; i < writeMe.length; i++) {
							builder.writeByte(writeMe[i]);
						}

					}

					// Unread messages not sent in 2003
					// builder.writeShort(sw.unreadMessages + 1); // Number of messages gets subtracted by 1 by the client
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
					MobsUpdateStruct mu = (MobsUpdateStruct) payload;
					builder.startBitAccess();
					for (Map.Entry<Integer, Integer> entry : mu.mobs) {
						builder.writeBits(entry.getKey(), entry.getValue());
					}
					builder.finishBitAccess();
					break;

				case SEND_UPDATE_NPC:
				case SEND_UPDATE_PLAYERS:
					AppearanceUpdateStruct au = (AppearanceUpdateStruct) payload;
					for (Object entry : au.info) {
						if (entry instanceof Byte) {
							builder.writeByte((Byte) entry);
						} else if (entry instanceof Short) {
							builder.writeShort((Short) entry);
						} else if (entry instanceof Integer) {
							builder.writeInt((Integer) entry);
						} else if (entry instanceof Long) {
							builder.writeLong((Long) entry);
						} else if (entry instanceof Character) { // wrapper class for appearance byte
							int value = (Character) entry;
							builder.writeAppearanceByte((byte) value, 177);
						} else if (entry instanceof String) {
							builder.writeNonTerminatedString((String) entry);
						} else if (entry instanceof RSCString) {
							byte[] byteMe = StringUtil.compressMessage(entry.toString());
							builder.writeByte(byteMe.length);
							builder.writeBytes(byteMe, 0, byteMe.length);
						}
					}
					break;

				case SEND_SCENERY_HANDLER:
					GameObjectsUpdateStruct go = (GameObjectsUpdateStruct) payload;
					for (GameObjectLoc objectLoc : go.objects) {
						builder.writeShort(objectLoc.getId());
						builder.writeByte((byte) objectLoc.getX());
						builder.writeByte((byte) objectLoc.getY());
					}
					break;

				case SEND_BOUNDARY_HANDLER:
					GameObjectsUpdateStruct go1 = (GameObjectsUpdateStruct) payload;
					for (GameObjectLoc objectLoc : go1.objects) {
						builder.writeShort(objectLoc.getId());
						builder.writeByte((byte) objectLoc.getX());
						builder.writeByte((byte) objectLoc.getY());
						builder.writeByte((byte) objectLoc.getDirection());
					}
					break;

				case SEND_GROUND_ITEM_HANDLER:
					GroundItemsUpdateStruct gri = (GroundItemsUpdateStruct) payload;
					for (ItemLoc it : gri.objects) {
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
			}
		}

		return builder != null ? builder.toPacket() : null;
	}
}
