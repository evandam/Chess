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
	public ArrayList<byte[]> getPossibleMoves(byte[] pos) {
		switch(type) {
			case PAWN:
				return getPawnMoves(pos);
			case KNIGHT:
				return getKnightMoves(pos);
			case BISHOP:
				return getBishopMoves(pos);
			case ROOK:
				return getRookMoves(pos);
			case QUEEN:
				return getQueenMoves(pos);
			default:
				return getKingMoves(pos);
		}
	}

	private ArrayList<byte[]> getKingMoves(byte[] pos) {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<byte[]> getQueenMoves(byte[] pos) {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<byte[]> getRookMoves(byte[] pos) {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<byte[]> getBishopMoves(byte[] pos) {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<byte[]> getKnightMoves(byte[] pos) {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<byte[]> getPawnMoves(byte[] pos) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// COLOR + PIECE...ie WP = white pawn
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
