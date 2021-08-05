package launcher.Utils;

import javax.swing.*;
import java.io.File;

public class ClientLauncher {
	private static ClassLoader loader;
	private static Class<?> mainClass;
	private static JFrame frame;

	public static JFrame getFrame() {
		return frame;
	}

	public static void launchClient() throws IllegalArgumentException, SecurityException {
		try {
			File f = new File(Defaults._DEFAULT_CONFIG_DIR + File.separator
				+ Defaults._CLIENT_FILENAME + ".jar");
			ProcessBuilder pb = new ProcessBuilder("java", "-jar", f.getAbsolutePath());
			pb.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void launchRSCPlus() throws IllegalArgumentException, SecurityException {
		try {
			String homeDir = Defaults._DEFAULT_CONFIG_DIR + File.separator + "extras" + File.separator + "rscplus";
			ProcessBuilder pb = new ProcessBuilder();
			pb.directory(new File(homeDir));
			pb.command("java", "-jar", "rscplus.jar");
			pb.start();
			exit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void launchAPOS() throws IllegalArgumentException, SecurityException {
		try {
			String homeDir = Defaults._DEFAULT_CONFIG_DIR + File.separator + "extras" + File.separator + "apos" + File.separator + "APOS-master";
			ProcessBuilder pb = new ProcessBuilder();
			pb.directory(new File(homeDir));
			pb.command("java", "-jar", "bot.jar");
			pb.start();
			exit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void launchIdleRSC() throws IllegalArgumentException, SecurityException {
		try {
			String homeDir = Defaults._DEFAULT_CONFIG_DIR + File.separator + "extras" + File.separator + "idlersc" + File.separator + "IdleRSC-master";
			ProcessBuilder pb = new ProcessBuilder();
			pb.directory(new File(homeDir));
			pb.command("java", "-jar", "IdleRSC.jar");
			pb.start();
			exit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void exit() {
		System.exit(0);
	}
}
