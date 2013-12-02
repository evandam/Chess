package chess;

/*import java.util.List;
import java.util.Map;*/

/* Code base for project */
public class program {
	
	/**
	 * Main entry point for the program, will contain the main game loop. 
	 * 
	 * @param args - team ID
	 */
	public static void main(String[] args) {
		int gameId;
		
		// we will pass the game id as a command line parameter
		if(args.length < 1) {
			return;
		}
		else {
			gameId = Integer.parseInt(args[0]);
		}
		
		ChessBoard board = new ChessBoard();
		
		//ServerAPI.setOurTeamDetails(007, "bond");		// hard code our team details here
		ServerAPI.setGameId(gameId);
		ServerAPI.setPollInterval(1);
		
		ServerAPI.poll();
		
		boolean startSearch = false;
		
		// if after polling, we have the ready and no moves have been made yet, then we know we are white
		// since chess rules state that white always moves first
		if(ServerAPI.ready == true && ServerAPI.lastmovenumber == 0 && ServerAPI.lastmove == null) {
			ServerAPI.setOurColor(Piece.WHITE);
			startSearch = true;
		}
		// otherwise we know we are waiting for the first move which means that we are black
		else if(ServerAPI.ready == false && ServerAPI.lastmovenumber == 0) {
			ServerAPI.setOurColor(Piece.BLACK);
		}
		// if we have the ready and there is a last move, then the other team has already submitted a move
		// for which we have to record in our internal representation and we know we are black
		else if(ServerAPI.ready == true && ServerAPI.lastmove != null) {
			ServerAPI.setOurColor(Piece.BLACK);
			// record that move - TODO - cleaner way of doing this?
			byte[] lastStartPos = ServerAPI.getLastMoveStartPos();
			byte[] lastEndPos = ServerAPI.getLastMoveEndPos();
			board.move(lastStartPos[0], lastStartPos[1], lastEndPos[0], lastEndPos[1]);
			startSearch = true;
		}
		
		if(startSearch) {
			byte[] move = SearchUtils.AlphaBetaSearch(board);
			board.move(move[0], move[1], move[2], move[3]);
			ServerAPI.move(Piece.getCharType(move[4]) + move[0] + move[1] + move[2] + move[3] + "");
		}
		
		while(!ServerAPI.gameover) {
			ServerAPI.poll();
			if(ServerAPI.ready) {
				
			}
			else {
				try {
					Thread.sleep((long) ServerAPI.getPollInterval() * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		// TODO - main game loop
	}
	
	
	private void TestStuff() {
		//ServerAPI.setTeam1();
		ServerAPI.poll();
		
		ChessBoard board = new ChessBoard();
		
		// moves for white bishop
		byte[] start = new byte[]{ChessBoard.R2, ChessBoard.B};
		byte[] end = new byte[]{ChessBoard.R5, ChessBoard.B};
		// move king to see what positions are open
		//board.move(start, end);
		//board.move(new byte[] {ChessBoard.R7, ChessBoard.C}, new byte[] {ChessBoard.R5, ChessBoard.C});
		
//		System.out.println(board);
		
		//List<byte[]> moves = board.get(end[0], end[1]).getPossibleMoves(board, end);
//		System.out.println("possible moves: ");
		
		//for(byte[] newPos : moves) {
//			System.out.println(newPos[0] + ", " + newPos[1]);
		//}
				
		// test en passant
		//board.move(end, new byte[]{2, 2});
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
		/*Map<String, String> moveData = ServerAPI.move(moveString);
		if(moveData != null) {
			for(String key : moveData.keySet()) {
				System.out.println(key + ": " + moveData.get(key));
			}
		}*/
	}

}
