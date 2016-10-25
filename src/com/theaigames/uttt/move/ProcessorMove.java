package com.theaigames.uttt.move;

import com.theaigames.uttt.field.Field;
import com.theaigames.uttt.player.Player;

public class ProcessorMove {

	// representations of field after player makes move
	private int[][] field;
	private int[][] microField;
	private int column, row;
	private Player player; // player that did this move
	private String illegalMove; // gets the value of the error message if move
								// is illegal, else remains empty
    
	public ProcessorMove(Player player) {
		this.player = player;
		field = new int[Field.FIELD_COLUMNS][Field.FIELD_ROWS];
		microField = new int[Field.MICRO_COLUMNS][Field.MICRO_ROWS];
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void setIllegalMove(String illegalMove) {
		this.illegalMove = illegalMove;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public boolean isIllegal() {
		if (illegalMove == null)
			return false;
		return true;
	}
	
	public String getIllegalMove() {
		return illegalMove;
    }
	
    public void setColumn(int column) {
        this.column = column;
    }
    
    public int getColumn() {
        return column;
    }

	public int getRow() {
		return row;
	}

	public void setRow(int mRow) {
		this.row = mRow;
	}

	public int getPlayerId() {
		return getPlayer().getId();
	}

	public void setField(int[][] field) {
		for (int col = 0; col < Field.FIELD_COLUMNS; col++) {
			System.arraycopy(field[col], 0, this.field[col], 0, Field.FIELD_ROWS);
		}
	}

	public int[][] getField() {
		return field;
	}

	public void setMicro(int[][] microField) {
		for (int col = 0; col < Field.MICRO_COLUMNS; col++) {
			System.arraycopy(microField[col], 0, this.microField[col], 0, Field.MICRO_ROWS);
		}
	}

	public int[][] getMicroField() {
		return microField;
	}
}
