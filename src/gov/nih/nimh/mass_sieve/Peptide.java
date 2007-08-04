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
     * Creates a new instance of Peptide
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
    
    public String getSequence() {
        return sequence;
    }
    
    public ArrayList<PeptideHit> getPeptideHits() {
        return peptideHits;
    }
    
    public int getCluster() {
        return cluster;
    }
    
    public void setCluster(Integer c) {
        cluster = c;
        //for (PeptideHit p:peptideHits) {
        //    p.setCluster(c);
        //}
    }
    
    public ArrayList<PeptideHit> getOmssa() {
        return omssa;
    }
    public ArrayList<PeptideHit> getXTandem() {
        return xtandem;
    }
    public ArrayList<PeptideHit> getMascot() {
        return mascot;
    }
    public HashSet<String> getProteins() {
        return proteinSet;
    }
    
    public void updateProteins(HashMap<String, Protein> mProteins) {
        proteinList = new ArrayList<Protein>();
        Iterator<String> i = proteinSet.iterator();
        while (i.hasNext()) {
            Protein p = mProteins.get(i.next());
            proteinList.add(p);
        }
    }
    
    public DefaultMutableTreeNode getTree() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(this);
        for (Protein pro:proteinList) {
            node.add(new DefaultMutableTreeNode(pro));
        }
        return node;
    }
    
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
    
    public boolean containsOmssa() {
        if ( omssa.size() > 0 ) return true;
        return false;
    }
    
    public boolean containsMascot() {
        if ( mascot.size() > 0 ) return true;
        return false;
    }
    
    public boolean containsXTandem() {
        if ( xtandem.size() > 0 ) return true;
        return false;
    }
    
    public boolean containsSequest() {
        if ( sequest.size() > 0 ) return true;
        return false;
    }
    
    public String toString() {
        //return sequence + " (" + proteinSet.size() + ")";
        return sequence;
    }
    
    public String toCSVString() {
        return sequence + ","
                + getNumPeptideHits() + ","
                + getLength() + ","
                + getNumProteins() + ","
                + getTheoreticalMass() + ","
                + getPeptideType() + ","
                + getSourceTypes();
    }
    
    public Integer getNumPeptideHits() {
        return uniqueScanNumbers.size();
    }
    public Integer getNumProteins() {
        return proteinSet.size();
    }
    public Double getTheoreticalMass() {
        return theoreticalMass;
    }
    public Integer getLength() {
        return sequence.length();
    }
    
    public JPanel getInfoPanel(ExperimentPanel ePanel) {
        if (infoPanel == null) {
            infoPanel = new PeptideHitPanel(this, ePanel);
        }
        return infoPanel;
    }
    
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
    
    public ParsimonyType getPeptideType() {
        return pType;
    }
    
    public HashSet<String> getExperimentSet() {
        return experimentSet;
    }
    
    public HashSet<String> getFileSet() {
        return fileSet;
    }
    
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
    
    public int compareTo(Peptide p) {
        return sequence.compareTo(p.getSequence());
    }
    
    public PeptideIndeterminacyType getIndeterminateType() {
        return indeterminateType;
    }
}
