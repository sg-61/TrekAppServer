

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
//		int uid=Integer.parseInt(request.getParameter("uid").toString());
		int trek_id=Integer.parseInt(request.getParameter("trek_id").toString());
		// check if this trek_id is present in database 
		JSONObject json=new JSONObject();
		
		
		try(Connection conn=DriverManager.getConnection(Config.url,Config.user,Config.password); ){
			// connection established
			
			PreparedStatement stmt=conn.prepareStatement("select * from trek where trek_id=?");
			stmt.setInt(1, trek_id);
			ResultSet trek_rs=stmt.executeQuery(); 
			if(trek_rs.next()) {
				json.put("name",trek_rs.getString(1));
				json.put("ne-lat", trek_rs.getDouble(3));
				json.put("ne-long", trek_rs.getDouble(4));
				json.put("sw-lat", trek_rs.getDouble(5));
				json.put("sw-long", trek_rs.getDouble(6));
//				int img_id=trek_rs.getInt(2); 
//				PreparedStatement img_data=conn.prepareStatement("select img_data from images where img_id=?");
//				img_data.setInt(1, img_id); 
//				ResultSet img_data_rs=img_data.executeQuery();
//				if(img_data_rs.next()) {
//					json.put("img",img_data_rs.getString(1) );
//				}
				PreparedStatement sps=conn.prepareStatement("select c.lattitude,c.longitude from start s, co_ordinates c where c.co_ordinate_id=s.coordinate_id");
				ResultSet sps_rs=sps.executeQuery();
				JSONArray spsa=new JSONArray(); 
				while(sps_rs.next()) {
					JSONObject obj= new JSONObject(); 
					obj.put("lat", sps_rs.getDouble(1)); 
					obj.put("long", sps_rs.getDouble(2));
					spsa.add(obj); 
				}
				json.put("start_points", spsa); 
				PreparedStatement cord=conn.prepareStatement("select lattitude,longitude,co_ordinate_id from co_ordinates where trek_id=?");
				cord.setInt(1, trek_id);
				ResultSet cord_rs=cord.executeQuery();
				JSONArray cord_obj= new JSONArray(); 
				while(cord_rs.next()) {
					JSONObject tt=new JSONObject(); 
					tt.put("lat",cord_rs.getFloat(1));
					tt.put("long",cord_rs.getFloat(2));
					tt.put("id", cord_rs.getFloat(3));
					cord_obj.add(tt); 
				}
				json.put("co_ordinates", cord_obj);
				PreparedStatement places=conn.prepareStatement("select p.place_id,p.place_name,p.co_ordinate_id,c.lattitude,c.longitude from trek_place p,co_ordinates c where p.trek_id=? and p.co_ordinate_id=c.co_ordinate_id"); 
				places.setInt(1, trek_id);
				ResultSet places_rs=places.executeQuery();
				JSONArray places_array=new JSONArray(); 
				while(places_rs.next()) {
					String name=places_rs.getString(2); 
//					String country=places_rs.getString(2); 
					int co_id=places_rs.getInt(3); 
//					PreparedStatement get_co=conn.prepareStatement("select * from co_ordinates where co_ordinate_id=?");
//					get_co.setInt(1,co_id); 
//					ResultSet co_rs=get_co.executeQuery();
					JSONObject po=new JSONObject();
//					if(co_rs.next()) {
					po.put("lat", places_rs.getFloat(4));
					po.put("long", places_rs.getFloat(5));
					po.put("name", name); 
					po.put("co_id", co_id);
					po.put("place_id", places_rs.getInt(1));
					places_array.add(po);
//					} 
				}
				json.put("places", places_array); 
				PreparedStatement paths=conn.prepareStatement("select c1.lattitude,c1.longitude,c2.lattitude,c2.longitude,s.count,c1.co_ordinate_id,c2.co_ordinate_id from path_segment s,co_ordinates c1,co_ordinates c2 where s.co_ordinate_id1=c1.co_ordinate_id and s.co_ordinate_id2=c2.co_ordinate_id and c1.trek_id=? and c1.trek_id=c2.trek_id");
				paths.setInt(1, trek_id);
				ResultSet paths_rs=paths.executeQuery(); 
				JSONArray paths_array=new JSONArray(); 
				while(paths_rs.next()) {
					JSONObject co=new JSONObject(); 
					co.put("flat", paths_rs.getFloat(1));
					co.put("flong", paths_rs.getFloat(2));
					co.put("slat", paths_rs.getFloat(3));
					co.put("slong", paths_rs.getFloat(4));
					co.put("fco_id", paths_rs.getInt(6));
					co.put("sco_id", paths_rs.getInt(7)); 
					co.put("count", paths_rs.getInt(5));
					paths_array.add(co); 
				}
				json.put("paths",paths_array);
				json.put("status", "success"); 
				out.print(json);
				return; 
			}
			else {
				json.put("status","fail" );
				json.put("message", "invalid trek_id"); 
				out.print(json); 
				// handle not available case
			}
		}
		catch(Exception e) {
			json.put("status","fail" );
			json.put("message", "database error"); 
			out.print(json); 
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
