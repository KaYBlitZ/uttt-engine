package com.theaigames.engine.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.theaigames.uttt.Constants;
import com.theaigames.uttt.UTTT;
import com.theaigames.uttt.field.MacroField;

@SuppressWarnings("serial")
public class BoardPanel extends JPanel {

	private UTTT game;

	public BoardPanel(UTTT game) {
		this.game = game;
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		new Timer(1000 / 60, new ActionListener() { // refresh 60 times a second
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		}).start();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		int thirdWidth = getWidth() / 3;
		int thirdHeight = getHeight() / 3;

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
			g.setColor(getPlayerColor(field[i]));

			if (field[i] == 1) { // draw x
				float x = col * ninthWidth;
				float y = row * ninthHeight;
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
			if (game.isGameOver()) {
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
}
