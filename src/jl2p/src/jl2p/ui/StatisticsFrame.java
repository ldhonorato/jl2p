package jl2p.ui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import jpcap.packet.*;
import javax.swing.*;

public abstract class StatisticsFrame extends JFrame
{
	StatisticsFrame(String title){
		super(title);
		frameUpdater.start();
		addWindowListener(new java.awt.event.WindowAdapter(){
			public void windowClosed(java.awt.event.WindowEvent evt){
				setVisible(false);
			}
		});
	}
	abstract void fireUpdate();
	public abstract void addPacket(Packet p);
	public abstract void clear();

	public void startUpdating(){
		frameUpdater.setRepeats(true);
		frameUpdater.start();
	}
	
	public void stopUpdating(){
		frameUpdater.stop();
		frameUpdater.setRepeats(false);
		frameUpdater.start();
	}

	javax.swing.Timer frameUpdater=new javax.swing.Timer(500,new ActionListener(){
		public void actionPerformed(ActionEvent evt){
			fireUpdate();
			repaint();
		}
	});

}
