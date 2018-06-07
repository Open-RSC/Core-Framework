/*
 * Copyright (C) openrsc 2009-13 All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by openrsc Team <dev@openrsc.net>, January, 2013
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
 *
 *
 *
 *
 */

package org.openrsc.server;

import org.openrsc.server.database.DatabaseService;
import org.openrsc.server.database.DefaultDatabaseService;

import com.rscdaemon.scripting.quest.Quest;
import com.rscdaemon.scripting.quest.QuestFactory;

/**
 * The (soon to be) entry point for the openrsc realm server
 * 
 * @author Zilent
 *
 * @version 1.1, 1.27.2013
 * 
 * @since 3.0
 * 
 */
public final class ServerBootstrap
{

	/// The DatabaseService for this server instance
	private final static DatabaseService databaseService =
			new DefaultDatabaseService();
	
	private final static QuestFactory questFactory = new QuestFactory("config/quests.xml");
	
	/**
	 * Retrieves the DatabaseService for this server instance
	 * 
	 * @return the DatabaseService for this server instance
	 * 
	 */
	public static DatabaseService getDatabaseService()
	{
		return databaseService;
	}
	
	/**
	 * Retrieves a fresh <code>Quest</code> with the provided ID
	 * 
	 * @param id the ID of the <code>Quest</code> to retrieve
	 * 
	 * @return a fresh <code>Quest</code> with the provided ID
	 * 
	 */
	public final static Quest getQuest(int id)
	{
		return questFactory.getQuest(id);
	}
	
	/// Entry Point
	public static void main(String[] args)
	{
		System.out.println("Don't call me yet!");
	}
}
