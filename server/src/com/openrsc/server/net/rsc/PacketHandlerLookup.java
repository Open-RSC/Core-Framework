package com.openrsc.server.net.rsc;

import java.util.HashMap;

import com.openrsc.server.net.rsc.handlers.AttackHandler;
import com.openrsc.server.net.rsc.handlers.BankHandler;
import com.openrsc.server.net.rsc.handlers.BlinkHandler;
import com.openrsc.server.net.rsc.handlers.ChatHandler;
import com.openrsc.server.net.rsc.handlers.CommandHandler;
import com.openrsc.server.net.rsc.handlers.FriendHandler;
import com.openrsc.server.net.rsc.handlers.GameObjectAction;
import com.openrsc.server.net.rsc.handlers.GameObjectWallAction;
import com.openrsc.server.net.rsc.handlers.GameSettingHandler;
import com.openrsc.server.net.rsc.handlers.GroundItemTake;
import com.openrsc.server.net.rsc.handlers.InterfaceOptionHandler;
import com.openrsc.server.net.rsc.handlers.InterfaceShopHandler;
import com.openrsc.server.net.rsc.handlers.ItemActionHandler;
import com.openrsc.server.net.rsc.handlers.ItemDropHandler;
import com.openrsc.server.net.rsc.handlers.ItemUseOnGroundItem;
import com.openrsc.server.net.rsc.handlers.ItemUseOnItem;
import com.openrsc.server.net.rsc.handlers.ItemUseOnNpc;
import com.openrsc.server.net.rsc.handlers.ItemUseOnObject;
import com.openrsc.server.net.rsc.handlers.ItemUseOnPlayer;
import com.openrsc.server.net.rsc.handlers.ItemWieldHandler;
import com.openrsc.server.net.rsc.handlers.Logout;
import com.openrsc.server.net.rsc.handlers.LogoutRequest;
import com.openrsc.server.net.rsc.handlers.MenuReplyHandler;
import com.openrsc.server.net.rsc.handlers.NpcCommand;
import com.openrsc.server.net.rsc.handlers.NpcTalkTo;
import com.openrsc.server.net.rsc.handlers.Ping;
import com.openrsc.server.net.rsc.handlers.PlayerAppearanceUpdater;
import com.openrsc.server.net.rsc.handlers.PlayerDuelHandler;
import com.openrsc.server.net.rsc.handlers.PlayerFollowRequest;
import com.openrsc.server.net.rsc.handlers.PlayerTradeHandler;
import com.openrsc.server.net.rsc.handlers.PrayerHandler;
import com.openrsc.server.net.rsc.handlers.PrivacySettingHandler;
import com.openrsc.server.net.rsc.handlers.ReportHandler;
import com.openrsc.server.net.rsc.handlers.SleepHandler;
import com.openrsc.server.net.rsc.handlers.SpellHandler;
import com.openrsc.server.net.rsc.handlers.StyleHandler;
import com.openrsc.server.net.rsc.handlers.TutorialHandler;
import com.openrsc.server.net.rsc.handlers.WalkRequest;

public class PacketHandlerLookup {

	/* Handlers for incoming Packets. */
	public static HashMap<Integer, PacketHandler> packetHandlers = new HashMap<Integer, PacketHandler>();

