package com.openrsc.server.model.entity.player;

import com.openrsc.server.model.PlayerAppearance;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.MessageType;

import java.util.HashMap;
import java.util.NoSuchElementException;

public class PlayerSettings {

	public static final int PRIVACY_BLOCK_CHAT_MESSAGES = 0,
		PRIVACY_BLOCK_PRIVATE_MESSAGES = 1,
		PRIVACY_BLOCK_TRADE_REQUESTS = 2,
		PRIVACY_BLOCK_DUEL_REQUESTS = 3,
		PRIVACY_HIDE_ONLINE_STATUS = 4;

	public static final int GAME_SETTING_AUTO_CAMERA = 0,
		GAME_SETTING_MOUSE_BUTTONS = 1,
		GAME_SETTING_SOUND_EFFECTS = 2;

	public static final String[] BLOCK_ALL_CACHES = {
		"setting_block_all_chat",
		"setting_block_all_private",
		"setting_block_all_trade",
		"setting_block_all_duel",
		"setting_hide_all_status"
	};

	private HashMap<Long, Long> attackedBy = new HashMap<Long, Long>();
	private HashMap<Integer, Long> attackedBy2 = new HashMap<Integer, Long>();

	private byte[] privacySettings = new byte[5];
	private boolean[] gameSettings = new boolean[3];

	private PlayerAppearance appearance;

	private Player player;

	PlayerSettings(Player player) {
		this.player = player;
	}


	public void setPrivacySetting(int i, byte b) {
		if (privacySettings[i] == b) {
			return;
		}
		privacySettings[i] = b;
		final boolean blockAll = b == 2;
		if (!player.getCache().hasKey(BLOCK_ALL_CACHES[i])
			|| player.getCache().getBoolean(BLOCK_ALL_CACHES[i]) != blockAll) {
			player.getCache().store(BLOCK_ALL_CACHES[i], blockAll);
		}
		if (i == 1) {
			for (Player pl : player.getWorld().getPlayers()) {
				if (pl.getSocial().isFriendsWith(player.getUsernameHash())
					&& pl.getIndex() != player.getIndex()) {
					ActionSender.sendFriendUpdate(pl, player.getUsernameHash(), player.getUsername(), player.getFormerName());
				}
			}
		} else if (i == 4) {
			if (!blockAll) {
				player.setHideOnline(b);
			}
		}
	}

	public byte getPrivacySetting(int i, boolean customClient) {
		if (customClient) {
			return privacySettings[i];
		} else {
			if (privacySettings[i] == 0) {
				return (byte)BlockingMode.None.id();
			} else {
				if (player.getCache().hasKey(BLOCK_ALL_CACHES[i]) && player.getCache().getBoolean(BLOCK_ALL_CACHES[i])) {
					return (byte)BlockingMode.All.id();
				} else {
					return (byte)BlockingMode.NonFriends.id();
				}
			}
		}
	}

	// These getter-setter aren't currently used
	/*public byte[] getPrivacySettings() {
		return privacySettings;
	}

	public void setPrivacySettings(byte[] privacySettings) {
		this.privacySettings = privacySettings;
	}*/

	public boolean getGameSetting(int i) {
		return gameSettings[i];
	}

	public boolean[] getGameSettings() {
		return gameSettings;
	}

	public void setGameSettings(boolean[] gameSettings) {
		this.gameSettings = gameSettings;
	}

	public void setGameSetting(int i, boolean b) {
		gameSettings[i] = b;
	}

	public PlayerAppearance getAppearance() {
		return appearance;
	}

	public void setAppearance(PlayerAppearance pa) {
		this.appearance = pa;
	}

	void addAttackedBy(Player player) {
		attackedBy.put(player.getUsernameHash(), System.currentTimeMillis());
	}
	public void addAttackedBy(Npc n) {
		attackedBy2.put(n.getID(), System.currentTimeMillis());
	}

	HashMap<Long, Long> getAttackedBy() {
		return attackedBy;
	}
	HashMap<Integer, Long> getAttackedBy2() {
		return attackedBy2;
	}

	public long lastAttackedBy(Player player) {
		Long time = attackedBy.get(player.getUsernameHash());
		if (time != null) {
			return time;
		}
		return 0;
	}
	long lastAttackedBy(Npc n) {
		Long time = attackedBy2.get(n.getID());
		if (time != null) {
			return time;
		}
		return 0;
	}

