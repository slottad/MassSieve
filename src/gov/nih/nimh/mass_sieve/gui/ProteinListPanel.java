/*
 * ProteinListPanel.java
 *
 * Created on May 13, 2007, 4:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.gui;

import gov.nih.nimh.mass_sieve.Peptide;
import gov.nih.nimh.mass_sieve.Protein;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author slotta
 */
public class ProteinListPanel extends ListPanel {
    protected HashSet<String> experiments;
    
    /** Creates a new instance of ProteinListPanel */
    public ProteinListPanel() {
        super();
    }
    
    public ProteinListPanel(ExperimentPanel ePanel) {
        super(ePanel);
    }
    
    public void addProteinList(ArrayList<Protein> list, HashSet<String> exp) {
        experiments = exp;
        pTableFormat = new ProteinTableFormat(exp, evList, false);
        this.addList(list);
    }
    
    public void tableToCSV(File file, boolean addPeptides) {
        try {
            FileWriter fw = new FileWriter(file);
            //	Output column headers if any.
            printColumnHeader(fw);
            
            for (int row=0 ; row < jTable.getRowCount(); row++) {
                Object obj = tableModel.getElementAt(row);
                printRow(fw, row);
                
                if ((obj instanceof Protein) && addPeptides) {
                    Protein pro = (Protein)obj;
                    writePeptides(fw, pro);
                }
            }
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return;
    }
    
    protected void writePeptides(FileWriter fw, Protein pro) throws IOException {
        fw.write(",Sequence,");
        if (experiments.size() > 1) {
            for (String e:experiments) {
                fw.write(e + " PepHits,");
            }
        } else {
            fw.write("PepHits,");
        }
        fw.write("Length,Num Proteins,Theoretical Mass,Type,Found by,Scans\n");        
        for (Peptide pep:pro.getAllPeptides()) {
            fw.write("," + pep.toCSVString(experiments) + "\n");
        }        
    }
    
    public void tableToTabSimple(File file) {
        try {
            FileWriter fw = new FileWriter(file);
            //	Output column headers if any.
            fw.write("Proteins\tPeptides\tScans\n");
            
            for (int row=0 ; row < jTable.getRowCount(); row++) {
                Object obj = tableModel.getElementAt(row);
                if ((obj instanceof Protein)) {
                    Protein pro = (Protein)obj;
                    fw.write(pro.getName());
                    for (Peptide pep : pro.getAllPeptides()) {
                        fw.write("\t" + pep.getSequence() + "\t" + pep.getScanList(false) + "\n");
                    }        
                }
            }
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return;        
    }

//    protected JPopupMenu createPopupMenu() {
//        final JPopupMenu menu = super.createPopupMenu();
//        // Create and add a menu item
//        JMenuItem exportPepItem = new JMenuItem("Export Table with Peptides");
//        exportPepItem.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                JFileChooser fc = new JFileChooser();
//                fc.setDialogTitle("Export to...");
//                int returnVal = fc.showSaveDialog(null);
//                if (returnVal == JFileChooser.APPROVE_OPTION) {
//                    File f = fc.getSelectedFile();
//                    tableToCSV(f, true);
//                }
//            }
//        });
//        menu.add(exportPepItem);
//        JMenuItem exportSimpleItem = new JMenuItem("Export Simple Protein-Peptide format");
//        exportSimpleItem.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                JFileChooser fc = new JFileChooser();
//                fc.setDialogTitle("Export to...");
//                int returnVal = fc.showSaveDialog(null);
//                if (returnVal == JFileChooser.APPROVE_OPTION) {
//                    File f = fc.getSelectedFile();
//                    tableToTabSimple(f);
//                }
//            }
//        });
//        menu.add(exportSimpleItem);
//        return menu;
//    }
    
    
}
