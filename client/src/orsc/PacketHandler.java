package orsc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

import com.openrsc.interfaces.misc.clan.Clan;
import com.openrsc.client.model.Sprite;

import orsc.buffers.RSBufferUtils;
import orsc.buffers.RSBuffer_Bits;
import orsc.enumerations.MessageType;
import orsc.enumerations.ORSCharacterDirection;
import orsc.graphics.gui.KillAnnouncer;
import orsc.graphics.gui.SocialLists;
import orsc.graphics.three.RSModel;
import orsc.net.Network_Socket;
import orsc.util.FastMath;
import orsc.util.GenUtil;
import orsc.util.StringUtil;


public class PacketHandler {

	private Network_Socket clientStream;
	private final RSBuffer_Bits packetsIncoming = new RSBuffer_Bits(30000);
	private mudclient mc;

	public PacketHandler (mudclient mc) {
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
			System.out.println("Opcode: " + opcode + " Length: " + length);

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
			else if (opcode == 158) updateChatBlockSettings();

			// Receive Private Message
			else if (opcode == 120) receivePrivateMessage();

			// Send Private Message
			else if (opcode == 87) sendPrivateMessage();

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
			else if (opcode == 53) updateInventoryItems();

			// Show Walls
			else if (opcode == 91) showWalls(length);

			// Show NPCs
			else if (opcode == 79) showNPCs(length);

			// NPC Quest Message
			else if (opcode == 104) updateQuestNPCStatus();

			// Show Options Menu
			else if (opcode == 245) showOptionsMenu();

			// Load Area
			else if (opcode == 25) loadArea();

			// Load Stats and Experience
			else if (opcode == 156) {
				loadStats();
				loadExperience();
				loadQuestPoints();

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

			// Quest Stage Update
			else if (opcode == 5) updateQuestStage();

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
			else if (opcode == 90) updateInventory();

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
			else if (opcode == 84) mc.setIsSleeping(false);

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

			// Server Message Input (Second Style)
			else if (opcode == 89) showServerMessageDialogTwo();

			// Teleport Bubbles
			else if (opcode == 36) drawTeleportBubbles();

			// Duel Acceptance
			else if (opcode == 253) duelOpponentDecision();

			// Drop Item
			else if (opcode == 123) dropItem();

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

			// Draw Ground Items
			else if (opcode == 99) drawGroundItems(length);

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
		if (actionType == 2) {
			mc.resetBatchProgressBar();
		}
		if (actionType == 3) {
			int repeat = packetsIncoming.getByte() & 0xff;
			mc.updateBatchProgressBar(repeat);
		}
	}

	private void displayBankPin() {
		int action = packetsIncoming.getByte();
		if (action == 0) {
			mc.showBankPinInterface();
		} else if (action == 1) {
			mc.hideBankPinInterface();
		}
	}

	private void setIronmanOptions() {
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
			mc.getOnlineList().addOnlineUser(packetsIncoming.readString(), packetsIncoming.getByte());
		}
		mc.getOnlineList().setVisible(true);
	}

	private void fishingTrawlerUpdate() {
		int action=packetsIncoming.getByte();
		switch(action){
			case 0:
				mc.getFishingTrawlerInterface().show();
				break;
			case 1:
				mc.getFishingTrawlerInterface().setVariables(packetsIncoming.getShort(),packetsIncoming.getShort(),
								packetsIncoming.getByte(),packetsIncoming.getByte()==1);
				break;
			case 2:
				mc.getFishingTrawlerInterface().hide();
				break;
		}
	}

