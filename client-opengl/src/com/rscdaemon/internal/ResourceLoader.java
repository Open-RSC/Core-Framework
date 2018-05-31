package com.rscdaemon.internal;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ResourceLoader
{
	public final static InputStream loadResource(String resourceName)
		throws
			IOException
	{
		// First, try to load from classloader resources
		InputStream rv = ResourceLoader.class.getResourceAsStream(resourceName);
		
		// No dice?  Try the filesystem
		if(rv == null)
		{
			rv = new FileInputStream(resourceName);
		}
		return rv;
	}
}
