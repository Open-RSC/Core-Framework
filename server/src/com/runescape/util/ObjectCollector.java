package com.runescape.util;

import java.util.List;

/**
 * @author hikilaka
 */
public interface ObjectCollector<T> {

	public List<T> collect();
	
}