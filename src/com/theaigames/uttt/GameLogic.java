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

package com.theaigames.uttt;

import com.theaigames.uttt.player.Player;

/**
 * Logic interface
 * 
 * DO NOT EDIT THIS FILE
 * 
 * Interface to implement when creating games.
 * 
 * @author Jackie Xu <jackie@starapple.nl>, Jim van Eeden <jim@starapple.nl>
 */
// handles generic game handling stuff
public interface GameLogic {
	public void playRound(); // play a round
	public void sendSettings(Player player); // send the game settings to given player
    public boolean isGameOver(); // check if the game is over
    public void createPlayer(String command, int id);
    public void finish(); // wrap things up
    public void saveGame(); // save all the game data
    public void start();
}
