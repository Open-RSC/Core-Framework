package com.openrsc.server.net.rsc;

public enum OpcodeIn {
	HEARTBEAT(67),
	WALK_TO_ENTITY(16),
	WALK_TO_POINT(187),
	CONFIRM_LOGOUT(31),
	LOGOUT(102),
	BLINK(59),
	COMBAT_STYLE_CHANGED(29),
	QUESTION_DIALOG_ANSWER(116),

	PLAYER_APPEARANCE_CHANGE(235),
	SOCIAL_ADD_IGNORE(132),
	SOCIAL_ADD_DELAYED_IGNORE(194), // inauthentic
	SOCIAL_ADD_FRIEND(195),
	SOCIAL_SEND_PRIVATE_MESSAGE(218),
	SOCIAL_REMOVE_FRIEND(167),
	SOCIAL_REMOVE_IGNORE(241),

	DUEL_FIRST_SETTINGS_CHANGED(8),
	DUEL_FIRST_ACCEPTED(176),
	DUEL_DECLINED(197),
	DUEL_OFFER_ITEM(33),
	DUEL_SECOND_ACCEPTED(77),

	INTERACT_WITH_BOUNDARY(14),
	INTERACT_WITH_BOUNDARY2(127),
	CAST_ON_BOUNDARY(180),
	USE_WITH_BOUNDARY(161),

	NPC_TALK_TO(153),
	NPC_COMMAND1(202),
	NPC_COMMAND2(203), //inauthentic
	NPC_ATTACK1(190),
	CAST_ON_NPC(50),
	NPC_USE_ITEM(135),

	PLAYER_CAST_PVP(229),
	PLAYER_USE_ITEM(113),
	PLAYER_ATTACK(171),
	PLAYER_DUEL(103),
	PLAYER_INIT_TRADE_REQUEST(142),
	PLAYER_FOLLOW(165),

	CAST_ON_GROUND_ITEM(249),
	GROUND_ITEM_USE_ITEM(53),
	GROUND_ITEM_TAKE(247),

	CAST_ON_INVENTORY_ITEM(4),
	ITEM_USE_ITEM(91),
	ITEM_UNEQUIP_FROM_INVENTORY(170),
	ITEM_EQUIP_FROM_INVENTORY(169),
	ITEM_UNEQUIP_FROM_EQUIPMENT(168), // inauthentic
	ITEM_EQUIP_FROM_BANK(172), // inauthentic
	ITEM_REMOVE_TO_BANK(173), // inauthentic
	ITEM_COMMAND(90),
	ITEM_DROP(246),

	CAST_ON_SELF(137),
	CAST_ON_LAND(158),

	OBJECT_COMMAND1(136),
	OBJECT_COMMAND2(79),
	CAST_ON_SCENERY(99),
	USE_ITEM_ON_SCENERY(115),

	SHOP_CLOSE(166),
	SHOP_BUY(236),
	SHOP_SELL(221),

	PLAYER_ACCEPTED_INIT_TRADE_REQUEST(55),
	PLAYER_DECLINED_TRADE(230),
	PLAYER_ADDED_ITEMS_TO_TRADE_OFFER(46),
	PLAYER_ACCEPTED_TRADE(104),

	PRAYER_ACTIVATED(60),
	PRAYER_DEACTIVATED(254),

	GAME_SETTINGS_CHANGED(111),
	CHAT_MESSAGE(216),
	COMMAND(38),
	PRIVACY_SETTINGS_CHANGED(64),
	REPORT_ABUSE(206),
	BANK_CLOSE(212),
	BANK_WITHDRAW(22),
	BANK_DEPOSIT(23),

	BANK_DEPOSIT_ALL_FROM_INVENTORY(24), // inauthentic
	BANK_DEPOSIT_ALL_FROM_EQUIPMENT(26), // inauthentic
	BANK_SAVE_PRESET(27), // inauthentic
	BANK_LOAD_PRESET(28), // inauthentic
	INTERFACE_OPTIONS(199), // inauthentic

	SLEEPWORD_ENTERED(45),

	SKIP_TUTORIAL(84),
	ON_BLACK_HOLE(86), // inauthentic
	NPC_DEFINITION_REQUEST(89), // inauthentic

	LOGIN(0),
	REGISTER_ACCOUNT(2), // part of RSC127 protocol, would like available even on 235 setting
	FORGOT_PASSWORD(4), // part of RSC127 protocol, shares opcode 4 with cast_on_inventory_item. Does not require conflict handler since its outside game
	RECOVERY_ATTEMPT(8), // part of RSC127 protocol, shares opcode 8 with duel_settings. Does not require conflict handler since its outside game

	CHANGE_RECOVERY_REQUEST(197), // part of RSC127 protocol, shares opcode 197 with duel_declined. Uses conflict handler
	CHANGE_DETAILS_REQUEST(247), // part of RSC127 protocol, shares opcode 247 with ground_item_take. Uses conflict handler

	CHANGE_PASS(25), // part of RSC127 protocol, would like available even on 235 setting
	SET_RECOVERY(208), // part of RSC127 protocol, would like available even on 235 setting
	SET_DETAILS(253), // part of RSC175 protocol, would like available even on 235 setting

	CANCEL_RECOVERY_REQUEST(196), // part of RSC127 protocol, would like available even on 235 setting

	;

	private int opcode;

	private OpcodeIn(int opcode) {
		this.setOpcode(opcode);
	}

	public int getOpcode() {
		return opcode;
	}

	public void setOpcode(int opcode) {
		this.opcode = opcode;
	}

	public static OpcodeIn get(int opcode) {
		for (OpcodeIn opcodeIn : OpcodeIn.values()) {
			if (opcodeIn.getOpcode() == opcode)
				return opcodeIn;
		}
		return null;
	}

	public static OpcodeIn getFromList(int opcode, OpcodeIn... choices) {
		for (OpcodeIn opcodeIn : choices) {
			if (opcodeIn.getOpcode() == opcode)
				return opcodeIn;
		}
		return null;
	}

	// opcodes that can be cancelled by subsequently sent opcodes of the same int received on the same tick
	public static boolean useLastPerTick(int opcode) {
		if (opcode == OpcodeIn.WALK_TO_ENTITY.getOpcode()) {
			return true;
		}
		if (opcode == OpcodeIn.WALK_TO_POINT.getOpcode()) {
			return true;
		}
		return false;
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
					return payloadLength >= 3;
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
