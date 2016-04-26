package com.theaigames.engine.gui;

import java.awt.Dimension;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.theaigames.uttt.Constants;
import com.theaigames.uttt.moves.Move;

@SuppressWarnings("serial")
public class MoveList extends JPanel {

	private List<Move> moves;
	private JList<String> list;
	private BoardPanel boardPanel;

	public MoveList(List<Move> moves, BoardPanel boardPanel) {
		this.moves = moves;
		this.boardPanel = boardPanel;
		list = new JList<String>(new DefaultListModel<String>());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent ev) {
				int selected = ((JList<String>) ev.getSource()).getSelectedIndex();
				boardPanel.changeState(moves.get(selected));
			}
		});
		JScrollPane pane = new JScrollPane(list);
		pane.setPreferredSize(new Dimension(Constants.MOVE_LIST_WIDTH, Constants.MOVE_LIST_HEIGHT));
		setEnabled(false); // enabled when match is over
		add(pane);
	}

	public void populate() {
		DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
		for (int i = 0; i < moves.size(); i++) {
			model.addElement("Move " + i);
		}
		int lastIndex = moves.size() - 1;
		list.setSelectedIndex(lastIndex);
		boardPanel.setLastMove(moves.get(lastIndex));
		boardPanel.changeState(moves.get(lastIndex));
	}
}
