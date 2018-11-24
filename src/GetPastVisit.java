

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class GetPastVisit
 */
@WebServlet("/GetPastVisit")
public class GetPastVisit extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetPastVisit() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		if(request.getParameter("uid") == null) { //not logged in
			response.getWriter().print(Helper.errorJson("Not logged in").toString());
			return;
		}
		int uid = Integer.parseInt(request.getParameter("uid").toString());
//		int uid = 1;
		
//		String query = "select place_name from visit v, trek_place p where uid=? and p.place_id = v.place_id;";
		String query=" select DISTINCT t.name,p.trek_id from visit  v, trek_place p, trek t where uid=? and p.place_id = v.place_id and t.trek_id=p.trek_id";
		ArrayNode json = null;
		try (Connection conn = DriverManager.getConnection(Config.url, Config.user, Config.password))
        {
            conn.setAutoCommit(false);
            try(PreparedStatement stmt = conn.prepareStatement(query)) {
//            	setParams(stmt, paramTypes, params);
            	stmt.setInt(1, uid);
                ResultSet rs = stmt.executeQuery();
                Helper.PairIA ret = Helper.resultSetToJson(rs);
                json=ret.arr; 
                conn.commit();
            }
            catch(Exception ex)
            {
                conn.rollback();
                throw ex;
            }
            finally{
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
        	response.getWriter().print(Helper.errorJson(e.getMessage()).toString());
        	return;
        }
		
		ObjectNode node = Helper.mapper.createObjectNode();
    	node.putArray(Helper.DATA_LABEL).addAll(json);
    	node.put(Helper.STATUS_LABEL, true);
		String result = node.toString();
    	response.getWriter().print(result);
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
