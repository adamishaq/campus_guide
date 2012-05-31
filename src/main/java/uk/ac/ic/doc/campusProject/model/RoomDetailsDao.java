package uk.ac.ic.doc.campusProject.model;

import java.io.Serializable;


public class RoomDetailsDao implements Serializable {
	private static final long serialVersionUID = 1L;
	private String number;
	private String building;
	private RoomType type;
	private String description;
	private SerializableBufferedImage image;
	
	public RoomDetailsDao(String number, String building, RoomType type,
			String description, SerializableBufferedImage image) {
		this.number = number;
		this.building = building;
		this.type = type;
		this.description = description;
		this.image = image;
	}
	
	
	public RoomDetailsDao(String building) {
		this.number = "";
		this.building = building;
		this.type = RoomType.OTHER;
		this.description = "";
		this.image = new SerializableBufferedImage();
	}
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getBuilding() {
		return building;
	}
	public void setBuilding(String building) {
		this.building = building;
	}
	public RoomType getType() {
		return type;
	}
	public void setType(RoomType type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public SerializableBufferedImage getImage() {
		return image;
	}
	public void setImage(SerializableBufferedImage image) {
		this.image = image;
	}
	
	
	
	

}
