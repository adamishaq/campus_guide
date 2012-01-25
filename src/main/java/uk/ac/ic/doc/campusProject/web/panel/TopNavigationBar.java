package uk.ac.ic.doc.campusProject.web.panel;

import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.border.BoxBorder;

public class TopNavigationBar extends Border {
	private static final long serialVersionUID = 1L;

	public TopNavigationBar(String id) {
		super(id);
		addToBorder(new BoxBorder("bodyBorder"));

	}
		
}
