package org.openrsc.server.packethandler;

import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.apache.mina.common.IoSession;
import org.openrsc.server.Config;
import org.openrsc.server.event.DelayedEvent;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ExploitLog;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Point;
import org.openrsc.server.model.TrajectoryHandler;
import org.openrsc.server.model.World;
import org.openrsc.server.net.Packet;
import org.openrsc.server.net.RSCPacket;
import org.openrsc.server.states.Action;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;
public class DMHandler implements PacketHandler {

	public static final Deque<DMEvent> events = new ArrayDeque<DMEvent>();
	
	private static final class NotifyEvent extends DelayedEvent
	{
		public NotifyEvent(Player player)
		{
			super(player, 5000);
		}

		@Override
		public void run()
		{
			// Another hack.
			if(!super.owner.isFightingInCage() || super.owner.getInDMWith() != null)
			{
				super.running = false;
				return;
			}
			int i = 1;
			for(DMEvent event : events)
			{
				if(event.hack(super.owner))
				{
					super.owner.sendMessage("You are " + i + " in queue");
					break;
				}
				++i;
			}
		}
		
	}
	
	public final static class DMEvent extends DelayedEvent
	{
		public Cage cage;
		private final Player player, affectedPlayer;
		private long started = -1;
		public boolean hack(Player player)
		{
			return this.player == player || this.affectedPlayer == player;
		}
		
		public DMEvent(Player player, Player affectedPlayer)
		{
			super(null, 5000);
			this.player = player;
			this.affectedPlayer = affectedPlayer;
			player.setDeathmatchEvent(this);
			affectedPlayer.setDeathmatchEvent(this);
			World.getDelayedEventHandler().add(new NotifyEvent(player));
			World.getDelayedEventHandler().add(new NotifyEvent(affectedPlayer));
		}
		
		public final void end()
		{
			player.resetDMing();
			affectedPlayer.resetDMing();
			super.running = false;
		}

		@Override
		public void run()
		{
			
			// If not started yet
			if(started == -1)
			{
				if(this != events.peekLast()) return;
				// Check for a cage
				for(Cage cage : cages)
				{
					if(!cage.isActive())
					{
						events.pollLast();
						cage.setActive(true);						
						this.cage = cage;
						started = System.currentTimeMillis();
						for (Player informee : World.getPlayers()) {
							if (informee.getLocation().isInDMArena())
								informee.sendNotification(Config.getPrefix() + player.getUsername() + " (" + player.getCombatLevel() + ") and " + affectedPlayer.getUsername() + " (" + affectedPlayer.getCombatLevel() + ") have entered the DM arena!");
						}
						
						int x1 = Formulae.rand(cage.getDimensions().x, cage.getDimensions().x + cage.getDimensions().width);
						int y1 =  Formulae.rand(cage.getDimensions().y, cage.getDimensions().y + cage.getDimensions().height);
						int x2 = Formulae.rand(cage.getDimensions().x, cage.getDimensions().x + cage.getDimensions().width);
						int y2 =  Formulae.rand(cage.getDimensions().y, cage.getDimensions().y + cage.getDimensions().height);
						
						player.teleport(x1, y1);						
						affectedPlayer.teleport(x2, y2);
						
						started = System.currentTimeMillis();
						cage.setActive(true);
						player.sendDMWindowClose();
						player.sendMessage("Commencing Death Match!");
						affectedPlayer.sendDMWindowClose();
						affectedPlayer.sendMessage("Commencing Death Match!");
						player.resetAllExceptDMing();
						player.setBusy(true);
						player.setStatus(Action.DMING_PLAYER);
						affectedPlayer.resetAllExceptDMing();
						affectedPlayer.setBusy(true);
						affectedPlayer.setStatus(Action.DMING_PLAYER);
						
						player.setInDMWith(affectedPlayer);
						affectedPlayer.setInDMWith(player);
						
						// Enforce prayer rules
						if (player.getDMSetting(0)) {
							for (int x = 0; x < 14; x++) {
								if (player.isPrayerActivated(x)) {
									player.removePrayerDrain(x);
									player.setPrayer(x, false);
								}
								player.sendPrayers();
								if (affectedPlayer.isPrayerActivated(x)) {
									affectedPlayer.removePrayerDrain(x);
									affectedPlayer.setPrayer(x, false);
								}
								affectedPlayer.sendPrayers();
							}
							player.sendPrayers();
							affectedPlayer.sendPrayers();
						}
						
						// Resets stats (if no potions is enabled)
						if (player.getDMSetting(3)) {
							for (int i = 0; i < Formulae.STAT_ARRAY.length; i++) {
								if (player.getCurStat(i) > player.getMaxStat(i)) {
									player.setCurStat(i, player.getMaxStat(i));
									player.sendStat(i);
								}
								if (affectedPlayer.getCurStat(i) > affectedPlayer.getMaxStat(i)) {
									affectedPlayer.setCurStat(i, affectedPlayer.getMaxStat(i));
									affectedPlayer.sendStat(i);
								}
							}
						}
						
						player.curePoison();
						affectedPlayer.curePoison();
						
						// Skull up
						player.addSkull(1200000);
						affectedPlayer.addSkull(1200000);
						
						// Start countdown
						player.startDMCountdown();
						affectedPlayer.startDMCountdown();
						
						if (player.getCurStat(5) < player.getMaxStat(5)) {
							player.setCurStat(5, player.getMaxStat(5));
							player.sendStat(5);
							player.sendSound("recharge", false);
						}
						if (player.getCurStat(3) < player.getMaxStat(3)) {																			
							player.setCurStat(3, player.getMaxStat(3));
							player.sendStat(3);																			
						}
						if (affectedPlayer.getCurStat(5) < affectedPlayer.getMaxStat(5)) {
							affectedPlayer.setCurStat(5, affectedPlayer.getMaxStat(5));
							affectedPlayer.sendStat(5);
							affectedPlayer.sendSound("recharge", false);
						}
						if (affectedPlayer.getCurStat(3) < affectedPlayer.getMaxStat(3)) {																			
							affectedPlayer.setCurStat(3, affectedPlayer.getMaxStat(3));
							affectedPlayer.sendStat(3);																			
						}
						break;
					}
				}
			}
			else
			{
				if(System.currentTimeMillis() - started > 300000)
				{
					player.sendMessage("DM time limit exceeded");
					affectedPlayer.sendMessage("DM time limit exceeded");
					player.teleport(Point.location(218, 2901), true);
					affectedPlayer.teleport(Point.location(218, 2901), true);
					end();
				}
			}
		}
	}
	
