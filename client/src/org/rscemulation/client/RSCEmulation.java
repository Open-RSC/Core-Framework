package org.rscemulation.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.rscemulation.client.util.DataConversions;

public abstract class RSCEmulation<Delegate_T extends ImplementationDelegate>
	implements
		Runnable
{

	private enum LoadingPhase
	{
		NOT_LOADED,
		LOADING,
		LOADED
	}
	
	private static String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
	private final static Color LOADING_BAR_COLOR = new Color(132, 132, 132);
	private final static Font TIMES_NEW_ROMAN = new Font("TimesRoman", 0, 15);
	private final static Font HELVETICA = new Font("Helvetica", 1, 13);
	
	private final Map<Integer, Boolean> keys = new HashMap<Integer, Boolean>();
	
	protected final Delegate_T delegate;
	
	private volatile LoadingPhase loadingPhase = LoadingPhase.NOT_LOADED;
	
	private final Image loadingImage;
	
	private final long[] currentTimeArray = new long[10];
	
	private int exitTimeout;
	
	public abstract boolean isSubscriber();
	
	protected boolean f1;
	protected boolean controlDown;
	protected boolean leftArrowKeyDown;
	protected boolean rightArrowKeyDown;
	protected boolean upArrowKeyDown;
	protected boolean downArrowKeyDown;
	public int mouseX;
	public int mouseY;
	protected int mouseDownButton;
	protected int lastMouseDownButton;
	public String inputText = "";
	public String enteredText = "";
	public String inputMessage = "";
	public String enteredMessage = "";

	/**
	 * Is the provided key allowed?
	 * 
	 * @param key the key to check
	 * 
	 * @return true if the key is allowed, false if not
	 * 
	 */
	protected static boolean isKeyValid(int key)
	{
		boolean validKeyDown = false;
		for (int j = 0; j < charSet.length(); j++)
		{
			if (key != charSet.charAt(j))
			{
				continue;
			}
			validKeyDown = true;
			break;
		}
		return validKeyDown;
	}
	
	/**
	 * Constructs an RSCEmulation game instance
	 * 
	 * @param container the <code>ImplementationDelegate</code> to use
	 * 
	 */
	public RSCEmulation(Delegate_T container)
	{
		this.delegate = container;
		Image img = null;
		try {
			img = ImageIO.read(Resources.load("/data/logo"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		this.loadingImage = img;

		/// Wait for the loading image to finish, otherwise it might not display!
		MediaTracker tracker = new MediaTracker(this.delegate.getContainerImpl());
		tracker.addImage(loadingImage, 0);

		/// Don't swallow an interruption!
		boolean interrupted = false;
		while(!tracker.isErrorAny() && !tracker.checkID(0))
		{
			try
			{
				tracker.waitForID(0);
			}
			catch(InterruptedException e)
			{
				interrupted = true;
			}
		}
		if(interrupted)
		{
			Thread.currentThread().interrupt();
		}
	}



	/**
	 * Simply returns the <code>Graphics</code> associated with the 
	 * underlying <code>ImplementationDelegate</code>
	 * 
	 * @return a <code>Graphics</code> object
	 * 
	 */
	protected final Graphics getGraphics()
	{
		return delegate.getContainerImpl().getGraphics();
	}

	
	/**
	 * Updates the loading screen with a new percentage and load stage.  This 
	 * method may only be called when the game is loading.  Calling this 
	 * method at any other time will result in an 
	 * <code>IllegalStateException</code> being thrown.
	 * 
	 * @param percentLoaded the new percent (between 0 and 100)
	 * 
	 * @param currentItem this will be displayed on top of the loading bar, 
	 * and may be null
	 * 
	 * @throws IllegalStateException if the game is not currently loading.
	 * 
	 * @throws IllegalArgumentException if the provided percentage is not 
	 * between 0 and 100 inclusive.
	 * 
	 */
	protected final void updateLoadingProgress(int percentLoaded, String loadStage)
	{
		if(loadingPhase != LoadingPhase.LOADING)
		{
			throw new IllegalStateException("state-check: \"phase == loading\" + failed");
		}
		if(percentLoaded < 0 || percentLoaded > 100)
		{
			throw new IllegalArgumentException("range-check: \"100 >= percent >= 0\" failed");
		}
		Graphics gfx = delegate.getContainerImpl().getGraphics();
		int j = (delegate.getContainerImpl().getWidth() - 281) / 2;
		int k = (delegate.getContainerImpl().getHeight() - 148) / 2;
		j += 2;
		k += 90;
		int l = (277 * percentLoaded) / 100;
		gfx.setColor(LOADING_BAR_COLOR);
		gfx.fillRect(j, k, l, 20);
		gfx.setColor(Color.black);
		gfx.fillRect(j + l, k, 277 - l, 20);
		gfx.setColor(new Color(198, 198, 198));
		/// optimization short-cut if length == 0
		if(loadStage != null && loadStage.length() > 0)
		{
			drawString(gfx, loadStage, TIMES_NEW_ROMAN, j + 138, k + 10);
		}
	}

	public final synchronized void mouseWheelMoved(int direction)
	{
		onMouseWheelMoved(direction);
	}
	
	/**
	 * Invoked by the delegate when a mouse button is pressed down
	 * 
	 * @param isMetaDown is the meta-modifier down?
	 * 
	 * @param x the x coordinate of the mouse
	 * 
	 * @param y the y coordinate of the mouse
	 * 
	 */
	public final synchronized void mouseDown(boolean isMetaDown, int x, int y)
	{
		mouseX = x;
		mouseY = y;
		mouseDownButton = isMetaDown ? 2 : 1;
		lastMouseDownButton = mouseDownButton;
		handleMouseDown(mouseDownButton, x, y);
	}
	
	/**
	 * Invoked by the delegate when a mouse button is released
	 * 
	 * @param button the mouse button that was released
	 * 
	 * @param x the x coordinate of the mouse
	 * 
	 * @param y the y coordinate of the mouse
	 * 
	 */
	public final synchronized void mouseUp(int button, int x, int y) {
		mouseX = x;
		mouseY = y;
		mouseDownButton = 0;
		handleMouseUp(button, x, y);
	}
	
	/**
	 * Invoked by the delegate when the mouse is dragged
	 * 
	 * @param isMetaDown is the meta-modifier down?
	 * 
	 * @param x the new x coordinate of the mouse
	 * 
	 * @param y the new y coordinate of the mouse
	 * 
	 */
	public final synchronized void mouseDrag(boolean isMetaDown, int x, int y)
	{
		this.onMouseDragged(mouseX - x, mouseY - y);
		mouseX = x;
		mouseY = y;
		mouseDownButton = isMetaDown ? 2 : 1;
		this.onMouseMove(x, y);
	}
	
	/**
	 * Invoked by the delegate when the mouse is moved
	 * 
	 * @param x the new x coordinate of the mouse
	 * 
	 * @param y the new y coordinate of the mouse
	 * 
	 */
	public final synchronized void mouseMove(int x, int y)
	{
		mouseX = x;
		mouseY = y;
		mouseDownButton = 0;
		onMouseMove(x, y);
	}
	
	/**
	 * Invoked by the delegate when a key is released
	 * 
	 * @param keyCode the code of the key that was released
	 * 
	 */
	public final synchronized void keyUp(int keyCode)
	{
		keys.put(keyCode, false);
		switch(keyCode)
		{
		case 37:
			leftArrowKeyDown = false;
			break;
		case 38:
			upArrowKeyDown = false;
			break;
		case 39:rightArrowKeyDown = false;
			break;
		case 40:
			downArrowKeyDown = false;
		case 17:
			controlDown = false;
		}
	}
	
	/** TODO:  Something very bad happened here..
	 *         first confirm no bugs in what I've done so far...
	 *         then I'll fix this...
	 */
	public final synchronized boolean keyDown(boolean shift, boolean ctrl, boolean action, int key, char keyChar, KeyEvent e) {
		keys.put(key, true);
		actionDown = action;
		shiftDown = shift;
		controlDown = ctrl;
	
		
		onKeyDown(shift, ctrl, action, key, keyChar);

		if (controlDown && key == 86) {
			return true;
		}
		
		if (key == 37)
			leftArrowKeyDown = true;
		if (key == 39)
			rightArrowKeyDown = true;
		if (key == 38)
			upArrowKeyDown = true;
		if (key == 40)
			downArrowKeyDown = true;
		if (key == 112) // F1
			f1 = !f1;
		if (actionDown)
			return true;
		if (actionDown && shiftDown)
			return true;
		if(controlDown) // Add other ctrl + w/e above this
			return true;
		boolean validKeyDown = isKeyValid(keyChar);
		if (key == 8 && inputText.length() > 0) // backspace
			inputText = inputText.substring(0, inputText.length() - 1);
		if (key == 8 && inputMessage.length() > 0) // backspace
			inputMessage = inputMessage.substring(0, inputMessage.length() - 1);
		if (key == 10 || key == 13) { // enter/return
			enteredText = inputText;
			enteredMessage = inputMessage;
			
			if (showAbuseBox() == 1) {
				if (inputText.length() > 0) {
					setReported(inputText.trim());
					inputText = "";
					enteredText = "";
					setAbuseBox(2);
				}				
			}
		}
		if (getInputBoxType() > 3 && getInputBoxType() < 14) {
			if (!Character.isDigit(keyChar) && !(Character.toString(keyChar).equals("k") || Character.toString(keyChar).equals("m") || Character.toString(keyChar).equals("b")))
				return false;
			if (inputText.contains("k") || inputText.contains("m") || inputText.contains("b"))
				return false;
			if ((inputText.length() < 1 || inputText.length() > 3) && (Character.toString(keyChar).equals("k") || Character.toString(keyChar).equals("m") || Character.toString(keyChar).equals("b")))
				return false;
			if (inputText.length() > 9)
				return false;
			inputText += keyChar;
			if (inputText.length() == 10 && DataConversions.containsOnlyNumbers(inputText))
				DataConversions.parseInt(inputText);
			return true;
		}
		if(key == 222 && !e.isShiftDown()) 
			inputText += "'";
		if(key == 222 && e.isShiftDown()) 
			inputText += "@";
		if (validKeyDown && inputText.length() < 20)
			inputText += keyChar;
		if (validKeyDown && inputMessage.length() < 80)
			inputMessage += keyChar;
		return true;
	}
	
	/**
	 * Is the provided key down?
	 * 
	 * @param key the key to check
	 * 
	 * @return true if the provided key is down, false if not
	 * 
	 */
	public final boolean isKeyDown(int key)
	{
		return keys.containsKey(key) ? keys.get(key) : false;
	}
	
	
	/// Internal helper function to draw strings
	private void drawString(Graphics g, String s, Font font, int x, int y)
	{
		assert g != null && s != null && s.length() > 0 && font != null && x > 0 && y > 0;
		
		FontMetrics fontmetrics = delegate.getContainerImpl().getFontMetrics(font);
		fontmetrics.stringWidth(s);
		g.setFont(font);
		g.drawString(s, x - fontmetrics.stringWidth(s) / 2, y + fontmetrics.getHeight() / 4);
	}
	
	/// Internal helper function for initially setting up the loading screen
	private void drawLoadingScreen()
	{
		
		Graphics gfx = delegate.getContainerImpl().getGraphics();
		int x = (delegate.getContainerImpl().getWidth() - 281) / 2;
		int y = (delegate.getContainerImpl().getHeight() - 148) / 2;
		gfx.setColor(Color.black);
		gfx.fillRect(0, 0, delegate.getContainerImpl().getWidth(), delegate.getContainerImpl().getHeight());
		gfx.drawImage(loadingImage, x, y, delegate.getContainerImpl());
		x += 2;
		y += 90;
		gfx.setColor(new Color(132, 132, 132));
		gfx.drawRect(x - 2, y - 2, 280, 23);
		gfx.setColor(new Color(198, 198, 198));
		drawString(gfx, "Created by JAGeX - visit www.jagex.com", HELVETICA, x + 138, y + 30);
		drawString(gfx, "\251 2001-2009 Jagex Ltd", HELVETICA, x + 138, y + 44);
	}
	
		
	@Override
	public void run()
	{
		loadingPhase = LoadingPhase.LOADING;
		drawLoadingScreen();
		loadGame();
		
		loadingPhase = LoadingPhase.LOADED;
		int anInt10 = 0;
		int index = 0;
		int j = 256;
		int sleepTime = 1;
		int i1 = 0;
		for (int timeIndex = 0; timeIndex < 10; timeIndex++)
			currentTimeArray[timeIndex] = System.currentTimeMillis();

		while (exitTimeout >= 0)
		{
			if (exitTimeout > 0)
			{
				exitTimeout--;
				if (exitTimeout == 0)
				{
					break;
				}
			}
			int k1 = j;
			int i2 = sleepTime;
			j = 300;
			sleepTime = 1;
			long now = System.currentTimeMillis();
			if (currentTimeArray[index] == 0L) {
				j = k1;
				sleepTime = i2;
			} else if (now > currentTimeArray[index])
				j = (int) ((long) (2560 * 20) / (now - currentTimeArray[index]));
			if (j < 25)
				j = 25;
			if (j > 256) {
				j = 256;
				sleepTime = (int) ((long) 20 - (now - currentTimeArray[index]) / 10L);
				if (sleepTime < 10)
					sleepTime = 10;
			}
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException _ex) {
			}
			currentTimeArray[index] = now;
			index = (index + 1) % 10;
			if (sleepTime > 1) {
				for (int j2 = 0; j2 < 10; j2++)
					if (currentTimeArray[j2] != 0L)
						currentTimeArray[j2] += sleepTime;

			}
			int k2 = 0;
			while (i1 < 256) {
				process();
				i1 += j;
				if (++k2 > 1000)
				{
					i1 = 0;
					anInt10 += 6;
					if (anInt10 > 25)
					{
						anInt10 = 0;
						f1 = true;
					}
					break;
				}
			}
			anInt10--;
			i1 &= 0xff;
			render();
		}
		logoutAndStop();
	}
	
	protected abstract void logoutAndStop();
	
	/**
	 * This method is responsible for loading all external data required for 
	 * a game instance to run.  It is essential that all file-system resources 
	 * be released before this method returns.  <strong>Locked resources will 
	 * interfere with the update process!<strong>
	 * 
	 */
	protected abstract void loadGame();
	
	/**
	 * This method is responsible for all non-rendering operations such as 
	 * message processing and collections updating.
	 * 
	 */
	protected abstract void process();
	
	/**
	 * This method is responsible for all rendering operations.
	 * 
	 */
	protected abstract void render();


    protected abstract void onKeyDown(boolean shift, boolean ctrl, boolean action, int key, char keyChar);
	protected abstract void handleMouseDown(int button, int x, int y);
	protected abstract void handleMouseUp(int button, int x, int y);
	protected abstract void onMouseMove(int x, int y);
	protected abstract void onMouseWheelMoved(int direction);
	
	protected abstract void onMouseDragged(int deltaX, int deltaY);
	
	
	/// These constructs will be removed
	private boolean actionDown;
	private boolean shiftDown;
	protected abstract void setAbuseBox(int n);
	protected abstract int getInputBoxType();
	protected abstract int showAbuseBox();
	protected abstract void setReported(String s);
	/// These constructs will be removed
	/// TODO: document
	public final void destroy() { exitTimeout = 1; }
	public void onResize(int width, int height) { }
	public void onPaused() { }
	public void onRestored() { }
	
}
