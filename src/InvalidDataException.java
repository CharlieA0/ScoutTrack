
@SuppressWarnings("serial")
public class InvalidDataException extends Exception{
	public InvalidDataException(String error) {
		super("Json Data Invalid" + " " + error);
	}
	public InvalidDataException() {
		super("Json Data Invalid");
	}
}
