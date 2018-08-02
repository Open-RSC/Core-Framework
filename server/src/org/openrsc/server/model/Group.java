/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openrsc.server.model;

import java.util.HashMap;

/**
 *
 * @author chris
 */
public class Group {
//    public static final int UNUSED0     = 0;
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
            case ADMIN:
                return "@gre@";
            case SUPER_MOD:
                return "@blu@";
            case DEV:
                return "@red@";
            case EVENT:
                return "@eve@";
            case SUBSCRIBER:
                return "@or2@";
            case MOD:
            case USER:
            default:
                return "@yel@";
        }
    }

    public static String getNameSprite(int groupID) {
        switch(groupID)
        {
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
