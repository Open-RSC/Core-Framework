package com.openrsc.server.database;

import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.database.builder.TableBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;

public class DatabaseUpgrades {
	private static final Logger LOGGER = LogManager.getLogger();

	// This is used to modify the database when new features may break SQL compatibility while upgrading the server
	// Please date your functions!
	public static boolean checkForDatabaseStructureChanges(GameDatabase db, ServerConfiguration conf) {
		try {
			rsc235Upgrade(db);
			playerTransfersUpgrade(db);
			xpRolloverUpgrade(db, conf);
			messageLoggingLengthUpgrade(db);
			return true;
		} catch (GameDatabaseException e) {
			LOGGER.error(e.toString());
			return false;
		}
	}

	// For servers before 2020-09-23 update
	private static void rsc235Upgrade(GameDatabase db) throws GameDatabaseException {
		if (!db.columnExists("logins", "clientVersion")) {
			db.addColumn("logins", "clientVersion", "INT (11)");
		}
	}

	// For servers before 2020-10-28 update
	private static void playerTransfersUpgrade(GameDatabase db) throws GameDatabaseException {
		if (!db.columnExists("players", "transfer")) {
			db.addColumn("players", "transfer", "INT (11)");
		}
	}

	// For servers before 2021-03-14 update
	private static void xpRolloverUpgrade(GameDatabase db, ServerConfiguration conf) throws GameDatabaseException {
		if (db.columnExists("experience", "attack")
			&& db.columnType("experience", "attack").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "attack", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "defense")
			&& db.columnType("experience", "defense").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "defense", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "strength")
			&& db.columnType("experience", "strength").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "strength", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "hits")
			&& db.columnType("experience", "hits").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "hits", "INT (9) NOT NULL DEFAULT 4616");
		}
		if (db.columnExists("experience", "ranged")
			&& db.columnType("experience", "ranged").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "ranged", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "prayer")
			&& db.columnType("experience", "prayer").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "prayer", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "magic")
			&& db.columnType("experience", "magic").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "magic", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "cooking")
			&& db.columnType("experience", "cooking").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "cooking", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "woodcut")
			&& db.columnType("experience", "woodcut").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "woodcut", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "fletching")
			&& db.columnType("experience", "fletching").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "fletching", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "fishing")
			&& db.columnType("experience", "fishing").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "fishing", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "firemaking")
			&& db.columnType("experience", "firemaking").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "firemaking", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "crafting")
			&& db.columnType("experience", "crafting").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "crafting", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "smithing")
			&& db.columnType("experience", "smithing").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "smithing", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "mining")
			&& db.columnType("experience", "mining").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "mining", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "herblaw")
			&& db.columnType("experience", "herblaw").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "herblaw", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "agility")
			&& db.columnType("experience", "agility").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "agility", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "thieving")
			&& db.columnType("experience", "thieving").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "thieving", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "runecraft")
			&& db.columnType("experience", "runecraft").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "runecraft", "INT (9) NOT NULL DEFAULT 0");
		}
		if (db.columnExists("experience", "harvesting")
			&& db.columnType("experience", "harvesting").toLowerCase().contains("unsigned")) {
			db.modifyColumn("experience", "harvesting", "INT (9) NOT NULL DEFAULT 0");
		}
		if (!db.tableExists("maxstats")) {
			TableBuilder maxStatsTable = new TableBuilder("maxstats", new LinkedHashMap<String, String>() {{
				put("ENGINE", "InnoDB");
				put("DEFAULT CHARSET", "utf8");
			}}).addColumn("playerID", "int(10) UNSIGNED NOT NULL")
				.addColumn("attack", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1")
				.addColumn("defense", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1")
				.addColumn("strength", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1")
				.addColumn("hits", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1")
				.addColumn("ranged", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1")
				.addColumn("prayer", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1")
				.addColumn("magic", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1")
				.addColumn("cooking", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1")
				.addColumn("woodcut", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1")
				.addColumn("fletching", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1")
				.addColumn("fishing", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1")
				.addColumn("firemaking", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1")
				.addColumn("crafting", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1")
				.addColumn("smithing", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1")
				.addColumn("mining", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1")
				.addColumn("herblaw", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1")
				.addColumn("agility", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1")
				.addColumn("thieving", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1");
			if (conf.WANT_RUNECRAFT) {
				maxStatsTable.addColumn("runecraft", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1");
			}
			if (conf.WANT_HARVESTING) {
				maxStatsTable.addColumn("harvesting", "tinyint(3) UNSIGNED NOT NULL DEFAULT 1");
			}
			maxStatsTable.addPrimaryKey("playerID")
				.addKey("playerID", "playerID");
			db.addTable(maxStatsTable.toString());
		}
		if (!db.tableExists("capped_experience")) {
			TableBuilder cappedXpTable = new TableBuilder("capped_experience", new LinkedHashMap<String, String>() {{
				put("ENGINE", "InnoDB");
				put("DEFAULT CHARSET", "utf8");
			}}).addColumn("playerID", "int(10) UNSIGNED NOT NULL")
				.addColumn("attack", "int(10) UNSIGNED")
				.addColumn("defense", "int(10) UNSIGNED")
				.addColumn("strength", "int(10) UNSIGNED")
				.addColumn("hits", "int(10) UNSIGNED")
				.addColumn("ranged", "int(10) UNSIGNED")
				.addColumn("prayer", "int(10) UNSIGNED")
				.addColumn("magic", "int(10) UNSIGNED")
				.addColumn("cooking", "int(10) UNSIGNED")
				.addColumn("woodcut", "int(10) UNSIGNED")
				.addColumn("fletching", "int(10) UNSIGNED")
				.addColumn("fishing", "int(10) UNSIGNED")
				.addColumn("firemaking", "int(10) UNSIGNED")
				.addColumn("crafting", "int(10) UNSIGNED")
				.addColumn("smithing", "int(10) UNSIGNED")
				.addColumn("mining", "int(10) UNSIGNED")
				.addColumn("herblaw", "int(10) UNSIGNED")
				.addColumn("agility", "int(10) UNSIGNED")
				.addColumn("thieving", "int(10) UNSIGNED");
			if (conf.WANT_RUNECRAFT) {
				cappedXpTable.addColumn("runecraft", "int(10) UNSIGNED");
			}
			if (conf.WANT_HARVESTING) {
				cappedXpTable.addColumn("harvesting", "int(10) UNSIGNED");
			}
			cappedXpTable.addPrimaryKey("playerID")
				.addKey("playerID", "playerID");
			db.addTable(cappedXpTable.toString());
		}
	}

	// For servers before 2021-03-24 update
	private static void messageLoggingLengthUpgrade(GameDatabase db) throws GameDatabaseException {
		if (db.columnType("chat_logs", "message").toLowerCase().contains("255")) {
			db.modifyColumn("chat_logs", "message", "VARCHAR (5000) NOT NULL DEFAULT 0");
		}
		if (db.columnType("private_message_logs", "message").toLowerCase().contains("255")) {
			db.modifyColumn("private_message_logs", "message", "VARCHAR (5000) NOT NULL DEFAULT 0");
		}
	}

}