	static {

		bind(OpcodeIn.PING.getOpcode(), Ping.class);

		bind(OpcodeIn.WALK_TO_ENTITY.getOpcode(), WalkRequest.class);
		bind(OpcodeIn.WALK_TO_POINT.getOpcode(), WalkRequest.class);

		bind(OpcodeIn.WALL_OBJECT_COMMAND1.getOpcode(), GameObjectWallAction.class);
		bind(OpcodeIn.WALL_OBJECT_COMMAND2.getOpcode(), GameObjectWallAction.class);

		bind(OpcodeIn.QUESTION_DIALOG_ANSWER.getOpcode(), MenuReplyHandler.class);
		
		bind(OpcodeIn.PLAYER_APPEARANCE_CHANGE.getOpcode(), PlayerAppearanceUpdater.class);

		bind(OpcodeIn.OBJECT_COMMAND1.getOpcode(), GameObjectAction.class);
		bind(OpcodeIn.OBJECT_COMMAND2.getOpcode(), GameObjectAction.class);

		bind(OpcodeIn.LOGOUT.getOpcode(), LogoutRequest.class);
		bind(OpcodeIn.CONFIRM_LOGOUT.getOpcode(), Logout.class);

		bind(OpcodeIn.COMMAND.getOpcode(), CommandHandler.class);
		bind(OpcodeIn.CHAT_MESSAGE.getOpcode(), ChatHandler.class);

		bind(OpcodeIn.PRAYER_ACTIVATED.getOpcode(), PrayerHandler.class);
		bind(OpcodeIn.PRAYER_DEACTIVATED.getOpcode(), PrayerHandler.class);

		bind(OpcodeIn.NPC_TALK_TO.getOpcode(), NpcTalkTo.class);
		bind(OpcodeIn.NPC_ATTACK1.getOpcode(), AttackHandler.class);
		bind(OpcodeIn.CAST_ON_SELF.getOpcode(), SpellHandler.class);
		bind(OpcodeIn.PLAYER_CAST_SPELL.getOpcode(), SpellHandler.class);
		bind(OpcodeIn.PLAYER_USE_ITEM.getOpcode(), ItemUseOnPlayer.class);
		bind(OpcodeIn.NPC_CAST_SPELL.getOpcode(), SpellHandler.class);
		bind(OpcodeIn.ITEM_CAST_SPELL.getOpcode(), SpellHandler.class);
		bind(OpcodeIn.WALL_OBJECT_CAST.getOpcode(), SpellHandler.class);
		bind(OpcodeIn.OBJECT_CAST.getOpcode(), SpellHandler.class);
		bind(OpcodeIn.GROUND_ITEM_CAST_SPELL.getOpcode(), SpellHandler.class);
		bind(OpcodeIn.CAST_ON_LAND.getOpcode(), SpellHandler.class);

		bind(OpcodeIn.PLAYER_ATTACK.getOpcode(), AttackHandler.class);
		bind(OpcodeIn.PLAYER_FOLLOW.getOpcode(), PlayerFollowRequest.class);

		bind(OpcodeIn.GROUND_ITEM_USE_ITEM.getOpcode(), ItemUseOnGroundItem.class);
		bind(OpcodeIn.ITEM_USE_ITEM.getOpcode(), ItemUseOnItem.class);
		bind(OpcodeIn.NPC_USE_ITEM.getOpcode(), ItemUseOnNpc.class);
		bind(OpcodeIn.ITEM_COMMAND.getOpcode(), ItemActionHandler.class);
		bind(OpcodeIn.OBJECT_USE_ITEM.getOpcode(), ItemUseOnObject.class);
		bind(OpcodeIn.WALL_USE_ITEM.getOpcode(), ItemUseOnObject.class);

		bind(OpcodeIn.GROUND_ITEM_TAKE.getOpcode(), GroundItemTake.class);

		bind(OpcodeIn.SHOP_BUY.getOpcode(), InterfaceShopHandler.class);
		bind(OpcodeIn.SHOP_SELL.getOpcode(), InterfaceShopHandler.class);
		bind(OpcodeIn.SHOP_CLOSE.getOpcode(), InterfaceShopHandler.class);

		bind(OpcodeIn.ITEM_DROP.getOpcode(), ItemDropHandler.class);
		bind(OpcodeIn.NPC_COMMAND1.getOpcode(), NpcCommand.class);
		bind(OpcodeIn.NPC_COMMAND2.getOpcode(), NpcCommand.class);

		bind(OpcodeIn.ITEM_EQUIP.getOpcode(), ItemWieldHandler.class);
		bind(OpcodeIn.ITEM_REMOVE_EQUIPPED.getOpcode(), ItemWieldHandler.class);

		bind(OpcodeIn.GAME_SETTINGS_CHANGED.getOpcode(), GameSettingHandler.class);
		bind(OpcodeIn.PRIVACY_SETTINGS_CHANGED.getOpcode(), PrivacySettingHandler.class);

		bind(OpcodeIn.PLAYER_DUEL.getOpcode(), PlayerDuelHandler.class);
		bind(OpcodeIn.DUEL_FIRST_SETTINGS_CHANGED.getOpcode(), PlayerDuelHandler.class);
		bind(OpcodeIn.DUEL_FIRST_ACCEPTED.getOpcode(), PlayerDuelHandler.class);
		bind(OpcodeIn.DUEL_DECLINED.getOpcode(), PlayerDuelHandler.class);
		bind(OpcodeIn.DUEL_OFFER_ITEM.getOpcode(), PlayerDuelHandler.class);
		bind(OpcodeIn.DUEL_SECOND_ACCEPTED.getOpcode(), PlayerDuelHandler.class);

		bind(OpcodeIn.TRADE_ACCEPTED.getOpcode(), PlayerTradeHandler.class);
		bind(OpcodeIn.TRADE_DECLINED.getOpcode(), PlayerTradeHandler.class);
		bind(OpcodeIn.TRADE_OFFER.getOpcode(), PlayerTradeHandler.class);
		bind(OpcodeIn.TRADE_CONFIRM_ACCEPTED.getOpcode(), PlayerTradeHandler.class);

		bind(OpcodeIn.SOCIAL_ADD_FRIEND.getOpcode(), FriendHandler.class);
		bind(OpcodeIn.SOCIAL_REMOVE_FRIEND.getOpcode(), FriendHandler.class);
		bind(OpcodeIn.SOCIAL_ADD_IGNORE.getOpcode(), FriendHandler.class);
		bind(OpcodeIn.SOCIAL_REMOVE_IGNORE.getOpcode(), FriendHandler.class);
		bind(OpcodeIn.SOCIAL_SEND_PRIVATE_MESSAGE.getOpcode(), FriendHandler.class);
		
		bind(OpcodeIn.REPORT_ABUSE.getOpcode(), ReportHandler.class);
		
		bind(OpcodeIn.BANK_CLOSE.getOpcode(), BankHandler.class);
		bind(OpcodeIn.BANK_WITHDRAW.getOpcode(), BankHandler.class);
		bind(OpcodeIn.BANK_DEPOSIT.getOpcode(), BankHandler.class);
		
		bind(OpcodeIn.INTERFACE_OPTIONS.getOpcode(), InterfaceOptionHandler.class);
		bind(OpcodeIn.BLINK.getOpcode(), BlinkHandler.class);
		
		bind(OpcodeIn.PLAYER_TRADE.getOpcode(), PlayerTradeHandler.class);
		bind(OpcodeIn.TRADE_ACCEPTED.getOpcode(), PlayerTradeHandler.class);
		bind(OpcodeIn.TRADE_CONFIRM_ACCEPTED.getOpcode(), PlayerTradeHandler.class);
		bind(OpcodeIn.TRADE_DECLINED.getOpcode(), PlayerTradeHandler.class);
		bind(OpcodeIn.TRADE_OFFER.getOpcode(), PlayerTradeHandler.class);
	
		bind(OpcodeIn.SLEEPWORD_ENTERED.getOpcode(), SleepHandler.class);
		bind(OpcodeIn.ON_TUTORIAL_ISLAND.getOpcode(), TutorialHandler.class);
		
		bind(OpcodeIn.COMBAT_STYLE_CHANGED.getOpcode(), StyleHandler.class);
	}

	private static void bind(int opcode, Class<?> clazz) {
		Object clazzObject;
		try {
			clazzObject = clazz.newInstance();
			if (clazzObject instanceof PacketHandler) {
				PacketHandler packetHandler = (PacketHandler) clazzObject;
				packetHandlers.put(opcode, packetHandler);
			} else {
				throw new Exception("bind(opcode, class) not instance of PacketHandler");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static PacketHandler get(int id) {
		return packetHandlers.get(id);
	}
}
