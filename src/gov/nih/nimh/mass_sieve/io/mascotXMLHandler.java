/*
 * mascotXMLHandler.java
 *
 * Created on February 24, 2006, 2:04 PM
 *
 * @author Douglas Slotta
 *
 * Warning: This file is obsolete and dangerous to use!
 *
 */

package gov.nih.nimh.mass_sieve.io;

import gov.nih.nimh.mass_sieve.*;
import java.util.ArrayList;
import java.util.HashMap;
import org.xml.sax.*;

public class mascotXMLHandler extends AnalysisHandler {
    
    private boolean inHits, inUnassigned, inQueries;
    private HashMap<String, Integer> QueryToScan;
    private String prot_score, prot_cover;
    private double pep_score, pep_ident;
    private String currentQuery;
    private ArrayList<PeptideHit> hold_hits;
    
    /**
     * Creates a new instance of mascotXMLHandler
     */
    public mascotXMLHandler(String fn) {
        super(fn);
        analysisProgram = AnalysisProgramType.MASCOT;
        inHits = false;
        inUnassigned = false;
        inQueries = false;
        QueryToScan = new HashMap<String, Integer>();
        hold_hits = new ArrayList<PeptideHit>();
    }
    
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        if (sName == "hits") {
            inHits = true;
        }
        if (sName == "unassigned") {
            inUnassigned = true;
        }
        if (sName == "queries") {
            inQueries = true;
        }
        if (inHits) {
            if (sName == "protein") {
                curPro = new ProteinInfo();
                curPro.setName(attrs.getValue("accession"));
                collectData = true;
            }
            if (sName == "peptide") {
                curPep = new PeptideHit();
                curPep.setQueryNum(attrs.getValue("query"));
                //curPep.setProteinName(curPro.getName());
                //curPep.setSourceFile(source_file);
                curPep.setSourceType(analysisProgram);
            }
        }
        if (inQueries) {
            if (sName == "query") {
                currentQuery = attrs.getValue("number");
                collectData = true;
            }
        }
    }
    
    public void endElement(String namespaceURI, String sName, String qName) {
        if (sName == "hits") {
            inHits = false;
        }
        if (sName == "unassigned") {
            inUnassigned = false;
        }
        if (sName == "queries") {
            inQueries = false;
            for (PeptideHit p: hold_hits) {
                p.setScanNum(QueryToScan.get(p.getQueryNum()));
                addPeptideHit(p);
            }
            collectData = false;
        }
        if (inHits) {
            if (curPro != null) {
                if (curPep != null) {
                    if (sName == "pep_exp_z") curPep.setCharge(data);
                    //if (sName == "pep_start") curPep.setStart(data);
                    //if (sName == "pep_end") curPep.setEnd(data);
                    if (sName == "pep_score") curPep.setIonScore(data);
                    if (sName == "pep_expect") curPep.setExpect(data);
                    if (sName == "pep_ident") curPep.setIdent(data);
                    if (sName == "pep_seq") curPep.setSequence(data);
                    if (sName == "pep_exp_mz") curPep.setExpMass(data);
                    if (sName == "pep_exp_mr") curPep.setExpNeutralMass(data);
                    if (sName == "pep_calc_mr") curPep.setTheoreticalMass(data);
                    if (sName == "pep_delta") curPep.setDiffMass(data);
                    
                    if (sName == "peptide") {
                        //addPeptideHit(curPep);
                        hold_hits.add(curPep);
                        curPep = null;
                    }
                } else {
                    if (sName == "prot_desc") curPro.setDescription(data);
                    //if (sName == "prot_mass") curPro.setMass(Double.parseDouble(data));
                    if (sName == "prot_score") prot_score = data;
                    if (sName == "prot_cover") prot_cover = data;
                    if (sName == "protein") {
                        //addProtein(curPro);
                        collectData = false;
                        curPro = null;
                    }
                }
                data = "";
            }
        }
        if (inQueries) {
            if (sName == "StringTitle") {
                QueryToScan.put(currentQuery,ScanFilenameToScanNumber(data));
            }
            data = "";
        }
    }
    
}