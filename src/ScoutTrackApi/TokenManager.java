package ScoutTrackApi;
import java.text.ParseException;
import java.util.Date;

import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;

/**
 * Class for creating and managing json access tokens
 * @author Charlie Vorbach
 *
 */
public class TokenManager extends DatabaseSearcher {
	private static byte[] SECRET_KEY;
	private Sql2o sql2o;
	
	
	/**
	 * Initialize TokenManager
	 * @param secretKey the secret key for the tokens
	 * @param sql2o Sql2o database
	 */
	public TokenManager(Sql2o sql2o, byte[] secretKey) {
		super(sql2o);
		this.sql2o = sql2o;
		TokenManager.SECRET_KEY = secretKey;
	}
	
	/**
	 * Logs in user and retrieves access token
	 * @param email email of user
	 * @param pwd user's password
	 * @param userType type of leader (e.g. Scout, Leader, etc.)
	 * @return Serialized Json Access Token
	 * @throws AuthenticationException thrown if login info is not valid
	 * @throws JOSEException thrown if error in creating token
	 */
	public String getToken(String email, String pwd, int userType) throws AuthenticationException, JOSEException {
		int id;
		try {
			if(userType == ScoutTrackToken.SCOUT_TYPE) id =  getId(email, pwd, DatabaseNames.SCOUT_TABLE);
			else if(userType == ScoutTrackToken.LEADER_TYPE) id = getId(email, pwd, DatabaseNames.LEADER_TABLE);
			else throw new AuthenticationException();
		} catch(NoRecordFoundException e) {
			throw new AuthenticationException();
		}
		ScoutTrackToken token = new ScoutTrackToken(SECRET_KEY, id, userType);
		return token.getSerialToken();
	}
	
	/**
	 * Authenticates scout JWT
	 * @param tokenString the serialized JWT
	 * @return id of the scout
	 * @throws AuthenticationException thrown if authentication fails
	 * @throws JOSEException thrown if encryption fails
	 */
	public int authenticateScout(String tokenString) throws AuthenticationException, JOSEException {
		return authenticate(tokenString, ScoutTrackToken.SCOUT_TYPE);
	}
	
	/**
	 * Authenticates leader JWT
	 * @param tokenString the serialized JWT
	 * @return id of the Leader
	 * @throws AuthenticationException thrown if authentication fails
	 * @throws JOSEException thrown if encryption fails
	 */
	public int authenticateLeader(String tokenString) throws AuthenticationException, JOSEException {
		return authenticate(tokenString, ScoutTrackToken.LEADER_TYPE);
	}
	
	/**
	 * Authenticates leader JWT and check leader is member of the troop
	 * @param tokenString the serialized JWT
	 * @param troopID the id of the troop
	 * @return id of the Leader
	 * @throws AuthenticationException thrown if authentication fails
	 * @throws JOSEException thrown if encryption fails
	 */
	public int authenticateTroopLeader(String tokenString, int troopID) throws AuthenticationException, JOSEException    {
		int leaderID = authenticate(tokenString, ScoutTrackToken.LEADER_TYPE);
		try {
			System.out.println(super.queryInt(DatabaseNames.LEADER_TABLE, "troopid", leaderID));
			if(super.queryInt(DatabaseNames.LEADER_TABLE, "troopid", leaderID) != troopID) throw new AuthenticationException(); //Should I just make an additional claim? I think probably no b/c I can't revoke the jwt.
		} catch (NoRecordFoundException e) {
			throw new AuthenticationException(); // thrown because leader no longer exists in database (was deleted) and no longer has access to troop.
		}
		return leaderID;
	}
	
	/**
	 * Authenticates JWT of passed type
	 * @param tokenString the serialized JWT
	 * @param userType type of user
	 * @return id of the user
	 * @throws AuthenticationException thrown if authentication fails
	 * @throws JOSEException thrown if encryption fails
	 */
	private int authenticate(String tokenString, int userType) throws AuthenticationException, JOSEException {
		try {
			if (tokenString == null) throw new AuthenticationException();
			SignedJWT token = SignedJWT.parse(tokenString);
			if (!inspectSignature(token)) throw new AuthenticationException();
			if (token.getJWTClaimsSet().getIntegerClaim("typ") != userType) throw new AuthenticationException();
			return token.getJWTClaimsSet().getIntegerClaim("id");
		} catch (ParseException e) {
			throw new AuthenticationException();
		}
	}
	
	/**
	 * Inspects the signature and expiration time of a ScoutTrack-issued JWT
	 * @param token the token to inspect
	 * @return true if the token is valid, else false
	 * @throws ParseException thrown if token parsing fails
	 * @throws JOSEException thrown if decryption fails
	 */
	private boolean inspectSignature(SignedJWT token) throws ParseException, JOSEException {
		JWSVerifier verifier = new MACVerifier(SECRET_KEY);
		if(!token.verify(verifier)) return false;											//Check signature
		if(new Date().after(token.getJWTClaimsSet().getExpirationTime())) return false;	//Check expiration
		return true;
	}
	
	/**
	 * Retrieves user's id from database given username and password hash
	 * @param userName user name of the user
	 * @param pwd pwd hash of the user (not plaintext)
	 * @param table name of the table to search
	 * @return the id of the user with passed name and password hash
	 * @throws NoRecordFoundException thrown if no user with that name and password hash is found
	 * @throws Sql2oException thrown if database error occurs
	 */
	private int getId(String email, String pwd, String table) throws NoRecordFoundException, Sql2oException {
		String sql = "SELECT id FROM " + table + " WHERE email=:email AND pwd=:pwd"; 
		Connection conn = sql2o.open();
		String response = conn.createQuery(sql).addParameter("email", email).addParameter("pwd", pwd).executeAndFetchFirst(String.class);
		if (response == null) throw new NoRecordFoundException();
		return Integer.parseInt(response);
	}
}
