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


	

}
