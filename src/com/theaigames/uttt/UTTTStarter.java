package com.theaigames.uttt;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.theaigames.gui.GUI;

public class UTTTStarter {
	
	public static void main(String[] args) {
		UTTTStarter engine = new UTTTStarter();
		if (Constants.DEV_MODE) {
			engine.setBots(Constants.TEST_BOT_1, Constants.TEST_BOT_2);
			engine.setSampleSize(Constants.DEV_BATCH_SAMPLE_SIZE);
			engine.setNumGamesPerSample(Constants.DEV_BATCH_NUM_GAMES);
			engine.setNumConcurrentGames(Constants.DEV_BATCH_NUM_CONCURRENT_GAMES);
			engine.disableTimebank(Constants.DISABLE_TIMEBANK);
			engine.enableBatchMode(Constants.DEV_BATCH_MODE);
			engine.setOutputBot1Error(Constants.OUTPUT_BOT_1_ERROR);
			engine.setOutputBot2Error(Constants.OUTPUT_BOT_2_ERROR);
		} else {
			if (args.length < 2) {
				System.err.println("Usage: UTTT bot1 bot2 [sample size] [# games per sample] [# concurrent games]");
				return;
			}
			if (args.length >= 3)
				engine.enableBatchMode(true);
			engine.setNumConcurrentGames(1);
			try {
				if (engine.inBatchMode()) {
					engine.setSampleSize(Integer.parseInt(args[2]));
					engine.setNumGamesPerSample(Integer.parseInt(args[3]));
					if (args.length >= 5)
						engine.setNumConcurrentGames(Integer.parseInt(args[4]));
				}
			} catch (NumberFormatException e) {
				System.err.println("Invalid 3rd, 4th, or 5th arg");
				System.err.println("Usage: UTTT bot1 bot2 [sample size] [# games per sample] [# concurrent games]");
				return;
			}
			engine.setBots(args[0], args[1]);
		}
		if (engine.inBatchMode())
			engine.initBatchMode();
		engine.start();
	}
	
	private static class GameThread extends Thread {
		private String bot1, bot2;
		private UTTTStarter starter;
		
		public GameThread(String bot1, String bot2, UTTTStarter starter) {
			this.bot1 = bot1;
			this.bot2 = bot2;
			this.starter = starter;
		}
		
