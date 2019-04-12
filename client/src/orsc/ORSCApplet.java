package orsc;

import com.openrsc.client.model.Sprite;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import orsc.graphics.two.Fonts;
import orsc.multiclient.ClientPort;
import orsc.util.GenUtil;

import static orsc.Config.C_LAST_ZOOM;
import static orsc.Config.S_ZOOM_VIEW_TOGGLE;

public class ORSCApplet extends Applet implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener, ComponentListener,
	ImageObserver, ImageProducer, ClientPort {
	private static final long serialVersionUID = 1L;
	public static int globalLoadingPercent = 0;
	public static String globalLoadingState = "";
	static mudclient mudclient;
	static PacketHandler packetHandler;
	private final boolean m_hb = false;
	protected int resizeWidth;
	protected int resizeHeight;
	private Font createdbyFont = new Font("Helvetica", 1, 13);
	private Font copyrightFont2 = new Font("Helvetica", 0, 12);
	private Font loadingFont = new Font("TimesRoman", 0, 15);
	private Graphics loadingGraphics;
	private Image loadingLogo;
	private String loadingState = "Loading";
	boolean m_N = false;
	String m_p = null;
	private int loadingPercent = 0;
	private int height = 384;
	private int width = 512;
	private DirectColorModel imageModel;
	private Image backingImage;
	private ImageConsumer imageProducer;

	void addMouseClick(int button, int x, int y) {
		try {
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "e.Q(" + x + ',' + "dummy" + ',' + button + ',' + y + ')');
		}
	}

	private void drawCenteredString(Font var1, String str, int y, boolean var4, int x, Graphics g) {
		try {
			FontMetrics metrics = getFontMetrics(var1);
			g.setFont(var1);
			g.drawString(str, x - metrics.stringWidth(str) / 2, y + metrics.getHeight() / 4);
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9,
				"e.LE(" + (var1 != null ? "{...}" : "null") + ',' + (str != null ? "{...}" : "null") + ',' + y + ','
					+ true + ',' + x + ',' + (g != null ? "{...}" : "null") + ')');
		}
	}

	public final boolean drawLoading(int var1) {
		try {
			Graphics var2 = this.getGraphics();
			if (var2 != null) {
				this.loadingGraphics = var2.create();
				this.loadingGraphics.translate(mudclient.screenOffsetX, mudclient.screenOffsetY);
				this.loadingGraphics.setColor(Color.black);
				this.loadingGraphics.fillRect(0, 0, this.width, this.height);
				this.drawLoadingScreen("Loading...", 0, var1 ^ 103);
				return true;
			} else return false;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.ME(" + var1 + ')');
		}
	}

	@Override
	public boolean isDisplayable() {
		return super.isDisplayable();
	}

	private void drawLoadingScreen(String state, int percent, int var3) {
		try {
			try {
				int x = (this.width - 281) / 2;
				int y = (this.height - 148) / 2;
				this.loadingGraphics.setColor(Color.black);
				this.loadingGraphics.fillRect(0, 0, this.width, this.height);
				if (!this.m_hb) this.loadingGraphics.drawImage(this.loadingLogo, x, y, this);

				x += 2;
				this.loadingPercent = percent;
				y += 90;
				this.loadingState = state;
				if (var3 <= 97) this.mouseReleased(null);

				this.loadingGraphics.setColor(new Color(132, 132, 132));
				if (this.m_hb) this.loadingGraphics.setColor(new Color(220, 0, 0));

				this.loadingGraphics.drawRect(x - 2, y - 2, 280, 23);
				this.loadingGraphics.fillRect(x, y, percent * 277 / 100, 20);
				this.loadingGraphics.setColor(new Color(198, 198, 198));
				if (this.m_hb) this.loadingGraphics.setColor(new Color(255, 255, 255));

				this.drawCenteredString(this.loadingFont, state, 10 + y, true, 138 + x, this.loadingGraphics);

				if (!this.m_hb) {
					this.drawCenteredString(this.createdbyFont, "Powered by Open RSC", 30 + y, true,
						x + 138, this.loadingGraphics);
					this.drawCenteredString(this.createdbyFont, "We support open source development.", y + 44, true, x + 138,
						this.loadingGraphics);
				} else {
					this.loadingGraphics.setColor(new Color(132, 132, 152));
					this.drawCenteredString(this.copyrightFont2, "We support open source development.", this.height - 20, true,
						138 + x, this.loadingGraphics);
				}

				if (null != this.m_p) {
					this.loadingGraphics.setColor(Color.white);
					this.drawCenteredString(this.createdbyFont, this.m_p, y - 120, true, x + 138, this.loadingGraphics);
				}
			} catch (Exception ignored) {
			}
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7,
				"e.FE(" + (state != null ? "{...}" : "null") + ',' + percent + ',' + var3 + ')');
		}
	}

	@Override
	public final synchronized void keyPressed(KeyEvent var1) {
		try {
			this.updateControlShiftState(var1);
			char keyChar = var1.getKeyChar();
			int keyCode = var1.getKeyCode();
			boolean hitInputFilter = false;
			mudclient.handleKeyPress((byte) 126, (int) keyChar);
			mudclient.lastMouseAction = 0;

			if (keyCode == 112) mudclient.interlace = !mudclient.interlace;
			if (keyCode == 113) Config.C_SIDE_MENU_OVERLAY = !Config.C_SIDE_MENU_OVERLAY;
			if (keyCode == 39) mudclient.keyRight = true;
			if (keyCode == 37) mudclient.keyLeft = true;
			if (keyCode == KeyEvent.VK_UP) mudclient.keyUp = true;
			if (keyCode == KeyEvent.VK_DOWN) mudclient.keyDown = true;
			if (keyCode == KeyEvent.VK_PAGE_DOWN) mudclient.pageDown = true;
			if (keyCode == KeyEvent.VK_PAGE_UP) mudclient.pageUp = true;

			for (int var5 = 0; var5 < Fonts.inputFilterChars.length(); ++var5)
				if (Fonts.inputFilterChars.charAt(var5) == keyChar) {
					hitInputFilter = true;
					break;
				}

			if (hitInputFilter && mudclient.inputTextCurrent.length() < 20)
				mudclient.inputTextCurrent = mudclient.inputTextCurrent + keyChar;

			if (hitInputFilter && mudclient.chatMessageInput.length() < 80)
				mudclient.chatMessageInput = mudclient.chatMessageInput + keyChar;

			// Backspace
			if (keyChar == '\b' && mudclient.inputTextCurrent.length() > 0)
				mudclient.inputTextCurrent = mudclient.inputTextCurrent.substring(0,
					mudclient.inputTextCurrent.length() - 1);

			// Backspace
			if (keyChar == '\b' && mudclient.chatMessageInput.length() > 0)
				mudclient.chatMessageInput = mudclient.chatMessageInput.substring(0,
					mudclient.chatMessageInput.length() - 1);

			if (keyChar == '\n' || keyChar == '\r') {
				mudclient.inputTextFinal = mudclient.inputTextCurrent;
				mudclient.chatMessageInputCommit = mudclient.chatMessageInput;
			}
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "e.keyPressed(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final synchronized void keyReleased(KeyEvent var1) {
		try {
			updateControlShiftState(var1);
			char c = var1.getKeyChar();
			int keyCode = var1.getKeyCode();

			if (keyCode == 39) mudclient.keyRight = false;
			if (keyCode == 37) mudclient.keyLeft = false;
			if (keyCode == KeyEvent.VK_UP) mudclient.keyUp = false;
			if (keyCode == KeyEvent.VK_DOWN) mudclient.keyDown = false;
			if (keyCode == KeyEvent.VK_PAGE_DOWN) mudclient.pageDown = false;
			if (keyCode == KeyEvent.VK_PAGE_UP) mudclient.pageUp = false;

		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "e.keyReleased(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final void keyTyped(KeyEvent var1) {
		try {
			updateControlShiftState(var1);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.keyTyped(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final void mouseClicked(MouseEvent var1) {
		try {
			updateControlShiftState(var1);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.mouseClicked(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final synchronized void mouseDragged(MouseEvent var1) {
		try {
			updateControlShiftState(var1);
			mudclient.mouseX = var1.getX() - mudclient.screenOffsetX;
			mudclient.mouseY = var1.getY() - mudclient.screenOffsetY;

			if (SwingUtilities.isRightMouseButton(var1)) mudclient.currentMouseButtonDown = 2;
			else mudclient.currentMouseButtonDown = 1;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.mouseDragged(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final void mouseEntered(MouseEvent var1) {
		try {
			updateControlShiftState(var1);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.mouseEntered(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final void mouseExited(MouseEvent var1) {
		try {
			updateControlShiftState(var1);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.mouseExited(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final synchronized void mouseMoved(MouseEvent var1) {
		try {
			updateControlShiftState(var1);
			mudclient.mouseX = var1.getX() - mudclient.screenOffsetX;
			mudclient.mouseY = var1.getY() - mudclient.screenOffsetY;
			mudclient.lastMouseAction = 0;
			mudclient.currentMouseButtonDown = 0;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.mouseMoved(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final synchronized void mousePressed(MouseEvent var1) {
		try {
			updateControlShiftState(var1);
			mudclient.mouseX = var1.getX() - mudclient.screenOffsetX;
			mudclient.mouseY = var1.getY() - mudclient.screenOffsetY;

			if (!SwingUtilities.isRightMouseButton(var1)) mudclient.currentMouseButtonDown = 1;
			else mudclient.currentMouseButtonDown = 2;

			mudclient.lastMouseButtonDown = mudclient.currentMouseButtonDown;
			mudclient.lastMouseAction = 0;
			mudclient.addMouseClick(mudclient.currentMouseButtonDown, mudclient.mouseX, mudclient.mouseY);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.mousePressed(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final synchronized void mouseReleased(MouseEvent var1) {
		try {
			updateControlShiftState(var1);
			mudclient.mouseX = var1.getX() - mudclient.screenOffsetX;
			mudclient.mouseY = var1.getY() - mudclient.screenOffsetY;
			mudclient.currentMouseButtonDown = 0;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.mouseReleased(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final synchronized void mouseWheelMoved(MouseWheelEvent e) {
		updateControlShiftState(e);
		mudclient.runScroll(e.getWheelRotation());
		if (mudclient.showUiTab == 0 && (S_ZOOM_VIEW_TOGGLE || mudclient.getLocalPlayer().isStaff())) {
			final int maxHeight = 1000;
			final int minHeight = 500;
			final int zoomIncrement = 20;
			e.consume();
			// Out
			if (e.getWheelRotation() == +1)
				if (mudclient.cameraZoom <= maxHeight) { //1 Recommended Value
					mudclient.cameraZoom += zoomIncrement; //This is how much it decrements.
					C_LAST_ZOOM = mudclient.cameraZoom / 10;
					mudclient.saveZoomDistance();
				} else return;
			// In
			if (e.getWheelRotation() == -1)
				if (mudclient.cameraZoom >= minHeight) { //1 Recommended Value
					mudclient.cameraZoom -= zoomIncrement; //This is how much it decrements.
					C_LAST_ZOOM = mudclient.cameraZoom / 10;
					mudclient.saveZoomDistance();
				}
		}
	}

	@Override
	public final void paint(Graphics var1) {
		try {
			if (mudclient != null) {
				mudclient.rendering = true;
				if (mudclient.getGameState() == 2 && this.loadingLogo != null)
					this.drawLoadingScreen(this.loadingState, this.loadingPercent, 126);
			}
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.paint(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	boolean reposition() {
		return false;
	}

	public final void showLoadingProgress(int percent, String state) {
		try {
			try {
				int x = (this.width - 281) / 2;
				x += 2;
				int y = (this.height - 148) / 2;
				this.loadingState = state;
				this.loadingPercent = percent;
				y += 90;
				int progress = percent * 277 / 100;
				this.loadingGraphics.setColor(new Color(132, 132, 132));
				if (this.m_hb) this.loadingGraphics.setColor(new Color(220, 0, 0));
				this.loadingGraphics.fillRect(x, y, progress, 20);
				this.loadingGraphics.setColor(Color.black);
				this.loadingGraphics.fillRect(progress + x, y, 277 - progress, 20);
				this.loadingGraphics.setColor(new Color(198, 198, 198));
				if (this.m_hb) this.loadingGraphics.setColor(new Color(255, 255, 255));
				this.drawCenteredString(this.loadingFont, state, 10 + y, true, 138 + x, this.loadingGraphics);
			} catch (Exception ignored) {
			}
		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8, "e.EE(" + percent + ',' + (state != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final void init() {
		try {
			mudclient = new mudclient(this);
			mudclient.packetHandler = new PacketHandler(mudclient);
			loadLogo();
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
			this.addKeyListener(this);
			this.addComponentListener(this);
			this.addMouseWheelListener(this);
		} catch (RuntimeException var2) {
			throw GenUtil.makeThrowable(var2, "client.init()");
		}
	}

	public void loadLogo() {
		// Leaving this blank
	}

	private void startApplet(int width, int height, int clientversion, int var4) {
		try {
			System.out.println("Started applet");
			this.width = width;
			this.height = height;
			mudclient.startMainThread();
		} catch (RuntimeException var12) {
			throw GenUtil.makeThrowable(var12, "e.OE(" + height + ',' + clientversion + ',' + var4 + ',' + width + ')');
		}
	}

	@Override
	public final void stop() {
		try {
			try {
				mudclient.clientBaseThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				System.exit(0);
			}
		} catch (RuntimeException var2) {
			throw GenUtil.makeThrowable(var2, "e.stop()");
		}
	}

	@Override
	public final void update(Graphics var1) {
		try {
			this.paint(var1);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.update(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	private void updateControlShiftState(InputEvent var1) {
		try {
			int mod = var1.getModifiers();
			if (mudclient == null)
				return;
			mudclient.controlPressed = (mod & Event.CTRL_MASK) != 0;
			mudclient.shiftPressed = (mod & Event.SHIFT_MASK) != 0;
		} catch (RuntimeException e) {
			throw GenUtil.makeThrowable(e, "e.SE(" + (var1 != null ? "{...}" : "null") + ',' + "dummy" + ')');
		}
	}

	public final void start() {
		try {
			if (mudclient.threadState >= 0) {
				mudclient.threadState = 0;
			}
			startApplet(512, 334 + 12, Config.CLIENT_VERSION, 12);
		} catch (RuntimeException var2) {
			throw GenUtil.makeThrowable(var2, "e.start()");
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentResized(ComponentEvent e) {
		mudclient.resizeWidth = e.getComponent().getWidth();
		mudclient.resizeHeight = e.getComponent().getHeight();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void initListeners() {
	}

	@Override
	public void crashed() {
	}

	@Override
	public void drawLoadingError() {
		Graphics g = this.getGraphics();
		if (g != null) {
			g.translate(mudclient.screenOffsetX, mudclient.screenOffsetY);
			g.setColor(Color.black);
			g.fillRect(0, 0, 512, 356);
			g.setFont(new Font("Helvetica", 1, 16));
			g.setColor(Color.yellow);
			byte var3 = 35;
			g.drawString("Sorry, an error has occured whilst loading " + Config.getServerNameWelcome(), 30, var3);
			g.setColor(Color.white);
			int var6 = var3 + 50;
			g.drawString("To fix this try the following (in order):", 30, var6);
			g.setColor(Color.white);
			var6 += 50;
			g.setFont(new Font("Helvetica", 1, 12));
			g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, var6);
			var6 += 30;
			g.drawString("2: Try clearing your web-browsers cache from tools->internet options", 30, var6);
			var6 += 30;
			g.drawString("3: Try using a different game-world", 30, var6);
			var6 += 30;
			g.drawString("4: Try rebooting your computer", 30, var6);
			var6 += 30;
			g.drawString("5: Try selecting a different version of Java from the play-game menu", 30, var6);
		}
	}

	@Override
	public void drawOutOfMemoryError() {
		Graphics g = this.getGraphics();
		if (null != g) {
			g.translate(mudclient.screenOffsetX, mudclient.screenOffsetY);
			g.setColor(Color.black);
			g.fillRect(0, 0, 512, 356);
			g.setFont(new Font("Helvetica", 1, 20));
			g.setColor(Color.white);
			g.drawString("Error - out of memory!", 50, 50);
			g.drawString("Close ALL unnecessary programs", 50, 100);
			g.drawString("and windows before loading the game", 50, 150);
			g.drawString(Config.getServerName() + " needs about 48meg of spare RAM", 50, 200);
		}
	}

	@Override
	public void drawTextBox(String line2, byte var2, String line1) {
		Graphics g = this.getGraphics();
		if (null != g) {
			g.translate(mudclient.screenOffsetX, mudclient.screenOffsetY);
			Font font = new Font("Helvetica", 1, 15);
			short width = 512;
			g.setColor(Color.black);
			short height = 344;
			g.fillRect(width / 2 - 140, height / 2 - 25, 280, 50);
			g.setColor(Color.white);
			g.drawRect(width / 2 - 140, height / 2 - 25, 280, 50);
			this.drawCenteredString(font, line1, height / 2 - 10, true, width / 2, g);
			this.drawCenteredString(font, line2, 10 + height / 2, true, width / 2, g);
		}
	}

	@Override
	public void initGraphics() {
		int width = mudclient.getSurface().width2;
		int height = mudclient.getSurface().height2;
		if (width > 1 && height > 1) {
			this.imageModel = new DirectColorModel(32, 16711680, '\uff00', 255);
			this.backingImage = createImage(this);
			this.commitToImage(true);
			prepareImage(this.backingImage, this);
			this.commitToImage(true);
			prepareImage(this.backingImage, this);
			this.commitToImage(true);
			prepareImage(this.backingImage, this);
		}
	}

	private synchronized void commitToImage(boolean var1) {
		try {
			if (null != this.imageProducer) {
				this.imageProducer.setPixels(0, 0, mudclient.getSurface().width2, mudclient.getSurface().height2,
					this.imageModel, mudclient.getSurface().pixelData, 0, mudclient.getSurface().width2);
				this.imageProducer.imageComplete(2);
			}
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ua.CA(" + true + ')');
		}
	}

	@Override
	public void addConsumer(ImageConsumer arg0) {
		try {
			this.imageProducer = arg0;
			arg0.setDimensions(mudclient.getSurface().width2, mudclient.getSurface().height2);
			arg0.setProperties(null);
			arg0.setColorModel(this.imageModel);
			arg0.setHints(14);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ua.addConsumer(" + (arg0 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public boolean isConsumer(ImageConsumer arg0) {
		return this.imageProducer == arg0;
	}

	@Override
	public void removeConsumer(ImageConsumer arg0) {
		if (this.imageProducer == arg0) this.imageProducer = null;
	}

	@Override
	public void requestTopDownLeftRightResend(ImageConsumer arg0) {
		try {
			System.out.println("TDLR");
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3,
				"ua.requestTopDownLeftRightResend(" + (arg0 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public void startProduction(ImageConsumer arg0) {
		this.addConsumer(arg0);
	}

	public final void draw(Graphics g, int x, int var3, int y) {
		this.commitToImage(true);
		g.drawImage(this.backingImage, x, y, this);
	}

	@Override
	public void draw() {
		draw(getGraphics(), mudclient.screenOffsetX, 256, mudclient.screenOffsetY);
	}

	@Override
	public void close() {
		stop();
	}

	@Override
	public String getCacheLocation() {
		return "../OpenRSC/";
	}

	@Override
	public void resized() {
		imageProducer.setDimensions(mudclient.getSurface().width2, mudclient.getSurface().height2);
		initGraphics();
	}

	@Override
	public Sprite getSpriteFromByteArray(ByteArrayInputStream byteArrayInputStream) {
		try {
			BufferedImage image = ImageIO.read(byteArrayInputStream);
			int captchaWidth = image.getWidth();
			int captchaHeight = image.getHeight();

			int[] pixels = new int[image.getWidth() * image.getHeight()];
			for (int y = 0; y < image.getHeight(); y++)
				for (int x = 0; x < image.getWidth(); x++) {
					int rgb = image.getRGB(x, y);
					pixels[x + y * image.getWidth()] = rgb;
				}

			Sprite sprite = new Sprite(pixels, captchaWidth, captchaHeight);
			sprite.setSomething(captchaWidth, captchaHeight);
			sprite.setShift(0, 0);
			sprite.setRequiresShift(false);
			return sprite;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void drawKeyboard() {
	}

	@Override
	public void playSound(byte[] soundData, int offset, int dataLength) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void stopSoundPlayer() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
