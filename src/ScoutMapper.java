import java.util.Arrays;
import java.util.List;

import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ScoutMapper extends UserMapper {
	private final int MAX_SCOUT_AGE = 18;
	private final int MIN_SCOUT_AGE = 10;
	private final int MAX_REQ_NUMBER = 250;
	private final int MAX_REQ_LENGTH = 5;
	private final int MAX_MB_NUMBER = 250;
	private final int MAX_MB_LENGTH = 70;
	
	private final List <String> FIELDS;
	private final String[] SCOUT_FIELDS = {"rank", "age", "req", "mb"};
	
	private JsonObject json;
	private Sql2o sql2o;
	
	private boolean validated;
	
	private String name;
	private String email;
	private String pwd;
	private int rankID;
	private int troopID;
	private int age;
	private String[] req;
	private String[] mb;
	
	/**
	 * Constructs ScoutMapper which can validate and map Json data to Scout objects
	 * @param json
	 * @param sql2o
	 */
	public ScoutMapper(JsonObject json, Sql2o sql2o) {
		this.json = json;
		this.sql2o = sql2o;
		this.validated = false;
		
		this.FIELDS = getFields();
	}
	
	public ScoutMapper(Sql2o sql2o) {
		this.json = null;
		this.sql2o = sql2o;
		this.validated = false;
		
		this.FIELDS = getFields();
	}
	
	/**
	 * Checks that Json data is valid and can be mapped
	 * @throws NoRecordFoundException 
	 * @throws Sql2oException 
	 * @throws InvalidJsonDataException 
	 * @throws NoJsonToParseException 
	 */
	public void validate() throws Sql2oException, NoRecordFoundException, InvalidJsonDataException, NoJsonToParseException {
		if(json == null) throw new NoJsonToParseException();
		
		//Check json has all the necessary fields
		for(String field : FIELDS){
			if(!json.has(field)) {
				throw new InvalidJsonDataException();
			}
		}
				
		DatabaseSearcher lookup = new DatabaseSearcher(sql2o);		
		try {
			//Check that strings are less than max size and not null
			this.name = json.get("name").getAsString();
			validateName(name);
							
			this.email = json.get("email").getAsString();
			validateEmail(email);
			
			this.pwd = json.get("pwd").getAsString();
			validatePwd(pwd);
	
			//Check that rank and troop are are present in database
			this.rankID = lookup.idOfRank(json.get("rank").getAsString());
			this.troopID = lookup.idOfTroop(json.get("troop").getAsString());
	
			//Check that age is a valid integer inside the scout age range
			this.age = json.get("age").getAsInt();
			validateAge(age);
			
			//Parse JsonArrays
			JsonArray jsonReq = json.get("req").getAsJsonArray();
			this.req = validateReq(jsonReq);
			
			JsonArray jsonMb = json.get("mb").getAsJsonArray();
			this.req = validateMb(jsonMb);
		}
		catch (ClassCastException | NoRecordFoundException e){
			throw new InvalidJsonDataException();
		}
		
		this.validated = true;
	}
	
	public String[] validateReq(JsonArray jsonReq) throws InvalidJsonDataException {
		if (jsonReq.size() > MAX_REQ_NUMBER) throw new InvalidJsonDataException();
		
		String[] req = new String[jsonReq.size()];
		for(int i = 0; i < req.length; i++) {		
			String requirement = jsonReq.get(i).getAsString();
			if (requirement == null || requirement.length() > MAX_REQ_LENGTH) throw new InvalidJsonDataException();
			req[i] = requirement;
		}
		return req;
	}
	
	public String[] validateMb(JsonArray jsonMb) throws InvalidJsonDataException {
		if(jsonMb.size() > MAX_MB_NUMBER) throw new InvalidJsonDataException();
		
		String[] mb = new String[jsonReq.size()];
		for(int i = 0; i < req.length; i++) {
			String meritbadge = json.get(i).getAsString();
			if (meritbadge == null || meritbadge.length() > MAX_MB_LENGTH) throw new InvalidJsonDataException();
			mb[i] = meritbadge;
		}
		return mb;
	}
	
	public String validateName(String name) throws InvalidJsonDataException {
		return super.validateName(name);
	}
	
	public String validateEmail(String email) throws InvalidJsonDataException {
		return super.validateEmail(email);
	}
	
	public String validatePwd(String pwd) throws InvalidJsonDataException {
		return super.validatePwd(pwd);
	}
	
	public int validateAge(int age) throws InvalidJsonDataException {
		if (age > MAX_SCOUT_AGE || age < MIN_SCOUT_AGE) throw new InvalidJsonDataException();
		return age;
	}
	
	/**
	 * Constructs new scout from json data and stores it in the database
	 * @return the new scout object
	 * @throws ScoutTrackNotValidatedException thrown if mapping was not validated or failed to validate.
	 */
	public Scout getScout() throws ScoutTrackNotValidatedException {
		if(!validated) throw new ScoutTrackNotValidatedException("Scout");
		return new Scout(this.sql2o, this.name, this.email, this.pwd, this.rankID, this.age, this.troopID, this.req, this.mb);
	}
	
	/**
	 * Gets fields from super class and combines with fields in this class
	 */
	protected List <String> getFields() {
		List <String> fields = super.getFields();
		fields.addAll(Arrays.asList(SCOUT_FIELDS));
		return fields;
	}
}
