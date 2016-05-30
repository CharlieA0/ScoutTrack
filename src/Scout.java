import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.security.SecureRandom;


public class Scout extends DatabaseSearcher implements DatabaseObject, User{

	
	private int id;
	private Sql2o sql2o;
	
	/**
	 * Constructs a new Scout object and adds its profile info to the database
	 * @param sql2o sql2o database object
	 * @param name scout's name
	 * @param email scout's email
	 * @param pwd scout's password hash (not plaintext)
	 * @param rankID scout's rank's primary key in database
	 * @param age scout's age
	 * @param troopID scout's troop's primary key in database
	 */
	public Scout(Sql2o sql2o, String name, String email, String pwd, int rankID, int age, int troopID) {
		super(sql2o);
		this.sql2o = sql2o;
		this.id = addScout(name, email, pwd, rankID, age, troopID);
	}
	
	/**
	 * Constructs new Scout but doesn't add any information to database.
	 * @param id
	 */
	public Scout(int id, Sql2o sql2o){
		super(sql2o);
		this.sql2o = sql2o;
		this.id = id;
	}
	
	/**
	 * Removes scout from database
	 * @return true on success else false
	 */
	public void destroy() {
		super.deleteFrom(DatabaseNames.SCOUT_TABLE, id);
	}
	
	/**
	 * Returns scout id
	 * @return the scout's name
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Gets scout's name from database
	 * @return the scout's name
	 * @throws NoRecordFoundException 
	 * @throws Sql2oException 
	 */
	public String queryName() throws Sql2oException, NoRecordFoundException {
		return super.queryString(DatabaseNames.SCOUT_TABLE, "name", id);
	}
	
	/**
	 * Updates scout's name in database
	 * @return true on success else false
	 * @throws Sql2oException
	 */
	public void updateName(String name) throws Sql2oException {
		super.updateString(DatabaseNames.SCOUT_TABLE, "name", name, id);
	}
	
	/**
	 * Gets scout's email from database
	 * @return the scout's email
	 * @throws NoRecordFoundException 
	 * @throws Sql2oException 
	 */
	public String queryEmail() throws Sql2oException, NoRecordFoundException {
		return super.queryString(DatabaseNames.SCOUT_TABLE, "email", id);
	}
	
	/**
	 * Updates scout's email in database
	 * @param email the new email
	 * @return true on success else false
	 */
	public void updateEmail(String email) throws Sql2oException {
		super.updateString(DatabaseNames.SCOUT_TABLE, "email", email, id);
	}

	
	/**
	 * Gets scout's password hash from database
	 * @return the scout's password hash
	 * @throws NoRecordFoundException 
	 * @throws Sql2oException 
	 */
	public String queryPwd() throws Sql2oException, NoRecordFoundException {
		return super.queryString(DatabaseNames.SCOUT_TABLE, "email", id);
	}
	
	/**
	 * Updates scout's password hash in database
	 * @return true on success, else false
	 */
	public void updatePwd(String pwd) throws Sql2oException {
		super.updateString(DatabaseNames.SCOUT_TABLE, "pwd", pwd, id);
	}

	
	/**
	 * Gets scout's rank from database
	 * @return the scout's rank
	 * @throws NoRecordFoundException 
	 * @throws Sql2oException 
	 */
	public String queryRank() throws Sql2oException, NoRecordFoundException {
		int rankID = super.queryInt(DatabaseNames.RANK_TABLE, "name", id);
		return super.queryString(DatabaseNames.RANK_TABLE, "name", rankID);
	}
	
	/**
	 * Updates scout's rank in database
	 * @param the scout's new rank
	 * @return true if sucessful, else false
	 * @throws NoRecordFoundException 
	 * @throws Sql2oException 
	 */
	public void updateRank(String rank) throws Sql2oException, NoRecordFoundException {
		int rankID = super.searchId(DatabaseNames.RANK_TABLE, "name", rank);
		super.updateInt(DatabaseNames.SCOUT_TABLE, "rankid", rankID, id);
	}
	
	
	/**
	 * Gets scout's age from database
	 * @return the scout's age
	 * @throws NoRecordFoundException 
	 */
	public int queryAge() throws NoRecordFoundException, Sql2oException {
			return super.queryInt(DatabaseNames.SCOUT_TABLE, "age", id);
	}
	
	/**
	 * Updates scout's age in database
	 * @return true on success, else false
	 */
	public void updateAge(int age) throws Sql2oException {
		super.updateInt(DatabaseNames.SCOUT_TABLE, "age", age, id);
	}

	/**
	 * Gets scout's troop from database
	 * @return the  troop name
	 * @throws NoRecordFoundException 
	 * @throws Sql2oException 
	 */
	public String queryTroop() throws Sql2oException, NoRecordFoundException {
		int troopID = super.queryInt(DatabaseNames.SCOUT_TABLE, "troopid", id);
		return super.queryString(DatabaseNames.TROOP_TABLE, "name", troopID);
	}
	
	/**
	 * Updates scout's troop in database
	 * @throws NoRecordFoundException 
	 * @throws Sql2oException 
	 */
	public void updateTroop(String troop) throws Sql2oException, NoRecordFoundException {
		int troopID = super.searchId(DatabaseNames.TROOP_TABLE, "name", troop);
		super.updateInt(DatabaseNames.SCOUT_TABLE, "troopid", troopID, id);
	}
	
	/**
	 * Gets scout's salt from database
	 * @return the salt
	 * @throws NoRecordFoundException 
	 * @throws Sql2oException 
	 */
	public String querySalt() throws Sql2oException, NoRecordFoundException {
		return super.queryString(DatabaseNames.SCOUT_TABLE, "salt", id);
	}
		
	/**
	 * Adds scout to database
	 * @param name scout's name
	 * @param email scout's email
	 * @param pwd scout's password hash (not plaintext)
	 * @param rankID id of scout's rank in database
	 * @param age scout's age
	 * @param troopID id of scout's troop in database
	 * @return id of scout;
	 */
	private int addScout(String name, String email, String pwd, int rankID, int age, int troopID) {
		byte[] salt = getSalt();
		return storeScoutData(name, email, pwd, salt, age, troopID, rankID);
	}
	
	/**
	 * Gets random salt for password
	 * @return the random salt
	 */
	private byte[] getSalt() {
		byte[] salt = new byte[SALT_LENGTH];
		SecureRandom gen = new SecureRandom();
		gen.nextBytes(salt);	
		return salt;
	}
	
	/**
	 * Stores scout's data in database after validation
	 * @param name scout's name
	 * @param email scout's email
	 * @param pwd scout's password hash
	 * @param salt scout's salt
	 * @param age scout's age
	 * @param troopID id of scout's troop in database
	 * @param rankID id of the scout's rank in database
	 * @return id of the scout in database or -1 if database error
	 */
	private int storeScoutData(String name, String email, String pwd, byte[] salt, int age, int troopID, int rankID) {
		String sql = "insert into "+ DatabaseNames.SCOUT_TABLE + "(name, email, pwd, salt, age, id, rankid) VALUES (:name, :email, :password, :salt, :age, :troopID, :rankID)";
		try (Connection conn = sql2o.beginTransaction()) {
			int id = conn.createQuery(sql, true)
				.addParameter("name", name)
				.addParameter("email", email)
				.addParameter("password", pwd)
				.addParameter("salt", salt)
				.addParameter("age", age)
				.addParameter("troopID", troopID)
				.addParameter("rankID", rankID)
				.executeUpdate().getKey(Integer.class);
			
			conn.commit();			
			return id;
		}
		catch (Sql2oException e) {
			System.out.println(e);
			throw e;
		}
	}
}
