/*
 * ProteinListPanel.java
 *
 * Created on May 13, 2007, 4:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.gui;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import com.publicobject.misc.swing.Icons;
import com.publicobject.misc.swing.JSeparatorTable;
import gov.nih.nimh.mass_sieve.Protein;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author slotta
 */
public class ProteinListPanel extends ListPanel {
    
    /** Creates a new instance of ProteinListPanel */
    public ProteinListPanel() {
        super();
    }
    
    public ProteinListPanel(ExperimentPanel ePanel) {
        super(ePanel);
    }
    
    public void addProteinList(ArrayList<Protein> list, HashSet<String> exp) {
        hasProteins = true;
        pTableFormat = new ProteinTableFormat(exp);
        this.addList(list);
    }
    
    public void addProteinList(ArrayList<Protein> list, HashSet<String> exp, boolean useClusters) {
        hasProteins = true;
        evList.addAll(list);
        pTableFormat = new ProteinTableFormat(exp);
        sortList = new SortedList(evList, null);
        SeparatorList<Protein> sepList;
        this.useClusters = useClusters;
        if (useClusters) {
            sepList = new SeparatorList<Protein>(sortList, GlazedLists.beanPropertyComparator(Protein.class, "cluster"), 0, Integer.MAX_VALUE);
        } else {
            sepList = new SeparatorList<Protein>(sortList, GlazedLists.beanPropertyComparator(Protein.class, "equivalentGroup"), 0, Integer.MAX_VALUE);
        }
        tableModel = new EventTableModel(sepList, pTableFormat);
        jSepTable = new JSeparatorTable(tableModel);
        jSepTable.setSeparatorRenderer(new ProteinSeparatorTableCell(sepList));
        jSepTable.setSeparatorEditor(new ProteinSeparatorTableCell(sepList));
        selectionModel = new EventSelectionModel(sepList);
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
    
    public static final Icon EXPANDED_ICON = Icons.triangle(9, SwingConstants.EAST, Color.DARK_GRAY);
    public static final Icon COLLAPSED_ICON = Icons.triangle(9, SwingConstants.SOUTH, Color.DARK_GRAY);
    public static final Border EMPTY_TWO_PIXEL_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);
    /**
     * Render the issues separator.
     */
    public class ProteinSeparatorTableCell extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
        
        private final MessageFormat clusterNameFormat = new MessageFormat("Cluster {0} ({1} proteins)");
        private final MessageFormat equivalentNameFormat = new MessageFormat("Putative Protein {0} ({1} candidate proteins)");
        
        /** the separator list to lock */
        private final SeparatorList separatorList;
        
        private final JPanel panel = new JPanel(new BorderLayout());
        private final JButton expandButton;
        private final JLabel nameLabel = new JLabel();
        
        private SeparatorList.Separator<Protein> separator;
        
        public ProteinSeparatorTableCell(SeparatorList separatorList) {
            this.separatorList = separatorList;
            
            this.expandButton = new JButton(EXPANDED_ICON);
            this.expandButton.setOpaque(false);
            this.expandButton.setBorder(EMPTY_TWO_PIXEL_BORDER);
            this.expandButton.setIcon(EXPANDED_ICON);
            this.expandButton.setContentAreaFilled(false);
            
            this.nameLabel.setFont(nameLabel.getFont().deriveFont(10.0f));
            this.nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            
            this.expandButton.addActionListener(this);
            
            this.panel.setBackground(Color.CYAN);
            this.panel.add(expandButton, BorderLayout.WEST);
            this.panel.add(nameLabel, BorderLayout.CENTER);
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            configure(value);
            return panel;
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            configure(value);
            return panel;
        }
        
        public Object getCellEditorValue() {
            return this.separator;
        }
        
        private void configure(Object value) {
            this.separator = (SeparatorList.Separator<Protein>)value;
            Protein pro = separator.first();
            if(pro == null) return; // handle 'late' rendering calls after this separator is invalid
            expandButton.setIcon(separator.getLimit() == 0 ? EXPANDED_ICON : COLLAPSED_ICON);
            if (useClusters) {
                nameLabel.setText(clusterNameFormat.format(new Object[] {pro.getCluster(), new Integer(separator.size())}));
            } else {
                nameLabel.setText(equivalentNameFormat.format(new Object[] {pro.getEquivalentGroup(), new Integer(separator.size())}));
            }
        }
        
        public void actionPerformed(ActionEvent e) {
            separatorList.getReadWriteLock().writeLock().lock();
            boolean collapsed;
            try {
                collapsed = separator.getLimit() == 0;
                separator.setLimit(collapsed ? Integer.MAX_VALUE : 0);
            } finally {
                separatorList.getReadWriteLock().writeLock().unlock();
            }
            expandButton.setIcon(collapsed ? COLLAPSED_ICON : EXPANDED_ICON);
        }
    }
}
