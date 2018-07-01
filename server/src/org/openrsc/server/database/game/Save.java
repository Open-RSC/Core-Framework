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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import org.openrsc.server.database.DefaultTransaction;
import org.openrsc.server.database.Transaction;
import org.openrsc.server.database.TransactionListener;
import org.openrsc.server.model.Bank;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Inventory;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.PlayerAppearance;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;

import com.rscdaemon.scripting.quest.Quest;

public class Save
	extends
		DefaultTransaction
{
	
	public final class DefaultSaveListener
		implements
			TransactionListener
	{
		@Override
		public void onSuccess()
		{
			System.out.println("Saved: " + DataConversions.hashToUsername(usernameHash));
		}

		@Override
		public void onFailure(int code)
		{
			System.out.println("Failed to save: " + DataConversions.hashToUsername(usernameHash));
		}
	}
	
	@Override
	public boolean equals(Object rhs)
	{
		return ((Save)rhs).usernameHash == usernameHash;
	}
	
	@Override
	public String toString()
	{
		return "\"Save\" {user=" + DataConversions.hashToUsername(usernameHash) + "}";
	}
	
	private final int[] wornItems;
	private Bank bank;
	private Inventory inventory;
	private PlayerAppearance playerAppearance;
	private boolean cameraAuto, oneMouse, soundOff, showRoof, autoScreenshot, killNotify, online, male, storeEmployee, draynorHopper, guildHopper, bananaJob, karamjaRum, sarimRum, killedSkeleton, hasTraibornKey, leelaHasKey, onCrandor, hasMap;
	private boolean[] privacySettings;
	private long skulled, owner, usernameHash;
	private ArrayList<Long> friends;
	private ArrayList<Long> ignores;
	private String currentIP;
	private int skillTotal, x, y, kills, deaths, combat, combatStyle, combatWindow, bones, ballsOfWool, bananasInCrate, leverA, leverB, leverC, leverD, leverE, leverF, ladyPatches, poison;
	private double fatigue;
	private long logoutDate;
	private long deathTime;
	private int[] curStats;
	double[] curExp;
	private boolean railing1, railing2, railing3, railing4, railing5, railing6, axle, shaft, pipe, barrel;
	private Map<Integer, Quest> quests;
	
	public Save(Player player)
	{
		quests = player.getScriptableQuests();
		this.logoutDate = player.getLogoutDate();
		this.deathTime = player.getDeathTime();
		this.wornItems = player.getWornItems();
		this.hasMap = player.hasMapPieceA();
		this.ladyPatches = player.getLadyPatches();
		this.onCrandor = player.onCrandor();
		this.leelaHasKey = player.leelaHasKey();
		this.bones = player.getBones();
		this.ballsOfWool = player.getBallsOfWool();
		this.bananasInCrate = player.getBananas();
		this.storeEmployee = player.isGroceryStoreEmployee();
		this.draynorHopper = player.isGrainInDraynorHopper();
		this.guildHopper = player.isGrainInCookingGuildHopper();
		this.bananaJob = player.hasBananaJob();
		this.karamjaRum = player.rumInKaramjaCrate();
		this.sarimRum = player.rumInSarimCrate();
		this.killedSkeleton = player.hasKilledSkeleton();
		this.hasTraibornKey = player.hasTraibornKey();
		this.cameraAuto = player.getGameSetting(0);
		this.oneMouse = player.getGameSetting(1);
		this.soundOff = player.getGameSetting(2);
		this.showRoof = player.getGameSetting(3);
		this.autoScreenshot = player.getGameSetting(4);
		this.killNotify = player.getGameSetting(5);
		this.friends = new ArrayList<Long>();
		this.ignores = new ArrayList<Long>();
		this.owner = player.getAccount();
		this.usernameHash = DataConversions.usernameToHash(player.getUsername());
		this.currentIP = player.getIP();
		this.fatigue = player.getFatigue();
		this.kills = player.getKills();
		this.deaths = player.getDeaths();
		this.x = player.getX();
		this.y = player.getY();
		this.online = player.loggedIn();
		this.bank = player.getBank();
		this.inventory = new Inventory();
		for(InvItem item : player.getInventory().getItems())
		{
			inventory.add(item);
		}
		this.playerAppearance = player.getPlayerAppearance();
		this.combat = player.getCombatLevel();
		this.combatStyle = player.getCombatStyle();
		this.combatWindow = player.getCombatWindow();
		this.privacySettings = player.getPrivacySettings();
		this.curStats = player.getCurStats();
		this.curExp = player.getExps();
		this.male = player.isMale();
		this.skulled = player.getSkullTime();
		this.skillTotal = player.getSkillTotal();
		this.leverA = player.leverADown() ? 1 : 0;
		this.leverB = player.leverBDown() ? 1 : 0;
		this.leverC = player.leverCDown() ? 1 : 0;
		this.leverD = player.leverDDown() ? 1 : 0;
		this.leverE = player.leverEDown() ? 1 : 0;
		this.leverF = player.leverFDown() ? 1 : 0;
		this.railing1 = player.isRailingFixed(181);
		this.railing2 = player.isRailingFixed(182);
		this.railing3 = player.isRailingFixed(183);
		this.railing4 = player.isRailingFixed(184);
		this.railing5 = player.isRailingFixed(185);
		this.railing6 = player.isRailingFixed(186);
		this.axle = player.isAxleFixed();
		this.shaft = player.isShaftFixed();
		this.barrel = player.isBarrelFixed();
		this.pipe = player.isPipeFixed();
		this.poison = player.getPoison();
		for (long friend : player.getFriendList())
			this.friends.add(friend);
		for (long ignore : player.getIgnoreList())
			this.ignores.add(ignore);
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
		
		connection.setAutoCommit(false);
		
		try
		{
			Statement statement = connection.createStatement();
			//RSCD_FRIENDS
			statement.executeUpdate("DELETE FROM `rscd_friends` WHERE `user` = '" + usernameHash + "'");
			if (friends.size() > 0) {
				StringBuilder builder = new StringBuilder("INSERT INTO `rscd_friends` (`user`, `friend`) VALUES ");
				for (long friend : friends)
					builder.append("('" + usernameHash + "', '" + friend + "'), ");
				statement.executeUpdate(builder.substring(0, builder.length() - 2));
			}

			//RSCD_IGNORES
			statement.executeUpdate("DELETE FROM `rscd_ignores` WHERE `user` = '" + usernameHash + "'");
			if (ignores.size() > 0) {
				StringBuilder builder = new StringBuilder("INSERT INTO `rscd_ignores` (`user`, `ignore`) VALUES ");
				for (long ignore : ignores)
					builder.append("('" + usernameHash + "', '" + ignore + "'), ");
				statement.executeUpdate(builder.substring(0, builder.length() - 2));
			}

			//RSCD_BANK
			statement.executeUpdate("DELETE FROM `rscd_bank` WHERE `owner` = '" + owner + "'");
			if (bank.getItems().size() > 0) {
				StringBuilder builder = new StringBuilder("INSERT INTO `rscd_bank`(`owner`, `id`, `amount`, `slot`) VALUES ");
				int slot = 0;
				for (InvItem item : bank.getItems())
					builder.append("('" + owner + "', '" + item.getID() + "', '" + item.getAmount() + "', '" + (slot++) + "'), ");
				statement.executeUpdate(builder.substring(0, builder.length() - 2));
			}

			//RSCD_INVITEMS
			statement.executeUpdate("DELETE FROM `rscd_invitems` WHERE `user` = '" + usernameHash + "'");
			if (inventory.size() > 0) {
				StringBuilder builder = new StringBuilder("INSERT INTO `rscd_invitems` (`user`, `id`, `amount`, `wielded`, `slot`) VALUES ");
				int slot = 0;
				for (InvItem item : inventory.getItems())
				{
					builder.append("('" + usernameHash + "', '" + item.getID() + "', '" + item.getAmount() + "', '" + (item.isWielded() ? 1 : 0) + "', '" + (slot++) + "'), ");
				}
				statement.executeUpdate(builder.substring(0, builder.length() - 2));
			}
			
			PreparedStatement ps = connection.prepareStatement("UPDATE `rscd_players` SET `quests` = ? WHERE `user` = '" + usernameHash + "'");
			ps.setObject(1, quests);
			ps.executeUpdate();
			//RSCD_PLAYERS
			statement.executeUpdate(new StringBuilder("UPDATE `rscd_players` SET ")
			.append("`combat` = '").append(combat)
			.append("', `skill_total` = '").append(skillTotal)
			.append("', `x` = '" + x)
			.append("', `y` = '" + y)
			.append("', `fatigue` = '" + fatigue)
			.append("', `haircolour` = '" + playerAppearance.getHairColour())
			.append("', `topcolour` = '" + playerAppearance.getTopColour())
			.append("', `trousercolour` = '" + playerAppearance.getTrouserColour())
			.append("', `skincolour` = '" + playerAppearance.getSkinColour())
			.append("', `headsprite` = '" + playerAppearance.getSprite(0))
			.append("', `bodysprite` = '" + playerAppearance.getSprite(1))
			.append("', `male` = '" + (male ? 1 : 0))
			.append("', `skulled` = '" + skulled)
			.append("', `combatstyle` = '" + combatStyle)
			.append("', `block_chat` = '" + (privacySettings[0] ? 0 : 1))
			.append("', `block_private` = '" + (privacySettings[1] ? 0 : 1))
			.append("', `block_trade` = '" + (privacySettings[2] ? 0 : 1))
			.append("', `block_duel` = '" + (privacySettings[3] ? 0 : 1))
			.append("', `block_global` = '" + (privacySettings[4] ? 0 : 1))
			.append("', `cameraauto` = '" + (cameraAuto ? 0 : 1))
			.append("', `onemouse` = '" + (oneMouse ? 0 : 1))
			.append("', `soundoff` = '" + (soundOff ? 0 : 1))
			.append("', `showroof` = '" + (showRoof ? 0 : 1))
			.append("', `autoscreenshot` = '" + (autoScreenshot ? 0 : 1))
			.append("', `killnotify` = '" + (killNotify ? 0 : 1))
			.append("', `combatwindow` = '" + combatWindow)
			.append("', `login_ip` = '" + currentIP)
			.append("', `kills` = '" + kills)
			.append("', `deaths` = '" + deaths)
			.append("', `online` = '" + (online ? 1 : 0))
			.append("', `store_employee` = '" + (storeEmployee ? 1 : 0))
			.append("', `draynor_hopper` = '" + (draynorHopper ? 1 : 0))
			.append("', `guild_hopper` = '" + (guildHopper ? 1 : 0))
			.append("', `banana_job` = '" + (bananaJob ? 1 : 0))
			.append("', `bananas_in_crate` = '" + bananasInCrate)
			.append("', `rum_in_karamja_crate` = '" + (karamjaRum ? 1 : 0))
			.append("', `rum_in_sarim_crate` = '" + (sarimRum ? 1 : 0))
			.append("', `has_traiborn_key` = '" + (hasTraibornKey ? 1 : 0))
			.append("', `collecting_bones` = '" + bones)
			.append("', `balls_of_wool` = '" + ballsOfWool)
			.append("', `killed_skeleton` = '" + (killedSkeleton ? 1 : 0))
			.append("', `leela_has_key` = '" + (leelaHasKey ? 1 : 0))
			.append("', `lever_A_down` = ' " + leverA)
			.append("', `lever_B_down` = ' " + leverB)
			.append("', `lever_C_down` = ' " + leverC)
			.append("', `lever_D_down` = ' " + leverD)
			.append("', `lever_E_down` = ' " + leverE)
			.append("', `lever_F_down` = ' " + leverF)
			.append("', `on_crandor` = ' " + (onCrandor ? 1 : 0))
			.append("', `lady_patches` = ' " + ladyPatches)
			.append("', `has_map_piece` = ' " + (hasMap ? 1 : 0))
			.append("', `railing1` = ' " + (railing1 ? 1 : 0))
			.append("', `railing2` = ' " + (railing2 ? 1 : 0))
			.append("', `railing3` = ' " + (railing3 ? 1 : 0))
			.append("', `railing4` = ' " + (railing4 ? 1 : 0))
			.append("', `railing5` = ' " + (railing5 ? 1 : 0))
			.append("', `railing6` = ' " + (railing6 ? 1 : 0))
			.append("', `pipe` = ' " + (pipe ? 1 : 0))
			.append("', `axle` = ' " + (axle ? 1 : 0))
			.append("', `shaft` = ' " + (shaft ? 1 : 0))
			.append("', `barrel` = ' " + (barrel ? 1 : 0))
			.append("', `poison` = ' " + (poison))
			.append("' WHERE `user` = '" + usernameHash + "'").toString());

			//RSCD_EXPERIENCE
			StringBuilder builder = new StringBuilder("UPDATE `rscd_experience` SET ");
			for (int i = 0; i < Formulae.STAT_ARRAY.length; i++)
				builder.append("`exp_" + Formulae.STAT_ARRAY[i] + "` = '" + curExp[i] + "', ");
			statement.executeUpdate(builder.substring(0, builder.length() - 2) + " WHERE `user` = '" + usernameHash + "'");
			builder.setLength(0);
			//RSCD_CURSTATS
			builder.append("UPDATE `rscd_curstats` SET ");
			for (int i = 0; i < Formulae.STAT_ARRAY.length; i++)
				builder.append("`cur_" + Formulae.STAT_ARRAY[i] + "` = '" + curStats[i] + "', ");
			statement.executeUpdate(builder.substring(0, builder.length() - 2) + " WHERE `user` = '" + usernameHash + "'");
			
			connection.commit();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			connection.rollback();
			throw e;
		}
		finally
		{
			connection.setAutoCommit(true);
		}
		return Transaction.TRANSACTION_SUCCESS;
	}

	@Override
	public boolean retryOnFatalError()
	{
		return true;
	}

}
