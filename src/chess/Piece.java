package chess;

import java.util.ArrayList;

public class Piece {
	// constants to reference the type of piece
	public static final byte PAWN = 0;
	public static final byte KNIGHT = 1;
	public static final byte BISHOP = 2;
	public static final byte ROOK = 3;
	public static final byte QUEEN = 4;
	public static final byte KING = 5;
	
	public static final byte WHITE = 0;
	public static final byte BLACK = 1;
	
	// TODO: merge all of these into one byte
	public byte type;	// pawn, knight, etc
	public byte color;	// white or black
	public byte hasMoved;	// 0 for false, 1 for true, 2 for pawn moving 2 spaces on first turn
	
	public Piece(byte type, byte color) {
		this.type = type;
		this.color = color;
		this.hasMoved = 0;
	}
		
	// get the positions of the piece for all possible moves it can make
	public ArrayList<byte[]> getPossibleMoves(Board board, byte[] pos) {
		switch(type) {
			case PAWN:
				return getPawnMoves(board, pos);
			case KNIGHT:
				return getKnightMoves(board, pos);
			case BISHOP:
				return getBishopMoves(board, pos);
			case ROOK:
				return getRookMoves(board, pos);
			case QUEEN:
				return getQueenMoves(board, pos);
			default:
				return getKingMoves(board, pos);
		}
	}

	// A king can move into any adjacent square
	// look into castling?
	private ArrayList<byte[]> getKingMoves(Board board, byte[] pos) {
		ArrayList<byte[]> moves = new ArrayList<byte[]>();
		Piece p;
		for(byte rank = (byte) (pos[0] - 1); rank <= (byte) (pos[0] + 1); rank++) {
			for(byte file = (byte) (pos[1] - 1); file <= (byte) (pos[1] + 1); file++) {
				// make sure it's on the board
				if(rank >= 0 && rank < 8 && file >= 0 && file < 8) {
					p = board.get(rank, file);
					// can move into an empty space or take out an opponent
					if(p == null || p.color != this.color)
						moves.add(new byte[]{rank, file});
				}
			}
		}
		// castling here
		return moves;
	}

	// A queen can move like a rook or a bishop (horizontal, vertical, diagonal)
	private ArrayList<byte[]> getQueenMoves(Board board, byte[] pos) {
		ArrayList<byte[]> moves = getRookMoves(board, pos);
		moves.addAll(getBishopMoves(board, pos));
		return moves;
	}

	// A rook can move any number of spaces horizontally or vertically
	private ArrayList<byte[]> getRookMoves(Board board, byte[] pos) {
		ArrayList<byte[]> moves = new ArrayList<byte[]>();
		Piece p;
		// get moves along same file going down
		for(byte r = (byte) (pos[0] - 1); r >= 0; r--) {
			p = board.get(r, pos[1]);
			if(p == null) 
				moves.add(new byte[] {r, pos[1]});
			else if(p.color != this.color) {
				moves.add(new byte[] {r, pos[1]});
				break;
			}
			else
				break;
		}
		// get moves along same file going up
		for(byte r = (byte) (pos[0] + 1); r < 8; r++) {
			p = board.get(r, pos[1]);
			if(p == null) 
				moves.add(new byte[] {r, pos[1]});
			else if(p.color != this.color) {
				moves.add(new byte[] {r, pos[1]});
				break;
			}
			else
				break;
		}
		// get moves along same rank going left
		for(byte f = (byte) (pos[1] - 1); f >= 0; f--) {
			p = board.get(pos[0], f);
			if(p == null) 
				moves.add(new byte[] {pos[0], f});
			else if(p.color != this.color) {
				moves.add(new byte[] {pos[0], f});
				break;
			}
			else
				break;
		}
		// get moves along same file going right
		for(byte f = (byte) (pos[1] + 1); f < 8; f++) {
			p = board.get(pos[0], f);
			if(p == null) 
				moves.add(new byte[] {pos[0], f});
			else if(p.color != this.color) {
				moves.add(new byte[] {pos[0], f});
				break;
			}
			else
				break;
		}
		return moves;
	}

