package testers;
import org.sql2o.Sql2o;

import Scout;

public class ScoutTester {
	private static final String DB_NAME = "scouttrack";
	private static final String DB_USERNAME = "Charlie";
	private static final String DB_PASSWORD = "Conway";
	
	public static void main(String[] args) {
		Sql2o sql2o = new Sql2o("jdbc:postgresql://localhost:5432/" + DB_NAME, DB_USERNAME, DB_PASSWORD);
		Scout scout1 = new Scout(sql2o, "Charlie", "charlie@foo.bar", "hj13jkbjdfbb3i2buio3987b4bue4bnui3b76cs09hu8bf2bu923f239b23fbbhj", 7, 17, 1);
		System.out.println("getID() returns " +  scout1.getID());
		System.out.println("queryName() returns " + scout1.queryName() + " and should return Charlie");
		System.out.println("queryEmail() returns " + scout1.queryEmail() + " and should return charlie@foo.bar");
		System.out.println("queryAge() returns " + scout1.queryAge() + " and should return 17");
		System.out.println("queryRank() returns " + scout1.queryRank() + " and should return Eagle");
		System.out.println("queryTroop() returns " + scout1.queryTroop() + " and should return Troop 5");
		System.out.println("queryPwd() returns " + scout1.queryPwd() + " and should return hj13jkbjdfbb3i2buio3987b4bue4bnui3b76cs09hu8bf2bu923f239b23fbbhj\n");
		
		System.out.println("destroy() returns " + scout1.destroy() + " and should return true\n");
		
		System.out.println("getID() returns " +  scout1.getID());
		System.out.println("queryName() returns " + scout1.queryName() + " and should return null");
		System.out.println("queryEmail() returns " + scout1.queryEmail() + " and should return null");
		System.out.println("queryAge() returns " + scout1.queryAge() + " and should return 500");
		System.out.println("queryRank() returns " + scout1.queryRank() + " and should return null");
		System.out.println("queryTroop() returns " + scout1.queryTroop() + " and should return null");
		System.out.println("queryPwd() returns " + scout1.queryPwd() + " and should return null");
	}

}
