package orsc;

public class osConfig {
	/* Android: */
	public static boolean F_ANDROID_BUILD = false; // This MUST be true if Android client or it will crash on launch, needs to be set as public for the Android client to use
	public static final String DL_URL = "game.openrsc.com"; // needs to be set as public for the Android client to use
	public static final String ANDROID_DOWNLOAD_PATH = "https://" + DL_URL + "/downloads/";
	public static final String CACHE_URL = "https://" + DL_URL + "/downloads/";
	public static final int ANDROID_CLIENT_VERSION = 128; // Important! Depends on web server android_version.txt to check for an updated version
	public static boolean F_SHOWING_KEYBOARD = false;
	public static boolean C_HOLD_AND_CHOOSE = true;
	public static int C_LONG_PRESS_TIMER = 5; // default hold timer setting
	public static int C_MENU_SIZE = 3; // default font choice
	public static boolean C_SWIPE_TO_SCROLL = true;
	public static boolean C_SWIPE_TO_ROTATE = true;
	public static boolean C_SWIPE_TO_ZOOM = true;
	public static int C_VOLUME_FUNCTION = 0; // default as rotate
	public static boolean C_ANDROID_INV_TOGGLE = false;
	public static int C_LAST_ZOOM = 75;
}
