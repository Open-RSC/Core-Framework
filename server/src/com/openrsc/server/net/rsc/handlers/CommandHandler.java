package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.CommandStruct;
import com.openrsc.server.plugins.triggers.CommandTrigger;

import java.util.Arrays;

public final class CommandHandler implements PayloadProcessor<CommandStruct, OpcodeIn> {
	public void process(CommandStruct payload, Player player) throws Exception {
		if (System.currentTimeMillis() - player.getLastCommand() < 1000 && !player.isAdmin()) {
			player.message(player.getConfig().MESSAGE_PREFIX + "There's a second delay between using commands");
		} else {
			String s = payload.command;
			handleCommandString(player, s);
		}
	}
	public static void handleCommandString(Player player, String s) {
		int firstSpace = s.indexOf(" ");
		String cmd = s;
		String[] args = new String[0];
		if (firstSpace != -1) {
			cmd = s.substring(0, firstSpace).trim();
			args = s.substring(firstSpace + 1).trim().split(" ");
		}
		if (player.getWorld().getServer().getDiscordService() != null) {
			String[] ignoredCommands = {
				"gang",
				"c",
				"clanaccept",
				"partyaccept",
				"claninvite",
				"clankick",
				"gameinfo",
				"event",
				"g",
				"pk",
				"p",
				"online",
				"uniqueonline",
				"leaveparty",
				"joinclan",
				"shareloot",
				"shareexp",
				"onlinelist",
				"onlinelistlocs",
				"groups",
				"ranks",
				"time",
				"date",
				"datetime",
				"pair",
				"d",
				"commands",
				"b",
				"qoloptout",
				"qoloptoutconfirm",
				"certoptout",
				"certoptoutconfirm",
				"toggleglobalchat",
				"getholidaydrop",
				"checkholidaydrop",
				"checkholidayevent",
				"drop",
				"toggleblockchat",
				"toggleblockprivate",
				"toggleblocktrade",
				"toggleblockduel",
				"clientlimitations",
				"setversion",
				"skiptutorial",
				"oldtrade",
				"notradeconfirm",
				"coords",
				"setlanguage",
				"language",
				"togglereceipts",
				"getpidlesscatching",
				"tellpidlesscatching",
				"pidless",
				"maxplayersperip",
				"mppi",
				"setglobalmessagecolor",
				"globalquest",
				"gq",
				"globalprivate",
				"gp",
				"set_icon",
				"redhat",
				"rhel",
				"robe",
				"setrobe",
				"setrobes",
				"become",
				"becomenpc",
				"morph",
				"morphnpc",
				"becomegod",
				"speaktongues",
				"restorehumanity",
				"resetappearance",
				"check",
				"pr",
				"pn",
				"pos",
				"lain",
				"leapaboutinstantnavigator",
				"hellonavi",
				"navi",
				"becomelain",
				"weird",
				"weirdplayer",
				"stay",
				"reset",
				"uptime",
				"enable_protocol_extensions",
				"kc",
				"kills"
			};
			if (player.isPlayerMod() && !Arrays.asList(ignoredCommands).contains(cmd.toLowerCase())) {
				player.getWorld().getServer().getDiscordService().staffCommandLog(player, "::" + cmd + " " + String.join(" ", args));
			}
		}
		player.getWorld().getServer().getPluginHandler().handlePlugin(
				CommandTrigger.class,
				player,
				new Object[]{player, cmd.toLowerCase(), args}
		);
	}
}
