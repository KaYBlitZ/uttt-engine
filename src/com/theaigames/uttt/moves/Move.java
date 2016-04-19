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

package com.theaigames.uttt.moves;

import com.theaigames.uttt.player.Player;

public class Move {

	private String mField;
	private int mColumn, mRow;
	private Player player; // player that did this move
	private String illegalMove; // gets the value of the error message if move
								// is illegal, else remains empty
    
	public Move(Player player) {
		this.player = player;
		this.illegalMove = "";
	}

	/**
	 * @param player
	 *            : Sets the name of the Player that this Move belongs to
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @param illegalMove
	 *            : Sets the error message of this move. Only set this if the
	 *            Move is illegal.
	 */
	public void setIllegalMove(String illegalMove) {
		this.illegalMove = illegalMove;
	}

	/**
	 * @return : The player that this Move belongs to
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * @return : True if this Move is illegal
	 */
	public boolean isIllegal() {
		if (this.illegalMove.isEmpty())
			return false;
		return true;
	}

	/**
	 * @return : The error message of this Move
	 */
	public String getIllegalMove() {
		return illegalMove;
    }
    
    /**
     * @param column : Sets the column of a move
     */
    public void setColumn(int column) {
        this.mColumn = column;
    }
    
    /**
     * @return : Column of move
     */
    public int getColumn() {
        return mColumn;
    }

	public int getRow() {
		return mRow;
	}

	public void setRow(int mRow) {
		this.mRow = mRow;
	}

	public int getPlayerId() {
		return getPlayer().getId();
	}

	public void setField(String field) {
		mField = field;
	}

	public String getField() {
		return mField;
	}
}
