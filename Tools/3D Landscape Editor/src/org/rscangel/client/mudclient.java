package org.rscangel.client;

import org.rscangel.client.entityhandling.EntityHandler;
import org.rscangel.client.model.Sector;
import org.rscangel.client.model.Sprite;
import org.rscangel.client.model.Tile;
import org.rscangel.client.util.Config;

import java.awt.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.*;

public final class mudclient extends GameWindowMiddleMan
{
	private static final long serialVersionUID = 7526472295622776147L;

	// -------------------------------------------------------------------------------------------------------------------
	public static final int SPRITE_MEDIA_START = 2000;
	public static final int SPRITE_UTIL_START = 2100;
	public static final int SPRITE_ITEM_START = 2150;
	public static final int SPRITE_LOGO_START = 3150;
	public static final int SPRITE_PROJECTILE_START = 3160;
	public static final int SPRITE_TEXTURE_START = 3220;
	private final int MAX_CONFIG_SEGMENTS = 20;

	// -------------------------------------------------------------------------------------------------------------------
	private boolean recording = false;
	private LinkedList<BufferedImage> frames = new LinkedList<BufferedImage>();
	private long lastFrame = 0;

	boolean realodCurrent = true;
	TileEditFrame tileEditFrame = null;

	// -------------------------------------------------------------------------------------------------------------------

	public boolean view2d = false;
	public String selectedSectionName = "";

	// -------------------------------------------------------------------------------------------------------------------
	private int lastWalkTimeout;
	private int playerCount;
	private Mob npcArray[] = new Mob[500];
	private boolean prayerOn[] = new boolean[50];
	private Mob mobArray[] = new Mob[8000];
	private int wildX = 0;
	private int wildY = 0;
	private int lastWildYSubtract = -1;
	private boolean memoryError = false;
	private int magicLoc = 128;
	private int loggedIn;
	private int screenRotationX;
	private int loginTimer;
	private int areaX;
	private int areaY;
	private int wildYSubtract = -1;
	private boolean showCharacterLookScreen = false;
	private Model objectModelArray[] = new Model[1500];
	private int systemUpdate;
	private int cameraRotation = 128;
	private int logoutTimeout;
	int anInt826;
	private int actionPictureType;
	private int lastAutoCameraRotatePlayerX;
	private int lastAutoCameraRotatePlayerY;
	private int objectX[] = new int[1500];
	private int objectY[] = new int[1500];
	private int objectType[] = new int[1500];
	private int objectID[] = new int[1500];
	private Mob ourPlayer = new Mob();
	int sectionX = 0;
	int sectionY = 0;
	int serverIndex = 0;
	private EngineHandle engineHandle;
	private Mob playerArray[] = new Mob[500];
	private int cameraHeight = 550;
	private int screenRotationY;
	private boolean cameraAutoAngleDebug = false;
	private Mob npcRecordArray[] = new Mob[8000];
	private GameImageMiddleMan gameGraphics;
	private boolean lastLoadedNull = false;
	private Camera gameCamera;
	int mouseClickXArray[] = new int[8192];
	int mouseClickYArray[] = new int[8192];
	private Graphics aGraphics936;
	private int modelUpdatingTimer;
	private int playerAliveTimeout;
	private int objectCount;
	private int cameraSizeInt = 9;
	private boolean freezScreen = false;
	int anInt981;
	long privateMessageTarget;

	private int worldXPos = 0;
	private int worldYPos = 0;
	private int dragStartPosX = 0;
	private int dragStartPosY = 0;
	private int dragEndPosX = 0;
	private int dragEndPosY = 0;

	private boolean sectionLoaded = false;
	public boolean isModified = false;

	private boolean mDragStart = false;
	private int mDragStartMousePosX = 0;
	private int mDragStartMousePosY = 0;
	private int mDragCurrentMousePosX = 0;
	private int mDragCurrentMousePosY = 0;

	private SectionsConfig config = new SectionsConfig( "recent" );

