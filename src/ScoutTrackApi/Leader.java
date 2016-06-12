package ScoutTrackApi;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.security.SecureRandom;

/**
 * Class encapsulating Leader database records and operations on such
 * @author Charlie Vorbach
 *
 */
public class Leader extends DatabaseSearcher implements DatabaseObject, User{
	
	private final Sql2o sql2o;
	private final int id;
	
	/**
	 * Constructs new leader but does not store any information in the database
	 * @param id the leader's id
	 * @param sql2o the Sql2o database object
	 */
	public Leader(int id, Sql2o sql2o) {
		super(sql2o);
		this.sql2o = sql2o;
		this.id = id;
	}
	
	/**
	 * Constructs new leader and adds record to database
	 * @param sql2o the Sql2o database object
	 * @param name the leader's name
	 * @param email the leader's email
	 * @param pwd the leader's password
	 * @param troopID the leader's troop's primary key in database
	 */
	public Leader(Sql2o sql2o, String name, String email, String pwd, int troopID) {
		super(sql2o);
		this.sql2o = sql2o;
		this.id = addLeader(name, email, pwd, troopID);
	}
	
	/**
	 * Removes leader's records from database
	 */
	public void destroy() throws Sql2oException {
		super.deleteFrom(DatabaseNames.LEADER_TABLE, id);
	}
	
	/**
	 * Gets leader's id
	 * @return the leader's id
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Retrieves leader's name from database
	 * @return leader's name
	 * @throws NoRecordFoundException thrown if no record of leader is found
	 * @throws Sql2oException thrown if database error occurs
	 */
	public String queryName() throws Sql2oException, NoRecordFoundException {
		return super.queryString(DatabaseNames.LEADER_TABLE, "name", id);
	}
	
	/**
	 * Updates leader's name in the database
	 * @param name new name
	 */
	public void updateName(String name) throws Sql2oException {
		super.updateString(DatabaseNames.LEADER_TABLE, "name", name, id);
	}
	
	/**
	 * Retrieves leader's email from database
	 * @return the leader's email
	 * @throws NoRecordFoundException thrown if no record of leader is found
	 * @throws Sql2oException thrown if database error occurs
	 */
	public String queryEmail() throws Sql2oException, NoRecordFoundException {
		return super.queryString(DatabaseNames.LEADER_TABLE, "email", id);
	}
	
	/**
	 * Updates leader's email in the database
	 * @param email the new email
	 */
	public void updateEmail(String email) throws Sql2oException {
		super.updateString(DatabaseNames.LEADER_TABLE, "email", email, id);
	}
	
	/**
	 * Gets leader's troop name from database
	 * @return leader's troop's name
	 * @throws NoRecordFoundException thrown if no record of leader is found
	 * @throws Sql2oException thrown if database error occurs
	 */
	public String queryTroop() throws Sql2oException, NoRecordFoundException {
		int troopID = super.queryInt(DatabaseNames.LEADER_TABLE, "troopid", id);
		return super.queryString(DatabaseNames.TROOP_TABLE, "name", troopID);
	}

	/**
	 * Updates leader's troop in the database
	 * @param troop the leader's new troop's name
	 * @throws NoRecordFoundException thrown if no record of troop is found
	 * @throws Sql2oException thrown if database error occurs
	 */
	public void updateTroop(String troop) throws Sql2oException, NoRecordFoundException {
		int troopID = super.searchId(DatabaseNames.TROOP_TABLE, "name", troop);
		super.updateInt(DatabaseNames.LEADER_TABLE, "troopid", troopID, id);
	}	
	
	/**
	 * Retrieves leader's salt from database
	 * @return the leader's password salt
	 * @throws NoRecordFoundException thrown if no record of leader is found
	 * @throws Sql2oException thrown if database error occurs
	 */
	public String querySalt() throws Sql2oException, NoRecordFoundException {
		return super.queryString(DatabaseNames.LEADER_TABLE, "salt", id);
	}
	
	/**
	 * Updates leader's password hash in the database
	 * @param pwd the leader's password hash (not plaintext)
	 */
	public void updatePwd(String pwd) throws Sql2oException {
		super.updateString(DatabaseNames.LEADER_TABLE, "pwd", pwd, id);
	}
	
	/**
	 * Adds Leader to database
	 * @param name the leader's name
	 * @param email the leader's email
	 * @param pwd the leader's password hash (not plaintext)
	 * @param troopID the leader's troop's primary key
	 * @return id of leader
	 */
	private int addLeader(String name, String email, String pwd, int troopID) {
		byte[] salt = getSalt();
		return storeLeader(name, email, pwd, salt, troopID);
	}
	
	/**
	 * Gets random salt
	 * @return the random salt
	 */
	private byte[] getSalt() {
		SecureRandom gen = new SecureRandom();
		byte[] salt = new byte[SALT_LENGTH];
		gen.nextBytes(salt);
		return salt;
	}
	
	/**
	 * Stores Leader information in database
	 * @param name leader's name
	 * @param email leader's email
	 * @param pwd password hash (not plaintext)
	 * @param salt password salt
	 * @param troopID the leader's troop's primary key
	 * @return
	 */
	private int storeLeader(String name, String email, String pwd, byte[] salt, int troopID) {
		String sql = "INSERT INTO " + DatabaseNames.LEADER_TABLE + " (name, email, pwd, salt, troopid) VALUES (:name, :email, :pwd, :salt, :troopID);";
		try(Connection conn = sql2o.beginTransaction()) {
			int id = conn.createQuery(sql, true).addParameter("name", name).addParameter("email", email).addParameter("pwd", pwd).addParameter("salt", salt).addParameter("troopID", troopID)
				.executeUpdate().getKey(Integer.class);
			conn.commit();
			return id;
		} catch(Sql2oException e) {
			System.out.println(e);
			throw e;
		}
	}
}
