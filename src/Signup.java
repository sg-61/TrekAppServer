

import java.io.IOException;
import java.io.PrintWriter;
//import java.util.List; 
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

//import org.json.simple.*;

//import org.json.*;

import java.sql.*;

/**
 * Servlet implementation class Signup
 */
@WebServlet("/Signup")
public class Signup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Signup() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
//		HttpSession session = request.getSession();
//		if(session.getAttribute("id") != null) { // logged in
//			response.getWriter().print(DbHelper.okJson().toString());
//		}
//		else {
//			response.getWriter().print(DbHelper.errorJson("Not logged in"));
//		}
//		response.getWriter().print("<html><body>Not logged in</body></html>");
//		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
//		HttpSession session = request.getSession();
		String user_name = request.getParameter("user_name");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String verified= request.getParameter("verified");
		verified=new String("1"); 
		System.out.println(user_name);
		System.out.println(email);
		System.out.println(password);
		// verify the email part : to be done 
//		response.getWriter().println("<html><body>hi hello</body></html> ");
	    try(Connection conn=DriverManager.getConnection(Config.url,Config.user,Config.password); ){
	    	System.out.println("conn established"); 
	    	try {
				PreparedStatement stmt=conn.prepareStatement("select count(*) from users where email=?");
				stmt.setString(1, email);
				ResultSet rs=stmt.executeQuery();
				if(rs.next() && rs.getInt(1)>0) {
					// id is already registered; handle this case
					JSONObject json = new JSONObject();
					json.put("status","fail");
					json.put("message","email already in use");
					out.print(json); 
					return;
				}
				else {
//					System.out.println("hi this is me"); 
					if(verified.compareTo("1")==0) {
						System.out.println("going to insert");
						stmt=conn.prepareStatement("select max(uid) from users"); 
						rs=stmt.executeQuery(); 
						int uid=0; 
						if(rs.next()) {
							uid=rs.getInt(1); 
						}
						uid++; 
						PreparedStatement stmt1=conn.prepareStatement("insert into users (name,pass,email) values (?,?,?);");
						stmt1.setString(1, user_name);
						stmt1.setString(2, password);
						stmt1.setString(3, email);
//						stmt1.setInt(4, uid);
						stmt1.executeUpdate();
						JSONObject json = new JSONObject();
						json.put("status","success"); 
						json.put("message",""); 
						json.put("uid",uid); 
						out.print(json); 
						return;
					}
					if(Verify.verify(user_name,email,password)==false) {
//						System.out.println("hi this is me - 3");
						JSONObject json = new JSONObject();
						json.put("status","fail");
						json.put("message","invalid email");
						out.print(json); 
						return;
					}
				}
	    	}
	    	catch(Exception e) {
	    		// error in execution of some query
	    		e.printStackTrace();
	    		try {
		    		JSONObject json = new JSONObject();
					json.put("status","fail");
					json.put("message","database error");
					out.print(json); 
					return;
	    		}
	    		catch(Exception e1) {
	    			e1.printStackTrace();
	    		}
	    	}
	    }
	    catch(Exception e) {
	    	// return in response failure
	    	try {
		    	JSONObject json = new JSONObject();
				json.put("status","fail"); 
				json.put("message","database error");
				out.print(json); 
				return;
	    	}
	    	catch(Exception e1) {
	    		e1.printStackTrace(); 
	    	}
	    }
	}
}
