package uk.ac.ic.doc.campusProject.web.pages.mapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;

public class JsPanel extends Panel implements IHeaderContributor {
	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(JsPanel.class);

	public JsPanel(String id) {
		super(id);
	}
	
	public void renderHead(IHeaderResponse response) {
		List<? extends Behavior> list = getBehaviors();
		String url = "";
		if (list != null && list.size() > 0) {
			AbstractDefaultAjaxBehavior behaviour = (AbstractDefaultAjaxBehavior) list.get(0);
			url = behaviour.getCallbackUrl().toString();
			log.info(url);
		}
		
		StringBuilder built = new StringBuilder();
		String str = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader("src/main/java/uk/ac/ic/doc/campusProject/web/pages/mapping/jsCode.txt"));
			while ((str = in.readLine()) != null) {
				built.append(str);
			}
			in.close();
		}
		catch (IOException e) {
			log.error(e);
		}
		built.append("wicketAjaxPost(" + url + ", location)");
		built.append("}");
		String methodCall = built.toString();
		log.info(methodCall);
		response.renderOnLoadJavaScript(methodCall);
	}
	
	

}
