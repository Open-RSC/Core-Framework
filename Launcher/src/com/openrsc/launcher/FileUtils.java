 package com.openrsc.launcher;
 
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.HttpURLConnection;
 import java.security.MessageDigest;
 
 public class FileUtils
 {
   public static void deleteFile(String key)
   {
     File file = new File(getCacheDir(), key);
     file.delete();
   }
   
   public static String getMD5Checksum(String filename) {
     InputStream fis = null;
     try {
       fis = new java.io.FileInputStream(new File(getCacheDir(), filename));
       byte[] buffer = new byte['Ѐ'];
       MessageDigest complete = MessageDigest.getInstance("MD5");
       int numRead;
       do {
         numRead = fis.read(buffer);
         if (numRead > 0) {
           complete.update(buffer, 0, numRead);
         }
       } while (numRead != -1);
       byte[] b = complete.digest();
       String result = "";
       for (int i = 0; i < b.length; i++) {
         result = result + Integer.toString((b[i] & 0xFF) + 256, 16).substring(1);
       }
       return result;
     } catch (Exception e) {
       e.printStackTrace();
     } finally {
       try {
         fis.close();
       } catch (IOException e) {
         e.printStackTrace();
       }
     }
     return null;
   }
   
   public static void downloadFile(String filename) { HttpURLConnection connection = null;
     try {
       connection = (HttpURLConnection)new java.net.URL(Config.CACHE_URL + filename).openConnection();
       connection.connect();
       int fileLength = connection.getContentLength();
       FileOutputStream fos = new FileOutputStream(Config.CONF_DIR + File.separator + filename);
       try {
         InputStream in = connection.getInputStream();
         byte[] buffer = new byte['Ѐ'];
         int total = 0;
         int len = 0;
         while ((len = in.read(buffer)) > 0) {
           total += len;
           if (fileLength > 0) {
             int progress = total * 100 / fileLength;
             Main.getSingleton().setDownloadProgress(filename, progress);
           }
           fos.write(buffer, 0, len);
         }
         fos.flush();
       } finally {
         fos.close();
       }
       connection.disconnect();
     } catch (Exception e) {
       e.printStackTrace();
     }
   }
   
   public static File getCacheDir() {
     return new File(Config.CONF_DIR + File.separator);
   }
 }