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
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.CloseButtonCallback;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import uk.ac.ic.doc.campusProject.model.FloorPlanDao;
import uk.ac.ic.doc.campusProject.model.RoomDetailsDao;
import uk.ac.ic.doc.campusProject.model.RoomType;
import uk.ac.ic.doc.campusProject.model.SerializableBufferedImage;
import uk.ac.ic.doc.campusProject.utils.db.DatabaseConnectionManager;

public class RoomTagInfoModal extends WebPage {
	private static final long serialVersionUID = 1L;
    static Logger log = Logger.getLogger(RoomTagInfoModal.class);
	RoomDetailsDao roomDetails;
	@SuppressWarnings("unused")
	private DropDownChoice<RoomType> roomTypeChoice;
	private FileUploadField fileUpload;

	public RoomTagInfoModal(final PageReference parent, final ModalWindow modal, final FloorPlanDao floor, final Point coord) {
		
		roomDetails = new RoomDetailsDao(floor.getBuilding());
		
		modal.setCloseButtonCallback(new CloseButtonCallback() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean onCloseButtonClicked(AjaxRequestTarget target) {
				return true;
			}
		});
		
		Form<Void> tagInfoForm = new Form<Void>("tagInfoForm") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit() {
				FileUpload uploadedFile = fileUpload.getFileUpload();
				Connection conn = DatabaseConnectionManager.getConnection("live");
				try {
					PreparedStatement roomStmt = conn.prepareStatement("INSERT INTO Room VALUES(?,?,?,?,?) ON DUPLICATE KEY UPDATE Type=?, Description=?, Image=?");
					roomStmt.setString(1, roomDetails.getNumber());
					roomStmt.setString(2, roomDetails.getBuilding());
					roomStmt.setString(3, roomDetails.getType().toString());
					roomStmt.setString(4, roomDetails.getDescription());
					roomStmt.setNull(5, Types.NULL);
					roomStmt.setString(6, roomDetails.getType().toString());
					roomStmt.setString(7, roomDetails.getDescription());
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
					floorStmt.setString(1, roomDetails.getBuilding());
					floorStmt.setInt(2, Integer.parseInt(floor.getFloor()));
					floorStmt.setString(3, roomDetails.getNumber());
					floorStmt.setInt(4, coord.x);
					floorStmt.setInt(5, coord.y);
					floorStmt.setInt(6, coord.x);
					floorStmt.setInt(7, coord.y);
					floorStmt.execute();
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
					log.error(e);
				}
			}
			
		};
		tagInfoForm.add(new TextField<String>("infoBox", new PropertyModel<String>(roomDetails, "description")));
		tagInfoForm.add(new TextField<Integer>("roomBox", new PropertyModel<Integer>(roomDetails, "number")));
		tagInfoForm.add(roomTypeChoice = new DropDownChoice<RoomType>("roomTypeChoice", new Model<RoomType>(), Arrays.asList(RoomType.values())) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}
			
			@Override
			protected void onSelectionChanged(RoomType newSelection) {
				roomDetails.setType(newSelection);
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
	
	/*
	private RoomDetailsDao retrieveExistingValues(FloorPlanDao floor) {
		if (number != null) {
			Connection conn = DatabaseConnectionManager.getConnection("live");
			try {
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Room WHERE Building=? AND Number=?");
				stmt.setString(1, floor.getBuilding());
				stmt.setString(2, number);
				if (stmt.execute()) {
					ResultSet rs = stmt.getResultSet();
					while(rs.next()) {
						
					}
				}
				
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else {
			
		}

	}
	*/
}
