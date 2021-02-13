package com.aposbot._default;

public interface IStaticAccess {

    String getNpcName(int id);

    String getNpcDesc(int id);

    int getNpcLevel(int id);

    String getItemName(int id);

    String getItemDesc(int id);

    String getItemCommand(int id);

    int getItemBasePrice(int id);

    boolean isItemStackable(int id);

    boolean isItemTradable(int id);

    String getObjectName(int id);

    String getObjectDesc(int id);

    String getBoundName(int id);

    String getBoundDesc(int id);

    int getSpellReqLevel(int id);

    int getSpellType(int i);

    int getReagentCount(int id);

    int getReagentId(int spell, int i);

    int getReagentAmount(int spell, int i);

    int getFriendCount();

    String getFriendName(int i);

    int getIgnoredCount();

    String getIgnoredName(int i);

    int getPrayerCount();

    int getPrayerLevel(int i);

    String getPrayerName(int i);

    String[] getSpellNames();

    String[] getSkillNames();
}