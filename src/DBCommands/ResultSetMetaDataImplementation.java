package DBCommands;

import java.io.IOException;
import java.sql.SQLException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;


public class ResultSetMetaDataImplementation {

	private DB db;
	private String[] columnsNames, columnsTypes;
	private String tableName = "";
	public ResultSetMetaDataImplementation(String databaseName, String tableName) {
		this.tableName = tableName;
		db = new DB(databaseName);
        try {
			columnsNames = db.getColumnsNames(tableName);
			columnsTypes = db.getColumnsTypes(tableName);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public int getColumnCount() throws SQLException {
		return columnsNames.length;
	}
	
	public String getColumnLabel(int column) throws SQLException {
		return columnsNames[column];
	}
	
	public String getColumnName(int column) throws SQLException {
		return columnsNames[column];
	}
	
	public int getColumnType(int column) throws SQLException {
		if(columnsTypes[column].equals("int")) {
			return 4;
		} else if(columnsTypes[column].equals("varchar")) {
			return 12;
		}
		return -1;
	}
	
	public String getTableName(int column) throws SQLException {
		return tableName;
	}
}
