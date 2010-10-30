package jl2p.ui;
import jpcap.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CaptureDialog extends JDialog implements ActionListener
{
	static JpcapCaptor jpcap=null;
	
	NetworkInterface[] devices;
	
	JComboBox adapterComboBox;
	//JTextField filterField,caplenField;
	//JRadioButton wholeCheck,headCheck,userCheck;
	JCheckBox promiscCheck;
	
	public CaptureDialog(JFrame parent){
		super(parent,"Escolha a Interface",true);
		
		devices=JpcapCaptor.getDeviceList();
		if(devices==null){
			JOptionPane.showMessageDialog(parent,"Nenhuma Interface Encontrada.");
			dispose();
			return;
		}else{
			String[] names=new String[devices.length];
			for(int i=0;i<names.length;i++)
				names[i]=(devices[i].description==null?devices[i].name:devices[i].description);
			adapterComboBox=new JComboBox(names);
		}
		JPanel adapterPane=new JPanel();
		adapterPane.add(adapterComboBox);
		adapterPane.setBorder(BorderFactory.createTitledBorder("Escolha a Interface."));
		adapterPane.setAlignmentX(Component.LEFT_ALIGNMENT);

		promiscCheck=new JCheckBox("Modo Promíscuo");
		promiscCheck.setSelected(true);
		promiscCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		/*
		filterField=new JTextField(20);
		//filterField.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
		JPanel filterPane=new JPanel();
		filterPane.add(new JLabel("Filter"));
		filterPane.add(filterField);
		filterPane.setBorder(BorderFactory.createTitledBorder("Capture filter"));
		filterPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		*/
		
		/*
		JPanel caplenPane=new JPanel();
		caplenPane.setLayout(new BoxLayout(caplenPane,BoxLayout.Y_AXIS));
		caplenField=new JTextField("1514");
		caplenField.setEnabled(false);
		caplenField.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
		wholeCheck=new JRadioButton("Whole packet");
		wholeCheck.setSelected(true);
		wholeCheck.setActionCommand("Whole");
		wholeCheck.addActionListener(this);
		headCheck=new JRadioButton("Header only");
		headCheck.setActionCommand("Head");
		headCheck.addActionListener(this);
		userCheck=new JRadioButton("Other");
		userCheck.setActionCommand("Other");
		userCheck.addActionListener(this);
		ButtonGroup group=new ButtonGroup();
		group.add(wholeCheck);
		group.add(headCheck);
		group.add(userCheck);
		caplenPane.add(caplenField);
		caplenPane.add(wholeCheck);
		caplenPane.add(headCheck);
		caplenPane.add(userCheck);
		caplenPane.setBorder(BorderFactory.createTitledBorder("Max capture length"));
		caplenPane.setAlignmentX(Component.RIGHT_ALIGNMENT);
		*/
		
		
		JPanel buttonPane=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton okButton=new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		JButton cancelButton=new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		buttonPane.add(okButton);
		buttonPane.add(cancelButton);
		buttonPane.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		
		JPanel westPane=new JPanel(),eastPane=new JPanel();
		westPane.setLayout(new BoxLayout(westPane,BoxLayout.Y_AXIS));
		westPane.add(Box.createRigidArea(new Dimension(5,5)));
		westPane.add(adapterPane);
		
		/*
		westPane.add(Box.createRigidArea(new Dimension(0,10)));
		westPane.add(promiscCheck);
		westPane.add(Box.createRigidArea(new Dimension(0,10)));
		westPane.add(filterPane);
		westPane.add(Box.createVerticalGlue());
		eastPane.add(Box.createRigidArea(new Dimension(5,5)));
		eastPane.setLayout(new BoxLayout(eastPane,BoxLayout.Y_AXIS));
		eastPane.add(caplenPane);
		eastPane.add(Box.createRigidArea(new Dimension(5,30)));
		*/
		eastPane.add(buttonPane);
		eastPane.add(Box.createRigidArea(new Dimension(5,5)));
		
		
		getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
		getContentPane().add(Box.createRigidArea(new Dimension(10,10)));
		getContentPane().add(westPane);
		getContentPane().add(Box.createRigidArea(new Dimension(10,10)));
		getContentPane().add(eastPane);
		getContentPane().add(Box.createRigidArea(new Dimension(10,10)));
		
		
		pack();
		setLocation(parent.getLocation().x+100,parent.getLocation().y+100);
	}
	
	public void actionPerformed(ActionEvent evt){
		String cmd=evt.getActionCommand();
		
		if(cmd.equals("OK")){
			try{
				int caplen=1514; //TAMANHO DO PACOTE A SER CAPTURADO!
				jpcap=JpcapCaptor.openDevice(devices[adapterComboBox.getSelectedIndex()],caplen,
						promiscCheck.isSelected(),50);
			}catch(java.io.IOException e){
				JOptionPane.showMessageDialog(null,e.toString());
				jpcap=null;
			}finally{
				dispose();
			}
		}else if(cmd.equals("Cancel")){
			dispose();
		}
	}
	
	public static JpcapCaptor getJpcap(JFrame parent){
		new CaptureDialog(parent).setVisible(true);
		return jpcap;
	}
}
