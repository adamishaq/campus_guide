package uk.ac.ic.doc.campusProject.web.pages.mapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import uk.ac.ic.doc.campusProject.utils.db.DatabaseConnectionManager;
import uk.ac.ic.doc.campusProject.web.pages.AdminPage;

public class MappingLandingPage extends AdminPage {
	private static final long serialVersionUID = 1L;
    static Logger log = Logger.getLogger(MappingLandingPage.class);
	DropDownChoice<String> buildings;
	PageParameters parameters = new PageParameters();
	

	public MappingLandingPage() {
		setPageLocation("Mapping - Home");
		add(new BookmarkablePageLink<Void>("uploadMap", MapUploadPage.class));
		add(buildings = new DropDownChoice<String>("buildingSelect", new Model<String>(), populateBuildingList()) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}
			
			@Override
			protected void onSelectionChanged(String newSelection) {
				parameters.add("building", newSelection);
			}
			
		});
		add(new BookmarkablePageLink<Void>("editMap", MapUploadPage.class, parameters));
	}
	
	
	private List<String> populateBuildingList() {
		List<String> returnList = new ArrayList<String>();
		Connection conn = DatabaseConnectionManager.getConnection("live");
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT Name from Building");
			if (stmt.execute()) {
				ResultSet rs = stmt.getResultSet();
				while(rs.next()) {
					returnList.add(rs.getString("Name"));
				}
			}
			conn.close();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		return returnList;
	}

}
