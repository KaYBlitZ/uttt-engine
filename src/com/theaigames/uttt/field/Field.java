// Copyright 2016 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//  
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package com.theaigames.uttt.field;

import java.util.Stack;

import com.theaigames.uttt.move.Position;

public class Field { // represents the entire field
	// origin is top left, access by column (x) the row (y)
	public static final int FIELD_COLUMNS = 9;
	public static final int FIELD_ROWS = 9;
	public static final int MICRO_COLUMNS = 3;
	public static final int MICRO_ROWS = 3;
	public static final int MINI_COLUMNS = 3;
	public static final int MINI_ROWS = 3;
	
	public static final int FIELD_PLAYABLE = 0;
	public static final int MICRO_PLAYABLE = -1;
	public static final int MICRO_UNPLAYABLE = 0, MICRO_TIE = 0;
    
	private int[][] field; // field state sent to bots
	private int[][] microField; // the micro field state sent to bots
    public String mLastError = "";
    private Stack<Position> moves;
    private String mWinType = "None";
	private int currentPlayerId = 1;
	private Position nextMicro;

    public Field() {
		field = new int[FIELD_COLUMNS][FIELD_ROWS];
		microField = new int[MICRO_COLUMNS][MICRO_ROWS];
		moves = new Stack<Position>();
		nextMicro = new Position(-1, -1); // anywhere
        clearFields();
    }
    
    public void clearFields() {
        for (int col = 0; col < FIELD_COLUMNS; col++) {
        	for (int row = 0; row < FIELD_ROWS; row++) {
        		field[col][row] = FIELD_PLAYABLE;
        	}
        }
        for (int col = 0; col < MICRO_COLUMNS; col++) {
        	for (int row = 0; row < MICRO_ROWS; row++) {
        		microField[col][row] = MICRO_PLAYABLE;
        	}
        }
    }
    
    /**
     * Adds a disc to the field
     * @param args : command line arguments passed on running of application
     * @return : true if disc fits, otherwise false
     */
	public boolean makeMove(int column, int row, int id) {
        mLastError = "";
        moves.add(new Position(column, row));
        
        if (microField[column / 3][row / 3] == MICRO_PLAYABLE) {
	    	if (field[column][row] == FIELD_PLAYABLE) {
	    		field[column][row] = id;
	    		// determine next micro field
	    		updateMicroWinner(column / 3, row / 3);
	    		updateMicroField(column, row);
				return true;
	        } else {
				mLastError = String.format("Field cell not empty (%d, %d)", column, row);
				return false;
	        }
        } else {
        	mLastError = String.format("Micro field not playable (%d, %d)", column / 3, row / 3);
        	return false;
        }
    }
	
	
	public void updateMicroWinner(int microColumn, int microRow) {
		microField[microColumn][microRow] = getMicroWinner(microColumn * 3, microRow * 3);
	}
	
