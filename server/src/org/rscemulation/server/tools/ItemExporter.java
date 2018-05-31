package org.rscemulation.server.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.rscemulation.server.Config;
import org.rscemulation.server.entityhandling.EntityHandler;
import org.rscemulation.server.entityhandling.defs.ItemDef;
import org.rscemulation.server.model.World;

public final class ItemExporter {

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
		}
		Files.write(Paths.get("items.dat"), baos.toByteArray());
	}

}
