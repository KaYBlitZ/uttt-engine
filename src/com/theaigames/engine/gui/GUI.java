package com.theaigames.engine.gui;

import javax.swing.JFrame;

import com.theaigames.uttt.Constants;
import com.theaigames.uttt.UTTT;

public class GUI extends JFrame {
	private UTTT game;

	public GUI(UTTT game) {
		this.game = game;
		setTitle("Ultimate Tic-Tac-Toe");
		setSize(Constants.WIDTH, Constants.HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // centers screen
		add(new BoardPanel(game));
	}
}
