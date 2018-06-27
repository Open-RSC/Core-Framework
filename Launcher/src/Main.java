import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
 
public class Main extends Application {
 
    private Scene scene;
 
    @Override
    public void start(Stage stage) {
        // create scene
        stage.setTitle("Open RSC Game Launcher");
        scene = new Scene(new Browser(),760,600, Color.web("#000"));
        stage.setScene(scene);
        // apply CSS style
        scene.getStylesheets().add("BrowserToolbar.css");
        // show stage
        stage.show();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}
 
class Browser extends Region {
 
    private HBox toolBar;
    private static String[] imageFiles = new String[]{
        "2930.png",
        "2937.png",
        "2931.png",
        "2932.png"
    };
    private static String[] captions = new String[]{
        "News",
        "Player",
        "Media",
        "Forum"
    };
    private static String[] urls = new String[]{
        "https://openrsc.com/updaternews.php",
        "https://openrsc.com/updaterplayer.php",
        "https://openrsc.com/updatermedia.php",
        "https://openrsc.com/board/index.php"
    };
    final ImageView selectedImage = new ImageView();
    final Hyperlink[] hpls = new Hyperlink[captions.length];
    final Image[] images = new Image[imageFiles.length];
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    final Button playNow = new Button("Play Open RSC Now");
    private boolean playNowButton;
 
    public Browser() {
        this.playNowButton = true;
        
        //apply the styles
        getStyleClass().add("browser");
 
        for (int i = 0; i < captions.length; i++) {
            // create hyperlinks
            Hyperlink hpl = hpls[i] = new Hyperlink(captions[i]);
            Image image = images[i] =
                    new Image(getClass().getResourceAsStream(imageFiles[i]));
            hpl.setGraphic(new ImageView(image));
            final String url = urls[i];
            final boolean addButton = (hpl.getText().equals("Play Open RSC Now"));
 
            // process event 
            hpl.setOnAction(new EventHandlerImpl(addButton, url));
        }
      
        // create the toolbar
        toolBar = new HBox();
        toolBar.setAlignment(Pos.BASELINE_LEFT);
        toolBar.getStyleClass().add("browser-toolbar");
        toolBar.getChildren().addAll(hpls);
        toolBar.getChildren().add(createSpacer()); //if enabled puts the launch game button on the right side of the window
        toolBar.getChildren().add(playNow);
 
        //set action for the button
        playNow.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                try {
                    updateClient();
                    updateCache();
                    updateLib();
                    launchGame();
                    exit();
                            } catch (IOException ex) {
                    Logger.getLogger(Browser.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(Browser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }           
        });
 
        // load the home page        
        webEngine.load("https://openrsc.com/updaternews.php");
 
        //add components
        getChildren().add(toolBar);
        getChildren().add(browser);
    }
 
    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
 
    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        double tbHeight = browser.prefHeight(w);
        layoutInArea(browser,0,40,w,760,0,HPos.CENTER, VPos.CENTER);
        layoutInArea(toolBar,0,0,w,tbHeight,0,HPos.CENTER,VPos.CENTER);	
	}
	 
    public void exit() {
            System.exit(0);
        }
	
