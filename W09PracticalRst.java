
import java.util.*;
import java.io.*;

import javax.json.*;

public class W09PracticalRst {
	
	private static String urlStr = "http://api.duckduckgo.com/?q=";
	/*
	* this is the main method that runs everything 
	*/ 
	public static void main(String[] args) throws Exception
	{
		
		W09PracticalRst n = new W09PracticalRst();
		String jsonString;
		if(args.length >= 1)
		{
			jsonString = n.getJsonString(n.createurl(args));
			n.run(jsonString);
		}
		else
		{
			System.out.println("No argument given!");
			System.exit(0);
		}
	}
	
	// This creates the url given the word or phrase that the user is interested in
	public String createurl(String[] arguments)
	{
		String phrase ="";
		for(int i = 0; i < arguments.length; i++) // used to concatenate the phrase/word properly 
		{
			if(i != arguments.length - 1) 
				phrase += arguments[i] + "+";
			else
				phrase += arguments[i];
		}
		// System.out.println(urlStr + phrase + "&format=json");
		return urlStr + phrase + "&format=json";
	}	
	
	// This is used to get the json string using the REST client. 
	public String getJsonString(String url)
	{
		RESTClient rc = new RESTClient();
		String jsonString = "";
		try
		{
			jsonString = rc.makeRESTCall(url);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("Failed");
			System.exit(0);
		}
		return jsonString;
		
	}
	
	//this method is what handles the main printing of the json file 
	public void run(String fileName) throws Exception
	{
		try
		{
		JsonReader reader = Json.createReader(new FileReader(fileName));
		JsonObject mainObject = reader.readObject();
		if(!mainObject.containsKey("Heading"))
			return;
		System.out.println(mainObject.get("Heading").toString() + " can refer to:" ); // used to get heading and print it
		JsonArray relatedTopics = mainObject.getJsonArray("RelatedTopics"); // used to get an array of related topics
		if(relatedTopics.size() > 0)
			for(int i = 0; i< relatedTopics.size(); i++) // handles all the printing in proper format
			{
				JsonObject obj = relatedTopics.getJsonObject(i);
				if(obj.containsKey("Name"))
				{
					JsonArray topics = obj.getJsonArray("Topics");
					if(topics.size() > 0)
						System.out.println("  * Category: " + obj.getString("Name"));
					for(int j = 0; j < topics.size(); j++)
					{
						JsonObject topicObj = topics.getJsonObject(j);
						if(topicObj.containsKey("Text"))
							System.out.println("    - " + topicObj.getString("Text"));
					}
				}
				else if(obj.containsKey("Text"))
					System.out.println("  - " + obj.get("Text").toString());
			}
			
		}catch(Exception e)
		{
			// e.printStackTrace();
			System.out.println("Not a valid JSON string!");
			System.exit(0);
		}
	}

}
