package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.PrivacySettingsStruct;

public class PrivacySettingHandler implements PayloadProcessor<PrivacySettingsStruct, OpcodeIn> {

	public void process(PrivacySettingsStruct payload, Player player) throws Exception {

		byte[] newSettings = new byte[5]; //todo: need to use setting of hide online later
		newSettings[0] = (byte) payload.blockChat;
		newSettings[1] = (byte) payload.blockPrivate;
		newSettings[2] = (byte) payload.blockTrade;
		newSettings[3] = (byte) payload.blockDuel;
		for (int i = 0; i < 4; i++) {
			player.getSettings().setPrivacySetting(i, newSettings[i]);
		}
	}

}
