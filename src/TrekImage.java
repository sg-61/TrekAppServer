

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.sun.org.apache.xml.internal.security.utils.Base64;
//import org.apache.commons.codec.binary.Base64;




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
				String s=rs.getString(1);
//				byte[] decodedHex = Hex.decodeHex(s);
				int len = s.length()-3;
			    byte[] data = new byte[len / 2];
			    for (int i = 3; i < len; i += 2) {
			        data[(i-3) / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
			                             + Character.digit(s.charAt(i+1), 16));
			    }
				byte[] encodedHexB64 = Base64.getEncoder().encode(data);
				
				/////////////
				File file = new File("/home/shubham/Documents/SEM5/Database/Project/images/sameer_hills.jpg");
				BufferedImage image = null; 
				byte[] dd=null;
			    try
			    {
			        image = ImageIO.read(file);
			        ByteArrayOutputStream baos = new ByteArrayOutputStream();
			        ImageIO.write(image, "jpg", baos);
			        byte[] bytes = baos.toByteArray();
			        System.out.println();
			        dd = Base64.getEncoder().encode(bytes);
			    
			    } 
			    catch (IOException e) 
			    {
			        e.printStackTrace();
			    }////////////
				obj.put("img_data", dd);
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
