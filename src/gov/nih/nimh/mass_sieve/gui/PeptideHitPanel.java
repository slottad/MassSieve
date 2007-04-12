/*
 * PeptidePanel.java
 *
 * Created on August 7, 2006, 9:24 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.gui;

import gov.nih.nimh.mass_sieve.*;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

/**
 *
 * @author slotta
 */
public class PeptideHitPanel extends JPanel {
    private Peptide pep;
    private JScrollPane tableScrollPane;
    private ExperimentPanel expPanel;
    
    /** Creates a new instance of PeptidePanel */
    public PeptideHitPanel(Peptide p, ExperimentPanel ePanel) {
        pep = p;
        expPanel = ePanel;
        setLayout(new BorderLayout());
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        //JCheckBox showProteins = new JCheckBox("Show Proteins");
        //showProteins.addItemListener(new java.awt.event.ItemListener() {
        //    public void itemStateChanged(java.awt.event.ItemEvent evt) {
        //        showProteinsStateChanged(evt);
        //    }
        //});
        //showProteins.setSelected(false);
        toolbar.add(new JLabel("Seq: " + pep.getSequence()));
        toolbar.addSeparator();
        toolbar.add(new JLabel("Peptide hit count: " + pep.getNumPeptideHits()));
        toolbar.addSeparator();
        toolbar.add(new JLabel("Proteins: " + pep.getNumProteins()));
        toolbar.addSeparator();
        toolbar.add(new JLabel("Theoretical Mass: " + pep.getTheoreticalMass()));
        toolbar.addSeparator();
        toolbar.add(new JLabel("Cluster: " + pep.getCluster()));
        
        //toolbar.add(Box.createHorizontalGlue());
        //toolbar.add(showProteins);
        add(toolbar, BorderLayout.NORTH);
        tableScrollPane = pep.getJTable(expPanel);
        add(tableScrollPane, BorderLayout.CENTER);
    }
    
    private void showProteinsStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            //tableScrollPane.setViewportView(pep.getJTableProteins(expPanel));
            tableScrollPane.setViewportView(pep.getJTable(expPanel));
        } else {
            tableScrollPane.setViewportView(pep.getJTable(expPanel));
        }
    }
}
