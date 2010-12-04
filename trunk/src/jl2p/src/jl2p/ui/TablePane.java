package jl2p.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jl2p.Captor;
import jl2p.JL2P;
import jl2p.AnalyzerLoader;
import jl2p.analyzer.Analyzer;
import jpcap.packet.Packet;

class TablePane extends JPanel implements ActionListener, ListSelectionListener {
	Table table;
	TableTree tree;	
	Captor captor;
	List<Analyzer> analyzers;

	JMenu[] tableViewMenu = new JMenu[4];

	TablePane(Captor captor) {
		this.captor = captor;
		table = new Table(this, captor);
		tree = new TableTree();
		

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setTopComponent(table);
		splitPane2.setTopComponent(tree);
		splitPane.setBottomComponent(splitPane2);
		splitPane.setDividerLocation(200);
		splitPane2.setDividerLocation(200);

		tableViewMenu[0] = new JMenu("Datalink Layer");
		tableViewMenu[1] = new JMenu("Network Layer");
		tableViewMenu[2] = new JMenu("Transport Layer");
		tableViewMenu[3] = new JMenu("Application Layer");
		analyzers = AnalyzerLoader.getAnalyzers();
		JMenuItem item, subitem;

		for (int i = 0; i < analyzers.size(); i++) {
			Analyzer analyzer = analyzers.get(i);
			item = new JMenu(analyzer.getProtocolName());
			String[] valueNames = analyzer.getValueNames();
			if (valueNames == null)
				continue;
			for (int j = 0; j < valueNames.length; j++) {
				subitem = new JCheckBoxMenuItem(valueNames[j]);
				subitem.setActionCommand("TableView" + i);
				subitem.addActionListener(this);
				item.add(subitem);
			}
			tableViewMenu[analyzer.layer].add(item);
		}

		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);

		loadProperty();
		setSize(400, 200);
	}

	void fireTableChanged() {
		table.fireTableChanged();
	}

	void clear() {
		table.clear();
	}

	public void setTableViewMenu(JMenu menu) {
		menu.add(tableViewMenu[0]);
		menu.add(tableViewMenu[1]);
		menu.add(tableViewMenu[2]);
		menu.add(tableViewMenu[3]);
	}

	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();

		if (cmd.startsWith("TableView")) {
			int index = Integer.parseInt(cmd.substring(9));
			JCheckBoxMenuItem item = (JCheckBoxMenuItem) evt.getSource();
			table.setTableView(analyzers.get(index), item.getText(), item.isSelected());
		}
	}

	public void valueChanged(ListSelectionEvent evt) {
		if (evt.getValueIsAdjusting())
			return;

		int index = ((ListSelectionModel) evt.getSource()).getMinSelectionIndex();
		if (index >= 0) {
			Packet p = (Packet) captor.getPackets().get(table.sorter.getOriginalIndex(index));
			tree.analyzePacket(p);			
		}
	}

	void loadProperty() {
		// get all menus
		Component[] menus = new Component[analyzers.size()];
		int k = 0;
		for (int j = 0; j < tableViewMenu[0].getMenuComponents().length; j++)
			menus[k++] = tableViewMenu[0].getMenuComponents()[j];
		for (int j = 0; j < tableViewMenu[1].getMenuComponents().length; j++)
			menus[k++] = tableViewMenu[1].getMenuComponents()[j];
		for (int j = 0; j < tableViewMenu[2].getMenuComponents().length; j++)
			menus[k++] = tableViewMenu[2].getMenuComponents()[j];
		for (int j = 0; j < tableViewMenu[3].getMenuComponents().length; j++)
			menus[k++] = tableViewMenu[3].getMenuComponents()[j];

		// load ptoperty
		StringTokenizer status = new StringTokenizer(JL2P.preferences.get("TableView",
				"Ethernet Frame:Source MAC,Ethernet Frame:Destination MAC,IPv4:Source IP,IPv4:Destination IP"), ",");

		while (status.hasMoreTokens()) {
			StringTokenizer s = new StringTokenizer(status.nextToken(), ":");
			if (s.countTokens() == 2) {
				String name = s.nextToken(), valueName = s.nextToken();

				for (int i = 0; i < menus.length; i++) {
					if (((JMenu) menus[i]).getText() == null || name == null)
						continue;
					if (((JMenu) menus[i]).getText().equals(name)) {
						Component[] vn = ((JMenu) menus[i]).getMenuComponents();

						for (int j = 0; j < vn.length; j++)
							if (valueName.equals(((JCheckBoxMenuItem) vn[j]).getText())) {
								((JCheckBoxMenuItem) vn[j]).setState(true);
								break;
							}
						break;
					}
				}

				for (Analyzer analyzer : analyzers)
					if (analyzer.getProtocolName().equals(name)) {
						table.setTableView(analyzer, valueName, true);
						break;
					}
			}
		}
		
		//TODO Mudar o nome da coluna
		table.setTableView(null, "Source Port", true);
		table.setTableView(null, "Destination Port", true);		
	}

	void saveProperty() {
		String[] viewStatus = table.getTableViewStatus();
		if (viewStatus.length > 0) {
			StringBuffer buf = new StringBuffer(viewStatus[0]);
			for (int i = 1; i < viewStatus.length; i++)
				buf.append("," + viewStatus[i]);

			JL2P.preferences.put("TableView", buf.toString());
		}
	}
}

