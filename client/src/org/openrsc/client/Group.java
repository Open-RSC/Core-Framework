package org.openrsc.group;

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
//    public static final int UNUSED4     = 4;
//    public static final int UNUSED5     = 5;
//    public static final int UNUSED6     = 6;
//    public static final int UNUSED7     = 7;
    public static final int DEV         = 8;
    public static final int EVENT       = 9;
    public static final int USER        = 10;
    public static final int SUBSCRIBER  = 11;

    public static final HashMap<Integer, String> GROUP_NAMES = new HashMap<Integer, String> ();

    static {
        GROUP_NAMES.put(OWNER, "Owner");
        GROUP_NAMES.put(ADMIN, "Admin");
        GROUP_NAMES.put(SUPER_MOD, "Super Moderator");
        GROUP_NAMES.put(MOD, "Moderator");
        GROUP_NAMES.put(DEV, "Developer");
        GROUP_NAMES.put(EVENT, "Event");
        GROUP_NAMES.put(USER, "User");
        GROUP_NAMES.put(SUBSCRIBER, "Subscriber");
    }

    public static String getNameColour(int groupID) {
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
            case SUBSCRIBER:
            case MOD:
            case USER:
            default:
                return "@whi@";
        }
    }

    public static String getNameSprite(int groupID) {
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
            case SUBSCRIBER:
            case USER:
            default:
                return "";
        }
    }

    public static String getStaffPrefix(int groupID) {
        return getNameSprite(groupID) + getNameColour(groupID);
    }
}
