package DBCommands;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyDriver implements java.sql.Driver {

	static Logger log = Logger.getLogger(MyDriver.class.getName());
	@Override
	public boolean acceptsURL(String arg0) throws SQLException {
		// to see if the url is on this format "jdbc:xmldb://localhost "
		boolean jdbc = false, protocol = false, localhost = false, success = false;
		if(arg0.substring(0, 5).equals("jdbc:")){
			jdbc = true;
		}
		if(arg0.substring(5,11).equals("xmldb:")){
			protocol = true;
		}
		if(arg0.substring(11).equals("//localhost")){
			localhost = true;
		}
		success = jdbc && protocol && localhost;
		log.setLevel(Level.INFO);
		log.info("Check url: " + success);
		return success;
	}

	@Override
	public Connection connect(String arg0, Properties arg1) throws SQLException {
		 File dir = (File) arg1.get("path");
		 String path = dir.getAbsolutePath();
		 return new MyConnection(path);
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String arg0, Properties arg1) throws SQLException {
		 ArrayList<String> prop = new ArrayList<String>();
	     prop.add("username");
	     prop.add("password");
	     prop.add("path");
	     DriverPropertyInfo[] toReturn = new DriverPropertyInfo[prop.size()];
	     for (int i = 0; i < prop.size(); i++) {
	         toReturn[i] = new DriverPropertyInfo(prop.get(i), arg1.getProperty(prop.get(i)));
	     }
	     return toReturn;
	}
	@Override
	public int getMajorVersion() throws java.lang.UnsupportedOperationException {
		return 0;
	}

	@Override
	public int getMinorVersion() throws java.lang.UnsupportedOperationException{

		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException, java.lang.UnsupportedOperationException {

		return null;
	}

	@Override
	public boolean jdbcCompliant()throws java.lang.UnsupportedOperationException {

		return false;
	}

}
