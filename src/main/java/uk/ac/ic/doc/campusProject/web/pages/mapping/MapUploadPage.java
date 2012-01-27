package uk.ac.ic.doc.campusProject.web.pages.mapping;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.util.lang.Bytes;

import uk.ac.ic.doc.campusProject.model.FloorPlanDao;
import uk.ac.ic.doc.campusProject.utils.pdf.PdfProcessor;
import uk.ac.ic.doc.campusProject.web.pages.AdminPage;

public class MapUploadPage extends AdminPage {
    static Logger log = Logger.getLogger(MapUploadPage.class);
    
	private static final long serialVersionUID = 1L;
	private FileUploadField fileUpload;
	private List<FloorPlanDao> images; 

	
	public MapUploadPage(List<FloorPlanDao> images) {
		setPageLocation("Mapping - Upload new Maps");
		this.images = images;
		
		add(new FeedbackPanel("feedback"));
		
		UploadForm uploadForm = new UploadForm();
		uploadForm.setMultiPart(true);
		uploadForm.setMaxSize(Bytes.megabytes(25));
		uploadForm.add(fileUpload = new FileUploadField("fileUpload"));
		add(uploadForm);
		
		EditForm editForm = new EditForm();
		add(editForm);
	}
	
	
	public MapUploadPage() {
		setPageLocation("Mapping - Upload new Maps");
		
		add(new FeedbackPanel("feedback"));
		
		UploadForm uploadForm = new UploadForm();
		uploadForm.setMultiPart(true);
		uploadForm.setMaxSize(Bytes.megabytes(25));
		uploadForm.add(fileUpload = new FileUploadField("fileUpload"));
		add(uploadForm);
		
		EditForm editForm = new EditForm();
		add(editForm);
		
	}
	
	class EditForm extends Form<Void> {
		private static final long serialVersionUID = 1L;
		private ListView<FloorPlanDao> uploadedImages;

		public EditForm() {
			super("editForm");
			uploadedImages = new ListView<FloorPlanDao>("uploadedImages", images) {
				private static final long serialVersionUID = 1L;

				protected void populateItem(final ListItem<FloorPlanDao> item) {
					item.add(new Image("preview", new DynamicImageResource() {
						private static final long serialVersionUID = 1L;

						@Override
						protected byte[] getImageData(Attributes attributes) {
							return item.getModel().getObject().getFloorPlan().getThumb();
						}
					}));
					item.add(new TextField<String>("building", new PropertyModel<String>(item.getModel().getObject(), "building")));
					item.add(new TextField<String>("floor", new PropertyModel<String>(item.getModel().getObject(), "floor")));
				}
			};
			add(uploadedImages);
		}
		
		@Override
		protected void onSubmit() {
			
			//VALIDATION CODE

		}
		
	}
	
	class UploadForm extends Form<Void> {
		public UploadForm() {
			super("uploadForm");
		}
		
		private static final long serialVersionUID = 1L;
		
		@Override
		protected void onSubmit() {
			final FileUpload uploadedFile = fileUpload.getFileUpload();
			if (uploadedFile != null && uploadedFile.getContentType().equals("application/pdf")) {
				info(uploadedFile.getClientFileName() + " was uploaded successfully");
				ByteArrayInputStream bais = new ByteArrayInputStream(uploadedFile.getBytes());
				images = PdfProcessor.pdfToImage(bais);
				try {
					bais.close();
					setResponsePage(new MapUploadPage(images));
				} 
				catch (IOException e) {
					log.error(e);
					e.printStackTrace();
				}
			}
			else if (uploadedFile != null && !uploadedFile.getContentType().equals("application/pdf")) {
				info("You must upload a PDF file");
			}
			else {
				info("Please select a file to upload");
			}
			
		}
		
	}
}
