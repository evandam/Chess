package chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for server calls
 * @author Evan
 *
 */
public class ServerAPI {
	public static int gameId;	// need to set this when game is started 
	private static int teamNumber = 1;	// temporary
	private static String teamSecret = "32c68cae";	// temporary
	private static String root = "http://www.bencarle.com/chess/";
	
	public static Map<String, String> poll() {
		String url = root + "poll/" + gameId + "/" + teamNumber + "/" + teamSecret;
		String response = readURL(url);
		System.out.println(response);
		Map<String, String> map = new HashMap<String, String>();
		// easy enough to just use regexes to get the values from json
		String pat = "\\{\"ready\": (\\w+), \"secondsleft\": ([\\d\\.]+), \"lastmovenumber\": (\\d+), \"lastmove\": \"(\\.*?)\"\\}";
		Pattern r = Pattern.compile(pat);
		Matcher m = r.matcher(response);
		if(m.find()) {
			map.put("ready", m.group(1));
			map.put("secondsleft", m.group(2));
			map.put("lastmovenumber", m.group(3));
			map.put("lastmove", m.group(4));
			return map;
		}
		else {
			System.out.println("I'm bad at regexes");
		}
		return null;
	}
	
	public static void move(String moveString) {
		String url = root + "move/" + gameId + "/" + teamNumber + "/" + teamSecret + "/" + moveString;
		readURL(url);
	}
	
	private static String readURL(String url) {
		String response = "";
		try {
			URL apiCall = new URL(url);
			BufferedReader in = new BufferedReader(new InputStreamReader(apiCall.openStream()));
			
			String line;
			while((line = in.readLine()) != null) {
				response += line;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
}
