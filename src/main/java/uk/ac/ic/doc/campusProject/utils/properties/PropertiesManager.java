package uk.ac.ic.doc.campusProject.utils.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesManager {
	static Logger log = Logger.getLogger(PropertiesManager.class);
	
	public static Properties getProperty(String machineId) {
		Properties prop = new Properties();
		try {
			prop.load(PropertiesManager.class.getResourceAsStream("/"+machineId+".properties"));
		} catch (FileNotFoundException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}

		return prop;
	}

}
