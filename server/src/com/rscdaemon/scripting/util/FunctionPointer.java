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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import com.rscdaemon.scripting.ScriptError;

/**
 * Essentially a workaround for lack of lambda expressions, 
 * <code>FunctionPointer</code>s are special objects that provide opaque 
 * handles to arbitrary methods.  While utilizing special under the hood 
 * functionality including the caching of reflection operations, the 
 * use of this class also helps to cut down on boilerplate code.
 * <br><br><strong>This class should be replaced once lambda expressions 
 * are included in an officially sanctioned JDK.</strong>
 * 
 * @author Zilent
 *
 * @version 1.0
 *
 * @since 3.3.0
 * 
 */
public final class FunctionPointer
{
	/// The {@link java.lang.invoke.MethodHandle handle} to function
	private final MethodHandle mh;
	
	/**
	 * Constructs a FunctionPointer to the provided method of the provided 
	 * class
	 * 
	 * @param clazz the class that contains the function
	 * 
	 * @param method the name of the function
	 * 
	 * @param argumentTypes the argument signature of the function
	 * 
	 * @throws ScriptError if the provided function could not be found
	 * 
	 */
	public FunctionPointer(Class<?> clazz, String method, 
			Class<?>... argumentTypes)
		throws
			ScriptError
	{
		try
		{
			MethodType sig = MethodType.methodType(void.class, argumentTypes);
			mh = MethodHandles.lookup().findVirtual(clazz, method, sig);
		}
		catch (Throwable t)
		{
			throw (ScriptError)new ScriptError(null, "Method " + 
				clazz.getName() + "." + method + " not found").initCause(t);
		}
	}
	
	/**
	 * Invokes the pointed to function with the provided arguments
	 * 
	 * @param args the arguments to run the function with
	 * 
	 * @throws ScriptError if the function invocation fails
	 * 
	 */
	public void invoke(Object... args)
		throws
			ScriptError
	{
		try
		{
			if(args.length > 1)
			{
				mh.invokeWithArguments(args);
			}
			else
			{
				mh.invokeWithArguments(args);
			}
		}
		catch(Throwable t)
		{
			throw (ScriptError)new ScriptError(null, 
					"Function invocation failed").initCause(t);
		}
	}
}
