/*
 * ListPanel.java
 *
 * Created on August 9, 2006, 5:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.gui;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.publicobject.misc.swing.JSeparatorTable;
import gov.nih.nimh.mass_sieve.*;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author slotta
 */
public class ListPanel {
    private String name;
    protected EventList evList;
    private JTable jTable;
    protected JSeparatorTable jSepTable;
    protected TableFormat pTableFormat;
    protected SortedList sortList;
    protected EventTableModel tableModel;
    protected EventSelectionModel selectionModel;
    private ExperimentPanel expPanel;
    protected boolean useClusters;
    protected boolean hasProteins;
    
    /** Creates a new instance of ListPanel */
    public ListPanel() {
        this(null);
    }
    
    public ListPanel(ExperimentPanel ePanel) {
        expPanel = ePanel;
        evList = new BasicEventList();
        hasProteins = false;
    }
    
    public void addList(Collection<?> list) {
        evList.addAll(list);
        sortList = new SortedList(evList, null);
        tableModel = new EventTableModel(sortList, pTableFormat);
        selectionModel = new EventSelectionModel(sortList);
    }
        
    public JScrollPane createTable() {
        if (jSepTable == null) {
            jTable = new JTable(tableModel);
        } else {
            jTable = jSepTable;
        }
        if (selectionModel != null) {
            selectionModel.setSelectionMode(ListSelection.SINGLE_SELECTION);
            selectionModel.addListSelectionListener(new RowSelectionListener());
            jTable.setSelectionModel(selectionModel);
        }
        //jTable.setCellSelectionEnabled(true);
        
        if (expPanel == null) {
            TableComparatorChooser tSorter = new TableComparatorChooser(jTable, sortList, true);
        } else {
            if (expPanel.getMassSieveFrame().getUseMultiColumnSort()) {
                TableComparatorChooser tSorter = new TableComparatorChooser(jTable, sortList, true);
            } else {
                TableComparatorChooser tSorter = new TableComparatorChooser(jTable, sortList, false);
            }
        }
        jTable.setAutoResizeMode(jTable.AUTO_RESIZE_OFF);
        initColumnSizes();
        
        final JPopupMenu menu = new JPopupMenu();
        
        // Create and add a menu item
        JMenuItem exportItem = new JMenuItem("Export Table");
        exportItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Export to...");
                int returnVal = fc.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    tableToCSV(f, false);
                }
            }
        });
        menu.add(exportItem);
        if (hasProteins) {
            // Create and add a menu item
            JMenuItem exportPepItem = new JMenuItem("Export Table with Peptides");
            exportPepItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    JFileChooser fc = new JFileChooser();
                    fc.setDialogTitle("Export to...");
                    int returnVal = fc.showSaveDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File f = fc.getSelectedFile();
                        tableToCSV(f, true);
                    }
                }
            });
            menu.add(exportPepItem);
        }
        JScrollPane jsPane = new JScrollPane(jTable);
        // Set the component to show the popup menu
        jTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    menu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
            public void mouseReleased(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    menu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });
        return jsPane;
    }
    
    public void setName(String s) {
        name = s;
    }
    public String getName() {
        return name;
    }
    public String toString() {
        return name;
    }
    
    private void initColumnSizes() {
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        TableCellRenderer headerRenderer = jTable.getTableHeader().getDefaultRenderer();
        int colCount = tableModel.getColumnCount();
        for (int i = 0; i < colCount; i++) {
            column = jTable.getColumnModel().getColumn(i);
            comp = headerRenderer.getTableCellRendererComponent(
                    null, column.getHeaderValue(),
                    false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width+25;
            column.setPreferredWidth(headerWidth);
        }
    }
    
    public void tableToCSV(File file, boolean addPeptides) {
        try {
            FileWriter fw = new FileWriter(file);
            //	Output column headers if any.
            if (jTable.getColumnCount() > 0) {
                String columnHeader;
                
                for ( int column = 0; column < jTable.getColumnCount(); column++ ) {
                    if ( column > 0 ) fw.write(",");
                    columnHeader = jTable.getColumnName(column).trim();
                    if ( columnHeader.indexOf(",") >= 0 ) {
                        fw.write("\"");
                        fw.write(columnHeader);
                        fw.write("\"" );
                    } else {
                        fw.write(columnHeader);
                    }
                }
                
                fw.write("\n");
            }
            for (int row=0 ; row < jTable.getRowCount(); row++) {
                Object obj = tableModel.getElementAt(row);
                if (obj instanceof SeparatorList.Separator) {
                    SeparatorList.Separator<Protein> separator = (SeparatorList.Separator<Protein>)obj;
                    if (useClusters) {
                        fw.write("\nCluster " + separator.first().getCluster() + " (" + separator.size() + " proteins)\n");
                    } else {
                        fw.write("\nPutative Protein " + separator.first().getEquivalentGroup() + " (" + separator.size() + " candidate proteins)\n");
                    }
                } else {
                    for ( int col=0; col<jTable.getColumnCount(); col++ ) {
                        if (col > 0) fw.write(",");
                        //Object elem = pTableFormat.getColumnValue(obj,col);
                        Object elem = jTable.getValueAt(row,col);
                        if (elem != null) {
                            String str = elem.toString();
                            if (str.contains(",")) {
                                fw.write("\"" + str + "\"");
                            } else {
                                fw.write(str);
                            }
                        }
                    }
                    fw.write("\n");
                    
                    if ((obj instanceof Protein) && addPeptides) {
                        Protein pro = (Protein)obj;
                        fw.write(",Sequence,Peptide Hits,Length,Num Proteins,Theoretical Mass,Type,Found by\n");
                        for (Peptide pep:pro.getAllPeptides()) {
                            fw.write("," + pep.toCSVString() + "\n");
                        }
                    }
                }
            }
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return;
    }
    
//    public static final Icon EXPANDED_ICON = Icons.triangle(9, SwingConstants.EAST, Color.DARK_GRAY);
//    public static final Icon COLLAPSED_ICON = Icons.triangle(9, SwingConstants.SOUTH, Color.DARK_GRAY);
//    public static final Border EMPTY_TWO_PIXEL_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);
//    /**
//     * Render the issues separator.
//     */
//    public class ProteinSeparatorTableCell extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
//        
//        private final MessageFormat clusterNameFormat = new MessageFormat("Cluster {0} ({1} proteins)");
//        private final MessageFormat equivalentNameFormat = new MessageFormat("Putative Protein {0} ({1} candidate proteins)");
//        
//        /** the separator list to lock */
//        private final SeparatorList separatorList;
//        
//        private final JPanel panel = new JPanel(new BorderLayout());
//        private final JButton expandButton;
//        private final JLabel nameLabel = new JLabel();
//        
//        private SeparatorList.Separator<Protein> separator;
//        
//        public ProteinSeparatorTableCell(SeparatorList separatorList) {
//            this.separatorList = separatorList;
//            
//            this.expandButton = new JButton(EXPANDED_ICON);
//            this.expandButton.setOpaque(false);
//            this.expandButton.setBorder(EMPTY_TWO_PIXEL_BORDER);
//            this.expandButton.setIcon(EXPANDED_ICON);
//            this.expandButton.setContentAreaFilled(false);
//            
//            this.nameLabel.setFont(nameLabel.getFont().deriveFont(10.0f));
//            this.nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
//            
//            this.expandButton.addActionListener(this);
//            
//            this.panel.setBackground(Color.CYAN);
//            this.panel.add(expandButton, BorderLayout.WEST);
//            this.panel.add(nameLabel, BorderLayout.CENTER);
//        }
//        
//        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
//            configure(value);
//            return panel;
//        }
//        
//        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//            configure(value);
//            return panel;
//        }
//        
//        public Object getCellEditorValue() {
//            return this.separator;
//        }
//        
//        private void configure(Object value) {
//            this.separator = (SeparatorList.Separator<Protein>)value;
//            Protein pro = separator.first();
//            if(pro == null) return; // handle 'late' rendering calls after this separator is invalid
//            expandButton.setIcon(separator.getLimit() == 0 ? EXPANDED_ICON : COLLAPSED_ICON);
//            if (useClusters) {
//                nameLabel.setText(clusterNameFormat.format(new Object[] {pro.getCluster(), new Integer(separator.size())}));
//            } else {
//                nameLabel.setText(equivalentNameFormat.format(new Object[] {pro.getEquivalentGroup(), new Integer(separator.size())}));
//            }
//        }
//        
//        public void actionPerformed(ActionEvent e) {
//            separatorList.getReadWriteLock().writeLock().lock();
//            boolean collapsed;
//            try {
//                collapsed = separator.getLimit() == 0;
//                separator.setLimit(collapsed ? Integer.MAX_VALUE : 0);
//            } finally {
//                separatorList.getReadWriteLock().writeLock().unlock();
//            }
//            expandButton.setIcon(collapsed ? COLLAPSED_ICON : EXPANDED_ICON);
//        }
//    }
    
    class RowSelectionListener implements ListSelectionListener {
        Object prevObject;
        public void valueChanged(ListSelectionEvent e) {
            if(selectionModel.getSelected().size() > 0) {
                Object selectedObject = selectionModel.getSelected().get(0);
                if (!(prevObject == selectedObject)) {
                    if (selectedObject instanceof SeparatorList.Separator) {
                        prevObject = selectedObject;
                        SeparatorList.Separator<Protein> separator = (SeparatorList.Separator<Protein>)selectedObject;
                        if (useClusters) {
                            expPanel.showClusterLower(separator.first().getCluster());
                        } else {
                            HashSet<Protein> proGroup = new HashSet<Protein>();
                            proGroup.addAll(separator.getGroup());
                            for (Protein p:separator.getGroup()) {
                                proGroup.addAll(p.getAssociatedProteinSet());
                            }
                            expPanel.showProteinsLower(proGroup);
                        }
                    }
                    if (selectedObject instanceof Protein) {
                        prevObject = selectedObject;
                        Protein pro = (Protein)selectedObject;
                        //System.out.println(pro.getName());
                        expPanel.showProtein(pro);
                    }
                    if (selectedObject instanceof Peptide) {
                        prevObject = selectedObject;
                        Peptide pep = (Peptide)selectedObject;
                        //System.out.println(pro.getName());
                        expPanel.showPeptide(pep);
                        prevObject = selectedObject;
                    }
                }
            }
        }
    }
    
}
