package com.openrsc.server;

import com.openrsc.server.model.PlayerAppearance;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.PrivateMessage;
import com.openrsc.server.model.entity.Entity;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
import com.openrsc.server.model.entity.update.*;
import com.openrsc.server.net.PacketBuilder;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.sql.query.logs.PMLog;
import com.openrsc.server.util.EntityList;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author n0m
 */
public final class GameStateUpdater {


	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final EntityList<Player> players;
	private final EntityList<Npc> npcs;

	private long lastProcessPlayersDuration			= 0;
	private long lastProcessNpcsDuration			= 0;
	private long lastProcessMessageQueuesDuration	= 0;
	private long lastUpdateClientsDuration			= 0;
	private long lastDoCleanupDuration				= 0;
	private long lastExecuteWalkToActionsDuration	= 0;

	private final Server server;
	public final Server getServer() {
		return server;
	}

	public GameStateUpdater(Server server) {
		this.server = server;
		this.players = getServer().getWorld().getPlayers();
		this.npcs = getServer().getWorld().getNpcs();
	}

	// private static final int PACKET_UPDATETIMEOUTS = 0;
	public void sendUpdatePackets(Player p) {
		// TODO: Should be private
		try {
			updatePlayers(p);
			updatePlayerAppearances(p);
			updateNpcs(p);
			updateNpcAppearances(p);
			updateGameObjects(p);
			updateWallObjects(p);
			updateGroundItems(p);
			sendClearLocations(p);
			updateTimeouts(p);
		} catch (Exception e) {
			LOGGER.catching(e);
			p.unregister(true, "Exception while updating player " + p.getUsername());
		}
	}

	/**
	 * Checks if the player has moved within the last X minutes
	 */
	protected void updateTimeouts(Player player) {
		long curTime = System.currentTimeMillis();
		int timeoutLimit = getServer().getConfig().IDLE_TIMER; // 5 minute idle log out
		int autoSave = getServer().getConfig().AUTO_SAVE; // 30 second autosave
		if (player.isRemoved() || player.getAttribute("dummyplayer", false)) {
			return;
		}
		if (curTime - player.getLastSaveTime() >= (autoSave) && player.loggedIn()) {
			player.timeIncrementActivity();
			player.save();
			player.setLastSaveTime(curTime);
		}
		if (curTime - player.getLastPing() >= 30000) {
			player.unregister(false, "Ping time-out");
		} else if (player.warnedToMove()) {
			if (curTime - player.getLastMoved() >= (timeoutLimit + 60000) && player.loggedIn() && !player.isStaff()) {
				player.unregister(false, "Movement time-out");
			} else if (player.hasMoved()) {
				player.setWarnedToMove(false);
			}
		} else if (curTime - player.getLastMoved() >= timeoutLimit && !player.isMod()) {
			if (player.isSleeping()) {
				player.setSleeping(false);
				ActionSender.sendWakeUp(player, false, false);
			}
			player.message("@cya@You have been standing here for " + (timeoutLimit / 60000)
				+ " mins! Please move to a new area");
			player.setWarnedToMove(true);
		}
	}

	protected void updateNpcs(Player playerToUpdate) throws Exception {
		com.openrsc.server.net.PacketBuilder packet = new com.openrsc.server.net.PacketBuilder();
		packet.setID(79);
		packet.startBitAccess();
		packet.writeBits(playerToUpdate.getLocalNpcs().size(), 8);
		for (Iterator<Npc> it$ = playerToUpdate.getLocalNpcs().iterator(); it$.hasNext(); ) {
			Npc localNpc = it$.next();

			if (!playerToUpdate.withinRange(localNpc) || localNpc.isRemoved() || localNpc.isTeleporting() || localNpc.inCombat()) {
				it$.remove();
				packet.writeBits(1, 1);
				packet.writeBits(1, 1);
				packet.writeBits(3, 2);
			} else {
				if (localNpc.hasMoved()) {
					packet.writeBits(1, 1);
					packet.writeBits(0, 1);
					packet.writeBits(localNpc.getSprite(), 3);
				} else if (localNpc.spriteChanged()) {
					packet.writeBits(1, 1);
					packet.writeBits(1, 1);
					packet.writeBits(localNpc.getSprite(), 4);
				} else {
					packet.writeBits(0, 1);
				}
			}
		}
		for (Npc newNPC : playerToUpdate.getViewArea().getNpcsInView()) {
			if (playerToUpdate.getLocalNpcs().contains(newNPC) || newNPC.equals(playerToUpdate) || newNPC.isRemoved()
				|| newNPC.getID() == 194 && !playerToUpdate.getCache().hasKey("ned_hired")
				|| !playerToUpdate.withinRange(newNPC, (getServer().getConfig().VIEW_DISTANCE * 8) - 1) || (newNPC.isTeleporting() && !newNPC.inCombat())) {
				continue;
			} else if (playerToUpdate.getLocalNpcs().size() >= 255) {
				break;
			}
			byte[] offsets = DataConversions.getMobPositionOffsets(newNPC.getLocation(), playerToUpdate.getLocation());
			packet.writeBits(newNPC.getIndex(), 12);
			packet.writeBits(offsets[0], 6);
			packet.writeBits(offsets[1], 6);
			packet.writeBits(newNPC.getSprite(), 4);
			packet.writeBits(newNPC.getID(), 10);

			playerToUpdate.getLocalNpcs().add(newNPC);
		}
		packet.finishBitAccess();
		playerToUpdate.write(packet.toPacket());
	}

