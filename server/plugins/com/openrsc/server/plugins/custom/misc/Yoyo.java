package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.AppearanceId;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;

import static com.openrsc.server.plugins.RuneScript.delay;

public class Yoyo implements OpInvTrigger {
	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		player.setBusy(true);
		player.message("You play with the yoyo");

		doAnim(player);

		player.exitMorph(); // reset appearance in case they are not wielding the yoyo
		incrementYoyoPlays(player);
		player.setBusy(false);
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.YOYO.id();
	}

	public static void doAnim(Player player) {
		// start with yoyo in hand in case it's not already
		player.updateWornItems(AppearanceId.YOYO_IN_HAND);
		delay(1);

		fast_personal_anim_up_down(player); // spams local player only with packets, for a smooth local animation
		slow_global_anim_up_down(player); // all other players see this, a slow up-down animation

		// return yoyo to hand at end of animation
		player.updateWornItems(AppearanceId.YOYO_IN_HAND);
		delay(1);

		// When fast_personal_anim ends, the local player will get hit with a sped-up 3 frame long `slow_global_anim_up_down` which has been queued behind the packet spam
		// We will mask this by initiating phase 2: Crazy Yoyo
		fast_personal_anim_crazy_yoyo(player);
		slow_global_anim_crazy_yoyo(player);
	}

	private static void slow_global_anim_up_down(Player player) {
		player.updateWornItems(AppearanceId.YOYO_UP_DOWN_ANIM1);
		delay(1);
		player.updateWornItems(AppearanceId.YOYO_UP_DOWN_ANIM4);
		delay(1);
		player.updateWornItems(AppearanceId.YOYO_UP_DOWN_ANIM1);
		delay(1);
	}

	private static void slow_global_anim_crazy_yoyo(Player player) {
		// loop de loop but slow
		player.updateWornItems(AppearanceId.YOYO_CRAZY_1_OCLOCK);
		delay(1);
		player.updateWornItems(AppearanceId.YOYO_CRAZY_5_OCLOCK);
		delay(1);
		player.updateWornItems(AppearanceId.YOYO_CRAZY_9_OCLOCK);
		delay(1);
		player.updateWornItems(AppearanceId.YOYO_CRAZY_1_OCLOCK);
		delay(1);
		player.updateWornItems(AppearanceId.YOYO_CRAZY_4_OCLOCK);
		delay(1);
		player.updateWornItems(AppearanceId.YOYO_UP_DOWN_ANIM4);
		delay(1);
		player.updateWornItems(AppearanceId.YOYO_UP_DOWN_ANIM1);
		delay(1);
		player.updateWornItems(AppearanceId.YOYO_IN_HAND);
		delay(1);
	}

	public static void fast_personal_anim_up_down(Player player) {
		// this is reliant on the fact that the client can only process one packet per frame.
		// therefore, if you spam the player with packets, they will be spaced out over time
		int animationFrames = 6;
		for (int i = 0; i < animationFrames; i++) {
			player.updateWornItems(AppearanceId.YOYO_UP_DOWN_ANIM1);
			player.getWorld().getServer().getGameUpdater().updatePlayerAppearances(player);
		}
		for (int i = 0; i < animationFrames; i++) {
			player.updateWornItems(AppearanceId.YOYO_UP_DOWN_ANIM2);
			player.getWorld().getServer().getGameUpdater().updatePlayerAppearances(player);
		}
		for (int i = 0; i < animationFrames; i++) {
			player.updateWornItems(AppearanceId.YOYO_UP_DOWN_ANIM3);
			player.getWorld().getServer().getGameUpdater().updatePlayerAppearances(player);
		}
		for (int i = 0; i < animationFrames; i++) {
			player.updateWornItems(AppearanceId.YOYO_UP_DOWN_ANIM4);
			player.getWorld().getServer().getGameUpdater().updatePlayerAppearances(player);
		}
		for (int i = 0; i < animationFrames; i++) {
			player.updateWornItems(AppearanceId.YOYO_UP_DOWN_ANIM3);
			player.getWorld().getServer().getGameUpdater().updatePlayerAppearances(player);
		}
		for (int i = 0; i < animationFrames; i++) {
			player.updateWornItems(AppearanceId.YOYO_UP_DOWN_ANIM2);
			player.getWorld().getServer().getGameUpdater().updatePlayerAppearances(player);
		}
		for (int i = 0; i < animationFrames; i++) {
			player.updateWornItems(AppearanceId.YOYO_UP_DOWN_ANIM1);
			player.getWorld().getServer().getGameUpdater().updatePlayerAppearances(player);
		}
		player.updateWornItems(AppearanceId.YOYO_IN_HAND);
		player.getWorld().getServer().getGameUpdater().updatePlayerAppearances(player);
	}

	private static void fast_personal_anim_crazy_yoyo(Player player) {
		AppearanceId curYoyoAnim;
		int animationFrames = 4;
		// loop de loop
		for (int j = 0; j < 18; j++) {
			if (j == 5 || j == 17) {
				// logic is complicated by this, should have maybe just made YOYO_CRAZY_6_OCLOCK
				curYoyoAnim = AppearanceId.YOYO_UP_DOWN_ANIM4;
			} else if (j < 12) {
				curYoyoAnim = AppearanceId.getById(AppearanceId.YOYO_CRAZY_1_OCLOCK.id() + j + (j > 5 ? -1 : 0));
			} else {
				curYoyoAnim = AppearanceId.getById(AppearanceId.YOYO_CRAZY_1_OCLOCK.id() + j - 12);
			}
			for (int i = 0; i < animationFrames; i++) {
				player.updateWornItems(curYoyoAnim);
				player.getWorld().getServer().getGameUpdater().updatePlayerAppearances(player);
			}
		}

		// return to hand
		for (int i = 0; i < animationFrames; i++) {
			player.updateWornItems(AppearanceId.YOYO_UP_DOWN_ANIM3);
			player.getWorld().getServer().getGameUpdater().updatePlayerAppearances(player);
		}
		for (int i = 0; i < animationFrames; i++) {
			player.updateWornItems(AppearanceId.YOYO_UP_DOWN_ANIM2);
			player.getWorld().getServer().getGameUpdater().updatePlayerAppearances(player);
		}
		for (int i = 0; i < animationFrames; i++) {
			player.updateWornItems(AppearanceId.YOYO_UP_DOWN_ANIM1);
			player.getWorld().getServer().getGameUpdater().updatePlayerAppearances(player);
		}
		player.updateWornItems(AppearanceId.YOYO_IN_HAND);
		player.getWorld().getServer().getGameUpdater().updatePlayerAppearances(player);
	}

	private void incrementYoyoPlays(Player player) {
		long yoyo_plays = 1;
		if (player.getCache().hasKey("yoyo_plays")) {
			yoyo_plays = player.getCache().getLong("yoyo_plays") + 1;
		}
		player.getCache().store("yoyo_plays", yoyo_plays);
	}
}
