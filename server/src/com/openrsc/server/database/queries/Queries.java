package com.openrsc.server.database.queries;

public class Queries {

    // BANK PRESETS
    @Named("bank.removePresetByPlayerId")
    public NamedParameterQuery REMOVE_BANK_PRESETS_BY_PLAYER_ID;

    @Named("bank.addBankPreset")
    public NamedParameterQuery ADD_BANK_PRESET;

    @Named("bank.getBankPresetsByPlayerId")
    public NamedParameterQuery GET_BANK_PRESETS_BY_PLAYER_ID;

    // PLAYER
    @Named("player.getPlayerByUsername")
    public NamedParameterQuery GET_PLAYER_BY_USERNAME;

    @Named("player.bank.getItemIds")
    public NamedParameterQuery GET_PLAYER_BANK_ITEM_IDS;

    @Named("player.bank.deleteItems")
    public NamedParameterQuery DELETE_PLAYER_BANK_ITEM_IDS;

    @Named("player.inventory.getItemIds")
    public NamedParameterQuery GET_PLAYER_INV_ITEM_IDS;

    @Named("player.inventory.deleteItems")
    public NamedParameterQuery DELETE_PLAYER_INV_ITEM_IDS;

    @Named("player.equipped.equipItem")
    public NamedParameterQuery SAVE_PLAYER_EQUIP_ITEM;

    @Named("player.equipped.getItemIds")
    public NamedParameterQuery GET_PLAYER_EQUIPPED_ITEM_IDS;

    @Named("player.equipped.deleteItems")
    public NamedParameterQuery DELETE_PLAYER_EQUIPPED_ITEM_IDS;

    // ITEMS
    @Named("item.createItem")
    public NamedParameterQuery ITEM_CREATE_ITEM;

    @Named("item.deleteItems")
    public NamedParameterQuery ITEM_DELETE_ITEM_IDS;
}
