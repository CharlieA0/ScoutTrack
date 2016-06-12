package ScoutTrackApi;
/**
 * Object encapsulating database requirement record
 * @author Charlie Vorbach
 */
public class RequirementRecord {
	private String name;
	private int rankid;
	
	/**
	 * Gets id of rank requirement is part of
	 * @return rank's id
	 */
	public int getRankID() {
		return rankid;
	}
	
	/**
	 * Gets name of the requirement
	 * @return name of requirement
	 */
	public String getName() {
		return name;
	}
}
