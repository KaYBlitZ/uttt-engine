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

package com.theaigames.game;

import java.util.List;

import com.theaigames.uttt.moves.Move;

/**
 * GameHandler interface
 * 
 * DO NOT EDIT THIS FILE
 * 
 * This interface handles all the game logic.
 * 
 * @author Jim van Eeden <jim@starapple.nl>
 */
// handles actual game specific logic
public interface GameHandler {
    public void playRound(); // play one round of the game
    public int getRoundNumber(); // return the current round number
	public int getWinner(); // return the winner of the game, -1 if no winner yet
    public boolean isGameOver(); // returns true if the game is over
    public String getPlayedGame(); // return the complete string of the game that can be parsed by the visualizer.
	public List<Move> getMoves();
	public void updateBatchValues();
}
