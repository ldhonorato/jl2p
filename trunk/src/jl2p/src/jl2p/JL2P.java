package jl2p;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import jl2p.ui.Frame;
import jpcap.NetworkInterface;

public class JL2P
{
	public static Preferences preferences;
	
	public static javax.swing.JFileChooser chooser;

	private static ArrayList<Frame> frames=new ArrayList<Frame>();

	public static void main(String[] args) throws Exception{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		chooser=new javax.swing.JFileChooser();
		try{
			Class.forName("jpcap.JpcapCaptor");
			NetworkInterface[] devices=jpcap.JpcapCaptor.getDeviceList();
			if(devices.length==0){
				JOptionPane.showMessageDialog(null,"Nenhuma interface foi encontrada.",
						"Warning",JOptionPane.WARNING_MESSAGE);
			}
		}catch(ClassNotFoundException e){
			JOptionPane.showMessageDialog(null,"Não foram encontradas as bibliotecas do Jpcap ou do WinPcap no sistema.",
					"Error",JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}catch(UnsatisfiedLinkError e){
			JOptionPane.showMessageDialog(null,"Não foram encontradas as bibliotecas do Jpcap ou do WinPcap no sistema.",
					"Error",JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		
		preferences=Preferences.userNodeForPackage(JL2P.class);
		
		AnalyzerLoader.loadDefaultAnalyzer();
		StatisticsLoader.loadDefaultStatistics();
		
		openNewWindow();
	}
	
	public static void saveProperty(){
		try{
			preferences.flush();
		} catch (BackingStoreException e) {
			JOptionPane.showMessageDialog(null,"Não foi possível salvar as preferências.",
					"Error",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static void openNewWindow(){
		Captor captor=new Captor();
		frames.add(Frame.openNewWindow(captor));
	}
	
	public static void closeWindow(Frame frame){
		frame.captor.stopCapture();
//		frame.captor.saveIfNot();
		frame.captor.closeAllWindows();
		frames.remove(frame);
		frame.dispose();
		if(frames.isEmpty()){
			saveProperty();
			System.exit(0);
		}
	}
	
	protected void finalize() throws Throwable{
		saveProperty();
	}
}
