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

public class MacroField {
	// represents the field
	
	private final int FIELD_COLUMNS = 9;
	private final int FIELD_ROWS = 9;
	private final int MACRO_COLUMNS = 3;
	private final int MACRO_ROWS = 3;
	
	private final int FIELD_EMPTY = 0;
	private final int MACRO_PLAYABLE = -1;
	private final int MACRO_UNPLAYABLE = 0; // or tie
    
	/* Index 0 is the top left box and index 48 is the bottom-right.
	 * Move from left to right, top to bottom, as indices increase.
	 */
	private int[] mField;
	private int[] mMacro;
    public String mLastError = "";
	private int mLastColumn, mLastRow;
    private String mWinType = "None";
	private boolean isFinished = false;

    public MacroField() {
		mField = new int[FIELD_COLUMNS * FIELD_ROWS];
		mMacro = new int[MACRO_COLUMNS * MACRO_ROWS];
        clearBoard();
    }
    
    public void clearBoard() {
        for (int i = 0; i < FIELD_COLUMNS * FIELD_ROWS; i++) {
            mField[i] = 0;
        }
        for (int i = 0; i < MACRO_COLUMNS * MACRO_ROWS; i++) {
            mMacro[i] = 0;
        }
    }
    
    public void dumpBoard() {
        for (int x = 0; x < mCols; x++) {
            System.out.print("--");
        }
        System.out.print("\n");
        for (int y = 0; y < mRows; y++) {
            for (int x = 0; x < mCols; x++) {
                System.out.print(mField[x][y]);
                if (x < mCols-1) {
                    System.out.print(",");
                }
            }
            System.out.print("\n");
        }
    }
    
    public static String padRight(String s, int n) {
         return String.format("%1$-" + n + "s", s);  
    }
    
    /**
     * Adds a disc to the board
     * @param args : command line arguments passed on running of application
     * @return : true if disc fits, otherwise false
     */
	public Boolean addMarker(int column, int row, int marker) {
        mLastError = "";
        mLastColumn = column;
        mLastRow = row;
        
        int fieldIndex = getFieldIndex(column, row);
        int macroIndex = getMacroIndex(column, row);
        if (fieldIndex == -1 || macroIndex == -1) {
        	mLastError = String.format("Move out of bounds. (%d, %d)", column, row);
        	return false;
        }
        /*
         *  We need to check if macro board is playable
         *  If yes, play it
         *  Determine next playable macro board
         */
        if (mMacro[macroIndex] == MACRO_PLAYABLE) {
	    	if (mField[fieldIndex] == FIELD_EMPTY) {
	    		mField[fieldIndex] = marker;
	    		// determine next 
				return true;
	        } else {
				mLastError = String.format("Field not empty. (%d, %d)", column, row);
				return false;
	        }
        } else {
        	mLastError = String.format("Macro field not playable (%d).", macroIndex);
        	return false;
        }
    }
	
	private int getFieldIndex(int column, int row) {
		if ((column >= 0 && column < FIELD_COLUMNS) && 
				(row >= 0 && row < FIELD_ROWS)) {
        	return row * FIELD_COLUMNS + column;
		} else {
			return -1;
        }
	}
	
	private int getMacroIndex(int col, int row) {
		int macroRow = getMacroRow(row);
		int macroCol = getMacroCol(col);
		if (macroRow == -1 || macroCol == -1) return -1;
		return macroRow * MACRO_COLUMNS + macroCol;
	}

	private int getMacroRow(int row) {
		if (row >= 0 && row < FIELD_ROWS) {
			return row / 3;
		}
		return -1;
	}
	
	private int getMacroCol(int col) {
		if (col >= 0 && col < FIELD_COLUMNS) {
			return col / 3;
		}
		return -1;
	}
	
	private int getTTTWinner(int macroIndex) {
		int[][] board = new int[3][3]; // small tic tac toe board
    	
		/* Check for vertical wins */
        for (int x = 0; x < mCols; x++) {
        	if (winner[x][0] == -1 || winner[x][0] == 0) continue; // don't check if running or tied
            if (winner[x][0] == winner[x][1] && winner[x][1] == winner[x][2]) {
				mWinType = "vertical";
				isFinished = true;
				mWinner = winner[x][0];
				return mWinner;
            }
        }
        
		/* Check for horizontal wins */
		for (int y = 0; y < mRows; y++) {
			if (winner[0][y] == -1 || winner[0][y] == 0) continue; // don't check if running or tied
			if (winner[0][y] == winner[1][y] && winner[1][y] == winner[2][y]) {
				mWinType = "horizontal";
				isFinished = true;
				mWinner = winner[0][y];
				return mWinner;
            }
        }
        
		/* Check for forward diagonal wins - / */
		if (winner[0][2] != -1 && winner[0][2] != 0 && // don't check if running or tied
				winner[0][2] == winner[1][1] && winner[1][1] == winner[2][0]) {
			mWinType = "forward diagonal";
			isFinished = true;
			mWinner = winner[0][2];
			return mWinner;
		}
		
		/* Check for backward diagonal wins - \ */
		if (winner[0][0] != -1 && winner[0][0] != 0 && // don't check if running or tied
				winner[0][0] == winner[1][1] && winner[1][1] == winner[2][2]) {
			mWinType = "backward diagonal";
			isFinished = true;
			mWinner = winner[0][0];
			return mWinner;
		}
		
		if (isFull()) {
			mWinner = 0;
			return mWinner;
		}

        return -1;
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
        return mLastColumn;
    }
    
