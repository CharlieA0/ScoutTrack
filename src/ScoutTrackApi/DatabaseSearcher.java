import java.util.ArrayList;
import java.util.List;

import org.sql2o.Connection;
import org.sql2o.Query;
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
	 * Retrieves Strings of all ids
	 * @param table database table
	 * @param column column to retrieve
	 * @param ids ids of records to return
	 * @return List of strings from column in records where ids match passed list
	 * @throws NoRecordFoundException
	 */
	public List <String> queryStrings(String table, String column, List <Integer> ids) throws NoRecordFoundException {
		//Prepare sql
		String sql = "SELECT " + column + " FROM " + table + " WHERE id IN ";
		sql = addCollection(sql, ids.size());
			
		//Add parameters and execute
		Query q = sql2o.open().createQuery(sql);
		q = addParameters(q, ids);
		List <String> response = q.executeAndFetch(String.class);
		
		if (response == null) throw new NoRecordFoundException();
		return response;
	}
	
	/**
	 * Retrieves list of strings from database where a column equals value
	 * @param table database table
	 * @param checkColumn column to compare to value
	 * @param hasValue value column must have to retrieve
	 * @param retrieveColumn the column to retrieve when checkColumn has hasValue
	 * @return List of strings from retrieveColumn where checkColumn has hasValue
	 * @throws Sql2oException thrown if database error
	 * @throws NoRecordFoundException thrown if no matching records are found
	 */
	public List<String> fetchStringsWhere(String table, String checkColumn, String hasValue, String retrieveColumn) throws Sql2oException, NoRecordFoundException {
		String sql = "SELECT " + retrieveColumn +" FROM " + table + " WHERE " + checkColumn + " = :value";
		Connection conn = sql2o.open();
		List <String> response = conn.createQuery(sql).addParameter("value", hasValue).executeAndFetch(String.class);
		if(response == null) throw new NoRecordFoundException();
		return response;
	}
	
	public List<String> fetchStringsWhere(String table, String checkColumn, int hasValue, String retrieveColumn) throws Sql2oException, NoRecordFoundException {
		String sql = "SELECT " + retrieveColumn +" FROM " + table + " WHERE " + checkColumn + " = :value";
		Connection conn = sql2o.open();
		List <String> response = conn.createQuery(sql).addParameter("value", hasValue).executeAndFetch(String.class);
		if(response == null) throw new NoRecordFoundException();
		return response;
	}
	
	/**
	 * Retrieves list of ints from database where a column equals value
	 * @param table database table
	 * @param checkColumn column to compare to value
	 * @param hasValue value column must have to retrieve
	 * @param retrieveColumn the column to retrieve when checkColumn has hasValue
	 * @return List of strings from retrieveColumn where checkColumn has hasValue
	 * @throws Sql2oException thrown if database error
	 * @throws NoRecordFoundException thrown if no matching records are found
	 */
	public List<Integer> fetchIntsWhere(String table, String checkColumn, String hasValue, String retrieveColumn) throws Sql2oException, NoRecordFoundException {
		String sql = "SELECT " + retrieveColumn +" FROM " + table + " WHERE " + checkColumn + " = :value";
		Connection conn = sql2o.open();
		List <String> response = conn.createQuery(sql).addParameter("value", hasValue).executeAndFetch(String.class);
		
		if(response == null) throw new NoRecordFoundException();
		
		List <Integer> ints = new ArrayList<Integer>();
		for(String str : response) ints.add(Integer.parseInt(str));
		return ints;
	}
	
	public List<Integer> fetchIntsWhere(String table, String checkColumn, int hasValue, String retrieveColumn) throws Sql2oException, NoRecordFoundException {
		String sql = "SELECT " + retrieveColumn +" FROM " + table + " WHERE " + checkColumn + " = :value";
		Connection conn = sql2o.open();
		List <String> response = conn.createQuery(sql).addParameter("value", hasValue).executeAndFetch(String.class);
		
		if(response == null) throw new NoRecordFoundException();
		
		List <Integer> ints = new ArrayList<Integer>();
		for(String str : response) ints.add(Integer.parseInt(str));
		return ints;
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
	 * Adds records to join table
	 * @param table the table to add the records to
	 * @param firstColumn the name of the first column of the join table
	 * @param secondColumn the name of the second column of the join table
	 * @param firstID array of ids to be inserted into the first column
	 * @param secondID array of ids to be inserted into the second column
	 * @throws InvalidDatabaseOperation thrown if lengths of id arrays are not the same
	 * @throws Sql2oException thrown if database error
	 */
	public void addJoinRecords(String table, String firstColumn, String secondColumn, int[] firstID, int[] secondID) throws Sql2oException, InvalidDatabaseOperation {
		if (firstID.length != secondID.length) throw new InvalidDatabaseOperation("add join table records");
		
		//Construct query with fence post correction
		String sql = "INSERT INTO " + table + " (" + firstColumn + ", " + secondColumn + ") VALUES ";
		for(int i = 0; i < firstID.length - 1; i++) {
			sql += "(:firstID" + i + ", :secondID" + i + "), ";
		}
		sql += "(:firstID" + (firstID.length - 1) + ", :secondID" + (secondID.length - 1) + ")";
		
		//make query
		try (Connection conn = sql2o.beginTransaction()){
			Query query = conn.createQuery(sql);
			for(int i = 0; i < firstID.length; i++){
				query.addParameter("firstID" + i, firstID[i]).addParameter("secondID" + i, secondID[i]);
			}
			query.executeUpdate();
			conn.commit();
		} catch (Sql2oException e) {
			System.out.println(e);
			throw e;
		}			
	}
	
	/**
	 * Adds record to joint table
	 * @param table database table's name
	 * @param firstColumn first column's name
	 * @param secondColumn second column's name
	 * @param firstID id for first column
	 * @param secondID id for second column
	 * @throws Sql2oException thrown if database error occurs
	 */
	public void addJoinRecord(String table, String firstColumn, String secondColumn, int firstID, int secondID) throws Sql2oException {
		String sql = "INSERT INTO " + table + " (" + firstColumn + ", " + secondColumn + ") VALUES (:firstID, :secondID)";
		Connection conn = sql2o.beginTransaction();
		conn.createQuery(sql).addParameter("firstID", firstID).addParameter("secondID", secondID).executeUpdate();
		conn.commit();
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

	public int searchId(String table, String column, int value) throws NoRecordFoundException, Sql2oException {
		String sql = "SELECT id FROM " + table + " WHERE " + column + " = :value";
		Connection conn = sql2o.open();
		String response = conn.createQuery(sql).addParameter("value", value).executeAndFetchFirst(String.class);
		if (response == null) throw new NoRecordFoundException();
		return Integer.parseInt(response);
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
	 * Checks if the value is present in passed column in passed table
	 * @param table name of table to search
	 * @param column name of column to search
	 * @param value value to search for
	 * @return true if the value is present, else false;
	 * @throws Sql2oException thrown by database error
	 */
	public boolean checkPresent(String table, String column, String value) throws Sql2oException {
		try {
			searchId(table, column, value);
		} catch (NoRecordFoundException e) {
			return false;
		}
		return true;
	}
	
	public boolean checkPresent(String table, String column, int value) throws Sql2oException {
		try {
			searchId(table, column, value);
		} catch (NoRecordFoundException e) {
			return false;
		}
		return true;
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
	 * Deletes records from database where column equals value
	 * @param table database table
	 * @param column column in the table
	 * @param value value of the column where the records should be deleted
	 */
	public void deleteWhere(String table, String column, int value) {
		String sql = "DELETE FROM " + table + " WHERE " + column + " = :value";
		try (Connection conn = sql2o.beginTransaction()) {
			conn.createQuery(sql).addParameter("value", value).executeUpdate();
			conn.commit();
		} catch (Sql2oException e) {
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
		return searchId(DatabaseNames.RANK_TABLE, "name", rankName);
	}
	
	/**
	 * Retrieves id of record with troop name
	 * @param troopName the name of the troop
	 * @return id of record
	 * @throws Sql2oException thrown if there is database error
	 * @throws NoRecordFoundException thrown if no record with troopName is found
	 */
	public int idOfTroop(String troopName) throws Sql2oException, NoRecordFoundException {
		return searchId(DatabaseNames.TROOP_TABLE, "name", troopName);
	}
	
	/**
	 * Retrieves id of requirement with passed name
	 * @param reqName the name of the requirement 
	 * @return the id of the requirement
	 * @throws Sql2oException thrown by database error
	 * @throws NoRecordFoundException thrown if no record is found
	 */
	public int idOfRequirement(String reqName, int rankid) throws Sql2oException, NoRecordFoundException {
		String sql = "SELECT id FROM " + DatabaseNames.REQ_TABLE + " WHERE name = :name AND rankid = :rankid";
		try (Connection conn = sql2o.open()) {
			String response = conn.createQuery(sql).addParameter("name", reqName).addParameter("rankid", rankid).executeAndFetchFirst(String.class);
			if(response == null) throw new NoRecordFoundException();
			return Integer.parseInt(response);
		} catch(Sql2oException e) {
			System.out.println(e);
			throw e;
		}
	}
	
	/**
	 * Retrieves id of merit badge record with passed name
	 * @param mbName name of the merit badge
	 * @return id of the merit badge
	 * @throws Sql2oException thrown by database error
	 * @throws NoRecordFoundException thrown if no record is found
	 */
	public int idOfMeritbadge(String mbName) throws Sql2oException, NoRecordFoundException {
		return searchId(DatabaseNames.MB_TABLE, "name", mbName);
	}
		
	/**
	 * Adds a collection to an sql statement (Meant to be used with addParameters method)
	 * @param sql sql statement
	 * @param size size of the collection
	 * @return sql statement with added collection
	 */
	public String addCollection(String sql, int size) {
		sql += "(";
		for (int i = 0; i < size - 1; i++) {
			sql += ":value" + i + ", ";
		}
		sql += ":value" + (size - 1) + ")";
		return sql;
	}
	
	/**
	 * Adds list of parameters to sql query
	 * @param q query to add parameters to
	 * @param param parameters to add
	 * @return query with passed parameters
	 */
	public Query addParameters(Query q, List<Integer> param) {
		for(int i = 0; i < param.size(); i++) q.addParameter("value" + i, param.get(i));
		return q;
	}
}