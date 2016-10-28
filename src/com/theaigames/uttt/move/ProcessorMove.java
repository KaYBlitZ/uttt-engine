package com.theaigames.uttt.move;

import com.theaigames.uttt.field.Field;
import com.theaigames.uttt.player.Player;

public class ProcessorMove {

	// representations of field after player makes move
	private int[][] field;
	private int[][] macroField;
	private int column, row;
	private Player player; // player that did this move
	private String illegalMove; // gets the value of the error message if move
								// is illegal, else remains empty
    
	public ProcessorMove(Player player) {
		this.player = player;
		field = new int[Field.FIELD_COLUMNS][Field.FIELD_ROWS];
		macroField = new int[Field.MACRO_COLUMNS][Field.MACRO_ROWS];
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

	public void setMacroField(int[][] macroField) {
		for (int col = 0; col < Field.MACRO_COLUMNS; col++) {
			System.arraycopy(macroField[col], 0, this.macroField[col], 0, Field.MACRO_ROWS);
		}
	}

	public int[][] getMacroField() {
		return macroField;
	}
}
