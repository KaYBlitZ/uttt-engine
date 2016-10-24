package com.theaigames.uttt;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.theaigames.gui.GUI;

public class UTTTStarter {
	
	public static void main(String[] args) {
		UTTTStarter engine = new UTTTStarter(args);
		engine.start();
	}
	
	private class GameThread extends Thread {
		private String bot1, bot2;
		private UTTTStarter starter;
		private int gameNum;
		
		public GameThread(String bot1, String bot2, UTTTStarter starter, int gameNum) {
			this.bot1 = bot1;
			this.bot2 = bot2;
			this.starter = starter;
			this.gameNum = gameNum;
		}
		
		@Override
		public void run() {
			try {
				UTTT game = new UTTT(bot1, bot2, starter, gameNum);
				if (heuristics != null)
					game.updateHeuristics(heuristics);
				if (raveConstants != null)
					game.updateRAVEConstants(raveConstants);
				if (raveHeuristicConstants != null)
					game.updateRAVEHeuristicConstants(raveHeuristicConstants);
				if (simulationConstants != null)
					game.updateSimulationConstants(simulationConstants);
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
	private boolean disableTimebank;
	private boolean inBatchMode, inHalfAndHalfMode;
	private boolean outputBot1Error = true, outputBot2Error = true;
	private boolean isFinished, started;
	private float avgP1Wins, avgP2Wins, avgTies, avgTimeouts;
	private boolean disableOutput = false;
	
	// For CMA-ES
	private String heuristics, raveConstants, raveHeuristicConstants, simulationConstants;
	private boolean seedBot1, seedBot2;
	
	public UTTTStarter() {
		this(false);
	}
	
	public UTTTStarter(boolean batchMode) {
		this.inBatchMode = batchMode;
		if (inBatchMode) {
			PLAYER_ONE_WINS = new AtomicInteger();
			PLAYER_TWO_WINS = new AtomicInteger();
			TIES = new AtomicInteger();
			TIMEOUTS = new AtomicInteger();
			sampleP1Wins = new ArrayList<Integer>(sampleSize);
			sampleP2Wins = new ArrayList<Integer>(sampleSize);
			sampleTies = new ArrayList<Integer>(sampleSize);
			sampleTimeouts = new ArrayList<Integer>(sampleSize);
		}
	}
	
	public UTTTStarter(String[] args) {
		if (Constants.DEV_MODE) {
			bot1 = Constants.TEST_BOT_1;
			bot2 = Constants.TEST_BOT_2;
			sampleSize = Constants.DEV_BATCH_SAMPLE_SIZE;
			numGamesPerSample = Constants.DEV_BATCH_NUM_GAMES;
			numConcurrentGames = Constants.DEV_BATCH_NUM_CONCURRENT_GAMES;
			disableTimebank = Constants.DISABLE_TIMEBANK;
			inBatchMode = Constants.DEV_BATCH_MODE;
			inHalfAndHalfMode = Constants.DEV_BATCH_MODE_HALF_AND_HALF;
			outputBot1Error = Constants.OUTPUT_BOT_1_ERROR;
			outputBot2Error = Constants.OUTPUT_BOT_2_ERROR;
		} else {
			if (args.length < 2) {
				throw new RuntimeException("Usage: UTTT bot1 bot2 [sample size] [# games per sample] [# concurrent games] [half & half mode (1 or 0)]");
			}
			if (args.length >= 3)
				inBatchMode = true;
			numConcurrentGames = 1;
			try {
				if (inBatchMode) {
					sampleSize = Integer.parseInt(args[2]);
					numGamesPerSample = Integer.parseInt(args[3]);
					if (args.length >= 5)
						numConcurrentGames = Integer.parseInt(args[4]);
					if (args.length >= 6)
						inHalfAndHalfMode = Integer.parseInt(args[5]) > 0;
				}
			} catch (NumberFormatException e) {
				System.err.println("Invalid 3rd, 4th, 5th, or 6th arg");
				throw new RuntimeException("Usage: UTTT bot1 bot2 [sample size] [# games per sample] [# concurrent games] [half & half mode (1 or 0)]");
			}
			bot1 = args[0];
			bot2 = args[1];
		}
		if (inBatchMode) {
			PLAYER_ONE_WINS = new AtomicInteger();
			PLAYER_TWO_WINS = new AtomicInteger();
			TIES = new AtomicInteger();
			TIMEOUTS = new AtomicInteger();
			sampleP1Wins = new ArrayList<Integer>(sampleSize);
			sampleP2Wins = new ArrayList<Integer>(sampleSize);
			sampleTies = new ArrayList<Integer>(sampleSize);
			sampleTimeouts = new ArrayList<Integer>(sampleSize);
		}
	}
	
	public void start() {
		started = true;
		if (bot1 == null || bot2 == null)
			throw new RuntimeException("Please call UTTTEngine.setBots()");
		if (inBatchMode && inHalfAndHalfMode && numGamesPerSample % 2 == 1)
			throw new RuntimeException("In half & half mode the number of games per sample should be an even number");
		try {
			if (inBatchMode) {
				long startTime = System.currentTimeMillis();
				for (int i = 0; i < sampleSize; i++) {
					if (!disableOutput)
						System.out.println("Sample " + i);
					for (int j = 0; j < numGamesPerSample; j++) {
						while (NUM_GAMES_RUNNING.get() >= numConcurrentGames)
							Thread.sleep(500);
						NUM_GAMES_RUNNING.incrementAndGet();
						if (!disableOutput)
							System.out.println("Game " + j);
						String b1 = seedBot1 ? bot1 + " " + j : bot1;
						String b2 = seedBot2 ? bot2 + " " + j : bot2;
						if (inHalfAndHalfMode && j < numGamesPerSample / 2) {
							// if half & half, switch bots for first half
							new GameThread(b2, b1, this, j).start();
						} else {
							new GameThread(b1, b2, this, j).start();
						}
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
				if (!disableOutput)
					System.out.printf("Elapsed time: %d:%02d:%02d\n", hours, minutes, seconds);
				finish();
			} else {
				UTTT game = new UTTT(bot1, bot2, this, 0);
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
	
	/** Will only be called in batch mode **/
	public void updateCurrentSampleValues(int winner, int gameNum) {
		if (winner == -1) { /* Game over due to too many player errors. Look up the other player, which became the winner */
    		TIMEOUTS.incrementAndGet();
    	} else if (winner == 1) {
    		if (inHalfAndHalfMode && gameNum < numGamesPerSample / 2) {
    			// in first half of half & half mode, player 1 is actually bot 2
    			PLAYER_TWO_WINS.incrementAndGet();
    		} else {
    			PLAYER_ONE_WINS.incrementAndGet();
    		}
        } else if (winner == 2) {
        	if (inHalfAndHalfMode && gameNum < numGamesPerSample / 2) {
    			// in first half of half & half mode, player 2 is actually bot 1
    			PLAYER_ONE_WINS.incrementAndGet();
    		} else {
    			PLAYER_TWO_WINS.incrementAndGet();
    		}
        } else {
        	TIES.incrementAndGet();
        }
	}
    
    private void displayBatchValues() {
    	if (!disableOutput) {
    		System.out.println("# Samples = " + sampleSize + ", # Games per Sample = " + numGamesPerSample);
    		System.out.println("Sample #, P1 Wins, P2 Wins, Ties, Timeouts");
    	}
    	for (int i = 0; i < sampleSize; i++) {
    		avgP1Wins += sampleP1Wins.get(i);
    		avgP2Wins += sampleP2Wins.get(i);
    		avgTies += sampleTies.get(i);
    		avgTimeouts += sampleTimeouts.get(i);
    		if (!disableOutput)
    			System.out.printf("%d, %d, %d, %d, %d\n", i, sampleP1Wins.get(i), sampleP2Wins.get(i), sampleTies.get(i), sampleTimeouts.get(i));
    	}
    	avgP1Wins /= sampleSize;
    	avgP2Wins /= sampleSize;
    	avgTies /= sampleSize;
    	avgTimeouts /= sampleSize;
    	if (!disableOutput)
    		System.out.printf("Mean, %f, %f, %f, %f\n", avgP1Wins, avgP2Wins, avgTies, avgTimeouts);
    }
    
    /** Heuristics must be updated before calling start() **/
    public void updateHeuristics(double[] heuristics) {
    	if (started)
    		throw new RuntimeException("Heuristics must be updated before calling start()");
    	String s = "";
    	for (int i = 0; i < heuristics.length - 1; i++) {
    		s += String.valueOf(heuristics[i]) + " ";
    	}
    	s += String.valueOf(heuristics[heuristics.length - 1]);
    	this.heuristics = s;
    }
    
    public void updateRAVEConstants(double explorationConstant, double raveConstant) {
    	if (started)
    		throw new RuntimeException("RAVE constants must be updated before calling start()");
    	raveConstants = String.valueOf(explorationConstant) + " " + String.valueOf(raveConstant);
    }
    
    public void updateRAVEHeuristicConstants(double heuristicMultiplier, double uctConfidence, double amafConfidence) {
    	if (started)
    		throw new RuntimeException("RAVE heuristic constants must be updated before calling start()");
    	raveHeuristicConstants = String.valueOf(heuristicMultiplier) + " " + String.valueOf(uctConfidence) + 
    			" " + String.valueOf(amafConfidence);
    }
    
    public void updateSimulationConstants(int raveHeuristic) {
    	if (started)
    		throw new RuntimeException("Heuristic simulation constants must be updated before calling start()");
    	simulationConstants = String.valueOf(raveHeuristic);
    }
    
    /** Valid only in batch mode. Seeds the corresponding bots with the iteration number. For a sample size of 100 games,
     * the bots will be seeded with 0 for the first game, 1 for the second game, and so on and so forth up to 99 for the last game.
     */
    public void seedBots(boolean bot1Seeded, boolean bot2Seeded) {
    	if (started)
    		throw new RuntimeException("Bots must be seeded before calling start()");
    	this.seedBot1 = bot1Seeded;
    	this.seedBot2 = bot2Seeded;
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
	
	public void enableHalfAndHalfMode(boolean enabled) {
		this.inHalfAndHalfMode = enabled;
	}
	
	public void disableOutput(boolean disabled) {
		this.disableOutput = disabled;
	}
	
	public void disableTimebank(boolean disabled) {
		this.disableTimebank = disabled;
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
