package com.openrsc.server.service;

import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.AppearanceId;
import com.openrsc.server.database.GameDatabase;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.struct.*;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.login.LoginRequest;
import com.openrsc.server.model.PlayerAppearance;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Bank;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
import com.openrsc.server.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class PlayerService implements IPlayerService {
    private static final Logger LOGGER = LogManager.getLogger(PlayerService.class);
    private final GameDatabase database;
    private final World world;
    private final ServerConfiguration configuration;

    public PlayerService(
            World world,
            ServerConfiguration configuration,
            GameDatabase database
    ) {
        this.world = world;
        this.configuration = configuration;
        this.database = database;
    }

    @Override
    public Player loadPlayer(final LoginRequest rq) {
        try {
            final Player loaded = new Player(world, rq);

            database.atomically(() -> {
                loadPlayerData(loaded);
                loadPlayerSkills(loaded);
                loadPlayerLastRecoveryChangeRequest(loaded);
                loadPlayerEquipment(loaded);
                loadPlayerInventory(loaded);
                loadPlayerBank(loaded);
                loadPlayerBankPresets(loaded);
                loadPlayerSocial(loaded);
                loadPlayerQuests(loaded);
                //loadPlayerAchievements(loaded);
                loadPlayerCache(loaded);
                loadPlayerLastSpellCast(loaded);
                loadPlayerNpcKills(loaded);
            });

            return loaded;
        } catch (final Exception ex) {
            LOGGER.error(
                    MessageFormat.format("Unable to load player from database: {0}", rq.getUsername()),
                    ex
            );
            return null;
        }
    }

    @Override
    public boolean savePlayer(final Player player) throws GameDatabaseException {
        try {
            if (!database.playerExists(player.getDatabaseID())) {
                LOGGER.error("ERROR SAVING : PLAYER DOES NOT EXIST : {}", player.getUsername());
                return false;
            }
            return database.atomically(() -> {
                savePlayerBankPresets(player);
                savePlayerInventory(player);
                savePlayerEquipment(player);
                savePlayerBank(player);
                //savePlayerAchievements(player);
                savePlayerQuests(player);
                savePlayerCastTime(player);
                savePlayerCache(player);
                savePlayerNpcKills(player);
                savePlayerData(player);
                savePlayerSkills(player);
                savePlayerSocial(player);
            });
        } catch (final Exception ex) {
            LOGGER.error(
                    MessageFormat.format("Unable to save player to database: {}", player.getUsername()),
                    ex
            );
            return false;
        }
    }

    private void loadPlayerCache(final Player player) throws GameDatabaseException {
        final PlayerCache[] playerCache = database.queryLoadPlayerCache(player);
        for (PlayerCache cache : playerCache) {
            final int identifier = cache.type;
            final String key = cache.key;
            switch (identifier) {
                case 0:
                    player.getCache().put(key, Integer.parseInt(cache.value));
                    break;
                case 1:
                    player.getCache().put(key, cache.value);
                    break;
                case 2:
                    player.getCache().put(key, Boolean.parseBoolean(cache.value));
                    break;
                case 3:
                    player.getCache().put(key, Long.parseLong(cache.value));
                    break;
            }
        }
    }

    private void loadPlayerData(final Player player) throws GameDatabaseException {
        final PlayerData playerData = database.queryLoadPlayerData(player);

        player.setOwner(playerData.playerId);
        player.setDatabaseID(playerData.playerId);
        player.setGroupID(playerData.groupId);
        player.setUsername(playerData.username);
        player.setTotalLevel(playerData.totalLevel);
        /*if (player.isUsingCustomClient()) {
            player.setCombatStyle((byte) playerData.combatStyle);
        } else {
            // authentically, server does not remember your combat style & can't tell you it.
            player.setCombatStyle((byte) Skills.CONTROLLED_MODE);
        }*/
		player.setCombatStyle((byte) playerData.combatStyle);

        player.setLastLogin(playerData.loginDate);
        player.setLastIP(playerData.loginIp);
        player.setInitialLocation(new Point(playerData.xLocation, playerData.yLocation));

        player.setFatigue(playerData.fatigue);
        player.setKills(playerData.kills);
        player.setDeaths(playerData.deaths);
        player.setNpcKills(playerData.npcKills);
        if (configuration.SPAWN_IRON_MAN_NPCS) {
            player.setIronMan(playerData.ironMan);
            player.setIronManRestriction(playerData.ironManRestriction);
            player.setHCIronmanDeath(playerData.hcIronManDeath);
        }
        player.setQuestPoints(playerData.questPoints);

        player.getSettings().setPrivacySetting(PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES, playerData.blockChat); // done
        player.getSettings().setPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, playerData.blockPrivate);
        player.getSettings().setPrivacySetting(PlayerSettings.PRIVACY_BLOCK_TRADE_REQUESTS, playerData.blockTrade);
        player.getSettings().setPrivacySetting(PlayerSettings.PRIVACY_BLOCK_DUEL_REQUESTS, playerData.blockDuel);

        player.getSettings().setGameSetting(PlayerSettings.GAME_SETTING_AUTO_CAMERA, playerData.cameraAuto);
        player.getSettings().setGameSetting(PlayerSettings.GAME_SETTING_MOUSE_BUTTONS, playerData.oneMouse);
        player.getSettings().setGameSetting(PlayerSettings.GAME_SETTING_SOUND_EFFECTS, playerData.soundOff);

        PlayerAppearance pa = new PlayerAppearance(
                playerData.hairColour,
                playerData.topColour,
                playerData.trouserColour,
                playerData.skinColour,
                playerData.headSprite,
                playerData.bodySprite
        );
        if (!pa.isValid()) {
            pa = new PlayerAppearance(
                    0, 0, 0, 0, 1, 2
            );
        }

        player.getSettings().setAppearance(pa);
        player.setMale(playerData.male);
        player.setWornItems(player.getSettings().getAppearance().getSprites());
    }

    private void loadPlayerInventory(final Player player) throws GameDatabaseException {
        final PlayerInventory[] invItems = database.queryLoadPlayerInvItems(player.getDatabaseID());
        final Inventory inv = new Inventory(player, invItems);

        player.getCarriedItems().setInventory(inv);
    }

    @Override
    public List<Item> retrievePlayerInventory(final String username) throws GameDatabaseException {
        int playerId = database.playerIdFromUsername(username);
        if (playerId == -1) {
            throw new GameDatabaseException(GameDatabase.class, "Could not find player.");
        }
        List<Item> items = new ArrayList<>();
        final PlayerInventory[] invItems = database.queryLoadPlayerInvItems(playerId);
        for (PlayerInventory i : invItems) {
            items.add(i.item);
        }
        return items;
    }

    private void loadPlayerEquipment(final Player player) throws GameDatabaseException {
        if (configuration.WANT_EQUIPMENT_TAB) {
            final Equipment equipment = new Equipment(player);
            synchronized (equipment.getList()) {
                final PlayerEquipped[] equippedItems = database.queryLoadPlayerEquipped(player);

                // check if player is morphed
                ItemDefinition morph = null;
                for (final PlayerEquipped equippedItem : equippedItems) {
                    final Item item = new Item(equippedItem.itemId, equippedItem.itemStatus);
                    final ItemDefinition itemDef = item.getDef(player.getWorld());
                    final AppearanceId appearance = AppearanceId.getById(itemDef.getAppearanceId());
                    if (appearance.getSuggestedWieldPosition() == AppearanceId.SLOT_MORPHING_RING) {
                        morph = itemDef;
                    }
                }

                // put items into slots & update appearance (if not morphed)
                for (final PlayerEquipped equippedItem : equippedItems) {
                    final Item item = new Item(equippedItem.itemId, equippedItem.itemStatus);
                    final ItemDefinition itemDef = item.getDef(player.getWorld());
                    if (item.isWieldable(player.getWorld())) {
                        equipment.getList()[itemDef.getWieldPosition()] = item;
                        if (morph == null) {
                            if (itemDef.getWieldPosition() < 12) {
                                player.updateWornItems(itemDef.getWieldPosition(), itemDef.getAppearanceId(),
                                        itemDef.getWearableId(), true);
                            }
                        }
                    }
                }

                // apply morph
                if (morph != null) {
                    player.updateWornItems(morph.getWieldPosition(), morph.getAppearanceId(),
                            morph.getWearableId(), true);
                }

                player.getCarriedItems().setEquipment(equipment);
            }
        } else
            player.getCarriedItems().setEquipment(new Equipment(player));
    }

    private void loadPlayerBank(final Player player) throws GameDatabaseException {
        final PlayerBank[] bankItems = database.queryLoadPlayerBankItems(player.getDatabaseID());
        final Bank bank = new Bank(player);
        for (int i = 0; i < bankItems.length; i++) {
            bank.getItems().add(new Item(bankItems[i].itemId, bankItems[i].itemStatus));
        }
        player.setBank(bank);
    }

    @Override
    public List<Item> retrievePlayerBank(final String username) throws GameDatabaseException {
        int playerId = database.playerIdFromUsername(username);
        if (playerId == -1) {
            throw new GameDatabaseException(GameDatabase.class, "Could not find player.");
        }
        final PlayerBank[] bankItems = database.queryLoadPlayerBankItems(playerId);
        List<Item> bank = new ArrayList<Item>();
        for (int i = 0; i < bankItems.length; i++) {
            bank.add(new Item(bankItems[i].itemId, bankItems[i].itemStatus));
        }
        return bank;
    }

    private void loadPlayerBankPresets(final Player player) throws GameDatabaseException {

        //Check the player is on a world with bank presets
        if (!configuration.WANT_BANK_PRESETS)
            return;

        //Make sure the player's bank isn't null
        if (player.getBank() == null)
            return;

        final PlayerBankPreset[] bankPresets = database.queryLoadPlayerBankPresets(player);

        for (PlayerBankPreset bankPreset : bankPresets) {
            final int slot = bankPreset.slot;
            final byte[] inventoryItems = bankPreset.inventory;
            final byte[] equipmentItems = bankPreset.equipment;
            player.getBank().getBankPreset(slot).loadFromByteData(inventoryItems, equipmentItems);
        }

    }

    private void loadPlayerSocial(final Player player) throws GameDatabaseException {
        player.getSocial().addFriends(database.queryLoadPlayerFriends(player));
        player.getSocial().addIgnore(database.queryLoadPlayerIgnored(player));
    }

    private void loadPlayerQuests(final Player player) throws GameDatabaseException {
        final PlayerQuest[] quests = database.queryLoadPlayerQuests(player);

        for (PlayerQuest quest : quests) {
            player.setQuestStage(quest.questId, quest.stage);
        }

        player.setQuestPoints(player.calculateQuestPoints());
    }

    private void loadPlayerAchievements(final Player player) throws GameDatabaseException {
        final PlayerAchievement[] achievements = database.queryLoadPlayerAchievements(player);
        for (PlayerAchievement achievement : achievements) {
            player.setAchievementStatus(achievement.achievementId, achievement.status);
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
        final PlayerNpcKills[] kills = database.queryLoadPlayerNpcKills(player);
        for (PlayerNpcKills kill : kills) {
            final int key = kill.npcId;
            final int value = kill.killCount;
            player.getKillCache().put(key, value);
        }
    }

    private void loadPlayerSkills(final Player player) throws GameDatabaseException {
        final PlayerExperience[] exp = database.queryLoadPlayerExperience(player.getDatabaseID());
        player.getSkills().loadExp(exp);
        player.getSkills().loadLevels(database.queryLoadPlayerSkills(player, false));
        try {
            player.getSkills().loadMaxLevels(database.queryLoadPlayerSkills(player, true));
        } catch (NoSuchElementException e) {
            // from old architecture to new one, compute the expected max level and store back in
            PlayerSkills[] lvls = player.getSkills().asLevels(exp);
            player.getSkills().loadMaxLevels(lvls);
            database.queryInitializeMaxStats(player.getDatabaseID());
            database.querySavePlayerMaxSkills(player.getDatabaseID(), lvls);
        }
        player.getSkills().loadExpCapped(database.queryLoadPlayerExperienceCapped(player.getDatabaseID()));
    }

    private void loadPlayerLastRecoveryChangeRequest(final Player player) throws GameDatabaseException {
        long dateSet = 0;
        final PlayerRecoveryQuestions[] recoveryChanges = database.queryPlayerRecoveryChanges(player);
        for (PlayerRecoveryQuestions recoveryChange : recoveryChanges) {
            dateSet = Math.max(dateSet, recoveryChange.dateSet);
        }
        player.setLastRecoveryChangeRequest(dateSet);
    }

    private void savePlayerData(final Player player) throws GameDatabaseException {
        database.querySavePlayerData(player);
    }

    private void savePlayerInventory(final Player player) throws GameDatabaseException {
        database.savePlayerInventory(player);
    }

    private void savePlayerEquipment(final Player player) throws GameDatabaseException {
        database.querySavePlayerEquipped(player);
    }

    private void savePlayerBank(final Player player) throws GameDatabaseException {
        database.savePlayerBank(player);
    }

    private void savePlayerBankPresets(final Player player) throws GameDatabaseException {
        database.querySavePlayerBankPresets(player);
    }

    private void savePlayerSocial(final Player player) throws GameDatabaseException {
        database.querySavePlayerFriends(player);
        database.querySavePlayerIgnored(player);
    }

    private void savePlayerQuests(final Player player) throws GameDatabaseException {
        database.querySavePlayerQuests(player);
    }

    private void savePlayerAchievements(final Player player) throws GameDatabaseException {
        database.querySavePlayerAchievements(player);
    }

    @Override
    public void savePlayerCache(final Player player) throws GameDatabaseException {
        player.getCache().store("last_spell_cast", player.getCastTimer());
        database.querySavePlayerCache(player);
    }

    private void savePlayerNpcKills(final Player player) throws GameDatabaseException {
        if (player.getKillCacheUpdated()) {
            database.querySavePlayerNpcKills(player);
            player.setKillCacheUpdated(false);
        }
    }

    private void savePlayerSkills(final Player player) throws GameDatabaseException {
        database.querySavePlayerSkills(player);
        database.querySavePlayerExperience(player);
    }

    public void savePlayerMaxSkill(final int playerId, final int skillId, final int level) throws GameDatabaseException {
        database.querySavePlayerMaxSkill(playerId, skillId, level);
    }

    public void savePlayerExpCapped(final int playerId, final int skillId, final long dateCapped) throws GameDatabaseException {
        database.querySavePlayerExpCapped(playerId, skillId, dateCapped);
    }

    private void savePlayerCastTime(final Player player) {
        player.getCache().store("last_spell_cast", player.getCastTimer());
    }
}
