package org.openrsc.server.packetbuilder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.openrsc.server.Config;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.model.Shop;
import org.openrsc.server.model.World;
import org.openrsc.server.model.auctions.Auction;
import org.openrsc.server.util.ChatFilter;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;

public final class MiscPacketBuilder {
	private final Player player;

	public MiscPacketBuilder(Player player) {
		this.player = player;
	}

	/*     */ public void sendOnlineCount() {
		/* 27 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 28 */ s.setID(202);
		/* 29 */ s.addShort(World.getPlayers().count());
		/* 30 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendOwner(int owner) {
		/* 34 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 35 */ s.setID(203);
		/* 36 */ s.addInt(owner);
		/* 37 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	public void sendTakeScreenshot() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(181);
		this.player.getSession().write(s.toPacket());
	}

	/*     */
	/*     */ public void sendQuestUpdate() {
		/* 41 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 42 */ s.setID(243);
		/* 43 */ s.addShort(this.player.getQuestPoints() + player.getScriptableQuestPoints());
		/* 44 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendQuestStarted(int questID) {
		/* 48 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 49 */ s.setID(242);
		/* 50 */ s.addByte((byte) questID);
		/* 51 */ s.addByte((byte) 0);
		/* 52 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendQuestFinished(int questID) {
		/* 56 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 57 */ s.setID(242);
		/* 58 */ s.addByte((byte) questID);
		/* 59 */ s.addByte((byte) 1);
		/* 60 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendQuestInformation() {
		/* 64 */ if (this.player.getQuests().size() > 0) {
			/* 65 */ RSCPacketBuilder s = new RSCPacketBuilder();
			/* 66 */ s.setID(241);
			/* 67 */ s.addByte((byte) this.player.getQuests().size());
			/* 68 */ for (Quest q : this.player.getQuests()) {
				/* 69 */ s.addByte((byte) q.getID());
				/* 70 */ if (q.finished())/* 71 */ s.addByte((byte) 1);
				/*     */ else/* 73 */ s.addByte((byte) 0);
				/*     */ }
			/* 75 */ this.player.getSession().write(s.toPacket());
			/*     */ }
		/*     */ }

	/**
	 * Opens and populates the auction house window
	 */
	public void openAuctionHouse() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(121);
		s.addShort(World.getWorld().getAuctionHouse().getAuctions().size());
		int i = 0;
		for (Auction auction : World.getWorld().getAuctionHouse().getAuctions()) {
			s.addShort(auction.getID());
			s.addLong(auction.getAmount());
			s.addLong(auction.getPrice());
			s.addByte((byte) (DataConversions.usernameToHash(auction.getOwner()) == player.getUsernameHash() ? 1 : 0));
		}
		this.player.getSession().write(s.toPacket());
	}

