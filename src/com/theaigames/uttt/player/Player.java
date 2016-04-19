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

import com.theaigames.engine.io.PlayerBot;
import com.theaigames.uttt.UTTT;

public class Player {

	private String name;
	private PlayerBot bot;
	private long timeBank;
	private long timePerMove;
	private int id;

	public Player(String name, PlayerBot bot, long maxTimeBank, long timePerMove, int id) {
		this.name = name;
		this.bot = bot;
		this.timeBank = maxTimeBank;
		this.timePerMove = timePerMove;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public long getTimeBank() {
		return timeBank;
	}

	public PlayerBot getBot() {
		return bot;
	}

	public int getId() {
		return id;
	}

	public void setTimeBank(long time) {
		this.timeBank = time;
	}

	public void updateTimeBank(long timeElapsed) {
		this.timeBank = Math.max(this.timeBank - timeElapsed, 0);
		this.timeBank = Math.min(this.timeBank + this.timePerMove, UTTT.TIMEBANK_MAX);
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
		String response = this.bot.getResponse(this.timeBank);

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
			this.bot.writeToBot(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
