package ScoutTrackApi;
/**
 * Exception thrown when no json is available to parse
 * @author Charlie Vorbach
 *
 */
public class NoJsonToParseException extends Exception {
	private static final long serialVersionUID = 8887090409227734356L;

	/**
	 * Constructs Exception
	 */
	public NoJsonToParseException() {
		super("No Json Available to Parse");
	}
}
