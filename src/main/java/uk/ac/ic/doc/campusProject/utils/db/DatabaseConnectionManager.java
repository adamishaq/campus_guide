package uk.ac.ic.doc.campusProject.utils.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import uk.ac.ic.doc.campusProject.utils.properties.PropertiesManager;

public class DatabaseConnectionManager {
	static Logger log = Logger.getLogger(DatabaseConnectionManager.class);
	
	public static Connection getConnection(String type) {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			log.error(e);
		}
		
		Connection conn = null;
		Properties prop = PropertiesManager.getProperty("otome");
		try {
			if (type.equals("live")) {
				conn = DriverManager.getConnection(prop.getProperty("admindb.live.connecturi"), prop.getProperty("admindb.live.user"), prop.getProperty("admindb.live.pass"));
			} else if (type.equals("dev")) {
				conn = DriverManager.getConnection(prop.getProperty("admindb.dev.connecturi"), prop.getProperty("admindb.dev.user"), prop.getProperty("admindb.dev.pass"));
			}
			return conn;
		} catch (SQLException e) {
			log.error(e);
			e.printStackTrace();
			return null;
		}	
			
	}

}
