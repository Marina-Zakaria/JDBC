package DBCommands;

import java.sql.SQLException;


public class QueryController implements Database {

	private static final Exception SQLException = null;
	public DB adaptee;
	private Parser parser;

	@Override
	public String createDatabase(String databaseName, boolean dropIfExists) {
		databaseName = databaseName.toLowerCase();
		adaptee = new DB(databaseName);
		try {
			if (adaptee.exists() && dropIfExists) {
				executeStructureQuery("DROP DATABASE " + databaseName);
				executeStructureQuery("CREATE DATABASE " + databaseName);
			} else if(!adaptee.exists()) {
				executeStructureQuery("CREATE DATABASE " + databaseName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return databaseName;
	}

	@Override
	public boolean executeStructureQuery(String query) throws SQLException {
		query = query.toLowerCase();
		parser = new Parser(query);
		String command = parser.getCommand();
		String name = parser.getName();

		if (!parser.isTable()) {
			adaptee = new DB(name);
			if (command.equals("create")) {
				adaptee.createDatabase();
			} else if (command.equals("drop")) {
				adaptee.dropDatabase();
			} else {
				try {
					throw SQLException;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			if (command.equals("create")) {
				adaptee.createTable(name, parser.getTableContent());
			} else if (command.equals("drop")) {
				adaptee.dropTable(name);
			} else {
				try {
					throw SQLException;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return true;
	}

	@Override
	public Object[][] executeQuery(String query) throws SQLException {
		query = query.toLowerCase();
		parser = new Parser(query);
		String tableName = parser.getTableName();
		String ColNames = parser.getColoumnNames();

		if (query.contains("where")) {
			Condition condition = new Condition(parser.getCondition());
			if (ColNames.equals("*")) {
				return adaptee.selectAllFromTableWithCondition(tableName, condition);

			} else {
				String[] Columns = ColNames.split(",");
				return adaptee.selectFromTableWithCondition(tableName, Columns, condition);
			}
		} else {
			if (ColNames.equals("*")) {
				return adaptee.selectAllFromTable(tableName);
			} else {
				String[] Columns = ColNames.split(",");
				return adaptee.selectFromTable(tableName, Columns);
			}
		}
	}

	@Override
	public int executeUpdateQuery(String query) throws SQLException {
		query = query.toLowerCase();
		parser = new Parser(query);
		String tableName = parser.getTableName();
		int updatedRowsCount = 0;
		if(query.contains("delete")){
			if(query.contains("where")){
				Condition condition = new Condition(parser.getCondition());
				updatedRowsCount = adaptee.deleteFromTable(tableName, condition);
			}
			else{
				updatedRowsCount = adaptee.deleteAllFromTable(tableName);
			}
		}
		else if(query.contains("update")){
			if(query.contains("where")){
				Condition condition = new Condition(parser.getCondition());
				String[][] updates = parser.getUpdates();
				updatedRowsCount = adaptee.updateTable(tableName, updates, condition);
			} else {
				String[][] updates = parser.getUpdates();
				updatedRowsCount = adaptee.updateAllTable(tableName, updates);
			}
		} else {
			if(!parser.noColNames()){
				adaptee.insertIntoTable(tableName, parser.getColAndValues());
				updatedRowsCount = 1;
			} else{
				adaptee.insertIntoTableWithoutColumns(tableName, parser.getValues());
				updatedRowsCount = 1;
			}
		}
		return updatedRowsCount;
	}

}
