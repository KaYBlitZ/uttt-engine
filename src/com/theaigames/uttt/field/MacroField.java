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
	// represents a mini TTT game
    
	private Field[][] mBoard;
	private int mCols, mRows;
    public String mLastError = "";
	private int mLastColumn, mLastRow;
    private String mWinType = "None";
	private boolean isFinished = false;
	private int mCurrentCol, mCurrentRow; // open

    public MacroField(int columns, int rows) {
		mBoard = new Field[columns][rows];
        mCols = columns;
        mRows = rows;
        clearBoard();
    }
    
    public void clearBoard() {
        for (int x = 0; x < mCols; x++) {
            for (int y = 0; y < mRows; y++) {
				mBoard = new Field[mCols][mRows];
            }
        }
    }
    
    public void dumpBoard() {
        for (int x = 0; x < mCols; x++) {
            System.out.print("--");
        }
        System.out.print("\n");
        for (int y = 0; y < mRows; y++) {
            for (int x = 0; x < mCols; x++) {
                System.out.print(mBoard[x][y]);
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
        if (column >= 0 && column < mCols) {
        	if (mBoard[mCurrentCol][mCurrentRow].addMarker(column, row, marker)) {
				return true;
            }
			mLastError = "Column " + column + " & row " + row + " is full.";
        } else {
			mLastError = "Move out of bounds. (" + column + ", " + row + ")";
        }
        return false;
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
     * @return : String with player names for every cell, or 'empty' when cell is empty.
     */
    public String toString() {
        String r = "";
        int counter = 0;
        for (int y = 0; y < mRows; y++) {
            for (int x = 0; x < mCols; x++) {
                r += mBoard[x][y];
                if (counter < mRows*mCols-1) {
                    if (x == mCols-1) {
                        r += ";";
                    } else {
                        r += ",";
                    }
                }
                counter++;
            }
        }
        return r;
    }
    
    /**
     * Checks whether the field is full
     * @param args : 
     * @return : Returns true when field is full, otherwise returns false.
     */
	public boolean isFinished() {
		return isFinished;
    }
    
    /**
     * Checks if there is a winner, if so, returns player id.
     * @param args : 
     * @return : Returns player id if there is a winner, otherwise returns 0.
     */
    public int getWinner() {
		/* Check for vertical wins */
        for (int x = 0; x < mCols; x++) {
            if (mBoard[x][0] == mBoard[x][1] && mBoard[x][1] == mBoard[x][2]) {
				mWinType = "vertical";
				isFinished = true;
				return mBoard[x][0];
            }
        }
        
		/* Check for horizontal wins */
		for (int y = 0; y < mRows; y++) {
			if (mBoard[0][y] == mBoard[1][y] && mBoard[1][y] == mBoard[2][y]) {
				mWinType = "horizontal";
				isFinished = true;
				return mBoard[0][y];
            }
        }
        
		/* Check for forward diagonal wins - / */
		if (mBoard[0][2] == mBoard[1][1] && mBoard[1][1] == mBoard[2][0]) {
			mWinType = "forward diagonal";
			isFinished = true;
			return mBoard[0][2];
		}
		
		/* Check for backward diagonal wins - \ */
		if (mBoard[0][0] == mBoard[1][1] && mBoard[1][1] == mBoard[2][2]) {
			mWinType = "backward diagonal";
			isFinished = true;
			return mBoard[0][0];
		}

        return 0;
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
