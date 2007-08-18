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
 * This class is used for display (in a tree) of complete sets of peptides and proteins.
 * Because this is for display, only the names (strings) of the entities are stored.
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
     * Returns the stored set of peptide names
     * @return Set of peptide strings
     */
    public Set<String> getPeptides() {
        return peptides;
    }

    /**
     * Stroes the set of petides strings.
     * @param peptides Set of peptide strings
     */
    public void setPeptides(Set<String> peptides) {
        this.peptides = peptides;
    }

    /**
     * returns a set of protein names.
     * @return Set of Protein names
     */
    public Set<String> getProteins() {
        return proteins;
    }

    /**
     * Stores a set of protein names.
     * @param proteins Set of protein names
     */
    public void setProteins(Set<String> proteins) {
        this.proteins = proteins;
    }

    /**
     * Set the name of this protein and peptide collection
     * @param name the name to be set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * The name of this set of peptides and proteins
     * @return set name.
     */
    public String toString() {
        return name;
    }
}
