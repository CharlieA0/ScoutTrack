import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.security.SecureRandom;

public class Leader extends DatabaseSearcher implements DatabaseObject, User{
	protected static final String TABLE = "leaders";
	protected static final String ID = "leader_id";
	protected static final String NAME = "leader_name";
	protected static final String EMAIL = "leader_email";
	protected static final String PWD = "leader_pwd";
	protected static final String SALT = "leader_salt";
	
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
	 * @return true if successful, else false
	 */
	public void destroy() throws Sql2oException {
		super.deleteFrom(Leader.TABLE, id);
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
	 * @throws NoRecordFoundException 
	 * @throws Sql2oException 
	 */
	public String queryName() throws Sql2oException, NoRecordFoundException {
		return super.queryString(Leader.TABLE, Leader.NAME, id);
	}
	
	/**
	 * Updates leader's name in the database
	 * @param name new name
	 * @return true if successful, else false
	 */
	public void updateName(String name) throws Sql2oException {
		super.updateString(Leader.TABLE, Leader.NAME, name, id);
	}
	
	/**
	 * Retrieves leader's email from database
	 * @return the leader's email
	 * @throws NoRecordFoundException 
	 * @throws Sql2oException 
	 */
	public String queryEmail() throws Sql2oException, NoRecordFoundException {
		return super.queryString(Leader.TABLE, Leader.EMAIL, id);
	}
	
	/**
	 * Updates leader's email in the database
	 * @param email the new email
	 * @return true if successful, else false
	 */
	public void updateEmail(String email) throws Sql2oException {
		super.updateString(Leader.TABLE, Leader.EMAIL, email, id);
	}
	
	/**
	 * Gets leader's troop name from database
	 * @return leader's troop's name
	 * @throws NoRecordFoundException 
	 * @throws Sql2oException 
	 */
	public String queryTroop() throws Sql2oException, NoRecordFoundException {
		int troopID = super.queryInt(Leader.TABLE, Troop.ID, id);
		return super.queryString(Troop.TABLE, Troop.NAME, troopID);
	}

	/**
	 * Updates leader's troop in the database
	 * @param troop the leader's new troop's name
	 * @return true if successful, else false
	 * @throws NoRecordFoundException 
	 * @throws Sql2oException 
	 */
	public void updateTroop(String troop) throws Sql2oException, NoRecordFoundException {
		int troopID = super.searchId(Troop.TABLE, Troop.NAME, troop);
		super.updateInt(Leader.TABLE, Troop.ID, troopID, id);
	}	
	
	/**
	 * Retrieves leader's salt from database
	 * @return the leader's password salt
	 * @throws NoRecordFoundException 
	 * @throws Sql2oException 
	 */
	public String querySalt() throws Sql2oException, NoRecordFoundException {
		return super.queryString(Leader.TABLE, Leader.SALT, id);
	}
	
	/**
	 * Updates leader's password hash in the database
	 * @param pwd the leader's password hash (not plaintext)
	 * @return true if successful, else false
	 */
	public void updatePwd(String pwd) throws Sql2oException {
		super.updateString(Leader.TABLE, Leader.PWD, pwd, id);
	}
	
	/**
	 * Adds Leader to database
	 * @param name the leader's name
	 * @param email the leader's email
	 * @param pwd the leader's password hash (not plaintext)
	 * @param troopID the leader's troop's primary key
	 * @return
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
		String sql = "INSERT INTO " + Leader.TABLE + " (" + Leader.NAME + ", " + Leader.EMAIL + ", " + Leader.PWD + ", " + Leader.SALT + ", " + Troop.ID + ") VALUES (:name, :email, :pwd, :salt, :troopID);";
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
