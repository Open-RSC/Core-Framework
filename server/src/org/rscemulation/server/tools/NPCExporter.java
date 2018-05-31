package org.rscemulation.server.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.rscemulation.server.Config;
import org.rscemulation.server.entityhandling.EntityHandler;
import org.rscemulation.server.entityhandling.defs.NPCDef;
import org.rscemulation.server.model.World;

public final class NPCExporter {

	public static void main(String[] args) throws Exception {
		if (args.length < 1) 
		{
			System.out
					.println("No configuration file provided - usage: Server <configfile>");
			return;
		}
		try 
		{
			File file = new File(args[0]);
			if (!file.exists()) {
				System.err.println("Could not find configuration file: "
						+ args[0]);
				return;
			}
			Config.initConfig(file);
		} catch (IOException ex) {
			System.err
					.println("An error has been encountered while loading configuration: ");
			ex.printStackTrace();
		}
	
		World.load();
	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(baos);
		out.writeInt(EntityHandler.npcCount());
		
		/*
		 * 
  			NPCDef def = new NPCDef(result.getString("name"));
			def.name = result.getString("name");
			def.description = result.getString("description");
			def. command = result.getString("command");
			def.hits = result.getInt("hits");
			def.attack = result.getInt("attack");
			def.defense = result.getInt("defense");
			def.strength = result.getInt("strength");
			def.hairColour = result.getInt("hair_colour");
			def.topColour = result.getInt("top_colour");
			def.bottomColour = result.getInt("bottom_colour");
			def.skinColour = result.getInt("skin_colour");
			def.camera1 = result.getInt("camera1");
			def.camera2 = result.getInt("camera2");
			def.walkModel = result.getInt("walk_model");
			def.combatModel = result.getInt("combat_model");
			def.combatSprite = result.getInt("combat_sprite");
			def.attackable = result.getInt("attackable") == 1 ? true : false;
			def.respawnTime = result.getInt("respawn");
			def.aggressive = result.getInt("aggressive") == 1 ? true : false;
			def.blocks = result.getInt("block") == 1 ? true : false;
			
			def.sprites = new int[12];
			for (int j = 1; j < 12; j++)
				def.sprites[j - 1] = result.getInt("sprite" + j);

			def.retreats = result.getInt("retreat") == 1 ? true : false;
			def.retreatHits = result.getInt("retreat_hits");
			def.follows = result.getInt("follows") == 1 ? true : false;
			def.undead = result.getInt("undead") == 1 ? true : false;
			def.dragon = result.getInt("dragon") == 1 ? true : false;
		 */
	
		for (int i = 0; i < EntityHandler.npcCount(); i++) {
			NPCDef def = EntityHandler.getNpcDef(i);
			out.writeByte(def.getName().length());
			out.writeBytes(def.getName());
			out.writeByte(def.getDescription().length());
			out.writeBytes(def.getDescription());
			out.writeByte(def.getCommand().length());
			out.writeBytes(def.getCommand());
			out.writeInt(def.getAtt());
			out.writeInt(def.getStr());
			out.writeInt(def.getHits());
			out.writeInt(def.getDef());
			out.writeInt(def.getHairColour());
			out.writeInt(def.getTopColour());
			out.writeInt(def.getBottomColour());
			out.writeInt(def.getSkinColour());
			out.writeInt(def.getCamera1());
			out.writeInt(def.getCamera2());
			out.writeInt(def.getWalkModel());
			out.writeInt(def.getCombatModel());
			out.writeInt(def.getCombatSprite());
			out.writeBoolean(def.isAttackable());
			out.writeInt(def.sprites.length);
			for (int j = 0; j < def.sprites.length; j++)
				out.writeInt(def.sprites[j]);
			
			//System.out.println(i + "NPC Name: " + def.getName() + "\n" + "NPC Desc: " + def.getDescription());
			
		}
		Files.write(Paths.get("npcs.dat"), baos.toByteArray());
	}

}
