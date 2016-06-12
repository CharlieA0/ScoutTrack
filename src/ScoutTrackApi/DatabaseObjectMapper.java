package ScoutTrackApi;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sql2o.Sql2oException;

/**
 * Class encapsulating mapping an object in database
 * @author Charlie Vorbach
 *
 */
public abstract class DatabaseObjectMapper {
	private String[] UNIVERSAL_FIELDS = {"name"};
	
	/**
	 * Gets fields neccessary for object
	 * @return
	 */
	protected List <String> getFields() {
		return new ArrayList<String> (Arrays.asList(UNIVERSAL_FIELDS));
	}
	
	/**
	 * Function checks key value is not null or longer than max size
	 * @param str String to check
	 * @param length max length of string
	 * @return true if valid, else false
	 */
	protected boolean checkString(String str, int length) {
		if (str == null || str.length() > length) {
			return false;
		}
		return true;
	}	
	
	/**
	 * Validates json object
	 * @throws Sql2oException thrown if database error occurs
	 * @throws NoRecordFoundException thrown if json data is inconsistent with that in database
	 * @throws InvalidDataException thrown if json data can not be read
	 * @throws NoJsonToParseException thrown if called without proper json data
	 */
	public abstract void validate() throws Sql2oException, NoRecordFoundException, InvalidDataException, NoJsonToParseException;
}
