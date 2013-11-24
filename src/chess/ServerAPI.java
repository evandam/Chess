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
	
	// data from polling
	public static boolean ready;
	public static float secondsleft;
	public static int lastmovenumber;
	public static String lastmove;
	
	
	public static void setTeam1() {
		teamNumber = 1;
		teamSecret = "32c68cae";
	}
	
	public static void setTeam2() {
		teamNumber = 2;
		teamSecret = "1a77594c";
	}
	
	public static void poll() {
		try {
			String url = root + "poll/" + gameId + "/" + teamNumber + "/" + teamSecret;
			String response = readURL(url);
			// easy enough to just use regexes to get the values from json
			String pat = "\\{\"ready\": (\\w+), \"secondsleft\": ([\\d\\.]+), \"lastmovenumber\": (\\d+)(, \"lastmove\": \"(\\.*?)\")?\\}";
			Pattern r = Pattern.compile(pat);
			Matcher m = r.matcher(response);
			if(m.find()) {
				ready = m.group(1).equals("true");
				secondsleft = Float.parseFloat(m.group(2));
				lastmovenumber = Integer.parseInt(m.group(3));
				lastmove = m.group(5);
			}
			else {
				System.out.println("I'm bad at regexes");
			}
		} catch(IOException e) {
			System.out.println("Maybe a bad team number/secret combo?");
		}
	}
	
	public static Map<String, String> move(String moveString) {
		String url = root + "move/" + gameId + "/" + teamNumber + "/" + teamSecret + "/" + moveString;
		try {
			String response = readURL(url);
			System.out.println(response);
			Map<String, String> map = new HashMap<String, String>();
			// Regex to get the vars out of JSON
			String pat = "\\{\"message\": \"(\\.*?)\", \"result\": (\\w+)\\}";
			Pattern r = Pattern.compile(pat);
			Matcher m = r.matcher(response);
			if(m.find()) {
				map.put("message", m.group(1));
				map.put("result", m.group(2));
				return map;
			}
			else {
				System.out.println("I'm bad at regexes");
			}
		} catch(IOException e) {
			System.out.println("Maybe a bad team number/secret combo?");
		}
		return null;
	}
	
	// movestring functions
	public static byte getLastMovedPiece() {
		return Piece.getType(lastmove.charAt(0));		
	}
	
	public static byte[] getLastMoveStartPos() {
		return new byte[] { Board.getFile(lastmove.charAt(2)), Board.getRank((byte) lastmove.charAt(1)) };
	}
	
	public static byte[] getLastEndPos() {
		return new byte[] { Board.getFile(lastmove.charAt(4)), Board.getRank((byte) lastmove.charAt(3)) };
	}
	
	private static String readURL(String url) throws IOException {
		String response = "";
		try {
			URL apiCall = new URL(url);
			BufferedReader in = new BufferedReader(new InputStreamReader(apiCall.openStream()));
			
			String line;
			while((line = in.readLine()) != null) {
				response += line;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} 
		return response;
	}
}
