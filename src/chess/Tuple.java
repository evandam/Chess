package chess;

/**
 * Class to represent a pair of values, used to return a solution, cutoff, or failure.
 * 
 * (Node, null) = solution
 * (null, #) = cutoff
 * (null, null) or null = failure 
 * 
 * @author Bryan
 *
 * @param <S1> type of left value
 * @param <S2> type of right value
 */
public class Tuple<S1, S2> {
	private S1 left;
	private S2 right;
	
	/**
	 * Default constructor, assigns null to each value.
	 */
	public Tuple() {
		this.left = null;
		this.right = null;
	}
	
	/**
	 * Overloaded constructor, assigns parameters to each value.
	 * 
	 * @param left value for left item
	 * @param right value for right item
	 */
	public Tuple(S1 left, S2 right) {
		this.left = left;
		this.right = right;
	}
	
	/**
	 * Gets the value in the tuple that is on the left.
	 * @return left value of tuple
	 */
	public S1 getLeft() { return left; }
	
	/**
	 * Gets the value in the tuple that is on the right.
	 * @return right value of tuple
	 */
	public S2 getRight() { return right; }
	
	/**
	 * Sets the value in the tuple that is on the left.
	 * @param value - new value for left item
	 */
	public void setLeft(S1 value) { left = value; }
	
	/**
	 * Sets the value in the tuple that is on the right.
	 * @param value - new value for right item
	 */
	public void setRight(S2 value) { right = value; }
}
