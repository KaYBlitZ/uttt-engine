package com.theaigames.engine.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.theaigames.uttt.Constants;
import com.theaigames.uttt.UTTT;
import com.theaigames.uttt.field.MacroField;
import com.theaigames.uttt.moves.Move;

@SuppressWarnings("serial")
public class BoardPanel extends JPanel {

	private UTTT game;
	// Moves used when the game is over
	private Move selectedMove;
	private Move lastMove;

	public BoardPanel(UTTT game) {
		this.game = game;
		this.setPreferredSize(new Dimension(Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		int thirdWidth = getWidth() / 3;
		int thirdHeight = getHeight() / 3;

		if (!game.isGameOver()) {
			// draw mini boards
			int macro[] = game.getMacroBoard();
			int playerId = game.getCurrentPlayerId();
			g2.setStroke(new BasicStroke(Constants.MINI_STROKE_WIDTH));
			drawMiniTTT(0, 0, thirdWidth, thirdHeight, g2, getMiniBoardColor(playerId, macro[0])); // top-left
			drawMiniTTT(thirdWidth, 0, thirdWidth, thirdHeight, g2, getMiniBoardColor(playerId, macro[1])); // top
			drawMiniTTT(thirdWidth * 2, 0, thirdWidth, thirdHeight, g2, getMiniBoardColor(playerId, macro[2])); // top-right
			drawMiniTTT(0, thirdHeight, thirdWidth, thirdHeight, g2, getMiniBoardColor(playerId, macro[3])); // left-middle
			drawMiniTTT(thirdWidth, thirdHeight, thirdWidth, thirdHeight, g2, getMiniBoardColor(playerId, macro[4])); // middle
			drawMiniTTT(thirdWidth * 2, thirdHeight, thirdWidth, thirdHeight, g2,
					getMiniBoardColor(playerId, macro[5])); // right-middle
			drawMiniTTT(0, thirdHeight * 2, thirdWidth, thirdHeight, g2, getMiniBoardColor(playerId, macro[6])); // bottom-left
			drawMiniTTT(thirdWidth, thirdHeight * 2, thirdWidth, thirdHeight, g2,
					getMiniBoardColor(playerId, macro[7])); // bottom
			drawMiniTTT(thirdWidth * 2, thirdHeight * 2, thirdWidth, thirdHeight, g2,
					getMiniBoardColor(playerId, macro[8])); // bottom-right

			// draw markers
			int field[] = game.getField();
			g2.setStroke(new BasicStroke(Constants.MARKER_STROKE_WIDTH));
			drawMarkers(g2, field, getWidth(), getHeight());
		} else {
			if (selectedMove != null) {
				// draw mini boards
				int macro[] = selectedMove.getMacro();
				// we need the next player's id to get the colors right
				int playerId = selectedMove.getPlayerId() == 1 ? 2 : 1;
				g2.setStroke(new BasicStroke(Constants.MINI_STROKE_WIDTH));
				drawMiniTTT(0, 0, thirdWidth, thirdHeight, g2, getMiniBoardColor(playerId, macro[0])); // top-left
				drawMiniTTT(thirdWidth, 0, thirdWidth, thirdHeight, g2, getMiniBoardColor(playerId, macro[1])); // top
				drawMiniTTT(thirdWidth * 2, 0, thirdWidth, thirdHeight, g2, getMiniBoardColor(playerId, macro[2])); // top-right
				drawMiniTTT(0, thirdHeight, thirdWidth, thirdHeight, g2, getMiniBoardColor(playerId, macro[3])); // left-middle
				drawMiniTTT(thirdWidth, thirdHeight, thirdWidth, thirdHeight, g2,
						getMiniBoardColor(playerId, macro[4])); // middle
				drawMiniTTT(thirdWidth * 2, thirdHeight, thirdWidth, thirdHeight, g2,
						getMiniBoardColor(playerId, macro[5])); // right-middle
				drawMiniTTT(0, thirdHeight * 2, thirdWidth, thirdHeight, g2, getMiniBoardColor(playerId, macro[6])); // bottom-left
				drawMiniTTT(thirdWidth, thirdHeight * 2, thirdWidth, thirdHeight, g2,
						getMiniBoardColor(playerId, macro[7])); // bottom
				drawMiniTTT(thirdWidth * 2, thirdHeight * 2, thirdWidth, thirdHeight, g2,
						getMiniBoardColor(playerId, macro[8])); // bottom-right

				// draw markers
				int field[] = selectedMove.getField();
				g2.setStroke(new BasicStroke(Constants.MARKER_STROKE_WIDTH));
				drawMarkers(g2, field, getWidth(), getHeight());
			}
		}

		g2.setColor(Color.BLACK);
		// draw large TTT board
		g2.setStroke(new BasicStroke(Constants.MACRO_STROKE_WIDTH));
		g2.drawLine(thirdWidth, 0, thirdWidth, getHeight());
		g2.drawLine(thirdWidth * 2, 0, thirdWidth * 2, getHeight());
		g2.drawLine(0, thirdHeight, getWidth(), thirdHeight);
		g2.drawLine(0, thirdHeight * 2, getWidth(), thirdHeight * 2);
	}
	
	public void drawMiniTTT(int x, int y, int width, int height, Graphics g, Color color) {
		// color background
		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 128));
		g.fillRect(x, y, width, height);
		// draw board
		g.setColor(color);
		g.drawLine(x + width / 3, y, x + width / 3, y + height);
		g.drawLine(x + width * 2 / 3, y, x + width * 2 / 3, y + height);
		g.drawLine(x, y + height / 3, x + width, y + height / 3);
		g.drawLine(x, y + height * 2 / 3, x + width, y + height * 2 / 3);
	}

