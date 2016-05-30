package testers;
import org.sql2o.Sql2o;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ScoutMapper;
import ScoutTrackNotValidatedException;

public class ScoutMapperTester {
	private static final String DB_NAME = "scouttrack";
	private static final String DB_USERNAME = "Charlie";
	private static final String DB_PASSWORD = "Conway";
	private static Sql2o sql2o = new Sql2o("jdbc:postgresql://localhost:5432/" + DB_NAME, DB_USERNAME, DB_PASSWORD);
	
	public static void main(String[] args) {
		System.out.println(testValidJson());
	}
	
	public static int testValidJson() {
		String jsonString = "{\"name\": \"Abe\", \"email\":\"Abe@foo.net\", \"pwd\":\"jksdfajklb2bib34uh899h49iuui2389\", \"rank\": \"Eagle\", \"troop\": \"Troop 5\", \"age\" : 16}";
		JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
		ScoutMapper testMap = new ScoutMapper(json, sql2o);
		testMap.validate();
		try {
			return testMap.getScout().getID();
		} catch (ScoutTrackNotValidatedException e) {
			e.printStackTrace();
			return -1;
		}
	}

}
