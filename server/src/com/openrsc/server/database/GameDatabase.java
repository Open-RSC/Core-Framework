package com.openrsc.server.database;

import com.openrsc.server.Server;
import com.openrsc.server.database.impl.mysql.queries.logging.StaffLog;
import com.openrsc.server.database.struct.*;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.external.ItemLoc;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.login.LoginRequest;
import com.openrsc.server.model.PlayerAppearance;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Bank;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Author: Kenix
 */
public abstract class GameDatabase extends GameDatabaseQueries{
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Server server;
	private volatile Boolean open;

	public GameDatabase(final Server server) {
		this.server = server;
		open = false;
	}

	protected abstract void openInternal();
	protected abstract void closeInternal();

	protected abstract void startTransaction() throws GameDatabaseException;
	protected abstract void commitTransaction() throws GameDatabaseException;
	protected abstract void rollbackTransaction() throws GameDatabaseException;

	protected abstract void initializeOnlinePlayers() throws GameDatabaseException;
	protected abstract boolean queryPlayerExists(int playerId) throws GameDatabaseException;
	protected abstract boolean queryPlayerExists(String username) throws GameDatabaseException;
	protected abstract String queryBanPlayer(String userNameToBan, Player bannedBy, long bannedForMinutes) throws GameDatabaseException;
	protected abstract NpcDrop[] queryNpcDrops() throws GameDatabaseException;
	protected abstract void queryAddDropLog(ItemDrop drop) throws GameDatabaseException;
	protected abstract PlayerLoginData queryPlayerLoginData(String username) throws GameDatabaseException;
	protected abstract PlayerRecoveryQuestions[] queryPlayerRecoveryChanges(Player player) throws GameDatabaseException;
	protected abstract String queryPlayerLoginIp(String username) throws GameDatabaseException;
	protected abstract LinkedPlayer[] queryLinkedPlayers(String ip) throws GameDatabaseException;
	protected abstract void queryInsertNpcSpawn(NPCLoc loc) throws GameDatabaseException;
	protected abstract void queryDeleteNpcSpawn(NPCLoc loc) throws GameDatabaseException;
	protected abstract void queryInsertObjectSpawn(GameObjectLoc loc) throws GameDatabaseException;
	protected abstract void queryDeleteObjectSpawn(GameObjectLoc loc) throws GameDatabaseException;
	protected abstract void queryInsertItemSpawn(ItemLoc loc) throws GameDatabaseException;
	protected abstract void queryDeleteItemSpawn(ItemLoc loc) throws GameDatabaseException;

	protected abstract PlayerData queryLoadPlayerData(Player player) throws GameDatabaseException;
	protected abstract PlayerInventory[] queryLoadPlayerInvItems(Player player) throws GameDatabaseException;
	protected abstract PlayerEquipped[] queryLoadPlayerEquipped(Player player) throws GameDatabaseException;
	protected abstract PlayerBank[] queryLoadPlayerBankItems(Player player) throws GameDatabaseException;
	protected abstract PlayerBankPreset[] queryLoadPlayerBankPresets(Player player) throws GameDatabaseException;
	protected abstract PlayerFriend[] queryLoadPlayerFriends(Player player) throws GameDatabaseException;
	protected abstract PlayerIgnore[] queryLoadPlayerIgnored(Player player) throws GameDatabaseException;
	protected abstract PlayerQuest[] queryLoadPlayerQuests(Player player) throws GameDatabaseException;
	protected abstract PlayerAchievement[] queryLoadPlayerAchievements(Player player) throws GameDatabaseException;
	protected abstract PlayerCache[] queryLoadPlayerCache(Player player) throws GameDatabaseException;
	protected abstract PlayerNpcKills[] queryLoadPlayerNpcKills(Player player) throws GameDatabaseException;
	protected abstract PlayerSkills[] queryLoadPlayerSkills(Player player) throws GameDatabaseException;
	protected abstract PlayerExperience[] queryLoadPlayerExperience(Player player) throws GameDatabaseException;

