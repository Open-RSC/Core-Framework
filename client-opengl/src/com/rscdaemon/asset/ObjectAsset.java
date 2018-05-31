package com.rscdaemon.asset;

/**
 * An asset wrapper class for 3D meshes that may be interacted with
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public class ObjectAsset
	implements
		Asset
{
	
	private static final long serialVersionUID = 2188474532100245662L;

	/// The name of this <code>ObjectAsset</code>
	private final String name;

	/// The description of this <code>ObjectAsset</code>
	private final String description;
	
	/// The primary command of this <code>ObjectAsset</code>
	private final String primaryCommand;
	
	/// The secondary command of this <code>ObjectAsset</code>
	private final String secondaryCommand;
		
	/// The model ID of this <code>ObjectAsset</code>
	private final int modelID;
	
	/**
	 * Constructs an <code>ObjectAsset</code> with the provided values
	 * 
	 * @param name the name of this <code>ObjectAsset</code>
	 * 
	 * @param description the description of this <code>ObjectAsset</code>
	 * 
	 * @param primaryCommand the primary command of this 
	 * <code>ObjectAsset</code>
	 * 
	 * @param secondaryCommand the secondary command of this 
	 * <code>ObjectAsset</code>
	 * 
	 * @param modelID the model ID of this <code>ObjectAsset</code>
	 * 
	 */
	public ObjectAsset(String name, String description, String primaryCommand, String secondaryCommand, int modelID)
	{
		this.name = name;
		this.description = description;
		this.primaryCommand = primaryCommand;
		this.secondaryCommand = secondaryCommand;
		this.modelID = modelID;
	}

	/**
	 * Retrieves the name of this <code>ObjectAsset</code>
	 * 
	 * @return the name of this <code>ObjectAsset</code>
	 * 
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Retrieves the description of this <code>ObjectAsset</code>
	 * 
	 * @return the description of this <code>ObjectAsset</code>
	 * 
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Retrieves the primary command of this <code>ObjectAsset</code>
	 * 
	 * @return the primary command of this <code>ObjectAsset</code>
	 * 
	 */
	public String getPrimaryCommand()
	{
		return primaryCommand;
	}

	/**
	 * Retrieves the secondary command of this <code>ObjectAsset</code>
	 * 
	 * @return the secondary command of this <code>ObjectAsset</code>
	 * 
	 */
	public String getSecondaryCommand()
	{
		return secondaryCommand;
	}

	/**
	 * Retrieves the model ID of this <code>ObjectAsset</code>
	 * 
	 * @return the model ID of this <code>ObjectAsset</code>
	 * 
	 */
	public int getModelID()
	{
		return modelID;
	}
}
