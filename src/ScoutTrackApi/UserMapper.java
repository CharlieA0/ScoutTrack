package ScoutTrackApi;
import java.util.Arrays;
import java.util.List;

import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

/**
 * Class mapping user json data to user database object
 * @author Charlie
 *
 */
public abstract class UserMapper extends DatabaseObjectMapper {
	public final static int NAME_LENGTH = 50;
	public final static int EMAIL_LENGTH = 50;
	public final static int PWD_LENGTH = 64;
	private final String[] USER_FIELDS = {"email", "pwd", "troop"};
	
	/**
	 * Validates User's name
	 * @param name name of the user
	 * @return the name of the user
	 * @throws InvalidDataException thrown if name fails to validate
	 */
	public String validateName(String name) throws InvalidDataException {
		if(!checkString(name, NAME_LENGTH)) throw new InvalidDataException();
		return name;
	}
	
	/**
	 * Validates User's email (Note, emails must be unique)
	 * @param email email of the user
	 * @param sql2o database object
	 * @return email of user
	 * @throws InvalidDataException thrown if email fails to validate
	 * @throws DuplicateRecordException thrown if email is already in the database
	 */
	public String validateEmail(String email, Sql2o sql2o) throws InvalidDataException, DuplicateRecordException {
		if(!checkString(email, EMAIL_LENGTH)) throw new InvalidDataException();
		DatabaseSearcher lookup = new DatabaseSearcher(sql2o);
		if(lookup.checkPresent(DatabaseNames.LEADER_TABLE, "email", email)) throw new DuplicateRecordException("email");
		if(lookup.checkPresent(DatabaseNames.SCOUT_TABLE, "email", email))throw new DuplicateRecordException("email");
		return email;
	}
	
	/**
	 * Validates User's email string (Note, this doesn't check for duplicate emails in database)
	 * @param email email of the user
	 * @param sql2o database object
	 * @return email of user
	 * @throws InvalidDataException thrown if email fails to validate
	 */
	public String validateEmailString(String email, Sql2o sql2o) throws InvalidDataException {
		if(!checkString(email, EMAIL_LENGTH)) throw new InvalidDataException();
		return email;
	}
	
	/**
	 * Validates User's password hash
	 * @param pwd the user's password hash
	 * @return pwd hash of user
	 * @throws InvalidDataException thrown if pwd hash fails to validate
	 */
	public String validatePwd(String pwd) throws InvalidDataException {
		if(pwd == null || pwd.length() != PWD_LENGTH) throw new InvalidDataException();
		return pwd;
	}
	
	/**
	 * Gets Json field from superclass and adds user fields
	 * @return list of fields
	 */
	protected List <String> getFields() {
		List <String> fields = super.getFields();
		fields.addAll(Arrays.asList(USER_FIELDS));
		return fields;
	}
	
	/**
	 * Checks String is valid
	 * @param str str to test
	 * @param length max length of the string
	 */
	protected  boolean checkString(String str, int length) {
		return super.checkString(str, length);
	}	

	/**
	 * Confirms user data is valid
	 */
	public abstract void validate() throws Sql2oException, NoRecordFoundException, InvalidDataException, NoJsonToParseException, DuplicateRecordException;
}
