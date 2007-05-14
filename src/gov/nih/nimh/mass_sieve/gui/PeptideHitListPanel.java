/*
 * PeptideHitListPanel.java
 *
 * Created on May 13, 2007, 3:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.gui;

import ca.odell.glazedlists.GlazedLists;
import gov.nih.nimh.mass_sieve.PeptideHit;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author slotta
 */
public class PeptideHitListPanel extends ListPanel {
    
    /** Creates a new instance of PeptideHitListPanel */
    public PeptideHitListPanel(ExperimentPanel ePanel) {
        super(ePanel);
    }
    
    public void addPeptideHitList(HashSet<PeptideHit> list) {
        String[] columnTitles = {"Modified Sequence", "Analysis", "Scan", "Query", "Indet", "Expect", "ION", "Ident", "m/z", "Exp. Mass", '\u0394' + "Mass", "Charge", "Protein", "Start", "End", "Experiment", "Source File", "Raw File"};
        String[] columnFields = {"modSequence", "sourceType", "scanNum", "queryNum", "indeterminate", "expect", "ionScore", "ident", "expMass", "expNeutralMass", "diffMass", "Z", "proteinName", "start", "end", "experiment", "sourceFile", "rawFile"};
        pTableFormat = GlazedLists.tableFormat(columnFields, columnTitles);
        this.addList(list);
    }
    
    public void addPeptideHitList(ArrayList<PeptideHit> list) {
        String[] columnTitles = {"Modified Sequence", "Analysis", "Scan", "Query", "Indet", "Expect", "ION", "Ident", "m/z", "Exp. Mass", '\u0394' + "Mass", "Charge", "Experiment", "Source File", "Raw File"};
        String[] columnFields = {"modSequence", "sourceType", "scanNum", "queryNum", "indeterminate", "expect", "ionScore", "ident", "expMass", "expNeutralMass", "diffMass", "Z", "experiment", "sourceFile", "rawFile"};
        pTableFormat = GlazedLists.tableFormat(columnFields, columnTitles);
        this.addList(list);
    }
    
    public void addProteinPeptideHitList(ArrayList<PeptideHit> list) {
        String[] columnTitles = {"Peptide", "Modified Sequence", "Analysis", "Scan", "Query", "Cluster", "Indet", "Expect", "ION", "Ident", "m/z", "Exp. Mass", '\u0394' + "Mass", "Charge", "Experiment", "Source File", "Raw File"};
        String[] columnFields = {"sequence", "modSequence", "sourceType", "scanNum", "queryNum", "cluster", "indeterminate", "expect", "ionScore", "ident", "expMass", "expNeutralMass", "diffMass", "Z", "experiment", "sourceFile", "rawFile"};
        pTableFormat = GlazedLists.tableFormat(columnFields, columnTitles);
        this.addList(list);
    }
}
