package com.openrsc.server.sql.query.logs;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.sql.query.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * staff username|action int|affected player username|time in epoch|staff x|staff y|affected x|affected y|staff ip|affected ip
 *
 * @author openfrog
 * <p>
 * 0 - Mute
 * 1 - Unmuted
 * 2 - Summon
 * 3 - Goto
 * 4 - Take
 * 5 - Put
 * 6 - kick
 * 7 - update
 * 8 - stopevent
 * 9 - setevent
 * 10 - blink
 * 11 - tban
 * 12 - putfatigue 100%
 * 13 - say
 * 14 - invisible
 * 15 - teleport
 * 16 - send
 * 17 - town
 * 18 - check
 * 19 - unban
 * 20 - ban (permanent)
 * 21 - globaldrop
 */

public final class StaffLog extends Query {

	private String staffUsername, affectedUsername, staffIp, affectedIp, extra;
	private int action, staffX, staffY, affectedX, affectedY;

	public StaffLog(Player staffMember, int action, Player affectedPlayer) {
		super("INSERT INTO `" + staffMember.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX
			+ "staff_logs`(`staff_username`, `action`, `affected_player`, `time`, `staff_x`, `staff_y`, `affected_x`, `affected_y`, `staff_ip`, `affected_ip`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		this.action = action;
		this.staffUsername = staffMember == null ? "" : staffMember.getUsername();
		this.affectedUsername = affectedPlayer == null ? "" : affectedPlayer.getUsername();
		this.staffX = staffMember == null ? 0 : staffMember.getX();
		this.staffY = staffMember == null ? 0 : staffMember.getY();
		this.affectedX = affectedPlayer == null ? 0 : affectedPlayer.getX();
		this.affectedY = affectedPlayer == null ? 0 : affectedPlayer.getY();
		this.staffIp = staffMember == null ? "" : staffMember.getCurrentIP();
		this.affectedIp = affectedPlayer == null ? "" : affectedPlayer.getCurrentIP();
	}

	public StaffLog(Player staffMember, int action, String extra) {
		super("INSERT INTO `" + staffMember.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX
			+ "staff_logs`(`staff_username`, `action`, `affected_player`, `time`, `staff_x`, `staff_y`, `affected_x`, `affected_y`, `staff_ip`, `affected_ip`, `extra`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		this.action = action;
		this.staffUsername = staffMember == null ? "" : staffMember.getUsername();
		this.staffX = staffMember == null ? 0 : staffMember.getX();
		this.staffY = staffMember == null ? 0 : staffMember.getY();
		this.staffIp = staffMember == null ? "" : staffMember.getCurrentIP();
		this.extra = extra == null ? "" : extra;
	}

	public StaffLog(Player staffMember, int action) {
		super("INSERT INTO `" + staffMember.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX
			+ "staff_logs`(`staff_username`, `action`, `affected_player`, `time`, `staff_x`, `staff_y`, `affected_x`, `affected_y`, `staff_ip`, `affected_ip`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		this.action = action;
		this.staffUsername = staffMember == null ? "" : staffMember.getUsername();
		this.staffX = staffMember == null ? 0 : staffMember.getX();
		this.staffY = staffMember == null ? 0 : staffMember.getY();
		this.staffIp = staffMember == null ? "" : staffMember.getCurrentIP();
	}

	public StaffLog(Player staffMember, int action, Player affectedPlayer, String extra) {
		super("INSERT INTO `" + staffMember.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX
			+ "staff_logs`(`staff_username`, `action`, `affected_player`, `time`, `staff_x`, `staff_y`, `affected_x`, `affected_y`, `staff_ip`, `affected_ip`, `extra`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		this.action = action;
		this.staffUsername = staffMember == null ? "" : staffMember.getUsername();
		this.affectedUsername = affectedPlayer == null ? "" : affectedPlayer.getUsername();
		this.staffX = staffMember == null ? 0 : staffMember.getX();
		this.staffY = staffMember == null ? 0 : staffMember.getY();
		this.affectedX = affectedPlayer == null ? 0 : affectedPlayer.getX();
		this.affectedY = affectedPlayer == null ? 0 : affectedPlayer.getY();
		this.staffIp = staffMember == null ? "" : staffMember.getCurrentIP();
		this.affectedIp = affectedPlayer == null ? "" : affectedPlayer.getCurrentIP();
		this.extra = extra == null ? "" : extra;
	}

	@Override
	public Query build() {
		return this;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, staffUsername);
		statement.setInt(2, action);
		statement.setString(3, affectedUsername);
		statement.setLong(4, time);
		statement.setInt(5, staffX);
		statement.setInt(6, staffY);
		statement.setInt(7, affectedX);
		statement.setInt(8, affectedY);
		statement.setString(9, staffIp);
		statement.setString(10, affectedIp);
		if (extra != null)
			statement.setString(11, extra);
		return statement;
	}

}
