package uk.ac.ic.doc.campusProject.web.pages.mapping.modal;

import java.awt.Point;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import uk.ac.ic.doc.campusProject.model.FloorPlanDao;
import uk.ac.ic.doc.campusProject.model.RoomType;
import uk.ac.ic.doc.campusProject.model.SerializableBufferedImage;
import uk.ac.ic.doc.campusProject.utils.db.DatabaseConnectionManager;

public class RoomTagInfoModal extends WebPage {
	private static final long serialVersionUID = 1L;
    static Logger log = Logger.getLogger(RoomTagInfoModal.class);
	private String description;
	private int number;
	private RoomType type;
	private DropDownChoice<RoomType> roomTypeChoice;
	private FileUploadField fileUpload;

	public RoomTagInfoModal(final PageReference parent, final ModalWindow modal, final FloorPlanDao floor, final Point coord) {
		description = "";
		number = 0;
		
		Form<Void> tagInfoForm = new Form<Void>("tagInfoForm") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit() {
				FileUpload uploadedFile = fileUpload.getFileUpload();
				Connection conn = DatabaseConnectionManager.getConnection("live");
				try {
					PreparedStatement roomStmt = conn.prepareStatement("INSERT INTO Room VALUES(?,?,?,?,?) ON DUPLICATE KEY UPDATE Type=?, Description=?, Image=?");
					roomStmt.setInt(1, number);
					roomStmt.setString(2, floor.getBuilding());
					roomStmt.setString(3, type.toString());
					roomStmt.setString(4, description);
					roomStmt.setNull(5, Types.NULL);
					roomStmt.setString(6, type.toString());
					roomStmt.setString(7, description);
					if (uploadedFile != null && uploadedFile.getContentType().equals("image/png")) {
						try {
							SerializableBufferedImage image = new SerializableBufferedImage(ImageIO.read(uploadedFile.getInputStream()));
							roomStmt.setBytes(8, image.getImage());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else {
						roomStmt.setNull(8, Types.NULL);
					}
					roomStmt.execute();
					PreparedStatement floorStmt = conn.prepareStatement("INSERT INTO Floor_Contains VALUES(?,?,?,?,?) ON DUPLICATE KEY UPDATE Xpixel=?, Ypixel=?");
					floorStmt.setString(1, floor.getBuilding());
					floorStmt.setInt(2, Integer.parseInt(floor.getFloor()));
					floorStmt.setInt(3, number);
					floorStmt.setInt(4, coord.x);
					floorStmt.setInt(5, coord.y);
					floorStmt.setInt(6, coord.x);
					floorStmt.setInt(7, coord.y);
					floorStmt.execute();
				} catch (SQLException e) {
					e.printStackTrace();
					log.error(e);
				}
			}
			
		};
		tagInfoForm.add(new TextField<String>("infoBox", new PropertyModel<String>(this, "description")));
		tagInfoForm.add(new TextField<Integer>("roomBox", new PropertyModel<Integer>(this, "number")));
		tagInfoForm.add(roomTypeChoice = new DropDownChoice<RoomType>("roomTypeChoice", new Model<RoomType>(), Arrays.asList(RoomType.values())) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}
			
			@Override
			protected void onSelectionChanged(RoomType newSelection) {
				type = newSelection;
			}
			
		});
		tagInfoForm.add(new AjaxButton("submitForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				modal.close(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				modal.close(target);
			}
			
		});
		tagInfoForm.add(fileUpload = new FileUploadField("fileUpload"));
		add(tagInfoForm);
		
	}
	
}