	protected abstract void querySavePlayerData(int playerId, PlayerData playerData) throws GameDatabaseException;
	protected abstract void querySavePlayerInventory(int playerId, PlayerInventory[] inventory) throws GameDatabaseException;
	protected abstract void querySavePlayerEquipped(int playerId, PlayerEquipped[] equipment) throws GameDatabaseException;
	protected abstract void querySavePlayerBank(int playerId, PlayerBank[] bank) throws GameDatabaseException;
	protected abstract void querySavePlayerBankPresets(int playerId, PlayerBankPreset[] bankPreset) throws GameDatabaseException;
	protected abstract void querySavePlayerFriends(int playerId, PlayerFriend[] friends) throws GameDatabaseException;
	protected abstract void querySavePlayerIgnored(int playerId, PlayerIgnore[] ignoreList) throws GameDatabaseException;
	protected abstract void querySavePlayerQuests(int playerId, PlayerQuest[] quests) throws GameDatabaseException;
	protected abstract void querySavePlayerAchievements(int playerId, PlayerAchievement[] achievements) throws GameDatabaseException;
	protected abstract void querySavePlayerCache(int playerId, PlayerCache[] cache) throws GameDatabaseException;
	protected abstract void querySavePlayerNpcKills(int playerId, PlayerNpcKills[] kills) throws GameDatabaseException;
	protected abstract void querySavePlayerSkills(int playerId, PlayerSkills[] currSkillLevels) throws GameDatabaseException;
	protected abstract void querySavePlayerExperience(int playerId, PlayerExperience[] experience) throws GameDatabaseException;

	public void open() {
		synchronized(open) {
			try {
				openInternal();
				initializeOnlinePlayers();
				open = true;
			} catch (final GameDatabaseException ex) {
				LOGGER.catching(ex);
				System.exit(1);
			}
		}
	}

	public void close() {
		synchronized(open) {
			closeInternal();
			open = false;
		}
	}

	public Player loadPlayer(final LoginRequest rq) {
		try {
			startTransaction();

			final Player loaded = new Player(getServer().getWorld(), rq);

			loadPlayerData(loaded);
			loadPlayerSkills(loaded);
			loadPlayerLastRecoveryChangeRequest(loaded);
			loadPlayerInventory(loaded);
			loadPlayerEquipment(loaded);
			loadPlayerBank(loaded);
			loadPlayerSocial(loaded);
			loadPlayerQuests(loaded);
			//loadPlayerAchievements(loaded);
			loadPlayerCache(loaded);
			loadPlayerLastSpellCast(loaded);
			loadPlayerNpcKills(loaded);

			commitTransaction();

			return loaded;
		} catch (final Exception ex) {
			try {
				rollbackTransaction();
			} catch (final Exception e) { }
			LOGGER.catching(ex);
			return null;
		}
	}

	public boolean savePlayer(final Player player) throws GameDatabaseException {
		try {
			startTransaction();

			if (!playerExists(player.getDatabaseID())) {
				LOGGER.error("ERROR SAVING : PLAYER DOES NOT EXIST : " + player.getUsername());
				return false;
			}

			savePlayerBank(player);
			savePlayerInventory(player);
			savePlayerEquipment(player);
			//savePlayerAchievements(player);
			savePlayerQuests(player);
			savePlayerCastTime(player);
			savePlayerCache(player);
			savePlayerNpcKills(player);
			savePlayerData(player);
			savePlayerSkills(player);
			savePlayerSocial(player);

			return true;
		} catch (final Exception ex) {
			rollbackTransaction();
			LOGGER.catching(ex);
			return false;
		}
	}

	public boolean playerExists(final int playerId) throws GameDatabaseException {
		return queryPlayerExists(playerId);
	}

	public boolean playerExists(final String username) throws GameDatabaseException {
		return queryPlayerExists(username);
	}

	public String playerLoginIp(final String username) throws GameDatabaseException {
		return queryPlayerLoginIp(username);
	}

	public LinkedPlayer[] linkedPlayers(final String ip) throws GameDatabaseException {
		return queryLinkedPlayers(ip);
	}

	public void addNpcSpawn(final NPCLoc loc) throws GameDatabaseException {
		queryInsertNpcSpawn(loc);
	}

	public void removeNpcSpawn(final NPCLoc loc) throws GameDatabaseException {
		queryDeleteNpcSpawn(loc);
	}

	public void addObjectSpawn(final GameObjectLoc loc) throws GameDatabaseException {
		queryInsertObjectSpawn(loc);
	}

	public void removeObjectSpawn(final GameObjectLoc loc) throws GameDatabaseException {
		queryDeleteObjectSpawn(loc);
	}

