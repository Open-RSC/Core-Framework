package com.openrsc.server.database.queries;

import com.openrsc.server.database.DatabaseType;
import com.openrsc.server.database.queries.xmldto.QueriesListDTO;
import com.openrsc.server.database.queries.xmldto.QueryDTO;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QueriesManager {
    private static final Map<String, String> QUERIES = new HashMap<>();
    private static final XStream X_STREAM = new XStream();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String PREFIX_MARKER = "_PREFIX_";

    private final String namespace;
    private final String tablePrefix;

    public static final String DATABASE_DIRECTORY = "database/";

    static {
		X_STREAM.addPermission(AnyTypePermission.ANY);
        X_STREAM.setMode(XStream.ID_REFERENCES);
        X_STREAM.alias("query", QueryDTO.class);
        X_STREAM.alias("queries", QueriesListDTO.class);
        X_STREAM.addImplicitCollection(QueriesListDTO.class, "queries");
        X_STREAM.registerConverter(new ToAttributedValueConverter(
                QueryDTO.class,
                X_STREAM.getMapper(),
                X_STREAM.getReflectionProvider(),
                X_STREAM.getConverterLookup(),
                "value"
        ));

        File databaseParentFolder = new File(DATABASE_DIRECTORY);
        File[] databaseTypes = databaseParentFolder.listFiles();
        // e.g. mysql, sqlite
        for (File databaseTypeFolder : databaseTypes) {
            if (databaseTypeFolder.isDirectory()) {
                String databaseKey = databaseTypeFolder.getName() + ".";
                File queriesFolder = new File(databaseTypeFolder, "queries/");
                initializeDatabaseQueries(databaseKey, queriesFolder);
            }
        }
    }

    private QueriesManager(DatabaseType databaseType, String tablePrefix) {
        this.namespace = databaseType.name().toLowerCase() + ".";
        this.tablePrefix = tablePrefix;
    }

    public String lookupQuery(String key) {
        return QUERIES.get(namespace + key).replaceAll(PREFIX_MARKER, tablePrefix);
    }

    public <T> T prefill(Class<T> type) {
        try {
            return prefill(type.getConstructor().newInstance());
        } catch (Exception ex) {
            LOGGER.error("Unable to fill queries in class: " + type, ex);
            return null;
        }
    }

    public <T> T prefill(T queries) {
        Arrays.stream(queries.getClass().getFields())
                .filter(field -> field.isAnnotationPresent(Named.class))
                .forEach(field -> {
                    String queryKey = field.getAnnotation(Named.class).value();
                    try {
                        String query = lookupQuery(queryKey);
                        field.set(queries, new NamedParameterQuery(query));
                    } catch (Exception e) {
                        LOGGER.warn(MessageFormat.format(
                                "Unable to register {0}.{1}: Query with key {2} does not exist",
                                Queries.class.getName(),
                                field.getName(),
                                queryKey
                        ));
                    }
                });

        return queries;
    }

    private static void initializeDatabaseQueries(String keyPrefix, File parentDir) {
        // Read all XML files in this directory, add to queries
        for (File child : parentDir.listFiles()) {
            if (child.isDirectory()) {
                // If it is a directory, crawl and repeat
                initializeDatabaseQueries(keyPrefix, child);
            } else {
                initializeQueriesFromFile(keyPrefix, child);
            }
        }
    }

    private static void initializeQueriesFromFile(String keyPrefix, File file) {
        try {
            QueriesListDTO list = (QueriesListDTO) X_STREAM.fromXML(file);
            list.getQueries().forEach(queryDTO -> {
                String key = keyPrefix + queryDTO.getKey();
                String value = queryDTO.getValue();

                if (QUERIES.containsKey(key)) {
                    throw new RuntimeException(
                            MessageFormat.format("Duplicate key found for queries: {0}", key)
                    );
                }
                QUERIES.put(key, value);
            });
        } catch (Exception ex) {
            LOGGER.catching(ex);
        }
    }

    public static QueriesManager getInstance(DatabaseType databaseType, String tablePrefix) {
        return new QueriesManager(databaseType, tablePrefix);
    }
}
