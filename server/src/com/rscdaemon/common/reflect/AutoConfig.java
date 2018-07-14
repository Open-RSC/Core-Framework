/*
 * Copyright (C) RSCDaemon - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by RSCDaemon Team <dev@rscdaemon.com>, September, 2012
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

package com.rscdaemon.common.reflect;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A simple utility for automatically loading configuration settings from 
 * the provided file.  The provided file must meet several
 * criteria:
 * <br>
 * <ul>
 * <li>It must conform to a {@linkplain Properties} file format</li>
 * <li>Each property that is to be configured must have a matching 
 * field in the provided configuration object, as well as a valid value 
 * that may be converted to the required type.  For example, for a 
 * properties file:
 * <pre>
 * 	{@code <}xml version="1.0" encoding="UTF-8"?{@code >}
 * 	{@code <}!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd"{@code >}
 * 	{@code <}properties{@code >}
 * 		{@code <}entry key="test"{@code >}this is a test{@code <}/entry{@code >}
 * 	{@code <}/properties{@code >}
 * </pre>
 * ...the provided configuration object must have a public mutator: <br><code>
 * public void setTest(String);<code>
 * </li>
 * <br><br>
 * The following data types are supported:
 * <br>
 * <ul>
 * 	<li>Boolean</li>
 * 	<li>
 * Byte</li>
 * 	<li>Short</li>
 * 	<li>Integer</li>
 * 	<li>Long</li>
 * 	<li>String</li>
 * 	<li>Float</li>
 * 	<li>Double</li>
 * </ul>
 * 
 * @author Zilent
 * 
 * @see Properties
 *
 */
public class AutoConfig
{

	/// Internal logger (uses Apache Commons Logging)
	private static final Log internal_log = 
			LogFactory.getLog(AutoConfig.class);

	/**
	 * Attempts to apply a new configuration to the provided configuration 
	 * object from the provided configuration file.  Note that this method 
	 * will not throw any exceptions, but rather use the internal logger to 
	 * report any issues that might have arisen during the process.  The new 
	 * configuration settings shall be applied to as many fields as possible, 
	 * regardless of how many discrepencies are found in the process.
	 * 
	 * @param config the configuration object to apply new new configuration to
	 * 
	 * @param configFile the configuration file to read the new properties from
	 * 
	 * @return the provided configuration
	 * 
	 */
	public static <T> T applyNewConfiguration(T config, String configFile)
	{
		Properties props = new Properties();
		try
		{
			props.loadFromXML(new FileInputStream(configFile));
		}
		catch (IOException e)
		{
			internal_log.error(e.getMessage(), e.getCause());
			return config;
		}
		/// Iterate through each method of the provided type
		for (Method m : config.getClass().getDeclaredMethods())
		{
			String setting = null;
			/// If it's a public, settable method with a single parameter
			if(Modifier.isPublic(m.getModifiers()) && 
					m.getName().startsWith("set") && 
					m.getParameterTypes().length == 1)
			{
				String methodName = m.getName();
				methodName = methodName.substring(3, methodName.length());
				setting = methodName.substring(0, 1).toLowerCase() + 
						methodName.substring(1);
			}
			else continue;

			setting = props.getProperty(setting);
			/// If there isn't a setting in the provided configuration, skip it
			if(setting == null) continue;
			Class<?> type = m.getParameterTypes()[0];
			/// Invoke the setter method with a proper argument
			try
			{
				if(String.class.equals(type))
				{
					m.invoke(config, setting);
				}
				else if(int.class.equals(type))
				{
					m.invoke(config, Integer.parseInt(setting));
				}
				else if(boolean.class.equals(type))
				{
					m.invoke(config, Boolean.parseBoolean(setting));
				}
				else if(long.class.equals(type))
				{
					m.invoke(config, Long.parseLong(setting));
				}
				else if(byte.class.equals(type))
				{
					m.invoke(config, Byte.parseByte(setting));
				}
				else if(short.class.equals(type))
				{
					m.invoke(config, Short.parseShort(setting));
				}
				else if(float.class.equals(type))
				{
					m.invoke(config, Float.parseFloat(setting));
				}
				else if(double.class.equals(type))
				{
					m.invoke(config, Double.parseDouble(setting));
				}
				else
				{
					/// This can happen if the provided configuration file 
					/// contains an entry for a method that meets the 
					/// selection criteria - simply warn the user about it,
					/// as it is a very tiny performance hit.
					internal_log.warn("AutoConfig encountered an unsupported " +
							"type [" + setting.getClass().getSimpleName() + "]" +
							" - No action has been taken");
				}
			}
			catch(InvocationTargetException ite)
			{
				/// If the invoked method throws an exception...
				internal_log.error(ite.getMessage(), ite.getCause());
			}
			catch(IllegalAccessException iae)
			{
				/// Should never happen - we checked to make sure that 
				/// the method we are invoking was publicly accessible.
				internal_log.debug("This should never happen - " + 
						iae.getMessage(), iae.getCause());
			}
		}
		return config;
	}
}
