package chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;


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
		
		
		/* ----------------------------- Interactive game play offline ---------------------------------- */
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    String s = "";
		
		boolean startSearch = false;
		ServerAPI.setOurColor(Piece.WHITE);
		
		if(!startSearch) {
			ServerAPI.setOurColor(Piece.BLACK);
			//try { s = bufferRead.readLine(); } catch (IOException e1) { return; }
			//startSearch = true;
		}
		
		while(!s.equals("q") || s.equals("Q")) {
			
			if(startSearch) {
				startSearch = false;
				
				Date start = new Date();
				byte[] move = SearchUtils.AlphaBetaSearch(board);
				
				// need to actually record the move in the running board representation
				board.move(move[0], move[1], move[2], move[3]);
				Date end = new Date();
				System.out.println("Done: duration: " + (end.getTime() - start.getTime()) / 1000.0 + " seconds");
				String moveStr = Piece.getCharType(move[4]) + "" + ChessBoard.getFile(move[1]) + "" +
						ChessBoard.getDisplayRank(move[0]) + "" + ChessBoard.getFile(move[3]) + "" + ChessBoard.getDisplayRank(move[2]);
				System.out.println(moveStr);
			}
			else {
				try { System.out.print("Enter move: "); s = bufferRead.readLine(); } catch (IOException e1) { return; }
				byte startRank, startFile, endRank, endFile;
				startRank = (byte) (Byte.parseByte(s.charAt(2) + "") - 1);
				startFile = ChessBoard.getFile(s.charAt(1));
				endRank = (byte) (Byte.parseByte(s.charAt(4) + "") - 1);
				endFile = ChessBoard.getFile(s.charAt(3));
				board.move(startRank, startFile, endRank, endFile);
				startSearch = true;
			}
			//System.out.println(board.toString());
		}
		/* ---------------------------------------------------------------------------------------------- */
		
		
		
		
		
		//ServerAPI.setOurTeamDetails(007, "bond");		// hard code our team details here
		ServerAPI.setGameId(gameId);
		ServerAPI.setPollInterval(1);
		
		ServerAPI.poll();
		
		// if after polling, we have the ready and no moves have been made yet, then we know we are white
		// since chess rules state that white always moves first
		if(ServerAPI.ready == true && ServerAPI.lastmovenumber == 0 && ServerAPI.lastmove == null) {
			ServerAPI.setOurColor(Piece.WHITE);
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
			byte[] lastMove = ServerAPI.getLastMove();
			board.move(lastMove[0], lastMove[1], lastMove[2], lastMove[3]);
		}
		
		// start the game
		while(ServerAPI.gameover) {
			ServerAPI.poll();
			if(!ServerAPI.ready) {
				Date start = new Date();
				byte[] move = SearchUtils.AlphaBetaSearch(board);
				
				// need to actually record the move in the running board representation
				board.move(move[0], move[1], move[2], move[3]);
				Date end = new Date();
				System.out.println("Done: duration: " + (end.getTime() - start.getTime()) / 1000.0 + " seconds");
				String moveStr = Piece.getCharType(move[4]) + "" + ChessBoard.getFile(move[1]) + "" +
						ChessBoard.getDisplayRank(move[0]) + "" + ChessBoard.getFile(move[3]) + "" + ChessBoard.getDisplayRank(move[2]);
				System.out.println(moveStr);
				
				// send the move to the server
				if(!ServerAPI.move(moveStr)) {
					System.out.println(ServerAPI.message);
					return;
				}
			}
			else {
				try {
					//System.out.println("Waiting for opponent...");
					Thread.sleep((long) ServerAPI.getPollInterval() * 1000);
				} catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		} // end while game is not over
	}
	
}
