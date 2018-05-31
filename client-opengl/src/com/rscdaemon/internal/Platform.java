package com.rscdaemon.internal;

/**
 * A class that provides values that are guaranteed to be cross-platform
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public class Platform
{
	/// The number of bytes per float
	static public final int BYTES_PER_FLOAT = Float.SIZE / Byte.SIZE;
	
	/// The number of bytes per short
	static public final int BYTES_PER_SHORT = Short.SIZE / Byte.SIZE;
}
