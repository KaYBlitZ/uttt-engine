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


package com.theaigames.uttt.player;

import java.io.IOException;

import com.theaigames.engine.io.IOPlayer;
import com.theaigames.uttt.Constants;

public class Player {

	private String name;
	private IOPlayer ioPlayer;
	private long timeBank;
	private int id;

	public Player(String name, IOPlayer ioPlayer, int id) {
		this.name = name;
		this.ioPlayer = ioPlayer;
		this.timeBank = Constants.TIMEBANK_MAX;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public long getTimeBank() {
		return timeBank;
	}

	public int getId() {
		return id;
	}

	public void setTimeBank(long time) {
		this.timeBank = time;
	}

	public void updateTimeBank(long timeElapsed) {
		this.timeBank = Math.max(this.timeBank - timeElapsed, 0);
		this.timeBank = Math.min(this.timeBank + Constants.TIME_PER_MOVE, Constants.TIMEBANK_MAX);
	}

	public void sendSetting(String type, String value) {
		sendLine(String.format("settings %s %s", type, value));
	}

	public void sendSetting(String type, int value) {
		sendLine(String.format("settings %s %d", type, value));
	}

	// sends updates about another player
	public void sendUpdate(String type, Player player, String value) {
		sendLine(String.format("update %s %s %s", player.getName(), type, value));
	}

	public void sendUpdate(String type, Player player, int value) {
		sendLine(String.format("update %s %s %d", player.getName(), type, value));
	}

	public void sendUpdate(String type, String value) {
		sendLine(String.format("update game %s %s", type, value));
	}

	public void sendUpdate(String type, int value) {
		sendLine(String.format("update game %s %d", type, value));
	}

	/**
	 * Asks the bot for given move type and returns the answer
	 * 
	 * @param moveType
	 *            : type of move the bot has to return
	 * @return : the bot's output
	 */
	public String requestMove(String moveType) {
		long startTime = System.currentTimeMillis();

		// write the request to the bot
		sendLine(String.format("action %s %d", moveType, this.timeBank));

		// wait for the bot to return his response
		String response = ioPlayer.getResponse(this.timeBank);

		// update the timebank
		long timeElapsed = System.currentTimeMillis() - startTime;
		updateTimeBank(timeElapsed);

		return response;
	}

	/**
	 * Sends given string to bot
	 * 
	 * @param info
	 */
	private void sendLine(String content) {
		try {
			ioPlayer.writeToBot(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Delegation to IOPlayer
	public void addToDump(String dumpy) {
		ioPlayer.addToDump(dumpy);
	}
	
	public void finish() {
		ioPlayer.finish();
	}
	
	public void writeToBot(String line) throws IOException {
		ioPlayer.writeToBot(line);
	}
	
	public String getStdout() {
		return ioPlayer.getStdout();
	}
	
	public String getStderr() {
		return ioPlayer.getStderr();
	}
}