package com.openrsc.server.plugins.commands;

import com.openrsc.server.Server;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.CommandListener;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.StaffLog;
import com.openrsc.server.util.rsc.DataConversions;

public final class SuperModerator implements CommandListener {

	public void onCommand(String cmd, String[] args, Player player) {
		if (isCommandAllowed(player, cmd))
			handleCommand(cmd, args, player);
	}

	public boolean isCommandAllowed(Player player, String cmd) {
		return player.isSuperMod();
	}

	@Override
	public void handleCommand(String cmd, String[] args, Player player) {
		if (cmd.equalsIgnoreCase("spawnnpc")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (radius) (time in minutes)");
				return;
			}

			int id = -1;
			try {
				id = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (radius) (time in minutes)");
				return;
			}

			int radius = -1;
			if(args.length >= 3) {
				try {
					radius = Integer.parseInt(args[1]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (radius) (time in minutes)");
					return;
				}
			}
			else {
				radius = 1;
			}

			int time = -1;
			if(args.length >= 4) {
				try {
					time = Integer.parseInt(args[2]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (radius) (time in minutes)");
					return;
				}
			}
			else {
				time = 10;
			}

			if (EntityHandler.getNpcDef(id) == null) {
				player.message(messagePrefix + "Invalid spawn npc id");
				return;
			}

			final Npc n = new Npc(id, player.getX(), player.getY(),
				player.getX() - radius, player.getX() + radius,
				player.getY() - radius, player.getY() + radius);
			n.setShouldRespawn(false);
			World.getWorld().registerNpc(n);
			Server.getServer().getEventHandler().add(new SingleEvent(null, time * 60000) {
				@Override
				public void action() {
					n.remove();
				}
			});

			player.message(messagePrefix + "You have spawned " + EntityHandler.getNpcDef(id).getName() + ", radius: " + radius + " for " + time + " minutes");
		}
		else if (cmd.equalsIgnoreCase("fatigue")) {
			if(args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] (percentage)");
				return;
			}

			Player p = World.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if(p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not fatigue a staff member of equal or greater rank.");
				return;
			}

			int fatigue;
			try {
				fatigue = args.length > 1 ? Integer.parseInt(args[1]) : 100;
			}
			catch(NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [amount]");
				return;
			}

			if(fatigue < 0)
				fatigue = 0;
			if(fatigue > 100)
				fatigue = 100;
			p.setFatigue(fatigue * 750);

			if(p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "Your fatigue has been set to " + ((p.getFatigue() / 25) * 100 / 750) + "% by a staff member");
			}
			player.message(messagePrefix + p.getUsername() + "'s fatigue has been set to " + ((p.getFatigue() / 25) * 100 / 750 / 4) + "%");
			GameLogging.addQuery(new StaffLog(player, 12, p, p.getUsername() + "'s fatigue percentage was set to " + fatigue + "% by " + player.getUsername()));
		}
		else if (cmd.equalsIgnoreCase("skull")) {
			if(args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] (boolean)");
				return;
			}

			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if(p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not skull a staff member of equal or greater rank.");
				return;
			}

			boolean skull;
			boolean toggle;
			if(args.length > 1) {
				try {
					skull = DataConversions.parseBoolean(args[1]);
					toggle = false;
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] (boolean)");
					return;
				}
			} else {
				toggle = true;
				skull = false;
			}

			if ((toggle && p.isSkulled()) || (!toggle && !skull)) {
				p.removeSkull();
			} else {
				p.addSkull(1200000);
			}

			String skullMessage = p.isSkulled() ? "added" : "removed";
			if(p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "PK skull has been " + skullMessage + " by a staff member");
			}
			player.message(messagePrefix + "PK skull has been " + skullMessage + ": " + p.getUsername());
		}
		else if (cmd.equalsIgnoreCase("jail")) {
			if (args.length != 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}

			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if(p.isJailed()) {
				player.message(messagePrefix + "You can not jail a player who has already been jailed.");
				return;
			}

			if(p.isStaff()) {
				player.message(messagePrefix + "You can not jail a staff member.");
				return;
			}

			Point originalLocation = p.jail();
			GameLogging.addQuery(new StaffLog(player, 5, player.getUsername() + " has summoned " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation));
			player.message(messagePrefix + "You have jailed " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation);
			if(p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "You have been jailed to " + p.getLocation() + " from " + originalLocation + " by " + player.getStaffName());
			}
		}
		else if (cmd.equalsIgnoreCase("release")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if(p.isStaff()) {
				player.message(messagePrefix + "You can not release a staff member.");
				return;
			}

			if(!p.isJailed()) {
				player.message(messagePrefix + p.getUsername() + " has not been jailed.");
				return;
			}

			Point originalLocation = p.releaseFromJail();
			GameLogging.addQuery(new StaffLog(player, 5, player.getUsername() + " has returned " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation));
			player.message(messagePrefix + "You have released " + p.getUsername() + " from jail to " + p.getLocation() + " from " + originalLocation);
			if(p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "You have been released from jail to " + p.getLocation() + " from " + originalLocation + " by " + player.getStaffName());
			}
		}
		else if (cmd.equalsIgnoreCase("ip")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			player.message(messagePrefix + p.getUsername() + " IP address: " + p.getCurrentIP());
		}
		else if (cmd.equalsIgnoreCase("ipcount")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			int count = 0;
			for (Player worldPlayer : world.getPlayers()) {
				if(worldPlayer.getCurrentIP().equals(p.getCurrentIP()))
					count++;
			}

			player.message(messagePrefix + p.getUsername() + " IP address: " + p.getCurrentIP() + " has " + count + " connections");
		}
	}
}
