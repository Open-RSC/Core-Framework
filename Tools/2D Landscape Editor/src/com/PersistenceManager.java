package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.thoughtworks.xstream.XStream;

public class PersistenceManager {
    private static final XStream xstream = new XStream();

    static {
	addAlias("GameObjectLoc", "com.data.GameObjectLoc");
	addAlias("NPCLoc", "com.data.NpcLoc");
	addAlias("ItemLoc", "com.data.ItemLoc");
    }

    private static void addAlias(String name, String className) {
	try {
	    xstream.alias(name, Class.forName(className));
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}
    }

    public static Object load(File file) {
	try {
	    InputStream is = new GZIPInputStream(new FileInputStream(file));
	    Object rv = xstream.fromXML(is);
	    return rv;
	} catch (IOException ioe) {
	    System.err.println(ioe.getMessage());
	}
	return null;
    }

    public static void write(File file, Object o) {
	try {
	    OutputStream os = new GZIPOutputStream(new FileOutputStream(file));
	    xstream.toXML(o, os);
	} catch (IOException ioe) {
	    System.err.println(ioe.getMessage());
	}
    }
}
