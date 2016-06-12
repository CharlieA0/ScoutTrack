import java.util.Arrays;
import java.util.List;

import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import com.google.gson.JsonObject;

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
	 * @param json
	 * @param sql2o
	 */
	public LeaderMapper (JsonObject json, Sql2o sql2o) {
		this.json = json;
		this.sql2o = sql2o;
		this.validated = false;

		this.FIELDS = getFields();
	}
	
	public LeaderMapper (Sql2o sql2o) {
		this.json = null;
		this.sql2o = sql2o;
		this.validated = false;
		
		this.FIELDS = getFields();
	}
	
	/**
	 * Checks that Json data is valid and can be mapped
	 * @throws NoRecordFoundException 
	 * @throws Sql2oException 
	 * @throws InvalidDataException 
	 * @throws NoJsonToParseException 
	 */
	public void validate() throws Sql2oException, NoRecordFoundException, InvalidDataException, NoJsonToParseException {
		if(json == null) throw new NoJsonToParseException();
		
		DatabaseSearcher lookup = new DatabaseSearcher(sql2o);
		
		//Check that json has all the necessary fields
		for (String field : FIELDS) {
			if (!json.has(field)) {
				throw new InvalidDataException();
			}
		}
				
		//Check that Strings are not null and less than the max size
		this.name = json.get("name").getAsString();
		validateName(name);
		
		this.email = json.get("email").getAsString();
		validateEmail(email);
		
		this.pwd = json.get("pwd").getAsString();
		validatePwd(pwd);
		
		//Check that troop is in the database
		this.troopID = lookup.idOfTroop(json.get("troop").getAsString());
		if (troopID < 0) throw new InvalidDataException();
		
		this.validated = true;
	}
	
	public String validateName(String name) throws InvalidDataException {
		return super.validateName(name);
	}
	
	public String validateEmail(String email) throws InvalidDataException {
		if(!checkString(email, EMAIL_LENGTH)) throw new InvalidDataException();
		if(new DatabaseSearcher(sql2o).checkPresent(DatabaseNames.LEADER_TABLE, "email", email)) throw new InvalidDataException();
		return email;
	}
	
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
	 */
	protected List <String> getFields() {
		List <String> fields = super.getFields();
		fields.addAll(Arrays.asList(LEADER_FIELDS));
		return fields;
	}
}
