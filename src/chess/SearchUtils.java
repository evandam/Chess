package chess;

import java.util.Date;

public class SearchUtils {
	// use this for in the cutoff test (limit 5 seconds per search?)
	public static long startSearchTime = 0;
	
	// have cutoff here which would be number of ply's?
	
	public static void AlphaBetaSearch(ChessBoard board) {
		startSearchTime = new Date().getTime();
		int v = MaxValue(board, Integer.MIN_VALUE, Integer.MAX_VALUE);
		
		// return move
	}
	
	public static boolean cutoffTest(ChessBoard board) {
		// search has been running longer than 5 seconds (5,000 milliseconds)
		if(new Date().getTime() - startSearchTime > 5000)
			return true;
		else
			return board.terminalTest();
	}
	
	public static int MaxValue(ChessBoard board, int alpha, int beta) {
		// if terminal / cutoff reached
		// return board.Utility();
		if(cutoffTest(board))
			return board.Utility();
		
		int v = Integer.MIN_VALUE;
		
		// for each move we can make in this state
		//	v = Max(v, MinValue(/*result of this move*/, alpha, beta)); 
		//	if(v > beta)
		//		return v;
		//	alpha = Max(alpha, v);
			
		return v;
	}
	
	public static int MinValue(ChessBoard board, int alpha, int beta) {
		// if terminal / cutoff reached
		// return board.Utility();
		if(cutoffTest(board))
			return board.Utility();
		
		int v = Integer.MAX_VALUE;
		
		// for each move we can make in this state
		//	v = Min(v, MaxValue(/*result of this move*/, alpha, beta)); 
		//	if(v < alpha)
		//		return v;
		//	alpha = Min(beta, v);
			
		return v;
	}
	
	
	private static int Max(int num1, int num2) {
		return num1 > num2 ? num1 : num2;
	}
	
	private static int Min(int num1, int num2) {
		return num1 < num2 ? num1 : num2;
	}
}
