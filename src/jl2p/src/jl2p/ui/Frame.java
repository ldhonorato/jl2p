package jl2p.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jl2p.Captor;
import jl2p.StatisticsLoader;
import jl2p.JL2P;

public class Frame extends JFrame implements ActionListener {
	public Captor captor;

	JLabel statusLabel;
	JMenuItem openMenu, saveMenu, captureMenu, stopMenu;
	JMenu statMenu;
	JButton openButton, saveButton, captureButton, stopButton;

	public TablePane tablePane;

	private JButton graphic1Button;

	private JButton graphic2Button;

	public static Frame openNewWindow(Captor captor) {
		Frame frame = new Frame(captor);
		frame.setVisible(true);

		return frame;
	}

	public Frame(Captor captor) {
		this.captor = captor;
		tablePane = new TablePane(captor);
		captor.setFrame(this);

		setTitle("JL2P Sniffer - Gerência de Redes 2010.2 - UNIVERSIDADE DE PERNAMBUCO");

		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);

		openButton = new JButton(getImageIcon("/image/open.png"));
		openButton.setActionCommand("Open");
		openButton.addActionListener(this);
		toolbar.add(openButton);

		saveButton = new JButton(getImageIcon("/image/save.png"));
		saveButton.setActionCommand("Save");
		saveButton.addActionListener(this);
		saveButton.setEnabled(false);
		toolbar.add(saveButton);

		toolbar.addSeparator();

		captureButton = new JButton(getImageIcon("/image/capture.png"));
		captureButton.setActionCommand("Start");
		captureButton.addActionListener(this);
		toolbar.add(captureButton);

		stopButton = new JButton(getImageIcon("/image/stopcap.png"));
		stopButton.setActionCommand("Stop");
		stopButton.addActionListener(this);
		stopButton.setEnabled(false);
		toolbar.add(stopButton);

		graphic1Button = new JButton(getImageIcon("/image/graph.png"));
		graphic1Button.setActionCommand("Graphic1");
		graphic1Button.addActionListener(this);
		graphic1Button.setEnabled(false);
		graphic1Button.setToolTipText("");
		toolbar.add(graphic1Button);

		graphic2Button = new JButton(getImageIcon("/image/graphic2.png"));
		graphic2Button.setActionCommand("Graphic2");
		graphic2Button.addActionListener(this);
		graphic2Button.setEnabled(false);
		graphic1Button.setToolTipText("");
		toolbar.add(graphic2Button);

		statusLabel = new JLabel("JL2P iniciado.");

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(statusLabel, BorderLayout.SOUTH);
		getContentPane().add(tablePane, BorderLayout.CENTER);
		getContentPane().add(toolbar, BorderLayout.NORTH);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				saveProperty();
				JL2P.closeWindow((Frame) evt.getSource());
			}
		});

		loadProperty();
	}

	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();

		if (cmd.equals("Open")) {
			captor.loadPacketsFromFile();
		} else if (cmd.equals("Save")) {
			captor.saveToFile();
		} else if (cmd.equals("NewWin")) {
			JL2P.openNewWindow();
		} else if (cmd.equals("Exit")) {
			saveProperty();
			System.exit(0);
		} else if (cmd.equals("Start")) {
			captor.capturePacketsFromDevice();
		} else if (cmd.equals("Stop")) {
			captor.stopCapture();
		} else if (cmd.equals("Graphic1")) {
			captor.addContinuousStatFrame(StatisticsLoader.getStatisticAt(2));
		} else if (cmd.equals("Graphic2")) {
			captor.addContinuousStatFrame(StatisticsLoader.getStatisticAt(3));
		} 
	}

	public void clear() {
		tablePane.clear();
	}

	public void startUpdating() {
		frameUpdater.setRepeats(true);
		frameUpdater.start();
	}

	public void stopUpdating() {
		frameUpdater.stop();
		frameUpdater.setRepeats(false);
		frameUpdater.start();
	}

	javax.swing.Timer frameUpdater = new javax.swing.Timer(500, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			try {
				tablePane.fireTableChanged();
				statusLabel.setText("Capturado(s): " + captor.getPackets().size() + " pacote(s).");
			} catch (Exception e) {
				e.printStackTrace();
			}

			repaint();
		}
	});

	void loadProperty() {
		setSize(Integer.parseInt(JL2P.preferences.get("WinWidth", "640")), Integer.parseInt(JL2P.preferences.get("WinHeight", "480")));
		setLocation(Integer.parseInt(JL2P.preferences.get("WinX", "0")), Integer.parseInt(JL2P.preferences.get("WinY", "0")));
	}

	void saveProperty() {		
		JL2P.preferences.put("WinWidth", String.valueOf(getBounds().width));
		JL2P.preferences.put("WinHeight", String.valueOf(getBounds().height));
		JL2P.preferences.put("WinX", String.valueOf(getBounds().x));
		JL2P.preferences.put("WinY", String.valueOf(getBounds().y));

		tablePane.saveProperty();

		JL2P.saveProperty();
	}

	public void enableCapture() {
		openButton.setEnabled(true);
		saveButton.setEnabled(true);
		captureButton.setEnabled(true);
		stopButton.setEnabled(false);
		graphic1Button.setEnabled(false);
		graphic2Button.setEnabled(false);
	}

	public void disableCapture() {
		openButton.setEnabled(false);
		captureButton.setEnabled(false);
		saveButton.setEnabled(true);
		stopButton.setEnabled(true);
		graphic1Button.setEnabled(true);
		graphic2Button.setEnabled(true);
	}

	private ImageIcon getImageIcon(String path) {
		return new ImageIcon(this.getClass().getResource(path));
	}
}
