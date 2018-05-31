package com.rscdaemon.core;

/**
 * This interface represents an object that uses system resources that 
 * should be explicitly freed when an object is no longer used.  Generally 
 * speaking, classes that implement this interface should be subject to 
 * debug assertions that are processed in {@link Object#finalize()} that 
 * alert the developer if a resource has not been properly disposed of.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 * 
 */
public interface Disposable
{
	/**
	 * Called when this object is no longer used, this method performs 
	 * any resource deallocation, context unbinding, or other clean-up 
	 * procedures.  <strong>Failing to call dispose on a resource before 
	 * it becomes unreachable is an error.</strong>
	 * 
	 * @since 1.0
	 * 
	 */
	void dispose();
}
