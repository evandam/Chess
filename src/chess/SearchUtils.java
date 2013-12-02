package chess;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class SearchUtils {
	// use this for in the cutoff test (limit 5 seconds per search?)
	public static long startSearchTime = 0;
	
	// have cutoff here which would be number of ply's?
	private static int maxPly = 5;
	
	private static byte[] nextMove = new byte[4];
	
	public static byte[] AlphaBetaSearch(ChessBoard board) {
		startSearchTime = new Date().getTime();
		double v = MaxValue(board, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
		
		// {startRank, startFile, endRank, endFile}
		return nextMove;
	}
	
	public static boolean cutoffTest(ChessBoard board) {
		// search has been running longer than 5 seconds (5,000 milliseconds)
		if(new Date().getTime() - startSearchTime > 5000)
			return true;
		else
			return board.terminalTest();
	}

	public static double MaxValue(ChessBoard board, double alpha, double beta, int currentPly) {
		// if terminal / cutoff reached
		if(cutoffTest(board))
			return board.Utility();
		
		currentPly++;
		
		double v = Double.MIN_VALUE;
		
		// for each move we can make in this state
		//	v = Max(v, MinValue(/*result of this move*/, alpha, beta, currentPly)); 
		//	if(v > beta)
		//		return v;
		//	alpha = Max(alpha, v);
			
		Map<Integer, ArrayList<byte[]>> moves = board.getAllLegalMoves(ServerAPI.getOurColor());
		ChessBoard newBoard;
		for(Integer startPos : moves.keySet()) {
			byte startFile = (byte) (startPos % 10);
			byte startRank = (byte) (startPos - startFile % 100);
			for(byte[] endPos : moves.get(startPos)) {
				newBoard = board.clone();
				newBoard.move(startRank, startFile, endPos[0], endPos[1]);
				nextMove[0] = startRank; nextMove[1] = startFile;
				nextMove[2] = endPos[0]; nextMove[3] = endPos[1];
				v = Math.max(v, MinValue(newBoard, alpha, beta, currentPly));
				if(v >= beta)
					return v;
				alpha = Math.max(alpha, v);
			}
		}
		return v;
	}
	
	public static double MinValue(ChessBoard board, double alpha, double beta, int currentPly) {
		// if terminal / cutoff reached
		// return board.Utility();
		if(cutoffTest(board))
			return board.Utility();
		
		currentPly++;
		
		double v = Double.MAX_VALUE;
		
		// for each move we can make in this state
		//	v = Min(v, MaxValue(/*result of this move*/, alpha, beta, currentPly)); 
		//	if(v < alpha)
		//		return v;
		//	alpha = Min(beta, v);
			
		Map<Integer, ArrayList<byte[]>> moves = board.getAllLegalMoves(ServerAPI.getOppontentColor());
		ChessBoard newBoard;
		for(Integer startPos : moves.keySet()) {
			byte startFile = (byte) (startPos % 10);
			byte startRank = (byte) (startPos - startFile % 100);
			for(byte[] endPos : moves.get(startPos)) {
				newBoard = board.clone();
				newBoard.move(startRank, startFile, endPos[0], endPos[1]);
				v = Math.min(v, MaxValue(newBoard, alpha, beta, currentPly));
				if(v <= alpha)
					return v;
				beta = Math.min(beta, v);
			}
		}		
		return v;
	}
}
