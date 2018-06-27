package org.rscangel.client;

import java.applet.Applet;
import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;

public class GameWindow extends Applet implements Runnable
{
	private static final long serialVersionUID = 7526472295622776147L;

	// -------------------------------------------------------------------------------------------------------------------
	protected static final String DEFAULT_WINDOW_TITLE = "RSCAngel Editor";
	protected static final int DEFAULT_WINDOW_WIDTH = 1024;
	protected static final int DEFAULT_WINDOW_HEIGHT = 768;
	protected static final int DEFAULT_PROGRESS_WIDTH = 281;
	protected static final int DEFAULT_MAX_PROGRESS_WIDTH = DEFAULT_PROGRESS_WIDTH - 4;

	// -------------------------------------------------------------------------------------------------------------------
	public static final Color BAR_COLOUR = new Color( 120, 0, 0 );
	public static final Font LOADING_FONT = new Font( "Helvetica", 0, 12 );

	// -------------------------------------------------------------------------------------------------------------------
	protected void startGame()
	{
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected synchronized void method2()
	{
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected synchronized void method4()
	{
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected final void createWindow( int width, int height, String title,
			boolean resizable )
	{
		gameFrame = new GameFrame( this, width, height, title, resizable, false );
		loadingScreen = 1;
		gameWindowThread = new Thread( this );
		gameWindowThread.start();
		gameWindowThread.setPriority( 1 );
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void setLogo( Image logo )
	{
		loadingLogo = logo;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void setLogin( Image logo )
	{
		loginLogo = logo;
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected final void changeThreadSleepModifier( int i )
	{
		threadSleepModifier = 1000 / i;
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected final void resetCurrentTimeArray()
	{
		for( int i = 0; i < 10; i++ )
		{
			currentTimeArray[i] = 0L;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final synchronized boolean keyDown( Event event, int key )
	{
		keyDown = key;
		keyDown2 = key;
		lastActionTimeout = 0;

		if( key == 1006 )
			keyLeftDown = true;

		if( key == 1007 )
			keyRightDown = true;

		if( key == 1004 )
			keyUpDown = true;

		if( key == 1005 )
			keyDownDown = true;

		if( (char) key == 'w' || (char) key == 'W' )
			keyWDown = true;

		if( (char) key == 's' || (char) key == 'S' )
			keySDown = true;

		if( (char) key == 'a' || (char) key == 'A' )
			keyADown = true;

		if( (char) key == 'd' || (char) key == 'D' )
			keyDDown = true;

		boolean validKeyDown = false;
		for( int j = 0; j < charSet.length(); j++ )
		{
			if( key != charSet.charAt( j ) )
				continue;

			validKeyDown = true;
			break;
		}

		if( validKeyDown && inputText.length() < 20 )
			inputText += (char) key;

		if( validKeyDown && inputMessage.length() < 80 )
			inputMessage += (char) key;

		if( key == 8 && inputText.length() > 0 ) // backspace
			inputText = inputText.substring( 0, inputText.length() - 1 );

		if( key == 8 && inputMessage.length() > 0 ) // backspace
			inputMessage = inputMessage
					.substring( 0, inputMessage.length() - 1 );

		if( key == 10 || key == 13 )
		{ // enter/return
			enteredText = inputText;
			enteredMessage = inputMessage;
		}

		return true;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final synchronized boolean keyUp( Event event, int i )
	{
		keyDown = 0;

		if( i == 1006 )
			keyLeftDown = false;

		if( i == 1007 )
			keyRightDown = false;

		if( i == 1004 )
			keyUpDown = false;

		if( i == 1005 )
			keyDownDown = false;

		if( (char) i == 'w' || (char) i == 'W' )
			keyWDown = false;

		if( (char) i == 's' || (char) i == 'S' )
			keySDown = false;

		if( (char) i == 'a' || (char) i == 'A' )
			keyADown = false;

		if( (char) i == 'd' || (char) i == 'D' )
			keyDDown = false;

		return true;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final synchronized boolean mouseMove( Event event, int i, int j )
	{
		mouseX = i;
		mouseY = j + yOffset;
		mouseDownButton = 0;
		lastActionTimeout = 0;
		handleMouseMove( (event.metaDown() ? 2 : 1), i, j );
		return true;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final synchronized boolean mouseUp( Event event, int i, int j )
	{
		mouseX = i;
		mouseY = j + yOffset;
		mouseDownButton = 0;
		handleMouseUp( (event.metaDown() ? 2 : 1), i, j );
		return true;
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected void handleMouseUp( int button, int x, int y )
	{
	}
	// -------------------------------------------------------------------------------------------------------------------

	// -------------------------------------------------------------------------------------------------------------------
	protected void handleMouseMove( int button, int x, int y )
		{

		}

	// -------------------------------------------------------------------------------------------------------------------
	public final synchronized boolean mouseDown( Event event, int i, int j )
	{
		mouseX = i;
		mouseY = j + yOffset;
		mouseDownButton = event.metaDown() ? 2 : 1;
		lastMouseDownButton = mouseDownButton;
		lastActionTimeout = 0;
		handleMouseDown( mouseDownButton, i, j );
		return true;
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected void handleMouseDown( int button, int x, int y )
	{
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final synchronized boolean mouseDrag( Event event, int i, int j )
	{
		mouseX = i;
		mouseY = j + yOffset;
		mouseDownButton = event.metaDown() ? 2 : 1;
		handleMouseDrag( mouseDownButton, i, j );
		return true;
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected void handleMouseDrag( int button, int x, int y )
	{
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final void init()
	{
		System.out.println( "Started applet" );
		loadingScreen = 1;
		startThread( this );
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final void start()
	{
		if( exitTimeout >= 0 )
		{
			exitTimeout = 0;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final void stop()
	{
		if( exitTimeout >= 0 )
		{
			exitTimeout = 4000 / threadSleepModifier;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final void destroy()
	{
		exitTimeout = -1;
		try
		{
			Thread.sleep( 2000L );
		}
		catch( Exception e )
		{
		}
		if( exitTimeout == -1 )
		{
			System.out.println( "2 seconds expired, forcing kill" );
			close();
			if( gameWindowThread != null )
			{
				gameWindowThread = null;
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void close()
	{
		exitTimeout = -2;
		System.out.println( "Closing program" );
		try
		{
			Thread.sleep( 1000L );
		}
		catch( Exception e )
		{
		}
		if( gameFrame != null )
		{
			gameFrame.dispose();
		}
		System.exit( 0 );
	}

	// -------------------------------------------------------------------------------------------------------------------
	private void loadFonts()
	{
		GameImage.loadFont( "h11p", 0, this );
		GameImage.loadFont( "h12b", 1, this );
		GameImage.loadFont( "h12p", 2, this );
		GameImage.loadFont( "h13b", 3, this );
		GameImage.loadFont( "h14b", 4, this );
		GameImage.loadFont( "h16b", 5, this );
		GameImage.loadFont( "h20b", 6, this );
		GameImage.loadFont( "h24b", 7, this );
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final void run()
	{
		if( loadingScreen == 1 )
		{
			loadingScreen = 2;
			loadingGraphics = getGraphics();
			drawLoadingLogo();
			drawLoadingScreen( 0, "Loading..." );
			startGame();
			loadingScreen = 0;
		}
		int i = 0;
		int j = 256;
		int sleepTime = 1;
		int i1 = 0;
		for( int j1 = 0; j1 < 10; j1++ )
			currentTimeArray[j1] = System.currentTimeMillis();

		while( exitTimeout >= 0 )
		{
			if( exitTimeout > 0 )
			{
				exitTimeout--;
				if( exitTimeout == 0 )
				{
					close();
					gameWindowThread = null;
					return;
				}
			}
			int k1 = j;
			int i2 = sleepTime;
			j = 300;
			sleepTime = 1;
			long l1 = System.currentTimeMillis();
			if( currentTimeArray[i] == 0L )
			{
				j = k1;
				sleepTime = i2;
			}
			else if( l1 > currentTimeArray[i] )
				j = (int) ((long) (2560 * threadSleepModifier) / (l1 - currentTimeArray[i]));
			if( j < 25 )
				j = 25;
			if( j > 256 )
			{
				j = 256;
				sleepTime = (int) ((long) threadSleepModifier - (l1 - currentTimeArray[i]) / 10L);
				if( sleepTime < threadSleepTime )
					sleepTime = threadSleepTime;
			}
			try
			{
				Thread.sleep( sleepTime );
			}
			catch( InterruptedException _ex )
			{
			}
			currentTimeArray[i] = l1;
			i = (i + 1) % 10;
			if( sleepTime > 1 )
			{
				for( int j2 = 0; j2 < 10; j2++ )
					if( currentTimeArray[j2] != 0L )
						currentTimeArray[j2] += sleepTime;

			}
			int k2 = 0;
			while( i1 < 256 )
			{
				method2();
				i1 += j;
				if( ++k2 > anInt5 )
				{
					i1 = 0;
					anInt10 += 6;
					if( anInt10 > 25 )
					{
						anInt10 = 0;
						keyF1Toggle = true;
					}
					break;
				}
			}
			anInt10--;
			i1 &= 0xff;
			method4();
		}
		if( exitTimeout == -1 )
			close();
		gameWindowThread = null;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final void update( Graphics g )
	{
		paint( g );
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final void paint( Graphics g )
	{
		if( loadingScreen == 2 && loadingLogo != null )
		{
			drawLoadingScreen( anInt16, loadingBarText );
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void drawLoadingLogo()
	{
		loadingGraphics.setColor( Color.black );
		loadingGraphics.drawImage( loadingLogo, 5, 0, this );
		loadFonts();
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void drawLoadingScreen( int i, String s )
	{
		try
		{
			// draw black screen
			loadingGraphics.setColor( Color.black );
			loadingGraphics.fillRect( 0, 0, DEFAULT_WINDOW_WIDTH,
					DEFAULT_WINDOW_HEIGHT );

			// draw logo image
			int imgeLeftPos = (DEFAULT_WINDOW_WIDTH - loadingLogo
					.getWidth( this )) / 2;
			int imageTopPos = (DEFAULT_WINDOW_HEIGHT - loadingLogo
					.getHeight( this )) / 2;
			loadingGraphics.drawImage( loadingLogo, imgeLeftPos, imageTopPos,
					this );

			// draw loading bar border
			int progressLeftPos = (DEFAULT_WINDOW_WIDTH - DEFAULT_PROGRESS_WIDTH) / 2;
			int progressTopPos = imageTopPos + loadingLogo.getHeight( this )
					+ 50;

			anInt16 = i;
			loadingBarText = s;

			loadingGraphics.setColor( new Color( 120, 0, 0 ) );
			loadingGraphics.drawRect( progressLeftPos - 2, progressTopPos - 2,
					280, 23 );
			loadingGraphics.fillRect( progressLeftPos, progressTopPos,
					(277 * i) / 100, 20 );
			loadingGraphics.setColor( Color.black );
			drawString( loadingGraphics, s, LOADING_FONT,
					progressLeftPos + 138, progressTopPos + 10 );

			if( loadingString != null )
			{
				loadingGraphics.setColor( Color.WHITE );
				drawString( loadingGraphics, loadingString, LOADING_FONT,
						progressLeftPos + 138, progressTopPos - 120 );

				return;
			}
		}
		catch( Exception _ex )
		{
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected final void drawLoadingBarText( int i, String s )
	{
		try
		{
			anInt16 = i;
			loadingBarText = s;

			int progressPosition = (DEFAULT_MAX_PROGRESS_WIDTH * i) / 100;
			int imageTopPos = (DEFAULT_WINDOW_HEIGHT - loadingLogo
					.getHeight( this )) / 2;
			int progressLeftPos = (DEFAULT_WINDOW_WIDTH - DEFAULT_PROGRESS_WIDTH) / 2;
			int progressTopPos = imageTopPos + loadingLogo.getHeight( this )
					+ 50;

			// draw progress
			loadingGraphics.setColor( BAR_COLOUR );
			loadingGraphics.fillRect( progressLeftPos, progressTopPos,
					progressPosition, 20 );

			// draw progress background
			loadingGraphics.setColor( Color.black );
			loadingGraphics.fillRect( progressLeftPos + progressPosition,
					progressTopPos, DEFAULT_MAX_PROGRESS_WIDTH
							- progressPosition, 20 );

			// draw progress text
			loadingGraphics.setColor( Color.WHITE );
			drawString( loadingGraphics, s, LOADING_FONT,
					progressLeftPos + 138, progressTopPos + 10 );

			return;
		}
		catch( Exception _ex )
		{
			return;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected final void drawString( Graphics g, String s, Font font, int i,
			int j )
	{
		FontMetrics fontmetrics = (gameFrame == null ? this : gameFrame)
				.getFontMetrics( font );
		fontmetrics.stringWidth( s );
		g.setFont( font );
		g.drawString( s, i - fontmetrics.stringWidth( s ) / 2, j
				+ fontmetrics.getHeight() / 4 );
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected byte[] load( String filename )
	{
		int j = 0;
		int k = 0;
		byte abyte0[] = null;
		try
		{
			java.io.InputStream inputstream = DataOperations
					.streamFromPath( filename );
			DataInputStream datainputstream = new DataInputStream( inputstream );
			byte abyte2[] = new byte[6];
			datainputstream.readFully( abyte2, 0, 6 );
			j = ((abyte2[0] & 0xff) << 16) + ((abyte2[1] & 0xff) << 8)
					+ (abyte2[2] & 0xff);
			k = ((abyte2[3] & 0xff) << 16) + ((abyte2[4] & 0xff) << 8)
					+ (abyte2[5] & 0xff);
			int l = 0;
			abyte0 = new byte[k];
			while( l < k )
			{
				int i1 = k - l;
				if( i1 > 1000 )
				{
					i1 = 1000;
				}
				datainputstream.readFully( abyte0, l, i1 );
				l += i1;
			}
			datainputstream.close();
		}
		catch( IOException _ex )
		{
			_ex.printStackTrace();
		}
		if( k != j )
		{
			byte abyte1[] = new byte[j];
			DataFileDecrypter.unpackData( abyte1, j, abyte0, k, 0 );
			return abyte1;
		}
		else
		{
			return abyte0;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	public Graphics getGraphics()
	{
		if( gameFrame != null )
		{
			return gameFrame.getGraphics();
		}
		return super.getGraphics();
	}

	// -------------------------------------------------------------------------------------------------------------------
	public Image createImage( int i, int j )
	{
		if( gameFrame != null )
		{
			return gameFrame.createImage( i, j );
		}
		return super.createImage( i, j );
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected void startThread( Runnable runnable )
	{
		Thread thread = new Thread( runnable );
		thread.setDaemon( true );
		thread.start();
	}

	// -------------------------------------------------------------------------------------------------------------------
	public GameWindow()
	{
		threadSleepModifier = 20;
		anInt5 = 1000;
		currentTimeArray = new long[10];
		loadingScreen = 1;
		loadingBarText = "Loading";
		keyLeftDown = false;
		keyRightDown = false;
		keyUpDown = false;
		keyDownDown = false;
		keyWDown = false;
		keySDown = false;
		keyADown = false;
		keyDDown = false;
		threadSleepTime = 1;
		keyF1Toggle = false;
		inputText = "";
		enteredText = "";
		inputMessage = "";
		enteredMessage = "";
	}

	// -------------------------------------------------------------------------------------------------------------------
	public Image loadingLogo;
	public Image loginLogo;
	private Thread gameWindowThread;
	private int threadSleepModifier;
	private int anInt5;
	private long currentTimeArray[];
	public static GameFrame gameFrame = null;
	private int exitTimeout;
	private int anInt10;
	public int yOffset;
	public int lastActionTimeout;
	public int loadingScreen;
	public String loadingString;
	private int anInt16;
	private String loadingBarText;
	public Graphics loadingGraphics;
	private static String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
	public boolean keyLeftDown;
	public boolean keyRightDown;
	public boolean keyUpDown;
	public boolean keyDownDown;
	public boolean keyWDown;
	public boolean keySDown;
	public boolean keyADown;
	public boolean keyDDown;
	public int threadSleepTime = 10;
	public int mouseX;
	public int mouseY;
	public int mouseDownButton;
	public int lastMouseDownButton;
	public int keyDown;
	public int keyDown2;
	public boolean keyF1Toggle;
	public String inputText;
	public String enteredText;
	public String inputMessage;
	public String enteredMessage;

}