	private void updateClan() {
		int actionType = packetsIncoming.getByte();
		Clan clan = new Clan(mc);
		switch (actionType) {
			case 0: // Send clan
				clan.setClanName(packetsIncoming.readString());
				clan.setClanTag(packetsIncoming.readString());
				clan.setClanLeaderUsername(packetsIncoming.readString());
				boolean isLeader = packetsIncoming.getByte() == 1;
				clan.setClanLeader(isLeader);
				SocialLists.clanListCount = packetsIncoming.getByte();
				for (int id = 0; id < SocialLists.clanListCount; id++) {
					clan.username[id] = packetsIncoming.readString();
					clan.clanRank[id] = packetsIncoming.getByte();
					clan.onlineClanMember[id] = packetsIncoming.getByte();
				}
				clan.putClan(true);
				break;
			case 1: // Leave clan
				clan.putClan(false);
				clan.update();
				break;
			case 2: // Sent invitation
				clan.getClanInterface().initializeInvite(packetsIncoming.readString(), packetsIncoming.readString());
				break;
			case 3: // Settings
				clan.setClanSetting(0, packetsIncoming.getByte());
				clan.setClanSetting(1, packetsIncoming.getByte());
				clan.setClanSetting(2, packetsIncoming.getByte());
				clan.allowed[0] = packetsIncoming.getByte() == 1;
				clan.allowed[1] = packetsIncoming.getByte() == 1;
				break;
			case 4: // Clan search visual
				clan.getClanInterface().resetClans();
				int clanCount = packetsIncoming.getShort();
				for (int i = 0; i < clanCount; i++) {
					int clanID = packetsIncoming.getShort();
					String clanName = packetsIncoming.readString();
					String clanTag = packetsIncoming.readString();
					int members = packetsIncoming.getByte();
					int canJoin = packetsIncoming.getByte();
					int clanPoints = packetsIncoming.get32();
					int clanRank = packetsIncoming.getShort();
					clan.getClanInterface().addClan(clanID, clanName, clanTag, members, canJoin, clanPoints, clanRank);
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
		int crown = packetsIncoming.getUnsignedByte();
		MessageType type = MessageType.lookup(packetsIncoming.getUnsignedByte());
		int var5 = packetsIncoming.getUnsignedByte();
		String message = packetsIncoming.readString();
		String sender = null;
		String clan = null;
		String color = null;
		if ((var5 & 1) != 0) {
			sender = packetsIncoming.readString();
		}

		if ((1 & var5) != 0) {
			clan = packetsIncoming.readString();
		}

		if ((var5 & 2) != 0) {
			color = packetsIncoming.readString();
		}

		mc.showMessage(false, sender, message, type, crown, clan, color);
	}

	private void sendConnectionMessage() {
		String currentName = packetsIncoming.readString();
		String formerName = packetsIncoming.readString();
		int arg = packetsIncoming.getUnsignedByte();
		boolean rename = (arg & 1) != 0;
		boolean online = (4 & arg) != 0;
		String var9 = null;
		if (online) {
			var9 = packetsIncoming.readString();
		}
		for (int i = 0; i < SocialLists.friendListCount; ++i) {
			if (!rename) {
				if (SocialLists.friendList[i].equals(currentName)) {
					if (SocialLists.friendListArgS[i] == null && online) {
						mc.showMessage(false, (String) null, currentName + " has logged in",
										MessageType.FRIEND_STATUS, 0, (String) null, (String) null);
					}

					if (null != SocialLists.friendListArgS[i] && !online) {
						mc.showMessage(false, (String) null, currentName + " has logged out",
										MessageType.FRIEND_STATUS, 0, (String) null, (String) null);
					}

					SocialLists.friendListOld[i] = formerName;
					SocialLists.friendListArgS[i] = var9;
					SocialLists.friendListArg[i] = arg;
					mc.sortOnlineFriendsList();
					return;
				}
			} else if (SocialLists.friendList[i].equals(formerName)) {
				if (SocialLists.friendListArgS[i] == null && online) {
					mc.showMessage(false, (String) null, currentName + " has logged in",
									MessageType.FRIEND_STATUS, 0, (String) null, (String) null);
				}

				if (SocialLists.friendListArgS[i] != null && !online) {
					mc.showMessage(false, (String) null, currentName + " has logged out",
									MessageType.FRIEND_STATUS, 0, (String) null, (String) null);
				}

				SocialLists.friendList[i] = currentName;
				SocialLists.friendListOld[i] = formerName;
				SocialLists.friendListArgS[i] = var9;
				SocialLists.friendListArg[i] = arg;
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
		SocialLists.friendListArgS[SocialLists.friendListCount] = var9;
		SocialLists.friendListArg[SocialLists.friendListCount] = arg;
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
		int icon = packetsIncoming.getUnsignedByte();
		String message = RSBufferUtils.getEncryptedString(packetsIncoming);
		mc.showMessage(icon == 2, sender, message, MessageType.PRIVATE_RECIEVE, icon, formerName,
						(String) null);
	}

	private void sendPrivateMessage() {
		String var13 = packetsIncoming.readString();
		String var14 = RSBufferUtils.getEncryptedString(packetsIncoming);
		mc.showMessage(false, var13, var14, MessageType.PRIVATE_SEND, 0, var13, (String) null);
	}

	private void setServerConfiguration() {
		Properties props = new Properties();
		String serverName;
		int playerLevelLimit, spawnAuctionNpcs, spawnIronManNpcs;
		int showFloatingNametags, wantClans, wantKillFeed, fogToggle;
		int groundItemToggle, autoMessageSwitchToggle, batchProgression;
		int sideMenuToggle, inventoryCountToggle, zoomViewToggle;
		int menuCombatStyleToggle, fightmodeSelectorToggle, experienceCounterToggle;
		int experienceDropsToggle, itemsOnDeathMenu, showRoofToggle;
		int wantGlobalChat, wantSkillMenus, wantQuestMenus;
		int wantExperienceElixirs, wantKeyboardShortcuts;
		int wantCustomBanks, wantBankPins, customFiremaking;
		int wantDropX, wantExpInfo, wantWoodcuttingGuild;
		int wantDecanting, wantCertsToBank;

		if (!mc.gotInitialConfigs) {
			serverName = this.getClientStream().readString();
			playerLevelLimit = this.getClientStream().getUnsignedByte();
			spawnAuctionNpcs = this.getClientStream().getUnsignedByte();
			spawnIronManNpcs = this.getClientStream().getUnsignedByte();
			showFloatingNametags = this.getClientStream().getUnsignedByte();
			wantClans = this.getClientStream().getUnsignedByte();
			wantKillFeed = this.getClientStream().getUnsignedByte();
			fogToggle = this.getClientStream().getUnsignedByte();
			groundItemToggle = this.getClientStream().getUnsignedByte();
			autoMessageSwitchToggle = this.getClientStream().getUnsignedByte();
			batchProgression = this.getClientStream().getUnsignedByte();
			sideMenuToggle = this.getClientStream().getUnsignedByte();
			inventoryCountToggle = this.getClientStream().getUnsignedByte();
			zoomViewToggle = this.getClientStream().getUnsignedByte();
			menuCombatStyleToggle = this.getClientStream().getUnsignedByte();
			fightmodeSelectorToggle = this.getClientStream().getUnsignedByte();
			experienceCounterToggle = this.getClientStream().getUnsignedByte();
			experienceDropsToggle = this.getClientStream().getUnsignedByte();
			itemsOnDeathMenu = this.getClientStream().getUnsignedByte();
			showRoofToggle = this.getClientStream().getUnsignedByte();
			wantGlobalChat = this.getClientStream().getUnsignedByte();
			wantSkillMenus = this.getClientStream().getUnsignedByte();
			wantQuestMenus = this.getClientStream().getUnsignedByte();
			wantExperienceElixirs = this.getClientStream().getUnsignedByte();
			wantKeyboardShortcuts = this.getClientStream().getUnsignedByte();
			wantCustomBanks = this.getClientStream().getUnsignedByte();
			wantBankPins = this.getClientStream().getUnsignedByte();
			customFiremaking = this.getClientStream().getUnsignedByte();
			wantDropX = this.getClientStream().getUnsignedByte();
			wantExpInfo = this.getClientStream().getUnsignedByte();
			wantWoodcuttingGuild = this.getClientStream().getUnsignedByte();
			wantDecanting = this.getClientStream().getUnsignedByte();
			wantCertsToBank = this.getClientStream().getUnsignedByte();
		} else {
			serverName = packetsIncoming.readString();
			playerLevelLimit = packetsIncoming.getUnsignedByte();
			spawnAuctionNpcs = packetsIncoming.getUnsignedByte();
			spawnIronManNpcs = packetsIncoming.getUnsignedByte();
			showFloatingNametags = packetsIncoming.getUnsignedByte();
			wantClans = packetsIncoming.getUnsignedByte();
			wantKillFeed = packetsIncoming.getUnsignedByte();
			fogToggle = packetsIncoming.getUnsignedByte();
			groundItemToggle = packetsIncoming.getUnsignedByte();
			autoMessageSwitchToggle = packetsIncoming.getUnsignedByte();
			batchProgression = packetsIncoming.getUnsignedByte();
			sideMenuToggle = packetsIncoming.getUnsignedByte();
			inventoryCountToggle = packetsIncoming.getUnsignedByte();
			zoomViewToggle = packetsIncoming.getUnsignedByte();
			menuCombatStyleToggle = packetsIncoming.getUnsignedByte();
			fightmodeSelectorToggle = packetsIncoming.getUnsignedByte();
			experienceCounterToggle = packetsIncoming.getUnsignedByte();
			experienceDropsToggle = packetsIncoming.getUnsignedByte();
			itemsOnDeathMenu = packetsIncoming.getUnsignedByte();
			showRoofToggle = packetsIncoming.getUnsignedByte();
			wantGlobalChat = packetsIncoming.getUnsignedByte();
			wantSkillMenus = packetsIncoming.getUnsignedByte();
			wantQuestMenus = packetsIncoming.getUnsignedByte();
			wantExperienceElixirs = packetsIncoming.getUnsignedByte();
			wantKeyboardShortcuts = packetsIncoming.getUnsignedByte();
			wantCustomBanks = packetsIncoming.getUnsignedByte();
			wantBankPins = packetsIncoming.getUnsignedByte();
			customFiremaking = packetsIncoming.getUnsignedByte();
			wantDropX = packetsIncoming.getUnsignedByte();
			wantExpInfo = packetsIncoming.getUnsignedByte();
			wantWoodcuttingGuild = packetsIncoming.getUnsignedByte();
			wantDecanting = packetsIncoming.getUnsignedByte();
			wantCertsToBank = packetsIncoming.getUnsignedByte();
		}

		if (mc.DEBUG) {
			System.out.println(
				"SERVER_NAME " + serverName +
				"\nS_PLAYER_LEVEL_LIMIT " + Integer.toString(playerLevelLimit) +
				"\nS_SPAWN_AUCTION_NPCS " + spawnAuctionNpcs +
				"\nS_SPAWN_IRON_MAN_NPCS " + spawnIronManNpcs +
				"\nS_SHOW_FLOATING_NAMETAGS " + showFloatingNametags +
				"\nS_WANT_CLANS " + wantClans +
				"\nS_WANT_KILL_FEED " + wantKillFeed +
				"\nS_FOG_TOGGLE " + fogToggle +
				"\nS_GROUND_ITEM_TOGGLE " + groundItemToggle +
				"\nS_AUTO_MESSAGE_SWITCH_TOGGLE " + autoMessageSwitchToggle +
				"\nS_BATCH_PROGRESSION " + batchProgression +
				"\nS_SIDE_MENU_TOGGLE " + sideMenuToggle +
				"\nS_INVENTORY_COUNT_TOGGLE " + inventoryCountToggle +
				"\nS_ZOOM_VIEW_TOGGLE " + zoomViewToggle +
				"\nS_MENU_COMBAT_STYLE_TOGGLE " + menuCombatStyleToggle +
				"\nS_FIGHTMODE_SELECTOR_TOGGLE " + fightmodeSelectorToggle +
				"\nS_EXPERIENCE_COUNTER_TOGGLE " + experienceCounterToggle +
				"\nS_EXPERIENCE_DROPS_TOGGLE " + experienceDropsToggle +
				"\nS_ITEMS_ON_DEATH_MENU " + itemsOnDeathMenu +
				"\nS_SHOW_ROOF_TOGGLE " + showRoofToggle +
				"\nS_WANT_GLOBAL_CHAT " + wantGlobalChat +
				"\nS_WANT_SKILL_MENUS " + wantSkillMenus +
				"\nS_WANT_QUEST_MENUS " + wantQuestMenus +
				"\nS_WANT_EXPERIENCE_ELIXIRS " + wantExperienceElixirs +
				"\nS_WANT_KEYBOARD_SHORTCUTS " + wantKeyboardShortcuts +
				"\nS_WANT_CUSTOM_BANKS " + wantCustomBanks +
				"\nS_WANT_BANK_PINS " + wantBankPins +
				"\nS_CUSTOM_FIREMAKING " + customFiremaking +
				"\nS_WANT_DROP_X " + wantDropX +
				"\nS_WANT_EXP_INFO " + wantExpInfo +
				"\nS_WANT_WOODCUTTING_GUILD " + wantWoodcuttingGuild +
				"\nS_WANT_DECANTING " + wantDecanting +
				"\nS_WANT_CERTS_TO_BANK " + wantCertsToBank
			);
		}

		props.setProperty("SERVER_NAME", serverName);
		props.setProperty("S_PLAYER_LEVEL_LIMIT", Integer.toString(playerLevelLimit));
		props.setProperty("S_SPAWN_AUCTION_NPCS", spawnAuctionNpcs == 1 ? "true" : "false");
		props.setProperty("S_SPAWN_IRON_MAN_NPCS", spawnIronManNpcs == 1 ? "true" : "false");
		props.setProperty("S_SHOW_FLOATING_NAMETAGS", showFloatingNametags == 1 ? "true" : "false");
		props.setProperty("S_WANT_CLANS", wantClans == 1 ? "true" : "false");
		props.setProperty("S_WANT_KILL_FEED", wantKillFeed == 1 ? "true" : "false");
		props.setProperty("S_FOG_TOGGLE", fogToggle == 1 ? "true" : "false");
		props.setProperty("S_GROUND_ITEM_TOGGLE", groundItemToggle == 1 ? "true" : "false");
		props.setProperty("S_AUTO_MESSAGE_SWITCH_TOGGLE", autoMessageSwitchToggle == 1 ? "true" : "false");
		props.setProperty("S_BATCH_PROGRESSION", batchProgression == 1 ? "true" : "false");
		props.setProperty("S_SIDE_MENU_TOGGLE", sideMenuToggle == 1 ? "true" : "false");
		props.setProperty("S_INVENTORY_COUNT_TOGGLE", inventoryCountToggle == 1 ? "true" : "false");
		props.setProperty("S_ZOOM_VIEW_TOGGLE", zoomViewToggle == 1 ? "true" : "false");
		props.setProperty("S_MENU_COMBAT_STYLE_TOGGLE", menuCombatStyleToggle == 1 ? "true" : "false");
		props.setProperty("S_FIGHTMODE_SELECTOR_TOGGLE", fightmodeSelectorToggle == 1 ? "true" : "false");
		props.setProperty("S_EXPERIENCE_COUNTER_TOGGLE", experienceCounterToggle == 1 ? "true" : "false");
		props.setProperty("S_EXPERIENCE_DROPS_TOGGLE", experienceDropsToggle == 1 ? "true" : "false");
		props.setProperty("S_ITEMS_ON_DEATH_MENU", itemsOnDeathMenu == 1 ? "true" : "false");
		props.setProperty("S_SHOW_ROOF_TOGGLE", showRoofToggle == 1 ? "true" : "false");
		props.setProperty("S_WANT_GLOBAL_CHAT", wantGlobalChat == 1 ? "true" : "false");
		props.setProperty("S_WANT_SKILL_MENUS", wantSkillMenus == 1 ? "true" : "false");
		props.setProperty("S_WANT_QUEST_MENUS", wantQuestMenus == 1 ? "true" : "false");
		props.setProperty("S_WANT_EXPERIENCE_ELIXIRS", wantExperienceElixirs == 1 ? "true" : "false");
		props.setProperty("S_WANT_KEYBOARD_SHORTCUTS", wantKeyboardShortcuts == 1 ? "true" : "false");
		props.setProperty("S_WANT_CUSTOM_BANKS", wantCustomBanks == 1 ? "true" : "false");
		props.setProperty("S_WANT_BANK_PINS", wantBankPins == 1 ? "true" : "false");
		props.setProperty("S_CUSTOM_FIREMAKING", customFiremaking == 1 ? "true" : "false");
		props.setProperty("S_WANT_DROP_X", wantDropX == 1 ? "true" : "false");
		props.setProperty("S_WANT_EXP_INFO", wantExpInfo == 1 ? "true" : "false");
		props.setProperty("S_WANT_WOODCUTTING_GUILD", wantWoodcuttingGuild == 1 ? "true" : "false");
		props.setProperty("S_WANT_DECANTING", wantDecanting == 1 ? "true" : "false");
		props.setProperty("S_WANT_CERTS_TO_BANK", wantCertsToBank == 1 ? "true" : "false");

		Config.updateServerConfiguration(props);

		mc.authenticSettings = !(
						Config.S_WANT_CLANS || Config.S_WANT_KILL_FEED
										|| Config.S_FOG_TOGGLE || Config.S_GROUND_ITEM_TOGGLE
										|| Config.S_AUTO_MESSAGE_SWITCH_TOGGLE || Config.S_BATCH_PROGRESSION
										|| Config.S_SIDE_MENU_TOGGLE || Config.S_INVENTORY_COUNT_TOGGLE
										|| Config.S_ZOOM_VIEW_TOGGLE || Config.S_MENU_COMBAT_STYLE_TOGGLE
										|| Config.S_FIGHTMODE_SELECTOR_TOGGLE || Config.S_SHOW_ROOF_TOGGLE
										|| Config.S_EXPERIENCE_COUNTER_TOGGLE || Config.S_WANT_GLOBAL_CHAT
										|| Config.S_EXPERIENCE_DROPS_TOGGLE || Config.S_ITEMS_ON_DEATH_MENU);


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
		int currentX = 64 + mc.getLocalPlayerX() * tileSize;
		int currentZ = mc.getLocalPlayerZ() * tileSize + 64;
		mc.setPlayerCount(0);
		if (needNextRegion) {
			mc.getLocalPlayer().movingStep = 0;
			mc.getLocalPlayer().waypointCurrent = 0;
			mc.getLocalPlayer().currentX = mc.getLocalPlayer().waypointsX[0] = currentX;
			mc.getLocalPlayer().currentZ = mc.getLocalPlayer().waypointsZ[0] = currentZ;
		}

		mc.setLocalPlayer(
			mc.createPlayer(currentZ, mc.getM_Zc(), currentX, 1,
					ORSCharacterDirection.lookup(direction)
			)
		);

		int dir = packetsIncoming.getBitMask(8);

		for (int var9 = 0; dir > var9; ++var9) {
			ORSCharacter var34 = mc.getKnownPlayer(var9 + 1);
			int needsUpdate = packetsIncoming.getBitMask(1);
			if (needsUpdate != 0) {
				int updateType = packetsIncoming.getBitMask(1);
				if (updateType != 0) {
					int needsNextSprite = packetsIncoming.getBitMask(2);
					if (needsNextSprite == 3) {
						continue;
					}
					var34.animationNext = packetsIncoming.getBitMask(2) + (needsNextSprite << 2);
				} else {
					int modelIndex = packetsIncoming.getBitMask(3);
					int var33 = var34.waypointCurrent;
					int var15 = var34.waypointsX[var33];
					int var16 = var34.waypointsZ[var33];
					if (modelIndex == 2 || modelIndex == 1 || modelIndex == 3) {
						var15 += tileSize;
					}

					if (modelIndex == 6 || modelIndex == 5 || modelIndex == 7) {
						var15 -= tileSize;
					}

					if (modelIndex == 4 || modelIndex == 3 || modelIndex == 5) {
						var16 += tileSize;
					}
					var34.animationNext = modelIndex;
					if (modelIndex == 0 || modelIndex == 1 || modelIndex == 7) {
						var16 -= tileSize;
					}
					var34.waypointCurrent = var33 = (1 + var33) % 10;
					var34.waypointsX[var33] = var15;
					var34.waypointsZ[var33] = var16;
				}
			}

			mc.setPlayer(mc.getPlayerCount(), var34);
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
					if (dir != 0 && dir != 4) {
						xSize = com.openrsc.client.entityhandling.EntityHandler.getObjectDef(id).getHeight();// EntityHandler.getObjectDef(id).getHeight();
						zSize = com.openrsc.client.entityhandling.EntityHandler.getObjectDef(id).getWidth();// EntityHandler.getObjectDef(id).getWidth();
					} else {
						zSize = com.openrsc.client.entityhandling.EntityHandler.getObjectDef(id).getHeight();// EntityHandler.getObjectDef(id).getHeight();
						xSize = com.openrsc.client.entityhandling.EntityHandler.getObjectDef(id).getWidth();// EntityHandler.getObjectDef(id).getWidth();
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

	private void updateInventoryItems() {
		mc.setInventoryItemCount(packetsIncoming.getUnsignedByte());
		for (int i = 0; i < mc.getInventoryItemCount(); ++i) {
			int itemID = packetsIncoming.getShort();
			mc.setInventoryItemID(i, itemID);
			mc.setInventoryItemEquipped(i, packetsIncoming.getByte());
			if (com.openrsc.client.entityhandling.EntityHandler.getItemDef(itemID).isStackable()) {
				mc.setInventoryItemSize(i, packetsIncoming.get32());
			} else {
				mc.setInventoryItemSize(i, 1);
			}
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

		int var10, var9, var11, dir, var19;
		for (var19 = 0; count > var19; ++var19) {
			ORSCharacter npc = mc.getNpcFromCache(var19);
			int var7 = packetsIncoming.getBitMask(1);
			if (var7 != 0) {
				dir = packetsIncoming.getBitMask(1);
				if (dir != 0) {
					int nextSpriteOffset = packetsIncoming.getBitMask(2);
					if (nextSpriteOffset == 3) {
						continue;
					}
					npc.animationNext = (nextSpriteOffset << 2)
									+ packetsIncoming.getBitMask(2);
				} else {
					var9 = packetsIncoming.getBitMask(3);
					var10 = npc.waypointCurrent;
					var11 = npc.waypointsX[var10];
					if (var9 == 2 || var9 == 1 || var9 == 3) {
						var11 += tileSize;
					}

					int var31 = npc.waypointsZ[var10];
					if (var9 == 6 || var9 == 5 || var9 == 7) {
						var11 -= tileSize;
					}

					if (var9 == 4 || var9 == 3 || var9 == 5) {
						var31 += tileSize;
					}

					if (var9 == 0 || var9 == 1 || var9 == 7) {
						var31 -= tileSize;
					}

					npc.waypointCurrent = var10 = (var10 - -1) % 10;
					npc.animationNext = var9;
					npc.waypointsX[var10] = var11;
					npc.waypointsZ[var10] = var31;
				}
			}

			mc.setNpc(mc.getNpcCount(), npc);
			mc.setNpcCount(mc.getNpcCount() + 1);
		}

		while (length * 8 > packetsIncoming.getBitHead() + 34) {
			var19 = packetsIncoming.getBitMask(12);
			int var6 = packetsIncoming.getBitMask(6);
			if (var6 > 31) {
				var6 -= 64;
			}
			int var7 = packetsIncoming.getBitMask(6);
			if (var7 > 31) {
				var7 -= 64;
			}
			dir = packetsIncoming.getBitMask(4);
			var9 = (var6 + mc.getLocalPlayerX()) * tileSize + 64;
			var10 = (var7 + mc.getLocalPlayerZ()) * tileSize + 64;
			var11 = packetsIncoming.getBitMask(10);
			mc.createNpc(dir, var11, var9, var10, var19);
		}

		packetsIncoming.endBitAccess();
	}

	private void updateQuestNPCStatus() {
		int var4 = packetsIncoming.getShort();

		for (int var19 = 0; var4 > var19; ++var19) {
			int var6 = packetsIncoming.getShort();
			ORSCharacter var22 = mc.getNpcFromServer(var6);
			int updateType = packetsIncoming.getUnsignedByte();
			if (updateType == 1) {
				int var9 = packetsIncoming.getShort();
				if (var22 != null) {
					String message = packetsIncoming.readString();
					var22.messageTimeout = 150;
					var22.message = message;
					if (mc.getLocalPlayer().serverIndex == var9) {
						mc.showMessage(false, (String) null,
										com.openrsc.client.entityhandling.EntityHandler.getNpcDef(var22.npcId).getName() + ": "
														+ var22.message,
										MessageType.QUEST, 0, (String) null, "@yel@");
					}
				}
			} else if (updateType == 2) {
				int var9 = packetsIncoming.getUnsignedByte();
				int var10 = packetsIncoming.getUnsignedByte();
				int var11 = packetsIncoming.getUnsignedByte();

				if (null != var22) {
					var22.damageTaken = var9;
					var22.healthMax = var11;
					var22.combatTimeout = 200;
					var22.healthCurrent = var10;
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
		mc.setM_Zc(packetsIncoming.getShort());
		mc.setWorldOffsetX(packetsIncoming.getShort());
		mc.setWorldOffsetZ(packetsIncoming.getShort());
		mc.setRequestedPlane(packetsIncoming.getShort());
		mc.setM_rc(packetsIncoming.getShort());
		mc.setWorldOffsetZ(mc.getWorldOffsetZ() - (mc.getRequestedPlane() * mc.getM_rc()));
	}

	private void loadStats() {
		for (int stat = 0; stat < 18; ++stat) {
			mc.setPlayerStatCurrent(stat, packetsIncoming.getUnsignedByte());
		}

		for (int stat = 0; stat < 18; ++stat) {
			mc.setPlayerStatBase(stat, packetsIncoming.getUnsignedByte());
		}
	}

	private void loadExperience() {
		for (int skill = 0; skill < 18; ++skill) {
			mc.setPlayerExperience(skill, packetsIncoming.get32() / 4);
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

			int count = 0;
			for (int j = 0; j < mc.getGroundItemCount(); ++j) {
				int var10 = (mc.getGroundItemX(j) >> 3) - x;
				int var11 = (mc.getGroundItemZ(j) >> 3) - z;
				if (var10 != 0 || var11 != 0) {
					if (count != j) {
						mc.setGroundItemX(count, mc.getGroundItemX(j));
						mc.setGroundItemZ(count, mc.getGroundItemZ(j));
						mc.setGroundItemID(count, mc.getGroundItemID(j));
						mc.setGroundItemHeight(count, mc.getGroundItemHeight(j));
					}

					++count;
				}
			}

			mc.setGroundItemCount(count);

			// Game Object Counts
			count = 0;
			for (int j = 0; j < mc.getGameObjectInstanceCount(); ++j) {
				int var10 = (mc.getGameObjectInstanceX(j) >> 3) - x;
				int var11 = (mc.getGameObjectInstanceZ(j) >> 3) - z;
				if (var10 == 0 && var11 == 0) {
					mc.getScene().removeModel(mc.getGameObjectInstanceModel(j));
					mc.getWorld().removeGameObject_CollisonFlags(
									mc.getGameObjectInstanceID(j),
									mc.getGameObjectInstanceX(j),
									mc.getGameObjectInstanceZ(j));
				} else {
					if (j != count) {
						mc.setGameObjectInstanceModel(count, mc.getGameObjectInstanceModel(j));
						mc.setGameObjectInstanceX(count, mc.getGameObjectInstanceX(j));
						mc.setGameObjectInstanceZ(count, mc.getGameObjectInstanceZ(j));
						mc.setGameObjectInstanceID(count, mc.getGameObjectInstanceID(j));
						mc.setGameObjectInstanceDir(count, mc.getGameObjectInstanceDir(j));
					}

					++count;
				}
			}

			mc.setGameObjectInstanceCount(count);

			// Wall Object Counts
			count = 0;
			for (int n = 0; mc.getWallObjectInstanceCount() > n; ++n) {
				int wallX = (mc.getWallObjectInstanceX(n) >> 3) - x;
				int wallZ = (mc.getWallObjectInstanceZ(n) >> 3) - z;
				if (wallX == 0 && wallZ == 0) {
					mc.getScene().removeModel(mc.getWallObjectInstanceModel(n));
					mc.getWorld().removeWallObject_CollisionFlags(true,
									mc.getWallObjectInstanceDir(n),
									mc.getWallObjectInstanceZ(n),
									mc.getWallObjectInstanceX(n),
									mc.getWallObjectInstanceID(n));
				} else {
					if (n != count) {
						mc.setWallObjectInstanceModel(count, mc.getWallObjectInstanceModel(n));
						mc.setWallObjectInstanceX(count, mc.getWallObjectInstanceX(n));
						mc.setWallObjectInstanceZ(count, mc.getWallObjectInstanceZ(n));
						mc.setWallObjectInstanceDir(count, mc.getWallObjectInstanceDir(n));
						mc.setWallObjectInstanceID(count, mc.getWallObjectInstanceID(n));
					}

					++count;
				}
			}

			mc.setWallObjectInstanceCount(count);
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
		mc.setAdminRights(packetsIncoming.getUnsignedByte() == 1);
		mc.setOptionCameraModeAuto(packetsIncoming.getUnsignedByte() == 1);
		mc.setOptionMouseButtonOne(packetsIncoming.getUnsignedByte() == 1);
		mc.setOptionSoundDisabled(packetsIncoming.getUnsignedByte() == 1);
		mc.setCombatStyle(packetsIncoming.getUnsignedByte());
		mc.setSettingsBlockGlobal(packetsIncoming.getUnsignedByte());
		mc.setClanInviteBlockSetting(packetsIncoming.getUnsignedByte() == 1);
	}

	private void togglePrayer(int length) {
		for (int i = 0; length - 1 > i; ++i) {
			boolean enabled = packetsIncoming.getByte() == 1;
			if (!mc.checkPrayerOn(i) && enabled) {
				mc.playSoundFile((String) "prayeron");
			}
			if (mc.checkPrayerOn(i) && !enabled) {
				mc.playSoundFile((String) "prayeroff");
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
	}

	private void showBank() {
		mc.setShowDialogBank(true);
		mc.setNewBankItemCount(packetsIncoming.getShort());
		mc.setBankItemsMax(packetsIncoming.getShort());
		mc.getBank().resetBank();
		for (int slot = 0; slot < mc.getNewBankItemCount(); ++slot) {
			mc.getBank().addBank(slot, packetsIncoming.getShort(), packetsIncoming.get32());
		}
	}

	private void updateIndividualExperience() {
		int skill = packetsIncoming.getUnsignedByte();
		mc.setPlayerExperience(skill, packetsIncoming.get32() / 4);
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
			mc.setTradeRecipientConfirmItems(var4, packetsIncoming.getShort());
			mc.setTradeRecipientConfirmItemCount(var4, packetsIncoming.get32());
		}

		mc.setTradeConfirmItemsCount(packetsIncoming.getUnsignedByte());

		for (int var4 = 0; var4 < mc.getTradeConfirmItemsCount(); ++var4) {
			mc.setTradeConfirmItems(var4, packetsIncoming.getShort());
			mc.setTradeConfirmItemsCount1(var4, packetsIncoming.get32());
		}
	}

	private void showDuelItems() {
		mc.setDuelOffsetOpponentItemCount(packetsIncoming.getUnsignedByte());

		for (int var4 = 0; mc.getDuelOffsetOpponentItemCount() > var4; ++var4) {
			mc.setDuelOpponentItemId(var4, packetsIncoming.getShort());
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

	private void updateInventory() {
		int slot = packetsIncoming.getUnsignedByte();
		int itemID = packetsIncoming.getShort();
		int stackSize = 1;
		if (com.openrsc.client.entityhandling.EntityHandler.getItemDef(itemID & 32767).isStackable()) {
			stackSize = packetsIncoming.get32();
		}
		mc.setInventoryItemID(slot, FastMath.bitwiseAnd(itemID, 32767));
		mc.setInventoryItemEquipped(slot, itemID / '\u8000');
		mc.setInventoryItemSize(slot, stackSize);
		if (slot >= mc.getInventoryItemCount()) {
			mc.setInventoryItemCount(1 + slot);
		}
	}

	private void updateExperience() {
		int skill = packetsIncoming.getUnsignedByte();
		mc.setRecentSkill(skill);
		int oldXp = mc.getPlayerExperience(skill);
		int oldLvl = mc.getPlayerStatBase(skill);
		mc.setPlayerStatCurrent(skill, packetsIncoming.getUnsignedByte());
		mc.setPlayerStatBase(skill, packetsIncoming.getUnsignedByte());
		mc.setPlayerExperience(skill, packetsIncoming.get32() / 4);

		int receivedXp = mc.getPlayerExperience(skill) - oldXp;
		receivedXp = receivedXp < 0 ? 0 : receivedXp;
		mc.setPlayerStatXpGained(skill, (long)mc.getPlayerStatXpGained(skill) + receivedXp);
		if (mc.getXpGainedStartTime(skill) == 0) {
			mc.setXpGainedStartTime(skill, System.currentTimeMillis());
		}
		mc.setPlayerXpGainedTotal(mc.getPlayerXpGainedTotal() + (long)receivedXp);
		if (mc.totalXpGainedStartTime == 0) {
			mc.totalXpGainedStartTime = System.currentTimeMillis();
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
		mc.setDuelOpponentItemsCount(packetsIncoming.getUnsignedByte());
		for (int var4 = 0; var4 < mc.getDuelOpponentItemsCount(); ++var4) {
			mc.setDuelOpponentItems(var4, packetsIncoming.getShort());
			mc.setDuelOpponentItemCounts(var4, packetsIncoming.get32());
		}

		mc.setDuelItemsCount(packetsIncoming.getUnsignedByte());

		for (int var4 = 0; mc.getDuelItemsCount() > var4; ++var4) {
			mc.setDuelItems(var4, packetsIncoming.getShort());
			mc.setDuelItemCounts(var4, packetsIncoming.get32());
		}

		mc.setDuelOptionRetreat(packetsIncoming.getUnsignedByte());
		mc.setDuelOptionMagic(packetsIncoming.getUnsignedByte());
		mc.setDuelOptionPrayer(packetsIncoming.getUnsignedByte());
		mc.setDuelOptionWeapons(packetsIncoming.getUnsignedByte());
	}

	private void playSound() {
		String filename = packetsIncoming.readString();
		mc.playSoundFile((String) filename);
	}

	private void showLoginDialog() {
		if (!mc.getWelcomeScreenShown()) {
			mc.setWelcomeLastLoggedInIp(packetsIncoming.readString());
			mc.setWelcomeLastLoggedInDays(packetsIncoming.getShort());
			//this.welcomeUnreadMessages = packetsIncoming.getShort();
			mc.setShowDialogMessage(true);
			mc.setWelcomeLastLoggedInHost(null);
			mc.setWelcomeScreenShown(true);
		}
	}

	private void showServerMessageDialog()	{
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
		mc.getSurface().createCaptchaSprite(mudclient.spriteLogo + 2, sprite);

		mc.setSleepingStatusText(null);
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

	private void dropItem() {
		int slot = packetsIncoming.getUnsignedByte();
		mc.setInventoryItemCount(mc.getInventoryItemCount() - 1);

		for (int index = slot; mc.getInventoryItemCount() > index; ++index) {
			mc.setInventoryItemID(index, mc.getInventoryItemID(index + 1));
			mc.setInventoryItemSize(index, mc.getInventoryItemSize(index + 1));
			mc.setInventoryItemEquipped(index, mc.getInventoryItemEquippedID(index + 1));
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
		mc.setDuelOffsetOpponentItemCount(0);
		mc.setDuelOfferItemCount(0);
	}

	private void showShopDialog() {
		mc.setShowDialogShop(true);
		int shopItemCount = packetsIncoming.getUnsignedByte();
		int shopType = packetsIncoming.getByte();
		mc.setShopSellPriceMod(packetsIncoming.getUnsignedByte());
		mc.setShopBuyPriceMod(packetsIncoming.getUnsignedByte());
		mc.setShopPriceMultiplier(packetsIncoming.getUnsignedByte());

		for (int i = 0; i < 40; ++i) {
			mc.setShopItemID(i, -1);
		}

		for (int i = 0; shopItemCount > i; ++i) {
			mc.setShopItemID(i, packetsIncoming.getShort());
			mc.setShopItemCount(i, packetsIncoming.getShort());
			mc.setShopItemPrice(i, packetsIncoming.getShort());
		}

		if (shopType == 1) {
			int var6 = 39;

			for (int inventoryIndex = 0; inventoryIndex < mc.getInventoryItemCount()
							&& shopItemCount <= var6; ++inventoryIndex) {
				boolean var25 = false;

				for (int var9 = 0; var9 < 40; ++var9) {
					if (mc.getInventoryItemID(inventoryIndex) == mc.getShopItemID(var9)) {
						var25 = true;
						break;
					}
				}

				if (mc.getInventoryItemID(inventoryIndex) == 10) {
					var25 = true;
				}

				if (!var25) {
					mc.setShopItemID(var6, FastMath.bitwiseAnd(32767, mc.getInventoryItemID(inventoryIndex)));
					mc.setShopItemCount(var6, 0);
					mc.setShopItemPrice(var6, 0);
					--var6;
				}
			}
		}

		if (mc.getShopSelectedItemIndex() >= 0 && 40 > mc.getShopSelectedItemIndex()
						&& mc.getShopSelectedItemType() != mc.getShopItemID(mc.getShopSelectedItemIndex())) {
			mc.setShopSelectedItemIndex(-1);
			mc.setShopSelectedItemType(-2);
		}
	}

	private void updateTradeDialog() {
		mc.setTradeRecipientItemsCount(packetsIncoming.getUnsignedByte());

		for (int var4 = 0; var4 < mc.getTradeRecipientItemsCount(); ++var4) {
			mc.setTradeRecipientItem(var4, packetsIncoming.getShort());
			mc.setTradeRecipientItemCount(var4, packetsIncoming.get32());
		}

		mc.setTradeItemCount(packetsIncoming.getUnsignedByte());

		for (int var4 = 0; var4 < mc.getTradeItemCount(); ++var4) {
			mc.setTradeItemID(var4, packetsIncoming.getShort());
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
			} else if (updateType == 1) {
				if (null != player) {
					int crownID = packetsIncoming.getUnsignedByte();
					String message = packetsIncoming.readString();
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
						mc.showMessage(crownID == 2, (player.clanTag != null ? "@whi@[@cla@" + player.clanTag + "@whi@]@yel@ " + player.displayName : player.displayName), player.message,
										MessageType.CHAT, crownID, player.accountName, (String) null);
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
					player.incomingProjectileSprite = sprite;
				}
			} else if (updateType == 4) {
				int sprite = packetsIncoming.getShort();
				int shooterServerIndex = packetsIncoming.getShort();
				if (player != null) {
					player.projectileRange = mc.getProjectileMaxRange();
					player.attackingNpcServerIndex = -1;
					player.attackingPlayerServerIndex = shooterServerIndex;
					player.incomingProjectileSprite = sprite;
				}
			} else if (updateType == 6 && player != null) {
				String message = packetsIncoming.readString();

				player.message = message;
				player.messageTimeout = 150;
				if (mc.getLocalPlayer() == player) {
					mc.showMessage(false, (player.clanTag != null ? "@whi@[@cla@" + player.clanTag + "@whi@]@whi@ " + player.displayName : player.displayName), player.message, MessageType.QUEST, 0,
									player.accountName, (String) null);
				}
			} else if (updateType == 5) {
				if (player == null) {
					packetsIncoming.getShort();
					packetsIncoming.readString();
					packetsIncoming.readString();
					int vtmp = packetsIncoming.getUnsignedByte();
					packetsIncoming.packetEnd += 6 + vtmp;
				} else {
					packetsIncoming.getShort();
					player.displayName = packetsIncoming.readString();
					player.accountName = packetsIncoming.readString();
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
				}
			}
		}
	}

	private void drawGroundItems(int length) {
		while (length > packetsIncoming.packetEnd) {
			if (packetsIncoming.getUnsignedByte() != 255) {
				--packetsIncoming.packetEnd;
				int groundItemID = packetsIncoming.getShort();
				int var19 = mc.getLocalPlayerX() + packetsIncoming.getByte();
				int var6 = mc.getLocalPlayerZ() + packetsIncoming.getByte();
				if ((groundItemID & 32768) != 0) {
					groundItemID &= 32767;
					int var7 = 0;

					for (int dir = 0; dir < mc.getGroundItemCount(); ++dir) {
						if (mc.getGroundItemX(dir) == var19 && mc.getGroundItemZ(dir) == var6
										&& mc.getGroundItemID(dir) == groundItemID) {
							groundItemID = -123;
						} else {
							if (var7 != dir) {
								mc.setGroundItemX(var7, mc.getGroundItemX(dir));
								mc.setGroundItemZ(var7, mc.getGroundItemZ(dir));
								mc.setGroundItemID(var7, mc.getGroundItemID(dir));
								mc.setGroundItemHeight(var7, mc.getGroundItemHeight(dir));
							}

							++var7;
						}
					}

					mc.setGroundItemCount(var7);

				} else {
					mc.setGroundItemX(mc.getGroundItemCount(), var19);
					mc.setGroundItemZ(mc.getGroundItemCount(), var6);
					mc.setGroundItemID(mc.getGroundItemCount(), groundItemID);
					mc.setGroundItemHeight(mc.getGroundItemCount(), 0);

					for (int var7 = 0; mc.getGameObjectInstanceCount() > var7; ++var7) {
						if (mc.getGameObjectInstanceX(var7) == var19
										&& mc.getGameObjectInstanceZ(var7) == var6) {
							mc.setGroundItemHeight(mc.getGroundItemCount(),
									com.openrsc.client.entityhandling.EntityHandler.getObjectDef(
											mc.getGameObjectInstanceID(var7)).getGroundItemVar());
							break;
						}
					}

					mc.setGroundItemCount(mc.getGroundItemCount() + 1);
				}

			} else {
				int var4 = 0;
				int offsetX = mc.getLocalPlayerX() + packetsIncoming.getByte() >> 3;
				int offsetY = mc.getLocalPlayerZ() + packetsIncoming.getByte() >> 3;

				for (int index = 0; mc.getGroundItemCount() > index; ++index) {
					int tileX = (mc.getGroundItemX(index) >> 3) - offsetX;
					int tileY = (mc.getGroundItemZ(index) >> 3) - offsetY;
					if (tileX != 0 || tileY != 0) {
						if (var4 != index) {
							mc.setGroundItemX(var4, mc.getGroundItemX(index));
							mc.setGroundItemZ(var4, mc.getGroundItemZ(index));
							mc.setGroundItemID(var4, mc.getGroundItemID(index));
							mc.setGroundItemHeight(var4, mc.getGroundItemHeight(index));
						}
						++var4;
					}
				}

				mc.setGroundItemCount(var4);
			}
		}
	}
}