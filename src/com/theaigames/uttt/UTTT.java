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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.theaigames.engine.Engine;
import com.theaigames.engine.io.IOPlayer;
import com.theaigames.game.GameHandler;
import com.theaigames.game.Processor;
import com.theaigames.gui.GUI;
import com.theaigames.uttt.field.MacroField;
import com.theaigames.uttt.moves.Move;
import com.theaigames.uttt.player.Player;

/**
 * abstract class AbstractGame
 * 
 * DO NOT EDIT THIS FILE
 * 
 * Extend this class with your main method. In the main method, create an
 * instance of your Logic and run setupEngine() and runEngine()
 * 
 * @author Jim van Eeden <jim@starapple.nl>
 */

public class UTTT implements GameLogic {
	
	public UTTTStarter starter;
	public Engine engine; // runs game
	public GameHandler processor; // handles data
	private List<Player> players;
	private MacroField mMacroField;
	private GUI gui;

	public UTTT(String bot1, String bot2, UTTTStarter starter) {
		this.starter = starter;
		players = new ArrayList<Player>(Constants.MAX_PLAYERS);
		// add players
		createPlayer(bot1, 1);
		createPlayer(bot2, 2);
		
		mMacroField = new MacroField();
		processor = new Processor(players, mMacroField, starter);
		engine = new Engine();
	}

	@Override
	public void createPlayer(String command, int id) {
		try {
			// Create new process
			Process process = Runtime.getRuntime().exec(command);
			// Attach IO to process
			IOPlayer ioPlayer = new IOPlayer(process, "ID_" + id, starter.isTimebankDisabled());
			// Start running
			ioPlayer.run();

			String playerName = "player" + id;
			Player player = new Player(playerName, ioPlayer, id);
			players.add(player);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendSettings(Player player) {
		player.sendSetting("timebank", Constants.TIMEBANK_MAX);
		player.sendSetting("time_per_move", Constants.TIME_PER_MOVE);
		player.sendSetting("player_names", this.players.get(0).getName() + "," + this.players.get(1).getName());
		player.sendSetting("your_bot", player.getName());
		player.sendSetting("your_botid", player.getId());
	}

	/**
	 * @return : True when the game is over
	 */
	@Override
	public boolean isGameOver() {
		if (processor.isGameOver() || processor.getRoundNumber() > Constants.MAX_ROUNDS) {
			return true;
		}
		return false;
	}

	/**
	 * Play one round of the game
	 * 
	 * @param roundNumber
	 *            : round number
	 */
	@Override
	public void playRound() {
		for (Player player : players) player.addToDump("Round " + processor.getRoundNumber());
		processor.playRound();
	}

	@Override
	public void start() {
		if (!starter.inBatchMode() && gui == null) throw new RuntimeException("Not in batch mode: Please set GUI in UTTT before starting.");
		for (Player player : players) {
			sendSettings(player);
		}
		engine.setLogic(this);
		engine.start();
	}

	/**
	 * close the bot processes, save, exit program
	 */
	@Override
	public void finish() {
		// stop the bots
		for (Player player : players)
			player.finish();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (!starter.inBatchMode()) {
			if (starter.outputBot1Error()) {
				System.out.println("Player " + 1 + " Stderr");
				System.out.println(players.get(0).getStderr());
			}
			if (starter.outputBot2Error()) {
				System.out.println("Player " + 2 + " Stderr");
				System.out.println(players.get(1).getStderr());
			}
		}
		System.out.println("Done.");
		if (starter.inBatchMode()) {
			processor.updateCurrentSampleValues();
			starter.NUM_GAMES_RUNNING.decrementAndGet();
		} else {
			gui.finishGame();
			starter.finish();
		}
	}

	/**
	 * Does everything that is needed to store the output of a game
	 */
	public void saveGame() {
		// save results to file here
	}

	/* For GUI */
	public void setGUI(GUI gui) {
		this.gui = gui;
	}

	public int[] getMacroBoard() {
		return mMacroField.getMacroBoard();
	}

	public int[] getField() {
		return mMacroField.getField();
	}

	public int getCurrentPlayerId() {
		return mMacroField.getCurrentPlayerId();
	}

	public List<Move> getMoves() {
		return processor.getMoves();
	}
	
	/**
	 * Sends the command "move row col" to the current bot. Should be consumed by a
	 * player bot that outputs "place_move row col" to standard output.
	 * @param col
	 * @param row
	 */
	public void enterMove(int col, int row) {
		Player bot = players.get(mMacroField.getCurrentPlayerId() - 1);
		try {
			bot.writeToBot("move " + col + " " + row);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
