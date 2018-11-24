

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;



/**
 * Servlet implementation class TrekImage
 */
@WebServlet("/TrekImage")
public class TrekImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	public static ObjectMapper mapper = new ObjectMapper();
    public TrekImage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
//		int uid=Integer.parseInt(request.getParameter("uid").toString());
		int trek_id=Integer.parseInt(request.getParameter("trek_id").toString());
		// verify uid 
		ObjectNode obj = mapper.createObjectNode();
		try(Connection conn=DriverManager.getConnection(Config.url,Config.user,Config.password); ){
			// connection established 
			PreparedStatement stmt=conn.prepareStatement("select img_data,extension from trek t,images i where t.trek_id=? and i.img_id=t.map_img");
			stmt.setInt(1, trek_id);
			ResultSet rs=stmt.executeQuery();
			if(rs.next()) {
				obj.put("status", "success");
				obj.put("img_data", rs.getString(1));
				obj.put("extension", rs.getString(2)); 
			}
			else {
				obj.put("status", "fail");
				obj.put("message", "image not available");
			}
			out.print(obj); return; 
		}
		catch(Exception e) {
			e.printStackTrace();
			obj.put("status", "fails"); 
			obj.put("message", "database error"); 
			out.print(obj); return; 
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
