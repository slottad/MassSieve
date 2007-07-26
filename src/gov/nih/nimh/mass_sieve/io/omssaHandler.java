/*
 * omssaHandler.java
 *
 * Created on February 24, 2006, 2:02 PM
 *
 * @author Douglas Slotta
 */

package gov.nih.nimh.mass_sieve.io;

import gov.nih.nimh.mass_sieve.*;
import java.util.ArrayList;
import org.xml.sax.*;
import java.util.HashMap;
import org.biojava.bio.symbol.SymbolList;
import org.biojavax.bio.seq.RichSequence;

public class omssaHandler extends AnalysisHandler {
    
    private boolean inMSHitSet;
    private boolean inMSSpectrum;
    private ArrayList<PeptideHit> peptideHits;
    private HashMap<Integer, Integer> QueryToScan;
    private HashMap<Integer, Double> QueryToMass;
    private HashMap<Integer, String> QueryToRawFile;
    private Integer currentQuery;
    private double scaleFactor;
    private String pepQueryNum;
    
    /** Creates a new instance of omssaHandler */
    public omssaHandler(String fn) {
        super(fn);
        analysisProgram = AnalysisProgramType.OMSSA;
        inMSHitSet = false;
        inMSSpectrum = false;
        QueryToScan = new HashMap<Integer, Integer>();
        QueryToMass = new HashMap<Integer, Double>();
        QueryToRawFile = new HashMap<Integer, String>();
        peptideHits = new ArrayList<PeptideHit>();
        scaleFactor = 100.0;
    }
    
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        
        if (sName == "MSHitSet") {
            collectData = true;
            inMSHitSet = true;
        }
        if (sName == "MSSpectrum") {
            collectData = true;
            inMSSpectrum = true;
        }
        
        if (sName == "MSSearchSettings_db") collectData = true;
        if (sName == "MSSearchSettings_scale") collectData = true;
        if (sName == "MSResponse_scale") collectData = true;
        
        if (sName == "MSHits") {
            curPep = new PeptideHit();
            curPep.setSourceType(analysisProgram);
        }
        
        if (inMSHitSet) {
            if (sName == "MSHits_mzhits") collectData = false;
            if (sName == "MSPepHit") {
                curPro = new Protein();
                curProHit = new ProteinHit();
            }
        }
    }
    
    

    public void endElement(String namespaceURI, String sName, String qName) {
        
        if (sName == "MSSearchSettings_db") {
            searchDB = data;
            collectData = false;
            data = "";
        }

        if (sName == "MSResponse_scale") {
            scaleFactor = Double.parseDouble(data);
            collectData = false;
            data = "";
        }
        if (sName == "MSSearchSettings_scale") {
            scaleFactor = Double.parseDouble(data);
            collectData = false;
            data = "";
        }
                
        if (sName == "MSHitSet") {
            collectData = false;
            inMSHitSet = false;
        }
        if (sName == "MSSpectrum") {
            collectData = false;
            inMSSpectrum = false;
        }
        
        if (sName == "MSHits") {
            curPep.setQueryNum(pepQueryNum);
            curPep.setScanNum(QueryToScan.get(pepQueryNum));
            curPep.setRawFile(QueryToRawFile.get(pepQueryNum));
            curPep.setExpMass(QueryToMass.get(pepQueryNum));
            curPep.setDiffMass();
            addPeptideHit(curPep);
            pepHitCount += 1;
            curPep = null;
        }
        if (sName == "MSPepHit") {
            curPro.fixIDandName();
            curProHit.setName(curPro.getName());
            addProtein(curPro);
            curPep.addProteinHit(curProHit);
            //curPep.setProteinName(curProHit.getName());
            //curPep.setStart(curProHit.getStart());
            //curPep.setEnd(curProHit.getEnd());
            //PeptideHit ph = curPep.createClone();
            
            curPro = null;
            curProHit = null;
        }
        if (inMSHitSet) {
            if (sName == "MSHits_mzhits") collectData = true;
            if (sName == "MSHitSet_number") pepQueryNum = data;
            if (sName == "MSHits_charge") curPep.setCharge(data);
            if (sName == "MSHits_mass") curPep.setExpNeutralMass(Integer.parseInt(data));
            if (sName == "MSHits_theomass") curPep.setTheoreticalMass(Integer.parseInt(data));
            if (sName == "MSHits_evalue") curPep.setExpect(data);
            if (sName == "MSHits_pepstring") curPep.setSequence(data);
            if (sName == "MSPepHit_start") curProHit.setStart(Integer.parseInt(data)+1);
            if (sName == "MSPepHit_stop") curProHit.setEnd(Integer.parseInt(data)+1);
            if (sName == "MSPepHit_gi") curPro.setID("gi|" + data);
            if (sName == "MSPepHit_accession") curPro.setName(data);
            if (sName == "MSPepHit_defline") curPro.setDescription(data);
            if (sName == "MSPepHit_protlength") curPro.setLength(data);
            data = "";
        }
        if (inMSSpectrum) {
            if (sName == "MSSpectrum_number") currentQuery = Integer.parseInt(data);
            if (sName == "MSSpectrum_precursormz") {
                QueryToMass.put(currentQuery,Double.parseDouble(data));
            }
            if (sName == "MSSpectrum_ids_E") {
                QueryToScan.put(currentQuery,ScanFilenameToScanNumber(data));
                String rawFile = ScanFilenameToRawFile(data);
                QueryToRawFile.put(currentQuery,rawFile);
                rawFiles.add(rawFile);
            }
            data = "";
        }
    }
    
    public void scaleMasses() {
        for (PeptideHit ph:peptide_hits) {
            ph.setExpMass(ph.getExpMass() / scaleFactor);
            ph.setExpNeutralMass(ph.getExpNeutralMass() / scaleFactor);
            ph.setTheoreticalMass(ph.getTheoreticalMass() / scaleFactor);
        }
    }

    public void addProtein(Protein p) {
        if (!proteinDB.containsKey(p.getName())) {
            RichSequence rs = RichSequence.Tools.createRichSequence(p.getName(),SymbolList.EMPTY_LIST);
            rs.setDescription(p.getDescription());
            proteinDB.put(p.getName(), rs);
        }
    }
}