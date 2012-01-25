package uk.ac.ic.doc.campusProject.web.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;

import uk.ac.ic.doc.campusProject.web.pages.mapping.MappingLandingPage;
import uk.ac.ic.doc.campusProject.web.panel.TopNavigationBar;

public abstract class AdminPage extends WebPage {
	private static final long serialVersionUID = 1L;
	
	private String pageLocation = "Home";
	private TopNavigationBar navBar; 

	public AdminPage() {
		add(new Label("title", new PropertyModel<String>(this, "pageLocation")));
		add(new Label("location", new PropertyModel<String>(this, "pageLocation")));
		add(navBar = new TopNavigationBar("navBar"));
		add(new BookmarkablePageLink<Void>("home", HomePage.class));
		add(new BookmarkablePageLink<Void>("mapping", MappingLandingPage.class));
	}
	

	public final String getPageLocation() {
		return pageLocation;
	}
	
	public final void setPageLocation(String location) {
		this.pageLocation = location;
	}

}
