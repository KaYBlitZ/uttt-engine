package com.theaigames.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.theaigames.uttt.Constants;
import com.theaigames.uttt.UTTT;
import com.theaigames.uttt.field.Field;
import com.theaigames.uttt.move.ProcessorMove;

@SuppressWarnings("serial")
public class BoardPanel extends JPanel {

	private UTTT game;
	private MoveErrorText errorText;
	// Moves used when the game is over
	private ProcessorMove selectedMove;
	private ProcessorMove lastMove;

	public BoardPanel(UTTT game, MoveErrorText errorText) {
		this.game = game;
		this.errorText = errorText;
		setPreferredSize(new Dimension(Constants.FIELD_WIDTH, Constants.FIELD_HEIGHT));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent ev) {
				int col = (int) (ev.getX() / (Constants.FIELD_WIDTH / 9f));
				int row = (int) (ev.getY() / (Constants.FIELD_HEIGHT / 9f));
				game.enterMove(col, row);
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseReleased(MouseEvent arg0) {}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		int thirdWidth = getWidth() / 3;
		int thirdHeight = getHeight() / 3;

		if (!game.isGameOver()) {
			// set error text
			ProcessorMove lastMove = getLastMove();
			if (lastMove.isIllegal()) {
				errorText.setError(lastMove.getIllegalMove());
			} else {
				errorText.setError(MoveErrorText.NO_ERROR);
			}
			
			// draw micro fields
			int macro[][] = game.getMacroField();
			int playerId = game.getCurrentPlayerId();
			g2.setStroke(new BasicStroke(Constants.MINI_STROKE_WIDTH));
			drawMicroField(0, 0, thirdWidth, thirdHeight, g2, getMicroFieldColor(playerId, macro[0][0])); // top-left
			drawMicroField(thirdWidth, 0, thirdWidth, thirdHeight, g2, getMicroFieldColor(playerId, macro[1][0])); // top
			drawMicroField(thirdWidth * 2, 0, thirdWidth, thirdHeight, g2, getMicroFieldColor(playerId, macro[2][0])); // top-right
			drawMicroField(0, thirdHeight, thirdWidth, thirdHeight, g2, getMicroFieldColor(playerId, macro[0][1])); // left-middle
			drawMicroField(thirdWidth, thirdHeight, thirdWidth, thirdHeight, g2, getMicroFieldColor(playerId, macro[1][1])); // middle
			drawMicroField(thirdWidth * 2, thirdHeight, thirdWidth, thirdHeight, g2,
					getMicroFieldColor(playerId, macro[2][1])); // right-middle
			drawMicroField(0, thirdHeight * 2, thirdWidth, thirdHeight, g2, getMicroFieldColor(playerId, macro[0][2])); // bottom-left
			drawMicroField(thirdWidth, thirdHeight * 2, thirdWidth, thirdHeight, g2,
					getMicroFieldColor(playerId, macro[1][2])); // bottom
			drawMicroField(thirdWidth * 2, thirdHeight * 2, thirdWidth, thirdHeight, g2,
					getMicroFieldColor(playerId, macro[2][2])); // bottom-right

			// draw markers
			int field[][] = game.getField();
			g2.setStroke(new BasicStroke(Constants.MARKER_STROKE_WIDTH));
			drawMarkers(g2, field, getWidth(), getHeight());
		} else {
			if (selectedMove != null) {
				// draw mini fields
				int macro[][] = selectedMove.getMacroField();
				// we need the next player's id to get the colors right
				int playerId = selectedMove.getPlayerId() == 1 ? 2 : 1;
				g2.setStroke(new BasicStroke(Constants.MINI_STROKE_WIDTH));
				drawMicroField(0, 0, thirdWidth, thirdHeight, g2, getMicroFieldColor(playerId, macro[0][0])); // top-left
				drawMicroField(thirdWidth, 0, thirdWidth, thirdHeight, g2, getMicroFieldColor(playerId, macro[1][0])); // top
				drawMicroField(thirdWidth * 2, 0, thirdWidth, thirdHeight, g2, getMicroFieldColor(playerId, macro[2][0])); // top-right
				drawMicroField(0, thirdHeight, thirdWidth, thirdHeight, g2, getMicroFieldColor(playerId, macro[0][1])); // left-middle
				drawMicroField(thirdWidth, thirdHeight, thirdWidth, thirdHeight, g2,
						getMicroFieldColor(playerId, macro[1][1])); // middle
				drawMicroField(thirdWidth * 2, thirdHeight, thirdWidth, thirdHeight, g2,
						getMicroFieldColor(playerId, macro[2][1])); // right-middle
				drawMicroField(0, thirdHeight * 2, thirdWidth, thirdHeight, g2, getMicroFieldColor(playerId, macro[0][2])); // bottom-left
				drawMicroField(thirdWidth, thirdHeight * 2, thirdWidth, thirdHeight, g2,
						getMicroFieldColor(playerId, macro[1][2])); // bottom
				drawMicroField(thirdWidth * 2, thirdHeight * 2, thirdWidth, thirdHeight, g2,
						getMicroFieldColor(playerId, macro[2][2])); // bottom-right

				// draw markers
				int field[][] = selectedMove.getField();
				g2.setStroke(new BasicStroke(Constants.MARKER_STROKE_WIDTH));
				drawMarkers(g2, field, getWidth(), getHeight());
			}
		}

		g2.setColor(Color.BLACK);
		// draw large TTT field
		g2.setStroke(new BasicStroke(Constants.MICRO_STROKE_WIDTH));
		g2.drawLine(thirdWidth, 0, thirdWidth, getHeight());
		g2.drawLine(thirdWidth * 2, 0, thirdWidth * 2, getHeight());
		g2.drawLine(0, thirdHeight, getWidth(), thirdHeight);
		g2.drawLine(0, thirdHeight * 2, getWidth(), thirdHeight * 2);
	}
	
