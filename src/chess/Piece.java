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
	public byte[] pos;	// file, rank (col, row) - we'll need some map from char -> num for files
	
	// get the positions of the piece for all possible moves it can make
	public ArrayList<byte[]> getPossibleMoves() {
		switch(type) {
			case PAWN:
				return getPawnMoves();
			case KNIGHT:
				return getKnightMoves();
			case BISHOP:
				return getBishopMoves();
			case ROOK:
				return getRookMoves();
			case QUEEN:
				return getQueenMoves();
			case KING:
				return getKingMoves();
			default:
				return null;
		}
	}

	private ArrayList<byte[]> getKingMoves() {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<byte[]> getQueenMoves() {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<byte[]> getRookMoves() {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<byte[]> getBishopMoves() {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<byte[]> getKnightMoves() {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<byte[]> getPawnMoves() {
		// TODO Auto-generated method stub
		return null;
	}
}
