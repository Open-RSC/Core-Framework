package org.rscangel.client;

import java.awt.MenuItem;
import java.awt.MenuShortcut;

public class ExtendedMenuItem extends MenuItem
{
	private static final long serialVersionUID = 7526472295622776147L;
	private int mID = 0;

	// -------------------------------------------------------------------------------------------------------------------
	public ExtendedMenuItem( String title, MenuShortcut shortcut )
	{
		super( title, shortcut );
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void setEventID( int id )
	{
		mID = id;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public int getEventID()
	{
		return mID;
	}
}
