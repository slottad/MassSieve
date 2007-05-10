/*
 * FilterSettings.java
 *
 * Created on May 9, 2007, 1:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve;

import java.io.Serializable;

/**
 *
 * @author slotta
 */
public class FilterSettings implements Serializable {
    private double omssaCutoff, mascotCutoff, xtandemCutoff;
    private String filterText;
    private boolean useIonIdent, useIndeterminates, filterPeptides, filterProteins, filterCoverage;
    private int pHitCutoffCount, peptideCutoffCount, coverageCutoffAmount;
    /** Creates a new instance of FilterSettings */
    public FilterSettings() {
        omssaCutoff = 0.05;
        mascotCutoff = 0.05;
        xtandemCutoff = 0.05;
        useIonIdent = true;
        filterText = "o+m+x";
        useIndeterminates = true;
        filterPeptides = false;
        filterProteins = false;
        pHitCutoffCount = 1;
        peptideCutoffCount = 1;
    }
    
    public void cloneFilterSettings(FilterSettings fromFilter) {
        this.setOmssaCutoff(fromFilter.getOmssaCutoff());
        this.setMascotCutoff(fromFilter.getMascotCutoff());
        this.setXtandemCutoff(fromFilter.getXtandemCutoff());
        this.setUseIonIdent(fromFilter.getUseIonIdent());
        this.setFilterText(fromFilter.getFilterText());
        this.setUseIndeterminates(fromFilter.getUseIndeterminates());
        this.setFilterPeptides(fromFilter.getFilterPeptides());
        this.setFilterProteins(fromFilter.getFilterProteins());
        this.setPHitCutoffCount(fromFilter.getPHitCutoffCount());
        this.setPeptideCutoffCount(fromFilter.getPeptideCutoffCount());
    }
    
    public void setOmssaCutoff(String s) {
        omssaCutoff = Double.parseDouble(s);
    }
    
    public void setMascotCutoff(String s) {
        mascotCutoff = Double.parseDouble(s);
    }
    public void setXtandemCutoff(String s) {
        xtandemCutoff = Double.parseDouble(s);
    }
    
    public void setOmssaCutoff(double d) {
        omssaCutoff = d;
    }
    public void setMascotCutoff(double d) {
        mascotCutoff = d;
    }
    public void setXtandemCutoff(double d) {
        xtandemCutoff = d;
    }   
    public void setUseIonIdent(boolean b) {
        useIonIdent = b;
        if (useIonIdent) {
            mascotCutoff = 0.05;
        }
    }
    
    public void setFilterText(String s) {
        filterText = s;
    }
    public double getOmssaCutoff() {
        return omssaCutoff;
    }
    public double getMascotCutoff() {
        return mascotCutoff;
    }
    public double getXtandemCutoff() {
        return xtandemCutoff;
    }
    
    public String getFilterText() {
        return filterText;
    }
    
    public boolean getUseIonIdent() {
        return useIonIdent;
    }
    
    public void setUseIndeterminates(boolean b) {
        useIndeterminates = b;
    }
    
    public void setFilterPeptides(boolean b) {
        filterPeptides = b;
    }
    
    public void setFilterProteins(boolean b) {
        filterProteins = b;
    }
    public void setFilterCoverage(boolean b) {
        filterCoverage = b;
    }
    
    public void setPHitCutoffCount(int i) {
        pHitCutoffCount = i;
    }
    
    public void setPeptideCutoffCount(int i) {
        peptideCutoffCount = i;
    }
    public void setCoverageCutoffAmount(int i) {
        coverageCutoffAmount = i;
    }
    
    public boolean getUseIndeterminates() {
        return useIndeterminates;
    }
    
    public boolean getFilterPeptides() {
        return filterPeptides;
    }
    
    public boolean getFilterProteins() {
        return filterProteins;
    }
    public boolean getFilterCoverage() {
        return filterCoverage;
    }
    
    public int getPHitCutoffCount() {
        return pHitCutoffCount;
    }
    
    public int getPeptideCutoffCount() {
        return peptideCutoffCount;
    }
    public int getCoverageCutoffAmount() {
        return coverageCutoffAmount;
    }
}
