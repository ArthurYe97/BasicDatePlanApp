import java.util.*;
import java.io.*;
import javax.json.*;

public class Describe {
	
	/*
	* This is the main method that runs everything in the program 
	*/ 
	public static void main(String[] args) throws Exception
	{
		Describe w09 = new Describe();
		w09.run(args[0]);
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
