package com.openrsc.server.plugins.commands;

import com.openrsc.server.Server;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.CommandListener;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.StaffLog;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

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
		else if (cmd.equalsIgnoreCase("fatigue"))
		{
			if(args.length < 1)
			{
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [amount]");
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

			try {
				int fatigue = args.length > 1 ? Integer.parseInt(args[1]) : 100;
				if(fatigue < 0)
					fatigue = 0;
				if(fatigue > 100)
					fatigue = 100;
				p.setFatigue(fatigue * 750);

				player.message(messagePrefix + p.getUsername() + "'s fatigue has been set to " + ((p.getFatigue() / 25) * 100 / 750) + "%");
				GameLogging.addQuery(new StaffLog(player, 12, p, p.getUsername() + "'s fatigue percentage was set to " + fatigue + "% by " + player.getUsername()));
			}
			catch(NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [amount]");
				return;
			}
		}
		else if (cmd.equalsIgnoreCase("skull")) {
			if(args.length == 0) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player]");
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

			String skullMessage;
			if(p.isSkulled()) {
				p.removeSkull();
				skullMessage = "removed";
			}
			else {
				p.addSkull(1200000);
				skullMessage = "added";
			}
			p.message(messagePrefix + "Skull has been " + skullMessage + " by an admin");
			player.message(messagePrefix + "Skull has been " + skullMessage + ": " + p.getUsername());
		}
		else if (cmd.equalsIgnoreCase("say")) { // SAY is not configged out for mods.
			String newStr = "";

			for (int i = 0; i < args.length; i++) {
				newStr += args[i] + " ";
			}
			GameLogging.addQuery(new StaffLog(player, 13, newStr.toString()));
			newStr = player.getStaffName() + player.getUsername() + ": @whi@" + newStr;
			for (Player p : World.getWorld().getPlayers()) {
				ActionSender.sendMessage(p, player, 1, MessageType.GLOBAL_CHAT, newStr, player.getIcon());
			}
		}
		else if (cmd.equalsIgnoreCase("summon")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}

			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if(p.wasSummoned()) {
				player.message(messagePrefix + "You can not summon a player who has already been summoned.");
				return;
			}

			if(p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not summon a staff member of equal or greater rank.");
				return;
			}

			if(player.getLocation().inWilderness() && !player.isSuperMod()) {
				player.message(messagePrefix + "You can not summon players into the wilderness.");
				return;
			}

			Point originalLocation = p.summon(player);
			GameLogging.addQuery(new StaffLog(player, 15, player.getUsername() + " has summoned " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation));
			player.message(messagePrefix + "You have summoned " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation);
			p.message(messagePrefix + "You have been summoned by " + player.getStaffName());
		}
		else if (cmd.equals("return")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if(p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not return a staff member of equal or greater rank.");
				return;
			}

			if(!p.wasSummoned()) {
				player.message(messagePrefix + p.getUsername() + " has not been summoned.");
				return;
			}

			Point originalLocation = p.returnFromSummon();
			GameLogging.addQuery(new StaffLog(player, 15, player.getUsername() + " has returned " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation));
			player.message(messagePrefix + "You have returned " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation);
			p.message(messagePrefix + "You have been returned by " + player.getStaffName());
		}
		else if (cmd.equalsIgnoreCase("jail")) {
			if (args.length != 1)
			{
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
			p.message(messagePrefix + "You have been jailed to " + p.getLocation() + " from " + originalLocation + " by " + player.getStaffName());
		}
		else if (cmd.equals("release")) {
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
			p.message(messagePrefix + "You have been released from jail to " + p.getLocation() + " from " + originalLocation + " by " + player.getStaffName());
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
				if(worldPlayer.getCurrentIP() == p.getCurrentIP())
					count++;
			}

			player.message(messagePrefix + p.getUsername() + " IP address: " + p.getCurrentIP() + " has " + count + " connections");
		}
	}
}
