package orsc;

import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.entityhandling.defs.ItemDef;
import com.openrsc.client.entityhandling.instances.Item;
import com.openrsc.client.model.Sprite;
import orsc.buffers.RSBufferUtils;
import orsc.buffers.RSBuffer_Bits;
import orsc.enumerations.MessageType;
import orsc.enumerations.ORSCharacterDirection;
import orsc.graphics.gui.KillAnnouncer;
import orsc.graphics.gui.SocialLists;
import orsc.graphics.three.RSModel;
import orsc.multiclient.ClientPort;
import orsc.net.Network_Socket;
import orsc.util.FastMath;
import orsc.util.GenUtil;
import orsc.util.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static orsc.Config.isAndroid;

public class PacketHandler {

	private final RSBuffer_Bits packetsIncoming = new RSBuffer_Bits(30000);
	private Network_Socket clientStream;
	private mudclient mc;

	private static final Map<Integer, String> incomingOpcodeMap = new HashMap<Integer, String>() {{
		put(4, "CLOSE_CONNECTION_NOTIFY");
		put(5, "QUEST_STATUS");
		put(6, "UPDATE_STAKED_ITEMS_OPPONENT");
		put(15, "UPDATE_TRADE_ACCEPTANCE");
		put(20, "SHOW_CONFIRM_TRADE");
		put(25, "FLOOR_SET");
		put(30, "SYNC_DUEL_SETTINGS");
		put(33, "UPDATE_XP");
		put(36, "DISPLAY_TELEPORT_TELEGRAB_BUBBLE");
		put(42, "OPEN_BANK");
		put(48, "SCENERY_HANDLER");
		put(51, "PRIVACY_SETTINGS");
		put(52, "UPDATE_SYSTEM_UPDATE_TIMER");
		put(53, "SET_INVENTORY");
		put(59, "SHOW_APPEARANCE_CHANGE");
		put(79, "NPC_COORDS");
		put(83, "DISPLAY_DEATH_SCREEN");
		put(84, "WAKE_UP");
		put(87, "SEND_PM");
		put(89, "SHOW_DIALOGUE_SERVER_MESSAGE_NOT_TOP");
		put(90, "SET_INVENTORY_SLOT");
		put(91, "BOUNDARY_HANDLER");
		put(92, "INITIATE_TRADE");
		put(97, "UPDATE_ITEMS_TRADED_TO_YOU");
		put(99, "GROUNDITEM_HANDLER");
		put(101, "SHOW_SHOP");
		put(104, "UPDATE_NPC");
		put(109, "SET_IGNORE");
		put(111, "COMPLETED_TUTORIAL");
		put(114, "SET_FATIGUE");
		put(117, "FALL_ASLEEP");
		put(120, "RECEIVE_PM");
		put(123, "REMOVE_INVENTORY_SLOT");
		put(128, "CONCLUDE_TRADE");
		put(131, "SEND_MESSAGE");
		put(137, "EXIT_SHOP");
		put(149, "UPDATE_FRIEND");
		put(153, "SET_EQUIP_STATS");
		put(156, "SET_STATS");
		put(159, "UPDATE_STAT");
		put(162, "UPDATE_TRADE_RECIPIENT_ACCEPTANCE");
		put(165, "CLOSE_CONNECTION");
		put(172, "SHOW_CONFIRM_DUEL");
		put(176, "SHOW_DIALOGUE_DUEL");
		put(182, "SHOW_WELCOME");
		put(183, "DENY_LOGOUT");
		put(191, "PLAYER_COORDS");
		put(194, "INCORRECT_SLEEPWORD");
		put(203, "CLOSE_BANK");
		put(204, "PLAY_SOUND");
		put(206, "SET_PRAYERS");
		put(210, "UPDATE_DUEL_ACCEPTANCE");
		put(211, "UPDATE_ENTITIES");
		put(213, "NO_OP_WHILE_WAITING_FOR_NEW_APPEARANCE");
		put(222, "SHOW_DIALOGUE_SERVER_MESSAGE_TOP");
		put(225, "CANCEL_DUEL_DIALOGUE");
		put(234, "UPDATE_PLAYERS");
		put(237, "UPDATE_IGNORE_BECAUSE_OF_NAME_CHANGE");
		put(240, "GAME_SETTINGS");
		put(244, "SET_FATIGUE_SLEEPING");
		put(245, "SHOW_DIALOGUE_MENU");
		put(249, "UPDATE_BANK_ITEMS_DISPLAY");
		put(252, "DISABLE_OPTION_MENU");
		put(253, "UPDATE_DUEL_OPPONENT_ACCEPTANCE");

		// CUSTOM
		put(19, "SEND_SERVER_CONFIGS");
		put(34, "FREEZE_EXPERIENCE_TOGGLE");
		put(113, "SEND_IRONMAN");
		put(115, "SEND_ON_BLACK_HOLE");
		put(129, "COMBAT_STYLE_CHANGED");
		put(135, "BANK_PIN_INTERFACE");
		put(136, "ONLINE_LIST");
		put(147, "SEND_KILLS2");
		put(150, "UPDATE_PRESET");
		put(254, "UPDATE_EQUIPMENT");
		put(255, "UPDATE_EQUIPMENT_SLOT");
	}};


	public PacketHandler(mudclient mc) {
		this.mc = mc;
	}

	public Network_Socket getClientStream() {
		return clientStream;
	}

	public void setClientStream(Network_Socket clientStream) {
		this.clientStream = clientStream;
	}