    private static String serverVer[] = new String[15]; // Leave these as they are.
    private static String clientVer[] = new String[15];
    public static byte[] createChecksum(String s) throws Exception {
        FileInputStream fileinputstream = new FileInputStream(s);
        byte abyte0[] = new byte[1024];
        MessageDigest messagedigest = MessageDigest.getInstance("MD5");
        int i;
        do {
                i = fileinputstream.read(abyte0);
                if(i >= 0)
                {
                    messagedigest.update(abyte0, 0, i);
                }
        } 
        while(i != -1);
        fileinputstream.close();
        return messagedigest.digest();
    }
    public static String getMD5Checksum(String s) throws Exception {
        byte abyte0[] = createChecksum(s);
        String s1 = "";
        for(int i = 0; i < abyte0.length; i++)
        {
            s1 = (new StringBuilder()).append(s1).append(Integer.toString((abyte0[i] & 0xff) + 256, 16).substring(1)).toString();
        }

        return s1;
    }
    /**
     * This is our update method where it downloads a copy of the client cache archive from the web server and then checks the server's MD5 hash against it.
     */
    public static void updateClient() throws IOException {
        try {
            for(int i = 0; i < 1; i++) {
                serverVer[i] = ""; // Leave these as they are.
                clientVer[i] = "";
            }
			File baseDir = new File(System.getProperty("user.home") // user.home specifies the home directory of any OS.
			+ File.separator 
			+ "OpenRSC"); // This is the folder in your home directory it creates, saves, and extracts the cache in to.
			if (!baseDir.isDirectory()) // Checks if the directory exists or not.
			{
			baseDir.mkdir(); // Creates the directory if none exists.
			}
			if((new File(System.getProperty("user.home") // user.home specifies the home directory of any OS.
			+ File.separator 
			+ "OpenRSC" // This is the folder in your home directory it creates, saves, and extracts the cache in to.
			+ File.separator 
			+ "client.zip")).exists()) // Here is your client cache archive.
            {
                clientVer[0] = getMD5Checksum(System.getProperty("user.home") // user.home specifies the home directory of any OS.
				+ File.separator 
				+ "OpenRSC" // This is the folder in your home directory it creates, saves, and extracts the cache in to.
				+ File.separator 
				+ "client.zip"); // Here is your client cache archive.
            }
                        
            URL url = new URL("https://openrsc.com/url.txt"); // The URL on your webserver that you can quickly update instead of client cache archive file and hashes.txt file.
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            String s = bufferedreader.readLine();
            bufferedreader.close();
            URL url1 = new URL((new StringBuilder()).append(s).append("/hashes.txt").toString()); // The file that lists the MD5 hashes on your webserver.
            Properties properties = new Properties();
            InputStream inputstream = url1.openStream();
            properties.load(inputstream);
            serverVer[0] = properties.getProperty("client"); // This must match the text before the = and the MD5 hash on the hashes.txt file.
            // EX: da=63d8e501858db397ae6e0b3ff762a1e0
            //serverVer[1] = properties.getProperty("da2"); //Disabled multiple file download but worth keeping incase of future need
            String s1 = "";
            byte byte0 = 0;
            int j = 0;
            do {
                if(j >= 1) {
                    break;
                }
                switch(j) {
                case 0:
                    s1 = "client.zip"; // Here is where you want to name the cache archive that the client needs to download.
                    break;
                }
                if(!clientVer[j].equals(serverVer[j])) {
                    URL url2 = new URL((new StringBuilder()).append(s).append("/").append(s1).toString());
                    int k = url2.openConnection().getContentLength();
                    byte abyte0[] = new byte[k];
                    BufferedInputStream bufferedinputstream = new BufferedInputStream(url2.openStream());
                    s1 = (new StringBuilder()).append(System.getProperty("user.home") // user.home specifies the home directory of any OS.
                    + File.separator 
                    + "OpenRSC" // This is the folder in your home directory it creates, saves, and extracts the cache in to.
                    + File.separator).append(s1).toString();
                    int i1 = 0;
                    do {
                        if(i1 >= k) {
                            break;
                        }
                        int l = bufferedinputstream.read(abyte0, i1, abyte0.length - i1);
                        if(l == -1) {
                            break;
                        }
                        i1 += l;
                    } while(true);
                    FileOutputStream fileoutputstream = new FileOutputStream(s1);
                    fileoutputstream.write(abyte0);
                    fileoutputstream.flush();
                    fileoutputstream.close();
                    bufferedinputstream.close();
                    clientVer[j] = getMD5Checksum(s1);
                    if(!clientVer[j].equals(serverVer[j]) && byte0 < 1) {
                        j--;
                        System.out.println((new StringBuilder()).append("Hash mis-match after download. Retrying (").append(byte0).append(")...").toString());
                        System.out.println((new StringBuilder()).append(getMD5Checksum(s1)));
                        byte0++;
                    }
                    else {
                        if(byte0 >= 1) {
                            System.out.println("Error downloading file."); //You may need to verify that the client cache archive file is in the right directory.
                            j = 2;
                            break;
                        }
                        byte0 = 0;
                        System.out.println((new StringBuilder()).append(s1).append(" - Download complete.").toString()); // Appears after the .zip has been downloaded.
			zip1(); // Extracts the downloaded archive
                    }
                }
                j++;
            } while(true);
        }
        catch(Exception exception) {
            System.out.println("Error saving file. Please make sure the game client is not already open. Try again.");
            System.out.println(exception);
        }
    }
    
