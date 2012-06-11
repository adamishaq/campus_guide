package uk.ac.ic.doc.campusProject.web.servlet;

import java.io.IOException;
import java.security.InvalidParameterException;
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
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.ic.doc.campusProject.utils.db.DatabaseConnectionManager;

public class PlacesApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Logger log = Logger.getLogger(PlacesApi.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = DatabaseConnectionManager.getConnection("live");
		PreparedStatement stmt;
		log.info(request.getRequestURI());
		String requestUri = request.getRequestURI().replaceFirst("/", "").trim();
		String[] uri = requestUri.split("/");
		
		try {
			if (!(uri[1].equals("api-places"))) {
				log.info("Issue");
				throw new InvalidParameterException("Bad first parameter passed");
			}
			else {
				String requestType = uri[2];
				log.info(requestType);
				if (requestType.equals("pixels")) {
					String building = uri[3];
					String floor = uri[4];
					stmt = conn.prepareStatement("SELECT Number, Xpixel, Ypixel FROM Room LEFT JOIN Floor_Contains ON Number=Room AND Room.Building=Floor_Contains.Building LEFT JOIN Building ON Floor_Contains.Building=Building.Name WHERE Building.Name=? AND Floor=?");
					stmt.setString(1, building);
					stmt.setString(2, floor);
					if (stmt.execute()) {
						ResultSet rs = stmt.getResultSet();
						response.setContentType("application/json");
						ServletOutputStream os = response.getOutputStream();
						StringBuilder jsonArray = new StringBuilder();
						jsonArray.append("[");
						while(rs.next()) {
							JSONObject jsonObject = new JSONObject();
							String number = rs.getString("Number");
							jsonObject.put("room", number);
							jsonObject.put("x", rs.getInt("Xpixel"));
							jsonObject.put("y", rs.getInt("Ypixel"));
							jsonArray.append(jsonObject.toString());
							jsonArray.append(",");
						}
						if (jsonArray.length() > 1) {
							jsonArray = jsonArray.replace(jsonArray.length() - 1, jsonArray.length(), "");
						}
						jsonArray.append("]");
						os.write(jsonArray.toString().getBytes());
						response.setStatus(HttpServletResponse.SC_OK);
						os.flush();
					}
					conn.close();
				}
				else if (requestType.equals("roominfo")) {
					String building = uri[3];
					String floor = uri[4];
					if (uri.length == 5) {
						stmt = conn.prepareStatement("SELECT Number, Type, Description, Room.Building FROM Room LEFT JOIN Floor_Contains ON Number=Room AND Room.Building=Floor_Contains.Building LEFT JOIN Building ON Floor_Contains.Building=Building.Name WHERE Building.Name=? AND Floor=?");
						stmt.setString(1, building);
						stmt.setString(2, floor);
						if (stmt.execute()) {
							ResultSet rs = stmt.getResultSet();
							response.setContentType("application/json");
							ServletOutputStream os = response.getOutputStream();
							StringBuilder jsonArray = new StringBuilder();
							jsonArray.append("[");
							while(rs.next()) {
								JSONObject jsonObject = new JSONObject();
								String number = rs.getString("Number");
								jsonObject.put("number", number);
								jsonObject.put("type", rs.getString("Type"));
								jsonObject.put("description", rs.getString("Description"));
								jsonObject.put("image", new byte[]{});
								jsonObject.put("building", rs.getString("Building"));
								jsonArray.append(jsonObject.toString());
								jsonArray.append(",");

							}
							if (jsonArray.length() > 1) {
								jsonArray = jsonArray.replace(jsonArray.length() - 1, jsonArray.length(), "");
							}
							jsonArray.append("]");
							os.write(jsonArray.toString().getBytes());
							response.setStatus(HttpServletResponse.SC_OK);
							os.flush();
						}
						conn.close();
					}
					else {
						String room = uri[5];
						stmt = conn.prepareStatement("SELECT Number, Type, Description, Room.Building FROM Room LEFT JOIN Floor_Contains ON Room.Number=Floor_Contains.Room AND Room.Building=Floor_Contains.Building LEFT JOIN Building ON Floor_Contains.Building=Building.Name WHERE Building.Name=? AND Floor=?" +
								" AND Room.Number= ?");
						stmt.setString(1, building);
						stmt.setString(2, floor);
						stmt.setString(3, room);
						if (stmt.execute()) {
							ResultSet rs = stmt.getResultSet();
							response.setContentType("application/json");
							ServletOutputStream os = response.getOutputStream();
							JSONObject jsonObject = new JSONObject();
							while(rs.next()) {
								String number = rs.getString("Number");
								jsonObject.put("number", number);
								jsonObject.put("type", rs.getString("Type"));
								jsonObject.put("description", rs.getString("Description"));
								jsonObject.put("image", new byte[]{});
								jsonObject.put("building", rs.getString("Building"));

							}
							os.write(jsonObject.toString().getBytes());
							response.setStatus(HttpServletResponse.SC_OK);
							os.flush();
						}
						conn.close();
					}
				}
				else if (requestType.equals("image")) {
					String building = uri[3];
					String floor = uri[4];
					String room = uri[5];
					stmt = conn.prepareStatement("SELECT Image FROM Room LEFT JOIN Floor_Contains ON Room.Number=Floor_Contains.Room AND Room.Building=Floor_Contains.Building LEFT JOIN Building ON Floor_Contains.Building=Building.Name WHERE Building.Name=? AND Floor=?" +
							" AND Room.Number= ?");
					stmt.setString(1, building);
					stmt.setString(2, floor);
					stmt.setString(3, room);
					if (stmt.execute()) {
						ResultSet rs = stmt.getResultSet();
						response.setContentType("application/json");
						ServletOutputStream os = response.getOutputStream();
						while(rs.next()) {
							Blob blob = rs.getBlob("Image");
							if (blob == null) {
								//UPLOAD DUMMY IMAGE 
								os.write(new byte[]{});
								response.setStatus(HttpServletResponse.SC_NO_CONTENT);
							}
							else {
								os.write(blob.getBytes(1, (int)blob.length()));
								response.setStatus(HttpServletResponse.SC_OK);
							}
						}

						os.flush();
					}
					conn.close();
				}
				else if (requestType.equals("buildinglist")) { 
					stmt = conn.prepareStatement("SELECT Name FROM Building");
					if (stmt.execute()) {
						ResultSet rs = stmt.getResultSet();
						ServletOutputStream os = response.getOutputStream();
						response.setContentType("application/json");
						StringBuilder jsonArray = new StringBuilder();
						jsonArray.append("[");
						while(rs.next()) {
							JSONObject jsonObject = new JSONObject();
							String building = rs.getString("Name");
							jsonObject.put("building", building);
							jsonArray.append(jsonObject.toString());
							jsonArray.append(",");

						}
						if (jsonArray.length() > 1) {
							jsonArray = jsonArray.replace(jsonArray.length() - 1, jsonArray.length(), "");
						}
						jsonArray.append("]");
						os.write(jsonArray.toString().getBytes());
						response.setStatus(HttpServletResponse.SC_OK);
						os.flush();
					}
				}
				else if (requestType.equals("floorlist")) {
					String building = uri[3];
					stmt = conn.prepareStatement("SELECT Floor.Floor FROM Floor LEFT JOIN Building ON Building.Name=Floor.Building WHERE Building.Name=?");
					stmt.setString(1, building);
					if (stmt.execute()) {
						ResultSet rs = stmt.getResultSet();
						ServletOutputStream os = response.getOutputStream();
						response.setContentType("application/json");
						StringBuilder jsonArray = new StringBuilder();
						jsonArray.append("[");
						while(rs.next()) {
							JSONObject jsonObject = new JSONObject();
							String floor = rs.getString("Floor.Floor");
							jsonObject.put("floor", floor);
							jsonArray.append(jsonObject.toString());
							jsonArray.append(",");

						}
						jsonArray = jsonArray.replace(jsonArray.length() - 1, jsonArray.length(), "");
						jsonArray.append("]");
						os.write(jsonArray.toString().getBytes());
						response.setStatus(HttpServletResponse.SC_OK);
						os.flush();
					}

				}
				else {
					throw new InvalidParameterException("Bad parameters passed");
				}
			}	
		}
		catch (InvalidParameterException e) {
			try {
				log.error(e);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			try {
				log.error(e);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


}
