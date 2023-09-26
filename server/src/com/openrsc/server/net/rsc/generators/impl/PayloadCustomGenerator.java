package com.openrsc.server.net.rsc.generators.impl;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.external.GameObjectLoc;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Custom RSC Protocol Generator for Outgoing Packets from respective Protocol Independent Structs
 * **/
public class PayloadCustomGenerator implements PayloadGenerator<OpcodeOut> {
	private static final Map<OpcodeOut, Integer> opcodeMap = new HashMap<OpcodeOut, Integer>() {{
		put(OpcodeOut.SEND_LOGOUT_REQUEST_CONFIRM, 4);
		put(OpcodeOut.SEND_QUESTS, 5);
		put(OpcodeOut.SEND_DUEL_OPPONENTS_ITEMS, 6);
		put(OpcodeOut.SEND_TRADE_ACCEPTED, 15);
		put(OpcodeOut.SEND_SERVER_CONFIGS, 19); // custom
		put(OpcodeOut.SEND_TRADE_OPEN_CONFIRM, 20);
		put(OpcodeOut.SEND_WORLD_INFO, 25);
		put(OpcodeOut.SEND_DUEL_SETTINGS, 30);
		put(OpcodeOut.SEND_EXPERIENCE, 33);
		put(OpcodeOut.SEND_EXPERIENCE_TOGGLE, 34); // custom
		put(OpcodeOut.SEND_BUBBLE, 36); // used for teleport, telegrab, and iban's magic
		put(OpcodeOut.SEND_BANK_OPEN, 42);
		put(OpcodeOut.SEND_SCENERY_HANDLER, 48);
		put(OpcodeOut.SEND_PRIVACY_SETTINGS, 51);
		put(OpcodeOut.SEND_SYSTEM_UPDATE, 52);
		put(OpcodeOut.SEND_INVENTORY, 53);
		put(OpcodeOut.SEND_ELIXIR, 54); // custom
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
		put(OpcodeOut.SEND_EXPSHARED, 98); // custom
		put(OpcodeOut.SEND_GROUND_ITEM_HANDLER, 99);
		put(OpcodeOut.SEND_SHOP_OPEN, 101);
		put(OpcodeOut.SEND_UPDATE_NPC, 104);
		put(OpcodeOut.SEND_IGNORE_LIST, 109);
		put(OpcodeOut.SEND_INPUT_BOX, 110); // custom
		put(OpcodeOut.SEND_ON_TUTORIAL, 111);
		put(OpcodeOut.SEND_CLAN, 112); // custom
		put(OpcodeOut.SEND_CLAN_LIST, 112); // custom - shares opcode currently should be changed in future
		put(OpcodeOut.SEND_CLAN_SETTINGS, 112); // custom
		put(OpcodeOut.SEND_IRONMAN, 113); // custom
		put(OpcodeOut.SEND_FATIGUE, 114);
		put(OpcodeOut.SEND_ON_BLACK_HOLE, 115); // custom
		put(OpcodeOut.SEND_PARTY, 116); // custom
		put(OpcodeOut.SEND_PARTY_LIST, 116); // custom - shares opcode currently should be changed in future
		put(OpcodeOut.SEND_PARTY_SETTINGS, 116); // custom
		put(OpcodeOut.SEND_SLEEPSCREEN, 117);
		put(OpcodeOut.SEND_KILL_ANNOUNCEMENT, 118); // custom
		put(OpcodeOut.SEND_PRIVATE_MESSAGE, 120);
		put(OpcodeOut.SEND_INVENTORY_REMOVE_ITEM, 123);
		put(OpcodeOut.SEND_TRADE_CLOSE, 128);
		put(OpcodeOut.SEND_COMBAT_STYLE, 129); // custom
		put(OpcodeOut.SEND_SERVER_MESSAGE, 131);
		put(OpcodeOut.SEND_AUCTION_PROGRESS, 132); // custom
		put(OpcodeOut.SEND_FISHING_TRAWLER, 133); // custom
		put(OpcodeOut.SEND_STATUS_PROGRESS_BAR, 134); // custom, formerly separated labeled progress, update, remove
		put(OpcodeOut.SEND_BANK_PIN_INTERFACE, 135); // custom
		put(OpcodeOut.SEND_ONLINE_LIST, 136); // custom
		put(OpcodeOut.SEND_SHOP_CLOSE, 137);
		put(OpcodeOut.SEND_OPENPK_POINTS_TO_GP_RATIO, 144); // custom
		put(OpcodeOut.SEND_NPC_KILLS, 147); // custom
		put(OpcodeOut.SEND_OPENPK_POINTS, 148); // custom
		put(OpcodeOut.SEND_FRIEND_UPDATE, 149);
		put(OpcodeOut.SEND_BANK_PRESET, 150); // custom
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
		put(OpcodeOut.SEND_UNLOCKED_APPEARANCES, 250);
		put(OpcodeOut.SEND_OPTIONS_MENU_CLOSE, 252);
		put(OpcodeOut.SEND_DUEL_OTHER_ACCEPTED, 253);
		put(OpcodeOut.SEND_EQUIPMENT, 254); // custom
		put(OpcodeOut.SEND_EQUIPMENT_UPDATE, 255); // custom
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

				case SEND_SERVER_CONFIGS:
					ServerConfigsStruct sc = (ServerConfigsStruct) payload;
					for (Object entry : sc.configs) {
						if (entry instanceof Byte) {
							builder.writeByte((Byte) entry);
						} else if (entry instanceof String) {
							builder.writeString((String) entry);
						}
					}
					break;

				case SEND_INPUT_BOX:
					InputBoxStruct ib = (InputBoxStruct) payload;
					builder.writeString(ib.messagePrompt);
					break;

				case SEND_BOX:
				case SEND_BOX2:
					MessageBoxStruct mb = (MessageBoxStruct) payload;
					String message = mb.message;
					builder.writeString(message);
					break;

				case SEND_OPTIONS_MENU_OPEN:
					MenuOptionStruct mo = (MenuOptionStruct) payload;
					int numOptions = mo.numOptions;
					builder.writeByte((byte) numOptions);
					for (int i = 0; i < numOptions; i++){
						builder.writeString(mo.optionTexts[i]);
					}
					break;

				case SEND_ON_TUTORIAL:
					PlayerOnTutorialStruct playOnTut = (PlayerOnTutorialStruct) payload;
					builder.writeByte((byte) playOnTut.onTutorial);
					break;

				case SEND_ON_BLACK_HOLE:
					PlayerOnBlackHoleStruct playOnBH = (PlayerOnBlackHoleStruct) payload;
					builder.writeByte((byte) playOnBH.onBlackHole);
					break;

				case SEND_SYSTEM_UPDATE:
					SystemUpdateStruct su = (SystemUpdateStruct) payload;
					builder.writeShort((int) (((double) su.seconds / 32D) * 50));
					break;

				case SEND_ELIXIR:
					ElixirUpdateStruct eu = (ElixirUpdateStruct) payload;
					builder.writeShort((int) (((double) eu.timeSeconds / 32D) * 50));
					break;

				case SEND_STATS:
					StatInfoStruct si = (StatInfoStruct) payload;
					// 18 skills minimum - current level
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
					if (player.getConfig().WANT_RUNECRAFT) {
						builder.writeByte((byte) si.currentRunecrafting);
					}
					if (player.getConfig().WANT_HARVESTING) {
						builder.writeByte((byte) si.currentHarvesting);
					}

					// 18 skills minimum - max level
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
					if (player.getConfig().WANT_RUNECRAFT) {
						builder.writeByte((byte) si.maxRunecrafting);
					}
					if (player.getConfig().WANT_HARVESTING) {
						builder.writeByte((byte) si.maxHarvesting);
					}

					// 18 skills minimum - experiences
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
					if (player.getConfig().WANT_RUNECRAFT) {
						builder.writeInt(si.experienceRunecrafting);
					}
					if (player.getConfig().WANT_HARVESTING) {
						builder.writeInt(si.experienceHarvesting);
					}

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

				case SEND_EXPERIENCE_TOGGLE:
					ExperienceToggleStruct ext = (ExperienceToggleStruct) payload;
					builder.writeByte((byte) ext.isExperienceFrozen);
					break;

				case SEND_EXPSHARED:
					ExpSharedStruct exps = (ExpSharedStruct) payload;
					builder.writeShort(exps.value);
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
					if (qi.isUpdate != 1) {
						builder.writeByte((byte) qi.isUpdate); // send all quests
						int numQuests = qi.numberOfQuests;
						builder.writeByte((byte) numQuests);
						for (int i = 0; i < numQuests; i++) {
							builder.writeInt(qi.questId[i]);
							builder.writeInt(qi.questStage[i]);
							builder.writeString(qi.questName[i]);
						}
					} else {
						builder.writeByte((byte) qi.isUpdate); // is specific quest update
						builder.writeInt(qi.questId[0]);
						builder.writeInt(qi.questStage[0]);
					}
					break;

				case SEND_COMBAT_STYLE:
					CombatStyleStruct cs = (CombatStyleStruct) payload;
					builder.writeByte((byte) cs.combatStyle);
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
					builder.writeShort(fs.serverFatigue / (player.MAX_FATIGUE / 100)); // backwards compatible with old custom clients
					builder.writeShort(fs.serverFatigue / (player.MAX_FATIGUE / 750)); // authentic higher precision, if client wants to read it
					break;

				case SEND_PLAY_SOUND:
					PlaySoundStruct pls = (PlaySoundStruct) payload;
					builder.writeString(pls.soundName);
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
					for (int customOpt : gs.customOptions) {
						builder.writeByte((byte) customOpt);
					}
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
					builder.writeString(tc.targetPlayer);
					int tradedItemSize = tc.opponentTradeCount;
					builder.writeByte((byte) tradedItemSize);
					for (int i = 0; i < tradedItemSize; i++) {
						builder.writeShort(tc.opponentCatalogIDs[i]);
						if (tc.opponentNoted != null) {
							// world accepts notes
							builder.writeByte((byte) tc.opponentNoted[i]);
						}
						builder.writeInt(tc.opponentAmounts[i]);
					}
					tradedItemSize = tc.myCount;
					builder.writeByte((byte) tradedItemSize);
					for (int i = 0; i < tradedItemSize; i++) {
						builder.writeShort(tc.myCatalogIDs[i]);
						if (tc.myNoted != null) {
							// world accepts notes
							builder.writeByte((byte) tc.myNoted[i]);
						}
						builder.writeInt(tc.myAmounts[i]);
					}
					break;

				case SEND_TRADE_OTHER_ITEMS:
					TradeTransactionStruct tt = (TradeTransactionStruct) payload;
					int tradeCount = tt.opponentTradeCount;
					builder.writeByte((byte) tradeCount);
					for (int i = 0; i < tradeCount; i++) {
						builder.writeShort(tt.opponentCatalogIDs[i]);
						if (tt.opponentNoted != null) {
							// world accepts notes
							builder.writeByte((byte) tt.opponentNoted[i]);
						}
						builder.writeInt(tt.opponentAmounts[i]);
					}
					tradeCount = tt.myCount;
					builder.writeByte((byte) tradeCount);
					for (int i = 0; i < tradeCount; i++) {
						builder.writeShort(tt.myCatalogIDs[i]);
						if (tt.myNoted != null) {
							// world accepts notes
							builder.writeByte((byte) tt.myNoted[i]);
						}
						builder.writeInt(tt.myAmounts[i]);
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
						if (ds.noted != null) {
							// world accepts notes
							builder.writeByte((byte) ds.noted[i]);
						}
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
					builder.writeString(dc.targetPlayer);
					int stakedItemSize = dc.opponentDuelCount;
					builder.writeByte((byte) stakedItemSize);
					for (int i = 0; i < stakedItemSize; i++) {
						builder.writeShort(dc.opponentCatalogIDs[i]);
						if (dc.opponentNoted != null) {
							// world accepts notes
							builder.writeByte((byte) dc.opponentNoted[i]);
						}
						builder.writeInt(dc.opponentAmounts[i]);
					}
					stakedItemSize = dc.myCount;
					builder.writeByte((byte) stakedItemSize);
					for (int i = 0; i < stakedItemSize; i++) {
						builder.writeShort(dc.myCatalogIDs[i]);
						if (dc.myNoted != null) {
							// world accepts notes
							builder.writeByte((byte) dc.myNoted[i]);
						}
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
					builder.writeString(fr.name);
					builder.writeString(fr.formerName);
					builder.writeByte((byte) fr.onlineStatus);
					if (!fr.worldName.equals(""))
						builder.writeString(fr.worldName);
					break;

				case SEND_IGNORE_LIST:
					IgnoreListStruct il = (IgnoreListStruct) payload;
					int ignoreSize = il.listSize;
					builder.writeByte((byte) ignoreSize);
					for (int i = 0; i < ignoreSize; i++) {
						builder.writeString(il.name[i]);
						builder.writeString(il.name[i]);
						builder.writeString(il.formerName[i]);
						builder.writeString(il.formerName[i]);
					}
					break;

				case SEND_UPDATE_IGNORE_LIST_BECAUSE_NAME_CHANGE:
					IgnoreListStruct uil = (IgnoreListStruct) payload;
					builder.writeString(uil.name[0]);
					builder.writeString(uil.name[0]);
					builder.writeString(uil.formerName[0]);
					builder.writeString(uil.formerName[0]);
					builder.writeByte((byte)(uil.updateExisting ? 1 : 0));
					break;

				case SEND_INVENTORY:
					InventoryStruct is = (InventoryStruct) payload;
					int inventorySize = is.inventorySize;
					builder.writeByte((byte) inventorySize);
					for (int i = 0; i < inventorySize; i++) {
						builder.writeShort(is.catalogIDs[i]);
						builder.writeByte((byte) is.wielded[i]);
						builder.writeByte((byte) is.noted[i]);
						// amount[i] will only be > 0 if the item is stackable or noted.
						if (is.amount[i] > 0) {
							builder.writeInt(is.amount[i]);
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
						builder.writeByte((byte) iup.noted);
						// amount will only be > 0 if the item is stackable or noted
						if (iup.amount > 0) {
							builder.writeInt(iup.amount);
						}
					} else {
						builder.writeShort(0);
						builder.writeShort(0);
						builder.writeInt(0);
					}
					break;

				case SEND_EQUIPMENT:
					EquipmentStruct eqs = (EquipmentStruct) payload;
					builder.writeByte((byte) eqs.equipmentCount);
					for (int i = 0; i < eqs.realCount; i++) {
						builder.writeByte((byte) eqs.wieldPositions[i]);
						builder.writeShort(eqs.catalogIDs[i]);
						// amount[i] will only be > 0 if the item is stackable.
						if (eqs.amount[i] > 0)
							builder.writeInt(eqs.amount[i]);
					}
					break;

				case SEND_EQUIPMENT_UPDATE:
					EquipmentUpdateStruct equ = (EquipmentUpdateStruct) payload;
					builder.writeByte((byte) equ.slotIndex);
					if (equ.catalogID != 0xFFFF) {
						builder.writeShort(equ.catalogID);
						if (equ.amount > 0)
							builder.writeInt(equ.amount);
					} else {
						builder.writeShort(equ.catalogID);
					}
					break;

				case SEND_BANK_PIN_INTERFACE:
					BankPinStruct bp = (BankPinStruct) payload;
					builder.writeByte((byte) bp.isOpen);
					break;

				case SEND_BANK_OPEN:
					BankStruct b = (BankStruct) payload;
					int storedSize = b.itemsStoredSize;
					int maxBankSize = b.maxBankSize;
					builder.writeShort(storedSize);
					builder.writeShort(maxBankSize);
					for (int i = 0; i < storedSize; i++) {
						builder.writeShort(b.catalogIDs[i]);
						builder.writeInt(b.amount[i]);
					}
					break;

				case SEND_BANK_UPDATE:
					BankUpdateStruct bu = (BankUpdateStruct) payload;
					builder.writeByte((byte) bu.slot);
					builder.writeShort(bu.catalogID);
					builder.writeInt(bu.amount);
					break;

				case SEND_BANK_PRESET:
					BankPresetStruct bps = (BankPresetStruct) payload;
					builder.writeShort(bps.slotIndex);
					int amt;
					for (Object item : bps.inventoryItems) {
						if (!(item instanceof Item)) {
							builder.writeByte(ItemId.NOTHING.id());
						} else {
							builder.writeShort(((Item) item).getCatalogId());
							builder.writeByte(((Item) item).getNoted() ? 1 : 0);
							// amount should only exist if stackable or noted here
							amt = ((Item) item).getAmount();
							if (amt > 0) {
								builder.writeInt(amt);
							}
						}
					}
					for (Object item : bps.equipmentItems) {
						if (!(item instanceof Item)) {
							builder.writeByte(ItemId.NOTHING.id());
						} else {
							builder.writeShort(((Item) item).getCatalogId());
							amt = ((Item) item).getAmount();
							if (amt > 0) {
								builder.writeInt(amt);
							}
						}
					}
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

				case SEND_AUCTION_PROGRESS: // TODO: unused?
					AuctionProgressStruct ap = (AuctionProgressStruct) payload;
					builder.writeByte((byte) ap.interfaceId);
					builder.writeByte((byte) ap.delay);
					builder.writeByte((byte) ap.timesRepeat);
					break;

				case SEND_STATUS_PROGRESS_BAR:
					ProgressBarStruct pb = (ProgressBarStruct) payload;
					builder.writeByte((byte) pb.interfaceId); // 1 = show, 2 = hide, 3 = update
					if (pb.interfaceId > 0 && pb.interfaceId != 2) {
						if (pb.interfaceId == 1) {
							builder.writeShort(pb.delay);
						}
						builder.writeByte((byte) pb.timesRepeat);
					}
					break;

				case SEND_IRONMAN:
					IronManStruct im = (IronManStruct) payload;
					builder.writeByte((byte) im.interfaceId);
					builder.writeByte((byte) im.actionId); //0 = info, 1 = show, 2 = hide
					if (im.actionId == 0) {
						// additional details
						builder.writeByte((byte) im.ironmanType);
						builder.writeByte((byte) im.ironmanRestriction);
					}
					break;

				case SEND_FISHING_TRAWLER:
					TrawlerUpdateStruct tu = (TrawlerUpdateStruct) payload;
					builder.writeByte((byte) tu.interfaceId);
					builder.writeByte((byte) tu.actionId); //0 = show, 1 = info, 2 = hide
					if (tu.actionId == 1) {
						// additional details
						builder.writeShort(tu.waterLevel);
						builder.writeShort(tu.fishCaught);
						builder.writeByte((byte) tu.minutesLeft);
						builder.writeByte((byte) tu.isNetBroken);
					}
					break;

				case SEND_SERVER_MESSAGE:
					MessageStruct m = (MessageStruct) payload;
					builder.writeInt(m.iconSprite);
					builder.writeByte((byte) m.messageTypeRsId);
					int infoContained = m.infoContained;
					builder.writeByte((byte) infoContained);
					builder.writeString(m.message);
					if ((infoContained & 1) != 0) {
						builder.writeString(m.senderName);
						builder.writeString(m.senderName); // This is authentic; all recorded instances it's just the same username twice.
					}
					if ((infoContained & 2) != 0) {
						builder.writeString(m.colorString);
					}
					break;

				case SEND_PRIVATE_MESSAGE:
					PrivateMessageStruct pm = (PrivateMessageStruct) payload;
					builder.writeString(pm.playerName);
					builder.writeString(pm.formerName);
					builder.writeInt(pm.iconSprite);
					builder.writeRSCString(pm.message);
					break;

				case SEND_PRIVATE_MESSAGE_SENT:
					PrivateMessageStruct pm1 = (PrivateMessageStruct) payload;
					builder.writeString(pm1.playerName);
					builder.writeRSCString(pm1.message);
					break;

				case SEND_KILL_ANNOUNCEMENT:
					KillUpdateStruct kl = (KillUpdateStruct) payload;
					builder.writeString(kl.victim);
					builder.writeString(kl.attacker);
					builder.writeInt(kl.killType);
					break;

				case SEND_NPC_KILLS:
					MobKillsStruct mk = (MobKillsStruct) payload;
					builder.writeInt(mk.totalCount);
					builder.writeInt(mk.recentNpcId);
					builder.writeInt(mk.recentNpcKills);
					break;

				case SEND_OPENPK_POINTS:
					PointsStruct points = (PointsStruct) payload;
					builder.writeLong(points.amount);
					break;

				case SEND_ONLINE_LIST:
					OnlineListStruct ol = (OnlineListStruct) payload;
					builder.writeShort(ol.numberOnline);
					int playerCount = ol.playerCount;
					for (int i = 0; i < playerCount; i++) {
						builder.writeString(ol.name[i]);
						builder.writeInt(ol.icon[i]);
						builder.writeString(ol.location[i]);
					}
					break;

				case SEND_WELCOME_INFO:
					WelcomeInfoStruct sw = (WelcomeInfoStruct) payload;
					builder.writeString(sw.lastIp);
					builder.writeShort(sw.daysSinceLogin);
					int daysSinceRecoveryChange = sw.daysSinceRecoveryChange;
					if (daysSinceRecoveryChange >= 0 && daysSinceRecoveryChange < 14) {
						//in rsc175 and earlier was sent days till activation (14 - days since change request)
						builder.writeShort(14 - daysSinceRecoveryChange);
					} else {
						builder.writeShort(0);
					}
					//builder.writeShort(sw.unreadMessages); // info not used on custom
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
							builder.writeAppearanceByte((byte) value, player.getClientVersion());
						} else if (entry instanceof String) {
							builder.writeString((String) entry);
						}
					}
					break;

				case SEND_SCENERY_HANDLER:
				case SEND_BOUNDARY_HANDLER:
					GameObjectsUpdateStruct go = (GameObjectsUpdateStruct) payload;
					for (GameObjectLoc objectLoc : go.objects) {
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
						if (player.getConfig().WANT_BANK_NOTES)
							builder.writeByte((byte) it.getNoted());
					}
					break;

				case SEND_REMOVE_WORLD_ENTITY:
					ClearLocationsStruct cl = (ClearLocationsStruct) payload;
					for (Point point : cl.points) {
						builder.writeShort(point.getX());
						builder.writeShort(point.getY());
					}
					break;

				case SEND_CLAN:
					ClanStruct cls = (ClanStruct) payload;
					// 0 = create, 1 = leave, 2 = invite
					builder.writeByte((byte) cls.actionId);
					if (cls.actionId == 0) {
						builder.writeString(cls.clanName);
						builder.writeString(cls.clanTag);
						builder.writeString(cls.leaderName);
						builder.writeByte((byte) cls.isLeader);
						builder.writeByte((byte) cls.clanSize);
						for (int i = 0; i < cls.clanSize; i++) {
							builder.writeString(cls.clanMembers[i]);
							builder.writeByte((byte) cls.memberRanks[i]);
							builder.writeByte((byte) cls.isMemberOnline[i]);
						}
					} else if (cls.actionId == 2) {
						builder.writeString(cls.nameInviter);
						builder.writeString(cls.clanName);
					}
					break;

				case SEND_CLAN_LIST:
					ClanListStruct clls = (ClanListStruct) payload;
					builder.writeByte((byte) clls.actionId);
					builder.writeShort(clls.totalClans);
					for (int i = 0; i < clls.totalClans; i++) {
						builder.writeShort(clls.clansInfo[i].clanId);
						builder.writeString(clls.clansInfo[i].clanName);
						builder.writeString(clls.clansInfo[i].clanTag);
						builder.writeByte((byte) clls.clansInfo[i].clanSize);
						builder.writeByte((byte) clls.clansInfo[i].allowsSearchedJoin);
						builder.writeInt(clls.clansInfo[i].clanPoints);
						builder.writeShort(i + 1);
					}
					break;

				case SEND_CLAN_SETTINGS:
					ClanSettingsStruct css = (ClanSettingsStruct) payload;
					builder.writeByte((byte) css.magicNumber);
					builder.writeByte((byte) css.kickSetting);
					builder.writeByte((byte) css.inviteSetting);
					builder.writeByte((byte) css.allowSearchJoin);
					builder.writeByte((byte) css.allowSetting0);
					builder.writeByte((byte) css.allowSetting1);
					break;

				case SEND_PARTY:
					PartyStruct pst = (PartyStruct) payload;
					// 0 = create, 1 = leave, 2 = invite
					builder.writeByte((byte) pst.actionId);
					if (pst.actionId == 0) {
						builder.writeString(pst.leaderName);
						builder.writeByte((byte) pst.isLeader);
						builder.writeByte((byte) pst.partySize);
						for (int i = 0; i < pst.partySize; i++) {
							builder.writeString(pst.partyMembers[i]);
							builder.writeByte((byte) pst.memberRanks[i]);
							builder.writeByte((byte) pst.isMemberOnline[i]);
							builder.writeByte((byte) pst.currentHitsMembers[i]);
							builder.writeByte((byte) pst.maximumHitsMembers[i]);
							builder.writeByte((byte) pst.combatLevelsMembers[i]);
							builder.writeByte((byte) pst.isMemberSkulled[i]);
							builder.writeByte((byte) pst.isMemberDead[i]);
							builder.writeByte((byte) pst.isShareLoot[i]);
							builder.writeByte((byte) pst.partyMemberTotal[i]); //total level?
							builder.writeByte((byte) pst.isInCombat[i]);
							builder.writeByte((byte) pst.shareExp[i]);
							builder.writeLong(pst.shareExp2[i]);
						}
					} else if (pst.actionId == 2) {
						builder.writeString(pst.nameInviter);
						builder.writeString(pst.partyName);
					}
					break;

				case SEND_PARTY_LIST:
					PartyListStruct plst = (PartyListStruct) payload;
					builder.writeByte((byte) plst.actionId);
					builder.writeShort(plst.totalParties);
					for (int i = 0; i < plst.totalParties; i++) {
						builder.writeShort(plst.partyInfo[i].partyId);
						builder.writeByte((byte) plst.partyInfo[i].partySize);
						builder.writeByte((byte) plst.partyInfo[i].allowsSearchedJoin);
						builder.writeInt(plst.partyInfo[i].partyPoints);
						builder.writeShort(i + 1);
					}
					break;

				case SEND_PARTY_SETTINGS:
					PartySettingsStruct pss = (PartySettingsStruct) payload;
					builder.writeByte((byte) pss.magicNumber);
					builder.writeByte((byte) pss.kickSetting);
					builder.writeByte((byte) pss.inviteSetting);
					builder.writeByte((byte) pss.allowSearchJoin);
					builder.writeByte((byte) pss.allowSetting0);
					builder.writeByte((byte) pss.allowSetting1);
					break;

				case SEND_UNLOCKED_APPEARANCES:
					UnlockedAppearancesStruct uas = (UnlockedAppearancesStruct) payload;

					builder.writeInt(uas.unlockedHairStyles.length);
					builder.writeInt(uas.unlockedBodyTypes.length);
					builder.writeInt(uas.unlockedSkinColours.length);
					builder.writeInt(uas.unlockedHairColours.length);
					builder.writeInt(uas.unlockedTopColours.length);
					builder.writeInt(uas.unlockedBottomColours.length);

					builder.startBitAccess();
					for (int i = 0; i < uas.unlockedHairStyles.length; i++) {
						builder.writeBits(uas.unlockedHairStyles[i] ? 1 : 0, 1);
					}
					for (int i = 0; i < uas.unlockedBodyTypes.length; i++) {
						builder.writeBits(uas.unlockedBodyTypes[i] ? 1 : 0, 1);
					}
					for (int i = 0; i < uas.unlockedSkinColours.length; i++) {
						builder.writeBits(uas.unlockedSkinColours[i] ? 1 : 0, 1);
					}
					for (int i = 0; i < uas.unlockedHairColours.length; i++) {
						builder.writeBits(uas.unlockedHairColours[i] ? 1 : 0, 1);
					}
					for (int i = 0; i < uas.unlockedTopColours.length; i++) {
						builder.writeBits(uas.unlockedTopColours[i] ? 1 : 0, 1);
					}
					for (int i = 0; i < uas.unlockedBottomColours.length; i++) {
						builder.writeBits(uas.unlockedBottomColours[i] ? 1 : 0, 1);
					}
					builder.finishBitAccess();
					break;
			}
		}

		return builder != null ? builder.toPacket() : null;
	}
}
