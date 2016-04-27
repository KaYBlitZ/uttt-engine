package com.theaigames.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import com.theaigames.uttt.Constants;
import com.theaigames.uttt.UTTT;

public class GUI extends JFrame {
	private UTTT game;
	private BoardPanel boardPanel;
	private MoveList moveList;

	public GUI(UTTT game) {
		this.game = game;
		setTitle("Ultimate Tic-Tac-Toe");
		setSize(Constants.GUI_WIDTH, Constants.GUI_HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // centers screen
		setLayout(new FlowLayout());
		boardPanel = new BoardPanel(game);
		moveList = new MoveList(game.getMoves(), boardPanel);
		add(boardPanel);
		add(moveList);
		new Timer(1000 / Constants.FPS, new ActionListener() {
			// refresh 10 times a second
			@Override
			public void actionPerformed(ActionEvent e) {
				update();
			}
		}).start();
	}

	public void update() {
		repaint();
	}

	public void finishGame() {
		moveList.populate();
		moveList.setEnabled(true);
	}
}
