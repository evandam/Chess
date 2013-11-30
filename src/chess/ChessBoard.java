package chess;

public class ChessBoard {
	// convert files (a - h) to numerical values - these are the COLUMNS
	public static final byte A = 0;
	public static final byte B = 1;
	public static final byte C = 2;
	public static final byte D = 3;
	public static final byte E = 4;
	public static final byte F = 5;
	public static final byte G = 6;
	public static final byte H = 7;
	
	// ranks have a funny representation (highest on top, also start at 1) - these are the ROWS
	public static final byte R8 = 7;
	public static final byte R7 = 6;
	public static final byte R6 = 5;
	public static final byte R5 = 4;
	public static final byte R4 = 3;
	public static final byte R3 = 2;
	public static final byte R2 = 1;
	public static final byte R1 = 0;
	
	// indexes in white/blackPieces arrays for each piece
	// to access the 2nd piece, add 1 to the starting index (ie ROOK + 1)
	public static byte KING   = 0;
	public static byte QUEEN  = 1;
	public static byte ROOK   = 2;
	public static byte BISHOP = 4;
	public static byte KNIGHT = 6;
	public static byte PAWN   = 8;
	
	// variable to hold which color we are, this matters because we need to be correctly oriented
	private byte ourColor;
	
	// [col, row] or [rank, file]
	// board will contain indexes into the white and black pieces arrays
	private byte[][] board;
	
	// keep track of where the white/black pieces are on the board...quicker to access
	// max size is 16 pieces so a plain old array is best
	public Piece[] whitePieces, blackPieces;
	// TODO - thinking of just keeping a count of number of each piece for each color for quick eval function calculations
	
	
	/**
	 * init all the pieces on the board
	 */
	public ChessBoard() {
		/* Board array structure
		 * Each entry in this array will contain 1 of 3 values:
		 * 
		 * 1) 0 for empty
		 * 2) index into the white pieces array plus 1 (values ranging from 1 to 16 initially)
		 * 3) index into the black pieces array plus 1 * -1 (values ranging from -1 to -16 initially)
		 */
		board = new byte[8][8];
		
		// hold the piece object for each side's pieces
		// we can resize these arrays when a move captures a piece
		whitePieces = new Piece[16];	// its more memory efficient to do [2][16]
		blackPieces = new Piece[16];	// store rank and file in one byte
		
		/* Pieces array structure:
		 * arranged from highest number of moves to least number of moves
		 * --> here is where we want to have an effective ordering for move generation
		 * 
		 * 0)  queen
		 * 1)  rook
		 * 2)  rook
		 * 3)  bishop
		 * 4)  bishop
		 * 5)  knight
		 * 6)  knight
		 * 7)  king
		 * 8)  pawn
		 * 9)  pawn
		 * 10) pawn
		 * 11) pawn
		 * 12) pawn
		 * 13) pawn
		 * 14) pawn
		 * 15) pawn
		 */

		// go through and set the pawns on each side
		for(byte file = A; file <= H; file++) {
			// both sides' pawns
			board[R2][file] = (byte) (9 + file);
			board[R7][file] = (byte) (-9 - file);
			
			whitePieces[8 + file] = new Piece(R2, file, Piece.PAWN);
			blackPieces[8 + file] = new Piece(R7, file, Piece.PAWN);
		}
		
		// white pieces
		board[R1][E] = 1;
		whitePieces[0] = new Piece(R1, E, Piece.QUEEN);
		
		board[R1][A] = 2;
		whitePieces[1] = new Piece(R1, A, Piece.ROOK);
		
		board[R1][H] = 3;
		whitePieces[2] = new Piece(R1, H, Piece.ROOK);
		
		board[R1][C] = 4;
		whitePieces[3] = new Piece(R1, C, Piece.BISHOP);
		
		board[R1][F] = 5;
		whitePieces[4] = new Piece(R1, F, Piece.BISHOP);
		
		board[R1][B] = 6;
		whitePieces[5] = new Piece(R1, B, Piece.KNIGHT);
		
		board[R1][G] = 7;
		whitePieces[6] = new Piece(R1, G, Piece.KNIGHT);
		
		board[R1][D] = 8;
		whitePieces[7] = new Piece(R1, D, Piece.KING);
		
		
		// black pieces
		board[R8][E] = -1;
		blackPieces[0] = new Piece(R8, E, Piece.QUEEN);
		
		board[R8][A] = -2;
		blackPieces[1] = new Piece(R8, A, Piece.ROOK);
		
		board[R8][H] = -3;
		blackPieces[2] = new Piece(R8, H, Piece.ROOK);
		
		board[R8][C] = -4;
		blackPieces[3] = new Piece(R8, C, Piece.BISHOP);
		
		board[R8][F] = -5;
		blackPieces[4] = new Piece(R8, F, Piece.BISHOP);
		
		board[R8][B] = -6;
		blackPieces[5] = new Piece(R8, B, Piece.KNIGHT);
		
		board[R8][G] = -7;
		blackPieces[6] = new Piece(R8, G, Piece.KNIGHT);
		
		board[R8][D] = -8;
		blackPieces[7] = new Piece(R8, D, Piece.KING);
	}
	