	protected void updatePlayers(Player playerToUpdate) throws Exception {

		com.openrsc.server.net.PacketBuilder positionBuilder = new com.openrsc.server.net.PacketBuilder();
		positionBuilder.setID(191);
		positionBuilder.startBitAccess();
		positionBuilder.writeBits(playerToUpdate.getX(), 11);
		positionBuilder.writeBits(playerToUpdate.getY(), 13);
		positionBuilder.writeBits(playerToUpdate.getSprite(), 4);
		positionBuilder.writeBits(playerToUpdate.getLocalPlayers().size(), 8);

		if (playerToUpdate.loggedIn()) {
			for (Iterator<Player> it$ = playerToUpdate.getLocalPlayers().iterator(); it$.hasNext(); ) {
				Player otherPlayer = it$.next();
				boolean visibleConditionOverride = otherPlayer.isVisibleTo(playerToUpdate);

				if (!playerToUpdate.withinRange(otherPlayer) || !otherPlayer.loggedIn() || otherPlayer.isRemoved()
					|| otherPlayer.isTeleporting() || otherPlayer.isInvisible(playerToUpdate)
					|| !visibleConditionOverride || otherPlayer.inCombat()) {
					positionBuilder.writeBits(1, 1); //Needs Update
					positionBuilder.writeBits(1, 1); //Update Type
					positionBuilder.writeBits(3, 2); //???
					it$.remove();
					playerToUpdate.getKnownPlayerAppearanceIDs().remove(otherPlayer.getUsernameHash());
				} else {
					if (!otherPlayer.hasMoved() && !otherPlayer.spriteChanged()) {
						positionBuilder.writeBits(0, 1); //Needs Update
					} else {
						// The player is actually going to be updated
						if (otherPlayer.hasMoved()) {
							positionBuilder.writeBits(1, 1); //Needs Update
							positionBuilder.writeBits(0, 1); //Update Type
							positionBuilder.writeBits(otherPlayer.getSprite(), 3);
						} else if (otherPlayer.spriteChanged()) {
							positionBuilder.writeBits(1, 1); //Needs Update
							positionBuilder.writeBits(1, 1); //Update Type
							positionBuilder.writeBits(otherPlayer.getSprite(), 4);
						}
					}
				}
			}

			for (Player otherPlayer : playerToUpdate.getViewArea().getPlayersInView()) {
				boolean visibleConditionOverride = otherPlayer.isVisibleTo(playerToUpdate);
				if (playerToUpdate.getLocalPlayers().contains(otherPlayer) || otherPlayer.equals(playerToUpdate)
					|| !otherPlayer.withinRange(playerToUpdate) || !otherPlayer.loggedIn()
					|| otherPlayer.isRemoved() || otherPlayer.isInvisible(playerToUpdate)
					|| !visibleConditionOverride || (otherPlayer.isTeleporting() && !otherPlayer.inCombat())) {
					continue;
				}
				byte[] offsets = DataConversions.getMobPositionOffsets(otherPlayer.getLocation(),
					playerToUpdate.getLocation());
				positionBuilder.writeBits(otherPlayer.getIndex(), 11);
				positionBuilder.writeBits(offsets[0], 6);
				positionBuilder.writeBits(offsets[1], 6);
				positionBuilder.writeBits(otherPlayer.getSprite(), 4);
				playerToUpdate.getLocalPlayers().add(otherPlayer);
				if (playerToUpdate.getLocalPlayers().size() >= 255) {
					break;
				}
			}
		}
		positionBuilder.finishBitAccess();
		playerToUpdate.write(positionBuilder.toPacket());
	}

