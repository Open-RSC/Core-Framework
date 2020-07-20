package com.openrsc.server.plugins.authentic.commands;

import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.CommandTrigger;

import javax.lang.model.SourceVersion;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class CommandRouter implements CommandTrigger {

	// TODO: Fix the DEV role to be above the MOD role. in Group.java
	private ArrayList<Integer> regularUser = new ArrayList<Integer>() {{
		add(Group.USER); add(Group.TESTER); add(Group.PLAYER_MOD);
		add(Group.EVENT); add(Group.MOD); add(Group.DEV);
		add(Group.SUPER_MOD); add(Group.ADMIN); add(Group.OWNER);
	}};
	private ArrayList<Integer> playerModerator = new ArrayList<Integer>() {{
		add(Group.PLAYER_MOD); add(Group.EVENT); add(Group.MOD); add(Group.DEV);
		add(Group.SUPER_MOD); add(Group.ADMIN); add(Group.OWNER);
	}};
	private ArrayList<Integer> eventModerator = new ArrayList<Integer>() {{
		add(Group.EVENT); add(Group.MOD); add(Group.DEV);
		add(Group.SUPER_MOD); add(Group.ADMIN); add(Group.OWNER);
	}};
	private ArrayList<Integer> gameModerator = new ArrayList<Integer>() {{
		add(Group.MOD); add(Group.DEV);	add(Group.SUPER_MOD);
		add(Group.ADMIN); add(Group.OWNER);
	}};
	private ArrayList<Integer> gameDeveloper = new ArrayList<Integer>() {{
		add(Group.DEV); add(Group.SUPER_MOD); add(Group.ADMIN); add(Group.OWNER);
	}};
	private ArrayList<Integer> superModerator = new ArrayList<Integer>() {{
		add(Group.SUPER_MOD); add(Group.ADMIN); add(Group.OWNER);
	}};
	private ArrayList<Integer> gameAdministrator = new ArrayList<Integer>() {{
		add(Group.ADMIN); add(Group.OWNER);
	}};
	private ArrayList<Integer> gameOwner = new ArrayList<Integer>() {{ add(Group.OWNER); }};

	// These roles can be changed in the future, if a command needs only specific roles to access it.
	// An example of this would be the TESTER role, which may need commands such as "::item".
	private HashMap<String, ArrayList<Integer>> authorizedRoles = new HashMap<String, ArrayList<Integer>>() {{

		// RegularPlayer.java
		put("gang", regularUser);
		put("wilderness", regularUser);
		put("c", regularUser);
		put("clanaccept", regularUser);
		put("partyaccept", regularUser);
		put("claninvite", regularUser);
		put("clankick", regularUser);
		put("gameinfo", regularUser);
		put("event", regularUser);
		put("g", regularUser);
		put("p", regularUser);
		put("online", regularUser);
		put("uniqueonline", regularUser);
		put("leaveparty", regularUser);
		put("joinclan", regularUser);
		put("shareloot", regularUser);
		put("shareexp", regularUser);
		put("onlinelist", regularUser);
		put("groups", regularUser);
		put("time", regularUser);
		put("kills", regularUser);
		put("pair", regularUser);
		put("d", regularUser);
		put("commands", regularUser);

		// PlayerModerator.java
		put("gmute", playerModerator);
		put("mute", playerModerator);
		put("alert", playerModerator);

		// Event.java
		put("tp", eventModerator);
		put("go", eventModerator);
		put("goto", eventModerator);
		put("blink", eventModerator);
		put("dismiss", eventModerator);
		put("invulnerable", eventModerator);
		put("check", eventModerator);
		put("partyhall", eventModerator);
		put("stoppvpevent", eventModerator);
		put("startpvpevent", eventModerator);
		put("group", eventModerator);
		put("invisible", eventModerator);
		put("quickbank", new ArrayList<Integer>() {{
			add(Group.EVENT); add(Group.ADMIN); add(Group.OWNER);
		}});

		// Moderator.java
		put("say", gameModerator);
		put("summon", gameModerator);
		put("info", gameModerator);
		put("checkinv", gameModerator);
		put("checkbank", gameModerator);
		put("announce", gameModerator);
		put("kick", gameModerator);

		// Development.java
		put("stat", gameDeveloper);
		put("curstat", gameDeveloper);
		put("npc", gameDeveloper);
		put("removenpc", gameDeveloper);
		put("object", gameDeveloper);
		put("removeobject", gameDeveloper);
		put("rotateobject", gameDeveloper);
		put("tile", gameDeveloper);
		put("debugregion", gameDeveloper);
		put("coords", gameDeveloper);
		put("serverstats", gameDeveloper);
		put("droptest", gameDeveloper);

		// SuperModerator.java
		put("setcache", superModerator);
		put("getcache", superModerator);
		put("removecache", superModerator);
		put("setquest", superModerator);
		put("completequest", superModerator);
		put("getquest", superModerator);
		put("reloadworld", superModerator);
		put("summonall", superModerator);
		put("dismissall", superModerator);
		put("fatigue", superModerator);
		put("jail", superModerator);
		put("release", superModerator);
		put("ban", superModerator);
		put("viewipbans", superModerator);
		put("ipban", superModerator);
		put("ipcount", superModerator);

		// Admins.java
		put("saveall", gameAdministrator);
		put("holidaydrop", gameAdministrator);
		put("stopholidaydrop", gameAdministrator);
		put("checkholidaydrop", gameAdministrator);
		put("npckills", gameAdministrator);
		put("restart", gameAdministrator);
		put("grounditem", gameAdministrator);
		put("removegrounditem", gameAdministrator);
		put("shutdown", gameAdministrator);
		put("update", gameAdministrator);
		put("item", gameAdministrator);
		put("bankitem", gameAdministrator);
		put("fillbank", gameAdministrator);
		put("unfillbank", gameAdministrator);
		put("quickauction", gameAdministrator);
		put("beastmode", gameAdministrator);
		put("hp", gameAdministrator);
		put("prayer", gameAdministrator);
		put("kill", gameAdministrator);
		put("damage", gameAdministrator);
		put("wipeinv", gameAdministrator);
		put("wipebank", gameAdministrator);
		put("massitem", gameAdministrator);
		put("massnpc", gameAdministrator);
		put("npctalk", gameAdministrator);
		put("playertalk", gameAdministrator);
		put("damagenpc", gameAdministrator);
		put("npcevent", gameAdministrator);
		put("chickenevent", gameAdministrator);
		put("stopnpcevent", gameAdministrator);
		put("checknpcevent", gameAdministrator);
		put("wildrule", gameAdministrator);
		put("freezeexperience", gameAdministrator);
		put("shootme", gameAdministrator);
		put("npcrangeevent", gameAdministrator);
		put("npcfightevent", gameAdministrator);
		put("npcrangedlvl", gameAdministrator);
		put("getnpcstats", gameAdministrator);
		put("strpotnpc", gameAdministrator);
		put("combatstylenpc", gameAdministrator);
		put("combatstyle", gameAdministrator);
		put("setnpcstats", gameAdministrator);
		put("skull", gameAdministrator);
		put("npcrangeevent2", gameAdministrator);
		put("ip", gameAdministrator);
		put("appearance", gameAdministrator);
		put("spawnnpc", gameAdministrator);
		put("winterholidayevent", gameAdministrator);
	}};

	@Override
	public boolean blockCommand(Player player, String command, String[] args) {
		command = command.toLowerCase();
		ArrayList<Integer> roles = authorizedRoles.getOrDefault(command, null);
		if (roles == null || !roles.contains(player.getGroupID())) {
			return false;
		}
		return true;
	}

	@Override
	public void onCommand(final Player player, String command, String[] args) {
		String methodCommand = command;
		try {
			// if command is reserved keyword append "_" to end to match java method
			if (SourceVersion.isKeyword(command)) {
				methodCommand += "_";
			}
			Method toExecute = Commands.class.getMethod(methodCommand, Player.class, String.class, String[].class);
			toExecute.invoke(null, player, command, args);
		}
		catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
			System.out.println(e.getCause().toString());
			for (StackTraceElement z : e.getCause().getStackTrace()) {
				System.out.println(z);
			}
		}
	}
}