    public static void updateCache() throws IOException {
        try {
            for(int i = 0; i < 1; i++) {
                serverVer[i] = ""; // Leave these as they are.
                clientVer[i] = "";
            }
			File baseDir = new File(System.getProperty("user.home") // user.home specifies the home directory of any OS.
			+ File.separator 
			+ "OpenRSC"); // This is the folder in your home directory it creates, saves, and extracts the cache in to.
			if (!baseDir.isDirectory()) // Checks if the directory exists or not.
			{
			baseDir.mkdir(); // Creates the directory if none exists.
			}
			if((new File(System.getProperty("user.home") // user.home specifies the home directory of any OS.
			+ File.separator 
			+ "OpenRSC" // This is the folder in your home directory it creates, saves, and extracts the cache in to.
			+ File.separator 
			+ "cache.zip")).exists()) // Here is your client cache archive.
            {
                clientVer[0] = getMD5Checksum(System.getProperty("user.home") // user.home specifies the home directory of any OS.
				+ File.separator 
				+ "OpenRSC" // This is the folder in your home directory it creates, saves, and extracts the cache in to.
				+ File.separator 
				+ "cache.zip"); // Here is your client cache archive.
            }
                        
            URL url = new URL("https://openrsc.com/url.txt"); // The URL on your webserver that you can quickly update instead of 															 // updating the hash checker code each time the URL changes for the
																	 // client cache archive file and hashes.txt file.
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            String s = bufferedreader.readLine();
            bufferedreader.close();
            URL url1 = new URL((new StringBuilder()).append(s).append("/hashes.txt").toString()); // The file that lists the MD5 hashes on your webserver.
            Properties properties = new Properties();
            InputStream inputstream = url1.openStream();
            properties.load(inputstream);
            serverVer[0] = properties.getProperty("cache"); // This must match the text before the = and the MD5 hash on the hashes.txt file.
            String s1 = "";
            byte byte0 = 0;
            int j = 0;
            do {
                if(j >= 1) {
                    break;
                }
                switch(j) {
                case 0:
                    s1 = "cache.zip"; // Here is where you want to name the cache archive that the client needs to download.
                    break;
                }
                if(!clientVer[j].equals(serverVer[j])) {
                    URL url2 = new URL((new StringBuilder()).append(s).append("/").append(s1).toString());
                    int k = url2.openConnection().getContentLength();
                    byte abyte0[] = new byte[k];
                    BufferedInputStream bufferedinputstream = new BufferedInputStream(url2.openStream());
                    s1 = (new StringBuilder()).append(System.getProperty("user.home") // user.home specifies the home directory of any OS.
                    + File.separator 
                    + "OpenRSC" // This is the folder in your home directory it creates, saves, and extracts the cache in to.
                    + File.separator).append(s1).toString();
                    int i1 = 0;
                    do {
                        if(i1 >= k) {
                            break;
                        }
                        int l = bufferedinputstream.read(abyte0, i1, abyte0.length - i1);
                        if(l == -1) {
                            break;
                        }
                        i1 += l;
                    } while(true);
                    FileOutputStream fileoutputstream = new FileOutputStream(s1);
                    fileoutputstream.write(abyte0);
                    fileoutputstream.flush();
                    fileoutputstream.close();
                    bufferedinputstream.close();
                    clientVer[j] = getMD5Checksum(s1);
                    if(!clientVer[j].equals(serverVer[j]) && byte0 < 1) {
                        j--;
                        System.out.println((new StringBuilder()).append("Hash mis-match after download. Retrying (").append(byte0).append(")...").toString());
                        System.out.println((new StringBuilder()).append(getMD5Checksum(s1)));
                        byte0++;
                    }
                    else {
                        if(byte0 >= 1) {
                            System.out.println("Error downloading file."); //You may need to verify that the client cache archive file is in the right directory.
                            j = 2;
                            break;
                        }
                        byte0 = 0;
                        System.out.println((new StringBuilder()).append(s1).append(" - Download complete.").toString()); // Appears after the .zip has been downloaded.
			zip3(); // Extracts the downloaded archive
                    }
                }
                j++;
            } while(true);
        }
        catch(Exception exception) {
            System.out.println("Error saving file. Please make sure the game client is not already open. Try again.");
            System.out.println(exception);
        }
    }
    