	// A bishop can move any number of spaces along a diagonal
	private ArrayList<byte[]> getBishopMoves(Board board, byte[] pos) {
		ArrayList<byte[]> moves = new ArrayList<byte[]>();
		Piece p;
		// top-left diagonal
		byte rank = (byte) (pos[0] - 1);
		byte file = (byte) (pos[1] - 1);
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
		rank = (byte) (pos[0] + 1);
		file = (byte) (pos[1] + 1);
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
		rank = (byte) (pos[0] - 1);
		file = (byte) (pos[1] + 1);
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
		rank = (byte) (pos[0] + 1);
		file = (byte) (pos[1] - 1);
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
	private ArrayList<byte[]> getKnightMoves(Board board, byte[] pos) {
		ArrayList<byte[]> moves = new ArrayList<byte[]>();
		// 1x2 moves
		byte rank = (byte) (pos[0] - 1);
		byte file = (byte) (pos[1] - 2);
		Piece p;
		if(rank >= 0 && file >= 0) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		rank = (byte) (pos[0] + 1);
		file = (byte) (pos[1] + 2);
		if(rank < 8 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		rank = (byte) (pos[0] - 1);
		file = (byte) (pos[1] + 2);
		if(rank >= 0 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		rank = (byte) (pos[0] + 1);
		file = (byte) (pos[1] - 2);
		if(rank >= 0 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}		
		// 2x1 moves
		rank = (byte) (pos[0] - 2);
		file = (byte) (pos[1] - 1);
		if(rank >= 0 && file >= 0) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		rank = (byte) (pos[0] + 2);
		file = (byte) (pos[1] + 1);
		if(rank < 8 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		rank = (byte) (pos[0] - 2);
		file = (byte) (pos[1] + 1);
		if(rank >= 0 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}
		rank = (byte) (pos[0] + 2);
		file = (byte) (pos[1] - 1);
		if(rank >= 0 && file < 8) {
			p = board.get(rank, file);
			if(p == null || p.color != this.color)
				moves.add(new byte[] {rank, file});
		}		
		return moves;
	}

	// can move one space ahead, 2 if in starting position
	// captures diagonally - figure out en passant captures
	private ArrayList<byte[]> getPawnMoves(Board board, byte[] pos) {
		ArrayList<byte[]> moves = new ArrayList<byte[]>();
		// white moves up (towards 0) and black moves down(towards 7)
		byte forward = 1;
		byte limit = 8;
		if(this.color == WHITE) {
			forward = -1;
			limit = -1;
		}
		byte rank = pos[0];
		byte file = pos[1];
		Piece p;
		// check for attacking moves - diagonal
		if(rank + forward != limit && file - 1 >= 0) {
			p = board.get((byte) (rank + forward), (byte) (file - 1));
			if(p != null && p.color != this.color) {
				moves.add(new byte[] {(byte) (rank + forward), (byte) (file - 1)});
			}
		}
		if(rank + forward != limit && file + 1 < 8) {
			p = board.get((byte) (rank + forward), (byte) (file + 1));
			if(p != null && p.color != this.color) {
				moves.add(new byte[] {(byte) (rank + forward), (byte) (file + 1)});
			}
		}
		// move forward
		if(rank + forward != limit) {
			p = board.get((byte) (rank + forward), file);
			if(p == null)
				moves.add(new byte[] {(byte) (rank + forward), file});
		}
		// move two spaces on first turn
		if(this.color == WHITE) {
			if(rank == Board.R2){
				p = board.get((byte) (Board.R4), file);
				if(p == null)
					moves.add(new byte[]{Board.R4, file});
			}
			// en passant
			else if(rank == Board.R5 && ServerAPI.getLastMovedPiece() == PAWN) {
				byte[] lastmove = ServerAPI.getLastEndPos();
				p = board.get(lastmove[0], lastmove[1]);
				// last move was a pawn advancing 2 spaces
				if(p.hasMoved > 1) {
					moves.add(new byte[]{Board.R6, lastmove[1]});
				}
			}
		}
		else {
			if(rank == Board.R7){
				p = board.get((byte) (Board.R5), file);
				if(p == null)
					moves.add(new byte[]{Board.R5, file});
			}
			// en passant
			else if(rank == Board.R4 && ServerAPI.getLastMovedPiece() == PAWN) {
				byte[] lastmove = ServerAPI.getLastEndPos();
				p = board.get(lastmove[0], lastmove[1]);
				// last move was a pawn advancing 2 spaces
				if(p.hasMoved > 1) {
					moves.add(new byte[]{Board.R3, lastmove[1]});
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
	
	public static byte getType(char c) {
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
