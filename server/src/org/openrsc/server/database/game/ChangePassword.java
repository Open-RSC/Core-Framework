/*
 * Copyright (C) openrsc 2009-13 All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by openrsc Team <dev@openrsc.com>, January, 2013
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

package org.openrsc.server.database.game;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openrsc.server.database.DefaultTransaction;
import org.openrsc.server.database.Transaction;
import org.openrsc.server.database.TransactionListener;
import org.openrsc.server.model.Player;
import org.openrsc.server.util.DataConversions;

import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ErrorLog;


/**
 * A Transaction for handling player change password
 * 
 * @author Kenix
 * 
 */
public class ChangePassword
	extends
		DefaultTransaction
{
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	Date date = new Date();
    
	public final class DefaultChangePasswordListener
		implements
			TransactionListener
	{
		@Override
		public void onSuccess()
		{
			System.out.println(dateFormat.format(date)+": Password Changed: " + DataConversions.hashToUsername(usernameHash));
		}

		@Override
		public void onFailure(int code)
		{
			System.out.println(dateFormat.format(date)+": Failed to save: " + DataConversions.hashToUsername(usernameHash));
		}
	}
	
	@Override
	public boolean equals(Object rhs)
	{
		return ((ChangePassword)rhs).usernameHash == usernameHash;
	}
	
	@Override
	public String toString()
	{
		return "\"Change Password\" {user=" + DataConversions.hashToUsername(usernameHash) + "}";
	}
    
    private final Player player;
    private final String newPassword;
    private final long usernameHash;
	
	public ChangePassword(Player player, String newPassword)
	{
        this.player = player;
		this.newPassword = newPassword;
        this.usernameHash = player.getUsernameHash();
	}
	
	@Override
	public Integer call()
		throws
			SQLException
	{
		Connection connection = super.getConnection();
		if(connection == null)
		{
			return Transaction.DATABASE_UNAVAILABLE;
		}
        
        try
		{
			Statement statement = connection.createStatement();
            
            String salt = DataConversions.generateSalt();
            String pass = DataConversions.hashPassword(newPassword, salt);
        
			statement.executeUpdate(
                "UPDATE `rscd_players` SET " + "`pass` = '" + pass + "', `password_salt` = '" + salt + "'" + " WHERE `user` = '" + usernameHash + "';"
			);
            
            return Transaction.TRANSACTION_SUCCESS;
        }
		catch(SQLException e)
		{
			e.printStackTrace();
			Logger.log(new ErrorLog(usernameHash, player.getAccount(), player.getIP(), "Unable to change password", DataConversions.getTimeStamp()));
            return Transaction.UNHANDLED_EXCEPTION;
		}
	}

	@Override
	public boolean retryOnFatalError()
	{
		return true;
	}
}
