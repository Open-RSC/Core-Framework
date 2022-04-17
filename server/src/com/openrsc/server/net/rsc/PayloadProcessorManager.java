package com.openrsc.server.net.rsc;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.handlers.*;
import com.openrsc.server.net.rsc.struct.AbstractStruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Redistributes payload to appropriate handler
 * */
public class PayloadProcessorManager {
	/* Handlers for parsed payload */
	private static HashMap<OpcodeIn, PayloadProcessor<? extends AbstractStruct<OpcodeIn>, OpcodeIn>> payloadProcessors = new HashMap<>();
	private static final Logger LOGGER = LogManager.getLogger();
	static {

		bind(OpcodeIn.HEARTBEAT, Heartbeat.class);

		bind(OpcodeIn.WALK_TO_ENTITY, WalkRequest.class);
		bind(OpcodeIn.WALK_TO_POINT, WalkRequest.class);

		bind(OpcodeIn.INTERACT_WITH_BOUNDARY, GameObjectWallAction.class);
		bind(OpcodeIn.INTERACT_WITH_BOUNDARY2, GameObjectWallAction.class);

		bind(OpcodeIn.QUESTION_DIALOG_ANSWER, MenuReplyHandler.class);

		bind(OpcodeIn.PLAYER_APPEARANCE_CHANGE, PlayerAppearanceUpdater.class);

		bind(OpcodeIn.OBJECT_COMMAND, GameObjectAction.class);
		bind(OpcodeIn.OBJECT_COMMAND2, GameObjectAction.class);

		bind(OpcodeIn.LOGOUT, LogoutRequest.class);
		bind(OpcodeIn.CONFIRM_LOGOUT, Logout.class);

		bind(OpcodeIn.COMMAND, CommandHandler.class);
		bind(OpcodeIn.CHAT_MESSAGE, ChatHandler.class);

		bind(OpcodeIn.PRAYER_ACTIVATED, PrayerHandler.class);
		bind(OpcodeIn.PRAYER_DEACTIVATED, PrayerHandler.class);

		bind(OpcodeIn.NPC_TALK_TO, NpcTalkTo.class);
		bind(OpcodeIn.NPC_ATTACK, AttackHandler.class);
		bind(OpcodeIn.CAST_ON_SELF, SpellHandler.class);
		bind(OpcodeIn.PLAYER_CAST_PVP, SpellHandler.class);
		bind(OpcodeIn.PLAYER_USE_ITEM, ItemUseOnPlayer.class);
		bind(OpcodeIn.CAST_ON_NPC, SpellHandler.class);
		bind(OpcodeIn.CAST_ON_INVENTORY_ITEM, SpellHandler.class);
		bind(OpcodeIn.CAST_ON_BOUNDARY, SpellHandler.class);
		bind(OpcodeIn.CAST_ON_SCENERY, SpellHandler.class);
		bind(OpcodeIn.CAST_ON_GROUND_ITEM, SpellHandler.class);
		bind(OpcodeIn.CAST_ON_LAND, SpellHandler.class);

		bind(OpcodeIn.PLAYER_ATTACK, AttackHandler.class);
		bind(OpcodeIn.PLAYER_FOLLOW, PlayerFollowRequest.class);

		bind(OpcodeIn.GROUND_ITEM_USE_ITEM, ItemUseOnGroundItem.class);
		bind(OpcodeIn.ITEM_USE_ITEM, ItemUseOnItem.class);
		bind(OpcodeIn.NPC_USE_ITEM, ItemUseOnNpc.class);
		bind(OpcodeIn.ITEM_COMMAND, ItemActionHandler.class);
		bind(OpcodeIn.USE_ITEM_ON_SCENERY, ItemUseOnObject.class);
		bind(OpcodeIn.USE_WITH_BOUNDARY, ItemUseOnObject.class);

		bind(OpcodeIn.GROUND_ITEM_TAKE, GroundItemTake.class);

		bind(OpcodeIn.SHOP_BUY, InterfaceShopHandler.class);
		bind(OpcodeIn.SHOP_SELL, InterfaceShopHandler.class);
		bind(OpcodeIn.SHOP_CLOSE, InterfaceShopHandler.class);

		bind(OpcodeIn.ITEM_DROP, ItemDropHandler.class);
		bind(OpcodeIn.NPC_COMMAND, NpcCommand.class);
		bind(OpcodeIn.NPC_COMMAND2, NpcCommand.class);

		bind(OpcodeIn.ITEM_EQUIP_FROM_INVENTORY, ItemEquip.class);
		bind(OpcodeIn.ITEM_UNEQUIP_FROM_INVENTORY, ItemUnequip.class);
		bind(OpcodeIn.ITEM_UNEQUIP_FROM_EQUIPMENT, ItemUnequip.class);
		bind(OpcodeIn.ITEM_EQUIP_FROM_BANK, ItemEquip.class);
		bind(OpcodeIn.ITEM_REMOVE_TO_BANK, ItemUnequip.class);

		bind(OpcodeIn.GAME_SETTINGS_CHANGED, GameSettingHandler.class);
		bind(OpcodeIn.PRIVACY_SETTINGS_CHANGED, PrivacySettingHandler.class);

		bind(OpcodeIn.PLAYER_DUEL, PlayerDuelHandler.class);
		bind(OpcodeIn.DUEL_FIRST_SETTINGS_CHANGED, PlayerDuelHandler.class);
		bind(OpcodeIn.DUEL_FIRST_ACCEPTED, PlayerDuelHandler.class);
		bind(OpcodeIn.DUEL_DECLINED, PlayerDuelHandler.class);
		bind(OpcodeIn.DUEL_OFFER_ITEM, PlayerDuelHandler.class);
		bind(OpcodeIn.DUEL_SECOND_ACCEPTED, PlayerDuelHandler.class);

		bind(OpcodeIn.SOCIAL_ADD_FRIEND, FriendHandler.class);
		bind(OpcodeIn.SOCIAL_REMOVE_FRIEND, FriendHandler.class);
		bind(OpcodeIn.SOCIAL_ADD_IGNORE, FriendHandler.class);
		bind(OpcodeIn.SOCIAL_REMOVE_IGNORE, FriendHandler.class);
		bind(OpcodeIn.SOCIAL_SEND_PRIVATE_MESSAGE, FriendHandler.class);
		bind(OpcodeIn.SOCIAL_ADD_DELAYED_IGNORE, FriendHandler.class);

		bind(OpcodeIn.REPORT_ABUSE, ReportHandler.class);

		bind(OpcodeIn.BANK_CLOSE, BankHandler.class);
		bind(OpcodeIn.BANK_WITHDRAW, BankHandler.class);
		bind(OpcodeIn.BANK_DEPOSIT, BankHandler.class);
		bind(OpcodeIn.BANK_DEPOSIT_ALL_FROM_INVENTORY, BankHandler.class);
		bind(OpcodeIn.BANK_DEPOSIT_ALL_FROM_EQUIPMENT, BankHandler.class);
		bind(OpcodeIn.BANK_SAVE_PRESET, BankHandler.class);
		bind(OpcodeIn.BANK_LOAD_PRESET, BankHandler.class);

		bind(OpcodeIn.INTERFACE_OPTIONS, InterfaceOptionHandler.class);
		bind(OpcodeIn.BLINK, BlinkHandler.class);

		bind(OpcodeIn.CHANGE_PASS, SecuritySettingsHandler.class);
		bind(OpcodeIn.CANCEL_RECOVERY_REQUEST, SecuritySettingsHandler.class);
		bind(OpcodeIn.CHANGE_RECOVERY_REQUEST, SecuritySettingsHandler.class);
		bind(OpcodeIn.CHANGE_DETAILS_REQUEST, SecuritySettingsHandler.class);
		bind(OpcodeIn.SET_RECOVERY, SecuritySettingsHandler.class);
		bind(OpcodeIn.SET_DETAILS, SecuritySettingsHandler.class);

		bind(OpcodeIn.PLAYER_INIT_TRADE_REQUEST, PlayerTradeHandler.class);
		bind(OpcodeIn.PLAYER_ACCEPTED_INIT_TRADE_REQUEST, PlayerTradeHandler.class);
		bind(OpcodeIn.PLAYER_ACCEPTED_TRADE, PlayerTradeHandler.class);
		bind(OpcodeIn.PLAYER_DECLINED_TRADE, PlayerTradeHandler.class);
		bind(OpcodeIn.PLAYER_ADDED_ITEMS_TO_TRADE_OFFER, PlayerTradeHandler.class);

		bind(OpcodeIn.SLEEPWORD_ENTERED, SleepHandler.class);
		bind(OpcodeIn.SKIP_TUTORIAL, TutorialHandler.class);
		bind(OpcodeIn.ON_BLACK_HOLE, BlackHoleHandler.class);

		bind(OpcodeIn.COMBAT_STYLE_CHANGED, CombatStyleHandler.class);
		bind(OpcodeIn.SEND_DEBUG_INFO, ClientDebugHandler.class);

		bind(OpcodeIn.KNOWN_PLAYERS, KnownPlayersHandler.class); // TODO: class logic needs to be implemented
	}

