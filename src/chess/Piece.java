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
	
	public byte type;	// pawn, knight, etc
	public byte color;	// white or black
	
	public Piece(byte type, byte color) {
		this.type = type;
		this.color = color;
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

	private ArrayList<byte[]> getKingMoves(Board board, byte[] pos) {
		ArrayList<byte[]> moves = new ArrayList<byte[]>();
		// Can move into an adjacent space that isn't taken by own color
		for(byte rank = (byte) (pos[0] - 1); rank <= (byte) (pos[0] + 1); rank++) {
			for(byte file = (byte) (pos[1] - 1); file <= (byte) (pos[1] + 1); file++) {
				// make sure it's on the board
				if(rank >= 0 && rank < 8 && file >= 0 && file < 8) {
					Piece neighbor = board.get(rank, file);
					// can move into an empty space or take out an opponent
					if(neighbor == null || neighbor.color != this.color)
						moves.add(new byte[]{rank, file});
				}
			}
		}
		return moves;
	}

	private ArrayList<byte[]> getQueenMoves(Board board, byte[] pos) {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<byte[]> getRookMoves(Board board, byte[] pos) {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<byte[]> getBishopMoves(Board board, byte[] pos) {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<byte[]> getKnightMoves(Board board, byte[] pos) {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<byte[]> getPawnMoves(Board board, byte[] pos) {
		// TODO Auto-generated method stub
		return null;
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
}
