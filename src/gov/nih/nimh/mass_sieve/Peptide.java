/*
 * Peptide.java
 *
 * Created on May 10, 2006, 10:59 AM
 *
 * @author Douglas Slotta
 */

package gov.nih.nimh.mass_sieve;
import gov.nih.nimh.mass_sieve.gui.ExperimentPanel;
import gov.nih.nimh.mass_sieve.gui.PeptideHitListPanel;
import gov.nih.nimh.mass_sieve.gui.PeptideHitPanel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Holds all the information pertaining to a given peptide. Including all of its peptide hits
 * and proteins it belongs to. A peptide can be viewed as a collection of peptide hits.
 */
public class Peptide implements Serializable, Comparable<Peptide> {
    private String sequence;
    private ArrayList<PeptideHit> peptideHits;
    private HashSet<String> uniqueScanNumbers;
    private ArrayList<PeptideHit> omssa;
    private ArrayList<PeptideHit> mascot;
    private ArrayList<PeptideHit> xtandem;
    private ArrayList<PeptideHit> sequest;
    private HashSet<String> proteinSet;
    private HashSet<String> experimentSet;
    private String experimentList;
    private HashSet<String> fileSet;
    private String fileList;
    private ArrayList<Protein> proteinList;
    private int cluster;
    private ParsimonyType pType;
    private PeptideIndeterminacyType indeterminateType;
    private double theoreticalMass;
    transient private PeptideHitPanel infoPanel;
    
    /**
     * Creates a new instance of Peptide from a peptide hit.
     * @param p The initial peptide hit that forms the basis for this peptide.
     */
    public Peptide(PeptideHit p) {
        sequence = p.getSequence();
        peptideHits = new ArrayList<PeptideHit>();
        uniqueScanNumbers = new HashSet<String>();
        omssa = new ArrayList<PeptideHit>();
        mascot = new ArrayList<PeptideHit>();
        xtandem = new ArrayList<PeptideHit>();
        sequest = new ArrayList<PeptideHit>();
        proteinSet = new HashSet<String>();
        experimentSet = new HashSet<String>();
        fileSet = new HashSet<String>();
        pType = ParsimonyType.DISTINCT;
        theoreticalMass = -1;
        infoPanel = null;
        indeterminateType = null; //PeptideIndeterminacyType.NONE;
        this.addPeptideHit(p);
    }
    
    /**
     * Adds a new PeptideHit to the peptide object.  Must have the same peptide sequence.
     * @param p The peptide hit to be added.
     */
    public void addPeptideHit(PeptideHit p) {
        if (!p.getSequence().contentEquals(sequence)) {
            System.err.println(sequence);
            System.err.println(p.getSequence());
            System.err.println("Something is wrong, tried to add a peptide to the wrong group!");
            System.exit(1);
        }
        peptideHits.add(p);
        uniqueScanNumbers.add(p.getSourceFile() + p.getScanNum());
        switch (p.getSourceType()) {
            case MASCOT:  mascot.add(p);  break;
            case OMSSA:   omssa.add(p);   break;
            case XTANDEM: xtandem.add(p); break;
            case SEQUEST: sequest.add(p); break;
            case UNKNOWN:
                System.err.println("Unable to determine source of peptide");
                System.exit(1);
                break;
        }
        for (ProteinHit pro:p.getProteinHits()) {
            proteinSet.add(pro.getName());
        }
        //proteinSet.add(p.getProteinName());
        if (proteinSet.size() > 1) {
            pType = ParsimonyType.SHARED;
        }
        if (theoreticalMass < 0) {
            if (p.getTheoreticalMass() > 0) {
                theoreticalMass = p.getTheoreticalMass();
            }
        }
        if (indeterminateType == null) {
            if (p.isIndeterminate()) {
                indeterminateType = PeptideIndeterminacyType.ALL;
            } else {
                indeterminateType = PeptideIndeterminacyType.NONE;
            }
        } else {
            if (p.isIndeterminate() && indeterminateType == PeptideIndeterminacyType.NONE) {
                indeterminateType = PeptideIndeterminacyType.SOME;
            }
            if (!p.isIndeterminate() && indeterminateType == PeptideIndeterminacyType.ALL) {
                indeterminateType = PeptideIndeterminacyType.SOME;
            }
        }
        experimentSet.add(p.getExperiment());
        //fileSet.add(p.getSourceFile());
        fileSet.add(p.getRawFile());
    }
    
    /**
     * Returns the amino acid sequence for this peptide
     * @return The string of amino acids.
     */
    public String getSequence() {
        return sequence;
    }
    
    /**
     * Retrieves the list of PeptideHits
     * @return list of PeptideHits
     */
    public ArrayList<PeptideHit> getPeptideHits() {
        return peptideHits;
    }
    
    /**
     * Retrieves the number of the cluster that this peptide belongs to.
     * @return Cluster number
     */
    public int getCluster() {
        return cluster;
    }
    
    /**
     * Sets the number of the cluster that the peptide belongs to.
     * @param c Cluster number
     */
    public void setCluster(Integer c) {
        cluster = c;
        //for (PeptideHit p:peptideHits) {
        //    p.setCluster(c);
        //}
    }
    
