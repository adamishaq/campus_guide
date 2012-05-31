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
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.DynamicImageResource;

import uk.ac.ic.doc.campusProject.model.FloorPlanDao;
import uk.ac.ic.doc.campusProject.model.RoomDetailsDao;
import uk.ac.ic.doc.campusProject.model.RoomType;
import uk.ac.ic.doc.campusProject.model.SerializableBufferedImage;
import uk.ac.ic.doc.campusProject.utils.db.DatabaseConnectionManager;

public class RoomTagInfoModal extends WebPage {
	private static final long serialVersionUID = 1L;
    static Logger log = Logger.getLogger(RoomTagInfoModal.class);
	final RoomDetailsDao roomDetails;
	@SuppressWarnings("unused")
	private DropDownChoice<RoomType> roomTypeChoice;
	private FileUploadField fileUpload;
	private boolean existing; 
	
	public RoomTagInfoModal(final PageReference parent, final ModalWindow modal, final FloorPlanDao floor, final Point coord) {
		this(parent, modal, floor, coord, null);
	}

	public RoomTagInfoModal(final PageReference parent, final ModalWindow modal, final FloorPlanDao floor, final Point coord, final RoomDetailsDao roomDetailsIn) {
		
		if (roomDetailsIn == null && floor != null) {
			log.info("Does not exist");
			this.roomDetails = new RoomDetailsDao(floor.getBuilding());
			existing = false; 
		}
		else {
			log.info("Exists at start");
			this.roomDetails = roomDetailsIn;
			existing = true;
		}
		
		modal.setCloseButtonCallback(new CloseButtonCallback() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean onCloseButtonClicked(AjaxRequestTarget target) {
				log.info("Called");
				return true;
			}
		});
		
		Form<Void> tagInfoForm = new Form<Void>("tagInfoForm");
		tagInfoForm.add(new TextField<String>("infoBox", new PropertyModel<String>(this.roomDetails, "description")));
		tagInfoForm.add(new TextField<Integer>("roomBox", new PropertyModel<Integer>(this.roomDetails, "number")));
		tagInfoForm.add(roomTypeChoice = new DropDownChoice<RoomType>("roomTypeChoice", new PropertyModel<RoomType>(this.roomDetails, "type"), Arrays.asList(RoomType.values())) {
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
				FileUpload uploadedFile = fileUpload.getFileUpload();
				Connection conn = DatabaseConnectionManager.getConnection("live");
				try {
					PreparedStatement roomStmt = conn.prepareStatement("INSERT INTO Room VALUES(?,?,?,?,?) ON DUPLICATE KEY UPDATE Type=?, Description=?, Image=?");
					roomStmt.setString(1, roomDetails.getNumber());
					roomStmt.setString(2, roomDetails.getBuilding());
					roomStmt.setString(3, roomDetails.getType().toString());
					roomStmt.setString(4, roomDetails.getDescription());
					if (uploadedFile != null && uploadedFile.getContentType().equals("image/png")) {
						try {
							SerializableBufferedImage image = new SerializableBufferedImage(ImageIO.read(uploadedFile.getInputStream()));
							roomStmt.setBytes(5, image.getImage());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else {
						roomStmt.setNull(5, Types.NULL);
					}
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
				modal.close(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				modal.close(target);
			}
			
		});
		tagInfoForm.add(fileUpload = new FileUploadField("fileUpload"));
		
		tagInfoForm.add(new NonCachingImage("preview", new DynamicImageResource() {
			private static final long serialVersionUID = 1L;

			@Override
			protected byte[] getImageData(Attributes attributes) {
				if (roomDetails.getImage() != null) {
					return roomDetails.getImage().getThumb();
				}
				else {
					return new byte[]{' '};
				}
			}
		}));
		
		tagInfoForm.add(new AjaxButton("delete") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (existing) {
					log.info("Exists");
					Connection conn = DatabaseConnectionManager.getConnection("live");
					try {
						PreparedStatement stmt = conn.prepareStatement("DELETE Room.*, Floor_Contains.* FROM Room JOIN Floor_Contains ON Room.Number=Floor_Contains.Room AND Room.Building=Floor_Contains.Building WHERE Room.Number=? AND Room.Building=?;");
						stmt.setString(1, roomDetails.getNumber());
						stmt.setString(2, roomDetails.getBuilding());
						if (stmt.execute()) {
							log.info("Successful delete");
						}
						else {
							log.info("Else");
						}
						conn.close();
					} 
					catch (SQLException e) {
						log.error(e);
						e.printStackTrace();
					}
				}
				modal.close(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				modal.close(target);
				
			}
			
		});
		add(tagInfoForm);
		
		
		
	}
}
