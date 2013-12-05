package chess;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Class to handle all search functions and return moves.
 * 
 * @author Bryan
 */
public class SearchUtils {
	// use this for in the cutoff test (limit 5 seconds per search?)
	public static long startSearchTime = 0;
	
	// have cutoff here which would be number of ply's?
	private static int maxPly = 2;
	
	//private static byte[] nextMove = new byte[5];
	public static byte[] lastMove = new byte[5];
	
	public static byte[] AlphaBetaSearch(ChessBoard board) {
		startSearchTime = new Date().getTime();
		Tuple<Double, byte[]> v = MaxValue(board, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
		
		// {startRank, startFile, endRank, endFile}		TODO - need a way of knowing which piece this is
		//return nextMove;
		return v.getRight();
	}
	
	public static boolean cutoffTest(ChessBoard board, int currentPly) {
		// search has been running longer than 5 seconds (5,000 milliseconds)
		//if(new Date().getTime() - startSearchTime > 5000)
		//	return true;
		if (currentPly >= maxPly)
			return true;
		else
			return board.terminalTest();
	}

	public static Tuple<Double, byte[]> MaxValue(ChessBoard board, double alpha, double beta, int currentPly) {
		// if terminal / cutoff reached
		if(cutoffTest(board, currentPly))
			return new Tuple<Double, byte[]>(board.Utility(), null);
		
		currentPly++;
		
		double v = Double.MIN_VALUE;
		byte[] nextMove = new byte[5];
		
		// for each move we can make in this state
		//	v = Max(v, MinValue(/*result of this move*/, alpha, beta, currentPly)); 
		//	if(v > beta)
		//		return v;
		//	alpha = Max(alpha, v);
		
		Map<Integer, ArrayList<byte[]>> moves = board.getAllLegalMoves(ServerAPI.getOurColor());
		ChessBoard newBoard;
		for(Integer startPos : moves.keySet()) {
			byte startFile = (byte) (startPos % 10);
			byte startRank = (byte) (startPos % 100 / 10);
			for(byte[] endPos : moves.get(startPos)) {
				newBoard = board.clone();
				System.out.println("ply: " + currentPly + " alpha: " + alpha + " beta: " + beta);
				newBoard.move(startRank, startFile, endPos[0], endPos[1]);
				
				lastMove[0] = nextMove[0] = startRank;
				lastMove[1] = nextMove[1] = startFile;
				lastMove[2] = nextMove[2] = endPos[0];
				lastMove[3] = nextMove[3] = endPos[1];
				Piece p = newBoard.get(endPos[0], endPos[1]);
				lastMove[4] = nextMove[4] = p != null ? p.getType() : -1;
				
				v = Math.max(v, MinValue(newBoard, alpha, beta, currentPly).getLeft());
				if(v >= beta) {
					System.out.println("CUTOFF");
					return new Tuple<Double, byte[]>(v, nextMove);
				}
				alpha = Math.max(alpha, v);
			}
		}
		return new Tuple<Double, byte[]>(v, nextMove);
	}
	
	public static Tuple<Double, byte[]> MinValue(ChessBoard board, double alpha, double beta, int currentPly) {
		// if terminal / cutoff reached
		// return board.Utility();
		if(cutoffTest(board, currentPly))
			return new Tuple<Double, byte[]>(board.Utility(), null);
		
		currentPly++;
		
		double v = Double.MAX_VALUE;
		byte[] nextMove = new byte[5];
		
		// for each move we can make in this state
		//	v = Min(v, MaxValue(/*result of this move*/, alpha, beta, currentPly)); 
		//	if(v < alpha)
		//		return v;
		//	alpha = Min(beta, v);
		
		Map<Integer, ArrayList<byte[]>> moves = board.getAllLegalMoves(ServerAPI.getOppontentColor());
		ChessBoard newBoard;
		for(Integer startPos : moves.keySet()) {
			byte startFile = (byte) (startPos % 10);
			byte startRank = (byte) (startPos % 100 / 10);
			for(byte[] endPos : moves.get(startPos)) {
				newBoard = board.clone();
				System.out.println("ply: " + currentPly + " alpha: " + alpha + " beta: " + beta);
				newBoard.move(startRank, startFile, endPos[0], endPos[1]);
				
				lastMove[0] = startRank;
				lastMove[1] = startFile;
				lastMove[2] = endPos[0];
				lastMove[3] = endPos[1];
				Piece p = newBoard.get(endPos[0], endPos[1]);
				lastMove[4] = nextMove[4] = p != null ? p.getType() : -1;
				
				v = Math.min(v, MaxValue(newBoard, alpha, beta, currentPly).getLeft());
				if(v <= alpha) {
					System.out.println("CUTOFF");
					return new Tuple<Double, byte[]>(v, nextMove);
				}
				beta = Math.min(beta, v);
			}
		}
		return new Tuple<Double, byte[]>(v, nextMove);
	}
}
