package org.rscangel.client;

import java.awt.*;

import java.awt.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

//-------------------------------------------------------------------------------------------------------------------
public class GameFrame extends Frame implements ActionListener
{
	private static final long serialVersionUID = 7526472295622776147L;

	SelectSectorsFrame selectSectorsFrame = null;

	// -------------------------------------------------------------------------------------------------------------------
	private final int MENU_EVENT_OPEN = 0;
	private final int MENU_EVENT_SAVE = 1;
	private final int MENU_EVENT_EXIT = 4;
	private final int MENU_EVENT_2D = 5;
	private final int MENU_EVENT_LAND = 6;
	// -------------------------------------------------------------------------------------------------------------------
	MenuBar mainMenu = null;
	Menu fileMenu = null;

	mudclient client = null;

	// -------------------------------------------------------------------------------------------------------------------
	public void showSelectSectorsFrame()
	{
		if( selectSectorsFrame == null )
		{
			selectSectorsFrame = new SelectSectorsFrame( client );

			selectSectorsFrame.setVisible( true );
			selectSectorsFrame.setResizable( false );
			selectSectorsFrame.setAlwaysOnTop( true );
			selectSectorsFrame.setMudClient( client );
		}

		if( !selectSectorsFrame.isVisible() )
		{
			selectSectorsFrame = new SelectSectorsFrame( client );

			selectSectorsFrame.setVisible( true );
			selectSectorsFrame.setResizable( false );
			selectSectorsFrame.setAlwaysOnTop( true );
			selectSectorsFrame.setMudClient( client );
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	public GameFrame( GameWindow gameWindow, int width, int height,
			String title, boolean resizable, boolean flag1 )
	{
		frameOffset = 28;
		frameWidth = width;
		frameHeight = height;
		aGameWindow = gameWindow;

		if( flag1 )
		{
			frameOffset = 48;
		}
		else
		{
			frameOffset = 28;
		}

		setTitle( title );
		setResizable( resizable );
		createMenuBar();
		setVisible( true );
		toFront();
		setNewSize( frameWidth, frameHeight );

		aGraphics49 = getGraphics();
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void setMudClient( mudclient cl )
	{
		client = cl;
	}

	// -------------------------------------------------------------------------------------------------------------------
	private void createMenuBar()
	{
		mainMenu = new MenuBar();
		setMenuBar( mainMenu );

		// file menu
		fileMenu = new Menu( "File" );
		mainMenu.add( fileMenu );

		ExtendedMenuItem menuItem = null;
		fileMenu.add( menuItem = new ExtendedMenuItem( "Open",
				new MenuShortcut( 'O' ) ) );
		menuItem.setEventID( MENU_EVENT_OPEN );
		menuItem.addActionListener( this );

		fileMenu.add( menuItem = new ExtendedMenuItem( "Save",
				new MenuShortcut( 'S' ) ) );
		menuItem.setEventID( MENU_EVENT_SAVE );
		menuItem.addActionListener( this );

		fileMenu.addSeparator();

		fileMenu.add( menuItem = new ExtendedMenuItem( "Exit",
				new MenuShortcut( 'Q' ) ) );
		menuItem.setEventID( MENU_EVENT_EXIT );
		menuItem.addActionListener( this );


		fileMenu = new Menu( "Editor" );
		mainMenu.add( fileMenu );

		menuItem = null;
		fileMenu.add( menuItem = new ExtendedMenuItem( "Switch between 2D/3D", new MenuShortcut( 'V' ) ) );
		menuItem.setEventID( MENU_EVENT_2D );
		menuItem.addActionListener( this );


		fileMenu = new Menu( "Brushes" );
		mainMenu.add( fileMenu );

		menuItem = null;
		fileMenu.add( menuItem = new ExtendedMenuItem( "Land", new MenuShortcut( 'L' ) ) );
		menuItem.setEventID( MENU_EVENT_LAND );
		menuItem.addActionListener( this );

	}

	// handle menu action events
	// -------------------------------------------------------------------------------------------------------------------
	public void actionPerformed( ActionEvent evt )
	{
		ExtendedMenuItem menuItem = (ExtendedMenuItem) evt.getSource();
		if( menuItem == null )
			return;

		int eventID = menuItem.getEventID();
		switch( eventID )
		{
			case MENU_EVENT_EXIT:
				System.exit( 0 );
				break;

			case MENU_EVENT_OPEN:
				if( client == null )
					return;

				if( client.isModified )
				{
					Object[] options = { "Yes, please", "No, thanks", "Cancel" };
					int n = JOptionPane.showOptionDialog( this,
							"Would you like to save changes?", "Question",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[2] );

					switch( n )
					{
						case 0:
							client.saveSectors();
							break;

						case 2:
							return;
					}
				}

				// display sections dialog
				showSelectSectorsFrame();
				break;

			case MENU_EVENT_SAVE:
				// save sectors
				client.saveSectors();
				break;
			case MENU_EVENT_2D:
				client.view2d = !client.view2d;
				client.reloadSection( client.selectedSectionName );
				break;
			case MENU_EVENT_LAND:
				client.showEditFrame();
				break;
			default:
				break;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	public Graphics getGraphics()
	{
		Graphics g = super.getGraphics();
		if( graphicsTranslate == 0 )
		{
			g.translate( 0, 24 );
		}
		else
		{
			g.translate( -5, 0 );
		}

		return g;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void setNewSize( int i, int j )
	{
		super.setSize( i, j + frameOffset );
	}

	// -------------------------------------------------------------------------------------------------------------------
	@SuppressWarnings("deprecation")
	public boolean handleEvent( Event event )
	{
		if( event.id == 401 )
		{
			aGameWindow.keyDown( event, event.key );
		}
		else if( event.id == 402 )
		{
			aGameWindow.keyUp( event, event.key );
		}
		else if( event.id == 501 )
		{
			aGameWindow.mouseDown( event, event.x, event.y - 24 );
		}
		else if( event.id == 506 )
		{
			aGameWindow.mouseDrag( event, event.x, event.y - 24 );
		}
		else if( event.id == 502 )
		{
			aGameWindow.mouseUp( event, event.x, event.y - 24 );
		}
		else if( event.id == 503 )
		{
			aGameWindow.mouseMove( event, event.x, event.y - 24 );
		}
		else if( event.id == 201 )
		{
			aGameWindow.destroy();
		}
		else if( event.id == 1001 )
		{
			aGameWindow.action( event, event.target );
		}
		else if( event.id == 403 )
		{
			aGameWindow.keyDown( event, event.key );
		}
		else if( event.id == 404 )
		{
			aGameWindow.keyUp( event, event.key );
		}

		return true;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public final void paint( Graphics g )
	{
		aGameWindow.paint( g );
	}

	// -------------------------------------------------------------------------------------------------------------------
	int frameWidth;
	int frameHeight;
	int graphicsTranslate;
	int frameOffset;
	GameWindow aGameWindow;
	Graphics aGraphics49;
}
