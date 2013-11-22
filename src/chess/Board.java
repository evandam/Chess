package chess;

public class Board {
	// convert files (a - h) to numerical values
	public static final byte A = 0;
	public static final byte B = 1;
	public static final byte C = 2;
	public static final byte D = 3;
	public static final byte E = 4;
	public static final byte F = 5;
	public static final byte G = 6;
	public static final byte H = 7;
	
	// ranks have a funny representation (highest on top, also start at 1)
	public static final byte R8 = 0;
	public static final byte R7 = 1;
	public static final byte R6 = 2;
	public static final byte R5 = 3;
	public static final byte R4 = 4;
	public static final byte R3 = 5;
	public static final byte R2 = 6;
	public static final byte R1 = 7;
	
	// [col, row] or [rank, file]
	private Piece[][] board;
	
	// init all the pieces on the board
	public Board() {
		board = new Piece[8][8];

		// pawns
		for(byte file = 0; file < 8; file++) {
			board[R2][file] = new Piece(Piece.PAWN, Piece.WHITE);
			board[R7][file] = new Piece(Piece.PAWN, Piece.BLACK);
		}
		// white pieces
		board[R1][A] = new Piece(Piece.ROOK, Piece.WHITE);
		board[R1][B] = new Piece(Piece.KNIGHT, Piece.WHITE);
		board[R1][C] = new Piece(Piece.BISHOP, Piece.WHITE);
		board[R1][D] = new Piece(Piece.KING, Piece.WHITE);
		board[R1][E] = new Piece(Piece.QUEEN, Piece.WHITE);
		board[R1][F] = new Piece(Piece.BISHOP, Piece.WHITE);
		board[R1][G] = new Piece(Piece.KNIGHT, Piece.WHITE);
		board[R1][H] = new Piece(Piece.ROOK, Piece.WHITE);
		
		// black pieces
		board[R8][A] = new Piece(Piece.ROOK, Piece.BLACK);
		board[R8][B] = new Piece(Piece.KNIGHT, Piece.BLACK);
		board[R8][C] = new Piece(Piece.BISHOP, Piece.BLACK);
		board[R8][D] = new Piece(Piece.QUEEN, Piece.BLACK);
		board[R8][E] = new Piece(Piece.KING, Piece.BLACK);
		board[R8][F] = new Piece(Piece.BISHOP, Piece.BLACK);
		board[R8][G] = new Piece(Piece.KNIGHT, Piece.BLACK);
		board[R8][H] = new Piece(Piece.ROOK, Piece.BLACK);
	}
	
	public Piece get(byte rank, byte file) {
		return board[rank][file];
	}
	
	public void move(byte[] start, byte[] end) {
		board[end[0]][end[1]] = board[start[0]][start[1]];
		board[start[0]][start[1]] = null;
	}
	
	@Override
	public String toString() {
		String str = "";
		for(byte rank = 0; rank < 8; rank++) {
			for(byte file = 0; file < 8; file++) {
				if(board[rank][file] != null) {
					str += board[rank][file].toString() + " ";
				}
				else {
					str += "---";
				}
			}
			str += "\n";
		}
		return str;
	}
}
