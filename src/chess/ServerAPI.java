package chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A utility class for server calls.
 * 
 * @author Evan
 */
public class ServerAPI {//implements Runnable {
	public static int gameId; 
	private static int teamNumber = 1;
	private static String teamSecret = "32c68cae";
	private static String root = "http://www.bencarle.com/chess/";
	
	private static int pollInterval = 1; 
	
	private static byte ourColor;
	private static byte opponentColor;
	
	// data from polling
	public static boolean ready;
	public static boolean gameover = false;
	public static int winner;
	public static float secondsleft;
	public static int lastmovenumber;
	public static String lastMoveString = "";
	public static String message;
	
	public static byte lastMovedPiece;
	/**
	 * [rank, file]
	 */
	public static byte[] lastStartingMove;
	/**
	 * [rank, file]
	 */
	public static byte[] lastEndingMove;
	/**
	 * [startRank, startFile, endRank, endFile]
	 */
	public static byte[] lastMoveBytes;

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
	 * Sets which color we are which determines our perspective and
	 * records the opponents color as well.
	 * 
	 * @param color - the color we are for this game
	 */
	public static void setOurColor(byte color) {
		ourColor = color;
		
		if(color == Piece.WHITE)
			opponentColor = Piece.BLACK;
		else
			opponentColor = Piece.WHITE;
	}
	
	public static byte getOurColor() {
		return ourColor;
	}
	
