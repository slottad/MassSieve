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
import org.xml.sax.helpers.DefaultHandler;

abstract public class AnalysisHandler extends DefaultHandler {

    public final static double MASS_HYDROGEN = 1.007277;
    protected boolean collectData;
    protected String data;
    protected AnalysisProgramType analysisProgram;
    protected String sourceFile;
    protected String searchDB;
    protected int pepHitCount;
    protected HashSet<String> rawFiles;
    protected ArrayList<PeptideHit> peptide_hits;
    protected HashMap<String, ProteinInfo> proteinDB;
    private HashMap<Integer, Double> scanExpectFilter;
    private HashMap<Integer, Boolean> scanExpectIndeterminate;
    ProteinInfo curPro;
    PeptideHit curPep;
    ProteinHit curProHit;

    public AnalysisHandler(String fn) {
        sourceFile = fn;
        collectData = false;
        data = "";
        rawFiles = new HashSet<String>();
        pepHitCount = 0;
        peptide_hits = new ArrayList<PeptideHit>();
        proteinDB = new HashMap<String, ProteinInfo>();
        scanExpectFilter = new HashMap<Integer, Double>();
        scanExpectIndeterminate = new HashMap<Integer, Boolean>();
    }

    public void characters(char chars[], int start, int length) {
        if (collectData) {
            String s = new String(chars, start, length);
            if (!s.trim().equals("")) {
                data = data + s.trim();
            }
        }
    }

    public void addPeptideHit(PeptideHit p) {
        peptide_hits.add(p);
        if (p.getSourceType() == AnalysisProgramType.SEQUEST) {
            p.normalizeXcorr();
        }
        double evalue = p.getExpect();
        int scan = p.getScanNum();
        if (scanExpectFilter.containsKey(scan)) {
            if (scanExpectFilter.get(scan) > evalue) {
                scanExpectFilter.put(p.getScanNum(), evalue);
                scanExpectIndeterminate.put(p.getScanNum(), false);
            } else if (scanExpectFilter.get(scan) < evalue) {
            // do nothing
            } else {  // Must be indeterminate do we still need this here?
                scanExpectIndeterminate.put(p.getScanNum(), true);
            }
        } else {
            scanExpectFilter.put(p.getScanNum(), evalue);
            scanExpectIndeterminate.put(p.getScanNum(), false);
        }
    }

    public ArrayList<PeptideHit> getPeptideHits() {
        if (scanExpectFilter.size() > 0) {
            ArrayList<PeptideHit> filtered_hits = new ArrayList<PeptideHit>();
            for (PeptideHit p : peptide_hits) {
                int scan = p.getScanNum();
                double evalue = p.getExpect();
                if (evalue <= scanExpectFilter.get(scan)) {
                    p.setIndeterminate(scanExpectIndeterminate.get(scan));
                    filtered_hits.add(p);
                }
            }
            return filtered_hits;
        } else {
            return peptide_hits;
        }
    }

    protected int ScanFilenameToScanNumber(String fn) {
        StringBuffer sb = new StringBuffer(fn);
        int val, start, stop;
        start = sb.indexOf(".") + 1;
        stop = sb.substring(start).indexOf(".") + start;
        try {
            if ((start != -1) && (stop != -1)) {
                val = Integer.parseInt(sb.substring(start, stop));
            } else {
                start = sb.lastIndexOf(" ") + 1;
                val = Integer.parseInt(sb.substring(start));
            }
        } finally {
            //sb.toString().
        }

        return val;
    }

    protected String ScanFilenameToRawFile(String fn) {
        StringBuffer sb = new StringBuffer(fn);
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
                } else {
                    start += 3;
                }
            } else {
                start += 2;
            }
        } else {
            start += 1;
        }

        stop = sb.indexOf(".", start);
        if ((start != -1) && (stop != -1)) {
            val = sb.substring(start, stop);
        } else {
            start = sb.lastIndexOf(" ");
            val = sb.substring(0, start);
        }
        return val;
    }

    protected String stripPathAndExtension(String iStr) {
        int loc;
        String newStr;

        loc = iStr.lastIndexOf('\\');
        if (loc > 0) {
            newStr = iStr.substring(loc + 1);
        } else {
            newStr = iStr;
        }

        loc = newStr.lastIndexOf('/');
        if (loc > 0) {
            newStr = newStr.substring(loc + 1);
        }

        loc = newStr.lastIndexOf('.');
        if (loc > 0) {
            newStr = newStr.substring(0, loc);
        }

        return newStr;
    }

    public void addProtein(ProteinInfo p) {
        if (proteinDB.containsKey(p.getName())) {
            proteinDB.get(p.getName()).update(p);
        } else {
            proteinDB.put(p.getName(), p);
        }
    }

    public HashMap<String, ProteinInfo> getProteinDB() {
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