	/**
	 * Repopulates the auction house window
	 */
	public void repopulateAuctionHouse() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(123);
		s.addShort(World.getWorld().getAuctionHouse().getAuctions().size());
		for (Auction auction : World.getWorld().getAuctionHouse().getAuctions()) {
			s.addShort(auction.getID());
			s.addLong(auction.getAmount());
			s.addLong(auction.getPrice());
			// s.addByte((byte) (auction.getOwner().equals(player.getUsername())
			// ? 1 : 0));
			s.addByte((byte) (DataConversions.usernameToHash(auction.getOwner()) == player.getUsernameHash() ? 1 : 0));
		}
		this.player.getSession().write(s.toPacket());
	}

	public void removeFromAuctionHouse(Auction auction, long amount) {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(130);
		s.addShort(auction.getIndex());
		s.addLong(amount);
		this.player.getSession().write(s.toPacket());
	}

	public void addToAuctionHouse(Auction auction) {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(132);
		s.addShort(auction.getID());
		s.addLong(auction.getAmount());
		s.addLong(auction.getPrice());
		s.addByte((byte) (DataConversions.usernameToHash(auction.getOwner()) == player.getUsernameHash() ? 1 : 0));
		s.addShort(auction.getIndex());
		this.player.getSession().write(s.toPacket());
	}

	/**
	 * Hides the auction house window
	 */
	public void hideAuctionHouse() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(122);
		this.player.getSession().write(s.toPacket());
	}

	public void sendScriptableQuestPrimer() {
		Map<Integer, com.rscdaemon.scripting.quest.Quest> quests = player.getScriptableQuests();
		if (quests.size() > 0) {
			int updateCount = 0;

			ByteBuffer buffer = ByteBuffer.allocate(512);
			RSCPacketBuilder builder = new RSCPacketBuilder();
			builder.setID(241);
			for (Entry<Integer, com.rscdaemon.scripting.quest.Quest> entry : quests.entrySet()) {
				com.rscdaemon.scripting.quest.Quest quest = entry.getValue();
				if (quest.getVariable(com.rscdaemon.scripting.quest.Quest.QUEST_STAGE)
						.equals(com.rscdaemon.scripting.quest.Quest.QUEST_NOT_STARTED)) {
					continue;
				}
				++updateCount;
				buffer.put((byte) entry.getKey().intValue());
				if (quest.getVariable(com.rscdaemon.scripting.quest.Quest.QUEST_STAGE)
						.equals(com.rscdaemon.scripting.quest.Quest.QUEST_FINISHED)) {
					buffer.put((byte) 1);
				} else {
					buffer.put((byte) 0);
				}
			}
			builder.addByte((byte) updateCount);
			builder.addBytes(buffer.array());
			player.getSession().write(builder.toPacket());
		}
	}

	/*     */
	/*     */ public void sendNotification(String notification) {
		/* 80 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 81 */ s.setID(224);
		/* 82 */ s.addBytes(notification.getBytes());
		/* 83 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendDMMessage(String notification) {
		/* 87 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 88 */ s.setID(221);
		/* 89 */ s.addBytes(notification.getBytes());
		/* 90 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void updateGroupID(byte newID) {
		/* 94 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 95 */ s.setID(222);
		/* 96 */ s.addByte(newID);
		/* 97 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendLoginInformation() {
		/* 101 */ sendServerInfo();
		/* 102 */ sendWorldInfo();
		/* 103 */ sendKills();
		/* 104 */ sendDeaths();
		/* 105 */ sendFatigue();
		/* 106 */ sendInventory();
		/* 107 */ sendEquipmentStats();
		/* 108 */ sendStats();
		/* 109 */ sendPrivacySettings();
		/* 110 */ sendGameSettings();
		/* 111 */ sendFriendList();
		/* 112 */ sendQuestInformation();
		sendScriptableQuestPrimer();
		/* 113 */ sendQuestUpdate();
		/* 114 */ sendOnlineCount();
		/* 115 */ sendOwner(this.player.getAccount());
		/* 116 */ for (Iterator<Long> iter = this.player.getFriendList().iterator(); iter.hasNext();) {
			long friend = iter.next();
			/* 117 */ if ((World.getPlayer(friend) != null)
					&& (((World.getPlayer(friend).isFriendsWith(this.player.getUsernameHash()))
							|| (World.getPlayer(friend).getPrivacySetting(1)))))
				/* 118 */ sendFriendUpdate(friend, (byte) 1);
		}
		/*     */
		/* 120 */ synchronized (World.getPlayers()) {
			/* 121 */ for (Player friend : World.getPlayers()) {
				/* 122 */ if ((friend.isFriendsWith(this.player.getUsernameHash())) && ((
				/* 123 */ (this.player.isFriendsWith(friend.getUsernameHash()))
						|| (this.player.getPrivacySetting(1))))) {
					/* 124 */ friend.sendFriendUpdate(this.player.getUsernameHash(), (byte) 1);
					/*     */ }
				/*     */ }
			/*     */ }
		/* 128 */ sendIgnoreList();
		/* 129 */ sendCombatStyle();
		// This is what it's supposed to do if the player hasn't logged in
		// before.
		/* 130 */ if (this.player.getLastLogin() == 0L) {
			/* 131 */ this.player.setChangingAppearance(true);
			/* 132 */ sendAppearanceScreen();
			/*     */ }

		/* 134 */ sendMessage("Welcome to RuneScape!");
		/* 135 */ sendLoginBox();
		sendPendingAuctions();
		/*     */
		/* 137 */ if (this.player.getLocation().isInDMArena())/* 138 */ this.player.teleport(216, 2905);
		/*     */ }

	/*     */
	/*     */ public void sendPing() {
		/* 142 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 143 */ s.setID(117);
		/* 144 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	public void sendPendingAuctions() {
		String pendingAuctions = "";
		for (Auction a : World.getWorld().getAuctionHouse().getSoldAuctions()) {
			if (DataConversions.usernameToHash(a.getOwner()) == player.getUsernameHash()) {
				pendingAuctions += "You have sold " + a.getAmount() + "x "
						+ EntityHandler.getItemDef(a.getID()).getName() + " for " + (a.getPrice() * a.getAmount())
						+ "gp.%";
			}
		}
		for (Auction a : World.getWorld().getAuctionHouse().getCanceledAuctions()) {
			if (DataConversions.usernameToHash(a.getOwner()) == player.getUsernameHash()) {
				pendingAuctions += "You can currently collect " + a.getAmount() + "x "
						+ EntityHandler.getItemDef(a.getID()).getName() + ".%";
			}
		}
		if (!pendingAuctions.isEmpty()) {
			pendingAuctions += "Speak to the auctioneer to collect your coins and items.";
			sendAlert(pendingAuctions, true);
		}
	}

	/*     */
	/*     */ public void sendSleepImage(BufferedImage sleepImage) {
		/*     */ try {
			/* 149 */ RSCPacketBuilder s = new RSCPacketBuilder();
			/* 150 */ s.setID(206);
			/* 151 */ ByteArrayOutputStream output = new ByteArrayOutputStream();
			/* 152 */ ImageIO.write(sleepImage, "png", output);
			/* 153 */ s.addBytes(output.toByteArray());
			/* 154 */ this.player.getSession().write(s.toPacket());
		} catch (Exception ex) {
			/*     */ }
		/*     */ }

	/*     */
	/*     */ public void sendSleepSuccess() {
		/* 159 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 160 */ s.setID(39);
		/* 161 */ this.player.setSleepString("[RESET]");
		/* 162 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendSleepFailure() {
		/* 166 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 167 */ s.setID(225);
		/* 168 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendKills() {
		/* 172 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 173 */ s.setID(200);
		/* 174 */ s.addShort(this.player.getKills());
		/* 175 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendDeaths() {
		/* 179 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 180 */ s.setID(201);
		/* 181 */ s.addShort(this.player.getDeaths());
		/* 182 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void requestLocalhost(long requestee) {
		/* 186 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 187 */ s.addLong(requestee);
		/* 188 */ s.setID(3);
		/* 189 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendScreenshot() {
		/* 193 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 194 */ s.setID(181);
		/* 195 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendCombatStyle() {
		/* 199 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 200 */ s.setID(129);
		/* 201 */ s.addByte((byte) this.player.getCombatStyle());
		/* 202 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendFatigue() {
		/* 206 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 207 */ s.setID(126);
		/* 208 */ s.addByte((byte) player.getFatigue());
		/* 209 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendTemporaryFatigue() {
		/* 213 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 214 */ s.setID(126);
		/* 215 */ s.addByte((byte) this.player.getTemporaryFatigue());
		/* 216 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void hideMenu() {
		/* 220 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 221 */ s.setID(127);
		/* 222 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendMenu(String[] options) {
		/* 226 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 227 */ s.setID(223);
		/* 228 */ s.addByte((byte) options.length);
		/* 229 */ for (String option : options) {
			/* 230 */ s.addByte((byte) option.length());
			/* 231 */ s.addBytes(option.getBytes());
			/*     */ }
		/* 233 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void showBank() {
		/* 237 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 238 */ s.setID(93);
		/* 239 */ s.addByte((byte) this.player.getBank().size());
		/* 240 */ s.addByte((byte) -64);
		/* 241 */ for (InvItem i : this.player.getBank().getItems()) {
			/* 242 */ s.addShort(i.getID());
			/* 243 */ s.addLong(i.getAmount());
			/*     */ }
		/* 245 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void hideBank() {
		/* 249 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 250 */ s.setID(171);
		/* 251 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void updateBankItem(int slot, int newID, long amount) {
		/* 255 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 256 */ s.setID(139);
		/* 257 */ s.addByte((byte) slot);
		/* 258 */ s.addShort(newID);
		/* 259 */ s.addLong(amount);
		/* 260 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void showShop(Shop shop) {
		/* 264 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 265 */ s.setID(253);
		/* 266 */ s.addByte((byte) shop.size());
		// hack for token shop
		/* 267 */ s.addByte((byte) (shop.getID() == 102 ? 2 : ((shop.isGeneral()) ? 1 : 0)));
		/* 268 */ s.addByte((byte) shop.getSellModifier());
		/* 269 */ s.addByte((byte) shop.getBuyModifier());
		/* 270 */ for (InvItem i : shop.getItems()) {
			/* 271 */ s.addShort(i.getID());
			/* 272 */ s.addLong(i.getAmount());
			/*     */ }
		/* 274 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void hideShop() {
		/* 278 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 279 */ s.setID(220);
		/* 280 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void startWildernessUpdate(int seconds, byte type) {
		/* 284 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 285 */ s.setID(174);
		/* 286 */ s.addByte(type);
		/* 287 */ s.addShort((int) (seconds / 32.0D * 50.0D));
		/* 288 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void startShutdown(int seconds) {
		/* 292 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 293 */ s.setID(172);
		/* 294 */ s.addShort((int) (seconds / 32.0D * 50.0D));
		/* 295 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendAlert(String message, boolean big) {
		/* 299 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 300 */ s.setID((big) ? 64 : 148);
		/* 301 */ s.addBytes(message.getBytes());
		/* 302 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendSound(String soundName, boolean mp3) {
		/* 306 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 307 */ s.setID(11);
		/* 308 */ s.addByte((mp3 == true) ? (byte) 1 : (byte) 0);
		/* 309 */ s.addBytes(soundName.getBytes());
		/* 310 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendDied() {
		/* 314 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 315 */ s.setID(165);
		/* 316 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendPM(long user, byte[] message, int rank, boolean sent) {
		/* 320 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 321 */ s.setID((sent) ? 175 : 170);
		/* 322 */ s.addLong(user);
		s.addInt(rank);
		/* 323 */ s.addBytes(message);
		/* 324 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendFriendUpdate(long usernameHash, byte online) {
		/* 328 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 329 */ s.setID(25);
		/* 330 */ s.addLong(usernameHash);
		/* 331 */ s.addByte(online);
		/* 332 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendFriendList() {
		/* 336 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 337 */ s.setID(249);
		/* 338 */ s.addByte((byte) this.player.getFriendList().size());
		/* 339 */ for (Iterator<Long> iter = this.player.getFriendList().iterator(); iter.hasNext();) {
			long friend = iter.next();
			/* 340 */ s.addLong(friend);
			/* 341 */ s.addByte((byte) 0);
		}
		/*     */
		/* 343 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendIgnoreList() {
		/* 347 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 348 */ s.setID(2);
		/* 349 */ s.addByte((byte) this.player.getIgnoreList().size());
		/* 350 */ for (Long usernameHash : this.player.getIgnoreList())
			/* 351 */ s.addLong(usernameHash.longValue());
		/* 352 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendTradeAccept() {
		/* 356 */ Player with = this.player.getWishToTrade();
		/* 357 */ if (with == null)/* 358 */ return;
		/* 359 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 360 */ s.setID(251);
		/* 361 */ s.addLong(with.getUsernameHash());
		/* 362 */ s.addByte((byte) with.getTradeOffer().size());
		/* 363 */ for (InvItem item : with.getTradeOffer()) {
			/* 364 */ s.addShort(item.getID());
			/* 365 */ s.addLong(item.getAmount());
			/*     */ }
		/* 367 */ s.addByte((byte) this.player.getTradeOffer().size());
		/* 368 */ for (InvItem item : this.player.getTradeOffer()) {
			/* 369 */ s.addShort(item.getID());
			/* 370 */ s.addLong(item.getAmount());
			/*     */ }
		/* 372 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendDuelAccept() {
		/* 376 */ Player with = this.player.getWishToDuel();
		/* 377 */ if (with == null)/* 378 */ return;
		/* 379 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 380 */ s.setID(147);
		/* 381 */ s.addLong(with.getUsernameHash());
		/* 382 */ s.addByte((byte) with.getDuelOffer().size());
		/* 383 */ for (InvItem item : with.getDuelOffer()) {
			/* 384 */ s.addShort(item.getID());
			/* 385 */ s.addLong(item.getAmount());
			/*     */ }
		/* 387 */ s.addByte((byte) this.player.getDuelOffer().size());
		/* 388 */ for (InvItem item : this.player.getDuelOffer()) {
			/* 389 */ s.addShort(item.getID());
			/* 390 */ s.addLong(item.getAmount());
			/*     */ }
		/* 392 */ s.addByte((byte) ((this.player.getDuelSetting(0)) ? 1 : 0));
		/* 393 */ s.addByte((byte) ((this.player.getDuelSetting(1)) ? 1 : 0));
		/* 394 */ s.addByte((byte) ((this.player.getDuelSetting(2)) ? 1 : 0));
		/* 395 */ s.addByte((byte) ((this.player.getDuelSetting(3)) ? 1 : 0));
		/*     */
		/* 397 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendDMAccept() {
		/* 401 */ Player with = this.player.getWishToDM();
		/* 402 */ if (with == null) {
			/* 403 */ return;
			/*     */ }
		/* 405 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 406 */ s.setID(105);
		/* 407 */ s.addLong(with.getUsernameHash());
		/*     */
		/* 409 */ s.addByte((byte) ((this.player.getDMSetting(0)) ? 1 : 0));
		/* 410 */ s.addByte((byte) ((this.player.getDMSetting(1)) ? 1 : 0));
		/* 411 */ s.addByte((byte) ((this.player.getDMSetting(2)) ? 1 : 0));
		/* 412 */ s.addByte((byte) ((this.player.getDMSetting(3)) ? 1 : 0));
		/*     */
		/* 414 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendTradeAcceptUpdate() {
		/* 418 */ Player with = this.player.getWishToTrade();
		/* 419 */ if (with == null)/* 420 */ return;
		/* 421 */ RSCPacketBuilder s1 = new RSCPacketBuilder();
		/* 422 */ s1.setID(18);
		/* 423 */ s1.addByte((byte) ((this.player.isTradeOfferAccepted()) ? 1 : 0));
		/* 424 */ this.player.getSession().write(s1.toPacket());
		/*     */
		/* 426 */ RSCPacketBuilder s2 = new RSCPacketBuilder();
		/* 427 */ s2.setID(92);
		/* 428 */ s2.addByte((byte) ((with.isTradeOfferAccepted()) ? 1 : 0));
		/* 429 */ this.player.getSession().write(s2.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendDuelAcceptUpdate() {
		/* 433 */ Player with = this.player.getWishToDuel();
		/* 434 */ if (with == null) {
			/* 435 */ return;
			/*     */ }
		/* 437 */ RSCPacketBuilder s2 = new RSCPacketBuilder();
		/* 438 */ s2.setID(65);
		/* 439 */ s2.addByte((byte) ((with.isDuelOfferAccepted()) ? 1 : 0));
		/* 440 */ this.player.getSession().write(s2.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendDMAcceptUpdate() {
		/* 444 */ Player with = this.player.getWishToDM();
		/* 445 */ if (with == null) {
			/* 446 */ return;
			/*     */ }
		/* 448 */ RSCPacketBuilder s2 = new RSCPacketBuilder();
		/* 449 */ s2.setID(103);
		/* 450 */ s2.addByte((byte) ((with.isDMOfferAccepted()) ? 1 : 0));
		/* 451 */ this.player.getSession().write(s2.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendDuelSettingUpdate() {
		/* 455 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 456 */ s.setID(198);
		/* 457 */ s.addByte((byte) ((this.player.getDuelSetting(0)) ? 1 : 0));
		/* 458 */ s.addByte((byte) ((this.player.getDuelSetting(1)) ? 1 : 0));
		/* 459 */ s.addByte((byte) ((this.player.getDuelSetting(2)) ? 1 : 0));
		/* 460 */ s.addByte((byte) ((this.player.getDuelSetting(3)) ? 1 : 0));
		/* 461 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendDMSettingUpdate() {
		/* 465 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 466 */ s.setID(102);
		/* 467 */ s.addByte((byte) ((this.player.getDMSetting(0)) ? 1 : 0));
		/* 468 */ s.addByte((byte) ((this.player.getDMSetting(1)) ? 1 : 0));
		/* 469 */ s.addByte((byte) ((this.player.getDMSetting(2)) ? 1 : 0));
		/* 470 */ s.addByte((byte) ((this.player.getDMSetting(3)) ? 1 : 0));
		/* 471 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendTradeItems() {
		/* 475 */ Player with = this.player.getWishToTrade();
		/* 476 */ if (with == null)/* 477 */ return;
		/* 478 */ ArrayList<InvItem> items = with.getTradeOffer();
		/* 479 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 480 */ s.setID(250);
		/* 481 */ s.addByte((byte) items.size());
		/* 482 */ for (InvItem item : items) {
			/* 483 */ s.addShort(item.getID());
			/* 484 */ s.addLong(item.getAmount());
			/*     */ }
		/* 486 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendDuelItems() {
		/* 490 */ Player with = this.player.getWishToDuel();
		/* 491 */ if (with == null)/* 492 */ return;
		/* 493 */ ArrayList<InvItem> items = with.getDuelOffer();
		/* 494 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 495 */ s.setID(63);
		/* 496 */ s.addByte((byte) items.size());
		/* 497 */ for (InvItem item : items) {
			/* 498 */ s.addShort(item.getID());
			/* 499 */ s.addLong(item.getAmount());
			/*     */ }
		/* 501 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendTradeOpen() {
		/* 505 */ Player with = this.player.getWishToTrade();
		/* 506 */ if (with == null)/* 507 */ return;
		/* 508 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 509 */ s.setID(4);
		/* 510 */ s.addShort(with.getIndex());
		/* 511 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendDuelOpen() {
		/* 515 */ Player with = this.player.getWishToDuel();
		/* 516 */ if (with == null)/* 517 */ return;
		/* 518 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 519 */ s.setID(229);
		/* 520 */ s.addShort(with.getIndex());
		/* 521 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendDMOpen() {
		/* 525 */ Player with = this.player.getWishToDM();
		/* 526 */ if (with == null)/* 527 */ return;
		/* 528 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 529 */ s.setID(100);
		/* 530 */ s.addShort(with.getIndex());
		/* 531 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendTradeClose() {
		/* 535 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 536 */ s.setID(187);
		/* 537 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendDuelClose() {
		/* 541 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 542 */ s.setID(160);
		/* 543 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendDMClose() {
		/* 547 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 548 */ s.setID(101);
		/* 549 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendAppearanceScreen() {
		/* 553 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 554 */ s.setID(207);
		/* 555 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendServerInfo() {
		/* 559 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 560 */ s.setID(110);
		/* 564 */ s.addShort(0);
		/* 565 */ s.addByte((byte) 0);
		/* 566 */ s.addLong(Config.START_TIME);
		/* 567 */ s.addBytes("USA".getBytes());
		/* 568 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendTeleBubble(int x, int y, boolean grab) {
		/* 572 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 573 */ s.setID(23);
		/* 574 */ s.addByte((byte) ((grab) ? 1 : 0));
		/* 575 */ s.addByte((byte) (x - this.player.getX()));
		/* 576 */ s.addByte((byte) (y - this.player.getY()));
		/* 577 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendMessagePointer(int pointer) {
		/* 581 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 582 */ s.setID(49);
		/* 583 */ s.addInt(pointer);
		/* 584 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendMessage(String message) {
		/* 588 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 589 */ s.setID(48);
		/* 590 */ s.addBytes(message.getBytes());
		/* 591 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendRemoveItem(int slot) {
		/* 595 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 596 */ s.setID(191);
		/* 597 */ s.addByte((byte) slot);
		/* 598 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendUpdateItem(int slot) {
		/* 602 */ InvItem item = this.player.getInventory().get(slot);
		/* 603 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 604 */ s.setID(228);
		/* 605 */ s.addByte((byte) slot);
		/* 606 */ s.addShort(item.getID() + ((item.isWielded()) ? 32768 : 0));
		/* 607 */ if (item.getDef().isStackable())/* 608 */ s.addLong(item.getAmount());
		/* 609 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendInventory() {
		/* 613 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 614 */ s.setID(114);
		/* 615 */ s.addByte((byte) this.player.getInventory().size());
		/* 616 */ for (InvItem item : this.player.getInventory().getItems()) {
			/* 617 */ s.addShort(item.getID() + ((item.isWielded()) ? 32768 : 0));

			/* 618 */ if (item.getDef().isStackable() || item.getDef().isNote()) {
				/* 619 */ s.addLong(item.getAmount());
				// System.out.println(item.getID());
			}
			/*     */ }
		/* 621 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendEquipmentStats() {
		/* 625 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 626 */ s.setID(177);
		/* 627 */ s.addShort(this.player.getArmourPoints());
		/* 628 */ s.addShort(this.player.getWeaponAimPoints());
		/* 629 */ s.addShort(this.player.getWeaponPowerPoints());
		/* 630 */ s.addShort(this.player.getMagicPoints());
		/* 631 */ s.addShort(this.player.getPrayerPoints());
		/* 632 */ s.addShort(this.player.getRangePoints());
		/* 633 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendStat(int stat) {
		/* 637 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 638 */ s.setID(208);
		/* 639 */ s.addByte((byte) stat);
		/* 640 */ s.addByte((byte) this.player.getCurStat(stat));
		/* 641 */ s.addByte((byte) this.player.getMaxStat(stat));
		/* 642 */ s.addInt((int) this.player.getExp(stat));
		/* 643 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendStats() {
		/* 647 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 648 */ s.setID(180);
		/* 649 */ for (int lvl : this.player.getCurStats())
			/* 650 */ s.addByte((byte) lvl);
		/* 651 */ for (int lvl : this.player.getMaxStats())
			/* 652 */ s.addByte((byte) lvl);
		/* 653 */ for (double exp : this.player.getExps())
			/* 654 */ s.addInt((int) exp);
		/* 655 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendWorldInfo()
	/*     */ {
		/* 660 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 661 */ s.setID(131);
		/* 662 */ s.addShort(this.player.getIndex());
		/* 663 */ s.addShort(2304);
		/* 664 */ s.addShort(1776);
		/* 665 */ s.addShort(Formulae.getHeight(this.player.getLocation()));
		/* 666 */ s.addShort(944);
		/* 667 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendPrayers() {
		/* 671 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 672 */ s.setID(209);
		/* 673 */ for (int x = 0; x < 14; ++x)
			/* 674 */ s.addByte((byte) ((this.player.isPrayerActivated(x)) ? 1 : 0));
		/* 675 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendGameSettings() {
		/* 679 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 680 */ s.setID(152);
		/* 681 */ byte settings = 0;
		/* 682 */ if (this.player.getGameSetting(0))/* 683 */ settings = (byte) (settings | 0x1);
		/* 684 */ if (this.player.getGameSetting(1))/* 685 */ settings = (byte) (settings | 0x2);
		/* 686 */ if (this.player.getGameSetting(2))/* 687 */ settings = (byte) (settings | 0x4);
		/* 688 */ if (this.player.getGameSetting(3))/* 689 */ settings = (byte) (settings | 0x8);
		/* 690 */ if (this.player.getGameSetting(4))/* 691 */ settings = (byte) (settings | 0x10);
		/* 692 */ if (this.player.getGameSetting(5))/* 693 */ settings = (byte) (settings | 0x20);
		/* 694 */ if (this.player.getCombatWindow() == 0)/* 695 */ settings = (byte) (settings | 0x40);
		/* 696 */ else if (this.player.getCombatWindow() == 1)/* 697 */ settings = (byte) (settings | 0x80);
		/* 698 */ s.addByte(settings);
		/* 699 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendPrivacySettings() {
		/* 703 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 704 */ s.setID(158);
		/* 705 */ byte settings = 0;
		/* 706 */ if (this.player.getPrivacySetting(0))/* 707 */ settings = (byte) (settings | 0x1);
		/* 708 */ if (this.player.getPrivacySetting(1))/* 709 */ settings = (byte) (settings | 0x2);
		/* 710 */ if (this.player.getPrivacySetting(2))/* 711 */ settings = (byte) (settings | 0x4);
		/* 712 */ if (this.player.getPrivacySetting(3))/* 713 */ settings = (byte) (settings | 0x8);
		/* 714 */ if (this.player.getPrivacySetting(4))/* 715 */ settings = (byte) (settings | 0x10);
		/* 716 */ s.addByte(settings);
		/* 717 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendLogout() {
		/* 721 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 722 */ s.setID(12);
		/* 723 */ s.addByte((byte) 1);
		/* 724 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendCantLogout() {
		/* 728 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 729 */ s.setID(136);
		/* 730 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendLoginBox() {
		/* 734 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 735 */ s.setID(248);
		/* 736 */ s.addShort(this.player.getDaysSinceLastLogin());
		/* 737 */ s.addShort(this.player.getDaysSubscriptionLeft());
		/* 738 */ s.addShort(this.player.getUnreadMessages());
		/* 739 */ s.addShort(this.player.getRecoveryDays());
		/* 740 */ s.addBytes(this.player.getLastIP().getBytes());
		/* 741 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	/*     */
	/*     */ public void sendGlobalMessage(long usernameHash, int rank, String message) {
		/* 745 */ RSCPacketBuilder s = new RSCPacketBuilder();
		/* 746 */ s.setID(235);
		/* 747 */ s.addLong(usernameHash);
		/* 748 */ s.addByte((byte) rank);
		/* 749 */ s.addBytes(ChatFilter.censor(message).getBytes());
		/* 750 */ this.player.getSession().write(s.toPacket());
		/*     */ }

	public void sendScript(String script) {
		RSCPacketBuilder pb = new RSCPacketBuilder().setID(124);
		pb.addBytes(script.getBytes());
		player.getSession().write(pb.toPacket());
	}

	public void sendScrollableAlert(String message) {
		RSCPacketBuilder pb = new RSCPacketBuilder().setID(156);
		pb.addBytes(message.getBytes());
		player.getSession().write(pb.toPacket());
	}

	public void sendLastMoved(Player p) {
		RSCPacketBuilder pb = new RSCPacketBuilder().setID(125);
		pb.addInt(p.getIndex());
		pb.addLong(p.getLastMoved());
		player.getSession().write(pb.toPacket());
	}
}