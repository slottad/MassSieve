/*
 * PeptideHit.java
 *
 * Created on March 6, 2006, 5:37 PM
 *
 * @author Douglas Slotta
 */

package gov.nih.nimh.mass_sieve;

import gov.nih.nimh.mass_sieve.io.AnalysisProgramType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;

public class PeptideHit implements Serializable, Comparable<PeptideHit> {
    private int queryNum;
    private int scanNum;
    private double expMass;
    private double expNeutralMass;
    private double theoreticalMass;
    private double diffMass;
    private String sequence;
    private String modSequence;
    private double expect;
    private double ionScore;
    private double ident;
    private double xcorr;
    private double pepProphet;
    private int Z;
    private HashSet<ProteinHit> proteinHits;
    private HashSet<String> proteinNames;
    private boolean indeterminate;
    private AnalysisProgramType sourceType;
    private boolean pepXML;
    private String sourceFile;
    private String rawFile;
    private String experiment;
    
    /**
     * Creates a new instance of PeptideHit
     */
    public PeptideHit() {
        //start = -1;
        //end = -1;
        expect = Double.MAX_VALUE;
        Z = 0;
        //proName = null;
        proteinHits = new HashSet<ProteinHit>();
        proteinNames = new HashSet<String>();
        theoreticalMass = -1;
        indeterminate = false;
        pepXML = false;
    }
    
    private PeptideHit createClone() {
        PeptideHit ph = new PeptideHit();
        ph.queryNum = queryNum;
        ph.scanNum = scanNum;
        ph.expMass = expMass;
        ph.expNeutralMass = expNeutralMass;
        ph.theoreticalMass = theoreticalMass;
        ph.diffMass = diffMass;
        ph.sequence = sequence;
        ph.modSequence = modSequence;
        ph.expect = expect;
        ph.ionScore = ionScore;
        ph.ident = ident;
        ph.xcorr = xcorr;
        ph.pepProphet = pepProphet;
        ph.Z = Z;
        ph.indeterminate = indeterminate;
        ph.sourceType = sourceType;
        ph.pepXML = pepXML;
        ph.sourceFile = sourceFile;
        ph.rawFile = rawFile;
        ph.experiment = experiment;
        //ph.proteinHits = new HashSet<ProteinHit>();
        //ph.proteinHits.addAll(proteinHits);
        //ph.proteinNames = new HashSet<String>();
        //ph.proteinNames.addAll(proteinNames);
        
        return ph;
    }
    
    // This method will return a clone, minus masked proteins
    // otherwise it will return itself if the hit does not contain
    // any proteins in the list
    public PeptideHit maskProtein(HashSet<String> verbotten) {
        boolean returnCopy = false;
        HashSet<ProteinHit> proHits = new HashSet<ProteinHit>();
        for (ProteinHit ph:proteinHits) {
            if (verbotten.contains(ph.getName())) {
                returnCopy = true;
            } else {
                proHits.add(ph);
            }
        }
        if (returnCopy) {
            if (proHits.isEmpty()) return null;
            PeptideHit newHit = this.createClone();
            newHit.addProteinHits(proHits);
            return newHit;
        }
        return this;
    }
    
    public int compareTo(PeptideHit p) {
        return sequence.compareToIgnoreCase(p.getSequence());
    }
    
