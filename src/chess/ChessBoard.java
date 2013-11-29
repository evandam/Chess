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
	private Piece[][] board;
	
	// keep track of where the white/black pieces are on the board...quicker to access
	// max size is 16 pieces so a plain old array is best
	public byte[][] whitePieces, blackPieces;
	// TODO - thinking of just keeping a count of number of each piece for each color for quick eval function calculations
	
	
	// init all the pieces on the board
	public ChessBoard() {
		board = new Piece[8][8];
		// hold the [rank, file] of each side's pieces
		whitePieces = new byte[16][2];	// its more memory efficient to do [2][16]
		blackPieces = new byte[16][2];	// store rank and file in one byte

		// TODO - know which indexes refer to which pieces
		
		// go through and set the pawns on each side
		for(byte file = A; file <= H; file++) {
			// both sides' pawns
			board[R2][file] = new Piece(Piece.PAWN, Piece.WHITE);
			board[R7][file] = new Piece(Piece.PAWN, Piece.BLACK);
			
			whitePieces[PAWN + file][0] = R2;
			whitePieces[PAWN + file][1] = file;
			
			blackPieces[PAWN + file][0] = R7;
			blackPieces[PAWN + file][1] = file;
		}
		
		// white pieces
		board[R1][D] = new Piece(Piece.KING, Piece.WHITE);
		whitePieces[KING][0] = R1;
		whitePieces[KING][1] = D;
		
		board[R1][E] = new Piece(Piece.QUEEN, Piece.WHITE);
		whitePieces[QUEEN][0] = R1;
		whitePieces[QUEEN][1] = E;
		
		board[R1][A] = new Piece(Piece.ROOK, Piece.WHITE);
		whitePieces[ROOK][0] = R1;
		whitePieces[ROOK][1] = A;
		
		board[R1][H] = new Piece(Piece.ROOK, Piece.WHITE);
		whitePieces[ROOK + 1][0] = R1;
		whitePieces[ROOK + 1][1] = H;
		
		board[R1][C] = new Piece(Piece.BISHOP, Piece.WHITE);
		whitePieces[BISHOP][0] = R1;
		whitePieces[BISHOP][1] = C;
		
		board[R1][F] = new Piece(Piece.BISHOP, Piece.WHITE);
		whitePieces[BISHOP + 1][0] = R1;
		whitePieces[BISHOP + 1][1] = F;
		
		board[R1][B] = new Piece(Piece.KNIGHT, Piece.WHITE);
		whitePieces[KNIGHT][0] = R1;
		whitePieces[KNIGHT][1] = B;
		
		board[R1][G] = new Piece(Piece.KNIGHT, Piece.WHITE);
		whitePieces[KNIGHT + 1][0] = R1;
		whitePieces[KNIGHT + 1][1] = G;
		
		
		// black pieces
		board[R8][D] = new Piece(Piece.KING, Piece.BLACK);
		blackPieces[KING][0] = R8;
		blackPieces[KING][1] = D;
		
		board[R8][E] = new Piece(Piece.QUEEN, Piece.BLACK);
		blackPieces[QUEEN][0] = R8;
		blackPieces[QUEEN][1] = E;
		
		board[R8][A] = new Piece(Piece.ROOK, Piece.BLACK);
		blackPieces[ROOK][0] = R8;
		blackPieces[ROOK][1] = A;
		
		board[R8][H] = new Piece(Piece.ROOK, Piece.BLACK);
		blackPieces[ROOK + 1][0] = R8;
		blackPieces[ROOK + 1][1] = H;
		
		board[R8][C] = new Piece(Piece.BISHOP, Piece.BLACK);
		blackPieces[BISHOP][0] = R8;
		blackPieces[BISHOP][1] = C;

		board[R8][F] = new Piece(Piece.BISHOP, Piece.BLACK);
		blackPieces[BISHOP + 1][0] = R8;
		blackPieces[BISHOP + 1][1] = F;
		
		board[R8][B] = new Piece(Piece.KNIGHT, Piece.BLACK);
		blackPieces[KNIGHT][0] = R8;
		blackPieces[KNIGHT][1] = B;
		
		board[R8][G] = new Piece(Piece.KNIGHT, Piece.BLACK);
		blackPieces[KNIGHT + 1][0] = R8;
		blackPieces[KNIGHT + 1][1] = G;
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
		return board[rank][file];
	}
	
	// don't handle any fancy moves yet
	// maybe return a new instance of the board?
	public void move(byte[] start, byte[] end) {
		// check if opponent's piece was in that spot and update black + white piece positions
		Piece startPiece = board[start[0]][start[1]];
		
		// update the teams array of pieces
		byte[][] pieces = whitePieces;
		if(startPiece.color == Piece.BLACK)
			pieces = blackPieces;
		for(byte[] pos : pieces) {
			if(start[0] == pos[0] && start[1] == pos[1]) {
				pos[0] = end[0];
				pos[1] = end[1];
				break;
			}
		}
		
		Piece endPiece = board[end[0]][end[1]];
		// piece is captured, remove it from the list
		if(endPiece != null) {
			byte[][] enemyPieces = blackPieces;
			if(endPiece.color == Piece.BLACK) 
				enemyPieces = whitePieces;
			// find the piece's element in the array
			for(int i = 0; i < enemyPieces.length; i++) {
				if(enemyPieces[i][0] == end[0] && enemyPieces[i][1] == end[1]) {
					enemyPieces[i] = null;
					break;
				}
			}
		}
		
		// mark that the piece has moved - necessary for castling and en passant
		startPiece.hasMoved = 1;
		
		// check for special cases for pawns
		if(startPiece.type == Piece.PAWN) {
			// moved up 2 ranks on first move
			if(Math.abs(start[0] - end[0]) > 1)
				startPiece.hasMoved = 2;
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
				startPiece.type = Piece.QUEEN;					
			}
		}
		// check for castling - moved 2 files
		else if(startPiece.type == Piece.KING) {
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
					if(board[rank][file].color == Piece.WHITE)
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
