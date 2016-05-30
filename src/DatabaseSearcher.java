import java.util.ArrayList;
import java.util.List;

import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

public class DatabaseSearcher {
	private Sql2o sql2o;
	
	/**
	 * Construct DatabaseSearcher
	 * @param sql2o Sql2o database object
	 */
	public DatabaseSearcher(Sql2o sql2o) {
		this.sql2o = sql2o;
	}
	
	/**
	 * Retrieves a String from a database record
	 * @param table database table
	 * @param column column in table
	 * @param id id of the record
	 * @return the string 
	 * @throws NoRecordFoundException thrown if the record is not found
	 * @throws Sql2oException thrown if there is a database error
	 */
	public String queryString(String table, String column, int id) throws NoRecordFoundException, Sql2oException {
		String sql = "SELECT " + column + " FROM " + table + " WHERE id = :id";
		try (Connection conn = sql2o.open()) {
			String response = conn.createQuery(sql).addParameter("id", id).executeAndFetchFirst(String.class);
			if (response == null) throw new NoRecordFoundException();
			return response;
		} catch (Sql2oException e) {
			System.out.println(e);
			throw e;
		}
	}
	
	/**
	 * Updates a string in a Database record
	 * @param table	database table
	 * @param column database column
	 * @param value string to update to
	 * @param id id of the record
	 * @throws Sql2oException thrown if there is a database error
	 */
	public void updateString(String table, String column, String value, int id) {
		String sql = "UPDATE " + table + " SET " + column + " = :value WHERE  id = :id";
		try (Connection conn = sql2o.beginTransaction()) {
			conn.createQuery(sql).addParameter("value", value).addParameter("id", id).executeUpdate();
			conn.commit();
		} catch (Sql2oException e) {
			System.out.println(e);
			throw e;
		}
	}
	
	/**
	 * Retrieves integer from database record
	 * @param table	database table
	 * @param column database column
	 * @param id id of the record;
	 * @return the integer
	 * @throws NoRecordFoundException thrown if record is not found
	 * @throws Sql2oException thrown if there is a database error
	 */
	public int queryInt(String table, String column, int id) throws NoRecordFoundException, Sql2oException {
		String sql = "SELECT " + column + " FROM " + table + " WHERE id = :id";
		try (Connection conn = sql2o.open()) {
			String response = conn.createQuery(sql).addParameter("id", id).executeAndFetchFirst(String.class);
			if (response == null) throw new NoRecordFoundException();
			return Integer.parseInt(response);
		} 
		catch (Sql2oException e) {
			System.out.println(e);
			throw e;
		}
	}
	
	/**
	 * Updates integer in database record
	 * @param table database table
	 * @param column database column
	 * @param value the integer to update the record with
	 * @param id the id of the record
	 * @throws Sql2oException thrown if there is database error
	 */
	public void updateInt(String table, String column, int value, int id) {
		String sql = "UPDATE " + table + " SET " + column + " = :value WHERE id = :id";
		try(Connection conn = sql2o.beginTransaction()) {
			conn.createQuery(sql).addParameter("value", value).addParameter("id", id).executeUpdate();
			conn.commit();
		} catch (Sql2oException e) {
			System.out.println(e);
			throw e;
		}
	}
	
	/**
	 * Retrieves id of record with column containing the specified string
	 * @param table database table
	 * @param column database column
	 * @param value database value
	 * @return the id of the record
	 * @throws NoRecordFoundException thrown if no record is found
	 * @throws Sql2oException thrown if there is a database error
	 */
	public int searchId(String table, String column, String value) throws NoRecordFoundException, Sql2oException {
		String sql = "SELECT id FROM " + table + " WHERE " + column + " = :value";
		try(Connection conn = sql2o.open()) {
			String response = conn.createQuery(sql).addParameter("value", value).executeAndFetchFirst(String.class);
			if (response == null) throw new NoRecordFoundException();
			return Integer.parseInt(response);
		} catch (Sql2oException e) {
			System.out.println(e);
			throw e;
		}
	}
	
	/**
	 * Retrieve ids of all records in table where column equals integer value
	 * @param table database table
	 * @param column database column
	 * @param value value of column to search
	 * @return List of ids
	 * @throws NoRecordFoundException thrown if no records with columns of that value are found
	 * @throws Sql2oException thrown by database error
	 */
	public List<Integer> searchIds(String table, String column, int value) throws NoRecordFoundException, Sql2oException {
		String sql = "SELECT id FROM " + table + " WHERE " + column + " = :value";
		try(Connection conn = sql2o.open()) {
			List <String> response = conn.createQuery(sql).addParameter("value", value).executeAndFetch(String.class);
			if(response == null) throw new NoRecordFoundException();
			List <Integer> ids = new ArrayList<Integer>();
			for(String id : response) ids.add(Integer.parseInt(id));
			return ids;
		} catch (Sql2oException e) {
			System.out.println(e);
			throw e;
		}
	}
	
	/**
	 * Retrieve ids of all records in table where column equals string value
	 * @param table database table
	 * @param column database column
	 * @param value value of column to search
	 * @return List of ids
	 * @throws NoRecordFoundException thrown if no records with columns of that value are found
	 * @throws Sql2oException thrown by database error
	 */
	public List<String> searchIds(String table, String column, String value) throws NoRecordFoundException, Sql2oException {
		String sql = "SELECT id FROM " + table + " WHERE " + column + " = :value";
		try(Connection conn = sql2o.open()) {
			List <String> response = conn.createQuery(sql).addParameter("value", value).executeAndFetch(String.class);
			if(response == null) throw new NoRecordFoundException();
			return response;
		} catch (Sql2oException e) {
			System.out.println(e);
			throw e;
		}
	} 
	
	/**
	 * Removes record from database
	 * @param table database table
	 * @param id the id of the record
	 * @throws Sql2oException thrown if there is a database error
	 */
	public void deleteFrom(String table, int id) {
		String sql = "DELETE FROM " + table + " WHERE id = :id";
		try (Connection conn = sql2o.beginTransaction()) {
			conn.createQuery(sql).addParameter("id", id).executeUpdate();
			conn.commit();
		} catch(Sql2oException e) {
			System.out.println(e);
			throw e;
		}
	}
	
	/**
	 * Retrieves id of record with rank name
	 * @param rankName name of rank
	 * @return id of record
	 * @throws Sql2oException thrown if there is a database error
	 * @throws NoRecordFoundException thrown if no record has rankName
	 */
	public int idOfRank(String rankName) throws Sql2oException, NoRecordFoundException {
		return searchId(Rank.TABLE, "name", rankName);
	}
	
	/**
	 * Retrieves id of record with troop name
	 * @param troopName the name of the troop
	 * @return id of record
	 * @throws Sql2oException thrown if there is database error
	 * @throws NoRecordFoundException thrown if no record with troopName is found
	 */
	public int idOfTroop(String troopName) throws Sql2oException, NoRecordFoundException {
		return searchId(Troop.TABLE, "name", troopName);
	}
}
