package ScoutTrackApi;
/**
 * Exception thrown when authentication fails
 * @author Charlie Vorbach
 */
public class AuthenticationException extends Exception {
	private static final long serialVersionUID = 5472454312463351082L;
	
	/**
	 * Constructs exception
	 */
	public AuthenticationException() {
		super("Authentication Failed");
	}
}
