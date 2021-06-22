package com.openrsc.server.net.rsc;

import com.openrsc.server.Server;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.content.clan.Clan;
import com.openrsc.server.content.clan.ClanManager;
import com.openrsc.server.content.clan.ClanPlayer;
import com.openrsc.server.content.party.Party;
import com.openrsc.server.content.party.PartyManager;
import com.openrsc.server.content.party.PartyPlayer;
import com.openrsc.server.event.custom.HolidayDropEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.BankPreset;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.generators.PayloadGenerator;
import com.openrsc.server.net.rsc.generators.impl.Payload177Generator;
import com.openrsc.server.net.rsc.generators.impl.Payload235Generator;
import com.openrsc.server.net.rsc.generators.impl.Payload38Generator;
import com.openrsc.server.net.rsc.generators.impl.PayloadCustomGenerator;
import com.openrsc.server.net.rsc.struct.AbstractStruct;
import com.openrsc.server.net.rsc.struct.outgoing.*;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.util.rsc.CaptchaGenerator;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Sends corresponding actions for use over the network layer
 * */
public class ActionSender {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Get respective generator
	 * */
	public static PayloadGenerator<OpcodeOut> getGenerator(Player player) {
		PayloadGenerator<OpcodeOut> generator;
		if (player.isRetroClient()) {
			generator = new Payload38Generator();
		} else if (player.isUsing233CompatibleClient()) {
			generator = new Payload235Generator();
		} else if (player.isUsing177CompatibleClient()) {
			generator = new Payload177Generator();
		} else {
			generator = new PayloadCustomGenerator();
		}
		return generator;
	}

	/**
	 * Completes the payload with the specified Opcode
	 * and then attempts to send the generated packet.
	 * Silently fails out if the appropriate generator could not generate packet
	 * */
	public static void tryFinalizeAndSendPacket(OpcodeOut opcode, AbstractStruct<OpcodeOut> payload, Player player) {
		payload.setOpcode(opcode);
		try {
			Packet p = getGenerator(player).generate(payload, player);
			if (p != null)
				player.write(p);
		} catch (GameNetworkException gne) {
			throw new GameNetworkException(gne);
		}
	}

	public static boolean isRetroClient(Player player) {
		//return player.getClientVersion() == 38;
		return player.isRetroClient();
	}

	/**
	 * Returns a safe to use value for privacy setting, depending on the client (original or custom)
	 **/
	public static int getPrivacySettingValue(Player player, int setting, boolean fromAuthentic) {
		if (fromAuthentic) {
			return player.getSettings().getPrivacySetting(setting, false)
				!= PlayerSettings.BlockingMode.None.id() ? 1 : 0;
		} else {
			return player.getSettings().getPrivacySetting(setting, true);
		}
	}

