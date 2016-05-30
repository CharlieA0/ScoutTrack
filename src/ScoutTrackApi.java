import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.util.jar.JarException;

import static spark.Spark.delete;
import spark.Response;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;


public class ScoutTrackApi {
	private static final String DB_NAME = "scouttrack";
	private static final String DB_USERNAME = "Charlie";
	private static final String DB_PASSWORD = "Conway";
	
	private static final int HTTP_BAD_REQUEST = 400;
	private static final int HTTP_INTERNAL_ERROR = 500;
	
    public static void main(String[] args){
    		
    		Sql2o sql2o = new Sql2o("jdbc:postgresql://localhost:5432/" + DB_NAME, DB_USERNAME, DB_PASSWORD);
         
            /* USER API */
            
            //Create new scout
            post("/scout/new", (request, response) -> {
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
            delete("/scout/:id/destroy", (request, response) -> {
            	try {
	            	Scout scout = new Scout(Integer.parseInt(request.params("id")), sql2o);
	            	scout.destroy();
	            	return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Create new leader
            post("/leader/new", (request, response) -> {
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
            delete("/leader/:id/destroy", (request, response) -> {
            	try {
            		Leader leader = new Leader(Integer.parseInt(request.params("id")), sql2o);
            		leader.destroy();
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Get all scout info
            get("/scout/:id/info",  (request, response) -> {
            	return "Not Implemented";
            });
        
            //Get scout email
            get("/scout/:id/email",  (request, response) -> {
            	try {
            		Scout scout = new Scout(Integer.parseInt(request.params("id")), sql2o);
            		return scout.queryEmail();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Update scout email
            put("/scout/:id/email/:email", (request, response) -> {
            	try {
            		Scout scout = new Scout(Integer.parseInt(request.params("id")), sql2o);
            		scout.updateEmail(new ScoutMapper(sql2o).validateEmail(request.params("email")));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
			
			//Update scout password hash
			put("/scout/:id/pwd/:pwd", (request, response) -> {
               	try {
               		Scout scout = new Scout(Integer.parseInt(request.params("id")), sql2o);
               		scout.updatePwd(new ScoutMapper(sql2o).validatePwd(request.params("pwd")));
               		return "";
               	} catch (Exception e) {
            		return handle(response, e);
            	}
			});
			
			/*Get scout salt - Not Necessary: Client -> Hash Plaintext -> https -> Server add Salt and Hash Again
			get("/scout/:id/salt", (request, response) -> {
				try {
					Scout scout = new Scout(Integer.parseInt(request.params("id")), sql2o);
					return scout.querySalt();
				} catch (Exception e) {
            		return handle(response, e);
            	}
			});*/
            
            //Get user troop
            get("/scout/:id/troop",  (request, response) -> {
            	try {
            		Scout scout = new Scout(Integer.parseInt(request.params("id")), sql2o);
            		return scout.queryTroop();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Update scout troop
            put("/scout/:id/troop/:troop", (request, response) -> {
            	try {
            		Scout scout = new Scout(Integer.parseInt(request.params("id")), sql2o);
            		scout.updateTroop(request.params("troop"));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            //Get scout rank
            get("/scout/:id/rank",  (request, response) -> {
            	try {
            		Scout scout = new Scout(Integer.parseInt(request.params("id")), sql2o);
            		return scout.queryRank();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            //Update scout rank
            put("/scout/:id/rank/:rank", (request, response) -> {
            	try {
            		Scout scout = new Scout(Integer.parseInt(request.params("id")), sql2o);
            		scout.updateRank(request.params("rank"));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Get user partial requirements
            get("/scout/:id/req",  (request, response) -> {
            	return "Not Implemented";
            });
            
            //Update scout partial requirements
            post("/scout/:id/req", (request, response) -> {
            	return "Not Implemented";
            });
            
            //Add scout requirement
            put("/scout/:id/req/add", (request, response) -> {
            	return "Not Implemented";
            });
            
            //Remove scout requirement
            delete("/scout/:id/req/destroy", (request, response) -> {
            	return "Not Implemented";
            });
            
            //Get scout name
            get("/scout/:id/name",  (request, response) -> {
            	try {
            		Scout scout = new Scout(Integer.parseInt(request.params("id")), sql2o);
            		return scout.queryName();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            //Update scout name
            put("/scout/:id/name/:name", (request, response) -> {
            	try {
            		Scout scout = new Scout(Integer.parseInt(request.params("id")), sql2o);
            		scout.updateName(new ScoutMapper(sql2o).validateName(request.params("name")));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Get scout age
            get("/scout/:id/age",  (request, response) -> {
            	try {
            		Scout scout = new Scout(Integer.parseInt(request.params("id")), sql2o);
            		return scout.queryAge();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Update scout age
            put("/scout/:id/age/:age", (request, response) -> {
            	try {
            		Scout scout = new Scout(Integer.parseInt(request.params("id")), sql2o);
            		scout.updateAge(new ScoutMapper(sql2o).validateAge(Integer.parseInt(request.params("age"))));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            //Get leader info
            get("/leader/:id/info",  (request, response) -> {
            	return "Not Implemented";
            });
            
            //Get leader name
            get("/leader/:id/name",  (request, response) -> { 
            	try { 
            		Leader leader = new Leader(Integer.parseInt(request.params("id")), sql2o);
            		return leader.queryName();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Update leader name
            put("/leader/:id/name/:name",  (request, response) -> {
            	try {
            		Leader leader = new Leader(Integer.parseInt(request.params("id")), sql2o);
            		leader.updateName(new LeaderMapper(sql2o).validateName(request.params("name")));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Get leader email
            get("/leader/:id/email", (request, response) -> {
            	try {
            		Leader leader = new Leader(Integer.parseInt(request.params("id")), sql2o);
            		return leader.queryEmail();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Update leader email
            put("/leader/:id/email/:email", (request, response) -> {
            	try {
            		Leader leader = new Leader(Integer.parseInt(request.params("id")), sql2o);
            		leader.updateEmail(new LeaderMapper(sql2o).validateEmail(request.params("email")));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            //Get leader troop
            get("/leader/:id/troop",  (request, response) -> {
            	try {
            		Leader leader = new Leader(Integer.parseInt(request.params("id")), sql2o);
            		return leader.queryTroop();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });            

            //Update leader troop
            put("/leader/:id/troop/:troop", (request, response) -> {
              	try {
              		Leader leader = new Leader(Integer.parseInt(request.params("id")), sql2o);
              		leader.updateTroop(request.params("troop"));
              		return "";
              	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
			/*//Get leader password salt - Not Necessary: Client -> Hash Plaintext -> https -> Server add Salt and Hash Again
			get("/leader/:id/salt", (request, response) -> {
               	try {
               		Leader leader = new Leader(Integer.parseInt(request.params("id")), sql2o);
               		return leader.querySalt();
               	} catch (Exception e) {
            		return handle(response, e);
            	}
			});*/

            
			//Update leader password hash
			put("/leader/:id/pwd/:pwd", (request, response) -> {
				try {
					Leader leader = new Leader(Integer.parseInt(request.params("id")), sql2o);
					leader.updatePwd(new LeaderMapper(sql2o).validatePwd(request.params("pwd")));
					return "";
				} catch (Exception e) {
            		return handle(response, e);
            	}
			});



            /* TROOP API */
            
            //Create new troop
            post("/troop/new", (request, response) -> {
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
            delete("/troop/:id/destroy", (request, response) -> {
            	return "Not Implemented";
            });
            
            //Get troop information
            get("/troop/:id/info", (request, response) -> {
            	return "Not Implemented";
            });
            
            //Get troop name
            get("/troop/:id/name",  (request, response) -> {
            	try {
            		Troop troop = new Troop(Integer.parseInt(request.params("id")), sql2o);
            		return troop.queryName();
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Update troop name
            put("/troop/:id/name/:name", (request, response) -> {
            	try {
            		Troop troop = new Troop(Integer.parseInt(request.params("id")), sql2o);
            		troop.updateName(new TroopMapper(sql2o).validateName(request.params("name")));
            		return "";
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });

            //Get troop scout list
            get("/troop/:id/scouts",  (request, response) -> {
            	try {
            		Troop troop = new Troop(Integer.parseInt(request.params("id")), sql2o);
            		Gson gson = new Gson();
            		return gson.toJson(troop.queryScouts());
            	} catch (Exception e) {
            		return handle(response, e);
            	}
            });
            
            //Update troop scout list
            post("troop/:id/scouts", (request, response) -> {
            	return "Not Implemented";
            });
            
            //Add scout to troop list
            put("troop/:id/scouts/add", (request, response) ->{
            	return "Not Implemented";
            });
            
            //Remove scout from troop list
            delete("troop/:id/scouts/destroy", (request, response) -> {
            	return "Not Implemented";
            });
            
            //Get troop leaders
            get("troop/:id/leaders", (request, response) -> {
            	try {
            		Troop troop = new Troop(Integer.parseInt(request.params("id")), sql2o);
            		Gson gson = new Gson();
            		response.body(gson.toJson(troop.queryLeaders()));
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
            put("troop/:id/leaders/add", (request, response) ->{
            	return "Not Implemented";
            });
            
            //Remove leader from troop list
            delete("troop/:id/leaders/destroy", (request, response) -> {
            	return "Not Implemented";
            });
            
           	
    		/* TOKEN API */
    		
            /*//get Token ?Need database of Tokens?
            get("/token/new", (request, response)-> {
                return "Not Implemented";
            });
            
            //renew token
            get("/token/renew", (request, response)->{
            	return "Not Implemented";
            });
        
            //destroy token
            delete("/token/destroy", (request, response) -> {
            	return "Not Implemented";
            });*/
            
        }
            
        private static String handle(Response response, Exception e) {
       		 if (e instanceof NoRecordFoundException) {
       			 response.status(HTTP_BAD_REQUEST);
       			 return "Request Data Invalid, No Record Found";
       		 }
       		 else if (e instanceof JsonSyntaxException || e instanceof InvalidJsonDataException || e instanceof NumberFormatException) {
       			response.status(HTTP_BAD_REQUEST);
       			return "Could Not Parse Request";
       		 }
       		 else if(e instanceof Sql2oException) {
       			 e.printStackTrace();
       			 response.status(HTTP_INTERNAL_ERROR);
       			 return "Database Error";
       		 }
       		 
       		 e.printStackTrace();
       		 response.status(HTTP_INTERNAL_ERROR);
       		 return "Internal Error";
       	 }
    }
