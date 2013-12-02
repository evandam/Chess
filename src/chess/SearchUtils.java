package chess;

public class SearchUtils {
	
	// have cutoff here which would be number of ply's?
	private static int maxPly = 5;
	
	public static void AlphaBetaSearch(ChessBoard board) {
		double v = MaxValue(board, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
		
		// return move
	}
	
	public static double MaxValue(ChessBoard board, int alpha, int beta, int currentPly) {
		// if terminal / cutoff reached
		if(currentPly > maxPly)
			return board.Utility();
		
		currentPly++;
		
		double v = Double.MIN_VALUE;
		
		// for each move we can make in this state
		//	v = Max(v, MinValue(/*result of this move*/, alpha, beta, currentPly)); 
		//	if(v > beta)
		//		return v;
		//	alpha = Max(alpha, v);
			
		return v;
	}
	
	public static double MinValue(ChessBoard board, int alpha, int beta, int currentPly) {
		// if terminal / cutoff reached
		// return board.Utility();
		
		currentPly++;
		
		double v = Double.MAX_VALUE;
		
		// for each move we can make in this state
		//	v = Min(v, MaxValue(/*result of this move*/, alpha, beta, currentPly)); 
		//	if(v < alpha)
		//		return v;
		//	alpha = Min(beta, v);
			
		return v;
	}
	
	
	private static double Max(double num1, double num2) {
		return num1 > num2 ? num1 : num2;
	}
	
	private static double Min(double num1, double num2) {
		return num1 < num2 ? num1 : num2;
	}
}
