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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * RSC Protocol-115 Generator for Outgoing Packets from respective Protocol Independent Structs
 * **/
public class Payload115Generator implements PayloadGenerator<OpcodeOut> {
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
		// 232 SEND_OPEN_DETAILS not yet existent and SEND_REMOVE_WORLD_PLAYER gone
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
		/*put(OpcodeOut.SEND_PLAY_SOUND, 207);
		put(OpcodeOut.SEND_BUBBLE, 206); // used for teleport, telegrab, and iban's magic
		put(OpcodeOut.SEND_WELCOME_INFO, 205);
		put(OpcodeOut.SEND_BOX2, 204);
		put(OpcodeOut.SEND_BOX, 203);
		put(OpcodeOut.SEND_FATIGUE, 202);
		put(OpcodeOut.SEND_SLEEPSCREEN, 201);
		put(OpcodeOut.SEND_SLEEP_FATIGUE, 200);
		put(OpcodeOut.SEND_STOPSLEEP, 199);
		put(OpcodeOut.SEND_SLEEPWORD_INCORRECT, 198);
		// put(OpcodeOut.SEND_SYSTEM_UPDATE, 197); // Doesn't exist
		// put(OpcodeOut.SEND_APPEARANCE_KEEPALIVE, 196); // also does not exist ofc
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
				case SEND_OPEN_RECOVERY:
				case SEND_OPEN_DETAILS:
				case SEND_DEATH:
				case SEND_TRADE_CLOSE:
				case SEND_DUEL_CLOSE:
				case SEND_BANK_CLOSE:
				case SEND_SHOP_CLOSE:
				case SEND_OPTIONS_MENU_CLOSE:
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

				case SEND_GAME_SETTINGS:
					GameSettingsStruct gs = (GameSettingsStruct) payload;
					builder.writeByte((byte) gs.cameraModeAuto);
					builder.writeByte((byte) gs.mouseButtonOne);
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

				case SEND_FRIEND_LIST:
					FriendListStruct fl = (FriendListStruct) payload;
					int friendSize = fl.listSize;
					builder.writeByte((byte) friendSize);
					for (int i = 0; i < friendSize; i++) {
						builder.writeLong(DataConversions.usernameToHash(fl.name[i]));
						builder.writeByte((byte) (fl.worldNumber[i] == 99 ? 10 : fl.worldNumber[i]));
					}
					break;

				case SEND_FRIEND_UPDATE:
					FriendUpdateStruct fr = (FriendUpdateStruct) payload;
					builder.writeLong(DataConversions.usernameToHash(fr.name));
					builder.writeByte((byte) (fr.worldNumber == 99 ? 10 : fr.worldNumber));
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
							builder.writeUnsignedByteInt(is.amount[i] & 0xffff);
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
					builder.writeByte(storedSize > 255 ? (byte)255 : storedSize & 0xFF);
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
					builder.writeBytes(make115ChatMessage(pm.message));
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
							builder.writeAppearanceByte((byte) value, 140);
						} else if (entry instanceof String) {
							builder.writeNonTerminatedString((String) entry);
						} else if (entry instanceof RSCString) {
							byte[] byteMe = make115ChatMessage(entry.toString());
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

	private byte[] make115ChatMessage(String paramString) {
		// TODO: refactor
		// good words - place and read from file
		String[] ygb = new String[]{"i","you","to","me","a","for","the","what","have","ok","it","do","no","lol","need","its","can","is","and","want","selling","how","get","iron","got","go","my","i'm","im","that","your","you're","youre","yourself","here","buy","don't","dont","i'll","ill","hey","in","free","on","buying","runes","of","will","where","much","press","hi","give","yes","are","not","one","armor","trade","all","this","make","am","some","just","know","ore","if","kill","bars","stuff","he","or","sell","gp","now","plz","money","help","more","who","so","oh","there","any","gold","sale","coal","level","up","axe","adam","him","yeah","wait","bronze","anyone","come","follow","good","with","only","we","but","then","please","lvl","steel","mine","did","like","be","take","sorry","lets","why","cool","sword","back","them","was","shield","at","can't","cant","smithing","thanks","thx","armour","too","well","see","meat","out","food","arrows","an","man","legs","right","bar","black","each","rune","they","fight","helmet","pay","bye","stop","going","guy","quest","run","off","hehe","np","long","from","mith","were","yo","does","someone","thanx","key","new","sure","bead","way","pie","smith","tin","many","nope","when","hold","attack","anything","ha","weapon","plate","body","hello","better","about","other","clan","something","kite","newbie","red","pies","let","look","pk","think","haha","nice","copper","use","magic","time","hat","fire","mining","tell","yep","has","really","wine","away","law","could","amulet","cut","nature","helm","guys","died","name","over","bow","wow","would","nothing","huh","had","talk","mind","won't","wont","pick","find","first","show","friend","dude","join","blue","wants","as","again","killed","by","yup","down","air","his","didn't","didnt","done","mithril","'em","say","said","bank","spare","thing","goblin","chaos","people","beads","gems","silverlight","brb","sec","clay","battle","hammer","stats","full","sapphire","fine","mace","than","leave","keep","kind","yet","try","weapons","though","put","nah","later","shoot","accept","else","top","still","sup","shop","water","heh","okay","chest","cosmic","place","cya","shut","never","hit","these","ruby","cooked","bolts","should","bones","her","worth","staff","white","those","range","i've","ive","emerald","lost","thank","gotta","blurite","k","crossbow","door","doing","chefs","thnx","dagger","store","uncut","god","varrock","left","she","stay","offer","half","cheap","us","hmm","smelt","almost","apple","move","which","nm","enough","already","gave","ready","best","same","cook","ty","drop","dark","another","hahaha","type","chain","yellow","even","friends","room","made","short","two","getting","little","dunno","ores","been","big","following","very","c'mon","after","death","work","anybody","coins","hurry","weak","large","yah","deal","sry","pickaxe","lot","oops","maybe","hard","kool","rock","thought","ice","items","things","king","ah","extra","wot","close","fighting","lots","dead","cooking","wanted","meet","ask","yay","last","open","sir","sweet","around","trying","log","cause","trading","girl","square","um","cabbage","necklace","wool","list","into","guess","welcome","easy","price","beer","wizard","wrong","min","needs","oil","poor","next","diamond","soon","sold","high","furnace","earth","mines","took","start","none","archer","town","fun","coming","strong","ranged","ever","phoenix","eat","both","leg","ain't","aint","swords","moi","player","must","told","started","cost","space","arrav","talking","knight","before","play","pot","nuggets","because","killer","might","wood","while","flour","server","guild","found","isn't","isnt","ppl","crafting","milk","chef","lumbridge","funny","change","piece","light","boy","day","low","falador","everything","beat","cash","team","ghost","newbies","care","ahh","train","killing","doesn't","doesnt","ones","watch","remember","raw","village","call","logged","knights","uh","umm","anyway","non","skull","fast","bought","silver","alright","bring","person","our","cross","hehehe","own","turn","prayer","wish","hmmm","making","levels","rich","everyone","jk","looking","attacked","lead","green","daggers","hp","used","bread","skirt","every","times","logs","today","saw","points","check","potion","great","gone","also","per","night","guard","sum","far","once","till","strength","i'd","id","lord","went","brass","always","add","giving","giants","takes","redberry","hahahaha","few","rocks","ohh","chicken","kinda","borrow","win","bit","gem","dwarf","pretty","crossbows","berries","wizards","eggs","hits","lose","castle","goblins","most","hole","bob","haven't","havent","whatever","side","needed","miner","amulets","says","called","egg","axes","seen","rat","burnt","may","war","outside","wear","nugget","boots","health","medium","power","berry","handed","eye","quick","shot","bear","anymore","bro","rats","shields","lucky","dish","arrow","lvls","spell","slow","myself","through","hay","heal","err","kills","sapphires","either","spaces","rest","ago","you'll","youll","chisel","near","garlic","ring","ned","master","gate","looks","dye","being","quests","three","higher","somebody","guards","bucket","robe","tried","forgot","together","advanced","actually","map","idea","their","part","item","waiting","smelting","mold","stronger","hear","bows","holy","problem","character","happy","south","less","aren't","arent","save","behind","jug","least","box","newt","walk","makes","hope","helmets","gives","fish","playing","peace","set","giant","barbarian","whoa","wearing","point","finish","luck","wasn't","wasnt","nevermind","warrior","dragon","grab","general","evil","ladder","orange","listen","without","shears","second","stake","buddy","bet","hah","anvil","under","heard","gets","worry","true","forget","color","hurt","face","scared","men","ground","char","inside","spot","shadow","leader","imp","wouldn't","wouldnt","taking","hiya","gtg","enchant","dropped","leather","suit","grr","enchanted","self","probably","sheep","running","faster","rare","plus","scimitar","since","keys","wind","archers","diamonds","except","carry","north","minute","days","training","delrith","ahead","unless","duh","until","matter","emeralds","super","kidding","speak","somewhere","raise","secret","vampire","drink","vault","defense","argh","minutes","tinderbox","stand","burn","member","goes","cooks","comes","chickens","came","arch","happened","finally","couldn't","couldnt","shall","heheh","chance","gloves","maces","push","knows","tower","witch","grain","able","tree","spider","tinder","pots","paid","mins","floor","fair","defence","road","certificate","using","teleport","such","interested","helping","others","myth","late","accuracy","yesterday","poison","barb","small","rid","wheat","promise","lend","pking","paying","adamantite","junk","busy","book","string","sigh","doh","whole","scorps","hatchet","asked","coals","awesome","monks","dishes","aha","shooting","lady","west","mould","broke","arm","anyways","meant","different","saying","ruins","ouch","means","dwarven","attacking","outta","scorpions","working","they're","theyre","demon","message","sometimes","common","area","upstairs","spells","dough","knew","grrr","craft","rule","city","works","week","that's","let's","he's","she's","it's","who's","what's","where's","how's","why's","falador's","varrock's","want's","here's","champ's","cook's","chief's","man's","mine's","thats","lets","hes","shes","its","whos","whats","wheres","hows","whys","faladors","varrocks","wants","heres","champs","cooks","chiefs","mans","mines","tail","leaving","rules","girls","gauge","front","smither","pker","wooden","helped","aww","asking","sun","juliet","fountain","prince","stairs","ranger","inventory","cow","draynor","church","wonder","romeo","sad","read","costs","word","skill","damage","woman","wassup","mix","willing","transfer","leggings","knife","hours","fighter","switch","skeleton","scorpion","fur","inn","stick","expensive","east","saving","end","tube","sells","port","drunk","stuck","normal","bored","understand","protect","moon","bury","mage","champs","monk","forever","bananas","tomorrow","distract","special","lawrence","answer","warriors","scorp","male","having","question","pieces","tough","imps","happens","cans","dungeon","chase","ooh","annoying","kicked","female","equipment","couple","cave","slayer","hour","instead","helms","farmer","lower","leaves","dig","mate","pull","send","kings","complete","highest","aw","reason","moss","words","cheers","boring","safe","peeps","arg","waste","hair","changed","twice","tired","practice","wolf","usually","cutting","wall","stone","round","onion","spiders","met","joking","finished","anvils","tons","smart","jolly","dose","apron","course","won","runs","letters","bunch","melt","final","sit","boar","besides","wines","goto","cobweb","names","npk","boo","tonight","past","party","moment","form","building","pressure","standing","pit","nobody","bah","sounds","root","favor","bridge","necklaces","'cos","beg","quite","meeting","library","kharid","joke","ic","herald","button","happen","against","pile","likes","corner","ton","storage","return","sewers","bone","badly","miss","mess","longbow","lalala","excuse","closed","rope","keeps","impure","die","dies","serious","pirates","kites","cheaper","woah","skills","reward","molds","closet","charge","become","adams","total","test","sewer","den","bk","onions","neither","meats","learn","everybody","woad","supposed","nearly","mined","trees","lala","ook","mark","kinds","chop","uniform","swap","metal","count","afford","wiz","smithed","rings","picked","moo","line","ashes","cares","woohoo","women","telling","rubies","locked","hats","dwarves","colors","cloud","anywhere","thief","skeletons","plenty","nearest","farm","equip","dying","cast","added","traded","gained","clans","steak","row","brown","pks","earlier","coin","ace","teach","spent","longsword","curse","sleep","players","members","exactly","dwarfs","depends","spy","named","lying","group","above","starting","appear","rofl","picture","pm","kept","easier","doors","block","monsters","manor","field","worked","whoops","whoever","fan","cadava","along","shouldn't","shouldnt","shortbow","bolt","blah","staffs","plates","no-one","clue","chat","zombies","scroll","rawmeat","ate","ruin","limp","laugh","fill","helps","empty","business","trip","star","squire","scary","report","rather","throw","saved","potions","number","mugger","gotten","file","wheel","cadavaberries","blood","weaken","status","sort","wondering","played","hunter","trouble","pink","mossy","okie","fought","dangerous","champions","break","zero","weeks","shirt","collect","split","loose","cough","combat","truce","summer","powerful","middle","zombie","loss","enjoy","archery","thinking","prove","nvm","catch","attacks","stat","snake","sarim","oic","forge","build","boys","whew","cold","thinks","prices","missed","thankyou","seriously","nights","heals","fires","figure","longer","wig","taken","private","talked","table","clothes","ally","write","stock","sky","seeya","redberries","monster","holding","hide","cows","cannot","advance","you've","youve","main","limpwurt","yawn","stores","sound","roll","partner","messed","info","ghosts","ernest","erm","choose","broken","soul","pour","mountain","exp","case","ali","rimmington","plan","increase","entrance","crowded","banana","healing","eyes","boss","refine","places","phew","mode","hitpoints","supply","lever","dieing","ash","afraid","seem","pastry","hero","etc","dropping","doric","between","queen","often","museum","jump","grim","mill","grave","given","prayers","non-pk","losing","forest","champ","turned","step","healed","grapes","chainmail","cabbages","anytime","typing","type","types","spawn","mistake","guide","careful","tells","post","offering","month","fit","enter","chill","walking","profit","mega","confused","bks","alive","suppose","redwine","bother","beside","useless","snow","search","outfit","lum","easily","earn","dudes","recharge","ignore","honest","bucks","straight","spinning","regular","grow","upper","moving","deposit","bid","bears","across","trades","tip","morning","evening","msg","hose","fell","fall","dare","completed","accepted","spend","priest","loan","disconnected","agree","sign","pirate","mission","miners","leela","ghostspeak","fly","flower","watching","thin","magical","gunthor","fresh","flame","cooper","beers","seems","runite","muggers","hunt","hire","cookedmeat","colour","calling","bag","awhile","armors","request","rain","paladin","hobgoblins","games","beard","army","tommorow","seconds","mighty","laughing","fred","apples","wild","whose","ways","temple","song","skulls","secs","sail","robes","kit","interesting","closest","apothecary","yum","worthless","weren't","werent","unicorn","stove","spade","gosh","battleaxe","ferment","yummy","wilderness","stout","shops","match","burned","smelter","pray","nose","mostly","amount","weaker","smithy","platemail","fights","windmill","purple","pkers","perfect","pans","months","gift","fault","cheese","below","raises","hungry","heya","heaps","drain","closer","choice","barely","aye","smelted","peoples","glad","crafter","stopped","prospect","pair","joining","hitting","examine","enemy","duck","smiths","sister","possible","market","looked","levers","golden","yarn","winning","uncooked","silent","spears","parts","lock","btw","board","bishop","bake","truth","strongest","lumb","cry","alchemy","stew","howdy","fail","everywhere","downstairs","doubt","club","barbarians","arms","window","palace","moulds","fishing","dart","clear","bigger","begging","adds","woodcutting","wing","telekinetic","supplies","pure","major","important","goodbye","fortress","dusty","asgarnia","worse","spots","sob","reldo","random","quickly","buys","worst","value","tsk","they'll","theyll","target","blackarm","mister","joined","indeed","dinner","ammo","agent","wise","valuable","tramp","swamp","starts","path","mass","manhole","stars","spike","sometime","sense","roar","revenge","rescue","offline","listening","impossible","handle","fence","fellow","element","dear","caves","swing","swift","storm","simple","sets","plated","option","notice","enchanting","eating","donate","chars","venom","showed","provide","prefer","oven","order","language","ladies","hammers","characters","yell","wasted","solid","sheesh","sake","loud","kebabs","hunting","herblaw","class","switched","skirts","pub","noticed","messages","lone","knock","keeping","improve","exit","decent","basement","trader","takers","sugar","staying","sand","regenerate","pack","fairy","failed","allies","wasting","walked","showing","roots","quiet","privacy","ooops","odd","incantation","hundred","honor","hideout","hail","further","foot","explain","experience","desert","corn","ale","yuck","ultra","third","taste","tall","quicker","offered","neat","missing","invisible","hugs","grey","faced","duel","difference","cover","unfermented","street","sneak","proud","fix","due","boom","pked","picks","mystic","materials","maker","machine","lest","land","figured","farther","cupboard","collateral","thou","thee","that'll","thatll","prize","present","ninja","misty","million","lowest","horse","hardly","cloak","brought","tag","sent","royal","ranging","putting","partners","jumped","irons","ingredients","headed","gypsy","grill","exact","crush","brothers","blocked","whenever","skin","respawn","finding","certain","center","bark","average","writing","useful","turns","style","sink","shear","scimitars","passed","onto","manage","leaders","jail","including","highway","froze","enemies","drops","deep","comment","chasing","brain","beast","base","bald","armours","angry","seller","santa","rebel","honey","holder","himself","hasn't","hasnt","fletching","eyeball","equal","direction","clean","checking","changing","wins","user","shows","select","seeing","scare","reach","older","normally","librarian","herbs","heading","desperate","darkwizards","chiefs","band","act","thy","strange","size","single","shots","paper","ours","loot","filled","extras","elite","compared","beep","basic","threw","spelled","park","music","hut","danger","dance","bounty","winner","wink","timber","survive","sunday","silk","shame","sailor","rainbow","pickaxes","opens","lovely","longs","intruder","hatchets","fact","exchange","especially","during","brave","born","attention","superheat","letting","human","gulp","funky","byebye","wake","thousand","tele","pole","perhaps","original","liked","increases","imcando","favour","dream","destroy","zone","yikes","weakest","trap","thurgo","speaking","restore","race","picking","owned","otherwise","jewels","jewelry","information","furnaces","drank","crying","confuse","vote","stash","plays","pipe","payment","opening","offers","mrs","morgan","messing","material","misunderstand","understood","misunderstood","misheard","magician","lately","heat","gardener","fear","eek","command","changes","alliance","totally","puts","horvik","hobs","hardest","freebies","force","cowboy","correct","collecting","champion","burning","bond","beginner","beautiful","action","task","mistakes","story","slowly","we'll","well","sheriff","osman","newts","moved","loading","laughs","hint","forth","forsale","firemaking","dip","alrighty","whistle","letter","hopefully","generous","fin","eventually","dyes","deadly","collection","coffin","carrying","camel","cake","bloke","adamantine","accidently","woods","waldo","turtle","tiny","tackle","squares","pottery","maze","loaded","lighting","kebab","goods","gather","fired","extreme","epic","early","doubled","climb","brings","appreciate","allowed","allot","zoom","weaklings","warm","visit","unfired","term","smithers","rogue","recently","raising","raised","proof","promised","obtain","mates","matches","leveled","history","herd","hall","guest","goldbar","fermented","expect","dungeons","create","animal","aggie","wanting","ultimate","trapped","spelling","sitting","'scuse","retreat","respect","repay","pressed","pow","mainly","leads","kitchen","gotcha","fund","followed","favorite","cloths","caught","burying","blink","bean","bartender","art","worthy","warlock","uses","spar","smiles","sharp","restless","rangers","questions","precious","options","offense","katrine","interest","hop","hiding","grant","glass","fruit","fireball","eternal","compost","combo","children","chased","challenge","bidder","barrow","amazing","alike","adventure","walls","underground","solve","several","rate","hobgoblin","helper","harlow","graveyard","gates","frost","foe","deeper","darkwizard","cage","beef","baker","whow","wars","turning","tries","talks","sos","shovel","shopping","practicing","pizza","numbers","nowhere","location","instant","icon","hush","hers","happening","guessing","gray","freeze","duke","clock","chestplate","chains","calm","buds","beggar","basically","attend","appears","yard","wk","weakness","trips","treasure","track","towards","somehow","silence","rusty","rocky","reading","pardon","oilcan","noon","mason","lizard","itself","flashy","farmers","discount","deserve","controlled","confusing","comic","cell","brand","boot","bonus","bathroom","asleep","arrgh","angle","whisper","trusted","snipper","shoes","sales","sage","rumble","river","rice","repeat","remove","recover","reasonable","rank","pocket","paste","obviously","nicely","news","journey","hiring","grand","feed","decide","created","colours","buyers","bottle","boat","blacksmith","belong","beauty","assistant","accurate","women's","womens","witches","weakened","travel","teleported","support","stands","sleeping","skate","shout","shake","scrolls","restock","quality","princess","plain","opened","object","monday","meaning","mansion","lesson","learned","knives","focus","fighters","eats","ear","drinking","dizzy","broadsword","bothering","blonde","blame","banker","bakery","armourer","admit","adding","wield","view","vest","thrander","swamps","spun","someday","soldier","servant","raining","paint","omen","limproot","leveling","known","knowing","however","hill","heavy","frank","entire","energy","enchantment","drinks","divide","defend","customer","combination","closing","carlem","buddies","breath","begin","battling","bats","alter","would've","wouldve","worries","spawns","rpg","profitable","products","outlaw","mixed","method","melting","icy","hitpoint","henge","glory","future","fully","eastern","decided","darkness","crossbones","creature","contest","claim","certificates","casting","carrier","bush","buckets","booty","bargain","altar","wash","uninteresting","switches","runner","risk","realize","proper","mountains","merci","likely","fishy","fails","everyday","edge","earned","dragons","docks","coolest","continue","arching","waited","spring","spares","shade","score","restart","regen","purpose","popular","nicer","keeper","jewel","held","heap","grin","goal","foods","figures","fiend","explore","eep","coast","clap","chief","chests","blocks","battles","bash","balance","backwards","animals","alley","wrath","wonderful","usual","unknown","twist","teamed","tanned","sisters","shorts","reporting","regain","print","ppls","posted","poisoned","owns","mention","lunch","lousy","hoping","highwayman","gladiator","fortune","fort","flier","fixed","feast","falls","failing","elf","disguise","dawn","curious","crystal","chances","bothered","armored","acting","accepting","within","various","unlike","unicorns","sunshine","spud","sorts","slay","sight","sighs","shrugs","shortcut","shiny","shearer","safer","sack","route","rooms","reply","regenerates","natures","leading","lair","jokes","jobs","glove","garden","frog","flying","fisherman","fare","excellent","dokie","dock","customers","checked","bottles","bold","bin","bags","assassin","asks","artist","aggressive","advice","adventurer","tunnel","teleporting","suggest","smash","sides","shortsword","recruit","recon","planning","pity","oldest","mirth","march","lease","learning","laws","kingdom","invincible","injured","inv","hopper","ham","graph","gambler","fried","fork","follows","folks","fisher","fields","fastest","excited","dressed","directions","difficult","defeat","crunch","creatures","clothing","closes","circle","cheapest","central","cart","captain","camp","buyer","breakfast","blocking","beans","agreed","bankaccount","worried","windows","weekend","upon","unlock","triple","transferring","themselves","swordsmen","summon","sudden","stayed","slice","singing","qp","penguin","packed","overall","opposite","nearby","mercy","manifest","mails","ladders","hidden","heaven","haunted","gamer","gaining","friendly","friar","females","fancy","eater","direct","chaps","burns","buildings","bringing","brains","blond","blank","beginning","bearded","backpack","avoid","armed","accidentally","wondered","wherever","undead","typo","treat","trained","tools","teams","tale","succeed","stonehenge","spinner","spinach","skip","ship","runestones","returns","regenerating","refill","rarely","note","needing","muhaha","lines","laters","june","joy","ivy","involved","hooray","friday","flat","fishfood","fairly","emergency","defensive","cure","crew","court","chew","chatting","butter","bodies","although","actual","wrote","withdraw","wigs","wave","walks","waits","unenchanted","toast","teaching","stored","stopping","sly","slide","shower","should've","shouldve","shopkeeper","sending","seek","scream","saves","rod","project","powers","patient","ocean","mound","mills","masked","lumby","judge","island","improves","hazy","greetings","giggles","furs","flips","flip","fermenting","famous","falling","easiest","cries","counts","considering","combine","coffee","batch","barbs","allow","warned","vampires","valley","update","unlucky","thieves","that'd","thatd","teeth","teaming","tastes","tails","steps","stays","stair","spoke","specific","smiley","smile","singer","saturday","rush","rolls","require","replace","rent","remind","regret","reckon","pretend","pressing","prepare","pointy","pointless","multiple","loyal","lonely","impressed","hurray","honestly","halt","groups","generals","freezing","facing","example","enabled","elsewhere","effort","draw","donations","disappeared","cuts","cup","could've","couldve","congo","clever","cleric","chair","cabin","buried","brick","boost","bits","banks","available","assistance","armory","aloud","vine","victory","unwanted","trail","toward","tied","thousands","tens","surprised","surprise","stops","staring","northwest","southwest","smaller","slider","rum","rot","risky","restores","respond","respawns","requires","remembered","potter","portrait","passing","oiling","obvious","northeast","nonplayer","newspaper","minion","minds","mile","magics","located","limited","limit","lethal","knots","july","impact","horses","hired","herb","growing","grabbed","gnarly","gains","freedom","forgive","fate","fallen","exist","ease","distracted","crushed","counter","coppers","copperore","convert","christmas","cars","cane","camels","butcher","books","blacks","beware","bend","beggars","awake","attitude","afterwards","voice","tour","tempting","temp","surely","suite","study","stated","squires","sport","spelt","slim","sidekick","shrug","shortbows","shining","shadows","searching","samurai","ruined","rounds","richer","requests","redberrypie","rarest","produce","prepared","pictures","person","peksa","patience","objects","nightmare","mud","minus","minerals","merry","meal","matters","mages","lords","loop","loaf","liquid","lift","lent","knees","intelligence","insist","holds","harbor","handy","guessed","grown","greatest","grabs","grabbing","goodness","glow","firing","filling","feasting","familiar","everlasting","ditch","deposited","demand","cyan","curses","crowd","costume","conversation","contains","consider","completely","colored","circles","ciao","beyond","baked","automatically","arena","answers","amounts","altogether","wreck","worker","wizardshat","warn","warlord","wannabe","vend","trough","tome","teatime","survivor","stranger","stouts","sports","sow","soot","sooner","songs","snore","smilie","slightly","sire","signed","sheet","shack","settle","scout","scientist","runeite","rolling","rise","record","reappear","rating","pairs","nest","nerves","mens","lure","loyalty","literally","harm","handing","genius","fourth","fever","faces","experienced","exceed","employee","distance","disappear","diplomacy","digging","deserved","definitely","current","crimson","crashed","core","condition","complicated","complain","chocolate","chisels","cheer","cheapo","charges","catching","burntmeat","blazing","bids","barren","barrel","auction","attacker","asap","advancing","ability","woman's","womans","wished","waves","wartface","wandering","waist","vulture","valuables","vacation","tunes","tear","tasty","swapping","summit","strikes","stood","sting","sticks","stack","spoken","sole","snap","smelts","slurp","shy","shed","shearing","shape","seagull","sarcastic","sandwich","safety","rotfl","rookie","role","resist","rents","removed","relax","refuse","refined","randomly","prizes","praying","potato","popped","pearl","peaceful","nun","noo","nooo","noooo","mugged","miles","midnight","metals","mask","lowers","lowered","lighter","jumping","jester","jest","include","imprint","impressive","humph","horrible","hooked","hid","healthy","haven","handsome","halves","flow","elemental","earnest","doses","divided","disconnect","digger","demons","deliver","darling","darkside","cutter","counting","cooker","chrome","chopping","charity","breaking","brake","brag","border","bodyguard","billion","barn","baraek","awful","autumn","aside","armorer","areas","answering","answered","alligator","al-kharid","younger","yippy","worm","whining","weather","weaponry","villa","vanilla","truly","trek","transform","tours","tougher","torch","topic","theirs","targets","tables","sweetness","supreme","supper","suggestion","stared","stakes","stacks","squad","spending","speedy","smirks","slower","slayers","sings","services","sept","science","scar","savings","sample","sadly","sabotage","reported","repair","reminds","regeneration","recognize","raid","puzzle","pushing","pushed","powered","polite","pitiful","pace","owed","opponent","o'clock","nag","mystery","modified","moans","maps","luckily","legend","instructions","increased","impatient","iced","hurricane","hilarious","hides","helpless","hearing","halve","guardian","grows","grinder","gifts","ghoul","gabindo","frames","forward","fooling","finishing","explains","eve","debt","deathless","curator","crime","crafted","cotton","controls","constantly","constant","commando","charging","channel","celebrate","brow","breaks","boxes","blinking","blanket","bath","bashing","appeared","affect","absolutely","woodcutters","woodcutter","watches","watched","wander","visual","vision","victor","vast","urban","upset","tribe","transformed","torn","throws","thirsty","tailoring","symbol","swim","surrounded","steam","stealers","staves","starter","speaks","slept","sleepy","skilled","setting","separate","sellers","screaming","scaring","scale","savage","sakes","ridiculous","response","recruiting","rally","rake","prysin","pros","professor","practically","possibly","pits","percent","peas","pale","ordinary","oars","npcs","newer","nearer","mob","mirror","mermaid","melted","marketplace","magicians","loses","listened","lake","kilt","justice","jungle","irritating","innocent","hundreds","humor","halfway","grief","gnome","forced","fletch","extremely","endless","ended","empires","drunken","doomed","destroyed","deserves","crumble","cruise","crossing","courtyard","compass","coat","click","clicks","chasm","chap","capital","cadavaberry","buttons","bulk","borrowing","blushes","beards","assure","anyhow","anger","afternoon","yawns","winter","what'll","whatll","oclock","wears","vip","victim","updated","typos","troll","trivia","timing","they've","theyve","they'd","theyd","terms","tap","swings","swag","sunny","suffer","streets","standard","speaker","soup","sorcerer","society","smooth","slicer","skillful","signs","shortly","shorter","seal","ruler","required","related","ranges","quester","purchase","pulled","potters","posts","position","population","poles","pleased","platinum","planned","pints","picky","patrol","operate","obstacle","obsessed","nod","nifty","monastery","mixing","mint","messes","meaty","managed","longest","lights","knot","keyprint","jackpot","ignoring","ideas","hunted","humble","hollow","hobgobs","hints","hermit","helpful","guarding","growl","graves","grape","goodnight","gasp","freebie","fetch","fakes","explosives","exploring","expert","enchants","directly","deed","crosses","cracks","cracked","coughs","cookers","compliment","competition","commander","combined","collector","claimed","chunk","cellar","cavern","catches","cards","bidding","became","assume","arrived","arrive","appearing","ancient","ambush","amazed","adamant","workers","wisely","when's","whens","unfortunately","underneath","today's","todays","territory","taught","surrender","sty","stubborn","stoop","stocks","spirit","southeast","souls","someplace","smarter","slipped","skele","skeles","sits","session","section","secrets","runesword","riddle","renegades","reasons","realised","pulling","profits","peasant","pattern","package","ourselves","officially","offended","odds","nicest","natural","moves","mizgog","missile","mentioned","medal","mature","matching","massive","lizardman","limits","laid","invented","holiday","highly","hedge","headquarters","hadn't","hadnt","grind","gremlin","grammar","grains","glasses","gladly","ginger","generation","funniest","frosty","follower","flames","farewell","faints","exciting","equals","effect","drill","dresser","disc","curved","creepy","counted","connected","complex","complaining","colorful","chips","chip","chant","certainly","cauldron","carts","carefully","bright","breathe","bragging","boing","blessed","becoming","battleaxes","basics","background","avatar","attire","apologize","yelled","xmas","worlds","wishes","whistles","whichever","whether","weakens","wand","updates","unit","tuesday","troubles","tricky","transferred","thursday","wednesday","throwing","throne","tests","testing","swamped","subtle","stump","stoves","stocking","sticker","station","stall","stabbed","spilt","sphere","speller","specially","spawning","spared","sounded","soldiers","smashed","simply","signing","shaking","sequence","senior","selves","selection","screenshot","scrap","scored","scavengers","scares","runestone","rotate","roof","romeo's","romeos","riches","reveal","retrieve","resources","remaining","recharging","recharged","ratio","rates","rampage","quote","pushes","public","pry","prospecting","propose","properly","process","priests","pricey","prey","plants","planet","pendant","payments","paradise","paladins","packs","ought","ork","ogre","non-pks","nap","mystical","missions","meetings","meanwhile","marching","manners","losses","laughed","kneed","intense","includes","inches","improved","immature","gunthor's","gunthors","guesses","grounds","grins","greeting","gown","gathering","gardening","forgotten","fold","flies","fixing","finest","fiery","ferments","estimate","equipped","enhancing","earning","dyed","dosh","dial","descent","depending","delicious","defeated","decision","december","darker","curiosity","crafters","covered","contain","confusion","compete","compare","cleared","choices","childish","charged","carries","brew","brawl","boats","blend","becomes","banked","baking","bacon","baa","auctioning","attached","ashamed","arguing","argue","aprons","annoyed","ambushed","aboard","fire-rune","water-rune","air-rune","earth-rune","mind-rune","body-rune","life-rune","death-rune","needle","nature-rune","chaos-rune","law-rune","thread","saradomin","unblessed","cosmic-rune","2-handed","burntbread","bad","chef's","chefs","reddye","yellowdye","shell","burntpie","faladian","knight's","knights","asgarnian","wizard's","wizards","mindbomb","rat's","rats","bluedye","unstrung","leaf","orangedye","zamorak","protection","karamja","tomato","incomplete","anchovie","partial","bowl","shrimp","anchovies","sardine","salmon","trout","herring","pike","tuna","swordfish","lobster","harpoon","bait","feather","returning","magenta","plank","tile","muddy","nails","anti","pumpkin","guam","marrentill","tarromin","harralander","ranarr","irit","avantoe","kwuarm","cadantine","unfinished","vial","pestle","mortar","snape","restoration","dramen","branch","hans","urhney","traiborn","rovin","lesser","lessers","greaters","jonny","veronica","weaponsmaster","oddenstein","bat","aubury","lowe","thessalia","zaff","zeke","louie","dr","mr","cassie","ranael","greldo","amik","varze","guildmaster","valaine","drogo","flynn","wyson","hassan","joe","keli","jailguard","redbeard","wydin","brian","vyvin","wayne","barmaid","hetty","betty","bentnoze","herquin","rommik","grum","customs","officer","luthas","zambo","tobias","gerrant","seaman","lorris","thresnor","tanner","dommik","abbot","langley","thordur","jered","melzar","mad","scavvo","greater","oziach","wormbrain","klarense","oracle","druid","baby","kaqemeex","sanfew","leprechaun","entrana","irksol","lunderwin","jakut","doorman","treestump","longtable","gravestone","bench","candles","landscape","millstones","palmtree","fern","cactus","bullrushes","mushroom","railing","pillar","bookcase","chute","sacks","signpost","dolmen","sails","cobweb","spiderweb","doric's","dorics","potter's","potters","crate","fungus","carcass","guthix","thunder","doorframe","railings","battlement","arrowslit","crumbled","strike","blast","burst","clarity","superhuman","reflexes","rapid","incredible","paralyze","missiles","volcano","volcanos","crandor","pier","al","edgeville","prison","gaol","zanaris","isle","lobbies","swordies","lobby","swordy","lava","tomb","crypt","plateau","overgrown","plantation","planks","nail","tiles","lobsters","paralyzes","crates","fungi","bookcases","pillars","railings","mushrooms","signposts","cacti","ferns","benches","gravestones","pumpkins","halloween","feathers","pikes","herrings","sardines","anchovie","bowls","shrimps","trouts","tomatoes","fire-runes","water-runes","air-runes","earth-runes","mind-runes","body-runes","life-runes","death-runes","nature-runes","chaos-runes","law-runes","cosmic-runes","rs","runescape","game","fantasy","world","escape","scape","escaped","cape","capes","caped","mail","belief","believe","imagine","version",":-)",":^)",":)",":-p",":^p",":p","-)","^)",")","-p","^p","p",":-(",":(","-(","(","stupid","silly","daft","crazy","idiot","fool","dumb","thick","moron","nasty","horrid","mean","meanie","unfair","liar","trick","scam","scammer","scamming","stole","stolen","nicked","smell","smelly","stinking","stinker","stink","foul","hate","naff","dislike","rubbish","garbage","trash","terrible","pathetic","miserable","unhappy","sea","multiplayer","control","rewards","advantage","adventuring","spooky","unarmed","requirement","mithral","adamite","pizzas","symbols","saphire","saph","saphs","adamnite","system","clan","count","country","hola","mom","truck","violet","needle","addy","saffire","ammy","cobra","where'd","whered","he'll","nipped","dedicated","someone's","spoon","gang","doughnuts","speed","lag","laggy","technically","capacity","brother","harpoons","bluerose13x","puffin","puffffin","nicodeamus","michelle","pan","war","premium","wolves","strangers","brazil","vancouver","perchance","wicked","panther","she'll","ladykilljoy","arsenes","pennywise","addiction","someones","pony","kalika","clicking","kicking","spencer","unlocked","nightsword","arrowslits","hellhound","hellhounds","ponies","upwards","trenger","druidic","hence","predict","prediction","achetties","clicked","spanish","shapeshifter","shifter","compound","retired","update","everyones","buman","album","puzzles","gain","messenger","analog","van","blessing","convinced","banner","glassblowing","baconer","tytn","query","queries","argument","online","worldpay","tourist","cooky","cert","certs","u","ur","konger","ammies","butch","married","wally","equalizer","ranked","stamping","assasin","assasins","appearance","effects","makeover","typer","bass","doombringer","effective","wildy","wazzup","germany","knocked","shore","titan","pine","pineapple","competition","thomas"};
		byte[] zgb = new byte[200];
		int xgb = 4229;

		int i = 0;
		try
		{
			if (paramString.length() > 80) {
				paramString = paramString.substring(0, 80);
			}
			paramString = paramString.toLowerCase() + " ";
			if (paramString.startsWith("@red@"))
			{
				zgb[(i++)] = -1;
				zgb[(i++)] = 0;
				paramString = paramString.substring(5);
			}
			if (paramString.startsWith("@gre@"))
			{
				zgb[(i++)] = -1;
				zgb[(i++)] = 1;
				paramString = paramString.substring(5);
			}
			if (paramString.startsWith("@blu@"))
			{
				zgb[(i++)] = -1;
				zgb[(i++)] = 2;
				paramString = paramString.substring(5);
			}
			if (paramString.startsWith("@cya@"))
			{
				zgb[(i++)] = -1;
				zgb[(i++)] = 3;
				paramString = paramString.substring(5);
			}
			if (paramString.startsWith("@ran@"))
			{
				zgb[(i++)] = -1;
				zgb[(i++)] = 4;
				paramString = paramString.substring(5);
			}
			if (paramString.startsWith("@whi@"))
			{
				zgb[(i++)] = -1;
				zgb[(i++)] = 5;
				paramString = paramString.substring(5);
			}
			if (paramString.startsWith("@bla@"))
			{
				zgb[(i++)] = -1;
				zgb[(i++)] = 6;
				paramString = paramString.substring(5);
			}
			if (paramString.startsWith("@ora@"))
			{
				zgb[(i++)] = -1;
				zgb[(i++)] = 7;
				paramString = paramString.substring(5);
			}
			if (paramString.startsWith("@yel@"))
			{
				zgb[(i++)] = -1;
				zgb[(i++)] = 8;
				paramString = paramString.substring(5);
			}
			if (paramString.startsWith("@mag@"))
			{
				zgb[(i++)] = -1;
				zgb[(i++)] = 9;
				paramString = paramString.substring(5);
			}
			String str = "";
			for (int j = 0; j < paramString.length(); j++)
			{
				char c = paramString.charAt(j);
				if (((c >= 'a') && (c <= 'z')) || ((c >= '0') && (c <= '9')) || (c == '\''))
				{
					str = str + c;
				}
				else
				{
					int k = xn(c);
					if (str.length() > 0) {
						for (int m = 0; m < xgb; m++) {
							if (str.equals(ygb[m]))
							{
								if ((k == 36) && (m < 165))
								{
									zgb[(i++)] = ((byte)(m + 90));
									k = -1;
								}
								else if (k == 36)
								{
									zgb[(i++)] = ((byte)(m / 256 + 50));
									zgb[(i++)] = ((byte)(m & 0xFF));
									k = -1;
								}
								else
								{
									zgb[(i++)] = ((byte)(m / 256 + 70));
									zgb[(i++)] = ((byte)(m & 0xFF));
								}
								str = "";
								break;
							}
						}
					}
					for (int m = 0; m < str.length(); m++) {
						zgb[(i++)] = ((byte)xn(str.charAt(m)));
					}
					str = "";
					if ((k != -1) && (j < paramString.length() - 1)) {
						zgb[(i++)] = ((byte)k);
					}
				}
			}
		}
		catch (Exception localException) {}

		return Arrays.copyOf(zgb, i);
	}

	private static int xn(char paramChar)
	{
		if ((paramChar >= 'a') && (paramChar <= 'z')) {
			return paramChar - 'a';
		}
		if ((paramChar >= '0') && (paramChar <= '9')) {
			return paramChar + '\032' - 48;
		}
		if (paramChar == ' ') {
			return 36;
		}
		if (paramChar == '!') {
			return 37;
		}
		if (paramChar == '?') {
			return 38;
		}
		if (paramChar == '.') {
			return 39;
		}
		if (paramChar == ',') {
			return 40;
		}
		if (paramChar == ':') {
			return 41;
		}
		if (paramChar == ';') {
			return 42;
		}
		if (paramChar == '(') {
			return 43;
		}
		if (paramChar == ')') {
			return 44;
		}
		if (paramChar == '-') {
			return 45;
		}
		if (paramChar == '&') {
			return 46;
		}
		if (paramChar == '*') {
			return 47;
		}
		if (paramChar == '\\') {
			return 48;
		}
		if (paramChar == '\'') {
			return 49;
		}
		return 36;
	}
}
