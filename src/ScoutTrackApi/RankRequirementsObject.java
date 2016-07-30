package ScoutTrackApi;

import java.util.List;

public class RankRequirementsObject {
	private String rank;
	private List <String> requirements;
	
	/**
	 * Constructs RequirementObject with requirement info
	 * @param name name of the requirement.
	 * @param rankName name of the rank the requirement is in.
	 */
	public RankRequirementsObject(String rank, List <String> requirements) {
		this.rank = rank;
		this.requirements = requirements;
	}
}
