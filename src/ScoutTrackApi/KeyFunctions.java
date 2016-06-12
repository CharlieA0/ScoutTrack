package ScoutTrackApi;
import java.security.SecureRandom;

/**
 * Functions for generating keys and performing key operations
 * @author Charlie Vorbach
 */
public class KeyFunctions {
	private final static int keyLengthSHA256 = 32;
	
	/**
	 * Create new random SHA256 key
	 * @return SHA256 key
	 */
	public static byte[] generateSHA256() {
		byte[] randomkey = new byte[keyLengthSHA256];
		new SecureRandom().nextBytes(randomkey);
		return randomkey;
	}
}
