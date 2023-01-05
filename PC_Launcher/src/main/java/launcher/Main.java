package launcher;

import launcher.Utils.Defaults;
import launcher.Utils.Utils;

import java.io.File;

public class Main {

  // DEFAULT FOLDERS
  public static String configFileLocation = "Cache";
  public static String SPRITEPACK_DIR = configFileLocation + File.separator + "video" + File.separator + "spritepacks";
  public static boolean disabledUpdate = false;

  public static void main(final String[] args) {

    handleArgs(args);

    Launcher mainLauncher = new Launcher();
    mainLauncher.initializeLauncher();

  }

  public static void handleArgs(final String[] args) {
    String helpMessage = "Help for the RSC launcher:\n" +
        "	--help, -h displays this help message\n" +
        "	--dir [loc], -d [loc] changes the cache directory location\n" +
        "	--no-update, -n Disables Launcher autoupdate feature and prompt\n" +
        "Example:\n" +
        "java -jar OpenRSC.jar -d /home/foo/.local/openrsc";

    int argIndex = 0;
    while (argIndex < args.length) {
      String arg = args[argIndex];
      if (arg.equals("--help") || arg.equals("-h")) {
        System.out.println(helpMessage);
        System.exit(0);
      } else if (arg.equals("--dir") || arg.equals("-d")) {
        if (argIndex + 1 < args.length) {
          String path = args[argIndex + 1];
          if (Utils.isValidPath(path)) {
            configFileLocation = Utils.getCanonicalPath(path);
            SPRITEPACK_DIR = configFileLocation + File.separator + "video" + File.separator + "spritepacks";
            argIndex += 2;
          } else {
            System.out.println("Error: please provide a valid path.\n" +
                "Usage: java -jar OpenRSC.jar -d /path/to/cache/folder");
            System.exit(1);
          }
        } else {
          System.out.println("Error: no path specified.\n" +
              "Usage: java -jar OpenRSC.jar -d /path/to/cache/folder");
          System.exit(1);
        }
      } else if (arg.equals("--no-update") || arg.equals("-n")) {
        disabledUpdate = true;
        argIndex++;
      } else {
        System.out.println("Unrecognized modifier.\n" +
            "Use -h for help.");
        System.exit(1);
      }
    }
  }

}
