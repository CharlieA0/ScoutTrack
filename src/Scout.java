import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


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
	 * @throws InvalidDatabaseOperation thrown if invalid operation is performed on database
	 * @throws Sql2oException thrown if database error occurs
	 */
	public Scout(Sql2o sql2o, String name, String email, String pwd, int rankID, int age, int troopID, int[] reqID, int[] mbID) throws Sql2oException, InvalidDatabaseOperation {
		super(sql2o);
		this.sql2o = sql2o;
		this.id = addScout(name, email, pwd, rankID, age, troopID);
		
		this.addReqList(reqID);
		this.addMBList(mbID);
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
		super.deleteWhere(DatabaseNames.SCOUT_REQ_TABLE, "scoutid", id);
		super.deleteWhere(DatabaseNames.SCOUT_MB_TABLE, "scoutid", id);
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
		int rankID = super.queryInt(DatabaseNames.SCOUT_TABLE, "rankid", id);
		return super.queryString(DatabaseNames.RANK_TABLE, "name", rankID);
	}
	
	/**
	 * Updates scout's rank in database
	 * @param the scout's new rank
	 * @return true if successful, else false
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
	 * Get a list of partial requirements the scout has completed (Json String)
	 * @return
	 * @throws Sql2oException
	 * @throws NoRecordFoundException
	 */
	public List<RequirementObject> queryReq() throws Sql2oException, NoRecordFoundException {
		//Get reqIDs
		List <Integer> reqIDs = super.fetchIntsWhere(DatabaseNames.SCOUT_REQ_TABLE, "scoutid", id, "reqid");
		
		//Get req records (name and rank id) from database using reqIDs
		String sql = "SELECT name, rankid FROM " + DatabaseNames.REQ_TABLE + " WHERE id IN ";
		sql = super.addCollection(sql, reqIDs.size());
		Query q = sql2o.open().createQuery(sql);
		q = super.addParameters(q, reqIDs);
		List <RequirementRecord> records = q.executeAndFetch(RequirementRecord.class);
		
		//Get Rank Names
		List <Integer> rankIDs = new ArrayList <Integer>();
		for(RequirementRecord record : records) rankIDs.add(record.getRankID());
		List <String> rankNames = super.queryStrings(DatabaseNames.RANK_TABLE, "name", rankIDs);
		rankNames = regularize(rankNames, rankIDs);
		
		//Transform to Requirement objects
		List <RequirementObject> req = new ArrayList<RequirementObject>();
		for(int i = 0; i < records.size(); i++) req.add(new RequirementObject(records.get(i).getName(), rankNames.get(i)));
		
		return req;
		}
	
	/**
	 * Adds a partial requirement to scout in database
	 * @param reqName name of the partial requirement
	 * @throws Sql2oException thrown by database error
	 * @throws NoRecordFoundException thrown if name of requirement is not found
	 */
	public void addReq(String reqName, String rank) throws Sql2oException, NoRecordFoundException {
		int rankID = super.idOfRank(rank);
		int reqID = super.idOfRequirement(reqName, rankID);
		super.addJoinRecord(DatabaseNames.SCOUT_REQ_TABLE, "scoutid", "reqid", id, reqID);
	}
	
	/**
	 * Remove a partial requirement from scout in the database
	 * @param reqName name of the requirement
	 * @param rank name of the rank
	 * @throws Sql2oException thrown if database error
	 * @throws NoRecordFoundException thrown if no record of rank name or requirement name is found
	 */
	public void destroyReq(String reqName, String rank) throws Sql2oException, NoRecordFoundException {
		System.out.println(rank);
		int rankID = super.idOfRank(rank);
		int reqID = super.idOfRequirement(reqName, rankID);
		super.deleteWhere(DatabaseNames.SCOUT_REQ_TABLE, "reqid", reqID);
	}

	/**
	 * Get List of Merit Badges
	 * @return List of Merit Badge Names
	 * @throws Sql2oException
	 * @throws NoRecordFoundException
	 */
	public List <String> queryMb() throws Sql2oException, NoRecordFoundException {
		List <Integer> meritbadgeIDs = super.fetchIntsWhere(DatabaseNames.SCOUT_MB_TABLE, "scoutid", id, "meritbadgeid"); 
		return super.queryStrings(DatabaseNames.MB_TABLE, "name", meritbadgeIDs);
	}
	
	/**
	 * Add Merit Badge to scout in database
	 * @param meritbadgeName name of the meritbadge to add to the scout
	 * @throws NoRecordFoundException thrown if no meritbadge of that name is found
	 * @throws Sql2oException thrown by database error
	 */
	
	public void addMb(String meritbadgeName) throws Sql2oException, NoRecordFoundException {
		int meritbadgeID = super.idOfMeritbadge(meritbadgeName);
		super.addJoinRecord(DatabaseNames.SCOUT_MB_TABLE, "scoutid", "meritbadgeid", id, meritbadgeID);
	}
	
	/**
	 * Removes Merit Badge from database 
	 * @param meritbadgeName name of the meritbadge to remove
	 * @throws Sql2oException thrown if database error occurs
	 * @throws NoRecordFoundException thrown if name of merit badge is not found
	 */
	public void destroyMb(String meritbadgeName) throws Sql2oException, NoRecordFoundException {
		int meritbadgeID = super.idOfMeritbadge(meritbadgeName);
		super.deleteWhere(DatabaseNames.SCOUT_MB_TABLE, "meritbadgeid", meritbadgeID);
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
	 * adds requirement ids to scout - requirement join table
	 * @param reqID array of requirement ids to add
	 * @throws Sql2oException thrown on database error
	 * @throws InvalidDatabaseOperation thrown if invalid operation is performed on database
	 */
	private void addReqList(int[] reqID) throws Sql2oException, InvalidDatabaseOperation {
		int[] scoutID = new int[reqID.length];
		for(int i = 0; i < scoutID.length; i++) scoutID[i] = id;	//This is ugly, there must be a better way to do this.
		super.addJoinRecords(DatabaseNames.SCOUT_REQ_TABLE, "scoutid", "reqid", scoutID, reqID);
	}
	
	/**
	 * adds meritbadges to scout-meritbadge join table
	 * @param mbID array of meritbadge ids
	 * @throws Sql2oException thrown if database error
	 * @throws InvalidDatabaseOperation thrown if invalid operation is performed on database
	 */
	private void addMBList(int[] mbID) throws Sql2oException, InvalidDatabaseOperation {
		int[] scoutID = new int[mbID.length];
		for(int i = 0; i < scoutID.length; i++) scoutID[i] = id;	//This is ugly, there must be a better way to do this.
		super.addJoinRecords(DatabaseNames.SCOUT_MB_TABLE, "scoutid", "meritbadgeid", scoutID, mbID);
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
		String sql = "insert into "+ DatabaseNames.SCOUT_TABLE + "(name, email, pwd, salt, age, troopid, rankid) VALUES (:name, :email, :password, :salt, :age, :troopID, :rankID)";
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
	
	/**
	 * Regularlize the entries of a list by inserting copies so that its order matches another's
	 * @param values list to be regularized
	 * @param order order values should be in (including repetitions)
	 * @return the regularized list;
	 */
	private List<String> regularize(List <String> values, List <Integer> order) {
		int entry = 0;
		List <String> regularizedList = new ArrayList<String>();
		
		for(int i = 0; i < order.size() - 1; i++) {
			regularizedList.add(values.get(entry));
			if(order.get(i) != order.get(i+1)) entry++;
		}
		regularizedList.add(values.get(entry));
		
		return regularizedList;
	}
}
