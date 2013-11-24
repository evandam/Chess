package chess;

import java.util.HashMap;

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
	
	// indexes in white/blackPieces arrays for each piece
	// to access the 2nd piece, add 1 to the starting index (ie ROOK + 1)
	public static byte KING = 0;
	public static byte QUEEN = 1;
	public static byte ROOK = 2;
	public static byte BISHOP = 4;
	public static byte KNIGHT = 6;
	public static byte PAWN = 8;
	
	
	// [col, row] or [rank, file]
	private Piece[][] board;
	
	// keep track of where the white/black pieces are on the board...quicker to access
	// max size is 16 pieces so a plain old array is best
	public byte[][] whitePieces, blackPieces;
	
	
	// init all the pieces on the board
	public Board() {
		board = new Piece[8][8];
		// hold the [rank, file] of each side's pieces
		whitePieces = new byte[16][2];
		blackPieces = new byte[16][2];

		for(byte file = 0; file < 8; file++) {
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
	
	public Piece get(byte rank, byte file) {
		return board[rank][file];
	}
	
	// don't handle any fancy moves yet
	// maybe return a new instance of the board?
	public void move(byte[] start, byte[] end) {
		// check if opponent's piece was in that spot and update black + white piece positions
		Piece startPiece = board[start[0]][start[1]];
		
		Piece endPiece = board[end[0]][end[1]];
		// piece is captured, remove it from the list
		if(endPiece != null) {
			byte[][] pieces = whitePieces;
			if(endPiece.color == Piece.BLACK) 
				pieces = blackPieces;
			// find the piece's element in the array
			for(int i = 0; i < pieces.length; i++) {
				if(pieces[i][0] == end[0] && pieces[i][1] == end[1]) {
					pieces[i] = null;
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
				byte[][] pieces = whitePieces;
				if(endPiece.color == Piece.BLACK) 
					pieces = blackPieces;
				// find the piece's element in the array and remove it
				for(int i = 0; i < pieces.length; i++) {
					if(pieces[i][0] == end[0] && pieces[i][1] == end[1]) {
						pieces[i] = null;
						break;
					}
				}
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
		case 'A':
			return A;
		case 'B':
			return B;
		case 'C':
			return C;
		case 'D':
			return D;
		case 'E':
			return E;
		case 'F':
			return F;
		case 'G':
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
