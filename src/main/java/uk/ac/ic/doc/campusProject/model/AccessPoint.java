package uk.ac.ic.doc.campusProject.model;

public class AccessPoint {
	private String mac;
	private String hostname;
	private double signalStrength;
	
	public AccessPoint(String mac, String hostname, double signalStrength) {
		this.mac = mac;
		this.hostname = hostname;
		this.signalStrength = signalStrength;
	}
	

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public String getBuildingFromHostname() {
		return this.hostname.split("-")[0];
	}
	
	public String getFloorFromHostname() {
		return this.hostname.split("-")[2];
		
	}

	public double getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(double signalStrength) {
		this.signalStrength = signalStrength;
	}
	
	

}
