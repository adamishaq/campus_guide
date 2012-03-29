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

public class PlacesApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Logger log = Logger.getLogger(PlacesApi.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = DatabaseConnectionManager.getConnection("live");
		String building = request.getParameter("building");
		String floor = request.getParameter("floor");
		String left = request.getParameter("left");
		String right = request.getParameter("right");
		String top = request.getParameter("top");
		String bottom = request.getParameter("bottom");
		
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT Number, Type, Description, Image FROM Room LEFT JOIN Floor_Contains ON Number=Room AND Room.Building=Floor_Contains.Building LEFT JOIN Building ON Floor_Contains.Building=Building.Name WHERE Building.ShortCode=? AND Floor=?" +
															" AND Xpixel <= ? AND Xpixel >= ? AND Ypixel <= ? AND Ypixel >= ?");
			stmt.setString(1, building);
			stmt.setString(2, floor);
			stmt.setString(3, right);
			stmt.setString(4, left);
			stmt.setString(5, bottom);
			stmt.setString(6, top);
			
			if (stmt.execute()) {
				ResultSet rs = stmt.getResultSet();
				response.setContentType("text/plain");
				ServletOutputStream os = response.getOutputStream();
				byte[] delimiter = new String("%%").getBytes();
				int x = 0 ;
				while(rs.next()) {
					os.write(rs.getString("Number").getBytes());
					os.write(delimiter);
					os.write(rs.getString("Type").getBytes());
					os.write(delimiter);
					os.write(rs.getString("Description").getBytes());
					os.write(delimiter);
					Blob blob = rs.getBlob("Image");
					if (blob.length() == 0) {
						os.write("NULL".getBytes());
						os.write(delimiter);
					}
					else {
						os.write(blob.getBytes(1, (int)blob.length()));
						os.write(delimiter);
					}
					x++;
				}
				if (x == 0) {
					os.write("NOTHING".getBytes());
					os.write(delimiter);
				}
				response.setStatus(HttpServletResponse.SC_OK);
				os.flush();
			}
			conn.close();
			
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
		
		
		
	}

}
