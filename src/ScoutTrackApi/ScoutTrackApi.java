package ScoutTrackApi;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import static spark.Spark.delete;

import java.util.List;

import spark.Response;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.nimbusds.jose.JOSEException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

/**
 * REST Api for securely storing, updating, and retrieving Scout information
 * @author Charlie Vorbach
 */

public class ScoutTrackApi {
	private static final byte[] SECRET_KEY = KeyFunctions.generateSHA256();
	private static final String DB_NAME = "scouttrack";
	private static final String DB_USERNAME = "Charlie";
	private static final String DB_PASSWORD = "Conway";
	
	private static final int HTTP_BAD_REQUEST = 400;
	private static final int HTTP_ACCESS_DENIED = 403;
	private static final int HTTP_INTERNAL_ERROR = 500;
	
    public static void main(String[] args){
    		
    		Sql2o sql2o = new Sql2o("jdbc:postgresql://localhost:5432/" + DB_NAME, DB_USERNAME, DB_PASSWORD);
    		TokenManager tokenManager = new TokenManager(sql2o, SECRET_KEY);
    		
    		/* More Magic */
			get("/hello", (request, response) -> {
				return "Hello, World";
			});
  		 
            /* USER API */
            
            /**
             * Stores new scout in database
             */
            post("/scout", (request, response) -> {
            	try {
            		JsonObject json;
            	
            		//Attempt to Parse Json            	
            		json = new JsonParser().parse(request.body()).getAsJsonObject();
            	            	
            		//Attempt to Map Json data to Scout Fields
            		ScoutMapper scoutMap = new ScoutMapper(json, sql2o);	
            		scoutMap.validate();
        		
            		//Store Scout in Database and return
            		scoutMap.getScout();
            		return "";
            	
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            /**
             * Removes Scout from database
             */
            delete("/scout", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
	            	scout.destroy();
	            	return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Stores new leader in database
             */
            post("/leader", (request, response) -> {
            	try {
            		JsonObject json;       
            		json = new JsonParser().parse(request.body()).getAsJsonObject();
            		
            		//Map Json data to Leader object
            		LeaderMapper leaderMap = new LeaderMapper(json, sql2o);
            		leaderMap.validate();
            	
            		//Store Leader
            		leaderMap.getLeader();
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Remove leader from database
             */
            delete("/leader", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateLeader(request.headers("Authorization"));
            		Leader leader = new Leader(id, sql2o);
            		leader.destroy();
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /* SCOUTS */
            
            /**
             * Retrieves a json containing Scout's information
             */
            get("/scout/info",  (request, response) -> {
            	return "Not Implemented";
            });
        
            /**
             * Retrieves scout's email
             */
            get("/scout/email",  (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);	            	
	            	return scout.queryEmail();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Updates scout's email
             */
            put("/scout/email", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		scout.updateEmail(new ScoutMapper(sql2o).validateEmail(request.queryParams("email")));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
			
			/**
			 * Updates scout's password hash (not plaintext)
			 */
			put("/scout/pwd", (request, response) -> {
               	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
               		scout.updatePwd(new ScoutMapper(sql2o).validatePwd(request.queryParams("pwd")));
               		return "";
               	} catch (Exception e) {
            		return handle(response, e);
            	}
			});
			
            /**
             * Retrieve's name of scout's troop
             */
            get("/scout/troop",  (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		return scout.queryTroop();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Updates scout's troop
             */
            put("/scout/troop", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		scout.updateTroop(request.queryParams("troop"));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            /**
             * Retrieve's name of scout's rank
             */
            get("/scout/rank",  (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		return scout.queryRank();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            /**
             * Updates rank of scout
             */
            put("/scout/rank", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		scout.updateRank(request.queryParams("rank"));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Updates scout's meritbadges with new json list of meritbadges
             */
            post("/scout/mb", (request, response) -> {
            	return "Not Implemented";
            });
            
            /**
             * Returns json of Scout's meritbadges
             */
            get("/scout/mb", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		return new Gson().toJson(scout.queryMb());
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });       
            
            /**
             * Adds meritbadge to scout
             */
            put("/scout/mb", (request, response) -> { 
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		scout.addMb(request.queryParams("mbName"));
            		return"";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Remove meritbadge from scout
             */
            delete("/scout/mb", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		scout.destroyMb(request.queryParams("meritbadgeName"));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            /**
             * Replaces scout's list of partial requirements with new list of partial requirements
             */
            post("/scout/req", (request, response) -> {
            	return "Not Implemented";
            });
            
            /**
             * Returns scout's list of partial requirements
             */
            get("/scout/req",  (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		List<RequirementObject> reqs =  scout.queryReq();
            		return new Gson().toJson(reqs);
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Adds partial requirement to scout
             */
            put("/scout/req", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
	            	ScoutMapper map = new ScoutMapper(sql2o);
            		scout.addReq(map.validateNewRequirement(new DatabaseSearcher(sql2o), id, request.queryParams("name"), request.queryParams("rank")));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Removes partial requirement from scout
             */
            delete("/scout/req", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		scout.destroyReq(request.queryParams("name"), request.queryParams("rank"));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Gets scout's name
             */
            get("/scout/name",  (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		return scout.queryName();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            /**
             * Updates scout's name
             */
            put("/scout/name", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		scout.updateName(new ScoutMapper(sql2o).validateName(request.queryParams("name")));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Gets scout's age
             */
            get("/scout/age",  (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		return scout.queryAge();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Updates scout's age
             */
            put("/scout/age", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		scout.updateAge(new ScoutMapper(sql2o).validateAge(Integer.parseInt(request.queryParams("age"))));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /* LEADERS */

            /**
             * Get Leader info in json
             */
            get("/leader/info",  (request, response) -> {
            	return "Not Implemented";
            });
            
            /**
             * Get Leader's name
             */
            get("/leader/name",  (request, response) -> { 
            	try { 
            		int id = tokenManager.authenticateLeader(request.headers("Authorization"));
            		Leader leader = new Leader(id, sql2o);
            		return leader.queryName();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Update leader's name
             */
            put("/leader/name",  (request, response) -> {
            	try {
            		int id = tokenManager.authenticateLeader(request.headers("Authorization"));
            		Leader leader = new Leader(id, sql2o);
            		leader.updateName(new LeaderMapper(sql2o).validateName(request.queryParams("name")));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Update Leader's troop
             */
            get("/leader/email", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateLeader(request.headers("Authorization"));
            		Leader leader = new Leader(id, sql2o);
            		return leader.queryEmail();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Update Leader's email
             */
            put("/leader/email", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateLeader(request.headers("Authorization"));
            		Leader leader = new Leader(id, sql2o);
            		leader.updateEmail(new LeaderMapper(sql2o).validateEmail(request.queryParams("email")));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            /**
             * Get leader's troop name
             */
            get("/leader/troop",  (request, response) -> {
            	try {
            		int id = tokenManager.authenticateLeader(request.headers("Authorization"));
            		Leader leader = new Leader(id, sql2o);
            		return leader.queryTroop();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });            

            /**
             * Update leader's troop
             */
            put("/leader/troop", (request, response) -> {
              	try {
            		int id = tokenManager.authenticateLeader(request.headers("Authorization"));
            		Leader leader = new Leader(id, sql2o);
              		leader.updateTroop(request.queryParams("troop"));
              		return "";
              	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
             
			/**
			 * Update leader's password hash (not plaintext)
			 */
			put("/leader/pwd", (request, response) -> {
				try {
            		int id = tokenManager.authenticateLeader(request.headers("Authorization"));
            		Leader leader = new Leader(id, sql2o);
					leader.updatePwd(new LeaderMapper(sql2o).validatePwd(request.queryParams("pwd")));
					return "";
				} catch (Exception e) {
            		return handle(response, e);
            	}
			});

            /* TROOP API */
            
            /**
             * Create new troop in database
             */
            post("/troop", (request, response) -> {
            	try{
            		JsonObject json;
            		json = new JsonParser().parse(request.body()).getAsJsonObject();
            	
            		TroopMapper troopMap = new TroopMapper(json, sql2o);
            		troopMap.validate();

            		Troop troop = troopMap.getTroop();            	
            		return troop.getID();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Remove troop from database
             */
            delete("/troop/:id", (request, response) -> {
            	return "Not Implemented";
            });
            
            /**
             * Get troop info from database
             */
            get("/troop/:id/info", (request, response) -> {
            	return "Not Implemented";
            });
            
            /**
             *Get troop name
             */
            get("/troop/:id/name",  (request, response) -> {
            	try {
            		int id = Integer.parseInt(request.params("id"));
            		tokenManager.authenticateTroopLeader(request.headers("Authorization"), id);
            		Troop troop = new Troop(id, sql2o);
            		return troop.queryName();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Update troop name
             */
            put("/troop/:id/name", (request, response) -> {
            	try {
            		int id = Integer.parseInt(request.params("id"));
            		tokenManager.authenticateTroopLeader(request.headers("Authorization"), id);
            		Troop troop = new Troop(id, sql2o);
            		troop.updateName(new TroopMapper(sql2o).validateName(request.queryParams("name")));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            /**
             * Get troop scout list
             */
            get("/troop/:id/scouts",  (request, response) -> {
            	try {
            		int id = Integer.parseInt(request.params("id"));
            		tokenManager.authenticateTroopLeader(request.headers("Authorization"), id);
            		Troop troop = new Troop(id, sql2o);
            		return new Gson().toJson(troop.queryScouts());
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Update troop scout list
             */
            post("troop/:id/scouts", (request, response) -> {
            	return "Not Implemented";
            });
            
            /**
             * Add scout to troop list
             */
            put("troop/:id/scouts", (request, response) ->{
            	return "Not Implemented";
            });
            
            /**
             * Remove scout from troop list
             */
            delete("troop/:id/scouts", (request, response) -> {
            	return "Not Implemented";
            });
            
            /**
             * Get troop leaders
             */
            get("troop/:id/leaders", (request, response) -> {
            	try {
            		int id = Integer.parseInt(request.params("id"));
            		tokenManager.authenticateTroopLeader(request.headers("Authorization"), id);
            		Troop troop = new Troop(id, sql2o);
            		response.body(new Gson().toJson(troop.queryLeaders()));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            /**
             * Update troop leader list
             */
            post("troop/:id/leaders", (request, response) -> {
            	return "Not Implemented";
            });
            
            /**
             * Add leader to troop list
             */
            put("troop/:id/leaders", (request, response) ->{
            	return "Not Implemented";
            });
            
            /**
             * Remove leader from troop list
             */
            delete("troop/:id/leaders", (request, response) -> {
            	return "Not Implemented";
            });
           	
    		/* TOKEN API */
    		
            /**
             * get Token for scout
             */
            get("/scout/token", (request, response)-> {
                try {
                	ScoutMapper map = new ScoutMapper(sql2o);
                	return tokenManager.getToken(map.validateEmail(request.queryParams("email")), new ScoutMapper(sql2o).validatePwd(request.queryParams("pwd")), ScoutTrackToken.SCOUT_TYPE);
                } catch (Exception e) {
                	return handle(response, e);
                }
            });
            
          /**
           * get Token for Leader
           */
            get("/leader/token", (request, response)-> {
                try {
                	return tokenManager.getToken(request.queryParams("email"), request.queryParams("pwd"), ScoutTrackToken.LEADER_TYPE);
                } catch (Exception e) {
                	return handle(response, e);
                }
            });
            
            /**
             * renew token
             */
            put("/token", (request, response)->{
            	return "Not Implemented";
            });
        
            /**
             * destroy token
             */
            delete("/token", (request, response) -> {
            	return "Not Implemented";
            });
            
        }
            
    	/**
    	 * Handles exceptions caused by http requests and returns appropriate response
    	 * @param response Spark Response object
    	 * @param e an exceptions
    	 * @return reply to http request
    	 */
        private static String handle(Response response, Exception e) {
       		 if (e instanceof NoRecordFoundException) {
       			 response.status(HTTP_BAD_REQUEST);
       			 return "Request Data Invalid, No Record Found";
       		 }
       		 else if (e instanceof JsonSyntaxException || e instanceof InvalidDataException || e instanceof NumberFormatException || e instanceof MalformedJsonException) {
       			 response.status(HTTP_BAD_REQUEST);
       			 return "Could Not Parse Request. Request Invalid."; //Implement Unique Email Exception
       		 }
       		 else if(e instanceof AuthenticationException) {
       			 response.status(HTTP_ACCESS_DENIED);
       			 return "Could Not Authenticate. Access Denied.";
       		 }
       		 else if(e instanceof JOSEException) {
       			 e.printStackTrace();
       			 response.status(HTTP_ACCESS_DENIED);
       			 return "Could Not Perform Secure Authentication.";
       		 }
       		 else if(e instanceof Sql2oException) { 
       			 e.printStackTrace();
       			 response.status(HTTP_INTERNAL_ERROR);
       			 return "Database Error. Try Again Later.";
       		 }
       		 
       		 e.printStackTrace();
       		 response.status(HTTP_INTERNAL_ERROR);
       		 return "Internal Error. Try Again Later.";
       	 }
    }
