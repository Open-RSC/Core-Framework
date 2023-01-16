package com.openrsc.server.model.entity.player;

import com.openrsc.server.model.world.World;

import java.util.HashMap;

public class Group {
	public static final int OWNER = 0;
	public static final int ADMIN = 1;
	public static final int SUPER_MOD = 2;
	public static final int MOD = 3;
	public static final int DEV = 5;
	public static final int EVENT = 7;
	public static final int PLAYER_MOD = 8;
	private static final int TESTER = 9;
	public static final int USER = 10;

	public static final int DEFAULT_GROUP = Group.USER;

	public static final HashMap<Integer, String> GROUP_NAMES = new HashMap<Integer, String>();

	static {
		GROUP_NAMES.put(OWNER, "Owner");
		GROUP_NAMES.put(ADMIN, "Admin");
		GROUP_NAMES.put(SUPER_MOD, "Super Moderator");
		GROUP_NAMES.put(MOD, "Moderator");
		GROUP_NAMES.put(DEV, "Developer");
		GROUP_NAMES.put(EVENT, "Event");
		GROUP_NAMES.put(PLAYER_MOD, "Player Moderator");
		GROUP_NAMES.put(TESTER, "Tester");
		GROUP_NAMES.put(USER, "User");
	}

	public static String getGlobalMessageName(int groupID) {
		switch (groupID) {
			case OWNER:
			case ADMIN:
				return "Admin";
			case SUPER_MOD:
			case MOD:
				return "Mod";
			case DEV:
			case EVENT:
				return "Event";
			case PLAYER_MOD:
				return "Pmod";
			case TESTER:
			case USER:
			default:
				return "";
		}
	}

	public static String getNameColour(World world, int groupID) {
		if (!world.getServer().getConfig().WANT_CUSTOM_RANK_DISPLAY)
			return "";

		switch (groupID) {
			case OWNER:
				return "@dcy@";
			case ADMIN:
				return "@gre@";
			case SUPER_MOD:
				return "@blu@";
			case MOD:
				return "@bl1@";
			case DEV:
				return "@red@";
			case EVENT:
				return "@eve@";
			case PLAYER_MOD:
			case TESTER:
			case USER:
			default:
				return "";
		}
	}

	public static String getNameSprite(int groupID) {
		return "";

		/*if (!getServer().getConfig().WANT_CUSTOM_RANK_DISPLAY)
			return "";

		switch (groupID) {
			case OWNER:
			case ADMIN:
				return "#adm#";
			case SUPER_MOD:
			case MOD:
				return "#mod#";
			case DEV:
				return "#dev#";
			case EVENT:
				return "#eve#";
			case USER:
			default:
				return "";
		}*/
	}

	public static String getStaffPrefix(World world, int groupID) {
		return getNameSprite(groupID) + getNameColour(world, groupID);
	}
}
