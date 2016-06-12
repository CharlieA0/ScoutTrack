import java.util.Arrays;
import java.util.List;

import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

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
	 * @return email of user
	 * @throws InvalidDataException thrown if email fails to validate
	 */
	public abstract String validateEmail(String email) throws InvalidDataException;
	
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
	 */
	protected List <String> getFields() {
		List <String> fields = super.getFields();
		fields.addAll(Arrays.asList(USER_FIELDS));
		return fields;
	}
	
	/**
	 * Checks String is valid
	 */
	protected  boolean checkString(String str, int length) {
		return super.checkString(str, length);
	}	

	/**
	 * Confirms user data is valid
	 * @throws InvalidDatabaseOperation 
	 */
	public abstract void validate() throws Sql2oException, NoRecordFoundException, InvalidDataException, NoJsonToParseException;
}
