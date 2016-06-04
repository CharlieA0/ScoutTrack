import java.security.SecureRandom;

public class KeyFunctions {
	private final static int keyLengthSHA256 = 32;
	
	
	public static byte[] generateSHA256() {
		byte[] randomkey = new byte[keyLengthSHA256];
		new SecureRandom().nextBytes(randomkey);
		return randomkey;
	}
}
