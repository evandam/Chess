package chess;

import java.util.List;
import java.util.Map;

/* Code base for project */
public class program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		/*
		Piece p = new Piece(Piece.KING, Piece.BLACK);	// parameter passing testing, if we set
		System.out.println(p.toString());
		pointerTest(p);
		System.out.println(p.toString()); */
		
		int gameId;
		
		// we will pass the game id as a command line parameter
		if(args.length < 1) {
			return;
		}
		else {
			gameId = Integer.parseInt(args[1]);
		}
		
		ChessBoard board = new ChessBoard();
		
		//ServerAPI.setOurTeamDetails(007, "bond");		// hard code our team details here
		ServerAPI.setGameId(gameId);
		ServerAPI.setPollInterval(1);
		
		ServerAPI.poll();
		
		// if after polling, we have the ready and no moves have been made yet, then we know we are white
		// since chess rules state that white always moves first
		if(ServerAPI.ready == true && ServerAPI.lastmovenumber == 0 && ServerAPI.lastmove == null) {
			board.setOurColor(Piece.WHITE);
		}
		// otherwise we know we are waiting for the first move which means that we are black
		else if(ServerAPI.ready == false && ServerAPI.lastmovenumber == 0) {
			board.setOurColor(Piece.BLACK);
		}
		// if we have the ready and there is a last move, then the other team has already submitted a move
		// for which we have to record in our internal representation and we know we are black
		else if(ServerAPI.ready == true && ServerAPI.lastmove != null) {
			board.setOurColor(Piece.BLACK);
			// TODO - record that move
		}
		
		// TODO - start game play
	}
	
	
	private static void pointerTest(Piece p) {
		/*p = new Piece(p.getType(), p.getColor());	// does not change the original object
		p.setType(Piece.QUEEN);*/
		
		//p.setType(Piece.QUEEN);					// changes the original object
	}
	
	
	private void TestStuff() {
		//ServerAPI.setTeam1();
		ServerAPI.poll();
		
		ChessBoard board = new ChessBoard();
		
		// moves for white bishop
		byte[] start = new byte[]{ChessBoard.R2, ChessBoard.B};
		byte[] end = new byte[]{ChessBoard.R5, ChessBoard.B};
		// move king to see what positions are open
		board.move(start, end);
		board.move(new byte[] {ChessBoard.R7, ChessBoard.C}, new byte[] {ChessBoard.R5, ChessBoard.C});
		
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
		//ServerAPI.setTeam1();
		
		String moveString = "Pe5d6";
		Map<String, String> moveData = ServerAPI.move(moveString);
		if(moveData != null) {
			for(String key : moveData.keySet()) {
				System.out.println(key + ": " + moveData.get(key));
			}
		}
	}

}
