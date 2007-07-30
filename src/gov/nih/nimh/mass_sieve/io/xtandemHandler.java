/*
 * xtandemHandler.java
 *
 * Created on February 24, 2006, 2:12 PM
 *
 * @author Douglas Slotta
 */

package gov.nih.nimh.mass_sieve.io;

import gov.nih.nimh.mass_sieve.*;
import java.util.HashMap;
import org.xml.sax.*;

class xtandemHandler extends AnalysisHandler {
    String mzFileName;
    int curCharge;
    double curMass;
    int curID;
    HashMap<PeptideHit, PeptideHit> unique_peptide_hits;
    
    // Creates a new instance of xtandemHandler
    public xtandemHandler(String fn) {
        super(fn);
        analysisProgram = AnalysisProgramType.XTANDEM;
        unique_peptide_hits = new HashMap<PeptideHit, PeptideHit>();
    }
    
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        String val;
        int start, stop;
        double nMass;
        
        if (sName == "bioml") {
            mzFileName = attrs.getValue("label");
        }
        
        if (sName == "group") {
            if (attrs.getValue("type").equals("model")) {
                curCharge = Integer.parseInt(attrs.getValue("z"));
                curMass = Double.parseDouble(attrs.getValue("mh"));
                curID = Integer.parseInt(attrs.getValue("id"));
            }
        }
        
        if (sName == "protein") {
            curPro = new ProteinInfo();
            curProHit = new ProteinHit();
            val = stripDescription(attrs.getValue("label"));
            if (val.endsWith("|")) {
                val = val.substring(0,val.length()-1);
            }
            curPro.setName(val);
            curProHit.setName(val);
        }
        
        if (sName == "note" && attrs.getValue("label").equals("description")) {
            collectData = true;
        }
        if (sName == "peptide") {
            collectData = true;
        }
        
        if (sName == "domain") {
            
            curPep = new PeptideHit();
            //val = attrs.getValue("id");
            //curPep.setQueryNum(val);
            //curPep.setScanNum(stripID(val));
            curPep.setQueryNum(curID);
            curPep.setScanNum(curID);
            curPep.setSourceFile(sourceFile);
            curPep.setSourceType(analysisProgram);
            curPep.setRawFile(mzFileName);
            curPep.setCharge(curCharge);
            nMass = curMass - 1.007277;  // minus mass of hydrogen
            curPep.setExpNeutralMass(nMass);
            curPep.setExpMass((nMass + (curCharge * 1.007277)) / curCharge);
            curPep.setTheoreticalMass(Double.parseDouble(attrs.getValue("mh"))-1.007277);  // minus mass of hydrogen
            curProHit.setStart(Integer.parseInt(attrs.getValue("start")));
            curProHit.setEnd(Integer.parseInt(attrs.getValue("end")));
            curPep.setExpect(attrs.getValue("expect"));
            curPep.setSequence(attrs.getValue("seq"));
            curPep.setExperiment("");
            //curPep.setProteinName(curPro.getName());
        }
    }
    
    public void endElement(String namespaceURI, String sName, String qName) {
        if (sName == "protein") {
            addProtein(curPro);
            curPro = null;
            curProHit = null;
        }
        if (sName == "note" && collectData) {
            curPro.setDescription(data);
            collectData = false;
            data = "";
        }
        if (sName == "peptide") {
            curPro.setSequence(data.replaceAll("\\s", ""));
            collectData = false;
            data = "";
        }

        if (sName == "domain") {
            if (unique_peptide_hits.containsKey(curPep)) {
                PeptideHit p = unique_peptide_hits.get(curPep);
                p.addProteinHit(curProHit);
            } else {
                curPep.addProteinHit(curProHit);
                unique_peptide_hits.put(curPep, curPep);
            }
            curPep = null;
        }
        if (sName == "bioml") {
            //peptide_hits.addAll(unique_peptide_hits.values());
            for (PeptideHit p:unique_peptide_hits.values()) {
                addPeptideHit(p);
            }
        }
    }
    
    private String stripID(String iStr) {
        int loc = iStr.indexOf('.');
        if (loc > 0) {
            return iStr.substring(0,loc);
        }
        return iStr;
    }
    
    private String stripDescription(String iStr) {
        int loc = iStr.indexOf(' ');
        if (loc > 0) {
            return iStr.substring(0,loc);
        }
        return iStr;
    }
}
