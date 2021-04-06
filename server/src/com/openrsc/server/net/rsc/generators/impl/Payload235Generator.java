package com.openrsc.server.net.rsc.generators.impl;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.PacketBuilder;
import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.generators.PayloadGenerator;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

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
		put(OpcodeOut.SEND_TRADE_ACCPETED, 15);
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
		put(OpcodeOut.SEND_APPEARANCE_CHANGE, 59);
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
		return null;
	}
}
