import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

 

public class Helper {
	public static class PairIA{
		int cnt; 
		ArrayNode arr; 
	}
	public static final String DATA_LABEL = "data";
	public static final String MSG_LABEL = "message";
	public static final String STATUS_LABEL = "status";
	
	public static ObjectMapper mapper = new ObjectMapper();
	
	public static ObjectNode errorJson(String errorMsg) {
		ObjectNode node = mapper.createObjectNode();
		node.put(STATUS_LABEL, "fail");
		node.put(MSG_LABEL, errorMsg);
		return node;
	}
	
	/**
	 * Returns the results as a JSON array object.
	 * Use toString() on the result to get JSON string.
	 */
	public static PairIA resultSetToJson(ResultSet rs) throws SQLException {
		ArrayNode arr = mapper.createArrayNode();

		ResultSetMetaData rsmd = rs.getMetaData();
		int cnt=0;
		while(rs.next()) {
			cnt++;
			int numColumns = rsmd.getColumnCount();
			ObjectNode obj = mapper.createObjectNode();
			
 			for (int i=1; i<numColumns+1; i++) {
				String column_name = rsmd.getColumnName(i);
				if(rs.getObject(column_name) == null) {
					obj.putNull(column_name);
					continue;
				}
				
				if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
					obj.put(column_name, rs.getInt(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
					obj.put(column_name, rs.getBoolean(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
					obj.put(column_name, rs.getDouble(column_name)); 
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
					obj.put(column_name, rs.getFloat(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
					obj.put(column_name, rs.getInt(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
					obj.put(column_name, rs.getNString(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
					obj.put(column_name, rs.getString(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
					obj.put(column_name, rs.getInt(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
					obj.put(column_name, rs.getInt(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
					obj.put(column_name, rs.getDate(column_name).toString());
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
					obj.put(column_name, rs.getTimestamp(column_name).toString());   
				}
				else{
					obj.put(column_name, rs.getObject(column_name).toString());
				}
			}
			arr.add(obj);
		}
		PairIA ret=new PairIA(); 
		ret.cnt=cnt; 
		ret.arr=arr; 
		return ret; 
	}
}
