/*
 * pepXMLHandler.java
 *
 * Created on August 21, 2007, 1:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.io;

import gov.nih.nimh.mass_sieve.*;
import org.xml.sax.*;

/**
 *
 * @author slotta
 */
public class pepXMLHandler extends AnalysisHandler {
    String mzFileName;
    boolean inSpectrumQuery;
    int curScan;
    int curQuery;
    int curCharge;
    double curExpMass;
    
    /** Creates a new instance of pepXMLHandler */
    public pepXMLHandler(String fn) {
        super(fn);
        analysisProgram = AnalysisProgramType.PEPXML;
        inSpectrumQuery = false;
    }
    
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        
        if (sName == "search_summary") {
            mzFileName = stripPathAndExtension(attrs.getValue("base_name"));
            String aProg = attrs.getValue("search_engine");
            if (aProg.compareToIgnoreCase("SEQUEST") == 0) {
                analysisProgram = AnalysisProgramType.SEQUEST;
                return;
            }
            if (aProg.compareToIgnoreCase("MASCOT") == 0) {
                analysisProgram = AnalysisProgramType.MASCOT;
                return;
            }
            if (aProg.compareToIgnoreCase("X! Tandem") == 0) {
                analysisProgram = AnalysisProgramType.XTANDEM;
                return;
            }
            if (aProg.compareToIgnoreCase("OMSSA") == 0) {
                analysisProgram = AnalysisProgramType.OMSSA;
                return;
            }
        }
        
        if (sName == "search_database") {
            searchDB = stripPathAndExtension(attrs.getValue("local_path"));
        }
        
        if (sName == "spectrum_query") {
            inSpectrumQuery = true;
            curScan = Integer.parseInt(attrs.getValue("start_scan"));
            curQuery = Integer.parseInt(attrs.getValue("index"));
            curCharge = Integer.parseInt(attrs.getValue("assumed_charge"));
            curExpMass = Double.parseDouble(attrs.getValue("precursor_neutral_mass"));
        }
        
        if (inSpectrumQuery) {
            if (sName == "search_hit") {
                curPep = new PeptideHit();
                curPep.setSequence(attrs.getValue("peptide"));
                curPep.setCharge(curCharge);
                curPep.setExpMass(curExpMass);
                curPep.setScanNum(curScan);
                curPep.setQueryNum(curQuery);
                curPep.setSourceType(analysisProgram);
                curPep.setSourceFile(sourceFile);
                curPep.setRawFile(mzFileName);
                curPep.setDiffMass(attrs.getValue("massdiff"));
                curPro = new ProteinInfo();
                curPro.setName(attrs.getValue("protein"));
                curPro.setDescription(attrs.getValue("protein_descr"));
                addProtein(curPro);
                curProHit = new ProteinHit();
                curProHit.setName(curPro.getName());
                curPep.addProteinHit(curProHit);
                curPro = null;
                curProHit = null;
            }
            if (sName == "alternative_protein") {
                curPro = new ProteinInfo();
                curPro.setName(attrs.getValue("protein"));
                curPro.setDescription(attrs.getValue("protein_descr"));
                addProtein(curPro);
                curProHit = new ProteinHit();
                curProHit.setName(curPro.getName());
                curPep.addProteinHit(curProHit);
                curPro = null;
                curProHit = null;
            }
            if (sName == "search_score") {
                String name = attrs.getValue("name");
                String value = attrs.getValue("value");
                if (name.compareToIgnoreCase("expect") == 0) curPep.setExpect(value); 
                if (name.compareToIgnoreCase("ionscore") == 0) curPep.setIonScore(value); 
                if (name.compareToIgnoreCase("identity") == 0) curPep.setIdent(value);
                    
                
            }
        }
        
    }
    
    public void endElement(String namespaceURI, String sName, String qName) {
        if (sName == "spectrum_query") {
            inSpectrumQuery = false;
        }
        if (inSpectrumQuery && sName == "search_hit") {
            addPeptideHit(curPep);
            curPep = null;
        }
    }
    
}
