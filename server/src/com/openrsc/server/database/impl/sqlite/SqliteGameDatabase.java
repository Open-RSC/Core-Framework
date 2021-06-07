package com.openrsc.server.database.impl.sqlite;

import com.openrsc.server.Server;
import com.openrsc.server.database.DatabaseType;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.JDBCDatabaseConnection;
import com.openrsc.server.database.impl.mysql.MySqlGameDatabase;
import com.openrsc.server.database.impl.mysql.MySqlQueries;
import com.openrsc.server.database.queries.NamedParameterQuery;
import com.openrsc.server.database.queries.Queries;
import com.openrsc.server.database.queries.QueriesManager;
import com.openrsc.server.database.struct.ItemStore;
import com.openrsc.server.database.struct.PlayerBank;
import com.openrsc.server.database.struct.PlayerBankPreset;
import com.openrsc.server.database.struct.PlayerEquipped;
import com.openrsc.server.database.struct.PlayerInventory;
import com.openrsc.server.model.entity.player.Player;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqliteGameDatabase extends MySqlGameDatabase {
    private final Logger LOGGER = LogManager.getLogger();
    private final SqliteGameDatabaseConnection connection;
    private final QueriesManager queriesManager;
    private final Queries queries;
    private final MySqlQueries mySqlQueries = new MySqlQueries(server);

    public SqliteGameDatabase(Server server) {
        super(server);
        connection = new SqliteGameDatabaseConnection(server);
        String tablePrefix = server.getConfig().DB_TABLE_PREFIX;
        queriesManager = QueriesManager.getInstance(
                DatabaseType.SQLITE,
                tablePrefix
        );
        queries = queriesManager.prefill(Queries.class);
    }

    @Override
    public JDBCDatabaseConnection getConnection() {
        return connection;
    }

    @Override
    protected void startTransaction() throws GameDatabaseException {
        executeUpdate("BEGIN TRANSACTION");
    }

    @Override
    protected void commitTransaction() throws GameDatabaseException {
        executeUpdate("END TRANSACTION");
    }

    @Override
    protected void rollbackTransaction() throws GameDatabaseException {
        executeUpdate("ROLLBACK");
    }

    private int executeUpdate(String query) {
        return withErrorHandling(() -> getConnection().executeUpdate(query));
    }

    @Override
    public void savePlayerBank(final int playerId, final PlayerBank[] bank) throws GameDatabaseException {
        withErrorHandling(() -> {
            emptyBank(playerId);
            PreparedStatement statement = getConnection().prepareStatement(mySqlQueries.save_BankAdd);
            PreparedStatement statement2 = getConnection().prepareStatement(mySqlQueries.save_ItemCreate);
            if (bank.length > 0) {
                int slot = 0;
                for (final PlayerBank item : bank) {
                    statement.setInt(1, playerId);
                    statement.setInt(2, item.itemId);
                    statement.setInt(3, slot++);
                    statement.addBatch();

                    statement2.setInt(1, item.itemId);
                    statement2.setInt(2, item.itemStatus.getCatalogId());
                    statement2.setInt(3, item.itemStatus.getAmount());
                    statement2.setInt(4, item.itemStatus.getNoted() ? 1 : 0);
                    statement2.setInt(5, 0);
                    statement2.setInt(6, item.itemStatus.getDurability());
                    statement2.addBatch();
                }

                statement.executeBatch();
                statement2.executeBatch();
            }
            statement.close();
            statement2.close();
        });
    }

    @Override
    public void savePlayerInventory(int playerId, PlayerInventory[] inventory) throws GameDatabaseException {
        withErrorHandling(() -> {
            emptyInventory(playerId);
            PreparedStatement statement = getConnection().prepareStatement(mySqlQueries.save_InventoryAdd);
            PreparedStatement statement2 = getConnection().prepareStatement(mySqlQueries.save_ItemCreate);

            for (final PlayerInventory item : inventory) {
                statement.setInt(1, playerId);
                statement.setInt(2, item.itemId);
                statement.setInt(3, item.slot);
                statement.addBatch();

                statement2.setInt(1, item.itemId);
                statement2.setInt(2, item.catalogID);
                statement2.setInt(3, item.amount);
                statement2.setInt(4, item.noted ? 1 : 0);
                statement2.setInt(5, item.wielded ? 1 : 0);
                statement2.setInt(6, item.durability);
                statement2.addBatch();
            }

            statement.executeBatch();
            statement2.executeBatch();
            statement.close();
            statement2.close();
        });
    }

    @Override
    public void querySavePlayerEquipped(int playerId, PlayerEquipped[] equipment) throws GameDatabaseException {
        withErrorHandling(() -> {
            emptyEquipped(playerId);
            NamedParameterQuery equipItem = queries.SAVE_PLAYER_EQUIP_ITEM;
            NamedParameterQuery createItem = queries.ITEM_CREATE_ITEM;

            for (final PlayerEquipped item : equipment) {
                executeUpdate(equipItem.fill(item));
                executeUpdate(createItem.fill(item));
            }
        });
    }

    @Override
    public void querySavePlayerBankPresets(final int playerId, final PlayerBankPreset[] bankPreset) throws GameDatabaseException {
        NamedParameterQuery removeBankPresets = queries.REMOVE_BANK_PRESETS_BY_PLAYER_ID;
        String removeBankPresetsQuery = removeBankPresets.fillParameter("playerId", playerId);
        executeUpdate(removeBankPresetsQuery);

        if (getServer().getConfig().WANT_BANK_PRESETS) {
            NamedParameterQuery addBankPreset = queries.ADD_BANK_PRESET;
            for (final PlayerBankPreset currentBankPreset : bankPreset) {
                executeUpdate(
                        addBankPreset.fillParameters(
                                Pair.of("playerId", playerId),
                                Pair.of("slot", currentBankPreset.slot),
                                Pair.of("inventory", Hex.encodeHexString(currentBankPreset.inventory)),
                                Pair.of("equipment", Hex.encodeHexString(currentBankPreset.equipment))
                        )
                );
            }
        }
    }

    @Override
    public PlayerBankPreset[] queryLoadPlayerBankPresets(final Player player) throws GameDatabaseException {
        final ArrayList<PlayerBankPreset> list = new ArrayList<>();
        NamedParameterQuery getBankPresets = queries.GET_BANK_PRESETS_BY_PLAYER_ID;
        if (getServer().getConfig().WANT_BANK_PRESETS) {
            String getBankPresetsQuery = getBankPresets.fillParameter("playerId", player.getDatabaseID());
            executeQuery(
                    getBankPresetsQuery,
                    result -> {
                        while(result.next()) {
                            PlayerBankPreset bankPreset = new PlayerBankPreset();
                            bankPreset.slot = result.getInt("slot");
                            bankPreset.inventory = Hex.decodeHex(result.getString("inventory"));
                            bankPreset.equipment = Hex.decodeHex(result.getString("equipment"));

                            list.add(bankPreset);
                        }
                    }
            );
        }
        return list.toArray(new PlayerBankPreset[0]);
    }

    private void emptyInventory(int playerId) throws Exception {
        List<Integer> itemsToDelete = getPlayerItemIds(ItemStore.INVENTORY, playerId);
        deletePlayerItems(ItemStore.INVENTORY, playerId, itemsToDelete);
    }

    private void emptyBank(int playerId) throws Exception {
        List<Integer> itemsToDelete = getPlayerItemIds(ItemStore.BANK, playerId);
        deletePlayerItems(ItemStore.BANK, playerId, itemsToDelete);
    }

    private void emptyEquipped(int playerId) throws Exception {
        List<Integer> itemsToDelete = getPlayerItemIds(ItemStore.EQUIPPED, playerId);
        deletePlayerItems(ItemStore.EQUIPPED, playerId, itemsToDelete);
    }

    private void purgeItems(List<Integer> itemsToDelete) throws SQLException {
        NamedParameterQuery deleteItemIds = queries.ITEM_DELETE_ITEM_IDS;
        executeUpdate(
                deleteItemIds.fillParameter("items", itemsToDelete)
        );
    }

    private List<Integer> getPlayerItemIds(ItemStore itemStore, int playerId) throws Exception {
        NamedParameterQuery getItemsByPlayerId;
        if (itemStore == ItemStore.BANK) {
            getItemsByPlayerId = queries.GET_PLAYER_BANK_ITEM_IDS;
        } else if (itemStore == ItemStore.INVENTORY) {
            getItemsByPlayerId = queries.GET_PLAYER_INV_ITEM_IDS;
        } else if (itemStore == ItemStore.EQUIPPED) {
            getItemsByPlayerId = queries.GET_PLAYER_EQUIPPED_ITEM_IDS;
        } else {
            throw new IllegalArgumentException("ItemStore must be: " + StringUtils.join(ItemStore.values()));
        }

        ResultSet resultSet = connection.executeQuery(
                getItemsByPlayerId.fillParameter("playerId", playerId)
        );
        List<Integer> itemIds = new ArrayList<>();
        while (resultSet.next()) {
            itemIds.add(resultSet.getInt("itemID"));
        }
        return itemIds;
    }

    private void deletePlayerItems(ItemStore itemStore, int playerId, List<Integer> itemIds) throws SQLException {
        if (itemIds.isEmpty()) {
            return;
        }
        NamedParameterQuery removeItemsFromPlayer;
        if (itemStore == ItemStore.BANK) {
            removeItemsFromPlayer = queries.DELETE_PLAYER_BANK_ITEM_IDS;
        } else if (itemStore == ItemStore.INVENTORY) {
            removeItemsFromPlayer = queries.DELETE_PLAYER_INV_ITEM_IDS;
        } else {
            removeItemsFromPlayer = queries.DELETE_PLAYER_EQUIPPED_ITEM_IDS;
        }

        executeUpdate(
                removeItemsFromPlayer.fillParameters(
                        Pair.of("playerId", playerId),
                        Pair.of("items", itemIds)
                )
        );
        purgeItems(itemIds);
    }
}
