package com.openrsc.server.database.patches;

import com.openrsc.server.database.DatabaseType;
import com.openrsc.server.database.GameDatabase;
import com.openrsc.server.database.JDBCDatabase;
import com.openrsc.server.database.JDBCDatabaseConnection;
import com.openrsc.server.database.impl.mysql.ScriptRunner;
import com.openrsc.server.database.queries.QueriesManager;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.StringReader;
import java.net.URI;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JDBCPatchApplier extends PatchApplier {
    private static final Logger LOGGER = LogManager.getLogger();
    private final JDBCDatabaseConnection connection;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;
    private final DatabaseType databaseType;
    private final PatchApplierQueries queries;
    private final String tablePrefix;
    private final GameDatabase gameDatabase;

    public JDBCPatchApplier(JDBCDatabase connection, String tablePrefix) {
    	this.gameDatabase = connection;
        this.connection = connection.getConnection();
        this.databaseType = this.connection.getDatabaseType();
        QueriesManager queriesManager = QueriesManager.getInstance(
                this.connection.getDatabaseType(),
                tablePrefix
        );
        queries = queriesManager.prefill(PatchApplierQueries.class);
        this.tablePrefix = tablePrefix;
    }

    @Override
    protected void markPatchExecuted(String fileName) {
        try {
            System.out.println("Marking " + fileName + " as executed...");
            String markPatchExecutedQuery = queries.PATCHES_MARK_PATCH_EXECUTED.fillParameters(
                    Pair.of("patchName", fileName),
                    Pair.of("runDate", LocalDate.now().format(dateTimeFormatter))
            );
            PreparedStatement statement = connection.prepareStatement(markPatchExecutedQuery);
            statement.executeUpdate();
            executePostPatchScripts(fileName);
        } catch (SQLException ex) {
            LOGGER.error("Failed to mark " + fileName + " as executed...");
            LOGGER.catching(ex);
        }
    }

	private void executePostPatchScripts(String fileName) {
		if (fileName.equals("2023_02_01_former_names.sql")) {
			LOGGER.info("Fixing capitalization of friends in Friend Lists...");
			int fixedCount = gameDatabase.queryFixCapitalizationFriendsList();
			LOGGER.info("Fixed the capitalization of " + fixedCount + " unique friends in Friend Lists");
		}
	}

	@Override
    protected Collection<String> getExecutedPatches() {
        try {
            ResultSet resultSet = connection.executeQuery(queries.PATCHES_GET_EXECUTED.get());
            Set<String> executedPatches = new HashSet<>();
            while (resultSet.next()) {
                String fileName = resultSet.getString("patch_name");
                executedPatches.add(fileName);
            }
            return executedPatches;
        } catch (Exception exception) {
            // Unable to fetch, assume the patches have never been run
            return Collections.emptyList();
        }
    }

    @Override
    protected boolean applyPatch(File file) {
        try {
            ScriptRunner scriptRunner = new ScriptRunner(
                    connection.getConnection(),
                    false,
                    true
            );
            String content = new String(Files.readAllBytes(file.toPath()));
            content = content.replaceAll("_PREFIX_", tablePrefix);
            scriptRunner.runScript(new StringReader(content));
        } catch (Exception ex) {
            LOGGER.error("Failed to apply patch " + file.getName(), ex);
            return false;
        }
        return true;
    }

    @Override
    protected URI getPatchDirectory() {
        return new File("database/" + databaseType.name().toLowerCase() + "/patches").toURI();
    }
}
