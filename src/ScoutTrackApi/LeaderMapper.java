package ScoutTrackApi;
import java.util.Arrays;
import java.util.List;

import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import com.google.gson.JsonObject;

/**
 * Class mapping Leader json data to Leader database object
 * @author Charlie Vorbach
 *
 */
public class LeaderMapper extends UserMapper {
	private final List <String> FIELDS;// = {"name", "email", "pwd", "troop"};
	private final String[] LEADER_FIELDS = {};
	
	private final JsonObject json;
	private final Sql2o sql2o;
	
	private boolean validated;
	
	private String name;
	private String email;
	private String pwd;
	private int troopID;

	/**
	 * Constructs LeaderMapper which can validate and map Json data to Leader objects
	 * @param json json data
	 * @param sql2o database object
	 */
	public LeaderMapper (JsonObject json, Sql2o sql2o) {
		this.json = json;
		this.sql2o = sql2o;
		this.validated = false;

		this.FIELDS = getFields();
	}
	
	/**
	 * Construct LeaderMapper without Json data
	 * @param sql2o database object
	 */
	public LeaderMapper (Sql2o sql2o) {
		this.json = null;
		this.sql2o = sql2o;
		this.validated = false;
		
		this.FIELDS = getFields();
	}
	
	/**
	 * Checks that Json data is valid and can be mapped
	 * @throws Sql2oException thrown if database error occurs
	 * @throws InvalidDataException thrown if json data is invalid
	 * @throws NoJsonToParseException thrown if json data was not passed
	 */
	public void validate() throws Sql2oException, InvalidDataException, NoJsonToParseException {
		if(json == null) throw new NoJsonToParseException();
		
		DatabaseSearcher lookup = new DatabaseSearcher(sql2o);
		
		//Check that json has all the necessary fields
		for (String field : FIELDS) {
			if (!json.has(field)) {
				throw new InvalidDataException();
			}
		}
				
		//Check that Strings are not null and less than the max size
		try {
			this.name = json.get("name").getAsString();
			validateName(name);
			
			this.email = json.get("email").getAsString();
			validateEmail(email);
			
			this.pwd = json.get("pwd").getAsString();
			validatePwd(pwd);
			
			//Check that troop is in the database
			this.troopID = lookup.idOfTroop(json.get("troop").getAsString());
			if (troopID < 0) throw new InvalidDataException();
		} catch (ClassCastException | NoRecordFoundException e) {
			throw new InvalidDataException();
		}
		
		this.validated = true;
	}
	
	/**
	 * Checks that sting is valid name
	 * @param name string to test
	 */
	public String validateName(String name) throws InvalidDataException {
		return super.validateName(name);
	}
	
	/**
	 * Checks string is valid email
	 * @param email string to test
	 * @throws InvalidDataException thrown if string is not valid hash
	 */
	public String validateEmail(String email) throws InvalidDataException {
		return super.validateEmail(email, sql2o);
	}
	
	/**
	 * Checks String is valid hash
	 * @param pwd string to test
	 * @throws InvalidDataException thrown if string is not valid hash
	 */
	public String validatePwd(String pwd) throws InvalidDataException {
		return super.validatePwd(pwd);
	}
	
	
	/**
	 * Constructs a Leader object and stores data in database
	 * @return the new Leader object
	 * @throws ScoutTrackNotValidatedException thrown if not validated or validation fails
	 */
	public Leader getLeader() throws ScoutTrackNotValidatedException {
		if(!validated) throw new ScoutTrackNotValidatedException("Leader");
		return new Leader(this.sql2o, this.name, this.email, this.pwd, this.troopID);
	}
	
	/**
	 * Gets necessary fields from super class and combines with those in leader class
	 * @return list of fields
	 */
	protected List <String> getFields() {
		List <String> fields = super.getFields();
		fields.addAll(Arrays.asList(LEADER_FIELDS));
		return fields;
	}
}
