package com.openrsc.server.plugins.quests.members.legendsquest.obstacles;

import com.openrsc.server.external.SpellDef;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.handlers.SpellHandler;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.SpellLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestDarkMetalGate implements OpLocTrigger, SpellLocTrigger {


	public static final int DARK_METAL_GATE = 1165;

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == DARK_METAL_GATE;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == DARK_METAL_GATE) {
			if (command.equalsIgnoreCase("open")) {
				if (player.getY() <= 3715) {
					mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "You open the gates and walk through..");
					player.teleport(474, 3720);
					player.message("You magically appear in another area of the cave system.");
					return;
				}
				mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "This gate is fused with rock, it doesn't seem possible to open it.",
					"But it does look slightly strange in some way.");
			} else if (command.equalsIgnoreCase("search")) {
				mes(player.getWorld().getServer().getConfig().GAME_TICK * 5, "It just looks like a normal gate...");
				mes(player.getWorld().getServer().getConfig().GAME_TICK, "At first...");
				mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "And then you notice that some of the bars of metal make up letters.",
					"After some time you manage to make sense of it...",
					"Would you like to read it?");
				int menu = multi(player,
					"Yes, I'll read it.",
					"No, I don't want to read that.",
					"Search further...");
				if (menu == 0) {
					mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "You attempt to read the message in the gate...");
					ActionSender.sendBox(player, "Gates of metal will not be kind, % %To those who care not for the way of mind. % %To all men of learning and supernatural powers, % %With book and rune spend the long dark hours. % %If passage further you would endure, % %Give me a taste of your power so pure. % %", true);
				} else if (menu == 1) {
					player.message("You decide not to read the message.");
				} else if (menu == 2) {
					mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "You scour the gate for any more clues...",
						"Something etched into the wall nearby catches your eye...",
						"It looks like a picture of four pillars or constructions.",
						"Over the first pillar is a picture of a cloud...",
						"Over the second pillar are some etched flickering flames...",
						"Over the third pillar is the carved image of a dew drop or a tear...",
						"Over the fourth pillar is the likeness of a ploughed field...",
						"All of these images are contained within a sphere.");
					say(player, null, "Hmmm, I wonder what they could mean?");
				}
			}
		}
	}

	@Override
	public void onSpellLoc(Player player, GameObject obj, SpellDef spell) {
		if (obj.getID() == DARK_METAL_GATE) {
			switch (spell.getName()) {
				case "Charge Fire Orb":
				case "Charge air Orb":
				case "Charge Water Orb":
				case "Charge earth Orb":
					if (!SpellHandler.checkAndRemoveRunes(player, spell)) {
						return;
					}
					mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "The orb shatters with the power of the magic.",
						"The spell works and the gates open.");
					player.teleport(474, 3714);
					mes(player.getWorld().getServer().getConfig().GAME_TICK * 8, "You magically appear in a different part of the cave system.");
					mes(player.getWorld().getServer().getConfig().GAME_TICK * 2, "It seems that the gate was a test of magical ability.",
						"As soon as you enter this room, you are filled with dread.",
						"In the centre of the room is a large gaping hole.",
						"It goes down a long way...");
					break;
				default:
					player.message("Nothing interesting happens");
					break;
			}
		}
	}

	@Override
	public boolean blockSpellLoc(Player player, GameObject obj, SpellDef spell) {
		return obj.getID() == DARK_METAL_GATE;
	}
}
