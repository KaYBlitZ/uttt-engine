package com.theaigames.uttt;

public class Constants {
	public static final int MAX_MOVES = 81;
	public static final int MAX_ROUNDS = 41;
	
	public static final int TIMEBANK_MAX = 10000;
	public static final int TIME_PER_MOVE = 500;
	public static final int MAX_PLAYERS = 2;
	
	public static final boolean DEV_MODE = true; // turn this on for local testing
	public static final boolean DEV_BATCH_MODE = true; // turn on to run multiple games
	// For each sample each bot plays half the games as player 1 and the other half as player 2, valid only when batch mode is true
	public static final boolean DEV_BATCH_MODE_HALF_AND_HALF = true;
	public static final int DEV_BATCH_SAMPLE_SIZE = 1; // # samples
	public static final int DEV_BATCH_NUM_GAMES = 1000; // # games per sample
	public static final int DEV_BATCH_NUM_CONCURRENT_GAMES = 2; // max # games running at any time
	//public static final String TEST_BOT_1 = "java -cp D:\\Users\\Kenneth\\git\\UTTTBots\\bin com.kayblitz.uttt.bot.RandomBot";
	//public static final String TEST_BOT_1 = "java -cp D:\\Users\\Kenneth\\git\\UTTTBots\\bin com.kayblitz.uttt.bot.PlayerBot";
	//public static final String TEST_BOT_1 = "java -cp D:\\Users\\Kenneth\\git\\UTTTBots\\bin com.kayblitz.uttt.bot.MinimaxBot 7 3";
	//public static final String TEST_BOT_1 = "java -cp D:\\Users\\Kenneth\\git\\UTTTBots\\bin com.kayblitz.uttt.bot.IterativeDeepeningMinimaxBot 3";
	public static final String TEST_BOT_1 = "java -cp D:\\Users\\Kenneth\\git\\UTTTBots\\bin com.kayblitz.uttt.bot.mcts.MCTSBot 2 2";
	
	//public static final String TEST_BOT_2 = "java -cp D:\\Users\\Kenneth\\git\\UTTTBots\\bin com.kayblitz.uttt.bot.RandomBot";
	//public static final String TEST_BOT_2 = "java -cp D:\\Users\\Kenneth\\git\\UTTTBots\\bin com.kayblitz.uttt.bot.PlayerBot";
	//public static final String TEST_BOT_2 = "java -cp D:\\Users\\Kenneth\\git\\UTTTBots\\bin com.kayblitz.uttt.bot.MinimaxBot 7 3";
	//public static final String TEST_BOT_2 = "java -cp D:\\Users\\Kenneth\\git\\UTTTBots\\bin com.kayblitz.uttt.bot.IterativeDeepeningMinimaxBot 3";
	public static final String TEST_BOT_2 = "java -cp D:\\Users\\Kenneth\\git\\UTTTBots\\bin com.kayblitz.uttt.bot.mcts.MCTSBot 2 2";
	
	// set to true when using human bot to prevent timeouts
	// set to false when using bots
	public static final boolean DISABLE_TIMEBANK = false;
	public static final boolean OUTPUT_BOT_1_ERROR = true; // disabled in batch mode
	public static final boolean OUTPUT_BOT_2_ERROR = true; // disabled in batch mode
	
	public static final int GUI_WIDTH = 800;
	public static final int FIELD_WIDTH = 500;
	public static final int FIELD_HEIGHT = 500;
	public static final int MOVE_LIST_WIDTH = 200;
	public static final int MOVE_LIST_HEIGHT = FIELD_HEIGHT;
	public static final int ERROR_WIDTH = FIELD_WIDTH + MOVE_LIST_WIDTH;
	public static final int ERROR_HEIGHT = 30;
	public static final int GUI_HEIGHT = FIELD_HEIGHT + ERROR_HEIGHT + 100;
	public static final int MICRO_STROKE_WIDTH = 10;
	public static final int MINI_STROKE_WIDTH = 5;
	public static final int MARKER_STROKE_WIDTH = 3;

	public static final boolean DELAY_MOVE = false; // delay move to watch it play out slowly
	public static final int MOVE_DELAY = 500; // time to delay moves in milliseconds
	public static final int FPS = 10; // gui refresh rate per second
}
