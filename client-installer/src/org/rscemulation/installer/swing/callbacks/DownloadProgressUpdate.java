package org.rscemulation.installer.swing.callbacks;

import org.rscemulation.installer.internationalization.LocaleProvider;
import org.rscemulation.installer.swing.InstallerFrame;

public class DownloadProgressUpdate
	implements
		Runnable
{
	
	private final InstallerFrame gui;

	private final String resource;
	
	private final float percent, kbps;

	@Override
	public void run()
	{
		gui.updateDownloadProgress(
				Math.round(percent));
		gui.onStatusChange(String.format(
				LocaleProvider.getString("Installer.DownloadStatusFormat"), 
				resource, percent, kbps));
	}
	
	public DownloadProgressUpdate(InstallerFrame gui, String resource, 
			float percent, float kbps)
	{
		this.gui = gui;
		this.resource = resource;
		this.percent = percent;
		this.kbps = kbps;
	}
}
