package chess;

import java.util.List;
import java.util.Map;

public class program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Board board = new Board();
		
		// moves for white king
		byte[] start = new byte[]{Board.R1, Board.D};
		byte[] end = new byte[]{Board.R3, Board.D};
		// move king to see what positions are open
		board.move(start, end);
		List<byte[]> moves = board.get(end[0], end[1]).getPossibleMoves(board, end);
		System.out.println("possible moves: ");
		for(byte[] newPos : moves) {
			System.out.println(newPos[0] + ", " + newPos[1]);
		}
		
		System.out.println(board);
		
		/*byte[] start = {Board.R1, Board.B};
		byte[] end = {Board.R3, Board.C};
		System.out.println(board.move(start, end));
		System.out.println(board);*/
		
		
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
