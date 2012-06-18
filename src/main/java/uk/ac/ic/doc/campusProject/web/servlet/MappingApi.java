package uk.ac.ic.doc.campusProject.web.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Adler32;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.ic.doc.campusProject.model.AccessPoint;
import uk.ac.ic.doc.campusProject.utils.comparator.AccessPointComparator;
import uk.ac.ic.doc.campusProject.utils.db.DatabaseConnectionManager;

public class MappingApi extends HttpServlet {

	private static final long serialVersionUID = 1L;
	Logger log = Logger.getLogger(MappingApi.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		
		Connection conn = DatabaseConnectionManager.getConnection("live");
		try {
			String type = request.getParameter("type");
			if (type.equals("manual")) {
				String building = request.getParameter("building");
				int floor = new Integer(request.getParameter("floor"));
	
				PreparedStatement stmt = conn.prepareStatement("SELECT Plan FROM Floor LEFT JOIN Building ON Building=Name WHERE (Building=? OR ShortCode=?) AND Floor=?");
				stmt.setString(1, building);
				stmt.setString(2, building);
				stmt.setInt(3, floor);
				if (stmt.execute()) {
					ResultSet rs = stmt.getResultSet();
					while(rs.next()) {
						Blob blob = rs.getBlob("Plan");
						byte[] image = blob.getBytes(1, (int)blob.length());						
						response.setContentType("image/png");
						ServletOutputStream os = response.getOutputStream();
						os.write(image);
						response.setStatus(HttpServletResponse.SC_OK);
						os.flush();
					}
				} 
				else {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				}
			}
			else if (type.equals("check")) {
				String building = request.getParameter("building");
				int floor = new Integer(request.getParameter("floor"));
	
				PreparedStatement stmt = conn.prepareStatement("SELECT Plan FROM Floor LEFT JOIN Building ON Building=Name WHERE (Building=? OR ShortCode=?) AND Floor=?");
				stmt.setString(1, building);
				stmt.setString(2, building);
				stmt.setInt(3, floor);
				if (stmt.execute()) {
					ResultSet rs = stmt.getResultSet();
					while(rs.next()) {
						Blob blob = rs.getBlob("Plan");
						byte[] image = blob.getBytes(1, (int)blob.length());
						
						Adler32 check = new Adler32();
						check.update(image);
						BufferedImage buffImage = ImageIO.read(new ByteArrayInputStream(image));
						int height = buffImage.getHeight();
						int width = buffImage.getWidth();
						
						JSONObject json = new JSONObject();
						json.put("width", width);
						json.put("height", height);
						json.put("checksum", check.getValue());
						response.setContentType("application/json");
						ServletOutputStream os = response.getOutputStream();
						os.write(json.toString().getBytes());
						response.setStatus(HttpServletResponse.SC_OK);
						os.flush();
					}
				} 
				else {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				}
			}
			else {
//				double longitude = 0;
//				double latitude = 0;
				List<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
//				longitude = Double.parseDouble(request.getParameter("longitude"));
//				latitude = Double.parseDouble(request.getParameter("latitude"));
				for (int x = 0 ; x < 25 ; x++) {
					AccessPoint ap = null;
					String macParameter = new String("mac" + x);
					String ssParameter = new String("ss" + x);
					String macThis = request.getParameter(macParameter);
					String ssThis = request.getParameter(ssParameter);
					if (macThis != null && ssThis != null) {
						macThis = parseMacAddress(macThis);
						PreparedStatement stmt = conn.prepareStatement("SELECT Hostname, Location FROM WirelessAccessPoints WHERE Radio_MAC=?");
						stmt.setString(1, macThis);
						if (stmt.execute()) {
							ResultSet rs = stmt.getResultSet();
							while(rs.next()) {
								log.info("We have locations:");
								ap = new AccessPoint(macThis, rs.getString("Hostname"), rs.getString("Location"), Double.parseDouble(ssThis));
								log.info("location:" + ap.getLocation() + ", ss:" + ap.getSignalStrength());
								accessPoints.add(ap);
							}
						}
					}
				}
				Collections.sort(accessPoints, new AccessPointComparator());

				String floor, building, location;
				AccessPoint verifiedLocation;
				if (verifyLocation(accessPoints) != null) {
					verifiedLocation = verifyLocation(accessPoints);
					building = verifiedLocation.getBuildingFromHostname();
					floor = verifiedLocation.getFloorFromHostname();
					location = verifiedLocation.getLocation();
					log.info("Verified Location:");
					log.info("building:" + building + ", floor:" + floor + ", location:" + location);
				}
				else {
					building = "hux";
					floor = "2";
					location = "219";
				}
				/*
				
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Building LEFT JOIN Building_Map_Attributes ON Name=Building WHERE ShortCode=?");
				stmt.setString(1, building);
				if (stmt.execute()) {
					ResultSet rs = stmt.getResultSet();
					double pixelDistance = 0.0;
					double coordinateDistance = 0.0;
					double x1 = 0.0;
					double y1 = 0.0;
					double long1 = 0.0;
					double lat1 = 0.0;
					while(rs.next()) {
						x1 = rs.getInt("Pixel_x_1");
						y1 = rs.getInt("Pixel_y_1");
						double x2 = rs.getInt("Pixel_x_2");
						double y2 = rs.getInt("Pixel_y_2");
						pixelDistance = getPythDistance(x1, y1, x2, y2);
						
						long1 = rs.getDouble("Longitude_point_1");
						lat1 = rs.getDouble("Latitude_point_1");
						double long2 = rs.getDouble("Longitude_point_2");
						double lat2 = rs.getDouble("Latitude_point_2");
						coordinateDistance = getMetresFromCoords(long1, lat1, long2, lat2);
						
						String scale = rs.getString("Scale");
						scale = scale.split(":")[1];
					}
					// The magic number - Investigate persisting this value 
					
					double pixelPerMetre = pixelDistance / coordinateDistance;
					
					double latPix = x1 + (pixelPerMetre * getMetresFromCoords(lat1, long1, latitude, long1));
					byte[] latPixBytes = ((Double)latPix).toString().getBytes();
					double longPix = y1 + (pixelPerMetre * getMetresFromCoords(lat1, long1, lat1, longitude));
					byte[] longPixBytes = ((Double)longPix).toString().getBytes();
				
					response.setContentType("text/plain");
					ServletOutputStream os = response.getOutputStream();
					os.write(latPixBytes);
					byte[] delimiter = new String(", ").getBytes();
					os.write(delimiter);
					os.write(longPixBytes);
					os.write(delimiter);
					byte[] floorArray = floor.getBytes();
					os.write(floorArray);
					byte[] buildingArray = building.getBytes();
					os.write(delimiter);
					os.write(buildingArray);
					response.setStatus(HttpServletResponse.SC_OK);
					os.flush();
				}
			*/	
			
				PreparedStatement stmt = conn.prepareStatement("SELECT Xpixel, Ypixel, Building FROM Floor_Contains LEFT JOIN Building ON Floor_Contains.Building = Building.Name WHERE Building.ShortCode=? AND Floor_Contains.Floor=? AND Floor_Contains.Room=?");
				stmt.setString(1, building);
				stmt.setString(2, floor);
				stmt.setString(3, location);
				if (stmt.execute()) {
					ResultSet rs = stmt.getResultSet();
					int xPixel = 0, yPixel = 0;
					while(rs.next()) {
						
						xPixel = rs.getInt("Xpixel");
						yPixel = rs.getInt("Ypixel");
						building = rs.getString("Building");
					}
					JSONObject json = new JSONObject();
					
					response.setContentType("application/json");
					try {
						json.put("x", xPixel);
						json.put("y", yPixel);
						json.put("floor", floor);
						json.put("building", building);
					} catch (JSONException e) {
						log.error(e);
						e.printStackTrace();
					}
					ServletOutputStream os = response.getOutputStream();
					os.write(json.toString().getBytes());
					response.setStatus(HttpServletResponse.SC_OK);
					os.flush();
				}
			}			
			conn.close();
		} 
		catch (SQLException e) {
		    log.error(e);
			e.printStackTrace();
		} 
		catch (IOException e) {
			log.error(e);
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		catch (NumberFormatException e) {
			log.error(e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	
//	private double getPythDistance(double x1, double y1, double x2, double y2) {
//		if (x1 == x2) {
//			/* return y distance */
//			return Math.abs(y1 - y2);
//		}
//		else if (y1 == y2) {
//			/* return x distance */
//			return Math.abs(x1 - x2);
//		}
//		else {
//			/* return pythagoras distance */		
//			double v1 = Math.abs(x1 - x2);
//			double v2 = Math.abs(y1 - y2);
//			return Math.sqrt(Math.pow(v1, 2.0) + Math.pow(v2, 2.0));
//			
//		}
//	}
//	
//	 private double getMetresFromCoords(double lat1, double long1, double lat2, double long2) {
//		 final double EARTHRAD = 3958.75;
//		 final int METRECONV = 1609;
//
//		 double dLat = Math.toRadians(lat2-lat1);
//		 double dLng = Math.toRadians(long2-long1);
//		 double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng/2) * Math.sin(dLng/2);
//		 double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//		 double dist = EARTHRAD * c;
//
//		 return new Double(dist * METRECONV);
//	 }
	 
	 
	 private AccessPoint verifyLocation(List<AccessPoint> accessPoints) {
		 /* Take the floor of the highest strength AP, get the MAC addresses of all other APs on that floor, 
		  * and determine how many of these other APs are in our list. 
		  */
		 
		 Map<String, Integer> floorFrequency = new HashMap<String, Integer>();
		 Map<String, Integer> buildingFrequency = new HashMap<String, Integer>();
		 
		 
		 if (accessPoints.size() == 1) {
			 return accessPoints.get(0);
		 }
		 else {
			 for (AccessPoint ap : accessPoints) {
				 String floor = ap.getFloorFromHostname();
				 String building = ap.getBuildingFromHostname();
				 
				 if (floorFrequency.containsKey(floor)) {
					 floorFrequency.put(floor, floorFrequency.get(floor) + 1);
				 }
				 else {
					 floorFrequency.put(floor, 1);
				 }
				 
				 if (buildingFrequency.containsKey(building)) {
					 buildingFrequency.put(building, buildingFrequency.get(building) + 1);
				 }
				 else {
					 buildingFrequency.put(building, 1);
				 }
				 
			 }
			 
			 Map.Entry<String, Integer> maxFloor = null;
			 for (Map.Entry<String, Integer> floor : floorFrequency.entrySet()) {
				 if (maxFloor == null || floor.getValue().compareTo(maxFloor.getValue()) > 0) {
					 maxFloor = floor;
				 }
			 }
			 if (maxFloor == null) {
				 return null;
			 }
			 String majorityFloor = new Integer(maxFloor.getKey()).toString();
			 
			 Map.Entry<String, Integer> maxBuilding = null;
			 for (Map.Entry<String, Integer > building : buildingFrequency.entrySet()) {
				 if (maxBuilding == null || building.getValue().compareTo(maxBuilding.getValue()) > 0) {
					 maxBuilding = building;
				 }
			 }
			 if (maxBuilding == null) {
				 return null;
			 }
			 String majorityBuilding = maxBuilding.getKey();
			 
			 for (AccessPoint ap : accessPoints) {
				 String[] accessPointDelimiter = ap.getHostname().split("-");
				 if (accessPointDelimiter[0].equals(majorityBuilding) && accessPointDelimiter[2].equals(majorityFloor)) {
					 log.info(majorityBuilding);
					 log.info(majorityFloor);
					 return ap;
				 }
				 
			 }
		 }
		 return null;
	 }
	 
	 private String parseMacAddress(String macIn){
		 StringBuilder mac = new StringBuilder();
		 log.info(macIn);
		 String[] macHexValues = macIn.split(":");
		 if (macHexValues.length == 6) {
			 String macFinalHex = macHexValues[5];
			 char[] macByteArray = macFinalHex.toCharArray();
			 if (macByteArray.length == 2) {
				 macByteArray[1] = '0';
			 }
			 macFinalHex = String.copyValueOf(macByteArray);
			 macHexValues[5] = macFinalHex;
			 mac.append(macHexValues[0]);
			 for (int x = 1; x < macHexValues.length; x++) {
				 mac.append(":");
				 mac.append(macHexValues[x]);
			 }
		 }
		 log.info(mac.toString());
		 
		 return mac.toString();
	 }
	 
}
