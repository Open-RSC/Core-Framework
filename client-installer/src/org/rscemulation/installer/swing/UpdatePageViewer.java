/*
 * Copyright (C) RSCDaemon - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by RSCDaemon Team <dev@rscdaemon.com>, Unknown Date
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
package org.rscemulation.installer.swing;

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.rscemulation.installer.Logger;

/**
 * A convenience class to encapsulate the web feed viewer.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public class UpdatePageViewer
	extends
		JScrollPane
{

	private static final long serialVersionUID = 2880895204646547013L;
	
	/**
	 * Constructs an <code>UpdatePageViewer</code> starting at the provided 
	 * page, with the provided bounds
	 * 
	 * @param url the initial page of this viewer
	 * 
	 * @param x the x-coordinate of the upper-left corner of this viewer
	 * 
	 * @param y the y-coordinate of the upper-left corner of this viewer
	 * 
	 * @param w the width of this viewer
	 * 
	 * @param h the height of this viewer
	 * 
	 * @throws IOException if any I/O error occurs
	 * 
	 */
	public UpdatePageViewer(final String url, final int x, final int y, final int w, final int h, Image scrollbarTrack, Image scrollbarGrip)
		throws
			IOException
	{
		super(
			new JEditorPane()
			{
				private static final long serialVersionUID = -8654682839787191500L;
				{
					super.setBorder(null);
					super.setEditable(false);
					super.setPage(url);
					super.setOpaque(false);
					super.addHyperlinkListener(new HyperlinkListener() {
					    public void hyperlinkUpdate(HyperlinkEvent e) {
					        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					        	try
								{
									setPage(e.getURL());
								}
					        	catch (IOException e1)
								{
									Logger.error(e1);
								}
					        }
					    }
					});
				}
			}
		);					super.setBorder(null);
		super.getVerticalScrollBar().setUI(new ImageScrollBarUI(scrollbarTrack, scrollbarGrip));
		super.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		super.getVerticalScrollBar().setOpaque(false);
		super.getViewport().setOpaque(false);
		super.setBackground(new Color(0, 0, 0, 0));
		super.getViewport().setBorder(null);
		super.getVerticalScrollBar().setBorder(null);
		super.setOpaque(false);
		super.setBounds(x, y, w, h);
	}
}
