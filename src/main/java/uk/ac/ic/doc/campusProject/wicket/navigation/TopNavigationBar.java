package uk.ac.ic.doc.campusProject.wicket.navigation;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.border.BoxBorder;

public class TopNavigationBar extends Border {
	private static final long serialVersionUID = 1L;

	public TopNavigationBar(String id, String name) {
		super(id);
		addToBorder(new BoxBorder("bodyBorder").add(new Label("location", name)));
	}
		
}
