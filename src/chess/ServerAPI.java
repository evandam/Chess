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
	public static int gameId;						// need to set this when game is started 
	private static int teamNumber = 1;				// temporary
	private static String teamSecret = "32c68cae";	// temporary
	private static String root = "http://www.bencarle.com/chess/";
	
	private static int pollInterval = 1; 
	
	// data from polling
	public static boolean ready;
	public static float secondsleft;
	public static int lastmovenumber;
	public static String lastmove = "Pd7d5";	// default to test en passant
	
	
	/**
	 * Method to set our team number and secret once it is given to us,
	 * otherwise it defaults to the given team 1.
	 * 
	 * @param num - team number
	 * @param secret - team secret
	 */
	public static void setOurTeamDetails(int num, String secret) {
		teamNumber = num;
		teamSecret = secret;
	}
	
	/**
	 * Sets the game id from command line parameters.
	 * 
	 * @param id - game id
	 */
	public static void setGameId(int id) {
		gameId = id;
	}
	
	/**
	 * Changes the poll interval should it be the case that we need to change it on the fly.
	 * 
	 * @param interval - new polling interval
	 */
	public static void setPollInterval(int interval) {
		pollInterval = interval;
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
				secondsleft = Float.parseFloat(m.group(2));		// TODO - when its game over we get the following returned to us
				lastmovenumber = Integer.parseInt(m.group(3));	// {"secondsleft": -47.027484, "lastmovenumber": 2, "lastmove": "Pa7a6", "winner": 1, "gameover": true, "ready": false}
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
				map.put("message", m.group(1));	// TODO - have to check for when the game is over which will be returned after a move
				map.put("result", m.group(2));	// so instead of {"message": "", "result": true}
				return map;						// we get a {"gameover": true, "winner": 1} if it is the end of the game
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
		if(lastmove.length() > 0)
			return Piece.getType(lastmove.charAt(0));
		else 
			return -1;
	}
	
	public static byte[] getLastMoveStartPos() {
		if(lastmove.length() > 2)
			return new byte[] { ChessBoard.getRank(Byte.parseByte(lastmove.charAt(2) + "")), ChessBoard.getFile(lastmove.charAt(1))  };
		else
			return null;
	}
	
	public static byte[] getLastEndPos() {
		if(lastmove.length() > 4)
			return new byte[] { ChessBoard.getRank(Byte.parseByte(lastmove.charAt(4) + "")), ChessBoard.getFile(lastmove.charAt(3)) };
		else
			return null;
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
