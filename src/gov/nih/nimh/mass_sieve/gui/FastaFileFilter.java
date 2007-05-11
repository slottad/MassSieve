/*
 * MSFileFilter.java
 *
 * Created on March 15, 2007, 2:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author slotta
 */
public class FastaFileFilter extends FileFilter {
    
    /*
     * Get the extension of a file.
     */
    private String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        
        String extension = getExtension(f);
        if (extension != null) {
             if (extension.equals("fasta") ||
                extension.equals("fas")) {
                return true;
            } else {
                return false;
            }
        }
        
        return false;
    }
    
    //The description of this filter
    public String getDescription() {
        return "Fasta Files (*.fasta, *.fas)";
    }
    
}
