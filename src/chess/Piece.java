package chess;

import java.util.ArrayList;

public class Piece implements Cloneable {
	// constants to reference the type of piece
	public static final byte PAWN = 0;
	public static final byte KNIGHT = 1;
	public static final byte BISHOP = 2;
	public static final byte ROOK = 3;
	public static final byte QUEEN = 4;
	public static final byte KING = 5;
	
	public static final byte WHITE = 1;
	public static final byte BLACK = -1;
	
	private byte rank, file;
	private byte type;		// pawn, knight, etc
	private byte color;		// white or black
	private byte hasMoved;	// 0 for false, 1 for true, 2 for pawn moving 2 spaces on first turn
	
	public Piece(byte type, byte color) {
		this.type = type;
		this.color = color;
		this.hasMoved = 0;
	}
	
	public Piece(byte rank, byte file, byte type, byte color) {
		this.rank = rank;
		this.file = file;
		this.type = type;
		this.color = color;
		this.hasMoved = 0;
	}
	
	private Piece(byte rank, byte file, byte type, byte color, byte moved) {
		this.rank = rank;
		this.file = file;
		this.type = type;
		this.color = color;
		this.hasMoved = moved;
	}
	
	private Piece clonePiece() {
		return new Piece(this.rank, this.file, this.type, this.color, this.hasMoved);
	}
	
	protected static Piece[] cloneArray(Piece[] p) {
		Piece[] newArray = new Piece[p.length];
		for(int i = 0; i < p.length; i++) {
			newArray[i] = p[i] == null ? null : p[i].clonePiece();
		}
		return newArray;
	}
	
	public byte getRank() {
		return this.rank;
	}
	
	public void setRank(byte r) {
		this.rank = r;
	}
	
	public byte getFile() {
		return this.file;
	}
	
	public void setFile(byte f) {
		this.file = f;
	}
	
	/**
	 * Updates the piece's position when a move is made on the board.
	 * 
	 * @param rank - new rank position
	 * @param file - new file position
	 */
	public void updatePosition(byte rank, byte file) {
		this.rank = rank;
		this.file = file;
	}
	
	public byte getType() {
		return this.type;
	}
	
	public void setType(byte t) {
		this.type = t;
	}
	
	public byte getColor() {
		return this.color;
	}
	
	public void setColor(byte c) {
		this.color = c;
	}
	
	public byte getHasMoved() {
		return this.hasMoved;
	}
	
	public void setHasMoved(byte b) {
		this.hasMoved = b;
	}
	
	/**
	 * Get the positions of the piece for all possible moves it can make.
	 *  
	 * @param board - given state of the chess board
	 * @return ArrayList<byte[]> - list of [rank, file] moves 
	 */
	public ArrayList<byte[]> getPossibleMoves(ChessBoard board) {
		switch(type) {
			case PAWN:
				return getPawnMoves(board);
			case KNIGHT:
				return getKnightMoves(board);
			case BISHOP:
				return getBishopMoves(board);
			case ROOK:
				return getRookMoves(board);
			case QUEEN:
				return getQueenMoves(board);
			default:
				return getKingMoves(board);
		}
	}

