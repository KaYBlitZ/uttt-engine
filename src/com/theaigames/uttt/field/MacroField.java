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

public class MacroField { // represents the entire field
	// macro is the large 3 x 3 TTT board
	// mini is a small 3 x 3 TTT board
	// field is the entire 9 x 9 board
	public static final int FIELD_COLUMNS = 9;
	public static final int FIELD_ROWS = 9;
	public static final int MACRO_COLUMNS = 3;
	public static final int MACRO_ROWS = 3;
	public static final int MINI_COLUMNS = 3;
	public static final int MINI_ROWS = 3;
	
	public static final int FIELD_PLAYABLE = 0;
	public static final int MACRO_PLAYABLE = -1;
	public static final int MACRO_UNPLAYABLE = 0, MACRO_TIE = 0;
    
	/* Index 0 is the top left box and index 48 is the bottom-right.
	 * Move from left to right, top to bottom, as indices increase.
	 */
	private int[] mField;
	private int[] mMacro;
    public String mLastError = "";
	private int mLastColumn, mLastRow;
    private String mWinType = "None";
	private int mWinner = -1; // 0 is tie, -1 means game is still ongoing
	private int nextMacroIndex;
	private int currentPlayerId = 1;

    public MacroField() {
		mField = new int[FIELD_COLUMNS * FIELD_ROWS];
		mMacro = new int[MACRO_COLUMNS * MACRO_ROWS];
        clearBoard();
    }
    
    public void clearBoard() {
        for (int i = 0; i < FIELD_COLUMNS * FIELD_ROWS; i++) {
            mField[i] = FIELD_PLAYABLE;
        }
        for (int i = 0; i < MACRO_COLUMNS * MACRO_ROWS; i++) {
            mMacro[i] = MACRO_PLAYABLE;
        }
    }
    
