package uk.ac.ic.doc.campusProject.wicket;

import org.apache.wicket.markup.html.WebPage;

import uk.ac.ic.doc.campusProject.wicket.navigation.TopNavigationBar;

public class AdminPage extends WebPage {
	private static final long serialVersionUID = 1L;

	public AdminPage(String location) {
		add(new TopNavigationBar("navBar", location));
	}

}
