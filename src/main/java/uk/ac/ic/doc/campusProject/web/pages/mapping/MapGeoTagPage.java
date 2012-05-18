package uk.ac.ic.doc.campusProject.web.pages.mapping;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.cycle.RequestCycle;

import uk.ac.ic.doc.campusProject.model.FloorPlanDao;

public class MapGeoTagPage extends WebPage {

	static Logger log = Logger.getLogger(MapGeoTagPage.class);	
	private static final long serialVersionUID = 1L;
	private List<FloorPlanDao> floorPlans;
	private WebMarkupContainer dummyContainer;
	
	/* Make extensible - add argumentless constructor for general case */
	
	public MapGeoTagPage() {
		final AbstractDefaultAjaxBehavior behave = new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void respond(AjaxRequestTarget target) {
				log.info("called");
				double longitude = 0L, latitude = 0L;
				target.add(new Label("foo", "Called from JS").setOutputMarkupId(true));
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
		add(dummyContainer = new MyWebMarkupContainer("mk", behave));
		dummyContainer.add(behave);
		
	}
	
	public MapGeoTagPage(List<FloorPlanDao> floorPlanDao) {
		super();
		this.floorPlans = floorPlanDao;
		
		//add(new FeedbackPanel("feedback"));
		
	}
	
	
	private class MyWebMarkupContainer extends WebMarkupContainer implements IHeaderContributor {
		private static final long serialVersionUID = 1L;
		AbstractDefaultAjaxBehavior behaviour;
		
		public MyWebMarkupContainer(String id, AbstractDefaultAjaxBehavior behaviour) {
			super(id);
			this.behaviour = behaviour;
		}
		
		public void renderHead(IHeaderResponse response) {
			String callback = new String("var callback = '" + this.behaviour.getCallbackUrl() + "';");
			log.info(callback);
			response.renderJavaScript(callback, "jsinject");
		}
		
	}
	
	
	

}
