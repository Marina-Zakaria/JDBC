package DBCommands;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ConnectionManager {
	 List<MyConnection> availableConnections = new ArrayList<MyConnection>();

	 public ConnectionManager()
	 {
	  initializeConnectionPool();
	 }

	 private void initializeConnectionPool()
	 {
	  while(!checkIfConnectionPoolIsFull())
	  {
	   availableConnections.add(createNewConnectionForPool());
	  }
	 }

	 private synchronized boolean checkIfConnectionPoolIsFull()
	 {
	  final int MAX_POOL_SIZE = Configuration.getInstance().DB_MAX_CONNECTIONS;

	  if(availableConnections.size() < MAX_POOL_SIZE)
	  {
	   return false;
	  }

	  return true;
	 }

	 //Creating a connection
	 private MyConnection createNewConnectionForPool()
	 {
	  Configuration config = Configuration.getInstance();
	  try {
	   Class.forName(config.DB_DRIVER);
	   MyConnection connection = (MyConnection) DriverManager.getConnection(
	     config.DB_URL, config.DB_USER_NAME, config.DB_PASSWORD);
	   return connection;
	  } catch (ClassNotFoundException e) {
	   e.printStackTrace();
	  } catch (SQLException e) {
	   e.printStackTrace();
	  }
	  return null;

	 }

	 public synchronized MyConnection getConnectionFromPool()
	 {
	  MyConnection connection = null;
	  if(availableConnections.size() > 0)
	  {
	   connection = (MyConnection) availableConnections.get(0);
	   availableConnections.remove(0);
	  }
	  return connection;
	 }

	 public synchronized void returnConnectionToPool(MyConnection connection)
	 {
	  availableConnections.add(connection);
	 }
	
}