	public void updateMicroField(int column, int row) {
		// ie: 3,2 move -> 0,2 next micro -> 0,6 top-left next micro box coords
		// ie: 5,4 -> 2,1 -> 6,3
		// convert move coords into next micro field coords 
		while (column > 2)
			column -= 3;
		while (row > 2)
			row -= 3;

		// whether the next micro field is finished or not
		boolean nextMicroPlayable = getMicroWinner(column * 3, row * 3) == MICRO_PLAYABLE;
		nextMicro = nextMicroPlayable ? new Position(column, row) : new Position(-1, -1);
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (microField[x][y] < 0 || (microField[x][y] == 0 && !isMicroFull(x * 3, y * 3))) {
					if (!nextMicroPlayable || (x == column && y == row)) {
						// next micro field finished, set all open micro fields to be playable
						// else set playable only if its the nextMicroIndex
						microField[x][y] = -1;
					} else {
						microField[x][y] = 0;
					}
				}
			}
		}
	}
	
	private boolean isMicroFull(int topLeftColumn, int topLeftRow) {
		if (field[topLeftColumn][topLeftRow] == 0 || 
			field[topLeftColumn + 1][topLeftRow] == 0 || 
			field[topLeftColumn + 2][topLeftRow] == 0 ||
			field[topLeftColumn][topLeftRow + 1] == 0 || 
			field[topLeftColumn + 1][topLeftRow + 1] == 0 || 
			field[topLeftColumn + 2][topLeftRow + 1] == 0 ||
			field[topLeftColumn][topLeftRow + 2] == 0 || 
			field[topLeftColumn + 1][topLeftRow + 2] == 0 || 
			field[topLeftColumn + 2][topLeftRow + 2] == 0) {
			return false;
		}
		return true;
	}
	
	private int getMicroWinner(int topLeftColumn, int topLeftRow) {
		int m00 = field[topLeftColumn][topLeftRow];
		int m10 = field[topLeftColumn + 1][topLeftRow];
		int m20 = field[topLeftColumn + 2][topLeftRow];
		int m01 = field[topLeftColumn][topLeftRow + 1];
		int m11 = field[topLeftColumn + 1][topLeftRow + 1];
		int m21 = field[topLeftColumn + 2][topLeftRow + 1];
		int m02 = field[topLeftColumn][topLeftRow + 2];
		int m12 = field[topLeftColumn + 1][topLeftRow + 2];
		int m22 = field[topLeftColumn + 2][topLeftRow + 2];
    	
		/* Check for vertical wins */
        if (m00 > 0 && m00 == m01 && m01 == m02) return m00;
        if (m10 > 0 && m10 == m11 && m11 == m12) return m10;
        if (m20 > 0 && m20 == m21 && m21 == m22) return m20;
        
		/* Check for horizontal wins */
        if (m00 > 0 && m00 == m10 && m10 == m20) return m00;
        if (m01 > 0 && m01 == m11 && m11 == m21) return m01;
        if (m02 > 0 && m02 == m12 && m12 == m22) return m02;
        
		/* Check for forward diagonal wins - / */
		if (m02 > 0 && m02 == m11 && m11 == m20) return m02;
		
		/* Check for backward diagonal wins - \ */
		if (m00 > 0 && m00 == m11 && m11 == m22) return m00;
		
		// check if the field is full
		if (m00 == 0 || m10 == 0 || m20 == 0 ||
				m01 == 0 || m11 == 0 || m21 == 0 ||
				m02 == 0 || m12 == 0 || m22 == 0) {
			return -1;
		}
		
		// must be a tie
        return 0;
	}
	
	public int getWinner() {
		if (microField[0][0] > 0 && microField[0][0] == microField[1][1] &&
				microField[1][1] == microField[2][2]) { // \ diagonal
			return microField[0][0];
		} else if (microField[0][2] > 0 && microField[0][2] == microField[1][1] &&
				microField[1][1] == microField[2][0]) { // / diagonal
			return microField[0][2];
		}
		
		// check vertical
		for (int x = 0; x < 3; x++) {
			if (microField[x][0] > 0 && microField[x][0] == microField[x][1] &&
					microField[x][1] == microField[x][2]) {
				return microField[x][0];
			}
		}
		
		// check horizontal
		for (int y = 0; y < 3; y++) {
			if (microField[0][y] > 0 && microField[0][y] == microField[1][y] &&
					microField[1][y] == microField[2][y]) {
				return microField[0][y];
			}
		}
		
		for (int col = 0; col < 3; col++) {
			for (int row = 0; row < 3; row++) {
				// game still playable
				if (microField[col][row] == -1)
					return -1;
			}
		}
		
		return 0;
	}
	
    /**
     * Returns reason why addDisc returns false
     * @param args : 
     * @return : reason why addDisc returns false
     */
    public String getLastError() {
        return mLastError;
    }
    
    /**
     * Returns last inserted column
     * @param args : 
     * @return : last inserted column
     */
    public int getLastColumn() {
    	if (!moves.isEmpty())
    		return moves.peek().column;
    	return -1;
    }
    
	public int getLastRow() {
		if (!moves.isEmpty())
			return moves.peek().row;
		return -1;
	}
    
    /**
     * Creates comma separated String of the micro field.
     * player id if won, 0 if not playable or tie, -1 if playable
     * @param args : 
     * @return : comma separated String of the micro field
     */
	public String getMicroFieldString() {
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		for (int y = 0; y < MICRO_ROWS; y++) {
			for (int x = 0; x < MICRO_COLUMNS; x++) {
				if (counter > 0)
					sb.append(',');
				sb.append(microField[x][y]);
				counter++;
			}
		}
		return sb.toString();
	}
    
    /**
     * Returns the comma-separated string of all the field elements to send to the bots. Each
     * successive integer corresponds to a box in the 9 x 9 field starting from the top-left and 
     * going right. 
     * @return String
     */
	public String getFieldString() {
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		for (int y = 0; y < FIELD_ROWS; y++) {
			for (int x = 0; x < FIELD_COLUMNS; x++) {
				if (counter > 0)
					sb.append(',');
				sb.append(field[x][y]);
				counter++;
			}
		}
		return sb.toString();
	}
    
    /**
     * Returns the direction of a win.
     * @param args : 
     * @return : Returns String with direction of win, or 'None' if there is no win yet.
     */
    public String getWinType() {
        return mWinType;
    }

	public Position getNextMicro() {
		return nextMicro;
	}

	/* For GUI */

	public int[][] getMicroField() {
		return microField;
	}

	public int[][] getField() {
		return field;
	}

	public void setCurrentPlayerId(int id) {
		currentPlayerId = id;
	}

	public int getCurrentPlayerId() {
		return currentPlayerId;
	}
}