	public void updateNpcAppearances(Player player) {
		ConcurrentLinkedQueue<Damage> npcsNeedingHitsUpdate = new ConcurrentLinkedQueue<>();
		ConcurrentLinkedQueue<ChatMessage> npcMessagesNeedingDisplayed = new ConcurrentLinkedQueue<>();
		ConcurrentLinkedQueue<Projectile> npcProjectilesNeedingDisplayed = new ConcurrentLinkedQueue<>();

		for (Npc npc : player.getLocalNpcs()) {
			UpdateFlags updateFlags = npc.getUpdateFlags();
			if (updateFlags.hasChatMessage()) {
				ChatMessage chatMessage = updateFlags.getChatMessage();
				npcMessagesNeedingDisplayed.add(chatMessage);
			}
			if (updateFlags.hasTakenDamage()) {
				Damage damage = updateFlags.getDamage().get();
				npcsNeedingHitsUpdate.add(damage);
			}
			if (updateFlags.hasFiredProjectile()) {
				Projectile projectileFired = updateFlags.getProjectile().get();
				npcProjectilesNeedingDisplayed.add(projectileFired);
			}
		}
		int updateSize = npcMessagesNeedingDisplayed.size() + npcsNeedingHitsUpdate.size()
			+ npcProjectilesNeedingDisplayed.size();
		if (updateSize > 0) {
			PacketBuilder npcAppearancePacket = new PacketBuilder();
			npcAppearancePacket.setID(104);
			npcAppearancePacket.writeShort(updateSize);

			ChatMessage chatMessage;
			while ((chatMessage = npcMessagesNeedingDisplayed.poll()) != null) {
				npcAppearancePacket.writeShort(chatMessage.getSender().getIndex());
				npcAppearancePacket.writeByte((byte) 1);
				npcAppearancePacket.writeShort(chatMessage.getRecipient() == null ? -1 : chatMessage.getRecipient().getIndex());
				npcAppearancePacket.writeString(chatMessage.getMessageString());
			}
			Damage npcNeedingHitsUpdate;
			while ((npcNeedingHitsUpdate = npcsNeedingHitsUpdate.poll()) != null) {
				npcAppearancePacket.writeShort(npcNeedingHitsUpdate.getIndex());
				npcAppearancePacket.writeByte((byte) 2);
				npcAppearancePacket.writeByte((byte) npcNeedingHitsUpdate.getDamage());
				npcAppearancePacket.writeByte((byte) npcNeedingHitsUpdate.getCurHits());
				npcAppearancePacket.writeByte((byte) npcNeedingHitsUpdate.getMaxHits());
			}
			Projectile projectile;
			while ((projectile = npcProjectilesNeedingDisplayed.poll()) != null) {
				Entity victim = projectile.getVictim();
				if (victim.isNpc()) {
					npcAppearancePacket.writeShort(projectile.getCaster().getIndex());
					npcAppearancePacket.writeByte((byte) 3);
					npcAppearancePacket.writeShort(projectile.getType());
					npcAppearancePacket.writeShort(((Npc) victim).getIndex());
				} else if (victim.isPlayer()) {
					npcAppearancePacket.writeShort(projectile.getCaster().getIndex());
					npcAppearancePacket.writeByte((byte) 4);
					npcAppearancePacket.writeShort(projectile.getType());
					npcAppearancePacket.writeShort(((Player) victim).getIndex());
				}
			}
			player.write(npcAppearancePacket.toPacket());
		}
	}

