package me.genericskid.gui.component;

import javax.swing.table.AbstractTableModel;

public class MappingTableModel extends AbstractTableModel
{
    private static final long serialVersionUID = 666L;
    private String[] columnNames;
    private String[][] data;
    
    public MappingTableModel() {
        this.columnNames = new String[] { "Original", "New" };
        this.data = new String[][] { new String[0] };
    }
    
    @Override
    public int getColumnCount() {
        return this.columnNames.length;
    }
    
    @Override
    public int getRowCount() {
        return this.data.length;
    }
    
    @Override
    public String getColumnName(final int col) {
        return this.columnNames[col];
    }
    
    @Override
    public String getValueAt(final int row, final int col) {
        return this.data[row][col];
    }
    
    public void setData(final String[][] data) {
        this.data = data;
    }
    
    @Override
    public boolean isCellEditable(final int row, final int col) {
        return false;
    }
    
    public void setValueAt(final String value, final int row, final int col) {
        this.data[row][col] = value;
        this.fireTableCellUpdated(row, col);
    }
}