    /**
     * Returns the list of PeptideHits found by OMSSA.
     * @return The list of PeptideHits found by OMSSA
     */
    public ArrayList<PeptideHit> getOmssa() {
        return omssa;
    }
    /**
     * Returns the list of PeptideHits found by X!Tandem
     * @return The list of PeptideHits found by X!Tandem
     */
    public ArrayList<PeptideHit> getXTandem() {
        return xtandem;
    }
    /**
     * Returns the list of PeptideHits found by Mascot
     * @return The list of PeptideHits found by Mascot
     */
    public ArrayList<PeptideHit> getMascot() {
        return mascot;
    }
    /**
     * Returns a list of the names of the proteins to which this peptide belongs
     * @return List of protein names
     */
    public HashSet<String> getProteins() {
        return proteinSet;
    }
    
    /**
     * Since the proteins are constructed after the peptides, this method allows the protein objects
     * to be added to the peptide.
     * @param mProteins The set of proteins to be updated
     */
    public void updateProteins(HashMap<String, Protein> mProteins) {
        proteinList = new ArrayList<Protein>();
        Iterator<String> i = proteinSet.iterator();
        while (i.hasNext()) {
            Protein p = mProteins.get(i.next());
            proteinList.add(p);
        }
    }
    
    /**
     * 
     * @return 
     */
//    public DefaultMutableTreeNode getTree() {
//        DefaultMutableTreeNode node = new DefaultMutableTreeNode(this);
//        for (Protein pro:proteinList) {
//            node.add(new DefaultMutableTreeNode(pro));
//        }
//        return node;
//    }
    
    /**
     * 
     * @return 
     */
    public String getSourceTypes() {
        StringBuilder sb = new StringBuilder();
        if (containsMascot()) {
            sb.append("MASCOT");
        }
        if (containsOmssa()) {
            if (sb.length() > 0) sb.append(",");
            sb.append("OMMSA");
        }
        if (containsXTandem()) {
            if (sb.length() > 0) sb.append(",");
            sb.append("XTANDEM");
        }
        return sb.toString();
    }
    
    /**
     * 
     * @return 
     */
    public boolean containsOmssa() {
        if ( omssa.size() > 0 ) return true;
        return false;
    }
    
    /**
     * 
     * @return 
     */
    public boolean containsMascot() {
        if ( mascot.size() > 0 ) return true;
        return false;
    }
    
    /**
     * 
     * @return 
     */
    public boolean containsXTandem() {
        if ( xtandem.size() > 0 ) return true;
        return false;
    }
    
    /**
     * 
     * @return 
     */
    public boolean containsSequest() {
        if ( sequest.size() > 0 ) return true;
        return false;
    }
    
    /**
     * 
     * @return 
     */
    public String toString() {
        //return sequence + " (" + proteinSet.size() + ")";
        return sequence;
    }
    
    /**
     * 
     * @return 
     */
    public String toCSVString() {
        return sequence + ","
                + getNumPeptideHits() + ","
                + getLength() + ","
                + getNumProteins() + ","
                + getTheoreticalMass() + ","
                + getPeptideType() + ","
                + getSourceTypes();
    }
    
    /**
     * 
     * @return 
     */
    public Integer getNumPeptideHits() {
        return uniqueScanNumbers.size();
    }
    /**
     * 
     * @return 
     */
    public Integer getNumProteins() {
        return proteinSet.size();
    }
    /**
     * 
     * @return 
     */
    public Double getTheoreticalMass() {
        return theoreticalMass;
    }
    /**
     * 
     * @return 
     */
    public Integer getLength() {
        return sequence.length();
    }
    
    /**
     * 
     * @param ePanel 
     * @return 
     */
    public JPanel getInfoPanel(ExperimentPanel ePanel) {
        if (infoPanel == null) {
            infoPanel = new PeptideHitPanel(this, ePanel);
        }
        return infoPanel;
    }
    
    /**
     * 
     * @param ePanel 
     * @return 
     */
    public JScrollPane getJTable(ExperimentPanel ePanel) {
        PeptideHitListPanel lp = new PeptideHitListPanel(ePanel);
        lp.addPeptideHitList(peptideHits);
        return lp.createTable();
    }
    
//    public JScrollPane getJTableProteins(ExperimentPanel ePanel) {
//        ListPanel lp = new ListPanel(ePanel);
//        lp.addPeptideHitList(peptideHits);
//        return lp.createTable();
//    }
    
    /**
     * 
     * @return 
     */
    public ParsimonyType getPeptideType() {
        return pType;
    }
    
    /**
     * 
     * @return 
     */
    public HashSet<String> getExperimentSet() {
        return experimentSet;
    }
    
    /**
     * 
     * @return 
     */
    public HashSet<String> getFileSet() {
        return fileSet;
    }
    
    /**
     * 
     * @return 
     */
    public String getExperimentList() {
        if (experimentList == null) {
            String buf = null;
            ArrayList<String> expList = new ArrayList<String>(experimentSet);
            Collections.sort(expList);
            for (String p:expList) {
                if (buf == null) {
                    buf = new String(p);
                } else {
                    buf += ", " + p;
                }
            }
            experimentList = buf;
        }
        return experimentList;
    }
    
    /**
     * 
     * @return 
     */
    public String getFileList() {
        if (fileList == null) {
            String buf = null;
            ArrayList<String> fList = new ArrayList<String>(fileSet);
            Collections.sort(fList);
            for (String p:fList) {
                if (buf == null) {
                    buf = new String(p);
                } else {
                    buf += ", " + p;
                }
            }
            fileList = buf;
        }
        return fileList;
    }
    
    /**
     * 
     * @param p 
     * @return 
     */
    public int compareTo(Peptide p) {
        return sequence.compareTo(p.getSequence());
    }
    
    /**
     * 
     * @return 
     */
    public PeptideIndeterminacyType getIndeterminateType() {
        return indeterminateType;
    }
}
