package uk.ac.ic.doc.campusProject.web.pages.mapping;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import uk.ac.ic.doc.campusProject.web.pages.AdminPage;

public class MappingLandingPage extends AdminPage {
	private static final long serialVersionUID = 1L;

	public MappingLandingPage() {
		setPageLocation("Mapping - Home");
		add(new BookmarkablePageLink<Void>("uploadMap", MapUploadPage.class));
	}
	
	// Allow user to choose between adding new maps, or modifying existing maps

}
