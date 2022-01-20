package com.openrsc.server.service;

import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.login.LoginRequest;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

import java.util.List;

public interface IPlayerService {

    Player loadPlayer(final LoginRequest rq);

    boolean savePlayer(final Player player);

    void savePlayerMaxStats(final Player player);

    void savePlayerMaxSkill(
            final int playerId,
            final int skillId,
            final int level
    );

    void savePlayerExpCapped(
            final int playerId,
            final int skillId,
            final long dateCapped
    );

    void savePlayerCache(final Player player) throws GameDatabaseException;

    List<Item> retrievePlayerInventory(final String username) throws GameDatabaseException;

    List<Item> retrievePlayerBank(final String username) throws GameDatabaseException;
}