    public static void updateLib() throws IOException {
        try {
            for(int i = 0; i < 1; i++) {
                serverVer[i] = ""; // Leave these as they are.
                clientVer[i] = "";
            }
			File baseDir = new File(System.getProperty("user.home") // user.home specifies the home directory of any OS.
			+ File.separator 
			+ "OpenRSC"
                        + File.separator 
			+ "lib");
			if (!baseDir.isDirectory()) // Checks if the directory exists or not.
			{
			baseDir.mkdir(); // Creates the directory if none exists.
			}
			if((new File(System.getProperty("user.home") // user.home specifies the home directory of any OS.
			+ File.separator 
			+ "OpenRSC"
			+ File.separator 
                        + "lib"
			+ File.separator 
			+ "lib.zip")).exists()) // Here is your client cache archive.
            {
                clientVer[0] = getMD5Checksum(System.getProperty("user.home") // user.home specifies the home directory of any OS.
				+ File.separator 
				+ "WK"
				+ File.separator 
                                + "lib"
				+ File.separator 
				+ "lib.zip"); // Here is your client cache archive.
            }
                        
            URL url = new URL("https://openrsc.com/url.txt"); // The URL on your webserver that you can quickly update instead of 															 // updating the hash checker code each time the URL changes for the
																	 // client cache archive file and hashes.txt file.
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            String s = bufferedreader.readLine();
            bufferedreader.close();
            URL url1 = new URL((new StringBuilder()).append(s).append("/hashes.txt").toString()); // The file that lists the MD5 hashes on your webserver.
            Properties properties = new Properties();
            InputStream inputstream = url1.openStream();
            properties.load(inputstream);
            serverVer[0] = properties.getProperty("lib"); // This must match the text before the = and the MD5 hash on the hashes.txt file.
            // EX: da=63d8e501858db397ae6e0b3ff762a1e0
            //serverVer[1] = properties.getProperty("da2"); //Disabled multiple file download but worth keeping incase of future need
            String s1 = "";
            byte byte0 = 0;
            int j = 0;
            do {
                if(j >= 1) {
                    break;
                }
                switch(j) {
                case 0:
                    s1 = "lib.zip"; // Here is where you want to name the cache archive that the client needs to download.
                    break;
                }
                if(!clientVer[j].equals(serverVer[j])) {
                    URL url2 = new URL((new StringBuilder()).append(s).append("/").append(s1).toString());
                    int k = url2.openConnection().getContentLength();
                    byte abyte0[] = new byte[k];
                    BufferedInputStream bufferedinputstream = new BufferedInputStream(url2.openStream());
                    s1 = (new StringBuilder()).append(System.getProperty("user.home") // user.home specifies the home directory of any OS.
                    + File.separator 
                    + "OpenRSC"
                    + File.separator 
                    + "lib"
                    + File.separator).append(s1).toString();
                    int i1 = 0;
                    do {
                        if(i1 >= k) {
                            break;
                        }
                        int l = bufferedinputstream.read(abyte0, i1, abyte0.length - i1);
                        if(l == -1) {
                            break;
                        }
                        i1 += l;
                    } while(true);
                    FileOutputStream fileoutputstream = new FileOutputStream(s1);
                    fileoutputstream.write(abyte0);
                    fileoutputstream.flush();
                    fileoutputstream.close();
                    bufferedinputstream.close();
                    clientVer[j] = getMD5Checksum(s1);
                    if(!clientVer[j].equals(serverVer[j]) && byte0 < 1) {
                        j--;
                        System.out.println((new StringBuilder()).append("Hash mis-match after download. Retrying (").append(byte0).append(")...").toString());
                        System.out.println((new StringBuilder()).append(getMD5Checksum(s1)));
                        byte0++;
                    }
                    else {
                        if(byte0 >= 1) {
                            System.out.println("Error downloading file."); //You may need to verify that the client cache archive file is in the right directory.
                            j = 2;
                            break;
                        }
                        byte0 = 0;
                        System.out.println((new StringBuilder()).append(s1).append(" - Download complete.").toString()); // Appears after the .zip has been downloaded.
			zip2(); // Extracts the downloaded archive
                    }
                }
                j++;
            } while(true);
        }
        catch(Exception exception) {
            System.out.println("Error saving file. Please make sure the game client is not already open. Try again.");
            System.out.println(exception);
        }
    }
    
    /**
    * This method extracts the downloaded .zip archive in to the home directory for the folder specified.
    */
    public static void zip1() throws Exception {
	String fName = System.getProperty("user.home") // user.home specifies the home directory of any OS.
	+ File.separator 
	+ "OpenRSC" // This is the folder in your home directory it creates, saves, and extracts the cache in to.
	+ File.separator 
	+ "client.zip"; // Here is your client cache archive.
	byte[] buf = new byte[1024];
	ZipInputStream zinstream = new ZipInputStream(
        new FileInputStream(fName));
	ZipEntry zentry = zinstream.getNextEntry();
	//System.out.println("Name of current Zip Entry : " + entry + "\n"); // You could have this printed out info for debugging purposes.
	System.out.println("Attempting to extract game client update...");
	while (zentry != null) {
		String entryName = zentry.getName();
		//System.out.println("Name of  Zip Entry : " + entryName); // You could have this printed out info for debugging purposes.
		FileOutputStream outstream = new FileOutputStream(System.getProperty("user.home") + File.separator + "WK" + File.separator + entryName);
		int n;
		while ((n = zinstream.read(buf, 0, 1024)) > -1) {
			outstream.write(buf, 0, n);
		}
		//System.out.println("Successfully Extracted File Name : " + entryName); // You could have this printed out info for debugging purposes.
		outstream.close();
		zinstream.closeEntry();
		zentry = zinstream.getNextEntry();
	}
	zinstream.close();
	System.out.println("Game client cache data update extracted successfully.");
    }
    
