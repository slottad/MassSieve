/*
 * FileExperimentTuple.java
 *
 * Created on October 21, 2006, 12:49 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.gui;

import java.io.File;

/**
 *
 * @author slotta
 */
public class FileExperimentTuple {
    private File file;
    private String exp;
    
    /** Creates a new instance of FileExperimentTuple */
    
    public FileExperimentTuple(File f, String e) {
        file = f;
        exp = e;
    }
    
    public String toString() {
        //return exp + " \uF0E7 " + file.getName();
        //return exp + " <- " + file.getName();
        return exp + " \u21e6 " + file.getName();
    }
    
    public File getFile() {
        return file;
    }
    
    public String getExp() {
        return exp;
    }
}