    /*public void dumpBoard() {
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
    }*/
    
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
         *  If yes, try play it
         *  Determine next playable macro board
         */
        if (mMacro[macroIndex] == MACRO_PLAYABLE) {
	    	if (mField[fieldIndex] == FIELD_PLAYABLE) {
	    		mField[fieldIndex] = marker;
	    		// determine next macro field
				updateMacro(column, row);
	    		checkWinner();
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
	
	public void updateMacro(int column, int row) {
		// find next macro index
		// shift indices to 3 x 3 board
		while (column > 2)
			column -= 3;
		while (row > 2)
			row -= 3;
		nextMacroIndex = row * MACRO_COLUMNS;
		nextMacroIndex += column;

		// whether the next board is finished or not
		boolean nextMiniOpen = getMiniWinner(nextMacroIndex) == MACRO_PLAYABLE;
		for (int i = 0; i < mMacro.length; i++) {
			int winner = getMiniWinner(i);
			if (winner >= 0) { // winner, tie
				mMacro[i] = winner;
			} else { // open mini TTT, need to check if playable or not
				if (!nextMiniOpen || i == nextMacroIndex) {
					// if next board is finished all open boards are playable
					// else set playable only if its the nextMacroIndex
					mMacro[i] = MACRO_PLAYABLE;
				} else {
					mMacro[i] = MACRO_UNPLAYABLE;
				}
			}
		}
	}
	
	public void checkWinner() {
		/* horizontal */
		if (mMacro[0] > 0 && mMacro[0] == mMacro[1] && mMacro[1] == mMacro[2]) {
			mWinner = mMacro[0];
		} else if (mMacro[3] > 0 && mMacro[3] == mMacro[4] && mMacro[4] == mMacro[5]) {
			mWinner = mMacro[3];
		} else if (mMacro[6] > 0 && mMacro[6] == mMacro[7] && mMacro[7] == mMacro[8]) {
			mWinner = mMacro[6];
		}
		/* vertical */
		else if (mMacro[0] > 0 && mMacro[0] == mMacro[3] && mMacro[3] == mMacro[6]) {
			mWinner = mMacro[0];
		} else if (mMacro[1] > 0 && mMacro[1] == mMacro[4] && mMacro[4] == mMacro[7]) {
			mWinner = mMacro[1];
		} else if (mMacro[2] > 0 && mMacro[2] == mMacro[5] && mMacro[5] == mMacro[8]) {
			mWinner = mMacro[2];
		}
		/* forward diagonal / */
		else if (mMacro[0] > 0 && mMacro[0] == mMacro[4] && mMacro[4] == mMacro[8]) {
			mWinner = mMacro[0];
		}
		/* backward diagonal \ */
		else if (mMacro[6] > 0 && mMacro[6] == mMacro[4] && mMacro[4] == mMacro[2]) {
			mWinner = mMacro[6];
		} else { //no winner
			for (int i = 0; i < mMacro.length; i++) {
				if (mMacro[i] == -1) {
					// still open
					mWinner = -1;
					return;
				}
			}
			mWinner = 0; // tie
		}
	}
	
	/**
	 * Returns winner if the mini TTT game is won; 0 if there is a tie; -1 if open
	 * @param macroIndex
	 * @return
	 */
	private int getMiniWinner(int macroIndex) {
		if (macroIndex < 0 && macroIndex >= MACRO_ROWS * MACRO_COLUMNS) {
			throw new RuntimeException("Invalid macro index in getMiniWinner");
		}
		
		int[][] board = new int[3][3]; // small tic tac toe board
		int bRow, bCol;
		// determine the top-left square indices of the smaller TTT board 
		if (macroIndex < 3) {
			bRow = 0;
		} else if (macroIndex >= 3 && macroIndex < 6) {
			bRow = 3;
		} else {
			bRow = 6;
		}
		if (macroIndex == 0 || macroIndex == 3 || macroIndex == 6) {
			bCol = 0;
		} else if (macroIndex == 1 || macroIndex == 4 || macroIndex == 7) {
			bCol = 3;
		} else {
			bCol = 6;
		}
		int bFieldIndex = getFieldIndex(bCol, bRow); // the beginning square indices as field index
		board[0][0] = mField[bFieldIndex];
		board[1][0] = mField[bFieldIndex + 1];
		board[2][0] = mField[bFieldIndex + 2];
		board[0][1] = mField[bFieldIndex + FIELD_COLUMNS];
		board[1][1] = mField[bFieldIndex + 1 + FIELD_COLUMNS];
		board[2][1] = mField[bFieldIndex + 2 + FIELD_COLUMNS];
		board[0][2] = mField[bFieldIndex + 2 * FIELD_COLUMNS];
		board[1][2] = mField[bFieldIndex + 1 + 2 * FIELD_COLUMNS];
		board[2][2] = mField[bFieldIndex + 2 + 2 * FIELD_COLUMNS];
    	
		/* Check for vertical wins */
        for (int x = 0; x < MINI_COLUMNS; x++) {
        	if (board[x][0] == 0) continue; // don't check if open square
            if (board[x][0] == board[x][1] && board[x][1] == board[x][2]) {
				return board[x][0];
            }
        }
        
		/* Check for horizontal wins */
		for (int y = 0; y < MINI_ROWS; y++) {
			if (board[0][y] == 0) continue; // don't check if running or tied
			if (board[0][y] == board[1][y] && board[1][y] == board[2][y]) {
				return board[0][y];
            }
        }
        
		/* Check for forward diagonal wins - / */
		if (board[0][2] != 0 && // don't check if open; dont want to count opens as winner
				board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
			return board[0][2];
		}
		
		/* Check for backward diagonal wins - \ */
		if (board[0][0] != 0 && // don't check if open
				board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
			return board[0][0];
		}
		
		// check if the board is full
		for (int x = 0; x < MINI_COLUMNS; x++) {
			for (int y = 0; y < MINI_ROWS; y++) {
				if (board[x][y] == FIELD_PLAYABLE) // not a tie
					return MACRO_PLAYABLE; 
			}
		}
		
		// must be a tie
        return MACRO_TIE;
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
    
    /**
     * Creates comma separated String of the macro board.
     * player id if won, 0 if not playable or tie, -1 if playable
     * @param args : 
     * @return : comma separated String of the macro board
     */
	public String getMacroFieldString() {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < mMacro.length; x++) {
        	builder.append(mMacro[x]);
        	if (x != mMacro.length - 1) builder.append(',');
        }
        return builder.toString();
    }
    
    /**
     * Returns the comma-separated string of all the field elements to send to the bots. Each
     * successive integer corresponds to a box in the 9 x 9 field starting from the top-left and 
     * going right. 
     * @return String
     */
	public String getFieldString() {
    	StringBuilder builder = new StringBuilder();
    	for (int i = 0; i < mField.length; i++) {
    		builder.append(mField[i]);
    		if (i != mField.length - 1) builder.append(',');
    	}
    	return builder.toString();
    }
    
    /**
     * Returns player id if there is a winner, 0 if tied, -1 if game is not finished.
     * @param args : 
     * @return : Returns the winner
     */
    public int getWinner() {
    	return mWinner;
    }
    
    
    /**
     * Returns the direction of a win.
     * @param args : 
     * @return : Returns String with direction of win, or 'None' if there is no win yet.
     */
    public String getWinType() {
        return mWinType;
    }

	public int getNextMacroIndex() {
		return nextMacroIndex;
	}

	/* For GUI */

	public int[] getMacroBoard() {
		return mMacro;
	}

	public int[] getField() {
		return mField;
	}

	public void setCurrentPlayerId(int id) {
		currentPlayerId = id;
	}

	public int getCurrentPlayerId() {
		return currentPlayerId;
	}
}
