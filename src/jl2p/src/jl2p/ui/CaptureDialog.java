package jl2p.ui;

import jpcap.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CaptureDialog extends JDialog implements ActionListener {
	static JpcapCaptor jpcap = null;

	NetworkInterface[] devices;

	JComboBox adapterComboBox;

	JCheckBox promiscCheck;

	public CaptureDialog(JFrame parent) {
		super(parent, "Escolha a Interface", true);

		devices = JpcapCaptor.getDeviceList();
		if (devices == null) {
			JOptionPane.showMessageDialog(parent, "Nenhuma interface encontrada.");
			dispose();
			return;
		} else {
			String[] names = new String[devices.length];
			for (int i = 0; i < names.length; i++)
				names[i] = (devices[i].description == null ? devices[i].name : devices[i].description);
			adapterComboBox = new JComboBox(names);
		}
		JPanel adapterPane = new JPanel();
		adapterPane.add(adapterComboBox);
		adapterPane.setBorder(BorderFactory.createTitledBorder("Escolha a Interface."));
		adapterPane.setAlignmentX(Component.LEFT_ALIGNMENT);

		promiscCheck = new JCheckBox("Modo Promíscuo");
		promiscCheck.setSelected(true);
		promiscCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
		adapterPane.add(promiscCheck);

		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		JButton cancelButton = new JButton("Cancelar");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		buttonPane.add(okButton);
		buttonPane.add(cancelButton);
		buttonPane.setAlignmentX(Component.RIGHT_ALIGNMENT);

		JPanel westPane = new JPanel(), eastPane = new JPanel();
		westPane.setLayout(new BoxLayout(westPane, BoxLayout.Y_AXIS));
		westPane.add(Box.createRigidArea(new Dimension(5, 5)));
		westPane.add(adapterPane);

		eastPane.add(buttonPane);
		eastPane.add(Box.createRigidArea(new Dimension(5, 5)));

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(Box.createRigidArea(new Dimension(10, 10)));
		getContentPane().add(westPane);
		getContentPane().add(Box.createRigidArea(new Dimension(10, 10)));
		getContentPane().add(eastPane);
		getContentPane().add(Box.createRigidArea(new Dimension(10, 10)));

		pack();
		setLocation(parent.getLocation().x + 100, parent.getLocation().y + 100);
	}

	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();

		if (cmd.equals("OK")) {
			try {
				int caplen = 1514; // TAMANHO DO PACOTE A SER CAPTURADO!
				jpcap = JpcapCaptor.openDevice(devices[adapterComboBox.getSelectedIndex()], caplen, promiscCheck.isSelected(), 50);
			} catch (java.io.IOException e) {
				JOptionPane.showMessageDialog(null, e.toString());
				jpcap = null;
			} finally {
				dispose();
			}
		} else if (cmd.equals("Cancel")) {
			dispose();
		}
	}

	public static JpcapCaptor getJpcap(JFrame parent) {
		new CaptureDialog(parent).setVisible(true);
		return jpcap;
	}
}