	public ChessBoard(byte[][] b) {
		// TODO - overloaded constructor for creating new chess board
		// objects when we search to avoid cloning
	}
	
	/**
	 * Sets which color we are which determines our perspective. 
	 * 
	 * @param color - the color we are for this game
	 */
	public void setOurColor(byte color) {
		this.ourColor = color;
	}
	
	public Piece get(byte rank, byte file) {
		int idx = board[rank][file];
		// check for no piece in that square
		if(idx == 0)
			return null;
		// return either the white piece or the black piece
		return idx > 0 ? whitePieces[idx - 1] : blackPieces[(idx*-1) - 1];
	}
	
	// don't handle any fancy moves yet
	// maybe return a new instance of the board?
	// TODO - handle promotions better?
	public void move(byte startRank, byte startFile, byte endRank, byte endFile) {
		// check if opponent's piece was in that spot and update black + white piece positions
		
		// get the index into either the white or black pieces array for the starting position
		int startIdx = board[startRank][startFile];
		// get the piece that is on the given spot
		Piece startPiece = startIdx > 0 ? whitePieces[startIdx - 1] : blackPieces[(startIdx*-1) - 1];
		// clear out the spot on the board since the piece is being moved from there
		board[startRank][startFile] = 0;
		
		// get the index into either the white or black pieces array or blank spot for the ending position
		int endIdx = board[endRank][endFile];
		// get the piece that is on the given spot
		Piece endPiece = endIdx > 0 ? whitePieces[endIdx - 1] : blackPieces[(endIdx*-1) - 1];
		
		// if piece is captured, remove it from the list
		if(endPiece != null) {
			endPiece = null;
			board[endRank][endFile] = 0;
			
			// remove the entry in the white pieces array
			if(endIdx > 0) {
				// remove the piece from the pieces array
				Piece[] newArr = new Piece[whitePieces.length - 1];
				System.arraycopy(whitePieces, 0, newArr, 0, endIdx - 1);
				System.arraycopy(whitePieces, endIdx, newArr, endIdx - 1, whitePieces.length - endIdx);
			}
			else {
				// remove the piece from the pieces array
				Piece[] newArr = new Piece[blackPieces.length - 1];
				System.arraycopy(blackPieces, 0, newArr, 0, endIdx - 1);
				System.arraycopy(blackPieces, endIdx, newArr, endIdx - 1, blackPieces.length - endIdx);
			}
		}
		
		// TODO - finish converting ...
		
		// mark that the piece has moved - necessary for castling and en passant
		startPiece.setHasMoved((byte) 1);
		
		// check for special cases for pawns
		if(startPiece.getType() == Piece.PAWN) {
			// moved up 2 ranks on first move
			if(Math.abs(start[0] - end[0]) > 1)
				startPiece.setHasMoved((byte) 2);
			// en passant - remove the piece that was adjacent
			// the pawn moved diagonally but didn't capture the piece in that square, must be en passant
			else if(start[1] != end[1] && endPiece == null) {
				endPiece = board[start[0]][end[1]];
				board[start[0]][end[1]] = null;

				// find the piece's element in the array and remove it
				for(int i = 0; i < pieces.length; i++) {
					if(pieces[i][0] == end[0] && pieces[i][1] == end[1]) {
						pieces[i] = null;
						break;
					}
				}
			}
			// queening promotion - should this be dynamic to allow others?
			else if(end[0] == 0 || end[0] == 7) {
				startPiece.setType(Piece.QUEEN);					
			}
		}
		// check for castling - moved 2 files
		else if(startPiece.getType() == Piece.KING) {
			// king side castle
			if(start[1] == D && end[1] == B) {
				// move the rook too
				board[start[0]][C] = board[start[0]][A];
				board[start[0]][A] = null;
				// update the player's array
				pieces[ROOK + 1][1] = C; 
			}
			// queen side castle
			else if(start[1] == D && end[1] == F) {
				// move the rook too
				board[start[0]][E] = board[start[0]][H];
				board[start[0]][H] = null;
				// update the player's array
				pieces[ROOK][1] = E; 
			}
		}
		
		board[end[0]][end[1]] = board[start[0]][start[1]];
		board[start[0]][start[1]] = null;
	}
	
