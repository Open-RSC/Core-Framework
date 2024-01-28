package launcher.Gameupdater;

import launcher.Main;
import launcher.Utils.Defaults;
import launcher.Utils.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClientDownloader {
  private static final String PROPERTIES_FILE = Main.configFileLocation + File.separator + "client_versions.properties";

  public static void downloadOrUpdate(File gamePath, String fileName, String url, String versionStringVarName) {
    Downloader.currently_updating = true;
    ProgressBar.initProgressBar();
    ProgressBar.setDownloadProgress("Checking for updates", 100.0f);

    new Thread(() -> {
      try {
        double currentVersion = getLocalVersion(versionStringVarName);
        Logger.Info("Checking if local version " + currentVersion + " is up to date with remote version "
            + getRemoteVersionNumber(versionStringVarName));
        if (isUpdateAvailable(currentVersion, versionStringVarName)) {
          Logger.Info("We are not up to date, updating..");
          createDirectory(gamePath);
          downloadFile(gamePath, fileName, url);
          unZipUpdate(gamePath + File.separator + fileName, gamePath.toString());
          updateVersion(versionStringVarName, getRemoteVersionNumber(versionStringVarName));
        }
        ProgressBar.setDownloadProgress("Done", 100.0f);
      } catch (Exception error) {
        Logger.Warn("Update error: " + error.getMessage());
        error.printStackTrace();
      } finally {
        Downloader.currently_updating = false;
      }
    }).start();
  }

  private static double getLocalVersion(String versionStringVarName) {
    try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
      Properties prop = new Properties();
      prop.load(input);
      Logger.Info("Found version " + prop.getProperty(versionStringVarName, "0.0") + " for " + versionStringVarName);
      return Double.parseDouble(prop.getProperty(versionStringVarName, "0.0"));
    } catch (IOException ex) {
      Logger.Warn("Unable to read version: " + ex.getMessage());
      return 0.0;
    }
  }

  public static Double getRemoteVersionNumber(String versionStringVarName) {
    try {
      double extraVersion = 0.0;
      URL updateURL = new URL(Defaults._VERSION_UPDATE_URL);

      // Open connection
      URLConnection connection = updateURL.openConnection();
      connection.setConnectTimeout(3000);
      connection.setReadTimeout(3000);
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        if (line.contains(versionStringVarName)) {
          extraVersion = Double.parseDouble(line.substring(line.indexOf('=') + 1, line.indexOf(';')));
          Logger.Info(versionStringVarName + " remote version: " + extraVersion);
          break;
        }
      }

      in.close();
      return extraVersion;
    } catch (Exception e) {
      Logger.Error("Error checking latest version of " + versionStringVarName);
      e.printStackTrace();
      return 0.0;
    }
  }

  private static void updateVersion(String versionStringVarName, double newVersion) {
    Properties prop = new Properties();
    try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
      // Load existing properties
      prop.load(input);
    } catch (IOException io) {
      Logger.Warn("Unable to load existing versions: " + io.getMessage());
    }

    try (OutputStream output = new FileOutputStream(PROPERTIES_FILE)) {
      // Set the new version
      prop.setProperty(versionStringVarName, Double.toString(newVersion));
      // Store all properties, including the updated one
      prop.store(output, null);
    } catch (IOException io) {
      Logger.Warn("Unable to update version: " + io.getMessage());
    }
  }

  private static void createDirectory(File gamePath) throws IOException {
    if (gamePath.getParentFile() != null) {
      Files.createDirectories(gamePath.toPath());
    }
  }

  private static void downloadFile(File gamePath, String fileName, String url) throws IOException {
    URLConnection connection = new URL(url).openConnection();
    int fileSize = connection.getContentLength();
    try (BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
        FileOutputStream fileOS = new FileOutputStream(gamePath + File.separator + fileName)) {
      byte[] data = new byte[1024];
      int byteContent, totalRead = 0;
      while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
        totalRead += byteContent;
        fileOS.write(data, 0, byteContent);
        ProgressBar.setDownloadProgress("Downloading " + fileName, (float) totalRead / fileSize * 100);
      }
    }
  }

  private static void unZipUpdate(String pathToUpdateZip, String destinationPath) {
    try (ZipInputStream inZip = new ZipInputStream(new FileInputStream(pathToUpdateZip))) {
      ZipEntry inZipEntry;
      int unzipCount = 0;
      while ((inZipEntry = inZip.getNextEntry()) != null) {
        File unZippedFile = new File(destinationPath, inZipEntry.getName());
        if (inZipEntry.isDirectory()) {
          unZippedFile.mkdirs();
        } else {
          new File(unZippedFile.getParent()).mkdirs();
          try (FileOutputStream unZippedFileOutputStream = new FileOutputStream(unZippedFile)) {
            int length;
            byte[] byteBuffer = new byte[1024];
            while ((length = inZip.read(byteBuffer)) > 0) {
              unZippedFileOutputStream.write(byteBuffer, 0, length);
            }
          }
        }
        unzipCount++;
        ProgressBar.setDownloadProgress("Unzipping: " + unZippedFile.getName(), unzipCount * 5);
        inZip.closeEntry();
      }
      Logger.Info("Finished Unzipping");
    } catch (IOException e) {
      Logger.Warn("Unzipping error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static boolean isUpdateAvailable(Double currentVersion, String versionStringVarName) {
    return currentVersion < getRemoteVersionNumber(versionStringVarName);
  }
}