	public void addItemSpawn(final ItemLoc loc) throws GameDatabaseException {
		queryInsertItemSpawn(loc);
	}

	public void removeItemSpawn(final ItemLoc loc) throws GameDatabaseException {
		queryDeleteItemSpawn(loc);
	}

	public String banPlayer(String userNameToBan, Player bannedBy, long bannedForMinutes) {
		final Player p = getServer().getWorld().getPlayer(DataConversions.usernameToHash(userNameToBan));

		if (p != null) {
			p.unregister(true, "You have been banned by " + bannedBy.getUsername() + " " + (bannedForMinutes == -1 ? "permanently" : " for " + bannedForMinutes + " minutes"));
		}

		if (bannedForMinutes == 0) {
			getServer().getGameLogger().addQuery(new StaffLog(bannedBy, 11, p, bannedBy.getUsername() + " was unbanned by " + bannedBy.getUsername()));
		} else {
			getServer().getGameLogger().addQuery(new StaffLog(bannedBy, 11, p, bannedBy.getUsername() + " was banned by " + bannedBy.getUsername() + " " + (bannedForMinutes == -1 ? "permanently" : " for " + bannedForMinutes + " minutes")));
		}

		try {
			return queryBanPlayer(userNameToBan, bannedBy, bannedForMinutes);
		} catch (final GameDatabaseException e) {
			return "There is not an account by that username";
		}
	}

	public PlayerLoginData getPlayerLoginData(final String username) throws GameDatabaseException {
		return queryPlayerLoginData(username);
	}

	public NpcDrop[] getNpcDrops() throws GameDatabaseException {
		return queryNpcDrops();
	}

	private void loadPlayerData(final Player player) throws GameDatabaseException {
		final PlayerData playerData = queryLoadPlayerData(player);

		player.setOwner(playerData.playerId);
		player.setDatabaseID(playerData.playerId);
		player.setGroupID(playerData.groupId);
		player.setCombatStyle((byte) playerData.combatStyle);
		player.setLastLogin(playerData.loginDate);
		player.setLastIP(playerData.loginIp);
		player.setInitialLocation(new Point(playerData.xLocation, playerData.yLocation));

		player.setFatigue(playerData.fatigue);
		player.setKills(playerData.kills);
		player.setDeaths(playerData.deaths);
		player.setKills2(playerData.kills2);
		player.setIronMan(playerData.ironMan);
		player.setIronManRestriction(playerData.ironManRestriction);
		player.setHCIronmanDeath(playerData.hcIronManDeath);
		player.setQuestPoints(playerData.questPoints);

		player.getSettings().setPrivacySetting(PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES, playerData.blockChat); // done
		player.getSettings().setPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, playerData.blockPrivate);
		player.getSettings().setPrivacySetting(PlayerSettings.PRIVACY_BLOCK_TRADE_REQUESTS, playerData.blockTrade);
		player.getSettings().setPrivacySetting(PlayerSettings.PRIVACY_BLOCK_DUEL_REQUESTS, playerData.blockDuel);

		player.getSettings().setGameSetting(PlayerSettings.GAME_SETTING_AUTO_CAMERA, playerData.cameraAuto);
		player.getSettings().setGameSetting(PlayerSettings.GAME_SETTING_MOUSE_BUTTONS, playerData.oneMouse);
		player.getSettings().setGameSetting(PlayerSettings.GAME_SETTING_SOUND_EFFECTS, playerData.soundOff);

		player.setBankSize(playerData.bankSize);

		PlayerAppearance pa = new PlayerAppearance(
			playerData.hairColour,
			playerData.topColour,
			playerData.trouserColour,
			playerData.skinColour,
			playerData.headSprite,
			playerData.bodySprite
		);

