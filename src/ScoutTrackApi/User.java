package ScoutTrackApi;
import org.sql2o.Sql2oException;

/**
 * Standard interface for user database objects
 * @author Charlie
 *
 */
public interface User {
	final int SALT_LENGTH = 32;

	void destroy() throws Sql2oException;
	
	String queryEmail() throws Sql2oException, NoRecordFoundException;
	void updateEmail(String email) throws Sql2oException;
	
	String queryTroop() throws Sql2oException, NoRecordFoundException;
	void updateTroop(String troop) throws Sql2oException, NoRecordFoundException;
	
	String querySalt() throws Sql2oException, NoRecordFoundException;
	
	void updatePwd(String pwd) throws Sql2oException;
}
