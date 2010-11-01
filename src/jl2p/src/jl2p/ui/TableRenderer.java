package jl2p.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

class TableRenderer extends JLabel implements TableCellRenderer {
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	public TableRenderer() {
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		if (isSelected) {
			super.setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			super.setForeground(table.getForeground());
			super.setBackground(table.getBackground());
		}

		setFont(table.getFont());

		if (hasFocus) {
			setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
		} else {
			setBorder(noFocusBorder);
		}

		if (value == null) {
			setText("Not Available");
			return this;
		}

		setText(value.toString());

		if (value.getClass().equals(Integer.class) || value.getClass().equals(Long.class)) {
			setHorizontalAlignment(SwingConstants.RIGHT);
		}

		Color back = getBackground();
		boolean colorMatch = (back != null) && (back.equals(table.getBackground())) && table.isOpaque();
		setOpaque(!colorMatch);

		return this;
	}
}