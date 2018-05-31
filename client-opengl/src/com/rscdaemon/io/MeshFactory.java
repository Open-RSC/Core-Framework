package com.rscdaemon.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rscdaemon.core.Mesh;
/**
 * A factory class for providing the means to load {@link Mesh meshes} 
 * from various formats.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public abstract class MeshFactory
{
	/// A mapping of factory names to their concrete implementations
	private final static Map<String, MeshFactory> factories =
			new HashMap<String, MeshFactory>();
	
	/// TODO: auto-load via reflection
	static
	{
		factories.put("OBJ", new OBJMeshFactory());
		factories.put("OB3", new OB3MeshFactory());
	}
	
	/**
	 * Attempts to retrieve a factory instance for the provided format
	 * 
	 * @param format the format name of the desired factory
	 * 
	 * @return a factory instance of the provided format, or null if the 
	 * format is not supported
	 * 
	 */
	public static MeshFactory getInstance(String format)
	{
		return factories.get(format);
	}
	
	/// A cache of all {@link Mesh meshes} that were successfully loaded
	private final Map<String, Mesh> cache = new HashMap<String, Mesh>();
	
	/**
	 * Attempts to create a mesh from the provided resource
	 * 
	 * @param resource the path to the resource to load
	 * 
	 * @return a {@link Mesh} generated from the provided resource
	 * 
	 * @throws IOException if any I/O error occurs
	 * 
	 */
	public final Mesh loadMesh(String resource)
		throws
			IOException
	{
		Mesh mesh = cache.get(resource);
		if(mesh == null)
		{
			mesh = loadMesh0(resource);
		}
		cache.put(resource, mesh);
		return mesh;
	}
	
	/**
	 * The implementation detail left up to the concrete factory classes.
	 * 
	 * @param resource the path to the resource to load
	 * 
	 * @return a {@link Mesh} generated from the provided resource
	 * 
	 * @throws IOException if any I/O error occurs
	 * 
	 */
	protected abstract Mesh loadMesh0(String resource) throws IOException;

}
