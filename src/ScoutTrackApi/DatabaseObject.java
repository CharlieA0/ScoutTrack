package ScoutTrackApi;
import org.sql2o.Sql2oException;
/**
 * Class encapsulating a database object
 * @author Charlie Vorbach
 *
 */
public interface DatabaseObject {
	/**
	 * Retrieves name of object from database
	 * @return name name of object
	 * @throws Sql2oException thrown by database error
	 * @throws NoRecordFoundException thrown if no record of object is found
	 */
	String queryName() throws Sql2oException, NoRecordFoundException;
	
	/**
	 * Updates name of object in database
	 * @param name name of object
	 * @throws Sql2oException thrown if database error occurs
	 */
	void updateName(String name) throws Sql2oException;
}
