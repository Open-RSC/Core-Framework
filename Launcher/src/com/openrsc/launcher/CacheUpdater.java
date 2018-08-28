package com.openrsc.launcher;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class CacheUpdater
{
  private Properties localChecksumTable;
  private Properties remoteChecksumTable;
  
  public CacheUpdater()
  {
     this.localChecksumTable = new Properties();
     this.remoteChecksumTable = new Properties();
  }
  
  public void update() {
     File file = new File(Config.CONF_DIR + File.separator);
     if (!file.exists()) {
       file.mkdir();
    }
     loadRemoteChecksumTable();
     generateLocalChecksumTable();
     updateCacheFiles();
     verifyCacheFiles();
  }
  
  private void loadRemoteChecksumTable() {
    try {
       Main.getSingleton().println("Fetching remote checksum table...");
       FileUtils.downloadFile("MD5CHECKSUM");
       java.io.FileInputStream f2 = new java.io.FileInputStream(Config.CONF_DIR + File.separator + "MD5CHECKSUM");
       this.remoteChecksumTable.load(f2);
       f2.close();
    } catch (Exception e) {
       publishProgress("Unable to fetch remote checksum table: " + e.getMessage());
       e.printStackTrace();
    }
  }
  
  private void generateLocalChecksumTable() {
     Main.getSingleton().println("Generating local checksum table...");
     for (Map.Entry<Object, Object> entry : this.remoteChecksumTable.entrySet()) {
       if (!new File(FileUtils.getCacheDir(), (String)entry.getKey()).exists()) {
         this.localChecksumTable.put(entry.getKey(), "doesntexist");
         publishProgress(entry.getKey() + " does not exist locally");
      } else {
         String localFileChecksum = FileUtils.getMD5Checksum((String)entry.getKey());
         this.localChecksumTable.put((String)entry.getKey(), localFileChecksum);
         publishProgress(entry.getKey() + " > " + localFileChecksum);
      }
    }
  }
  
  private void updateCacheFiles() {
     publishProgress("Checking game-cache files");
    try
    {
       for (Map.Entry<Object, Object> e : this.localChecksumTable.entrySet()) {
         Iterator<Map.Entry<Object, Object>> itr = this.remoteChecksumTable.entrySet().iterator();
         while (itr.hasNext()) {
           Map.Entry<Object, Object> e1 = (Map.Entry)itr.next();
           String localChecksum = (String)e.getValue();
           String serverChecksum = (String)e1.getValue();
          
           if ((e1.getKey().equals(e.getKey())) && (!localChecksum.equalsIgnoreCase(serverChecksum))) {
             FileUtils.deleteFile((String)e1.getKey());
             FileUtils.downloadFile((String)e1.getKey());
             publishProgress("Updating " + e.getKey() + ", (" + localChecksum + " != " + serverChecksum + ") ...\n");
          }
        }
      }
    } catch (Exception e) {
       publishProgress("Unable to update cache files: " + e.getMessage());
       e.printStackTrace();
    }
  }
  
  private void verifyCacheFiles()
  {
     publishProgress("Verifying game-cache files");
    try {
       for (Map.Entry<Object, Object> entrySet : this.remoteChecksumTable.entrySet()) {
         String filename = (String)entrySet.getKey();
         String hash = (String)entrySet.getValue();
         boolean verified = false;
        
         while (!verified) {
           verified = verifyFile(filename, hash);
           if (!verified) {
             publishProgress("Re-downloading " + filename);
             FileUtils.deleteFile(filename);
             FileUtils.downloadFile(filename);
          }
        }
      }
    } catch (Exception e) {
       publishProgress("Unable to verify data files > " + e.getMessage());
       e.printStackTrace();
    }
  }
  
  boolean verifyFile(String filename, String checksum) {
    try {
       String downloadedChecksum = FileUtils.getMD5Checksum(filename);
      
       if (downloadedChecksum.equalsIgnoreCase(checksum)) {
         publishProgress("Verified: " + filename + "(" + downloadedChecksum + " = " + checksum + ") -> OK");
        
         return true;
      }
      
       publishProgress("Verification failed, re-downloading " + filename + "...");
       publishProgress("Downloaded file: " + filename + " hash:" + downloadedChecksum + " doesn't match official MD5: " + checksum + " re-downloading");
      
       return false;
    } catch (Exception e) {
       e.printStackTrace();
       publishProgress("Verification Error " + e.getCause().getMessage());
    }
     return false;
  }
  
  private void publishProgress(String s) {
     Main.getSingleton().println(s);
     System.out.println(s);
  }
}