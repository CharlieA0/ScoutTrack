import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import static spark.Spark.delete;

import java.util.List;

import spark.Response;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.nimbusds.jose.JOSEException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;


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
         
            /* USER API */
            
            //Create new scout
            post("/scout", (request, response) -> {
            	try {
            		JsonObject json;
            	
            		//Attempt to Parse Json            	
            		json = new JsonParser().parse(request.body()).getAsJsonObject();
            	            	
            		//Attempt to Map Json data to Scout Fields
            		ScoutMapper scoutMap = new ScoutMapper(json, sql2o);	
            		scoutMap.validate();
        		
            		//Store Scout in Database and return Scout ID
            		Scout scout = scoutMap.getScout();
            		return scout.getID();
            	
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            //Remove scout
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
            
            //Create new leader
            post("/leader", (request, response) -> {
            	try {
            		JsonObject json;       
            		json = new JsonParser().parse(request.body()).getAsJsonObject();
            		
            		//Map Json data to Leader object
            		LeaderMapper leaderMap = new LeaderMapper(json, sql2o);
            		leaderMap.validate();
            	
            		Leader leader = leaderMap.getLeader();
            		return leader.getID();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Remove leader
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
            
            //Get all scout info
            get("/scout/:id/info",  (request, response) -> {
            	return "Not Implemented";
            });
        
            //Get scout email
            get("/scout/:id/email",  (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);	            	
	            	return scout.queryEmail();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Update scout email
            put("/scout/:id/email", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		scout.updateEmail(new ScoutMapper(sql2o).validateEmail(request.queryParams("email")));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
			
			//Update scout password hash
			put("/scout/:id/pwd", (request, response) -> {
               	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
               		scout.updatePwd(new ScoutMapper(sql2o).validatePwd(request.queryParams("pwd")));
               		return "";
               	} catch (Exception e) {
            		return handle(response, e);
            	}
			});
			
            //Get user troop
            get("/scout/:id/troop",  (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		return scout.queryTroop();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Update scout troop
            put("/scout/:id/troop", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		scout.updateTroop(request.queryParams("troop"));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            //Get scout rank
            get("/scout/:id/rank",  (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		return scout.queryRank();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            //Update scout rank
            put("/scout/:id/rank", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		scout.updateRank(request.queryParams("rank"));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Update scout list of meritbadges
            post("/scout/:id/mb", (request, response) -> {
            	return "Not Implemented";
            });
            
            //Get scout's meritbadges
            get("/scout/:id/mb", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		return new Gson().toJson(scout.queryMb());
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });       
            
            //Add scout meritbadge
            put("/scout/:id/mb", (request, response) -> { 
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		scout.addMb(request.queryParams("mbName"));
            		return"";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Remove a scout's meritbadge
            delete("/scout/:id/mb", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		scout.destroyMb(request.queryParams("meritbadgeName"));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            //Update scout partial requirements
            post("/scout/:id/req", (request, response) -> {
            	return "Not Implemented";
            });
            
            //Get scout partial requirements
            get("/scout/:id/req",  (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		List<RequirementObject> reqs =  scout.queryReq();
            		return new Gson().toJson(reqs);
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Add scout requirement
            put("/scout/:id/req", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		scout.addReq(request.queryParams("name"), request.queryParams("rank"));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Remove scout requirement
            delete("/scout/:id/req", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		scout.destroyReq(request.queryParams("name"), request.queryParams("rank"));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Get scout name
            get("/scout/:id/name",  (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		return scout.queryName();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            //Update scout name
            put("/scout/:id/name", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		scout.updateName(new ScoutMapper(sql2o).validateName(request.queryParams("name")));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Get scout age
            get("/scout/:id/age",  (request, response) -> {
            	try {
            		int id = tokenManager.authenticateScout(request.headers("Authorization"));
	            	Scout scout = new Scout(id, sql2o);
            		return scout.queryAge();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Update scout age
            put("/scout/:id/age", (request, response) -> {
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

            //Get leader info
            get("/leader/:id/info",  (request, response) -> {
            	return "Not Implemented";
            });
            
            //Get leader name
            get("/leader/:id/name",  (request, response) -> { 
            	try { 
            		int id = tokenManager.authenticateLeader(request.headers("Authorization"));
            		Leader leader = new Leader(id, sql2o);
            		return leader.queryName();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Update leader name
            put("/leader/:id/name",  (request, response) -> {
            	try {
            		int id = tokenManager.authenticateLeader(request.headers("Authorization"));
            		Leader leader = new Leader(id, sql2o);
            		leader.updateName(new LeaderMapper(sql2o).validateName(request.queryParams("name")));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Get leader email
            get("/leader/:id/email", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateLeader(request.headers("Authorization"));
            		Leader leader = new Leader(id, sql2o);
            		return leader.queryEmail();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Update leader email
            put("/leader/:id/email", (request, response) -> {
            	try {
            		int id = tokenManager.authenticateLeader(request.headers("Authorization"));
            		Leader leader = new Leader(id, sql2o);
            		leader.updateEmail(new LeaderMapper(sql2o).validateEmail(request.queryParams("email")));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            //Get leader troop
            get("/leader/:id/troop",  (request, response) -> {
            	try {
            		int id = tokenManager.authenticateLeader(request.headers("Authorization"));
            		Leader leader = new Leader(id, sql2o);
            		return leader.queryTroop();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });            

            //Update leader troop
            put("/leader/:id/troop", (request, response) -> {
              	try {
            		int id = tokenManager.authenticateLeader(request.headers("Authorization"));
            		Leader leader = new Leader(id, sql2o);
              		leader.updateTroop(request.queryParams("troop"));
              		return "";
              	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
             
			//Update leader password hash
			put("/leader/:id/pwd", (request, response) -> {
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
            
            //Create new troop
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
            
            //Remove troop
            delete("/troop/:id", (request, response) -> {
            	return "Not Implemented";
            });
            
            //Get troop information
            get("/troop/:id/info", (request, response) -> {
            	return "Not Implemented";
            });
            
            //Get troop name
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
            
            //Update troop name
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

            //Get troop scout list
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
            
            //Update troop scout list
            post("troop/:id/scouts", (request, response) -> {
            	return "Not Implemented";
            });
            
            //Add scout to troop list
            put("troop/:id/scouts", (request, response) ->{
            	return "Not Implemented";
            });
            
            //Remove scout from troop list
            delete("troop/:id/scouts", (request, response) -> {
            	return "Not Implemented";
            });
            
            //Get troop leaders
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
            
            //Update troop leader list
            post("troop/:id/leaders", (request, response) -> {
            	return "Not Implemented";
            });
            
            //Add leader to troop list
            put("troop/:id/leaders", (request, response) ->{
            	return "Not Implemented";
            });
            
            //Remove leader from troop list
            delete("troop/:id/leaders", (request, response) -> {
            	return "Not Implemented";
            });
           	
    		/* TOKEN API */
    		
            //get Token ?Need database of Tokens?
            get("/token", (request, response)-> {
                try {
                	return tokenManager.getToken(request.queryParams("email"), request.queryParams("pwd"), Integer.parseInt(request.queryParams("type")));
                } catch (Exception e) {
                	return handle(response, e);
                }
            });
            
            //renew token
            get("/token/renew", (request, response)->{
            	return "Not Implemented";
            });
        
            //destroy token
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
       			 e.printStackTrace();
       			 return "Request Data Invalid, No Record Found";
       		 }
       		 else if (e instanceof JsonSyntaxException || e instanceof InvalidJsonDataException || e instanceof NumberFormatException) {
       			response.status(HTTP_BAD_REQUEST);
       			return "Could Not Parse Request. Request Invalid.";
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
