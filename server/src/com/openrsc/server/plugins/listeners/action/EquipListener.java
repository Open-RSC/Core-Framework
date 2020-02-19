package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.struct.EquipRequest;

public interface EquipListener {

	public void onEquip(EquipRequest request);
}
