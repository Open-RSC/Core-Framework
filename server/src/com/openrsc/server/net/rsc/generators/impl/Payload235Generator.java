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

import java.util.HashMap;
import java.util.Map;

/**
 * RSC Protocol-235 Generator for Outgoing Packets from respective Protocol Independent Structs
 * **/
public class Payload235Generator implements PayloadGenerator<OpcodeOut> {
	private static final Map<OpcodeOut, Integer> opcodeMap = new HashMap<OpcodeOut, Integer>() {{
		put(OpcodeOut.SEND_LOGOUT_REQUEST_CONFIRM, 4);
		put(OpcodeOut.SEND_QUESTS, 5);
		put(OpcodeOut.SEND_DUEL_OPPONENTS_ITEMS, 6);
		put(OpcodeOut.SEND_TRADE_ACCEPTED, 15);
		put(OpcodeOut.SEND_TRADE_OPEN_CONFIRM, 20);
		put(OpcodeOut.SEND_WORLD_INFO, 25);
		put(OpcodeOut.SEND_DUEL_SETTINGS, 30);
		put(OpcodeOut.SEND_EXPERIENCE, 33);
		put(OpcodeOut.SEND_BUBBLE, 36); // used for teleport, telegrab, and iban's magic
		put(OpcodeOut.SEND_BANK_OPEN, 42);
		put(OpcodeOut.SEND_SCENERY_HANDLER, 48);
		put(OpcodeOut.SEND_PRIVACY_SETTINGS, 51);
		put(OpcodeOut.SEND_SYSTEM_UPDATE, 52);
		put(OpcodeOut.SEND_INVENTORY, 53);
		put(OpcodeOut.SEND_APPEARANCE_SCREEN, 59);
		put(OpcodeOut.SEND_NPC_COORDS, 79);
		put(OpcodeOut.SEND_DEATH, 83);
		put(OpcodeOut.SEND_STOPSLEEP, 84);
		put(OpcodeOut.SEND_PRIVATE_MESSAGE_SENT, 87);
		put(OpcodeOut.SEND_BOX2, 89);
		put(OpcodeOut.SEND_INVENTORY_UPDATEITEM, 90);
		put(OpcodeOut.SEND_BOUNDARY_HANDLER, 91);
		put(OpcodeOut.SEND_TRADE_WINDOW, 92);
		put(OpcodeOut.SEND_TRADE_OTHER_ITEMS, 97);
		put(OpcodeOut.SEND_GROUND_ITEM_HANDLER, 99);
		put(OpcodeOut.SEND_SHOP_OPEN, 101);
		put(OpcodeOut.SEND_UPDATE_NPC, 104);
		put(OpcodeOut.SEND_IGNORE_LIST, 109);
		put(OpcodeOut.SEND_ON_TUTORIAL, 111);
		put(OpcodeOut.SEND_FATIGUE, 114);
		put(OpcodeOut.SEND_SLEEPSCREEN, 117);
		put(OpcodeOut.SEND_PRIVATE_MESSAGE, 120);
		put(OpcodeOut.SEND_INVENTORY_REMOVE_ITEM, 123);
		put(OpcodeOut.SEND_TRADE_CLOSE, 128);
		put(OpcodeOut.SEND_SERVER_MESSAGE, 131);
		put(OpcodeOut.SEND_SHOP_CLOSE, 137);
		put(OpcodeOut.SEND_FRIEND_UPDATE, 149);
		put(OpcodeOut.SEND_EQUIPMENT_STATS, 153);
		put(OpcodeOut.SEND_STATS, 156);
		put(OpcodeOut.SEND_STAT, 159);
		put(OpcodeOut.SEND_TRADE_OTHER_ACCEPTED, 162);
		put(OpcodeOut.SEND_LOGOUT, 165);
		put(OpcodeOut.SEND_DUEL_CONFIRMWINDOW, 172);
		put(OpcodeOut.SEND_DUEL_WINDOW, 176);
		put(OpcodeOut.SEND_WELCOME_INFO, 182);
		put(OpcodeOut.SEND_CANT_LOGOUT, 183);
		put(OpcodeOut.SEND_28_BYTES_UNUSED, 189);
		put(OpcodeOut.SEND_PLAYER_COORDS, 191);
		put(OpcodeOut.SEND_SLEEPWORD_INCORRECT, 194);
		put(OpcodeOut.SEND_BANK_CLOSE, 203);
		put(OpcodeOut.SEND_PLAY_SOUND, 204);
		put(OpcodeOut.SEND_PRAYERS_ACTIVE, 206);
		put(OpcodeOut.SEND_DUEL_ACCEPTED, 210);
		put(OpcodeOut.SEND_REMOVE_WORLD_ENTITY, 211);
		put(OpcodeOut.SEND_APPEARANCE_KEEPALIVE, 213);
		put(OpcodeOut.SEND_BOX, 222);
		put(OpcodeOut.SEND_OPEN_RECOVERY, 224); // part of rsc era protocol
		put(OpcodeOut.SEND_DUEL_CLOSE, 225);
		put(OpcodeOut.SEND_OPEN_DETAILS, 232); // part of rsc era protocol
		put(OpcodeOut.SEND_UPDATE_PLAYERS, 234);
		put(OpcodeOut.SEND_UPDATE_IGNORE_LIST_BECAUSE_NAME_CHANGE, 237);
		put(OpcodeOut.SEND_GAME_SETTINGS, 240);
		put(OpcodeOut.SEND_SLEEP_FATIGUE, 244);
		put(OpcodeOut.SEND_OPTIONS_MENU_OPEN, 245);
		put(OpcodeOut.SEND_BANK_UPDATE, 249);
		put(OpcodeOut.SEND_OPTIONS_MENU_CLOSE, 252);
		put(OpcodeOut.SEND_DUEL_OTHER_ACCEPTED, 253);
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
				// not currently implemented
				case SEND_28_BYTES_UNUSED:
					break;

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
					builder.writeZeroQuotedString(message);
					break;

				case SEND_OPTIONS_MENU_OPEN:
					MenuOptionStruct mo = (MenuOptionStruct) payload;
					int numOptions = Math.min(mo.numOptions, 5);
					builder.writeByte((byte) numOptions);
					for (int i = 0; i < 5 && i < numOptions; i++){
						builder.writeZeroQuotedString(mo.optionTexts[i]);
					}
					break;

				case SEND_ON_TUTORIAL:
					PlayerOnTutorialStruct playOnTut = (PlayerOnTutorialStruct) payload;
					builder.writeByte((byte) playOnTut.onTutorial);
					break;

				case SEND_SYSTEM_UPDATE:
					SystemUpdateStruct su = (SystemUpdateStruct) payload;
					builder.writeShort((int) (((double) su.seconds / 32D) * 50));
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
					builder.writeZeroQuotedString(pls.soundName);
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

					builder.writeZeroQuotedString(tc.targetPlayer);
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
					builder.writeZeroQuotedString(dc.targetPlayer);
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

				case SEND_FRIEND_UPDATE:
					FriendUpdateStruct fr = (FriendUpdateStruct) payload;
					builder.writeZeroQuotedString(fr.name);
					builder.writeZeroQuotedString(fr.formerName);
					builder.writeByte((byte) fr.onlineStatus);
					if (!fr.worldName.equals(""))
						builder.writeZeroQuotedString(fr.worldName);
					break;

				case SEND_UPDATE_IGNORE_LIST_BECAUSE_NAME_CHANGE:
					IgnoreListStruct uil = (IgnoreListStruct) payload;
					builder.writeZeroQuotedString(uil.name[0]);
					builder.writeZeroQuotedString(uil.name[0]);
					builder.writeZeroQuotedString(uil.formerName[0]);
					builder.writeZeroQuotedString(uil.formerName[0]);
					builder.writeByte((byte)(uil.updateExisting ? 1 : 0));
					break;

				case SEND_IGNORE_LIST:
					IgnoreListStruct il = (IgnoreListStruct) payload;
					int ignoreSize = il.listSize;
					builder.writeByte((byte) ignoreSize);
					for (int i = 0; i < ignoreSize; i++) {
						builder.writeZeroQuotedString(il.name[i]);
						builder.writeZeroQuotedString(il.name[i]); // meant to be a duplicate
						builder.writeZeroQuotedString(il.formerName[i]);
						builder.writeZeroQuotedString(il.formerName[i]); // meant to be a duplicate
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
							builder.writeUnsignedShortInt(is.amount[i]);
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
							builder.writeUnsignedShortInt(iup.amount);
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
						builder.writeUnsignedShortInt(b.amount[i]);
					}
					break;

				case SEND_BANK_UPDATE:
					BankUpdateStruct bu = (BankUpdateStruct) payload;
					builder.writeByte((byte) bu.slot);
					builder.writeShort(bu.catalogID);
					builder.writeUnsignedShortInt(bu.amount);
					break;

				case SEND_SHOP_OPEN:
					ShopStruct s = (ShopStruct) payload;
					int shopSize = s.itemsStockSize;
					builder.writeByte((byte) shopSize);
					builder.writeByte((byte) s.isGeneralStore);
					builder.writeByte((byte) s.sellModifier);
					builder.writeByte((byte) s.buyModifier);
					builder.writeByte((byte) s.stockSensitivity);
					for (int i = 0; i < shopSize; i++) {
						builder.writeShort(s.catalogIDs[i]);
						builder.writeShort(s.amount[i]);
						builder.writeShort(s.baseAmount[i]);
					}
					break;

				case SEND_SERVER_MESSAGE:
					MessageStruct m = (MessageStruct) payload;
					builder.writeByte((byte) m.messageTypeRsId);
					int infoContained = m.infoContained;
					builder.writeByte((byte) infoContained);
					builder.writeZeroQuotedString(m.message);
					if ((infoContained & 1) != 0) {
						builder.writeZeroQuotedString(m.senderName);
						builder.writeZeroQuotedString(m.senderName); // This is authentic; all recorded instances it's just the same username twice.
					}
					if ((infoContained & 2) != 0) {
						builder.writeZeroQuotedString(m.colorString);
					}
					break;

				case SEND_PRIVATE_MESSAGE:
					PrivateMessageStruct pm = (PrivateMessageStruct) payload;
					builder.writeZeroQuotedString(pm.playerName);
					builder.writeZeroQuotedString(pm.formerName);
					builder.writeByte((byte) pm.iconSprite);

					// 8 byte "Message ID" field is next
					builder.writeByte(0); // Unused Padding
					builder.writeByte(0); // Unused Padding
					builder.writeByte(0); // Unused Padding
					builder.writeShort(pm.worldNumber);
					// 24 bit value for number of private messages sent on server since restart
					builder.writeByte((pm.totalSentMessages & 0x00FF0000) >> 16);
					builder.writeByte((pm.totalSentMessages & 0x0000FF00) >> 8);
					builder.writeByte((pm.totalSentMessages & 0x000000FF));
					builder.writeRSCString(pm.message);
					break;

				case SEND_PRIVATE_MESSAGE_SENT:
					PrivateMessageStruct pm1 = (PrivateMessageStruct) payload;
					builder.writeZeroQuotedString(pm1.playerName);
					builder.writeRSCString(pm1.message);
					break;

				case SEND_WELCOME_INFO:
					WelcomeInfoStruct sw = (WelcomeInfoStruct) payload;
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

					// TODO: this format may not be exactly compatible.
					builder.writeShort(sw.daysSinceLogin);

					int daysSinceRecoveryChange = sw.daysSinceRecoveryChange;
					if (daysSinceRecoveryChange == -1) {
						builder.writeByte((byte) 200);
					} else if (daysSinceRecoveryChange < 14) {
						builder.writeByte((byte) daysSinceRecoveryChange);
					} else {
						builder.writeByte((byte) 201);
					}

					// TODO: if player.getUnreadMessages is implemented, implement that here
					builder.writeShort(sw.unreadMessages + 1); // Number of messages gets subtracted by 1 by the client
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
						} else if (entry instanceof Character) { // wrapper class for appearance byte
							int value = (Character) entry;
							builder.writeAppearanceByte((byte) value, 235);
						} else if (entry instanceof String) {
							builder.writeZeroQuotedString((String) entry);
						} else if (entry instanceof RSCString) {
							builder.writeRSCString(entry.toString());
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
