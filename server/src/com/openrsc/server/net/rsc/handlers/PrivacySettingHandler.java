package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.PrivacySettingsStruct;

public class PrivacySettingHandler implements PayloadProcessor<PrivacySettingsStruct, OpcodeIn> {

	public void process(PrivacySettingsStruct payload, Player player) throws Exception {

		byte[] newSettings = new byte[4];
		for (int i = 0; i < 4; i++) {
			newSettings[i] = payload.newSettings[i];
		}
		for (int i = 0; i < 4; i++) {
			player.getSettings().setPrivacySetting(i, newSettings[i]);
		}
	}

}
