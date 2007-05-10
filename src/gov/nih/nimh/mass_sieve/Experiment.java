/*
 * Experiment.java
 *
 * Created on May 9, 2007, 1:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve;

import gov.nih.nimh.mass_sieve.io.FileInformation;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author slotta
 */
public class Experiment implements Serializable {
    private PeptideCollection pepCollection;
    private ArrayList<FileInformation> fileInfos;
    private FilterSettings filterSettings;
    
    /** Creates a new instance of Experiment */
    public Experiment() {
    }

    public PeptideCollection getPepCollection() {
        return pepCollection;
    }

    public void setPepCollection(PeptideCollection pepCollection) {
        this.pepCollection = pepCollection;
    }

    public ArrayList<FileInformation> getFileInfos() {
        return fileInfos;
    }

    public void setFileInfos(ArrayList<FileInformation> fileInfos) {
        this.fileInfos = fileInfos;
    }

    public FilterSettings getFilterSettings() {
        return filterSettings;
    }

    public void setFilterSettings(FilterSettings filterSettings) {
        this.filterSettings = filterSettings;
    }
    
 
    
}
