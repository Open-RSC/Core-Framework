package org.rscemulation.server;

import org.rscemulation.server.model.ChatMessage;
import org.rscemulation.server.model.Entity;
import org.rscemulation.server.model.GameObject;
import org.rscemulation.server.model.Item;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.PlayerAppearance;
import org.rscemulation.server.model.Projectile;
import org.rscemulation.server.model.World;
import org.rscemulation.server.packetbuilder.RSCPacketBuilder;
import org.rscemulation.server.util.DataConversions;
import org.rscemulation.server.util.EntityList;

public final class ClientUpdater {
	private EntityList<Player> players = World.getPlayers();
	private EntityList<Npc> npcs = World.getNpcs();
	
	public ClientUpdater() {
		World.setClientUpdater(this);
	}

	public void updateClients() {
		updateNpcPositions();
		synchronized(players) {
			updatePlayersPositions();
			updateMessageQueues();
			updateOffers();
			for (Player p : players) {
				if (p != null) {
					updateTimeouts(p);
					updatePlayerPositions(p);
					updateNpcPositons(p);
					updateGameObjectPositions(p);
					updateWallObjectPositions(p);
					updateItemPositions(p);
					updateChatMessages(p);
					updateHits(p);
					updateProjectiles(p);
					updateUsernames(p);
					updateWornItems(p);
					updatePlayerAppearances(p);
					updateNpcAppearances(p);
					
				}
			}
			updateCollections();
		}
	}	
	
	private void updateNpcPositions() {
		synchronized (npcs) {
			for (Npc n : npcs) {
				if (n.hasPlayersInZone()) {
					n.resetMoved();
					n.updatePosition();
					n.updateAppearanceID();
				}
			}
		}
	}
	
	private void updatePlayersPositions() {
		for (Player p : players) {
			p.resetMoved();
			p.updatePosition();
			p.updateAppearanceID();
		}
		for (Player p : players) {
			p.revalidateWatchedPlayers();
			p.revalidateWatchedObjects();
			p.revalidateWatchedItems();
			p.revalidateWatchedNpcs();
			p.updateViewedPlayers();
			p.updateViewedObjects();
			p.updateViewedItems();
			p.updateViewedNpcs();
		}
	}
	
	public void updateMessageQueues() {
		for (Player sender : players) {
			ChatMessage message = sender.getNextChatMessage();
			if (message == null || !sender.loggedIn())
				continue;
			for (Player recipient : sender.getViewArea().getPlayersInView()) {
				if (!recipient.loggedIn())
					continue;
				if (!recipient.getPrivacySetting(0) && !recipient.isFriendsWith(sender.getUsernameHash()) && !sender.isMod())
					continue;
				if (recipient.isIgnoring(sender.getUsernameHash()) && !sender.isMod())
					continue;
				recipient.informOfChatMessage(message);
			}
		}
	}
	
	public void updateOffers() {
		for (Player player : players) {
			if (!player.requiresOfferUpdate())
				continue;
			player.setRequiresOfferUpdate(false);
			if (player.isTrading()) {
				Player affectedPlayer = player.getWishToTrade();
				if (affectedPlayer == null)
					continue;
				affectedPlayer.sendTradeItems();
			} else if(player.isDueling()) {
				Player affectedPlayer = player.getWishToDuel();
				if (affectedPlayer == null)
					continue;
				player.sendDuelSettingUpdate();
				affectedPlayer.sendDuelSettingUpdate();
				affectedPlayer.sendDuelItems();
			}
		}
	}

	private void updateTimeouts(final Player p) {
		if (!p.destroyed()) {
			long curTime = System.currentTimeMillis();
			long lastOnlineTimer = 0;
			int lastOnlineCount = 0;
			if (curTime - lastOnlineTimer > 1000) {
				if (lastOnlineCount != World.getPlayers().count()) {
					p.sendOnlineCount();
					lastOnlineTimer = curTime;
					lastOnlineCount = World.getPlayers().count();
				}
			}

			if (curTime - p.getLastPing() > 30000 && !p.destroying()) {
				p.destroy(false);
			} else if (p.warnedToMove() && !p.isMod()) {
					if (curTime - p.getLastMoved() >= 60 * 30 * 1000 && p.loggedIn())
						World.unregisterEntity(p);
			} else {
				if (curTime - p.getLastMoved() >= 60 * 25 * 1000 && !p.warnedToMove()) {
					p.warnToMove();	
					p.sendMessage("@pri@You have not moved in 25 minutes.  Please move to a new area to avoid the logout.");
				}
			}
		}// else
		//	p.putInUnregisterQueue();
	}
	
