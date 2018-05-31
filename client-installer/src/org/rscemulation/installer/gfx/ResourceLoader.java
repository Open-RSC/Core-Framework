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
package org.rscemulation.installer.gfx;

import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.rscemulation.installer.internationalization.LocaleProvider;

/**
 * A utility class to aid in attempting to load resources via the system 
 * class loader, which for all intents and purposes *should* work correctly, 
 * given that all of the requested resources are located within the classpath 
 * environment variable of this application instance.
 * <br><br>
 * In the event that a resource could not be located, the end-user shall be 
 * provided with a dialog explaining that a resource could not be located and 
 * is presented with three choices, OK, RETRY, and ABORT.
 * <br><br>
 * If:
 * <ul>
 * 	<li>OK is clicked, then the application continues without the resource.
 * 	</li>
 * 	<li>RETRY is clicked, then the application tries to load the requested 
 * 	resource again (which should fail again, but what the hell!)</li>
 * 	<li>ABORT is clicked, then the application shall exit, throwing an 
 * 	{@link ExceptionInInitializerError} whose cause is the underlying 
 * 	exception that caused this dialog to appear in the first place.
 * </ul>
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public final class ResourceLoader
{

	/**
	 * Attempts to load an {@link Image} located at the provided classpath 
	 * location
	 * <br>
	 * <br>
	 * Example:
	 * <pre>
	 * // Load an image, 'image.png' found in package 'path.to.the'
	 * ResourceLoader.loadImage("path/to/the/image.png");
	 * </pre>
	 * @param pathToImage the path to the requested image
	 * 
	 * @return an {@link Image} created from the resource found at the 
	 * provided classpath location or null if an error occurs and the user 
	 * still wishes to continue loading the application
	 * 
	 * @throws ExceptionInInitializerError if an error occurs and the user 
	 * wishes to abort this application instance
	 * 
	 */
	public static Image loadImage(String pathToImage)
	{
		boolean retry = true;
		do
		{
			try
			{
				return ImageIO.read(
						ResourceLoader.class.getResourceAsStream("/" + ResourceLoader.class.getPackage().getName().replaceAll("\\.", "/") + "/" + pathToImage));
			}
			catch(Exception e)
			{
				Object[] options = { 
					LocaleProvider.getString(
							"ResourceLoader.OkButtonText"), //$NON-NLS-1$
					LocaleProvider.getString(
							"ResourceLoader.RetryButtonText"), //$NON-NLS-1$
					LocaleProvider.getString(
							"ResourceLoader.CancelButtonText") }; //$NON-NLS-1$
				switch(JOptionPane.showOptionDialog(
						null, 
						LocaleProvider.getString(
							"ResourceLoader.ErrorDialogTitle")//$NON-NLS-1$
							+ System.getProperty("line.separator")//$NON-NLS-1$
						+ e.getMessage(),
						LocaleProvider.getString(
							"ResourceLoader.ErrorDialogText"),  //$NON-NLS-1$
						JOptionPane.DEFAULT_OPTION, 
						JOptionPane.WARNING_MESSAGE,
						null, 
						options, 
						options[0]))
				{
				case 0:
					retry = false;
					break;
				case 2:
					throw (ExceptionInInitializerError)
							new ExceptionInInitializerError().initCause(e);
				}
			}
		}
		while(retry);
		return null;
	}
}