    public boolean equals(Object obj) {
        PeptideHit p = (PeptideHit)obj;
        if (p.getSequence().equals(sequence)) {
            if (p.getScanNum() == scanNum) {
                if (p.getQueryNum() == queryNum) {
                    if (p.getSourceType().equals(sourceType)) {
                        if (p.getSourceFile().equals(sourceFile)) {
                            if (p.getExperiment().equals(experiment)) {
                                if (p.getCharge() == Z) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public int hashCode() {
        return scanNum;
        //return sequence.hashCode();
    }
    
    public void setSequence(String s) {
        sequence = new String(s).intern();
    }
    
    public String getSequence() {
        return sequence;
    }
    
    public void setModSequence(String s) {
        modSequence = new String(s).intern();
    }
    
    public String getModSequence() {
        return modSequence;
    }
    
    void print() {
        System.out.print(" Scan Num: " + scanNum);
        System.out.print(" Query Num: " + queryNum);
        System.out.print(" Type: " + sourceType);
        System.out.print(" Z: " + Z);
        System.out.print(" Expect: " + expect);
        System.out.print(" Seq: " + sequence);
        System.out.println(" Src: " + sourceFile);
    }
        
    public void setExpect(String s) {
        expect = Double.parseDouble(s);
    }
    public void setExpect(double s) {
        expect = s;
    }
    public void setCharge(String c) {
        Z = Integer.parseInt(c);
    }
    public void setCharge(int c) {
        Z = c;
    }
    
    public void addProteinHit(ProteinHit p) {
        proteinHits.add(p);
        proteinNames.add(p.getName());
    }
    
    public void addProteinHits(HashSet<ProteinHit> proHits) {
        proteinHits.addAll(proHits);
        for (ProteinHit p:proHits) {
            proteinNames.add(p.getName());
        }
    }
    
    public HashSet<ProteinHit> getProteinHits() {
        return proteinHits;
    }
    
    public HashSet<String> getProteinNames() {
        return proteinNames;
    }
    
    public boolean containsProtein(String proName) {
        for (ProteinHit p:proteinHits) {
            if (p.getName().equalsIgnoreCase(proName)) return true;
        }
        return false;
    }
    
    public void setSourceType(AnalysisProgramType t) {
        sourceType = t;
    }
    public void setSourceFile(String s) {
        sourceFile = s.intern();
    }
    
    public double getExpect() {
        return expect;
    }
    public int getCharge() {
        return Z;
    }
    public int getZ() {
        return Z;
    }

    public AnalysisProgramType getSourceType() {
        return sourceType;
    }
    public String getSourceFile() {
        return sourceFile;
    }
    
    public int getQueryNum() {
        return queryNum;
    }
    
    public void setQueryNum(int qn) {
        queryNum = qn;
    }
    
    //public void setQueryNum(String qn) {
    //    queryNum = Integer.parseInt(qn);
    //    //queryNum = qn;
    //}
    
    public int getScanNum() {
        return scanNum;
    }
    
    public void setScanNum(String sn) {
        scanNum = Integer.parseInt(sn);
    }
    
    public void setScanNum(int sn) {
        scanNum = sn;
    }
    
    public double getExpMass() {
        return expMass;
    }
    
    public void setExpMass(String m) {
        expMass = Double.parseDouble(m);
    }
    
    public void setExpMass(double m) {
        expMass = m;
    }
    
    public double getExpNeutralMass() {
        return expNeutralMass;
    }
    
    public void setExpNeutralMass(String m) {
        expNeutralMass = Double.parseDouble(m);
    }
    
    public void setExpNeutralMass(double m) {
        expNeutralMass = m;
    }
    
    public double getTheoreticalMass() {
        return theoreticalMass;
    }
    
    public void setTheoreticalMass(String m) {
        theoreticalMass = Double.parseDouble(m);
    }
    
    public void setTheoreticalMass(double m) {
        theoreticalMass = m;
    }
    
    public double getDiffMass() {
        return diffMass;
    }
    
    public void setDiffMass(double m) {
        diffMass = m;
    }
    
    public void setDiffMass(String m) {
        try {
            diffMass = Double.parseDouble(m);
        } catch (NumberFormatException e) {
            // do nothing
        }
    }    
    public void setDiffMass() {
        diffMass = (new BigDecimal(expNeutralMass - theoreticalMass)).setScale(2,BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }
    
    public void setIdent(String i) {
        ident = Double.parseDouble(i);
    }
    
    public void setIdent(double d) {
        ident = d;
    }
    
    public double getIdent() {
        return ident;
    }
    
    public double getIonScore() {
        return ionScore;
    }
    
    public void setIonScore(String s) {
        ionScore = Double.parseDouble(s);
    }
    public void setIonScore(double d) {
        ionScore = d;
    }
    
    public String getExperiment() {
        return experiment;
    }
    
    public void setExperiment(String e) {
        experiment = e.intern();
    }
    
    public boolean isIndeterminate() {
        return indeterminate;
    }
    
    public String getIndeterminate() {
        if (indeterminate) {
            return "Indeterminate";
        }
        return "";
    }
    
    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
    }
    
    public String getRawFile() {
        return rawFile;
    }
    
    public void setRawFile(String s) {
        rawFile = s;
    }

    public String getScanTuple() {
        return this.getRawFile() + ":" + this.getScanNum();
    }
    
    public void normalizeXcorr() {
        double cutoff;
        
        // Should make these parameters
        switch (this.getCharge()) {
            case 1: cutoff = 1.8; break;
            case 2: cutoff = 2.5; break;
            case 3: cutoff = 3.5; break;
            default: cutoff = 3.5; break;
        }
        expect = (xcorr - cutoff) / cutoff;
    }
    
    public double getXcorr() {
        return xcorr;
    }

    public void setXcorr(double xcorr) {
        this.xcorr = xcorr;
    }

    public void setXcorr(String xcorr) {
        this.xcorr = Double.parseDouble(xcorr);
    }
    public double getPepProphet() {
        return pepProphet;
    }

    public void setPepProphet(String s) {
        pepProphet = Double.parseDouble(s);
    }

    public void setPepProphet(double d) {
        pepProphet = d;
    }

    public boolean isPepXML() {
        return pepXML;
    }

    public void setPepXML(boolean pepXML) {
        this.pepXML = pepXML;
    }
}
