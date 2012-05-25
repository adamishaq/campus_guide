package uk.ac.ic.doc.campusProject.web.pages.mapping;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.DynamicImageResource;

import uk.ac.ic.doc.campusProject.model.FloorPlanDao;
import uk.ac.ic.doc.campusProject.web.pages.AdminPage;
import uk.ac.ic.doc.campusProject.web.pages.CallbackUrlInjector;
import uk.ac.ic.doc.campusProject.web.pages.mapping.modal.RoomTagInfoModal;

public class RoomTagPage extends AdminPage {
	private static final long serialVersionUID = 1L;
    static Logger log = Logger.getLogger(RoomTagPage.class);
	DropDownChoice<String> floorChoice;
	ModalWindow tagModal;
	WebMarkupContainer mapCanvas;
	NonCachingImage map;

	public RoomTagPage(final List<FloorPlanDao> daos, final String floor) {
		setPageLocation("Room Tagging");
		add(tagModal = new ModalWindow("tagModal"));
		tagModal.setTitle("Room Information");
		floorChoice = new DropDownChoice<String>("floorChoice", new Model<String>(), getFloorList(daos)) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}
			
			@Override
			protected void onSelectionChanged(String newSelection) {
				setResponsePage(new RoomTagPage(daos, newSelection));
			}
			
		};
		add(floorChoice);
		
		map = new NonCachingImage("map");
		
		DynamicImageResource mapResource = new DynamicImageResource() {
			private static final long serialVersionUID = 1L;

			@Override
			protected byte[] getImageData(Attributes attributes) {
				if (floor != null) {
					for (FloorPlanDao dao : daos) {
						if (dao.getFloor().equals(floor)) {
							return dao.getFloorPlan().getImage();
						}
					}
				}
				return new byte[]{' '};
			}
			
		};
		map.setImageResource(mapResource);
		map.setMarkupId("map");
		add(map);
		
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
				Point point = new Point(xCoord, yCoord);
				final RoomTagInfoModal roomTagModal = new RoomTagInfoModal(RoomTagPage.this.getPageReference(), tagModal, getFloorPlanDao(daos, floor), point);
				tagModal.setPageCreator(new ModalWindow.PageCreator() {
					private static final long serialVersionUID = 1L;

					@Override
					public Page createPage() {
						return roomTagModal;
					}
				});
				tagModal.show(target);
			}
		};
		
		add(mapCanvas = new CallbackUrlInjector("injector", behaviour));
		mapCanvas.add(behaviour);

	}

	private List<String> getFloorList(List<FloorPlanDao> daos) {
		List<String> returnList = new ArrayList<String>();
		for (FloorPlanDao dao : daos) {
			returnList.add(dao.getFloor());
		}
		return returnList;
	}
	
	private FloorPlanDao getFloorPlanDao(List<FloorPlanDao> daos, String floor) {
		for (FloorPlanDao dao : daos) {
			if (dao.getFloor().equals(floor)) {
				return dao;
			}
		}
		return null;
	}
	
}