	public final static class Cage
	{
		private final Rectangle dimensions;
		private boolean active;
		
		public Cage(Rectangle dimensions)
		{
			this.dimensions = dimensions;
			this.active = false;
		}
		
		public Rectangle getDimensions()
		{
			return dimensions;
		}
		
		public void setActive(boolean active)
		{
			this.active = active;
		}
		
		public boolean isActive()
		{
			return active;
		}
	}
	
	private final static List<Cage> cages = new ArrayList<Cage>();
	
	static
	{
		cages.add(new Cage(new Rectangle(220, 2913, 3,4)));
		cages.add(new Cage(new Rectangle(212, 2913, 3,4)));
		cages.add(new Cage(new Rectangle(212, 2890, 3,4)));
		cages.add(new Cage(new Rectangle(220, 2890, 3,4)));
		cages.add(new Cage(new Rectangle(227, 2898, 4,3)));
		cages.add(new Cage(new Rectangle(227, 2906, 4,3)));
		cages.add(new Cage(new Rectangle(204, 2898, 4,3)));
		cages.add(new Cage(new Rectangle(204, 2906, 4,3)));
	}
	
	private boolean busy(Player player) {
		return player.isBusy() || player.isRanging() || player.accessingBank() || player.isTrading() || player.isDueling();
	}
	
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if (player != null) {
			if(!player.getLocation().isInDMArena())
			{
				return;
			}
			if(player.isFightingInCage())
			{
				return;
			}
			int pID = ((RSCPacket)p).getID();
			Player affectedPlayer = player.getWishToDuel();
			if (affectedPlayer == player) {
				Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "DMHandler (1)", DataConversions.getTimeStamp()));
				return;
			}
			if (player.isDMConfirmAccepted() && affectedPlayer != null && affectedPlayer.isDMConfirmAccepted()) {
				Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "DMHandler (2)", DataConversions.getTimeStamp()));
			}
			if (busy(player) || (!player.getLocation().isInDMArena() && player.getLocation().inWilderness())) {
				unsetOptions(player);
				unsetOptions(affectedPlayer);
				return;
			}
		
		switch (pID) {
			case 78: // Sending DM request
				int index = p.readShort();
				affectedPlayer = World.getPlayer(index);
				if (player != affectedPlayer && affectedPlayer != null && !affectedPlayer.isDMing() && !player.isDMing() && !player.tradeDuelThrottling()) {
					if (player.withinRange(affectedPlayer, 8)) {
						if (!TrajectoryHandler.isRangedBlocked(player.getX(), player.getY(), affectedPlayer.getX(), affectedPlayer.getY())) {
							if (!affectedPlayer.isIgnoring(player.getUsernameHash())) {
								int combDiff = Math.abs(player.getCombatLevel() - affectedPlayer.getCombatLevel());
								if (combDiff >= 0 && combDiff <= 57) {
									player.setWishToDM(affectedPlayer);
									player.sendMessage(Config.getPrefix() + "Sending Death Match request");
									affectedPlayer.sendMessage(DataConversions.ucwords(player.getUsername()).replaceAll("_", " ") + " " + Formulae.getLvlDiffColour(affectedPlayer.getCombatLevel() - player.getCombatLevel()) + "(level-" + player.getCombatLevel() + ")@whi@ wishes to Death Match you");
									if (!player.isDMing() && affectedPlayer.getWishToDM() != null && affectedPlayer.getWishToDM().equals(player) && !affectedPlayer.isDMing()) {
										player.setDMing(true);
										player.resetPath();
										player.clearDMOptions();
										player.resetAllExceptDMing();
										
										affectedPlayer.setDMing(true);
										affectedPlayer.resetPath();
										affectedPlayer.clearDMOptions();
										affectedPlayer.resetAllExceptDMing();
										
										player.sendDMWindowOpen();
										affectedPlayer.sendDMWindowOpen();
									}
								} else
									player.sendMessage(Config.getPrefix() + "You can only Death Match players within 57 combat levels of yours");
							} else
								player.sendMessage(Config.getPrefix() + "Sending Death Match request");
						} else
							player.sendMessage("There is an obstacle in the way");
					} else
						player.sendMessage("I'm not near enough");
				}
				break;
				
			case 79: // DM accepted
				if (System.currentTimeMillis() - player.getLastTradeDuelUpdate() > 50) {
					player.setLastTradeDuelUpdate(System.currentTimeMillis());
					affectedPlayer = player.getWishToDM();
					if (affectedPlayer == null || busy(affectedPlayer) || !player.isDMing() || !affectedPlayer.isDMing()) { // This shouldn't happen
						unsetOptions(player);
						unsetOptions(affectedPlayer);
						return;
					}
					
					player.setDMOfferAccepted(true);
					
					player.sendDMAcceptUpdate();
					affectedPlayer.sendDMAcceptUpdate();
					
					if (affectedPlayer.isDMOfferAccepted()) {
						player.sendDMAccept();
						affectedPlayer.sendDMAccept();
					}
				}
				break;
			case 81: // Confirm accepted
				if(System.currentTimeMillis() - player.getLastTradeDuelUpdate() > 50) {
					player.setLastTradeDuelUpdate(System.currentTimeMillis());				
					affectedPlayer = player.getWishToDM();
					if (affectedPlayer == null || busy(affectedPlayer) || !player.isDMing() || !affectedPlayer.isDMing() || !player.isDMOfferAccepted() || !affectedPlayer.isDMOfferAccepted()) { // This shouldn't happen
						unsetOptions(player);
						unsetOptions(affectedPlayer);
						return;
					}
					player.setDMConfirmAccepted(true);
					if (affectedPlayer.isDMConfirmAccepted())
					{
						DMEvent event = new DMEvent(player, affectedPlayer);
						World.getDelayedEventHandler().add(event);
						events.add(event);
					}
				}
				break;
				
				case 80: // Decline DM
					affectedPlayer = player.getWishToDM();
					if (affectedPlayer == null || busy(affectedPlayer) || !player.isDMing() || !affectedPlayer.isDMing()) { // This shouldn't happen
						unsetOptions(player);
						unsetOptions(affectedPlayer);
						return;
					}
					affectedPlayer.sendMessage(Config.getPrefix() + "Other player left the Death Match screen");
					
					unsetOptions(player);
					unsetOptions(affectedPlayer);
					break;

				case 82: // Set DM options
					if (System.currentTimeMillis() - player.getLastTradeDuelUpdate() > 50) {
						player.setLastTradeDuelUpdate(System.currentTimeMillis());
						affectedPlayer = player.getWishToDM();
						if (affectedPlayer == null || busy(affectedPlayer) || !player.isDMing() || !affectedPlayer.isDMing() || (player.isDMOfferAccepted() && affectedPlayer.isDMOfferAccepted()) || player.isDMConfirmAccepted() || affectedPlayer.isDMConfirmAccepted()) { // This shouldn't happen
							unsetOptions(player);
							unsetOptions(affectedPlayer);
							return;
						}
						
						player.setDMOfferAccepted(false);
						player.setDMConfirmAccepted(false);
						
						affectedPlayer.setDMOfferAccepted(false);
						affectedPlayer.setDMConfirmAccepted(false);
						
						player.sendDMAcceptUpdate();
						affectedPlayer.sendDMAcceptUpdate();
						
						for (int i = 0; i < 4; i++) {
							boolean b = p.readByte() == 1;
							player.setDMSetting(i, b);
							affectedPlayer.setDMSetting(i, b);
						}
						
						player.sendDMSettingUpdate();
						affectedPlayer.sendDMSettingUpdate();
					}
					break;
			}
		}
	}

	private void unsetOptions(Player p) {
		if (p != null)
	      	p.resetDMing();
	}
}