package org.rscemulation.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;

public class Resources
{
	public static InputStream load(String path)
	{
		try
		{
			{
				return new FileInputStream(System.getProperty("user.home") + System.getProperty("file.separator") + ".rscunity" + System.getProperty("file.separator") + path.substring(1));
			}
		}
		catch(IOException ioe)
		{
			JOptionPane.showMessageDialog(null, "Unable to locate resource: " + path, "A fatal error has occurred", JOptionPane.OK_OPTION);
			throw (RuntimeException)new RuntimeException().initCause(ioe);
		}
	}
}
