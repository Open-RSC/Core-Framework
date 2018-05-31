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

import java.io.IOException;

import javax.swing.SwingUtilities;

import org.rscemulation.installer.swing.InstallerFrame;

/**
 * The entry point of the RSCEmulation application installer
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public class Installer
{
	public static void main(String... unused)
	{
		SwingUtilities.invokeLater(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						InstallerFrame installer = new InstallerFrame();
						installer.setVisible(true);	
					}
					catch (IOException e)
					{
						e.printStackTrace();
						Logger.error(e);
					}
				}
			}
		);
	}
}
