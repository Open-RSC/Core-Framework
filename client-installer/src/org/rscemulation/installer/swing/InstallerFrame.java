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
package org.rscemulation.installer.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.rscemulation.installer.Config;
import org.rscemulation.installer.exec.ClientLauncher;
import org.rscemulation.installer.gfx.ResourceLoader;
import org.rscemulation.installer.internationalization.LocaleProvider;

/**
 * The main application window of the installer
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public class InstallerFrame
	extends
		JFrame
{
	private static final long serialVersionUID = 1962486523230690400L;
	
	/// The background image of this frame while updating
	private final Image updatingBackgroundImage = 
			ResourceLoader.loadImage(
					Config.UPDATING_BACKGROUND_IMAGE_PATH);

	/// The background image of this frame after updating is finished
	private final Image updatedBackgroundImage = 
			ResourceLoader.loadImage(
					Config.UPDATED_BACKGROUND_IMAGE_PATH);
	
	/// The control that renders the updates page
	private final UpdatePageViewer updateViewer = 
			new UpdatePageViewer("http://" + Config.UPDATE_HOST + Config.UPDATE_FEED_PAGE,
								 Config.UPDATE_VIEWER_X,
								 Config.UPDATE_VIEWER_Y,
								 Config.UPDATE_VIEWER_WIDTH,
								 Config.UPDATE_VIEWER_HEIGHT,
								 ResourceLoader.loadImage(Config.UPDATE_VIEWER_SCROLLBAR_TRACK_IMAGE_PATH),
								 ResourceLoader.loadImage(Config.UPDATE_VIEWER_SCROLLBAR_GRIP_IMAGE_PATH));

	/// The progress bar that shows download progression
	private final JProgressBar progressBar = 
			new ProgressBar(Config.PROGRESS_BAR_X, 
							Config.PROGRESS_BAR_Y, 
							Config.PROGRESS_BAR_WIDTH, 
							Config.PROGRESS_BAR_HEIGHT);
	
	/// The label that shows the current operation status
	private final JLabel statusText = 
			new AutoCenteringLabel(LocaleProvider.getString("Installer.CheckingForUpdatesMessage"),
								   Config.STATUS_LABEL_FONT,
								   Config.STATUS_LABEL_COLOR,
								   Config.STATUS_LABEL_X,
								   Config.STATUS_LABEL_Y);
	
	/// The button that is clicked to launch the game
	private final JButton launchButton = 
			new TransparencyEnabledButton(
					LocaleProvider.getString("Installer.LaunchClientButtonText"),
					Config.BUTTON_X,
					Config.BUTTON_Y,
					Config.BUTTON_FONT,
					Config.PLAY_BUTTON_TEXT_COLOR, 
					ResourceLoader.loadImage(
							Config.PLAY_BUTTON_DEFAULT_IMAGE_PATH),
					ResourceLoader.loadImage(
							Config.PLAY_BUTTON_ROLLOVER_IMAGE_PATH));

	/// Are we still updating?
	private boolean updating = true;
	
	/**
	 * Invoked to update the status text
	 * 
	 * @param txt the new status text to set
	 * 
	 */
	public void onStatusChange(String txt)
	{
		statusText.setText(txt);
	}

	/**
	 * Invoked to reset the progress bar
	 * 
	 */
	public void resetDownloadProgress()
	{
		progressBar.setIndeterminate(false);
		progressBar.setValue(0);
	}

	/**
	 * Invoked when a new download progress is available
	 * 
	 * @param percent the new progress value
	 * 
	 */
	public void updateDownloadProgress(int percent)
	{
		progressBar.setValue(percent);
	}

	/**
	 * Invoked when updating has completed
	 */
	void onUpdateFinished()
	{
		progressBar.setVisible(false);
		statusText.setVisible(false);
		super.getContentPane().add(launchButton);
		updating = false;
		revalidate();
		super.getContentPane().repaint();
	}
	
	/**
	 * Creates an <code>InstallerFrame</code>
	 * 
	 * @throws IOException if any I/O errors occur
	 * 
	 */
	public InstallerFrame()
		throws
			IOException
	{
		super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		super.setResizable(false);
		super.setContentPane(
			new JPanel()
			{
				private static final long serialVersionUID = 1236474325029684314L;
	
				{
					super.setLayout(null);
					super.setOpaque(false);
					super.setPreferredSize(new Dimension(Config.INSTALLER_WIDTH, Config.INSTALLER_HEIGHT));
					super.add(updateViewer);
					super.add(statusText);
					super.add(progressBar);
				}
				public void paintComponent(Graphics g)
				{
					super.paintComponent(g);
					g.drawImage(updating ? updatingBackgroundImage : updatedBackgroundImage, 0, 0, this);
				}
			}
		);
		launchButton.addActionListener(new ClientLauncher(Config.CLIENT_MAIN_METHOD_ARGS));
		pack();
		new BackgroundWorker(this).execute();
	}
}
