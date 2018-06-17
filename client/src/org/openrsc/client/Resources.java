package org.openrsc.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;
import org.openrsc.client.loader.various.AppletUtils;

public class Resources
{
	public static InputStream load(String path)
	{
		try
		{
			{
				return new FileInputStream(AppletUtils.CACHE + System.getProperty("file.separator") + path.substring(1));
			}
		}
		catch(IOException ioe)
		{
			JOptionPane.showMessageDialog(null, "Unable to locate resource: " + path, "A fatal error has occurred", JOptionPane.OK_OPTION);
			throw (RuntimeException)new RuntimeException().initCause(ioe);
		}
	}
}
