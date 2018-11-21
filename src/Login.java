

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.*;
import org.json.simple.*; 
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
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
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		try(Connection conn=DriverManager.getConnection(Config.url,Config.user,Config.password); ){
//			System.out.println("first");
			PreparedStatement stmt=conn.prepareStatement("select uid from users where email=? and pass=?");
			stmt.setString(1, email);
			stmt.setString(2, password);
//			System.out.println("second");
			ResultSet rs=stmt.executeQuery();
//			System.out.println("third");
			if(rs.next()) {
				int uid=rs.getInt(1); 
				JSONObject json=new JSONObject();
				json.put("status","success");
				json.put("uid", uid);
				out.print(json);
			}
			else { 
				JSONObject json=new JSONObject();
				json.put("status","fail");
				json.put("message", "wrong credentials");
				out.print(json);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			JSONObject json=new JSONObject();
			json.put("status","fail");
			json.put("message", "database error");
			out.print(json);
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
