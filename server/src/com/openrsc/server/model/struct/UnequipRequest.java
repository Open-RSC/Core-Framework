package com.openrsc.server.model.struct;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public class UnequipRequest {

	public Player player;
	public Item item;

	public UnequipRequest(){}
}