	public void drawMarkers(Graphics g, int field[], int width, int height) {
		float ninthWidth = width / 9f;
		float ninthHeight = height / 9f;
		int offset = 10;

		for (int i = 0; i < field.length; i++) {
			int row = i / MacroField.FIELD_COLUMNS;
			int col = i % MacroField.FIELD_COLUMNS;
			float x = col * ninthWidth;
			float y = row * ninthHeight;
			// draw pink box to show move made
			g.setColor(Color.GRAY);
			if (game.isGameOver() && selectedMove != null) {
				if (selectedMove.getRow() == row && selectedMove.getColumn() == col) {
					g.fillRect((int) x, (int) y, (int) ninthWidth, (int) ninthHeight);
				}
			} else {
				Move move = getLastMove();
				if (move.getRow() == row && move.getColumn() == col) {
					g.fillRect((int) x, (int) y, (int) ninthWidth, (int) ninthHeight);
				}
			}

			g.setColor(getPlayerColor(field[i]));

			if (field[i] == 1) { // draw x
				g.drawLine((int) (x + offset), (int) (y + offset), (int) (x + ninthWidth - offset),
						(int) (y + ninthHeight - offset));
				g.drawLine((int) (x + offset), (int) (y + ninthHeight - offset), (int) (x + ninthWidth - offset),
						(int) (y + offset));
			} else if (field[i] == 2) { // draw o
				g.drawOval((int) (col * ninthWidth + offset), (int) (row * ninthHeight + offset),
						(int) (ninthWidth - 2 * offset), (int) (ninthHeight - 2 * offset));
			}
		}
	}

	public Color getMiniBoardColor(int playerId, int macroValue) {
		if (macroValue == MacroField.MACRO_PLAYABLE) {
			if ((game.isGameOver() && selectedMove == null) || (selectedMove != null && selectedMove == lastMove)) {
				// First case is for when game just ended. We need to show
				// playable fields as open, not the other player's color.
				// Second case is for when game has ended and we are checking
				// the last move made in the list.
				// game is over show as open
				return Color.ORANGE;
			}
			return getPlayerColor(playerId);
		} else if (macroValue == MacroField.MACRO_UNPLAYABLE) {
			return Color.ORANGE; // tie or not playable
		} else { // return winner's color
			return getPlayerColor(macroValue);
		}
	}

	public Color getPlayerColor(int playerId) {
		return playerId == 1 ? Color.RED : Color.BLUE;
	}

	public void changeState(Move move) {
		selectedMove = move;
	}

	/**
	 * Gets the last move while the game is currently running
	 * 
	 * @return
	 */
	private Move getLastMove() {
		List<Move> moves = game.getMoves();
		return moves.get(moves.size() - 1);
	}

	/**
	 * Sets the last move after the game is finished
	 * 
	 * @param lastMove
	 */
	public void setLastMove(Move lastMove) {
		this.lastMove = lastMove;
	}
}
