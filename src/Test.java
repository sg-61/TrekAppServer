import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Test{
	public static void main(String[] args) throws ParseException {
		String data="{\"name\":\"sameer\", \"status\": \"success\"}"; 
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(data); 
        JSONObject json =  (JSONObject) obj;
        String s=(String) json.get("name");
        System.out.println(s);
	}
}