	public int getLastRow() {
		return mLastRow;
	}
    
    @Override
    /**
     * Creates comma separated String with player names for every cell.
     * @param args : 
     * @return : comma separated String of player id if won, 0 if not playable, -1 if playable
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < mCols; x++) {
        	for (int y = 0; y < mRows; y++) {
        		int winner = mField[x][y].getWinner();
        		if (winner > 0) { // have winner
        			builder.append(winner);
        		} else if (winner == 0) { // is tied
        			builder.append(0);
        		} else if (mCurrentCol == -1 && mCurrentRow == -1) { // playable
        			builder.append(-1);
        		} else { // need to check if playable
        			if (mCurrentCol == x && mCurrentRow == y) { // playable
        				builder.append(-1);
        			} else {
        				builder.append(0); // not playable
        			}
        		}
        		// append comma if not last field
        		if (x != mCols - 1 && y != mRows - 1) builder.append(',');
        	}
        }
        return builder.toString();
    }
    
    /**
     * Returns the comma-separated string of all the field elements to send to the bots. Each
     * successive integer corresponds to a box in the 9 x 9 field starting from the top-left and 
     * going right. 
     * @return String
     */
    public String getFieldsString() {
    	for (int x = 0; x < 9; x++) {
    		for (int y = 0; y < 9; y++) {
    			
    		}
    	}
    	return null;
    }
    
    /**
     * Checks whether the field is full
     * @param args : 
     * @return : Returns true when field is full, otherwise returns false.
     */
	public boolean isFinished() {
		return isFinished;
    }
	
	public boolean isFull() {
		for (int x = 0; x < mCols; x++) {
			for (int y = 0; y < mRows; y++) {
				if (mField[x][y].getWinner() == -1) return false;
			}
		}
		return true;
	}
    
    /**
     * Checks if there is a winner, if so, returns player id.
     * @param args : 
     * @return : Returns player id if there is a winner, 0 if tied, -1 if open.
     */
    public int getWinner() {
    	int winner[][] = new int[mCols][mRows];
    	winner[0][0] = mField[0][0].getWinner();
    	winner[0][1] = mField[0][1].getWinner();
    	winner[0][2] = mField[0][2].getWinner();
    	winner[1][0] = mField[1][0].getWinner();
    	winner[1][1] = mField[1][1].getWinner();
    	winner[1][2] = mField[1][2].getWinner();
    	winner[2][0] = mField[2][0].getWinner();
    	winner[2][1] = mField[2][1].getWinner();
    	winner[2][2] = mField[2][2].getWinner();
    	
		/* Check for vertical wins */
        for (int x = 0; x < mCols; x++) {
        	if (winner[x][0] == -1 || winner[x][0] == 0) continue; // don't check if running or tied
            if (winner[x][0] == winner[x][1] && winner[x][1] == winner[x][2]) {
				mWinType = "vertical";
				isFinished = true;
				mWinner = winner[x][0];
				return mWinner;
            }
        }
        
		/* Check for horizontal wins */
		for (int y = 0; y < mRows; y++) {
			if (winner[0][y] == -1 || winner[0][y] == 0) continue; // don't check if running or tied
			if (winner[0][y] == winner[1][y] && winner[1][y] == winner[2][y]) {
				mWinType = "horizontal";
				isFinished = true;
				mWinner = winner[0][y];
				return mWinner;
            }
        }
        
		/* Check for forward diagonal wins - / */
		if (winner[0][2] != -1 && winner[0][2] != 0 && // don't check if running or tied
				winner[0][2] == winner[1][1] && winner[1][1] == winner[2][0]) {
			mWinType = "forward diagonal";
			isFinished = true;
			mWinner = winner[0][2];
			return mWinner;
		}
		
		/* Check for backward diagonal wins - \ */
		if (winner[0][0] != -1 && winner[0][0] != 0 && // don't check if running or tied
				winner[0][0] == winner[1][1] && winner[1][1] == winner[2][2]) {
			mWinType = "backward diagonal";
			isFinished = true;
			mWinner = winner[0][0];
			return mWinner;
		}
		
		if (isFull()) {
			mWinner = 0;
			return mWinner;
		}

        return -1;
    }
    
    
    /**
     * Returns the direction of a win.
     * @param args : 
     * @return : Returns String with direction of win, or 'None' if there is no win yet.
     */
    public String getWinType() {
        return mWinType;
    }

    public int getNrColumns() {
        return mCols;
    }
    
    public int getNrRows() {
        return mRows;
    }
}
