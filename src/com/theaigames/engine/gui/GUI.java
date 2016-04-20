package com.theaigames.engine.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import com.theaigames.uttt.Constants;

public class GUI extends JFrame implements WindowListener {
	public GUI() {
		setTitle("Ultimate Tic-Tac-Toe");
		setSize(Constants.WIDTH, Constants.HEIGHT);
		addWindowListener(this);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}
}