	/**
	 * Hides the bank windows
	 */
	public static void hideBank(Player player) {
		NoPayloadStruct struct = new NoPayloadStruct();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_BANK_CLOSE, struct, player);
	}

	/**
	 * Hides a question menu
	 */
	public static void hideMenu(Player player) {
		NoPayloadStruct struct = new NoPayloadStruct();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_OPTIONS_MENU_CLOSE, struct, player);
	}

	/**
	 * Hides the shop window
	 */
	public static void hideShop(Player player) {
		NoPayloadStruct struct = new NoPayloadStruct();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_SHOP_CLOSE, struct, player);
	}

	/**
	 * Sends a message box
	 */
	public static void sendBox(Player player, String message, boolean big) {
		MessageBoxStruct struct = new MessageBoxStruct();
		struct.message = message;
		OpcodeOut opcode = big ? OpcodeOut.SEND_BOX : OpcodeOut.SEND_BOX2;
		tryFinalizeAndSendPacket(opcode, struct, player);
	}

	/**
	 * Inform client to start displaying the appearance changing screen.
	 *
	 * @param player
	 */
	public static void sendAppearanceScreen(Player player) {
		player.setChangingAppearance(true);
		NoPayloadStruct struct = new NoPayloadStruct();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_APPEARANCE_SCREEN, struct, player);
	}

	public static void sendRecoveryScreen(Player player) {
		player.setChangingRecovery(true);
		NoPayloadStruct struct = new NoPayloadStruct();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_OPEN_RECOVERY, struct, player);
	}

	public static void sendDetailsScreen(Player player) {
		player.setChangingDetails(true);
		NoPayloadStruct struct = new NoPayloadStruct();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_OPEN_DETAILS, struct, player);
	}

	public static void sendPlayerOnTutorial(Player player) {
		PlayerOnTutorialStruct struct = new PlayerOnTutorialStruct();
		struct.onTutorial = player.getLocation().onTutorialIsland() ? 1 : 0;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_ON_TUTORIAL, struct, player);
	}

	public static void sendPlayerOnBlackHole(Player player) {
		PlayerOnBlackHoleStruct struct = new PlayerOnBlackHoleStruct();
		struct.onBlackHole = player.getLocation().onBlackHole() ? 1 : 0;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_ON_BLACK_HOLE, struct, player);
	}

	/**
	 * Inform client of log-out request denial.
	 */
	public static void sendCantLogout(Player player) {
		NoPayloadStruct struct = new NoPayloadStruct();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_CANT_LOGOUT, struct, player);
	}

	/**
	 * Inform client of combat style
	 *
	 * This is generally not necessary, because remembered style is sent on log-in with opcode SEND_GAME_SETTINGS
	 *  and the client takes care of remembering the combat style in all other cases.
	 *
	 *  If some type of network unreliability issue happens, it is possible the server could interpret bad data
	 *  as a combat style change packet, and we would like to avoid a client-server desync combat style,
	 *  which is very important for Pure accounts.
	 *
	 * @param player
	 */
	public static void sendCombatStyle(Player player) {
		CombatStyleStruct struct = new CombatStyleStruct();
		struct.combatStyle = player.getCombatStyle();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_COMBAT_STYLE, struct, player);
	}

	/**
	 * Inform client to display the 'Oh dear...you are dead' screen.
	 *
	 * @param player
	 */
	public static void sendDied(Player player) {
		hideBank(player);
		NoPayloadStruct struct = new NoPayloadStruct();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_DEATH, struct, player);
	}

	/**
	 * Inform client of everything on the duel screen
	 *
	 * @param player
	 */
	public static void sendDuelConfirmScreen(Player player) {
		Player with = player.getDuel().getDuelRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}
		DuelConfirmStruct struct = new DuelConfirmStruct();
		struct.targetPlayer = with.getUsername();
		int stakedSize, i;
		synchronized(with.getDuel().getDuelOffer().getItems()) {
			stakedSize = with.getDuel().getDuelOffer().getItems().size();
			struct.opponentDuelCount = stakedSize;
			struct.opponentCatalogIDs = new int[stakedSize];
			struct.opponentAmounts = new int[stakedSize];
			if (player.getConfig().WANT_BANK_NOTES) {
				struct.opponentNoted = new int[stakedSize];
			}
			i = 0;
			for (Item item : with.getDuel().getDuelOffer().getItems()) {
				struct.opponentCatalogIDs[i] = item.getCatalogId();
				if (item.getNoted() && !player.isUsingCustomClient()) {
					String itemName = item.getDef(player.getWorld()).getName();
					player.playerServerMessage(MessageType.QUEST,
						String.format("@ran@Please Confirm: @whi@Other player is staking @gre@%d @yel@%s", item.getAmount(), itemName));
				}
				if (struct.opponentNoted != null) {
					struct.opponentNoted[i] = item.getNoted() ? 1 : 0;
				}
				struct.opponentAmounts[i] = item.getAmount();
				i++;
			}
		}
		synchronized(player.getDuel().getDuelOffer().getItems()) {
			stakedSize = player.getDuel().getDuelOffer().getItems().size();
			struct.myCount = stakedSize;
			struct.myCatalogIDs = new int[stakedSize];
			struct.myAmounts = new int[stakedSize];
			if (player.getConfig().WANT_BANK_NOTES) {
				struct.myNoted = new int[stakedSize];
			}
			i = 0;
			for (Item item : player.getDuel().getDuelOffer().getItems()) {
				struct.myCatalogIDs[i] = item.getCatalogId();
				if (struct.myNoted != null) {
					struct.myNoted[i] = item.getNoted() ? 1 : 0;
				}
				struct.myAmounts[i] = item.getAmount();
				i++;
			}
		}
		struct.disallowRetreat = player.getDuel().getDuelSetting(0) ? 1 : 0;
		struct.disallowMagic = player.getDuel().getDuelSetting(1) ? 1 : 0;
		struct.disallowPrayer = player.getDuel().getDuelSetting(2) ? 1 : 0;
		struct.disallowWeapons = player.getDuel().getDuelSetting(3) ? 1 : 0;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_DUEL_CONFIRMWINDOW, struct, player);
	}

	/**
	 * Inform client of duel accept
	 *
	 * @param player
	 */

	public static void sendOwnDuelAcceptUpdate(Player player) {
		Player with = player.getDuel().getDuelRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}
		DuelAcceptStruct struct = new DuelAcceptStruct();
		struct.accepted = player.getDuel().isDuelAccepted() ? 1 : 0;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_DUEL_ACCEPTED, struct, player);
	}

	public static void sendOpponentDuelAcceptUpdate(Player player) {
		Player with = player.getDuel().getDuelRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}
		DuelAcceptStruct struct = new DuelAcceptStruct();
		struct.accepted = with.getDuel().isDuelAccepted() ? 1 : 0;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_DUEL_OTHER_ACCEPTED, struct, player);
	}

	/**
	 * Inform client of the offer changes on duel window.
	 *
	 * @param player
	 */
	public static void sendDuelOpponentItems(Player player) {
		Player with = player.getDuel().getDuelRecipient();
		if (with == null) {
			return;
		}
		List<Item> items = with.getDuel().getDuelOffer().getItems();
		synchronized(items) {
			DuelStakeStruct struct = new DuelStakeStruct();
			int stakedSize = items.size();
			struct.count = stakedSize;
			struct.catalogIDs = new int[stakedSize];
			struct.amounts = new int[stakedSize];
			if (player.getConfig().WANT_BANK_NOTES) {
				struct.noted = new int[stakedSize];
			}
			int i = 0;
			for (Item item : items) {
				struct.catalogIDs[i] = player.isUsingCustomClient() ? item.getCatalogId() : item.getCatalogIdAuthenticNoting();
				if (item.getNoted() && !player.isUsingCustomClient()) {
					String itemName = item.getDef(player.getWorld()).getName();
					player.playerServerMessage(MessageType.QUEST,
						String.format("@whi@Other player is staking @gre@%d @yel@%s", item.getAmount(), itemName));
				}
				if (struct.noted != null) {
					struct.noted[i] = item.getNoted() ? 1 : 0;
				}
				struct.amounts[i] = item.getAmount();
				i++;
			}
			tryFinalizeAndSendPacket(OpcodeOut.SEND_DUEL_OPPONENTS_ITEMS, struct, player);
		}
	}

	/**
	 * Inform client to update the duel settings on duel window.
	 *
	 * @param player
	 */
	public static void sendDuelSettingUpdate(Player player) {
		DuelSettingsStruct struct = new DuelSettingsStruct();
		struct.disallowRetreat = player.getDuel().getDuelSetting(0) ? 1 : 0;
		struct.disallowMagic = player.getDuel().getDuelSetting(1) ? 1 : 0;
		struct.disallowPrayer = player.getDuel().getDuelSetting(2) ? 1 : 0;
		struct.disallowWeapons = player.getDuel().getDuelSetting(3) ? 1 : 0;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_DUEL_SETTINGS, struct, player);
	}

	/**
	 * Inform client to close the duel window
	 *
	 * @param player
	 */
	public static void sendDuelWindowClose(Player player) {
		NoPayloadStruct struct = new NoPayloadStruct();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_DUEL_CLOSE, struct, player);
	}

	/**
	 * Inform client to open duel window
	 *
	 * @param player
	 */
	public static void sendDuelWindowOpen(Player player) {
		Player with = player.getDuel().getDuelRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}
		DuelShowWindowStruct struct = new DuelShowWindowStruct();
		struct.serverIndex = with.getIndex();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_DUEL_WINDOW, struct, player);
	}

	/**
	 * Inform client to start drawing sleep screen and the captcha.
	 *
	 * @param player
	 */
	public static void sendEnterSleep(Player player) {
		player.setSleeping(true);
		SleepScreenStruct struct = new SleepScreenStruct();
		struct.image = CaptchaGenerator.generateCaptcha(player);
		tryFinalizeAndSendPacket(OpcodeOut.SEND_SLEEPSCREEN, struct, player);
	}

	/**
	 * Updates the equipment status
	 */
	public static void sendEquipmentStats(Player player) {
		sendEquipmentStats(player, -1);
	}

	public static void sendEquipmentStats(Player player, int slot) {
		EquipmentStatsStruct struct = new EquipmentStatsStruct();
		struct.armourPoints = player.getArmourPoints();
		struct.weaponAimPoints = player.getWeaponAimPoints();
		struct.weaponPowerPoints = player.getWeaponPowerPoints();
		struct.magicPoints = player.getMagicPoints();
		struct.prayerPoints = player.getPrayerPoints();
		struct.hidingPoints = player.getHidingPoints();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_EQUIPMENT_STATS, struct, player);

		if (player.isUsingCustomClient()) {
            if (player.getConfig().WANT_EQUIPMENT_TAB) {
                if (slot == -1)
                    sendEquipment(player);
                else
                    updateEquipmentSlot(player, slot);
            }
        }
	}


	/**
	 * Sends fatigue
	 *
	 * @param player
	 */
	public static void sendFatigue(Player player) {
		FatigueStruct struct = new FatigueStruct();
		struct.serverFatigue = player.getFatigue();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_FATIGUE, struct, player);
	}

	public static void showPointsToGp(Player player) {
		NoPayloadStruct struct = new NoPayloadStruct();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_OPENPK_POINTS_TO_GP_RATIO, struct, player);
	}

	public static void sendNpcKills(Player player) {
	    MobKillsStruct struct = new MobKillsStruct();
	    struct.count = player.getNpcKills();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_NPC_KILLS, struct, player);
	}

	public static void sendPoints(Player player) {
		PointsStruct struct = new PointsStruct();
		struct.amount = player.getOpenPkPoints();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_OPENPK_POINTS, struct, player);
	}

	public static void sendExpShared(Player player) {
		ExpSharedStruct struct = new ExpSharedStruct();
		struct.value = player.getExpShared();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_EXPSHARED, struct, player);
	}

	/**
	 * Sends the sleeping state fatigue
	 *
	 * @param player
	 * @param fatigue
	 */
	public static void sendSleepFatigue(Player player, int fatigue) {
		FatigueStruct struct = new FatigueStruct();
		struct.serverFatigue = fatigue;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_SLEEP_FATIGUE, struct, player);
	}

	/**
	 * Sends friend list
	 *
	 * @param player
	 */
	public static void sendFriendList(Player player) {
		if (isRetroClient(player) || player.isUsing177CompatibleClient()) {
			FriendListStruct struct = new FriendListStruct();
			int listSize = player.getSocial().getFriendList().size();
			int i = 0;
			struct.listSize = listSize;
			struct.name = new String[listSize];
			struct.formerName = new String[listSize];
			struct.onlineStatus = new int[listSize];
			struct.worldNumber = new int[listSize];
			for (final Map.Entry<Long, Integer> entry : player.getSocial().getFriendList().entrySet()) {
				long usernameHash = entry.getKey();
				String username = DataConversions.hashToUsername(usernameHash);

				int onlineStatus = 0; // offline
				struct.worldNumber[i] = 0;
				if (usernameHash == Long.MIN_VALUE && player.getConfig().WANT_GLOBAL_FRIEND) {
					if (player.getBlockGlobalFriend()) continue;
					onlineStatus = 6; // online and same world
					username = "Global$";
					struct.worldNumber[i] = 99;
				} else if (player.getWorld().getPlayer(usernameHash) != null &&
					player.getWorld().getPlayer(usernameHash).isLoggedIn()) {
					onlineStatus = getPlayerOnlineStatus(player, usernameHash);
					try {
						// TODO: we won't be able to reach across servers like this if there's more than one server
						if (onlineStatus == 6) {
							struct.worldNumber[i] = 99; // same world
						} else {
							struct.worldNumber[i] = player.getWorld().getPlayer(usernameHash).getWorld().getServer().getConfig().WORLD_NUMBER;
						}
					} catch (Exception e) {
						struct.worldNumber[i] = 99; // assume same world
					}
				}

				struct.name[i] = username;
				struct.formerName[i] = "";
				struct.onlineStatus[i] = onlineStatus;
				i++;
			}
			tryFinalizeAndSendPacket(OpcodeOut.SEND_FRIEND_LIST, struct, player);
		} else {
			for (int currentFriend = 0; currentFriend < player.getSocial().getFriendListEntry().size() + 1; ++currentFriend) {
				int iteratorIndex = 0;
				for (Entry<Long, Integer> entry : player.getSocial().getFriendListEntry()) {
					if (iteratorIndex == currentFriend) {
						sendFriendUpdate(player, entry.getKey());
						break;
					}
					iteratorIndex++;
				}
			}
		}
	}

	public static int getPlayerOnlineStatus(Player player, long friendHash) {
		int onlineStatus = 0;

		Player otherPlayer = player.getWorld().getPlayer(friendHash);
		boolean blockAll = otherPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, otherPlayer.isUsingCustomClient())
			== PlayerSettings.BlockingMode.All.id();
		boolean blockNone = otherPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, otherPlayer.isUsingCustomClient())
			== PlayerSettings.BlockingMode.None.id();
		if (blockNone || (otherPlayer.getSocial().isFriendsWith(player.getUsernameHash()) && !blockAll) || player.isMod()) {
			onlineStatus |= 4 | 2; // 4 for is online and 2 for on same world. 1 would be if the User's name changed from original
		}

		return onlineStatus;
	}

	/**
	 * Updates a friends login status
	 * @param player - Our player
	 * @param usernameHash - the friend player
	 */
	public static void sendFriendUpdate(Player player, long usernameHash) {
		FriendUpdateStruct struct = new FriendUpdateStruct();
		int onlineStatus = 0;
		struct.worldNumber = 0;
		String username = DataConversions.hashToUsername(usernameHash);

		if (usernameHash == Long.MIN_VALUE && player.getConfig().WANT_GLOBAL_FRIEND) {
			if (player.getBlockGlobalFriend()) return;
			onlineStatus = 6;
			username = "Global$";
			struct.worldNumber = 99;
		} else if (player.getWorld().getPlayer(usernameHash) != null &&
			player.getWorld().getPlayer(usernameHash).isLoggedIn()) {
			onlineStatus = getPlayerOnlineStatus(player, usernameHash);
			try {
				// TODO: we won't be able to reach across servers like this if there's more than one server
				if (onlineStatus == 6) {
					struct.worldNumber = 99; // same world
				} else {
					struct.worldNumber = player.getWorld().getPlayer(usernameHash).getWorld().getServer().getConfig().WORLD_NUMBER;
				}
			} catch (Exception e) {
				struct.worldNumber = 99; // assume same world
			}
		}

		struct.name = username;
		struct.formerName = ""; // TODO: Allow name changes to fill this variable.
		struct.onlineStatus = onlineStatus;
		struct.worldName = (onlineStatus & 4) != 0 ? "OpenRSC" : "";
		tryFinalizeAndSendPacket(OpcodeOut.SEND_FRIEND_UPDATE, struct, player);
	}

	/**
	 * Updates game settings, ie sound effects etc
	 */
	public static void sendGameSettings(Player player) {
		GameSettingsStruct struct = new GameSettingsStruct();
		struct.cameraModeAuto = player.getSettings().getGameSetting(PlayerSettings.GAME_SETTING_AUTO_CAMERA) ? 1 : 0;
		struct.mouseButtonOne = player.getSettings().getGameSetting(PlayerSettings.GAME_SETTING_MOUSE_BUTTONS) ? 1 : 0;
		struct.soundDisabled = player.getSettings().getGameSetting(PlayerSettings.GAME_SETTING_SOUND_EFFECTS) ? 1 : 0;
		struct.playerKiller = player.getPkMode();
		struct.pkChangesLeft = player.getPkChanges();
		List<Integer> customOptions = new ArrayList<>();
		if (player.isUsingCustomClient()) { // custom options
			// keep order same that custom client expects!
			customOptions.add(player.getCombatStyle());
			customOptions.add(player.getGlobalBlock());
			customOptions.add(player.getClanInviteSetting() ? 0 : 1);
			customOptions.add(player.getVolumeFunction());
			customOptions.add(player.getSwipeToRotate() ? 1 : 0);
			customOptions.add(player.getSwipeToScroll() ? 1 : 0);
			customOptions.add(player.getLongPressDelay());
			customOptions.add(player.getFontSize());
			customOptions.add(player.getHoldAndChoose() ? 1 : 0);
			customOptions.add(player.getSwipeToZoom() ? 1 : 0);
			customOptions.add(player.getLastZoom());
			customOptions.add(player.getBatchProgressBar() ? 1 : 0);
			customOptions.add(player.getExperienceDrops() ? 1 : 0);
			customOptions.add(player.getHideRoofs() ? 1 : 0);
			customOptions.add(player.getHideFog() ? 1 : 0);
			customOptions.add(player.getGroundItemsToggle());
			customOptions.add(player.getAutoMessageSwitch() ? 1 : 0);
			customOptions.add(player.getHideSideMenu() ? 1 : 0);
			customOptions.add(player.getHideKillFeed() ? 1 : 0);
			customOptions.add(player.getFightModeSelectorToggle());
			customOptions.add(player.getExperienceCounterToggle());
			customOptions.add(player.getHideInventoryCount() ? 1 : 0);
			customOptions.add(player.getHideNameTag() ? 1 : 0);
			customOptions.add(player.getPartyInviteSetting() ? 1 : 0);
			customOptions.add(player.getAndroidInvToggle() ? 1 : 0);
			customOptions.add(player.getShowNPCKC() ? 1 : 0);
			customOptions.add(player.getCustomUI() ? 1 : 0);
			customOptions.add(player.getHideLoginBox() ? 1 : 0);
			customOptions.add(player.getBlockGlobalFriend() ? 1 : 0);
		}
		struct.customOptions = customOptions;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_GAME_SETTINGS, struct, player);
	}

	public static void sendInitialServerConfigs(Server server, Channel channel) {
		LOGGER.info("Sending initial configs to: " + channel.remoteAddress());
		if (server.getConfig().DEBUG) {
			LOGGER.info("Debug server configs being sent:");
			LOGGER.info(server.getConfig().SERVER_NAME + " 1");
			LOGGER.info(server.getConfig().SERVER_NAME_WELCOME + " 2");
			LOGGER.info(server.getConfig().PLAYER_LEVEL_LIMIT + " 3");
			LOGGER.info(server.getConfig().SPAWN_AUCTION_NPCS + " 4");
			LOGGER.info(server.getConfig().SPAWN_IRON_MAN_NPCS + " 5");
			LOGGER.info(server.getConfig().SHOW_FLOATING_NAMETAGS + " 6");
			LOGGER.info(server.getConfig().WANT_CLANS + " 7");
			LOGGER.info(server.getConfig().WANT_KILL_FEED + " 8");
			LOGGER.info(server.getConfig().FOG_TOGGLE + " 9");
			LOGGER.info(server.getConfig().GROUND_ITEM_TOGGLE + " 10");
			LOGGER.info(server.getConfig().AUTO_MESSAGE_SWITCH_TOGGLE + " 11");
			LOGGER.info(server.getConfig().BATCH_PROGRESSION + " 12");
			LOGGER.info(server.getConfig().SIDE_MENU_TOGGLE + " 13");
			LOGGER.info(server.getConfig().INVENTORY_COUNT_TOGGLE + " 14");
			LOGGER.info(server.getConfig().ZOOM_VIEW_TOGGLE + " 15");
			LOGGER.info(server.getConfig().MENU_COMBAT_STYLE_TOGGLE + " 16");
			LOGGER.info(server.getConfig().FIGHTMODE_SELECTOR_TOGGLE + " 17");
			LOGGER.info(server.getConfig().EXPERIENCE_COUNTER_TOGGLE + " 18");
			LOGGER.info(server.getConfig().EXPERIENCE_DROPS_TOGGLE + " 19");
			LOGGER.info(server.getConfig().ITEMS_ON_DEATH_MENU + " 20");
			LOGGER.info(server.getConfig().SHOW_ROOF_TOGGLE + " 21");
			LOGGER.info(server.getConfig().WANT_HIDE_IP + " 22");
			LOGGER.info(server.getConfig().WANT_REMEMBER + " 23");
			LOGGER.info(server.getConfig().WANT_GLOBAL_CHAT + " 24");
			LOGGER.info(server.getConfig().WANT_SKILL_MENUS + " 25");
			LOGGER.info(server.getConfig().WANT_QUEST_MENUS + " 26");
			LOGGER.info(server.getConfig().WANT_EXPERIENCE_ELIXIRS + " 27");
			LOGGER.info(server.getConfig().WANT_KEYBOARD_SHORTCUTS + " 28");
			LOGGER.info(server.getConfig().WANT_CUSTOM_BANKS + " 29");
			LOGGER.info(server.getConfig().WANT_BANK_PINS + " 30");
			LOGGER.info(server.getConfig().WANT_BANK_NOTES + " 31");
			LOGGER.info(server.getConfig().WANT_CERT_DEPOSIT + " 32");
			LOGGER.info(server.getConfig().CUSTOM_FIREMAKING + " 33");
			LOGGER.info(server.getConfig().WANT_DROP_X + " 34");
			LOGGER.info(server.getConfig().WANT_EXP_INFO + " 35");
			LOGGER.info(server.getConfig().WANT_WOODCUTTING_GUILD + " 36");
			LOGGER.info(server.getConfig().WANT_DECANTING + " 37");
			LOGGER.info(server.getConfig().WANT_CERTER_BANK_EXCHANGE + " 38");
			LOGGER.info(server.getConfig().WANT_CUSTOM_RANK_DISPLAY + " 39");
			LOGGER.info(server.getConfig().RIGHT_CLICK_BANK + " 40");
			LOGGER.info(server.getConfig().FIX_OVERHEAD_CHAT + " 41");
			LOGGER.info(server.getConfig().WELCOME_TEXT + " 42");
			LOGGER.info(server.getConfig().MEMBER_WORLD + " 43");
			LOGGER.info(server.getConfig().DISPLAY_LOGO_SPRITE + " 44");
			LOGGER.info(server.getConfig().LOGO_SPRITE_ID + " 45");
			LOGGER.info(server.getConfig().FPS + " 46");
			LOGGER.info(server.getConfig().WANT_EMAIL + " 47");
			LOGGER.info(server.getConfig().WANT_REGISTRATION_LIMIT + " 48");
			LOGGER.info(server.getConfig().ALLOW_RESIZE + " 49");
			LOGGER.info(server.getConfig().LENIENT_CONTACT_DETAILS + " 50");
			LOGGER.info(server.getConfig().WANT_FATIGUE + " 51");
			LOGGER.info(server.getConfig().WANT_CUSTOM_SPRITES + " 52");
			LOGGER.info(server.getConfig().PLAYER_COMMANDS + " 53");
			LOGGER.info(server.getConfig().WANT_PETS + " 54");
			LOGGER.info(server.getConfig().MAX_WALKING_SPEED + " 55");
			LOGGER.info(server.getConfig().SHOW_UNIDENTIFIED_HERB_NAMES + " 56");
			LOGGER.info(server.getConfig().WANT_QUEST_STARTED_INDICATOR + " 57");
			LOGGER.info(server.getConfig().FISHING_SPOTS_DEPLETABLE + " 58");
			LOGGER.info(server.getConfig().IMPROVED_ITEM_OBJECT_NAMES + " 59");
			LOGGER.info(server.getConfig().WANT_RUNECRAFT + " 60");
			LOGGER.info(server.getConfig().WANT_CUSTOM_LANDSCAPE + " 61");
			LOGGER.info(server.getConfig().WANT_EQUIPMENT_TAB + " 62");
			LOGGER.info(server.getConfig().WANT_BANK_PRESETS + " 63");
			LOGGER.info(server.getConfig().WANT_PARTIES + " 64");
			LOGGER.info(server.getConfig().MINING_ROCKS_EXTENDED + " 65");
			LOGGER.info(server.getConfig().WANT_NEW_RARE_DROP_TABLES + "");
			LOGGER.info(server.getConfig().WANT_LEFTCLICK_WEBS + " 67");
			LOGGER.info(server.getConfig().WANT_CUSTOM_QUESTS + " 68");
			LOGGER.info(server.getConfig().WANT_CUSTOM_UI + " 69");
			LOGGER.info(server.getConfig().WANT_GLOBAL_FRIEND + " 70");
			LOGGER.info(server.getConfig().CHARACTER_CREATION_MODE + " 71");
			LOGGER.info(server.getConfig().SKILLING_EXP_RATE + " 72");
			LOGGER.info(server.getConfig().WANT_HARVESTING + " 73");
			LOGGER.info(server.getConfig().HIDE_LOGIN_BOX_TOGGLE + " 74");
			LOGGER.info(server.getConfig().WANT_GLOBAL_FRIEND + " 75");
			LOGGER.info(server.getConfig().RIGHT_CLICK_TRADE + " 76");
			LOGGER.info(server.getConfig().FEATURES_SLEEP + " 77");
			LOGGER.info(server.getConfig().WANT_EXTENDED_CATS_BEHAVIOR + " 78");
			LOGGER.info(server.getConfig().WANT_CERT_AS_NOTES + " 79");
		}
		Packet p = prepareServerConfigs(server);
		// ConnectionAttachment attachment = new ConnectionAttachment();
		// channel.attr(RSCConnectionHandler.attachment).set(attachment);
		if (p != null)
			channel.writeAndFlush(p);
		channel.close();
	}

	static void sendServerConfigs(Player player) {
		Packet p = prepareServerConfigs(player.getWorld().getServer());
		if (p != null)
			player.write(p);
	}

	private static Packet prepareServerConfigs(Server server) {
		PayloadCustomGenerator generator = new PayloadCustomGenerator();
		ServerConfigsStruct struct = new ServerConfigsStruct();

		int stepsPerFrame;
		if (server.getConfig().WANT_CUSTOM_WALK_SPEED)
			stepsPerFrame = (int)Math.round(4.0f * 640.0f / (double)server.getConfig().WALKING_TICK);
		else
			stepsPerFrame = (int)Math.round(4.0f * 640.0f / (double)server.getConfig().GAME_TICK);

		List<Object> configs = new ArrayList<>();
		configs.add(server.getConfig().SERVER_NAME); // 1
		configs.add(server.getConfig().SERVER_NAME_WELCOME); // 2
		configs.add((byte) server.getConfig().PLAYER_LEVEL_LIMIT); // 3
		configs.add((byte) (server.getConfig().SPAWN_AUCTION_NPCS ? 1 : 0)); // 4
		configs.add((byte) (server.getConfig().SPAWN_IRON_MAN_NPCS ? 1 : 0)); // 5
		configs.add((byte) (server.getConfig().SHOW_FLOATING_NAMETAGS ? 1 : 0)); // 6
		configs.add((byte) (server.getConfig().WANT_CLANS ? 1 : 0)); // 7
		configs.add((byte) (server.getConfig().WANT_KILL_FEED ? 1 : 0)); // 8
		configs.add((byte) (server.getConfig().FOG_TOGGLE ? 1 : 0)); // 9
		configs.add((byte) (server.getConfig().GROUND_ITEM_TOGGLE ? 1 : 0)); // 10
		configs.add((byte) (server.getConfig().AUTO_MESSAGE_SWITCH_TOGGLE ? 1 : 0)); // 11
		configs.add((byte) (server.getConfig().BATCH_PROGRESSION ? 1 : 0)); // 12
		configs.add((byte) (server.getConfig().SIDE_MENU_TOGGLE ? 1 : 0)); // 13
		configs.add((byte) (server.getConfig().INVENTORY_COUNT_TOGGLE ? 1 : 0)); // 14
		configs.add((byte) (server.getConfig().ZOOM_VIEW_TOGGLE ? 1 : 0)); // 15
		configs.add((byte) (server.getConfig().MENU_COMBAT_STYLE_TOGGLE ? 1 : 0)); // 16
		configs.add((byte) (server.getConfig().FIGHTMODE_SELECTOR_TOGGLE ? 1 : 0)); // 17
		configs.add((byte) (server.getConfig().EXPERIENCE_COUNTER_TOGGLE ? 1 : 0)); // 18
		configs.add((byte) (server.getConfig().EXPERIENCE_DROPS_TOGGLE ? 1 : 0)); // 19
		configs.add((byte) (server.getConfig().ITEMS_ON_DEATH_MENU ? 1 : 0)); // 20
		configs.add((byte) (server.getConfig().SHOW_ROOF_TOGGLE ? 1 : 0)); // 21
		configs.add((byte) (server.getConfig().WANT_HIDE_IP ? 1 : 0)); // 22
		configs.add((byte) (server.getConfig().WANT_REMEMBER ? 1 : 0)); // 23
		configs.add((byte) (server.getConfig().WANT_GLOBAL_CHAT ? 1 : 0)); // 24
		configs.add((byte) (server.getConfig().WANT_SKILL_MENUS ? 1 : 0)); // 25
		configs.add((byte) (server.getConfig().WANT_QUEST_MENUS ? 1 : 0)); // 26
		configs.add((byte) (server.getConfig().WANT_EXPERIENCE_ELIXIRS ? 1 : 0)); // 27
		configs.add((byte) server.getConfig().WANT_KEYBOARD_SHORTCUTS); // 28
		configs.add((byte) (server.getConfig().WANT_CUSTOM_BANKS ? 1 : 0)); // 29
		configs.add((byte) (server.getConfig().WANT_BANK_PINS ? 1 : 0)); // 30
		configs.add((byte) (server.getConfig().WANT_BANK_NOTES ? 1 : 0)); // 31
		configs.add((byte) (server.getConfig().WANT_CERT_DEPOSIT ? 1 : 0)); // 32
		configs.add((byte) (server.getConfig().CUSTOM_FIREMAKING ? 1 : 0)); // 33
		configs.add((byte) (server.getConfig().WANT_DROP_X ? 1 : 0)); // 34
		configs.add((byte) (server.getConfig().WANT_EXP_INFO ? 1 : 0)); // 35
		configs.add((byte) (server.getConfig().WANT_WOODCUTTING_GUILD ? 1 : 0)); // 36
		configs.add((byte) (server.getConfig().WANT_DECANTING ? 1 : 0)); // 37
		configs.add((byte) (server.getConfig().WANT_CERTER_BANK_EXCHANGE ? 1 : 0)); // 38
		configs.add((byte) (server.getConfig().WANT_CUSTOM_RANK_DISPLAY ? 1 : 0)); // 39
		configs.add((byte) (server.getConfig().RIGHT_CLICK_BANK ? 1 : 0)); // 40
		configs.add((byte) (server.getConfig().FIX_OVERHEAD_CHAT ? 1 : 0)); // 41
		configs.add(server.getConfig().WELCOME_TEXT); // 42
		configs.add((byte) (server.getConfig().MEMBER_WORLD ? 1 : 0)); // 43
		configs.add((byte) (server.getConfig().DISPLAY_LOGO_SPRITE ? 1 : 0)); // 44
		configs.add(server.getConfig().LOGO_SPRITE_ID); // 45
		configs.add((byte) server.getConfig().FPS); // 46
		configs.add((byte) (server.getConfig().WANT_EMAIL ? 1 : 0)); // 47
		configs.add((byte) (server.getConfig().WANT_REGISTRATION_LIMIT ? 1 : 0)); // 48
		configs.add((byte) (server.getConfig().ALLOW_RESIZE ? 1 : 0)); // 49
		configs.add((byte) (server.getConfig().LENIENT_CONTACT_DETAILS ? 1 : 0)); // 50
		configs.add((byte) (server.getConfig().WANT_FATIGUE ? 1 : 0)); // 51
		configs.add((byte) (server.getConfig().WANT_CUSTOM_SPRITES ? 1 : 0)); // 52
		configs.add((byte) (server.getConfig().PLAYER_COMMANDS ? 1 : 0)); // 53
		configs.add((byte) (server.getConfig().WANT_PETS ? 1 : 0)); // 54
		configs.add((byte) server.getConfig().MAX_WALKING_SPEED); // 55
		configs.add((byte) (server.getConfig().SHOW_UNIDENTIFIED_HERB_NAMES ? 1 : 0)); // 56
		configs.add((byte) (server.getConfig().WANT_QUEST_STARTED_INDICATOR ? 1 : 0)); // 57
		configs.add((byte) (server.getConfig().FISHING_SPOTS_DEPLETABLE ? 1 : 0)); // 58
		configs.add((byte) (server.getConfig().IMPROVED_ITEM_OBJECT_NAMES ? 1 : 0)); // 59
		configs.add((byte) (server.getConfig().WANT_RUNECRAFT ? 1 : 0)); //60
		configs.add((byte) (server.getConfig().WANT_CUSTOM_LANDSCAPE ? 1 : 0)); //61
		configs.add((byte) (server.getConfig().WANT_EQUIPMENT_TAB ? 1 : 0)); //62
		configs.add((byte) (server.getConfig().WANT_BANK_PRESETS ? 1 : 0)); //63
		configs.add((byte) (server.getConfig().WANT_PARTIES ? 1 : 0)); //64
		configs.add((byte) (server.getConfig().MINING_ROCKS_EXTENDED ? 1 : 0)); //65
		configs.add((byte) stepsPerFrame); //66
		configs.add((byte) (server.getConfig().WANT_LEFTCLICK_WEBS ? 1 : 0)); //67
		configs.add((byte) ((server.getConfig().NPC_KILL_LOGGING && server.getConfig().NPC_KILL_MESSAGES) ? 1 : 0)); //68
		configs.add((byte) (server.getConfig().WANT_CUSTOM_UI ? 1 : 0)); //69
		configs.add((byte) (server.getConfig().WANT_GLOBAL_FRIEND ? 1 : 0)); //70
		configs.add((byte) server.getConfig().CHARACTER_CREATION_MODE); //71
		configs.add((byte) server.getConfig().SKILLING_EXP_RATE); //72
		configs.add((byte) (server.getConfig().WANT_HARVESTING ? 1 : 0)); // 73
		configs.add((byte) (server.getConfig().HIDE_LOGIN_BOX_TOGGLE ? 1 : 0)); // 74
		configs.add((byte) (server.getConfig().WANT_GLOBAL_FRIEND ? 1 : 0)); // 75
		configs.add((byte) (server.getConfig().RIGHT_CLICK_TRADE ? 1 : 0)); // 76
		configs.add((byte) (server.getConfig().FEATURES_SLEEP ? 1 : 0)); // 77
		configs.add((byte) (server.getConfig().WANT_EXTENDED_CATS_BEHAVIOR ? 1 : 0)); // 78
		configs.add((byte) (server.getConfig().WANT_CERT_AS_NOTES ? 1 : 0)); // 79
		configs.add((byte) (server.getConfig().WANT_OPENPK_POINTS ? 1 : 0)); // 80
		configs.add((byte) (server.getConfig().OPENPK_POINTS_TO_GP_RATIO)); // 81

		struct.configs = configs;
		struct.setOpcode(OpcodeOut.SEND_SERVER_CONFIGS);
		return generator.generate(struct, null);
	}

	/**
	 * Sends the whole ignore list
	 */
	public static void sendIgnoreList(Player player) {
		IgnoreListStruct struct = new IgnoreListStruct();
		int listSize = player.getSocial().getIgnoreList().size();
		int i = 0;
		struct.listSize = listSize;
		struct.name = new String[listSize];
		struct.formerName = new String[listSize];
		for (long usernameHash : player.getSocial().getIgnoreList()) {
			String username = DataConversions.hashToUsername(usernameHash);
			struct.name[i] = username;
			struct.formerName[i] = "";
			i++;
		}
		tryFinalizeAndSendPacket(OpcodeOut.SEND_IGNORE_LIST, struct, player);
	}

	/**
	 * Incorrect sleep word!
	 */
	public static void sendIncorrectSleepword(Player player) {
		NoPayloadStruct struct = new NoPayloadStruct();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_SLEEPWORD_INCORRECT, struct, player);
	}

	/**
	 * @param player sends the player inventory
	 */
	public static void sendInventory(Player player) {
		if (player == null)
			return; /* In this case, it is a trade offer */
		InventoryStruct struct = new InventoryStruct();
		int inventorySize, i;
		synchronized(player.getCarriedItems().getInventory().getItems()) {
			inventorySize = player.getCarriedItems().getInventory().getItems().size();
			struct.inventorySize = inventorySize;
			struct.wielded = new int[inventorySize];
			struct.catalogIDs = new int[inventorySize];
			struct.amount = new int[inventorySize];
			struct.noted = new int[inventorySize];
			i = 0;
			for (Item item : player.getCarriedItems().getInventory().getItems()) {
				struct.wielded[i] = item.isWielded() ? 1 : 0;
				struct.catalogIDs[i] = player.isUsingCustomClient() ? item.getCatalogId() : item.getCatalogIdAuthenticNoting();
				struct.noted[i] = item.getNoted() ? 1 : 0;
				if (item.getDef(player.getWorld()).isStackable() || item.getNoted()) {
					// amount sent only for stackable
					struct.amount[i] = displayableStack(player, item.getAmount());
				}
				i++;
			}
		}
		tryFinalizeAndSendPacket(OpcodeOut.SEND_INVENTORY, struct, player);
	}

	/**
	 * Sends the client all bank preset data
	 * @param player
	 */
	public static void sendBankPresets(final Player player) {
		for (int i = 0; i < BankPreset.PRESET_COUNT; i++) {
			sendBankPreset(player, i);
		}
	}

	/**
	 * Sends the client bank preset data for one slot
	 * @param player: player to send the information to
	 * @param slot: slot to send the data from
	 */
	public static void sendBankPreset(final Player player, final int slot) {
		//Various checks on the parameters
		if (player == null || slot >= BankPreset.PRESET_COUNT || slot < 0)
			return;

		BankPresetStruct struct = new BankPresetStruct();
		struct.slotIndex = slot;

		List<Object> inventoryItems = new ArrayList<>();
		for (Item item : player.getBank().getBankPreset(slot).getInventory()) {
			if (item == null || item.getDef(player.getWorld()) == null || item.getCatalogId() == ItemId.NOTHING.id()) {
				inventoryItems.add(new Object());
				continue;
			}
			// when itemAmount is 0, the field is not sent over at protocol level
			int itemAmount = item.getDef(player.getWorld()).isStackable() || item.getNoted() ?
				item.getAmount() : 0;
			Item itemAdd = new Item(item.getCatalogId(), itemAmount, item.getNoted());
			inventoryItems.add(itemAdd);
		}
		struct.inventoryItems = inventoryItems;

		List<Object> equipmentItems = new ArrayList<>();
		for (Item item : player.getBank().getBankPreset(slot).getEquipment()) {
			if (item == null || item.getDef(player.getWorld()) == null || item.getCatalogId() == ItemId.NOTHING.id()) {
				equipmentItems.add(new Object());
				continue;
			}
			// when itemAmount is 0, the field is not sent over at protocol level
			int itemAmount = item.getDef(player.getWorld()).isStackable() || item.getNoted() ?
				item.getAmount() : 0;
			Item itemAdd = new Item(item.getCatalogId(), itemAmount, item.getNoted());
			equipmentItems.add(itemAdd);
		}
		struct.equipmentItems = equipmentItems;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_BANK_PRESET, struct, player);
	}

	// Sends the player's equipment
	public static void sendEquipment(Player player) {
		if (player == null)
			return;

		if (!player.getConfig().WANT_EQUIPMENT_TAB)
			return;

		EquipmentStruct struct = new EquipmentStruct();
		struct.equipmentCount = player.getCarriedItems().getEquipment().equipCount();
		int realSize = 0;
		for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
			if (player.getCarriedItems().getEquipment().get(i) != null) {
				realSize++;
			}
		}
		struct.realCount = realSize;
		struct.catalogIDs = new int[realSize];
		struct.wieldPositions = new int[realSize];
		struct.amount = new int[realSize];

		int j = 0;
		Item item;
		for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
			item = player.getCarriedItems().getEquipment().get(i);
			if (item != null) {
				struct.wieldPositions[j] = item.getDef(player.getWorld()).getWieldPosition();
				struct.catalogIDs[j] =  item.getCatalogId();
				struct.amount[j] = item.getDef(player.getWorld()).isStackable() ? item.getAmount() : 0;
				j++;
			}
		}

		tryFinalizeAndSendPacket(OpcodeOut.SEND_EQUIPMENT, struct, player);
	}

	public static void updateEquipmentSlot(Player player, int slot) {
		if (player == null)
			return;

		if (!player.getConfig().WANT_EQUIPMENT_TAB)
			return;

		EquipmentUpdateStruct struct = new EquipmentUpdateStruct();
		struct.slotIndex = slot;
		Item item = player.getCarriedItems().getEquipment().get(slot);
		if (item != null) {
			struct.catalogID = item.getCatalogId();
			struct.amount = item.getDef(player.getWorld()).isStackable() ? item.getAmount() : 0;
		} else {
			struct.catalogID = 0xFFFF;
		}
		tryFinalizeAndSendPacket(OpcodeOut.SEND_EQUIPMENT_UPDATE, struct, player);
	}


	/**
	 * Displays the login box and last IP and login date
	 */
	private static void sendLoginBox(Player player) {
		WelcomeInfoStruct struct = new WelcomeInfoStruct();
		struct.lastIp = player.getLastIP();
		struct.daysSinceLogin = player.getDaysSinceLastLogin();
		long currently = Calendar.getInstance().getTimeInMillis() / (1000 * 86400);
		boolean recoveryNotSet = player.getDaysSinceLastRecoveryChangeRequest() - currently == 0;
		struct.daysSinceRecoveryChange = recoveryNotSet ? -1 : player.getDaysSinceLastRecoveryChangeRequest();
		struct.unreadMessages = 0; // TODO: if player.getUnreadMessages is implemented, place that here
		tryFinalizeAndSendPacket(OpcodeOut.SEND_WELCOME_INFO, struct, player);
	}

	/**
	 * Confirm logout allowed
	 */
	public static void sendLogout(final Player player) {
		NoPayloadStruct struct = new NoPayloadStruct();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_LOGOUT, struct, player);
	}

	public static void sendLogoutRequestConfirm(final Player player) {
		Packet p;
		AbstractStruct<OpcodeOut> struct;
		if (!player.isRetroClient()) {
			NoPayloadStruct npStruct = new NoPayloadStruct();
			npStruct.setOpcode(OpcodeOut.SEND_LOGOUT_REQUEST_CONFIRM);
			struct = npStruct;
		} else {
			MessageStruct mStruct = new MessageStruct();
			mStruct.message = "@sys@k"; // special code to close connection (need to verify)
			mStruct.setOpcode(OpcodeOut.SEND_SERVER_MESSAGE);
			struct = mStruct;
		}
		p = getGenerator(player).generate(struct, player);
		player.getChannel().writeAndFlush(p).addListener((ChannelFutureListener) arg0 -> arg0.channel().close());
	}

	/**
	 * Sends quest names and stages
	 */
	private static void sendQuestInfo(Player player) {
		QuestInfoStruct struct = new QuestInfoStruct();
		List<QuestInterface> quests = player.getWorld().getQuests();
		struct.isUpdate = 0;
		int numberQuests = quests.size();
		struct.numberOfQuests = numberQuests;
		struct.questId = new int[numberQuests];
		struct.questStage = new int[numberQuests];
		struct.questName = new String[numberQuests];
		struct.questCompleted = new int[numberQuests];
		int i = 0;
		for (QuestInterface q : quests) {
			struct.questId[i] = q.getQuestId();
			struct.questStage[i] = player.getQuestStage(q);
			struct.questName[i] = q.getQuestName();
			struct.questCompleted[q.getQuestId()] = player.getQuestStage(q) < 0 ? 1 : 0; // array indexed by quest id (original clients)
			i++;
		}

		tryFinalizeAndSendPacket(OpcodeOut.SEND_QUESTS, struct, player);
	}

	/**
	 * Sends quest stage
	 */
	public static void sendQuestInfo(Player player, int questID, int stage) {
		if (!player.isUsingCustomClient()) {
			// authentic client does not care unless quest is complete.
			if (stage < 0) {
				sendQuestInfo(player);
			}
		} else {
			QuestInfoStruct struct = new QuestInfoStruct();
			struct.isUpdate = 1;
			struct.numberOfQuests = 1; // only quest to update
			struct.questId = new int[]{ questID };
			struct.questStage = new int[]{ stage };

			tryFinalizeAndSendPacket(OpcodeOut.SEND_QUESTS, struct, player);
		}
	}

	/**
	 * Shows a question menu
	 */
	public static void sendMenu(Player player, String[] options) {
		MenuOptionStruct struct = new MenuOptionStruct();
		int numOptions = options.length;
		struct.numOptions = numOptions;
		struct.optionTexts = new String[numOptions];
		for (int i = 0; i < numOptions; i++){
			struct.optionTexts[i] = options[i];
		}

		if (!player.isUsingCustomClient() && numOptions > 5) {
			LOGGER.error("Truncated options menu for authentic client! This is an error in programming!");
			player.playerServerMessage(MessageType.QUEST, "@red@There is a bug in the server which prevented you from seeing all options.");
			player.playerServerMessage(MessageType.QUEST, "@ran@Please report this! @whi@You are missing these options:");

			for (int i = 5; i < numOptions; i++) {
				player.playerServerMessage(MessageType.QUEST, "@ora@" + (i + 1) + ") @whi@" + options[i]);
			}
		}
		tryFinalizeAndSendPacket(OpcodeOut.SEND_OPTIONS_MENU_OPEN, struct, player);
	}

	public static void sendMessage(Player player, String message) {
		sendMessage(player, null, MessageType.GAME, message, 0, null);
	}

	public static void sendPlayerServerMessage(Player player, MessageType type, String message) {
		sendMessage(player, null, type, message, 0, null);
	}

	public static void sendMessage(Player player, Player sender, MessageType type, String message,
								   int iconSprite, String colorString) {
		MessageStruct struct = new MessageStruct();
		struct.iconSprite = iconSprite;
		struct.messageTypeRsId = type.getRsID();
		byte infoContained = 0;
		if (sender != null) {
			infoContained += 1;
		}
		if (colorString != null && !colorString.equals("")) {
			infoContained += 2;
		}

		struct.infoContained = infoContained;
		struct.message = message;
		struct.senderName = sender != null ? sender.getUsername() : "";
		struct.colorString = colorString;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_SERVER_MESSAGE, struct, player);
	}

	public static void sendPrayers(Player player, boolean[] activatedPrayers) {
		PrayersActiveStruct struct = new PrayersActiveStruct();
		int numPrayers = activatedPrayers.length;
		struct.prayerActive = new int[numPrayers];
		int i = 0;
		for (boolean prayerActive : activatedPrayers) {
			struct.prayerActive[i] = prayerActive ? 1 : 0;
			i++;
		}
		tryFinalizeAndSendPacket(OpcodeOut.SEND_PRAYERS_ACTIVE, struct, player);
	}

	public static void sendPrivacySettings(Player player) {
		PrivacySettingsStruct struct = new PrivacySettingsStruct();
		boolean fromAuthentic = !player.isUsingCustomClient();
		struct.blockChat = getPrivacySettingValue(player, PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES, fromAuthentic);
		struct.blockPrivate = getPrivacySettingValue(player, PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, fromAuthentic);
		struct.blockTrade = getPrivacySettingValue(player, PlayerSettings.PRIVACY_BLOCK_TRADE_REQUESTS, fromAuthentic);
		struct.blockDuel = getPrivacySettingValue(player, PlayerSettings.PRIVACY_BLOCK_DUEL_REQUESTS, fromAuthentic);
		tryFinalizeAndSendPacket(OpcodeOut.SEND_PRIVACY_SETTINGS, struct, player);
	}

	/**
	 * Send a private message
	 */
	public static void sendPrivateMessageReceived(Player player, Player sender, String message, boolean isGlobal) {
		if (isGlobal && player.getBlockGlobalFriend())
			return;

		PrivateMessageStruct struct = new PrivateMessageStruct();
		// TODO: we won't be able to reach across servers like this to access incrementPrivateMessages if there's more than one server
		// It will need to be rewritten when there is a proper login server managing private messages.
		struct.totalSentMessages = sender.getWorld().getServer().incrementPrivateMessagesSent();
		struct.worldNumber = sender.getWorld().getServer().getConfig().WORLD_NUMBER;
		struct.message = message;
		if (!isGlobal) {
			struct.playerName = struct.formerName = sender.getUsername();
		} else {
			if (player.isUsing233CompatibleClient()) {
				struct.playerName = struct.formerName = "Global$";
				struct.message = "@ora@[@gre@" + sender.getUsername() + "@ora@]:@cya@ " + message;
			} else if (player.isUsing177CompatibleClient()) {
				struct.playerName = struct.formerName = "Global";
				// can't change colour mid line in 177 chat I think...
				struct.message = "[" + sender.getUsername() + "]: " + message;
			} else if (isRetroClient(player)) {
				struct.playerName = struct.formerName = "Global";
				struct.message = "@ora@[@gre@" + sender.getUsername() + "@ora@]:@cya@ " + message;
			} else {
				struct.playerName = struct.formerName = "Global$" + sender.getUsername();
			}
		}
		struct.iconSprite = player.isUsing233CompatibleClient() ? sender.getIconAuthentic() : sender.getIcon();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_PRIVATE_MESSAGE, struct, player);
	}

	public static void sendPrivateMessageSent(Player player, long usernameHash, String message, boolean isGlobal) {
		PrivateMessageStruct struct = new PrivateMessageStruct();
		struct.playerName = !isGlobal ? DataConversions.hashToUsername(usernameHash) : "Global$";
		struct.message = message;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_PRIVATE_MESSAGE_SENT, struct, player);
	}

	public static void sendRemoveItem(Player player, int slot) {
		if (isRetroClient(player)) {
			// doesn't have the ability to update per item & must send entire inventory
			sendInventory(player);
		} else {
			InventoryUpdateStruct struct = new InventoryUpdateStruct();
			struct.slot = slot;
			tryFinalizeAndSendPacket(OpcodeOut.SEND_INVENTORY_REMOVE_ITEM, struct, player);
		}
	}

	/**
	 * Sends a sound effect
	 */
	public static void sendSound(Player player, String soundName) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			// F2P does not have sound effects
			return;
		}

		PlaySoundStruct struct = new PlaySoundStruct();
		struct.soundName = soundName;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_PLAY_SOUND, struct, player);
	}

	/**
	 * Updates just one stat
	 */
	public static void sendStat(Player player, int stat) {
		StatUpdateStruct struct = new StatUpdateStruct();
		struct.statId = stat;
		struct.currentLevel = player.getSkills().getLevel(stat);
		struct.maxLevel = player.getSkills().getMaxStat(stat);
		struct.experience = player.getSkills().getExperience(stat);
		tryFinalizeAndSendPacket(OpcodeOut.SEND_STAT, struct, player);
	}

	public static void sendExperience(Player player, int stat) {
		ExperienceStruct struct = new ExperienceStruct();
		struct.statId = stat;
		struct.experience = player.getSkills().getExperience(stat);
		tryFinalizeAndSendPacket(OpcodeOut.SEND_EXPERIENCE, struct, player);
	}

	public static void sendExperienceToggle(Player player) {
		ExperienceToggleStruct struct = new ExperienceToggleStruct();
		struct.isExperienceFrozen = player.isExperienceFrozen() ? 1 : 0;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_EXPERIENCE_TOGGLE, struct, player);
	}

	/**
	 * Updates the users stats
	 */
	public static void sendStats(Player player) {
		StatInfoStruct struct = new StatInfoStruct();
		// TODO: method player.getWorld().getServer().getConstants().getSkills().getSkillName(i)
		// should ensure uniqueness based on config or an alternative method may need to be used
		// to identify named skill to place info on for retro client
		int i = 0;
		for (int lvl : player.getSkills().getLevels()) {
			switch (player.getWorld().getServer().getConstants().getSkills().getSkillName(i)) {
				case "Attack":
					struct.currentAttack = lvl;
					break;
				case "Defense":
					struct.currentDefense = lvl;
					break;
				case "Strength":
					struct.currentStrength = lvl;
					break;
				case "Hits":
					struct.currentHits = lvl;
					break;
				case "Ranged":
					struct.currentRanged = lvl;
					break;
				case "PrayGood":
					struct.currentPrayGood = lvl;
					break;
				case "PrayEvil":
					struct.currentPrayEvil = lvl;
					break;
				case "Prayer":
					struct.currentPrayer = lvl;
					break;
				case "GoodMagic":
					struct.currentGoodMagic = lvl;
					break;
				case "EvilMagic":
					struct.currentEvilMagic = lvl;
					break;
				case "Magic":
					struct.currentMagic = lvl;
					break;
				case "Cooking":
					struct.currentCooking = lvl;
					break;
				case "Woodcut":
				case "Woodcutting":
					struct.currentWoodcutting = lvl;
					break;
				case "Fletching":
					struct.currentFletching = lvl;
					break;
				case "Fishing":
					struct.currentFishing = lvl;
					break;
				case "Firemaking":
					struct.currentFiremaking = lvl;
					break;
				case "Crafting":
					struct.currentCrafting = lvl;
					break;
				case "Smithing":
					struct.currentSmithing = lvl;
					break;
				case "Mining":
					struct.currentMining = lvl;
					break;
				case "Herblaw":
					struct.currentHerblaw = lvl;
					break;
				case "Agility":
					struct.currentAgility = lvl;
					break;
				case "Thieving":
					struct.currentThieving = lvl;
					break;
				case "Runecraft":
					struct.currentRunecrafting = lvl;
					break;
				case "Harvesting":
					struct.currentHarvesting = lvl;
					break;
				case "Influence":
					struct.currentInfluence = lvl;
					break;
				case "Tailoring":
					struct.currentTailoring = lvl;
					break;
			}
			i++;
		}

		i = 0;
		for (int lvl : player.getSkills().getMaxStats()) {
			switch (player.getWorld().getServer().getConstants().getSkills().getSkillName(i)) {
				case "Attack":
					struct.maxAttack = lvl;
					break;
				case "Defense":
					struct.maxDefense = lvl;
					break;
				case "Strength":
					struct.maxStrength = lvl;
					break;
				case "Hits":
					struct.maxHits = lvl;
					break;
				case "Ranged":
					struct.maxRanged = lvl;
					break;
				case "PrayGood":
					struct.maxPrayGood = lvl;
					break;
				case "PrayEvil":
					struct.maxPrayEvil = lvl;
					break;
				case "Prayer":
					struct.maxPrayer = lvl;
					break;
				case "GoodMagic":
					struct.maxGoodMagic = lvl;
					break;
				case "EvilMagic":
					struct.maxEvilMagic = lvl;
					break;
				case "Magic":
					struct.maxMagic = lvl;
					break;
				case "Cooking":
					struct.maxCooking = lvl;
					break;
				case "Woodcut":
				case "Woodcutting":
					struct.maxWoodcutting = lvl;
					break;
				case "Fletching":
					struct.maxFletching = lvl;
					break;
				case "Fishing":
					struct.maxFishing = lvl;
					break;
				case "Firemaking":
					struct.maxFiremaking = lvl;
					break;
				case "Crafting":
					struct.maxCrafting = lvl;
					break;
				case "Smithing":
					struct.maxSmithing = lvl;
					break;
				case "Mining":
					struct.maxMining = lvl;
					break;
				case "Herblaw":
					struct.maxHerblaw = lvl;
					break;
				case "Agility":
					struct.maxAgility = lvl;
					break;
				case "Thieving":
					struct.maxThieving = lvl;
					break;
				case "Runecraft":
					struct.maxRunecrafting = lvl;
					break;
				case "Harvesting":
					struct.maxHarvesting = lvl;
					break;
				case "Influence":
					struct.maxInfluence = lvl;
					break;
				case "Tailoring":
					struct.maxTailoring = lvl;
					break;
			}
			i++;
		}

		i = 0;
		for (int exp : player.getSkills().getExperiences()) {
			switch (player.getWorld().getServer().getConstants().getSkills().getSkillName(i)) {
				case "Attack":
					struct.experienceAttack = exp;
					break;
				case "Defense":
					struct.experienceDefense = exp;
					break;
				case "Strength":
					struct.experienceStrength = exp;
					break;
				case "Hits":
					struct.experienceHits = exp;
					break;
				case "Ranged":
					struct.experienceRanged = exp;
					break;
				case "PrayGood":
					struct.experiencePrayGood = exp;
					break;
				case "PrayEvil":
					struct.experiencePrayEvil = exp;
					break;
				case "Prayer":
					struct.experiencePrayer = exp;
					break;
				case "GoodMagic":
					struct.experienceGoodMagic= exp;
					break;
				case "EvilMagic":
					struct.experienceEvilMagic = exp;
					break;
				case "Magic":
					struct.experienceMagic = exp;
					break;
				case "Cooking":
					struct.experienceCooking = exp;
					break;
				case "Woodcut":
				case "Woodcutting":
					struct.experienceWoodcutting = exp;
					break;
				case "Fletching":
					struct.experienceFletching = exp;
					break;
				case "Fishing":
					struct.experienceFishing = exp;
					break;
				case "Firemaking":
					struct.experienceFiremaking = exp;
					break;
				case "Crafting":
					struct.experienceCrafting = exp;
					break;
				case "Smithing":
					struct.experienceSmithing = exp;
					break;
				case "Mining":
					struct.experienceMining = exp;
					break;
				case "Herblaw":
					struct.experienceHerblaw = exp;
					break;
				case "Agility":
					struct.experienceAgility = exp;
					break;
				case "Thieving":
					struct.experienceThieving = exp;
					break;
				case "Runecraft":
					struct.experienceRunecrafting = exp;
					break;
				case "Harvesting":
					struct.experienceHarvesting = exp;
					break;
				case "Influence":
					struct.experienceInfluence = exp;
					break;
				case "Tailoring":
					struct.experienceTailoring = exp;
					break;
			}
			i++;
		}

		int questPoints = player.getQuestPoints();
		struct.questPoints = questPoints;

		// computed for compat if retro client on modern world
		struct.useInfluence = player.getConfig().INFLUENCE_INSTEAD_QP;
		int computedInfluence = DataConversions.questPointsToInfluence(questPoints, player.getConfig().PLAYER_LEVEL_LIMIT);
		struct.computedInfluence = computedInfluence;
		struct.computedExperienceInfluence = player.getSkills().experienceForLevel(computedInfluence);

		tryFinalizeAndSendPacket(OpcodeOut.SEND_STATS, struct, player);
	}

	public static void sendTeleBubble(Player player, int x, int y, boolean grab) {
		TeleBubbleStruct struct = new TeleBubbleStruct();
		struct.isGrab = grab ? 1 : 0; // 1 for telegrab/Iban's magic; 0 for teleportation
		struct.localPoint = new Point(x - player.getX(), y - player.getY());
		tryFinalizeAndSendPacket(OpcodeOut.SEND_BUBBLE, struct, player);
	}

	public static void sendSecondTradeScreen(Player player) {
		Player with = player.getTrade().getTradeRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}

		TradeConfirmStruct struct = new TradeConfirmStruct();
		struct.targetPlayer = with.getUsername();

		int tradedSize, i;
		tradedSize = with.getTrade().getTradeOffer().getItems().size();
		struct.opponentTradeCount = tradedSize;
		struct.opponentCatalogIDs = new int[tradedSize];
		struct.opponentAmounts = new int[tradedSize];
		if (player.getConfig().WANT_BANK_NOTES) {
			struct.opponentNoted = new int[tradedSize];
		}
		i = 0;
		for (Item item : with.getTrade().getTradeOffer().getItems()) {
			struct.opponentCatalogIDs[i] = item.getCatalogId();
			if (item.getNoted() && !player.isUsingCustomClient()) {
				String itemName = item.getDef(player.getWorld()).getName();
				player.playerServerMessage(MessageType.QUEST,
					String.format("@ran@Please Confirm: @whi@Other player is offering @gre@%d @yel@%s", item.getAmount(), itemName));
			}
			if (struct.opponentNoted != null) {
				struct.opponentNoted[i] = item.getNoted() ? 1 : 0;
			}
			struct.opponentAmounts[i] = item.getAmount();
			i++;
		}

		tradedSize = player.getTrade().getTradeOffer().getItems().size();
		struct.myCount = tradedSize;
		struct.myCatalogIDs = new int[tradedSize];
		struct.myAmounts = new int[tradedSize];
		if (player.getConfig().WANT_BANK_NOTES) {
			struct.myNoted = new int[tradedSize];
		}
		i = 0;
		for (Item item : player.getTrade().getTradeOffer().getItems()) {
			struct.myCatalogIDs[i] = item.getCatalogId();
			if (struct.myNoted != null) {
				struct.myNoted[i] = item.getNoted() ? 1 : 0;
			}
			struct.myAmounts[i] = item.getAmount();
			i++;
		}

		try {
			tryFinalizeAndSendPacket(OpcodeOut.SEND_TRADE_OPEN_CONFIRM, struct, player);
		} catch (GameNetworkException gne) {
			// an unsupported catalog id was received for authentic client
			sendMessage(player, String.format("Cannot handle inauthentic item ID %s", gne.getExposedDetail()));
			sendMessage(with, String.format("Other player cannot handle inauthentic item ID %s", gne.getExposedDetail()));
			player.getTrade().setTradeActive(false);
			with.getTrade().setTradeActive(false);
			sendTradeWindowClose(player);
			sendTradeWindowClose(with);
		}
	}

	public static void sendTradeAcceptUpdate(Player player) {
		Player with = player.getTrade().getTradeRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}
		TradeAcceptStruct struct = new TradeAcceptStruct();
		struct.accepted = with.getTrade().isTradeAccepted() ? 1 : 0;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_TRADE_OTHER_ACCEPTED, struct, player);
	}

	// authentically, this function is only called to confirm cancellation of previous trade acceptance (new items added)
	public static void sendOwnTradeAcceptUpdate(Player player) {
		Player with = player.getTrade().getTradeRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}
		TradeAcceptStruct struct = new TradeAcceptStruct();
		struct.accepted = player.getTrade().isTradeAccepted() ? 1 : 0;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_TRADE_ACCEPTED, struct, player);
	}

	public static void sendTradeItems(Player player) {
		Player with = player.getTrade().getTradeRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}
		TradeTransactionStruct struct = new TradeTransactionStruct();
		List<Item> items = with.getTrade().getTradeOffer().getItems();
		int tradedSize, i;
		synchronized(items) {
			tradedSize = items.size();
			struct.opponentTradeCount = tradedSize;
			struct.opponentCatalogIDs = new int[tradedSize];
			struct.opponentAmounts = new int[tradedSize];
			if (player.getConfig().WANT_BANK_NOTES) {
				struct.opponentNoted = new int[tradedSize];
			}
			i = 0;
			for (Item item : items) {
				struct.opponentCatalogIDs[i] = player.isUsingCustomClient() ? item.getCatalogId() : item.getCatalogIdAuthenticNoting();
				if (item.getNoted() && !player.isUsingCustomClient()) {
					String itemName = item.getDef(player.getWorld()).getName();
					player.playerServerMessage(MessageType.QUEST,
						String.format("@whi@Other player offered @gre@%d @yel@%s", item.getAmount(), itemName));
				}
				if (struct.opponentNoted != null) {
					struct.opponentNoted[i] = item.getNoted() ? 1 : 0;
				}
				struct.opponentAmounts[i] = item.getAmount();
				i++;
			}
		}

		items = player.getTrade().getTradeOffer().getItems();
		synchronized (items) {
			tradedSize = items.size();
			struct.myCount = tradedSize;
			struct.myCatalogIDs = new int[tradedSize];
			struct.myAmounts = new int[tradedSize];
			if (player.getConfig().WANT_BANK_NOTES) {
				struct.myNoted = new int[tradedSize];
			}
			i = 0;
			for (Item item : items) {
				struct.myCatalogIDs[i] = item.getCatalogId();
				if (struct.myNoted != null) {
					struct.myNoted[i] = item.getNoted() ? 1 : 0;
				}
				struct.myAmounts[i] = item.getAmount();
				i++;
			}
		}
		tryFinalizeAndSendPacket(OpcodeOut.SEND_TRADE_OTHER_ITEMS, struct, player);
	}

	public static void sendTradeWindowClose(Player player) {
		NoPayloadStruct struct = new NoPayloadStruct();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_TRADE_CLOSE, struct, player);
	}

	public static void sendTradeWindowOpen(Player player) {
		Player with = player.getTrade().getTradeRecipient();
		if (with == null) { // This shouldn't happen
			return;
		}
		TradeShowWindowStruct struct = new TradeShowWindowStruct();
		struct.serverIndex = with.getIndex();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_TRADE_WINDOW, struct, player);
	}

	public static void sendInventoryUpdateItem(Player player, int slot) {
		if (isRetroClient(player)) {
			// doesn't have the ability to update per item & must send entire inventory
			sendInventory(player);
		} else {
			InventoryUpdateStruct struct = new InventoryUpdateStruct();
			struct.slot = slot;
			Item item = player.getCarriedItems().getInventory().get(slot);
			if (item != null) {
				struct.catalogID = player.isUsingCustomClient() ? item.getCatalogId() : item.getCatalogIdAuthenticNoting();
				struct.wielded = item.isWielded() ? 1 : 0;
				struct.noted = item.getNoted() ? 1 : 0;
				struct.amount = item.getDef(player.getWorld()).isStackable() || item.getNoted() ?
					displayableStack(player, item.getAmount()) : 0;
			} else {
				LOGGER.warn(String.format("Null item in %s's inventory! (slot %d)", player.getUsername(), slot ));
				struct.catalogID = 0;
				struct.wielded = 0;
				struct.amount = 0;
			}
			tryFinalizeAndSendPacket(OpcodeOut.SEND_INVENTORY_UPDATEITEM, struct, player);
		}
	}

	private static int displayableStack(Player player, int amount) {
		final int MAXSTACK = !player.getConfig().SHORT_MAX_STACKS ? Integer.MAX_VALUE : (Short.MAX_VALUE - Short.MIN_VALUE);
		return Math.min(amount, MAXSTACK);
	}

	public static void sendWakeUp(Player player, boolean success, boolean silent) {
		if (!silent) {
			if (success) {
				player.handleWakeup();
				sendMessage(player, "You wake up - feeling refreshed");
			} else {
				sendMessage(player, "You are unexpectedly awoken! You still feel tired");
			}
		}
		player.setSleeping(false);
		NoPayloadStruct struct = new NoPayloadStruct();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_STOPSLEEP, struct, player);
	}

	/**
	 * Sent when the user changes coords incase they moved up/down a level
	 */
	public static void sendWorldInfo(Player player) {
		WorldInfoStruct struct = new WorldInfoStruct();
		struct.serverIndex = player.getIndex();
		struct.planeWidth = 2304;
		struct.planeHeight = 1776;
		struct.planeFloor = Formulae.getHeight(player.getLocation());
		struct.distanceBetweenFloors = 944;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_WORLD_INFO, struct, player);
	}

	/**
	 * Show the bank window
	 */
	public static void showBank(Player player) {
		int itemsInBank = player.getBank().size();
		if (!player.isUsingCustomClient()) {
			if ((player.getWorld().getServer().getConfig().MEMBER_WORLD && itemsInBank > 192) ||
				(!player.getWorld().getServer().getConfig().MEMBER_WORLD && itemsInBank > 48)) {
				sendMessage(player, "Warning: Unable to display all items in bank!");
			}
			// If bank is filled to page 4 and bank size reports supporting more than 4 pages
			if (itemsInBank > (192 - 48) && player.getWorld().getMaxBankSize() > 192) {
				sendMessage(player, "Warning: Bank is unauthentically large. Deposited items may not be visible to be withdrawn!");
			}
		}

		BankStruct struct = new BankStruct();
		struct.itemsStoredSize = itemsInBank;
		struct.maxBankSize = player.getWorld().getMaxBankSize();
		struct.catalogIDs = new int[itemsInBank];
		struct.amount = new int[itemsInBank];
		synchronized (player.getBank().getItems()) {
			int i = 0;
			for (Item item : player.getBank().getItems()) {
				struct.catalogIDs[i] = player.isUsingCustomClient() ? item.getCatalogId() : item.getCatalogIdAuthenticNoting();
				struct.amount[i] = item.getAmount();
				i++;
			}
		}
		tryFinalizeAndSendPacket(OpcodeOut.SEND_BANK_OPEN, struct, player);
	}

	public static void showBankOther(Player player, List<Item> bank) {
		int itemsInBank = bank.size();
		if (!player.isUsingCustomClient()) {
			if ((player.getWorld().getServer().getConfig().MEMBER_WORLD && itemsInBank > 192) ||
				(!player.getWorld().getServer().getConfig().MEMBER_WORLD && itemsInBank > 48)) {
				sendMessage(player, "Warning: Unable to display all items in bank!");
			}
			// If bank is filled to page 4 and bank size reports supporting more than 4 pages
			if (itemsInBank > (192 - 48) && player.getWorld().getMaxBankSize() > 192) {
				sendMessage(player, "Warning: Bank is unauthentically large. Deposited items may not be visible to be withdrawn!");
			}
		}

		BankStruct struct = new BankStruct();
		struct.itemsStoredSize = itemsInBank;
		struct.maxBankSize = player.getWorld().getMaxBankSize();
		struct.catalogIDs = new int[itemsInBank];
		struct.amount = new int[itemsInBank];

		int i = 0;
		for (Item item : bank) {
			struct.catalogIDs[i] = player.isUsingCustomClient() ? item.getCatalogId() : item.getCatalogIdAuthenticNoting();
			struct.amount[i] = item.getAmount();
			i++;
		}

		tryFinalizeAndSendPacket(OpcodeOut.SEND_BANK_OPEN, struct, player);

	}

	public static void showShop(Player player, Shop shop) {
		ShopStruct struct = new ShopStruct();
		int playerMaxId = player.isUsingCustomClient() ? ItemId.NOTHING.id() : player.getClientLimitations().maxItemId;
		int worldMaxId = player.getConfig().RESTRICT_ITEM_ID;
		int maxId = Integer.compareUnsigned(playerMaxId, worldMaxId) <= 0 ? playerMaxId : worldMaxId;
		int shopSize = shop.getFilteredSize(maxId);
		struct.itemsStockSize = shopSize;
		struct.isGeneralStore = shop.isGeneral() ? 1 : 0;
		struct.sellModifier = shop.getSellModifier();
		struct.buyModifier = shop.getBuyModifier();
		struct.stockSensitivity = shop.getPriceModifier(); // This is how much being over/understock affects the price
		struct.catalogIDs = new int[shopSize];
		struct.amount = new int[shopSize];
		struct.baseAmount = new int[shopSize];
		struct.price = new int[shopSize];

		int idx = 0;
		for (int i = 0; i < shop.getShopSize(); i++) {
			Item item = shop.getShopItem(i);
			if (maxId > ItemId.NOTHING.id() && item.getCatalogId() > maxId)
				continue;
			struct.catalogIDs[idx] = player.isUsingCustomClient() ? item.getCatalogId() : item.getCatalogIdAuthenticNoting();
			struct.amount[idx] = item.getAmount();
			struct.baseAmount[idx] = shop.getStock(item.getCatalogId());
			struct.price[idx] = 0; // TODO: get from shop list for early protocols??
			idx++;
		}
		tryFinalizeAndSendPacket(OpcodeOut.SEND_SHOP_OPEN, struct, player);
	}

	/**
	 * Sends a system update message
	 */
	public static void startShutdown(Player player, int seconds) {
		SystemUpdateStruct struct = new SystemUpdateStruct();
		struct.seconds = seconds;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_SYSTEM_UPDATE, struct, player);
	}

	/**
	 * Sends the elixir timer
	 */
	public static void sendElixirTimer(Player player, int seconds) {
		if (!player.getConfig().WANT_EXPERIENCE_ELIXIRS) return;

		ElixirUpdateStruct struct = new ElixirUpdateStruct();
		struct.timeSeconds = seconds;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_ELIXIR, struct, player);
	}

	/**
	 * Updates the id and amount of an item in the bank
	 */
	public static void updateBankItem(Player player, int slot, Item newId, int amount) {
		BankUpdateStruct struct = new BankUpdateStruct();
		struct.slot = slot;
		struct.catalogID = amount == 0 && player.isUsingCustomClient() ? 0 : newId.getCatalogId();
		struct.amount = amount;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_BANK_UPDATE, struct, player);
	}

	public static void sendRemoveProgressBar(Player player) {
		ProgressBarStruct struct = new ProgressBarStruct();
		struct.interfaceId = 2;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_STATUS_PROGRESS_BAR, struct, player);
	}

	public static void sendProgressBar(Player player, int delay, int repeatFor) {
		// TODO: it could be cool to abuse a textbox for original clients, or send an NPC with a health bar. :-)
		ProgressBarStruct struct = new ProgressBarStruct();
		struct.interfaceId = 1;
		struct.delay = delay;
		struct.timesRepeat = repeatFor;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_STATUS_PROGRESS_BAR, struct, player);
	}

	public static void sendUpdateProgressBar(Player player, int repeatFor) {
		ProgressBarStruct struct = new ProgressBarStruct();
		struct.interfaceId = 3;
		struct.timesRepeat = repeatFor;  //where is this called from
		tryFinalizeAndSendPacket(OpcodeOut.SEND_STATUS_PROGRESS_BAR, struct, player);
	}

	public static void sendProgress(Player player, long repeated) {
		AuctionProgressStruct struct = new AuctionProgressStruct();
		struct.interfaceId = 0;
		struct.delay = 3;
		struct.timesRepeat = (int)repeated;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_AUCTION_PROGRESS, struct, player);
	}

	public static void sendBankPinInterface(Player player) {
		BankPinStruct struct = new BankPinStruct();
		struct.isOpen = 1;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_BANK_PIN_INTERFACE, struct, player);
	}

	public static void sendCloseBankPinInterface(Player player) {
		BankPinStruct struct = new BankPinStruct();
		struct.isOpen = 0;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_BANK_PIN_INTERFACE, struct, player);
	}

	public static void sendInputBox(Player player, String s) {
		if (!player.isUsingCustomClient()) {
			sendMessage(player, "@lre@Input Box not implemented for authentic clients.");
			sendMessage(player, "@lre@Server asked the following: @whi@" + s);
			return;
		}
		InputBoxStruct struct = new InputBoxStruct();
		struct.messagePrompt = s;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_INPUT_BOX, struct, player);
	}

	public static void sendUpdatedPlayer(Player player) {
		try {
			player.getWorld().getServer().getGameUpdater().sendUpdatePackets(player);
		} catch (Throwable e) {
			LOGGER.catching(e);
		}
	}

	static void sendLogin(Player player) {
		try {
			if (player.getWorld().registerPlayer(player)) {
                sendPrivacySettings(player);
                sendMessage(player, null,  MessageType.QUEST, "Welcome to " + player.getConfig().SERVER_NAME + "!", 0, null);

				if (HolidayDropEvent.isOccurring(player) && player.getWorld().getServer().getConfig().WANT_BANK_PINS) { // TODO: this is not a good way to detect that we are not using the RSCP config
				    sendMessage(player, null, MessageType.QUEST, "@mag@There is a Holiday Drop Event going on now! Type @gre@::drop@mag@ for more information.", 0, null);
                }

                sendGameSettings(player);
				sendWorldInfo(player);
                sendQuestInfo(player);
                sendPlayerOnTutorial(player);
                sendLoginBox(player);

				sendPlayerOnBlackHole(player);
				if (player.getLastLogin() == 0L) {
					sendAppearanceScreen(player);
					if (!player.getConfig().USES_CLASSES) {
						for (int itemId : player.getWorld().getServer().getConstants().STARTER_ITEMS) {
							Item i = new Item(itemId);
							player.getCarriedItems().getInventory().add(i, false);
						}
					}
					//Block PK chat by default.
					player.getCache().set("setting_block_global", 3);
				}

                sendInventory(player);
                player.checkEquipment();

                sendStats(player);
                sendEquipmentStats(player);
                sendPrayers(player, player.getPrayers().getActivePrayers());
                sendFatigue(player);

                if (player.getCache().hasKey("openpk_points") && player.getCache().getLong("openpk_points") > 0) {
                	player.setOpenPkPoints(player.getCache().getLong("openpk_points"));
				}

				player.getWorld().getServer().getGameUpdater().sendUpdatePackets(player);
				long timeTillShutdown = player.getWorld().getServer().getTimeUntilShutdown();
				if (timeTillShutdown > -1)
					startShutdown(player, (int)(timeTillShutdown / 1000));

				int elixir = player.getElixir();
				if (elixir > -1)
					sendElixirTimer(player, player.getElixir());

				sendWakeUp(player, false, true);

				if (player.isMuted()) {
					// doesn't seem authentic to have notified the player that they are muted after login
					/*player.message("You have been " + (player.getMuteExpires() == -1 ? "permanently" : "temporarily") + " due to breaking a rule");
					if (player.getMuteExpires() != -1) {
						player.message("This mute will remain for a further " + DataConversions.formatTimeString(player.getMinutesMuteLeft()));
					}
					player.message("To prevent further mutes please read the rules");*/
				}

				if (player.getLocation().inTutorialLanding()) {
					sendBox(player, "@gre@Welcome to the " + player.getConfig().SERVER_NAME + " tutorial.% %Most actions are performed with the mouse. To walk around left click on the ground where you want to walk. To interact with something, first move your mouse pointer over it. Then left click or right click to perform different actions% %Try left clicking on one of the guides to talk to her. She will tell you more about how to play", true);
				}

				sendNpcKills(player);

				sendCombatStyle(player);
				sendIronManMode(player);

				if (player.getConfig().WANT_BANK_PRESETS)
					sendBankPresets(player);

				if (!player.getConfig().WANT_FATIGUE)
					sendExperienceToggle(player);

				/*if (!getServer().getConfig().MEMBER_WORLD) {
					p.unwieldMembersItems();
				}*/

				if (!player.getLocation().inWilderness()) {
					if (player.getConfig().SPAWN_AUCTION_NPCS) {
						player.getWorld().getMarket().addCollectableItemsNotificationTask(player);
					}
				}

				//AchievementSystem.achievementListGUI(p);
				sendFriendList(player);
				sendIgnoreList(player);
			} else {
				LOGGER.info("Send Login, Failed: " + player.getUsername());
				player.getChannel().close();
			}
		} catch (Throwable e) {
			LOGGER.catching(e);
		}
	}

	public static void sendOnlineList(Player player, ArrayList<Player> players, ArrayList<String> locations, int online) {
	    if (!player.isUsingCustomClient()) {
	    	StringBuilder onlinePlayers = new StringBuilder(String.format("@lre@Players online @gre@(%d) %%", online));
			for (int i = 0; i < players.size(); i++) {
				onlinePlayers.append("@whi@");
				onlinePlayers.append(players.get(i).getUsername());
				if (locations.get(i).length() > 0) {
					onlinePlayers.append(" @yel@(");
					onlinePlayers.append(locations.get(i));
					onlinePlayers.append(")");
				}
				if (i + 1 != players.size()) {
					onlinePlayers.append(" @mag@; ");
				}
			}

			ActionSender.sendBox(player, onlinePlayers.toString(), true);
        } else {
			OnlineListStruct struct = new OnlineListStruct();
			struct.numberOnline = online;
			int count = players.size();
			struct.playerCount = count;
			struct.name = new String[count];
			struct.icon = new int[count];
			struct.location = new String[count];

			for (int i = 0; i < players.size(); i++) {
				Player friend = players.get(i);
				struct.name[i] = friend.getUsername();
				struct.icon[i] = friend.getIcon();
				struct.location[i] = locations.get(i);
			}

			tryFinalizeAndSendPacket(OpcodeOut.SEND_ONLINE_LIST, struct, player);
        }
	}

	public static void showFishingTrawlerInterface(Player player) {
        TrawlerUpdateStruct struct = new TrawlerUpdateStruct();
        struct.interfaceId = 6;
        struct.actionId = 0;
        tryFinalizeAndSendPacket(OpcodeOut.SEND_FISHING_TRAWLER, struct, player);
	}

	public static void hideFishingTrawlerInterface(Player player) {
		TrawlerUpdateStruct struct = new TrawlerUpdateStruct();
		struct.interfaceId = 6;
		struct.actionId = 2;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_FISHING_TRAWLER, struct, player);
	}

	public static void updateFishingTrawler(Player player, int waterLevel, int minutesLeft, int fishCaught,
											boolean netBroken) {
		TrawlerUpdateStruct struct = new TrawlerUpdateStruct();
		struct.interfaceId = 6;
		struct.actionId = 1;
		struct.waterLevel = waterLevel;
		struct.fishCaught = fishCaught;
		struct.minutesLeft = minutesLeft;
		struct.isNetBroken = netBroken ? 1 : 0;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_FISHING_TRAWLER, struct, player);
	}

	public static void sendKillUpdate(Player player, long killedHash, long killerHash, int type) {
		if (!player.getConfig().WANT_KILL_FEED) return;

		KillUpdateStruct struct = new KillUpdateStruct();
		struct.victim = DataConversions.hashToUsername(killedHash);
		struct.attacker = DataConversions.hashToUsername(killerHash);
		struct.killType = type;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_KILL_ANNOUNCEMENT, struct, player);
	}

	public static void sendOpenAuctionHouse(final Player player) {
		player.getWorld().getMarket().addRequestOpenAuctionHouseTask(player);
	}

	public static void sendClan(Player player) {
		ClanStruct struct = new ClanStruct();
		struct.actionId = 0;
		struct.clanName = player.getClan().getClanName();
		struct.clanTag = player.getClan().getClanTag();
		struct.leaderName = player.getClan().getLeader().getUsername();
		struct.isLeader = player.getClan().getLeader().getUsername().equalsIgnoreCase(player.getUsername()) ? 1 : 0;
		int clanSize = player.getClan().getPlayers().size();
		struct.clanSize = clanSize;
		struct.clanMembers = new String[clanSize];
		struct.memberRanks = new int[clanSize];
		struct.isMemberOnline = new int[clanSize];

		int i = 0;
		for (ClanPlayer m : player.getClan().getPlayers()) {
			struct.clanMembers[i] = m.getUsername();
			struct.memberRanks[i] = m.getRank().getRankIndex();
			struct.isMemberOnline[i] = m.isOnline() ? 1 : 0;
			i++;
		}
		tryFinalizeAndSendPacket(OpcodeOut.SEND_CLAN, struct, player);
	}

	public static void sendParty(Player player) {
		PartyStruct struct = new PartyStruct();
		struct.actionId = 0;
		struct.leaderName = player.getParty().getLeader().getUsername();
		struct.isLeader = player.getParty().getLeader().getUsername().equalsIgnoreCase(player.getUsername()) ? 1 : 0;
		int partySize = player.getParty().getPlayers().size();
		struct.partySize = partySize;
		struct.partyMembers = new String[partySize];
		struct.memberRanks = new int[partySize];
		struct.isMemberOnline = new int[partySize];
		struct.currentHitsMembers = new int[partySize];
		struct.maximumHitsMembers = new int[partySize];
		struct.combatLevelsMembers = new int[partySize];
		struct.isMemberSkulled = new int[partySize];
		struct.isMemberDead = new int[partySize];
		struct.isShareLoot = new int[partySize];
		struct.partyMemberTotal = new int[partySize];
		struct.isInCombat = new int[partySize];
		struct.shareExp = new int[partySize];
		struct.shareExp2 = new long[partySize];

		int i = 0;
		for (PartyPlayer m : player.getParty().getPlayers()) {
			struct.partyMembers[i] = m.getUsername();
			struct.memberRanks[i] = m.getRank().getRankIndex();
			struct.isMemberOnline[i] = m.isOnline() ? 1 : 0;
			struct.currentHitsMembers[i] = m.getCurHp();
			struct.maximumHitsMembers[i] = m.getMaxHp();
			struct.combatLevelsMembers[i] = m.getCbLvl();
			struct.isMemberSkulled[i] = m.getSkull();
			struct.isMemberDead[i] = m.getPartyMemberDead();
			struct.isShareLoot[i] = m.getShareLoot();
			struct.partyMemberTotal[i] = m.getPartyMembersTotal();
			struct.isInCombat[i] = m.getInCombat();
			struct.shareExp[i] = m.getShareExp();
			struct.shareExp2[i] = m.getExpShared2();
			i++;
		}
		tryFinalizeAndSendPacket(OpcodeOut.SEND_PARTY, struct, player);
	}

	public static void sendClans(Player player) {
		ClanListStruct struct = new ClanListStruct();
		struct.actionId = 4;
		int totalClans = player.getWorld().getClanManager().getClans().size();
		struct.totalClans = totalClans;
		struct.clansInfo = new ClanStruct[totalClans];

		ClanStruct clan;
		player.getWorld().getClanManager().getClans().sort(ClanManager.CLAN_COMPERATOR);
		int i = 0;
		for (Clan c : player.getWorld().getClanManager().getClans()) {
			clan = new ClanStruct();
			clan.clanId = c.getClanID();
			clan.clanName = c.getClanName();
			clan.clanTag = c.getClanTag();
			clan.clanSize = c.getPlayers().size();
			clan.allowsSearchedJoin = c.getAllowSearchJoin();
			clan.clanPoints = c.getClanPoints();
			struct.clansInfo[i] = clan;
			i++;
		}
		tryFinalizeAndSendPacket(OpcodeOut.SEND_CLAN_LIST, struct, player);
	}

	public static void sendParties(Player player) {
		PartyListStruct struct = new PartyListStruct();
		struct.actionId = 4;
		int totalParties = player.getWorld().getPartyManager().getParties().size();
		struct.totalParties = totalParties;
		struct.partyInfo = new PartyStruct[totalParties];

		PartyStruct party;
		player.getWorld().getPartyManager().getParties().sort(PartyManager.PARTY_COMPERATOR);
		int i = 0;
		for (Party p : player.getWorld().getPartyManager().getParties()) {
			party = new PartyStruct();
			party.partyId = p.getPartyID();
			party.partySize = p.getPlayers().size();
			party.allowsSearchedJoin = p.getAllowSearchJoin();
			party.partyPoints = p.getPartyPoints();
			struct.partyInfo[i] = party;
			i++;
		}
		tryFinalizeAndSendPacket(OpcodeOut.SEND_PARTY_LIST, struct, player);
	}

	public static void sendLeaveClan(Player playerReference) {
		ClanStruct struct = new ClanStruct();
		struct.actionId = 1;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_CLAN, struct, playerReference);
	}

	public static void sendLeaveParty(Player playerReference) {
		PartyStruct struct = new PartyStruct();
		struct.actionId = 1;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_PARTY, struct, playerReference);
	}

	public static void sendClanInvitationGUI(Player invited, String name, String username) {
		ClanStruct struct = new ClanStruct();
		struct.actionId = 2;
		struct.nameInviter = username;
		struct.clanName = name;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_CLAN, struct, invited);
	}

	public static void sendPartyInvitationGUI(Player invited, String name, String username) {
		PartyStruct struct = new PartyStruct();
		struct.actionId = 2;
		struct.nameInviter = username;
		struct.partyName = name;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_PARTY, struct, invited);
	}

	public static void sendClanSetting(Player player) {
		ClanSettingsStruct struct = new ClanSettingsStruct();
		struct.magicNumber = 3;
		struct.kickSetting = player.getClan().getKickSetting();
		struct.inviteSetting = player.getClan().getInviteSetting();
		struct.allowSearchJoin = player.getClan().getAllowSearchJoin();
		struct.allowSetting0 = player.getClan().isAllowed(0, player) ? 1 : 0;
		struct.allowSetting1 = player.getClan().isAllowed(1, player) ? 1 : 0;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_CLAN_SETTINGS, struct, player);
	}

	public static void sendPartySetting(Player player) {
		PartySettingsStruct struct = new PartySettingsStruct();
		struct.magicNumber = 3;
		struct.kickSetting = player.getParty().getKickSetting();
		struct.inviteSetting = player.getParty().getInviteSetting();
		struct.allowSearchJoin = player.getParty().getAllowSearchJoin();
		struct.allowSetting0 = player.getParty().isAllowed(0, player) ? 1 : 0;
		struct.allowSetting1 = player.getParty().isAllowed(1, player) ? 1 : 0;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_PARTY_SETTINGS, struct, player);
	}

	public static void sendIronManMode(Player player) {
		IronManStruct struct = new IronManStruct();
		struct.interfaceId = 2;
		struct.actionId = 0;
		struct.ironmanType = player.getIronMan();
		struct.ironmanRestriction = player.getIronManRestriction();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_IRONMAN, struct, player);
	}

	public static void sendIronManInterface(Player player) {
		IronManStruct struct = new IronManStruct();
		struct.interfaceId = 2;
		struct.actionId = 1;
		tryFinalizeAndSendPacket(OpcodeOut.SEND_IRONMAN, struct, player);
	}

	public static void sendHideIronManInterface(Player player) {
        IronManStruct struct = new IronManStruct();
        struct.interfaceId = 2;
        struct.actionId = 2;
        tryFinalizeAndSendPacket(OpcodeOut.SEND_IRONMAN, struct, player);
	}

}