	// -------------------------------------------------------------------------------------------------------------------
	private mudclient()
	{
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void showEditFrame()
	{
		if( tileEditFrame == null )
		{
			tileEditFrame = new TileEditFrame();

			tileEditFrame.setVisible( true );
			tileEditFrame.setResizable( false );
			tileEditFrame.setAlwaysOnTop( true );
			tileEditFrame.setMudClient( this );
		}

		if( !tileEditFrame.isVisible() )
			tileEditFrame.setVisible( true );
	}

	// -------------------------------------------------------------------------------------------------------------------
	private void setTileEditProperties( Tile tile, boolean clean )
	{
		if( tileEditFrame == null )
			return;

		if( clean )
			tileEditFrame.clean();

		if( tile == null )
			return;

		tileEditFrame.inspectTile( tile );
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void updateRender( boolean modified )
	{
		isModified = modified;

		realodCurrent = false;
		freezScreen = true;
		loadSection( sectionX, sectionY );
		freezScreen = false;
	}

	// -------------------------------------------------------------------------------------------------------------------
	private Tile getTileAtPos( int x, int y )
	{
		byte byte0 = 0;

		if( x < 0 || x >= 96 || y < 0 || y >= 96 )
			return null;

		if( x >= 48 && y < 48 )
		{
			byte0 = 1;
			x -= 48;
		}
		else if( x < 48 && y >= 48 )
		{
			byte0 = 2;
			y -= 48;
		}
		else if( x >= 48 && y >= 48 )
		{
			byte0 = 3;
			x -= 48;
			y -= 48;
		}

		Sector sector = engineHandle.sectors[byte0];
		if( sector == null )
			return null;

		Tile ret = sector.getTile( x, y );
		if( ret != null )
		{
			ret.setName( "s" + byte0 + "x" + x + "y" + y );
		}
		return ret;
	}
	// -------------------------------------------------------------------------------------------------------------------
	private int _diagonalWallsEdit = 0;
	private byte _groundElevEdit = 0;
	private byte _groundOverEdit = 0;
	private byte _groundTexEdit = 0;
	private byte _horizontalWallsEdit = 0;
	private byte _roofEdit = 0;
	private byte _verticalWallsEdit = 0;

	public int diagonalWallsEdit = 1;
	public byte groundElevEdit = 0;
	public byte groundOverEdit = 0;
	public byte groundTexEdit = 0;
	public byte horizontalWallsEdit = 0;
	public byte roofEdit = 0;
	public byte verticalWallsEdit = 0;

	private void rememberTileValue(Tile tile)
		{
		_diagonalWallsEdit = tile.diagonalWalls;
		_groundElevEdit = tile.groundElevation;
		if( tile.mIsEmpty )
			{
			_groundOverEdit = tile.mDefaultGroundOverlay;
			}
		else
			{
			_groundOverEdit = tile.groundOverlay;
			}
		_groundTexEdit = tile.groundTexture;
		_horizontalWallsEdit = tile.horizontalWall;
		_roofEdit = tile.roofTexture;
		_verticalWallsEdit = tile.verticalWall;
		}
	public void updateTile(Tile tileElement)
		{
		if( tileElement == null )
			return;
		tileElement.diagonalWalls = diagonalWallsEdit;
		tileElement.groundElevation = groundElevEdit;
		tileElement.groundOverlay = groundOverEdit;
		tileElement.groundTexture = groundTexEdit;
		tileElement.horizontalWall = horizontalWallsEdit;
		tileElement.roofTexture = roofEdit;
		tileElement.verticalWall = verticalWallsEdit;
		tileElement.mIsEmpty = false;
		}

	// -------------------------------------------------------------------------------------------------------------------
	private void editElevation( int x, int y )
	{
		if( !sectionLoaded )
			return;

		//showEditFrame();

			Tile tile = getTileAtPos( x, y );
			if( tile == null )
				System.out.println( "Cannot obtain tile at coordinates: " + x
						+ ":" + y );
			rememberTileValue(tile);
			updateTile(tile);
			Sector sector = new Sector(selectedSectionName);
			sector.setTile(x, y, tile);
			updateRender( true );
			//setTileEditProperties( tile, true );
	}
	public void reloadTile(Tile tile)
	{

	}

	// -------------------------------------------------------------------------------------------------------------------
	public void reloadSection( String sectionName ) // update edit siin pets
	{
		isModified = false;
		int x = 0;
		int y = 0;

		int hIndex = sectionName.lastIndexOf( 'h' );
		int xIndex = sectionName.lastIndexOf( 'x' );
		int yIndex = sectionName.lastIndexOf( 'y' );

		if( hIndex < 0 || xIndex < 0 || yIndex < 0 )
			return;

		try
		{
			wildYSubtract = Integer.parseInt( sectionName.substring(
					hIndex + 1, xIndex ) );
			x = Integer.parseInt( sectionName.substring( xIndex + 1, yIndex ) );
			y = Integer.parseInt( sectionName.substring( yIndex + 1,
					sectionName.length() ) );
		}
		catch( Exception e )
		{
			return;
		}

		wildX = ((x + 1) * 48) - 24;
		wildY = ((y + 1) * 48) - 24;

		realodCurrent = true;

		// clean engine
		for( int i = 0; i < engineHandle.sectors.length; i++ )
			engineHandle.sectors[i] = null;

		// load section
		sectionLoaded = loadSection( sectionX, sectionY );

		sectionX -= areaX;
		sectionY -= areaY;
		int mapEnterX = sectionX * magicLoc + 64;
		int mapEnterY = sectionY * magicLoc + 64;

		if( sectionLoaded )
		{
			ourPlayer.waypointCurrent = 0;
			ourPlayer.waypointEndSprite = 0;
			ourPlayer.currentX = ourPlayer.waypointsX[0] = mapEnterX;
			ourPlayer.currentY = ourPlayer.waypointsY[0] = mapEnterY;
		}

		ourPlayer = makePlayer( serverIndex, mapEnterX, mapEnterY, 1 );

		// reset global variables for rendering
		resetVars();

		gameFrame.setTitle( DEFAULT_WINDOW_TITLE + " - " + sectionName );
		selectedSectionName = sectionName;
	}

	// -------------------------------------------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public String[] getSectionNames()
	{
		ZipFile tileArchive = engineHandle.getTileArchive();
		if( tileArchive == null )
			return new String[0];

		String[] sections = new String[tileArchive.size()];

		try
		{
			Enumeration entries = tileArchive.entries();
			ZipEntry entry = null;

			int i = 0;
			while( entries.hasMoreElements() )
			{
				entry = (ZipEntry) entries.nextElement();
				if( entry == null )
					continue;

				sections[i] = entry.getName();
				i++;
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return sections;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public String[] getLastSectionNames()
	{
		String[] sections = new String[MAX_CONFIG_SEGMENTS];
		sections = config.getSetionsList();
		return sections;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void setLastSectionName( String sectionName )
	{
		config.setLastSection( sectionName );
	}

	// -------------------------------------------------------------------------------------------------------------------
	public SectionsConfig getConfig()
	{
		return config;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public static final void main( String[] args ) throws Exception
	{
		Config.initConfig( args.length > 0 ? args[0] : "settings.ini" );
		GameWindowMiddleMan.clientVersion = 1;

		mudclient mc = new mudclient();
		mc.setLogo( Toolkit.getDefaultToolkit().getImage( Config.CONF_DIR + File.separator + "Loading.dq" ) );
		mc.createWindow( DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT, DEFAULT_WINDOW_TITLE, false );
	}

	// -------------------------------------------------------------------------------------------------------------------
	private BufferedImage getImage() throws IOException
	{
		BufferedImage bufferedImage = new BufferedImage( DEFAULT_WINDOW_WIDTH,
				DEFAULT_WINDOW_HEIGHT + 11, BufferedImage.TYPE_INT_RGB );

		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.drawImage( gameGraphics.image, 0, 0, this );
		g2d.dispose();

		return bufferedImage;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final Graphics getGraphics()
	{
		if( GameWindow.gameFrame != null )
		{
			return GameWindow.gameFrame.getGraphics();
		}

		return super.getGraphics();
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void loadConfigFilter()
	{
		drawLoadingBarText( 15, "Unpacking Configuration" );
		EntityHandler.load();
	}

	// calculate mouse position in the world
	// -------------------------------------------------------------------------------------------------------------------
	private void calculateMousePosition( int x, int y )
	{
		if( gameCamera == null )
			return;

		// gameCamera.updateMouseCoords( x, y );
		// gameCamera.finishCamera();

		int j = -1;
		int i1 = gameCamera.getCurrentVisibleModelCount();
		Model models[] = gameCamera.getVisibleModels();

		// System.out.println( "Search visible objects" );
		for( int j1 = 0; j1 < i1; j1++ )
		{
			Model model = models[j1];
			int ai[] = gameCamera.getVisibleModelIntArray();
			int k1 = ai[j1];

			if( k1 >= 0 )
				k1 = model.anIntArray258[k1] - 0x30d40;

			if( k1 >= 0 )
				j = k1;

			int l1 = j;
			worldXPos = engineHandle.selectedX[l1];
			worldYPos = engineHandle.selectedY[l1];
			// System.out.println( "Visible object found" );
		}
	}

    private final void setPixelsAndAroundColour(int x, int y, int colour) {
        gameGraphics.setPixelColour(x, y, colour);
        gameGraphics.setPixelColour(x - 1, y, colour);
        gameGraphics.setPixelColour(x + 1, y, colour);
        gameGraphics.setPixelColour(x, y - 1, colour);
        gameGraphics.setPixelColour(x, y + 1, colour);
    }

	protected void handleMouseMove( int button, int x, int y)
		{
		calculateMousePosition( x, y );
		}

	// game window mouse down handler
	// -------------------------------------------------------------------------------------------------------------------
	protected void handleMouseUp( int button, int x, int y )
	{
		// drawGame();
		// calculateMousePosition();
		//calculateMousePosition( x, y );
		if (button == 1)
			{
			editElevation( worldXPos, worldYPos );
			}
		else if (button == 2)
			{
			Tile tile = getTileAtPos( worldXPos, worldYPos );
			if( tile == null )
				System.out.println( "Cannot obtain tile at coordinates: " + x + ":" + y );
			setTileEditProperties( tile, false );
			}
		if( button == 12 )
		{
			if( mDragStart )
			{
				// int oldPosX = ourPlayer.currentX;
				// int oldPosY = ourPlayer.currentY;

				// ourPlayer.currentX = ourPlayer.waypointsX[0] = worldXPos;
				// ourPlayer.currentY = ourPlayer.waypointsY[0] = worldYPos;

				calculateMousePosition( x, y );

				// ourPlayer.currentX = ourPlayer.waypointsX[0] = oldPosX;
				// ourPlayer.currentY = ourPlayer.waypointsY[0] = oldPosY;

				dragEndPosX = worldXPos;
				dragEndPosY = worldYPos;

				if( mDragStartMousePosX == x && mDragStartMousePosY == y )
				{
					dragStartPosX = worldXPos;
					dragStartPosY = worldYPos;
				}

				mDragStart = false;
				editElevation( worldXPos, worldYPos );
			}
		}
	}

	// game window mouse down handler
	// -------------------------------------------------------------------------------------------------------------------
	protected final void handleMouseDown( int button, int x, int y )
	{

			/*calculateMousePosition( x, y );
			editElevation( worldXPos, worldYPos );
		if( button == 1 && !mDragStart )
		{
			mDragStartMousePosX = x;
			mDragStartMousePosY = y;

			mDragCurrentMousePosX = x;
			mDragCurrentMousePosY = y;

			// int oldPosX = ourPlayer.currentX;
			// int oldPosY = ourPlayer.currentY;

			// ourPlayer.currentX = ourPlayer.waypointsX[0] = worldXPos;
			// ourPlayer.currentY = ourPlayer.waypointsY[0] = worldYPos;

			calculateMousePosition( x, y );

			// ourPlayer.currentX = ourPlayer.waypointsX[0] = oldPosX;
			// ourPlayer.currentY = ourPlayer.waypointsY[0] = oldPosY;

			dragStartPosX = worldXPos;
			dragStartPosY = worldYPos;

			mDragStart = true;
		}
		*/
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected final void handleMouseDrag( int button, int x, int y )
	{
		if( button == 1 && mDragStart )
		{
			mDragCurrentMousePosX = x;
			mDragCurrentMousePosY = y;

			// drawGame();
		}
	}

	// needs to start the game after login
	// -------------------------------------------------------------------------------------------------------------------
	protected final void method4()
	{
		try
		{
			if( loggedIn == 1 )
			{
				drawGame();
			}
			else
			{
				gameFrame.setMudClient( this );
				// gameFrame.openSectionWithDialog();
				gameFrame.showSelectSectorsFrame();
				resetVars();
			}
		}
		catch( OutOfMemoryError e )
		{
			garbageCollect();
			memoryError = true;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final Mob makePlayer( int mobArrayIndex, int x, int y, int sprite )
	{
		if( mobArray[mobArrayIndex] == null )
		{
			mobArray[mobArrayIndex] = new Mob();
			mobArray[mobArrayIndex].serverIndex = mobArrayIndex;
			mobArray[mobArrayIndex].mobIntUnknown = 0;
		}

		Mob mob = mobArray[mobArrayIndex];
		mob.serverIndex = mobArrayIndex;
		mob.waypointEndSprite = 0;
		mob.waypointCurrent = 0;
		mob.waypointsX[0] = mob.currentX = x;
		mob.waypointsY[0] = mob.currentY = y;
		mob.nextSprite = mob.currentSprite = sprite;
		mob.stepCount = 0;

		playerArray[playerCount++] = mob;
		return mob;
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected final void method2()
	{
		if( memoryError )
			return;

		if( lastLoadedNull )
			return;

		try
		{
			loginTimer++;
			if( loggedIn == 0 )
			{
				super.lastActionTimeout = 0;
			}
			if( loggedIn == 1 )
			{
				super.lastActionTimeout++;
				processGame();
			}
		}
		catch( OutOfMemoryError _ex )
		{
			garbageCollect();
			memoryError = true;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void resetLoginVars()
	{
		loggedIn = 0;
		playerCount = 0;
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void drawGame()
	{
		if(view2d)
			{


			}
		if( freezScreen )
		{
			return;
		}

		long now = System.currentTimeMillis();

		if( now - lastFrame > (1000 / Config.MOVIE_FPS) && recording )
		{
			try
			{
				lastFrame = now;
				frames.add( getImage() );
			}
			catch( Exception e )
			{
			}
		}

		if( showCharacterLookScreen )
		{
			return;
		}

		if( !engineHandle.playerIsAlive )
		{
			return;
		}

		gameGraphics.f1Toggle = false;
		gameGraphics.method211();
		gameGraphics.f1Toggle = super.keyF1Toggle;

		int l5 = lastAutoCameraRotatePlayerX + screenRotationX;
		int i8 = lastAutoCameraRotatePlayerY + screenRotationY;
		gameCamera.setCamera( l5, -engineHandle.getAveragedElevation( l5, i8 ),
				i8, 912, cameraRotation * 4, 0, cameraHeight * 2 );

		gameCamera.finishCamera();
		if( mDragStart )
		{
			int minX = Math.min( mDragStartMousePosX, mDragCurrentMousePosX );
			int maxX = Math.max( mDragStartMousePosX, mDragCurrentMousePosX );

			int minY = Math.min( mDragStartMousePosY, mDragCurrentMousePosY );
			int maxY = Math.max( mDragStartMousePosY, mDragCurrentMousePosY );

			minX = Math.max( gameGraphics.getImageX(), minX );
			minX = Math.min( gameGraphics.getImageX()
					+ gameGraphics.getImageWidth(), minX );

			maxX = Math.max( gameGraphics.getImageX(), maxX );
			maxX = Math.min( gameGraphics.getImageX()
					+ gameGraphics.getImageWidth(), maxX );

			minY = Math.max( gameGraphics.getImageY(), minY );
			minY = Math.min( gameGraphics.getImageY()
					+ gameGraphics.getImageHeight(), minY );

			maxY = Math.max( gameGraphics.getImageY(), maxY );
			maxY = Math.min( gameGraphics.getImageY()
					+ gameGraphics.getImageHeight(), maxY );

			if( maxX > minX && maxY > minY )
			{
				gameGraphics.drawBoxEdge( minX, minY, Math.min( maxX - minX,
						gameGraphics.getImageWidth() - minX ), Math.min( maxY
						- minY, gameGraphics.getImageHeight() - minY ),
						0xffffff );
			}

		}
		gameGraphics.drawImage( aGraphics936, 0, 0 );
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected final void resetIntVars()
	{
		systemUpdate = 0;
		loggedIn = 0;
		logoutTimeout = 0;
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void garbageCollect()
	{
		try
		{
			if( gameGraphics != null )
			{
				gameGraphics.cleanupSprites();
				gameGraphics.imagePixelArray = null;
				gameGraphics = null;
			}

			if( gameCamera != null )
			{
				gameCamera.cleanupModels();
				gameCamera = null;
			}

			objectModelArray = null;
			mobArray = null;
			playerArray = null;
			npcRecordArray = null;
			npcArray = null;
			ourPlayer = null;

			if( engineHandle != null )
			{
				engineHandle.aModelArray596 = null;
				engineHandle.aModelArrayArray580 = null;
				engineHandle.aModelArrayArray598 = null;
				engineHandle.aModel = null;
				engineHandle = null;
			}

			System.gc();
			return;
		}
		catch( Exception _ex )
		{
			return;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected final void startGame()
	{
		super.yOffset = 0;
		loadConfigFilter(); // 15%

		if( lastLoadedNull )
			return;

		aGraphics936 = getGraphics();
		changeThreadSleepModifier( 50 );
		gameGraphics = new GameImageMiddleMan( DEFAULT_WINDOW_WIDTH,
				DEFAULT_WINDOW_HEIGHT + 12 + 99, 4000, this );
		gameGraphics._mudclient = this;
		gameGraphics.setDimensions( 0, 0, DEFAULT_WINDOW_WIDTH,
				DEFAULT_WINDOW_HEIGHT + 12 );
		Menu.aBoolean220 = false;


		if( lastLoadedNull )
			return;

		gameCamera = new Camera( gameGraphics, 15000, 15000, 1000 );
		gameCamera.setCameraSize( DEFAULT_WINDOW_WIDTH / 2,
				DEFAULT_WINDOW_HEIGHT / 2, DEFAULT_WINDOW_WIDTH / 2,
				DEFAULT_WINDOW_HEIGHT / 2, DEFAULT_WINDOW_WIDTH, cameraSizeInt );
		gameCamera.zoom1 = 23000;
		gameCamera.zoom2 = 23000;
		gameCamera.zoom3 = 10;
		gameCamera.zoom4 = 21000;

		engineHandle = new EngineHandle( gameCamera, gameGraphics );
		loadTextures(); // 60%

		if( lastLoadedNull )
			return;

		drawLoadingBarText( 100, "Starting game..." );
		resetLoginVars();
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void loadSprite( int id, String packageName, int amount )
	{
		for( int i = id; i < id + amount; i++ )
		{
			if( !gameGraphics.loadSprite( i, packageName ) )
			{
				lastLoadedNull = true;
				return;
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void loadMedia()
	{
		drawLoadingBarText( 30, "Unpacking media" );

		int i = EntityHandler.invPictureCount();

		for( int j = 1; i > 0; j++ )
		{
			int k = i;
			i -= 30;

			if( k > 30 )
			{
				k = 30;
			}

			loadSprite( SPRITE_ITEM_START + (j - 1) * 30, "media.object", k );
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void loadTextures()
	{
		drawLoadingBarText( 60, "Unpacking textures" );
		gameCamera.method297( EntityHandler.textureCount(), 7, 11 );

		for( int i = 0; i < EntityHandler.textureCount(); i++ )
		{
			loadSprite( SPRITE_TEXTURE_START + i, "texture", 1 );
			Sprite sprite = ((GameImage) (gameGraphics)).sprites[SPRITE_TEXTURE_START
					+ i];

			int length = sprite.getWidth() * sprite.getHeight();
			int[] pixels = sprite.getPixels();
			int ai1[] = new int[32768];

			for( int k = 0; k < length; k++ )
			{
				ai1[((pixels[k] & 0xf80000) >> 9) + ((pixels[k] & 0xf800) >> 6)
						+ ((pixels[k] & 0xf8) >> 3)]++;
			}

			int[] dictionary = new int[256];
			dictionary[0] = 0xff00ff;
			int[] temp = new int[256];

			for( int i1 = 0; i1 < ai1.length; i1++ )
			{
				int j1 = ai1[i1];

				if( j1 > temp[255] )
				{
					for( int k1 = 1; k1 < 256; k1++ )
					{
						if( j1 <= temp[k1] )
							continue;

						for( int i2 = 255; i2 > k1; i2-- )
						{
							dictionary[i2] = dictionary[i2 - 1];
							temp[i2] = temp[i2 - 1];
						}

						dictionary[k1] = ((i1 & 0x7c00) << 9)
								+ ((i1 & 0x3e0) << 6) + ((i1 & 0x1f) << 3)
								+ 0x40404;
						temp[k1] = j1;
						break;
					}
				}

				ai1[i1] = -1;
			}

			byte[] indices = new byte[length];

			for( int l1 = 0; l1 < length; l1++ )
			{
				int j2 = pixels[l1];
				int k2 = ((j2 & 0xf80000) >> 9) + ((j2 & 0xf800) >> 6)
						+ ((j2 & 0xf8) >> 3);
				int l2 = ai1[k2];

				if( l2 == -1 )
				{
					int i3 = 0x3b9ac9ff;
					int j3 = j2 >> 16 & 0xff;
					int k3 = j2 >> 8 & 0xff;
					int l3 = j2 & 0xff;

					for( int i4 = 0; i4 < 256; i4++ )
					{
						int j4 = dictionary[i4];
						int k4 = j4 >> 16 & 0xff;
						int l4 = j4 >> 8 & 0xff;
						int i5 = j4 & 0xff;
						int j5 = (j3 - k4) * (j3 - k4) + (k3 - l4) * (k3 - l4)
								+ (l3 - i5) * (l3 - i5);
						if( j5 < i3 )
						{
							i3 = j5;
							l2 = i4;
						}
					}

					ai1[k2] = l2;
				}

				indices[l1] = (byte) l2;
			}

			gameCamera.method298( i, indices, dictionary, sprite
					.getSomething1() / 64 - 1 );
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final boolean loadSection( int i, int j )
	{
		engineHandle.playerIsAlive = false;

		i += wildX;
		j += wildY;
		int i1 = (i + 24) / 48;
		int j1 = (j + 24) / 48;
		lastWildYSubtract = wildYSubtract;
		areaX = i1 * 48 - 48;
		areaY = j1 * 48 - 48;

		engineHandle.method401( i, j, lastWildYSubtract, realodCurrent );

		areaX -= wildX;
		areaY -= wildY;
		engineHandle.playerIsAlive = true;

		return true;
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected final byte[] load( String filename )
	{
		return super.load( Config.CONF_DIR + File.separator + "data"
				+ File.separator + filename );
	}

	// -------------------------------------------------------------------------------------------------------------------
	private final void processGame()
	{
		if( systemUpdate > 1 )
			systemUpdate--;

		if( logoutTimeout > 0 )
			logoutTimeout--;

		if( ourPlayer.currentSprite == 8 || ourPlayer.currentSprite == 9 )
			lastWalkTimeout = 500;

		if( lastWalkTimeout > 0 )
			lastWalkTimeout--;

		if( showCharacterLookScreen )
			return;

		if( cameraAutoAngleDebug )
		{
			if( lastAutoCameraRotatePlayerX - ourPlayer.currentX < -500
					|| lastAutoCameraRotatePlayerX - ourPlayer.currentX > 500
					|| lastAutoCameraRotatePlayerY - ourPlayer.currentY < -500
					|| lastAutoCameraRotatePlayerY - ourPlayer.currentY > 500 )
			{
				lastAutoCameraRotatePlayerX = ourPlayer.currentX;
				lastAutoCameraRotatePlayerY = ourPlayer.currentY;
			}
		}
		else
		{
			if( lastAutoCameraRotatePlayerX - ourPlayer.currentX < -500
					|| lastAutoCameraRotatePlayerX - ourPlayer.currentX > 500
					|| lastAutoCameraRotatePlayerY - ourPlayer.currentY < -500
					|| lastAutoCameraRotatePlayerY - ourPlayer.currentY > 500 )
			{
				lastAutoCameraRotatePlayerX = ourPlayer.currentX;
				lastAutoCameraRotatePlayerY = ourPlayer.currentY;
			}

			if( lastAutoCameraRotatePlayerX != ourPlayer.currentX )
				lastAutoCameraRotatePlayerX += (ourPlayer.currentX - lastAutoCameraRotatePlayerX)
						/ (16 + (cameraHeight - 500) / 15);

			if( lastAutoCameraRotatePlayerY != ourPlayer.currentY )
				lastAutoCameraRotatePlayerY += (ourPlayer.currentY - lastAutoCameraRotatePlayerY)
						/ (16 + (cameraHeight - 500) / 15);
		}

		if( playerAliveTimeout != 0 )
			super.lastMouseDownButton = 0;

		gameCamera.updateMouseCoords( super.mouseX, super.mouseY );
		super.lastMouseDownButton = 0;

		if( super.keyLeftDown )
		{
			cameraRotation = cameraRotation + 2 & 0xff;
		}
		else if( super.keyRightDown )
		{
			cameraRotation = cameraRotation - 2 & 0xff;
		}
		else if( super.keyUpDown )
		{
			cameraHeight -= 10;
		}
		else if( super.keyDownDown )
		{
			cameraHeight += 10;
		}
		else if( super.keyWDown )
		{
			ourPlayer.currentX += 10;
		}
		else if( super.keySDown )
		{
			ourPlayer.currentX -= 10;
		}

		if( super.keyADown )
			ourPlayer.currentY += 10;

		if( super.keyDDown )
		{
			ourPlayer.currentY -= 10;
			// for testing
		}

		if( actionPictureType > 0 )
		{
			actionPictureType--;
		}
		else if( actionPictureType < 0 )
		{
			actionPictureType++;
		}

		modelUpdatingTimer++;
		if( modelUpdatingTimer > 5 )
			modelUpdatingTimer = 0;
	}

	// -------------------------------------------------------------------------------------------------------------------
	protected final void resetVars()
	{
		systemUpdate = 0;
		logoutTimeout = 0;
		loggedIn = 1;
		gameGraphics.method211();
		if( freezScreen == false )
		{
			gameGraphics.drawImage( aGraphics936, 0, 0 );
		}

		for( int i = 0; i < objectCount; i++ )
		{
			gameCamera.removeModel( objectModelArray[i] );
			engineHandle.updateObject( objectX[i], objectY[i], objectType[i],
					objectID[i] );
		}

		objectCount = 0;
		playerCount = 0;

		for( int k = 0; k < mobArray.length; k++ )
			mobArray[k] = null;

		for( int l = 0; l < playerArray.length; l++ )
			playerArray[l] = null;

		for( int i1 = 0; i1 < npcRecordArray.length; i1++ )
			npcRecordArray[i1] = null;

		for( int j1 = 0; j1 < npcArray.length; j1++ )
			npcArray[j1] = null;

		for( int k1 = 0; k1 < prayerOn.length; k1++ )
			prayerOn[k1] = false;

		super.lastMouseDownButton = 0;
		super.mouseDownButton = 0;
		super.friendsCount = 0;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final Image createImage( int i, int j )
	{
		if( GameWindow.gameFrame != null )
			return GameWindow.gameFrame.createImage( i, j );

		return super.createImage( i, j );
	}

	// -------------------------------------------------------------------------------------------------------------------
	public boolean saveSectors()
	{
		SectorSaver saver = new SectorSaver( engineHandle );
		isModified = false;

		return saver.save();
	}
}
