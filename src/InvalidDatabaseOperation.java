
public class InvalidDatabaseOperation extends Exception {
	public InvalidDatabaseOperation(String message) {
		super("Can not preform operation " + message);
	}
}
