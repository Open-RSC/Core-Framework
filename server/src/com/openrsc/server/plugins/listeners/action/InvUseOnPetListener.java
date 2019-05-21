package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.pet.Pet;
import com.openrsc.server.model.entity.player.Player;

public interface InvUseOnPetListener {

	public void onInvUseOnNpc(Player player, Pet pet, Item item);

}
