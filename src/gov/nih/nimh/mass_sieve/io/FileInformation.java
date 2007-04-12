/*
 * FileInformation.java
 *
 * Created on December 19, 2006, 9:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.io;

import java.util.HashSet;

/**
 *
 * @author slotta
 */
public class FileInformation implements Comparable<FileInformation> {
    private AnalysisProgramType analysisProgram;
    private String sourceFile;
    private String searchDB;
    private int pepHitCount;
    private HashSet<String> rawFiles;
    
    /** Creates a new instance of FileInformation */
    public FileInformation() {
    }

    public AnalysisProgramType getAnalysisProgram() {
        return analysisProgram;
    }

    public void setAnalysisProgram(AnalysisProgramType analysisProgram) {
        this.analysisProgram = analysisProgram;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getSearchDB() {
        return searchDB;
    }

    public void setSearchDB(String searchDB) {
        this.searchDB = searchDB;
    }

    public int getPepHitCount() {
        return pepHitCount;
    }

    public void setPepHitCount(int pepHitCount) {
        this.pepHitCount = pepHitCount;
    }

    public HashSet<String> getRawFiles() {
        return rawFiles;
    }

    public void setRawFiles(HashSet<String> rawFiles) {
        this.rawFiles = rawFiles;
    }
    
    public int compareTo(FileInformation fi) {
        return sourceFile.compareTo(fi.getSourceFile());
    }
}
