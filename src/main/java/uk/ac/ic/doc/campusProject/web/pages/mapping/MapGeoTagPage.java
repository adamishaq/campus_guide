package uk.ac.ic.doc.campusProject.web.pages.mapping;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import uk.ac.ic.doc.campusProject.model.FloorPlanDao;

public class MapGeoTagPage extends WebPage {

	static Logger log = Logger.getLogger(MapGeoTagPage.class);	
	private static final long serialVersionUID = 1L;
	private List<FloorPlanDao> floorPlans;
	
	/* Make extensible - add argumentless constructor for general case */
	
	public MapGeoTagPage() {
		//add(new FeedbackPanel("feedback"));
		
	}
	
	public MapGeoTagPage(List<FloorPlanDao> floorPlanDao) {
		this.floorPlans = floorPlanDao;
		
		//add(new FeedbackPanel("feedback"));
		
	}
	
	public MapGeoTagPage(PageParameters pp) {
		log.info("pp here!");
	}
	
	
	

}
