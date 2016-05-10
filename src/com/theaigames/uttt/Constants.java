package com.theaigames.uttt;

public class Constants {
	public static final int MAX_MOVES = 81;
	public static final int MAX_ROUNDS = 41;
	
	public static final int TIMEBANK_MAX = 10000;
	public static final int TIME_PER_MOVE = 500;
	public static final int MAX_PLAYERS = 2;
	
	public static final boolean DEV_MODE = true; // turn this on for local testing
	public static final String TEST_BOT_1 = "java -cp D:\\Users\\Kenneth\\git\\UTTTBots\\bin com.kayblitz.uttt.bot.RandomBot";
	//public static final String TEST_BOT_2 = "java -cp D:\\Users\\Kenneth\\git\\UTTTBots\\bin com.kayblitz.uttt.bot.RandomBot";
	//public static final String TEST_BOT_2 = "java -cp D:\\Users\\Kenneth\\git\\UTTTBots\\bin com.kayblitz.uttt.bot.PlayerBot";
	public static final String TEST_BOT_2 = "java -cp D:\\Users\\Kenneth\\git\\UTTTBots\\bin com.kayblitz.uttt.bot.AlphabetaBot";
	
	// set to true when using human bot to prevent timeouts
	// set to false when using bots
	public static final boolean DISABLE_TIMEBANK = false;
	public static final boolean OUTPUT_BOT_ERROR = true;
	
	public static final int GUI_WIDTH = 800;
	public static final int BOARD_WIDTH = 500;
	public static final int BOARD_HEIGHT = 500;
	public static final int MOVE_LIST_WIDTH = 200;
	public static final int MOVE_LIST_HEIGHT = BOARD_HEIGHT;
	public static final int ERROR_WIDTH = BOARD_WIDTH + MOVE_LIST_WIDTH;
	public static final int ERROR_HEIGHT = 30;
	public static final int GUI_HEIGHT = BOARD_HEIGHT + ERROR_HEIGHT + 100;
	public static final int MACRO_STROKE_WIDTH = 10;
	public static final int MINI_STROKE_WIDTH = 5;
	public static final int MARKER_STROKE_WIDTH = 3;

	public static final boolean DELAY_MOVE = true;
	public static final int MOVE_DELAY = 0;
	public static final int FPS = 10;
}
