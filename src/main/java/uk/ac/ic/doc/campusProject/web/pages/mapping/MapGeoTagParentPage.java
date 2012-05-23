package uk.ac.ic.doc.campusProject.web.pages.mapping;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import uk.ac.ic.doc.campusProject.model.FloorPlanDao;
import uk.ac.ic.doc.campusProject.web.pages.AdminPage;

public class MapGeoTagParentPage extends AdminPage  {
	private static final long serialVersionUID = 1L;
    static Logger log = Logger.getLogger(MapGeoTagParentPage.class);
	ModalWindow geoTagModal;
	ModalWindow mapTagModal;
	DropDownChoice<String> floors;
	PropertyModel<String> selectedFloor;

	public MapGeoTagParentPage() {
		setPageLocation("Mapping - Geo-Tagging");
		initialiseGeoTagModal();
		initialiseMapTagModal();
	}


	public MapGeoTagParentPage(final List<FloorPlanDao> daos) {
		setPageLocation("Mapping - Geo-Tagging");
		initialiseGeoTagModal();
		initialiseMapTagModal();
		//selectedFloor = new PropertyModel<String>(this, "selectedFloor");
		List<String> floorList = new ArrayList<String>();
		for (int x = 0; x < daos.size(); x++) {
			floorList.add(daos.get(x).getFloor());
		}
		
		floors = new DropDownChoice<String>("floors", new Model<String>(), floorList) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}
			
			@Override
			protected void onSelectionChanged(String newSelection) {
				for (int x = 0; x < daos.size(); x++) {
					final MapCoordinateTagPanel tagPanel = new MapCoordinateTagPanel(MapGeoTagParentPage.this.getPageReference(), mapTagModal, daos.get(x));
					if (daos.get(x).getFloor().equals(newSelection)) {
						log.info("Changing floor DAO");
						mapTagModal.setPageCreator(new ModalWindow.PageCreator() {
							private static final long serialVersionUID = 1L;
							@Override
							public Page createPage() {
								return tagPanel;
							}
						});
					}
				}
			}
		};
		add(floors);
	}

	public void initialiseGeoTagModal() {
		add(geoTagModal = new ModalWindow("geoTagModal"));
		geoTagModal.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;

			@Override
			public Page createPage() {
				return new MapGeoTagPanel(MapGeoTagParentPage.this
						.getPageReference(), geoTagModal);
			}
		});
		geoTagModal.setTitle("Geo-tag Map");

		add(new AjaxLink<Void>("showgeoTagModal") {
			private static final long serialVersionUID = 1L;

			public void onClick(AjaxRequestTarget target) {
				geoTagModal.show(target);
			}
		});
	}
	
	private void initialiseMapTagModal() {
		add(mapTagModal = new ModalWindow("mapTagModal"));
		mapTagModal.setTitle("Geo-Tag Floor");
		add(new AjaxLink<Void>("showMapTagModal") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				mapTagModal.show(target);
			}
		});
		
	}

}
