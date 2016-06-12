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
	private int[] reqID;
	private int[] mbID;
	
	/**
	 * Constructs ScoutMapper which can validate and map Json data to Scout objects
	 * @param json Json to be mapped to scout
	 * @param sql2o Sql2o database object
	 */
	public ScoutMapper(JsonObject json, Sql2o sql2o) {
		this.json = json;
		this.sql2o = sql2o;
		this.validated = false;
		
		this.FIELDS = getFields();
	}
	
	/**
	 * Constructs new ScoutMapper without any data. Can be used to validate parts of Scout object
	 * @param sql2o the Sql2o database object
	 */
	public ScoutMapper(Sql2o sql2o) {
		this.json = null;
		this.sql2o = sql2o;
		this.validated = false;
		
		this.FIELDS = getFields();
	}
	
	/**
	 * Checks that Json data is valid and can be mapped
	 * @throws NoRecordFoundException thrown if json contains info (like ranks or troops) not in the database
	 * @throws Sql2oException thrown by database error
	 * @throws InvalidDataException thrown if json is malformed
	 * @throws NoJsonToParseException thrown if object constructed without data
	 * @throws InvalidDatabaseOperation thrown if duplicate email already in the database
	 */
	public void validate() throws Sql2oException, NoRecordFoundException, InvalidDataException, NoJsonToParseException {
		if(json == null) throw new NoJsonToParseException();
		
		//Check json has all the necessary fields
		for(String field : FIELDS){
			if(!json.has(field)) {
				throw new InvalidDataException();
			}
		}
				
		DatabaseSearcher lookup = new DatabaseSearcher(sql2o);		
		try {
			//Check that strings are less than max size and not null
			this.name = json.get("name").getAsString();
			validateName(name);
							
			this.email = json.get("email").getAsString();
			validateEmail(email);
			if(!checkUniqueEmail(email)) throw new InvalidDataException("Email already in the database");
			
			this.pwd = json.get("pwd").getAsString();
			validatePwd(pwd);
	
			//Check that rank and troop are are present in database
			this.rankID = lookup.idOfRank(json.get("rank").getAsString());
			this.troopID = lookup.idOfTroop(json.get("troop").getAsString());
	
			//Check that age is a valid integer inside the scout age range
			this.age = json.get("age").getAsInt();
			validateAge(age);
			
			//Parse JsonArrays
			JsonArray jsonReqArray = json.get("req").getAsJsonArray();
			validateRequirements(lookup, jsonReqArray);
			
			JsonArray jsonMb = json.get("mb").getAsJsonArray();
			validateMeritbadges(jsonMb);
		}
		catch (ClassCastException | NoRecordFoundException e){
			throw new InvalidDataException();
		}
		
		this.validated = true;
	}
	
	/**
	 * Validates that JsonArray contains valid requirements and return array of ids from database (This currently an ugly kludge, there must be a better way of doing this)
	 * @param jsonReq
	 * @return
	 * @throws InvalidDataException
	 * @throws Sql2oException
	 * @throws NoRecordFoundException
	 */
	public int[] validateRequirements(DatabaseSearcher lookup, JsonArray jsonReqArray) throws InvalidDataException, Sql2oException, NoRecordFoundException {
		//Check number of partial requirements is valid
		if (jsonReqArray.size() > MAX_REQ_NUMBER) throw new InvalidDataException();		//Check number of partial requirements is valid
		reqID = new int[jsonReqArray.size()];												//Construct array to hold requirement ids
		for (int i = 0; i < reqID.length; i++) {											//For each requirement in the array
			JsonObject jsonReq = jsonReqArray.get(i).getAsJsonObject();						//Get requirement json object
			String reqName = jsonReq.get("name").getAsString();								//Get requirement name
			int reqRankID = lookup.idOfRank(jsonReq.get("rank").getAsString());				//Get requirement rank (Separate lookup)
			if (reqRankID <= this.rankID) throw new InvalidDataException();				//if requirement is for rank lower than the scout's current rank, throw an exception
			reqID[i] = validateRequirement(lookup, reqName, reqRankID);						//validate requirement and add it to the array
		}
		return reqID; 
	}
	
	/**
	 * Check that a string is a valid requirement in the database and returns id of record
	 * @param req the name of the requirement
	 * @return id of the requirement
	 * @throws InvalidDataException thrown if string is not a valid requirement name
	 * @throws Sql2oException thrown by database error
	 * @throws NoRecordFoundException thrown if no requirement with that name is found in database
	 */
	private int validateRequirement(DatabaseSearcher lookup, String req, int reqRankID) throws InvalidDataException, Sql2oException, NoRecordFoundException { 
		if( req == null || req.length() > MAX_REQ_LENGTH) throw new InvalidDataException();
		return lookup.idOfRequirement(req, reqRankID);
	}
	
	/**
	 * Check that requirement is valid and in database 
	 * @param lookup database searcher object
	 * @param scoutID id of the scout
	 * @param reqName name of requirement
	 * @param reqRankName name of requirement's rank
	 * @return id of requirement in database
	 * @throws InvalidDataException thrown if requirement is not valid
	 * @throws Sql2oException thrown if database error
	 * @throws NoRecordFoundException thrown if requirement not found in database.
	 */
	public int validateNewRequirement(DatabaseSearcher lookup, int scoutID, String reqName, String reqRankName) throws InvalidDataException, Sql2oException, NoRecordFoundException {
		int scoutRankID = lookup.queryInt(DatabaseNames.SCOUT_TABLE, "rankid", scoutID);
		int reqRankID = lookup.idOfRank(reqRankName);
		if (scoutRankID >= reqRankID) throw new InvalidDataException();
		int reqID = lookup.idOfRequirement(reqName, rankID);		
		return reqID;
	}
	
	/**
	 * Validates a JsonArray of meritbadge names and returns any array of their ids
	 * @param jsonMB JsonArray of meritbadge names
	 * @return array of meritbadge ids
	 * @throws InvalidDataException thrown if json fails to validate
	 * @throws NoRecordFoundException  thrown if meritbadge name not found in database
	 * @throws Sql2oException thrown if database error
	 */
	public int[] validateMeritbadges(JsonArray jsonMB) throws InvalidDataException, Sql2oException, NoRecordFoundException {
		if(jsonMB.size() > MAX_MB_NUMBER) throw new InvalidDataException();
		
		mbID = new int[jsonMB.size()];
		for(int i = 0; i < mbID.length; i++) {
			mbID[i] = validateMeritbadge(jsonMB.get(i).getAsString());
		}
		return mbID;
	}
	
	/**
	 * Verifies meritbadge name and returns id of meritbadge record
	 * @param mb meritbadge name
	 * @return id of meritbadge
	 * @throws InvalidDataException thrown if meritbadge name fails validation
	 * @throws Sql2oException thrown if database error
	 * @throws NoRecordFoundException thrown if name not found in database
	 */
	public int validateMeritbadge(String mb) throws InvalidDataException, Sql2oException, NoRecordFoundException {
		if(mb == null || mb.length() > MAX_MB_LENGTH) throw new InvalidDataException();
		DatabaseSearcher lookup = new DatabaseSearcher(sql2o);
		return lookup.idOfMeritbadge(mb);
	}
	
	/**
	 * Validates Scout Name
	 */
	public String validateName(String name) throws InvalidDataException {
		return super.validateName(name);
	}
	
	/**
	 * Validates Scout Email
	 */
	public String validateEmail(String email) throws InvalidDataException {
		System.out.println(email);
		if(!checkString(email, EMAIL_LENGTH)) throw new InvalidDataException();
		return email;
	}
	
	private boolean checkUniqueEmail(String email) {
		if(new DatabaseSearcher(sql2o).checkPresent(DatabaseNames.SCOUT_TABLE, "email", email)) return false;
		return true;
	}
	
	/**
	 * Validates Scout Password Hash
	 */
	public String validatePwd(String pwd) throws InvalidDataException {
		return super.validatePwd(pwd);
	}
	
	/**
	 * Validates Scout Age
	 * @param age age of scout
	 * @return the age
	 * @throws InvalidDataException thrown if age fails to validate
	 */
	public int validateAge(int age) throws InvalidDataException {
		if (age > MAX_SCOUT_AGE || age < MIN_SCOUT_AGE) throw new InvalidDataException();
		return age;
	}
	
	/**
	 * Constructs new scout from json data and stores it in the database
	 * @return the new scout object
	 * @throws ScoutTrackNotValidatedException thrown if mapping was not validated or failed to validate.
	 * @throws InvalidDatabaseOperation thrown if invalid operation is performed on database
	 * @throws Sql2oException thrown by database error
	 */
	public Scout getScout() throws ScoutTrackNotValidatedException, Sql2oException, InvalidDatabaseOperation {
		if(!validated) throw new ScoutTrackNotValidatedException("Scout");
		return new Scout(this.sql2o, this.name, this.email, this.pwd, this.rankID, this.age, this.troopID, this.reqID, this.mbID);
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
