package uk.ac.ic.doc.campusProject.web.pages.mapping.modal;

import java.awt.Point;
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
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.DynamicImageResource;

import uk.ac.ic.doc.campusProject.model.FloorPlanDao;
import uk.ac.ic.doc.campusProject.utils.db.DatabaseConnectionManager;
import uk.ac.ic.doc.campusProject.web.pages.CallbackUrlInjector;

public class MapCoordinateTagModal extends WebPage {

	private static final long serialVersionUID = 1L;
    static Logger log = Logger.getLogger(MapCoordinateTagModal.class);
    WebMarkupContainer mapCanvas;
	List<Point> points = new ArrayList<Point>();
	
	
	public MapCoordinateTagModal(final PageReference parent, final ModalWindow modal, final FloorPlanDao floorPlan) {
		add(new Image("map", new DynamicImageResource() {
			private static final long serialVersionUID = 1L;

			@Override
			protected byte[] getImageData(Attributes attributes) {
				return floorPlan.getFloorPlan().getImage();
			}		
		}).setMarkupId("map"));
		
		final AbstractDefaultAjaxBehavior behaviour = new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void respond(AjaxRequestTarget target) {
				int xCoord = 0, yCoord = 0;
				String url = RequestCycle.get().getRequest().getUrl().toAbsoluteString();
				String[] parameters = url.split("&");
				if (parameters.length != 4) {
					log.error("error");
				}
				for (int x = 0; x < parameters.length; x++) {
					String currentItem = parameters[x];
					String[] keyValue = currentItem.split("=");
					if (keyValue[0].equals("x")) {
						xCoord = Integer.parseInt(keyValue[1]);
					}
					else if (keyValue[0].equals("y")) {
						yCoord  = Integer.parseInt(keyValue[1]);
					}
				}
				points.add(new Point(xCoord, yCoord));
			}
		};
		add(mapCanvas = new CallbackUrlInjector("injector", behaviour));
		mapCanvas.add(behaviour);
		
		Form<Void> submitCoordinates = new Form<Void>("submitCoordinates"){
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit() {
				if (points.size() != 2) {
					log.error("Too many points");
				}
				else {
					for (Point point : points) {
						if (point.x == 0 && point.y == 0) {
							log.error("Invalid coordinates set");
						}
					}
					Connection conn = DatabaseConnectionManager.getConnection("live");
					try {
						PreparedStatement stmt = conn.prepareStatement("UPDATE Building_Map_Attributes SET Pixel_x_1=?, Pixel_y_1=?, Pixel_x_2=?, Pixel_y_2=? WHERE Building=?");
						stmt.setInt(1, points.get(0).x);
						stmt.setInt(2, points.get(0).y);
						stmt.setInt(3, points.get(1).x);
						stmt.setInt(4, points.get(1).y);
						stmt.setString(5, floorPlan.getBuilding());
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
				modal.close(target);	
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				modal.close(target);
			}
		});
		add(submitCoordinates);
	}

}
