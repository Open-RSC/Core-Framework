package launcher;

import launcher.Utils.Defaults;
import launcher.Utils.Utils;

public class Main {

    public static void main(final String[] args) {

        // Config file default
        String configFileLocation = Defaults._DEFAULT_CONFIG_DIR;

        // Get args from the command line if any
        if (args.length > 0) {
            // Help modifier
            if (args[0].contains("-h") || args[0].contains("--help")) {
                System.out.println("Help for the RSC launcher:\n" +
                        "	--help, -h displays this help message\n" +
                        "	--dir, -d changes the cache directory location\n" +
                        "Example:\n" +
                        "java -jar OpenRSC.jar -d /home/foo/.local/openrsc");
                return;
            } else if (args[0].contains("-d") || args[0].contains("--dir")) { // Change cache directory modifier
                // Check if there is a second arg (path)
                if (args.length > 1) {

                    // Check if the provided arg is a valid path
                    if (Utils.isValidPath(args[1])) { // Valid path
                        configFileLocation = Utils.getCanonicalPath(args[1]);
                    } else { // Invalid path
                        System.out.println("Error: please provide a valid path.\n" +
                                "Usage: java -jar OpenRSC.jar -d /path/to/cache/folder");
                        return;
                    }
                } else { // No path specified
                    System.out.println("Error: no path specified.\n" +
                            "Usage: java -jar OpenRSC.jar -d /path/to/cache/folder");
                    return;
                }
            } else { // Unrecognized modifier
                System.out.println("Unrecognized modifier.\n" +
                        "Use -h for help.");
                return;
            }
        }

        Launcher mainLauncher = new Launcher();
        mainLauncher.initializeLauncher();

    }

}
