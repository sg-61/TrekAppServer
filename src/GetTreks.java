

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
import javax.servlet.http.HttpSession;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class GetTreks
 */
@WebServlet("/GetTreks")
public class GetTreks extends HttpServlet {
	//private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTreks() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		/*HttpSession session = request.getSession();
		if(session.getAttribute("id") == null) { //not logged in
			response.getWriter().print(Helper.errorJson("Not logged in").toString());
			return;
		}*/
		String query = "select name, trek_id from trek;";
		ArrayNode json = null;
		try (Connection conn = DriverManager.getConnection(Config.url, Config.user, Config.password))
        {
            conn.setAutoCommit(false);
            try(PreparedStatement stmt = conn.prepareStatement(query)) {
//            	setParams(stmt, paramTypes, params);
                ResultSet rs = stmt.executeQuery();
                Helper.PairIA ret = Helper.resultSetToJson(rs);
//                if(ret.cnt==0) {
//                	response.getWriter().print(Helper.errorJson("invalid uid").toString());
//                	return; 
//                }
                json = ret.arr; 
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
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
