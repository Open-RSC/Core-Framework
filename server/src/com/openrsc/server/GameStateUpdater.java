package com.openrsc.server;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.database.impl.mysql.queries.logging.PMLog;
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

	private long lastWorldUpdateDuration = 0;
	private long lastProcessPlayersDuration = 0;
	private long lastProcessNpcsDuration = 0;
	private long lastProcessMessageQueuesDuration = 0;
	private long lastUpdateClientsDuration = 0;
	private long lastDoCleanupDuration = 0;
	private long lastExecuteWalkToActionsDuration = 0;

	private final Server server;
	public final Server getServer() {
		return server;
	}

	public GameStateUpdater(final Server server) {
		this.server = server;
	}

	public void load() {

	}

	public void unload() {
		lastWorldUpdateDuration = 0;
		lastProcessPlayersDuration = 0;
		lastProcessNpcsDuration = 0;
		lastProcessMessageQueuesDuration = 0;
		lastUpdateClientsDuration = 0;
		lastDoCleanupDuration = 0;
		lastExecuteWalkToActionsDuration = 0;
	}

	// private static final int PACKET_UPDATETIMEOUTS = 0;
	public void sendUpdatePackets(final Player player) {
		// TODO: Should be private
		try {
			if (player.isUsingAuthenticClient()) {
				if (player.isChangingAppearance()) {
					sendAppearanceKeepalive(player);
				} else {
					updatePlayers(player);
					updatePlayerAppearances(player);
					updateNpcs(player);
					updateNpcAppearances(player);
					updateGameObjects(player);
					updateWallObjects(player);
					updateGroundItems(player);
					sendClearLocations(player);
					updateTimeouts(player);
				}
			} else {
				updatePlayers(player);
				updatePlayerAppearances(player);
				updateNpcs(player);
				updateNpcAppearances(player);
				updateGameObjects(player);
				updateWallObjects(player);
				updateGroundItems(player);
				sendClearLocations(player);
				updateTimeouts(player);
			}
		} catch (final Exception e) {
			LOGGER.catching(e);
			player.unregister(true, "Exception while updating player " + player.getUsername());
		}
	}

	/**
	 * Checks if the player has moved within the last X minutes
	 */
	protected void updateTimeouts(final Player player) {
		final long curTime = System.currentTimeMillis();
		final int timeoutLimit = getServer().getConfig().IDLE_TIMER; // 5 minute idle log out
		final int autoSave = getServer().getConfig().AUTO_SAVE; // 30 second autosave
		if (player.isRemoved() || player.getAttribute("dummyplayer", false)) {
			return;
		}
		if (curTime - player.getLastSaveTime() >= (autoSave) && player.loggedIn()) {
			player.timeIncrementActivity();
			player.save();
			player.setLastSaveTime(curTime);
		}

		if (curTime - player.getLastClientActivity() >= 30000) {
			player.unregister(false, "Client activity time-out");
		}

		if (player.warnedToMove()) {
			if (curTime - player.getLastMoved() >= (timeoutLimit + 60000) && player.loggedIn() && !player.hasElevatedPriveledges()) {
				player.unregister(true, "Movement time-out");
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

	protected void updateNpcs(final Player playerToUpdate) {
		final com.openrsc.server.net.PacketBuilder packet = new com.openrsc.server.net.PacketBuilder();
		packet.setID(ActionSender.Opcode.SEND_NPC_COORDS.opcode);
		packet.startBitAccess();
		packet.writeBits(playerToUpdate.getLocalNpcs().size(), 8);
		for (final Iterator<Npc> it$ = playerToUpdate.getLocalNpcs().iterator(); it$.hasNext(); ) {
			Npc localNpc = it$.next();

			if (!playerToUpdate.withinRange(localNpc) || localNpc.isRemoved() || localNpc.isRespawning() || localNpc.isTeleporting() || localNpc.inCombat()) {
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
		for (final Npc newNPC : playerToUpdate.getViewArea().getNpcsInView()) {
			if (playerToUpdate.getLocalNpcs().contains(newNPC) || newNPC.equals(playerToUpdate) || newNPC.isRemoved() || newNPC.isRespawning()
				|| newNPC.getID() == NpcId.NED_BOAT.id() && !playerToUpdate.getCache().hasKey("ned_hired")
				|| !playerToUpdate.withinRange(newNPC, (getServer().getConfig().VIEW_DISTANCE * 8) - 1) || (newNPC.isTeleporting() && !newNPC.inCombat())) {
				continue;
			} else if (playerToUpdate.getLocalNpcs().size() >= 255) {
				break;
			}
			final byte[] offsets = DataConversions.getMobPositionOffsets(newNPC.getLocation(), playerToUpdate.getLocation());
			packet.writeBits(newNPC.getIndex(), 12);
			if (playerToUpdate.isUsingAuthenticClient()) {
				packet.writeBits(offsets[0], 5);
				packet.writeBits(offsets[1], 5);
			} else {
				packet.writeBits(offsets[0], 6);
				packet.writeBits(offsets[1], 6);
			}
			packet.writeBits(newNPC.getSprite(), 4);
			packet.writeBits(newNPC.getID(), 10);

			playerToUpdate.getLocalNpcs().add(newNPC);
		}
		packet.finishBitAccess();
		playerToUpdate.write(packet.toPacket());
	}

	protected void updatePlayers(final Player playerToUpdate) {
		final com.openrsc.server.net.PacketBuilder positionBuilder = new com.openrsc.server.net.PacketBuilder();
		positionBuilder.setID(ActionSender.Opcode.SEND_PLAYER_COORDS.opcode);
		positionBuilder.startBitAccess();
		positionBuilder.writeBits(playerToUpdate.getX(), 11);
		positionBuilder.writeBits(playerToUpdate.getY(), 13);
		positionBuilder.writeBits(playerToUpdate.getSprite(), 4);
		positionBuilder.writeBits(playerToUpdate.getLocalPlayers().size(), 8);

		if (playerToUpdate.loggedIn()) {
			for (final Iterator<Player> it$ = playerToUpdate.getLocalPlayers().iterator(); it$.hasNext(); ) {
				final Player otherPlayer = it$.next();

				if (!playerToUpdate.withinRange(otherPlayer) || !otherPlayer.loggedIn() || otherPlayer.isRemoved()
					|| otherPlayer.isTeleporting() || otherPlayer.isInvisibleTo(playerToUpdate)
					|| otherPlayer.inCombat() || otherPlayer.hasMoved()) {
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

			for (final Player otherPlayer : playerToUpdate.getViewArea().getPlayersInView()) {
				if (playerToUpdate.getLocalPlayers().contains(otherPlayer) || otherPlayer.equals(playerToUpdate)
					|| !otherPlayer.withinRange(playerToUpdate) || !otherPlayer.loggedIn()
					|| otherPlayer.isRemoved() || otherPlayer.isInvisibleTo(playerToUpdate)
					|| (otherPlayer.isTeleporting() && !otherPlayer.inCombat())) {
					continue;
				}
				final byte[] offsets = DataConversions.getMobPositionOffsets(otherPlayer.getLocation(),
					playerToUpdate.getLocation());
				positionBuilder.writeBits(otherPlayer.getIndex(), 11);
				if (playerToUpdate.isUsingAuthenticClient()) {
					positionBuilder.writeBits(offsets[0], 5);
					positionBuilder.writeBits(offsets[1], 5);
				} else {
					positionBuilder.writeBits(offsets[0], 6);
					positionBuilder.writeBits(offsets[1], 6);
				}
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

	public void updateNpcAppearances(final Player player) {
		final ConcurrentLinkedQueue<Damage> npcsNeedingHitsUpdate = new ConcurrentLinkedQueue<>();
		final ConcurrentLinkedQueue<ChatMessage> npcMessagesNeedingDisplayed = new ConcurrentLinkedQueue<>();
		final ConcurrentLinkedQueue<Projectile> npcProjectilesNeedingDisplayed = new ConcurrentLinkedQueue<>();
		final ConcurrentLinkedQueue<Skull> npcSkullsNeedingDisplayed = new ConcurrentLinkedQueue<>();
		final ConcurrentLinkedQueue<Wield> npcWieldsNeedingDisplayed = new ConcurrentLinkedQueue<>();
		final ConcurrentLinkedQueue<BubbleNpc> npcBubblesNeedingDisplayed = new ConcurrentLinkedQueue<>();

		for (final Npc npc : player.getLocalNpcs()) {
			final UpdateFlags updateFlags = npc.getUpdateFlags();
			if (updateFlags.hasChatMessage()) {
				ChatMessage chatMessage = updateFlags.getChatMessage();
				npcMessagesNeedingDisplayed.add(chatMessage);
			}
			if (updateFlags.hasSkulled()) {
				Skull skull = updateFlags.getSkull().get();
				npcSkullsNeedingDisplayed.add(skull);
			}
			if (updateFlags.changedWield()) {
				Wield wield = updateFlags.getWield().get();
				npcWieldsNeedingDisplayed.add(wield);
			}
			if (updateFlags.changedWield2()) {
				Wield wield2 = updateFlags.getWield2().get();
				npcWieldsNeedingDisplayed.add(wield2);
			}
			if (updateFlags.hasTakenDamage()) {
				Damage damage = updateFlags.getDamage().get();
				npcsNeedingHitsUpdate.add(damage);
			}
			if (updateFlags.hasFiredProjectile()) {
				Projectile projectileFired = updateFlags.getProjectile().get();
				npcProjectilesNeedingDisplayed.add(projectileFired);
			}
			if (updateFlags.hasBubbleNpc()) {
					BubbleNpc bubble = updateFlags.getActionBubbleNpc().get();
					npcBubblesNeedingDisplayed.add(bubble);
			}
		}
		final int updateSize = npcMessagesNeedingDisplayed.size() + npcsNeedingHitsUpdate.size()
			+ npcProjectilesNeedingDisplayed.size() + npcSkullsNeedingDisplayed.size() + npcWieldsNeedingDisplayed.size() + npcBubblesNeedingDisplayed.size();
		if (updateSize > 0) {
			final PacketBuilder npcAppearancePacket = new PacketBuilder();
			npcAppearancePacket.setID(ActionSender.Opcode.SEND_UPDATE_NPC.opcode);
			npcAppearancePacket.writeShort(updateSize);

			ChatMessage chatMessage;
			while ((chatMessage = npcMessagesNeedingDisplayed.poll()) != null) {
				npcAppearancePacket.writeShort(chatMessage.getSender().getIndex());
				npcAppearancePacket.writeByte((byte) 1);
				npcAppearancePacket.writeShort(chatMessage.getRecipient() == null ? -1 : chatMessage.getRecipient().getIndex());
				if (player.isUsingAuthenticClient()) {
					npcAppearancePacket.writeRSCString(chatMessage.getMessageString());
				} else {
					npcAppearancePacket.writeString(chatMessage.getMessageString());
				}
			}
			Damage npcNeedingHitsUpdate;
			while ((npcNeedingHitsUpdate = npcsNeedingHitsUpdate.poll()) != null) {
				npcAppearancePacket.writeShort(npcNeedingHitsUpdate.getIndex());
				npcAppearancePacket.writeByte((byte) 2);
				npcAppearancePacket.writeByte((byte) npcNeedingHitsUpdate.getDamage());
				npcAppearancePacket.writeByte((byte) npcNeedingHitsUpdate.getCurHits());
				npcAppearancePacket.writeByte((byte) npcNeedingHitsUpdate.getMaxHits());
			}
			if (!player.isUsingAuthenticClient()) {
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
				Skull npcNeedingSkullUpdate;
				while ((npcNeedingSkullUpdate = npcSkullsNeedingDisplayed.poll()) != null) {
					npcAppearancePacket.writeShort(npcNeedingSkullUpdate.getIndex());
					npcAppearancePacket.writeByte((byte) 5);
					npcAppearancePacket.writeByte((byte) npcNeedingSkullUpdate.getSkull());
				}
				Wield npcNeedingWieldUpdate;
				while ((npcNeedingWieldUpdate = npcWieldsNeedingDisplayed.poll()) != null) {
					npcAppearancePacket.writeShort(npcNeedingWieldUpdate.getIndex());
					npcAppearancePacket.writeByte((byte) 6);
					npcAppearancePacket.writeByte((byte) npcNeedingWieldUpdate.getWield());
					npcAppearancePacket.writeByte((byte) npcNeedingWieldUpdate.getWield2());
				}
				BubbleNpc npcNeedingBubbleUpdate;
				while ((npcNeedingBubbleUpdate = npcBubblesNeedingDisplayed.poll()) != null) {
					npcAppearancePacket.writeShort(npcNeedingBubbleUpdate.getOwner().getIndex());
					npcAppearancePacket.writeByte((byte) 7);
					npcAppearancePacket.writeShort(npcNeedingBubbleUpdate.getID());
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
	public void updatePlayerAppearances(final Player player) {
		final ArrayDeque<Bubble> bubblesNeedingDisplayed = new ArrayDeque<>();
		final ArrayDeque<ChatMessage> chatMessagesNeedingDisplayed = new ArrayDeque<>();
		final ArrayDeque<Projectile> projectilesNeedingDisplayed = new ArrayDeque<>();
		final ArrayDeque<Damage> playersNeedingDamageUpdate = new ArrayDeque<>();
		final ArrayDeque<HpUpdate> playersNeedingHpUpdate = new ArrayDeque<HpUpdate>();
		final ArrayDeque<Player> playersNeedingAppearanceUpdate = new ArrayDeque<>();

		if (player.getUpdateFlags().hasBubble()) {
			Bubble bubble = player.getUpdateFlags().getActionBubble().get();
			bubblesNeedingDisplayed.add(bubble);
		}
		if (player.getUpdateFlags().hasFiredProjectile()) {
			Projectile projectileFired = player.getUpdateFlags().getProjectile().get();
			projectilesNeedingDisplayed.add(projectileFired);
		}
		boolean myBlockAll = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES, player.isUsingAuthenticClient())
			== PlayerSettings.BlockingMode.All.id();
		if (player.getUpdateFlags().hasChatMessage() && (!myBlockAll || player.isMod()
			|| player.getUpdateFlags().getChatMessage().getRecipient() != null)) {
			ChatMessage chatMessage = player.getUpdateFlags().getChatMessage();
			if (!chatMessage.getMuted() || player.hasElevatedPriveledges())
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
		for (final Player otherPlayer : player.getLocalPlayers()) {
			final UpdateFlags updateFlags = otherPlayer.getUpdateFlags();

			boolean otherBlockAll = otherPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES, otherPlayer.isUsingAuthenticClient())
				== PlayerSettings.BlockingMode.All.id();
			boolean blockAll = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES, player.isUsingAuthenticClient())
				== PlayerSettings.BlockingMode.All.id();
			boolean blockNone = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES, player.isUsingAuthenticClient())
				== PlayerSettings.BlockingMode.None.id();

			if(otherPlayer.getUsername().trim().equalsIgnoreCase("kenix") && player.getUsername().trim().equalsIgnoreCase("kenix")) {
				LOGGER.info("UF: " + updateFlags + ", isTeleporting: " + otherPlayer.isTeleporting() + ", Override: " + player.requiresAppearanceUpdateForPeek(otherPlayer));
			}

			if (updateFlags.hasBubble()) {
				final Bubble bubble = updateFlags.getActionBubble().get();
				bubblesNeedingDisplayed.add(bubble);
			}
			if (updateFlags.hasFiredProjectile()) {
				Projectile projectileFired = updateFlags.getProjectile().get();
				projectilesNeedingDisplayed.add(projectileFired);
			}

			if (updateFlags.hasChatMessage()
				&& (((player.getSocial().isFriendsWith(otherPlayer.getUsernameHash()) && !blockAll)
				|| (!player.getSocial().isFriendsWith(otherPlayer.getUsernameHash()) && blockNone))
				&& !player.getSocial().isIgnoring(otherPlayer.getUsernameHash()) && !otherBlockAll
				|| otherPlayer.isMod() || updateFlags.getChatMessage().getRecipient() != null)) {
				ChatMessage chatMessage = updateFlags.getChatMessage();
				if (!chatMessage.getMuted() || player.hasElevatedPriveledges())
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
			if (player.requiresAppearanceUpdateFor(otherPlayer)) {
				playersNeedingAppearanceUpdate.add(otherPlayer);
			}
		}
		issuePlayerAppearanceUpdatePacket(player, bubblesNeedingDisplayed, chatMessagesNeedingDisplayed,
			projectilesNeedingDisplayed, playersNeedingDamageUpdate, playersNeedingHpUpdate, playersNeedingAppearanceUpdate);
	}

	private void issuePlayerAppearanceUpdatePacket(final Player player, final Queue<Bubble> bubblesNeedingDisplayed,
												   final Queue<ChatMessage> chatMessagesNeedingDisplayed, final Queue<Projectile> projectilesNeedingDisplayed,
												   final Queue<Damage> playersNeedingDamageUpdate,final Queue<HpUpdate> playersNeedingHpUpdate,
												   final Queue<Player> playersNeedingAppearanceUpdate) {
		if (player.loggedIn()) {
			final int updateSize = bubblesNeedingDisplayed.size() + chatMessagesNeedingDisplayed.size()
				+ playersNeedingDamageUpdate.size() + projectilesNeedingDisplayed.size()
				+ playersNeedingAppearanceUpdate.size() + playersNeedingHpUpdate.size();

			if (updateSize > 0) {
				final PacketBuilder appearancePacket = new PacketBuilder();
				appearancePacket.setID(ActionSender.Opcode.SEND_UPDATE_PLAYERS.opcode);
				appearancePacket.writeShort(updateSize); // This is how many updates there are in this packet

				// Note: The order that these updates are written to packet 234 is not authentic.
				// Probably the correct way to handle it is *not* having different arrays for every type of update.
				// It looks more like "playersNeedingXXXUpdate" would just be one array where mixed update types are put as-acquired.
				// There is no consistent order of update types in the real server's data.
				// It is also not consistent in order of PID. I suspect that they are ordered "as acquired and processed".
				// TODO: entire server structure regarding how UpdateFlags are used is probably wrong, but it doesn't matter much.
				// It'll be frame-accurate anyway. -- 2020-08-26 Logg

				// Update Type 0, Bubble
				Bubble b;
				while ((b = bubblesNeedingDisplayed.poll()) != null) {
					appearancePacket.writeShort(b.getOwner().getIndex());
					appearancePacket.writeByte((byte) 0);
					appearancePacket.writeShort(b.getID());
				}

				// Update Type 1: Chat Message
				// AND
				// Update Type 6: Quest Chat Message
				ChatMessage cm;
				while ((cm = chatMessagesNeedingDisplayed.poll()) != null) {
					Player sender = (Player) cm.getSender();
					boolean tutorialPlayer = sender.getLocation().onTutorialIsland() && !sender.hasElevatedPriveledges();
					boolean muted = sender.isMuted();

					// Determine Update Type
					int updateType;
					if (cm.getRecipient() == null) {
						if (tutorialPlayer || muted) {
							updateType = 7; // Not authentic! There is no update type 7.
						} else {
							updateType = 1; // Public Chat
						}
					} else {
						if (cm.getRecipient() instanceof Player) {
							if (tutorialPlayer || muted) {
								updateType = 7; // Not authentic! There is no update type 7.
							} else {
								updateType = 6; // Quest Chat
							}
						} else {
							updateType = 6; // Quest Chat
						}
					}

					if (player.isUsingAuthenticClient()) {
						String message = cm.getMessageString();
						if (updateType == 7) {
							if (player.hasElevatedPriveledges()) {
								// Just prepend "Muted" to message, could be faked but doesn't matter.
								message = "(Muted) " + message;
								if (cm.getRecipient() == null) {
									updateType = 1;
								} else {
									updateType = 6;
								}
							}
						}
						if (updateType != 7) {
							appearancePacket.writeShort(cm.getSender().getIndex());
							appearancePacket.writeByte(updateType);
							if (updateType != 6) {
								appearancePacket.writeByte(sender.getIconAuthentic());
							}
							appearancePacket.writeRSCString(message);
						} else {
							LOGGER.error("extraneous chat update packet will crash the authentic client...!");
						}

					} else {
						// Non Authentic OpenRSC client
						appearancePacket.writeShort(cm.getSender().getIndex());
						appearancePacket.writeByte(updateType);

						if (updateType == 1 || updateType == 7) {
							if (cm.getSender() != null && cm.getSender() instanceof Player)
								appearancePacket.writeInt(sender.getIcon());
						}

						if (updateType == 7) {
							appearancePacket.writeByte(sender.isMuted() ? 1 : 0);
							appearancePacket.writeByte(sender.getLocation().onTutorialIsland() ? 1 : 0);
						}

						if (updateType != 7 || player.isAdmin()) {
							appearancePacket.writeString(cm.getMessageString());
						} else {
							appearancePacket.writeString("");
						}
					}
				}

				// Update Type 2: Damage Update
				Damage playerNeedingHitsUpdate;
				while ((playerNeedingHitsUpdate = playersNeedingDamageUpdate.poll()) != null) {
					appearancePacket.writeShort(playerNeedingHitsUpdate.getIndex());
					appearancePacket.writeByte((byte) 2);
					appearancePacket.writeByte((byte) playerNeedingHitsUpdate.getDamage());
					appearancePacket.writeByte((byte) playerNeedingHitsUpdate.getCurHits());
					appearancePacket.writeByte((byte) playerNeedingHitsUpdate.getMaxHits());
				}

				// Update Types 3 & 4: Projectile Update (draws the projectile)
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

				// Update Type 5: Player appearance and identity
				Player playerNeedingAppearanceUpdate;
				while ((playerNeedingAppearanceUpdate = playersNeedingAppearanceUpdate.poll()) != null) {
					PlayerAppearance appearance = playerNeedingAppearanceUpdate.getSettings().getAppearance();

					appearancePacket.writeShort((short) playerNeedingAppearanceUpdate.getIndex());
					appearancePacket.writeByte((byte) 5);
					if (player.isUsingAuthenticClient()) {
                        // This is unused by the authentic 233+ clients, but is meant to be "Appearance ID", which changes when the player's appearance changes
                        // TODO: look into this more
						appearancePacket.writeShort(0);
					}
					if (player.isUsingAuthenticClient()) {
						appearancePacket.writeZeroQuotedString(playerNeedingAppearanceUpdate.getUsername());
						appearancePacket.writeZeroQuotedString(playerNeedingAppearanceUpdate.getUsername()); // Pretty sure this is unnecessary & always redundant authentically.
					} else {
						appearancePacket.writeString(playerNeedingAppearanceUpdate.getUsername());
					}


                    // Handle Invisibility & Invulnerability in the authentic client
					if (player.isUsingAuthenticClient() &&
                        (playerNeedingAppearanceUpdate.stateIsInvisible() ||
                            playerNeedingAppearanceUpdate.stateIsInvulnerable())) {
                        int[] wornItems = playerNeedingAppearanceUpdate.getWornItems();

                        // All possible boots to choose from
                        final int regularBoots = 12;
                        final int redGnomeBoots = 204;
                        final int greenGnomeBoots = 205;
                        final int blueGnomeBoots = 206;
                        final int yellowGnomeBoots = 207;
                        final int skyBlueGnomeBoots = 208;
                        final int desertBoots = 212;
                        final int shadowWarriorBoots = 227;
                        int bootColour = shadowWarriorBoots; // default
                        if (wornItems[9] != 0) {
                            // if player is already wearing boots, we can let them choose their colour. :-)
                            bootColour = wornItems[9];
                        }

                        final int runeShieldSprite = 103;
                        final int dragonShieldSprite = 225;
                        int shieldSprite = 0; // default to invisible
                        if (playerNeedingAppearanceUpdate.stateIsInvulnerable()) {
                            if (wornItems[3] == dragonShieldSprite) {
                                shieldSprite = runeShieldSprite;
                            } else {
                                shieldSprite = dragonShieldSprite;
                            }
                        }

                        // these two gloves are the only ones that exist.
                        final int lightGloves = 47;
                        final int darkGloves = 156;
                        int gloveColour = lightGloves; // default
                        if (wornItems[8] != 0) {
                            // if player is already wearing gloves, we can let them choose their colour. :-)
                            gloveColour = wornItems[8];
                        }

                        // if player is just invulnerable & not invisible, give them a dark-robed appearance
                        int headSprite = 0; // default to invisible
                        int hatSprite = 0;
                        int bodySprite = 0;
                        int legSprite = 0;
                        int pantsSprite = 0;
                        int shirtSprite = 0;
                        int amuletSprite = 0;
                        if (!playerNeedingAppearanceUpdate.stateIsInvisible()) {
                            headSprite = wornItems[0];
                            if (wornItems[5] == 0) {
                                hatSprite = 19; // black helm
                                headSprite = 0;
                            } else {
                                hatSprite = wornItems[5];
                            }

                            // dark robes
                            bodySprite = 183;
                            legSprite = 184;
                            pantsSprite = 3;
                            shirtSprite = 5;
                            amuletSprite = 172; // amulet of lucien
                        }

                        appearancePacket.writeByte((byte) 11); // Equipment count
                        appearancePacket.writeByte((byte) headSprite);
                        appearancePacket.writeByte((byte) shirtSprite);
                        appearancePacket.writeByte((byte) pantsSprite);
                        appearancePacket.writeByte((byte) shieldSprite);  // Shield is used to denote if invulnerable while invisible
                        appearancePacket.writeByte((byte) wornItems[4]);  // Weapon can stay
                        appearancePacket.writeByte((byte) hatSprite);
                        appearancePacket.writeByte((byte) bodySprite);
                        appearancePacket.writeByte((byte) legSprite);
                        appearancePacket.writeByte((byte) gloveColour);
                        appearancePacket.writeByte((byte) bootColour);
                        appearancePacket.writeByte((byte) amuletSprite);
                        // No Cape
                    } else {
                        appearancePacket.writeByte((byte) playerNeedingAppearanceUpdate.getWornItems().length);
                        for (int i : playerNeedingAppearanceUpdate.getWornItems()) {
                            if (player.isUsingAuthenticClient()) {
                                appearancePacket.writeByte(i & 0xFF);
                            } else {
                                appearancePacket.writeShort(i);
                            }
                        }
                    }

                    appearancePacket.writeByte(appearance.getHairColour());
                    appearancePacket.writeByte(appearance.getTopColour());
                    appearancePacket.writeByte(appearance.getTrouserColour());
                    appearancePacket.writeByte(appearance.getSkinColour());
                    appearancePacket.writeByte((byte) playerNeedingAppearanceUpdate.getCombatLevel());
                    appearancePacket.writeByte((byte) playerNeedingAppearanceUpdate.getSkullType());

					if (!player.isUsingAuthenticClient()) {
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
				}

				if (!player.isUsingAuthenticClient()) {
					// Non authentic type 9. In authentic network protocol, this information is just in type 2.
					HpUpdate playerNeedingHpUpdate;
					while ((playerNeedingHpUpdate = playersNeedingHpUpdate.poll()) != null) {
						appearancePacket.writeShort(playerNeedingHpUpdate.getIndex());
						appearancePacket.writeByte((byte) 9);
						appearancePacket.writeByte((byte) playerNeedingHpUpdate.getCurHits());
						appearancePacket.writeByte((byte) playerNeedingHpUpdate.getMaxHits());
					}
				}

				player.write(appearancePacket.toPacket());
			}
		}
	}

	protected void updateGameObjects(final Player playerToUpdate) {
		boolean changed = false;
		final PacketBuilder packet = new PacketBuilder();
		packet.setID(ActionSender.Opcode.SEND_SCENERY_HANDLER.opcode);
		// TODO: Unloading scenery is not handled correctly.
		//       According to RSC+ replays, the server never tells the client to unload objects until
		//       a region is unloaded. It then instructs the client to only unload the region.

		if (playerToUpdate.isUsingAuthenticClient()) {
			// authentic client; remove scenery
			// 2020-10-03; still not authentic, but should be a better experience
			for (final Iterator<GameObject> it$ = playerToUpdate.getLocalGameObjects().iterator(); it$.hasNext(); ) {
				final GameObject o = it$.next();
				if (!playerToUpdate.within5GridRange(o) || o.isRemoved() || o.isInvisibleTo(playerToUpdate)) {
					final int offsetX = o.getX() - playerToUpdate.getX();
					final int offsetY = o.getY() - playerToUpdate.getY();
					if (o.isRemoved() && offsetX > -16 && offsetY > -16 && offsetX < 16 && offsetY < 16) {
						packet.writeShort(60000);
						packet.writeByte(offsetX);
						packet.writeByte(offsetY);
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
		} else { // non-authentic client; remove scenery
			for (final Iterator<GameObject> it$ = playerToUpdate.getLocalGameObjects().iterator(); it$.hasNext(); ) {
				final GameObject o = it$.next();
				if (!playerToUpdate.withinGridRange(o) || o.isRemoved() || o.isInvisibleTo(playerToUpdate)) {
					final int offsetX = o.getX() - playerToUpdate.getX();
					final int offsetY = o.getY() - playerToUpdate.getY();
					//If the object is close enough we can use regular way to remove:
					if (offsetX > -128 && offsetY > -128 && offsetX < 128 && offsetY < 128) {
						packet.writeShort(60000);
						packet.writeByte(offsetX);
						packet.writeByte(offsetY);
						if (!playerToUpdate.isUsingAuthenticClient()) {
							packet.writeByte(o.getDirection());
						}
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
		}

		// Add scenery
		for (final GameObject newObject : playerToUpdate.getViewArea().getGameObjectsInView()) {
			if (!playerToUpdate.withinGridRange(newObject) || newObject.isRemoved()
				|| newObject.isInvisibleTo(playerToUpdate) || newObject.getType() != 0
				|| playerToUpdate.getLocalGameObjects().contains(newObject)) {
				continue;
			}

			packet.writeShort(newObject.getID());
			final int offsetX = newObject.getX() - playerToUpdate.getX();
			final int offsetY = newObject.getY() - playerToUpdate.getY();
			packet.writeByte(offsetX);
			packet.writeByte(offsetY);
			if (!playerToUpdate.isUsingAuthenticClient()) {
				packet.writeByte(newObject.getDirection());
			}
			playerToUpdate.getLocalGameObjects().add(newObject);
			changed = true;
		}
		if (changed)
			playerToUpdate.write(packet.toPacket());
	}

	protected void updateGroundItems(final Player playerToUpdate) {
		boolean changed = false;
		final PacketBuilder packet = new PacketBuilder();
		packet.setID(ActionSender.Opcode.SEND_GROUND_ITEM_HANDLER.opcode);
		for (final Iterator<GroundItem> it$ = playerToUpdate.getLocalGroundItems().iterator(); it$.hasNext(); ) {
			final GroundItem groundItem = it$.next();
			final int offsetX = (groundItem.getX() - playerToUpdate.getX());
			final int offsetY = (groundItem.getY() - playerToUpdate.getY());

			if (!playerToUpdate.withinGridRange(groundItem)) {
				if (offsetX > -128 && offsetY > -128 && offsetX < 128 && offsetY < 128) {
					packet.writeByte(255);
					packet.writeByte(offsetX);
					packet.writeByte(offsetY);
					if (!playerToUpdate.isUsingAuthenticClient()) {
						if (getServer().getConfig().WANT_BANK_NOTES)
							packet.writeByte(groundItem.getNoted() ? 1 : 0);
					}
				} else {
					playerToUpdate.getLocationsToClear().add(groundItem.getLocation());
				}
				it$.remove();
				changed = true;
			} else if (groundItem.isRemoved() || groundItem.isInvisibleTo(playerToUpdate)) {
				packet.writeShort(groundItem.getID() + 32768);
				packet.writeByte(offsetX);
				packet.writeByte(offsetY);
				if (!playerToUpdate.isUsingAuthenticClient()) {
					if (getServer().getConfig().WANT_BANK_NOTES)
						packet.writeByte(groundItem.getNoted() ? 1 : 0);
				}
				//System.out.println("Removing " + groundItem + " with isRemoved() remove: " + offsetX + ", " + offsetY);
				it$.remove();
				changed = true;
			}
		}

		for (final GroundItem groundItem : playerToUpdate.getViewArea().getItemsInView()) {
			if (!playerToUpdate.withinGridRange(groundItem) || groundItem.isRemoved()
				|| groundItem.isInvisibleTo(playerToUpdate)
				|| playerToUpdate.getLocalGroundItems().contains(groundItem)) {
				continue;
			}
			packet.writeShort(groundItem.getID());
			final int offsetX = groundItem.getX() - playerToUpdate.getX();
			final int offsetY = groundItem.getY() - playerToUpdate.getY();
			packet.writeByte(offsetX);
			packet.writeByte(offsetY);
			if (!playerToUpdate.isUsingAuthenticClient()) {
				if (getServer().getConfig().WANT_BANK_NOTES) {
					packet.writeByte(groundItem.getNoted() ? 1 : 0);
				}
			}
			playerToUpdate.getLocalGroundItems().add(groundItem);
			changed = true;
		}
		if (changed) {
			playerToUpdate.write(packet.toPacket());
		}
	}

	protected void updateWallObjects(final Player playerToUpdate) {
		boolean changed = false;
		final PacketBuilder packet = new PacketBuilder();
		packet.setID(ActionSender.Opcode.SEND_BOUNDARY_HANDLER.opcode);

		// remove all boundaries that need to be removed
		for (final Iterator<GameObject> it$ = playerToUpdate.getLocalWallObjects().iterator(); it$.hasNext(); ) {
			final GameObject o = it$.next();
			if (!playerToUpdate.withinGridRange(o) || (o.isRemoved() || o.isInvisibleTo(playerToUpdate))) {
				final int offsetX = o.getX() - playerToUpdate.getX();
				final int offsetY = o.getY() - playerToUpdate.getY();
				if (offsetX > -128 && offsetY > -128 && offsetX < 128 && offsetY < 128) {
					if (playerToUpdate.isUsingAuthenticClient()) {
                        // The authentic server does not really send removals for boundaries.
                        // The client is able to handle having boundaries overwritten by new boundaries, but
                        // it doesn't correctly handle having boundaries outright removed.
                        //
                        // The RSC server may have sent proper removals at one time, the structure is there in the client,
                        // but in 2018, the server does something which confuses me, and it should be considered a bug in the server.
                        //
                        // Sometimes when adding a boundary, it will send a removal for some unrelated coordinate first.
                        // The coordinate it specifies for boundary removal *does not* have a boundary at that location.
                        // If it did have a boundary, it would cause erroneous extraneous removals of nearby boundaries.
                        // I haven't spent a lot of time looking at it to discern any further pattern, if there is one. Sorry.
                        //
                        // TODO: determine the pattern that the server uses to send its buggy "random" boundary removal instructions
                        // Until this is implemented, the server will not be 100% authentic to 2018 RSC.
                        // (Also, removals & additions are intertwined, not in a removal block & addition block, as structured here)
                        //
                        // I went through the effort of writing code in the RSCMinus scraper to check if the boundary removal command
                        // *ever* successfully removed a boundary.
                        // ...
                        // **It never does.**
                        // ...
                        // Because X & Y coordinates never match with the coordinate of a boundary that has been added,
                        // all instances where 0xFF removal are invoked are effectively NO-OPs.
                        // Therefore, no buggy behaviour from omitting the ability to remove boundaries should arise.

                        /* RSC235 Compatible removal code, shouldn't be used
                        packet.writeByte(0xFF);
                        packet.writeByte(offsetX);
                        packet.writeByte(offsetY);
                        */

					} else {
						packet.writeShort(60000);
						packet.writeByte(offsetX);
						packet.writeByte(offsetY);
						packet.writeByte(o.getDirection());
                        changed = true;
					}
					it$.remove();
				} else {
					playerToUpdate.getLocationsToClear().add(o.getLocation());
					it$.remove();
					changed = true;
				}
			}
		}

		// add all new boundaries to be added
		for (final GameObject newObject : playerToUpdate.getViewArea().getGameObjectsInView()) {
			if (!playerToUpdate.withinGridRange(newObject) || newObject.isRemoved()
				|| newObject.isInvisibleTo(playerToUpdate) || newObject.getType() != 1
				|| playerToUpdate.getLocalWallObjects().contains(newObject)) {
				continue;
			}

			final int offsetX = newObject.getX() - playerToUpdate.getX();
			final int offsetY = newObject.getY() - playerToUpdate.getY();
			packet.writeShort(newObject.getID());
			packet.writeByte(offsetX);
			packet.writeByte(offsetY);
			packet.writeByte(newObject.getDirection());
			playerToUpdate.getLocalWallObjects().add(newObject);
			changed = true;
		}
		if (changed) {
			playerToUpdate.write(packet.toPacket());
		}
	}

	protected void sendAppearanceKeepalive(final Player player) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(ActionSender.Opcode.SEND_APPEARANCE_KEEPALIVE.opcode); // 213
		player.write(s.toPacket());
	}

	protected void sendClearLocations(final Player player) {
		if (player.getLocationsToClear().size() > 0) {
			final PacketBuilder packetBuilder = new PacketBuilder(ActionSender.Opcode.SEND_REMOVE_WORLD_ENTITY.opcode);
			for (final Point point : player.getLocationsToClear()) {
				final int offsetX = point.getX() - player.getX();
				final int offsetY = point.getY() - player.getY();
				packetBuilder.writeShort(offsetX);
				packetBuilder.writeShort(offsetY);
			}
			player.getLocationsToClear().clear();
			player.write(packetBuilder.toPacket());
		}
	}

	public long doUpdates() {
		final long gameStateStart = System.currentTimeMillis();
		lastWorldUpdateDuration = updateWorld();
		lastProcessPlayersDuration = processPlayers();
		lastProcessNpcsDuration = processNpcs();
		lastProcessMessageQueuesDuration = processMessageQueues();
		lastUpdateClientsDuration = updateClients();
		lastDoCleanupDuration = doCleanup();
		lastExecuteWalkToActionsDuration = executeWalkToActions();
		final long gameStateEnd = System.currentTimeMillis();

		return gameStateEnd - gameStateStart;
	}

	protected final long updateWorld() {
		final long updateWorldStart = System.currentTimeMillis();
		getServer().getWorld().run();
		final long updateWorldEnd = System.currentTimeMillis();
		return updateWorldEnd - updateWorldStart;
	}

	protected final long updateClients() {
		final long updateClientsStart	= System.currentTimeMillis();
		for (final Player player : getServer().getWorld().getPlayers()) {
			sendUpdatePackets(player);
			player.process();
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
		for (final Npc npc : getServer().getWorld().getNpcs()) {
			npc.setHasMoved(false);
			npc.resetSpriteChanged();
			npc.getUpdateFlags().reset();
			npc.setTeleporting(false);
		}

		/*
		 * Reset the update related flags and unregister players that are
		 * flagged as unregistered
		 */
		for (final Player player : getServer().getWorld().getPlayers()) {
			player.setTeleporting(false);
			player.resetSpriteChanged();
			player.getUpdateFlags().reset();
			player.setHasMoved(false);
		}

		final long doCleanupEnd	= System.currentTimeMillis();

		return doCleanupEnd - doCleanupStart;
	}

	protected final long executeWalkToActions() {
		final long executeWalkToActionsStart	= System.currentTimeMillis();
		for (final Player player : getServer().getWorld().getPlayers()) {
			if (player.getWalkToAction() != null) {
				if (player.getWalkToAction().shouldExecute()) {
					player.getWalkToAction().execute();
				}
			}
		}
		final long executeWalkToActionsEnd	= System.currentTimeMillis();
		return executeWalkToActionsEnd - executeWalkToActionsStart;
	}

	protected final long processNpcs() {
		final long processNpcsStart	= System.currentTimeMillis();
		for (final Npc n : getServer().getWorld().getNpcs()) {
			try {
				if (n.isUnregistering()) {
					getServer().getWorld().unregisterNpc(n);
					continue;
				}

				// Only do the walking tick here if the NPC's walking tick matches the game tick
				if(!getServer().getConfig().WANT_CUSTOM_WALK_SPEED) {
					n.updatePosition();
				}
			} catch (final Exception e) {
				LOGGER.error("Error while updating " + n + " at position " + n.getLocation() + " loc: " + n.getLoc());
				LOGGER.catching(e);
			}
		}
		final long processNpcsEnd = System.currentTimeMillis();
		return processNpcsEnd - processNpcsStart;
	}

	/**
	 * Updates the messages queues for each player
	 */
	protected final long processMessageQueues() {
		final long processMessageQueuesStart = System.currentTimeMillis();
		for (final Player player : getServer().getWorld().getPlayers()) {
			final PrivateMessage pm = player.getNextPrivateMessage();
			if (pm != null) {
				Player affectedPlayer = getServer().getWorld().getPlayer(pm.getFriend());
				if (affectedPlayer != null) {
					boolean blockAll = affectedPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, affectedPlayer.isUsingAuthenticClient())
						== PlayerSettings.BlockingMode.All.id();
					boolean blockNone = affectedPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, affectedPlayer.isUsingAuthenticClient())
						== PlayerSettings.BlockingMode.None.id();
					if (((affectedPlayer.getSocial().isFriendsWith(player.getUsernameHash()) && !blockAll) || blockNone)
						&& !affectedPlayer.getSocial().isIgnoring(player.getUsernameHash()) || player.isMod()) {
						ActionSender.sendPrivateMessageSent(player, affectedPlayer.getUsernameHash(), pm.getMessage(), false);
						ActionSender.sendPrivateMessageReceived(affectedPlayer, player, pm.getMessage(), false);
					}

					player.getWorld().getServer().getGameLogger().addQuery(new PMLog(player.getWorld(), player.getUsername(), pm.getMessage(),
						DataConversions.hashToUsername(pm.getFriend())));
				}
			}
		}
		GlobalMessage gm ;
		while((gm = getServer().getWorld().getNextGlobalMessage()) != null) {
			for (final Player player : getServer().getWorld().getPlayers()) {
				if (player == gm.getPlayer()) {
					player.getWorld().getServer().getGameLogger().addQuery(new PMLog(player.getWorld(), player.getUsername(), gm.getMessage(),
						"Global$"));
					ActionSender.sendPrivateMessageSent(gm.getPlayer(), -1L, gm.getMessage(), true);
				} else {
					if (!player.getBlockGlobalFriend()) {
						boolean blockNone = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, player.isUsingAuthenticClient())
							== PlayerSettings.BlockingMode.None.id();
						if (blockNone && !player.getSocial().isIgnoring(gm.getPlayer().getUsernameHash()) || gm.getPlayer().isMod()) {
							ActionSender.sendPrivateMessageReceived(player, gm.getPlayer(), gm.getMessage(), true);
						}
					}
				}
			}
		}
		for (final Player player : getServer().getWorld().getPlayers()) {
			if (player.requiresOfferUpdate()) {
				ActionSender.sendTradeItems(player);
				player.setRequiresOfferUpdate(false);
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
		for (final Player player : getServer().getWorld().getPlayers()) {
			// Checking login because we don't want to unregister more than once
			if (player.isUnregistering() && player.isLoggedIn()) {
				getServer().getWorld().unregisterPlayer(player);
				continue;
			}

			// Only do the walking tick here if the Players' walking tick matches the game tick
			if(!getServer().getConfig().WANT_CUSTOM_WALK_SPEED) {
				player.updatePosition();
			}

			if (player.getUpdateFlags().hasAppearanceChanged()) {
				player.incAppearanceID();
			}
		}
		final long processPlayersEnd	= System.currentTimeMillis();
		return processPlayersEnd - processPlayersStart;
	}

	public long getLastWorldUpdateDuration() {
		return lastWorldUpdateDuration;
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
