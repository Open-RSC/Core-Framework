package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Constants;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.plugins.PluginHandler;

public final class CommandHandler implements PacketHandler 
{
	public void handlePacket(Packet p, Player player) throws Exception {
        if (System.currentTimeMillis() - player.getLastCommand() < 1000 && !player.isAdmin())
        {
            player.message(Constants.GameServer.MESSAGE_PREFIX + "There's a second delay between using commands");
        }
        else
        {
            String s = p.readString();
            int firstSpace = s.indexOf(" ");
            String cmd = s;
            String[] args = new String[0];
            if (firstSpace != -1) {
                cmd = s.substring(0, firstSpace).trim();
                args = s.substring(firstSpace + 1).trim().split(" ");
            }

            PluginHandler.getPluginHandler().handleAction("Command",
                    new Object[] { cmd.toLowerCase(), args, player });
        }
	}
}
