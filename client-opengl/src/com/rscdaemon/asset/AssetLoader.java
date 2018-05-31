package com.rscdaemon.asset;

/**
 * An interface for all classes that load {@link Asset assets}
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 * 
 */
public interface AssetLoader
{
	/**
	 * Adds the provided {@link ProgressListener} to this 
	 * <code>AssetLoader</code>
	 * 
	 * @param listener the {@link ProgressListener} to add
	 * 
	 */
	void addProgressListener(ProgressListener listener);

	/**
	 * Removes the provided {@link ProgressListener} from this 
	 * <code>AssetLoader</code>
	 * 
	 * @param listener the {@link ProgressListener} to remove
	 * 
	 */
	void removeProgressListener(ProgressListener listener);
	
	/**
	 * Loads the provided {@link AssetManager}
	 * 
	 * @param manager the {@link AssetManager} to load
	 * 
	 */
	void load(AssetManager manager);
}