	private void updateCollections() {
		for (Player p : players) {
			if (p != null) {
				if (p.isRemoved() && p.initialized() && !p.isUnregistered())
					World.unregisterEntity(p);
				else {
					p.getWatchedPlayers().update();
					p.getWatchedObjects().update();
					p.getWatchedItems().update();
					p.getWatchedNpcs().update();
					p.clearProjectilesNeedingDisplayed();
					p.clearPlayersNeedingHitsUpdate();
					p.clearNpcsNeedingHitsUpdate();
					p.clearChatMessagesNeedingDisplayed();
					p.clearNpcMessagesNeedingDisplayed();			
					p.resetSpriteChanged();
					p.setWornItemsChanged(false);
					p.setAppearnceChanged(false);
				}
			}
		}
		synchronized(npcs) {
			for (Npc n : npcs) {
				n.resetSpriteChanged();
				n.setAppearnceChanged(false);
			}
		}
	}

	private void updatePlayerPositions(Player player) {
		RSCPacketBuilder packet = new RSCPacketBuilder();
		packet.setID(145);
		packet.addBits(player.getX(), 11);
		packet.addBits(player.getY(), 13);
		packet.addBits(player.getSprite(), 4);
		packet.addBits(player.getWatchedPlayers().getKnownEntities().size(), 16);
		for(Player p : player.getWatchedPlayers().getKnownEntities()) {
			if(player.getIndex() != p.getIndex()) {
				packet.addBits(p.getIndex(), 16);
				if (player.getWatchedPlayers().isRemoving(p)) {
					packet.addBits(1, 1);
					packet.addBits(1, 1);
					packet.addBits(12, 4);
				} else if (p.hasMoved()) {
					packet.addBits(1, 1);
					packet.addBits(0, 1);
					packet.addBits(p.getSprite(), 3);
				} else if (p.spriteChanged()) {
					packet.addBits(1, 1);
					packet.addBits(1, 1);
					packet.addBits(p.getSprite(), 4);
				} else
					packet.addBits(0, 1);
			}
		}

		for (Player p : player.getWatchedPlayers().getNewEntities()) {
			byte[] offsets = DataConversions.getMobPositionOffsets(p.getLocation(), player.getLocation());
			packet.addBits(p.getIndex(), 16);
			packet.addBits(offsets[0], 5);
			packet.addBits(offsets[1], 5);
			packet.addBits(p.getSprite(), 4);
			packet.addBits(0, 1);
		}

		if (packet.toPacket() != null)
			player.getSession().write(packet.toPacket());
		for (Player p : player.getWatchedPlayers().getNewEntities())
			player.getActionSender().sendLastMoved(p);
	}
	
	private void updateNpcPositons(Player player) {
		RSCPacketBuilder packet = new RSCPacketBuilder();
		packet.setID(77);
		packet.addBits(player.getWatchedNpcs().getKnownEntities().size(), 16);
		for (Npc n : player.getWatchedNpcs().getKnownEntities()) {
			packet.addBits(n.getIndex(), 16);
			if (player.getWatchedNpcs().isRemoving(n)) {
				packet.addBits(1, 1);
				packet.addBits(1, 1);
				packet.addBits(12, 4);
			} else if (n.hasMoved()) {
				packet.addBits(1, 1);
				packet.addBits(0, 1);
				packet.addBits(n.getSprite(), 3);
			} else if(n.spriteChanged()) {
  				packet.addBits(1, 1);
  				packet.addBits(1, 1);
  				packet.addBits(n.getSprite(), 4);
  			} else
  				packet.addBits(0, 1);
		}
		for (Npc n : player.getWatchedNpcs().getNewEntities()) {
			byte[] offsets = DataConversions.getMobPositionOffsets(n.getLocation(), player.getLocation());
			packet.addBits(n.getIndex(), 16);
			packet.addBits(offsets[0], 5);
			packet.addBits(offsets[1], 5);
			packet.addBits(n.getSprite(), 4);
			packet.addBits(n.getID(), 10);
		}
		if (packet.toPacket() != null)
			player.getSession().write(packet.toPacket());
	}
	
