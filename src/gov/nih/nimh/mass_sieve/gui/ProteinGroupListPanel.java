/*
 * ProteinGroupListPanel.java
 *
 * Created on May 15, 2007, 8:02 AM
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
import gov.nih.nimh.mass_sieve.Peptide;
import gov.nih.nimh.mass_sieve.Protein;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author slotta
 */
public class ProteinGroupListPanel extends ProteinListPanel {

    /** Creates a new instance of ProteinGroupListPanel */
    public ProteinGroupListPanel() {
        super();
    }

    public ProteinGroupListPanel(ExperimentPanel ePanel) {
        super(ePanel);
    }

    public void addProteinList(ArrayList<Protein> list, HashSet<String> exp, boolean useClusters) {
        experiments = exp;
        evList.addAll(list);
        pTableFormat = new ProteinTableFormat(exp, evList, !useClusters);
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

    protected JPopupMenu createPopupMenu() {
        final JPopupMenu menu = super.createPopupMenu();

        if (!useClusters) {
            // Create and add a menu item
            JMenuItem exportPrefItem = new JMenuItem("Export Prefered Proteins");
            exportPrefItem.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    JFileChooser fc = new JFileChooser();
                    fc.setDialogTitle("Export to...");
                    int returnVal = fc.showSaveDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File f = fc.getSelectedFile();
                        preferedToCSV(f, false);
                    }
                }
            });
            menu.add(exportPrefItem);

            JMenuItem exportPrefPepItem = new JMenuItem("Export Prefered Proteins with Peptides");
            exportPrefPepItem.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    JFileChooser fc = new JFileChooser();
                    fc.setDialogTitle("Export to...");
                    int returnVal = fc.showSaveDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File f = fc.getSelectedFile();
                        preferedToCSV(f, true);
                    }
                }
            });
            menu.add(exportPrefPepItem);

            JMenuItem exportSimplePrefPepItem = new JMenuItem("Export Prefered Simple Protein-Peptide format");
            exportSimplePrefPepItem.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    JFileChooser fc = new JFileChooser();
                    fc.setDialogTitle("Export to...");
                    int returnVal = fc.showSaveDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File f = fc.getSelectedFile();
                        preferedToTabSimple(f);
                    }
                }
            });
            menu.add(exportSimplePrefPepItem);
        }
        return menu;
    }

    public void tableToCSV(File file, boolean addPeptides) {
        try {
            FileWriter fw = new FileWriter(file);
            //	Output column headers if any.
            printColumnHeader(fw);
            StringBuffer pepString = new StringBuffer();
            for (int row = 0; row < jTable.getRowCount(); row++) {
                Object obj = tableModel.getElementAt(row);
                if (obj instanceof SeparatorList.Separator) {
                    if (pepString.length() > 0) {
                        fw.write(pepString.toString());
                        pepString = new StringBuffer();
                    }
                    SeparatorList.Separator<Protein> separator = (SeparatorList.Separator<Protein>) obj;
                    if (useClusters) {
                        fw.write("\nCluster " + separator.first().getCluster() + " (" + separator.size() + " proteins)\n");
                    } else {
                        fw.write("\nPutative Protein " + separator.first().getMostEquivalent() + " (" + separator.size() + " candidate proteins)\n");
                    }
                } else {
                    printRow(fw, row);

                    if ((obj instanceof Protein) && addPeptides) {
                        Protein pro = (Protein) obj;
                        writePeptides(fw, pro);
                    }
                }
            }
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return;
    }

    public void preferedToCSV(File file, boolean addPeptides) {
        try {
            FileWriter fw = new FileWriter(file);
            //	Output column headers if any.
            printColumnHeader(fw);
            for (int row = 0; row < jTable.getRowCount(); row++) {
                Object obj = tableModel.getElementAt(row);

                if ((obj instanceof Protein)) {
                    Protein pro = (Protein) obj;
                    if (pro.isMostEquivalent()) {
                        printRow(fw, row);
                        if (addPeptides) {
                            writePeptides(fw, pro);
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

    public void preferedToTabSimple(File file) {
        try {
            FileWriter fw = new FileWriter(file);
            //	Output column headers if any.
            fw.write("Proteins\tPeptides\tScans\n");

            for (int row = 0; row < jTable.getRowCount(); row++) {
                Object obj = tableModel.getElementAt(row);
                if ((obj instanceof Protein)) {
                    Protein pro = (Protein) obj;
                    if (pro.isMostEquivalent()) {
                        fw.write(pro.getName());
                        for (Peptide pep : pro.getAllPeptides()) {
                            fw.write("\t" + pep.getSequence() + "\t" + pep.getScanList(false) + "\n");
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
            this.separator = (SeparatorList.Separator<Protein>) value;
            Protein pro = separator.first();
            if (pro == null) {
                return;
            } // handle 'late' rendering calls after this separator is invalid
            expandButton.setIcon(separator.getLimit() == 0 ? EXPANDED_ICON : COLLAPSED_ICON);
            if (useClusters) {
                nameLabel.setText(clusterNameFormat.format(new Object[]{pro.getCluster(), new Integer(separator.size())}));
            } else {
                //nameLabel.setText(equivalentNameFormat.format(new Object[] {pro.getEquivalentGroup(), new Integer(separator.size())}));
                nameLabel.setText(equivalentNameFormat.format(new Object[]{pro.getMostEquivalent(), new Integer(separator.size())}));
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
