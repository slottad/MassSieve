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
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.publicobject.misc.swing.JSeparatorTable;
import gov.nih.nimh.mass_sieve.*;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
    protected JTable jTable;
    protected JSeparatorTable jSepTable;
    protected TableFormat pTableFormat;
    protected SortedList sortList;
    protected DefaultEventTableModel tableModel;
    protected DefaultEventSelectionModel selectionModel;
    private ExperimentPanel expPanel;
    protected boolean useClusters;
    
    /** Creates a new instance of ListPanel */
    public ListPanel() {
        this(null);
    }
    
    public ListPanel(ExperimentPanel ePanel) {
        expPanel = ePanel;
        evList = new BasicEventList();
    }
    
    public void addList(Collection<?> list) {
        evList.addAll(list);
        sortList = new SortedList(evList, null);
        tableModel = new DefaultEventTableModel(sortList, pTableFormat);
        selectionModel = new DefaultEventSelectionModel(sortList);
    }
    
    public void addProteinList(HashMap<String, ExperimentPanel> expSet) {
        HashSet<String> uniqueProteins = new HashSet<String>();
        for (ExperimentPanel panel: expSet.values()) {
            uniqueProteins.addAll(panel.getProteins().keySet());
        }
        ArrayList<String> list = new ArrayList<String>(uniqueProteins);
        Collections.sort(list);
        pTableFormat = new DiffTableFormat(expSet);
        this.addList(list);
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
            TableComparatorChooser.install(jTable, sortList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE);
        } else {
            if (expPanel.getMassSieveFrame().getUseMultiColumnSort()) {
                TableComparatorChooser.install(jTable, sortList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE);
            } else {
                TableComparatorChooser.install(jTable, sortList, TableComparatorChooser.SINGLE_COLUMN);
            }
        }
        jTable.setAutoResizeMode(jTable.AUTO_RESIZE_OFF);
        initColumnSizes();
        
        final JPopupMenu menu = createPopupMenu();
        
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
    
    protected JPopupMenu createPopupMenu() {
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
                    tableToCSV(f);
                }
            }
        });
        menu.add(exportItem);
        return menu;
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
    protected void printColumnHeader(FileWriter fw) throws IOException {
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
        
    }
    
    protected void printRow(FileWriter fw, int row) throws IOException {
        for ( int col=0; col<jTable.getColumnCount(); col++ ) {
            if (col > 0) fw.write(",");
            Object elem = jTable.getValueAt(row, col);
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
    }
    
    public void tableToCSV(File file) {
        try {
            FileWriter fw = new FileWriter(file);
            //	Output column headers if any.
            printColumnHeader(fw);
            
            for (int row=0 ; row < jTable.getRowCount(); row++) {
                Object obj = tableModel.getElementAt(row);
                if (obj instanceof SeparatorList.Separator) {
                    fw.write("---------Protein Group---------\n");
                } else {
                    printRow(fw, row);
                }
            }
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return;
    }
    
    class RowSelectionListener implements ListSelectionListener {
        Object prevObject;
        public void valueChanged(ListSelectionEvent e) {
            if(selectionModel.getSelected().size() > 0) {
                Object selectedObject = selectionModel.getSelected().get(0);
                if (!(prevObject == selectedObject)) {
                    prevObject = selectedObject;
                    if (selectedObject instanceof SeparatorList.Separator) {
                        SeparatorList.Separator<Protein> separator = (SeparatorList.Separator<Protein>)selectedObject;
                        if (useClusters) {
                            //expPanel.showClusterLower(separator.first().getCluster());
                            expPanel.showCluster(separator.first().getCluster());
                        } else {
                            HashSet<Protein> proGroup = new HashSet<Protein>();
                            proGroup.addAll(separator.getGroup());
                            for (Protein p:separator.getGroup()) {
                                proGroup.addAll(p.getAssociatedProteinSet());
                            }
                            //expPanel.showProteinsLower(proGroup);
                        }
                    }
                    if (selectedObject instanceof Protein) {
                        Protein pro = (Protein)selectedObject;
                        expPanel.showProtein(pro, false);
                        expPanel.showCluster(pro.getCluster());
                    }
                    if (selectedObject instanceof Peptide) {
                        Peptide pep = (Peptide)selectedObject;
                        expPanel.showPeptide(pep, false);
                        expPanel.showCluster(pep.getCluster());
                    }
                    if (selectedObject instanceof PeptideHit) {
                        PeptideHit pepHit = (PeptideHit)selectedObject;
                        expPanel.showPeptideHit(pepHit);
                    }
                }
            }
        }
    }
    
}
