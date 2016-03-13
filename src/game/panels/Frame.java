package game.panels;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import engine.game.Controls;
import engine.game.Game;
import game.game.GameEditor;

public class Frame extends JFrame {

	private static final long serialVersionUID = -5045644400326830672L;

	public Canvas openGLCanvas;
	private EmptyDetailsPanel<? extends Object> detailsPanel;

	public Frame(int width, int height) {
		super("Editor");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize((int) (width * 1.25), height);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());

		openGLCanvas = new Canvas();
		openGLCanvas.setPreferredSize(new Dimension(width, height));
		add(openGLCanvas, BorderLayout.CENTER);

		EmptyDetailsPanel.setWidth(getWidth() - width);
		detailsPanel = new EmptyDetailsPanel<Object>(null);
		detailsPanel.setPreferredSize(new Dimension(getWidth() - width, getHeight()));
		add(detailsPanel, BorderLayout.LINE_END);

		// Menu bar
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);

		JMenuItem itemSave = new JMenuItem("Save");
		itemSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: save
			}
		});
		menuFile.add(itemSave);
		JMenuItem itemLoad = new JMenuItem("Load");
		itemLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: load
			}
		});
		menuFile.add(itemLoad);

		Controls.MouseListener mouseListener = new Controls.MouseListener() {
			@Override
			public void onMouseRelease(int button) {
				remove(detailsPanel);
				detailsPanel = DetailsPanelSelector.select(GameEditor.getInstance().controller.selectedObject);
				add(detailsPanel, BorderLayout.LINE_END);
				pack();
			}

			@Override
			public void onMousePress(int button) {

			}

			@Override
			public void onMouseMoved(int amount_x, int amount_y) {

			}
		};
		Controls.addMouseListener(mouseListener);

		pack();
		setVisible(true);
	}

	@Override
	public void dispose() {
		super.dispose();
		Game.end();
	}

}
