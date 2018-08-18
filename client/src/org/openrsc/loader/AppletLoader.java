package org.openrsc.loader;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.openrsc.client.Config;
import org.openrsc.client.ImplementationDelegate;
import org.openrsc.client.loader.various.AppletUtils;
import org.openrsc.client.loading;
import org.openrsc.client.mudclient;
import org.openrsc.loader.util.unzip;

public class AppletLoader
	extends
		Applet
	implements
		ImplementationDelegate,
		Runnable
{
	private static final long serialVersionUID = 1L;

	private final static String FILE_SEPARATOR = System.getProperty("file.separator");

	private MediaTracker mediaTracker;

	private Image loaderImage;

	private String md5CheckSum;

	public int width = -1;

	public int height = -1;

	String[] dataMirrors;

	public String cache;

	private loading<AppletLoader> instance;

	private boolean canStart = true;
	public AppletLoader() {
	}

	protected void createDir() {
		File file = new File(AppletUtils.CACHE.getPath());
		if (file.exists())
			return;
		else {
			file.mkdir();
			return;
		}
	}

	// Entry point for the openrsc web applet
	public void init() {
		width = Integer.parseInt(getParameter("width"));
		height = (int) (width * 0.73046875 - 40);
		/// Set ip and port if directed to...
		if(getParameter("ip") != null && getParameter("port") != null)
		{
			Config.IP = getParameter("ip");
			Config.PORT = Integer.parseInt(getParameter("port"));
		}
		loadCacheAndMirrors();
		all = true;
		img = false;
		new Thread(this).start();
	}

	public boolean started = false;

	public void run() {
		try {
			started = true;
			createDir();
			loaderImage = Toolkit
					.getDefaultToolkit()
					.getImage(
							new URL(
									"https://" + cache + "/updated.png"));
			mediaTracker = new MediaTracker(this);
			mediaTracker.addImage(loaderImage, 0, 100, 100);
			if (AppletUtils.CACHEFILE.exists()) {
				md5CheckSum = getMD5Checksum(AppletUtils.CACHEFILE);
				if (!getServerResponse().equals(md5CheckSum))
					downloadAndExtract();
			}
			if (!AppletUtils.CACHEFILE.exists()) {
				downloadAndExtract();
			}
			if(canStart) {
				showPercent("Loading game...", 100, 100);
				this.instance = new mudclient<AppletLoader>(this, width, height);
				addMouseWheelListener(this);
				addMouseMotionListener(this);
				addMouseListener(this);
				addKeyListener(this);
				this.instance.run();
			} else {
				showPercent("Cache can not be downloaded..", 100, 100);
			}
		} catch (Exception e) {
		}
	}

	private void unpack() {
		new unzip(AppletUtils.CACHEFILE.toString());
		fileDownloaded = false;
	}

	private String getServerResponse() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new URL(
				"https://"+cache+"/update.php?md5=" + md5CheckSum)
				.openStream()));
		String response = br.readLine();
		br.close();
		return response;
	}

	private void downloadAndExtract() {
		download();
		if(fileDownloaded)
			unpack();
	}

	private boolean img;
	private boolean all;

	@Override
	public void paint(Graphics gfx)
	{

	}

	public final void update(Graphics g) {
		paint(g);
	}

	private final void showPercent(String s, int i, int j) {
		Graphics g = getGraphics();
		Font font = new Font("Helvetica", 1, 13);
		FontMetrics fontmetrics = getFontMetrics(font);
		Font font1 = new Font("Helvetica", 0, 13);
		FontMetrics fontmetrics1 = getFontMetrics(font1);
		Font font2 = new Font("TimesRoman", 0, 15);
		FontMetrics fontmetrics2 = getFontMetrics(font2);
		if (all || !img && mediaTracker.checkAll(true)) {
			if (!img || mediaTracker.checkAll(true)) {
				g.drawImage(loaderImage, (width / 2) - 256, (height / 2) - 167, this);
				img = true;
			} else {
				g.setColor(Color.black);
				g.fillRect(0, 0, width, height);
			}
			g.setColor(new Color(198, 198, 198));
			g.setFont(font);
			String s1 = "RuneScape has been updated!";
			g
					.drawString("RuneScape has been updated!", (width / 2)
							- fontmetrics.stringWidth(s1) / 2,
							(height / 2) - 167 + 125);
			s1 = "Please wait - Fetching new files...";
			g.drawString(s1, (width / 2) - fontmetrics.stringWidth(s1) / 2,
					(height / 2) - 167 + 140);
			g.setFont(font1);
			s1 = "This may take a few minutes, but only";
			g.drawString(s1, (width / 2) - fontmetrics1.stringWidth(s1) / 2,
					(height / 2) - 167 + 165);
			s1 = "needs to be done when the game is updated.";
			g.drawString(s1, (width / 2) - fontmetrics1.stringWidth(s1) / 2,
					(height / 2) - 167 + 180);
		}
		Color color = new Color(132, 132, 132);
		g.setColor(color);
		g.drawRect((width / 2) - 304 / 2, (height / 2) - 167 + 190, 303, 23);
		g.fillRect((width / 2) - 300 / 2, (height / 2) - 167 + 192, j * 3, 20);
		g.setColor(Color.black);
		g.fillRect((width / 2) - 300 / 2 + j * 3, (height / 2) - 167 + 192,
				300 - j * 3, 20);
		String s2 = s + " - " + i + "%";
		g.setFont(font2);
		g.setColor(new Color(198, 198, 198));
		g.drawString(s2, (width / 2) - fontmetrics2.stringWidth(s2) / 2,
				(height / 2) - 167 + 207);
	}

	private boolean fileDownloaded = false;

	private void download() {
		File file = new File(AppletUtils.CACHEFILE.getPath());
		if (file.exists()) {
			file.delete();
		}
		for (String s : dataMirrors) {
			try {
				URL url = new URL(s);
				HttpURLConnection huc = (HttpURLConnection) url
						.openConnection();
				huc.setDefaultUseCaches(false);
				if (huc.getResponseCode() != 200) {
				} else {
					InputStream is = huc.getInputStream();
					file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					int contentLength = huc.getContentLength();
					int size = 0;
					int copy;
					byte[] buffer = new byte[4096];
					while ((copy = is.read(buffer)) != -1) {
						fos.write(buffer, 0, copy);
						size += copy;
						int percentage = (int) (((double) size / (double) contentLength) * 100D);
						showPercent("Downloading data...", percentage,
								percentage);
					}
					is.close();
					fos.close();
					fileDownloaded = true;
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e);
			}
		}
		if(!fileDownloaded) {
			canStart = false;
		}
	}

	public Image createImage(int i, int j) {
		return super.createImage(i, j);
	}

	public String getMD5Checksum(java.io.File filename) {
		String md5Checksum = "";
		try {
			java.io.InputStream inputStream = new java.io.FileInputStream(
					filename);
			byte[] inputStreamBuffer = new byte[1024];
			java.security.MessageDigest messageDigest = null;
			try {
				messageDigest = java.security.MessageDigest.getInstance("MD5");
			} catch (java.security.NoSuchAlgorithmException exception) {
			}
			int cluster;
			do {
				cluster = inputStream.read(inputStreamBuffer);
				if (cluster > 0)
					messageDigest.update(inputStreamBuffer, 0, cluster);
			} while (cluster != -1);
			inputStream.close();
			byte[] md5Bytes = messageDigest.digest();
			for (int index = 0; index < md5Bytes.length; index++)
				md5Checksum += Integer.toString(
						(md5Bytes[index] & 0xff) + 0x100, 16).substring(1);
		} catch (java.io.IOException exception) {
			exception.printStackTrace();
		}
		return md5Checksum;
	}

	public final synchronized void destroy() {
		if (started && instance != null)
			instance.destroy();
	}


	public void loadCacheAndMirrors() {
		try {
			final BufferedReader in = new BufferedReader(new InputStreamReader(
					new URL("https://"+cache+"/update.php?md5=hackaround")
							.openStream()));
			String s = null;
			final java.util.List<String> mirrors = new ArrayList<String>();
			while ((s = in.readLine()) != null) {
				s = s.trim();
				if (s.startsWith("http")) {
					mirrors.add(s);
				}
			}
			dataMirrors = mirrors.toArray(new String[mirrors.size()]);
		} catch (final Exception e) {
			System.err.println("Unable to read data mirrors");
			e.printStackTrace();
		}
	}



	/*************************************************************/
	/** Forward events from the applet to the openrsc game  */
	/**                                                          */
	/**  Note: Events are only forwarded once the game is loaded */
	/**                                                          */
	/*************************************************************/

	@Override
	public final void keyPressed(KeyEvent e)
	{
		instance.keyDown(e.isShiftDown(), e.isControlDown(), e.isActionKey(), e.getKeyCode(), e.getKeyChar(), e);
	}

	@Override
	public final void keyReleased(KeyEvent e)
	{
		instance.keyUp(e.getKeyCode());
	}

	@Override
	public final void keyTyped(KeyEvent e) { /* Intentionally Empty */ }

	@Override
	public final void mouseMoved(MouseEvent e)
	{
		instance.mouseMove(e.getX(), e.getY());

	}

	@Override
	public final void mouseDragged(MouseEvent e)
	{
		instance.mouseDrag(e.isMetaDown(), e.getX(), e.getY());
	}

	@Override
	public final void mouseExited(MouseEvent e)
	{
		mouseMoved(e);
	}

	@Override
	public final void mouseEntered(MouseEvent e)
	{
		mouseMoved(e);
	}

	@Override
	public final void mousePressed(MouseEvent e)
	{
		instance.mouseDown(e.isMetaDown(), e.getX(), e.getY());
	}

	@Override
	public final void mouseReleased(MouseEvent e)
	{
		instance.mouseUp(e.getButton(), e.getX(), e.getY());
	}

	@Override
	public final void mouseClicked(MouseEvent e) { /* Intentionally Empty */ }

	@Override
	public final void mouseWheelMoved(MouseWheelEvent e)
	{
		instance.mouseWheelMoved(e.getWheelRotation());
	}

	@Override
	public final Container getContainerImpl()
	{
		return this;
	}

	@Override
	public void onLogin() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLogout() {
		// TODO Auto-generated method stub

	}
}
