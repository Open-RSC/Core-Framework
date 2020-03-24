package com.openrsc.server;

import com.openrsc.server.model.GlobalMessage;
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
import com.openrsc.server.database.impl.mysql.queries.logging.PMLog;
import com.openrsc.server.util.EntityList;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
			updatePlayerAppearances(p); // why seperate?
			updateNpcs(p);
			updateNpcAppearances(p); // why seperate?
			updateGameObjects(p);
			updateWallObjects(p);
			updateGroundItems(p);
			sendClearLocations(p);
			updateTimeouts(p); // maybe do this first?
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
			if (curTime - player.getLastMoved() >= (timeoutLimit + 60000) && player.loggedIn() && !player.hasElevatedPriveledges()) {
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
			// If you change the order of these blocks, bad things will probably occur.
			Npc localNpc = it$.next();

			// all the client cares about here is these next 4 bits are flipped on, even though it reads in 6 bits
			// the leftover 2 bits that get tacked on to the value it reads end up getting masked out for the check
			if (!playerToUpdate.withinRange(localNpc) || localNpc.isRemoved() || localNpc.isTeleporting() || localNpc.inCombat()) {
				packet.writeBits(1, 1);
				packet.writeBits(1, 1);
				packet.writeBits(0b11, 2);
				it$.remove();
				continue;
			}

			// This has a different block of code to the removal and sprite updates, because valid movement
			// sprite changes will fit into just 3 bits, and the other code block reads 4 bits for that value
			if (localNpc.hasMoved()) {
				packet.writeBits(1, 1);
				packet.writeBits(0, 1);
				packet.writeBits(localNpc.getSprite(), 3);
				continue;
			}

			// The reason this sprite value is larger than movements is because while all possible directions fit
			// into a 3-bit value, all possible character sprites do not.
			if (localNpc.spriteChanged()) {
				packet.writeBits(1, 1);
				packet.writeBits(1, 1);
				packet.writeBits(localNpc.getSprite(), 4);
				continue;
			}

			// nothing changed.
			packet.writeBits(0, 1);
		}
		for (Npc newNPC : playerToUpdate.getViewArea().getNpcsInView()) {
			if (playerToUpdate.getLocalNpcs().size() >= 255) {
                break;
            }

			// FIXME: Use Entity.isInvisibleTo for ned_hired check  Needs override in Npc class
			if (playerToUpdate.getLocalNpcs().contains(newNPC) || newNPC.isRemoved()
				|| newNPC.getID() == 194 && !playerToUpdate.getCache().hasKey("ned_hired")
				|| !playerToUpdate.withinRange(newNPC, (getServer().getConfig().VIEW_DISTANCE * 8) - 1) || (newNPC.isTeleporting() && !newNPC.inCombat())) {
				continue;
			}

			byte[] offsets = DataConversions.getMobPositionOffsets(newNPC.getLocation(), playerToUpdate.getLocation());
			packet.writeBits(newNPC.getIndex(), 12);
			packet.writeBits(offsets[0], 5);
			packet.writeBits(offsets[1], 5);
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
				// If you change the order of these blocks, bad things will probably occur.
				Player otherPlayer = it$.next();

				// all the client cares about here is these next 4 bits are flipped on, even though it reads in 6 bits
				// the leftover 2 bits that get tacked on to the value it reads end up getting masked out for the check
				if (!playerToUpdate.withinRange(otherPlayer) || !otherPlayer.loggedIn() || otherPlayer.isRemoved()
					|| otherPlayer.isTeleporting() || otherPlayer.isInvisibleTo(playerToUpdate)
					|| otherPlayer.inCombat() || otherPlayer.hasMoved()) {
					positionBuilder.writeBits(1, 1);
					positionBuilder.writeBits(1, 1);
					positionBuilder.writeBits(0b11, 2);
					it$.remove();
					playerToUpdate.getKnownPlayerAppearanceIDs().remove(otherPlayer.getUsernameHash());
					continue;
				}

				// This has a different block of code to the removal and sprite updates, because valid movement
				// sprite changes will fit into just 3 bits, and the other code block reads 4 bits for that value
				if (otherPlayer.hasMoved()) {
					positionBuilder.writeBits(1, 1);
					positionBuilder.writeBits(0, 1);
					positionBuilder.writeBits(otherPlayer.getSprite(), 3);
					continue;
				}

				// The reason this sprite value is larger than movements is because while all possible directions
				// fit into a 3-bit value, all possible character sprites do not.
				if(otherPlayer.spriteChanged()) {
					positionBuilder.writeBits(1, 1);
					positionBuilder.writeBits(1, 1);
					positionBuilder.writeBits(otherPlayer.getSprite(), 4);
					continue;
				}

				// nothing changed
				positionBuilder.writeBits(0, 1);
			}

			for (Player otherPlayer : playerToUpdate.getViewArea().getPlayersInView()) {
				if (playerToUpdate.getLocalPlayers().size() >= 255) {
                    break;
                }

				if (playerToUpdate.getLocalPlayers().contains(otherPlayer) || otherPlayer.equals(playerToUpdate)
					|| !otherPlayer.withinRange(playerToUpdate) || !otherPlayer.loggedIn()
					|| otherPlayer.isRemoved() || otherPlayer.isInvisibleTo(playerToUpdate)
					|| (otherPlayer.isTeleporting() && !otherPlayer.inCombat())) {
					continue;
				}

				byte[] offsets = DataConversions.getMobPositionOffsets(otherPlayer.getLocation(),
					playerToUpdate.getLocation());
				positionBuilder.writeBits(otherPlayer.getIndex(), 11);
				positionBuilder.writeBits(offsets[0], 5);
				positionBuilder.writeBits(offsets[1], 5);
				positionBuilder.writeBits(otherPlayer.getSprite(), 4);
				// previously seen flag. Conditionally triggers client to send us all the appearance tickets its seen
				positionBuilder.writeBits(playerToUpdate.getKnownPlayerAppearanceIDs().containsKey(
					otherPlayer.getUsernameHash()) ? 1 : 0, 1);

				playerToUpdate.getLocalPlayers().add(otherPlayer);
			}
		}
		positionBuilder.finishBitAccess();
		playerToUpdate.write(positionBuilder.toPacket());
	}

	public void updateNpcAppearances(Player player) {
		int updateSize = 0;
		PacketBuilder npcAppearancePacket = new PacketBuilder();
		npcAppearancePacket.setID(104);
		npcAppearancePacket.writeShort(0);
		for (Npc npc : player.getLocalNpcs()) {
			if (npc.getUpdateFlags().hasChatMessage()) {
				ChatMessage chatMessage = npc.getUpdateFlags().getChatMessage();
				npcAppearancePacket.writeShort(chatMessage.getSender().getIndex());
				npcAppearancePacket.writeByte((byte) 1);
				npcAppearancePacket.writeShort(chatMessage.getRecipient() == null ? -1 : chatMessage.getRecipient().getIndex());
				npcAppearancePacket.writeString(chatMessage.getMessageString());
				updateSize++;
			}
			if (npc.getUpdateFlags().hasTakenDamage()) {
				Damage damage = npc.getUpdateFlags().getDamage().get();
				npcAppearancePacket.writeShort(damage.getIndex());
				npcAppearancePacket.writeByte((byte) 2);
				npcAppearancePacket.writeByte((byte) damage.getDamage());
				npcAppearancePacket.writeByte((byte) damage.getCurHits());
				npcAppearancePacket.writeByte((byte) damage.getMaxHits());
				updateSize++;
			}
			if (npc.getUpdateFlags().hasFiredProjectile()) {
				Projectile projectile = npc.getUpdateFlags().getProjectile().get();
				npcAppearancePacket.writeShort(projectile.getCaster().getIndex());
				Entity victim = projectile.getVictim();
				npcAppearancePacket.writeByte(victim.isNpc() ? (byte) 3 : (byte) 4);
				npcAppearancePacket.writeShort(projectile.getType());
				npcAppearancePacket.writeShort(victim.getIndex());
				updateSize++;
			}
			if (npc.getUpdateFlags().hasSkulled()) {
				Skull skull = npc.getUpdateFlags().getSkull().get();
				npcAppearancePacket.writeShort(skull.getIndex());
				npcAppearancePacket.writeByte((byte) 5);
				npcAppearancePacket.writeByte((byte) skull.getSkull());
				updateSize++;
			}
			if (npc.getUpdateFlags().changedWield()) {
				Wield wield = npc.getUpdateFlags().getWield().get();
				npcAppearancePacket.writeShort(wield.getIndex());
				npcAppearancePacket.writeByte((byte) 6);
				npcAppearancePacket.writeByte((byte) wield.getWield());
				npcAppearancePacket.writeByte((byte) wield.getWield2());
				updateSize++;
			}
			if (npc.getUpdateFlags().changedWield2()) {
				Wield wield2 = npc.getUpdateFlags().getWield2().get();
				npcAppearancePacket.writeShort(wield2.getIndex());
				npcAppearancePacket.writeByte((byte) 6);
				npcAppearancePacket.writeByte((byte) wield2.getWield());
				npcAppearancePacket.writeByte((byte) wield2.getWield2());
				updateSize++;
			}
			if (npc.getUpdateFlags().hasBubbleNpc()) {
				BubbleNpc bubble = npc.getUpdateFlags().getActionBubbleNpc().get();
				npcAppearancePacket.writeShort(bubble.getOwner().getIndex());
				npcAppearancePacket.writeByte((byte) 7);
				npcAppearancePacket.writeShort(bubble.getID());
				updateSize++;
			}
		}
		if (updateSize == 0)
			return;
		npcAppearancePacket.setShort(0, (short) updateSize);
		player.write(npcAppearancePacket.toPacket());
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

		// TODO: Wasting cycles doing so much looping when in practice, it could be done with one loop over local
		// players followed by changing the packet buffer at the offset containing the update size after we're done

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
					boolean tutorialPlayer = sender.getLocation().onTutorialIsland() && !sender.hasElevatedPriveledges();
					boolean muted = sender.isMuted();

					int chatType = cm.getRecipient() == null ? (tutorialPlayer || muted ? 7 : 1)
						: cm.getRecipient() instanceof Player ? (tutorialPlayer || muted ? 7 : 6) : 6;
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
			if (!playerToUpdate.withinGridRange(o) || o.isRemoved() || o.isInvisibleTo(playerToUpdate)) {
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
				|| newObject.isInvisibleTo(playerToUpdate) || newObject.getType() != 0
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
					it$.remove();
					changed = true;
				} else {
					playerToUpdate.getLocationsToClear().add(groundItem.getLocation());
					it$.remove();
					changed = true;
				}
			} else if (groundItem.isRemoved() || groundItem.isInvisibleTo(playerToUpdate)) {
				// flip 15th bit
				packet.writeShort(groundItem.getID() | 0x8000);
				packet.writeByte(offsetX);
				packet.writeByte(offsetY);
				it$.remove();
				changed = true;
			}
		}

		for (GroundItem groundItem : playerToUpdate.getViewArea().getItemsInView()) {
			if (!playerToUpdate.withinGridRange(groundItem) || groundItem.isRemoved()
				|| groundItem.isInvisibleTo(playerToUpdate)
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
			if (!playerToUpdate.withinGridRange(o) || (o.isRemoved() || o.isInvisibleTo(playerToUpdate))) {
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
				|| newObject.isInvisibleTo(playerToUpdate) || newObject.getType() != 1
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
		// I notice a common theme where iteration is duplicated in this class without reason, often in places that I
		// can't see as negligible.  Should go through and redesign what is possible to improve.
		// TODO: Consolidate this method to use less loops.  Actually, isn't it better to make it a part of the
		// updatePlayers method, and leave this maybe just for global chat queue?  I almost feel global queue needs
		// redesign to just have global message queue per player and post these during updatePlayers too.
		for (Player p : players) {
			PrivateMessage pm = p.getNextPrivateMessage();
			if (pm != null) {
				Player affectedPlayer = getServer().getWorld().getPlayer(pm.getFriend());
				if (affectedPlayer != null) {
					if ((affectedPlayer.getSocial().isFriendsWith(p.getUsernameHash()) || !affectedPlayer.getSettings()
						.getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES))
						&& !affectedPlayer.getSocial().isIgnoring(p.getUsernameHash()) || p.isMod()) {
						ActionSender.sendPrivateMessageSent(p, affectedPlayer.getUsernameHash(), pm.getMessage(), false);
						ActionSender.sendPrivateMessageReceived(affectedPlayer, p, pm.getMessage(), false);
					}

					p.getWorld().getServer().getGameLogger().addQuery(new PMLog(p.getWorld(), p.getUsername(), pm.getMessage(),
						DataConversions.hashToUsername(pm.getFriend())));
				}
			}
		}
		GlobalMessage gm = null;
		while((gm = getServer().getWorld().getNextGlobalMessage()) != null) {
			for (Player p : players) {
				if (p == gm.getPlayer()) {
					ActionSender.sendPrivateMessageSent(gm.getPlayer(), -1L, gm.getMessage(), true);
				} else if (!p.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES)
						&& !p.getSocial().isIgnoring(gm.getPlayer().getUsernameHash()) || gm.getPlayer().isMod()) {
					ActionSender.sendPrivateMessageReceived(p, gm.getPlayer(), gm.getMessage(), true);
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
