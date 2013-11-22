package chess;

import java.util.Map;

public class program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Board board = new Board();
		
		byte[] start = {Board.R1, Board.B};
		byte[] end = {Board.R3, Board.C};
		System.out.println(board.move(start, end));
		System.out.println(board);
		
		
		// set this up at
		// http://www.bencarle.com/chess/startgame
		ServerAPI.gameId = 350;
		
		/*
		// test polling server
		Map<String, String> pollData = ServerAPI.poll();
		for(String key : pollData.keySet()) {
			System.out.println(key + ": " + pollData.get(key));
		}
		
		// test moving
		ServerAPI.setTeam1();
		
		String moveString = "Pd3d4";
		Map<String, String> moveData = ServerAPI.move(moveString);
		for(String key : moveData.keySet()) {
			System.out.println(key + ": " + moveData.get(key));
		}
		*/
	}

}
