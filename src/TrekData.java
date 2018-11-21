

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TrekData
 */
@WebServlet("/TrekData")
public class TrekData extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TrekData() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		int uid=Integer.parseInt(request.getParameter("uid").toString());
		int trek_id=Integer.parseInt(request.getParameter("trek_id").toString());
		// verify uid 
		JSONObject json=new JSONObject();
		try(Connection conn=DriverManager.getConnection(Config.url,Config.user,Config.password); ){
			// connection established 
			PreparedStatement stmt=conn.prepareStatement("select * from trek where trek_id=?");
			stmt.setInt(1, trek_id);
			ResultSet trek_rs=stmt.executeQuery(); 
			if(trek_rs.next()) {
				json.put("name",trek_rs.getString(1));
				int img_id=trek_rs.getInt(2); 
				PreparedStatement img_data=conn.prepareStatement("select img_data from images where img_id=?");
				img_data.setInt(1, img_id); 
				ResultSet img_data_rs=img_data.executeQuery();
				if(img_data_rs.next()) {
					json.put("img",img_data_rs.getString(1) );
				}
				PreparedStatement cord=conn.prepareStatement("select * from co_ordinates where trek_id=?");
				cord.setInt(1, trek_id);
				ResultSet cord_rs=cord.executeQuery();
				JSONArray cord_obj= new JSONArray(); 
				while(cord_rs.next()) {
					JSONObject tt=new JSONObject(); 
					tt.put("lattitude",cord_rs.getFloat(1));
					tt.put("longitude",cord_rs.getFloat(2));
					cord_obj.add(tt); 
				}
				json.put("co_ordinates", cord_obj);
				PreparedStatement places=conn.prepareStatement("select * from trek_place where trek_id=?"); 
				places.setInt(1, trek_id);
				ResultSet places_rs=places.executeQuery();
				JSONArray places_array=new JSONArray(); 
				while(places_rs.next()) {
					String name=places_rs.getString(2); 
					String country=places_rs.getString(2); 
					int co_id=places_rs.getInt(4); 
					PreparedStatement get_co=conn.prepareStatement("select * from co_ordinates where co_ordinate_id=?");
					get_co.setInt(1,co_id); 
					ResultSet co_rs=get_co.executeQuery();
					JSONObject po=new JSONObject();
					if(co_rs.next()) {
						po.put("lattitude", co_rs.getFloat(1));
						po.put("longitude", co_rs.getFloat(2));
					}
					places_array.add(po); 
				}
				json.put("places", places_array); 
				out.print(json);
				return; 
			}
			else {
				// handle not available case
			}
		}
		catch(Exception e) {
			// handle database error here 
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
