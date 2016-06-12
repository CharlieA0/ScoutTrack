package ScoutTrackApi;

/**
 * Exception thrown when no record is found in database
 * @author Charlie Vorbach
 *
 */
public class NoRecordFoundException extends Exception {
	private static final long serialVersionUID = -6737077866414102446L;

	/**
	 * Constructs exception
	 */
	public NoRecordFoundException() {
		super("No Record Found");
	}
}
