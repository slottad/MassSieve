/*
 * PeptideProteinNameSet.java
 *
 * Created on August 1, 2007, 3:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve;

import java.util.Set;

/**
 *
 * @author slotta
 */
public class PeptideProteinNameSet {
    private Set<String> peptides;
    private Set<String> proteins;
    private String name;
    
    /** Creates a new instance of PeptideProteinNameSet */
    public PeptideProteinNameSet() {
        name = "Generic Peptide Protein Set";
    }

    /**
     * 
     * @return 
     */
    public Set<String> getPeptides() {
        return peptides;
    }

    /**
     * 
     * @param peptides 
     */
    public void setPeptides(Set<String> peptides) {
        this.peptides = peptides;
    }

    /**
     * 
     * @return 
     */
    public Set<String> getProteins() {
        return proteins;
    }

    /**
     * 
     * @param proteins 
     */
    public void setProteins(Set<String> proteins) {
        this.proteins = proteins;
    }

    /**
     * 
     * @return 
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 
     * @return 
     */
    public String toString() {
        return name;
    }
}
