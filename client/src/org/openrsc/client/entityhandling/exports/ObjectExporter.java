package org.openrsc.client.entityhandling.exports;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.openrsc.client.entityhandling.EntityHandler;
import org.openrsc.client.entityhandling.defs.GameObjectDef;

public final class ObjectExporter {

	public static void main(String[] args) throws Exception {
		/**
		 * DO NOT RELEASE ME IN THE CLIENT!
		 */
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(baos);
		out.writeInt(EntityHandler.objectCount());
		for (int i = 0; i < EntityHandler.objectCount(); i++) {
			GameObjectDef def = EntityHandler.getObjectDef(i);
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
