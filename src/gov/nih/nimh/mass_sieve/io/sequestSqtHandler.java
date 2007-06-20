/*
 * sequestSqtHandler.java
 *
 * Created on June 12, 2007, 9:22 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.io;

import java.awt.Component;
import java.util.HashSet;

/**
 *
 * @author slotta
 */
public class sequestSqtHandler extends AnalysisHandler {
    private Component parent;
    private HashSet<String> minProteins;
    
    /** Creates a new instance of sequestSqtHandler */
    public sequestSqtHandler(String f, Component p) {
        super(f);
        parent = p;
        minProteins = new HashSet<String>();
        analysisProgram = AnalysisProgramType.MASCOT;
    }
    
    public void sequestSqtParse() {
        
    }
        
}
