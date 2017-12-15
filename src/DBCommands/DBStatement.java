package DBCommands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.management.RuntimeErrorException;


public class DBStatement {
	
	private ArrayList<String> batch = new ArrayList<>();
	private Connection connection;
	private Resultset resultSet;
	private int queryTimeout = 0;
	private String query;
	private QueryController controller = new QueryController();
	private String databaseName;
	
	public DBStatement(String databaseName){
		this.databaseName = databaseName;
		controller.adaptee = new  DB(databaseName);
	}
	
	
	public void addBatch(String sql) throws SQLException {
		batch.add(sql);
	}
	
	public void clearBatch() throws SQLException {
		batch.clear();
	}
	
	public void close() throws SQLException {
		//batch = null;
		connection = null;
		//resultSet.close();
	}
	
	public boolean execute(String arg0) throws SQLException {
		Parser parser = new Parser(arg0);
		String command = parser.getCommand();
		try{
			if(command.equals("create") || command.equals("drop")){
				controller.executeStructureQuery(arg0);
			}
			else if(command.equals("select")){
				controller.executeQuery(arg0);
			}
			else{
				controller.executeUpdateQuery(arg0);
			}
		}
		catch(Exception e){
			return false;
		}
		
		return true;
	}
	
	public int[] executeBatch() throws SQLException {
		int[] results = new int[batch.size()];
		Parser parser;
		for(int i = 0; i < batch.size(); i++){
			parser = new Parser(batch.get(i));
			String command = parser.getCommand();
			if(command == "create" || command == "select"){
				results[i] = 0;
				this.execute(batch.get(i));
			}
			else if(command == "delete" || command == "update" || command == "insert"){
				results[i] = controller.executeUpdateQuery(batch.get(i));
			}
		}
		return results;
	}
	
	public ResultSet executeQuery(String arg0) throws SQLException {
		this.setSelectQuery(arg0);
		resultSet = new Resultset(controller.executeQuery(arg0), databaseName);
		return resultSet;
	}
	
	public int executeUpdate(String arg0) throws SQLException {
		//throw new RuntimeErrorException(null,arg0);
		int result = controller.executeUpdateQuery(arg0);
		return result;
	}
	
	public Connection getConnection() throws SQLException {
		return connection;
	}
	
	public int getQueryTimeout() throws SQLException {
		return queryTimeout;
	}
	
	public void setQueryTimeout(int arg0) throws SQLException {
		queryTimeout = arg0;
		
	}
	
	public void setConnection(Connection connection) throws SQLException {
		this.connection = connection;
	}
	
	public void setSelectQuery(String query){
		this.query = query;
	}
	
	public String[] getColNames(){
		Parser parser = new Parser(query);
		String tempCol = parser.getColoumnNames();
		String[] colNames = tempCol.split(",");
		return colNames;
	}
}
