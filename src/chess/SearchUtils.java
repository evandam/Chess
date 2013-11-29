package chess;

public class SearchUtils {
	
	
	public static void AlphaBetaSearch(ChessBoard board) {
		int v = MaxValue(board, Integer.MIN_VALUE, Integer.MAX_VALUE);
		
		// TODO - check for 
	}
	
	public static int MaxValue(ChessBoard board, int alpha, int beta) {
		// if terminal / cutoff reached
		// return board.Utility();
		
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
