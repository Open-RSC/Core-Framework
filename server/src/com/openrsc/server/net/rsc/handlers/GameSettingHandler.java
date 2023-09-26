package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.GameSettingStruct;

public final class GameSettingHandler implements PayloadProcessor<GameSettingStruct, OpcodeIn> {

	public void process(GameSettingStruct payload, Player player) throws Exception {

		final int idx = payload.index;
		if (idx < 0 || idx > 99) {
			player.setSuspiciousPlayer(true, "game setting idx < 0 or idx > 99");
			return;
		}

		final byte value = (byte) payload.value;

		if (idx >= 4) {
			if (idx == 4) {
				player.getCache().set("setting_android_longpress", value);
			} else if (idx == 7) {
				player.getCache().store("setting_android_holdnchoose", value == 1);
			} else if (idx == 8) {
				player.getCache().store("block_tab_messages", value == 1);
			} else if (idx == 9) {
				player.getCache().set("setting_block_global", value);
			} else if (idx == 10) {
				player.getCache().store("p_xp_notifications_enabled", value == 1);
			} else if (idx == 11) {
				player.getCache().store("p_block_invites", value == 1);
			} else if (idx == 16) {
				player.getCache().set("setting_volume_function", value);
			} else if (idx == 17) {
				player.getCache().set("setting_swipe_rotate_mode", value);
			} else if (idx == 18) {
				player.getCache().set("setting_swipe_scroll_mode", value);
			} else if (idx == 19) {
				player.getCache().set("setting_press_delay", value);
			} else if (idx == 20) {
				player.getCache().set("setting_font_size", value);
			} else if (idx == 21) {
				player.getCache().store("setting_hold_choose", value == 1);
			} else if (idx == 22) {
				player.getCache().set("setting_swipe_zoom_mode", value);
			} else if (idx == 23) {
				player.getCache().set("setting_last_zoom", value);
			} else if (idx == 24) {
				player.getCache().store("setting_batch_progressbar", value== 1);
			} else if (idx == 25) {
				player.getCache().store("setting_experience_drops", value == 1);
			} else if (idx == 26) {
				player.getCache().store("setting_showroof", value == 1);
			} else if (idx == 27) {
				player.getCache().store("setting_showfog", value == 1);
			} else if (idx == 28) {
				player.getCache().set("setting_ground_items", value);
			} else if (idx == 29) {
				player.getCache().store("setting_auto_messageswitch", value == 1);
			} else if (idx == 30) {
				player.getCache().store("setting_side_menu", value == 1);
			} else if (idx == 31) {
				player.getCache().store("setting_kill_feed", value == 1);
			} else if (idx == 32) {
				player.getCache().set("setting_fightmode_selector", value);
			} else if (idx == 33) {
				player.getCache().set("setting_experience_counter", value);
			} else if (idx == 34) {
				player.getCache().store("setting_inventory_count", value == 1);
			} else if (idx == 35) {
				player.getCache().store("setting_floating_nametags", value == 1);
			} else if (idx == 36) {
				player.getCache().store("party_block_invites", value == 1);
			} else if (idx == 37) {
				player.getCache().store("android_inv_toggle", value == 1);
			} else if (idx == 38) {
				player.getCache().store("show_npc_kc", value == 1);
			} else if (idx == 39) {
				player.getCache().store("custom_ui", value == 1);
			} else if (idx == 40) {
				player.getCache().store("setting_hide_login_box", value == 1);
			} else if (idx == 41) {
				player.getCache().store("setting_block_global_friend", value == 1);
			} else if (idx == 42) {
				player.getCache().store("setting_showunderground_flicker", value == 1);
			} else if (idx == 43) {
				player.getCache().set("setting_status_bar", value);
			} else if (idx == 44) {
				player.getCache().store("show_recent_npc_kc", value == 1);
			}
			return;
		}

		if (player.getClientVersion() <= 235) {
			if (idx == 0) { // Camera Mode Auto
				player.getSettings().setGameSetting(PlayerSettings.GAME_SETTING_AUTO_CAMERA, payload.cameraModeAuto == 1);
			} else if (idx == 1) { // 1: Change in PkMode
				int changesLeft = player.getPkChanges();
				if (changesLeft > 0) {
					player.setPkMode(payload.playerKiller);
					player.setPkChanges(changesLeft - 1);
					player.getUpdateFlags().setAppearanceChanged(true);
				}
			} else if (idx == 2) { // 2: Number of Mouse Buttons
				player.getSettings().setGameSetting(PlayerSettings.GAME_SETTING_MOUSE_BUTTONS, payload.mouseButtonOne == 1);
			} else if (idx == 3) { // 3: Sound Enabled
				player.getSettings().setGameSetting(PlayerSettings.GAME_SETTING_SOUND_EFFECTS, payload.soundDisabled == 1);
			}
		} else {
			player.getSettings().setGameSetting(idx, value == 1);
		}
		ActionSender.sendGameSettings(player);
	}
}
