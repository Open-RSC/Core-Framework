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
package org.rscemulation.installer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import javax.swing.JOptionPane;

import org.rscemulation.installer.internationalization.LocaleProvider;


/**
 * A remote logging utility that provides error details to the update server.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public class Logger
{
	public static void error(String userFriendlyMessage, Throwable t)
	{
		StringBuilder error = new StringBuilder(t.getClass().getName()).append(": ").append(t.getMessage()).append("\nat ");
		do
		{
			for(StackTraceElement ste : t.getStackTrace())
			{
				error.append(ste).append('\n');
			}
			t = t.getCause();
			if(t != null)
			{
				error.append("Caused By:\n").append(t.getClass().getName()).append(": ").append(t.getMessage()).append("\nat ");
			}
		}
		while(t != null);
		String ticketID = null;
		BufferedReader isr = null;
		try
		{
			isr = new BufferedReader(new InputStreamReader(new URI("http", 
					Config.UPDATE_HOST, 
					Config.ERROR_REPORTING_PAGE, 
					"error="+error.toString() + 
					"&os="+Config.OPERATING_SYSTEM + 
					"&os_version="+Config.OPERATING_SYSTEM_VERSION + 
					"&java_vendor="+Config.JAVA_VENDOR + 
					"&java_version="+Config.JAVA_VERSION, 
					null).toURL().openStream()));
			ticketID = isr.readLine();
		}
		catch(Throwable th)
		{
			/** Unfortunately, we can't log this remotely, so ignore it. */
		}
		finally
		{
			if(isr != null)
			{
				try
				{
					isr.close();
				}
				catch(IOException ioe)
				{
					/** Ignore */
				}
			}
		}
		JOptionPane.showMessageDialog(null, userFriendlyMessage + (ticketID != null ? "\n\n" + String.format(LocaleProvider.getString("Logger.IssueTicketMessage"), ticketID) : ""), LocaleProvider.getString("Logger.GenericErrorTitle"), JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Attempts to report the provided error to the update server.  If no 
	 * network connection is available, the error is obviously not sent to 
	 * the update server, but a message dialog is still presented to the 
	 * end-user containing the same details that are normally sent to the 
	 * update server.
	 * 
	 * @param t the error that was encountered
	 * 
	 */
	public static void error(Throwable t)
	{
		error(t.getMessage(), t);
	}
}
