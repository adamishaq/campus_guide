package uk.ac.ic.doc.campusProject.web.pages.mapping.modal;

import java.awt.geom.Point2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.cycle.RequestCycle;

import uk.ac.ic.doc.campusProject.utils.db.DatabaseConnectionManager;
import uk.ac.ic.doc.campusProject.web.pages.CallbackUrlInjector;

public class MapGeoTagModal extends WebPage {

	static Logger log = Logger.getLogger(MapGeoTagModal.class);	
	private static final long serialVersionUID = 1L;
	private WebMarkupContainer dummyContainer;
	List<Point2D.Double> points = new ArrayList<Point2D.Double>();
	
	
	public MapGeoTagModal(final PageReference parent, final ModalWindow window, final String building) {
		final AbstractDefaultAjaxBehavior behave = new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void respond(AjaxRequestTarget target) {
				double longitude = 0L, latitude = 0L;
				String url = RequestCycle.get().getRequest().getUrl().toAbsoluteString();
				String[] parameters = url.split("&");
				if (parameters.length != 4) {
					log.error("error");
				}
				for (int x = 0; x < parameters.length; x++) {
					String currentItem = parameters[x];
					String[] keyValue = currentItem.split("=");
					if (keyValue[0].equals("long")) {
						longitude = Double.parseDouble(keyValue[1]);
					}
					else if (keyValue[0].equals("lat")) {
						latitude = Double.parseDouble(keyValue[1]);
					}
				}
				log.info(longitude + ", " + latitude);
				points.add(new Point2D.Double(longitude, latitude));
			}
			
		};
		add(dummyContainer = new CallbackUrlInjector("mk", behave));
		dummyContainer.add(behave);
		
		Form<Void> submitCoordinates = new Form<Void>("submitCoordinates"){
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit() {
				if (points.size() != 2) {
					log.error("Too many points");
				}
				else {
					for (Point2D.Double point : points) {
						if (point.x == 0 && point.y == 0) {
							log.error("Invalid coordinates set");
						}
					}
					Connection conn = DatabaseConnectionManager.getConnection("live");
					try {
						PreparedStatement stmt = conn.prepareStatement("UPDATE Building SET Longitude_point_1=?, Latitude_point_1=?, Longitude_point_2=?, Latitude_point_2=? WHERE Name=?");
						stmt.setDouble(1, points.get(0).x);
						stmt.setDouble(2, points.get(0).y);
						stmt.setDouble(3, points.get(1).x);
						stmt.setDouble(4, points.get(1).y);
						stmt.setString(5, building);
						stmt.execute();
						conn.close();
					}
					catch (SQLException e) {
						log.error(e);
					}
				}
			}
			
		};
		submitCoordinates.add(new AjaxButton("submitForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				window.close(target);	
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				window.close(target);
			}
		});
		add(submitCoordinates);

	}
		
	
}