	private static void bind(OpcodeIn opcode, Class<?> clazz) {
		Object clazzObject;
		try {
			clazzObject = clazz.getConstructor().newInstance();
			if (clazzObject instanceof PayloadProcessor) {
				PayloadProcessor<? extends AbstractStruct<OpcodeIn>, OpcodeIn> payloadProcessor = (PayloadProcessor<? extends AbstractStruct<OpcodeIn>, OpcodeIn>) clazzObject;
				payloadProcessors.put(opcode, payloadProcessor);
			} else {
				throw new Exception("bind(opcode, class) not instance of PayloadProcessor");
			}
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	public static boolean processed(AbstractStruct<OpcodeIn> payload, Player player) {
		PayloadProcessor<? extends AbstractStruct<OpcodeIn>, OpcodeIn> processor = get(payload.getOpcode());
		if (processor != null) {
			try {
				Method method = null;
				for (Method m : processor.getClass().getDeclaredMethods()) {
					if (m.getName().equals("process")) {
						method = m;
						break;
					}
				}
				if (method != null) {
					checkIfShouldCancelMenu(player, payload.getOpcode());
					method.invoke(processor, payload, player); //processor.process(payload, player);
				}
			} catch(Exception e) {
				return false;
			}
			return true;
		}
		return false;
	}

	private static void checkIfShouldCancelMenu(Player player, OpcodeIn opcode) {
		// most player actions other than choosing a dialogue choice must cancel the menu handler and set them non-busy
		if (!OpcodeIn.QUESTION_DIALOG_ANSWER.equals(opcode)
			&& !OpcodeIn.HEARTBEAT.equals(opcode)
			&& !OpcodeIn.KNOWN_PLAYERS.equals(opcode)
			&& !OpcodeIn.CHAT_MESSAGE.equals(opcode)
			&& !OpcodeIn.SOCIAL_SEND_PRIVATE_MESSAGE.equals(opcode)
			&& !OpcodeIn.COMBAT_STYLE_CHANGED.equals(opcode)
			&& !OpcodeIn.GAME_SETTINGS_CHANGED.equals(opcode)
			&& !OpcodeIn.PRIVACY_SETTINGS_CHANGED.equals(opcode)) {

			player.cancelMenuHandler();
		}
	}

	private static PayloadProcessor<? extends AbstractStruct<OpcodeIn>, OpcodeIn> get(OpcodeIn opcode) {
		return payloadProcessors.get(opcode);
	}

}
