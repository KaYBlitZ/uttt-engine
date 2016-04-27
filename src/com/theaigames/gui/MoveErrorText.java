package com.theaigames.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.theaigames.uttt.Constants;

@SuppressWarnings("serial")
public class MoveErrorText extends JPanel {
	
	public static final String NO_ERROR = "No move error";
	private String error;
	private int fontSize;
	
	public MoveErrorText() {
		fontSize = (int) (2/3f * Constants.ERROR_HEIGHT);
		error = NO_ERROR;
		setPreferredSize(new Dimension(Constants.ERROR_WIDTH, Constants.ERROR_HEIGHT));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setFont(new Font(g.getFont().getFontName(), g.getFont().getStyle(), fontSize));
		g.drawString(error, 0, fontSize);
	}
	
	public void setError(String error) {
		this.error = error;
	}
}