	private void updateGameObjectPositions(Player player) {
		if (player.getWatchedObjects().changed()) {
			RSCPacketBuilder packet = new RSCPacketBuilder();
			packet.setID(27);
			for (GameObject o : player.getWatchedObjects().getKnownEntities()) {
				if (o.getType() != 0)
					continue;
				if (player.getWatchedObjects().isRemoving(o)) {
					byte[] offsets = DataConversions.getObjectPositionOffsets(o.getLocation(), player.getLocation());
					packet.addShort(60000);
					packet.addByte(offsets[0]);
					packet.addByte(offsets[1]);
					packet.addByte((byte)o.getDirection());
				}
			}
			for (GameObject o : player.getWatchedObjects().getNewEntities()) {
				if (o.getType() != 0)
					continue;
				byte[] offsets = DataConversions.getObjectPositionOffsets(o.getLocation(), player.getLocation());
				packet.addShort(o.getID());
				packet.addByte(offsets[0]);
				packet.addByte(offsets[1]);
				packet.addByte((byte)o.getDirection());
			}
			if (packet.toPacket() != null)
				player.getSession().write(packet.toPacket());
		}
	}
	
	private void updateWallObjectPositions(Player player) {
		if (player.getWatchedObjects().changed()) {
			RSCPacketBuilder packet = new RSCPacketBuilder();
			packet.setID(95);
			for (GameObject o : player.getWatchedObjects().getKnownEntities()) {
				if (o.getType() != 1)
					continue;
				if (player.getWatchedObjects().isRemoving(o)) {
					byte[] offsets = DataConversions.getObjectPositionOffsets(o.getLocation(), player.getLocation());
					packet.addShort(60000);
					packet.addByte(offsets[0]);
					packet.addByte(offsets[1]);
					packet.addByte((byte)o.getDirection());
				}
			}
			for (GameObject o : player.getWatchedObjects().getNewEntities()) {
				if (o.getType() != 1)
					continue;
				byte[] offsets = DataConversions.getObjectPositionOffsets(o.getLocation(), player.getLocation());
				packet.addShort(o.getID());
				packet.addByte(offsets[0]);
				packet.addByte(offsets[1]);
				packet.addByte((byte)o.getDirection());
			}
			if (packet.toPacket() != null)
				player.getSession().write(packet.toPacket());
		}
	}
	
	private void updateItemPositions(Player player) {
		if (player.getWatchedItems().changed()) {
			RSCPacketBuilder packet = new RSCPacketBuilder();
			packet.setID(109);
			for (Item i : player.getWatchedItems().getKnownEntities()) {
				if (player.getWatchedItems().isRemoving(i)) {
					byte[] offsets = DataConversions.getObjectPositionOffsets(i.getLocation(), player.getLocation());
					packet.addShort(i.getID() + 0x8000);
					packet.addByte(offsets[0]);
					packet.addByte(offsets[1]);
				}
			}
			for (Item i : player.getWatchedItems().getNewEntities()) {
				byte[] offsets = DataConversions.getObjectPositionOffsets(i.getLocation(), player.getLocation());
				packet.addShort(i.getID());
				packet.addByte(offsets[0]);
				packet.addByte(offsets[1]);
			}
			if (packet.toPacket() != null)
				player.getSession().write(packet.toPacket());
		}
	}
	
	private void updateChatMessages(Player player) {
		int updateSize = player.getChatMessagesNeedingDisplayed().size();
		if (updateSize > 0) {
			RSCPacketBuilder updates = new RSCPacketBuilder();
			updates.setID(55);
			updates.addShort(updateSize);
			synchronized (player.getChatMessagesNeedingDisplayed()) {
				for (ChatMessage cm : player.getChatMessagesNeedingDisplayed()) {
					updates.addShort(cm.getSender().getIndex());
					updates.addByte((byte)(cm.getRecipient() == null ? 2 : (cm.getRecipient() instanceof Npc ? 77 : 5)));
					updates.addByte((byte)cm.getLength());
					updates.addBytes(cm.getMessage());
				}
			}
			player.getSession().write(updates.toPacket());
		}
	}
	
	private void updateHits(Player player) {
		int updateSize = player.getPlayersRequiringHitsUpdate().size();
		if (updateSize > 0) {
			RSCPacketBuilder updates = new RSCPacketBuilder();
			updates.setID(56);
			updates.addShort(updateSize);
			for (Player p : player.getPlayersRequiringHitsUpdate()) {
				updates.addShort(p.getIndex());
				updates.addByte((byte)p.getLastDamage());
				updates.addByte((byte)p.getCurStat(3));
				updates.addByte((byte)p.getMaxStat(3));
			}
			player.getSession().write(updates.toPacket());
		}
	}
	
