package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;

public final class GameSettingHandler implements PacketHandler {

	public void handlePacket(Packet packet, Player player) {

		int idx = (int) packet.readByte();
		if (idx < 0 || idx > 99) {
			player.setSuspiciousPlayer(true, "game setting idx < 0 or idx > 99");
			return;
		}

		if (idx >= 4) {
			if (idx == 4) {
				player.getCache().set("setting_android_longpress", packet.readByte());
			} else if (idx == 7) {
				player.getCache().store("setting_android_holdnchoose", packet.readByte() == 1);
			} else if (idx == 8) {
				player.getCache().store("block_tab_messages", packet.readByte() == 1);
			} else if (idx == 9) {
				player.getCache().set("setting_block_global", packet.readByte());
			} else if (idx == 10) {
				player.getCache().store("p_xp_notifications_enabled", packet.readByte() == 1);
			} else if (idx == 11) {
				player.getCache().store("p_block_invites", packet.readByte() == 1);
			} else if (idx == 16) {
				player.getCache().store("setting_volume_rotate", packet.readByte() == 1);
			} else if (idx == 17) {
				player.getCache().store("setting_swipe_rotate", packet.readByte() == 1);
			} else if (idx == 18) {
				player.getCache().store("setting_swipe_scroll", packet.readByte() == 1);
			} else if (idx == 19) {
				player.getCache().set("setting_press_delay", packet.readByte());
			} else if (idx == 20) {
				player.getCache().set("setting_font_size", packet.readByte());
			} else if (idx == 21) {
				player.getCache().store("setting_hold_choose", packet.readByte() == 1);
			} else if (idx == 22) {
				player.getCache().store("setting_swipe_zoom", packet.readByte() == 1);
			} else if (idx == 23) {
				player.getCache().set("setting_last_zoom", packet.readByte());
			} else if (idx == 24) {
				player.getCache().store("setting_batch_progressbar", packet.readByte() == 1);
			} else if (idx == 25) {
				player.getCache().store("setting_experience_drops", packet.readByte() == 1);
			} else if (idx == 26) {
				player.getCache().store("setting_showroof", packet.readByte() == 1);
			} else if (idx == 27) {
				player.getCache().store("setting_showfog", packet.readByte() == 1);
			} else if (idx == 28) {
				player.getCache().set("setting_ground_items", packet.readByte());
			} else if (idx == 29) {
				player.getCache().store("setting_auto_messageswitch", packet.readByte() == 1);
			} else if (idx == 30) {
				player.getCache().store("setting_side_menu", packet.readByte() == 1);
			} else if (idx == 31) {
				player.getCache().store("setting_kill_feed", packet.readByte() == 1);
			} else if (idx == 32) {
				player.getCache().set("setting_fightmode_selector", packet.readByte());
			} else if (idx == 33) {
				player.getCache().set("setting_experience_counter", packet.readByte());
			} else if (idx == 34) {
				player.getCache().store("setting_inventory_count", packet.readByte() == 1);
			} else if (idx == 35) {
				player.getCache().store("setting_floating_nametags", packet.readByte() == 1);
			} else if (idx == 36) {
				player.getCache().store("party_block_invites", packet.readByte() == 1);
			} else if (idx == 37) {
				player.getCache().store("android_inv_toggle", packet.readByte() == 1);
			} else if (idx == 38) {
				player.getCache().store("show_npc_kc", packet.readByte() == 1);
			} else if (idx == 39) {
				player.getCache().store("custom_ui", packet.readByte() == 1);
			} else if (idx == 40) {
				player.getCache().store("setting_hide_login_box", packet.readByte() == 1);
			} else if (idx == 41) {
				player.getCache().store("setting_block_global_friend", packet.readByte() == 1);
			}
			return;
		}

		boolean on = packet.readByte() == 1;
		player.getSettings().setGameSetting(idx, on);
		ActionSender.sendGameSettings(player);
	}
}
