import java.util.Arrays;
import java.util.List;

import org.sql2o.Sql2oException;

public abstract class UserMapper extends DatabaseObjectMapper {
	public final static int NAME_LENGTH = 70;
	public final static int EMAIL_LENGTH = 70;
	public final static int PWD_LENGTH = 64;
	private final String[] USER_FIELDS = {"email", "pwd", "troop"};
	
	public String validateName(String name) throws InvalidJsonDataException {
		if(!checkString(name, NAME_LENGTH)) throw new InvalidJsonDataException();
		return name;
	}
	
	public String validateEmail(String email) throws InvalidJsonDataException {
		if(!checkString(email, EMAIL_LENGTH)) throw new InvalidJsonDataException();
		return email;
	}
	
	public String validatePwd(String pwd) throws InvalidJsonDataException {
		if(pwd == null || pwd.length() != PWD_LENGTH) throw new InvalidJsonDataException();
		return pwd;
	}
	
	protected List <String> getFields() {
		List <String> fields = super.getFields();
		fields.addAll(Arrays.asList(USER_FIELDS));
		return fields;
	}
	
	protected  boolean checkString(String str, int length) {
		return super.checkString(str, length);
	}	

	public abstract void validate() throws Sql2oException, NoRecordFoundException, InvalidJsonDataException, NoJsonToParseException;
}
