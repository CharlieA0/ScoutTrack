import java.util.List;

import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

public class Troop extends DatabaseSearcher implements DatabaseObject{
	protected static final String TABLE = "troops";
	protected static final String ID = "troop_id";
	protected static final String NAME = "troop_name";
	
	private int id;
	private Sql2o sql2o;
	
	/**
	 * Constructs new Troop but doesn't any data in database
	 * @param id id of troop
	 * @param sql2o Sql2o database object
	 * @throws Sql2oException thrown if database error
	 */
	public Troop(int id, Sql2o sql2o) throws Sql2oException {
		super(sql2o);
		this.id = id;
		this.sql2o = sql2o;
	}
	
	/**
	 * Constructs a new Troop and stores data in database
	 * @param sql2o Sql2o database object
	 * @param name name of troop
	 * @param scoutIDs array of scoutids
	 */
	public Troop(Sql2o sql2o, String name, int[] scoutIDs) {
		super(sql2o);
		this.sql2o = sql2o;
		this.id = addTroop(name);
	}
	
	/**
	 * Gets troop's id
	 * @return id of the troop
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Retrieves name of troop
	 * @return name of troop
	 * @throws NoRecordFoundException thrown if no troop with this id found
	 * @throws Sql2oException thrown if database error
	 */
	public String queryName() throws Sql2oException, NoRecordFoundException {
		return super.queryString(Troop.TABLE, Troop.NAME, id);
	}
	
	/**
	 * Updates name in database
	 * @param name new name for troop
	 * @throws Sql2oException thrown if database error
	 */
	public void updateName(String name) throws Sql2oException {
		super.updateString(Troop.TABLE, Troop.NAME, name, id);
	}
	
	/**
	 * Retrieves list of scout in troop from database
	 * @return list of scouts in troop
	 * @throws NoRecordFoundException thrown if no scouts with troopid of this troop found
	 * @throws Sql2oException thrown if database error
	 */
	public List <Integer> queryScouts() throws Sql2oException, NoRecordFoundException {
		return super.searchIds(Scout.TABLE, Troop.ID, id);
	}
	
	/**
	 * Retrieves list of leaders in troop from database
	 * @return list of leaders in troop
	 * @throws NoRecordFoundException thrown if no leaders with troopid of this troop found
	 * @throws Sql2oException  thrown if database error
	 */
	public List <Integer> queryLeaders() throws Sql2oException, NoRecordFoundException {
		return super.searchIds(Leader.TABLE, Troop.ID, id);
	}
	
	/**
	 * Adds troop data to database
	 * @param name name of the troop
	 * @return id of the troop (primary key in database)
	 * @throws thrown if database error
	 */
	private int addTroop(String name)throws Sql2oException {
		String sql = "INSERT INTO " + DatabaseNames.TROOP_TABLE + " (" + Troop.NAME + ") VALUES (:name);";
		try (Connection conn = sql2o.beginTransaction()) {
			int id = conn.createQuery(sql, true).addParameter("name", name).executeUpdate().getKey(Integer.class);
			conn.commit();
			return id;
		} catch (Sql2oException e) {
			System.out.println(e);
			throw e;
		}
	}
	
	/*
	 * Not Necessary?
	 * 
	 * private void addScouts (int[] scoutIDs) {
		String sql = "UPDATE " + DatabaseNames.SCOUT_TABLE + " SET troopid = :troopid WHERE id=:scoutid;";
		for(int scoutID : scoutIDs) {
			try (Connection conn = sql2o.beginTransaction()) {
				conn.createQuery(sql).addParameter("troopid", id).addParameter("scoutid", scoutID).executeUpdate();
				conn.commit();
			} catch (Sql2oException e) {
				System.out.println(e);
			}
		}
	}*/
	
}
