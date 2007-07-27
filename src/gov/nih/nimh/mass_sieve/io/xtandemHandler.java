/*
 * xtandemHandler.java
 *
 * Created on February 24, 2006, 2:12 PM
 *
 * @author Douglas Slotta
 */

package gov.nih.nimh.mass_sieve.io;

import gov.nih.nimh.mass_sieve.*;
import org.xml.sax.*;

class xtandemHandler extends AnalysisHandler {
    String mzFileName;
    int curCharge;
    double curMass;
    
    // Creates a new instance of xtandemHandler
    public xtandemHandler(String fn) {
        super(fn);
        analysisProgram = AnalysisProgramType.XTANDEM;
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
        if (sName == "domain") {
            
            curPep = new PeptideHit();
            val = attrs.getValue("id");
            curPep.setQueryNum(val);
            curPep.setScanNum(stripID(val));
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
            //curPep.setProteinName(curPro.getName());
        }
    }
    
    public void endElement(String namespaceURI, String sName, String qName) {
        if (sName == "protein") {
            curPro = null;
            curProHit = null;
        }
        if (sName == "domain") {
            curPep.addProteinHit(curProHit);
            addPeptideHit(curPep);
            curPep = null;
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
