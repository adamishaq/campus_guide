package uk.ac.ic.doc.campusProject.web.pages.mapping;

import org.apache.log4j.Logger;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.RequestCycle;

import uk.ac.ic.doc.campusProject.web.pages.CallbackUrlInjector;

public class MapGeoTagPanel extends WebPage {

	static Logger log = Logger.getLogger(MapGeoTagPanel.class);	
	private static final long serialVersionUID = 1L;
	private WebMarkupContainer dummyContainer;
	
	
	public MapGeoTagPanel(final PageReference parent, final ModalWindow window) {
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
			}
			
		};
		add(dummyContainer = new CallbackUrlInjector("mk", behave));
		dummyContainer.add(behave);
		
		
		
	}
		
	
}