	public void toggleBlockChat(Player player) {
		boolean currentSetting;
		String cacheName = BLOCK_ALL_CACHES[PRIVACY_BLOCK_CHAT_MESSAGES];
		try {
			currentSetting = player.getCache().getBoolean(cacheName);
		} catch (NoSuchElementException e) {
			currentSetting = privacySettings[PRIVACY_BLOCK_CHAT_MESSAGES] == BlockingMode.All.id();
		}

		if (currentSetting) {
			player.playerServerMessage(MessageType.QUEST, "You will now see all chat messages");
		} else {
			player.playerServerMessage(MessageType.QUEST, "You will no longer see any chat messages from players");
		}
		currentSetting = !currentSetting;
		setPrivacySetting(PRIVACY_BLOCK_CHAT_MESSAGES, (byte)(currentSetting ? BlockingMode.All.id() : BlockingMode.None.id()));
		ActionSender.sendPrivacySettings(player);
	}

	public void toggleBlockPrivate(Player player) {
		boolean currentSetting;
		String cacheName = BLOCK_ALL_CACHES[PRIVACY_BLOCK_PRIVATE_MESSAGES];
		try {
			currentSetting = player.getCache().getBoolean(cacheName);
		} catch (NoSuchElementException e) {
			currentSetting = privacySettings[PRIVACY_BLOCK_PRIVATE_MESSAGES] == BlockingMode.All.id();
		}

		if (currentSetting) {
			player.playerServerMessage(MessageType.QUEST, "You will now see all private messages");
		} else {
			player.playerServerMessage(MessageType.QUEST, "You will no longer see any private messages from players");
		}
		currentSetting = !currentSetting;
		setPrivacySetting(PRIVACY_BLOCK_PRIVATE_MESSAGES, (byte)(currentSetting ? BlockingMode.All.id() : BlockingMode.None.id()));
		ActionSender.sendPrivacySettings(player);
	}

	public void toggleBlockTrade(Player player) {
		boolean currentSetting;
		String cacheName = BLOCK_ALL_CACHES[PRIVACY_BLOCK_TRADE_REQUESTS];
		try {
			currentSetting = player.getCache().getBoolean(cacheName);
		} catch (NoSuchElementException e) {
			currentSetting = privacySettings[PRIVACY_BLOCK_TRADE_REQUESTS] == BlockingMode.All.id();
		}

		if (currentSetting) {
			player.playerServerMessage(MessageType.QUEST, "You will now receive trade requests");
		} else {
			player.playerServerMessage(MessageType.QUEST, "You will no longer receive trade requests from players");
		}
		currentSetting = !currentSetting;
		setPrivacySetting(PRIVACY_BLOCK_TRADE_REQUESTS, (byte)(currentSetting ? BlockingMode.All.id() : BlockingMode.None.id()));
		ActionSender.sendPrivacySettings(player);
	}

	public void toggleBlockDuel(Player player) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.playerServerMessage(MessageType.QUEST, "Please log into a members world to do this toggle");
			return;
		}

		boolean currentSetting;
		String cacheName = BLOCK_ALL_CACHES[PRIVACY_BLOCK_DUEL_REQUESTS];
		try {
			currentSetting = player.getCache().getBoolean(cacheName);
		} catch (NoSuchElementException e) {
			currentSetting = privacySettings[PRIVACY_BLOCK_DUEL_REQUESTS] == BlockingMode.All.id();
		}

		if (currentSetting) {
			player.playerServerMessage(MessageType.QUEST, "You will now receive duel requests");
		} else {
			player.playerServerMessage(MessageType.QUEST, "You will no longer receive duel requests from players");
		}
		currentSetting = !currentSetting;
		setPrivacySetting(PRIVACY_BLOCK_DUEL_REQUESTS, (byte)(currentSetting ? BlockingMode.All.id() : BlockingMode.None.id()));
		ActionSender.sendPrivacySettings(player);
	}

	public enum BlockingMode {
		None(0),
		NonFriends(1),
		All(2);

		private int mode;

		BlockingMode(int mode) {
			this.mode = mode;
		}

		public int id() {
			return this.mode;
		}
	}
}
