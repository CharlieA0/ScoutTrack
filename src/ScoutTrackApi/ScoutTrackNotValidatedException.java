package ScoutTrackApi;

/**
 * Exception thrown when database object is created without having first been validated.
 * @author Charlie Vorbach
 *
 */
public class ScoutTrackNotValidatedException extends Exception {
	private static final long serialVersionUID = 6358766166815165543L;

	/**
	 * Constructs exception
	 * @param ObjectType type of database object
	 */
	public ScoutTrackNotValidatedException (String ObjectType) {
		super(ObjectType + " was not validated. Can not construct or get " + ObjectType);
	}
}
