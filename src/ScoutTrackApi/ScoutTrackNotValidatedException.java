
@SuppressWarnings("serial")
public class ScoutTrackNotValidatedException extends Exception {
	public ScoutTrackNotValidatedException (String ObjectType) {
		super(ObjectType + " was not validated. Can not construct or get " + ObjectType);
	}
}
