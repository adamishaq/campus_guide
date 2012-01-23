package uk.ac.ic.doc.campusProject.web.pages;

import org.apache.wicket.markup.html.WebPage;

import uk.ac.ic.doc.campusProject.web.panel.TopNavigationBar;

public class AdminPage extends WebPage {
	private static final long serialVersionUID = 1L;

	public AdminPage(String location) {
		add(new TopNavigationBar("navBar", location));
	}

}
