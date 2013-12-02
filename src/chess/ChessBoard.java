package chess;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

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
	private boolean kingCaptured = false;
	
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
			
			whitePieces[8 + file] = new Piece(R2, file, Piece.PAWN, Piece.WHITE);
			blackPieces[8 + file] = new Piece(R7, file, Piece.PAWN, Piece.BLACK);
		}
		
		// white pieces
		board[R1][E] = 1;
		whitePieces[0] = new Piece(R1, E, Piece.QUEEN, Piece.WHITE);
		
		board[R1][A] = 2;
		whitePieces[1] = new Piece(R1, A, Piece.ROOK, Piece.WHITE);
		
		board[R1][H] = 3;
		whitePieces[2] = new Piece(R1, H, Piece.ROOK, Piece.WHITE);
		
		board[R1][C] = 4;
		whitePieces[3] = new Piece(R1, C, Piece.BISHOP, Piece.WHITE);
		
		board[R1][F] = 5;
		whitePieces[4] = new Piece(R1, F, Piece.BISHOP, Piece.WHITE);
		
		board[R1][B] = 6;
		whitePieces[5] = new Piece(R1, B, Piece.KNIGHT, Piece.WHITE);
		
		board[R1][G] = 7;
		whitePieces[6] = new Piece(R1, G, Piece.KNIGHT, Piece.WHITE);
		
		board[R1][D] = 8;
		whitePieces[7] = new Piece(R1, D, Piece.KING, Piece.WHITE);
		
		
		// black pieces
		board[R8][E] = -1;
		blackPieces[0] = new Piece(R8, E, Piece.QUEEN, Piece.BLACK);
		
		board[R8][A] = -2;
		blackPieces[1] = new Piece(R8, A, Piece.ROOK, Piece.BLACK);
		
		board[R8][H] = -3;
		blackPieces[2] = new Piece(R8, H, Piece.ROOK, Piece.BLACK);
		
		board[R8][C] = -4;
		blackPieces[3] = new Piece(R8, C, Piece.BISHOP, Piece.BLACK);
		
		board[R8][F] = -5;
		blackPieces[4] = new Piece(R8, F, Piece.BISHOP, Piece.BLACK);
		
		board[R8][B] = -6;
		blackPieces[5] = new Piece(R8, B, Piece.KNIGHT, Piece.BLACK);
		
		board[R8][G] = -7;
		blackPieces[6] = new Piece(R8, G, Piece.KNIGHT, Piece.BLACK);
		
		board[R8][D] = -8;
		blackPieces[7] = new Piece(R8, D, Piece.KING, Piece.BLACK);
	}
	
	private ChessBoard(byte[][] b, Piece[] white, Piece[] black, boolean noKing) {
		this.board = b.clone();
		this.whitePieces = Piece.cloneArray(white);
		this.blackPieces = Piece.cloneArray(black);
		this.kingCaptured = noKing;
	}
	
	protected ChessBoard clone() {
		return new ChessBoard(this.board, this.whitePieces, this.blackPieces, this.kingCaptured);
	}
	
	public Piece get(byte rank, byte file) {
		int idx = board[rank][file];
		// check for no piece in that square
		if(idx == 0)
			return null;
		// return either the white piece or the black piece
		return idx > 0 ? whitePieces[idx - 1] : blackPieces[(idx*-1) - 1];
	}
	
	/**
	 * Use this method whenever a piece is captured and we need to remove it from
	 * the whitePieces or blackPieces array, this method also clears the spot on the board.
	 * 
	 * @param rank - rank on the board of the piece being captured 
	 * @param file - file on the board of the piece being captured
	 */
	private void capture(int rank, int file) {
		int idx = board[rank][file];
		
		// remove the entry in the white pieces array
		if(idx > 0) {
			// remove the piece from the pieces array
			Piece[] newArr = new Piece[whitePieces.length - 1];
			System.arraycopy(whitePieces, 0, newArr, 0, idx - 1);
			System.arraycopy(whitePieces, idx, newArr, idx - 1, whitePieces.length - idx);
			this.whitePieces = newArr;
		}
		else {
			// remove the piece from the pieces array
			Piece[] newArr = new Piece[blackPieces.length - 1];
			System.arraycopy(blackPieces, 0, newArr, 0, idx - 1);
			System.arraycopy(blackPieces, idx, newArr, idx - 1, blackPieces.length - idx);
			this.blackPieces = newArr;
		}
		// clear the space on the board where this piece used to be
		this.board[rank][file] = 0;
	}
	
	// don't handle any fancy moves yet
	// maybe return a new instance of the board?
	// TODO - handle promotions better?
	public void move(byte startRank, byte startFile, byte endRank, byte endFile) {
		// get the piece that is on the starting spot
		Piece startPiece = this.get(startRank, startFile);
		// get the piece that is on the ending spot
		Piece endPiece = this.get(endRank, endFile);
		
		// check for capturing a king
		if(endPiece != null && endPiece.getType() == Piece.KING)
			this.kingCaptured = true;
		
		// mark that the piece has moved - necessary for castling and en passant
		startPiece.setHasMoved((byte) 1);
		
		// if piece is captured, remove it from the list
		if(endPiece != null) {
			this.capture(endRank, endFile);
		}
		
		// check for special cases for pawns - en passant and promotions
		if(startPiece.getType() == Piece.PAWN) {
			// if the pawn moved up 2 ranks on first move, set has moved appropriately
			if(Math.abs(startRank - endRank) > 1) {
				startPiece.setHasMoved((byte) 2);
			}
			// en passant - the pawn moved diagonally but didn't capture the piece in that square
			else if(startFile != endFile && endPiece == null) {
				this.capture(startRank, endFile);
			}
			// TODO - need to handle other promotions if opponent decides to promote to something else
			//		  possibly have an overloaded method with promotion type to just handle this one instance?
			else if(endRank == ChessBoard.R1 || endRank == ChessBoard.R8) {
				startPiece.setType(Piece.QUEEN);
			}
		}
		// check for castling - moved 2 files
		else if(startPiece.getType() == Piece.KING) {
			// king side castling
			if(startFile == D && endFile == B) {
				// move the rook too
				Piece rook = this.get(startRank, C);
				rook.updatePosition(startRank, C);
				this.board[startRank][C] = this.board[startRank][A];
				this.board[startRank][A] = 0;
			}
			// queen side castle
			else if(startFile == D && endFile == F) {
				// move the rook too
				Piece rook = this.get(startRank, C);
				rook.updatePosition(startRank, E);
				this.board[startRank][E] = this.board[startRank][H];
				this.board[startRank][H] = 0;
			}
		}
		
		// move the piece from the starting spot to the ending spot
		startPiece.updatePosition(endRank, endFile);
		// finally update the board
		this.board[endRank][endFile] = this.board[startRank][startFile];
		// clear out the starting spot since the piece is being moved from there
		this.board[startRank][startFile] = 0;
	}
	
	/**
	 * Method to return a move string that the server can use.
	 * 
	 * @param startRank - starting rank of the piece
	 * @param startFile - starting file of the piece
	 * @param endRank - ending rank of the piece
	 * @param endFile - ending file of the piece
	 * @return string - string in the form of Nh1h6 or Pb7b8Q
	 */
	public String getMoveString(byte startRank, byte startFile, byte endRank, byte endFile) {
		String str = this.get(startRank, startFile).toString();
		String moveString = str + getFile(startFile) + getRank(startRank) + getFile(endFile) + getRank(endRank) + "";
		// add extra char for queening
		if(str.equals("P") && (endRank == ChessBoard.R1 || endRank == ChessBoard.R8))
			str += "Q";
		return moveString;
	}
	
	// check all possible enemy moves to see if they can attack the location
	public boolean isUnderAttack(byte rank, byte file, byte color) {
		Piece[] pieces = whitePieces;
		if(color == Piece.WHITE)
			pieces = blackPieces;
		// go through each piece the enemy has and check if it can move into the square
		for(Piece pos : pieces) {
			for(byte[] attackLocations : pos.getPossibleMoves(this)) {
				// the square can be attacked
				if(rank == attackLocations[0] && file == attackLocations[1])
					return true;
			}
		}
		return false;
	}
	
	// checkmate (one player doesn't have a king..pretty crude for now)
	public boolean terminalTest() {
		// don't know that is where the king's piece is since the array shrinks as pieces are captured
		//return (whitePieces[KING] == null || blackPieces[KING] == null);
		return this.kingCaptured;
	}
	
	/**
	 * Method to return the list of all legal moves for a given color,
	 * used for move generation when searching.
	 * 
	 * @param color - color for the moves we want to calculate
	 * @return SortedMap<Integer, ArrayList<byte[]>> - key/value pair of piece
	 * 		   location as (rank*10+file) and a list of their moves 
	 */
	public SortedMap<Integer, ArrayList<byte[]>> getAllLegalMoves(byte color) {
		// our list of legal moves will be a key/value list where the key is the rank*10 + file
		// of the piece and the value is the list of moves that piece can perform in a byte[]
		// that way we know what moves each piece can perform 
		SortedMap<Integer, ArrayList<byte[]>> moves = new TreeMap<Integer, ArrayList<byte[]>>();
		
		if(color == Piece.WHITE) {
			for(Piece p : whitePieces) {
				int key = p.getRank() * 10 + p.getFile();
				moves.put(key, p.getPossibleMoves(this));
			}
		}
		else {
			for(Piece p : blackPieces) {
				int key = p.getRank() * 10 + p.getFile();
				moves.put(key, p.getPossibleMoves(this));
			}
		}
		
		return moves;
	}
	
	/**
	 * Returns the number of legal moves for the given player, used for calculating the utility.
	 * 
	 * @param color - which color we want to count moves for
	 * @return int - number of legal moves for the given color
	 */
	public int countLegalMoves(byte color) {
		int count = 0;
		
		if(color == Piece.WHITE) {
			for(Piece p : whitePieces) {
				count += p.getPossibleMoves(this).size();
			}
		}
		else {
			for(Piece p : blackPieces) {
				count += p.getPossibleMoves(this).size();
			}
		}
		
		return count;
	}
	
	public double Utility() {
		// TODO - need to calculate the number of legal moves for each side
		//      - might be better to keep a running tally and only subtract from it when a capture takes place,
		//      - would also be good to keep total number of moves available 
		
		int blackKings = 0, blackQueens = 0, blackRooks = 0, blackBishops = 0, blackKnights = 0, blackPawns = 0,
			whiteKings = 0, whiteQueens = 0, whiteRooks = 0, whiteBishops = 0, whiteKnights = 0, whitePawns = 0;
		
		// count the white pieces
		for(int i = 0; i < this.whitePieces.length; i++) {
			switch(whitePieces[i].getType()) {
				case Piece.KING:
					whiteKings++;
					break;
				case Piece.QUEEN:
					whiteQueens++;
					break;
				case Piece.ROOK:
					whiteRooks++;
					break;
				case Piece.BISHOP:
					whiteBishops++;
					break;
				case Piece.KNIGHT:
					whiteKnights++;
					break;
				case Piece.PAWN:
					whitePawns++;
					break;
			}
		}
		
		// count the black pieces
		for(int i = 0; i < this.blackPieces.length; i++) {
			switch(blackPieces[i].getType()) {
				case Piece.KING:
					blackKings++;
					break;
				case Piece.QUEEN:
					blackQueens++;
					break;
				case Piece.ROOK:
					blackRooks++;
					break;
				case Piece.BISHOP:
					blackBishops++;
					break;
				case Piece.KNIGHT:
					blackKnights++;
					break;
				case Piece.PAWN:
					blackPawns++;
					break;
			}
		}
		
		/* Evaluation function
		 * 
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
		
		// here is where we use ourColor to determine how we calculate the utility
		double util;
		if(ServerAPI.getOurColor() == Piece.WHITE) {
			util = 200 * (whiteKings - blackKings) +
					9 * (whiteQueens - blackQueens) +
					5 * (whiteRooks - blackRooks) +
					3 * (whiteBishops - blackBishops + whiteKnights - blackKnights) +
					1 * (whitePawns - blackPawns) +
					/*-0.5 * () +*/
					0.1 * (this.countLegalMoves(ServerAPI.getOurColor()) - this.countLegalMoves(ServerAPI.getOppontentColor()));
		}
		else {
			util = 200 * (blackKings - whiteKings) +
					9 * (blackQueens - whiteQueens) +
					5 * (blackRooks - whiteRooks) +
					3 * (blackBishops - whiteBishops + blackKnights - whiteKnights) +
					1 * (blackPawns - whitePawns) +
					/*-0.5 * () +*/
					0.1 * (this.countLegalMoves(ServerAPI.getOurColor()) - this.countLegalMoves(ServerAPI.getOppontentColor()));
		}
		
		return util;
	}
	
	
	/**
	 * Convert the constant back to a character (a-h)
	 * 
	 * @param i - constant byte value
	 * @return char equivalent
	 */
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
	
	/**
	 * Convert the char to a constant byte.
	 *  
	 * @param c - char to be translated
	 * @return byte representation
	 */
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
	
	/**
	 * Convert from constant (0-7) to standard repr. for chess (1-8)
	 * 
	 * @param i - byte value
	 * @return byte - chess board representation
	 */
	public static byte getRank(byte i) {
		return (byte) (i - 1);
	}
	
	@Override
	public String toString() {
		String str = "";
		for(byte rank = R1; rank <= R8; rank++) {
			for(byte file = A; file <= H; file++) {
				if(this.board[rank][file] != 0) {
					Piece chessPiece = this.get(rank, file); 
					if(chessPiece.getColor() == Piece.WHITE)
						str += "W";
					else
						str += "B";
					str += chessPiece.toString() + " ";
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