	public void drawMicroField(int x, int y, int width, int height, Graphics g, Color color) {
		// color background
		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 128));
		g.fillRect(x, y, width, height);
		// draw field
		g.setColor(color);
		g.drawLine(x + width / 3, y, x + width / 3, y + height);
		g.drawLine(x + width * 2 / 3, y, x + width * 2 / 3, y + height);
		g.drawLine(x, y + height / 3, x + width, y + height / 3);
		g.drawLine(x, y + height * 2 / 3, x + width, y + height * 2 / 3);
	}

	public void drawMarkers(Graphics g, int field[][], int width, int height) {
		float ninthWidth = width / 9f;
		float ninthHeight = height / 9f;
		int offset = 10;
		
		for (int row = 0; row < Field.FIELD_ROWS; row++) {
			for (int col = 0; col < Field.FIELD_COLUMNS; col++) {
				float x = col * ninthWidth;
				float y = row * ninthHeight;
				// draw gray box to show move made
				g.setColor(Color.GRAY);
				if (game.isGameOver() && selectedMove != null) {
					if (selectedMove.getRow() == row && selectedMove.getColumn() == col) {
						g.fillRect((int) x, (int) y, (int) ninthWidth, (int) ninthHeight);
					}
				} else {
					ProcessorMove move = getLastMove();
					if (move.getRow() == row && move.getColumn() == col) {
						g.fillRect((int) x, (int) y, (int) ninthWidth, (int) ninthHeight);
					}
				}

				g.setColor(getPlayerColor(field[col][row]));

				if (field[col][row] == 1) { // draw x
					g.drawLine((int) (x + offset), (int) (y + offset), (int) (x + ninthWidth - offset),
							(int) (y + ninthHeight - offset));
					g.drawLine((int) (x + offset), (int) (y + ninthHeight - offset), (int) (x + ninthWidth - offset),
							(int) (y + offset));
				} else if (field[col][row] == 2) { // draw o
					g.drawOval((int) (col * ninthWidth + offset), (int) (row * ninthHeight + offset),
							(int) (ninthWidth - 2 * offset), (int) (ninthHeight - 2 * offset));
				}
			}
		}
	}

	public Color getMicroFieldColor(int playerId, int macroValue) {
		if (macroValue == Field.MACRO_PLAYABLE) {
			if ((game.isGameOver() && selectedMove == null) || (selectedMove != null && selectedMove == lastMove)) {
				// First case is for when game just ended. We need to show
				// playable fields as open, not the other player's color.
				// Second case is for when game has ended and we are checking
				// the last move made in the list.
				// game is over show as open
				return Color.ORANGE;
			}
			return Color.GREEN;
		} else if (macroValue == Field.MACRO_UNPLAYABLE) {
			return Color.ORANGE; // tie or not playable
		} else { // return winner's color
			return getPlayerColor(macroValue);
		}
	}

	public Color getPlayerColor(int playerId) {
		return playerId == 1 ? Color.RED : Color.BLUE;
	}

	public void changeState(ProcessorMove move) {
		selectedMove = move;
	}

	/**
	 * Gets the last move while the game is currently running
	 * 
	 * @return
	 */
	private ProcessorMove getLastMove() {
		List<ProcessorMove> moves = game.getMoves();
		if (moves.size() == 0) return null;
		return moves.get(moves.size() - 1);
	}

	/**
	 * Sets the last move after the game is finished
	 * 
	 * @param lastMove
	 */
	public void setLastMove(ProcessorMove lastMove) {
		this.lastMove = lastMove;
	}
}
