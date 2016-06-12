package ScoutTrackApi;

/**
 * Exception thrown when invalid data is passed
 * @author Charlie Vorbach
 *
 */
public class InvalidDataException extends Exception{
	private static final long serialVersionUID = 2297158887937176955L;

	/**
	 * Constructs exception with custom message
	 * @param error error message
	 */
	public InvalidDataException(String error) {
		super("Json Data Invalid" + " " + error);
	}
	
	/**
	 * Constructs exception
	 */
	public InvalidDataException() {
		super("Json Data Invalid");
	}
}
