package org.openrsc.server.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.openrsc.server.Config;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.entityhandling.defs.ItemDef;
import org.openrsc.server.model.World;

public final class ItemExporter {

	public static void main(String[] args) throws Exception {
		try 
		{
            String configFile   = args.length < 1 ? "config/config.xml" : args[0];
            File file           = new File(configFile);
			if(!file.exists())
			{
				System.err.println("Could not find configuration file: " + configFile);
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
		out.writeInt(EntityHandler.itemCount());
		
		for (int i = 0; i < EntityHandler.itemCount(); i++) {
			ItemDef def = EntityHandler.getItemDef(i);
			out.writeByte(def.getName().length());
			out.writeBytes(def.getName());
			out.writeByte(def.getDescription().length());
			out.writeBytes(def.getDescription());
			out.writeByte(def.getCommand().length());
			out.writeBytes(def.getCommand());
			out.writeInt(def.getBasePrice());
			out.writeInt(def.getBaseTokenPrice());
			out.writeInt(def.getSprite());
			out.writeInt(def.getPictureMask());
			out.writeBoolean(def.isStackable());
			out.writeBoolean(def.isWieldable());
			out.writeBoolean(def.questItem());
            
            /*System.out.println("Name Length: " + def.getName().length());
            System.out.println("Name: " + def.getName());
            System.out.println("Description Length: " + def.getDescription().length());
            System.out.println("Description: " + def.getDescription());
            System.out.println("Command Length: " + def.getCommand().length());
            System.out.println("Base Price: " + def.getCommand());
            System.out.println("Command: " + def.getBasePrice());
            System.out.println("Base Token Price: " + def.getBaseTokenPrice());
            System.out.println("Sprite: " + def.getSprite());
            System.out.println("Picture Mask: " + def.getPictureMask());
            System.out.println("Is Stackable: " + def.isStackable());
            System.out.println("Is Wieldable: " + def.isWieldable());
            System.out.println("Is Quest Item: " + def.questItem());
            System.out.println("---------------------------");*/
		}
		Files.write(Paths.get("items.dat"), baos.toByteArray());
	}

}
