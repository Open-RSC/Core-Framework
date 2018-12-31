package orsc;

import java.util.HashMap;

/**
 *
 * @author Kenix
 */
public class Group {
	public static final int OWNER       = 0;
	public static final int ADMIN       = 1;
	public static final int SUPER_MOD   = 2;
	public static final int MOD         = 3;
	public static final int DEV         = 8;
	public static final int EVENT       = 9;
	public static final int USER        = 10;

	public static final int DEFAULT_GROUP	= Group.USER;

	public static final HashMap<Integer, String> GROUP_NAMES = new HashMap<Integer, String> ();

	static {
		GROUP_NAMES.put(OWNER, "Owner");
		GROUP_NAMES.put(ADMIN, "Admin");
		GROUP_NAMES.put(SUPER_MOD, "Super Moderator");
		GROUP_NAMES.put(MOD, "Moderator");
		GROUP_NAMES.put(DEV, "Developer");
		GROUP_NAMES.put(EVENT, "Event");
		GROUP_NAMES.put(USER, "User");
	}

	public static String getNameColour(int groupID, boolean isMenu) {
		if(!Config.S_WANT_CUSTOM_RANK_DISPLAY)
			return "";

		switch(groupID)
		{
			case OWNER:
				return "@dcy@";
			case ADMIN:
				return "@gre@";
			case SUPER_MOD:
				return "@blu@";
			case DEV:
				return "@red@";
			case EVENT:
				return "@eve@";
			case MOD:
			case USER:
			default:
				return isMenu ? "@whi@" : "@yel@";
		}
	}

	public static String getNameSprite(int groupID, boolean isMenu) {
		if(!Config.S_WANT_CUSTOM_RANK_DISPLAY)
			return "";

		switch(groupID)
		{
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
		}
	}

	public static String getStaffPrefix(int groupID) {
		return getStaffPrefix(groupID, true);
	}

	public static String getStaffPrefix(int groupID, boolean isMenu) {
		return getNameSprite(groupID, isMenu) + getNameColour(groupID, isMenu);
	}
}
