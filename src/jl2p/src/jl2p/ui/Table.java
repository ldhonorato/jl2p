package jl2p.ui;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
import java.awt.*;

import jl2p.Captor;
import jl2p.analyzer.Analyzer;
import jpcap.packet.*;


class Table extends JComponent
{
	JDTableModel model;
	TableSorter sorter;
	Vector views=new Vector();
	Captor captor;
	
	Table(TablePane parent,Captor captor){
		this.captor=captor;
		model=new JDTableModel();
		sorter = new TableSorter(model);
		//JTable table=new JTable(model);
		JTable table = new JTable(sorter);
		sorter.addMouseListenerToHeaderInTable(table); //ADDED THIS
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(parent);
		table.setDefaultRenderer(Object.class,new TableRenderer());
		JScrollPane tableView=new JScrollPane(table);
		
		setLayout(new BorderLayout());
		add(tableView,BorderLayout.CENTER);
	}
	
	/*void setPackets(Vector packets){
		if(packets==null) return;
		this.packets=packets;
		model.fireTableStructureChanged();
		model.fireTableDataChanged();
	}*/
	
	void fireTableChanged(){
		/*model.fireTableStructureChanged();
		model.fireTableDataChanged();*/
		model.fireTableRowsInserted(captor.getPackets().size()-1,captor.getPackets().size()-1);
	}
	
	void clear(){
		model.fireTableStructureChanged();
		model.fireTableDataChanged();
	}
	
	void setTableView(Analyzer analyzer,String name,boolean set){
		if(set){
			views.addElement(new TableView(analyzer,name));
		}else{
			for(int i=0;i<views.size();i++){
				TableView view=(TableView)views.elementAt(i);
				if(view.analyzer==analyzer && view.valueName.equals(name))
					views.removeElement(view);
			}
		}
		model.fireTableStructureChanged();
	}
	
	String[] getTableViewStatus(){
		String[] status=new String[views.size()];
		
		for(int i=0;i<status.length;i++){
			TableView view=(TableView)views.elementAt(i);
			status[i]=view.analyzer.getProtocolName()+":"+view.valueName;
		}
		
		return status;
	}
	
	class TableView{
		Analyzer analyzer;
		String valueName;
		
		TableView(Analyzer analyzer,String name){
			this.analyzer=analyzer;valueName=name;
		}
	}
	
	class JDTableModel extends AbstractTableModel
	{
		public int getRowCount(){
			return captor.getPackets().size();
		}
		
		public int getColumnCount(){
			return views.size()+1;
		}
		
		public Object getValueAt(int row,int column){
			if(captor.getPackets().size()<=row) return "";
			Packet packet=(Packet)(captor.getPackets().get(row));
			
			if(column==0)	return new Integer(row);
			TableView view=(TableView)views.elementAt(column-1);
			
			if(view.analyzer.isAnalyzable(packet)){
				synchronized(view.analyzer){
					view.analyzer.analyze(packet);
					Object obj=view.analyzer.getValue(view.valueName);
					
					if(obj instanceof Vector)
						if(((Vector)obj).size()>0)
							return ((Vector)obj).elementAt(0);
						else
							return null;
					else
						return obj;
				}
			}else{
				return null;
			}
		}
		
    /*public Class getColumnClass(int c) {
			for(int i=0;i<getRowCount();i++){
				if(getValueAt(i,c)!=null && !"Not available".equals(getValueAt(i,c)))
					return getValueAt(i, c).getClass();
			}
			
			return String.class;
    }*/
		
		public boolean isCellEditable(int row,int column){
			return false;
		}
		
		public String getColumnName(int column){
			if(column==0) return "No.";
			
			return ((TableView)views.elementAt(column-1)).valueName;
		}
	}
}
