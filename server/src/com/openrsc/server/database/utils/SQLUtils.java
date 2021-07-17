package com.openrsc.server.database.utils;

public class SQLUtils {
    public static String escapeLikeParameter(String parameter) {
        return parameter.replaceAll("_", "\\\\_").replaceAll("%", "\\\\%");
    }
}
