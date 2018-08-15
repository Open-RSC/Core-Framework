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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;

import org.apache.mina.common.IoSession;
import org.openrsc.server.LoginResponse;
import org.openrsc.server.Server;
import org.openrsc.server.database.DefaultTransaction;
import org.openrsc.server.database.Transaction;
import org.openrsc.server.model.Bank;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Inventory;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.PlayerAppearance;
import org.openrsc.server.model.Point;
import org.openrsc.server.packetbuilder.RSCPacketBuilder;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;

import com.rscdaemon.scripting.quest.Quest;
import org.openrsc.server.Config;

import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import org.apache.commons.lang.StringEscapeUtils;


/**
 * A Transaction for handling player login requests.
 * 
 * @author Zilent
 * 
 * @versions 1.1, 2/1/2013
 * 
 * @since 3.0
 *
 */
public final class Login
	extends
		DefaultTransaction
{

	static
	{
		try
		{
			new DefaultTransaction(){
	
			@Override
			public boolean retryOnFatalError()
			{
				return false;
			}
	
			@Override
			public Integer call() throws Exception
			{
				Connection connection = super.getConnection();
				Statement statement = connection.createStatement();

				// Set all players offline on server start-up.
				statement.executeUpdate("UPDATE `rscd_players` SET `online` = '0' WHERE `online` != '0'");
				return Transaction.TRANSACTION_SUCCESS;
			}}.call();	
		}
		catch(Exception e)
		{
            // Should never happen -- only if:
            // A) the world loads successfully
            // B) after the world loads ( and before this is ran [ < 1 millisecond ] ), the database crashes
            // C) In which case, the server is terminated -- restart the server to fix, 1 in 1,000,000,000 shot to hit this.
            e.printStackTrace();
            throw new ExceptionInInitializerError();
		}
	}

	/// The maximum client width for a non-subscriber
	private final static int MAX_NON_SUB_WIDTH = 700;

	/// The session associated with the login request
	private final transient IoSession session;

	/// The username associated with the login request
	private final String username;

	/// The password associated with the login request
	private final String password;

	/// The client width associated with the login request
	private final int clientWidth;

	/**
	 * Constructs a Login
	 * 
	 * @param username the username associated with the login request
	 * 
	 * @param password the password associated with the login request
	 * 
	 * @param session the session associated with the login request
	 * 
	 * @param clientWidth the client width associated with the login request
	 * @param uid 
	 * 
	 * @throws NullPointerException if the username, password, 
	 * or session are null
	 * 
	 * @throws IllegalArgumentException if the provided client width is 
	 * less than 1
	 * 
	 */
	public Login(String username, String password, IoSession session, int clientWidth)
	{
		if(username == null)
		{
			throw new NullPointerException("The provided username must not be null");
		}
		if(password == null)
		{
			throw new NullPointerException("The provided password must not be null");
		}
		if(session == null)
		{
			throw new NullPointerException("The provided session must not be null");
		}
		if(clientWidth < 1)
		{
			throw new IllegalArgumentException("The provided client width must be greater than 0");
		}
		this.username = username;
		this.password = password;
		this.session = session;
		this.clientWidth = clientWidth;
	}

	@Override
	public String toString()
	{
		return "\"Login\" {user=" + username + "}";
	}

	/**
	 * Sends the provided response to the associated session
	 * 
	 * @param response the LoginResponse to send
	 * 
	 */
	private void sendLoginResponse(LoginResponse response)
	{
		session.write(new RSCPacketBuilder().setBare(true).addByte((byte)response.ordinal()).toPacket());
		if(response != LoginResponse.LOGIN_SUCCESS)
		{
			session.close();
		}
	}

	/**
	 * Loads the player from the database
	 * 
	 * @throws SQLException if a database error occurs
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void load(Statement statement)
		throws
			SQLException
	{
		try(ResultSet rs = statement.executeQuery("SELECT `owner`, `rscd_players`.`group_id`, `login_date`, `logout_date`, `login_ip`, `death_time`, `quests`, `combat`, `x`, `y`, `fatigue`, `combatstyle`, `block_chat`, `block_private`, `block_trade`, `block_duel`, `block_global`, `cameraauto`, `killnotify`, `onemouse`, `soundoff`, `showroof`, `autoscreenshot`, `combatwindow`, `haircolour`, `topcolour`, `trousercolour`, `skincolour`, `headsprite`, `bodysprite`, `male`, `skulled`, `kills`, `deaths`, `muted`, `store_employee`, `draynor_hopper`, `guild_hopper`, `banana_job`, `bananas_in_crate`, `rum_in_karamja_crate`, `rum_in_sarim_crate`, `has_traiborn_key`, `collecting_bones`, `bones`, `balls_of_wool`, `killed_skeleton`, `leela_has_key`, `lever_A_down`, `lever_B_down`, `lever_C_down`, `lever_D_down`, `lever_E_down`, `lever_F_down`, `lady_patches`, `on_crandor`, `has_map_piece`, `railing1`, `railing2`, `railing3`, `railing4`, `railing5`, `railing6`, `pipe`, `barrel`, `axle`, `shaft`, `poison`, `online`, `exp_attack`, `exp_defense`, `exp_strength`, `exp_hits`, `exp_ranged`, `exp_prayer`, `exp_magic`, `exp_cooking`, `exp_woodcut`, `exp_fletching`, `exp_fishing`, `exp_firemaking`, `exp_crafting`, `exp_smithing`, `exp_mining`, `exp_herblaw`, `exp_agility`, `exp_thieving`, `exp_runecrafting`, `cur_attack`, `cur_defense`, `cur_strength`, `cur_hits`, `cur_ranged`, `cur_prayer`, `cur_magic`, `cur_cooking`, `cur_woodcut`, `cur_fletching`, `cur_fishing`, `cur_firemaking`, `cur_crafting`, `cur_smithing`, `cur_mining`, `cur_herblaw`, `cur_agility`, `cur_thieving`, `cur_runecrafting`, (SELECT COUNT(`id`) FROM `messages` WHERE `showed` = '0' AND `show_message` = '1' AND `owner` = `rscd_players`.`owner`) AS `unread_messages`, (SELECT `time` FROM `recovery_questions` WHERE `account` = `rscd_players`.`owner` ORDER BY `time` DESC LIMIT 1) AS `recoveries_questions` FROM `rscd_players` JOIN `rscd_experience` ON `rscd_players`.`user` = `rscd_experience`.`user` JOIN `rscd_curstats` ON `rscd_players`.`user` = `rscd_curstats`.`user` JOIN `users` ON `rscd_players`.`owner` = `users`.`id` WHERE `rscd_players`.`user` = '" + DataConversions.usernameToHash(username) + "'"))
		{
			if(rs.next())
			{
                            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                            Date date = new Date();

				Player player = new Player(session);
				session.setAttachment(player);
				player.setUsername(username);				
				//RSCD_PLAYERS
				player.setAccount(rs.getInt("owner"));
				player.setGroupID(rs.getInt("group_id"));
				player.setLastLogin(rs.getLong("login_date"));
				player.setLastLogout(rs.getLong("logout_date"));
				player.setLastIP(rs.getString("login_ip"));
				player.setLastDeath(rs.getLong("death_time"));
				player.setLocation(Point.location(rs.getInt("x"), rs.getInt("y")), true);
				player.setFatigue(rs.getInt("fatigue"));
				player.setCombatStyle((byte)rs.getInt("combatstyle"));
				player.setPrivacySetting(0, rs.getInt("block_chat") == 0);
				player.setPrivacySetting(1, rs.getInt("block_private") == 0);
				player.setPrivacySetting(2, rs.getInt("block_trade") == 0);
				player.setPrivacySetting(3, rs.getInt("block_duel") == 0);
				player.setPrivacySetting(4, rs.getInt("block_global") == 0);
				player.setGameSetting(0, rs.getInt("cameraauto") == 0);
				player.setGameSetting(1, rs.getInt("onemouse") == 0);
				player.setGameSetting(2, rs.getInt("soundoff") == 0);
				player.setGameSetting(3, rs.getInt("showroof") == 0);
				player.setGameSetting(4, rs.getInt("autoscreenshot") == 0);
				player.setGameSetting(5, rs.getInt("killnotify") == 0);
				player.setCombatWindow(rs.getInt("combatwindow"));
				PlayerAppearance appearance = new PlayerAppearance((byte)rs.getInt("haircolour"), (byte)rs.getInt("topcolour"), (byte)rs.getInt("trousercolour"), (byte)rs.getInt("skincolour"), (byte)rs.getInt("headsprite"), (byte)rs.getInt("bodysprite"));
				player.setAppearance(appearance);
				
				player.setWornItems(player.getPlayerAppearance().getSprites());
				player.setMale(rs.getInt("male") == 1);
				player.addSkull(rs.getLong("skulled"));
				player.setKills(rs.getInt("kills"));
				player.setDeaths(rs.getInt("deaths"));
				player.setMuted(rs.getInt("muted"));
				player.setUnreadMessages(rs.getInt("unread_messages"));
				player.setRecoveryQuestions(rs.getLong("recoveries_questions"));	

				/**
				 *	Begin extra data
				 */
				player.setBones(rs.getInt("bones"));
				player.setStoreEmployee(rs.getInt("store_employee") == 1 ? true : false);
				player.grainInDraynorHopper(rs.getInt("draynor_hopper") == 1 ? true : false);
				player.grainInCookingGuildHopper(rs.getInt("guild_hopper") == 1 ? true : false);
				player.setBananaJob(rs.getInt("banana_job") == 1 ? true : false);
				player.setBananas(rs.getInt("bananas_in_crate"));
				player.setRumInKaramjaCrate(rs.getInt("rum_in_karamja_crate") == 1 ? true : false);
				player.setRumInSarimCrate(rs.getInt("rum_in_sarim_crate") == 1 ? true : false);
				player.setTraibornKey(rs.getInt("has_traiborn_key") == 1 ? true : false);
				player.setCollectingBones(rs.getInt("collecting_bones") == 1 ? true : false);
				player.setBones(rs.getInt("bones"));
				player.setBallsOfWool(rs.getInt("balls_of_wool"));
				player.setKilledSkeleton(rs.getInt("killed_skeleton") == 1 ? true : false);
				player.setLeelaKey(rs.getInt("leela_has_key") == 1 ? true : false);
				player.setLeverA(rs.getInt("lever_A_down") == 1 ? true : false);
				player.setLeverB(rs.getInt("lever_B_down") == 1 ? true : false);
				player.setLeverC(rs.getInt("lever_C_down") == 1 ? true : false);
				player.setLeverD(rs.getInt("lever_D_down") == 1 ? true : false);
				player.setLeverE(rs.getInt("lever_E_down") == 1 ? true : false);
				player.setLeverF(rs.getInt("lever_F_down") == 1 ? true : false);
				player.setCrandor(rs.getInt("on_crandor") == 1 ? true : false);
				player.setLadyPatches(rs.getInt("lady_patches"));
				player.setHasMap(rs.getInt("has_map_piece") == 1 ? true : false);
				player.loadFixedRailing(181, rs.getInt("railing1") == 1 ? true : false);
				player.loadFixedRailing(182, rs.getInt("railing2") == 1 ? true : false);
				player.loadFixedRailing(183, rs.getInt("railing3") == 1 ? true : false);
				player.loadFixedRailing(184, rs.getInt("railing4") == 1 ? true : false);
				player.loadFixedRailing(185, rs.getInt("railing5") == 1 ? true : false);
				player.loadFixedRailing(186, rs.getInt("railing6") == 1 ? true : false);
				player.setAxleFixed(rs.getInt("axle") == 1 ? true : false);
				player.setPipeFixed(rs.getInt("pipe") == 1 ? true : false);
				player.setShaftFixed(rs.getInt("shaft") == 1 ? true : false);
				player.setBarrelFixed(rs.getInt("barrel") == 1 ? true : false);
				player.setPoisonPower(rs.getInt("poison"));
				
				InputStream questStream = rs.getBinaryStream("quests");
				Map<Integer, Quest> quests = null;
				if(questStream == null)
				{
					quests = new TreeMap<>();
				}
				else
				{
					try
					{
						quests = (Map<Integer, Quest>)new ObjectInputStream(questStream).readObject();
					}
					catch (ClassNotFoundException | IOException e)
					{
						e.printStackTrace();
						sendLoginResponse(LoginResponse.MYSQL_ERROR);
						return;
					}
				}
				player.setScriptableQuests(quests);
				/**
				 *	End extra data
				 */

				//RSCD_EXPERIENCE
				for (int i = 0; i < Formulae.STAT_ARRAY.length; i++) {
					player.setCurStat(i, rs.getInt("cur_" + Formulae.statArray[i]));
					player.setExp(i, rs.getInt("exp_" + Formulae.statArray[i]));
					player.setMaxStat(i, Formulae.experienceToLevel((int) player.getExp(i)));
				}
				player.setCombatLevel(Formulae.getCombatLevel(player.getMaxStat(0), player.getMaxStat(1), player.getMaxStat(2), player.getMaxStat(3), player.getMaxStat(6), player.getMaxStat(5), player.getMaxStat(4)));
				try(ResultSet invItemRS = statement.executeQuery("SELECT * FROM `rscd_invitems` WHERE `user`='" + DataConversions.usernameToHash(username) + "' ORDER BY `slot` ASC"))
				{
					Inventory inv = new Inventory(player);
					while(invItemRS.next()) {
						InvItem invItem = new InvItem(invItemRS.getInt("id"), invItemRS.getLong("amount"));
						if (invItem.getAmount() != 0 && invItem.getDef() != null) {
							if(invItemRS.getInt("wielded") == 1) {
								if(invItem.isWieldable()) {
									invItem.setWield(true);
									player.updateWornItems(invItem.getWieldableDef().getWieldPos(), invItem.getWieldableDef().getSprite());								
								} else {
									System.out.println(dateFormat.format(date)+": Player: " + player.getUsername() + " is wielding an unwieldable item ID: " + invItemRS.getInt("id"));
								}
							}
							inv.add(invItem);
						} else {
							System.out.println(dateFormat.format(date)+": "+player.getUsername() + " has an invalid inventory item amount! (0)");
						}
					}
					player.setInventory(inv);
				}
				try(ResultSet bankRS = statement.executeQuery("SELECT * FROM `rscd_bank` WHERE `owner`='" + player.getAccount() + "' ORDER BY `slot` ASC"))
				{
					Bank bank = new Bank();
					while (bankRS.next()) {
						if (bankRS.getLong("amount") > 0)
							bank.add(new InvItem(bankRS.getInt("id"), bankRS.getLong("amount")));
					}
					player.setBank(bank);
				}
				try(ResultSet friendRS = statement.executeQuery("SELECT * FROM `rscd_friends` WHERE `user`='" + DataConversions.usernameToHash(player.getUsername()) + "'"))
				{
					while(friendRS.next())
					{
						player.addFriend(friendRS.getLong("friend"));
					}
				}
				try(ResultSet ignoreRS = statement.executeQuery("SELECT * FROM `rscd_ignores` WHERE `user`='" + DataConversions.usernameToHash(player.getUsername()) + "'"))
				{
					while(ignoreRS.next())
					{
						player.addIgnore(ignoreRS.getLong("ignore"));
					}
				}
				try(ResultSet questRS = statement.executeQuery("SELECT `quest_id`, `quest_stage`, `finished`, `quest_points` FROM `rscd_quests` WHERE `user` = '" + DataConversions.usernameToHash(player.getUsername()) + "'"))
				{
					int questPoints = 0;
					while(questRS.next())
					{
						player.addQuest(questRS.getInt("quest_id"), questRS.getInt("quest_stage"), (questRS.getInt("finished") == 0 ? false : true), questRS.getInt("quest_points"));
						questPoints += (questRS.getInt("finished") == 0) ? 0 : questRS.getInt("quest_points");
					}
					player.setQuestPoints(questPoints);
				}
				
				//3.9.2013 Login Patch
				statement.executeUpdate("UPDATE `rscd_players` SET `login_date` = '" + (player.getLastLogin() == 0L && player.isChangingAppearance() ? 0 : player.getCurrentLogin()) + "', `login_ip` = '" + player.getIP() + "', `online` = '1' WHERE `user` = '" + DataConversions.usernameToHash(player.getUsername()) + "'");
				//3.9.2013 Login Patch
				
				// 3.11.2013 Login re-patch
				// The 'online' field may lag behind in certain rare cases, but will always correct itself eventually.
				// This might increase the player count in the database by a few, but otherwise *should* be harmless
				 
				Server.getEngine().addPlayerToLoadQueue(player); // The game server can still deny the player's login at this point (multiple login attempts).
			}
			else
			{
				sendLoginResponse(LoginResponse.MYSQL_ERROR);
			}
		}
    }

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public synchronized Integer call()
		throws
			SQLException
	{
		Connection connection = super.getConnection();
		if(connection == null)
		{
			sendLoginResponse(LoginResponse.LOGIN_SERVER_OFFLINE);
			return Transaction.DATABASE_UNAVAILABLE;
		}

		Statement statement = connection.createStatement();
        long usernameHash   = DataConversions.usernameToHash(username);

		try(ResultSet rs = statement.executeQuery("SELECT `owner`, `rscd_players`.`pass`, `rscd_players`.`password_salt`, `login_ip`, `banned`, `delete_date`, `rscd_players`.`group_id` FROM `rscd_players` JOIN `users` ON `rscd_players`.`owner` = `users`.`id` WHERE `user` = '" + usernameHash + "'"))
		{
			if(rs.next() && rs.getInt("delete_date") == 0)
			{
				int banned = rs.getInt("banned");
                if(rs.getString("pass").equalsIgnoreCase(DataConversions.hashPassword(password, rs.getString("password_salt")))) {
					if(banned == 1 || (banned != 0 && (banned - DataConversions.getTimeStamp() > 0))) {
						sendLoginResponse(LoginResponse.CHARACTER_BANNED);
					}
                    load(statement);
				}
                else { // Password is wrong
					sendLoginResponse(LoginResponse.INVALID_CREDENTIALS);
				}
			}
            else { //User does not exist ... create it
                String salt = DataConversions.generateSalt();
                String pass = DataConversions.hashPassword(password, salt);
                String creation_ip = ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();
                int owner = -1;

                // Insert forum user ... not used.
                // TODO: Check for failure.
                Statement user = connection.createStatement();
                user.executeUpdate("INSERT INTO `users` (username) VALUES ('" + StringEscapeUtils.escapeSql(username) + "');", Statement.RETURN_GENERATED_KEYS);
                ResultSet userRs = user.getGeneratedKeys();
                if (userRs.next())
                        owner = userRs.getInt(1);
                else
                        sendLoginResponse(LoginResponse.MYSQL_ERROR);
                userRs.close();
                user.close();

                // Insert a character
                Statement players   = connection.createStatement();
                int playersResult   = players.executeUpdate(
                    "INSERT INTO `rscd_players` (owner,user,username,pass,password_salt,creation_date,creation_ip)"
                     + " VALUES ("
                        + owner + ","
                        + "'" + usernameHash + "',"// user
                        + "'" + StringEscapeUtils.escapeSql(username) + "',"// username
                        + "'" + pass + "'," // password
                        + "'" + StringEscapeUtils.escapeSql(salt) + "'," // salt
                        + Instant.now().getEpochSecond() + "," // creation_date
                        + "'" + creation_ip + "'" // creation_ip
                    + ");"
                );
                players.close();

                if(playersResult != 1)
                    sendLoginResponse(LoginResponse.MYSQL_ERROR);

                // Insert character stats
                // TODO: Check for failure
                Statement curstats  = connection.createStatement();
                curstats.executeUpdate("INSERT INTO `rscd_curstats` (user) VALUES ('" + usernameHash + "');");
                curstats.close();

                // Insert character stats
                // TODO: Check for failure
                Statement experience    = connection.createStatement();
                experience.executeUpdate("INSERT INTO `rscd_experience` (user) VALUES ('" + usernameHash + "');");
                experience.close();
                load(statement);
            }
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			sendLoginResponse(LoginResponse.MYSQL_ERROR);
			throw e;
		}
		return Transaction.TRANSACTION_SUCCESS;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean retryOnFatalError()
	{
		return false;
	}
}