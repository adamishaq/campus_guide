package uk.ac.ic.doc.campusProject.web.pages.mapping;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;

import uk.ac.ic.doc.campusProject.model.FloorPlanDao;
import uk.ac.ic.doc.campusProject.model.SerializableBufferedImage;
import uk.ac.ic.doc.campusProject.utils.db.DatabaseConnectionManager;
import uk.ac.ic.doc.campusProject.web.pages.AdminPage;

public class PlacesLandingPage extends AdminPage {
	private static final long serialVersionUID = 1L;
	DropDownChoice<String> buildings;
	Link<Void> editMap;
	String buildingSelected;
	
	public PlacesLandingPage() {
		setPageLocation("Places Landing Page");
		buildingSelected = null;
		editMap = new Link<Void>("editMap") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onClick() {
				setResponsePage(new RoomTagPage(populateFloorPlanDao(buildingSelected), null, false));
			}
			
		};
		editMap.setVisible(false);
		add(buildings = new DropDownChoice<String>("buildingSelect", new Model<String>(), populateBuildingList()) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}
			
			@Override
			protected void onSelectionChanged(String newSelection) {
				buildingSelected = newSelection;
				editMap.setVisible(true);
			}
			
		});
		add(editMap);
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
	
	private List<FloorPlanDao> populateFloorPlanDao(String building) {
		List<FloorPlanDao> returnPlans = new ArrayList<FloorPlanDao>();
		Connection conn = DatabaseConnectionManager.getConnection("live");
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Floor WHERE Building=?");
			stmt.setString(1, building);
			if (stmt.execute()) {
				ResultSet rs = stmt.getResultSet();
				while (rs.next()) {
					try {
						int floor = rs.getInt("Floor");
						String rsBuilding = rs.getString("Building");
						SerializableBufferedImage buffImage = new SerializableBufferedImage(ImageIO.read(rs.getBlob("Plan").getBinaryStream()));
						returnPlans.add(new FloorPlanDao(buffImage, rsBuilding, String.valueOf(floor)));
					} 
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			conn.close();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		return returnPlans;
	}


}
