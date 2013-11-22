package chess;

import java.util.Map;

public class program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// set this up at
		// http://www.bencarle.com/chess/startgame
		ServerAPI.gameId = 350;
		
		// test polling server
		/*Map<String, String> pollData = ServerAPI.poll();
		for(String key : pollData.keySet()) {
			System.out.println(key + ": " + pollData.get(key));
		}*/
		
		// test moving
		ServerAPI.setTeam1();
		
		String moveString = "Pd3d4";
		Map<String, String> moveData = ServerAPI.move(moveString);
		for(String key : moveData.keySet()) {
			System.out.println(key + ": " + moveData.get(key));
		}
	}

}