	/**
	 * Handles the appearance updating for @param player
	 *
	 * @param player
	 */
	public void updatePlayerAppearances(Player player) {

		ArrayDeque<Bubble> bubblesNeedingDisplayed = new ArrayDeque<>();
		ArrayDeque<ChatMessage> chatMessagesNeedingDisplayed = new ArrayDeque<>();
		ArrayDeque<Projectile> projectilesNeedingDisplayed = new ArrayDeque<>();
		ArrayDeque<Damage> playersNeedingDamageUpdate = new ArrayDeque<>();
		ArrayDeque<HpUpdate> playersNeedingHpUpdate = new ArrayDeque<HpUpdate>();
		ArrayDeque<Player> playersNeedingAppearanceUpdate = new ArrayDeque<>();

		if (player.getUpdateFlags().hasBubble()) {
			Bubble bubble = player.getUpdateFlags().getActionBubble().get();
			bubblesNeedingDisplayed.add(bubble);
		}
		if (player.getUpdateFlags().hasFiredProjectile()) {
			Projectile projectileFired = player.getUpdateFlags().getProjectile().get();
			projectilesNeedingDisplayed.add(projectileFired);
		}
		if (player.getUpdateFlags().hasChatMessage()) {
			ChatMessage chatMessage = player.getUpdateFlags().getChatMessage();
			chatMessagesNeedingDisplayed.add(chatMessage);
		}
		if (player.getUpdateFlags().hasTakenDamage()) {
			Damage damage = player.getUpdateFlags().getDamage().get();
			playersNeedingDamageUpdate.add(damage);
		}
		if (player.getUpdateFlags().hasTakenHpUpdate()) {
			HpUpdate hpUpdate = player.getUpdateFlags().getHpUpdate().get();
			playersNeedingHpUpdate.add(hpUpdate);
		}
		if (player.getUpdateFlags().hasAppearanceChanged()) {
			playersNeedingAppearanceUpdate.add(player);
		}
		for (Player otherPlayer : player.getLocalPlayers()) {

			UpdateFlags updateFlags = otherPlayer.getUpdateFlags();

			if(otherPlayer.getUsername().trim().equalsIgnoreCase("kenix") && player.getUsername().trim().equalsIgnoreCase("kenix")) {
				LOGGER.info("UF: " + updateFlags + ", isTeleporting: " + otherPlayer.isTeleporting() + ", Override: " + player.requiresAppearanceUpdateForPeek(otherPlayer));
			}

			if (updateFlags.hasBubble()) {
				Bubble bubble = updateFlags.getActionBubble().get();
				bubblesNeedingDisplayed.add(bubble);
			}
			if (updateFlags.hasFiredProjectile()) {
				Projectile projectileFired = updateFlags.getProjectile().get();
				projectilesNeedingDisplayed.add(projectileFired);
			}
			if (updateFlags.hasChatMessage() && !player.getSettings()
				.getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES)) {
				ChatMessage chatMessage = updateFlags.getChatMessage();
				chatMessagesNeedingDisplayed.add(chatMessage);
			}
			if (updateFlags.hasTakenDamage()) {
				Damage damage = updateFlags.getDamage().get();
				playersNeedingDamageUpdate.add(damage);
			}
			if (updateFlags.hasTakenHpUpdate()) {
				HpUpdate hpUpdate = updateFlags.getHpUpdate().get();
				playersNeedingHpUpdate.add(hpUpdate);
			}
			if (player.requiresAppearanceUpdateFor(otherPlayer))
				playersNeedingAppearanceUpdate.add(otherPlayer);
		}
		issuePlayerAppearanceUpdatePacket(player, bubblesNeedingDisplayed, chatMessagesNeedingDisplayed,
			projectilesNeedingDisplayed, playersNeedingDamageUpdate, playersNeedingHpUpdate, playersNeedingAppearanceUpdate);
	}

	private void issuePlayerAppearanceUpdatePacket(Player player, Queue<Bubble> bubblesNeedingDisplayed,
														  Queue<ChatMessage> chatMessagesNeedingDisplayed, Queue<Projectile> projectilesNeedingDisplayed,
														  Queue<Damage> playersNeedingDamageUpdate, Queue<HpUpdate> playersNeedingHpUpdate, Queue<Player> playersNeedingAppearanceUpdate) {
		if (player.loggedIn()) {
			int updateSize = bubblesNeedingDisplayed.size() + chatMessagesNeedingDisplayed.size()
				+ playersNeedingDamageUpdate.size() + projectilesNeedingDisplayed.size()
				+ playersNeedingAppearanceUpdate.size() + playersNeedingHpUpdate.size();

			if (updateSize > 0) {
				PacketBuilder appearancePacket = new PacketBuilder();
				appearancePacket.setID(234);
				appearancePacket.writeShort(updateSize);
				Bubble b;
				while ((b = bubblesNeedingDisplayed.poll()) != null) {
					appearancePacket.writeShort(b.getOwner().getIndex());
					appearancePacket.writeByte((byte) 0);
					appearancePacket.writeShort(b.getID());
				}
				ChatMessage cm;
				while ((cm = chatMessagesNeedingDisplayed.poll()) != null) {
					Player sender = (Player) cm.getSender();

					int chatType = sender.isMuted() || (sender.getLocation().onTutorialIsland() && !sender.isStaff()) ? 7 : (cm.getRecipient() == null ? 1 : 6);
					appearancePacket.writeShort(cm.getSender().getIndex());
					appearancePacket.writeByte(chatType);

					if (chatType == 1 || chatType == 7) {
						if (cm.getSender() != null && cm.getSender() instanceof Player)
							appearancePacket.writeInt(sender.getIcon());
					}

					if (chatType == 7) {
						appearancePacket.writeByte(sender.isMuted() ? 1 : 0);
						appearancePacket.writeByte(sender.getLocation().onTutorialIsland() ? 1 : 0);
					}

					if (chatType != 7 || player.isAdmin()) {
						appearancePacket.writeString(cm.getMessageString());
					} else {
						appearancePacket.writeString("");
					}
				}
				Damage playerNeedingHitsUpdate;
				while ((playerNeedingHitsUpdate = playersNeedingDamageUpdate.poll()) != null) {
					appearancePacket.writeShort(playerNeedingHitsUpdate.getIndex());
					appearancePacket.writeByte((byte) 2);
					appearancePacket.writeByte((byte) playerNeedingHitsUpdate.getDamage());
					appearancePacket.writeByte((byte) playerNeedingHitsUpdate.getCurHits());
					appearancePacket.writeByte((byte) playerNeedingHitsUpdate.getMaxHits());
				}
				Projectile projectile;
				while ((projectile = projectilesNeedingDisplayed.poll()) != null) {
					Entity victim = projectile.getVictim();
					if (victim.isNpc()) {
						appearancePacket.writeShort(projectile.getCaster().getIndex());
						appearancePacket.writeByte((byte) 3);
						appearancePacket.writeShort(projectile.getType());
						appearancePacket.writeShort(((Npc) victim).getIndex());
					} else if (victim.isPlayer()) {
						appearancePacket.writeShort(projectile.getCaster().getIndex());
						appearancePacket.writeByte((byte) 4);
						appearancePacket.writeShort(projectile.getType());
						appearancePacket.writeShort(((Player) victim).getIndex());
					}
				}
				Player playerNeedingAppearanceUpdate;
				while ((playerNeedingAppearanceUpdate = playersNeedingAppearanceUpdate.poll()) != null) {
					PlayerAppearance appearance = playerNeedingAppearanceUpdate.getSettings().getAppearance();

					appearancePacket.writeShort((short) playerNeedingAppearanceUpdate.getIndex());
					appearancePacket.writeByte((byte) 5);
					//appearancePacket.writeShort(0);
					appearancePacket.writeString(playerNeedingAppearanceUpdate.getUsername());
					//appearancePacket.writeString(playerNeedingAppearanceUpdate.getUsername());

					appearancePacket.writeByte((byte) playerNeedingAppearanceUpdate.getWornItems().length);
					for (int i : playerNeedingAppearanceUpdate.getWornItems()) {
						appearancePacket.writeShort(i);
					}
					appearancePacket.writeByte(appearance.getHairColour());
					appearancePacket.writeByte(appearance.getTopColour());
					appearancePacket.writeByte(appearance.getTrouserColour());
					appearancePacket.writeByte(appearance.getSkinColour());
					appearancePacket.writeByte((byte) playerNeedingAppearanceUpdate.getCombatLevel());
					appearancePacket.writeByte((byte) (playerNeedingAppearanceUpdate.getSkullType()));

					if (playerNeedingAppearanceUpdate.getClan() != null) {
						appearancePacket.writeByte(1);
						appearancePacket.writeString(playerNeedingAppearanceUpdate.getClan().getClanTag());
					} else {
						appearancePacket.writeByte(0);
					}

					appearancePacket.writeByte(playerNeedingAppearanceUpdate.stateIsInvisible() ? 1 : 0);
					appearancePacket.writeByte(playerNeedingAppearanceUpdate.stateIsInvulnerable() ? 1 : 0);
					appearancePacket.writeByte(playerNeedingAppearanceUpdate.getGroupID());
					appearancePacket.writeInt(playerNeedingAppearanceUpdate.getIcon());
				}
				HpUpdate playerNeedingHpUpdate;
				while ((playerNeedingHpUpdate = playersNeedingHpUpdate.poll()) != null) {
					appearancePacket.writeShort(playerNeedingHpUpdate.getIndex());
					appearancePacket.writeByte((byte) 9);
					appearancePacket.writeByte((byte) playerNeedingHpUpdate.getCurHits());
					appearancePacket.writeByte((byte) playerNeedingHpUpdate.getMaxHits());
				}

				player.write(appearancePacket.toPacket());
			}
		}
	}

	protected void updateGameObjects(Player playerToUpdate) throws Exception {
		boolean changed = false;
		PacketBuilder packet = new PacketBuilder();
		packet.setID(48);
		// TODO: This is not handled correctly.
		//       According to RSC+ replays, the server never tells the client to unload objects until
		//       a region is unloaded. It then instructs the client to only unload the region.
		for (Iterator<GameObject> it$ = playerToUpdate.getLocalGameObjects().iterator(); it$.hasNext(); ) {
			GameObject o = it$.next();
			if (!playerToUpdate.withinGridRange(o) || o.isRemoved() || !o.isVisibleTo(playerToUpdate)) {
				int offsetX = o.getX() - playerToUpdate.getX();
				int offsetY = o.getY() - playerToUpdate.getY();
				//If the object is close enough we can use regular way to remove:
				if (offsetX > -128 && offsetY > -128 && offsetX < 128 && offsetY < 128) {
					packet.writeShort(60000);
					packet.writeByte(offsetX);
					packet.writeByte(offsetY);
					packet.writeByte(o.getDirection());
					it$.remove();
					changed = true;
				} else {
					//If it's not close enough we need to use the region clean packet
					playerToUpdate.getLocationsToClear().add(o.getLocation());
					it$.remove();
					changed = true;
				}
			}
		}

		for (GameObject newObject : playerToUpdate.getViewArea().getGameObjectsInView()) {
			if (!playerToUpdate.withinGridRange(newObject) || newObject.isRemoved()
				|| !newObject.isVisibleTo(playerToUpdate) || newObject.getType() != 0
				|| playerToUpdate.getLocalGameObjects().contains(newObject)) {
				continue;
			}
			packet.writeShort(newObject.getID());
			int offsetX = newObject.getX() - playerToUpdate.getX();
			int offsetY = newObject.getY() - playerToUpdate.getY();
			packet.writeByte(offsetX);
			packet.writeByte(offsetY);
			packet.writeByte(newObject.getDirection());
			playerToUpdate.getLocalGameObjects().add(newObject);
			changed = true;
		}
		if (changed)
			playerToUpdate.write(packet.toPacket());
	}

	protected void updateGroundItems(Player playerToUpdate) throws Exception {
		boolean changed = false;
		PacketBuilder packet = new PacketBuilder();
		packet.setID(99);
		for (Iterator<GroundItem> it$ = playerToUpdate.getLocalGroundItems().iterator(); it$.hasNext(); ) {
			GroundItem groundItem = it$.next();
			int offsetX = (groundItem.getX() - playerToUpdate.getX());
			int offsetY = (groundItem.getY() - playerToUpdate.getY());

			if (!playerToUpdate.withinGridRange(groundItem)) {
				if (offsetX > -128 && offsetY > -128 && offsetX < 128 && offsetY < 128) {
					packet.writeByte(255);
					packet.writeByte(offsetX);
					packet.writeByte(offsetY);
					//System.out.println("Removing " + groundItem + " with grounditem remove: " + offsetX + ", " + offsetY);
					it$.remove();
					changed = true;
				} else {
					playerToUpdate.getLocationsToClear().add(groundItem.getLocation());
					//System.out.println("Removing " + groundItem + " with region remove");
					it$.remove();
					changed = true;
				}
			} else if (groundItem.isRemoved() || !groundItem.visibleTo(playerToUpdate)) {
				packet.writeShort(groundItem.getID() + 32768);
				packet.writeByte(offsetX);
				packet.writeByte(offsetY);
				//System.out.println("Removing " + groundItem + " with isRemoved() remove: " + offsetX + ", " + offsetY);
				it$.remove();
				changed = true;
			}
		}

		for (GroundItem groundItem : playerToUpdate.getViewArea().getItemsInView()) {
			if (!playerToUpdate.withinGridRange(groundItem) || groundItem.isRemoved()
				|| !groundItem.visibleTo(playerToUpdate)
				|| playerToUpdate.getLocalGroundItems().contains(groundItem)) {
				continue;
			}
			packet.writeShort(groundItem.getID());
			int offsetX = groundItem.getX() - playerToUpdate.getX();
			int offsetY = groundItem.getY() - playerToUpdate.getY();
			packet.writeByte(offsetX);
			packet.writeByte(offsetY);
			playerToUpdate.getLocalGroundItems().add(groundItem);
			changed = true;

		}
		if (changed)
			playerToUpdate.write(packet.toPacket());
	}

	protected void updateWallObjects(Player playerToUpdate) throws Exception {
		boolean changed = false;
		PacketBuilder packet = new PacketBuilder();
		packet.setID(91);

		for (Iterator<GameObject> it$ = playerToUpdate.getLocalWallObjects().iterator(); it$.hasNext(); ) {
			GameObject o = it$.next();
			if (!playerToUpdate.withinGridRange(o) || (o.isRemoved() || !o.isVisibleTo(playerToUpdate))) {
				int offsetX = o.getX() - playerToUpdate.getX();
				int offsetY = o.getY() - playerToUpdate.getY();
				if (offsetX > -128 && offsetY > -128 && offsetX < 128 && offsetY < 128) {
					packet.writeShort(60000);
					packet.writeByte(offsetX);
					packet.writeByte(offsetY);
					packet.writeByte(o.getDirection());
					it$.remove();
					changed = true;
				} else {
					playerToUpdate.getLocationsToClear().add(o.getLocation());
					it$.remove();
					changed = true;
				}
			}
		}
		for (GameObject newObject : playerToUpdate.getViewArea().getGameObjectsInView()) {
			if (!playerToUpdate.withinGridRange(newObject) || newObject.isRemoved()
				|| !newObject.isVisibleTo(playerToUpdate) || newObject.getType() != 1
				|| playerToUpdate.getLocalWallObjects().contains(newObject)) {
				continue;
			}

			int offsetX = newObject.getX() - playerToUpdate.getX();
			int offsetY = newObject.getY() - playerToUpdate.getY();
			packet.writeShort(newObject.getID());
			packet.writeByte(offsetX);
			packet.writeByte(offsetY);
			packet.writeByte(newObject.getDirection());
			playerToUpdate.getLocalWallObjects().add(newObject);
			changed = true;
		}
		if (changed)
			playerToUpdate.write(packet.toPacket());
	}

	protected void sendClearLocations(Player p) {
		if (p.getLocationsToClear().size() > 0) {
			PacketBuilder packetBuilder = new PacketBuilder(211);
			for (Point point : p.getLocationsToClear()) {
				int offsetX = point.getX() - p.getX();
				int offsetY = point.getY() - p.getY();
				packetBuilder.writeShort(offsetX);
				packetBuilder.writeShort(offsetY);
			}
			p.getLocationsToClear().clear();
			p.write(packetBuilder.toPacket());
		}
	}

	public long doUpdates() throws Exception {
		final long gameStateStart			= System.currentTimeMillis();
		lastProcessPlayersDuration			= processPlayers();
		lastProcessNpcsDuration				= processNpcs();
		lastProcessMessageQueuesDuration	= processMessageQueues();
		lastUpdateClientsDuration			= updateClients();
		lastDoCleanupDuration				= doCleanup();
		lastExecuteWalkToActionsDuration	= executeWalkToActions();
		final long gameStateEnd				= System.currentTimeMillis();

		return gameStateEnd - gameStateStart;
		/*final int HORIZONTAL_PLANES = (World.MAX_WIDTH / RegionManager.REGION_SIZE) + 1;
		final int VERTICAL_PLANES = (World.MAX_HEIGHT / RegionManager.REGION_SIZE) + 1;
		for (int x = 0; x < HORIZONTAL_PLANES; ++x)
			for (int y = 0; y < VERTICAL_PLANES; ++y) {
				Region r = RegionManager.getRegion(x * RegionManager.REGION_SIZE, y * RegionManager.REGION_SIZE);
				if (r != null)
					for (Iterator<Player> i = r.getPlayers().iterator(); i.hasNext();) {
						if (i.next().isRemoved())
							i.remove();
					}
			}*/
	}

	protected final long updateClients() {
		final long updateClientsStart	= System.currentTimeMillis();
		for (Player p : players) {
			sendUpdatePackets(p);
			p.process();
		}
		final long updateClientsEnd		= System.currentTimeMillis();
		return updateClientsEnd - updateClientsStart;
	}

	protected final long doCleanup() {// it can do the teleport at this time.
		final long doCleanupStart	= System.currentTimeMillis();

		/*
		 * Reset the update related flags and unregister npcs flagged as
		 * unregistering
		 */
		for (Npc npc : npcs) {
			npc.resetMoved();
			npc.resetSpriteChanged();
			npc.getUpdateFlags().reset();
			npc.setTeleporting(false);
		}

		/*
		 * Reset the update related flags and unregister players that are
		 * flagged as unregistered
		 */
		for (Player player : players) {
			player.setTeleporting(false);
			player.resetSpriteChanged();
			player.getUpdateFlags().reset();
			player.resetMoved();
		}

		final long doCleanupEnd	= System.currentTimeMillis();

		return doCleanupEnd - doCleanupStart;
	}

	protected final long executeWalkToActions() {
		final long executeWalkToActionsStart	= System.currentTimeMillis();
		for (Player p : players) {
			if (p.getWalkToAction() != null) {
				if (p.getWalkToAction().shouldExecute()) {
					p.getWalkToAction().execute();
					p.setWalkToAction(null);
				}
			}
		}
		final long executeWalkToActionsEnd	= System.currentTimeMillis();

		return executeWalkToActionsEnd - executeWalkToActionsStart;
	}

	protected final long processNpcs() {
		final long processNpcsStart	= System.currentTimeMillis();
		for (Npc n : npcs) {
			try {
				if (n.isUnregistering()) {
					getServer().getWorld().unregisterNpc(n);
					continue;
				}

				// Only do the walking tick here if the NPC's walking tick matches the game tick
				if(!getServer().getConfig().WANT_CUSTOM_WALK_SPEED) {
					n.updatePosition();
				}
			} catch (Exception e) {
				LOGGER.error(
					"Error while updating " + n + " at position " + n.getLocation() + " loc: " + n.getLoc());
				LOGGER.catching(e);
			}
		}
		final long processNpcsEnd	= System.currentTimeMillis();
		return processNpcsEnd - processNpcsStart;
	}

	/**
	 * Updates the messages queues for each player
	 */
	protected final long processMessageQueues() {
		final long processMessageQueuesStart	= System.currentTimeMillis();
		for (Player p : players) {
			PrivateMessage pm = p.getNextPrivateMessage();
			if (pm != null) {
				Player affectedPlayer = getServer().getWorld().getPlayer(pm.getFriend());
				if (affectedPlayer != null) {
					if ((affectedPlayer.getSocial().isFriendsWith(p.getUsernameHash()) || !affectedPlayer.getSettings()
						.getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES))
						&& !affectedPlayer.getSocial().isIgnoring(p.getUsernameHash()) || p.isMod()) {
						ActionSender.sendPrivateMessageSent(p, affectedPlayer.getUsernameHash(), pm.getMessage());
						ActionSender.sendPrivateMessageReceived(affectedPlayer, p, pm.getMessage());
					}

					p.getWorld().getServer().getGameLogger().addQuery(new PMLog(p.getWorld(), p.getUsername(), pm.getMessage(),
						DataConversions.hashToUsername(pm.getFriend())));
				}
			}
		}
		for (Player p : players) {
			if (p.requiresOfferUpdate()) {
				ActionSender.sendTradeItems(p);
				p.setRequiresOfferUpdate(false);
			}
		}
		final long processMessageQueuesEnd	= System.currentTimeMillis();
		return processMessageQueuesEnd - processMessageQueuesStart;
	}

	/**
	 * Update the position of players, and check if who (and what) they are
	 * aware of needs updated
	 */
	protected final long processPlayers() {
		final long processPlayersStart	= System.currentTimeMillis();
		for (Player p : players) {
			// Checking login because we don't want to unregister more than once
			if (p.isUnregistering() && p.isLoggedIn()) {
				getServer().getWorld().unregisterPlayer(p);
				continue;
			}

			// Only do the walking tick here if the Players' walking tick matches the game tick
			if(!getServer().getConfig().WANT_CUSTOM_WALK_SPEED) {
				p.updatePosition();
			}

			if (p.getUpdateFlags().hasAppearanceChanged()) {
				p.incAppearanceID();
			}
		}
		final long processPlayersEnd	= System.currentTimeMillis();
		return processPlayersEnd - processPlayersStart;
	}

	public long getLastProcessPlayersDuration() {
		return lastProcessPlayersDuration;
	}

	public long getLastProcessNpcsDuration() {
		return lastProcessNpcsDuration;
	}

	public long getLastProcessMessageQueuesDuration() {
		return lastProcessMessageQueuesDuration;
	}

	public long getLastUpdateClientsDuration() {
		return lastUpdateClientsDuration;
	}

	public long getLastDoCleanupDuration() {
		return lastDoCleanupDuration;
	}

	public long getLastExecuteWalkToActionsDuration() {
		return lastExecuteWalkToActionsDuration;
	}
}