	/**
	 * A king can move into any adjacent square
	 * 
	 * @param board - chess board we are searching
	 * @return list of moves that this king piece can move to as [rank, file]
	 * 		   where rank and file = [0,7]
	 */
	private ArrayList<byte[]> getKingMoves(ChessBoard board) {
		ArrayList<byte[]> moves = new ArrayList<byte[]>();

		for(byte rank = (byte) (this.rank - 1); rank <= this.rank + 1; rank++) {
			for(byte file = (byte) (this.file - 1); file <= this.file + 1; file++) {
				// make sure it's on the board
				if(rank >= ChessBoard.R1 && rank <= ChessBoard.R8 && file >= ChessBoard.A && file <= ChessBoard.H) {
					//p = board.get(rank, file);
					// can move into an empty space or take out an opponent
					if(board.isSpotEmptyOrCapturable(rank, this.file, this.color))
						moves.add(new byte[]{rank, file});
				}
			}
		}
		// castling - king must not have moved yet
		if(this.hasMoved == 0) {
			// check for queen-side castling
			Piece p = board.get(this.rank, ChessBoard.A);
			// rook must not have been moved already
			if(p != null && p.type == ROOK && p.hasMoved == 0) {
				// all spaces in between must be open
				if(board.isSpotEmpty(this.rank, ChessBoard.B)
						&& board.isSpotEmpty(this.rank, ChessBoard.C)
						&& board.isSpotEmpty(this.rank, ChessBoard.D)) {
					// the spaces in between must not be under attack
					// and we can't move out of check
					if(!board.isUnderAttack(this.rank, ChessBoard.B, this.color)
							&& !board.isUnderAttack(this.rank, ChessBoard.C, this.color)
							&& !board.isUnderAttack(this.rank, ChessBoard.D, this.color)
							&& !board.isUnderAttack(this.rank, ChessBoard.E, this.color)) {
						moves.add(new byte[]{this.rank, ChessBoard.C});
					}
				}
			}
			
			// king-side castling
			p = board.get(this.rank, ChessBoard.H);
			// rook must not have been moved already
			if(p != null && p.type == ROOK && p.hasMoved == 0) {
				// all spaces in between must be open
				if(board.isSpotEmpty(this.rank, ChessBoard.F) && board.isSpotEmpty(this.rank, ChessBoard.G)) {
					// the spaces in between must not be under attack
					// and we can't move out of check
					if(!board.isUnderAttack(this.rank, ChessBoard.F, this.color)
							&& !board.isUnderAttack(this.rank, ChessBoard.G, this.color)
							&& !board.isUnderAttack(this.rank, ChessBoard.E, this.color)) {
						moves.add(new byte[]{this.rank, ChessBoard.G});
					}
				}
			}
		}
		return moves;	// TODO - how do we know to move the knight??
	}

	/**
	 * A queen can move like a rook or a bishop (horizontal, vertical, diagonal).
	 * 
	 * @param board - chess board we are searching
	 * @return list of moves that this queen piece can move to as [rank, file]
	 * 		   where rank and file = [0,7]
	 */
	private ArrayList<byte[]> getQueenMoves(ChessBoard board) {
		ArrayList<byte[]> moves = getRookMoves(board);
		moves.addAll(getBishopMoves(board));
		return moves;
	}

	/**
	 * A rook can move any number of spaces horizontally or vertically.
	 * 
	 * @param board - chess board we are searching
	 * @return list of moves that this rook piece can move to as [rank, file]
	 * 		   where rank and file = [0,7]
	 */
	private ArrayList<byte[]> getRookMoves(ChessBoard board) {
		ArrayList<byte[]> moves = new ArrayList<byte[]>();
		Piece p;
		
		// get moves along same file going down
		for(byte r = (byte) (this.rank - 1); r >= ChessBoard.R1; r--) {
			p = board.get(r, this.file);
			// check if its empty spot
			if(p == null)
				moves.add(new byte[] {r, this.file});
			// check if we can capture this piece then break
			else if(p.color != this.color) {
				moves.add(new byte[] {r, this.file});
				break;
			}
			// otherwise we hit our own piece, break
			else
				break;
		}
		
		// get moves along same file going up
		for(byte r = (byte) (this.rank + 1); r <= ChessBoard.R8; r++) {
			p = board.get(r, this.file);
			if(p == null)
				moves.add(new byte[] {r, this.file});
			else if(p.color != this.color) {
				moves.add(new byte[] {r, this.file});
				break;
			}
			else
				break;
		}
		
		// get moves along same rank going left
		for(byte f = (byte) (this.file - 1); f >= ChessBoard.A; f--) {
			p = board.get(this.rank, f);
			if(p == null)
				moves.add(new byte[] {this.rank, f});
			else if(p.color != this.color) {
				moves.add(new byte[] {this.rank, f});
				break;
			}
			else
				break;
		}
		
		// get moves along same file going right
		for(byte f = (byte) (this.file + 1); f <= ChessBoard.H; f++) {
			p = board.get(this.rank, f);
			if(p == null)
				moves.add(new byte[] {this.rank, f});
			else if(p.color != this.color) {
				moves.add(new byte[] {this.rank, f});
				break;
			}
			else
				break;
		}
		
		return moves;
	}
	
