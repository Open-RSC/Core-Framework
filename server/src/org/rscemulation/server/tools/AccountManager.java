package org.rscemulation.server.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// fuck java.util.logging...
public class AccountManager
{

	private final static PrintStream OUT;

	static
	{
		try
		{
			OUT = new PrintStream(new FileOutputStream("log.txt"));
		}
		catch(IOException e)
		{
			throw (RuntimeException)new RuntimeException().initCause(e);
		}
	}
	
	public static String hashToUsername(long l) {
		if (l < 0L)
			return "invalid_name";
		String s = "";
		while (l != 0L) {
			int i = (int) (l % 37L);
			l /= 37L;
			if (i == 0)
				s = " " + s;
			else if (i < 27) {
				if (l % 37L == 0L)
					s = (char) ((i + 65) - 1) + s;
				else
					s = (char) ((i + 97) - 1) + s;
			} else
				s = (char) ((i + 48) - 27) + s;
		}
		
		return s;
	}
	
	public static void main(String[] args)
		throws
			SQLException
	{
		OUT.println("Removing banned accounts...");
		AccountManager mgr = new AccountManager("localhost", "rscunity", "root", "malware");
		mgr.deleteBannedPlayers();
	}
	
	private String host, database, user, pass;
	
	private static long usernameToHash(String s) {
		try {
			s = s.toLowerCase();
			String s1 = "";
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c >= 'a' && c <= 'z')
					s1 = s1 + c;
				else if (c >= '0' && c <= '9')
					s1 = s1 + c;
				else
					s1 = s1 + ' ';
			}
			s1 = s1.trim();
			if (s1.length() > 12)
				s1 = s1.substring(0, 12);
			long l = 0L;
			for (int j = 0; j < s1.length(); j++) {
				char c1 = s1.charAt(j);
				l *= 37L;
				if (c1 >= 'a' && c1 <= 'z')
					l += ((1 + c1) - 97);
				else if (c1 >= '0' && c1 <= '9')
					l += ((27 + c1) - 48);
			}
			
			return l;
		} catch(Exception ex) {
			System.out.println("Error encoding username " + s);
		}
		return -1;
	}
	
	public AccountManager(String host, String database, String user, String pass)
	{
		this.host = host;
		this.database = database;
		this.user = user;
		this.pass = pass;
	}
	
	public void deleteBannedPlayers()
		throws
			SQLException
	{
		String query = "SELECT rscd_players.user AS PlayerHash, rscd_players.username AS PlayerName, rscd_players.owner AS PlayerOwner, FROM_UNIXTIME( rscd_players.login_date ) AS PlayerLastLogin FROM `rscd_players` INNER JOIN users ON rscd_players.owner = users.id WHERE users.banned = '1' AND ( FROM_UNIXTIME( rscd_players.login_date ) <= ( NOW( ) - INTERVAL 14 DAY ))";
		try(Connection connection = DriverManager.getConnection("jdbc:mysql://"+host+"/"+database, user, pass))
		{
			try(Statement statement = connection.createStatement())
			{
				try(ResultSet rs = statement.executeQuery(query))
				{
					while(rs.next())
					{
						//deletePlayer(rs.getLong("PlayerHash"), connection);
					}
				}
			}
		}
	}
	
	public void deletePlayer(String username)
		throws
			SQLException
	{
		deletePlayer(usernameToHash(username));
	}

	public void deletePlayer(long username)
		throws
			SQLException
	{
		try(Connection connection = DriverManager.getConnection("jdbc:mysql://"+host+"/"+database, user, pass))
		{
			connection.setAutoCommit(false);
			deletePlayer(username, connection);
			connection.commit();
		}		
	}
	
	private void runUpdateQuery(Statement statement, String query)
		throws
			SQLException
	{
		int affectedRows = statement.executeUpdate(query);
		OUT.println("\t" + query + " - " + affectedRows + " affected rows");
	}
	
	public void deletePlayer(long username, Connection connection)
		throws
			SQLException
	{
		OUT.println("Deleting player: " + hashToUsername(username));
		try(Statement statement = connection.createStatement())
		{
			runUpdateQuery(statement, "DELETE FROM `rscd_players` WHERE `user` = '" + username + "'");
			runUpdateQuery(statement, "DELETE FROM `rscd_quests` WHERE `user` = '" + username + "'");
			runUpdateQuery(statement, "DELETE FROM `rscd_curstats` WHERE `user` = '" + username + "'");
			runUpdateQuery(statement, "DELETE FROM `rscd_experience` WHERE `user` = '" + username + "'");
			runUpdateQuery(statement, "DELETE FROM `rscd_invitems` WHERE `user` = '" + username + "'");
			runUpdateQuery(statement, "DELETE FROM `rscd_friends` WHERE `user` = '" + username + "' OR `friend` = '" + username + "'");
			runUpdateQuery(statement, "DELETE FROM `rscd_ignores` WHERE `user` = '" + username + "' OR `ignore` = '" + username + "'");
			runUpdateQuery(statement, "DELETE FROM `highscores` WHERE `user` = '" + username + "'");
			runUpdateQuery(statement, "DELETE FROM `screenshots` WHERE `character` = '" + username + "'");
		}

	}
	
	// Inherently buggy, do not uncomment and/or call...
/*	public void deleteAccount(long accountID)
		throws
			SQLException
	{
		try(Connection connection = DriverManager.getConnection("jdbc:mysql://"+host+"/"+database, user, pass))
		{
			connection.setAutoCommit(false);
			try(Statement statement = connection.createStatement())
			{
				try(ResultSet rs = statement.executeQuery("SELECT * FROM `users` WHERE `id` = '" + accountID + "'"))
				{
					if(rs.next())
					{
						try(Statement statement2 = connection.createStatement())
						{
							System.out.println("Removed " + statement2.executeUpdate("DELETE FROM `bans` WHERE `username` = '" + rs.getString("username") + "' LIMIT 1") + "rows from bans");
							System.out.println("Removed " + statement2.executeUpdate("DELETE FROM `messages` WHERE `sender_id` = '" + accountID + "' OR `owner` = '" + accountID + "'") + "rows from messages");
							System.out.println("Removed " + statement2.executeUpdate("DELETE FROM `online` WHERE `user_id` = '" + accountID + "'") + "rows from online");
							System.out.println("Removed " + statement2.executeUpdate("DELETE FROM `posts` WHERE `poster_id` = '" + accountID + "'") + "rows from posts");
							System.out.println("Removed " + statement2.executeUpdate("DELETE FROM `recovery_questions` WHERE `account` = '" + accountID + "'") + "rows from recovery_questions");
							System.out.println("Removed " + statement2.executeUpdate("DELETE FROM `screenshots` WHERE `account` = '" + accountID + "'") + "rows from screenshots");
							System.out.println("Removed " + statement2.executeUpdate("DELETE FROM `topics` WHERE `poster` = '" + accountID + "'") + "rows from topics");
							System.out.println("Removed " + statement2.executeUpdate("DELETE FROM `users` WHERE `id` = '" + accountID + "' LIMIT 1") + "rows from users");
							System.out.println("Removed " + statement2.executeUpdate("DELETE FROM `rscd_bank` WHERE `owner` = '" + accountID + "'") + "rows from rscd_bank");
						
							try(ResultSet rs2 = statement2.executeQuery("SELECT * FROM `rscd_players` WHERE `owner` = '" + accountID + "'"))
							{
								while(rs2.next())
								{
									deletePlayer(rs2.getLong("user"), connection);
								}
							}
						}
					}
				}
			}
			connection.commit();
		}
	}*/
}
