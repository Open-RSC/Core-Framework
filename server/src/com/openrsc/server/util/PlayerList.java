package com.openrsc.server.util;

import com.openrsc.server.model.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerList extends EntityList<Player> {
    private final Map<Long, Player> playerHashIndex = new HashMap<>();

    public PlayerList(int capacity) {
        super(capacity);
    }

    @Override
    public synchronized boolean add(Player entity) {
        if(super.add(entity)) {
            playerHashIndex.put(entity.getUsernameHash(), entity);
        }
        return true;
    }

    @Override
    public synchronized Player remove(int index) {
        Player player = super.remove(index);
        if(player != null) {
            playerHashIndex.remove(player.getUsernameHash());
        }
        return player;
    }

    /**
     * Gets a player by their username hash
     * @param hash username hash
     * @return the player associated with this hash
     */
    public Player getPlayerByHash(long hash) {
        return playerHashIndex.get(hash);
    }
	/**
	 * Remove a player by their username hash
	 * @param hash username hash
	 * @return the player associated with this hash
	 */
	public Player removePlayerByHash(long hash) {
		return playerHashIndex.remove(hash);
	}
}
