package uk.ac.ic.doc.campusProject.model;

public enum RoomType {
	OFFICE("Office"),
	LECTURE_THEATRE("Lecture Theatre"),
	GENTS_TOILET("Gents Toilet"),
	LADIES_TOILET("Ladies Toilet"),
	IT_FACILITY("IT Facility"),
	CATERING("Catering"),
	RETAIL("Retail"),
	LAB("Lab"),
	SEMINAR_ROOM("Seminar Room"),
	LEISURE("Leisure"),
	OTHER("Other");
	
	private final String typeString;
	
	RoomType(String typeString) {
		this.typeString = typeString;
	}
	
	public String getFriendlyName(RoomType type) {
		return type.typeString;
	}
	
	public static RoomType getType(String type) {
		for (RoomType roomType : RoomType.values()) {
			if (roomType.toString().equals(type)) {
				return roomType;
			}
		}
		return RoomType.OTHER;
	}
	
}
