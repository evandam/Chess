package chess;

import java.io.Serializable;
import java.util.ArrayList;

public class Piece implements Cloneable, Serializable {
	// constants to reference the type of piece
	public static final byte PAWN = 0;
	public static final byte KNIGHT = 1;
	public static final byte BISHOP = 2;
	public static final byte ROOK = 3;
	public static final byte QUEEN = 4;
	public static final byte KING = 5;
	
	public static final byte WHITE = 0;
	public static final byte BLACK = 1;
	
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
	
	@Override
	protected Piece clone() {
		return new Piece(this.rank, this.file, this.type, this.color);
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

	// A king can move into any adjacent square
	// look into castling?
	private ArrayList<byte[]> getKingMoves(ChessBoard board) {
		ArrayList<byte[]> moves = new ArrayList<byte[]>();
		Piece p;
		byte rankPos = this.rank, filePos = this.file;
		for(byte rank = (byte) (rankPos - 1); rank <= rankPos + 1; rank++) {
			for(byte file = (byte) (filePos - 1); file <= filePos + 1; file++) {
				// make sure it's on the board
				if(rank >= ChessBoard.R1 && rank < ChessBoard.R8 && file >= ChessBoard.A && file < ChessBoard.H) {
					p = board.get(rank, file);
					// can move into an empty space or take out an opponent
					if(p == null || p.color != this.color)
						moves.add(new byte[]{rank, file});
				}
			}
		}
		// castling
		// king must not have moved yet
		if(this.hasMoved == 0) {
			// check for king-side castling
			p = board.get(rankPos, ChessBoard.A);
			// rook must not have been moved already
			if(p != null && p.type == ROOK && p.hasMoved == 0) {
				// all spaces in between must be open
				if(board.get(rankPos, ChessBoard.B) == null && board.get(rankPos, ChessBoard.C) == null) {
					// the spaces in between must not be under attack
					if(!board.isUnderAttack(rankPos, ChessBoard.C, this.color) && !board.isUnderAttack(rankPos, ChessBoard.B, this.color))
						moves.add(new byte[]{rankPos, ChessBoard.B});
				}
			}
			
			// queen-side castling
			p = board.get(rankPos, ChessBoard.H);
			// rook must not have been moved already
			if(p != null && p.type == ROOK && p.hasMoved == 0) {
				// all spaces in between must be open
				if(board.get(rankPos, ChessBoard.E) == null && board.get(rankPos, ChessBoard.F) == null) {
					// the spaces in between must not be under attack
					if(!board.isUnderAttack(rankPos, ChessBoard.E, this.color) && !board.isUnderAttack(rankPos, ChessBoard.F, this.color))
						moves.add(new byte[]{rankPos, ChessBoard.F});
				}
			}
		}
		return moves;
	}

	// A queen can move like a rook or a bishop (horizontal, vertical, diagonal)
	private ArrayList<byte[]> getQueenMoves(ChessBoard board) {
		ArrayList<byte[]> moves = getRookMoves(board);
		moves.addAll(getBishopMoves(board));
		return moves;
	}

	// A rook can move any number of spaces horizontally or vertically
	private ArrayList<byte[]> getRookMoves(ChessBoard board) {
		ArrayList<byte[]> moves = new ArrayList<byte[]>();
		Piece p;
		byte rankPos = this.rank, filePos = this.file;
		// get moves along same file going down
		for(byte r = (byte) (rankPos - 1); r >= 0; r--) {
			p = board.get(r, filePos);
			if(p == null) 
				moves.add(new byte[] {r, filePos});
			else if(p.color != this.color) {
				moves.add(new byte[] {r, filePos});
				break;
			}
			else
				break;
		}
		// get moves along same file going up
		for(byte r = (byte) (rankPos + 1); r < 8; r++) {
			p = board.get(r, filePos);
			if(p == null) 
				moves.add(new byte[] {r, filePos});
			else if(p.color != this.color) {
				moves.add(new byte[] {r, filePos});
				break;
			}
			else
				break;
		}
		// get moves along same rank going left
		for(byte f = (byte) (filePos - 1); f >= 0; f--) {
			p = board.get(rankPos, f);
			if(p == null) 
				moves.add(new byte[] {rankPos, f});
			else if(p.color != this.color) {
				moves.add(new byte[] {rankPos, f});
				break;
			}
			else
				break;
		}
		// get moves along same file going right
		for(byte f = (byte) (filePos + 1); f < 8; f++) {
			p = board.get(rankPos, f);
			if(p == null) 
				moves.add(new byte[] {rankPos, f});
			else if(p.color != this.color) {
				moves.add(new byte[] {rankPos, f});
				break;
			}
			else
				break;
		}
		return moves;
	}

	// A bishop can move any number of spaces along a diagonal
	private ArrayList<byte[]> getBishopMoves(ChessBoard board) {
		ArrayList<byte[]> moves = new ArrayList<byte[]>();
		Piece p;
		byte rankPos = this.rank, filePos = this.file;
		// top-left diagonal
		byte rank = (byte) (rankPos - 1);
		byte file = (byte) (filePos - 1);
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
		// bottom-right diagonal
		rank = (byte) (rankPos + 1);
		file = (byte) (filePos + 1);
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
		// top-right diagonal
		rank = (byte) (rankPos - 1);
		file = (byte) (filePos + 1);
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
		// bottom-left diagonal
		rank = (byte) (rankPos + 1);
		file = (byte) (filePos - 1);
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

	// An "L" shape, need to hardcode each move I think
	private ArrayList<byte[]> getKnightMoves(ChessBoard board) {
		ArrayList<byte[]> moves = new ArrayList<byte[]>();
		// 1x2 moves
		byte rankPos = this.rank, filePos = this.file;
		byte rank = (byte) (rankPos - 1);
		byte file = (byte) (filePos - 2);
		Piece p;
		if(rank >= 0 && file >= 0) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		rank = (byte) (rankPos + 1);
		file = (byte) (filePos + 2);
		if(rank < 8 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		rank = (byte) (rankPos - 1);
		file = (byte) (filePos + 2);
		if(rank >= 0 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		rank = (byte) (rankPos + 1);
		file = (byte) (filePos - 2);
		if(rank >= 0 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}		
		// 2x1 moves
		rank = (byte) (rankPos - 2);
		file = (byte) (filePos - 1);
		if(rank >= 0 && file >= 0) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		rank = (byte) (rankPos + 2);
		file = (byte) (filePos + 1);
		if(rank < 8 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		rank = (byte) (rankPos - 2);
		file = (byte) (filePos + 1);
		if(rank >= 0 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		rank = (byte) (rankPos + 2);
		file = (byte) (filePos - 1);
		if(rank >= 0 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}		
		return moves;
	}

	// can move one space ahead, 2 if in starting position
	// captures diagonally - figure out en passant captures
	private ArrayList<byte[]> getPawnMoves(ChessBoard board) {
		ArrayList<byte[]> moves = new ArrayList<byte[]>();
		// white moves up (towards 0) and black moves down(towards 7)
		byte forward = 1;
		byte limit = 8;
		if(this.color == WHITE) {		// switch rank
			forward = -1;
			limit = -1;
		}
		byte rankPos = this.rank, filePos = this.file;
		Piece p;
		// check for attacking moves - diagonal					// TODO check if in row for en passant and check last move
		if(rankPos + forward != limit && filePos - 1 >= 0) {
			p = board.get((byte) (rankPos + forward), (byte) (filePos - 1));

			if(p != null && p.color != p.color) {
				moves.add(new byte[] {(byte) (rankPos + forward), (byte) (filePos - 1)});
			}
		}
		if(rankPos + forward != limit && filePos + 1 < 8) {
			p = board.get((byte) (rankPos + forward), (byte) (filePos + 1));
			if(p != null && p.color != p.color) {
				moves.add(new byte[] {(byte) (rankPos + forward), (byte) (filePos + 1)});
			}
		}
		// move forward
		if(rankPos + forward != limit) {
			p = board.get((byte) (rankPos + forward), filePos);
			if(p == null)
				moves.add(new byte[] {(byte) (rankPos + forward), filePos});
		}
		// move two spaces on first turn
		if(this.color == WHITE) {
			if(rankPos == ChessBoard.R2){
				p = board.get((byte) (ChessBoard.R4), filePos);
				if(p == null)
					moves.add(new byte[]{ChessBoard.R4, filePos});
			}
			// en passant
			else if(rankPos == ChessBoard.R5 && ServerAPI.getLastMovedPiece() == PAWN) {
				byte[] lastmove = ServerAPI.getLastEndPos();
				if(lastmove != null) {
					p = board.get(lastmove[0], lastmove[1]);
					// last move was a pawn advancing 2 spaces
					if(p != null && p.hasMoved > 1) {
						moves.add(new byte[]{ChessBoard.R6, lastmove[1]});
					}
				}
			}
		}
		else {
			if(rankPos == ChessBoard.R7){
				p = board.get((byte) (ChessBoard.R5), filePos);
				if(p == null)
					moves.add(new byte[]{ChessBoard.R5, filePos});
			}
			// en passant
			else if(rankPos == ChessBoard.R4 && ServerAPI.getLastMovedPiece() == PAWN) {
				byte[] lastmove = ServerAPI.getLastEndPos();
				if(lastmove != null) {
					p = board.get(lastmove[0], lastmove[1]);
					// last move was a pawn advancing 2 spaces
					if(p != null && p.hasMoved > 1) {
						moves.add(new byte[]{ChessBoard.R3, lastmove[1]});
					}
				}
			}
		}			
		
		return moves;
	}
	
	// char for the piece type - {P, N, B, R, Q, K}
	@Override
	public String toString() {
		String str = "";
		switch(type) {
			case PAWN:
				str += "P";
				break;
			case KNIGHT:
				str += "N";
				break;
			case BISHOP:
				str += "B";
				break;
			case ROOK:
				str += "R";
				break;
			case QUEEN:
				str += "Q";
				break;
			default:
				str += "K";
				break;
		}
		return str;
	}
	
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
}
