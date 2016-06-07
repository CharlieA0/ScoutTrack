import org.sql2o.Sql2o;

public class TokenManager extends DatabaseSeracher {
	public final byte[] SECRET_KEY;
	private Sql2o sql2o;
	
	
	/**
	 * Initialize TokenManager
	 * @param secretKey the secret key for the tokens
	 */
	public TokenManager(Sql2o sql2o, byte[] secretKey) {
		super(sql2o);
		this.sql2o = sql2o;
		this.SECRET_KEY = secretKey;
	}
	
	public String loginScout(String userName, String pwd) {
		
	}
}
