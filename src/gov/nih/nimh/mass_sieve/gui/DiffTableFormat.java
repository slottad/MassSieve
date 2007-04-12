/*
 * DiffTableFormat.java
 *
 * Created on October 25, 2006, 1:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.gui;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import gov.nih.nimh.mass_sieve.Protein;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author slotta
 */
public class DiffTableFormat implements AdvancedTableFormat {
    private HashMap<String, ExperimentPanel> expSet;
    private HashMap<Integer, String> expLocation;
    private ArrayList<String> experiments;
    private ArrayList<String> columnNames;
    private ArrayList<Class> columnClasses;
    
    /** Creates a new instance of DiffTableFormat */
    public DiffTableFormat(HashMap<String, ExperimentPanel> expSet) {
        this.expSet = expSet;
        expLocation = new HashMap<Integer, String>();
        experiments = new ArrayList<String>();
        experiments.addAll(expSet.keySet());
        Collections.sort(experiments);
        columnNames = new ArrayList<String>();       columnClasses =  new ArrayList<Class>();
        columnNames.add("Protein Name");             columnClasses.add(String.class);
        int location = 1;
        for (String eName:experiments) {
            columnNames.add(eName + " Parsimony");   columnClasses.add(String.class);  expLocation.put(location++, eName);
            columnNames.add(eName + " Cover");       columnClasses.add(Integer.class); expLocation.put(location++, eName);
            columnNames.add(eName + " %Cover");      columnClasses.add(Double.class);  expLocation.put(location++, eName);
            columnNames.add(eName + " Unique Peps"); columnClasses.add(Integer.class); expLocation.put(location++, eName);
            columnNames.add(eName + " PepHits");     columnClasses.add(Integer.class); expLocation.put(location++, eName);
        }
    }
    
    public Class getColumnClass(int column) {
        return columnClasses.get(column);
    }
    
    public int getColumnCount() {
        return columnNames.size();
    }
    
    public String getColumnName(int column) {
        return columnNames.get(column);
    }
    public Comparator getColumnComparator(int column) {
        return GlazedLists.comparableComparator();
    }
        
    public Object getColumnValue(Object baseObject, int column) {
        String pName = (String)baseObject;
        if (column == 0) return pName;
        String expName = expLocation.get(column);
        Protein protein = expSet.get(expName).getProteins().get(pName);
        if (protein == null) return null;
        switch ((column-1) % 5) {
            case 0:
                return protein.getParsimonyType();
            case 1:
                return protein.getCoverageNum();
            case 2:
                return protein.getCoveragePercent();
            case 3:
                return protein.getNumUniquePeptides();
            case 4:
                return protein.getNumPeptideHits();
        }
        return null;
    }
}