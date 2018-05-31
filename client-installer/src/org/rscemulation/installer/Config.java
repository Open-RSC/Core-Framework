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

import java.awt.Color;
import java.awt.Font;
import java.io.File;

/**
 * In this file, there are compile-time configurations that may be applied 
 * to drastically change how the installer looks and operates without having 
 * to redesign any complex source code.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public class Config
{

	/************************************************************************
	 ************************************************************************
	 **                                                                    **
	 ** GUI related fields - change as required to fit new interfaces      **
	 **                                                                    **
	 ************************************************************************
	 ************************************************************************/

	/// The background image to use when updates are running
	public final static String UPDATING_BACKGROUND_IMAGE_PATH = 
			"background_updating.png";
	
	/// The background image to use when updates are finished
	public final static String UPDATED_BACKGROUND_IMAGE_PATH = 
			"background_updated.png";

	/// The overall width of the installer GUI content pane
	public final static int INSTALLER_WIDTH = 600;
	
	/// The overall height of the installer GUI content pane
	public final static int INSTALLER_HEIGHT = 400;
	
	/// The X-coordinate of the upper-left hand corner of the update viewer
	public final static int UPDATE_VIEWER_X = 64;

	/// The Y-coordinate of the upper-left hand corner of the update viewer
	public final static int UPDATE_VIEWER_Y = 108;
	
	/// The width of the update viewer
	public final static int UPDATE_VIEWER_WIDTH = 470;
	
	/// The height of the update viewer
	public final static int UPDATE_VIEWER_HEIGHT = 200;
		
	/// The image to use for the scrollbar background in the update viewer
	public final static String UPDATE_VIEWER_SCROLLBAR_TRACK_IMAGE_PATH = 
			"track.png";

	/// The image to use for the scrollbar grip in the update viewer
	public final static String UPDATE_VIEWER_SCROLLBAR_GRIP_IMAGE_PATH = 
			"grip.png";
	
	/// The x-coordinate of the center of the 'Play' button
	public final static int BUTTON_X = 300;
	
	/// The y-coordinate of the center of the 'Play' button
	public final static int BUTTON_Y = 347;
	
	/// The font of the 'Play' button
	public final static Font BUTTON_FONT = new Font("Tahoma", Font.PLAIN, 32);
	
	/// The color of the 'Play' button
	public final static Color PLAY_BUTTON_TEXT_COLOR = Color.WHITE;
	
	/// The default icon of the 'Play' button
	public final static String PLAY_BUTTON_DEFAULT_IMAGE_PATH = 
			"default_button.png";
	
	/// The hovered over icon of the 'Play' button
	public final static String PLAY_BUTTON_ROLLOVER_IMAGE_PATH = 
			"rollover_button.png";
	
	/// The x-coordinate of the center of the 'Status Text' label
	public final static int STATUS_LABEL_X = 300;
	
	/// The y-coordinate of the center of the 'Status Text' label
	public final static int STATUS_LABEL_Y = 345;
	
	/// The font of the 'Status Text' label
	public final static Font STATUS_LABEL_FONT = new Font("Tahoma", Font.PLAIN, 12);
	
	/// The color of the 'Status Text' label
	public final static Color STATUS_LABEL_COLOR = Color.white;
	
	/// The x-coordinate of the upper-left corner of the progress bar
	public final static int PROGRESS_BAR_X = 50;
	
	/// The y-coordinate of the upper-left corner of the progress bar
	public final static int PROGRESS_BAR_Y = 333;
	
	/// The width of the progress bar
	public final static int PROGRESS_BAR_WIDTH = 500;
	
	/// The height of the progress bar
	public final static int PROGRESS_BAR_HEIGHT = 26;
	
	/// The color of the progress bar
	public final static Color PROGRESS_BAR_COLOR = new Color(55, 200, 80, 185);
	

	/************************************************************************
	 ************************************************************************
	 **                                                                    **
	 ** Web related fields - change as required to fit new hosting needs   **
	 **                                                                    **
	 ************************************************************************
	 ************************************************************************/

	/// The protocol that should be used to download updates (http, ftp, etc)
	public final static String PROTOCOL = "http";
	
	/// The host that is acting as the update server
	public final static String UPDATE_HOST = "rscemulation.org";

	/// The page that acts as the update files index
	public final static String FILE_INDEX_PAGE = "/installer_index.html";
	
	/// The page that the updates view should pull from
	public final static String UPDATE_FEED_PAGE = "/update_feed.html";

	/// The page that errors should be reported to
	public static final String ERROR_REPORTING_PAGE = "/installer_logger.html";
	
	/************************************************************************
	 ************************************************************************
	 **                                                                    **
	 ** Local-installation related fields - change as required             **
	 **                                                                    **
	 ************************************************************************
	 ************************************************************************/
	
	/// The local directory that acts as the installation home
	public final static String INSTALL_DIR = System.getProperty("user.home") + 
									  File.separator + 
									  "RSCEmulation" + 
									  File.separator;
	
	/// The JAR file that contains the main method of the game client
	public final static String MAIN_CLASS_JAR_FILE = INSTALL_DIR + 
															"RSCEmulation.jar";

	/// The arguments to provide to the main method of the game client
	public static final String[] CLIENT_MAIN_METHOD_ARGS = 
			new String[]{"512", "334"};

	/// The prefix to apply to game threads spawned by this launcher
	public static final String CLIENT_MAIN_THREAD_NAME_PREFIX = 
			"rscemulation-main";
	

	
	/************************************************************************
	 ************************************************************************
	 **                                                                    **
	 ** Updating the fields below is not required, but may add performance **
	 **                                                                    **
	 ************************************************************************
	 ************************************************************************/

	/// The estimated number of entries in the index page
	public final static int EXPECTED_FILE_COUNT = 5;

	/// The temporary-buffer size to use for downloading remote resources
	public final static int DOWNLOAD_BUFFER_SIZE = 1024;

	/************************************************************************
	 ************************************************************************
	 **                                                                    **
	 ** DFW Settings - Don't Fuck With                                     **
	 **                                                                    **
	 ************************************************************************
	 ************************************************************************/
	
	public final static String OPERATING_SYSTEM = System.getProperty("os.name");
	
	public final static String SYSTEM_ARCHITECTURE = System.getProperty("os.arch");
	
	public final static String OPERATING_SYSTEM_VERSION = System.getProperty("os.version");
	
	public final static String JAVA_VENDOR = System.getProperty("java.vendor");
	
	public final static String JAVA_VERSION = System.getProperty("java.version");
}
