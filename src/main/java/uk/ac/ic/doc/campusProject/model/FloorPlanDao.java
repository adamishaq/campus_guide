package uk.ac.ic.doc.campusProject.model;

import java.io.Serializable;

public class FloorPlanDao implements Serializable {
	private static final long serialVersionUID = 1L;
	private SerializableBufferedImage floorPlan;
	private String building;
	private String floor;
	
	public FloorPlanDao(SerializableBufferedImage floorPlan, String building, String floor) {
		this.floorPlan = floorPlan;
		this.building = building;
		this.floor = floor;
	}
	
	public SerializableBufferedImage getFloorPlan() {
		return floorPlan;
	}
	public void setFloorPlan(SerializableBufferedImage floorPlan) {
		this.floorPlan = floorPlan;
	}
	public String getBuilding() {
		return building;
	}
	public void setBuilding(String building) {
		this.building = building;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	

}
