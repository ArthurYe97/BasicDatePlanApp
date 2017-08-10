import java.io.StringReader;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.jsoup.Jsoup;


public class W09PracticalGoogleMaps{
	
	private static String restaurantUrlStr = "";
	private static String movieUrlStr = "";
	private static String directionsURL = "";
	private static String desiredMovieTheater = "";
	private static String desiredRestaurant = "";
	private static String googlePlacesAPIKey = "&key=AIzaSyDBRpv3J1wVBuRVv_Y026LR0Q3Mj62kvMc";
	private static String googleAPIURL1 = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
	private static String googleAPIQuery = "query=";
	private static String googleDirectionsAPIURL = "https://maps.googleapis.com/maps/api/directions/json?origin=place_id:";
	private static String googleDirectionsAPIKey = "&key=AIzaSyCqk9PCi_O8aSNPfX9WYsmZ-onyctgnq_k";
	private String travelMode = "";
	
	/*
	* This is the main method that runs everything properly
	*/
	public static void main(String[] args) throws Exception
	{
		W09PracticalGoogleMaps n = new W09PracticalGoogleMaps();
		n.createUrlStr();
		String restaurantJsonString = n.getJsonString(restaurantUrlStr);
		String movieJsonString = n.getJsonString(movieUrlStr);
		n.run(restaurantJsonString);
		n.run(movieJsonString);
		System.out.println("desiredRestaurant id: " + desiredRestaurant);
		System.out.println("desiredMovieTheater id: " + desiredMovieTheater);
		n.createDirectionsURL();
		System.out.println(directionsURL);
		String directionsJsonStr = n.getJsonString(directionsURL);
		System.out.println(directionsJsonStr);
		n.createDrivingDirections(directionsJsonStr);
	}
	
	//Creates the url for both restaurants and movies after asking the user 
	// for information about the location they would like. 
	public void createUrlStr() 
	{
		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter a local vicinity where you would like to dine and watch a movie!");
		String topic = sc.nextLine();
		String[] parts = topic.split(" ");
		String input = "";
		for(int i = 0; i < parts.length; i++) // for loop is used to create the input in proper format for the url 
		{
			if(i < parts.length - 1)
				input += parts[i] + "+";
			else
				input += parts[i];
		}
		restaurantUrlStr = googleAPIURL1 + googleAPIQuery + "restaurants+near+" + input + googlePlacesAPIKey;
		movieUrlStr = googleAPIURL1 + googleAPIQuery + "movies+near+" + input + googlePlacesAPIKey;
	}
	
	// this method creates the driving instructions from using json file.
	public void createDrivingDirections(String directionsJsonStr)
	{
		try
		{
			System.out.println("Your mode of travel is: " + travelMode); // asking for travel mode
			JsonReader reader = Json.createReader(new StringReader(directionsJsonStr));
			JsonObject mainObject = reader.readObject();
			JsonArray routes = mainObject.getJsonArray("routes");
			for(int i = 0; i < routes.size(); i++) // used to access legs
			{
				//System.out.println("Went through1");
				JsonArray legs = routes.getJsonObject(i).getJsonArray("legs"); 
				for(int k = 0; k < legs.size(); k++) //used to access steps
				{
					//System.out.println("Went through2");
					JsonArray steps = legs.getJsonObject(k).getJsonArray("steps");
					for(int j = 0; j < steps.size(); j++) // used to get instructions for the user 
					{
						String step = steps.getJsonObject(j).getString("html_instructions");
						step = Jsoup.parse(step).text();
						System.out.println("Step " + (j + 1) + ": " + step);
						String distance = steps.getJsonObject(j).getJsonObject("distance").get("text").toString();
						System.out.println("\tFor a distance of " + distance);
					}
				}
		}
		}catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("Not a valid JSON String!");
			System.exit(0);
		}
	}
	
	// this is used to create the url for getting the directions between two places. 
	// it uses the place ids because they are more specific 
	public void createDirectionsURL()
	{
		Scanner sc = new Scanner(System.in);
		System.out.println("Please specify how you would like to travel among these method: Driving, Bicycling, Walking");
		travelMode = sc.nextLine();
		directionsURL = googleDirectionsAPIURL + desiredRestaurant + "&destination=place_id:" + desiredMovieTheater + "&mode=" + travelMode+  googleDirectionsAPIKey;
	}
	
	// as used by the other classes, this will only get the json string using
	// the rest client
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
	
	// this method is used to get the movies and the restaurants near the given location
	// it will also ask the user for which restaurant and movie they would like 
	public void run(String jsonString) throws Exception
	{
		try
		{
			JsonReader reader = Json.createReader(new StringReader(jsonString));
			JsonObject mainObject = reader.readObject();
			JsonArray results = mainObject.getJsonArray("results");
			int[] value = new int[results.size()];
			for(int i = 0; i < results.size(); i++) // handles the printing of the results 
			{
				JsonObject obj = results.getJsonObject(i);
				if(obj.containsKey("name"))
					System.out.println("Name: " + obj.getString("name"));
				if(obj.containsKey("formatted_address"))
					System.out.println("\tLocation address: " + obj.getString("formatted_address") );
				if(obj.containsKey("price_level"))
					System.out.println("\tPrice Level: " + obj.getInt("price_level"));
				if(obj.containsKey("rating"))
					System.out.println("\tRating: " + obj.getInt("rating"));
			}
			Scanner sc = new Scanner(System.in);
			if(jsonString.contains("restaurant")) // checks if the json string contains restaurant and if it does you will use this
			{
				System.out.println("Which restaurant would you like? Please enter the name exactly as shown by the results above!");
				String restaurantName = sc.nextLine();
				for(int k = 0; k < results.size(); k++)
					if(restaurantName.equalsIgnoreCase(results.getJsonObject((k)).getString("name")))
					{
							desiredRestaurant = results.getJsonObject(k).getString("place_id");
							return;
					}
				System.out.println("You have not entered a valid restaurant, the program will now end.");
				System.exit(0);
			}
			if(jsonString.contains("movie")) // checks if the json string contains movie and if it does it will use this 
			{
				System.out.println("Which movie theatre would you like? Please enter the name exactly as shown by the results above!");
				String movieName = sc.nextLine();
				for(int k = 0; k < results.size(); k++)
					if(movieName.equalsIgnoreCase(results.getJsonObject((k)).getString("name")))
					{
							desiredMovieTheater = results.getJsonObject(k).getString("place_id");
							return;
					}
				System.out.println("You have not entered a valid movie theatre, the program will now end.");
				System.exit(0);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("Not a valid JSON String!");
			System.exit(0);
		}
	}
	
	

}
