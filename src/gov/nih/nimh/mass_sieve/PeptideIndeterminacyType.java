/*
 * PeptideIndeterminacyType.java
 *
 * Created on November 14, 2006, 3:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve;

/**
 * This is used to denote if a Peptide is indeterminate, based upon its PeptideHits
 * @author slotta
 */
public enum PeptideIndeterminacyType { 
    /**
     * No PetideHits are indeterminate
     */
    NONE {
        public String toString() {
            return "";
        }
    },
    
    /**
     * Some PeptideHits are indeterminate
     */
    SOME {
        public String toString() {
            return "Some peptides";
        }
    },
    
    /**
     * All of the PeptideHits are indeterminate.
     */
    ALL {
        public String toString() {
            return "All peptides";
        }
    }
 };