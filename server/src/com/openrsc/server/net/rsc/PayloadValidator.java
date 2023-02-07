package com.openrsc.server.net.rsc;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;
import com.openrsc.server.net.rsc.struct.outgoing.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Validator class to check if class is correct (expected) instance for the Opcode
 */
public class PayloadValidator {

	private static final Map<OpcodeOut, Class<? extends AbstractStruct<OpcodeOut>>> opcodeValidate = new HashMap<OpcodeOut, Class<? extends AbstractStruct<OpcodeOut>>>() {{
		put(OpcodeOut.SEND_LOGOUT_REQUEST_CONFIRM, NoPayloadStruct.class);
		put(OpcodeOut.SEND_QUESTS, QuestInfoStruct.class);
		put(OpcodeOut.SEND_DUEL_OPPONENTS_ITEMS, DuelStakeStruct.class);
		put(OpcodeOut.SEND_TRADE_ACCEPTED, TradeAcceptStruct.class);
		put(OpcodeOut.SEND_SERVER_CONFIGS, ServerConfigsStruct.class); // custom
		put(OpcodeOut.SEND_TRADE_OPEN_CONFIRM, TradeConfirmStruct.class);
		put(OpcodeOut.SEND_WORLD_INFO, WorldInfoStruct.class);
		put(OpcodeOut.SEND_DUEL_SETTINGS, DuelSettingsStruct.class);
		put(OpcodeOut.SEND_EXPERIENCE, ExperienceStruct.class);
		put(OpcodeOut.SEND_EXPERIENCE_TOGGLE, ExperienceToggleStruct.class); // custom
		put(OpcodeOut.SEND_BUBBLE, TeleBubbleStruct.class);
		put(OpcodeOut.SEND_BANK_OPEN, BankStruct.class);
		put(OpcodeOut.SEND_SCENERY_HANDLER, GameObjectsUpdateStruct.class);
		put(OpcodeOut.SEND_PRIVACY_SETTINGS, PrivacySettingsStruct.class);
		put(OpcodeOut.SEND_SYSTEM_UPDATE, SystemUpdateStruct.class);
		put(OpcodeOut.SEND_INVENTORY, InventoryStruct.class);
		put(OpcodeOut.SEND_ELIXIR, ElixirUpdateStruct.class); // custom
		put(OpcodeOut.SEND_APPEARANCE_SCREEN, NoPayloadStruct.class);
		put(OpcodeOut.SEND_NPC_COORDS, MobsUpdateStruct.class);
		put(OpcodeOut.SEND_DEATH, NoPayloadStruct.class);
		put(OpcodeOut.SEND_STOPSLEEP, NoPayloadStruct.class);
		put(OpcodeOut.SEND_PRIVATE_MESSAGE_SENT, PrivateMessageStruct.class);
		put(OpcodeOut.SEND_BOX2, MessageBoxStruct.class);
		put(OpcodeOut.SEND_INVENTORY_UPDATEITEM, InventoryUpdateStruct.class);
		put(OpcodeOut.SEND_BOUNDARY_HANDLER, GameObjectsUpdateStruct.class);
		put(OpcodeOut.SEND_TRADE_WINDOW, TradeShowWindowStruct.class);
		put(OpcodeOut.SEND_TRADE_OTHER_ITEMS, TradeTransactionStruct.class);
		put(OpcodeOut.SEND_EXPSHARED, ExpSharedStruct.class); // custom
		put(OpcodeOut.SEND_GROUND_ITEM_HANDLER, GroundItemsUpdateStruct.class);
		put(OpcodeOut.SEND_SHOP_OPEN, ShopStruct.class);
		put(OpcodeOut.SEND_UPDATE_NPC, AppearanceUpdateStruct.class);
		put(OpcodeOut.SEND_IGNORE_LIST, IgnoreListStruct.class);
		put(OpcodeOut.SEND_INPUT_BOX, InputBoxStruct.class); // custom
		put(OpcodeOut.SEND_ON_TUTORIAL, PlayerOnTutorialStruct.class);
		put(OpcodeOut.SEND_CLAN, ClanStruct.class); // custom
		put(OpcodeOut.SEND_CLAN_LIST, ClanListStruct.class); // custom
		put(OpcodeOut.SEND_CLAN_SETTINGS, ClanSettingsStruct.class); // custom
		put(OpcodeOut.SEND_IRONMAN, IronManStruct.class); // custom
		put(OpcodeOut.SEND_FATIGUE, FatigueStruct.class);
		put(OpcodeOut.SEND_ON_BLACK_HOLE, PlayerOnBlackHoleStruct.class); // custom
		put(OpcodeOut.SEND_PARTY, PartyStruct.class); // custom
		put(OpcodeOut.SEND_PARTY_LIST, PartyListStruct.class); // custom
		put(OpcodeOut.SEND_PARTY_SETTINGS, PartySettingsStruct.class); // custom
		put(OpcodeOut.SEND_SLEEPSCREEN, SleepScreenStruct.class);
		put(OpcodeOut.SEND_KILL_ANNOUNCEMENT, KillUpdateStruct.class); // custom
		put(OpcodeOut.SEND_PRIVATE_MESSAGE, PrivateMessageStruct.class);
		put(OpcodeOut.SEND_INVENTORY_REMOVE_ITEM, InventoryUpdateStruct.class);
		put(OpcodeOut.SEND_TRADE_CLOSE, NoPayloadStruct.class);
		put(OpcodeOut.SEND_COMBAT_STYLE, CombatStyleStruct.class); // custom
		put(OpcodeOut.SEND_SERVER_MESSAGE, MessageStruct.class);
		put(OpcodeOut.SEND_AUCTION_PROGRESS, AuctionProgressStruct.class); // custom - not referenced currently
		put(OpcodeOut.SEND_FISHING_TRAWLER, TrawlerUpdateStruct.class); // custom
		put(OpcodeOut.SEND_STATUS_PROGRESS_BAR, ProgressBarStruct.class); // custom
		put(OpcodeOut.SEND_BANK_PIN_INTERFACE, BankPinStruct.class); // custom
		put(OpcodeOut.SEND_ONLINE_LIST, OnlineListStruct.class); // custom
		put(OpcodeOut.SEND_SHOP_CLOSE, NoPayloadStruct.class);
		put(OpcodeOut.SEND_NPC_KILLS, MobKillsStruct.class); // custom
		put(OpcodeOut.SEND_OPENPK_POINTS_TO_GP_RATIO, NoPayloadStruct.class); // custom
		put(OpcodeOut.SEND_OPENPK_POINTS, PointsStruct.class); // custom
		put(OpcodeOut.SEND_FRIEND_LIST, FriendListStruct.class); // retro rsc
		put(OpcodeOut.SEND_FRIEND_UPDATE, FriendUpdateStruct.class);
		put(OpcodeOut.SEND_BANK_PRESET, BankPresetStruct.class); // custom
		put(OpcodeOut.SEND_EQUIPMENT_STATS, EquipmentStatsStruct.class);
		put(OpcodeOut.SEND_STATS, StatInfoStruct.class);
		put(OpcodeOut.SEND_STAT, StatUpdateStruct.class);
		put(OpcodeOut.SEND_TRADE_OTHER_ACCEPTED, TradeAcceptStruct.class);
		put(OpcodeOut.SEND_LOGOUT, NoPayloadStruct.class);
		put(OpcodeOut.SEND_DUEL_CONFIRMWINDOW, DuelConfirmStruct.class);
		put(OpcodeOut.SEND_DUEL_WINDOW, DuelShowWindowStruct.class);
		put(OpcodeOut.SEND_WELCOME_INFO, WelcomeInfoStruct.class);
		put(OpcodeOut.SEND_CANT_LOGOUT, NoPayloadStruct.class);
		put(OpcodeOut.SEND_28_BYTES_UNUSED, NoPayloadStruct.class); // TODO: revise not implemented
		put(OpcodeOut.SEND_PLAYER_COORDS, MobsUpdateStruct.class);
		put(OpcodeOut.SEND_SLEEPWORD_INCORRECT, NoPayloadStruct.class);
		put(OpcodeOut.SEND_BANK_CLOSE, NoPayloadStruct.class);
		put(OpcodeOut.SEND_PLAY_SOUND, PlaySoundStruct.class);
		put(OpcodeOut.SEND_PRAYERS_ACTIVE, PrayersActiveStruct.class);
		put(OpcodeOut.SEND_DUEL_ACCEPTED, DuelAcceptStruct.class);
		put(OpcodeOut.SEND_REMOVE_WORLD_ENTITY, ClearLocationsStruct.class);
		put(OpcodeOut.SEND_REMOVE_WORLD_NPC, ClearMobsStruct.class); // retro rsc
		put(OpcodeOut.SEND_REMOVE_WORLD_PLAYER, ClearMobsStruct.class); // retro rsc
		put(OpcodeOut.SEND_APPEARANCE_KEEPALIVE, NoPayloadStruct.class);
		put(OpcodeOut.SEND_BOX, MessageBoxStruct.class);
		put(OpcodeOut.SEND_OPEN_RECOVERY, NoPayloadStruct.class); // part of rsc era protocol
		put(OpcodeOut.SEND_DUEL_CLOSE, NoPayloadStruct.class);
		put(OpcodeOut.SEND_OPEN_DETAILS, NoPayloadStruct.class); // part of rsc era protocol
		put(OpcodeOut.SEND_UPDATE_PLAYERS, AppearanceUpdateStruct.class);
		put(OpcodeOut.SEND_UPDATE_PLAYERS_RETRO, AppearanceUpdateStruct.class); // retro rsc
		put(OpcodeOut.SEND_UPDATE_IGNORE_LIST_BECAUSE_NAME_CHANGE, IgnoreListStruct.class);
		put(OpcodeOut.SEND_GAME_SETTINGS, GameSettingsStruct.class);
		put(OpcodeOut.SEND_SLEEP_FATIGUE, FatigueStruct.class);
		put(OpcodeOut.SEND_OPTIONS_MENU_OPEN, MenuOptionStruct.class);
		put(OpcodeOut.SEND_BANK_UPDATE, BankUpdateStruct.class);
		put(OpcodeOut.SEND_OPTIONS_MENU_CLOSE, NoPayloadStruct.class);
		put(OpcodeOut.SEND_DUEL_OTHER_ACCEPTED, DuelAcceptStruct.class);
		put(OpcodeOut.SEND_EQUIPMENT, EquipmentStruct.class); // custom
		put(OpcodeOut.SEND_EQUIPMENT_UPDATE, EquipmentUpdateStruct.class); // custom
		put(OpcodeOut.RUNESCAPE_UPDATED, NoPayloadStruct.class); // TODO: might be relevant at some custom 2001scape ?
		put(OpcodeOut.SEND_YOPTIN, NoPayloadStruct.class); // retro rsc
		put(OpcodeOut.SEND_INVENTORY_SIZE, InventoryStruct.class); // retro rsc
		put(OpcodeOut.SEND_UNLOCKED_APPEARANCES, UnlockedAppearancesStruct.class);
	}};

	public static boolean isPayloadCorrectInstance(AbstractStruct<OpcodeOut> payload, OpcodeOut opcode) {
		return payload.getClass().equals(opcodeValidate.getOrDefault(opcode, null));
	}
}