	/**
	 * A bishop can move any number of spaces along a diagonal.
	 * 
	 * @param board - chess board we are searching
	 * @return list of moves that this bishop piece can move to as [rank, file]
	 * 		   where rank and file = [0,7]
	 */
	private ArrayList<byte[]> getBishopMoves(ChessBoard board) {
		ArrayList<byte[]> moves = new ArrayList<byte[]>();
		Piece p;
		
		// bottom-left diagonal
		byte rank = (byte) (this.rank - 1);
		byte file = (byte) (this.file - 1);
		while(rank >= 0 && file >= 0) {
			p = board.get(rank, file);
			if(p == null)
				moves.add(new byte[] {rank, file});
			else if(p.color != this.color) {
				moves.add(new byte[] {rank, file});
				break;
			}
			else
				break;
			rank--;
			file--;
		}
		
		// top-right diagonal
		rank = (byte) (this.rank + 1);
		file = (byte) (this.file + 1);
		while(rank < 8 && file < 8) {
			p = board.get(rank, file);
			if(p == null)
				moves.add(new byte[] {rank, file});
			else if(p.color != this.color) {
				moves.add(new byte[] {rank, file});
				break;
			}
			else
				break;
			rank++;
			file++;
		}
		
		// bottom-right diagonal
		rank = (byte) (this.rank - 1);
		file = (byte) (this.file + 1);
		while(rank >= 0 && file < 8) {
			p = board.get(rank, file);
			if(p == null)
				moves.add(new byte[] {rank, file});
			else if(p.color != this.color) {
				moves.add(new byte[] {rank, file});
				break;
			}
			else
				break;
			rank--;
			file++;
		}
		
		// top-left diagonal
		rank = (byte) (this.rank + 1);
		file = (byte) (this.file - 1);
		while(rank < 8 && file >= 0) {
			p = board.get(rank, file);
			if(p == null)
				moves.add(new byte[] {rank, file});
			else if(p.color != this.color) {
				moves.add(new byte[] {rank, file});
				break;
			}
			else
				break;
			rank++;
			file--;
		}
		
		return moves;
	}
	
