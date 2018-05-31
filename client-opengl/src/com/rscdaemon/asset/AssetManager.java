package com.rscdaemon.asset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetManager
{
	private final Map<Class<? super Asset>, List<Asset>> assets = 
			new HashMap<Class<? super Asset>, List<Asset>>();

	@SuppressWarnings("unchecked")
	public void add(Asset asset)
	{
		List<Asset> assets = this.assets.get(asset.getClass());
		if(assets == null)
		{
			assets = new ArrayList<Asset>();
			this.assets.put((Class<? super Asset>)asset.getClass(), assets);
		}
		assets.add(asset);
	}

	public List<Asset> getAssets(Class<? extends Asset> type)
	{
		return assets.get(type);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Asset> T getAsset(Class<T> type, int id)
	{
		return (T) assets.get(type).get(id);
	}
}