	// add promotion to this too...
	public String getMoveString(byte[] start, byte[] end) {
		// i.e. Pd2d3, Nb1c3
		String moveString = board[end[0]][end[1]].toString();		// piece type
		moveString += getFile(start[1]) + "" + getRank(start[0]);	// beginning pos
		moveString += getFile(end[1]) + "" + getRank(end[0]);		// end pos
		return moveString;
	}
	
	// check all possible enemy moves to see if they can attack the location
	public boolean isUnderAttack(byte rank, byte file, byte color) {
		byte[][] pieces = whitePieces;
		if(color == Piece.WHITE) 
			pieces = blackPieces;
		Piece p;
		// go through each piece the enemy has and check if it can move into the square
		for(byte[] pos : pieces) {
			p = board[pos[0]][pos[1]];
			if(p != null) {
				for(byte[] attackLoc : p.getPossibleMoves(this, new byte[]{rank, file})) {
					// the square can be attacked
					if(rank == attackLoc[0] && file == attackLoc[1])
						return true;
				}
			}
		}
		return false;
	}
	
	
	public int Utility() {
		// TODO - actually write code to calculate this, have it calculate each time a move is made in searching maybe?
		
		// here is where we use ourColor to determine how we calculate the utility
		
		int blackKings, blackQueens, blackRooks, blackBishops, blackKnights, blackPawns,
			whiteKings, whiteQueens, whiteRooks, whiteBishops, whiteKnights, whitePawns;
		
		for(int i = 0; i < this.whitePieces.length; i++) {
			
		}
		
		/* Evaluation function to use?
		 
			f(p) = 200(K-K')
		       + 9(Q-Q')
		       + 5(R-R')
		       + 3(B-B' + N-N')
		       + 1(P-P')
		       - 0.5(D-D' + S-S' + I-I')
		       + 0.1(M-M') + ...
		 
			KQRBNP = number of kings, queens, rooks, bishops, knights and pawns
			D,S,I = doubled, blocked and isolated pawns
			M = Mobility (the number of legal moves)
			
		 */
		
		return 0;
	}
	
	
	
	
	
	// convert the constant back to a character (a-h)
	public static char getFile(byte i) {
		switch(i) {
		case A:
			return 'a';
		case B:
			return 'b';
		case C:
			return 'c';
		case D:
			return 'd';
		case E:
			return 'e';
		case F:
			return 'f';
		case G:
			return 'g';
		default:
			return 'h';
		}
	}
	
	// convert the char to a constant byte
	public static byte getFile(char c) {
		switch(c) {
		case 'a':
			return A;
		case 'b':
			return B;
		case 'c':
			return C;
		case 'd':
			return D;
		case 'e':
			return E;
		case 'f':
			return F;
		case 'g':
			return G;
		default:
			return H;
		}
	}
	
	// convert from constant (0-7) to standard repr. for chess (8-1)
	public static byte getRank(byte i) {
		return (byte) (8 - i);
	}
	
	@Override
	public String toString() {
		String str = "";
		for(byte rank = 0; rank < 8; rank++) {
			for(byte file = 0; file < 8; file++) {
				if(board[rank][file] != null) {
					if(board[rank][file].getColor() == Piece.WHITE)
						str += "W";
					else
						str += "B";
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
