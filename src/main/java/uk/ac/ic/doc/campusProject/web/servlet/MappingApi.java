package uk.ac.ic.doc.campusProject.web.servlet;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import uk.ac.ic.doc.campusProject.utils.db.DatabaseConnectionManager;

public class MappingApi extends HttpServlet {

	private static final long serialVersionUID = 1L;
	Logger log = Logger.getLogger(MappingApi.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		
		Connection conn = DatabaseConnectionManager.getConnection("live");
		try {
			String type = request.getParameter("type");
			if (type.equals("manual")) {;
				String building = request.getParameter("building");
				int floor = new Integer(request.getParameter("floor"));
	
				PreparedStatement stmt = conn.prepareStatement("SELECT Plan FROM Floor WHERE Building=? AND Floor=?");
				stmt.setString(1, building);
				stmt.setInt(2, floor);
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
			else {
				double longitude = 0;
				double latitude = 0;
				double altitude = 0;
				String[] mac = new String[5];
				double[] ss = new double[5];
				try {
					longitude = Double.parseDouble(request.getParameter("longitude"));
					latitude = Double.parseDouble(request.getParameter("latitude"));
					altitude = Double.parseDouble(request.getParameter("altitude"));
					for (int x = 0 ; x < 5 ; x++) {
						String macThis = request.getParameter("mac" + x);
						String ssThis = request.getParameter("ss" + x);
						if (macThis != null && ssThis != null) {
							mac[x] = macThis;
							double ssDouble = Double.parseDouble(ssThis);
							ss[x] = ssDouble;
						}
					}
					
				} 
				catch (NullPointerException e) {
					log.error(e);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
				catch (NumberFormatException e) {
					log.error(e);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
				log.info("---------");
				log.info(latitude);
				log.info(longitude);
				log.info(altitude);
				log.info(mac.length);
				log.info(ss.length);
				
				/* Do database query to determine what building we are in, and what floor 
				 * We will assume Huxley Level 3 
				 * 
				 * Get the floor plan, scale and sync coordinates
				 * 
				 * Translate gps coordinates to pixel coordinates
				 * 
				 * TODO: Once pixel/coordinate values for aps are known, take mac address into account when doing pixel mapping
				 */
				
				String building = "Huxley";
				//int floor = 2;
				
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Building LEFT JOIN Building_Map_Attributes ON Name=Building WHERE Building=?");
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
					/* The magic number - Investigate persisting this value */
					
					//double metrePerPixel = coordinateDistance / pixelDistance;
					double pixelPerMetre = pixelDistance / coordinateDistance;
					
					double latPix = x1 + (pixelPerMetre * getMetresFromCoords(lat1, long1, latitude, long1));
					double longPix = y1 + (pixelPerMetre * getMetresFromCoords(lat1, long1, lat1, longitude));
					
					log.info("Latitude" + latPix);
					log.info("Longitude" + longPix);
					log.info("--------");


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
	}
	
	private double getPythDistance(double x1, double y1, double x2, double y2) {
		if (x1 == x2) {
			/* return y distance */
			return Math.abs(y1 - y2);
		}
		else if (y1 == y2) {
			/* return x distance */
			return Math.abs(x1 - x2);
		}
		else {
			/* return pythagoras distance */		
			double v1 = Math.abs(x1 - x2);
			double v2 = Math.abs(y1 - y2);
			return Math.sqrt(Math.pow(v1, 2.0) + Math.pow(v2, 2.0));
			
		}
	}
	
	 private double getMetresFromCoords(double lat1, double long1, double lat2, double long2) {
		 final double EARTHRAD = 3958.75;
		 final int METRECONV = 1609;

		 double dLat = Math.toRadians(lat2-lat1);
		 double dLng = Math.toRadians(long2-long1);
		 double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng/2) * Math.sin(dLng/2);
		 double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		 double dist = EARTHRAD * c;

		 return new Double(dist * METRECONV);
	 }


	

}
