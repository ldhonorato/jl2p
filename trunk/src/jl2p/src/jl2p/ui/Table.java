package jl2p.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import jl2p.Captor;
import jl2p.analyzer.Analyzer;
import jl2p.analyzer.TCPAnalyzer;
import jl2p.analyzer.UDPAnalyzer;
import jpcap.packet.Packet;

class Table extends JComponent {
	TableModel model;
	TableSorter sorter;
	Vector views = new Vector();
	Captor captor;

	Table(TablePane parent, Captor captor) {
		this.captor = captor;
		model = new TableModel();
		sorter = new TableSorter(model);
		JTable table = new JTable(sorter);
		sorter.addMouseListenerToHeaderInTable(table);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(parent);
		table.setDefaultRenderer(Object.class, new TableRenderer());
		JScrollPane tableView = new JScrollPane(table);

		setLayout(new BorderLayout());
		add(tableView, BorderLayout.CENTER);
	}

	void fireTableChanged() {
		model.fireTableRowsInserted(captor.getPackets().size() - 1, captor.getPackets().size() - 1);
	}

	void clear() {
		model.fireTableStructureChanged();
		model.fireTableDataChanged();
	}

	void setTableView(Analyzer analyzer, String name, boolean set) {
		if (set) {
			views.addElement(new TableView(analyzer, name));
		} else {
			for (int i = 0; i < views.size(); i++) {
				TableView view = (TableView) views.elementAt(i);
				if (view.analyzer == analyzer && view.valueName.equals(name))
					views.removeElement(view);
			}
		}
		model.fireTableStructureChanged();
	}

	String[] getTableViewStatus() {
		String[] status = new String[views.size() - 2];

		for (int i = 0; i < status.length; i++) {
			TableView view = (TableView) views.elementAt(i);
			if (view.analyzer != null) {
				status[i] = view.analyzer.getProtocolName() + ":" + view.valueName;
			}
		}

		return status;
	}

	class TableView {
		Analyzer analyzer;
		String valueName;
		

		TableView(Analyzer analyzer, String name) {
			this.analyzer = analyzer;
			valueName = name;
			
		
		}
	}

	class TableModel extends AbstractTableModel {
		private List<Integer> ports = new ArrayList<Integer>();
		
		public TableModel() {
			ports.add(21);
			ports.add(23);
			ports.add(24);
			ports.add(25);
			ports.add(42);
			ports.add(80);
			ports.add(110);
			ports.add(119);
			ports.add(143);
			ports.add(161);
			ports.add(162);
			ports.add(1025);
			ports.add(1026);
			ports.add(1214);
			ports.add(1240);
			ports.add(1863);
			ports.add(4200);
			ports.add(4422);
			ports.add(4444);
			ports.add(5190);
			ports.add(5555);
			ports.add(6346);
			ports.add(6571);
			ports.add(6588);
			ports.add(7777);
			ports.add(8888);
			ports.add(8000);			
		}
		
		public int getRowCount() {
			return captor.getPackets().size();
		}

		public int getColumnCount() {
			return views.size() + 1;
		}

		public Object getValueAt(int row, int column) {
			if (captor.getPackets().size() <= row)
				return "";
			Packet packet = (Packet) (captor.getPackets().get(row));

			if (column == 0)
				return new Integer(row);
			TableView view = (TableView) views.elementAt(column - 1);

			if (view.analyzer == null) {
				String port = view.valueName;
				Object portValue = null;

				TCPAnalyzer tcpAnalyzer = new TCPAnalyzer();
				if (tcpAnalyzer.isAnalyzable(packet)) {
					synchronized (tcpAnalyzer) {
						tcpAnalyzer.analyze(packet);
						portValue = tcpAnalyzer.getValue(port);
					}
				}

				if (portValue == null) {
					UDPAnalyzer udpAnalyzer = new UDPAnalyzer();
					if (udpAnalyzer.isAnalyzable(packet)) {
						synchronized (udpAnalyzer) {
							udpAnalyzer.analyze(packet);
							portValue = udpAnalyzer.getValue(port);
						}
					}
				}

				if(portValue != null && ports.contains(portValue)){
					return portValue.toString() + "(PORTA SUSPEITA)";
				}				
				
				return portValue;
			} else if (view.analyzer.isAnalyzable(packet)) {
				synchronized (view.analyzer) {
					view.analyzer.analyze(packet);
					Object obj = view.analyzer.getValue(view.valueName);

					if (obj instanceof Vector)
						if (((Vector) obj).size() > 0)
							return ((Vector) obj).elementAt(0);
						else
							return null;
					else
						return obj;
				}
			} else {
				return null;
			}
		}

		public boolean isCellEditable(int row, int column) {
			return false;
		}

		public String getColumnName(int column) {
			if (column == 0)
				return "No.";

			return ((TableView) views.elementAt(column - 1)).valueName;
		}
	}

	private Object getAttributeValue(Analyzer analyzer, String valueName, Packet packet) {
		if (analyzer.isAnalyzable(packet)) {
			synchronized (analyzer) {
				analyzer.analyze(packet);
				Object obj = analyzer.getValue(valueName);

				if (obj instanceof Vector)
					if (((Vector) obj).size() > 0)
						return ((Vector) obj).elementAt(0);
					else
						return obj;
			}
		}
		return null;
	}
}
