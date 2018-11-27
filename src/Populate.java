import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*; 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;




public class Populate {
	public static int insert_images(String path,String extension, Connection conn) throws SQLException {
		PreparedStatement stmt=conn.prepareStatement("insert into images values(pg_read_binary_file(?),?);");
		stmt.setString(1, path);
		stmt.setString(2, extension);
		stmt.executeUpdate(); 
		stmt=conn.prepareStatement("select max(img_id) from images");
		ResultSet rs=stmt.executeQuery(); 
		if(rs.next()) {
			return rs.getInt(1); 
		}
		return -1; 
	}
	public static int insert_trek(String name, int img_id, Double ne_lat, Double ne_long, Double sw_lat, Double sw_long, Connection conn) throws SQLException {
		PreparedStatement stmt=conn.prepareStatement("insert into trek values(?,?,?,?,?,?);");
		stmt.setString(1, name);
		stmt.setInt(2, img_id);
		stmt.setDouble(3, ne_lat);
		stmt.setDouble(4, ne_long);
		stmt.setDouble(5, sw_lat);
		stmt.setDouble(6, sw_long);
		stmt.executeUpdate(); 
		stmt=conn.prepareStatement("select max(trek_id) from trek");
		ResultSet rs=stmt.executeQuery(); 
		if(rs.next()) {
			return rs.getInt(1); 
		}
		return -1; 
	}
	public static int insert_trek_place(int trek_id, String name, String country , Double lattitude, Double longitude, Connection conn) throws SQLException {
		int id=get_coordinate_id(lattitude,longitude,conn); 
		PreparedStatement stmt=conn.prepareStatement("insert into trek_place values(?,?,?,?)");
		stmt.setInt(1, trek_id); 
		stmt.setString(2, name);
		stmt.setString(3, country);
		stmt.setInt(4, id);
		stmt.executeUpdate();  
		return 1; 
	}
	public static int insert_co_ordinates(Double lattitude, Double longitude, Double altitude, int trek_id, Connection conn) throws SQLException{
		PreparedStatement stmt=conn.prepareStatement("insert into co_ordinates values(?,?,?,?)");
		stmt.setDouble(1,lattitude); 
		stmt.setDouble(2, longitude);
		stmt.setDouble(3, altitude);
		stmt.setInt(4, trek_id);
		stmt.executeUpdate(); 		
		return 1; 
	}
	public static int get_coordinate_id(Double lattitude, Double longitude, Connection conn) throws SQLException{
		PreparedStatement stmt=conn.prepareStatement("select co_ordinate_id from co_ordinates where lattitude=? and longitude=?"); 
		stmt.setDouble(1, lattitude);
		stmt.setDouble(2, longitude);
//		stmt.setInt(3, trek_id);
		ResultSet rs=stmt.executeQuery();
		if(rs.next()) {
//			System.out.println("found co_ordinate id");
			return rs.getInt(1); 
		}
//		System.out.println("did not found co_ordinate id");
		return -1; 
	}
	public static int insert_path_segment(Double flat,Double flong, Double slat, Double slong, int count, Connection conn) throws SQLException{
		int id1=get_coordinate_id(flat,flong,conn); 
		int id2=get_coordinate_id(slat,slong,conn); 
		PreparedStatement stmt=conn.prepareStatement("insert into path_segment values(?,?,?);"); 
		stmt.setInt(1, id1);
		stmt.setInt(2, id2);
		stmt.setInt(3, count);
		stmt.executeUpdate(); 
		return 1; 
	}
	public static int insert_start(Double lat, Double longi, int trek_id,Connection conn) throws SQLException{
		int id=get_coordinate_id(lat,longi,conn);
		PreparedStatement stmt = conn.prepareStatement("insert into start values(?,?);"); 
		stmt.setInt(1, id);
		stmt.setInt(2, trek_id);
		stmt.executeUpdate(); 
		return 1; 
	}
	public static void populate(String data) {
		// TODO Auto-generated method stub
//		String path=new String("/home/shubham/Documents/SEM5/Database/Project/data/sameer_hill_data.json"); 
		JSONParser parser = new JSONParser(); 
		try {
            Object obj = parser.parse(data);
            JSONObject json =  (JSONObject) obj;
            if(json.containsKey("images")==false) {
            	json.put("images", "images/trek.jpg");
            }
//            System.out.println(json.toString()); 
            List<Pair<Double,Double>> coordinates=new ArrayList<Pair<Double,Double>>();
            JSONArray path_segs=(JSONArray) json.get("path_segment"); 
            for(int i=0;i<path_segs.size();i++) {
            	JSONArray path_seg=(JSONArray) path_segs.get(i); 
            	for(int j=0;j<path_seg.size();j++) {
            		JSONObject coord=(JSONObject)path_seg.get(j); 
            		coordinates.add(new Pair(coord.get("lat"),coord.get("long")));
            	}	
//            	System.out.println();
            }
            Collections.sort(coordinates, new CoordinateCompare());
//            for(int i=0;i<coordinates.size();i++) {
//            	coordinates.get(i).print();   
//            }
            try(Connection conn=DriverManager.getConnection(Config.url,Config.user,Config.password); ){
            	try {
	            	int img_id=insert_images(json.get("images").toString(),json.get("images").toString().split("\\.")[1],conn);
	            	JSONObject trek_obj=(JSONObject) json.get("trek"); 
	            	int trek_id=insert_trek(trek_obj.get("name").toString(),img_id,Double.parseDouble(trek_obj.get("ne_lat").toString()),Double.parseDouble(trek_obj.get("ne_long").toString()),Double.parseDouble(trek_obj.get("sw_lat").toString()),Double.parseDouble(trek_obj.get("sw_long").toString()),conn);
	            	// insert all co_ordinates at once -- there is an assumption that all co-ordinates are on some path segment so place co_ordinates are also on path
	            	for(int i=0;i<coordinates.size();i++) {
	            		Pair<Double,Double> p=coordinates.get(i); 
	            		if(i==0) {
	            			insert_co_ordinates(p.getLeft(),p.getRight(),new Double(0),trek_id,conn); 
	            		}
	            		else if(p.equals(coordinates.get(i-1))==false){
	            			insert_co_ordinates(p.getLeft(),p.getRight(),new Double(0),trek_id,conn);
	            		}
	            	}
	            	JSONArray tpa=(JSONArray) json.get("trek_place"); 
	            	for(int i=0;i<tpa.size();i++) {
	            		JSONObject tp=(JSONObject) tpa.get(i); 
	            		int status=insert_trek_place(trek_id,tp.get("place_name").toString(),tp.get("country").toString(),Double.parseDouble(tp.get("lat").toString()), Double.parseDouble(tp.get("long").toString()),conn);
	            	}
	            	for(int i=0;i<path_segs.size();i++) {
	                	JSONArray path_seg=(JSONArray) path_segs.get(i); 
	                	for(int j=1;j<path_seg.size();j++) {
	                		JSONObject f=(JSONObject)path_seg.get(j-1);
	                		JSONObject s=(JSONObject)path_seg.get(j); 
	                		insert_path_segment(Double.parseDouble(f.get("lat").toString()), Double.parseDouble(f.get("long").toString()),Double.parseDouble(s.get("lat").toString()),Double.parseDouble(s.get("long").toString()),0,conn); 
	                	}
	                }
	            	JSONArray spsa=(JSONArray) json.get("start_points");
	            	for(int i=0;i<spsa.size();i++) {
	            		JSONObject sp=(JSONObject) spsa.get(i);
	            		insert_start(Double.parseDouble(sp.get("lat").toString()),Double.parseDouble(sp.get("long").toString()),trek_id,conn); 
	            	}
            	}
            	catch(Exception e) {
            		e.printStackTrace();
            	}
            }
            catch(Exception e) {
            	// handle database exception
            	e.printStackTrace();
            }
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}

class CoordinateCompare implements Comparator< Pair<Double,Double> >{
	@Override
	public int compare(Pair<Double,Double> a, Pair<Double,Double> b) {
		if(a.getLeft()<b.getLeft()) return -1; 
		else if(a.getLeft()==b.getLeft() && a.getRight() < b.getRight()) return -1; 
		return 1; 
	}
}
class Pair<L,R> {

	  private final L left;
	  private final R right;

	  public Pair(L left, R right) {
	    this.left = left;
	    this.right = right;
	  }

	  public L getLeft() { return left; }
	  public R getRight() { return right; }

	  @Override
	  public int hashCode() { return left.hashCode() ^ right.hashCode(); }
	  public void print() {
		  System.out.println(left.toString() + " " + right.toString());
	  }
	  @Override
	  public boolean equals(Object o) {
	    if (!(o instanceof Pair)) return false;
	    Pair pairo = (Pair) o;
	    return this.left.equals(pairo.getLeft()) &&
	           this.right.equals(pairo.getRight());
	  }

	}
