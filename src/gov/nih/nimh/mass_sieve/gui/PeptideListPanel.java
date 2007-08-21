/*
 * PeptideListPanel.java
 *
 * Created on May 13, 2007, 12:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.gui;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import gov.nih.nimh.mass_sieve.Peptide;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author slotta
 */
public class PeptideListPanel extends ListPanel {
    
    /** Creates a new instance of PeptideListPanel */
    public PeptideListPanel(ExperimentPanel ePanel) {
        super(ePanel);
    }
    
    public void addPeptideList(ArrayList<Peptide> list, HashSet<String> exp) {
        pTableFormat = new PeptideTableFormat(exp, evList);
        this.addList(list);
    }
}