	public static byte getOppontentColor() {
		return opponentColor;
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
	
	/**
	 * Polls the server to check the status and see if the opponent made a move yet or not,
	 * storing all the information into the member variables.
	 */
	public static void poll() {
		try {
			String url = root + "poll/" + gameId + "/" + teamNumber + "/" + teamSecret;
			String response = readURL(url);

			String[] components = response.substring(1, response.length() - 1).replace("\"", "").replace(" ", "").split(",");
			
			for(String val : components) {
				String[] pair = val.split(":");
				if(pair[0].equals("ready"))
					ready = pair[1].equals("true");
				else if(pair[0].equals("secondsleft"))
					secondsleft = Float.parseFloat(pair[1]);
				else if(pair[0].equals("lastmovenumber"))
					lastmovenumber = Integer.parseInt(pair[1]);
				else if(pair[0].equals("lastmove")) {
					lastMoveString = pair.length > 1 ? pair[1] : "";
					if(lastMoveString == "")
						continue;
					lastMovedPiece = Piece.getNumericType(lastMoveString.charAt(0));
					lastStartingMove = new byte[] { 
							ChessBoard.getRankFromDisplay(lastMoveString.charAt(2)),
							ChessBoard.getFile(lastMoveString.charAt(1))
					};
					lastEndingMove = new byte[] { 
							ChessBoard.getRankFromDisplay(lastMoveString.charAt(4)),
							ChessBoard.getFile(lastMoveString.charAt(3))
					};
					if(lastMoveString.length() > 5) {
						byte promotionType = Piece.getNumericType(lastMoveString.charAt(5));
						lastMoveBytes = new byte[] { lastStartingMove[0], lastStartingMove[1],
								lastEndingMove[0], lastEndingMove[1], promotionType };
					}
					else {
						lastMoveBytes = new byte[] { lastStartingMove[0], lastStartingMove[1],
							lastEndingMove[0], lastEndingMove[1] };
					}
				}
				else if(pair[0].equals("gameover"))
					gameover = pair[1].equals("true");
				else if(pair[0].equals("winner"))
					winner = Integer.parseInt(pair[1]);
			}
			
		} catch(IOException e) {
			System.out.println(e);
			//System.out.println("Maybe a bad team number/secret combo?");
		}
	}
	
	/**
	 * Purely for offline use as when we play offline there is no polling so we
	 * need a way to update the last move values since move generation relies on this.
	 * 
	 * This is exactly the reason why en passant was causing problems, this only updates the last 
	 * committed move and doesn't take into consideration the last move in searches...as a result we
	 * cannot rely on the server's last move we got from polling to check for en passant.
	 * 
	 * @param s string - move string to parse
	 */
	public static void setLastMoveString(String s) {
		lastMoveString = s;
		lastStartingMove = new byte[] { 
				ChessBoard.getRankFromDisplay(s.charAt(2)),
				ChessBoard.getFile(s.charAt(1))
		};
		lastEndingMove = new byte[] { 
				ChessBoard.getRankFromDisplay(s.charAt(4)),
				ChessBoard.getFile(s.charAt(3))
		};
		lastMoveBytes = new byte[] { lastStartingMove[0], lastStartingMove[1], lastEndingMove[0], lastEndingMove[1] };
	}
	
	/**
	 * Sends our move to the server.
	 * 
	 * @param moveString - string move string formatted according to the
	 * @return true if successful, false otherwise
	 */
	public static boolean move(String moveString) {
		String url = root + "move/" + gameId + "/" + teamNumber + "/" + teamSecret + "/" + moveString;
		try {
			String response = readURL(url);
			System.out.println(response);

			String[] components = response.substring(1, response.length() - 1).replace("\"", "").replace(" ", "").split(",");
			
			for(String val : components) {
				String[] pair = val.split(":");
				if(pair[0].equals("message"))
					message = pair.length > 1 ? pair[1] : "";
				if(pair[0].equals("result"))
					return pair[1].equals("true");
				else if(pair[0].equals("gameover"))
					gameover = pair[1].equals("true");
				else if(pair[0].equals("winner"))
					winner = Integer.parseInt(pair[1]);
			}
		} catch(IOException e) {
			System.out.println(e);
			//System.out.println("Maybe a bad team number/secret combo?");
		}
		return false;
	}
	
	// movestring functions
	/*public static byte getLastMovedPiece() {
		if(lastmove.length() > 0)
			return Piece.getNumericType(lastmove.charAt(0));
		else 
			return -1;
	}*/
	
	/**
	 * Gets the last move received from the server in the form of [rank, file]
	 * where 0 <= rank <= 7 and 0 <= file <= 7 
	 * 
	 * @return [rank, file] of last move from server
	 */
	/*public static byte[] getLastMove() {
		if(lastMoveString.length() > 4) {
			byte rank = Byte.parseByte(lastMoveString.charAt(2) + ""),
				 rankEnd = Byte.parseByte(lastMoveString.charAt(4) + "");
			return new byte[] { ChessBoard.getDisplayRank((byte) (rank - 1)),
					ChessBoard.getFile(lastMoveString.charAt(1)),
					ChessBoard.getDisplayRank((byte) (rankEnd - 1)),
					ChessBoard.getFile(lastMoveString.charAt(3))};
		}
		else
			return null;
	}*/
	
	/*public static byte[] getLastMoveStartPos() {
		if(lastmove.length() > 2) {
			byte rank = Byte.parseByte(lastmove.charAt(2) + "");
				 //file;
			return new byte[] { ChessBoard.getDisplayRank((byte) (rank - 1)),
				ChessBoard.getFile(lastmove.charAt(1))};
		}
		else
			return null;
	}
	
	public static byte[] getLastMoveEndPos() {
		if(lastmove.length() > 4)
			return new byte[] { ChessBoard.getDisplayRank(Byte.parseByte(lastmove.charAt(4) + "")),
				ChessBoard.getFile(lastmove.charAt(3)) };
		else
			return null;
	}*/
	
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
	
	public static int getPollInterval() {
		return pollInterval;
	}

	// Loop in a new thread to continuously poll the server looking for new moves
	/*@Override
	public void run() {
		// TODO Auto-generated method stub
		poll();
		while(ready) {
			int prevLastMoveNum = lastmovenumber;
			poll();
			// the move has been updated since the last poll
			if(prevLastMoveNum != lastmovenumber) {
				byte[] start = getLastMoveStartPos();
				byte[] end = getLastMoveEndPos();
				board.move(start[0], start[1], end[0], end[1]);
				// will probably need to do something to interrupt the current search?
			}
			try {
				Thread.sleep((long) pollInterval * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/
}
