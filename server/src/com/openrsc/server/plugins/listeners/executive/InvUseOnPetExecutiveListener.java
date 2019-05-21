package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.pet.Pet;
import com.openrsc.server.model.entity.player.Player;

public interface InvUseOnPetExecutiveListener {

	public boolean blockInvUseOnNpc(Player player, Pet pet, Item item);

}
