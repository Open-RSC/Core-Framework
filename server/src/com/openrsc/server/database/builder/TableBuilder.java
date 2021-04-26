package com.openrsc.server.database.builder;

import java.util.LinkedHashMap;
import java.util.Map;

public class TableBuilder {
	private final String tableName;
	private String tableContents;
	private final Map<String, String> tableProperties;

	public TableBuilder(String tableName) {
		this(tableName, new LinkedHashMap<String, String>());
	}

	public TableBuilder(String tableName, Map<String, String> tableProperties) {
		this.tableName = tableName;
		this.tableProperties = tableProperties;
		this.tableContents = "";
	}

	public TableBuilder addColumn(String columnName, String columnDescriptor) {
		if (!columnName.trim().equals("") && !columnDescriptor.trim().equals("")) {
			this.tableContents += String.format("`%s` %s,\n", columnName, columnDescriptor);
		}
		return this;
	}

	public TableBuilder addPrimaryKey(String primaryKey) {
		if (!primaryKey.trim().equals("")) {
			this.tableContents += String.format("PRIMARY KEY (`%s`),\n", primaryKey);
		}
		return this;
	}

	public TableBuilder addKey(String key, String refKey) {
		if (!key.trim().equals("") && !refKey.trim().equals("")) {
			this.tableContents += String.format("KEY `%s` (`%s`),\n", key, refKey);
		}
		return this;
	}

	@Override
	public String toString() {
		String result = "";
		String propsAsString = "";
		for(Map.Entry<String, String> entry : tableProperties.entrySet()) {
			propsAsString += String.format("%s = %s\n", entry.getKey(), entry.getValue());
		}
		int indexLastComma = this.tableContents.lastIndexOf(",");
		result = String.format("CREATE TABLE `%s`\n(\n%s\n) %s", this.tableName, this.tableContents.substring(0, indexLastComma), propsAsString);
		return result;
	}
}
