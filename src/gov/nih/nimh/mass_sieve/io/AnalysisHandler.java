/*
 * AnalysisHandler.java
 *
 * Created on February 23, 2006, 9:13 AM
 *
 * @author Douglas Slotta
 */

package gov.nih.nimh.mass_sieve.io;

import gov.nih.nimh.mass_sieve.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.biojavax.bio.seq.RichSequence;
import org.xml.sax.helpers.DefaultHandler;

abstract public class AnalysisHandler extends DefaultHandler {
    
    protected boolean collectData;
    protected String data;
    
    protected AnalysisProgramType analysisProgram;
    protected String sourceFile;
    protected String searchDB;
    protected int pepHitCount;
    protected HashSet<String> rawFiles;
    
    protected ArrayList<PeptideHit> peptide_hits;
    protected HashMap<String, RichSequence> proteinDB;
    private HashMap<Integer, Double> scanExpectFilter;
    private HashMap<Integer, Boolean> scanExpectIndeterminate;
    
    Protein curPro;
    PeptideHit curPep;
    ProteinHit curProHit;
    
    public AnalysisHandler(String fn) {
        sourceFile = fn;
        collectData = false;
        data = "";
        rawFiles = new HashSet<String>();
        pepHitCount = 0;
        peptide_hits = new ArrayList<PeptideHit>();
        proteinDB = new HashMap<String, RichSequence>();
        scanExpectFilter = new HashMap<Integer, Double>();
        scanExpectIndeterminate = new HashMap<Integer, Boolean>();
    }
    
    public void characters(char chars[], int start, int length) {
        if (collectData){
            String s = new String(chars, start, length);
            if (s.trim() != "") data = data + s.trim();
        }
    }
    
    public void addPeptideHit(PeptideHit p) {
        peptide_hits.add(p);
        int scan = p.getScanNum();
        if (scanExpectFilter.containsKey(scan)) {
            if (scanExpectFilter.get(scan) > p.getExpect()) {
                scanExpectFilter.put(p.getScanNum(),p.getExpect());
                scanExpectIndeterminate.put(p.getScanNum(),false);
            } else if (scanExpectFilter.get(scan) < p.getExpect()) {
                // do nothing
            } else {  // Must be indeterminate
                scanExpectIndeterminate.put(p.getScanNum(),true);
            }
        } else {
            scanExpectFilter.put(p.getScanNum(),p.getExpect());
            scanExpectIndeterminate.put(p.getScanNum(),false);
        }
    }
    
    public ArrayList<PeptideHit> getPeptideHits() {
        if (scanExpectFilter.size() > 0) {
            ArrayList<PeptideHit> filtered_hits = new ArrayList<PeptideHit>();
            for (PeptideHit p:peptide_hits) {
                int scan = p.getScanNum();
                if (p.getExpect() <= scanExpectFilter.get(scan)) {
                    p.setIndeterminate(scanExpectIndeterminate.get(scan));
                    filtered_hits.add(p);
                } //else {
                //    System.out.print("Rejected: ");
                //    p.print();
                //}
            }
            return filtered_hits;
        } else {
            return peptide_hits;
        }
    }
    
    protected int ScanFilenameToScanNumber(String fn) {
        StringBuffer sb = new StringBuffer(fn);
        int val, start, stop;
        start = sb.indexOf(".")+1;
        stop = sb.substring(start).indexOf(".")+start;
        if ((start != -1) && (stop != -1)) {
            val = Integer.parseInt(sb.substring(start,stop));
        } else {
            start = sb.lastIndexOf(" ")+1;
            val = Integer.parseInt(sb.substring(start));
        }
        
        return val;
    }
    
    protected String ScanFilenameToRawFile(String fn) {
        StringBuffer sb  = new StringBuffer(fn);
        String val;
        int start, stop;
        
        // This hack is to hack off the file path, if it exists.
        start = sb.lastIndexOf("/");
        if (start == -1) {
            start = sb.lastIndexOf("\\");
            if (start == -1) {
                start = sb.lastIndexOf("%2f");
                if (start == -1) {
                    start = 0;
                } else { start += 3; }
            } else { start += 2; }
        } else { start += 1; }
        
        stop = sb.indexOf(".",start);
        if ((start != -1) && (stop != -1)) {
            val = sb.substring(start,stop);
        } else {
            start = sb.lastIndexOf(" ");
            val = sb.substring(0,start);
        }
        return val;
    }
    
    public HashMap<String, RichSequence> getProteinDB() {
        return proteinDB;
    }
    
    public FileInformation getFileInformation() {
        FileInformation fInfo = new FileInformation();
        
        fInfo.setAnalysisProgram(analysisProgram);
        fInfo.setPepHitCount(pepHitCount);
        fInfo.setRawFiles(rawFiles);
        fInfo.setSearchDB(searchDB);
        File f = new File(sourceFile);
        fInfo.setSourceFile(f.getName());
        
        return fInfo;
    }
}