	private void updateProjectiles(Player player) {
		int updateSize = player.getProjectilesNeedingDisplayed().size();
		if (updateSize > 0) {
			RSCPacketBuilder updates = new RSCPacketBuilder();
			updates.setID(57);
			updates.addShort(updateSize);
			for (Projectile p : player.getProjectilesNeedingDisplayed()) {
				Entity victim = p.getVictim();
				if (victim instanceof Npc) {
					updates.addShort(p.getCaster().getIndex());
					updates.addByte((byte)3);
					updates.addShort(p.getType());
					updates.addShort(((Npc)victim).getIndex());
				} else if (victim instanceof Player) {
					updates.addShort(p.getCaster().getIndex());
					updates.addByte((byte)4);
					updates.addShort(p.getType());
					updates.addShort(((Player)victim).getIndex());
				}
			}
			player.getSession().write(updates.toPacket());
		}
	}
	
	private void updateUsernames(Player player) {
		int updateSize = player.getPlayersRequiringUsernameUpdate().size();
		if (updateSize > 0) {
			RSCPacketBuilder updates = new RSCPacketBuilder();
			updates.setID(61);
			updates.addShort(updateSize);
			for (Player p : player.getPlayersRequiringUsernameUpdate()) {
				updates.addShort(p.getIndex());
				updates.addLong(p.getUsernameHash());
			}
			player.getSession().write(updates.toPacket());
		}
	}
	
	private void updateWornItems(Player player) {
		int updateSize = player.getPlayersRequiringWornItemUpdate().size();
		if (updateSize > 0) {
			RSCPacketBuilder updates = new RSCPacketBuilder();
			updates.setID(62);
			updates.addShort(updateSize);
			for(Player p : player.getPlayersRequiringWornItemUpdate()) {
				updates.addShort(p.getIndex());	
				updates.addShort(p.getWornItemID());
				updates.addByte((byte)p.getWornItems().length);
				for (int i : p.getWornItems())
					updates.addByte((byte)i);
			}
			player.getSession().write(updates.toPacket());
		}
	}
	
	private void updatePlayerAppearances(Player player) {
		int updateSize = player.getPlayersRequiringAppearanceUpdate().size();
		if (updateSize > 0) {
			RSCPacketBuilder updates = new RSCPacketBuilder();
			updates.setID(58);
			updates.addShort(updateSize);
			for(Player p : player.getPlayersRequiringAppearanceUpdate()) {
				PlayerAppearance appearance = p.getPlayerAppearance();
				updates.addShort(p.getIndex());
				updates.addByte(appearance.getHairColour());
				updates.addByte(appearance.getTopColour());
				updates.addByte(appearance.getTrouserColour());
				updates.addByte(appearance.getSkinColour());
				updates.addByte((byte)p.getCombatLevel());
				updates.addByte((byte)(p.isSkulled() ? 1 : 0));
				updates.addByte((byte)(p.isAdmin() ? 1 :(p.isMod() ? 2 : (p.isDev() ? 6 :  (p.isEvent() ? 7 : (p.isSub() ? 5 : 4))))));
			}
			player.getSession().write(updates.toPacket());
		}
	}
	
	private void updateNpcAppearances(Player player) {
		int updateSize = player.getNpcMessagesNeedingDisplayed().size() + player.getNpcsRequiringHitsUpdate().size();
		if (updateSize > 0) {
			RSCPacketBuilder updates = new RSCPacketBuilder();
			updates.setID(190);
			updates.addShort(updateSize);
			for (ChatMessage cm : player.getNpcMessagesNeedingDisplayed()) {
				updates.addShort(cm.getSender().getIndex());
				updates.addByte((byte)1);
				updates.addShort(cm.getRecipient().getIndex());
				updates.addByte((byte)cm.getLength());
				updates.addBytes(cm.getMessage());
			}
			for (Npc n : player.getNpcsRequiringHitsUpdate()) {
				updates.addShort(n.getIndex());
				updates.addByte((byte)2);
				updates.addByte((byte)n.getLastDamage());
				updates.addByte((byte)n.getHits());
				updates.addByte((byte)n.getDef().getHits());
			}
			if (updates.toPacket() != null)
				player.getSession().write(updates.toPacket());
		}
	}
}