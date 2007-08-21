/*
 * PeptideTableFormat.java
 *
 * Created on August 20, 2007, 11:39 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.gui;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import gov.nih.nimh.mass_sieve.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

/**
 *
 * @author Douglas J. Slotta
 */
public class PeptideTableFormat implements WritableTableFormat, AdvancedTableFormat {
    private EventList evList;
    private ArrayList<String> experiments;
    private ArrayList<String> columnNames;
    private ArrayList<Class> columnClasses;
    private int phStart, phStop;
    
    //    String[] columnTitles = {"Sequence", "Peptide Hits", "Length", "Num Proteins", "Theoretical Mass", "Indeterminate", "Type", "Analysis", "Cluster", "Files found in"};
    //    String[] columnFields = {"sequence", "numPeptideHits", "Length", "numProteins", "theoreticalMass", "indeterminateType", "peptideType", "sourceTypes", "cluster", "fileList"};
    
    /** Creates a new instance of ProteinTableFormat */
    public PeptideTableFormat(HashSet<String> exp, EventList list) {
        evList = list;
        experiments = new ArrayList<String>();
        experiments.addAll(exp);
        columnNames = new ArrayList<String>();  columnClasses =  new ArrayList<Class>();

        columnNames.add("Sequence");    columnClasses.add(String.class);
        phStart = columnNames.size();
        if (experiments.size() > 1) {
            for (String e:experiments) {
                columnNames.add(e + " PepHits"); columnClasses.add(Integer.class);
            }
        } else {
            columnNames.add("PepHits");      columnClasses.add(Integer.class);
        }
        phStop = phStart + experiments.size()-1;
        columnNames.add("Length");           columnClasses.add(Integer.class);
        columnNames.add("Num Proteins");     columnClasses.add(Integer.class);
        columnNames.add("Theoretical Mass"); columnClasses.add(Double.class);
        columnNames.add("Indeterminate");    columnClasses.add(PeptideIndeterminacyType.class);
        columnNames.add("Type");             columnClasses.add(ParsimonyType.class);
        columnNames.add("Analysis");         columnClasses.add(String.class);
        columnNames.add("Cluster");          columnClasses.add(Integer.class);
        columnNames.add("Files found in");   columnClasses.add(String.class);
    }
    
    public Class getColumnClass(int column) {
        return columnClasses.get(column);
    }
    
    public int getColumnCount() {
        return columnNames.size();
    }
    
    public String getColumnName(int column) {
        return columnNames.get(column);
        //throw new IllegalStateException();
    }
    public Comparator getColumnComparator(int column) {
        return GlazedLists.comparableComparator();
    }
    
    
    public boolean isEditable(Object baseObject, int column) {
        return true;
        //return baseObject instanceof SeparatorList.Separator;
    }
    
    public Object setColumnValue(Object baseObject, Object editedValue, int column) {
        return null;
    }
    
    public Object getColumnValue(Object baseObject, int column) {
        if (baseObject == null) return null;
        Peptide p = (Peptide)baseObject;
        if (column == 0) return p.getSequence();
        else if ((column >= phStart) && (column <= phStop)) {
            if (experiments.size() > 1) return p.getNumPeptideHits(experiments.get(column-phStart));
            else return p.getNumPeptideHits();
        } else if (column == (phStop+1)) return p.getLength();
        else if (column == (phStop+2)) return p.getNumProteins();
        else if (column == (phStop+3)) return p.getTheoreticalMass();
        else if (column == (phStop+4)) return p.getIndeterminateType();
        else if (column == (phStop+5)) return p.getPeptideType();
        else if (column == (phStop+6)) return p.getSourceTypes(false);
        else if (column == (phStop+7)) return p.getCluster();
        else if (column == (phStop+8)) return p.getFileList();
        throw new IllegalStateException();
    }
    
    
}
