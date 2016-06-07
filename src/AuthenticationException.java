
public class AuthenticationException extends Exception {
	private static final long serialVersionUID = 5472454312463351082L;

	public AuthenticationException() {
		super("Bad Login Username or Password");
	}
}
