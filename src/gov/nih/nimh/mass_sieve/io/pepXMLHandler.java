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
    double curMZ;
    
    /** Creates a new instance of pepXMLHandler */
    public pepXMLHandler(String fn) {
        super(fn);
        analysisProgram = AnalysisProgramType.PEPXML;
        inSpectrumQuery = false;
    }
    
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        
        if (sName.equals("search_summary")) {
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
            //if (aProg.compareToIgnoreCase("PepArML") == 0) {
            //    analysisProgram = AnalysisProgramType.PEPARML;
            //    return;
            //}
            analysisProgram = AnalysisProgramType.PEPXML;
        }
        
        if (sName.equals("search_database")) {
            searchDB = stripPathAndExtension(attrs.getValue("local_path"));
        }
        
        if (sName.equals("spectrum_query")) {
            inSpectrumQuery = true;
            curScan = Integer.parseInt(attrs.getValue("start_scan"));
            curQuery = Integer.parseInt(attrs.getValue("index"));
            curCharge = Integer.parseInt(attrs.getValue("assumed_charge"));
            curExpMass = Double.parseDouble(attrs.getValue("precursor_neutral_mass"));
            curMZ = (curExpMass + (curCharge * MASS_HYDROGEN)) / curCharge;
        }
        
        if (inSpectrumQuery) {
            if (sName.equals("search_hit")) {
                curPep = new PeptideHit();
                curPep.setPepXML(true);
                curPep.setSequence(attrs.getValue("peptide"));
                curPep.setCharge(curCharge);
                curPep.setExpNeutralMass(curExpMass);
                curPep.setExpMass(curMZ);
                curPep.setScanNum(curScan);
                curPep.setQueryNum(curQuery);
                curPep.setSourceType(analysisProgram);
                curPep.setSourceFile(sourceFile);
                curPep.setRawFile(mzFileName);
                curPep.setDiffMass(attrs.getValue("massdiff"));
                curPep.setTheoreticalMass(attrs.getValue("calc_neutral_pep_mass"));
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
            if (sName.equals("alternative_protein")) {
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
            if (sName.equals("search_score")) {
                String name = attrs.getValue("name");
                String value = attrs.getValue("value");
                if (name.compareToIgnoreCase("expect") == 0) curPep.setExpect(value);
                if (name.compareToIgnoreCase("estfdr") == 0) curPep.setExpect(value);
                if (name.compareToIgnoreCase("xcorr") == 0) curPep.setXcorr(value); 
                if (name.compareToIgnoreCase("ionscore") == 0) curPep.setIonScore(value); 
                if (name.compareToIgnoreCase("identityscore") == 0) curPep.setIdent(value);
            }
            if (sName.equals("peptideprophet_result")) {
                curPep.setPepProphet(attrs.getValue("probability"));
            }
        }
        
    }
    
    public void endElement(String namespaceURI, String sName, String qName) {
        if (sName.equals("spectrum_query")) {
            inSpectrumQuery = false;
        }
        if (inSpectrumQuery && sName.equals("search_hit")) {
            addPeptideHit(curPep);
            curPep = null;
        }
    }
    
}
