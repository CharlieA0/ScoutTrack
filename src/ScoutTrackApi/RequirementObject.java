package ScoutTrackApi;
/**
 * Object encapuslating scout requirement
 * @author Charlie
 *
 */
public class RequirementObject {
	private String name;
	private String rank;
	
	/**
	 * Constructs RequirementObject with requirement info
	 * @param name name of the requirement.
	 * @param rankName name of the rank the requirement is in.
	 */
	public RequirementObject(String name, String rankName) {
		this.name = name;
		this.rank = rankName;
	}

	/**
	 * Get requirement name.
	 * @return name of the rank.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get name of the rank this requirement is in.
	 * @return name of rank this requirement is in.
	 */
	public String getRank() {
		return rank;
	}

}
