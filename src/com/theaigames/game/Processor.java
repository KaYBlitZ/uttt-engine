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

import java.util.ArrayList;
import java.util.List;

import com.theaigames.uttt.Constants;
import com.theaigames.uttt.UTTTStarter;
import com.theaigames.uttt.field.Field;
import com.theaigames.uttt.move.ProcessorMove;
import com.theaigames.uttt.player.Player;

public class Processor implements GameHandler {
	private int mRoundNumber = 1, mMoveNumber = 1;
    private List<Player> mPlayers;
    private List<ProcessorMove> mMoves;
    private Field field;
    private UTTTStarter starter;
    private int mGameOverByPlayerErrorPlayerId = 0;
    private int gameNum;

    public Processor(List<Player> players, Field field, UTTTStarter starter, int gameNum) {
    	this.starter = starter;
    	this.gameNum = gameNum;
        mPlayers = players;
        this.field = field;
        mMoves = new ArrayList<ProcessorMove>(Constants.MAX_MOVES);
        
		// add initial move
		// The first move is player 1, but since we need the next player's
		// id in Board Panel, we use player 2 here in order to get player 1
		// there
		ProcessorMove move = new ProcessorMove(players.get(1));
		move.setColumn(-1);
		move.setRow(-1);
		move.setField(field.getField());
		move.setMicro(field.getMicroField());
		mMoves.add(move);
    }


    @Override
    public void playRound() {
    	/*
    	 * update game round i
    	 * update game move i
    	 * update gamefield [ ... ]
    	 * update game macroboard [ ... ]
    	 * action move t
    	 */
        for (Player player : mPlayers) {
			field.setCurrentPlayerId(player.getId());
			if (Constants.DELAY_MOVE) {
				try {
					Thread.sleep(Constants.MOVE_DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (!isGameOver()) {
				player.sendUpdate("round", mRoundNumber);
				player.sendUpdate("move", mMoveNumber);
				player.sendUpdate("field", field.getFieldString());
				player.sendUpdate("macroboard", field.getMicroFieldString());
				if (!starter.inBatchMode()) { 
					System.out.printf("Round %d, Move %d\n", mRoundNumber, mMoveNumber);
					System.out.println("Field: " + field.getFieldString());
					System.out.println("Micro: " + field.getMicroFieldString());
				}

                String response = player.requestMove("move");
				boolean success = parseResponse(response, player);
				ProcessorMove move = new ProcessorMove(player);
				move.setColumn(field.getLastColumn());
				move.setRow(field.getLastRow());
				move.setField(field.getField());
				move.setMicro(field.getMicroField());
				if (success) { // successful move
                    mMoves.add(move);
				} else { // 1st try bad move
                    move.setIllegalMove(field.getLastError() + " (first try)");
                    mMoves.add(move);
					player.sendUpdate("field", field.getFieldString());
					player.sendUpdate("macroboard", field.getMicroFieldString());
                    response = player.requestMove("move");
					success = parseResponse(response, player);
					move = new ProcessorMove(player);
					move.setColumn(field.getLastColumn());
					move.setRow(field.getLastRow());
					move.setField(field.getField());
					move.setMicro(field.getMicroField());
					if (success) {
                        mMoves.add(move);
					} else { // 2nd try bad move
                        move.setIllegalMove(field.getLastError() + " (second try)");
                        mMoves.add(move);
						player.sendUpdate("field", field.getFieldString());
						player.sendUpdate("macroboard", field.getMicroFieldString());
                        response = player.requestMove("move");
						success = parseResponse(response, player);
						move = new ProcessorMove(player);
						move.setColumn(field.getLastColumn());
						move.setRow(field.getLastRow());
						move.setField(field.getField());
						move.setMicro(field.getMicroField());
						if (success) {
							mMoves.add(move);
                        } else { /* Too many errors, other player wins */
							// 3rd try bad move, game over
                            move.setIllegalMove(field.getLastError() + " (last try)");
                            mMoves.add(move);
                            mGameOverByPlayerErrorPlayerId = player.getId();
                        }
                    }
                }
				mMoveNumber++;
			}
        }
        mRoundNumber++; // round increases after both players play
		if (isGameOver() && !starter.inBatchMode()) {
			System.out.println("Final State:");
			System.out.println("Field: " + field.getFieldString());
			System.out.println("Micro: " + field.getMicroFieldString());
			System.out.println("The winner is player " + getWinner());
		}
    }
    
    /**
     * Parses player response and inserts disc in field
     * @param args : command line arguments passed on running of application
     * @return : true if valid move, otherwise false
     */
    private Boolean parseResponse(String r, Player player) {
        String[] parts = r.split(" ");
		if (parts.length >= 3 && parts[0].equals("place_move")) {
            int column = Integer.parseInt(parts[1]);
			int row = Integer.parseInt(parts[2]);
			if (field.makeMove(column, row, player.getId())) {
				if (!starter.inBatchMode()) { 
					System.out.printf("%d plays (%d %d). Next micro: (%d, %d)\n", player.getId(), column, row,
							field.getNextMicro().column, field.getNextMicro().row);
				}
                return true;
            }
        }
		// field.mLastError = "Unknown command";
        return false;
    }

    @Override
    public int getRoundNumber() {
        return this.mRoundNumber;
    }

    @Override
	public int getWinner() {
        if (mGameOverByPlayerErrorPlayerId > 0) { /* Game over due to too many player errors. Look up the other player, which became the winner */
            return (mGameOverByPlayerErrorPlayerId == 1 ? 2 : 1);
        }
		return field.getWinner(); // -1 for ongoing, 0 for tie, else winner
    }
    
    /**
     * Returns a List of Moves played in this game
     * @param args : 
     * @return : List with Move objects
     */
	@Override
    public List<ProcessorMove> getMoves() {
        return mMoves;
    }
    
	public Field getField() {
        return field;
    }

    @Override
    public boolean isGameOver() {
		// -1 is game still ongoing; 0 is tied game; else winner
		return getWinner() != -1;
    }
    
    public void updateCurrentSampleValues() {
    	if (mGameOverByPlayerErrorPlayerId > 0) /* Game over due to too many player errors. Look up the other player, which became the winner */
    		starter.updateCurrentSampleValues(-1, gameNum);
        starter.updateCurrentSampleValues(getWinner(), gameNum);
    }
}
