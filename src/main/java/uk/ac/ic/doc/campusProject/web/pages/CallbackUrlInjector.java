package uk.ac.ic.doc.campusProject.web.pages;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;

public class CallbackUrlInjector extends WebMarkupContainer implements IHeaderContributor {
	private static final long serialVersionUID = 1L;
	AbstractDefaultAjaxBehavior behaviour;
	
	public CallbackUrlInjector(String id, AbstractDefaultAjaxBehavior behaviour) {
		super(id);
		this.behaviour = behaviour;
	}
	
	public void renderHead(IHeaderResponse response) {
		String callback = new String("var callback = '" + this.behaviour.getCallbackUrl() + "';");
		response.renderJavaScript(callback, "jsinject");
	}
	
	
}
