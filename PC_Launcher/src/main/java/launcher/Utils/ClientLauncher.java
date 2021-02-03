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

    public static void launchClient(boolean dev) throws IllegalArgumentException, SecurityException {
        try {
            File f = new File(Defaults._DEFAULT_CONFIG_DIR + File.separator
                    + Defaults._CLIENT_FILENAME + (dev ? "_dev" : "") + ".jar");
            ProcessBuilder pb = new ProcessBuilder(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java", "-jar", f.getAbsolutePath());
            pb.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void exit() {
        System.exit(0);
    }
}