		@Override
		public void run() {
			try {
				UTTT game = new UTTT(bot1, bot2, starter);
				game.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// for batch
	private AtomicInteger PLAYER_ONE_WINS, PLAYER_TWO_WINS, TIES, TIMEOUTS;
	private ArrayList<Integer> sampleP1Wins, sampleP2Wins, sampleTies, sampleTimeouts;
	
	public final AtomicInteger NUM_GAMES_RUNNING = new AtomicInteger();
	private String bot1, bot2;
	private int sampleSize = 1, numGamesPerSample = 500, numConcurrentGames = 3;
	private boolean disableTimebank = false;
	private boolean inBatchMode = false;
	private boolean outputBot1Error = true, outputBot2Error = true;
	private boolean isFinished = false;
	private float avgP1Wins, avgP2Wins, avgTies, avgTimeouts;
	
	private void initBatchMode() {
		PLAYER_ONE_WINS = new AtomicInteger();
		PLAYER_TWO_WINS = new AtomicInteger();
		TIES = new AtomicInteger();
		TIMEOUTS = new AtomicInteger();
		sampleP1Wins = new ArrayList<Integer>(sampleSize);
		sampleP2Wins = new ArrayList<Integer>(sampleSize);
		sampleTies = new ArrayList<Integer>(sampleSize);
		sampleTimeouts = new ArrayList<Integer>(sampleSize);
	}
	
	public void start() {
		if (bot1 == null || bot2 == null)
			throw new RuntimeException("Please call UTTTEngine.setBots()");
		try {
			if (inBatchMode) {
				long startTime = System.currentTimeMillis();
				for (int i = 0; i < sampleSize; i++) {
					System.out.println("Sample " + i);
					for (int j = 0; j < numGamesPerSample; j++) {
						while (NUM_GAMES_RUNNING.get() >= numConcurrentGames)
							Thread.sleep(500);
						NUM_GAMES_RUNNING.incrementAndGet();
						System.out.println("Game " + j);
						new GameThread(bot1, bot2, this).start();
					}
					while (NUM_GAMES_RUNNING.get() > 0)
						Thread.sleep(1000);
					finishCurrentSample();
				}
				displayBatchValues();
				long elapsed = (System.currentTimeMillis() - startTime) / 1000;
				long hours = elapsed / 3600;
				long minutes = (elapsed % 3600) / 60;
				long seconds = (elapsed % 3600) % 60;
				System.out.printf("Elapsed time: %d:%02d:%02d\n", hours, minutes, seconds);
				finish();
			} else {
				UTTT game = new UTTT(bot1, bot2, this);
				GUI gui = new GUI(game);
				gui.setVisible(true);
				game.setGUI(gui);
				game.start();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void finish() {
		isFinished = true;
	}
	
	public boolean isFinished() {
		return isFinished;
	}
	
	private void finishCurrentSample() {
    	sampleP1Wins.add(PLAYER_ONE_WINS.get());
    	sampleP2Wins.add(PLAYER_TWO_WINS.get());
    	sampleTies.add(TIES.get());
    	sampleTimeouts.add(TIMEOUTS.get());
    	PLAYER_ONE_WINS.set(0);
    	PLAYER_TWO_WINS.set(0);
    	TIES.set(0);
    	TIMEOUTS.set(0);
    }
	
	public void updateCurrentSampleValues(int winner) {
		if (winner == -1) { /* Game over due to too many player errors. Look up the other player, which became the winner */
    		TIMEOUTS.incrementAndGet();
    	} else if (winner == 1) {
    		PLAYER_ONE_WINS.incrementAndGet();
        } else if (winner == 2) {
        	PLAYER_TWO_WINS.incrementAndGet();
        } else {
        	TIES.incrementAndGet();
        }
	}
    
    private void displayBatchValues() {
    	System.out.println("# Samples = " + sampleSize + ", # Games per Sample = " + numGamesPerSample);
    	System.out.println("Sample #, P1 Wins, P2 Wins, Ties, Timeouts");
    	for (int i = 0; i < sampleSize; i++) {
    		avgP1Wins += sampleP1Wins.get(i);
    		avgP2Wins += sampleP2Wins.get(i);
    		avgTies += sampleTies.get(i);
    		avgTimeouts += sampleTimeouts.get(i);
    		System.out.printf("%d, %d, %d, %d, %d\n", i, sampleP1Wins.get(i), sampleP2Wins.get(i), sampleTies.get(i), sampleTimeouts.get(i));
    	}
    	avgP1Wins /= sampleSize;
    	avgP2Wins /= sampleSize;
    	avgTies /= sampleSize;
    	avgTimeouts /= sampleSize;
    	System.out.printf("Mean, %f, %f, %f, %f\n", avgP1Wins, avgP2Wins, avgTies, avgTimeouts);
    }
    
    public float getAverageP1Wins() {
    	return avgP1Wins;
    }
    
    public float getAverageP2Wins() {
    	return avgP2Wins;
    }
    
    public float getAverageTies() {
    	return avgTies;
    }
    
    public float getAverageTimeouts() {
    	return avgTimeouts;
    }
	
	public void setBots(String bot1, String bot2) {
		this.bot1 = bot1;
		this.bot2 = bot2;
	}
	
	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}
	
	public void setNumGamesPerSample(int numGamesPerSample) {
		this.numGamesPerSample = numGamesPerSample;
	}
	
	public void setNumConcurrentGames(int numConcurrentGames) {
		this.numConcurrentGames = numConcurrentGames;
	}
	
	public void disableTimebank(boolean disabled) {
		this.disableTimebank = disabled;
	}
	
	public void enableBatchMode(boolean enabled) {
		this.inBatchMode = enabled;
	}
	
	public void setOutputBot1Error(boolean enabled) {
		outputBot1Error = enabled;
	}
	
	public void setOutputBot2Error(boolean enabled) {
		outputBot2Error = enabled;
	}
	
	public boolean inBatchMode() {
		return inBatchMode;
	}
	
	
	public boolean isTimebankDisabled() {
		return disableTimebank;
	}
	
	public int getSampleSize() {
		return sampleSize;
	}
	
	public int getNumGamesPerSample() {
		return numGamesPerSample;
	}
	
	public boolean outputBot1Error() {
		return outputBot1Error;
	}
	
	public boolean outputBot2Error() {
		return outputBot2Error;
	}
}
