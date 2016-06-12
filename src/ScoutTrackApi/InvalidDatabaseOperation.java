
public class InvalidDatabaseOperation extends Exception {
	private static final long serialVersionUID = -7731933950577528542L;

	public InvalidDatabaseOperation(String message) {
		super("Can not preform operation " + message);
	}
}
