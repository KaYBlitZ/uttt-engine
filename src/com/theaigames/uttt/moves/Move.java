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

	// representations of field before player makes move
	private String mField;
	private String mMacro;
	private int mColumn, mRow;
	private Player player; // player that did this move
	private String illegalMove; // gets the value of the error message if move
								// is illegal, else remains empty
    
	public Move(Player player) {
		this.player = player;
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
        this.mColumn = column;
    }
    
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
	
	public void setMacro(String macro) {
		mMacro = macro;
	}
	
	public String getMacro() {
		return mMacro;
	}
}
