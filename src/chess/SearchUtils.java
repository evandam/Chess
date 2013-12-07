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
	private static int maxPly = 6;
	
	public static byte[] lastMove = new byte[5];
	
	public static byte[] AlphaBetaSearch(ChessBoard board) {
		startSearchTime = new Date().getTime();
		Tuple<Integer, byte[]> v = MaxValue(board, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
		
		return v.getRight();
	}
	
	public static boolean cutoffTest(ChessBoard board, int currentPly) {
		// search has been running longer than 5 seconds (5,000 milliseconds)
		//if(new Date().getTime() - startSearchTime > 5000)
		//	return true;
		
		// start with a larger max ply like 7, since we are exploring interesting pieces first (pieces
		// that have the potential to make the most moves) then we want to explore those thoroughly and
		// if time is getting up there, gradually back down the # of plys we go to which will speed
		// up the search while ensuring that we will have always explored down to some base ply
		if(board.terminalTest())
			return true;
		/*else if((new Date().getTime() - startSearchTime) > 20000 && currentPly == 1) {
			// can't just return at a given ply because if we are at 7 and then change
			// the cutoff to 4, we will prune but return values that may be good because
			// we went down farther on some but that doesn't necessarily mean they are best
			// the solution is to change the max ply only when we get back to the root max node
			maxPly = 4;
			return false;
		}
		else if((new Date().getTime() - startSearchTime) > 10000 && currentPly == 1) {
			maxPly = 5;
			return false;
		}*/
		else if (currentPly >= maxPly)
			return true;
		else
			return false;
		
	}

	/**
	 * Picks the value of the best choice found so far at
	 * any point along the path for MAX.
	 * 
	 * @param board - ChessBoard that we are searching through
	 * @param alpha - highest-value choice so far
	 * @param beta - value of lowest-value choice so far
	 * @param currentPly - how far down the search tree we currently are 
	 * @return - tuple of return value and the move associate with that value
	 */
	public static Tuple<Integer, byte[]> MaxValue(ChessBoard board, int alpha, int beta, int currentPly) {
		// if terminal / cutoff reached
		if(cutoffTest(board, currentPly))
			return new Tuple<Integer, byte[]>(board.Utility(), null);
		
		currentPly++;
		
		Tuple<Integer, byte[]> v = new Tuple<Integer, byte[]>(Integer.MIN_VALUE, null);
		ArrayList<String> vs = new ArrayList<String>(); //for debugging - seeing utils and their moves
		Map<Integer, ArrayList<byte[]>> moves = board.getAllLegalMoves(ServerAPI.getOurColor());
		ChessBoard newBoard;
		for(Integer startPos : moves.keySet()) {
			byte startFile = (byte) (startPos % 10);
			byte startRank = (byte) (startPos % 100 / 10);
			for(byte[] endPos : moves.get(startPos)) {
				newBoard = board.clone();
				//System.out.println("ply: " + currentPly + " alpha: " + alpha + " beta: " + beta);
				newBoard.move(startRank, startFile, endPos[0], endPos[1]);
				
				lastMove[0] = startRank;
				lastMove[1] = startFile;
				lastMove[2] = endPos[0];
				lastMove[3] = endPos[1];
				Piece p = newBoard.get(endPos[0], endPos[1]);
				lastMove[4] = p != null ? p.getType() : 0;
				
				// copy this move we just made as we will be storing the move in a tuple
				// along with the value in order to pick the max value of all the mins
				byte[] copyLastMove = new byte[lastMove.length];
				// we only need to perform the copying on the root max node
				// so avoid some time copying moves, albeit this saves very little time
				if(currentPly == 1)
					System.arraycopy(lastMove, 0, copyLastMove, 0, lastMove.length);
				
				// make the mutually recursive call, when it returns (from the perspective of the
				// root max node) we will store the min value along with the move that generated that
				// child node...this move is the one we will be eventually selecting and returning
				// as our best guess at an optimal move given a ply depth or time span
				Tuple<Integer, byte[]> minResult =
					new Tuple<Integer, byte[]>(MinValue(newBoard, alpha, beta, currentPly), copyLastMove);
				byte[] move = minResult.getRight();
				vs.add(minResult.getLeft() + "-" + move[0] + "" + move[1] + "" + move[2] + "" + move[3]);
				// the first min value (the left most child of the root max node) will always be expanded fully and 
				// as a result, we will always pick that so the value of v is always assigned the first min result
				v = Max(v, minResult);
				if(v.getLeft() >= beta) {
					//System.out.println("MAX CUTOFF v = " + v + " beta = " + beta);
					return v;
				}
				alpha = Math.max(alpha, v.getLeft());
			}
		}
		if(currentPly == 1)	//for debugging - seeing utils and their moves
			return v;
		else
			return v;
	}
	
	/**
	 * Picks the value of the worst choice found so far at
	 * any point along the path for MIN.
	 * 
	 * @param board - ChessBoard that we are searching through
	 * @param alpha - highest-value choice so far
	 * @param beta - value of lowest-value choice so far
	 * @param currentPly - how far down the search tree we currently are 
	 * @return - tuple of return value and the move associate with that value
	 */
	public static int MinValue(ChessBoard board, int alpha, int beta, int currentPly) {
		// if terminal / cutoff reached
		// return board.Utility();
		if(cutoffTest(board, currentPly))
			return board.Utility();
		
		currentPly++;
		
		int v = Integer.MAX_VALUE;
		
		Map<Integer, ArrayList<byte[]>> moves = board.getAllLegalMoves(ServerAPI.getOppontentColor());
		ChessBoard newBoard;
		for(Integer startPos : moves.keySet()) {
			byte startFile = (byte) (startPos % 10);
			byte startRank = (byte) (startPos % 100 / 10);
			for(byte[] endPos : moves.get(startPos)) {
				newBoard = board.clone();
				//System.out.println("ply: " + currentPly + " alpha: " + alpha + " beta: " + beta);
				newBoard.move(startRank, startFile, endPos[0], endPos[1]);
				
				lastMove[0] = startRank;
				lastMove[1] = startFile;
				lastMove[2] = endPos[0];
				lastMove[3] = endPos[1];
				Piece p = newBoard.get(endPos[0], endPos[1]);
				lastMove[4] = p != null ? p.getType() : 0;
				
				v = Math.min(v, MaxValue(newBoard, alpha, beta, currentPly).getLeft());
				if(v <= alpha) {
					//System.out.println("MIN CUTOFF v = " + v + " alpha = " + alpha);
					return v;
				}
				beta = Math.min(beta, v);
			}
		}
		return v;
	}
	
	/**
	 * This method compares two pairs of values and their moves, returning
	 * the one with the higher value. If the two values are equivalent, then the
	 * first value is returned (if we do a better job at our evaluation function then
	 * this should never be an issue, however if we order our moves better then picking
	 * the first one between two that are even will usually give us a better move).
	 * We use this instead of the Math.max because while we are only comparing 
	 * their values (v which is really utility), we also need to return the move
	 * associated with that.
	 * 
	 * @param t1 - first tuple of values
	 * @param t2 - second tuple of values
	 * @return tuple with the highest value in its left item
	 */
	private static Tuple<Integer, byte[]> Max(Tuple<Integer, byte[]> t1, Tuple<Integer, byte[]> t2) {
		if(t1.getLeft().equals(t2.getLeft()))
			return t1;
		else if(t1.getLeft() > t2.getLeft())
			return t1;
		else
			return t2;
		//return t1.getLeft().equals(t2.getLeft()) ? t1 : (t1.getLeft() > t2.getLeft() ? t1 : t2);
	}
}
