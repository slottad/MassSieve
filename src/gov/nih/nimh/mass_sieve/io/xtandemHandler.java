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
    
    // Creates a new instance of xtandemHandler
    public xtandemHandler(String fn) {
        super(fn);
        analysisProgram = AnalysisProgramType.XTANDEM;
    }
    
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        String val;
        int start, stop;
        
        if (sName == "protein") {
            curPro = new Protein();
            val = attrs.getValue("label");
            if (val.endsWith("|")) {
                val = val.substring(0,val.length()-1);
            }
            curPro.setName(val);
        }
        if (sName == "domain") {
            
            curPep = new PeptideHit();
            curPep.setQueryNum(stripID(attrs.getValue("id")));
            //curPep.setScanNum(pepScan);
            //curPep.setSourceFile(source_file);
            curPep.setSourceType(analysisProgram);
            //curPep.setStart(attrs.getValue("start"));
            //curPep.setEnd(attrs.getValue("end"));
            curPep.setExpect(attrs.getValue("expect"));
            curPep.setSequence(attrs.getValue("seq"));
            //curPep.setProteinName(curPro.getName());
        }
    }
    
    public void endElement(String namespaceURI, String sName, String qName) {
        if (sName == "protein") {
            curPro = null;
        }
        if (sName == "domain") {
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
}