    public static void zip2() throws Exception {
	String fName = System.getProperty("user.home") // user.home specifies the home directory of any OS.
	+ File.separator 
	+ "OpenRSC" // This is the folder in your home directory it creates, saves, and extracts the cache in to.
	+ File.separator 
        + "lib" // This is the folder in your home directory it creates, saves, and extracts the cache in to.
	+ File.separator 
	+ "lib.zip"; // Here is your client cache archive.
	byte[] buf = new byte[1024];
	ZipInputStream zinstream = new ZipInputStream(
        new FileInputStream(fName));
	ZipEntry zentry = zinstream.getNextEntry();
	//System.out.println("Name of current Zip Entry : " + entry + "\n"); // You could have this printed out info for debugging purposes.
	System.out.println("Attempting to extract game client update...");
	while (zentry != null) {
		String entryName = zentry.getName();
		//System.out.println("Name of  Zip Entry : " + entryName); // You could have this printed out info for debugging purposes.
		FileOutputStream outstream = new FileOutputStream(System.getProperty("user.home") + File.separator + "WK" + File.separator + "lib" + File.separator + entryName);
		int n;
		while ((n = zinstream.read(buf, 0, 1024)) > -1) {
			outstream.write(buf, 0, n);
		}
		//System.out.println("Successfully Extracted File Name : " + entryName); // You could have this printed out info for debugging purposes.
		outstream.close();
		zinstream.closeEntry();
		zentry = zinstream.getNextEntry();
	}
	zinstream.close();
	System.out.println("Game client lib data update extracted successfully.");
    }
    
    public static void zip3() throws Exception {
	String fName = System.getProperty("user.home") // user.home specifies the home directory of any OS.
	+ File.separator 
	+ "OpenRSC" // This is the folder in your home directory it creates, saves, and extracts the cache in to.
	+ File.separator 
	+ "cache.zip"; // Here is your client cache archive.
	byte[] buf = new byte[1024];
	ZipInputStream zinstream = new ZipInputStream(
        new FileInputStream(fName));
	ZipEntry zentry = zinstream.getNextEntry();
	//System.out.println("Name of current Zip Entry : " + entry + "\n"); // You could have this printed out info for debugging purposes.
	System.out.println("Attempting to extract game client cache...");
	while (zentry != null) {
		String entryName = zentry.getName();
		//System.out.println("Name of  Zip Entry : " + entryName); // You could have this printed out info for debugging purposes.
		FileOutputStream outstream = new FileOutputStream(System.getProperty("user.home") + File.separator + "WK" + File.separator + entryName);
		int n;
		while ((n = zinstream.read(buf, 0, 1024)) > -1) {
			outstream.write(buf, 0, n);
		}
		//System.out.println("Successfully Extracted File Name : " + entryName); // You could have this printed out info for debugging purposes.
		outstream.close();
		zinstream.closeEntry();
		zentry = zinstream.getNextEntry();
	}
	zinstream.close();
	System.out.println("Game client cache data update extracted successfully.");
    }
    
    /**
    * This method launches your executable jar after it has verified the MD5 hash is up to date.
    */
    public static void launchGame() throws Exception {
	File f = new File(System.getProperty("user.home") // user.home specifies the home directory of any OS.
	+ File.separator 
	+ "OpenRSC" // This is the folder in your home directory it creates, saves, and extracts the cache in to.
	+ File.separator 
	+ "Open_RSC_Client.jar"); // Here is your executable client JAR file that it launches.
        ProcessBuilder pb = new ProcessBuilder(System.getProperty("java.home") // Your PC's Java runtime environment folder
	+ File.separator 
	+ "bin" // Self explainatory
	+ File.separator 
	+ "java", "-jar", f.getAbsolutePath() ); // Launches your executable JAR with the java -jar command.
        Process p = pb.start();
    }

    private class EventHandlerImpl implements EventHandler<ActionEvent> {

        private final boolean addButton;
        private final String url;

        public EventHandlerImpl(boolean addButton, String url) {
            this.addButton = addButton;
            this.url = url;
        }

        @Override
        public void handle(ActionEvent e) {
            playNowButton = addButton;
            webEngine.load(url);
        }
    }
}