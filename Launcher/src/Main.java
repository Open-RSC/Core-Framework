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
import static javafx.application.Application.launch;
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
    private String GAME_NAME = "Open RSC";
 
    @Override
    public void start(Stage stage) {
        stage.setTitle(GAME_NAME + " Game Launcher");
        scene = new Scene(new Browser(),838,600, Color.web("#000"));
        stage.setScene(scene);
        scene.getStylesheets().add("BrowserToolbar.css");
        stage.show();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}
 
class Browser extends Region {

    private String GAME_NAME = "Open RSC";
    private static String URL = "https://www.openrsc.com";
    private static String CLIENT_FILENAME = "client.zip";
    private static String CACHE_FILENAME = "cache.zip";
    private static String CLIENT_JAR_FILENAME = "Open_RSC_Client.jar";
    private static String CACHE_FOLDER = "OpenRSC";
 
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
        URL + "/updaternews.php",
        URL + "/updaterplayer.php",
        URL + "/updatermedia.php",
        URL + "/board/index.php"
    };
    final ImageView selectedImage = new ImageView();
    final Hyperlink[] hpls = new Hyperlink[captions.length];
    final Image[] images = new Image[imageFiles.length];
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    final Button playNow = new Button("Play " + GAME_NAME);
    private boolean playNowButton;
 
    public Browser() {
        this.playNowButton = true;
        getStyleClass().add("browser");
 
        for (int i = 0; i < captions.length; i++) {
            Hyperlink hpl = hpls[i] = new Hyperlink(captions[i]);
            Image image = images[i] =
                new Image(getClass().getResourceAsStream(imageFiles[i]));
            hpl.setGraphic(new ImageView(image));
            final String url = urls[i];
            final boolean addButton = (hpl.getText().equals("Play " + GAME_NAME + " Now"));
            hpl.setOnAction(new EventHandlerImpl(addButton, url));
        }

        toolBar = new HBox();
        toolBar.setAlignment(Pos.BASELINE_LEFT);
        toolBar.getStyleClass().add("browser-toolbar");
        toolBar.getChildren().addAll(hpls);
        toolBar.getChildren().add(createSpacer()); //if enabled puts the launch game button on the right side of the window
        toolBar.getChildren().add(playNow);
        playNow.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                try {
                    updateClient();
                    updateCache();
                    launchGame();
                    exit();
                            } catch (IOException ex) {
                    Logger.getLogger(Browser.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(Browser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }           
        });
        
        webEngine.load(URL + "/updaternews.php");
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
        layoutInArea(browser,0,40,w,720,0,HPos.CENTER, VPos.CENTER);
        layoutInArea(toolBar,0,0,w,tbHeight,0,HPos.CENTER,VPos.CENTER);	
	}
	 
    public void exit() {
            System.exit(0);
        }
	
    private static String serverVer[] = new String[15];
    private static String clientVer[] = new String[15];
    public static byte[] createChecksum(String s) throws Exception {
        FileInputStream fileinputstream = new FileInputStream(s);
        byte abyte0[] = new byte[1024];
        MessageDigest messagedigest = MessageDigest.getInstance("MD5");
        int i;
        do {
                i = fileinputstream.read(abyte0);
                if(i >= 0) {
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
        for(int i = 0; i < abyte0.length; i++) {
            s1 = (new StringBuilder()).append(s1).append(Integer.toString((abyte0[i] & 0xff) + 256, 16).substring(1)).toString();
        }

        return s1;
    }
    /**
     * This is the update method where it downloads a copy of the client cache archive from the web server and then checks the server's MD5 hash against it.
     */
    public static void updateClient() throws IOException {
        try {
            for(int i = 0; i < 1; i++) {
                serverVer[i] = "";
                clientVer[i] = "";
            }
			File baseDir = new File(System.getProperty("user.home") + File.separator + CACHE_FOLDER);
			if (!baseDir.isDirectory()) {
			baseDir.mkdir();
			}
			if((new File(System.getProperty("user.home") + File.separator + CACHE_FOLDER + File.separator + CLIENT_FILENAME)).exists())
            {
                clientVer[0] = getMD5Checksum(System.getProperty("user.home") + File.separator + CACHE_FOLDER + File.separator + CLIENT_FILENAME);
            }
                        
            URL url = new URL(URL + "/url.txt");
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            String s = bufferedreader.readLine();
            bufferedreader.close();
            URL url1 = new URL((new StringBuilder()).append(s).append("/hashes.txt").toString());
            Properties properties = new Properties();
            InputStream inputstream = url1.openStream();
            properties.load(inputstream);
            serverVer[0] = properties.getProperty("client");
            String s1 = "";
            byte byte0 = 0;
            int j = 0;
            do {
                if(j >= 1) {
                    break;
                }
                switch(j) {
                case 0:
                    s1 = CLIENT_FILENAME;
                    break;
                }
                if(!clientVer[j].equals(serverVer[j])) {
                    URL url2 = new URL((new StringBuilder()).append(s).append("/").append(s1).toString());
                    int k = url2.openConnection().getContentLength();
                    byte abyte0[] = new byte[k];
                    BufferedInputStream bufferedinputstream = new BufferedInputStream(url2.openStream());
                    s1 = (new StringBuilder()).append(System.getProperty("user.home") + File.separator + CACHE_FOLDER + File.separator).append(s1).toString();
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
                            System.out.println("Error downloading file.");
                            j = 2;
                            break;
                        }
                        byte0 = 0;
                        System.out.println((new StringBuilder()).append(s1).append(" - Download complete.").toString());
			unzip_client();
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
                serverVer[i] = "";
                clientVer[i] = "";
            }
			File data = new File(System.getProperty("user.home") + File.separator + CACHE_FOLDER + File.separator + "data");
			if (!data.isDirectory()) {
			data.mkdir();
			}
                        File media = new File(System.getProperty("user.home") + File.separator + CACHE_FOLDER + File.separator + "media");
			if (!media.isDirectory()) {
			media.mkdir();
			}
			if((new File(System.getProperty("user.home") + File.separator + CACHE_FOLDER + File.separator + CACHE_FILENAME)).exists())
            {
                clientVer[0] = getMD5Checksum(System.getProperty("user.home") + File.separator + CACHE_FOLDER + File.separator + CACHE_FILENAME);
            }
                        
            URL url = new URL(URL + "/url.txt");
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            String s = bufferedreader.readLine();
            bufferedreader.close();
            URL url1 = new URL((new StringBuilder()).append(s).append("/hashes.txt").toString());
            Properties properties = new Properties();
            InputStream inputstream = url1.openStream();
            properties.load(inputstream);
            serverVer[0] = properties.getProperty("cache");
            String s1 = "";
            byte byte0 = 0;
            int j = 0;
            do {
                if(j >= 1) {
                    break;
                }
                switch(j) {
                case 0:
                    s1 = CACHE_FILENAME;
                    break;
                }
                if(!clientVer[j].equals(serverVer[j])) {
                    URL url2 = new URL((new StringBuilder()).append(s).append("/").append(s1).toString());
                    int k = url2.openConnection().getContentLength();
                    byte abyte0[] = new byte[k];
                    BufferedInputStream bufferedinputstream = new BufferedInputStream(url2.openStream());
                    s1 = (new StringBuilder()).append(System.getProperty("user.home") + File.separator + CACHE_FOLDER + File.separator).append(s1).toString();
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
                            System.out.println("Error downloading file.");
                            j = 2;
                            break;
                        }
                        byte0 = 0;
                        System.out.println((new StringBuilder()).append(s1).append(" - Download complete.").toString());
			unzip_cache();
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
    public static void unzip_client() throws Exception {
	String fName = System.getProperty("user.home") + File.separator + CACHE_FOLDER + File.separator + CLIENT_FILENAME;
	byte[] buf = new byte[1024];
	ZipInputStream zinstream = new ZipInputStream(
        new FileInputStream(fName));
	ZipEntry zentry = zinstream.getNextEntry();
	System.out.println("Attempting to extract game client update...");
	while (zentry != null) {
		String entryName = zentry.getName();
		FileOutputStream outstream = new FileOutputStream(System.getProperty("user.home") + File.separator + CACHE_FOLDER + File.separator + entryName);
		int n;
		while ((n = zinstream.read(buf, 0, 1024)) > -1) {
			outstream.write(buf, 0, n);
		}
		outstream.close();
		zinstream.closeEntry();
		zentry = zinstream.getNextEntry();
	}
	zinstream.close();
	System.out.println("Game client extract finished.");
    }
    
    public static void unzip_cache() throws Exception {
	String fName = System.getProperty("user.home") + File.separator + CACHE_FOLDER + File.separator + CACHE_FILENAME;
	byte[] buf = new byte[1024];
	ZipInputStream zinstream = new ZipInputStream(
        new FileInputStream(fName));
	ZipEntry zentry = zinstream.getNextEntry();
	System.out.println("Attempting to extract game client cache...");
	while (zentry != null) {
		String entryName = zentry.getName();
		FileOutputStream outstream = new FileOutputStream(System.getProperty("user.home") + File.separator + CACHE_FOLDER + File.separator + entryName);
		int n;
		while ((n = zinstream.read(buf, 0, 1024)) > -1) {
			outstream.write(buf, 0, n);
		}
		outstream.close();
		zinstream.closeEntry();
		zentry = zinstream.getNextEntry();
	}
	zinstream.close();
	System.out.println("Game cache update extract finished.");
    }
    
    /**
    * This method launches your executable jar after it has verified the MD5 hash is up to date.
    */
    public static void launchGame() throws Exception {
	File f = new File(System.getProperty("user.home") + File.separator + CACHE_FOLDER + File.separator + CLIENT_JAR_FILENAME);
        ProcessBuilder pb = new ProcessBuilder(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java", "-jar", f.getAbsolutePath() );
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