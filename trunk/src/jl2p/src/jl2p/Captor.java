package jl2p;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import jl2p.statistics.Statistics;
import jl2p.ui.CaptureDialog;
import jl2p.ui.ContinuousStatFrame;
import jl2p.ui.Frame;
import jl2p.ui.StatisticsFrame;
import jpcap.JpcapCaptor;
import jpcap.JpcapWriter;
import jpcap.PacketReceiver;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;

public class Captor {
	long MAX_PACKETS_HOLD = 10000;

	List<Packet> packets = new ArrayList<Packet>();

	JpcapCaptor jpcap = null;

	boolean isLiveCapture;
	boolean isSaved = false;

	Frame frame;

	public void setFrame(Frame frame) {
		this.frame = frame;
	}

	public List<Packet> getPackets() {
		return packets;
	}

	public void capturePacketsFromDevice() {
		if (jpcap != null)
			jpcap.close();
		jpcap = CaptureDialog.getJpcap(frame);
		clear();

		if (jpcap != null) {
			isLiveCapture = true;
			frame.disableCapture();

			startCaptureThread();
		}
	}

	public void loadPacketsFromFile() {
		isLiveCapture = false;
		clear();

		int ret = JL2P.chooser.showOpenDialog(frame);
		if (ret == JFileChooser.APPROVE_OPTION) {
			String path = JL2P.chooser.getSelectedFile().getPath();

			try {
				if (jpcap != null) {
					jpcap.close();
				}
				jpcap = JpcapCaptor.openFile(path);
			} catch (java.io.IOException e) {
				JOptionPane.showMessageDialog(frame, "Não foi possível abrir o arquivo: " + path);
				e.printStackTrace();
				return;
			}

			frame.disableCapture();

			startCaptureThread();
		}
	}

	private void clear() {
		packets.clear();
		frame.clear();

		for (int i = 0; i < sframes.size(); i++)
			((StatisticsFrame) sframes.get(i)).clear();
	}

	public void saveToFile() {
		if (packets == null)
			return;

		int ret = JL2P.chooser.showSaveDialog(frame);
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = JL2P.chooser.getSelectedFile();

			if (file.exists()) {
				if (JOptionPane.showConfirmDialog(frame, "Substituir " + file.getName() + "?", "Substituir?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
					return;
				}
			}

			try {
				JpcapWriter writer = JpcapWriter.openDumpFile(jpcap, file.getPath());

				for (Packet p : packets) {
					writer.writePacket(p);
				}

				writer.close();
				isSaved = true;

			} catch (java.io.IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "Não foi possível salvar o arquivo: " + file.getPath());
			}
		}
	}

	public void stopCapture() {
		stopCaptureThread();
	}

	public void saveIfNot() {
		if (isLiveCapture && !isSaved) {
			int ret = JOptionPane.showConfirmDialog(null, "Deseja salvar os dados?", "Deseja salvar os dados?", JOptionPane.YES_NO_OPTION);
			if (ret == JOptionPane.YES_OPTION)
				saveToFile();
		}
	}

	List<StatisticsFrame> sframes = new ArrayList<StatisticsFrame>();

	public void addContinuousStatFrame(Statistics taker) {
		sframes.add(ContinuousStatFrame.openWindow(packets, taker.newInstance()));
	}

	public void closeAllWindows() {
		for (int i = 0; i < sframes.size(); i++)
			((StatisticsFrame) sframes.get(i)).dispose();
	}

	private Thread captureThread;

	private void startCaptureThread() {
		if (captureThread != null)
			return;

		captureThread = new Thread(new Runnable() {
			public void run() {
				while (captureThread != null) {
					if (jpcap.processPacket(1, handler) == 0 && !isLiveCapture)
						stopCaptureThread();
					Thread.yield();
				}

				jpcap.breakLoop();
				frame.enableCapture();
			}
		});
		captureThread.setPriority(Thread.MIN_PRIORITY);

		frame.startUpdating();
		for (int i = 0; i < sframes.size(); i++) {
			((StatisticsFrame) sframes.get(i)).startUpdating();
		}

		captureThread.start();
	}

	void stopCaptureThread() {
		captureThread = null;
		frame.stopUpdating();
		for (int i = 0; i < sframes.size(); i++) {
			((StatisticsFrame) sframes.get(i)).stopUpdating();
		}
	}

	private ExecutorService exe = Executors.newFixedThreadPool(10);
	public static final Map<InetAddress, String> hostnameCache = new HashMap<InetAddress, String>();

	private PacketReceiver handler = new PacketReceiver() {
		public void receivePacket(final Packet packet) {
			packets.add(packet);
			while (packets.size() > MAX_PACKETS_HOLD) {
				packets.remove(0);
			}
			if (!sframes.isEmpty()) {
				for (int i = 0; i < sframes.size(); i++)
					((StatisticsFrame) sframes.get(i)).addPacket(packet);
			}
			isSaved = false;

			if (packet instanceof IPPacket) {
				exe.execute(new Runnable() {
					public void run() {
						IPPacket ip = (IPPacket) packet;
						if (!hostnameCache.containsKey(ip.src_ip))
							hostnameCache.put(ip.src_ip, ip.src_ip.getHostName());
						if (!hostnameCache.containsKey(ip.dst_ip))
							hostnameCache.put(ip.dst_ip, ip.dst_ip.getHostName());
//						System.out.println(hostnameCache.size());
					}
				});
			}
		}
	};

}
