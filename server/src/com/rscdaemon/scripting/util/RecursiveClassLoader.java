/*
 * Copyright (C) RSCDaemon - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by RSCDaemon Team <dev@rscdaemon.com>, Unknown Date
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
package com.rscdaemon.scripting.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * I'm not very proud of this little hack.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public class RecursiveClassLoader
{
	/**
	 * Determines if this application instance is running from within a JAR 
	 * file or not
	 * 
	 * @return the fully qualified path to the JAR, or null if this instance 
	 * is not running from a JAR
	 * 
	 * @throws IOException if an I/O error occurs
	 * 
	 */
	private static String isJar()
    	throws
    		IOException
    {
    	String classJar = RecursiveClassLoader.class.getResource("/" + RecursiveClassLoader.class.getName().replace('.', '/') + ".class").toString();
		if (classJar.startsWith("jar:"))
		{
	    	URL url = new URL(classJar);
	    	JarURLConnection connection = (JarURLConnection) url.openConnection();
	        url = connection.getJarFileURL();
	        return url.getFile();
		}
		return null;
    }
	
    private final static Class<?>[] loadClasses(String packageName)
    	throws
    		ClassNotFoundException,
    		IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        String jar = isJar();
        if(jar != null)
        {
        	classes.addAll(findJarClasses(new ZipFile(jar), packageName));
        }
        else
        {
            String path = packageName.replace('.', '/');
            classes.addAll(findNonJarClasses(new File(classLoader.getResource(path).getFile().replace("%20", " ").replace("%5b", "[").replace("%5d",  "]")), packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }
    
	private static List<Class<?>> findJarClasses(ZipFile zf, String packageName) throws ClassNotFoundException
    {
    	List<Class<?>> classes = new ArrayList<Class<?>>();
    	Enumeration<? extends ZipEntry> entries = zf.entries();
    	while(entries.hasMoreElements())
    	{
    		ZipEntry entry = entries.nextElement();
    		if (entry.getName().contains(packageName.replace('.', '/')) && entry.getName().endsWith(".class") && !entry.getName().contains("$"))
            {
                classes.add(Class.forName(entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6)));
            }
    	}
    	return classes;
    }
    
    private static List<Class<?>> findNonJarClasses(File directory, String packageName) throws ClassNotFoundException
    {
    	assert directory.exists();
        List<Class<?>> classes = new ArrayList<Class<?>>();
        File[] files = directory.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
            {
                classes.addAll(findNonJarClasses(file, packageName + "." + file.getName()));
            }
            else if (file.getName().endsWith(".class") && !file.getName().contains("$"))
            {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    @SuppressWarnings("unchecked")
	public static <T> List<Class<T>> findClasses(String packageName, Class<T> toFind) throws ClassNotFoundException, IOException
    {
    	List<Class<T>> rv = new ArrayList<Class<T>>();
    	for(Class<?> c: loadClasses(packageName))
    	{
    		if(toFind.isAssignableFrom(c))
    		{
    			rv.add((Class<T>)c);
    		}
    	}
		return rv;
    }
    
}
