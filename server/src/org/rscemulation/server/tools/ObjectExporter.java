package org.rscemulation.server.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.rscemulation.server.Config;
import org.rscemulation.server.entityhandling.EntityHandler;
import org.rscemulation.server.entityhandling.defs.GameObjectDef;
import org.rscemulation.server.model.World;

public final class ObjectExporter {

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
		out.writeInt(EntityHandler.objectCount());
		
		for (int i = 0; i < EntityHandler.objectCount(); i++) {
			GameObjectDef def = EntityHandler.getGameObjectDef(i);
			out.writeByte(def.getName().length());
			out.writeBytes(def.getName());
			out.writeByte(def.getDescription().length());
			out.writeBytes(def.getDescription());
			out.writeByte(def.getObjectModel().length());
			out.writeBytes(def.getObjectModel());
			out.writeByte(def.getCommand1().length());
			out.writeBytes(def.getCommand1());
			out.writeByte(def.getCommand2().length());
			out.writeBytes(def.getCommand2());
			out.writeInt(def.getType());
			out.writeInt(def.getWidth());
			out.writeInt(def.getHeight());
			out.writeInt(def.getGroundItemVar());
		}
		Files.write(Paths.get("objects.dat"), baos.toByteArray());
	}

}
