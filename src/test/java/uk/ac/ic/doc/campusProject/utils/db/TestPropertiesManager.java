package uk.ac.ic.doc.campusProject.utils.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import uk.ac.ic.doc.campusProject.utils.properties.PropertiesManager;

public class TestPropertiesManager {

	@Test
	public void testPropertiesLoad() throws FileNotFoundException, IOException {
		Properties prop = new Properties();
		prop.load(new FileInputStream(new File("C://Users//Adam//Documents//Imperial//Second_Term//Workspace//campus_guide//src//main//resources//otome.properties")));
		assert(prop.equals((Properties)PropertiesManager.getProperty("otome")));
		assert(prop.getProperty("admindb.dev.user").equals("user"));
	}

}
