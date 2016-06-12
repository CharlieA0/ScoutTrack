package ScoutTrackApi;
/**
 * Exception thrown when invalid database operation is attempted
 * @author Charlie Vorbach
 *
 */
public class InvalidDatabaseOperation extends Exception {
	private static final long serialVersionUID = -7731933950577528542L;

	/**
	 * Constructs exception
	 * @param message custom error message
	 */
	public InvalidDatabaseOperation(String message) {
		super("Can not preform operation " + message);
	}
}
