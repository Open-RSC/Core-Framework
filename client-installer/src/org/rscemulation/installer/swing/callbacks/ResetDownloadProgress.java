package org.rscemulation.installer.swing.callbacks;

import org.rscemulation.installer.swing.InstallerFrame;

public class ResetDownloadProgress
	implements
		Runnable
{

	private final InstallerFrame installer;
	
	@Override
	public void run()
	{
		installer.resetDownloadProgress();
	}

	public ResetDownloadProgress(InstallerFrame installer)
	{
		this.installer = installer;
	}
	
}
