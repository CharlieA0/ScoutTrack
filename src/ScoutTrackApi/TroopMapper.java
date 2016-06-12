package ScoutTrackApi;
import java.util.Arrays;
import java.util.List;

import org.sql2o.Sql2o;

import com.google.gson.JsonObject;

/**
 * Class mapping json data to troop object
 * @author Charlie
 *
 */
public class TroopMapper extends DatabaseObjectMapper {
	private final int TROOP_NAME_LENGTH = 30;
	
	private final List <String> FIELDS;
	private final String[] TROOP_FIELDS = {};
	
	private final JsonObject json;
	private final Sql2o sql2o;
		
	private boolean validated;
	private String name;
	private int[] scoutIDs;
	
	/**
	 * Constructs a TroopMapper which validates Json data and maps it to a Troop object
	 * @param json json data
	 * @param sql2o database object
	 */
	public TroopMapper(JsonObject json, Sql2o sql2o) {	
		this.json = json;
		this.sql2o = sql2o;
		this.validated = false;

		this.FIELDS = getFields();
	}
	
	/**
	 * Constructs TroopMapper without json data
	 * @param sql2o database object
	 */
	public TroopMapper(Sql2o sql2o) {
		this.json = null;
		this.sql2o = sql2o;
		this.validated = false;
		
		this.FIELDS = getFields();
	}
	
	/**
	 * Returns true if data is valid and can be mapped.
	 * @throws InvalidDataException thrown if json data is invalid
	 * @throws NoJsonToParseException thrown if no json data is available to parse
	 */
	public void validate() throws InvalidDataException, NoJsonToParseException {	
		if(json == null) throw new NoJsonToParseException();
		
		for(String field : FIELDS) {
			if(!json.has(field)) throw new InvalidDataException();
		}
		
		this.name = json.get("name").getAsString();
		validateName(name);

		this.validated = true;
	}
	
	/**
	 * Checks string is valid troop name
	 * @param name name of troop
	 * @return name of troop
	 * @throws InvalidDataException thrown if name is invalid
	 */
	public String validateName(String name) throws InvalidDataException {
		if(!checkString(name, TROOP_NAME_LENGTH)) throw new InvalidDataException();
		return name;
	}
	
	/**
	 * Constructs Troop object and stores data in database
	 * @return the troop object
	 * @throws ScoutTrackNotValidatedException thrown if mapping wasn't validated or validation failed.
	 */
	public Troop getTroop() throws ScoutTrackNotValidatedException {
		if(!validated) throw new ScoutTrackNotValidatedException("Troop");
		return new Troop(this.sql2o, this.name, this.scoutIDs);
	}
	
	/**
	 * Gets necessary fields from super class and adds troop fields
	 */
	protected List <String> getFields() {
		List <String> fields = super.getFields();
		fields.addAll(Arrays.asList(TROOP_FIELDS));
		return fields;
	}
}
