package uk.ac.ic.doc.campusProject.wicket;

import org.apache.wicket.protocol.http.WebApplication;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see uk.ac.ic.doc.Start#main(String[])
 */
public class WicketApplication extends WebApplication { 
	
	
	@Override
	public Class<HomePage> getHomePage() {
		return HomePage.class;
	}
	
	@Override
	public void init() {
		super.init();
	}
}