	/**
	 * Can move in an L shape.
	 * 
	 * @param board - chess board we are searching
	 * @return list of moves that this knight piece can move to as [rank, file]
	 * 		   where rank and file = [0,7]
	 */
	private ArrayList<byte[]> getKnightMoves(ChessBoard board) {
		ArrayList<byte[]> moves = new ArrayList<byte[]>();
		
		// 1x2 moves
		// down one, left two
		byte rank = (byte) (this.rank - 1);
		byte file = (byte) (this.file - 2);
		Piece p;
		if(rank >= 0 && file >= 0) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		
		// up one, right two
		rank = (byte) (this.rank + 1);
		file = (byte) (this.file + 2);
		if(rank < 8 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		
		// down one, right two
		rank = (byte) (this.rank - 1);
		file = (byte) (this.file + 2);
		if(rank >= 0 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		
		// up one, left two
		rank = (byte) (this.rank + 1);
		file = (byte) (this.file - 2);
		if(rank >= 0 && rank < 8 && file >= 0 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		
		
		// 2x1 moves
		// down two, left one
		rank = (byte) (this.rank - 2);
		file = (byte) (this.file - 1);
		if(rank >= 0 && file >= 0) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		
		// up two, right one
		rank = (byte) (this.rank + 2);
		file = (byte) (this.file + 1);
		if(rank < 8 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		
		// down two, right one
		rank = (byte) (this.rank - 2);
		file = (byte) (this.file + 1);
		if(rank >= 0 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		
		// up two, left one
		rank = (byte) (this.rank + 2);
		file = (byte) (this.file - 1);
		if(rank >= 0 && rank < 8 && file >= 0 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		
		return moves;
	}
	
	/**
	 * Can move one space ahead, 2 if in starting position, or capture diagonally.
	 * 
	 * @param board - chess board we are searching
	 * @return list of moves that this pawn piece can move to as [rank, file]
	 * 		   where rank and file = [0,7]
	 */
	private ArrayList<byte[]> getPawnMoves(ChessBoard board) {
		ArrayList<byte[]> moves = new ArrayList<byte[]>();
		
		// white moves up (towards 7) and black moves down (towards 0) - remember black is -1 and white is +1
		byte forward = BLACK;
		byte limit = ChessBoard.R1 - 1;
		
		// switch rank
		if(this.color == WHITE) {
			forward = WHITE;
			limit = ChessBoard.R8 + 1;
		}
		
		// check for attacking moves - left diagonal
		if(this.rank + forward != limit && this.file - 1 >= ChessBoard.A) {
			
			if(board.isSpotCapturable((byte) (this.rank + forward), (byte)(this.file - 1), this.color)) {
				moves.add(new byte[] {(byte) (this.rank + forward), (byte) (this.file - 1)});
			}
		}
		// check for attacking moves - right diagonal
		if(this.rank + forward != limit && this.file + 1 <= ChessBoard.H) {
			
			if(board.isSpotCapturable((byte) (this.rank + forward), (byte) (this.file + 1), this.color)) {
				moves.add(new byte[] {(byte) (this.rank + forward), (byte) (this.file + 1)});
			}
		}
		
		// move forward
		if(this.rank + forward != limit && board.isSpotEmpty((byte) (this.rank + forward), this.file)) {
			moves.add(new byte[] {(byte) (this.rank + forward), this.file});
		}
		
		if(this.color == WHITE) {
			// move two spaces on first turn
			if(this.rank == ChessBoard.R2){
				// have to check two spaces up and space directly in front
				if(board.isSpotEmpty(ChessBoard.R3, this.file) && board.isSpotEmpty(ChessBoard.R4, this.file))
					moves.add(new byte[] {ChessBoard.R4, this.file});
			}
			// en passant, make sure the last moved piece was a pawn and is on the same rank - a requirement of en passant
			else if(this.rank == ChessBoard.R5 && this.rank == SearchUtils.lastMove[0] && SearchUtils.lastMove[4] == PAWN) {
				byte lastOpponentMoveRank = SearchUtils.lastMove[2];
				byte lastOpponentMoveFile = SearchUtils.lastMove[3];
				// make sure the last move is not default values
				if(SearchUtils.lastMove[0] != SearchUtils.lastMove[2] &&
						SearchUtils.lastMove[1] != SearchUtils.lastMove[3] && SearchUtils.lastMove[0] != SearchUtils.lastMove[1]) {
					Piece p = board.get(lastOpponentMoveRank, lastOpponentMoveFile);
					// last move was a pawn advancing 2 spaces in the file to the left of right of this piece
					if(p != null && p.hasMoved > 1 && (p.getFile() == this.file + 1 || p.getFile() == this.file - 1)) {
						moves.add(new byte[]{ChessBoard.R6, lastOpponentMoveFile});
					}
				}
			}
		}
		// this.color == BLACK
		else {
			if(this.rank == ChessBoard.R7){
				// have to check two spaces up and space directly in front
				if(board.isSpotEmpty(ChessBoard.R6, this.file) && board.isSpotEmpty(ChessBoard.R5, this.file))
					moves.add(new byte[]{ChessBoard.R5, this.file});
			}
			// en passant, make sure the last moved piece was a pawn and is on the same rank - a requirement of en passant
			else if(this.rank == ChessBoard.R4 && this.rank == SearchUtils.lastMove[0] && SearchUtils.lastMove[4] == PAWN) {
				byte lastOpponentMoveRank = SearchUtils.lastMove[2];
				byte lastOpponentMoveFile = SearchUtils.lastMove[3];
				// make sure the last move is not default values
				if(SearchUtils.lastMove[0] != SearchUtils.lastMove[2] &&
						SearchUtils.lastMove[1] != SearchUtils.lastMove[3] && SearchUtils.lastMove[0] != SearchUtils.lastMove[1]) {
					Piece p = board.get(lastOpponentMoveRank, lastOpponentMoveFile);
					// last move was a pawn advancing 2 spaces in the file to the left of right of this piece
					if(p != null && p.hasMoved > 1 && (p.getFile() == (this.file + 1) || p.getFile() == (this.file - 1))) {
						moves.add(new byte[]{ChessBoard.R3, lastOpponentMoveFile});
					}
				}
			}
		}
		
		return moves;
	}
	
	// char for the piece type - {P, N, B, R, Q, K}
	@Override
	public String toString() {
		switch(type) {
			case PAWN:
				return "P";
			case KNIGHT:
				return "N";
			case BISHOP:
				return "B";
			case ROOK:
				return "R";
			case QUEEN:
				return "Q";
			default:
				return "K";
		}
	}
	
	/**
	 * Translates the piece type from a character {P, N, B, R, Q, K}
	 * to its numeric, byte representation.  Used when we parse a move
	 * string and need to determine the internal piece type representation.
	 * 
	 * @param c char - character representation of the piece
	 * @return byte representation of the piece
	 */
	public static byte getNumericType(char c) {
		switch(c) {
			case 'P':
				return PAWN;
			case 'N':
				return KNIGHT;
			case 'B':
				return BISHOP;
			case 'R':
				return ROOK;
			case 'Q':
				return QUEEN;
			default:
				return KING;
		}
	}
	
	public static char getCharType(byte b) {
		switch(b) {
			case PAWN:
				return 'P';
			case KNIGHT:
				return 'N';
			case BISHOP:
				return 'B';
			case ROOK:
				return 'R';
			case QUEEN:
				return 'Q';
			default:
				return 'K';
		}
	}
	
}
