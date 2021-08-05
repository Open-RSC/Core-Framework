package com.openrsc.server;

import com.openrsc.server.constants.AppearanceId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.database.impl.mysql.queries.logging.PMLog;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.external.ItemLoc;
import com.openrsc.server.model.*;
import com.openrsc.server.model.action.WalkToAction;
import com.openrsc.server.model.entity.Entity;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
import com.openrsc.server.model.entity.update.*;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.outgoing.*;
import com.openrsc.server.util.EntityList;
import com.openrsc.server.util.rsc.AppearanceRetroConverter;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.openrsc.server.net.rsc.ActionSender.tryFinalizeAndSendPacket;

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
		setLastExecuteWalkToActionsDuration(0);
	}

	// private static final int PACKET_UPDATETIMEOUTS = 0;
	public void sendUpdatePackets(final Player player) {
		// TODO: Should be private
		try {
			if (player.isUsing233CompatibleClient()) {
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
		MobsUpdateStruct struct = new MobsUpdateStruct();
		ClearMobsStruct clearStruct = new ClearMobsStruct();
		if (playerToUpdate.isRetroClient()) {
			// TODO: check impl
			List<Object> mobsUpdate = new ArrayList<>();
			List<Integer> clearIdx = new ArrayList<>();

			for (final Iterator<Npc> it$ = playerToUpdate.getLocalNpcs().iterator(); it$.hasNext(); ) {
				Npc localNpc = it$.next();

				if (!playerToUpdate.withinRange(localNpc) || localNpc.isRemoved() || localNpc.isRespawning() || localNpc.isTeleporting() || localNpc.inCombat() || !localNpc.withinAuthenticRange(playerToUpdate)) {
					if (localNpc.isRemoved() || localNpc.isTeleporting()) {
						// TODO: check if more conditions need to be added from outer if
						clearIdx.add(localNpc.getIndex());
					}
					it$.remove();
				} else {
					final byte[] offsets = DataConversions.getMobPositionOffsets(localNpc.getLocation(), playerToUpdate.getLocation());

					int X = offsets[0];
					int Y = offsets[1];
					int packed = (localNpc.getIndex() << 6) | ((X & 0x1F) << 1) | ((Y & 0x1F) >> 4);
					mobsUpdate.add((short) packed);
					int packed2 = ((Y & 0xF) << 4) | (localNpc.getSprite() & 0xF);
					mobsUpdate.add((byte) packed2);
					mobsUpdate.add((byte) localNpc.getID());
				}
			}
			clearStruct.indices = clearIdx;
			for (final Npc newNPC : playerToUpdate.getViewArea().getNpcsInView()) {
				if (playerToUpdate.getLocalNpcs().contains(newNPC) || newNPC.equals(playerToUpdate) || newNPC.isRemoved() || newNPC.isRespawning()
					|| newNPC.getID() == NpcId.NED_BOAT.id() && !playerToUpdate.getCache().hasKey("ned_hired")
					|| !playerToUpdate.withinRange(newNPC) || (newNPC.isTeleporting() && !newNPC.inCombat())) {
					continue;
				} else if (playerToUpdate.getLocalNpcs().size() >= 255) {
					break;
				}
				if (!newNPC.withinAuthenticRange(playerToUpdate))
					continue; // only have 5 bits in the rsc38 protocol, so the npc can only be shown up to 16 away

				final byte[] offsets = DataConversions.getMobPositionOffsets(newNPC.getLocation(), playerToUpdate.getLocation());

				int X = offsets[0];
				int Y = offsets[1];
				int packed = (newNPC.getIndex() << 6) | ((X & 0x1F) << 1) | ((Y & 0x1F) >> 4);
				mobsUpdate.add((short) packed);
				int packed2 = ((Y & 0xF) << 4) | (newNPC.getSprite() & 0xF);
				mobsUpdate.add((byte) packed2);
				mobsUpdate.add((byte) newNPC.getID());

				playerToUpdate.getLocalNpcs().add(newNPC);
			}

			struct.mobsUpdate = mobsUpdate;
		} else {
			List<AbstractMap.SimpleEntry<Integer, Integer>> mobsUpdate = new ArrayList<>();

			mobsUpdate.add(new AbstractMap.SimpleEntry<>(playerToUpdate.getLocalNpcs().size(), 8));
			for (final Iterator<Npc> it$ = playerToUpdate.getLocalNpcs().iterator(); it$.hasNext(); ) {
				Npc localNpc = it$.next();

				if (!playerToUpdate.withinRange(localNpc) || localNpc.isRemoved() || localNpc.isRespawning() || localNpc.isTeleporting() || localNpc.inCombat() || !localNpc.withinAuthenticRange(playerToUpdate)) {
					it$.remove();
					mobsUpdate.add(new AbstractMap.SimpleEntry<>(1, 1));
					mobsUpdate.add(new AbstractMap.SimpleEntry<>(1, 1));
					mobsUpdate.add(new AbstractMap.SimpleEntry<>(3, 2));
				} else {
					if (localNpc.hasMoved()) {
						mobsUpdate.add(new AbstractMap.SimpleEntry<>(1, 1));
						mobsUpdate.add(new AbstractMap.SimpleEntry<>(0, 1));
						mobsUpdate.add(new AbstractMap.SimpleEntry<>(localNpc.getSprite(), 3));
					} else if (localNpc.spriteChanged()) {
						mobsUpdate.add(new AbstractMap.SimpleEntry<>(1, 1));
						mobsUpdate.add(new AbstractMap.SimpleEntry<>(1, 1));
						mobsUpdate.add(new AbstractMap.SimpleEntry<>(localNpc.getSprite(), 4));
					} else {
						mobsUpdate.add(new AbstractMap.SimpleEntry<>(0, 1));
					}
				}
			}
			for (final Npc newNPC : playerToUpdate.getViewArea().getNpcsInView()) {
				if (playerToUpdate.getLocalNpcs().contains(newNPC) || newNPC.equals(playerToUpdate) || newNPC.isRemoved() || newNPC.isRespawning()
					|| newNPC.getID() == NpcId.NED_BOAT.id() && !playerToUpdate.getCache().hasKey("ned_hired")
					|| !playerToUpdate.withinRange(newNPC) || (newNPC.isTeleporting() && !newNPC.inCombat())) {
					continue;
				} else if (playerToUpdate.getLocalNpcs().size() >= 255) {
					break;
				}
				if (!newNPC.withinAuthenticRange(playerToUpdate))
					continue; // only have 5 bits in the rsc235 protocol, so the npc can only be shown up to 16 away

				final byte[] offsets = DataConversions.getMobPositionOffsets(newNPC.getLocation(), playerToUpdate.getLocation());
				mobsUpdate.add(new AbstractMap.SimpleEntry<>(newNPC.getIndex(), 12));
				boolean forAuthentic = !playerToUpdate.isUsingCustomClient();
				mobsUpdate.add(new AbstractMap.SimpleEntry<>((int) offsets[0], forAuthentic ? 5 : 6));
				mobsUpdate.add(new AbstractMap.SimpleEntry<>((int) offsets[1], forAuthentic ? 5 : 6));
				mobsUpdate.add(new AbstractMap.SimpleEntry<>(newNPC.getSprite(), 4));
				mobsUpdate.add(new AbstractMap.SimpleEntry<>(newNPC.getID(), 10));

				playerToUpdate.getLocalNpcs().add(newNPC);
			}

			struct.mobs = mobsUpdate;
		}
		if (clearStruct.indices != null && clearStruct.indices.size() > 0) {
			tryFinalizeAndSendPacket(OpcodeOut.SEND_REMOVE_WORLD_NPC, clearStruct, playerToUpdate);
		}
		tryFinalizeAndSendPacket(OpcodeOut.SEND_NPC_COORDS, struct, playerToUpdate);
	}

	protected void updatePlayers(final Player playerToUpdate) {
		MobsUpdateStruct struct = new MobsUpdateStruct();
		ClearMobsStruct clearStruct = new ClearMobsStruct();
		if (playerToUpdate.isRetroClient()) {
			// TODO: check impl
			List<Object> mobsUpdate = new ArrayList<>();
			List<Integer> clearIdx = new ArrayList<>();

			mobsUpdate.add((short) playerToUpdate.getIndex());
			mobsUpdate.add((short) playerToUpdate.getX());
			mobsUpdate.add((short) playerToUpdate.getY());
			mobsUpdate.add((byte) playerToUpdate.getSprite());

			if (playerToUpdate.loggedIn()) {
				for (final Iterator<Player> it$ = playerToUpdate.getLocalPlayers().iterator(); it$.hasNext(); ) {
					final Player otherPlayer = it$.next();

					if (!playerToUpdate.withinRange(otherPlayer) || !otherPlayer.loggedIn() || otherPlayer.isRemoved()
						|| otherPlayer.isTeleporting() || otherPlayer.isInvisibleTo(playerToUpdate)
						|| otherPlayer.inCombat() || otherPlayer.hasMoved()
						|| !otherPlayer.withinAuthenticRange(playerToUpdate)) {
						if (!otherPlayer.loggedIn() || otherPlayer.isRemoved()
							|| otherPlayer.isTeleporting() || otherPlayer.isInvisibleTo(playerToUpdate)) {
							// TODO: check if more conditions need to be added from outer if
							clearIdx.add(otherPlayer.getIndex());
						}
						it$.remove();
						playerToUpdate.getKnownPlayerAppearanceIDs().remove(otherPlayer.getUsernameHash());
					} else {
						final byte[] offsets = DataConversions.getMobPositionOffsets(otherPlayer.getLocation(),
							playerToUpdate.getLocation());

						int X = offsets[0];
						int Y = offsets[1];
						if (otherPlayer.equals(playerToUpdate)) {
							int packed = ((X & 0x1F) << 5) | (Y & 0x1F);
							mobsUpdate.add((short) packed);
							int packed2 = (otherPlayer.getIndex() << 4) | (otherPlayer.getSprite() & 0xF);
							mobsUpdate.add((short) packed2);
						} else {
							int packed = (otherPlayer.getIndex() << 6) | ((X & 0x1F) << 1) | ((Y & 0x1F) >> 4);
							mobsUpdate.add((short) packed);
							int packed2 = ((Y & 0xF) << 4) | (otherPlayer.getSprite() & 0xF);
							mobsUpdate.add((byte) packed2);
						}
					}
				}
				clearStruct.indices = clearIdx;

				for (final Player otherPlayer : playerToUpdate.getViewArea().getPlayersInView()) {
					if (playerToUpdate.getLocalPlayers().contains(otherPlayer) || otherPlayer.equals(playerToUpdate)
						|| !otherPlayer.withinRange(playerToUpdate) || !otherPlayer.loggedIn()
						|| otherPlayer.isRemoved() || otherPlayer.isInvisibleTo(playerToUpdate)
						|| (otherPlayer.isTeleporting() && !otherPlayer.inCombat())) {
						continue;
					}
					if (!otherPlayer.withinAuthenticRange(playerToUpdate))
						continue; // only have 5 bits in the rsc38 protocol, so the player can only be shown up to 16 tiles away

					final byte[] offsets = DataConversions.getMobPositionOffsets(otherPlayer.getLocation(),
						playerToUpdate.getLocation());

					int X = offsets[0];
					int Y = offsets[1];
					if (otherPlayer.equals(playerToUpdate)) {
						int packed = ((X & 0x1F) << 5) | (Y & 0x1F);
						mobsUpdate.add((short) packed);
						int packed2 = (otherPlayer.getIndex() << 4) | (otherPlayer.getSprite() & 0xF);
						mobsUpdate.add((short) packed2);
					} else {
						int packed = (otherPlayer.getIndex() << 6) | ((X & 0x1F) << 1) | ((Y & 0x1F) >> 4);
						mobsUpdate.add((short) packed);
						int packed2 = ((Y & 0xF) << 4) | (otherPlayer.getSprite() & 0xF);
						mobsUpdate.add((byte) packed2);
					}

					playerToUpdate.getLocalPlayers().add(otherPlayer);
					if (playerToUpdate.getLocalPlayers().size() >= 255) {
						break;
					}
				}
			}

			struct.mobsUpdate = mobsUpdate;
		} else {
			List<AbstractMap.SimpleEntry<Integer, Integer>> mobsUpdate = new ArrayList<>();

			mobsUpdate.add(new AbstractMap.SimpleEntry<>(playerToUpdate.getX(), 11));
			mobsUpdate.add(new AbstractMap.SimpleEntry<>(playerToUpdate.getY(), 13));
			mobsUpdate.add(new AbstractMap.SimpleEntry<>(playerToUpdate.getSprite(), 4));
			mobsUpdate.add(new AbstractMap.SimpleEntry<>(playerToUpdate.getLocalPlayers().size(), 8));
			if (playerToUpdate.loggedIn()) {
				for (final Iterator<Player> it$ = playerToUpdate.getLocalPlayers().iterator(); it$.hasNext(); ) {
					final Player otherPlayer = it$.next();

					if (!playerToUpdate.withinRange(otherPlayer) || !otherPlayer.loggedIn() || otherPlayer.isRemoved()
						|| otherPlayer.isTeleporting() || otherPlayer.isInvisibleTo(playerToUpdate)
						|| otherPlayer.inCombat() || otherPlayer.hasMoved()
						|| !otherPlayer.withinAuthenticRange(playerToUpdate)) {
						mobsUpdate.add(new AbstractMap.SimpleEntry<>(1, 1)); //Needs Update
						mobsUpdate.add(new AbstractMap.SimpleEntry<>(1, 1)); //Update Type
						mobsUpdate.add(new AbstractMap.SimpleEntry<>(3, 2)); //Animation type (Remove)
						it$.remove();
						playerToUpdate.getKnownPlayerAppearanceIDs().remove(otherPlayer.getUsernameHash());
					} else {
						if (!otherPlayer.hasMoved() && !otherPlayer.spriteChanged()) {
							mobsUpdate.add(new AbstractMap.SimpleEntry<>(0, 1)); //Needs Update
						} else {
							// The player is actually going to be updated
							if (otherPlayer.hasMoved()) {
								mobsUpdate.add(new AbstractMap.SimpleEntry<>(1, 1)); //Needs Update
								mobsUpdate.add(new AbstractMap.SimpleEntry<>(0, 1)); //Update Type
								mobsUpdate.add(new AbstractMap.SimpleEntry<>(otherPlayer.getSprite(), 3));
							} else if (otherPlayer.spriteChanged()) {
								mobsUpdate.add(new AbstractMap.SimpleEntry<>(1, 1)); //Needs Update
								mobsUpdate.add(new AbstractMap.SimpleEntry<>(1, 1)); //Update Type
								mobsUpdate.add(new AbstractMap.SimpleEntry<>(otherPlayer.getSprite(), 4));
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
					if (!otherPlayer.withinAuthenticRange(playerToUpdate))
						continue; // only have 5 bits in the rsc235 protocol, so the player can only be shown up to 16 tiles away

					final byte[] offsets = DataConversions.getMobPositionOffsets(otherPlayer.getLocation(),
						playerToUpdate.getLocation());
					mobsUpdate.add(new AbstractMap.SimpleEntry<>(otherPlayer.getIndex(), 11));
					boolean forAuthentic = !playerToUpdate.isUsingCustomClient();
					mobsUpdate.add(new AbstractMap.SimpleEntry<>((int) offsets[0], forAuthentic ? 5 : 6));
					mobsUpdate.add(new AbstractMap.SimpleEntry<>((int) offsets[1], forAuthentic ? 5 : 6));
					mobsUpdate.add(new AbstractMap.SimpleEntry<>(otherPlayer.getSprite(), 4));
					if (playerToUpdate.isUsing177CompatibleClient()) {
						mobsUpdate.add(new AbstractMap.SimpleEntry<>(playerToUpdate.isKnownPlayer(otherPlayer.getIndex()) ? 1 : 0, 1));
					}

					playerToUpdate.getLocalPlayers().add(otherPlayer);
					if (playerToUpdate.getLocalPlayers().size() >= 255) {
						break;
					}
				}
			}

			struct.mobs = mobsUpdate;
		}
		if (clearStruct.indices != null && clearStruct.indices.size() > 0) {
			tryFinalizeAndSendPacket(OpcodeOut.SEND_REMOVE_WORLD_PLAYER, clearStruct, playerToUpdate);
		}
		tryFinalizeAndSendPacket(OpcodeOut.SEND_PLAYER_COORDS, struct, playerToUpdate);
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
				if (projectileFired.getCaster().getIndex() != -1 && projectileFired.getVictim().getIndex() != -1) {
					npcProjectilesNeedingDisplayed.add(projectileFired);
				}
			}
			if (updateFlags.hasBubbleNpc()) {
					BubbleNpc bubble = updateFlags.getActionBubbleNpc().get();
					npcBubblesNeedingDisplayed.add(bubble);
			}
		}
		int updateSize = npcMessagesNeedingDisplayed.size() + npcsNeedingHitsUpdate.size();
		if (player.isUsingCustomClient()) {
			updateSize += npcProjectilesNeedingDisplayed.size() + npcSkullsNeedingDisplayed.size() + npcWieldsNeedingDisplayed.size() + npcBubblesNeedingDisplayed.size();
		}
		if (updateSize > 0) {
			AppearanceUpdateStruct struct = new AppearanceUpdateStruct();
			List<Object> updates = new ArrayList<>();

			updates.add((short) updateSize);

			ChatMessage chatMessage;
			while ((chatMessage = npcMessagesNeedingDisplayed.poll()) != null) {
				updates.add((short) chatMessage.getSender().getIndex());
				updates.add((byte) 1);
				updates.add((short) (chatMessage.getRecipient() == null ? -1 : chatMessage.getRecipient().getIndex()));
				if (player.isRetroClient()) {
					updates.add((byte) chatMessage.getMessageString().length());
					updates.add(chatMessage.getMessageString());
				} else if (player.isUsing177CompatibleClient()) {
					updates.add(new RSCString(chatMessage.getMessageString()));
				} else if (player.isUsing233CompatibleClient()) {
					updates.add(new RSCString(chatMessage.getMessageString()));
				} else {
					updates.add(chatMessage.getMessageString());
				}
			}
			Damage npcNeedingHitsUpdate;
			while ((npcNeedingHitsUpdate = npcsNeedingHitsUpdate.poll()) != null) {
				updates.add((short) npcNeedingHitsUpdate.getIndex());
				updates.add((byte) 2);
				updates.add((byte) npcNeedingHitsUpdate.getDamage());
				updates.add((byte) npcNeedingHitsUpdate.getCurHits());
				updates.add(((byte) npcNeedingHitsUpdate.getMaxHits()));
			}
			if (player.isUsingCustomClient()) {
				Projectile projectile;
				while ((projectile = npcProjectilesNeedingDisplayed.poll()) != null) {
					Entity victim = projectile.getVictim();
					if (victim.isNpc()) {
						updates.add((short) projectile.getCaster().getIndex());
						updates.add((byte) 3);
						updates.add((short) projectile.getType());
						updates.add((short) victim.getIndex());
					} else if (victim.isPlayer()) {
						updates.add((short) projectile.getCaster().getIndex());
						updates.add((byte) 4);
						updates.add((short) projectile.getType());
						updates.add((short) victim.getIndex());
					}
				}
				Skull npcNeedingSkullUpdate;
				while ((npcNeedingSkullUpdate = npcSkullsNeedingDisplayed.poll()) != null) {
					updates.add((short) npcNeedingSkullUpdate.getIndex());
					updates.add((byte) 5);
					updates.add((byte) npcNeedingSkullUpdate.getSkull());
				}
				Wield npcNeedingWieldUpdate;
				while ((npcNeedingWieldUpdate = npcWieldsNeedingDisplayed.poll()) != null) {
					updates.add((short) npcNeedingWieldUpdate.getIndex());
					updates.add((byte) 6);
					updates.add((byte) npcNeedingWieldUpdate.getWield());
					updates.add((byte) npcNeedingWieldUpdate.getWield2());
				}
				BubbleNpc npcNeedingBubbleUpdate;
				while ((npcNeedingBubbleUpdate = npcBubblesNeedingDisplayed.poll()) != null) {
					updates.add((short) npcNeedingBubbleUpdate.getOwner().getIndex());
					updates.add((byte) 7);
					updates.add((short) npcNeedingBubbleUpdate.getID());
				}
			}

			struct.info = updates;
			tryFinalizeAndSendPacket(OpcodeOut.SEND_UPDATE_NPC, struct, player);
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
			if (projectileFired.getCaster().getIndex() != -1 && projectileFired.getVictim().getIndex() != -1) {
				projectilesNeedingDisplayed.add(projectileFired);
			}
		}

		if (player.getUpdateFlags().hasChatMessage()) {
			ChatMessage chatMessage = player.getUpdateFlags().getChatMessage();
			if (!chatMessage.getMuted() || player.hasElevatedPriveledges()) {
				// 177 client locally echos player's own chat messages instead of having the server confirm what the player sent
				if (
						!(
							// is a client that echos their own local chat messages
							player.isUsing177CompatibleClient() &&
							// is public chat & not quest/private message
							(chatMessage.getRecipient() == null || chatMessage.getRecipient().isPlayer()) &&
							// chat sender is chat receiver
							((Player)chatMessage.getSender()).getUsernameHash() == player.getUsernameHash()
						)
					)
				{
					chatMessagesNeedingDisplayed.add(chatMessage);
				}
			}
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

			boolean blockAll = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES, player.isUsingCustomClient())
				== PlayerSettings.BlockingMode.All.id();
			boolean blockNone = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES, player.isUsingCustomClient())
				== PlayerSettings.BlockingMode.None.id();

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
				&& !player.getSocial().isIgnoring(otherPlayer.getUsernameHash())
				|| player.isMod()|| otherPlayer.isMod() || updateFlags.getChatMessage().getRecipient() != null)) {
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
				+ playersNeedingAppearanceUpdate.size();

			// TODO: needs to be later revised for mc38
			if (updateSize > 0) {
				AppearanceUpdateStruct mainStruct = new AppearanceUpdateStruct();
				AppearanceUpdateStruct altStruct = new AppearanceUpdateStruct(); // for early mudclient, appearance update was sent appart;
				boolean isRetroClient = player.isRetroClient();
				boolean isCustomClient = player.isUsingCustomClient();
				boolean is177Compat = player.isUsing177CompatibleClient();

				List<Object> updatesMain = new ArrayList<>();
				List<Object> updatesAlt = new ArrayList<>();
				if (isRetroClient) {
					if (updateSize - playersNeedingAppearanceUpdate.size() > 0) {
						updatesMain.add((short) (updateSize - playersNeedingAppearanceUpdate.size()));
					}
					if (playersNeedingAppearanceUpdate.size() > 0) {
						updatesAlt.add((short) playersNeedingAppearanceUpdate.size());
					}
				} else if (!player.isUsingCustomClient()) {
					updatesMain.add((short) updateSize);
				} else {
					updatesMain.add((short) (updateSize + playersNeedingHpUpdate.size()));
				}

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
					updatesMain.add((short) b.getOwner().getIndex());
					updatesMain.add((byte) 0);
					updatesMain.add((short) b.getID());
				}

				// Update Type 1: Chat Message
				// AND
				// Update Type 6: Quest Chat Message, 1 on retro client prefixed by "@que@"
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

					if (isCustomClient) {
						// Non Authentic OpenRSC client
						updatesMain.add((short) cm.getSender().getIndex());
						updatesMain.add((byte) updateType);

						if (updateType == 1 || updateType == 7) {
							if (cm.getSender() != null && cm.getSender() instanceof Player)
								updatesMain.add((int) sender.getIcon());
						}

						if (updateType == 7) {
							updatesMain.add((byte) (sender.isMuted() ? 1 : 0));
							updatesMain.add((byte) (sender.getLocation().onTutorialIsland() ? 1 : 0));
						}

						if (updateType != 7 || player.isAdmin()) {
							updatesMain.add(cm.getMessageString());
						} else {
							updatesMain.add("");
						}
					} else {
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
							updatesMain.add((short) cm.getSender().getIndex());
							updatesMain.add((byte) (!isRetroClient ? updateType : 1));
							if (updateType != 6 && (isCustomClient || player.isUsing233CompatibleClient())) {
								updatesMain.add((byte) sender.getIconAuthentic());
							}
							if (isRetroClient) {
								String messageUse = message;
								if (updateType == 6) messageUse = "@que@" + message;
								updatesMain.add((byte) messageUse.length());
								updatesMain.add(messageUse);
							} else {
								updatesMain.add(new RSCString(message));
							}
						} else {
							LOGGER.error("extraneous chat update packet will crash the authentic client...!");
						}
					}
				}

				// Update Type 2: Damage Update
				Damage playerNeedingHitsUpdate;
				while ((playerNeedingHitsUpdate = playersNeedingDamageUpdate.poll()) != null) {
					updatesMain.add((short) playerNeedingHitsUpdate.getIndex());
					updatesMain.add((byte) 2);
					updatesMain.add((byte) playerNeedingHitsUpdate.getDamage());
					updatesMain.add((byte) playerNeedingHitsUpdate.getCurHits());
					updatesMain.add((byte) playerNeedingHitsUpdate.getMaxHits());
				}

				// Update Types 3 & 4: Projectile Update (draws the projectile)
				Projectile projectile;
				while ((projectile = projectilesNeedingDisplayed.poll()) != null) {
					Entity victim = projectile.getVictim();
					if (victim.isNpc()) {
						updatesMain.add((short) projectile.getCaster().getIndex());
						updatesMain.add((byte) 3);
						updatesMain.add((short) projectile.getType());
						updatesMain.add((short) victim.getIndex());
					} else if (victim.isPlayer()) {
						updatesMain.add((short) projectile.getCaster().getIndex());
						updatesMain.add((byte) 4);
						updatesMain.add((short) projectile.getType());
						updatesMain.add((short) victim.getIndex());
					}
				}

				// Update Type 5: Player appearance and identity
				Player playerNeedingAppearanceUpdate;
				while ((playerNeedingAppearanceUpdate = playersNeedingAppearanceUpdate.poll()) != null) {
					PlayerAppearance appearance = playerNeedingAppearanceUpdate.getSettings().getAppearance();
					final int clientVersion = playerNeedingAppearanceUpdate.getClientVersion();

					if (isRetroClient) {
						updatesAlt.add((short) playerNeedingAppearanceUpdate.getIndex()); // server index
						updatesAlt.add((short) playerNeedingAppearanceUpdate.getIndex()); // server id
						updatesAlt.add((long) DataConversions.usernameToHash(playerNeedingAppearanceUpdate.getUsername()));
					} else {
						updatesMain.add((short) playerNeedingAppearanceUpdate.getIndex());
						updatesMain.add((byte) 5);
						if (player.isUsing233CompatibleClient()) {
							updatesMain.add((short) player.getAppearanceID());
							updatesMain.add(playerNeedingAppearanceUpdate.getUsername());
							updatesMain.add(playerNeedingAppearanceUpdate.getUsername()); // Pretty sure this is unnecessary & always redundant authentically.
						} else if (is177Compat) {
							updatesMain.add((short) player.getAppearanceID());
							updatesMain.add((long) DataConversions.usernameToHash(playerNeedingAppearanceUpdate.getUsername()));
						} else if (player.isUsingCustomClient()) {
							updatesMain.add(playerNeedingAppearanceUpdate.getUsername());
						}
					}

					if (playerNeedingAppearanceUpdate.getPossessing() != null) {
						// while possessing another creature
						// do not wish to see any sprites of our own character under any circumstance
						if (isRetroClient) {
							updatesAlt.add((byte) 0); // Equipment count
						} else {
							updatesMain.add((byte) 0); // Equipment count
						}
					} else if (!isCustomClient &&
                        (playerNeedingAppearanceUpdate.stateIsInvisible() ||
                            playerNeedingAppearanceUpdate.stateIsInvulnerable())) {
						// Handle Invisibility & Invulnerability in the authentic client

                        int[] wornItems = playerNeedingAppearanceUpdate.getWornItems();

                        int bootColour = AppearanceId.SHADOW_WARRIOR_BOOTS.id(); // default
                        if (wornItems[AppearanceId.SLOT_BOOTS] != 0) {
                            // if player is already wearing boots, we can let them choose their colour. :-)
                            bootColour = wornItems[AppearanceId.SLOT_BOOTS];
                        }

                        int shieldSprite = 0; // default to invisible
                        if (playerNeedingAppearanceUpdate.stateIsInvulnerable()) {
                            if (wornItems[AppearanceId.SLOT_SHIELD] == AppearanceId.DRAGON_SQUARE_SHIELD.id()) {
                                shieldSprite = AppearanceId.RUNE_SQUARE_SHIELD.id();
                            } else {
                                shieldSprite = AppearanceId.DRAGON_SQUARE_SHIELD.id();
                            }
                        }

                        int gloveColour = wornItems[AppearanceId.SLOT_GLOVES]; // let player keep their gloves, even if they have none
						if (wornItems[AppearanceId.SLOT_GLOVES] == 0 && wornItems[AppearanceId.SLOT_WEAPON] != 0) {
							// give player gloves if they are wielding a weapon
							gloveColour = AppearanceId.LEATHER_GLOVES.id();
						}

                        // if player is just invulnerable & not invisible, give them a dark-robed appearance
                        int headSprite = 0; // default to invisible
                        int hatSprite = 0;
                        int bodySprite = 0;
                        int legSprite = 0;
                        int pantsSprite = 0;
                        int shirtSprite = 0;
                        int amuletSprite = wornItems[AppearanceId.SLOT_AMULET];
                        if (!playerNeedingAppearanceUpdate.stateIsInvisible()) {
                            headSprite = wornItems[AppearanceId.SLOT_HEAD];
                            if (wornItems[AppearanceId.SLOT_HAT] == 0) {
                                hatSprite = AppearanceId.LARGE_BLACK_HELMET.id();
                                headSprite = AppearanceId.NOTHING.id();
                            } else {
                                hatSprite = wornItems[AppearanceId.SLOT_HAT];
                            }

                            // dark robes
                            bodySprite = AppearanceId.SHADOW_WARRIOR_ROBE.id();
                            legSprite = AppearanceId.SHADOW_WARRIOR_SKIRT.id();
                            pantsSprite = AppearanceId.COLOURED_PANTS.id();
                            shirtSprite = AppearanceId.FEMALE_BODY.id();
                            gloveColour = AppearanceId.ICE_GLOVES.id();
                            amuletSprite = AppearanceId.PENDANT_OF_LUCIEN.id();
                        }

                        // as char to indicate to the generator to use appearancebyte
						if (isRetroClient) {
							updatesAlt.add((byte) 11); // Equipment count
							updatesAlt.add((char) headSprite);
							updatesAlt.add((char) shirtSprite);
							updatesAlt.add((char) pantsSprite);
							updatesAlt.add((char) shieldSprite);
							updatesAlt.add((char) wornItems[AppearanceId.SLOT_WEAPON]);
							updatesAlt.add((char) hatSprite);
							updatesAlt.add((char) bodySprite);
							updatesAlt.add((char) legSprite);
							updatesAlt.add((char) gloveColour);
							updatesAlt.add((char) bootColour);
							updatesAlt.add((char) amuletSprite);
						} else {
							updatesMain.add((byte) 11); // Equipment count
							updatesMain.add((char) headSprite);
							updatesMain.add((char) shirtSprite);
							updatesMain.add((char) pantsSprite);
							updatesMain.add((char) shieldSprite);
							updatesMain.add((char) wornItems[AppearanceId.SLOT_WEAPON]);
							updatesMain.add((char) hatSprite);
							updatesMain.add((char) bodySprite);
							updatesMain.add((char) legSprite);
							updatesMain.add((char) gloveColour);
							updatesMain.add((char) bootColour);
							updatesMain.add((char) amuletSprite);
						}
                        // No Cape
                    } else {
						// normal appearance update (not invisible)
						if (isRetroClient) {
							updatesAlt.add((byte) playerNeedingAppearanceUpdate.getWornItems().length);
						} else {
							updatesMain.add((byte) playerNeedingAppearanceUpdate.getWornItems().length);
						}
                        for (int i : playerNeedingAppearanceUpdate.getWornItems()) {
                            if (isRetroClient) {
								updatesAlt.add((char) (AppearanceRetroConverter.convert(i) & 0xFF));
							} else if (player.isUsing233CompatibleClient() || is177Compat) {
								updatesMain.add((char) (i & 0xFF));
							} else {
								updatesMain.add((short) i);
							}
                        }
                    }

					if (isRetroClient) {
						updatesAlt.add((char) appearance.getHairColour());
						updatesAlt.add((char) appearance.getTopColour());
						updatesAlt.add((char) appearance.getTrouserColour());
						updatesAlt.add((char) appearance.getSkinColour());
						updatesAlt.add((byte) playerNeedingAppearanceUpdate.getPkMode()); //is player attackable?
						updatesAlt.add((byte) playerNeedingAppearanceUpdate.getCombatLevel());
						updatesAlt.add((byte) playerNeedingAppearanceUpdate.getSkullType());
					} else {
						updatesMain.add((char) appearance.getHairColour());
						updatesMain.add((char) appearance.getTopColour());
						updatesMain.add((char) appearance.getTrouserColour());
						updatesMain.add((char) appearance.getSkinColour());
						updatesMain.add((byte) playerNeedingAppearanceUpdate.getCombatLevel());
						updatesMain.add((byte) playerNeedingAppearanceUpdate.getSkullType());
					}

					if (isCustomClient) {
						if (playerNeedingAppearanceUpdate.getClan() != null) {
							updatesMain.add((byte) 1);
							updatesMain.add(playerNeedingAppearanceUpdate.getClan().getClanTag());
						} else {
							updatesMain.add((byte) 0);
						}

						updatesMain.add((byte) (playerNeedingAppearanceUpdate.stateIsInvisible() ? 1 : 0));
						updatesMain.add((byte) (playerNeedingAppearanceUpdate.stateIsInvulnerable() ? 1 : 0));
						updatesMain.add((byte) playerNeedingAppearanceUpdate.getGroupID());
						updatesMain.add((int) playerNeedingAppearanceUpdate.getIcon());
					}
				}

				if (isCustomClient) {
					// Non authentic type 9. In authentic network protocol, this information is just in type 2.
					HpUpdate playerNeedingHpUpdate;
					while ((playerNeedingHpUpdate = playersNeedingHpUpdate.poll()) != null) {
						updatesMain.add((short) playerNeedingHpUpdate.getIndex());
						updatesMain.add((byte) 9);
						updatesMain.add((byte) playerNeedingHpUpdate.getCurHits());
						updatesMain.add((byte) playerNeedingHpUpdate.getMaxHits());
					}
				}

				mainStruct.info = updatesMain;
				altStruct.info = updatesAlt;
				if (updatesMain.size() > 0 ) {
					tryFinalizeAndSendPacket(OpcodeOut.SEND_UPDATE_PLAYERS, mainStruct, player);
				}
				if (updatesAlt.size() > 0) {
					tryFinalizeAndSendPacket(OpcodeOut.SEND_UPDATE_PLAYERS_RETRO, altStruct, player);
				}
			}
		}
	}

	protected void updateGameObjects(final Player playerToUpdate) {
		boolean changed = false;

		GameObjectsUpdateStruct struct = new GameObjectsUpdateStruct();
		List<GameObjectLoc> objectLocs = new ArrayList<>();

		// TODO: Unloading scenery is not handled correctly.
		//       According to RSC+ replays, the server never tells the client to unload objects until
		//       a region is unloaded. It then instructs the client to only unload the region.
		//       Right now the server is very aggressive in unloading scenery, which is detrimental for clients with a larger view

		for (final Iterator<GameObject> it$ = playerToUpdate.getLocalGameObjects().iterator(); it$.hasNext(); ) {
			final GameObject o = it$.next();
			if (!playerToUpdate.withinGridRange(o) || o.isRemoved() || o.isInvisibleTo(playerToUpdate)) {
				final int offsetX = o.getX() - playerToUpdate.getX();
				final int offsetY = o.getY() - playerToUpdate.getY();
				//If the object is close enough we can use regular way to remove:
				if (offsetX > -128 && offsetY > -128 && offsetX < 128 && offsetY < 128) {
					objectLocs.add(new GameObjectLoc(60000, offsetX, offsetY, o.getDirection(), 0));
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

		// Add scenery
		for (final GameObject newObject : playerToUpdate.getViewArea().getGameObjectsInView()) {
			boolean skipAdd = newObject.isRemoved() ||
				newObject.isInvisibleTo(playerToUpdate) ||
				newObject.getType() != 0 || // not a wallObject
				playerToUpdate.getLocalGameObjects().contains(newObject);
			if (!playerToUpdate.isUsingCustomClient()) {
				// Honestly don't think this does anything because the scenery isn't iterated over in the view anyway
				// TODO: funny behaviour where if a rock is mined > 16 tiles from you, it can be removed but not replaced until you get closer.
				skipAdd |= !playerToUpdate.within4GridRange(newObject);
			} else {
				skipAdd |= !playerToUpdate.withinGridRange(newObject);
			}
			if (skipAdd) {
				continue;
			}

			final int offsetX = newObject.getX() - playerToUpdate.getX();
			final int offsetY = newObject.getY() - playerToUpdate.getY();
			objectLocs.add(new GameObjectLoc(newObject.getID(), offsetX, offsetY, newObject.getDirection(), 0));
			playerToUpdate.getLocalGameObjects().add(newObject);
			changed = true;
		}
		struct.objects = objectLocs;
		if (changed) {
			tryFinalizeAndSendPacket(OpcodeOut.SEND_SCENERY_HANDLER, struct, playerToUpdate);
		}
	}

	protected void updateGroundItems(final Player playerToUpdate) {
		boolean changed = false;

		GroundItemsUpdateStruct struct = new GroundItemsUpdateStruct();
		List<ItemLoc> itemLocs = new ArrayList<>();

		for (final Iterator<GroundItem> it$ = playerToUpdate.getLocalGroundItems().iterator(); it$.hasNext(); ) {
			final GroundItem groundItem = it$.next();
			final int offsetX = (groundItem.getX() - playerToUpdate.getX());
			final int offsetY = (groundItem.getY() - playerToUpdate.getY());

			if (!playerToUpdate.withinGridRange(groundItem)) {
				if (offsetX > -128 && offsetY > -128 && offsetX < 128 && offsetY < 128) {
					// respawnTime = -1 to indicate to clear not on range
					itemLocs.add(new ItemLoc(groundItem.getID(), offsetX, offsetY, groundItem.getAmount(), -1,
						groundItem.getNoted() && getServer().getConfig().WANT_BANK_NOTES ? 1 : 0));
				} else {
					playerToUpdate.getLocationsToClear().add(groundItem.getLocation());
				}
				it$.remove();
				changed = true;
			} else if (groundItem.isRemoved() || groundItem.isInvisibleTo(playerToUpdate)) {
				itemLocs.add(new ItemLoc(groundItem.getID() + 32768, offsetX, offsetY, groundItem.getAmount(), 0,
					groundItem.getNoted() && getServer().getConfig().WANT_BANK_NOTES ? 1 : 0));
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
			final int offsetX = groundItem.getX() - playerToUpdate.getX();
			final int offsetY = groundItem.getY() - playerToUpdate.getY();
			itemLocs.add(new ItemLoc(groundItem.getID(), offsetX, offsetY, groundItem.getAmount(), 0,
				groundItem.getNoted() && getServer().getConfig().WANT_BANK_NOTES ? 1 : 0));
			playerToUpdate.getLocalGroundItems().add(groundItem);
			changed = true;
		}
		struct.objects = itemLocs;
		if (changed) {
			tryFinalizeAndSendPacket(OpcodeOut.SEND_GROUND_ITEM_HANDLER, struct, playerToUpdate);
		}
	}

	protected void updateWallObjects(final Player playerToUpdate) {
		boolean changed = false;

		GameObjectsUpdateStruct struct = new GameObjectsUpdateStruct();
		List<GameObjectLoc> objectLocs = new ArrayList<>();

		// remove all boundaries that need to be removed
		for (final Iterator<GameObject> it$ = playerToUpdate.getLocalWallObjects().iterator(); it$.hasNext(); ) {
			final GameObject o = it$.next();
			if (!playerToUpdate.withinGridRange(o) || (o.isRemoved() || o.isInvisibleTo(playerToUpdate))) {
				final int offsetX = o.getX() - playerToUpdate.getX();
				final int offsetY = o.getY() - playerToUpdate.getY();
				if (offsetX > -128 && offsetY > -128 && offsetX < 128 && offsetY < 128) {
					if (playerToUpdate.isUsing233CompatibleClient()) {
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
						objectLocs.add(new GameObjectLoc(60000, offsetX, offsetY, o.getDirection(), 1));
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
			objectLocs.add(new GameObjectLoc(newObject.getID(), offsetX, offsetY, newObject.getDirection(), 1));
			changed = true;
		}
		struct.objects = objectLocs;
		if (changed) {
			tryFinalizeAndSendPacket(OpcodeOut.SEND_BOUNDARY_HANDLER, struct, playerToUpdate);
		}
	}

	protected void sendAppearanceKeepalive(final Player player) {
		NoPayloadStruct struct = new NoPayloadStruct();
		tryFinalizeAndSendPacket(OpcodeOut.SEND_APPEARANCE_KEEPALIVE, struct, player);
	}

	protected void sendClearLocations(final Player player) {
		if (player.getLocationsToClear().size() > 0) {
			ClearLocationsStruct struct = new ClearLocationsStruct();
			List<Point> pointList = new ArrayList<>();
			for (final Point point : player.getLocationsToClear()) {
				final int offsetX = point.getX() - player.getX();
				final int offsetY = point.getY() - player.getY();
				pointList.add(new Point(offsetX, offsetY));
			}
			player.getLocationsToClear().clear();
			struct.points = pointList;
			tryFinalizeAndSendPacket(OpcodeOut.SEND_REMOVE_WORLD_ENTITY, struct, player);
		}
	}

	public long doUpdates() {
		return getServer().bench(() -> {
			lastWorldUpdateDuration = updateWorld();
			lastProcessPlayersDuration = processPlayers();
			lastProcessNpcsDuration = processNpcs();
			lastProcessMessageQueuesDuration = processMessageQueues();
			lastUpdateClientsDuration = updateClients();
			lastDoCleanupDuration = doCleanup();
			// lastExecuteWalkToActionsDuration = executeWalkToActions();
		});
	}

	protected final long updateWorld() {
		return getServer().bench(() -> getServer().getWorld().run());
	}

	protected final long updateClients() {
		return getServer().bench(() -> {
			for (final Player player : getServer().getWorld().getPlayers()) {
				sendUpdatePackets(player);
				player.process();
			}
		});
	}

	protected final long doCleanup() {// it can do the teleport at this time.
		return getServer().bench(() -> {
			World world = getServer().getWorld();
			world.getPlayers().forEach(Player::resetAfterUpdate);
			world.getNpcs().forEach(Npc::resetAfterUpdate);
		});
	}

	protected final long executeWalkToActions() {
		return getServer().bench(() -> {
			final EntityList<Player> players = getServer().getWorld().getPlayers();
			for (final Player player : players) {
				final WalkToAction walkToAction = player.getWalkToAction();
				if (walkToAction != null) {
					if (walkToAction.shouldExecute()) {
						walkToAction.execute();
					}
				}
			}
		});
	}

	protected final long processNpcs() {
		return getServer().bench(() -> {
			final boolean shouldUpdatePosition = !getServer().getConfig().WANT_CUSTOM_WALK_SPEED;
			final EntityList<Npc> npcs = getServer().getWorld().getNpcs();
			npcs.forEach(n -> {
				try {
					if (n.isUnregistering()) {
						getServer().getWorld().unregisterNpc(n);
						return;
					}

					// Only do the walking tick here if the NPC's walking tick matches the game tick
					if (shouldUpdatePosition) {
						n.updatePosition();
					}
				} catch (final Exception e) {
					LOGGER.error("Error while updating " + n + " at position " + n.getLocation() + " loc: " + n.getLoc());
					LOGGER.catching(e);
				}
			});
		});
	}

	/**
	 * Updates the messages queues for each player
	 */
	protected final long processMessageQueues() {
		return getServer().bench(() -> {
			for (final Player player : getServer().getWorld().getPlayers()) {
				final PrivateMessage pm = player.getNextPrivateMessage();
				if (pm != null) {
					Player affectedPlayer = getServer().getWorld().getPlayer(pm.getFriend());
					if (affectedPlayer != null) {
						boolean blockAll = affectedPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, affectedPlayer.isUsingCustomClient())
							== PlayerSettings.BlockingMode.All.id();
						boolean blockNone = affectedPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, affectedPlayer.isUsingCustomClient())
							== PlayerSettings.BlockingMode.None.id();
						if (!player.getSocial().isFriendsWith(affectedPlayer.getUsernameHash())) {
							player.message("Unable to send message - player not on your friendlist.");
						} else if (((affectedPlayer.getSocial().isFriendsWith(player.getUsernameHash()) && !blockAll) || blockNone)
							&& !affectedPlayer.getSocial().isIgnoring(player.getUsernameHash()) || player.isMod()) {
							ActionSender.sendPrivateMessageSent(player, affectedPlayer.getUsernameHash(), pm.getMessage(), false);
							ActionSender.sendPrivateMessageReceived(affectedPlayer, player, pm.getMessage(), false);
						}

						player.getWorld().getServer().getGameLogger().addQuery(new PMLog(player.getWorld(), player.getUsername(), pm.getMessage(),
							DataConversions.hashToUsername(pm.getFriend())));
					} else {
						// player not online
						if (pm.getFriend() >= 0L) {
							try {
								int friendId = player.getWorld().getServer().getDatabase().playerIdFromUsername(DataConversions.hashToUsername(pm.getFriend()));

								if (player.getWorld().getServer().getDatabase().playerExists(friendId)) {
									// player not online
									player.message("Unable to send message - player unavailable.");
								}
							} catch (Exception e) { }
						}
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
							boolean blockNone = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, player.isUsingCustomClient())
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
		});
	}

	/**
	 * Update the position of players, and check if who (and what) they are
	 * aware of needs updated
	 */
	protected final long processPlayers() {
		final boolean shouldUpdatePosition = !getServer().getConfig().WANT_CUSTOM_WALK_SPEED;
		return getServer().bench(() -> {
			for (final Player player : getServer().getWorld().getPlayers()) {
				// Checking login because we don't want to unregister more than once
				if (player.isUnregistering() && player.isLoggedIn()) {
					getServer().getWorld().unregisterPlayer(player);
					continue;
				}

				// Only do the walking tick here if the Players' walking tick matches the game tick
				if (shouldUpdatePosition) {
					player.updatePosition();
				}

				if (player.getUpdateFlags().hasAppearanceChanged()) {
					player.incAppearanceID();
				}
			}
		});
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

	public void setLastExecuteWalkToActionsDuration(long lastExecuteWalkToActionsDuration) {
		this.lastExecuteWalkToActionsDuration = lastExecuteWalkToActionsDuration;
	}
}
