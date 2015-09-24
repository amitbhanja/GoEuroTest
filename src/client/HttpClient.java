package client;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
/**
 * @author Amit Bhanja
 * The application takes a single string as an argument. 
 * The string should be a name of the place and the application would fetch the details about the place
 * from a HTTP server and populate the entries in a csv file.
 * The filename format is "Query@"+Current time in milliseconds+"Query name passed as argument to the application"
 */

public class HttpClient {
	private final static String url = "http://api.goeuro.com/api/v2/position/suggest/en/";
	private final static String COMMA_DELIMETER = ",";
	private final static String HEADER = "_id,name,type,latitude,longitude";
	private final static String NEWLINE = "\n";
	private static String filename;
	/**
	 * @param args
	 * @author Amit Bhanja
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length != 1){
			System.err.println(" Please provide one string as argument");
			return;
		}
		else{
			//System.out.println(" The argument provided "+args[0]);
			URL goeuro = null;
			try {
				goeuro = new URL(url+args[0]);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				System.err.println(" The url address "+url+args[0]+" is not formed correctly");
				e.printStackTrace();
			}
			
			HttpURLConnection httpconnection = null;
			if(goeuro != null){
			try {
				httpconnection = (HttpURLConnection) goeuro.openConnection();
				/*Check if a http connection has been established*/
				if(httpconnection != null){
					httpconnection.setRequestMethod("GET");
					httpconnection.setRequestProperty("accept", "application/json");
					int response = httpconnection.getResponseCode();
					//System.out.println(" Http response "+response);
					/*Check if the response is HTTP OK*/
					if(response == 200){
						BufferedReader input = new BufferedReader(new InputStreamReader(httpconnection.getInputStream()));
						String jsonreceived = input.readLine();
						//System.out.println("Received json "+jsonreceived);
						JSONArray json = null;
						try {
							json = new JSONArray(jsonreceived); // Create a JSON Array Object
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(json != null){
							/*Check if there are no json objects and populate the entries in a csv file*/
							if(json.length() > 0){
								filename = "Query @"+String.valueOf(System.currentTimeMillis())+" "+args[0]+".csv";
								FileWriter filewriter = new FileWriter(filename);
								filewriter.append(HEADER.toString());
								for(int i = 0;i < json.length();i++){
									JSONObject temp = (JSONObject)json.get(i);
									filewriter.append(NEWLINE);
									filewriter.append(String.valueOf(temp.getInt("_id")));
									filewriter.append(COMMA_DELIMETER);
									filewriter.append(temp.getString("name"));
									filewriter.append(COMMA_DELIMETER);
									filewriter.append(temp.getString("type"));
									filewriter.append(COMMA_DELIMETER);
									JSONObject geoposition = temp.getJSONObject("geo_position");
									if(geoposition != null){
										filewriter.append(String.valueOf(geoposition.getDouble("latitude")));
										filewriter.append(COMMA_DELIMETER);
										filewriter.append(String.valueOf(geoposition.getDouble("longitude")));
									}
								}
								filewriter.close();
							}
							input.close();// Closing the input stream after the data is read
						}
						
						//System.out.println(json.length());
					}
					else{
						System.err.println(" Received a HTTP error "+response);
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("Could not connect to "+url+args[0]);
				e.printStackTrace();
			}
			catch(JSONException e){
				e.printStackTrace();
			}
			finally{
				if(httpconnection != null)
					httpconnection.disconnect();
			}
			}
			}
				
		}
			
	}