	public void startThread(int andStart, Runnable proc) {
		try {

			Thread var3 = new Thread(proc);
			if (andStart == 1) {
				var3.setDaemon(true);
				var3.start();
			}
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "e.S(" + andStart + ',' + (proc != null ? "{...}" : "null") + ')');
		}
	}

	public final Socket openSocket(int port, String host) throws IOException {
		Socket s = new Socket(InetAddress.getByName(host), port);
		//s.setSendBufferSize(25000);
		//s.setReceiveBufferSize(25000);
		s.setSoTimeout(30000);
		s.setTcpNoDelay(true);
		return s;
	}

	public RSBuffer_Bits getPacketsIncoming() {
		return packetsIncoming;
	}

	public final void handlePacket(int opcode, int length) {
		if (length > 0)
			handlePacket1(opcode, length);
	}

	private void handlePacket1(int opcode, int length) {
		try {
			if (Config.DEBUG) {
				System.out.println("Frame: " + mc.getFrameCounter()
					+ ", Opcode: " + incomingOpcodeMap.get(opcode) + " (" + opcode + "), Length: " + length);
			}

			// Unhandled Opcodes Received...
			/*if (opcode == 9 || opcode == 34 || opcode == 16
					|| opcode == 39 || opcode == 98 || opcode == 32
					|| opcode == 55 || opcode == 94 || opcode == 7
					|| opcode == 23 || opcode == 71 || opcode == 119
					|| opcode == 49 || opcode == 0 || opcode == 28
					|| opcode == 95 || opcode == 157 || opcode == 21
					|| opcode == 29 || opcode == 255 || opcode == 246
					|| opcode == 8 || opcode == 37) return;

			else */
			if (opcode == 88) createNPC();

			else if (opcode == 134) { // Batch Progression
				if (!Config.S_BATCH_PROGRESSION) return;
				updateBatchProgression();
			}

			// Bank Pin Overlay
			else if (opcode == 135) displayBankPin();

				// Ironman Options
			else if (opcode == 113) setIronmanOptions();

				// Auction House Updates
			else if (opcode == 132) updateAuctionHouse();

				// Online List
			else if (opcode == 136) refreshOnlineList();

				// Fishing Trawler
			else if (opcode == 133) fishingTrawlerUpdate();

				// Clan Options
			else if (opcode == 112) updateClan();

			else if (opcode == 116) updateParty();

        /*else if(opcode == 50) { // Achievements
          int achievementStatus = this.packetsIncoming.getByte();
          if (achievementStatus == 1) {
            totalAchievements = this.packetsIncoming.getShort();
            for (int i = 0; i < totalAchievements; i++) {
              int achievementID = this.packetsIncoming.get32();
              byte progress = this.packetsIncoming.getByte();

              String achievementName = this.packetsIncoming.readString();
              String achievementTitle = this.packetsIncoming.readString();
              String achievementDesc = this.packetsIncoming.readString();

              achievementNames[achievementID] = achievementName;
              achievementTitles[achievementID] = achievementTitle;
              achievementDescs[achievementID] = achievementDesc;
              achievementProgress[achievementID] = progress;
            }
          } else if (achievementStatus == 2) {
            int achievementID = this.packetsIncoming.get32();
            byte progress = this.packetsIncoming.getByte();

            achievementProgress[achievementID] = progress;
          }
          return;
        }*/

			else if (opcode == 118) announceKill(); // Kill Announcement

			else if (opcode == 131) showMessage(); // Chat Message

				// Close Connection and send packet
			else if (opcode == 4) mc.closeConnection(true);

				// No logout allowed
			else if (opcode == 183) mc.cantLogout((byte) -65);

				// Close Connection and do not send packet
			else if (opcode == 165) mc.closeConnection(false);

				// Log In/Out Message
			else if (opcode == 149) sendConnectionMessage();

				// Name changed, but not on ignore list. (Bad packet)
			else if (opcode == 237) ignoreListNameChangeFailure();

				// Ignore List Update
			else if (opcode == 109) updateIgnoreList();

				// Chat Blocking Settings
			else if (opcode == 51) updateChatBlockSettings();

				// Receive Private Message
			else if (opcode == 120) receivePrivateMessage();

				// Send Private Message
			else if (opcode == 87) sendPrivateMessage();

			else if (opcode == 150) updatePreset();

				// Set Server Configs
			else if (opcode == 19) setServerConfiguration();

			else this.handlePacket2(opcode, length);

		} catch (RuntimeException var11) {
			throw GenUtil.makeThrowable(var11, "client.LD(" + "dummy" + ',' + length + ',' + opcode + ')');
		}
	}

	public final void handlePacket2(int opcode, int length) {
		try {

			// Show Other Players
			if (opcode == 191) showOtherPlayers(length);

				// Show Game Objects
			else if (opcode == 48) showGameObjects(length);

				// Inventory items
			else if (opcode == 53) updateInventory();

				//All Equipment sent
			else if (opcode == 254) updateEquipment();

				//Only update one slot
			else if (opcode == 255) updateEquipmentSlot();

				// Show Walls
			else if (opcode == 91) showWalls(length);

				// Show NPCs
			else if (opcode == 79) showNPCs(length);

				// NPC Appearances
			else if (opcode == 104) updateNPCAppearances();

				// Show Options Menu
			else if (opcode == 245) showOptionsMenu();

				// Load Area
			else if (opcode == 25) loadArea();

				// Load Stats and Experience
			else if (opcode == 156) {
				loadStats();
				loadExperience();
				loadQuestPoints();
				mc.updateQuestRewards();
				// Set Death Screen Timeout
			} else if (opcode == 83) mc.setDeathScreenTimeout(250);

				// Generate Object/Ground Items/Walls Counts
			else if (opcode == 211) generateCounts(length);

				// Appearance Change Menu
			else if (opcode == 59) mc.setShowAppearanceChange(true);

				// Trade Request
			else if (opcode == 92) showTradeDialog();

				// Confirm Trade
			else if (opcode == 128) setTradeConfirmed();

				// Trade Recipient Accept or Decline
			else if (opcode == 162) tradeRecipientDecision();

				// Close Shop Dialog
			else if (opcode == 137) mc.setShowDialogShop(false);

				// Trade Accept or Decline (Self)
			else if (opcode == 15) tradeSelfDecision();

				// Options Menu Settings
			else if (opcode == 240) updateOptionsMenuSettings();

			else if (opcode == 206) togglePrayer(length);

			else if (opcode == 232) mc.setShowContactDialogue(true);

			else if (opcode == 224) mc.setShowRecoveryDialogue(true);

				// Quest Stage Update
			else if (opcode == 5) {
				updateQuestStage();
				mc.updateQuestCommandOptions();
			}

			// Show Bank
			else if (opcode == 42) showBank();

				// Update Experience
			else if (opcode == 33) updateIndividualExperience();

				// Close Duel Dialog
			else if (opcode == 225) closeDuelDialog();

				// Confirm and Complete Trade
			else if (opcode == 20) confirmTrade();

				// Show Duel Items
			else if (opcode == 6) showDuelItems();

				// Toggle Duel Setting
			else if (opcode == 30) toggleDuelSetting();

				// Update Bank
			else if (opcode == 249) updateBank();

				// Update Inventory
			else if (opcode == 90) updateInventoryItem();

				// Experience Updates & Notification
			else if (opcode == 159) updateExperience();

				// Duel confirm / deny
			else if (opcode == 210) duelDecision();

				// Show Duel Confirm Dialog
			else if (opcode == 172) showDuelConfirmDialog();

				// Play Sound
			else if (opcode == 204) playSound();

				// Show Log In Dialog
			else if (opcode == 182) showLoginDialog();

				// Show Server Message Dialog
			else if (opcode == 222) showServerMessageDialog();

				// Show Sleep Screen
			else if (opcode == 117) showSleepScreen(length);

				// Not Sleeping
			else if (opcode == 84) {
				mc.setIsSleeping(false);
				if (isAndroid() && osConfig.F_SHOWING_KEYBOARD) {
					mc.clientPort.closeKeyboard();
				}
			}

				// Wrong Sleep Word
			else if (opcode == 194) mc.setSleepingStatusText("Incorrect - Please wait...");

				// System Update Timer
			else if (opcode == 52) mc.setSystemUpdate(packetsIncoming.getShort() * 32);

				// Elixir Timer
			else if (opcode == 54 && Config.S_WANT_EXPERIENCE_ELIXIRS)
				mc.setElixirTimer(packetsIncoming.getShort() * 32);

				// Sleeping Menu Fatigue
			else if (opcode == 244)
				mc.setFatigueSleeping(packetsIncoming.getShort());

				// Total Fatigue
			else if (opcode == 114)
				mc.setStatFatigue(packetsIncoming.getShort());

				// Kills2
			else if (opcode == 147)
				mc.setStatKills2(packetsIncoming.getShort());

			else if (opcode == 98)
				mc.setExpShared(packetsIncoming.getShort());

			else if (opcode == 140)
				mc.setPetFatigue(packetsIncoming.getShort());

				// Server Message Input (Second Style)
			else if (opcode == 89) showServerMessageDialogTwo();

				// Teleport Bubbles
			else if (opcode == 36) drawTeleportBubbles();

				// Duel Acceptance
			else if (opcode == 253) duelOpponentDecision();

				// Drop Item
			else if (opcode == 123) removeItem();

				// Open Duel Dialog
			else if (opcode == 176) beginDuelOptions();

				// Bank Dialog
			else if (opcode == 203) mc.setShowDialogBank(false);

				// Shop Dialog
			else if (opcode == 101) showShopDialog();

				// Trade Dialog Update
			else if (opcode == 97) updateTradeDialog();

				// Equipment Stats
			else if (opcode == 153) updateEquipmentStats();

				// Hide Options Menu
			else if (opcode == 252) mc.setOptionsMenuShow(false);

				// Draw Nearby Players
			else if (opcode == 234) drawNearbyPlayers();

				// Inside Tutorial
			else if (opcode == 111) mc.setInsideTutorial(packetsIncoming.getUnsignedByte() != 0);

				// Inside Black Hole
			else if (opcode == 115) mc.setInsideBlackHole(packetsIncoming.getUnsignedByte() != 0);

				// Draw Ground Items
			else if (opcode == 99) drawGroundItems(length);

				//toggle experience freeze
			else if (opcode == 34) mc.toggleExperienceFreeze(packetsIncoming.getByte());

			    //sync combat style with server (needed to compensate for network errors)
			else if (opcode == 129) gotCombatStylePacket();

			else mc.closeConnection(true);

		} catch (RuntimeException var17) {
			String var5 = "T2 - " + opcode + " - " + length + " rx:" + mc.getLocalPlayerX() + " ry:" + mc.getLocalPlayerZ()
				+ " num3l:" + mc.getGameObjectInstanceCount() + " - ";

			for (int var6 = 0; length > var6 && var6 < 50; ++var6) {
				var5 = var5 + packetsIncoming.getByte() + ",";
			}
			var17.printStackTrace();
			mc.closeConnection(true);
		}
	}

	private void createNPC() {
		int id = packetsIncoming.getShort();

		String name = packetsIncoming.readString();
		String description = packetsIncoming.readString();

		String optionCommand = "";
		int commandLength = packetsIncoming.getByte() & 0xff;
		if (commandLength > 0) {
			optionCommand = packetsIncoming.readString();
		}

		int attack = packetsIncoming.getByte() & 0xff;
		int strength = packetsIncoming.getByte() & 0xff;
		int defense = packetsIncoming.getByte() & 0xff;
		int hits = packetsIncoming.getByte() & 0xff;
		boolean attackable = packetsIncoming.getByte() == 1;

		int spriteCount = packetsIncoming.getByte() & 0xff;
		int[] sprites = new int[spriteCount];
		for (int c = 0; c < spriteCount; c++) {
			sprites[c] = packetsIncoming.get32();
		}
		for (int c = spriteCount; c < 12; c++)
			sprites[c] = 0;

		int hairColour = packetsIncoming.get32();
		int topColour = packetsIncoming.get32();
		int bottomColour = packetsIncoming.get32();
		int skinColour = packetsIncoming.get32();

		int camera1 = packetsIncoming.getShort();
		int camera2 = packetsIncoming.getShort();

		int walkModel = packetsIncoming.getByte() & 0xff;
		int combatModel = packetsIncoming.getByte() & 0xff;
		int combatSprite = packetsIncoming.getByte() & 0xff;
		com.openrsc.client.entityhandling.defs.NPCDef newNpc = new com.openrsc.client.entityhandling.defs.NPCDef(name, description, optionCommand, attack, strength, hits, defense,
			attackable, sprites, hairColour, topColour, bottomColour, skinColour, camera1, camera2,
			walkModel, combatModel, combatSprite, id);
		com.openrsc.client.entityhandling.EntityHandler.npcs.set(id, newNpc);
	}

	private void updateBatchProgression() {
		if (!Config.C_BATCH_PROGRESS_BAR) {
			mc.hideBatchProgressBar();
			return;
		}
		/* Progress bar */
		int actionType = packetsIncoming.getByte() & 0xff;
		if (actionType == 1) {
			int delay = packetsIncoming.getShort();
			int repeatFor = packetsIncoming.getByte() & 0xff;
			mc.initializeBatchProgressVariables(repeatFor, delay);
			mc.showBatchProgressBar();
		}
		else if (actionType == 2) {
			mc.resetBatchProgressBar();
		}
		else if (actionType == 3) {
			int repeat = packetsIncoming.getByte() & 0xff;
			mc.updateBatchProgressBar(repeat);
		}
	}

	private void displayBankPin() {
		int action = packetsIncoming.getByte();
		if (action == 1) {
			mc.showBankPinInterface();
		} else if (action == 0) {
			mc.hideBankPinInterface();
		}
	}

	private void setIronmanOptions() {
		int p = packetsIncoming.getByte();
		int iAction = packetsIncoming.getByte();
		if (iAction == 0) {
			mc.getIronmanInterface().setIronManMode(packetsIncoming.getByte());
			mc.getIronmanInterface().setIronManRestriction(packetsIncoming.getByte());
		} else if (iAction == 1) {
			mc.getIronmanInterface().setVisible(true);
		} else if (iAction == 2) {
			mc.getIronmanInterface().setVisible(false);
		}
	}

	private void updateAuctionHouse() {
		int packetType = packetsIncoming.getByte() & 0xff;
		if (packetType == 0) { // start receiving items
			mc.getAuctionHouse().resetAuctionItems();
		} else if (packetType == 1) { // read items
			int auctionItemCount = packetsIncoming.getShort();
			for (int i = 0; i < auctionItemCount; i++) {
				int auctionID = packetsIncoming.get32();
				int itemID = packetsIncoming.get32();
				int amount = packetsIncoming.get32();
				int price = packetsIncoming.get32();
				String seller = "";
				boolean isMyItem = packetsIncoming.getByte() == 1;
				if (isMyItem) {
					seller = mc.getLocalPlayer().displayName;
				} else {
					seller = packetsIncoming.readString();
				}
				int hoursLeft = packetsIncoming.getByte();
				mc.getAuctionHouse().addAuction(auctionID, itemID, amount, price, seller, hoursLeft);
			}
			mc.getAuctionHouse().setVisible(true);
		}
	}

	private void refreshOnlineList() {
		mc.getOnlineList().reset();
		int onlinePlayerCount = packetsIncoming.getShort();
		for (int i = 0; i < onlinePlayerCount; i++) {
			mc.getOnlineList().addOnlineUser(packetsIncoming.readString(), packetsIncoming.get32(), packetsIncoming.readString(), i == (onlinePlayerCount - 1));
		}
		mc.getOnlineList().setVisible(true);
	}

	private void fishingTrawlerUpdate() {
		int action = packetsIncoming.getByte();
		switch (action) {
			case 0:
				mc.getFishingTrawlerInterface().show();
				break;
			case 1:
				mc.getFishingTrawlerInterface().setVariables(packetsIncoming.getShort(), packetsIncoming.getShort(),
					packetsIncoming.getByte(), packetsIncoming.getByte() == 1);
				break;
			case 2:
				mc.getFishingTrawlerInterface().hide();
				break;
		}
	}

	private void updateClan() {
		int actionType = packetsIncoming.getByte();
		//Clan clan = new Clan(mc);
		switch (actionType) {
			case 0: // Send clan
				mc.clan.setClanName(packetsIncoming.readString());
				mc.clan.setClanTag(packetsIncoming.readString());
				mc.clan.setClanLeaderUsername(packetsIncoming.readString());
				boolean isLeader = packetsIncoming.getByte() == 1;
				mc.clan.setClanLeader(isLeader);
				SocialLists.clanListCount = packetsIncoming.getByte();
				for (int id = 0; id < SocialLists.clanListCount; id++) {
					mc.clan.username[id] = packetsIncoming.readString();
					mc.clan.clanRank[id] = packetsIncoming.getByte();
					mc.clan.onlineClanMember[id] = packetsIncoming.getByte();
				}
				mc.clan.putClan(true);

				break;
			case 1: // Leave clan
				mc.clan.putClan(false);
				mc.clan.update();
				break;
			case 2: // Sent invitation
				mc.clan.getClanInterface().initializeInvite(packetsIncoming.readString(), packetsIncoming.readString());
				break;
			case 3: // Settings
				mc.clan.setClanSetting(0, packetsIncoming.getByte());
				mc.clan.setClanSetting(1, packetsIncoming.getByte());
				mc.clan.setClanSetting(2, packetsIncoming.getByte());
				mc.clan.allowed[0] = packetsIncoming.getByte() == 1;
				mc.clan.allowed[1] = packetsIncoming.getByte() == 1;
				break;
			case 4: // Clan search visual
				mc.clan.getClanInterface().resetClans();
				int clanCount = packetsIncoming.getShort();
				for (int i = 0; i < clanCount; i++) {
					int clanID = packetsIncoming.getShort();
					String clanName = packetsIncoming.readString();
					String clanTag = packetsIncoming.readString();
					int members = packetsIncoming.getByte();
					int canJoin = packetsIncoming.getByte();
					int clanPoints = packetsIncoming.get32();
					int clanRank = packetsIncoming.getShort();
					mc.clan.getClanInterface().addClan(clanID, clanName, clanTag, members, canJoin, clanPoints, clanRank);
				}
				break;
		}
	}

	private void updateParty() {
		int actionType = packetsIncoming.getByte();
		switch (actionType) {
			case 0: // Send party
				mc.party.setPartyLeaderUsername(packetsIncoming.readString());
				boolean isLeader = packetsIncoming.getByte() == 1;
				mc.party.setPartyLeader(isLeader);
				SocialLists.partyListCount = packetsIncoming.getByte();
				for (int id = 0; id < SocialLists.partyListCount; id++) {
					mc.party.username[id] = packetsIncoming.readString();
					mc.party.partyRank[id] = packetsIncoming.getByte();
					mc.party.onlinePartyMember[id] = packetsIncoming.getByte();
					mc.party.curHp[id] = packetsIncoming.getByte();
					mc.party.maxHp[id] = packetsIncoming.getByte();
					mc.party.cbLvl[id] = packetsIncoming.getByte();
					mc.party.skull[id] = packetsIncoming.getByte();
					mc.party.pMemD[id] = packetsIncoming.getByte();
					mc.party.shareLoot[id] = packetsIncoming.getByte();
					mc.party.partyMembersTotal[id] = packetsIncoming.getByte();
					mc.party.inCombat[id] = packetsIncoming.getByte();
					mc.party.shareExp[id] = packetsIncoming.getByte();
					mc.party.expShared[id] = packetsIncoming.getLong(0);
				}
				mc.party.putParty(true);
				mc.showPartyMenu();

				break;
			case 1: // Leave party
				mc.party.putParty(false);
				mc.party.update();
				mc.hidePartyMenu();
				break;
			case 2: // Sent invitation
				mc.party.getPartyInterface().initializeInvite(packetsIncoming.readString(), packetsIncoming.readString());
				break;
			case 3: // Settings
				mc.party.setPartySetting(0, packetsIncoming.getByte());
				mc.party.setPartySetting(1, packetsIncoming.getByte());
				mc.party.setPartySetting(2, packetsIncoming.getByte());
				mc.party.allowed[0] = packetsIncoming.getByte() == 1;
				mc.party.allowed[1] = packetsIncoming.getByte() == 1;
				break;
			case 4: // Party search visual
				mc.party.getPartyInterface().resetPartys();
				int partyCount = packetsIncoming.getShort();
				for (int i = 0; i < partyCount; i++) {
					int partyID = packetsIncoming.getShort();
					int members = packetsIncoming.getByte();
					int canJoin = packetsIncoming.getByte();
					int partyPoints = packetsIncoming.get32();
					int partyRank = packetsIncoming.getShort();
					mc.party.getPartyInterface().addParty(partyID, members, canJoin, partyPoints, partyRank);
				}
				break;
		}
	}

	private void announceKill() {
		if (!Config.S_WANT_KILL_FEED) return;
		String killed = packetsIncoming.readString();
		String killer = packetsIncoming.readString();
		int killType = packetsIncoming.get32();
		mudclient.killQueue.addKill(new KillAnnouncer(killer, killed, killType));
	}

	private void showMessage() {
		int crown = packetsIncoming.get32();
		MessageType type = MessageType.lookup(packetsIncoming.getUnsignedByte());
		int messageType = packetsIncoming.getUnsignedByte();
		String message = packetsIncoming.readString();
		String sender = null;
		String clan = null;
		String colour = null;
		if ((messageType & 1) != 0) {
			sender = packetsIncoming.readString();
			clan = packetsIncoming.readString();
		}
		if ((messageType & 2) != 0) {
			colour = packetsIncoming.readString();
		}

		// Why is clan being sent into former name?
		mc.showMessage(true, sender, message, type, crown, clan, colour);
	}

	private void sendConnectionMessage() {
		String currentName = packetsIncoming.readString();
		String formerName = packetsIncoming.readString();
		int onlineStatus = packetsIncoming.getUnsignedByte();
		boolean rename = (onlineStatus & 1) != 0;
		boolean online = (4 & onlineStatus) != 0;
		String world = null;
		if (online) {
			world = packetsIncoming.readString();
		}
		for (int i = 0; i < SocialLists.friendListCount; ++i) {
			if (!rename) {
				if (SocialLists.friendList[i].equals(currentName)) {
					if (SocialLists.friendListWorld[i] == null && online) {
						mc.showMessage(false, null, currentName + " has logged in",
							MessageType.FRIEND_STATUS, 0, null);
					}

					if (null != SocialLists.friendListWorld[i] && !online) {
						mc.showMessage(false, null, currentName + " has logged out",
							MessageType.FRIEND_STATUS, 0, null);
					}

					SocialLists.friendListOld[i] = formerName;
					SocialLists.friendListWorld[i] = world;
					SocialLists.friendListOnlineStatus[i] = onlineStatus;
					mc.sortOnlineFriendsList();
					return;
				}
			} else if (SocialLists.friendList[i].equals(formerName)) {
				if (SocialLists.friendListWorld[i] == null && online) {
					mc.showMessage(false, null, currentName + " has logged in",
						MessageType.FRIEND_STATUS, 0, null);
				}

				if (SocialLists.friendListWorld[i] != null && !online) {
					mc.showMessage(false, null, currentName + " has logged out",
						MessageType.FRIEND_STATUS, 0, null);
				}

				SocialLists.friendList[i] = currentName;
				SocialLists.friendListOld[i] = formerName;
				SocialLists.friendListWorld[i] = world;
				SocialLists.friendListOnlineStatus[i] = onlineStatus;
				mc.sortOnlineFriendsList();
				return;
			}
		}

		if (rename) {
			System.out.println("Error: friend display name change packet received, but old name \'" + formerName
				+ "\' is not on friend list");
			return;
		}

		SocialLists.friendList[SocialLists.friendListCount] = currentName;
		SocialLists.friendListOld[SocialLists.friendListCount] = formerName;
		SocialLists.friendListWorld[SocialLists.friendListCount] = world;
		SocialLists.friendListOnlineStatus[SocialLists.friendListCount] = onlineStatus;
		++SocialLists.friendListCount;
		mc.sortOnlineFriendsList();
	}

	private void ignoreListNameChangeFailure() {
		String arg0 = packetsIncoming.readString();
		String replace = packetsIncoming.readString();
		if (replace.length() == 0) {
			replace = arg0;
		}

		String arg1 = packetsIncoming.readString();
		String find = packetsIncoming.readString();
		if (find.length() == 0) {
			find = arg0;
		}

		boolean rename = packetsIncoming.getUnsignedByte() == 1;

		for (int j = 0; j < SocialLists.ignoreListCount; ++j) {
			if (rename) {
				if (SocialLists.ignoreList[j].equals(find)) {
					SocialLists.ignoreListArg0[j] = arg0;
					SocialLists.ignoreList[j] = replace;
					SocialLists.ignoreListArg1[j] = arg1;
					SocialLists.ignoreListOld[j] = find;
					return;
				}
			} else if (SocialLists.ignoreList[j].equals(replace)) {
				return;
			}
		}

		if (rename) {
			System.out.println("Error: ignore display name change packet received, but old name \'" + find
				+ "\' is not on ignore list");
			return;
		}

		SocialLists.ignoreListArg0[SocialLists.ignoreListCount] = arg0;
		SocialLists.ignoreList[SocialLists.ignoreListCount] = replace;
		SocialLists.ignoreListArg1[SocialLists.ignoreListCount] = arg1;
		SocialLists.ignoreListOld[SocialLists.ignoreListCount] = find;
		++SocialLists.ignoreListCount;
	}

	private void updateIgnoreList() {
		SocialLists.ignoreListCount = packetsIncoming.getUnsignedByte();

		for (int var4 = 0; var4 < SocialLists.ignoreListCount; ++var4) {
			SocialLists.ignoreListArg0[var4] = packetsIncoming.readString();
			SocialLists.ignoreList[var4] = packetsIncoming.readString();
			SocialLists.ignoreListArg1[var4] = packetsIncoming.readString();
			SocialLists.ignoreListOld[var4] = packetsIncoming.readString();
		}
	}

	private void updateChatBlockSettings() {
		mc.setBlockChat(packetsIncoming.getUnsignedByte());
		mc.setBlockPrivate(packetsIncoming.getUnsignedByte());
		mc.setBlockTrade(packetsIncoming.getUnsignedByte());
		mc.setBlockDuel(packetsIncoming.getUnsignedByte());
	}

	private void receivePrivateMessage() {
		String sender = packetsIncoming.readString();
		String formerName = packetsIncoming.readString();
		int icon = packetsIncoming.get32();
		String message = RSBufferUtils.getEncryptedString(packetsIncoming);
		mc.showMessage(true, sender, message, MessageType.PRIVATE_RECIEVE, icon, formerName);
	}

	private void sendPrivateMessage() {
		String var13 = packetsIncoming.readString();
		String var14 = RSBufferUtils.getEncryptedString(packetsIncoming);
		mc.showMessage(false, var13, var14, MessageType.PRIVATE_SEND, 0, var13);
	}

	private void setServerConfiguration() {
		Properties props = new Properties();
		String serverName, serverNameWelcome, welcomeText;
		int playerLevelLimit, spawnAuctionNpcs, spawnIronManNpcs;
		int showFloatingNametags, wantClans, wantKillFeed, fogToggle;
		int groundItemToggle, autoMessageSwitchToggle, batchProgression;
		int sideMenuToggle, inventoryCountToggle, zoomViewToggle;
		int menuCombatStyleToggle, fightmodeSelectorToggle, experienceCounterToggle;
		int experienceDropsToggle, itemsOnDeathMenu, showRoofToggle, wantHideIp, wantRemember;
		int wantGlobalChat, wantSkillMenus, wantQuestMenus, wantQuestStartedIndicator, maxWalkingSpeed;
		int wantExperienceElixirs, wantKeyboardShortcuts, wantMembers, displayLogoSprite;
		int wantCustomBanks, wantBankPins, wantBankNotes, wantCertDeposit, customFiremaking;
		int wantDropX, wantExpInfo, wantWoodcuttingGuild, wantFixedOverheadChat, wantPets, showUnidentifiedHerbNames;
		int wantDecanting, wantCertsToBank, wantCustomRankDisplay, wantRightClickBank, wantPlayerCommands;
		int getFPS, wantEmail, wantRegistrationLimit, allowResize, lenientContactDetails, wantFatigue, wantCustomSprites;
		int fishingSpotsDepletable, improvedItemObjectNames, wantRunecraft, wantCustomLandscape, wantEquipmentTab;
		int wantBankPresets, wantParties, miningRocksExtended, movePerFrame, wantLeftclickWebs, npcKillMessages;
		int wantCustomUI, wantGlobalFriend, characterCreationMode, skillingExpRate, wantHarvesting, hideLoginBox;
		int globalFriendChat, wantRightClickTrade, nothingReuseMe, wantExtendedCatsBehavior, wantCertAsNotes;

		String logoSpriteID;

		if (!mc.gotInitialConfigs) {
			serverName = this.getClientStream().readString(); // 1
			serverNameWelcome = this.getClientStream().readString(); // 2
			playerLevelLimit = this.getClientStream().getUnsignedByte(); // 3
			spawnAuctionNpcs = this.getClientStream().getUnsignedByte(); // 4
			spawnIronManNpcs = this.getClientStream().getUnsignedByte(); // 5
			showFloatingNametags = this.getClientStream().getUnsignedByte(); // 6
			wantClans = this.getClientStream().getUnsignedByte(); // 7
			wantKillFeed = this.getClientStream().getUnsignedByte(); // 8
			fogToggle = this.getClientStream().getUnsignedByte(); // 9
			groundItemToggle = this.getClientStream().getUnsignedByte(); // 10
			autoMessageSwitchToggle = this.getClientStream().getUnsignedByte(); // 11
			batchProgression = this.getClientStream().getUnsignedByte(); // 12
			sideMenuToggle = this.getClientStream().getUnsignedByte(); // 13
			inventoryCountToggle = this.getClientStream().getUnsignedByte(); // 14
			zoomViewToggle = this.getClientStream().getUnsignedByte(); // 15
			menuCombatStyleToggle = this.getClientStream().getUnsignedByte(); // 16
			fightmodeSelectorToggle = this.getClientStream().getUnsignedByte(); // 17
			experienceCounterToggle = this.getClientStream().getUnsignedByte(); // 18
			experienceDropsToggle = this.getClientStream().getUnsignedByte(); // 19
			itemsOnDeathMenu = this.getClientStream().getUnsignedByte(); // 20
			showRoofToggle = this.getClientStream().getUnsignedByte(); // 21
			wantHideIp = this.getClientStream().getUnsignedByte(); // 22
			wantRemember = this.getClientStream().getUnsignedByte(); // 23
			wantGlobalChat = this.getClientStream().getUnsignedByte(); // 24
			wantSkillMenus = this.getClientStream().getUnsignedByte(); // 25
			wantQuestMenus = this.getClientStream().getUnsignedByte(); // 26
			wantExperienceElixirs = this.getClientStream().getUnsignedByte(); // 27
			wantKeyboardShortcuts = this.getClientStream().getUnsignedByte(); // 28
			wantCustomBanks = this.getClientStream().getUnsignedByte(); // 29
			wantBankPins = this.getClientStream().getUnsignedByte(); // 30
			wantBankNotes = this.getClientStream().getUnsignedByte(); // 31
			wantCertDeposit = this.getClientStream().getUnsignedByte(); // 32
			customFiremaking = this.getClientStream().getUnsignedByte(); // 33
			wantDropX = this.getClientStream().getUnsignedByte(); // 34
			wantExpInfo = this.getClientStream().getUnsignedByte(); // 35
			wantWoodcuttingGuild = this.getClientStream().getUnsignedByte(); // 36
			wantDecanting = this.getClientStream().getUnsignedByte(); // 37
			wantCertsToBank = this.getClientStream().getUnsignedByte(); // 38
			wantCustomRankDisplay = this.getClientStream().getUnsignedByte(); // 39
			wantRightClickBank = this.getClientStream().getUnsignedByte(); // 40
			wantFixedOverheadChat = this.getClientStream().getUnsignedByte(); // 41
			welcomeText = this.getClientStream().readString(); // 42
			wantMembers = this.getClientStream().getUnsignedByte(); // 43
			displayLogoSprite = this.getClientStream().getUnsignedByte(); // 44
			logoSpriteID = this.getClientStream().readString(); // 45
			getFPS = this.getClientStream().getUnsignedByte(); // 46
			wantEmail = this.getClientStream().getUnsignedByte(); // 47
			wantRegistrationLimit = this.getClientStream().getUnsignedByte(); // 48
			allowResize = this.getClientStream().getUnsignedByte(); // 49
			lenientContactDetails = this.getClientStream().getUnsignedByte(); // 50
			wantFatigue = this.getClientStream().getUnsignedByte(); // 51
			wantCustomSprites = this.getClientStream().getUnsignedByte(); // 52
			wantPlayerCommands = this.getClientStream().getUnsignedByte(); // 53
			wantPets = this.getClientStream().getUnsignedByte(); // 54
			maxWalkingSpeed = this.getClientStream().getUnsignedByte(); // 55
			showUnidentifiedHerbNames = this.getClientStream().getUnsignedByte(); // 56
			wantQuestStartedIndicator = this.getClientStream().getUnsignedByte(); // 57
			fishingSpotsDepletable = this.getClientStream().getUnsignedByte(); //58
			improvedItemObjectNames = this.getClientStream().getUnsignedByte(); //59
			wantRunecraft = this.getClientStream().getUnsignedByte(); //60
			wantCustomLandscape = this.getClientStream().getUnsignedByte(); //61
			wantEquipmentTab = this.getClientStream().getUnsignedByte(); //62
			wantBankPresets = this.getClientStream().getUnsignedByte(); //63
			wantParties = this.getClientStream().getUnsignedByte(); // 64
			miningRocksExtended = this.getClientStream().getUnsignedByte(); //65
			movePerFrame = this.getClientStream().getByte(); //66
			wantLeftclickWebs = this.getClientStream().getByte(); //67
			npcKillMessages = this.getClientStream().getByte(); //68
			wantCustomUI = this.getClientStream().getUnsignedByte(); //69
			wantGlobalFriend = this.getClientStream().getUnsignedByte(); //70
			characterCreationMode = this.getClientStream().getUnsignedByte(); //71
			skillingExpRate = this.getClientStream().getUnsignedByte(); //72
			wantHarvesting = this.getClientStream().getUnsignedByte(); //73
			hideLoginBox = this.getClientStream().getUnsignedByte(); // 74
			globalFriendChat = this.getClientStream().getUnsignedByte(); // 75
			wantRightClickTrade = this.getClientStream().getUnsignedByte(); // 76
			nothingReuseMe = this.getClientStream().getUnsignedByte(); // 77
			wantExtendedCatsBehavior = this.getClientStream().getUnsignedByte(); // 78
			wantCertAsNotes = this.getClientStream().getUnsignedByte(); // 79
		} else {
			serverName = packetsIncoming.readString(); // 1
			serverNameWelcome = packetsIncoming.readString(); // 2
			playerLevelLimit = packetsIncoming.getUnsignedByte(); // 3
			spawnAuctionNpcs = packetsIncoming.getUnsignedByte(); // 4
			spawnIronManNpcs = packetsIncoming.getUnsignedByte(); // 5
			showFloatingNametags = packetsIncoming.getUnsignedByte(); // 6
			wantClans = packetsIncoming.getUnsignedByte(); // 7
			wantKillFeed = packetsIncoming.getUnsignedByte(); // 8
			fogToggle = packetsIncoming.getUnsignedByte(); // 9
			groundItemToggle = packetsIncoming.getUnsignedByte(); // 10
			autoMessageSwitchToggle = packetsIncoming.getUnsignedByte(); // 11
			batchProgression = packetsIncoming.getUnsignedByte(); // 12
			sideMenuToggle = packetsIncoming.getUnsignedByte(); // 13
			inventoryCountToggle = packetsIncoming.getUnsignedByte(); // 14
			zoomViewToggle = packetsIncoming.getUnsignedByte(); // 15
			menuCombatStyleToggle = packetsIncoming.getUnsignedByte(); // 16
			fightmodeSelectorToggle = packetsIncoming.getUnsignedByte(); // 17
			experienceCounterToggle = packetsIncoming.getUnsignedByte(); // 18
			experienceDropsToggle = packetsIncoming.getUnsignedByte(); // 19
			itemsOnDeathMenu = packetsIncoming.getUnsignedByte(); // 20
			showRoofToggle = packetsIncoming.getUnsignedByte(); // 21
			wantHideIp = packetsIncoming.getUnsignedByte(); // 22
			wantRemember = packetsIncoming.getUnsignedByte(); // 23
			wantGlobalChat = packetsIncoming.getUnsignedByte(); // 24
			wantSkillMenus = packetsIncoming.getUnsignedByte(); // 25
			wantQuestMenus = packetsIncoming.getUnsignedByte(); // 26
			wantExperienceElixirs = packetsIncoming.getUnsignedByte(); // 27
			wantKeyboardShortcuts = packetsIncoming.getUnsignedByte(); // 28
			wantCustomBanks = packetsIncoming.getUnsignedByte(); // 29
			wantBankPins = packetsIncoming.getUnsignedByte(); // 30
			wantBankNotes = packetsIncoming.getUnsignedByte(); // 31
			wantCertDeposit = packetsIncoming.getUnsignedByte(); // 32
			customFiremaking = packetsIncoming.getUnsignedByte(); // 33
			wantDropX = packetsIncoming.getUnsignedByte(); // 34
			wantExpInfo = packetsIncoming.getUnsignedByte(); // 35
			wantWoodcuttingGuild = packetsIncoming.getUnsignedByte(); // 36
			wantDecanting = packetsIncoming.getUnsignedByte(); // 37
			wantCertsToBank = packetsIncoming.getUnsignedByte(); // 38
			wantCustomRankDisplay = packetsIncoming.getUnsignedByte(); // 39
			wantRightClickBank = packetsIncoming.getUnsignedByte(); // 40
			wantFixedOverheadChat = packetsIncoming.getUnsignedByte(); // 41
			welcomeText = packetsIncoming.readString(); // 42
			wantMembers = packetsIncoming.getUnsignedByte(); // 43
			displayLogoSprite = packetsIncoming.getUnsignedByte(); // 44
			logoSpriteID = packetsIncoming.readString(); // 45
			getFPS = packetsIncoming.getUnsignedByte(); // 46
			wantEmail = packetsIncoming.getUnsignedByte(); // 47
			wantRegistrationLimit = packetsIncoming.getUnsignedByte(); // 48
			allowResize = packetsIncoming.getUnsignedByte(); // 49
			lenientContactDetails = packetsIncoming.getUnsignedByte(); // 50
			wantFatigue = packetsIncoming.getUnsignedByte(); // 51
			wantCustomSprites = packetsIncoming.getUnsignedByte(); // 52
			wantPlayerCommands = packetsIncoming.getUnsignedByte(); // 53
			wantPets = packetsIncoming.getUnsignedByte(); // 54
			maxWalkingSpeed = packetsIncoming.getUnsignedByte(); // 55
			showUnidentifiedHerbNames = packetsIncoming.getUnsignedByte(); // 56
			wantQuestStartedIndicator = packetsIncoming.getUnsignedByte(); // 57
			fishingSpotsDepletable = packetsIncoming.getUnsignedByte(); //58
			improvedItemObjectNames = packetsIncoming.getUnsignedByte(); //59
			wantRunecraft = packetsIncoming.getUnsignedByte(); //60
			wantCustomLandscape = packetsIncoming.getUnsignedByte(); //61
			wantEquipmentTab = packetsIncoming.getUnsignedByte(); //62
			wantBankPresets = packetsIncoming.getUnsignedByte(); //63
			wantParties = packetsIncoming.getUnsignedByte(); // 64
			miningRocksExtended = packetsIncoming.getUnsignedByte(); //65
			movePerFrame = packetsIncoming.getByte(); //66
			wantLeftclickWebs = packetsIncoming.getByte(); //67
			npcKillMessages = packetsIncoming.getByte(); //68
			wantCustomUI = packetsIncoming.getUnsignedByte(); //69
			wantGlobalFriend = packetsIncoming.getUnsignedByte(); //70
			characterCreationMode = packetsIncoming.getUnsignedByte(); //71
			skillingExpRate = packetsIncoming.getUnsignedByte(); //72
			wantHarvesting = packetsIncoming.getUnsignedByte(); //73
			hideLoginBox = packetsIncoming.getUnsignedByte(); // 74
			globalFriendChat = packetsIncoming.getUnsignedByte(); // 75
			wantRightClickTrade = packetsIncoming.getUnsignedByte(); // 76
			nothingReuseMe = packetsIncoming.getUnsignedByte(); // 77
			wantExtendedCatsBehavior = packetsIncoming.getUnsignedByte(); // 78
			wantCertAsNotes = packetsIncoming.getUnsignedByte(); // 79
		}

		if (Config.DEBUG) {
			System.out.println(
				"SERVER_NAME " + serverName + // 1
					"\nSERVER_NAME_WELCOME " + serverNameWelcome + // 2
					"\nS_PLAYER_LEVEL_LIMIT " + playerLevelLimit + // 3
					"\nS_SPAWN_AUCTION_NPCS " + spawnAuctionNpcs + // 4
					"\nS_SPAWN_IRON_MAN_NPCS " + spawnIronManNpcs + // 5
					"\nS_SHOW_FLOATING_NAMETAGS " + showFloatingNametags + // 6
					"\nS_WANT_CLANS " + wantClans + // 7
					"\nS_WANT_KILL_FEED " + wantKillFeed + // 8
					"\nS_FOG_TOGGLE " + fogToggle + // 9
					"\nS_GROUND_ITEM_TOGGLE " + groundItemToggle + // 10
					"\nS_AUTO_MESSAGE_SWITCH_TOGGLE " + autoMessageSwitchToggle + // 11
					"\nS_BATCH_PROGRESSION " + batchProgression + // 12
					"\nS_SIDE_MENU_TOGGLE " + sideMenuToggle + // 13
					"\nS_INVENTORY_COUNT_TOGGLE " + inventoryCountToggle + // 14
					"\nS_ZOOM_VIEW_TOGGLE " + zoomViewToggle + // 15
					"\nS_MENU_COMBAT_STYLE_TOGGLE " + menuCombatStyleToggle + // 16
					"\nS_FIGHTMODE_SELECTOR_TOGGLE " + fightmodeSelectorToggle + // 17
					"\nS_EXPERIENCE_COUNTER_TOGGLE " + experienceCounterToggle + // 18
					"\nS_EXPERIENCE_DROPS_TOGGLE " + experienceDropsToggle + // 19
					"\nS_ITEMS_ON_DEATH_MENU " + itemsOnDeathMenu + // 20
					"\nS_SHOW_ROOF_TOGGLE " + showRoofToggle + // 21
					"\nS_WANT_HIDE_IP " + wantHideIp + // 22
					"\nS_WANT_REMEMBER " + wantRemember + // 23
					"\nS_WANT_GLOBAL_CHAT " + wantGlobalChat + // 24
					"\nS_WANT_SKILL_MENUS " + wantSkillMenus + // 25
					"\nS_WANT_QUEST_MENUS " + wantQuestMenus + // 26
					"\nS_WANT_EXPERIENCE_ELIXIRS " + wantExperienceElixirs + // 27
					"\nS_WANT_KEYBOARD_SHORTCUTS " + wantKeyboardShortcuts + // 28
					"\nS_WANT_CUSTOM_BANKS " + wantCustomBanks + // 29
					"\nS_WANT_BANK_PINS " + wantBankPins + // 30
					"\nS_WANT_BANK_NOTES " + wantBankNotes + // 31
					"\nS_WANT_CERT_DEPOSIT " + wantCertDeposit + // 32
					"\nS_CUSTOM_FIREMAKING " + customFiremaking + // 33
					"\nS_WANT_DROP_X " + wantDropX + // 34
					"\nS_WANT_EXP_INFO " + wantExpInfo + // 35
					"\nS_WANT_WOODCUTTING_GUILD " + wantWoodcuttingGuild + // 36
					"\nS_WANT_DECANTING " + wantDecanting + // 37
					"\nS_WANT_CERTS_TO_BANK " + wantCertsToBank + // 38
					"\nS_WANT_CUSTOM_RANK_DISPLAY " + wantCustomRankDisplay + // 39
					"\nS_RIGHT_CLICK_BANK " + wantRightClickBank + // 40
					"\nS_WANT_FIXED_OVERHEAD_CHAT " + wantFixedOverheadChat + // 41
					"\nWELCOME_TEXT " + welcomeText + // 42
					"\nMEMBERS_FEATURES " + wantMembers + // 43
					"\nDISPLAY_LOGO_SPRITE " + displayLogoSprite + // 44
					"\nC_LOGO_SPRITE_ID " + logoSpriteID + // 45
					"\nC_FPS " + getFPS + // 46
					"\nC_WANT_EMAIL " + wantEmail + // 47
					"\nS_WANT_REGISTRATION_LIMIT " + wantRegistrationLimit + // 48
					"\nS_ALLOW_RESIZE " + allowResize + // 49
					"\nS_LENIENT_CONTACT_DETAILS " + lenientContactDetails + // 50
					"\nS_WANT_FATIGUE " + wantFatigue + // 51
					"\nS_WANT_CUSTOM_SPRITES " + wantCustomSprites + // 52
					"\nS_WANT_PLAYER_COMMANDS " + wantPlayerCommands + // 53
					"\nS_WANT_PETS " + wantPets + // 54
					"\nS_MAX_RUNNING_SPEED " + maxWalkingSpeed + //55
					"\nS_SHOW_UNIDENTIFIED_HERB_NAMES " + showUnidentifiedHerbNames + // 56
					"\nS_WANT_QUEST_STARTED_INDICATOR " + wantQuestStartedIndicator + // 57
					"\nS_FISHING_SPOTS_DEPLETABLE " + fishingSpotsDepletable + // 58
					"\nS_IMPROVED_ITEM_OBJECT_NAMES " + improvedItemObjectNames + // 59
					"\nS_WANT_RUNECRAFT " + wantRunecraft + // 60
					"\nS_WANT_CUSTOM_LANDSCAPE " + wantCustomLandscape + // 61
					"\nS_WANT_EQUIPMENT_TAB " + wantEquipmentTab + // 62
					"\nS_WANT_BANK_PRESETS " + wantEquipmentTab + // 63
					"\nS_WANT_PARTIES " + wantClans + // 64
					"\nS_MINING_ROCKS_EXTENDED " + miningRocksExtended + // 65
					"\nC_MOVE_PER_FRAME " + movePerFrame + // 66
					"\nS_WANT_LEFTCLICK_WEBS " + wantLeftclickWebs + // 67
					"\nS_NPC_KILL_MESSAGES " + npcKillMessages + // 68
					"\nS_WANT_CUSTOM_UI " + wantCustomUI + // 69
					"\nS_WANT_GLOBAL_FRIEND" + wantGlobalFriend + // 70
					"\nS_CHARACTER_CREATION_MODE" + characterCreationMode + // 71
					"\nS_SKILLING_EXP_RATE" + skillingExpRate + //72
					"\nS_WANT_HARVESTING " + wantHarvesting + // 74
					"\nS_HIDE_LOGIN_BOX " + hideLoginBox + // 75
					"\nS_WANT_GLOBAL_FRIEND" + globalFriendChat + // 76
					"\nS_RIGHT_CLICK_TRADE " + wantRightClickTrade + // 77
					"\nS_NOTHING_REUSE_ME " + nothingReuseMe + // 78
					"\nS_WANT_EXTENDED_CATS_BEHAVIOR " + wantExtendedCatsBehavior + // 79
					"\nS_WANT_CERT_AS_NOTES " + wantCertAsNotes // 80
			);
		}

		if (!mc.gotInitialConfigs || Config.isAndroid()) {
			props.setProperty("SERVER_IP", ClientPort.loadIP()); // 0
			props.setProperty("SERVER_PORT", String.valueOf(ClientPort.loadPort())); // 0
		}
		props.setProperty("SERVER_NAME", serverName); // 1
		props.setProperty("SERVER_NAME_WELCOME", serverNameWelcome); // 2
		props.setProperty("S_PLAYER_LEVEL_LIMIT", Integer.toString(playerLevelLimit)); // 3
		props.setProperty("S_SPAWN_AUCTION_NPCS", spawnAuctionNpcs == 1 ? "true" : "false"); // 4
		props.setProperty("S_SPAWN_IRON_MAN_NPCS", spawnIronManNpcs == 1 ? "true" : "false"); // 5
		props.setProperty("S_SHOW_FLOATING_NAMETAGS", showFloatingNametags == 1 ? "true" : "false"); // 6
		props.setProperty("S_WANT_CLANS", wantClans == 1 ? "true" : "false"); // 7
		props.setProperty("S_WANT_KILL_FEED", wantKillFeed == 1 ? "true" : "false"); // 8
		props.setProperty("S_FOG_TOGGLE", fogToggle == 1 ? "true" : "false"); // 9
		props.setProperty("S_GROUND_ITEM_TOGGLE", groundItemToggle == 1 ? "true" : "false"); // 10
		props.setProperty("S_AUTO_MESSAGE_SWITCH_TOGGLE", autoMessageSwitchToggle == 1 ? "true" : "false"); // 11
		props.setProperty("S_BATCH_PROGRESSION", batchProgression == 1 ? "true" : "false"); // 12
		props.setProperty("S_SIDE_MENU_TOGGLE", sideMenuToggle == 1 ? "true" : "false"); // 13
		props.setProperty("S_INVENTORY_COUNT_TOGGLE", inventoryCountToggle == 1 ? "true" : "false"); // 14
		props.setProperty("S_ZOOM_VIEW_TOGGLE", zoomViewToggle == 1 ? "true" : "false"); // 15
		props.setProperty("S_MENU_COMBAT_STYLE_TOGGLE", menuCombatStyleToggle == 1 ? "true" : "false"); // 16
		props.setProperty("S_FIGHTMODE_SELECTOR_TOGGLE", fightmodeSelectorToggle == 1 ? "true" : "false"); // 17
		props.setProperty("S_EXPERIENCE_COUNTER_TOGGLE", experienceCounterToggle == 1 ? "true" : "false"); // 18
		props.setProperty("S_EXPERIENCE_DROPS_TOGGLE", experienceDropsToggle == 1 ? "true" : "false"); // 19
		props.setProperty("S_ITEMS_ON_DEATH_MENU", itemsOnDeathMenu == 1 ? "true" : "false"); // 20
		props.setProperty("S_SHOW_ROOF_TOGGLE", showRoofToggle == 1 ? "true" : "false"); // 21
		props.setProperty("S_WANT_HIDE_IP", wantHideIp == 1 ? "true" : "false"); // 22
		props.setProperty("S_WANT_REMEMBER", wantRemember == 1 ? "true" : "false"); // 23
		props.setProperty("S_WANT_GLOBAL_CHAT", wantGlobalChat == 1 ? "true" : "false"); // 24
		props.setProperty("S_WANT_SKILL_MENUS", wantSkillMenus == 1 ? "true" : "false"); // 25
		props.setProperty("S_WANT_QUEST_MENUS", wantQuestMenus == 1 ? "true" : "false"); // 26
		props.setProperty("S_WANT_EXPERIENCE_ELIXIRS", wantExperienceElixirs == 1 ? "true" : "false"); // 27
		props.setProperty("S_WANT_KEYBOARD_SHORTCUTS", Integer.toString(wantKeyboardShortcuts)); // 28
		props.setProperty("S_WANT_CUSTOM_BANKS", wantCustomBanks == 1 ? "true" : "false"); // 29
		props.setProperty("S_WANT_BANK_PINS", wantBankPins == 1 ? "true" : "false"); // 30
		props.setProperty("S_WANT_BANK_NOTES", wantBankNotes == 1 ? "true" : "false"); // 31
		props.setProperty("S_WANT_CERT_DEPOSIT", wantCertDeposit == 1 ? "true" : "false"); // 32
		props.setProperty("S_CUSTOM_FIREMAKING", customFiremaking == 1 ? "true" : "false"); // 33
		props.setProperty("S_WANT_DROP_X", wantDropX == 1 ? "true" : "false"); // 34
		props.setProperty("S_WANT_EXP_INFO", wantExpInfo == 1 ? "true" : "false"); // 35
		props.setProperty("S_WANT_WOODCUTTING_GUILD", wantWoodcuttingGuild == 1 ? "true" : "false"); // 36
		props.setProperty("S_WANT_DECANTING", wantDecanting == 1 ? "true" : "false"); // 37
		props.setProperty("S_WANT_CERTS_TO_BANK", wantCertsToBank == 1 ? "true" : "false"); // 38
		props.setProperty("S_WANT_CUSTOM_RANK_DISPLAY", wantCustomRankDisplay == 1 ? "true" : "false"); // 39
		props.setProperty("S_RIGHT_CLICK_BANK", wantRightClickBank == 1 ? "true" : "false"); // 40
		props.setProperty("S_WANT_FIXED_OVERHEAD_CHAT", wantFixedOverheadChat == 1 ? "true" : "false"); // 41
		props.setProperty("WELCOME_TEXT", welcomeText); // 42
		props.setProperty("MEMBER_WORLD", wantMembers == 1 ? "true" : "false"); // 43
		props.setProperty("DISPLAY_LOGO_SPRITE", displayLogoSprite == 1 ? "true" : "false"); // 44
		props.setProperty("C_LOGO_SPRITE_ID", logoSpriteID); // 45
		props.setProperty("C_FPS", Integer.toString(getFPS)); // 46
		props.setProperty("C_WANT_EMAIL", wantEmail == 1 ? "true" : "false"); // 47
		props.setProperty("S_WANT_REGISTRATION_LIMIT", wantRegistrationLimit == 1 ? "true" : "false"); // 48
		props.setProperty("S_ALLOW_RESIZE", allowResize == 1 ? "true" : "false"); // 49
		props.setProperty("S_LENIENT_CONTACT_DETAILS", lenientContactDetails == 1 ? "true" : "false"); // 50
		props.setProperty("S_WANT_FATIGUE", wantFatigue == 1 ? "true" : "false"); // 51
		props.setProperty("S_WANT_CUSTOM_SPRITES", wantCustomSprites == 1 ? "true" : "false"); // 52
		props.setProperty("S_WANT_PLAYER_COMMANDS", wantPlayerCommands == 1 ? "true" : "false"); // 53
		props.setProperty("S_WANT_PETS", wantPets == 1 ? "true" : "false"); // 54
		props.setProperty("S_MAX_WALKING_SPEED", Integer.toString(maxWalkingSpeed)); // 55
		props.setProperty("S_SHOW_UNIDENTIFIED_HERB_NAMES", showUnidentifiedHerbNames == 1 ? "true" : "false"); // 56
		props.setProperty("S_WANT_QUEST_STARTED_INDICATOR", wantQuestStartedIndicator == 1 ? "true" : "false"); // 57
		props.setProperty("S_FISHING_SPOTS_DEPLETABLE", fishingSpotsDepletable == 1 ? "true" : "false"); //58
		props.setProperty("S_IMPROVED_ITEM_OBJECT_NAMES", improvedItemObjectNames == 1 ? "true" : "false"); //59
		props.setProperty("S_WANT_RUNECRAFT", wantRunecraft == 1 ? "true" : "false"); //60
		props.setProperty("S_WANT_CUSTOM_LANDSCAPE", wantCustomLandscape == 1 ? "true" : "false"); //61
		props.setProperty("S_WANT_EQUIPMENT_TAB", wantEquipmentTab == 1 ? "true" : "false"); //62
		props.setProperty("S_WANT_BANK_PRESETS", wantBankPresets == 1 ? "true" : "false"); //63
		props.setProperty("S_WANT_PARTIES", wantParties == 1 ? "true" : "false"); //64
		props.setProperty("S_MINING_ROCKS_EXTENDED", miningRocksExtended == 1 ? "true" : "false"); //65
		props.setProperty("C_MOVE_PER_FRAME", String.valueOf(movePerFrame)); //66
		props.setProperty("S_WANT_LEFTCLICK_WEBS", wantLeftclickWebs == 1 ? "true" : "false"); //67
		props.setProperty("S_NPC_KILL_MESSAGES", npcKillMessages == 1 ? "true" : "false"); //68
		props.setProperty("S_WANT_CUSTOM_UI", wantCustomUI == 1 ? "true" : "false"); //69
		props.setProperty("S_WANT_GLOBAL_FRIEND", wantGlobalFriend == 1 ? "true" : "false"); //70
		props.setProperty("S_CHARACTER_CREATION_MODE", Integer.toString(characterCreationMode)); //71
		props.setProperty("S_SKILLING_EXP_RATE", Integer.toString(skillingExpRate)); //72
		props.setProperty("S_WANT_HARVESTING", wantHarvesting == 1 ? "true" : "false"); // 74
		props.setProperty("S_HIDE_LOGIN_BOX", hideLoginBox == 1 ? "true" : "false"); // 75
		props.setProperty("S_WANT_GLOBAL_FRIEND", globalFriendChat == 1 ? "true" : "false"); // 76
		props.setProperty("S_RIGHT_CLICK_TRADE", wantRightClickTrade == 1 ? "true" : "false"); // 77
		props.setProperty("S_NOTHING_REUSE_ME", nothingReuseMe == 1 ? "true" : "false"); // 78
		props.setProperty("S_WANT_EXTENDED_CATS_BEHAVIOR", wantExtendedCatsBehavior == 1 ? "true" : "false"); // 79
		props.setProperty("S_WANT_CERT_AS_NOTES", wantCertAsNotes == 1 ? "true" : "false"); // 80
		Config.updateServerConfiguration(props);

		mc.authenticSettings = !(
			Config.isAndroid() ||
				Config.S_WANT_CLANS || Config.S_WANT_KILL_FEED
				|| Config.S_FOG_TOGGLE || Config.S_GROUND_ITEM_TOGGLE
				|| Config.S_AUTO_MESSAGE_SWITCH_TOGGLE || Config.S_BATCH_PROGRESSION
				|| Config.S_SIDE_MENU_TOGGLE || Config.S_INVENTORY_COUNT_TOGGLE
				|| Config.S_MENU_COMBAT_STYLE_TOGGLE
				|| Config.S_FIGHTMODE_SELECTOR_TOGGLE || Config.S_SHOW_ROOF_TOGGLE
				|| Config.S_EXPERIENCE_COUNTER_TOGGLE || Config.S_WANT_GLOBAL_CHAT
				|| Config.S_EXPERIENCE_DROPS_TOGGLE || Config.S_ITEMS_ON_DEATH_MENU
				|| Config.S_HIDE_LOGIN_BOX || Config.S_WANT_GLOBAL_FRIEND);


		if (!mc.gotInitialConfigs) {
			mc.setExperienceArray(new int[Config.S_PLAYER_LEVEL_LIMIT]);
			mc.setExperienceArray();
			mc.gotInitialConfigs = true;
			mc.continueStartGame((byte) -92);
		}
	}

	private void showOtherPlayers(int length) {
		mc.setKnownPlayerCount(mc.getPlayerCount());

		for (int i = 0; mc.getKnownPlayerCount() > i; ++i) {
			mc.setKnownPlayer(i, mc.getPlayer(i));
		}

		packetsIncoming.startBitAccess();

		mc.setLocalPlayerX(packetsIncoming.getBitMask(11));
		mc.setLocalPlayerZ(packetsIncoming.getBitMask(13));

		int direction = packetsIncoming.getBitMask(4);
		boolean needNextRegion = mc.loadNextRegion(mc.getLocalPlayerZ(), mc.getLocalPlayerX(), false);
		mc.setLocalPlayerX(mc.getLocalPlayerX() - mc.getMidRegionBaseX());
		mc.setLocalPlayerZ(mc.getLocalPlayerZ() - mc.getMidRegionBaseZ());

		int tileSize = mc.getTileSize();
		int currentX = mc.getLocalPlayerX() * tileSize + 64;
		int currentZ = mc.getLocalPlayerZ() * tileSize + 64;
		mc.setPlayerCount(0);
		if (needNextRegion) {
			mc.getLocalPlayer().waypointIndexNext = 0;
			mc.getLocalPlayer().waypointIndexCurrent = 0;
			mc.getLocalPlayer().currentX = mc.getLocalPlayer().waypointsX[0] = currentX;
			mc.getLocalPlayer().currentZ = mc.getLocalPlayer().waypointsZ[0] = currentZ;
		}

		mc.setLocalPlayer(
			mc.createPlayer(currentZ, mc.getLocalPlayerServerIndex(), currentX, 1,
				ORSCharacterDirection.lookup(direction)
			)
		);

		int dir = packetsIncoming.getBitMask(8);

		for (int var9 = 0; dir > var9; ++var9) {
			ORSCharacter playerToShow = mc.getKnownPlayer(var9 + 1);
			int needsUpdate = packetsIncoming.getBitMask(1);
			if (needsUpdate != 0) {
				int updateType = packetsIncoming.getBitMask(1);
				if (updateType != 0) {
					int needsNextSprite = packetsIncoming.getBitMask(2);
					if (needsNextSprite == 3) {
						continue;
					}
					playerToShow.animationNext = packetsIncoming.getBitMask(2) + (needsNextSprite << 2);
				} else {
					int modelIndex = packetsIncoming.getBitMask(3);
					int var33 = playerToShow.waypointIndexCurrent;
					int var15 = playerToShow.waypointsX[var33];
					int var16 = playerToShow.waypointsZ[var33];
					if (modelIndex == 2 || modelIndex == 1 || modelIndex == 3) {
						var15 += tileSize;
					}

					if (modelIndex == 6 || modelIndex == 5 || modelIndex == 7) {
						var15 -= tileSize;
					}

					if (modelIndex == 4 || modelIndex == 3 || modelIndex == 5) {
						var16 += tileSize;
					}
					playerToShow.animationNext = modelIndex;
					if (modelIndex == 0 || modelIndex == 1 || modelIndex == 7) {
						var16 -= tileSize;
					}
					playerToShow.waypointIndexCurrent = var33 = (1 + var33) % 10;
					playerToShow.waypointsX[var33] = var15;
					playerToShow.waypointsZ[var33] = var16;
				}
			}

			mc.setPlayer(mc.getPlayerCount(), playerToShow);
			mc.setPlayerCount(mc.getPlayerCount() + 1);
		}

		while (length * 8 > packetsIncoming.getBitHead() + 24) {
			int var9 = packetsIncoming.getBitMask(11);
			int var10 = packetsIncoming.getBitMask(6);
			if (var10 > 31) {
				var10 -= 64;
			}

			int var11 = packetsIncoming.getBitMask(6);
			if (var11 > 31) {
				var11 -= 64;
			}

			direction = packetsIncoming.getBitMask(4);
			currentZ = (mc.getLocalPlayerZ() + var11) * tileSize + 64;
			currentX = (mc.getLocalPlayerX() + var10) * tileSize + 64;
			mc.createPlayer(currentZ, var9, currentX, 1, ORSCharacterDirection.lookup(direction));
		}

		packetsIncoming.endBitAccess();
	}

	private void showGameObjects(int length) {
		while (length > packetsIncoming.packetEnd) {
			if (packetsIncoming.getUnsignedByte() != 255) {
				--packetsIncoming.packetEnd;
				int id = packetsIncoming.getShort();
				int xTile = mc.getLocalPlayerX() + packetsIncoming.getByte();
				int zTile = mc.getLocalPlayerZ() + packetsIncoming.getByte();
				int dir = packetsIncoming.getByte();
				int count = 0;

				for (int i = 0; i < mc.getGameObjectInstanceCount(); ++i) {
					if (mc.getGameObjectInstanceX(i) == xTile && zTile == mc.getGameObjectInstanceZ(i)) {
						mc.getScene().removeModel(mc.getGameObjectInstanceModel(i));
						mc.getWorld().removeGameObject_CollisonFlags(mc.getGameObjectInstanceID(i),
							mc.getGameObjectInstanceX(i), mc.getGameObjectInstanceZ(i));
					} else {
						if (count != i) {
							mc.setGameObjectInstanceModel(count, mc.getGameObjectInstanceModel(i));
							mc.setGameObjectInstanceX(count, mc.getGameObjectInstanceX(i));
							mc.setGameObjectInstanceZ(count, mc.getGameObjectInstanceZ(i));
							mc.setGameObjectInstanceID(count, mc.getGameObjectInstanceID(i));
							mc.setGameObjectInstanceDir(count, mc.getGameObjectInstanceDir(i));
						}

						++count;
					}
				}

				mc.setGameObjectInstanceCount(count);

				mc.getWorld().registerObjectDir(xTile, zTile, dir);
				if (id != 60000) {
					int xSize, zSize;
					if (dir == 0 || dir == 4) {
						zSize = com.openrsc.client.entityhandling.EntityHandler.getObjectDef(id).getHeight();
						xSize = com.openrsc.client.entityhandling.EntityHandler.getObjectDef(id).getWidth();
					} else {
						xSize = com.openrsc.client.entityhandling.EntityHandler.getObjectDef(id).getHeight();
						zSize = com.openrsc.client.entityhandling.EntityHandler.getObjectDef(id).getWidth();
					}

					int tileSize = mc.getTileSize();
					int xWorld = (xTile * 2 + xSize) * tileSize / 2;
					int zWorld = (zTile * 2 + zSize) * tileSize / 2;
					int modelIndex = com.openrsc.client.entityhandling.EntityHandler.getObjectDef(id).modelID;// CacheValues.gameObjectModelIndex[id];
					RSModel m = mc.getModelCacheItem(modelIndex).clone();
					mc.getScene().addModel(m);
					m.key = mc.getGameObjectInstanceCount();
					m.addRotation(0, dir * 32, 0);
					m.translate2(xWorld, -mc.getWorld().getElevation(xWorld, zWorld), zWorld);
					m.setDiffuseLightAndColor(-50, -10, -50, 48, 48, true, 117);
					mc.getWorld().addGameObject_UpdateCollisionMap(xTile, zTile, id, false);
					if (id == 74) {
						m.translate2(0, -480, 0);
					}

					mc.setGameObjectInstanceX(mc.getGameObjectInstanceCount(), xTile);
					mc.setGameObjectInstanceZ(mc.getGameObjectInstanceCount(), zTile);
					mc.setGameObjectInstanceID(mc.getGameObjectInstanceCount(), id);
					mc.setGameObjectInstanceDir(mc.getGameObjectInstanceCount(), dir);
					mc.setGameObjectInstanceModel(mc.getGameObjectInstanceCount(), m);
					mc.setGameObjectInstanceCount(mc.getGameObjectInstanceCount() + 1);
				}

			} else {
				int id = 0;
				int xTile = mc.getLocalPlayerX() + packetsIncoming.getByte() >> 3;
				int zTile = mc.getLocalPlayerZ() + packetsIncoming.getByte() >> 3;

				for (int localIndex = 0; mc.getGameObjectInstanceCount() > localIndex; ++localIndex) {
					int dxTile = (mc.getGameObjectInstanceX(localIndex) >> 3) - xTile;
					int dzTile = (mc.getGameObjectInstanceZ(localIndex) >> 3) - zTile;
					if (dxTile == 0 && dzTile == 0) {
						mc.getScene().removeModel(mc.getGameObjectInstanceModel(localIndex));
						mc.getWorld().removeGameObject_CollisonFlags(mc.getGameObjectInstanceID(localIndex),
							mc.getGameObjectInstanceX(localIndex),
							mc.getGameObjectInstanceZ(localIndex));
					} else {
						if (localIndex != id) {
							mc.setGameObjectInstanceModel(id, mc.getGameObjectInstanceModel(localIndex));
							mc.setGameObjectInstanceX(id, mc.getGameObjectInstanceX(localIndex));
							mc.setGameObjectInstanceZ(id, mc.getGameObjectInstanceZ(localIndex));
							mc.setGameObjectInstanceID(id, mc.getGameObjectInstanceID(localIndex));
							mc.setGameObjectInstanceDir(id, mc.getGameObjectInstanceDir(localIndex));
						}
						++id;
					}
				}
				mc.setGameObjectInstanceCount(id);
			}
		}
	}

	private void removeItem() {
		int slot = packetsIncoming.getUnsignedByte();
		mc.setInventoryItemCount(mc.getInventoryItemCount() - 1);

		for (int index = slot; index < mc.getInventoryItemCount(); ++index) {
			mc.setInventoryItem(index, mc.getInventoryItem(index + 1).clone());
		}
		if (mc.getInventoryItemCount() < mc.getInventory().length) {
			mc.setInventoryItem(mc.getInventoryItemCount(), new Item());
		}
	}


	private void gotCombatStylePacket() {
		mc.timeOfLastCombatStylePacket = System.currentTimeMillis();
		mc.proposedStyle = packetsIncoming.getByte();
	}

	private void updateInventoryItem() {
		int slot = packetsIncoming.getUnsignedByte();
		int itemID = packetsIncoming.getShort();
		boolean noted = packetsIncoming.getUnsignedByte() == 1;
		int stackSize = 1;
		if (com.openrsc.client.entityhandling.EntityHandler.getItemDef(itemID & 32767).isStackable() || noted) {
			stackSize = packetsIncoming.get32();
		}
		mc.setInventoryItemID(slot, FastMath.bitwiseAnd(itemID, 32767));
		mc.setInventoryItemEquipped(slot, itemID / '\u8000');
		mc.getInventoryItem(slot).setNoted(noted);
		mc.setInventoryItemSize(slot, stackSize);
		if (slot >= mc.getInventoryItemCount()) {
			mc.setInventoryItemCount(1 + slot);
		}
	}

	private void updateInventory() {
		for (int i = 0; i < Config.S_PLAYER_INVENTORY_SLOTS; i++) {
			Item item = mc.getInventoryItem(i);
			item.setItemDef(null);
			item.setAmount(0);
			item.setEquipped(false);
			item.setNoted(false);
			item.setCharges(0);
			item.setDurability(0);
		}
		mc.setInventoryItemCount(packetsIncoming.getUnsignedByte());
		for (int i = 0; i < mc.getInventoryItemCount(); ++i) {
			int itemID = packetsIncoming.getShort();
			mc.setInventoryItemID(i, itemID);
			mc.setInventoryItemEquipped(i, packetsIncoming.getByte());

			// must always check for this even if server config has notes off, in case they were enabled previously
			boolean noted = packetsIncoming.getByte() == 1;

			mc.getInventoryItem(i).setNoted(noted);
			if (com.openrsc.client.entityhandling.EntityHandler.getItemDef(itemID, noted).isStackable()) {
				mc.setInventoryItemSize(i, packetsIncoming.get32());
			} else {
				mc.setInventoryItemSize(i, 1);
			}
		}
	}

	private void updateEquipmentSlot() {
		int slot = packetsIncoming.getByte();
		int id = packetsIncoming.getShort();
		if (slot == 5)
			slot = 0;
		else if (slot == 6)
			slot = 1;
		else if (slot == 7)
			slot = 2;
		else if (slot > 7)
			slot -= 3;
		int amount = 1;
		if (id == 0xFFFF) {
			mc.equippedItems[slot] = null;
			mc.equippedItemAmount[slot] = 0;
		} else {
			ItemDef item = EntityHandler.getItemDef(id);
			if (item == null)
				return;
			if (item.isStackable())
				amount = packetsIncoming.get32();
			mc.equippedItems[slot] = item;
			mc.equippedItemAmount[slot] = amount;
		}

	}

	private void updateEquipment() {
		int equipCount = packetsIncoming.getUnsignedByte();

		int equipslot = 0;
		int itemID = 0;

		for (int i = 0; i < Config.S_PLAYER_SLOT_COUNT; i++) {
			mc.equippedItems[i] = null;
			mc.equippedItemAmount[i] = 0;
		}

		for (int i = 0; i < equipCount; i++) {
			equipslot = packetsIncoming.getByte();
			itemID = packetsIncoming.getShort();
			if (equipslot == 5)
				equipslot = 0;
			else if (equipslot == 6)
				equipslot = 1;
			else if (equipslot == 7)
				equipslot = 2;
			else if (equipslot > 7)
				equipslot -= 3;
			mc.equippedItems[equipslot] = EntityHandler.getItemDef(itemID);
			if (mc.equippedItems[equipslot].isStackable())
				mc.equippedItemAmount[equipslot] = packetsIncoming.get32();
			else
				mc.equippedItemAmount[equipslot] = 1;
		}
	}

	private void showWalls(int length) {
		while (length > packetsIncoming.packetEnd) {
			if (packetsIncoming.getUnsignedByte() == 255) {
				int wallID = 0;
				int var19 = mc.getLocalPlayerX() + packetsIncoming.getByte() >> 3;
				int var6 = mc.getLocalPlayerZ() + packetsIncoming.getByte() >> 3;

				for (int wallInstance = 0; mc.getWallObjectInstanceCount() > wallInstance; ++wallInstance) {
					int dir = (mc.getWallObjectInstanceX(wallInstance) >> 3) - var19;
					int var9 = (mc.getWallObjectInstanceZ(wallInstance) >> 3) - var6;
					if (dir == 0 && var9 == 0) {
						mc.getScene().removeModel(mc.getWallObjectInstanceModel(wallInstance));
						mc.getWorld().removeWallObject_CollisionFlags(true,
							mc.getWallObjectInstanceDir(wallInstance),
							mc.getWallObjectInstanceZ(wallInstance),
							mc.getWallObjectInstanceX(wallInstance),
							mc.getWallObjectInstanceID(wallInstance));
					} else {
						if (wallID != wallInstance) {
							mc.setWallObjectInstanceModel(wallID, mc.getWallObjectInstanceModel(wallInstance));
							mc.setWallObjectInstanceX(wallID, mc.getWallObjectInstanceX(wallInstance));
							mc.setWallObjectInstanceZ(wallID, mc.getWallObjectInstanceZ(wallInstance));
							mc.setWallObjectInstanceDir(wallID, mc.getWallObjectInstanceDir(wallInstance));
							mc.setWallObjectInstanceID(wallID, mc.getWallObjectInstanceID(wallInstance));
						}

						++wallID;
					}
				}

				mc.setWallObjectInstanceCount(wallID);

			} else {
				--packetsIncoming.packetEnd;
				int id = packetsIncoming.getShort();
				int x = mc.getLocalPlayerX() + packetsIncoming.getByte();
				int y = mc.getLocalPlayerZ() + packetsIncoming.getByte();
				int direction = packetsIncoming.getByte();
				int localIndex = 0;

				for (int var9 = 0; var9 < mc.getWallObjectInstanceCount(); ++var9) {
					if (mc.getWallObjectInstanceX(var9) == x
						&& mc.getWallObjectInstanceZ(var9) == y
						&& direction == mc.getWallObjectInstanceDir(var9)) {
						mc.getScene().removeModel(mc.getWallObjectInstanceModel(var9));
						mc.getWorld().removeWallObject_CollisionFlags(true,
							mc.getWallObjectInstanceDir(var9),
							mc.getWallObjectInstanceZ(var9),
							mc.getWallObjectInstanceX(var9),
							mc.getWallObjectInstanceID(var9));
					} else {
						if (var9 != localIndex) {
							mc.setWallObjectInstanceModel(localIndex, mc.getWallObjectInstanceModel(var9));
							mc.setWallObjectInstanceX(localIndex, mc.getWallObjectInstanceX(var9));
							mc.setWallObjectInstanceZ(localIndex, mc.getWallObjectInstanceZ(var9));
							mc.setWallObjectInstanceDir(localIndex, mc.getWallObjectInstanceDir(var9));
							mc.setWallObjectInstanceID(localIndex, mc.getWallObjectInstanceID(var9));
						}

						++localIndex;
					}
				}

				mc.setWallObjectInstanceCount(localIndex);
				if (id != 60000) {
					mc.getWorld().applyWallToCollisionFlags(id, x, y, direction);
					RSModel model = mc.createWallObjectModel(x, y, id, direction,
						mc.getWallObjectInstanceCount());
					mc.setWallObjectInstanceModel(mc.getWallObjectInstanceCount(), model);
					mc.setWallObjectInstanceX(mc.getWallObjectInstanceCount(), x);
					mc.setWallObjectInstanceZ(mc.getWallObjectInstanceCount(), y);
					mc.setWallObjectInstanceID(mc.getWallObjectInstanceCount(), id);
					mc.setWallObjectInstanceDir(mc.getWallObjectInstanceCount(), direction);
					mc.setWallObjectInstanceCount(mc.getWallObjectInstanceCount() + 1);
				}
			}
		}
	}

	private void showNPCs(int length) {
		mc.setNpcCacheCount(mc.getNpcCount());
		mc.setNpcCount(0);

		for (int i = 0; i < mc.getNpcCacheCount(); ++i) {
			mc.setNpcFromCache(i, mc.getNpc(i));
		}

		packetsIncoming.startBitAccess();
		int count = packetsIncoming.getBitMask(8);
		int tileSize = mc.getTileSize();

		int waypointCurrentIndex, rsDir, waypointX, var12, i;
		for (i = 0; count > i; ++i) {
			ORSCharacter npc = mc.getNpcFromCache(i);
			int var7 = packetsIncoming.getBitMask(1);
			if (var7 != 0) {
				var12 = packetsIncoming.getBitMask(1);
				if (var12 != 0) {
					int nextSpriteOffset = packetsIncoming.getBitMask(2);
					if (nextSpriteOffset == 3) {
						continue;
					}
					npc.animationNext = (nextSpriteOffset << 2)
						+ packetsIncoming.getBitMask(2);
				} else {
					rsDir = packetsIncoming.getBitMask(3);
					waypointCurrentIndex = npc.waypointIndexCurrent;
					waypointX = npc.waypointsX[waypointCurrentIndex];
					if (rsDir == 2 || rsDir == 1 || rsDir == 3) {
						waypointX += tileSize;
					}

					int waypointY = npc.waypointsZ[waypointCurrentIndex];
					if (rsDir == 6 || rsDir == 5 || rsDir == 7) {
						waypointX -= tileSize;
					}

					if (rsDir == 4 || rsDir == 3 || rsDir == 5) {
						waypointY += tileSize;
					}

					if (rsDir == 0 || rsDir == 1 || rsDir == 7) {
						waypointY -= tileSize;
					}

					npc.waypointIndexCurrent = waypointCurrentIndex = (waypointCurrentIndex + 1) % 10;
					npc.animationNext = rsDir;
					npc.waypointsX[waypointCurrentIndex] = waypointX;
					npc.waypointsZ[waypointCurrentIndex] = waypointY;
				}
			}

			mc.setNpc(mc.getNpcCount(), npc);
			mc.setNpcCount(mc.getNpcCount() + 1);
		}

		while (length * 8 > packetsIncoming.getBitHead() + 34) {
			i = packetsIncoming.getBitMask(12);
			int var6 = packetsIncoming.getBitMask(6);
			if (var6 > 31) {
				var6 -= 64;
			}
			int var7 = packetsIncoming.getBitMask(6);
			if (var7 > 31) {
				var7 -= 64;
			}
			var12 = packetsIncoming.getBitMask(4);
			rsDir = (var6 + mc.getLocalPlayerX()) * tileSize + 64;
			waypointCurrentIndex = (var7 + mc.getLocalPlayerZ()) * tileSize + 64;
			waypointX = packetsIncoming.getBitMask(10);
			mc.createNpc(var12, waypointX, rsDir, waypointCurrentIndex, i);
		}

		packetsIncoming.endBitAccess();
	}

	private void updateNPCAppearances() {
		int numberOfUpdates = packetsIncoming.getShort();

		for (int update = 0; numberOfUpdates > update; ++update) {
			int sender = packetsIncoming.getShort();
			ORSCharacter npc = mc.getNpcFromServer(sender);
			int updateType = packetsIncoming.getUnsignedByte();
			if (updateType == 1) { // NPC Chat
				int chatRecipient = packetsIncoming.getShort();
				if (npc != null) {
					String message = packetsIncoming.readString();
					npc.messageTimeout = 150;
					npc.message = message;
					if (mc.getLocalPlayer().serverIndex == chatRecipient) {
						mc.showMessage(false, null,
							com.openrsc.client.entityhandling.EntityHandler.getNpcDef(npc.npcId).getName() + ": " + npc.message,
							MessageType.QUEST, 0, null, "@yel@");

						// Discord update
						Discord.setLastUpdate("Questing");
					}
				}

			} else if (updateType == 2) { // NPC Hitpoints
				int damage = packetsIncoming.getUnsignedByte();
				int currentHits = packetsIncoming.getUnsignedByte();
				int maximumHits = packetsIncoming.getUnsignedByte();

				if (null != npc) {
					npc.damageTaken = damage;
					npc.healthMax = maximumHits;
					npc.combatTimeout = 200;
					npc.healthCurrent = currentHits;
				}
			} else if (updateType == 3) {
				int sprite = packetsIncoming.getShort();
				int shooterServerIndex = packetsIncoming.getShort();
				if (null != npc) {
					npc.attackingNpcServerIndex = shooterServerIndex;
					npc.projectileRange = mc.getProjectileMaxRange();
					npc.attackingPlayerServerIndex = -1;
					npc.incomingProjectileSprite = EntityHandler.projectiles.get(sprite);
				}
			} else if (updateType == 4) {
				int sprite = packetsIncoming.getShort();
				int shooterServerIndex = packetsIncoming.getShort();
				if (npc != null) {
					npc.projectileRange = mc.getProjectileMaxRange();
					npc.attackingNpcServerIndex = -1;
					npc.attackingPlayerServerIndex = shooterServerIndex;
					npc.incomingProjectileSprite = EntityHandler.projectiles.get(sprite);
				}
			} else if (updateType == 5) {
				int skull = packetsIncoming.getUnsignedByte();
				if (null != npc) {
					npc.skull = skull;
				}
			} else if (updateType == 6) {
				int wield = packetsIncoming.getUnsignedByte();
				int wield2 = packetsIncoming.getUnsignedByte();
				if (null != npc) {
					npc.wield = wield;
					npc.wield2 = wield2;
				}
			} else if (updateType == 7) {
				int itemType = packetsIncoming.getShort();
				if (null != npc) {
					//npc.bubbleTimeout = 150;
					npc.bubbleItem = itemType;
				}
			}
		}
	}

	private void showOptionsMenu() {
		mc.setOptionsMenuShow(true);
		int count = packetsIncoming.getUnsignedByte();
		mc.setOptionsMenuCount(count);
		for (int i = 0; count > i; ++i) {
			mc.setOptionsMenuText(i, packetsIncoming.readString());
		}
	}

	private void loadArea() {
		mc.setLoadingArea(true);
		mc.setLocalPlayerServerIndex(packetsIncoming.getShort());
		mc.setWorldOffsetX(packetsIncoming.getShort());
		mc.setWorldOffsetZ(packetsIncoming.getShort());
		mc.setRequestedPlane(packetsIncoming.getShort());
		mc.setM_rc(packetsIncoming.getShort());
		mc.setWorldOffsetZ(mc.getWorldOffsetZ() - (mc.getRequestedPlane() * mc.getM_rc()));
	}

	private void loadStats() {
		for (int stat = 0; stat < mudclient.skillCount; ++stat) {
			mc.setPlayerStatCurrent(stat, packetsIncoming.getUnsignedByte());
		}

		for (int stat = 0; stat < mudclient.skillCount; ++stat) {
			mc.setPlayerStatBase(stat, packetsIncoming.getUnsignedByte());
		}
	}

	private void loadExperience() {
		for (int skill = 0; skill < mudclient.skillCount; ++skill) {
			mc.setPlayerExperience(skill, (int)((packetsIncoming.get32() & 0xffffffffL) / 4));
		}
	}

	private void loadQuestPoints() {
		mc.setQuestPoints(packetsIncoming.getUnsignedByte());
	}

	private void generateCounts(int length) {
		int packets = (length - 1) / 4;

		// Ground Item Counts
		for (int i = 0; packets > i; ++i) {
			int x = mc.getLocalPlayerX() + packetsIncoming.get16_V2() >> 3;
			int z = mc.getLocalPlayerZ() + packetsIncoming.get16_V2() >> 3;

			int newIndex = 0;
			for (int oldIndex = 0; oldIndex < mc.getGroundItemCount(); ++oldIndex) {
				int offsetX = (mc.getGroundItemX(oldIndex) >> 3) - x;
				int offsetY = (mc.getGroundItemZ(oldIndex) >> 3) - z;
				if (offsetX != 0 || offsetY != 0) {
					if (newIndex != oldIndex) {
						mc.setGroundItemX(newIndex, mc.getGroundItemX(oldIndex));
						mc.setGroundItemZ(newIndex, mc.getGroundItemZ(oldIndex));
						mc.setGroundItemID(newIndex, mc.getGroundItemID(oldIndex));
						mc.setGroundItemHeight(newIndex, mc.getGroundItemHeight(oldIndex));
						if (Config.S_WANT_BANK_NOTES)
							mc.setGroundItemNoted(newIndex, mc.getGroundItemNoted(oldIndex));
					}

					++newIndex;
				}
			}

			mc.setGroundItemCount(newIndex);

			// Game Object Counts
			newIndex = 0;
			for (int oldIndex = 0; oldIndex < mc.getGameObjectInstanceCount(); ++oldIndex) {
				int var10 = (mc.getGameObjectInstanceX(oldIndex) >> 3) - x;
				int var11 = (mc.getGameObjectInstanceZ(oldIndex) >> 3) - z;
				if (var10 == 0 && var11 == 0) {
					mc.getScene().removeModel(mc.getGameObjectInstanceModel(oldIndex));
					mc.getWorld().removeGameObject_CollisonFlags(
						mc.getGameObjectInstanceID(oldIndex),
						mc.getGameObjectInstanceX(oldIndex),
						mc.getGameObjectInstanceZ(oldIndex));
				} else {
					if (oldIndex != newIndex) {
						mc.setGameObjectInstanceModel(newIndex, mc.getGameObjectInstanceModel(oldIndex));
						mc.setGameObjectInstanceX(newIndex, mc.getGameObjectInstanceX(oldIndex));
						mc.setGameObjectInstanceZ(newIndex, mc.getGameObjectInstanceZ(oldIndex));
						mc.setGameObjectInstanceID(newIndex, mc.getGameObjectInstanceID(oldIndex));
						mc.setGameObjectInstanceDir(newIndex, mc.getGameObjectInstanceDir(oldIndex));
					}

					++newIndex;
				}
			}

			mc.setGameObjectInstanceCount(newIndex);

			// Wall Object Counts
			newIndex = 0;
			for (int oldIndex = 0; mc.getWallObjectInstanceCount() > oldIndex; ++oldIndex) {
				int wallX = (mc.getWallObjectInstanceX(oldIndex) >> 3) - x;
				int wallZ = (mc.getWallObjectInstanceZ(oldIndex) >> 3) - z;
				if (wallX == 0 && wallZ == 0) {
					mc.getScene().removeModel(mc.getWallObjectInstanceModel(oldIndex));
					mc.getWorld().removeWallObject_CollisionFlags(true,
						mc.getWallObjectInstanceDir(oldIndex),
						mc.getWallObjectInstanceZ(oldIndex),
						mc.getWallObjectInstanceX(oldIndex),
						mc.getWallObjectInstanceID(oldIndex));
				} else {
					if (oldIndex != newIndex) {
						mc.setWallObjectInstanceModel(newIndex, mc.getWallObjectInstanceModel(oldIndex));
						mc.setWallObjectInstanceX(newIndex, mc.getWallObjectInstanceX(oldIndex));
						mc.setWallObjectInstanceZ(newIndex, mc.getWallObjectInstanceZ(oldIndex));
						mc.setWallObjectInstanceDir(newIndex, mc.getWallObjectInstanceDir(oldIndex));
						mc.setWallObjectInstanceID(newIndex, mc.getWallObjectInstanceID(oldIndex));
					}

					++newIndex;
				}
			}

			mc.setWallObjectInstanceCount(newIndex);
		}
	}

	private void showTradeDialog() {
		int serverIndex = packetsIncoming.getShort();
		if (mc.getPlayerFromServer(serverIndex) != null) {
			mc.setTradeRecipientName(mc.getPlayerFromServer(serverIndex).displayName);
		}

		mc.setShowDialogTrade(true);
		mc.setTradeRecipientItemsCount(0);
		mc.setTradeItemCount(0);
		mc.setTradeAccepted(false);
		mc.setTradeRecipientAccepted(false);
	}

	private void setTradeConfirmed() {
		mc.setShowDialogTradeConfirm(false);
		mc.setShowDialogTrade(false);
	}

	private void tradeRecipientDecision() {
		int accepted = packetsIncoming.getUnsignedByte();
		if (accepted != 1) {
			mc.setTradeRecipientAccepted(false);
		} else {
			mc.setTradeRecipientAccepted(true);
		}
	}

	private void tradeSelfDecision() {
		int accepted = packetsIncoming.getByte();
		if (accepted != 1) {
			mc.setTradeAccepted(false);
		} else {
			mc.setTradeAccepted(true);
		}
	}

	private void updateOptionsMenuSettings() {
		mc.setOptionCameraModeAuto(packetsIncoming.getUnsignedByte() == 1); // byte index 0
		mc.setOptionMouseButtonOne(packetsIncoming.getUnsignedByte() == 1); // 1
		mc.setOptionSoundDisabled(packetsIncoming.getUnsignedByte() == 1); // 2
		mc.setCombatStyle(packetsIncoming.getUnsignedByte()); // ?
		mc.setSettingsBlockGlobal(packetsIncoming.getUnsignedByte()); // 9
		mc.setClanInviteBlockSetting(packetsIncoming.getUnsignedByte() == 1); // 11
		mc.setVolumeFunction(packetsIncoming.getUnsignedByte()); // 16
		mc.setSwipeToRotate(packetsIncoming.getUnsignedByte() == 1); // 17
		mc.setSwipeToScroll(packetsIncoming.getUnsignedByte() == 1); // 18
		mc.setLongPressDelay(packetsIncoming.getUnsignedByte()); // 19
		mc.setFontSize(packetsIncoming.getUnsignedByte()); // 20
		mc.setHoldAndChoose(packetsIncoming.getUnsignedByte() == 1); // 21
		mc.setSwipeToZoom(packetsIncoming.getUnsignedByte() == 1); // 22
		mc.setLastZoom(packetsIncoming.getUnsignedByte()); // 23
		mc.setOptionBatchProgressBar(packetsIncoming.getUnsignedByte() == 1); // 24
		mc.setOptionExperienceDrops(packetsIncoming.getUnsignedByte() == 1); // 25
		mc.setOptionHideRoofs(packetsIncoming.getUnsignedByte() == 1); // 26
		mc.setOptionHideFog(packetsIncoming.getUnsignedByte() == 1); // 27
		mc.setGroundItemsToggle(packetsIncoming.getUnsignedByte()); // 28
		mc.setOptionAutoMessageSwitch(packetsIncoming.getUnsignedByte() == 1); // 29
		mc.setOptionSideMenu(packetsIncoming.getUnsignedByte() == 1); // 30
		mc.setOptionHideKillFeed(packetsIncoming.getUnsignedByte() == 1); // 31
		mc.setFightModeSelectorToggle(packetsIncoming.getUnsignedByte()); // 32
		mc.setExperienceCounterToggle(packetsIncoming.getUnsignedByte()); // 33
		mc.setHideInventoryCount(packetsIncoming.getUnsignedByte() == 1); // 34
		mc.setHideNameTag(packetsIncoming.getUnsignedByte() == 1); // 35
		mc.setBlockPartyInv(packetsIncoming.getUnsignedByte() == 1); // 36
		mc.setAndroidInvToggle(packetsIncoming.getUnsignedByte() == 1); // 37
		mc.setShowNPCKC(packetsIncoming.getUnsignedByte() == 1); // 38
		mc.setCustomUI(packetsIncoming.getUnsignedByte() == 1); //39
		mc.setHideLoginBox(packetsIncoming.getUnsignedByte() == 1); // 40
		mc.setBlockGlobalFriend(packetsIncoming.getUnsignedByte() == 1); // 41
	}

	private void togglePrayer(int length) {
		for (int i = 0; length - 1 > i; ++i) {
			boolean enabled = packetsIncoming.getByte() == 1;
			if (!mc.checkPrayerOn(i) && enabled) {
				soundPlayer.playSoundFile("prayeron");
			}
			if (mc.checkPrayerOn(i) && !enabled) {
				soundPlayer.playSoundFile("prayeroff");
			}

			mc.togglePrayer(i, enabled);
		}
	}

	private void updateQuestStage() {
		int updateQuestType = packetsIncoming.getByte();
		if (updateQuestType == 0) {
			int questCount = packetsIncoming.getByte();
			for (int i = 0; i < questCount; i++) {
				int questId = packetsIncoming.get32();
				int questStage = packetsIncoming.get32();

				String questName = packetsIncoming.readString();

				mc.setQuestName(questId, questName);
				mc.setQuestStage(questId, questStage);
			}
		} else if (updateQuestType == 1) {
			int questID = packetsIncoming.get32();
			int stage = packetsIncoming.get32();

			mc.setQuestStage(questID, stage);
		}

		// Discord status
		Discord.setLastUpdate("Questing");
	}

	private void showBank() {
		mc.setShowDialogBank(true);
		mc.setNewBankItemCount(packetsIncoming.getShort());
		mc.setBankItemsMax(packetsIncoming.getShort());
		mc.getBank().resetBank();
		for (int slot = 0; slot < mc.getNewBankItemCount(); ++slot) {
			mc.getBank().addBank(slot, packetsIncoming.getShort(), packetsIncoming.get32());
		}

		// Discord update
		Discord.setLastUpdate("Banking");
	}

	private void updateIndividualExperience() {
		int skill = packetsIncoming.getUnsignedByte();
		mc.setPlayerExperience(skill, (int)((packetsIncoming.get32() & 0xffffffffL) / 4));
	}

	private void closeDuelDialog() {
		mc.setShowDialogDuel(false);
		mc.setShowDialogDuelConfirm(false);
	}

	private void confirmTrade() {
		mc.setShowDialogTrade(false);
		mc.setShowDialogTradeConfirm(true);
		mc.setTradeConfirmAccepted(false);
		mc.setTradeRecipientConfirmName(packetsIncoming.readString());
		mc.setTradeRecipientConfirmItemsCount(packetsIncoming.getUnsignedByte());

		for (int var4 = 0; mc.getTradeRecipientConfirmItemsCount() > var4; ++var4) {
			int itemID = packetsIncoming.getShort();
			mc.setTradeRecipientConfirmItemID(var4, itemID);
			boolean noted = false;
			if (Config.S_WANT_BANK_NOTES) {
				noted = packetsIncoming.getByte() == 1;
			}
			mc.getTradeRecipientConfirmItem(var4).setNoted(noted);
			mc.setTradeRecipientConfirmItemCount(var4, packetsIncoming.get32());
		}

		mc.setTradeConfirmItemsCount(packetsIncoming.getUnsignedByte());

		for (int var4 = 0; var4 < mc.getTradeConfirmItemsCount(); ++var4) {
			int itemID = packetsIncoming.getShort();
			mc.setTradeConfirmItemID(var4, itemID);
			boolean noted = false;
			if (Config.S_WANT_BANK_NOTES) {
				noted = packetsIncoming.getByte() == 1;
			}
			mc.getTradeConfirmItem(var4).setNoted(noted);
			mc.setTradeConfirmItemsCount(var4, packetsIncoming.get32());
		}
	}

	private void showDuelItems() {
		mc.setDuelOpponentItemsCount(packetsIncoming.getUnsignedByte());

		for (int var4 = 0; mc.getDuelOpponentItemsCount() > var4; ++var4) {
			int itemID = packetsIncoming.getShort();
			mc.setDuelOpponentItemID(var4, itemID);
			boolean noted = false;
			if (Config.S_WANT_BANK_NOTES) {
				noted = packetsIncoming.getByte() == 1;
			}
			mc.getDuelOpponentItem(var4).setNoted(noted);
			mc.setDuelOpponentItemCount(var4, packetsIncoming.get32());
		}

		mc.setDuelOfferAccepted(false);
		mc.setDuelOffsetOpponentAccepted(false);
	}

	private void toggleDuelSetting() {
		if (packetsIncoming.getUnsignedByte() == 1) {
			mc.setDuelSettingsRetreat(true);
		} else {
			mc.setDuelSettingsRetreat(false);
		}

		if (packetsIncoming.getUnsignedByte() != 1) {
			mc.setDuelSettingsMagic(false);
		} else {
			mc.setDuelSettingsMagic(true);
		}

		if (packetsIncoming.getUnsignedByte() != 1) {
			mc.setDuelSettingsPrayer(false);
		} else {
			mc.setDuelSettingsPrayer(true);
		}

		if (packetsIncoming.getUnsignedByte() != 1) {
			mc.setDuelSettingsWeapons(false);
		} else {
			mc.setDuelSettingsWeapons(true);
		}

		mc.setDuelOfferAccepted(false);
		mc.setDuelOffsetOpponentAccepted(false);
	}

	private void updateBank() {
		int slot = packetsIncoming.getUnsignedByte();
		int item = packetsIncoming.getShort();
		int itemCount = packetsIncoming.get32();
		mc.getBank().updateBank(slot, item, itemCount);
	}

	private void updateExperience() {
		int skill = packetsIncoming.getUnsignedByte();
		int oldXP = mc.getPlayerExperience(skill);
		int oldLvl = mc.getPlayerStatBase(skill);
		mc.setPlayerStatCurrent(skill, packetsIncoming.getUnsignedByte());
		mc.setPlayerStatBase(skill, packetsIncoming.getUnsignedByte());
		mc.setPlayerExperience(skill, (int)((packetsIncoming.get32() & 0xffffffffL) / 4));
		updateExperienceTracker(skill, oldXP, oldLvl);

		// Update the discord status
		final String[] skillNames = {"Attack", "Defense", "Strength", "Hits",
			"Ranged", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching",
			"Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblaw",
			"Agility", "Thieving", "Runecraft", "Harvesting"};
		if (skill == 0 || skill == 1 || skill == 2 || skill == 3) {
			Discord.setLastUpdate("Training Combat");
		} else {
			Discord.setLastUpdate("Training " + skillNames[skill]);
		}
	}

	private void updateExperienceTracker(int skill, int oldXp, int oldLvl) {
		int receivedXp = mc.getPlayerExperience(skill) - oldXp;
		receivedXp = Math.max(receivedXp, 0);

		if (Config.S_EXPERIENCE_COUNTER_TOGGLE) {
			mc.setRecentSkill(skill);
			mc.setPlayerStatXpGained(skill, mc.getPlayerStatXpGained(skill) + receivedXp);
			if (mc.getXpGainedStartTime(skill) == 0) {
				mc.setXpGainedStartTime(skill, System.currentTimeMillis());
			}
			mc.setPlayerXpGainedTotal(mc.getPlayerXpGainedTotal() + (long) receivedXp);
			if (mc.totalXpGainedStartTime == 0) {
				mc.totalXpGainedStartTime = System.currentTimeMillis();
			}
		}

		if (Config.S_EXPERIENCE_DROPS_TOGGLE && Config.C_EXPERIENCE_DROPS) {
			if (receivedXp > 0) {
				mc.addXpNotification(skill, receivedXp, false);
			}
			if (oldLvl < mc.getPlayerStatBase(skill)) {
				mc.addXpNotification(skill, 1, true);
			}
		}
	}

	private void duelDecision() {
		int accepted = packetsIncoming.getByte();
		if (accepted != 1) {
			mc.setDuelOfferAccepted(false);
		} else {
			mc.setDuelOfferAccepted(true);
		}
	}

	private void showDuelConfirmDialog() {
		mc.setDuelConfirmed(false);
		mc.setShowDialogDuelConfirm(true);
		mc.setShowDialogDuel(false);
		mc.setDuelOpponentName(packetsIncoming.readString());
		mc.setDuelOpponentConfirmItemsCount(packetsIncoming.getUnsignedByte());
		for (int var4 = 0; var4 < mc.getDuelOpponentConfirmItemsCount(); ++var4) {
			int itemID = packetsIncoming.getShort();
			mc.setDuelOpponentConfirmItemID(var4, itemID);
			boolean noted = false;
			if (Config.S_WANT_BANK_NOTES) {
				noted = packetsIncoming.getByte() == 1;
			}
			mc.getDuelOpponentConfirmItem(var4).setNoted(noted);
			mc.setDuelOpponentConfirmItemCount(var4, packetsIncoming.get32());
		}

		mc.setDuelConfirmItemsCount(packetsIncoming.getUnsignedByte());

		for (int var4 = 0; mc.getDuelConfirmItemsCount() > var4; ++var4) {
			int itemID = packetsIncoming.getShort();
			mc.setDuelConfirmItemID(var4, itemID);
			boolean noted = false;
			if (Config.S_WANT_BANK_NOTES) {
				noted = packetsIncoming.getByte() == 1;
			}
			mc.getDuelConfirmItem(var4).setNoted(noted);
			mc.setDuelConfirmItemCount(var4, packetsIncoming.get32());
		}

		mc.setDuelOptionRetreat(packetsIncoming.getUnsignedByte());
		mc.setDuelOptionMagic(packetsIncoming.getUnsignedByte());
		mc.setDuelOptionPrayer(packetsIncoming.getUnsignedByte());
		mc.setDuelOptionWeapons(packetsIncoming.getUnsignedByte());
	}

	private void playSound() {
		String filename = packetsIncoming.readString();
		soundPlayer.playSoundFile(filename);
	}

	private void showLoginDialog() {
		if (!Config.C_HIDE_LOGIN_BOX) {
			if (!mc.getWelcomeScreenShown()) {
				mc.setWelcomeLastLoggedInIp(packetsIncoming.readString());
				mc.setWelcomeLastLoggedInDays(packetsIncoming.getShort());
				mc.setWelcomeRecoverySetDays(packetsIncoming.getShort());
				mc.setWelcomeTipOfDay((int) (Math.random() * 6.0D));
				//this.welcomeUnreadMessages = packetsIncoming.getShort();
				mc.setShowDialogMessage(true);
				mc.setWelcomeLastLoggedInHost(null);
				mc.setWelcomeScreenShown(true);
			}
		}
	}

	private void showServerMessageDialog() {
		mc.setServerMessage(packetsIncoming.readString());
		mc.setShowDialogServerMessage(true);
		mc.setServerMessageBoxTop(true);
	}

	private void showSleepScreen(int length) {
		if (!mc.getIsSleeping()) {
			mc.setFatigueSleeping(mc.getStatFatigue());
		}

		mc.setInputTextCurrent("");
		mc.setIsSleeping(true);
		mc.setInputTextFinal("");
		Sprite sprite = mc.makeSleepSprite(new ByteArrayInputStream(packetsIncoming.dataBuffer, 1, length));
		mc.getSurface().createCaptchaSprite(sprite);

		mc.setSleepingStatusText(null);

		if (isAndroid() && !osConfig.F_SHOWING_KEYBOARD) {
			mc.clientPort.drawKeyboard();
		}
	}

	private void showServerMessageDialogTwo() {
		mc.setServerMessage(packetsIncoming.readString());
		mc.setShowDialogServerMessage(true);
		mc.setServerMessageBoxTop(false);
	}

	private void drawTeleportBubbles() {
		if (mc.getTeleportBubbleCount() < 50) {
			int type = packetsIncoming.getUnsignedByte();
			int x = packetsIncoming.getByte() + mc.getLocalPlayerX();
			int z = packetsIncoming.getByte() + mc.getLocalPlayerZ();
			mc.setTeleportBubbleType(mc.getTeleportBubbleCount(), type);
			mc.setTeleportBubbleTime(mc.getTeleportBubbleCount(), 0);
			mc.setTeleportBubbleX(mc.getTeleportBubbleCount(), x);
			mc.setTeleportBubbleZ(mc.getTeleportBubbleCount(), z);
			mc.setTeleportBubbleCount(mc.getTeleportBubbleCount() + 1);
		}
	}

	private void duelOpponentDecision() {
		int accepted = packetsIncoming.getByte();
		if (accepted != 1) {
			mc.setDuelOffsetOpponentAccepted(false);
		} else {
			mc.setDuelOffsetOpponentAccepted(true);
		}
	}

	private void beginDuelOptions() {
		int var4 = packetsIncoming.getShort();
		if (null != mc.getPlayerFromServer(var4)) {
			mc.setDuelConfirmOpponentName(mc.getPlayerFromServer(var4).displayName);
		}

		mc.setDuelOfferAccepted(false);
		mc.setDuelSettingsPrayer(false);
		mc.setDuelOffsetOpponentAccepted(false);
		mc.setDuelSettingsWeapons(false);
		mc.setDuelSettingsRetreat(false);
		mc.setShowDialogDuel(true);
		mc.setDuelSettingsMagic(false);
		mc.setDuelOpponentItemsCount(0);
		mc.setDuelOfferItemsCount(0);
	}

	private void showShopDialog() {
		mc.setShowDialogShop(true);
		int shopItemCount = packetsIncoming.getUnsignedByte();
		int shopType = packetsIncoming.getByte();
		mc.setShopSellPriceMod(packetsIncoming.getUnsignedByte());
		mc.setShopBuyPriceMod(packetsIncoming.getUnsignedByte());
		mc.setShopPriceMultiplier(packetsIncoming.getUnsignedByte());

		for (int i = 0; i < 40; ++i) {
			mc.setShopCategoryID(i, -1);
		}

		for (int i = 0; shopItemCount > i; ++i) {
			mc.setShopCategoryID(i, packetsIncoming.getShort());
			mc.setShopItemCount(i, packetsIncoming.getShort());
			mc.setShopItemPrice(i, packetsIncoming.getShort());
		}

		// Check through player's inventory items if this is a general store.
		int lastAvailableShopSlotIndex = 39;
		for (int inventoryIndex = 0; inventoryIndex < mc.getInventoryItemCount()
			&& shopItemCount <= lastAvailableShopSlotIndex; ++inventoryIndex) {

			// Only show player items if they are in a general store, or it's a note in a specialty store.
			if (!(shopType == 1
				|| (mc.getInventoryItem(inventoryIndex).getNoted() && mc.shopContains(mc.getInventoryItemID(inventoryIndex)))))
				continue;

			boolean foundEquivalentShopItem = false;
			for (int currentShopSlot = 0; currentShopSlot < 40; ++currentShopSlot) {

				// Can only match if category matches and not noted.
				if (mc.getInventoryItemID(inventoryIndex) == mc.getShopCategoryID(currentShopSlot)
					&& mc.getInventoryItem(inventoryIndex).getNoted() == mc.getShopItemNoted(currentShopSlot)) {
					foundEquivalentShopItem = true;
					break;
				}
			}

			if (mc.getInventoryItemID(inventoryIndex) == 10) {
				continue;
			}

			// If we have not found an inventory item in the shop stock, add the item to the end of the shop.
			if (!foundEquivalentShopItem) {
				mc.setShopCategoryID(lastAvailableShopSlotIndex, FastMath.bitwiseAnd(32767, mc.getInventoryItemID(inventoryIndex)));
				mc.setShopItemCount(lastAvailableShopSlotIndex, 0);
				mc.setShopItemPrice(lastAvailableShopSlotIndex, 0);
				mc.setShopItemNoted(lastAvailableShopSlotIndex, mc.getInventoryItem(inventoryIndex).getNoted());
				--lastAvailableShopSlotIndex;
			}
		}

		if (mc.getShopSelectedItemIndex() >= 0 && 40 > mc.getShopSelectedItemIndex()
			&& mc.getShopSelectedItemType() != mc.getShopCategoryID(mc.getShopSelectedItemIndex())) {
			mc.setShopSelectedItemIndex(-1);
			mc.setShopSelectedItemType(-2);
		}

		// Discord status
		Discord.setLastUpdate("Shopping");
	}

	private void updateTradeDialog() {
		mc.setTradeRecipientItemsCount(packetsIncoming.getUnsignedByte());

		for (int var4 = 0; var4 < mc.getTradeRecipientItemsCount(); ++var4) {
			int itemID = packetsIncoming.getShort();
			mc.setTradeRecipientItemID(var4, itemID);
			boolean noted = false;
			if (Config.S_WANT_BANK_NOTES) {
				noted = packetsIncoming.getByte() == 1;
			}
			mc.getTradeRecipientItem(var4).setNoted(noted);
			mc.setTradeRecipientItemCount(var4, packetsIncoming.get32());
		}

		mc.setTradeItemCount(packetsIncoming.getUnsignedByte());

		for (int var4 = 0; var4 < mc.getTradeItemCount(); ++var4) {
			int itemID = packetsIncoming.getShort();
			mc.setTradeItemID(var4, itemID);
			boolean noted = false;
			if (Config.S_WANT_BANK_NOTES) {
				noted = packetsIncoming.getByte() == 1;
			}
			mc.getTradeItem(var4).setNoted(noted);
			mc.setTradeItemSize(var4, packetsIncoming.get32());
		}

		mc.setTradeRecipientAccepted(false);
		mc.setTradeAccepted(false);
	}

	private void updateEquipmentStats() {
		for (int eq = 0; eq < 5; ++eq) {
			mc.setPlayerStatEquipment(eq, packetsIncoming.getUnsignedByte());
		}
	}

	private void drawNearbyPlayers() {
		int playerCount = packetsIncoming.getShort();
		for (int pp = 0; playerCount > pp; ++pp) {
			int playerServerIndex = packetsIncoming.getShort();
			ORSCharacter player = mc.getPlayerFromServer(playerServerIndex);
			int updateType = packetsIncoming.getByte();
			if (updateType == 0) {
				int itemType = packetsIncoming.getShort();
				if (null != player) {
					player.bubbleTimeout = 150;
					player.bubbleItem = itemType;
				}
			} else if (updateType == 1 || updateType == 6 || updateType == 7) {
				if (updateType == 1 || updateType == 7) {
					int crownID = packetsIncoming.get32();
					boolean muted = false, onTutorial = false;
					if (updateType == 7) {
						muted = packetsIncoming.getUnsignedByte() > 0;
						onTutorial = packetsIncoming.getUnsignedByte() > 0;
					}
					String message = packetsIncoming.readString();

					if (updateType == 7 && message.equalsIgnoreCase(""))
						continue;

					if (null != player) {
						boolean var29 = false;
						String displayName = StringUtil.displayNameToKey(player.accountName);
						if (null != displayName) {
							for (int modelIndex = 0; modelIndex < SocialLists.ignoreListCount; ++modelIndex) {
								if (displayName.equals(
									StringUtil.displayNameToKey(SocialLists.ignoreList[modelIndex]))) {
									var29 = true;
									break;
								}
							}
						}

						if (!var29) {
							player.messageTimeout = 150;
							player.message = message;
							mc.showMessage(
								/*!Config.S_WANT_CUSTOM_RANK_DISPLAY*/ true,
								(
									((updateType == 7 && muted) ? "@whi@[MUTED]@yel@ " : "") +
										((updateType == 7 && onTutorial) ? "@whi@[TUTORIAL]@yel@ " : "") +
										(player.clanTag != null ? "@whi@[@cla@" + player.clanTag + "@whi@]@yel@ " : "") +
										player.getStaffName()
								),
								player.message,
								MessageType.CHAT,
								crownID,
								player.accountName
							);
						}
					}
				} else {
					String message = packetsIncoming.readString();

					if (null != player) {
						player.message = message;
						player.messageTimeout = 150;
						if (mc.getLocalPlayer() == player) {
							mc.showMessage(false, (player.clanTag != null ? "@whi@[@cla@" + player.clanTag + "@whi@]@whi@ " + player.getStaffName() : player.getStaffName()), player.message, MessageType.QUEST, 0, player.accountName);
						}
					}
				}
			} else if (updateType == 2) {
				int damage = packetsIncoming.getUnsignedByte();
				int curhp = packetsIncoming.getUnsignedByte();
				int maxhp = packetsIncoming.getUnsignedByte();
				if (player != null) {
					player.healthMax = maxhp;
					player.healthCurrent = curhp;
					player.damageTaken = damage;
					if (mc.getLocalPlayer() == player) {
						mc.setPlayerStatCurrent(3, curhp);
						mc.setPlayerStatBase(3, maxhp);
						mc.setShowDialogServerMessage(false);
						mc.setShowDialogMessage(false);
					}
					player.combatTimeout = 200;
				}
			} else if (updateType == 3) {
				int sprite = packetsIncoming.getShort();
				int shooterServerIndex = packetsIncoming.getShort();
				if (null != player) {
					player.attackingNpcServerIndex = shooterServerIndex;
					player.projectileRange = mc.getProjectileMaxRange();
					player.attackingPlayerServerIndex = -1;
					player.incomingProjectileSprite = EntityHandler.projectiles.get(sprite);
				}
			} else if (updateType == 4) {
				int sprite = packetsIncoming.getShort();
				int shooterServerIndex = packetsIncoming.getShort();
				if (player != null) {
					player.projectileRange = mc.getProjectileMaxRange();
					player.attackingNpcServerIndex = -1;
					player.attackingPlayerServerIndex = shooterServerIndex;
					player.incomingProjectileSprite = EntityHandler.projectiles.get(sprite);
				}
			} else if (updateType == 5) {
				if (player == null) {
					//packetsIncoming.getShort();
					packetsIncoming.readString();
					//packetsIncoming.readString();
					int itemCount = packetsIncoming.getUnsignedByte();
					for (int i = 0; i < itemCount; ++i)
						packetsIncoming.getShort();
					packetsIncoming.getUnsignedByte();
					packetsIncoming.getUnsignedByte();
					packetsIncoming.getUnsignedByte();
					packetsIncoming.getUnsignedByte();
					packetsIncoming.getUnsignedByte();
					packetsIncoming.getUnsignedByte();
					if (packetsIncoming.getByte() == 1)
						packetsIncoming.readString();
					packetsIncoming.getUnsignedByte();
					packetsIncoming.getUnsignedByte();
					packetsIncoming.getUnsignedByte();
					packetsIncoming.get32();
				} else {
					//packetsIncoming.getShort();

					String playerName = packetsIncoming.readString();
					player.displayName = player.accountName = playerName;
					//player.displayName = packetsIncoming.readString();
					//player.accountName = packetsIncoming.readString();

					int itemCount = packetsIncoming.getUnsignedByte();
					for (int i = 0; i < itemCount; ++i) {
						player.layerAnimation[i] = packetsIncoming.getShort();
					}
					for (int i = itemCount; i < 12; ++i) {
						player.layerAnimation[i] = 0;
					}

					player.colourHair = packetsIncoming.getUnsignedByte();
					player.colourTop = packetsIncoming.getUnsignedByte();
					player.colourBottom = packetsIncoming.getUnsignedByte();
					player.colourSkin = packetsIncoming.getUnsignedByte();
					player.level = packetsIncoming.getUnsignedByte();
					player.skullVisible = packetsIncoming.getUnsignedByte();
					if (packetsIncoming.getByte() == 1) {
						player.clanTag = packetsIncoming.readString();
					} else {
						player.clanTag = null;
					}

					player.isInvisible = packetsIncoming.getByte() > 0;
					player.isInvulnerable = packetsIncoming.getByte() > 0;
					player.groupID = packetsIncoming.getByte();
					player.icon = packetsIncoming.get32();
				}
			} else if (updateType == 8) {
				int heal = packetsIncoming.getUnsignedByte();
				int curhp = packetsIncoming.getUnsignedByte();
				int maxhp = packetsIncoming.getUnsignedByte();
				if (player != null) {
					player.healthMax = maxhp;
					player.healthCurrent = curhp;
					player.healTaken = heal;
					if (mc.getLocalPlayer() == player) {
						mc.setPlayerStatCurrent(3, curhp);
						mc.setPlayerStatBase(3, maxhp);
						mc.setShowDialogServerMessage(false);
						mc.setShowDialogMessage(false);
					}
					player.healTimeout = 200;
				}
			} else if (updateType == 9) {
				int curhp = packetsIncoming.getUnsignedByte();
				int maxhp = packetsIncoming.getUnsignedByte();
				if (player != null) {
					player.healthMax = maxhp;
					player.healthCurrent = curhp;
					if (mc.getLocalPlayer() == player) {
						mc.setPlayerStatCurrent(3, curhp);
						mc.setPlayerStatBase(3, maxhp);
						mc.setShowDialogServerMessage(false);
						mc.setShowDialogMessage(false);
					}
				}
			}
		}
	}

	private void drawGroundItems(int length) {
		while (length > packetsIncoming.packetEnd) {

			boolean groundItemNoted = false;

			// Ground items that are in range
			if (packetsIncoming.getUnsignedByte() != 255) {
				--packetsIncoming.packetEnd;
				int groundItemID = packetsIncoming.getShort();
				int groundItemX = mc.getLocalPlayerX() + packetsIncoming.getByte();
				int groundItemY = mc.getLocalPlayerZ() + packetsIncoming.getByte();
				if (Config.S_WANT_BANK_NOTES)
					groundItemNoted = packetsIncoming.getByte() == 1;

				// Currently visible ground items
				if ((groundItemID & 32768) != 0) {
					groundItemID &= 32767;

					// Loop through the ground items and check if they need to be reindexed.
					int newIndex = 0;
					for (int oldIndex = 0; oldIndex < mc.getGroundItemCount(); ++oldIndex) {

						// No need to reindex if X, Y, and ID are the same.
						if (mc.getGroundItemX(oldIndex) == groundItemX && mc.getGroundItemZ(oldIndex) == groundItemY
							&& mc.getGroundItemID(oldIndex) == groundItemID) {
							groundItemID = -123;
						} else {
							if (newIndex != oldIndex) {
								mc.setGroundItemX(newIndex, mc.getGroundItemX(oldIndex));
								mc.setGroundItemZ(newIndex, mc.getGroundItemZ(oldIndex));
								mc.setGroundItemID(newIndex, mc.getGroundItemID(oldIndex));
								mc.setGroundItemHeight(newIndex, mc.getGroundItemHeight(oldIndex));
								if (Config.S_WANT_BANK_NOTES)
									mc.setGroundItemNoted(newIndex, mc.getGroundItemNoted(oldIndex));
							}

							++newIndex;
						}
					}

					// After the above loop, we now have our new count of ground items.
					mc.setGroundItemCount(newIndex);

				// Currently invisible ground items
				} else {
					mc.setGroundItemX(mc.getGroundItemCount(), groundItemX);
					mc.setGroundItemZ(mc.getGroundItemCount(), groundItemY);
					mc.setGroundItemID(mc.getGroundItemCount(), groundItemID);
					mc.setGroundItemHeight(mc.getGroundItemCount(), 0);
					if (Config.S_WANT_BANK_NOTES) {
						mc.setGroundItemNoted(mc.getGroundItemCount(), groundItemNoted);
					}

					for (int var7 = 0; mc.getGameObjectInstanceCount() > var7; ++var7) {
						if (mc.getGameObjectInstanceX(var7) == groundItemX
							&& mc.getGameObjectInstanceZ(var7) == groundItemY) {
							// Ground item is on a game object
							mc.setGroundItemHeight(mc.getGroundItemCount(),
								com.openrsc.client.entityhandling.EntityHandler.getObjectDef(
									mc.getGameObjectInstanceID(var7)).getGroundItemVar());
							break;
						}
					}

					mc.setGroundItemCount(mc.getGroundItemCount() + 1);
				}

			// Ground items that are not in range.
			} else {
				int newIndex = 0;
				int offsetX = mc.getLocalPlayerX() + packetsIncoming.getByte() >> 3;
				int offsetY = mc.getLocalPlayerZ() + packetsIncoming.getByte() >> 3;

				if (Config.S_WANT_BANK_NOTES)
					groundItemNoted = packetsIncoming.getByte() == 1;

				for (int oldIndex = 0; mc.getGroundItemCount() > oldIndex; ++oldIndex) {
					int tileX = (mc.getGroundItemX(oldIndex) >> 3) - offsetX;
					int tileY = (mc.getGroundItemZ(oldIndex) >> 3) - offsetY;
					if (tileX != 0 || tileY != 0) {
						if (newIndex != oldIndex) {
							mc.setGroundItemX(newIndex, mc.getGroundItemX(oldIndex));
							mc.setGroundItemZ(newIndex, mc.getGroundItemZ(oldIndex));
							mc.setGroundItemID(newIndex, mc.getGroundItemID(oldIndex));
							mc.setGroundItemHeight(newIndex, mc.getGroundItemHeight(oldIndex));
							if (Config.S_WANT_BANK_NOTES)
								mc.setGroundItemNoted(newIndex, mc.getGroundItemNoted(oldIndex));
						}
						++newIndex;
					}
				}

				mc.setGroundItemCount(newIndex);
			}
		}
	}

	private void updatePreset() {
		int slot = packetsIncoming.getShort();
		int itemID, amount;
		ItemDef item;
		Item[] inventoryItems = new Item[Config.S_PLAYER_INVENTORY_SLOTS];
		Item[] equipmentItems = new Item[Config.S_PLAYER_SLOT_COUNT];
		byte[] itemBytes = new byte[2];
		for (int i = 0; i < inventoryItems.length; i++) {
			itemBytes[0] = packetsIncoming.getByte();
			if (itemBytes[0] == -1)
				continue;
			itemBytes[1] = packetsIncoming.getByte();
			itemID = (((int) itemBytes[0] << 8) & 0xFF00) | (int) itemBytes[1] & 0xFF;
			item = EntityHandler.getItemDef(itemID);
			boolean noted = packetsIncoming.getByte() == 1;
			if (item != null) {
				if (item.isStackable() || noted)
					amount = packetsIncoming.get32();
				else
					amount = 1;
				inventoryItems[i] = new Item();
				inventoryItems[i].setItemDef(itemID);
				inventoryItems[i].setAmount(amount);
				inventoryItems[i].setNoted(noted);
			}
		}
		//The server uses 3 more slots than the client
		for (int i = 0; i < equipmentItems.length + 3; i++) {
			itemBytes[0] = packetsIncoming.getByte();
			if (itemBytes[0] == -1)
				continue;
			itemBytes[1] = packetsIncoming.getByte();
			itemID = (((int) itemBytes[0] << 8) & 0xFF00) | (int) itemBytes[1] & 0xFF;
			item = EntityHandler.getItemDef(itemID);
			if (item != null) {
				if (item.isStackable())
					amount = packetsIncoming.get32();
				else
					amount = 1;
				int equipslot = i;
				if (equipslot == 5)
					equipslot = 0;
				else if (equipslot == 6)
					equipslot = 1;
				else if (equipslot == 7)
					equipslot = 2;
				else if (equipslot > 7)
					equipslot -= 3;
				equipmentItems[equipslot] = new Item();
				equipmentItems[equipslot].setItemDef(itemID);
				equipmentItems[equipslot].setAmount(amount);
			}
		}
		mc.getBank().updatePreset(slot, inventoryItems, equipmentItems);
	}
}
