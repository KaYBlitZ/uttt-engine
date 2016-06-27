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
import java.util.concurrent.atomic.AtomicInteger;

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
	
	public static final AtomicInteger NUM_GAMES_RUNNING = new AtomicInteger();
	
	private static class GameThread extends Thread {
		private String args[];
		
		public GameThread(String args[]) {
			this.args = args;
		}
		
		@Override
		public void run() {
			try {
				UTTT game = new UTTT(args);
				game.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// DEV_MODE can be turned on to easily test the
	// engine from eclipse
	public static void main(final String args[]) {
		try {
			if (Constants.DEV_MODE) {
				if (Constants.DEV_BATCH_MODE) {
					long startTime = System.currentTimeMillis();
					for (int i = 0; i < Constants.DEV_BATCH_SAMPLE_SIZE; i++) {
						System.out.println("Sample " + i);
						for (int j = 0; j < Constants.DEV_BATCH_NUM_GAMES; j++) {
							while (NUM_GAMES_RUNNING.get() >= Constants.DEV_BATCH_NUM_CONCURRENT_GAMES) Thread.sleep(500);
							NUM_GAMES_RUNNING.incrementAndGet();
							System.out.println("Game " + j);
							new GameThread(args).start();
						}
						while (NUM_GAMES_RUNNING.get() > 0) Thread.sleep(1000);
						Processor.finishCurrentSample();
					}
					Processor.displayBatchValues();
					long elapsed = (System.currentTimeMillis() - startTime) / 1000;
					long hours = elapsed / 3600;
					long minutes = (elapsed % 3600) / 60;
					long seconds = (elapsed % 3600) % 60;
					System.out.printf("Elapsed time: %d:%02d:%02d\n", hours, minutes, seconds);
				} else {
					UTTT game = new UTTT(args);
					GUI gui = new GUI(game);
					gui.setVisible(true);
					game.setGUI(gui);
					game.start();
				}
			} else { // command line
				if (args.length < 2) {
					System.err.println("Usage: UTTT bot1 bot2 [sample size] [# games per sample] [# concurrent games]");
					return;
				}
				boolean isBatch = false;
				if (args.length >= 3) isBatch = true;
				int sampleSize = -1;
				int gamesPerSample = -1;
				int numConcurrentGames = 1;
				try {
					if (isBatch) {
						sampleSize = Integer.parseInt(args[2]);
						gamesPerSample = Integer.parseInt(args[3]);
						if (args.length >= 5) numConcurrentGames = Integer.parseInt(args[4]);
					}
				} catch (NumberFormatException e) {
					System.err.println("Invalid 3rd, 4th, or 5th arg");
					System.err.println("Usage: UTTT bot1 bot2 [sample size] [# games per sample] [# concurrent games]");
					return;
				}
				if (isBatch) {
					for (int i = 0; i < Constants.DEV_BATCH_SAMPLE_SIZE; i++) {
						System.out.println("Sample " + i);
						for (int j = 0; j < Constants.DEV_BATCH_NUM_GAMES; j++) {
							while (NUM_GAMES_RUNNING.get() >= Constants.DEV_BATCH_NUM_CONCURRENT_GAMES) Thread.sleep(500);
							NUM_GAMES_RUNNING.incrementAndGet();
							System.out.println("Game " + j);
							new GameThread(args).start();
						}
						while (NUM_GAMES_RUNNING.get() > 0) Thread.sleep(1000);
						Processor.finishCurrentSample();
					}
					Processor.displayBatchValues();
				} else {
					UTTT game = new UTTT(args);
					GUI gui = new GUI(game);
					gui.setVisible(true);
					game.setGUI(gui);
					game.start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Engine engine; // runs game
	public GameHandler processor; // handles data
	private List<Player> players;
	private MacroField mMacroField;
	private GUI gui;

	public UTTT(String args[]) throws Exception {
		players = new ArrayList<Player>(Constants.MAX_PLAYERS);
		// add players
		if (Constants.DEV_MODE) {
			if (Constants.TEST_BOT_1 == null || Constants.TEST_BOT_1.isEmpty() || Constants.TEST_BOT_2 == null
					|| Constants.TEST_BOT_2.isEmpty()) {
				throw new RuntimeException("DEV_MODE: Please set 'TEST_BOT_1' and 'TEST_BOT_2' in your main class.");
			}
			createPlayer(Constants.TEST_BOT_1, 1);
			createPlayer(Constants.TEST_BOT_2, 2);
		} else {
			// add the bots from the arguments if not in DEV_MODE
			if (args.length < 2) {
				throw new RuntimeException("Two bot commands needed.");
			}

			// add the players
			createPlayer(args[0], 1);
			createPlayer(args[1], 2);
		}

		mMacroField = new MacroField();
		processor = new Processor(players, mMacroField);
		engine = new Engine();
	}

	@Override
	public void createPlayer(String command, int id) {
		try {
			// Create new process
			Process process = Runtime.getRuntime().exec(command);
			// Attach IO to process
			IOPlayer ioPlayer = new IOPlayer(process, "ID_" + id);
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
		if (!Constants.DEV_BATCH_MODE && gui == null) throw new RuntimeException("Not in batch mode: Please set GUI in UTTT before starting.");
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

		if (Constants.DEV_MODE) { // print the game file when in DEV_MODE
			String playedGame = processor.getPlayedGame();
			System.out.println(playedGame);
			if (!Constants.DEV_BATCH_MODE) {
				if (Constants.OUTPUT_BOT_1_ERROR) {
					System.out.println("Player " + 1 + " Stderr");
					System.out.println(players.get(0).getStderr());
				}
				if (Constants.OUTPUT_BOT_2_ERROR) {
					System.out.println("Player " + 2 + " Stderr");
					System.out.println(players.get(1).getStderr());
				}
			}
		} else { // save the game to database
			try {
				saveGame();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done.");
		if (Constants.DEV_BATCH_MODE) {
			processor.updateCurrentSampleValues();
			NUM_GAMES_RUNNING.decrementAndGet();
		} else {
			gui.finishGame();
		}
	}

	/**
	 * Does everything that is needed to store the output of a game
	 */
	public void saveGame() {
		// save results to file here
		String playedGame = processor.getPlayedGame();
		System.out.println(playedGame);
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
