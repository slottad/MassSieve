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
        if (sName.equals("hits")) {
            inHits = true;
        }
        if (sName.equals("unassigned")) {
            inUnassigned = true;
        }
        if (sName.equals("queries")) {
            inQueries = true;
        }
        if (inHits) {
            if (sName.equals("protein")) {
                curPro = new ProteinInfo();
                curPro.setName(attrs.getValue("accession"));
                collectData = true;
            }
            if (sName.equals("peptide")) {
                curPep = new PeptideHit();
                curPep.setQueryNum(Integer.parseInt(attrs.getValue("query")));
                //curPep.setProteinName(curPro.getName());
                //curPep.setSourceFile(source_file);
                curPep.setSourceType(analysisProgram);
            }
        }
        if (inQueries) {
            if (sName.equals("query")) {
                currentQuery = attrs.getValue("number");
                collectData = true;
            }
        }
    }
    
    public void endElement(String namespaceURI, String sName, String qName) {
        if (sName.equals("hits")) {
            inHits = false;
        }
        if (sName.equals("unassigned")) {
            inUnassigned = false;
        }
        if (sName.equals("queries")) {
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
                    if (sName.equals("pep_exp_z")) curPep.setCharge(data);
                    if (sName.equals("pep_score")) curPep.setIonScore(data);
                    if (sName.equals("pep_expect")) curPep.setExpect(data);
                    if (sName.equals("pep_ident")) curPep.setIdent(data);
                    if (sName.equals("pep_seq")) curPep.setSequence(data);
                    if (sName.equals("pep_exp_mz")) curPep.setExpMass(data);
                    if (sName.equals("pep_exp_mr")) curPep.setExpNeutralMass(data);
                    if (sName.equals("pep_calc_mr")) curPep.setTheoreticalMass(data);
                    if (sName.equals("pep_delta")) curPep.setDiffMass(data);
                    
                    if (sName.equals("peptide")) {
                        hold_hits.add(curPep);
                        curPep = null;
                    }
                } else {
                    if (sName.equals("prot_desc")) curPro.setDescription(data);
                    if (sName.equals("prot_score")) prot_score = data;
                    if (sName.equals("prot_cover")) prot_cover = data;
                    if (sName.equals("protein")) {
                        //addProtein(curPro);
                        collectData = false;
                        curPro = null;
                    }
                }
                data = "";
            }
        }
        if (inQueries) {
            if (sName.equals("StringTitle")) {
                //QueryToScan.put(currentQuery, ScanFilenameToScanNumber(data, currentQuery));
            }
            data = "";
        }
    }
    
}