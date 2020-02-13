package com.openrsc.server.sql.query.logs;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.world.World;
import com.openrsc.server.sql.query.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * TODO: Change log to builder design (ex: new TradeLog().build())
 * Logic should never be involved in preparing statements other than passing data to the database
 *
 * @author openfrog
 */


public final class TradeLog extends Query {

	private String player1, player2, playerOnesOffer, playerTwosOffer, player1_ip, player2_ip;
	private List<Item> player1Offer, player2Offer;

	public TradeLog(World world, String player1, String player2, List<Item> player1Offer, List<Item> player2Offer, String player1_ip, String player2_ip) {
		super("INSERT INTO `" + world.getServer().getConfig().MYSQL_TABLE_PREFIX + "trade_logs`(`player1`, `player2`, `player1_items`, `player2_items`, `player1_ip`, `player2_ip`, `time`) VALUES(?, ?, ?, ?, ?, ?, ?)");
		this.player1 = player1;
		this.player2 = player2;
		this.player1Offer = player1Offer;
		this.player2Offer = player2Offer;
		this.player1_ip = player1_ip;
		this.player2_ip = player2_ip;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, player1);
		statement.setString(2, player2);
		statement.setString(3, playerOnesOffer);
		statement.setString(4, playerTwosOffer);
		statement.setString(5, player1_ip);
		statement.setString(6, player2_ip);
		statement.setLong(7, time);
		return statement;
	}

	@Override
	public Query build() {
		StringBuilder sb = new StringBuilder();

		for (Item i : player1Offer) {
			//System.out.println("Player 1 offered: " + i.getID());
			sb.append(i.getID()).append(":").append(i.getAmount()).append(",");
		}

		playerOnesOffer = sb.toString();
		sb = new StringBuilder();

		for (Item i : player2Offer) {
			//System.out.println("Player 2 offered: " + i.getID());
			sb.append(i.getID()).append(":").append(i.getAmount()).append(",");
		}

		playerTwosOffer = sb.toString();
		return this;
	}

}
