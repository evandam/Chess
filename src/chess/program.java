package chess;

import java.util.List;
import java.util.Map;

public class program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerAPI.setTeam1();
		ServerAPI.poll();
		
		Board board = new Board();
		
		// moves for white bishop
		byte[] start = new byte[]{Board.R2, Board.B};
		byte[] end = new byte[]{Board.R5, Board.B};
		// move king to see what positions are open
		board.move(start, end);
		board.move(new byte[] {Board.R7, Board.C}, new byte[] {Board.R5, Board.C});
		
//		System.out.println(board);
		
		List<byte[]> moves = board.get(end[0], end[1]).getPossibleMoves(board, end);
//		System.out.println("possible moves: ");
		
		for(byte[] newPos : moves) {
//			System.out.println(newPos[0] + ", " + newPos[1]);
		}
				
		// test en passant
		board.move(end, new byte[]{2, 2});
//		System.out.println(board);
		/*byte[] start = {Board.R1, Board.B};
		byte[] end = {Board.R3, Board.C};
		System.out.println(board.move(start, end));
		System.out.println(board);*/
		
		
		// set this up at
		// http://www.bencarle.com/chess/startgame
		ServerAPI.gameId = 352;
		
		// test moving
		ServerAPI.setTeam1();
		
		String moveString = "Pe5d6";
		Map<String, String> moveData = ServerAPI.move(moveString);
		if(moveData != null) {
			for(String key : moveData.keySet()) {
				System.out.println(key + ": " + moveData.get(key));
			}
		}
	}

}
