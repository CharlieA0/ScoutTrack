package ScoutTrackApi;

import java.util.ArrayList;
import java.util.List;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import com.google.gson.JsonObject;

public class AssetRetrieval extends DatabaseSearcher {
	
	public AssetRetrieval(Sql2o sql2o) {
		super(sql2o);
	}
	
	/**
	 * Gets json object containing boy scout requirements from database. 
	 * @return list of requirements
	 * @throws NoRecordFoundException thrown if no record of the rank is found.
	 */
	public List <RankRequirementsObject> getRankRequirements() throws NoRecordFoundException {
		List <Integer> rankIds = super.queryIntColumn(DatabaseNames.RANK_TABLE, "id");
		
		List <RankRequirementsObject> rankRequirements = new ArrayList <RankRequirementsObject> ();
		for (int rankId : rankIds) {
			String rankName = super.queryString(DatabaseNames.RANK_TABLE, "name", rankId);
			List <String> requirements = getRequirementsFor(rankId);
			rankRequirements.add(new RankRequirementsObject(rankName, requirements));
		}
		
		return rankRequirements;
	}
	
	/**
	 * Gets requirements for the specified rank
	 * @param rank name of the rank
	 * @return List of requirements
	 * @throws Sql2oException thrown if database error occurs.
	 * @throws NoRecordFoundException thrown if no record of the rank is found.
	 */
	private List<String> getRequirementsFor(int rankId) throws Sql2oException, NoRecordFoundException {
		return super.fetchStringsWhere(DatabaseNames.REQ_TABLE, "rankid", rankId, "requirement");
	}
	
	private List<String> getRequirementsFor(String rankName) throws Sql2oException, NoRecordFoundException {
		int rankId = super.idOfRank(rankName);
		return super.fetchStringsWhere(DatabaseNames.REQ_TABLE, "rankid", rankId, "requirement");
	}
}