		player.getSettings().setAppearance(pa);
		player.setMale(playerData.male);
		player.setWornItems(player.getSettings().getAppearance().getSprites());
	}

	private void loadPlayerInventory(final Player player) throws GameDatabaseException {
		final Inventory inv = new Inventory(player);
		final PlayerInventory[] invItems = queryLoadPlayerInvItems(player);

		for (int i = 0; i < invItems.length; i++) {
			Item item = new Item(invItems[i].itemId, invItems[i].amount);
			ItemDefinition itemDef = item.getDef(player.getWorld());
			item.setWielded(false);
			if (item.isWieldable(player.getWorld()) && invItems[i].wielded) {
				if (itemDef != null) {
					if (!getServer().getConfig().WANT_EQUIPMENT_TAB)
						item.setWielded(true);
						inv.add(item, false);
					}
					player.updateWornItems(itemDef.getWieldPosition(), itemDef.getAppearanceId(), itemDef.getWearableId(), true);
			} else
				inv.add(item, false);
		}

		player.setInventory(inv);
	}

	private void loadPlayerEquipment(final Player player) throws GameDatabaseException {
		if (getServer().getConfig().WANT_EQUIPMENT_TAB) {
			final Equipment equipment = new Equipment(player);
			final PlayerEquipped[] equippedItems = queryLoadPlayerEquipped(player);

			for (final PlayerEquipped equippedItem : equippedItems) {
				final Item item = new Item(equippedItem.itemId, equippedItem.amount);
				final ItemDefinition itemDef = item.getDef(player.getWorld());
				if (item.isWieldable(player.getWorld())) {
					equipment.equip(itemDef.getWieldPosition(), item);
					player.updateWornItems(itemDef.getWieldPosition(), itemDef.getAppearanceId(),
						itemDef.getWearableId(), true);
				}
			}

			player.setEquipment(equipment);
		}
	}

	private void loadPlayerBank(final Player player) throws GameDatabaseException {
		final PlayerBank[] bankItems = queryLoadPlayerBankItems(player);
		final Bank bank = new Bank(player);
		for (int i = 0; i < bankItems.length; i++) {
			bank.add(new Item(bankItems[i].itemId, bankItems[i].amount));
		}
		if (getServer().getConfig().WANT_BANK_PRESETS) {
			final PlayerBankPreset bankPresets[] = queryLoadPlayerBankPresets(player);
			for (PlayerBankPreset bankPreset : bankPresets) {
				final int slot = bankPreset.slot;
				final byte[] inventoryItems = bankPreset.inventory;
				final byte[] equipmentItems = bankPreset.equipment;
				bank.loadPreset(slot, inventoryItems, equipmentItems);
			}
		}
		player.setBank(bank);
	}

	private void loadPlayerSocial(final Player player) throws GameDatabaseException {
		player.getSocial().addFriends(queryLoadPlayerFriends(player));
		player.getSocial().addIgnore(queryLoadPlayerIgnored(player));
	}

	private void loadPlayerQuests(final Player player) throws GameDatabaseException {
		final PlayerQuest[] quests = queryLoadPlayerQuests(player);

		for (int i = 0; i < quests.length; i++) {
			player.setQuestStage(quests[i].questId, quests[i].stage);
		}

		player.setQuestPoints(player.calculateQuestPoints());
	}

	private void loadPlayerAchievements(final Player player) throws GameDatabaseException {
		final PlayerAchievement achievements[] = queryLoadPlayerAchievements(player);
		for (int i = 0; i < achievements.length; i++) {
			player.setAchievementStatus(achievements[i].achievementId, achievements[i].status);
		}
	}

	private void loadPlayerCache(final Player player) throws GameDatabaseException {
		final PlayerCache playerCache[] = queryLoadPlayerCache(player);
		for (int i = 0; i < playerCache.length; i++) {
			final int identifier = playerCache[i].type;
			final String key = playerCache[i].key;
			switch(identifier) {
				case 0:
					player.getCache().put(key, Integer.parseInt(playerCache[i].value));
					break;
				case 1:
					player.getCache().put(key, playerCache[i].value);
					break;
				case 2:
					player.getCache().put(key, Boolean.parseBoolean(playerCache[i].value));
					break;
				case 3:
					player.getCache().put(key, Long.parseLong(playerCache[i].value));
					break;
			}
		}
	}

	private void loadPlayerLastSpellCast(final Player player) {
		try {
			player.setCastTimer(player.getCache().getLong("last_spell_cast"));
		} catch (Throwable t) {
			player.setCastTimer();
		}
	}

	private void loadPlayerNpcKills(final Player player) throws GameDatabaseException {
		final PlayerNpcKills kills[] = queryLoadPlayerNpcKills(player);
		for (PlayerNpcKills kill : kills) {
			final int key = kill.npcId;
			final int value = kill.killCount;
			player.getKillCache().put(key, value);
		}
	}

	private void loadPlayerSkills(final Player player) throws GameDatabaseException {
		player.getSkills().loadExp(queryLoadPlayerExperience(player));
		player.getSkills().loadLevels(queryLoadPlayerSkills(player));
	}

	private void loadPlayerLastRecoveryChangeRequest(final Player player) throws GameDatabaseException {
		long dateSet = 0;
		final PlayerRecoveryQuestions recoveryChanges[] = queryPlayerRecoveryChanges(player);
		for (PlayerRecoveryQuestions recoveryChange : recoveryChanges) {
			dateSet = Math.max(dateSet, recoveryChange.dateSet);
		}
		player.setLastRecoveryChangeRequest(dateSet);
	}

	private void savePlayerData(final Player player) throws GameDatabaseException {
		querySavePlayerData(player);
	}

	private void savePlayerInventory(final Player player) throws GameDatabaseException {
		querySavePlayerInventory(player);
	}

	private void savePlayerEquipment(final Player player) throws GameDatabaseException {
		querySavePlayerEquipped(player);
	}

	private void savePlayerBank(final Player player) throws GameDatabaseException {
		querySavePlayerBank(player);
		querySavePlayerBankPresets(player);
	}

	private void savePlayerSocial(final Player player) throws GameDatabaseException {
		querySavePlayerFriends(player);
		querySavePlayerIgnored(player);
	}

	private void savePlayerQuests(final Player player) throws GameDatabaseException {
		querySavePlayerQuests(player);
	}

	private void savePlayerAchievements(final Player player) throws GameDatabaseException {
		querySavePlayerAchievements(player);
	}

	public void savePlayerCache(final Player player) throws GameDatabaseException {
		player.getCache().store("last_spell_cast", player.getCastTimer());
		querySavePlayerCache(player);
	}

	private void savePlayerNpcKills(final Player player) throws GameDatabaseException {
		if(player.getKillCacheUpdated()) {
			querySavePlayerNpcKills(player);
			player.setKillCacheUpdated(false);
		}
	}

	private void savePlayerSkills(final Player player) throws GameDatabaseException {
		querySavePlayerSkills(player);
		querySavePlayerExperience(player);
	}

	private void savePlayerCastTime(final Player player) {
		player.getCache().store("last_spell_cast", player.getCastTimer());
	}

	public void addDropLog(final Player player, final Npc npc, final int dropId, final int dropAmount) throws GameDatabaseException {
		final ItemDrop drop = new ItemDrop();
		drop.itemId = dropId;
		drop.npcId = npc.getID();
		drop.playerId = player.getDatabaseID();
		drop.amount = dropAmount;
		queryAddDropLog(drop);
	}

	public final Server getServer() {
		return server;
	}

	public boolean isOpen() {
		return open;
	}

	protected void querySavePlayerData(Player player) throws GameDatabaseException {
		final PlayerData playerData = new PlayerData();

		playerData.combatLevel = player.getCombatLevel();
		playerData.totalLevel = player.getSkills().getTotalLevel();
		playerData.xLocation = player.getX();
		playerData.yLocation = player.getY();
		playerData.fatigue = player.getFatigue();
		playerData.kills = player.getKills();
		playerData.deaths = player.getDeaths();
		playerData.kills2 = player.getKills2();
		playerData.ironMan = player.getIronMan();
		playerData.ironManRestriction = player.getIronManRestriction();
		playerData.hcIronManDeath = player.getHCIronmanDeath();
		playerData.questPoints = player.calculateQuestPoints();
		playerData.hairColour = player.getSettings().getAppearance().getHairColour();
		playerData.topColour = player.getSettings().getAppearance().getTopColour();
		playerData.trouserColour = player.getSettings().getAppearance().getTrouserColour();
		playerData.skinColour = player.getSettings().getAppearance().getSkinColour();
		playerData.headSprite = player.getSettings().getAppearance().getHead();
		playerData.bodySprite = player.getSettings().getAppearance().getBody();
		playerData.male = player.isMale();
		playerData.combatStyle = player.getCombatStyle();
		playerData.muteExpires = player.getMuteExpires();
		playerData.bankSize = player.getBankSize();
		playerData.groupId = player.getGroupID();
		playerData.blockChat = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES);
		playerData.blockPrivate = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES);
		playerData.blockTrade = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_TRADE_REQUESTS);
		playerData.blockDuel = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_DUEL_REQUESTS);
		playerData.cameraAuto = player.getSettings().getGameSetting(PlayerSettings.GAME_SETTING_AUTO_CAMERA);
		playerData.oneMouse = player.getSettings().getGameSetting(PlayerSettings.GAME_SETTING_MOUSE_BUTTONS);
		playerData.soundOff = player.getSettings().getGameSetting(PlayerSettings.GAME_SETTING_SOUND_EFFECTS);
		playerData.playerId = player.getDatabaseID();

		querySavePlayerData(player.getDatabaseID(), playerData);
	}

	protected void querySavePlayerInventory(Player player) throws GameDatabaseException {
		final int invSize = player.getInventory().size();
		final PlayerInventory[] inventory = new PlayerInventory[invSize];

		for(int i = 0; i < invSize; i++) {
			inventory[i] = new PlayerInventory();
			inventory[i].itemId = player.getInventory().get(i).getID();
			inventory[i].amount = player.getInventory().get(i).getAmount();
			inventory[i].wielded = player.getInventory().get(i).isWielded();
		}

		querySavePlayerInventory(player.getDatabaseID(), inventory);
	}

	protected void querySavePlayerEquipped(Player player) throws GameDatabaseException {
		if (getServer().getConfig().WANT_EQUIPMENT_TAB) {
			final int equipSize = Equipment.slots;

			final ArrayList<PlayerEquipped> list = new ArrayList<>();

			for (int i = 0; i < equipSize; i++) {
				final Item item = player.getEquipment().get(i);
				if(item != null) {
					final PlayerEquipped equipment = new PlayerEquipped();
					equipment.itemId = player.getEquipment().get(i).getID();
					equipment.amount = player.getEquipment().get(i).getAmount();
					list.add(equipment);
				}
			}

			final PlayerEquipped[] equippedItems = list.toArray(new PlayerEquipped[list.size()]);

			querySavePlayerEquipped(player.getDatabaseID(), equippedItems);
		}
	}

	protected void querySavePlayerBank(Player player) throws GameDatabaseException {
		final int bankSize = player.getBank().size();
		final PlayerBank[] bank = new PlayerBank[bankSize];

		for(int i = 0; i < bankSize; i++){
			bank[i] = new PlayerBank();
			bank[i].itemId = player.getBank().get(i).getID();
			bank[i].amount = player.getBank().get(i).getAmount();
		}

		querySavePlayerBank(player.getDatabaseID(), bank);
	}

	protected void querySavePlayerBankPresets(Player player) throws GameDatabaseException {
		try {
			if (getServer().getConfig().WANT_BANK_PRESETS) {
				final ArrayList<PlayerBankPreset> list = new ArrayList<>();

				for (int k = 0; k < Bank.PRESET_COUNT; k++) {
					if (player.getBank().presets[k].changed) {
						ByteArrayOutputStream inventoryBuffer = new ByteArrayOutputStream();
						DataOutputStream inventoryWriter = new DataOutputStream(inventoryBuffer);
						for (final Item inventoryItem : player.getBank().presets[k].inventory) {
							if (inventoryItem.getID() == -1)
								inventoryWriter.writeByte(-1);
							else {
								inventoryWriter.writeShort(inventoryItem.getID());
								if (inventoryItem.getDef(player.getWorld()) != null && inventoryItem.getDef(player.getWorld()).isStackable())
									inventoryWriter.writeInt(inventoryItem.getAmount());
							}

						}
						inventoryWriter.close();

						final ByteArrayOutputStream equipmentBuffer = new ByteArrayOutputStream();
						final DataOutputStream equipmentWriter = new DataOutputStream(equipmentBuffer);
						for (Item equipmentItem : player.getBank().presets[k].equipment) {
							if (equipmentItem.getID() == -1)
								equipmentWriter.writeByte(-1);
							else {
								equipmentWriter.writeShort(equipmentItem.getID());
								if (equipmentItem.getDef(player.getWorld()) != null && equipmentItem.getDef(player.getWorld()).isStackable())
									equipmentWriter.writeInt(equipmentItem.getAmount());
							}

						}
						equipmentWriter.close();

						final PlayerBankPreset preset = new PlayerBankPreset();
						preset.inventory = inventoryBuffer.toByteArray();
						preset.equipment = equipmentBuffer.toByteArray();
						preset.slot = k;
						list.add(preset);
					}
				}

				final PlayerBankPreset[] presets = list.toArray(new PlayerBankPreset[list.size()]);

				querySavePlayerBankPresets(player.getDatabaseID(), presets);
			}
		} catch (final IOException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	protected void querySavePlayerFriends(Player player) throws GameDatabaseException {
		final ArrayList<PlayerFriend> list = new ArrayList<>();
		final Set<Map.Entry<Long, Integer>> entrySet = player.getSocial().getFriendList().entrySet();

		for (final Map.Entry<Long, Integer> entry : entrySet) {
			PlayerFriend friend = new PlayerFriend();
			friend.playerHash = entry.getKey();
			list.add(friend);
		}

		final PlayerFriend[] friends = list.toArray(new PlayerFriend[list.size()]);

		querySavePlayerFriends(player.getDatabaseID(), friends);
	}

	protected void querySavePlayerIgnored(Player player) throws GameDatabaseException {
		final int ignoreSize = player.getSocial().getIgnoreList().size();
		final PlayerIgnore[] ignores = new PlayerIgnore[ignoreSize];

		for(int i = 0; i < ignoreSize; i++) {
			ignores[i] = new PlayerIgnore();
			ignores[i].playerHash = player.getSocial().getIgnoreList().get(i);
		}

		querySavePlayerIgnored(player.getDatabaseID(), ignores);
	}

	protected void querySavePlayerQuests(Player player) throws GameDatabaseException {
		final ArrayList<PlayerQuest> list = new ArrayList<>();
		final Set<Integer> keys = player.getQuestStages().keySet();

		for (final int id : keys) {
			final PlayerQuest quest = new PlayerQuest();
			quest.questId = id;
			quest.stage = player.getQuestStage(id);
			list.add(quest);
		}

		final PlayerQuest[] quests = list.toArray(new PlayerQuest[list.size()]);

		querySavePlayerQuests(player.getDatabaseID(), quests);
	}

	protected void querySavePlayerAchievements(Player player) throws GameDatabaseException {

	}

	protected void querySavePlayerCache(Player player) throws GameDatabaseException {
		final int cacheSize = player.getCache().getCacheMap().size();
		final PlayerCache[] caches = new PlayerCache[cacheSize];

		int i = 0;
		for (final String key : player.getCache().getCacheMap().keySet()) {
			final Object o = player.getCache().getCacheMap().get(key);

			caches[i] = new PlayerCache();
			caches[i].value = o != null ? o.toString() : null;
			caches[i].key = key;

			if(o instanceof Integer) {
				caches[i].type = 0;
			} else if(o instanceof String) {
				caches[i].type = 1;
			} else if(o instanceof Boolean) {
				caches[i].type = 2;
			} else if(o instanceof Long) {
				caches[i].type = 3;
			}
			i++;
		}

		querySavePlayerCache(player.getDatabaseID(), caches);
	}

	protected void querySavePlayerNpcKills(Player player) throws GameDatabaseException {
		final int killsSize = player.getKillCache().size();
		final PlayerNpcKills[] killMap = new PlayerNpcKills[killsSize];

		int i = 0;
		for (final Map.Entry<Integer, Integer> e : player.getKillCache().entrySet()) {
			killMap[i] = new PlayerNpcKills();
			killMap[i].killCount = e.getValue();
			killMap[i].npcId = e.getKey();

			i++;
		}

		querySavePlayerNpcKills(player.getDatabaseID(), killMap);
	}

	protected void querySavePlayerSkills(Player player) throws GameDatabaseException {
		final int skillsSize = getServer().getConstants().getSkills().getSkillsCount();
		final PlayerSkills[] skills = new PlayerSkills[skillsSize];

		for(int i = 0; i < skillsSize; i++) {
			skills[i] = new PlayerSkills();
			skills[i].skillId = i;
			skills[i].skillCurLevel = player.getSkills().getLevel(i);
		}

		querySavePlayerSkills(player.getDatabaseID(), skills);
	}

	protected void querySavePlayerExperience(Player player) throws GameDatabaseException {
		final int skillsSize = getServer().getConstants().getSkills().getSkillsCount();
		final PlayerExperience[] skills = new PlayerExperience[skillsSize];

		for(int i = 0; i < skillsSize; i++) {
			skills[i] = new PlayerExperience();
			skills[i].skillId = i;
			skills[i].experience = player.getSkills().getExperience(i);
		}

		querySavePlayerExperience(player.getDatabaseID(), skills);
	}
}

