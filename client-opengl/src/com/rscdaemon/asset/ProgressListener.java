package com.rscdaemon.asset;

/**
 * A listener that is invoked when an applicable event happens in the asset 
 * loading process.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public interface ProgressListener
{
	void onAssetLoaded(Asset asset);
	
	void onAssetError(Asset asset);
}
