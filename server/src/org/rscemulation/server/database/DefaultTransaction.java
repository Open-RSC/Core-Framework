/*
 * Copyright (C) RSCEmulation 2009-13 All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by RSCEmulation Team <dev@rscemulation.net>, January, 2013
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
 
package org.rscemulation.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.rscemulation.server.Config;

/**
 * A default <code>Transaction</code> implementation that simply uses 
 * a single, static connection from the default server bootstrap 
 * configuration.  <b>It should be noted that using connection pooling 
 * may vastly enhance performance.</b>
 * 
 * @author Zilent
 * 
 * @version 1.1, 1.27.2013
 * 
 * @since 3.0
 *
 */
 public abstract class DefaultTransaction
	implements
		Transaction
{
	

	/// The connection that is used in all <code>DefaultTransactions</code>
	private static Connection connection;

	static
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}
		
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Connection getConnection()
	{
		try
		{
			if(connection == null || !connection.isValid(0))
			{
				connection = DriverManager.getConnection(
						"jdbc:mysql://" + 
						Config.DB_HOST + "/" + Config.DB_NAME, 
						Config.DB_LOGIN, 
						Config.DB_PASS);
			}
		}
		catch (SQLException e)
		{
			connection = null;
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		return connection;
	}
}
