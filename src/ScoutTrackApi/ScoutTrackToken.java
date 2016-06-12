package ScoutTrackApi;
import java.util.Calendar;
import java.util.Date;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * Class encapsulating Json Access token and operations on such
 * @author Charlie Vorbach
 *
 */
public class ScoutTrackToken {
	private byte[] secretKey;
	private int userID;
	private int userType;
	
	private int TOKEN_LIFESPAN = 60; //In days
	private String SCOUTTRACK_IDENTIFIER = "http://scouttrack.org";
	
	public final static int SCOUT_TYPE = 0;
	public final static int LEADER_TYPE = 1;
	
	/**
	 * Initializes Token Info 
	 * @param secretKey key for token
	 * @param userID user's id (from database)
	 * @param userType user's account type (Scout, Leader, etc.)
	 */
	public ScoutTrackToken(byte[] secretKey, int userID, int userType) {
		this.secretKey = secretKey;
		this.userID = userID;
		this.userType = userType;
	}
	
	/**
	 * Builds JWT using secret key. The token identifies one user.
	 * @return Serialized JWT.
	 * @throws JOSEException thrown if encryption fails
	 */
	public String getSerialToken() throws JOSEException {
		JWSSigner signer = new MACSigner(secretKey);	//Prepare to sign token with secret key
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().claim("id", userID).claim("typ", userType) //Add private claims
				.issuer(SCOUTTRACK_IDENTIFIER).expirationTime(getExpiration(TOKEN_LIFESPAN)).build();	//Add standard claims
		
		SignedJWT token = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);	//Build token
		token.sign(signer);																//Sign token
								
		return token.serialize();	//Convert token to String
	}
	
	/**
	 * Safely calculate expiration date of token.
	 * @param numberOfDaysValid period for which token is valid
	 * @return Date on which token expires
	 */
	private Date getExpiration(int numberOfDaysValid) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, numberOfDaysValid);
		return cal.getTime();
	}
}
